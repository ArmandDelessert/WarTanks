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

	// Info du joueur
	public int id;

	// Infos de connexion
	public String ip;
	public int numeroPort;

	/**
	 * 
	 * @param id
	 * @param ip
	 * @param numeroPort 
	 */
	public InfoClient(int id, String ip, int numeroPort) {

		this.id = id;

		this.ip = ip;
		this.numeroPort = numeroPort;
	}
}
