package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.Moneda;
import java.util.Base64;

public class MonedaManager {

    public static Moneda obtenerTipoMoneda(Connection conn, String codigoMoneda) throws SQLException {
        String querySelect = "SELECT * FROM c_Moneda WHERE Moneda = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, codigoMoneda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Moneda moneda = new Moneda();
                    moneda.setMoneda(rs.getString("Moneda"));
                    moneda.setDescripcion(rs.getString("Descripcion"));
                    return moneda;
                } else {
                    throw new SQLException("No Moneda found with code: " + codigoMoneda);
                }
            }
        }
    }
}
