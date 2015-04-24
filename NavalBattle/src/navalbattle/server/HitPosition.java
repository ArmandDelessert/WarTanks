package navalbattle.server;

public class HitPosition {
    private final int x;
    private final int y;

    public HitPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
