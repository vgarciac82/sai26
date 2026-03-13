package com.syc.contable;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class CarteraVigenteGetSaldoBusinessLogic {

	private static DataSource	ds	= null;

	public CarteraVigenteGetSaldoBusinessLogic() {
		if (ds != null)
			return;

		Context initContext;

		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/gestion");
		} catch (NamingException ne) {

			throw new RuntimeException("No se encontro la fuente 'jdbc/proinpro'");
		}
	}

	public String validaCartera(String ep, double monto, String tipoMovimiento) throws Exception {
		CarteraVigenteBusinessLogic cvBL = new CarteraVigenteBusinessLogic();
		Connection conn = null;
		double saldo = cvBL.validaCartera(ep);
		conn = ds.getConnection();
		if ("A".equalsIgnoreCase(tipoMovimiento)) {
			double montoModificado = CarteraVigenteGetSaldoManager.getSaldoModificado(conn, ep);
			if (montoModificado + monto <= saldo)
				return "OK";
			else
				throw new Exception("Con este movimiento se supera el monto planeado para este año, en el registro vigente de la cartera de la unidad de inversiones. Su movimiento no es procedente.");
		} else
			return "OK";

	}

}
