package com.syc.contable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.contable.core.Rectificacion;
import com.syc.contable.core.RectificacionDetalle;
import com.syc.contable.core.RectificacionEncabezado;
import com.syc.contable.core.RectificacionPresupuestariaManager;
import com.syc.contable.core.ReintegrosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         SA de CV desarrollo gestion_conagua_sif México D.F. 19/07/2012
 */
public class RectificacionPresupuestariaMilBusinessLogic extends DataSourceManager {

    public Rectificacion detalleSB = null;

    private static Logger log = LoggerFactory.getLogger(RectificacionPresupuestariaMilBusinessLogic.class);

    public boolean correoProduccion = false;

    public RectificacionPresupuestariaMilBusinessLogic(String jniName) {
        super.init(jniName);
    }

    ReintegrosBusinessLogic rpbl = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);

    public Rectificacion filtrar(String clc, String tipoCLC, String folioSICOP, String folioSAI, String folioSIAFF, String CXP) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if ("".equals(tipoCLC)) {
                if ("".equals(folioSICOP) && "".equals(folioSAI) && "".equals(folioSIAFF))
                    detalleSB = RectificacionPresupuestariaManager.filtraCLC(conn, clc);
                if ("".equals(clc) && "".equals(folioSAI) && "".equals(folioSIAFF)) {
                    detalleSB = RectificacionPresupuestariaManager.filtraFolioSICOP(conn, folioSICOP, CXP);
                }
                if ("".equals(clc) && "".equals(folioSICOP) && "".equals(folioSIAFF)) {
                    detalleSB = RectificacionPresupuestariaManager.filtraFolioSAI(conn, folioSAI);
                }
                if ("".equals(clc) && "".equals(folioSICOP) && "".equals(folioSAI)) {
                    detalleSB = RectificacionPresupuestariaManager.filtraFolioSIAFF(conn, folioSIAFF);
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return detalleSB;
    }

    public Rectificacion filtrar(String folioSICOP, String UR, String CXP) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            detalleSB = RectificacionPresupuestariaManager.filtraFolioSICOP(conn, folioSICOP, UR, CXP);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return detalleSB;
    }

    public boolean insertTRectificacionEncabezado(int folio, String nIDCaso, String cEjercicio, String cRamo, String cUnidad, String cCentroContable, java.sql.Date fExp, java.sql.Date fApl, String conceptoRectificacion, String cTipoMovto, String nOrigenPPTO, String nMes, String oficioRectif, String ctr_int, String cTipoRectificacion, String nFolioSICOP, String caNoContrarrecibo, String cTipoPoliza, String cDescripcionPoliza, String u_login, String nFolioPoliza, String cUnidadResponsableContable, String nFolioSIAFF, String totalDebe, String totalDice, String esIP) throws Exception {
        Connection conn = null;
        boolean insertar = false;
        try {
            conn = getConnection();
            insertar = RectificacionPresupuestariaManager.insertarEncabezado(conn, folio, nIDCaso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, esIP);
            // FALTABA COMMIT, NUNCA INSERTABA ADEMÁS QUE TENÍA MAL EL NOMBRE DE
            // UNA COLUMNA FMC
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return insertar;
    }

    public boolean insertRectificacionDetalle(ArrayList<RectificacionDetalle> rd) throws Exception {
        Connection conn = null;
        boolean insertar = false;
        try {
            conn = getConnection();
            insertar = RectificacionPresupuestariaManager.insertarDetalle(conn, rd);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return insertar;
    }

    public String cancelarAppContableNuevo(Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha, Usuario usuario) throws Exception {
        String retVal = null;
        Connection conn = null;
        try {
            ContableInterface ci = new AplicacionContable();
            // conn = getConnection();
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            AplicarContableReturn acr = ci.cancelarAppContableNueva(conn, c, "", "", "", 1, "", m, prefixPath, uLogin, cFecha);
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                String to = usuario.getU_email();
                String body = (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "") + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue cancelado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + c.getFolio() + "</b><br>" + "Mismo que ya cuenta con estatus de cancelado en el SAI.";
                try {
                    if (!correoProduccion)
                        to = "vgarciac@axtel.com.mx";
                    AlarmaManager.procesaAlarmaCNF(conn, prefixPath, c.getCasoOperacion(0), c, "", to, body);
                } catch (Exception exmail) {
                    log.error("Object: {}", "No se logro enviar el correo de cancelacion de rectificaciones: " + exmail);
                }
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_RECTIFICACIONMIL" }, new String[] { "cons_rectificacion_m" }, m, prefixPath);
            } else {
                conn.rollback();
            }
            retVal = acr.getMessageList().get(acr.getMessageList().size() - 1);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return retVal;
    }

    public RectificacionEncabezado getRectificacionEncabezado(int folio) throws Exception {
        Connection conn = null;
        RectificacionEncabezado res;
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionEncabezado(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public RectificacionEncabezado getRectificacionEncabezadoMil(int folio) throws Exception {
        Connection conn = null;
        RectificacionEncabezado res = null;
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionEncabezadoMil(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public RectificacionEncabezado getRectificacionEncabezadoSicop(int folio) throws Exception {
        Connection conn = null;
        RectificacionEncabezado res = new RectificacionEncabezado();
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionEncabezadoSicopMil(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public StringBuilder getRectificacionDetalleSicop(int folio) throws Exception {
        Connection conn = null;
        StringBuilder res = new StringBuilder();
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionDetalleSicopMil(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ArrayList<RectificacionDetalle> getRectificacionDetalle(int folio) throws Exception {
        Connection conn = null;
        ArrayList<RectificacionDetalle> res = new ArrayList<RectificacionDetalle>();
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionDetalle(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    private void generaEncabezadoDetalleRectificacion(String archivo, Caso c, Usuario usuario, RectificacionEncabezado recE, RectificacionDetalle recD, ArrayList<RectificacionDetalle> recDetalles, int folio, int idCaso) throws Exception {
        // Voy a dejar este método en caso de que se quieran las rectificaciones
        // normales funcionando con excel
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        recE.setcCentroContable(usuario.getPropiedad("CCENTROCONTABLE").getValor());
        recE.setnFolioRectificacion(folio);
        recE.setnIdCaso(idCaso);
        recE.setcUnidadResponsableContable("RHQ");
        recE.setcTipoPoliza("RE");
        recE.setfExp(today);
        InputStream inp = new FileInputStream(archivo);
        HSSFWorkbook wb = new HSSFWorkbook(inp);
        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        int fila = 0;
        while (rowIterator.hasNext()) {
            fila++;
            int celda = 0;
            HSSFRow hssfRow = (HSSFRow) rowIterator.next();
            Iterator<Cell> cellIterator = hssfRow.cellIterator();
            while (cellIterator.hasNext()) {
                try {
                    String valor = valorCelda(cellIterator);
                    if (valor != null) {
                        if (fila == 2) {
                            if (celda == 0)
                                recE.setaEjercicioFiscal(valor);
                            if (celda == 1)
                                recE.setcRamo(valor);
                            if (celda == 2)
                                recE.setcUnidadResponsable(valor);
                            if (celda == 3)
                                recE.setTotalDice(valor);
                            if (celda == 4)
                                recE.setTotalDebe(valor);
                            if (celda == 5)
                                recE.setcTipoMovto(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 6)
                                recE.setnOrigenPPTO(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 7)
                                recE.setCtr_int(valor);
                            if (celda == 8) {
                                recE.setCaNoContrarrecibo(valor);
                                // CAMBIAR CUANDO SE TENGA EL MIL, MIENTRAS ESTÁ LA VISTA ESTO ES PARA PRUEBAS
                                recE.setCaNoContrarreciboMil(valor);
                            }
                            if (celda == 9)
                                recE.setnFolioSicop(valor);
                            if (celda == 10)
                                recE.setnFolioSIAFF(valor);
                            if (celda == 11)
                                recE.setcTipoRectificacion(valor);
                        } else if (fila == 4) {
                            if (celda == 0)
                                recE.setcConceptoRectificacion(valor);
                        }
                        if (fila > 5) {
                            if (celda == 0) {
                                recD = new RectificacionDetalle();
                                recD.setRenglon(Integer.parseInt(valor));
                            }
                            if (celda == 1) {
                                if (valor.trim().equals("DICE"))
                                    recD.setEvento("DICE_TRA");
                                else {
                                    recD.setEvento("DEBE_DECIR_TRA_DI");
                                }
                            }
                            if (celda == 2)
                                recD.setEp(valor);
                            if (celda == 3)
                                recD.setMes(Integer.parseInt(valor));
                            if (celda == 4) {
                                recD.setImporte(Double.parseDouble(valor));
                                recDetalles.add(recD);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("Hubo un error de formato en la fila " + fila + " celda " + (celda + 1) + "." + e.toString(), e);
                } catch (IllegalStateException exc) {
                    throw new Exception("Hubo un error de formato de formula en la fila " + fila + " celda " + celda + "." + exc.toString(), exc);
                } catch (IndexOutOfBoundsException exc) {
                    throw new Exception("Hubo un error de formato posiblemente de espacios en blanco en la fila " + fila + " celda " + celda + "." + exc.toString(), exc);
                } catch (Exception e) {
                    throw e;
                }
                celda++;
            }
        }
    }

    private void generaEncabezadoDetalleRectificacionMil(String archivo, Caso c, Usuario usuario, RectificacionEncabezado recE, RectificacionDetalle recD, ArrayList<RectificacionDetalle> recDetalles, int folio, int idCaso) throws Exception {
        // Voy a dejar este método en caso de que se quieran las rectificaciones
        // normales funcionando con excel
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        recE.setcCentroContable(usuario.getPropiedad("CCENTROCONTABLE").getValor());
        recE.setnFolioRectificacion(folio);
        recE.setnIdCaso(idCaso);
        recE.setcUnidadResponsableContable("RHQ");
        recE.setcTipoPoliza("RE");
        recE.setfExp(today);
        InputStream inp = new FileInputStream(archivo);
        HSSFWorkbook wb = new HSSFWorkbook(inp);
        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        int fila = 0;
        while (rowIterator.hasNext()) {
            fila++;
            int celda = 0;
            HSSFRow hssfRow = (HSSFRow) rowIterator.next();
            Iterator<Cell> cellIterator = hssfRow.cellIterator();
            while (cellIterator.hasNext()) {
                try {
                    String valor = valorCelda(cellIterator);
                    if (valor != null) {
                        if (fila == 2) {
                            if (celda == 0)
                                recE.setaEjercicioFiscal(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 1)
                                recE.setcRamo(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 2)
                                recE.setcUnidadResponsable(valor);
                            if (celda == 3)
                                recE.setTotalDice(valor);
                            if (celda == 4)
                                recE.setTotalDebe(valor);
                            if (celda == 5)
                                //recE.setcTipoMovto(String.format("%.0f", Double.parseDouble(valor)));
                                recE.setcTipoMovto(valor);
                            if (celda == 6)
                                recE.setnOrigenPPTO(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 7)
                                recE.setCtr_int(valor);
                            if (celda == 8)
                                recE.setCaNoContrarrecibo(valor);
                            if (celda == 9)
                                recE.setCaNoContrarreciboMil(valor);
                            // if (celda == 10)
                            // recE.setTipoConcepto(valor);
                            if (celda == 10)
                                recE.setnFolioSicop(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 11)
                                recE.setnFolioSIAFF(String.format("%.0f", Double.parseDouble(valor)));
                            if (celda == 12)
                                recE.setcTipoRectificacion(valor);
                        } else if (fila == 4) {
                            if (celda == 0)
                                recE.setcConceptoRectificacion(valor);
                        }
                        if (fila > 5) {
                            if (celda == 0) {
                                recD = new RectificacionDetalle();
                                recD.setRenglon(Integer.parseInt(valor.replace(".0", "")));
                            }
                            if (celda == 1) {
                                if (valor.trim().equals("DICE"))
                                    recD.setEvento("DICE_TRA");
                                else {
                                    //if (recE.getcTipoRectificacion().equals("DIRECTA"))
                                    recD.setEvento("DEBE_DECIR_TRA_DI");
                                    //else
                                    //recD.setEvento("DEBE_DECIR_TRA_CO");
                                }
                            }
                            if (celda == 2)
                                recD.setEp(valor);
                            if (celda == 3)
                                recD.setMes(Integer.parseInt(valor.replace(".0", "")));
                            if (celda == 4) {
                                recD.setImporte(Double.parseDouble(valor));
                            }
                            if (celda == 5) {
                                recD.setConcepto(valor);
                            }
                            if (celda == 6) {
                                recD.setMovimiento(valor.replace(".0", ""));
                                recDetalles.add(recD);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("Hubo un error de formato en la fila " + fila + " celda " + celda + "." + e.toString(), e);
                } catch (IllegalStateException exc) {
                    throw new Exception("Hubo un error de formato de formula en la fila " + fila + " celda " + celda + "." + exc.toString(), exc);
                } catch (IndexOutOfBoundsException exc) {
                    throw new Exception("Hubo un error de formato posiblemente de espacios en blanco en la fila " + fila + " celda " + celda + "." + exc.toString(), exc);
                } catch (Exception ex) {
                    if (recE.getcTipoRectificacion() == null)
                        throw new Exception("El tipo de rectificacion no debe estar vacío. " + ex.toString(), ex);
                    else
                        throw new Exception("Hubo un error de formato en la fila " + fila + " celda " + celda + "." + ex.toString(), ex);
                }
                celda++;
            }
        }
    }

    private List<String> validaArchivoExcelIntegro(Caso c, ArrayList<RectificacionDetalle> recDetalles, RectificacionEncabezado recE, RectificacionDetalle recD) throws Exception {
        List<String> mensajes = new ArrayList<String>();
        boolean banderaRemanente = false;
        int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        double remanente = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            int dices = 0;
            int debes = 0;
            double totalDebe = 0;
            double totalDice = 0;
            for (int i = 0; i < recDetalles.size(); i++) {
                RectificacionDetalle rd = recDetalles.get(i);
                if (!rpbl.getEPCatalogo(rd.getEp())) {
                    mensajes.add("La EP " + rd.getEp() + " no existe o se encuentra mal escrita \\n");
                    log.warn("Object: {}", "La EP " + rd.getEp() + " no existe o se encuentra mal escrita \n");
                    throw new Exception("La EP " + rd.getEp() + " no existe o se encuentra mal escrita \\n");
                } else {
                    String str = rpbl.clcPagada(0, recE.getCaNoContrarrecibo());
                    if (str != null && !"".equals(str)) {
                        mensajes.add(str);
                        throw new Exception(str);
                    }
                    if (rd.getEvento().contains("DICE")) {
                        totalDice += rd.getImporte();
                        dices++;
                    } else if (rd.getEvento().contains("DEBE")) {
                        totalDebe += rd.getImporte();
                        debes++;
                    }
                    /*
					 * el primer parametro no se usa, pero por si se llega a
					 * usar sería
					 */
                    // rpbl.clcPagada(Integer.parseInt(recE.getnFolioSIAFF()),recE.getCaNoContrarrecibo());
                    int secuenciaCLC = rpbl.secCLCRect(String.valueOf(recE.getnFolioSIAFF()), rd.getEp());
                    /*
					 * Revisar por que esta programado especificamente para
					 * reintegros
					 */
                    if (secuenciaCLC == -1 && rd.getEvento().contains("DICE")) {
                        mensajes.add("No se puede obtener el valor de la secuencia para la EP " + rd.getEp() + " Cuenta por Pagar " + recE.getCaNoContrarrecibo() + ".Favor de revisar la tabla CLC_SICOP.\\n");
                        log.warn("Object: {}", "No se puede obtener el valor de la secuencia para la EP " + rd.getEp() + "Cuenta por Pagar " + recE.getCaNoContrarrecibo() + ".Favor de revisar la tabla CLC_SICOP.\n");
                        throw new Exception("No se puede obtener el valor de la secuencia para la EP " + rd.getEp() + " Cuenta por Pagar " + recE.getCaNoContrarrecibo() + ".Favor de revisar la tabla CLC_SICOP.\\n");
                    } else {
                        int[] docRenglon = rpbl.getNDocRenglon(recE.getCaNoContrarrecibo(), rd.getEp(), rd.getMes(), folio);
                        if (docRenglon[0] == -1 && rd.getEvento().contains("DICE")) {
                            mensajes.add("Favor de revisar el mes para la EP " + rd.getEp() + " con CXP " + recE.getCaNoContrarrecibo() + "\\n");
                            log.warn("Object: {}", "Favor de revisar el mes para la EP " + rd.getEp() + " con CXP " + recE.getCaNoContrarrecibo() + "\n");
                            throw new Exception("Favor de revisar el mes para la EP " + rd.getEp() + " con CXP " + recE.getCaNoContrarrecibo() + "\\n");
                        } else {
                            int j = 0;
                            while (j < docRenglon.length && !banderaRemanente) {
                                remanente = rpbl.getRemanente(rd.getEp(), recE.getCaNoContrarrecibo(), docRenglon[j]);
                                if (remanente >= rd.getImporte()) {
                                    banderaRemanente = true;
                                }
                                j++;
                            }
                            if (!banderaRemanente) {
                                String mensaje = "No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + secuenciaCLC + " mes " + rd.getMes() + " cxp " + recE.getCaNoContrarrecibo() + " remanente " + remanente + " importe a reintegrar " + rd.getImporte() + "\\n";
                                String meses = ReintegrosManager.getMesesImportes(conn, rd.getEp(), recE.getCaNoContrarrecibo());
                                mensaje += meses;
                                mensajes.add(mensaje);
                                log.warn("Object: {}", "No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + secuenciaCLC + " mes " + rd.getMes() + " cxp " + recE.getCaNoContrarrecibo() + " remanente " + remanente + " importe a reintegrar " + rd.getImporte() + "\n");
                                throw new Exception("No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + secuenciaCLC + " mes " + rd.getMes() + " cxp " + recE.getCaNoContrarrecibo() + " remanente " + remanente + " importe a reintegrar " + rd.getImporte() + "\\n");
                            }
                        }
                    }
                }
            }
            /*if (debes < dices) {
				mensajes.add("Favor de revisar el numero de dices y de debes\\n");
				log.warn("Favor de revisar el numero de dices y de debes \n");
				throw new Exception("Favor de revisar el numero de dices y de debes\\n");
			}*/
            if (!recE.getTotalDebe().equals(recE.getTotalDice())) {
                mensajes.add("El total del DEBE necesita coincidir con el del DICE \\n");
                log.warn("El total del DEBE necesita coincidir con el del DICE \n");
                throw new Exception("El total del DEBE necesita coincidir con el del DICE \\n");
            }
            if (totalDebe != totalDice) {
                mensajes.add("La suma de los importes del DEBE necesita coincidir con la suma de los DICES \\n");
                log.warn("La suma de los importes del DEBE necesita coincidir con la suma de los DICES \n");
                throw new Exception("La suma de los importes del DEBE necesita coincidir con la suma de los DICES \\n");
            }
            if (totalDebe != Double.parseDouble(recE.getTotalDebe()) || totalDice != Double.parseDouble(recE.getTotalDice())) {
                mensajes.add("Favor de revisar la suma de los renglones de dices/debes con su importe total del encabezado \\n");
                log.warn("Favor de revisar la suma de los renglones de dices/debes con su importe total del encabezado \n");
                throw new Exception("Favor de revisar la suma de los renglones de dices/debes con su importe total del encabezado \\n");
            }
            return mensajes;
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public List<String> leeArchivoExcel(String archivo, Caso c, Usuario usuario, int folio, int idCaso, String tipo) throws Exception {
        Connection conn = null;
        DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
        try {
            conn = getConnection();
            /*
			 * se pone true por si hay remanente para la ep, mes y cxp que
			 * cumplan aunque sea un renglon de muchos, en ese hay remanente
			 */
            RectificacionEncabezado recE = new RectificacionEncabezado();
            RectificacionDetalle recD = new RectificacionDetalle();
            ArrayList<RectificacionDetalle> recDetalles = new ArrayList<RectificacionDetalle>();
            if ("Mil".equals(tipo))
                generaEncabezadoDetalleRectificacionMil(archivo, c, usuario, recE, recD, recDetalles, folio, idCaso);
            else
                generaEncabezadoDetalleRectificacion(archivo, c, usuario, recE, recD, recDetalles, folio, idCaso);
            List<String> lstErrores = validaArchivoExcelIntegro(c, recDetalles, recE, recD);
            if (lstErrores.size() > 0)
                return lstErrores;
            else {
                if ("Mil".equals(tipo))
                    RectificacionPresupuestariaManager.insertaRectificacionMil(conn, recE, recDetalles, folio, c.getFolio());
                /*
				 * else
				 * RectificacionPresupuestariaManager.insertaRectificacion(conn,
				 * recE, recDetalles,folio,c.getFolio());
				 */
                // Se deja preparado por si se quiere usar después en
                // rectificaciones normales
                List<String> l = new ArrayList<String>();
                l.add("EXITO");
                return l;
            }
        } catch (Exception e) {
            dbl.limpiaDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
            throw e;
        }
    }

    private String valorCelda(Iterator<Cell> cellIterator) {
        String valor = null;
        HSSFCell hssfCell = (HSSFCell) cellIterator.next();
        if (hssfCell.getCellType() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
                Date date = HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue());
                DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                valor = dateFormatter.format(date);
            } else {
                if (!String.valueOf(hssfCell.getNumericCellValue()).equals("") && String.valueOf(hssfCell.getNumericCellValue()) != null) {
                    valor = String.valueOf(hssfCell.getNumericCellValue());
                }
            }
        } else {
            if (!hssfCell.getStringCellValue().equals("") && hssfCell.getStringCellValue() != null) {
                valor = hssfCell.getStringCellValue();
            }
        }
        return valor;
    }

    public boolean borraRectificacionMil(int folio) throws Exception {
        Connection conn = null;
        boolean borra = false;
        try {
            conn = getConnection();
            borra = RectificacionPresupuestariaManager.borraRecitificacionMil(conn, folio);
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
        return borra;
    }

    public String aplicaRectificacion(int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map<?, ?> m, String prefixPath, String uLogin) throws Exception {
        List<String> arrLResult = null;
        Connection conn = null;
        String cMensaje = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Object: {}", "Inicia Apartado aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "tRectificacionEncabezadoMil", "tRectificacionDetalleMil", "nFolioRectificacionMil", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "RECTIFICACION", m, prefixPath, uLogin, "");
            arrLResult = acr.getMessageList();
            log.debug("Object: {}", "Termina Apartado Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                // cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
                // "AUTORIZADOR_RECTIFICACION" }, new String[] {
                // "layout_rectificacion" }, m, prefixPath);
            } else {
                conn.rollback();
                // cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
                // "REVISOR_REINTEGRO" }, new String[] { "revisa_reintegro" },
                // m, prefixPath);
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc.getMessage(), exc);
            arrLResult.add(exc.getLocalizedMessage());
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        Iterator<String> iteraMensajes = arrLResult.iterator();
        while (iteraMensajes.hasNext()) {
            cMensaje += iteraMensajes.next();
        }
        return cMensaje;
    }

    public String autorizaRectificacion(Caso c, Map m, String prefixPath, String uLogin, Usuario usuario) throws SQLException {
        int nIdCaso = 0;
        List<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        String cMensaje = "";
        String cTablaEncabezado = "";
        String cTablaDetalle = "";
        String cFolio = "";
        String cTipoDocumento = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        String mensaje = "";
        String tipo = "RectificacionMil";
        String tipoAplicacion = "";
        try {
            conn = getConnection();
            mensaje = RectificacionPresupuestariaManager.validaEvento(conn, c, tipo);
            arrLResult.add(mensaje);
            if (mensaje == "") {
                tipoAplicacion = RectificacionPresupuestariaManager.tipoRectificacion(conn, c, tipo);
                RectificacionPresupuestariaManager.autorizaRectificacionMil(conn, c, uLogin, prefixPath, tipoAplicacion);
                nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                if (nIdCaso > 0) {
                    ContableInterface conInt = new AplicacionContable();
                    log.debug("Object: {}", "Inicia Autorización contable" + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + nIdCaso);
                    // boolean validaSaldo;
                    cTablaEncabezado = "tRectificacionAutEncabezadoMil";
                    cTablaDetalle = "tRectificacionAutDetalleMil";
                    cFolio = "nFolioRectificacionMilAut";
                    cTipoDocumento = "RECTIFICACIONAUTMIL";
                    AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, cTablaEncabezado, cTablaDetalle, cFolio, nIdCaso, cTipoDocumento, m, prefixPath, uLogin, "");
                    arrLResult = acr.getMessageList();
                    conn.commit();
                    log.debug("Object: {}", "Termina Autorización contable " + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + nIdCaso);
                    Caso cReloaded = new Caso();
                    cReloaded.setIdCaso(c.getIdCaso());
                    cReloaded = CasoManager.select(conn, cReloaded);
                    if (acr.isSuccess()) {
                        conn.commit();
                        String to = usuario.getU_email();
                        String body = (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "") + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue autorizado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + c.getFolio() + "</b><br>" + "Mismo que ya cuenta con estatus de autorizado en el SAI.";
                        try {
                            if (!correoProduccion)
                                to = "vgarciac@axtel.com.mx;";
                            AlarmaManager.procesaAlarmaCNF(conn, prefixPath, c.getCasoOperacion(0), c, "", to, body);
                        } catch (Exception exmail) {
                            log.error("Object: {}", "No se logro enviar el correo de autorizacion de rectificaciones: " + exmail);
                        }
                        cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_RECTIFICACIONMIL" }, new String[] { "cons_rectificacion_m" }, m, prefixPath);
                    } else {
                        conn.rollback();
                    }
                } else {
                    log.debug("Error. no determinado se da rollback");
                    conn.rollback();
                }
            } else {
                log.debug("Object: {}", mensaje);
                //cMensaje += mensaje;
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            arrLResult.add(exc.getLocalizedMessage());
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        Iterator<String> iteraMensajes = arrLResult.iterator();
        while (iteraMensajes.hasNext()) {
            cMensaje += iteraMensajes.next();
        }
        return cMensaje;
    }

    public void actualizaFechaAplicacion(int folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            RectificacionPresupuestariaManager.actualizaFechaAplicacion(conn, folio);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }
}
