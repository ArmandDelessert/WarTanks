package navalbattle.boats;

import navalbattle.protocol.common.NavalBattleProtocol;

public class BoatPosition {

    private final int x;
    private final int y;
    private final NavalBattleProtocol.BOAT_ORIENTATION orientation;
    private final int length;
    
    public BoatPosition(int x, int y, NavalBattleProtocol.BOAT_ORIENTATION orientation, int length) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.length = length;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLength() {
        return length;
    }

    public NavalBattleProtocol.BOAT_ORIENTATION getOrientation() {
        return orientation;
    }
}
