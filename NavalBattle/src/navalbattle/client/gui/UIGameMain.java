package navalbattle.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import navalbattle.client.gui.actionlisteners.ALGameMain;
import navalbattle.client.gui.utils.ComponentMover;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import navalbattle.boats.Aircraftcarrier;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.boats.BoatPosition;
import navalbattle.boats.BoatUtils;
import navalbattle.boats.Cruiser;
import navalbattle.boats.Destroyer;
import navalbattle.boats.Submarine;
import navalbattle.client.ClientGrid;
import navalbattle.client.gui.messages.NotifyMessage;
import navalbattle.client.gui.utils.PicManip;
import navalbattle.client.viewmodelmessages.GameStateChangedEvent;
import navalbattle.client.models.ModelGameMain;
import navalbattle.html.htmlutils;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.common.BoatQuantity;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.NavalBattleAttackCoordinateResponse;
import navalbattle.protocol.messages.common.NavalBattleAttackedCoordinate;
import navalbattle.protocol.messages.common.NavalBattleChatReceive;
import navalbattle.protocol.messages.common.NavalBattleChatSendResponse;
import navalbattle.protocol.messages.common.NavalBattleEndOfGame;
import navalbattle.protocol.messages.common.NavalBattleGameStart;
import navalbattle.server.BoatsAreOverlapping;
import navalbattle.sounds.SoundHelper;

public class UIGameMain extends JFrame implements Observer {

    private static final long serialVersionUID = -5839340289045647964L;

    private static final String UI_NAME = "ui_gameMain";
    private static int CELL_NUMBER;
    private final int CELL_SIZE;
    private static int TOTAL_SIZE;
    private final LanguageHelper lh;

    private final ModelGameMain model;
    private final MyGrid myGrid;
    private final EnemyGrid enemyGrid;
    private JScrollPane chatTextPane;
    private boolean hasGameEnded = false;

    private final JTextField chatInputField = new JTextField();

    private final JTextPane chatText = new JTextPane();
    private final HTMLEditorKit kit = new HTMLEditorKit();
    private final HTMLDocument doc = new HTMLDocument();

    private static final String MISS_PIC_PATH = "pictures/grid/miss.gif";
    private static final String HIT_PIC_PATH = "pictures/grid/hit.gif";
    private static final String SINK_PIC_PATH = "pictures/grid/sink.gif";
    private static final String FIRE_CURSOR = "pictures/grid/fire_here.gif";
    private static final String FIRE_CURSOR_FORBIDDEN = "pictures/grid/crosshair_forbidden.gif";
    private static final String BONUS_MINE = "pictures/grid/mine.gif";
    private static final String BONUS_SAT = "pictures/grid/sat.gif";
    private static final String REVElATION_SHIP = "pictures/grid/revelation_ship.gif";
    private static final String REVElATION_NO_SHIP = "pictures/grid/revelation_noship.gif";

    private final BufferedImage missPic;
    private final BufferedImage hitPic;
    private final BufferedImage sinkPic;
    private final BufferedImage fireCursor;
    private final BufferedImage fireCursorFobidden;
    private final BufferedImage bonusMine;
    private final BufferedImage bonusSat;
    private final BufferedImage revelationShip;
    private final BufferedImage revelationNoShip;

    private final ClientGrid clientGameGrid;
    private final ClientGrid enemyGameGrid;

