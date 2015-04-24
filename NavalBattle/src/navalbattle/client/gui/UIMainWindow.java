package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import navalbattle.client.gui.actionlisteners.ALMainWindow;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.customcomponents.JFrameWithBGimg;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.models.ModelMainWindow;
import navalbattle.lang.LanguageHelper;

public class UIMainWindow extends JFrameWithBGimg implements Observer {

    private static final long serialVersionUID = 1433671331229689918L;

    private static final String UI_NAME = "ui_mainWindow";

    private JButtonDesign buttonCreate;

    private final ModelMainWindow model;
    private final LanguageHelper lh;

    public UIMainWindow(ModelMainWindow model) throws IOException {
        super("pictures/backgrounds/HomeBackgroundFr.jpg", 411, 530);
        this.model = model;
        this.lh = this.model.getLanguageHelper();
        initUI();
    }

    public JButtonDesign getButtonCreate() {
        return buttonCreate;
    }

    private void initUI() {
        // frame settings
        setTitle(this.lh.getTextDef(UI_NAME, "WINDOW_TITLE"));
        setResizable(false);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // contents
        JLabel pane = getBG();
        setContentPane(pane);
        addComponents(pane);

        new ComponentMover(this, this.getContentPane());
        
        try {
            setIconImage(ImageIO.read(new File("pictures/icons/sail_boat.png")));
        } catch (IOException ex) {
        }
        
        pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addComponents(JLabel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        // space on top
        pane.add(Box.createRigidArea(new Dimension(0, 320)));

        buttonCreate = addCenteredButton(this.lh.getTextDef(UI_NAME, "SERVER"), pane);
        pane.add(Box.createRigidArea(new Dimension(0, 10))); // space between buttons
        JButtonDesign buttonStats = addCenteredButton(this.lh.getTextDef(UI_NAME, "SERVER_STATS"), pane);
        pane.add(Box.createRigidArea(new Dimension(0, 10))); // space between buttons		
        JButtonDesign buttonList = addCenteredButton(this.lh.getTextDef(UI_NAME, "LIST_SERVERS"), pane);
        pane.add(Box.createRigidArea(new Dimension(0, 20))); // space between buttons
        JButtonDesign buttonConfigUsername = addCenteredButton(this.lh.getTextDef(UI_NAME, "CONFIG_USERNAME"), pane);
        pane.add(Box.createRigidArea(new Dimension(0, 20))); // space between buttons
        JButtonDesign buttonExit = new JButtonDesign(this.lh.getTextDef(UI_NAME, "QUIT"));
        buttonExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(buttonExit);

        buttonCreate.setActionCommand("create");
        buttonStats.setActionCommand("stats");
        buttonList.setActionCommand("list");
        buttonConfigUsername.setActionCommand("config_username");
        buttonExit.setActionCommand("quit");

        ActionListener al = new ALMainWindow(this);
        buttonCreate.addActionListener(al);
        buttonStats.addActionListener(al);
        buttonList.addActionListener(al);
        buttonConfigUsername.addActionListener(al);
        buttonExit.addActionListener(al);
    }

    private JButtonDesign addCenteredButton(String text, Container container) {
        JButtonDesign button = new JButtonDesign(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font(UIManager.getDefaults().getFont("TabbedPane.font").getFontName(), Font.PLAIN, 16));
        container.add(button);

        return button;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    public void userWantsToEditPseudo() {

        if (isConnectedToServer()) {
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "ERROR_CONNECTED_TO_A_SERVER_CONTENT"), this.lh.getTextDef(UI_NAME, "ERROR_CONNECTED_TO_A_SERVER_TITLE"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        
        model.userWantsToEditUserParameters();
    }

    public void userWantsToListAllBroadcastingServers() {
        model.userWantsToListAllBroadcastingServers();
    }

    public void userWantsToCreateAServer() {
        
        if (this.isServerRunning())
        {
            if (JOptionPane.showConfirmDialog(null, this.lh.getTextDef(UI_NAME, "QUESTION_STOP_SERVER_MESSAGE"), this.lh.getTextDef(UI_NAME, "QUESTION_STOP_SERVER_TITLE"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                model.userWantsToStopServer();
            }
        }
        else
        {
            model.userWantsToCreateAServer();
        }
    }

    public void userWantsToViewServerStats() {
        model.userWantsToViewServerStats();
    }

    public boolean isServerRunning() {
        return this.model.isServerRunning();
    }

    public boolean isConnectedToServer() {
        return this.model.isConnectedToServer();
    }
}
