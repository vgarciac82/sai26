package com.syc.gestion.core;

import java.sql.Connection;
import java.util.List;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListadoCorreosPendientesBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ListadoCorreosPendientesBusinessLogic.class);

    private String jniName;

    private Usuario usuario;

    public ListadoCorreosPendientesBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public String getJniName() {
        return jniName;
    }

    public int insertaNotificacionPendiente(CorreosPendientesBean cpb) {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            insertados = ListadoCorreosPendientesManager.insert(conn, cpb);
            conn.commit();
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas con rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return insertados;
    }

    public int insertaNotificacionesFactura(List<String> facturasError) {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(getJniName());
        String inicioCorreo = "<b>Atenci&oacute;n</b><br><br>Se hace de su conocimiento que el usuario <b>" + (getUsuario() == null ? "Desconocido" : getUsuario().getNombre()) + "</b> de la Unidad Responsable: <b>" + (getUsuario() == null ? "Desconocido" : getUsuario().getU_UR()) + "</b> intento realizar una solicitud de pago con facturas no validas ante el SAT.<br><br> Se notifica a continuaci&oacute;n la lista de facturas y la respuesta del servicio de validacion expuesto por el SAT: <br><br>";
        String mailBody = "";
        String to = cabl.getSystemSetting("CORREO_NOTIFICACION_FACTURAS");
        String subject = "Facturas no validas por SAT";
        mailBody = "<ul>\n";
        for (int i = 0; i < facturasError.size(); i++) {
            mailBody += "<li>" + facturasError.get(i) + "</li>\n";
        }
        mailBody += "</ul>\n";
        mailBody += "<br><br>Atte. <b>Sistema de Administracion Integral SAI</b>";
        CorreosPendientesBean cpb = new CorreosPendientesBean(to, inicioCorreo + mailBody, subject, "");
        int nInsertados = insertaNotificacionPendiente(cpb);
        log.trace("Notificacion insertada exitosamente. Para[" + to + "] Correo[" + mailBody + "] Asunto: " + subject);
        return nInsertados;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // private CorreosPendientesBean notificacionABean(String )
    public void setJniName(String jniName) {
        this.jniName = jniName;
    }

    public int insertaNotificacionesFacturaRepetida(List<String> erroresFactExistenteEFA) {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(getJniName());
        String inicioCorreo = "<b>Atenci&oacute;n</b><br><br>Se hace de su conocimiento que el usuario <b>" + (getUsuario() == null ? "Desconocido" : getUsuario().getNombre()) + "</b> de la Unidad Responsable: <b>" + (getUsuario() == null ? "Desconocido" : getUsuario().getU_UR()) + "</b> intent&oacute; realizar una solicitud de pago con facturas utilizadas en a&ntilde;os anteriores.<br><br> Se notifica a continuaci&oacute;n la lista de facturas: <br><br>";
        String mailBody = "";
        String to = cabl.getSystemSetting("CORREO_NOTIFICACION_FACTURAS");
        String subject = "Facturas utilizadas en otros ejercicios fiscales";
        mailBody = "<ul>\n";
        for (int i = 0; i < erroresFactExistenteEFA.size(); i++) {
            mailBody += "<li>" + erroresFactExistenteEFA.get(i) + "</li>\n";
        }
        mailBody += "</ul>\n";
        mailBody += "<br><br>Atte. <b>Sistema de Administracion Integral SAI</b>";
        CorreosPendientesBean cpb = new CorreosPendientesBean(to, inicioCorreo + mailBody, subject, "");
        int nInsertados = insertaNotificacionPendiente(cpb);
        log.trace("Notificacion insertada exitosamente. Para[" + to + "] Correo[" + mailBody + "] Asunto: " + subject);
        return nInsertados;
    }
}
