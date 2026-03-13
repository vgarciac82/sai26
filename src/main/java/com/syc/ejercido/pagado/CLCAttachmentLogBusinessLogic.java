package com.syc.ejercido.pagado;

import java.sql.Connection;
import com.syc.cfdi.utils.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLCAttachmentLogBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CLCAttachmentLogBusinessLogic.class);

    public CLCAttachmentLogBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public boolean insertLog(int clc, int folioPago, String tipoPago, char estatusPago, String logProc, String caNoContraRecibo, String nombreArchivo, String logInfo) {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            insertados = CLCAttachmentLogManager.insertLog(conn, nombreArchivo, clc, caNoContraRecibo, folioPago, tipoPago, estatusPago, logInfo);
            conn.commit();
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error insertando el log: " + e, e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Error realizando rollback: " + e2, e2);
                }
        } finally {
            try {
                CloseObject.closeObject(conn, true);
            } catch (Exception e) {
                log.error("Error cerrando conexion a DB: " + e, e);
            }
        }
        return insertados > 0;
    }
}
