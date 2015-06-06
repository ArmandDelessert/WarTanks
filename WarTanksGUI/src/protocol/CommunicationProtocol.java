/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 04.06.2015
 * 
 * Description :
 * Liste des fonctions nécessaires à la communication client-serveur.
 */

package protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.messages.Command;
import protocol.messages.InfoClient;
import protocol.messages.InfoPlayer;
import protocol.messages.Message;
import protocol.messages.Movement;
import protocol.messages.Shoot;
import protocol.messages.StateMap;
import protocol.messages.TiledMapMessage;
import protocol.messages.UseBonus;

/**
 *
 * @author Armand Delessert
 */
public class CommunicationProtocol {

	private Socket socket;
	private OutputStream outputStream;
	private ObjectOutputStream outputSer;
	private InputStream inputStream;
	private ObjectInputStream inputSer;
        
        public static class UnknownCommand extends Exception {}

	/**
	 * 
	 * @param s
	 * @throws IOException 
	 */
	public CommunicationProtocol(Socket s) throws IOException {

		this.socket = s;

		// Création du socket de connexion
		try {
			outputStream = socket.getOutputStream();
			outputSer = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			inputSer = new ObjectInputStream(inputStream);
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.CommunicationProtocol().");
		}
	}

	/**
	 * 
	 * @throws IOException 
	 */
	public void close() throws IOException {
		try {
			inputStream.close();
			outputStream.close();
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.close().");
		}
	}
	
	/**
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendStringMessage(String message) throws IOException {

		message = message.concat("\0"); // Ajout du caractère EOF

		// Envoi d'un message
		try {
			outputStream.write(message.getBytes());
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.sendStringMessage() lors de l'envoie d'un message.");
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

		// Réception d'un message
		try {
			inputStream.read(message, 0, messageSize);
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receiveStringMessage() lors de la réception du message.");
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

		// Envoi d'un message
		try {
			// Sérialisation et envoi du message
			outputSer.writeObject(message);
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.sendMessage() lors de l'envoie d'un message.");
		}
	}

	/**
	 * 
	 * @return 
	 * @throws java.io.IOException 
	 */
	public Message receiveMessage() throws IOException {

		// Réception d'un message
		try {
			// Réception et désérialisation du message
			return (Message)inputSer.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receiveMessage() lors de la réception du message.");
		}
	}

	/**
	 * 
	 * @param infoClient
	 * @throws IOException 
	 */
	public void sendInfoClient(InfoClient infoClient) throws IOException {

		try {
			outputSer.writeObject(infoClient);
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.sendInfoClient().");
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public InfoClient receiveInfoClient() throws IOException {

		try {
			return (InfoClient)inputSer.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receiveInfoClient().");
		}
	}

	/**
	 * 
	 * @param infoPlayer
	 * @throws java.io.IOException 
	 */
	public void sendInfoPlayer(InfoPlayer infoPlayer) throws IOException {

		try {
			outputSer.writeObject(infoPlayer);
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.sendInfoPlayer().");
		}
	}

	/**
	 * 
	 * @return 
	 * @throws java.io.IOException 
	 */
	public InfoPlayer receiveInfoPlayer() throws IOException {

		try {
			return (InfoPlayer)inputSer.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receiveInfoPlayer().");
		}
	}

	

	/**
	 * 
	 * @param stateMap
	 * @throws IOException 
	 */
	public void sendStateMap(StateMap stateMap) throws IOException {

		try {
			outputSer.writeObject(stateMap);
		}
		catch (IOException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.sendPlayerCommand().");
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public StateMap receiveStateMap() throws IOException {

		try {
			return (StateMap)inputSer.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receivePlayerCommand().");
		}
	}
        
        public TiledMapMessage receiveTiledMapMessage() throws IOException {

		try {
			return (TiledMapMessage)inputSer.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receiveTiledMapMessage().");
		}
	}
        
        public void sendTiledMapMessage(TiledMapMessage tiledMapMessage) throws IOException {
            try {
                outputSer.writeObject(tiledMapMessage);
            }
            catch (IOException ex) {
                Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException("Problème interne à CommunicationProtocol.sendTiledMapMessage().");
            }
        }
        
        /**
         * 
         * @return 
         */
	public boolean isAvailable() throws IOException {
            return (inputSer.available() > 0);
	}
        
        /**
         * 
         * @param cmd
         * @throws IOException 
         */
        public void sendCmd(Command cmd) throws IOException {
            try {
                    outputSer.writeObject(cmd);
            }
            catch (IOException ex) {
                    Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException("Problème interne à CommunicationProtocol.sendCmd() (IOException)");
            }
        }

        /**
         * 
         * @return
         * @throws IOException
         * @throws client.CommunicationProtocol.UnknownCommand 
         */
	public Command receiveCmd() throws IOException, UnknownCommand {
                Command input = null;
		try {
			input =  (Command)inputSer.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(CommunicationProtocol.class.getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Problème interne à CommunicationProtocol.receivePlayerCommand().");
		} finally {
                    if (input.getClass().equals(UseBonus.class)) {
                        return (UseBonus)input;
                    }
                    
                    else if (input.getClass().equals(Shoot.class)) {
                        return (Shoot)input;
                    }
                    
                    else if (input.getClass().equals(Movement.class)) {
                        return (Movement)input;
                    }
                    
                    else {
                        throw new CommunicationProtocol.UnknownCommand();
                    }
                    
                }
	}
}