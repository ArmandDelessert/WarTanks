/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 12.05.2015
 * 
 * Description :
 * Classe pour le stockage des informations concernant un client.
 */

package protocol;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class InfoClient implements Serializable {

	// Infos de connexion
	public String ipAddress;
	public int portNumber;

	/**
	 * 
	 * @param ipAddress
	 * @param portNumber 
	 */
	public InfoClient(String ipAddress, int portNumber) {

		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}
}
