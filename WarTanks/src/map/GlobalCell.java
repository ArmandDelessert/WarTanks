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
public class GlobalCell {

	/*** Constructeurs ***/

	private GlobalCell() {}

	/*** Cases libres ***/

	public static Cell freeCell = new FreeCell();
	public static Cell brokenWall = new BrokenWall();
	public static Cell blackSpot = new BlackSpot();

	/*** Obstacles cassables ***/

	public static Cell breakableWall = new BreakableWall();
	public static Cell wreck = new Wreck();

	/*** Obstacles incassables ***/

	public static Cell unbreakableWall = new UnbreakableWall();
}
