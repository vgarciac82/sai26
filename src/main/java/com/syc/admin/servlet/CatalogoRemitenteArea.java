package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoRemitenteArea {

	public static int delete(Connection conn, String cra_id_area) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_cat_remitente_area WHERE cra_id_area = ?");

			pstmnt.setString(1, cra_id_area);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	
	public static int insert(Connection conn, CatRemArea cra) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		try {
			String valor1 = "";
			valor1 = "SELECT Count(*) total FROM cg_cat_remitente_area" +
			" Where cra_id_area ='" + cra.getCra_id_area()+"'" +
			" AND cra_descripcion='" + cra.getCra_descripcion() +"'" +
			" AND tipo='" + cra.getTipo()+"'" +
			" AND orden='" + cra.getOrden()+"'";
			System.out.println(valor1);
			
			pstmnt = conn.prepareStatement(valor1);
			rs = pstmnt.executeQuery();
			if(!rs.next()) return -1;
			System.out.println(rs.getString("total"));
			retval = Integer.parseInt(rs.getString("total"));
			//retval = rs.getString("total");
			//System.out.println(retval);
			if(rs.getString("total").equals("0")){
				
				pstmnt = conn.prepareStatement("INSERT INTO cg_cat_remitente_area (cra_id_area, cra_descripcion, tipo, orden) VALUES (?, ?, ?, ?)");
								
				pstmnt.setString(1, cra.getCra_id_area());
				pstmnt.setString(2, cra.getCra_descripcion());
				pstmnt.setString(3, cra.getTipo());
				pstmnt.setInt(4, cra.getOrden());
				retval = pstmnt.executeUpdate();
			}
			else{
				System.out.println("Area ya registrada");
		}
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static String select(Connection conn, String cra_id_area) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String ru_login = null;

		try {
			pstmnt = conn.prepareStatement("SELECT cra_id_area FROM cg_cat_remitente_area WHERE cra_id_area = ?");

			pstmnt.setString(1, cra_id_area);
			
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

	public static CatRemArea select(Connection conn, CatRemArea cra) throws SQLException {

		CatRemArea ru = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (cra.getCra_id_area() != null) {
				where.append(token + "cra_id_area = ?");
				token = " AND ";
			}

			if (cra.getCra_descripcion() != null) {
				where.append(token + "cra_descripcion = ?");
				token = " AND ";
			}
			
			if (cra.getTipo() != null) {
				where.append(token + "tipo = ?");
				token = " AND ";
			}
			
			if (cra.getOrden() != -1) {
				where.append(token + "orden = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_remitente_area " + where.toString());

			int i = 1;
			if (cra.getCra_id_area() != null)
				pstmnt.setString(i++, cra.getCra_id_area());

			if (cra.getCra_descripcion() != null)
				pstmnt.setString(i++, cra.getCra_descripcion());
			
			if (cra.getTipo() != null)
				pstmnt.setString(i++, cra.getTipo());
			
			if (cra.getOrden() != -1)
				pstmnt.setInt(i++, cra.getOrden());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				ru = new CatRemArea();

				ru.setCra_id_area(rs.getString("cra_id_area"));
				ru.setCra_descripcion(rs.getString("cra_descripcion"));
				ru.setTipo(rs.getString("tipo"));
				ru.setOrden(rs.getInt("orden"));
				
				
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
			pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_remitente_area ORDER BY cra_descripcion");

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				CatRemArea cra = new CatRemArea();

				cra.setCra_id_area(rs.getString("cra_id_area"));
				cra.setCra_descripcion(rs.getString("cra_descripcion"));
				cra.setTipo(rs.getString("tipo"));
				cra.setOrden(rs.getInt("orden"));
				
								
				//ce.setPropiedades(UsuarioPropiedadesManager.select(conn, ce.getLogin()));
				//u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
				//u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));

				usrList.add(cra);
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

	public static int update(Connection conn, CatRemArea cra) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		//Statement st = null;
		try {
			//pstmnt = conn.prepareStatement("UPDATE cg_cat_empleado SET id_ce = ?,ce_nombre_completo = ?"
				//	+ ", ce_ap_paterno = ?, ce_ap_materno = ?, ce_os_responsable = ? WHERE id_ce = ?");
				
			pstmnt = conn.prepareStatement("UPDATE cg_cat_remitente_area SET cra_descripcion = ?, tipo = ?, orden =? WHERE cra_id_area = ?");
			pstmnt.setString(1, cra.getCra_descripcion());
			pstmnt.setString(2, cra.getTipo());
			pstmnt.setInt(3, cra.getOrden());
			pstmnt.setString(4, cra.getCra_id_area());
						
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