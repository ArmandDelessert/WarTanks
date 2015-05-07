/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 * 
 * Description :
 * Test du jeu.
 */

package wartanks;

import gamemanager.GameManager;

/**
 *
 * @author Armand
 */
public class WarTanks {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		new GameManager().run();
	}
}
