package com.syc.contable.core;

import java.util.Iterator;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.utils.mail.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AutoEnviaCorreoCompromisosBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(AutoEnviaCorreoCompromisosBusinessLogic.class);

    ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    public boolean ambienteDesarrollo = cabl.getSystemSetting("AMBIENTE_DESARROLLO") == null ? false : Boolean.valueOf(cabl.getSystemSetting("AMBIENTE_DESARROLLO"));

    public AutoEnviaCorreoCompromisosBusinessLogic(String jniName) {
        super.init(jniName);
        log.debug("Auto Envia Correos Compromisos Business Logic");
    }

    public AutoEnviaCorreoCompromisosBusinessLogic() {
        super.init();
        log.debug("Auto Envia Correos Compromisos Business Logic");
    }

    public void enviaAlertasCompromisosPendientes() {
        try {
            AutoEnviaCorreoCompromisosComposser alertasComposser = new AutoEnviaCorreoCompromisosComposser();
            alertasComposser.ambienteDesarrollo = this.ambienteDesarrollo;
            List<AlertaCorreoCompromiso> advertencias = alertasComposser.listaCorreoCompromisos();
            for (Iterator<AlertaCorreoCompromiso> i = advertencias.iterator(); i.hasNext(); ) {
                AlertaCorreoCompromiso advertencia = i.next();
                try {
                    MailSender.enviaCorreoCNF(advertencia.getTo(), advertencia.getMessage(), "Compromiso Pendiente");
                } catch (Exception e) {
                    log.error("Error enviando correo de advertencia " + e, e);
                }
            }
        } catch (Exception e) {
            log.error("Error de envio de advertencias", e);
        }
    }
}
