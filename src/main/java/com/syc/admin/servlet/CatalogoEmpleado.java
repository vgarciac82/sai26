package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoEmpleado {

	public static int delete(Connection conn, int id_empleado) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_cat_empleado WHERE id_empleado = ?");

			pstmnt.setInt(1, id_empleado);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	
	public static int insert(Connection conn, CatEmpleado ce) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn
				.prepareStatement("INSERT INTO cg_cat_empleado (id_empleado, ce_nombre_completo, ce_ap_paterno, ce_ap_materno, ce_os_responsable, salutacion, tipo_rem_des) VALUES (?, ?, ?, ?, ?, ?, ?)");

			
			pstmnt.setInt(1, ce.getId_empleado());
			pstmnt.setString(2, ce.getCe_nombre_completo());
			pstmnt.setString(3, ce.getCe_ap_paterno());
			pstmnt.setString(4, ce.getCe_ap_materno());
			pstmnt.setString(5, ce.getCe_os_responsable());
			pstmnt.setString(6, ce.getSalutacion());
			pstmnt.setString(7, ce.getTipo_rem_des());

			
			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static String select(Connection conn, int id_empleado) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String ru_login = null;

		try {
			pstmnt = conn.prepareStatement("SELECT id_empleado FROM cg_cat_empleado WHERE id_empleado = ?");

			pstmnt.setInt(1, id_empleado);
			
			rs = pstmnt.executeQuery();
			
			if (rs.next())
				ru_login = rs.getString(1);
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return ru_login;
	}

	public static CatEmpleado select(Connection conn, CatEmpleado ce) throws SQLException {

		CatEmpleado ru = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (ce.getId_empleado() != -1) {
				where.append(token + "id_empleado = ?");
				token = " AND ";
			}

			if (ce.getCe_nombre_completo() != null) {
				where.append(token + "ce_nombre_completo = ?");
				token = " AND ";
			}

			if (ce.getCe_ap_paterno() != null) {
				where.append(token + "ce_ap_paterno = ?");
				token = " AND ";
			}

			if (ce.getCe_ap_materno() != null) {
				where.append(token + "ce_ap_materno = ?");
				token = " AND ";
			}
			
			if (ce.getCe_os_responsable() != null) {
				where.append(token + "ce_os_responsable = ?");
				token = " AND ";
			}
			
			if (ce.getSalutacion() != null){
				where.append(token + "salutacion = ?");
				token = " AND ";
			}
			
			if(ce.getTipo_rem_des() != null){
				where.append(token + "tipo_rem_des = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_empleado " + where.toString());

			int i = 1;
			if (ce.getId_empleado() != -1)
				pstmnt.setInt(i++, ce.getId_empleado());

			if (ce.getCe_nombre_completo() != null)
				pstmnt.setString(i++, ce.getCe_nombre_completo());

			if (ce.getCe_ap_paterno() != null)
				pstmnt.setString(i++, ce.getCe_ap_paterno());

			if (ce.getCe_ap_materno() != null)
				pstmnt.setString(i++, ce.getCe_ap_materno());
			
			if (ce.getCe_os_responsable()  != null)
				pstmnt.setString(i++, ce.getCe_os_responsable());
			
			if (ce.getSalutacion() != null)
				pstmnt.setString(i++, ce.getSalutacion());
			
			if(ce.getTipo_rem_des() != null)
				pstmnt.setString(i++, ce.getTipo_rem_des());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				ru = new CatEmpleado();

				ru.setId_empleado(rs.getInt("id_empleado"));
				ru.setCe_nombre_completo(rs.getString("ce_nombre_completo"));
				ru.setCe_ap_paterno(rs.getString("ce_ap_paterno"));
				ru.setCe_ap_materno(rs.getString("ce_ap_materno"));
				ru.setCe_os_responsable(rs.getString("ce_os_responsable"));
				ru.setSalutacion(rs.getString("salutacion"));
				ru.setTipo_rem_des(rs.getString("tipo_rem_des"));
				
				//ru.setPropiedades(CatalogoEmpleado.select(conn, ce.getLogin()));
				//ru.setGrupos(UsuarioGrupoManager.selectGrupos(conn, ru.getLogin()));
				//ru.setRoles(UsuarioRoleManager.selectRoles(conn, ru.getLogin()));
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return ru;
	}

	public static List selectAll(Connection conn) throws SQLException {

		List usrList = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_empleado ORDER BY ce_nombre_completo");

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				CatEmpleado ce = new CatEmpleado();

				ce.setId_empleado(rs.getInt("id_empleado"));
				ce.setCe_nombre_completo(rs.getString("ce_nombre_completo"));
				ce.setCe_ap_paterno(rs.getString("ce_ap_paterno"));
				ce.setCe_ap_materno(rs.getString("ce_ap_materno"));
				ce.setCe_os_responsable(rs.getString("ce_os_responsable"));
				ce.setSalutacion(rs.getString("salutacion"));
				ce.setTipo_rem_des(rs.getString("tipo_rem_des"));
				
				//ce.setPropiedades(UsuarioPropiedadesManager.select(conn, ce.getLogin()));
				//u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
				//u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));

				usrList.add(ce);
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return usrList;
	}

	public static int update(Connection conn, CatEmpleado ce) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		//Statement st = null;
		try {
			//pstmnt = conn.prepareStatement("UPDATE cg_cat_empleado SET id_ce = ?,ce_nombre_completo = ?"
				//	+ ", ce_ap_paterno = ?, ce_ap_materno = ?, ce_os_responsable = ? WHERE id_ce = ?");
				
			pstmnt = conn.prepareStatement("UPDATE cg_cat_empleado SET ce_nombre_completo = ?, ce_ap_paterno = ?, ce_ap_materno = ?, ce_os_responsable = ?, salutacion = ?, tipo_rem_des = ? WHERE id_empleado = ?");
			
			pstmnt.setString(1, ce.getCe_nombre_completo());
			pstmnt.setString(2, ce.getCe_ap_paterno());
			pstmnt.setString(3, ce.getCe_ap_materno());
			pstmnt.setString(4, ce.getCe_os_responsable());
			pstmnt.setString(5, ce.getSalutacion());
			pstmnt.setString(6, ce.getTipo_rem_des());
			pstmnt.setInt(7, ce.getId_empleado());
			
			retval = pstmnt.executeUpdate();
			
			
			
			//pstmnt.setString(1, "'"+ce.getCe_nombre_completo()+"'");
			//pstmnt.setString(2, "'"+ce.getCe_ap_paterno()+"'");
			//pstmnt.setString(3, "'"+ce.getCe_ap_materno()+"'");
			//pstmnt.setString(4, "'"+ce.getCe_os_responsable()+"'");
			//pstmnt.setInt(5, ce.getId_ce());
			
			//String id = Integer.toString(ce.getId_ce());
			//String nombre = ce.getCe_nombre_completo();
			//String ap = ce.getCe_ap_paterno();
			//String am = ce.getCe_ap_materno();
			//String respo = ce.getCe_os_responsable();
			
			
			
			//String qry =  "UPDATE cg_cat_empleado SET ce_nombre_completo = '"+nombre
				//+ "', ce_ap_paterno = '"+ap+"', ce_ap_materno = '"+am+"', ce_os_responsable = '"+respo+"' WHERE id_ce ="+id; 
			//UsuarioPropiedadesManager.update(conn, ce.getPropiedades());
			// FIXME Se deben actualizar
			// UsuarioGrupoManager.update(conn, u.getGrupos());
			// UsuarioRoleManager.update(conn, u.getRoles());
			//boolean rs = st.execute(qry);
			//if(rs){conn.commit();
			//if(pstmnt.execute()){retval=1;}else{retval=0;}
			//}else{conn.rollback();}
			
			
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
}