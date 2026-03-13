package com.syc.pasivoscontingentes;

import java.sql.Connection;
import com.syc.pasivoscontingentes.ProcesaNotificacionManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class ProcesaNotificacionBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ProcesaNotificacionBusinessLogic.class);

    public void enviaCorreoLaudos() throws Exception {
        Connection conn = null;
        conn = getConnection();
        ProcesaNotificacionManager process = new ProcesaNotificacionManager();
        int total = process.validaInfo(conn);
        if (total > 0) {
            process.envioAlertas(conn);
        }
    }
}
