package navalbattle.client.gui.utils;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class JTableColumnsAdjuster {
    /*
     *  Adjust the widths of all the columns in the table
     */

    public static void adjustColumns(JTable table) {
        TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumn(table, i);
        }
    }

    /*
     *  Adjust the width of the specified column in the table
     */
    private static void adjustColumn(JTable table, final int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        int columnHeaderWidth = getColumnHeaderWidth(table, column);
        int columnDataWidth = getColumnDataWidth(table, column);
        int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

        updateTableColumn(table, column, preferredWidth);
    }


    /*
     *  Calculated the width based on the column name
     */
    private static int getColumnHeaderWidth(JTable table, int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /*
     *  Calculate the width based on the widest cell er for the
     *  given column.
     */
    private static int getColumnDataWidth(JTable table, int column) {
        int preferredWidth = 0;
        int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

        for (int row = 0; row < table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(table, row, column));

			//  We've exceeded the maximum width, no need to check other rows
            if (preferredWidth >= maxWidth) {
                break;
            }
        }

        return preferredWidth;
    }


    /*
     *  Get the preferred width for the specified cell
     */
    private static int getCellDataWidth(JTable table, int row, int column) {
        //  Invoke the renderer for the cell to calculate the preferred width

        TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        Component c = table.prepareRenderer(cellRenderer, row, column);
        int width = c.getPreferredSize().width + table.getIntercellSpacing().width;

        return width;
    }

    /*
     *  Update the TableColumn with the newly calculated width
     */
    private static void updateTableColumn(JTable table, int column, int width) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        width += 10;

        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }
}
