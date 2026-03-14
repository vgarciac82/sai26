package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.PresupuestoBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.servlet.GestionSignFileReceiverServlet;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "CargaPresupuestoServlet", urlPatterns = { "/gstnmngr/cargaPresupuesto" })
public class CargaPresupuestoServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 1L;

    private String jniName = null;

    private static Logger log = LoggerFactory.getLogger(GestionSignFileReceiverServlet.class);

    public CargaPresupuestoServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        //inicio sesion
        if (session == null) {
            log.warn("No hay sesión");
            response.sendRedirect("../index.jsp");
            return;
        }
        //Se obtiene el caso para subir el archivo al expediente
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            log.warn("No hay Caso en la sesión");
            throw new ServletException("No hay Caso en la sesión");
        }
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            log.warn("No hay Usuario en la sesión");
            throw new ServletException("No hay Usuario en la sesión");
        }
        //TODO JESUS aqui hay que llamar una calse que reciba el nodo y haga las validaciones y regrese el resultado
        PresupuestoBusinessLogic presu = new PresupuestoBusinessLogic(jniName);
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        try {
            int carpeta = new Integer(request.getParameter("carpeta")).intValue();
            String ejercicio_fiscal = c.getCasoDato("EJERCICIO_FISCAL").getValor();
            ArrayList<String> arrResultado = null;
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            String u_logion = usuario.getLogin();
            arrResultado = presu.validaArchivoExcel(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpeta, 1)[0].getAbsolutePath(), ejercicio_fiscal, c, usuario, m, prefixPath, u_logion);
            response.sendRedirect("../plantillasCasos/cargaPresupuestal.jsp?msg=" + arrResultado);
        } catch (Exception ex) {
            response.sendRedirect("../plantillasCasos/cargaPresupuestal.jsp?msg=" + ex.getMessage());
        }
    }

    public void init() throws ServletException {
        super.init();
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
