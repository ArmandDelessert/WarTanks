package navalbattle.server;

import java.util.ArrayList;

public class GamePlaying {

    private final GameParameters parameters;
    private final ArrayList<NavalBattleClientHandler> players;
    
    GamePlaying(GameParameters parameters, ArrayList<NavalBattleClientHandler> players) {
        this.parameters = parameters;
        this.players = players;
    }

    public GameParameters getParameters() {
        return parameters;
    }

    public ArrayList<NavalBattleClientHandler> getPlayers() {
        return players;
    }
}
