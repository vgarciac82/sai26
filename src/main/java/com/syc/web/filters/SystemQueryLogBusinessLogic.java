package com.syc.web.filters;

import java.sql.Connection;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SystemQueryLogBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(SystemQueryLogBusinessLogic.class);

    public SystemQueryLogBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public synchronized boolean insertLog(HttpServletRequest request) {
        boolean correcto = false;
        Connection conn = null;
        try {
            conn = getConnection();
            SystemQueryLog sql = SystemQueryLog.instanceFromRequest(request);
            SystemQueryLogManager.insertLog(conn, sql);
            conn.commit();
            correcto = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return correcto;
    }
}
