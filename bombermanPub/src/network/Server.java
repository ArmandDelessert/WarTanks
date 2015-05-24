package network;

import java.io.IOException;
import java.net.*;

import role.ServerRole;

/**
 * Cette classe représente un serveur réseau
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Server extends NetworkComponent {
   
   private ServerSocket serverSocket;
   private Thread thread;
   private ServerRole serverRole; 
   
   public Server(ServerRole serverRole) {
      this.serverRole = serverRole;
   }
   
   /**
    * Cette méthode permet de démarrer le serveur
    * @param port indique le numéro de port pour le serveur
    */
   public void startServer(int port) {

      try {
         serverSocket = new ServerSocket(port);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      thread = new Thread(this);
      thread.start();
      
      System.out.println("Server started");

   }
   
   /**
    * Cette méthode gère les nouvelles connextions des clients
    */
   public void run() {
      while (true) {
         try {
            Socket socket = serverSocket.accept();
            super.getConnections().add(new Connection(socket, serverRole));
            
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

}