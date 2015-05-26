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
public class AcceptNewClient {

	public boolean clientAccepted;

	public AcceptNewClient(boolean isClientAccepted) {

		clientAccepted = isClientAccepted;
	}
}
