package com.syc.sai.bitacora.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class BitacoraOperacionDoctosManager extends DataSourceManager {

    public static boolean insertaBitacora(Connection conn, String uLogin, String cModulo, int idTc, int nFolio, Integer idOperacion, String cLog) throws SQLException {
        PreparedStatement psInsertaBitacora = null;
        String queryInsert = "INSERT INTO tBitacoraOperacionDoctos(uLogin, cModulo, id_tc, nFolio, id_operacion, cLog) " + "VALUES (?, ?, ?, ?, ?, ?)";
        boolean insertado = false;
        try {
            psInsertaBitacora = conn.prepareStatement(queryInsert);
            psInsertaBitacora.setString(1, uLogin);
            psInsertaBitacora.setString(2, cModulo);
            psInsertaBitacora.setInt(3, idTc);
            psInsertaBitacora.setInt(4, nFolio);
            psInsertaBitacora.setInt(5, idOperacion);
            psInsertaBitacora.setString(6, cLog);
            psInsertaBitacora.executeUpdate();
            return insertado;
        } finally {
            CloseObject.closeObject(psInsertaBitacora);
        }
    }
}
