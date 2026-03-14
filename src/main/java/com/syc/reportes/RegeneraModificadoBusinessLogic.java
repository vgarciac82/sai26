package com.syc.reportes;

import java.sql.Connection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.reportes.core.RegeneraModificadoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegeneraModificadoBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(RegeneraModificadoBusinessLogic.class);

    public RegeneraModificadoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public String RegeneraModificado(HttpServletRequest req, HttpServletResponse resp) {
        Connection conn = null;
        String msg = "El modificado se regenero correctamente.";
        try {
            conn = getConnection();
            int mes = Integer.parseInt(req.getParameter("mesMod"));
            int ejercicioFiscal = Integer.parseInt(req.getParameter("ejercicioFiscal"));
            RegeneraModificadoManager.limpiaEdoEjercicio(conn, mes, ejercicioFiscal);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.toString();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return msg;
    }
}
