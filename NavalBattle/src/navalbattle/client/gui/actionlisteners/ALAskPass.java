package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIAskPass;

public class ALAskPass implements ActionListener {

    private UIAskPass view;
    
    public ALAskPass(UIAskPass view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "connect":
                this.view.submitPage();
                break;
            case "cancel":
                this.view.cancel();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
