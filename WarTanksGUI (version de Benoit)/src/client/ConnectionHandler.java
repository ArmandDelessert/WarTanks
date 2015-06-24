/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.protocol.CommunicationProtocol;
import network.protocol.messages.Command;
import network.protocol.messages.InfoPlayer;
import network.protocol.messages.StateGame;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author xajkep
 */
public class ConnectionHandler extends Thread {

	private final int SLEEP_TIME = 50;

	private InetAddress addr;
	private short port;

	private Socket conn;
	private CommunicationProtocol communicationProtocol;

	private InfoPlayer infoPlayer;

	private boolean running;

	private List<Command> cmdQueue;

	public ConnectionHandler(InetAddress addr, short port) {
		this.addr = addr;
		this.port = port;

		this.conn = null;
		this.communicationProtocol = null;

		cmdQueue = new LinkedList<Command>();
	}

	public void run() {
		connect();

		if (running) {
			try {
				this.communicationProtocol = new CommunicationProtocol(conn);

				// Si le handshake échoue, ferme la connection
				if (!exchangeHandshake()) {
					disconnect();
				}

				// Récupération les informations de notre joueur
				this.infoPlayer = this.communicationProtocol.receiveInfoPlayer();
				System.out.println("[" + this.getClass() + " " + this.infoPlayer.id + "]: " + "infoPlayer : " + infoPlayer);

				// Récupération de TiledMap
				TiledMap tiledMap = (TiledMap) this.communicationProtocol.receiveTiledMapMessage().getTiledMap();

				// Récupération de l'état du jeu
				StateGame stateGame = this.communicationProtocol.receiveStateGame();

				// Wait start from the server
				while (!this.communicationProtocol.receiveStringMessage().equals("Start"));

				while (running) {

					// Send commands in queue
					for (Command cmd : cmdQueue) {
						this.communicationProtocol.sendCommand(cmd);
					}
					cmdQueue.clear();

					// Récupère l'état du jeu s'il a été envoyé
					if (this.communicationProtocol.isAvailable()) {
						stateGame = this.communicationProtocol.receiveStateGame();
					}

					// sleep to not overload
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException ex) {
						Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private boolean exchangeHandshake() {
		String confirmation = "";
		try {
			confirmation = this.communicationProtocol.receiveStringMessage();
		} catch (IOException ex) {
			Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			switch (confirmation) {
				case "OK":
					System.out.println("[" + this.getClass() + "]: " + "Connecté au serveur");
					return true;
				case "Refused":
					System.out.println("ERREUR : Connexion au serveur refusée.");
					return false;
				default:
					System.out.println("[" + this.getClass() + "]: " + "Le serveur a répondu : " + confirmation);
					System.out.println("ERREUR : La réponse du serveur est invalide.");
					return false;
			}
		}
	}

	public void addCmd(Command cmd) {
		cmdQueue.add(cmd);
	}

	private void connect() {
		try {
			this.conn = new Socket(addr, port);
		} catch (IOException ex) {
			Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			running = true;
		}
	}

	private void disconnect() {
		if (conn != null) {
			try {
				System.out.println("[*] Close connection");
				this.communicationProtocol.close();
				conn.close();
			} catch (IOException ex) {
				Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				running = false;
			}
		}
	}

}
