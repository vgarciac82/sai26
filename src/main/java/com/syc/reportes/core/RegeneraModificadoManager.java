package com.syc.reportes.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class RegeneraModificadoManager {

    public static void limpiaEdoEjercicio(Connection conn, int mesIni, int anio) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{CALL dbo.sp_OriginalModificadoSolicitado ( ?, ? ) }";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, anio);
            cs.setInt(2, mesIni);
            rs = cs.executeQuery();
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }
}
