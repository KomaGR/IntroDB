import java.sql.*;
import java.util.Properties;

public class Main {

    static Connection conn;
    private static connectionManager sql_manager;
    private static String conn_username = "dbapp";
    private static String password = "p!nkp@anther";
    private static String serverName = "snf-795627.vm.okeanos.grnet.gr"; //"localhost";
    private static String dbname = "rentexdb";
    private static String portNumber = "3306";


    public static void main(String[] args) {
        try {

            sql_manager = new connectionManager(conn_username, password, "mysql", serverName, dbname, portNumber);
            conn = sql_manager.getConnection();
//            Properties connectionProps = new Properties();
//            connectionProps.put("user", "noah");
//            connectionProps.put("password", "upisdown");
//            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentexdb", connectionProps);

            Statement s = conn.createStatement();
            s.execute("SELECT * FROM Store");
            ResultSet rs = s.getResultSet();

            while (rs.next()) {
                System.out.println(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
