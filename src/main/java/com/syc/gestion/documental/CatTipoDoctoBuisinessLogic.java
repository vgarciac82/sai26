package com.syc.gestion.documental;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatTipoDoctoBuisinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CatTipoDoctoBuisinessLogic.class);

    public CatTipoDoctoBuisinessLogic(String jniName) {
        super.init(jniName);
    }

    public List consultaCatTipoDocto() {
        Connection conn = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        List l = new ArrayList();
        try {
            conn = getConnection();
            pstmnt = conn.prepareStatement("SELECT * FROM cat_tipo_documento ORDER BY td_descripcion");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CatTipoDocumento td = new CatTipoDocumento();
                td.setTd_id(rs.getInt("td_id"));
                td.setTd_descripcion(rs.getString("td_descripcion"));
                l.add(td);
            }
        } catch (SQLException exc) {
            log.error("Obteniendo lista de tipos de documento", exc);
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
