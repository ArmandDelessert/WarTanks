/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package server.projectile;

import server.map.Direction;
import server.map.Map;
import server.map.Movable;


/**
 *
 * @author Armand
 */
public abstract class Projectile implements Movable {
	private Map map;
	protected int posX;
	protected int posY;
	protected Direction direction;

	/*** Constructeurs ***/

	/**
	 * Création d'un projectile.
	 * 
	 * @param map
	 * @param posX
	 * @param posY
	 * @param direction 
	 */
	public Projectile(Map map, int posX, int posY, Direction direction) {
		this.map = map;
		this.posX = posX;
		this.posY = posY;
		this.direction = direction;
	}

	/*** Getters ***/

	public int getPosX() {return this.posX;}
	public int getPosY() {return this.posY;}
	public Direction getDirection() {return this.direction;}

	/*** Méthodes ***/

	/**
	 * Déplacer le projectile dans l'axe X sur la carte.
	 * 
	 * @param moveX 
	 */
	public void moveX(int moveX) {
		if (this.posX + moveX >= 0 && this.posX + moveX < map.getSizeX()) {
			this.posX += moveX;
		}
	}

	/**
	 * Déplacer le projectile dans l'axe Y sur la carte.
	 * 
	 * @param moveY 
	 */
	public void moveY(int moveY) {
		if (this.posY + moveY >= 0 && this.posY + moveY < map.getSizeY()) {
			this.posY += moveY;
		}
	}

	/**
	 * Tourner le projectile sur lui-même.
	 * 
	 * @param direction 
	 */
	public void rotate(Direction direction) {
		this.direction = direction;
	}
}
