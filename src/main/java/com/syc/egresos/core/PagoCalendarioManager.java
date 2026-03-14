package com.syc.egresos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PagoCalendarioManager {

    private static final Logger log = LoggerFactory.getLogger("InsertaCalendario");

    public static int insertaCalendario(Connection conn, CalendarioPago calendarioPago) throws Exception {
        int insertados = 0;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tPagoCalendario( ");
        queryInsert.append("cTipoPago, nFolioPago, EP, nMes, mImporteBruto, ");
        queryInsert.append("idTipoConcepto, idTipoMovimiento, mImporteRetencion ) ");
        queryInsert.append("VALUES(?,?,?,?,?,?,?,?)");
        PreparedStatement psInsert = null;
        try {
            log.debug("Object: {}", "Preparando inserción en tPagoCalendario..." + calendarioPago);
            log.debug("Object: {}", "Query generada: " + queryInsert.toString());
            psInsert = conn.prepareStatement(queryInsert.toString());
            log.debug("Asignando parámetros para CalendarioPago:");
            log.debug("Object: {}", "  -> TipoPago: " + calendarioPago.getTipoPago());
            log.debug("Object: {}", "  -> FolioPago: " + calendarioPago.getFolioPago());
            log.debug("Object: {}", "  -> EP: " + calendarioPago.getEp());
            log.debug("Object: {}", "  -> Mes: " + calendarioPago.getMes());
            log.debug("Object: {}", "  -> ImporteBrutoMes: " + calendarioPago.getImporteBrutoMes());
            log.debug("Object: {}", "  -> IdTipoConcepto: " + calendarioPago.getIdTipoConcepto());
            log.debug("Object: {}", "  -> IdTipoMovimiento: " + calendarioPago.getIdTipoMovimiento());
            log.debug("Object: {}", "  -> ImporteRetencion: " + calendarioPago.getImporteRetencion());
            psInsert.setString(1, calendarioPago.getTipoPago());
            psInsert.setInt(2, calendarioPago.getFolioPago());
            psInsert.setString(3, calendarioPago.getEp());
            psInsert.setInt(4, calendarioPago.getMes());
            psInsert.setBigDecimal(5, calendarioPago.getImporteBrutoMes());
            if (StringUtils.isBlank(calendarioPago.getIdTipoConcepto())) {
                log.debug("IdTipoConcepto vacío -> seteando NULL");
                psInsert.setNull(6, Types.VARCHAR);
            } else {
                psInsert.setString(6, calendarioPago.getIdTipoConcepto());
            }
            if (StringUtils.isBlank(calendarioPago.getIdTipoMovimiento())) {
                log.debug("IdTipoMovimiento vacío -> seteando NULL");
                psInsert.setNull(7, Types.VARCHAR);
            } else {
                psInsert.setString(7, calendarioPago.getIdTipoMovimiento());
            }
            psInsert.setBigDecimal(8, calendarioPago.getImporteRetencion());
            log.info("Ejecutando INSERT de calendario de pago...");
            insertados = psInsert.executeUpdate();
            log.info("Object: {}", "Filas insertadas correctamente: " + insertados);
            return insertados;
        } catch (Exception e) {
            log.error("Ocurrió un error al insertar en tPagoCalendario: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                CloseObject.closeObject(psInsert);
                log.debug("PreparedStatement cerrado correctamente.");
            } catch (Exception ex) {
                log.warn("Error al cerrar PreparedStatement: " + ex.getMessage(), ex);
            }
        }
    }
}
