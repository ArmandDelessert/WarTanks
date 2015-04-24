package navalbattle.client.models;

import navalbattle.client.viewmodelmessages.CloseWindowAskPassword;
import navalbattle.client.viewmodelmessages.ConnectParameters;
import java.io.IOException;
import navalbattle.client.NavalBattleClient;
import navalbattle.controllers.MainController;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.messages.common.NavalBattleErrorCode;
import navalbattle.protocol.messages.common.NavalBattleConnect;
import navalbattle.protocol.messages.common.NavalBattleConnectResponse;
import navalbattle.protocol.messages.common.NavalBattleGameParameters;
import navalbattle.protocol.messages.common.NavalBattleGameParametersResponse;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public class ModelConnectServer extends Model {

    private final String serverName;
    private final ConnectParameters param;
    private NavalBattleClient client;
    private final LanguageHelper lh;
    private final String UI_NAME = "ui_connectServer";

    public void connect() {
        this.client = new NavalBattleClient();
        NavalBattleConnect connectMessage = new NavalBattleConnect();
        connectMessage.setValues(this.param.getUsername(), this.param.getPassword(), this.param.getDigest());

        try {
            client.connectToServer(this.param.getHost(), 7); // TO-DO: check timeout value
        } catch (IOException ex) {
            this.cannotConnectToServer();
        }

        this.connectedToServer();
    }

    public ModelConnectServer(MainController controller, ConnectParameters connectTo, String serverName) {
        super(controller);
        this.lh = this.getController().getLanguageHelper();
        this.param = connectTo;
        this.serverName = serverName;
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
        if (message.getClass() == NavalBattleConnectResponse.class) {
            NavalBattleConnectResponse response = (NavalBattleConnectResponse) message;
            NavalBattleErrorCode errorObj = response.getError();
            String error = "";

            switch (response.getResult()) {
                case AUTHENTICATED:
                case CONNECTED: {
                    this.setChanged();
                    this.notifyObservers(new CloseWindowAskPassword());
                    
                    NavalBattleGameParameters requestGameParameters = new NavalBattleGameParameters();
                    this.client.sendMessage(requestGameParameters);
                }
                break;

                case CONNECTION_ERROR:
                case AUTHENTICATION_ERROR:
                    switch (errorObj.getErrorCode())
                    {
                        case 1:
                            error = lh.getTextDef(UI_NAME, "USERNAME_ALREADY_CONNECTED");
                            break;
                            
                        case 2:
                            error = lh.getTextDef(UI_NAME, "BAD_PASSWORD");
                            break;
                        
                        case 3:
                            error = lh.getTextDef(UI_NAME, "SERVER_IS_FULL");
                            break;
                            
                       case 4:
                            error = lh.getTextDef(UI_NAME, "NOT_ALLOWED_TO_CONNECTED");
                            break;
                           
                       default:
                           error = "Connection error";
                           break;
                    }
                    
                    break;
            }

            if (!error.equals("")) {
                this.setChanged();
                this.notifyObservers(new NotifyMessage(NotifyMessage.Type.DISPLAY_ERROR, error));
                
                this.setChanged();
                this.notifyObservers(new NotifyMessage(NotifyMessage.Type.CLOSE_WINDOW, null));
            }
        } else if (message.getClass() == NavalBattleGameParametersResponse.class) {
            NavalBattleGameParametersResponse response = (NavalBattleGameParametersResponse) message;
            BeaconingParameters parameters = response.getParameters();

            this.setChanged();
            this.notifyObservers(new NotifyMessage(NotifyMessage.Type.CLOSE_WINDOW, null));

            this.getController().connectedToServer(this.param.getHost(), new DiscoveredServer(this.param.getHost(), parameters), this.client);
        }
    }

    private void cannotConnectToServer() {
        this.setChanged();
        this.notifyObservers(new NotifyMessage(NotifyMessage.Type.DISPLAY_ERROR, lh.getTextDef(UI_NAME, "CANNOT_ESTABLISH_CONNECTION_SERVER").replaceAll("%s", param.getHost())));

        this.setChanged();
        this.notifyObservers(new NotifyMessage(NotifyMessage.Type.CLOSE_WINDOW, null));
    }

    private void connectedToServer() {
        this.authenticate();
    }

    private void authenticate() {
        this.client.subscribe(this);

        // Send connect message
        NavalBattleConnect message = new NavalBattleConnect();
        message.setValues(param.getUsername(), param.getPassword(), param.getDigest());

        this.client.sendMessage(message);
    }

}
