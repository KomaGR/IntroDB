import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class mainFrame extends JFrame {
        private connectionManager sql_manager;

    // Constructor

    public mainFrame(String s, connectionManager sql_manager) throws HeadlessException, SQLException {
        super(s);
        setVisible(true);
        this.sql_manager = sql_manager;
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

        tablePanel contentPane = new tablePanel(Main.rs, sql_manager);
        contentPane.registerParent(this);
        contentPane.setOpaque(true); //content panes must be opaque

        controlPanel cPanel = new controlPanel(sql_manager);

        Container cont = this.getContentPane();
        cont.add(cPanel,BorderLayout.PAGE_START);
        cont.add(contentPane.getScrollPane(),BorderLayout.CENTER);

        this.addWindowListener(mwl);
        pack();

    }

    public void registerSQLManager(connectionManager sql_manager) {
        this.sql_manager = sql_manager;
    }


    private class MyWindowListener extends WindowAdapter implements ActionListener {
        @Override
        public void windowClosing(WindowEvent event) {
            System.out.println("Logged an X_CLOSE press.");
            System.out.println("Exiting initialized.");
            int qOpResult;
            if (!sql_manager.getqBuffer().isEmpty()) {
                System.out.println("QBuffer contains queries.");
                int anw = JOptionPane.showConfirmDialog(null,"Would you like to save text?","Exit",JOptionPane.YES_NO_OPTION);
                System.out.println(anw);
                if (anw == 0 ) {    //0 is YES Button
                    qOpResult = sql_manager.commitQueries();    //Commit changes
                }
            } else {
                System.out.println("QBuffer was found empty. Closing.");
                qOpResult = sql_manager.cancelQueries();
            }
            /*if (qOpResult != pushedQueries) {
                //TODO: Throw problems
            }*/
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
