/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 31.05.2015
 * 
 * Description :
 * Classe pour la communication des commandes du client au serveur.
 */

package protocol;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class Command implements Serializable {

	public String cmd;

	public Command(String cmd) {

		this.cmd = cmd;
	}
}
