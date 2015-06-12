/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 12.06.2015
 * 
 * Description :
 * Classe pour le stockage des informations concernant l'état du jeu.
 */

package gamemanager.stategame;

import gamemanager.player.Player;
import java.util.Date;
import network.protocol.messages.InfoPlayer;

/**
 *
 * @author Armand Delessert
 */
public class StateGame {

	public Date lastUpdate;

	public Player player1;
	public Player player2;

	// Liste des bonus sur la carte
	// Liste des obus sur la carte

	public StateGame() {}

	public StateGame(Player player1, Player player2) {

		this.player1 = player1;
		this.player2 = player2;
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public String toString() {
		return network.protocol.messages.StateGame.class.toGenericString() + " (Last update: " + this.lastUpdate.toString() + ")";
	}
}
