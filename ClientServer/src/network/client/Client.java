/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Client de test de la communication client-serveur.
 */

package network.client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.CommunicationProtocol;
import network.protocol.messages.InfoClient;
import network.protocol.messages.InfoPlayer;
import network.protocol.messages.Movement;
import network.protocol.messages.StateGame;

/**
 * Classe Client
 * 
 * @author Armand Delessert
 */
public class Client implements Runnable {

	public static int cptId = 0;
	public final int id;

	private CommunicationProtocol communicationProtocol;

	InfoClient infoClient;
	InfoPlayer infoPlayer;

	/**
	 *
	 * @param infoClient
	 * @throws IOException
	 */
	public Client(InfoClient infoClient) throws IOException {

		this.id = Client.cptId ++; // Définition de l'id du client

		this.infoClient = infoClient;

		// Création du socket de connexion au serveur
		try {
			this.communicationProtocol = new CommunicationProtocol(new Socket(infoClient.ipAddress, infoClient.portNumber));
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à Client[" + this.id + "].Client() lors de la création de CommunicationProtocol.");
		}

		// Hello from client
		System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Hello from <" + this.getClass() + ">!");
	}

	/**
	 * 
	 */
	@Override
	public void run() {

		try {
			client();
		} catch (IOException ex) {
			System.out.println(ex);
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws IOException 
	 */
	private void client() throws IOException {

		boolean connectedToTheServer = true;

		// Le client s'annonce au serveur
		this.communicationProtocol.sendInfoClient(this.infoClient);

		// Le client attend la confirmation du serveur
		String confirmation = this.communicationProtocol.receiveStringMessage();
		switch (confirmation) {
//			case "OKsr":
//				System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Réponse du serveur : OKsr");
			case "OK":
				System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Connecté au serveur");
				this.infoPlayer = this.communicationProtocol.receiveInfoPlayer();
				System.out.println("[" + this.getClass() + " " + this.id + "]: " + "infoPlayer : " + infoPlayer);
				break;
			case "Refused":
				System.out.println("ERREUR : Connexion au serveur refusée.");
				break;
			default:
				System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Le serveur a répondu : " + confirmation);
				System.out.println("ERREUR : La réponse du serveur est invalide.");
				break;
		}

		// Boucle principale pour la communication avec le serveur
		while (connectedToTheServer) {

			// Paramétrage de la partie
			

			// Attente du début de la partie
			while (!this.communicationProtocol.receiveStringMessage().equals("Start"));

			// Boucle principale pour la communication pendant la partie
			Movement command = new Movement(0);
			StateGame stateGame;
			for (int i = 0; i < 4; i ++) {
				// Envoie des commandes au serveur
				this.communicationProtocol.sendCommand(command);

				// Réceptionde la mise à jour de la map
				stateGame = this.communicationProtocol.receiveStateGame();
				System.out.println("[" + this.getClass() + " " + this.id + "]: " + "StateGame reçu : " + stateGame);
			}

			// Fin de la boucle principale pour la communication avec le serveur
			connectedToTheServer = false;
		}

		// Fin du client
		this.communicationProtocol.close(); // Fermeture des connexions

		System.out.println("[" + this.getClass() + " " + this.id + "]: " + "I have finished my work. Goodbye!");
	}
}
