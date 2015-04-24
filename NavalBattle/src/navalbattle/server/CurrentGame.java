package navalbattle.server;

import java.awt.Dimension;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.NavalBattleDisconnect;
import navalbattle.protocol.messages.common.NavalBattleEndOfGame;
import navalbattle.protocol.messages.common.NavalBattleGameReadyToStart;
import navalbattle.protocol.messages.common.NavalBattleGameStart;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class CurrentGame {

    public enum GAME_STATE {

        NOT_STARTED, WAITING_FOR_BOAT_POSITIONING, PLAYING, ENDED
    }

    private final Random rng = new Random();
    private final ArrayList<NavalBattleClientHandler> players = new ArrayList<>();
    private final HashMap<NavalBattleClientHandler, PlayerGrid> grids = new HashMap<>();
    private final GameParameters parameters;
    private GAME_STATE state = GAME_STATE.NOT_STARTED;

    private int playerNumberToPlay;

    public GamePlaying getCurrentGame() {
        return new GamePlaying(parameters, players);
    }

    // Returns the winner
    // null if no winner yet
    public WinnerAndLoser getWinner() {

        NavalBattleClientHandler winner = null;
        NavalBattleClientHandler loser = null;

        for (Map.Entry<NavalBattleClientHandler, PlayerGrid> grid : this.grids.entrySet()) {
            if (grid.getValue().isAllFleetSinked()) {
                loser = grid.getKey();
            } else {
                winner = grid.getKey();
            }
        }

        return (loser != null && winner != null) ? new WinnerAndLoser(winner, loser) : null;
    }
    
    public void stopCurrentGame() {
        this.disconnectAllUsers();
        this.setGameEnded();
    }
    
    private void disconnectAllUsers()
    {
        NavalBattleDisconnect disconnectMEssage = new NavalBattleDisconnect();
                
        synchronized (this.players)
        {
            // Disconnect everyone
            for (NavalBattleClientHandler c : this.players) {
                c.sendMessage(disconnectMEssage);
                c.disconnectClient();
            }

            this.players.clear();
        }
        
        synchronized (this.grids)
        {
            this.grids.clear();
        }
        
        this.state = GAME_STATE.NOT_STARTED;
    }
    
    public void setGameEnded()
    {
        this.state = GAME_STATE.ENDED;
    }

    public boolean isUserAPlayer(NavalBattleClientHandler user) {
        synchronized (this.players) {
            for (NavalBattleClientHandler client : this.players) {
                if (client == user) {
                    return true;
                }
            }
        }

        return false;
    }

    public void removeUser(NavalBattleClientHandler user) {
        synchronized (this.players) {
            this.players.remove(user);
        }

        synchronized (this.grids) {
            this.grids.remove(user);
        }
    }

    public void userDisconnected(NavalBattleClientHandler client) {
        if (!this.isUserAPlayer(client)) {
            return;
        }

        this.removeUser(client);
        
        switch (this.state) {
            case NOT_STARTED:
                // We do nothing
                break;

            case WAITING_FOR_BOAT_POSITIONING:
            case PLAYING:
                // We must inform other players that the game cannot continue

                NavalBattleEndOfGame endOfGameMessage = new NavalBattleEndOfGame();
                endOfGameMessage.setValues(NavalBattleProtocol.REAON_END_OF_GAME.CANNOT_CONTINUE_GAME, null);
                this.sendMessageToAllPlayers(endOfGameMessage);

                synchronized (this.players)
                {
                    if (this.players.size() < 2)
                    {
                        this.disconnectAllUsers();
                        this.setGameEnded();
                    }
                }

                break;

            case ENDED:
                
                this.disconnectAllUsers();
                
                break;
        }
    }

    public ArrayList<CoordinateWithType> userIsFiring(NavalBattleClientHandler client, int x, int y) throws FireOutsideGridException, GameIsNotInThePlayingStateException, ThisPlayerIsnotAnOpponentException, ThisPlayerIsNotTheOneWhoMustPlay, CannotFindOpponentGridException, AlreadyFiredOnThisPositionException {

        if (this.state != GAME_STATE.PLAYING) {
            throw new GameIsNotInThePlayingStateException();
        }

        int mapSize = parameters.getMapSize().getSize();

        if (x < 0 || x >= mapSize || y < 0 || y >= mapSize) {
            throw new FireOutsideGridException();
        }

        int playerNumber = this.getPlayerNumber(client);

        if (playerNumber != playerNumberToPlay) {
            throw new ThisPlayerIsNotTheOneWhoMustPlay();
        }

        // Getting the opponent's grid
        // CHANGE-THIS-MORE-PLAYERS
        PlayerGrid opponentGrid = null;

        for (Map.Entry<NavalBattleClientHandler, PlayerGrid> e : this.grids.entrySet()) {
            if (e.getKey() != client) {
                opponentGrid = e.getValue();
                break;
            }
        }

        if (opponentGrid == null) {
            throw new CannotFindOpponentGridException();
        }

        ArrayList<CoordinateWithType> attackResult = opponentGrid.fire(x, y, opponentGrid, false, false);

        // CHANGE-THIS-MORE-PLAYERS
        if (playerNumberToPlay == 0) {
            playerNumberToPlay = 1;
        } else {
            playerNumberToPlay = 0;
        }

        return attackResult;
    }

    public void setPlayerGrid(NavalBattleClientHandler client, ArrayList<Boat> allBoats) throws ThisPlayerAlreadyHasAGameGridException, InvalidGameStateException, UsernameNotYetReceivedException, ThisPlayerNumberDoesNotExistException, BoatsAreOverlapping, BoatIsOutsideGridException {
        if (this.hasUserPositionedBoats(client)) {
            throw new ThisPlayerAlreadyHasAGameGridException();
        }

        if (this.state != GAME_STATE.WAITING_FOR_BOAT_POSITIONING) {
            throw new InvalidGameStateException();
        }

        PlayerGrid newGridForThisNewPlayer = new PlayerGrid(parameters.getMapSize(), parameters.isBonusMinesEnabled(), parameters.isBonusSatelliteEnabled());

        newGridForThisNewPlayer.positionBoats(allBoats);

        try {
            newGridForThisNewPlayer.addBonusses();
        } catch (BoatsNotPlacedException ex) {
            // will never happen
        }

        this.grids.put(client, newGridForThisNewPlayer);

        if (this.grids.size() == this.players.size()) {
            try {
                this.startGame();
                this.state = GAME_STATE.PLAYING;
            } catch (NotEnoughPlayersToBeginGameException ex) {
            }
        }
    }

    public boolean hasUserPositionedBoats(NavalBattleClientHandler user) {
        synchronized (this.grids) {
            return this.grids.containsKey(user);
        }
    }

    public CurrentGame(GameParameters parameters) {
        this.parameters = parameters;
    }

    public GameParameters getParameters() {
        return this.parameters;
    }

    boolean isReadyToStartGame() {
        return this.getPlayersCount() == 2;
    }

    private void startGame() throws NotEnoughPlayersToBeginGameException, UsernameNotYetReceivedException, ThisPlayerNumberDoesNotExistException {
        if (!this.isServerFull()) {
            throw new NotEnoughPlayersToBeginGameException();
        }

        synchronized (this.players) {
            // Randomly choose who will start to play
            this.playerNumberToPlay = this.rng.nextInt(this.players.size());
        }

        this.sendStartGameMessageToPlayers();
        this.state = GAME_STATE.PLAYING;
    }

    public boolean isServerFull() {
        synchronized (this.players) {
            return (this.players.size() >= 2);
        }
    }

    private void sendStartGameMessageToPlayers() throws UsernameNotYetReceivedException, ThisPlayerNumberDoesNotExistException {
        NavalBattleGameStart startMessage = null;
        startMessage = new NavalBattleGameStart();

        startMessage.setValues(this.getPlayerByNumber(this.playerNumberToPlay).getUsername());

        this.sendMessageToAllPlayers(startMessage);
    }

    public void sendMessageToAllPlayers(NavalBattleMessage message) {
        synchronized (this.players) {
            for (NavalBattleClientHandler player : this.players) {
                player.sendMessage(message);
            }
        }
    }

    public boolean isUsernameAlreadyConnected(String username) throws UsernameNotYetReceivedException {
        synchronized (this.players) {
            Iterator<NavalBattleClientHandler> it = this.players.iterator();

            while (it.hasNext()) {
                if (it.next().getUsername().equals(username)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addOpponent(NavalBattleClientHandler player) throws GameIsFullException, ThisUsernameIsAlreadyConnectedException, UsernameNotYetReceivedException {
        synchronized (this.players) {
            if (this.isServerFull()) {
                throw new GameIsFullException();
            }
        }

        // Usernames must be unique within the realm of the game
        // Two players cannot have the same username
        if (this.isUsernameAlreadyConnected(player.getUsername())) {
            throw new ThisUsernameIsAlreadyConnectedException();
        }

        this.players.add(player);

        if (this.isReadyToStartGame()) {
            this.sendReadyToStartToAllPlayers();
            this.state = GAME_STATE.WAITING_FOR_BOAT_POSITIONING;
        }
    }

    private void sendReadyToStartToAllPlayers() {
        NavalBattleGameReadyToStart readyToStartMessage = new NavalBattleGameReadyToStart();
        this.sendMessageToAllPlayers(readyToStartMessage);
    }

    public NavalBattleClientHandler getPlayerByNumber(int number) throws ThisPlayerNumberDoesNotExistException {
        if (number < 0) {
            throw new InvalidParameterException();
        }

        synchronized (this.players) {
            try {
                return this.players.get(number);
            } catch (Exception ex) {
            }
        }

        throw new ThisPlayerNumberDoesNotExistException();
    }

    public int getPlayerNumber(NavalBattleClientHandler player) throws ThisPlayerIsnotAnOpponentException {
        synchronized (this.players) {
            Iterator<NavalBattleClientHandler> it = this.players.iterator();

            for (int i = 0; it.hasNext(); ++i) {
                if (it.next() == player) {
                    return i;
                }
            }
        }

        throw new ThisPlayerIsnotAnOpponentException();
    }

    public int getPlayersCount() {
        synchronized (this.players) {
            return this.players.size();
        }
    }

}
