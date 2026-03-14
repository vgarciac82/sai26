package com.syc.subejercicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class UsuarioUEManager {

    public static List<UsuarioCorreo> buscaUsuariosUE(Connection conn, String ue, String grupo) throws Exception {
        String query = "select ug.U_LOGIN, u.U_NOMBRE, empleado.ID_AREA, ur.cUnidadResponsable, u.U_EMAIL " + " from CG_USUARIO_GRUPO ug " + " left outer join CG_CAT_EMPLEADO empleado " + " on ug.U_LOGIN = empleado.CE_OS_RESPONSABLE " + " left outer join tCatalogoUnidadResponsable ur " + " on empleado.ID_AREA = ur.ID_AREA " + " inner join cg_usuario u " + " on u.U_LOGIN = ug.U_LOGIN " + " where (G_NOMBRE = ? or G_NOMBRE = 'x" + grupo + "x' ) " + " and cUnidadResponsable = ?" + " order by cUnidadResponsable";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<UsuarioCorreo> l = new ArrayList<UsuarioCorreo>();
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, grupo);
            ps.setString(2, ue);
            rs = ps.executeQuery();
            while (rs.next()) {
                UsuarioCorreo usuario = new UsuarioCorreo();
                usuario.setCorreo(rs.getString("U_EMAIL"));
                usuario.setNombreCompleto(rs.getString("U_NOMBRE"));
                usuario.setUe(ue);
                l.add(usuario);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }
}
