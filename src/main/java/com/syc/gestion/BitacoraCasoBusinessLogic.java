package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.BitacoraCaso;
import com.syc.gestion.core.BitacoraCasoManager;
import com.syc.gestion.core.GestionException;

public class BitacoraCasoBusinessLogic extends DataSourceManager {

	//private static String OPER_CONTINUE = "CONTINUAR";
	//private static String CASO_END = "TERMINAR";

	private static Logger log = Logger.getLogger(BitacoraCasoBusinessLogic.class);

	public BitacoraCasoBusinessLogic(String jniName) {

		super.init(jniName);
	}

	public BitacoraCaso getBitacoraCaso(int idCaso) throws GestionException {

		BitacoraCaso retVal = null;
		Connection conn = null;

		try {
			conn = getConnection();
			
			retVal = new BitacoraCaso();
			retVal.setIdCaso(idCaso);

			retVal = BitacoraCasoManager.select(conn, retVal);
		} catch (SQLException exc) {
			log.warn("Obteniendo Bitacora Caso", exc);
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

		return retVal;
	}

	public int updateBitacoraCasoUser(String oldUser, String newUser) throws GestionException {

		int retVal = -1;
		Connection conn = null;

		try {
			conn = getConnection();
			
			retVal = BitacoraCasoManager.updateUser(conn, oldUser, newUser);

			conn.commit();
			
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("Error en rollback", ex);
			}
			log.warn("Actualizando Bitacora Caso Usuario ", exc);
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

		return retVal;
	}

}
