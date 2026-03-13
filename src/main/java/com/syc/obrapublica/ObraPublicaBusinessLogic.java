package com.syc.obrapublica;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.ws.inventario.RespuestaWS;
import com.syc.ws.inventario.WSManager;
import com.syc.ws.obrapublica.core.EstimacionObra;

public class ObraPublicaBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(ObraPublicaBusinessLogic.class);

	public ObraPublicaBusinessLogic() {
		super();
	}

	public boolean changeStatus(String folioSAI, int status) throws GestionException {
		Connection conn = null;
		boolean success = false;

		try {
			conn = getConnection();
			success = ObraPublicaManager.changeStatusContrato(conn, folioSAI, status);
			conn.commit();
			return success;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.error("Problemas realizando rollback: " + e2.toString(), e2);
				}
			throw new GestionException(e);
		} finally {
			try {
				CloseObject.closeObject(conn, false);
			} catch (Exception e) {
			}
			conn = null;
		}
	}

	public String getStatusContrato(String folioSAI) throws GestionException {
		Connection conn = null;
		String statusContrato = null;

		try {
			conn = getConnection();
			statusContrato = ObraPublicaManager.getStatusContrato(conn, folioSAI);
			return statusContrato;
		} catch (Exception e) {
			throw new GestionException(e);
		} finally {
			try {
				CloseObject.closeObject(conn, false);
			} catch (Exception e) {
			}
			conn = null;
		}
	}

	public int actualizaEncabezadoConvModificatorio(String folioConvenio, String fFechaIniContr, String fFechaFinContr, double mMonto, double nPorceIVA, double mMontoConIVA, String cDescripcionConvenio, double mMontoIncremento, double mMontoIncrementoIVA, int nFolioOPConvHeader) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			String convenioAutorizado = ObraPublicaManager.esConvenioModificatorioAutorizado(conn, nFolioOPConvHeader);
			boolean tieneIncremento = ObraPublicaManager.cmTieneIncrementoMonto(conn, String.valueOf( nFolioOPConvHeader ));
			if ("S".equalsIgnoreCase(convenioAutorizado) && tieneIncremento) {
				
				int nuevoIDConvenio = ObraPublicaManager.copiaConvenioModificatorioEncabezado(conn, nFolioOPConvHeader);
				ObraPublicaManager.setConvenioModificatorioEnCaptura(conn, "N", nFolioOPConvHeader);
				nFolioOPConvHeader = nuevoIDConvenio;
			}

			ObraPublicaManager.actualizaEncabezadoConvenioModificatorio(conn, folioConvenio, fFechaIniContr, fFechaFinContr, mMonto, nPorceIVA, mMontoConIVA, cDescripcionConvenio, mMontoIncremento, mMontoIncrementoIVA, nFolioOPConvHeader);
			conn.commit();
			return nFolioOPConvHeader;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback: " + e, e);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}
//IRD 20131121	RO-0009
	public String actualizaEncabezadoPagoPasivo(double mPagoPasivo, double ivaPagPasF, double mTotalPagoPasivo, String cMotivoPagoPasivo, String nFolioOPPagPasHeader, String folioSAI, String fFechaAplicacion, String aEjercicioFiscal) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			
			nFolioOPPagPasHeader = ObraPublicaManager.actualizaEncabezadoPagoPasivo(conn, mPagoPasivo,  ivaPagPasF,  mTotalPagoPasivo,  cMotivoPagoPasivo,  nFolioOPPagPasHeader, folioSAI, fFechaAplicacion, aEjercicioFiscal);
			conn.commit();
			return nFolioOPPagPasHeader;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback: " + e, e);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}

	//MLR 20131217	RO-0010
		public String actualizaEncabezadoPlurianual(double mPlurianual, double ivaPlurianual, double mTotalPlurianual,  String nFolioOPPlurianualHeader, String folioSAI, String fFechaAplicacion, String aEjercicioFiscal) throws Exception {
			Connection conn = null;
			try {
				conn = getConnection();
				
				nFolioOPPlurianualHeader = ObraPublicaManager.actualizaEncabezadoPlurianual(conn, mPlurianual,  ivaPlurianual,  mTotalPlurianual,  nFolioOPPlurianualHeader, folioSAI, fFechaAplicacion, aEjercicioFiscal);
				conn.commit();
				return nFolioOPPlurianualHeader;
			} catch (Exception e) {
				if (conn != null)
					try {
						conn.rollback();
					} catch (Exception e2) {
						log.warn("Problemas realizando rollback: " + e, e);
					}
				throw e;
			} finally {
				CloseObject.closeObject(conn, false);
			}

		}
	public Map<String, String> loadCMInfoMap(int folioSAI) throws Exception {
		Map<String, String> data = null;
		Connection conn = null;

		try {
			conn = getConnection();
			data = ObraPublicaManager.loadCMInfoMap(conn, folioSAI);
			return data;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
	
	public boolean registraEstimacionObra(EstimacionObra estimacion,Usuario u) throws Exception {
		Connection conn = null;

		try {
			conn = getConnection();
			ObraPublicaManager.registraEstimacionObra(conn, estimacion);
			
			conn.commit();
			return true;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn(e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn);
		}
		
	}
	
	public static void enviaEstimacion(Connection conn, String strCaNoContrarrecibo ) throws Exception{
		String folioSAI = "";
		try {		
			
			folioSAI = ObraPublicaManager.esObraEnProceso(conn, strCaNoContrarrecibo);
			
			if (!folioSAI.equals( "" )) {
				RespuestaWS respuesta = WSManager.generatePublicWorkPartial(conn, strCaNoContrarrecibo, folioSAI);
			} else{
				log.info("La estimación de ancicipo no se reporta al sistema de inventario");
			}					
			
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn(e2);
				}
			throw e;
		}
	}
	
}
