package com.syc.contable;

/**
 * ***********************************************************
 */
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import com.syc.admin.servlet.CasoCatalogoLogic;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.contable.core.RefasManager;
import com.syc.contable.core.ReintegroDetalle;
import com.syc.contable.core.ReintegroDetalleMil;
import com.syc.contable.core.ReintegroEncabezado;
import com.syc.contable.core.ReintegroEncabezadoMil;
import com.syc.contable.core.ReintegrosManager;
import com.syc.contable.core.ReintegrosMilManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.mail.CargaParametrosCorreo;
import com.syc.utils.mail.ParametrosCorreo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefasBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(RefasBusinessLogic.class);

    public boolean correoProduccion = false;

    private String jniName = null;

    public RefasBusinessLogic(String jniName) {
        super.init(jniName);
    }

    // para
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    // obtener
    // el
    // ejercicio
    // fiscal
    // en
    // diferentes
    // funciones
    public List<String> leeArchivoExcel(String archivo, Caso c, Usuario usuario, int folio) throws Exception {
        Connection conn = null;
        DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
        try {
            conn = getConnection();
            ReintegroEncabezadoMil reinE = new ReintegroEncabezadoMil();
            ReintegroDetalleMil reinD = new ReintegroDetalleMil();
            ArrayList<ReintegroDetalleMil> reinDetalles = new ArrayList<ReintegroDetalleMil>();
            List<String> lstErrores = generaEncabezadoDetalleRefas(archivo, c, usuario, reinE, reinD, reinDetalles, folio);
            lstErrores.addAll(validaArchivoExcelReintegro(c, reinDetalles, reinE, reinD));
            if (lstErrores.size() > 0) {
                dbl.limpiaDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
                return lstErrores;
            } else {
                RefasManager.insertaRefas(conn, reinE, reinDetalles, folio, c.getFolio(), usuario, reinE.getEjercicioRefas());
                List<String> l = new ArrayList<String>();
                RefasBusinessLogic refasBusinessLogic = new RefasBusinessLogic(null);
                refasBusinessLogic.actualizaFechaUltimaAct(reinE);
                int idCasoRefas = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                refasBusinessLogic.restarRemante(idCasoRefas, "TEMPORAL", "-");
                l.add("Carga Exitosa");
                return l;
            }
        } catch (Exception e) {
            dbl.limpiaDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
            throw e;
        }
    }

    public List<String> validaArchivoExcelReintegro(Caso c, ArrayList<ReintegroDetalleMil> reinDetalles, ReintegroEncabezadoMil recE, ReintegroDetalleMil recD) throws Exception {
        List<String> mensajes = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            double importe = 0;
            if (reinDetalles.size() > 5) {
                mensajes.add("La cantidad de clc's a reintegrar debe de ser menor a 5");
            }
            for (int i = 0; i < reinDetalles.size(); i++) {
                ReintegroDetalleMil rd = reinDetalles.get(i);
                if (duplicidad(reinDetalles, rd)) {
                    mensajes.add("La EP " + rd.getEP() + " y su cuenta por pagar " + rd.getCxp() + " se encuentra duplicada \\n");
                    log.warn("La EP " + rd.getEP() + " y su cuenta por pagar " + rd.getCxp() + " se encuentra duplicada \n");
                    // throw new Exception("La EP " + rd.getEP() + " y su cuenta
                    // por pagar "+ rd.getCxp()+" se encuentra duplicada \\n");
                }
                importe += rd.getmImporteCLC();
                if (!getEPCatalogo(rd.getEP(), recE.getEjercicioRefas(), rd.getnSIAFF())) {
                    mensajes.add("La CLC " + rd.getnSIAFF() + " ó la ep " + rd.getEP() + " no existen o se encuentra mal escritas \\n");
                    log.warn("La CLC " + rd.getnSIAFF() + " ó la ep " + rd.getEP() + " no existen o se encuentra mal escritas \\n");
                    // throw new Exception("La CLC " + rd.getnSIAFF() + " ó la
                    // ep "+rd.getEP()+" no existen o se encuentra mal escritas
                    // \\n");
                }
                if (!getRemanenteValido(rd.getEP(), recE.getEjercicioRefas(), rd.getnSIAFF(), rd.getmImporteCLC())) {
                    mensajes.add("El importe de la CLC " + rd.getnSIAFF() + " en la linea " + (i + 5) + " supera al remanente existente o se encuentra mal escritas \\n");
                    log.warn("El importe de la CLC " + rd.getnSIAFF() + "en la linea " + (i + 5) + " supera al remanente existente o se encuentra mal escritas \\n");
                    // throw new Exception("El importe de la CLC" +
                    // rd.getnSIAFF() + "en la linea "+i+" supera al remanente
                    // existente \\n");
                }
            }
            if (importe <= 100) {
                // validacion total del importe del archivo
                // subido mayor a 100
                mensajes.add("No se puede subir un reintegro con importe MENOR A CIEN PESOS.\\n");
                log.warn("No se puede subir un reintegro con importe MENOR A CIEN PESOS.\n");
                // throw new Exception("No se puede subir un reintegro con
                // importe MENOR A CIEN PESOS.\\n");
            }
            if (com.syc.contable.util.Math.truncate(importe, 2) != com.syc.contable.util.Math.truncate(Double.parseDouble(recE.getImporteLC()), 2)) {
                log.warn("El importe del encabezado y la suma de los importes de los detalles no coinciden \n");
                // throw new Exception("El importe del encabezado y la suma de
                // los importes de los detalles no coinciden \\n");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return mensajes;
    }

    public boolean getRemanenteValido(String EP, String ejercicio, String clc, double importe) throws SQLException {
        boolean valido = false;
        double remanente = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            remanente = RefasManager.getRemanenteValido(conn, EP, ejercicio, clc);
            if (remanente >= importe)
                valido = true;
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return valido;
    }

    private boolean duplicidad(ArrayList<ReintegroDetalleMil> reinDetalles, ReintegroDetalleMil recD) {
        boolean duplicado = false;
        /**
         * ***************************************************************************************************************
         */
        // Logica para detectar valores duplicados en una lista
        int duplicados = 0;
        Iterator iter = reinDetalles.iterator();
        while (iter.hasNext()) {
            ReintegroDetalleMil valor = (ReintegroDetalleMil) iter.next();
            if (valor.equals(recD)) {
                duplicados++;
            }
            if (duplicados > 1) {
                duplicado = true;
                break;
            }
        }
        /**
         * *******************************************************************************************************************
         */
        return duplicado;
    }

    private boolean mezclaCompensadaEfectivo(ArrayList<ReintegroDetalleMil> reinDetalles) {
        boolean mezcla = false;
        ReintegroDetalleMil rd = reinDetalles.get(0);
        String tipo = rd.getTipoCLC();
        int i;
        for (i = 1; i < reinDetalles.size(); i++) {
            rd = reinDetalles.get(i);
            if (!tipo.equals(rd.getTipoCLC()))
                mezcla = true;
        }
        return mezcla;
    }

    private List<String> generaEncabezadoDetalleRefas(String archivo, Caso c, Usuario usuario, ReintegroEncabezadoMil reinE, ReintegroDetalleMil reinD, ArrayList<ReintegroDetalleMil> reinDetalles, int folio) throws Exception {
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        reinE.setcCentroContable(usuario.getPropiedad("CCENTROCONTABLE").getValor());
        reinE.setnFolioReintegro(folio);
        reinE.setcUnidadResponsableContable("B00");
        reinE.setcTipoPoliza("DI");
        reinE.setfExpedicion(today);
        reinE.setcDocumentoHaplicado("N");
        reinE.setaEjercicioFiscal(adecProy.obtenEjercicioFiscal());
        List<String> errores = new ArrayList<String>();
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
                            if (celda == 0) {
                                reinE.setaEjercicioFiscal("2016");
                                reinE.setEjercicioRefas(valor.replace(".0", ""));
                            }
                            if (celda == 1)
                                reinE.setcRamo(valor.replace(".0", ""));
                            if (celda == 2) {
                                if (!valor.equals(usuario.getU_UR())) {
                                    log.error("La UR del archivo no corresponde a la del usuario \\n");
                                    errores.add("La UR del archivo no corresponde a la del usuario \\n");
                                }
                                reinE.setcUnidadResponsable(valor);
                            }
                            if (celda == 3)
                                reinE.setImporteLC(valor);
                            // if (celda == 4)
                            // reinE.setAviso(valor.replace(".0", ""));
                            if (celda == 4) {
                                reinE.setfSolicitud(today);
                            }
                            if (celda == 5) {
                                if (validaCatMovimiento(valor)) {
                                    reinE.setMovimiento(valor.replace(".0", ""));
                                } else {
                                    log.error("El movimiento no se encuentra en catalogos \\n");
                                    errores.add("El movimiento no se encuentra en catalogos \\n");
                                }
                            }
                            if (celda == 6) {
                                if (validaCatTipoAviso(valor.replace(".0", ""))) {
                                    reinE.setTipoAviso(valor.replace(".0", ""));
                                } else {
                                    log.error("El tipo aviso no se encuentra en catalogos \\n");
                                    errores.add("El tipo aviso no se encuentra en catalogos \\n");
                                }
                            }
                            if (celda == 7) {
                                if (validaCatFormaPago(valor.replace(".0", ""))) {
                                    reinE.setFormaDePago(valor.replace(".0", ""));
                                } else {
                                    log.error("La forma pago no se encuentra en catalogos \\n");
                                    errores.add("La forma pago no se encuentra en catalogos  \\n");
                                }
                            }
                            if (celda == 8) {
                                if (validaCatCausaAviso(valor.replace(".0", ""))) {
                                    reinE.setCausaAviso(valor.replace(".0", ""));
                                } else {
                                    log.error("La causa de aviso no se encuentra en catalogos \\n");
                                    errores.add("La causa de aviso no se encuentra en catalogos \\n");
                                }
                            }
                            // if (celda == 10){
                            // //if("N/A".equals(valor))
                            // reinE.setfAplicacion(today);
                            // //else
                            // // reinE.setfAplicacion(valor);
                            // }
                            // if (celda == 11){
                            // if("N/A".equals(valor))
                            // reinE.setfAcreditacion(today);
                            // else
                            // reinE.setfAcreditacion(valor);
                            // }
                            // if (celda == 12)
                            // reinE.setClvRastreo(valor.replace(".0", ""));
                            // if (celda == 13)
                            // reinE.setFichaDeposito(valor.replace(".0", ""));
                            // if (celda == 14)
                            // reinE.setLc(valor.replace(".0", ""));
                            // if (celda == 15)
                            // reinE.setClvBanco(valor.replace(".0", ""));
                            // if (celda == 16)
                            // reinE.setCuentaBancaria(valor.replace(".0", ""));
                            // if (celda == 17)
                            // reinE.setFolioDependencia(valor.replace(".0",
                            // ""));
                        } else if (fila == 4) {
                            if (celda == 0)
                                reinE.setObservaciones(valor);
                            // if (celda == 1)
                            // reinE.setConcepto(valor);
                        }
                        if (fila > 5) {
                            if (celda == 0) {
                                reinD = new ReintegroDetalleMil();
                                // viene
                                reinD.setnSIAFF(valor.replace(".0", ""));
                                // en
                                // el
                                // archivo
                                // como
                                // CLC
                                // indicando
                                // que
                                // es
                                // el
                                // número
                                // de
                                // CLC,
                                // el
                                // que
                                // proporcionan
                                // es
                                // el
                                // SIAFF, de ahí el nombre para no confundirnos
                                // con el nCLCSicop
                            }
                            if (celda == 1) {
                                reinD.setnDocRenglon(String.valueOf(renglon));
                                renglon++;
                            }
                            if (celda == 2)
                                reinD.setEP(valor);
                            if (celda == 3) {
                                reinD.setmImporteCLC(Double.parseDouble(valor));
                                reinD.setcEvento("REF_TRAM");
                                reinDetalles.add(reinD);
                            }
                        }
                    }
                    celda++;
                } catch (NumberFormatException e) {
                    errores.add("Hubo un error de formato en la fila " + fila + " celda " + celda + "." + e.toString());
                } catch (IllegalStateException exc) {
                    errores.add("Hubo un error de formato de formula en la fila " + fila + " celda " + celda + "." + exc.toString());
                } catch (IndexOutOfBoundsException exc) {
                    errores.add("Hubo un error de formato posiblemente de espacios en blanco en la fila " + fila + " celda " + celda + "." + exc.toString());
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return errores;
    }

    private String valorCelda(Iterator<Cell> cellIterator) {
        String valor = null;
        HSSFCell hssfCell = (HSSFCell) cellIterator.next();
        if (hssfCell.getCellType() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
                Date date = HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue());
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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
			 * int j=0; //String ef = RefasManager.getEntidadFederativa(conn,
			 * datos.get(0).getEP()); while(j<datos.size() && iguales){
			 * //comparamos cada entidad federativa para que sean iguales, ya
			 * que no se debe permitir la diferencia //según los requerimientos
			 * y la plática con la gente de reintegros del día 13 de septiembre
			 * de 2012 //if(!ef.equals(RefasManager.getEntidadFederativa(conn,
			 * datos.get(j).getEP()))) //iguales = false; j++; } if(iguales){
			 * for (int i = 0; i < datos.size(); i++) { String cOBGINI =
			 * datos.get(i).getEP().split("\\.")[9]; String cCTGA =
			 * datos.get(i).getEP().split("\\.")[10]; //String evento =
			 * RefasManager.getEvento(conn, cOBGINI, cCTGA, clc); String evento
			 * = "REIN_TRAM"; //Cuando está en trámite todos los reintegros van
			 * a tener este evento, excepto SPEI
			 * if("REIN_TRAM_SPEI".equals(clc)) evento = "REIN_TRAM_SPEI";
			 * //Evento en trámite SPEI //inserta =
			 * RefasManager.insertaDetReintegro(conn, folio, datos.get(i),
			 * evento, cCentro, getNDocRenglon(datos.get(i).getCxp(),
			 * datos.get(i).getEP(), datos.get(i).getMes())[0]); } inserta =
			 * RefasManager.insertaDetReintegro(conn, folio, null, "", cCentro,
			 * 1); }else{ inserta = false; }
			 */
            inserta = RefasManager.insertaDetReintegro(conn, folio, null, "", cCentro, 1);
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
            inserta = RefasManager.insertaDetReintegroPaso(conn, folio, datos, evento, cCentro, consecutivo, folioDep);
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
            borra = RefasManager.borraDetReintegro(conn, folio);
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
                borra = RefasManager.borraDetReintegroPaso(conn, folio);
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
            if (!"2013".equals(adecProy.obtenEjercicioFiscal())) {
                try {
                    // Descomentar
                    fAplicacion = new java.sql.Date(dateFormatter.parse(cFechaAplica).getTime());
                    // cuando
                    // se
                    // requiera
                    // cambiar
                    // a
                    // 2014
                } catch (Exception e) {
                    java.util.Date today = new java.util.Date();
                    fAplicacion = new java.sql.Date(today.getTime());
                }
            } else {
                fAplicacion = new java.sql.Date(dateFormatter.parse("31/12/2013").getTime());
            }
            inserta = RefasManager.insertaEncReintegro(conn, folio, datos, cCentro, fAplicacion);
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
            res = RefasManager.getEvento(conn, cOBGINI, cCTGA, tipoCLC);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ArrayList<ReintegroDetalle> getRefasDetalle(int folio) throws SQLException {
        Connection conn = null;
        ArrayList<ReintegroDetalle> detalles;
        try {
            conn = getConnection();
            detalles = RefasManager.getRefasDetalle(conn, folio);
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
                    // getcPartida(reintegro.getEP());
                    partida = "2015";
                    if (partida.trim().length() > 0 && partida != null && !"".equals(partida)) {
                        reintegro.setnPartida(partida);
                    } else {
                        reintegrosDetalle.clear();
                        break;
                    }
                } catch (Exception sqle) {
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
            total = RefasManager.getDetallePasoTotal(conn, folio);
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
            res = RefasManager.getCLCPagada(conn, nCLC, cxp);
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
            res = RefasManager.tipoPago(conn, nCLC, cxp);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String AutorizaReintegroNuevo(Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario) throws SQLException {
        // correoProduccion = true;
        List<String> arrLResult = null;
        Connection conn = null;
        String cMensaje = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        String year = adecProy.obtenEjercicioFiscal();
        System.out.println("El año fiscal que se obtiene es::::" + year);
        try {
            int nFolioReintegro = 0;
            int nFolioReintegroDep = 0;
            String fechaAplicacion = "";
            ArrayList numerosSicopSiaff = new ArrayList();
            conn = getConnection();
            int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            // lugar para validar antes de aplicar Brenda Urias//
            RefasManager.autorizaReintegro(conn, c, nNumSicop, cRecMotivSicop, nNumMAP, cRecMotivMAP, uLogin, prefixPath);
            ReintegroEncabezadoMil re = RefasManager.getReintegroEncabezadoNuevo(conn, folio);
            String lineaCaptura = re.getLc();
            RefasBusinessLogic reintegro = new RefasBusinessLogic(GestionInterface.ATT_CONEXION);
            // obtiene
            ReintegroDetalle reD = reintegro.getReinDetSIAFFSICOP(folio);
            // NoCLC
            // y
            // nFolioDependencia
            // para
            // despues
            // buscar
            // el
            // folio
            // siaff
            // y
            // sicop
            if (reD != null) {
                int NoCLC = reD.getNoCLC();
                String nFolioDependencia = reD.getnFolioDependencia();
                // busca
                numerosSicopSiaff = adecProy.buscaDatosSIAFFSICOP(lineaCaptura, folio);
                // datos
                // de
                // reintegro
                // en
                // la
                // tabla
                // de
                // reintegros_clc
                // Brenda
                // Urias
            }
            if (numerosSicopSiaff.size() > 0) {
                nFolioReintegro = (Integer) numerosSicopSiaff.get(0);
                nFolioReintegroDep = (Integer) numerosSicopSiaff.get(1);
                fechaAplicacion = (String) numerosSicopSiaff.get(2);
            }
            ContableInterface conInt = new AplicacionContable();
            log.debug("Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr;
            if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAutEncabezado", "tReintegroAutDetalle", "nFolioReintegroaut", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROAUT", m, prefixPath, uLogin, "");
            else
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAutEncabezado", "tReintegroAutDetalle", "nFolioReintegroaut", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROAUT", m, prefixPath, uLogin, "SI");
            arrLResult = acr.getMessageList();
            log.debug("Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Calendar cal = new GregorianCalendar();
            String mesActual = Util.NOMBRE_MESES_MX[cal.get(Calendar.MONTH)];
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                CargaParametrosCorreo ChargeParamMail = new CargaParametrosCorreo(GestionInterface.ATT_CONEXION);
                /**
                 * *******************************
                 *  Sabemos que trata del proceso de reintegros...
                 * ***************************
                 */
                String proceso = "Reintegros";
                // String proceso=
                // ChargeParamMail.obtieneNombreProceso(c.getIdTC());
                ParametrosCorreo PC = new ParametrosCorreo();
                PC = ChargeParamMail.CargaParametros("Reintegros");
                String fromName = "Avisos de Reintegro";
                String asuntoCorreo = "Aviso de Reintegro " + c.getFolio() + " (Ejercicio " + adecProy.obtenEjercicioFiscal() + ")";
                String to = "";
                String cc = "";
                String bcc = "";
                String from = " ";
                if (PC.getIdAmbiente() == 1) {
                    correoProduccion = true;
                    String usuarioRevisor = RefasManager.getCorreoRevisor(conn, c);
                    String usuariosBitacora = RefasManager.getListaCorreos(conn, c);
                    // String usuarioAutoriza =
                    // RefasManager.getCorreoAutorizador(conn,uLogin);
                    to = usuarioRevisor + ";" + usuariosBitacora;
                    // cc= usuariosBitacora;
                    // to=PC.getPersonaPara();
                    cc = PC.getPersonaPara() + ";" + PC.getPersonaCC();
                } else {
                    correoProduccion = false;
                    to = PC.getPersonaPara();
                    cc = PC.getPersonaCC();
                }
                /**
                 * ***************************************************************************************
                 */
                String body = "";
                if ("2014".equals(year.trim())) {
                    // mesActual = "DICIEMBRE";
                    // (!correoProduccion ? "CORREO DE PRUEBA <br>" : "")
                    body = // + (!correoProduccion ? "este correo le hubiera
                    // llegado a: " + to + cc + "<br> <br>" : "")
                    // + "Cierre de "+mesActual+" de "+year+"<br><br>" +
                    "Cierre de " + mesActual + " de 2015<br><br>" + // +"Cierre de Abril de 2014<br><br>" +
                    "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + " con el folio siguiente: " + nFolioReintegro + " , " + nFolioReintegroDep + " respectivamente" + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>" + "Cabe mencionar que, dentro de la carpeta de 'Comprobante de Pago' deberá de estar adjuntada la siguiente documentación: <br>" + "-       Memorando dirigido  al Lic. Sergio Ramirez Rosales, indicando Ejercicio, Clc y Clave Presupuestal del reintegro<br>" + "-       Comprobante del  pago de cargas financieras, con el nombre, cargo y firma autógrafa del responsable administrativo.<br><br>";
                    // + "Y dentro de la carpeta del 'Reportes' el reporte que
                    // genera el SAI.";
                } else
                    // (!correoProduccion ? "CORREO DE PRUEBA <br>" : "")
                    body = // + (!correoProduccion ? "este correo le hubiera
                    // llegado a: " + to + cc + "<br> <br>" : "")
                    "Cierre de " + mesActual + " de " + year + "<br><br>" + "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + " con el folio siguiente: " + nFolioReintegro + " , " + nFolioReintegroDep + " respectivamente" + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>";
                // + "Dentro de la carpeta del 'Reportes' subir el reporte que
                // genera el SAI del reintegro en mención.";
                try {
                    AlarmaManager.procesaAlarmaCNF(conn, prefixPath, c.getCasoOperacion(0), c, asuntoCorreo, to, cc, bcc, body);
                } catch (Exception exmail) {
                    log.error("No se logro enviar el correo de autorizacion de reintegros: " + exmail);
                }
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REINTEGRO" }, new String[] { "consulta_reintegro" }, m, prefixPath);
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

    @SuppressWarnings({ "unchecked" })
    public void avanzaCasoAutorizado(Caso c, Map m, String prefixPath, String uLogin, String status) throws Exception {
        Connection conn = null;
        try {
            CasoCatalogoLogic cbl = new CasoCatalogoLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            int operacion = 0;
            operacion = c.getCasoOperacion(0).getIdOperacion();
            m = parseMap(c.getCasoDato());
            if (status.equals("autorizada")) {
                switch(operacion) {
                    case 4:
                        cbl.avanzaCaso(c, uLogin, "", new String[] { "AUTORIZADOR_REFAS" }, new String[] { "adjunta_linea_aut" }, m, prefixPath);
                        break;
                    case 5:
                        cbl.avanzaCaso(c, uLogin, "", new String[] { "CAPTURISTA_REFAS" }, new String[] { "digitaliza_comp_pago_aut" }, m, prefixPath);
                        break;
                    case 6:
                        cbl.avanzaCaso(c, uLogin, "", new String[] { "CONSULTA_REFAS" }, new String[] { "consulta_autorizado" }, m, prefixPath);
                        break;
                    default:
                        break;
                }
            }
            if (status.equals("caduca")) {
                cbl.avanzaCaso(c, uLogin, "", new String[] { "CONSULTA_REFAS" }, new String[] { "consulta_cancelado" }, m, prefixPath);
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
            throw exc;
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    private Map parseMap(Map casoDato) {
        Map m = new HashMap<String, String>();
        for (Iterator iter = casoDato.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            CasoDato value = (CasoDato) casoDato.get(name);
            m.put(name, value.getValor());
        }
        return m;
    }

    public String cancelarAppContableNuevo(Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha, String folio) throws Exception {
        String retVal = "Cancelacion Exitosa";
        Connection conn = null;
        try {
            ContableInterface ci = new AplicacionContable();
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            RefasManager.updateFechaCancelacion(folio, conn);
            cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REFAS" }, new String[] { "consulta_cancelado" }, m, prefixPath);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            retVal = exc.getMessage();
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
        List<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        String cMensaje = "Envio Exitoso";
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
            // AplicarContableReturn acr;
            // if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
            // acr = conInt.aplicarContableNuevo(conn, c,
            // "CTRL_DOC..tRefasEncabezado", "tRefasDetalle", "nFolioRefas", new
            // Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') +
            // 1)).intValue(), "REINTEGRO", m, prefixPath, uLogin, "");
            // else
            // acr = conInt.aplicarContableNuevo(conn, c,
            // "tReintegroEncabezado", "tReintegroDetalle", "nFolioReintegro",
            // new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-')
            // + 1)).intValue(), "REINTEGRO", m, prefixPath, uLogin, "SI");
            // arrLResult = acr.getMessageList();
            log.debug("Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            // if (acr.isSuccess()) {
            conn.commit();
            cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "AUTORIZADOR_REFAS" }, new String[] { "genera_layout" }, m, prefixPath);
            // } else {
            // conn.rollback();
            // cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
            // "REVISOR_REINTEGRO" }, new String[] { "revisa_reintegro" },
            // m, prefixPath);
            // }
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
            res = RefasManager.getTipoPoliza(conn, tipoPoliza);
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
            res = RefasManager.getRemanente(conn, EP, CXP.trim(), secClc);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getcPartida(String EP, String ejercicio) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.getcPartida(conn, EP, ejercicio);
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
            res = RefasManager.getEntidadFederativa(conn, EP);
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
            res = RefasManager.getTipoPago(conn, cxp);
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
            re = RefasManager.getReintegroEncabezado(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public ReintegroEncabezadoMil getRefasEncabezadoNuevo(int nFolio) throws SQLException {
        Connection conn = null;
        ReintegroEncabezadoMil re = new ReintegroEncabezadoMil();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = RefasManager.getReintegroEncabezadoNuevo(conn, nFolio);
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
        // SIRVE
        // PARA
        // SABER
        // SI
        // YA
        // HICIERON
        // LA
        // PRIMERA
        // APLICACION
        // CONTABLE
        // Y QUE NO LA VUELVAN A APLICAR
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.getReintegroHApartado(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean getEPCatalogo(String EP, String ejercicio, String clc) throws SQLException {
        boolean existe = false;
        Connection conn = null;
        try {
            conn = getConnection();
            existe = RefasManager.getEPCatalogo(conn, EP, ejercicio, clc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return existe;
    }

    public boolean actualizaInfoPagos(int nFolio, String clvRastreo, String lc, String ficha, String clvBanco, String cuenta, String fechaAcredit) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.actualizaInfoPagos(conn, nFolio, clvRastreo, lc, ficha, clvBanco, cuenta, fechaAcredit);
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
            res = RefasManager.secCLC(conn, caNoContrarrecibo, EP, folio);
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
            res = RefasManager.secCLCRect(conn, caNoContrarrecibo, EP);
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
            res = RefasManager.tipoConcepto(conn, caNoContrarrecibo, EP);
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
            res = RefasManager.folioDependencia(conn, caNoContrarrecibo, EP, folio);
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
            res = RefasManager.getRFC(conn, caNoContrarrecibo, ep);
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
            res = RefasManager.getNDocRenglon(conn, caNoContrarrecibo, EP, cMes, folio);
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
            borra = RefasManager.borraReintegro(conn, folio);
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
            res = RefasManager.validaCatMovimiento(conn, texto);
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
            res = RefasManager.validaCatTipoAviso(conn, texto);
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
            res = RefasManager.validaCatFormaPago(conn, texto);
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
            res = RefasManager.validaCatCausaAviso(conn, texto);
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
            res = RefasManager.datosSICOPMil(conn, caNoContrarrecibo, EP, folio, movimiento.replace(".0", ""), concepto);
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
            res = RefasManager.getRenglonPagadoMil(conn, caNoContrarrecibo, EP, cMes, folio, concepto, movimiento);
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
            res = RefasManager.getALM(conn, caNoContrarrecibo, EP, cMes, folio, renglon);
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
            res = RefasManager.getCCNormal(conn, caNoContrarrecibo, EP, cMes, folio, renglon);
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
                casoTx.avanzaCaso(c, uLogin, "", new String[] { "CAPTURISTA_REINTEGRO" }, new String[] { "captura_reintegro" }, data, "");
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

    public void insertaRefas(ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles, int folio, String folioCompleto, Usuario usuario, String ejercicioRefa) throws Exception, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            RefasManager.insertaRefas(conn, reinE, reinDetalles, folio, folioCompleto, usuario, ejercicioRefa);
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
            res = RefasManager.actualizaFechaAplicacion(conn, folio, fAplicacion);
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

    public ReintegroDetalle getReinDetSIAFFSICOP(int nFolio) throws SQLException {
        Connection conn = null;
        ReintegroDetalle re = new ReintegroDetalle();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = RefasManager.getReinDetSIAFFSICOP(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public void restarRemante(int folio, String tipo, String operacion) throws Exception {
        ArrayList<ReintegroDetalle> refasList = new ArrayList<ReintegroDetalle>();
        try {
            ReintegroEncabezado refa = getRefasEncabezado(folio);
            refasList = getRefasDetalle(folio);
            restarRemanteReintegro(folio, refasList, refa.getEjercicioRefas(), tipo, operacion);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean restarRemanteReintegro(int folio, ArrayList<ReintegroDetalle> refasList, String ejercicioRefas, String tipo, String operacion) throws Exception {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.restaRemanteReintegro(folio, refasList, conn, ejercicioRefas, tipo, operacion);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
            throw exc;
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean restarRemante(ReintegroEncabezadoMil refa, ArrayList<ReintegroDetalleMil> refasList, String ejercicio, String tipo) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.restaRemante(refa, refasList, conn, ejercicio, tipo);
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

    public boolean restarRemante(int folio, ArrayList<ReintegroDetalleMil> refasList, String ejercicio, String tipo) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.restaRemante(folio, refasList, conn, ejercicio, tipo);
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

    public void actualizaFechaUltimaAct(ReintegroEncabezadoMil refa) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            RefasManager.updateFechaAct(refa.getnFolioReintegro(), conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void actualizaFechaUltimaAct(int folio) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            RefasManager.updateFechaAct(folio, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String getFechaAct(String folio) throws SQLException {
        Connection conn = null;
        Date date = null;
        String[] folioString = folio.split("-");
        int intFolio = Integer.parseInt(folioString[2]);
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String dateFormat = null;
        try {
            conn = getConnection();
            date = RefasManager.getFechaAct(intFolio, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        if (date != null)
            dateFormat = sdf.format(date);
        return dateFormat;
    }

    public String getFechaApli(String folio) throws SQLException {
        Connection conn = null;
        Date date = null;
        String[] folioString = folio.split("-");
        int intFolio = Integer.parseInt(folioString[2]);
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String dateFormat = null;
        try {
            conn = getConnection();
            date = RefasManager.getFechaApli(intFolio, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        if (date != null)
            dateFormat = sdf.format(date);
        return dateFormat;
    }

    public void avanzarCaso(String rol, String accion, String uLogin, String prefixPath, Map<?, ?> m, Caso c) {
        Connection conn = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            conn = cbl.getConnection();
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            conn.commit();
            cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { rol }, new String[] { accion }, m, prefixPath);
        } catch (Exception e) {
            log.info(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.info(e);
            }
        }
    }

    public ReintegroEncabezado getRefasEncabezado(int nFolio) {
        Connection conn = null;
        ReintegroEncabezado re = new ReintegroEncabezado();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = RefasManager.getRefasEncabezado(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.info(e);
            }
        }
        return re;
    }

    public ReintegroEncabezado getRefasEncabezado(int nFolio, Connection conn) throws SQLException {
        ReintegroEncabezado re = new ReintegroEncabezado();
        re = RefasManager.getRefasEncabezado(conn, nFolio);
        return re;
    }

    public ArrayList<ReintegroDetalle> getRefasLayout(int folio) throws SQLException {
        Connection conn = null;
        ArrayList<ReintegroDetalle> detalles;
        try {
            conn = getConnection();
            detalles = RefasManager.getRefasLayout(conn, folio);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return detalles;
    }

    public ArrayList<String> getUsersRefas(String folio) throws SQLException {
        Connection conn = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            conn = getConnection();
            list = RefasManager.getUsersRefas(folio, conn);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return list;
    }

    public void enviarCorreos(Map<String, String> mapFolios, String prefixPath) {
        Connection conn = null;
        for (Map.Entry<String, String> entry : mapFolios.entrySet()) {
            try {
                conn = getConnection(GestionInterface.ATT_CONEXION);
                ArrayList<String> listMails = new ArrayList<String>();
                listMails.addAll(getUsersRefas(entry.getKey()));
                ReintegroEncabezado re = getRefasEncabezado(Integer.parseInt(entry.getKey()), conn);
                Caso c = new Caso();
                String folioLargo = "REFA-" + re.getcUnidadResponsable().trim() + "-" + entry.getKey();
                c.setFolio(folioLargo);
                c = CasoManager.select(conn, c);
                enviarCorreo(c, prefixPath, listMails, entry.getKey(), entry.getValue(), conn);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
    }

    public void enviarCorreosReintegros(Map<String, String> mapFolios, String prefixPath) {
        Connection conn = null;
        for (Map.Entry<String, String> entry : mapFolios.entrySet()) {
            try {
                conn = getConnection(GestionInterface.ATT_CONEXION);
                Caso c = new Caso();
                String folioLargo = getFolioCasoReintegro(entry.getKey(), conn);
                c.setFolio(folioLargo);
                c = CasoManager.select(conn, c);
                String listMails = new String();
                listMails = ReintegrosManager.getListaCorreos(conn, c);
                enviarCorreoReintegros(c, prefixPath, listMails, entry.getKey(), entry.getValue());
            } catch (Exception exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
    }

    public void enviarCorreosReintegrosMil(Map<String, String> mapFolios, String prefixPath) {
        Connection conn = null;
        for (Map.Entry<String, String> entry : mapFolios.entrySet()) {
            try {
                conn = getConnection(GestionInterface.ATT_CONEXION);
                Caso c = new Caso();
                String folioLargo = getFolioCasoMil(entry.getKey(), conn);
                c.setFolio(folioLargo);
                c = CasoManager.select(conn, c);
                String listMails = ReintegrosManager.getListaCorreos(conn, c);
                enviarCorreoReintegrosMil(c, prefixPath, listMails, entry.getKey(), entry.getValue());
            } catch (Exception exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
    }

    public void enviarCorreosReintegrosSPEI(Map<String, String> mapFolios, String prefixPath) {
        Connection conn = null;
        for (Map.Entry<String, String> entry : mapFolios.entrySet()) {
            try {
                conn = getConnection(GestionInterface.ATT_CONEXION);
                Caso c = new Caso();
                String folioLargo = getFolioCasoReintegro(entry.getKey(), conn);
                c.setFolio(folioLargo);
                c = CasoManager.select(conn, c);
                String listMails = "maria.falcon@conagua.gob.mx;hector.salazarb@conagua.gob.mx;claudia.soria@conagua.gob.mx";
                enviarCorreoReintegrosSPEI(c, prefixPath, listMails, entry.getKey(), entry.getValue());
            } catch (Exception exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
    }

    public void enviarCorreo(Caso c, String prefixPath, ArrayList<String> listMails, String folio, String estatus, Connection conn) throws NumberFormatException, Exception {
        ParametrosCorreo PC = new ParametrosCorreo();
        CargaParametrosCorreo ChargeParamMail = new CargaParametrosCorreo(GestionInterface.ATT_CONEXION);
        PC = ChargeParamMail.CargaParametros("Reintegros");
        Calendar cal = new GregorianCalendar();
        String mesActual = Util.NOMBRE_MESES_MX[cal.get(Calendar.MONTH)];
        try {
            PC = ChargeParamMail.CargaParametros("Reintegros");
            String fromName = "Avisos de REFAS";
            String asuntoCorreo = "Aviso de REFAS, Folio: " + c.getFolio();
            String to = "";
            String cc = PC.getPersonaPara() + ";" + PC.getPersonaCC();
            String bcc = "";
            String from = " ";
            ReintegroEncabezado re = getRefasEncabezado(Integer.parseInt(folio));
            for (Iterator iterator = listMails.iterator(); iterator.hasNext(); ) {
                String mail = (String) iterator.next();
                to = to + mail + ";";
            }
            ReintegrosBusinessLogic businessLogic = new ReintegrosBusinessLogic(null);
            ArrayList<String> documentos = businessLogic.getDocumentosAnexos(c.getIdGabinete(), "REFAS");
            String body = "";
            if (estatus.equals("pendiente")) {
                cc = PC.getPersonaCC();
                fromName = "Avisos de REFAS";
                asuntoCorreo = "Documentos Faltantes REFAS, Folio: " + c.getFolio();
            }
            if (estatus.equals("autorizada")) {
                body = body + "Cierre de " + mesActual + " de 2016<br><br>";
                body = body + " Para su conocimiento y efectos correspondientes, se le informa que ha sido " + estatus + " en SIAFF y SICOP el Reintegro de Años Anteriores(REFAS " + re.getEjercicioRefas() + ") " + "por $ " + re.getImporteLC() + " con el folio siguiente: " + re.getnFolioTramiteSiaff() + " , " + re.getnFolioTramiteSicop() + " respectivamente" + "<br>" + "Mismo que ya se encuentra con estatus de " + estatus + " en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>";
            } else if (estatus.equals("caduca")) {
                // Para cancelacion
                body = body + " Para su conocimiento y efectos correspondientes, se le informa que ha sido cancelada en SIAFF y SICOP el Reintegro de Años Anteriores(REFAS " + re.getEjercicioRefas() + ") " + "por $ " + re.getImporteLC() + " con el folio siguiente: " + re.getnFolioTramiteSiaff() + " , " + re.getnFolioTramiteSicop() + " respectivamente" + "<br>" + "toda vez que llego a su vigencia y no fue pagada.<br><br>" + "Es importante señalar que, podríamos recibir una observación por las cancelaciones de Líneas de Captura no utilizadas.";
            } else {
                body = body + " Para su conocimiento y efectos correspondientes, se le informa que falta documentación por adjuntar para  el Reintegro de Años Anteriores(REFAS " + re.getEjercicioRefas() + ") " + "por $ " + re.getImporteLC() + " con el folio el SAI: No. " + c.getFolio();
            }
            if (estatus.equals("autorizada") || estatus.equals("pendiente")) {
                if (estatus.equals("autorizada"))
                    body = body + "Cabe mencionar que deberá de estar adjuntada la siguiente documentación: <br>";
                if (estatus.equals("pendiente"))
                    body = body + "<br>Documentación Faltante: <br>";
                if (!documentos.contains("PDF CLC"))
                    body = body + "-      PDF CLC<br>";
                if (!documentos.contains("Solicitud Linea Captura"))
                    body = body + "-      Solicitud Linea Captura<br>";
                if (!documentos.contains("Linea de Captura"))
                    body = body + "-      Linea de Captura<br>";
                if (!documentos.contains("Pago Carga F"))
                    body = body + "-      Pago Carga Financieras<br>";
                if (!documentos.contains("Pago de Refas"))
                    body = body + "-      Pago de Refas<br>";
                if (!documentos.contains("Reporte SICOP"))
                    body = body + "-      Archivo Reporte SICOP<br>";
                if (!documentos.contains("Reporte SIAFF"))
                    body = body + "-      Archivo Reporte SIAFF<br>";
            }
            System.out.println("to:" + to);
            System.out.println("cc:" + cc);
            System.out.println("fromName:" + fromName);
            System.out.println("asunto:" + asuntoCorreo);
            System.out.println("body:" + body);
            c.setIdTC(44);
            CasoOperacion co = new CasoOperacion();
            AlarmaManager.procesaAlarmaCNF(conn, prefixPath, co, c, asuntoCorreo, to, cc, bcc, body);
        } catch (Exception exmail) {
            exmail.printStackTrace();
            log.error("No se logro enviar el correo de autorizacion de reintegros: " + exmail);
        }
    }

    public void enviarCorreoReintegrosSPEI(Caso c, String prefixPath, String listMails, String folio, String estatus) throws NumberFormatException, Exception {
        ParametrosCorreo PC = new ParametrosCorreo();
        Connection conn = getConnection();
        CargaParametrosCorreo ChargeParamMail = new CargaParametrosCorreo(GestionInterface.ATT_CONEXION);
        try {
            PC = ChargeParamMail.CargaParametros("Reintegros");
            String to = listMails;
            String cc = "sergio.ramirezr@conagua.gob.mx;";
            String bcc = "";
            String from = " ";
            ReintegroEncabezado re = ReintegrosManager.getReintegroEncabezado(conn, Integer.parseInt(folio));
            conn = getConnection();
            ReintegrosBusinessLogic businessLogic = new ReintegrosBusinessLogic(null);
            ArrayList<String> documentos = businessLogic.getDocumentosAnexos(c.getIdGabinete(), "REINTEGRO");
            String body = "";
            cc = PC.getPersonaCC();
            String fromName = "Avisos de Reintegros";
            String asuntoCorreo = "Documentos Faltantes Reintegros SPEI, Folio: " + c.getFolio();
            body = body + " Para su conocimiento y efectos correspondientes, se le informa que falta documentación por adjuntar para el Reintegro 2016, el cual fue generado por rechazo SPEI  " + "por $ " + re.getImporteLC() + " con el folio el SAI: No. " + c.getFolio();
            body = body + "<br>Documentación Faltante: <br>";
            if (!documentos.contains("PDF CLC"))
                body = body + "-      PDF CLC<br>";
            if (!documentos.contains("Archivo Linea de Captura"))
                body = body + "-      Archivo Linea de Captura<br>";
            if (!documentos.contains("Comprobante de pago"))
                body = body + "-      Comprobante de pago<br>";
            if (!documentos.contains("Reporte SICOP"))
                body = body + "-      Reporte SICOP<br>";
            if (!documentos.contains("Reporte SIAFF"))
                body = body + "-      Reporte SIAFF<br>";
            if (!documentos.contains("Reporte"))
                body = body + "-      Reporte<br>";
            System.out.println("to:" + to);
            System.out.println("cc:" + cc);
            System.out.println("fromName:" + fromName);
            System.out.println("asunto:" + asuntoCorreo);
            System.out.println("body:" + body);
            c.setIdTC(15);
            CasoOperacion co = new CasoOperacion();
            AlarmaManager.procesaAlarmaCNF(conn, prefixPath, co, c, asuntoCorreo, to, cc, bcc, body);
        } catch (Exception exmail) {
            exmail.printStackTrace();
            log.error("No se logro enviar el correo de autorizacion de reintegros: " + exmail);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    public void actualizaFechaRevision(int folio) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            RefasManager.updateFechaRevision(folio, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String procesoAutomatico(FileInputStream archivo, Usuario usuario, String u_logion, String prefixPath, Map m, HttpServletRequest request) throws Exception {
        Connection conn = null;
        ArrayList<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        String mensaje = "Carga Exitosa";
        ArrayList<ArrayList<String>> lst = new ArrayList<ArrayList<String>>();
        ArrayList<String> lstString = new ArrayList<String>();
        String u_Nombre = usuario.getNombre();
        CargaRefasBusinessLogic businessLogic = new CargaRefasBusinessLogic(null);
        HSSFWorkbook workBook = new HSSFWorkbook(archivo);
        HSSFSheet hssfSheet = workBook.getSheetAt(0);
        ArrayList<ArrayList<String>> list = businessLogic.extraerLineas(lst, lstString, hssfSheet);
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            try {
                conn = getConnection();
                ArrayList<String> arrayList = (ArrayList<String>) iterator.next();
                if (arrayList.isEmpty())
                    continue;
                listMap = RefasManager.insertaDataProcesoAutomatico(conn, arrayList);
                if (listMap.get(0).isEmpty())
                    continue;
                RefasManager.restaRemanenteMap(listMap.get(0), conn);
                RefasManager.avanzarCasos(listMap.get(1), m, prefixPath, u_logion, conn, request);
                enviarCorreos(listMap.get(0), prefixPath);
                conn.commit();
            } catch (Exception e) {
                mensaje = "";
                log.trace("Error en Proceso de Refas");
                e.printStackTrace();
                mensaje = mensaje + e.getMessage();
                listMap.clear();
                conn.rollback();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException exc) {
                    exc.printStackTrace();
                }
                conn = null;
            }
        }
        workBook.close();
        return mensaje;
    }

    public void retornaRemante(String folio, String tipo) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.retornaRemante(folio, conn, tipo);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void getRemanente(String clc, String ep) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RefasManager.retornaRemante(clc, conn, ep);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String enviarCorreosPendientes(String prefixPath, String tipoReintegro) {
        String respuesta = "";
        Map<String, String> listMap = new HashMap();
        if ("mailREFAS".equals(tipoReintegro)) {
            listMap = RefasManager.getFoliosDocPendientes();
            enviarCorreos(listMap, prefixPath);
            respuesta = "Se enviaron:" + listMap.size() + " correos pendientes de REFAS";
        } else if ("mailReintegros".equals(tipoReintegro)) {
            listMap = RefasManager.getFoliosDocPendientesReintegros();
            enviarCorreosReintegros(listMap, prefixPath);
            respuesta = "Se enviaron:" + listMap.size() + " correos pendientes de Reintegros";
        } else if ("mailSPEI".equals(tipoReintegro)) {
            listMap = RefasManager.getFoliosDocPendientesReintegrosSPEI();
            enviarCorreosReintegrosSPEI(listMap, prefixPath);
            respuesta = "Se enviaron:" + listMap.size() + " correos pendientes de Rechazo SPEI";
        } else if ("mailMil".equals(tipoReintegro)) {
            listMap = RefasManager.getFoliosDocPendientesReintegrosMil();
            enviarCorreosReintegrosMil(listMap, prefixPath);
            respuesta = "Se enviaron:" + listMap.size() + " correos pendientes de Reintegro MIL";
        }
        return respuesta;
    }

    public void enviarCorreoReintegros(Caso c, String prefixPath, String listMails, String folio, String estatus) throws NumberFormatException, Exception {
        ParametrosCorreo PC = new ParametrosCorreo();
        Connection conn = getConnection();
        CargaParametrosCorreo ChargeParamMail = new CargaParametrosCorreo(GestionInterface.ATT_CONEXION);
        try {
            PC = ChargeParamMail.CargaParametros("Reintegros");
            String to = listMails;
            String cc = PC.getPersonaPara() + ";" + PC.getPersonaCC();
            String bcc = "";
            String from = " ";
            ReintegroEncabezado re = ReintegrosManager.getReintegroEncabezado(conn, Integer.parseInt(folio));
            conn = getConnection();
            ReintegrosBusinessLogic businessLogic = new ReintegrosBusinessLogic(null);
            ArrayList<String> documentos = businessLogic.getDocumentosAnexos(c.getIdGabinete(), "REINTEGRO");
            String body = "";
            cc = PC.getPersonaCC();
            String fromName = "Avisos de Reintegros";
            String asuntoCorreo = "Documentos Faltantes Reintegros, Folio: " + c.getFolio();
            body = body + " Para su conocimiento y efectos correspondientes, se le informa que falta documentación por adjuntar para el Reintegro 2016 " + "por $ " + re.getImporteLC() + " con el folio el SAI: No. " + c.getFolio();
            body = body + "<br>Documentación Faltante: <br>";
            if (!documentos.contains("PDF CLC"))
                body = body + "-      PDF CLC<br>";
            if (!documentos.contains("PDF CxP"))
                body = body + "-      PDF CxP<br>";
            if (!documentos.contains("Archivo Linea de Captura"))
                body = body + "-      Archivo Linea de Captura<br>";
            if (!documentos.contains("Comprobante de pago"))
                body = body + "-      Comprobante de pago<br>";
            if (!documentos.contains("Reporte SICOP"))
                body = body + "-      Reporte SICOP<br>";
            if (!documentos.contains("Reporte SIAFF"))
                body = body + "-      Reporte SIAFF<br>";
            if (!documentos.contains("Reporte"))
                body = body + "-      Reporte<br>";
            System.out.println("to:" + to);
            System.out.println("cc:" + cc);
            System.out.println("fromName:" + fromName);
            System.out.println("asunto:" + asuntoCorreo);
            System.out.println("body:" + body);
            c.setIdTC(15);
            CasoOperacion co = new CasoOperacion();
            AlarmaManager.procesaAlarmaCNF(conn, prefixPath, co, c, asuntoCorreo, to, cc, bcc, body);
        } catch (Exception exmail) {
            exmail.printStackTrace();
            log.error("No se logro enviar el correo de autorizacion de reintegros: " + exmail);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    public void enviarCorreoReintegrosMil(Caso c, String prefixPath, String listMails, String folio, String estatus) throws NumberFormatException, Exception {
        ParametrosCorreo PC = new ParametrosCorreo();
        Connection conn = getConnection();
        CargaParametrosCorreo ChargeParamMail = new CargaParametrosCorreo(GestionInterface.ATT_CONEXION);
        try {
            PC = ChargeParamMail.CargaParametros("Reintegros");
            String to = listMails;
            String cc = PC.getPersonaPara() + ";" + PC.getPersonaCC();
            String bcc = "";
            String from = " ";
            ReintegroEncabezadoMil re = ReintegrosMilManager.getReintegroEncabezadoNuevo(conn, Integer.parseInt(folio));
            conn = getConnection();
            ReintegrosBusinessLogic businessLogic = new ReintegrosBusinessLogic(null);
            ArrayList<String> documentos = businessLogic.getDocumentosAnexos(c.getIdGabinete(), "REINTEGROMIL");
            String body = "";
            cc = PC.getPersonaCC();
            String fromName = "Avisos de Reintegros";
            String asuntoCorreo = "Documentos Faltantes Reintegros Mil, Folio: " + c.getFolio();
            body = body + " Para su conocimiento y efectos correspondientes, se le informa que falta documentación por adjuntar para el Reintegro Mil 2016 " + "por $ " + re.getImporteLC() + " con el folio el SAI: No. " + c.getFolio();
            body = body + "<br>Documentación Faltante: <br>";
            if (!documentos.contains("PDF CLC"))
                body = body + "-      PDF CLC<br>";
            if (!documentos.contains("PDF CxP"))
                body = body + "-      PDF CxP<br>";
            if (!documentos.contains("Archivo Linea de Captura"))
                body = body + "-      Archivo Linea de Captura<br>";
            if (!documentos.contains("Comprobante de pago"))
                body = body + "-      Comprobante de pago<br>";
            if (!documentos.contains("Reporte SICOP"))
                body = body + "-      Reporte SICOP<br>";
            if (!documentos.contains("Reporte SIAFF"))
                body = body + "-      Reporte SIAFF<br>";
            if (!documentos.contains("Reporte"))
                body = body + "-      Reporte<br>";
            System.out.println("to:" + to);
            System.out.println("cc:" + cc);
            System.out.println("fromName:" + fromName);
            System.out.println("asunto:" + asuntoCorreo);
            System.out.println("body:" + body);
            c.setIdTC(15);
            CasoOperacion co = new CasoOperacion();
            AlarmaManager.procesaAlarmaCNF(conn, prefixPath, co, c, asuntoCorreo, to, cc, bcc, body);
        } catch (Exception exmail) {
            exmail.printStackTrace();
            log.error("No se logro enviar el correo de autorizacion de reintegros: " + exmail);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    public static String getFolioCasoReintegro(String folio, Connection conn) throws SQLException {
        ResultSet rs = null;
        String folioCaso = "";
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT C_FOLIO FROM cg_caso WHERE C_FOLIO like('AVI -%-" + folio + "') AND ID_TC=15");
            rs = pstm.executeQuery();
            if (rs.next()) {
                folioCaso = rs.getString(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return folioCaso;
    }

    public static String getFolioCasoMil(String folio, Connection conn) throws SQLException {
        ResultSet rs = null;
        String folioCaso = "";
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT C_FOLIO FROM cg_caso WHERE C_FOLIO like('AVIN-%-" + folio + "') AND ID_TC=26");
            rs = pstm.executeQuery();
            if (rs.next()) {
                folioCaso = rs.getString(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return folioCaso;
    }

    public ArrayList<String> getDocumentosAnexos(int idGabinete, String destino) throws SQLException {
        Connection conn = null;
        ArrayList<String> documentos = new ArrayList<String>();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            documentos = ReintegrosManager.getDocumentosAnexos(conn, idGabinete, destino);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return documentos;
    }
}
