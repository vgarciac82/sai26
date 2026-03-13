package com.syc.contable.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CancelaCompromisos", urlPatterns = { "/gstnmngr/CancelaCompromisos" })
public class CancelaCompromisos extends HttpServlet {

    private static final Logger log = Logger.getLogger(CancelaCompromisos.class);

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public CancelaCompromisos() {
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

    /**
     * The doDelete method of the servlet. <br>
     *
     * This method is called when a HTTP delete request is received.
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
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CancelaDocumento cancelaDocumento = new
        // CancelaDocumento(GestionInterface.ATT_CONEXION);
        CompromisoBussinessLogic cmpBL = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession httpSession = request.getSession(false);
        Usuario usuario = (Usuario) httpSession.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
        }
        String[] compromisosCancelados = StringUtils.trimToEmpty(request.getParameter("comp")).split(",");
        boolean error = false;
        String msg = "";
        int i = 1;
        try {
            for (String compromisoCancelado : compromisosCancelados) {
                cmpBL.estatusCancelado(compromisoCancelado);
                i++;
            }
            msg = "Se regreso el estatus a captura de " + i + " compromisos ";
        } catch (SQLException e) {
            error = true;
            log.error(e, e);
        } catch (Exception ex) {
            log.error(ex, ex);
            error = true;
        }
        if (error)
            msg = "Se regreso el estatus a captura de " + i + " de " + compromisosCancelados.length + " compromisos debido a un error ";
        Map<String, String> r = new HashMap<>();
        r.put("message", msg);
        Util.sendJSONResponse(response, (error ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : HttpServletResponse.SC_OK), r);
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
