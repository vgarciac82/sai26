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
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelacionEFederativaUEjecutoraBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(RelacionEFederativaUEjecutoraBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public RelacionEFederativaUEjecutoraBusinessLogic(String uLogin) {
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
    public List<String> cargaExcelRelacionEFederativaUEjecutora(String nombreDestino) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(nombreDestino);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + nombreDestino + "]");
            stream = new FileInputStream(f);
            return cargaExcelRelacionEFederativaUEjecutora(stream);
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
    public synchronized List<String> cargaExcelRelacionEFederativaUEjecutora(InputStream stream) throws Exception {
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
                Cell CellcEntidadFederativa = row.getCell(0);
                Cell CellcUnidadEjecutora = row.getCell(1);
                if (CellcEntidadFederativa != null) {
                    log.debug("Object: {}", "Procesando renglon " + (renglon));
                    try {
                        String cEntidadFederativa = CellcEntidadFederativa.getStringCellValue();
                        String cUnidadEjecutora = CellcUnidadEjecutora.getStringCellValue();
                        renglonMap = new HashMap<String, String>();
                        renglonMap.put("cEntidadFederativa", cEntidadFederativa);
                        renglonMap.put("cUnidadEjecutora", cUnidadEjecutora);
                        log.debug("Object: {}", "cEntidadFederativa[" + cEntidadFederativa + "] cUnidadEjecutora[" + cUnidadEjecutora);
                        RelacionEFederativaUEjecutoraManager.insertaRenglonEFederativaCartera(conn, renglonMap);
                    } catch (Exception e) {
                        renglonInsertada = false;
                        errores.add("Error en renglon [" + renglon + "] " + e.toString());
                    }
                }
            }
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
