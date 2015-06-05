/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 31.05.2015
 * 
 * Description :
 * Classe pour le stockage des informations concernant un joueur.
 */

package network.protocol.messages;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class InfoPlayer implements Serializable {

	// Info du joueur
	public int id;
	public String name;
	public ColorPlayer colorPlayer;

	public int nbLives;
	public int nbHealthPoints;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param color
	 */
	public InfoPlayer(int id, String name, ColorPlayer color) {

		this.id = id;
		this.name = name;
		this.colorPlayer = ColorPlayer.getColor(id);
	}

	@Override
	public String toString() {

		return String.valueOf(this.id) + " " + this.name + " [" + this.colorPlayer.toString() + "]";
	}

	public enum ColorPlayer {

		BLUE	(1,	"blue"),
		RED		(2,	"red");

		private int id;
		private String name;

		private ColorPlayer(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public static ColorPlayer getColor(int id) {
			switch (id) {
				case 1:
					return ColorPlayer.BLUE;
				case 2:
					return ColorPlayer.RED;
				default:
					return null;
			}
		}
	}
}
