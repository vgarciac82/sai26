package com.syc.contable.anteproyecto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.servlet.DescargaExcelProyectoServlet;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Clase encargada de la carga de proyecto desde un archivo excel.
 *
 * @author Vicente Garcia Carrillo
 * @version 1.0
 */
public class CargaProyectoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CargaProyectoBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public CargaProyectoBusinessLogic(String uLogin) {
        super.init();
        this.uLoginCarga = uLogin;
    }

    /**
     * Indica si se han cargado las partidas de capitulos restringidos.
     *
     * @param u
     *            usuario
     * @return true si y solo si se han capturado EPs con capitulos
     *         restringidos.
     */
    public boolean calendarioCapitulosRestringidosCargado(Usuario u) throws Exception {
        Connection conn = null;
        boolean calendarioCargado = false;
        try {
            conn = getConnection();
            boolean esAdministrador = u.getRole("ADMIN_PRESUPUESTO") != null;
            String ue = u.getU_UR();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            calendarioCargado = CargaProyectoManager.calendarioCapitulosRestringidosCargado(conn, esAdministrador, ue, DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS, ejercicioFiscal);
            return calendarioCargado;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Indica si se han cargado las EPs de una unidad ejecutora.
     *
     * @param u
     *            usuario
     * @return true si y solo si se han capturado EPs de una unidad ejecutora.
     */
    public boolean calendarioUECargado(Usuario u) throws Exception {
        Connection conn = null;
        boolean calendarioCargado = false;
        try {
            conn = getConnection();
            boolean esAdministrador = u.getRole("ADMIN_PRESUPUESTO") != null;
            String ue = u.getU_UR();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            if (esAdministrador)
                calendarioCargado = CargaProyectoManager.calendarioCapitulosRestringidosCargado(conn, esAdministrador, ue, DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS, ejercicioFiscal) & CargaProyectoManager.calendarioUECargado(conn, esAdministrador, ue, DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS, ejercicioFiscal);
            else
                calendarioCargado = CargaProyectoManager.calendarioUECargado(conn, esAdministrador, ue, DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS, ejercicioFiscal);
            return calendarioCargado;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public synchronized List<String> cargaExcelCalendario(InputStream stream, boolean esAdmin, String UE, int[] capitulosRestringidos) throws Exception {
        List<String> errores = new ArrayList<String>();
        Connection conn = null;
        boolean renglonCorrecto = true;
        boolean renglonInsertada = true;
        try {
            conn = getConnection();
            Workbook workbook = new HSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
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
                Cell cellEP = row.getCell(0);
                if (cellEP != null) {
                    log.debug("Object: " + String.valueOf("Procesando renglon " + (renglon)));
                    try {
                        /*
						 * Desagrega la ep. Si algo sale mal desagregandola
						 * enviara el error
						 */
                        String ep = cellEP.getStringCellValue();
                        Map<String, String> epMap = EPManager.desagregaEP(conn, ep, true);
                        List<String> mensajes = EPManager.validaEPCalendario(conn, ejercicioFiscal, esAdmin, UE, capitulosRestringidos, epMap, row);
                        renglonCorrecto = renglonCorrecto & !(mensajes.size() > 0);
                        if (!renglonCorrecto)
                            errores.addAll(mensajes);
                        else {
                            EPManager.agregaCalendario(epMap, row);
                            CargaProyectoManager.insertaRenglonCalendario(conn, epMap);
                        }
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

    /**
     * Carga el calendario de proyecto dada la ruta de un archivo excel y lo
     * almacena en la base de datos. <br>
     * Consideraciones.<br>
     * El excel solo contiene una hoja.<br>
     * El excel sera formato 97-2003 <br>
     * El excel esta conformado por catorce columnas:<br>
     * Posicion: 01 Elemento: EP. Contiene la EP condensada <br>
     * Posicion: 02 Elemento: Monto Anual . Contiene el monto para la EP
     * Posicion: 03 Elemento: Monto Enero . Contiene el monto para la EP
     * Posicion: 04 Elemento: Monto Febrero . Contiene el monto para la EP
     * Posicion: 05 Elemento: Monto Marzo . Contiene el monto para la EP
     * Posicion: 06 Elemento: Monto Abril . Contiene el monto para la EP
     * Posicion: 07 Elemento: Monto Mayo . Contiene el monto para la EP
     * Posicion: 08 Elemento: Monto Junio . Contiene el monto para la EP
     * Posicion: 09 Elemento: Monto Julio . Contiene el monto para la EP
     * Posicion: 10 Elemento: Monto Agosto . Contiene el monto para la EP
     * Posicion: 11 Elemento: Monto Septiembre . Contiene el monto para la EP
     * Posicion: 12 Elemento: Monto Octubre . Contiene el monto para la EP
     * Posicion: 13 Elemento: Monto Noviembre . Contiene el monto para la EP
     * Posicion: 14 Elemento: Monto Diciembre . Contiene el monto para la EP
     *
     * @param nombreDestino
     *            Nombre del archivo destino
     * @return Lista de mensajes de error de existir
     * @throws Exception
     */
    public List<String> cargaExcelCalendario(String nombreDestino, boolean esAdmin, String UE, int[] capitulosRestringidos) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(nombreDestino);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + nombreDestino + "]");
            stream = new FileInputStream(f);
            return cargaExcelCalendario(stream, esAdmin, UE, capitulosRestringidos);
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

    /**
     * Carga el proyecto desde un flujo de datos abierto y lo almacena en la
     * base de datos. <br>
     * Consideraciones.<br>
     * El excel solo contiene una hoja.<br>
     * El excel sera formato 97-2003 El excel esta conformado por solo dos
     * columnas:<br>
     * Posicion: 1 Elemento: EP. Contiene la EP condensada <br>
     * Posicion: 2 Elemento: Monto. Contiene el monto para la EP
     *
     * @param stream
     *            Flujo de entrada desde el anteproyecto.
     * @return Lista con los errores encontrados.
     * @throws Exception
     *             Si ocurre un error en la lectura del archivo excel
     */
    public synchronized List<String> cargaExcelProyecto(InputStream stream) throws Exception {
        List<String> errores = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            CargaProyectoManager.limpiaProyecto(conn);
            Workbook workbook = new HSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();
            int renglon = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell cellEP = row.getCell(0);
                Cell cellMonto = row.getCell(1);
                if (cellEP != null) {
                    log.debug("Object: " + String.valueOf("Procesando renglon " + (renglon++)));
                    String ep = cellEP.getStringCellValue();
                    Map<String, String> epMap = EPManager.desagregaEP(conn, ep);
                    double monto = cellMonto.getNumericCellValue();
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.getDefault());
                    String montoFormateado = fmt.format(monto);
                    epMap.put("EP", ep);
                    epMap.put("MONTO", montoFormateado);
                    log.debug("Object: " + String.valueOf("EP[" + ep + "] Monto[" + montoFormateado + "]"));
                    CargaProyectoManager.insertaRenglonProyecto(conn, epMap);
                }
            }
            CargaProyectoManager.insertaVersionProyecto(conn, Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn)), uLoginCarga);
            if (errores.size() == 0)
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

    /**
     * Carga el proyecto dada la ruta de un archivo excel y lo almacena en la
     * base de datos. <br>
     * Consideraciones.<br>
     * El excel solo contiene una hoja.<br>
     * El excel sera formato 97-2003 El excel esta conformado por solo dos
     * columnas:<br>
     * Posicion: 1 Elemento: EP. Contiene la EP condensada <br>
     * Posicion: 2 Elemento: Monto. Contiene el monto para la EP
     *
     * @param stream
     *            Flujo de entrada desde el anteproyecto.
     * @return Lista con los errores encontrados.
     * @throws Exception
     *             Si ocurre un error en la lectura del archivo excel
     */
    public List<String> cargaExcelProyecto(String file) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(file);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + file + "]");
            stream = new FileInputStream(f);
            return cargaExcelProyecto(stream);
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

    /**
     * Genera el archivo de calendario para su edicion por el usuario. Si es un
     * administrador se descarga su unidad ejecutora mas las restricciones.Si se
     * trata de un usuario de unidad ejecutora solo descargara su unidad
     * ejecutora.
     *
     * @param esAdministrador
     *            Indica si la descarga es de un usuario administrador.
     * @param ue
     *            Unidad Responsable/Ejecutora del usuario que descarga-
     * @param ejercicioFiscal
     *            Ejercicio fiscal a descargar
     * @param capitulosExcluir
     *            Lista de capitulos a excluir.
     * @return Referencia al archivo generado.
     * @throws Exception
     */
    public File generaArchivoCalendarioProyecto(boolean esAdministrador, String ue, int ejercicioFiscal, int[] capitulosExcluir) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String nombreArchivoDescarga = "PROYECTO_" + ejercicioFiscal + "_" + ue + "." + "xls";
            return CargaProyectoManager.generaArchivoCalendarioProyecto(conn, esAdministrador, ue, ejercicioFiscal, capitulosExcluir, nombreArchivoDescarga);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Genera el archivo de proyecto para su descarga y consulta. Si es un
     * administrador se descarga completo. Si es un usuario de unidad normativa
     * se descargaran todas sus unidades ejecutoras sin tomar en cuenta aquellas
     * subcuentas especificadas en el arreglo. Si se trata de un usuario de
     * unidad ejecutora solo descargara su unidad ejecutora.
     *
     * @param esAdministrador
     *            Indica si la descarga es de un usuario administrador.
     * @param ur
     *            Unidad Responsable/Ejecutora del usuario que descarga-
     * @param ejercicioFiscal
     *            Ejercicio fiscal a descargar
     * @param capitulosExcluir
     *            Lista de capitulos a excluir.
     * @return Referencia al archivo generado.
     * @throws Exception
     */
    public File generaArchivoProyecto(boolean esAdministrador, String ur, int ejercicioFiscal, int[] capitulosExcluir) throws Exception {
        int numeroUR = Integer.parseInt(ur.substring(1));
        Connection conn = null;
        try {
            conn = getConnection();
            String nombreArchivoDescarga = "PROYECTO_" + ejercicioFiscal + (esAdministrador ? "" : "_" + ur) + "." + "xls";
            if (esAdministrador)
                return CargaProyectoManager.generaArchivoProyecto(conn, esAdministrador, ur, ejercicioFiscal, capitulosExcluir, nombreArchivoDescarga);
            else if (numeroUR >= 0 && numeroUR <= 15)
                return generaArchivoProyectoPorUN(ur, nombreArchivoDescarga, ejercicioFiscal, capitulosExcluir);
            else
                return CargaProyectoManager.generaArchivoProyecto(conn, esAdministrador, ur, ejercicioFiscal, capitulosExcluir, nombreArchivoDescarga);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Genera el archivo de proyecto para su descarga y consulta. Se descargaran
     * todas sus unidades ejecutoras sin tomar en cuenta aquellas subcuentas
     * especificadas en el arreglo.
     *
     * @param un
     *            Unidad Normativa
     * @param nombreArchivo
     *            Nombre del archivo temporal
     * @param ejercicioFiscal
     *            Ejercicio fiscal a descargar
     * @param partidasExcluir
     *            Lista de capitulos a excluir
     * @return Referencia al archivo generado.
     * @throws Exception
     */
    public File generaArchivoProyectoPorUN(String un, String nombreArchivo, int ejercicioFiscal, int[] partidasExcluir) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return CargaProyectoManager.generaArchivoProyectoPorUN(conn, un, nombreArchivo, ejercicioFiscal, partidasExcluir);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Genera en una cadena los capitulos restringidos separados por el caracter
     * de control |.
     *
     * @return cadena separada por | que incluye los capitulos restringidos.
     */
    public String generaCadenaPartidasRestringidas() {
        String capitulos = "";
        String token = "";
        int[] capitulosRestingidos = DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS;
        for (int i = 0; i < capitulosRestingidos.length; i++) {
            capitulos += token + String.valueOf(capitulosRestingidos[i]);
            token = "|";
        }
        return capitulos;
    }

    /**
     * Genera un archivo con el total del Proyecto Calendarizado al momento.
     *
     * @param unidadEjecutora
     *            Unidad Ejecutora
     * @param unidadNormativa
     *            Unidad Normativa
     * @param ejercicioFiscal
     *            Ejercicio Fiscal
     * @return Referencia al archivo creado.
     * @throws Exception
     */
    public File generaArchivoCalendarioProyectoFinal(String unidadEjecutora, String unidadNormativa, int ejercicioFiscal) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String nombreArchivoDescarga = "CALENDARIO_PROYECTO_" + ejercicioFiscal + ((unidadEjecutora != null && !"".equals(unidadEjecutora)) ? "_" + unidadEjecutora : "") + ((unidadNormativa != null && !"".equals(unidadNormativa)) ? "_" + unidadNormativa : "") + "." + "xls";
            return CargaProyectoManager.generaArchivoCalendarioProyectoFinal(conn, unidadEjecutora, unidadNormativa, ejercicioFiscal, nombreArchivoDescarga);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
