import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class mainFrame extends JFrame {
        private connectionManager sql_manager;
        tablePanel data  = null;
        controlPanel cPanel = null;
        JScrollPane scrollPane = null;

    // Constructor

    public mainFrame(String s, connectionManager sql_manager) throws HeadlessException, SQLException {
        super(s);
        setVisible(true);
        this.sql_manager = sql_manager;
        // create menu bar
        JMenuBar menuBar = new JMenuBar();
        // Create menu
        JMenu mbitem1 = new JMenu("File");
        // Create menu items
        JMenuItem item1 = new JMenuItem("Save");

        // Add items to menus
        mbitem1.add(item1);

        // Add menus to menu bar
        menuBar.add(mbitem1);

        JMenu help = new JMenu("Help");
        JMenuItem options = new JMenuItem("Options");
        JMenuItem usage = new JMenuItem("Usage");
        JMenuItem about = new JMenuItem("About");

        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame optionsFrame = new JFrame("Options");
                JPanel optonsPanel = new JPanel(new SpringLayout());

                JCheckBox autoCommit = new JCheckBox("Auto-Commit Changes");
                autoCommit.setSelected(sql_manager.getAutoCommit());
                autoCommit.setToolTipText("Be careful when setting this feature!");
                autoCommit.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (autoCommit.isSelected()) {
                            sql_manager.setAutoCommit(true);
                        } else {
                            sql_manager.setAutoCommit(false);
                        }
                    }
                });

                optonsPanel.add(autoCommit);

                optionsFrame.add(optonsPanel);
                optionsFrame.setSize(new Dimension(300,200));
                optionsFrame.setLocationRelativeTo(cPanel);
                optionsFrame.requestFocus();
                optionsFrame.setAlwaysOnTop(true);
                optionsFrame.setVisible(true);
            }
        });

        help.add(options);
        help.add(usage);
        help.add(about);
        menuBar.add(help);

        // Apply menu bar to top level container
        this.setJMenuBar(menuBar);

        //Action Listeners
        MyWindowListener mwl = new MyWindowListener();
        item1.addActionListener(mwl);
        usage.addActionListener(mwl);
        about.addActionListener(mwl);

        tablePanel contentPane = new tablePanel(Main.rs, sql_manager);
        data = contentPane;
        contentPane.registerParent(this);
        contentPane.setOpaque(true); //content panes must be opaque

        cPanel = new controlPanel(sql_manager,this);

        Container cont = this.getContentPane();
        cont.add(cPanel,BorderLayout.PAGE_START);
        this.scrollPane = contentPane.getScrollPane();
        cont.add(contentPane.getScrollPane(),BorderLayout.CENTER);

        this.addWindowListener(mwl);
        pack();

    }

    public void changeContent(ResultSet rs) {
        try {
            tablePanel newContent = new tablePanel(rs, sql_manager);
            remove(scrollPane);
            System.out.println("Content change.");
            this.getContentPane().add(newContent.getScrollPane(),BorderLayout.CENTER);
            data = newContent;
            scrollPane = newContent.getScrollPane();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void registerSQLManager(connectionManager sql_manager) {
        this.sql_manager = sql_manager;
    }

    public void refreshContent() {
        if (cPanel != null) {
            try{
                System.out.println("------ CONTENT REFRESH START -------");
                String table = cPanel.currentTable;
                System.out.println("\tCurrent Table: " + table);
                ResultSet rs = sql_manager.getSelect(table);
                this.changeContent(rs);
                System.out.println("Refreshed content.");
            }catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
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
            System.exit(0);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println(actionEvent.getActionCommand());
            if (actionEvent.getActionCommand().equals("Save")) {
                //TODO: Save instance
                sql_manager.commitQueries();
                refreshContent();
            } else if (actionEvent.getActionCommand().equals("About")){
                //TODO: Open <<About>>
            } else if (actionEvent.getActionCommand().equals("Usage")){
                //TODO: Open <<Usage>>
            }
        }
    }
}
