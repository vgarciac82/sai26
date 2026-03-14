package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.RectificacionPresupuestariaBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "RectificacionServlet", urlPatterns = { "/gstnmngr/RectificacionServlet" })
public class RectificacionServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 8140857888553424437L;

    private static final Logger log = LoggerFactory.getLogger(RectificacionServlet.class);

    public RectificacionServlet() {
        super();
    }

    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //para obtener el ejercicio fiscal en diferentes funciones
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        RectificacionPresupuestariaBusinessLogic rectificacion = new RectificacionPresupuestariaBusinessLogic(GestionInterface.ATT_CONEXION);
        String accion = request.getParameter("accion");
        String fApl = request.getParameter("fApl");
        if (accion != null && !"".equals(accion)) {
            if ("1".equals(accion)) {
                //APARTADO RECTIFICACION
                HttpSession session = request.getSession(false);
                PrintWriter out = response.getWriter();
                if (session == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                try {
                    out.println(new String(rectificacion.ValidaRectificacion(new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), usuario, adecProy.obtenEjercicioFiscal(), usuario.getU_Ramo(), usuario.getU_UR(), c, usuario.getPropiedad("CCENTROCONTABLE").getValor(), c.getCasoDato("FECHA_AP_CONT").getValor(), m, prefixPath, usuario.getLogin()).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.warn("Object: {}", ex.getMessage());
                }
            } else if ("2".equals(accion)) {
                //AUTORIZACION RECTIFICACION
                HttpSession session = request.getSession(false);
                PrintWriter out = response.getWriter();
                if (session == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                try {
                    out.println(new String(rectificacion.aplicaRectificacion(c, m, prefixPath, usuario.getLogin(), usuario, fApl).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.warn("Object: {}", ex.getMessage());
                }
            } else if ("3".equals(accion)) {
                //CANCELA RECTIFICACION
                HttpSession session = request.getSession(false);
                PrintWriter out = response.getWriter();
                if (session == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                try {
                    out.println(new String(rectificacion.cancelarAppContableNuevo(c, m, prefixPath, usuario.getLogin(), c.getCasoDato("FECHA_AP_CONT").getValor()).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.warn("Object: {}", ex.getMessage());
                }
            }
        }
    }

    public void init() throws ServletException {
    }
}
