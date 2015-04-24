package navalbattle.client.models;

import navalbattle.client.viewmodelmessages.ExternalIPReceived;
import navalbattle.client.viewmodelmessages.InternalIPReceived;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import navalbattle.controllers.MainController;
import navalbattle.datamodel.DMGameHistory;
import navalbattle.protocol.messages.common.NavalBattleMessage;
import navalbattle.server.GamePlaying;
import navalbattle.storage.StorageUtils;

public class ModelServerStats extends Model {

    public ModelServerStats(MainController controller) {
        super(controller);
    }
    
    public void viewStarted()
    {
        new Thread() {
            @Override
            public void run()
            {
                String ip = ip = getMyInternalIPAddr();
                setChanged();
                notifyObservers(new InternalIPReceived(ip));
                
                ip = getMyExternalIPAddr();
                setChanged();
                notifyObservers(new ExternalIPReceived(ip));
            }
        }.start();
    }

    public String getMyInternalIPAddr() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (UnknownHostException ex) {
            return null;
        }
    }
    
    public String getMyExternalIPAddr() {

        BufferedReader in = null;

        try {
            URL myIP = new URL("http://checkip.dyndns.com/");
            URLConnection yc = myIP.openConnection();
            in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));

            StringBuilder buf = new StringBuilder();
            
            String read;
            while ((read = in.readLine()) != null) 
                buf.append(read);
            
            Pattern pattern = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
            
            String pageContents = buf.toString();
            Matcher matcher = pattern.matcher(pageContents);
            if (matcher.find())
            {
                return matcher.group(0);
            }
            
            return null;
        } catch (IOException ex) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
    }

    public ArrayList<DMGameHistory> getGameHistory() {
        try {
            return StorageUtils.getServerHistory();
        } catch (Exception ex) {
            // TO-DO
            return null;
        }
    }

    public GamePlaying getCurrentGame() {
        return this.getController().getCurrentGame();
    }
}
