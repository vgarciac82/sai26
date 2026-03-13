package com.syc.sai.firmaElectronica.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.core.SolicitudCajaFirmaElectronica;
import com.syc.egresos.core.RelacionGastosMasivaBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "FirmaElectronicaIntegracionServlet", urlPatterns = { "/egresos/cancelaIntegracion" })
public class FirmaElectronicaIntegracionServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 3253825593092507538L;

    public static final String CANCELA_INTEGRACION = "cancelaIntegracion";

    private static final Logger log = LoggerFactory.getLogger(FirmaElectronicaIntegracionServlet.class);

    private String jniName;

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
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        if (CANCELA_INTEGRACION.equals(accion)) {
            try {
                int folioMasivo = Integer.parseInt(req.getParameter("nFolio"));
                FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(jniName);
                febl.cargaInformacionTramite("RELACIONGASTOS");
                SolicitudFirmaElectronica sfe = (SolicitudFirmaElectronica) Util.instanceCasoFIEL(febl.getTramiteSolicitud().getClaseImplementa());
                sfe.setDocument("RELACIONGASTOS");
                sfe.setHeader("tRELACIONGASTOSEncabezado");
                sfe.setDetail("tRELACIONGASTOSDetalle");
                sfe.setField("nFolioRELACIONGASTOS");
                sfe.setIdField(folioMasivo);
                sfe.setUsuario(u);
                sfe.setCargaMasiva(true);
                RelacionGastosMasivaBussinessLogic rgmbl = new RelacionGastosMasivaBussinessLogic(jniName);
                rgmbl.cancelaCargaMasiva(u, folioMasivo);
                rgmbl.notificaCancelacion(folioMasivo, sfe);
                ResponseSender.sendClientSimpleMessage(resp, true, "Cancelado exitosamente");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
            }
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
