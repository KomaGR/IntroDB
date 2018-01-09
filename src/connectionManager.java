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
    private boolean autoCommit = false;

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
        Object mayBeQuery = null;
        int count = 0;
        while (!qBuffer.isEmpty()) {
            try {
                mayBeQuery = qBuffer.pop();
                if (mayBeQuery.getClass().equals(String.class)) {
                    //If it's a string add it to batch
                    query = mayBeQuery.toString();
                    assert statement != null;
                    statement.addBatch(query);
                } else {
                    //If it's a prepared statement execute it;
                    PreparedStatement stmt = (PreparedStatement) mayBeQuery;
                    try{
                        stmt.executeQuery();
                    } catch (SQLException e1) {
                        try {
                            stmt.executeUpdate();
                        } catch (SQLException e2) {
                            System.out.println(e2.getMessage());
                        } finally {
                            System.out.println("Update Query processed.");
                        }
                    }
                }
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
        return new String[]{"Store", //Our Stores
                            "Employee",
                            "Customer",
                            "Vehicle",
                            "Statute"};
    }

    public ResultSet getSelect(String option) throws SQLException{
        String q = null;
        switch (option) {
            case "Store":
                q = "SELECT * FROM Store";
                PreparedStatement stmt0 = connection.prepareStatement(q);
                return stmt0.executeQuery();
            case "Employee":
                q = "SELECT * FROM Employee";
                PreparedStatement stmt1 = connection.prepareStatement(q);
                return stmt1.executeQuery();
            case "Customer":
                q = "SELECT * FROM Customer";
                PreparedStatement stmt2 = connection.prepareStatement(q);
                return stmt2.executeQuery();
            case "Vehicle":
                q = "SELECT V.License_Plate, V.Model, V.Type, V.Make, V.Year, V.Kilometers, V.Cylinder_Capacity, V.Horse_Power, V.Damages, V.Malfunctions, V.Next_Service, V.Insurance_Exp_Date, V.Last_Service, S.City\n" +
                        "FROM Vehicle V\n" +
                        "INNER JOIN Store S ON V.Store_id = S.Store_id;";
                PreparedStatement stmt3 = connection.prepareStatement(q);
                return stmt3.executeQuery();
            case "Statute":
                q = "SELECT * FROM Statute";
                PreparedStatement stmt4 = connection.prepareStatement(q);
                return stmt4.executeQuery();

        }
        return null;
    }

    public boolean editable(String table) {
        switch (table) {
            case "Store":
                return true;
            case "Employee":
                return true;
            case "Customer":
                return false;
            case "Vehicle":
                return false;
        }
        return false;
    }

    public void updateTable(String table, String[] values) throws SQLException {
        String q = null;
        int response;
        switch (table) {
            case "Store":
                q = "UPDATE Store SET Street = ?, Street_Number = ?, Postal_Code = ?, City = ? WHERE Store_id = ?";
                PreparedStatement stmt0 = connection.prepareStatement(q);
                stmt0.setString(1,values[1]);
                stmt0.setString(2,values[2]);
                stmt0.setString(3,values[3]);
                stmt0.setString(4,values[4]);
                stmt0.setString(5,values[0]);
//                System.out.println(stmt0.toString());
                if (autoCommit) stmt0.executeUpdate();
                else qBuffer.add(stmt0);
//                response = stmt0.executeUpdate();   //TODO: Enqueue instead of execute
//                System.out.println(response);
                return;
            case "Employee":
                q = "UPDATE Employee SET Social_Security_Number = ?, Driver_License = ?, First_Name = ?, Last_Name = ?, Street = ?, " +
                        "Street_Number = ?, Postal_Code = ?, City = ? WHERE IRS_NUMBER = ?";
                PreparedStatement stmt1 = connection.prepareStatement(q);
                stmt1.setString(1,values[1]);
                stmt1.setString(2,values[2]);
                stmt1.setString(3,values[3]);
                stmt1.setString(4,values[4]);
                stmt1.setString(5,values[5]);
                stmt1.setString(6,values[6]);
                stmt1.setString(7,values[7]);
                stmt1.setString(8,values[8]);
                stmt1.setString(9,values[0]);
                if (autoCommit) stmt1.executeUpdate();
                else qBuffer.add(stmt1);
//                response = stmt1.executeUpdate();   //TODO: Enqueue instead of execute
//                System.out.println(response);
                return;
            case "Vehicle":
                q = "UPDATE Vehicle SET Model = ?, Type = ?, Year = ?, Kilometers = ?, Cylinder_Capacity = ?, Horse_Power = ?, " +
                        "Damages = ?, Malfunctions = ?, Next_Service = ?, Insurance_Exp_Date = ?, Last_Service = ?, Store_id = ?, Make = ? WHERE License_Plate = ?";
                PreparedStatement stmt2 = connection.prepareStatement(q);
                stmt2.setString(1,values[1]);
                stmt2.setString(2,values[2]);
                stmt2.setString(3,values[3]);
                stmt2.setString(4,values[4]);
                stmt2.setString(5,values[5]);
                stmt2.setString(6,values[6]);
                stmt2.setString(7,values[7]);
                stmt2.setString(8,values[8]);
                stmt2.setString(9,values[9]);
                stmt2.setString(10,values[10]);
                stmt2.setString(11,values[11]);
                stmt2.setString(12,values[12]);
                stmt2.setString(13,values[13]);
                stmt2.setString(14,values[0]);
                if (autoCommit) stmt2.executeUpdate();
                else qBuffer.add(stmt2);
//                response = stmt2.executeUpdate();   //TODO: Enqueue instead of execute
//                System.out.println(response);
                return;

        }

    }


    public int getPKCol(String tableName) {
        switch (tableName) {
            case "Store":
                return 0;
            case "Employee":
                return 0;
            case "Customer":
                return 0;
            case "Vehicle":
                return 0;
            case "Reserves":
                return 1;

        }
        return -1;
    }

    public void deleteRow(String tableName, Object valueAt) throws SQLException {
        String q = null;
        switch (tableName) {
            case "Store":
                q = "DELETE FROM Store WHERE Store_id = ?";
                PreparedStatement stmt0 = connection.prepareStatement(q);
                stmt0.setString(1,valueAt.toString());
                if (autoCommit) stmt0.executeUpdate();
                else qBuffer.add(stmt0);
                return;
            case "Employee":
                q = "DELETE FROM Employee WHERE IRS_NUMBER = ?";
                PreparedStatement stmt1 = connection.prepareStatement(q);
                stmt1.setString(1,valueAt.toString());
                if (autoCommit) stmt1.executeUpdate();
                else qBuffer.add(stmt1);
                return;
            case "Customer":
                q = "DELETE FROM Customer WHERE Customer_id = ?";
                PreparedStatement stmt2 = connection.prepareStatement(q);
                stmt2.setString(1,valueAt.toString());
                if (autoCommit) stmt2.executeUpdate();
                else qBuffer.add(stmt2);
                return;
            case "Vehicle":
                q = "";
                PreparedStatement stmt3 = connection.prepareStatement(q);
                if (autoCommit) stmt3.executeUpdate();
                else qBuffer.add(stmt3);
        }
    }

    public void insertRow(String tableName, String[] values) throws SQLException {
        String q = null;
        switch (tableName) {
            case "Store":
                q = "INSERT INTO Store(Store_id,Street,Street_Number,Postal_Code,City) VALUES (NULL,?,?,?,?)";
                PreparedStatement stmt0 = connection.prepareStatement(q);
                stmt0.setString(1,values[1]);
                stmt0.setString(2,values[2]);
                stmt0.setString(3,values[3]);
                stmt0.setString(4,values[4]);
                if (autoCommit) stmt0.executeUpdate();
                else qBuffer.add(stmt0);
                return;
            case "Employee":
                q = "INSERT INTO Employee(IRS_NUMBER, Social_Security_Number, Driver_License, " +
                        "First_Name, Last_Name, Street, Street_Number, Postal_Code, City) VALUES (?,?,?,?,?,?,?,?,?)";

                PreparedStatement stmt1 = connection.prepareStatement(q);
                stmt1.setString(1,values[0]);
                stmt1.setString(2,values[1]);
                stmt1.setString(3,values[2]);
                stmt1.setString(4,values[3]);
                stmt1.setString(5,values[4]);
                stmt1.setString(6,values[5]);
                stmt1.setString(7,values[6]);
                stmt1.setString(8,values[7]);
                stmt1.setString(9,values[8]);
                if (autoCommit) stmt1.executeUpdate();
                else qBuffer.add(stmt1);
                return;
            case "Customer":
                q = "INSERT INTO Customer(Customer_id, IRS_Number, Social_Security_Number, Last_Name, First_Name, " +
                        "Driver_License, First_Registration, City, Postal_Code, Street, Street_Number) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement stmt2 = connection.prepareStatement(q);
                stmt2.setString(1,values[0]);
                stmt2.setString(2,values[1]);
                stmt2.setString(3,values[2]);
                stmt2.setString(4,values[3]);
                stmt2.setString(5,values[4]);
                stmt2.setString(6,values[5]);
                stmt2.setString(7,values[6]);
                stmt2.setString(8,values[7]);
                stmt2.setString(9,values[8]);
                stmt2.setString(10,values[9]);
                stmt2.setString(11,values[10]);
                if (autoCommit) stmt2.executeUpdate();
                else qBuffer.add(stmt2);
                return;
            case "Vehicle":
                q = "";
                PreparedStatement stmt3 = connection.prepareStatement(q);
                if (autoCommit) stmt3.executeUpdate();
                else qBuffer.add(stmt3);
        }
    }

    public boolean pkSuppliedByUser(String tableName) {
        switch (tableName) {
            case "Store":
                return false;
            case "Employee":
                return true;    //It's IRS
            case "Customer":
                return false;
            case "Vehicle":
                return true;    //It's License plate
            case "Reserves":
                return true;    //It's Start Date

        }
        return false;
    }

    public void setAutoCommit(boolean b) {
        this.autoCommit = b;

    }

    public boolean getAutoCommit() {
        return autoCommit;
    }
}


