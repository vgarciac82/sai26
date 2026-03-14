package com.syc.fortimax.core;

import java.sql.Connection;
import java.util.List;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ExpedientBussinessLogic extends DataSourceManager {

    Caso caso;

    public Caso getCaso() {
        return caso;
    }

    public void setCaso(Caso caso) {
        this.caso = caso;
    }

    public ExpedientBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public List<ExpedientNode> getExpedientNodes() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return ExpedientManager.getExpedientNodes(conn, getCaso().getTipoCaso().getGavetaAsociada(), getCaso().getIdGabinete());
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
