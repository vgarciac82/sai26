package com.syc.sai.procesos;

import java.sql.Connection;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProinproApartadoBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(ProinproApartadoBusinessLogic.class);

    public ProinproApartadoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void revisaTiempoLimite(long maxTiempoApartado) {
        Connection conn = null;
        try {
            conn = getConnection();
            ProinproApartadoManager.revisaTiempoLimite(conn, maxTiempoApartado);
            conn.commit();
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
}
