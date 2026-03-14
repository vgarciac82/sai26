package com.syc.contable.core;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

/**
 * @author Janise Diaz Sanchez
 *         04/03/2019
 */
public class CargaAdecuacionManager {

    public CargaAdecuacionManager() {
        super();
    }

    public static boolean copiaArchivoRemoto(Connection conn, String archivoOrigen, DataInputStream archivoCargaStream, int mes) throws Exception {
        String rutaRemoto = "";
        String dominio = "";
        String nombreCompleto = "";
        Boolean error = true;
        int indexDiagonal = archivoOrigen.lastIndexOf("\\");
        if (indexDiagonal > 0)
            nombreCompleto = archivoOrigen.substring(indexDiagonal + 1);
        rutaRemoto = ConfiguraAplicativoManager.obtenRutaRemoto(conn);
        dominio = ConfiguraAplicativoManager.obtenDominioRemoto(conn);
        dominio = dominio.replace(".", "");
        //SE COMENTA PARA PRUEBAS Y SE COPIA MANUALMENTE EL ARCHIVO
        Util.uploadStreamServerBD(archivoOrigen, archivoCargaStream);
        nombreCompleto = rutaRemoto.substring(4) + nombreCompleto;
        error = cargaArchivo(conn, nombreCompleto);
        return error;
    }

    public static boolean cargaArchivo(Connection conn, String ruta) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        Boolean respuesta = false;
        String estatus = "";
        String query = ("{ call sp_CargarArchivo ( ?)}");
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, ruta);
            rs = cs.executeQuery();
            while (rs.next()) {
                estatus = rs.getString("VALIDAR");
            }
            if ("CORRECTO".equals(estatus)) {
                respuesta = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
        return respuesta;
    }

    public static String insertRegisterLayout(Connection conn, int mes, String cEsIP, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String fileName = "";
        String query = ("{ call sp_FlujoEfectivo_redondeo( ?, ?)}");
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mes);
            cs.setString(2, cEsIP);
            rs = cs.executeQuery();
            fileName = generaRegistroLayout(rs, plantillas.get("Adecuaciones"), mes);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String consultaRegisterLayout(Connection conn, int mes, int version, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String fileName = "";
        String query = " SELECT * FROM dbo.tModificadoMapeDetalle (NOLOCK)" + " WHERE nmes = ?  AND nIdModificado = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, mes);
            ps.setInt(2, version);
            rs = ps.executeQuery();
            fileName = generaRegistroLayout(rs, plantillas.get("Adecuaciones"), mes);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaRegistroLayout(ResultSet rs, String plantillaPath, int mes) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = "ReporteFlujoEfectivo" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
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
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static boolean borrarMes(Connection conn, int mes) throws Exception {
        boolean borrado = false;
        PreparedStatement pstmnt = null;
        try {
            String querySelect = "DELETE ADECUACIONES_MAP where Month(fFecha) = " + mes;
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
}
