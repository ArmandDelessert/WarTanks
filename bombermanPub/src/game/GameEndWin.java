package game;

import org.newdawn.slick.*;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.state.*;

/**
 * Cette classe représente l'état d'avoir gangé => "You Win!"
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class GameEndWin extends BasicGameState{

	private Image gameWin;
	public GameEndWin(int state){
		
	}
	/**
    * Méthode de Slick: Initialisation
    */
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) 
			throws SlickException {
		gameWin = new Image("res/gameWin.png");
	}

	/**
	 * Méthode de Slick: Mis à jour de la fenêtre
	 */
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.drawImage(gameWin, 0, 0);
	}

	/**
	 * Méthode de Slick: Mis à jour des données
	 */
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		int posX = Mouse.getX();
		int posY = Mouse.getY();
		
		if((posX > 300) && posX < 501 && (posY > 220 && posY < 300))
		{
			if(Mouse.isButtonDown(0))
			{
				sbg.enterState(1);
			}
		}	
	}

   /**
    * @return the le nombre d'état
    */
	@Override
	public int getID() {
		return 0;
	}

}
