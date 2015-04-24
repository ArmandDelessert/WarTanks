package navalbattle.client;

import java.awt.Dimension;
import java.util.ArrayList;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.boats.BoatPosition;
import navalbattle.boats.BoatUtils;
import navalbattle.boats.PositionNotSetException;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.server.BoatsAreOverlapping;

public class ClientGrid {

    private final MapSizeEnum mapSize;
    private ArrayList<Boat> allBoats = null;
    private NavalBattleProtocol.COORDINATE_TYPE[][] grid = null;
    private NavalBattleProtocol.COORDINATE_TYPE[][] previousGrid = null;

    // TO-DO: return a copy
    public NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawing() {
        return this.grid;
    }

    public NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawinPreviousGrid() {
        return this.previousGrid;
    }

    public ClientGrid(MapSizeEnum mapSize) {
        this.mapSize = mapSize;

        int cells = this.mapSize.getSize();
        grid = new NavalBattleProtocol.COORDINATE_TYPE[cells][cells];

        for (int i = 0; i < cells; ++i) {
            for (int j = 0; j < cells; ++j) {
                grid[i][j] = NavalBattleProtocol.COORDINATE_TYPE.NOTHING;
            }
        }
    }

    public void receivedFireFromOpponentSimpleDesign(ArrayList<CoordinateWithType> allCoordinates) {
        previousGrid = new NavalBattleProtocol.COORDINATE_TYPE[grid.length][grid.length];

        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[i].length; ++j) {
                previousGrid[i][j] = grid[i][j];
            }
        }

        for (CoordinateWithType cwt : allCoordinates) {
            NavalBattleProtocol.COORDINATE_TYPE t = cwt.getType();

            switch (cwt.getType()) {
                case NOTHING:
                    // Your opponent (or a mine) fired here but there was no effect at all
                    t = NavalBattleProtocol.COORDINATE_TYPE.FIRED_BUT_DID_NOT_DAMAGE_ANYTHING;
                    break;
            }

            this.grid[cwt.getX()][cwt.getY()] = t;
        }
    }

    public void receivedFireFromOpponent(ArrayList<CoordinateWithType> allCoordinates) throws InconsistentGameStateBetweenServerAndClientException, BoatNotPlacedException {

        previousGrid = new NavalBattleProtocol.COORDINATE_TYPE[grid.length][grid.length];

        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[i].length; ++j) {
                previousGrid[i][j] = grid[i][j];
            }
        }
        
        if (allBoats == null) {
            throw new BoatNotPlacedException();
        }

        for (CoordinateWithType cwt : allCoordinates) {
            int x = cwt.getX();
            int y = cwt.getY();

            switch (cwt.getType()) {
                case DAMAGED:
                case SINKED:
                    try {
                        for (Boat boat : this.allBoats) {
                            if (boat.isPositionOnShip(x, y)) {

                                NavalBattleProtocol.HIT_ON_BOAT hitResult = boat.fire(x, y);

                                if (cwt.getType() == NavalBattleProtocol.COORDINATE_TYPE.DAMAGED) {
                                    if (hitResult != NavalBattleProtocol.HIT_ON_BOAT.HIT) {
                                        throw new InconsistentGameStateBetweenServerAndClientException();
                                    }

                                    this.grid[x][y] = NavalBattleProtocol.COORDINATE_TYPE.DAMAGED;

                                } else if (cwt.getType() == NavalBattleProtocol.COORDINATE_TYPE.SINKED) {

                                    if (hitResult != NavalBattleProtocol.HIT_ON_BOAT.SINKED) {
                                        throw new InconsistentGameStateBetweenServerAndClientException();
                                    }

                                    BoatPosition position = boat.getPosition();
                                    int boatLength = position.getLength();
                                    int posX = position.getX();
                                    int posY = position.getY();

                                    switch (boat.getPosition().getOrientation()) {
                                        case HORIZONTAL:
                                            for (int i = 0; i < boatLength; ++i) {
                                                grid[posX + i][posY] = NavalBattleProtocol.COORDINATE_TYPE.SINKED;
                                            }
                                            break;

                                        case VERTICAL:
                                            for (int i = 0; i < boatLength; ++i) {
                                                grid[posX][posY + i] = NavalBattleProtocol.COORDINATE_TYPE.SINKED;
                                            }
                                            break;
                                    }
                                }
                            }

                        }
                    } catch (PositionNotSetException ex) {
                        // will never happen (game has started)
                    }
                    break;

                case SATELLITE:
                    grid[x][y] = NavalBattleProtocol.COORDINATE_TYPE.SATELLITE;
                    break;

                case MINE:
                    grid[x][y] = NavalBattleProtocol.COORDINATE_TYPE.MINE;
                    break;

                case NOTHING:
                    // Your opponent (or a mine) fired here but there was no effect at all
                    grid[x][y] = NavalBattleProtocol.COORDINATE_TYPE.FIRED_BUT_DID_NOT_DAMAGE_ANYTHING;

                    break;

                case REAVEALED_HAS_SHIP:
                    grid[x][y] = NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP;
                    break;

                case REAVEALED_HAS_NO_SHIP:
                    grid[x][y] = NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_NO_SHIP;
                    break;
            }
        }
    }

    public void positionBoats(ArrayList<Boat> boats) throws BoatsAreOverlapping, BoatIsOutsideGridException {

        ArrayList<BoatPosition> positions = new ArrayList<>(boats.size());

        for (Boat boat : boats) {
            positions.add(boat.getPosition());
        }

        int cells = this.mapSize.getSize();
        if (BoatUtils.areBoatsOverlapping(new Dimension(cells, cells), positions)) {
            throw new BoatsAreOverlapping();
        }

        for (BoatPosition bp : positions) {
            if (bp.getX() < 0 || bp.getX() >= cells
                    || bp.getY() < 0 || bp.getY() >= cells) {
                throw new BoatIsOutsideGridException();
            }
        }

        for (BoatPosition bp : positions) {

            int boatLength = bp.getLength();
            int posX = bp.getX();
            int posY = bp.getY();

            switch (bp.getOrientation()) {
                case HORIZONTAL:
                    for (int i = 0; i < boatLength; ++i) {
                        grid[posX + i][posY] = NavalBattleProtocol.COORDINATE_TYPE.BOAT;
                    }
                    break;

                case VERTICAL:
                    for (int i = 0; i < boatLength; ++i) {
                        grid[posX][posY + i] = NavalBattleProtocol.COORDINATE_TYPE.BOAT;
                    }
                    break;
            }
        }

        this.allBoats = boats;
    }

    public MapSizeEnum getMapSize() {
        return this.mapSize;
    }
}
