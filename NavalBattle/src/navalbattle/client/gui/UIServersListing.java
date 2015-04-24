package navalbattle.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import navalbattle.client.gui.actionlisteners.ALServerListing;
import navalbattle.client.gui.customcomponents.JButtonDesign;
import navalbattle.client.gui.utils.ComponentMover;
import navalbattle.client.gui.utils.JTableColumnsAdjuster;
import navalbattle.client.gui.utils.PicManip;
import navalbattle.client.models.ModelServersListing;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.beaconing.client.DiscoveredServer;

public class UIServersListing extends JFrame implements Observer {

    private static final long serialVersionUID = -8379327763047478016L;

    private static final String UI_NAME = "ui_serversListing";

    private static final String CHECK_ICON_PATH = "pictures/icons/check.gif";
    private static final String CROSS_ICON_PATH = "pictures/icons/cross.gif";
    private static final String LOCK_ICON_PATH = "pictures/icons/lock.gif";
    private static final String UNLOCK_ICON_PATH = "pictures/icons/unlock.gif";

    private JTable ServListTable;
    private ServTableModel servTableModel;
    private JScrollPane sp;
    private final Object lockPainting = new Object();

    private final ModelServersListing model;
    private final LanguageHelper lh;

    public UIServersListing(ModelServersListing model) throws IOException {
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
        pane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        JPanel buttonManualContainer = new JPanel();
        JButtonDesign buttonManualSpecify = new JButtonDesign(this.lh.getTextDef(UI_NAME, "SPECIFY_MANUALLY"));
        buttonManualContainer.add(buttonManualSpecify);
        pane.add(buttonManualContainer);

        JPanel tablePane = new JPanel();

        synchronized (this.lockPainting) {
            this.servTableModel = new ServTableModel(this.model.getDiscoveredServers());
            ServListTable = new JTable(this.servTableModel);

            try {
                Object mObj = ServListTable.getModel();

                if (mObj != null) {
                    replaceBoolWithIcon((ServTableModel) mObj);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            sp = new JScrollPane(ServListTable);
            this.setTableProperties();
        }

        tablePane.add(sp);

        pane.add(tablePane);

        JPanel buttonConnectContainer = new JPanel();
        JButtonDesign buttonConnect = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CONNECT"));
        buttonConnectContainer.add(buttonConnect);
        pane.add(buttonConnectContainer);

        JPanel buttonCancelContainer = new JPanel();
        JButtonDesign buttonCancel = new JButtonDesign(this.lh.getTextDef(UI_NAME, "CANCEL"));
        buttonCancelContainer.add(buttonCancel);
        pane.add(buttonCancelContainer);

        buttonManualSpecify.setActionCommand("manual");
        buttonConnect.setActionCommand("connect");
        buttonCancel.setActionCommand("cancel");

        ActionListener al = new ALServerListing(this);
        buttonManualSpecify.addActionListener(al);
        buttonConnect.addActionListener(al);
        buttonCancel.addActionListener(al);

        pane.getRootPane().setDefaultButton(buttonConnect);
    }

    public void setTableProperties() {
        ServListTable.setRowHeight(30);
        ServListTable.setFillsViewportHeight(true);
        ServListTable.getTableHeader().setReorderingAllowed(false);
        ServListTable.getTableHeader().setResizingAllowed(false);
        ServListTable.setRowSelectionAllowed(true);
        ServListTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ServListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer renderString = (DefaultTableCellRenderer) ServListTable.getDefaultRenderer(String.class);
        renderString.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer renderInt = (DefaultTableCellRenderer) ServListTable.getDefaultRenderer(Integer.class);
        renderInt.setHorizontalAlignment(JLabel.CENTER);
        JTableColumnsAdjuster.adjustColumns(ServListTable);

        Dimension dimTable = ServListTable.getPreferredSize();
        sp.setPreferredSize(new Dimension(dimTable.width + 3, dimTable.height + 23));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.getClass() == ModelServersListing.class) {
            HashSet<DiscoveredServer> discoveredServers = (HashSet<DiscoveredServer>) arg;
            servTableModel.setServers(discoveredServers);

            synchronized (this.lockPainting) {

                try {
                    replaceBoolWithIcon(servTableModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.setTableProperties();
                ((AbstractTableModel) ServListTable.getModel()).fireTableDataChanged();
                ServListTable.repaint();
            }
        }
    }

    public void userWantsToManuallySpecifiyToServer() {
        this.model.userWantsToManuallySpecifiyServerToConnectTo();
    }

    public void connectToSelectedServer() {
        int row = ServListTable.convertRowIndexToModel(ServListTable.getSelectedRow());

        if (row < 0) {
            JOptionPane.showMessageDialog(null, this.lh.getTextDef(UI_NAME, "ERROR_NO_SERVER_SELECTED"), this.lh.getTextDef(UI_NAME, "ERROR"), JOptionPane.ERROR_MESSAGE);
        } else {
            Object mObj = ServListTable.getModel();

            if (mObj != null) {
                ServTableModel m = (ServTableModel) mObj;
                Object[] data = m.getRowAt(row);

                this.model.userWantsToConnectToAServer((String) data[0]); // passing the IP
            }
        }
    }

    private class ServTableModel extends AbstractTableModel {

        private Object[][] data;

        public synchronized Object[] getRowAt(int index) {
            return data[index];
        }

        public ServTableModel(HashSet<DiscoveredServer> servers) {
            this.setServers(servers);
        }

        private static final long serialVersionUID = -5985198864046684897L;

        private final String[] columnNames = {
            lh.getTextDef(UI_NAME, "HOST_NAME"),
            lh.getTextDef(UI_NAME, "PASS_PROTECTED"),
            lh.getTextDef(UI_NAME, "NAME"),
            lh.getTextDef(UI_NAME, "PLAYERS"),
            lh.getTextDef(UI_NAME, "FREE_SPACE_AVAILABLE")};

        /*
         private Object[][] data
         = {
         {new Boolean(true), "Luc's Server", new Integer(2), new Boolean(false)},
         {new Boolean(false), "Hophop", new Integer(1), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)},
         {new Boolean(false), "NoName", new Integer(0), new Boolean(true)}
         };
         */
        @Override
        public synchronized int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public synchronized int getRowCount() {
            return data.length;
        }

        @Override
        public synchronized String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public synchronized Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public synchronized void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        @Override
        public synchronized Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        private synchronized void setServers(HashSet<DiscoveredServer> discoveredServers) {
            data = new Object[discoveredServers.size()][];

            int index = 0;
            for (DiscoveredServer server : discoveredServers) {

                data[index] = new Object[5];
                data[index][0] = server.getHostName();
                data[index][1] = server.isAuthenticationRequired();
                data[index][2] = server.getServerName();
                data[index][3] = server.getPlayersCount();
                data[index][4] = !(server.isServerFull());

                ++index;
            }
        }
    }

//	/*
//	 *  Adjust the widths of all the columns in the table
//	 */
//    private void adjustColumns()
//	{
//		TableColumnModel tcm = ServListTable.getColumnModel();
//
//		for (int i = 0; i < tcm.getColumnCount(); i++)
//		{
//			adjustColumn(i);
//		}
//	}
//
//	/*
//	 *  Adjust the width of the specified column in the table
//	 */
//	private void adjustColumn(final int column)
//	{
//		TableColumn tableColumn = ServListTable.getColumnModel().getColumn(column);
//
//		if (! tableColumn.getResizable()) return;
//
//		int columnHeaderWidth = getColumnHeaderWidth( column );
//		int columnDataWidth   = getColumnDataWidth( column );
//		int preferredWidth    = Math.max(columnHeaderWidth, columnDataWidth);
//
//		updateTableColumn(column, preferredWidth);
//	}
//
//
//	/*
//	 *  Calculated the width based on the column name
//	 */
//	private int getColumnHeaderWidth(int column)
//	{
//		TableColumn tableColumn = ServListTable.getColumnModel().getColumn(column);
//		Object value = tableColumn.getHeaderValue();
//		TableCellRenderer renderer = tableColumn.getHeaderRenderer();
//
//		if (renderer == null)
//		{
//			renderer = ServListTable.getTableHeader().getDefaultRenderer();
//		}
//
//		Component c = renderer.getTableCellRendererComponent(ServListTable, value, false, false, -1, column);
//		return c.getPreferredSize().width;
//	}
//
//	/*
//	 *  Calculate the width based on the widest cell er for the
//	 *  given column.
//	 */
//	private int getColumnDataWidth(int column)
//	{
//		int preferredWidth = 0;
//		int maxWidth = ServListTable.getColumnModel().getColumn(column).getMaxWidth();
//
//		for (int row = 0; row < ServListTable.getRowCount(); row++)
//		{
//    		preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
//
//			//  We've exceeded the maximum width, no need to check other rows
//
//			if (preferredWidth >= maxWidth)
//			    break;
//		}
//
//		return preferredWidth;
//	}
//	
//
//	/*
//	 *  Get the preferred width for the specified cell
//	 */
//	private int getCellDataWidth(int row, int column)
//	{
//		//  Invoke the renderer for the cell to calculate the preferred width
//
//		TableCellRenderer cellRenderer = ServListTable.getCellRenderer(row, column);
//		Component c = ServListTable.prepareRenderer(cellRenderer, row, column);
//		int width = c.getPreferredSize().width + ServListTable.getIntercellSpacing().width;
//
//		return width;
//	}
//
//	/*
//	 *  Update the TableColumn with the newly calculated width
//	 */
//	private void updateTableColumn(int column, int width)
//	{
//		final TableColumn tableColumn = ServListTable.getColumnModel().getColumn(column);
//
//		if (! tableColumn.getResizable()) return;
//
//		width += 10;
//
//		ServListTable.getTableHeader().setResizingColumn(tableColumn);
//		tableColumn.setWidth(width);
//	}
    private void replaceBoolWithIcon(ServTableModel table) throws IOException {

        synchronized (this.lockPainting) {
            BufferedImage buffCheckIcon = ImageIO.read(new File(CHECK_ICON_PATH));
            buffCheckIcon = PicManip.resizeImg(buffCheckIcon, 20, 16);
            ImageIcon checkIcon = new ImageIcon(buffCheckIcon);

            BufferedImage buffCrossIcon = ImageIO.read(new File(CROSS_ICON_PATH));
            buffCrossIcon = PicManip.resizeImg(buffCrossIcon, 16, 16);
            ImageIcon crossIcon = new ImageIcon(buffCrossIcon);

            BufferedImage buffLock = ImageIO.read(new File(LOCK_ICON_PATH));
            buffLock = PicManip.resizeImg(buffLock, 16, 16);
            ImageIcon lockIcon = new ImageIcon(buffLock);

            BufferedImage buffUnlock = ImageIO.read(new File(UNLOCK_ICON_PATH));
            buffUnlock = PicManip.resizeImg(buffUnlock, 16, 16);
            ImageIcon unlockIcon = new ImageIcon(buffUnlock);

            for (int i = 0; i < table.getRowCount(); ++i) {
                for (int j = 0; j < table.getColumnCount(); ++j) {
                    if (table.getValueAt(i, j).getClass() == Boolean.class) {
                        if (((Boolean) table.getValueAt(i, j))) {
                            if (j == 1) {
                                table.setValueAt(lockIcon, i, j);
                            } else if (j == 4) {
                                table.setValueAt(checkIcon, i, j);
                            }
                        } else {
                            if (j == 1) {
                                table.setValueAt(unlockIcon, i, j);
                            } else if (j == 4) {
                                table.setValueAt(crossIcon, i, j);
                            }
                        }
                    }
                }
            }
        }
    }
}
