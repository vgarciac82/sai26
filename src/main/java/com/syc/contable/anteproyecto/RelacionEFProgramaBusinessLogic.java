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

public class RelacionEFProgramaBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(RelacionEFProgramaBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public RelacionEFProgramaBusinessLogic(String uLogin) {
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
    public List<String> cargaExcelRelacionEFPrograma(String nombreDestino) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(nombreDestino);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + nombreDestino + "]");
            stream = new FileInputStream(f);
            return cargaExcelRelacionEFPrograma(stream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e2) {
                    log.warn("No se pudo cerrar el archivo de carga." + e2);
                }
            stream = null;
        }
    }

    @SuppressWarnings("null")
    public synchronized List<String> cargaExcelRelacionEFPrograma(InputStream stream) throws Exception {
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
                Cell CellcgrupoFuncional = row.getCell(0);
                Cell CellcFuncion = row.getCell(1);
                Cell CellcSubFuncion = row.getCell(2);
                Cell CellcProgramaGeneral = row.getCell(3);
                Cell CellcActividadInstitucional = row.getCell(4);
                Cell CellcProgramaPresupuestario = row.getCell(5);
                if (CellcgrupoFuncional != null) {
                    log.debug("Procesando renglon " + (renglon));
                    try {
                        String cgrupoFuncional = CellcgrupoFuncional.getStringCellValue();
                        String cFuncion = CellcFuncion.getStringCellValue();
                        String cSubFuncion = CellcSubFuncion.getStringCellValue();
                        String cProgramaGeneral = CellcProgramaGeneral.getStringCellValue();
                        String cActividadInstitucional = CellcActividadInstitucional.getStringCellValue();
                        String cProgramaPresupuestario = CellcProgramaPresupuestario.getStringCellValue();
                        renglonMap = new HashMap<String, String>();
                        renglonMap.put("cgrupoFuncional", cgrupoFuncional);
                        renglonMap.put("cFuncion", cFuncion);
                        renglonMap.put("cSubFuncion", cSubFuncion);
                        renglonMap.put("cProgramaGeneral", cProgramaGeneral);
                        renglonMap.put("cActividadInstitucional", cActividadInstitucional);
                        renglonMap.put("cProgramaPresupuestario", cProgramaPresupuestario);
                        log.debug("cgrupoFuncional[" + cgrupoFuncional + "] cFuncion[" + cFuncion + "]cSubFuncion[" + cSubFuncion + "]cProgramaGeneral[" + cProgramaGeneral + "]cActividadInstitucional[" + cActividadInstitucional + "]cProgramaPresupuestario[" + cProgramaPresupuestario + "]");
                        RelacionEFProgramaManager.insertaRenglonRelacionEFPrograma(conn, renglonMap);
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
                    log.warn("No se pudo realizar rollback en conexion" + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
