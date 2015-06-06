/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Serveur de test de la communication client-serveur.
 */

package network.server;

import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.CommunicationProtocol;
import network.protocol.messages.InfoClient;
import network.protocol.messages.InfoPlayer;
import network.protocol.messages.InfoPlayer.ColorPlayer;
import network.protocol.messages.Command;
import network.protocol.messages.StateMap;

/**
 * Classe ClientListener
 * 
 * @author Armand Delessert
 */
public class ClientListener implements Runnable, Disposable {

	public static int cptIdClientHandler = 0;

	public static int nbClient = 0;

	public boolean running = true;
	private int nbActualClientHandler;
	private int nbTotalClientHandler;
	private int nbMaxClientHandler;

	private ServerSocket socket;

	/**
	 * 
	 * @param nbMaxClient
	 * @param numeroPort
	 * @throws IOException 
	 */
	public ClientListener(int nbMaxClient, int numeroPort) throws IOException {

		nbActualClientHandler = 0;
		nbTotalClientHandler = 0;
		nbMaxClientHandler = nbMaxClient;

		// Création du socket d'écoute des clients
		try {
			socket = new ServerSocket(numeroPort);
		}
		catch (IOException ex) {
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws IOException 
	 */
	private void clientWaiting() throws IOException {

		Socket newSocket;
		Vector clientHandlerList = new Vector();

		// Création des ClientHandler
		for (nbTotalClientHandler = 0; nbTotalClientHandler < nbMaxClientHandler; nbTotalClientHandler ++) {
			try {
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				clientHandlerList.add(new Thread(new ClientHandler(newSocket)));
				nbActualClientHandler ++;
				System.out.println("[" + this.getClass() + "]: " + "nbActualClientHandler : " + nbActualClientHandler + " ; " + "nbTotalClientHandler : " + nbTotalClientHandler);
			}
			catch (IOException ex) {
				Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ClientHandler.");
			}
		}

		// Lancement des ClientHandler
		for (Object i : clientHandlerList) {
			((Thread)i).start();
		}

		// Join sur les threads ClientHandler
		try {
			for (Object i : clientHandlerList) {
				((Thread)i).join();
			}
		} catch (InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
		}

		socket.close();

		System.out.println("[" + this.getClass() + "]: " + "I have finished my work. Goodbye!");
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
				Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Classe interne ClientHandler
	 */
	private class ClientHandler implements Runnable {

		public final int id;

		private CommunicationProtocol communicationProtocol;

		InfoClient infoClient;

		/**
		 * 
		 * @param s
		 * @throws IOException 
		 */
		public ClientHandler(Socket s) throws IOException {

			this.id = ClientListener.cptIdClientHandler ++;

			try {
				this.communicationProtocol = new CommunicationProtocol(s);
			}
			catch (IOException ex) {
				Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
				throw new IOException("Problème interne à Server.ClientHandler[" + this.id + "].ClientHandler() lors de la création de CommunicationProtocol.");
			}

			// Hello from clientHandler
			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Hello from <" + this.getClass() + ">!");
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
				Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
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
			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "infoClient reçu");

			// Envoie d'une confirmation et des infos du joueur
			this.communicationProtocol.sendStringMessage("OK");
			ClientListener.nbClient ++;
			this.communicationProtocol.sendInfoPlayer(new InfoPlayer(ClientListener.nbClient, "Joueur" + ClientListener.nbClient, ColorPlayer.getColor(ClientListener.nbClient)));

			// Boucle principale pour la communication avec le client
			while (clientConnected) {

				// Paramétrage de la partie
				

				// Démarrage de la partie
				this.communicationProtocol.sendStringMessage("Start");

				// Boucle principale pour la communication pendant la partie
				Command command;
				StateMap stateMap = new StateMap();
				for (int i = 0; i < 4; i ++) {
					// Réception des commandes des clients
					command = this.communicationProtocol.receivePlayerCommand();
					System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Commande reçue : " + command);

					// Envoie de la mise à jour de la map
					this.communicationProtocol.sendStateMap(stateMap);
				}

				// Fin de la boucle principale pour la communication avec le client
				clientConnected = false;
			}

			// Fin du clientHandler
			this.communicationProtocol.close(); // Fermeture des connexions

			nbActualClientHandler --;
			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "nbClientHandler restants : " + nbActualClientHandler);
			if (nbActualClientHandler == 0) {
				running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
			}

			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "I have finished my work. Goodbye!");
		}
	}
}
