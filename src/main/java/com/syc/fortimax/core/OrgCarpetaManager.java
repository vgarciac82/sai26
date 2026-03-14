package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class OrgCarpetaManager {

    public static int insert(Connection conn, OrgCarpeta oc) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO imx_org_carpeta (titulo_aplicacion, id_gabinete, " + "id_carpeta_hija, id_carpeta_padre, nombre_hija) VALUES (?, ?, ?, ?, ?)");
            pstmnt.setString(1, oc.getTituloAplicacion());
            pstmnt.setInt(2, oc.getIdGabinete());
            pstmnt.setInt(3, oc.getIdCarpetaHija());
            pstmnt.setInt(4, oc.getIdCarpetaPadre());
            pstmnt.setString(5, oc.getNombreHija());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
}
