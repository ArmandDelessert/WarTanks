package navalbattle.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.protocol.client.IServerListener;
import navalbattle.protocol.client.NavalBattleClientMessagesHelper;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.common.ProtocolHelper;
import navalbattle.protocol.messages.common.*;
import navalbattle.nethelper.NetHelper;
import navalbattle.protocol.common.IRemoteMessageListener;
import navalbattle.protocol.common.ITimeoutHandler;
import navalbattle.protocol.common.NavalBattleTimeoutGuard;
import org.w3c.dom.Document;
import xmlhelper.XMLHelper;

public class NavalBattleClient implements IServerListener, IMessageGateway, ITimeoutHandler {

    private Socket server = null;
    private OutputStream bWriter;
    private InputStream bReader;
    private Thread reader = null;
    private Thread keepAlive = null;
    private Date lastSend = null;
    private Date lastReceived = null;
    private NavalBattleTimeoutGuard timeoutGuard = null;
    private final HashSet<IRemoteMessageListener> observersMessageNotification = new HashSet<>();
    private boolean gameReadyToStart = false;
    private boolean receivedServerParametersYet = false;

    public NavalBattleClient() {

    }

    @Override
    public void sendMessage(NavalBattleMessage message) {

        if (message == null) {
            throw new InvalidParameterException();
        }

        this.lastSend = new Date();
        ProtocolHelper.sendMessage(this.bWriter, message);
    }

    @Override
    public void finalize() throws Throwable {
        this.disconnect();
        super.finalize();
    }
    
   

    private void readMessagesAndProcess() throws IOException {
        while (true) {
            String message = ProtocolHelper.readMessage(this.bReader);

            if (message.trim().isEmpty()) {
                return;
            }

            Document messageXML;
            NavalBattleMessage highLevelMessage = null;

            try {
                messageXML = XMLHelper.parseXMLFromString(message);
                highLevelMessage = NavalBattleClientMessagesHelper.parseMessageToRepresentation(messageXML, this);
            } catch (Exception ex) {
                this.message_CannotParseMessage(message);
            }

            if (highLevelMessage != null) {
                this.notifiyNewMessageReceived(highLevelMessage);
            }
        }
    }

    private void notifiyNewMessageReceived(final NavalBattleMessage message) {

        this.lastReceived = new Date();

        new Thread() {
            @Override
            public void run()
            {
                synchronized (observersMessageNotification) {
                    for (IRemoteMessageListener listener : observersMessageNotification) {
                listener.onMessageReceived(IRemoteMessageListener.NOTIFICATION_TYPE.MESSAGE, message);
            }
                }
            }
        }.start();
    }

    private void notifiyBeenDisconnected() {

        new Thread() {
            @Override
            public void run()
            {
                synchronized (observersMessageNotification) {
                    for (IRemoteMessageListener listener : observersMessageNotification) {
                        listener.onMessageReceived(IRemoteMessageListener.NOTIFICATION_TYPE.BEEN_DISCONNECTED, null);
                    }
                }
            }
        }.start();
    }

