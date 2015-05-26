package game;

import org.newdawn.slick.*;

/**
 * 
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Player {
   
   public Image hero_down;
   public Image hero_up;
   public Image hero_left;
   public Image hero_right;
   private int orientation; // 0 up, 1 left, 2 down, 3 right
   private int posX;
   private int posY;
   private int number;
   
   public boolean alive;
   private int firePower;
   private int bombAmt;

   
    public Player(Image[] image, int x, int y, int number)
    {
       
      hero_down = image[0];
      hero_up = image[1];
      hero_left = image[2];
      hero_right = image[3];
      posX = x;
      posY = y;
      orientation = 0;
      alive = true;
      firePower = 2;
      bombAmt = 1;
      this.number = number;
      
      
    }
    
    /**
     * @return the timeLeft
     */
    public int X() {
       return posX;
    }
    
    /**
     * @return the posY
     */
    public int Y() {
       return posY;
    }
    
    /**
     * @param x the x to set
     */
    public synchronized void setX(int x) {
       posX = x;
    }
    
    /**
     * @param y the y to set
     */
    public synchronized void setY(int y) {
       posY = y;
    }
    
    /**
     * @return the bombAmt
     */
    public int getBombAmt(){
       return bombAmt;
    }
    
    /**
     * @param a the bombAmt to set
     */
    public synchronized void setBombAmt(int a){
       bombAmt = a;
    }
    
    /**
     * @return the firePower
     */
    public int getFirePower() {
       return firePower;
    }
    
    /**
     * @param fp the firePower to set
     */
    public synchronized void setFirePower(int fp) {
       firePower = fp;
    }
    
    /**
     * @return the orientation
     */
    public int getOrientation() {
       return orientation;
    }
    
    /**
     * @return the number
     */
    public int getNumber() {
       return number;
    }
    
    /**
     * @param n the number to set
     */
    public synchronized void setNumber(int n) {
       number = n;
    }
    
    /**
     * @param o the orientation to set
     */
    public synchronized void setOrientation(int o) {
       orientation = o;
    }
    
    /**
     * @param alive the alive to set
     */
    public synchronized void setAlive(boolean alive) {
       this.alive = alive;
    }
    
    /**
     * Méthode appelé par le récepteur de données du réseau qui met à jour le joueur
     * @param networkData
     */
    public void networkUpdate(PlayerNetworkData networkData) {
       this.setX(networkData.getPosX());
       this.setY(networkData.getPosY());
       this.setAlive(networkData.isAlive());
       this.setBombAmt(networkData.getBombAmt());
       this.setFirePower(networkData.getFirePower());
       this.setOrientation(networkData.getOrientation());
       this.setNumber(networkData.getNumber());
    }   
}