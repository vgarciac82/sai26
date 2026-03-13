package com.axtel.presupuesto;

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
import java.util.Map;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OficiosSIPLANManager {

    public static final Logger log = LoggerFactory.getLogger(OficiosSIPLANManager.class);

    public static int buscaOficio(Connection conn, int nOficio, String coordinacion) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int cont = 0;
        String query = "SELECT COUNT(*) FROM tOficiosSIPLAN WHERE nOficio = " + nOficio;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                cont = (rs.getInt(1));
            }
            return cont;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int InsertaOficio(Connection conn, String fechaInicio, int nOficio, String usuario, String coordinacion, int mes, String qwhere) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        int cont = 0;
        try {
            cstmt = conn.prepareCall("{call sp_insertaOficioSIPLAN ( ?, ?, ?, ?, ?, ? )}");
            cstmt.setInt(1, nOficio);
            cstmt.setString(2, fechaInicio);
            cstmt.setInt(3, mes);
            cstmt.setString(4, qwhere);
            cstmt.setString(5, usuario);
            cstmt.setString(6, coordinacion);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                cont = (rs.getInt(1));
            }
            return cont;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    public static String ReporteModificado(Connection conn, int mes, Map<String, String> plantilla) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String fileName = "";
        String query = "SELECT nMes, cSubCuenta, Modenero, ModFebrero, ModMarzo, ModAbril, ModMayo, ModJunio, ModJulio, ModAgosto, ModSeptiembre, ModOctubre, ModNoviembre, ModDiciembre FROM tModificadoSAI WHERE nMes = ?";
        try {
            ps = conn.prepareCall(query);
            ps.setInt(1, mes);
            rs = ps.executeQuery();
            fileName = generaReporte(rs, plantilla.get("ModificadoSIPLAN"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporte(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Modificado" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 1;
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