    public UIGameMain(final ModelGameMain model) throws IOException {
        this.model = model;

        this.clientGameGrid = this.model.getMyGrid();
        CELL_NUMBER = this.clientGameGrid.getMapSize().getSize();

        CELL_SIZE = this.clientGameGrid.getMapSize().getCellSize();
        this.lh = this.model.getController().getLanguageHelper();

        // pics
        this.missPic = PicManip.resizeImg(ImageIO.read(new File(MISS_PIC_PATH)), CELL_SIZE, CELL_SIZE);
        this.hitPic = PicManip.resizeImg(ImageIO.read(new File(HIT_PIC_PATH)), CELL_SIZE, CELL_SIZE);
        this.sinkPic = PicManip.resizeImg(ImageIO.read(new File(SINK_PIC_PATH)), CELL_SIZE, CELL_SIZE);
        this.fireCursor = PicManip.resizeImg(ImageIO.read(new File(FIRE_CURSOR)), CELL_SIZE, CELL_SIZE);
        this.fireCursorFobidden = PicManip.resizeImg(ImageIO.read(new File(FIRE_CURSOR_FORBIDDEN)), CELL_SIZE, CELL_SIZE);
        this.bonusMine = PicManip.resizeImg(ImageIO.read(new File(BONUS_MINE)), CELL_SIZE, CELL_SIZE);
        this.bonusSat = PicManip.resizeImg(ImageIO.read(new File(BONUS_SAT)), CELL_SIZE, CELL_SIZE);
        this.revelationShip = PicManip.resizeImg(ImageIO.read(new File(REVElATION_SHIP)), CELL_SIZE, CELL_SIZE);
        this.revelationNoShip = PicManip.resizeImg(ImageIO.read(new File(REVElATION_NO_SHIP)), CELL_SIZE, CELL_SIZE);

        this.enemyGameGrid = this.model.getOpponentsGrid();

        TOTAL_SIZE = CELL_NUMBER * CELL_SIZE;

        this.myGrid = new MyGrid(this, model, this.clientGameGrid, missPic, hitPic, sinkPic, fireCursor, fireCursorFobidden, bonusMine, bonusSat, revelationShip, revelationNoShip);
        this.enemyGrid = new EnemyGrid(this, model, this.enemyGameGrid, missPic, hitPic, sinkPic, fireCursor, fireCursorFobidden, bonusMine, bonusSat, revelationShip, revelationNoShip);

        initUI();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                model.windowClosed();
                setVisible(false);
            }
        });
    }

    public MyGrid getMyGrid() {
        return this.myGrid;
    }

    public EnemyGrid getEnemyGrid() {
        return this.enemyGrid;
    }

    private void initUI() {
        // frame settings
        setTitle(this.lh.getTextDef(UI_NAME, "WINDOW_TITLE"));
        setResizable(false);
        setUndecorated(true);

        // contents
        JPanel pane = new JPanel();
        setContentPane(pane);
        addComponents(pane);

        new ComponentMover(this, this.getContentPane());

        try {
            setIconImage(ImageIO.read(new File("pictures/icons/sail_boat.png")));
        } catch (IOException ex) {
        }

        this.updateChatArea(null, this.lh.getTextDef(UI_NAME, "PLACE_YOUR_BOATS_NOW"));
        pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addComponents(JPanel pane) {
        pane.setLayout(new BorderLayout());

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        JPanel opponentContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel opponent = new JLabel(this.lh.getTextDef(UI_NAME, "OPPONENT_FLEET"));
        opponent.setPreferredSize(new Dimension(enemyGrid.getPreferredSize().width, 15));
        opponent.setHorizontalAlignment(SwingConstants.CENTER);
        opponentContainer.add(opponent);
        pane.add(opponentContainer, BorderLayout.NORTH);

        // boats
        JPanel boatsAreaContainer = new JPanel();
        JPanel boatsArea = new JPanel();
        boatsArea.setLayout(new BoxLayout(boatsArea, BoxLayout.Y_AXIS));

        boatsArea.add(enemyGrid);
        boatsArea.add(Box.createRigidArea(new Dimension(0, 20))); // space between grids
        boatsArea.add(myGrid);

        boatsAreaContainer.add(boatsArea);
        pane.add(boatsAreaContainer, BorderLayout.CENTER);

        JPanel myFleetContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel myFleet = new JLabel(this.lh.getTextDef(UI_NAME, "MY_FLEET"));
        myFleet.setPreferredSize(new Dimension(myGrid.getPreferredSize().width, 15));
        myFleet.setHorizontalAlignment(SwingConstants.CENTER);
        myFleetContainer.add(myFleet);
        pane.add(myFleetContainer, BorderLayout.SOUTH);

        // chat
        JPanel chatPaneContainer = new JPanel();
        JPanel chatPane = new JPanel(new BorderLayout());

        chatText.setEditorKit(kit);
        chatText.setDocument(doc);
        chatText.setEditable(false);
        chatTextPane = new JScrollPane(chatText,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel inputPane = new JPanel(new BorderLayout());

        JButton buttonSend = new JButton(this.lh.getTextDef(UI_NAME, "SEND_CHAT"));
        inputPane.add(chatInputField, BorderLayout.CENTER);
        inputPane.add(buttonSend, BorderLayout.EAST);

        chatPane.add(chatTextPane, BorderLayout.CENTER);
        chatPane.add(inputPane, BorderLayout.SOUTH);
        chatPane.setPreferredSize(new Dimension(300, boatsArea.getPreferredSize().height));
        chatPaneContainer.add(chatPane);

        pane.add(chatPaneContainer, BorderLayout.EAST);

        buttonSend.setActionCommand("send");

        ActionListener al = new ALGameMain(this);
        buttonSend.addActionListener(al);

        pane.getRootPane().setDefaultButton(buttonSend);
    }

    public StringBuilder getDamagesTranslated(ArrayList<CoordinateWithType> allCoordinatesHit) {
        StringBuilder sb = new StringBuilder();

        for (CoordinateWithType cwt : allCoordinatesHit) {
            sb.append("(").append(cwt.getX()).append(",").append(cwt.getY()).append(") : ");

            String fireResultTranslated = null;
            switch (cwt.getType()) {
                case DAMAGED:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "DAMAGED_A_BOAT");
                    break;
                case SINKED:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "SINKED_A_BOAT");
                    break;
                case SATELLITE:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "SAT_BONUS");
                    break;
                case MINE:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "MINE_BONUS");
                    break;
                case NOTHING:
                case FIRED_BUT_DID_NOT_DAMAGE_ANYTHING:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "FIRED_BUT_DID_NOT_DAMAGE_ANYTHING");
                    break;
                case REAVEALED_HAS_SHIP:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "REAVEALED_HAS_SHIP");
                    break;
                case REAVEALED_HAS_NO_SHIP:
                    fireResultTranslated = this.lh.getTextDef(UI_NAME, "REAVEALED_HAS_NO_SHIP");
                    break;
            }

            sb.append(fireResultTranslated).append("%nl%");
        }

        return sb;
    }

    // %nl% in text for newline
    private void updateChatArea(String username, String text) {
        if (text.endsWith("%nl%")) {
            text = text.substring(0, text.length() - 4);
        }

        String color;

        if (username == null) {
            color = "#FF6600";
        } else if (username.equals(this.model.getMyUsername())) {
            color = "#0033CC";
        } else {
            color = "#009933";
        }

        String usernameEscaped = username == null ? this.lh.getTextDef(UI_NAME, "SERVER") : htmlutils.escapeHTML(username);
        String textEscaped = htmlutils.escapeHTML(text).replaceAll("%nl%", "<br />");

        try {
            kit.insertHTML(doc, doc.getLength(), "<span style=\"color: " + color + ";font-size: 1.11em;font-weight: bold;\">" + usernameEscaped + ":</span><br />" + textEscaped + "<br />", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
        }

        chatText.setCaretPosition(doc.getLength());
    }

    private void showYouFireOrNot(boolean isItMyTurn) {
        String color = isItMyTurn ? "green" : "black";
        String text = isItMyTurn ? this.lh.getTextDef(UI_NAME, "PICK_SPOT_YOUR_TURN") : this.lh.getTextDef(UI_NAME, "WAIT_FOR_THE_ATTACK_NOT_YOUR_TURN");

        try {
            kit.insertHTML(doc, doc.getLength(), "<span style=\"color: " + color + ";font-weight: bold;\">" + text + "</span>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
        }

        chatText.setCaretPosition(doc.getLength());
    }

    private void showYouWonOrNot(boolean won) {
        String color = won ? "green" : "red";
        String text = won ? this.lh.getTextDef(UI_NAME, "WON_THE_GAME") : this.lh.getTextDef(UI_NAME, "LOST_THE_GAME");

        try {
            kit.insertHTML(doc, doc.getLength(), "<span style=\"color: " + color + ";font-weight: bold;font-size: 1.25em;\">" + text.toUpperCase() + "</span>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
        }

        chatText.setCaretPosition(doc.getLength());
    }

    @Override
    public void update(Observable o, Object arg) {
        Class classEvent = arg.getClass();

        if (classEvent == NavalBattleChatReceive.class) {
            // Someone spoke

            NavalBattleChatReceive messageCasted = (NavalBattleChatReceive) arg;
            this.updateChatArea(messageCasted.getUsername(), messageCasted.getText());

        } else if (classEvent == NavalBattleChatSendResponse.class) {
            NavalBattleChatSendResponse messageCasted = (NavalBattleChatSendResponse) arg;
        } else if (classEvent == GameStateChangedEvent.class) {
            // We have received the response to our own fire or the opponent just played
            // the grids must be refreshed

            GameStateChangedEvent messageCasted = (GameStateChangedEvent) arg;

            this.repaint();

            ModelGameMain.WHO shoSuffered = messageCasted.getWho();

            if (shoSuffered == ModelGameMain.WHO.MYSELF) {
                this.myGrid.makeBlink();
            } else {
                this.enemyGrid.makeBlink();
            }

            this.repaint();

        } else if (classEvent == NavalBattleAttackCoordinateResponse.class) {
            NavalBattleAttackCoordinateResponse messageCasted = (NavalBattleAttackCoordinateResponse) arg;
            StringBuilder sb = getDamagesTranslated(messageCasted.getAllCoordinatesHit());
            this.updateChatArea(null, this.lh.getTextDef(UI_NAME, "YOUR_ATTACK_RESULT") + "%nl%" + sb.toString());
            this.playSoundsAccordingToDamages(messageCasted.getAllCoordinatesHit());

            if (!hasGameEnded) {
                this.showYouFireOrNot(false);
            }
        } else if (classEvent == NavalBattleAttackedCoordinate.class) {

            NavalBattleAttackedCoordinate messageCasted = (NavalBattleAttackedCoordinate) arg;
            StringBuilder sb = getDamagesTranslated(messageCasted.getAllCoordinatesHit());
            this.updateChatArea(null, this.lh.getTextDef(UI_NAME, "YOUR_OPPONENT_FIRE_RESULT").replaceAll("%a", messageCasted.getUsernameOpponent()) + "%nl%" + sb.toString());
            this.playSoundsAccordingToDamages(messageCasted.getAllCoordinatesHit());

            if (!hasGameEnded) {
                this.showYouFireOrNot(true);
            }

        } else if (classEvent == NotifyMessage.class) {
            NotifyMessage messageCasted = (NotifyMessage) arg;
            int messageType = 0;

            switch (messageCasted.getType()) {
                case DISPLAY_INFO:
                    messageType = JOptionPane.INFORMATION_MESSAGE;
                    break;

                case DISPLAY_ERROR:
                    messageType = JOptionPane.ERROR_MESSAGE;
                    break;

                case CLOSE_WINDOW:
                    this.setVisible(false);
                    this.dispose(); // ADDED
//                    try {
//                        this.finalize();
//                    } catch (Throwable ex) {
//                    }
                    return;
            }

            JOptionPane.showMessageDialog(null,
                    (String) messageCasted.getData(),
                    "",
                    messageType);

        } else if (classEvent == NavalBattleGameStart.class) {
            NavalBattleGameStart messageCasted = (NavalBattleGameStart) arg;

            String gameStartText = this.lh.getTextDef(UI_NAME, "GAME_STARTED");
            String usernameStartPlay = messageCasted.getUsernameFirstToPlay();
            boolean iStart = usernameStartPlay.equals(this.model.getMyUsername());

            if (iStart) {
                gameStartText += this.lh.getTextDef(UI_NAME, "YOU_ARE_FIRST_TO_PLAY");
            } else {
                gameStartText += this.lh.getTextDef(UI_NAME, "YOU_ARE_NOT_FIRST_TO_PLAY").replaceAll("%a", usernameStartPlay);
            }

            this.updateChatArea(null, gameStartText);
            this.showYouFireOrNot(iStart);
        } else if (classEvent == NavalBattleEndOfGame.class) {
            NavalBattleEndOfGame messageCasted = (NavalBattleEndOfGame) arg;

            this.hasGameEnded = true;
            this.myGrid.stopBlink();
            this.enemyGrid.stopBlink();
            this.repaint();

            if (messageCasted.getReason() == NavalBattleProtocol.REAON_END_OF_GAME.CANNOT_CONTINUE_GAME) {
                JOptionPane.showMessageDialog(null,
                        this.lh.getTextDef(UI_NAME, "CANNOT_CONTINUE_GAME"),
                        "",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                boolean iWon = (this.model.getMyUsername().equals(messageCasted.getUsernameWinner()));

                this.showYouWonOrNot(iWon);

                SoundHelper.playSound(iWon ? SoundHelper.SOUND_TYPE.VICTORY : SoundHelper.SOUND_TYPE.LOSS);
                
                JOptionPane.showMessageDialog(null,
                        iWon ? this.lh.getTextDef(UI_NAME, "WON_THE_GAME") : this.lh.getTextDef(UI_NAME, "LOST_THE_GAME"),
                        "",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void userWantsToSpeak() {
        String text = this.chatInputField.getText();

        if (text.isEmpty()) {
            return;
        }

        this.chatInputField.setText("");
        this.model.userWantsToSpeak(text);
    }

    private void playSoundsAccordingToDamages(ArrayList<CoordinateWithType> allPositionsHit) {
        ArrayList<SoundHelper.SOUND_TYPE> soundsToPlay = new ArrayList<>();

        boolean isDamaged = false;
        boolean isSinked = false;
        boolean hasHitSat = false;
        boolean hasHitMine = false;
        boolean hasHitWater = false;

        for (CoordinateWithType cwt : allPositionsHit) {
            switch (cwt.getType()) {
                case DAMAGED:
                    isDamaged = true;
                    break;
                case SINKED:
                    isSinked = true;
                    break;
                case SATELLITE:
                    hasHitSat = true;
                    break;
                case MINE:
                    hasHitMine = true;
                    break;
                case NOTHING:
                case FIRED_BUT_DID_NOT_DAMAGE_ANYTHING:
                    hasHitWater = true;
                    break;
                case REAVEALED_HAS_SHIP:
                    break;
                case REAVEALED_HAS_NO_SHIP:
                    break;
            }
        }

        if (isSinked) {
            soundsToPlay.add(SoundHelper.SOUND_TYPE.SANK);
        }

        if (isDamaged) {
            soundsToPlay.add(SoundHelper.SOUND_TYPE.DAMAGED);
        }

        if (hasHitSat) {

        }

        if (hasHitMine) {
            soundsToPlay.add(SoundHelper.SOUND_TYPE.MINE);
        }

        if (hasHitWater && !isDamaged && !isSinked && !hasHitSat && !hasHitMine) {
            soundsToPlay.add(SoundHelper.SOUND_TYPE.MISS);
        }

        for (SoundHelper.SOUND_TYPE soundToPlay : soundsToPlay) {
            SoundHelper.playSound(soundToPlay);

            try {
                Thread.sleep(350);
            } catch (InterruptedException ex) {
            }
        }
    }

    private class BlinkerDamages {

        private boolean displayPreviousGrid = false;
        private Thread blinker = null;
        private final GridArea grid;

        public BlinkerDamages(GridArea grid) {
            this.grid = grid;
        }

        public void startBlinkingDamages() {
            this.blinker = new Thread() {
                @Override
                public void run() {
                    displayPreviousGrid = false;

                    for (int i = 0; i < 2; ++i) {
                        grid.repaint();
                        displayPreviousGrid = true;

                        try {
                            Thread.sleep(120);
                        } catch (InterruptedException ex) {
                        }

                        grid.repaint();
                        displayPreviousGrid = false;

                        try {
                            Thread.sleep(240);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            };

            this.blinker.start();
        }

        private boolean displayPreviousGrid() {
            return this.displayPreviousGrid;
        }

        private void stopBlinkingDamages() {
            if (this.blinker != null && this.blinker.isAlive()) {
                this.blinker.stop();
                grid.repaint();
            }
        }
    }

    private class EnemyGrid extends GridArea {

        private final ClientGrid enemyGrid;
        private final ModelGameMain model;
        private final UIGameMain ui;
        private final BufferedImage missPic;
        private final BufferedImage hitPic;
        private final BufferedImage sinkPic;
        private final BufferedImage fireCursor;
        private final BufferedImage fireCursorFobidden;
        private final BufferedImage bonusMine;
        private final BufferedImage bonusSat;
        private final BufferedImage revelationShip;
        private final BufferedImage revelationNoShip;
        private MouseHandler mh;
        private final BlinkerDamages blinker = new BlinkerDamages(this);

//		public EnemyGrid(int[][] playField)
        public EnemyGrid(UIGameMain ui, ModelGameMain model, ClientGrid enemyGrid, BufferedImage missPic, BufferedImage hitPic, BufferedImage sinkPic, BufferedImage fireCursor, BufferedImage fireCursorFobidden, BufferedImage bonusMine, BufferedImage bonusSat, BufferedImage revelationShip, BufferedImage revelationNoShip) {
            super(CELL_NUMBER, CELL_SIZE);

            // ici il faut recevoir le modele
//			this.playField = playField;
            this.model = model;
            this.ui = ui;
            this.missPic = missPic;
            this.hitPic = hitPic;
            this.sinkPic = sinkPic;
            this.fireCursor = fireCursor;
            this.fireCursorFobidden = fireCursorFobidden;
            this.bonusMine = bonusMine;
            this.bonusSat = bonusSat;
            this.revelationShip = revelationShip;
            this.revelationNoShip = revelationNoShip;
            this.enemyGrid = enemyGrid;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            if (super.cursorLocation == null) {
                return;
            }

            super.drawDamages(this, g2, this.hitPic, this.sinkPic, this.missPic, this.fireCursor, this.fireCursorFobidden, this.bonusMine, this.bonusSat, this.revelationShip, this.revelationNoShip);

            if (isPositionValidForFiring((int) super.cursorLocation.getX(), (int) super.cursorLocation.getY())) {

                BufferedImage cusrorCrossedOrNot = this.model.isIsItMyTurnToPlay() ? this.fireCursor : this.fireCursorFobidden;

                super.drawPictureOnCell((int) super.cursorLocation.getX(), (int) super.cursorLocation.getY(), g2, cusrorCrossedOrNot);
            }

        }

        public boolean isPositionValidForFiring(int cellX, int cellY) {
            if (hasGameEnded) {
                return false;
            }

            NavalBattleProtocol.COORDINATE_TYPE[][] enemyGrid = this.enemyGrid.getGridForDrawing();

            return (enemyGrid[cellX][cellY] == NavalBattleProtocol.COORDINATE_TYPE.NOTHING || enemyGrid[cellX][cellY] == NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP);
        }

        private void userFiresOn(int x, int y) {
            this.model.fireOnPosition(x, y);
        }

        @Override
        public NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawing() {

            if (blinker.displayPreviousGrid()) {
                return this.enemyGrid.getGridForDrawinPreviousGrid();
            }

            return this.enemyGrid.getGridForDrawing();
        }

        private void registerClickEvents() {

            this.mh = new EnemyGrid.MouseHandler(this);
            addMouseMotionListener(this.mh);
            addMouseListener(this.mh);
        }

        @Override
        public void makeBlink() {
            this.blinker.startBlinkingDamages();
        }

        @Override
        public void stopBlink() {
            this.blinker.stopBlinkingDamages();
        }

        private class MouseHandler extends MouseAdapter {

            private Rectangle lastSelectedInMyGrid = new Rectangle();
            private final GridArea grid;

            public MouseHandler(GridArea myGrid) {
                this.grid = myGrid;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = (int) (e.getPoint().getX() / CELL_SIZE);
                int y = (int) (e.getPoint().getY() / CELL_SIZE);

                if (x < CELL_NUMBER && y < CELL_NUMBER && cellsLocations[x][y] != lastSelectedInMyGrid) {
                    lastSelectedInMyGrid = cellsLocations[x][y];
                    cursorLocation = new Point(x, y);
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

                    int x = (int) (e.getPoint().getX() / CELL_SIZE);
                    int y = (int) (e.getPoint().getY() / CELL_SIZE);

                    EnemyGrid casted = (EnemyGrid) this.grid;

                    if (x < casted.getWidth() && y < casted.getHeight() && casted.isPositionValidForFiring(x, y)) {
                        casted.userFiresOn(x, y);
                    }
                }
            }
        }
    }

    private class MyGrid extends GridArea {

        private final ClientGrid myGameGrid;
        private final ModelGameMain model;

        // for placement
        private Boat boatPlace = null;
        private ArrayList<Boat> boatsPlace = new ArrayList<>();
        private ArrayList<Boat> boatsPlaced = new ArrayList<>();
        private MouseHandler mh;
        private Point lastCursorLocation;
        private boolean lastReturn;
        private final UIGameMain ui;
        private final BufferedImage missPic;
        private final BufferedImage hitPic;
        private final BufferedImage sinkPic;
        private final BufferedImage fireCursor;
        private final BufferedImage fireCursorFobidden;
        private final BufferedImage bonusMine;
        private final BufferedImage bonusSat;
        private final BufferedImage revelationShip;
        private final BufferedImage revelationNoShip;
        private final BlinkerDamages blinker = new BlinkerDamages(this);

        // fireCursor, bonusMine, bonusSat
        public MyGrid(UIGameMain ui, ModelGameMain model, ClientGrid myGameGrid, BufferedImage missPic, BufferedImage hitPic, BufferedImage sinkPic, BufferedImage fireCursor, BufferedImage fireCursorFobidden, BufferedImage bonusMine, BufferedImage bonusSat, BufferedImage revelationShip, BufferedImage revelationNoShip) throws IOException {
            super(CELL_NUMBER, CELL_SIZE);
            this.ui = ui;
            this.missPic = missPic;
            this.hitPic = hitPic;
            this.sinkPic = sinkPic;
            this.fireCursor = fireCursor;
            this.fireCursorFobidden = fireCursorFobidden;
            this.bonusMine = bonusMine;
            this.bonusSat = bonusSat;
            this.revelationShip = revelationShip;
            this.revelationNoShip = revelationNoShip;
            this.model = model;
            this.myGameGrid = myGameGrid;

            // Placement dea bateaux
            MapSizeEnum size = this.myGameGrid.getMapSize();
            BoatQuantity quantityOfBoats = size.getQuantityOfBoats();

            int cnt = quantityOfBoats.getAircraftCarriersCount();
            for (int i = 0; i < cnt; ++i) {
                boatsPlace.add(new Aircraftcarrier(CELL_SIZE));
            }

            cnt = quantityOfBoats.getCruiserCount();
            for (int i = 0; i < cnt; ++i) {
                boatsPlace.add(new Cruiser(CELL_SIZE));
            }

            cnt = quantityOfBoats.getDestroyerCount();
            for (int i = 0; i < cnt; ++i) {
                boatsPlace.add(new Destroyer(CELL_SIZE));
            }

            cnt = quantityOfBoats.getSubmarineCount();
            for (int i = 0; i < cnt; ++i) {
                boatsPlace.add(new Submarine(CELL_SIZE));
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    
                    SoundHelper.playSound(SoundHelper.SOUND_TYPE.POSITION);
                }
            });

            this.setNextBoatPlace();

            // pour tester le placement
            super.horizontalPlacement = true;

            this.mh = new MouseHandler(this);
            addMouseMotionListener(this.mh);
            addMouseListener(this.mh);
        }

        public void allBoatsHaveBeenPlaced() {

            // Deregistering mouse events related to my grid (no longer used once boats are placed)
            this.removeMouseMotionListener(this.mh);
            this.removeMouseListener(this.mh);
            this.mh = null;

            try {
                // Sending boat position to the server
                this.model.positionMyBoats(this.boatsPlaced);
            } catch (BoatsAreOverlapping | BoatIsOutsideGridException ex) {
                Logger.getLogger(UIGameMain.class.getName()).log(Level.SEVERE, null, ex);
            }

            EnemyGrid eg = this.ui.getEnemyGrid();
            eg.registerClickEvents();
        }

        public void setNextBoatPlace() {
            Boat bp = null;

            if (!this.boatsPlace.isEmpty()) {
                bp = this.boatsPlace.get(0);
                this.boatsPlace.remove(0);
            }
            else
            {
                SoundHelper.playSound(SoundHelper.SOUND_TYPE.BEGIN);
            }

            this.boatPlace = bp;
        }

        public void setCurrentBoatPlaced(int atX, int atY, NavalBattleProtocol.BOAT_ORIENTATION o) {
            this.boatPlace.setPosition(atX, atY, o);
            this.boatsPlaced.add(this.boatPlace);
        }

        // pour dire quel bateau il faut placer
        public void setBoatPlace(Boat boat) {
            this.boatPlace = boat;
        }

        public Boat getBoatPlace() {
            return this.boatPlace;
        }

        private boolean validPlacement() {
            if (cursorLocation == null) {
                // all boats have been placed
                lastReturn = false;
                return false;
            }

            if (lastCursorLocation == cursorLocation) {
                return lastReturn;
            }

            lastCursorLocation = cursorLocation;

            if (super.horizontalPlacement) {
                if (((cursorLocation.x * CELL_SIZE) + (this.boatPlace.getLength() * CELL_SIZE)) > TOTAL_SIZE) {
                    lastReturn = false;
                    return false;
                }
            } else {
                if (((cursorLocation.y * CELL_SIZE) + (this.boatPlace.getLength() * CELL_SIZE)) > TOTAL_SIZE) {
                    lastReturn = false;
                    return false;
                }
            }

            {
                ArrayList<BoatPosition> checkPos = new ArrayList<>();

                for (Boat b : this.boatsPlaced) {
                    checkPos.add(b.getPosition());
                }

                int cellX = (int) cursorLocation.getX();
                int cellY = (int) cursorLocation.getY();

                checkPos.add(new BoatPosition(cellX, cellY, (myGrid.horizontalPlacement ? NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL : NavalBattleProtocol.BOAT_ORIENTATION.VERTICAL), this.boatPlace.getLength()));

                int cells = this.myGameGrid.getMapSize().getSize();
                try {
                    if (BoatUtils.areBoatsOverlapping(new Dimension(cells, cells), checkPos)) {
                        lastReturn = false;
                        return false;
                    }
                } catch (BoatIsOutsideGridException ex) {
                    lastReturn = false;
                    return false;
                }
            }

            lastReturn = true;
            return true;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            this.paintPlacedBoats(g2);

            if (this.boatPlace == null) {
                // Game has started, we migh have miss/hit/etc. pictures to draw
                super.drawDamages(this, g2, this.hitPic, this.sinkPic, this.missPic, this.fireCursor, this.fireCursorFobidden, this.bonusMine, this.bonusSat, this.revelationShip, this.revelationNoShip);
            }

            // Boats already placed or invalid position => stop
            if (this.boatPlace == null || cursorLocation == null) {
                return;
            }

            int cellX = CELL_SIZE * (int) cursorLocation.getX();
            int cellY = CELL_SIZE * (int) cursorLocation.getY();

            if (validPlacement()) {
                if (super.horizontalPlacement) {
                    g2.fill3DRect(cellX,
                            cellY,
                            CELL_SIZE * this.boatPlace.getLength(),
                            CELL_SIZE,
                            false);
                } else {
                    g2.fill3DRect(cellX,
                            cellY,
                            CELL_SIZE,
                            CELL_SIZE * this.boatPlace.getLength(),
                            false);
                }
            }
        }

        private void paintPlacedBoats(Graphics2D g2) {
            // ici il faut mettre un for sur le tableau de bateaux avec leurs emplacements (modele)
            for (Boat boat : boatsPlaced) {
                int posX = CELL_SIZE * boat.getPosition().getX();
                int posY = CELL_SIZE * boat.getPosition().getY();
                BufferedImage boatPic;

                if (boat.getPosition().getOrientation() == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) {
                    boatPic = boat.getImageHorizontal();
                } else {
                    boatPic = boat.getImageVertical();
                }

                g2.drawImage(boatPic, null, posX + 3, posY + 3);
            }
        }

        @Override
        public NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawing() {

            if (blinker.displayPreviousGrid()) {
                return this.myGameGrid.getGridForDrawinPreviousGrid();
            }

            return this.myGameGrid.getGridForDrawing();
        }

        @Override
        public void makeBlink() {
            this.blinker.startBlinkingDamages();
        }

        @Override
        public void stopBlink() {
            this.blinker.stopBlinkingDamages();
        }

        private class MouseHandler extends MouseAdapter {

            private Rectangle lastSelectedInMyGrid = new Rectangle();
            private final GridArea grid;

            public MouseHandler(GridArea myGrid) {
                this.grid = myGrid;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = (int) (e.getPoint().getX() / CELL_SIZE);
                int y = (int) (e.getPoint().getY() / CELL_SIZE);

                if (x < CELL_NUMBER && y < CELL_NUMBER && cellsLocations[x][y] != lastSelectedInMyGrid) {
                    lastSelectedInMyGrid = cellsLocations[x][y];
                    cursorLocation = new Point(x, y);

                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if (boatPlace != null) {
                    // WE ARE PLACING BOATS
                    // this.grid is of type MyGrid

                    if (!validPlacement()) {
                        return;
                    }

                    // click gauche
                    if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                        // ici il faut envoyer les coordonnées de la case cliquée, son orientation et son type et faire un repaint apres

                        int x = (int) (e.getPoint().getX() / CELL_SIZE);
                        int y = (int) (e.getPoint().getY() / CELL_SIZE);

                        setCurrentBoatPlaced(x, y, (grid.horizontalPlacement ? NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL : NavalBattleProtocol.BOAT_ORIENTATION.VERTICAL));
                        setNextBoatPlace();
                        cursorLocation = null;
                        repaint();

                        MyGrid casted = (MyGrid) this.grid;

                        if (casted.getBoatPlace() == null) {
                            casted.allBoatsHaveBeenPlaced();
                        }

                    } // click droit
                    else if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                        horizontalPlacement = !horizontalPlacement;
                        lastCursorLocation = null;

                        if (!validPlacement()) {
                            horizontalPlacement = !horizontalPlacement;
                            return;
                        }

                        repaint();
                    }
                }
            }
        }
    }

    private abstract class GridArea extends JPanel {

        public abstract NavalBattleProtocol.COORDINATE_TYPE[][] getGridForDrawing();

        public abstract void makeBlink();

        public abstract void stopBlink();
        private static final long serialVersionUID = 4301030987169745017L;

        private final int cellNumber;
        private final int cellSize;
        private final int totalSize;
        protected Rectangle cellsLocations[][] = new Rectangle[CELL_NUMBER][CELL_NUMBER];

        protected boolean horizontalPlacement;
        protected Point cursorLocation;

        public boolean isHorizontalPlacement() {
            return horizontalPlacement;
        }

        private void drawPictureOnCell(int x, int y, Graphics2D gridGraphics2D, BufferedImage imageToDraw) {
            int posX = CELL_SIZE * x;
            int posY = CELL_SIZE * y;

            gridGraphics2D.drawImage(imageToDraw, null, posX, posY);
        }

        public GridArea(int cellNumber, int cellSize) {
            this.cellNumber = cellNumber;
            this.cellSize = cellSize;
            this.totalSize = cellNumber * cellSize;
            setOpaque(false);

            for (int x = 0; x < CELL_NUMBER; ++x) {
                for (int y = 0; y < CELL_NUMBER; ++y) {
                    cellsLocations[x][y] = new Rectangle(x * CELL_SIZE,
                            y * CELL_SIZE,
                            CELL_SIZE,
                            CELL_SIZE);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(totalSize + 1, totalSize + 1);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp
                    = new GradientPaint(0.0f, 0.0f, new Color(40, 200, 140),
                            (float) (totalSize), (float) (totalSize), new Color(40, 180, 210));

            g2.setPaint(gp);
            g2.fillRect(0, 0, totalSize, totalSize);

            // bordures
            g2.setColor(new Color(0, 100, 90));

            for (int i = 1; i < cellNumber; ++i) {
                g2.drawLine(i * cellSize, 0, i * cellSize, totalSize);
                g2.drawLine(0, i * cellSize, totalSize, i * cellSize);
            }
            g2.setColor(Color.black);
            g2.draw3DRect(0, 0, totalSize, totalSize, false);
        }

        private void drawDamages(GridArea grid, Graphics2D g2, BufferedImage hitPic, BufferedImage sinkPic, BufferedImage missPic, BufferedImage fireCursor, BufferedImage fireCursorFobidden, BufferedImage bonusMine, BufferedImage bonusSat, BufferedImage revelationShip, BufferedImage revelationNoShip) {
            NavalBattleProtocol.COORDINATE_TYPE[][] gridForDrawing = grid.getGridForDrawing();

            for (int x = 0; x < gridForDrawing.length; ++x) {
                for (int y = 0; y < gridForDrawing[x].length; ++y) {
                    switch (gridForDrawing[x][y]) {
                        case BOAT:
                            // NOT APPLICABLE HERE
                            // DO NOT WRITE CODE HERE
                            break;
                        case DAMAGED:
                            this.drawPictureOnCell(x, y, g2, hitPic);
                            break;
                        case SINKED:
                            this.drawPictureOnCell(x, y, g2, sinkPic);
                            break;
                        case SATELLITE:
                            this.drawPictureOnCell(x, y, g2, bonusSat);
                            break;
                        case MINE:
                            this.drawPictureOnCell(x, y, g2, bonusMine);
                            break;
                        case NOTHING:
                            break;
                        case FIRED_BUT_DID_NOT_DAMAGE_ANYTHING:
                            this.drawPictureOnCell(x, y, g2, missPic);
                            break;
                        case REAVEALED_HAS_SHIP:
                            this.drawPictureOnCell(x, y, g2, revelationShip);
                            break;
                        case REAVEALED_HAS_NO_SHIP:
                            this.drawPictureOnCell(x, y, g2, revelationNoShip);
                            break;
                    }
                }
            }
        }
    }
}
