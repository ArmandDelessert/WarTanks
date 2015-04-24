package navalbattle.client.gui;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.models.ModelConnectServer;
import navalbattle.lang.LanguageHelper;

public class UIConnectServer implements Observer {

    private static final String UI_NAME = "ui_connectServer";

    JFrame frame;
    private final LanguageHelper lh;
    private final ModelConnectServer model;

    public UIConnectServer(ModelConnectServer model) {
        this.model = model;
        this.lh = this.model.getLanguageHelper();
        this.showWindow();
    }

    private void showWindow() {
        frame = new JFrame(this.lh.getTextDef(UI_NAME, "WINDOW_TITLE"));

        JLabel label = new JLabel(this.lh.getTextDef(UI_NAME, "CONNECTING"));
        frame.add(label);

        frame.setType(javax.swing.JFrame.Type.UTILITY);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void showError(String error) {
        JOptionPane.showMessageDialog(null, error);
        
        try {
            this.frame.setVisible(false);
            this.frame.dispose(); // ADDED
        } catch (Throwable ex) {
        }
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass() == NotifyMessage.class) {
            NotifyMessage message = (NotifyMessage) arg;

            switch (message.getType()) {
                case DISPLAY_INFO:
                    this.showInfo((String) message.getData());
                    break;

                case DISPLAY_ERROR:
                    this.showError((String) message.getData());
                    break;

                case CLOSE_WINDOW:
                    WindowEvent wev = new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING);
                    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
                    break;
            }
        }
    }
}
