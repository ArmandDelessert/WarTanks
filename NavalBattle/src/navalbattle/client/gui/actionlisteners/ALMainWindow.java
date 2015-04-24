package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import navalbattle.client.gui.UIMainWindow;

public class ALMainWindow implements ActionListener {

    private UIMainWindow ui;

    public ALMainWindow(UIMainWindow ui) {
        this.ui = ui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "config_username":
                this.ui.userWantsToEditPseudo();
                break;
            case "create":
                this.ui.userWantsToCreateAServer();
                break;
            case "stats":
                this.ui.userWantsToViewServerStats();
                break;
            case "list":
                this.ui.userWantsToListAllBroadcastingServers();
                break;
            case "quit":
                System.exit(0);
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
