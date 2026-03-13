package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.contable.core.AdministracionAccesoManager;
import com.syc.contable.core.ControlAccesoManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class AdministracionAccesoBusinessLogic extends DataSourceManager {

	private static Logger	log	= Logger.getLogger(AdministracionAccesoBusinessLogic.class);

	public AdministracionAccesoBusinessLogic(String jniName) {

		super.init(jniName);
	}

	public List<String> getOpcionDelegable() throws Exception {
		Connection conn = null;
		List<String> res;
		try {
			conn = getConnection();
			res = AdministracionAccesoManager.opcionDel(conn);
		} finally {
			if (conn != null)
				conn.close();
			conn = null;
		}
		return res;
	}


	public List<String> getselecChecks( String listadoUsuario) throws Exception {
		Connection conn = null;
		List<String> res;
		try {
			conn = getConnection();
			res = AdministracionAccesoManager.selecChecks(conn, listadoUsuario);
		} finally {
			if (conn != null)
				conn.close();
			conn = null;
		}
		return res;
	}

	
	public boolean asignar( String listadoUsuario, String opcion) throws SQLException {
		Connection conn = null;
		boolean res = true;
		try {
			conn = getConnection();
			if (!"".equals(listadoUsuario)) {
				boolean existe = AdministracionAccesoManager.validaUsuario(conn, listadoUsuario);
				if (!existe)
					throw new SQLException("No Existe el usuario o relacion usuario-Grupo para " + listadoUsuario);
			}
			AdministracionAccesoManager.asignar(conn, listadoUsuario, opcion);
			conn.commit();
		} catch (Exception e) {
			res = false;
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn("Problemas realizando rollback de conexi\u00F3n" + e2, e2);
			}
			throw new SQLException(e);
		} finally {
			try {
				CloseObject.closeObject(conn, false);
			} catch (Exception e) {
				log.warn("Problemas realizando rollback de conexi\u00F3n" + e, e);
			}
		}
		return res;
	}

	public boolean desasignar( String listadoUsuario) throws SQLException {
		Connection conn = null;
		boolean res = true;
		try {
			conn = getConnection();
			if (!"".equals(listadoUsuario)) {
				boolean existe = AdministracionAccesoManager.validaUsuario(conn, listadoUsuario);
				if (!existe)
					throw new SQLException("No Existe el usuario o relacion usuario-Grupo para " + listadoUsuario);
			}
			AdministracionAccesoManager.desasignar(conn, listadoUsuario );
			conn.commit();
		} catch (Exception e) {
			res = false;
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn("Problemas realizando rollback de conexi\u00F3n" + e2, e2);
			}
			throw new SQLException(e);
		} finally {
			try {
				CloseObject.closeObject(conn, false);
			} catch (Exception e) {
				log.warn("Problemas realizando rollback de conexi\u00F3n" + e, e);
			}
		}
		return res;
	}

	
}