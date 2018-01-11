import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class controlPanel extends JPanel {
    connectionManager sql_manager = null;
    mainFrame mFrame = null;
    String currentTable = "Store";
    private JTextField editability;
    private JComboBox<String> viewOption;

    public void setNoSelectedOption() {
        viewOption.setSelectedItem(null);
        editability.setText("Not Editable");
    }

    public String getSelectedOption() {
        if (viewOption.getSelectedItem() != null) return viewOption.getSelectedItem().toString();
        else return null;
    }

    public controlPanel(connectionManager sql_manager, mainFrame mainFrame) {
        super(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10,10,10,10);


        this.sql_manager = sql_manager;
        this.mFrame = mainFrame;
        // Editability text
        boolean edit = sql_manager.editable(currentTable);
        String canEdit = ( edit ? "Editable" : "Not Editable" );
        editability = new JTextField(canEdit,7);
        JButton commitButton = new JButton("COMMIT");
        commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Commit Button pressed.");
                if (!sql_manager.getqBuffer().isEmpty()) {
                    int anw = JOptionPane.showConfirmDialog(mFrame,"Are you sure you want to commit changes to " +
                            "database?","Confirm Commit",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                    System.out.println(anw);
                    if (anw == 0) {
                        sql_manager.commitQueries();
                        mainFrame.refreshContent();
                    }
                }
            }
        });
        JButton dropButton = new JButton("DROP");
        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Drop Button pressed.");
                if (!sql_manager.getqBuffer().isEmpty()) {
                    int anw = JOptionPane.showConfirmDialog(mFrame,"Are you sure you want to drop changes?",
                            "Drop Changes",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                    System.out.println(anw);
                    if (anw == 0) {
                        sql_manager.cancelQueries();
                    }
                }
            }
        });
        JButton undoButton = new JButton("UNDO");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Undo Button pressed.");
                if (!sql_manager.getqBuffer().isEmpty()) {
                    sql_manager.getqBuffer().undo();
                }
            }
        });
        JButton redoButton = new JButton("REDO");
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Redo Button pressed.");
                sql_manager.getqBuffer().redo();
            }
        });

        // Table Selector
        viewOption = new JComboBox<>(sql_manager.getOptions());
        viewOption.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    Object item = itemEvent.getItem();
                    String selection = item.toString();
                    currentTable = selection;
                    System.out.println("Selected " + selection);
                    try {
                        ResultSet rs = sql_manager.getSelect(selection);

                        String canEdit = (sql_manager.editable(currentTable) ? "Editable" : "Not Editable" );
                        System.out.println("It's " + canEdit);
                        editability.setText(canEdit);
                        currentTable = selection;
                        mFrame.changeContent(rs);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });

        //view option placement
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        add(viewOption,constraints);

        // Editability text placement
        constraints.insets = new Insets(0,10,10,10);
        editability.setEditable(false);
        editability.setBackground(Color.orange);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(editability,constraints);

        // commit button placement
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 6;
        constraints.gridy = 1;
        add(commitButton,constraints);

        //undo button placement
        constraints.gridx = 7;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(undoButton,constraints);

        //redo button placement
        constraints.gridx = 8;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(redoButton,constraints);

        //drop button placement
        constraints.gridx = 9;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(dropButton,constraints);

    }

    public void refresh(String newTable) {
        currentTable = newTable;
        editability.setText(sql_manager.editable(currentTable)? "Editable" : "Not Editable");
    }
}
