package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.Calendar;

import com.syc.contable.AccountingEngineException;

public class CondicionCierreMenor extends CondicionCierreMes {

	@Override
	public void preEjecucion(Connection con, int nMes, int aEjercicioFiscal,
			String cCentroContable) throws AccountingEngineException {

	}

	@Override
	public boolean CumpleCondicion(Connection conn, int nMes,
			int aEjercicioFiscal, String cCentroContable)
			throws AccountingEngineException {
		Calendar hoy = Calendar.getInstance();
		int mesActual = hoy.get(Calendar.MONTH) + 1;
		boolean r = true;
		setMensaje("");
		if (nMes > mesActual && nMes != 12 ) {
			setMensaje("No puede cerrar el mes actual mientras no ha terminado");
			r = false;
		}
		/*
		if (nMes < (mesActual - 1)) {
			setMensaje("Solo puede cerrar el mes inmediato anterior");
			r = false;
		}
		*/
		return r;
	}

}
