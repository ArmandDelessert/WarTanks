/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package map;


/**
 * 
 * @author Armand
 */
public interface Movable {

	/**
	 * Déplacer l'objet dans l'axe X sur la carte.
	 * 
	 * @param moveX 
	 */
	public void moveX(int moveX);

	/**
	 * Déplacer l'objet dans l'axe Y sur la carte.
	 * 
	 * @param moveY 
	 */
	public void moveY(int moveY);

	/**
	 * Rotation de l'objet sur lui-même.
	 * 
	 * @param direction 
	 */
	public void rotate(String direction);
}
