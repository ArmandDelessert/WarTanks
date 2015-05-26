/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 12.05.2015
 * 
 * Description :
 * Client pour l'envoi des commandes.
 */

package client_test.network;

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
public class Client_test implements Runnable {

	static String clientIPAddress = "localhost";
	static String serverIPAddress = "localhost";
	static int clientNumPortTCP = 54555;
	static int clientNumPortUDP = 54777;
	static int serverNumPortTCP = 54555;
	static int serverNumPortUDP = 54777;

	private static boolean clientFinishIsWork = false;

	public static void client_test() {

		/**
		 * Création d'un client
		 */

		com.esotericsoftware.kryonet.Client clientKryoNet = new com.esotericsoftware.kryonet.Client();

		// Enregistrement des classes transmises pour le client
		clientKryoNet.getKryo().register(AnnouncesNewClient.class);
		clientKryoNet.getKryo().register(AcceptNewClient.class);

		/**
		 * Démarrage du client
		 */

		// DEBUG
		System.out.println("Démarrage du client et envoi d'une requête");

		// Envoie d'une requête au serveur
		AnnouncesNewClient request = new AnnouncesNewClient(clientIPAddress, clientNumPortTCP, clientNumPortUDP);
		System.out.println("[" + "Client" + "]: " + "I send a \"AnnouncesNewClient\" request to the server.");
		clientKryoNet.sendTCP(request);

		clientKryoNet.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof AcceptNewClient) {

					// DEBUG
					System.out.println("Le client a reçu une réponse");

					// Affichage de la réponse reçue
					AcceptNewClient response = (AcceptNewClient)object;
					if (response.clientAccepted) {
						System.out.println("[" + "Client" + "]: " + "The server tells me that I am accepted!");
					}
					else {
						System.out.println("[" + "Client" + "]: " + "The server tells me that I am refused!");
					}

					// Fin du client
					clientFinishIsWork = true;
				}
			}
		});

		try {
			clientKryoNet.connect(5000, serverIPAddress, serverNumPortTCP, serverNumPortUDP);
		} catch (IOException ex) {
			Logger.getLogger(Client_test.class.getName()).log(Level.SEVERE, null, ex);
		}
		clientKryoNet.start();

		/**
		 * Fermeture du serveur et du client
		 */

		// DEBUG
		System.out.println("Fermeture du client");

		while (!clientFinishIsWork);
		clientKryoNet.close();
		clientKryoNet.stop();
	}

	@Override
	public void run() {
		client_test();
	}
}
