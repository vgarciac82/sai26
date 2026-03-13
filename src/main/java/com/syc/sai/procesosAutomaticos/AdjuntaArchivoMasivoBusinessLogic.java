package com.syc.sai.procesosAutomaticos;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class AdjuntaArchivoMasivoBusinessLogic extends DataSourceManager {

	private static final Logger	log			= Logger.getLogger(AdjuntaArchivoMasivoBusinessLogic.class);
	private static boolean		standAlone	= false;

	public AdjuntaArchivoMasivoBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public AdjuntaArchivoMasivoBusinessLogic() {
		standAlone = true;
	}

	public int adjuntaMasivo(String tituloAplicacion, int idCarpeta, int idDocumento, String cCentroContable, int aEjercicioFiscal, int desde, int hasta, String nombreDestino) throws Exception {

		int afectados = 0;
		Connection conn = null;

		try {
			if (standAlone)
				conn = Util.getStandAloneConnection();
			else
				conn = getConnection();

			afectados = AdjuntaArchivoMasivoManager.adjuntMasivo(conn, tituloAplicacion, idCarpeta, idDocumento, cCentroContable, aEjercicioFiscal, desde, hasta, nombreDestino);

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn(e2, e2);
			}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

		return afectados;
	}

	public int adjuntaMasivoPol(String tituloAplicacion, int idCarpeta, int idDocumento, String cCentroContable, int aEjercicioFiscal, List<Rango> range, String nombreDestino) throws Exception {

		int afectados = 0;
		Connection conn = null;

		try {
			if (standAlone)
				conn = Util.getStandAloneConnection();
			else
				conn = getConnection();

			afectados = AdjuntaArchivoMasivoManager.adjuntMasivoDocPoliza(conn, tituloAplicacion, idCarpeta, idDocumento, cCentroContable, aEjercicioFiscal, range, nombreDestino);

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn(e2, e2);
			}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

		return afectados;
	}

	public String adjuntaSolicitudPagoMasivo(String tituloAplicacion, String nombreDestino, String directorioTemporal, Usuario u) throws Exception {
		Connection conn = null;
		try {
			if (standAlone)
				conn = Util.getStandAloneConnection();
			else
				conn = getConnection();
			String logAdjunto = AdjuntaArchivoMasivoManager.adjuntMasivo(conn, tituloAplicacion, nombreDestino, directorioTemporal, u);
			conn.commit();
			return logAdjunto;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback: " + e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn);
		}
	}

	public String adjuntaCertificadoTransitoMasivo(String tituloAplicacion, String nombreDestino, String directorioTemporal, Usuario u) throws Exception {
		Connection conn = null;
		try {
			if (standAlone)
				conn = Util.getStandAloneConnection();
			else
				conn = getConnection();
			String logAdjunto = AdjuntaArchivoMasivoManager.adjuntaCertificadoTransitoMasivo(conn, tituloAplicacion, nombreDestino, directorioTemporal, u);
			conn.commit();
			return logAdjunto;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback: " + e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn);
		}
	}

	public int adjuntaArchivoSimple(String tituloAplicacion, String usuario , int idGabinete, String nombreCarpeta, String nombreDocumento, File archivo) throws Exception {

		int afectados = 0;
		Connection conn = null;

		try {
			conn = getConnection();

			afectados = AdjuntaArchivoMasivoManager.adjuntaArchivo(conn, usuario, tituloAplicacion, idGabinete, nombreCarpeta, nombreDocumento, archivo);

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn(e2, e2);
			}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

		return afectados;
	}

}
