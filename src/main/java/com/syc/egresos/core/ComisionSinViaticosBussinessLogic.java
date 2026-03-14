package com.syc.egresos.core;

import java.sql.Connection;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.SolicitudTramiteFirmaElectronica;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import com.syc.solicitudviaticos.core.ComisionSinViaticosManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComisionSinViaticosBussinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ComisionSinViaticosBussinessLogic.class);

    public ComisionSinViaticosBussinessLogic() {
    }

    public ComisionSinViaticosBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public void cancelaSolicitudFirmaElectronica(String folioComision, String cancelReason, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ComisionSinViaticosManager.liberaVuelos(conn, Integer.parseInt(folioComision));
            SolicitudFirmaElectronica sfe = new SolicitudTramiteFirmaElectronica();
            sfe.setIdField(Integer.parseInt(folioComision));
            sfe.setUsuario(u);
            sfe.onCancelaTramite(conn, cancelReason);
            ComisionSinViaticosManager.actualizaAplicacion(conn, "C", Integer.parseInt(folioComision));
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, sfe, SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
