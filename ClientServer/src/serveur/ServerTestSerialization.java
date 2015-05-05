/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 05.05.2015
 * 
 * Description :
 * Serveur de test du protocole de communication client-serveur pour le jeu WarTanks.
 */

package serveur;

import clientserver.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Armand Delessert
 */
public class ServerTestSerialization implements Runnable {

	public boolean running = true;
	private List<ClientHandler> clientHandlerList;

	private ServerSocket socket;

	/**
	 * 
	 * @param numeroPort
	 * @throws IOException 
	 */
	public ServerTestSerialization(int numeroPort) throws IOException {

		clientHandlerList = new LinkedList<>();

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
		}
		catch (IOException ex) {
			System.out.println(ex);
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws IOException 
	 */
	private void clientWaiting() throws IOException {

		ClientHandler newServerHandler;
		Socket newSocket;

		while (running) {
			try {
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				newServerHandler = new ClientHandler(newSocket);
				newServerHandler.run();
			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ServerHandler.");
			}
		}

		// Arrêt du serveur
		while (clientHandlerList.size() > 0); // Attente de la fin des clientHandler
		socket.close();
		Thread.currentThread().stop();
	}

	/**
	 * Classe interne ClientHandler
	 */
	private class ClientHandler implements Runnable {

		private Socket socket;
		private InputStream inputStream;
		private OutputStream outputStream;

		public ClientHandler(Socket s) throws IOException {

			socket = s;

			try {
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
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
			} catch (IOException ex) {
				System.out.println(ex);
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ServerTestSerialization.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		/**
		 * 
		 * @param message
		 * @throws IOException 
		 */
		public void sendStringMessage(String message) throws IOException {

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

			return new String(message);
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
				ObjectOutputStream outputSer = new ObjectOutputStream(outputStream);
				outputSer.writeObject(message);
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.sendMessage() lors de l'envoie d'un message au client.");
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
				ObjectInputStream inputSer = new ObjectInputStream(inputStream);
				message = (Message)inputSer.readObject();
			} catch (IOException ex) {
				throw new IOException("Problème interne à ClientHandler.receiveMessage() lors de la réception du message.");
			}

			return message;
		}

		/**
		 * 
		 * @throws IOException 
		 */
		private void clientHandler() throws IOException, ClassNotFoundException {

			Message receivedMessage = new Message("Armand Delessert", "Ça marche pas !");
			Message sendedMessage = new Message("Armand Delessert", "Hello World from <" + this.getClass() + ">!");

			// Écoute du client
			receivedMessage = receiveMessage();

			// Affichage du message reçu
			System.out.println("[" + this.getClass() + "]: " + "I receive this message from the client:");
			receivedMessage.afficher();

			// Envoie d'une réponse au client
			System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the client.");
			sendMessage(sendedMessage);

			// Fin du clientHandler
			inputStream.close();
			outputStream.close();
			socket.close(); // Fermer le socket du clientHandler et non celui du serveur
			Thread.currentThread().stop();
			Thread.currentThread().stop();
		}
	}
}
