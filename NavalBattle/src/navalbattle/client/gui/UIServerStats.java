package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import navalbattle.client.gui.actionlisteners.ALServerStats;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.gui.utils.JTableColumnsAdjuster;
import navalbattle.client.gui.utils.PicManip;
import navalbattle.client.viewmodelmessages.ExternalIPReceived;
import navalbattle.client.viewmodelmessages.InternalIPReceived;
import navalbattle.client.models.ModelServerStats;
import navalbattle.datahelper.DateTimeHelper;
import navalbattle.datamodel.DMGameHistory;
import navalbattle.lang.LanguageHelper;
import navalbattle.server.NavalBattleClientHandler;
import navalbattle.server.UsernameNotYetReceivedException;
import navalbattle.server.GamePlaying;

public class UIServerStats extends JFrame implements Observer {

    private static final long serialVersionUID = 3562034473955614333L;

    private static final String UI_NAME = "ui_serverStats";
    private static final String ICON = " ";

    private static final String WINNER_ICON_PATH = "pictures/icons/winner.gif";
    private final ModelServerStats model;
    private final LanguageHelper lh;

    private JLabel locAdrIpField;
    private JLabel extAdrIpField;
    private JLabel passField;
    private JLabel playerOne;
    private JLabel playerTwo;
    private JTable historyTable;

    public UIServerStats(ModelServerStats model) throws IOException {
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
        this.setPreferredSize(new Dimension(390, 700));
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponents(JPanel pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        // contour
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        JPanel buttonBackContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButtonDesign buttonBack = new JButtonDesign(lh.getTextDef(UI_NAME, "BACK"));
        buttonBackContainer.add(buttonBack);
        buttonBackContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        pane.add(buttonBackContainer);

        JPanel statsPanelContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel statsPanel = new JPanel(new GridLayout(5, 2));
        
        GamePlaying currentGameInfos = this.model.getCurrentGame();
        ArrayList<NavalBattleClientHandler> players = currentGameInfos == null ? null : currentGameInfos.getPlayers();
        
        JLabel locAdrIpText = new JLabel(lh.getTextDef(UI_NAME, "LOCAL_IP_ADDRESS"));
        locAdrIpText.setAlignmentX(LEFT_ALIGNMENT);
        locAdrIpField = new JLabel("Please wait ...");
        JLabel extAdrIpText = new JLabel(lh.getTextDef(UI_NAME, "EXTERNAL_IP_ADDRESS"));
        extAdrIpField = new JLabel("Please wait ...");
        JLabel passText = new JLabel(lh.getTextDef(UI_NAME, "PASSWORD"));
        passField = new JLabel(currentGameInfos == null ? "-" : currentGameInfos.getParameters().getPassword());
        JLabel currentGame = new JLabel(lh.getTextDef(UI_NAME, "CURRENT_GAME"));
        

        String username1;
        String username2;
        
        try {
            username1 = ((players == null || players.size() < 1) ? "-" : players.get(0).getUsername());
        } catch (UsernameNotYetReceivedException ex) {
            username1 = "n/a";
        }
        
        try {
            username2 = ((players == null || players.size() < 2) ? "-" : players.get(1).getUsername());
        } catch (UsernameNotYetReceivedException ex) {
            username2 = "n/a";
        }

        playerOne = new JLabel(username1);
        
        JLabel blank = new JLabel("");
        playerTwo = new JLabel(username2);

        statsPanel.add(locAdrIpText);
        statsPanel.add(locAdrIpField);
        statsPanel.add(extAdrIpText);
        statsPanel.add(extAdrIpField);
        statsPanel.add(passText);
        statsPanel.add(passField);
        statsPanel.add(currentGame);
        statsPanel.add(playerOne);
        statsPanel.add(blank);
        statsPanel.add(playerTwo);

        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanelContainer.add(statsPanel);
        statsPanelContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        pane.add(statsPanelContainer);

        
        JPanel historyPaneContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel historyPane = new JPanel();
        historyPane.setLayout(new BoxLayout(historyPane, BoxLayout.Y_AXIS));
        
        JLabel historyLabel = new JLabel(lh.getTextDef(UI_NAME, "HISTORY"));
        historyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyPane.add(historyLabel);

        JPanel historyTablePane = new JPanel();
        historyTable = new JTable(new HistoricTableModel(this));
        
        try {
            setWinnerIcon((HistoricTableModel) historyTable.getModel());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        historyTable.setFillsViewportHeight(true);
        historyTable.setEnabled(false);
        historyTable.setShowGrid(false);
        historyTable.setRowHeight(20);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        historyTable.getColumnModel().getColumn(2).setMinWidth(100);
        historyTable.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTableColumnsAdjuster.adjustColumns(historyTable);
        
        historyTablePane.add(historyTable);
        historyTablePane.setAlignmentX(Component.LEFT_ALIGNMENT);
        //historyPane.add(historyTablePane);
        
        JScrollPane historyScroll = new JScrollPane(historyTablePane);
        historyScroll.setMaximumSize(new Dimension(640, 540));
        historyPane.add(historyScroll);
        
        historyPaneContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyPaneContainer.add(historyPane);
        pane.add(historyPaneContainer);

        buttonBack.setActionCommand("back");

        ActionListener al = new ALServerStats(this);
        buttonBack.addActionListener(al);

        pane.getRootPane().setDefaultButton(buttonBack);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass() == ExternalIPReceived.class)
        {
            ExternalIPReceived casted = (ExternalIPReceived)arg;
            String ip = casted.getIp();
            this.extAdrIpField.setText(ip == null ? lh.getTextDef(UI_NAME, "CANNOT_GET_EXTERNAL_IP_ADDR") : ip);
        }
        else if (arg.getClass() == InternalIPReceived.class)
        {
            InternalIPReceived casted = (InternalIPReceived)arg;
            String ip = casted.getIp();
            this.locAdrIpField.setText(ip == null ? lh.getTextDef(UI_NAME, "CANNOT_GET_INTERNAL_IP_ADDR") : ip);
        }
    }

    private class HistoricTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -8909549809875743360L;

        private final UIServerStats view;
        private String[] columnNames = {ICON,
            lh.getTextDef(UI_NAME, "WINNER"),
            lh.getTextDef(UI_NAME, "LOSER"),
            lh.getTextDef(UI_NAME, "DATE")};
        private Object[][] data;

        private HistoricTableModel(UIServerStats view) {
            this.view = view;
            
            ArrayList<DMGameHistory> gameHistory = this.view.getGameHistory();
            
            this.data = new Object[gameHistory.size()][];
            
            int r = 0;
            for (DMGameHistory gh : gameHistory)
            {
                Object[] row = new Object[4];
                row[0] = "";
                row[1] = gh.getUsernameWinner();
                row[2] = gh.getUsernameNotWinner();
                row[3] = DateTimeHelper.dateToUniversalDateString(gh.getDate());
                
                this.data[r++] = row;
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
    
    public ArrayList<DMGameHistory> getGameHistory()
    {
        return this.model.getGameHistory();
    }

    private void setWinnerIcon(HistoricTableModel table) throws IOException {
        BufferedImage buffWin = ImageIO.read(new File(WINNER_ICON_PATH));
        buffWin = PicManip.resizeImg(buffWin, 16, 16);
        ImageIcon winIcon = new ImageIcon(buffWin);

        for (int i = 0; i < table.getRowCount(); ++i) {
            table.setValueAt(winIcon, i, 0);
        }
    }
}
