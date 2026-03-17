package com.syc.rendicioncuentasFONDEN;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.ws.fonden.PolizaAutomatica;
import com.syc.ws.fonden.PolizaAutomaticaDetalle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class RendicionCuentasFONDENManager {

    private static final Logger log = LoggerFactory.getLogger(RendicionCuentasFONDENManager.class);

    public static String rendicionCuentasFONDENManager(Connection conn, String fecha, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando calculo de póliza previa"));
        CallableStatement cs = null;
        ResultSet rs = null;
        int mes = Integer.parseInt(fecha.substring(3, 5));
        int ejFiscal = Integer.parseInt(fecha.substring(6, 10));
        String query = "{call sp_rendicionCuentasFONDENPrevia( ? )}";
        String fileName = "";
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mes);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mes);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[" + query + "]%S", mes)));
            rs = cs.executeQuery();
            fileName = generaReporteMasivo(rs, plantillas.get("VISTAPREVIA"), mes, ejFiscal);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static int obtenerUltimoDiaMes(int anio, int mes) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(anio, mes - 1, 1);
        return calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private static String generaReporteMasivo(ResultSet rs, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = "VistaPreviaPoliza" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 3;
        String periodo = "";
        periodo = "Mes: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc2 = (sheet0.getRow(0) == null ? sheet0.createRow(0) : sheet0.getRow(0));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
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
        bos.flush();
        bos.close();
        fos.close();
        workbook.close();
        return file_name;
    }

    public static PolizaAutomatica generaEncabezadoPolizaFONDEN(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_poliza_FONDEN(?,?,?)}";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fAplicacion);
            cs.setString(2, uLogin);
            cs.setString(3, strUE);
            rs = cs.executeQuery();
            PolizaAutomatica encabezado = new PolizaAutomatica();
            if (rs.next()) {
                // encabezado.setfAplicacion(fAplicacion);
                encabezado.setUnidadEjecutora(strUE);
                encabezado.setTipoPoliza("DI");
                encabezado.setMesCierre(rs.getInt("mes"));
                encabezado.setDescripcion(rs.getString("cDescripcionPoliza"));
                encabezado.setTotalPoliza(rs.getBigDecimal("mtotalAbonos"));
                encabezado.setCentroContable(cCentroContable);
            } else {
                throw new Exception("No hay movimientos en el mes de aplicacion. ");
            }
            return encabezado;
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rs);
        }
    }

    public static List<PolizaAutomaticaDetalle> generaDetallePolizaFONDEN(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre) throws Exception {
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        String query2 = "{call sp_poliza_FONDEN_Detalle(?,?,?)}";
        List<PolizaAutomaticaDetalle> detalle = new ArrayList<PolizaAutomaticaDetalle>();
        try {
            cs2 = conn.prepareCall(query2);
            cs2.setString(1, fAplicacion);
            cs2.setString(2, uLogin);
            cs2.setString(3, strUE);
            rs2 = cs2.executeQuery();
            while (rs2.next()) {
                PolizaAutomaticaDetalle renglon = new PolizaAutomaticaDetalle();
                renglon.setCuentaContable(rs2.getString("ncuenta"));
                renglon.setSubcuenta(rs2.getString("nsubCuenta"));
                renglon.setEvento(rs2.getString("cevento"));
                renglon.setImporteMovimiento(rs2.getBigDecimal("mMovimiento"));
                detalle.add(renglon);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(cs2);
            CloseObject.closeObject(rs2);
        }
    }
}
