import java.sql.*;
import java.util.Properties;


public class connectionManager {
    private String userName;
    private String password;
    private String dbms;
    private String serverName;
    private String dbName;
    private String portNumber;

    public connectionManager(String userName, String password, String dbms, String serverName, String dbName, String portNumber) {
        this.userName = userName;
        this.password = password;
        this.dbms = dbms;
        this.serverName = serverName;
        this.dbName = dbName;
        this.portNumber = portNumber;
    }

    public connectionManager() {
    }

    public Connection getConnection() throws SQLException {

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        if (this.dbms.equals("mysql")) {
            conn = DriverManager.getConnection(
                    "jdbc:" + this.dbms + "://" +
                            this.serverName +
                            ":" + this.portNumber + "/" + this.dbName,
                    connectionProps);
        } else if (this.dbms.equals("derby")) {
            conn = DriverManager.getConnection(
                    "jdbc:" + this.dbms + ":" +
                            this.dbName +
                            ";create=true",
                    connectionProps);
        }
        System.out.println("Connected to database");
        return conn;
    }


}

