package com.syc.gestion.core;

import java.sql.Connection;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.core.UnidadEjecutoraManager;
import java.util.Base64;

public class UnidadEjecutoraBusinessLogic extends DataSourceManager {

    private String unidadEjecutora;

    public UnidadEjecutoraBusinessLogic(String jniName) {
        super.init();
    }

    public String getCentroContableUnidad() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return UnidadEjecutoraManager.selectCentroContableUE(conn, getUnidadEjecutora());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    /**
     * @return the unidadEjecutora
     */
    public String getUnidadEjecutora() {
        return unidadEjecutora;
    }

    public void setUnidadEjecutora(String unidadEjecutora) {
        this.unidadEjecutora = unidadEjecutora;
    }
}
