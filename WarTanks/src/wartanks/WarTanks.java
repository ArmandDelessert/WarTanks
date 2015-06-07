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

		Thread client, gameManager;
		String ipAddress = "localhost";
		int portNumber = 1991;

		// Lancement du serveur
		gameManager = new Thread(new GameManager());
		gameManager.start();

		// Création d'un client pour les tests
		System.out.println("Création d'un client pour tester la communication.");
		try {
			client = new Thread(new Client(new InfoClient(ipAddress, portNumber)));
			client.start();
			client.join();

		} catch (IOException | InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(WarTanks.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
