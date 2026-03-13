package com.syc.sai.contabilidad.caja;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.core.SolicitudCajaFirmaElectronica;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;

public class CancelaCajaBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(CancelaCajaBusinessLogic.class);

	public CancelaCajaBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public List<String> cancelaSolicitudes(String[] cancel) throws Exception {

		List<String> result = new ArrayList<String>();
		Connection conn = null;
		String msg = "";
		CajaBusinessLogic cbl = new CajaBusinessLogic();
		String datosCancelcion[] = new String[3];

		try {

			for (int i = 0; i < cancel.length; i++) {
				try {
					msg = "";
					conn = getConnection();
					msg += "Solicitud No Presupuestal[" + cancel[i] + "] ";
					AccountingEngine motorContble = new AccountingEngine();
					// String fechaCancelacion=cbl.readfCancelacion(cancel[i]);
					motorContble.cancelAccountingApplication(conn, "CAJA", cancel[i], "tCajaEncabezado", "tCajaDetalle", "nFolioCaja", cbl.readfCancelacion(cancel[i]));
					CajaManager.actualizaFolioSICOP(conn, cancel[i]);
					CajaManager.actualizaFolioComprobacion(conn, cancel[i]);
					datosCancelcion = cbl.PolizaCancelacion(conn, cancel[i]);
					conn.commit();

					msg += "CANCELADA exitosamente" + " Poliza de Cancelacion No.:" + datosCancelcion[0] + " Tipo de Poliza:" + datosCancelcion[1] + " Con Fecha de Aplicaciòn:" + datosCancelcion[3] + " " + datosCancelcion[4];

				} catch (Exception e) {
					log.error(e, e);
					msg += "Ocurrio el siguiente error al cancelar: " + e.getMessage();
					if (conn != null)
						try {
							conn.rollback();
						} catch (Exception e2) {
							log.error("Ocurrio el siguiente error al hacer rollback: " + e2, e2);
						}
				} finally {
					CloseObject.closeObject(conn, false);
				}
				result.add(msg);
			}
		} catch (Exception e) {
			log.error(e, e);
			result.add("Ocurrio el siguiente error mientras se cancelaban las solicitudes: " + e);
		}

		return result;
	} // fin metodo cancela SNP

	public void cancelaSolicitudFirmaElectronica(String folio, String cancelReason, Usuario u) throws Exception {
		Connection conn = null;
		
		try {
			conn = getConnection();
			CajaManager.cancelaSolicitudFirmaElectronica(conn,folio, cancelReason);
			
			SolicitudFirmaElectronica scfe = new SolicitudCajaFirmaElectronica();
			scfe.setHeader("tCajaEncabezado");
			scfe.setDetail("tCajaDetalle");
			scfe.setDocument("CAJA");
			scfe.setField("nFolioCaja");
			scfe.setIdField( Integer.parseInt(folio));
			scfe.setUsuario(u);
			
			FirmaElectronicaManager.avanzaEstatusSICOP(conn, scfe, SolicitudFirmaElectronica.SOLICITUD_CANCELADA );
			scfe.onCancelaTramite(conn,cancelReason);
			conn.commit();
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn(e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}

}
