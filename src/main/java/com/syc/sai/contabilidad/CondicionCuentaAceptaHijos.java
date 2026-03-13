package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.List;

import com.syc.contable.AccountingEngineException;

public class CondicionCuentaAceptaHijos extends CondicionCuentaContable {

	@Override
	public void preEjecucion(Connection con) throws AccountingEngineException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean CumpleCondicion(Connection conn, CuentaContable cuenta)
			throws AccountingEngineException {

		boolean cumple = true;
		if (cuenta.getNivelCuenta() > 1) {

			CuentaContable aux = new CuentaContable();
			String nCtaPadre = CuentaContableManager.calculaCuentaPadre(cuenta);
			aux.setnCuenta(nCtaPadre);
			List<CuentaContable> l = CuentaContableManager
					.buscaCuentasContables(conn, aux);

			if (l == null || l.size() == 0) {
				setMensaje("No se encuentra el padre para la cuenta "
						+ cuenta.getnCuenta());
				return false;
			} else {
				aux = l.get(0);
				if ("S".equals(aux.getAplicacionCuenta())) {
					setMensaje("La cuenta padre "
							+ aux.getnCuenta()
							+ " es una cuenta de aplicacion. No pueden agregarse cuentas a esta.");
					return false;
				}
				if (!cuenta.getTipoCuenta().equals(aux.getTipoCuenta())) {
					setMensaje("Los tipos de cuenta de la cuenta  padre "
							+ aux.getnCuenta() + " y del hijo "
							+ cuenta.getnCuenta() + " son diferentes.");
					return false;
				}
				if (!cuenta.getTipoBalance().equals(aux.getTipoBalance())) {
					setMensaje("Los tipos de balance de la cuenta  padre "
							+ aux.getnCuenta() + " y del hijo "
							+ cuenta.getnCuenta() + " son diferentes.");
					return false;
				}
				if (!cuenta.getNaturalezaCuenta().equals(
						aux.getNaturalezaCuenta())) {
					setMensaje("Los tipos de cuenta de la cuenta  padre "
							+ aux.getnCuenta() + " y del hijo "
							+ cuenta.getnCuenta() + " son diferentes.");
					return false;
				}
			}
		} else {
			return cumple;
		}
		return cumple;
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
