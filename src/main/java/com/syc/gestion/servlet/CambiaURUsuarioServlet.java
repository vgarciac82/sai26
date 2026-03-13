package com.syc.gestion.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.syc.gestion.core.UnidadEjecutoraBusinessLogic;
import com.syc.gestion.core.Usuario;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CambiaUnidadServlet", urlPatterns = { "/usuarios/CambiaUnidad" })
public class CambiaURUsuarioServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 1052294261504907936L;

    public static final Logger log = Logger.getLogger(CambiaURUsuarioServlet.class);

    private String jniName;

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msg = "";
        boolean status = false;
        try {
            if (session == null)
                msg = "No tienen sesion activa. Ingrese nuevamente al sistema.";
            else {
                Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                if (u == null)
                    msg = "No tienen sesion activa. Ingrese nuevamente al sistema.";
                else {
                    UnidadEjecutoraBusinessLogic uebl = new UnidadEjecutoraBusinessLogic(jniName);
                    String urNueva = StringUtils.trimToEmpty(req.getParameter("unidadSeleccionada"));
                    uebl.setUnidadEjecutora(urNueva);
                    String centroContable = uebl.getCentroContableUnidad();
                    if (!StringUtils.isBlank(urNueva) & !StringUtils.isBlank(centroContable)) {
                        u.setU_UR(urNueva);
                        u.getPropiedad("CCENTROCONTABLE").setValor(centroContable);
                        session.setAttribute(ATT_USER, u);
                        msg = "Cambio de unidad realizado exitosamente.";
                        status = true;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            msg = e.toString();
        }
        try {
            JSONObject resJson = new JSONObject();
            resJson.put("status", status);
            resJson.put("msg", msg);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            ServletOutputStream out = resp.getOutputStream();
            out.println(resJson.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(e, e);
            throw new ServletException(e);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
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
