package com.syc.registroingresos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
//import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
//import org.json.JSONObject;
import com.syc.contable.AccountingEngine;
//import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.ContableInterface;
//import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.PagosDiversosRGManager;
//import com.syc.contable.core.RelacionGastosManager;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

//import com.syc.ws.inventario.WSManager;
public class RegistroIngresosManager {

    public static final Logger log = LoggerFactory.getLogger(RegistroIngresosManager.class);

    private static final String CRIR = "79900/CNF010405EG1";

    public RegistroIngresosManager() {
        super();
    }

    public static RegistrosIngresosEncabezado getRegistroIngresosEncabezadoNuevo(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RegistrosIngresosEncabezado rie = null;
        pstm = conn.prepareStatement("SELECT * FROM tRegistroIngresoEncabezado WITH (NOLOCK) WHERE nFolioRegistroIngreso = ?");
        log.debug("Object: " + String.valueOf("Leyendo tRegistroIngresoEncabezado del Folio: " + folio));
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            rie = new RegistrosIngresosEncabezado();
            rie.setnTipoIngreso(res.getString("nTipoIngreso"));
            rie.setcTipoIngreso(res.getString("cTipoIngreso"));
            rie.setfCaptura(res.getString("fCaptura"));
            rie.setfAplicacion(res.getString("fAplicacion"));
            rie.setcRamo(res.getString("cRamo"));
            rie.setcUnidadResponsable(res.getString("cUnidadResponsable"));
            rie.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
            rie.setnFolioPoliza(res.getInt("nFolioPoliza"));
            rie.setcTipoPoliza(res.getString("cTipoPoliza"));
            rie.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
            rie.setcCentroContable(res.getString("cCentroContable"));
            rie.setcConcepto(res.getString("cConcepto"));
            rie.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
            rie.setnFolioPolizaCancelacion(res.getInt("nFolioPolizaCancelacion"));
            rie.setfCancelacion(res.getString("fCancelacion"));
            rie.setcDescripcionPoliza(res.getString("cDescripcionPoliza"));
            rie.setnFolioSicop(res.getString("nFolioSicop"));
            //DecimalFormat formatter = new DecimalFormat("###,###.##");
            rie.setmImporte(res.getDouble("mImporte"));
            rie.setnApartado(res.getInt("nApartado"));
            rie.setcPrograma(res.getString("cPrograma"));
            rie.setcOrigen(res.getString("cOrigen"));
            rie.setcaNoContrarrecibo(res.getString("caNoContrarrecibo"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return rie;
    }

    public static String insertaRegistroIngreso(Connection conn, RegistrosIngresosEncabezado regInEnc, ArrayList<RegistrosIngresosDetalle> regInDetalles, int folio, String folioCompleto, Usuario usuario) throws Exception {
        String mensaje = "";
        PreparedStatement psInsertDetalle = null;
        PreparedStatement psInsertEncabezado = null;
        PreparedStatement psInsertDetalleApartado = null;
        PreparedStatement psInsertEncabezadoapartado = null;
        PreparedStatement psUpdateEncabezadoIF = null;
        String cEvento = "";
        String EP = "";
        String cap = "";
        String DescPol = "";
        int mesFin = 0;
        try {
            String sTipoIngreso = regInEnc.getnTipoIngreso();
            int iTipoIngreso = Integer.parseInt(sTipoIngreso);
            if (iTipoIngreso == 1) {
                cEvento = "";
                DescPol = "Póliza de Registro Ingreso Rescurso Fiscal";
            } else if (iTipoIngreso == 3) {
                cEvento = "";
                DescPol = "Póliza de Registro Ingreso Rescurso de FONDEN";
            } else {
                cEvento = ObtenerEvento(conn, Integer.parseInt(regInEnc.getcPrograma()));
                DescPol = "Póliza de Registro Ingreso Recurso Propio";
            }
            psInsertEncabezado = conn.prepareStatement("INSERT INTO tRegistroIngresoEncabezado(nFolioRegistroIngreso, nTipoIngreso, cTipoIngreso, fCaptura, fAplicacion,caNoContrarrecibo, cConcepto, mImporte, cOrigen, cPrograma, cCentroContable, cUnidadResponsable, aEjercicioFiscal, cRamo, cUnidadResponsableContable, U_LOGIN, cTipoPoliza, cDescripcionPoliza) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            //folio del registro ingreso
            psInsertEncabezado.setInt(1, folio);
            psInsertEncabezado.setInt(2, iTipoIngreso);
            psInsertEncabezado.setString(3, regInEnc.getcTipoIngreso());
            psInsertEncabezado.setString(4, regInEnc.getfCaptura());
            psInsertEncabezado.setString(5, regInEnc.getfAplicacion());
            psInsertEncabezado.setString(6, regInEnc.getcaNoContrarrecibo());
            psInsertEncabezado.setString(7, regInEnc.getcConcepto());
            psInsertEncabezado.setDouble(8, regInEnc.getmImporte());
            psInsertEncabezado.setString(9, regInEnc.getcOrigen());
            psInsertEncabezado.setString(10, regInEnc.getcPrograma());
            psInsertEncabezado.setString(11, regInEnc.getcCentroContable());
            psInsertEncabezado.setString(12, regInEnc.getcUnidadResponsable());
            psInsertEncabezado.setString(13, regInEnc.getaEjercicioFiscal());
            psInsertEncabezado.setString(14, regInEnc.getcRamo());
            psInsertEncabezado.setString(15, regInEnc.getcUnidadResponsableContable());
            psInsertEncabezado.setString(16, usuario.getLogin());
            psInsertEncabezado.setString(17, regInEnc.getcTipoPoliza());
            psInsertEncabezado.setString(18, DescPol);
            psInsertEncabezado.execute();
            log.debug("Object: " + String.valueOf(psInsertEncabezado.toString()));
            psInsertDetalle = conn.prepareStatement("INSERT INTO tRegistroIngresoDetalle(nFolioRegistroIngreso, nDocRenglon, cEvento, EP, mImporte, mImporteNegativo, nMes, cCentroContable, mSaldo, CTAB, mImporteMod)VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            for (Iterator<RegistrosIngresosDetalle> i = regInDetalles.iterator(); i.hasNext(); ) {
                RegistrosIngresosDetalle rd = i.next();
                if (iTipoIngreso == 1) {
                    EP = rd.getEP();
                    cap = EP.substring(31, 32);
                    cEvento = "INGRESO_FISCAL";
                } else if (iTipoIngreso == 3) {
                    EP = rd.getEP();
                    cap = EP.substring(31, 32);
                    if (cap.equals("1") || cap.equals("2") || cap.equals("3")) {
                        cEvento = "INT_IF_CAP123";
                    } else if (cap.equals("4")) {
                        cEvento = "INT_IF_CAP4";
                    } else if (cap.equals("5") || cap.equals("6")) {
                        cEvento = "INT_IF_CAP56";
                    }
                } else if (iTipoIngreso == 2) {
                    String[] componentesFApl = regInEnc.getfAplicacion().split("/");
                    mesFin = Integer.parseInt(componentesFApl[1]);
                }
                psInsertDetalle.setInt(1, regInEnc.getnFolioRegistroIngreso());
                psInsertDetalle.setInt(2, rd.getnDocRenglon());
                psInsertDetalle.setString(3, cEvento);
                psInsertDetalle.setString(4, rd.getEP());
                psInsertDetalle.setDouble(5, rd.getmImporte());
                psInsertDetalle.setDouble(6, rd.getmImporteNegativo());
                if (iTipoIngreso == 1) {
                    psInsertDetalle.setInt(7, rd.getnMes());
                } else if (iTipoIngreso == 2) {
                    psInsertDetalle.setInt(7, mesFin);
                }
                psInsertDetalle.setString(8, rd.getcCentroContable());
                psInsertDetalle.setDouble(9, rd.getmSaldo());
                psInsertDetalle.setString(10, rd.getCTAB());
                psInsertDetalle.setDouble(11, 0.00);
                psInsertDetalle.addBatch();
                log.debug("Object: " + String.valueOf(psInsertDetalle.toString()));
            }
            psInsertDetalle.executeBatch();
            conn.commit();
            mensaje = "insercion";
            Integer mes = 0;
            if ("IP_GMBI".equals(cEvento)) {
                insertaEdoCuenta(conn, regInEnc.getnFolioRegistroIngreso(), "REGISTROINGRESO");
            }
            if (iTipoIngreso == 1) {
                cEvento = "";
                DescPol = "Apartado de Registro Ingreso Rescurso Fiscal";
                mes = Integer.parseInt(regInEnc.getfCaptura().substring(3, 5));
                psInsertEncabezadoapartado = conn.prepareStatement("INSERT INTO dbo.tApartadoEncabezado( nFolioApartado,fCarga ,cCentroContable ,cRamo ,cUnidadResponsable, caNoPreCompromiso ,cTipoPoliza, nMes,aEjercicioFiscal ,cUnidadResponsableContable,cDescripcionPoliza ,nStatusFinanciero ,fVigencia ,nEnviadoSICOP ,cIdSolicitud) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                //folio del Apartado
                psInsertEncabezadoapartado.setInt(1, regInEnc.getnFolioApartado());
                psInsertEncabezadoapartado.setString(2, regInEnc.getfCaptura());
                psInsertEncabezadoapartado.setString(3, regInEnc.getcCentroContable());
                psInsertEncabezadoapartado.setString(4, regInEnc.getcRamo());
                psInsertEncabezadoapartado.setString(5, regInEnc.getcUnidadResponsable());
                //caNoPreCopromiso
                psInsertEncabezadoapartado.setString(6, "");
                psInsertEncabezadoapartado.setString(7, "PR");
                psInsertEncabezadoapartado.setInt(8, mes);
                psInsertEncabezadoapartado.setString(9, regInEnc.getaEjercicioFiscal());
                psInsertEncabezadoapartado.setString(10, regInEnc.getcUnidadResponsableContable());
                psInsertEncabezadoapartado.setString(11, DescPol);
                //nStatusFinanciero
                psInsertEncabezadoapartado.setInt(12, 0);
                // fVigencia
                psInsertEncabezadoapartado.setString(13, regInEnc.getfCaptura());
                //nEnviadoSICOP
                psInsertEncabezadoapartado.setInt(14, 0);
                psInsertEncabezadoapartado.setString(15, folioCompleto);
                psInsertEncabezadoapartado.execute();
                log.debug("Object: " + String.valueOf(psInsertEncabezadoapartado.toString()));
                psInsertDetalleApartado = conn.prepareStatement("INSERT INTO dbo.tApartadoDetalle( nFolioApartado ,nDocRenglon ,EP ,cEvento ,mImporte ,mImporteNegativo ,cMes ,cCentroContable)VALUES(?,?,?,?,?,?,?,?)");
                for (Iterator<RegistrosIngresosDetalle> i = regInDetalles.iterator(); i.hasNext(); ) {
                    RegistrosIngresosDetalle rd = i.next();
                    cEvento = "APARTADO_IF";
                    psInsertDetalleApartado.setInt(1, regInEnc.getnFolioApartado());
                    psInsertDetalleApartado.setInt(2, rd.getnDocRenglon());
                    psInsertDetalleApartado.setString(3, rd.getEP());
                    psInsertDetalleApartado.setString(4, cEvento);
                    psInsertDetalleApartado.setDouble(5, rd.getmImporte());
                    psInsertDetalleApartado.setDouble(6, rd.getmImporteNegativo());
                    psInsertDetalleApartado.setInt(7, rd.getnMes());
                    psInsertDetalleApartado.setString(8, rd.getcCentroContable());
                    psInsertDetalleApartado.addBatch();
                    log.debug("Object: " + String.valueOf(psInsertDetalleApartado.toString()));
                }
                psInsertDetalleApartado.executeBatch();
                conn.commit();
                mensaje = "insercion";
            }
            psUpdateEncabezadoIF = conn.prepareStatement("UPDATE tRegistroIngresoEncabezado SET nApartado = ? WHERE nFolioRegistroIngreso = ?");
            psUpdateEncabezadoIF.setInt(1, regInEnc.getnFolioApartado());
            psUpdateEncabezadoIF.setInt(2, regInEnc.getnFolioRegistroIngreso());
            psUpdateEncabezadoIF.execute();
            log.debug("Object: " + String.valueOf(psUpdateEncabezadoIF.toString()));
            conn.commit();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                    mensaje = "error";
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                    e2.printStackTrace();
                }
            throw e;
        } finally {
            CloseObject.closeObject(psInsertDetalle, false);
        }
        return mensaje;
    }

    public static String ObtenerEvento(Connection conn, int idprograma) {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            String sSQL = "SELECT cTipoConcepto, ID_DESTINO FROM tCatProgramasProyectos WITH (NOLOCK) WHERE nIdPrograma = ? ";
            pstmnt = conn.prepareStatement(sSQL);
            log.debug("Object: " + String.valueOf(sSQL));
            pstmnt.setInt(1, idprograma);
            log.debug("Object: " + String.valueOf("Programa: " + idprograma));
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1) + "_" + rs.getString(2);
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt = null;
        }
        return retval;
    }

    public static String autorizaRegistroIngresos(Connection conn, Caso c, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario, String sEjercicioFiscal) throws SQLException {
        ArrayList<String> arrLResult = new ArrayList<String>();
        String cMensaje = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Object: " + String.valueOf("Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis())));
            AplicarContableReturn acr;
            if (!"2012".equals(sEjercicioFiscal))
                acr = conInt.aplicarContableNuevo(conn, c, "tRegistroIngresoEncabezado", "tRegistroIngresoDetalle", "nFolioRegistroIngreso", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REGISTROINGRESO", m, prefixPath, uLogin, "");
            else
                acr = conInt.aplicarContableNuevo(conn, c, "tRegistroIngresoEncabezado", "tRegistroIngresoDetalle", "nFolioRegistroIngreso", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REGISTROINGRESO", m, prefixPath, uLogin, "SI");
            arrLResult = (ArrayList<String>) acr.getMessageList();
            log.debug("Object: " + String.valueOf("Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis())));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                if (actualizaDoctoAplicado(conn, new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue())) {
                    conn.commit();
                    String enviaCorreo = aplicaEnvioAlertas(conn, new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue());
                    if ("SI".equals(enviaCorreo)) {
                        envioAlertas(conn, new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue());
                    }
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REGISTROINGRESO" }, new String[] { "consulta_registroingreso" }, m, prefixPath);
                    cMensaje = "Registro de Ingreso aplicado correctamente";
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc.getMessage(), exc);
            arrLResult.add(exc.getLocalizedMessage());
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        Iterator<String> iteraMensajes = arrLResult.iterator();
        while (iteraMensajes.hasNext()) {
            cMensaje += iteraMensajes.next();
        }
        return cMensaje;
    }

    public static boolean actualizaDoctoAplicado(Connection conn, int folio) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        try {
            String sSQL = " UPDATE tRegistroIngresoEncabezado SET cDocumentoHaplicado = 'S' WHERE nFolioRegistroIngreso = ? ";
            log.debug("Object: " + String.valueOf(sSQL));
            pstmntUp = conn.prepareStatement(sSQL);
            log.debug("Object: " + String.valueOf(folio));
            pstmntUp.setInt(1, folio);
            pstmntUp.execute();
            retval = true;
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }

    public static String autorizaRegistroIngresosApartado(Connection conn, String Campo, String Encabezado, String Folio, String Detalle, String Documento, String fAplica, Caso c, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario) throws SQLException {
        PreparedStatement pstmnEnc = null;
        PreparedStatement pstmnt = null;
        ResultSet rsEnc = null;
        String cMensaje = "";
        boolean success = false;
        String errorApl = null;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            AccountingEngine accEng = new AccountingEngine();
            pstmnt = conn.prepareStatement("UPDATE tApartadoEncabezado SET faplicacion = '" + fAplica + "' WHERE nFolioApartado = ?");
            pstmnt.setInt(1, Integer.parseInt(Folio));
            pstmnt.execute();
            success = accEng.makeAccountingApplication(conn, Documento, Folio, Encabezado, Detalle, Campo);
            if (success) {
                Caso cReloaded = new Caso();
                cReloaded.setIdCaso(c.getIdCaso());
                cReloaded = CasoManager.select(conn, cReloaded);
                if (actualizaDoctoApartadoAplicado(conn, Integer.parseInt(Folio))) {
                    conn.commit();
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REGISTROINGRESO" }, new String[] { "consulta_registroingreso" }, m, prefixPath);
                    cMensaje = "Registro de Ingreso para Envio SICOP-SIAFF";
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
                cMensaje = "No se logro aplicar el Ingreso";
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            cMensaje = errorApl;
            try {
                conn.rollback();
            } catch (Exception x) {
            }
        } finally {
            try {
                if (rsEnc != null) {
                    rsEnc.close();
                }
                if (pstmnEnc != null) {
                    pstmnEnc.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            pstmnEnc = null;
            rsEnc = null;
            conn = null;
        }
        return cMensaje;
    }

    public static boolean actualizaDoctoApartadoAplicado(Connection conn, int folio) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        try {
            String sSQL = " UPDATE tApartadoEncabezado SET cDocumentoHaplicado = 'S' WHERE nFolioApartado = ? ";
            log.debug("Object: " + String.valueOf(sSQL));
            pstmntUp = conn.prepareStatement(sSQL);
            log.debug("Object: " + String.valueOf(folio));
            pstmntUp.setInt(1, folio);
            pstmntUp.execute();
            retval = true;
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }

    // LayOut Registro Ingreso
    public static int UpdateStatus(Connection conn, String Integracion, String usuario) throws SQLException {
        PreparedStatement pstmnt = null, updateLayout = null;
        int retval;
        try {
            // TODO: Validar que solo sea en esta tabla y no falte la del apartado
            pstmnt = conn.prepareStatement("UPDATE tRegistroIngresoEncabezado SET nEnviadoSICOP = 0 WHERE nFolioRegistroIngreso IN (SELECT Folio FROM vListaRegistroIngresoConLayout WHERE sAuxiliarComodin = '" + Integracion + "')");
            retval = pstmnt.executeUpdate();
            updateLayout = conn.prepareStatement("UPDATE tLayoutsCreadosRegistroIngresoEncabezado SET cEstatus = 'DEVUELTO', fDevolucionLayout = GETDATE(), sLoginDevolucion = '" + usuario + "' WHERE sAuxiliarComodin = '" + Integracion + "'");
            updateLayout.executeUpdate();
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (updateLayout != null) {
                updateLayout.close();
            }
            pstmnt = null;
            updateLayout = null;
        }
        return retval;
    }

    public static ArrayList<String> buscaRIFIntegrados(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, Usuario usuario, String sTimeStamp, String folioGenerator, String centroContableUser) throws Exception {
        String sUsuario = usuario.getLogin();
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        PreparedStatement pstmntHLayout = null;
        PreparedStatement pstmntHLayoutDet = null;
        PreparedStatement pstmUpSeqLayout = null;
        PreparedStatement pstmSeqLayout = null;
        String strFolioLayout = "";
        try {
            String[] arrFolios = listaIds.split(",");
            String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
            String[] arrFechas = listaFechas.split(",");
            String[] arrLeyendas = listaLeyendas.split(",");
            // Obtiene Ejercicio Fiscal
            String ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
            String sREFERENCIA1_107 = sTimeStamp;
            //SE ARMA EL "SP" ENCABEZADO
            String Sql = " SELECT TOP 1 " + "	SUBSTRING('" + sREFERENCIA1_107 + "',LEN('" + sREFERENCIA1_107 + "')-7,LEN('" + sREFERENCIA1_107 + "'))," + "	'H' AS Header," + "	CONVERT(nvarchar(10), GETDATE(),103)," + "	CONVERT(nvarchar(10), GETDATE(),103)," + "	tCE.cRamo," + "	tCE.cRamo," + "	tCE.cRamo," + "	'RHQ' UnidadResponsable," + "	'RHQ' UnidadResponsable," + "	'RHQ' UnidadResponsable," + "	'N' ID_TIPO_MOVIMIENTO," + "	'1' AS OrigenPpto," + "	'3' AS TipoSol," + "	'MXN' TipoMoneda," + "	'1' TipoCambio," + "	'1' TIPO_PAGO," + "	'PENDIENTE' AS CveLeyenda," + "	'S04929' CBEN," + "	'" + arrCuentasBancarias[0].trim() + "' CUENTA_BANCARIA," + "	'16RHQ'," + "	'FAC'," + "	'' FechaReferencia," + "	'' Referencia1," + "	'' Referencia2," + " 	'Integracion de RIF " + sREFERENCIA1_107 + "' Concepto," + "	'' NotasReverso," + "	'' AMF," + "	'" + sREFERENCIA1_107 + "' NO_ACMI," + "	'" + sREFERENCIA1_107 + "' AuxiliarComodin," + "	'' CTR," + "	'' FolioDC," + "	CONVERT(DECIMAL(17, 2), 0) DCD_ISR, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_IVADES, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_MIL5, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_MIL2, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_IVA, " + "	CONVERT(DECIMAL(17, 2), 0) IVAANT, " + "	'NA' ID_DESTINO_GASTO " + "FROM tRegistroIngresoEncabezado tCE " + "WHERE tCE.nFolioRegistroIngreso IN (" + listaIds + ") " + "GROUP BY cRamo";
            pstmntH = conn.prepareStatement(Sql);
            log.debug("Object: " + String.valueOf(Sql.toString()));
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                //inserta encabezado
                log.debug("Object: " + String.valueOf("Procesando folio[" + arrFolios[0].trim() + "]"));
                //String nFolio, nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2) + "," + arrFechas[0].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + arrLeyendas[0].trim().trim() + "," + rs.getString(18).trim() + "," + rs.getString(19).trim() + "," + rs.getString(20).trim() + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim() + "," + rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ") + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim() + "," + rs.getString(38).trim() + "," + rs.getString(39).trim() + "," + rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                //OBTENER SEQUENCE DE LAYOUT DE REGISTROINGRESOS
                pstmUpSeqLayout = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'LAYOUTRIF' ");
                pstmUpSeqLayout.executeUpdate();
                pstmSeqLayout = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'LAYOUTRIF' ");
                rs4 = pstmSeqLayout.executeQuery();
                if (rs4.next()) {
                    strFolioLayout = rs4.getString("seq_value");
                } else {
                    //SI NO EXISTE LO INSERTA
                    String InsertSequenceLayout = "INSERT INTO CF_SEQUENCE " + "		SELECT 'LAYOUTRIF', 1 ";
                    pstmUpSeqLayout = conn.prepareStatement(InsertSequenceLayout);
                    pstmUpSeqLayout.executeUpdate();
                    strFolioLayout = "1";
                }
                // Aqui grabamos dentro de layouts creados encabezado
                String SqlLayoutGrabado = "" + "	INSERT INTO tLayoutsCreadosRegistroIngresoEncabezado " + "		SELECT " + strFolioLayout + "," + "			getdate(),  " + "			tCE.nFolioRegistroIngreso, " + "			'H' AS Header, " + "			CONVERT(nvarchar(10), tCE.fAplicacion,103)," + "			CONVERT(nvarchar(10), tCE.fAplicacion,103)," + "			tCE.cRamo," + "			tCE.cRamo," + "			tCE.cRamo," + "			'RHQ' UnidadResponsable," + "			'RHQ' UnidadResponsable," + "			'RHQ' UnidadResponsable," + "			'N' ID_TIPO_MOVIMIENTO," + "			'1' AS OrigenPpto," + "			'2' AS TipoSol," + "			'MXN' TipoMoneda," + "			'1' TipoCambio," + "			'1' TIPO_PAGO," + "			'1'," + "			'S04929' CBEN," + "			'" + arrCuentasBancarias[0].trim() + "'," + "			'16RHQ'," + "			'FAC', " + "			'' FechaReferencia," + "			'' Referencia1," + "			'' Referencia2," + "			REPLACE(LEFT(tCE.cConcepto, 70),',','')," + "			'' NotasReverso," + "			'' AMF," + "			rtrim(tCE.caNoContrarrecibo) NO_ACMI," + "			'" + sREFERENCIA1_107 + "' AuxiliarComodin," + "			'' CTR," + "			'' FolioDC," + "			CONVERT(DECIMAL(17, 2), 0) DCD_ISR, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_IVADES, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_MIL5, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_MIL2, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_IVA, " + "			CONVERT(DECIMAL(17, 2), 0) IVAANT, " + "			'RIF' AS ID_DESTINO_GASTO," + "			'" + sUsuario + "'," + "			'ACTIVO'," + "			NULL, " + "			NULL" + "		FROM tRegistroIngresoEncabezado tCE  WITH (NOLOCK)" + "		LEFT JOIN tBeneficiario B  WITH (NOLOCK) " + "			ON B.dRFC = 'CNF010405EG1' " + "		WHERE tCE.nFolioRegistroIngreso IN (" + listaIds + ")";
                pstmntHLayout = conn.prepareStatement(SqlLayoutGrabado);
                pstmntHLayout.executeUpdate();
                //SE ARMA EL "SP" DETALLE
                //penalizaciones
                String //penalizaciones
                Sql2 = " SELECT '1' ID_EVENTO," + "	'24.0.001' EVENTO," + "	SUBSTRING(D.EP,6,2) ID_RAMO_ML," + "	'RHQ'," + "	SUBSTRING(D.EP,1,4) aEjercicioFiscal," + "	SUBSTRING(D.EP,13,1) cGrupoFuncional," + "	SUBSTRING(D.EP,15,1) cFuncion," + "	SUBSTRING(D.EP,17,2) cSubFuncion," + "	SUBSTRING(D.EP,20,2) cProgramaGeneral, " + "	SUBSTRING(D.EP,23,3) cActividadInstitucional, " + "	SUBSTRING(D.EP,27,4) cProgramaPresupuestario, " + "	SUBSTRING(D.EP,32,1) CCAP_157, " + "	SUBSTRING(D.EP,33,1) CCON_158," + "	SUBSTRING(D.EP,34,1) CPARG_300, " + "	SUBSTRING(D.EP,35,2) CPAR_159, " + "	SUBSTRING(D.EP,38,1) cTipoGasto, " + "	SUBSTRING(D.EP,40,1) cFuenteFinanciamiento, " + "	SUBSTRING(D.EP,42,2) cEntidadFederativa, " + "	SUBSTRING(D.EP,45,11)cCartera, " + "	'0000000000'," + "	'00'CCOP_163," + "	'000' PL," + "	'000' OFI," + "	'00000' AUX1," + "	'00000' AUX2," + "	'0000000000' AUX3," + "	CONVERT(decimal(17, 2),SUM(D.mImporte)) MONTO," + "	nMes MES_149," + "	'0' NRES," + "	CASE WHEN SUBSTRING(ep,32,5)='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO," + "	'000' CONC_MOV," + " 	CONVERT(DECIMAL(17, 2),0) DCD_ISR," + " 	CONVERT(DECIMAL(17, 2),0) DCD_IVA," + " 	CONVERT(DECIMAL(17, 2),0) DCD_MIL5," + " 	CONVERT(DECIMAL(17, 2),0) DCD_MIL2," + " 	CONVERT(decimal(17,2), 0) DCD_CONTRIBUCION," + " 	CONVERT(DECIMAL(17, 2),0) DCD_OTRAS_RET," + "	CONVERT(DECIMAL(17, 2),0)," + "	'' id_ctr_intdet " + "FROM tRegistroIngresoDetalle D  WITH (NOLOCK) " + "WHERE nFolioRegistroIngreso IN (" + listaIds + ") " + "GROUP BY SUBSTRING(D.EP,6,2), " + "	SUBSTRING(D.EP,1,4), " + "	SUBSTRING(D.EP,13,1), " + "	SUBSTRING(D.EP,15,1), " + "	SUBSTRING(D.EP,17,2), " + "	SUBSTRING(D.EP,20,2), " + "	SUBSTRING(D.EP,23,3), " + "	SUBSTRING(D.EP,27,4), " + "	SUBSTRING(D.EP,32,1), " + "	SUBSTRING(D.EP,33,1), " + "	SUBSTRING(D.EP,34,1), " + "	SUBSTRING(D.EP,35,2), " + "	SUBSTRING(D.EP,38,1), " + "	SUBSTRING(D.EP,40,1), " + "	SUBSTRING(D.EP,42,2), " + "	SUBSTRING(D.EP,45,11), " + "	nMes, " + "	SUBSTRING(ep,32,5)";
                pstmntD = conn.prepareStatement(Sql2);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 40; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (pstmntD != null) {
                    pstmntD.close();
                }
                // Grabamos dentro de layouts creados detalle
                String SqlLayoutGrabadoDet = "	DECLARE @lsUR varchar(3), @lsEP varchar(100), @lmSuma money " + "	DECLARE curRGIntegrado CURSOR LOCAL FOR " + "			SELECT E.cUnidadResponsable, D.EP, SUM(D.mImporte)" + "			FROM tRegistroIngresoEncabezado E (NOLOCK), tRegistroIngresoDetalle D (NOLOCK) " + "			WHERE E.nFolioRegistroIngreso = D.nFolioRegistroIngreso AND E.nFolioRegistroIngreso IN (" + listaIds + ") " + "			GROUP BY E.cUnidadResponsable, D.EP " + " 	OPEN curRGIntegrado " + " 		FETCH NEXT FROM curRGIntegrado into @lsUR, @lsEP, @lmSuma WHILE @@FETCH_STATUS = 0 " + " 		BEGIN " + " 			INSERT INTO tLayoutsCreadosRegistroIngresoDetalle " + " 			SELECT TOP 1 " + strFolioLayout + "," + "				'1' ID_EVENTO," + "				'24.0.001' EVENTO," + "				ltrim(TCEP.cRamo) ID_RAMO_ML," + "				'RHQ'," + "				TCEP.aEjercicioFiscal," + "				TCEP.cGrupoFuncional," + "				tCEP.cFuncion," + "				tCEP.cSubFuncion," + "				tCEP.cProgramaGeneral," + "				tCEP.cActividadInstitucional," + "				tCEP.cProgramaPresupuestario," + "				ltrim(substring(cpartida,1,1)) CCAP_157," + "				substring(cpartida,2,1) CCON_158," + "				substring(cpartida,3,1) CPARG_300," + "				substring(cpartida,4,2) CPAR_159," + "				tCEP.cTipoGasto," + "				tCEP.cFuenteFinanciamiento," + "				tCEP.cEntidadFederativa," + "				tCEP.cCartera," + "				ltrim('0000000' + tCEP.cUnidadEjecutora)," + "				substring(TCEP.cUnidadNorativa,2,2) CCOP_163," + "				'000' PL," + "				'000' OFI," + "				'00000' AUX1," + "				'00000' AUX2," + "				'0000000000' AUX3," + "				@lmSuma MONTO," + "				MONTH(GETDATE()) MES_149," + "				'0' NRES," + "				ltrim('PN') TIPO_CONTRATO," + "				'000' CONC_MOV," + "				CONVERT(DECIMAL(17, 2), 0) DCD_ISR, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_IVA, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_MIL5, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_MIL2, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_IVADES, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION, " + "				'' id_ctr_intdet," + "				" + strFolioLayout + " 			FROM tRegistroIngresoDetalle TPDD (NOLOCK) " + "			INNER JOIN tRegistroIngresoEncabezado TPDE (NOLOCK) ON TPDD.nFolioRegistroIngreso = TPDE.nFolioRegistroIngreso " + "			INNER JOIN tCatalogoEP (NOLOCK) TCEP ON TPDD.EP = TCEP.EP " + "			WHERE TPDE.cUnidadResponsable = @lsUR" + "				AND TPDD.EP = @lsEP" + "			FETCH NEXT FROM curRGIntegrado into @lsUR, @lsEP, @lmSuma" + "		END" + "	CLOSE curRGIntegrado " + " 	DEALLOCATE curRGIntegrado  ";
                pstmntHLayoutDet = conn.prepareStatement(SqlLayoutGrabadoDet);
                pstmntHLayoutDet.executeUpdate();
                int nFolioConsolidacion = insertaConsolidacionRegistroIngreso(conn, sTimeStamp, usuario, ejercicioFiscal, folioGenerator);
                insertaCompromisoRegistroIngreso(conn, sTimeStamp, usuario, ejercicioFiscal, nFolioConsolidacion, centroContableUser);
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(rs4, false);
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(pstmntHLayout, false);
            CloseObject.closeObject(pstmntHLayoutDet, false);
            CloseObject.closeObject(pstmUpSeqLayout, false);
            CloseObject.closeObject(pstmSeqLayout, false);
        }
    }

    public static int insertaConsolidacionRegistroIngreso(Connection conn, String sTimeStamp, Usuario sUsuario, String ejercicioFiscal, String folioGenerator) throws Exception {
        log.info("Object: {}", "Insertando consolidacion de Ingreso Fiscal. Folio Integracion[" + sTimeStamp + "] Usuario[" + sUsuario + "] Ejercicio Fiscal[" + ejercicioFiscal + "]");
        int insertados = 0;
        String sqlInsertConsolidacion = "INSERT INTO tconsolidacionrelaciongastosencabezado " + "            (nFolioConsolidacion," + "             nidintegracion, " + "             fcarga, " + "             faplicacion, " + "             ctipopoliza, " + "             u_login, " + "             cunidadresponsablecontable, " + "             cdescripcionpoliza, " + "             cramo, " + "             cunidadresponsable, " + "             aejerciciofiscal) " + "SELECT ?								AS nFolioConsolidacion," + "        ?                             AS nIdIntegracion, " + "       Getdate()                     AS fCarga, " + "       Getdate()                     AS fAplicacion, " + "       'IN'                          AS cTipoPoliza, " + "       ?                             AS U_LOGIN, " + "       'RHQ'                         AS cUnidadResponsableContable, " + "       'Poliza de Ingreso Devengado y Recaudado de la Integración " + sTimeStamp + "/RIF' AS cDescripcionPoliza, " + "       '16'                          AS cRamo, " + "       ?                             AS cUnidadResponsable, " + "       ?                             AS aEjercicioFiscal ";
        String sqlInsertConsolidacionDetalle = "INSERT INTO dbo.tconsolidacionrelaciongastosdetalle" + "        ( nDocRenglon ," + "          nFolioConsolidacion ," + "          ep ," + "          cevento ," + "          ccentrocontable ," + "          cmes ," + "          ID_destino_gasto ," + "          ID_TIPO_CONCEPTO ," + "          partida ," + "          tipogasto ," + "          mimportemasiva ," + "          mImporteNegativo ," + "          nidintegracion ," + "          CTAB ," + "          RFC ," + "          ALM ," + "          OBGT" + "        )" + "SELECT Row_number() OVER (ORDER BY nfolioconsolidacion) AS nDocRenglon, " + "       consolidacion_encabezado.nfolioconsolidacion AS nFolioConsolidacion, " + "       ep, " + "       dbo.fn_evento_integracion_IF(ep) AS cevento, " + "       DETALLE.ccentrocontable, " + "       DETALLE.nmes, " + "       '' id_destino_gasto, " + "       '' id_tipo_concepto, " + "       Substring(detalle.ep, 32, 5)                 AS partida, " + "       Substring(detalle.ep, 38, 1)                 AS tipogasto, " + "       Sum(DETALLE.mImporte)                  AS mimportemasiva, " + "       Sum(DETALLE.mImporteNegativo)         AS mImporteNegativo, " + "       LAYOUT.sauxiliarcomodin                      AS nidintegracion, " + "       LAYOUT.scuenta_bancaria                      AS CTAB, " + "       '' RFC, " + "       '' alm, " + "       Substring(detalle.ep, 32, 5)                 AS OBGT " + "FROM   dbo.tRegistroIngresoEncabezado ENCABEZADO WITH (nolock) " + "INNER JOIN dbo.tRegistroIngresoDetalle DETALLE WITH (nolock) " + "		  ON ENCABEZADO.nFolioRegistroIngreso = DETALLE.nFolioRegistroIngreso " + "LEFT OUTER JOIN dbo.tLayoutsCreadosRegistroIngresoEncabezado LAYOUT WITH (nolock) " + "       ON ENCABEZADO.canocontrarrecibo = LAYOUT.snocontrarrecibo " + "INNER JOIN dbo.tconsolidacionrelaciongastosencabezado consolidacion_encabezado WITH (nolock) " + "       ON LAYOUT.sauxiliarcomodin = consolidacion_encabezado.nidintegracion " + "WHERE  LAYOUT.sauxiliarcomodin IS NOT NULL " + "       AND nfolioconsolidacion = ? " + "GROUP  BY ep, " + "       consolidacion_encabezado.nfolioconsolidacion, " + "       DETALLE.ccentrocontable, " + "       DETALLE.nmes, " + "       LAYOUT.sauxiliarcomodin, " + "       scuenta_bancaria, " + "       Substring(detalle.ep, 32, 5)";
        PreparedStatement psInsertaEncabezado = null;
        PreparedStatement psInsertaDetalle = null;
        ResultSet rsFolioConsolidacion = null;
        int nFolioConsolidacion = -1;
        try {
            Caso c = PagosDiversosRGManager.generaCaso(conn, sUsuario, folioGenerator);
            nFolioConsolidacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            log.debug("Object: " + String.valueOf("Query Insert Encabezado[" + sqlInsertConsolidacion + "]"));
            log.debug("Object: " + String.valueOf("Query Insert Detalle[" + sqlInsertConsolidacionDetalle + "]"));
            psInsertaEncabezado = conn.prepareStatement(sqlInsertConsolidacion, Statement.RETURN_GENERATED_KEYS);
            psInsertaDetalle = conn.prepareStatement(sqlInsertConsolidacionDetalle);
            psInsertaEncabezado.setInt(1, nFolioConsolidacion);
            psInsertaEncabezado.setString(2, sTimeStamp);
            psInsertaEncabezado.setString(3, sUsuario.getLogin());
            psInsertaEncabezado.setString(4, "");
            psInsertaEncabezado.setString(5, ejercicioFiscal);
            insertados += psInsertaEncabezado.executeUpdate();
            log.debug("Object: " + String.valueOf("Insertados en encabezado: " + insertados + " registros "));
            rsFolioConsolidacion = psInsertaEncabezado.getGeneratedKeys();
            psInsertaDetalle.setInt(1, nFolioConsolidacion);
            insertados += psInsertaDetalle.executeUpdate();
            log.debug("Object: " + String.valueOf("Insertados en detalle: " + insertados + " registros "));
            return nFolioConsolidacion;
        } finally {
            CloseObject.closeObject(rsFolioConsolidacion, false);
            CloseObject.closeObject(psInsertaEncabezado, false);
            CloseObject.closeObject(psInsertaDetalle, false);
        }
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sREFERENCIA1_107 = "'" + sTimeStamp + "'";
        if (bIntegra)
            sREFERENCIA1_107 = "DCD.caNoContrarrecibo";
        // ENCABEZADO DEL DOCCOMP
        String Sql = "	select distinct " + "		PDE.nFolioRegistroIngreso," + "		'H' H," + "		PDE.cRamo," + "		'RHQ'," + "		'' SOL_PAGO," + "		'3'," + "		" + sREFERENCIA1_107 + " FOLIO_INTERNO," + " 		" + sREFERENCIA1_107 + " COMODIN" + "	from  dbo.tRegistroIngresoEncabezado PDE" + "	WHERE PDE.nFolioRegistroIngreso in (" + listaIds + ") ";
        try {
            pstmntH = conn.prepareStatement(Sql);
            System.out.println(Sql);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                String sCampo = "'S04929'";
                //DETALLE DEL DOCCOMP
                //07 ??
                String //07 ??
                Sql2 = " select distinct " + "		PDE.cRamo," + "		PDE.caNoContrarrecibo, " + "		CONVERT(nvarchar(10), PDE.fAplicacion,103)," + "		CONVERT(nvarchar(10), PDE.fAplicacion,103) + ' 12:00:00 a.m.', " + "		" + sCampo + "," + "		case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen'," + "		'85' TIPO_OPE," + "		'05' TIVA," + "		'0' DCD_VALOR," + "		CONVERT(decimal(17, 2), PDE.mImporte) MONTO," + "		CONVERT(DECIMAL(17, 2), 0) DCD_IVA," + "		CONVERT(DECIMAL(17, 2), 0) DCD_IVADES," + "		CONVERT(DECIMAL(17, 2), 0) DCD_ISR," + "		CONVERT(DECIMAL(17, 2), 0) DCD_MIL5," + "		CONVERT(DECIMAL(17, 2), 0) DCD_MIL2," + "		CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET," + "		CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION," + "		CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION," + "		'0' DCD_CTOEXT," + "		PDE.caNoContrarrecibo DCD_FACTURA," + "		PDE.cConcepto," + "		PDE.caNoContrarrecibo" + "	from dbo.tRegistroIngresoEncabezado PDE  WITH (NOLOCK) " + "	INNER JOIN dbo.tRegistroIngresoDetalle RGD  WITH (NOLOCK) ON PDE.nFolioRegistroIngreso = RGD.nFolioRegistroIngreso" + "	INNER JOIN tBeneficiario B  WITH (NOLOCK) " + "		ON B.dRFC = 'CNF010405EG1' " + "	where PDE.nFolioRegistroIngreso in (" + listaIds + ")";
                pstmntD = conn.prepareStatement(Sql2);
                System.out.println(Sql2);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 21; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                if (!bIntegra)
                    break;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static int updateHeaderRIFEnvioSICOP(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tRegistroIngresoEncabezado SET nEnviadoSICOP = 1 WHERE nFolioRegistroIngreso IN (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static int ActualizaRendimientosGreenMex(Connection conn, int folio, String mImporteRendimientos, String mImporteRendimientosGM) throws Exception {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tRegistroIngresoDetalle SET mImporteRendimientosGM = " + mImporteRendimientosGM + ", mImporteRendimientos = " + mImporteRendimientos + ", RFC = 'GREENMEX' WHERE nFolioRegistroIngreso = " + folio + " AND nDocRenglon = 1");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static RegistrosIngresosDetalle getRegistroIngresosDetalleNuevo(Connection conn, int nFolio) throws Exception {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RegistrosIngresosDetalle rid = null;
        pstm = conn.prepareStatement("SELECT * FROM tRegistroIngresoDetalle WITH (NOLOCK) WHERE nFolioRegistroIngreso = ? AND nDocRenglon = 1");
        log.debug("Object: " + String.valueOf("Leyendo tRegistroIngresoEncabezado del Folio: " + nFolio));
        pstm.setInt(1, nFolio);
        res = pstm.executeQuery();
        if (res.next()) {
            rid = new RegistrosIngresosDetalle();
            rid.setmImporteRendimientos(res.getDouble("mImporteRendimientos"));
            rid.setmImporteRendimientosGM(res.getDouble("mImporteRendimientosGM"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return rid;
    }

    public static String insertaRegistroRazonSolicalIP(Connection conn, RegistroIngresoRazonSocial rzip, String cPrograma, Usuario u) throws Exception {
        String mensaje = "";
        PreparedStatement psInsertRazonSocialIP = null;
        String cClaveCRI = "";
        try {
            cClaveCRI = ObtenerCRI(conn, Integer.parseInt(cPrograma));
            psInsertRazonSocialIP = conn.prepareStatement("INSERT INTO tRegistroIngresoRazonSocial (nFolioRegistroIngreso, cClave,	cClaveCRI, fRecepcionRecurso, cOrigenTransferencia, cNombreGestion,	nesExtranjero, cesDonativo, cComprobanteFiscal, U_LOGIN ) VALUES (?,?,?,?,?,?,?,?,?,?)");
            //folio del registro ingreso
            psInsertRazonSocialIP.setInt(1, rzip.getnFolioRegistroIngreso());
            psInsertRazonSocialIP.setString(2, rzip.getcClave());
            psInsertRazonSocialIP.setString(3, cClaveCRI);
            psInsertRazonSocialIP.setString(4, rzip.getfRecepcionRecurso());
            psInsertRazonSocialIP.setString(5, rzip.getcOrigenTransferencia());
            psInsertRazonSocialIP.setString(6, rzip.getcNombreGestion());
            psInsertRazonSocialIP.setInt(7, rzip.getNesExtranjero());
            psInsertRazonSocialIP.setString(8, rzip.getCesDonativo());
            psInsertRazonSocialIP.setString(9, rzip.getcComprobanteFiscal());
            psInsertRazonSocialIP.setString(10, u.getLogin());
            psInsertRazonSocialIP.execute();
            log.debug("Object: " + String.valueOf(psInsertRazonSocialIP.toString()));
            actualizaCRI(conn, rzip.getnFolioRegistroIngreso(), cClaveCRI, rzip.getcClave());
            conn.commit();
            mensaje = "insercion";
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                    mensaje = "error";
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                    e2.printStackTrace();
                }
            throw e;
        } finally {
            CloseObject.closeObject(psInsertRazonSocialIP, false);
        }
        return mensaje;
    }

    private static String ObtenerCRI(Connection conn, int idprograma) {
        String cClaveCRI = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String sSQL = "SELECT cClaveCRI \r\n" + "FROM tCatProgramasProyectos WITH (NOLOCK) \r\n" + "JOIN tCatalogoCRI_v2 WITH (NOLOCK) ON SUBSTRING(cCuenta,1,11) = SUBSTRING(cCuentaIngreso,1,11) \r\n" + "WHERE nIdPrograma = ? ";
            pstmnt = conn.prepareStatement(sSQL);
            log.debug("Object: " + String.valueOf(sSQL));
            pstmnt.setInt(1, idprograma);
            log.debug("Object: " + String.valueOf("Programa: " + idprograma));
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cClaveCRI = rs.getString(1);
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt = null;
        }
        return cClaveCRI;
    }

    public static void actualizaCRI(Connection conn, int folio, String cClaveCRI, String cClave) throws SQLException {
        PreparedStatement pstmnt = null, pstmnt2 = null, pstmntR = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE tRegistroIngresoDetalle SET CRI = '" + cClaveCRI + "/" + cClave + "' WHERE nFolioRegistroIngreso = " + folio);
            pstmnt.executeUpdate();
            if ("GREENMEX".equals(cClave)) {
                pstmnt2 = conn.prepareStatement("UPDATE tRegistroIngresoDetalle SET RFC = '" + cClave + "' WHERE nFolioRegistroIngreso = " + folio);
                pstmnt2.executeUpdate();
            }
            pstmntR = conn.prepareStatement("INSERT INTO tRegistroIngresoDetalle (nFolioRegistroIngreso, nDocRenglon, cEvento, EP, mImporte, mImporteNegativo, nMes, cCentroContable, mSaldo, CTAB, RFC, mImporteRetencion, cUnidadResponsable, mImporteRendimientosGM, mImporteRendimientos, CRI, mImporteMod)\r\n" + "SELECT nFolioRegistroIngreso, 2, cEvento, '', 0, 0, nMes, cCentroContable, 0, CTAB, '', 0, cUnidadResponsable, 0, 0, '" + CRIR + "', mImporte \r\n" + "from tRegistroIngresoDetalle WHERE nFolioRegistroIngreso = " + folio);
            pstmntR.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(pstmnt2);
        }
    }

    public static RegistroIngresoRazonSocial getRegistroIngresoRazonSocial(Connection conn, int nFolio) throws Exception {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RegistroIngresoRazonSocial rirs = null;
        pstm = conn.prepareStatement("SELECT * FROM tRegistroIngresoRazonSocial WITH (NOLOCK) WHERE nFolioRegistroIngreso = ?");
        log.debug("Object: " + String.valueOf("Leyendo tRegistroIngresoRazonSocial del Folio: " + nFolio));
        pstm.setInt(1, nFolio);
        res = pstm.executeQuery();
        if (res.next()) {
            rirs = new RegistroIngresoRazonSocial();
            rirs.setcClave(res.getString("cClave"));
            rirs.setfRecepcionRecurso(res.getString("fRecepcionRecurso"));
            rirs.setcOrigenTransferencia(res.getString("cOrigenTransferencia"));
            rirs.setcNombreGestion(res.getString("cNombreGestion"));
            rirs.setNesExtranjero(res.getInt("nesExtranjero"));
            rirs.setCesDonativo(res.getString("cesDonativo"));
            rirs.setcComprobanteFiscal(res.getString("cComprobanteFiscal"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return rirs;
    }

    private static String aplicaEnvioAlertas(Connection conn, int nFolio) throws Exception {
        ResultSet res = null;
        PreparedStatement pstm = null;
        String aplicaCorreo = "";
        try {
            pstm = conn.prepareStatement("SELECT CASE WHEN COUNT(*) = 0 THEN 'SI' ELSE 'NO' END aplicaCorreo FROM tRegistroIngresoDetalle DET\r\n" + "JOIN tRegistroIngresoEventosSinCorreo EC ON DET.cEvento = EC.cEvento\r\n" + "WHERE nFolioRegistroIngreso = ?");
            log.debug("Object: " + String.valueOf("Leyendo tRegistroIngresoRazonSocial del Folio: " + nFolio));
            pstm.setInt(1, nFolio);
            res = pstm.executeQuery();
            if (res.next()) {
                aplicaCorreo = res.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(res);
        }
        return aplicaCorreo;
    }

    public static void envioAlertas(Connection conn, int nFolio) throws Exception {
        //String[] cben = listaCBEN.split( "," );
        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSet rs2 = null;
        PreparedStatement ps2 = null;
        String subject = "Registro de Ingresos Propios";
        log.info("Object: {}", "Correo: " + subject);
        try {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            String body = getCuerpoCorreoVistoBueno(conn, nFolio);
            String dest = cabl.getSystemSetting("CORREO_REGISTRO_INGRESO");
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, dest, body);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(ps2);
        }
    }

    static String getCuerpoCorreoVistoBueno(Connection conn, int nFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        String fAplicacion = null;
        String concepto = null;
        String importe = null;
        String nombre = null;
        String fRecepcion = null;
        String origenTransfer = null;
        String nombreGestion = null;
        String esExtranjero = null;
        String esDonativo = null;
        String requiereCFDI = null;
        String programa = null;
        try {
            query.append("SELECT ENC.nFolioRegistroIngreso ");
            query.append(" , fAplicacion ");
            query.append(" , cConcepto ");
            query.append(" , CAST(ENC.mImporte AS VARCHAR(20)) AS mImporte ");
            query.append(" , cNombre ");
            query.append(" , fRecepcionRecurso ");
            query.append(" , cOrigenTransferencia ");
            query.append(" , cNombreGestion ");
            query.append(" , CASE WHEN nesExtranjero = 1 THEN 'SI' ");
            query.append(" 	   WHEN nesExtranjero = 0 THEN 'NO' ");
            query.append(" END AS esExtrajero ");
            query.append(" , cesDonativo ");
            query.append(" , cComprobanteFiscal ");
            query.append(" , dCuenta AS programa ");
            query.append("FROM tRegistroIngresoEncabezado ENC WITH (NOLOCK) ");
            query.append("JOIN tCatProgramasProyectos P WITH (NOLOCK) ON ENC.cPrograma = nIdPrograma ");
            query.append("JOIN tRegistroIngresoRazonSocial IRZ WITH (NOLOCK) ON ENC.nFolioRegistroIngreso = IRZ.nFolioRegistroIngreso ");
            query.append("JOIN tRazonSocialIP RZ WITH (NOLOCK) ON IRZ.cClave = RZ.cClave ");
            query.append("WHERE ENC.nFolioRegistroIngreso = ? ");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                fAplicacion = rs.getString(2);
                concepto = rs.getString(3);
                importe = rs.getString(4);
                nombre = rs.getString(5);
                fRecepcion = rs.getString(6);
                origenTransfer = rs.getString(7);
                nombreGestion = rs.getString(8);
                esExtranjero = rs.getString(9);
                esDonativo = rs.getString(10);
                requiereCFDI = rs.getString(11);
                programa = rs.getString(12);
            } else
                throw new Exception("No fue posible encontrar el cConcepto para el folio " + nFolio);
            String mailBody = "<html>";
            mailBody += "\n\t<head>";
            mailBody += "\n\t<meta charset=\"UTF-8\">";
            mailBody += "\n\t<style type=\"text/css\">";
            mailBody += "\n\tbody {";
            mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
            mailBody += "\n\t\t	font-size: 12px;";
            mailBody += "\n\t}";
            mailBody += "\n\ttable {";
            mailBody += "\n\t\tfont-size: 12px;";
            mailBody += "\n\t\tcolor: #333333;";
            mailBody += "\n\t\tborder-width: 1px;";
            mailBody += "\n\t\tborder-color: #666666;";
            mailBody += "\n\t\tborder-collapse: collapse;";
            mailBody += "\n\t}";
            mailBody += "\n\ttable th {";
            mailBody += "\n\t\tborder-width: 1px;";
            mailBody += "\n\t\tpadding: 8px;";
            mailBody += "\n\t\tborder-style: solid;";
            mailBody += "\n\t\tborder-color: #666666;";
            mailBody += "\n\t\tbackground-color: #dedede;";
            mailBody += "\n\t}";
            mailBody += "\n\ttable td {";
            mailBody += "\n\t\tborder-width: 1px;";
            mailBody += "\n\t\tpadding: 8px;";
            mailBody += "\n\t\tborder-style: solid;";
            mailBody += "\n\t\tborder-color: #666666;";
            mailBody += "\n\t\tbackground-color: #ffffff;";
            mailBody += "\n\t}";
            mailBody += "\n\t</style>";
            mailBody += "</head>";
            mailBody += "\n\t<body>";
            mailBody += "\n\t\t<form id=\"Form\" name=\"FormRegistroIngresosIP\" >";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Para su conocimiento y efectos correspondientes, se le informa que fue autorizado el siguiente folio de un Registro de Ingresos Propios";
            mailBody += "	</p>";
            mailBody += "	<p>";
            mailBody += "		Tipo de ingreso: <b>" + programa + "</b>";
            mailBody += "	</p>";
            mailBody += "	<br />";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Folio del Registo de Ingresos</th>";
            mailBody += "				<th>Fecha Aplicacion Contable</th>";
            mailBody += "				<th>Concepto</th>";
            mailBody += "				<th>Monto</th>";
            mailBody += "				<th>Razon Social</th>";
            mailBody += "				<th>Es Extranjero</th>";
            mailBody += "				<th>Fecha Recepcion Recurso</th>";
            mailBody += "				<th>Origen de la Transferencia</th>";
            mailBody += "				<th>Nombre quien Gestiono el Recurso</th>";
            mailBody += "				<th>Es Donativo</th>";
            mailBody += "				<th>Requiere Comprobante Fiscal</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            mailBody += "<tr>";
            mailBody += "\n<td>" + nFolio + "</td>";
            mailBody += "\n<td>" + fAplicacion + "</td>";
            mailBody += "\n<td>" + concepto + "</td>";
            mailBody += "\n<td>" + importe + "</td>";
            mailBody += "\n<td>" + nombre + "</td>";
            mailBody += "\n<td>" + esExtranjero + "</td>";
            mailBody += "\n<td>" + fRecepcion + "</td>";
            mailBody += "\n<td>" + origenTransfer + "</td>";
            mailBody += "\n<td>" + nombreGestion + "</td>";
            mailBody += "\n<td>" + esDonativo + "</td>";
            mailBody += "\n<td>" + requiereCFDI + "</td>";
            mailBody += "</tr>";
            mailBody += "		</tbody>";
            mailBody += "	</table>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static int insertaCompromisoRegistroIngreso(Connection conn, String sTimeStamp, Usuario sUsuario, String ejercicioFiscal, int nFolioConsolidacion, String centroContableUser) throws Exception {
        log.info("Object: {}", "Insertando consolidacion de Ingreso Fiscal. Folio Integracion[" + sTimeStamp + "] Usuario[" + sUsuario + "] Ejercicio Fiscal[" + ejercicioFiscal + "]");
        int insertados = 0;
        int strFolioCompromiso = 0;
        PreparedStatement pstmntE = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        PreparedStatement pstmUpSeqCompromiso = null;
        PreparedStatement pstmSeqCompromiso = null;
        //OBTENER SEQUENCE DEL COMRPMISO
        pstmUpSeqCompromiso = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'COMPROMISO' ");
        pstmUpSeqCompromiso.executeUpdate();
        pstmSeqCompromiso = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'COMPROMISO' ");
        rs = pstmSeqCompromiso.executeQuery();
        if (rs.next()) {
            strFolioCompromiso = rs.getInt("seq_value");
        }
        CFSequenceManager sequence = CFSequenceManager.getInstance();
        int seqFolio = 100000 + sequence.nextVal("CO-" + centroContableUser);
        String contrarecibo = centroContableUser + "CO" + EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal() + String.valueOf(seqFolio);
        StringBuilder sqlInsertCompromisoEnc = new StringBuilder();
        sqlInsertCompromisoEnc.append("INSERT INTO tCompromisoEncabezado ");
        sqlInsertCompromisoEnc.append("            (nFolioCompromiso,");
        sqlInsertCompromisoEnc.append("             fCarga, ");
        sqlInsertCompromisoEnc.append("             cIdContrato, ");
        sqlInsertCompromisoEnc.append("             cTipoContrato, ");
        sqlInsertCompromisoEnc.append("             fAplicacion, ");
        sqlInsertCompromisoEnc.append("             cCentroContable, ");
        sqlInsertCompromisoEnc.append("             cRamo, ");
        sqlInsertCompromisoEnc.append("             cUnidadResponsable, ");
        sqlInsertCompromisoEnc.append("             cDocumentoHaplicado, ");
        sqlInsertCompromisoEnc.append("             caNoCompromiso, ");
        sqlInsertCompromisoEnc.append("             nEnviadoSICOP, ");
        sqlInsertCompromisoEnc.append("             cTipoPoliza, ");
        sqlInsertCompromisoEnc.append("             nMes, ");
        sqlInsertCompromisoEnc.append("             aEjercicioFiscal, ");
        sqlInsertCompromisoEnc.append("             cUnidadResponsableContable, ");
        sqlInsertCompromisoEnc.append("             cDescripcionPoliza, ");
        sqlInsertCompromisoEnc.append("             usuario, ");
        sqlInsertCompromisoEnc.append("             cRadicado) ");
        sqlInsertCompromisoEnc.append("SELECT ? AS nFolioCompromiso, ");
        sqlInsertCompromisoEnc.append("       GETDATE() AS fCarga, ");
        sqlInsertCompromisoEnc.append("       nIdIntegracion AS nFolioConsolidacion, ");
        sqlInsertCompromisoEnc.append("       'RE' AS cTipoContrato, ");
        sqlInsertCompromisoEnc.append("       GETDATE() AS fAplicacion, ");
        sqlInsertCompromisoEnc.append("       ? AS cCentroContable, ");
        sqlInsertCompromisoEnc.append("       ENCABEZADO.cRamo, ");
        sqlInsertCompromisoEnc.append("       ENCABEZADO.cUnidadResponsable, ");
        sqlInsertCompromisoEnc.append("       'S' AS cDocumentoHaplicado, ");
        sqlInsertCompromisoEnc.append("       ? AS caNoCompromiso, ");
        sqlInsertCompromisoEnc.append("       0 AS nEnviadoSICOP, ");
        sqlInsertCompromisoEnc.append("       'DI' AS cTipoPoliza, ");
        sqlInsertCompromisoEnc.append("       MONTH(GETDATE()) AS nMes, ");
        sqlInsertCompromisoEnc.append("       ENCABEZADO.aEjercicioFiscal, ");
        sqlInsertCompromisoEnc.append("       ENCABEZADO.cUnidadResponsableContable, ");
        sqlInsertCompromisoEnc.append("       'REGISTRO ORIGINAL DE LA INTEGRACION DEL REGISTRO DE INGRESO FISCAL ' + nIdIntegracion AS cDescripcionPoliza, ");
        sqlInsertCompromisoEnc.append("       ENCABEZADO.U_LOGIN, 'N' AS cRadicado  ");
        sqlInsertCompromisoEnc.append("FROM   dbo.tRegistroIngresoEncabezado ENCABEZADO WITH (nolock) ");
        sqlInsertCompromisoEnc.append("LEFT OUTER JOIN dbo.tLayoutsCreadosRegistroIngresoEncabezado LAYOUT WITH (nolock) ");
        sqlInsertCompromisoEnc.append("       ON ENCABEZADO.canocontrarrecibo = LAYOUT.snocontrarrecibo ");
        sqlInsertCompromisoEnc.append("INNER JOIN dbo.tconsolidacionrelaciongastosencabezado consolidacion_encabezado WITH (nolock) ");
        sqlInsertCompromisoEnc.append("       ON LAYOUT.sauxiliarcomodin = consolidacion_encabezado.nidintegracion ");
        sqlInsertCompromisoEnc.append("WHERE  LAYOUT.sauxiliarcomodin IS NOT NULL ");
        sqlInsertCompromisoEnc.append("       AND nfolioconsolidacion = ? ");
        StringBuilder sqlInsertCompromisoDet = new StringBuilder();
        sqlInsertCompromisoDet.append("INSERT INTO dbo.tcompromisodetalle");
        sqlInsertCompromisoDet.append("        ( nfoliocompromiso ,");
        sqlInsertCompromisoDet.append("          ndocrenglon ,");
        sqlInsertCompromisoDet.append("          ep ,");
        sqlInsertCompromisoDet.append("          cevento ,");
        sqlInsertCompromisoDet.append("          mimporte ,");
        sqlInsertCompromisoDet.append("          mimportenegativo ,");
        sqlInsertCompromisoDet.append("          cmes ,");
        sqlInsertCompromisoDet.append("          ccentrocontable)");
        sqlInsertCompromisoDet.append("SELECT ? AS nFolioCompromiso, ");
        sqlInsertCompromisoDet.append("       Row_number()  OVER( ORDER BY ep, nMes ASC) AS nDocRenglon, ");
        sqlInsertCompromisoDet.append("       ep,  ");
        sqlInsertCompromisoDet.append("       'CMP001' AS cevento, ");
        sqlInsertCompromisoDet.append("       Sum(DETALLE.mImporte) AS mimportemasiva, ");
        sqlInsertCompromisoDet.append("       Sum(DETALLE.mImporte)*-1 AS mImporteNegativo, ");
        sqlInsertCompromisoDet.append("       DETALLE.nMes, ");
        sqlInsertCompromisoDet.append("       DETALLE.cCentroContable ");
        sqlInsertCompromisoDet.append("FROM   dbo.tRegistroIngresoEncabezado ENCABEZADO WITH (nolock) ");
        sqlInsertCompromisoDet.append("INNER JOIN dbo.tRegistroIngresoDetalle DETALLE WITH (nolock) ");
        sqlInsertCompromisoDet.append("		  ON ENCABEZADO.nFolioRegistroIngreso = DETALLE.nFolioRegistroIngreso ");
        sqlInsertCompromisoDet.append("LEFT OUTER JOIN dbo.tLayoutsCreadosRegistroIngresoEncabezado LAYOUT WITH (nolock) ");
        sqlInsertCompromisoDet.append("       ON ENCABEZADO.canocontrarrecibo = LAYOUT.snocontrarrecibo ");
        sqlInsertCompromisoDet.append("INNER JOIN dbo.tconsolidacionrelaciongastosencabezado consolidacion_encabezado WITH (nolock) ");
        sqlInsertCompromisoDet.append("       ON LAYOUT.sauxiliarcomodin = consolidacion_encabezado.nidintegracion ");
        sqlInsertCompromisoDet.append("WHERE  LAYOUT.sauxiliarcomodin IS NOT NULL ");
        sqlInsertCompromisoDet.append("       AND nfolioconsolidacion = ? ");
        sqlInsertCompromisoDet.append("GROUP  BY ep, ");
        sqlInsertCompromisoDet.append("       consolidacion_encabezado.nfolioconsolidacion, ");
        sqlInsertCompromisoDet.append("       DETALLE.ccentrocontable, ");
        sqlInsertCompromisoDet.append("       DETALLE.nmes ");
        PreparedStatement psInsertaEncabezado = null;
        PreparedStatement psInsertaDetalle = null;
        ResultSet rsFolioConsolidacion = null;
        try {
            log.debug("Object: " + String.valueOf("Query Insert Encabezado[" + sqlInsertCompromisoEnc + "]"));
            log.debug("Object: " + String.valueOf("Query Insert Detalle[" + sqlInsertCompromisoDet + "]"));
            pstmntE = conn.prepareStatement(sqlInsertCompromisoEnc.toString());
            pstmntD = conn.prepareStatement(sqlInsertCompromisoDet.toString());
            pstmntE.setInt(1, strFolioCompromiso);
            pstmntE.setString(2, centroContableUser);
            pstmntE.setString(3, contrarecibo);
            pstmntE.setInt(4, nFolioConsolidacion);
            insertados += pstmntE.executeUpdate();
            log.debug("Object: " + String.valueOf("Insertados en encabezado: " + insertados + " registros "));
            pstmntD.setInt(1, strFolioCompromiso);
            pstmntD.setInt(2, nFolioConsolidacion);
            insertados += pstmntD.executeUpdate();
            log.debug("Object: " + String.valueOf("Insertados en detalle: " + insertados + " registros "));
            return insertados;
        } finally {
            CloseObject.closeObject(rsFolioConsolidacion, false);
            CloseObject.closeObject(psInsertaEncabezado, false);
            CloseObject.closeObject(psInsertaDetalle, false);
        }
    }

    private static void insertaEdoCuenta(Connection conn, int nFolio, String tipoPago) throws Exception {
        CallableStatement cs = null;
        String query = "{call sp_inserta_encabezadoGreenMex( ?, ? )}";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, nFolio);
            cs.setString(2, tipoPago);
            cs.execute();
        } finally {
            CloseObject.closeObject(cs, false);
        }
    }
}
