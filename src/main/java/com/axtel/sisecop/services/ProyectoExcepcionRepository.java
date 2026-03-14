package com.axtel.sisecop.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.axtel.sisecop.entities.ProyectoExcepcion;
import com.axtel.sisecop.entities.TipoExcepcion;
import java.util.Base64;

public class ProyectoExcepcionRepository {

    public ProyectoExcepcion insertProyectoExcepcion(Connection conn, ProyectoExcepcion proyectoExcepcion) throws SQLException {
        String sql = "INSERT INTO sisecop_proyecto_excepcion (id_proyecto, id_hijo, id_tipo_excepcion, consecutivo, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, proyectoExcepcion.getIdProyecto());
            stmt.setInt(2, proyectoExcepcion.getIdHijo());
            stmt.setInt(3, proyectoExcepcion.getTipoExcepcion().getId());
            stmt.setInt(4, proyectoExcepcion.getConsecutivo());
            stmt.setString(5, proyectoExcepcion.getActivo());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating proyectoExcepcion failed, no rows affected.");
            }
            return findProyectoExcepcionById(conn, proyectoExcepcion.getIdProyecto(), proyectoExcepcion.getIdHijo());
        }
    }

    public ProyectoExcepcion updateProyectoExcepcion(Connection conn, ProyectoExcepcion proyectoExcepcion) throws SQLException {
        String sql = "UPDATE sisecop_proyecto_excepcion SET id_tipo_excepcion = ?, consecutivo = ?, activo = ? WHERE id_proyecto = ? AND id_hijo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proyectoExcepcion.getTipoExcepcion().getId());
            stmt.setInt(2, proyectoExcepcion.getConsecutivo());
            stmt.setString(3, proyectoExcepcion.getActivo());
            stmt.setInt(4, proyectoExcepcion.getIdProyecto());
            stmt.setInt(5, proyectoExcepcion.getIdHijo());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating proyectoExcepcion failed, no rows affected.");
            }
            return findProyectoExcepcionById(conn, proyectoExcepcion.getIdProyecto(), proyectoExcepcion.getIdHijo());
        }
    }

    public ProyectoExcepcion findProyectoExcepcionById(Connection conn, int idProyecto, int idHijo) throws SQLException {
        String sql = "SELECT * FROM sisecop_proyecto_excepcion WHERE id_proyecto = ? AND id_hijo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProyecto);
            stmt.setInt(2, idHijo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToProyectoExcepcion(rs);
                }
            }
        }
        return null;
    }

    public ProyectoExcepcion findProyectoExcepcionByChild(Connection conn, int idHijo) throws SQLException {
        String sql = "SELECT * FROM sisecop_proyecto_excepcion WHERE    id_hijo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHijo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToProyectoExcepcion(rs);
                }
            }
        }
        return null;
    }

    public List<ProyectoExcepcion> findAllProyectoExcepcion(Connection conn) throws SQLException {
        List<ProyectoExcepcion> list = new ArrayList<>();
        String sql = "SELECT * FROM sisecop_proyecto_excepcion";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapToProyectoExcepcion(rs));
            }
        }
        return list;
    }

    public void deleteProyectoExcepcion(Connection conn, int idProyecto, int idHijo) throws SQLException {
        String sql = "DELETE FROM sisecop_proyecto_excepcion WHERE id_proyecto = ? AND id_hijo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProyecto);
            stmt.setInt(2, idHijo);
            stmt.executeUpdate();
        }
    }

    public int getMaxConsecutivo(Connection conn, int idProyecto, int idTipoExcepcion) throws SQLException {
        String sql = "SELECT ISNULL(MAX(consecutivo), 0) AS max_consecutivo FROM sisecop_proyecto_excepcion WHERE id_proyecto = ? AND id_tipo_excepcion = ? AND activo = 'S'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProyecto);
            stmt.setInt(2, idTipoExcepcion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("max_consecutivo");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error querying max consecutivo: " + e.getMessage(), e);
        }
    }

    private ProyectoExcepcion mapToProyectoExcepcion(ResultSet rs) throws SQLException {
        ProyectoExcepcion proyectoExcepcion = new ProyectoExcepcion();
        proyectoExcepcion.setIdProyecto(rs.getInt("id_proyecto"));
        proyectoExcepcion.setIdHijo(rs.getInt("id_hijo"));
        proyectoExcepcion.setTipoExcepcion(TipoExcepcion.findById(rs.getInt("id_tipo_excepcion")));
        proyectoExcepcion.setConsecutivo(rs.getInt("consecutivo"));
        proyectoExcepcion.setActivo(rs.getString("activo"));
        proyectoExcepcion.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return proyectoExcepcion;
    }
}
