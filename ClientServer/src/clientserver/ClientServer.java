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
import serveur.Server;

/**
 *
 * @author Armand Delessert
 */
public class ClientServer {

	private static Thread client;
	private static Thread server;

	private static final String ip = "localhost";
	private static final int port = 1991;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		// Création du serveur et d'un client pour les tests
		try {
			server = new Thread(new Server(port));
			server.start();

			client = new Thread(new Client(ip, port));
			client.start();

		} catch (IOException e) { System.out.println(e); }
	}
}
