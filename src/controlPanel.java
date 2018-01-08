import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class controlPanel extends JPanel {
    connectionManager sql_manager = null;
    mainFrame mFrame = null;
    public controlPanel(connectionManager sql_manager, mainFrame mainFrame) {
        super(new GridLayout(2,3));
        this.sql_manager = sql_manager;
        this.mFrame = mainFrame;
        JComboBox<String> viewOption = new JComboBox<>(sql_manager.getOptions());
        viewOption.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    Object item = itemEvent.getItem();
                    String selection = item.toString();
                    System.out.println("Selected " + selection);
                    try {
                        ResultSet rs = sql_manager.getSelect(selection);
                        mFrame.changeContent(rs);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
        add(viewOption);
    }
}
