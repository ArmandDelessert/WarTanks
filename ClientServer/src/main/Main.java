/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 01.05.2015
 */

package main;

import client.Client;
import java.io.IOException;
import serveur.Server;


/**
 * Classe Main
 * 
 * @author Armand
 */
public class Main {

	private static Thread client;
	private static Thread server;
	private static final String ip = "localhost";
	private static final int port = 1991;

	/**
	 *
	 * @param arg
	 */
	public static void main(String[] arg) {

		// Création du serveur et d'un client
		try {
			server = new Thread(new Server(port));
			server.start();

			client = new Thread(new Client(ip, port));
			client.start();

		} catch (IOException e) { System.out.println(e); }
	}
}
