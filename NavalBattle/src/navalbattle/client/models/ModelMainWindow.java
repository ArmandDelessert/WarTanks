package navalbattle.client.models;

import navalbattle.controllers.MainController;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class ModelMainWindow extends Model {

    public ModelMainWindow(MainController controller) {
        super(controller);
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
    }
    
    public void userWantsToEditUserParameters()
    {
        this.getController().editUserParameters(false);
    }
    
    public void userWantsToListBeaconingServers()
    {
        this.getController().openServersListing();
    }
    
    public void userWantsToExitApplication()
    {
        this.getController().exitApplication();
    }

    public void userWantsToListAllBroadcastingServers() {
        this.getController().openServersListing();
    }

    public void userWantsToCreateAServer() {
        this.getController().openServerCreation();
    }

    public void userWantsToViewServerStats() {
        this.getController().openServersStats();
    }

    public boolean isServerRunning() {
        return (this.getController().getCurrentGame() != null);
    }

    public boolean isConnectedToServer() {
        return this.getController().isConnectedToAServer();
    }

    public void userWantsToStopServer() {
        this.getController().stopGameServer();
    }
}
