package game;

import java.util.Timer;
import java.util.TimerTask;

import org.newdawn.slick.*;

/**
 * Cette classe représente le feu après l'explosion de la bombe
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Fire extends TimerTask {

	public Image fire;
	private int timeLeft;
	private int radius;
	private boolean boom;
	private int x;
	private int y;
	private Timer timer;
	
	public Fire(Image image, int timeLeft, int radius,  
			int x, int y)
	{
		this.timeLeft = timeLeft;
		this.radius = radius;
		this.boom = false;
		this.x = x;
		this.y = y;
		this.fire = image;
		this.timer = new Timer();
        this.timer.schedule(this, 3000);
	}
	
	/**
	 * Cette méthode gère le cycle de vie de l'explosion (la durée du feu)
	 */
	public void update()
	{
		if(timeLeft < 1)
			boom = true;
	}

	/**
    * @return the timeLeft
    */
	public int getTimeLeft() {
	   return timeLeft;
	}
	
	/**
    * @param t the timeLeft to set
    */
	public void setTimeLeft(int t) {
	   timeLeft = t;
	}
	
	/**
    * @return the boom
    */
	public boolean getBoom() {
	   return boom;
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

	@Override
	public void run() {
		
		this.boom = true;
	}
}