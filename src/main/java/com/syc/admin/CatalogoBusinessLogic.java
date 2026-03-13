package com.syc.admin;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;

public class CatalogoBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(CatalogoBusinessLogic.class);

	public CatalogoBusinessLogic(String jniName) {

		super.init(jniName);
	}

	
	public int servicio(String evento) throws CatalogoException {

		Connection conn = null;
		int retVal = -1;
		try {
			
			conn = getConnection();
			
			if (evento.endsWith("User")){
				
				if(evento!=null) {
					if(evento.equals("addUser")){
						//retVal = UsuarioManager.insert(conn, u);
					} else if(evento.equals("updateUser")) {
						//retVal = UsuarioManager.update(conn, u);
					} else if(evento.equals("deleteUser")) {
						//retVal = UsuarioManager.delete(conn, u.getLogin());
					}
				}
			}

		} catch (SQLException exc) {
			log.error("Recuperando descripcion", exc);
			throw new CatalogoException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}
		return retVal;
	}
}
