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

import navalbattle.client.gui.actionlisteners.ALManualConnect;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.models.ModelManualConnect;
import navalbattle.lang.LanguageHelper;

public class UIManualConnect extends JFrame implements Observer {
    
    private static final long serialVersionUID = -5809380707006840995L;
    
    private static final String UI_NAME = "ui_manualConnect";
    
    private final ModelManualConnect model;
    private final LanguageHelper lh;
    
    JTextField ipAdrField = new JTextField(20);
    
    public UIManualConnect(ModelManualConnect model) throws IOException {
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
        pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void addComponents(JPanel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
        
        JPanel firstLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel ipAdrText = new JLabel(this.lh.getTextDef(UI_NAME, "IP_ADDRESS"));
        ipAdrText.setLabelFor(ipAdrField);
        firstLine.add(ipAdrText);
        firstLine.add(ipAdrField);
        
        JPanel secondLine = new JPanel();
        JButtonDesign buttonConnect = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CONNECT"));
        JButtonDesign buttonCancel = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CANCEL"));
        secondLine.add(buttonConnect);
        secondLine.add(buttonCancel);
        
        firstLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pane.add(firstLine);
        pane.add(secondLine);
        
        buttonConnect.setActionCommand("connect");
        buttonCancel.setActionCommand("cancel");
        
        ActionListener al = new ALManualConnect(this);
        buttonConnect.addActionListener(al);
        buttonCancel.addActionListener(al);
        
        pane.getRootPane().setDefaultButton(buttonConnect);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (o.getClass() == ModelManualConnect.class) {
            if (arg.getClass() == NotifyMessage.class) {
                NotifyMessage message = (NotifyMessage) arg;
                
                switch (message.getType()) {
                    case CLOSE_WINDOW:
                        o.deleteObserver(this);
                        this.setVisible(false);
                        this.dispose(); // ADDED
                        //try {
                        //    this.finalize();
                        //} catch (Throwable ex) {
                        //}
                        break;
                    
                    case DISPLAY_INFO:
                        break;
                    
                    case DISPLAY_ERROR:
                        JOptionPane.showMessageDialog(null, (String)message.getData(), "", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        }
    }
    
    public void connectToServer() {
        String hostOrIP = this.ipAdrField.getText();
        this.model.connectToServer(hostOrIP);
    }
}
