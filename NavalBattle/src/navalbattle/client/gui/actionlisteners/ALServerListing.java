package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIServersListing;

public class ALServerListing implements ActionListener {

    final UIServersListing view;
    
    public ALServerListing(UIServersListing view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "manual":
                this.view.userWantsToManuallySpecifiyToServer();
                break;
            case "connect":
                this.view.connectToSelectedServer();
                break;
            case "cancel":
                this.view.dispose();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
