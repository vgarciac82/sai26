package com.syc.contable.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
// // // // // import org.omg.PortableServer.ID_UNIQUENESS_POLICY_ID;
import com.syc.contable.ControlAccesoBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ControlAccesoServlet", urlPatterns = { "/gstnmngr/ControlAcceso" })
public class ControlAccesoServlet extends HttpServlet {

    private static final long serialVersionUID = -2132300865011700319L;

    private static final Logger log = LoggerFactory.getLogger(ControlAccesoServlet.class);

    public ControlAccesoServlet() {
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
        ControlAccesoBusinessLogic caBL = new ControlAccesoBusinessLogic(GestionInterface.ATT_CONEXION);
        int tc = (request.getParameter("TC") == null ? -1 : new Integer(request.getParameter("TC")).intValue());
        String ur = request.getParameter("UR");
        String cc = request.getParameter("cc");
        String idUsuario = "";
        String apagar = request.getParameter("apagar");
        String prender = request.getParameter("prender");
        String listarUsuariosCC = request.getParameter("listarUsuariosCC");
        String[] lu = request.getParameterValues("checks");
        String mensaje = "";
        List<String> l;
        boolean res = true;
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        try {
            if (prender != null) {
                if (lu != null) {
                    for (int i = 0; i < lu.length; i++) {
                        idUsuario = lu[i];
                        caBL.prender(tc, ur, cc, idUsuario);
                    }
                } else {
                    caBL.prender(tc, ur, cc, "");
                }
            } else if (apagar != null) {
                if (lu != null) {
                    for (int i = 0; i < lu.length; i++) {
                        idUsuario = lu[i];
                        caBL.apagar(tc, ur, cc, idUsuario);
                    }
                } else {
                    caBL.apagar(tc, ur, cc, "");
                }
            } else if (listarUsuariosCC != null) {
                l = caBL.getUsuariosCC(cc, tc);
                String[] r = new String[l.size()];
                l.toArray(r);
                ResponseSender.sendResult(response, r, "data");
                return;
            }
            if (res && apagar != null)
                mensaje = "El desactivado de trámites fue realizado con éxito.";
            else if (res && prender != null)
                mensaje = "La  Activación de trámites fue realizada con éxito.";
            session.setAttribute("mensaje", mensaje);
            response.sendRedirect(basePath + "plantillasCasos/controlAcceso.jsp");
        } catch (Exception e) {
            log.error("error apagando o encendiendo trámites" + e, e);
            res = false;
            mensaje += "No fue posible realizar el " + (apagar != null ? "Desactivado" : "Activado") + " de los tr\u00E1mites. Notifique a soporte SAI <br> Error:" + e.toString();
            if (session != null)
                session.setAttribute("mensaje", mensaje);
            response.sendRedirect(basePath + "plantillasCasos/controlAcceso.jsp");
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
