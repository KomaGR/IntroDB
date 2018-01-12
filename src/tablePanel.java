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
import java.util.NoSuchElementException;
import java.util.Vector;

public class tablePanel extends JPanel implements ActionListener,TableModelListener {
    static dataPort dataPort = new dataPort();
    static JScrollPane scrollPane = null;
    private static mainFrame parentFrame = null;
    static connectionManager sql_manager = null;
    String[] columnNames;
    private static String tableName;
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
            try {
                result[i] = table.getModel().getValueAt(row, i).toString();
            } catch (NullPointerException e) {
                result[i] = null;   // Null pointer exception here just means that field is empty
            }
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
        JMenuItem getTableItem = null;
        JMenuItem getTableItem2 = null;
        JMenuItem getTableItem3 = null;
        JMenuItem getTableItem4 = null;
        if (tableName.equals("Customer")){
            getTableItem2 = new JMenuItem("Get clients' start location");
            getTableItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        parentFrame.cPanel.setNoSelectedOption();
                        parentFrame.changeContent(sql_manager.getClientStartLoc());
                }
            });
        }
        if (tableName.equals("Store")) {
            getTableItem = new JMenuItem("Get clients registered here");
            getTableItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    try {
                        parentFrame.cPanel.setNoSelectedOption();
                        parentFrame.changeContent(sql_manager.getRegisteredAt(resultTable.getValueAt(resultTable.getSelectedRow(),4)));
                    } catch (SQLException je) {
                        System.out.println(je.getMessage());
                    } catch (NoSuchElementException ex) {
                        JOptionPane.showMessageDialog(parentFrame,"It appears no customers have" +
                                " registered at " + resultTable.getValueAt(resultTable.getSelectedRow(),4)+".","No" +
                                " Results",JOptionPane.ERROR_MESSAGE);
                    }

                }
            });
        }
        if (tableName.equals("Store")) {
            getTableItem2 = new JMenuItem("Get damaged vehicles here");
            getTableItem2.setEnabled(false);
            getTableItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    ResultSet rs = sql_manager.getDamagedVehiclesAt((resultTable.getValueAt(resultTable.getSelectedRow(),4).toString()));
                    try {
                        if (!rs.next()){
                            JOptionPane.showMessageDialog(parentFrame,"No vehicles have been placed in this table.",
                                    "No Result",JOptionPane.ERROR_MESSAGE);
                            rs.beforeFirst();
                        } else {
                            rs.beforeFirst();
                            parentFrame.cPanel.setNoSelectedOption();
                            parentFrame.changeContent(rs);

                        }
                    } catch (SQLException ke) {
                        System.out.println(ke.getMessage());
                    }
                }
            });
        }
        if (tableName.equals("Store")) {
            getTableItem3 = new JMenuItem("More Info...");
            getTableItem3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        parentFrame.cPanel.setNoSelectedOption();
                        parentFrame.changeContent(sql_manager.getStoresInfo());
                }
            });
        }
        if (tableName.equals("Vehicle")) {
            getTableItem = new JMenuItem("Show Fuel Types");
            getTableItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose fueltype");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.getFuelTypes());
                }
            });
        }
        if (tableName.equals("Vehicle")) {
            getTableItem2 = new JMenuItem("In need of repair");
            getTableItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose repair");
                    ResultSet rs = sql_manager.vehiclesToRepair();
                    try {
                        if (!rs.next()){
                            JOptionPane.showMessageDialog(parentFrame,"No vehicles have been placed in this table.",
                                    "No Result",JOptionPane.ERROR_MESSAGE);
                            rs.beforeFirst();
                        } else {
                            rs.beforeFirst();
                            parentFrame.cPanel.setNoSelectedOption();
                            parentFrame.changeContent(rs);

                        }

                    } catch (SQLException ke) {
                        System.out.println(ke.getMessage());
                    }
                }
            });
        }
        if (tableName.equals("Vehicle")) {
            getTableItem3 = new JMenuItem("Assign fuel type");
            getTableItem3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose fueltype");
                    ResultSet rs = sql_manager.vehiclesToRepair();
                    String[] fields = {"License Plate","Fuel Type"};
                    String License_Plate = resultTable.getValueAt(resultTable.getSelectedRow(),0).toString();
                    String[] values = {License_Plate,""};
                    JForm editForm = new JForm(fields,values);
                    editForm.setEditability(0,false);
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
                                    sql_manager.assignFuelTypeToVehicle(License_Plate,editedFields[1]);
                                    parentFrame.refreshContent();
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
                }
            });
        }
        if (tableName.equals("Payment_Transaction")) {
            getTableItem = new JMenuItem("Show aggregate");
            getTableItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose aggregate");
                    String agg = sql_manager.getPaymentAggregate();
                    JOptionPane.showMessageDialog(parentFrame,"Aggregate is: " + agg,"Aggregate",JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }



        if (tableName.equals("Customer")) {
            getTableItem = new JMenuItem("Get Customers per city");
            getTableItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose customer/city");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.showClientsPerCity());

                }
            });
        }
        if (tableName.equals("Customer")) {
            getTableItem2 = new JMenuItem("Good customers");
            getTableItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose good customer");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.bestClients());
                }
            });
        }
        if (tableName.equals("Customer")) {
            getTableItem3 = new JMenuItem("Prepaid");
            getTableItem3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose prepaid");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.showPrepaid());
                }
            });
        }
        if (tableName.equals("Employee")) {
            getTableItem = new JMenuItem("Positions");
            getTableItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose positions");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.getPositions());
                }
            });

        }
        if (tableName.equals("Employee")) {
            getTableItem2 = new JMenuItem("Current employees");
            getTableItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose statute");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.getStatute());
                }
            });

        }
        if (tableName.equals("Employee")) {
            getTableItem3 = new JMenuItem("Position coverage");
            getTableItem3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose coverage");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.getCoverage());
                }
            });

        }
        if (tableName.equals("Employee")) {
            getTableItem4 = new JMenuItem("Low coverage positions");
            getTableItem4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose low coverage");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.getLowCoveragePositions());
                }
            });

        }

        if (tableName.equals("Rents")) {
            getTableItem = new JMenuItem("Show minimum charged car");
            getTableItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose Least Profit");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.getMinRentInfo());

                }
            });
        }
        if (tableName.equals("Rents")) {
            getTableItem2 = new JMenuItem("High season rents");
            getTableItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose highseason");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.highSeasonRents());

                }
            });
        }
        if (tableName.equals("Rents")) {
            getTableItem3 = new JMenuItem("Best rents");
            getTableItem3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose bestrents");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.highSeasonRents());

                }
            });
        }
        if (tableName.equals("Rents")) {
            getTableItem4 = new JMenuItem("Top 10 Rents");
            getTableItem4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Right-click performed on table and choose topten");
                    parentFrame.cPanel.setNoSelectedOption();
                    parentFrame.changeContent(sql_manager.topTen());

                }
            });
        }
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

        if (!sql_manager.editable(tableName) || (parentFrame != null &&parentFrame.cPanel.getSelectedOption() == null)) {
            editItem.setEnabled(false);
            insertItem.setEnabled(false);
            deleteItem.setEnabled(false);
        }

        rightClickMenu.add(editItem);
        rightClickMenu.add(insertItem);
        rightClickMenu.add(deleteItem);
        if (getTableItem != null) rightClickMenu.add(getTableItem);
        if (getTableItem2 != null) rightClickMenu.add(getTableItem2);
        if (getTableItem3 != null) rightClickMenu.add(getTableItem3);
        if (getTableItem4 != null) rightClickMenu.add(getTableItem4);

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
            if (location != null) popUpFrame.setLocation(location);   //set pop up location
            else    popUpFrame.setLocationRelativeTo(this);
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
