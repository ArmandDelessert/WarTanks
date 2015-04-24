package navalbattle.protocol.common;

public enum MapSizeEnum {

    SMALL(7, 27, new BoatQuantity(0, 1, 1, 2), 1, 1, 1, 0, 0, 1, 3),
    MEDIUM(10, 27, new BoatQuantity(1, 2, 2, 2), 1, 3, 1, 1, 1, 1, 4),
    LARGE(15, 20, new BoatQuantity(3, 3, 0, 3), 5, 7, 2, 2, 2, 3, 5);

    private final int gridSize;
    private final int cellSize;
    private final BoatQuantity quantity;
    private final int minesCountMin;
    private final int minesCountMax;
    private final int minesRadius;
    private final int satCountMin;
    private final int satCountMax;
    private final int satRevelationPercentageMin;
    private final int satRevelationPercentageMax;

    
    /**
     * 
     * @param gridSize width/height of the game grid
     * @param cellSize size in pixel of a single cell
     * @param quantity number of boats of each type
     * @param minesCountMin minimal number of mines to place on the game grid, per player
     * @param minesCountMax maximum number of mines to place on the game grid, per player
     * @param minesRadius radius of the mines, in cellSize
     * @param satCountMin minimal number of sat to place on the game grid, per player
     * @param satCountMax maximal number of mines to place on the game grid, per player
     * @param satRevelationPercentageMin minimal percentage of cells to reveal when a sat is hit
     * @param satRevelationPercentageMax maximal percentage of cells to reveal when a sat is hit
     */
    MapSizeEnum(int gridSize, int cellSize, BoatQuantity quantity,
            int minesCountMin, int minesCountMax, int minesRadius,
            int satCountMin, int satCountMax,
            int satRevelationPercentageMin, int satRevelationPercentageMax) {

        if (gridSize < 0 || cellSize < 1 || minesCountMin < 0 || minesCountMax < 0 || minesCountMin > minesCountMax
                || satCountMin < 0 || satCountMin > satCountMax || satRevelationPercentageMin < 0 || satRevelationPercentageMin > satRevelationPercentageMax) {
            throw new IllegalArgumentException();
        }

        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.quantity = quantity;

        this.minesCountMin = minesCountMin;
        this.minesCountMax = minesCountMax;
        this.minesRadius = minesRadius;

        this.satRevelationPercentageMin = satRevelationPercentageMin;
        this.satRevelationPercentageMax = satRevelationPercentageMax;

        this.satCountMin = satCountMin;
        this.satCountMax = satCountMax;
    }

    public int getSize() {
        return this.gridSize;
    }

    public int getCellSize() {
        return this.cellSize;
    }

    public BoatQuantity getQuantityOfBoats() {
        return this.quantity;
    }

    public int getMinesCountMin() {
        return minesCountMin;
    }

    public int getMinesCountMax() {
        return minesCountMax;
    }

    public int getSatCountMin() {
        return satCountMin;
    }

    public int getSatCountMax() {
        return satCountMax;
    }

    public int getMinesRadius() {
        return minesRadius;
    }

    public int getSatRevelationPercentageMin() {
        return satRevelationPercentageMin;
    }

    public int getSatRevelationPercentageMax() {
        return satRevelationPercentageMax;
    }
}
