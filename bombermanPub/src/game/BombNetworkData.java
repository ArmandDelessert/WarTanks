package game;

import java.io.Serializable;

/**
 * Cette classe permet de transmettre les données nécessaires d'une bombe
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class BombNetworkData implements Serializable {

   private static final long serialVersionUID = 1L;
   private int radius;
   private int x;
   private int y;
   
   public BombNetworkData(int radius, int x, int y) {
      this.radius = radius;
      this.x = x;
      this.y = y;
   }
   

   /**
    * @return the x
    */
   public int getX() {
      return x;
   }

   /**
    * @return the y
    */
   public int getY() {
      return y;
   }

   /**
    * @return the radius
    */
   public int getRadius() {
      return radius;
   }

   
   
}
