package com.syc.contable;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import com.syc.contable.core.CargaRefasManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CargaRefasBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CargaRefasBusinessLogic.class);

    public CargaRefasBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public boolean cargaExcelRefas(FileInputStream archivo, String ejercicio_fiscal, Usuario usuario, String u_logion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ArrayList<ArrayList<String>> lst = new ArrayList<ArrayList<String>>();
            ArrayList<String> lstString = new ArrayList<String>();
            String u_Nombre = usuario.getNombre();
            HSSFWorkbook workBook = new HSSFWorkbook(archivo);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);
            CargaRefasManager.insertaDataCargaRefas(conn, extraerLineas(lst, lstString, hssfSheet), ejercicio_fiscal);
            CargaRefasManager.insertaCargaRefas(conn, u_logion, u_Nombre, ejercicio_fiscal);
            CargaRefasManager.insertaCargaEjercicio(conn, ejercicio_fiscal);
            workBook.close();
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.commit();
            if (conn != null)
                conn.close();
            conn = null;
        }
        return true;
    }

    public ArrayList<ArrayList<String>> extraerLineas(ArrayList<ArrayList<String>> lst, ArrayList<String> lstString, HSSFSheet hssfSheet) throws Exception {
        Iterator rowIterator = hssfSheet.rowIterator();
        int iteCount = 0;
        String cValor;
        int rowCount = 0;
        boolean ultRow = false;
        while (rowIterator.hasNext()) {
            HSSFRow hssfRow = (HSSFRow) rowIterator.next();
            Iterator iterator = hssfRow.cellIterator();
            //while (iterator.hasNext()) {
            int algo = hssfRow.getLastCellNum();
            int dos = hssfRow.getHeight();
            for (int i = 0; i < algo; i++) {
                if (rowCount == 0) {
                    rowCount++;
                    break;
                }
                HSSFCell hssfCell = hssfRow.getCell(i);
                cValor = obtenerValor(hssfCell);
                if (iteCount == 0 && cValor.equals("")) {
                    ultRow = true;
                    break;
                }
                iteCount++;
                lstString.add(cValor);
            }
            iteCount = 0;
            if (ultRow == false && rowCount != 0) {
                lst.add(lstString);
                lstString = new ArrayList<String>();
            }
        }
        return lst;
    }

    public ArrayList<String> getEjerciciosRefas() throws SQLException {
        Connection conn = null;
        ArrayList<String> lstString = new ArrayList<String>();
        try {
            conn = getConnection();
            lstString = CargaRefasManager.getEjercicios(conn, "N");
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.commit();
            if (conn != null)
                conn.close();
            conn = null;
        }
        return lstString;
    }

    public ArrayList<String> getEjerciciosValidosRefas() throws SQLException {
        Connection conn = null;
        ArrayList<String> lstString = new ArrayList<String>();
        try {
            conn = getConnection();
            lstString = CargaRefasManager.getEjercicios(conn, "S");
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.commit();
            if (conn != null)
                conn.close();
            conn = null;
        }
        return lstString;
    }

    private String obtenerValor(HSSFCell hssfCell) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        String cValor = " ";
        try {
            cValor = hssfCell.getStringCellValue();
        } catch (Exception e) {
            if (DateUtil.isCellDateFormatted(hssfCell)) {
                Date c3 = hssfCell.getDateCellValue();
                cValor = sdf2.format(c3.getTime());
            } else {
                try {
                    cValor = String.valueOf((double) hssfCell.getNumericCellValue());
                } catch (NullPointerException nullExp) {
                    cValor = " ";
                }
            }
        }
        return cValor;
    }
}
