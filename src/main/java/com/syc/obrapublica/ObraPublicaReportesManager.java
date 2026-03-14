package com.syc.obrapublica;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ObraPublicaReportesManager {

    private static final Logger log = LoggerFactory.getLogger(ObraPublicaReportesManager.class);

    private static final String[] columnasTotales = { "", "LP", "I3P", "AD", "CC", "", "", "", "", "", "totalma", "totalcontratosiniva", "montoIVA", "montoTotal", "totalCMAmp", "totalCMRed", "total_cnt_cm", "ret2pc", "ret5pc", "totalpagos", "totalejercido", "por_ejercer", "penalizaciones_aplicadas", "saldo_del_contrato", "" };

    public static String generaReporteSeguimientodeObraToExcel(Connection conn, String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
        Map<String, Map<String, ReporteSeguimientoContratosBean>> result = generaReporteSeguimientoContratos(conn, fechaI, fechaF, idunidadresponsable, idArea);
        if (result != null && !result.isEmpty()) {
            StringBuffer excelFile = new StringBuffer();
            Iterator<String> i = result.keySet().iterator();
            DecimalFormat formato = new DecimalFormat("##############0.00");
            Map<String, Double> totalGeneral = new HashMap<String, Double>();
            int totalContratos = 0;
            while (i.hasNext()) {
                String ur = i.next();
                Map<String, ReporteSeguimientoContratosBean> urResult = result.get(ur);
                Map<String, Double> totalUR = new HashMap<String, Double>();
                Iterator<String> itArea = urResult.keySet().iterator();
                String encabezadoUR = getNombreUR(conn, ur);
                excelFile.append(RSToTable.StringToRow(encabezadoUR, 12, 12) + "\n");
                int totalCntUR = 0;
                while (itArea.hasNext()) {
                    String area = itArea.next();
                    String encArea = getNombreArea(conn, Integer.parseInt(area));
                    excelFile.append(RSToTable.StringToRow(encArea, 12, 12) + "\n");
                    ReporteSeguimientoContratosBean rAdjudicadosB = urResult.get(area);
                    List<String[]> info = rAdjudicadosB.getInfo();
                    int totCnt = 0;
                    for (Iterator<String[]> itInfo = info.iterator(); itInfo.hasNext(); ) {
                        ++totCnt;
                        ++totalContratos;
                        excelFile.append(RSToTable.ArrayToRow(itInfo.next(), new int[] { 12, 13, 14, 15 }));
                    }
                    String[] subtotalArea = new String[9];
                    subtotalArea[0] = "Subtotal en la " + encArea;
                    subtotalArea[1] = "Total " + totCnt + " contratos";
                    //subtotalArea[2] = String.valueOf(rAdjudicadosB.getTotales().get("total_obra_conv").doubleValue());
                    //subtotalArea[7] = String.valueOf(rAdjudicadosB.getTotales().get("avance_financiero").doubleValue());
                    //subtotalArea[8] = String.valueOf(rAdjudicadosB.getTotales().get("avance_financiero_total").doubleValue());
                    subtotalArea[2] = formato.format(rAdjudicadosB.getTotales().get("total_obra_conv"));
                    subtotalArea[7] = formato.format(rAdjudicadosB.getTotales().get("avance_financiero"));
                    subtotalArea[8] = formato.format(rAdjudicadosB.getTotales().get("avance_financiero_total"));
                    excelFile.append(RSToTable.ConvertArrayToRow(subtotalArea, new int[] { 3 }));
                    totalUR = sumaTotales(totalUR, rAdjudicadosB.getTotales());
                    totalCntUR += totCnt;
                }
                String[] subtotalUR = new String[9];
                subtotalUR[0] = "Subtotal en el " + encabezadoUR;
                subtotalUR[1] = "Total " + totalCntUR + " contratos";
                //subtotalUR[2] = (Double.parseDouble(totalUR.get("total_obra_conv").toString()));
                //subtotalUR[7] = String.valueOf(totalUR.get("avance_financiero").);
                //subtotalUR[8] = String.valueOf(totalUR.get("avance_financiero_total"));
                subtotalUR[2] = formato.format(totalUR.get("total_obra_conv"));
                subtotalUR[7] = formato.format(totalUR.get("avance_financiero"));
                subtotalUR[8] = formato.format(totalUR.get("avance_financiero_total"));
                excelFile.append(RSToTable.ConvertArrayToRow(subtotalUR, new int[] { 3 }));
                totalGeneral = sumaTotales(totalGeneral, totalUR);
            }
            String[] totalCNA = new String[9];
            totalCNA[0] = "Total en la Comision Nacional del Agua";
            totalCNA[1] = "Total " + totalContratos + " contratos";
            //totalCNA[2] = String.valueOf(totalGeneral.get("total_obra_conv"));
            //totalCNA[7] = String.valueOf(totalGeneral.get("avance_financiero"));
            //totalCNA[8] = String.valueOf(totalGeneral.get("avance_financiero_total"));
            totalCNA[2] = formato.format(totalGeneral.get("total_obra_conv"));
            totalCNA[7] = formato.format(totalGeneral.get("avance_financiero"));
            totalCNA[8] = formato.format(totalGeneral.get("avance_financiero_total"));
            excelFile.append(RSToTable.ConvertArrayToRow(totalCNA, new int[] { 3 }));
            log.debug("Object: {}", excelFile.toString());
            return excelFile.toString();
        } else {
            return "";
        }
    }

    public static String generaReporteContratosAcumuladoToExcel(Connection conn, String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
        Map<String, Map<String, ReporteContratosAdjudicadosBean>> result = generaReporteContratosAdjudicados(conn, fechaI, fechaF, idunidadresponsable, idArea);
        if (result != null && !result.isEmpty()) {
            StringBuffer excelFile = new StringBuffer();
            Iterator<String> i = result.keySet().iterator();
            int totalContratos = 0;
            DecimalFormat formato = new DecimalFormat("##############0.00");
            Map<String, Double> totalGeneral = new HashMap<String, Double>();
            while (i.hasNext()) {
                String ur = i.next();
                Map<String, ReporteContratosAdjudicadosBean> urResult = result.get(ur);
                Map<String, Double> totalUR = new HashMap<String, Double>();
                Iterator<String> itArea = urResult.keySet().iterator();
                String encabezadoUR = getNombreUR(conn, ur);
                excelFile.append(RSToTable.StringToRow(encabezadoUR, 18, 18) + "\n");
                int totalCntUR = 0;
                while (itArea.hasNext()) {
                    String area = itArea.next();
                    String encArea = getNombreArea(conn, Integer.parseInt(area));
                    excelFile.append(RSToTable.StringToRow(encArea, 18, 18) + "\n");
                    ReporteContratosAdjudicadosBean rAdjudicadosB = urResult.get(area);
                    List<String[]> info = rAdjudicadosB.getInfo();
                    int totCnt = 0;
                    for (Iterator<String[]> itInfo = info.iterator(); itInfo.hasNext(); ) {
                        ++totCnt;
                        ++totalContratos;
                        excelFile.append(RSToTable.ArrayToRow(itInfo.next(), new int[] { 18, 19, 20, 21, 22 }));
                    }
                    String[] subtotalArea = new String[17];
                    subtotalArea[0] = "Subtotal en la " + encArea;
                    subtotalArea[1] = "Total " + totCnt + " contratos";
                    //Mofificacion del formato  5-dic-2013
                    //subtotalArea[8] = String.valueOf(rAdjudicadosB.getTotales().get("monto"));
                    //subtotalArea[12] = String.valueOf(rAdjudicadosB.getTotales().get("monto_asignado"));
                    //subtotalArea[13] = String.valueOf(rAdjudicadosB.getTotales().get("monto_ejercido"));
                    subtotalArea[8] = formato.format(rAdjudicadosB.getTotales().get("monto"));
                    subtotalArea[12] = formato.format(rAdjudicadosB.getTotales().get("monto_asignado"));
                    subtotalArea[13] = formato.format(rAdjudicadosB.getTotales().get("monto_ejercido"));
                    excelFile.append(RSToTable.ConvertArrayToRow(subtotalArea, new int[] { 1, 2 }));
                    totalUR = sumaTotales(totalUR, rAdjudicadosB.getTotales());
                    totalCntUR += totCnt;
                }
                String[] subtotalUR = new String[17];
                subtotalUR[0] = "Subtotal en el " + encabezadoUR;
                subtotalUR[1] = "Total " + totalCntUR + " contratos";
                //Mofificacion del formato  5-dic-2013
                //subtotalUR[8] = String.valueOf(totalUR.get("monto"));
                //subtotalUR[12] = String.valueOf(totalUR.get("monto_asignado"));
                //subtotalUR[13] = String.valueOf(totalUR.get("monto_ejercido"));
                subtotalUR[8] = formato.format(totalUR.get("monto"));
                subtotalUR[12] = formato.format(totalUR.get("monto_asignado"));
                subtotalUR[13] = formato.format(totalUR.get("monto_ejercido"));
                excelFile.append(RSToTable.ConvertArrayToRow(subtotalUR, new int[] { 1, 2 }));
                totalGeneral = sumaTotales(totalGeneral, totalUR);
            }
            String[] totalCNA = new String[17];
            totalCNA[0] = "Total en la Comision Nacional del Agua";
            //totalCNA[1] = "Total " + totalGeneral + " contratos";//MODIF. 5-DIC-2013
            //totalCNA[8] = String.valueOf(totalGeneral.get("monto"));
            //totalCNA[12] = String.valueOf(totalGeneral.get("monto_asignado"));
            //totalCNA[13] = String.valueOf(totalGeneral.get("monto_ejercido"));
            totalCNA[1] = "Total " + totalContratos + " contratos";
            totalCNA[8] = formato.format(totalGeneral.get("monto"));
            totalCNA[12] = formato.format(totalGeneral.get("monto_asignado"));
            totalCNA[13] = formato.format(totalGeneral.get("monto_ejercido"));
            excelFile.append(RSToTable.ConvertArrayToRow(totalCNA, new int[] { 1, 2 }));
            log.debug("Object: {}", excelFile.toString());
            return excelFile.toString();
        } else {
            return "";
        }
    }

    public static String generaReporteContratosAdjudicadosToExcel(Connection conn, String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
        Map<String, Map<String, ReporteContratosAdjudicadosBean>> result = generaReporteContratosAdjudicados(conn, fechaI, fechaF, idunidadresponsable, idArea);
        if (result != null && !result.isEmpty()) {
            StringBuffer excelFile = new StringBuffer();
            Iterator<String> i = result.keySet().iterator();
            int totalContratos = 0;
            DecimalFormat formato = new DecimalFormat("##############0.00");
            DecimalFormat df = new DecimalFormat("$#,###.#");
            Map<String, Double> totalGeneral = new HashMap<String, Double>();
            while (i.hasNext()) {
                String ur = i.next();
                Map<String, ReporteContratosAdjudicadosBean> urResult = result.get(ur);
                Map<String, Double> totalUR = new HashMap<String, Double>();
                Iterator<String> itArea = urResult.keySet().iterator();
                String encabezadoUR = getNombreUR(conn, ur);
                //excelFile.append(RSToTable.StringToRow(encabezadoUR, 21, 21) + "\n");
                int totalCntUR = 0;
                while (itArea.hasNext()) {
                    String area = itArea.next();
                    String encArea = getNombreArea(conn, Integer.parseInt(area));
                    excelFile.append(RSToTable.StringToRow(encArea, 21, 21) + "\n");
                    ReporteContratosAdjudicadosBean rAdjudicadosB = urResult.get(area);
                    List<String[]> info = rAdjudicadosB.getInfo();
                    int totCnt = 0;
                    for (Iterator<String[]> itInfo = info.iterator(); itInfo.hasNext(); ) {
                        ++totCnt;
                        ++totalContratos;
                        excelFile.append(RSToTable.ArrayToRow(itInfo.next(), new int[] { 21, 22 }) + "\n");
                    }
                    String[] subtotalArea = new String[20];
                    subtotalArea[0] = "Subtotal en la " + encArea;
                    subtotalArea[1] = "Total " + totCnt + " contratos";
                    //modificaciones a formatos
                    //subtotalArea[8] = String.valueOf(rAdjudicadosB.getTotales().get("monto"));
                    //subtotalArea[12] = String.valueOf(rAdjudicadosB.getTotales().get("monto_asignado"));
                    //subtotalArea[13] = String.valueOf(rAdjudicadosB.getTotales().get("monto_ejercido"));
                    //					subtotalArea[8] = formato.format(rAdjudicadosB.getTotales().get("monto"));
                    //					subtotalArea[12] = formato.format(rAdjudicadosB.getTotales().get("monto_asignado"));
                    //					subtotalArea[13] = formato.format(rAdjudicadosB.getTotales().get("monto_ejercido"));
                    subtotalArea[8] = df.format(rAdjudicadosB.getTotales().get("monto"));
                    subtotalArea[12] = df.format(rAdjudicadosB.getTotales().get("monto_asignado"));
                    subtotalArea[13] = df.format(rAdjudicadosB.getTotales().get("monto_ejercido"));
                    excelFile.append(RSToTable.ConvertArrayToRow(subtotalArea, new int[] { 1, 2 }) + "\n");
                    totalUR = sumaTotales(totalUR, rAdjudicadosB.getTotales());
                    totalCntUR += totCnt;
                }
                String[] subtotalUR = new String[20];
                subtotalUR[0] = "Subtotal en el " + encabezadoUR;
                subtotalUR[1] = "Total " + totalCntUR + " contratos";
                //subtotalUR[8] = String.valueOf(totalUR.get("monto"));
                //subtotalUR[12] = String.valueOf(totalUR.get("monto_asignado"));
                //subtotalUR[13] = String.valueOf(totalUR.get("monto_ejercido"));
                //				subtotalUR[8] = formato.format(totalUR.get("monto"));
                //				subtotalUR[12] = formato.format(totalUR.get("monto_asignado"));
                //				subtotalUR[13] = formato.format(totalUR.get("monto_ejercido"));
                subtotalUR[8] = df.format(totalUR.get("monto"));
                subtotalUR[12] = df.format(totalUR.get("monto_asignado"));
                subtotalUR[13] = df.format(totalUR.get("monto_ejercido"));
                //excelFile.append(RSToTable.ConvertArrayToRow(subtotalUR, new int[] { 1, 2 }) + "\n");
                totalGeneral = sumaTotales(totalGeneral, totalUR);
            }
            String[] totalCNA = new String[20];
            totalCNA[0] = "Total en la Comision Nacional del Agua";
            //totalCNA[1] = "Total " + totalGeneral + " contratos"; mod 6 dic-2013
            totalCNA[1] = "Total " + totalContratos + " contratos";
            //totalCNA[8] = String.valueOf(totalGeneral.get("monto"));
            //totalCNA[12] = String.valueOf(totalGeneral.get("monto_asignado"));
            //totalCNA[13] = String.valueOf(totalGeneral.get("monto_ejercido"));
            //			totalCNA[8] = formato.format(totalGeneral.get("monto"));
            //			totalCNA[12] =formato.format(totalGeneral.get("monto_asignado"));
            //			totalCNA[13] = formato.format(totalGeneral.get("monto_ejercido"));
            totalCNA[8] = df.format(totalGeneral.get("monto"));
            totalCNA[12] = df.format(totalGeneral.get("monto_asignado"));
            totalCNA[13] = df.format(totalGeneral.get("monto_ejercido"));
            excelFile.append(RSToTable.ConvertArrayToRow(totalCNA, new int[] { 1, 2 }) + "\n");
            log.debug("Object: {}", excelFile.toString());
            return excelFile.toString();
        } else {
            return "";
        }
    }

    public static Map<String, Map<String, ReporteSeguimientoContratosBean>> generaReporteSeguimientoContratos(Connection conn, String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String token = " WHERE ";
        String query = "";
        query += "SELECT a.ccvecontrato, " + "       a.cdescripcion, " + "       a.cbeneficiario, " + "       a.crfc, " + "       a.total_obra_conv, " + "       a.estado_realizacion_obra, " + "       a.finicio_obra, " + "       a.ffin_obra, " + "       a.avance_fisico, " + "       Round(CASE " + "               WHEN a.total_pagado IS NULL OR a.total_obra_conv = 0 THEN CONVERT(FLOAT, 0.0) " + "               ELSE CONVERT(FLOAT, a.total_pagado * 100 / a.total_obra_conv) " + "             END, 2)AS avance_financiero, " + "       a.avance_financiero_total, " + "       a.observaciones, " + "       a.cu_cc, " + "       a.cu_ue, " + "       a.cu_ur, " + "       a.id_area " + "FROM   (SELECT ce.ccvecontrato, " + "               ce.cdescripcion, " + "               ce.cbeneficiario, " + "               ce.crfc, " + "               ( CASE " + "                   WHEN cm.totalcm IS NULL THEN ce.nmonto " + "                   ELSE cm.totalcm " + "                 END ) " + "               total_obra_conv, " + "               CONVERT(VARCHAR, Isnull(ef.dentidadfederativa, '')) AS " + "                      estado_realizacion_obra, " + "               CONVERT(VARCHAR, ce.ffechainicontr, 103)            AS " + "               finicio_obra, " + "               CONVERT(VARCHAR, ce.ffechafincontr, 103)            AS ffin_obra, " + "               ''                                                  AS " + "               avance_fisico, " + "               CONVERT(FLOAT, Isnull(pag_op.total_pagado, 0))      AS " + "               total_pagado, " + "               Isnull(pag_op.total_pagado, 0)                      AS " + "                      avance_financiero_total, " + "               ''                                                  AS " + "               observaciones, " + "               ce.cu_cc, " + "               ce.cu_ue, " + "               ce.cu_ur, " + "               u.id_area " + ",   pag_op.fprogramadapago      FROM   tobrapublicacompromisoencabezado ce  WITH(NOLOCK)" + "               LEFT OUTER JOIN tcatalogoentidadfederativa ef  WITH(NOLOCK)" + "                            ON ce.cidentidadfederativa = ef.centidadfederativa " + "               LEFT OUTER JOIN convenio_modificatorio_obra_publica cm  WITH(NOLOCK)" + "                            ON ce.ccvecontrato = cm.ccvecontrato " + "               LEFT OUTER JOIN vpagosobrapublica pag_op  WITH(NOLOCK)" + "                            ON ce.ccvecontrato = pag_op.cfoliocontratoobra " + "               LEFT OUTER JOIN vimx_usuario u  WITH(NOLOCK)" + "                            ON ce.u_login = u.u_login " + "        WHERE  cdocumentohaplicado = 'S') AS a";
        if (fechaI != null && !"".equals(fechaI)) {
            query += token + " CONVERT(DATE, a.fprogramadapago, 103) >=  " + "                                   CONVERT(DATE, ?, 103)  ";
            token = " AND ";
        }
        if (fechaF != null && !"".equals(fechaF)) {
            query += token + " CONVERT(DATE, a.fprogramadapago, 103) <=  " + "                                   CONVERT(DATE, ?, 103)  ";
            token = " AND ";
        }
        if (idunidadresponsable != null && !"".equals(idunidadresponsable)) {
            query += token + " cU_UR = ?";
            token = " AND ";
        }
        if (idArea != null && !"".equals(idArea)) {
            query += token + " ID_AREA = ? ";
            token = " AND ";
        }
        // query += token + " cgrupofuncional is not null ";
        try {
            log.trace("Object: {}", query.toString());
            pStmnt = conn.prepareStatement(query);
            int i = 1;
            if (fechaI != null && !"".equals(fechaI))
                pStmnt.setString(i++, fechaI);
            if (fechaF != null && !"".equals(fechaF))
                pStmnt.setString(i++, fechaF);
            if (idunidadresponsable != null && !"".equals(idunidadresponsable))
                pStmnt.setString(i++, idunidadresponsable);
            if (idArea != null && !"".equals(idArea))
                pStmnt.setString(i++, idArea);
            rs = pStmnt.executeQuery();
            Map<String, Map<String, ReporteSeguimientoContratosBean>> m = ObraPublicaReportesManager.agrupaContenidoContratos(rs);
            return m;
        } finally {
            try {
                CloseObject.closeObject(pStmnt, false);
                CloseObject.closeObject(rs, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static Map<String, Map<String, ReporteContratosAdjudicadosBean>> generaReporteContratosAdjudicados(Connection conn, String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String token = " WHERE ";
        String query = "";
        query += "select distinct  " + "       ope.ccvecontrato,ent.siglas AS subdir,entUR.siglas AS acontratante  " + "       ,mtot.cgrupofuncional,mtot.cfuncion,mtot.csubfuncion,mtot.cunidadejecutora,  " + "       mtot.cactividadinstitucional,mtot.cprogramapresupuestario  " + "       ,ope.nMontoConIVA as monto  " + "       ,cadj.siglasTAdjudicacion AS tipo_adjudicacion,ope.cdescripcion  " + "       ,ope.cbeneficiario  " + "       ,af.mmontoestimacion as monto_asignado  " + "       ,mtot.suma as monto_ejercido  " + "       ,CASE WHEN ope.ctiporecurso = 'CE' THEN 'X' ELSE '' END AS cexterno_si  " + "       ,CASE WHEN ope.ctiporecurso <> 'CE' THEN 'X' ELSE '' END  AS cexterno_no  " + "       ,cto.cdescripcion AS tipo_obra  " + "       ,ope.crfc as cRFC " + "       ,CONVERT(VARCHAR, ope.ffechainicontr, 103) AS fFechaIniContr " + "       ,CONVERT(VARCHAR, ope.ffechafincontr, 103) AS fFechaFinContr " + "       ,ope.cu_ur,u.id_area " + "from tobrapublicacompromisoencabezado ope  " + "        left join (select  ccvecontrato, sum(mmontoestimacion) mmontoestimacion, sum(mmontoFisicoEjecutado) mmontoFisicoEjecutado, sum(mmontofisicoprogramado ) mmontofisicoprogramado from tobrapublicaavancefisico group by ccvecontrato) af on af.ccvecontrato = ope.ccvecontrato " + "        inner join(select sum(tp.mImporteMasIva) as suma,tp.cfoliocontratoobra " + "                      ,pod.cgrupofuncional,pod.cfuncion,pod.csubfuncion,pod.cunidadejecutora, " + "                       pod.cactividadinstitucional,pod.cprogramapresupuestario " + "                       from tpagoobraencabezado as tp  " + "                       LEFT OUTER JOIN v_op_pagos_ep pod ON pod.nfoliopagoobra = tp.nfoliopagoobra  " + "                       where cDocumentoHaplicado='S'  ";
        query += "                 group by tp.cfoliocontratoobra,pod.cgrupofuncional,pod.cfuncion,pod.csubfuncion,pod.cunidadejecutora, " + "                       pod.cactividadinstitucional,pod.cprogramapresupuestario  " + "                       )mtot on mtot.cFolioContratoObra=ope.cCveContrato  " + "        INNER JOIN tcatalogoadjudicacion cadj   WITH(NOLOCK) ON ope.ctipoadjudica = cadj.cidtadjudicacion  " + "        INNER JOIN tcatalogotipoobra cto   WITH(NOLOCK) ON ope.ctipoobra = cto.cidtobra  " + "        LEFT OUTER JOIN mCatalogoEntidadFederativaRpt ent   WITH(NOLOCK) ON ope.cu_ue = ent.cIdUnidadEjecutora  " + "        LEFT OUTER JOIN mCatalogoEntidadFederativaRpt entUR  WITH(NOLOCK) ON ope.cu_ur = entUR.cIdUnidadEjecutora  " + "        INNER JOIN CG_CAT_EMPLEADO u  WITH(NOLOCK) ON ope.u_login = u.CE_OS_RESPONSABLE  ";
        if (fechaI != null && !"".equals(fechaI)) {
            query += "                               AND CONVERT(DATE, ope.fFechaOficio, 103) >=  " + "                                   CONVERT(DATE, ?, 103)  ";
        }
        if (fechaF != null && !"".equals(fechaF))
            query += "                               AND CONVERT(DATE, ope.fFechaOficio, 103) <=  " + "                                   CONVERT(DATE, ?, 103)  ";
        if (idunidadresponsable != null && !"".equals(idunidadresponsable)) {
            query += token + " cU_UR = ?";
            token = " AND ";
        }
        if (idArea != null && !"".equals(idArea)) {
            query += token + " ID_AREA = ? ";
            token = " AND ";
        }
        try {
            log.trace("Object: {}", query.toString());
            pStmnt = conn.prepareStatement(query);
            int i = 1;
            if (fechaI != null && !"".equals(fechaI))
                pStmnt.setString(i++, fechaI);
            if (fechaF != null && !"".equals(fechaF))
                pStmnt.setString(i++, fechaF);
            if (idunidadresponsable != null && !"".equals(idunidadresponsable))
                pStmnt.setString(i++, idunidadresponsable);
            if (idArea != null && !"".equals(idArea))
                pStmnt.setString(i++, idArea);
            rs = pStmnt.executeQuery();
            Map<String, Map<String, ReporteContratosAdjudicadosBean>> m = ObraPublicaReportesManager.agrupaContenidoContratosAdjudicados(rs);
            return m;
        } finally {
            try {
                CloseObject.closeObject(pStmnt, false);
                CloseObject.closeObject(rs, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static Map<String, Map<String, ReporteSeguimientoContratosBean>> agrupaContenidoContratos(ResultSet rs) throws Exception {
        Map<String, Map<String, ReporteSeguimientoContratosBean>> result = new HashMap<String, Map<String, ReporteSeguimientoContratosBean>>();
        while (rs.next()) {
            String ur = rs.getString("cu_ur");
            String idArea = rs.getString("id_area");
            Map<String, ReporteSeguimientoContratosBean> mUR = result.get(ur);
            if (mUR == null) {
                mUR = new HashMap<String, ReporteSeguimientoContratosBean>();
                result.put(ur, mUR);
            }
            ReporteSeguimientoContratosBean idAreaBean = mUR.get(idArea);
            if (idAreaBean == null) {
                idAreaBean = ReporteSeguimientoContratosBeanManager.instanceFromRS(rs);
                mUR.put(idArea, idAreaBean);
            } else
                mUR.put(idArea, ReporteSeguimientoContratosBeanManager.updateInstanceFromRS(rs, idAreaBean));
        }
        return result;
    }

    public static Map<String, Map<String, ReporteContratosAdjudicadosBean>> agrupaContenidoContratosAdjudicados(ResultSet rs) throws Exception {
        Map<String, Map<String, ReporteContratosAdjudicadosBean>> result = new HashMap<String, Map<String, ReporteContratosAdjudicadosBean>>();
        while (rs.next()) {
            String ur = rs.getString("cu_ur");
            String idArea = rs.getString("id_area");
            Map<String, ReporteContratosAdjudicadosBean> mUR = result.get(ur);
            if (mUR == null) {
                mUR = new HashMap<String, ReporteContratosAdjudicadosBean>();
                result.put(ur, mUR);
            }
            ReporteContratosAdjudicadosBean idAreaBean = mUR.get(idArea);
            if (idAreaBean == null) {
                idAreaBean = ReporteContratosAdjudicadosBeanManager.instanceFromRS(rs);
                mUR.put(idArea, idAreaBean);
            } else
                mUR.put(idArea, ReporteContratosAdjudicadosBeanManager.updateInstanceFromRS(rs, idAreaBean));
        }
        return result;
    }

    public static boolean cambiaDatos(Connection conn, String cQuery) throws Exception {
        int iReturn = 0;
        PreparedStatement pstmnt = null;
        Boolean retval = false;
        try {
            pstmnt = conn.prepareStatement(cQuery);
            retval = pstmnt.execute();
            conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static ArrayList execQuery(Connection conn, String cQuery) throws Exception {
        Map<String, String> datarecord = null;
        ArrayList arrReturnQuery = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String[] cColQuery = null;
        int[] cColType = null;
        try {
            pstmnt = conn.prepareStatement(cQuery);
            rs = pstmnt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            int i = 0;
            arrReturnQuery.add(numberOfColumns);
            cColQuery = new String[numberOfColumns];
            cColType = new int[numberOfColumns];
            while (i < numberOfColumns) {
                cColQuery[i] = rsmd.getColumnName(i + 1);
                cColType[i] = rsmd.getColumnType(i + 1);
                i++;
            }
            arrReturnQuery.add(cColQuery);
            arrReturnQuery.add(cColType);
            boolean enviar = false;
            i = 0;
            int j = 0;
            while (rs.next()) {
                ArrayList arrDataQuery = new ArrayList();
                while (i < numberOfColumns) {
                    arrDataQuery.add(rs.getString(cColQuery[i]));
                    i++;
                }
                //System.out.println("Renglon:"+j);
                arrReturnQuery.add(arrDataQuery);
                i = 0;
                j++;
            }
            //System.out.println("termina lectura con un total de registros:"+j);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
        return arrReturnQuery;
    }

    public static ArrayList execMultiReporte(Connection conn, String cQuery, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, Workbook wb, String reportType, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar, String strGeneral, String strResumen) throws Exception {
        Map<String, String> datarecord = null;
        ArrayList arrReturnQuery = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String[] cColQuery = null;
        int[] cColType = null;
        try {
            //pstmnt = conn.prepareStatement(cQuery);
            //rs = pstmnt.executeQuery();
            CallableStatement cs1 = null;
            cs1 = conn.prepareCall(cQuery);
            cs1.setString(1, strCondMultiR);
            cs1.setString(2, columnasBorrar);
            cs1.setString(3, "cSubCuenta");
            cs1.setString(4, "cSubCuenta");
            cs1.setString(5, "mSaldo12");
            cs1.setString(6, strGeneral);
            cs1.setString(7, strUsuario);
            cs1.setString(8, strCondicion);
            cs1.setString(9, strResumen);
            cs1.registerOutParameter(10, Types.VARCHAR);
            rs = cs1.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            int i = 0;
            arrReturnQuery.add(numberOfColumns);
            cColQuery = new String[numberOfColumns];
            cColType = new int[numberOfColumns];
            while (i < numberOfColumns) {
                cColQuery[i] = rsmd.getColumnName(i + 1);
                cColType[i] = rsmd.getColumnType(i + 1);
                i++;
            }
            arrReturnQuery.add(cColQuery);
            arrReturnQuery.add(cColType);
            boolean enviar = false;
            i = 0;
            int j = 0;
            while (rs.next()) {
                ArrayList arrDataQuery = new ArrayList();
                while (i < numberOfColumns) {
                    arrDataQuery.add(rs.getString(cColQuery[i]));
                    i++;
                }
                //System.out.println("Renglon:"+j);
                arrReturnQuery.add(arrDataQuery);
                i = 0;
                j++;
            }
            //System.out.println("termina lectura con un total de registros:"+j);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
        return arrReturnQuery;
    }

    public static String generaReporteFormato10ToExcel(Connection conn, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, Workbook wb, String reportType) throws Exception {
        generaReporteFormato10(conn, cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, wb, reportType, "", "", "", "");
        /*
		if (result != null && !result.isEmpty()) {
			StringBuffer excelFile = new StringBuffer();
			Iterator<String> i = result.keySet().iterator();
			while (i.hasNext()) {
				String ur = i.next();
				Map<String, ReporteF10Bean> urResult = result.get(ur);
				Map<String, Double> totalUR = new HashMap<String, Double>();
				double[][] resumenTotal = new double[4][2];
				Iterator<String> itCc = urResult.keySet().iterator();

				String encabezadoUR = new String("Nombre del ente publico:" + getNombreUR(conn, ur));
				excelFile.append(RSToTable.StringToRow(encabezadoUR, 10, 25) + "\n");
				excelFile.append(reportBody);
				while (itCc.hasNext()) {
					ReporteF10Bean rf10b = urResult.get(itCc.next());
					List<String[]> info = rf10b.getInfo();
					for (Iterator<String[]> itInfo = info.iterator(); itInfo.hasNext();) {
						excelFile.append(RSToTable.ArrayToRow(itInfo.next(), new int[] { 21, 22, 23 }, new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 }));
					}
					totalUR = sumaTotales(totalUR, rf10b.getTotales());
					resumenTotal = sumaResumen(resumenTotal, rf10b.getResumen());
				}
				excelFile.append(RSToTable.ArrayToRow(totalesToArray(totalUR), new int[] {}, new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 }));
				excelFile.append(RSToTable.MatrixToRow(generaMatrizResumen(resumenTotal), new int[] { 4, 1, 3, 1 }, 9));
			}
			log.debug(excelFile.toString());
			return excelFile.toString();
		} else*/
        return "";
    }

    public static String generaReporteFormato10ToExcel(Connection conn, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, Workbook wb, String reportType, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar) throws Exception {
        generaReporteFormato10(conn, cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, wb, reportType, strUsuario, strCondicion, strCondMultiR, columnasBorrar);
        return "";
    }

    public static Map<String, Map<String, ReporteF10Bean>> generaReporteFormato10(Connection conn, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, Workbook wb, String reportType, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        ResultSet rsHeader = null;
        String token = " WHERE ";
        String query = "";
        if ("FORMATO_10".equalsIgnoreCase(reportType)) {
            query += "select a.ccveconcurso, a.LP, a.I3P, case when a.AD + a.LP + I3P + CC = '' then '1' else  a.AD end AD , a.CC, a.articulo, a.pasivo, a.numero, a.contratista, a.cdescripcion, case when a.totalma = 0 then a.montoTotal else a.totalma end , a.totalcontratosiniva, a.montoIVA, a.montoTotal, a.totalCMAmp, a.totalCMRed, a.total_cnt_cm, a.ret2pc, a.ret5pc, 0.0 ret2_3iva, a.totalpagos, a.totalejercido, CONVERT(MONEY, a.total_cnt_cm - totalejercido) AS por_ejercer, mPenalizacion penalizaciones, 0 Saldo, '' observaciones, a.cu_ur, a.cu_cc, a.faplicacion   " + "FROM   (SELECT mPenalizacion, ccveconcurso,  " + "               CASE  " + "                 WHEN ctipoadjudica = '01' THEN '1' " + "                 ELSE ''  " + "               END                                              AS LP, " + "               CASE " + "                 WHEN ctipoadjudica = '08' THEN '1' " + "                 ELSE ''  " + "               END                                              AS I3P, " + "               CASE  " + "                 WHEN ctipoadjudica = '23' " + "                       OR ctipoadjudica = '24' THEN '1' " + "                 ELSE '' " + "               END                                              AS AD, " + "               CASE  " + "                 WHEN ctipoadjudica = '17' THEN '1' " + "                 ELSE ''  " + "               END                                              AS CC, " + "               b.carticulo                                      AS articulo, " + "               ''                                               AS pasivo, " + "               a.ccvecontrato                                   AS numero,  " + "               a.cbeneficiario                                  AS contratista, " + "               a.cdescripcion,  " + "               CASE " + "                 WHEN c.monto_total_ma IS NULL THEN 0 " + "                 ELSE   c.monto_total_ma " + "               END                                              AS totalma,  " + "               a.nmonto                                         AS  " + "               totalcontratosiniva,  " + "               CONVERT(MONEY, a.nmonto * a.nporceiva)           AS montoIVA, " + "               a.nmontoconiva                                   AS montoTotal,  " + "               CONVERT(MONEY, ( CASE  " + "                                  WHEN d.totalcmiva IS NULL THEN 0.0 " + "                                  ELSE d.totalcmiva - a.nmontoconiva  " + "                                END ))                          AS totalCMAmp, " + "               CONVERT(MONEY, 0.0)                              AS totalCMRed,  " + "               CONVERT(MONEY, ( CASE  " + "                                  WHEN d.totalcmiva IS NULL THEN a.nmontoconiva " + "                                  ELSE d.totalcmiva  " + "                                END ))                          AS total_cnt_cm, " + "               Isnull(CONVERT(MONEY, e.ret2pcc + e.ret2pci), 0) AS ret2pc,  " + "               Isnull(CONVERT(MONEY, e.ret5pc), 0)              AS ret5pc,  " + "               Isnull(CONVERT(MONEY, e.total_pagado), 0)        AS totalpagos,  " + "               Isnull( CONVERT(MONEY, e.ret2pcc + e.ret2pci ), 0)  " + "               + Isnull(CONVERT(MONEY, e.ret5pc), 0)  " + "               + Isnull(CONVERT(MONEY, e.total_pagado), 0)      AS totalejercido " + "               , " + "               a.cu_ur, " + "               a.cu_cc,  " + "               a.faplicacion  " + "               , dbo.fn_entidadContrato(a.cCveContrato) entidad  " + "        FROM   tobrapublicacompromisoencabezado a  WITH(NOLOCK) " + "               INNER JOIN tcatalogoadjudicacion b  WITH(NOLOCK) " + "                       ON a.ctipoadjudica = b.cidtadjudicacion " + "               LEFT OUTER JOIN vcontratomultianualobrapublica c   WITH(NOLOCK) " + "                            ON a.ccvecontrato = c.ccvecontrato  " + "               LEFT OUTER JOIN convenio_modificatorio_obra_publica d  WITH(NOLOCK) " + "                            ON a.ccvecontrato = d.ccvecontrato  " + "               LEFT OUTER JOIN (SELECT a.cfoliocontratoobra,  " + "                                       Sum(b.mimportemasiva) AS total_pagado, " + "                                       Sum(b.mobra5)         AS ret5PC, " + "                                       Sum(b.mcnic)          AS ret2pcC, " + "                                       Sum(b.mPenalizacion)          AS mPenalizacion, " + "                                       Sum(b.mimdt)          AS ret2pcI " + "                                FROM   tpagoobraencabezado a WITH(nolock) " + "                                       INNER JOIN tpagoobradetalle b WITH(nolock " + "                                                  ) " + "                                               ON a.cdocumentohaplicado = 'S' " + "                                                  AND b.cevento <> 'ANTICIPO' " + "                                                  AND a.nfoliopagoobra =  " + "                                                      b.nfoliopagoobra ";
            if (fechaI != null && !"".equals(fechaI)) {
                query += "                                       AND  " + "                                       CONVERT(DATE, a.fprogramadapago, " + "                                       103) >=  " + "                                       CONVERT(DATE, ?, 103) ";
            }
            if (fechaF != null && !"".equals(fechaF)) {
                query += "                                       AND  " + "                                       CONVERT(DATE, a.fprogramadapago, " + "                                       103) <=  " + "                                       CONVERT(DATE, ?, 103) ";
            }
            query += "                                GROUP  BY a.cfoliocontratoobra) e " + "                            ON a.ccvecontrato = e.cfoliocontratoobra  " + "        WHERE  cdocumentohaplicado = 'S') a  ";
            if (idunidadresponsable != null && !"".equals(idunidadresponsable)) {
                query += token + " a.cu_ur = ? ";
                token = " AND ";
            }
            if (cCentroContable != null && !"".equals(cCentroContable)) {
                query += token + " a.cU_CC = ? ";
                token = " AND ";
            }
            query += "ORDER  BY cu_ur, " + "          cu_cc, entidad, numero ";
        } else if ("CONSOLI_NAL".equalsIgnoreCase(reportType)) {
            query += " select tipoArea, uEjecutora, Oiginal, Modificado, monto, monto/Modificado porcModificado, montoEjecutado, case when montoEstimado = 0 then 0 else montoEjecutado/montoEstimado end porcEjecutado, montoEstimado, montoEstimado/Modificado porcEstimado, TOTAL_MN, TOTAL_MN/Modificado porcPagado ";
            query += " from ( ";
            query += " select case when uniejec < 'B20' then 'GERENCIAS CENTRALES' else  ";
            query += " case when uEjecutora like '% cuenca %' then 'ORGANISMOS DE CUENCA' else 'GERENCIAS REGIONALES' end end tipoArea, * from (select  ";
            query += "  ueje.D_DESCRIPCION uEjecutora ";
            query += "  , Ueje.cUnidadResponsable UniEjec ";
            query += " ,sum(case when substring(s.nCuenta,1,5) = '81101' then s.mSaldoarrastre else 0 end)/1.00 Oiginal ";
            query += " ,sum(case when substring(s.nCuenta,1,5) = '81102' then s.mSaldoarrastre else 0 end)/1.00 Modificado ";
            query += "  from tSaldos S with(nolock) ";
            query += " , tcatalogoep catEP  with(nolock) ";
            query += " , tCatalogoCentroContable CC with(nolock) ";
            query += " , tCatUnidadResponsable UResp with(nolock) ";
            query += " , tCatUnidadResponsable UNor with(nolock) ";
            query += " , tCatUnidadResponsable Ueje with(nolock) ";
            query += "  where catep.EP = s.cSubCuenta and  CC.CcENTROcONTABLE = S.cCentroContable ";
            query += "  and UResp.cUnidadResponsable = s.cUnidadResponsable ";
            query += "   and unor.cUnidadResponsable = cUnidadNorativa ";
            query += "   and Ueje.cUnidadResponsable = cUnidadEjecutora ";
            query += "   AND (nCuenta like '81101%' or nCuenta like '81102%') ";
            query += "   and cPartida like '6%' ";
            query += "   group by  ";
            query += "   ueje.D_DESCRIPCION , uresp.D_DESCRIPCION ";
            query += " , UResp.cUnidadResponsable  ";
            query += "  , Ueje.cUnidadResponsable ) Tsal left join ";
            query += " (select  ";
            query += " isnull(pasEnc.cU_UR, isnull(comEnc.cU_UR, isnull(preEnc.cU_UR, apaEnc.cU_UR))) UEjec ";
            query += " , (sum(ISNULL(pasEnc.mMontoConIVA,ISNULL(comEnc.nMontoConIVA,ISNULL(preEnc.nMontoConIVA,ISNULL(apaEnc.nMontoConIVA,0)))) +isnull(conmod.mMontoconIva, 0)))/1.00 monto ";
            query += " , sum(isnull(mmontoestimacion, 0))/1.00 montoEstimado, sum(isnull(mmontoFisicoEjecutado, 0))/1.00 montoEjecutado, SUM(ISNULL(TOTAL_MN,0))/1.00 TOTAL_MN ";
            query += " ,max(comEnc.fFechaOficio) fFechaOficio ";
            query += "    from ";
            query += "  tObraPublicaApartadoEncabezado apaEnc with(nolock) ";
            query += "  left join tObraPublicaApartadoEncabezado preEnc with(nolock) on apaEnc.foliosai = preenc.FolioSAI  ";
            query += "  left join tObraPublicaCompromisoEncabezado comEnc with(nolock) on apaEnc.foliosai = comEnc.FolioSAI  ";
            query += "  left join tObraPublicaPagoPasivoEncabezado pasEnc with(nolock) on apaEnc.foliosai = pasEnc.FolioSAI  ";
            query += "  left join ( select af.foliosai,sum(isnull(af.mmontoestimacion,0)) mmontoestimacion, af.efiscalpago ";
            query += "  , sum(isnull(af.nporceavancefisicoejecutado,0)) nporceavancefisicoejecutado ";
            query += "  , sum(isnull(af.nporceavancefisicoestimado,0)) nporceavancefisicoestimado ";
            query += "  , sum(isnull(af.mmontoFisicoEjecutado,0)) mmontoFisicoEjecutado ";
            query += "  , sum(isnull(af.nporceavancefisicoprogramado,0)) nporceavancefisicoprogramado ";
            query += "  , sum(isnull( ";
            query += "  CASE WHEN siaff.FOLIO_CLC = sicop.FOLIO_SIAFF_112 and sicop.FOLIO_SIAFF_112 = pe.nFolioSIAFF and obrae.cDocumentoHaplicado = 'S' and siaff.ESTATUS_CLC = 'Pagada' THEN CAST(SIAFF.TOTAL_MN AS MONEY) ELSE 0 END ";
            query += "  ,0)) TOTAL_MN ";
            query += "   FROM tObraPublicaAvanceFisico AF with(nolock)  ";
            query += " 							 left join tpagoobraencabezado obrae with(nolock) on AF.noestimacion = obrae.cNoEstimacion and AF.mmontoestimacion = obrae.mImporteMasIva and obrae.cDocumentoHaplicado != 'C' ";
            query += " 							 left join tpagoobraencabezado poe with(nolock) on poe.cFolioContratoObra = AF.ccvecontrato and poe.cnoestimacion = AF.noestimacion and poe.cDocumentoHaplicado ='S' ";
            query += " 							 left join tpagadoencabezado pe with(nolock) on pe.nFolioPAGO = obrae.nFolioPAGOOBRA and pe.cTipoPago ='PAGOOBRA' ";
            query += " 							 left join clc_sicop sicop with(nolock) on sicop.FOLIO_SIAFF_112 = pe.nFolioSIAFF ";
            query += " 							 left join CLC_SIAFF_ENC siaff with(nolock) on siaff.FOLIO_CLC = sicop.FOLIO_SIAFF_112 ";
            query += " 							  group by af.foliosai,af.efiscalpago ";
            query += " ) avfEnc on apaEnc.foliosai = avfEnc.FolioSAI and avfEnc.efiscalpago = (SELECT aejerciciofiscal AS cEjercicio  ";
            query += " 				FROM   tejerciciofiscal WITH(NOLOCK) ";
            query += " 				WHERE  cactivo = 1 ) ";
            query += " left join (select epe.ccvecontrato, sum(epe.mMontoconIva) mMontoconIva, epe.cU_UR, epe.aEjercicioFiscal  ";
            query += "    from tObraPublicaConvModifEncabezado epe with(nolock) where epe.cDocumentoHaplicado = 'S' and epe.aEjercicioFiscal =(SELECT aejerciciofiscal AS cEjercicio  ";
            query += " 				FROM   tejerciciofiscal WITH(NOLOCK) ";
            query += " 				WHERE  cactivo = 1 ) ";
            query += "     group by epe.cCveContrato, epe.cU_UR, epe.aEjercicioFiscal ) conMod ";
            query += "       on conmod.cCveContrato = comEnc.cCveContrato ";
            query += "  where apaEnc.cDocumentoHaplicado = 'S' ";
            query += "  and preEnc.cDocumentoHaplicado = 'S' ";
            query += "  and comEnc.cDocumentoHaplicado = 'S' ";
            if (fechaI != null && !"".equals(fechaI))
                query += "  and comenc.fFechaOficio >= convert(date, '" + fechaI + "', 103) ";
            if (fechaF != null && !"".equals(fechaF))
                query += "  and comenc.fFechaOficio <= convert(date, '" + fechaF + "', 103) ";
            query += "  group by isnull(pasEnc.cU_Ur, isnull(comEnc.cU_Ur, isnull(preEnc.cU_Ur, apaEnc.cU_Ur))) ) tabContr ";
            query += "  on tabContr.UEjec = Tsal.UniEjec ";
            query += ") m ";
            query += " order by tipoArea, uEjecutora  ";
        } else if ("CONSOLI_AREA_RESP".equalsIgnoreCase(reportType)) {
            query += " select tipoArea, uEjecutora, Oiginal, Modificado, monto, monto/Modificado porcModificado, montoEjecutado, case when montoEstimado = 0 then 0 else montoEjecutado/montoEstimado end porcEjecutado, montoEstimado, montoEstimado/Modificado porcEstimado, TOTAL_MN, TOTAL_MN/Modificado porcPagado from ";
            query += " ( ";
            query += " select 'NADA' tipoArea, * from (select  ";
            query += "  unor.D_DESCRIPCION uEjecutora ";
            query += "  , Unor.cUnidadResponsable UniEjec ";
            query += " ,sum(case when substring(s.nCuenta,1,5) = '81101' then s.mSaldoarrastre else 0 end)/1000.00 Oiginal ";
            query += " ,sum(case when substring(s.nCuenta,1,5) = '81102' then s.mSaldoarrastre else 0 end)/1000.00 Modificado ";
            query += "  from tSaldos S with(nolock) ";
            query += " , tcatalogoep catEP  with(nolock) ";
            query += " , tCatalogoCentroContable CC with(nolock) ";
            query += " , tCatUnidadResponsable UResp with(nolock) ";
            query += " , tCatUnidadResponsable UNor with(nolock) ";
            query += " , tCatUnidadResponsable Ueje with(nolock) ";
            query += "  where catep.EP = s.cSubCuenta and  CC.CcENTROcONTABLE = S.cCentroContable ";
            query += "  and UResp.cUnidadResponsable = s.cUnidadResponsable ";
            query += "   and unor.cUnidadResponsable = cUnidadNorativa ";
            query += "   and Ueje.cUnidadResponsable = cUnidadEjecutora ";
            query += "   AND (nCuenta like '81101%'  or nCuenta like '81102%') ";
            query += "   and catep.cPartida like '6%' ";
            query += "   group by  ";
            query += "   unor.D_DESCRIPCION  ";
            query += "  , Unor.cUnidadResponsable ) Tsal left join ";
            query += " (select  ";
            query += " isnull(pasEnc.cU_UE, isnull(comEnc.cU_Ue, isnull(preEnc.cU_Ue, apaEnc.cU_Ue))) UEjec ";
            query += " , (sum(ISNULL(pasEnc.mMontoConIVA,ISNULL(comEnc.nMontoConIVA,ISNULL(preEnc.nMontoConIVA,ISNULL(apaEnc.nMontoConIVA,0)))) +isnull(conmod.mMontoconIva, 0)))/1000.00 monto ";
            query += " , sum(isnull(mmontoestimacion, 0))/1000.00 montoEstimado, sum(isnull(mmontoFisicoEjecutado, 0))/1000.00 montoEjecutado, SUM(ISNULL(TOTAL_MN,0))/1000.00 TOTAL_MN ";
            query += "    from ";
            query += "  tObraPublicaApartadoEncabezado apaEnc with(nolock) ";
            query += "  left join tObraPublicaApartadoEncabezado preEnc with(nolock) on apaEnc.foliosai = preenc.FolioSAI  ";
            query += "  left join tObraPublicaCompromisoEncabezado comEnc with(nolock) on apaEnc.foliosai = comEnc.FolioSAI  ";
            query += "  left join tObraPublicaPagoPasivoEncabezado pasEnc with(nolock) on apaEnc.foliosai = pasEnc.FolioSAI  ";
            query += "  left join ( select af.foliosai,sum(isnull(af.mmontoestimacion,0)) mmontoestimacion, af.efiscalpago ";
            query += "  , sum(isnull(af.nporceavancefisicoejecutado,0)) nporceavancefisicoejecutado ";
            query += "  , sum(isnull(af.nporceavancefisicoestimado,0)) nporceavancefisicoestimado ";
            query += "  , sum(isnull(af.mmontoFisicoEjecutado,0)) mmontoFisicoEjecutado ";
            query += "  , sum(isnull(af.nporceavancefisicoprogramado,0)) nporceavancefisicoprogramado ";
            query += "  , sum(isnull( ";
            query += "  CASE WHEN siaff.FOLIO_CLC = sicop.FOLIO_SIAFF_112 and sicop.FOLIO_SIAFF_112 = pe.nFolioSIAFF and obrae.cDocumentoHaplicado = 'S' and siaff.ESTATUS_CLC = 'Pagada' THEN CAST(SIAFF.TOTAL_MN AS MONEY) ELSE 0 END ";
            query += "  ,0)) TOTAL_MN ";
            query += "   FROM tObraPublicaAvanceFisico AF with(nolock)  ";
            query += " 							 left join tpagoobraencabezado obrae with(nolock) on AF.noestimacion = obrae.cNoEstimacion and AF.mmontoestimacion = obrae.mImporteMasIva and obrae.cDocumentoHaplicado != 'C' ";
            query += " 							 left join tpagoobraencabezado poe with(nolock) on poe.cFolioContratoObra = AF.ccvecontrato and poe.cnoestimacion = AF.noestimacion and poe.cDocumentoHaplicado ='S' ";
            query += " 							 left join tpagadoencabezado pe with(nolock) on pe.nFolioPAGO = obrae.nFolioPAGOOBRA and pe.cTipoPago ='PAGOOBRA' ";
            query += " 							 left join clc_sicop sicop with(nolock) on sicop.FOLIO_SIAFF_112 = pe.nFolioSIAFF ";
            query += " 							 left join CLC_SIAFF_ENC siaff with(nolock) on siaff.FOLIO_CLC = sicop.FOLIO_SIAFF_112 ";
            query += " 							  group by af.foliosai,af.efiscalpago ";
            query += " ) avfEnc on apaEnc.foliosai = avfEnc.FolioSAI and avfEnc.efiscalpago = (SELECT aejerciciofiscal AS cEjercicio  ";
            query += " 				FROM   tejerciciofiscal WITH(NOLOCK) ";
            query += " 				WHERE  cactivo = 1 ) ";
            query += " left join (select epe.ccvecontrato, sum(epe.mMontoconIva) mMontoconIva, epe.cU_UR, epe.aEjercicioFiscal  ";
            query += "    from tObraPublicaConvModifEncabezado epe with(nolock) where epe.cDocumentoHaplicado = 'S' and epe.aEjercicioFiscal =(SELECT aejerciciofiscal AS cEjercicio  ";
            query += " 				FROM   tejerciciofiscal WITH(NOLOCK) ";
            query += " 				WHERE  cactivo = 1 ) ";
            query += "     group by epe.cCveContrato, epe.cU_UR, epe.aEjercicioFiscal ) conMod ";
            query += "       on conmod.cCveContrato = comEnc.cCveContrato ";
            query += "  where apaEnc.cDocumentoHaplicado = 'S' ";
            query += "  and preEnc.cDocumentoHaplicado = 'S' ";
            query += "  and comEnc.cDocumentoHaplicado = 'S' ";
            if (fechaI != null && !"".equals(fechaI))
                query += "  and comenc.fFechaOficio >= convert(date, '" + fechaI + "', 103) ";
            if (fechaF != null && !"".equals(fechaF))
                query += "  and comenc.fFechaOficio <= convert(date, '" + fechaF + "', 103) ";
            query += "  and comenc.cCveContrato in (select e.cIdContrato from tCompromisoEncabezado e where e.cDocumentoHaplicado = 'S') ";
            query += "   group by isnull(pasEnc.cU_Ue, isnull(comEnc.cU_Ue, isnull(preEnc.cU_Ue, apaEnc.cU_Ue))) ) tabContr ";
            query += "  on tabContr.UEjec = Tsal.UniEjec ";
            query += " ) m ";
            query += " order by tipoArea desc, uEjecutora  ";
        } else if ("CONSOLI_PROYECTO".equalsIgnoreCase(reportType)) {
            query += " select tipoArea, dProgramaPresupuestario, Oiginal, Modificado, monto, monto/Modificado porcModificado, montoEjecutado, montoEjecutado/Modificado porcEjecutado, montoEstimado, montoEstimado/Modificado porcEstimado, TOTAL_MN, TOTAL_MN/Modificado porcPagado from vConsolidadoxProyecto order by tipoArea desc, dProgramaPresupuestario  ";
        } else if ("MULTI_REPORTE".equalsIgnoreCase(reportType)) {
            //		query += " select apa_foliosai, apa_cCveContrato, apa_ep, apa_mes, apa_importe, 0.0 com_nMontoconPlirianual, apa_cDescripcion, apa_nMonto, apa_nPorceIVA, apa_nMontoconIva, pc_cCveContrato, pc_ep, pc_mes, pc_importe, com_cCveContrato, com_ep, com_mes, com_importe, com_nMonto, com_nPorceIVA, com_nMontoconIva  from vOP_multireporte  ";
            //		query = "{call sp_pMultiReporte_syc_OP(' AND aEjercicioFiscal_1 = ''2013''', '''81101'',''81102'',''81103'',''81104'',''81105'',''81106'',''81107'',''81108'',''81109'',''81110'',''82101'',''82102'',''82103'',''82104'',''82105'',''82106'',''82107'',''82108''', 'cSubCuenta', 'cSubCuenta', 'mSaldo12', 'GENERAL', 'WROBLES', @Salida OUTPUT)}";
            query = "{call sp_pMultiReporte_syc_OP2(?,?,?,?,?,?,?,?,?)}";
        } else if ("CONSOLI_PROYECTO_REGION".equalsIgnoreCase(reportType)) {
            query += " select tipoArea, dProgramaPresupuestario, Oiginal, Modificado, monto, monto/Modificado porcModificado, montoEjecutado, montoEjecutado/Modificado porcEjecutado, montoEstimado, montoEstimado/Modificado porcEstimado, TOTAL_MN, TOTAL_MN/Modificado porcPagado from vConsolidadoxProyectoxRegion order by tipoArea desc, dProgramaPresupuestario  ";
        } else if ("CONSOLI_POR_AREA_EJECUTORA".equalsIgnoreCase(reportType)) {
            query += "select  uNormativa,tipoArea,uEjecutora,Original,Modificado,montoContratado,montoContratado/1000 as por_Contratado,montoEjecutado, montoEjecutado/1000 as por_Ejecutado,montoEstimado, montoEstimado/1000 as Porc_Estimado   " + " from vConsolidadoporAreaEjecutora    " + " group by  UniEjec, uEjecutora,tipoArea,UniNormativa,uNormativa,Original,Modificado,montoContratado,montoEjecutado,montoEstimado " + " order by uNormativa,tipoArea, uEjecutora ";
        } else if ("CONSOLI_POR_AREA_EJECUTORA_y_PROYECTO".equalsIgnoreCase(reportType)) {
            query += "select  uEjecutora,tipoArea,Original,Modificado,montoContratado,montoContratado/1000 as por_Contratado,montoEjecutado, montoEjecutado/1000 as por_Ejecutado,montoEstimado, montoEstimado/1000 as Porc_Estimado   " + " from vConsolidadoporAreaEjecutorayProyecto   " + " group by  UniEjec, uEjecutora,tipoArea,Original,Modificado,montoContratado,montoEjecutado,montoEstimado " + " order by uEjecutora,tipoArea ";
        }
        String queryHeader = "select '";
        try {
            log.trace("Object: {}", query.toString());
            int i = 1;
            if ("MULTI_REPORTE".equalsIgnoreCase(reportType)) {
                CallableStatement cs1 = null;
                cs1 = conn.prepareCall(query);
                cs1.setString(1, strCondMultiR);
                cs1.setString(2, "'81101','81102','81103','81104','81105','81106','81107','81108','81109','81110','82101','82102','82103','82104','82105','82106','82107','82108'");
                cs1.setString(3, "cSubCuenta");
                cs1.setString(4, "cSubCuenta");
                cs1.setString(5, "mSaldo12");
                cs1.setString(6, "GENERAL");
                cs1.setString(7, strUsuario);
                cs1.setString(8, strCondicion);
                cs1.registerOutParameter(9, Types.VARCHAR);
                rs = cs1.executeQuery();
            } else if ("CONSOLI_NAL".equalsIgnoreCase(reportType) || "CONSOLI_AREA_RESP".equalsIgnoreCase(reportType)) {
                pstmnt = conn.prepareStatement(query);
                if (fechaI != null && !"".equals(fechaI)) {
                    queryHeader += " del '+cast(DAY(convert(date, '" + fechaI + "', 103)) as varchar(2))+ ' de '+DATENAME(MONTH, convert(date, '" + fechaI + "', 103)) +'";
                }
                if (fechaF != null && !"".equals(fechaF)) {
                    queryHeader += " al '+cast(DAY(convert(date, '" + fechaF + "', 103)) as varchar(2))+ ' de '+DATENAME(MONTH, convert(date, '" + fechaF + "', 103))+' de ' + CAST(year(convert(date, '" + fechaF + "', 103)) as varchar(4)) +'";
                } else
                    queryHeader += " al '+cast(DAY(getdate()) as varchar(2))+ ' de '+DATENAME(MONTH, getdate())+' de ' + CAST(year(getdate()) as varchar(4)) +'";
                rs = pstmnt.executeQuery();
            } else {
                pstmnt = conn.prepareStatement(query);
                if (fechaI != null && !"".equals(fechaI)) {
                    queryHeader += " del '+cast(DAY(convert(date, '" + fechaI + "', 103)) as varchar(2))+ ' de '+DATENAME(MONTH, convert(date, '" + fechaI + "', 103)) +'";
                    pstmnt.setString(i++, fechaI);
                }
                if (fechaF != null && !"".equals(fechaF)) {
                    queryHeader += " al '+cast(DAY(convert(date, '" + fechaF + "', 103)) as varchar(2))+ ' de '+DATENAME(MONTH, convert(date, '" + fechaF + "', 103))+' de ' + CAST(year(convert(date, '" + fechaF + "', 103)) as varchar(4)) +'";
                    pstmnt.setString(i++, fechaF);
                } else
                    queryHeader += " al '+cast(DAY(getdate()) as varchar(2))+ ' de '+DATENAME(MONTH, getdate())+' de ' + CAST(year(getdate()) as varchar(4)) +'";
                if (idunidadresponsable != null && !"".equals(idunidadresponsable))
                    pstmnt.setString(i++, idunidadresponsable);
                if (cCentroContable != null && !"".equals(cCentroContable))
                    pstmnt.setString(i++, cCentroContable);
                rs = pstmnt.executeQuery();
            }
            queryHeader += "' periodo, (select aEjercicioFiscal from tEjercicioFiscal ef where ef.cActivo = 1) ejercicioFiscal";
            pstmnt = conn.prepareStatement(queryHeader);
            rsHeader = pstmnt.executeQuery();
            generaXLS("", wb, rs, 0, conn, rsHeader, reportType, columnasBorrar);
            Map<String, Map<String, ReporteF10Bean>> m = ObraPublicaReportesManager.agrupaContenidoReporteF10(rs);
            return m;
        } finally {
            try {
                CloseObject.closeObject(rsHeader, false);
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(pstmnt, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private static Map<String, Map<String, ReporteF10Bean>> agrupaContenidoReporteF10(ResultSet rs) throws Exception {
        Map<String, Map<String, ReporteF10Bean>> result = new HashMap<String, Map<String, ReporteF10Bean>>();
        while (rs.next()) {
            String ur = rs.getString("cu_ur");
            String cc = rs.getString("cu_cc");
            Map<String, ReporteF10Bean> mUR = result.get(ur);
            if (mUR == null) {
                mUR = new HashMap<String, ReporteF10Bean>();
                result.put(ur, mUR);
            }
            ReporteF10Bean ccBean = mUR.get(cc);
            if (ccBean == null) {
                ccBean = ReporteF10BeanManager.instanceFromRS(rs);
                mUR.put(cc, ccBean);
            } else
                mUR.put(cc, ReporteF10BeanManager.updateInstanceFromRS(rs, ccBean));
        }
        return result;
    }

    public static final String getNombreUR(Connection conn, String IDUR) throws Exception {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String query = "SELECT D_DESCRIPCION FROM tCatUnidadResponsable  WITH(NOLOCK) where cUnidadResponsable = ?";
        try {
            pStmnt = conn.prepareStatement(query);
            pStmnt.setString(1, IDUR);
            rs = pStmnt.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                return "";
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(pStmnt, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static final String getNombreArea(Connection conn, int idArea) throws Exception {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String query = "SELECT D_DESCRIPCION from CG_CAT_AREAS  WITH(NOLOCK) where ID_AREA = ?";
        try {
            pStmnt = conn.prepareStatement(query);
            pStmnt.setInt(1, idArea);
            rs = pStmnt.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                return "";
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(pStmnt, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private static Map<String, Double> sumaTotales(Map<String, Double> to, Map<String, Double> from) {
        Iterator<String> i = from.keySet().iterator();
        while (i.hasNext()) {
            String keyStr = i.next();
            if (!to.containsKey(keyStr))
                to.put(keyStr, 0.0d);
            to.put(keyStr, to.get(keyStr) + from.get(keyStr));
        }
        return to;
    }

    private static String[] totalesToArray(Map<String, Double> totalUR) {
        String[] arr = new String[ObraPublicaReportesManager.columnasTotales.length];
        for (int i = 0; i < ObraPublicaReportesManager.columnasTotales.length; i++) {
            if ("".equals(ObraPublicaReportesManager.columnasTotales[i])) {
                arr[i] = "";
            } else {
                if (!totalUR.containsKey(ObraPublicaReportesManager.columnasTotales[i]))
                    arr[i] = "";
                else
                    arr[i] = String.valueOf(totalUR.get(ObraPublicaReportesManager.columnasTotales[i]));
            }
        }
        return arr;
    }

    private static double[][] sumaResumen(double[][] resumenTotal, double[][] resumen) {
        for (int i = 0; i < resumen.length; i++) for (int j = 0; j < resumen[i].length; j++) resumenTotal[i][j] += resumen[i][j];
        return resumenTotal;
    }

    private static String[][] generaMatrizResumen(double[][] resumen) {
        String[][] result = new String[6][4];
        result[0] = new String[] { "TIPO DE ADJUDICACION", "", "No. DE PROCEDIMIENTOS", "IMPORTE" };
        double totalOperaciones = 0.0d;
        double totalImporte = 0.0d;
        for (int i = 0; i < resumen.length; i++) {
            if (i == 0) {
                result[1] = new String[] { "POR LICITACION PUBLICA NACIONAL", "", String.valueOf(resumen[i][0]), String.valueOf(resumen[i][1]) };
                totalImporte += resumen[i][1];
                totalOperaciones += resumen[i][0];
            }
            if (i == 1) {
                result[2] = new String[] { "POR INVITACION A CUANDO MENOS 3", "", String.valueOf(resumen[i][0]), String.valueOf(resumen[i][1]) };
                totalImporte += resumen[i][1];
                totalOperaciones += resumen[i][0];
            }
            if (i == 2) {
                result[3] = new String[] { "ADJUDICACIÓN DIRECTA", "", String.valueOf(resumen[i][0]), String.valueOf(resumen[i][1]) };
                totalImporte += resumen[i][1];
                totalOperaciones += resumen[i][0];
            }
            if (i == 3) {
                result[4] = new String[] { "CONVENIO DE COLABORACIÓN", "", String.valueOf(resumen[i][0]), String.valueOf(resumen[i][1]) };
                totalImporte += resumen[i][1];
                totalOperaciones += resumen[i][0];
            }
        }
        result[5] = new String[] { "TOTAL DE PROCEDIMIENTOS E IMPORTE CONTRATADO.", "", String.valueOf(totalOperaciones), String.valueOf(totalImporte) };
        return result;
    }

    private static void generaXLS(String tipoReporte, Workbook wb, ResultSet rs, int numHoja, Connection conn, ResultSet rsHead, String reportType, String columnasBorrar) throws Exception {
        //ArrayList arrDetalle = null;
        ArrayList arrHeader = null;
        Sheet sheet = wb.getSheetAt(numHoja);
        //boolean errorInformix = false;
        //boolean errorProgress = false;
        Map<String, String> datarecord = null;
        ArrayList arrDetalle = new ArrayList();
        PreparedStatement pstmnt = null;
        String[] cColQuery = null;
        int[] cColType = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int numberOfColumns = rsmd.getColumnCount();
        int i = 0;
        arrDetalle.add(numberOfColumns);
        cColQuery = new String[numberOfColumns];
        cColType = new int[numberOfColumns];
        while (i < numberOfColumns) {
            cColQuery[i] = rsmd.getColumnName(i + 1);
            cColType[i] = rsmd.getColumnType(i + 1);
            i++;
        }
        arrDetalle.add(cColQuery);
        arrDetalle.add(cColType);
        boolean enviar = false;
        i = 0;
        int j = 0;
        while (rs.next()) {
            ArrayList arrDataQuery = new ArrayList();
            while (i < numberOfColumns) {
                arrDataQuery.add(rs.getString(cColQuery[i]));
                i++;
            }
            //System.out.println("Renglon:"+j);
            arrDetalle.add(arrDataQuery);
            i = 0;
            j++;
        }
        int nrengXLS = 4;
        nrengXLS = 1;
        j = 3;
        Cell laCelda = null;
        numberOfColumns = 0;
        int[] arrColTipo = null;
        String ctaAnterior = "";
        int ultRow = 1;
        ultRow = sheet.getLastRowNum();
        /*		if (arrHeader != null) {
			numberOfColumns = (Integer) arrHeader.get(0);
			arrColTipo = new int[numberOfColumns]; 
			arrColTipo = (int[]) arrHeader.get(2);

			//font.setFontName(HSSFFont.FONT_ARIAL);
			//font.setFontHeightInPoints((short) 10);
			//font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			while (j < arrHeader.size()){
				ArrayList arrAdecua = (ArrayList) arrHeader.get(j);
				Row RowDetIntegra = null;
				if (ultRow < nrengXLS) {
					RowDetIntegra = sheet.createRow(nrengXLS);
				}
				else{
					RowDetIntegra = sheet.getRow(nrengXLS);
				}
				i=0;
				while ( i < arrAdecua.size()) {
					if (ultRow < nrengXLS) {
						laCelda = RowDetIntegra.createCell(i);
					}
					else{
						laCelda = RowDetIntegra.getCell(i);
						if (laCelda == null)
							laCelda = RowDetIntegra.createCell(i);
							
					}
					if(arrColTipo[i]==3) {
						laCelda.setCellValue(Double.parseDouble((String)arrAdecua.get(i)));
						laCelda.setCellType(Cell.CELL_TYPE_NUMERIC);

						CellStyle cellStyle = wb.createCellStyle();
						cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
						//cellStyle.setFont(font);
						laCelda.setCellStyle(cellStyle);
					}
					else {
						CellStyle cellStyle = laCelda.getCellStyle();
						laCelda.setCellStyle(cellStyle);
						if (!"".equalsIgnoreCase((String)arrAdecua.get(i)))
							laCelda.setCellValue((String)arrAdecua.get(i));

					}
					i++;
				}

				arrAdecua=null;
				j++;
				nrengXLS++;
			}
		}
*/
        int primerDetalle = 11;
        nrengXLS = primerDetalle;
        //int conEncabezados = -1;
        //conEncabezados = 0;
        if (arrDetalle != null) {
            if ("FORMATO_10".equalsIgnoreCase(reportType)) {
                primerDetalle = 11;
                nrengXLS = primerDetalle;
                int nTotCols = 29;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(5, nTotCols, sheet, 0, 5);
                formatoTitulos.setArrayAgrupa(new int[][] { { 1, 1, 0, 9 }, { 2, 2, 0, 9 }, { 3, 3, 0, 9 }, { 4, 4, 0, 9 }, { 5, 5, 0, 9 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(5, nTotCols, sheet, 5, 23);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 0, 0, 9 }, { 1, 2, 0, 5 }, { 3, 4, 0, 0 }, { 3, 4, 5, 5 }, { 2, 4, 6, 6 }, { 2, 4, 7, 7 }, { 2, 4, 8, 8 }, { 2, 4, 9, 9 }, { 2, 4, 10, 10 }, { 2, 4, 11, 11 }, { 2, 4, 12, 12 }, { 2, 4, 13, 13 }, { 2, 4, 14, 14 }, { 2, 4, 15, 15 }, { 2, 4, 16, 16 }, { 2, 4, 17, 17 }, { 2, 4, 18, 18 }, { 2, 4, 19, 19 }, { 2, 4, 20, 20 }, { 2, 4, 21, 21 }, { 2, 4, 22, 22 }, { 2, 4, 23, 23 }, { 2, 4, 24, 24 } });
                formatoEncabezado.valCelda[2][11] = formatoEncabezado.valCelda[2][11].replace("2013", "2014");
                formatoEncabezado.valCelda[2][13] = formatoEncabezado.valCelda[2][13].replace("2013", "2014");
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 10, 0);
                ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(6, nTotCols, sheet, 13, 12);
                formatoResumen.setArrayAgrupa(new int[][] { { 0, 0, 1, 4 }, { 1, 1, 1, 4 }, { 2, 2, 1, 4 }, { 3, 3, 1, 4 }, { 4, 4, 1, 4 }, { 5, 5, 1, 4 }, { 0, 0, 5, 6 }, { 1, 1, 5, 6 }, { 2, 2, 5, 6 }, { 3, 3, 5, 6 }, { 4, 4, 5, 6 }, { 5, 5, 5, 6 } });
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 5;
                String[][] arrTit = formatoTitulos.getValCelda();
                if (rsHead.next())
                    arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(26)))) {
                        formatoSubTotal.print(sheet, nrengXLS++);
                        formatoResumen.totResumen(formatoSubTotal.getValCelda());
                        formatoResumen.print(sheet, nrengXLS);
                        formatoSubTotal.reinicia();
                        formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 8;
                        arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][26]));
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 5;
                    }
                    arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][26]));
                    formatoEncabezado.setValCelda(arrEncabezado);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 5;
                    }
                    formatoDetalle.print(sheet, nrengXLS);
                    formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    ctaAnterior = (String) arrAdecua.get(26);
                    j++;
                }
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
            } else if ("CONSOLI_NAL".equalsIgnoreCase(reportType)) {
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 12;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 7);
                formatoTitulos.setArrayAgrupa(new int[][] { { 0, 0, 3, 10 }, { 1, 1, 3, 10 }, { 2, 2, 3, 10 }, { 3, 3, 3, 10 }, { 4, 4, 3, 10 }, { 5, 5, 3, 10 }, { 6, 6, 3, 10 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(3, nTotCols, sheet, 8, 7);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 1, 1, 1 }, { 0, 1, 2, 2 }, { 0, 1, 3, 3 }, { 0, 0, 4, 5 }, { 0, 0, 6, 7 }, { 0, 0, 8, 9 }, { 0, 0, 10, 11 } });
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1, nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                //formatoResumen.setArrayAgrupa(new int[][] { {0,0,1,4}, {1,1,1,4},{2,2,1,4},{3,3,1,4},{4,4,1,4},{5,5,1,4}, {0,0,5,6}, {1,1,5,6},{2,2,5,6},{3,3,5,6},{4,4,5,6},{5,5,5,6}} );
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                if (rsHead.next())
                    arrTit[6][3] = rsHead.getString(1);
                //if (rsHead.next())
                //	arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(0)))) {
                        formatoSubTotal.valCelda[0][1] = "SUBTOTAL " + ctaAnterior;
                        formatoSubTotal.print(sheet, nrengXLS++);
                        //formatoResumen.totResumen(formatoSubTotal.getValCelda());
                        //formatoResumen.print(sheet,nrengXLS);
                        formatoSubTotal.reinicia();
                        //formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 2;
                        arrEncabezado[2][1] = (String) arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    //arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][0]));
                    formatoEncabezado.setValCelda(arrEncabezado);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        arrEncabezado[2][1] = (String) arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    formatoDetalle.print(sheet, nrengXLS);
                    formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumula(arrDet);
                    //formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    ctaAnterior = (String) arrAdecua.get(0);
                    j++;
                }
                formatoSubTotal.valCelda[0][1] = "SUBTOTAL " + ctaAnterior;
                formatoSubTotal.print(sheet, nrengXLS++);
                nrengXLS++;
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
            } else if ("CONSOLI_AREA_RESP".equalsIgnoreCase(reportType)) {
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 12;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 7);
                formatoTitulos.setArrayAgrupa(new int[][] { { 0, 0, 3, 10 }, { 1, 1, 3, 10 }, { 2, 2, 3, 10 }, { 3, 3, 3, 10 }, { 4, 4, 3, 10 }, { 5, 5, 3, 10 }, { 6, 6, 3, 10 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(2, nTotCols, sheet, 8, 7);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 1, 1, 1 }, { 0, 1, 2, 2 }, { 0, 1, 3, 3 }, { 0, 0, 4, 5 }, { 0, 0, 6, 7 }, { 0, 0, 8, 9 }, { 0, 0, 10, 11 } });
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                //ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1,nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                //formatoResumen.setArrayAgrupa(new int[][] { {0,0,1,4}, {1,1,1,4},{2,2,1,4},{3,3,1,4},{4,4,1,4},{5,5,1,4}, {0,0,5,6}, {1,1,5,6},{2,2,5,6},{3,3,5,6},{4,4,5,6},{5,5,5,6}} );
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    // Quitar agrupaciones
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    //Borrar renglones
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                if (rsHead.next())
                    arrTit[6][3] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(0)))) {
                        //formatoSubTotal.print(sheet,nrengXLS++);
                        //formatoResumen.totResumen(formatoSubTotal.getValCelda());
                        //formatoResumen.print(sheet,nrengXLS);
                        //formatoSubTotal.reinicia();
                        //formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 2;
                        //arrEncabezado[2][1] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    //arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][0]));
                    formatoEncabezado.setValCelda(arrEncabezado);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        //arrEncabezado[2][1] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    formatoDetalle.print(sheet, nrengXLS);
                    //formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumula(arrDet);
                    //formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    ctaAnterior = (String) arrAdecua.get(0);
                    j++;
                }
                //formatoSubTotal.print(sheet,nrengXLS++);
                //nrengXLS++;
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
            } else if ("CONSOLI_PROYECTO".equalsIgnoreCase(reportType)) {
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 12;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 7);
                formatoTitulos.setArrayAgrupa(new int[][] { { 0, 0, 3, 10 }, { 1, 1, 3, 10 }, { 2, 2, 3, 10 }, { 3, 3, 3, 10 }, { 4, 4, 3, 10 }, { 5, 5, 3, 10 }, { 6, 6, 3, 10 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(2, nTotCols, sheet, 8, 7);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 1, 1, 1 }, { 0, 1, 2, 2 }, { 0, 1, 3, 3 }, { 0, 0, 4, 5 }, { 0, 0, 6, 7 }, { 0, 0, 8, 9 }, { 0, 0, 10, 11 } });
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                //ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1,nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                //formatoResumen.setArrayAgrupa(new int[][] { {0,0,1,4}, {1,1,1,4},{2,2,1,4},{3,3,1,4},{4,4,1,4},{5,5,1,4}, {0,0,5,6}, {1,1,5,6},{2,2,5,6},{3,3,5,6},{4,4,5,6},{5,5,5,6}} );
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    // Quitar agrupaciones
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    //Borrar renglones
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                //if (rsHead.next())
                //	arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(0)))) {
                        //formatoSubTotal.print(sheet,nrengXLS++);
                        //formatoResumen.totResumen(formatoSubTotal.getValCelda());
                        //formatoResumen.print(sheet,nrengXLS);
                        //formatoSubTotal.reinicia();
                        //formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 2;
                        //arrEncabezado[2][1] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    //arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][0]));
                    formatoEncabezado.setValCelda(arrEncabezado);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        //arrEncabezado[2][1] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    formatoDetalle.print(sheet, nrengXLS);
                    //formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumula(arrDet);
                    //formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    ctaAnterior = (String) arrAdecua.get(0);
                    j++;
                }
                //formatoSubTotal.print(sheet,nrengXLS++);
                //nrengXLS++;
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
            } else if ("MULTI_REPORTE".equalsIgnoreCase(reportType)) {
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 68;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 7);
                formatoTitulos.setArrayAgrupa(new int[][] { { 0, 0, 2, 10 }, { 1, 1, 2, 10 }, { 2, 2, 2, 10 }, { 3, 3, 2, 10 }, { 4, 4, 2, 10 }, { 5, 5, 2, 10 }, { 6, 6, 2, 10 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(2, nTotCols, sheet, 8, 6);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 0, 38, 45 }, { 0, 0, 46, 49 }, { 0, 0, 50, 56 }, { 0, 0, 57, 60 }, { 0, 0, 61, 63 }, { 0, 0, 64, 67 } });
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                //ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1,nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                //formatoResumen.setArrayAgrupa(new int[][] { {0,0,1,4}, {1,1,1,4},{2,2,1,4},{3,3,1,4},{4,4,1,4},{5,5,1,4}, {0,0,5,6}, {1,1,5,6},{2,2,5,6},{3,3,5,6},{4,4,5,6},{5,5,5,6}} );
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    // Quitar agrupaciones
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    //Borrar renglones
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                //if (rsHead.next())
                //	arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                formatoEncabezado.print(sheet, nrengXLS);
                nrengXLS = nrengXLS + 3;
                //ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    /*					if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String)arrAdecua.get(0)))) {
						//formatoSubTotal.print(sheet,nrengXLS++);
						//formatoResumen.totResumen(formatoSubTotal.getValCelda());
						//formatoResumen.print(sheet,nrengXLS);
						//formatoSubTotal.reinicia();
						//formatoResumen.reinicia();
						nrengXLS = nrengXLS + 2;
						//arrEncabezado[2][1] = (String)arrAdecua.get(0);
						formatoEncabezado.setValCelda(arrEncabezado);
						formatoEncabezado.print(sheet,nrengXLS);
						nrengXLS = nrengXLS +3;
					}*/
                    //arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][0]));
                    /*	formatoEncabezado.setValCelda(arrEncabezado);
					if ("".equalsIgnoreCase(ctaAnterior)){
						//arrEncabezado[2][1] = (String)arrAdecua.get(0);
						formatoEncabezado.setValCelda(arrEncabezado);
						formatoEncabezado.print(sheet,nrengXLS);
						nrengXLS = nrengXLS +3;
					}*/
                    formatoDetalle.print(sheet, nrengXLS);
                    //formatoSubTotal.acumula(arrDet);
                    //formatoResumen.acumula(arrDet);
                    //formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    //ctaAnterior = (String)arrAdecua.get(0);
                    j++;
                }
                //formatoSubTotal.print(sheet,nrengXLS++);
                //nrengXLS++;
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                //CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                //int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                int rowDesde = primerDetalle;
                int rowHasta = primerDetalle - 1;
                Cell celda = null;
                Row RowDetIntegra = null;
                RowDetIntegra = sheet.getRow(primerDetalle);
                celda = RowDetIntegra.getCell(0);
                String valAnterior = "";
                valAnterior = celda.getStringCellValue();
                for (int nRow = primerDetalle; nRow < ultRow; nRow++) {
                    rowHasta++;
                    RowDetIntegra = sheet.getRow(nRow);
                    celda = RowDetIntegra.getCell(0);
                    if (!valAnterior.equalsIgnoreCase(celda.getStringCellValue())) {
                        if (rowHasta - 1 < rowDesde)
                            rowHasta = rowDesde + 1;
                        for (int numCol = 0; numCol < 36; numCol++) {
                            sheet.addMergedRegion(new // mention first row here
                            //mention last row here, it is 1 as we are doing a column wise merging
                            //mention first column of merging
                            //mention last column to include in merge
                            CellRangeAddress(rowDesde, rowHasta - 1, numCol, numCol));
                        }
                        rowDesde = rowHasta;
                        //rowHasta--;
                    }
                    valAnterior = celda.getStringCellValue();
                }
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
                String[] fields = columnasBorrar.split(",");
                if (fields.length > 0) {
                    for (int nCol = 0; nCol < fields.length; nCol++) {
                        if (!"".equalsIgnoreCase(fields[nCol])) {
                            sheet.setColumnHidden(Integer.parseInt(fields[nCol]), true);
                        }
                    }
                }
            } else if ("CONSOLI_PROYECTO_REGION".equalsIgnoreCase(reportType)) {
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 12;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 7);
                formatoTitulos.setArrayAgrupa(new int[][] { { 0, 0, 3, 10 }, { 1, 1, 3, 10 }, { 2, 2, 3, 10 }, { 3, 3, 3, 10 }, { 4, 4, 3, 10 }, { 5, 5, 3, 10 }, { 6, 6, 3, 10 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(3, nTotCols, sheet, 8, 7);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 1, 1, 1 }, { 0, 1, 2, 2 }, { 0, 1, 3, 3 }, { 0, 0, 4, 5 }, { 0, 0, 6, 7 }, { 0, 0, 8, 9 }, { 0, 0, 10, 11 } });
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1, nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                formatoResumen.setArrayAgrupa(new int[][] { { 0, 0, 1, 4 }, { 1, 1, 1, 4 }, { 2, 2, 1, 4 }, { 3, 3, 1, 4 }, { 4, 4, 1, 4 }, { 5, 5, 1, 4 }, { 0, 0, 5, 6 }, { 1, 1, 5, 6 }, { 2, 2, 5, 6 }, { 3, 3, 5, 6 }, { 4, 4, 5, 6 }, { 5, 5, 5, 6 } });
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    // Quitar agrupaciones
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    //Borrar renglones
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                //if (rsHead.next())
                //	arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    String[][] arrSubTotal = formatoSubTotal.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(0)))) {
                        arrSubTotal[0][1] = ctaAnterior;
                        formatoSubTotal.setValCelda(arrSubTotal);
                        formatoSubTotal.print(sheet, nrengXLS++);
                        //formatoResumen.totResumen(formatoSubTotal.getValCelda());
                        //formatoResumen.print(sheet,nrengXLS);
                        formatoSubTotal.reinicia();
                        //formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 2;
                        arrEncabezado[2][1] = (String) arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    //arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][0]));
                    formatoEncabezado.setValCelda(arrEncabezado);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        arrEncabezado[2][1] = (String) arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    formatoDetalle.print(sheet, nrengXLS);
                    formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumula(arrDet);
                    formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    ctaAnterior = (String) arrAdecua.get(0);
                    j++;
                }
                formatoSubTotal.print(sheet, nrengXLS++);
                nrengXLS++;
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
            } else if ("CONSOLI_POR_AREA_EJECUTORA".equalsIgnoreCase(reportType)) {
                //posicion en donde se dibujara el  primer detalle
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 10;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 0);
                //formatoTitulos.setArrayAgrupa(new int[][] {{0,0,3,10},{1,1,3,10},{2,2,3,10},{3,3,3,10},{4,4,3,10},{5,5,3,10},{6,6,3,10}} );
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(3, nTotCols, sheet, 8, 0);
                //formatoEncabezado.setArrayAgrupa(new int[][] {{0,1,1,1},{0,1,2,2},{0,1,3,3},{0,0,4,5},{0,0,6,7},{0,0,8,9},{0,0,10,11}}			);
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoTotalTipoArea = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    // Quitar agrupaciones
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    //Borrar renglones
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                //if (rsHead.next())
                //	arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                String grupoTitulo = "";
                String[][] arrTotalTipoArea = formatoTotalTipoArea.getValCelda();
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    //Ver arrEncabezado
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(1)))) {
                        formatoResumen.print(sheet, nrengXLS++);
                        formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 3;
                        arrEncabezado[2][2] = (String) arrAdecua.get(1);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS++);
                        nrengXLS = nrengXLS + 2;
                    }
                    if (!"".equalsIgnoreCase(grupoTitulo) && !(grupoTitulo.equalsIgnoreCase((String) arrAdecua.get(0)))) {
                        formatoResumen.print(sheet, nrengXLS++);
                        formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 3;
                        arrEncabezado[2][2] = (String) arrAdecua.get(1);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoTotalTipoArea.print(sheet, nrengXLS++);
                        //  Agregaríamos un salto de página
                        //arrTotalTipoArea[4][4] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoTitulos.print(sheet, nrengXLS++);
                        nrengXLS = nrengXLS + 10;
                        formatoEncabezado.print(sheet, nrengXLS++);
                        nrengXLS = nrengXLS + 2;
                        formatoTotalTipoArea.reinicia();
                    }
                    //arrTotalTipoArea[4][4] = (String)arrAdecua.get(0);
                    formatoEncabezado.setValCelda(arrEncabezado);
                    formatoTotalTipoArea.setValCelda(arrTotalTipoArea);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        //Cuando el encabezado entra por primera vez
                        arrEncabezado[2][2] = (String) arrAdecua.get(1);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS++);
                        nrengXLS++;
                    }
                    if ("".equalsIgnoreCase(grupoTitulo)) {
                        //Cuando el encabezado entra por primera vez
                        arrEncabezado[2][2] = (String) arrAdecua.get(1);
                        formatoTotalTipoArea.setValCelda(arrTotalTipoArea);
                        formatoTotalTipoArea.print(sheet, nrengXLS++);
                        nrengXLS++;
                    }
                    formatoDetalle.print(sheet, nrengXLS++);
                    //formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumula(arrDet);
                    formatoTotalTipoArea.acumula(arrDet);
                    //formatoResumen.acumResumen(arrDet);
                    ctaAnterior = (String) arrAdecua.get(1);
                    grupoTitulo = (String) arrAdecua.get(0);
                    j++;
                }
                //formatoSubTotal.print(sheet,nrengXLS++);
                //nrengXLS++;
                formatoResumen.print(sheet, nrengXLS++);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
                //fin de Reporte consolidado por Area Ejecutora
            } else if ("CONSOLI_POR_AREA_EJECUTORA_y_PROYECTO".equalsIgnoreCase(reportType)) {
                primerDetalle = 12;
                nrengXLS = primerDetalle;
                int nTotCols = 12;
                ReporteF10ExcelBean formatoTitulos = new ReporteF10ExcelBean(8, nTotCols, sheet, 0, 7);
                formatoTitulos.setArrayAgrupa(new int[][] { { 0, 0, 3, 10 }, { 1, 1, 3, 10 }, { 2, 2, 3, 10 }, { 3, 3, 3, 10 }, { 4, 4, 3, 10 }, { 5, 5, 3, 10 }, { 6, 6, 3, 10 } });
                ReporteF10ExcelBean formatoEncabezado = new ReporteF10ExcelBean(2, nTotCols, sheet, 8, 7);
                formatoEncabezado.setArrayAgrupa(new int[][] { { 0, 1, 1, 1 }, { 0, 1, 2, 2 }, { 0, 1, 3, 3 }, { 0, 0, 4, 5 }, { 0, 0, 6, 7 }, { 0, 0, 8, 9 }, { 0, 0, 10, 11 } });
                ReporteF10ExcelBean formatoDetalle = new ReporteF10ExcelBean(1, nTotCols, sheet, 12, 0);
                //ReporteF10ExcelBean formatoSubTotal = new ReporteF10ExcelBean(1,nTotCols, sheet, 14, 0);
                ReporteF10ExcelBean formatoResumen = new ReporteF10ExcelBean(1, nTotCols, sheet, 16, 0);
                //formatoResumen.setArrayAgrupa(new int[][] { {0,0,1,4}, {1,1,1,4},{2,2,1,4},{3,3,1,4},{4,4,1,4},{5,5,1,4}, {0,0,5,6}, {1,1,5,6},{2,2,5,6},{3,3,5,6},{4,4,5,6},{5,5,5,6}} );
                int numRenglones = sheet.getLastRowNum() + 1;
                int numAgrup = sheet.getNumMergedRegions();
                int agrupBorrar = numAgrup;
                while (agrupBorrar > 0) {
                    // Quitar agrupaciones
                    sheet.removeMergedRegion(agrupBorrar);
                    agrupBorrar--;
                }
                for (int regBorrar = numRenglones; regBorrar >= 5; regBorrar--) {
                    //Borrar renglones
                    try {
                        sheet.removeRow(sheet.getRow(regBorrar));
                    } catch (Exception hazNada) {
                    }
                }
                nrengXLS = 9;
                String[][] arrTit = formatoTitulos.getValCelda();
                //if (rsHead.next())
                //	arrTit[3][0] = rsHead.getString(1);
                formatoTitulos.setValCelda(arrTit);
                formatoTitulos.print(sheet, 0);
                ctaAnterior = "";
                while (j < arrDetalle.size()) {
                    ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                    String[][] arrDet = formatoDetalle.getValCelda();
                    String[][] arrEncabezado = formatoEncabezado.getValCelda();
                    for (int iCol = 0; iCol < formatoDetalle.getNumCol(); iCol++) {
                        arrDet[0][iCol] = (String) arrAdecua.get(iCol);
                    }
                    formatoDetalle.setValCelda(arrDet);
                    if (!"".equalsIgnoreCase(ctaAnterior) && !(ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(0)))) {
                        //formatoSubTotal.print(sheet,nrengXLS++);
                        //formatoResumen.totResumen(formatoSubTotal.getValCelda());
                        //formatoResumen.print(sheet,nrengXLS);
                        //formatoSubTotal.reinicia();
                        //formatoResumen.reinicia();
                        nrengXLS = nrengXLS + 2;
                        //arrEncabezado[2][1] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    //arrEncabezado[0][0] = new String("Nombre del ente publico:" + getNombreUR(conn, arrDet[0][0]));
                    formatoEncabezado.setValCelda(arrEncabezado);
                    if ("".equalsIgnoreCase(ctaAnterior)) {
                        //arrEncabezado[2][1] = (String)arrAdecua.get(0);
                        formatoEncabezado.setValCelda(arrEncabezado);
                        formatoEncabezado.print(sheet, nrengXLS);
                        nrengXLS = nrengXLS + 3;
                    }
                    formatoDetalle.print(sheet, nrengXLS);
                    //formatoSubTotal.acumula(arrDet);
                    formatoResumen.acumula(arrDet);
                    //formatoResumen.acumResumen(arrDet);
                    nrengXLS++;
                    ctaAnterior = (String) arrAdecua.get(0);
                    j++;
                }
                //formatoSubTotal.print(sheet,nrengXLS++);
                //nrengXLS++;
                formatoResumen.print(sheet, nrengXLS);
                j = 5;
                laCelda = null;
                CellStyle cellStyle6 = null;
                numberOfColumns = (Integer) arrDetalle.get(0);
                arrColTipo = null;
                arrColTipo = new int[numberOfColumns];
                arrColTipo = (int[]) arrDetalle.get(2);
                int colEspacio = -1;
                ultRow = sheet.getLastRowNum();
                CellStyle[] arrStyleDet = new CellStyle[numberOfColumns];
                //fin de Reporte consolidado por Area Ejecutora  y proyecto
            }
        }
        nrengXLS++;
        i = 1;
        j = 3;
        try {
            while (j <= ultRow) {
                Row RowDetIntegra = sheet.getRow(j);
                i = 1;
                while (i < numberOfColumns) {
                    laCelda = RowDetIntegra.getCell(i);
                    if (laCelda != null) {
                        CellType eltipo = laCelda.getCellType();
                        if (eltipo == CellType.FORMULA) {
                            // 2 = formula, 0 = numerico, 1 = string
                            laCelda.setCellFormula(laCelda.getCellFormula());
                            //						String algo = String.valueOf(laCelda.);
                            //						String nada = algo;
                        }
                    }
                    i++;
                }
                j++;
            }
        } catch (Exception hazNada) {
            log.info("Object: {}", "En recalculando fórmulas i = '" + i + "', j = '" + j + "'");
        }
    }
}
