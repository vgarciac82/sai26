package com.syc.gestion.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaInboxServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -2132300865011700319L;

    private static final Logger log = LoggerFactory.getLogger(CargaInboxServlet.class);

    private String jniName = null;

    private Usuario u;

    public CargaInboxServlet() {
        super();
    }

    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        java.util.Date di = new java.util.Date();
        System.out.println("Entrando time: " + di.toString());
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect(request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/sai");
            return;
        }
        if (log.isDebugEnabled())
            log.debug("Actualizando InBox FILTROS");
        u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            response.sendRedirect(request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/sai");
            return;
        }
        try {
            actualizaInboxFiltros(jniName, request, response);
        } catch (Exception exc) {
            log.error("error actualizando inbox con filtros", exc);
            throw new ServletException(exc);
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
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
    }

    private void actualizaInboxFiltros(String jniName, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    }
}
