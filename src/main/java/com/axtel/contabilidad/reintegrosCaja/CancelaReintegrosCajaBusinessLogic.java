package com.axtel.contabilidad.reintegrosCaja;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.axtel.contabilidad.reintegrosCaja.core.SolicitudReintegroCajaFirmaElectronica;
import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CancelaReintegrosCajaBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CancelaReintegrosCajaBusinessLogic.class);

    public CancelaReintegrosCajaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public List<String> cancelaSolicitudes(String[] cancel) throws Exception {
        List<String> result = new ArrayList<String>();
        Connection conn = null;
        String msg = "";
        ReintegrosCajaBusinessLogic rcbl = new ReintegrosCajaBusinessLogic(GestionInterface.ATT_CONEXION);
        String[] datosCancelcionAUT = new String[3];
        String[] datosCancelcion = new String[3];
        try {
            for (int i = 0; i < cancel.length; i++) {
                try {
                    msg = "";
                    conn = getConnection();
                    msg += "Solicitud de Reintegro de Caja [" + cancel[i] + "] CANCELADA exitosamente";
                    AccountingEngine motorContble = new AccountingEngine();
                    motorContble.cancelAccountingApplication(conn, "REINTEGROCAJAAUT", cancel[i], "tReintegroCajaAutEncabezado", "tReintegroCajaAutDetalle", "nFolioReintegroCajaAut", rcbl.readfCancelacion(cancel[i], "tReintegroCajaAutEncabezado", "nFolioReintegroCajaAut"));
                    motorContble.cancelAccountingApplication(conn, "REINTEGROCAJA", cancel[i], "tReintegroCajaEncabezado", "tReintegroCajaDetalle", "nFolioReintegroCaja", rcbl.readfCancelacion(cancel[i], "tReintegroCajaEncabezado", "nFolioReintegroCaja"));
                    datosCancelcionAUT = rcbl.PolizaCancelacion(cancel[i], "tReintegroCajaAutEncabezado", "tReintegroCajaAutDetalle", "nFolioReintegroCajaAut");
                    datosCancelcion = rcbl.PolizaCancelacion(cancel[i], "tReintegroCajaEncabezado", "tReintegroCajaDetalle", "nFolioReintegroCaja");
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
            result.add("Ocurrio el siguiente error mientras se cancelaban las solicitudes: " + e);
        }
        return result;
    }

    // fin metodo cancela Reintegro Caja
    public void cancelaSolicitudFirmaElectronica(String folio, String cancelReason, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ReintegrosCajaManager.MotivoRechazoSolicitudFirmaElectronica(conn, folio, cancelReason);
            SolicitudFirmaElectronica scfe = new SolicitudReintegroCajaFirmaElectronica();
            scfe.setHeader("tReintegroCajaEncabezado");
            scfe.setDetail("tReintegroCajaDetalle");
            scfe.setDocument("REINTEGROCAJA");
            scfe.setField("nFolioReintegroCaja");
            scfe.setIdField(Integer.parseInt(folio));
            scfe.setUsuario(u);
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, scfe, SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
            FirmaElectronicaManager.cancelaDocumento(conn, scfe);
            scfe.onCancelaTramite(conn, cancelReason);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
