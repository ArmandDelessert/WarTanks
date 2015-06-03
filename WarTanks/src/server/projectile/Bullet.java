/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package server.projectile;

import server.map.Direction;
import server.map.Map;


/**
 *
 * @author Armand
 */
public class Bullet extends Projectile {

	/*** Constructeurs ***/

	/**
	 * Création d'un obu.
	 * 
	 * @param map
	 * @param posX
	 * @param posY
	 * @param direction 
	 */
	public Bullet(Map map, int posX, int posY, Direction direction) {
		super(map, posX, posY, direction);
	}

	/*** Méthodes ***/

	
}
