package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.cfdi.CFDIDetalle;
import java.util.Base64;

public class CFDIDetalleManager {

    public static CFDIDetalle guardarCFDIDetalle(Connection conn, CFDIDetalle detalle) throws SQLException {
        String queryInsert = "INSERT INTO CFDI_Detalle (CFDI_ID, ClaveProdServ, NoIdentificacion, Cantidad, ClaveUnidad, Unidad, Descripcion, ValorUnitario, Importe, Descuento, ObjetoImp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(queryInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detalle.getCfdiId());
            ps.setString(2, detalle.getClaveProdServ().getClaveProdServ());
            ps.setString(3, detalle.getNoIdentificacion());
            ps.setBigDecimal(4, detalle.getCantidad());
            ps.setString(5, detalle.getClaveUnidad().getClaveUnidad());
            ps.setString(6, detalle.getUnidad());
            ps.setString(7, detalle.getDescripcion());
            ps.setBigDecimal(8, detalle.getValorUnitario());
            ps.setBigDecimal(9, detalle.getImporte());
            ps.setBigDecimal(10, detalle.getDescuento());
            ps.setString(11, detalle.getObjetoImp().getObjetoImp());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating CFDI detalle failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalle = CFDIDetalleManager.obtenerCFDIDetalle(conn, generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating CFDI detalle failed, no ID obtained.");
                }
            }
            return detalle;
        }
    }

    public static CFDIDetalle obtenerCFDIDetalle(Connection conn, int cfdiDetalleId) throws SQLException {
        String querySelect = "SELECT * FROM CFDI_Detalle WHERE CFDI_Detalle_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setInt(1, cfdiDetalleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CFDIDetalle detalle = new CFDIDetalle();
                    detalle.setCfdiDetalleId(rs.getInt("CFDI_Detalle_ID"));
                    detalle.setCfdiId(rs.getInt("CFDI_ID"));
                    detalle.setClaveProdServ(ClaveProdServManager.obtenerClaveProdServ(conn, rs.getString("ClaveProdServ")));
                    detalle.setNoIdentificacion(rs.getString("NoIdentificacion"));
                    detalle.setCantidad(rs.getBigDecimal("Cantidad"));
                    detalle.setClaveUnidad(ClaveUnidadManager.obtenerClaveUnidad(conn, rs.getString("ClaveUnidad")));
                    detalle.setUnidad(rs.getString("Unidad"));
                    detalle.setDescripcion(rs.getString("Descripcion"));
                    detalle.setValorUnitario(rs.getBigDecimal("ValorUnitario"));
                    detalle.setImporte(rs.getBigDecimal("Importe"));
                    detalle.setDescuento(rs.getBigDecimal("Descuento"));
                    detalle.setObjetoImp(ObjetoImpManager.obtenerObjetoImp(conn, rs.getString("ObjetoImp")));
                    return detalle;
                } else {
                    throw new SQLException("No CFDI detalle found with ID: " + cfdiDetalleId);
                }
            }
        }
    }

    public static void actualizarCFDIDetalle(Connection conn, CFDIDetalle detalle) throws SQLException {
        String queryUpdate = "UPDATE CFDI_Detalle SET ClaveProdServ = ?, NoIdentificacion = ?, Cantidad = ?, ClaveUnidad = ?, Unidad = ?, Descripcion = ?, ValorUnitario = ?, Importe = ?, Descuento = ?, ObjetoImp = ? WHERE CFDI_Detalle_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
            ps.setString(1, detalle.getClaveProdServ().getClaveProdServ());
            ps.setString(2, detalle.getNoIdentificacion());
            ps.setBigDecimal(3, detalle.getCantidad());
            ps.setString(4, detalle.getClaveUnidad().getClaveUnidad());
            ps.setString(5, detalle.getUnidad());
            ps.setString(6, detalle.getDescripcion());
            ps.setBigDecimal(7, detalle.getValorUnitario());
            ps.setBigDecimal(8, detalle.getImporte());
            ps.setBigDecimal(9, detalle.getDescuento());
            ps.setString(10, detalle.getObjetoImp().getObjetoImp());
            ps.setInt(11, detalle.getCfdiDetalleId());
            ps.executeUpdate();
        }
    }

    public static void eliminarCFDIDetalle(Connection conn, int cfdiDetalleId) throws SQLException {
        String queryDelete = "DELETE FROM CFDI_Detalle WHERE CFDI_Detalle_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
            ps.setInt(1, cfdiDetalleId);
            ps.executeUpdate();
        }
    }

    public static List<CFDIDetalle> obtenerTodosCFDIDetalles(Connection conn, int cfdiId) throws SQLException {
        String querySelectAll = "SELECT * FROM CFDI_Detalle WHERE CFDI_ID = ?";
        List<CFDIDetalle> detalles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(querySelectAll)) {
            ps.setInt(1, cfdiId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CFDIDetalle detalle = new CFDIDetalle();
                    detalle.setCfdiDetalleId(rs.getInt("CFDI_Detalle_ID"));
                    detalle.setCfdiId(rs.getInt("CFDI_ID"));
                    detalle.setClaveProdServ(ClaveProdServManager.obtenerClaveProdServ(conn, rs.getString("ClaveProdServ")));
                    detalle.setNoIdentificacion(rs.getString("NoIdentificacion"));
                    detalle.setCantidad(rs.getBigDecimal("Cantidad"));
                    detalle.setClaveUnidad(ClaveUnidadManager.obtenerClaveUnidad(conn, rs.getString("ClaveUnidad")));
                    detalle.setUnidad(rs.getString("Unidad"));
                    detalle.setDescripcion(rs.getString("Descripcion"));
                    detalle.setValorUnitario(rs.getBigDecimal("ValorUnitario"));
                    detalle.setImporte(rs.getBigDecimal("Importe"));
                    detalle.setDescuento(rs.getBigDecimal("Descuento"));
                    detalle.setObjetoImp(ObjetoImpManager.obtenerObjetoImp(conn, rs.getString("ObjetoImp")));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }
}
