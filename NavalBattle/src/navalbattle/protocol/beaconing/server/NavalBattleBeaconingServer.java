package navalbattle.protocol.beaconing.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.xml.transform.TransformerException;
import navalbattle.protocol.common.IRequestServerParamsForBeaconing;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.common.NavalBattleProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmlhelper.XMLHelper;

public class NavalBattleBeaconingServer {

    private final IRequestServerParamsForBeaconing server;
    private volatile boolean stop = false;
    private Thread beaconing = null;
    private Object notify = new Object();

    public NavalBattleBeaconingServer(IRequestServerParamsForBeaconing server) {
        this.server = server;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (this.isBroadcasting()) {
            this.stopBroadcasting();
        }
    }

    /**
     * Return the status of the beaconing
     * @return boolean true if the server is broadcasting, false if it isn't
     */

    public boolean isBroadcasting() {
        return (this.beaconing != null);
    }
    
    /**
     * Stops the broadcasting process
     */

    public void stopBroadcasting() {
        if (!this.isBroadcasting()) {
            return;
        }

        stop = true;

        if (this.beaconing != null && this.beaconing.isAlive()) {
            synchronized (notify) {
                notify.notify();
            }
        }
    }

    /**
     * Starts the broadcasting process
     */
    
    public synchronized void startBroadcasting() {
        if (this.isBroadcasting()) {
            return;
        }

        stop = false;

        this.beaconing = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!stop) {
                        sendBeacon();
                        synchronized (notify) {
                            notify.wait(NavalBattleProtocol.BEACONING_INTERVAL * 1000);
                        }
                    }
                } catch (InterruptedException ex) {
                }
            }
        });

        this.beaconing.start();
    }

    /**
     * Sends the periodic beacon
     * Is not an active method
     */
    
    private void sendBeacon() {
        // Gathering beacon parameters from the server
        BeaconingParameters beaconParameters = this.server.getServerParamsForBeaconing();

        Document doc = ServerBeaconingHelper.getBeaconingMessage(beaconParameters);

        DatagramSocket sendSocket = null;
        String data = null;

        try {
            data = XMLHelper.prettyPrintXML(doc, NavalBattleProtocol.ENCODING);
        } catch (TransformerException ex) {
        }

        try {
            sendSocket = new DatagramSocket();
            sendSocket.setBroadcast(true);
            //sendSocket.setReuseAddress(true);
        } catch (SocketException ex) {
        }

        byte[] sendData = null;

        try {
            sendData = data.getBytes(NavalBattleProtocol.ENCODING);
        } catch (UnsupportedEncodingException ex) {
        }

        DatagramPacket sendPacket = null;

        try {
            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), NavalBattleProtocol.BEACONING_PORT);
        } catch (UnknownHostException ex) {
        }

        try {
            sendSocket.send(sendPacket);
        } catch (IOException ex) {
        }

        sendSocket.close();
    }
}
