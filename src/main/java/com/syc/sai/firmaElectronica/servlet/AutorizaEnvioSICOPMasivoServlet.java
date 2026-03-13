package com.syc.sai.firmaElectronica.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.google.gson.Gson;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = { "/FIEL/autorizaEnvioSICOPMasivo", "/FIEL/revisaEnvioSICOPMasivo" })
public final class AutorizaEnvioSICOPMasivoServlet extends HttpServlet {

    private static final long serialVersionUID = 2789426049420564428L;

    private static final Logger log = LoggerFactory.getLogger(AutorizaEnvioSICOPMasivoServlet.class);

    private FirmaElectronicaBusinessLogic febl = null;

    private String jniName = "";

    private CasoBusinessLogic processBL = null;

    private ConfiguraAplicativoBusinessLogic sysConfig = null;

    private boolean esAmbiental = false;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("Inicio de doPost en servlet de autorización de trámites.");
        String action = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        log.info("#Executing: " + action + " action");
        HttpSession session = req.getSession(false);
        String msg = "";
        if (session == null) {
            msg = "Su sesión ha caducado. Ingrese nuevamente al sistema.";
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null) {
            msg = "Su sesión ha caducado. Ingrese nuevamente al sistema.";
        }
        try {
            if (StringUtils.isBlank(msg)) {
                String[] idCasos = req.getParameterValues("idCaso");
                if (idCasos == null || idCasos.length == 0) {
                    log.warn("No se recibieron IDs de casos en la solicitud.");
                    throw new Exception("No se recibieron IDs de casos.");
                }
                int total = idCasos.length;
                int exito = 0;
                int status = ("revisaEnvioSICOPMasivo".equals(action) ? SolicitudFirmaElectronica.AUT_LAYOUT : SolicitudFirmaElectronica.GENERA_LAYOUT);
                log.info("Procesando " + total + " trámites para autorización. Nuevo estatus: " + status);
                for (String idCaso : idCasos) {
                    log.debug("Procesando trámite con ID Caso: " + idCaso);
                    try {
                        Caso c = processBL.getCaso(Integer.parseInt(idCaso));
                        log.debug("Caso obtenido: " + c);
                        String tablaEncabezado = SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(c.getTipoCaso().getGavetaAsociada());
                        String tablaDetalle = "";
                        if (tablaEncabezado == null) {
                            tablaEncabezado = "t" + c.getTipoCaso().getGavetaAsociada() + "encabezado";
                            tablaDetalle = "t" + c.getTipoCaso().getGavetaAsociada() + "detalle";
                        }
                        String campoLlave = SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(c.getTipoCaso().getGavetaAsociada());
                        if (campoLlave == null) {
                            campoLlave = "nFolio" + c.getTipoCaso().getGavetaAsociada();
                        }
                        int valorLlave = Util.folio(c);
                        log.debug("Autorizando trámite en tabla: " + tablaEncabezado + ", campo: " + campoLlave + ", valor: " + valorLlave);
                        if (esAmbiental == false && "tPAGODIVERSOENCABEZADO".equalsIgnoreCase(tablaEncabezado)) {
                            SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
                            solicitudPagoPrinter.setDetail(tablaDetalle);
                            solicitudPagoPrinter.setDocument(c.getTipoCaso().getGavetaAsociada());
                            solicitudPagoPrinter.setField(campoLlave);
                            solicitudPagoPrinter.setFileExtension("pdf");
                            solicitudPagoPrinter.setHeader(tablaEncabezado);
                            solicitudPagoPrinter.setIdField(valorLlave);
                            solicitudPagoPrinter.setUsuario(u);
                            boolean vobo = febl.voBoFirmado(c.getTipoCaso().getGavetaAsociada(), valorLlave);
                            if (!vobo)
                                febl.enviarCorreoVoBo(tablaEncabezado, campoLlave, valorLlave, SolicitudFirmaElectronica.VO_BO_SICOP, solicitudPagoPrinter);
                            else {
                                febl.actualizaEstatusSICOP(tablaEncabezado, campoLlave, valorLlave, status);
                            }
                        } else {
                            febl.actualizaEstatusSICOP(tablaEncabezado, campoLlave, valorLlave, status);
                        }
                        log.info("Autorizado envío SICOP/SIAFF del trámite: " + tablaEncabezado + " con folio: " + valorLlave);
                        exito++;
                    } catch (Exception e) {
                        log.error("Error mientras se avanzaba trámite con ID Caso: " + idCaso + ". Detalles: " + e.toString(), e);
                    }
                }
                Map<String, Integer> result = new HashMap<>();
                result.put("total", total);
                result.put("success", exito);
                String jsonResponse = new Gson().toJson(result);
                log.info("Autorización finalizada. Total: " + total + ", Exitosos: " + exito);
                log.debug("Enviando respuesta JSON: " + jsonResponse);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(jsonResponse);
            } else {
                log.warn("Sesión inválida: " + msg);
                throw new Exception(msg);
            }
        } catch (Exception e) {
            log.error("Error general en doPost: " + e.toString(), e);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ocurrió un error al procesar la solicitud. " + e.toString() + "\"}");
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
        sysConfig = new ConfiguraAplicativoBusinessLogic(jniName);
        febl = new FirmaElectronicaBusinessLogic(jniName);
        processBL = new CasoBusinessLogic(jniName);
        esAmbiental = "true".equalsIgnoreCase(sysConfig.getSystemSetting("SAI_AMBIENTAL"));
        log.info(" ============================================ AMBIENTE: " + (esAmbiental ? " COMPENSACION AMBIENTAL" : " RECURSOS FISCALES") + "================================================");
    }
}
