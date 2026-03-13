package com.syc.sai.ejercido.pagado;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.contable.AccountingEngine;
import com.syc.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.Ejercido;
import com.syc.ejercido.pagado.Pago;
import com.syc.gestion.util.Condicion;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.ingresos.core.ConsolidacionRGManager;

/**
 * Se encarga de realizar el proceso de Ejercido/Pagado segun los estatus de los
 * sitemas SICOP y SIAFF
 * 
 * @author Vicente Garcia Carrillo
 * @version 1.0
 */

public class EjercidoPagadoBusinessLogic extends DataSourceManager {

	private static final String	EJERCIDO	= "noEjercido";
	private static final String	PAGADO		= "noPagado";
	private static final Logger	log			= Logger.getLogger(EjercidoPagadoBusinessLogic.class);

	/**
	 * Constructor.
	 */
	public EjercidoPagadoBusinessLogic() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param jniName
	 *            Nombre del JNDI para la base de datos.
	 */
	public EjercidoPagadoBusinessLogic(String jniName) {
		super.init(jniName);
	}

	/**
	 * Aplica el ejercido pagado
	 * 
	 * @throws Exception
	 */
	public void ejercidoPagadoIntegracion() throws Exception {
		log.info("Inicio de Ejercido Pagado Laudos");
		Connection conn = null;
		List<Condicion> condiciones = new ArrayList<Condicion>();

		log.trace("Especificando condiciones.");
		condiciones.add(new Condicion("caNoContrarrecibo", "<>", "''"));
		condiciones.add(new Condicion("integracion", "<>", "'0'"));
		condiciones.add(new Condicion("cTipoPago", "=", "'RELACIONGASTOS' "));
		condiciones.add(new Condicion("laudos", "=", "'1'"));

		log.trace("Solicitando pagos a procesar.");
		List<Pago> pagosProcesar = obtenerPagosProcesar(condiciones);
		log.trace("Se obtubieron " + pagosProcesar.size() + " pagos por procesar.");

		try {
			log.trace("Obteniendo conexion a base de datos. Inicia transaccion.");
			conn = getConnection();

			log.trace("Iterando lista de pagos.");
			for (Iterator<Pago> i = pagosProcesar.iterator(); i.hasNext();) {
				Pago pago = i.next();

				log.debug(String.format("Procesando pago. Tipo [%s] Folio [%d] Estatus Ejercido [%s] Estatus Pagado [%s] Monto[%f]", pago.getTipoPago(), pago.getFolioPagado(), pago.getEstatusEjercido(), pago.getEstatusPagado(), pago.getImporteNeto().floatValue()));

				/*
				 * Las solicitudes de laudos devengadas seran aplicadas
				 * manualmente.
				 */
				if (!EjercidoPagadoManager.esLaudoDevengado(conn, pago.getIntegracion())) {

					if (EjercidoPagadoBusinessLogic.EJERCIDO.equalsIgnoreCase(pago.getEstatusEjercido())) {
						/* Inicio de la aplicacion del ejercido. */

						/*
						 * Primer validacion. La integracion debe existir en las
						 * tablas de SICOP/SIAFF
						 */
						if (EjercidoPagadoManager.existeIntegracionSICOP(conn, pago.getIntegracion())) {
							/*
							 * Segunda validacion. La suma de las RG integradas
							 * debe ser igual a la integradora en SICOP.
							 */
							if (EjercidoPagadoManager.montosCorrectos(conn, pago.getIntegracion())) {
								List<String> montosIncorrectos = EjercidoPagadoManager.validaDetallePago(conn, pago.getIntegracion());
								if (montosIncorrectos.size() == 0) {
									/*
									 * Las validaciones son correctas. Se ejerce
									 * el pago.
									 */
									List<Ejercido> ejercer = EjercidoPagadoManager.insertaInfoEjercido(conn, pago.getIntegracion(), "admin", "DI");
									AccountingEngine ae = new AccountingEngine();
									ae.setValidaInsuficienciaDeSaldo(true);
									for (Iterator<Ejercido> itEjer = ejercer.iterator(); itEjer.hasNext();) {
										Ejercido ejercidoAE = itEjer.next();
										ae.makeAccountingApplication(conn, "EJERCIDO", String.valueOf(ejercidoAE.getEncabezado().getFolioEjercido()), "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido");
									}
								} else {
									for (Iterator<String> it = montosIncorrectos.iterator(); it.hasNext();) {
										EjercidoPagadoManager.insertaLog(conn, pago.getTipoPago(), pago.getFolioPagado(), pago.getIntegracion(), "-1", Util.getTodayESMX(), "", pago.getImporteNeto().doubleValue(), "EJERCIDO", it.next());
									}
									continue;
								}
							} else {
								EjercidoPagadoManager.insertaLog(conn, pago.getTipoPago(), pago.getFolioPagado(), pago.getIntegracion(), "-1", Util.getTodayESMX(), "", pago.getImporteNeto().doubleValue(), "EJERCIDO", "Los montos entre la integradora en SAI [" + pago.getIntegracion() + "] Total["
									+ pago.getImporteNeto() + "] no corresponde a la registrada en las tablas de SICOP/SIAF Total[" + EjercidoPagadoManager.obtenMontoIntegracionSicop(conn, pago.getIntegracion()));
								continue;
							}
						} else {
							EjercidoPagadoManager.insertaLog(conn, pago.getTipoPago(), pago.getFolioPagado(), pago.getIntegracion(), "-1", Util.getTodayESMX(), "", pago.getImporteNeto().doubleValue(), "EJERCIDO", "No se encontro la integracion [" + pago.getIntegracion()
								+ " en las tablas de SICOP/SIAFF");
							continue;
						}

					}
					if (EjercidoPagadoBusinessLogic.PAGADO.equalsIgnoreCase(pago.getEstatusPagado()) && "pagada".equalsIgnoreCase(EjercidoPagadoManager.getEstatusIntegracion(conn, pago.getIntegracion()))) {
						ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, pago.getIntegracion(), "");
						List<Pagado> pagar = EjercidoPagadoManager.insertaInfoPagado(conn, pago.getIntegracion(), "admin", "EG");
						AccountingEngine ae = new AccountingEngine();
						ae.setValidaInsuficienciaDeSaldo(true);
						for (Iterator<Pagado> itPagar = pagar.iterator(); itPagar.hasNext();) {
							Pagado pagadoAE = itPagar.next();
							ae.makeAccountingApplication(conn, "PAGADO", String.valueOf(pagadoAE.getEncabezado().getFolioPagado()), "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado");
						}
					}
				} else {
					if ( "ENVIO BANCO".equalsIgnoreCase(EjercidoPagadoManager.getEstatusIntegracion(conn, pago.getIntegracion())) || "PAGADA".equalsIgnoreCase(EjercidoPagadoManager.getEstatusIntegracion(conn, pago.getIntegracion())) )
						ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, pago.getIntegracion(), "");
				}
			}
			log.trace("Terminando transaccion.");
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn("Problemas realizando rollback " + e2);
			}
			throw e;
		} finally {
			log.trace("Cerrando conexion a base de datos. Operacion terminada");
			CloseObject.closeObject(conn);
		}
	}

	/**
	 * Aplica el ejercido pagado
	 * 
	 * @throws Exception
	 */
	public void ejercidoPagadoPenas() throws Exception {
		log.info("Inicio de Ejercido Pagado Penas");
		Connection conn = null;
		List<Condicion> condiciones = new ArrayList<Condicion>();

		log.trace("Especificando condiciones.");
		condiciones.add(new Condicion("caNoContrarrecibo", "<>", "''"));
		condiciones.add(new Condicion("integracion", "<>", "'0'"));
		condiciones.add(new Condicion("cTipoPago", "=", "'PAGOPENASCONV' "));

		log.trace("Solicitando pagos a procesar.");
		List<Pago> pagosProcesar = obtenerPagosProcesar(condiciones);
		log.trace("Se obtubieron " + pagosProcesar.size() + " pagos por procesar.");

		try {
			log.trace("Obteniendo conexion a base de datos. Inicia transaccion.");
			conn = getConnection();

			log.trace("Iterando lista de pagos.");
			for (Iterator<Pago> i = pagosProcesar.iterator(); i.hasNext();) {
				Pago pago = i.next();

				log.debug(String.format("Procesando pago. Tipo [%s] Folio [%d] Estatus Ejercido [%s] Estatus Pagado [%s] Monto[%f]", pago.getTipoPago(), pago.getFolioPagado(), pago.getEstatusEjercido(), pago.getEstatusPagado(), pago.getImporteNeto().floatValue()));


					if (EjercidoPagadoBusinessLogic.EJERCIDO.equalsIgnoreCase(pago.getEstatusEjercido())) {
						/* Inicio de la aplicacion del ejercido. */

						/*
						 * Primer validacion. La integracion debe existir en las
						 * tablas de SICOP/SIAFF
						 */
						if (EjercidoPagadoManager.existeIntegracionSICOP(conn, pago.getIntegracion())) {
							/*
							 * Segunda validacion. La suma de las  integradas
							 * debe ser igual a la integradora en SICOP.
							 */
							if (EjercidoPagadoManager.montosCorrectos(conn, pago.getIntegracion())) {
								List<String> montosIncorrectos = EjercidoPagadoManager.validaDetallePago(conn, pago.getIntegracion());
								if (montosIncorrectos.size() == 0) {
									/*
									 * Las validaciones son correctas. Se ejerce
									 * el pago.
									 */
									List<Ejercido> ejercer = EjercidoPagadoManager.insertaInfoEjercido(conn, pago.getIntegracion(), "admin", "DI");
									AccountingEngine ae = new AccountingEngine();
									ae.setValidaInsuficienciaDeSaldo(true);
									for (Iterator<Ejercido> itEjer = ejercer.iterator(); itEjer.hasNext();) {
										Ejercido ejercidoAE = itEjer.next();
										ae.makeAccountingApplication(conn, "EJERCIDO", String.valueOf(ejercidoAE.getEncabezado().getFolioEjercido()), "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido");
									}
								} else {
									for (Iterator<String> it = montosIncorrectos.iterator(); it.hasNext();) {
										EjercidoPagadoManager.insertaLog(conn, pago.getTipoPago(), pago.getFolioPagado(), pago.getIntegracion(), "-1", Util.getTodayESMX(), "", pago.getImporteNeto().doubleValue(), "EJERCIDO", it.next());
									}
									continue;
								}
							} else {
								EjercidoPagadoManager.insertaLog(conn, pago.getTipoPago(), pago.getFolioPagado(), pago.getIntegracion(), "-1", Util.getTodayESMX(), "", pago.getImporteNeto().doubleValue(), "EJERCIDO", "Los montos entre la integradora en SAI [" + pago.getIntegracion() + "] Total["
									+ pago.getImporteNeto() + "] no corresponde a la registrada en las tablas de SICOP/SIAF Total[" + EjercidoPagadoManager.obtenMontoIntegracionSicop(conn, pago.getIntegracion()));
								continue;
							}
						} else {
							EjercidoPagadoManager.insertaLog(conn, pago.getTipoPago(), pago.getFolioPagado(), pago.getIntegracion(), "-1", Util.getTodayESMX(), "", pago.getImporteNeto().doubleValue(), "EJERCIDO", "No se encontro la integracion [" + pago.getIntegracion()
								+ " en las tablas de SICOP/SIAFF");
							continue;
						}

					}
					if (EjercidoPagadoBusinessLogic.PAGADO.equalsIgnoreCase(pago.getEstatusPagado()) && "pagada".equalsIgnoreCase(EjercidoPagadoManager.getEstatusIntegracion(conn, pago.getIntegracion()))) {
						List<Pagado> pagar = EjercidoPagadoManager.insertaInfoPagado(conn, pago.getIntegracion(), "admin", "EG");
						AccountingEngine ae = new AccountingEngine();
						ae.setValidaInsuficienciaDeSaldo(true);
						for (Iterator<Pagado> itPagar = pagar.iterator(); itPagar.hasNext();) {
							Pagado pagadoAE = itPagar.next();
							ae.makeAccountingApplication(conn, "PAGADO", String.valueOf(pagadoAE.getEncabezado().getFolioPagado()), "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado");
						}
					}
			}
			log.trace("Terminando transaccion.");
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				log.warn("Problemas realizando rollback " + e2);
			}
			throw e;
		} finally {
			log.trace("Cerrando conexion a base de datos. Operacion terminada");
			CloseObject.closeObject(conn);
		}
	}
	/**
	 * Obtiene la lista de pagos a procesar segun las condiciones recibidas.
	 * 
	 * @param condiciones
	 *            Condiciones que deben cumplir los pagos.
	 * @return Listado de los pagos que cumplen con las condiciones.
	 * @throws Exception
	 */
	private List<Pago> obtenerPagosProcesar(List<Condicion> condiciones) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return EjercidoPagadoManager.obtenerPagosProcesar(conn, condiciones);
		} finally {
			CloseObject.closeObject(conn);
		}
	}

}
