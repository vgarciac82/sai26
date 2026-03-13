package com.syc.gestion.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import com.syc.gestion.core.AlarmaManager;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "PruebaMensajesServlet", urlPatterns = { "/test/mensajes" })
public class PruebaMensajesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PruebaMensajesServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jniName = "jdbc/gestion";
        DataSource ds = null;
        Connection conn = null;
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(jniName);
        } catch (NamingException ne) {
            try {
                Context initContext = new InitialContext();
                ds = (DataSource) initContext.lookup(jniName);
            } catch (NamingException exc) {
                ne.printStackTrace();
                exc.printStackTrace();
                throw new RuntimeException("No se encontro la fuente '" + jniName + "'");
            }
        }
        try {
            conn = ds.getConnection();
            String cmd = "mail={to:[\"jefe_analistas\",\"JEFE_ANALISTAS\"],subject:\"Caso en Mesa de Recepción Atrasado\",body:\"revisa_doctos.bdy\"}";
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies") + File.separator;
            //			AlarmaManager.procesaAlarma(conn, prefixPath, cmd);
        } catch (SQLException exc) {
            log.error("Probando Mensajes", exc);
            throw new ServletException(exc);
        }
    }
}
