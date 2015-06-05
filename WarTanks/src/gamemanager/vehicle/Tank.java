/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package gamemanager.vehicle;

import gamemanager.map.Direction;
import gamemanager.map.Map;
import gamemanager.projectile.Bullet;


/**
 *
 * @author Armand
 */
public class Tank extends Vehicle {

	/*** Constructeurs ***/

	/**
	 * Création d'un tank.
	 * 
	 * @param name
	 * @param map
	 * @param posX
	 * @param posY
	 * @param direction 
	 */
	public Tank(String name, Map map, int posX, int posY, Direction direction) {
		super(name, map, posX, posY, direction);
	}

	/*** Méthodes ***/

	/**
	 * Faire tirer le tank.
	 * 
	 * @return 
	 */
	public Bullet tire() {
		return new Bullet(this.map, this.posX, this.posY, this.direction);
	}
}
