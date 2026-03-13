package com.syc.contable.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.contable.AdministracionMensajesBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "AdministracionMensajesServlet", urlPatterns = { "/gstnmngr/AdministracionMensajes" })
public class AdministracionMensajesServlet extends HttpServlet {

    private static final long serialVersionUID = -2132300865011700319L;

    private static final Logger log = Logger.getLogger(AdministracionMensajesServlet.class);

    public AdministracionMensajesServlet() {
        super();
    }

    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null)
            response.sendRedirect("../index.jsp");
        AdministracionMensajesBusinessLogic amBL = new AdministracionMensajesBusinessLogic(GestionInterface.ATT_CONEXION);
        String mensajeAdmin = request.getParameter("mensajeAdmin");
        String ur = request.getParameter("UR");
        String cc = request.getParameter("cc");
        String accion = request.getParameter("accion");
        String activaAlerta = request.getParameter("activaAlerta") != null ? "S" : "N";
        String idUsuario = "";
        String[] lu = request.getParameterValues("checks");
        String mensaje = "";
        List<String> l;
        boolean res = true;
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        if ("SELECCIONA_USUARIOS".equals(accion)) {
            try {
                l = amBL.getselecChecks();
                String[] r = new String[l.size()];
                l.toArray(r);
                ResponseSender.sendResult(response, r, "data");
                return;
            } catch (Exception e) {
                log.error("Error cargando opciones delegadas al usuario: " + e, e);
                ResponseSender.sendError(response, "Error cargando opciones delegadas al usuario: " + e);
            }
        } else if ("LISTA_USUARIOS".equals(accion)) {
            try {
                l = amBL.getUsuariosCC(cc, ur);
                String[] r = new String[l.size()];
                l.toArray(r);
                ResponseSender.sendResult(response, r, "data");
                return;
            } catch (Exception e) {
                log.error("Error listando usuarios: " + e, e);
                ResponseSender.sendError(response, "Error listando usuarios: " + e);
            }
        } else if ("AGREGA_MENSAJE".equals(accion)) {
            try {
                if (mensajeAdmin != "") {
                    amBL.guardaAlcance(mensajeAdmin, ur, cc, lu, activaAlerta);
                } else {
                    session.setAttribute("mensaje", "Debe capturar un mensaje");
                    response.sendRedirect(basePath + "admin/AdministracionMensajes.jsp");
                    return;
                }
            } catch (Exception e) {
                log.error("Error asignando nuevo mensaje: " + e, e);
                mensaje = "Error asignando nuevo mensaje: " + e;
                if (session != null) {
                    session.setAttribute("mensaje", mensaje);
                    response.sendRedirect("../admin/administracionMensajes.jsp");
                    return;
                }
            }
            session.setAttribute("mensaje", "Asignación de mensaje realizada exitosamente.");
            response.sendRedirect(basePath + "admin/AdministracionMensajes.jsp");
        } else if ("DESACTIVA_MENSAJE".equals(accion)) {
            try {
                amBL.desactivaMensaje();
            } catch (Exception e) {
                log.error("Error desasignando mensaje actual: " + e, e);
                mensaje = "Error desasignando mensaje actual: " + e;
                if (session != null) {
                    session.setAttribute("mensaje", mensaje);
                    response.sendRedirect("../admin/administracionMensajes.jsp");
                    return;
                }
            }
            session.setAttribute("mensaje", "Eliminación de mensaje realizada exitosamente.");
            response.sendRedirect(basePath + "admin/AdministracionMensajes.jsp");
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
