package navalbattle.client.viewmodelmessages;

public class ExternalIPReceived {

    private final String ip;
    
    public ExternalIPReceived(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
