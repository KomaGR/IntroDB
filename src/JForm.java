import javax.swing.*;
import javax.swing.SpringLayout;

public class JForm extends JPanel {
    private JTextField[] textFields;
    public JForm(String[] fields, String[] values) {
        super(new SpringLayout());
        textFields = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            JLabel label = new JLabel(fields[i], JLabel.TRAILING);
            this.add(label);
            textFields[i] = new JTextField(values[i]);
            label.setLabelFor(textFields[i]);
            this.add(textFields[i]);
        }
        SpringUtilities.makeGrid(this,fields.length,2,6,6,6,6);

        System.out.println("I cri evritiem");
    }

}
