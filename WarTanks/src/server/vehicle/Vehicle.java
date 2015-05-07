/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package vehicle;

import map.Direction;
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
	protected Direction direction;

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
	public Vehicle(String name, Map map, int posX, int posY, Direction direction) {
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
	public Direction getDirection() {return this.direction;}

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
			this.direction = Direction.east;
		}
		else if (moveX < 0) {
			this.direction = Direction.west;
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
			this.direction = Direction.north;
		}
		else if (moveY < 0) {
			this.direction = Direction.south;
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
	public void rotate(Direction direction) {
		this.direction = direction;
	}
}
