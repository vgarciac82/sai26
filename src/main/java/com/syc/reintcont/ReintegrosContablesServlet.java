/**
 */
package com.syc.reintcont;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.servlet.ReintegrosServlet;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReintegrosContablesServlet", urlPatterns = { "/servlet/ReintegrosContablesServlet" })
public class ReintegrosContablesServlet extends HttpServlet {

    private static final long serialVersionUID = 3830246252504144684L;

    private static final Logger log = Logger.getLogger(ReintegrosServlet.class);

    private String jniName = null;

    private static String folioGenerator = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    //para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    ReintegroContBussinesLogic reintegro = new ReintegroContBussinesLogic(GestionInterface.ATT_CONEXION);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String aEjercicioFiscal = "";
        try {
            aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
        } catch (Exception e) {
            throw new ServletException(e);
        }
        if (Integer.parseInt(aEjercicioFiscal) != c1.get(Calendar.YEAR))
            today = "31/12/" + aEjercicioFiscal;
        String mensaje = "";
        String cancelaDocumento = request.getParameter("cancelaDocumento");
        String generaExcel = request.getParameter("generaExcel");
        String folio = request.getParameter("folio");
        String autoriza = request.getParameter("autoriza");
        String aplica = request.getParameter("aplica");
        String cancela = request.getParameter("cancela");
        String borraTodo = request.getParameter("borraTodo");
        String generaCaso = request.getParameter("generaCaso");
        String ctab = request.getParameter("ctab");
        String fAcredit = request.getParameter("fAcredit");
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        if (cancelaDocumento != null && !"".equals(cancelaDocumento) && "true".equals(cancelaDocumento)) {
            try {
                if (folio != null && !"".equals(folio)) {
                    mensaje += cancelaReintegro(folio);
                } else {
                    mensaje += "Todos los campos deben llenarse";
                }
            } catch (Exception ex) {
                log.warn(ex);
                mensaje += ex.toString();
                log.warn(mensaje);
            } catch (IllegalAccessError e) {
                mensaje += e.toString();
                log.warn(mensaje);
            } finally {
                response.sendRedirect("../plantillasCasos/cancelaDocumentoManual.jsp?mensaje=" + mensaje);
            }
        }
        if (aplica != null && !"".equals(aplica) && "1".equals(aplica)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                reintegro.actualizaFechaAplicacion(new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), today);
                out.println(new String(reintegro.ValidaReintegro(new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), usuario, adecProy.obtenEjercicioFiscal(), usuario.getU_Ramo(), usuario.getU_UR(), c, usuario.getPropiedad("CCENTROCONTABLE").getValor(), c.getCasoDato("FECHA_AP_CONT").getValor(), m, prefixPath, usuario.getLogin()).getBytes("UTF-8"), "ISO-8859-1"));
            } catch (Exception ex) {
                ex.printStackTrace();
                log.warn(mensaje);
            }
        }
        if (autoriza != null && !"".equals(autoriza) && "1".equals(autoriza)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                String ipNombreServidor = java.net.InetAddress.getByName(request.getServerName()).toString();
                String[] ipServidor = ipNombreServidor.split("/");
                if (ipServidor[1].equals(GestionInterface.SYS_IP_PRODUCCION))
                    reintegro.correoProduccion = true;
                out.println(new String(reintegro.AutorizaReintegroNuevo(c, "", "", "", "", m, prefixPath, usuario.getLogin(), usuario, fAcredit).getBytes("UTF-8"), "ISO-8859-1"));
            } catch (Exception ex) {
                ex.printStackTrace();
                log.warn(ex);
            }
        }
        if (generaCaso != null && !"".equals(generaCaso) && "3".equals(generaCaso)) {
            //Reintegros Contables
            try {
                generaCasoInsertsContable(response, request);
            } catch (SQLException e) {
                e.printStackTrace();
                log.warn(e);
            } catch (GestionException e) {
                e.printStackTrace();
                log.warn(e);
            } catch (Exception e) {
                e.printStackTrace();
                log.warn(e);
            }
        }
        if (cancela != null && !"".equals(cancela) && "1".equals(cancela)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                out.println(new String(reintegro.cancelarAppContableNuevo(c, m, prefixPath, usuario.getLogin(), c.getCasoDato("FECHA_AP_CONT").getValor()).getBytes("UTF-8"), "ISO-8859-1"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    public String cancelaReintegro(String folio) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            AccountingEngine ae = new AccountingEngine();
            conn = ae.getConnection();
            //CANCELAR REINTEGROAUT
            boolean reintegroAutResultado = ae.cancelAccountingApplication(conn, "REINTEGROAUT", folio, "tReintegroAutEncabezado", "tReintegroAutDetalle", "nFolioReintegroAut");
            mensaje += "Cancela reintegroAut Resultado:" + new Boolean(reintegroAutResultado).toString();
            //CANCELAR REINTEGRO
            boolean reintegroResultado = ae.cancelAccountingApplication(conn, "REINTEGRO", folio, "tReintegroEncabezado", "tReintegroDetalle", "nFolioReintegro");
            mensaje += ", Cancela reintegro Resultado:" + new Boolean(reintegroResultado).toString();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            mensaje += "No ha sido posible realizar la cancelación <br> <br> " + e.toString();
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        log.warn(mensaje);
        return mensaje;
    }

    public void generaCasoInsertsContable(HttpServletResponse response, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Caso cRein = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class<?> clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        cRein = generaCaso(u, 52, fg, "CAPTURA_REINTEGROCONT", "Reintegro Contable");
        int idCasoReintegro = new Integer(cRein.getFolio().substring(cRein.getFolio().lastIndexOf('-') + 1)).intValue();
        String folioCasoReintegro = cRein.getFolio();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String[] valuesCLC = request.getParameterValues("clc");
        String[] valuesCXP = request.getParameterValues("cxp");
        String[] valuesEP = request.getParameterValues("ep");
        String[] valuesMes = request.getParameterValues("mes");
        String[] valuesImporte = request.getParameterValues("importe");
        String[] valuesCentroContableCxP = request.getParameterValues("centrocontablecxp");
        ReintegroContEncabezado re = new ReintegroContEncabezado();
        re.setObservaciones(new String(request.getParameter("observaciones").replaceAll("\r\n", "").replaceAll("\n", "").getBytes("ISO-8859-1"), "UTF-8"));
        re.setConcepto(new String(request.getParameter("concepto").replaceAll("\r\n", "").replaceAll("\n", "").getBytes("ISO-8859-1"), "UTF-8"));
        re.setMovimiento(request.getParameter("CatMovimientoReintegro"));
        re.setTipoAviso(request.getParameter("CatTipoCausaAvisoReintegro"));
        re.setCausaAviso(request.getParameter("CatCausaAvisoReintegro"));
        re.setFormaDePago(request.getParameter("CatFormaPagoAvisoReintegro"));
        re.setaEjercicioFiscal(adecProy.obtenEjercicioFiscal());
        re.setcCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        re.setcRamo(u.getU_Ramo());
        re.setcUnidadResponsableContable("RHQ");
        //TIPO POLIZA REINTEGRO CONTABLE
        re.setcTipoPoliza("PD");
        re.setcDocumentoHaplicado("N");
        re.setLc("N/A");
        re.setcUnidadResponsable(u.getU_UR());
        re.setnFolioReintegro(idCasoReintegro);
        re.setfSolicitud(fecha);
        re.setClvRastreo("N/A");
        re.setFichaDeposito("N/A");
        re.setClvBanco("N/A");
        re.setCuentaBancaria("N/A");
        re.setnId_TipoReintegro(Integer.parseInt(request.getParameter("CatTipoReintegro")));
        ArrayList<ReintegroContDetalle> rd = new ArrayList<ReintegroContDetalle>();
        ReintegroContDetalle rdaux;
        double sumaImportes = 0;
        for (int i = 0; i < valuesCLC.length; i++) {
            rdaux = new ReintegroContDetalle();
            rdaux.setnSIAFF(valuesCLC[i]);
            rdaux.setCxp(valuesCXP[i]);
            rdaux.setEP(valuesEP[i]);
            rdaux.setMes(Integer.parseInt(valuesMes[i]));
            rdaux.setmImporteCLC(Double.parseDouble(valuesImporte[i]));
            rdaux.setnDocRenglon(String.valueOf(i + 1));
            rdaux.setRenglonPagado(String.valueOf(reintegro.getNDocRenglon(rdaux.getCxp(), rdaux.getEP(), rdaux.getMes(), 0)[0]));
            rdaux.setcCentroContable(valuesCentroContableCxP[i]);
            rdaux.setAlm(reintegro.getALM(rdaux.getCxp(), rdaux.getEP(), rdaux.getMes(), 0, Integer.parseInt(rdaux.getnDocRenglon())));
            rdaux.setcPartida(reintegro.getcPartida(rdaux.getEP()));
            rdaux.setSecCLC(String.valueOf(reintegro.secCLC(String.valueOf(rdaux.getnSIAFF()).replace(".0", ""), rdaux.getEP(), 0)));
            rdaux.setFolioDependenciaSicop(reintegro.folioDependencia(String.valueOf(rdaux.getnSIAFF()).replace(".0", ""), rdaux.getEP(), 0));
            rdaux.setRfc(reintegro.getRFC(rdaux.getCxp(), rdaux.getEP()));
            rdaux.setcEvento("REIN_TRAM");
            rdaux.setObgt(rdaux.getEP().substring(31, 36));
            sumaImportes += rdaux.getmImporteCLC();
            rd.add(rdaux);
        }
        re.setImporteLC(String.valueOf(sumaImportes));
        reintegro.insertaReintegro(re, rd, idCasoReintegro, folioCasoReintegro, u);
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        ITree tree = casoTx.getArbolCaso(cRein);
        session.setAttribute(GestionInterface.ATT_TREE, tree);
        session.setAttribute(GestionInterface.ATT_CASE, cRein);
        response.sendRedirect("../caso/exec-container.jsp");
    }

    private Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String concepto) throws GestionException, SQLException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, idTCaso, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(adecProy.obtenEjercicioFiscal());
        c.getCasoDato("CONCEPTO_MOV").setValor(concepto);
        c.getCasoDato("MONEDA").setValor("MXP");
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", adecProy.obtenEjercicioFiscal());
        m.put("CONCEPTO_MOV", "Reintegro Presupuestal");
        m.put("MONEDA", "MXP");
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        cobl.updateCasoResponsable(co, opResponsable);
        return c;
    }
}
