/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package vehicle;

import map.Map;
import projectile.Bullet;


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
	public Tank(String name, Map map, int posX, int posY, String direction) {
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