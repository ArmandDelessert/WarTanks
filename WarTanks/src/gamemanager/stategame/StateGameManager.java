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
import network.protocol.messages.StateGame;

/**
 *
 * @author Armand Delessert
 */
public class StateGameManager {

	public Date lastUpdate;
	public boolean gameEnd = false;

	public int[][] map;

	public Player player1;
	public Player player2;

	// Liste des bonus sur la carte
	// Liste des obus sur la carte

	/**
	 * 
	 */
	public StateGameManager() {
		this.map = new int[32][32];
	}

	/**
	 * 
	 * @param player1
	 * @param player2 
	 */
	public StateGameManager(Player player1, Player player2) {

		this.map = new int[32][32];

		this.player1 = player1;
		this.player2 = player2;
	}

	public StateGame getStateGame() {

		StateGame stateGame = new StateGame(
			new InfoPlayer(this.player1.id, this.player1.name, this.player1.positionX, this.player1.positionY, this.player1.direction),
			new InfoPlayer(this.player2.id, this.player2.name, this.player2.positionX, this.player2.positionY, this.player2.direction)
		);
		stateGame.gameEnd = this.gameEnd;
		return stateGame;
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
