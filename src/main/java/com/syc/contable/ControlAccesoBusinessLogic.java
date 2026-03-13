package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.contable.core.ControlAccesoManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ControlAccesoBusinessLogic extends DataSourceManager {

	private static Logger	log	= Logger.getLogger(ControlAccesoBusinessLogic.class);

	public ControlAccesoBusinessLogic(String jniName) {

		super.init(jniName);
	}

	public ArrayList<String> getUnidadesResponsables() throws Exception {
		Connection conn = null;
		ArrayList<String> res;
		try {
			conn = getConnection();
			res = ControlAccesoManager.UnidadesResponsables(conn);
		} finally {
			if (conn != null)
				conn.close();
			conn = null;
		}
		return res;
	}

	public List<String> getUsuariosCC(String cc, int tc) throws Exception {
		Connection conn = null;
		List<String> res;
		try {
			conn = getConnection();
			res = ControlAccesoManager.UsuariosCC(conn, cc, tc);
		} finally {
			if (conn != null)
				conn.close();
			conn = null;
		}
		return res;
	}

	
	public boolean apagar(int tc, String ur, String cc, String idUsuario) throws SQLException {
		Connection conn = null;
		boolean res = true;
		try {
			conn = getConnection();
			if (!"".equals(idUsuario)) {
				boolean existe = ControlAccesoManager.validaUsuario(conn, idUsuario);
				if (!existe)
					throw new SQLException("No Existe el usuario o relacion usuario-Grupo para " + idUsuario);
				if(tc > 0 || tc == -3)
					existe = ControlAccesoManager.validaUsuarioGrupo(conn,idUsuario,tc);
				if (!existe)
					throw new SQLException("No Existe el usuario " + idUsuario + " en el grupo: " + (ControlAccesoManager.casoGrupo == null? String.valueOf(tc): ControlAccesoManager.casoGrupo.get(new Integer (tc))) + " que desea apagar ");
			}
			ControlAccesoManager.apagar(conn, tc, ur, cc, idUsuario);
			conn.commit();

		} catch (Exception e) {
			res = false;
			if (conn != null)
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

	public boolean prender(int tc, String ur, String cc, String idUsuario) throws SQLException {
		Connection conn = null;
		boolean res = true;
		try {
			conn = getConnection();
			if (!"".equals(idUsuario)) {
				boolean existe = ControlAccesoManager.validaUsuario(conn, idUsuario);
				if (!existe)
					throw new SQLException("No Existe el usuario o relacion usuario-Grupo para " + idUsuario);
				if(tc > 0 || tc == -3)
					existe = ControlAccesoManager.validaUsuarioGrupo(conn,idUsuario,tc);
				if (!existe)
					throw new SQLException("No Existe el usuario " + idUsuario + " en el grupo: " + (ControlAccesoManager.casoGrupo == null? String.valueOf(tc): ControlAccesoManager.casoGrupo.get(new Integer (tc))) + " que desea encender ");
			}
			ControlAccesoManager.prender(conn, tc, ur, cc, idUsuario);
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