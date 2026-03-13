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
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;
import com.syc.ejercido.pagado.CierrePresupuestal;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AvanzaCasoSICOPServlet", urlPatterns = { "/FIEL/autorizaEnvioSICOP" })
public class AvanzaCasoSICOPServlet extends HttpServlet implements GestionInterface {

    /**
     * Version
     */
    private static final long serialVersionUID = -4043730852663737703L;

    private static final Logger log = LoggerFactory.getLogger(AvanzaCasoSICOPServlet.class);

    private String jniName;

    private CierrePresupuestal cpAMF;

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
        }
        String urlDirect = "../caso/inboxAutorizaFIEL.jsp";
        try {
            if (StringUtils.isBlank(msg)) {
                FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(jniName);
                boolean autorizar = "S".equals(req.getParameter("envioSicop"));
                String tipoDoc = c.getTipoCaso().getGavetaAsociada();
                String laudo_IF = "";
                String tablaEncabezado;
                if (SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(c.getTipoCaso().getGavetaAsociada()) == null)
                    tablaEncabezado = "t" + c.getTipoCaso().getGavetaAsociada() + "encabezado";
                else
                    tablaEncabezado = SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(c.getTipoCaso().getGavetaAsociada());
                String campoLlave;
                if (SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(c.getTipoCaso().getGavetaAsociada()) == null)
                    campoLlave = "nFolio" + c.getTipoCaso().getGavetaAsociada();
                else
                    campoLlave = SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(c.getTipoCaso().getGavetaAsociada());
                int valorLlave = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
                String nFolio = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
                int currentStatus = 0;
                if ("PAGODIVERSO".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada())) {
                    EgresoPAGODIVERSOEncabezado payment = new EgresoPAGODIVERSOEncabezado();
                    payment.setJniName(jniName);
                    payment = (EgresoPAGODIVERSOEncabezado) payment.cargaEncabezado(valorLlave);
                    currentStatus = payment.getEnviadoSICOP();
                }
                if ("RELACIONGASTOS".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada())) {
                    laudo_IF = febl.esLaudoIF(valorLlave);
                }
                int nextStatus = 0;
                if ("SI".equals(laudo_IF)) {
                    nextStatus = SolicitudFirmaElectronica.LAYOUT_GENERADO_LAUDOS_IF;
                } else {
                    nextStatus = SolicitudFirmaElectronica.GENERA_LAYOUT;
                }
                if (currentStatus == SolicitudFirmaElectronica.WAIT_FOR_MANAGER_AUTH)
                    urlDirect = "../caso/inboxPreautorizaFIEL.jsp";
                if (autorizar) {
                    if (currentStatus == SolicitudFirmaElectronica.WAIT_FOR_MANAGER_AUTH)
                        nextStatus = SolicitudFirmaElectronica.AUT_LAYOUT;
                    else {
                        if ("SI".equals(laudo_IF)) {
                            nextStatus = SolicitudFirmaElectronica.LAYOUT_GENERADO_LAUDOS_IF;
                        } else {
                            nextStatus = SolicitudFirmaElectronica.GENERA_LAYOUT;
                        }
                    }
                } else
                    nextStatus = SolicitudFirmaElectronica.ESPERA_CANCELACION;
                febl.actualizaEstatusSICOP(tablaEncabezado, campoLlave, valorLlave, nextStatus);
                if (!autorizar) {
                    JSONObject resJson = cpAMF.cancelaDevengado(tipoDoc, nFolio, u, false);
                }
                log.info(" Autorizando el envio de SICOP/SIAFF del tramite: " + tablaEncabezado + " con folio: " + valorLlave);
                session.setAttribute("MSG", (autorizar ? "Autorizacion" : "Rechazo") + " de envio a SICOP del tramite " + c.getTipoCaso().getDescripcion() + " con folio: " + valorLlave + " realizado exitosamente. " + (autorizar ? "Ahora puede generar el layout" : " Cancelación del devengado exitoso."));
            } else {
                throw new Exception(msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            session.setAttribute("MSG", "No se puede logro continuar con el proceso debido al siguiente error: " + e);
        }
        resp.sendRedirect(urlDirect);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
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
        cpAMF = new CierrePresupuestal(jniName);
    }
}
