package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CambiaPropiedadesUsuario", urlPatterns = { "/servlet/CambiaPropiedadesUsuario" })
public class CambiaPropiedadesUsuario extends HttpServlet {

    /**
     * Humberto Rene Farias Rojas.
     */
    private static Logger log = Logger.getLogger(CambiaPropiedadesUsuario.class);

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private String folioGenerator = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexión a BD
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @SuppressWarnings("null")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("Cambio de propiedades del usuario.");
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            try {
                cambiaCentroContableUE(request, response, u);
            } catch (Exception e) {
                log.error("Error al llamar el metodo cambiCentroContableUE.");
                e.printStackTrace();
            }
        } else {
            log.info("Invalidando sesion");
            session.invalidate();
            response.sendRedirect(request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/sai");
        }
        return;
    }

    public synchronized void cambiaCentroContableUE(String UE, String centroContable, Usuario u) {
        Map<String, UsuarioPropiedades> propiedades = null;
        try {
            propiedades = u.getPropiedades();
            UsuarioPropiedades up = (UsuarioPropiedades) propiedades.get("CCENTROCONTABLE");
            log.info("Las propiedades del usuario cambiarón. UnidadEjecutora=" + UE + " y su centroContable=" + centroContable);
            up.setValor(centroContable);
            u.setU_UR(UE);
            u.setPropiedad("CCENTROCONTABLE", up);
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Error al cambiar las propiedades del usuario");
            e.printStackTrace();
        } finally {
            propiedades = null;
        }
    }

    public synchronized void cambiaCentroContableUE(HttpServletRequest request, HttpServletResponse response, Usuario u) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        String nuevocCentroContable = "";
        String ue = "";
        boolean error = false;
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            Map propiedades = u.getPropiedades();
            UsuarioPropiedades up = (UsuarioPropiedades) propiedades.get("CCENTROCONTABLE");
            String cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
            ue = request.getParameter("UnidadEjecutora");
            if (ue != null) {
                // Se obtiene el centro contable de la unidad ejecutora
                pstmt = conn.prepareStatement("select cCentroContable from tCatalogoURCC  WITH(NOLOCK) where cUnidadResponsable=?");
                pstmt.setString(1, ue);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    nuevocCentroContable = rs.getString("cCentroContable");
                }
            }
            if (!("".equals(nuevocCentroContable))) {
                log.info("Las propiedades del usuario cambiarón. UnidadEjecutora=" + ue + " y su centroContable=" + nuevocCentroContable);
                up.setValor(nuevocCentroContable);
                u.setU_UR(ue);
                u.setPropiedad("CCENTROCONTABLE", up);
            } else {
                error = true;
                nuevocCentroContable = cCentroContable;
                ue = u.getU_UR();
                log.warn("Las propiedades del usuario no cambiarón.");
                conn.rollback();
            }
            conn.commit();
        } catch (Exception e) {
            // TODO: handle exception
            error = true;
            log.error("Error al cambiar las propiedades del usuario.");
            e.printStackTrace();
            conn.rollback();
        } finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
            try {
                jsonObj.put("centroContable", nuevocCentroContable);
                jsonObj.put("unidadEjecutora", ue);
                jsonObj.put("error", error);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                log.error("Error del Json  en el cambio de propiedades del usuario");
                e.printStackTrace();
            }
        }
    }
}
