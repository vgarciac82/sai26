package com.axtel.cfdi.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.axtel.cfdi.Serie;
import java.util.Base64;

public class SerieManager {

    public static Serie obtenerSerie(Connection conn, int idSerie) throws SQLException {
        String querySelect = "SELECT id, serie, exclusiva_nomina, exclusiva_pagos FROM serie_cfdi WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setInt(1, idSerie);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanceSerie(rs);
                } else {
                    throw new SQLException("No Serie found with ID: " + idSerie);
                }
            }
        }
    }

    public static List<Serie> obtenerSeries(Connection conn) throws SQLException {
        String querySelectAll = "SELECT id, serie, exclusiva_nomina, exclusiva_pagos FROM serie_cfdi";
        List<Serie> series = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(querySelectAll);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                series.add(instanceSerie(rs));
            }
        }
        return series;
    }

    public static Serie insertarSerie(Connection conn, Serie serie) throws SQLException {
        String queryInsert = "INSERT INTO serie_cfdi (serie, exclusiva_nomina, exclusiva_pagos) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, serie.getSerie());
            ps.setBoolean(2, serie.isExclusivaNomina());
            ps.setBoolean(3, serie.isExclusivaPagos());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Serie failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    serie.setIdSerie(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating Serie failed, no ID obtained.");
                }
            }
        }
        return serie;
    }

    public static boolean actualizarSerie(Connection conn, Serie serie) throws SQLException {
        String queryUpdate = "UPDATE serie_cfdi SET serie = ?, exclusiva_nomina = ?, exclusiva_pagos = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
            ps.setString(1, serie.getSerie());
            ps.setBoolean(2, serie.isExclusivaNomina());
            ps.setBoolean(3, serie.isExclusivaPagos());
            ps.setInt(4, serie.getIdSerie());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean eliminarSerie(Connection conn, int idSerie) throws SQLException {
        String queryDelete = "DELETE FROM serie_cfdi WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
            ps.setInt(1, idSerie);
            return ps.executeUpdate() > 0;
        }
    }

    private static Serie instanceSerie(ResultSet rs) throws SQLException {
        Serie serie = new Serie();
        serie.setIdSerie(rs.getInt("id"));
        serie.setSerie(rs.getString("serie"));
        serie.setExclusivaNomina(rs.getBoolean("exclusiva_nomina"));
        serie.setExclusivaPagos(rs.getBoolean("exclusiva_pagos"));
        return serie;
    }
}
