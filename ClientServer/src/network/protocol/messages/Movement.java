/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 06.06.2015
 * 
 * Description :
 * Classe pour la communication des déplacements.
 */

package network.protocol.messages;

/**
 *
 * @author Armand Delessert
 */
public class Movement extends Command {

	private final int direction;

	public Movement(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}
}
