/**
 * Projet : WarTanks Auteur : Armand Delessert Date : 21.03.2015
 */
package wartanks;

import Slick2d.Game;
import java.io.IOException;
import launcher.LauncherFrame;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Armand
 */
public class WarTanks {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, SlickException{
        LauncherFrame laucher = new LauncherFrame();
        int nbPlayer = 1;
        int nbBponus = 10;
        int temps = 30;
        AppGameContainer app = new AppGameContainer(new Game(nbPlayer,nbBponus,temps), 800, 600, false);
        app.setShowFPS(false);
        app.start();
    }
}
