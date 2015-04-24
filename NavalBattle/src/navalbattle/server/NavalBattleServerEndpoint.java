package navalbattle.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.protocol.beaconing.server.NavalBattleBeaconingServer;
import navalbattle.protocol.common.IRemoteMessageListener;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.common.NavalBattleTimeoutGuard;

public class NavalBattleServerEndpoint {

    private final IClientListener server;
    private final NavalBattleCommonStorage storage;
    private final NavalBattleTimeoutGuard timeoutGuard;
    private ServerSocket enpointSocket;
    private Thread accept;
    private HashSet<IRemoteMessageListener> listenersAllMessages = new HashSet<>();

    public NavalBattleServerEndpoint(IClientListener server) throws IOException {
        this.server = server;
        this.storage = new NavalBattleCommonStorage();
        this.timeoutGuard = new NavalBattleTimeoutGuard(NavalBattleProtocol.TIMEOUT, 1);
        this.timeoutGuard.startPeriodicCheck();
    }
    
    public void addMessageListener(IRemoteMessageListener listener)
    {
        this.listenersAllMessages.add(listener);
    }
    
    public boolean isMessageListenerRegistered(IRemoteMessageListener listener)
    {
        return this.listenersAllMessages.contains(listener);
    }

    public boolean isListeningForClients() {
        return (this.accept != null && this.accept.isAlive());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        this.stopAcceptingClients();
    }

    public void startAcceptingClients() throws IOException {
        if (this.isListeningForClients()) {
            return;
        }

        this.enpointSocket = new ServerSocket(NavalBattleProtocol.ENDPOINT_PORT);

        accept = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket newClient = enpointSocket.accept();
                        NavalBattleClientHandler newHandler = new NavalBattleClientHandler(storage, server, newClient);
                        storage.addClientHandler(newHandler);
                        timeoutGuard.addHandler(newHandler);

                    } catch (IOException ex) {
                        Logger.getLogger(NavalBattleBeaconingServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        accept.start();
    }

    public void stopAcceptingClients() {
        if (!this.isListeningForClients()) {
            return;
        }

        try {
            this.enpointSocket.close();
        } catch (IOException ex) {
        }

        this.accept.stop();
        this.accept = null;
        this.enpointSocket = null;
    }
}
