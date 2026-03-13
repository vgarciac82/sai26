package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.UsuarioGrupoManager;

public class UsuarioGrupoBusinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger(UsuarioGrupoBusinessLogic.class);

	public UsuarioGrupoBusinessLogic(String jniName) {
		super.init(jniName);
		System.out.println("usuario grupo business logic 1");
	}
	
	public Map selectUsuarios(String g_nombre) throws GestionException {

		Map mu = new Hashtable();
		Connection conn = null;

		try {
			conn = getConnection();

			mu = UsuarioGrupoManager.selectUsuarios(conn, g_nombre);
			
		} catch (SQLException exc) {
			log.error("Recuperando usuarios del grupo: " + g_nombre, exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return mu;
	}


}
