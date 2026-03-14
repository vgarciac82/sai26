package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.solicitudviaticos.SolicitudViaticosBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "RegistroViaticos", urlPatterns = { "/viaticos/CreaViatico", "/viaticos/CreaViaticoTransporte", "/viaticos/CreaViaticoAgenda", "/viaticos/CreaFirmantes", "/viaticos/IngresoAutorizacion", "/viaticos/RegistraAutorizacion" })
public class RegistroViaticosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 7293212240749888023L;

    private static final Logger log = LoggerFactory.getLogger(RegistroViaticosServlet.class);

    private String jniName;

    public static String SUBREPORT_DIR = "";

    public static String REPORT_DIR = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        try {
            SolicitudViaticosBusinessLogic svbl = new SolicitudViaticosBusinessLogic(jniName);
            if ("IngresoAutorizacion".equals(accion)) {
                svbl.direccionaSolAutorizacion(req, resp);
            } else if ("RegistraAutorizacion".equals(accion)) {
                svbl.registraAutorizacion(req, resp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                HttpSession session = req.getSession(false);
                if (session == null)
                    session = req.getSession(true);
                session.setAttribute("tipoRespuesta", "3");
                session.setAttribute("mensaje", "No se pudo completar la operacion debido al siguiente error:\n" + e + "\n Notifique al administrador del sistema.");
                resp.sendRedirect("../Generador/RespuestaViaticos.jsp");
            } catch (Exception e3) {
                log.warn("Object: {}", "Problemas enviando respuesta " + e3);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new Exception("No cuenta con sesion iniciada. Ingrese nuevamente al sistema");
            Usuario usuario = (Usuario) session.getAttribute(ATT_USER);
            if (usuario == null)
                throw new Exception("No cuenta con sesion iniciada. Ingrese nuevamente al sistema");
            String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            SolicitudViaticosBusinessLogic svbl = new SolicitudViaticosBusinessLogic(jniName);
            if ("CreaViaticoTransporte".equals(accion)) {
                svbl.creaViaticoTransporte(req);
                ResponseSender.sendClientSimpleMessage(resp, true, "Registro insertado exitosamente");
            } else if ("CreaViatico".equals(accion)) {
                svbl.creaViatico(req);
                ResponseSender.sendClientSimpleMessage(resp, true, "Registro insertado exitosamente");
            } else if ("CreaViaticoAgenda".equals(accion)) {
                svbl.creaViaticoAgenda(req);
                ResponseSender.sendClientSimpleMessage(resp, true, "Registro insertado exitosamente");
            } else if ("CreaFirmantes".equals(accion)) {
                svbl.creaFirmantes(req);
                ResponseSender.sendClientSimpleMessage(resp, true, "Registro insertado exitosamente");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            SUBREPORT_DIR = getServletContext().getRealPath("Reportes");
            REPORT_DIR = getServletContext().getRealPath("Reportes" + File.separator);
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
