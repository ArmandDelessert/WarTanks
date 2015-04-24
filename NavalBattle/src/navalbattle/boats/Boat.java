package navalbattle.boats;

import java.awt.image.BufferedImage;
import navalbattle.protocol.common.NavalBattleProtocol;

public abstract class Boat {

    public abstract BufferedImage getImageVertical();
    public abstract BufferedImage getImageHorizontal();
    
    private int x = -1;
    private int y = -1;
    private NavalBattleProtocol.BOAT_ORIENTATION orientation = null;
    private final int length;
    private final boolean[] hitPos;
    private boolean isSinked;

    public Boat(int length) {

        if (length < 1) {
            throw new IllegalArgumentException();
        }

        this.length = length;
        this.hitPos = new boolean[this.length];
        this.isSinked = false;
    }

    public int getLength() {
        return length;
    }

    public boolean isSinked() {
        return isSinked;
    }

    public boolean isDamagedAtBloc(int bloc) {
        if (bloc < 0 || bloc >= this.length) {
            throw new IllegalArgumentException();
        }

        return this.hitPos[bloc];
    }

    public BoatPosition getPosition() {
        return new BoatPosition(this.x, this.y, this.orientation, this.length);
    }

    public void setPosition(int x, int y, NavalBattleProtocol.BOAT_ORIENTATION orientation) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }

        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }
    
    public boolean isDamagedAt(int x, int y) throws PositionNotSetException, PositionIsNotOnShipException {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        
        if (this.x < 0 || this.y < 0 || orientation == null) {
            throw new PositionNotSetException();
        }
        
        if (!this.isPositionOnShip(x, y)) {
            throw new PositionIsNotOnShipException();
        }
        
        int bloc;

        if (this.orientation == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) {
            bloc = x - this.x;
        } else {
            bloc = y - this.y;
        }
        
        return hitPos[bloc];
    }

    public NavalBattleProtocol.HIT_ON_BOAT fire(int x, int y) throws PositionNotSetException {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }

        if (this.x < 0 || this.y < 0 || orientation == null) {
            throw new PositionNotSetException();
        }

        if (!this.isPositionOnShip(x, y)) {
            return NavalBattleProtocol.HIT_ON_BOAT.MISSED;
        }

        int bloc;

        if (this.orientation == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) {
            bloc = x - this.x;
        } else {
            bloc = y - this.y;
        }

        if (this.isSinked || hitPos[bloc]) {
            return NavalBattleProtocol.HIT_ON_BOAT.ALREADY_FIRED_ON_THAT_POSITION;
        }

        hitPos[bloc] = true;

        for (int i = 0; i < this.length; ++i) {
            // At least one part of the ship is still good
            if (!this.isDamagedAtBloc(i)) {
                return NavalBattleProtocol.HIT_ON_BOAT.HIT;
            }
        }

        this.isSinked = true;
        return NavalBattleProtocol.HIT_ON_BOAT.SINKED;
    }

    public boolean isPositionOnShip(int x, int y) throws PositionNotSetException {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }

        if (this.x < 0 || this.y < 0 || orientation == null) {
            throw new PositionNotSetException();
        }

        if (this.orientation == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) {
            if (y != this.y) {
                return false;
            }

            int xStart = this.x;
            int xEnd = this.x + this.length;

            return (x >= xStart && x < xEnd);
        } else {
            if (x != this.x) {
                return false;
            }

            int yStart = this.y;
            int yEnd = this.y + this.length;

            return (y >= yStart && y < yEnd);
        }
    }
}
