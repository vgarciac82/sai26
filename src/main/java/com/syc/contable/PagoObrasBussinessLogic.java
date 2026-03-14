package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import com.syc.contable.core.PagoObrasManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PagoObrasBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public PagoObrasBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagoObrasManager.BuscaCompromisos(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public boolean ActualizaStatus(String listaFolios) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            PagoObrasManager.UpdateStatus(conn, listaFolios);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return true;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagoObrasManager.CreaDocumentacionComprobatoria(conn, listaIds);
            PagoObrasManager.updateHeaderCompromisos(conn, listaIds);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception {
        boolean regActualizado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regActualizado = PagoObrasManager.updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regActualizado;
    }

    public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        boolean regInsertado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regInsertado = PagoObrasManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regInsertado;
    }
}
