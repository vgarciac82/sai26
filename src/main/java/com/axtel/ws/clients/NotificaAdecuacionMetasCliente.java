package com.axtel.ws.clients;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.axtel.notifications.CorreoNotificacion;
import com.axtel.presupuesto.AdecuacionCancelacion;
import com.axtel.presupuesto.AdecuacionDetalleResumen;
import com.axtel.presupuesto.AdecuacionEncabezadoResumen;
import com.axtel.presupuesto.AdecuacionResumen;
import com.axtel.web.clients.WSClient;
import com.axtel.ws.exceptions.WSException;
import com.syc.contable.adecuaciones.AdecuacionDetalleManager;
import com.syc.contable.adecuaciones.AdecuacionEncabezadoManager;
import com.syc.contable.adecuaciones.UsuarioNotificado;
import com.syc.contable.core.AdecuacionManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class NotificaAdecuacionMetasCliente extends WSClient {

    private static final Logger log = LoggerFactory.getLogger(NotificaAdecuacionMetasCliente.class);

    private static String MAIL_TEMPLATE;

    private static String MAIL_TEMPLATE_CAPTURA;

    public NotificaAdecuacionMetasCliente(String urlService) {
        super();
        log.info("Object: {}", "Creado cliente para servicio de notificacion de adecuaciones. URL[" + urlService + "]");
        setUrlService(urlService);
        loadMailTemplates();
    }

    public NotificaAdecuacionMetasCliente() {
        super();
        loadMailTemplates();
    }

    private void loadMailTemplates() {
        MAIL_TEMPLATE = Util.readMailTemplate("/NotificacionSIPLAN.html");
        MAIL_TEMPLATE_CAPTURA = Util.readMailTemplate("/NotificacionSIPLANCapturista.html");
    }

    public AdecuacionResumen readAdecuacionNotificar(Connection conn, int folioAdecuacion) throws Exception {
        AdecuacionEncabezadoResumen encabezado = AdecuacionEncabezadoManager.readAdecuacionResumenEncabezado(conn, folioAdecuacion);
        List<AdecuacionDetalleResumen> detalle = AdecuacionDetalleManager.readAdecuacionDetalle(conn, folioAdecuacion, AdecuacionManager.leeProgramaMetas(conn));
        for (AdecuacionDetalleResumen r : detalle) {
            r.setFolio(folioAdecuacion);
        }
        return new AdecuacionResumen(encabezado, detalle);
    }

    public AdecuacionRespuesta enviaNotificacionCancelacion(Connection conn, AdecuacionCancelacion adecuacionCancelacion) throws WSException {
        log.debug("Object: " + String.valueOf("Iniciando envio de cancelacion de adecuacion: " + adecuacionCancelacion.getFolio()));
        WebTarget target = getClient().target(getUrlService());
        Builder requestBuilder = target.request(MediaType.APPLICATION_JSON);
        Entity<AdecuacionCancelacion> msgJSON = Entity.json(adecuacionCancelacion);
        log.trace("Object: {}", "Enviando: " + msgJSON);
        Response rsp = requestBuilder.post(msgJSON);
        AdecuacionRespuesta adecuacionRespuesta = null;
        if (rsp.getStatus() != HttpServletResponse.SC_OK) {
            String motivoError = "Error desconocido";
            try {
                adecuacionRespuesta = rsp.readEntity(AdecuacionRespuesta.class);
                motivoError = adecuacionRespuesta.getMensaje();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            throw new WSException("Problemas en request a endpoint[] Status: " + rsp.getStatus() + " - " + motivoError);
        }
        adecuacionRespuesta = rsp.readEntity(AdecuacionRespuesta.class);
        log.info("Object: {}", "Registro de adecuacion terminado. Resultado:" + rsp);
        return adecuacionRespuesta;
    }

    public AdecuacionRespuesta enviaNotificacion(Connection conn, AdecuacionResumen adecuacionNotificar) throws WSException {
        log.debug("Object: " + String.valueOf("Iniciando envio de registro de adecuacion: " + adecuacionNotificar.getEncabezado().getFolio()));
        log.info("Object: {}", "Se consumira servicio en: " + getUrlService());
        WebTarget target = getClient().target(getUrlService());
        Builder requestBuilder = target.request(MediaType.APPLICATION_JSON);
        Entity<AdecuacionResumen> msgJSON = Entity.json(adecuacionNotificar);
        log.trace("Object: {}", "Enviando: " + msgJSON);
        Response rsp = requestBuilder.post(msgJSON);
        AdecuacionRespuesta adecuacionRespuesta = null;
        if (rsp.getStatus() != HttpServletResponse.SC_OK) {
            String motivoError = "Error desconocido";
            try {
                adecuacionRespuesta = rsp.readEntity(AdecuacionRespuesta.class);
                motivoError = adecuacionRespuesta.getMensaje();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            throw new WSException("Problemas en request a endpoint[] Status: " + rsp.getStatus() + " - " + motivoError);
        }
        adecuacionRespuesta = rsp.readEntity(AdecuacionRespuesta.class);
        log.info("Object: {}", "Registro de adecuacion terminado. Resultado:" + rsp);
        return adecuacionRespuesta;
    }

    public void guardaAdecuacionNotificacion(Connection conn, AdecuacionResumen adecuacionNotificar) throws SQLException {
        AdecuacionEncabezadoManager.insertaAdecuacionNotificadaEncabezado(conn, adecuacionNotificar.getEncabezado());
        AdecuacionEncabezadoManager.insertaAdecuacionDetalleResumen(conn, adecuacionNotificar.getDetalle());
    }

    public AdecuacionResumen readAdecuacionNotificada(Connection conn, int folioAdecuacion) throws Exception {
        AdecuacionEncabezadoResumen encabezado = AdecuacionEncabezadoManager.readAdecuacionNotificadaEncabezado(conn, folioAdecuacion);
        List<AdecuacionDetalleResumen> detalle = AdecuacionEncabezadoManager.readAdecuacionNotificadaDetalle(conn, folioAdecuacion);
        for (AdecuacionDetalleResumen r : detalle) {
            r.setFolio(folioAdecuacion);
        }
        return new AdecuacionResumen(encabezado, detalle);
    }

    private List<CorreoNotificacion> generateMail(Connection conn, AdecuacionResumen adecuacionNotificar, List<UsuarioNotificado> usuarios, String mailTemplate) throws Exception {
        log.trace("Object: {}", mailTemplate);
        List<CorreoNotificacion> correos = new ArrayList<>();
        StringBuilder datosEncabezadoAdecuacion = new StringBuilder("<tr>").append("<td>").append(adecuacionNotificar.getEncabezado().getFolio()).append("</td>").append("<td>").append(adecuacionNotificar.getEncabezado().getJustificacion()).append("</td>").append("</tr>");
        StringBuilder datosDetalleAdecuacion = new StringBuilder("<tbody>");
        for (AdecuacionDetalleResumen renglon : adecuacionNotificar.getDetalle()) {
            datosDetalleAdecuacion.append("<tr>");
            datosDetalleAdecuacion.append("<td>").append(renglon.getPrograma()).append("</td>");
            datosDetalleAdecuacion.append("<td>").append(renglon.getUnidadEjecutora()).append("</td>");
            datosDetalleAdecuacion.append("<td>").append(renglon.getEntidadFederativa()).append("</td>");
            datosDetalleAdecuacion.append("<td>").append(renglon.getTipoMovimiento()).append("</td>");
            datosDetalleAdecuacion.append("<td>").append(renglon.getMes()).append("</td>");
            datosDetalleAdecuacion.append("<td>").append(Util.formatNumber(renglon.getMontoMovimiento())).append("</td>");
            datosDetalleAdecuacion.append("</tr>");
        }
        datosDetalleAdecuacion.append("</tbody>");
        for (UsuarioNotificado usuario : usuarios) {
            String mailBody = mailTemplate.replaceAll("[#]FUNCIONARIO[#]", usuario.getNombre() == null ? "Estimado Usuario" : usuario.getNombre()).replaceAll("[#]PUESTO[#]", usuario.getPuesto() == null ? " - " : usuario.getPuesto()).replaceAll("[#]ENCABEZADO_ADECUACION[#]", datosEncabezadoAdecuacion.toString()).replaceAll("[#]DETALLE_ADECUACION[#]", datosDetalleAdecuacion.toString()).replaceAll("[#]FECHA[#]", Util.getTodayESMX()).replaceAll("[#]URL_SIPLAN[#]", ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SIPLAN"));
            correos.add(new CorreoNotificacion(mailBody, usuario.getCorreo()));
        }
        return correos;
    }

    public List<CorreoNotificacion> generateMailCaptura(Connection conn, AdecuacionResumen adecuacionNotificar, List<UsuarioNotificado> usuarios) throws Exception {
        return generateMail(conn, adecuacionNotificar, usuarios, NotificaAdecuacionMetasCliente.MAIL_TEMPLATE_CAPTURA);
    }

    public List<CorreoNotificacion> generateMailSIPLAN(Connection conn, AdecuacionResumen adecuacionNotificar, List<UsuarioNotificado> usuarios) throws Exception {
        return generateMail(conn, adecuacionNotificar, usuarios, NotificaAdecuacionMetasCliente.MAIL_TEMPLATE);
    }
}
