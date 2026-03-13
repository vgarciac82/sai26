package com.syc.egresos.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CalculaMontosServlet", urlPatterns = { "/egresos/CalculaPAGODIVERSO" })
public class CalculaMontosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 16725712711730933L;

    private static final Logger log = Logger.getLogger(CalculaMontosServlet.class);

    private String jniName = "jdbc/gestion";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("Su sesion termino. Ingrese nuevamente al sistema.");
            }
            String action = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            if ("CalculaPAGODIVERSO".equals(action)) {
                Object recepcion = Util.requestToMap(req);
            }
        } catch (Exception e) {
            log.error(e, e);
            ResponseSender.sendError(resp, e.toString());
        }
    }

    @Override
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
}
