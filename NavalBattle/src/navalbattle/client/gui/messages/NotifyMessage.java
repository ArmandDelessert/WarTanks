package navalbattle.client.gui.messages;

public class NotifyMessage {
    public enum Type { DISPLAY_INFO, DISPLAY_ERROR, CLOSE_WINDOW }
    
    private final Type type;
    private final Object data;

    public NotifyMessage(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
