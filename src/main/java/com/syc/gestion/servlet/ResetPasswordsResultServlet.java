package com.syc.gestion.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import com.syc.adquisiciones.vo.ConexionesBD;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.documental.CatalogosManager;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ********************************************************************************************
 */
@WebServlet(name = "ResetPasswordsResultServlet", urlPatterns = { "/gstnmngr/ContraReiniciada" })
public class ResetPasswordsResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private DataSource ds = null;

    private String jniName = null;

    private static Logger log = LoggerFactory.getLogger(GestionServlet.class);

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
        Context initContext;
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        Connection conn = null;
        String uLogin = request.getParameter("login");
        String errorMsg = null;
        String resultMsg = null;
        List<ConexionesBD> conexiones = null;
        ConexionesBD cbd = null;
        if ((uLogin == null) || ("".equals(uLogin))) {
            errorMsg = "No especificó un nombre de usuario";
        }
        String s = "" + new java.util.Date().getTime();
        String pass = s.substring(s.length() - 6) + "_" + uLogin;
        Usuario u = null;
        if (errorMsg == null) {
            errorMsg = "";
            try {
                conn = ds.getConnection();
                String queryBase = "SELECT * FROM TEJERCICIOFISCAL";
                conexiones = CatalogosManager.getBasesDeDatos(conn, queryBase);
            } catch (SQLException exc) {
                exc.printStackTrace();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
            Iterator<ConexionesBD> ite = conexiones.iterator();
            String passwordTemp = pass;
            while (ite.hasNext()) {
                cbd = ite.next();
                try {
                    conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
                    u = new Usuario();
                    u.setLogin(uLogin);
                    u = UsuarioManager.select(conn, u);
                    if (u != null) {
                        /**
                         * ************************************************************************************************
                         */
                        pass = passwordTemp;
                        try {
                            pass = convertCMD5(pass);
                            System.out.println("El password encriptado es:" + pass);
                        } catch (Exception e) {
                            throw new Exception("Error en encriptacion de datos :" + e.getMessage());
                        }
                        /**
                         * ************************************************************************************************
                         */
                        u.setPassword(pass);
                        int i = UsuarioManager.update(conn, u);
                        if (i <= 0) {
                            errorMsg += "No se pudo asignar el \"token\" al usuario.\n";
                        } else {
                            //resultMsg = "Usuario: "+uLogin +" Nuevo password:"+ u.getPassword() + "<br/>Favor de comunicarlo y pedirle que lo cambie.";
                            resultMsg = "Usuario: " + uLogin + " Nuevo password:" + passwordTemp + "<br/>Favor de comunicarlo y pedirle que lo cambie.";
                            conn.commit();
                        }
                    } else {
                        if (cbd.getEjercicioFiscal().trim().length() > 3)
                            errorMsg += "El usuario proporcionado no existe en la BD del Ejercicio Fiscal: " + cbd.getEjercicioFiscal() + "\n";
                        else
                            errorMsg += "El usuario proporcionado no existe en la BD del Ejercicio Fiscal: 2" + cbd.getEjercicioFiscal().trim() + "-PRUEBAS\n";
                    }
                } catch (SQLException exc) {
                    exc.printStackTrace(System.out);
                    throw new ServletException(exc.getMessage());
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                    throw new ServletException(exc.getMessage());
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (Exception exc) {
                    }
                }
            }
        }
        response.sendRedirect("ReinciarContra?Msg=" + resultMsg + "&Err=" + errorMsg);
    }

    private Connection getConectionCatalogo(String server, String port, String bd, String user, String pass) {
        Connection conn = null;
        String url = null;
        try {
            url = "jdbc:jtds:sqlserver://" + server + ":" + port + "/" + bd;
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * ********************************************************************************************
     */
    public String convertCMD5(String pass) throws Exception {
        String pwd = pass;
        String pwdCMD5 = "";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(pwd.getBytes());
            byte[] hash = digest.digest();
            pwdCMD5 = convertToHex(hash);
            System.out.println(convertToHex(hash));
            System.out.println(digest.toString());
        } catch (Exception e) {
            System.out.println("Error al encriptar credenciales..." + e.getMessage());
        }
        return pwdCMD5;
    }

    /**
     * ********************************************************************************************
     */
    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
