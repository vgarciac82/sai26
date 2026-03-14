package com.axtel.contratos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.contratos.core.SuficienciaPagoDirectoDetalle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SuficienciaPagoDirectoDetalleManager {

    private static final Logger log = LoggerFactory.getLogger(SuficienciaPagoDirectoDetalleManager.class);

    public static void insert(Connection conn, SuficienciaPagoDirectoDetalle bean) throws SQLException {
        log.info("Insertando Detalle...");
        String sql = "INSERT INTO tSuficienciaPagoDirectoDetalle " + "(nFolioSuficienciaPagoDirecto, nDocRenglon, cEP, mImporte, mImporteNegativo, cCentroContable, cUR) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bean.getFolioSuficienciaPagoDirecto());
            ps.setInt(2, bean.getDocRenglon());
            ps.setString(3, bean.getEp());
            ps.setBigDecimal(4, bean.getImporte());
            ps.setBigDecimal(5, bean.getImporteNegativo());
            ps.setInt(6, bean.getCentroContable());
            ps.setString(7, bean.getUr());
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas insertadas: " + rows);
        }
    }

    public static List<SuficienciaPagoDirectoDetalle> findByFolio(Connection conn, int folio) throws SQLException {
        log.info("Object: {}", "Buscando Detalles para folio: " + folio);
        List<SuficienciaPagoDirectoDetalle> list = new ArrayList<>();
        String sql = "SELECT * FROM tSuficienciaPagoDirectoDetalle WHERE nFolioSuficienciaPagoDirecto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, folio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SuficienciaPagoDirectoDetalle bean = new SuficienciaPagoDirectoDetalle();
                    bean.setFolioSuficienciaPagoDirecto(rs.getInt("nFolioSuficienciaPagoDirecto"));
                    bean.setDocRenglon(rs.getInt("nDocRenglon"));
                    bean.setEp(rs.getString("cEP"));
                    bean.setImporte(rs.getBigDecimal("mImporte"));
                    bean.setImporteNegativo(rs.getBigDecimal("mImporteNegativo"));
                    bean.setCentroContable(rs.getInt("cCentroContable"));
                    bean.setUr(rs.getString("cUR"));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    public static void update(Connection conn, SuficienciaPagoDirectoDetalle bean) throws SQLException {
        log.info("Object: {}", "Actualizando Detalle para folio: " + bean.getFolioSuficienciaPagoDirecto());
        String sql = "UPDATE tSuficienciaPagoDirectoDetalle SET cEP = ?, mImporte = ?, mImporteNegativo = ?, " + "cCentroContable = ?, cUR = ? WHERE nFolioSuficienciaPagoDirecto = ? AND nDocRenglon = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bean.getEp());
            ps.setBigDecimal(2, bean.getImporte());
            ps.setBigDecimal(3, bean.getImporteNegativo());
            ps.setInt(4, bean.getCentroContable());
            ps.setString(5, bean.getUr());
            ps.setInt(6, bean.getFolioSuficienciaPagoDirecto());
            ps.setInt(7, bean.getDocRenglon());
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas actualizadas: " + rows);
        }
    }

    public static void deleteByFolio(Connection conn, int folio) throws SQLException {
        log.info("Object: {}", "Eliminando Detalles para folio: " + folio);
        String sql = "DELETE FROM tSuficienciaPagoDirectoDetalle WHERE nFolioSuficienciaPagoDirecto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, folio);
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas eliminadas: " + rows);
        }
    }
}
