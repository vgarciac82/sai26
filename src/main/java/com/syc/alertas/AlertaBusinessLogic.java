package com.syc.alertas;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertaBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(AlertaBusinessLogic.class);

    public AlertaBusinessLogic() {
        super();
    }

    public AlertaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void buscaCasosExpirados() throws Exception {
        log.info("Buscando Casos Expirados ...");
        Map<String, List<Integer>> casosProcesar = AlertaManager.procesaVencidos(this);
        for (Iterator<String> i = casosProcesar.keySet().iterator(); i.hasNext(); ) {
            Connection conn = null;
            try {
                conn = getConnection();
                String key = i.next();
                List<Integer> casoList = casosProcesar.get(key);
                AlertaManager.procesoAlerta(conn, casoList.get(0), casoList.get(1), casoList.get(2), casoList.get(3));
                conn.commit();
            } catch (Exception e) {
                log.error("No fue posible procesar la alarma. " + e, e);
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            } finally {
                CloseObject.closeObject(conn);
                log.info("Termina Proceso de Casos Expirados ...");
            }
        }
    }

    public boolean realizarProceso() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return AlertaManager.realizarProceso(conn);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
