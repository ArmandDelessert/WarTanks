package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIGameMain;

public class ALGameMain implements ActionListener {

    private final UIGameMain ui;
    
    public ALGameMain(UIGameMain ui) {
        this.ui = ui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "send":
                this.ui.userWantsToSpeak();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }
}
