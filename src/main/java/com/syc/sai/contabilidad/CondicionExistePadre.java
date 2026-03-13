package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jfree.util.Log;

import com.syc.contable.AccountingEngineException;

public class CondicionExistePadre extends CondicionCuentaContable {

	@Override
	public void preEjecucion(Connection con) throws AccountingEngineException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean CumpleCondicion(Connection conn, CuentaContable cuenta)
			throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		ResultSet rs = null;
		String qry = "SELECT	* " + " FROM	tCuentas WITH(nolock) "
				+ " WHERE	NivelCuenta = ? " + "		AND	nCuenta = ? ";
		boolean existePadre = false;
		try {
			if (cuenta.getNivelCuenta() == 1)
				return true;
			else {
				pStmnt = conn.prepareStatement(qry);
				pStmnt.setInt(1, cuenta.getNivelCuenta() - 1);
				pStmnt.setString(2,
						CuentaContableManager.calculaCuentaPadre(cuenta));
				rs = pStmnt.executeQuery();

				if (rs.next())
					existePadre = true;
				else {
					existePadre = false;
					setMensaje("No existe la cuenta padre para la cuenta "
							+ cuenta.getnCuenta());
				}
				return existePadre;
			}
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null)
				try {
					pStmnt.close();
				} catch (Exception e2) {
					Log.warn("Problemas cerrando PreparedStatemnt"
							+ e2.toString());
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e2) {
					Log.warn("Problemas cerrando ResultSet" + e2.toString());
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
