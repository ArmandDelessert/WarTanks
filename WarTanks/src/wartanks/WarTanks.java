/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 * 
 * Description :
 * Test du jeu.
 */

package wartanks;

import java.util.logging.Level;
import java.util.logging.Logger;
import gamemanager.GameManager;
import java.io.IOException;
import network.client.Client;
import network.protocol.messages.InfoClient;

/**
 *
 * @author Armand
 */
public class WarTanks {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		GameManager gameManager;
		Thread gameManagerThread, clientThread = null;
		String ipAddress = "localhost";
		int portNumber = 1991;

		System.out.println("Projet WarTanks - Test du serveur.");

		// Lancement du GameManager (serveur du jeu)
		gameManager = new GameManager();
		gameManagerThread = new Thread(gameManager);
		gameManagerThread.start();

		// Création d'un ou plusieurs client(s) pour les tests
		for (int i = 0; i < gameManager.nbJoueurs; i ++) {
			System.out.println("[" + WarTanks.class + "]: " + "Création du client" + (i+1) + " pour tester la communication.");
			try {
				clientThread = new Thread(new Client(new InfoClient(ipAddress, portNumber)));
				clientThread.start();
			} catch (IOException ex) {
				System.out.println(ex);
				Logger.getLogger(WarTanks.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		// Démarrage du ou des client(s)
		for (int i = 0; i < gameManager.nbJoueurs; i ++) {
			try {
				clientThread.join();
			} catch (InterruptedException ex) {
				System.out.println(ex);
				Logger.getLogger(WarTanks.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
