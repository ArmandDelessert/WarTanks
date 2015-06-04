/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Serveur de test de la communication client-serveur.
 */

package serveur;

import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.CommunicationProtocol;
import protocol.messages.InfoClient;
import protocol.messages.InfoPlayer;
import protocol.messages.InfoPlayer.ColorPlayer;
import protocol.messages.PlayerCommand;

/**
 * Classe Server
 * 
 * @author Armand Delessert
 */
public class Server implements Runnable, Disposable {

	public static int nbClient = 0;

	public boolean running = true;
	private int nbActualClientHandler;
	private int nbTotalClientHandler;
	private int nbMaxClientHandler;
	private Vector clientHandlerList;

	private ServerSocket socket;

	/**
	 * 
	 * @param nbMaxClient
	 * @param numeroPort
	 * @throws IOException 
	 */
	public Server(int nbMaxClient, int numeroPort) throws IOException {

		nbActualClientHandler = 0;
		nbTotalClientHandler = 0;
		nbMaxClientHandler = nbMaxClient;
		clientHandlerList = new Vector();

		// Création du socket d'écoute des clients
		try {
			socket = new ServerSocket(numeroPort);
		}
		catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à Server.Server() lors de la création du socket.");
		}

		// Hello from server
		System.out.println("[" + this.getClass() + "]: " + "Hello from <" + this.getClass() + ">!");
	}

	/**
	 * 
	 */
	@Override
	public void run() {

		try {
			clientWaiting();
		} catch (IOException ex) {
			System.out.println(ex);
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws IOException 
	 */
	private void clientWaiting() throws IOException {

		ClientHandler newClientHandler;
		Socket newSocket;

		while (running) {
			// On limite le nombre de clients pouvant se connecter au serveur
			if (nbTotalClientHandler < nbMaxClientHandler) {
				try {
					newSocket = socket.accept();

					// Création d'un serveur spécifique au client
					newClientHandler = new ClientHandler(newSocket);
					clientHandlerList.add(newClientHandler);
					nbActualClientHandler ++;
					nbTotalClientHandler ++;
					System.out.println("[" + this.getClass() + "]: " + "nbActualClientHandler : " + nbActualClientHandler + " ; " + "nbTotalClientHandler : " + nbTotalClientHandler);
					new Thread(newClientHandler).start();
				}
				catch (IOException ex) {
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
					throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ClientHandler.");
				}
			}
		}

		socket.close();

		System.out.println("[" + this.getClass() + "]: " + "I have finished my work.");
	}

	/**
	 * 
	 */
	@Override
	public void dispose() {

		if (this.socket != null)
		{
			try {
				socket.close();
			} catch (IOException ex) {
				System.out.println(ex);
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Classe interne ClientHandler
	 */
	private class ClientHandler implements Runnable {

		private CommunicationProtocol communicationProtocol;

		InfoClient infoClient;

		/**
		 * 
		 * @param s
		 * @throws IOException 
		 */
		public ClientHandler(Socket s) throws IOException {

			try {
				this.communicationProtocol = new CommunicationProtocol(s);
			}
			catch (IOException ex) {
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
				throw new IOException("Problème interne à Server.ClientHandler.ClientHandler() lors de la création de CommunicationProtocol.");
			}

			// Hello from clientHandler
			System.out.println("[" + this.getClass() + "]: " + "Hello from <" + this.getClass() + ">!");
		}

		/**
		 * 
		 */
		@Override
		public void run() {

			try {
				clientHandler();
			} catch (IOException ex) {
				System.out.println(ex);
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		/**
		 * 
		 * @throws IOException 
		 */
		private void clientHandler() throws IOException {

			boolean clientConnected = true;

			// Réception des infos du client
			this.infoClient = this.communicationProtocol.receiveInfoClient();
			System.out.println("[" + this.getClass() + "]: " + "infoClient reçu");

			// Envoie d'une confirmation et des infos du joueur
			this.communicationProtocol.sendStringMessage("OK");
			Server.nbClient ++;
			this.communicationProtocol.sendInfoPlayer(new InfoPlayer(Server.nbClient, "Joueur" + Server.nbClient, ColorPlayer.getColor(Server.nbClient)));

			// Boucle principale pour la communication avec le client
			while (clientConnected) {

				// Paramétrage de la partie
				

				// Démarrage de la partie
				this.communicationProtocol.sendStringMessage("Start");

				// Réception des commandes des clients
				PlayerCommand command;
				for (int i = 0; i < 4; i ++) {
					command = this.communicationProtocol.receivePlayerCommand();
					System.out.println("[" + this.getClass() + "]: " + "Commande reçue : " + command);
				}

				// Fin de la boucle principale pour la communication avec le client
				clientConnected = false;
			}

/*
			// Tests de transfert de messages
//		String message;
			Message receivedMessage = new Message("Armand Delessert", "Ça marche pas !");
			Message sendedMessage = new Message("Armand Delessert", "Hello World from <" + this.getClass() + ">!");

			// Écoute du client
//		message = receiveStringMessage();
			receivedMessage = receiveMessage();

			// Affichage du message reçu
			System.out.println("[" + this.getClass() + "]: " + "I receive this message from the client:");
//		System.out.println(message);
			receivedMessage.afficher();

			// Envoie d'une réponse au client
			System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the client.");
//		sendStringMessage("Hello World from <" + this.getClass() + ">!");
			sendMessage(sendedMessage);
*/

			// Fin du clientHandler
			this.communicationProtocol.close(); // Fermeture des connexions

			nbActualClientHandler --;
			System.out.println("[" + this.getClass() + "]: " + "nbClientHandler restants : " + nbActualClientHandler);
			if (nbActualClientHandler == 0) {
				running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
			}

			System.out.println("[" + this.getClass() + "]: " + "I have finished my work.");
		}
	}
}
