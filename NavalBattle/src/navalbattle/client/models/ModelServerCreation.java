package navalbattle.client.models;

import navalbattle.controllers.MainController;
import navalbattle.datamodel.DMServerConfig;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.messages.common.NavalBattleMessage;
import navalbattle.server.GameParameters;
import navalbattle.storage.StorageUtils;

public class ModelServerCreation extends Model {

    public ModelServerCreation(MainController controller) {
        super(controller);
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
    }

    public void userWantsToStartServer(GameParameters gameParameters) {
        
        // Storing last game params in XML file
        
        DMServerConfig entity = new DMServerConfig();
        entity.setValues(gameParameters.getName(), gameParameters.getMapSize(), gameParameters.getOpponentType(), gameParameters.getPassword() != null, gameParameters.getPassword(), gameParameters.isBonusMinesEnabled(), gameParameters.isBonusSatelliteEnabled());
        
        try {
            StorageUtils.writeServerConfigFile(entity);
        } catch (Exception ex) {
            // TO-DO
        }
        
        this.getController().startGameServer(gameParameters);
    }
    
    public DMServerConfig getPreviousServerParameters()
    {
        try {
            return StorageUtils.getServerConfig();
        } catch (Exception ex) {
            // TO-DO
            return null;
        }
    }

    public boolean isServerRunning() {
        return (this.getController().getCurrentGame() != null);
    }

    public void userWantsToconnectToCreatedServer(GameParameters gameParameters) {
        if (gameParameters.getPassword() == null)
            this.getController().connectToServer(new DiscoveredServer("127.0.0.1", this.getController().getServer().getServerParamsForBeaconing()), null);
        else
            this.getController().connectToServer(new DiscoveredServer("127.0.0.1", this.getController().getServer().getServerParamsForBeaconing()), gameParameters.getPassword(), null);
    }
}
