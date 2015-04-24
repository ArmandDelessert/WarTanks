package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import navalbattle.client.gui.actionlisteners.ALServerCreation;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.gui.utils.JTextFieldLimit;
import navalbattle.client.models.ModelManualConnect;
import navalbattle.client.models.ModelServerCreation;
import navalbattle.datamodel.DMServerConfig;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.server.GameParameters;

public class UIServerCreation extends JFrame implements ItemListener, Observer {

    private static final long serialVersionUID = 2270003163430296836L;

    private static final String UI_NAME = "ui_serverCreation";
    private final ModelServerCreation model;
    private final LanguageHelper lh;

    private JCheckBox addPassCheck;
    private JPanel passArea;

    JRadioButton buttonVsAI;
    JRadioButton buttonTwoPlayers;
    JCheckBox checkSpySat;
    JCheckBox checkMines;
    JRadioButton buttonSmall;
    JRadioButton buttonMedium;
    JRadioButton buttonLarge;
    JTextField nameField;
    JPasswordField passField;

    public MapSizeEnum getMapSize() {
        if (buttonSmall.isSelected()) {
            return MapSizeEnum.SMALL;
        }

        if (buttonMedium.isSelected()) {
            return MapSizeEnum.MEDIUM;
        }

        if (buttonLarge.isSelected()) {
            return MapSizeEnum.LARGE;
        }

        return null;
    }

    public NavalBattleProtocol.OPPONENT_TYPE getOpponentType() {
        if (buttonVsAI.isSelected()) {
            return NavalBattleProtocol.OPPONENT_TYPE.HumanVSAI;
        }

        if (buttonTwoPlayers.isSelected()) {
            return NavalBattleProtocol.OPPONENT_TYPE.HumanVSHuman;
        }

        return null;
    }

