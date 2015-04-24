package navalbattle.client.models;

import navalbattle.boats.BoatPosition;

public class BoatPositionWithNewGrid {
    final BoatPosition bp;
    final Boolean[][] newGrid;

    public BoatPositionWithNewGrid(BoatPosition bp, Boolean[][] newGrid) {
        this.bp = bp;
        this.newGrid = newGrid;
    }
    
    public BoatPosition getBp() {
        return bp;
    }

    public Boolean[][] getNewGrid() {
        return newGrid;
    }
}
