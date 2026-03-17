package com.axtel.contabilidad.validacionXML;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class validaXMLManager {

    public static final Logger log = LoggerFactory.getLogger(validaXMLManager.class);

    public static String buscaXML(Connection conn, String condicion, String nombreDestino) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int cont = 0;
        String fileName = "";
        String query = "SELECT UUID, nFolioPago, cTipoPago, RFC, mImporteBruto, mimporteiva	mimporteconiva, cNombreBD, estatus FROM v_busca_UUID WHERE UUID " + condicion;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            fileName = generaReporteMasivo(rs, nombreDestino);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteMasivo(ResultSet rs, String nombreDestino) throws Exception {
        File cFileExcelPlantilla = new File(nombreDestino);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ResultadoUUID" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet1 = workbook.createSheet("Resultado");
        int renglonInicio = 0;
        sheet1 = Util.resultSetToExcel(rs, sheet1, renglonInicio, true);
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
