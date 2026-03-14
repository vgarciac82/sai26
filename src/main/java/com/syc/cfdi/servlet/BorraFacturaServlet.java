package com.syc.cfdi.servlet;

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
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorraFacturaServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 3305084130463045947L;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(BorraFacturaServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        boolean validaContraSAT = false;
        boolean notificaFacturasInvalidasSAT = false;
        boolean notificaFacturasEFA = false;
        if (session == null) {
            log.warn("Se intenta operacion sin sesion");
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (u == null || c == null) {
            log.warn("Se intenta operacion sin sesion");
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
        validaContraSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_VALIDACION_SAT"));
        notificaFacturasEFA = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
        notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));
        if ((c.getCasoOperacion(0) != null && c.getCasoOperacion(0).getOperacion() != null && !c.getCasoOperacion(0).getOperacion().getResponsable().toUpperCase().startsWith("CONSULTA"))) {
            String accion = req.getParameter("accion");
            String documento = req.getParameter("documento");
            String folio = req.getParameter("folio");
            String folioGestion = c.getFolio();
            String rfc = req.getParameter("rfc");
            String parametrosFaltantes = "";
            String token = "";
            if (StringUtils.isEmpty(accion)) {
                parametrosFaltantes += token + "Accion";
                token = ",";
            }
            if (StringUtils.isEmpty(documento)) {
                parametrosFaltantes += token + "Documento";
                token = ",";
            }
            if (StringUtils.isEmpty(folio)) {
                parametrosFaltantes += token + "folio";
                token = ",";
            }
            if (StringUtils.isEmpty(rfc)) {
                parametrosFaltantes += token + "rfc";
                token = ",";
            }
            if (!StringUtils.isEmpty(parametrosFaltantes)) {
                log.warn("Object: {}", "Se intenta operacion sin parametros completos " + parametrosFaltantes);
                ResponseSender.sendClientSimpleMessage(resp, false, "No se recibieron los siguientes parametros[ " + parametrosFaltantes + "]");
                return;
            }
            FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
            fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
            fbl.setNotificaErroresEFA(notificaFacturasEFA);
            try {
                int eliminados = 0;
                CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
                ITree tree = cbl.getArbolCaso(c);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("tree.model", tree);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(eliminados));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error al eliminar: " + e);
                return;
            }
        } else {
            ResponseSender.sendClientSimpleMessage(resp, true, "0");
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
    }
}
