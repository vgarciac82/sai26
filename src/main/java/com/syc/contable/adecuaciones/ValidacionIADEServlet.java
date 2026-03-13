package com.syc.contable.adecuaciones;

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
import org.apache.log4j.Logger;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.ejercido.pagado.ActualizaFechaAplicacionServlet;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ValidacionIADEServlet", urlPatterns = { "/ValidacionIADEServlet" })
public class ValidacionIADEServlet extends HttpServlet implements GestionInterface {

    private static String jniName;

    private static final Logger log = Logger.getLogger(ActualizaFechaAplicacionServlet.class);

    private static final long serialVersionUID = -7321972009382438157L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion ha terminado. Reingrese al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion ha terminado. Reingrese al sistema");
            return;
        }
        String strFolio = req.getParameter("nFolioIADE");
        if (StringUtils.isEmpty(strFolio)) {
            ResponseSender.sendClientSimpleMessage(resp, false, "No se recibio el parametro nFolioIADE. Notifique al administrador");
        }
        try {
            int nFolioIADE = Integer.parseInt(strFolio);
            AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
            List<String> errores = adbl.validaIntegracionNeteo(nFolioIADE);
            if (errores.size() > 0) {
                ResponseSender.sendClientSimpleMessage(resp, false, errores.get(0));
            } else {
                ResponseSender.sendClientSimpleMessage(resp, true, "");
            }
        } catch (Exception e) {
            log.error(e, e);
            ResponseSender.sendClientSimpleMessage(resp, false, e.toString().replaceAll("\"", ""));
        }
    }

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
