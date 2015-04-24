package navalbattle.bonus;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.boats.Boat;
import navalbattle.boats.PositionIsNotOnShipException;
import navalbattle.boats.PositionNotSetException;
import navalbattle.server.PlayerGrid;

public class SpySat extends Bonus {

    // Position which will trigger the bonus
    private final int x;
    private final int y;
    
    private final int percentageMinToReveal; // in percentage
    private final int percentageMaxToReveal;

    public SpySat(int percentageMinToReveal, int percentageMaxToReveal, int x, int y) {
        this.percentageMinToReveal = percentageMinToReveal;
        this.percentageMaxToReveal = percentageMaxToReveal;
        this.x = x;
        this.y = y;
    }

    @Override
    public ArrayList<RevelationsDueToSat> fire(int x, int y, PlayerGrid playerWhoWillSufferDamages, PlayerGrid playerWhoFired) {
        if (x != this.x && y != this.y) {
            return null;
        }
        
        int gridSizeX = playerWhoWillSufferDamages.getGridSizeX();
        int gridSizeY = playerWhoWillSufferDamages.getGridSizeY();
        
        ArrayList<RevelationsDueToSat> revelations = new ArrayList<>();
        Random rng = new Random();
        double percentageToReveal = (((int)(rng.nextDouble() * percentageMaxToReveal + percentageMinToReveal)) - percentageMinToReveal) / 100.0;
        long numberOfPositionsToReveal = Math.round(percentageToReveal * gridSizeX * gridSizeY);
        final int size = playerWhoWillSufferDamages.getGridSizeX();
        final int tries = size * 10;
        // Picking some random points

        ArrayList<Boat> boats = playerWhoWillSufferDamages.getAllBoats();
        
        for (long i = 0; i < numberOfPositionsToReveal ; ++i)
        {
            for (int t = 0; t < tries; ++t)
            {
                int randomX = rng.nextInt(gridSizeX);
                int randomY = rng.nextInt(gridSizeY);
                
                // Cannot be own position
                if (randomX == this.x && randomY == this.y)
                    continue;

                // Cannot be an already revealed position
                for (RevelationsDueToSat rev : revelations)
                {
                    if (rev.getX() == randomX && rev.getY() == randomY)
                        continue;
                }
                
                boolean isThereAShipOnThisPosition = false;
                boolean pickAnotherPosition = false;

                for (Boat boat : boats)
                {
                    try {
                        try {
                            if (boat.isPositionOnShip(randomX, randomY))
                            {
                                if (boat.isDamagedAt(randomX, randomY))
                                {
                                    pickAnotherPosition = true;
                                    continue;
                                }
                                
                                isThereAShipOnThisPosition = true;
                                
                                break;
                            }
                        } catch (PositionIsNotOnShipException ex) {
                        }
                    } catch (PositionNotSetException ex) {
                        // Should never happen
                    }
                }
                
                if (pickAnotherPosition)
                    continue;

                revelations.add(new RevelationsDueToSat(randomX, randomY, isThereAShipOnThisPosition));
                break;
            }
        }
        
        return revelations;
    }
    
}
