package com.syc.registroingresos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Timestamp;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.registroingresos.RegistroIngresosManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistroIngresosBussinesLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(RegistroIngresosBussinesLogic.class);

    public boolean correoProduccion = false;

    private String folioGenerator;

    public RegistroIngresosBussinesLogic(String jniName) {
        super.init(jniName);
    }

    public RegistroIngresosBussinesLogic(String jniName, String folioGenerator) {
        super.init(jniName);
        this.folioGenerator = folioGenerator;
    }

    // para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public String insertaRegistroIngreso(RegistrosIngresosEncabezado regInE, ArrayList<RegistrosIngresosDetalle> regInDetalles, int folio, String folioCompleto, Usuario usuario) throws Exception, SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            mensaje = RegistroIngresosManager.insertaRegistroIngreso(conn, regInE, regInDetalles, folio, folioCompleto, usuario);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public String cancelarAppContableNuevo(Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha) throws Exception {
        String retVal = null;
        Connection conn = null;
        try {
            ContableInterface ci = new AplicacionContable();
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            AplicarContableReturn acr = ci.cancelarAppContableNueva(conn, c, "", "", "", 1, "", m, prefixPath, uLogin, cFecha);
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CAPTURA_REGISTROINGRESO" }, new String[] { "captura_registroingreso" }, m, prefixPath);
            } else {
                conn.rollback();
            }
            retVal = acr.getMessageList().get(acr.getMessageList().size() - 1);
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return retVal;
    }

    public RegistrosIngresosEncabezado getRegistroEncabezadoNuevo(int nFolio) throws SQLException {
        Connection conn = null;
        RegistrosIngresosEncabezado re = new RegistrosIngresosEncabezado();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = RegistroIngresosManager.getRegistroIngresosEncabezadoNuevo(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public RegistrosIngresosDetalle getRegistroDetalleNuevo(int nFolio) throws SQLException {
        Connection conn = null;
        RegistrosIngresosDetalle re = new RegistrosIngresosDetalle();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = RegistroIngresosManager.getRegistroIngresosDetalleNuevo(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public String autorizaRegistroIngresos(Caso c, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario) throws Exception, SQLException {
        Connection conn = null;
        String mensaje = "";
        String sEjercicioFiscal = adecProy.obtenEjercicioFiscal();
        try {
            conn = getConnection();
            mensaje = RegistroIngresosManager.autorizaRegistroIngresos(conn, c, m, prefixPath, uLogin, usuario, sEjercicioFiscal);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public String autorizaRegistroIngresosApartado(String Campo, String Encabezado, String Folio, String Detalle, String Documento, String fAplica, Caso c, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario) throws Exception, SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            mensaje = RegistroIngresosManager.autorizaRegistroIngresosApartado(conn, Campo, Encabezado, Folio, Detalle, Documento, fAplica, c, m, prefixPath, uLogin, usuario);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    // LayOut Registro Ingreso
    public boolean ActualizaStatus(String listaFolios, String usuario) throws Exception {
        Connection conn = null;
        Boolean Actualizado = null;
        try {
            conn = getConnection();
            RegistroIngresosManager.UpdateStatus(conn, listaFolios, usuario);
            Actualizado = true;
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                Actualizado = false;
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return Actualizado;
    }

    public ArrayList<String> buscaRIFIntegrados(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, Usuario usuario, String pTimeStamp, String folioGenerator, String centroContableUser) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = RegistroIngresosManager.buscaRIFIntegrados(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, usuario, pTimeStamp, folioGenerator, centroContableUser);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                log.error(e.getMessage(), e);
                try {
                    conn.rollback();
                } catch (Exception e3) {
                    log.error("Problemas haciendo rollback " + e3, e3);
                }
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = RegistroIngresosManager.CreaDocumentacionComprobatoria(conn, listaIds, sTimeStamp, bIntegra);
            RegistroIngresosManager.updateHeaderRIFEnvioSICOP(conn, listaIds);
            conn.commit();
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

    public String getUE(String sCB) throws Exception {
        PreparedStatement pstmntH = null;
        Connection conn = null;
        ResultSet rs3 = null;
        conn = getConnection();
        String sRet = "";
        String Sql3 = " select strUnidadEjecutora from tUECuentasBancarias where strclabe = '" + sCB + "'";
        pstmntH = conn.prepareStatement(Sql3);
        rs3 = pstmntH.executeQuery();
        if (rs3.next()) {
            sRet = rs3.getString(1);
        }
        rs3.close();
        return sRet;
    }

    public void ActualizaRendimientosGreenMex(HttpServletResponse response, HttpServletRequest request) throws Exception {
        Connection conn = null;
        int insertado;
        int folio = Integer.parseInt(request.getParameter("folio"));
        String mImporteRendimientos = request.getParameter("mImporteRendimientos");
        String mImporteRendimientosGM = request.getParameter("mImporteRendimientosGM");
        try {
            conn = getConnection();
            insertado = RegistroIngresosManager.ActualizaRendimientosGreenMex(conn, folio, mImporteRendimientos, mImporteRendimientosGM);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String insertaRegistroRazonSolicalIP(RegistroIngresoRazonSocial rzip, String cPrograma, Usuario u) throws Exception {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            mensaje = RegistroIngresosManager.insertaRegistroRazonSolicalIP(conn, rzip, cPrograma, u);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public RegistroIngresoRazonSocial getRegistroIngresoRazonSocial(int nFolio) throws SQLException {
        Connection conn = null;
        RegistroIngresoRazonSocial re = new RegistroIngresoRazonSocial();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = RegistroIngresosManager.getRegistroIngresoRazonSocial(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }
}
