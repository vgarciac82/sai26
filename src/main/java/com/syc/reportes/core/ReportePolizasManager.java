package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportePolizasManager {

    private static final Logger log = LoggerFactory.getLogger(ReportePolizasManager.class);

    public static String generaReportePolizasManager(Connection conn, String fechaInicio, String fechaFin, String cContable, String tPoliza, String tipoReporte, String ur, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_l_repPolizas( ?, ?, ?, ?, ?, ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, cContable);
            cs.setString(4, tPoliza);
            cs.setString(5, tipoReporte);
            cs.setString(6, ur);
            rs = cs.executeQuery();
            fileName = generaReportePolizas(rs, plantillas.get("REPPOLIZAS"), fechaInicio, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReportePolizas(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReportePolizas" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
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
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(0, 4);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
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
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteREP(Connection conn, String unidad, String rfc, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT cTipoPago, nFolioPago, caNoContrarrecibo,cUnidadEjecutora,FechaPagado,FacturaUUID,RFC,RazonSocial,MontoFactura,MontoTotalREP	,MontoSaldoAnterior	,MontoPagado	,MontoInsoluto	,nComprobado	,mPendienteComprobar	,conceptoPago " + " FROM v_tReciboElectronico_PagoFactura " + " WHERE RFC like '" + rfc + "' AND cUnidadEjecutora like '" + unidad + "'";
        String fileName = "";
        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            log.debug("Object: {}", query.toString());
            fileName = generaReporteREPExcel(rs, plantillas.get("EXTLISTADO"), unidad);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteREPExcel(ResultSet rs, String plantillaPath, String unidad) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + "ReporteREP" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(unidad);
        CellStyle estiloTabla = workbook.createCellStyle();
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
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteBoletosAdjuntos(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT  nFolioRelacionGastos, RFC, cConcepto, cUnidadResponsable, fAplicacion, cEsFirmaElectronica, Documento, cnombre , NOMBRE_DOCUMENTO " + " FROM v_solicitudes_con_boleto";
        String fileName = "";
        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            log.debug("Object: {}", query.toString());
            fileName = generaReporteBoletosExcel(rs, plantillas.get("EXTBOLETOSCOMP"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    public static String generaReporteBoletosPendientes(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT nFolioComision, RFC, cInformeComision, cUnidadResponsable, faplicacion, cEsFirmaElectronica,  Documento, cBoleto, cRuta, mImporteBoleto, nombreCompleto " + " FROM v_solicitudes_pendientes_boleto ";
        String fileName = "";
        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            log.debug("Object: {}", query.toString());
            fileName = generaReporteBoletosExcel(rs, plantillas.get("EXTBOLETOS"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteBoletosExcel(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + "ReporteBoletos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setDataFormat(df.getFormat("dd/MM/yyyy"));
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (i == 4) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloFecha);
                }
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static boolean borraTabla(Connection conn) throws Exception {
        boolean borrado = false;
        PreparedStatement pstmnt = null;
        try {
            String querySelect = "DELETE CFDI_SAT";
            pstmnt = conn.prepareStatement(querySelect);
            pstmnt.executeUpdate();
            borrado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return borrado;
    }

    public static void insertarDatosCfdi(Connection conn, List<String[]> renglonesArchivo) throws Exception {
        PreparedStatement ps = null;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO cfdi_sat (");
        sql.append("CFDI, Efecto, UUID, Serie, Folio, Emision, Hora_emision, Timbrado, Hora_timbrado, Estatus, ");
        sql.append("Forma, Metodo, Condiciones_pago, Moneda, Tipo_cambio, Exportacion, Descripcion_exportacion, Expedido, Complemento, FechaCancela, ");
        sql.append("HoraCancela, EstatusCancela, MotivoCancela, FolioSustitucion, ProcesoCancela, Periodicidad, Descripcion_periodicidad, Meses, DescripcionMeses, Año, ");
        sql.append("TipoRel, UUIDRelacionados, EmisorRFC, EmisorRazonSocial, EmisorRegimen, Descripcion, Predial, SubTotal, Descuento, Imp_trasladados, ");
        sql.append("Imp_retenidos, Imp_local_trasladados, Imp_local_retenidos, Total, ImpuestoIvaExento, TipoExento, BaseExento, ImpuestoIvaCero, TipoCero, ");
        sql.append("TasaCero, BaseIvaCero, Importe, ImpuestoIva8, TipoIva8, TasaIva8, BaseIva8, ImporteIva8, ImpuestoIva16, TipoIva16, ");
        sql.append("TasaIva16, BaseIva16, ImporteIva16, ImpuestoIeps, TipoIeps, TasaIeps, BaseIeps, ImporteIeps, ImpuestoIeps3, TipoIeps3, ");
        sql.append("TasaIeps3, BaseIeps3, ImporteIeps3, ImpuestoIEPS6, TipoIEPS6, TasaIEPS6, BaseIEPS6, ImporteIEPS6, ImpuestoIEPS7, TipoIEPS7, ");
        sql.append("TasaIEPS7, BaseIEPS7, ImporteIEPS7, ImpuestoIEPS8, TipoIEPS8, TasaIEPS8, BaseIEPS8, ImporteIEPS8, ImpuestoISR, BaseISR, ");
        sql.append("ImporteISR, ImpuestoIVA, BaseIVA, ImporteIVA, ImpuestoLocal1, TasaLocal1, ImporteLocal1, ImpuestoLocal2, TasaLocal2, ImporteRetLocal2, ");
        sql.append("ImpuestoRetLocal2, TasaRetLocal2, ImporteLocal2");
        sql.append(") VALUES (");
        for (int i = 0; i < 102; i++) {
            // 102 campos
            sql.append("?");
            if (i < 101) {
                sql.append(", ");
            }
        }
        sql.append(");");
        try {
            ps = conn.prepareStatement(sql.toString());
            int batchSize = 10;
            int count = 0;
            for (String[] item : renglonesArchivo) {
                StringBuilder sql2 = new StringBuilder();
                for (int i = 0; i < 102; i++) {
                    String valor = (i < item.length) ? item[i] : null;
                    // Campo Descripción
                    if (i == 35 && valor != null && valor.length() > 255) {
                        valor = valor.substring(0, 255);
                    } else if (i == 35) {
                        //valor = valor.replace("\"", "");
                        // reemplaza con espacio
                        // colapsa múltiples espacios
                        valor = // colapsa múltiples espacios
                        valor.replaceAll("[\\r\\n\\u00A0\\u2028\\u2029\\u200B\\t]", " ").// colapsa múltiples espacios
                        replaceAll(" +", " ").trim();
                    }
                    // PreparedStatement es 1-based
                    ps.setString(i + 1, valor);
                    sql2.append(valor + ",");
                }
                ps.addBatch();
                log.debug("Object: {}", "Agregando el CFDI:" + sql2.toString());
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                    conn.commit();
                    log.debug("Commit correcto");
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
