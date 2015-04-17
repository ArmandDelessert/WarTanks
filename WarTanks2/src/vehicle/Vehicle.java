/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package vehicle;

import map.Map;
import map.Movable;


/**
 *
 * @author Armand
 */
public abstract class Vehicle implements Movable {
	protected String name;
	protected Map map;
	protected int posX;
	protected int posY;
	protected String direction;

	/*** Constructeurs ***/

	/**
	 * Création d'un véhicule.
	 * 
	 * @param name
	 * @param map
	 * @param posX
	 * @param posY
	 * @param direction 
	 */
	public Vehicle(String name, Map map, int posX, int posY, String direction) {
		this.name = name;
		this.map = map;
		this.posX = posX;
		this.posY = posY;
		this.direction = direction;
	}

	/*** Getters ***/

	public String getName() {return this.name;}
	public int getPosX() {return this.posX;}
	public int getPosY() {return this.posY;}
	public String getDirection() {return this.direction;}

	/*** Méthodes ***/

	/**
	 * Afficher le véhicule.
	 * 
	 * @return 
	 */
	@Override
	public String toString() {
		return "O";
	}

	/**
	 * Afficher les infos sur le véhicule.
	 * 
	 * @return 
	 */
	public String getInfo() {
		return new String(this.name + " (" + Integer.toString(posX) + "; " + Integer.toString(posY) + "; " + direction + ")");
	}

	/**
	 * Déplacer le véhicule dans l'axe X sur la carte.
	 * 
	 * @param moveX 
	 */
	public void moveX(int moveX) {
		// Changement de direction du véhicule
		if (moveX > 0) {
			this.direction = "est";
		}
		else if (moveX < 0) {
			this.direction = "west";
		}
		// Déplacement du véhicule
		if (this.posX + moveX >= 0 && this.posX + moveX < map.getSizeX()) {
			this.posX += moveX;
		}
	}

	/**
	 * Déplacer le véhicule dans l'axe Y sur la carte.
	 * 
	 * @param moveY 
	 */
	public void moveY(int moveY) {
		// Changement de direction du véhicule
		if (moveY > 0) {
			this.direction = "north";
		}
		else if (moveY < 0) {
			this.direction = "south";
		}
		// Déplacement du véhicule
		if (this.posY + moveY >= 0 && this.posY + moveY < map.getSizeY()) {
			this.posY += moveY;
		}
	}

	/**
	 * Tourner le véhicule sur lui-même.
	 * 
	 * @param direction 
	 */
	public void rotate(String direction) {
		this.direction = direction;
	}
}
