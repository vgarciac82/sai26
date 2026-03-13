package com.axtel.sisecop.services;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import com.axtel.sisecop.entities.NotificacionProyectoPendiente;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationGeneratorService extends DataSourceManager {

    private static final Logger log = LogManager.getLogger(NotificationGeneratorService.class);

    private static final VelocityEngine velocityEngine;

    private static final Template templateRevision;

    private static final Template templateCorreccion;

    private static final Template templateDictamen;

    private static String AUTH_LOGIN;

    private static String AUTH_CC;

    private ConfiguraAplicativoBusinessLogic systemConfig;

    static {
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine = new VelocityEngine(properties);
        velocityEngine.init();
        templateRevision = velocityEngine.getTemplate("templates/revision_proyecto.vm");
        templateCorreccion = velocityEngine.getTemplate("templates/correccion_proyecto.vm");
        templateDictamen = velocityEngine.getTemplate("templates/dictamen_proyecto.vm");
    }

    public NotificationGeneratorService() {
        super.init();
        systemConfig = new ConfiguraAplicativoBusinessLogic(null);
        loadConfig();
    }

    public NotificationGeneratorService(String jniName) {
        super.init(jniName);
        systemConfig = new ConfiguraAplicativoBusinessLogic(jniName);
        loadConfig();
    }

    private final void loadConfig() {
        NotificationGeneratorService.AUTH_LOGIN = systemConfig.getSystemSetting("AUTH_LOGIN");
        NotificationGeneratorService.AUTH_CC = systemConfig.getSystemSetting("AUTH_CC");
    }

    public void sendNotificacion(ProyectoServicio proyectoServicio) {
        NotificacionProyectoPendiente notificacion = new NotificacionProyectoPendiente();
        notificacion.setTitulo(proyectoServicio.getServicioTitulo());
        notificacion.setFolio(proyectoServicio.getServicioFolioPre() + "/" + proyectoServicio.getServicioFolioAnio() + "/" + String.format("%04d", proyectoServicio.getServicioFolioNum()));
        notificacion.setFecha(Util.getTodayESMX());
        Connection conn = null;
        try {
            conn = getConnection();
            String body = "";
            if ("PROJECT_AUTHORIZER".equals(ProyectoServicioService.PROCESS_RESPONSIBLES.get(proyectoServicio.getEstatus().getEstatusId()))) {
                Usuario u = new Usuario(AUTH_LOGIN);
                Empleado e = new Empleado();
                e.setClaveUsuario(u.getLogin());
                u = UsuarioManager.select(conn, u);
                e = EmpleadoManager.select(conn, e);
                notificacion.setFuncionario("C." + u.getNombre());
                notificacion.setCargo(e.getCargo());
                body = generateSolicitudRevision(notificacion);
                AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Revision de proyecto  " + notificacion.getFolio() + " pendiente", u.getU_email(), body);
                if (StringUtils.isNotBlank(AUTH_CC)) {
                    String[] to = AUTH_CC.split(";");
                    for (String login : to) {
                        u.setLogin(login);
                        u = UsuarioManager.select(conn, u);
                        AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Revision de proyecto  " + notificacion.getFolio() + " pendiente", u.getU_email(), body);
                    }
                }
            } else {
                Usuario u = new Usuario(proyectoServicio.getLoginUsuario());
                Empleado e = new Empleado();
                e.setClaveUsuario(u.getLogin());
                u = UsuarioManager.select(conn, u);
                e = EmpleadoManager.select(conn, e);
                notificacion.setFuncionario("C." + u.getNombre());
                if (e != null && StringUtils.isNotBlank(e.getCargo()))
                    notificacion.setCargo(e.getCargo());
                else
                    notificacion.setCargo("");
                if ("PROJECT_REGISTER".equals(ProyectoServicioService.PROCESS_RESPONSIBLES.get(proyectoServicio.getEstatus().getEstatusId()))) {
                    notificacion.setMotivoRechazo(proyectoServicio.getObservaciones());
                    body = generateSolicitudCorreccion(notificacion);
                    AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Correccion de proyecto  " + notificacion.getFolio() + "", u.getU_email(), body);
                } else if ("PROJECT_CONSULT".equals(ProyectoServicioService.PROCESS_RESPONSIBLES.get(proyectoServicio.getEstatus().getEstatusId()))) {
                    body = generateNotificaDictamen(notificacion);
                    AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Dictamen de proyecto " + notificacion.getFolio() + " emitido.", u.getU_email(), body);
                }
            }
        } catch (Exception e) {
            log.warn("No fue posible enviar la notificacion: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String generateSolicitudRevision(NotificacionProyectoPendiente notificacion) {
        return generateNotification(templateRevision, notificacion, false);
    }

    public String generateSolicitudCorreccion(NotificacionProyectoPendiente notificacion) {
        return generateNotification(templateCorreccion, notificacion, true);
    }

    public String generateNotificaDictamen(NotificacionProyectoPendiente notificacion) {
        return generateNotification(templateDictamen, notificacion, false);
    }

    private String generateNotification(Template template, NotificacionProyectoPendiente notificacion, boolean instrucciones) {
        Context context = new VelocityContext();
        context.put("funcionario", notificacion.getFuncionario());
        context.put("cargo", notificacion.getCargo());
        context.put("folio", notificacion.getFolio());
        context.put("titulo", notificacion.getTitulo());
        context.put("fecha", notificacion.getFecha());
        if (instrucciones)
            context.put("motivo", notificacion.getMotivoRechazo());
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}
