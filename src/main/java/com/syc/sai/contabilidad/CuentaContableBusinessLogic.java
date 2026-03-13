package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.contable.AccountingEngineException;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabms;
import com.syc.sai.contabilidad.polizamanual.EventoRelacion;
import com.syc.sai.contabilidad.polizamanual.GrupoEvento;
import com.syc.sai.contabilidad.polizamanual.SubGrupoEvento;

public class CuentaContableBusinessLogic extends DataSourceManager {
	Logger log = Logger.getLogger(CuentaContableBusinessLogic.class);
	Map<String, List<CondicionCuentaContable>> map;

	public CuentaContableBusinessLogic() {
		super();
		map = new LinkedHashMap<String, List<CondicionCuentaContable>>();
		List<CondicionCuentaContable> altasLst = new ArrayList<CondicionCuentaContable>();
		altasLst.add(new CondicionNoExisteCuentaContable());
		altasLst.add(new CondicionExistePadre());
		altasLst.add(new CondicionCuentaAceptaHijos());
		map.put("ALTAS", altasLst);

		List<CondicionCuentaContable> actualizarLst = new ArrayList<CondicionCuentaContable>();
		actualizarLst.add(new CondicionCuentaSinHijos());
		actualizarLst.add(new CondicionCuentaSinRegistros());
		map.put("ACTUALIZAR", actualizarLst);
		map.put("ELIMINAR", actualizarLst);
	}

	public String actualizaCuenta(CuentaContable c) {
		Connection conn = null;
		String msg = null;
		try {
			conn = getConnection();
			List<CondicionCuentaContable> condiciones = map.get("ACTUALIZAR");

			for (Iterator<CondicionCuentaContable> i = condiciones.iterator(); i
					.hasNext();) {
				CondicionCuentaContable cond = i.next();
				if (!cond.CumpleCondicion(conn, c)) {
					msg = cond.getMensaje();
					break;
				}
			}
			if (msg != null)
				return msg;

			int r = CuentaContableManager.updateCuentaContable(conn, c);
			msg = "Se actualizaron exitosamente " + r + " cuenta(s) ";
			conn.commit();
		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error(
							"Error realizando rollback a la conexion. "
									+ e2.toString(), e2);
				}
			msg = "Error insertando cuenta." + e.toString();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e2) {
					log.error(
							"Error cerrando conexion a base de datos "
									+ e2.toString(), e2);
				}
		}
		return msg;
	}
	
	public String actualizaCuenta(CuentaContable c,boolean soloDetalle) {
		Connection conn = null;
		String msg = null;
		try {
			conn = getConnection();
			/*List<CondicionCuentaContable> condiciones = map.get("ACTUALIZAR");

			for (Iterator<CondicionCuentaContable> i = condiciones.iterator(); i
					.hasNext();) {
				CondicionCuentaContable cond = i.next();
				if (!cond.CumpleCondicion(conn, c)) {
					msg = cond.getMensaje();
					break;
				}
			}
			if (msg != null)
				return msg;*/

			int r = CuentaContableManager.updateCuentaContable(conn, c);
			msg = "Se actualizaron exitosamente " + r + " cuenta(s) ";
			conn.commit();
		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error(
							"Error realizando rollback a la conexion. "
									+ e2.toString(), e2);
				}
			msg = "Error insertando cuenta." + e.toString();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e2) {
					log.error(
							"Error cerrando conexion a base de datos "
									+ e2.toString(), e2);
				}
		}
		return msg;
	}

	public List<CuentaContable> buscaCuentas(CuentaContable cuenta)
			throws AccountingEngineException {
		Connection conn = null;
		try { 
			conn = getConnection();			
			return CuentaContableManager.buscaCuentasContables(conn, cuenta);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}	
	public ArrayList <CuentaContable> creaArbol()
	throws AccountingEngineException {
		Connection conn = null;
		try { 
			conn = getConnection();
			
			return CuentaContableManager.obtenCuentasArbol(conn);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

}

	public List<CuentaContable> buscaCuentasDeAplicacion(CuentaContable cuenta)
			throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.buscaCuentasDeAplicacion(conn, cuenta);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}
	
	//*********************** Polizas Eventos Manuales 2014 *********************
	// Ing J. Luis DR
	public List<GrupoEvento> autoCompletaGrupoEvento(String aux)
		throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.autoCompletaGrupoEvento(conn, aux);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}
	
	public List<SubGrupoEvento> autoCompletaSubGrupoEvento(String nGrupo, String aux) throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.autoCompletaSubGrupoEvento(conn, nGrupo, aux);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}
	
	public List<EventoRelacion> autoCompletaEventoRelacion(String nGrupo, String nSubGrupo, String nEvento)
		throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.autoCompletaEventoRelacion(conn, nGrupo, nSubGrupo, nEvento);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}
	
	}
	
	public List<CatalogoCabms> autoCompletaCABMS(String nGrupo, String nSubGrupo, String nEvento, String nCABMS)
		throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.autoCompletaCABMS(conn, nGrupo, nSubGrupo, nEvento, nCABMS);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}
	
	}
	public List<CatalogoCabms> selectCABMS(String type, String id)
		throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.selectCABMS(conn, type,  id);
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}
	
	}
