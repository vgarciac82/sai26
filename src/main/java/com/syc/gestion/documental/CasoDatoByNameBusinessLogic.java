package com.syc.gestion.documental;
//
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;

public class CasoDatoByNameBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(CasoDatoByNameBusinessLogic.class);

	public CasoDatoByNameBusinessLogic(String jniName) {

		super.init(jniName);
	}

	public String select(int id_caso, String id_tcv) throws GestionException {

		Connection conn = null;
		String v = null;

		try {
			conn = getConnection();

			v = CasoDatoByNameManager.select(conn, id_caso, id_tcv);

			log.debug("[CasoDatoByNameBusinessLogic] variable='" + id_tcv + "', Caso='" + id_caso + "', Valor='" + v + "'");
		} catch (SQLException exc) {
			log.error("Recuperando la variable '" + id_tcv + "' en el Caso '" + id_caso + "'");
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