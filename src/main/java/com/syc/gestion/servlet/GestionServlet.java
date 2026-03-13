package com.syc.gestion.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.auditoria.AuditoriaBusinessLogic;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.core.Saldo;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.MensajesBusinessLogic;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Mensaje;
import com.syc.gestion.core.MensajeManager;
import com.syc.gestion.core.URLDocumento;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.custom.GenericInterface;
import com.syc.gestion.custom.XmlGeneratorInterface;
import com.syc.itam.GeneraLlaves;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GestionServlet", urlPatterns = { "/gstnmngr/gestion" })
public class GestionServlet extends HttpServlet implements GestionInterface {

    private DataSource ds = null;

    private String corregir_calis = "NO";

    public static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionServlet.class);

    private String jniName = null;

    private String folioGenerator = null;

    private String xmlGenerator = null;

    private Usuario u;

    public static String reportPath = "";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reportPath = getServletContext().getRealPath("Reportes" + File.separator);
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
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(jniName);
        } catch (NamingException ne) {
            try {
                initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:comp/env");
                ds = (DataSource) envContext.lookup(jniName);
            } catch (NamingException nexc) {
                try {
                    initContext = new InitialContext();
                    ds = (DataSource) initContext.lookup(jniName);
                } catch (NamingException exc) {
                    ne.printStackTrace();
                    exc.printStackTrace();
                    throw new RuntimeException("No se encontro la fuente '" + jniName + "'");
                }
            }
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
        try {
            InitialContext ic = new InitialContext();
            xmlGenerator = (String) ic.lookup("java:comp/env/xmlGeneratorInterface");
            if (xmlGenerator == null) {
                log.debug("Environment Entry \"xmlGeneratorInterface\" no definida");
            } else
                log.info("xmlGeneratorInterface=" + xmlGenerator);
        } catch (NamingException exc) {
            xmlGenerator = null;
            log.debug("Environment Entry \"xmlGeneratorInterface\" no definida");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String strCmd = req.getParameter(PRM_CMD);
            String redireccionaEjercicio = req.getParameter("redirEjer");
            if (strCmd != null) {
                int cmd = Integer.parseInt(strCmd);
                if (cmd == CMD_OPEN_INBOX || cmd == CMD_GET_DOCUMENT_FOR_NAME || cmd == CMD_DOWNLOAD_KEYS || "no".equals(redireccionaEjercicio)) {
                    doPost(req, resp);
                    return;
                } else if (cmd == CMD_FILTRA_INBOX) {
                    doPost(req, resp);
                    return;
                }
            }
            log.info("Invalidando sesion");
            session.invalidate();
        }
        // resp.sendRedirect("../index.jsp");
        // por petición de Sergio Cruz, aunque el redireccionamiento dinámico ya
        // sirve bien
        resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + "/sai");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        java.util.Date di = new java.util.Date();
        System.out.println("Entrando time: " + di.toString());
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            // resp.sendRedirect("../index.jsp");
            // por petición de Sergio Cruz, aunque el redireccionamiento
            // dinámico ya sirve bien
            resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + "/sai");
            // <script language="javascript">self.top.location.href =
            // "../index.jsp";</script>
            return;
        }
        // Usuario u = (Usuario) session.getAttribute(ATT_USER);
        u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            // resp.sendRedirect("../index.jsp");
            // por petición de Sergio Cruz, aunque el redireccionamiento
            // dinámico ya sirve bien
            resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + "/sai");
            return;
        }
        String strCmd = req.getParameter(PRM_CMD);
        if (strCmd == null) {
            log.error("Llamada inválida sin comando");
            throw new ServletException("Llamada inválida sin comando");
        }
        boolean close = "true".equals(req.getParameter("close"));
        int command = Integer.parseInt(strCmd);
        switch(command) {
            case // 0
            CMD_MAIN:
                if (log.isDebugEnabled())
                    log.debug("Llamando a Principal");
                resp.sendRedirect("../caso/principal.jsp");
                break;
            case // 1
            CMD_OPEN_INBOX:
                try {
                    long startInbox = System.currentTimeMillis();
                    if (log.isDebugEnabled())
                        log.debug("Actualizando InBox");
                    long startDepuracion = System.currentTimeMillis();
                    log.debug("Iniciando depuracion de casos duplicados");
                    AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                    int eliminados = adbl.eliminaRegistrosDuplicados();
                    adbl = null;
                    long stopDepuracion = System.currentTimeMillis();
                    log.info("Tardo " + (stopDepuracion - startDepuracion) / 1000 + " segundos en depurar Duplicados");
                    log.info("Se eliminaron: " + eliminados + " casos repetidos");
                    long startUpdateInbox = System.currentTimeMillis();
                    actualizaInbox(jniName, req);
                    long stopUpdateInbox = System.currentTimeMillis();
                    log.info("Tardo " + (stopUpdateInbox - startUpdateInbox) / 1000 + " segundos en actualizar inbox.");
                    String valorFiltro = req.getParameter("valorFiltro");
                    String fltr = req.getParameter(PRM_PROM_FILTER);
                    fltr = (fltr == null) ? new String() : "?" + PRM_PROM_FILTER + "=" + fltr;
                    String refresh = req.getParameter("refresh");
                    if (refresh == null)
                        refresh = (String) session.getAttribute("refresh");
                    refresh = (refresh == null) ? "30" : refresh;
                    session.setAttribute("refresh", refresh);
                    long stopInbox = System.currentTimeMillis();
                    log.info("Tardo " + (stopInbox - startInbox) / 1000 + " segundos en cargar el inbox.");
                    resp.sendRedirect("../caso/inbox2.jsp" + (valorFiltro != null ? "?valorFiltro=" + valorFiltro : ""));
                } catch (GestionException exc) {
                    log.error("Actualizando InBox", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 1
            CMD_OPEN_INBOX2:
                try {
                    long startInbox = System.currentTimeMillis();
                    if (log.isDebugEnabled())
                        log.debug("Actualizando InBox");
                    long startDepuracion = System.currentTimeMillis();
                    log.debug("Iniciando depuracion de casos duplicados");
                    AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                    int eliminados = adbl.eliminaRegistrosDuplicados();
                    adbl = null;
                    long stopDepuracion = System.currentTimeMillis();
                    log.info("Tardo " + (stopDepuracion - startDepuracion) / 1000 + " segundos en depurar Duplicados");
                    log.info("Se eliminaron: " + eliminados + " casos repetidos");
                    long startUpdateInbox = System.currentTimeMillis();
                    actualizaInbox(jniName, req);
                    long stopUpdateInbox = System.currentTimeMillis();
                    log.info("Tardo " + (stopUpdateInbox - startUpdateInbox) / 1000 + " segundos en actualizar inbox.");
                    String valorFiltro = req.getParameter("valorFiltro");
                    String fltr = req.getParameter(PRM_PROM_FILTER);
                    fltr = (fltr == null) ? new String() : "?" + PRM_PROM_FILTER + "=" + fltr;
                    String refresh = req.getParameter("refresh");
                    if (refresh == null)
                        refresh = (String) session.getAttribute("refresh");
                    refresh = (refresh == null) ? "30" : refresh;
                    session.setAttribute("refresh", refresh);
                    long stopInbox = System.currentTimeMillis();
                    log.info("Tardo " + (stopInbox - startInbox) / 1000 + " segundos en cargar el inbox.");
                    resp.sendRedirect("../caso/inbox.jsp" + (valorFiltro != null ? "?valorFiltro=" + valorFiltro : ""));
                } catch (GestionException exc) {
                    log.error("Actualizando InBox", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 2
            CMD_INIT_CASE:
                if ("-1".equals(req.getParameter("id_tc"))) {
                    if (log.isDebugEnabled())
                        log.debug("Iniciando Caso sin seleccion");
                    resp.sendRedirect("../caso/inbox.jsp?" + PRM_USER_MSG + "=Debe seleccionar un Caso para iniciarlo, por favor");
                } else {
                    if (log.isDebugEnabled())
                        log.debug("Iniciando Caso");
                    try {
                        Caso c = iniciaCaso(req);
                        session.setAttribute(ATT_CASE, c);
                        resp.sendRedirect("../caso/exec-container.jsp");
                    } catch (GestionException exc) {
                        log.error("Iniciando Caso", exc);
                        throw new ServletException(exc);
                    }
                }
                break;
            case // 3
            CMD_EXEC_CASE:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Ejecutando Caso");
                    Caso c = ejecutaCaso(req);
                    session.setAttribute(ATT_CASE, c);
                    resp.sendRedirect("../caso/exec-container.jsp");
                } catch (GestionException exc) {
                    log.error("Ejecuntando Caso", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 4
            CMD_ADVANCE_CASE:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Avanzando Caso");
                    // Ethiel, se agrega el response
                    avanzaCaso(req, resp);
                    try {
                        // Solo adecuaciones en operacion 5 y 2
                        Caso c = (Caso) session.getAttribute(ATT_CASE);
                        if (c != null && c.getIdTC() == 3 && (c.getCasoOperacion(0).getIdOperacion() == 5 || c.getCasoOperacion(0).getIdOperacion() == 2)) {
                            ArrayList<Saldo> arradec = (ArrayList<Saldo>) session.getAttribute("objAdecuacion");
                            if (arradec.size() < 20)
                                Thread.sleep(5000);
                        }
                    } catch (Exception ex) {
                        log.debug("Tiempo de espera para refrescar inbox de adecuaciones: " + ex);
                    }
                    actualizaInbox(jniName, req);
                    resp.sendRedirect("../caso/inbox.jsp");
                } catch (GestionException exc) {
                    log.error("Avanzando Caso", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 6
            CMD_MSG_COMPOSE:
                if (log.isDebugEnabled())
                    log.debug("Nuevo Mensaje");
                resp.sendRedirect("../caso/composemsg.jsp?close=" + close);
                break;
            case // 7
            CMD_SEND_MSG:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Enviando Mensaje");
                    if (enviaMensaje(req))
                        session.setAttribute(ATT_MSG, "Mensaje enviado existosamente");
                    else
                        session.setAttribute(ATT_MSG, "No se logr&oacute; enviar el mensaje");
                    resp.sendRedirect("../caso/sendedmsg.jsp?close=" + close);
                } catch (GestionException exc) {
                    log.error("Enviando Mensaje", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 8
            CMD_MSG_SENDED:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Mensajes Enviados");
                    actualizaMensajes(req, MensajeManager.MSG_ENVIADO);
                    resp.sendRedirect("../caso/messages.jsp?" + PRM_MSG_TYPE + "=" + CMD_MSG_SENDED);
                } catch (GestionException exc) {
                    log.error("Mensajes Enviados", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 9
            CMD_MSG_RECIVED:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Mensajes Recibidos");
                    actualizaMensajes(req, MensajeManager.MSG_SIN_LEER);
                    resp.sendRedirect("../caso/messages.jsp?" + PRM_MSG_TYPE + "=" + CMD_MSG_RECIVED);
                } catch (GestionException exc) {
                    log.error("Mensajes Recibidos", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 10
            CMD_MSG_READED:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Mensajes Leidos");
                    actualizaMensajes(req, MensajeManager.MSG_LEIDO);
                    resp.sendRedirect("../caso/messages.jsp?" + PRM_MSG_TYPE + "=" + CMD_MSG_READED);
                } catch (GestionException exc) {
                    log.error("Mensajes Leidos", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 11
            CMD_SEND_CHNG_PWD:
                if (log.isDebugEnabled())
                    log.debug("Cambio de contraseña");
                resp.sendRedirect("../caso/chngpwd.jsp");
                break;
            case // 12
            CMD_CHNG_PWD:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Cambiando contraseña");
                    if (cambiaPassword(req, resp))
                        resp.sendRedirect("../caso/chngdpwd.jsp");
                    else
                        resp.sendRedirect("../caso/chngpwd.jsp");
                } catch (GestionException exc) {
                    log.error("Cambiando contraseña", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 13
            CMD_GET_USRES_GROUPS:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Obteniendo lista usuarios/grupos");
                    getListaUsersGroups(resp);
                } catch (GestionException exc) {
                    log.error("Obteniendo lista usuarios/grupos", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 14
            CMD_GET_BODY_MSG:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Leyendo mensaje");
                    readMessage(req, resp);
                } catch (GestionException exc) {
                    log.error("Leyendo mensaje", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 15
            CMD_DEL_MSG:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Borrando mensaje");
                    deleteMessage(req, resp);
                } catch (GestionException exc) {
                    log.error("Borrando mensaje", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 16
            CMD_GET_USRES_GROUPS_OPER:
                try {
                    if (log.isDebugEnabled())
                        log.debug("Obteniendo lista operaciones/usuarios/grupos");
                    getListaUsersGroupsOper(req, resp);
                } catch (GestionException exc) {
                    log.error("Obteniendo lista operaciones/usuarios/grupos", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 17
            CMD_GENERIC_INTERFACE:
                try {
                    genericInterface(req, resp);
                } catch (GestionException exc) {
                    log.error("Llamando interfaz generica", exc);
                }
                break;
            case // 18
            CMD_GET_DOCUMENT_FOR_NAME:
                try {
                    URLDocumento urlDoc = getDocumentoPorNombre(req);
                    String url = urlDoc.getURL(req);
                    resp.sendRedirect(url);
                } catch (GestionException exc) {
                    log.error("Obteniendo documento por nombre", exc);
                }
                break;
            case // 19
            CMD_CREATE_KEYS:
                GeneraLlaves gl = new GeneraLlaves(ds, u.getNombre(), u.getLogin());
                boolean generadas = false;
                Object[] llaves = null;
                try {
                    llaves = gl.generaLlaves(u.getLogin());
                    generadas = true;
                } catch (Exception exc) {
                    log.error("Generando llaves", exc);
                    throw new ServletException("Generando llaves: " + exc.getMessage());
                }
                if (generadas) {
                    if (session.getAttribute("llaveCertificado") != null)
                        session.removeAttribute("llaveCertificado");
                    session.setAttribute("llaveCertificado", llaves);
                }
                resp.sendRedirect("../caso/genera_llaves.jsp?generadas=" + generadas);
                break;
            case // 20
            CMD_DOWNLOAD_KEYS:
                if (session.getAttribute("llaveCertificado") != null || !(session.getAttribute("llaveCertificado") instanceof Object[])) {
                    Object[] llaveCertificado = (Object[]) session.getAttribute("llaveCertificado");
                    // 0-certificado, 1-llave privada
                    boolean isKey = req.getParameter("opt").equalsIgnoreCase("key");
                    byte[] objeto = isKey ? (byte[]) llaveCertificado[1] : (byte[]) llaveCertificado[0];
                    resp.setContentType("application/octet-stream");
                    resp.setContentLength(objeto.length);
                    resp.setHeader("Content-Disposition", "attachment; filename=" + (isKey ? "llaveprivada" : "certificadopublico") + "cgitam" + u.getLogin() + ".key");
                    ServletOutputStream sos = resp.getOutputStream();
                    sos.write(objeto);
                    sos.flush();
                    sos.close();
                } else {
                    log.error("Haciendo download de llaves", new Exception("No viene el llaveCertificado en la sesion, o no es un Object[]"));
                    throw new ServletException("No vienen la llave y certificado, imposible bajar.");
                }
                break;
            case // 21
            CMD_DELETE_CASE:
                try {
                    deleteCaso(req);
                    actualizaInbox(jniName, req);
                    resp.sendRedirect("../caso/inbox.jsp");
                } catch (GestionException exc) {
                    log.error("Mensajes Enviados", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 22 al cerrar el caso el usuario lo
            CMD_USER_LEAVE_CASE:
                // libera para que lso demas del grupo
                // lo puedan ver
                try {
                    // MLR RO-0001 Libera el caso al abandonar la pantalla de
                    // obra pública. Se agreg falg de redirect
                    String redirectStr = req.getParameter("redirect");
                    boolean redirect = true;
                    if (redirectStr != null)
                        redirect = "true".equalsIgnoreCase(redirectStr);
                    Caso c = (Caso) session.getAttribute(ATT_CASE);
                    /*
					 * if (c.getIdTC() == 23 &&
					 * (c.getCasoOperacion(0).getIdOperacion() >= 6 ) ){
					 * resp.sendRedirect("../caso/inbox.jsp"); return; }
					 */
                    liberaCaso(req);
                    if (c.getCasoOperacion(0).getOperacion().getNombre().toUpperCase().contains("CONSULTA")) {
                        // resp.sendRedirect("../caso/inboxC.jsp?gavetaAsociada="+c.getTipoCaso().getGavetaAsociada());
                        if (redirect)
                            // no lo
                            resp.sendRedirect("../caso/search.jsp");
                        // podemos
                        // regresar
                        // a la
                        // de
                        // consulta
                        // a
                        // menos
                        // que
                        // conservemos
                        // los
                        // filtros
                    } else {
                        actualizaInbox(jniName, req);
                        if (redirect)
                            resp.sendRedirect("../caso/inbox.jsp");
                    }
                    log.info("Caso: " + c.getFolio() + " liberado");
                } catch (GestionException exc) {
                    log.error("Mensajes Enviados", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 35
            RPT_HOMOVIATI:
                if (log.isDebugEnabled())
                    log.debug("Reporte de Homologacion de Viaticos");
                resp.sendRedirect("../admin/reporte_frame.jsp?id=8");
                break;
            case // 36
            CMD_SEND_RESET_PWDS:
                if (log.isDebugEnabled())
                    log.debug("Reiniciado contraseñas");
                resp.sendRedirect("ReinciarContra");
                break;
            case CMD_FILTRA_INBOX:
                try {
                    actualizaInboxFiltros(req, resp);
                } catch (Exception exc) {
                    log.error("Actualizando InBox", exc);
                    throw new ServletException(exc);
                }
                break;
            case // 99
            CMD_EXIT:
                // Ethiel, para registrar la operacion en IMX_AUDITORIA
                try {
                    Empleado e = (Empleado) session.getAttribute(ATT_EMPLEADO);
                    EmpleadoArea ea = e.getArea();
                    AuditoriaBusinessLogic ABL = new AuditoriaBusinessLogic(GestionInterface.ATT_CONEXION);
                    ABL.agregaAuditoria(u.getLogin(), ea.getId(), GestionInterface.ATT_CONEXION.substring(GestionInterface.ATT_CONEXION.indexOf("/") + 1), "Acceso", "Logout", u.getLogin(), u.getLogin(), "SELECT * FROM CG_USUARIO WHERE U_LOGIN=" + u.getLogin(), u.getLogin());
                } catch (Exception audex) {
                    log.error("Error escribiendo en la bitacora de accesos al intentar login de:" + u.getLogin());
                    audex.printStackTrace();
                }
                try {
                    session.invalidate();
                } catch (Exception ex) {
                    log.warn("Tratando de cerrar session de SAI");
                    // ex.printStackTrace();
                }
                // resp.sendRedirect("../index.jsp");
                // por petición de Sergio Cruz, aunque el redireccionamiento
                // dinámico ya sirve bien
                String contexto = req.getContextPath().indexOf("_") > 0 ? (req.getContextPath().substring(0, req.getContextPath().indexOf("_"))) : req.getContextPath();
                resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + contexto);
                break;
            default:
                log.error("Comando desconocido");
                throw new ServletException("Comando desconocido (" + command + ")");
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private void actualizaInbox(String jniName, HttpServletRequest req) throws GestionException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        CasoOperacionBusinessLogic casoOperTx = new CasoOperacionBusinessLogic(jniName);
        String u_login = u.getLogin();
        String UR = u.getU_UR();
        String cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
        session.setAttribute(ATT_INBOX, casoOperTx.getInbox(u_login, " ORDER BY o.o_descripcion DESC, co.co_fecha_ini ASC", UR, cCentroContable));
    }

    private Caso iniciaCaso(HttpServletRequest req) throws GestionException {
        HttpSession session = req.getSession(false);
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        String tCaso = req.getParameter("id_tc");
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        }
        // c = casoTx.IniciaCaso(u.getNombre(), idTC, fg);
        c = casoTx.IniciaCaso(u, idTC, fg);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        ITree tree = casoTx.getArbolCaso(c);
        session.setAttribute(ATT_TREE, tree);
        return c;
    }

    private Caso ejecutaCaso(HttpServletRequest req) throws GestionException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        String idCaso = req.getParameter(PRM_CASE);
        String idCasoOper = req.getParameter(PRM_CASE_OPER);
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        if (idCaso == null) {
            log.error("Llamada invalida, sin identificador de caso");
            throw new GestionException("Llamada inválida, sin identificador de caso");
        }
        if (idCasoOper == null) {
            log.error("Llamada invalida, sin identificador de caso operacion");
            throw new GestionException("Llamada inválida, sin identificador de caso operación");
        }
        int id_caso = Integer.parseInt(idCaso);
        int id_caso_oper = Integer.parseInt(idCasoOper);
        if (id_caso <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (id_caso_oper <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        Caso c = casoTx.ejecutaCaso(id_caso, id_caso_oper, u.getNombre());
        ITree tree = casoTx.getArbolCaso(c);
        session.setAttribute(ATT_TREE, tree);
        return c;
    }

    private void deleteCaso(HttpServletRequest req) throws GestionException, ServletException, IOException {
        HttpSession session = req.getSession();
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(ATT_USER);
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Map m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        if ((c.getIdTC() != 16 || c.getIdTC() != 22) && (StringUtils.trimToEmpty(c.getCasoOperacion(0).getOperacion().getDescripcion()).contains("CONSULTA") || "true".equals(c.getCasoDato("APLICADO_CONT") == null ? "" : c.getCasoDato("APLICADO_CONT").getValor()))) {
            log.error("El documento no puede ser descartado pues ya fue aplicado contablemente.");
            throw new GestionException("El documento no puede ser descartado pues ya fue aplicado contablemente.");
        } else {
            cbl.avanzaCaso(c, usuario.getLogin(), "Caso cancelado por el usuario", new String[] { "TERMINAR" }, new String[] { "TERMINAR" }, m, prefixPath);
        }
    }

    private void liberaCaso(HttpServletRequest req) throws GestionException, ServletException, IOException {
        HttpSession session = req.getSession();
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(ATT_USER);
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Map m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        // Se agreaga está validacion para el módulo de adquisiciones y
        // servicios porque se avanza caso en otro método
        // if(c.getIdTC()!= GestionInterface.IDTC_PRECOMPROMISO &&
        // u.getNombre().equals(c.getCasoOperacion(0).getResponsable())){
        if (!u.getPropiedades().containsKey("ADMIN_RECMAT") && u.getNombre().equals(c.getCasoOperacion(0).getResponsable())) {
            // solo para Anteproyecto se agrega UN al
            String UN = "";
            // responsable siguiente
            if (c.getIdTC() == 16 && (c.getCasoOperacion(0).getIdOperacion() == 2 || c.getCasoOperacion(0).getIdOperacion() == 5))
                // se
                UN = "_" + c.getCasoDato("EJERCICIO_FISCAL").getValor();
            // uso
            // el
            // caso
            // dato
            // de
            // ejercicio
            // fiscal
            // para
            // guardar
            // la
            // UN
            // (solo
            // anteproyecto)
            if (c.getIdTC() == 13) {
                try {
                    cbl.actualizaResponsable(c.getIdCaso(), c.getIdTC());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (c.getIdTC() == 40) {
                int id_oper = c.getCasoOperacion(0).getIdOperacion();
                String grupo = "";
                if (id_oper == 1) {
                    if (usuario.getGrupo("CAP_ANTPROYECTO_UN_" + usuario.getU_UR()) != null) {
                        grupo = "CAP_ANTPROYECTO_UN_" + usuario.getU_UR();
                    }
                    if (usuario.getGrupo("CAP_ANTPROYECTO_OC_" + usuario.getU_UR()) != null) {
                        grupo = "CAP_ANTPROYECTO_OC_" + usuario.getU_UR();
                    }
                    if (usuario.getGrupo("CAP_ANTPROYECTO") != null) {
                        grupo = "CAP_ANTPROYECTO";
                    }
                }
                if (id_oper == 2) {
                    if (usuario.getGrupo("INT_ANTPROYECTO_UN_" + usuario.getU_UR()) != null) {
                        grupo = "INT_ANTPROYECTO_UN_" + usuario.getU_UR();
                    }
                    if (usuario.getGrupo("INT_ANTPROYECTO_OC_" + usuario.getU_UR()) != null) {
                        grupo = "INT_ANTPROYECTO_OC_" + usuario.getU_UR();
                    }
                    if (usuario.getGrupo("INT_ANTPROYECTO_DL_" + usuario.getU_UR()) != null) {
                        grupo = "INT_ANTPROYECTO_DL_" + usuario.getU_UR();
                    }
                }
                if (id_oper == 3) {
                    if (usuario.getGrupo("INTEGRADOR_ANTPROYECTO_UN_" + usuario.getU_UR()) != null) {
                        grupo = "INTEGRADOR_ANTPROYECTO_UN_" + usuario.getU_UR();
                    }
                    if (usuario.getGrupo("INTEGRADOR_ANTPROYECTO_OC_" + usuario.getU_UR()) != null) {
                        grupo = "INTEGRADOR_ANTPROYECTO_OC_" + usuario.getU_UR();
                    }
                    if (usuario.getGrupo("INTEGRADOR_ANTPROYECTO_DL_" + usuario.getU_UR()) != null) {
                        grupo = "INTEGRADOR_ANTPROYECTO_DL_" + usuario.getU_UR();
                    }
                }
                if (id_oper == 4) {
                    if (usuario.getGrupo("INTEGRADOR_ANTPROYECTO_UN_" + usuario.getU_UR()) != null) {
                        grupo = "INTEGRADOR_ANTPROYECTO_UN_" + usuario.getU_UR();
                    }
                }
                if (id_oper == 5) {
                    grupo = "INT_ANTPROYECTO_GRF";
                }
                if (id_oper == 6) {
                    grupo = "INTEGRADOR_ANTPROYECTO_GRF";
                }
                c.getCasoOperacion(0).setResponsable(grupo);
                cbl.actualizaCoResponsable(c.getIdCaso(), c.getIdTC(), c.getCasoOperacion(0).getResponsable());
            } else {
                String[] resp_sig = new String[] { c.getCasoOperacion(0).getOperacion().getResponsable() + UN };
                String[] oper_sig = new String[] { c.getCasoOperacion(0).getOperacion().getNombre() };
                cbl.liberaCaso(c, usuario.getLogin(), "Caso liberado por el usuario", resp_sig, oper_sig, m, prefixPath);
            }
        }
    }

    private void avanzaCaso(HttpServletRequest req, HttpServletResponse response) throws GestionException, ServletException, IOException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            throw new GestionException("Llamada inválida, sin Caso seleccionado");
        }
        if (c.getIdCaso() <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (c.getCasoOperacion(0).getIdOperacion() <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        String strResps = req.getParameter(PRM_RESP);
        if (strResps == null) {
            log.error("Llamada invalida, no hay Responsable(s) a enviar");
            throw new GestionException("Llamada inválida, no hay Responsable(s) a enviar");
        }
        String strOper = req.getParameter(PRM_OPER);
        if (strOper == null) {
            log.error("Llamada invalida, no hay Operacion(es) a ejecutar");
            throw new GestionException("Llamada inválida, no hay Operación(es) a ejecutar");
        }
        /*
		 * Ethiel, se concatenan las observaciones de todos los pasos indicando
		 * el nombre de quien hace la observ.
		 */
        final String strObserv = (!"".equals(c.getCasoOperacion(0).getObservacion()) && c.getCasoOperacion(0).getObservacion() != null ? c.getCasoOperacion(0).getObservacion() : "") + (req.getParameter(PRM_OBSERV) != null && !"".equals(req.getParameter(PRM_OBSERV)) ? "<b>" + c.getCasoOperacion(0).getResponsable() + "</b>: " + req.getParameter(PRM_OBSERV) : "");
        final String[] resp = strResps.split(";");
        final String[] oper = strOper.split(";");
        Map m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        casoTx.avanzaCaso(c, u.getLogin(), strObserv, resp, oper, m, prefixPath);
    }

    private boolean enviaMensaje(HttpServletRequest req) throws GestionException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        String para = req.getParameter("para");
        String asunto = req.getParameter("asunto");
        String body = req.getParameter("body");
        if (para == null)
            throw new GestionException("No se indico destinatario(s) del mensaje");
        if (asunto == null)
            throw new GestionException("No se indico el asunto del mensaje");
        if (body == null)
            throw new GestionException("No hay ning?n contenido en el mensaje");
        MensajesBusinessLogic mbl = new MensajesBusinessLogic(jniName);
        String[] tmp = para.split(";");
        ArrayList to = new ArrayList();
        for (int i = 0; i < tmp.length; i++) {
            if ((tmp[i] == null) && (tmp[i].length() == 0))
                continue;
            to.add(tmp[i].trim());
        }
        String[] from = (String[]) to.toArray(new String[to.size()]);
        return mbl.EnviaMensaje(u.getLogin(), from, asunto, body);
    }

    private void actualizaMensajes(HttpServletRequest req, int msg_status) throws GestionException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        MensajesBusinessLogic mbl = new MensajesBusinessLogic(jniName);
        List msg = mbl.getMensajesEnviados(u.getLogin(), msg_status);
        session.setAttribute(ATT_MSG, msg);
    }

    private boolean cambiaPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException, GestionException, ServletException {
        String[] sendText = new String[5];
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        String oldpwd = req.getParameter("oldpwd");
        String newpwd = req.getParameter("newpwd");
        String verpwd = req.getParameter("verpwd");
        sendText[0] = oldpwd;
        sendText[1] = newpwd;
        sendText[2] = verpwd;
        if ((oldpwd == null) || "".equals(oldpwd)) {
            sendText[3] = "document.getElementById(\"oldpwd\").focus();";
            sendText[4] = "Debe capturar su contraseña actual";
            session.setAttribute(ATT_MSG, sendText);
            return false;
        }
        if ((newpwd == null) || "".equals(newpwd)) {
            sendText[3] = "document.getElementById(\"newpwd\").focus();";
            sendText[4] = "Debe capturar la contraseña nueva";
            session.setAttribute(ATT_MSG, sendText);
            return false;
        }
        if ((verpwd == null) || "".equals(verpwd)) {
            sendText[3] = "document.getElementById(\"verpwd\").focus();";
            sendText[4] = "Debe capturar la confirmación de la contraseña nueva";
            session.setAttribute(ATT_MSG, sendText);
            return false;
        }
        if (!newpwd.equals(verpwd)) {
            sendText[3] = "document.getElementById(\"verpwd\").value=\"\";" + "var a = document.getElementById(\"newpwd\");a.value=\"\";a.focus();";
            sendText[4] = "La nueva contraseña y su confirmación no son iguales";
            session.setAttribute(ATT_MSG, sendText);
            return false;
        }
        UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(jniName);
        // Object[] chngPwdData = ubl.cambiaPassword(u.getLogin(), oldpwd,
        // newpwd);
        Object[] chngPwdData = ubl.changePasswordNBases(u.getLogin(), oldpwd, newpwd);
        if (chngPwdData[ubl.USR_OBJ] == null) {
            sendText[3] = "document.getElementById(\"verpwd\").value=\"\";" + "document.getElementById(\"newpwd\").value=\"\";" + "var a = document.getElementById(\"oldpwd\");a.value=\"\";a.focus();";
            sendText[4] = (String) chngPwdData[ubl.MSG_TEXT];
            session.setAttribute(ATT_MSG, sendText);
            return false;
        }
        session.setAttribute(ATT_USER, u);
        sendText[3] = null;
        session.setAttribute(ATT_MSG, chngPwdData[ubl.MSG_TEXT]);
        return true;
    }

    private void getListaUsersGroups(HttpServletResponse resp) throws GestionException, IOException {
        UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(jniName);
        Document respDoc = new Document(ubl.toParaXML());
        resp.setContentType("text/xml");
        new XMLOutputter().output(respDoc, resp.getOutputStream());
    }

    private void readMessage(HttpServletRequest req, HttpServletResponse resp) throws GestionException, IOException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        MensajesBusinessLogic mbl = new MensajesBusinessLogic(jniName);
        String idMsg = req.getParameter("id_msg");
        if (idMsg == null)
            throw new GestionException("No se indico id de mensaje a leer");
        String msgStatus = req.getParameter("msg_status");
        if (msgStatus == null)
            throw new GestionException("No se indico estatus de mensaje a leer");
        boolean update = "true".equalsIgnoreCase(req.getParameter("update"));
        int id_msg = Integer.parseInt(idMsg);
        int id_status = Integer.parseInt(msgStatus);
        Mensaje msg = mbl.getMensaje(id_msg, u.getLogin(), id_status);
        if (update)
            mbl.updateMessage(msg);
        String body = msg.getBody();
        if (body == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Mensaje no encontrado");
            return;
        }
        PrintWriter out = resp.getWriter();
        out.println(body);
        out.flush();
        out.close();
    }

    private void deleteMessage(HttpServletRequest req, HttpServletResponse resp) throws GestionException, IOException {
        HttpSession session = req.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        MensajesBusinessLogic mbl = new MensajesBusinessLogic(jniName);
        String idMsg = req.getParameter("id_msg");
        if (idMsg == null)
            throw new GestionException("No se indico id de mensaje a leer");
        String msgStatus = req.getParameter("msg_status");
        if (msgStatus == null)
            throw new GestionException("No se indico estatus de mensaje a leer");
        String msgType = req.getParameter("msg_type");
        if (msgType == null)
            throw new GestionException("No se indico tipo de mensaje");
        int id_msg = Integer.parseInt(idMsg);
        int id_status = Integer.parseInt(msgStatus);
        mbl.deleteMensaje(id_msg, u.getLogin(), id_status);
        if ("Enviados".equalsIgnoreCase(msgType)) {
            actualizaMensajes(req, MensajeManager.MSG_ENVIADO);
            resp.sendRedirect("../caso/messages.jsp?" + PRM_MSG_TYPE + "=" + CMD_MSG_SENDED);
        } else if ("Recibidos".equalsIgnoreCase(msgType)) {
            actualizaMensajes(req, MensajeManager.MSG_SIN_LEER);
            resp.sendRedirect("../caso/messages.jsp?" + PRM_MSG_TYPE + "=" + CMD_MSG_SENDED);
        } else if ("Leidos".equalsIgnoreCase(msgType)) {
            actualizaMensajes(req, MensajeManager.MSG_LEIDO);
            resp.sendRedirect("../caso/messages.jsp?" + PRM_MSG_TYPE + "=" + CMD_MSG_SENDED);
        }
    }

    private void getListaUsersGroupsOper(HttpServletRequest req, HttpServletResponse resp) throws GestionException, IOException {
        HttpSession session = req.getSession();
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(jniName);
        Document respDoc = new Document(ubl.toUserGroupOperXML(c.getIdTC()));
        if (xmlGenerator != null) {
            XmlGeneratorInterface xml = null;
            try {
                ClassLoader cl = getClass().getClassLoader();
                Class clase = cl.loadClass(xmlGenerator);
                xml = (XmlGeneratorInterface) clase.newInstance();
            } catch (ClassNotFoundException exc) {
                log.error("Generador XML", exc);
                throw new GestionException(exc);
            } catch (InstantiationException exc) {
                log.error("Generador XML", exc);
                throw new GestionException(exc);
            } catch (IllegalAccessException exc) {
                log.error("Generador XML", exc);
                throw new GestionException(exc);
            }
            ubl.modifyXML(xml, respDoc.getRootElement());
        }
        resp.setContentType("text/xml");
        new XMLOutputter().output(respDoc, resp.getOutputStream());
    }

    private void genericInterface(HttpServletRequest req, HttpServletResponse resp) throws GestionException, IOException {
        GenericInterface gi = null;
        String name = req.getParameter("class");
        String action = req.getParameter("action");
        if (name == null)
            throw new GestionException("Sin clase a ejecutar");
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(name);
            gi = (GenericInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Interfaz Generica " + name, exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("Interfaz Generica " + name, exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Interfaz Generica " + name, exc);
            throw new GestionException(exc);
        }
        gi.execute(req, resp, jniName, action);
    }

    private URLDocumento getDocumentoPorNombre(HttpServletRequest req) throws GestionException {
        HttpSession session = req.getSession();
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        String doc_nombre = req.getParameter("docName");
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        if ((doc_nombre == null) || ("".equals(doc_nombre)))
            throw new GestionException("El nombre del documento es nulo o vacio");
        URLDocumento urlDoc = cbl.getIdNodeDocumento(c, doc_nombre);
        return urlDoc;
    }

    private void actualizaInboxFiltros(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        CasoOperacionBusinessLogic casoOperTx = new CasoOperacionBusinessLogic(jniName);
        String u_login = u.getLogin();
        String UR = u.getU_UR();
        String cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
        if (u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES")) {
            if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) {
                u_login = "INTEGRADOR_ADECUACIONES";
            }
        }
        List<CasoOperacion> inbox = casoOperTx.getCasoOperacionPorUsuarioFiltros(u_login, " ORDER BY o.o_descripcion DESC, co.co_fecha_ini ASC", UR, cCentroContable, request);
        session.setAttribute(ATT_INBOX, inbox);
        String valorFiltro = request.getParameter("valorFiltro");
        String fltr = request.getParameter(PRM_PROM_FILTER);
        fltr = (fltr == null) ? new String() : "?" + PRM_PROM_FILTER + "=" + fltr;
        String refresh = request.getParameter("refresh");
        if (refresh == null)
            refresh = (String) session.getAttribute("refresh");
        refresh = (refresh == null) ? "30" : refresh;
        session.setAttribute("refresh", refresh);
        String strRedirect = "../caso/inbox.jsp?" + returnRequestString(request);
        log.debug("Redirect: " + strRedirect);
        response.sendRedirect(strRedirect);
    }

    private String returnRequestString(HttpServletRequest request) throws Exception {
        String reqString = "";
        String token = "";
        for (Enumeration<?> i = request.getParameterNames(); i.hasMoreElements(); ) {
            String paramName = (String) i.nextElement();
            String val = request.getParameter(paramName);
            reqString += token + paramName + "=" + (val == null ? "" : URLEncoder.encode(new String(val.getBytes("ISO-8859-1"), "UTF-8")));
            token = "&";
        }
        return reqString;
    }

    public Caso iniciaCasoContable(String tCaso, Usuario u, String cCentroContable) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        String centroContableBackup = u.getPropiedad("CCENTROCONTABLE").getValor();
        u.getPropiedad("CCENTROCONTABLE").setValor(cCentroContable);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass("com.syc.gestion.custom.DefaultFolioGenerator");
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        }
        c = casoTx.IniciaCaso(u, idTC, fg);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        u.getPropiedad("CCENTROCONTABLE").setValor(centroContableBackup);
        log.info("Inicia Caso Contable con: " + u.getNombre_equipo_login());
        c.setC_nom_equipoIni(u.getNombre_equipo_login());
        c.setC_nom_equipoUser(u.getIp_equipo_login());
        return c;
    }
}
