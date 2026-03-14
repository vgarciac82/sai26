package com.axtel.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Base64;

public class DBUtils {

    public static void showResultSetInfo(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int totalColumnas = rsmd.getColumnCount();
        for (int i = 1; i <= totalColumnas; i++) {
            System.out.println("Columna[" + i + "]" + rsmd.getColumnName(i));
        }
    }
}
