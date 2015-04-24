package navalbattle.bonus;

public class RevelationsDueToSat {

    private final int x;
    private final int y;
    private final boolean isThereAShipThere;
    
    RevelationsDueToSat(int x, int y, boolean isThereAShipThere) {
        this.x = x;
        this.y = y;
        this.isThereAShipThere = isThereAShipThere;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isIsThereAShipThere() {
        return isThereAShipThere;
    }
}
