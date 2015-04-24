package navalbattle.client.models;

import navalbattle.client.NavalBattleClient;
import navalbattle.controllers.MainController;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.messages.common.NavalBattleGameReadyToStart;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class ModelWaitPlayers extends Model {
    
    private final NavalBattleClient client;
    private final DiscoveredServer connectedTo;
    private final String myUsername;
    
    @Override
    public void finalize() throws Throwable {
        if (this.client != null) {
            this.client.unsubscribe(this);
        }
    }
    
    public ModelWaitPlayers(MainController controller, NavalBattleClient client, DiscoveredServer connectedTo, String myUsername) {
        super(controller);
        this.client = client;
        this.connectedTo = connectedTo;
        this.myUsername = myUsername;
        
        this.client.subscribe(this);
    }
    
    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
        if (type == NOTIFICATION_TYPE.MESSAGE) {
            if (message.getClass() == NavalBattleGameReadyToStart.class) {
                this.client.unsubscribe(this);
                this.gameCanBegin();
            }
        }
    }
    
    private void gameCanBegin()
    {
        this.setChanged();
        this.notifyObservers(new NotifyMessage(NotifyMessage.Type.CLOSE_WINDOW, null));
        
        this.getController().openGameMain(this.client, myUsername, this.connectedTo);
    }
    
    public void userWantsToDisconnect() {
        this.getController().disconnectFromServer();
    }
}
