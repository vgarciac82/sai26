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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReportesGreenMexManager {

    private static final Logger log = LoggerFactory.getLogger(ReportesGreenMexManager.class);

    public static String ReporteFinancieroGreenMex(Connection conn, String fechaInicio, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando reporte financiero GreenMex"));
        long startQueries = System.currentTimeMillis();
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "";
        String fileName = "";
        try {
            query = "{call sp_saldoFinancieroGreenMex( ? )}";
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            rs = cs.executeQuery();
            log.trace("Object: {}", "Select[" + query + "]");
            long stopQueries = System.currentTimeMillis();
            log.info("Object: {}", String.format("Ejecucion de consultas terminado en [%2d] segundos", (stopQueries - startQueries) / 1000));
            fileName = generaReporteFinancieroGreenMex(rs, plantillas.get("FINANCIEROGREENMEX"), fechaInicio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteFinancieroGreenMex(ResultSet rs, String plantillaPath, String fechaInicio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteFinancieroGreenMex" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 5;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        sheet0 = Util.resultSetToExcelE(rs, sheet0, renglonInicio, 0, false, estiloTabla);
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
