/**
 */
package com.syc.sai.procesosAutomaticos.core;

import java.sql.Connection;
import java.util.Date;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vicente Garcia Carrillo
 */
public class ProcesoAdjuntaBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ProcesoAdjuntaBusinessLogic.class);

    /**
     * Acceso a DB
     */
    private String jniNAme;

    private boolean standAlone = false;

    /**
     * Crea una nueva instancia del objeto
     */
    public ProcesoAdjuntaBusinessLogic() {
        setStandAlone(true);
    }

    /**
     * Crea una nueva instancia del objeto
     */
    public ProcesoAdjuntaBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniNAme = jniName;
    }

    /**
     * Actualiza el resultado del proceso.
     *
     * @param pa
     *            Proceso
     * @throws Exception
     */
    public void actualizaResultado(ProcesoAdjunta pa) throws Exception {
        Connection conn = null;
        try {
            if (isStandAlone())
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            ProcesoAdjuntaManager.actualizaResultado(conn, pa);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    /**
     * Obtiene el siguiente ID de proceso.
     *
     * @return Siguiente id de proceso.
     * @throws Exception
     */
    private int generaID() throws Exception {
        int id = CFSequenceManager.getInstance(jniNAme).nextVal("ADJUNTACLC");
        return id;
    }

    /**
     * Instancia un objeto <code>ProcesoAdjunta</code> en el estatus inicial.
     *
     * @param uLogin
     *            login del usuario
     * @param UR
     *            Unidad Ejecutora del usuario
     * @return Instancia con el estatus inicial de la carga
     * @throws Exception
     */
    public ProcesoAdjunta instanceObject(String uLogin, String UR) throws Exception {
        Connection conn = null;
        try {
            if (isStandAlone())
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            int idProc = generaID();
            ProcesoAdjunta pa = new ProcesoAdjunta(new Date(), 0, UR + "CLC" + "/" + idProc, "", uLogin, UR);
            ProcesoAdjuntaManager.insertaProceso(conn, pa);
            log.trace("Object: {}", "Se genero correctamente el proceso: " + pa);
            conn.commit();
            return pa;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean isStandAlone() {
        return standAlone;
    }

    public void setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
    }
}
