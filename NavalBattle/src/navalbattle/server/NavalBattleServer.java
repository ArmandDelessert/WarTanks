package navalbattle.server;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import navalbattle.boats.Aircraftcarrier;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.boats.BoatPosition;
import navalbattle.boats.Cruiser;
import navalbattle.boats.BoatUtils;
import navalbattle.boats.Destroyer;
import navalbattle.boats.Submarine;
import navalbattle.datamodel.DMGameHistory;
import navalbattle.protocol.beaconing.server.NavalBattleBeaconingServer;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.common.BoatQuantity;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.IRequestServerParamsForBeaconing;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.NavalBattleErrorCode;
import navalbattle.protocol.messages.common.*;
import navalbattle.storage.StorageUtils;
import org.w3c.dom.Document;

public class NavalBattleServer implements IRequestServerParamsForBeaconing, IClientListener {

    private NavalBattleBeaconingServer beaconing = null;
    private NavalBattleServerEndpoint ipEndpoint = null;
    private CurrentGame currentGame;
    
    public GamePlaying getGamePlaying() {
        return this.currentGame.getCurrentGame();
    }

    public NavalBattleServer(GameParameters gameParameters) {
        this.currentGame = new CurrentGame(gameParameters);

        // This starts the SocketServer and spawns a NavalBattleClientHandler for every new client
        try {
            beaconing = new NavalBattleBeaconingServer(this);
            beaconing.startBroadcasting();

            ipEndpoint = new NavalBattleServerEndpoint(this);
            ipEndpoint.startAcceptingClients();
        } catch (IOException ex) {
        }
    }

    // The beaconing entity will call this method in order to know the server parameters
    @Override
    public BeaconingParameters getServerParamsForBeaconing() {
        return new BeaconingParameters(this.currentGame);
    }

