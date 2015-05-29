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
	public int numero;
	public String nomJoueur;

	// Info réseau
	public String ip;
	public int numeroPort;

	/**
	 * 
	 * @param numero
	 * @param nomJoueur
	 * @param ip
	 * @param numeroPort 
	 */
	public InfoClient(int numero, String nomJoueur, String ip, int numeroPort) {

		this.numero = numero;
		this.nomJoueur = nomJoueur;

		this.ip = ip;
		this.numeroPort = numeroPort;
	}
}
