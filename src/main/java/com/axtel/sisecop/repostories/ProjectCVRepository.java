package com.axtel.sisecop.repostories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.sisecop.entities.ProyectoServicioTDR;
import java.util.Base64;

public class ProjectCVRepository {

    public ProyectoServicioTDR createServicioTermino(Connection conn, int servicioId, ProyectoServicioTDR projectTDR) throws SQLException {
        String sql = "INSERT INTO sisecop_serviciosterminos (servicioId, servicioterminoArchivo, servicioterminoRuta) VALUES (?, ?, ?)";
        int generatedId = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, servicioId);
            stmt.setString(2, projectTDR.getTdrArchivo());
            stmt.setString(3, projectTDR.getTdrRuta());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating servicioTermino failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                    return getServicioTerminoById(conn, generatedId);
                } else {
                    throw new SQLException("Creating servicioTermino failed, no ID obtained.");
                }
            }
        }
    }

    public void deleteServicioTermino(Connection conn, int servicioterminoId) throws SQLException {
        String sql = "DELETE FROM sisecop_serviciosterminos WHERE servicioterminoId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioterminoId);
            stmt.executeUpdate();
        }
    }

    public ProyectoServicioTDR getServicioTerminoById(Connection conn, int servicioterminoId) throws SQLException {
        String sql = "SELECT * FROM sisecop_serviciosterminos WHERE servicioterminoId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioterminoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToServicioTermino(rs);
                }
            }
        }
        return null;
    }

    public List<ProyectoServicioTDR> readByServicioId(Connection conn, int servicioId) throws SQLException {
        List<ProyectoServicioTDR> terminos = new ArrayList<>();
        String sql = "SELECT servicioterminoId, servicioId, servicioterminoArchivo, servicioterminoRuta " + "FROM sisecop_serviciosterminos WHERE servicioId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProyectoServicioTDR termino = mapToServicioTermino(rs);
                    terminos.add(termino);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error reading servicio terms by servicioId: " + e.getMessage(), e);
        }
        return terminos;
    }

    private ProyectoServicioTDR mapToServicioTermino(ResultSet rs) throws SQLException {
        ProyectoServicioTDR termino = new ProyectoServicioTDR();
        termino.setTdrId(rs.getInt("servicioterminoId"));
        termino.setTdrArchivo(rs.getString("servicioterminoArchivo"));
        termino.setTdrRuta(rs.getString("servicioterminoRuta"));
        return termino;
    }

    public void updateServicioTermino(Connection conn, int servicioterminoId, int servicioId, String archivo, String ruta) throws SQLException {
        String sql = "UPDATE sisecop_serviciosterminos SET servicioId = ?, servicioterminoArchivo = ?, servicioterminoRuta = ? WHERE servicioterminoId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioId);
            stmt.setString(2, archivo);
            stmt.setString(3, ruta);
            stmt.setInt(4, servicioterminoId);
            stmt.executeUpdate();
        }
    }
}
