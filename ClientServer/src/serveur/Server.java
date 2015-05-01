/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 */

package serveur;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe Server
 * 
 * @author Armand
 */
public class Server implements Runnable {

	private ServerSocket socket;

	/**
	 * 
	 * @param numeroPort
	 * @throws IOException 
	 */
	public Server(int numeroPort) throws IOException {

		// Création du socket d'écoute des clients
		try {
			socket = new ServerSocket(numeroPort);
		} catch (IOException ex) {
			throw new IOException("Problème interne à Server.Server() lors de la création du socket !");
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

		while (true) {
			try {
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				newServerHandler = new ClientHandler(newSocket);
				newServerHandler.run();
			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ServerHandler.");
			}
		}
	}

	/**
	 * Classe interne ClientHandler
	 */
	private class ClientHandler implements Runnable {

		private InputStream is;

		public ClientHandler(Socket socket) throws IOException {
			try {
				is = socket.getInputStream();
			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.ServerHandler.ServerHandler() lors de la création du socket.getInputStream().");
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
		private void clientHandler() throws IOException {

			int messageSize = 1024;
			byte message[] = new byte[messageSize];

			// Écoute du client
			try {
				is.read(message, 0, messageSize);

				// Affichage du message reçu
				System.out.println(this.getClass() + ": I receive this message from the client:");
				System.out.println(new String(message));

			} catch (IOException ex) {
				throw new IOException("Problème interne à Server.ServerHandler.run() lors de la lecture du message reçu.");
			}
		}
	}
}
