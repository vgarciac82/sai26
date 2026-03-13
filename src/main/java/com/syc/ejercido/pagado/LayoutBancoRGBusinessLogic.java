package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.ejercido.pagado.core.LayoutBancoRGManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayoutBancoRGBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(Anexo1BusinessLogic.class);

    private String folioGenerator;

    public LayoutBancoRGBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public LayoutBancoRGBusinessLogic(String jniName, String folioGenerator) {
        super.init(jniName);
        this.folioGenerator = folioGenerator;
    }

    public ArrayList<String> ArmaLayoutBancoRG(String nFolios, String sTipo, boolean tipoBanorte, String sCuentaLayout) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = LayoutBancoRGManager.ArmaLayoutBancoRG(conn, nFolios, sTipo, tipoBanorte, sCuentaLayout);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public void actualizaEnviadoSICOPCaja(String nFolios, String sTipo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            LayoutBancoRGManager.actualizaEnviadoSICOPCaja(conn, nFolios, sTipo);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2);
                }
            }
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
