import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

public class tablePanel extends JPanel implements ActionListener,TableModelListener {
    static dataPort dataPort = new dataPort();
    JScrollPane scrollPane = null;
    private static mainFrame parentFrame = null;
    static connectionManager sql_manager = null;
    String[] columnNames;
    private String tableName;
    private JTable resultTable = null;

    public static void setSql_manager(connectionManager sql_manager) {
        tablePanel.sql_manager = sql_manager;
    }

    public void registerParent(mainFrame parent) {
        parentFrame = parent;
    }

    public tablePanel(ResultSet rs, connectionManager sql_manager) throws SQLException {
//        super(new GridLayout(1,0));
        super(new BorderLayout());
        setSql_manager(sql_manager);
        //Get data ready to display
        resultTable = changeTableData(rs);
        String[] columnNames = dataPort.getColumnNames(rs);
        Vector<String[]> data = dataPort.getData(rs);

    }
    private String[] getRowAt(JTable table, int row, int colNumber) {
        String[] result = new String[colNumber];
        for (int i = 0; i < colNumber; i++) {
            result[i] = table.getModel().getValueAt(row, i).toString();
        }
        return result;
    }

    public JTable changeTableData(ResultSet rs) {
        //Get data ready to display

        Vector<String> vecColNames = null;
        try {
            this.tableName = rs.getMetaData().getTableName(1);
            System.out.println(tableName);
            vecColNames = new Vector<>(rs.getMetaData().getColumnCount());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        columnNames = dataPort.getColumnNames(rs);
        vecColNames.addAll(Arrays.asList(columnNames));
        Vector<String[]> data = dataPort.getData(rs);

        resultTable = new JTable(dataPort.toObjectArray(data),dataPort.getColumnNames(rs)) {
            private static final long serialVersionUID = 1L;
            private Class editingClass;
            //Override editability
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            //Implement table cell tool tips. (for coolness)
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                    System.out.println(e1.getMessage());
                }

                return tip;
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    Class rowClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultRenderer(rowClass);
                } else {
                    return super.getCellRenderer(row, column);
                }
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    editingClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultEditor(editingClass);
                } else {
                    return super.getCellEditor(row, column);
                }
            }
            //  This method is also invoked by the editor when the value in the editor
            //  component is saved in the TableModel. The class was saved when the
            //  editor was invoked so the proper class can be created.
            @Override
            public Class<?> getColumnClass(int column) {
                return editingClass != null ? editingClass : super.getColumnClass(column);
            }


        };

        resultTable.setAutoCreateRowSorter(true);
        resultTable.setPreferredScrollableViewportSize(new Dimension(900, 500));
        resultTable.setFillsViewportHeight(false);
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();

                int row = table.rowAtPoint(point);
                System.out.println(row);
                row = table.convertRowIndexToModel(row);
                System.out.println(row);
                System.out.println(table.getValueAt(row,table.columnAtPoint(point)).getClass());
                if (mouseEvent.getClickCount() == 2) {
                    editRow(row,columnNames,mouseEvent.getLocationOnScreen());
                    //TODO: Get changes;
                }
            }
        });

        // Right Click Menu
        JPopupMenu rightClickMenu = new JPopupMenu();
        //Delete Item
        JMenuItem deleteItem = new JMenuItem("Delete Row");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Right-click performed on table and choose DELETE");
                deleteRow(resultTable.getSelectedRow());
            }
        });
        JMenuItem editItem = new JMenuItem("Edit Row");
        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Right-click performed on table and choose EDIT");
                editRow(resultTable.getSelectedRow(),columnNames,resultTable.getMousePosition());

            }
        });
        JMenuItem insertItem = new JMenuItem("Insert into " + tableName);
        insertItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Right-click performed on table and choose INSERT");
                insertRow();
            }
        });
        //select with right click
        rightClickMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = resultTable.rowAtPoint(SwingUtilities.convertPoint(rightClickMenu, new Point(0, 0), resultTable));
                        if (rowAtPoint > -1) {
                            resultTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        rightClickMenu.add(editItem);
        rightClickMenu.add(insertItem);
        rightClickMenu.add(deleteItem);
        resultTable.setComponentPopupMenu(rightClickMenu);



        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(resultTable);
        return resultTable;

    }


    private void deleteRow(int row) {
        int pkCol = sql_manager.getPKCol(tableName);
        //Delete row with primary key
        try {
            sql_manager.deleteRow(tableName,resultTable.getValueAt(row,pkCol));
            parentFrame.refreshContent();
        } catch (SQLException e) {
            System.out.println("Could not delete " + tableName + " row " + row);
        }
    }

    private void insertRow() {
        if (sql_manager.editable(tableName)) {
            JForm editForm = new JForm(columnNames, new String[columnNames.length]);
            editForm.setEditability(sql_manager.getPKCol(tableName),sql_manager.pkSuppliedByUser(tableName));
            JFrame popUpFrame = new JFrame("Insert into " + tableName);
            popUpFrame.setLayout(new FlowLayout());
            popUpFrame.add(editForm);
            popUpFrame.setLocation(parentFrame.getLocation());   //set pop up location
            popUpFrame.setAlwaysOnTop(true);
            JPanel askDonePan = new JPanel();
            // OK Button and Listener
            JButton okButton = new JButton("OK");
            okButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    super.mouseClicked(mouseEvent);

                    //Close window and enqueue changes
                    String[] editedFields = editForm.getTextFields();
                    popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
                    if (editedFields != null) {
                        //enqueue change
                        System.out.println("Enqueue changes");
                        try {
                            sql_manager.insertRow(tableName, editedFields);
                            parentFrame.refreshContent();
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                    } else
                        System.out.println("No values");

                }
            });
            askDonePan.add(okButton);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    super.mouseClicked(mouseEvent);
                    System.out.println("Discard changes");
                    popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
                }
            });
            askDonePan.add(cancelButton);

            popUpFrame.add(askDonePan);

            popUpFrame.setSize(new Dimension(350, columnNames.length * 35 + 50));
            popUpFrame.setVisible(true);
            popUpFrame.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,"The selected" +
                    " table is not editable.","Invalid Action",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editRow(int row, String[] columnNames, Point location) {
        if (sql_manager.editable(tableName)) {
            System.out.println("Double click on row " + Integer.toString(row + 1));
            JForm editForm = new JForm(columnNames, getRowAt(resultTable, row, columnNames.length));
            JFrame popUpFrame = new JFrame("Edit row " + row);
            popUpFrame.setLayout(new FlowLayout());
            popUpFrame.add(editForm);
            popUpFrame.setLocation(location);   //set pop up location
            popUpFrame.setAlwaysOnTop(true);    //Make it stay on top

            JPanel askDonePan = new JPanel();
            // OK Button and Listener
            JButton okButton = new JButton("OK");
            okButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    super.mouseClicked(mouseEvent);

                    //Close window and enqueue changes
                    popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
                    String[] editedFields = editForm.getTextFields();
                    if (editedFields != null) {
                        //enqueue change
                        System.out.println("Enqueue changes");
                        try {
                            sql_manager.updateTable(tableName, editedFields);
//                                    sql_manager.getqBuffer().add(query);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    System.out.println("No changes");
                }
            });
            askDonePan.add(okButton);
            // Cancel button and onClick listener for window closing
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    super.mouseClicked(mouseEvent);
                    System.out.println("Discard changes");
                    popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
                }
            });
            askDonePan.add(cancelButton);

            popUpFrame.add(askDonePan);

            popUpFrame.setSize(new Dimension(350, columnNames.length * 35 + 50));
            popUpFrame.setVisible(true);
            popUpFrame.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,"The selected" +
                    " table is not editable.","Invalid Action",JOptionPane.WARNING_MESSAGE);
        }
    }


    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
        //Table can't change
    }

}
