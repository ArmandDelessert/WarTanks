package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIManualConnect;

public class ALManualConnect implements ActionListener {

    final UIManualConnect view;
    
    public ALManualConnect(UIManualConnect view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "connect":
                this.view.connectToServer();
                break;
            case "cancel":
                this.view.dispose();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
