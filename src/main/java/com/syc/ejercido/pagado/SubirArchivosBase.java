package com.syc.ejercido.pagado;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
//import com.syc.gestion.core.GestionException;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;

public class SubirArchivosBase extends DataSourceManager{
	
	public boolean subirArchivo(String nomTabla, String Ruta) throws SQLException {
		boolean result = false;
		CallableStatement cs1 = null;
		//ResultSet rs = null;
		Connection conn = null;
		try{
			System.out.println("Tabla: "+nomTabla+" Ruta: " +Ruta);
			String path = ("/SubirArchivo");
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			String pathO = (Ruta);
			File fileO = new File(pathO);
			if(fileO.exists()){
				conn = getConnection();
				cs1 = conn.prepareCall("{call SubirArchivo(?,?)}");
				cs1.setString(1, nomTabla);
				cs1.setString(2, Ruta);
				cs1.execute();
				conn.commit();
	 			result = true;
			}
		}catch(Exception e){
			System.out.println("Error: " +e);
			conn.rollback();
			result = false;
		}finally{
			if (cs1 != null){cs1.close();}
			cs1= null;
		}
		return result;
	}
	
	public boolean subirArchivo() throws SQLException {
		boolean result = false;
		CallableStatement cs1 = null;
		Connection conn = null;
		try{
			
			conn = getConnection();
			cs1 = conn.prepareCall("{call SubirArchivos}");
			cs1.execute();
			conn.commit();
 			result = true;
		}catch(Exception e){
			System.out.println("Error: " +e);
			conn.rollback();
			result = false;
		}finally{
			if (cs1 != null){cs1.close();}
			cs1= null;
		}
		return result;
	}	
}