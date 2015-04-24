package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIServerCreation;

public class ALServerCreation implements ActionListener {

    final UIServerCreation view;
    
    public ALServerCreation(UIServerCreation view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "addPass":
                break;
            case "create":
                this.view.submitPage();
                break;
            case "cancel":
                this.view.dispose();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
