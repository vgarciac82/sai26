package com.axtel.cfdi.core;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.FormaPago;

public class FormaPagoManager {

    public static FormaPago obtenerFormaPago(Connection conn, String formaPago) throws SQLException {
        String querySelect = "SELECT * FROM c_FormaPago WHERE FormaPago = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, formaPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FormaPago fp = new FormaPago();
                    fp.setFormaPago(rs.getString("FormaPago"));
                    fp.setDescripcion(rs.getString("Descripcion"));
                    return fp;
                } else {
                    throw new SQLException("No FormaPago found with ID: " + formaPago);
                }
            }
        }
    }
}
