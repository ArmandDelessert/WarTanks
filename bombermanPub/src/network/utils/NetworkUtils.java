package network.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Cette classe met à disposition certaine méthodes générales qui concernent la partie réseau
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class NetworkUtils {
   
   /**
    * Cette classe permet de récupérer l'adress IP de l'interface principale
    * (celui qui est utilisé par la route par défaut)
    * @return
    */ 
   public static String getLocalIp() {
	   Enumeration<NetworkInterface> interfaces;
      String ipAddress = null;
      try {
         interfaces = NetworkInterface.getNetworkInterfaces();
   	   while (interfaces.hasMoreElements()){
   		   NetworkInterface current = interfaces.nextElement();
   		   if (!current.isUp() || current.isLoopback() || current.isVirtual() || current.getDisplayName().contains("Virtual")) continue;
   		      Enumeration<InetAddress> addresses = current.getInetAddresses();
   		      InetAddress current_addr = addresses.nextElement();
   		      if (current_addr instanceof Inet4Address)
   		         ipAddress =  current_addr.getHostAddress();
   		      if (current_addr.isLoopbackAddress()) continue;
   		    }
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
      return ipAddress;
   }

}
