package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.RecepcionInterfacce;
import com.syc.adquisiciones.core.DatosRecepcion;
import com.syc.adquisiciones.core.RecepcionImpl;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "RecepcionServlet", urlPatterns = { "/servlet/RecepcionServlet" })
public class RecepcionServlet extends HttpServlet {

    private static final long serialVersionUID = 6614973599742330246L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(RecepcionServlet.class);

    private Connection conn = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    /**
     * Constructor of the object.
     */
    public RecepcionServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
            out.println("  <BODY>");
            out.print("    This is ");
            out.print(this.getClass());
            out.println(", using the GET method");
            out.println("  </BODY>");
            out.println("</HTML>");
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String mensaje = "";
        boolean respuesta = false;
        try {
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            conn = DataSourceManager.getConnection(jndiName);
            DatosRecepcion datosRecep = new DatosRecepcion();
            datosRecep.setcIdPedContDef(request.getParameter("cIdContrato"));
            datosRecep.setcIdRecepAnticipo(request.getParameter("cIdSolAnticipo"));
            datosRecep.setcIdRecepcionMat(request.getParameter("cIdRecepMat"));
            datosRecep.setFactorAmortizacion("1".equals(request.getParameter("factAmort")) ? true : false);
            datosRecep.setRecepMat("1".equals(request.getParameter("isRecepMat")) ? true : false);
            RecepcionInterfacce recepInt = new RecepcionImpl();
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            out = response.getWriter();
            int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
            log.debug("Object: {}", "operacion: " + tipoOperacion);
            switch(tipoOperacion) {
                case 0:
                    respuesta = recepInt.crear(conn, usuario, datosRecep);
                    break;
                case 1:
                    mensaje = "Ya se puede hacer el pago";
                    respuesta = recepInt.enviar(conn, usuario, datosRecep);
                    break;
                case 2:
                    respuesta = recepInt.devolver(conn, usuario, datosRecep);
                    break;
                case 3:
                    respuesta = recepInt.cancelar(conn, usuario, datosRecep);
                    break;
                case 4:
                    respuesta = recepInt.modificar(conn, usuario, datosRecep);
                    break;
                default:
                    log.warn("Operación incorrecta");
                    break;
            }
            if (respuesta) {
                conn.commit();
            } else {
                mensaje = "Error. Notifique a soporte técnico SAI.";
                conn.rollback();
            }
        } catch (Exception e) {
            // TODO: handle exception
            try {
                respuesta = false;
                conn.rollback();
                e.printStackTrace();
                mensaje = e.getMessage();
                log.error("Object: {}", e.getMessage());
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                mensaje = e1.getMessage();
                log.error("Object: {}", e1.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("Object: {}", e.getMessage());
            } catch (JSONException ex) {
                ex.printStackTrace();
                log.error("Object: {}", ex.getMessage());
            }
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init(ServletConfig config) throws ServletException {
        // Put your code here
        //Crea la conexión a BD
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
}
