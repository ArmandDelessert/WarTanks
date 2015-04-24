package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import navalbattle.client.gui.actionlisteners.ALUsernameConf;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.models.ModelUsernameConf;
import navalbattle.datamodel.DMClientConfig;
import navalbattle.lang.LanguageHelper;

public class UIUsernameConf extends JFrame implements Observer {

    private static final long serialVersionUID = -5809380707006840995L;

    private static final String UI_NAME = "ui_usernameConf";
    
    final ModelUsernameConf model;
    private final LanguageHelper lh;
    private JTextField nicknameField;
    
    public UIUsernameConf(ModelUsernameConf model) throws IOException {
        this.model = model;
        this.lh = this.model.getLanguageHelper();
        initUI();
    }

    private void initUI() {
        // frame settings
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        // contents
//	    JLabel pane = getBG();
        JPanel pane = new JPanel();
        setContentPane(pane);
        addComponents(pane);

        new ComponentMover(this, this.getContentPane());
        
        this.setType(javax.swing.JFrame.Type.UTILITY);
        
        DMClientConfig userParameters = this.model.getUserParameters();
        this.nicknameField.setText(userParameters.getUsername());
        
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponents(JPanel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

        JPanel firstLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nicknameText = new JLabel(this.lh.getTextDef(UI_NAME, "NEW_NICKNAME"));
        nicknameField = new JTextField(20);
        nicknameText.setLabelFor(nicknameField);
        firstLine.add(nicknameText);
        firstLine.add(nicknameField);

        JPanel secondLine = new JPanel();
        JButtonDesign buttonModify = new JButtonDesign(this.lh.getTextDef(UI_NAME, "MODIFY"));
        JButtonDesign buttonCancel = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CANCEL"));
        secondLine.add(buttonModify);
        secondLine.add(buttonCancel);

        firstLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        pane.add(firstLine);
        pane.add(secondLine);

        buttonModify.setActionCommand("modify");
        buttonCancel.setActionCommand("cancel");

        ActionListener al = new ALUsernameConf(this);
        buttonModify.addActionListener(al);
        buttonCancel.addActionListener(al);

        pane.getRootPane().setDefaultButton(buttonModify);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass() == NotifyMessage.class) {
            NotifyMessage message = (NotifyMessage) arg;

            switch (message.getType()) {
                case DISPLAY_INFO:
                    JOptionPane.showMessageDialog(null, (String)message.getData(), "", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        }
    }

    public void submitPage() {
        
        if (this.isConnectedToServer()) {
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "ERROR_CONNECTED_TO_A_SERVER_CONTENT"), this.lh.getTextDef(UI_NAME, "ERROR_CONNECTED_TO_A_SERVER_TITLE"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String pseudo = nicknameField.getText();
        
        if (pseudo.length() < 3)
        {
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "MINIMUM_LENGTH_ERROR_CONTENT"), this.lh.getTextDef(UI_NAME, "MINIMUM_LENGTH_ERROR_TITLE"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        this.model.editUserParameters(pseudo);
        this.setVisible(false);
        
       // try {
        //    this.finalize();
        //} catch (Throwable ex) {
        //}
    }

    private boolean isConnectedToServer() {
        return this.model.isConnectedToServer();
    }

    public void userClickedCancel() {
        
        this.model.userClickedCancel();
        
        this.dispose();
    }
}
