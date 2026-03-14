package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.TasaOCuota;
import java.util.Base64;

public class TasaOCuotaManager {

    public TasaOCuota obtenerTasaOCuota(Connection conn, int tasaOCuotaID) throws SQLException {
        String querySelect = "SELECT * FROM c_TasaOCuota WHERE TasaOCuotaID = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setInt(1, tasaOCuotaID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TasaOCuota toc = new TasaOCuota();
                    toc.setTasaOCuotaID(rs.getInt("TasaOCuotaID"));
                    toc.setRangoOFijo(rs.getString("RangoOFijo"));
                    toc.setValorMinimo(rs.getBigDecimal("ValorMinimo"));
                    toc.setValorMaximo(rs.getBigDecimal("ValorMaximo"));
                    toc.setImpuesto(rs.getString("Impuesto"));
                    toc.setFactor(rs.getString("Factor"));
                    toc.setTraslado(rs.getBoolean("Traslado"));
                    toc.setRetencion(rs.getBoolean("Retencion"));
                    return toc;
                } else {
                    throw new SQLException("No TasaOCuota found with ID: " + tasaOCuotaID);
                }
            }
        }
    }
}
