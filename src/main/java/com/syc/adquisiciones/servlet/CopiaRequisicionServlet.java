package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.dsmngr.DataSourceManager;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "CopiaRequisicionServlet", urlPatterns = { "/servlet/CopiaRequisicionServlet" })
public class CopiaRequisicionServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    private static final long serialVersionUID = 1L;

    private String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(CopiaRequisicionServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String strParam = request.getParameter("Param");
        String[] param = strParam.split(",");
        int cmd = Integer.parseInt(param[5].toString().trim());
        Connection conn = null;
        CallableStatement cmst = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (cmd == 1) {
            try {
                conn = DataSourceManager.getConnection(jndiName);
                log.info("Se obtuvo la conexion");
                cmst = conn.prepareCall("{call pa_mCopiaRequisicion (?,?,?,?,?,?)}");
                cmst.setString(1, param[0].trim());
                cmst.setString(2, param[1].trim());
                cmst.setInt(3, Integer.parseInt(param[2].toString().trim()));
                cmst.setString(4, param[3].trim());
                cmst.setString(5, param[4].trim());
                cmst.registerOutParameter(6, Types.INTEGER);
                cmst.execute();
                log.info("Se ejecuto el procedimiento almacenado");
                int outputValue = cmst.getInt(6);
                log.info("Object: {}", "Se obtiene resultado..." + outputValue);
                try {
                    if (outputValue == 1) {
                        jsonObj.put("Col1", "1");
                    }
                    if (outputValue == 2) {
                        jsonObj.put("Col1", "2");
                        conn.rollback();
                    }
                    if (outputValue == 3) {
                        jsonObj.put("Col1", "3");
                        conn.rollback();
                    }
                    if (outputValue == 4) {
                        jsonObj.put("Col1", "4");
                        conn.rollback();
                    }
                    if (outputValue == 5) {
                        jsonObj.put("Col1", "5");
                        conn.rollback();
                    }
                    if (outputValue == 6) {
                        jsonObj.put("Col1", "6");
                        conn.rollback();
                    }
                    if (outputValue == 7) {
                        jsonObj.put("Col1", "7");
                        conn.rollback();
                    }
                    if (outputValue == 8) {
                        jsonObj.put("Col1", "8");
                        conn.rollback();
                    }
                    if (outputValue == 0) {
                        jsonObj.put("Col1", "0");
                        conn.commit();
                    }
                    String destino = arrayObj.put(jsonObj).toString();
                    out.println(destino);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException exc) {
                    log.warn("Cerrando conexion a base de datos", exc);
                }
                if (cmst != null)
                    try {
                        cmst.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
            }
        }
        cmst = null;
        conn = null;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
