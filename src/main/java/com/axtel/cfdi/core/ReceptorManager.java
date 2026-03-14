package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.cfdi.Receptor;
import java.util.Base64;

public class ReceptorManager {

    public static Receptor obtenerCliente(Connection conn, String rfcCliente) throws SQLException {
        String querySelect = "SELECT * FROM Receptor WHERE RFC = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, rfcCliente);
            return obtenerCliente(conn, ps);
        }
    }

    public static Receptor obtenerCliente(Connection conn, int idCliente) throws SQLException {
        String querySelect = "SELECT * FROM Receptor WHERE ReceptorID = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setInt(1, idCliente);
            return obtenerCliente(conn, ps);
        }
    }

    private static Receptor obtenerCliente(Connection conn, PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Receptor cliente = instanceReceptor(rs);
                return cliente;
            } else {
                throw new SQLException("No Cliente found ");
            }
        }
    }

    public static List<Receptor> buscarReceptores(Connection conn, String nombre, String rfc) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM Receptor WHERE 1=1");
        // Construir filtros dinámicamente
        if (nombre != null && !nombre.isEmpty()) {
            query.append(" AND Nombre LIKE ?");
        }
        if (rfc != null && !rfc.isEmpty()) {
            query.append(" AND RFC LIKE ?");
        }
        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int parameterIndex = 1;
            // Asignar valores a los parámetros según los filtros
            if (nombre != null && !nombre.isEmpty()) {
                ps.setString(parameterIndex++, "%" + nombre + "%");
            }
            if (rfc != null && !rfc.isEmpty()) {
                ps.setString(parameterIndex++, "%" + rfc + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Receptor> receptores = new ArrayList<>();
                while (rs.next()) {
                    Receptor receptor = instanceReceptor(rs);
                    receptores.add(receptor);
                }
                return receptores;
            }
        }
    }

    private static final Receptor instanceReceptor(ResultSet rs) throws SQLException {
        Receptor receptor = new Receptor();
        receptor.setReceptorID(rs.getInt("ReceptorID"));
        receptor.setRfc(rs.getString("RFC"));
        receptor.setNombre(rs.getString("Nombre"));
        receptor.setRegimenFiscal(rs.getString("RegimenFiscal"));
        receptor.setDomicilioFiscal(rs.getString("DomicilioFiscal"));
        receptor.setEmail(rs.getString("Email"));
        return receptor;
    }
}
