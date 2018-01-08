
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class dataPort {
    public String[] getColumnNames(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            String[] result = new String[metaData.getColumnCount()];
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                result[i] = metaData.getColumnLabel(i+1);
            }
            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Vector<String[]> getData(ResultSet rs) {
        try {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Object[][] toObjectArray(Vector<String[]> vec) {
        Object[][] strArrArr = new Object[vec.size()][vec.firstElement().length];
        for (int i = 0; i < vec.size(); i++) {
            String[] elemLine = vec.elementAt(i);
            for (int j = 0; j < elemLine.length; j++) {
                try {
                    strArrArr[i][j] = Integer.parseInt(elemLine[j]);
                } catch (NumberFormatException e) {
                    strArrArr[i][j] = elemLine[j];
                }
            }
        }
        return strArrArr;
    }
}
