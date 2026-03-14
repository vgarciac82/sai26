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
import java.util.ArrayList;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteRetencionesManager {

    public static final Logger log = LoggerFactory.getLogger(ReporteRetencionesManager.class);

    public static String generaReporteRetenciones(Connection conn, String fechaInicio, String fechaFin, int tipoAjena, String centroContable, String ur, Map<String, String> plantillas, String conEP) throws Exception {
        CallableStatement cs = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = null;
        if ("S".equals(conEP))
            query = "{call sp_a_reporteRetencionesEP( ?, ?, ?, ?, ? )}";
        else
            query = "{call sp_a_reporteRetenciones( ?, ?, ?, ? , ?)}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        String fileName = "";
        String mensaje = "";
        String nom_Retencion = "";
        try {
            //Se establecen parametros para la ejecucion de los stores
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setInt(3, tipoAjena);
            cs.setString(4, centroContable);
            cs.setString(5, ur);
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                mensaje = rs2.getString("cDescripcion");
            }
            if (4 == tipoAjena)
                nom_Retencion = "RETENCION 5 AL MILLAR";
            else if (6 == tipoAjena)
                nom_Retencion = "RETENCION CEDULAR";
            //Se generan los archivos en Excel
            if (1 == tipoAjena)
                fileName = generaReporteTESOFE1(rs, plantillas.get("TESOFE1"), fechaInicio, fechaFin, centroContable, mensaje, conEP);
            else if (4 == tipoAjena || 6 == tipoAjena)
                fileName = generaReporteTESOFE2_Cedular(rs, plantillas.get("TESOFE46"), fechaInicio, fechaFin, centroContable, mensaje, conEP, nom_Retencion);
            else if (7 == tipoAjena)
                fileName = generaReporteTESOFE1_IVA(rs, plantillas.get("TESOFE7"), fechaInicio, fechaFin, centroContable, mensaje);
            else if (8 == tipoAjena)
                fileName = generaReporteTESOFE1_ISR(rs, plantillas.get("TESOFE8"), fechaInicio, fechaFin, centroContable, mensaje);
            else if (9 == tipoAjena)
                fileName = generaReporteLAUDOS_ISR(rs, plantillas.get("LAUDOS"), fechaInicio, fechaFin, centroContable, mensaje, conEP);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String generaReporteRetencionesCC(Connection conn, String fechaInicio, String fechaFin, int tipoAjena, String centroContable, String ur, Map<String, String> plantillas) throws Exception {
        //Se genera reporte de Ajenas por Cordinacion
        CallableStatement cs = null;
        ResultSet rs = null, rs2 = null;
        String query = null;
        PreparedStatement ps = null;
        if (1 == tipoAjena) {
            //Tipo Ajenas IVA, ISR TESOFE 1
            query = "{call sp_reporteRetencionesEP_coord( ?, ?, ?, ?, ? , ?)}";
        } else if (4 == tipoAjena || 6 == tipoAjena) {
            //Tipo Ajenas Arrendamientos, Cedular.. Tesofe2
            query = "{call sp_reporteRetencionesEP_coord46( ?, ?, ?, ?, ? , ?)}";
        } else if (7 == tipoAjena || 8 == tipoAjena) {
            //Tipo Ajenas 7 IVA y 8 ISR
            query = "{call sp_reporteRetencionesEP_coord78( ?, ?, ?, ?, ? , ?)}";
        } else if (9 == tipoAjena) {
            //Tipo de Ajenas Laudos
            query = "{call sp_reporteRetencionesEP_coord9( ?, ?, ?, ?, ? , ?)}";
        }
        String fileName = "";
        try {
            ArrayList<String> nombres = new ArrayList<String>();
            ps = conn.prepareStatement("select nCoord, dCordinacion from tcoordinacion (NOLOCK)");
            rs2 = ps.executeQuery();
            int i = 0;
            while (rs2.next()) {
                nombres.add(i, rs2.getString(2));
                i++;
            }
            ArrayList<ResultSet> resultsets = new ArrayList<ResultSet>();
            for (i = 1; i <= nombres.size(); i++) {
                cs = conn.prepareCall(query);
                cs.setString(1, fechaInicio);
                cs.setString(2, fechaFin);
                cs.setInt(3, tipoAjena);
                cs.setString(4, centroContable);
                cs.setString(5, ur);
                cs.setInt(6, i);
                rs = cs.executeQuery();
                resultsets.add(rs);
            }
            //Se envian los resultSet y los nombres por cada hoja
            if (1 == tipoAjena)
                fileName = generaReporteTESOFE1CC(resultsets, plantillas.get("TESOFE1CC"), fechaInicio, fechaFin, nombres, tipoAjena);
            else if (7 == tipoAjena || 8 == tipoAjena)
                fileName = generaReporteTESOFE1CC(resultsets, plantillas.get("TESOFE7CC"), fechaInicio, fechaFin, nombres, tipoAjena);
            else if (4 == tipoAjena || 6 == tipoAjena)
                fileName = generaReporteTESOFE1CC(resultsets, plantillas.get("TESOFE46CC"), fechaInicio, fechaFin, nombres, tipoAjena);
            else if (9 == tipoAjena)
                fileName = generaReporteTESOFE1CC(resultsets, plantillas.get("TESOFE9CC"), fechaInicio, fechaFin, nombres, tipoAjena);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
        }
    }

    private static String generaReporteTESOFE1(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje, String conEP) throws Exception {
        //Genera reporte Tesofe 1
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        //Se agrega la columna con EP
        if ("S".equals(conEP)) {
            Row rwEnc3 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell cell3 = (rwEnc3.getCell(21) == null ? rwEnc3.createCell(21) : rwEnc3.getCell(21));
            cell3.setCellValue("EP");
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 2), (renglonInicio + cnt + 8), 12, 20);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteTESOFE1CC(ArrayList<ResultSet> resultsets, String plantillaPath, String fechaInicio, String fechaFin, ArrayList<String> nombres, int tipoAjena) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        //Se copia la plantilla en un nuevo documento
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        for (int i = 0; i < nombres.size(); i++) {
            Sheet sheet0 = workbook.getSheetAt(i);
            int renglonInicio = 8, mesIni = 0, mesFin = 0;
            String anio = "", periodo = "";
            mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
            mesFin = Integer.parseInt(fechaFin.substring(3, 5));
            anio = fechaInicio.substring(6, 10);
            if (mesIni == mesFin)
                periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
            else if (mesIni != mesFin)
                periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
            //Datos hoja 1
            Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
            Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
            cell.setCellValue(nombres.get(i));
            Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
            Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
            cell2.setCellValue(periodo);
            //	Inserta el detalle en la hoja
            recorreResultset(workbook, sheet0, renglonInicio, resultsets.get(i), tipoAjena);
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static void recorreResultset(Workbook workbook, Sheet sheet, int renglonInicio, ResultSet rs, int tipoAjena) throws Exception {
        //Funcion para recorrer los resultSet del reporte de OA por coordinacion
        int cnt = 0;
        int renglonIntermedio = 0, renglon = 0;
        String ur = "";
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ArrayList<Integer> celdasTotales = new ArrayList<Integer>();
        //Estilo de celdas que se van a usar
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 10, false, true, true, true, true, false);
        CellStyle estiloMoney = Util.generaEstilo2(workbook, 10, false, true, true, true, true, true);
        CellStyle estiloFecha = Util.generaEstilo2(workbook, 10, false, true, true, true, true, false);
        CellStyle estiloTitulo = Util.generaEstilo(workbook, 11, true, true, true, true, true);
        estiloTitulo.setBorderLeft(BorderStyle.THICK);
        estiloTitulo.setBorderRight(BorderStyle.THICK);
        estiloTitulo.setBorderTop(BorderStyle.THICK);
        estiloTitulo.setBorderBottom(BorderStyle.THICK);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        CellStyle estiloTot = Util.generaEstiloBordesAnchos(workbook, 11, true, true, true, true, true);
        estiloTot.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estiloTot.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloTot.setFont(font);
        while (rs.next()) {
            renglon = renglonInicio + cnt;
            if (renglon < sheet.getLastRowNum())
                sheet.shiftRows(renglon, sheet.getLastRowNum(), 1);
            Row rw = (sheet.getRow(renglonInicio + cnt) == null ? sheet.createRow(renglonInicio + cnt) : sheet.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (i == 1)
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloFecha);
                else if (i <= 20 && i >= 11)
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                else {
                    if (i == 0) {
                        Cell cell20 = Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                        if (renglon == renglonInicio) {
                            //Se busca el primer registro para tener la Coordinación inicial
                            Row rw2 = (sheet.getRow(renglonInicio + cnt) == null ? sheet.createRow(renglonInicio + cnt) : sheet.getRow(renglonInicio + cnt));
                            Cell cell22 = (rw2.getCell(0) == null ? rw2.createCell(0) : rw2.getCell(0));
                            ur = cell22.getStringCellValue();
                            //urIni = ur;
                        }
                        if (!ur.equals(cell20.getStringCellValue())) {
                            //Se insertan los subtotales
                            insertaTotales(sheet, workbook, renglon, renglonInicio, renglonIntermedio + 1, cnt, ur, tipoAjena);
                            cnt = cnt + 2;
                            //Se copia el encabezado y se pega despues
                            Util.copyRow(workbook, sheet, 7, renglonInicio + cnt + 1);
                            //Guardar las celdas donde se inserta un total
                            celdasTotales.add(renglonInicio + cnt);
                            //Se asigna la nueva UR con la que se comparara las siguientes veces
                            ur = cell20.getStringCellValue();
                            cell20.setBlank();
                            cnt = cnt + 2;
                            //Se aumenta el renglon para que se ponga el detalle 4 renglones despues de la sig coordinación
                            rw.setRowNum(renglonInicio + cnt);
                            renglonIntermedio = rw.getRowNum();
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                        }
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
        }
        if (rs.isAfterLast()) {
            if (renglon < renglonIntermedio) {
                insertaTotales(sheet, workbook, renglonIntermedio + 1, renglonInicio, renglonIntermedio + 1, cnt, ur, tipoAjena);
                //Guardar las celdas donde se inserta un total
                celdasTotales.add(renglonInicio + cnt + 2);
            } else {
                insertaTotales(sheet, workbook, renglon + 1, renglonInicio, renglonIntermedio + 1, cnt, ur, tipoAjena);
                //Guardar las celdas donde se inserta un total
                celdasTotales.add(renglonInicio + cnt + 2);
            }
        }
        //Genera totales generales
        renglon = renglonInicio + cnt + 3;
        Row rw = (sheet.getRow(renglon) == null ? sheet.createRow(renglon) : sheet.getRow(renglon));
        Cell contentCell = (rw.getCell(9) == null ? rw.createCell(9) : rw.getCell(9));
        contentCell.setCellValue((String) "TOTAL GENERAL ");
        String formula = "";
        if (celdasTotales.size() > 0) {
            for (int i = 0; i < celdasTotales.size(); i++) {
                if (i > 0)
                    formula = formula + " + L" + celdasTotales.get(i);
                else
                    formula = "L" + celdasTotales.get(i);
            }
            if (tipoAjena == 9) {
                renglon = renglonInicio + cnt + 3;
                Row contentRow = (sheet.getRow(renglon) == null ? sheet.createRow(renglon) : sheet.getRow(renglon));
                Cell Cell11 = (contentRow.getCell(11) == null ? contentRow.createCell(11) : contentRow.getCell(11));
                Cell11.setCellFormula("(" + formula + ")");
                Cell11.setCellStyle(estiloTot);
            } else {
                sheet.addMergedRegion(new CellRangeAddress(rw.getRowNum(), rw.getRowNum(), 9, 10));
                contentCell.setCellStyle(estiloTot);
                renglon = renglonInicio + cnt + 3;
                Row contentRow = (sheet.getRow(renglon) == null ? sheet.createRow(renglon) : sheet.getRow(renglon));
                Cell Cell11 = (contentRow.getCell(11) == null ? contentRow.createCell(11) : contentRow.getCell(11));
                Cell11.setCellFormula("(" + formula + ")");
                Cell11.setCellStyle(estiloTot);
                String newFormula = reemplazar(formula, "L", "M");
                Cell Cell12 = (contentRow.getCell(12) == null ? contentRow.createCell(12) : contentRow.getCell(12));
                Cell12.setCellFormula("(" + newFormula + ")");
                Cell12.setCellStyle(estiloTot);
                String newFormula2 = reemplazar(formula, "L", "N");
                Cell Cell13 = (contentRow.getCell(13) == null ? contentRow.createCell(13) : contentRow.getCell(13));
                Cell13.setCellFormula("(" + newFormula2 + ")");
                Cell13.setCellStyle(estiloTot);
                String newFormula3 = reemplazar(formula, "L", "O");
                Cell Cell14 = (contentRow.getCell(14) == null ? contentRow.createCell(14) : contentRow.getCell(14));
                Cell14.setCellFormula("(" + newFormula3 + ")");
                Cell14.setCellStyle(estiloTot);
                String newFormula4 = reemplazar(formula, "L", "P");
                Cell Cell15 = (contentRow.getCell(15) == null ? contentRow.createCell(15) : contentRow.getCell(15));
                Cell15.setCellFormula("(" + newFormula4 + ")");
                Cell15.setCellStyle(estiloTot);
                String newFormula5 = reemplazar(formula, "L", "Q");
                Cell Cell16 = (contentRow.getCell(16) == null ? contentRow.createCell(16) : contentRow.getCell(16));
                Cell16.setCellFormula("(" + newFormula5 + ")");
                Cell16.setCellStyle(estiloTot);
                String newFormula6 = reemplazar(formula, "L", "R");
                Cell Cell17 = (contentRow.getCell(17) == null ? contentRow.createCell(17) : contentRow.getCell(17));
                Cell17.setCellFormula("(" + newFormula6 + ")");
                Cell17.setCellStyle(estiloTot);
                String newFormula7 = reemplazar(formula, "L", "S");
                Cell Cell18 = (contentRow.getCell(18) == null ? contentRow.createCell(18) : contentRow.getCell(18));
                Cell18.setCellFormula("(" + newFormula7 + ")");
                Cell18.setCellStyle(estiloTot);
            }
        }
    }

    public static String reemplazar(String cadena, String busqueda, String reemplazo) {
        return cadena.replaceAll(busqueda, reemplazo);
    }

    private static void insertaTotales(Sheet sheet0, Workbook workbook, int rows, int renglonInicio, int renglonIntermedio, int cnt, String ur, int tipoAjena) throws Exception {
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        CellStyle estiloTitulo = Util.generaEstiloBordesAnchos(workbook, 11, true, true, true, true, true);
        estiloTitulo.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloTitulo.setFont(font);
        //Se ponen el nombre del subtotal y las filas se combinan
        Row contentRow = (sheet0.getRow(rows + 1) == null ? sheet0.createRow(rows + 1) : sheet0.getRow(rows + 1));
        Cell contentCell = (contentRow.getCell(9) == null ? contentRow.createCell(9) : contentRow.getCell(9));
        contentCell.setCellValue((String) "TOTAL DE LA UNIDAD " + ur);
        if (tipoAjena == 9) {
            sheet0.addMergedRegion(new CellRangeAddress(contentRow.getRowNum(), contentRow.getRowNum(), 9, 10));
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(10) == null ? contentRow.createCell(10) : contentRow.getCell(10));
            contentCell.setCellStyle(estiloTitulo);
        } else {
            sheet0.addMergedRegion(new CellRangeAddress(contentRow.getRowNum(), contentRow.getRowNum(), 9, 10));
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(10) == null ? contentRow.createCell(10) : contentRow.getCell(10));
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(9) == null ? contentRow.createCell(9) : contentRow.getCell(9));
            contentCell.setCellStyle(estiloTitulo);
        }
        if (renglonIntermedio == 1)
            renglonIntermedio = renglonInicio + 1;
        //Totales
        if (tipoAjena == 9) {
            contentCell = (contentRow.getCell(11) == null ? contentRow.createCell(11) : contentRow.getCell(11));
            contentCell.setCellFormula("SUM(L" + (renglonIntermedio) + ":L" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
        } else {
            contentCell = (contentRow.getCell(11) == null ? contentRow.createCell(11) : contentRow.getCell(11));
            contentCell.setCellFormula("SUM(L" + (renglonIntermedio) + ":L" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(12) == null ? contentRow.createCell(12) : contentRow.getCell(12));
            contentCell.setCellFormula("SUM(M" + (renglonIntermedio) + ":M" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(13) == null ? contentRow.createCell(13) : contentRow.getCell(13));
            contentCell.setCellFormula("SUM(N" + (renglonIntermedio) + ":N" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(14) == null ? contentRow.createCell(14) : contentRow.getCell(14));
            contentCell.setCellFormula("SUM(O" + (renglonIntermedio) + ":O" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(15) == null ? contentRow.createCell(15) : contentRow.getCell(15));
            contentCell.setCellFormula("SUM(P" + (renglonIntermedio) + ":P" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
        }
        //Se agregan los totales de estas columnas en caso de Tesofe1
        if (tipoAjena == 1 || tipoAjena == 7) {
            contentCell = (contentRow.getCell(16) == null ? contentRow.createCell(16) : contentRow.getCell(16));
            contentCell.setCellFormula("SUM(Q" + (renglonIntermedio) + ":Q" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(17) == null ? contentRow.createCell(17) : contentRow.getCell(17));
            contentCell.setCellFormula("SUM(R" + (renglonIntermedio) + ":R" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
            contentCell = (contentRow.getCell(18) == null ? contentRow.createCell(18) : contentRow.getCell(18));
            contentCell.setCellFormula("SUM(S" + (renglonIntermedio) + ":S" + (renglonInicio + cnt) + ")");
            contentCell.setCellStyle(estiloTitulo);
        }
    }

    private static String generaReporteTESOFE2_Cedular(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje, String conEP, String nom_Retencion) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        if ("S".equals(conEP)) {
            Row rwEnc3 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell cell3 = (rwEnc3.getCell(16) == null ? rwEnc3.createCell(16) : rwEnc3.getCell(16));
            cell3.setCellValue("EP");
        }
        Row rwEnc4 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell4 = (rwEnc4.getCell(0) == null ? rwEnc4.createCell(0) : rwEnc4.getCell(0));
        cell4.setCellValue(nom_Retencion);
        sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 2), (renglonInicio + cnt + 8), 12, 13);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteTESOFE1_IVA(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 2), (renglonInicio + cnt + 8), 10, 20);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteTESOFE1_ISR(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 2), (renglonInicio + cnt + 8), 10, 14);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaResumenRetenciones(Connection conn, String fechaInicio, String fechaFin, int tipoAjena, String centroContable, String ur, Map<String, String> plantillasResumen) throws Exception {
        CallableStatement cs = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = "{call sp_a_resumenRetenciones( ?, ?, ?, ? , ? )}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        String fileName = "";
        String mensaje = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setInt(3, tipoAjena);
            cs.setString(4, centroContable);
            cs.setString(5, ur);
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                mensaje = rs2.getString("cDescripcion");
            }
            if (1 == tipoAjena)
                fileName = generaReporteTESOFE1R(rs, plantillasResumen.get("TESOFE1R"), fechaInicio, fechaFin, centroContable, mensaje);
            else if (4 == tipoAjena)
                fileName = generaReporteTESOFE4R(rs, plantillasResumen.get("TESOFE4R"), fechaInicio, fechaFin, centroContable, mensaje);
            else if (6 == tipoAjena)
                fileName = generaReporteTESOFE6R(rs, plantillasResumen.get("TESOFE6R"), fechaInicio, fechaFin, centroContable, mensaje);
            else if (7 == tipoAjena || 8 == tipoAjena)
                fileName = generaReporteTESOFE78R(rs, plantillasResumen.get("TESOFE78R"), fechaInicio, fechaFin, centroContable, mensaje, tipoAjena);
            else if (9 == tipoAjena)
                fileName = generaReporteLAUDOSR(rs, plantillasResumen.get("LAUDOS"), fechaInicio, fechaFin, centroContable, mensaje);
            return fileName;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteTESOFE1R(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ResumenRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        double autotransporte = 0.0d;
        double iva_arrendamiento = 0.0d;
        double isr_arrendamiento = 0.0d;
        double iva_honorarios = 0.0d;
        double isr_honorarios = 0.0d;
        double iva_6porciento = 0.0d;
        String total_letra = "";
        double total_iva = 0.0d;
        double total_isr = 0.0d;
        double total = 0.0d;
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell = (rwEnc.getCell(2) == null ? rwEnc.createCell(2) : rwEnc.getCell(2));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(periodo);
        if (rs.next()) {
            autotransporte = rs.getDouble("autotransporte");
            iva_arrendamiento = rs.getDouble("iva_arrendamiento");
            isr_arrendamiento = rs.getDouble("isr_arrendamiento");
            iva_honorarios = rs.getDouble("iva_honorarios");
            isr_honorarios = rs.getDouble("isr_honorarios");
            iva_6porciento = rs.getDouble("iva_6porciento");
            total_letra = rs.getString("total_letra");
            total_iva = autotransporte + iva_arrendamiento + iva_honorarios + iva_6porciento;
            total_isr = isr_arrendamiento + isr_honorarios;
            total = total_iva + total_isr;
            Row rwTot1 = (sheet0.getRow(14) == null ? sheet0.createRow(14) : sheet0.getRow(14));
            Cell cell3 = (rwTot1.getCell(7) == null ? rwTot1.createCell(7) : rwTot1.getCell(7));
            cell3.setCellValue(autotransporte);
            Row rwTot2 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cell4 = (rwTot2.getCell(7) == null ? rwTot2.createCell(7) : rwTot2.getCell(7));
            cell4.setCellValue(iva_arrendamiento);
            Row rwTot3 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cell5 = (rwTot3.getCell(6) == null ? rwTot3.createCell(6) : rwTot3.getCell(6));
            cell5.setCellValue(isr_arrendamiento);
            Row rwTot4 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
            Cell cell6 = (rwTot4.getCell(7) == null ? rwTot4.createCell(7) : rwTot4.getCell(7));
            cell6.setCellValue(iva_honorarios);
            Row rwTot5 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
            Cell cell7 = (rwTot5.getCell(6) == null ? rwTot5.createCell(6) : rwTot5.getCell(6));
            cell7.setCellValue(isr_honorarios);
            Row rwTot6 = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell cell8 = (rwTot6.getCell(6) == null ? rwTot6.createCell(6) : rwTot6.getCell(6));
            cell8.setCellValue(total_isr);
            Row rwTot7 = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell cell9 = (rwTot7.getCell(7) == null ? rwTot7.createCell(7) : rwTot7.getCell(7));
            cell9.setCellValue(total_iva);
            Row rwTot8 = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell cell10 = (rwTot8.getCell(8) == null ? rwTot8.createCell(8) : rwTot8.getCell(8));
            cell10.setCellValue(total);
            Row rwTot9 = (sheet0.getRow(16) == null ? sheet0.createRow(16) : sheet0.getRow(16));
            Cell cell11 = (rwTot9.getCell(7) == null ? rwTot9.createCell(7) : rwTot9.getCell(7));
            cell11.setCellValue(iva_6porciento);
        }
        Row rwNot = (sheet0.getRow(22) == null ? sheet0.createRow(22) : sheet0.getRow(22));
        Cell cell7 = (rwNot.getCell(2) == null ? rwNot.createCell(2) : rwNot.getCell(2));
        cell7.setCellValue("Bajo protesta de decir verdad, manifiesto que el Estado de " + mensaje + " a mi cargo, generó actos o actividades en el periodo " + periodo + " con fundamento en los articulos  106 quinto párrafo de la Ley del ISR 116 quinto párrafo de la ley del ISR y el artículo 1-A de la ley del IVA y el artículo 3 de su reglamento y a los conceptos de retenciones antes mencionados que los montos a pagar a la SHCP correspondientes a ISR e IVA ascienden a un total de $" + Math.round(total * Math.pow(10, 2)) / Math.pow(10, 2) + "(" + total_letra + ") Los impuestos que no se hayan enterado en tiempo, se pagarán con sus respectivos recargos y actualizaciones.");
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteTESOFE6R(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ResumenRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        double cedular = 0.0d;
        String total_letra = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell = (rwEnc.getCell(2) == null ? rwEnc.createCell(2) : rwEnc.getCell(2));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(periodo);
        if (rs.next()) {
            cedular = rs.getDouble("cedular");
            total_letra = rs.getString("total_letra");
            Row rwTot1 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
            Cell cell1 = (rwTot1.getCell(6) == null ? rwTot1.createCell(6) : rwTot1.getCell(6));
            cell1.setCellValue(cedular);
            Row rwTot2 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cell3 = (rwTot2.getCell(6) == null ? rwTot2.createCell(6) : rwTot2.getCell(6));
            cell3.setCellValue(cedular);
        }
        Row rwNot = (sheet0.getRow(16) == null ? sheet0.createRow(16) : sheet0.getRow(16));
        Cell cell7 = (rwNot.getCell(1) == null ? rwNot.createCell(1) : rwNot.getCell(1));
        cell7.setCellValue("Bajo protesta de decir verdad manifiesto que el Estado de " + mensaje + " a mi cargo generó actos o actividades en el periodo " + periodo + " por concepto de retenciones de impuestos estatal por un total de $" + Math.round(cedular * Math.pow(10, 2)) / Math.pow(10, 2) + "(" + total_letra + ") a pagar a la Secretaría de Finanzas del Estado de" + "(" + mensaje + " con fundamento en el Artículo 13 tercer párrafo y artículo 19 segundo párrafo de la Ley de Hacienda del Estado de " + mensaje + ".  Los impuestos que no se hayan enterado en tiempo, se pagaran con sus respectivos recargos y actualizaciones.");
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteLAUDOS_ISR(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje, String conEP) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        if ("S".equals(conEP)) {
            Row rwEnc3 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell cell3 = (rwEnc3.getCell(9) == null ? rwEnc3.createCell(9) : rwEnc3.getCell(9));
            cell3.setCellValue("EP");
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 2), (renglonInicio + cnt + 8), 8, 9);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteLAUDOSR(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ResumenRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        double cedular = 0.0d;
        String total_letra = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell = (rwEnc.getCell(2) == null ? rwEnc.createCell(2) : rwEnc.getCell(2));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(periodo);
        if (rs.next()) {
            cedular = rs.getDouble("isr_laudos");
            total_letra = rs.getString("total_letra");
            Row rwTot1 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
            Cell cell1 = (rwTot1.getCell(6) == null ? rwTot1.createCell(6) : rwTot1.getCell(6));
            cell1.setCellValue(cedular);
            Row rwTot2 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cell3 = (rwTot2.getCell(6) == null ? rwTot2.createCell(6) : rwTot2.getCell(6));
            cell3.setCellValue(cedular);
        }
        Row rwNot = (sheet0.getRow(16) == null ? sheet0.createRow(16) : sheet0.getRow(16));
        Cell cell7 = (rwNot.getCell(1) == null ? rwNot.createCell(1) : rwNot.getCell(1));
        cell7.setCellValue("Bajo protesta de decir verdad manifiesto que el Estado de " + mensaje + " a mi cargo generó actos o actividades en el periodo " + periodo + " por concepto de retenciones de impuestos estatal por un total de $" + Math.round(cedular * Math.pow(10, 2)) / Math.pow(10, 2) + "(" + total_letra + ") a pagar a la Secretaría de Finanzas del Estado de" + "(" + mensaje + " con fundamento en el Artículo 13 tercer párrafo y artículo 19 segundo párrafo de la Ley de Hacienda del Estado de " + mensaje + ".  Los impuestos que no se hayan enterado en tiempo, se pagaran con sus respectivos recargos y actualizaciones.");
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteRetencionesAcc(Connection conn, String fechaInicio, String fechaFin, String ur, Map<String, String> plantillasAcumulada) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ReporteRetencionesAcc( ?, ?, ?  )}";
        String fileName = "";
        String mensaje = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, ur);
            rs = cs.executeQuery();
            fileName = generaReporteAcc(rs, plantillasAcumulada.get("Acumulada"), fechaInicio, fechaFin, mensaje);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteAcc(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        sheet0 = Util.resultSetToExcel(rs, sheet0, renglonInicio, 0, false);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteTESOFE4R(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ResumenRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        double cedular = 0.0d;
        String total_letra = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell = (rwEnc.getCell(2) == null ? rwEnc.createCell(2) : rwEnc.getCell(2));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(periodo);
        if (rs.next()) {
            cedular = rs.getDouble("cedular");
            total_letra = rs.getString("total_letra");
            Row rwTot1 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
            Cell cell1 = (rwTot1.getCell(6) == null ? rwTot1.createCell(6) : rwTot1.getCell(6));
            cell1.setCellValue(cedular);
            Row rwTot2 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cell3 = (rwTot2.getCell(6) == null ? rwTot2.createCell(6) : rwTot2.getCell(6));
            cell3.setCellValue(cedular);
        }
        Row rwNot = (sheet0.getRow(16) == null ? sheet0.createRow(16) : sheet0.getRow(16));
        Cell cell7 = (rwNot.getCell(1) == null ? rwNot.createCell(1) : rwNot.getCell(1));
        cell7.setCellValue("Bajo protesta de decir verdad manifiesto que el Estado de " + mensaje + " a mi cargo generó actos o actividades en el periodo " + periodo + " por concepto de retenciones de 5 al millar por un total de $" + Math.round(cedular * Math.pow(10, 2)) / Math.pow(10, 2) + "(" + total_letra + ") a pagar a la Tesorería de la Federación (con fundamento en el Artículo 191 de la Ley Federal de Derechos). Los impuestos que no se hayan enterado en tiempo, se pagaran con sus respectivos recargos y actualizaciones.");
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteTESOFE78R(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje, int tipoAjena) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ResumenRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        double autotransporte = 0.0d;
        double iva_arrendamiento = 0.0d;
        double isr_arrendamiento = 0.0d;
        double iva_honorarios = 0.0d;
        double isr_honorarios = 0.0d;
        double isr_resico = 0.0d;
        String total_letra = "";
        double total_iva = 0.0d;
        double total_isr = 0.0d;
        double total = 0.0d;
        String retencion = "";
        if (7 == tipoAjena)
            retencion = "IVA";
        else if (8 == tipoAjena)
            retencion = "ISR";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell = (rwEnc.getCell(2) == null ? rwEnc.createCell(2) : rwEnc.getCell(2));
        cell.setCellValue("ESTADO: " + mensaje);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(periodo);
        Row rwEnc9 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell cell9 = (rwEnc9.getCell(6) == null ? rwEnc9.createCell(6) : rwEnc9.getCell(6));
        cell9.setCellValue(retencion);
        if (rs.next()) {
            if (7 == tipoAjena) {
                autotransporte = rs.getDouble("autotransporte");
                iva_arrendamiento = rs.getDouble("iva_arrendamiento");
                iva_honorarios = rs.getDouble("iva_honorarios");
                total_iva = autotransporte + iva_arrendamiento + iva_honorarios + isr_resico;
                total = total_iva;
                Row rwTot1 = (sheet0.getRow(14) == null ? sheet0.createRow(14) : sheet0.getRow(14));
                Cell cell3 = (rwTot1.getCell(6) == null ? rwTot1.createCell(6) : rwTot1.getCell(6));
                cell3.setCellValue(autotransporte);
                Row rwTot2 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
                Cell cell4 = (rwTot2.getCell(6) == null ? rwTot2.createCell(6) : rwTot2.getCell(6));
                cell4.setCellValue(iva_arrendamiento);
                Row rwTot4 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
                Cell cell6 = (rwTot4.getCell(6) == null ? rwTot4.createCell(6) : rwTot4.getCell(6));
                cell6.setCellValue(iva_honorarios);
            } else if (8 == tipoAjena) {
                isr_arrendamiento = rs.getDouble("isr_arrendamiento");
                isr_honorarios = rs.getDouble("isr_honorarios");
                isr_resico = rs.getDouble("isr_resico");
                total_isr = isr_arrendamiento + isr_honorarios;
                total = total_isr;
                Row rwTot3 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
                Cell cell5 = (rwTot3.getCell(6) == null ? rwTot3.createCell(6) : rwTot3.getCell(6));
                cell5.setCellValue(isr_arrendamiento);
                Row rwTot5 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
                Cell cell7 = (rwTot5.getCell(6) == null ? rwTot5.createCell(6) : rwTot5.getCell(6));
                cell7.setCellValue(isr_honorarios);
                Row rwTot9 = (sheet0.getRow(16) == null ? sheet0.createRow(16) : sheet0.getRow(16));
                Cell cell11 = (rwTot9.getCell(6) == null ? rwTot9.createCell(6) : rwTot9.getCell(6));
                cell11.setCellValue(isr_resico);
            }
            total_letra = rs.getString("total_letra");
            Row rwTot6 = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell cell8 = (rwTot6.getCell(6) == null ? rwTot6.createCell(6) : rwTot6.getCell(6));
            cell8.setCellValue(total);
            Row rwTot8 = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell cell10 = (rwTot8.getCell(7) == null ? rwTot8.createCell(7) : rwTot8.getCell(7));
            cell10.setCellValue(total);
        }
        Row rwNot = (sheet0.getRow(22) == null ? sheet0.createRow(22) : sheet0.getRow(22));
        Cell cell7 = (rwNot.getCell(2) == null ? rwNot.createCell(2) : rwNot.getCell(2));
        cell7.setCellValue("Bajo protesta de decir verdad, manifiesto que el Estado de " + mensaje + " a mi cargo, generó actos o actividades en el periodo " + periodo + " con fundamento en los articulos  106 quinto párrafo de la Ley del ISR 116 quinto párrafo de la ley del ISR y el artículo 1-A de la ley del IVA y el artículo 3 de su reglamento y a los conceptos de retenciones antes mencionados que los montos a pagar a la SHCP correspondientes a ISR e IVA ascienden a un total de $" + Math.round(total * Math.pow(10, 2)) / Math.pow(10, 2) + "(" + total_letra + ") Los impuestos que no se hayan enterado en tiempo, se pagarán con sus respectivos recargos y actualizaciones.");
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
