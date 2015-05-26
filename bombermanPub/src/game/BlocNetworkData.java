package game;

import java.io.Serializable;

/**
 * Cette classe permet de transmettre les données nécessaires de la matrice
 * qui contient les blocs destructibles et indestructibles 
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class BlocNetworkData implements Serializable {

   private static final long serialVersionUID = 1L;
   private int[][] bloc;
   
   public BlocNetworkData(int[][] bloc) {
      this.bloc = bloc; 
   }

   /**
    * @return the bloc
    */
   public int[][] getBloc() {
      return bloc;
   }


}
