package com.syc.contable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import com.syc.contable.core.ReintegroDetalle;
import com.syc.contable.core.ReintegroDetalleMil;
import com.syc.contable.core.ReintegroEncabezado;
import com.syc.contable.core.ReintegroEncabezadoMil;
import com.syc.contable.core.ReintegrosAnexo1Manager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReintegrosAnexo1BusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ReintegrosBusinessLogic.class);

    public boolean correoProduccion = false;

    private String jniName = null;

    public ReintegrosAnexo1BusinessLogic(String jniName) {
        super.init(jniName);
    }

    // para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public List<String> leeArchivoExcel(String archivo, Caso c, Usuario usuario, int folio) throws Exception {
        Connection conn = null;
        DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
        try {
            conn = getConnection();
            /*
			 * se pone true por si hay remanente para la ep, mes y cxp que
			 * cumplan aunque sea un renglon de muchos, en ese hay remanente
			 */
            ReintegroEncabezadoMil reinE = new ReintegroEncabezadoMil();
            ReintegroDetalleMil reinD = new ReintegroDetalleMil();
            ArrayList<ReintegroDetalleMil> reinDetalles = new ArrayList<ReintegroDetalleMil>();
            generaEncabezadoDetalleReintegro(archivo, c, usuario, reinE, reinD, reinDetalles, folio);
            List<String> lstErrores = validaArchivoExcelReintegro(c, reinDetalles, reinE, reinD);
            if (lstErrores.size() > 0)
                return lstErrores;
            else {
                ReintegrosAnexo1Manager.insertaReintegro(conn, reinE, reinDetalles, folio, c.getFolio(), usuario);
                List<String> l = new ArrayList<String>();
                l.add("EXITO");
                return l;
            }
        } catch (Exception e) {
            dbl.limpiaDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
            throw e;
        }
    }

    public List<String> validaArchivoExcelReintegro(Caso c, ArrayList<ReintegroDetalleMil> reinDetalles, ReintegroEncabezadoMil recE, ReintegroDetalleMil recD) throws Exception {
        List<String> mensajes = new ArrayList<String>();
        boolean banderaRemanente = false;
        int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        double remanente = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            double importe = 0;
            for (int i = 0; i < reinDetalles.size(); i++) {
                ReintegroDetalleMil rd = reinDetalles.get(i);
                importe += rd.getmImporteCLC();
                if (!getEPCatalogo(rd.getEP())) {
                    mensajes.add("La EP " + rd.getEP() + " no existe o se encuentra mal escrita \\n");
                    log.warn("La EP " + rd.getEP() + " no existe o se encuentra mal escrita \n");
                    throw new Exception("La EP " + rd.getEP() + " no existe o se encuentra mal escrita \\n");
                } else {
                    String str = clcPagada(Integer.parseInt(rd.getnSIAFF().replace(".0", "")), rd.getCxp());
                    if (str != null && !"".equals(str)) {
                        mensajes.add(str);
                        throw new Exception(str);
                    } else if (!"2012".equals(adecProy.obtenEjercicioFiscal())) {
                        // para dejar pasar el reintegro sin validación de remanentes en caso de ser 2012
                        String tipo = tipoPago(Integer.parseInt(rd.getnSIAFF().replace(".0", "")), rd.getCxp());
                        if ("AJENAS".equals(tipo)) {
                            log.warn("Una CLC de retenciones " + rd.getEP() + " no se puede reintegrar \n");
                            throw new Exception("Una CLC de retenciones " + rd.getEP() + " no se puede reintegrar \\n");
                        }
                        // ******************************************************************************//
                        rd.setSecCLC(String.valueOf(secCLC(String.valueOf(rd.getnSIAFF()).replace(".0", ""), rd.getEP(), folio)));
                        rd.setFolioDependenciaSicop(folioDependencia(String.valueOf(rd.getnSIAFF()).replace(".0", ""), rd.getEP(), folio));
                        rd.setRfc(getRFC(rd.getCxp(), rd.getEP()));
                        // *****************************************************************************//
                        // int secuenciaCLC = 0; //TEMPORALMENTE PARA REGULARIZACION!!!!!
                        if ("-1".equals(rd.getSecCLC())) {
                            mensajes.add("No se puede obtener el valor de la secuencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n");
                            log.warn("No se puede obtener el valor de la secuencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\n");
                            throw new Exception("No se puede obtener el valor de la secuencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n");
                        } else {
                            if ("".equals(rd.getFolioDependenciaSicop())) {
                                mensajes.add("No se puede obtener el folio de dependencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n");
                                log.warn("No se puede obtener el folio de dependencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\n");
                                throw new Exception("No se puede obtener el folio de dependencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n");
                            }
                            banderaRemanente = false;
                            int[] docRenglon = getNDocRenglon(rd.getCxp(), rd.getEP(), rd.getMes(), folio);
                            if (docRenglon[0] == -1) {
                                mensajes.add("Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + rd.getCxp() + "\\n");
                                log.warn("Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + rd.getCxp() + "\n");
                                throw new Exception("Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + rd.getCxp() + "\\n");
                            } else {
                                int j = 0;
                                while (j < docRenglon.length && !banderaRemanente) {
                                    remanente = getRemanente(rd.getEP(), rd.getCxp(), docRenglon[j]);
                                    if (remanente >= rd.getmImporteCLC()) {
                                        banderaRemanente = true;
                                        rd.setRenglonPagado(String.valueOf(docRenglon[j]));
                                        rd.setAlm(getALM(rd.getCxp(), rd.getEP(), rd.getMes(), folio, docRenglon[j]));
                                        rd.setcCentroContable(getCCNormal(rd.getCxp(), rd.getEP(), rd.getMes(), folio, docRenglon[j]));
                                    }
                                    j++;
                                }
                                if (!banderaRemanente) {
                                    mensajes.add("No hay suficiente remanente para poder aplicar el reintegro para " + rd.getEP() + " renglon " + rd.getSecCLC() + " mes " + rd.getMes() + " cxp " + rd.getCxp() + " remanente " + remanente + " importe a reintegrar " + rd.getmImporteCLC() + "\\n");
                                    //mensajes.add(ReintegrosAnexo1Manager.getMesesImportes(conn, rd.getEP(), rd.getCxp()));
                                    log.warn("No hay suficiente remanente para poder aplicar el reintegro para " + rd.getEP() + " renglon " + rd.getSecCLC() + " mes " + rd.getMes() + " cxp " + rd.getCxp() + " remanente " + remanente + " importe a reintegrar " + rd.getmImporteCLC() + "\n");
                                    throw new Exception("No hay suficiente remanente para poder aplicar el reintegro para " + rd.getEP() + " renglon " + rd.getSecCLC() + " mes " + rd.getMes() + " cxp " + rd.getCxp() + " remanente " + remanente + " importe a reintegrar " + rd.getmImporteCLC() + //+ ReintegrosAnexo1Manager.getMesesImportes(conn, rd.getEP(), rd.getCxp())
                                    "\\n");
                                } else {
                                    String partida = getcPartida(rd.getEP());
                                    if (partida.trim().length() > 0 && partida != null && !"".equals(partida)) {
                                        rd.setcPartida(partida);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (com.syc.contable.util.Math.truncate(importe, 2) != com.syc.contable.util.Math.truncate(Double.parseDouble(recE.getImporteLC()), 2)) {
                log.warn("El importe del encabezado y la suma de los importes de los detalles no coinciden \n");
                throw new Exception("El importe del encabezado y la suma de los importes de los detalles no coinciden \\n");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return mensajes;
    }

    private void generaEncabezadoDetalleReintegro(String archivo, Caso c, Usuario usuario, ReintegroEncabezadoMil reinE, ReintegroDetalleMil reinD, ArrayList<ReintegroDetalleMil> reinDetalles, int folio) throws Exception {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        reinE.setcCentroContable(usuario.getPropiedad("CCENTROCONTABLE").getValor());
        reinE.setnFolioReintegro(folio);
        reinE.setcUnidadResponsableContable("RHQ");
        reinE.setcTipoPoliza("DI");
        reinE.setfExpedicion(today);
        reinE.setcDocumentoHaplicado("N");
        reinE.setaEjercicioFiscal(adecProy.obtenEjercicioFiscal());
        int renglon = 1;
        InputStream inp = new FileInputStream(archivo);
        HSSFWorkbook wb = new HSSFWorkbook(inp);
        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        int fila = 0;
        while (rowIterator.hasNext()) {
            fila++;
            int celda = 0;
            String ep = "";
            HSSFRow hssfRow = (HSSFRow) rowIterator.next();
            Iterator<Cell> cellIterator = hssfRow.cellIterator();
            while (cellIterator.hasNext()) {
                try {
                    String valor = valorCelda(cellIterator);
                    if (valor != null) {
                        if (fila == 2) {
                            if (celda == 0)
                                reinE.setaEjercicioFiscal(valor.replace(".0", ""));
                            if (celda == 1)
                                reinE.setcRamo(valor.replace(".0", ""));
                            if (celda == 2) {
                                if (!valor.equals(usuario.getU_UR())) {
                                    log.error("La UR del archivo no corresponde a la del usuario.");
                                    throw new Exception("La UR del archivo no corresponde a la del usuario.");
                                }
                                reinE.setcUnidadResponsable(valor);
                            }
                            if (celda == 3)
                                reinE.setImporteLC(valor);
                            if (celda == 4)
                                reinE.setAviso(valor.replace(".0", ""));
                            if (celda == 5) {
                                if ("N/A".equals(valor))
                                    reinE.setfSolicitud(today);
                                else
                                    reinE.setfSolicitud(valor);
                            }
                            if (celda == 6) {
                                if (validaCatMovimiento(valor)) {
                                    reinE.setMovimiento(valor);
                                } else {
                                    log.error("El movimiento no se encuentra en catalogos.");
                                    throw new Exception("El movimiento no se encuentra en catalogos.");
                                }
                            }
                            if (celda == 7) {
                                if (validaCatTipoAviso(valor.replace(".0", ""))) {
                                    reinE.setTipoAviso(valor.replace(".0", ""));
                                } else {
                                    log.error("El tipo aviso no se encuentra en catalogos.");
                                    throw new Exception("El tipo aviso no se encuentra en catalogos.");
                                }
                            }
                            if (celda == 8) {
                                if (validaCatFormaPago(valor.replace(".0", ""))) {
                                    reinE.setFormaDePago(valor.replace(".0", ""));
                                } else {
                                    log.error("La forma pago no se encuentra en catalogos.");
                                    throw new Exception("La forma pago no se encuentra en catalogos.");
                                }
                            }
                            if (celda == 9) {
                                if (validaCatCausaAviso(valor.replace(".0", ""))) {
                                    reinE.setCausaAviso(valor.replace(".0", ""));
                                } else {
                                    log.error("La causa de aviso no se encuentra en catalogos.");
                                    throw new Exception("La causa de aviso no se encuentra en catalogos.");
                                }
                            }
                            if (celda == 10) {
                                if ("N/A".equals(valor))
                                    reinE.setfAplicacion(today);
                                else
                                    reinE.setfAplicacion(valor);
                            }
                            if (celda == 11) {
                                if ("N/A".equals(valor))
                                    reinE.setfAcreditacion(today);
                                else
                                    reinE.setfAcreditacion(valor);
                            }
                            if (celda == 12)
                                reinE.setClvRastreo(valor.replace(".0", ""));
                            if (celda == 13)
                                reinE.setFichaDeposito(valor.replace(".0", ""));
                            if (celda == 14)
                                reinE.setLc(valor.replace(".0", ""));
                            if (celda == 15)
                                reinE.setClvBanco(valor.replace(".0", ""));
                            if (celda == 16)
                                reinE.setCuentaBancaria(valor.replace(".0", ""));
                            if (celda == 17)
                                reinE.setFolioDependencia(valor.replace(".0", ""));
                        } else if (fila == 4) {
                            if (celda == 0)
                                reinE.setObservaciones(valor);
                            if (celda == 1)
                                reinE.setConcepto(valor);
                        }
                        if (fila > 5) {
                            if (celda == 0) {
                                reinD = new ReintegroDetalleMil();
                                // viene en el archivo como CLC indicando que es el número de CLC, el que proporcionan es el
                                reinD.setnSIAFF(valor.replace(".0", ""));
                                // SIAFF, de ahí el nombre para no confundirnos con el nCLCSicop
                            }
                            if (celda == 1) {
                                //reinD.setnDocRenglon(valor.replace(".0", ""));
                                reinD.setnDocRenglon(String.valueOf(renglon));
                                renglon++;
                            }
                            if (celda == 2)
                                ep = valor;
                            if (celda == 3) {
                                if (!"A02".equals(usuario.getU_UR())) {
                                    if (!valor.contains(usuario.getU_UR())) {
                                        log.error("La clave " + ep + "." + valor + " no corresponde a la UR del usuario.");
                                        throw new Exception("La clave " + ep + "." + valor + " no corresponde a la UR del usuario.");
                                    }
                                }
                                reinD.setEP(ep + "." + valor);
                                reinD.setObgt(reinD.getObgt().substring(31, 36));
                            }
                            if (celda == 4)
                                reinD.setMes(Integer.parseInt(valor.replace(".0", "")));
                            if (celda == 5) {
                                reinD.setmImporteCLC(Double.parseDouble(valor));
                                if (reinD.getmImporteCLC() == 0) {
                                    log.error("No se puede subir un reintegro con importes de cero.");
                                    throw new Exception("No se puede subir un reintegro con importes de cero.");
                                }
                            }
                            if (celda == 6) {
                                reinD.setCxp(valor);
                                reinD.setcEvento("REIN_TRAM");
                                reinDetalles.add(reinD);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("Hubo un error de formato en la fila " + fila + " celda " + celda + "." + e.toString(), e);
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

    public void buscaReintegro(Caso c) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public boolean insertaDetReintegro(int folio, ArrayList<ReintegroDetalle> datos, String clc, String cCentro) throws Exception {
        Connection conn = null;
        boolean inserta = false;
        // para comparar si son de la misma entidad
        boolean iguales = true;
        // federativa
        try {
            conn = getConnection();
            /*
			 * int j=0; //String ef =
			 * ReintegrosAnexo1Manager.getEntidadFederativa(conn,
			 * datos.get(0).getEP()); while(j<datos.size() && iguales){
			 * //comparamos cada entidad federativa para que sean iguales, ya
			 * que no se debe permitir la diferencia //según los requerimientos
			 * y la plática con la gente de reintegros del día 13 de septiembre
			 * de 2012
			 * //if(!ef.equals(ReintegrosAnexo1Manager.getEntidadFederativa(conn,
			 * datos.get(j).getEP()))) //iguales = false; j++; } if(iguales){
			 * for (int i = 0; i < datos.size(); i++) { String cOBGINI =
			 * datos.get(i).getEP().split("\\.")[9]; String cCTGA =
			 * datos.get(i).getEP().split("\\.")[10]; //String evento =
			 * ReintegrosAnexo1Manager.getEvento(conn, cOBGINI, cCTGA, clc); String
			 * evento = "REIN_TRAM"; //Cuando está en trámite todos los
			 * reintegros van a tener este evento, excepto SPEI
			 * if("REIN_TRAM_SPEI".equals(clc)) evento = "REIN_TRAM_SPEI";
			 * //Evento en trámite SPEI //inserta =
			 * ReintegrosAnexo1Manager.insertaDetReintegro(conn, folio, datos.get(i),
			 * evento, cCentro, getNDocRenglon(datos.get(i).getCxp(),
			 * datos.get(i).getEP(), datos.get(i).getMes())[0]); } inserta =
			 * ReintegrosAnexo1Manager.insertaDetReintegro(conn, folio, null, "",
			 * cCentro, 1); }else{ inserta = false; }
			 */
            inserta = ReintegrosAnexo1Manager.insertaDetReintegro(conn, folio, null, "", cCentro, 1);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return inserta;
    }

    public boolean insertaDetReintegroPaso(Connection conn, int folio, ReintegroDetalle datos, String clc, String cCentro, int consecutivo, String folioDep) throws Exception {
        boolean inserta = false;
        try {
            // Cuando está en trámite todos los
            String evento = "REIN_TRAM";
            // reintegros van a tener este
            // evento, excepto SPEI
            if ("REIN_TRAM_SPEI".equals(clc))
                // Evento en trámite SPEI
                evento = "REIN_TRAM_SPEI";
            inserta = ReintegrosAnexo1Manager.insertaDetReintegroPaso(conn, folio, datos, evento, cCentro, consecutivo, folioDep);
        } catch (Exception exc) {
            log.error(exc);
            throw new Exception(exc);
        } finally {
        }
        return inserta;
    }

    public boolean borraDetReintegro(int folio) throws Exception {
        Connection conn = null;
        boolean borra = false;
        try {
            conn = getConnection();
            borra = ReintegrosAnexo1Manager.borraDetReintegro(conn, folio);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return borra;
    }

    public boolean borraDetReintegroPaso(int folio) throws Exception {
        Connection conn = null;
        boolean borra = false;
        try {
            conn = getConnection();
            if (getDetallePasoTotal(folio) > 0) {
                borra = ReintegrosAnexo1Manager.borraDetReintegroPaso(conn, folio);
                conn.commit();
            }
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return borra;
    }

    public boolean insertaEncReintegro(int folio, ReintegroEncabezado datos, String clc, String cCentro, String cFechaAplica, Caso c) throws Exception {
        Connection conn = null;
        boolean inserta = false;
        try {
            conn = getConnection();
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            if ((cFechaAplica == null) || (cFechaAplica.trim().length() == 0)) {
                if (c.getCasoDato("FECHA_AP_CONT").getValor() != null)
                    cFechaAplica = c.getCasoDato("FECHA_AP_CONT").getValor();
                else {
                    cFechaAplica = dateFormatter.format(new Date());
                }
            }
            /*
			 * if (c.getCasoDato("FECHA_AP_CONT").getValor() != null) { String
			 * cFechaAplicacion = c.getCasoDato("FECHA_AP_CONT").getValor();
			 * fAplicacion = new
			 * java.sql.Date(dateFormatter.parse(cFechaAplicacion).getTime()); }
			 * else { fAplicacion = new
			 * java.sql.Date(System.currentTimeMillis()); }
			 */
            java.sql.Date fAplicacion;
            if (!"2014".equals(adecProy.obtenEjercicioFiscal())) {
                try {
                    // Descomentar cuando se requiera cambiar a 2014
                    fAplicacion = new java.sql.Date(dateFormatter.parse(cFechaAplica).getTime());
                } catch (Exception e) {
                    java.util.Date today = new java.util.Date();
                    fAplicacion = new java.sql.Date(today.getTime());
                }
            } else {
                fAplicacion = new java.sql.Date(dateFormatter.parse("31/12/2014").getTime());
            }
            inserta = ReintegrosAnexo1Manager.insertaEncReintegro(conn, folio, datos, cCentro, fAplicacion);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return inserta;
    }

    public String getEvento(String cOBGINI, String cCTGA, String tipoCLC) throws Exception {
        Connection conn = null;
        String res = "";
        try {
            conn = getConnection();
            res = ReintegrosAnexo1Manager.getEvento(conn, cOBGINI, cCTGA, tipoCLC);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ArrayList<ReintegroDetalle> getReintegroDetalle(int folio) throws SQLException {
        Connection conn = null;
        ArrayList<ReintegroDetalle> detalles;
        try {
            conn = getConnection();
            detalles = ReintegrosAnexo1Manager.getReintegroDetalle(conn, folio);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return detalles;
    }

    public ArrayList<ReintegroDetalle> getDetallesInsert(String tablaDatos, int folio) throws Exception {
        ArrayList<ReintegroDetalle> reintegrosDetalle = new ArrayList<ReintegroDetalle>();
        String pagado = "";
        if (!tablaDatos.equals("")) {
            String[] tablaDatosArreglo = tablaDatos.split("!");
            for (int i = 0; i < tablaDatosArreglo.length; i += 7) {
                ReintegroDetalle reintegro = new ReintegroDetalle();
                if (!tablaDatosArreglo[i].equals("")) {
                    reintegro.setNoCLC(new Double(tablaDatosArreglo[i]).intValue());
                    reintegro.setCxp(tablaDatosArreglo[i + 5]);
                    pagado = clcPagada(reintegro.getNoCLC(), reintegro.getCxp());
                    if (pagado.trim().length() > 0 && pagado != null && !"".equals(pagado)) {
                        reintegrosDetalle.clear();
                        break;
                    }
                }
                if (!tablaDatosArreglo[i + 1].equals(""))
                    reintegro.setSecCLC(secCLC(tablaDatosArreglo[i + 5].trim(), tablaDatosArreglo[i + 2].trim(), folio));
                if (!tablaDatosArreglo[i + 2].equals(""))
                    reintegro.setEP(tablaDatosArreglo[i + 2].trim());
                if (!tablaDatosArreglo[i + 3].equals(""))
                    reintegro.setMes(new Double(tablaDatosArreglo[i + 3]).intValue());
                if (!tablaDatosArreglo[i + 4].equals(""))
                    reintegro.setmImporteCLC(Double.valueOf(tablaDatosArreglo[i + 4]));
                if (!tablaDatosArreglo[i + 5].equals(""))
                    reintegro.setCxp(tablaDatosArreglo[i + 5].trim());
                if (!tablaDatosArreglo[i + 6].equals(""))
                    reintegro.setnDocRenglon(Integer.parseInt(tablaDatosArreglo[i + 6].trim()));
                String partida = "";
                try {
                    partida = getcPartida(reintegro.getEP());
                    if (partida.trim().length() > 0 && partida != null && !"".equals(partida)) {
                        reintegro.setnPartida(partida);
                    } else {
                        reintegrosDetalle.clear();
                        break;
                    }
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
                if ("".equals(pagado.trim()) && pagado.trim().length() == 0)
                    reintegrosDetalle.add(reintegro);
                else {
                    reintegrosDetalle = null;
                }
            }
        }
        return reintegrosDetalle;
    }

    public int getDetallePasoTotal(int folio) throws Exception {
        Connection conn = null;
        int total = 0;
        try {
            conn = getConnection();
            total = ReintegrosAnexo1Manager.getDetallePasoTotal(conn, folio);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return total;
    }

    public String clcPagada(int nCLC, String cxp) throws Exception {
        Connection conn = null;
        String res = "";
        try {
            conn = getConnection();
            res = ReintegrosAnexo1Manager.getCLCPagada(conn, nCLC, cxp);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String tipoPago(int nCLC, String cxp) throws Exception {
        Connection conn = null;
        String res = "";
        try {
            conn = getConnection();
            res = ReintegrosAnexo1Manager.tipoPago(conn, nCLC, cxp);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String AutorizaReintegroNuevo(Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario, String fAcredit) throws SQLException {
        //correoProduccion = true;
        //List<String> arrLResult = null;
        ArrayList<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        String cMensaje = "";
        String validaMes = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        String year = adecProy.obtenEjercicioFiscal();
        try {
            conn = getConnection();
            int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            validaMes = ReintegrosAnexo1Manager.validaMes(conn, c, folio, fAcredit);
            if ("S".equals(validaMes)) {
                ReintegrosAnexo1Manager.autorizaReintegro(conn, c, nNumSicop, cRecMotivSicop, nNumMAP, cRecMotivMAP, uLogin, prefixPath, fAcredit);
                ReintegroEncabezadoMil re = ReintegrosAnexo1Manager.getReintegroEncabezadoNuevo(conn, folio);
                ContableInterface conInt = new AplicacionContable();
                log.debug("Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                AplicarContableReturn acr;
                if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
                    acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROANEXO1", m, prefixPath, uLogin, "");
                else
                    acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROANEXO1", m, prefixPath, uLogin, "SI");
                //arrLResult = acr.getMessageList();
                arrLResult = (ArrayList<String>) acr.getMessageList();
                log.debug("Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                Calendar cal = new GregorianCalendar();
                String mesActual = Util.NOMBRE_MESES_MX[cal.get(Calendar.MONTH)];
                Caso cReloaded = new Caso();
                cReloaded.setIdCaso(c.getIdCaso());
                cReloaded = CasoManager.select(conn, cReloaded);
                if (acr.isSuccess()) {
                    conn.commit();
                    String usuarioRevisor = ReintegrosAnexo1Manager.getCorreoRevisor(conn, c);
                    String usuariosBitacora = ReintegrosAnexo1Manager.getListaCorreos(conn, c);
                    String to = usuarioRevisor;
                    String cc = "";
                    //if(!"".equals(to) && to!=null)
                    //cc=";";
                    cc += usuariosBitacora;
                    String bcc = "";
                    String from = "";
                    String fromName = "Avisos de Reintegro";
                    String asuntoCorreo = "Aviso de Reintegro " + c.getFolio() + " (Ejercicio " + adecProy.obtenEjercicioFiscal() + ")";
                    String body = "";
                    if ("2013".equals(year))
                        body = (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + to + cc + "<br> <br>" : "") + //+ "Cierre de "+mesActual+" de "+year+"<br><br>" +
                        "Cierre de " + mesActual + " de 2014<br><br>" + //+"Cierre de Abril de 2014<br><br>" +
                        "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + "  con el folio siguiente: " + c.getFolio() + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>" + "Cabe mencionar que, dentro de la carpeta de 'Comprobante de Pago' deberá de estar adjuntada la siguiente documentación: <br>" + "-       Memorando dirigido  al Lic. Sergio Ramirez Rosales, indicando Ejercicio, Clc y Clave Presupuestal del reintegro<br>" + "-       Comprobante del  pago de cargas financieras, con el nombre, cargo y firma autógrafa del responsable administrativo.<br><br>" + "Y dentro de la carpeta del 'Reportes' el reporte que genera el SAI.";
                    else
                        body = (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + to + cc + "<br> <br>" : "") + "Cierre de " + mesActual + " de " + year + "<br><br>" + "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + "  con el folio siguiente: " + c.getFolio() + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>";
                    try {
                        if (!correoProduccion) {
                            to = "arlopeza@axtel.com.mx";
                            cc = "vgarciac@axtel.com.mx";
                        }
                        AlarmaManager.procesaAlarmaCNF(conn, prefixPath, c.getCasoOperacion(0), c, asuntoCorreo, to, cc, bcc, body);
                    } catch (Exception exmail) {
                        log.error("No se logro enviar el correo de autorizacion de reintegros: " + exmail);
                    }
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REINTEGROANEXO1" }, new String[] { "consulta_reintegroanexo1" }, m, prefixPath);
                } else {
                    conn.rollback();
                }
            } else {
                arrLResult.add("El mes de aplicacion esta cerrado contablemente, favor de notificar a contabilidad o cambiar la fecha de aplicacion.");
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc);
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

    @SuppressWarnings({ "unchecked" })
    public ArrayList AutorizaReintegro(Caso c, String nNumSicop, String cRecMotivSicop, Map m, String prefixPath, String uLogin, String cSuperReduccion) throws SQLException {
        ArrayList arrLResult = new ArrayList();
        Connection conn = null;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr;
            if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROANEXO1", m, prefixPath, uLogin, "");
            else
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROANEXO1", m, prefixPath, uLogin, "SI");
            arrLResult = (ArrayList<String>) acr.getMessageList();
            log.debug("Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REINTEGROANEXO1" }, new String[] { "consulta_reintegroanexo1" }, m, prefixPath);
            } else {
                conn.rollback();
                // cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
                // "AUTORIZADOR_REINTEGRO" }, new String[] {
                // "autoriza_reintegro" }, m, prefixPath);
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc);
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
        return arrLResult;
    }

    public String cancelarAppContableNuevo(Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha) throws Exception {
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
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REINTEGROANEXO1" }, new String[] { "consulta_reintegroanexo1" }, m, prefixPath);
            } else {
                conn.rollback();
            }
            retVal = acr.getMessageList().get(acr.getMessageList().size() - 1);
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return retVal;
    }

    public String ValidaReintegro(int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map<?, ?> m, String prefixPath, String uLogin) throws Exception {
        List<String> arrLResult = null;
        Connection conn = null;
        String cMensaje = "";
        try {
            String DATE_FORMAT = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String today = sdf.format(c1.getTime());
            final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            actualizaFechaAplicacion(folio, today);
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr;
            if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROANEXO1", m, prefixPath, uLogin, "");
            else
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROANEXO1", m, prefixPath, uLogin, "SI");
            arrLResult = acr.getMessageList();
            log.debug("Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            if (acr.isSuccess()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc);
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

    public String getTipoPoliza(String tipoPoliza) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getTipoPoliza(conn, tipoPoliza);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public double getRemanente(String EP, String CXP, int secClc) throws SQLException {
        Connection conn = null;
        double res = 0.00;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getRemanente(conn, EP, CXP.trim(), secClc);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getcPartida(String EP) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getcPartida(conn, EP);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getEntidadFederativa(String EP) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getEntidadFederativa(conn, EP);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getTipoPago(String cxp) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getTipoPago(conn, cxp);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ReintegroEncabezado getReintegroEncabezado(int nFolio) throws SQLException {
        Connection conn = null;
        ReintegroEncabezado re = new ReintegroEncabezado();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = ReintegrosAnexo1Manager.getReintegroEncabezado(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public ReintegroEncabezadoMil getReintegroEncabezadoNuevo(int nFolio) throws SQLException {
        Connection conn = null;
        ReintegroEncabezadoMil re = new ReintegroEncabezadoMil();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = ReintegrosAnexo1Manager.getReintegroEncabezadoNuevo(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public boolean getReintegroHApartado(int nFolio) throws SQLException {
        // SIRVE PARA SABER SI YA HICIERON LA PRIMERA APLICACION CONTABLE
        // Y QUE NO LA VUELVAN A APLICAR
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getReintegroHApartado(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ArrayList<ReintegroDetalle> getReintegroLayout(int folio) throws SQLException {
        Connection conn = null;
        ArrayList<ReintegroDetalle> detalles;
        try {
            conn = getConnection();
            detalles = ReintegrosAnexo1Manager.getReintegroLayout(conn, folio);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return detalles;
    }

    public boolean getEPCatalogo(String EP) throws SQLException {
        boolean existe = false;
        Connection conn = null;
        try {
            conn = getConnection();
            existe = ReintegrosAnexo1Manager.getEPCatalogo(conn, EP);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return existe;
    }

    public boolean actualizaInfoPagos(int nFolio, String clvRastreo, String lc, String ficha, String clvBanco, String cuenta, String fechaAcredit, String ctab, String aviso, String folioDep) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.actualizaInfoPagos(conn, nFolio, clvRastreo, lc, ficha, clvBanco, cuenta, fechaAcredit, ctab, aviso, folioDep);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int secCLC(String caNoContrarrecibo, String EP, int folio) throws SQLException {
        Connection conn = null;
        int res = 0;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.secCLC(conn, caNoContrarrecibo, EP, folio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int secCLCRect(String caNoContrarrecibo, String EP) throws SQLException {
        Connection conn = null;
        int res = 0;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.secCLCRect(conn, caNoContrarrecibo, EP);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String tipoConcepto(String caNoContrarrecibo, String EP) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.tipoConcepto(conn, caNoContrarrecibo, EP);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String folioDependencia(String caNoContrarrecibo, String EP, int folio) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.folioDependencia(conn, caNoContrarrecibo, EP, folio);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getRFC(String caNoContrarrecibo, String ep) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getRFC(conn, caNoContrarrecibo, ep);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int[] getNDocRenglon(String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException {
        Connection conn = null;
        int[] res = new int[10];
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getNDocRenglon(conn, caNoContrarrecibo, EP, cMes, folio);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean borraReintegro(int folio) throws Exception {
        Connection conn = null;
        boolean borra = false;
        try {
            conn = getConnection();
            borra = ReintegrosAnexo1Manager.borraReintegro(conn, folio);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return borra;
    }

    public boolean validaCatMovimiento(String texto) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.validaCatMovimiento(conn, texto);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean validaCatTipoAviso(String texto) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.validaCatTipoAviso(conn, texto);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean validaCatFormaPago(String texto) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.validaCatFormaPago(conn, texto);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean validaCatCausaAviso(String texto) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.validaCatCausaAviso(conn, texto);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ReintegroDetalleMil datosSICOPMil(String caNoContrarrecibo, String EP, String folio, String movimiento, String concepto) throws SQLException {
        Connection conn = null;
        ReintegroDetalleMil res = new ReintegroDetalleMil();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.datosSICOPMil(conn, caNoContrarrecibo, EP, folio, movimiento, concepto);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int getRenglonPagadoMil(String caNoContrarrecibo, String EP, int cMes, int folio, String concepto, int movimiento) throws SQLException {
        Connection conn = null;
        int res = -1;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getRenglonPagadoMil(conn, caNoContrarrecibo, EP, cMes, folio, concepto, movimiento);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getALM(String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getALM(conn, caNoContrarrecibo, EP, cMes, folio, renglon);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getCCNormal(String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.getCCNormal(conn, caNoContrarrecibo, EP, cMes, folio, renglon);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public Caso generaAvisoDeReintegro(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            if (c != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", uLogin);
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", adecProy.obtenEjercicioFiscal());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                casoTx.avanzaCaso(c, uLogin, "", new String[] { "VENTANILLA_REINTEGROANEXO1" }, new String[] { "ventanilla_reintegroanexo1" }, data, "");
                c = casoTx.ejecutaCaso(c.getIdCaso(), -1, uLogin);
            } else
                throw new GestionException("No se encontro el tramite con folio: " + folioSAI);
            conn.commit();
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public void insertaReintegro(ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles, int folio, String folioCompleto, Usuario usuario) throws Exception, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            ReintegrosAnexo1Manager.insertaReintegro(conn, reinE, reinDetalles, folio, folioCompleto, usuario);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public boolean actualizaFechaAplicacion(int folio, String fAplicacion) throws SQLException, ParseException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosAnexo1Manager.actualizaFechaAplicacion(conn, folio, fAplicacion);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }
}
