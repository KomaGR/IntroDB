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

    public void registerParent(mainFrame parent) {
        parentFrame = parent;
    }

    public tablePanel(ResultSet rs) throws SQLException {
//        super(new GridLayout(1,0));
        super(new BorderLayout());


        //Get data ready to display
        Vector<String> vecColNames = new Vector<>(rs.getMetaData().getColumnCount());
        String[] columnNames = dataPort.getColumnNames(rs);
        vecColNames.addAll(Arrays.asList(columnNames));
        Vector<String[]> data = dataPort.getData(rs);
//        JTable resultTable = new JTable(data,vecColNames);
//        JTable resultTable = new JTable(dataPort.toObjectArray(data),dataPort.getColumnNames(rs));
        JTable resultTable = new JTable(dataPort.toObjectArray(data),dataPort.getColumnNames(rs)) {
            private static final long serialVersionUID = 1L;
            //Override editability
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        resultTable.setPreferredScrollableViewportSize(new Dimension(900, 500));
        resultTable.setFillsViewportHeight(false);
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2) {
                    System.out.println("Double click on row" + Integer.toString(row+1));
                    //TODO: Pass click to the mainFrame
                    if (parentFrame != null) {
                        parentFrame.editRow()
                    }

                }
            }
        });


        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(resultTable);
        //Add the scroll pane to this panel.
        renderView();
//        add(scrollPane);
        //Create and set up the content pane.

    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    private void renderView() {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {

    }
}
