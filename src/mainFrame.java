import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;

public class mainFrame extends JFrame implements ActionListener {

    private JMenu jmenuFile, jmenuHelp;
    private JMenuItem jmenuitemExit, jmenuitemAbout;
    private JLabel jlbOutput;
    private JButton jbnButtons[];
    private JPanel jplMaster, jplBackSpace, jplControl;

    Font f12 = new Font("Times New Roman", 0, 12);
    Font f121 = new Font("Times New Roman", 1, 12);

    // Constructor
    public mainFrame() {

        jmenuFile = new JMenu("File");
        jmenuFile.setFont(f121);
        jmenuFile.setMnemonic(KeyEvent.VK_F);
        jmenuitemExit = new JMenuItem("Exit");
        jmenuitemExit.setFont(f12);
        jmenuitemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        jmenuFile.add(jmenuitemExit);
        jmenuHelp = new JMenu("Help");
        jmenuHelp.setFont(f121);
        jmenuHelp.setMnemonic(KeyEvent.VK_H);
        jmenuitemAbout = new JMenuItem("About CRUD9000");
        jmenuitemAbout.setFont(f12);
        jmenuHelp.add(jmenuitemAbout);
        JMenuBar mb = new JMenuBar();
        mb.add(jmenuFile);
        mb.add(jmenuHelp);
        setJMenuBar(mb);
        setBackground(Color.gray);
        setVisible(true);

    }
    public void actionPerformed(ActionEvent e) {
        
    }
    void HelloWorldFrame() {
        JLabel jlbHelloWorld = new JLabel("Hello World");
        add(jlbHelloWorld);
        this.setSize(300, 300);
        // pack();
        setVisible(true);
    }
}
