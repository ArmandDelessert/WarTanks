/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 03.05.2015
 * 
 * Description :
 * Classe de test de la communication client-serveur.
 */

package clientserver;

import client.Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.messages.InfoClient;
import server.Server;

/**
 *
 * @author Armand Delessert
 */
public class ClientServer {

	private static Thread client1 = null;
	private static Thread client2 = null;
	private static Thread server1 = null;
	private static Thread server2 = null;

	private static final String ip = "localhost";
	private static final int port1 = 1991;
	private static final int port2 = 1992;

	/**
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		// Création du serveur et d'un ou deux client(s) pour les tests
		System.out.println("Test de communication entre le serveur et un ou deux client(s).");
		try {
			server1 = new Thread(new Server(2, port1));
			server1.start();

			client1 = new Thread(new Client(new InfoClient(ip, port1)));
			client1.start();

			client2 = new Thread(new Client (new InfoClient(ip, port1)));
			client2.start();

			client1.join();
			client2.join();
			server1.join();

		} catch (IOException | InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
