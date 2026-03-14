package com.axtel.contratos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.contratos.core.SuficienciaPagoDirectoEP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SuficienciaPagoDirectoEPManager {

    private static final Logger log = LoggerFactory.getLogger(SuficienciaPagoDirectoEPManager.class);

    public static void insert(Connection conn, SuficienciaPagoDirectoEP bean) throws SQLException {
        log.info("Insertando SuficienciaPagoDirectoEP...");
        String sql = "INSERT INTO tSuficienciaPagoDirectoEP (nFolioSuficienciaPagoDirecto, ep) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bean.getFolioSuficienciaPagoDirecto());
            ps.setString(2, bean.getEp());
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas insertadas: " + rows);
        }
    }

    public static List<SuficienciaPagoDirectoEP> findByFolio(Connection conn, int folio) throws SQLException {
        log.info("Object: {}", "Buscando SuficienciaPagoDirectoEP por folio: " + folio);
        List<SuficienciaPagoDirectoEP> list = new ArrayList<>();
        String sql = "SELECT * FROM tSuficienciaPagoDirectoEP WHERE nFolioSuficienciaPagoDirecto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, folio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SuficienciaPagoDirectoEP bean = new SuficienciaPagoDirectoEP();
                    bean.setFolioSuficienciaPagoDirecto(rs.getInt("nFolioSuficienciaPagoDirecto"));
                    bean.setEp(rs.getString("ep"));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    public static void update(Connection conn, SuficienciaPagoDirectoEP bean) throws SQLException {
        log.info("Actualizando SuficienciaPagoDirectoEP...");
        String sql = "UPDATE tSuficienciaPagoDirectoEP SET ep = ? WHERE nFolioSuficienciaPagoDirecto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bean.getEp());
            ps.setInt(2, bean.getFolioSuficienciaPagoDirecto());
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas actualizadas: " + rows);
        }
    }

    public static void deleteByFolio(Connection conn, int folio, String ep) throws SQLException {
        log.info("Object: {}", "Eliminando SuficienciaPagoDirectoEP por folio: " + folio);
        String sql = "DELETE FROM tSuficienciaPagoDirectoEP WHERE nFolioSuficienciaPagoDirecto = ? and ep = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, folio);
            ps.setString(2, ep);
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas eliminadas: " + rows);
        }
    }
}
