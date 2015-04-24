package navalbattle.client.gui.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import navalbattle.client.gui.UIServerStats;

public class ALServerStats implements ActionListener {

    final UIServerStats view;
    
    public ALServerStats(UIServerStats view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "back":
                view.dispose();
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

}
