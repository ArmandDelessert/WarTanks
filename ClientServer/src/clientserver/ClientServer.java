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
import protocol.InfoClient;
import serveur.Server;

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
	 * @param args the command line arguments
	 * @throws java.lang.InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		// Création du serveur et d'un client pour les tests
		System.out.println("Test 1 : Création du serveur et d'un client pour une communication.");
		try {
			server1 = new Thread(new Server(port1));
			server1.start();

			client1 = new Thread(new Client(new InfoClient(1, ip, port1)));
			client1.start();

			client1.join();
			server1.join();

		} catch (IOException e) { System.out.println(e); }

/*
		// Création de 2 clients et d'un serveur
		// Test de 2 connexions en parallèle
		System.out.println("Test 2 : Création du serveur et de 2 clients pour une communication.");
		try {
			server1 = new Thread(new Server(port1));
			server1.start();

			client1 = new Thread(new Client(ip, port1));
			client1.start();

			client2 = new Thread(new Client(ip, port1));
			client2.start();

			client1.join();
			client2.join();
			server1.join();

		} catch (IOException e) { System.out.println(e); }
*/
	}
}
