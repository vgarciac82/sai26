package com.syc.contable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.core.IAnteProyectoManager;
import com.syc.contable.core.IAnteproyectoEncabezado;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class IAnteProyectoBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(IAnteProyectoBusinessLogic.class);

    private String jniName = "";

    public IAnteProyectoBusinessLogic(String jniName) {
        this.jniName = jniName;
        super.init(jniName);
    }

    public List<String> cargaExcelValidacionAnteProyecto(String nombreDestino, int folio) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(nombreDestino);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + nombreDestino + "]");
            stream = new FileInputStream(f);
            return cargaExcelValidacionAnteProyecto(stream, folio);
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
    public synchronized List<String> cargaExcelValidacionAnteProyecto(InputStream stream, int folio) throws Exception {
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
                Cell Cellconsecutivo = row.getCell(0);
                Cell CellEP = row.getCell(1);
                Cell CellMCalculado = row.getCell(2);
                Cell CellMOptimo = row.getCell(3);
                Cell CellMIreductible = row.getCell(4);
                if (Cellconsecutivo != null) {
                    log.debug("Object: " + String.valueOf("Procesando renglon " + (renglon)));
                    try {
                        String nFolioAnteProyecto = String.valueOf(folio);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
                        int consecutivo = (int) Cellconsecutivo.getNumericCellValue();
                        String consecutivoS = String.valueOf(consecutivo);
                        String EP = CellEP.getStringCellValue();
                        Double MCalculado = CellMCalculado.getNumericCellValue();
                        String MCalculadoS = formatter.format(MCalculado);
                        Double MOptimo = CellMOptimo.getNumericCellValue();
                        String MOptimoS = formatter.format(MOptimo);
                        Double MIreductible = CellMIreductible.getNumericCellValue();
                        String MIreductibleS = formatter.format(MIreductible);
                        renglonMap = new HashMap<String, String>();
                        renglonMap.put("nFolioAnteProyecto", nFolioAnteProyecto);
                        renglonMap.put("consecutivo", consecutivoS);
                        renglonMap.put("EP", EP);
                        renglonMap.put("MCalculado", MCalculadoS);
                        renglonMap.put("MOptimo", MOptimoS);
                        renglonMap.put("MIreductible", MIreductibleS);
                        log.debug("Object: " + String.valueOf("nFolioAnteProyecto[" + nFolioAnteProyecto + "]consecutivo[" + consecutivoS + "] EP[" + EP + "]MCalculado[" + MCalculadoS + "]MOptimo[" + MOptimoS + "]MIreductible[" + MIreductibleS));
                        IAnteProyectoManager.insertaRenglonValidacionAnteProyecto(conn, renglonMap);
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

    public void insertaIAnteproyectoEncabezado(IAnteproyectoEncabezado enc) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            IAnteProyectoManager.insertaIAnteproyectoEncabezado(conn, enc);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public List<String> validacionAnteProyectoCatalogos(int folio) throws Exception {
        Connection conn = null;
        List<String> r = null;
        try {
            conn = getConnection();
            r = IAnteProyectoManager.validacionAnteProyectoCatalogos(conn, folio);
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public List<String> validacionAnteProyectoDuplicidad(int folio) throws Exception {
        Connection conn = null;
        List<String> r = null;
        try {
            conn = getConnection();
            r = IAnteProyectoManager.validacionAnteProyectoDuplicidad(conn, folio);
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public List<String> validacionAnteProyectoUR(int folio, String UR) throws Exception {
        Connection conn = null;
        List<String> r = null;
        try {
            conn = getConnection();
            r = IAnteProyectoManager.validacionAnteProyectoUR(conn, folio, UR);
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public String integrado(String UR, String grupoInt, int id_oper) throws Exception {
        Connection conn = null;
        String r = "";
        try {
            conn = getConnection();
            r = IAnteProyectoManager.integrado(conn, UR, grupoInt, id_oper);
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public void borraTCorrida(int folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            IAnteProyectoManager.borraTCorrida(conn, folio);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void insertaIAnteproyectoValidado(int folio) throws Exception, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            IAnteProyectoManager.insertaIAnteproyectoValidado(conn, folio);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void insertaIntegraCaptura(int folioInt, int folioCap) throws Exception, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            IAnteProyectoManager.insertaIntegraCaptura(conn, folioInt, folioCap);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public Caso dispersaIntegracionUN(int nFolio, Usuario u, FolioGeneratorInterface fg, String jniName, IAnteproyectoEncabezado enc, String fCarga) throws Exception, SQLException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = IAnteProyectoManager.dispersaIntegracionUN(conn, nFolio, u, fg, jniName, enc, fCarga);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return c;
    }
}
