/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 31.05.2015
 * 
 * Description :
 * Classe pour le stockage des informations concernant la carte et l'état de la partie.
 */

package network.protocol.messages;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class StateMap implements Serializable {

	public InfoPlayer player1;
	public InfoPlayer player2;

	// Liste des bonus sur la carte
	// Liste des obus sur la carte

	public StateMap() {}

	public StateMap(InfoPlayer player1, InfoPlayer player2) {

		this.player1 = player1;
		this.player2 = player2;
	}
}
