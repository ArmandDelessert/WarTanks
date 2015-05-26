package game;

import java.util.Timer;
import java.util.TimerTask;

import org.newdawn.slick.*;

/**
 * Cette classe représente une bombe dans le jeu
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Bomb extends TimerTask {

   public Image bomb;
	private int timeLeft;
	private int radius;
	private Player creator;
	private boolean exploded;
	private int x;
	private int y;
	private boolean planted;
	private Timer timer;
	
	public Bomb(Image image, int timeLeft, int radius, Player creator, int x, int y)
	{
		this.timeLeft = timeLeft;
		this.radius = radius;
		this.creator = creator;
		this.exploded = false;
		this.planted = false;
		this.x = x;
		this.y = y;
		this.bomb = image;
		this.timer = new Timer();
      this.timer.schedule(this, 3000);

	}
	
	/**
	 * Cette méthode détècte si le temps est découlé pour laisser exploser la bombe
	 */
	public void update()
	{
		if(timeLeft < 1)
			explode();
	}
	
	/**
	 * Cette méthode gère l'explosion de la bombe
	 */
	public void explode()
	{
		if(creator != null)
		{
			creator.setBombAmt(creator.getBombAmt() + 1);
		}
		exploded = true;
	}
	
	/**
	 * @return the timeLeft
	 */
	public int getTimeLeft()
	{
	   return timeLeft;
	}
	
   /**
    * @param t the timeLeft to set
    */
	public void setTimeLeft(int t){
	   timeLeft = t;
	}
	
	/**
    * @return the exploded
    */
	public boolean getExploded() {
	   return exploded;
	}
	
	/**
    * @return the radius
    */
	public int getRadius() {
	   return radius;
	   }
	
	/**
    * @return the x
    */
	public int X() {
	   return x;
	}
	
	/**
    * @return the y
    */
	public int Y() {
	   return y;
	}
	
	/**
    * @return the planted
    */
	public boolean isPlanted() {
	   return planted;
	}
	
	/**
	 * @param b the planted to set
	 */
	public void setPlanted(boolean b) {
	   planted = b;
	}

	@Override
	public void run() {
		
		this.exploded = true;
		this.explode();
	}

}