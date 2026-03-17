package com.syc.sai.firmaElectronica.servlet;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "CasoFirmaElectronicaServlet", urlPatterns = { "/FIEL/solicitaFirmaElectronica", "/FIEL/validaUsuarioAutorizador" })
public class CasoFirmaElectronicaServlet extends HttpServlet implements GestionInterface {

    /**
     * Version
     */
    private static final long serialVersionUID = -4043730852663737703L;

    private static final Logger log = LoggerFactory.getLogger(CasoFirmaElectronicaServlet.class);

    private static final String VALIDA_USUARIO_AUTORIZADOR = "validaUsuarioAutorizador";

    private String jniName;

    private String reportPath;

    /*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, jakarta.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msg = "";
        if (session == null)
            msg = "Su sesion ha caducado. Ingrese nuevamente al sistema.";
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            msg = "Su sesion ha caducado. Ingrese nuevamente al sistema.";
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            msg = "Su sesion ha caducado. Ingrese nuevamente al sistema.";
        } else if (StringUtils.isBlank(c.getTipoCaso().getInterface()))
            msg = "El tipo de tramite: " + c.getTipoCaso().getDescripcion() + " no contiene interfaz asignada. No se puede realizar firma electronica.";
        try {
            if (StringUtils.isBlank(msg)) {
                TipoCasoInterface tci = Util.instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                tci.onSolicitaFirmaElectronica(c, u, reportPath);
                ResponseSender.sendClientSimpleMessage(resp, true, "Solicitud procesada exitosamente");
            } else {
                throw new Exception("No se puede realizar el tramite debido al siguiente error: " + msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new Exception("Su sesion ha caducado. Ingrese nuevamente al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new Exception("Su sesion ha caducado. Ingrese nuevamente al sistema.");
            Caso c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null)
                throw new Exception("Su sesion ha caducado. Ingrese nuevamente al sistema.");
            String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            if (VALIDA_USUARIO_AUTORIZADOR.equals(accion)) {
                FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(jniName);
                boolean puedeAutorizar = febl.esUsuarioAutorizadorSICOP(u);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(puedeAutorizar));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendClientSimpleMessage(resp, false, "No se puede realizar la operacion debido al siguiente error: " + e.toString());
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
            reportPath = getServletContext().getRealPath("Reportes" + File.separator);
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
