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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteAuxiliarCRIManager {

    private static ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private static String tipoFuente = cabl.getSystemSetting("FUENTE_INSTITUCIONAL");

    private static final Logger log = LoggerFactory.getLogger(ReporteAcreedoresDeudoresManager.class);

    public static String ReporteAuxilairCRIManager(Connection conn, String buscaCtaMayor, String buscaSubCtaMayor, String cCentroContable, String fAuxIni, String fAuxFin, String conEP, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_GeneraAuxiliarCRI( ?, ?, ?, ?, ?, ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, buscaCtaMayor);
            cs.setString(2, buscaSubCtaMayor);
            cs.setString(3, cCentroContable);
            cs.setString(4, fAuxIni);
            cs.setString(5, fAuxFin);
            cs.setString(6, conEP);
            rs = cs.executeQuery();
            fileName = generaReporteAuxiliarCRI(rs, plantillas.get("AUXILIARCRI"), fAuxIni, fAuxFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteAuxiliarCRI(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "AuxiliarCRI" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
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
            periodo = "Detalle de Cuenta del mes " + Util.NOMBRE_MESES_MX[mesIni - 1] + " a " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(7) == null ? rwEnc2.createCell(7) : rwEnc2.getCell(7));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, renglonInicio + cnt, 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        //sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 2), (renglonInicio + cnt + 8), 8, 9);
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
