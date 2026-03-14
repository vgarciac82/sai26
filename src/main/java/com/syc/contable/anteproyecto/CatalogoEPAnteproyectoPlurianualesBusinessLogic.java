package com.syc.contable.anteproyecto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.core.IAnteProyectoManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CatalogoEPAnteproyectoPlurianualesBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CatalogoEPAnteproyectoPlurianualesBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public CatalogoEPAnteproyectoPlurianualesBusinessLogic(String uLogin) {
        super.init();
        this.uLoginCarga = uLogin;
    }

    /**
     * Carga el archivo y lo almacena en la base de datos.
     */
    /**
     * Carga la base SICOP dada la ruta de un archivo excel y lo almacena en la
     * base de datos. <br>
     */
    public List<String> CatalogoEPAnteproyectoPlurianuales(String nombreDestino) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(nombreDestino);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + nombreDestino + "]");
            stream = new FileInputStream(f);
            return CatalogoEPAnteproyectoPlurianuales(stream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "No se pudo cerrar el archivo de carga." + e2);
                }
            stream = null;
        }
    }

    @SuppressWarnings("null")
    public synchronized List<String> CatalogoEPAnteproyectoPlurianuales(InputStream stream) throws Exception {
        List<String> errores = new ArrayList<String>();
        Map<String, String> renglonMap = null;
        Connection conn = null;
        boolean renglonInsertada = true;
        try {
            conn = getConnection();
            Workbook workbook = new HSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            /* Descarta el encabezado (Renglon 1) */
            rowIterator.next();
            int renglon = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                renglon++;
                /*
				 * Extrae la primer columna. Si esta vacia, salta a la siguiente
				 * fila
				 */
                Cell CellEP = row.getCell(0);
                Cell CellMonto = row.getCell(1);
                if (CellEP != null) {
                    log.debug("Object: {}", "Procesando renglon " + (renglon));
                    try {
                        String EP = CellEP.getStringCellValue();
                        Double Monto = CellMonto.getNumericCellValue();
                        renglonMap = new HashMap<String, String>();
                        renglonMap.put("EP", EP);
                        renglonMap.put("Monto", String.valueOf(Monto));
                        log.debug("Object: {}", "EP[" + EP + "] Monto[" + Monto);
                        CatalogoEPAnteproyectoPlurianualesManager.insertaRenglonEPAnteproyectoPlurianuales(conn, renglonMap);
                    } catch (Exception e) {
                        renglonInsertada = false;
                        errores.add("Error en renglon [" + renglon + "] " + e.toString());
                    }
                }
            }
            errores.addAll(CatalogoEPAnteproyectoPlurianualesManager.validacionEPPlurianual(conn));
            if (renglonInsertada && errores.size() == 0)
                conn.commit();
            else
                conn.rollback();
            workbook.close();
            return errores;
        } catch (Exception e) {
            log.error("Error insertando proyecto " + e, e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "No se pudo realizar rollback en conexion" + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
