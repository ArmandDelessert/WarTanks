package navalbattle.bonus;

import java.util.ArrayList;
import navalbattle.server.HitPosition;
import navalbattle.server.PlayerGrid;

// TO-DO: Mines can trigger other mines
public class Mine extends Bonus {

    // Position wich will trigger the mine
    private final int x;
    private final int y;
    private final int radius;

    public Mine(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public ArrayList<HitPosition> fire(int x, int y, PlayerGrid playerWhoWillSufferDamages, PlayerGrid playerWhoFired) {
        if (x != this.x && y != this.y) {
            return null;
        }
        
        ArrayList<HitPosition> ret = new ArrayList<>();

        int xfrom = this.x - this.radius;
        if (xfrom < 0) {
            xfrom = 0;
        }

        int xto = Math.min(playerWhoWillSufferDamages.getGridSizeX() - 1, this.x + this.radius);

        int yfrom = this.y - this.radius;
        if (yfrom < 0) {
            yfrom = 0;
        }

        int yto = Math.min(playerWhoWillSufferDamages.getGridSizeY() - 1, this.y + this.radius);

        for (int xa = xfrom; xa <= xto; ++xa) {
            for (int ya = yfrom; ya <= yto; ++ya) {
                ret.add(new HitPosition(xa, ya));
            }
        }

        return ret;
    }
}
