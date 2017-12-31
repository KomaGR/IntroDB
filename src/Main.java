import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Properties;

public class Main {

    static Connection conn;
    static mainFrame frame;
    private static connectionManager sql_manager;
    private static String conn_username = "dbapp";
    private static String password = "p!nkp@anther";
    private static String serverName = "snf-795627.vm.okeanos.grnet.gr"; //"localhost";
    private static String dbname = "rentexdb";
    private static String portNumber = "3306";


    public static void main(String[] args) {

        try {
            frame = new mainFrame();
        } catch (HeadlessException e) {
            System.out.println(e.getMessage());
        }
        try {

            sql_manager = new connectionManager(conn_username, password, "mysql", serverName, dbname, portNumber);
            conn = sql_manager.getConnection();

            // An example Query
            String query = "SELECT * FROM Store";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                int id = rs.getInt("Store_id");
                String firstName = rs.getString("Street");
                String lastName = rs.getString("Street_Number");
                String dateCreated = rs.getString("Postal_Code");
                String city = rs.getString("City");

                // print the results
                System.out.format("%s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, city);
                JLabel row = new JLabel(String.format("%s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, city));
                frame.add(row);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
