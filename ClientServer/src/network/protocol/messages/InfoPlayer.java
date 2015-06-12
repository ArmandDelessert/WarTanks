/**
 * Projet : ClientServer
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

	public final static int maxPlayer = 2;

	// Info générales du joueur
	public int id;
	public String name;
	public ColorPlayer colorPlayer;

	// Info sur le tank du joueur
	public int nbLives;
	public int nbHealthPoints;

	// Info sur la position du joueur sur la carte
	public int positionX;
	public int positionY;
	public int direction;

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

	/**
	 * 
	 * @param id
	 * @param name
	 * @param positionX
	 * @param positionY
	 * @param direction
	 */
	public InfoPlayer(int id, String name, int positionX, int positionY, int direction) {

		this.id = id;
		this.name = name;
		this.colorPlayer = ColorPlayer.getColor(id);

		this.positionX = positionX;
		this.positionY = positionY;
		this.direction = direction;
	}

	@Override
	public String toString() {

		return String.valueOf(this.id) + " " + this.name + " [" + this.colorPlayer + "]";
	}

	public enum ColorPlayer {

		NO_COLOR	(0, "no color"),
		BLUE			(1,	"blue"),
		RED				(2,	"red");

		private final int id;
		private final String name;

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
