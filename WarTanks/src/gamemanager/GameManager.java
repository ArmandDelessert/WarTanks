/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 03.05.2015
 * 
 * Description :
 * Gestion du plateau de jeu.
 */

package gamemanager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.server.ClientListener;

/**
 *
 * @author Armand Delessert
 */
public class GameManager implements Runnable {

	private int nbJoueurs = 2;
	private boolean running = true;

	@Override
	public void run() {
		try {
			gameManager();
		} catch (InterruptedException | IOException ex) {
			System.out.println(ex);
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws java.io.IOException
	 * @throws InterruptedException 
	 */
	public void gameManager() throws IOException, InterruptedException {

		Thread networkServer;

		String ipAddress = "localhost";
		int numeroPort = 1991;

		/**
		 * Création et démarrage du serveur pour la communication avec les clients
		 */
		networkServer = new Thread(new ClientListener(2, numeroPort));
		networkServer.start();

		/**
		 * Démarrage de la partie
		 */
		while (running) {

			

			/**
			 * Fin de la partie
			 */
			running = false;
		}

		/**
		 * Fin du serveur
		 */
		networkServer.join(); // Fermeture du serveur


/*
	Fonctionnement du GameManager :
Déroulement d'un rafraîchissement du jeu :
1. Recevoir les commandes des joueurs.
	Un thread par client doit rester à l'écoute du client (clientHandler) pour recevoir les commandes et les enregistrer dans un buffer.
2. Vérifier la validité des commandes.
	Vérifier pour chaque client si les commandes reçues sont valides.
3. Appliquer les commandes sur le jeu.
4. Vérifier les conditions de victoire et défaite (joueurs détruits, etc.).
5. Envoyer le nouveau plateau de jeu aux clients.
	Il ne devrait pas y avoir besoin d'un thread pour cette tâche.
*/

		// Récupération des commandes des joueurs
		/*
		Stocker les commandes de chacun des clients dans un buffer qu'on viendra lire en début de boucle
		*/

		// Vérifier la validité des commandes
		// Appliquer les commandes sur le jeu
		// Vérifier les conditions de victoire et défaite

		// Envoyer le nouveau plateau de jeu aux clients
		

	}
}
