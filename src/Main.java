import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

public class Main {

    static Connection conn;
    static mainFrame frame;
    static dataPort dataPort;
    private static connectionManager sql_manager;
    private static String conn_username = "dbapp";
    private static String password = "p!nkp@anther";
    private static String serverName = "snf-795627.vm.okeanos.grnet.gr"; //"localhost";
    private static String dbname = "rentexdb";
    private static String portNumber = "3306";
    public static ResultSet rs;


    public static void main(String[] args) {
        try {

            sql_manager = new connectionManager(conn_username, password, "mysql", serverName, dbname, portNumber);
            conn = sql_manager.getConnection();

            // An example Query
            String query = "SELECT * FROM Employee";
//            query = "SELECT Store.Store_id, Phone_Number.Phone_Number, Store.Street, Store.Street_Number, Store.City FROM Store INNER JOIN Phone_Number ON Store.Store_id = Phone_Number.Store_id";
            Statement s = conn.createStatement();
            rs = s.executeQuery(query);


            //Create and set up the window.
            mainFrame mFrame = new mainFrame("CRUD9000");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
