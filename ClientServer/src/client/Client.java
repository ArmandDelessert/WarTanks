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
import protocol.InfoPlayer;

/**
 * Classe Client
 * 
 * @author Armand
 */
public class Client implements Runnable {

	private Socket socket;
	private OutputStream outputStream;
	private ObjectOutputStream outputSer;
	private InputStream inputStream;
	private ObjectInputStream inputSer;

	InfoClient infoClient;
	InfoPlayer infoPlayer;

	/**
	 *
	 * @param infoClient
	 * @throws IOException
	 */
	public Client(InfoClient infoClient) throws IOException {

		this.infoClient = infoClient;

		// Création du socket de connexion au serveur
		try {
			socket = new Socket(infoClient.ipAddress, infoClient.portNumber);
			outputStream = socket.getOutputStream();
			outputSer = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			inputSer = new ObjectInputStream(inputStream);
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
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @param message
	 * @throws IOException 
	 */
	public void sendStringMessage(String message) throws IOException {

		message = message.concat("\0"); // Ajout du caractère EOF

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

		// Suppression des caractères '\0' en trop
		String s = new String(message);
		return s.substring(0, s.indexOf('\0'));
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
			message = (Message)inputSer.readObject();
		} catch (IOException ex) {
			throw new IOException("Problème interne à ClientHandler.receiveMessage() lors de la réception du message.");
		}

		return message;
	}

	/**
	 * 
	 * @return 
	 * @throws java.io.IOException 
	 * @throws java.lang.ClassNotFoundException 
	 */
	public InfoPlayer receiveInfoPlayer() throws IOException, ClassNotFoundException {

		InfoPlayer receivedInfoPlayer;

		// Réception d'un message du client
		try {
			// Réception et désérialisation du message
			receivedInfoPlayer = (InfoPlayer)inputSer.readObject();
		} catch (IOException ex) {
			throw new IOException("Problème interne à ClientHandler.receiveInfoPlayer().");
		}

		return receivedInfoPlayer;
	}

	/**
	 * 
	 * @throws IOException 
	 */
	private void client() throws IOException, ClassNotFoundException {

		boolean connectedToTheServer = true;

		// Le client s'annonce au serveur
		sendInfoClient(this.infoClient);

		// Le client attend la confirmation du serveur
		String confirmation = receiveStringMessage();
		switch (confirmation) {
			case "OKsr":
				System.out.println("[" + this.getClass() + "]: " + "Réponse du serveur : OKsr");
			case "OK":
				System.out.println("[" + this.getClass() + "]: " + "Connecté au serveur");
				this.infoPlayer = receiveInfoPlayer();
				System.out.println("[" + this.getClass() + "]: " + "infoPlayer : " + infoPlayer);
				break;
			case "Refused":
				System.out.println("ERREUR : Connexion au serveur refusée.");
				break;
			default:
				System.out.println("[" + this.getClass() + "]: " + "Le serveur a répondu : " + confirmation);
				System.out.println("ERREUR : La réponse du serveur est invalide.");
				break;
		}

		// Boucle principale pour la communication avec le serveur
		while (connectedToTheServer) {

			// Paramétrage de la partie
			

			// Fin de la boucle principale pour la communication avec le serveur
			connectedToTheServer = false;
		}

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
		outputStream.close();
		inputStream.close();
//	socket.close(); // Le socket du client est déjà clos

		System.out.println("[" + this.getClass() + "]: " + "I have finished my work.");
	}
}
