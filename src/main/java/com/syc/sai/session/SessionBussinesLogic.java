package com.syc.sai.session;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.session.core.SessionManager;

public class SessionBussinesLogic extends DataSourceManager {

	public static class OperacionAtrapada {
		private String	coResponsable;
		private int		idCaso;
		private int		idCasoOper;
		private int		idOper;
		private String	oResponsable;

		public OperacionAtrapada(int idCaso, int idCasoOper, int idOper, String coResponsable, String oResponsable) {
			super();
			this.idCaso = idCaso;
			this.idCasoOper = idCasoOper;
			this.idOper = idOper;
			this.coResponsable = coResponsable;
			this.oResponsable = oResponsable;
		}

		public String getCoResponsable() {
			return coResponsable;
		}

		public int getIdCaso() {
			return idCaso;
		}

		public int getIdCasoOper() {
			return idCasoOper;
		}

		public int getIdOper() {
			return idOper;
		}

		public String getoResponsable() {
			return oResponsable;
		}

		public void setCoResponsable(String coResponsable) {
			this.coResponsable = coResponsable;
		}

		public void setIdCaso(int idCaso) {
			this.idCaso = idCaso;
		}

		public void setIdCasoOper(int idCasoOper) {
			this.idCasoOper = idCasoOper;
		}

		public void setIdOper(int idOper) {
			this.idOper = idOper;
		}

		public void setoResponsable(String oResponsable) {
			this.oResponsable = oResponsable;
		}

	}

	private static final Logger	log	= Logger.getLogger(SessionBussinesLogic.class);

	public SessionBussinesLogic(String jniName) {
		super.init(jniName);
	}

	public int liberaCasos(Usuario u) throws Exception {
		Connection conn = null;
		int afectados = 0;
		List<OperacionAtrapada> l = null;
		try {
			
			l = obtenOperacionesAtrapadas(u);
			
			conn = getConnection();
			afectados = SessionManager.liberaCasosBatch(conn, l);
			conn.commit();
			
			return afectados;
			
		} catch (Exception e) {
			
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback " + e2, e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
			l = null;
		}

	}

	public List<OperacionAtrapada> obtenOperacionesAtrapadas(Usuario u) throws Exception {
		Connection conn = null;
		List<OperacionAtrapada> operaciones = new ArrayList<SessionBussinesLogic.OperacionAtrapada>();
		try {
			conn = getConnection();
			operaciones = SessionManager.listaOperacionesAtrapadas(conn, u);
			return operaciones;
		} finally {
			CloseObject.closeObject(conn);
		}
	}
}
