import java.sql.*;


public class Main {

    static Connection conn;
    static mainFrame frame;
    static dataPort dataPort = new dataPort();
    private static connectionManager sql_manager;
//    private static String conn_username = "dbapp";
//    private static String password = "p!nkp@anther";
//    private static String serverName = "snf-795627.vm.okeanos.grnet.gr"; //"localhost";
//    private static String dbname = "rentexdb";
//    private static String portNumber = "3306";
    public static ResultSet rs;


    public static void main(String[] args) {
        try {

//            sql_manager = new connectionManager(conn_username, password, "mysql", serverName, dbname, portNumber);
            sql_manager = new connectionManager();
            conn = sql_manager.getConnection();

//            rs = sql_manager.getClients("First_Name",true);

            rs = sql_manager.getSelect("Stores");
            //Create and set up the window.
            mainFrame mFrame = new mainFrame("CRUD9000",sql_manager);
            frame = mFrame;
            mFrame.registerSQLManager(sql_manager);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static connectionManager getSql_manager() {
        return sql_manager;
    }
}
