package com.syc.sai.tesoreria.retenciones;

import java.sql.Connection;
import java.util.List;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.tesoreria.retenciones.core.Retencion;
import java.util.Base64;

public class RetencionBusinessLogic extends DataSourceManager {

    public RetencionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public String obtieneTipoPersona(String cRFC) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return RetencionTipoPersonaManager.obtieneTipoPersona(conn, cRFC);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public List<Retencion> obtieneRetenciones(String tipoPersona, String partidas, String cc, String tipoPago, String folioPago) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return RetencionTipoPersonaManager.obtieneRetenciones(conn, tipoPersona, partidas, cc, tipoPago, folioPago);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
