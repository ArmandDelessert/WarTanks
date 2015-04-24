package navalbattle.client.models;

import navalbattle.client.viewmodelmessages.GameStateChangedEvent;
import java.util.ArrayList;
import java.util.UUID;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.boats.BoatPosition;
import navalbattle.client.BoatNotPlacedException;
import navalbattle.client.ClientGrid;
import navalbattle.client.InconsistentGameStateBetweenServerAndClientException;
import navalbattle.client.NavalBattleClient;
import navalbattle.controllers.MainController;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.NavalBattleAttackCoordinate;
import navalbattle.protocol.messages.common.NavalBattleAttackCoordinateResponse;
import navalbattle.protocol.messages.common.NavalBattleAttackedCoordinate;
import navalbattle.protocol.messages.common.NavalBattleChatReceive;
import navalbattle.protocol.messages.common.NavalBattleChatSend;
import navalbattle.protocol.messages.common.NavalBattleChatSendResponse;
import navalbattle.protocol.messages.common.NavalBattleDisconnect;
import navalbattle.protocol.messages.common.NavalBattleEndOfGame;
import navalbattle.protocol.messages.common.NavalBattleGameStart;
import navalbattle.protocol.messages.common.NavalBattleMessage;
import navalbattle.protocol.messages.common.NavalBattlePositionMyBoats;
import navalbattle.server.BoatsAreOverlapping;

public class ModelGameMain extends Model {

    public static enum WHO { MYSELF, MY_OPPONENT }
    
    private final NavalBattleClient client;
    private final DiscoveredServer connectedOn;

    private final ClientGrid myGrid;
    private final ClientGrid opponentsGrid;
    
    private final String myUsername;
    private final String UI_NAME = "ui_gameMain";
    private final LanguageHelper lh;
    
    // MULTIPLAYER
    boolean isItMyTurnToPlay = false;

    public ClientGrid getMyGrid() {
        return myGrid;
    }

    public ClientGrid getOpponentsGrid() {
        return opponentsGrid;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();

        if (this.client != null) {
            this.client.unsubscribe(this);
        }
        
        this.client.disconnect();
    }

    public ModelGameMain(MainController controller, NavalBattleClient client, DiscoveredServer connectedOn, String myUsername) {
        super(controller);
        this.lh = this.getController().getLanguageHelper();
        this.client = client;
        this.connectedOn = connectedOn;
        this.myUsername = myUsername;

        MapSizeEnum size = this.connectedOn.getMapSize();
        this.myGrid = new ClientGrid(size);
        this.opponentsGrid = new ClientGrid(size);

        if (this.client != null)
            this.client.subscribe(this);
    }

    // MULTIPLAYER
    public boolean isIsItMyTurnToPlay() {
        return isItMyTurnToPlay;
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
        if (type == NOTIFICATION_TYPE.MESSAGE) {
            Class classMessage = message.getClass();

            if (classMessage == NavalBattleGameStart.class) {
                
                NavalBattleGameStart messageCasted = (NavalBattleGameStart) message;
                
                // MULTIPLAYER
                this.isItMyTurnToPlay = messageCasted.getUsernameFirstToPlay().equals(this.getController().getMyUsername());
                
                this.setChanged();
                this.notifyObservers(messageCasted);
            }
            else if (classMessage == NavalBattleChatReceive.class) {
                // Someone spoke
                
                NavalBattleChatReceive messageCasted = (NavalBattleChatReceive) message;
                
                this.setChanged();
                this.notifyObservers(messageCasted);
            }
            else if (classMessage == NavalBattleChatSendResponse.class) {
                // ACK of our chat message
                
                NavalBattleChatSendResponse messageCasted = (NavalBattleChatSendResponse) message;
                
                this.setChanged();
                this.notifyObservers(messageCasted);
            }
            else if (classMessage == NavalBattleAttackCoordinateResponse.class) {
                // We receive the damages we made to our opponent

                NavalBattleAttackCoordinateResponse messageCasted = (NavalBattleAttackCoordinateResponse) message;

                if (messageCasted.getResult() == NavalBattleAttackCoordinateResponse.RESULT.SUCCESS) {
                    this.opponentsGrid.receivedFireFromOpponentSimpleDesign(messageCasted.getAllCoordinatesHit());
                    
                    this.setChanged();
                    this.notifyObservers(messageCasted);
                    
                    this.gameStateChanged(ModelGameMain.WHO.MY_OPPONENT);
                } else {
                    // TO-DO: display better errors
                    
                    this.notifyView(NotifyMessage.Type.DISPLAY_ERROR, messageCasted.getError().getErrorDescription());
                }
                
            } else if (classMessage == NavalBattleAttackedCoordinate.class) {
                NavalBattleAttackedCoordinate messageCasted = (NavalBattleAttackedCoordinate) message;

                try {
                    this.myGrid.receivedFireFromOpponent(messageCasted.getAllCoordinatesHit());
                    
                    this.setChanged();
                    this.notifyObservers(messageCasted);
                    
                    this.gameStateChanged(ModelGameMain.WHO.MYSELF);
                    
                    // MULTIPLAYER
                    this.isItMyTurnToPlay = true;
                    
                } catch (InconsistentGameStateBetweenServerAndClientException ex) {
                    this.notifyView(NotifyMessage.Type.DISPLAY_ERROR, this.lh.getTextDef(UI_NAME, "INCONSISTENT_GAME_STATE_BETWEEN_CLIENT_AND_SERVER"));
                } catch (BoatNotPlacedException ex) {
                    // Will never happen
                }
            }
            else if (classMessage == NavalBattleEndOfGame.class) {
                NavalBattleEndOfGame messageCasted = (NavalBattleEndOfGame)message;
                
                this.setChanged();
                this.notifyObservers(messageCasted);
            }
            else if (classMessage == NavalBattleDisconnect.class)
            {
                this.endOfTransactionWithServer();
            }
        }
        else if (type == NOTIFICATION_TYPE.BEEN_DISCONNECTED)
        {
            this.notifyView(NotifyMessage.Type.DISPLAY_ERROR, this.lh.getTextDef(UI_NAME, "DISCONNECTED_FROM_SERVER"));
        }
    }
    
