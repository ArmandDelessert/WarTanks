
package navalbattle.client.models;

import navalbattle.client.viewmodelmessages.CloseWindowAskPassword;
import java.util.Observable;
import java.util.Observer;
import navalbattle.controllers.MainController;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class ModelAskPass extends Model implements Observer {

    private final DiscoveredServer connectTo;
    
    public ModelAskPass(MainController controller, DiscoveredServer connectTo) {
        super(controller);
        this.connectTo = connectTo;
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
    }

    public void connectToServer(String password) {
        this.getController().connectToServer(this.connectTo, password, this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass() == CloseWindowAskPassword.class)
        {
            this.setChanged();
            this.notifyObservers(new NotifyMessage(NotifyMessage.Type.CLOSE_WINDOW, null));
        }
    }
}
