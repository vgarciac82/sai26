package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.OpcionManager;

public class OpcionBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(OpcionBusinessLogic.class);

	public OpcionBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public Vector getOpcionByUser(String u_login, String o_estaenmenu,
			int id_producto) throws GestionException {

		Vector v = new Vector();
		Connection conn = null;

		try {
			conn = getConnection();

			v = OpcionManager.selectByUser(conn, u_login, o_estaenmenu,
					id_producto);
		} catch (SQLException exc) {
			log.warn("Obteniendo Opcion por Usuario", exc);
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

		return v;
	}

}
