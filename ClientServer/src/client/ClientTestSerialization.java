/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 05.05.2015
 * 
 * Description :
 * Client de test du protocole de communication client-serveur pour le jeu WarTanks.
 */

package client;

import clientserver.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Armand Delessert
 */
public class ClientTestSerialization implements Runnable {

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	/**
	 * 
	 * @param adresseIP
	 * @param numeroPort
	 * @throws IOException 
	 */
	public ClientTestSerialization(String adresseIP, int numeroPort) throws IOException {

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
			warTanksClient();
		} catch (IOException ex) {
			System.out.println(ex);
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClientTestSerialization.class.getName()).log(Level.SEVERE, null, ex);
		}
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
			ObjectOutputStream outputSer = new ObjectOutputStream(outputStream);
			outputSer.writeObject(message);
		} catch (IOException ex) {
			throw new IOException("Problème interne à ClientHandler.sendMessage() lors de l'envoie d'un message au client.");
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
			ObjectInputStream inputSer = new ObjectInputStream(inputStream);
			message = (Message)inputSer.readObject();
		} catch (IOException ex) {
			throw new IOException("Problème interne à ClientHandler.receiveMessage() lors de la réception du message.");
		}

		return message;
	}

	/**
	 * 
	 * @throws IOException 
	 */
	private void warTanksClient() throws IOException, ClassNotFoundException {

		Message receivedMessage = new Message("Armand Delessert", "Ça marche pas !");
		Message sendedMessage = new Message("Armand Delessert", "Hello World from <" + this.getClass() + ">!");

		// Envoie d'un message au serveur
		System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the server.");
		sendMessage(sendedMessage);

		// Réception de la réponse du serveur
		receivedMessage = receiveMessage();

		// Affichage du message reçu
		System.out.println("[" + this.getClass() + "]: " + "I receive this message from the server:");
		receivedMessage.afficher();

		// Fin du client
		inputStream.close();
		outputStream.close();
		socket.close();
		Thread.currentThread().stop();
	}
}
