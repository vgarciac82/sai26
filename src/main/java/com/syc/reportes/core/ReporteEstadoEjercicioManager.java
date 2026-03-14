package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Calendar;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ReporteEstadoEjercicioManager {

    public static String ReporteEstadoEjercicioManager(Connection conn, int mesIni, int anio, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null, cs2 = null, cs3 = null, cs4 = null, cs5 = null, cs6 = null, cs7 = null, cs8 = null, cs9 = null, cs10 = null, cs11 = null, cs12 = null, cs13 = null, cs14 = null, cs15 = null, cs16 = null, cs17 = null, cs18 = null, cs19 = null, cs20 = null, cs21 = null, cs22 = null, cs23 = null, cs24 = null, cs25 = null, cs26 = null, cs27 = null, cs28 = null, cs29 = null, cs30 = null, cs31 = null, cs32 = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null, rs9 = null, rs10 = null, rs11 = null, rs12 = null, rs13 = null, rs14 = null, rs15 = null, rs16 = null, rs17 = null, rs18 = null, rs19 = null, rs20 = null, rs21 = null, rs22 = null, rs23 = null, rs24 = null, rs25 = null, rs26 = null, rs27 = null, rs28 = null, rs29 = null, rs30 = null, rs31 = null, rs32 = null;
        String query31 = null;
        String query = "{CALL dbo.sp_OriginalModificadoSolicitado ( ?, ? )}";
        String query17 = "{CALL dbo.sp_Solicitado ( ?, ? )}";
        String query18 = "{CALL dbo.sp_AcumuladoSolicitado ( ?, ? )}";
        String query2 = "{CALL dbo.sp_DevengadoPagadoDiferencia ( ?, ? )}";
        String query3 = "{CALL dbo.sp_recaudadoSIAFF ( ?, ? )}";
        String query4 = "{CALL dbo.sp_acumuladosSIAFF ( ?, ? )}";
        String query25 = "{CALL dbo.sp_Comprobaciones ( ?, ? )}";
        String query5 = "{CALL dbo.sp_Flujo_OrgModSol ( ?, ? )}";
        String query19 = "{CALL dbo.sp_Flujo_Solicitado ( ?, ? )}";
        String query20 = "{CALL dbo.sp_Flujo_AcumuladoSol ( ?, ? )}";
        String query6 = "{CALL dbo.sp_Flujo_DevPagDif ( ?, ? )}";
        String query7 = "{CALL dbo.sp_Flujo_RecSIAFF ( ?, ? )}";
        String query8 = "{CALL dbo.sp_Flujo_AcumSIAFF ( ?, ? )}";
        String query26 = "{CALL dbo.sp_Flujo_Cmprobaciones ( ?, ? )}";
        String query9 = "{CALL dbo.sp_Flujo_OrgModSol_Propios ( ?, ? )}";
        String query21 = "{CALL dbo.sp_Flujo_Solicitado_Propios ( ?, ? )}";
        String query22 = "{CALL dbo.sp_Flujo_AcumuladoSol_Propios ( ?, ? )}";
        String query10 = "{CALL dbo.sp_Flujo_RecSIAFF_Propios ( ?, ? )}";
        String query11 = "{CALL dbo.sp_Flujo_AcumSIAFF_Propios ( ?, ? )}";
        String query12 = "{CALL dbo.sp_Flujo_DevPagDif_Propios ( ?, ? )}";
        String query27 = "{CALL dbo.sp_Flujo_Comprobaciones_Propios ( ?, ? )}";
        String query13 = "{CALL dbo.sp_Flujo_OrgModSol_FIP ( ?, ? )}";
        String query23 = "{CALL dbo.sp_Flujo_Solicitado_FIP ( ?, ? )}";
        String query24 = "{CALL dbo.sp_Flujo_AcumuladoSol_FIP ( ?, ? )}";
        String query14 = "{CALL dbo.sp_Flujo_RecSIAFF_FIP ( ?, ? )}";
        String query15 = "{CALL dbo.sp_Flujo_AcumSIAFF_FIP ( ?, ? )}";
        String query16 = "{CALL dbo.sp_Flujo_DevPagDif_FIP ( ?, ? )}";
        String query28 = "{CALL dbo.sp_Flujo_Comprobaciones_FIP ( ?, ? )}";
        String query29 = "{CALL dbo.sp_Totales ( ?, ? )}";
        String query30 = "{CALL dbo.sp_Totales2daParte ( ?, ? )}";
        String query32 = "{CALL dbo.sp_Totales3raParte ( ?, ? )}";
        if (anio >= 2018) {
            query31 = "{call sp_SIPOT_31_IngresosPresupuestales_v2( ?, ?)}";
        } else {
            query31 = "{call sp_SIPOT_31_IngresosPresupuestales( ?, ?)}";
        }
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, anio);
            cs.setInt(2, mesIni);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, anio);
            cs2.setInt(2, mesIni);
            rs2 = cs2.executeQuery();
            cs3 = conn.prepareCall(query3);
            cs3.setInt(1, anio);
            cs3.setInt(2, mesIni);
            rs3 = cs3.executeQuery();
            cs4 = conn.prepareCall(query4);
            cs4.setInt(1, anio);
            cs4.setInt(2, mesIni);
            rs4 = cs4.executeQuery();
            cs5 = conn.prepareCall(query5);
            cs5.setInt(1, anio);
            cs5.setInt(2, mesIni);
            rs5 = cs5.executeQuery();
            cs6 = conn.prepareCall(query6);
            cs6.setInt(1, anio);
            cs6.setInt(2, mesIni);
            rs6 = cs6.executeQuery();
            cs7 = conn.prepareCall(query7);
            cs7.setInt(1, anio);
            cs7.setInt(2, mesIni);
            rs7 = cs7.executeQuery();
            cs8 = conn.prepareCall(query8);
            cs8.setInt(1, anio);
            cs8.setInt(2, mesIni);
            rs8 = cs8.executeQuery();
            cs9 = conn.prepareCall(query9);
            cs9.setInt(1, anio);
            cs9.setInt(2, mesIni);
            rs9 = cs9.executeQuery();
            cs10 = conn.prepareCall(query10);
            cs10.setInt(1, anio);
            cs10.setInt(2, mesIni);
            rs10 = cs10.executeQuery();
            cs11 = conn.prepareCall(query11);
            cs11.setInt(1, anio);
            cs11.setInt(2, mesIni);
            rs11 = cs11.executeQuery();
            cs12 = conn.prepareCall(query12);
            cs12.setInt(1, anio);
            cs12.setInt(2, mesIni);
            rs12 = cs12.executeQuery();
            cs13 = conn.prepareCall(query13);
            cs13.setInt(1, anio);
            cs13.setInt(2, mesIni);
            rs13 = cs13.executeQuery();
            cs14 = conn.prepareCall(query14);
            cs14.setInt(1, anio);
            cs14.setInt(2, mesIni);
            rs14 = cs14.executeQuery();
            cs15 = conn.prepareCall(query15);
            cs15.setInt(1, anio);
            cs15.setInt(2, mesIni);
            rs15 = cs15.executeQuery();
            cs16 = conn.prepareCall(query16);
            cs16.setInt(1, anio);
            cs16.setInt(2, mesIni);
            rs16 = cs16.executeQuery();
            cs17 = conn.prepareCall(query17);
            cs17.setInt(1, anio);
            cs17.setInt(2, mesIni);
            rs17 = cs17.executeQuery();
            cs18 = conn.prepareCall(query18);
            cs18.setInt(1, anio);
            cs18.setInt(2, mesIni);
            rs18 = cs18.executeQuery();
            cs19 = conn.prepareCall(query19);
            cs19.setInt(1, anio);
            cs19.setInt(2, mesIni);
            rs19 = cs19.executeQuery();
            cs20 = conn.prepareCall(query20);
            cs20.setInt(1, anio);
            cs20.setInt(2, mesIni);
            rs20 = cs20.executeQuery();
            cs21 = conn.prepareCall(query21);
            cs21.setInt(1, anio);
            cs21.setInt(2, mesIni);
            rs21 = cs21.executeQuery();
            cs22 = conn.prepareCall(query22);
            cs22.setInt(1, anio);
            cs22.setInt(2, mesIni);
            rs22 = cs22.executeQuery();
            cs23 = conn.prepareCall(query23);
            cs23.setInt(1, anio);
            cs23.setInt(2, mesIni);
            rs23 = cs23.executeQuery();
            cs24 = conn.prepareCall(query24);
            cs24.setInt(1, anio);
            cs24.setInt(2, mesIni);
            rs24 = cs24.executeQuery();
            cs25 = conn.prepareCall(query25);
            cs25.setInt(1, anio);
            cs25.setInt(2, mesIni);
            rs25 = cs25.executeQuery();
            cs26 = conn.prepareCall(query26);
            cs26.setInt(1, anio);
            cs26.setInt(2, mesIni);
            rs26 = cs26.executeQuery();
            cs27 = conn.prepareCall(query27);
            cs27.setInt(1, anio);
            cs27.setInt(2, mesIni);
            rs27 = cs27.executeQuery();
            cs28 = conn.prepareCall(query28);
            cs28.setInt(1, anio);
            cs28.setInt(2, mesIni);
            rs28 = cs28.executeQuery();
            cs29 = conn.prepareCall(query29);
            cs29.setInt(1, anio);
            cs29.setInt(2, mesIni);
            rs29 = cs29.executeQuery();
            cs30 = conn.prepareCall(query30);
            cs30.setInt(1, anio);
            cs30.setInt(2, mesIni);
            rs30 = cs30.executeQuery();
            cs31 = conn.prepareCall(query31);
            cs31.setInt(1, anio);
            cs31.setInt(2, mesIni);
            rs31 = cs31.executeQuery();
            cs32 = conn.prepareCall(query32);
            cs32.setInt(1, anio);
            cs32.setInt(2, mesIni);
            rs32 = cs32.executeQuery();
            fileName = generaReporte(rs, rs2, rs3, rs4, rs5, rs6, rs7, rs8, rs9, rs10, rs11, rs12, rs13, rs14, rs15, rs16, rs17, rs18, rs19, rs20, rs21, rs22, rs23, rs24, rs25, rs26, rs27, rs28, rs29, rs30, rs32, plantillas.get("MASIVO"), mesIni);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(rs4, false);
            CloseObject.closeObject(rs5, false);
            CloseObject.closeObject(rs6, false);
            CloseObject.closeObject(rs7, false);
            CloseObject.closeObject(rs8, false);
            CloseObject.closeObject(rs9, false);
            CloseObject.closeObject(rs10, false);
            CloseObject.closeObject(rs11, false);
            CloseObject.closeObject(rs12, false);
            CloseObject.closeObject(rs13, false);
            CloseObject.closeObject(rs14, false);
            CloseObject.closeObject(rs15, false);
            CloseObject.closeObject(rs16, false);
            CloseObject.closeObject(rs17, false);
            CloseObject.closeObject(rs18, false);
            CloseObject.closeObject(rs19, false);
            CloseObject.closeObject(rs20, false);
            CloseObject.closeObject(rs21, false);
            CloseObject.closeObject(rs22, false);
            CloseObject.closeObject(rs23, false);
            CloseObject.closeObject(rs24, false);
            CloseObject.closeObject(rs25, false);
            CloseObject.closeObject(rs26, false);
            CloseObject.closeObject(rs27, false);
            CloseObject.closeObject(rs28, false);
            CloseObject.closeObject(rs29, false);
            CloseObject.closeObject(rs30, false);
            CloseObject.closeObject(rs31, false);
            CloseObject.closeObject(rs32, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(cs3, false);
            CloseObject.closeObject(cs4, false);
            CloseObject.closeObject(cs5, false);
            CloseObject.closeObject(cs6, false);
            CloseObject.closeObject(cs7, false);
            CloseObject.closeObject(cs8, false);
            CloseObject.closeObject(cs9, false);
            CloseObject.closeObject(cs10, false);
            CloseObject.closeObject(cs11, false);
            CloseObject.closeObject(cs12, false);
            CloseObject.closeObject(cs13, false);
            CloseObject.closeObject(cs14, false);
            CloseObject.closeObject(cs15, false);
            CloseObject.closeObject(cs16, false);
            CloseObject.closeObject(cs17, false);
            CloseObject.closeObject(cs18, false);
            CloseObject.closeObject(cs19, false);
            CloseObject.closeObject(cs20, false);
            CloseObject.closeObject(cs21, false);
            CloseObject.closeObject(cs22, false);
            CloseObject.closeObject(cs23, false);
            CloseObject.closeObject(cs24, false);
            CloseObject.closeObject(cs25, false);
            CloseObject.closeObject(cs26, false);
            CloseObject.closeObject(cs27, false);
            CloseObject.closeObject(cs28, false);
            CloseObject.closeObject(cs29, false);
            CloseObject.closeObject(cs30, false);
            CloseObject.closeObject(cs31, false);
            CloseObject.closeObject(cs32, false);
        }
    }

    private static String generaReporte(ResultSet rs, ResultSet rs2, ResultSet rs3, ResultSet rs4, ResultSet rs5, ResultSet rs6, ResultSet rs7, ResultSet rs8, ResultSet rs9, ResultSet rs10, ResultSet rs11, ResultSet rs12, ResultSet rs13, ResultSet rs14, ResultSet rs15, ResultSet rs16, ResultSet rs17, ResultSet rs18, ResultSet rs19, ResultSet rs20, ResultSet rs21, ResultSet rs22, ResultSet rs23, ResultSet rs24, ResultSet rs25, ResultSet rs26, ResultSet rs27, ResultSet rs28, ResultSet rs29, ResultSet rs30, ResultSet rs32, String plantillaPath, int mesIni) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteEstadoDelEjercicio" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        Calendar fecha = Calendar.getInstance();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Row newRow;
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 12;
        String mesTrabajo = "MODIFICADO ANUAL " + Util.NOMBRE_MESES_MX[mesIni - 1];
        String Encabezado = "ESTADO DEL EJERCICIO " + Util.NOMBRE_MESES_MX[mesIni - 1] + "  " + dia + "/" + mes + "/" + año + " " + hora + ":" + minuto + ":" + segundo;
        // Original, Modificado
        sheet0 = Util.resultSetToExcel(rs, sheet0, renglonInicio, -1, 1, false);
        int renglonFinal = sheet0.getLastRowNum();
        // Solicitado
        sheet0 = Util.resultSetToExcel(rs17, sheet0, renglonInicio, 43, 2, false);
        // Acumulado Solicitado
        sheet0 = Util.resultSetToExcel(rs18, sheet0, renglonInicio, 103, 2, false);
        // Devengado, Pagado, Diferencia	--antes 147
        sheet0 = Util.resultSetToExcel(rs2, sheet0, renglonInicio, 199, 2, false);
        // Radicado SIAFF --antes 56
        sheet0 = Util.resultSetToExcel(rs3, sheet0, renglonInicio, 108, 2, false);
        // Acumulados SIAFF --antes 140
        sheet0 = Util.resultSetToExcel(rs4, sheet0, renglonInicio, 192, 2, false);
        // Comprobaciones
        sheet0 = Util.resultSetToExcel(rs25, sheet0, renglonInicio, 237, 2, false);
        System.out.println("Impresion del detalle");
        // Flujo Ingreso Orginal, Modificado
        sheet0 = Util.resultSetToExcel(rs5, sheet0, renglonFinal + 5, 15, 1, false);
        // Flujo Solicitado
        sheet0 = Util.resultSetToExcel(rs19, sheet0, renglonFinal + 5, 43, 2, false);
        // Flujo Ingreso Acumulado Solicitado
        sheet0 = Util.resultSetToExcel(rs20, sheet0, renglonFinal + 5, 103, 2, false);
        // Flujo Ingreso Devengado, Pagado, Diferencia --antes 147
        sheet0 = Util.resultSetToExcel(rs6, sheet0, renglonFinal + 5, 199, 2, false);
        // Flujo Ingreso Radicado SIAFF --antes 56
        sheet0 = Util.resultSetToExcel(rs7, sheet0, renglonFinal + 5, 108, 2, false);
        // Flujo Ingreso Acumulado SIAFF --antes 140
        sheet0 = Util.resultSetToExcel(rs8, sheet0, renglonFinal + 5, 192, 2, false);
        // Flujo Ingreso Comprobaciones
        sheet0 = Util.resultSetToExcel(rs26, sheet0, renglonFinal + 5, 237, 2, false);
        System.out.println("Impresion del Flujo Fiscal (Ingreso y Egreso");
        // Flujo Ingreso Orginal, Modificado Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs9, sheet0, renglonFinal + 31, 15, 1, false);
        // Flujo Solicitado Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs21, sheet0, renglonFinal + 31, 43, 2, false);
        // Flujo Acumulado Solicitado Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs22, sheet0, renglonFinal + 31, 103, 2, false);
        // Flujo Ingreso Radicado SIAFF Ingresos Propios --antes 56
        sheet0 = Util.resultSetToExcel(rs10, sheet0, renglonFinal + 31, 108, 2, false);
        // Flujo Ingreso Acumulado SIAFF Ingresos Propios --antes 140
        sheet0 = Util.resultSetToExcel(rs11, sheet0, renglonFinal + 31, 192, 2, false);
        // Flujo Ingreso Devengado, Pagado, Diferencia --antes 147
        sheet0 = Util.resultSetToExcel(rs12, sheet0, renglonFinal + 31, 199, 2, false);
        // Flujo Comprobaciones Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs27, sheet0, renglonFinal + 31, 237, 2, false);
        System.out.println("Impresion del Flujo Ingresos Propios (Egresos)");
        // Flujo Ingreso Orginal, Modificado Federal + Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs13, sheet0, renglonFinal + 44, 15, 1, false);
        // Flujo Ingreso Solicitado Federal + Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs23, sheet0, renglonFinal + 44, 43, 2, false);
        // Flujo Ingreso Solicitado Federal + Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs24, sheet0, renglonFinal + 44, 103, 2, false);
        // Flujo Ingreso Radicado SIAFF Federal + Ingresos Propios del Egreso --antes 56
        sheet0 = Util.resultSetToExcel(rs14, sheet0, renglonFinal + 44, 108, 2, false);
        // Flujo Ingreso Acumulado SIAFF Federal + Ingresos Propios del Egreso --antes 140
        sheet0 = Util.resultSetToExcel(rs15, sheet0, renglonFinal + 44, 192, 2, false);
        // Flujo Ingreso Devengado, Federal + Pagado, Diferencia del Egreso --antes 147
        sheet0 = Util.resultSetToExcel(rs16, sheet0, renglonFinal + 44, 199, 2, false);
        // Flujo Comprobacion Federal + Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs28, sheet0, renglonFinal + 44, 237, 2, false);
        System.out.println("Impresion del Flujo Fiscal + Ingresos Propios (Egresos)");
        // Flujo Comprobacion Federal + Ingresos Propios
        sheet0 = Util.resultSetToExcel(rs29, sheet0, 6, 16, 1, false);
        // Flujo Comprobacion Federal + Ingresos Propios 2da Parte
        sheet0 = Util.resultSetToExcel(rs30, sheet0, 6, 109, 1, false);
        // Flujo Comprobacion Federal + Ingresos Propios 3ra Parte
        sheet0 = Util.resultSetToExcel(rs32, sheet0, 6, 200, 1, false);
        System.out.println("Impresion de totales en el encabezado");
        Row rwEnc = (sheet0.getRow(renglonFinal + 3) == null ? sheet0.createRow(renglonFinal + 3) : sheet0.getRow(renglonFinal + 3));
        Cell cell = (rwEnc.getCell(16) == null ? rwEnc.createCell(16) : rwEnc.getCell(16));
        cell.setCellValue(" FLUJO DE EFECTIVO:  FEDERAL ");
        Row rwEnc2 = (sheet0.getRow(renglonFinal + 29) == null ? sheet0.createRow(renglonFinal + 29) : sheet0.getRow(renglonFinal + 29));
        Cell cell2 = (rwEnc2.getCell(16) == null ? rwEnc2.createCell(16) : rwEnc2.getCell(16));
        cell2.setCellValue(" FLUJO DE EFECTIVO:  INGRESOS PROPIOS ");
        Row rwEnc3 = (sheet0.getRow(renglonFinal + 42) == null ? sheet0.createRow(renglonFinal + 42) : sheet0.getRow(renglonFinal + 42));
        Cell cell3 = (rwEnc3.getCell(16) == null ? rwEnc3.createCell(16) : rwEnc3.getCell(16));
        cell3.setCellValue(" FLUJO DE EFECTIVO:  FEDERAL + INGRESOS PROPIOS ");
        Row rwEnc4 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
        Cell cell4 = (rwEnc4.getCell(43) == null ? rwEnc4.createCell(43) : rwEnc4.getCell(43));
        cell4.setCellValue(mesTrabajo);
        Row rwEnc5 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell5 = (rwEnc5.getCell(1) == null ? rwEnc5.createCell(1) : rwEnc5.getCell(1));
        cell5.setCellValue(Encabezado);
        /*INSERTAR LINEAS ENTRE LAS YA EXISTENTES*/
        //Add the new row between row 9 and 10
        int createNewRowAt = renglonFinal + 14;
        for (int i = 0; i < 3; i++) {
            sheet0.shiftRows(createNewRowAt, sheet0.getLastRowNum(), 1, true, false);
            newRow = sheet0.createRow(createNewRowAt);
            newRow = sheet0.getRow(createNewRowAt);
            createNewRowAt++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 0, 3, 15, 243);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static void limpiaEdoEjercicio(Connection conn, int mesIni, int anio) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{CALL dbo.sp_a_BorraEdoEjercicio ( ?, ? ) }";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, anio);
            cs.setInt(2, mesIni);
            cs.executeQuery();
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }
}
