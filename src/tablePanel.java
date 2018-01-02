import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

public class tablePanel extends JPanel implements ItemListener {
    static dataPort dataPort = new dataPort();

    public tablePanel(ResultSet rs) throws SQLException {
        super(new GridLayout(1,0));
        //Get data ready to display
        Vector<String> vecColNames = new Vector<>(rs.getMetaData().getColumnCount());
        String[] columnNames = dataPort.getColumnNames(rs);
        vecColNames.addAll(Arrays.asList(columnNames));
        Vector<String[]> data = dataPort.getData(rs);
//        JTable resultTable = new JTable(data,vecColNames);
        JTable resultTable = new JTable(dataPort.toObjectArray(data),dataPort.getColumnNames(rs));

        resultTable.setPreferredScrollableViewportSize(new Dimension(900, 500));

        resultTable.setFillsViewportHeight(true);


        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(resultTable);
        //Add the scroll pane to this panel.
        renderView();
        add(scrollPane);
        //Create and set up the content pane.

    }

    private void renderView() {

    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {

    }
}
