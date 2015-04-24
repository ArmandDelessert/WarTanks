package navalbattle.protocol.client;

import navalbattle.protocol.messages.common.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class NavalBattleClientMessagesHelper {

    /**
     * Parse the XML message into a high-level representation of the message
     * @param message The XML document to parse
     * @param listener The message listener to notify (can be null)
     * @return NavalBattleMessage The high-level message parsed from the supplied XML message
     * @throws navalbattle.protocol.messages.common.InvalidMessage In case the supplied XML message cannot be parsed into a high-level message
     */
    
    public static NavalBattleMessage parseMessageToRepresentation(Document message, IServerListener listener) throws InvalidMessage {
        Node root = message.getFirstChild();

        String rootName;
        String messageType;
        
        try
        {
            rootName = root.getNodeName();
            messageType = root.getAttributes().getNamedItem("type").getNodeValue();
        }
        catch (Exception ex)
        {
            throw new InvalidMessage();
        }

        switch (rootName) {
            case "request":

                switch (messageType) {
                    
                    case "gameReadyToStart": {
                        NavalBattleGameReadyToStart messageReceived = new NavalBattleGameReadyToStart();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_GameReadyToStartReceived(messageReceived);
                        }

                        return messageReceived;
                    }
                    
                    case "endOfGame": {
                        NavalBattleEndOfGame messageReceived = new NavalBattleEndOfGame();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_EndOfGameReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "gameStart": {
                        NavalBattleGameStart messageReceived = new NavalBattleGameStart();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_GameStartReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "nop": {
                        NavalBattleNop messageReceived = new NavalBattleNop();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_NopReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "attackedCoordinate": {
                        NavalBattleAttackedCoordinate messageReceived = new NavalBattleAttackedCoordinate();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_AttackedCoordinateReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "chatReceive": {
                        NavalBattleChatReceive messageReceived = new NavalBattleChatReceive();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_ChatReceiveReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "userDisconnect": {
                        NavalBattleUserDisconnect messageReceived = new NavalBattleUserDisconnect();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_UserDisconnectReceived(messageReceived);
                        }

                        return messageReceived;
                    }
                    
                    case "disconnect": {
                        NavalBattleDisconnect messageReceived = new NavalBattleDisconnect();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_BeenDisconnectedReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    default: {
                        if (listener != null) {
                            listener.message_UnknownMessageReceived(message);
                        }

                        return null;
                    }
                }

            case "response":

                switch (messageType) {
                    case "connect": {
                        NavalBattleConnectResponse messageReceived = new NavalBattleConnectResponse();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_ConnectReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "attackCoordinate": {
                        NavalBattleAttackCoordinateResponse messageReceived = new NavalBattleAttackCoordinateResponse();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_AttackCoordinateResponseReceived(messageReceived);
                        }

                        return messageReceived;
                    }
                    case "positionMyBoats": {
                        NavalBattlePositionMyBoatsResponse messageReceived = new NavalBattlePositionMyBoatsResponse();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_PositionMyBoatsReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    case "chatSend": {
                        NavalBattleChatSendResponse messageReceived = new NavalBattleChatSendResponse();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_ChatSendReceived(messageReceived);
                        }

                        return messageReceived;
                    }
                    
                    case "getGameParameters": {
                        NavalBattleGameParametersResponse messageReceived = new NavalBattleGameParametersResponse();
                        messageReceived.parse(message);

                        // Callback
                        if (listener != null) {
                            listener.message_GetGameParametersReceived(messageReceived);
                        }

                        return messageReceived;
                    }

                    default: {
                        if (listener != null) {
                            listener.message_UnknownMessageReceived(message);
                        }

                        return null;
                    }
                }

            default: {
                if (listener != null) {
                    listener.message_UnknownMessageReceived(message);
                }

                return null;
            }
        }
    }
}
