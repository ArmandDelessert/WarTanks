package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIUsernameConf;

public class ALUsernameConf implements ActionListener {

    final UIUsernameConf view;
    
    public ALUsernameConf(UIUsernameConf view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "modify":
                this.view.submitPage();
                break;
            case "cancel":
                this.view.userClickedCancel();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