    @Override
    public void message_AttackCoordinateReceived(NavalBattleClientHandler client, NavalBattleAttackCoordinate message) {

        // User is firing against the opponent
        
        // Must be authenticated
        if (client.isAuthenticated()) {
            

            if (this.currentGame.isUserAPlayer(client)) {
                ArrayList<CoordinateWithType> coordinatesHit = null; 
                NavalBattleAttackCoordinateResponse response = new NavalBattleAttackCoordinateResponse();
                NavalBattleErrorCode error = null;

                try {
                    coordinatesHit = this.currentGame.userIsFiring(client, message.getX(), message.getY());
                } catch (FireOutsideGridException ex) {
                    error = new NavalBattleErrorCode(2, "Your coordinates were invalid (off grid or empty)");
                } catch (GameIsNotInThePlayingStateException ex) {
                    error = new NavalBattleErrorCode(4, "Game ended or has not yet started");
                } catch (ThisPlayerIsNotTheOneWhoMustPlay ex) {
                    error = new NavalBattleErrorCode(3, "It is not your turn to play");
                } catch (AlreadyFiredOnThisPositionException ex) {
                    error = new NavalBattleErrorCode(1, "You already fired on this position (or a mine did)");
                } catch (CannotFindOpponentGridException | ThisPlayerIsnotAnOpponentException ex) {
                    error = new NavalBattleErrorCode(5, "Generic error");
                }
                
                response.setValues(((error != null) ? NavalBattleAttackCoordinateResponse.RESULT.ERROR : NavalBattleAttackCoordinateResponse.RESULT.SUCCESS), coordinatesHit, error);
                
                // We notify the one who fired
                client.sendMessage(response);
                
                if (error == null)
                {
                    String usernamePlayerWhoFired = null;
                    int playerWhoFiredNumber = 0;

                    try {
                        usernamePlayerWhoFired = client.getUsername();
                    } catch (UsernameNotYetReceivedException ex) {
                        // Will never happen (user is authenticated)
                    }
                    try {
                        playerWhoFiredNumber = this.currentGame.getPlayerNumber(client);
                    } catch (ThisPlayerIsnotAnOpponentException ex) {
                        // Will never happen (user is authenticated)
                    }

                    // Getting the other player client (in order to send his damages)
                    // MULTIPLAYER
                    int playerWhoSuffersDamagesNumber = (playerWhoFiredNumber == 1 ? 0 : 1);

                    NavalBattleClientHandler clientPlayerSuffersDamages = null;

                    try {
                        clientPlayerSuffersDamages = this.currentGame.getPlayerByNumber(playerWhoSuffersDamagesNumber);
                    } catch (ThisPlayerNumberDoesNotExistException ex) {
                        // Will never happen
                        // MULTIPLAYER
                    }

                     // We notify tho other player (who will suffer damages)
                    NavalBattleAttackedCoordinate notifyDamagesToOwnFleet = new NavalBattleAttackedCoordinate();
                    notifyDamagesToOwnFleet.setValues(usernamePlayerWhoFired, coordinatesHit);
                    clientPlayerSuffersDamages.sendMessage(notifyDamagesToOwnFleet);
                    
                    
                    // Are all boats sinked ?
                    
                    WinnerAndLoser gameEndedOrNot = this.currentGame.getWinner();
                    
                    if (gameEndedOrNot != null)
                    {
                        this.currentGame.setGameEnded();
                        
                        // Game ended
                        
                        NavalBattleEndOfGame endOfGameMessage = new NavalBattleEndOfGame();
                        
                        try {
                            endOfGameMessage.setValues(NavalBattleProtocol.REAON_END_OF_GAME.THERE_IS_WINNER, gameEndedOrNot.getWinner().getUsername());
                        } catch (UsernameNotYetReceivedException ex) {
                        }
                        
                        this.currentGame.sendMessageToAllPlayers(endOfGameMessage);
                        
                        
                        // Storing game result
                        try {
                            DMGameHistory gameHistory = new DMGameHistory();
                            gameHistory.setValues(new Date(), gameEndedOrNot.getWinner().getUsername(), gameEndedOrNot.getLoser().getUsername());
                            StorageUtils.addGameHistory(gameHistory);
                        } catch (UsernameNotYetReceivedException ex) {
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void message_ChatReceiveReceived(NavalBattleClientHandler client, NavalBattleChatSend message) {
        if (client.isAuthenticated()) {

            // Confirming the reception of the message
            {
                NavalBattleChatSendResponse confirmReception = new NavalBattleChatSendResponse();
                confirmReception.setValues(message.getNonce());

                client.sendMessage(confirmReception);
            }

            // Sending the message to everyone (that's right : including the sender)
            {
                String usernameSender = null;

                try {
                    usernameSender = client.getUsername();
                } catch (UsernameNotYetReceivedException ex) {
                    // Will never happen (user is authenticated and has thus a username)
                }

                if (usernameSender != null)
                {
                    NavalBattleChatReceive sendToEveryone = new NavalBattleChatReceive();
                    sendToEveryone.setValues(usernameSender, message.getText());

                    this.currentGame.sendMessageToAllPlayers(sendToEveryone);
                }
            }
        }
    }

    @Override
    public void message_ConnectReceived(NavalBattleClientHandler client, NavalBattleConnect message) {

        NavalBattleConnectResponse.RESULT connectResult;
        NavalBattleErrorCode error = null;

        if (currentGame.getParameters().isPasswordProtected()) {

            if (message.getPassword() != null
                    && message.getPassword().equals(currentGame.getParameters().getPassword())) {
                connectResult = NavalBattleConnectResponse.RESULT.AUTHENTICATED;
                client.setIsAuthenticated(true);
            } else {
                connectResult = NavalBattleConnectResponse.RESULT.AUTHENTICATION_ERROR;
                error = new NavalBattleErrorCode(2, "The provided shared secret is incorrect");
                client.setIsAuthenticated(false);
            }
        } else {
            if (message.getPassword() != null) {
                connectResult = NavalBattleConnectResponse.RESULT.CONNECTION_ERROR;
                error = new NavalBattleErrorCode(5, "Authentication is not required but you authenticated anyway");
                client.setIsAuthenticated(false);
            } else {
                connectResult = NavalBattleConnectResponse.RESULT.CONNECTED;
                client.setIsAuthenticated(true);
            }
        }

        if (currentGame.isServerFull()) {
            connectResult = NavalBattleConnectResponse.RESULT.CONNECTION_ERROR;
            error = new NavalBattleErrorCode(3, "Server is full");
        } else {
            try {
                if (currentGame.isUsernameAlreadyConnected(client.getUsername())) {
                    connectResult = NavalBattleConnectResponse.RESULT.CONNECTION_ERROR;
                    error = new NavalBattleErrorCode(1, "The provided username is already connected to the server");
                }
            } catch (UsernameNotYetReceivedException ex) {

                // This will never happen because the handler populated the username field
            }
        }

        NavalBattleConnectResponse response = new NavalBattleConnectResponse();
        response.setValues(connectResult, error);
        client.sendMessage(response);
        
        if (error == null && client.isAuthenticated()) {

            try {
                try {
                    Thread.sleep(1000); // do not remove
                } catch (InterruptedException ex) {
                }
                
                this.currentGame.addOpponent(client);
            } catch (GameIsFullException ex) {
                connectResult = NavalBattleConnectResponse.RESULT.CONNECTION_ERROR;
                error = new NavalBattleErrorCode(3, "Server is full");
            } catch (ThisUsernameIsAlreadyConnectedException ex) {
                connectResult = NavalBattleConnectResponse.RESULT.CONNECTION_ERROR;
                error = new NavalBattleErrorCode(1, "The provided username is already connected to the server");
            } catch (UsernameNotYetReceivedException ex) {
                // will never happen
            }

        }
    }

    @Override
    public void message_NopReceived(NavalBattleClientHandler client, NavalBattleNop message) {
        // Nothing to do : the last message received is updated by the ClientHandler
    }

    @Override
    public void message_PositionMyBoatsReceived(NavalBattleClientHandler client, NavalBattlePositionMyBoats message) {
        if (client.isAuthenticated()) {
            // Is the user a player ?
            if (this.currentGame.isUserAPlayer(client)) {
                NavalBattlePositionMyBoatsResponse.RESULT result = NavalBattlePositionMyBoatsResponse.RESULT.ERROR;
                NavalBattleErrorCode errorCode = null;

                // Are his/her boats already positioned ? This is an error
                // DANGER
                // Concurreny issue, check must be performed again to counter concurrencies issues
                // this is just a fast test to avoid obvious re-attempts
                if (!this.currentGame.hasUserPositionedBoats(client)) {

                    ArrayList<BoatPosition> allPositions = message.getBoats();
                    GameParameters currentGameParams = this.currentGame.getParameters();
                    int mapSize = currentGameParams.getMapSize().getSize();

                    // Mapping boat sizes to real ships
                    HashMap<Class, ArrayList<BoatPosition>> boatClasses = new HashMap<>();
                    boatClasses.put(Destroyer.class, new ArrayList<BoatPosition>());
                    boatClasses.put(Submarine.class, new ArrayList<BoatPosition>());
                    boatClasses.put(Cruiser.class, new ArrayList<BoatPosition>());
                    boatClasses.put(Aircraftcarrier.class, new ArrayList<BoatPosition>());
                    

                    /*
                    =======================
                    REFLECTION IS USED HERE
                    =======================
                    */
                    for (BoatPosition bp : allPositions) {
                        for (Map.Entry<Class, ArrayList<BoatPosition>> boatClass : boatClasses.entrySet()) {
                            try {
                                if (boatClass.getKey().getField("LENGTH").getInt(null) == bp.getLength()) {
                                    boatClass.getValue().add(new BoatPosition(bp.getX(), bp.getY(), bp.getOrientation(), bp.getLength()));
                                }
                            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                            }
                        }

                        if (bp.getX() >= mapSize || bp.getY() >= mapSize) {
                            errorCode = new NavalBattleErrorCode(1, "Your coordinates were invalid (off grid or empty)");
                            break;
                        }
                    }

                    if (errorCode == null) {
                        int numberOfDestroyers = boatClasses.get(Destroyer.class).size();
                        int numberOfSubmarines = boatClasses.get(Submarine.class).size();
                        int numberOfCruisers = boatClasses.get(Cruiser.class).size();
                        int numberOfAircraftCarriers = boatClasses.get(Aircraftcarrier.class).size();

                        BoatQuantity suppliedQuantities = new BoatQuantity(numberOfAircraftCarriers, numberOfCruisers, numberOfDestroyers, numberOfSubmarines);
                        BoatQuantity quantitiesMustBe = currentGameParams.getMapSize().getQuantityOfBoats();

                        boolean correctNumberOfShipsSupplied = (suppliedQuantities.equals(quantitiesMustBe));
                        final int arbitraryCellSize = 20;
                        
                        if (correctNumberOfShipsSupplied) {

                            boolean areBoatsOverlapping = false;

                            try {
                                BoatUtils.areBoatsOverlapping(new Dimension(mapSize, mapSize), allPositions);
                            } catch (BoatIsOutsideGridException ex) {
                                areBoatsOverlapping = true;
                                errorCode = new NavalBattleErrorCode(2, "Your boats are overlapping");
                            }

                            if (!areBoatsOverlapping) {
                                
                                ArrayList<Boat> allBoats = new ArrayList();
                        
                                try {

                                        
                                    {
                                        ArrayList<BoatPosition> positions = boatClasses.get(Destroyer.class);
                                        for (int i = 0 ; i < numberOfDestroyers ; ++i)
                                        {
                                            BoatPosition pos = positions.get(i);
                                            Boat newBoat = new Destroyer(arbitraryCellSize);
                                            newBoat.setPosition(pos.getX(), pos.getY(), pos.getOrientation());
                                            allBoats.add(newBoat);
                                        }
                                    }
                                    
                                     {
                                        ArrayList<BoatPosition> positions = boatClasses.get(Submarine.class);
                                        for (int i = 0 ; i < numberOfSubmarines ; ++i)
                                        {
                                            BoatPosition pos = positions.get(i);
                                            Boat newBoat = new Submarine(arbitraryCellSize);
                                            newBoat.setPosition(pos.getX(), pos.getY(), pos.getOrientation());
                                            allBoats.add(newBoat);
                                        }
                                    }
                                     
                                      {
                                        ArrayList<BoatPosition> positions = boatClasses.get(Cruiser.class);
                                        for (int i = 0 ; i < numberOfCruisers ; ++i)
                                        {
                                            BoatPosition pos = positions.get(i);
                                            Boat newBoat = new Cruiser(arbitraryCellSize);
                                            newBoat.setPosition(pos.getX(), pos.getY(), pos.getOrientation());
                                            allBoats.add(newBoat);
                                        }
                                    }
                                      
                                       {
                                        ArrayList<BoatPosition> positions = boatClasses.get(Aircraftcarrier.class);
                                        for (int i = 0 ; i < numberOfAircraftCarriers ; ++i)
                                        {
                                            BoatPosition pos = positions.get(i);
                                            Boat newBoat = new Aircraftcarrier(arbitraryCellSize);
                                            newBoat.setPosition(pos.getX(), pos.getY(), pos.getOrientation());
                                            allBoats.add(newBoat);
                                        }
                                    }

                                   

                                } catch (IOException ex) {
                                }
                                
                                
                                try {
                                    this.currentGame.setPlayerGrid(client, allBoats);
                                    
                                    result = NavalBattlePositionMyBoatsResponse.RESULT.SUCCESS;
                                } catch (ThisPlayerAlreadyHasAGameGridException ex) {
                                    // Concurrency error
                                    // Check performed before might not have worked
                                    errorCode = new NavalBattleErrorCode(3, "Your boats are already positioned");
                                } catch (InvalidGameStateException ex) {
                                    errorCode = new NavalBattleErrorCode(5, "Server is not waiting for your boats");
                                } catch (UsernameNotYetReceivedException | ThisPlayerNumberDoesNotExistException ex) {
                                    errorCode = new NavalBattleErrorCode(6, "Authentication required");
                                } catch (BoatsAreOverlapping ex) {
                                    errorCode = new NavalBattleErrorCode(2, "Your boats are overlapping");
                                } catch (BoatIsOutsideGridException ex) {
                                    errorCode = new NavalBattleErrorCode(1, "Your coordinates were invalid (off grid or empty)");
                                }

                            }
                        } else {
                            errorCode = new NavalBattleErrorCode(4, "Not the correct number of boats for the current map size");
                        }
                    }

                } else {
                    errorCode = new NavalBattleErrorCode(3, "Your boats are already positioned");
                }

                NavalBattlePositionMyBoatsResponse response = new NavalBattlePositionMyBoatsResponse();
                response.setValues(result, errorCode);
            }
        }
    }
    
    private void userDisconnected(NavalBattleClientHandler client) {
        
        if (client == null)
            return;
        
        currentGame.userDisconnected(client);
        client.disconnectClient();
    }
    
    @Override
    public void message_DisconnectReceived(NavalBattleClientHandler client, NavalBattleDisconnect message) {
        this.userDisconnected(client);
    }

    @Override
    public void message_UserDisconnected(NavalBattleClientHandler client) {
        this.userDisconnected(client);
    }

    @Override
    public void message_CannotParseMessage(NavalBattleClientHandler client, String message) {
        // Do nothing
    }

    @Override
    public void message_UnknownMessageReceived(NavalBattleClientHandler client, Document message) {
    }

    @Override
    public void message_GetGameParametersReceived(NavalBattleClientHandler client, NavalBattleGameParameters message) {

        // Anyone can ask for this info, even if they are not authenticated
        BeaconingParameters beaconParams = this.getServerParamsForBeaconing();
        NavalBattleGameParametersResponse response = new NavalBattleGameParametersResponse();
        response.setValues(beaconParams);

        client.sendMessage(response);
    }

    public void stopGameServer() {
        if (beaconing != null)
            beaconing.stopBroadcasting();
        
        if (ipEndpoint != null)
            ipEndpoint.stopAcceptingClients();
        
        if (currentGame != null)
            currentGame.stopCurrentGame();
    }

    

}
