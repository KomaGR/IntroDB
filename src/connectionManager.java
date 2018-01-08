import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Properties;



public class connectionManager {
    private String userName = "dbapp";
    private String password = "p!nkp@anther";
    private String dbms = "mysql";
    private String serverName = "snf-795627.vm.okeanos.grnet.gr";
    private String dbName = "rentexdb";
    private String portNumber = "3306";
    private queryBuffer qBuffer = new queryBuffer();
    private Connection connection = null;

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
            conn = DriverManager.getConnection("jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/" + this.dbName, connectionProps);
        } else if (this.dbms.equals("derby")) {
            conn = DriverManager.getConnection("jdbc:" + this.dbms + ":" + this.dbName +";create=true", connectionProps);
        }
        System.out.println("Connected to database");
        connection = conn;
        return conn;
    }

    public ResultSet readTable(String table) throws SQLException {
        if (table.contains(";") || table.contains("\"") || table.contains("\'")) {
            throw new SQLException("Invalid input.");
        }
        String query = "SELECT * FROM ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1,table);
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    public ResultSet getClients(String order_by, boolean asc) throws SQLException {
        if (!(order_by.equals("Last_Name") || order_by.equals("Start_date") || order_by.equals("Finish_Date") ||
                order_by.equals("License_Plate") || order_by.equals("Payment_Amount") || order_by.equals("First_Name"))) {
            throw new SQLException("Invalid order_by");
        }
        String query = "SELECT C.Last_Name, C.First_Name, R.Start_date, R.Finish_Date, V.License_Plate, P.Payment_Amount\n" +
                "FROM Customer C\n" +
                "INNER JOIN Rents R\n" +
                "    ON C.Customer_id = R.Customer_id\n" +
                "INNER JOIN Vehicle V\n" +
                "  ON R.License_Plate = V.License_Plate\n" +
                "INNER JOIN rentexdb.Payment_Transaction P\n" +
                "    ON R.License_Plate = P.Licence_Plate\n" +
                "ORDER BY " + order_by + " " + (asc?"ASC":"DESC") + ";";
        PreparedStatement pstmt = connection.prepareStatement(query);
        return pstmt.executeQuery();
    }

    public queryBuffer getqBuffer() {
        return qBuffer;
    }

    public int commitQueries() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        String query = null;
        int count = 0;
        while (!qBuffer.isEmpty()) {
            try {
                query = qBuffer.pop();
                assert statement != null;
                statement.addBatch(query);
            } catch (NullPointerException | SQLException e) {
                System.out.println(e.getMessage());
            }
            count++;
        }
        try {
            assert statement != null;
            statement.executeBatch();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    public int cancelQueries() {
        int count = 0;
        while (!qBuffer.isEmpty()) {
            qBuffer.pop();
            count++;
        }
        return count;
    }

    public String[]  getOptions() {
        //TODO: Pass correct view Options
        return new String[]{"Stores", //Our Stores
                            "Employees",
                            "Customers",
                            "Vehicles"};
    }

    public ResultSet getSelect(String option) throws SQLException{
        String q = null;
        switch (option) {
            case "Stores":
                q = "SELECT * FROM Store";
                PreparedStatement stmt0 = connection.prepareStatement(q);
                return stmt0.executeQuery();
            case "Employees":
                q = "SELECT * FROM Employee";
                PreparedStatement stmt1 = connection.prepareStatement(q);
                return stmt1.executeQuery();
            case "Customers":
                q = "SELECT * FROM Customer";
                PreparedStatement stmt2 = connection.prepareStatement(q);
                return stmt2.executeQuery();
            case "Vehicles":
                q = "SELECT * FROM Vehicle";
                PreparedStatement stmt3 = connection.prepareStatement(q);
                return stmt3.executeQuery();
        }
        return null;
    }

    public void updateTable(String table, String[] values) throws SQLException {
        String q = null;
        switch (table) {
            case "Store":
                q = "UPDATE Store SET Street = ?, Street_Number = ?, Postal_Code = ?, City = ? WHERE Store_id = ?";
                PreparedStatement stmt0 = connection.prepareStatement(q);
                stmt0.setString(1,values[1]);
                stmt0.setString(2,values[2]);
                stmt0.setString(3,values[3]);
                stmt0.setString(4,values[4]);
                stmt0.setString(5,values[0]);
                int response = stmt0.executeUpdate();   //TODO: Enqueue instead of execute

                System.out.println(response);
        }
    }


}

