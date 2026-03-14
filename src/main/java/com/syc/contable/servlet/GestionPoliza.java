package com.syc.contable.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.AccountingEngine;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager;
import com.syc.gestion.core.TablasManager;
import com.syc.contable.CancelaDocumento;
import com.syc.contable.PolizaManager;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "GestionPoliza", urlPatterns = { "/gstn/GestionPoliza" })
public class GestionPoliza extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Connection conn = null;

    private String nfolioD = null;

    private int status = 0;

    private int id_oper = 0;

    private String CO_RESPONSABLE = "''";

    private String MOVIMIENTO = "";

    private String NOMBREUSR = "''";

    private PreparedStatement st = null;

    private Usuario u = null;

    final static String ERRORMOV = "Error: No se recibió el tipo de movimiento";

    final static String ERRORDOCPOL = "Error: No se recibió el folio del documento";

    final static String ERROROPER = "Error: No se recibió la operación";

    final static String REVOK = "Revisión guardada correctamente";

    final static String REVKO = "ERROR AL AVANZAR REVISIÓN";

    final static String AUTOK = "AUTORIZACIÓN GUARDADA CORRECTAMENTE";

    final static String AUTKO = "ERROR AL AVANZAR AUTORIZACIÓN";

    final static String ERRORID = "ERROR AL OBTENER ID OPERACIÓN";

    final static String ERREDIT = "FALLÓ AL REGRESAR LA POLIZA";

    final static String OKEDIT = "POLIZA REGRESADA CORRECTAMENTE";

    final static String ERRCAN = "FALLÓ AL CANCELAR LA PÓLIZA";

    final static String OKCAN = "PÓLIZA CANCELADA CORRECTAMENTE EN EL MES CONTABLE ABIERTO";

    String OKCANA = "PÓLIZA DEL MES ANTERIOR CANCELADA CORRECTAMENTE CON FECHA: ";

    final static String ERROROP = "Reintente nuevamente en un momento";

    final static String ERRREG = "No se pudo regresar el documento, reintente mas tarde";

    final static String CAPTURA = "'CAPTURA_POLIZA'";

    final static String REVISION = "'REVISION_POLIZA'";

    final static String AUTORIZACION = "'AUTORIZA_POLIZA'";

    final static String CONSULTA = "'CONSULTA_POLIZA'";

    final static String EDICION = "E";

    final static String APLICADO = "S";

    final static String NUEVO = "";

    final static String ENCABEZADO = "update tdocpolizaEncabezado WITH (ROWLOCK) set ncambio=?,cDocumentoHaplicado=- where nfolioDocPoliza=+";

    final static String CASO_DATO = "update CG_CASO_DATO WITH (ROWLOCK) set CD_VALOR=? where ID_CD=- and id_caso=(select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=+)	";

    final static String TPOLIZA = "update tpoliza WITH (ROWLOCK) set DocHAplicado=? where cTipoDocumento='DOCPOLIZA' and nFolioDocumento=-	";

    final static String TMOVIMIENTO = "delete *  from tmovimiento where cTipoDocumento='DOCPOLIZA' cFolioDocumentoMovimiento= ?	";

    final static String CASO_OPERACION = "update CG_CASO_OPERACION WITH (ROWLOCK)  set  ID_OPER=?,CO_RESPONSABLE=- where id_caso=(select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=+)	";

    final static String CASO = "update CG_CASO WITH (ROWLOCK) set C_STATUS=? where ID_CASO=-	";

    final static String ID_OPER = "select ID_OPER from CG_CASO_OPERACION with(nolock) where id_caso=(select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=?)	";

    final static String USRCAP = "update tDocPolizaEncabezado WITH (ROWLOCK) set cIdUsuarioCaptura=? where nFolioDocPoliza=-";

    final static String USRREV = "update tDocPolizaEncabezado WITH (ROWLOCK) set cIdUsuarioRevision=? where nFolioDocPoliza=-";

    final static String USRAUT = "update tDocPolizaEncabezado WITH (ROWLOCK) set cIdUsuarioAprobacion=? where nFolioDocPoliza=-";

    final static String CREAENCABCANCEL = " insert into tdocpolizaCancelEncabezado select nFolioDocPoliza,fCarga,fAplicacion,cCentroContable,cRamo,cUnidadResponsable," + " null cDocumentoHaplicado,nFolioPoliza,'XX' cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion" + " ,fCancelacion,cDescripcionPoliza,cConcepto,cIdUsuarioCaptura,cIdUsuarioRevision,cIdUsuarioAprobacion,cidOrigen,mTotalCargos*(-1)" + " mTotalCargos,mTotalAbonos*(-1)mTotalAbonos,cTipoDocumento,cComentarios,nCambio,nIdCasoOrigen,Periodo13,ADEFAS,nTipoAjuste," + " nFormatoPoliza from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=?";

    final static String CREADETALLEC = "insert into tDocPolizaCancelDetalle  select nFolioDocPoliza,nDocRenglon,nCuenta,nSubCuenta,cEvento,mImporte *(-1) mImporte,cCentroContable," + " 'XX' cTipoPoliza,aEjercicioFiscal,cConcepto,nIdCasoOrigen,Periodo13,ADEFAS,nTipoAjuste,parcial,cCABMS,cCUCOP,cPartida,nIdGrupoEvento," + " nIdSubGrupoEvento,cIdEventoManual,nNumeroEvento from tDocPolizaDetalle with(nolock) where nFolioDocPoliza=?";

    final static String CREAPOLIZAC = "insert into tPoliza select nFolioPoliza,fCreacion,cDescripcionPoliza,mTotalCargo,mTotalAbono,nMes,nCuenta,fAplicacion,nPolizaAutomatica," + " cCentroContable,aEjercicioFiscal,'XX' cTipoPoliza,'DOCPOLIZACANCEL' cTipoDocumento,nFolioDocumento,'E' DocHAplicado,cUsuarioAutorizo" + " from tPoliza with(nolock) where 'DOCPOLIZA'=cTipoDocumento and nFolioDocumento=? and not exists(select * from tPoliza with(nolock) " + " where 'DOCPOLIZACANCEL'=cTipoDocumento and nFolioDocumento=?)";

    final static String DELETEMOV = "delete from tMovimiento where cTipoDocumento=? and cFolioDocumentoMovimiento=-";

    final static String DELDETPC = "delete from tDocPolizaCancelDetalle where nFolioDocPolizaCancel=?";

    final static String DELENCPC = "delete from tDocPolizaCancelEncabezado where nFolioDocPolizaCancel=?";

    final static String DELPOLC = "delete from tPoliza where cTipoDocumento='DOCPOLIZACANCEL' and nFolioDocumento=?";

    final static String UPDTC = "update CG_CASO WITH (ROWLOCK) set ID_TC=13 where ID_CASO=(select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=?)";

    final static String UPDPOL = "update tPoliza WITH (ROWLOCK) set DocHAplicado='E' ,mTotalCargo=0,	mTotalAbono=0 where cTipoDocumento='DOCPOLIZA' and  nFolioDocumento=?";

    final static String ID_CASO = "select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=?";

    final static String DOCPOL = "select nfolioDocPoliza from tDocPolizaEncabezado with(nolock) where nFolioPoliza=? and cCentroContable='-' and cTipoPoliza='+'";

    final static String MAXFDOC = "select MAX(nFolioDocPoliza)+1 from tDocPolizaEncabezado with(rowlock)";

    final static String NFOLIO = "select nFolioPoliza from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=?";

    final static String UPPOLCAN = "update tDocPolizaEncabezado WITH (ROWLOCK) set  cDescripcionPoliza='Póliza Cancelada con la póliza ? '+cDescripcionPoliza,cConcepto='Póliza" + " Cancelada con la póliza ? '+cConcepto,nFolioPolizaCancelacion=?,fCancelacion=convert(date,getDate()) ,cDocumentoHaplicado='C' " + "where nFolioDocPoliza=*";

    final static String GETFECHA = "SELECT case when tm.nMes=DATEPART(MONTH,GETDATE()) then CONVERT(date,GETDATE()) else convert(date,DATEADD(s,-1,DATEADD(mm, " + "DATEDIFF(m,0,'1/'+CONVERT(varchar,tm.nMes)+'/'+CONVERT(varchar,TM.aEjercicioFiscal))+1,0))) end fAplicacion from tMesesContables" + " tm with(nolock) where tm.mesAbierto='S' and cCentroContable='?'";

    final static String GETCC = "select cCentroContable  from tDocPolizaEncabezado te with(nolock) where nFolioDocPoliza=?";

    final static String FOLMOV = "select nfolioPoliza from tMovimiento with(nolock) where cTipoDocumento='DOCPOLIZA' and   cFolioDocumentoMovimiento=?";

    final static String FOLPOL = "select nfolioPoliza from tPoliza with(nolock) where cTipoDocumento='DOCPOLIZA' and  nFolioDocumento=?";

    final static String WFOLPOL = "update tdocpolizaencabezado with(rowlock) set nFolioPoliza=? where nFolioDocPoliza=-";

    final static String CREATPOL = "insert into tPoliza (nFolioPoliza,fCreacion,cDescripcionPoliza,mTotalCargo,mTotalAbono,nMes,nCuenta,fAplicacion,nPolizaAutomatica," + " cCentroContable,aEjercicioFiscal,cTipoPoliza,cTipoDocumento,nFolioDocumento,DocHAplicado,cUsuarioAutorizo) select distinct dpe.nFolioPoliza," + "dpe.fCarga fCreacion,dpe.cDescripcionPoliza,dpe.mTotalCargos mTotalCargo,dpe.mTotalAbonos mTotalAbono,dpe.nMes,NULL nCuenta,dpe.fAplicacion,0 " + "nPolizaAutomatica,dpe.cCentroContable,dpe.aEjercicioFiscal,dpe.cTipoPoliza,'DOCPOLIZA' cTipoDocumento,dpe.nFolioDocPoliza nFolioDocumento," + "dpe.cDocumentoHaplicado DocHAplicado,NULL cUsuarioAutorizo from tDocPolizaEncabezado dpe with(nolock) where dpe.nFolioDocPoliza =?";

    final static String ENCABEZADO2 = "update tdocpolizaEncabezado WITH (ROWLOCK) set ncambio=? where nfolioDocPoliza=-";

    public GestionPoliza() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            conn = TablasManager.getConnection(GestionInterface.ATT_CONEXION);
            conn.setAutoCommit(false);
            HttpSession session = request.getSession(false);
            u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            CO_RESPONSABLE = "'" + u.getLogin() + "'";
            NOMBREUSR = "'" + u.getNombre() + "'";
            id_oper = request.getParameter("id_oper") != null ? Integer.parseInt(request.getParameter("id_oper")) : 0;
            if (id_oper == 0) {
                MOVIMIENTO = request.getParameter("movimiento");
                if (MOVIMIENTO.equals("edicion"))
                    if (retrocedeEdicion(request.getParameter("nfolioPoliza"), request.getParameter("cCentroContable"), request.getParameter("cTipoPoliza"), nfolioD, CO_RESPONSABLE, NOMBREUSR, st, conn)) {
                        commit(conn);
                        mensaje(response, OKEDIT, true);
                    } else {
                        rollback(conn);
                        mensaje(response, ERREDIT, false);
                    }
                else if (MOVIMIENTO.equals("cancelacion"))
                    if (calcelaPoliza(request.getParameter("nfolioPoliza"), request.getParameter("cCentroContable"), request.getParameter("cTipoPoliza"), nfolioD, NOMBREUSR, st, conn, CO_RESPONSABLE)) {
                        commit(conn);
                        mensaje(response, OKCAN, true);
                    } else {
                        rollback(conn);
                        mensaje(response, ERRCAN, false);
                    }
                else if (MOVIMIENTO.equals("cancelacionMAnterior")) {
                    nfolioD = request.getParameter("nfoliodocPoliza");
                    boolean esFirmaElectronica = DocPolizaEncabezadoManager.esAutorizacionFirmaElectronica(conn, nfolioD);
                    if (cancelaPolizaMesAnterior(nfolioD, OKCANA, st, conn, CO_RESPONSABLE, esFirmaElectronica)) {
                        commit(conn);
                        mensaje(response, OKCANA, true);
                    } else {
                        rollback(conn);
                        mensaje(response, ERRCAN, false);
                    }
                } else
                    mensaje(response, ERROROPER, false);
            } else {
                nfolioD = request.getParameter("folioDocumento");
                if (nfolioD == null) {
                    mensaje(response, ERRORDOCPOL, false);
                } else {
                    status = Integer.parseInt(ejecutaQueryrS(ID_OPER.replace("?", nfolioD), st, conn));
                    if (id_oper >= status)
                        if (avanzar(id_oper, NOMBREUSR, CO_RESPONSABLE, nfolioD, st, conn)) {
                            commit(conn);
                            if (id_oper == 2)
                                mensaje(response, "Documento aplicado Contablemente", true);
                            else if (id_oper == 3)
                                mensaje(response, "Documento avanzado a estatus Autorización", true);
                            else if (id_oper == 4)
                                mensaje(response, "Documento avanzado a estatus de Consulta", true);
                        } else {
                            rollback(conn);
                            mensaje(response, "Falló la operación, reintente en un momento", false);
                        }
                    else if (retroceder(id_oper, nfolioD, NOMBREUSR, CO_RESPONSABLE, st, conn)) {
                        commit(conn);
                        if (id_oper == 1)
                            mensaje(response, "Documento regresado correctamente", true);
                        if (id_oper == 2)
                            mensaje(response, AUTOK, true);
                    } else {
                        rollback(conn);
                        mensaje(response, ERRREG, false);
                    }
                }
            }
        } catch (Exception generalException) {
            try {
                mensaje(response, ERROROP, false);
                if (conn != null) {
                    rollback(conn);
                }
            } catch (Exception rollbackException) {
                System.out.println("<<<<Error en rollback>>>>");
                conn = null;
            }
        } finally {
            try {
                if (st != null)
                    st.close();
                if (conn != null) {
                    conn.rollback();
                    conn.close();
                    conn = null;
                }
            } catch (Exception finallyException) {
                finallyException.printStackTrace();
            }
        }
    }

    private static synchronized boolean avanzar(int id_oper, String NOMBREUSR, String CO_RESPONSABLE, String nfolioD, PreparedStatement st, Connection conn) throws Exception {
        if (id_oper == 1)
            ;
        else if (id_oper == 2) {
            if (!avanzaRevision(NOMBREUSR, nfolioD, CO_RESPONSABLE, st, conn))
                return false;
        } else if (id_oper == 3) {
            if (!avanzaAutorizacion(NOMBREUSR, CO_RESPONSABLE, nfolioD, st, conn))
                return false;
        } else if (id_oper == 4) {
            if (!avanzaConsulta(NOMBREUSR, CO_RESPONSABLE, nfolioD, st, conn))
                return false;
        }
        return true;
    }

    private static synchronized boolean retroceder(int id_oper, String nfolioD, String NOMBREUSR, String CO_RESPONSABLE, PreparedStatement st, Connection conn) throws Exception {
        if (id_oper == 1) {
            if (!retrocedeCaptura("E", nfolioD, NOMBREUSR, CO_RESPONSABLE, st, conn))
                return false;
        } else if (id_oper == 2) {
            if (!retrocedeRevision(NOMBREUSR, nfolioD, st, conn))
                return false;
        }
        return true;
    }

    private static synchronized boolean avanzaRevision(String NOMBREUSR, String nfolioD, String CO_RESPONSABLE, PreparedStatement st, Connection conn) throws Exception {
        verificaMovimientos(conn, st, nfolioD);
        if (!ejecutaQuery(CASO_DATO.replace("?", "'true'").replace("-", "8").replace("+", nfolioD), st, conn))
            return false;
        if (!ejecutaQuery(CASO_DATO.replace("?", "'true'").replace("-", "11").replace("+", nfolioD), st, conn))
            return false;
        if (!ejecutaQuery(CASO_DATO.replace("?", NOMBREUSR).replace("-", "4").replace("+", nfolioD), st, conn))
            return false;
        if (!ejecutaQuery(CASO_OPERACION.replace("?", "2").replace("-", REVISION).replace("+", nfolioD), st, conn))
            return false;
        if (!ejecutaQuery(ENCABEZADO2.replace("?", "2").replace("-", nfolioD), st, conn))
            return false;
        if (!aplicaPolizaContablemente(conn, "DOCPOLIZA", "tdocPolizaEncabezado", "tdocPolizaDetalle", "nFolioDocPoliza", nfolioD))
            return false;
        return true;
    }

    private static synchronized boolean avanzaAutorizacion(String NOMBREUSR, String CO_RESPONSABLE, String nfolioD, PreparedStatement st, Connection conn) throws Exception {
        boolean OK;
        OK = ejecutaQuery(ENCABEZADO.replace("?", "3").replace("-", "'S'").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_DATO.replace("?", NOMBREUSR).replace("-", "4").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_OPERACION.replace("?", "3").replace("-", AUTORIZACION).replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(USRREV.replace("?", CO_RESPONSABLE).replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        return true;
    }

    //avanzaautorizacion
    private static synchronized boolean avanzaConsulta(String NOMBREUSR, String CO_RESPONSABLE, String nfolioD, PreparedStatement st, Connection conn) throws Exception {
        boolean OK = true;
        OK = ejecutaQuery(ENCABEZADO.replace("?", "5").replace("-", "'S'").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_DATO.replace("?", NOMBREUSR).replace("-", "4").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_OPERACION.replace("?", "4").replace("-", CONSULTA).replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(USRAUT.replace("?", CO_RESPONSABLE).replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        return OK;
    }

    //avanzaautorizacion
    private static synchronized boolean retrocedeCaptura(String tipoAccion, String nfolioD, String NOMBREUSR, String CO_RESPONSABLE, PreparedStatement st, Connection conn) throws Exception {
        boolean OK = false;
        int ROWS = 0;
        System.out.println("++++++++++++++++++++++++BORRANDO REGISTROS XX ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        ROWS = ejecutaQuerynt(DELDETPC.replace("?", nfolioD), st, conn);
        ROWS = ejecutaQuerynt(DELENCPC.replace("?", nfolioD), st, conn);
        ROWS = ejecutaQuerynt(DELPOLC.replace("?", nfolioD), st, conn);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("++++++++++++++++++++++++COPIANDO POLIZAS++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        ROWS = ejecutaQuerynt(CREAENCABCANCEL.replace("?", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(CREADETALLEC.replace("?", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(CREAPOLIZAC.replace("?", nfolioD).replace("?", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        commit(conn);
        ROWS = ejecutaQuerynt(UPDTC.replace("?", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(UPDPOL.replace("?", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(ENCABEZADO.replace("?", "10").replace("-", "null").replace("+", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(CASO_DATO.replace("?", "''").replace("-", "8").replace("+", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(CASO_DATO.replace("?", "''").replace("-", "11").replace("+", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        ROWS = ejecutaQuerynt(CASO_DATO.replace("?", NOMBREUSR).replace("-", "4").replace("+", nfolioD), st, conn);
        if (ROWS < 1)
            return false;
        if (tipoAccion.equals("E")) {
            ROWS = ejecutaQuerynt(CASO_OPERACION.replace("?", "1").replace("-", CAPTURA).replace("+", nfolioD), st, conn);
            if (ROWS < 1)
                return false;
        }
        OK = ejecutaQuery(USRCAP.replace("?", CO_RESPONSABLE).replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (!aplicaPolizaContablemente(conn, "DOCPOLIZACANCEL", "tdocPolizaCancelEncabezado", "tdocPolizaCancelDetalle", "nFolioDocPolizaCancel", nfolioD))
            return false;
        System.out.println("++++++++++++++++++++++++REINICIANDO DATOS++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        ROWS = ejecutaQuerynt(DELETEMOV.replace("?", "'DOCPOLIZA'").replace("-", nfolioD), st, conn);
        ROWS = ejecutaQuerynt(DELETEMOV.replace("?", "'DOCPOLIZACANCEL'").replace("-", nfolioD), st, conn);
        ROWS = ejecutaQuerynt(DELDETPC.replace("?", nfolioD), st, conn);
        ROWS = ejecutaQuerynt(DELENCPC.replace("?", nfolioD), st, conn);
        ROWS = ejecutaQuerynt(DELPOLC.replace("?", nfolioD), st, conn);
        return true;
    }

    private static synchronized boolean retrocedeRevision(String NOMBREUSR, String nfolioD, PreparedStatement st, Connection conn) throws Exception {
        boolean OK;
        System.out.println("RETROCEDIENDO A REVISION");
        OK = ejecutaQuery(ENCABEZADO.replace("?", "2").replace("-", "'S'").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_DATO.replace("?", NOMBREUSR).replace("-", "4").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_OPERACION.replace("?", "2").replace("-", REVISION).replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(USRREV.replace("?", "''").replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        return true;
    }

    //RETROCEDE REVISION
    private static synchronized boolean retrocedeEdicion(String nFolioPol, String cCentroContable, String cTipoPoliza, String nfolioD, String CO_RESPONSABLE, String NOMBREUSR, PreparedStatement st, Connection conn) throws Exception {
        boolean OK;
        System.out.println("++++++++++++++++++++++++RETROCEDIENDO A EDICION++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        nfolioD = ejecutaQueryrS(DOCPOL.replace("?", nFolioPol).replace("-", cCentroContable).replace("+", cTipoPoliza), st, conn);
        //HttpSession session = request.getSession(false);
        //session.setAttribute(GestionInterface.ATT_CASE, null);
        OK = retrocedeCaptura("E", nfolioD, NOMBREUSR, CO_RESPONSABLE, st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(USRAUT.replace("?", "''").replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(USRREV.replace("?", "''").replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(USRCAP.replace("?", CO_RESPONSABLE).replace("-", nfolioD), st, conn);
        if (!OK)
            return false;
        return true;
    }

    private static synchronized boolean calcelaPoliza(String nFolioPol, String cCentroContable, String cTipoPoliza, String nfolioD, String NOMBREUSR, PreparedStatement st, Connection conn, String CO_RESPONSABLE) throws Exception {
        System.out.println("++++++++++++++++++++++++RETROCEDIENDO A EDICION++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        nfolioD = ejecutaQueryrS(DOCPOL.replace("?", nFolioPol).replace("-", cCentroContable).replace("+", cTipoPoliza), st, conn);
        boolean OK = false;
        OK = retrocedeCaptura("C", nfolioD, NOMBREUSR, CO_RESPONSABLE, st, conn);
        if (!OK)
            return false;
        System.out.println("++++++++++++++++++++++++CAMBIANDO A CANCELACIÓN++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        OK = ejecutaQuery(ENCABEZADO.replace("?", "5").replace("-", "'C'").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        OK = ejecutaQuery(CASO_DATO.replace("?", NOMBREUSR).replace("-", "4").replace("+", nfolioD), st, conn);
        if (!OK)
            return false;
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        return true;
    }

    private static synchronized boolean aplicaPolizaContablemente(Connection conn, String cTipoDocumento, String Tencabezado, String Tdetalle, String nFolioDoc, String nfolioD) throws Exception {
        boolean appCont;
        AccountingEngine accEng = new AccountingEngine();
        appCont = accEng.makeAccountingApplicationWithoutEvent(conn, cTipoDocumento, nfolioD, Tencabezado, Tdetalle, nFolioDoc);
        return appCont;
    }

    private static synchronized boolean cancelaPolizaMesAnterior(String nfolioD, String OKCANA, PreparedStatement st, Connection conn, String Login, boolean esFirmaElectronica) throws Exception {
        String CentroContable = ejecutaQueryrS(GETCC.replace("?", nfolioD), st, conn);
        String Fecha = ejecutaQueryrS(GETFECHA.replace("?", CentroContable), st, conn);
        OKCANA += Fecha;
        System.out.println("Fecha de aplicacion del mes contable y cc abierta: " + Fecha);
        CancelaDocumento canselDocto = new CancelaDocumento(GestionInterface.ATT_CONEXION);
        String mensaje = null;
        Map m = new HashMap();
        mensaje = canselDocto.cancelaDoctoNuevo("POLI-C" + CentroContable + "-" + nfolioD, "DOCPOLIZA", Fecha, m, "", Login, esFirmaElectronica);
        if (mensaje.contains("APLICADO CONTABLEMENTE"))
            return true;
        else
            return false;
    }

    private static synchronized void verificaMovimientos(Connection conn, PreparedStatement st, String nFolioDocPoliZA) throws Exception {
        String FolioPolizaS = "";
        String FolioTPolizaS = "";
        FolioPolizaS = ejecutaQueryrS(FOLMOV.replace("?", nFolioDocPoliZA), st, conn);
        if (!FolioPolizaS.equalsIgnoreCase("")) {
            System.out.println("Actualizando número de poliza");
            ejecutaQuery(ENCABEZADO.replace("?", "10").replace("-", "''").replace("+", nFolioDocPoliZA), st, conn);
            ejecutaQuery(WFOLPOL.replace("?", FolioPolizaS + "").replace("-", nFolioDocPoliZA), st, conn);
            FolioTPolizaS = ejecutaQueryrS(FOLPOL.replace("?", nFolioDocPoliZA), st, conn);
            if (FolioTPolizaS.equalsIgnoreCase("")) {
                System.out.println("Creando Tpoliza");
                ejecutaQuery(CREATPOL.replace("?", nFolioDocPoliZA + ""), st, conn);
            }
            ejecutaQuery(DELETEMOV.replace("?", "'DOCPOLIZA'").replace("-", nFolioDocPoliZA), st, conn);
        }
    }

    private static synchronized void mensaje(HttpServletResponse response, String mensaje, boolean ESTATUS) throws Exception {
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "attachment;filename=mensaje.txt");
        PrintWriter out = null;
        response.setCharacterEncoding("UTF-8");
        out = response.getWriter();
        out.write(ESTATUS + "//" + mensaje.toPath());
        out.flush();
        out.close();
        System.out.println("Mensaje del sistema: " + mensaje);
    }

    private static synchronized void rollback(Connection conn) throws Exception {
        conn.rollback();
        System.out.println("ROLLBACK Cambios regresados a estatus original");
    }

    private static synchronized void commit(Connection conn) throws Exception {
        conn.commit();
        System.out.println("Cambios COMITTEADOS");
    }

    public static synchronized int ejecutaQuerynt(String Query, PreparedStatement st, Connection conn) throws Exception {
        int rows = 0;
        st = conn.prepareStatement(Query);
        System.out.print(Query);
        rows = st.executeUpdate();
        System.out.println(" : " + rows);
        return rows;
    }

    public static synchronized boolean ejecutaQuery(String Query, PreparedStatement st, Connection conn) throws Exception {
        System.out.println(Query);
        st = conn.prepareStatement(Query);
        st.executeUpdate();
        return true;
    }

    public static synchronized String ejecutaQueryrS(String Query, PreparedStatement st, Connection conn) throws Exception {
        String dato = "";
        ResultSet rs = null;
        st = conn.prepareStatement(Query);
        st.executeQuery();
        rs = st.getResultSet();
        while (rs.next()) {
            dato = rs.getString(1);
        }
        return dato;
    }
}
