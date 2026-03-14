package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.ClaveUnidad;
import java.util.Base64;

public class ClaveUnidadManager {

    public static ClaveUnidad obtenerClaveUnidad(Connection conn, String claveUnidad) throws SQLException {
        String querySelect = "SELECT * FROM c_ClaveUnidad WHERE ClaveUnidad = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, claveUnidad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ClaveUnidad cu = new ClaveUnidad();
                    cu.setClaveUnidad(rs.getString("ClaveUnidad"));
                    cu.setDescripcion(rs.getString("Descripcion"));
                    return cu;
                } else {
                    throw new SQLException("No ClaveUnidad found with code: " + claveUnidad);
                }
            }
        }
    }

    public static void guardarClaveUnidad(Connection conn, ClaveUnidad claveUnidad) throws SQLException {
        String queryInsert = "INSERT INTO c_ClaveUnidad (ClaveUnidad, Descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(queryInsert)) {
            ps.setString(1, claveUnidad.getClaveUnidad());
            ps.setString(2, claveUnidad.getDescripcion());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ClaveUnidad failed, no rows affected.");
            }
        }
    }

    public static void actualizarClaveUnidad(Connection conn, ClaveUnidad claveUnidad) throws SQLException {
        String queryUpdate = "UPDATE c_ClaveUnidad SET Descripcion = ? WHERE ClaveUnidad = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
            ps.setString(1, claveUnidad.getDescripcion());
            ps.setString(2, claveUnidad.getClaveUnidad());
            ps.executeUpdate();
        }
    }

    public static void eliminarClaveUnidad(Connection conn, String claveUnidad) throws SQLException {
        String queryDelete = "DELETE FROM c_ClaveUnidad WHERE ClaveUnidad = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
            ps.setString(1, claveUnidad);
            ps.executeUpdate();
        }
    }
}
