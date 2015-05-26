package game;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import role.Role;

/**
 * Cette méthode démarre une session du jeu
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Game extends StateBasedGame {

	public static final String gameName = "Bomberman 2013";
	
	public static final int gameEndWin   = 0;
	public static final int play         = 1;
	public static final int gameEndLose  = 2;
	
	private Play playSession;

	
	public Game(String gameName, Role networkAccess)
	{
		super(gameName); //Titre du jeu
		playSession = new Play(play, networkAccess);
		this.addState(playSession);
		this.addState(new GameEndLose(gameEndLose));
		this.addState(new GameEndWin(gameEndWin));
	}
	/**
	 * Méthode de Slick: Initialisation
	 */
	public void initStatesList(GameContainer gc) throws SlickException{
		this.getState(play).init(gc, this);
		this.getState(gameEndLose).init(gc, this);
		this.getState(gameEndWin).init(gc, this);
		this.enterState(play); //Permet de choisir le premier state à afficher
				
	}

	/**
    * @return the playSession
    */
	public Play getPlaySession() {
	   return playSession;
	}
	
}