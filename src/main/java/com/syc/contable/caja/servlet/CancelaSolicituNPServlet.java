package com.syc.contable.caja.servlet;

import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.caja.CancelaCajaBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CancelaSolicitudNP", urlPatterns = { "/CancelaSolNP" })
public class CancelaSolicituNPServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -3620769987066164309L;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(CancelaSolicituNPServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = "";
        log.info("Cancelando solicitudes de caja");
        HttpSession session = req.getSession(false);
        String responseType = "";
        boolean success = false;
        try {
            if (session == null)
                throw new Exception("Su sesion ha terminado. Ingrese nuevamente al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                msg = "Su sesion ha terminado. Ingrese nuevamente al sistema.";
            String autType = req.getParameter("autType");
            String cancelReason = StringUtils.trimToEmpty(req.getParameter("reason"));
            String[] cancel = req.getParameterValues("chk_caja");
            responseType = StringUtils.isBlank(req.getParameter("responseType")) ? "REDIRECT" : req.getParameter("responseType");
            if (cancel == null || cancel.length == 0)
                msg = "No se recibieron folios a cancelar.";
            if (StringUtils.isEmpty(msg)) {
                CancelaCajaBusinessLogic ccbl = new CancelaCajaBusinessLogic(jniName);
                if ("FIEL".equals(autType)) {
                    ccbl.cancelaSolicitudFirmaElectronica(cancel[0], cancelReason, u);
                } else {
                    List<String> result = ccbl.cancelaSolicitudes(cancel);
                    String token = "";
                    for (int i = 0; i < result.size(); i++) {
                        msg += token + result.get(i);
                        token = "<br>";
                    }
                }
            }
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg += e.toString();
        }
        if ("REDIRECT".equalsIgnoreCase(responseType)) {
            if (session != null)
                session.setAttribute("RESULT", msg);
            else {
                resp.sendRedirect("index.jsp");
                return;
            }
            resp.sendRedirect("Generador/CancelaSolicitudesNoPresupuestales.jsp");
        } else if ("JSON".equalsIgnoreCase(responseType)) {
            ResponseSender.sendClientSimpleMessage(resp, success, msg);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
