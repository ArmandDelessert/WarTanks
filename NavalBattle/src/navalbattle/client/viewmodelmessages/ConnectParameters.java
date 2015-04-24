package navalbattle.client.viewmodelmessages;

import navalbattle.protocol.messages.common.NavalBattleConnect;

public class ConnectParameters {
    private final String host;
    private final String username;
    private final String password;
    private final NavalBattleConnect.DIGEST_TYPE digest;

    public ConnectParameters(String host, String username, String password, NavalBattleConnect.DIGEST_TYPE digest) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.digest = digest;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public NavalBattleConnect.DIGEST_TYPE getDigest() {
        return digest;
    }
}
