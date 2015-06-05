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
		String ip = "localhost";
		int numeroPort = 1991;

		// Lancement du serveur
		gameManager = new Thread(new GameManager());
		gameManager.start();

		// Création d'un client pour les tests
		System.out.println("Création d'un client pour tester la communication.");
		try {
			client = new Thread(new Client_test());
			client.start();
			client.join();

//		} catch (IOException e) {
//			System.out.println(e);
		} catch (InterruptedException ex) {
			Logger.getLogger(WarTanks.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
