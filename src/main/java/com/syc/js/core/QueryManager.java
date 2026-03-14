package com.syc.js.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;

public class QueryManager {

    public static Element makeXMLQuery(Connection conn, String sql) throws SQLException {
        Element xml = new Element("resultSet");
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        if (sql == null)
            throw new SQLException("Llamada invalida");
        if (!sql.toLowerCase().startsWith("select"))
            throw new SQLException("Llamada invalida");
        try {
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount() + 1;
            Element header = new Element("header");
            for (int i = 1; i < cols; i++) {
                Element head = new Element("column");
                head.setAttribute("id", String.valueOf(i));
                head.addContent(md.getColumnLabel(i).toLowerCase());
                header.addContent(head);
            }
            xml.addContent(header);
            int rowNum = 1;
            Element rows = new Element("rows");
            while (rs.next()) {
                Element row = new Element("row");
                row.setAttribute("id", String.valueOf(rowNum++));
                for (int i = 1; i < cols; i++) {
                    row.addContent(new Element(md.getColumnLabel(i).toLowerCase()).addContent(rs.getString(i)));
                }
                rows.addContent(row);
            }
            xml.addContent(rows);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return xml;
    }

    public static JSONObject makeJSONQuery(Connection conn, String sql) throws SQLException {
        JSONObject json = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        if (sql == null)
            throw new SQLException("Llamada invalida");
        if (!sql.toLowerCase().startsWith("select"))
            throw new SQLException("Llamada invalida");
        try {
            json = new JSONObject();
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount() + 1;
            JSONArray column = new JSONArray();
            for (int i = 1; i < cols; i++) column.put(md.getColumnLabel(i).toLowerCase());
            json.put("column", column);
            JSONArray row = new JSONArray();
            while (rs.next()) {
                JSONObject col = new JSONObject();
                for (int i = 1; i < cols; i++) col.put(md.getColumnLabel(i).toLowerCase(), (rs.getString(i) == null) ? "" : rs.getString(i));
                row.put(col);
            }
            json.put("row", row);
        } catch (JSONException exc) {
            throw new SQLException(exc.getMessage());
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return json;
    }
}
