package navalbattle.client.models;

import navalbattle.controllers.MainController;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.datamodel.DMClientConfig;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.messages.common.NavalBattleMessage;  
import navalbattle.storage.StorageUtils;

public class ModelUsernameConf extends Model {

    private final boolean forceUserToSpecifyAUsername;
    private final LanguageHelper lh;
    private final String UI_NAME = "ui_usernameConf";
    
    public ModelUsernameConf(MainController controller, boolean forceUserToSpecifyAUsername) {
        super(controller);
        this.forceUserToSpecifyAUsername = forceUserToSpecifyAUsername;
        this.lh = controller.getLanguageHelper();
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
    }

    public void editUserParameters(String pseudo) {
        
        DMClientConfig entity = new DMClientConfig();
        entity.setValues(pseudo);
        
        this.getController().assignPseudo(pseudo);
        
        try {
            StorageUtils.writeClientConfigFile(entity);
        } catch (Exception ex) {
            // TO-DO
        }
    }
    
    public DMClientConfig getUserParameters()
    {
        try {
            return StorageUtils.getClientConfig();
        } catch (Exception ex) {
            // TO-DO
            return null;
        }
    }

    public boolean isConnectedToServer() {
        return this.getController().isConnectedToAServer();
    }

    public void process() {
        
        if (this.forceUserToSpecifyAUsername)
        {
            this.setChanged();
            this.notifyObservers(new NotifyMessage(NotifyMessage.Type.DISPLAY_INFO, this.lh.getTextDef(UI_NAME, "SPECIFY_YOUR_USERNAME_FIRST_LAUNCH")));
        }
    }

    public void userClickedCancel() {
        if (this.forceUserToSpecifyAUsername)
        {
            this.getController().exitApplication();
        }
    }
}
