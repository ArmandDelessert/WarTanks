package navalbattle.server;

import navalbattle.protocol.messages.common.*;
import org.w3c.dom.Document;

// This interface is intended to be implemented by the server

public interface IClientListener {
    public void message_AttackCoordinateReceived(NavalBattleClientHandler client, NavalBattleAttackCoordinate message); // A client attacks
    // public void message_AttackedCoordinateReceived(); // not applicable
    // public void message_ChatReceiveReceived(); // not applicable
    public void message_ChatReceiveReceived(NavalBattleClientHandler client, NavalBattleChatSend message); // A client wants to speak
    public void message_ConnectReceived(NavalBattleClientHandler client, NavalBattleConnect message); // Connect from a new client
    public void message_NopReceived(NavalBattleClientHandler client, NavalBattleNop message); // Heartbeat received from a client
    public void message_PositionMyBoatsReceived(NavalBattleClientHandler client, NavalBattlePositionMyBoats message); // A client positioned his/her boats
    public void message_UserDisconnected(NavalBattleClientHandler client); // One user disconnected
    public void message_CannotParseMessage(NavalBattleClientHandler client, String message); // We have received an invalid message (XML wise
    public void message_UnknownMessageReceived(NavalBattleClientHandler client, Document message); // We have received an unknown message
    public void message_GetGameParametersReceived(NavalBattleClientHandler client, NavalBattleGameParameters message); // A client requests game parameters
    public void message_DisconnectReceived(NavalBattleClientHandler client, NavalBattleDisconnect message); // A client is disconnecting
}
