import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.activation.ActivationInstantiator;
import java.sql.SQLException;

public class mainFrame extends JFrame {


    // Constructor

    public mainFrame(String s) throws HeadlessException {
        super(s);
        setVisible(true);

        // create menu bar
        MenuBar menuBar = new MenuBar();
        // Create menu
        Menu mbitem1 = new Menu("File");
        // Create menu items
        MenuItem item1 = new MenuItem("Save");
        // Add items to menus
        mbitem1.add(item1);
        // Add menus to menu bar
        menuBar.add(mbitem1);

        Menu help = new Menu("Help");
        MenuItem usage = new MenuItem("Usage");
        MenuItem about = new MenuItem("About");
        help.add(usage);
        help.add(about);
        menuBar.add(help);

        // Apply menu bar to top level container
        this.setMenuBar(menuBar);

        //Action Listeners
        MyWindowListener mwl = new MyWindowListener();
        item1.addActionListener(mwl);
        usage.addActionListener(mwl);
        about.addActionListener(mwl);
        try {
            tablePanel contentPane = new tablePanel(Main.rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        setContentPane(contentPane);
        //Display the window.
        pack();

        this.setDefaultCloseOperation(JEXIT_ON_CLOSE);


    }

    public void actionPerformed(ActionEvent e) {
        
    }

    private class MyWindowListener extends WindowAdapter implements ActionListener {
        public void windowClosing(WindowEvent event) {
            System.out.println("Exit window app");
            int anw = JOptionPane.showConfirmDialog(null,"Would you like to save text?","Exit",JOptionPane.YES_NO_OPTION);
            /*HERE WE MUST SAVE CHANGES*/
            System.exit(0);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println(actionEvent.getActionCommand());
            if (actionEvent.getActionCommand().equals("Save")) {
                //TODO: Save instance
            } else if (actionEvent.getActionCommand().equals("About")){
                //TODO: Open <<About>>
            } else if (actionEvent.getActionCommand().equals("Usage")){
                //TODO: Open <<Usage>>
            }
        }
    }
}
