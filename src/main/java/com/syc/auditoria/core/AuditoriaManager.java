package com.syc.auditoria.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class AuditoriaManager {
	
	public static ArrayList getValores_origen(Connection conn,String tabla, String where)throws SQLException{
		ArrayList originales=new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs=null;
		
		try {
			pstmnt = conn.prepareStatement("select * from "+tabla+"  where "+where);

			rs = pstmnt.executeQuery();
			
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			int cols=resultSetMetaData.getColumnCount();
			if (rs.next()){
				for (int i=1; i<(cols+1); i++){
					originales.add(resultSetMetaData.getColumnName(i)+"="+rs.getString(i));
				}
			}
			else{
				for (int i=1; i<(cols+1); i++){
					originales.add(resultSetMetaData.getColumnName(i));
				}
			}
			
		} 
		catch(Exception e){e.printStackTrace();}
		finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return originales;		
	}
	public static int insert(Connection conn, String usuario,String area, String aplicacion, String modulo, String accion, String valor_origen,String valor_destino,String valor,String valor_llave) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		
		try {
			pstmnt = conn.prepareStatement("INSERT INTO IMXAUDITORIA "
					+ "(nombre_aplicacion, nombre_modulo, accion, valor_origen,valor_destino, usuario, area_usuario, fecha_operacion,query,valor_llave) "
					+ "VALUES (?,?,?,?,?,?,?,GETDATE(),?,?)");
			
			pstmnt.setString(1, aplicacion);
			pstmnt.setString(2, modulo);
			pstmnt.setString(3, accion);
			pstmnt.setString(4, valor_origen);
			pstmnt.setString(5, valor_destino);
			pstmnt.setString(6, usuario);
			pstmnt.setString(7, area);
			pstmnt.setString(8, valor);
			pstmnt.setString(9, valor_llave);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}


	//Ethiel, no se usa para nada
	public static int delete(Connection conn, int id_auditoria ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM IMXAUDITORIA WHERE id_auditoria = ?");

			pstmnt.setInt(1, id_auditoria);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
	
	//Ethiel, no se usa para nada
	public static int update(Connection conn, Auditoria a) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE IMXAUDITORIA SET id_auditoria = ?, nombre_aplicacion = ?, nombre_modulo = ?"
					+ ", accion = ?, valor = ?, usuario = ?, fecha_operacion = ? WHERE id_auditoria = ?");

			pstmnt.setInt(1, a.getId_auditoria());
			pstmnt.setString(2, a.getNombre_aplicacion());
			pstmnt.setString(3, a.getNombre_modulo());
			pstmnt.setString(4, a.getAccion());
			pstmnt.setString(5, a.getValor_origen());
			pstmnt.setString(6, a.getUsuario());
			pstmnt.setTimestamp(7, a.getFecha_operacion());

			retval = pstmnt.executeUpdate();
			
		} catch (SQLException sqle) {
			conn.rollback();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static List select(Connection conn,String modulo, String accion, String login, String fechaini, String fechafin,String area) throws SQLException {

		List acumuladoLst = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		String where = " where au.area_usuario=convert(nchar,area.id_area) and au.usuario=usu.u_login and "+
						" area.ID_AREA=ur.ID_AREA ";
		String and = " and ";
		
		where+=(modulo!=null&&!"".equals(modulo)? and+" au.nombre_modulo= '"+modulo+"'":"");
		and=(modulo!=null&&!"".equals(modulo)?" and ":and);
		where+=(accion!=null&&!"".equals(accion)? and+" au.accion= '"+accion+"'":"");
		and=(accion!=null&&!"".equals(accion)?" and ":and);
		//where+=(login!=null&&!"".equals(login)? and+" au.usuario= '"+login+"'":""); //Ethiel, se comento para que se busque sobre el usuario afectado y no por el que hace la afectacion
		where+=(login!=null&&!"".equals(login)? and+" (au.valor_origen like '%"+login+"%' or au.valor_destino like '%"+login+"%')":"");//Ethiel, ahora es esta la condicion para buscar sobre el afectado y no sobre el que hace la afectacion
		and=(login!=null&&!"".equals(login)?" and ":and);
		//where+=(area!=null&&!"".equals(area)? and+" au.area_usuario= '"+area+"'":"");//Ethiel, se comento para que se busque sobre el usuario afectado y no por el que hace la afectacion
		where+=(area!=null&&!"".equals(area)? and+" (au.valor_origen like '%"+area+"%' or au.valor_destino like '%"+area+"%')":"");//Ethiel, ahora es esta la condicion para buscar sobre el afectado y no sobre el que hace la afectacion
		and=(area!=null&&!"".equals(area)?" and ":and);
		if (fechaini!=null && fechafin!=null &&!"".equals(fechaini) &&!"".equals(fechafin)) {
			where+= and + "  au.fecha_operacion BETWEEN CONVERT(DATETIME, '" + fechaini + " 00:00:00', 103) and CONVERT(DATETIME, '" + fechafin + " 23:59:59', 103)";
		}
		else if (fechaini!=null &&!"".equals(fechaini)) {
			where+= and + "  au.fecha_operacion > CONVERT(DATETIME, '" + fechaini + " 00:00:00', 103)";
			
		} else if (fechafin!=null &&!"".equals(fechafin)) {
			where+= and + "  au.fecha_operacion < CONVERT(DATETIME, '" + fechafin + " 23:59:59', 103)";
		}
		where=(" where ".equals(where)?"":where);
		
		String query="select au.id_auditoria, au.nombre_modulo, au.accion, au.valor_origen, au.valor_destino, au.usuario, au.area_usuario, usu.u_nombre,"+
                     " area.D_DESCRIPCION AS area_usuario_desc, "+
                     //" (SELECT D_DESCRIPCION FROM CG_CAT_AREAS WHERE ID_AREA = area.ID_AREA_PADRE) AS area_padre_desc, "+
                     " ur.cUnidadResponsable as UR, "+
                     " au.fecha_operacion, au.query "+
                     " FROM IMXAUDITORIA AS au, CG_CAT_AREAS AS area, cg_usuario as usu, tCatalogoUnidadResponsable ur  "+
                     where+
                     " order by au.fecha_operacion";
		System.out.println("query=["+query+"]");

		try {

			pstmnt = conn.prepareStatement(query);

			rs = pstmnt.executeQuery();
			
			while (rs.next()) {
				Auditoria a = new Auditoria();
				
				a.setId_auditoria(rs.getInt("id_auditoria"));
				a.setNombre_modulo(rs.getString("nombre_modulo"));
				a.setAccion(rs.getString("accion"));
				a.setValor_origen(rs.getString("valor_origen"));
				a.setValor_destino(rs.getString("valor_destino"));
				a.setUsuario(rs.getString("usuario"));
				a.setNombre_usuario(rs.getString("u_nombre"));
				a.setId_area(rs.getString("area_usuario"));
				a.setArea_usuario(rs.getString("area_usuario_desc"));
				//a.setArea_padre(rs.getString("area_padre_desc"));
				a.setArea_padre(rs.getString("UR"));
				a.setFecha_operacion(rs.getTimestamp("fecha_operacion"));
				a.setQuery(rs.getString("query"));
				
				acumuladoLst.add(a);
			}


		}
		catch(Exception ex){ex.printStackTrace();}
		finally {
			
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return acumuladoLst;
	}

}

