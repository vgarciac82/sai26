package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.CodigoPostal;
import java.util.Base64;

public class CodigoPostalManager {

    public static CodigoPostal obtenerCodigoPostal(Connection conn, String codigoPostal) throws SQLException {
        String querySelect = "SELECT * FROM c_CodigoPostal WHERE CodigoPostal = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, codigoPostal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CodigoPostal cp = new CodigoPostal();
                    cp.setCodigoPostal(rs.getString("CodigoPostal"));
                    cp.setDescripcion(rs.getString("Descripcion"));
                    return cp;
                } else {
                    throw new SQLException("No CodigoPostal found with code: " + codigoPostal);
                }
            }
        }
    }
}