    // connectTimeout in seconds
    public void connectToServer(String host, int connectTimeout) throws UnknownHostException, IOException {
        if (connectTimeout < 1) {
            throw new InvalidParameterException();
        }
        
        this.gameReadyToStart = false;
        this.receivedServerParametersYet = false;

        String ipToConnectTo = null;

        if (NetHelper.isIPv4Valid(host)) {
            ipToConnectTo = host;
        } else {
            // Getting IP from host

            ArrayList<InetAddress> ipAddressesOfThisDomain = new ArrayList<>();

            for (InetAddress addr : InetAddress.getAllByName(host)) {
                ipAddressesOfThisDomain.add(addr);
            }

            if (ipAddressesOfThisDomain.size() < 1) {
                throw new UnknownHostException();
            }

            // Arbitrary connecting to the first IP returned
            ipToConnectTo = ipAddressesOfThisDomain.get(0).getHostAddress();
        }

        this.server = new Socket();

        InetSocketAddress endPoint = new InetSocketAddress(ipToConnectTo, NavalBattleProtocol.ENDPOINT_PORT);
        this.server.connect(endPoint, connectTimeout * 1000);

        // Initializing reading and writing streams
        this.initStreams();

        this.lastReceived = new Date();
        this.timeoutGuard = new NavalBattleTimeoutGuard(NavalBattleProtocol.TIMEOUT, 1);
        this.timeoutGuard.addHandler(this);

        this.reader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readMessagesAndProcess();
                } catch (IOException ex) {
                    Logger.getLogger(NavalBattleClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        this.lastSend = new Date();
        this.keepAlive = new Thread(new Runnable() {
            @Override
            public void run() {
                keepAlive();
            }
        });

        this.reader.start();
        this.keepAlive.start();
    }

    private void keepAlive() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            Date limit = new Date();
            limit.setTime(limit.getTime() - NavalBattleProtocol.TIMEOUT / 3 * 1000);
            boolean hasReachedTimeout = this.lastSend.compareTo(limit) < 0;

            if (hasReachedTimeout) {
                this.sendMessage(new NavalBattleNop());
            }
        }
    }

    private void initStreams() {
        try {
            this.bWriter = this.server.getOutputStream();
            this.bReader = this.server.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(NavalBattleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isConnectedToServer() {
        return (this.server != null && server.isConnected());
    }

    public void message_AttackedCoordinateReceived() {
    }

    @Override
    public void message_ChatReceiveReceived(NavalBattleChatReceive message) {
    }

    @Override
    public void message_ChatSendReceived(NavalBattleChatSendResponse message) {
    }

    @Override
    public void message_ConnectReceived(NavalBattleConnectResponse message) {
    }

    @Override
    public void message_EndOfGameReceived(NavalBattleEndOfGame message) {
    }

    @Override
    public void message_GameStartReceived(NavalBattleGameStart message) {
    }

    @Override
    public void message_NopReceived(NavalBattleNop message) {
    }

    @Override
    public void message_PositionMyBoatsReceived(NavalBattlePositionMyBoatsResponse message) {
    }

    @Override
    public void message_UserDisconnectReceived(NavalBattleUserDisconnect message) {
    }

    @Override
    public void message_AttackCoordinateResponseReceived(NavalBattleAttackCoordinateResponse message) {
    }

    @Override
    public void message_AttackedCoordinateReceived(NavalBattleAttackedCoordinate message) {
    }

    @Override
    public void message_CannotParseMessage(String message) {
    }

    @Override
    public void message_UnknownMessageReceived(Document message) {
    }

    @Override
    public void subscribe(IRemoteMessageListener subscriber) {
        synchronized (this.observersMessageNotification) {
            this.observersMessageNotification.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(IRemoteMessageListener subscriber) {
        synchronized (this.observersMessageNotification) {
            this.observersMessageNotification.remove(subscriber);
        }
    }

    public void disconnect() {
        try {
            if (reader != null) {
                reader.stop();
            }

            if (bWriter != null) {
                bWriter.close();
            }

            if (bReader != null) {
                bReader.close();
            }

            if (server != null) {
                server.close();
                server = null;
            }

            if (keepAlive != null) {
                keepAlive.stop();
            }
        } catch (IOException ex) {
        } finally {
        }
    }

    @Override
    public void message_GetGameParametersReceived(NavalBattleGameParametersResponse message) {
        this.receivedServerParametersYet = true;
    }

    @Override
    public void message_GameReadyToStartReceived(NavalBattleGameReadyToStart message) {
        this.gameReadyToStart = true;
    }
    
    public boolean isGameReadyToStart()
    {
        return this.gameReadyToStart && this.receivedServerParametersYet;
    }

    @Override
    public Date lastCommunicationWas() {
        return this.lastReceived;
    }

    @Override
    public void hasReachedTimeout() {
        this.message_BeenDisconnected();
        this.notifiyBeenDisconnected();

        try {
            this.finalize();
        } catch (Throwable ex) {
        }
    }

    @Override
    public void message_BeenDisconnected() {
    }

    @Override
    public void message_BeenDisconnectedReceived(NavalBattleDisconnect messageReceived) {
    }
}
