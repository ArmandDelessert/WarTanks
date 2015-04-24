package navalbattle.protocol.common;

import navalbattle.protocol.messages.common.NavalBattleMessage;

public interface IRemoteMessageListener {
    
    public enum NOTIFICATION_TYPE { MESSAGE, BEEN_DISCONNECTED };
    
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message);
}
