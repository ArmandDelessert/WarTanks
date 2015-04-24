package navalbattle.client.viewmodelmessages;

public class InternalIPReceived {

    private final String ip;
    
    public InternalIPReceived(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
