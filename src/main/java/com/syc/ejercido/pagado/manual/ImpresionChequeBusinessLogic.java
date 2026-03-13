package com.syc.ejercido.pagado.manual;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.manual.core.ImpresionChequeManager;

public class ImpresionChequeBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(ImpresionChequeBusinessLogic.class);

	public ImpresionChequeBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public int reemplazaCheque(int nFolioCheque) throws Exception {
		int nuevoFolio = 0;
		Connection conn = null;
		try {
			log.info("Reemplazando cheque [" + nFolioCheque + "]");
			conn = getConnection();
			nuevoFolio = ImpresionChequeManager.reemplazaCheque(conn, nFolioCheque);

			AccountingEngine motorContable = new AccountingEngine();
			motorContable.makeAccountingApplication(conn, "CHEQUE", String.valueOf(nuevoFolio), "tChequeEncabezado", "tChequeDetalle", "nFolioCheque");

			conn.commit();
			return nuevoFolio;

		} catch (Exception e) {
			log.error("Ocurrio el siguiente error mientras se reemplazaba el cheque: " + e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error("Error realizando rollback " + e2, e2);
				}
			throw e;

		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
}
