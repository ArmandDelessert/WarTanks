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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe Server
 * 
 * @author Armand
 */
public class Server implements Runnable, Disposable {

	public boolean running = true;
	private int nbClientHandler;
//private List<ClientHandler> clientHandlerList;

	private ServerSocket socket;

	/**
	 * 
	 * @param numeroPort
	 * @throws IOException 
	 */
	public Server(int numeroPort) throws IOException {

//	clientHandlerList = new LinkedList<>();

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
//			clientHandlerList.add(newServerHandler);
				nbClientHandler++;
				System.out.println("nbClientHandler : " + nbClientHandler);
				newClientHandler.run();
			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ServerHandler.");
			}
		}

		// Arrêt du serveur
//	while (clientHandlerList.size() > 0); // Attente de la fin des clientHandler
		System.out.println("Server : J'a fini !");
		socket.close();
		Thread.currentThread().stop();
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
//		socket.close(); // Fermer le socket du clientHandler et non celui du serveur

			nbClientHandler--;
			System.out.println("nbClientHandler restants : " + nbClientHandler);
			if (nbClientHandler == 0) {
				running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
			}

			Thread.currentThread().stop();
		}
	}
}
