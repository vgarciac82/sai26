package com.axtel.contratos.services;

import java.sql.Connection;
import com.axtel.contratos.repositories.CompromisoPagoDirectoEncabezadoManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompromisoPagoDirectoEncabezadoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CompromisoPagoDirectoEncabezadoBusinessLogic.class);

    public CompromisoPagoDirectoEncabezadoBusinessLogic(String jndiName) {
        super.init(jndiName);
    }

    public void insertaPagoDesdeSuficiencia(Caso c, Usuario user, String suficiencia) throws Exception {
        log.info("Object: {}", "Insertando pago directo desde suficiencia " + suficiencia);
        Connection conn = null;
        try {
            conn = getConnection();
            CompromisoPagoDirectoEncabezadoManager.insertaPagoDesdeSuficiencia(conn, c, user, suficiencia);
            conn.commit();
        } catch (Exception e) {
            Util.rollback(conn);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
