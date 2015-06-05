/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 31.05.2015
 * 
 * Description :
 * Classe pour le stockage des informations concernant la carte et l'état de la partie.
 */

package protocol.messages;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class StateMap implements Serializable {

//public Map map;
	public InfoPlayer player1;
	public InfoPlayer player2;

	public StateMap() {}

	public StateMap(InfoPlayer player1, InfoPlayer player2) {

		this.player1 = player1;
		this.player2 = player2;
	}
}
