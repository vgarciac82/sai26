package com.syc.gestion.documental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatLDistribucionBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CatLDistribucionBusinessLogic.class);

    public CatLDistribucionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public List consultaCatLDistribucion(String u_login) {
        Connection conn = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        List l = new ArrayList();
        try {
            conn = getConnection();
            pstmnt = conn.prepareStatement("SELECT * FROM cat_ldistribucion WHERE u_login = ? ORDER BY ld_nombre");
            pstmnt.setString(1, u_login);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CatLDistribucion ld = new CatLDistribucion();
                ld.setId_ldistribucion(rs.getInt("id_ldistribucion"));
                ld.setLd_nombre(rs.getString("ld_nombre"));
                l.add(ld);
            }
        } catch (SQLException exc) {
            log.error("Obteniendo lista de distribucion", exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            rs = null;
            pstmnt = null;
        }
        return l;
    }
}
