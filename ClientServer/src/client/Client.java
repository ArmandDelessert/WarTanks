/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 */

package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe Client
 * 
 * @author Armand
 */
public class Client implements Runnable {

	private Socket socket;
	private OutputStream os;

	/**
	 * 
	 * @param adresseIP
	 * @param numeroPort
	 * @throws IOException 
	 */
	public Client(String adresseIP, int numeroPort) throws IOException {

		// Création du socket de connexion au serveur
		try {
			socket = new Socket(adresseIP, numeroPort);
			os = socket.getOutputStream();
		} catch (IOException ex) {
			throw new IOException("Problème interne à Client.Client() lors de la connexion au serveur !");
		}

		// Hello from client
//		System.out.println("[" + this.getClass() + "]: " + "Hello from <" + this.getClass() + ">!");
	}

	/**
	 * 
	 * @param message
	 * @throws IOException 
	 */
	public void sendMessage(String message) throws IOException {

		// Envoi d'un message au serveur
		try {
			os.write(message.getBytes());
		}
		catch (IOException ex) {
			throw new IOException("Problème interne à Client.sendMessage() lors de la connexion au serveur !");
		}
	}

	/**
	 * 
	 */
	@Override
	public void run() {

		// Test de communication entre le client et le serveur
		try {
			System.out.println(this.getClass() + ": I send \"Hello World!\" to the server.");
			sendMessage("Hello World!");
		}
		catch (IOException ex) {
			System.out.println(ex);
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
