package navalbattle.client.models;

import java.io.IOException;

import navalbattle.client.NavalBattleClient;
import navalbattle.controllers.MainController;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.messages.common.NavalBattleGameParameters;
import navalbattle.protocol.messages.common.NavalBattleGameParametersResponse;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class ModelManualConnect extends Model {

    private String ipOrHostAddr;
    private NavalBattleClient client;
    private LanguageHelper lh;
    private String UI_NAME = "ui_manualConnect";

    public ModelManualConnect(MainController controller) {
        super(controller);
        this.lh = controller.getLanguageHelper();
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
        if (type == NOTIFICATION_TYPE.MESSAGE) {
            if (message.getClass() == NavalBattleGameParametersResponse.class) {

                this.client.unsubscribe(this);
                this.client.disconnect();
                
                NavalBattleGameParametersResponse castedMessage = (NavalBattleGameParametersResponse) message;

                BeaconingParameters gameParameters = castedMessage.getParameters();

                this.setChanged();
                this.notifyObservers(new NotifyMessage(NotifyMessage.Type.CLOSE_WINDOW, null));
                
                this.getController().userWantsToConnectToAServer(new DiscoveredServer(ipOrHostAddr, gameParameters));
            }
        }
    }

    public void connectToServer(String ipOrHostAddr) {
        this.ipOrHostAddr = ipOrHostAddr;
        this.client = new NavalBattleClient();

        try {
            this.client.connectToServer(ipOrHostAddr, 7); // TO-DO: check timeout value
        } catch (IOException ex) {
            this.cannotConnectToServer();
            return;
        }

        this.connectedToServer();
    }

    private void cannotConnectToServer() {
        this.setChanged();
        this.notifyObservers(new NotifyMessage(NotifyMessage.Type.DISPLAY_ERROR, this.lh.getTextDef(UI_NAME, "CANNOT_CONNECT_TO_SERVER")));
    }

    private void connectedToServer() {
        NavalBattleGameParameters getGameParameters = new NavalBattleGameParameters();

        this.client.subscribe(this);
        this.client.sendMessage(getGameParameters);
    }

}
