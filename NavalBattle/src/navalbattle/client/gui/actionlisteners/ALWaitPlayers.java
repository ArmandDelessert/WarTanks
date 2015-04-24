package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIWaitPlayers;

public class ALWaitPlayers implements ActionListener {

    final UIWaitPlayers view;

    public ALWaitPlayers(UIWaitPlayers view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "disconnect":
                this.view.userWantsToDisconnect();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
