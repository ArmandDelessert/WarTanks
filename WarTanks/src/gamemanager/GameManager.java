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
import static java.lang.Thread.sleep;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.messages.StateGame;
import network.server.ClientHandler;
import network.server.ClientListener;

/**
 *
 * @author Armand Delessert
 */
public class GameManager implements Runnable {

	public final int nbJoueurs = 2;
	private boolean running = true;

	private final StateGame stateGame = new StateGame();

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

		ClientListener clientListener;
		Thread clientListenerThread;

		String ipAddress = "localhost";
		int portNumber = 1991;

		/**
		 * Création et démarrage du serveur pour la communication avec les clients
		 */
		System.out.println("[" + this.getClass() + "]: " + "Démarrage du test avec " + nbJoueurs + " client(s).");
		clientListener = new ClientListener(nbJoueurs, portNumber);
		clientListenerThread = new Thread(clientListener);
		clientListenerThread.start();

		/**
		 * Démarrage de la partie
		 */
		// Attente du démarrage des ClientHandler
		System.out.println("[" + this.getClass() + "]: " + "Attente que tous les ClientHandler soient démarrés.");
		while (!clientListener.allClientHandlerReady) {
			System.out.println("[" + this.getClass() + "]: " + "Tous les ClientHandler ne sont pas encore prêt...");
			sleep(200); // Attente de 0.2 seconde
		}
		System.out.println("[" + this.getClass() + "]: " + "Tous les ClientHandler sont prêt.");

		// Attente de l'initialisation des ClientHandler
		boolean readyToStart = false;
		while (!readyToStart) {
			readyToStart = true;
			for (Object i : clientListener.clientHandlerList) {
				if (!((ClientHandler)i).readyToStart) {
					readyToStart = false;
					break;
				}
			}
		}

		System.out.println("[" + this.getClass() + "]: " + "Avant le start.notifyAll()");
		synchronized(clientListener.start) {
			clientListener.start.notifyAll();
		}
		System.out.println("[" + this.getClass() + "]: " + "Après le start.notifyAll()");
		

		// Boucle principale du déroulement de la partie
		while (running) {

			/*
				À faire dans cette boucle :
				 - Vérifier la validité des commandes
				 - Appliquer les commandes sur le jeu
				 - Vérifier les conditions de victoire et défaite
			*/

			// Récupération des commandes des clients
			

			// Mise à jour de la carte
			this.stateGame.lastUpdate = new Date(System.currentTimeMillis());
//		this.stateGame.lastUpdate.setTime(System.currentTimeMillis());

			// Envoi de la mise à jour de l'état de la carte à tous les ClientHandler
			for (Object i : clientListener.clientHandlerList) {
				System.out.println("[" + this.getClass() + "]: " + "this.stateGame : " + this.stateGame);
				((ClientHandler)i).setStateGame(stateGame);
			}

			/**
			 * Fin de la partie
			 */
			running = false;
		}

		/**
		 * Fin du serveur
		 */
		clientListenerThread.join(); // Fermeture du serveur


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
	}
}
