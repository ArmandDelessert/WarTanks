package network;

/**
 * Interface qui met à disposition une méthode qui reçoit les messages du réseau
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public interface Receiver {
   
   /**
    * Cette méthode est appelé par l'objet Connection dans le moment où un message
    * arrive à travers du réseau.
    * C'est un interface du style java.util.Observer 
    * @param message
    */
   public void recieve(Object message);
}
 