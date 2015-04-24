package navalbattle.server;

import java.awt.Dimension;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.boats.BoatPosition;
import navalbattle.boats.BoatUtils;
import navalbattle.boats.PositionNotSetException;
import navalbattle.bonus.Mine;
import navalbattle.bonus.RevelationsDueToSat;
import navalbattle.bonus.SpySat;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;

public class PlayerGrid {

    private ArrayList<Boat> allBoats;
    private final HashMap<Integer, HashMap<Integer, Mine>> allMines = new HashMap<>();
    private final HashMap<Integer, HashMap<Integer, SpySat>> allSpySat = new HashMap<>();
    private final HashSet<HitPosition> missedFires = new HashSet<>();
    private final MapSizeEnum mapSize;

    private final NavalBattleProtocol.BONUS_STATE minesEnabled;
    private final NavalBattleProtocol.BONUS_STATE satEnabled;

    private Random rng;
    private int numberOfMines;
    private int numberOfSpySatBonus;
    private int minesRadius;
    private int satRevelationPercentageMin;
    private int satRevelationPercentageMax;

    private final int gridSizeX;
    private final int gridSizeY;

    private Mine isThereAMineOn(int x, int y) {
        if (!this.allMines.containsKey(x)) {
            return null;
        }

        HashMap<Integer, Mine> atX = this.allMines.get(x);

        if (atX.containsKey(y)) {
            return atX.get(y);
        }

        return null;
    }

    private SpySat isThereASpySat(int x, int y) {
        if (!this.allSpySat.containsKey(x)) {
            return null;
        }

        HashMap<Integer, SpySat> atX = this.allSpySat.get(x);

        if (atX.containsKey(y)) {
            return atX.get(y);
        }

        return null;
    }

    private HitPosition getRandomUnusedPositionForBonus(ArrayList<HitPosition> excludePositions) {

        while (true) {
            int xPos = this.rng.nextInt(this.gridSizeX);
            int yPos = this.rng.nextInt(this.gridSizeY);

            // There must be no boat there
            boolean isThereABoatOnThatPosition = false;

            for (Boat boat : allBoats) {
                try {
                    if (boat.isPositionOnShip(xPos, yPos)) {
                        isThereABoatOnThatPosition = true;
                        break;
                    }
                } catch (PositionNotSetException ex) {
                    // Should never happen
                }
            }

            if (isThereABoatOnThatPosition) {
                continue;
            }

            boolean excludeThisPositionBecauseThereAlreadyIsAMineThere = false;

            for (HitPosition pos : excludePositions) {
                if (pos.getX() == xPos && pos.getY() == yPos) {
                    excludeThisPositionBecauseThereAlreadyIsAMineThere = true;
                    break;
                }
            }

            if (excludeThisPositionBecauseThereAlreadyIsAMineThere) {
                continue;
            }

            return new HitPosition(xPos, yPos);
        }
    }

    public void addBonusses() throws BoatsNotPlacedException {

        if (allBoats.isEmpty()) {
            throw new BoatsNotPlacedException();
        }

        if (this.minesEnabled == NavalBattleProtocol.BONUS_STATE.ACTIVATED) {
            this.numberOfMines = this.rng.nextInt(Math.abs(this.mapSize.getMinesCountMax() - this.mapSize.getMinesCountMin()) + 1) + this.mapSize.getMinesCountMin();
            this.minesRadius = this.mapSize.getMinesRadius();
        } else {
            this.numberOfMines = 0;
            this.minesRadius = 0;
        }

        if (this.satEnabled == NavalBattleProtocol.BONUS_STATE.ACTIVATED) {
            this.numberOfSpySatBonus = this.rng.nextInt(Math.abs(this.mapSize.getSatCountMax() - this.mapSize.getSatCountMin()) + 1) + this.mapSize.getSatCountMin();
        } else {
            this.numberOfSpySatBonus = 0;
        }

        this.satRevelationPercentageMin = this.mapSize.getSatRevelationPercentageMin();
        this.satRevelationPercentageMax = this.mapSize.getSatRevelationPercentageMax();

        ArrayList<HitPosition> positionBonus = new ArrayList<>();

        for (int i = 0; i < numberOfMines; ++i) {
            // Warning: can fail in an infinite loop if there is no more space left on the grid
            HitPosition position = this.getRandomUnusedPositionForBonus(positionBonus);

            final int posX = position.getX();
            final int posY = position.getY();

            Mine mine = new Mine(posX, posY, this.minesRadius);

            if (!allMines.containsKey(posX)) {

                HashMap<Integer, Mine> toAdd = new HashMap<>();
                toAdd.put(posY, mine);

                allMines.put(posX, toAdd);
            } else {
                allMines.get(posX).put(posY, mine);
            }

            positionBonus.add(position);
        }

        for (int i = 0; i < this.numberOfSpySatBonus; ++i) {
            // Warning: can fail in an infinite loop if there is no more space left on the grid
            HitPosition position = this.getRandomUnusedPositionForBonus(positionBonus);

            final int posX = position.getX();
            final int posY = position.getY();

            SpySat spySat = new SpySat(this.satRevelationPercentageMin, this.satRevelationPercentageMax, posX, posY);

            if (!allSpySat.containsKey(posX)) {

                HashMap<Integer, SpySat> toAdd = new HashMap<>();
                toAdd.put(posY, spySat);

                allSpySat.put(posX, toAdd);
            } else {
                allSpySat.get(posX).put(posY, spySat);
            }

            positionBonus.add(position);
        }
    }

