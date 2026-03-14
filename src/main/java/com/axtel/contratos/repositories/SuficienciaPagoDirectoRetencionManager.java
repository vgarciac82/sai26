package com.axtel.contratos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.contratos.core.SuficienciaPagoDirectoRetencion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuficienciaPagoDirectoRetencionManager {

    private static final Logger log = LoggerFactory.getLogger(SuficienciaPagoDirectoRetencionManager.class);

    public static void insert(Connection conn, SuficienciaPagoDirectoRetencion bean) throws SQLException {
        log.info("Insertando Retención...");
        String sql = "INSERT INTO tSuficienciaPagoDirectoRetencion " + "(nFolioSuficienciaPagoDirecto, cIdTipoRetencion, mImporteRetencion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bean.getFolioSuficienciaPagoDirecto());
            ps.setInt(2, bean.getIdTipoRetencion());
            ps.setBigDecimal(3, bean.getImporteRetencion());
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas insertadas: " + rows);
        }
    }

    public static List<SuficienciaPagoDirectoRetencion> findByFolio(Connection conn, int folio) throws SQLException {
        log.info("Object: {}", "Buscando Retenciones para folio: " + folio);
        List<SuficienciaPagoDirectoRetencion> list = new ArrayList<>();
        String sql = "SELECT * FROM tSuficienciaPagoDirectoRetencion WHERE nFolioSuficienciaPagoDirecto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, folio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SuficienciaPagoDirectoRetencion bean = new SuficienciaPagoDirectoRetencion();
                    bean.setFolioSuficienciaPagoDirecto(rs.getInt("nFolioSuficienciaPagoDirecto"));
                    bean.setIdTipoRetencion(rs.getInt("cIdTipoRetencion"));
                    bean.setImporteRetencion(rs.getBigDecimal("mImporteRetencion"));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    public static void update(Connection conn, SuficienciaPagoDirectoRetencion bean) throws SQLException {
        log.info("Object: {}", "Actualizando Retención para folio: " + bean.getFolioSuficienciaPagoDirecto());
        String sql = "UPDATE tSuficienciaPagoDirectoRetencion SET mImporteRetencion = ? " + "WHERE nFolioSuficienciaPagoDirecto = ? AND cIdTipoRetencion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, bean.getImporteRetencion());
            ps.setInt(2, bean.getFolioSuficienciaPagoDirecto());
            ps.setInt(3, bean.getIdTipoRetencion());
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas actualizadas: " + rows);
        }
    }

    public static void deleteByFolio(Connection conn, int folio, int id) throws SQLException {
        log.info("Object: {}", "Eliminando Retenciones para folio: " + folio);
        String sql = "DELETE FROM tSuficienciaPagoDirectoRetencion WHERE nFolioSuficienciaPagoDirecto = ? AND cIdTipoRetencion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, folio);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            log.info("Object: {}", "Filas eliminadas: " + rows);
        }
    }
}
