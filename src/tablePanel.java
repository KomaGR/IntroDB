import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

public class tablePanel extends JPanel implements ActionListener,TableModelListener {
    static dataPort dataPort = new dataPort();
    JScrollPane scrollPane = null;
    private mainFrame parentFrame = null;
    static connectionManager sql_manager = null;
    private String tableName;

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
        JTable resultTable = changeTableData(rs);
        String[] columnNames = dataPort.getColumnNames(rs);
        Vector<String[]> data = dataPort.getData(rs);
//
//        resultTable.setAutoCreateRowSorter(true);
//        resultTable.setPreferredScrollableViewportSize(new Dimension(900, 500));
//        resultTable.setFillsViewportHeight(false);
//        resultTable.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent mouseEvent) {
//                JTable table = (JTable) mouseEvent.getSource();
//                Point point = mouseEvent.getPoint();
//                int row = table.rowAtPoint(point);
//                row = table.convertRowIndexToModel(row);
//                if (mouseEvent.getClickCount() == 2) {
//                    //TODO: Case of no allowed edit?
//                    System.out.println("Double click on row " + Integer.toString(row+1));
//                    JForm editForm = new JForm(columnNames,getRowAt(resultTable,row,columnNames.length));
//                    JFrame popUpFrame = new JFrame("Edit row " + row);
//                    popUpFrame.setLayout(new FlowLayout());
//                    popUpFrame.add(editForm);
//                    popUpFrame.setLocation(mouseEvent.getLocationOnScreen());   //set pop up location
//                    popUpFrame.setAlwaysOnTop(true);    //Make it stay on top
//
//                    JPanel askDonePan = new JPanel();
//                    // OK Button and Listener
//                    JButton okButton = new JButton("OK");
//                    okButton.addMouseListener(new MouseAdapter() {
//                        @Override
//                        public void mouseClicked(MouseEvent mouseEvent) {
//                            super.mouseClicked(mouseEvent);
//                            System.out.println("Enqueue changes");
//                            //Close window and enqueue changes
//                            popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
//                            String[] editedFields = editForm.getTextFields();
//                            if (editedFields != null) {
//                                //enqueue change
//                                String query = "UPDATE " + " "; //TODO: Find out where table name is
//                                sql_manager.getqBuffer().add(query);
//
//                            }
//                        }
//                    });
//                    askDonePan.add(okButton);
//                    // Cancel button and onClick listener for window closing
//                    JButton cancelButton = new JButton("Cancel");
//                    cancelButton.addMouseListener(new MouseAdapter() {
//                        @Override
//                        public void mouseClicked(MouseEvent mouseEvent) {
//                            super.mouseClicked(mouseEvent);
//                            System.out.println("Discard changes");
//                            popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
//                        }
//                    });
//                    askDonePan.add(cancelButton);
//
//                    popUpFrame.add(askDonePan);
//
//                    popUpFrame.setSize(new Dimension(300,columnNames.length*40+30));
//                    popUpFrame.setVisible(true);
//                    popUpFrame.requestFocus();
//                    //TODO: Get changes;
//                }
//            }
//        });


        //Create the scroll pane and add the table to it.
//        scrollPane = new JScrollPane(resultTable);
        //Add the scroll pane to this panel.
//        add(scrollPane);
        //Create and set up the content pane.

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
        String[] columnNames = dataPort.getColumnNames(rs);
        vecColNames.addAll(Arrays.asList(columnNames));
        Vector<String[]> data = dataPort.getData(rs);

        JTable resultTable = new JTable(dataPort.toObjectArray(data),dataPort.getColumnNames(rs)) {
            private static final long serialVersionUID = 1L;
            //Override editability
            public boolean isCellEditable(int row, int column) {
                return false;
            };
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
                if (mouseEvent.getClickCount() == 2) {
                    //TODO: Case of no allowed edit?
                    System.out.println("Double click on row " + Integer.toString(row+1));
                    JForm editForm = new JForm(columnNames,getRowAt(resultTable,row,columnNames.length));
                    JFrame popUpFrame = new JFrame("Edit row " + row);
                    popUpFrame.setLayout(new FlowLayout());
                    popUpFrame.add(editForm);
                    popUpFrame.setLocation(mouseEvent.getLocationOnScreen());   //set pop up location
                    popUpFrame.setAlwaysOnTop(true);    //Make it stay on top

                    JPanel askDonePan = new JPanel();
                    // OK Button and Listener
                    JButton okButton = new JButton("OK");
                    okButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent mouseEvent) {
                            super.mouseClicked(mouseEvent);
                            System.out.println("Enqueue changes");
                            //Close window and enqueue changes
                            popUpFrame.dispatchEvent(new WindowEvent(popUpFrame, WindowEvent.WINDOW_CLOSING));
                            String[] editedFields = editForm.getTextFields();
                            if (editedFields != null) {
                                //enqueue change
                                try {
                                    sql_manager.updateTable(tableName,editedFields);
//                                    sql_manager.getqBuffer().add(query);
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }

                            }
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

                    popUpFrame.setSize(new Dimension(300,columnNames.length*40+30));
                    popUpFrame.setVisible(true);
                    popUpFrame.requestFocus();
                    //TODO: Get changes;
                }
            }
        });


        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(resultTable);
        return resultTable;

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
