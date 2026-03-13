package com.syc.sai.contabilidad.polizamanual.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.syc.contable.AccountingEngine;
import com.syc.contable.PolizaManager;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class GeneradorPolizaManualManager {

	public static void generaPolizaCancelaPago(Connection conn, String strFolioPagado, String strUE) throws Exception {
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo(true);
		
		int nFolioDocPoliza = insertaPolizaCancelaPago(conn, strFolioPagado, strUE);
		ae.makeAccountingApplicationWithoutEvent(conn, "DOCPOLIZA", String.valueOf(nFolioDocPoliza), "tdocpolizaencabezado", "tdocpolizadetalle", "nFolioDocPoliza");
		
	}

	private static int insertaPolizaCancelaPago(Connection conn, String strFolioPagado, String strUE) throws Exception {
		CallableStatement cmst = null;
		String query = "{call sp_inserta_poliza_manual(?,?,?)}";
		int nfolioDocPoliza = -1;
		try {

			Caso c = PolizaManager.instanciaCasoPoliza(strUE);
			nfolioDocPoliza = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cmst = conn.prepareCall(query);

			cmst.setInt(1, Integer.parseInt(strFolioPagado));
			cmst.setInt(2, nfolioDocPoliza);
			cmst.setString(3, strUE);

			cmst.execute();

			return nfolioDocPoliza;

		} finally {
			CloseObject.closeObject(cmst);
		}
	}


	/**
	 * * Genera de manera automatica un caso de poliza manual para notificar un
	 * pasivo contingente.
	 * 
	 * @param conn
	 * @param fAplicacion
	 * @param strUE
	 * @param cCentroContable
	 * @param uLogin
	 * @param uNombre
	 * @return
	 * @throws Exception
	 */
	public static int generaPolizaPasivoCont(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre) throws Exception {
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo(true);

		int nFolioDocPoliza = insertaPolizaMensualPasivosCont(conn, fAplicacion, strUE, cCentroContable, uLogin, uNombre);
		ae.makeAccountingApplicationWithoutEvent(conn, "DOCPOLIZA", String.valueOf(nFolioDocPoliza), "tdocpolizaencabezado", "tdocpolizadetalle", "nFolioDocPoliza");
		return nFolioDocPoliza;
	}
	
	public static int insertaPolizaMensualPasivosCont(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre) throws Exception {

		CallableStatement cmst = null;
		String query = "{call sp_inserta_poliza_pasivos(?,?,?,?,?)}";
		int nfolioDocPoliza = -1;

		try {

			Caso c = PolizaManager.instanciaCasoPoliza(strUE, uLogin, uNombre, cCentroContable);
			nfolioDocPoliza = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cmst = conn.prepareCall(query);

			cmst.setString(1, fAplicacion);
			cmst.setInt(2, nfolioDocPoliza);
			cmst.setString(3, cCentroContable);
			cmst.setString(4, uLogin);
			cmst.setString(5, strUE);

			cmst.execute();

			return nfolioDocPoliza;

		} finally {
			CloseObject.closeObject(cmst);
		}

	}
	
	/**
	 * * Genera de manera automatica un caso de poliza manual dar de alta un
	 * pasivo contingente.
	 * 
	 * @param conn
	 * @param fAplicacion
	 * @param strUE
	 * @param cCentroContable
	 * @param uLogin
	 * @param uNombre
	 * @return
	 * @throws Exception
	 */
	public static int generaPolizaPasivoContAlta(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre, String RFC, String PC) throws Exception {
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo(true);

		int nFolioDocPoliza = insertaPolizaAltaPasivosCont(conn, fAplicacion, strUE, cCentroContable, uLogin, uNombre, RFC, PC);
		ae.makeAccountingApplicationWithoutEvent(conn, "DOCPOLIZA", String.valueOf(nFolioDocPoliza), "tdocpolizaencabezado", "tdocpolizadetalle", "nFolioDocPoliza");
		return nFolioDocPoliza;
	}
	
	public static int insertaPolizaAltaPasivosCont(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre, String RFC, String PC) throws Exception {

		CallableStatement cmst = null;
		String query = "{call sp_inserta_poliza_pasivos_alta(?,?,?,?,?,?,?)}";
		int nfolioDocPoliza = -1;

		try {

			Caso c = PolizaManager.instanciaCasoPoliza(strUE, uLogin, uNombre, cCentroContable);
			nfolioDocPoliza = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cmst = conn.prepareCall(query);

			cmst.setString(1, fAplicacion);
			cmst.setInt(2, nfolioDocPoliza);
			cmst.setString(3, cCentroContable);
			cmst.setString(4, uLogin);
			cmst.setString(5, strUE);
			cmst.setString(6, RFC);
			cmst.setString(7, PC);

			cmst.execute();

			return nfolioDocPoliza;

		} finally {
			CloseObject.closeObject(cmst);
		}

	}
	
	/**
	 * * Genera de manera automatica un caso de poliza manual para dar de baja un 
	 * pasivo contingente.
	 * 
	 * @param conn
	 * @param fAplicacion
	 * @param strUE
	 * @param cCentroContable
	 * @param uLogin
	 * @param uNombre
	 * @return
	 * @throws Exception
	 */
	public static int generaPolizaPasivoContBaja(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre, String RFC, String PC) throws Exception {
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo(true);

		int nFolioDocPoliza = insertaPolizaMensualPasivosContBaja(conn, fAplicacion, strUE, cCentroContable, uLogin, uNombre, RFC, PC);		
		ae.makeAccountingApplicationWithoutEvent(conn, "DOCPOLIZA", String.valueOf(nFolioDocPoliza), "tdocpolizaencabezado", "tdocpolizadetalle", "nFolioDocPoliza");
		return nFolioDocPoliza;
	}
	
	public static int insertaPolizaMensualPasivosContBaja(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre, String RFC, String PC) throws Exception {

		CallableStatement cmst = null;
		String query = "{call sp_inserta_poliza_pasivos_baja(?,?,?,?,?,?,?)}";
		int nfolioDocPoliza = -1;

		try {

			Caso c = PolizaManager.instanciaCasoPoliza(strUE, uLogin, uNombre, cCentroContable);
			nfolioDocPoliza = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cmst = conn.prepareCall(query);

			cmst.setString(1, fAplicacion);
			cmst.setInt(2, nfolioDocPoliza);
			cmst.setString(3, cCentroContable);
			cmst.setString(4, uLogin);
			cmst.setString(5, strUE);
			cmst.setString(6, RFC);
			cmst.setString(7, PC);

			cmst.execute();

			return nfolioDocPoliza;

		} finally {
			CloseObject.closeObject(cmst);
		}

	}
	
	public static int generaPolizaPasivoContLaboral(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre) throws Exception {
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo(true);

		int nFolioDocPoliza = insertaPolizaMensualPasivosContLaboral(conn, fAplicacion, strUE, cCentroContable, uLogin, uNombre);
		ae.makeAccountingApplicationWithoutEvent(conn, "DOCPOLIZA", String.valueOf(nFolioDocPoliza), "tdocpolizaencabezado", "tdocpolizadetalle", "nFolioDocPoliza");
		return nFolioDocPoliza;
	}
	
	public static int insertaPolizaMensualPasivosContLaboral(Connection conn, String fAplicacion, String strUE, String cCentroContable, String uLogin, String uNombre) throws Exception {

		CallableStatement cmst = null;
		String query = "{call sp_inserta_poliza_pasivosLaborales(?,?,?,?,?)}";
		int nfolioDocPoliza = -1;

		try {

			Caso c = PolizaManager.instanciaCasoPoliza(strUE, uLogin, uNombre, cCentroContable);
			nfolioDocPoliza = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cmst = conn.prepareCall(query);

			cmst.setString(1, fAplicacion);
			cmst.setInt(2, nfolioDocPoliza);
			cmst.setString(3, cCentroContable);
			cmst.setString(4, uLogin);
			cmst.setString(5, strUE);

			cmst.execute();

			return nfolioDocPoliza;

		} finally {
			CloseObject.closeObject(cmst);
		}

	}

}
