import javax.swing.*;
import java.awt.*;

public class mainFrame extends JFrame {

    public mainFrame() throws HeadlessException {
        HelloWorldFrame();
    }

    void HelloWorldFrame() {
        JLabel jlbHelloWorld = new JLabel("Hello World");
        add(jlbHelloWorld);
        this.setSize(300, 300);
        // pack();
        setVisible(true);
    }
}
