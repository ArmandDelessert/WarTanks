package navalbattle.boats;

import java.awt.Dimension;
import java.util.ArrayList;
import navalbattle.protocol.common.NavalBattleProtocol;

public class BoatUtils {

    public static boolean areBoatsOverlapping(Dimension gridSize, ArrayList<BoatPosition> boats) throws BoatIsOutsideGridException {
        if (gridSize.width < 0 || gridSize.height < 0) {
            throw new IllegalArgumentException();
        }

        boolean grid[][] = new boolean[gridSize.width][gridSize.height];

        for (int i = 0; i < gridSize.width; ++i) {
            for (int j = 0; j < gridSize.height; ++j) {
                grid[i][j] = false;
            }
        }

        for (BoatPosition position : boats) {
            int posX = position.getX();
            int posY = position.getY();
            int length = position.getLength();
            NavalBattleProtocol.BOAT_ORIENTATION orientation = position.getOrientation();

            if (posX < 0 || posX >= gridSize.width) {
                throw new BoatIsOutsideGridException();
            }

            if (posY < 0 || posY >= gridSize.height) {
                throw new BoatIsOutsideGridException();
            }

            if (orientation == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) {
                if (posX + length > gridSize.width) {
                    throw new BoatIsOutsideGridException();
                }

                for (int i = 0; i < length; ++i) {
                    if (grid[posX + i][posY]) {
                        return true;
                    }

                    grid[posX + i][posY] = true;
                }
            } else {
                if (posY + length > gridSize.height) {
                    throw new BoatIsOutsideGridException();
                }

                for (int i = 0; i < length; ++i) {
                    if (grid[posX][posY + i]) {
                        return true;
                    }

                    grid[posX][posY + i] = true;
                }
            }
        }

        return false;
    }
}
