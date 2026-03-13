package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import com.syc.contable.core.PagoAcuerdosAMFManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagoAcuerdosAMFBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public PagoAcuerdosAMFBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagoAcuerdosAMFManager.BuscaCompromisos(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public boolean ActualizaStatus(String listaFolios) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            PagoAcuerdosAMFManager.UpdateStatus(conn, listaFolios);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return true;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //Actualiza nEnviadoSICOP en la Tabla tPagoAMF
            PagoAcuerdosAMFManager.updateHeaderCompromisos(conn, listaIds);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception {
        boolean regActualizado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regActualizado = PagoAcuerdosAMFManager.updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return regActualizado;
    }

    public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        boolean regInsertado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regInsertado = PagoAcuerdosAMFManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return regInsertado;
    }

    public ArrayList<String> buscaCierreAMFExport(String listaFolios) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagoAcuerdosAMFManager.buscarCierreAMFExport(conn, listaFolios);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public ArrayList<String> buscarAdefaExport(String listaFolios) throws Exception {
        ArrayList<String> arrLista = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrLista = PagoAcuerdosAMFManager.buscarAdefaExport(conn, listaFolios);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrLista;
    }
}
