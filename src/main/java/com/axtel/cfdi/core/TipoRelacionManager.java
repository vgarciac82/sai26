package com.axtel.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.cfdi.TipoRelacion;
import java.util.Base64;

public class TipoRelacionManager {

    public static TipoRelacion obtener(Connection conn, String tipoRelacion) throws SQLException {
        String querySelect = "SELECT * FROM c_TipoRelacion WHERE TipoRelacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setString(1, tipoRelacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoRelacion relacion = new TipoRelacion();
                    relacion.setTipoRelacion(rs.getString("TipoRelacion"));
                    relacion.setDescripcion(rs.getString("Descripcion"));
                    return relacion;
                } else {
                    throw new SQLException("No TipoRelacion found with code: " + tipoRelacion);
                }
            }
        }
    }
}
