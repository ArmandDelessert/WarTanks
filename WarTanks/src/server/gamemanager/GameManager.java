/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 03.05.2015
 * 
 * Description :
 * Gestion du plateau de jeu.
 */

package server.gamemanager;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.network.protocol.AcceptNewClient;
import server.network.protocol.AnnouncesNewClient;

/**
 *
 * @author Armand Delessert
 */
public class GameManager implements Runnable {

	private int nbJoueurs = 2;
	private static boolean serverFinishIsWork = false;

	@Override
	public void run() {
		try {
			gameManager();
		} catch (InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws InterruptedException 
	 */
	public static void gameManager() throws InterruptedException {

// ========== Architecture du GameManager ========== //

/**
 * Initialisation du serveur
 */

		String ipAddress = "localhost";
		int numPortTCP = 54555;
		int numPortUDP = 54777;

		/**
		 * Création d'un serveur
		 */

		com.esotericsoftware.kryonet.Server serverKryoNet = new com.esotericsoftware.kryonet.Server();

		// Enregistrement des classes transmises pour le serveur
		serverKryoNet.getKryo().register(AnnouncesNewClient.class);
		serverKryoNet.getKryo().register(AcceptNewClient.class);

		/**
		 * Démarrage du serveur
		 */

		// DEBUG
		System.out.println("Démarrage du serveur");

		serverKryoNet.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				while (!serverFinishIsWork) {
					if (object instanceof AnnouncesNewClient) {

						// DEBUG
						System.out.println("Le serveur a reçu une requête");

						// Affichage de la requête reçue
						AnnouncesNewClient request = (AnnouncesNewClient)object;
						System.out.println("[" + "Server" + "]: " + "A new client is connected:");
						System.out.println("Client name: " + request.clientName);
						System.out.println("Client IP address: " + request.clientIPAddress);

						// DEBUG
						System.out.println("Le serveur envoi sa réponse");

						// Envoie d'une réponse au client
						AcceptNewClient response = new AcceptNewClient(true);
						System.out.println("[" + "Server" + "]: " + "I responds to the client that I accept his request.");
						connection.sendTCP(response);

						// Fin du serveur
						serverFinishIsWork = true;
					}
				}
			}
		});

		try {
			serverKryoNet.bind(numPortTCP, numPortUDP);
		} catch (IOException ex) {
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		serverKryoNet.start();

		/**
		 * Fermeture du serveur et du client
		 */

		// DEBUG
		System.out.println("Fermeture du serveur");

		serverKryoNet.close();
		serverKryoNet.stop();

/**
 * Attente des clients
 */

		

/**
 * Démarrage de la partie
 */

		

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
