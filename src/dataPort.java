
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class dataPort {
    public String[] getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        String[] result = new String[metaData.getColumnCount()];
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            result[i] = metaData.getColumnLabel(i+1);
        }
        return result;
    }
    public Vector<String[]> getData(ResultSet rs) throws SQLException {
        String[] columnNames = this.getColumnNames(rs);
        int colNo = rs.getMetaData().getColumnCount();
        Vector<String[]> data = new Vector<>();
        while (rs.next()) {
            String[] row = new String[colNo];
            for (int i = 0; i < colNo; i++) {
                Object obj = rs.getObject(i+1);
                row[i] = (obj == null) ? null : obj.toString();
            }
            data.add(row);
        }
        return data;
    }
}
