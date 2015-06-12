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
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe ClientListener
 * 
 * @author Armand Delessert
 */
public class ClientListener implements Runnable, Disposable {

	private ServerSocket socket;

	public static int cptIdClientHandler = 0;

	public boolean running = true;
	public int nbActualClientHandler;
	private int nbTotalClientHandler;
	private int nbMaxClientHandler;

	public final List<ClientHandler> clientHandlerList;
	private final List<Thread> clientHandlerThreadList;
	public boolean allClientHandlerReady;
	public final Lock start;

	/**
	 * 
	 * @param nbMaxClient
	 * @param portNumber
	 * @throws IOException 
	 */
	public ClientListener(int nbMaxClient, int portNumber) throws IOException {

		nbActualClientHandler = 0;
		nbTotalClientHandler = 0;
		nbMaxClientHandler = nbMaxClient;

		// Création du socket d'écoute des clients
		try {
			socket = new ServerSocket(portNumber);
		}

//		while (!portNumberAvailable(portNumber) && portNumber < portNumber + 10) {
//			portNumber ++;
//		}
//		try {
//			if (portNumber < portNumber + 10) {
//				socket = new ServerSocket(portNumber);
//			}
//		}
		catch (IOException ex) {
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à Server.Server() lors de la création du socket.");
		}

		this.clientHandlerList = new LinkedList<ClientHandler>();
		this.clientHandlerThreadList = new LinkedList<Thread>();
		this.allClientHandlerReady = false;
		this.start = new ReentrantLock();

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
	 * Checks to see if a specific port is available.
	 *
	 * @param portNumber the port to check for availability
	 * @return 
	 */
	public static boolean portNumberAvailable(int portNumber) {
		if (portNumber < 0 || portNumber > 65534) {
			throw new IllegalArgumentException("Invalid start port: " + portNumber);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(portNumber);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(portNumber);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException ex) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ex) {
					/* Should not be thrown */
				}
			}
		}

		return false;
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
				// Attente sur la connexion d'un nouveau client
				newSocket = socket.accept();

				// Création d'un serveur spécifique au client
				clientHandlerList.add(new ClientHandler(newSocket, this));
				clientHandlerThreadList.add(new Thread(clientHandlerList.get(clientHandlerList.size()-1)));
				nbActualClientHandler ++;
				System.out.println("[" + this.getClass() + "]: " + "nbActualClientHandler : " + nbActualClientHandler + " ; " + "nbTotalClientHandler : " + nbTotalClientHandler);
			}
			catch (IOException ex) {
				Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
				throw new IOException("Problème interne à Server.clientWaiting() lors de la création du ClientHandler.");
			}
		}

		// Lancement des ClientHandler
		for (Object i : clientHandlerThreadList) {
			((Thread)i).start();
		}

		// Attente sur l'initialisation des ClientHandler
		boolean readyToStart = false;
		while (!readyToStart) {
			readyToStart = true;
			for (Object i : this.clientHandlerList) {
				if (!((ClientHandler)i).readyToStart) {
					readyToStart = false;
					break;
				}
			}
		}

		// Tous les ClientHandler sont prêt
		this.allClientHandlerReady = true;

		System.out.println("[" + this.getClass() + "]: " + "Tous les ClientHandler sont prêt.");

		// Join sur les threads ClientHandler
		try {
			for (Object i : clientHandlerThreadList) {
				((Thread)i).join();
			}
		} catch (InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
		}

		// Fermeture du socket du serveur
		this.dispose();

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