    private void gameStateChanged(ModelGameMain.WHO who)
    {
        this.setChanged();
        this.notifyObservers(new GameStateChangedEvent(who));
    }
    
    private void notifyView(NotifyMessage.Type type, Object arg)
    {
        this.setChanged();
        this.notifyObservers(new NotifyMessage(type, arg));
    }
    
    public NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawing_MyGrid() {
        return this.myGrid.getGridForDrawing();
    }

    public NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawing_OpponentGrid() {
        return this.opponentsGrid.getGridForDrawing();
    }

    public void fireOnPosition(int x, int y) {

        // MULTIPLAYER
        if (!this.isItMyTurnToPlay)
            return;
        
        // MULTIPLAYER
        this.isItMyTurnToPlay = false;
        
        NavalBattleAttackCoordinate coordinateToAttackMessage = new NavalBattleAttackCoordinate();
        coordinateToAttackMessage.setValues(x, y);

        this.client.sendMessage(coordinateToAttackMessage);
    }
    
    public void positionMyBoats(ArrayList<Boat> boats) throws BoatsAreOverlapping, BoatIsOutsideGridException
    {
        this.myGrid.positionBoats(boats);
        
        ArrayList<BoatPosition> positions = new ArrayList();
        
        for (Boat b : boats)
            positions.add(b.getPosition());
        
        NavalBattlePositionMyBoats positionMyBoatsMessage = new NavalBattlePositionMyBoats();
        positionMyBoatsMessage.setValues(positions);
        
        this.client.sendMessage(positionMyBoatsMessage);
    }
    
    private void sendCloseWindowEvent() {
        this.notifyView(NotifyMessage.Type.CLOSE_WINDOW, null);
    }

    public void userWantsToSpeak(String text) {
        boolean isSpecialCommand = false;
        // special commands
        
        if (text.equals("/quit"))
        {
           isSpecialCommand = true;
           this.disconnectFromServer();
           this.client.unsubscribe(this);
           this.sendCloseWindowEvent();
        }
        
        if (!isSpecialCommand)
        {
            // TO-DO: keep list of messages to detect the ones who did not receive a confirmation
            NavalBattleChatSend chatSend = new NavalBattleChatSend();
            chatSend.setValues(UUID.randomUUID().toString(), text);
            this.client.sendMessage(chatSend);
        }
    }
    
    private void disconnectFromServer()
    {
        NavalBattleDisconnect disconnect = new NavalBattleDisconnect();
        this.client.sendMessage(disconnect);
        
        this.client.disconnect();
    }

    public String getMyUsername() {
        return this.myUsername;
    }

    public void windowClosed() {
        
        this.getController().disconnectFromServer();
        
        try {
            this.finalize();
        } catch (Throwable ex) {
        }
    }

    private void endOfTransactionWithServer() {
        this.notifyView(NotifyMessage.Type.DISPLAY_INFO, this.lh.getTextDef(UI_NAME, "ALL_PARTICIPANTS_ARE_NOW_GONE"));
    }
}
