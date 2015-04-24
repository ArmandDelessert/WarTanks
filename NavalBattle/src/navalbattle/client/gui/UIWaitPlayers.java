package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import navalbattle.client.gui.actionlisteners.ALWaitPlayers;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.models.ModelWaitPlayers;
import navalbattle.lang.LanguageHelper;

public class UIWaitPlayers extends JFrame implements Observer {

    private static final long serialVersionUID = -739742586451537622L;

    private static final String UI_NAME = "ui_waitPlayers";

    private final String serverName;
    private final String IPAdress;
    
    private final ModelWaitPlayers model;
    private final LanguageHelper lh;
    
    public UIWaitPlayers(ModelWaitPlayers model, String serverName, String IPAdress) throws IOException {
        this.model = model;
        this.lh = this.model.getLanguageHelper();
        this.serverName = serverName;
        this.IPAdress = IPAdress;
        initUI();
    }

    private void initUI() {
        // frame settings
        setResizable(false);
        setUndecorated(true);

        // contents
        JPanel pane = new JPanel();
        setContentPane(pane);
        addComponents(pane);

        new ComponentMover(this, this.getContentPane());

        this.setType(javax.swing.JFrame.Type.UTILITY);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponents(JPanel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

        JPanel firstLine = new JPanel();
        JLabel firstLineText = new JLabel(this.lh.getTextDef(UI_NAME, "YOU_ARE_CONNECTED_TO") + " " + serverName + " " + this.lh.getTextDef(UI_NAME, "ON") + " " + IPAdress);
        firstLine.add(firstLineText);

        JPanel secondLine = new JPanel();
        JLabel secondLineText = new JLabel(this.lh.getTextDef(UI_NAME, "PLEASE_WAIT"));
        secondLine.add(secondLineText);

        JPanel thirdLine = new JPanel();
        JButtonDesign buttonDisconnect = new JButtonDesign(this.lh.getTextDef(UI_NAME, "DISCONNECT"));
        thirdLine.add(buttonDisconnect);

        firstLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        secondLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonDisconnect.setAlignmentX(Component.CENTER_ALIGNMENT);

        pane.add(firstLine);
        pane.add(secondLine);
        pane.add(thirdLine);

        buttonDisconnect.setActionCommand("disconnect");

        ALWaitPlayers al = new ALWaitPlayers(this);
        buttonDisconnect.addActionListener(al);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass() == NotifyMessage.class)
        {
            NotifyMessage messageCasted = (NotifyMessage)arg;
            
            if (messageCasted.getType() == NotifyMessage.Type.CLOSE_WINDOW)
            {
                this.setVisible(false);
                
                this.dispose(); // ADDED
                //try {
                //    this.finalize();
                //} catch (Throwable ex) {
                //}
            }
        }
    }

    public void userWantsToDisconnect() {
        this.model.userWantsToDisconnect();
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }
}
