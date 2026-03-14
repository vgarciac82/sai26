package com.syc.adquisiciones.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import com.syc.adquisiciones.OperacionesCRUD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmpliacionesContratosManager implements OperacionesCRUD {

    private static Logger log = LoggerFactory.getLogger(AmpliacionesContratosManager.class);

    public boolean Creade(Map<String, String> map, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean Reade(Map<String, String> map, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean Update(Map<String, String> map, Connection conn) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "UPDATE dbo.mContratoAmpliacion SET mMontoNeto=" + map.get("mMontoNetoMod") + " WHERE cIdContratoDefinitivo='" + map.get("cIdContratoDef") + "' AND nIdConsecutivoAmpliacion=" + map.get("nIdConsecutivoAmpliacion");
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }

    public boolean Delete(Map<String, String> map, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
}
