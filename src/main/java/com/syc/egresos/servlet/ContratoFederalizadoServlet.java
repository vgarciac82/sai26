package com.syc.egresos.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.ProgFederalizadosBussinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.sai.contratos.core.ContratoFederalizadoBean;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ContratoFederalizadoServlet", urlPatterns = { "/contratosFederalizados/RegistraDetalle", "/contratosFederalizados/CreaCompromiso" })
public class ContratoFederalizadoServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -6230531682608221553L;

    private static final Logger log = LoggerFactory.getLogger(ContratoFederalizadoServlet.class);

    private String jniName;

    private static final String SAVE_DETAIL = "RegistraDetalle";

    private static final String CREATE_COMPROMISO = "CreaCompromiso";

    private static FolioGeneratorInterface fg = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        try {
            if (SAVE_DETAIL.equals(accion)) {
                ProgFederalizadosBussinessLogic pfbl = new ProgFederalizadosBussinessLogic(jniName);
                Map<String, BigDecimal> detailRow = pfbl.parseCalendarDetail(req);
                ContratoFederalizadoBean cfb = ContratoFederalizadoBean.instanceFromRequest(req);
                String ep = req.getParameter("ep");
                int insertados = pfbl.actualizaDetalle(ep, cfb, detailRow);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(insertados));
                return;
            } else if (CREATE_COMPROMISO.equals(accion)) {
                ProgFederalizadosBussinessLogic pfbl = new ProgFederalizadosBussinessLogic(jniName);
                ContratoFederalizadoBean cfb = ContratoFederalizadoBean.instanceFromRequest(req);
                String numCompromiso = pfbl.creaCompromisoFederalizado(c, u, fg, cfb);
                ResponseSender.sendClientSimpleMessage(resp, true, numCompromiso);
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error al guardar el detalle: " + e);
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
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class<?> clase = cl.loadClass("com.syc.gestion.custom.DefaultFolioGenerator");
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new ServletException(exc);
        }
    }
}