    public ArrayList<Boat> getAllBoats() {
        return allBoats;
    }

    public boolean isAllFleetSinked() {
        for (Boat boat : this.allBoats) {
            if (!boat.isSinked()) {
                return false;
            }
        }

        return true;
    }

    public int getGridSizeX() {
        return gridSizeX;
    }

    public int getGridSizeY() {
        return gridSizeY;
    }

    public void print(PrintStream out) {

        String[][][] grid = new String[this.gridSizeX][this.gridSizeY][1];

        for (int i = 0; i < this.gridSizeX; ++i) {
            for (int j = 0; j < this.gridSizeY; ++j) {
                grid[i][j][0] = "  ";
            }
        }

        for (Boat boat : allBoats) {
            BoatPosition position = boat.getPosition();
            int posX = position.getX();
            int posY = position.getY();
            int length = position.getLength();
            NavalBattleProtocol.BOAT_ORIENTATION orientation = position.getOrientation();

            if (orientation == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) {
                int endX = posX + length;

                for (int i = posX; i < endX; ++i) {
                    boolean isDamaged = boat.isDamagedAtBloc(i - posX);

                    grid[i][posY][0] = (isDamaged ? "X" : "O") + " ";
                    //grid[i][posY][1] = isDamaged ? "XX" : "OO";
                }
            } else {
                int endY = posY + length;

                for (int i = posY; i < endY; ++i) {
                    boolean isDamaged = boat.isDamagedAtBloc(i - posY);

                    grid[posX][i][0] = (isDamaged ? "X" : "O") + " ";
                    //grid[posY][i][1] = isDamaged ? "XX" : "OO";
                }
            }
        }

        for (HitPosition hit : this.missedFires) {
            grid[hit.getX()][hit.getY()][0] = "X" + " ";
        }

        for (Map.Entry<Integer, HashMap<Integer, Mine>> column : this.allMines.entrySet()) {
            final int posX = (int) column.getKey();

            for (Map.Entry<Integer, Mine> line : column.getValue().entrySet()) {
                final int posY = line.getKey();
                String letter = line.getValue().isActive() ? "M" : "X";
                grid[posX][posY][0] = letter + " ";
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, SpySat>> column : this.allSpySat.entrySet()) {
            final int posX = (int) column.getKey();

            for (Map.Entry<Integer, SpySat> line : column.getValue().entrySet()) {
                final int posY = line.getKey();
                grid[posX][posY][0] = "S" + " ";
            }
        }

        final String ls = System.lineSeparator();

        // Horizontal labels
        out.print("  ");
        for (int j = 0; j < this.gridSizeX; ++j) {
            out.print(Character.toString((char) (j + 'A')) + " ");
        }
        out.print(ls);

        // Printing every vertical grid
        for (int i = 0; i < this.gridSizeY; ++i) {

            // Vertical label
            out.print(i + " ");

            // Printing grid horizontally
            for (int c = 0; c < 1; ++c) {
                for (int j = 0; j < this.gridSizeX; ++j) {
                    out.print(grid[j][i][c]);
                }
                out.print(ls);
            }
        }

        out.flush();
    }

    public PlayerGrid(MapSizeEnum mapSize, NavalBattleProtocol.BONUS_STATE minesEnabled, NavalBattleProtocol.BONUS_STATE satEnabled) {
        this.mapSize = mapSize;
        this.minesEnabled = minesEnabled;
        this.satEnabled = satEnabled;
        this.gridSizeX = this.mapSize.getSize();
        this.gridSizeY = this.gridSizeX;

        if (this.gridSizeX < 0 || this.gridSizeY < 0) {
            throw new IllegalArgumentException();
        }

        this.rng = new Random();
    }

    public PlayerGrid(MapSizeEnum mapSize, NavalBattleProtocol.BONUS_STATE minesEnabled, NavalBattleProtocol.BONUS_STATE satEnabled, long seed) {
        this(mapSize, minesEnabled, satEnabled);
        this.rng = new Random(seed);
    }

    public void positionBoats(ArrayList<Boat> boats) throws BoatsAreOverlapping, BoatIsOutsideGridException {

        ArrayList<BoatPosition> positions = new ArrayList<>(boats.size());

        for (Boat boat : boats) {
            positions.add(boat.getPosition());
        }

        if (BoatUtils.areBoatsOverlapping(new Dimension(gridSizeX, gridSizeY), positions)) {
            throw new BoatsAreOverlapping();
        }

        for (BoatPosition bp : positions) {
            if (bp.getX() < 0 || bp.getX() >= this.gridSizeX
                    || bp.getY() < 0 || bp.getY() >= this.gridSizeY) {
                throw new BoatIsOutsideGridException();
            }
        }

        this.allBoats = boats;
    }

    public ArrayList<CoordinateWithType> fire(int x, int y, PlayerGrid otherPlayerGrid, boolean automaticFire, boolean isRec) throws AlreadyFiredOnThisPositionException {
        if (x < 0 || x >= gridSizeX || y < 0 || y >= this.gridSizeY) {
            throw new IllegalArgumentException();
        }

        //System.out.println("Fire on x=" + x + ", y=" + y + "");
        ArrayList<CoordinateWithType> ret = new ArrayList<>();

        for (HitPosition hp : this.missedFires) {
            if (hp.getX() == x && hp.getY() == y) {
                if (automaticFire) {
                    return ret;
                }

                throw new AlreadyFiredOnThisPositionException();
            }
        }

        NavalBattleProtocol.COORDINATE_TYPE hitOrNot = NavalBattleProtocol.COORDINATE_TYPE.NOTHING;

        for (Boat boat : allBoats) {
            try {
                if (boat.isPositionOnShip(x, y)) {
                    NavalBattleProtocol.HIT_ON_BOAT hit = boat.fire(x, y);

                    switch (hit) {
                        case ALREADY_FIRED_ON_THAT_POSITION:

                            if (automaticFire) {
                                return ret;
                            }

                            throw new AlreadyFiredOnThisPositionException();

                        //break;
                        case HIT:
                            hitOrNot = NavalBattleProtocol.COORDINATE_TYPE.DAMAGED;
                            break;

                        case SINKED:
                            hitOrNot = NavalBattleProtocol.COORDINATE_TYPE.SINKED;
                            break;
                    }

                    // We hit a boat, since they can't be overlapping
                    // this will be the only boat we can damage
                    if (hitOrNot != NavalBattleProtocol.COORDINATE_TYPE.NOTHING) {
                        break;
                    }
                }
            } catch (PositionNotSetException ex) {
            }
        }

        if (hitOrNot == NavalBattleProtocol.COORDINATE_TYPE.NOTHING) {
            this.missedFires.add(new HitPosition(x, y));

            // Check if we hit a bonus
            // TO-DO
            Mine mine = this.isThereAMineOn(x, y);

            if (mine != null) {
                if (!mine.isActive()) {
                    throw new AlreadyFiredOnThisPositionException();
                }

                ret.add(new CoordinateWithType(x, y, NavalBattleProtocol.COORDINATE_TYPE.MINE));

                // There is a mine there
                ArrayList<HitPosition> allDamagesDueToThisMine = mine.fire(x, y, otherPlayerGrid, this);

                // Important: we deactivate the bonus
                mine.setIsActive(false);

                for (HitPosition hp : allDamagesDueToThisMine) {
                    /*
                     ================================================
                     DEACTIVATING SPYSAT BONUS WHICH ARE HIT BY MINES
                     ================================================
                     */
                    SpySat spySat = this.isThereASpySat(hp.getX(), hp.getY());
                    if (spySat != null) {
                        spySat.setIsActive(false);
                    }

                    /*
                     ==============
                     RECURSIVE CALL
                     ==============
                     */
                    ArrayList<CoordinateWithType> newHitPos = new ArrayList(this.fire(hp.getX(), hp.getY(), otherPlayerGrid, true, true));
                    ret.addAll(newHitPos);
                }

                // Only a manual fire can trigger a SpySat bonus
            } else if (!automaticFire) {
                SpySat spySat = this.isThereASpySat(x, y);

                if (spySat != null) {
                    if (!spySat.isActive()) {
                        throw new AlreadyFiredOnThisPositionException();
                    }

                    ret.add(new CoordinateWithType(x, y, NavalBattleProtocol.COORDINATE_TYPE.SATELLITE));

                    // There is a spysat bonus there
                    ArrayList<RevelationsDueToSat> allRevelationsFromThisSatBonus = spySat.fire(x, y, this, otherPlayerGrid);

                    // Important: we deactivate the bonus
                    spySat.setIsActive(false);

                    for (RevelationsDueToSat rev : allRevelationsFromThisSatBonus) {
                        ret.add(new CoordinateWithType(rev.getX(), rev.getY(), (rev.isIsThereAShipThere() ? NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP : NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_NO_SHIP)));
                    }
                }
            }
        }

        boolean isOriginalFirePresent = false;

        for (CoordinateWithType cwt : ret) {
            if (cwt.getX() == x && cwt.getY() == y) {
                isOriginalFirePresent = true;
                break;
            }
        }

        if (!isOriginalFirePresent) {
            ret.add(new CoordinateWithType(x, y, hitOrNot));
        }

        return ret;
    }
}
