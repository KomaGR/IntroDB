import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class controlPanel extends JPanel implements ActionListener{
    connectionManager sql_manager = null;
    public controlPanel(connectionManager sql_manager) {
        super(new GridLayout(2,3));
        this.sql_manager = sql_manager;
        JComboBox<String> viewOption = new JComboBox<>(sql_manager.getOptions());
        viewOption.addActionListener(this);
        add(viewOption);


    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
