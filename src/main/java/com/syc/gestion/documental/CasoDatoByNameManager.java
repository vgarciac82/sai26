package com.syc.gestion.documental;

//
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CasoDatoByNameManager implements GestionInterface {

    private static Logger log = LoggerFactory.getLogger(CasoDatoByNameManager.class.getName());

    public static String select(Connection conn, int id_caso, String tcv_nombre) throws SQLException {
        String v = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        log.debug("Object: " + String.valueOf("[CasoDatoByNameManager] idCaso=" + id_caso + ", tcvNombre=" + tcv_nombre));
        try {
            pstmnt = conn.prepareStatement("SELECT d.cd_valor  FROM cg_tipo_caso_variable v, cg_caso_dato d " + "WHERE  d.id_caso    = ? " + "AND    d.id_cd      = v.id_tcv " + "AND    v.id_tc      = d.id_tc " + "AND    v.tcv_nombre = ?");
            pstmnt.setInt(1, id_caso);
            pstmnt.setString(2, tcv_nombre);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                v = rs.getString("cd_valor");
                log.debug("Object: " + String.valueOf("[CasoDatoByNameManager] valor=" + v));
            } else
                log.debug("Object: " + String.valueOf("[CasoDatoByNameManager] variable no encontrada" + tcv_nombre));
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return v;
    }
}
