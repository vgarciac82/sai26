package com.syc.sai.ingresos.core;

import java.sql.Connection;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Logica de negocios para la consolidacion de relaciones de gastos. <br>
 * Modo de uso: <br>
 * <code>
 * ConsolidacionRGBusinessLogic consolidacionBL = new ConsolidacionRGBusinessLogic("jdbc/gestion");
 * <br>
 * consolidacionBL.aplicaConsolidacionRelacionGastos("G33140801095345");
 * </code>
 *
 * @author Vicente Garcia Carrillo
 */
public class ConsolidacionRGBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ConsolidacionRGBusinessLogic.class);

    /**
     * Crea una nueva instancia.
     *
     * @param jniName
     */
    public ConsolidacionRGBusinessLogic(String jniName) {
        super.init(jniName);
    }

    /**
     * Indica si es una integracion de relaciones de gastos a proveedor.
     *
     * @param nFolioRG
     *            Folio de integracion (campo sauxiliarcomodin) P.E.
     *            G33140801095345
     * @return true si es una relacion de gastos a proveedor o subsidios.
     * @throws Exception
     */
    public boolean esIntegracionProveedor(String nFolioRG) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return ConsolidacionRGManager.esIntegracionProveedor(conn, nFolioRG);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Aplica contablemente una integracion de relacion de gastos.
     *
     * @param nFolioRG
     *            Folio de integracion (campo sauxiliarcomodin) P.E.
     *            G33140801095345
     * @throws Exception
     */
    public void aplicaConsolidacionRelacionGastos(String nFolioRG) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, nFolioRG, "");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
