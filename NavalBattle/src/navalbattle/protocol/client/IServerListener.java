package navalbattle.protocol.client;

import navalbattle.protocol.messages.common.*;
import org.w3c.dom.Document;

// This interface is intended to be implemented by the client
public interface IServerListener {

    /**
     * Function called when we receive an attack message from the opponent
     *
     * @param message
     */
    public void message_AttackCoordinateResponseReceived(NavalBattleAttackCoordinateResponse message); // Another player attacked me

    /**
     * Function called when we receive the response to our last attack
     *
     * @param message
     */
    public void message_AttackedCoordinateReceived(NavalBattleAttackedCoordinate message); // Response to our attack

    /**
     * Function called when we receive a chat message from our opponent or from
     * the server itself
     *
     * @param message
     */
    public void message_ChatReceiveReceived(NavalBattleChatReceive message); // Someone speaks

    /**
     * Function called when we receive a delivery message to a chat message we
     * sent
     *
     * @param message
     */
    public void message_ChatSendReceived(NavalBattleChatSendResponse message); // Acknowledgement

    /**
     * Function called when we receive a response to our last connect attempt
     *
     * @param message
     */
    public void message_ConnectReceived(NavalBattleConnectResponse message); // Connect response

    
    /**
     * Function called when we receive a response to our getGameParameters request
     *
     * @param message
     */
    public void message_GetGameParametersReceived(NavalBattleGameParametersResponse message); // Game parameters response
    
    /**
     * Function called when we receive the message indicating that the game
     * ended
     *
     * @param message
     */
    public void message_EndOfGameReceived(NavalBattleEndOfGame message); // Game ends

    /**
     * Function called when we receive the message indicating that the game started
     *
     * @param message
     */
    public void message_GameStartReceived(NavalBattleGameStart message); // Game starts

    /**
     * Function called when we receive the message indicating we are ready to start the game
     *
     * @param message
     */
    public void message_GameReadyToStartReceived(NavalBattleGameReadyToStart message);
    
    /**
     * Function called when we receive a heartbeat message from the server
     *
     * @param message
     */
    
    public void message_NopReceived(NavalBattleNop message); // Heartbeat received
    
    /**
     * Function called when we receive a response to our PositionMyBoats message
     *
     * @param message
     */
    public void message_PositionMyBoatsReceived(NavalBattlePositionMyBoatsResponse message); // Boat positioning response

    /**
     * Function called when we receive a notification from the server indicating someone just disconnected
     *
     * @param message
     */
    
    public void message_UserDisconnectReceived(NavalBattleUserDisconnect message); // One user disconnected
    
    /**
     * Function called when we receive a malformed message from the server
     *
     * @param message
     */
    public void message_CannotParseMessage(String message); // We have received an invalid message (XML wise

    /**
     * Function called when we receive an unknown message type from the server
     *
     * @param message
     */
    public void message_UnknownMessageReceived(Document message); // We have received an unknown message
    
    
    /**
     * Function called when the server sends a disconnect message
     * @param message
     */
    
    public void message_BeenDisconnected();

    public void message_BeenDisconnectedReceived(NavalBattleDisconnect messageReceived);
    
}
