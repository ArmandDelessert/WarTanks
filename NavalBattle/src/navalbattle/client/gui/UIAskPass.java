package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import navalbattle.client.gui.actionlisteners.ALAskPass;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.models.ModelAskPass;
import navalbattle.lang.LanguageHelper;

public class UIAskPass extends JFrame implements Observer {

    private static final long serialVersionUID = 3263451715909934219L;

    private final String UI_NAME = "ui_askPass";

    private final ModelAskPass model;
    private final LanguageHelper lh;

    private JPasswordField passField;

    public UIAskPass(ModelAskPass model) {

        this.model = model;
        this.lh = this.model.getLanguageHelper();
        initUI();
    }

    private void initUI() {
        // frame settings
        setResizable(false);
        setUndecorated(true);

        // contents
//	    JLabel pane = getBG();
        JPanel pane = new JPanel();
        setContentPane(pane);
        addComponents(pane);

        new ComponentMover(this, this.getContentPane());

        this.setType(javax.swing.JFrame.Type.UTILITY);
        
        pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addComponents(JPanel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

        JPanel firstLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel firstLineText = new JLabel(this.lh.getTextDef(UI_NAME, "SERVER_NEEDS_PASS"));
        firstLine.add(firstLineText);

        JPanel secondLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passText = new JLabel(this.lh.getTextDef(UI_NAME, "PASSWORD"));
        passField = new JPasswordField(20);
        passText.setLabelFor(passField);
        secondLine.add(passText);
        secondLine.add(passField);

        JPanel thirdLine = new JPanel();
        JButtonDesign buttonConnect = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CONNECT"));
        JButtonDesign buttonCancel = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CANCEL"));
        thirdLine.add(buttonConnect);
        thirdLine.add(buttonCancel);

        firstLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        thirdLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        pane.add(firstLine);
        pane.add(secondLine);
        pane.add(thirdLine);

        buttonConnect.setActionCommand("connect");
        buttonCancel.setActionCommand("cancel");

        ActionListener al = new ALAskPass(this);
        buttonConnect.addActionListener(al);
        buttonCancel.addActionListener(al);

        pane.getRootPane().setDefaultButton(buttonConnect);
    }

    public void submitPage() {
        final String password = new String(this.passField.getPassword());

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "ERROR_NO_PASSWORD_SPECIFIED"), this.lh.getTextDef(UI_NAME, "ERROR"), JOptionPane.ERROR_MESSAGE);
        } else {
            this.model.connectToServer(password);
        }
    }

    public void cancel() {
        
        this.setVisible(false);
        this.dispose(); // ADDED

        //try {
        //    this.finalize();
        //} catch (Throwable ex) {
        //}
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass() == NotifyMessage.class) {
            NotifyMessage casted = (NotifyMessage) arg;

            switch (casted.getType()) {
                case CLOSE_WINDOW:
					this.setVisible(false);
				
                    //try {
                    //    
                    //    this.finalize();
                    //} catch (Throwable ex) {
                    //}
                    break;
            }
        }
    }
}
