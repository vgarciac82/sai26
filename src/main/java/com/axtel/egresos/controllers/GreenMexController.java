package com.axtel.egresos.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.axtel.egresos.entities.GreenMex;
import com.axtel.egresos.services.impl.JDBCGreenMexService;
import com.axtel.egresos.repositories.impl.JDBCGreenMexRepository;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GreenMexController", urlPatterns = { "/GREENMEX/greenmex" })
public class GreenMexController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(GreenMexController.class);

    private String jniName;

    private JDBCGreenMexService GreenMexService;

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
        GreenMexService = new JDBCGreenMexService(jniName, new JDBCGreenMexRepository());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Inicia registro de comprobacion de ingreso.");
        GreenMex GreenMex = null;
        try {
            HttpSession session = request.getSession(false);
            if (session == null)
                response.sendRedirect("../../index.jsp");
            Usuario user = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (user == null)
                response.sendRedirect("../../index.jsp");
            int folioComprobacion = Integer.parseInt(request.getParameter("folioRG"));
            BigDecimal impEjercer = new BigDecimal(request.getParameter("impEjercer"));
            String folioING = (request.getParameter("folioING") != null) ? request.getParameter("folioING").trim() : "";
            String remanenteING = (request.getParameter("remanenteING") != null) ? request.getParameter("remanenteING").trim() : "";
            String fecha = request.getParameter("fecha");
            boolean retorno = false;
            retorno = GreenMexService.insertaComprobacion(folioComprobacion, impEjercer, folioING, remanenteING, fecha);
            log.info("Object: {}", String.valueOf(GreenMex));
            Util.sendJSON(response, GreenMex);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            Util.sendJSONError(response, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
