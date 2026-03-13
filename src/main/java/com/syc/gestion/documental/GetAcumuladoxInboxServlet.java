package com.syc.gestion.documental;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.AcumuladoInbox;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "GetAcumuladoxInboxServlet", urlPatterns = {})
public class GetAcumuladoxInboxServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());

    private String jniName = null;

    public void init() {
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = GestionInterface.ATT_CONEXION;
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            res.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            res.sendRedirect("../index.jsp");
            return;
        }
        log.info("[GetAcumuladoxInboxServlet]");
        try {
            AcumuladoInbox inbox = null;
            CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
            inbox = cobl.getTotalCasoOperacionPorUsuarioBandeja(u.getLogin(), "N");
            StringBuffer sb = new StringBuffer();
            res.setContentType("text/xml");
            res.setHeader("Cache-Control", "no-cache");
            sb.append("<acumulado>");
            sb.append("<entrada>" + inbox.getTotalEntrada() + "</entrada>");
            sb.append("<porenviar>" + inbox.getTotalPorEnviar() + "</porenviar>");
            sb.append("<turnados>" + inbox.getTotalTurnados() + "</turnados>");
            sb.append("<respuestas>" + inbox.getTotalRespuestas() + "</respuestas>");
            sb.append("<prorrogas>" + inbox.getTotalProrrogas() + "</prorrogas>");
            sb.append("</acumulado>");
            res.getWriter().write(sb.toString());
        } catch (GestionException e) {
            log.warn("Error al consultar acumulados, usuario [ " + u.getLogin() + " ] ", e);
        }
    }
}
