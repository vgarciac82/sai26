package com.syc.gestion.documental;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GetFolioCasoServlet", urlPatterns = {})
public class GetFolioCasoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Logger log = LoggerFactory.getLogger(getClass());

    private String jniName = null;

    public void init() throws ServletException {
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
        log.info("[GetFolioCasoServlet]");
        String sid = req.getParameter("id");
        try {
            int id = Integer.parseInt(sid);
            CasoBusinessLogic cb = new CasoBusinessLogic(jniName);
            Caso c = cb.getCaso(id);
            log.info("[GetFolioCasoServlet] folio=" + c.getFolio());
            res.setContentType("text/xml");
            res.setHeader("Cache-Control", "no-cache");
            res.getWriter().write("<folio>" + c.getFolio() + "</folio>");
        } catch (GestionException e) {
            log.warn("Error al leer folio para id=" + sid, e);
        }
    }
}
