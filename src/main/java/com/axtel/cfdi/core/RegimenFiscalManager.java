package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.RegimenFiscal;
import java.util.Base64;

public class RegimenFiscalManager {

    public static RegimenFiscal obtenerRegimenFiscal(Connection conn, String regimenFiscal) throws SQLException {
        String querySelect = "SELECT * FROM c_RegimenFiscal WHERE RegimenFiscal = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, regimenFiscal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RegimenFiscal rf = new RegimenFiscal();
                    rf.setRegimenFiscal(rs.getString("RegimenFiscal"));
                    rf.setDescripcion(rs.getString("Descripcion"));
                    return rf;
                } else {
                    throw new SQLException("No RegimenFiscal found with code: " + regimenFiscal);
                }
            }
        }
    }
}
