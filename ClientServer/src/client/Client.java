/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Client de test de la communication client-serveur.
 */

package client;

import java.io.IOException;
import java.io.InputStream;
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
	private InputStream inputStream;
	private OutputStream outputStream;

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
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException ex) {
			throw new IOException("Problème interne à Client.Client() lors de la création du socket ou lors de la création du socket.getInputStream() ou du socket.getOutputStream().");
		}

		// Hello from client
		System.out.println("[" + this.getClass() + "]: " + "Hello from <" + this.getClass() + ">!");
	}

	/**
	 * 
	 */
	@Override
	public void run() {

		try {
			client();
		} catch (IOException ex) {
			System.out.println(ex);
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
	private void client() throws IOException {

		String message;

		// Envoie d'un message au serveur
		System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the server.");
		sendStringMessage("Hello World from <" + this.getClass() + ">!");

		// Réception de la réponse du serveur
		message = receiveStringMessage();

		// Affichage du message reçu
		System.out.println("[" + this.getClass() + "]: " + "I receive this message from the server:");
		System.out.println(message);

		// Fin du client
		inputStream.close();
		outputStream.close();
//	socket.close();
		Thread.currentThread().stop();
	}
}
