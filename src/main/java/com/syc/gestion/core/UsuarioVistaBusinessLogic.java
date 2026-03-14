package com.syc.gestion.core;

import java.sql.Connection;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class UsuarioVistaBusinessLogic extends DataSourceManager {

    public UsuarioVistaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public List<UnidadEjecutora> getVistasUsuario(String uLogin, String modulo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return UsuarioVistaManager.getVistasUsuario(conn, uLogin, modulo);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
