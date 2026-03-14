package com.syc.sai.firmaElectronica.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import com.axtel.web.exceptions.SessionExpiredException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.FirmaAutorizacionObraInterface;
import com.syc.obrapublica.businessLogic.FirmaAutEstObraBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Servlet implementation class FirmaAutEstimacionObraServlet
 */
@WebServlet(name = "FirmaAutEstimacionObraServlet", urlPatterns = { "/FirmaAutEstimacionObraServlet" })
public class FirmaAutEstimacionObraServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(FirmaAutEstimacionObraServlet.class);

    public static String REPORT_PATH = "";

    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        REPORT_PATH = getServletContext().getRealPath("Reportes");
    }

    /**
     * @see Servlet#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean success = false;
        String msg = "";
        Usuario u = null;
        FirmaAutorizacionObraInterface firmaAut = null;
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                log.warn("Usuario sin sesion. ");
                throw new SessionExpiredException("Su sesion ha terminado. Ingrese nuevamente al sistema.");
            }
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                log.warn("Sesion sin objeto usuario. ");
                throw new SessionExpiredException("Su sesion ha terminado. Ingrese nuevamente al sistema.");
            }
            firmaAut = new FirmaAutEstObraBusinessLogic();
            firmaAut.enviaAutorizacion(u, request, REPORT_PATH);
            response.getWriter();
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.getMessage();
        } finally {
            u = null;
            firmaAut = null;
        }
        JSONObject obj = null;
        PrintWriter out = null;
        try {
            obj = new JSONObject();
            obj.put("success", success);
            obj.put("message", msg);
            out = response.getWriter();
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out.print(obj.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("Error procesando respuesta! " + e, e);
            throw new ServletException(e);
        } finally {
            obj = null;
            out = null;
        }
    }
}
