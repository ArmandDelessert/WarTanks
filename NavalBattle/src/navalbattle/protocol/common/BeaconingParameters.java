package navalbattle.protocol.common;

import navalbattle.server.CurrentGame;
import navalbattle.server.GameParameters;


public class BeaconingParameters {

    private final String serverName;
    private final boolean authenticationRequired;
    private final int playersConnected;
    private final boolean isServerFull;
    private final MapSizeEnum mapSize;

    public String getServerName() {
        return serverName;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public boolean isServerFull() {
        return isServerFull;
    }

    public MapSizeEnum getMapSize() {
        return mapSize;
    }
    
    public BeaconingParameters(CurrentGame game) {
        
        GameParameters parameters = game.getParameters();
        
        this.serverName = parameters.getName();
        this.authenticationRequired = parameters.isPasswordProtected();
        this.playersConnected = game.getPlayersCount();
        this.isServerFull = game.isServerFull();
        this.mapSize = parameters.getMapSize();
    }
    
     public BeaconingParameters(String serverName, boolean authenticationRequired, int playersConnected, boolean isServerFull, MapSizeEnum mapSize) {
        this.serverName = serverName;
        this.authenticationRequired = authenticationRequired;
        this.playersConnected = playersConnected;
        this.isServerFull = isServerFull;
        this.mapSize = mapSize;
    }
}
