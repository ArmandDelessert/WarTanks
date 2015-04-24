package navalbattle.client.models;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import navalbattle.controllers.MainController;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.beaconing.client.ServersListener;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class ModelServersListing extends Model implements Observer {

    final ServersListener listener;
    
    public ModelServersListing(MainController controller) {
        super(controller);
        this.listener = this.getController().getServersListener();
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
    }
    
    public HashSet<DiscoveredServer> getDiscoveredServers() {
        return this.listener.GetServers(); // already a copy
    }
    
    public void userWantsToManuallySpecifiyServerToConnectTo() {
       this.getController().openManualConnect();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.getClass() == ServersListener.class)
        {
            this.setChanged();
            
            // args = list of servers
            this.notifyObservers(arg);
        }
    }

    public void userWantsToConnectToAServer(String host) {
        HashSet<DiscoveredServer> servers = this.listener.GetServers();
        DiscoveredServer connectTo = null;
        
        for (DiscoveredServer server : servers)
        {
            if (server.getHostName().equals(host))
            {
                connectTo = server;
                break;
            }
        }
        
        if (connectTo != null)
            this.getController().userWantsToConnectToAServer(connectTo);
    }
    
}
