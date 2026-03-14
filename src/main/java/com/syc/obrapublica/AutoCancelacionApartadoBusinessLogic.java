package com.syc.obrapublica;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.utils.mail.MailSender;
import com.syc.utils.mail.MessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AutoCancelacionApartadoBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(AutoCancelacionApartadoBusinessLogic.class);

    public boolean correoProduccion = false;

    public AutoCancelacionApartadoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void enviaAdvertenciaProximaCancelacion() {
        try {
            AdvertenciaCancelacionComposser advertencias = new AdvertenciaCancelacionComposser();
            MailSender.sendMail("mop", advertencias);
        } catch (Exception e) {
            log.error("Error de envio de advertencias", e);
        }
    }

    // IRD RO-0003 Se agrega parámetro MotivoCancela y listaFolios
    public void enviaAdvertenciaResponsableProximaCancelacion(String listaFolios, String motivoCancela) {
        try {
            AdvertenciaCancelacionComposser advertenciasComposser = new AdvertenciaCancelacionComposser();
            advertenciasComposser.correoProduccion = this.correoProduccion;
            List<AdvertenciaCancelacionCorreo> advertencias = advertenciasComposser.composeMessageList(listaFolios, motivoCancela);
            for (Iterator<AdvertenciaCancelacionCorreo> i = advertencias.iterator(); i.hasNext(); ) {
                AdvertenciaCancelacionCorreo advertencia = i.next();
                try {
                    if ("".equalsIgnoreCase(listaFolios)) {
                        MailSender.sendMail("mop", advertencia.getTo(), advertencia.getMessage(), "Apartado proximo a vencer");
                    } else {
                        MailSender.sendMail("mop", advertencia.getTo(), advertencia.getMessage(), "Contrato Cancelado");
                    }
                } catch (Exception e) {
                    log.error("Error enviando correo de advertencia " + e, e);
                }
            }
        } catch (Exception e) {
            log.error("Error de envio de advertencias", e);
        }
    }

    public void revisaTiempoLimite() {
        Connection conn = null;
        try {
            conn = getConnection();
            String mailBdy = AutoCancelacionApartadoManager.revisaTiempoLimite(conn);
            conn.commit();
            if (!"".equals(mailBdy)) {
                try {
                    CancelacionApartadoMessageComposer msgComposser = new CancelacionApartadoMessageComposer();
                    msgComposser.setMessage(mailBdy);
                    MailSender.sendMail("mop", msgComposser);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("No se logro realizar el rollback en la conexion." + e2, e2);
                }
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando conexion a la DB." + e2, e2);
                }
        }
    }

    private class CancelacionApartadoMessageComposer implements MessageComposer {

        String message;

        public String composeMessage() throws Exception {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
