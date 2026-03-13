package com.syc.sai.contabilidad.polizamanual.model;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.polizamanual.controller.GeneradorPolizaManualManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class GeneradorPolizaManualBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(GeneradorPolizaManualBusinessLogic.class);

	public GeneradorPolizaManualBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaPolizaCancelaPago(String strFolioPagado, String cUE) {
		Connection conn = null;
		try {
			conn = getConnection();
			GeneradorPolizaManualManager.generaPolizaCancelaPago(conn, strFolioPagado, cUE);
			conn.commit();
		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas en rollback: " + e2, e2);
				}
		} finally {
			CloseObject.closeObject(conn);
		}
	}
}
