package com.syc.contable;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.core.PagosDirectosManager;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.core.impl.EgresoPAGODIRECTOEncabezado;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PagosDirectosBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(PagosDirectosBussinessLogic.class);

    public PagosDirectosBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception {
        boolean regActualizado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regActualizado = PagosDirectosManager.updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
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

    public int actualizaEstatus(EgresoPAGODIRECTOEncabezado pde) throws Exception {
        Connection conn = null;
        int regreso = 0;
        try {
            conn = getConnection();
            regreso = PagosDirectosManager.actualizaEstatus(conn, pde);
            conn.commit();
            return regreso;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean ActualizaStatus(String listaFolios) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            PagosDirectosManager.UpdateStatus(conn, listaFolios);
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

    public ArrayList<StringBuilder> armaDocumentoComprobatorio(String listaIds) throws Exception {
        ArrayList<StringBuilder> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagosDirectosManager.creaDocumentacionComprobatoria(conn, listaIds);
            PagosDirectosManager.updateHeaderCompromisos(conn, listaIds);
            conn.commit();
            return arrListaComp;
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

    public ArrayList<StringBuilder> generaLayoutPagosDirectos(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario) throws Exception {
        ArrayList<StringBuilder> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            log.info("Object: {}", "Generando layout de Pago Directo para los folios: [" + listaFolios + "]" + "Cuentas Bancarias [" + listaCuentaBancaria + "]" + "]" + "Fechas [" + listaFechas + "]" + "] " + "Leyendas [" + ListaLeyendas + "]" + "Usaurio [" + sUsuario + "]");
            arrListaComp = PagosDirectosManager.generaLayoutPagos(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);
            log.debug("Layout generado exitosamente");
            conn.commit();
            return arrListaComp;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public ArrayList<StringBuilder> generaLayoutPagoDiversoCompromiso(Usuario usuario, String folio, String cuentaBancaria, String fecha, String leyenda) throws Exception {
        Connection conn = null;
        ArrayList<StringBuilder> arrListaComp = null;
        try {
            conn = getConnection();
            arrListaComp = PagosDirectosManager.generaLayoutCompromisoPD(conn, usuario, folio, cuentaBancaria, fecha, leyenda);
            conn.commit();
            return arrListaComp;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        boolean regInsertado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regInsertado = PagosDirectosManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
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

    public File generaLayoutPagoDirectoCompromiso(Usuario usuario, String caNoCompromiso) throws Exception {
        Connection conn = null;
        File layout = null;
        try {
            conn = getConnection();
            layout = PagosDirectosManager.generaLayoutCompromisoPDNomina(conn, usuario, caNoCompromiso);
            conn.commit();
            return layout;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public File generaDocComprobatoria(Usuario usuario, String caNoCompromiso) throws Exception {
        Connection conn = null;
        File layout = null;
        try {
            conn = getConnection();
            String CXP = PagosDirectosManager.buscaCXP(conn, caNoCompromiso);
            layout = PagosDirectosManager.generaDocComp(conn, CXP);
            conn.commit();
            return layout;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
