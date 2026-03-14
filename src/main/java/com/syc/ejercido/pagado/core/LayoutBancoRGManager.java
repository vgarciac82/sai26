package com.syc.ejercido.pagado.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.syc.contable.caja.core.CajaManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class LayoutBancoRGManager {

    private static final Logger log = LoggerFactory.getLogger(CajaManager.class);

    public LayoutBancoRGManager() {
        super();
    }

    public static ArrayList<String> ArmaLayoutBancoRG(Connection conn, String nFolios, String sTipo, boolean tipoBanorte, String sCuentaLayout) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmLayout = null;
        ResultSet rsLayout = null;
        StringBuilder sql = new StringBuilder();
        String ctaIntegradora = sCuentaLayout;
        String tablaEncabezado = "";
        String tablaDetalle = "";
        String campoRfc = "";
        String folioCondicion = "";
        String esBanorte = " IN ";
        try {
            if (sTipo.equals("RG")) {
                tablaEncabezado = "tRELACIONGASTOSEncabezado";
                campoRfc = "cIdRFC";
                folioCondicion = "nFolioRELACIONGASTOS";
            } else if (sTipo.equals("PO")) {
                tablaEncabezado = "tPAGOOBRAEncabezado";
                campoRfc = "RFC";
                folioCondicion = "nFolioPAGOOBRA";
            } else if (sTipo.equals("CAJA")) {
                tablaEncabezado = "tcajaencabezado";
                tablaDetalle = "tcajadetalle";
                campoRfc = "RFC";
                folioCondicion = "nFoliocaja";
            } else {
                tablaEncabezado = "tPAGODIVERSOEncabezado";
                campoRfc = "RFC";
                folioCondicion = "nFolioPAGODIVERSO";
            }
            if (!tipoBanorte)
                esBanorte = " NOT IN ";
            if (sTipo.equals("CAJA")) {
                sql.append("SELECT CASE WHEN SUBSTRING(subCuentaBancaria,1,3)='072' THEN '02' ELSE '04' END AS tipoBanco, ");
                sql.append("cIdBancario, ");
                sql.append("SUBSTRING('").append(ctaIntegradora).append("',8,10) AS CTABCNF, ");
                sql.append("CASE WHEN SUBSTRING(subCuentaBancaria,1,3)='072' THEN SUBSTRING(subCuentaBancaria,8,10) ELSE subCuentaBancaria END AS CTABEMPLEADO, ");
                sql.append("CONVERT(VARCHAR(32), mImporte) AS mImporteNeto, ");
                sql.append("RIGHT('000000' + CONVERT(VARCHAR(6), pago.").append(folioCondicion).append("),6) AS nFolio, ");
                sql.append("LEFT((SELECT dbo.fn_quitarCaracteresEspeciales('CONAFOR ' + ");
                sql.append("(SELECT TOP 1 dEvento FROM tEvento evento WITH (NOLOCK) WHERE evento.cEvento = detalle.cEvento))), 30), ");
                sql.append("'CNF010405EG1' AS concepto, ");
                sql.append("'', ");
                sql.append("'', ");
                sql.append("'x' ");
                sql.append("FROM ").append(tablaEncabezado).append(" pago WITH(NOLOCK) ");
                sql.append("JOIN ").append(tablaDetalle).append(" detalle WITH(NOLOCK) ON pago.").append(folioCondicion).append(" = detalle.").append(folioCondicion).append(" ");
                sql.append("INNER JOIN tBeneficiario beneficiario WITH(NOLOCK) ON detalle.").append(campoRfc).append(" = beneficiario.dRFC ");
                sql.append("INNER JOIN tBeneficiarioCuentasBancarias beneficiarioCuentas WITH(NOLOCK) ON detalle.").append(campoRfc).append(" = beneficiarioCuentas.dRFC ");
                sql.append("AND nCuentaBeneficiario = dCuentaBancaria ");
                sql.append("WHERE cDocumentoHaplicado = 'S' ");
                sql.append("AND pago.").append(folioCondicion).append(" IN (").append(nFolios).append(") ");
                sql.append("AND SUBSTRING(subCuentaBancaria,1,3) ").append(esBanorte).append(" ('072');");
            } else {
                sql.append(" SELECT CASE WHEN SUBSTRING(CTAB,1,3)='072' THEN '02' ELSE '04' END, ");
                sql.append(" cIdBancario, ");
                sql.append(" SUBSTRING('").append(ctaIntegradora).append("',8,10), ");
                sql.append(" CASE WHEN pago.ID_DESTINO_GASTO = 'GCRE' THEN 'NA' ");
                sql.append("	  WHEN SUBSTRING(CTAB,1,3)='072' THEN SUBSTRING(CTAB,8,10) ELSE CTAB END, ");
                sql.append(" CONVERT(VARCHAR(32), mImporteNeto) AS mImporteNeto, ");
                sql.append(" RIGHT('000000' + CONVERT(VARCHAR(6), ").append(folioCondicion).append("),6) cxp, ");
                sql.append(" LEFT((SELECT dbo.fn_quitarCaracteresEspeciales('CONAFOR ' + ");
                sql.append(" (SELECT TOP 1 DESTINO_GASTO FROM CAT_DESTINO_GASTO gasto WITH (NOLOCK) ");
                sql.append(" WHERE gasto.ID_DESTINO_GASTO = pago.ID_DESTINO_GASTO))), 30), ");
                sql.append(" 'CNF010405EG1', ");
                sql.append(" '', ");
                sql.append(" '', ");
                sql.append(" 'x' ");
                sql.append(" FROM ").append(tablaEncabezado).append(" pago WITH(NOLOCK) ");
                sql.append(" INNER JOIN tBeneficiario beneficiario WITH(NOLOCK) ON pago.").append(campoRfc).append(" = beneficiario.dRFC ");
                sql.append(" WHERE cDocumentoHaplicado = 'S' ");
                sql.append(" AND ").append(folioCondicion).append(" IN (").append(nFolios).append(") ");
                sql.append(" AND SUBSTRING(CTAB,1,3) ").append(esBanorte).append(" ('072');");
            }
            log.info("Object: {}", "Ejecutando query de Layout " + sTipo + " [" + sql.toString() + "]");
            pstmLayout = conn.prepareStatement(sql.toString());
            rsLayout = pstmLayout.executeQuery();
            while (rsLayout.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 12; i++) {
                    detalle.append(token).append(rsLayout.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    token = "	";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(rsLayout, false);
            CloseObject.closeObject(pstmLayout, false);
        }
        return arrListaComp;
    }

    public static void actualizaEnviadoSICOPCaja(Connection conn, String nFolios, String sTipo) throws Exception {
        PreparedStatement pstm = null;
        String tablaEncabezado = "";
        String folioCondicion = "";
        try {
            if (sTipo.equals("CAJA")) {
                tablaEncabezado = "tcajaencabezado";
                folioCondicion = "nFoliocaja";
            }
            pstm = conn.prepareStatement("UPDATE " + tablaEncabezado + " SET nEnviadoSICOP = 1 WHERE " + folioCondicion + " IN (" + nFolios + ")");
            pstm.executeUpdate();
            log.info("Object: {}", "Ejecutando query de actualizacion del Enviado a SICOP " + sTipo + " en los folios " + nFolios);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(pstm, false);
        }
    }
}
