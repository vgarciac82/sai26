package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.List;

import com.syc.contable.AccountingEngineException;

public class CondicionAbrirSinMesMenorAbierto extends CondicionCierreMes {

	@Override
	public void preEjecucion(Connection con, int nMes, int aEjercicioFiscal,
			String cCentroContable) throws AccountingEngineException {
	}

	@Override
	public boolean CumpleCondicion(Connection conn, int nMes,
			int aEjercicioFiscal, String cCentroContable)
			throws AccountingEngineException {
		boolean cumple = true;
		if (cCentroContable == null || "".equals(cCentroContable)) {
			MesContable m = new MesContable();
			m.setaEjercicioFiscal(aEjercicioFiscal);
			m.setMesAbierto("S");
			int max = MesContableManager.ultimoMesContableAbierto(conn, m);

			if (nMes < max) {
				m.setnMes(nMes);
				m.setaEjercicioFiscal(aEjercicioFiscal);
				List<MesContable> l = MesContableManager.estadoMesesAnteriores(
						conn, m);
				if (l.size() > 0) {
					setMensaje("No se puede abrir/cerrar todos los meses contables ya que existen meses abiertos menores al mes que desea abrir.");
					cumple = false;
				}
			}
		}
		return cumple;
	}
}
