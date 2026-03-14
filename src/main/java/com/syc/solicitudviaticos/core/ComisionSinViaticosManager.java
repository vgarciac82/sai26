package com.syc.solicitudviaticos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComisionSinViaticosManager {

    private static final Logger log = LoggerFactory.getLogger(ComisionSinViaticosManager.class);

    public static String getMontoSolicitudStr(Connection conn, int idField) throws Exception {
        double montoSolicitud = 0.0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT mImporteNeto ");
        query.append("  FROM tComisionesSinComprobacionEnc ");
        query.append(" WHERE nFolioComision = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idField);
            rs = ps.executeQuery();
            if (rs.next()) {
                montoSolicitud = rs.getDouble(1);
                return Util.formatNumber(montoSolicitud);
            } else {
                throw new Exception("No se encontro monto para el folio de caja: " + idField);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void liberaVuelos(Connection conn, int nFolioComision) throws Exception {
        log.debug("Object: {}", "Se liberaran los vuelos en el Com. sin Viat. " + nFolioComision);
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE	tLayoutVuelosDet ");
        query.append("    SET	tLayoutVuelosDet.Status = 'A' ");
        query.append("   FROM	tComisionesSinComprobacionDet  ");
        query.append("  WHERE	tLayoutVuelosDet.cReferencia = tComisionesSinComprobacionDet.cBoleto ");
        query.append("    AND	tLayoutVuelosDet.mTotal = tComisionesSinComprobacionDet.mImporteBoleto ");
        query.append("    AND	tLayoutVuelosDet.cRuta = tComisionesSinComprobacionDet.cRuta ");
        query.append("    AND	tLayoutVuelosDet.rfc = tComisionesSinComprobacionDet.RFCVuelo ");
        query.append("    AND  tComisionesSinComprobacionDet.nFolioComision = ? ");
        log.trace("Object: {}", "Se ejecutara: \n" + query);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolioComision);
            int actualizados = ps.executeUpdate();
            liberaTransporteAereo(conn, nFolioComision);
            log.info("Object: {}", "Se cambio el estatus a " + actualizados + " vuelos de la comision sin viaticos " + nFolioComision);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static void liberaTransporteAereo(Connection conn, int nFolioComision) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" DELETE TA FROM tComisionesSinComprobacionDet cdet ");
        query.append("    INNER JOIN tTransporteAereo TA ");
        query.append("    ON TA.nFolioPago = cdet.nFolioComision AND TA.cReferencia= cdet.cBoleto  ");
        query.append("  where nFolioComision = ?");
        log.trace("Object: {}", "Se ejecutara: \n" + query);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolioComision);
            int actualizados = ps.executeUpdate();
            log.info("Object: {}", "Se eliminaron " + actualizados + " vuelos del transporte aereo " + nFolioComision);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void actualizaAplicacion(Connection conn, String docAplicado, int idField) throws Exception {
        log.info("Object: {}", "Se cambiara el estatus de la solicitud: " + idField + " a: " + docAplicado);
        String query = "UPDATE tComisionesSinComprobacionEnc SET cDocumentohAplicado = ? WHERE nFolioComision = ?";
        PreparedStatement ps = null;
        try {
            log.trace("Object: {}", "Se ejecutara: \n[" + query + "]\n[" + docAplicado + "," + idField + "]");
            ps = conn.prepareStatement(query);
            ps.setString(1, docAplicado);
            ps.setInt(2, idField);
            int actualizados = ps.executeUpdate();
            log.trace("Object: {}", "Se actualizaron " + actualizados + " registros");
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void actualizaFechaAplicacion(Connection conn, int idField) throws Exception {
        log.info("Object: {}", "Se cambiara la fecha de la solicitud: " + idField);
        String query = "UPDATE tComisionesSinComprobacionEnc SET faplicacion = GETDATE() WHERE  nFolioComision = ? ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idField);
            int actualizados = ps.executeUpdate();
            log.trace("Object: {}", "Se actualizaron " + actualizados + " registros");
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
