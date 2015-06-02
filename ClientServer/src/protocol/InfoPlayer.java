/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 31.05.2015
 * 
 * Description :
 * Classe pour le stockage des informations concernant un joueur.
 */

package protocol;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class InfoPlayer implements Serializable {

	// Info du joueur
	public int id;
	public String name;
	public String color;

	public int nbLives;
	public int nbHealthPoints;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param color 
	 */
	public InfoPlayer(int id, String name, String color) {

		this.id = id;
		this.name = name;
		this.color = color;
	}
}
