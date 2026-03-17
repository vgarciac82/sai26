package com.syc.egresos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class DocumentacionComprobatoriaManager {

    private static Logger log = LoggerFactory.getLogger(DocumentacionComprobatoriaManager.class);

    public static void regeneraDocComprobatoria(Connection conn, String tipoPago, int folio) throws Exception {
        PreparedStatement ps = null;
        String contrarecibo = DocumentacionComprobatoriaManager.buscaContrarecibo(conn, tipoPago, folio);
        String query = "INSERT INTO tDocumentacionComprobatoriaDet ";
        query += "SELECT aEjercicioFiscal, cCentroContable, caNoContrarrecibo, nConsecutivo, CRAMO, DCD_FACTURA, fRecepcion, fAplicacion, DCD_CBEN ";
        query += " , DCD_TBEN, DCD_TIPO_OPE, DCD_TIVA, DCD_VALOR, DCD_IMP_BRUTO, DCD_IVADES, DCD_ISR, dcd_iva, dcd_mil5, dcd_mil2, DCD_OTRAS_RET ";
        query += " ,DCD_PENALIZACION, DCD_CONTRIBUCION, DCD_CTOEXT, cConcepto, DCD_TODAY";
        query += " from v_pagosDocComprobatoria ";
        query += " WHERE caNoContrarrecibo = ? ";
        try {
            int borrados = DocumentacionComprobatoriaManager.borrarDocComp(conn, contrarecibo);
            log.debug("Object: " + String.valueOf("Se eliminaron: " + borrados + " registros de documentacion comprobatoria"));
            ps = conn.prepareStatement(query);
            ps.setString(1, contrarecibo);
            int insertados = ps.executeUpdate();
            if (insertados == 00)
                throw new Exception("No se inserto documentacion comprobatoria. Intente nuevamente.");
        } catch (Exception e) {
            log.error("Object: {}", e.getMessage());
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static String buscaContrarecibo(Connection conn, String tipoPago, int folio) throws Exception {
        String query = "SELECT canocontrarrecibo FROM t" + tipoPago + "Encabezado WITH(nolock) WHERE nFolio" + tipoPago + "= ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else
                throw new Exception("No se encontro contrarecibo para el tipo de pago: " + tipoPago + " con folio: " + folio);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarDocComp(Connection conn, String contrarecibo) throws Exception {
        PreparedStatement ps = null;
        String query = "DELETE tDocumentacionComprobatoriaDet WHERE caNoContrarrecibo = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, contrarecibo);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
