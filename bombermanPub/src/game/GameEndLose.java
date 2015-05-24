package game;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

/**
 * Cette classe représente l'état d'avoir perdu => "Game Over"
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class GameEndLose extends BasicGameState{

	private Image gameLose;
	public GameEndLose(int state){
		
	}
	/**
	 * Méthode de Slick: Initialisation
	 */
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) 
			throws SlickException {
		
		gameLose = new Image("res/gameOver.png");
		
	}

	/**
	 * Méthode de Slick: Mis à jour de la fenêtre
	 */
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		g.drawImage(gameLose, 0, 0);
	}

	/**
	 * Méthode de Slick: Mis à jour des données
	 */
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		
	}

	/**
    * @return the le nombre d'état
    */
	@Override
	public int getID() {
		return 2;
	}

}
