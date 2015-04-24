package navalbattle.controllers;

import java.io.File;
import navalbattle.client.viewmodelmessages.ConnectParameters;
import java.io.IOException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import navalbattle.client.NavalBattleClient;
import navalbattle.client.gui.*;
import navalbattle.client.models.*;
import navalbattle.datamodel.DMClientConfig;
import navalbattle.lang.CannotReadLangFileException;
import navalbattle.lang.DuplicateConstantNameInLanguageFileException;
import navalbattle.lang.InvalidLangIdException;
import navalbattle.lang.LanguageHelper;
import navalbattle.lang.MalformedLangFileException;
import navalbattle.lang.NoLangFilesException;
import navalbattle.protocol.beaconing.client.DiscoveredServer;
import navalbattle.protocol.beaconing.client.ServersListener;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.NavalBattleConnect;
import navalbattle.protocol.messages.common.NavalBattleDisconnect;
import navalbattle.server.GameParameters;
import navalbattle.server.GamePlaying;
import navalbattle.server.NavalBattleServer;
import navalbattle.storage.StorageUtils;

public final class MainController {

    // Keeping track of all opened models
    final HashSet<Observable> allObservables = new HashSet<>();
    final String langRootFolder = "lang";
    final LanguageHelper languageHelper;
    final ServersListener serversListener;
    final String lang = "fr";
    NavalBattleServer gameServer = null;
    String username = null;
    private NavalBattleClient currentServer = null;
    
    public boolean checkBaseFoldersExist()
    {
        if (!(new File(langRootFolder).exists()))
            return false;
        
        if (!(new File("pictures").exists()))
            return false;
        
        if (!(new File("storage").exists()))
            return false;
        
        return true;
    }
    
    public ServersListener getServersListener() {
        return serversListener;
    }

    // Entry point of the program
    public static void main(String[] args) {
        // This controller will handle all high-level procedures (window coordination and high-level instances creation)
        new MainController();
    }
    
    public MainController() {

	if (!this.checkBaseFoldersExist())
        {
            JOptionPane.showMessageDialog(null, "One or more folder is missing." + System.lineSeparator() + "Please copy folders along the .jar file." + System.lineSeparator() + "Application will now close", "Critical error", JOptionPane.ERROR_MESSAGE);
            this.exitApplication();
        }
        
        // We are listening for servers broadcasts
        this.serversListener = new ServersListener(NavalBattleProtocol.BEACONING_PORT, NavalBattleProtocol.BEACONING_INTERVAL * 5);

        // Loading language files
        this.languageHelper = new LanguageHelper(langRootFolder);
        
        try {
            this.languageHelper.loadLangFiles(lang);
        } catch (MalformedLangFileException | InvalidLangIdException | NoLangFilesException | CannotReadLangFileException | DuplicateConstantNameInLanguageFileException ex) {
            JOptionPane.showMessageDialog(null, "Cannot load language files." + System.lineSeparator() + "Application will now close", "Critical error", JOptionPane.ERROR_MESSAGE);
            this.exitApplication();
        }
        
        try {
            // Assign the saved username
           DMClientConfig clientConfig = StorageUtils.getClientConfig();
           this.username = clientConfig.getUsername();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not read client config file." + System.lineSeparator() + "Application will now close", "Critical error", JOptionPane.ERROR_MESSAGE);
            this.exitApplication();
        }
        
        if (this.username == null || this.username.isEmpty())
        {
            this.editUserParameters(true);
        }
        else
        {
            this.openMainWindow();
        }
        
        /*
        DEBUG
        Creating a server
        Connecting two client on it
        //ModelIA iaTest = new ModelIA("", "", NavalBattleConnect.DIGEST_TYPE.NONE, null);
        
        this.startGameServer(new GameParameters("testserv", null, MapSizeEnum.SMALL, NavalBattleProtocol.OPPONENT_TYPE.HumanVSHuman, NavalBattleProtocol.BONUS_STATE.ACTIVATED, NavalBattleProtocol.BONUS_STATE.ACTIVATED));


        NavalBattleClient client1 = new NavalBattleClient();

        try {
        client1.connectToServer("127.0.0.1", 7);
        } catch (IOException ex) {
        }

        String usernameClient1 = this.username + new Random().nextInt(999);
        String usernameClient2 = this.username + new Random().nextInt(999);

        NavalBattleConnect connectMessage = new NavalBattleConnect();
        connectMessage.setValues(usernameClient1, null, NavalBattleConnect.DIGEST_TYPE.NONE);

        client1.sendMessage(connectMessage);

        this.openGameMain(client1, usernameClient1, new DiscoveredServer("127.0.0.1", new BeaconingParameters("testserv", false, 1, false, MapSizeEnum.SMALL)));


        NavalBattleClient client2 = new NavalBattleClient();

        try {
        client2.connectToServer("127.0.0.1", 7);
        } catch (IOException ex) {
        }

        connectMessage = new NavalBattleConnect();
        connectMessage.setValues(usernameClient2, null, NavalBattleConnect.DIGEST_TYPE.NONE);

        client2.sendMessage(connectMessage);

        this.openGameMain(client2, usernameClient2, new DiscoveredServer("127.0.0.1", new BeaconingParameters("testserv", false, 1, false, MapSizeEnum.SMALL)));
        */
    }
    
