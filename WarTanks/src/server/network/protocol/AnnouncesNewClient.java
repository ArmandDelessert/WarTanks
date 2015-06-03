/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.protocol;

/**
 *
 * @author Armand Delessert
 */
public class AnnouncesNewClient {

	public static int clientNumber = 1;
	public String clientName;
	public String clientColor;

	public String clientIPAddress;
	public int clientTCPPort;
	public int clientUDPPort;

	public AnnouncesNewClient(String clientIPAddress, int clientTCPPort, int clientUDPPort) {

		this.clientIPAddress = clientIPAddress;
		this.clientTCPPort = clientTCPPort;
		this.clientUDPPort = clientUDPPort;

		// Attribution d'un nom par défaut au joueur
		this.clientName = "Joueur " + clientNumber++;
	}
}
