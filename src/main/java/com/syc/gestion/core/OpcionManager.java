package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class OpcionManager {

	// private static Logger log = Logger.getLogger(CasoOperacionManager.class);

	public static Vector selectByUser(Connection conn, String u_login,
			String o_estaenmenu, int id_producto) throws SQLException {

		Vector v = new Vector();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		StringBuffer where = new StringBuffer();
		String token = " AND ";

		try {
			if (o_estaenmenu != null) {
				where.append(token + "o_estaenmenu = ?");
				token = " AND ";
			}

			if (id_producto > 0) {
				where.append(token + "id_producto = ?");
				token = " AND ";
			}

			/*pstmnt = conn.prepareStatement("SELECT DISTINCT o.* " + "FROM   cg_opcion o" 
					+ ",      cg_role_opcion r" + ",      cg_usuario_role u "
					+ " WHERE  o.id_opcion = r.id_opcion "
					+ " AND    u.r_nombre  = r.r_nombre "
					+ " AND    u.u_login   = ? " 
					+ where.toString()
					+ " UNION SELECT o.id_opcion, o_descripcion, o_estaenmenu, " // se agregan las condiciones delegadas de administracion
					+ " o_action, o_target, o_id_parent, o_orden, 3 as id_producto, O_DELEGABLE "
					+ " FROM  cg_opcion o, cg_usuario_opcion_delegada od WHERE  o.id_opcion =  od.id_opcion "
					+ " AND   od.u_login   = ? "
					+ " ORDER BY o.o_orden");*/

			pstmnt = conn.prepareStatement("SELECT DISTINCT o.ID_OPCION, o.O_DESCRIPCION,o.O_ESTAENMENU, REPLACE(O_ACTION, '#DESC#', O_DESCRIPCION) as O_ACTION , o.O_TARGET, o.O_ID_PARENT, o.O_ORDEN, o.ID_PRODUCTO, o.O_DELEGABLE " 
				+ " FROM   cg_opcion o" 
				+ ", cg_role_opcion r" 
				+ ", cg_usuario_role u "
				+ " WHERE  o.id_opcion = r.id_opcion "
				+ " AND    u.r_nombre  = r.r_nombre "
				+ " AND    u.u_login   = ? " 
				+ where.toString()
				+ " UNION SELECT o.id_opcion, o_descripcion, o_estaenmenu, " // se agregan las condiciones delegadas de administracion
				+ " REPLACE(O_ACTION, '#DESC#', O_DESCRIPCION) as O_ACTION , o_target, o_id_parent, o_orden, 3 as id_producto, O_DELEGABLE "
				+ " FROM  cg_opcion o, cg_usuario_opcion_delegada od WHERE  o.id_opcion =  od.id_opcion "
				+ " AND   od.u_login   = ? "
				+ " ORDER BY o.o_orden");
			
			

			pstmnt.setString(1, u_login);
			
			pstmnt.setString(4, u_login);

			int i = 2;
			if (o_estaenmenu != null) {
				pstmnt.setString(i++, o_estaenmenu);
			}

			if (id_producto > 0) {
				pstmnt.setInt(i++, id_producto);
			}

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				Opcion lo = new Opcion();

				lo.setId_opcion(rs.getInt("id_opcion"));
				lo.setO_descripcion(rs.getString("o_descripcion"));
				lo.setO_estaenmenu(rs.getString("o_estaenmenu"));
				lo.setO_action(rs.getString("o_action"));
				lo.setO_target(rs.getString("o_target"));
			/* ********************************************************************
			* linea agregada para utilizar el campo o_orden de la tabla cg_opcion
			* 08/06/2012
			* Martha Aurora Sánchez Valdivieso
			*/
				lo.setO_orden(rs.getInt("o_orden"));
			// ********************************************************************
				lo.setO_id_parent(rs.getInt("o_id_parent"));
				v.add(lo);
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return v;
	}
	
}
