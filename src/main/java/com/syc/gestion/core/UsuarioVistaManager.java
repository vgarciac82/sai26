package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.syc.sai.contabilidad.utils.db.CloseObject;

public class UsuarioVistaManager {

	public static List<UnidadEjecutora> getVistasUsuario(Connection conn, String uLogin, String modulo) throws Exception {
		String query =   "SELECT vista.ur, "
			+"       Isnull(ur.d_descripcion, '') AS descripcion "
			+"FROM   dbo.tvistasur vista WITH(nolock) "
			+"       LEFT OUTER JOIN tcatunidadresponsable ur WITH(nolock) "
			+"                    ON vista.ur = ur.cunidadresponsable "
			+"WHERE  modulo = ? "
			+"       AND vista.usuario = ? "
			+"UNION "
			+"SELECT ur.cunidadresponsable AS ur, "
			+"       ur.d_descripcion      AS descripcion "
			+"FROM   dbo.cg_cat_empleado empleado WITH(nolock) "
			+"       INNER JOIN dbo.tcatalogounidadresponsable area_ur WITH(nolock) "
			+"               ON empleado.id_area = area_ur.id_area "
			+"       INNER JOIN dbo.tcatunidadresponsable ur WITH(nolock) "
			+"               ON area_ur.cunidadresponsable = ur.cunidadresponsable "
			+"WHERE  ce_os_responsable = ? ";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<UnidadEjecutora> vistas = null;
		
		try {

			ps = conn.prepareStatement(query);
			ps.setString(1, modulo);
			ps.setString(2, uLogin);
			ps.setString(3, uLogin);
			
			rs = ps.executeQuery();

			while (rs.next()) {
				
				if (vistas == null)
					vistas = new ArrayList<UnidadEjecutora>();
				
				UnidadEjecutora ue = new UnidadEjecutora();
				ue.setUe(rs.getString("ur"));
				ue.setDescripcion(rs.getString("descripcion"));
				
				vistas.add(ue);
				
			}

			return vistas;

		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}
	}

}
