/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Client de test de la communication client-serveur.
 */

package client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.CommunicationProtocol;
import protocol.messages.PlayerCommand;
import protocol.messages.InfoClient;
import protocol.messages.InfoPlayer;

/**
 * Classe Client
 * 
 * @author Armand Delessert
 */
public class Client implements Runnable {

	private CommunicationProtocol communicationProtocol;

	InfoClient infoClient;
	InfoPlayer infoPlayer;

	/**
	 *
	 * @param infoClient
	 * @throws IOException
	 */
	public Client(InfoClient infoClient) throws IOException {

		this.infoClient = infoClient;

		// Création du socket de connexion au serveur
		try {
			this.communicationProtocol = new CommunicationProtocol(new Socket(infoClient.ipAddress, infoClient.portNumber));
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à Client.Client() lors de la création de CommunicationProtocol.");
		}

		// Hello from client
		System.out.println("[" + this.getClass() + "]: " + "Hello from <" + this.getClass() + ">!");
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
			case "OKsr":
				System.out.println("[" + this.getClass() + "]: " + "Réponse du serveur : OKsr");
			case "OK":
				System.out.println("[" + this.getClass() + "]: " + "Connecté au serveur");
				this.infoPlayer = this.communicationProtocol.receiveInfoPlayer();
				System.out.println("[" + this.getClass() + "]: " + "infoPlayer : " + infoPlayer);
				break;
			case "Refused":
				System.out.println("ERREUR : Connexion au serveur refusée.");
				break;
			default:
				System.out.println("[" + this.getClass() + "]: " + "Le serveur a répondu : " + confirmation);
				System.out.println("ERREUR : La réponse du serveur est invalide.");
				break;
		}

		// Boucle principale pour la communication avec le serveur
		while (connectedToTheServer) {

			// Paramétrage de la partie
			

			// Attente du début de la partie
//		while (this.communicationProtocol.receiveStringMessage() != "Start"); // Ca marche pas !
			System.out.println("[" + this.getClass() + "]: " + "Le serveur a envoyé : " + this.communicationProtocol.receiveStringMessage());

			// Envoie des commandes au serveur
			PlayerCommand command = new PlayerCommand();
			for (int i = 0; i < 4; i ++) {
				command.newCommand(PlayerCommand.CommandType.MOVEMENT, PlayerCommand.Command_Movement.RIGHT);
				this.communicationProtocol.sendPlayerCommand(command);
			}

			// Fin de la boucle principale pour la communication avec le serveur
			connectedToTheServer = false;
		}

/*
		// Tests de transfert de messages
//	String message;
		Message receivedMessage = new Message("Armand Delessert", "Ça marche pas !");
		Message sendedMessage = new Message("Armand Delessert", "Hello World from <" + this.getClass() + ">!");

		// Envoie d'un message au serveur
		System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the server.");
//	sendStringMessage("Hello World from <" + this.getClass() + ">!");
		sendMessage(sendedMessage);

		// Réception de la réponse du serveur
//	message = receiveStringMessage();
		receivedMessage = receiveMessage();

		// Affichage du message reçu
		System.out.println("[" + this.getClass() + "]: " + "I receive this message from the server:");
//	System.out.println(message);
		receivedMessage.afficher();
*/

		// Fin du client
		this.communicationProtocol.close(); // Fermeture des connexions

		System.out.println("[" + this.getClass() + "]: " + "I have finished my work.");
	}
}
