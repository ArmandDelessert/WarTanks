/**
 * Programme de test de la librairie KryoNet pour la gestion du réseau.
 * 
 * Plus d'infos disponible sur le repo GitHub de KryoNet :
 * https://github.com/EsotericSoftware/kryonet
 */

package kryonet_test;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Armand Delessert
 */
public class KryoNet_test {

	private static boolean serverFinishIsWork = false;
	private static boolean clientFinishIsWork = false;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		String ipAddress = "localhost";
		int numPortTCP = 54555;
		int numPortUDP = 54777;

		/**
		 * Création d'un serveur
		 */

		Server server = new Server();
    server.start();
		try {
			server.bind(numPortTCP, numPortUDP);
		} catch (IOException ex) {
			Logger.getLogger(KryoNet_test.class.getName()).log(Level.SEVERE, null, ex);
		}

		// Enregistrement des classes transmises pour le serveur
		server.getKryo().register(SomeRequest.class);
		server.getKryo().register(SomeResponse.class);

		/**
		 * Lancement du serveur
		 */

		server.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof SomeRequest) {
					// Affichage de la requête reçue
					SomeRequest request = (SomeRequest)object;
					System.out.println("[" + "Server" + "]: " + "I receive this request from the client:");
					System.out.println(request.text);

					// Envoie d'une réponse au client
					SomeResponse response = new SomeResponse();
					response.text = "Here are some peanuts.";
					System.out.println("[" + "Server" + "]: " + "I send a reponse to the client.");
					connection.sendTCP(response);

					// Fin du serveur
					serverFinishIsWork = true;
				}
			}
		});

		/**
		 * Création d'un client
		 */

		Client client = new Client();
    client.start();
		try {
			client.connect(5000, ipAddress, numPortTCP, numPortUDP);
		} catch (IOException ex) {
			Logger.getLogger(KryoNet_test.class.getName()).log(Level.SEVERE, null, ex);
		}

		// Enregistrement des classes transmises pour le client
		client.getKryo().register(SomeRequest.class);
		client.getKryo().register(SomeResponse.class);

		/**
		 * Démarrage du client
		 */

		// Envoie d'une requête au serveur
    SomeRequest request = new SomeRequest();
    request.text = "I want peanuts!";
		System.out.println("[" + "Client" + "]: " + "I send a request to the server.");
    client.sendTCP(request);

		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof SomeResponse) {
					// Affichage de la réponse reçue
					SomeResponse response = (SomeResponse)object;
					System.out.println("[" + "Client" + "]: " + "I receive this reponse from the server:");
					System.out.println(response.text);

					// Fin du client
					clientFinishIsWork = true;
				}
			}
    });

		/**
		 * Fermeture du serveur et du client
		 */
		while (!(serverFinishIsWork && clientFinishIsWork));
		server.close();
		client.close();
		server.stop();
		client.stop();
	}
}
