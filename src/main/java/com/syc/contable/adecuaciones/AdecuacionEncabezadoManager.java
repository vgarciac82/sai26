/**
 * Manager para el encabezado de una adecuacion.
 * Version 1.0
 * 17/03/2014
 * Vicente Garcia C.
 */
package com.syc.contable.adecuaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import com.axtel.presupuesto.AdecuacionDetalleResumen;
import com.axtel.presupuesto.AdecuacionEncabezadoResumen;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase manager del encabezado de una adecuacion.
 *
 * @author Vicente Garcia
 * @version 1.0
 */
public class AdecuacionEncabezadoManager {

    private static final Logger log = LoggerFactory.getLogger(AdecuacionEncabezadoManager.class);

    /**
     * Lee y valida los elementos del encabezado desde el archivo excel de la
     * adecuacion. Los datos se esperan en el orden:
     * <table border="1">
     * <tr>
     * <td>LOGIN</td>
     * <td>EJERCICIO</td>
     * <td>CLAVE</td>
     * <td>UNIDAD ADMINISTRATIVA</td>
     * <td>CLAVE AFECTACION</td>
     * <td>RAMO</td>
     * <td>MONTO TOTAL</td>
     * </tr>
     * <tr>
     * <td>Texto</td>
     * <td>Numero</td>
     * <td>Texto</td>
     * <td>Texto</td>
     * <td>Numero</td>
     * <td>Texto</td>
     * <td>Numero</td>
     * </tr>
     * </table>
     *
     * @param wb
     *            Hoja de excel
     * @param hoja
     *            Hoja abierta
     * @param usuario
     *            Usuario que solicita la adecuacion
     * @param nFolio
     *            Folio de la adecuacion-
     * @param superReduccion
     *            indica si es una super reduccion
     * @param SRInterna
     *            Indica si es una super reduccion interna.
     * @param nRenglonCuerpo
     *            Renglon en el que inicia el cuerpo
     * @return Objeto que contiene los elementos del encabezado
     * @throws Exception
     *             Si falta algun dato o su formato es incompatible con el
     *             esperado.
     * @param nRenglonCuerpo
     * @return
     * @throws Exception
     */
    public static AdecuacionEncabezado readFromExcel(int aEjercicio, Workbook wb, HSSFSheet hoja, String usuario, int nFolio, boolean superReduccion, boolean SRInterna, int nRenglonCuerpo) throws Exception {
        int contadorRenglones = -1;
        String mensaje = "";
        AdecuacionEncabezado encabezado;
        String uLogin = "";
        int ejercicioFiscal = -1;
        String unidadEjecutora = "";
        String ramo = "";
        double montoTotal = -1.0d;
        int claveAfectacion = 0;
        String justificacion = "";
        try {
            for (Iterator<Row> i = hoja.iterator(); i.hasNext(); ) {
                contadorRenglones++;
                Row r = i.next();
                if (contadorRenglones == nRenglonCuerpo) {
                    try {
                        if (r.getCell(0).getCellType() == CellType.BLANK)
                            mensaje += "\\nNo se capturo en el archivo Excel el usuario";
                        else
                            uLogin = r.getCell(0).getStringCellValue();
                    } catch (Exception e) {
                        log.error("Ocurrio el siguiente error cuando se intentaba leer el usuario: " + e, e);
                        mensaje += "\\nOcurrio el siguiente error cuando se intentaba leer el usuario: " + e;
                    }
                    if (!SRInterna && (uLogin != null && !"".equals(uLogin))) {
                        if (!usuario.equalsIgnoreCase(uLogin.trim())) {
                            mensaje += "\nError: El Usuario del sistema (" + usuario + ") no coincide con el del archivo (" + uLogin + "). ";
                        }
                    }
                    try {
                        if (r.getCell(1).getCellType() == CellType.BLANK)
                            mensaje += "\\nNo se capturo en el archivo Excel el ejercicio fiscal";
                        else
                            ejercicioFiscal = (int) r.getCell(1).getNumericCellValue();
                        if (aEjercicio != ejercicioFiscal)
                            mensaje += "\\nEl ejercicio fiscal en el archivo [" + ejercicioFiscal + "] no coincide con el ejercicio fiscal actual [" + aEjercicio + "]";
                    } catch (Exception e) {
                        log.warn("No fue posible leer el ejercicio como numero. Se intenta como texto" + e, e);
                        try {
                            ejercicioFiscal = Integer.parseInt(r.getCell(1).getStringCellValue());
                        } catch (Exception e2) {
                            log.error("Error leyendo ejercicio fiscal en excel de adecuaciones: " + e2, e2);
                            mensaje += "\\nNo fue posible leer el ejercicio fiscal en el archivo. Debe ser numero entero. Por ejemplo 2015";
                        }
                    }
                    try {
                        if (r.getCell(2).getCellType() == CellType.BLANK)
                            mensaje += "\\nNo se capturo en el archivo Excel la unidad ejecutora";
                        else
                            unidadEjecutora = r.getCell(2).getStringCellValue();
                    } catch (Exception e) {
                        log.error("Ocurrio el siguiente error cuando se intentaba leer la unidad ejecutora: " + e, e);
                        mensaje += "\\nOcurrio el siguiente error cuando se intentaba leer la unidad ejecutora: " + e;
                    }
                    try {
                        if (r.getCell(3).getCellType() == CellType.BLANK)
                            claveAfectacion = 0;
                        else
                            claveAfectacion = (int) r.getCell(3).getNumericCellValue();
                    } catch (Exception e) {
                        log.warn("No fue posible leer la claveAfectacion como numero. Se intenta como texto" + e, e);
                        try {
                            claveAfectacion = Integer.parseInt(r.getCell(3).getStringCellValue());
                        } catch (Exception e2) {
                            log.error("Error leyendo clave de adecuacion: " + e2, e2);
                            claveAfectacion = 0;
                        }
                    }
                    try {
                        if (r.getCell(4).getCellType() == CellType.BLANK)
                            mensaje += "\\nNo se capturo en el archivo Excel el ramo.";
                        else if (r.getCell(4).getCellType() == CellType.NUMERIC)
                            ramo = String.valueOf((int) r.getCell(4).getNumericCellValue());
                        else if (r.getCell(4).getCellType() == CellType.STRING)
                            ramo = r.getCell(4).getStringCellValue();
                        else
                            throw new Exception("El ramo se especifico en un formato incompatible. Se espera numero entero.");
                    } catch (Exception e) {
                        log.error("Ocurrio el siguiente error cuando se intentaba leer el ramo: " + e, e);
                        mensaje += "\\nOcurrio el siguiente error cuando se intentaba leer el ramo: " + e;
                    }
                    try {
                        if (CellType.BLANK == r.getCell(5).getCellType())
                            mensaje += "\\nNo se capturo en el archivo Excel el monto total.";
                        else if (CellType.NUMERIC == r.getCell(5).getCellType())
                            // montoTotal = new BigDecimal(
                            // r.getCell(5).getNumericCellValue() );
                            montoTotal = r.getCell(5).getNumericCellValue();
                        else if (CellType.STRING == r.getCell(5).getCellType())
                            // montoTotal = new BigDecimal(
                            // r.getCell(5).getStringCellValue() );
                            montoTotal = Double.parseDouble(r.getCell(5).getStringCellValue());
                        else if (CellType.FORMULA == r.getCell(5).getCellType()) {
                            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
                            CellValue cellValue = evaluator.evaluate(r.getCell(5));
                            try {
                                // montoTotal = new BigDecimal(
                                // cellValue.getNumberValue() );
                                montoTotal = cellValue.getNumberValue();
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                mensaje += "\\nSe especifico una formula para el monto total. Sin embargo esta no se puede evaluar " + e;
                            }
                        } else
                            mensaje += "\\nSe especifico el monto total en un formato incompatible ";
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                        mensaje += "\\nOcurrio el siguiente error mientras se leia el monto total el archivo excel: " + ex.toString();
                    }
                } else if (contadorRenglones == nRenglonCuerpo + 2) {
                    try {
                        if (r.getCell(0).getCellType() == CellType.BLANK)
                            mensaje += "\\nNo se capturo la justificacion.";
                        if (r.getCell(0).getCellType() == CellType.STRING) {
                            justificacion = r.getCell(0).getStringCellValue();
                            if (justificacion == null || "".equals(justificacion.trim()))
                                mensaje += "\\nNo se capturo la justificacion.";
                        }
                    } catch (Exception e) {
                        mensaje += "\\nOcurrio el siguiente error mientras se leia la justificacion " + e;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            mensaje += "\\nOcurrio el siguiente error mientras se leia el encabezado del archivo excel: " + e.toString();
        }
        if ("".equals(mensaje)) {
            encabezado = new AdecuacionEncabezado(uLogin, ejercicioFiscal, unidadEjecutora, claveAfectacion, ramo, montoTotal, justificacion);
            return encabezado;
        } else {
            Exception e = new Exception(mensaje);
            throw e;
        }
    }

    /**
     * Inserta el encabezado de una adecuacion.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param adecuacionEncabezado
     *            encabezado a insertar
     * @return Numero de elementos insertados
     * @throws Exception
     */
    public static int insertaAdecuacionEncabezado(Connection conn, AdecuacionEncabezado adecuacionEncabezado) throws Exception {
        PreparedStatement pstmnt = null;
        int retval = -1;
        String query = "INSERT INTO tAdecuacionEncabezado (nFolioAdecuacion,fCarga,nNivel,cRamo,cUnidadResponsable,cRevisado,aEjercicioFiscal, cJustificacion, U_LOGIN,cTipoPoliza,fAplicacion,cdescripcionpoliza, cTipoAdecuacion, cSuperAdecuacion, id_caso, cSRInterna)  " + "VALUES (?,?,?,?,?,?,?,replace(?,'%',' porciento '),?,?,?,?,?,?,?,?)";
        log.info("Object: {}", "Insertando encabezado de la adecuacion con el numero de folio " + adecuacionEncabezado.getnFolioAdecuacion());
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, adecuacionEncabezado.getnFolioAdecuacion());
            pstmnt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmnt.setInt(3, adecuacionEncabezado.getnNivel());
            pstmnt.setString(4, adecuacionEncabezado.getRamo());
            pstmnt.setString(5, adecuacionEncabezado.getUnidadEjecutora());
            pstmnt.setString(6, "0");
            pstmnt.setInt(7, adecuacionEncabezado.getEjercicioFiscal());
            pstmnt.setString(8, adecuacionEncabezado.getJustificacion());
            pstmnt.setString(9, adecuacionEncabezado.getuLogin());
            pstmnt.setString(10, "PR");
            pstmnt.setDate(11, adecuacionEncabezado.getfAplicacion());
            pstmnt.setString(12, "ADECUACION PRESUPUESTAL FOLIO " + adecuacionEncabezado.getnFolioAdecuacion());
            pstmnt.setString(13, adecuacionEncabezado.getcTipoAdecuacion());
            pstmnt.setString(14, "NO");
            pstmnt.setInt(15, adecuacionEncabezado.getId_caso());
            pstmnt.setString(16, "NO");
            retval = pstmnt.executeUpdate();
            return retval;
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
    }

    /**
     * Devuelve el encabezado de una adecuacion alamacenada en la base de datos.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param nFolioAdecuacion
     *            Folio de la adecuacion a cargar
     * @return Encabezado de la adecuacion
     * @throws Exception
     */
    public static AdecuacionEncabezado readAdecuacionEncabezado(Connection conn, int nFolioAdecuacion) throws Exception {
        String query = "SELECT nfolioadecuacion, " + "       fcarga, " + "       ctipoadecuacion, " + "       faplicacion, " + "       cramo, " + "       cunidadresponsable, " + "       cdocumentohaplicado, " + "       nfoliopoliza, " + "       ctipopoliza, " + "       cmes, " + "       crevisado, " + "       aejerciciofiscal, " + "       nfolioconsolidacion, " + "       cjustificacion, " + "       u_login, " + "       nfoliotramitesicop, " + "       nfoliotramitemap, " + "       cunidadresponsablecontable, " + "       nfoliopolizacancelacion, " + "       fcancelacion, " + "       cdescripcionpoliza, " + "       nnivel, " + "       nconsecutivosicop, " + "       csuperadecuacion, " + "       id_caso, " + "       csrinterna, " + "       nfoliofiaf, " + "       justificaciona, " + "       justificacionr, " + "       justificacionn, " + "       alertacorreo, " + "       cmotivocancelacion, " + "       fsicop, " + "       fmap " + " FROM   tadecuacionencabezado " + " WHERE nFolioAdecuacion = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        AdecuacionEncabezado encabezado = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioAdecuacion);
            rs = ps.executeQuery();
            if (rs.next()) {
                encabezado = new AdecuacionEncabezado();
                encabezado.setAlertaCorreo(rs.getInt("alertaCorreo"));
                encabezado.setcDescripcionPoliza(rs.getString("cDescripcionPoliza"));
                encabezado.setcDocumentoHaplicado(rs.getString("cDocumentoHaplicado"));
                encabezado.setcMes(rs.getInt("cMes"));
                encabezado.setcMotivoCancelacion(rs.getString("cMotivoCancelacion"));
                encabezado.setcRevisado(rs.getString("cRevisado"));
                encabezado.setcSRInterna(rs.getString("cSRInterna"));
                encabezado.setcSuperAdecuacion(rs.getString("cSuperAdecuacion"));
                encabezado.setcTipoAdecuacion(rs.getString("cTipoAdecuacion"));
                encabezado.setcTipoPoliza(rs.getString("cTipoPoliza"));
                encabezado.setEjercicioFiscal(rs.getInt("aEjercicioFiscal"));
                encabezado.setfAplicacion(rs.getDate("fAplicacion"));
                encabezado.setfCancelacion(rs.getDate("fCancelacion"));
                encabezado.setfCarga(rs.getDate("fCarga"));
                encabezado.setFMAP(rs.getDate("FMAP"));
                encabezado.setFSICOP(rs.getDate("FSICOP"));
                encabezado.setId_caso(rs.getInt("id_caso"));
                encabezado.setJustificacion(rs.getString("cJustificacion"));
                encabezado.setJustificacionA(rs.getString("justificacionA"));
                encabezado.setJustificacionN(rs.getString("justificacionN"));
                encabezado.setJustificacionR(rs.getString("justificacionR"));
                encabezado.setnConsecutivoSicop(rs.getInt("nConsecutivoSicop"));
                encabezado.setnFolioAdecuacion(rs.getInt("nFolioAdecuacion"));
                encabezado.setnFolioConsolidacion(rs.getInt("nFolioConsolidacion"));
                encabezado.setnFolioFIAF(rs.getInt("nFolioFIAF"));
                encabezado.setnFolioPoliza(rs.getInt("nFolioPoliza"));
                encabezado.setnFolioPolizaCancelacion(rs.getInt("nFolioPolizaCancelacion"));
                encabezado.setnFolioTramiteMAP(rs.getString("nFolioTramiteMAP"));
                encabezado.setnFolioTramiteSicop(rs.getString("nFolioTramiteSicop"));
                encabezado.setnNivel(rs.getInt("nNivel"));
                encabezado.setRamo(rs.getString("cRamo"));
                encabezado.setuLogin(rs.getString("U_LOGIN"));
                encabezado.setUnidadEjecutora(rs.getString("cUnidadResponsable"));
            }
            return encabezado;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    /**
     * Borra el encabezado de una adecuacion. Primero debe eliminarse el detalle
     *
     * @param conn
     *            COnexion activa a la base de datos
     * @param nFolioAdecuacion
     *            Folio de la adecuacion.
     * @return Numero de registros eliminados.
     * @throws Exception
     */
    public static int deleteAdecuacionEncabezado(Connection conn, int nFolioAdecuacion) throws Exception {
        String query = "DELETE FROM tAdecuacionEncabezado WHERE nFolioAdecuacion = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioAdecuacion);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static AdecuacionEncabezadoResumen readAdecuacionResumenEncabezado(Connection conn, int folioAdecuacion) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT nfolioadecuacion AS folio, ");
        query.append("       cjustificacion   AS justificacion, ");
        query.append("       CONVERT(VARCHAR(32), faplicacion,103)      AS fAplicacion ");
        query.append("FROM   tadecuacionencabezado  WITH(NOLOCK)");
        query.append("WHERE  nfolioadecuacion = ?  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioAdecuacion);
            log.debug("Object: {}", "Ejecutando: \n[" + query.toString() + "]\n[" + folioAdecuacion + "]");
            rs = ps.executeQuery();
            if (rs.next()) {
                AdecuacionEncabezadoResumen aer = new AdecuacionEncabezadoResumen();
                aer.setfAplicacion(rs.getString("faplicacion"));
                aer.setJustificacion(rs.getString("justificacion"));
                aer.setFolio(rs.getInt("folio"));
                return aer;
            }
            return null;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String readAdecuacionTipo(Connection conn, int folioAdecuacion) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT cTipoAdecuacion ");
        query.append("FROM   tadecuacionencabezado WITH(NOLOCK)");
        query.append("WHERE  nfolioadecuacion = ?  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioAdecuacion);
            log.debug("Object: {}", "Ejecutando: \n[" + query.toString() + "]\n[" + folioAdecuacion + "]");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new RuntimeException("No se encontro adecuacion con el folio: " + folioAdecuacion);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void insertaAdecuacionDetalleResumen(Connection conn, List<AdecuacionDetalleResumen> detalle) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO tAdecuacionNotificadaDetalle(nFolioAdecuacion,cPrograma,cUnidadEjecutora,cEntidadFederativa,cTipoMovimiento,mMontoMovimiento,nMes) VALUES(?,?,?,?,?,?,?)");
        QueryRunner run = new QueryRunner();
        for (AdecuacionDetalleResumen adecuacionDetalleResumen : detalle) run.update(conn, query.toString(), adecuacionDetalleResumen.getFolio(), adecuacionDetalleResumen.getPrograma(), adecuacionDetalleResumen.getUnidadEjecutora(), adecuacionDetalleResumen.getEntidadFederativa(), adecuacionDetalleResumen.getTipoMovimiento(), adecuacionDetalleResumen.getMontoMovimiento(), adecuacionDetalleResumen.getMes());
    }

    public static int insertaAdecuacionNotificadaEncabezado(Connection conn, AdecuacionEncabezadoResumen adecuacionEncabezadoResumen) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO  tAdecuacionNotificada( nFolioAdecuacion, cJustificacionAdecuacion )  VALUES(?, ? )");
        QueryRunner run = new QueryRunner();
        int insertados = run.update(conn, query.toString(), adecuacionEncabezadoResumen.getFolio(), adecuacionEncabezadoResumen.getJustificacion());
        return insertados;
    }

    public static List<AdecuacionDetalleResumen> readAdecuacionNotificadaDetalle(Connection conn, int folio) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT nFolioAdecuacion AS  folio ");
        query.append("      ,cPrograma AS programa ");
        query.append("      ,cUnidadEjecutora AS unidadEjecutora ");
        query.append("      ,cEntidadFederativa AS entidadFederativa ");
        query.append("      ,cTipoMovimiento AS tipoMovimiento ");
        query.append("      ,mMontoMovimiento AS montoMovimiento ");
        query.append("      ,nMes AS mes ");
        query.append("FROM  tAdecuacionNotificadaDetalle  WITH(NOLOCK) ");
        query.append("WHERE nFolioAdecuacion = ? ");
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<AdecuacionDetalleResumen>> h = new BeanListHandler<AdecuacionDetalleResumen>(AdecuacionDetalleResumen.class);
        List<AdecuacionDetalleResumen> detalle = run.query(conn, query.toString(), h, folio);
        return detalle;
    }

    public static AdecuacionEncabezadoResumen readAdecuacionNotificadaEncabezado(Connection conn, int folio) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT nFolioAdecuacion AS folio ");
        query.append("      ,cJustificacionAdecuacion AS justificacion ");
        query.append("      ,CONVERT( VARCHAR(12),dFechaNotificacion, 103) AS fAplicacion ");
        query.append("      ,ISNULL( cAfectaMetas, '') AS afectaMetas ");
        query.append("      ,ISNULL( cRespuesta, '') AS respuesta ");
        query.append(" FROM  tAdecuacionNotificada  WITH(NOLOCK) ");
        query.append("WHERE nFolioAdecuacion = ? ");
        ResultSetHandler<AdecuacionEncabezadoResumen> h = new BeanHandler<AdecuacionEncabezadoResumen>(AdecuacionEncabezadoResumen.class);
        QueryRunner run = new QueryRunner();
        AdecuacionEncabezadoResumen adecuacionEncabezadoResumen = run.query(conn, query.toString(), h, folio);
        return adecuacionEncabezadoResumen;
    }

    public static String identificaMovimiento(Connection conn, int folioAdecuacion) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "exec sp_valida_adecuacion_META ?";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, folioAdecuacion);
            rs = cs.executeQuery();
            log.debug("Object: {}", "Ejecutando: \n[" + query.toString() + "]\n[" + folioAdecuacion + "]");
            if (rs.next()) {
                return rs.getString("validaMETA");
            } else {
                throw new RuntimeException("No se encontro adecuacion con el folio: " + folioAdecuacion);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rs);
        }
    }
}
