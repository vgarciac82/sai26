package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

	public class NegativaPestanaManager {
		public static Map <String,String> selectByRol(Connection conn, String rol, String modulo) throws SQLException{
			Map <String,String> v=new Hashtable<String, String>();
			PreparedStatement pstmnt = null;
			ResultSet rs = null;
			try
			{
				pstmnt = conn.prepareStatement("SELECT MODULO, PESTANA FROM mRoleNegPestana WHERE ? LIKE ('%'+R_NOMBRE+'%') AND MODULO=?");
				pstmnt.setString(1, rol);
				pstmnt.setString(2, modulo);
				rs=pstmnt.executeQuery();
				int i=0;
				while (rs.next()) {	
					v.put(rs.getString("MODULO")+i, rs.getString("PESTANA"));
					i++;
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
	public static Map<String,String> selectByPestana(Connection conn, String role, String modulo, String pestana) throws SQLException{
		Map<String,String> botones=new Hashtable<String, String>();
		PreparedStatement pstmnt=null;
		ResultSet rs=null;
		try{
			pstmnt=conn.prepareStatement("select PESTANA, BOTON from mRoleNegBoton where ? LIKE ('%'+r_nombre+'%') and modulo=? and pestana=? ");
			pstmnt.setString(1,role);
			pstmnt.setString(2,modulo);
			pstmnt.setString(3,pestana);
			rs=pstmnt.executeQuery();
			int i=0;
			while(rs.next())
			{
				botones.put(rs.getString("PESTANA")+i, rs.getString("BOTON"));
				i++;
			}
		}finally{
			if (rs!=null)
				rs.close();
			if (pstmnt!=null)
				pstmnt.close();
			
		rs=null;
		pstmnt=null;
		}
		return botones;
	}
}