    public void submitPage() {
        
        if (this.isServerRunning()) {
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "ERROR_SERVER_IS_RUNNING_TITLE"), this.lh.getTextDef(UI_NAME, "ERROR_SERVER_IS_RUNNING_CONTENT"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        final MapSizeEnum mapSizeSelected = this.getMapSize();
        final NavalBattleProtocol.OPPONENT_TYPE opponentTypeSelected = this.getOpponentType();
        final String serverName = this.nameField.getText();
        final boolean passwordRequired = this.addPassCheck.isSelected();
        final String password = (passwordRequired ? new String(this.passField.getPassword()) : null);
        final boolean useMines = this.checkMines.isSelected();
        final boolean useSpySatellite = this.checkSpySat.isSelected();

        ArrayList<String> errors = new ArrayList<>();

        if (mapSizeSelected == null) {
            errors.add(this.lh.getTextDef(UI_NAME, "ERROR_SELECT_A_MAP_SIZE"));
        }

        if (opponentTypeSelected == null) {
            errors.add(this.lh.getTextDef(UI_NAME, "ERROR_SELECT_AN_OPPONENT_TYPE"));
        }

        if (serverName.isEmpty()) {
            errors.add(this.lh.getTextDef(UI_NAME, "ERROR_NO_NAME_SPECIFIED"));
        } else if (serverName.length() > 20) {
            errors.add(this.lh.getTextDef(UI_NAME, "ERROR_NAME_TOO_LONG"));
        }

        if (passwordRequired) {
            if (password.isEmpty()) {
                errors.add(this.lh.getTextDef(UI_NAME, "ERROR_NO_PASSWORD_SPECIFIED"));
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            final String ls = System.lineSeparator();

            for (String error : errors) {
                sb.append(error);
                sb.append(ls);
            }

            JOptionPane.showMessageDialog(null, sb.toString().trim(), this.lh.getTextDef(UI_NAME, "ERROR"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            GameParameters gameParameters = new GameParameters(serverName,
                    password,
                    mapSizeSelected,
                    opponentTypeSelected,
                    (useMines ? NavalBattleProtocol.BONUS_STATE.ACTIVATED : NavalBattleProtocol.BONUS_STATE.DEACTIVATED),
                    (useSpySatellite ? NavalBattleProtocol.BONUS_STATE.ACTIVATED : NavalBattleProtocol.BONUS_STATE.DEACTIVATED));

            
            
            
            this.model.userWantsToStartServer(gameParameters);
            
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "SERVER_HAS_BEEN_STARTED"), this.lh.getTextDef(UI_NAME, "INFO"), JOptionPane.INFORMATION_MESSAGE);
            
            if (JOptionPane.showConfirmDialog(null, this.lh.getTextDef(UI_NAME, "WOULD_LIKE_CONNECT_SERVER_CREATED_CONTENT"), this.lh.getTextDef(UI_NAME, "WOULD_LIKE_CONNECT_SERVER_CREATED_TITLE"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                model.userWantsToconnectToCreatedServer(gameParameters);
            }
            
            this.dispose();
        }
    }

    public UIServerCreation(ModelServerCreation model) throws IOException {
        this.model = model;
        this.lh = this.model.getLanguageHelper();

        buttonVsAI = new JRadioButton(this.lh.getTextDef(UI_NAME, "VS_AI"));
        buttonTwoPlayers = new JRadioButton(this.lh.getTextDef(UI_NAME, "TWO_PLAYERS"));
        checkSpySat = new JCheckBox(this.lh.getTextDef(UI_NAME, "SPY_SATELLITE"));
        checkMines = new JCheckBox(this.lh.getTextDef(UI_NAME, "MINES"));
        buttonSmall = new JRadioButton(this.lh.getTextDef(UI_NAME, "SMALL"));
        buttonMedium = new JRadioButton(this.lh.getTextDef(UI_NAME, "MEDIUM"));
        buttonLarge = new JRadioButton(this.lh.getTextDef(UI_NAME, "LARGE"));
        nameField = new JTextField(20);
        passField = new JPasswordField(20);
        
        initUI();
        
        DMServerConfig previousServerParameters = this.model.getPreviousServerParameters();
        
        nameField.setText(previousServerParameters.getServerName());
        
        if (previousServerParameters.getMode() == NavalBattleProtocol.OPPONENT_TYPE.HumanVSAI) {
            buttonVsAI.setSelected(true);
        }
        else {
            buttonTwoPlayers.setSelected(true);
        }
        
        if (previousServerParameters.getAreMinesEnabled() == NavalBattleProtocol.BONUS_STATE.ACTIVATED) {
            checkMines.setSelected(true);
        }
        
        if (previousServerParameters.getAreSatEnabled()== NavalBattleProtocol.BONUS_STATE.ACTIVATED) {
            checkSpySat.setSelected(true);
        }
        
        switch (previousServerParameters.getMapSize())
        {
            case SMALL:
                buttonSmall.setSelected(true);
                break;
                
            case MEDIUM:
                buttonMedium.setSelected(true);
                break;
                
                
            case LARGE:
                buttonLarge.setSelected(true);
                break;
        }
        
        if (previousServerParameters.isRequiresPassword())
            addPassCheck.setSelected(true);
                
        passField.setText(previousServerParameters.getPassword());
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
        setVisible(true);
    }

    private void addComponents(JPanel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(83, 172));
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JPanel top = new JPanel();
        passArea = new JPanel();
        JPanel buttons = new JPanel();

        top.add(left);
        top.add(right);

        pane.add(top);
        pane.add(passArea);
        pane.add(buttons);

        passArea.setVisible(false);

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        // left
        JLabel modeText = new JLabel(this.lh.getTextDef(UI_NAME, "MODE"));
        modeText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        left.add(modeText);

        left.add(Box.createRigidArea(new Dimension(0, 5))); // empty space

        JLabel modeSizeText = new JLabel(this.lh.getTextDef(UI_NAME, "SIZE"));
        modeSizeText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        left.add(modeSizeText);

        left.add(Box.createRigidArea(new Dimension(0, 20))); // empty space

        JLabel bonusText = new JLabel(this.lh.getTextDef(UI_NAME, "BONUS"));
        bonusText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        left.add(bonusText);

        left.add(Box.createRigidArea(new Dimension(0, 40))); // empty space

        JLabel nameText = new JLabel(this.lh.getTextDef(UI_NAME, "NAME"));
        nameText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        left.add(nameText);

        // right
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(buttonVsAI);
        modeGroup.add(buttonTwoPlayers);

        JPanel modeButtons = new JPanel();
        modeButtons.setLayout(new BoxLayout(modeButtons, BoxLayout.X_AXIS));
        modeButtons.add(buttonVsAI);
        modeButtons.add(buttonTwoPlayers);
        modeButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(modeButtons);

        ButtonGroup modeGroupMapSize = new ButtonGroup();
        modeGroupMapSize.add(buttonSmall);
        modeGroupMapSize.add(buttonMedium);
        modeGroupMapSize.add(buttonLarge);

        JPanel modeGroupeMapSizeButtons = new JPanel();
        modeGroupeMapSizeButtons.setLayout(new BoxLayout(modeGroupeMapSizeButtons, BoxLayout.X_AXIS));
        modeGroupeMapSizeButtons.add(buttonSmall);
        modeGroupeMapSizeButtons.add(buttonMedium);
        modeGroupeMapSizeButtons.add(buttonLarge);
        modeGroupeMapSizeButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(modeGroupeMapSizeButtons);

        right.add(Box.createRigidArea(new Dimension(0, 10))); // empty space

        JPanel checkBoxContainer = new JPanel();
        checkBoxContainer.setLayout(new BoxLayout(checkBoxContainer, BoxLayout.Y_AXIS));

        checkBoxContainer.add(checkSpySat);
        checkBoxContainer.add(checkMines);
        checkBoxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(checkBoxContainer);

        right.add(Box.createRigidArea(new Dimension(0, 10))); // empty space

        JPanel nameFieldContainer = new JPanel();
        nameFieldContainer.setLayout(new BoxLayout(nameFieldContainer, BoxLayout.Y_AXIS));

        nameField.setDocument(new JTextFieldLimit(20));
        JLabel maxChar = new JLabel(this.lh.getTextDef(UI_NAME, "MAX_CHAR"));
        maxChar.setFont(new Font(UIManager.getDefaults().getFont("TabbedPane.font").getFontName(), Font.PLAIN, 10));

        nameFieldContainer.add(nameField);
        nameFieldContainer.add(maxChar);
        nameFieldContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(nameFieldContainer);

        right.add(Box.createRigidArea(new Dimension(0, 10))); // empty space

        addPassCheck = new JCheckBox(this.lh.getTextDef(UI_NAME, "WITH_PASS"));
        addPassCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(addPassCheck);

        // pass area
        JLabel passText = new JLabel(this.lh.getTextDef(UI_NAME, "PASSWORD"));
        passArea.add(passText);

        passField.setDocument(new JTextFieldLimit(20));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passArea.add(passField);

        // buttons
        JButtonDesign buttonCreate = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CREATE"));
        JButtonDesign buttonCancel = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CANCEL"));
        buttons.add(buttonCreate);
        buttons.add(buttonCancel);

        // Listeners
        addPassCheck.addItemListener(this);
        buttonCreate.setActionCommand("create");
        buttonCancel.setActionCommand("cancel");

        ActionListener al = new ALServerCreation(this);
        buttonCreate.addActionListener(al);
        buttonCancel.addActionListener(al);

        pane.getRootPane().setDefaultButton(buttonCreate);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == addPassCheck) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.passArea.setVisible(true);
                this.pack();
            } else {
                this.passArea.setVisible(false);
                this.pack();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.getClass() == ModelManualConnect.class) {
            if (arg.getClass() == NotifyMessage.class) {
                NotifyMessage message = (NotifyMessage) arg;

                switch (message.getType()) {
                    case CLOSE_WINDOW:
                        this.dispose();
                        break;
                }
            }
        }
    }

    private boolean isServerRunning() {
        return this.model.isServerRunning();
    }

}
