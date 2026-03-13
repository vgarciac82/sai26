package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.syc.cfdi.utils.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import common.Logger;

public class MesContableBusinessLogic extends DataSourceManager {
	private static final Logger			log	= Logger.getLogger(MesContableBusinessLogic.class);
	private List<CondicionCierreMes>	condiciones;

	public MesContableBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public MesContableBusinessLogic(List<CondicionCierreMes> condiciones) {
		this.condiciones = condiciones;
	}

	public String abreMesContable(int nMes, int aEjercicioFiscal, String cCentroContable, String usuarioCierra, String cUnidadResponsable) {
		Connection conn = null;
		String msg = "";
		int cerrados = 0, abiertos = 0, a = 0, c = 0;
		boolean condicionesAprobadas = true;

		try {

			conn = getConnection();
			if (conn.getAutoCommit())
				conn.setAutoCommit(false);

			if (condiciones != null) {
				for (int i = 0; i < condiciones.size() && condicionesAprobadas; i++) {
					condicionesAprobadas = condicionesAprobadas && condiciones.get(i).CumpleCondicion(conn, nMes, aEjercicioFiscal, cCentroContable);
					msg = condiciones.get(i).getMensaje();
				}
				if (!condicionesAprobadas)
					return msg;
			}

			MesContable m = new MesContable(), m2 = new MesContable();
			m.setaEjercicioFiscal(aEjercicioFiscal);
			m.setcCentroContable(cCentroContable);
			m.setcUnidadResponsable(cUnidadResponsable);
			m.setMesAbierto("S");

			List<MesContable> meses = MesContableManager.buscaMesContable(conn, m);

			for (Iterator<MesContable> i = meses.iterator(); i.hasNext();) {
				m2 = i.next();
				c = m2.getnMes();
				cerrados += MesContableManager.cierraMesContable(conn, m2);
			}

			m.setnMes(nMes);
			m.setUsuarioCerro(usuarioCierra);
			m.setfCierre(new Date());

			abiertos += MesContableManager.abreMesContable(conn, m);
			a = m.getnMes();

			if (c == a + 1 || c == a - 1) {
				conn.commit();

				log.info("Se cerraron " + cerrados + " y se abrieron " + abiertos + " meses contables ");
			} else {
				conn.rollback();
				msg = "El mes que se pretende abrir no es consecutivo";
			}
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error("Problemas haciendo rollback " + e2.toString(), e2);
				}
			msg = "No se pudo cerrar el mes debido al siguiente error: " + e.toString();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando conexion a base de datos: " + e2.toString(), e2);
				}
		}

		return msg;
	}

	public String cierraMesContable(int nMes, int aEjercicioFiscal, String cCentroContable, String usuarioCierra, String cUnidadResponsable) {

		Connection conn = null;
		boolean condicionesAprobadas = true;
		String msg = "";

		try {
			conn = getConnection();
			if (conn.getAutoCommit())
				conn.setAutoCommit(false);

			if (condiciones != null) {
				for (int i = 0; i < condiciones.size() && condicionesAprobadas; i++) {
					condicionesAprobadas = condicionesAprobadas && condiciones.get(i).CumpleCondicion(conn, nMes, aEjercicioFiscal, cCentroContable);
					msg = condiciones.get(i).getMensaje();
				}
				if (!condicionesAprobadas)
					return msg;
			}

			MesContable m = new MesContable();
			m.setnMes(nMes);
			m.setaEjercicioFiscal(aEjercicioFiscal);
			m.setcCentroContable(cCentroContable);
			m.setcUnidadResponsable(cUnidadResponsable);
			m.setfCierre(new Date(System.currentTimeMillis()));
			m.setMesAbierto("N");
			m.setUsuarioCerro(usuarioCierra);

			int r = MesContableManager.cierraMesContable(conn, m);
			m.setnMes(m.getnMes() + 1);
			r += MesContableManager.abreMesContable(conn, m);
			conn.commit();

			log.info("Se alteraron " + r + " meses contables");
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error("Problemas haciendo rollback " + e2.toString(), e2);
				}
			msg = "No se pudo cerrar el mes debido al siguiente error: " + e.toString();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando conexion a base de datos: " + e2.toString(), e2);
				}
		}

		return msg;

	}

	/**
	 * Cambia la feha de aplicacion por la fecha del dia en un pago
	 * 
	 * @param tipoPago
	 *            Tipo de pago
	 *            (DIVERSO;FEDERALIZADO;OBRA;DIRECTO;RELACIONGASTOS)
	 * @param nFolioPago
	 *            Folio del pago a actualizar
	 * @return numero de registros afectados.
	 */
	public int cambiaFechaAplicacion(String tipoPago, int nFolioPago) throws Exception {
		Connection conn = null;
		try {

			conn = getConnection();
			int afectados = MesContableManager.cambiaFechaAplicacion(conn, tipoPago, nFolioPago);
			conn.commit();
			return afectados;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback " + e2, e2);
				}

			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}
}
