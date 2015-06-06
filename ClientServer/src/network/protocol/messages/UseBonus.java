/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 06.06.2015
 * 
 * Description :
 * Classe pour la communication de l'utilisation des bonus.
 */

package network.protocol.messages;

/**
 *
 * @author Armand Delessert
 */
public class UseBonus extends Command {

	private final int type;

	public UseBonus(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
