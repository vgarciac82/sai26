package com.syc.reportes.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConciliaAutomaticaFFMServlet", urlPatterns = { "/servlet/ConciliaAutomaticaFFMServlet" })
public class ConciliaBancosAutFFMServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConciliaBancosAutFFMServlet.class);

    private Connection conn = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = null;
        try {
            session = request.getSession(false);
            if (session == null) {
                log.info("no hay sessión");
                return;
            }
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null) {
                log.info("no hay sessión");
                return;
            }
            ConciliaAut(request, response, session);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            session.setAttribute(GestionInterface.ATT_MSG, e.toString());
            try {
                response.sendRedirect("../Generador/ConciliaBancosFFM.jsp?error=SI");
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
    }

    private synchronized void ConciliaAut(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        String[] param = request.getParameter("Param").toString().split(",");
        if (param.length < 3) {
            throw new Exception("Faltan datos para la consulta");
        }
        String strCuenta = param[0];
        Integer nMes = Integer.parseInt(param[1]);
        Integer idConciliacion = Integer.parseInt(param[2]);
        String query = "";
        CallableStatement cs = null;
        ResultSet rs = null;
        conn = DataSourceManager.getConnection(jndiName);
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        PrintWriter out = response.getWriter();
        try {
            query = "{call sp_j_ConciliaAutoFFM( ?, ?, ?)}";
            cs = conn.prepareCall(query);
            cs.setInt(1, idConciliacion);
            cs.setString(2, strCuenta);
            cs.setInt(3, nMes);
            rs = cs.executeQuery();
            if (rs.next()) {
                System.out.println("Se proceso la conciliacion automatica.");
            } else {
                throw new Exception("No se proceso la conciliacion automatica. Notifique al administrador");
            }
            conn.commit();
            arrayObj.put(0, strCuenta);
            arrayObj.put(1, nMes.toString());
            arrayObj.put(2, idConciliacion.toString());
            jsonObj.put("DATA", arrayObj);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            log.error("Error: " + e);
            jsonObj.put("ERROR", new String(e.getMessage().getBytes("UTF-8"), "ISO-8859-1"));
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    log.warn("Error en rollback " + e1);
                }
            throw e;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(cs);
            CloseObject.closeObject(conn);
            out.write(jsonObj.toString());
            out.flush();
            out.close();
            out = null;
            System.out.println("Cerramos Objetos conciliacion automatica...");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
