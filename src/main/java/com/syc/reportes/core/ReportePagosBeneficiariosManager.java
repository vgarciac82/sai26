package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.util.Util;
import com.syc.reportes.ReportePagosBeneficiariosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReportePagosBeneficiariosManager {

    private static final Map<String, String> camposConsulta = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(ReportePagosBeneficiariosBusinessLogic.class);

    static {
        camposConsulta.put("presupuestal", "SELECT cTipoPago, nFolioPago, fAplicacion, cxp, tipoPoliza, nFolioPoliza, descPoliza, pagosBen.rfc, nombre, CBEN, ur, ep, importe, retencion, estatus , fechaEjercido, fechaPagado, contrato, c_folio, usuario, CTAB, dBanco, clcNomina FROM dbo.v_pagosBeneficiarios2 as pagosBen with(Nolock) ");
        camposConsulta.put("conFactura", "SELECT ContratoCNet, Fformalizacion, Estado, nombre, cfactura, fechaEjercido , fechaPagado , mImporteconiva , descPoliza , mImporteBruto, iva, ivaretenido, IsrRetenido , contrato , cTipoPago , pagosBen.rfc, docAplicado , folio , nFolioPago , cxp , ep , tipoPoliza , nFolioPoliza , faplicacion , ur , estatus , cc, CTAB, dBanco FROM dbo.v_pagosBeneficiarios3 as pagosBen with(Nolock) ");
        camposConsulta.put("todos", "SELECT cTipoPago , nFolioPago , fAplicacion, cxp , tipoPoliza , nFolioPoliza, descPoliza , pagosBen.rfc , nombre , CBEN , ur , ep , importe , retencion , estatus  , fechaEjercido , fechaPagado , contrato, folio, usuario, CTAB, dBanco FROM dbo.v_pagosBeneficiarios_Todos as pagosBen with(Nolock) ");
        camposConsulta.put("conBancos", "SELECT * from v_pagosConDatosBancarios with(Nolock) ");
    }

    //private static Logger log = Logger.getLogger(ReportePagosBeneficiariosManager.class);
    public static String ReporteManager(Connection conn, String fechaInicio, String fechaFin, String cContable, String ep, String rfc, String cRazonSocial, String cContrato, String estatus, String conFact, String cUR, String usuario, String ctaBanco, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cc = "", cEp = "", cRfc = "", contrato = "", cEstatus = "";
        String razonSocial = "", fini = "", ffin = "", usu = "";
        if (!cContable.equals("*"))
            cc = cContable;
        if (!"".equals(StringUtils.trimToEmpty(cRazonSocial)))
            razonSocial = cRazonSocial.trim();
        if (!"".equals(StringUtils.trimToEmpty(ep)))
            cEp = ep;
        if (!"".equals(StringUtils.trimToEmpty(rfc)))
            cRfc = rfc;
        if (!"".equals(StringUtils.trimToEmpty(cContrato)))
            contrato = cContrato;
        if (!"00".equals(StringUtils.trimToEmpty(estatus)))
            cEstatus = estatus;
        if (!"".equals(StringUtils.trimToEmpty(usuario)))
            usu = usuario;
        if (!"".equals(StringUtils.trimToEmpty(fechaInicio)) || !"".equals(StringUtils.trimToEmpty(fechaFin))) {
            fini = fechaInicio;
            ffin = fechaFin;
        }
        if ("".equals(StringUtils.trimToEmpty(ctaBanco)) || ctaBanco == null || ctaBanco.isEmpty())
            ctaBanco = "";
        else
            ctaBanco = ctaBanco.trim();
        String fileName = "";
        String query = camposConsulta.get(conFact);
        query += "WHERE 1 = 1" + "	AND fAplicacion between cast('" + fini + "' as date) AND  cast('" + ffin + "' AS date)	";
        if (!"".equals(cc))
            query += " AND cc LIKE '" + cc + "' ";
        if (!"".equals(razonSocial))
            query += " AND nombre LIKE '%" + razonSocial + "%'";
        if (!"".equals(cEp))
            query += "	AND ep LIKE '%" + cEp + "%' ";
        if (!"".equals(cRfc))
            query += "AND rfc LIKE '%" + cRfc + "%'";
        if (!"".equals(contrato))
            query += "	AND contrato LIKE '%" + contrato + "%' ";
        if (!"".equals(cEstatus))
            query += " AND estatus  LIKE '%" + cEstatus + "%' ";
        if (!"*".equals(cUR))
            query += "	AND ur LIKE '%" + cUR + "%'";
        if (!"".equals(usu))
            query += "	AND usuario LIKE '%" + usu + "%' ";
        if (!"".equals(ctaBanco)) {
            if (conFact.equals("conBancos")) {
                query += "	AND CuentaBanco LIKE '%" + ctaBanco + "%' ";
            } else {
                query += "	AND CTAB LIKE '%" + ctaBanco + "%' ";
            }
        }
        //query += " ORDER BY fAplicacion ";
        log.info("----------------Preparando la consulta para enviarla ------------------");
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        log.info("Object: {}", query.toString());
        try {
            if (conFact.equals("presupuestal") || (conFact.equals("todos"))) {
                fileName = generaReporte(rs, plantillas.get("PAGOSBENEFICIARIOS"), fechaInicio, fechaFin);
            } else if (conFact.equals("conBancos")) {
                fileName = generaReporte(rs, plantillas.get("PAGOSBENEFICIARIOSBANCO"), fechaInicio, fechaFin);
            } else {
                fileName = generaReporte(rs, plantillas.get("PAGOSBENEFICIARIOSCF"), fechaInicio, fechaFin);
            }
            return fileName;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    private static String generaReporte(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReportePagosBeneficiarios" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        String periodo = "";
        XSSFRow rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        XSSFCell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        XSSFCellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