//************************************************************************************************
	
	public boolean cuentaBloqueada(String nCuenta, int nMes,
			String cCentroContable, String operacion)
			throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.cuentaBloqueada(conn, nCuenta, nMes,
					cCentroContable, operacion);
		} catch (Exception e) {
			throw new AccountingEngineException(e.toString(), e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("Error cerrando conexion a la base de datos " + e,
							e);
				}
			conn = null;
		}
	}

	public String[] cuentasBloqueadas(String cuentasCargo, String cuentasAbono,
			String cCentroContable, int nMes) throws AccountingEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return CuentaContableManager.cuentasBloqueadas(conn, cuentasCargo,
					cuentasAbono, cCentroContable, nMes);
		} catch (Exception e) {
			throw new AccountingEngineException(e.toString(), e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("Error cerrando conexion a la base de datos " + e,
							e);
				}
			conn = null;
		}
	}

	public String eliminaCuenta(CuentaContable c) {
		Connection conn = null;
		String msg = null;
		try {
			conn = getConnection();
			List<CondicionCuentaContable> condiciones = map.get("ELIMINAR");

			for (Iterator<CondicionCuentaContable> i = condiciones.iterator(); i
					.hasNext();) {
				CondicionCuentaContable cond = i.next();
				if (!cond.CumpleCondicion(conn, c)) {
					msg = cond.getMensaje();
					break;
				}
			}
			if (msg != null)
				return msg;

			int r = CuentaContableManager.eliminaCuentaContable(conn, c);
			msg = "Se eliminaron exitosamente " + r + " cuenta(s) ";
			conn.commit();
		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error(
							"Error realizando rollback a la conexion. "
									+ e2.toString(), e2);
				}
			msg = "Error insertando cuenta." + e.toString();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e2) {
					log.error(
							"Error cerrando conexion a base de datos "
									+ e2.toString(), e2);
				}
		}
		return msg;
	}

	public String insertaCuenta(CuentaContable c) {
		Connection conn = null;
		String msg = null;
		String msgPst = "";
		try {
			conn = getConnection();
			List<CondicionCuentaContable> condiciones = map.get("ALTAS");

			for (Iterator<CondicionCuentaContable> i = condiciones.iterator(); i
					.hasNext();) {
				CondicionCuentaContable cond = i.next();
				if (!cond.CumpleCondicion(conn, c)) {
					msg = cond.getMensaje();
					break;
				}
			}
			if (msg != null)
				return msg;
			if (c.getnCuentaPadre() == null || "".equals(c.getnCuentaPadre()))
				c.setnCuentaPadre(CuentaContableManager.calculaCuentaPadre(c));
			/*
			 * Calcula el orden. 1) Si tiene hermanos, el orden y el nivel de la
			 * balanza seran el mismo. 2) Si no tiene hermanos el orden de la
			 * balanza sera el del padre y el nivel sera uno mas que el del
			 * padre
			 * 
			 * Si la cuenta es de primer nivel debe calcularse el orden y el
			 * nivel.
			 */

			CuentaContable m = new CuentaContable();
			m.setnCuentaPadre(c.getnCuentaPadre());

			if (c.getNivelCuenta() > 1) {
				List<CuentaContable> l = null;
				l = CuentaContableManager.buscaCuentasContables(conn, m);
				boolean sinHermanos = false;
				if (l == null || l.size() == 0) {
					m.setnCuentaPadre(null);
					m.setnCuenta(c.getnCuentaPadre());
					l = CuentaContableManager.buscaCuentasContables(conn, m);
					sinHermanos = true;
				}
				if (l != null && l.size() > 0) {
					c.setnNivelBalanza(l.get(0).getnNivelBalanza()
							+ (sinHermanos ? 1 : 0));
					c.setnOrdenBalanza(l.get(0).getnOrdenBalanza());
					l.clear();
					l = null;
				}
			} else {
				msgPst = "<br>Contacte al administrador para definir el orden en la balanza y el nivel";
			}

			int r = CuentaContableManager.insertCuentaContable(conn, c);
			msg = "Se insertaron exitosamente " + r + " cuenta(s) " + msgPst;
			conn.commit();
		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error(
							"Error realizando rollback a la conexion. "
									+ e2.toString(), e2);
				}
			msg = "Error insertando cuenta." + e.toString();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e2) {
					log.error(
							"Error cerrando conexion a base de datos "
									+ e2.toString(), e2);
				}
		}
		return msg;
	}
	
	
	public int[] getNivelOrdenBalanza(String nCuenta){
		
		Connection conn=null;
		int NivelOrdenBalanza[]={4,5};
		
		
		try
		{
			conn = getConnection();
			NivelOrdenBalanza=CuentaContableManager.obtenOrdenNivelBalanza(conn,nCuenta);
			
			
		}catch(Exception e){;}
		
	
		return NivelOrdenBalanza;
	}
	
}
