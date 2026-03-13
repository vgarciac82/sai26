package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import org.apache.log4j.Logger;
import com.syc.gestion.core.Grupo;
import com.syc.gestion.core.GrupoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "GestionReportesListas", urlPatterns = { "/reporteslistas" })
public class GestionReportesListas extends HttpServlet {

    private DataSource ds = null;

    public static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(GestionServlet.class);

    private String jniName = null;

    public void init() {
        Context initContext;
        jniName = "jdbc/gestion";
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(jniName);
        } catch (NamingException ne) {
            try {
                initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:comp/env");
                ds = (DataSource) envContext.lookup(jniName);
            } catch (NamingException nexc) {
                try {
                    initContext = new InitialContext();
                    ds = (DataSource) initContext.lookup(jniName);
                } catch (NamingException exc) {
                    ne.printStackTrace();
                    exc.printStackTrace();
                    throw new RuntimeException("No se encontro la fuente '" + jniName + "'");
                }
            }
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        Connection conn = null;
        try {
            conn = ds.getConnection();
            List lista = null;
            if ("usuarios".equalsIgnoreCase(req.getParameter("tipo")))
                lista = UsuarioManager.selectAll(conn);
            else
                lista = GrupoManager.selectAll(conn);
            for (int i = 0; i < lista.size(); i++) {
                Object obj = lista.get(i);
                if (obj instanceof Usuario) {
                    Usuario u = (Usuario) obj;
                    out.println("<option value=\"" + u.getLogin() + "\">" + u.getNombre() + " (" + u.getLogin() + ")</option>\n");
                } else {
                    Grupo g = (Grupo) obj;
                    out.println("<option value=\"" + g.getNombre() + "\">" + g.getNombre() + "</option>\n");
                }
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
            }
        }
    }
}
