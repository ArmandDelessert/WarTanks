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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.CommunicationProtocol;
import network.protocol.messages.InfoClient;
import network.protocol.messages.InfoPlayer;
import network.protocol.messages.InfoPlayer.ColorPlayer;
import network.protocol.messages.Command;
import network.protocol.messages.StateGame;

/**
 * Classe ClientListener
 * 
 * @author Armand Delessert
 */
public class ClientListener implements Runnable, Disposable {

	private ServerSocket socket;

	public static int cptIdClientHandler = 0;

	public static int nbClient = 0;

	public boolean running = true;
	public int nbActualClientHandler;
	private int nbTotalClientHandler;
	private int nbMaxClientHandler;

	public final List<ClientHandler> clientHandlerList;
	private final List<Thread> threadList;

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

		this.clientHandlerList = new LinkedList<ClientHandler>();
		this.threadList = new LinkedList<Thread>();

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

		// Création des ClientHandler
		for (nbTotalClientHandler = 0; nbTotalClientHandler < nbMaxClientHandler; nbTotalClientHandler ++) {
			try {
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				clientHandlerList.add(new ClientHandler(newSocket, this));
				threadList.add(new Thread(clientHandlerList.get(clientHandlerList.size()-1)));
				nbActualClientHandler ++;
				System.out.println("[" + this.getClass() + "]: " + "nbActualClientHandler : " + nbActualClientHandler + " ; " + "nbTotalClientHandler : " + nbTotalClientHandler);
			}
			catch (IOException ex) {
				Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ClientHandler.");
			}
		}

		// Lancement des ClientHandler
		for (Object i : threadList) {
			((Thread)i).start();
		}

		// Join sur les threads ClientHandler
		try {
			for (Object i : threadList) {
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
}
