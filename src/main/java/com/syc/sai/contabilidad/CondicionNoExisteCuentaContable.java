package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.syc.contable.AccountingEngineException;
import common.Logger;

public class CondicionNoExisteCuentaContable extends CondicionCuentaContable {
	private static final Logger log = Logger
			.getLogger(CondicionNoExisteCuentaContable.class);

	@Override
	public boolean CumpleCondicion(Connection conn, CuentaContable cuenta)
			throws AccountingEngineException {
		String qry = " SELECT	1 as EXISTE " + " FROM	tCuentas WITH(nolock) "
				+ " WHERE	nCuenta = ? ";
		PreparedStatement pStmnt = null;
		ResultSet rs = null;
		boolean existe = false;
		try {
			pStmnt = conn.prepareStatement(qry);
			pStmnt.setString(1, cuenta.getnCuenta());
			rs = pStmnt.executeQuery();
			existe = rs.next();
			if (existe)
				setMensaje("La cuenta " + cuenta.getnCuenta() + " ya existe.");
			return !existe;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null)
				try {
					pStmnt.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando PreparedStatemnt: " + e2, e2);
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando PreparedStatemnt: " + e2, e2);
				}
		}
	}

	@Override
	public boolean CumpleCondicion(Connection conn, int nMes,
			int aEjercicioFiscal, String cCentroContable)
			throws AccountingEngineException {
		return true;
	}

	@Override
	public void preEjecucion(Connection con) throws AccountingEngineException {

	}

	@Override
	public void preEjecucion(Connection con, int nMes, int aEjercicioFiscal,
			String cCentroContable) throws AccountingEngineException {

	}

}