    public void startGameServer(GameParameters gameParameters) {

        // This starts the server AND the broadcasting
        this.gameServer = new NavalBattleServer(gameParameters);
        
        // Should we create and AI ?
        if (gameParameters.getOpponentType() == NavalBattleProtocol.OPPONENT_TYPE.HumanVSAI)
        {
            String iaUsername = "IAmIA";
            
            ModelIA ia = new ModelIA("127.0.0.1", iaUsername, gameParameters.getPassword() == null ? NavalBattleConnect.DIGEST_TYPE.NONE : NavalBattleConnect.DIGEST_TYPE.SHARED_SECRET, gameParameters.getPassword());
            ia.connectToServer();
        }
    }
    
    public void stopGameServer() {
        if (this.gameServer != null)
        {
            this.gameServer.stopGameServer();
            this.gameServer = null;
        }
    }
    
    public void connectToServer(String serverName, String host, String username, String password,
            NavalBattleConnect.DIGEST_TYPE type, Observer obs) {
        // Passing the connect parameters
        ConnectParameters connectParam = new ConnectParameters(host,
                username,
                // password must be null if digest = none
                password,
                type);

        // Creating the ConnectServer window and its associated model
        ModelConnectServer modelConnectServer = new ModelConnectServer(this, connectParam, serverName);
        UIConnectServer uiConnectServer = new UIConnectServer(modelConnectServer);
        modelConnectServer.addObserver(uiConnectServer);
        
        if (obs != null)
            modelConnectServer.addObserver(obs);
        
        this.allObservables.add(modelConnectServer);
        
        modelConnectServer.connect();
    }
    
    public void editUserParameters(boolean forceUserToSpecifyAUsername) {
        ModelUsernameConf modelUsernameConf = new ModelUsernameConf(this, forceUserToSpecifyAUsername);
        UIUsernameConf uiUsernameConf = null;
        
        try {
            uiUsernameConf = new UIUsernameConf(modelUsernameConf);
        } catch (IOException ex) {
        }
        
        modelUsernameConf.addObserver(uiUsernameConf);
        modelUsernameConf.process();
    }
    
    public void connectToSpecificServer() {
        // TO-DO

        ModelManualConnect modelManualConnect = new ModelManualConnect(this);
        UIManualConnect uiManualConnect = null;
        
        try {
            uiManualConnect = new UIManualConnect(modelManualConnect);
        } catch (IOException ex) {
        }
        
        modelManualConnect.addObserver(uiManualConnect);
        
        this.allObservables.add(modelManualConnect);
    }
    
    public void openMainWindow() {
        ModelMainWindow modelMainWindow = new ModelMainWindow(this);
        UIMainWindow uiMainWindow = null;
        
        try {
            uiMainWindow = new UIMainWindow(modelMainWindow);
        } catch (IOException ex) {
        }
        
        modelMainWindow.addObserver(uiMainWindow);
        
        this.allObservables.add(modelMainWindow);
    }
    
    public void openServersListing() {
        ModelServersListing modelServersListing = new ModelServersListing(this);
        UIServersListing uiServersListing = null;
        
        try {
            uiServersListing = new UIServersListing(modelServersListing);
        } catch (IOException ex) {
        }
        
        modelServersListing.addObserver(uiServersListing);
        
        this.serversListener.addObserver(modelServersListing);
        this.allObservables.add(modelServersListing);
    }
    
