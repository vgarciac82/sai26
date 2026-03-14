package com.syc.egresos.servlet;

import java.io.IOException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import com.syc.contable.PasivoDiferidoOperAjenaBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "PasivoDiferidoOperAjenaServlet", urlPatterns = { "/egresos/AplicaPasivoDiferido" })
public class PasivoDiferidoOperAjenaServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 3273304253562873899L;

    private static final Logger log = LoggerFactory.getLogger(OperacionesAjenasServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject respuesta = new JSONObject();
        try {
            // almacena mensajes de error
            String message = null;
            // validar sesion
            HttpSession session = req.getSession(false);
            if (session == null) {
                message = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
            }
            // validar usuario en sesion
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                message = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
            }
            if (message != null) {
                throw new Exception(message);
            } else {
                String caNoContrarrecibo = req.getParameter("contrarrecibo");
                String tipoRetencion = req.getParameter("tipoRetencion");
                PasivoDiferidoOperAjenaBussinessLogic pdoa = new PasivoDiferidoOperAjenaBussinessLogic(ATT_CONEXION);
                pdoa.buscaSolicitudesJSON(caNoContrarrecibo, tipoRetencion).toString();
                resp.setContentType("text/x-json; charset=ISO-8859-1");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                respuesta.put("success", "false");
                respuesta.put("message", e.toString());
                respuesta.put("resultObj", "");
            } catch (Exception e2) {
                log.error("No se pudo generar la respuesta: " + e2, e2);
                throw new ServletException(e2);
            }
        }
        ServletOutputStream out = resp.getOutputStream();
        out.println(respuesta.toString());
        out.flush();
        out.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
}
