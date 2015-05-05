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
import client.ClientTestSerialization;
import java.io.IOException;
import serveur.Server;
import serveur.ServerTestSerialization;

/**
 *
 * @author Armand Delessert
 */
public class ClientServer {

	private static Thread client1;
	private static Thread client2;
	private static Thread server1;
	private static Thread server2;

	private static final String ip = "localhost";
	private static final int port1 = 1991;
	private static final int port2 = 1992;

	/**
	 * @param args the command line arguments
	 * @throws java.lang.InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		// Création du serveur et d'un client pour les tests
		try {
			server1 = new Thread(new Server(port1));
			server1.start();

			client1 = new Thread(new Client(ip, port1));
			client1.start();

			client1.join();
			server1.join();

		} catch (IOException e) { System.out.println(e); }

/*
		// Création de 2 clients et de 2 serveurs
		// Test de 2 connexions en parallèle
		try {
			server1 = new Thread(new Server(port1));
			server1.start();

			client1 = new Thread(new Client(ip, port1));
			client1.start();

			server2 = new Thread(new Server(port2));
			server2.start();

			client2 = new Thread(new Client(ip, port2));
			client2.start();

		} catch (IOException e) { System.out.println(e); }
*/
		// Création du serveur et d'un client pour les tests
		try {
			server1 = new Thread(new ServerTestSerialization(port1));
			server1.start();

			client1 = new Thread(new ClientTestSerialization(ip, port1));
			client1.start();

			client1.join();
			server1.join();

		} catch (IOException e) { System.out.println(e); }
	}
}
