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
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
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

public class PolizaCierreManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteAcreedoresDeudoresManager.class);

    public static float creaPolizaCierreManager(Connection conn, String usuario, String ur, String cc, int id_documento) throws Exception {
        log.info("Iniciando calculo de la poliza de cierre de ejercicio");
        CallableStatement cs1 = null;
        PreparedStatement psEF = null, psC = null;
        ResultSet rs1 = null, rsEF = null, rsC = null;
        String ejercicioFiscal = "";
        float dif = -1f;
        String queryEF = "SELECT aEjercicioFiscal FROM tejerciciofiscal WHERE cactivo = 1";
        String query1 = "{call sp_polizaCierre_Final( ?, ?, ?, ?, ? )}";
        String queryC = "SELECT SUM(cargo) - SUM(abono) AS dif FROM ( " + "SELECT CASE WHEN cEvento = 'CARGO' THEN SUM(mImporte) ELSE 0 END AS cargo " + ", CASE WHEN cEvento = 'ABONO' THEN SUM(mImporte) ELSE 0 END AS abono " + "FROM tPolizaCierreDetalle WITH (NOLOCK) " + "WHERE nFolioPolizaCierre = ? " + "GROUP BY cEvento) dif";
        try {
            psEF = conn.prepareStatement(queryEF);
            rsEF = psEF.executeQuery();
            if (rsEF.next()) {
                ejercicioFiscal = rsEF.getString(1);
            }
            log.debug("Object: {}", "Ejecutando [" + queryEF + "]");
            log.debug("Object: {}", "Ejercicio Fiscal: " + ejercicioFiscal);
            cs1 = conn.prepareCall(query1);
            cs1.setString(1, ejercicioFiscal);
            cs1.setInt(2, id_documento);
            cs1.setString(3, usuario);
            cs1.setString(4, ur);
            cs1.setString(5, cc);
            rs1 = cs1.executeQuery();
            log.debug("Object: {}", query1.toString());
            log.debug("Object: {}", "Ejercicio Fiscal: " + ejercicioFiscal);
            log.debug("Object: {}", "Id poliza de cierre: " + id_documento);
            log.debug("Object: {}", "Usuario en sesion: " + usuario);
            log.debug("Object: {}", "Unidad Responsable: " + ur);
            log.debug("Object: {}", "Centro Contable: " + cc);
            psC = conn.prepareStatement(queryC);
            psC.setInt(1, id_documento);
            rsC = psC.executeQuery();
            while (rsC.next()) {
                dif = (rsC.getFloat(1));
            }
            log.debug("Object: {}", "Ejecutando [" + queryC + "]");
            log.info("Object: {}", "Diferencia entre cargos y abonos es: " + dif);
            return dif;
        } finally {
            CloseObject.closeObject(rs1, false);
            CloseObject.closeObject(cs1, false);
            CloseObject.closeObject(psEF, false);
            CloseObject.closeObject(rsEF, false);
            CloseObject.closeObject(rsC, false);
            CloseObject.closeObject(psC, false);
        }
    }

    public static String consultaTemporal(Connection conn, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        log.info("Iniciando calculo de póliza de cierre");
        long startQueries = System.currentTimeMillis();
        String fileName = "";
        String ejercicioFiscal = "";
        CallableStatement cs = null;
        PreparedStatement psEF = null;
        ResultSet rs = null, rsEF = null;
        String queryEF = "SELECT aEjercicioFiscal FROM tejerciciofiscal WHERE cactivo = 1";
        try {
            psEF = conn.prepareStatement(queryEF);
            rsEF = psEF.executeQuery();
            if (rsEF.next()) {
                ejercicioFiscal = rsEF.getString(1);
            }
            String query = "{call sp_polizaCierre_Consulta (?)}";
            cs = conn.prepareCall(query);
            cs.setString(1, ejercicioFiscal);
            log.debug("Object: {}", "Ejecutando[" + query + "]");
            log.debug("Object: {}", "Ejercicio Fiscal: " + ejercicioFiscal);
            rs = cs.executeQuery();
            long stopQueries = System.currentTimeMillis();
            log.info("Object: {}", String.format("Ejecucion de consultas terminado en [%2d] segundos", (stopQueries - startQueries) / 1000));
            fileName = generaExcel(rs, plantillas.get("CIERRE"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rsEF, false);
            CloseObject.closeObject(psEF, false);
        }
    }

    private static String generaExcel(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "TemporalPolizaCierre" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        sheet0 = Util.resultSetToExcel(rs, sheet0, 3, 1, true);
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

    public static int obtieneFolioDocumento(Connection conn) throws Exception {
        PreparedStatement psV = null;
        ResultSet rsV = null;
        int idVersion_sig = 0;
        String queryV = "SELECT COUNT(*) + 1 FROM dbo.tPolizaCierreEncabezado WITH (NOLOCK)";
        try {
            psV = conn.prepareStatement(queryV);
            rsV = psV.executeQuery();
            if (rsV.next()) {
                idVersion_sig = rsV.getInt(1);
            }
            log.debug("Object: {}", queryV.toString());
            log.debug("Object: {}", "Id poliza de cierre " + idVersion_sig);
            return idVersion_sig;
        } finally {
            CloseObject.closeObject(rsV, false);
            CloseObject.closeObject(psV, false);
        }
    }
}
