package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import com.axtel.contratos.repositories.SuficienciaPagoDirectoEncabezadoManager;
import com.axtel.egresos.viaticos.Agenda;
import com.axtel.egresos.viaticos.ViaticosBusinessLogic;
import com.axtel.egresos.viaticos.core.AgendaDAO;
import com.axtel.egresos.viaticos.core.ComisionDAO;
import com.axtel.egresos.viaticos.core.GeneraSolicitudViaticos;
import com.axtel.egresos.viaticos.core.TransporteDAO;
import com.syc.adquisiciones.ConsumePAASInterface;
import com.syc.adquisiciones.businessLogic.ConsumePAASImpl;
import com.syc.cfdi.core.FacturaManager;
import com.syc.contable.AccountingEngine;
import com.syc.contable.CancelaDocumento;
import com.syc.contable.PagosDiversosBussinessLogic;
import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.OperacionAjenaManager;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.contable.core.SolicitudPOLIZAFirmaElectronica;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.dsmngr.DataSourceManager;
import com.syc.egresos.firmante.FirmanteBussinessLogic;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.IngresoGreenMexManager;
import com.syc.sai.contabilidad.PasivoDiferidoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import com.syc.ws.inventario.WSManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CierrePresupuestal extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    String DATE_FORMAT = "yyyy-MM-dd";

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    // today
    Calendar c1 = Calendar.getInstance();

    String today = sdf.format(c1.getTime());

    String reportPath;

    Usuario u = null;

    private String empleadoVoBo;

    private String empleadoCaptura;

    private String empleadoAutoriza;

    boolean autorizadoPorFiel;

    private String jniName;

    private FirmanteBussinessLogic fbl = null;

    public CierrePresupuestal(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public JSONObject guardarPagoAMFCierre(String tipoDocumento, String numPagoAMF, String numFolioAMF, String nClaveAMF, String rfcAMF, String aEjercicioFiscal, String fCaptura, String fPago, String cCentroContable, String u_login, String caNoContrarrecibo, String cUnidadResponsable, String cUnidadResponsableAplica) throws SQLException {
        PreparedStatement pstmnL = null, pstmnE = null, pstmnD = null, pstmnDi = null, pstmnU = null, pstmnUD = null;
        ResultSet rsL = null, rsE = null, rsD = null, rsDi = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        try {
            conn = getConnection();
            // System.out.println("numPagoAMF:" +numPagoAMF);
            String[] doc = tipoDocumento.split("/");
            String[] pag = numPagoAMF.split("/");
            String[] rfc = rfcAMF.split("/");
            String[] caN = caNoContrarrecibo.split("/");
            String tablaEnc = "";
            String tablaDet = "";
            String tablaEncLayout = "";
            String cuentaBancaria = "";
            String nFolio = "";
            String sNoContrarrecibo = "";
            String contrarrecibo = "";
            String conCuentaBancaria = "sinCuentaBancaria";
            String cuentaBan = "";
            String folio = "";
            String cConcepto = "";
            String mImporteNeto = "";
            String cConceptoEnc = "";
            String noGuardado = "";
            double mImporteNetoEnc = 0.00;
            int intDi = 0;
            int intU = 0;
            int intUD = 0;
            int numRegistros = doc.length - 1;
            int numCorrectos = 0;
            for (int i = 0; i < doc.length; i++) {
                if (doc[i].equalsIgnoreCase("Directo")) {
                    tablaEnc = "tPagoDirectoEncabezado";
                    cConcepto = "cConcepto";
                    mImporteNeto = "mImporteNeto";
                    tablaDet = "tPagoDirectoDetalle";
                    contrarrecibo = "caNoContrarrecibo";
                    nFolio = "nFolioPagoDirecto";
                    tablaEncLayout = "tLayoutsCreadosHeader";
                    sNoContrarrecibo = "sNoContrarrecibo";
                    cuentaBancaria = "sCUENTA_BANCARIA";
                } else if (doc[i].equalsIgnoreCase("Obra")) {
                    tablaEnc = "tPAGOOBRAEncabezado";
                    cConcepto = "cConcepto";
                    mImporteNeto = "mImporteNeto";
                    tablaDet = "tPAGOOBRADetalle";
                    contrarrecibo = "caNoContrarrecibo";
                    nFolio = "nFolioPAGOOBRA";
                    tablaEncLayout = "tLayoutsCreadosPagoObrasHeader";
                    sNoContrarrecibo = "sNoContrarrecibo";
                    cuentaBancaria = "sCUENTA_BANCARIA";
                } else if (doc[i].equalsIgnoreCase("Diverso")) {
                    tablaEnc = "tPAGODIVERSOEncabezado";
                    cConcepto = "cConcepto";
                    mImporteNeto = "mImporteNeto";
                    tablaDet = "tPAGODIVERSODetalle";
                    contrarrecibo = "caNoContrarrecibo";
                    nFolio = "nFolioPAGODIVERSO";
                    tablaEncLayout = "tLayoutsCreadosPagosDiversosHeader";
                    sNoContrarrecibo = "sNoContrarrecibo";
                    cuentaBancaria = "sCUENTA_BANCARIA";
                } else if (doc[i].equalsIgnoreCase("Federalizado")) {
                    tablaEnc = "tPAGOFEDERALIZADOEncabezado";
                    cConcepto = "cConcepto";
                    mImporteNeto = "mImporteNeto";
                    tablaDet = "tPAGOFEDERALIZADODetalle";
                    contrarrecibo = "caNoContrarrecibo";
                    nFolio = "nFolioPAGOFEDERALIZADO";
                    tablaEncLayout = "tFEDERALIZADOLAYOUTAUT";
                    sNoContrarrecibo = "nFolioPAGOFEDERALIZADO";
                    cuentaBancaria = "dCuentaBancaria";
                } else if (doc[i].equalsIgnoreCase("relacion")) {
                    tablaEnc = "tRELACIONGASTOSEncabezado";
                    cConcepto = "cConcepto";
                    mImporteNeto = "mImporteNeto";
                    tablaDet = "tRELACIONGASTOSDetalle";
                    contrarrecibo = "caNoContrarrecibo";
                    nFolio = "nFolioRELACIONGASTOS";
                    tablaEncLayout = "tLayoutsCreadosRelacionGastosHeader";
                    sNoContrarrecibo = "sNoContrarrecibo";
                    cuentaBancaria = "sCUENTA_BANCARIA";
                }
                cuentaBan = "";
                conCuentaBancaria = "sinCuentaBancaria";
                folio = "";
                // System.out.println("SELECT DISTINCT "+nFolio+" AS nFolio,
                // "+cuentaBancaria+" AS cBancaria, "+cConcepto+" AS cConcepto,
                // "+mImporteNeto+" AS mImporteNeto FROM "+tablaEnc+" INNER JOIN
                // "+tablaEncLayout+" ON "+contrarrecibo+" =
                // "+sNoContrarrecibo+" WHERE "+contrarrecibo+" = "+caN[i]);
                pstmnL = conn.prepareStatement("SELECT DISTINCT " + nFolio + " AS nFolio, " + cuentaBancaria + " AS cBancaria, " + cConcepto + " AS cConcepto, " + mImporteNeto + " AS mImporteNeto FROM " + tablaEnc + " INNER JOIN " + tablaEncLayout + " ON " + contrarrecibo + " = " + sNoContrarrecibo + "  WHERE " + contrarrecibo + " = ? ");
                pstmnL.setString(1, caN[i]);
                rsL = pstmnL.executeQuery();
                while (rsL.next()) {
                    cuentaBan = rsL.getString("cBancaria");
                    folio = rsL.getString("nFolio");
                    conCuentaBancaria = "conCuentaBancaria";
                    cConceptoEnc = rsL.getString("cConcepto");
                    mImporteNetoEnc = rsL.getDouble("mImporteNeto");
                }
                if (conCuentaBancaria.equalsIgnoreCase("conCuentaBancaria")) {
                    pstmnE = conn.prepareStatement("INSERT INTO tPagoAMF (numPagoAMF,numFolioAMF,nClaveAMF,referenciaAMF,rfcAMF,cuentaBancaria,aEjercicioFiscal,cUnidadResponsable,fechaCaptura,fechaPago,importePago,cCentroContable,U_LOGIN,estatus) " + " VALUES ('" + pag[i] + "','" + numFolioAMF + "','" + nClaveAMF + "','" + cConceptoEnc + "','" + rfc[i] + "','" + cuentaBan + "', " + " '" + aEjercicioFiscal + "','" + cUnidadResponsableAplica + "','" + fCaptura + "','" + fPago + "'," + mImporteNetoEnc + ", " + " '" + cCentroContable + "','" + u_login + "','Activo') ");
                    pstmnE.executeUpdate();
                    pstmnD = conn.prepareStatement("SELECT EP, mImporteNeto FROM " + tablaDet + " WHERE " + nFolio + " = ? ");
                    pstmnD.setString(1, folio);
                    rsD = pstmnD.executeQuery();
                    while (rsD.next()) {
                        pstmnDi = conn.prepareStatement("INSERT INTO tPagoAMFDetalle (numPagoAMF,EP,importePago)" + " VALUES ('" + pag[i] + "','" + rsD.getString("EP") + "','" + rsD.getString("mImporteNeto") + "' ) ");
                        intDi = pstmnDi.executeUpdate();
                    }
                    if (intDi > 0) {
                        pstmnUD = conn.prepareStatement("UPDATE " + tablaEnc + " SET NumPagoAMF = '" + pag[i] + "' WHERE caNoContrarrecibo = ? ");
                        pstmnUD.setString(1, caN[i]);
                        intUD = pstmnUD.executeUpdate();
                        pstmnU = conn.prepareStatement("UPDATE tAcuerdosMFDetalle SET mSaldo  = (mSaldo - " + mImporteNetoEnc + ") where nClaveAMF = ? AND cFolio = ? AND cUnidadResponsable = ? ");
                        pstmnU.setString(1, nClaveAMF);
                        pstmnU.setString(2, numFolioAMF);
                        pstmnU.setString(3, cUnidadResponsable);
                        intU = pstmnU.executeUpdate();
                        if (intU > 0 && intUD > 0) {
                            numCorrectos = numCorrectos + i;
                            json.put("estatus", "guardado");
                            conn.commit();
                        } else {
                            conn.rollback();
                            noGuardado += " No Se Guardaron los Documentos: " + caN[i] + " \n";
                            json.put("estatus", noGuardado);
                        }
                    }
                } else {
                    json.put("sinCuenta", "Sin Cuenta Bancaria: " + caN[i]);
                }
                if (numCorrectos == numRegistros) {
                    json.put("estatus", "guardado");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error:" + e);
            try {
                json.put("estatus", "error");
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            CloseObject.closeObject(pstmnL);
            CloseObject.closeObject(pstmnE);
            CloseObject.closeObject(pstmnD);
            CloseObject.closeObject(pstmnDi);
            CloseObject.closeObject(pstmnU);
            CloseObject.closeObject(pstmnUD);
            CloseObject.closeObject(rsL);
            CloseObject.closeObject(rsE);
            CloseObject.closeObject(rsD);
            CloseObject.closeObject(rsDi);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject guardarAdefa(String tipoDocumento, String caNoContrarrecibo, String nFolioAdefa, String u_login) {
        PreparedStatement pstmnEnc = null, pstmnDet = null, pstmnE = null, pstmnD = null, pstmnU = null;
        // , rsUpd = null;
        ResultSet rsEnc = null, rsDet = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String campoSelectEnc = "";
        String campoSelectDet = "";
        String tablaEnc = "";
        String tablaDet = "";
        String nomFolioDoc = "";
        String nFolioDoc = "";
        String cEvento = "";
        try {
            conn = getConnection();
            String[] doc = tipoDocumento.split("/");
            String[] caN = caNoContrarrecibo.split("/");
            String[] nFolioAd = nFolioAdefa.split("/");
            for (int i = 0; i < doc.length; i++) {
                if (doc[i].equalsIgnoreCase("Directo")) {
                    campoSelectEnc = "nFolioPagoDirecto AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, cIdRFC, mImporteNeto , " + " ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, U_LOGIN, cUnidadResponsableContable, cTipoPoliza";
                    campoSelectDet = "nDocRenglon, cEvento, cEjercicio, RFC, cCentroContable, EP, mImporteNeto, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO ";
                    tablaEnc = "tPagoDirectoEncabezado";
                    tablaDet = "tPagoDirectoDetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioPagoDirecto";
                } else if (doc[i].equalsIgnoreCase("Obra")) {
                    campoSelectEnc = "nFolioPAGOOBRA AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, RFC AS cIdRFC, mImporteNeto , " + " ID_TIPO_MOVIMIENTO,  '' AS ID_TIPO_CONCEPTO, U_LOGIN, cUnidadResponsableContable, cTipoPoliza ";
                    campoSelectDet = "nDocRenglon, cEvento, cEjercicio, RFC, cCentroContable, EP, mImporteNeto, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO ";
                    tablaEnc = "tPAGOOBRAEncabezado";
                    tablaDet = "tPAGOOBRADetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioPAGOOBRA";
                } else if (doc[i].equalsIgnoreCase("Rel_Gastos")) {
                    campoSelectEnc = "nFolioRELACIONGASTOS AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, cIdRFC, mImporteNeto , " + " ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, U_LOGIN, cUnidadResponsableContable, cTipoPoliza ";
                    campoSelectDet = "nDocRenglon, cEvento, cEjercicio, RFC, cCentroContable, EP, mImporteNeto, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO ";
                    tablaEnc = "tRELACIONGASTOSEncabezado";
                    tablaDet = "tRELACIONGASTOSDetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioRELACIONGASTOS";
                } else if (doc[i].equalsIgnoreCase("Diverso")) {
                    campoSelectEnc = "nFolioPAGODIVERSO AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, RFC AS cIdRFC, mImporteNeto , " + " ID_TIPO_MOVIMIENTO, '' AS ID_TIPO_CONCEPTO, U_LOGIN, cUnidadResponsableContable, cTipoPoliza ";
                    campoSelectDet = "nDocRenglon, cEvento, cEjercicio, RFC, cCentroContable, EP, mImporteNeto, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO ";
                    tablaEnc = "tPAGODIVERSOEncabezado";
                    tablaDet = "tPAGODIVERSODetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioPAGODIVERSO";
                } else if (doc[i].equalsIgnoreCase("Federalizado")) {
                    campoSelectEnc = "nFolioPAGOFEDERALIZADO AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, RFC AS cIdRFC, mImporteNeto , " + " ID_TIPO_MOVIMIENTO, '' AS ID_TIPO_CONCEPTO, U_LOGIN, cUnidadResponsableContable, cTipoPoliza ";
                    campoSelectDet = "nDocRenglon, cEvento, cEjercicio, RFC, cCentroContable, EP, mImporteNeto, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO ";
                    tablaEnc = "tPAGOFEDERALIZADOEncabezado";
                    tablaDet = "tPAGOFEDERALIZADODetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioPAGOFEDERALIZADO";
                } else if (doc[i].equalsIgnoreCase("Ajenas")) {
                    campoSelectEnc = "nFolioOperAjenas AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, cIDRFC AS cIdRFC, mImportes AS mImporteNeto , " + " '' AS ID_TIPO_MOVIMIENTO, '' AS ID_TIPO_CONCEPTO, U_LOGIN, cUnidadResponsableContable, cTipoPoliza ";
                    campoSelectDet = "nDocRenglon, cEvento, aEjercicioFiscal AS cEjercicio, RFC, cCentroContable, Ep AS EP, mTotal AS mImporteNeto, '' AS nCapitulo, '' AS ID_TIPO_MOVIMIENTO, '' AS ID_TIPO_CONCEPTO ";
                    tablaEnc = "tOperAjenasEncabezado";
                    tablaDet = "tOperAjenasDetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioOperAjenas";
                } else if (doc[i].equalsIgnoreCase("Nomina")) {
                    campoSelectEnc = "nFolioNomina AS nFolio, cRamo, fAplicacion, cUnidadResponsable, aEjercicioFiscal, cIdRFC AS cIdRFC, mImporteNeto  , " + " ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cIdUsuarioCaptura AS U_LOGIN, cUnidadResponsableContable, cTipoPoliza ";
                    campoSelectDet = "nDocRenglon, cEvento, aEjercicioFiscal AS cEjercicio, RFC, cCentroContable, Ep AS EP, mImporteNeto, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO ";
                    tablaEnc = "tNominaEncabezado";
                    tablaDet = "tNominaDetalle";
                    caNoContrarrecibo = "caNoContrarrecibo";
                    nomFolioDoc = "nFolioNomina";
                }
                /* Buscar Encabezado Documento */
                pstmnEnc = conn.prepareStatement("SELECT " + campoSelectEnc + " FROM " + tablaEnc + " WITH(NOLOCK) WHERE " + caNoContrarrecibo + " = ? ");
                pstmnEnc.setString(1, caN[i]);
                rsEnc = pstmnEnc.executeQuery();
                if (rsEnc.next()) {
                    // Folio Para
                    nFolioDoc = rsEnc.getString("nFolio");
                    // Buscar
                    // Detalle
                    double importeNegEnc = 0 - rsEnc.getDouble("mImporteNeto");
                    pstmnU = conn.prepareStatement("UPDATE tAdefaEncabezado SET Activo = 0 WHERE caNoContrarrecibo = ? ");
                    pstmnU.setString(1, caN[i]);
                    pstmnU.executeUpdate();
                    /* Guardar Encabezado Adefa */
                    pstmnE = conn.prepareStatement("INSERT INTO tAdefaEncabezado (nFolioAdefa,tipoDocumento,fAplicacion,cRamo,cUnidadResponsable,aEjercicioFiscal, " + " cIdRFC,mImporte,caNoContrarrecibo,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,U_LOGIN,cUnidadResponsableContable, cTipoPoliza, Activo )" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    pstmnE.setInt(1, Integer.parseInt(nFolioAd[i], 10));
                    pstmnE.setString(2, doc[i]);
                    pstmnE.setDate(3, rsEnc.getDate("fAplicacion"));
                    pstmnE.setString(4, rsEnc.getString("cRamo"));
                    pstmnE.setString(5, rsEnc.getString("cUnidadResponsable"));
                    pstmnE.setString(6, rsEnc.getString("aEjercicioFiscal"));
                    // pstmnE.setString(7,
                    // rsEnc.getString("cIdEntidadContable"));
                    pstmnE.setString(7, rsEnc.getString("cIdRFC"));
                    pstmnE.setDouble(8, importeNegEnc);
                    pstmnE.setString(9, caN[i]);
                    pstmnE.setString(10, rsEnc.getString("ID_TIPO_MOVIMIENTO"));
                    pstmnE.setString(11, rsEnc.getString("ID_TIPO_CONCEPTO"));
                    pstmnE.setString(12, u_login);
                    pstmnE.setString(13, rsEnc.getString("cUnidadResponsableContable"));
                    pstmnE.setString(14, rsEnc.getString("cTipoPoliza"));
                    pstmnE.setInt(15, 1);
                    pstmnE.executeUpdate();
                    /* Buscar Detalle Documento */
                    pstmnDet = conn.prepareStatement("SELECT " + campoSelectDet + " FROM " + tablaDet + " WITH(NOLOCK) WHERE " + nomFolioDoc + " = ? AND cEvento != 'ANTICIPO' ");
                    pstmnDet.setString(1, nFolioDoc);
                    rsDet = pstmnDet.executeQuery();
                    while (rsDet.next()) {
                        double importeNegDet = 0 - rsDet.getDouble("mImporteNeto");
                        String[] cE = rsDet.getString("cEvento").split("_");
                        cEvento = "AD";
                        for (int ii = 1; ii < cE.length; ii++) {
                            cEvento += "_";
                            cEvento += cE[ii];
                        }
                        /* Guardar Detalle Adefa */
                        pstmnD = conn.prepareStatement("INSERT INTO tAdefaDetalle (nFolioAdefa, nDocRenglon, cEvento, cEjercicio, cCentroContable, RFC, EP," + " mImporte, nCapitulo, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO) " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?  ) ");
                        pstmnD.setInt(1, Integer.parseInt(nFolioAd[i], 10));
                        pstmnD.setInt(2, rsDet.getInt("nDocRenglon"));
                        pstmnD.setString(3, cEvento);
                        pstmnD.setString(4, rsDet.getString("cEjercicio"));
                        pstmnD.setString(5, rsDet.getString("cCentroContable"));
                        pstmnD.setString(6, rsDet.getString("RFC"));
                        pstmnD.setString(7, rsDet.getString("EP"));
                        pstmnD.setDouble(8, importeNegDet);
                        pstmnD.setString(9, rsDet.getString("nCapitulo"));
                        pstmnD.setString(10, rsDet.getString("ID_TIPO_MOVIMIENTO"));
                        pstmnD.setString(11, rsDet.getString("ID_TIPO_CONCEPTO"));
                        pstmnD.executeUpdate();
                    }
                    AccountingEngine accEng = new AccountingEngine();
                    accEng.makeAccountingApplication(conn, "ADEFA", nFolioAd[i], "tAdefaEncabezado", "tAdefaDetalle", "nFolioAdefa");
                    conn.commit();
                    json.put("estatus", "guardado");
                } else {
                    log.warn("Error: No Encontro Informacion");
                    json.put("estatus", "sinInformacion");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error:" + e);
            try {
                json.put("estatus", "error");
                conn.rollback();
            } catch (Exception ee) {
                log.error("Error: Cerrando rollback");
            }
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(pstmnU);
            CloseObject.closeObject(pstmnE);
            CloseObject.closeObject(pstmnDet);
            CloseObject.closeObject(pstmnD);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(rsDet);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject cancelaAdefa(String numFolioAdefa) {
        Connection conn = null;
        JSONObject json = new JSONObject();
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            if (accEng.cancelAccountingApplication(conn, "ADEFA", numFolioAdefa, "tAdefaEncabezado", "tAdefaDetalle", "nFolioAdefa", today)) {
                json.put("estatus", "guardado");
                conn.commit();
            } else {
                json.put("estatus", "error");
                conn.rollback();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject cancelaEjercidoPagado(String numFolioPago, String cTipoDocumento) {
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String errorApl = null;
        PreparedStatement pstmnFolio = null;
        ResultSet rsFolio = null;
        PreparedStatement pstmnUpdate = null;
        String sTablaEnc = "";
        String sTablaDet = "";
        String sFolio = "";
        String sCampoFolio = "nFolio" + cTipoDocumento;
        String sTipoDocto = cTipoDocumento;
        sTablaEnc = "t" + cTipoDocumento + "Encabezado";
        sTablaDet = "t" + cTipoDocumento + "Detalle";
        sFolio = numFolioPago;
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            if (accEng.cancelAccountingApplication(conn, cTipoDocumento, numFolioPago, sTablaEnc, sTablaDet, sCampoFolio, today)) {
                if (cTipoDocumento.equalsIgnoreCase("PAGODIRECTO") || cTipoDocumento.equalsIgnoreCase("RELACIONGASTOS")) {
                    pstmnEnc = conn.prepareStatement("SELECT nFolioPagoApartado FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago = ? and nFolioPago = ? and cDocumentoHaplicado = 'S' ");
                    pstmnEnc.setString(1, cTipoDocumento);
                    pstmnEnc.setString(2, numFolioPago);
                    rsEnc = pstmnEnc.executeQuery();
                    if (rsEnc.next()) {
                        numFolioPago = rsEnc.getString("nFolioPagoApartado");
                        cTipoDocumento = "PagoApartado";
                        if (accEng.cancelAccountingApplication(conn, cTipoDocumento, numFolioPago, "t" + cTipoDocumento + "Encabezado", "t" + cTipoDocumento + "Detalle", "nFolio" + cTipoDocumento, today)) {
                            pstmnFolio = conn.prepareStatement("SELECT * FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago = ? and nFolioPagoApartado = ? and cDocumentoHaplicado = 'C' ");
                            pstmnFolio.setString(1, sTipoDocto);
                            pstmnFolio.setString(2, numFolioPago);
                            rsFolio = pstmnFolio.executeQuery();
                            if (rsFolio.next()) {
                                pstmnUpdate = conn.prepareStatement(" UPDATE " + sTablaEnc + " SET cDocumentoHaplicado = 'C' WHERE " + sCampoFolio + " = ? ");
                                pstmnUpdate.setString(1, sFolio);
                                if (pstmnUpdate.execute()) {
                                    json.put("estatus", "guardado");
                                    conn.commit();
                                }
                            }
                        }
                    } else {
                        json.put("estatus", "guardado");
                        conn.commit();
                    }
                } else {
                    json.put("estatus", "guardado");
                    conn.commit();
                }
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            try {
                json.put("estatus", errorApl);
                conn.rollback();
            } catch (Exception x) {
            }
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(pstmnFolio);
            CloseObject.closeObject(pstmnUpdate);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(rsFolio);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject aplicarCompromisoCapituloMil(String caNoContraRecibo) {
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        List<String> arrayMessageReturn = new ArrayList<>();
        String msg = "";
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            pstmnEnc = conn.prepareStatement("SELECT nFolioCompromisoNomina FROM tCompromisoNominaEncabezado WITH(NOLOCK) WHERE caNoCompromiso = ? ");
            pstmnEnc.setString(1, caNoContraRecibo);
            rsEnc = pstmnEnc.executeQuery();
            String nFolio = "";
            if (rsEnc.next()) {
                nFolio = rsEnc.getString("nFolioCompromisoNomina");
            }
            if (accEng.makeAccountingApplication(conn, "COMPROMISO", nFolio, "tCompromisoNominaEncabezado", "tCompromisoNominaDetalle", "nFolioCompromisoNomina")) {
                arrayMessageReturn.add(" COMPROMISO APLICADO CONTABLEMENTE CANCELADO");
                json.put("estatus", "guardado");
                conn.commit();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            msg = exc.toString();
            try {
                json.put("estatus", msg);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            try {
                conn.rollback();
            } catch (Exception excc) {
                log.warn("Error: rollback ", excc);
            }
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject aplicarNominaCapituloMil(String caNoContraRecibo) {
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String msg = "";
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            pstmnEnc = conn.prepareStatement("SELECT nFolioNOMINA FROM tNOMINAEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = ? ");
            pstmnEnc.setString(1, caNoContraRecibo);
            rsEnc = pstmnEnc.executeQuery();
            String nFolio = "";
            if (rsEnc.next()) {
                nFolio = rsEnc.getString("nFolioNOMINA");
            }
            if (accEng.makeAccountingApplication(conn, "NOMINA", nFolio, "tNOMINAEncabezado", "tNOMINADetalle", "nFolioNomina")) {
                PasivoDiferidoManager.aplicarPasivoDiferido(conn, "NOMINA", nFolio, "tNOMINAEncabezado", "tNOMINADetalle", "nFolioNomina");
                json.put("estatus", "guardado");
                conn.commit();
            }
        } catch (Exception exc) {
            log.error("Error occurred", "Error aplicando contablemente " + exc);
            try {
                msg = exc.toString();
                json.put("estatus", msg);
                conn.rollback();
            } catch (Exception excc) {
                log.error("Error occurred", "Error: rollback " + excc);
            }
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject totalAdefa() {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        try {
            conn = getConnection();
            pstmnt = conn.prepareStatement("SELECT CONVERT(VARCHAR,SUM(CONVERT(money,mImporte)),1) AS importeTotal, COUNT(nFolioAdefa) totalDoc FROM tAdefaEncabezado WHERE cDocumentoHaplicado = 'S' ");
            rs = pstmnt.executeQuery();
            String importeTotal = "";
            String totalDocumento = "";
            if (rs.next()) {
                importeTotal = rs.getString("importeTotal");
                totalDocumento = rs.getString("totalDoc");
            }
            int valor = importeTotal.length();
            importeTotal = importeTotal.substring(1, valor);
            String respuesta = importeTotal + "/" + totalDocumento;
            json.put("respuesta", respuesta);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject validarArchivo(String caNoContrarrecibo, String strDocumento) {
        PreparedStatement pstmntEnc = null, pstmntDet = null;
        ResultSet rs = null, rsDet = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String folio = "";
        String cuantos = "";
        String total = "";
        try {
            conn = getConnection();
            if (strDocumento.equalsIgnoreCase("compromiso")) {
                pstmntEnc = conn.prepareStatement("SELECT nFolioCompromisoNomina FROM tCompromisoNominaEncabezado WHERE caNoCompromiso = ? ");
                pstmntEnc.setString(1, caNoContrarrecibo);
                rs = pstmntEnc.executeQuery();
                if (rs.next()) {
                    folio = rs.getString("nFolioCompromisoNomina");
                }
                pstmntDet = conn.prepareStatement("SELECT MAX(nDocRenglon) AS cuantos, CONVERT(VARCHAR,SUM(CONVERT(money,mImporte)),1) AS total FROM tCompromisoNominaDetalle WHERE nFolioCompromisoNomina = ? ");
                pstmntDet.setInt(1, Integer.parseInt(folio, 10));
                rsDet = pstmntDet.executeQuery();
                if (rsDet.next()) {
                    cuantos = rsDet.getString("cuantos");
                    total = rsDet.getString("total");
                }
            } else if (strDocumento.equalsIgnoreCase("compromisoAmpliacion")) {
                pstmntEnc = conn.prepareStatement("SELECT nFolioCompromisoNomina FROM tCompromisoNominaEncabezado WHERE caNoCompromiso = ? ");
                pstmntEnc.setString(1, caNoContrarrecibo);
                rs = pstmntEnc.executeQuery();
                if (rs.next())
                    folio = rs.getString("nFolioCompromisoNomina");
                pstmntDet = conn.prepareStatement("SELECT MAX(nDocRenglon) AS cuantos, CONVERT(VARCHAR,SUM(CONVERT(money,mImporte)),1) AS total FROM tCompromisoNominaDetalle WHERE nFolioCompromisoNomina = ? ");
                pstmntDet.setInt(1, Integer.parseInt(folio, 10));
                rsDet = pstmntDet.executeQuery();
                if (rsDet.next()) {
                    cuantos = rsDet.getString("cuantos");
                    total = rsDet.getString("total");
                }
            } else if (strDocumento.equalsIgnoreCase("nomina")) {
                pstmntEnc = conn.prepareStatement("SELECT nFolioNomina FROM tNominaEncabezado WHERE caNoContrarrecibo = ? ");
                pstmntEnc.setString(1, caNoContrarrecibo);
                rs = pstmntEnc.executeQuery();
                if (rs.next()) {
                    folio = rs.getString("nFolioNomina");
                }
                pstmntDet = conn.prepareStatement("SELECT MAX(nDocRenglon) AS cuantos, CONVERT(VARCHAR,SUM(CONVERT(money,mImporteNeto)),1) AS total FROM tNominaDetalle WHERE nFolioNOMINA =  ? ");
                pstmntDet.setInt(1, Integer.parseInt(folio, 10));
                rsDet = pstmntDet.executeQuery();
                if (rsDet.next()) {
                    cuantos = rsDet.getString("cuantos");
                    total = rsDet.getString("total");
                }
            }
            String respuesta = cuantos + "/" + total;
            json.put("respuesta", respuesta);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(pstmntEnc);
            CloseObject.closeObject(pstmntDet);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsDet);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject aplicarCancelacionCompromiso(String caNoContraRecibo) {
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        List<String> arrayMessageReturn = new ArrayList<>();
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            pstmnEnc = conn.prepareStatement("SELECT nFolioCancelaCompromiso FROM tCancelaCompromisoEncabezado WITH(NOLOCK) WHERE caNoCompromiso = ? ");
            pstmnEnc.setString(1, caNoContraRecibo);
            rsEnc = pstmnEnc.executeQuery();
            String nFolio = "";
            if (rsEnc.next()) {
                nFolio = rsEnc.getString("nFolioCancelaCompromiso");
            }
            if (accEng.makeAccountingApplication(conn, "CANCELACOMPROMISO", nFolio, "tCancelaCompromisoEncabezado", "tCancelaCompromisoDetalle", "nFolioCancelaCompromiso")) {
                arrayMessageReturn.add(" COMPROMISO APLICADO CONTABLEMENTE");
                json.put("estatus", "guardado");
                conn.commit();
            } else {
                arrayMessageReturn.add(" CIERRE DE COMPROMISOS NO APLICADO");
                json.put("estatus", "error");
                conn.rollback();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject aplicarNominaCapMilDev(String caNoContraRecibo) {
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            pstmnEnc = conn.prepareStatement("SELECT nFolioNOMINACLC FROM tNOMINADevEncabezado WITH(NOLOCK) WHERE caNoContrarreciboCLC = ? ");
            pstmnEnc.setString(1, caNoContraRecibo);
            rsEnc = pstmnEnc.executeQuery();
            String nFolio = "";
            if (rsEnc.next()) {
                nFolio = rsEnc.getString("nFolioNOMINACLC");
            }
            if (accEng.makeAccountingApplication(conn, "NOMINADEV", nFolio, "tNOMINADevEncabezado", "tNOMINADevDetalle", "nFolioNominaCLC")) {
                PasivoDiferidoManager.aplicarReduccionDevengado(conn, "NOMINADEV", nFolio, "tNOMINADevEncabezado", "tNOMINADevDetalle", "nFolioNominaCLC");
                json.put("estatus", "guardado");
                conn.commit();
            } else {
                json.put("estatus", "error");
                conn.rollback();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject aplicarMotor(String caNoCompromiso, String campo, String tablaEnc, String campoCondicion, String tablaDet, String tipoAplicar, String fAplicar) {
        PreparedStatement pstmnEnc = null, pstmnEvento = null;
        PreparedStatement pstmnt = null;
        ResultSet rsEnc = null, rsEvto = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        boolean success = false;
        String errorApl = null;
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            String nFolio = "";
            String cEvento = "";
            pstmnEnc = conn.prepareStatement("SELECT " + campo + " FROM " + tablaEnc + " WITH(NOLOCK) WHERE " + campoCondicion + " = ? ");
            pstmnEnc.setString(1, caNoCompromiso);
            rsEnc = pstmnEnc.executeQuery();
            if (rsEnc.next()) {
                nFolio = rsEnc.getString("" + campo + "");
            }
            if ("tcajaencabezado".equalsIgnoreCase(tablaEnc)) {
                if (CajaManager.esSolicitudAplicada(conn, Integer.parseInt(nFolio))) {
                    json.put("estatus", "guardado");
                    return json;
                }
                pstmnt = conn.prepareStatement("UPDATE tcajaencabezado SET faplicacion = '" + fAplicar + "' WHERE nFoliocaja = ?");
                pstmnt.setString(1, caNoCompromiso);
                pstmnt.execute();
                pstmnEvento = conn.prepareStatement("SELECT TOP 1 cEvento FROM tcajadetalle WITH(NOLOCK) WHERE nFolioCaja = ? ");
                pstmnEvento.setString(1, caNoCompromiso);
                rsEvto = pstmnEvento.executeQuery();
                if (rsEvto.next()) {
                    cEvento = rsEvto.getString("cEvento");
                }
                CajaManager.actualizaRemanentesComprobacion(conn, nFolio);
                if ("8_2_2".equals(cEvento) || "8_1_2".equals(cEvento) || "8_2_16".equals(cEvento)) {
                    CajaManager.actualizaRemanentesDevolucion(conn, nFolio);
                } else if ("8_2_1".equals(cEvento) || "8_2_7".equals(cEvento) || "8_1_1".equals(cEvento) || "8_2_12".equals(cEvento)) {
                    CajaManager.insertaEstadoCtaViaticos(conn, Integer.parseInt(nFolio));
                }
                CajaManager.actualizaEnvioSICOP(conn, Integer.parseInt(nFolio), SolicitudFirmaElectronica.GENERA_LAYOUT);
                // conn.commit();
            }
            if (!"tPrecomFinancieroEncabezado".equalsIgnoreCase(tablaEnc)) {
                if ("tPAGODIVERSOEncabezado".equals(tablaEnc)) {
                    String destino = CajaManager.esGREENMEX(conn, Integer.parseInt(nFolio));
                    if ("CPGM".equals(destino)) {
                        CajaManager.actualizaEstadoCuentaGreenMex(conn, Integer.parseInt(nFolio));
                        success = IngresoGreenMexManager.aplicarIngresoGreenMex(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
                    }
                }
                if ("tRELACIONGASTOSEncabezado".equals(tablaEnc)) {
                    String cEsPagoCuotas = RelacionGastosManager.esPagoCuotasLAUDOS(conn, Integer.parseInt(nFolio, 10));
                    if ("S".equals(cEsPagoCuotas)) {
                        RelacionGastosManager.updateRFCCuotas(conn, Integer.parseInt(nFolio, 10));
                    }
                }
                success = accEng.makeAccountingApplication(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
            }
            /*
			 * Se quita la aplicacion del pasivo diferido por la parte de la
			 * retencion de LAUDOS
			 */
            success = PasivoDiferidoManager.aplicarPasivoDiferido(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
            if (tipoAplicar.equalsIgnoreCase("PAGODIRECTO") || tipoAplicar.equalsIgnoreCase("PAGODIVERSO") || tipoAplicar.equalsIgnoreCase("PAGOOBRA") || tipoAplicar.equalsIgnoreCase("PAGOFEDERALIZADO") || tipoAplicar.equalsIgnoreCase("RELACIONGASTOS"))
                EgresosManager.actualizaInformacionEmpleados(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo, getEmpleadoCaptura(), getEmpleadoVoBo(), getEmpleadoAutoriza());
            boolean esViatico = false;
            if (success) {
                if ("TRELACIONGASTOSENCABEZADO".equalsIgnoreCase(tablaEnc.toUpperCase())) {
                    RelacionGastosManager.actualizaTipoRG(conn, Integer.parseInt(nFolio, 10));
                    int nFolioCaja = RelacionGastosManager.esRelacionComprobacion(conn, Integer.parseInt(nFolio, 10));
                    // Validacion para que si es partida 3700 tenga informe de
                    // comisión
                    esViatico = RelacionGastosManager.esViaticos(conn, Integer.parseInt(nFolio, 10));
                    if (esViatico) {
                        boolean tieneInforme = RelacionGastosManager.tieneInformeComision(conn, Integer.parseInt(nFolio, 10));
                        if (!tieneInforme) {
                            throw new Exception("La solicitud no tiene informe de comisión " + nFolio + ". No se puede avanzar, favor de descartar y volver a capturar correctamente.");
                        }
                    }
                    if (nFolioCaja > 0) {
                        double montosRelacion = RelacionGastosManager.getMontoRelacionGastos(conn, Integer.parseInt(nFolio, 10));
                        if (RelacionGastosManager.remanenteSuficiente(conn, nFolioCaja, montosRelacion)) {
                            CajaManager.insertViaticosDetalle(conn, nFolioCaja, Integer.parseInt(nFolio, 10), null, montosRelacion);
                            CajaManager.updateViaticosEncabezado(conn, nFolioCaja, -montosRelacion);
                            String evento = CajaManager.validaEventoCajaChica(conn, nFolioCaja);
                            double remanente = CajaManager.buscaRemanente(conn, nFolioCaja);
                            if ("8_1_1".equalsIgnoreCase(evento) && remanente == 0.0) {
                                CajaManager.eliminaRFCAsignadoConCajaChica(conn, nFolioCaja);
                            }
                        } else
                            throw new Exception("No queda saldo remanente en la solicitud " + nFolioCaja);
                    }
                    // Validacion para que si es GreenMex Insertar el Ingreso y
                    // disminuir el remanente del anticipo del ingreso
                    boolean esGreenMex = RelacionGastosManager.esGreenMex(conn, Integer.parseInt(nFolio, 10));
                    if (esGreenMex) {
                        CajaManager.actualizaEstadoCuentaGreenMex(conn, Integer.parseInt(nFolio));
                        success = IngresoGreenMexManager.aplicarIngresoGreenMex(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
                    }
                } else if ("TPAGODIVERSOENCABEZADO".equalsIgnoreCase(tablaEnc)) {
                    if (esCapitulo5Mil(conn, Integer.parseInt(nFolio, 10)) || esAlmacenVirtual(conn, Integer.parseInt(nFolio, 10)) || (esRecepcionDeMaterial(conn, Integer.parseInt(nFolio, 10)) && validaPartidayTipoPago(conn, Integer.parseInt(nFolio, 10)) && !(validaEsPedidoCap2(conn, Integer.parseInt(nFolio, 10))))) {
                        int status = WSManager.sendAdquistion(conn, nFolio, 0);
                        if (status != 0 && status != -2) {
                            json.put("estatus", "Error de respuesta del Web Service");
                            throw new Exception("Error de respuesta del Web Service para el folio:" + nFolio);
                        }
                    }
                    String msjUpdateAmortiza = "";
                    msjUpdateAmortiza = amortizacionUpdate(conn, nFolio, false);
                    if (!"".equalsIgnoreCase(msjUpdateAmortiza)) {
                        throw new Exception(msjUpdateAmortiza);
                    }
                }
                if ("tPrecomFinancieroEncabezado".equalsIgnoreCase(tablaEnc)) {
                    boolean aplica = false;
                    // CompromisoBussinessLogic cbl = new
                    // CompromisoBussinessLogic( null );
                    ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
                    boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
                    if (esSAIAlterno) {
                        accEng.makeAccountingApplication(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
                        accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(nFolio), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
                    } else {
                        aplica = compromisoValidar(Integer.parseInt(nFolio, 10));
                        /* ARLA 11072025 Se aplican solo compromiso de IP */
                        if (aplica) {
                            accEng.makeAccountingApplication(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
                            accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(nFolio), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
                            boolean insertado = CompromisoManager.updateHeaderCompromisosMenor300UMAS(conn, 2, nFolio, "-1");
                        } else {
                            accEng.makeAccountingApplication(conn, tipoAplicar, nFolio, tablaEnc, tablaDet, campo);
                        }
                    }
                }
                if (success && (tipoAplicar.equalsIgnoreCase("PAGODIRECTO") || tipoAplicar.equalsIgnoreCase("PAGODIVERSO") || tipoAplicar.equalsIgnoreCase("PAGOOBRA") || tipoAplicar.equalsIgnoreCase("PAGOFEDERALIZADO") || tipoAplicar.equalsIgnoreCase("RELACIONGASTOS"))) {
                    EgresosManager.validaRetencionRegimenRESICO(conn, tipoAplicar, Integer.parseInt(nFolio));
                    SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
                    solicitudPagoPrinter.setDetail(tablaDet);
                    solicitudPagoPrinter.setDocument(tipoAplicar);
                    solicitudPagoPrinter.setField(campo);
                    solicitudPagoPrinter.setFileExtension("pdf");
                    solicitudPagoPrinter.setHeader(tablaEnc);
                    solicitudPagoPrinter.setIdField(Integer.parseInt(nFolio));
                    solicitudPagoPrinter.setReportPath(getReportPath());
                    solicitudPagoPrinter.setUsuario(getUsuario());
                    if (isAutorizadoPorFiel()) {
                        String lastDocName = solicitudPagoPrinter.getDocName();
                        try {
                            solicitudPagoPrinter.setDocName("Solicitud de Pago Firmada");
                            FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
                            // Valida que un pago es profoem para enviarlo a
                            // prefirmar
                            PagosDiversosBussinessLogic pbb = new PagosDiversosBussinessLogic();
                            if (tipoAplicar.equalsIgnoreCase("PAGODIVERSO") && pbb.esPagoPROFOEM(Integer.parseInt(nFolio))) {
                                solicitudPagoPrinter.notificaPrefirmante(conn);
                            } else {
                                solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
                            }
                            // Si es Viatico guarda el informe de comisión
                            if (esViatico) {
                                solicitudPagoPrinter.setDocName("Informe de Comision");
                                FirmaElectronicaManager.generaArchivoInformeComision(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
                            }
                        } finally {
                            solicitudPagoPrinter.setDocName(lastDocName);
                        }
                    } else {
                        FirmaElectronicaManager.avanzaEstatusSICOP(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.AUT_LAYOUT);
                    }
                    FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, tablaEnc, campo, String.valueOf(nFolio), isAutorizadoPorFiel());
                }
                json.put("estatus", "guardado");
                conn.commit();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            try {
                json.put("estatus", errorApl);
                conn.rollback();
            } catch (Exception x) {
            }
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public boolean compromisoValidar(int folio) throws Exception {
        Connection conn = null;
        boolean regreso = false;
        int rIP = 0, fiscal = 0;
        /*
		 * boolean contratoAnterior = false; String contrato ="", tipo ="";
		 */
        try {
            conn = getConnection();
            rIP = CompromisoManager.compromisoEsIP(conn, folio);
            fiscal = CompromisoManager.compromisoNoEsIP(conn, folio);
            if (rIP >= 1 && fiscal == 0) {
                regreso = true;
            }
            /*
				 * else { contrato = CompromisoManager.consultaContrato( conn,
				 * folio ); tipo = contrato.substring( 0, 2 );
				 * 
				 * if ("PE".equalsIgnoreCase( tipo )) regreso= true; else {
				 * contratoAnterior = CompromisoManager.contratoAnterior(conn,
				 * contrato); if (!contratoAnterior) { regreso =
				 * CompromisoManager.contratoMenor300UMAS( conn, folio ); } } }
				 */
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regreso;
    }

    /**
     * @return the empleadoVoBo
     */
    public String getEmpleadoVoBo() {
        return empleadoVoBo;
    }

    /**
     * @return the empleadoCaptura
     */
    public String getEmpleadoCaptura() {
        return empleadoCaptura;
    }

    /**
     * @return the empleadoAutoriza
     */
    public String getEmpleadoAutoriza() {
        return empleadoAutoriza;
    }

    private boolean esCapitulo5Mil(Connection conn, int nFolio) throws SQLException {
        boolean bReturn = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (!esRecepcionDeMaterial(conn, nFolio)) {
            bReturn = false;
        } else {
            String query = " SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS esCap5Mil " + " FROM tPAGODIVERSODetalle WITH (NOLOCK) " + " WHERE nFolioPAGODIVERSO = ? AND SUBSTRING(EP, 32, 1) = '5'";
            try {
                ps = conn.prepareStatement(query);
                ps.setInt(1, nFolio);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int esCap5Mil = 0;
                    esCap5Mil = rs.getInt("esCap5Mil");
                    if (esCap5Mil == 1) {
                        bReturn = true;
                    }
                }
            } finally {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(ps);
            }
        }
        return bReturn;
    }

    private boolean esAlmacenVirtual(Connection conn, int nFolio) throws SQLException {
        boolean bReturn = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String query = "SELECT nIdEntraAlmacen FROM v_atentaNota WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                int estatus = 0;
                estatus = rs.getInt("nIdEntraAlmacen");
                if (estatus == 2) {
                    bReturn = true;
                }
            }
            return bReturn;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public JSONObject aplicarMotorPoliza(String nFoliodocPoliza, String campo, String tablaEnc, String tablaDet, String tipoAplicar) throws Exception {
        log.debug("Iniciando aplicacion de documento poliza manual");
        log.info("Object: {}", "Se aplicara el documento: " + nFoliodocPoliza + "," + campo + "," + tablaEnc + "," + tablaDet + "," + tipoAplicar);
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String errorApl = null;
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            boolean success = accEng.makeAccountingApplicationWithoutEvent(conn, tipoAplicar, nFoliodocPoliza, tablaEnc, tablaDet, campo);
            if (success) {
                json.put("estatus", "guardado");
                if (!"DOCPOLIZACANCEL".equals(tipoAplicar)) {
                    /* VGCFIEL */
                    EgresosManager.actualizaInformacionEmpleados(conn, tipoAplicar, nFoliodocPoliza, tablaEnc, tablaDet, campo, getEmpleadoCaptura(), getEmpleadoVoBo(), getEmpleadoAutoriza());
                    SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPOLIZAFirmaElectronica();
                    solicitudPagoPrinter.setDetail(tablaDet);
                    solicitudPagoPrinter.setDocument("POLIZA");
                    solicitudPagoPrinter.setField(campo);
                    solicitudPagoPrinter.setFileExtension("pdf");
                    solicitudPagoPrinter.setHeader(tablaEnc);
                    solicitudPagoPrinter.setIdField(Integer.parseInt(nFoliodocPoliza));
                    solicitudPagoPrinter.setReportPath(getReportPath());
                    solicitudPagoPrinter.setUsuario(getUsuario());
                    if (isAutorizadoPorFiel()) {
                        solicitudPagoPrinter.setDocName("Poliza Firmada");
                        FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Poliza Firmada", false);
                        solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
                    } else {
                        FirmaElectronicaManager.avanzaEstatusSICOP(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.POLIZA_AUTORIZADA);
                    }
                    FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, tablaEnc, campo, String.valueOf(nFoliodocPoliza), isAutorizadoPorFiel());
                }
            }
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback " + e2);
                }
            errorApl = exc.toString();
            json.put("estatus", errorApl);
        } finally {
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    /**
     * Cancela poliza manual de firma autografa.
     *
     * La cancelacion de poliza manual se afecta diferente que las poliza
     * automaticas ya que este elimina los movimientos.
     *
     * @param cFolioDocumento
     *            Folio del documento (Completo)
     * @param Fecha
     *            Fecha de cancelacion
     * @param Usuario
     *            usuario que realiza la operacion.
     * @param mesAbierto
     *            Mes abierto al momento
     * @return Objeto con la respuesta
     */
    public JSONObject aplicarCancelacionMotorPoliza(String cFolioDocumento, String Fecha, String Usuario, String mesAbierto) {
        return aplicarCancelacionMotorPoliza(cFolioDocumento, Fecha, Usuario, mesAbierto, false);
    }

    /**
     * Cancela poliza manual.
     *
     * La cancelacion de poliza manual se afecta diferente que las poliza
     * automaticas ya que este elimina los movimientos.
     *
     * @param cFolioDocumento
     *            Folio del documento (Completo)
     * @param Fecha
     *            Fecha de cancelacion
     * @param Usuario
     *            usuario que realiza la operacion.
     * @param mesAbierto
     *            Mes abierto al momento
     *
     * @param esFirmaElectronica
     *            bandera que indica si el origen es de una firma electronica.
     * @return Objeto con la respuesta
     */
    @SuppressWarnings("rawtypes")
    public JSONObject aplicarCancelacionMotorPoliza(String cFolioDocumento, String Fecha, String Usuario, String mesAbierto, boolean esFirmaElectronica) {
        JSONObject json = new JSONObject();
        String errorApl = null;
        try {
            String DATE_FORMAT = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            Calendar c1 = Calendar.getInstance();
            String fechaAplicar = "";
            int mesActual = c1.get(Calendar.MONTH);
            if (Integer.parseInt(mesAbierto, 10) != mesActual) {
                c1.set(Calendar.MONTH, Integer.parseInt(mesAbierto, 10) - 1);
                c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
            fechaAplicar = sdf.format(c1.getTime());
            CancelaDocumento canselDocto = new CancelaDocumento(GestionInterface.ATT_CONEXION);
            if (canselDocto.cancelaDoctoNuevo(cFolioDocumento, "DOCPOLIZA", fechaAplicar, new HashMap(), "", Usuario, esFirmaElectronica).contains("APLICADO CONTABLEMENTE")) {
                json.put("estatus", "guardado");
            } else
                json.put("estatus", errorApl);
            json.put("estatus", "guardado");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            try {
                json.put("estatus", errorApl);
            } catch (Exception x) {
            }
        }
        return json;
    }

    public void cancelaFirmaElectronica(Connection conn, String tipoPago, String tDetalle, String keyName, String tEncabezado, int nFolio) throws Exception {
        FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);
        febl.cargaInformacionTramite(tipoPago);
        SolicitudFirmaElectronica sfe = (SolicitudFirmaElectronica) Util.instanceCasoFIEL(febl.getTramiteSolicitud().getClaseImplementa());
        sfe.setDocument(tipoPago);
        sfe.setDetail(tDetalle);
        sfe.setDocument(tipoPago);
        sfe.setField(keyName);
        sfe.setHeader(tEncabezado);
        sfe.setIdField(nFolio);
        sfe.setUsuario(getUsuario());
        sfe.onCancelaTramite(conn, "Rechazo de pago solicitado por FIEL del usuario: " + u.getNombre());
    }

    public JSONObject cancelaDevengado(String tipoDocumento, String nFolio, Usuario usuario, boolean esFirmaElectronica) throws Exception {
        Connection conn = null;
        JSONObject json = new JSONObject();
        try {
            conn = getConnection();
            json = cancelaDevengado(conn, tipoDocumento, nFolio, usuario, esFirmaElectronica);
            conn.commit();
        } catch (Exception exc) {
            String errorApl = exc.toString();
            try {
                conn.rollback();
            } catch (Exception x) {
                log.warn("Error occurred", "Error dando rollback: " + x);
            }
            json.put("estatus", errorApl);
        } finally {
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject aplicarDisminucionDev(String tipoDoc, String campo, String nFolio, String tablaEnc, String tablaDet) {
        PreparedStatement pstmnEnc = null;
        ResultSet rsEnc = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String errorApl = null;
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            if (accEng.makeAccountingApplication(conn, tipoDoc, nFolio, tablaEnc, tablaDet, campo)) {
                // Aplicar la disminucion del apartado cuando los tipos de pago
                // son RG y Directo
                aplicarDisminucionApartado(conn, tipoDoc, nFolio);
                PasivoDiferidoManager.aplicarReduccionDevengado(conn, tipoDoc, nFolio, tablaEnc, tablaDet, campo);
                json.put("estatus", "guardado");
                conn.commit();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            try {
                json.put("estatus", errorApl);
                conn.rollback();
            } catch (Exception x) {
            }
        } finally {
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject cancelaDocumento(String tipoDoc, String nFolio, Usuario usuario) {
        Connection conn = null;
        JSONObject json = new JSONObject();
        String errorApl = null;
        try {
            String[] doc = tipoDoc.split("/");
            String[] nFol = nFolio.split("/");
            conn = getConnection();
            Integer folioViaticos = 0;
            for (int i = 0; i < doc.length; i++) {
                folioViaticos = Integer.parseInt(nFol[i], 10);
                LogCancelaDevengadoManager.BitacoraLog(conn, doc[i], folioViaticos, usuario.getLogin());
                if ("COMSINVIATICOS".equals(doc[i])) {
                    updateViaticosComision(conn, folioViaticos);
                } else if ("VIATICOS".equals(doc[i])) {
                    ComisionDAO.cancelaComision(conn, folioViaticos);
                }
                json.put("estatus", "guardado");
            }
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            try {
                json.put("estatus", errorApl);
                conn.rollback();
            } catch (Exception x) {
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public void updateViaticosComision(Connection conn, int folioViaticos) throws Exception {
        PreparedStatement pstEnc = null, pstBoletos = null, pstTransp = null;
        String queryEncabezado = "update tComisionesSinComprobacionEnc set cDocumentoHAplicado = 'C' where nFolioComision = ?";
        String queryBoletos = "update DET set Status = 'A',  cEsPagado = 'N' from tLayoutVuelosDet det inner join tComisionesSinComprobacionDet com on det.cReferencia = com.cboleto and rfcVuelo = det.rfc where nFolioComision = ?";
        String queryTransporte = "DELETE tTransporteAereo WHERE cTipoPago = 'COMSINVIATICOS' and nFolioPago = ?";
        try {
            pstEnc = conn.prepareStatement(queryEncabezado);
            pstEnc.setInt(1, folioViaticos);
            pstBoletos = conn.prepareStatement(queryBoletos);
            pstBoletos.setInt(1, folioViaticos);
            pstTransp = conn.prepareStatement(queryTransporte);
            pstTransp.setInt(1, folioViaticos);
            pstEnc.executeUpdate();
            log.info("Object: {}", "Se actualizo el encabezado " + folioViaticos);
            int updateBoletos = pstBoletos.executeUpdate();
            log.info("Object: {}", "Se Actualizaron: " + updateBoletos + " Vuelos.");
            pstTransp.executeUpdate();
            log.info("Object: {}", "Se borro el transporte aereo de la solicitud " + folioViaticos);
        } finally {
            CloseObject.closeObject(pstBoletos);
            CloseObject.closeObject(pstEnc);
        }
    }

    public String amortizacionUpdate(Connection conn, String nFolio, Boolean resta) throws Exception {
        String msj = "";
        int valida = 0;
        double mAmortizacionAnticipo = 0.00;
        PreparedStatement psValida = null;
        ResultSet rsValida = null;
        String cIdContrato = "";
        String queryvalida = " SELECT 	CASE WHEN SUM(DIVERSODetalle.mAmortizacionAnticipo) > 0 THEN 1 ELSE 0 END AS valida, " + " 			SUM(DIVERSODetalle.mAmortizacionAnticipo) AS mAmortizacionAnticipo, " + " 			DIVERSOEncabezado.cFolioPAGODIVERSO as cIdContrato " + " FROM 	tPAGODIVERSOEncabezado DIVERSOEncabezado WITH (NOLOCK) " + " INNER JOIN tPAGODIVERSODetalle DIVERSODetalle WITH(NOLOCK) " + " 			ON (DIVERSODetalle.cCentroContable = DIVERSOEncabezado.cCentroContable " + " 				AND DIVERSODetalle.nFolioPAGODIVERSO = DIVERSOEncabezado.nFolioPAGODIVERSO) " + " WHERE 	DIVERSOEncabezado.nFolioPAGODIVERSO = ? " + " GROUP BY DIVERSOEncabezado.cFolioPAGODIVERSO ";
        try {
            psValida = conn.prepareStatement(queryvalida);
            psValida.setInt(1, Integer.parseInt(nFolio));
            rsValida = psValida.executeQuery();
            if (rsValida.next()) {
                valida = rsValida.getInt("valida");
                mAmortizacionAnticipo = rsValida.getDouble("mAmortizacionAnticipo");
                cIdContrato = rsValida.getString("cIdContrato");
                if (valida == 1) {
                    String queryInfoPago = "  SELECT 	CASE WHEN mTotalAnticipo > mAmortizado THEN 1 ELSE 0 END AS ejecutaUpdate " + "  FROM		pContratoDiversoAnticipo WITH (NOLOCK) " + "  WHERE	cIdContrato = ? ";
                    PreparedStatement psInfoPago = null;
                    ResultSet rsInfoPago = null;
                    int resultado = 0;
                    int ejecutaUpdate = 0;
                    try {
                        psInfoPago = conn.prepareStatement(queryInfoPago);
                        psInfoPago.setString(1, cIdContrato);
                        rsInfoPago = psInfoPago.executeQuery();
                        if (rsInfoPago.next()) {
                            ejecutaUpdate = rsInfoPago.getInt("ejecutaUpdate");
                            if (resta) {
                                ejecutaUpdate = 1;
                            }
                            if (ejecutaUpdate == 1) {
                                String update = "";
                                if (resta) {
                                    update = " UPDATE  pContratoDiversoAnticipo " + " SET	   mAmortizado = ( mAmortizado - ? ) " + " WHERE   cIdContrato = ? ";
                                } else {
                                    update = " UPDATE  pContratoDiversoAnticipo " + " SET	   mAmortizado = ( mAmortizado + ? ) " + " WHERE   cIdContrato = ? ";
                                }
                                PreparedStatement psUpdate = null;
                                try {
                                    psUpdate = conn.prepareStatement(update);
                                    psUpdate.setDouble(1, mAmortizacionAnticipo);
                                    psUpdate.setString(2, cIdContrato);
                                    resultado = psUpdate.executeUpdate();
                                    if (resultado == 0) {
                                        msj = "Ocurrio un error al actualizar la amortizacón.";
                                    }
                                } catch (Exception excUpdate) {
                                    log.error("Object: {}", excUpdate);
                                    msj = excUpdate.toString();
                                } finally {
                                    CloseObject.closeObject(psUpdate);
                                }
                            }
                        } else {
                            msj = "No fue posible cargar la informacion del folio: [ " + nFolio + " ].";
                        }
                    } catch (Exception excRead) {
                        log.error("Object: {}", excRead);
                        msj = excRead.toString();
                    } finally {
                        CloseObject.closeObject(rsInfoPago);
                        CloseObject.closeObject(psInfoPago);
                    }
                }
            } else {
                msj = "No fue posible validar la información del folio: [ " + nFolio + " ].";
            }
            return msj;
        } finally {
            CloseObject.closeObject(rsValida);
            CloseObject.closeObject(psValida);
        }
    }

    private static boolean esRecepcionDeMaterial(Connection conn, int folio) throws SQLException {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "SELECT *FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ? and cIdRecepMat like'RM%'";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nfolioPagoDiverso: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
            } else {
                log.info("Object: {}", "El folio del pago diverso " + folio + " está ligado a una recepción de anticipo por tal motivo no se manda llamar el web service.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return respuesta;
    }

    private static boolean validaPartidayTipoPago(Connection conn, int folio) throws SQLException {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select enc.cFolioPAGODIVERSO,enc.nFolioPAGODIVERSO,enc.ID_DESTINO_GASTO,det.ID_TIPO_CONCEPTO,det.OBGT from tPAGODIVERSOEncabezado as enc with(Nolock)" + " inner join tPAGODIVERSODetalle as det with(Nolock) on enc.nFolioPAGODIVERSO=det.nFolioPAGODIVERSO" + " where enc.cDocumentoHaplicado='S' and enc.ID_DESTINO_GASTO in('ALDV','ALRO') and det.ID_TIPO_CONCEPTO='AL'" + " and det.OBGT not in( select cPartida from tCatPartidaWSRestringida with(Nolock) where nActivo=1 )" + " and enc.nFolioPAGODIVERSO=?" + " group by enc.cFolioPAGODIVERSO,enc.nFolioPAGODIVERSO,enc.ID_DESTINO_GASTO,det.ID_TIPO_CONCEPTO,det.OBGT";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nfolioPagoDiverso: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
            } else {
                log.info("No se ejecuta el ws por el tipo de pago ó por que es una partida restringida");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return respuesta;
    }

    private static boolean validaEsPedidoCap2(Connection conn, int folio) throws SQLException {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select * from tPAGODIVERSOEncabezado as enc with(Nolock) " + " inner join tPAGODIVERSODetalle as det with(Nolock) on enc.nFolioPAGODIVERSO=det.nFolioPAGODIVERSO " + " where enc.cDocumentoHaplicado='S' and enc.nFolioPAGODIVERSO=? " + " and enc.cFolioPAGODIVERSO like'PE%' and det.OBGT like'2%'";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nfolioPagoDiverso: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
                log.info("No se ejecuta el ws por que el pago es de un pedido de capitulo 2");
            } else {
                log.info("Se ejecuta el ws por que no es un pago de un pedido de capitulo 2");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return respuesta;
    }

    private static boolean validaClvEP_E04_E09(Connection conn, int folio) throws SQLException {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select * from tPAGODIVERSOEncabezado as enc with(Nolock) " + " inner join tPAGODIVERSODetalle as det with(Nolock) on enc.nFolioPAGODIVERSO=det.nFolioPAGODIVERSO " + " inner join tCatUnidadEjecutora as catUE with(Nolock) on catUE.cUnidadEjecutora=SUBSTRING(det.EP,57,3) " + " where enc.cDocumentoHaplicado='S' and enc.nFolioPAGODIVERSO=? and (catUE.D_DESCRIPCION like'%Cecfor%' or catUE.D_DESCRIPCION like'%Cefofor%')";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nfolioPagoDiverso: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
                log.info("No se ejecuta el ws por que el pago es de un CECFOR O CEFOFOR");
            } else {
                log.info("Se ejecuta el ws por que  el pago no es de un CECFOR O CEFOFOR");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return respuesta;
    }

    private static boolean cancelaRecepcionAnticipo(Connection conn, int folio) throws SQLException {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = " SELECT	* FROM tPAGODIVERSOEncabezado WITH (NOLOCK) " + " WHERE cFolioPAGODIVERSO = (SELECT cFolioPAGODIVERSO FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ? ) " + " AND cIdRecepMat LIKE 'RM%' AND (cDocumentoHaplicado IS NULL OR cDocumentoHaplicado = 'S') ";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nfolioPagoDiverso: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return respuesta;
    }

    private static boolean updateEliminaInfoVuelosRG(Connection conn, int folio) throws SQLException {
        boolean respuesta = true;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement psUpdateLayoutVuelos = null;
        PreparedStatement psDeleteInfoVuelos = null;
        try {
            String query = " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe FROM tInfoBoleto WITH (NOLOCK) WHERE nFolioRelacionGastos = ? ";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String existe = rs.getString("existe");
                if ("1".equalsIgnoreCase(existe)) {
                    String sUpdateLayoutVuelos = " UPDATE	vuelosdet SET vuelosdet.Status = 'A' " + " FROM		tLayoutVuelosDet vuelosdet WITH (NOLOCK) " + " INNER JOIN tInfoBoleto infoboleto WITH(NOLOCK) " + " ON " + " ( " + "	infoboleto.cNumeroBoleto = vuelosdet.cReferencia " + "	AND infoboleto.RFCVuelo = vuelosdet.RFC " + "	AND infoboleto.cNombreRFC = vuelosdet.cNombre " + "	AND infoboleto.mImporteBoleto = vuelosdet.mTotal " + " ) " + " WHERE infoboleto.nFolioRelacionGastos = ? ";
                    String sDeleteInfoVuelos = " DELETE FROM tInfoBoleto WHERE nFolioRelacionGastos = ? ";
                    psUpdateLayoutVuelos = conn.prepareStatement(sUpdateLayoutVuelos);
                    psUpdateLayoutVuelos.setInt(1, folio);
                    psDeleteInfoVuelos = conn.prepareStatement(sDeleteInfoVuelos);
                    psDeleteInfoVuelos.setInt(1, folio);
                    int updateVuelos = psUpdateLayoutVuelos.executeUpdate();
                    log.info("Object: {}", "Se Actualizaron: " + updateVuelos + " Vuelos.");
                    int deleteVuelos = psDeleteInfoVuelos.executeUpdate();
                    log.info("Object: {}", "Se eliminaron: " + deleteVuelos + " Vuelos.");
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(psUpdateLayoutVuelos);
            CloseObject.closeObject(psDeleteInfoVuelos);
        }
        return respuesta;
    }

    private static boolean eliminaVuelosPDIV(Connection conn, int folio) throws SQLException {
        boolean respuesta = true;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement psDeleteInfoVuelos = null;
        try {
            String query = " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe FROM tPagoDiversoBoletajeAvion WITH (NOLOCK) WHERE nFolioPagoDiverso = ? ";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String existe = rs.getString("existe");
                if ("1".equalsIgnoreCase(existe)) {
                    String sDeleteInfoVuelos = " DELETE FROM tPagoDiversoBoletajeAvion WHERE nFolioPagoDiverso = ? ";
                    psDeleteInfoVuelos = conn.prepareStatement(sDeleteInfoVuelos);
                    psDeleteInfoVuelos.setInt(1, folio);
                    int deleteVuelos = psDeleteInfoVuelos.executeUpdate();
                    log.info("Object: {}", "Se eliminaron: " + deleteVuelos + " Vuelos del Pago.");
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(psDeleteInfoVuelos);
        }
        return respuesta;
    }

    /**
     * @return the reportPath
     */
    public String getReportPath() {
        return reportPath;
    }

    /**
     * @param reportPath
     *            the reportPath to set
     */
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    /**
     * @return the u
     */
    public Usuario getUsuario() {
        return u;
    }

    /**
     * @param u
     *            the u to set
     */
    public void setUsuario(Usuario u) {
        this.u = u;
    }

    public void setEmpleadoVoBo(String empleadoVoBo) {
        this.empleadoVoBo = empleadoVoBo;
    }

    public void setEmpleadoCaptura(String empleadoCaptura) {
        this.empleadoCaptura = empleadoCaptura;
    }

    public void setEmpleadoAutoriza(String empleadoAutoriza) {
        this.empleadoAutoriza = empleadoAutoriza;
    }

    /**
     * @return the autorizadoPorFiel
     */
    public boolean isAutorizadoPorFiel() {
        return autorizadoPorFiel;
    }

    /**
     * @param autorizadoPorFiel
     *            the autorizadoPorFiel to set
     */
    public void setAutorizadoPorFiel(boolean autorizadoPorFiel) {
        this.autorizadoPorFiel = autorizadoPorFiel;
    }

    public JSONObject guardarCierreCuentasPorPagar(String tipoDocumento, String caNoContrarrecibo, String idCierreCXP, String u_login) {
        PreparedStatement pstmnES = null, pstmnDS = null, pstmnEI = null, pstmntDI = null;
        ResultSet rs = null, rsDS = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String nomCampoRecibo = "";
        String nFolio = "";
        String nFolioDoc = "";
        String campoEnc = "";
        String campoDet = "";
        String campoInsertEnc = "";
        String campoInsertDeta = "";
        String cTipoDocumento = "";
        String nFolioo = "";
        String cTablaPadre = "";
        String cTablaHija = "";
        String cFolioo = "";
        try {
            conn = getConnection();
            String[] doc = tipoDocumento.split("/");
            String[] caN = caNoContrarrecibo.split("/");
            String[] iCCXP = idCierreCXP.split("/");
            for (int i = 0; i < doc.length; i++) {
                if (doc[i].equalsIgnoreCase("Directo")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "nFolioDetalle";
                    cTipoDocumento = "PAGODIRECTO";
                    cTablaPadre = "tPAGODirectoEncabezado";
                    cTablaHija = "tPAGODirectoDetalle";
                    cFolioo = "nFolioPagoDirecto";
                    campoEnc = " nFolioEnc, fCarga, fAplicacion, cRamo, cUnidadResponsable, aEjercicioFiscal, cIdEntidadContable, cIdDocumento, " + " cIdTipoDocumento, cIdTipoMontoDesembolso, cIdRFC, fRecepcion, fRevision, fProgramadaPago, cConcepto, fVigenciaIVA, " + " nPorcIVA, mImporteBruto, mImporteIVA, mImporteRetencion, mImporteNeto, cIdUnidadAdministrativa, cIdGRegional, " + " cIdGEstatal, cIdDistritoRiego, cIdTipoPagoDirecto, cIdTipoFondo, caNoContrarrecibo, caNoAP, cIdEstadoPagoDirecto, " + " lContrarreciboImpreso, cReferenciaPRODDER, nIdConcepto_PagDirecto, nPorcImpuestoCedular, lAplicaImpuestoCedular, " + " cIdTipoLimiteDlls, nTipoCambio, cOficioDiferenciaCambiaria, cIdUsuarioCaptura, cIdUsuarioImpresion, cIdUsuarioRevision, " + " cIdUsuarioAprobacion, cIdUsuarioRechazo, ID_DESTINO_GASTO, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cDocumentoHaplicado, " + " nFolioPoliza, cTipoPoliza, U_LOGIN, nEnviadoSICOP, cUnidadResponsableContable, isnull(nFolioPolizaCancelacion, 0) AS nFolioPolizaCancelacion, " + " fCancelacion, cDescripcionPoliza, NumPagoAMF ";
                    campoDet = " nFolioDetalle, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, cIdDocumento, EP, cIdCuentaContable, " + " mComprometido, nPoliza, mImporteBruto, mSancion, mDevolucion, mImporteAmortiza, mImporteIva, mRetencion, " + " mPenalizacion, mImporteNeto, m2Millar, m23IVA, mISRHonorarios, mObra5, mImporteFlete4, mISRArrenda, mRetImpuestoCedular, " + " ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, ALM, RFC, mImporte, mImporteMasIva, mImporteIvaArrenda, mImporteIvaHonorarios, " + " mImporteFlete23, mImporteIvaProv, mImporteObra, mCNIC, mIMDT, mTesofe, nCapitulo, altaAlmacen ";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, nFolioPagoDirecto, fCarga, fAplicacion, cRamo, cUnidadResponsable, " + " aEjercicioFiscal, cIdEntidadContable, cIdDocumento, cIdTipoDocumento, cIdTipoMontoDesembolso, cIdRFC, fRecepcion, " + " fRevision, fProgramadaPago, cConcepto, fVigenciaIVA, nPorcIVA, mImporteBruto, mImporteIVA, mImporteRetencion, " + " mImporteNeto, cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal, cIdDistritoRiego, cIdTipoPagoDirecto, cIdTipoFondo, " + " caNoContrarrecibo, caNoAP, cIdEstadoPagoDirecto, lContrarreciboImpreso, cReferenciaPRODDER, nIdConcepto, " + " nPorcImpuestoCedular, lAplicaImpuestoCedular, cIdTipoLimiteDlls, nTipoCambio, cOficioDiferenciaCambiaria, " + " cIdUsuarioCaptura, cIdUsuarioImpresion, cIdUsuarioRevision, cIdUsuarioAprobacion, cIdUsuarioRechazo, " + " ID_DESTINO_GASTO, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cDocumentoHaplicado, nFolioPoliza, cTipoPoliza, " + " U_LOGIN, nEnviadoSICOP, cUnidadResponsableContable, nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, NumPagoAMF ";
                    campoInsertDeta = " nFolioCierreCuentas, nFolioPagoDirecto, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, " + " cIdDocumento, EP, cIdCuentaContable, mComprometido, nPoliza, mImporteBruto, mSancion, mDevolucion, " + " mImporteAmortiza, mImporteIva, mRetencion, mPenalizacion, mImporteNeto, m2Millar, m23IVA, mISRHonorarios, " + " mObra5, mImporteFlete4, mISRArrenda, mRetImpuestoCedular, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, ALM, RFC, " + " mImporte, mImporteMasIva, mImporteIvaArrenda, mImporteIvaHonorarios, mImporteFlete23, mImporteIvaProv, mImporteObra, " + " mCNIC, mIMDT, mTesofe, nCapitulo, altaAlmacen";
                } else if (doc[i].equalsIgnoreCase("Obra")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "nFolioDetalle";
                    cTipoDocumento = "PAGOOBRA";
                    cTablaPadre = "tPAGOOBRAEncabezado";
                    cTablaHija = "tPAGOOBRADetalle";
                    cFolioo = "nFolioPAGOOBRA";
                    campoEnc = " cFolioContratoObra,nFolioEnc,caNoContrarrecibo, cIdRFC, NOMBRE, cNoEstimacion, cIdTipoOperacion, fAplicacion, cNoFactura, " + " mImporteBruto, mImporteSancion, mImporteDevolucion, mAmortizacionAnticipo, mImporteIVA, mImporteRetencion, mImportePenalizacion, " + " mImporteNeto, nPorcAmortizacion, cIdEstadoEstimacion, lContrarreciboImpreso, cConcepto, lAmortizarAnticipoConEscalacion, " + " nIdConcepto_Obra, fProgramadaPago, nTipoCambio, cIdUsuarioCaptura, cIdUsuarioImpresion, cIdUsuarioRevision, " + " cIdUsuarioAprobacion, cIdUsuarioRechazo, ID_DESTINO_GASTO, ID_TIPO_OPERACION, ID_TIPO_MOVIMIENTO, ID_TIPO_FONDO, fperiodode, " + " fperiodohasta, cUnidadResponsable, cRamo, cIdTipoDocumento, mImporteMasIva, mAmortizacion, mSaldoCedula, mAcumuladoxpagar, " + " mSaldoAnticipo, cCentroContable, cMes, aEjercicioFiscal, mAmortizacionAcumulado, mImporteSancionAcumulado, " + " mImporteDevolucionAcumulado, cDocumentoHaplicado, nFolioPoliza, ALM, capitulo, cReferenciaPRODDER, " + " nPorcImpuestoCedular, lAplicaImpuestoCedular, cOficioDiferenciaCambiaria, U_LOGIN, nFolioPolizaCancelacion, fCancelacion, " + " cDescripcionPoliza, cUnidadResponsableContable, nEnviadoSICOP, NumPagoAMF ";
                    campoDet = " nFolioDetalle, nDocRenglon, nMes, cEjercicio, cIdEntidadContable, cIdRelacion, EP, cIdCuentaContable, mComprometido, nPoliza, " + " ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO,  cEvento, aEjercicioFiscal, cCentroContable, cMes, RFC, mImporteNeto, ALM, mImporteBruto, " + " mImporteMasIva, mImporteIva, nCapitulo, cDocumentoHaplicado, nFolioPoliza, cTipoPoliza, mSancion, mDevolucion, mImporteAmortiza, " + " mRetencion, mPenalizacion, m2Millar, m23IVA, mISRHonorarios, mObra5, mImporteFlete4, mISRArrenda, mRetImpuestoCedular, " + " mBruto, mAmortizacionAnticipo, mIVA, mNeto, m5Millar, mFletes, mCedular, mImporte, mImporteIvaArrenda, mImporteIvaHonorarios, " + " mImporteFlete23, mImporteIvaProv, mImporteObra, mCNIC, mIMDT, mTesofe, altaAlmacen ";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, nFolioPAGOOBRA, cFolioContratoObra, caNoContrarrecibo, RFC, NOMBRE, cNoEstimacion, " + " cIdTipoOperacion, fAplicacion, cNoFactura, mImporteBruto, mImporteSancion, mImporteDevolucion, mAmortizacionAnticipo, mImporteIVA, " + " mImporteRetencion, mImportePenalizacion, mImporteNeto, nPorcAmortizacion, cIdEstadoEstimacion, lContrarreciboImpreso, " + " cConcepto, lAmortizarAnticipoConEscalacion, nIdConcepto_Obra, fProgramadaPago, nTipoCambio, cIdUsuarioCaptura, " + " cIdUsuarioImpresion, cIdUsuarioRevision, cIdUsuarioAprobacion, cIdUsuarioRechazo, ID_DESTINO_GASTO, ID_TIPO_OPERACION, " + " ID_TIPO_MOVIMIENTO, ID_TIPO_FONDO, fperiodode, fperiodohasta, cUnidadResponsable, cRamo, cIdTipoDocumento, mImporteMasIva, " + " mAmortizacion, mSaldoCedula, mAcumuladoxpagar, mSaldoAnticipo, cCentroContable, cMes, aEjercicioFiscal, " + " mAmortizacionAcumulado, mImporteSancionAcumulado, mImporteDevolucionAcumulado, cDocumentoHaplicado, nFolioPoliza, ALM, " + " capitulo, cReferenciaPRODDER, nPorcImpuestoCedular, lAplicaImpuestoCedular, cOficioDiferenciaCambiaria, U_LOGIN, " + " nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, cUnidadResponsableContable, nEnviadoSICOP, NumPagoAMF ";
                    campoInsertDeta = " nFolioCierreCuentas, nFolioPAGOOBRA, nDocRenglon, nMes, cEjercicio, cIdEntidadContable, cIdRelacion, EP, " + " cIdCuentaContable, mComprometido, nPoliza, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cEvento, aEjercicioFiscal, cCentroContable, cMes, " + " RFC, mImporteNeto, ALM, mImporteBruto, mImporteMasIva, mImporteIva, nCapitulo, cDocumentoHaplicado, nFolioPoliza, " + " cTipoPoliza, mSancion, mDevolucion, mImporteAmortiza, mRetencion, mPenalizacion, m2Millar, m23IVA, mISRHonorarios, " + " mObra5, mImporteFlete4, mImporteObra, mISRArrenda, mRetImpuestoCedular, mBruto, mAmortizacionAnticipo, mIVA, mNeto, m5Millar, mFletes, " + " mCedular, mImporte, mImporteIvaArrenda, mImporteIvaHonorarios, mImporteFlete23, mImporteIvaProv, mCNIC, mIMDT, mTesofe, altaAlmacen";
                } else if (doc[i].equalsIgnoreCase("Rel_Gastos")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "nFolioDetalle";
                    cTipoDocumento = "RELACIONGASTOS";
                    cTablaPadre = "tRELACIONGASTOSEncabezado";
                    cTablaHija = "tRELACIONGASTOSDetalle";
                    cFolioo = "nFolioRELACIONGASTOS";
                    campoEnc = " nFolioEnc,fAplicacion,cRamo,cUnidadResponsable,cEjercicio,cIdEntidadContable,cIdRelacion,cIdTipoDocumento, " + " cIdTipoRelacion,cIdTipoMontoDesembolso,cIdRFC,fRecepcion,fRevision,fProgramadaPago,cConcepto,mImporteNeto, " + " cIdUnidadAdministrativa,cIdGRegional,cIdGEstatal,cIdDistritoRiego,cIdTipoFondo,caNoContrarrecibo,caNoAP,cIdEstadoRelacion, " + " lContrarreciboImpreso,nIdConcepto_Obra,cIdTipoLimiteDlls,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion,cIdUsuarioRechazo, " + " lSuficienciaAnualValidada,lSuficienciaMensualValidada,ID_DESTINO_GASTO,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cnombre,TIPO_OPERACION,cEvento, " + " aEjercicioFiscal,cCentroContable,cMes,cIdRFC,mImporteBruto,mImporteMasIva,cDocumentoHaplicado,nFolioPoliza, " + " cTipoPoliza,ALM,capitulo,cReferenciaPRODDER,nPorcImpuestoCedular,lAplicaImpuestoCedular,nTipoCambio,cOficioDiferenciaCambiaria, " + " U_LOGIN,nEnviadoSICOP,cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza ";
                    campoDet = " nFolioDetalle,nDocRenglon,nMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable, " + " mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,aEjercicioFiscal,cCentroContable,cMes, " + "  RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,cDocumentoHaplicado, " + "  nFolioPoliza,cTipoPoliza,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar, " + "  m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mBruto,mAmortizacionAnticipo, " + "  mIVA,mNeto,m5Millar,mFletes,mCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23, " + "  mImporteIvaProv,mImporteObra,mCNIC,mIMDT,mTesofe,altaAlmacen ";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, nFolioRELACIONGASTOS, fAplicacion, cRamo, cUnidadResponsable, cEjercicio, cIdEntidadContable, cIdRelacion, cIdTipoDocumento, " + " cIdTipoRelacion, cIdTipoMontoDesembolso, cIdRFC, fRecepcion, fRevision, fProgramadaPago, cConcepto, mImporteNeto, " + " cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal, cIdDistritoRiego, cIdTipoFondo, caNoContrarrecibo, caNoAP, cIdEstadoRelacion, " + " lContrarreciboImpreso,nIdConcepto,cIdTipoLimiteDlls,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion,cIdUsuarioRechazo, " + " lSuficienciaAnualValidada,lSuficienciaMensualValidada,ID_DESTINO_GASTO,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cnombre,TIPO_OPERACION,cEvento, " + " aEjercicioFiscal,cCentroContable,cMes,RFC,mImporteBruto,mImporteMasIva,cDocumentoHaplicado,nFolioPoliza, " + " cTipoPoliza,ALM,capitulo,cReferenciaPRODDER,nPorcImpuestoCedular,lAplicaImpuestoCedular,nTipoCambio,cOficioDiferenciaCambiaria, " + " U_LOGIN,nEnviadoSICOP,cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza";
                    campoInsertDeta = " nFolioCierreCuentas, nFolioRELACIONGASTOS,nDocRenglon,nMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable, " + " mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,aEjercicioFiscal,cCentroContable,cMes, " + " RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,cDocumentoHaplicado, " + " nFolioPoliza,cTipoPoliza,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar, " + " m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mBruto,mAmortizacionAnticipo, " + " mIVA,mNeto,m5Millar,mFletes,mCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23, " + " mImporteIvaProv,mImporteObra,mCNIC,mIMDT,mTesofe,altaAlmacen";
                } else if (doc[i].equalsIgnoreCase("Federalizado")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "nFolioDetalle";
                    cTipoDocumento = "CNTRTFERALIZADO";
                    cTablaPadre = "tPAGOFEDERALIZADOEncabezado";
                    cTablaHija = "tPAGOFEDERALIZADODetalle";
                    cFolioo = "nFolioPAGOFEDERALIZADO";
                    campoEnc = "cFolioContratoObra,nFolioEnc,caNoContrarrecibo,cIdRFC,NOMBRE,cNoEstimacion,cIdTipoOperacion,fAplicacion, " + " cNoFactura,mImporteBruto,mImporteSancion,mImporteDevolucion,mAmortizacionAnticipo,mImporteIVA,mImporteRetencion, " + " mImportePenalizacion,mImporteNeto,nPorcAmortizacion,cIdEstadoEstimacion,lContrarreciboImpreso,cConcepto,lAmortizarAnticipoConEscalacion, " + " nIdConcepto_Obra,fProgramadaPago,nTipoCambio,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion,cIdUsuarioRechazo, " + " ID_DESTINO_GASTO,ID_TIPO_OPERACION,ID_TIPO_MOVIMIENTO,ID_TIPO_FONDO,fperiodode,fperiodohasta,cUnidadResponsable,cRamo,cIdTipoDocumento, " + " mImporteMasIva,mAmortizacion,mSaldoCedula,mAcumuladoxpagar,mSaldoAnticipo,cCentroContable,cMes,aEjercicioFiscal,mAmortizacionAcumulado, " + " mImporteSancionAcumulado,mImporteDevolucionAcumulado,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,ALM,capitulo,cReferenciaPRODDER, " + " nPorcImpuestoCedular,lAplicaImpuestoCedular,cOficioDiferenciaCambiaria,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza, " + " cUnidadResponsableContable,nEnviadoSICOP,NumPagoAMF";
                    campoDet = " nFolioDetalle,nDocRenglon,nMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido, " + " nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,aEjercicioFiscal,cCentroContable,cMes,RFC,mImporteNeto,ALM, " + " mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,mSancion,mDevolucion, " + " mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular, " + " mBruto,mAmortizacionAnticipo,mIVA,mNeto,m5Millar,mFletes,mCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios, " + " mImporteFlete23,mImporteIvaProv,mImporteObra,mCNIC,mIMDT,mTesofe,altaAlmacen";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, cFolioContratoObra,nFolioPAGOFEDERALIZADO,caNoContrarrecibo,RFC,NOMBRE,cNoEstimacion,cIdTipoOperacion,fAplicacion, " + " cNoFactura,mImporteBruto,mImporteSancion,mImporteDevolucion,mAmortizacionAnticipo,mImporteIVA,mImporteRetencion, " + " mImportePenalizacion,mImporteNeto,nPorcAmortizacion,cIdEstadoEstimacion,lContrarreciboImpreso,cConcepto,lAmortizarAnticipoConEscalacion, " + " nIdConcepto_Obra,fProgramadaPago,nTipoCambio,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion,cIdUsuarioRechazo, " + " ID_DESTINO_GASTO,ID_TIPO_OPERACION,ID_TIPO_MOVIMIENTO,ID_TIPO_FONDO,fperiodode,fperiodohasta,cUnidadResponsable,cRamo,cIdTipoDocumento, " + " mImporteMasIva,mAmortizacion,mSaldoCedula,mAcumuladoxpagar,mSaldoAnticipo,cCentroContable,cMes,aEjercicioFiscal,mAmortizacionAcumulado, " + " mImporteSancionAcumulado,mImporteDevolucionAcumulado,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,ALM,capitulo,cReferenciaPRODDER, " + " nPorcImpuestoCedular,lAplicaImpuestoCedular,cOficioDiferenciaCambiaria,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza, " + " cUnidadResponsableContable,nEnviadoSICOP,NumPagoAMF";
                    campoInsertDeta = " nFolioCierreCuentas, nFolioPAGOFEDERALIZADO,nDocRenglon,nMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido, " + " nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,aEjercicioFiscal,cCentroContable,cMes,RFC,mImporteNeto,ALM, " + " mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,mSancion,mDevolucion, " + " mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular, " + " mBruto,mAmortizacionAnticipo,mIVA,mNeto,m5Millar,mFletes,mCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios, " + " mImporteFlete23,mImporteIvaProv,mImporteObra,mCNIC,mIMDT,mTesofe,altaAlmacen";
                } else if (doc[i].equalsIgnoreCase("Diverso")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "nFolioDetalle";
                    cTipoDocumento = "PAGODIVERSO";
                    cTablaPadre = "tPAGODIVERSOEncabezado";
                    cTablaHija = "tPAGODIVERSODetalle";
                    cFolioo = "nFolioPAGODIVERSO";
                    campoEnc = " cFolioContratoObra,nFolioEnc,caNoContrarrecibo,cIdRFC,NOMBRE,cNoEstimacion,cIdTipoOperacion, " + "   fAplicacion,cNoFactura,mImporteBruto,mImporteSancion,mImporteDevolucion,mAmortizacionAnticipo, " + " mImporteIVA,mImporteRetencion,mImportePenalizacion,mImporteNeto,nPorcAmortizacion,cIdEstadoEstimacion, " + " lContrarreciboImpreso,cConcepto,lAmortizarAnticipoConEscalacion,nIdConcepto_Obra,fProgramadaPago, " + " nTipoCambio,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion, " + " cIdUsuarioRechazo,ID_DESTINO_GASTO,ID_TIPO_OPERACION,ID_TIPO_MOVIMIENTO,ID_TIPO_FONDO, " + "      cUnidadResponsable,cRamo,cIdTipoDocumento,mImporteMasIva,mAmortizacion,mSaldoCedula, " + "    mAcumuladoxpagar,mSaldoAnticipo,cCentroContable,cMes,aEjercicioFiscal,mAmortizacionAcumulado, " + " mImporteSancionAcumulado,mImporteDevolucionAcumulado,cDocumentoHaplicado,nFolioPoliza, " + "      cTipoPoliza,ALM,capitulo,cReferenciaPRODDER,nPorcImpuestoCedular,lAplicaImpuestoCedular, " + "      cOficioDiferenciaCambiaria,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza, " + " cUnidadResponsableContable,nEnviadoSICOP,NumPagoAMF";
                    campoDet = " nFolioDetalle,nDocRenglon,nMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido, " + " nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,aEjercicioFiscal,cCentroContable,cMes,RFC,mImporteNeto, " + " ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza, " + " mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios, " + " mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mBruto,mAmortizacionAnticipo,mIVA, " + " mNeto,m5Millar,mFletes,mCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23, " + " mImporteIvaProv,mImporteObra,mCNIC,mIMDT,mTesofe,altaAlmacen,Periodo13,ADEFAS";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, cFolioPAGODIVERSO,nFolioPAGODIVERSO,caNoContrarrecibo,RFC,NOMBRE,cNoEstimacion,cIdTipoOperacion, " + "   fAplicacion,cNoFactura,mImporteBruto,mImporteSancion,mImporteDevolucion,mAmortizacionAnticipo, " + " mImporteIVA,mImporteRetencion,mImportePenalizacion,mImporteNeto,nPorcAmortizacion,cIdEstadoEstimacion, " + " lContrarreciboImpreso,cConcepto,lAmortizarAnticipoConEscalacion,nIdConcepto_Obra,fProgramadaPago, " + " nTipoCambio,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion, " + " cIdUsuarioRechazo,ID_DESTINO_GASTO,ID_TIPO_OPERACION,ID_TIPO_MOVIMIENTO,ID_TIPO_FONDO, " + "      cUnidadResponsable,cRamo,cIdTipoDocumento,mImporteMasIva,mAmortizacion,mSaldoCedula, " + "    mAcumuladoxpagar,mSaldoAnticipo,cCentroContable,cMes,aEjercicioFiscal,mAmortizacionAcumulado, " + " mImporteSancionAcumulado,mImporteDevolucionAcumulado,cDocumentoHaplicado,nFolioPoliza, " + "      cTipoPoliza,ALM,capitulo,cReferenciaPRODDER,nPorcImpuestoCedular,lAplicaImpuestoCedular, " + "      cOficioDiferenciaCambiaria,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza, " + " cUnidadResponsableContable,nEnviadoSICOP,NumPagoAMF";
                    campoInsertDeta = " nFolioCierreCuentas, nFolioPAGODIVERSO,nDocRenglon,nMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido, " + " nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,aEjercicioFiscal,cCentroContable,cMes,RFC,mImporteNeto, " + " ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza, " + " mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios, " + " mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mBruto,mAmortizacionAnticipo,mIVA, " + " mNeto,m5Millar,mFletes,mCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23, " + " mImporteIvaProv,mImporteObra,mCNIC,mIMDT,mTesofe,altaAlmacen,Periodo13,ADEFAS";
                } else if (doc[i].equalsIgnoreCase("Ajenas")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "nFolioDetalle";
                    campoEnc = " nFolioEnc, cNombre, cBeneficiario, fCaptura, fAplicacion, fperiodode, fperiodohasta, cConcepto, caNoContrarrecibo, " + " mImportes, U_LOGIN, cCentroContable, aEjercicioFiscal, cUnidadResponsable, cRamo,cIdUsuarioAprobacion, " + " cIdUsuarioRechazo, cDocumentoHaplicado, nFolioPoliza, nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, " + " cUnidadResponsableContable, nEnviadoSICOP, cTipoPoliza, cIDRFC, cIdTipoDocumento, nFolioSICOP, NumPagoAMF ";
                    campoDet = " nFolioDetalle, nDocRenglon, nFolioDoc, cTRetencion, Ep, fAplicacion, cTipoDoc, cMes, cEvento, " + " aEjercicioFiscal, cCentroContable, mTotal, mEntero, mSobrante, mImporteFlete23, mISRHonorarios,mISRArrenda, " + " mImporteFlete4, mCNIC, mIMDT, mObra5, mTesofe,mRetImpuestoCedular,caNoContrarrecibo, RFC, Periodo13, ADEFAS ";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, nFolioOperAjenas, cNombre, cBeneficiario, fCaptura, fAplicacion, fDesde, FHasta, cConcepto, caNoContrarrecibo, " + " mImportes, U_LOGIN, cCentroContable, aEjercicioFiscal, cUnidadResponsable, cRamo,cIdUsuarioAprobacion, " + " cIdUsuarioRechazo, cDocumentoHaplicado, nFolioPoliza, nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, " + " cUnidadResponsableContable, nEnviadoSICOP, cTipoPoliza, cIDRFC, cIdTipoDocumento, nFolioSICOP, NumPagoAMF ";
                    campoInsertDeta = " nFolioCierreCuentas, nFolioOperAjenas, nDocRenglon, nFolioDoc, cTRetencion, Ep, fAplicacion, cTipoDoc, cMes, cEvento, " + " aEjercicioFiscal, cCentroContable, mTotal, mEntero, mSobrante, mImporteFlete23, mISRHonorarios,mISRArrenda, " + " mImporteFlete4, mCNIC, mIMDT, mObra5, mTesofe,mRetImpuestoCedular,caNoContrarrecibo, RFC, Periodo13, ADEFAS ";
                } else if (doc[i].equalsIgnoreCase("Registro Pasivo")) {
                    nomCampoRecibo = "caNoContrarrecibo";
                    nFolio = "caNoContrarrecibo";
                    cTipoDocumento = "PAGODIVERSO";
                    cTablaPadre = "tPAGODIVERSOEncabezado";
                    cTablaHija = "tPAGODIVERSODetalle";
                    cFolioo = "nFolioPAGODIVERSO";
                    campoEnc = " caNoContrarrecibo,referenciaAMF,cIdRFC,cuentaBancaria,aEjercicioFiscal,cUnidadResponsable,fCaptura,fechaPago,mImportes, " + " cCentroContable,U_LOGIN,estatus";
                    campoDet = " nFolioDetalle,EP,mImporte ";
                    campoInsertEnc = " nFolioCierreCuentas, tipoDocumento, clavePasivo,referenciaAMF,rfcAMF,cuentaBancaria,aEjercicioFiscal,cUnidadResponsable,fechaCaptura,fechaPago,importePago, " + " cCentroContable,U_LOGIN,estatus";
                    campoInsertDeta = " nFolioCierreCuentas, clavePasivo, EP, mImporte ";
                }
                // Buscar Encabezado
                pstmnES = conn.prepareStatement("SELECT " + campoEnc + " FROM vCierreCuentasPorPagarEncabezado WITH(NOLOCK) WHERE " + nomCampoRecibo + " = ? AND docto = ?");
                pstmnES.setString(1, caN[i]);
                pstmnES.setString(2, doc[i]);
                rs = pstmnES.executeQuery();
                if (rs.next()) {
                    nFolioDoc = (doc[i].equalsIgnoreCase("Registro Pasivo")) ? rs.getString("caNoContrarrecibo") : rs.getString("nFolioEnc");
                    if (doc[i].equalsIgnoreCase("Directo")) {
                        String fAplicacion = (rs.getString("fAplicacion") == null) ? null : "'" + rs.getString("fAplicacion") + "'";
                        String fProgramadaPago = rs.getString("fProgramadaPago").replace("-", "");
                        String fRecepcion = rs.getString("fRecepcion").replace("-", "");
                        String fCancelacion = (rs.getString("fCancelacion") == null) ? null : "'" + rs.getString("fCancelacion") + "'";
                        String nFolioPolizaCancelacion = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String cIdEntidadContable = (rs.getString("cIdEntidadContable") == null) ? null : "'" + rs.getString("cIdEntidadContable") + "'";
                        String cIdTipoDocumento = (rs.getString("cIdTipoDocumento") == null) ? null : "'" + rs.getString("cIdTipoDocumento") + "'";
                        String cIdTipoMontoDesembolso = (rs.getString("cIdTipoMontoDesembolso") == null) ? null : "'" + rs.getString("cIdTipoMontoDesembolso") + "'";
                        String fRevision = (rs.getString("fRevision") == null) ? null : "'" + rs.getString("fRevision") + "'";
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        // Insertar Encabezado
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Directo','" + rs.getString("nFolioEnc") + "','" + rs.getString("fCarga") + "', " + " " + fAplicacion + ",'" + rs.getString("cRamo") + "','" + rs.getString("cUnidadResponsable") + "', " + " '" + rs.getString("aEjercicioFiscal") + "'," + cIdEntidadContable + "," + " '" + rs.getString("cIdDocumento") + "'," + cIdTipoDocumento + ", " + " " + cIdTipoMontoDesembolso + ",'" + rs.getString("cIdRFC") + "','" + fRecepcion + "', " + " " + fRevision + ",'" + fProgramadaPago + "','" + rs.getString("cConcepto") + "', " + " '" + rs.getString("fVigenciaIVA") + "','" + rs.getString("nPorcIVA") + "','" + rs.getString("mImporteBruto") + "', " + " '" + rs.getString("mImporteIVA") + "','" + rs.getString("mImporteRetencion") + "','" + rs.getString("mImporteNeto") + "', " + " '" + rs.getString("cIdUnidadAdministrativa") + "','" + rs.getString("cIdGRegional") + "','" + rs.getString("cIdGEstatal") + "', " + " '" + rs.getString("cIdDistritoRiego") + "','" + rs.getString("cIdTipoPagoDirecto") + "','" + rs.getString("cIdTipoFondo") + "', " + " '" + rs.getString("caNoContrarrecibo") + "','" + rs.getString("caNoAP") + "','" + rs.getString("cIdEstadoPagoDirecto") + "', " + " '" + rs.getString("lContrarreciboImpreso") + "','" + rs.getString("cReferenciaPRODDER") + "', " + " '" + rs.getString("nIdConcepto_PagDirecto") + "','" + rs.getString("nPorcImpuestoCedular") + "', " + " '" + rs.getString("lAplicaImpuestoCedular") + "','" + rs.getString("cIdTipoLimiteDlls") + "', " + " '" + rs.getString("nTipoCambio") + "','" + rs.getString("cOficioDiferenciaCambiaria") + "','" + rs.getString("cIdUsuarioCaptura") + "', " + " '" + rs.getString("cIdUsuarioImpresion") + "','" + rs.getString("cIdUsuarioRevision") + "','" + rs.getString("cIdUsuarioAprobacion") + "', " + " '" + rs.getString("cIdUsuarioRechazo") + "','" + rs.getString("ID_DESTINO_GASTO") + "','" + rs.getString("ID_TIPO_MOVIMIENTO") + "', " + " '" + rs.getString("ID_TIPO_CONCEPTO") + "','" + rs.getString("cDocumentoHaplicado") + "','" + rs.getString("nFolioPoliza") + "', " + " '" + rs.getString("cTipoPoliza") + "','" + rs.getString("U_LOGIN") + "','" + rs.getString("nEnviadoSICOP") + "', " + " '" + rs.getString("cUnidadResponsableContable") + "'," + nFolioPolizaCancelacion + "," + fCancelacion + ", " + " '" + rs.getString("cDescripcionPoliza") + "','" + rs.getString("NumPagoAMF") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("nDocRenglon") + "', " + " '" + rsDS.getString("cMes") + "','" + rsDS.getString("cEvento") + "','" + rsDS.getString("cEjercicio") + "', " + " '" + rsDS.getString("cCentroContable") + "','" + rsDS.getString("cIdDocumento") + "', " + " '" + rsDS.getString("EP") + "','" + rsDS.getString("cIdCuentaContable") + "','" + rsDS.getString("mComprometido") + "', " + " '" + rsDS.getString("nPoliza") + "','" + rsDS.getString("mImporteBruto") + "','" + rsDS.getString("mSancion") + "', " + " '" + rsDS.getString("mDevolucion") + "','" + rsDS.getString("mImporteAmortiza") + "', " + " '" + rsDS.getString("mImporteIva") + "','" + rsDS.getString("mRetencion") + "','" + rsDS.getString("mPenalizacion") + "', " + " '" + rsDS.getString("mImporteNeto") + "','" + rsDS.getString("m2Millar") + "','" + rsDS.getString("m23IVA") + "', " + " '" + rsDS.getString("mISRHonorarios") + "','" + rsDS.getString("mObra5") + "','" + rsDS.getString("mImporteFlete4") + "', " + " '" + rsDS.getString("mISRArrenda") + "','" + rsDS.getString("mRetImpuestoCedular") + "', " + " '" + rsDS.getString("ID_TIPO_MOVIMIENTO") + "','" + rsDS.getString("ID_TIPO_CONCEPTO") + "', " + " '" + rsDS.getString("ALM") + "','" + rsDS.getString("RFC") + "','" + rsDS.getString("mImporte") + "', " + " '" + rsDS.getString("mImporteMasIva") + "','" + rsDS.getString("mImporteIvaArrenda") + "', " + " '" + rsDS.getString("mImporteIvaHonorarios") + "','" + rsDS.getString("mImporteFlete23") + "', " + " '" + rsDS.getString("mImporteIvaProv") + "','" + rsDS.getString("mImporteObra") + "','" + rsDS.getString("mCNIC") + "', " + " '" + rsDS.getString("mIMDT") + "','" + rsDS.getString("mTesofe") + "','" + rsDS.getString("nCapitulo") + "', " + " '" + rsDS.getString("altaAlmacen") + "')");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (doc[i].equalsIgnoreCase("Obra")) {
                        String fProgramadaPago = rs.getString("fProgramadaPago").replace("-", "");
                        String fCancelacion = (rs.getString("fCancelacion") == null) ? null : "'" + rs.getString("fCancelacion") + "'";
                        String nFolioPolizaCancelacion = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String cIdTipoOperacion = (rs.getString("cIdTipoOperacion") == null) ? null : "'" + rs.getString("cIdTipoOperacion") + "'";
                        String nPorcImpuestoCedular = (rs.getString("nPorcImpuestoCedular") == null) ? null : rs.getString("nPorcImpuestoCedular");
                        // String lAplicaImpuestoCedular = ( rs.getString(
                        // "lAplicaImpuestoCedular" ) == null ) ? null :
                        // rs.getString( "lAplicaImpuestoCedular" );
                        String fperiodode = (rs.getString("fperiodode") == null) ? null : "'" + rs.getString("fperiodode").replace("-", "") + "'";
                        String fperiodohasta = (rs.getString("fperiodohasta") == null) ? null : "'" + rs.getString("fperiodohasta").replace("-", "") + "'";
                        String ALM = (rs.getString("ALM") == null) ? null : "'" + rs.getString("ALM") + "'";
                        String cConcepto = (rs.getString("cConcepto") == null || rs.getString("cConcepto") != "") ? null : "'" + rs.getString("cConcepto").trim() + "'";
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Obra','" + rs.getString("nFolioEnc") + "','" + rs.getString("cFolioContratoObra").trim() + "', " + " '" + rs.getString("caNoContrarrecibo").trim() + "','" + rs.getString("cIdRFC").trim() + "','" + rs.getString("NOMBRE").trim() + "', " + " '" + rs.getString("cNoEstimacion").trim() + "'," + cIdTipoOperacion + ",'" + rs.getString("fAplicacion") + "', " + " '" + rs.getString("cNoFactura").trim() + "','" + rs.getString("mImporteBruto") + "','" + rs.getString("mImporteSancion") + "', " + " '" + rs.getString("mImporteDevolucion") + "','" + rs.getString("mAmortizacionAnticipo") + "','" + rs.getString("mImporteIVA") + "', " + " '" + rs.getString("mImporteRetencion") + "','" + rs.getString("mImportePenalizacion") + "','" + rs.getString("mImporteNeto") + "', " + " '" + rs.getString("nPorcAmortizacion") + "','" + rs.getString("cIdEstadoEstimacion") + "','" + rs.getString("lContrarreciboImpreso") + "', " + " " + cConcepto + ",'" + rs.getString("lAmortizarAnticipoConEscalacion") + "','" + rs.getString("nIdConcepto_Obra") + "', " + " '" + fProgramadaPago + "','" + rs.getString("nTipoCambio") + "','" + rs.getString("cIdUsuarioCaptura") + "', " + " '" + rs.getString("cIdUsuarioImpresion") + "','" + rs.getString("cIdUsuarioRevision") + "','" + rs.getString("cIdUsuarioAprobacion") + "', " + " '" + rs.getString("cIdUsuarioRechazo") + "','" + rs.getString("ID_DESTINO_GASTO") + "','" + rs.getString("ID_TIPO_OPERACION") + "', " + " '" + rs.getString("ID_TIPO_MOVIMIENTO") + "','" + rs.getString("ID_TIPO_FONDO") + "'," + fperiodode + "," + fperiodohasta + ", " + " '" + rs.getString("cUnidadResponsable") + "','" + rs.getString("cRamo") + "','" + rs.getString("cIdTipoDocumento") + "', " + " '" + rs.getString("mImporteMasIva") + "','" + rs.getString("mAmortizacion") + "','" + rs.getString("mSaldoCedula") + "', " + " '" + rs.getString("mAcumuladoxpagar") + "','" + rs.getString("mSaldoAnticipo") + "','" + rs.getString("cCentroContable") + "', " + " '" + rs.getString("cMes") + "','" + rs.getString("aEjercicioFiscal") + "','" + rs.getString("mAmortizacionAcumulado") + "', " + " '" + rs.getString("mImporteSancionAcumulado") + "','" + rs.getString("mImporteDevolucionAcumulado") + "','" + rs.getString("cDocumentoHaplicado") + "', " + " '" + rs.getString("nFolioPoliza") + "'," + ALM + ",'" + rs.getString("capitulo") + "','" + rs.getString("cReferenciaPRODDER") + "', " + " " + nPorcImpuestoCedular + "," + rs.getString("lAplicaImpuestoCedular") + ",'" + rs.getString("cOficioDiferenciaCambiaria") + "', " + " '" + rs.getString("U_LOGIN") + "'," + nFolioPolizaCancelacion + "," + fCancelacion + ",'" + rs.getString("cDescripcionPoliza") + "', " + " '" + rs.getString("cUnidadResponsableContable") + "','" + rs.getString("nEnviadoSICOP") + "','" + rs.getString("NumPagoAMF") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            String cDocumentoHaplicado = (rsDS.getString("cDocumentoHaplicado") == null) ? null : "'" + rs.getString("cDocumentoHaplicado") + "'";
                            String nFolioPoliza = (rsDS.getString("nFolioPoliza") == null) ? null : rs.getString("nFolioPoliza");
                            String cTipoPoliza = (rsDS.getString("cTipoPoliza") == null) ? null : "'" + rs.getString("cTipoPoliza") + "'";
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("nDocRenglon") + "', " + "'" + rsDS.getString("nMes") + "','" + rsDS.getString("cEjercicio") + "','" + rsDS.getString("cIdEntidadContable") + "', " + "'" + rsDS.getString("cIdRelacion") + "','" + rsDS.getString("EP") + "','" + rsDS.getString("cIdCuentaContable") + "', " + "'" + rsDS.getString("mComprometido") + "','" + rsDS.getString("nPoliza") + "','" + rsDS.getString("ID_TIPO_MOVIMIENTO") + "', " + "'" + rsDS.getString("ID_TIPO_CONCEPTO") + "','" + rsDS.getString("cEvento") + "','" + rsDS.getString("aEjercicioFiscal") + "', " + "'" + rsDS.getString("cCentroContable") + "','" + rsDS.getString("cMes") + "','" + rsDS.getString("RFC") + "', " + "'" + rsDS.getString("mImporteNeto") + "','" + rsDS.getString("ALM") + "','" + rsDS.getString("mImporteBruto") + "', " + "'" + rsDS.getString("mImporteMasIva") + "','" + rsDS.getString("mImporteIva") + "','" + rsDS.getString("nCapitulo") + "', " + " " + cDocumentoHaplicado + "," + nFolioPoliza + "," + cTipoPoliza + ", " + "'" + rsDS.getString("mSancion") + "','" + rsDS.getString("mDevolucion") + "','" + rsDS.getString("mImporteAmortiza") + "', " + "'" + rsDS.getString("mRetencion") + "','" + rsDS.getString("mPenalizacion") + "','" + rsDS.getString("m2Millar") + "',  " + "'" + rsDS.getString("m23IVA") + "','" + rsDS.getString("mISRHonorarios") + "','" + rsDS.getString("mObra5") + "', " + "'" + rsDS.getString("mImporteFlete4") + "','" + rsDS.getString("mImporteObra") + "','" + rsDS.getString("mISRArrenda") + "','" + rsDS.getString("mRetImpuestoCedular") + "', " + "'" + rsDS.getString("mBruto") + "','" + rsDS.getString("mAmortizacionAnticipo") + "','" + rsDS.getString("mIVA") + "', " + "'" + rsDS.getString("mNeto") + "','" + rsDS.getString("m5Millar") + "','" + rsDS.getString("mFletes") + "','" + rsDS.getString("mCedular") + "', " + "'" + rsDS.getString("mImporte") + "','" + rsDS.getString("mImporteIvaArrenda") + "','" + rsDS.getString("mImporteIvaHonorarios") + "', " + "'" + rsDS.getString("mImporteFlete23") + "','" + rsDS.getString("mImporteIvaProv") + "', " + "'" + rsDS.getString("mCNIC") + "','" + rsDS.getString("mIMDT") + "','" + rsDS.getString("mTesofe") + "','" + rsDS.getString("altaAlmacen") + "')");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (doc[i].equalsIgnoreCase("Rel_Gastos")) {
                        String fProgramadaPago = rs.getString("fProgramadaPago").replace("-", "");
                        String fCancelacion = (rs.getString("fCancelacion") == null) ? null : "'" + rs.getString("fCancelacion") + "'";
                        String nFolioPolizaCancelacion = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String fRecepcion = rs.getString("fRecepcion").replace("-", "");
                        String nPorcImpuestoCedular = (rs.getString("nPorcImpuestoCedular") == null) ? null : rs.getString("nPorcImpuestoCedular");
                        String lAplicaImpuestoCedular = (rs.getString("lAplicaImpuestoCedular") == null) ? null : rs.getString("lAplicaImpuestoCedular");
                        String ALM = (rs.getString("ALM") == null) ? null : "'" + rs.getString("ALM") + "'";
                        String nIdConcepto_Obra = (rs.getString("nIdConcepto_Obra") == null) ? null : rs.getString("nIdConcepto_Obra");
                        String lContrarreciboImpreso = (rs.getString("lContrarreciboImpreso") == null) ? null : rs.getString("lContrarreciboImpreso");
                        String nTipoCambio = (rs.getString("nTipoCambio") == null) ? null : rs.getString("nTipoCambio");
                        String cOficioDiferenciaCambiaria = (rs.getString("cOficioDiferenciaCambiaria") == null) ? null : "'" + rs.getString("cOficioDiferenciaCambiaria") + "'";
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        // Insertar Encabezado
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Rel_Gastos','" + rs.getString("nFolioEnc") + "','" + rs.getString("fAplicacion") + "', " + " '" + rs.getString("cRamo") + "','" + rs.getString("cUnidadResponsable") + "','" + rs.getString("cEjercicio") + "', " + " '" + rs.getString("cIdEntidadContable") + "','" + rs.getString("cIdRelacion") + "','" + rs.getString("cIdTipoDocumento") + "', " + " '" + rs.getString("cIdTipoRelacion") + "','" + rs.getString("cIdTipoMontoDesembolso") + "','" + rs.getString("cIdRFC") + "','" + fRecepcion + "', " + " '" + rs.getString("fRevision") + "','" + fProgramadaPago + "','" + rs.getString("cConcepto") + "', " + " '" + rs.getString("mImporteNeto") + "','" + rs.getString("cIdUnidadAdministrativa") + "','" + rs.getString("cIdGRegional") + "', " + " '" + rs.getString("cIdGEstatal") + "','" + rs.getString("cIdDistritoRiego") + "','" + rs.getString("cIdTipoFondo") + "', " + " '" + rs.getString("caNoContrarrecibo") + "','" + rs.getString("caNoAP") + "', " + " '" + rs.getString("cIdEstadoRelacion") + "'," + lContrarreciboImpreso + "," + nIdConcepto_Obra + ",'" + rs.getString("cIdTipoLimiteDlls") + "', " + " '" + rs.getString("cIdUsuarioCaptura") + "','" + rs.getString("cIdUsuarioImpresion") + "','" + rs.getString("cIdUsuarioRevision") + "', " + " '" + rs.getString("cIdUsuarioAprobacion") + "','" + rs.getString("cIdUsuarioRechazo") + "', " + " '" + rs.getString("lSuficienciaAnualValidada") + "','" + rs.getString("lSuficienciaMensualValidada") + "', " + " '" + rs.getString("ID_DESTINO_GASTO") + "','" + rs.getString("ID_TIPO_MOVIMIENTO") + "', " + " '" + rs.getString("ID_TIPO_CONCEPTO") + "','" + rs.getString("cnombre") + "','" + rs.getString("TIPO_OPERACION") + "', " + " '" + rs.getString("cEvento") + "','" + rs.getString("aEjercicioFiscal") + "','" + rs.getString("cCentroContable") + "', " + " '" + rs.getString("cMes") + "','" + rs.getString("cIdRFC") + "','" + rs.getString("mImporteBruto") + "', " + " '" + rs.getString("mImporteMasIva") + "','" + rs.getString("cDocumentoHaplicado") + "','" + rs.getString("nFolioPoliza") + "', " + " '" + rs.getString("cTipoPoliza") + "'," + ALM + ",'" + rs.getString("capitulo") + "', " + " '" + rs.getString("cReferenciaPRODDER") + "'," + nPorcImpuestoCedular + "," + lAplicaImpuestoCedular + "," + nTipoCambio + "," + cOficioDiferenciaCambiaria + ", " + " '" + rs.getString("U_LOGIN") + "','" + rs.getString("nEnviadoSICOP") + "', " + " '" + rs.getString("cUnidadResponsableContable") + "'," + nFolioPolizaCancelacion + "," + fCancelacion + ",'" + rs.getString("cDescripcionPoliza") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            String cDocumentoHaplicado = (rs.getString("cDocumentoHaplicado") == null) ? null : "'" + rs.getString("cDocumentoHaplicado") + "'";
                            String nFolioPoliza = (rs.getString("nFolioPoliza") == null) ? null : rs.getString("nFolioPoliza");
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("nDocRenglon") + "', " + "'" + rsDS.getString("nMes") + "','" + rsDS.getString("cEjercicio") + "','" + rsDS.getString("cIdEntidadContable") + "', " + "'" + rsDS.getString("cIdRelacion") + "','" + rsDS.getString("EP") + "','" + rsDS.getString("cIdCuentaContable") + "', " + "'" + rsDS.getString("mComprometido") + "','" + rsDS.getString("nPoliza") + "','" + rsDS.getString("ID_TIPO_MOVIMIENTO") + "', " + "'" + rsDS.getString("ID_TIPO_CONCEPTO") + "','" + rsDS.getString("cEvento") + "','" + rsDS.getString("aEjercicioFiscal") + "', " + "'" + rsDS.getString("cCentroContable") + "','" + rsDS.getString("cMes") + "','" + rsDS.getString("RFC") + "', " + "'" + rsDS.getString("mImporteNeto") + "','" + rsDS.getString("ALM") + "','" + rsDS.getString("mImporteBruto") + "', " + "'" + rsDS.getString("mImporteMasIva") + "','" + rsDS.getString("mImporteIva") + "','" + rsDS.getString("nCapitulo") + "'," + cDocumentoHaplicado + "," + nFolioPoliza + ", " + "'" + rsDS.getString("cTipoPoliza") + "','" + rsDS.getString("mSancion") + "','" + rsDS.getString("mDevolucion") + "', " + "'" + rsDS.getString("mImporteAmortiza") + "','" + rsDS.getString("mRetencion") + "', " + "'" + rsDS.getString("mPenalizacion") + "','" + rsDS.getString("m2Millar") + "','" + rsDS.getString("m23IVA") + "', " + "'" + rsDS.getString("mISRHonorarios") + "','" + rsDS.getString("mObra5") + "','" + rsDS.getString("mImporteFlete4") + "', " + "'" + rsDS.getString("mISRArrenda") + "','" + rsDS.getString("mRetImpuestoCedular") + "','" + rsDS.getString("mBruto") + "', " + "'" + rsDS.getString("mAmortizacionAnticipo") + "','" + rsDS.getString("mIVA") + "','" + rsDS.getString("mNeto") + "', " + "'" + rsDS.getString("m5Millar") + "','" + rsDS.getString("mFletes") + "','" + rsDS.getString("mCedular") + "', " + "'" + rsDS.getString("mImporte") + "','" + rsDS.getString("mImporteIvaArrenda") + "','" + rsDS.getString("mImporteIvaHonorarios") + "', " + "'" + rsDS.getString("mImporteFlete23") + "','" + rsDS.getString("mImporteIvaProv") + "','" + rsDS.getString("mImporteObra") + "', " + "'" + rsDS.getString("mCNIC") + "','" + rsDS.getString("mIMDT") + "','" + rsDS.getString("mTesofe") + "','" + rsDS.getString("altaAlmacen") + "') ");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (doc[i].equalsIgnoreCase("Diverso")) {
                        String fProgramadaPago = rs.getString("fProgramadaPago").replace("-", "");
                        String fCancelacion = (rs.getString("fCancelacion") == null) ? null : "'" + rs.getString("fCancelacion") + "'";
                        String nFolioPolizaCancelacion = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String cFolioContratoObra = (rs.getString("cFolioContratoObra") == null) ? null : "'" + rs.getString("cFolioContratoObra").trim() + "'";
                        String nFolioEnc = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String cIdTipoOperacion = (rs.getString("cIdTipoOperacion") == null) ? null : "'" + rs.getString("cIdTipoOperacion").trim() + "'";
                        String mImporteDevolucion = (rs.getString("mImporteDevolucion") == null) ? null : rs.getString("mImporteDevolucion");
                        String ALM = (rs.getString("ALM") == null) ? null : "'" + rs.getString("ALM") + "'";
                        String capitulo = (rs.getString("capitulo") == null) ? null : "'" + rs.getString("capitulo").trim() + "'";
                        String lAplicaImpuestoCedular = (rs.getString("lAplicaImpuestoCedular") == null) ? null : "'" + rs.getString("lAplicaImpuestoCedular").trim() + "'";
                        String nPorcImpuestoCedular = (rs.getString("nPorcImpuestoCedular") == null) ? null : "'" + rs.getString("nPorcImpuestoCedular").trim() + "'";
                        String cReferenciaPRODDER = (rs.getString("cReferenciaPRODDER") == null) ? null : "'" + rs.getString("cReferenciaPRODDER").trim() + "'";
                        String nEnviadoSICOP = (rs.getString("nEnviadoSICOP") == null) ? null : rs.getString("nEnviadoSICOP");
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        // Insertar Encabezado
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Diverso'," + cFolioContratoObra + "," + nFolioEnc + ", " + " '" + rs.getString("caNoContrarrecibo") + "','" + rs.getString("cIdRFC") + "','" + rs.getString("NOMBRE") + "', " + " '" + rs.getString("cNoEstimacion") + "'," + cIdTipoOperacion + ",'" + rs.getString("fAplicacion") + "', " + " '" + rs.getString("cNoFactura") + "','" + rs.getString("mImporteBruto") + "','" + rs.getString("mImporteSancion") + "', " + "    " + mImporteDevolucion + ",'" + rs.getString("mAmortizacionAnticipo") + "','" + rs.getString("mImporteIVA") + "', " + " '" + rs.getString("mImporteRetencion") + "','" + rs.getString("mImportePenalizacion") + "','" + rs.getString("mImporteNeto") + "', " + " '" + rs.getString("nPorcAmortizacion") + "','" + rs.getString("cIdEstadoEstimacion") + "', " + " '" + rs.getString("lContrarreciboImpreso") + "','" + rs.getString("cConcepto") + "', " + " '" + rs.getString("lAmortizarAnticipoConEscalacion") + "','" + rs.getString("nIdConcepto_Obra") + "', " + "    '" + fProgramadaPago + "','" + rs.getString("nTipoCambio") + "','" + rs.getString("cIdUsuarioCaptura") + "', " + " '" + rs.getString("cIdUsuarioImpresion") + "','" + rs.getString("cIdUsuarioRevision") + "', " + " '" + rs.getString("cIdUsuarioAprobacion") + "','" + rs.getString("cIdUsuarioRechazo") + "'," + " '" + rs.getString("ID_DESTINO_GASTO") + "','" + rs.getString("ID_TIPO_OPERACION") + "','" + rs.getString("ID_TIPO_MOVIMIENTO") + "', " + " '" + rs.getString("ID_TIPO_FONDO") + "','" + rs.getString("cUnidadResponsable") + "','" + rs.getString("cRamo") + "', " + " '" + rs.getString("cIdTipoDocumento") + "','" + rs.getString("mImporteMasIva") + "','" + rs.getString("mAmortizacion") + "', " + " '" + rs.getString("mSaldoCedula") + "','" + rs.getString("mAcumuladoxpagar") + "','" + rs.getString("mSaldoAnticipo") + "', " + " '" + rs.getString("cCentroContable") + "','" + rs.getString("cMes") + "','" + rs.getString("aEjercicioFiscal") + "', " + " '" + rs.getString("mAmortizacionAcumulado") + "','" + rs.getString("mImporteSancionAcumulado") + "'," + " '" + rs.getString("mImporteDevolucionAcumulado") + "','" + rs.getString("cDocumentoHaplicado") + "', " + " '" + rs.getString("nFolioPoliza") + "','" + rs.getString("cTipoPoliza") + "'," + ALM + "," + capitulo + ", " + " " + cReferenciaPRODDER + "," + nPorcImpuestoCedular + "," + lAplicaImpuestoCedular + ", " + " '" + rs.getString("cOficioDiferenciaCambiaria") + "','" + rs.getString("U_LOGIN") + "'," + nFolioPolizaCancelacion + ", " + " " + fCancelacion + ",'" + rs.getString("cDescripcionPoliza") + "','" + rs.getString("cUnidadResponsableContable") + "', " + " " + nEnviadoSICOP + ",'" + rs.getString("NumPagoAMF") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            String cDocumentoHaplicado = (rs.getString("cDocumentoHaplicado") == null) ? null : "'" + rs.getString("cDocumentoHaplicado") + "'";
                            String nFolioPoliza = (rs.getString("nFolioPoliza") == null) ? null : rs.getString("nFolioPoliza");
                            String cTipoPoliza = (rs.getString("cTipoPoliza") == null) ? null : "'" + rs.getString("cTipoPoliza") + "'";
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("nDocRenglon") + "', " + " '" + rsDS.getString("nMes") + "','" + rsDS.getString("cEjercicio") + "','" + rsDS.getString("cIdEntidadContable") + "'," + " '" + rsDS.getString("cIdRelacion") + "','" + rsDS.getString("EP") + "', " + " '" + rsDS.getString("cIdCuentaContable") + "','" + rsDS.getString("mComprometido") + "', " + " '" + rsDS.getString("nPoliza") + "','" + rsDS.getString("ID_TIPO_MOVIMIENTO") + "', " + " '" + rsDS.getString("ID_TIPO_CONCEPTO") + "','" + rsDS.getString("cEvento") + "', " + " '" + rsDS.getString("aEjercicioFiscal") + "','" + rsDS.getString("cCentroContable") + "', " + " '" + rsDS.getString("cMes") + "','" + rsDS.getString("RFC") + "','" + rsDS.getString("mImporteNeto") + "','" + rsDS.getString("ALM") + "','" + rsDS.getString("mImporteBruto") + "', " + " '" + rsDS.getString("mImporteMasIva") + "','" + rsDS.getString("mImporteIva") + "','" + rsDS.getString("nCapitulo") + "'," + cDocumentoHaplicado + ", " + " " + nFolioPoliza + "," + cTipoPoliza + ",'" + rsDS.getString("mSancion") + "','" + rsDS.getString("mDevolucion") + "', " + " '" + rsDS.getString("mImporteAmortiza") + "','" + rsDS.getString("mRetencion") + "','" + rsDS.getString("mPenalizacion") + "','" + rsDS.getString("m2Millar") + "', " + " '" + rsDS.getString("m23IVA") + "','" + rsDS.getString("mISRHonorarios") + "','" + rsDS.getString("mObra5") + "','" + rsDS.getString("mImporteFlete4") + "', " + " '" + rsDS.getString("mISRArrenda") + "','" + rsDS.getString("mRetImpuestoCedular") + "','" + rsDS.getString("mBruto") + "','" + rsDS.getString("mAmortizacionAnticipo") + "', " + " '" + rsDS.getString("mIVA") + "','" + rsDS.getString("mNeto") + "','" + rsDS.getString("m5Millar") + "','" + rsDS.getString("mFletes") + "','" + rsDS.getString("mCedular") + "', " + " '" + rsDS.getString("mImporte") + "','" + rsDS.getString("mImporteIvaArrenda") + "','" + rsDS.getString("mImporteIvaHonorarios") + "','" + rsDS.getString("mImporteFlete23") + "', " + " '" + rsDS.getString("mImporteIvaProv") + "','" + rsDS.getString("mImporteObra") + "','" + rsDS.getString("mCNIC") + "','" + rsDS.getString("mIMDT") + "','" + rsDS.getString("mTesofe") + "', " + " '" + rsDS.getString("altaAlmacen") + "','" + rsDS.getString("Periodo13") + "','" + rsDS.getString("ADEFAS") + "')");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (doc[i].equalsIgnoreCase("Federalizado")) {
                        String fProgramadaPago = (rs.getString("fProgramadaPago") == null) ? null : "'" + rs.getString("fProgramadaPago").replace("-", "") + "'";
                        String fCancelacion = (rs.getString("fCancelacion") == null) ? null : "'" + rs.getString("fCancelacion") + "'";
                        String nFolioPolizaCancelacion = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String nPorcImpuestoCedular = (rs.getString("nPorcImpuestoCedular") == null) ? null : rs.getString("nPorcImpuestoCedular");
                        String lAplicaImpuestoCedular = (rs.getString("lAplicaImpuestoCedular") == null) ? null : rs.getString("lAplicaImpuestoCedular");
                        String ALM = (rs.getString("ALM") == null) ? null : "'" + rs.getString("ALM") + "'";
                        String nIdConcepto_Obra = (rs.getString("nIdConcepto_Obra") == null) ? null : "'" + rs.getString("nIdConcepto_Obra") + "'";
                        String lContrarreciboImpreso = (rs.getString("lContrarreciboImpreso") == null) ? null : rs.getString("lContrarreciboImpreso");
                        String nTipoCambio = (rs.getString("nTipoCambio") == null) ? null : rs.getString("nTipoCambio");
                        String cOficioDiferenciaCambiaria = (rs.getString("cOficioDiferenciaCambiaria") == null) ? null : "'" + rs.getString("cOficioDiferenciaCambiaria") + "'";
                        String cIdTipoOperacion = (rs.getString("cIdTipoOperacion") == null) ? null : "'" + rs.getString("cIdTipoOperacion") + "'";
                        String fperiodode = (rs.getString("fperiodode") == null) ? null : "'" + rs.getString("fperiodode").replace("-", "") + "'";
                        String fperiodohasta = (rs.getString("fperiodohasta") == null) ? null : "'" + rs.getString("fperiodohasta").replace("-", "") + "'";
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        // Insertar Encabezado
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Federalizado','" + rs.getString("cFolioContratoObra") + "', " + "'" + rs.getString("nFolioEnc") + "','" + rs.getString("caNoContrarrecibo") + "', " + "'" + rs.getString("cIdRFC") + "','" + rs.getString("NOMBRE") + "','" + rs.getString("cNoEstimacion") + "'," + cIdTipoOperacion + ", " + "'" + rs.getString("fAplicacion") + "','" + rs.getString("cNoFactura") + "','" + rs.getString("mImporteBruto") + "', " + "'" + rs.getString("mImporteSancion") + "','" + rs.getString("mImporteDevolucion") + "','" + rs.getString("mAmortizacionAnticipo") + "', " + "'" + rs.getString("mImporteIVA") + "','" + rs.getString("mImporteRetencion") + "','" + rs.getString("mImportePenalizacion") + "', " + "'" + rs.getString("mImporteNeto") + "','" + rs.getString("nPorcAmortizacion") + "','" + rs.getString("cIdEstadoEstimacion") + "'," + lContrarreciboImpreso + ", " + "'" + rs.getString("cConcepto") + "','" + rs.getString("lAmortizarAnticipoConEscalacion") + "'," + nIdConcepto_Obra + "," + fProgramadaPago + "," + nTipoCambio + ", " + "'" + rs.getString("cIdUsuarioCaptura") + "','" + rs.getString("cIdUsuarioImpresion") + "','" + rs.getString("cIdUsuarioRevision") + "', " + "'" + rs.getString("cIdUsuarioAprobacion") + "','" + rs.getString("cIdUsuarioRechazo") + "','" + rs.getString("ID_DESTINO_GASTO") + "', " + "'" + rs.getString("ID_TIPO_OPERACION") + "','" + rs.getString("ID_TIPO_MOVIMIENTO") + "','" + rs.getString("ID_TIPO_FONDO") + "'," + fperiodode + "," + fperiodohasta + ", " + "'" + rs.getString("cUnidadResponsable") + "','" + rs.getString("cRamo") + "','" + rs.getString("cIdTipoDocumento") + "', " + "'" + rs.getString("mImporteMasIva") + "','" + rs.getString("mAmortizacion") + "','" + rs.getString("mSaldoCedula") + "', " + "'" + rs.getString("mAcumuladoxpagar") + "','" + rs.getString("mSaldoAnticipo") + "','" + rs.getString("cCentroContable") + "', " + "'" + rs.getString("cMes") + "','" + rs.getString("aEjercicioFiscal") + "','" + rs.getString("mAmortizacionAcumulado") + "', " + "'" + rs.getString("mImporteSancionAcumulado") + "','" + rs.getString("mImporteDevolucionAcumulado") + "', " + "'" + rs.getString("cDocumentoHaplicado") + "','" + rs.getString("nFolioPoliza") + "','" + rs.getString("cTipoPoliza") + "'," + ALM + ", " + "'" + rs.getString("capitulo") + "','" + rs.getString("cReferenciaPRODDER") + "'," + nPorcImpuestoCedular + "," + lAplicaImpuestoCedular + "," + cOficioDiferenciaCambiaria + ", " + "'" + rs.getString("U_LOGIN") + "'," + nFolioPolizaCancelacion + "," + fCancelacion + ",'" + rs.getString("cDescripcionPoliza") + "', " + "'" + rs.getString("cUnidadResponsableContable") + "','" + rs.getString("nEnviadoSICOP") + "','" + rs.getString("NumPagoAMF") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            String cDocumentoHaplicado = (rs.getString("cDocumentoHaplicado") == null) ? null : "'" + rs.getString("cDocumentoHaplicado") + "'";
                            String nFolioPoliza = (rs.getString("nFolioPoliza") == null) ? null : rs.getString("nFolioPoliza");
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("nDocRenglon") + "'," + " '" + rsDS.getString("nMes") + "','" + rsDS.getString("cEjercicio") + "','" + rsDS.getString("cIdEntidadContable") + "', " + " '" + rsDS.getString("cIdRelacion") + "','" + rsDS.getString("EP") + "','" + rsDS.getString("cIdCuentaContable") + "', " + "  '" + rsDS.getString("mComprometido") + "','" + rsDS.getString("nPoliza") + "','" + rsDS.getString("ID_TIPO_MOVIMIENTO") + "', " + " '" + rsDS.getString("ID_TIPO_CONCEPTO") + "','" + rsDS.getString("cEvento") + "','" + rsDS.getString("aEjercicioFiscal") + "', " + " '" + rsDS.getString("cCentroContable") + "','" + rsDS.getString("cMes") + "','" + rsDS.getString("RFC") + "', " + " '" + rsDS.getString("mImporteNeto") + "','" + rsDS.getString("ALM") + "','" + rsDS.getString("mImporteBruto") + "', " + " '" + rsDS.getString("mImporteMasIva") + "','" + rsDS.getString("mImporteIva") + "','" + rsDS.getString("nCapitulo") + "'," + " " + cDocumentoHaplicado + "," + nFolioPoliza + ",'" + rsDS.getString("cTipoPoliza") + "','" + rsDS.getString("mSancion") + "', " + " '" + rsDS.getString("mDevolucion") + "','" + rsDS.getString("mImporteAmortiza") + "','" + rsDS.getString("mRetencion") + "', " + " '" + rsDS.getString("mPenalizacion") + "','" + rsDS.getString("m2Millar") + "','" + rsDS.getString("m23IVA") + "', " + " '" + rsDS.getString("mISRHonorarios") + "','" + rsDS.getString("mObra5") + "','" + rsDS.getString("mImporteFlete4") + "', " + " '" + rsDS.getString("mISRArrenda") + "','" + rsDS.getString("mRetImpuestoCedular") + "','" + rsDS.getString("mBruto") + "', " + " '" + rsDS.getString("mAmortizacionAnticipo") + "','" + rsDS.getString("mIVA") + "','" + rsDS.getString("mNeto") + "', " + " '" + rsDS.getString("m5Millar") + "','" + rsDS.getString("mFletes") + "','" + rsDS.getString("mCedular") + "', " + " '" + rsDS.getString("mImporte") + "','" + rsDS.getString("mImporteIvaArrenda") + "','" + rsDS.getString("mImporteIvaHonorarios") + "', " + " '" + rsDS.getString("mImporteFlete23") + "','" + rsDS.getString("mImporteIvaProv") + "','" + rsDS.getString("mImporteObra") + "', " + " '" + rsDS.getString("mCNIC") + "','" + rsDS.getString("mIMDT") + "','" + rsDS.getString("mTesofe") + "','" + rsDS.getString("altaAlmacen") + "')");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (doc[i].equalsIgnoreCase("Ajenas")) {
                        String fCancelacion = (rs.getString("fCancelacion") == null) ? null : "'" + rs.getString("fCancelacion") + "'";
                        String nFolioPolizaCancelacion = (rs.getString("nFolioPolizaCancelacion") == null) ? null : rs.getString("nFolioPolizaCancelacion");
                        String fperiodode = (rs.getString("fperiodode") == null) ? null : "'" + rs.getString("fperiodode").replace("-", "") + "'";
                        String fperiodohasta = (rs.getString("fperiodohasta") == null) ? null : "'" + rs.getString("fperiodohasta").replace("-", "") + "'";
                        String cIdUsuarioAprobacion = (rs.getString("cIdUsuarioAprobacion") == null) ? null : "'" + rs.getString("cIdUsuarioAprobacion") + "'";
                        String cIdUsuarioRechazo = (rs.getString("cIdUsuarioRechazo") == null) ? null : "'" + rs.getString("cIdUsuarioRechazo") + "'";
                        String nFolioPoliza = (rs.getString("nFolioPoliza") == null) ? null : rs.getString("nFolioPoliza");
                        String nFolioSICOP = (rs.getString("nFolioSICOP") == null) ? null : rs.getString("nFolioSICOP");
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        // Insertar Encabezado
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Ajenas','" + rs.getString("nFolioEnc") + "','" + rs.getString("cNombre") + "', " + " '" + rs.getString("cBeneficiario") + "','" + rs.getString("fCaptura") + "','" + rs.getString("fAplicacion") + "', " + " " + fperiodode + "," + fperiodohasta + ",'" + rs.getString("cConcepto") + "', " + " '" + rs.getString("caNoContrarrecibo") + "','" + rs.getString("mImportes") + "', " + " '" + rs.getString("U_LOGIN") + "','" + rs.getString("cCentroContable") + "', " + " '" + rs.getString("aEjercicioFiscal") + "','" + rs.getString("cUnidadResponsable") + "','" + rs.getString("cRamo") + "', " + " " + cIdUsuarioAprobacion + "," + cIdUsuarioRechazo + ",'" + rs.getString("cDocumentoHaplicado") + "', " + " " + nFolioPoliza + "," + nFolioPolizaCancelacion + "," + fCancelacion + ", " + " '" + rs.getString("cDescripcionPoliza") + "','" + rs.getString("cUnidadResponsableContable") + "','" + rs.getString("nEnviadoSICOP") + "', " + " '" + rs.getString("cTipoPoliza") + "','" + rs.getString("cIDRFC") + "','" + rs.getString("cIdTipoDocumento") + "', " + " " + nFolioSICOP + ",'" + rs.getString("NumPagoAMF") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            nFolioo = rsDS.getString("nFolioDetalle");
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("nDocRenglon") + "'," + " '" + rsDS.getString("nFolioDoc") + "','" + rsDS.getString("cTRetencion") + "','" + rsDS.getString("Ep") + "', " + " '" + rsDS.getString("fAplicacion") + "','" + rsDS.getString("cTipoDoc") + "','" + rsDS.getString("cMes") + "', " + "  '" + rsDS.getString("cEvento") + "','" + rsDS.getString("aEjercicioFiscal") + "','" + rsDS.getString("cCentroContable") + "', " + " '" + rsDS.getString("mTotal") + "','" + rsDS.getString("mEntero") + "','" + rsDS.getString("mSobrante") + "', " + " '" + rsDS.getString("mImporteFlete23") + "','" + rsDS.getString("mISRHonorarios") + "','" + rsDS.getString("mISRArrenda") + "', " + " '" + rsDS.getString("mImporteFlete4") + "','" + rsDS.getString("mCNIC") + "','" + rsDS.getString("mIMDT") + "', " + " '" + rsDS.getString("mObra5") + "','" + rsDS.getString("mTesofe") + "','" + rsDS.getString("mRetImpuestoCedular") + "', " + " '" + rsDS.getString("caNoContrarrecibo") + "','" + rsDS.getString("RFC") + "','" + rsDS.getString("Periodo13") + "', " + " '" + rsDS.getString("ADEFAS") + "')");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (doc[i].equalsIgnoreCase("Registro Pasivo")) {
                        // Buscar Detalles
                        pstmnDS = conn.prepareStatement("SELECT " + campoDet + " FROM vCierreCuentasPorPagarDetalles WITH(NOLOCK) WHERE " + nFolio + " = ? AND documento = ? ");
                        pstmnDS.setString(1, nFolioDoc);
                        pstmnDS.setString(2, doc[i]);
                        rsDS = pstmnDS.executeQuery();
                        // Insertar Encabezado
                        pstmnEI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarEncabezado (" + campoInsertEnc + ") " + " VALUES (" + iCCXP[i] + ",'Registro Pasivo','" + rs.getString("caNoContrarrecibo") + "','" + rs.getString("referenciaAMF") + "', " + " '" + rs.getString("cIdRFC") + "','" + rs.getString("cuentaBancaria") + "', " + " '" + rs.getString("aEjercicioFiscal") + "','" + rs.getString("cUnidadResponsable") + "'," + " '" + rs.getString("fCaptura") + "','" + rs.getString("fechaPago") + "', " + " '" + rs.getString("mImportes") + "','" + rs.getString("cCentroContable") + "', " + " '" + rs.getString("U_LOGIN") + "','" + rs.getString("estatus") + "')");
                        pstmnEI.executeUpdate();
                        // Insertar Detalles
                        while (rsDS.next()) {
                            pstmntDI = conn.prepareStatement("INSERT INTO tCierreCuentasPorPagarDetalle(" + campoInsertDeta + ") " + " VALUES (" + iCCXP[i] + ",'" + rsDS.getString("nFolioDetalle") + "','" + rsDS.getString("EP") + "', " + " '" + rsDS.getString("mImporte") + "')");
                            pstmntDI.executeUpdate();
                            nFolioo = rsDS.getString("nFolioDetalle");
                        }
                    }
                    if (!doc[i].equalsIgnoreCase("Registro Pasivo")) {
                        log.warn("No es Pasivo");
                        AccountingEngine accEng = new AccountingEngine();
                        accEng.setValidaInsuficienciaDeSaldo(false);
                        accEng.cancelAccountingApplication(conn, cTipoDocumento, nFolioo, cTablaPadre, cTablaHija, cFolioo, today);
                    }
                    conn.commit();
                }
                json.put("estatus", "guardado");
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error occurred", "Error:" + e);
            try {
                json.put("estatus", "error");
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            CloseObject.closeObject(pstmnES);
            CloseObject.closeObject(pstmnEI);
            CloseObject.closeObject(pstmnDS);
            CloseObject.closeObject(pstmntDI);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsDS);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    private boolean aplicarDisminucionApartado(Connection conn, String tipoDoc, String nFolio) throws Exception {
        boolean aplicaDApartado = true;
        String tipoDocOrg = "", esRETE = "";
        String queryInsertaEnc = null;
        String queryInsertDet = null;
        PreparedStatement psEncabezado = null;
        PreparedStatement psDetalle = null;
        try {
            tipoDocOrg = validaTipoPago(conn, Integer.parseInt(nFolio));
            esRETE = validaEsRete(conn, Integer.parseInt(nFolio));
            if (("RELACIONGASTOS".equals(tipoDocOrg) || "PAGODIRECTO".equals(tipoDocOrg)) && !"esRete".equals(esRETE)) {
                CFSequenceManager seq = CFSequenceManager.getInstance();
                int nFolioPagoApartado = seq.nextVal("APARTADO");
                queryInsertaEnc = "INSERT INTO tPagoApartadoEncabezado ( nFolioPagoApartado, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cDocumentoHaplicado, nFolioPoliza, cTipoPoliza, cUnidadResponsableContable, nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, cUnidadResponsable )" + " SELECT ? AS nFolioPagoApartado, 'DISMINUCIONDEV' AS cTipoPago, nFolioDisminucionDev AS nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, NULL AS cDocumentoHaplicado, NULL  AS nFolioPoliza, 'PR' AS cTipoPoliza, cUnidadResponsableContable, NULL AS nFolioPolizaCancelacion, NULL AS fCancelacion, 'Disminucion Apartado del pago: ' + caNoContrarrecibo AS cDescripcionPoliza, cUnidadResponsable FROM tDisminucionDevEncabezado WITH (NOLOCK) WHERE nFolioDisminucionDev = ? ";
                queryInsertDet = "INSERT INTO tPagoApartadoDetalle ( nFolioPagoApartado, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mImporte, cUnidadResponsable )" + " SELECT ? AS nFolioPagoApartado, nDocRenglon, cMes, 'APARTADO' AS cEvento, aEjercicioFiscal, cCentroContable, EP, mTotal AS mImporte, cUnidadResponsable FROM tDisminucionDevDetalle detalle WITH(NOLOCK) WHERE nFolioDisminucionDev = ?";
                psEncabezado = conn.prepareStatement(queryInsertaEnc);
                psDetalle = conn.prepareStatement(queryInsertDet);
                psEncabezado.setInt(1, nFolioPagoApartado);
                psEncabezado.setInt(2, Integer.parseInt(nFolio));
                psDetalle.setInt(1, nFolioPagoApartado);
                psDetalle.setInt(2, Integer.parseInt(nFolio));
                int insertados = psEncabezado.executeUpdate();
                insertados += psDetalle.executeUpdate();
                log.debug("Object: " + String.valueOf("Se insertaron " + insertados + " campos para aplicar disminucion de apartado "));
                log.info("Object: {}", " Aplicando motor para el Pasivo Diferido " + Integer.parseInt(nFolio));
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                accEng.makeAccountingApplication(conn, "PAGOAPARTADO", String.valueOf(nFolioPagoApartado), "tPagoApartadoEncabezado", "tPagoApartadoDetalle", "nFolioPagoApartado");
            } else {
                aplicaDApartado = false;
            }
        } finally {
            CloseObject.closeObject(psEncabezado, false);
            CloseObject.closeObject(psDetalle, false);
        }
        return aplicaDApartado;
    }

    private String validaTipoPago(Connection conn, int nfoliodisminuciondev) throws Exception {
        String query = " SELECT TOP 1 cTipoDoc " + " FROM   tdisminuciondevdetalle disminucionDevengado WITH (NOLOCK) " + " WHERE  nfoliodisminuciondev = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String tipoDoc = "";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nfoliodisminuciondev);
            rs = ps.executeQuery();
            if (rs.next()) {
                tipoDoc = rs.getString("cTipoDoc");
            }
            return tipoDoc;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public JSONObject cancelaContrato(String tipoDoc, String nFolio, Usuario usuario, String contrato) {
        Connection conn = null;
        JSONObject json = new JSONObject();
        String errorApl = null;
        try {
            String[] nFol = tipoDoc.split("/");
            String[] contratos = contrato.split("/");
            conn = getConnection();
            Integer folio = 0;
            String cont = "";
            for (int i = 0; i < nFol.length; i++) {
                folio = Integer.parseInt(nFol[i], 10);
                cont = contratos[i];
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                accEng.cancelAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(folio), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
                // Actualizar el estatus de la tabla de compromisos
                updateCompromiso(conn, folio);
                // Actualiza el estatus del contrato
                updateContratos(conn, cont);
                json.put("estatus", "guardado");
            }
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            errorApl = exc.toString();
            try {
                json.put("estatus", errorApl);
                conn.rollback();
            } catch (Exception x) {
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public static void updateCompromiso(Connection conn, Integer folio) throws Exception {
        PreparedStatement pst = null;
        String query = "update tCompromisoEncabezado set cDocumentoHaplicado = 'C' where nFolioCompromiso = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void updateContratos(Connection conn, String contratos) throws Exception {
        PreparedStatement pst = null;
        String query = "update pContratoFederalizado set cDocumentoHaplicado = 'C' where cIdContrato = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, contratos);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    private String validaEsRete(Connection conn, int nfoliodisminuciondev) throws Exception {
        String query = " SELECT CASE WHEN cEvento LIKE '%RETE%' THEN 'esRete' ELSE 'nEsRete' END AS validaRete " + " FROM   tdisminuciondevdetalle disminucionDevengado WITH (NOLOCK) " + " WHERE  nfoliodisminuciondev = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String validaRete = "";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nfoliodisminuciondev);
            rs = ps.executeQuery();
            if (rs.next()) {
                validaRete = rs.getString("validaRete");
            }
            return validaRete;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public JSONObject cancelaDevengado(Connection conn, String tipoDocumento, String nFolio, Usuario usuario, boolean esFirmaElectronica) throws Exception {
        PreparedStatement pstmnEnc = null, pstmDoc = null, pstmnInse = null, pstmnCaja = null;
        ResultSet rsEnc = null, rsDoc = null, rsCaja = null;
        JSONObject json = new JSONObject();
        String errorApl = null;
        PreparedStatement pstmn = null;
        ResultSet rs = null;
        try {
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            String[] doc = tipoDocumento.split("/");
            String[] nFol = nFolio.split("/");
            String tipoDoc = "";
            String idRFC = "";
            String importeNeto = "mImporteNeto";
            for (int i = 0; i < doc.length; i++) {
                String tipoPago = doc[i];
                String tEncabezado = SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(tipoPago) == null ? ("t" + doc[i] + "Encabezado") : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(tipoPago);
                String tDetalle = SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_D.get(tipoPago) == null ? ("t" + doc[i] + "Detalle") : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_D.get(tipoPago);
                String keyName = SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(tipoPago) == null ? ("nFolio" + doc[i]) : SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(tipoPago);
                if ("OPERAJENAS".equalsIgnoreCase(tipoPago)) {
                    OperacionAjenaManager.cancelaOperacionAjena(conn, Integer.parseInt(nFol[i]));
                    if (esFirmaElectronica) {
                        cancelaFirmaElectronica(conn, tipoPago, tDetalle, keyName, tEncabezado, Integer.parseInt(nFol[i]));
                    }
                    json.put("estatus", "guardado");
                    continue;
                }
                if (accEng.cancelAccountingApplication(conn, doc[i], nFol[i], tEncabezado, tDetalle, keyName, today)) {
                    PasivoDiferidoManager.cancelarPasivoDiferido(conn, doc[i], nFol[i]);
                    IngresoGreenMexManager.cancelarIngresoGreenMex(conn, doc[i], nFol[i]);
                    if ("RELACIONGASTOS".equals(tipoPago) || "RelacionGastos".equals(tipoPago)) {
                        int nIdComision = TransporteDAO.buscaComision(conn, Integer.parseInt(nFol[i], 10));
                        if (nIdComision != 0) {
                            TransporteDAO.updateEstatusBoletos(conn, Integer.parseInt(nFol[i], 10));
                            TransporteDAO.borrarTransporteAereo(conn, nIdComision, Integer.parseInt(nFol[i], 10), doc[i]);
                            TransporteDAO.borrarTaxi(conn, Integer.parseInt(nFol[i], 10), tipoPago);
                            if (ComisionDAO.estaFinalizada(conn, nIdComision)) {
                                ComisionDAO.actualizaEstatusAplicado(conn, nIdComision, 9);
                                Agenda agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, nIdComision);
                                GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, agenda.getIdEmpleado(), "Comprobando");
                            }
                        }
                        borraIngreso(conn, Integer.parseInt(nFol[i], 10));
                    } else if ("PagoDirecto".equalsIgnoreCase(tipoPago)) {
                        //Actualizar status
                        SuficienciaPagoDirectoEncabezadoManager.updateStatusCancelPayment(conn, Integer.parseInt(nFol[i], 10));
                    }
                    if (esFirmaElectronica) {
                        cancelaFirmaElectronica(conn, tipoPago, tDetalle, keyName, tEncabezado, Integer.parseInt(nFol[i]));
                    }
                    LogCancelaDevengadoManager.registraLog(conn, doc[i], Integer.parseInt(nFol[i], 10), usuario.getLogin());
                    FacturaManager.eliminaFacturas(conn, doc[i], nFol[i]);
                    if (importeNeto.equalsIgnoreCase("mImporteNeto")) {
                        if (doc[i].equalsIgnoreCase("PagoDirecto")) {
                            tipoDoc = "PDIR";
                            idRFC = "cIdRFC";
                        } else if (doc[i].equalsIgnoreCase("RelacionGastos")) {
                            tipoDoc = "RELG";
                            idRFC = "cIdRFC";
                        } else if (doc[i].equalsIgnoreCase("PagoDiverso")) {
                            tipoDoc = "PDIR";
                            idRFC = "RFC";
                        } else if (doc[i].equalsIgnoreCase("Pagoobra")) {
                            tipoDoc = "POBR";
                            idRFC = "RFC";
                        } else if (doc[i].equalsIgnoreCase("OperAjenas")) {
                            tipoDoc = "OPAJ";
                            idRFC = "cIDRFC";
                            importeNeto = "mImportes";
                        } else if (doc[i].equalsIgnoreCase("PagoFederalizado")) {
                            tipoDoc = "PFED";
                            idRFC = "RFC";
                        } else if (doc[i].equalsIgnoreCase("Retencion")) {
                            tipoDoc = "RETE";
                            idRFC = "cIDRFC";
                            importeNeto = "mImporteRetencion";
                        }
                        String sqlstmnt = "";
                        sqlstmnt = "SELECT '" + tipoDoc + "-'+RTRIM(cUnidadResponsable)+'-" + nFol[i] + "' AS NumeroFolio, caNoContrarrecibo, " + idRFC + " AS RFC, dNombre AS nomRFC, fAplicacion, " + importeNeto + " AS mImporteNeto FROM t" + doc[i] + "Encabezado with(nolock), tBeneficiario tb with(nolock) WHERE " + idRFC + " = tb.dRFC AND nFolio" + doc[i] + " = ? ";
                        pstmDoc = conn.prepareStatement(sqlstmnt);
                        pstmDoc.setString(1, nFol[i]);
                        rsDoc = pstmDoc.executeQuery();
                        if (doc[i].equalsIgnoreCase("PagoDirecto") || doc[i].equalsIgnoreCase("RelacionGastos")) {
                            if (doc[i].equalsIgnoreCase("PagoDirecto")) {
                                ConsumePAASInterface consumePAAS = new ConsumePAASImpl(jniName);
                                consumePAAS.liberaPaasPago(conn, Integer.parseInt(nFol[i]));
                            }
                            pstmnEnc = conn.prepareStatement("SELECT nFolioPagoApartado FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago = ? and nFolioPago = ? and cDocumentoHaplicado = 'S' ");
                            pstmnEnc.setString(1, doc[i]);
                            pstmnEnc.setString(2, nFol[i]);
                            rsEnc = pstmnEnc.executeQuery();
                            if (rsEnc.next()) {
                                Integer FolioComprobacion = Integer.parseInt(nFol[i], 10);
                                nFol[i] = rsEnc.getString("nFolioPagoApartado");
                                doc[i] = "PagoApartado";
                                if (accEng.cancelAccountingApplication(conn, doc[i], nFol[i], "t" + doc[i] + "Encabezado", "t" + doc[i] + "Detalle", "nFolio" + doc[i], today)) {
                                    if (tipoPago.equalsIgnoreCase("RelacionGastos")) {
                                        updateEliminaInfoVuelosRG(conn, FolioComprobacion);
                                        // -------------------------------------------
                                        pstmnCaja = conn.prepareStatement("SELECT nFolioCaja FROM dbo.tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?");
                                        pstmnCaja.setInt(1, FolioComprobacion);
                                        rsCaja = pstmnCaja.executeQuery();
                                        String us = usuario.getLogin();
                                        if (rsCaja.next()) {
                                            Integer FolioCaja = rsCaja.getInt("nFolioCaja");
                                            // Ya que se hizo la aplicacion
                                            // contable se elimina el detalle
                                            // del estado de cuenta y se
                                            // actualiza el encabezado con el
                                            // remanente cancelado
                                            CajaManager.borraDetalleViaticos(conn, FolioCaja, FolioComprobacion, us);
                                            CajaManager.borraDetalleLaudos(conn, FolioCaja, FolioComprobacion, us);
                                        }
                                    }
                                    json.put("estatus", "guardado");
                                }
                            } else {
                                json.put("estatus", "guardado");
                            }
                        } else {
                            // URVP.06012014 SE ACTUALIZA A EMITIDA LA RECEPCION
                            // PARA PODER USAR ESA RECEPCION EN OTRO PAGO
                            if (doc[i].equalsIgnoreCase("PagoDiverso")) {
                                int FolioPAGODIVERSO = 0;
                                FolioPAGODIVERSO = Integer.parseInt(nFol[i]);
                                eliminaVuelosPDIV(conn, FolioPAGODIVERSO);
                                // Liberar penas de pagos Diversos y RG con OC
                                if (tienePena(conn, Integer.parseInt(nFol[i], 10))) {
                                    StringBuilder queryPena = new StringBuilder();
                                    queryPena.append("UPDATE mPenaltyDeduction SET nIdEstate = 3 WHERE nIdPenaltyDeduction = (SELECT nFolioPenalizacion FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ?) ");
                                    PreparedStatement pstmnPena = null;
                                    int afectados = 0;
                                    pstmnPena = conn.prepareStatement(queryPena.toString());
                                    pstmnPena.setInt(1, FolioPAGODIVERSO);
                                    afectados = pstmnPena.executeUpdate();
                                }
                                if (esRecepcionDeMaterial(conn, Integer.parseInt(nFol[i], 10))) {
                                    StringBuilder query = new StringBuilder();
                                    query.append("UPDATE	mRecepcionpMat ");
                                    query.append("   SET	nIdEstadoRecepMat = ? ");
                                    query.append(" WHERE nIdEstadoRecepMat = 3 ");
                                    query.append("   AND	cIdRecepMat = (SELECT ISNULL(cIdRecepMat,'') FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ?) ");
                                    query.append("	AND cIdpedContDef = (SELECT cFolioPAGODIVERSO FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ? )");
                                    PreparedStatement pstmnstatus = null;
                                    int afectados = 0;
                                    int nStatusRecepcion = 4;
                                    pstmnstatus = conn.prepareStatement(query.toString());
                                    pstmnstatus.setInt(1, nStatusRecepcion);
                                    pstmnstatus.setInt(2, FolioPAGODIVERSO);
                                    pstmnstatus.setInt(3, FolioPAGODIVERSO);
                                    afectados = pstmnstatus.executeUpdate();
                                    if (afectados == 0) {
                                        json.put("estatus", "No se pudo actualizar el status a cancelado");
                                        throw new Exception("Error al actualizar el status del folio:" + nFol[i]);
                                    } else {
                                        String msjUpdateAmortiza = "";
                                        msjUpdateAmortiza = amortizacionUpdate(conn, nFol[i], true);
                                        if (!"".equalsIgnoreCase(msjUpdateAmortiza)) {
                                            throw new Exception(msjUpdateAmortiza);
                                        }
                                    }
                                    // Quitar está validación cuando los
                                    // devengados estén bien empatado con
                                    // inventario, es temporal
                                    String sql = "select case when cCentroContable<>'10' then 1 else 10 end cCentroContableForaneo,case when fAplicacion<convert(date,'2016-08-19') then 1" + " else 0 end laFechaEsAnterior  from tPAGODIVERSOEncabezado with(nolock) where nFolioPAGODIVERSO=" + FolioPAGODIVERSO;
                                    log.info("Object: {}", "Obtención de datos para revisar si se ejecuta el ws de la cancelación del devengado  : " + sql);
                                    pstmn = conn.prepareStatement(sql);
                                    rs = pstmn.executeQuery();
                                    int ccForaneo = 0;
                                    int laFechaEsAnterior = 0;
                                    if (rs.next()) {
                                        ccForaneo = rs.getInt("cCentroContableForaneo");
                                        laFechaEsAnterior = rs.getInt("laFechaEsAnterior");
                                    }
                                    int status = 0;
                                    if (!(ccForaneo == 1 && laFechaEsAnterior == 1)) {
                                        if (esCapitulo5Mil(conn, Integer.parseInt(nFol[i], 10)) || esAlmacenVirtual(conn, Integer.parseInt(nFol[i], 10)) || (validaPartidayTipoPago(conn, Integer.parseInt(nFol[i], 10)) && !(validaEsPedidoCap2(conn, Integer.parseInt(nFolio, 10))))) {
                                            status = WSManager.sendAdquistion(conn, nFol[i], 2);
                                            if (status != 0 && status != -2) {
                                                json.put("estatus", "Error de respuesta del Web Service");
                                                throw new Exception("Error de respuesta del Web Service para el folio:" + nFol[i]);
                                            }
                                        }
                                    } else {
                                        log.info("No se ejecuta el ws para los estados si el devengado está generado antes de la fecha '2016-08-19");
                                    }
                                } else {
                                    log.info("Object: {}", "El folio del pago diverso " + nFol[i] + " está ligado a una recepción de anticipo por tal motivo no se manda llamar el web service.");
                                    if (!cancelaRecepcionAnticipo(conn, FolioPAGODIVERSO)) {
                                        PreparedStatement pstmncancela = null;
                                        int cancelo = 0;
                                        int nStatusRecepcion = 4;
                                        String sSqlCancela = " UPDATE	Anticipo " + "   SET       Anticipo.mImporteAnticipo = Anticipo.mImporteAnticipo - PAGODIVERSO.mImporteBruto " + " 			, Anticipo.mImporteAnticipoIVA = Anticipo.mImporteAnticipoIVA - (PAGODIVERSO.mImporteNeto - PAGODIVERSO.mImporteBruto) " + "   		, Anticipo.mTotalAnticipo = Anticipo.mTotalAnticipo - PAGODIVERSO.mImporteNeto " + "	FROM	pContratoDiversoAnticipo Anticipo WITH(NOLOCK) " + "   INNER JOIN tPAGODIVERSOEncabezado PAGODIVERSO WITH (NOLOCK) " + "   	ON (PAGODIVERSO.cFolioPAGODIVERSO = Anticipo.cIdContrato) " + "   WHERE PAGODIVERSO.nFolioPAGODIVERSO = ? ";
                                        sSqlCancela = sSqlCancela + "\n" + " UPDATE	mRecepcionpMat " + "   SET	nIdEstadoRecepMat = ? " + " WHERE nIdEstadoRecepMat = 3 " + "   AND	cIdRecepMat = (SELECT ISNULL(cIdRecepMat,'') FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ?) " + "	AND cIdpedContDef = (SELECT cFolioPAGODIVERSO FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ? ) ";
                                        pstmncancela = conn.prepareStatement(sSqlCancela);
                                        pstmncancela.setInt(1, FolioPAGODIVERSO);
                                        pstmncancela.setInt(2, nStatusRecepcion);
                                        pstmncancela.setInt(3, FolioPAGODIVERSO);
                                        pstmncancela.setInt(4, FolioPAGODIVERSO);
                                        cancelo = pstmncancela.executeUpdate();
                                        log.info("Object: {}", "Se cancelaron: " + cancelo + " RM ");
                                    }
                                }
                            }
                            json.put("estatus", "guardado");
                        }
                    } else {
                        json.put("estatus", "noGuardado");
                    }
                }
            }
            return json;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        } finally {
            CloseObject.closeObject(pstmDoc);
            CloseObject.closeObject(pstmnInse);
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(pstmn);
            CloseObject.closeObject(rsDoc);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsEnc);
        }
    }

    private boolean tienePena(Connection conn, int folio) throws SQLException {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM tPAGODIVERSOEncabezado PDIV WITH (NOLOCK) \r\n" + "JOIN mPenaltyDeduction PENA WITH (NOLOCK) ON PDIV.nFolioPenalizacion = PENA.nIdPenaltyDeduction \r\n" + "WHERE nFolioPAGODIVERSO = ? ";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nfolioPagoDiverso: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return respuesta;
    }

    private void borraIngreso(Connection conn, int folio) throws Exception {
        boolean respuesta = false;
        PreparedStatement pstm = null;
        PreparedStatement pstUP = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM tComprobacionLaudos_Ingreso WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            log.info("Object: {}", "query: " + query + "\nFolioRELACIONGASTOS: " + folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                respuesta = true;
            }
            if (respuesta) {
                pstUP = conn.prepareStatement("DELETE FROM tComprobacionLaudos_Ingreso WHERE nFolioRELACIONGASTOS = ?");
                pstUP.setInt(1, folio);
                pstUP.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(pstUP);
        }
    }
}
