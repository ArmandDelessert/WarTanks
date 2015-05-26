package role;

import network.Client;
import startMenu.Model;


/**
 * Cette class représente le rôle du client pour le joueur
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class ClientRole extends Role {

   Client client;
    
   public ClientRole(Model model) {
      super(model);
   }
   
   /**
    * Cette méthode permet de se connecter à un serveur
    * @param serverIp
    * @param serverPort
    */
   public void connectServer(String serverIp, Integer serverPort) {
      client = new Client(this);
      client.connect(serverIp, serverPort);
      super.setConnected(true);
      this.getModel().setScreen("connecté");
   }
   
   /* (non-Javadoc)
    * @see role.Role#send()
    */
   @Override
   public synchronized void send(Object message) {
      client.sendMessage(message);
   }
   
   /* (non-Javadoc)
    * @see role.Role#amITheServer()
    */
   public boolean amITheServer() {
      return false; 
   }
}
