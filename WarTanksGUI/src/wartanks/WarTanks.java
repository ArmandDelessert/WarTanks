/**
 * Projet : WarTanks Auteur : Armand Delessert Date : 21.03.2015
 */
package wartanks;

import Slick2d.Game;
import client.ConnectionHandler;
import java.io.IOException;
import java.net.InetAddress;
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
        int nbPlayer = 10;
        int nbBponus = 10;
        int temps = 30;
        
        
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        short port = 1991;

        ConnectionHandler conn = new ConnectionHandler(addr, port);
        conn.start();
        
        AppGameContainer app = new AppGameContainer(new Game(nbPlayer,nbBponus,temps, conn), 800, 600, false);
        app.setShowFPS(false);
        app.start();
    }
}
