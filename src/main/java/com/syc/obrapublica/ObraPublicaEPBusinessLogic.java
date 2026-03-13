package com.syc.obrapublica;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ObraPublicaEPBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(ObraPublicaEPBusinessLogic.class);

	public ObraPublicaEPBusinessLogic() {
	}

	public List<String> getEPsSinOli(String cartera, String oli, String ur, String ue, String folioSAI) throws Exception {
		Connection conn = null;
		List<String> eps = new ArrayList<String>();

		try {
			EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
			EjercicioFiscal efActivo = efbl.getEjercicioFiscalActivo();

			if (efActivo == null)
				throw new Exception("No se ha definido Ejercicio Fiscal Activo");

			conn = getConnection();
			eps = ObraPublicaEPManager.getEPsSinOli( conn, cartera, ur, ue, folioSAI); 
			return eps;
		} catch (Exception e) {
			throw e;
		} finally {
			if (conn != null)
				try {
					CloseObject.closeObject(conn, true);
				} catch (Exception e2) {
					log.warn("Error cerrando la DB.", e2);
				}
		}

	}
	public List<String> getCarterasSinOli( String ur, String ue) throws Exception {
		Connection conn = null;
		List<String> eps = new ArrayList<String>();

		try {
			EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
			EjercicioFiscal efActivo = efbl.getEjercicioFiscalActivo();

			if (efActivo == null)
				throw new Exception("No se ha definido Ejercicio Fiscal Activo");

			conn = getConnection();
			eps = ObraPublicaEPManager.getCarterasSinOli( conn, ur, ue); 
			return eps;
		} catch (Exception e) {
			throw e;
		} finally {
			if (conn != null)
				try {
					CloseObject.closeObject(conn, true);
				} catch (Exception e2) {
					log.warn("Error cerrando la DB.", e2);
				}
		}

	}

	public List<String> getEPs(String usuario, String cartera, String oli, String ur, String ue) throws Exception {
		Connection conn = null;
		List<String> eps = new ArrayList<String>();

		try {
			EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
			EjercicioFiscal efActivo = efbl.getEjercicioFiscalActivo();

			if (efActivo == null)
				throw new Exception("No se ha definido Ejercicio Fiscal Activo");

			conn = getConnection();
			boolean UESinOlis = ObraPublicaEPManager.ueSinOLIS(conn, ue);
			if( "-1".equals(cartera) && UESinOlis )
				eps = ObraPublicaEPManager.getEPFromVistas(conn, usuario, efActivo.getaEjercicioFiscal(), cartera, oli, ur, ue);
			else
				eps = ObraPublicaEPManager.getEPFromCarteras(conn, usuario, efActivo.getaEjercicioFiscal(), cartera, oli, ur, ue); 
			return eps;
		} catch (Exception e) {
			throw e;
		} finally {
			if (conn != null)
				try {
					CloseObject.closeObject(conn, true);
				} catch (Exception e2) {
					log.warn("Error cerrando la DB.", e2);
				}
		}

	}
	public List<String> getEPs(List<String> clavesPresupuestales, String cartera, String oli, String ur, String ue) throws Exception {
		Connection conn = null;
		List<String> eps = new ArrayList<String>();

		try {
			EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
			EjercicioFiscal efActivo = efbl.getEjercicioFiscalActivo();

			if (efActivo == null)
				throw new Exception("No se ha definido Ejercicio Fiscal Activo");

			conn = getConnection();
			eps = ObraPublicaEPManager.getEPFromCarteras(conn, clavesPresupuestales, efActivo.getaEjercicioFiscal(), cartera, oli, ur, ue); 
			return eps;
		} catch (Exception e) {
			throw e;
		} finally {
			if (conn != null)
				try {
					CloseObject.closeObject(conn, true);
				} catch (Exception e2) {
					log.warn("Error cerrando la DB.", e2);
				}
		}

	}
}
