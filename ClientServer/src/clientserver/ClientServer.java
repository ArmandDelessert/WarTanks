/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 03.05.2015
 * 
 * Description :
 * Classe de test de la communication client-serveur.
 */

package clientserver;

import network.client.Client;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.messages.InfoClient;
import network.server.ClientHandler;
import network.server.ClientListener;

/**
 *
 * @author Armand Delessert
 */
public class ClientServer {

	private static final String ip = "localhost";
	private static final int port = 1991;

	/**
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		final int nbJoueurs = 2;

		Thread server;
		final List<Client> clientHandlerList = new LinkedList<>();
		final List<Thread> threadList = new LinkedList<>();

		ClientListener clientListener;

		System.out.println("Projet ClientServer - Test de la communication client-serveur.");

		// Création du serveur et d'un ou deux client(s) pour les tests
		System.out.println("Test de communication entre le serveur et un ou deux client(s).");
		try {
			// Création et lancement du serveur
			System.out.println("Création et lancement du serveur.");
			clientListener = new ClientListener(nbJoueurs, port);
			server = new Thread(clientListener);
			server.start();

			// Création des clients
			System.out.println("Création des clients.");
			for (int i = 0; i < nbJoueurs; i ++) {
				clientHandlerList.add(new Client(new InfoClient(ip, port)));
				threadList.add(new Thread(clientHandlerList.get(i)));
				threadList.get(i).start();
			}

			// Attente du démarrage des ClientHandler
			System.out.println("Attente que tous les ClientHandler soient démarrés.");
			while (!clientListener.allClientHandlerReady) {
				System.out.println("Tous les ClientHandler ne sont pas encore prêt...");
				sleep(200); // Attente de 0.2 seconde
			}
			System.out.println("Tous les ClientHandler sont prêt.");

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

			// Synchronisation des clients pour le lancement de la partie
			System.out.println("Envoi du signal de synchronisation des clients pour le lancement de la partie.");
			synchronized(clientListener.start) {
				clientListener.start.notifyAll();
			}

			// Arrêt des clients
			System.out.println("Attente de la fin des clients.");
			for (int i = 0; i < nbJoueurs; i ++) {
				threadList.get(i).join();
			}

			// Arrêt du serveur
			System.out.println("Arrêt du serveur.");
			server.join();

		} catch (IOException | InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println("Fin du test de la communication client-serveur.");
	}
}
