/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 08.05.2015
 * 
 * Description :
 * Serveur pour la réception des commandes.
 */

package server.network;

import java.io.IOException;
import java.io.InputStream;
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
public class Server implements Runnable {

	public boolean running = true;
	private int nbClientHandler;
	private List clientHandlerList;

	private ServerSocket socket;

	/**
	 * 
	 * @param numeroPort
	 * @throws IOException 
	 */
	public Server(int numeroPort) throws IOException {

		clientHandlerList = new LinkedList();

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

		ClientHandler newClientHandler;
		Socket newSocket;

		while (running) {
			try {
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				newClientHandler = new ClientHandler(newSocket);
				clientHandlerList.add(newClientHandler);
				newClientHandler.run();
				nbClientHandler++;
			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ServerHandler.");
			}
		}

		// Arrêt du serveur
//	while (clientHandlerList.size() > 0); // Attente de la fin des clientHandler
		if (nbClientHandler > 0) {
			System.out.println("nbClientHandler : " + nbClientHandler);
		}

		socket.close();
		Thread.currentThread().stop();
	}

	public List getInfoClients() {
		return this.clientHandlerList;
	}

	/**
	 * Classe interne ClientHandler
	 */
	protected class ClientHandler implements Runnable {

		private Socket socket;
		private InputStream inputStream;
		private OutputStream outputStream;

		public InfoClient infoClient;

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
		 * @throws IOException 
		 */
		private void clientHandler() throws IOException {

			String message;

			// Écoute du client
			message = receiveStringMessage();

			// Affichage du message reçu
			System.out.println("[" + this.getClass() + "]: " + "I receive this message from the client:");
			System.out.println(message);

			// Envoie d'une réponse au client
			System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the client.");
			sendStringMessage("Hello World from <" + this.getClass() + ">!");

			// Fin du clientHandler
			inputStream.close();
			outputStream.close();
			socket.close(); // Fermer le socket du clientHandler et non celui du serveur

			nbClientHandler--;
			if (nbClientHandler == 0) {
				running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
			}

			Thread.currentThread().stop();


			// ========== clientHandler ==========

			// Réception des infos du client (ip, nom, ...)
		}
	}
}
