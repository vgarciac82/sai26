package com.syc.gestion.reportes.anteproyecto;

/**
 * ************************Version 1.0 ****************************************************
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.syc.contable.core.AdecuacionManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.reportes.core.ReporteTomaDeDesicionManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logica de negocio para la generacion de reportes.
 *
 * @author Vicente Garcia Carrillo
 */
public class ReporteTomaDeDesicionBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteTomaDeDesicionBusinessLogic.class);

    /**
     * Construye una nueva instancia de este objeto.
     */
    public ReporteTomaDeDesicionBusinessLogic() {
        super();
    }

    public ReporteTomaDeDesicionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    /**
     * Realiza el congelamiento de la informacion presupuestal hasta el mes
     * anterior. Previo a la insercion, elimina el congelamiento anterior.
     *
     * @return Numero de registros insertados.
     * @throws Exception
     */
    public int congelaInformacion() throws Exception {
        Connection conn = null;
        int r = 0;
        try {
            conn = getConnection();
            r = ReporteTomaDeDesicionManager.congelaInformacion(conn);
            conn.commit();
            return r;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Genera un archivo excel con la informacion congelada.
     *
     * @return Referencia al archivo creado.
     * @throws Exception
     */
    public File generaArchivoInformacionCongelada() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String nombreArchivoDescarga = "InformacionCongelada" + "." + "csv";
            return ReporteTomaDeDesicionManager.generaArchivoCSVInformacionCongelada(conn, nombreArchivoDescarga);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Genera reporte por estructura economica.
     *
     * @param mesCorte
     *            mes al que se require el reporte
     * @param momentosPresupuestales
     *            Lista con los momentos presupuestales deseados.
     * @param capitulos
     *            Lista con los capitulos deseados
     * @return Referencia al archivo excel generado con el resultado del reporte
     */
    public File generaReporteEstructuraEconomica(String nombreArchivo, String mesCorte, String[] momentosPresupuestales, String[] capitulos) throws Exception {
        Connection conn = null;
        HSSFWorkbook wb = null;
        HSSFSheet hs = null;
        try {
            conn = getConnection();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            File salida = new File(nombreArchivo);
            wb = new HSSFWorkbook();
            hs = wb.createSheet();
            int rows = ReporteTomaDeDesicionManager.generaEncabezadoReporteEstructuraEconomica(wb, hs, mesCorte, momentosPresupuestales, ejercicioFiscal);
            ReporteTomaDeDesicionManager.generaCuerpoReporteEstructuraEconomica(conn, wb, hs, mesCorte, capitulos, momentosPresupuestales, ejercicioFiscal, rows);
            FileOutputStream fos = new FileOutputStream(salida);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            wb.write(bos);
            hs.autoSizeColumn(0);
            bos.flush();
            bos.close();
            fos.close();
            return salida;
        } finally {
            CloseObject.closeObject(conn, true);
        }
    }

    /**
     * Genera reporte por subfuncion y programa presupuestario.
     *
     * @param nombreArchivo
     *            Nombre del archivo a generar.
     * @param mesCorte
     *            Mes en el que se realiza el corte.
     * @param momentosPresupuestales
     *            Momentos presupuestales requeridos en el reporte. Simpre
     *            incluye el original y modificado anual
     * @param capitulos
     *            Capitulos a incluir en el reporte
     * @return Referencia al archivo generado
     * @throws Exception
     */
    public File generaReporteSubFuncion(String nombreArchivo, String mesCorte, String[] momentosPresupuestales, String[] capitulos) throws Exception {
        Connection conn = null;
        HSSFWorkbook wb = null;
        HSSFSheet hs = null;
        try {
            conn = getConnection();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            File salida = new File(nombreArchivo);
            wb = new HSSFWorkbook();
            hs = wb.createSheet();
            int rows = ReporteTomaDeDesicionManager.generaEncabezadoReporteSubfuncion(wb, hs, mesCorte, momentosPresupuestales, ejercicioFiscal);
            ReporteTomaDeDesicionManager.generaCuerpoReporteSubfuncion(conn, wb, hs, mesCorte, capitulos, momentosPresupuestales, ejercicioFiscal, rows);
            FileOutputStream fos = new FileOutputStream(salida);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            wb.write(bos);
            hs.autoSizeColumn(0);
            bos.flush();
            bos.close();
            fos.close();
            return salida;
        } finally {
            CloseObject.closeObject(conn, true);
        }
    }

    /**
     * Genera reporte por Unidad Ejecutora
     *
     * @param nombreArchivo
     *            Nombre del archivo a generar.
     * @param mesCorte
     *            Mes en el que se realiza el corte.
     * @param momentosPresupuestales
     *            Momentos presupuestales requeridos en el reporte. Simpre
     *            incluye el original y modificado anual
     * @param capitulos
     *            Capitulos a incluir en el reporte
     * @return Referencia al archivo generado
     * @throws Exception
     */
    public File generaReporteUE(String nombreArchivo, String mesCorte, String[] momentosPresupuestales, String[] capitulos) throws Exception {
        Connection conn = null;
        HSSFWorkbook wb = null;
        HSSFSheet hs = null;
        try {
            conn = getConnection();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            File salida = new File(nombreArchivo);
            wb = new HSSFWorkbook();
            hs = wb.createSheet();
            int rows = ReporteTomaDeDesicionManager.generaEncabezadoReporteUE(wb, hs, mesCorte, momentosPresupuestales, ejercicioFiscal);
            ReporteTomaDeDesicionManager.generaCuerpoReporteUE(conn, wb, hs, mesCorte, capitulos, momentosPresupuestales, ejercicioFiscal, rows);
            FileOutputStream fos = new FileOutputStream(salida);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            wb.write(bos);
            hs.autoSizeColumn(0);
            bos.flush();
            bos.close();
            fos.close();
            return salida;
        } finally {
            CloseObject.closeObject(conn, true);
        }
    }

    /**
     * Genera reporte por Unidad Normativa
     *
     * @param nombreArchivo
     *            Nombre del archivo a generar.
     * @param mesCorte
     *            Mes en el que se realiza el corte.
     * @param momentosPresupuestales
     *            Momentos presupuestales requeridos en el reporte. Simpre
     *            incluye el original y modificado anual
     * @param capitulos
     *            Capitulos a incluir en el reporte
     * @return Referencia al archivo generado
     * @throws Exception
     */
    public File generaReporteUN(String nombreArchivo, String mesCorte, String[] momentosPresupuestales, String[] capitulos) throws Exception {
        Connection conn = null;
        HSSFWorkbook wb = null;
        HSSFSheet hs = null;
        try {
            conn = getConnection();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            File salida = new File(nombreArchivo);
            wb = new HSSFWorkbook();
            hs = wb.createSheet();
            int rows = ReporteTomaDeDesicionManager.generaEncabezadoReporteUN(wb, hs, mesCorte, momentosPresupuestales, ejercicioFiscal);
            ReporteTomaDeDesicionManager.generaCuerpoReporteUN(conn, wb, hs, mesCorte, capitulos, momentosPresupuestales, ejercicioFiscal, rows);
            FileOutputStream fos = new FileOutputStream(salida);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            wb.write(bos);
            hs.autoSizeColumn(0);
            bos.flush();
            bos.close();
            fos.close();
            return salida;
        } finally {
            CloseObject.closeObject(conn, true);
        }
    }

    /**
     * Genera reporte por subfuncion y entidad federativa.
     *
     * @param nombreArchivo
     *            Nombre del archivo a generar.
     * @param mesCorte
     *            Mes en el que se realiza el corte.
     * @param momentosPresupuestales
     *            Momentos presupuestales requeridos en el reporte. Simpre
     *            incluye el original y modificado anual
     * @param capitulos
     *            Capitulos a incluir en el reporte
     * @return Referencia al archivo generado
     * @throws Exception
     */
    public File generaReporteEF(String nombreArchivo, String mesCorte, String[] momentosPresupuestales, String[] capitulos) throws Exception {
        Connection conn = null;
        HSSFWorkbook wb = null;
        HSSFSheet hs = null;
        try {
            conn = getConnection();
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            File salida = new File(nombreArchivo);
            wb = new HSSFWorkbook();
            hs = wb.createSheet();
            int rows = ReporteTomaDeDesicionManager.generaEncabezadoReporteEF(wb, hs, mesCorte, momentosPresupuestales, ejercicioFiscal);
            ReporteTomaDeDesicionManager.generaCuerpoReporteEntidadFederativa(conn, wb, hs, mesCorte, capitulos, momentosPresupuestales, ejercicioFiscal, rows);
            FileOutputStream fos = new FileOutputStream(salida);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            wb.write(bos);
            hs.autoSizeColumn(0);
            bos.flush();
            bos.close();
            fos.close();
            return salida;
        } finally {
            CloseObject.closeObject(conn, true);
        }
    }
}
