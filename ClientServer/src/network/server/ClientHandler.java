/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.server;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.CommunicationProtocol;
import network.protocol.messages.Command;
import network.protocol.messages.InfoClient;
import network.protocol.messages.InfoPlayer;
import network.protocol.messages.StateGame;
import network.protocol.messages.TiledMapMessage;

/**
 * Classe interne ClientHandler
 * 
 * @author Armand Delessert
 */
public class ClientHandler implements Runnable {

	public final int id;
	private final ClientListener clientListener;

	private CommunicationProtocol communicationProtocol;

	private InfoClient infoClient;
	private TiledMapMessage tiledMapMessage;
	private StateGame stateGame;
	private boolean newStateGameAvailable;
	private final List<Command> commandQueue;

	public boolean readyToStart;
	private Semaphore semaphore;

	/**
	 * 
	 * @param s
	 * @param clientListener
	 * @throws IOException 
	 */
	public ClientHandler(Socket s, ClientListener clientListener) throws IOException {

		this.id = ClientListener.cptIdClientHandler ++;

		this.semaphore = new Semaphore(1);
		this.readyToStart = false;
		this.clientListener = clientListener;

		try {
			this.communicationProtocol = new CommunicationProtocol(s);
		}
		catch (IOException ex) {
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à Server.ClientHandler[" + this.id + "].ClientHandler() lors de la création de CommunicationProtocol.");
		}

		this.commandQueue = new LinkedList<Command>();

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
	 * @param tiledMapMessage
	 */
	public void setTiledMapMessage(TiledMapMessage tiledMapMessage) {
		try {
			this.semaphore.acquire();
			this.tiledMapMessage = tiledMapMessage;
			this.semaphore.release();
		} catch (InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @return 
	 */
	public TiledMapMessage getTiledMapMessage() {

		TiledMapMessage newTiledMapMessage = null;

		try {
			this.semaphore.acquire();
			newTiledMapMessage = this.tiledMapMessage;
			this.semaphore.release();
		} catch (InterruptedException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
		return newTiledMapMessage;
	}

	/**
	 * 
	 * @param newStateGame
	 */
	public void setStateGame(StateGame newStateGame) {
		try {
			this.semaphore.acquire();
			this.stateGame = newStateGame;
			this.newStateGameAvailable = true;
			this.semaphore.release();
		} catch (InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @return 
	 */
	public StateGame getStateGame() {

		StateGame newStateGame = null;

		try {
			this.semaphore.acquire();
			newStateGame = this.stateGame;
			this.newStateGameAvailable = false;
			this.semaphore.release();
		} catch (InterruptedException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
		return newStateGame;
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
		this.communicationProtocol.sendInfoPlayer(new InfoPlayer(this.id + 1, "Joueur" + (this.id + 1),
						InfoPlayer.ColorPlayer.getColor(this.id + 1)));

		// Boucle principale pour la communication avec le client
		while (clientConnected) {

			// Paramétrage de la partie
//			this.getTiledMapMessage(); // Pas besoin, on le possède déjà dans la classe ClientHandler...
//			this.getStateGame(); // Pas besoin, on le possède déjà dans la classe ClientHandler...

			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Prêt !");
			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Avant le start.wait()");

			// Prêt pour le début de la partie, attente du signal pour le démarrage de la partie
			try {
				synchronized(this.clientListener.start) {
					this.readyToStart = true;
					this.clientListener.start.wait();
				}
			} catch (InterruptedException ex) {
				System.out.println(ex);
				Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
			System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Après le start.wait()");
			this.communicationProtocol.sendStringMessage("Start");

			// Boucle principale pour la communication pendant la partie
//			Command command;
			for (int i = 0; i < 4; i ++) {
				// Réception des commandes des clients
				if (this.communicationProtocol.isAvailable()) {
					try {
						commandQueue.add(this.communicationProtocol.receiveCommand());
					} catch (CommunicationProtocol.UnknownCommand ex) {
						System.out.println(ex);
						Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

//				try {
////				command = this.communicationProtocol.receiveCommand();
////				System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Commande reçue : " + command);
//
//					commandQueue.add(this.communicationProtocol.receiveCommand());
//					System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Commande(s) reçue(s) : " + this.commandQueue);
//
//				} catch (CommunicationProtocol.UnknownCommand ex) {
//					System.out.println(ex);
//					Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
//				}

				// Envoie de la mise à jour du jeu si disponible
				if (this.newStateGameAvailable) {
					System.out.println("[" + this.getClass() + " " + this.id + "]: " + "Envoie du StateGame au client.");
					this.communicationProtocol.sendStateGame(this.getStateGame());
				}
			}

			// Fin de la boucle principale pour la communication avec le client
			clientConnected = false;
		}

		// Fin du clientHandler
		this.communicationProtocol.close(); // Fermeture des connexions

		this.clientListener.nbActualClientHandler --;
		System.out.println("[" + this.getClass() + " " + this.id + "]: " + "nbClientHandler restants : "
						+ this.clientListener.nbActualClientHandler);
		if (this.clientListener.nbActualClientHandler == 0) {
			this.clientListener.running = false; // Fin du serveur lorsqu'il n'y a plus de client à servir
		}

		System.out.println("[" + this.getClass() + " " + this.id + "]: " + "I have finished my work. Goodbye!");
	}
}
