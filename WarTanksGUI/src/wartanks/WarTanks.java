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
    public static void main(String[] args) throws InterruptedException, IOException, SlickException {
        LauncherFrame laucher = new LauncherFrame();
        AppGameContainer app = new AppGameContainer(new Game(), 800, 600, false);
        app.setShowFPS(false);
        app.start();
    }
}
