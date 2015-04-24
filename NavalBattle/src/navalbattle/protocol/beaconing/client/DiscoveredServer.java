package navalbattle.protocol.beaconing.client;

import java.util.Date;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.server.GameParameters;

public final class DiscoveredServer {

    protected Date lastBeaconReceived;
    protected String hostName;
    protected String serverName;
    protected boolean requiresAuthentication;
    protected int playersCount;
    protected boolean serverFull;
    protected MapSizeEnum mapSize;
  
    public DiscoveredServer(String hostName, String serverName, boolean requiresAuthentication, int playersCount, boolean serverFull, MapSizeEnum mapSize) {
        this.hostName = hostName;
        this.serverName = serverName;
        this.requiresAuthentication = requiresAuthentication;
        this.playersCount = playersCount;
        this.serverFull = serverFull;
        this.mapSize = mapSize;
        
        this.ReceivedBeacon();
    }

    public DiscoveredServer(String hostName, BeaconingParameters bp) {
        this(hostName, bp.getServerName(), bp.isAuthenticationRequired(), bp.getPlayersConnected(), bp.isServerFull(), bp.getMapSize());
    }

    public String getHostName() {
        return hostName;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isAuthenticationRequired() {
        return requiresAuthentication;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public boolean isServerFull() {
        return serverFull;
    }

    public MapSizeEnum getMapSize() {
        return mapSize;
    }

    public void ReceivedBeacon() {
        this.lastBeaconReceived = new Date();
    }

    public Date GetLastBeacon() {
        return this.lastBeaconReceived;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + this.hostName.hashCode();
        //hash = hash * 23 + this.serverName.hashCode();
        //hash = hash * 26 + (this.requiresAuthentication ? 1 : 0);
        //hash = hash * 30 + this.mapSize.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        DiscoveredServer other = (DiscoveredServer) obj;

        return (this.hostName.equals(other.hostName)
                && this.serverName.equals(other.serverName)
                && this.requiresAuthentication == other.requiresAuthentication
                && this.playersCount == other.playersCount
                && this.mapSize == other.mapSize);
    }
}