    public void openGameMain(NavalBattleClient client, String myUsername, DiscoveredServer connectedOn) {
        ModelGameMain modelGameMain = new ModelGameMain(this, client, connectedOn, myUsername);
        
        UIGameMain uiGameMain = null;
        
        try {
            uiGameMain = new UIGameMain(modelGameMain);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        modelGameMain.addObserver(uiGameMain);
        
        this.allObservables.add(modelGameMain);
    }
    
    public void openAskPass(DiscoveredServer connectTo) {
        ModelAskPass modelAskPass = new ModelAskPass(this, connectTo);
        UIAskPass uiAskPass = new UIAskPass(modelAskPass);
        
        modelAskPass.addObserver(uiAskPass);

        //modelAskPass.addObserver(uiAskPass);
        this.allObservables.add(modelAskPass);
    }
    
    public void openManualConnect() {
        ModelManualConnect modelManualConnect = new ModelManualConnect(this);
        UIManualConnect uiManualConnect = null;
        
        try {
            uiManualConnect = new UIManualConnect(modelManualConnect);
        } catch (IOException ex) {
        }
        
        modelManualConnect.addObserver(uiManualConnect);
        
        this.allObservables.add(modelManualConnect);
    }
    
    public void openServersStats() {
        ModelServerStats modelServerStats = new ModelServerStats(this);
        UIServerStats uiServerStats = null;
        
        try {
            uiServerStats = new UIServerStats(modelServerStats);
        } catch (IOException ex) {
        }

        modelServerStats.addObserver(uiServerStats);
        
        
        this.allObservables.add(modelServerStats);
        
        modelServerStats.viewStarted();
    }
    
    public void openServerCreation() {
        ModelServerCreation modelServerCreation = new ModelServerCreation(this);
        UIServerCreation uiServerCreation = null;
        
        try {
            uiServerCreation = new UIServerCreation(modelServerCreation);
        } catch (IOException ex) {
        }
        
        modelServerCreation.addObserver(uiServerCreation);
        
        this.allObservables.add(modelServerCreation);
        
    }

    
    public void waitForOpponent(String ip, DiscoveredServer connectedTo, NavalBattleClient client) {
        
        if (client.isGameReadyToStart())
        {
            this.openGameMain(client, this.username, connectedTo);
        }
        else
        {
            ModelWaitPlayers modelWaitPlayers = new ModelWaitPlayers(this, client, connectedTo, this.getMyUsername());
            UIWaitPlayers uiWaitPlayers = null;

            try {
                uiWaitPlayers = new UIWaitPlayers(modelWaitPlayers, connectedTo.getServerName(), ip);
            } catch (IOException ex) {
            }

            modelWaitPlayers.addObserver(uiWaitPlayers);

            this.allObservables.add(modelWaitPlayers);
        }
        
        
    }
    
    public void exitApplication() {
        System.exit(0);
    }
    
    public LanguageHelper getLanguageHelper() {
        return this.languageHelper;
    }
    
    public void userWantsToConnectToAServer(DiscoveredServer connectTo) {
        
        if (connectTo.isAuthenticationRequired()) {
            this.openAskPass(connectTo);
        } else {
            this.connectToServer(connectTo, null);
        }
    }
    
    public void connectToServer(DiscoveredServer connectTo, String password, Observer obs) {
        this.connectToServer(connectTo.getServerName(), connectTo.getHostName(), this.username, password, NavalBattleConnect.DIGEST_TYPE.SHARED_SECRET, obs);
    }
    
    public void connectToServer(DiscoveredServer connectTo, Observer obs) {
        this.connectToServer(connectTo.getServerName(), connectTo.getHostName(), this.username, null, NavalBattleConnect.DIGEST_TYPE.NONE, obs);
    }

    // Once connected and eventually authenticated
    public void connectedToServer(String host, DiscoveredServer connectedTo, NavalBattleClient client) {
        this.currentServer = client;
        this.waitForOpponent(host, connectedTo, client);
    }

    public void disconnectFromServer() {
        
        if (this.currentServer.isConnectedToServer())
        {
            NavalBattleDisconnect message = new NavalBattleDisconnect();
            this.currentServer.sendMessage(message);
        }
        
        this.currentServer.disconnect();
        this.currentServer = null;
    }
    
    public boolean isConnectedToAServer()
    {
        return (this.currentServer != null && this.currentServer.isConnectedToServer());
    }

    public String getMyUsername() {
         return this.username;
    }

    public GamePlaying getCurrentGame() {
        if (this.gameServer == null)
            return null;
        
        return this.gameServer.getGamePlaying();
    }

    public NavalBattleServer getServer() {
        return this.gameServer;
    }

    public void assignPseudo(String pseudo) {
        
        boolean openMainWindow = (this.username == null || this.username.isEmpty());
        
        this.username = pseudo;
        
        if (openMainWindow)
            this.openMainWindow();
    }


}
