package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.ClaveProdServ;
import java.util.Base64;

public class ClaveProdServManager {

    public static ClaveProdServ obtenerClaveProdServ(Connection conn, String claveProdServ) throws SQLException {
        String querySelect = "SELECT * FROM c_ClaveProdServ WHERE ClaveProdServ = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, claveProdServ);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ClaveProdServ cps = new ClaveProdServ();
                    cps.setClaveProdServ(rs.getString("ClaveProdServ"));
                    cps.setDescripcion(rs.getString("Descripcion"));
                    return cps;
                } else {
                    throw new SQLException("No ClaveProdServ found with code: " + claveProdServ);
                }
            }
        }
    }

    public static void guardarClaveProdServ(Connection conn, ClaveProdServ claveProdServ) throws SQLException {
        String queryInsert = "INSERT INTO c_ClaveProdServ (ClaveProdServ, Descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(queryInsert)) {
            ps.setString(1, claveProdServ.getClaveProdServ());
            ps.setString(2, claveProdServ.getDescripcion());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ClaveProdServ failed, no rows affected.");
            }
        }
    }

    public static void actualizarClaveProdServ(Connection conn, ClaveProdServ claveProdServ) throws SQLException {
        String queryUpdate = "UPDATE c_ClaveProdServ SET Descripcion = ? WHERE ClaveProdServ = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
            ps.setString(1, claveProdServ.getDescripcion());
            ps.setString(2, claveProdServ.getClaveProdServ());
            ps.executeUpdate();
        }
    }

    public static void eliminarClaveProdServ(Connection conn, String claveProdServ) throws SQLException {
        String queryDelete = "DELETE FROM c_ClaveProdServ WHERE ClaveProdServ = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
            ps.setString(1, claveProdServ);
            ps.executeUpdate();
        }
    }
}
