package navalbattle.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import navalbattle.protocol.common.ITimeoutHandler;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.common.ProtocolHelper;
import navalbattle.protocol.messages.common.*;
import org.w3c.dom.Document;
import xmlhelper.XMLHelper;

public class NavalBattleClientHandler implements ITimeoutHandler {

    public enum CLIENT_STATE {

        AUTHENTICATED, WAITING_FOR_CONNECT_MESSAGE
    }

    private final NavalBattleCommonStorage storage;
    private boolean isAuthenticated = false;
    private final Socket client;
    private final OutputStream bWriter;
    private final InputStream bReader;
    private final Date connectDate;
    private final IClientListener server;
    private Thread keepAlive = null;
    private Date lastSend = new Date();
    private CLIENT_STATE state = CLIENT_STATE.WAITING_FOR_CONNECT_MESSAGE;
    private final Thread handler;

    private Date lastTimeoutReceived;
    private String username = null;

    public NavalBattleCommonStorage getStorage() {
        return storage;
    }

    public void setIsAuthenticated(boolean b) {

        if (b) {
            this.state = CLIENT_STATE.AUTHENTICATED;
        } else {
            this.state = CLIENT_STATE.WAITING_FOR_CONNECT_MESSAGE;
        }

        this.isAuthenticated = b;
    }

    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    public void sendMessage(NavalBattleMessage message) {
        
        this.lastSend = new Date();
        ProtocolHelper.sendMessage(this.bWriter, message);
    }

    public void disconnectClient() {

        System.out.println("===[ IMPORTANT ] ===");
        System.out.println("Disconnecting client");
        System.out.println("====================");

        try {
            handler.stop();

            if (bWriter != null) {
                bWriter.close();
            }

            if (bReader != null) {
                bReader.close();
            }

            if (client != null) {
                client.close();
            }

            if (this.keepAlive != null) {
                this.keepAlive.stop();
            }

        } catch (IOException ex) {
        }
    }

    public boolean isUsernameKnown() {
        return (this.username != null);
    }

    public String getUsername() throws UsernameNotYetReceivedException {
        if (!isUsernameKnown()) {
            throw new UsernameNotYetReceivedException();
        }

        return this.username;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();

        this.disconnectClient();
    }

    public CLIENT_STATE getState() {
        return this.state;
    }

    public Socket getClient() {
        return this.client;
    }

    public Date getLastTimeoutReceived() {
        return this.lastTimeoutReceived;
    }

    public Date getConnectDate() {
        return this.connectDate;
    }

    public NavalBattleClientHandler(NavalBattleCommonStorage storage, IClientListener server, Socket client) throws IOException {
        this.storage = storage;
        this.server = server;
        this.client = client;
        this.bWriter = this.client.getOutputStream();
        this.bReader = this.client.getInputStream();
        this.lastTimeoutReceived = this.connectDate = new Date();

        // This thread will handle the client
        this.handler = new Thread(new Runnable() {
            @Override
            public void run() {
                handleClient();
            }
        });
        
        this.keepAlive = new Thread(new Runnable() {
            @Override
            public void run() {
                keepAlive();
            }
        });

        this.handler.start();
        this.keepAlive.start();
    }

    private boolean readMessageAndProcess() {

        String message;

        try {
            // Blocking call
            message = ProtocolHelper.readMessage(this.bReader);
        } catch (IOException ex) {
            this.disconnectClient();
            return false;
        }

        // Updating the last activity indicator
        this.updateLastCommunication();

        Document messageXML = null;

        try {
            messageXML = XMLHelper.parseXMLFromString(message);
        } catch (Exception ex) {

            messageXML = null; // just to make sure

            // REPORTING NEW MESSAGE
            this.server.message_CannotParseMessage(this, message);
        }

        // Parsing the message into a high-level message
        if (messageXML != null) {

            String messageCategory;
            String messageType;

            try {
                messageCategory = messageXML.getFirstChild().getNodeName(); // must be either "request" or "response"
                messageType = messageXML.getFirstChild().getAttributes().getNamedItem("type").getNodeValue();
            } catch (Exception ex) {
                this.server.message_CannotParseMessage(this, message);
                return false;
            }

            try {
                switch (messageCategory) {
                    case "request":

                        switch (messageType) {
                            case "getGameParameters": {
                                NavalBattleGameParameters messageReceived = new NavalBattleGameParameters().parse(messageXML);

                                // REPORTING NEW MESSAGE
                                this.server.message_GetGameParametersReceived(this, messageReceived);
                            }
                            break;

                            case "connect": {
                                NavalBattleConnect messageReceived = new NavalBattleConnect().parse(messageXML);

                                this.username = messageReceived.getUsername();

                                // REPORTING NEW MESSAGE
                                this.server.message_ConnectReceived(this, messageReceived);
                            }
                            break;

                            case "disconnect": {
                                NavalBattleDisconnect messageReceived = new NavalBattleDisconnect().parse(messageXML);

                                // REPORTING NEW MESSAGE
                                this.server.message_DisconnectReceived(this, messageReceived);

                            }
                            break;

                            case "attackCoordinate": {
                                NavalBattleAttackCoordinate messageReceived = new NavalBattleAttackCoordinate().parse(messageXML);

                                // REPORTING NEW MESSAGE
                                this.server.message_AttackCoordinateReceived(this, messageReceived);
                            }
                            break;

                            case "positionMyBoats": {
                                NavalBattlePositionMyBoats messageReceived = new NavalBattlePositionMyBoats().parse(messageXML);

                                // REPORTING NEW MESSAGE
                                this.server.message_PositionMyBoatsReceived(this, messageReceived);
                            }
                            break;

                            case "nop": {
                                NavalBattleNop messageReceived = new NavalBattleNop();

                                // REPORTING NEW MESSAGE
                                this.server.message_NopReceived(this, messageReceived);
                            }
                            break;

                            case "chatSend": {
                                NavalBattleChatSend messageReceived = new NavalBattleChatSend().parse(messageXML);

                                // REPORTING NEW MESSAGE
                                this.server.message_ChatReceiveReceived(this, messageReceived);
                            }
                            break;

                            default:
                                // REPORTING NEW MESSAGE
                                this.server.message_UnknownMessageReceived(this, messageXML);
                        }

                        break;

                    case "response":

                        switch (messageType) {

                            /*
                             For now there's no message of this type
                             */
                            default:
                                // REPORTING NEW MESSAGE
                                this.server.message_UnknownMessageReceived(this, messageXML);
                        }

                        break;

                    default:
                        // REPORTING NEW MESSAGE
                        this.server.message_UnknownMessageReceived(this, messageXML);
                }
            } catch (InvalidMessage ex) {
                // REPORTING NEW MESSAGE
                this.server.message_UnknownMessageReceived(this, messageXML);
            }

            return true;
        }

        return false;
    }

    private void handleClient() {
        while (readMessageAndProcess()) {
        };

        // Client is disconnected
    }

    private void updateLastCommunication() {
        this.lastTimeoutReceived = new Date();
    }

    @Override
    public Date lastCommunicationWas() {
        return this.lastTimeoutReceived;
    }

    @Override
    public void hasReachedTimeout() {

        this.disconnectClient();
        this.server.message_UserDisconnected(this);

        /*
         try {
         this.bReader.close();
         this.bWriter.close();
         this.client.close();
         } catch (IOException ex) {
         }
         */
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
}
