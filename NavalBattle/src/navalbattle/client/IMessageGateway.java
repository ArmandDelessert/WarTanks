package navalbattle.client;

import navalbattle.protocol.common.IRemoteMessageListener;
import navalbattle.protocol.messages.common.NavalBattleMessage;

public interface IMessageGateway {
    public void subscribe(IRemoteMessageListener subscriber);
    public void unsubscribe(IRemoteMessageListener subscriber);
    public void sendMessage(NavalBattleMessage message);
}
