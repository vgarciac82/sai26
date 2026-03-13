package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.syc.contable.AccountingEngineException;
import common.Logger;

public class CondicionCuentaSinRegistros extends CondicionCuentaContable {

	Logger log = Logger.getLogger(CondicionCuentaSinRegistros.class);

	@Override
	public void preEjecucion(Connection con) throws AccountingEngineException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean CumpleCondicion(Connection conn, CuentaContable cuenta)
			throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		ResultSet rs = null;
		String qry = "SELECT	TOP 1 nCuenta "
				+ " FROM	tmovimiento WITH(nolock) " + " WHERE	nCuenta = ? ";
		boolean cumple = true;
		try {
			pStmnt = conn.prepareStatement(qry);
			pStmnt.setString(1, cuenta.getnCuenta());
			rs = pStmnt.executeQuery();

			if (rs.next()) {
				setMensaje("No puede modificar la cuenta ya que se cuenta con registros contables previos");
				cumple = false;
			}

			return cumple;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null)
				try {
					pStmnt.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando PreparedStatement");
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando ResultSet");
				}
		}
	}

	@Override
	public void preEjecucion(Connection con, int nMes, int aEjercicioFiscal,
			String cCentroContable) throws AccountingEngineException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean CumpleCondicion(Connection conn, int nMes,
			int aEjercicioFiscal, String cCentroContable)
			throws AccountingEngineException {
		// TODO Auto-generated method stub
		return false;
	}

}
