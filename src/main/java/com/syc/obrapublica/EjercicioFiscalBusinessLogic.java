package com.syc.obrapublica;

import java.sql.Connection;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EjercicioFiscalBusinessLogic extends DataSourceManager {

    public EjercicioFiscalBusinessLogic() {
        super.init(GestionInterface.ATT_CONEXION);
    }

    public EjercicioFiscalBusinessLogic(String jniName) {
        super.init(jniName);
    }

    private static final Logger log = LoggerFactory.getLogger(EjercicioFiscalBusinessLogic.class);

    public EjercicioFiscal getEjercicioFiscalActivo() {
        EjercicioFiscal ejercicioActivo = null;
        Connection conn = null;
        try {
            conn = getConnection();
            ejercicioActivo = EjercicioFiscalManager.getEjercicioFiscalActivo(conn);
            return ejercicioActivo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (conn != null)
                try {
                    CloseObject.closeObject(conn, true);
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
        }
    }

    public static String getEjercicioFiscal() {
        EjercicioFiscalBusinessLogic efbl = null;
        try {
            efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
            return efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            efbl = null;
        }
    }
}
