package com.syc.cfdi.servlet;

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
import com.syc.sai.interfaces.CFDIBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "NotificaREPFaltantes", urlPatterns = { "/notificaREPFaltante" })
public class NotificaREPServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 715260361207686246L;

    private static final Logger log = LoggerFactory.getLogger(NotificaREPServlet.class);

    private String jniName;

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        String mensaje = "Notificacion enviada exitosamente.";
        try {
            String unidadEjecutora = req.getParameter("cUnidadEjecutoraSel");
            String RFCBeneficiario = req.getParameter("cRFC");
            CFDIBusinessLogic cfdibl = new CFDIBusinessLogic(jniName);
            cfdibl.notificaREPFaltantes(u, unidadEjecutora, RFCBeneficiario);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            mensaje = "Ocurrio el siguiente error mientras se enviaban las notificaciones: " + e;
        }
        session.setAttribute("MSG", mensaje);
        resp.sendRedirect("Generador/listadoRecibosElectronicos.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
