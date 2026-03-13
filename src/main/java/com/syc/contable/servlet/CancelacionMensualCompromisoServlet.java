package com.syc.contable.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CancelacionCompromisoServlet", urlPatterns = { "/compromiso/cancelacionMasivaMensual" })
public class CancelacionMensualCompromisoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -3590765210008483180L;

    private String jniName = "";

    private static Logger log = Logger.getLogger(CancelacionMensualCompromisoServlet.class);

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema");
            return;
        }
        String nMes = req.getParameter("nMes");
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(jniName);
        try {
            int totalCancelados = cbl.cancelacionMensual(Integer.parseInt(nMes, 10));
            ResponseSender.sendClientSimpleMessage(resp, true, "Compromisos cancelados exitosamente. Se cancelaron " + String.valueOf(totalCancelados) + " compromisos");
        } catch (Exception e) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error al intentar cancelar el compromiso: " + e);
            log.error("Error en la cancelacion masiva de compromisos: " + e, e);
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
