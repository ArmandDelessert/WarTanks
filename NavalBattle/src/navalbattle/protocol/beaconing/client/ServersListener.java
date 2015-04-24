package navalbattle.protocol.beaconing.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.messages.common.InvalidMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class ServersListener extends Observable {

    protected final int port;
    protected final int removeNoBeaconInterval;
    protected volatile boolean runThreads = true;
    protected final HashSet<DiscoveredServer> servers = new HashSet<>();

    // removeNoBeaconInterval in seconds
    public ServersListener(final int port, final int removeNoBeaconInterval) {
        this.port = port;
        this.removeNoBeaconInterval = removeNoBeaconInterval;

        // First thread is listening for beacons
        new Thread(new Runnable() {
            @Override
            public void run() {
                startListening(port, 65535);
            }
        }).start();

        // Second thread is constantly removing old discovered servers
        new Thread(new Runnable() {
            @Override
            public void run() {
                removeTimedOutServers(removeNoBeaconInterval);
            }
        }).start();
    }

    private void removeTimedOutServers(final int removeNoBeaconInterval) {
        while (this.runThreads) {
            try {
                Thread.sleep(removeNoBeaconInterval * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServersListener.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean collectionHasChanged = false;
            
            synchronized (this.servers) {
                if (!this.servers.isEmpty()) {
                    Iterator<DiscoveredServer> it = this.servers.iterator();
                    
                    while (it.hasNext()) {
                        DiscoveredServer current = it.next();
                        Date lastBeaconReceivedFromThisServer = current.GetLastBeacon();

                        if (new Date().getTime() - lastBeaconReceivedFromThisServer.getTime() > removeNoBeaconInterval) {
                            it.remove();
                            collectionHasChanged = true;
                        }
                    }
                }
            }
            
            // Notify listeners in case servers been removed
            if (collectionHasChanged) {
                this.setChanged();
                this.notifyObservers(this.GetServers());
            }
        }
    }

    public HashSet<DiscoveredServer> GetServers() {
        return new HashSet<DiscoveredServer>(this.servers);
    }

    private void startListening(final int portReceive, final int bufferSize) {
        byte[] bufferReceive = new byte[bufferSize];

        DatagramSocket serverSocket = null;
        DatagramPacket receivePacket;

        try {
            serverSocket = new DatagramSocket(port);
            //serverSocket.setReuseAddress(true);
            //serverSocket.bind(new InetSocketAddress(port));
        } catch (SocketException ex) {
            System.out.println(ex);
        }

        while (this.runThreads) {
            receivePacket = new DatagramPacket(bufferReceive, bufferReceive.length);

            try {
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                System.out.println(ex);
            }

            byte[] actualData = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
            String received = null;

            try {
                received = new String(actualData, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
            }

            Document document = null;

            try {
                document = XMLHelper.parseXMLFromString(received);
            } catch (Exception ex) {
                Logger.getLogger(ServersListener.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            Node responseNode = document.getDocumentElement();
            String messageType = null;

            try {
                messageType = responseNode.getAttributes().getNamedItem("type").getTextContent();
            } catch (Exception ex) {
            }

            if (messageType != null && messageType.equals("endpointsDiscovery")) {
                BeaconingParameters beaconParams = null;
                
                try {
                    beaconParams = ClientBeaconingHelper.parseBeaconingMessage(document);
                } catch (InvalidMessage ex) {
                }
                
                if (beaconParams != null)
                {
                    String hostName = receivePacket.getAddress().getHostAddress();

                    DiscoveredServer discovered = new DiscoveredServer(hostName, beaconParams);
                    HashSet<DiscoveredServer> notify = null;
                    
                    synchronized (this.servers) {
                        
                        Iterator<DiscoveredServer> ds = this.servers.iterator();
                        DiscoveredServer found = null;
                        
                        while (ds.hasNext())
                        {
                            DiscoveredServer current = ds.next();

                            if (current.getHostName().equals(discovered.getHostName()))
                            {
                                found = current;
                                break;
                            }
                        }
                        
                        if (found == null) {
                            // This ia a new server
                            this.servers.add(discovered);

                            notify = this.GetServers();
                        }
                        else
                        {
                            boolean hasChangedItsProperties = !(discovered.equals(found));

                            if (hasChangedItsProperties)
                            {
                                this.servers.remove(found);
                                this.servers.add(discovered);

                                notify = this.GetServers();
                            }
                        }
                    }
                    
                    // Outside the synchronized block!
                    if (notify != null)
                    {
                        this.setChanged();
                        this.notifyObservers(notify);
                    }
                }
            }
        }
    }

}
