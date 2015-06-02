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
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.InfoClient;
import protocol.InfoPlayer;

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
			try {
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				newClientHandler = new ClientHandler(newSocket);
//			clientHandlerList.add(newServerHandler);
				nbClientHandler++;
				System.out.println("nbClientHandler : " + nbClientHandler);
				newClientHandler.start();
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
	private class ClientHandler extends Thread {

		private Socket socket;
		private InputStream inputStream;
		private ObjectInputStream inputSer;
		private OutputStream outputStream;
		private ObjectOutputStream outputSer;

		InfoClient infoClient;

		/**
		 * 
		 * @param s
		 * @throws IOException 
		 */
		public ClientHandler(Socket s)  throws IOException {

			socket = s;

			try {
				System.out.println("ClientHandler1");
				inputStream = socket.getInputStream();
				System.out.println("ClientHandler2");
				inputSer = new ObjectInputStream(inputStream);
				System.out.println("ClientHandler3");
				outputStream = socket.getOutputStream();
				outputSer = new ObjectOutputStream(outputStream);
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

			// Réception des infos du client
			infoClient = receiveInfoClient();
			System.out.println("infoClient.id = " + infoClient.id);

			System.out.println("Serveur : Youhouuu!");

			// Envoie d'une confirmation et des infos du joueur
			sendStringMessage("OK");
			sendInfoPlayer(new InfoPlayer(1, "Joueur1", "Blue"));

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
			inputStream.close();
			outputStream.close();
//		socket.close(); // Le socket du ClientHandler est déjà clos

			nbClientHandler--;
			System.out.println("nbClientHandler restants : " + nbClientHandler);
			if (nbClientHandler == 0) {
				running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
			}

			Thread.currentThread().stop();
		}
	}
}
