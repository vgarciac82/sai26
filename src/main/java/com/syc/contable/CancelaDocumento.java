package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.pasivoscontingentes.PasivosContingentesManager;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CancelaDocumento extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public CancelaDocumento(String jniName) {
        super.init(jniName);
    }

    public String cancelaDoctox(String cFolioDocto, String cTipoDcto, String cFecha) throws SQLException {
        Connection conn = null;
        String retVal = "";
        int nFolioDocumento = 0;
        String cTablaPadre = "";
        String cTablaHija = "";
        String cFolio = "";
        String cTipoDocumento = "";
        try {
            conn = getConnection();
            // nFolioDocumento = new Integer (cFolioDocto); //numero de Folio
            // del Documento
            // cTablaPadre = "t" + cTipoDcto + "Encabezado";
            // cTablaHija = "t" + cTipoDcto + "Detalle";
            // cFolio = "nFolio" + cTipoDcto;
            // cTipoDocumento = cTipoDcto;
            Caso c = new Caso();
            c.setFolio(cFolioDocto);
            c = CasoManager.select(conn, c);
            AplicacionContable ci = new AplicacionContable();
            System.out.println("Antes de Cancelar Documento");
            // retVal=ci.cancelarAppContable(conn, null, cTablaPadre,
            // cTablaHija, cFolio, nFolioDocumento, cTipoDocumento, cFecha);
            if (c.getIdTC() != 3) {
                retVal = ci.cancelarAppContable(conn, c, "", "", "", 0, "", cFecha);
            } else
                retVal = "Las Adecuaciones presupuestales NO se cancelan desde esta pantalla. Favor de contactar a la Gerencia de Presupuesto";
            System.out.println(retVal);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return retVal;
    }

    public String cancelaDoctoNuevo(String cFolioDocto, String cTipoDcto, String cFecha, Map m, String prefixPath, String uLogin, boolean esFirmaElectronica) throws Exception {
        Connection conn = null;
        String retVal = "";
        try {
            conn = getConnection();
            Caso c = new Caso();
            c.setFolio(cFolioDocto);
            c = CasoManager.select(conn, c);
            Usuario u = new Usuario();
            u.setLogin(uLogin);
            u = UsuarioManager.select(conn, u);
            AplicacionContable ci = new AplicacionContable();
            log.debug("Object: {}", "Cancelacion con motor nuevo del folio: " + c.getFolio());
            if (c.getIdTC() != 3) {
                AplicarContableReturn acr = ci.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, uLogin, cFecha);
                int nFolio = Integer.parseInt(cFolioDocto.substring((cFolioDocto.lastIndexOf('-') + 1)));
                String esPasivo = PasivosContingentesManager.esPasivoContingente(conn, nFolio);
                if ("SI".equalsIgnoreCase(esPasivo)) {
                    PasivosContingentesManager.cancelaBajaPasivo(conn, nFolio);
                }
                if (acr.isSuccess()) {
                    if (esFirmaElectronica) {
                        FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);
                        febl.cargaInformacionTramite("POLIZA");
                        SolicitudFirmaElectronica sfe = (SolicitudFirmaElectronica) Util.instanceCasoFIEL(febl.getTramiteSolicitud().getClaseImplementa());
                        sfe.setDocument("POLIZA");
                        sfe.setDetail("tDocPolizaDetalle");
                        sfe.setField("nFolioDocPoliza");
                        sfe.setHeader("tDocPolizaEncabezado");
                        sfe.setIdField(nFolio);
                        sfe.setUsuario(u);
                        sfe.onCancelaTramite(conn, "Rechazo de poliza manual del usuario: " + u.getNombre());
                        FirmaElectronicaManager.avanzaEstatusSICOP(conn, sfe.getHeader(), sfe.getField(), sfe.getIdField(), SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
                    }
                    conn.commit();
                } else
                    conn.rollback();
                retVal = acr.getMessageList().get(acr.getMessageList().size() - 1);
            } else {
                retVal = "Las Adecuaciones presupuestales NO se cancelan desde esta pantalla. Favor de contactar a la Gerencia de Presupuesto";
            }
            log.debug("Object: {}", retVal);
            return retVal;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String guardaDocumentoCancelado(String fDevolucion, String NumeroFolio, String FechaRegistro, String Documento, String noContrarrecibo, String Folio, String RFC, String nomRFC, String fAplicacion, String importeNeto, String MotivoDevolucion, String DescripcionDevolucion) throws SQLException {
        Connection conn = null;
        String valor = "";
        PreparedStatement pstmntInsert = null;
        try {
            conn = getConnection();
            pstmntInsert = conn.prepareStatement("INSERT INTO tVolante_Devolucion (folioDevolucion,NumeroFolio,FechaRegistro,Documento,noContrarrecibo,Folio,RFC,nomRFC,fAplicacion,importeNeto,MotivoDevolucion,DescripcionDevolucion) " + " VALUES ('" + fDevolucion + "','" + NumeroFolio + "','" + FechaRegistro + "','" + Documento + "','" + noContrarrecibo + "','" + Folio + "','" + RFC + "','" + nomRFC + "','" + fAplicacion + "','" + importeNeto + "','" + MotivoDevolucion + "','" + DescripcionDevolucion + "')");
            int intrInsert = pstmntInsert.executeUpdate();
            if (intrInsert > 0) {
                conn.commit();
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                if (pstmntInsert != null) {
                    pstmntInsert.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando statement");
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            pstmntInsert = null;
            conn = null;
        }
        return valor;
    }
}
