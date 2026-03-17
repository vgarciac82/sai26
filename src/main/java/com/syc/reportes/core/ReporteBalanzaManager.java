package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteBalanzaManager {

    public static String generaReporteBalanzaManager(Connection conn, String ejercicio, String cc, String mes, String tipoReporte, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "";
        int anio;
        int nMes;
        anio = Integer.parseInt(ejercicio);
        nMes = Integer.parseInt(mes);
        switch(tipoReporte) {
            case "1":
                if (anio <= 2024) {
                    if (nMes <= 7)
                        query = "{call sp_Balanza_Conta_Mensual ( ?, ?, ? )}";
                    else if (nMes >= 8)
                        query = "{call sp_Balanza_Conta_Mensual_v2 ( ?, ?, ? )}";
                } else if (anio > 2024)
                    query = "{call sp_Balanza_Conta_Mensual_v2 ( ?, ?, ? )}";
                break;
            case "2":
                query = "{call sp_Balanza_Det_Mensual( ?, ?, ? )}";
                break;
            case "3":
                if (anio <= 2024) {
                    if (nMes <= 7)
                        query = "{call sp_Balanza_Conta_Ac( ?, ?, ? )}";
                    else if (nMes >= 8)
                        query = "{call sp_Balanza_Conta_Ac_v2( ?, ?, ? )}";
                } else if (anio > 2024)
                    query = "{call sp_Balanza_Conta_Ac_v2( ?, ?, ? )}";
                break;
            case "4":
                query = "{call sp_Balanza_Det_Ac( ?, ?, ? )}";
                break;
            default:
        }
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, ejercicio);
            cs.setString(2, cc);
            cs.setString(3, mes);
            rs = cs.executeQuery();
            fileName = generaReporteExcel(rs, plantillas.get("Balanza"), mes);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteExcel(ResultSet rs, String plantillaPath, String mes) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteBalanza" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 1;
        //XSSFCellStyle estiloTabla = ( XSSFCellStyle ) Util.generaEstilo( workbook, 9, false, false, false, false, false );
        //XSSFCellStyle estiloMoneda = ( XSSFCellStyle ) Util.generaEstilo( workbook, 9, false, false, false, false, true );
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                /*if (i < 8)
					Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla );
				else
					Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
					*/
                Util.createExcelCell(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
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
}
