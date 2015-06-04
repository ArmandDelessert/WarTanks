/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Serveur de test de la communication client-serveur.
 */

package serveur;

import clientserver.Message;
import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.InfoClient;
import protocol.InfoPlayer;
import protocol.InfoPlayer.ColorPlayer;

/**
 * Classe Server
 * 
 * @author Armand
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
		} catch (IOException ex) {
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
					newClientHandler.start();
				} catch (IOException ex) {
					throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ServerHandler.");
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
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Classe interne ClientHandler
	 */
	private class ClientHandler extends Thread {

		private Socket socket;
		private OutputStream outputStream;
		private ObjectOutputStream outputSer;
		private InputStream inputStream;
		private ObjectInputStream inputSer;

		InfoClient infoClient;

		/**
		 * 
		 * @param s
		 * @throws IOException 
		 */
		public ClientHandler(Socket s) throws IOException {

			socket = s;

			try {
				outputStream = socket.getOutputStream();
				outputSer = new ObjectOutputStream(outputStream);
				inputStream = socket.getInputStream();
				inputSer = new ObjectInputStream(inputStream);
			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.ClientHandler.ClientHandler() lors de la création du socket.getInputStream() ou du socket.getOutputStream().");
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
			} catch (IOException | ClassNotFoundException ex) {
				System.out.println(ex);
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		/**
		 * 
		 * @param message
		 * @throws IOException 
		 */
		public void sendStringMessage(String message) throws IOException {

			message = message.concat("\0"); // Ajout du caractère EOF

			// Envoi d'un message au client
			try {
				outputStream.write(message.getBytes());
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.sendStringMessage() lors de l'envoie d'un message au client.");
			}
		}

		/**
		 * 
		 * @return
		 * @throws IOException 
		 */
		public String receiveStringMessage() throws IOException {

			int messageSize = 1024;
			byte message[] = new byte[messageSize];

			// Réception d'un message du client
			try {
				inputStream.read(message, 0, messageSize);
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.receiveStringMessage() lors de la réception du message.");
			}

			// Suppression des caractères '\0' en trop
			String s = new String(message);
			return s.substring(0, s.indexOf('\0'));
		}

		/**
		 * 
		 * @param message 
		 * @throws java.io.IOException 
		 */
		public void sendMessage(Message message) throws IOException {

			// Envoi d'un message au client
			try {
				// Sérialisation et envoi du message
				outputSer.writeObject(message);
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.sendMessage() lors de l'envoie d'un message au client.");
			}
		}

		/**
		 * 
		 * @param message 
		 * @throws java.io.IOException 
		 */
		public void sendInfoPlayer(InfoPlayer infoPlayer) throws IOException {

			// Envoi d'un message au client
			try {
				// Sérialisation et envoi du message
				outputSer.writeObject(infoPlayer);
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.sendInfoPlayer().");
			}
		}

		/**
		 * 
		 * @return 
		 * @throws java.io.IOException 
		 * @throws java.lang.ClassNotFoundException 
		 */
		public Message receiveMessage() throws IOException, ClassNotFoundException {

			Message message;

			// Réception d'un message du client
			try {
				// Réception et désérialisation du message
				message = (Message)inputSer.readObject();
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.receiveMessage() lors de la réception du message.");
			}

			return message;
		}

		/**
		 * 
		 * @return
		 * @throws IOException
		 * @throws ClassNotFoundException 
		 */
		public InfoClient receiveInfoClient() throws IOException, ClassNotFoundException {

			InfoClient receivedInfoClient;

			// Réception d'un message du client
			try {
				// Réception et désérialisation du message
				receivedInfoClient = (InfoClient)inputSer.readObject();
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.receiveInfoClient().");
			}

			return receivedInfoClient;
		}

		/**
		 * 
		 * @throws IOException 
		 */
		private void clientHandler() throws IOException, ClassNotFoundException {

			boolean clientConnected = true;

			// Réception des infos du client
			this.infoClient = receiveInfoClient();
			System.out.println("[" + this.getClass() + "]: " + "infoClient reçu");

			// Envoie d'une confirmation et des infos du joueur
			sendStringMessage("OK");
			Server.nbClient ++;
			sendInfoPlayer(new InfoPlayer(Server.nbClient, "Joueur" + Server.nbClient, ColorPlayer.getColor(Server.nbClient)));

			// Boucle principale pour la communication avec le client
			while (clientConnected) {

				// Paramétrage de la partie
				

				// Démarrage de la partie
				sendStringMessage("Start");

				// Fin de la boucle principale pour la communication avec le client
				clientConnected = false;
			}

/*
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
			outputStream.close();
			inputStream.close();
//		socket.close(); // Le socket du ClientHandler est déjà clos

			nbActualClientHandler --;
			System.out.println("[" + this.getClass() + "]: " + "nbClientHandler restants : " + nbActualClientHandler);
			if (nbActualClientHandler == 0) {
				running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
			}

			System.out.println("[" + this.getClass() + "]: " + "I have finished my work.");
		}
	}
}
