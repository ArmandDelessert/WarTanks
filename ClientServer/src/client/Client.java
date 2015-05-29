/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 * 
 * Description :
 * Client de test de la communication client-serveur.
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
import protocol.InfoClient;

/**
 * Classe Client
 * 
 * @author Armand
 */
public class Client implements Runnable {

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	InfoClient infoClient;

	/**
	 *
	 * @param infoClient
	 * @throws IOException
	 */
	public Client(InfoClient infoClient) throws IOException {

		this.infoClient = infoClient;

		// Création du socket de connexion au serveur
		try {
			socket = new Socket(infoClient.ip, infoClient.numeroPort);
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
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClientTestSerialization.class.getName()).log(Level.SEVERE, null, ex);
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

		// Réception d'un message du serveur
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

		// Envoi d'un message au serveur
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
	 * @param infoClient
	 * @throws IOException 
	 */
	public void sendInfoClient(InfoClient infoClient) throws IOException {

		// Envoi d'un message au client
		try {
			// Sérialisation et envoi du message
			ObjectOutputStream outputSer = new ObjectOutputStream(outputStream);
			outputSer.writeObject(infoClient);
		} catch (IOException ex) {
			throw new IOException("Problème interne à ClientHandler.sendInfoClient().");
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
	private void client() throws IOException, ClassNotFoundException {

		// Le client s'annonce au serveur
		sendInfoClient(this.infoClient);

/*
//	String message;
		Message receivedMessage = new Message("Armand Delessert", "Ça marche pas !");
		Message sendedMessage = new Message("Armand Delessert", "Hello World from <" + this.getClass() + ">!");

		// Envoie d'un message au serveur
		System.out.println("[" + this.getClass() + "]: " + "I send \"Hello World!\" to the server.");
//	sendStringMessage("Hello World from <" + this.getClass() + ">!");
		sendMessage(sendedMessage);

		// Réception de la réponse du serveur
//	message = receiveStringMessage();
		receivedMessage = receiveMessage();

		// Affichage du message reçu
		System.out.println("[" + this.getClass() + "]: " + "I receive this message from the server:");
//	System.out.println(message);
		receivedMessage.afficher();
*/

		// Fin du client
		inputStream.close();
		outputStream.close();
//	socket.close();
		Thread.currentThread().stop();
	}
}
