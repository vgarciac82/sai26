package com.syc.dbms;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

public class DBMS {

    public static final int UNKNOW = -1;

    public static final int SQLSERVER = 0;

    public static final int ORACLE = 1;

    public static final int INFORMIX = 2;

    public static final int SYBASE = 3;

    public static final int SQLANYWHERE = 4;

    public static final int ANTS = 5;

    public static final int DB2 = 6;

    public static final int ISERIES = 7;

    private static final String[] dbmsList = { "SQL SERVER", "ORACLE", "INFORMIX", "SYBASE", "ANYWHERE", "ANTS", "DB2", "ISERIES" };

    public static int searchDBMSByName(String databaseProductName) {
        for (int i = 0; i <= dbmsList.length; i++) {
            if (databaseProductName.toUpperCase().indexOf(dbmsList[i]) >= 0)
                return i;
        }
        return -1;
    }

    private int currentDbms;

    private String dbmsName;

    public DBMS(Connection conn) {
        try {
            dbmsName = conn.getMetaData().getDatabaseProductName();
            currentDbms = searchDBMSByName(dbmsName);
        } catch (SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    public DBMS(String dbmsName) {
        currentDbms = searchDBMSByName(dbmsName);
    }

    public String inStr(String str, String nombreCampo) {
        String retValue = new String();
        switch(currentDbms) {
            case SQLSERVER:
                retValue = "CHARINDEX('" + str + "'," + nombreCampo + ")";
                break;
            case ORACLE:
                retValue = "INSTR(" + nombreCampo + ",'" + str + "')";
                break;
            case INFORMIX:
                break;
            case SYBASE:
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
                break;
            case DB2:
            case ISERIES:
                retValue = "LOCATE('" + str + "'," + nombreCampo + ")";
                break;
            default:
                throw new RuntimeException("DBMS \"" + dbmsName + "\" no implementado");
        }
        return retValue;
    }

    public String lower(String expresion) {
        String retValue = new String();
        switch(currentDbms) {
            case INFORMIX:
                break;
            case SYBASE:
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
                break;
            case DB2:
            case ORACLE:
            case ISERIES:
            case SQLSERVER:
                retValue = "LOWER(" + expresion + ")";
                break;
        }
        return retValue;
    }

    public String subString(String expresion, String indexStart, String indexLength) {
        String retValue = new String();
        switch(currentDbms) {
            case SQLSERVER:
                retValue = "SUBSTRING(" + expresion + "," + indexStart + "," + indexLength + ")";
                break;
            case INFORMIX:
                break;
            case SYBASE:
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
                break;
            case DB2:
            case ORACLE:
            case ISERIES:
                retValue = "SUBSTR(" + expresion + "," + indexStart + "," + indexLength + ")";
                break;
        }
        return retValue;
    }

    public String toChar(String value, String format) {
        String retValue = new String();
        switch(currentDbms) {
            case SQLSERVER:
                retValue = "CONVERT(char(10)," + value + ", 112)";
                break;
            case INFORMIX:
                break;
            case SYBASE:
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
            case ORACLE:
                retValue = "TO_CHAR(" + value + ", '" + format + "')";
                break;
            case DB2:
            case ISERIES:
                if ("YYYYMMDD".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),1,4) || SUBSTR(CHAR(" + value + "),6,2) || SUBSTR(CHAR(" + value + "),9,2)";
                else if ("YYYY".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),1,4)";
                else if ("YYYYMM".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),1,4) || SUBSTR(CHAR(" + value + "),6,2)";
                else if ("YYYYDD".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),1,4) || SUBSTR(CHAR(" + value + "),9,2)";
                else if ("MM".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),6,2)";
                else if ("MMDD".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),6,2) || SUBSTR(CHAR(" + value + "),9,2)";
                else if ("DD".equals(format.toUpperCase()))
                    retValue = "SUBSTR(CHAR(" + value + "),9,2)";
                break;
        }
        return retValue;
    }

    public String concatOperator() {
        String retValue = new String();
        switch(currentDbms) {
            case SQLSERVER:
                retValue = " + ";
                break;
            case INFORMIX:
                break;
            case SYBASE:
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
            case ORACLE:
            case DB2:
            case ISERIES:
                retValue = " || ";
                break;
        }
        return retValue;
    }

    public String SQLFunc_Now() {
        String define_funcion = "";
        switch(currentDbms) {
            case SQLSERVER:
                define_funcion = "GETDATE()";
                break;
            case ORACLE:
                define_funcion = "SYSDATE";
                break;
            case INFORMIX:
                break;
            case SYBASE:
                define_funcion = "GETDATE()";
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
                break;
            case DB2:
            case ISERIES:
                break;
        }
        return define_funcion;
    }

    public String convertStringToInt(String columnName) {
        String define_funcion = "";
        switch(currentDbms) {
            case SQLSERVER:
                define_funcion = "CONVERT(INT, " + columnName + ")";
                break;
            case ORACLE:
                define_funcion = "to_number(" + columnName + ")";
                break;
            case INFORMIX:
                break;
            case SYBASE:
                define_funcion = "to_number(" + columnName + ")";
                break;
            case SQLANYWHERE:
                break;
            case ANTS:
                break;
            case DB2:
            case ISERIES:
                break;
        }
        return define_funcion;
    }
}
