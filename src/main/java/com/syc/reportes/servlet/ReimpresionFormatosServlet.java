package com.syc.reportes.servlet;

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
import org.apache.log4j.Logger;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReimpresionFormatosBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReimpresionFormatosServlet", urlPatterns = { "/reportes/ReImpresionFormatos" })
public class ReimpresionFormatosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = Logger.getLogger(ReimpresionFormatosServlet.class);

    private static String jniName = "jdbc/gestion";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String formato = req.getParameter("TIPO_FORMATO");
        String tipoFormato = req.getParameter("fiel");
        String tipo = req.getParameter("formato");
        String ruta = getServletContext().getRealPath("Reportes");
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        ReimpresionFormatosBusinessLogic rpb = new ReimpresionFormatosBusinessLogic(jniName);
        try {
            if (tipoFormato.equals("SI")) {
                formato = formato + "_FIEL.jasper";
            } else {
                formato = formato + ".jasper";
            }
            String reportPath = getServletContext().getRealPath("Reportes" + File.separator + formato);
            rpb.reimpresionPagos(req, resp, reportPath, ruta, tipo);
        } catch (Exception e) {
            log.error(e, e);
            throw new ServletException(e);
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
