package com.syc.gestion;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.TablasManager;
import com.syc.gestion.core.TipoCaso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class TablasBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(TablasBusinessLogic.class);

    public TablasBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<ArrayList<String>> getTablas(String[] campos) {
        Connection conn = null;
        ArrayList<ArrayList<String>> retVal = null;
        try {
            conn = getConnection();
            retVal = TablasManager.ExecSp(conn, campos);
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int insertPolizaDetalle(String campos) {
        Connection conn = null;
        int rows = 0;
        try {
            conn = getConnection();
            rows = TablasManager.insertaPolizaEventosDetalle(conn, campos);
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rows;
    }

    public int actualizaEncabezado(String campos) {
        Connection conn = null;
        int rows = 0;
        try {
            conn = getConnection();
            rows = TablasManager.actualizaEncabezado(conn, campos);
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rows;
    }

    public ArrayList<String> getDetalleDocPoliza(String nFolioDocPolizaDetalle) {
        Connection conn = null;
        ArrayList<String> data = null;
        try {
            conn = getConnection();
            data = TablasManager.selectDetalleDocPoliza(conn, nFolioDocPolizaDetalle);
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return data;
    }

    public String[] getDatosDocPoliza(String nFolioDocPoliza) {
        Connection conn = null;
        String[] data = null;
        try {
            conn = getConnection();
            data = TablasManager.ExecSpDatosPoliza(conn, nFolioDocPoliza);
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return data;
    }

    public String getCancelaDocPoliza(String nFolioDocPoliza, HttpServletRequest request, HttpServletResponse response) {
        Connection conn = null;
        HttpSession session = request.getSession(false);
        String msj = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        TipoCaso tc;
        String id_caso = "";
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            conn = cbl.getConnection();
            conn.setAutoCommit(false);
            id_caso = TablasManager.ejecutaQueryrS(conn, "select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=" + nFolioDocPoliza);
            //log.debug("select nIdCasoOrigen from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza="+nFolioDocPoliza);
        } catch (Exception e) {
            log.debug("Error al crear la conexión");
        }
        if (c == null) {
            //response.sendRedirect("../index.jsp");
            //return;
            //System.out.println("AQUIII en Get CANCELA DOC POL");
            try {
                TablasManager.ejecutaQuery(conn, "update tDocPolizaEncabezado set cDocumentoHaplicado=null where nFolioDocPoliza=" + nFolioDocPoliza);
                //log.debug("update tDocPolizaEncabezado set cDocumentoHaplicado=null where nFolioDocPoliza="+nFolioDocPoliza);
                TablasManager.ejecutaQuery(conn, "update CG_CASO_DATO set CD_VALOR='' where ID_CD in(8,11) and ID_CASO=" + id_caso);
                //log.debug("update CG_CASO_DATO set CD_VALOR='' where ID_CD in(8,11) and ID_CASO="+id_caso);
                TablasManager.ejecutaQuery(conn, "update tPoliza set DocHAplicado='E' where cTipoDocumento='DOCPOLIZA' and nFolioDocumento=" + nFolioDocPoliza);
                //log.debug("update tPoliza set DocHAplicado='E' where cTipoDocumento='DOCPOLIZA' and nFolioDocumento="+nFolioDocPoliza);
                //243
                CasoBusinessLogic cblo = new CasoBusinessLogic("jdbc/gestion");
                c = cblo.getCaso(Integer.parseInt(id_caso));
                //33879
            } catch (Exception e) {
                log.debug("Error al crear el caso");
            }
        }
        c.setIdTC(41);
        c.getTipoCaso().setGavetaAsociada("DOCPOLIZACANCEL");
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        try {
            String[] Querys = new String[3];
            Querys[0] = " insert into tdocpolizaCancelEncabezado select nFolioDocPoliza,fCarga,fAplicacion,cCentroContable,cRamo,cUnidadResponsable," + " cDocumentoHaplicado,nFolioPoliza,'XX' cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion" + " ,fCancelacion,cDescripcionPoliza,cConcepto,cIdUsuarioCaptura,cIdUsuarioRevision,cIdUsuarioAprobacion,cidOrigen,mTotalCargos*(-1)" + " mTotalCargos,mTotalAbonos*(-1)mTotalAbonos,cTipoDocumento,cComentarios,nCambio,nIdCasoOrigen,Periodo13,ADEFAS,nTipoAjuste," + " nFormatoPoliza from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza=" + nFolioDocPoliza;
            Querys[1] = "insert into tDocPolizaCancelDetalle  select nFolioDocPoliza,nDocRenglon,nCuenta,nSubCuenta,cEvento,mImporte *(-1) mImporte,cCentroContable," + " 'XX' cTipoPoliza,aEjercicioFiscal,cConcepto,nIdCasoOrigen,Periodo13,ADEFAS,nTipoAjuste,parcial,cCABMS,cCUCOP,cPartida,nIdGrupoEvento," + " nIdSubGrupoEvento,cIdEventoManual,nNumeroEvento from tDocPolizaDetalle with(nolock) where nFolioDocPoliza=" + nFolioDocPoliza;
            //and cCentroContable='10'
            Querys[2] = "insert into tPoliza select nFolioPoliza,fCreacion,cDescripcionPoliza,mTotalCargo,mTotalAbono,nMes,nCuenta,fAplicacion,nPolizaAutomatica," + " cCentroContable,aEjercicioFiscal,'XX' cTipoPoliza,'DOCPOLIZACANCEL' cTipoDocumento,nFolioDocumento,DocHAplicado,cUsuarioAutorizo" + " from tPoliza with(nolock) where 'DOCPOLIZA'=cTipoDocumento and nFolioDocumento=" + nFolioDocPoliza + " and not exists(select * from tPoliza with(nolock) " + " where 'DOCPOLIZACANCEL'=cTipoDocumento and nFolioDocumento=" + nFolioDocPoliza + "  )";
            boolean error = false;
            // log.debug("++++++++++++++++++++++++COPIANDO POLIZAS++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < Querys.length; i++) {
                error = TablasManager.ejecutaQuery(conn, Querys[i]);
                //log.debug(i+" QUERY EJECTUTAD0 >> "+Querys[i]+ " << Status: " +!error);
            }
            //log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = request.getSession().getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                Querys = new String[12];
                Querys[0] = "delete from tMovimiento where cTipoDocumento in('DOCPOLIZACANCEL','DOCPOLIZA') and cFolioDocumentoMovimiento=" + nFolioDocPoliza;
                //Querys[1]="delete from tMovimiento where cTipoDocumento= and cFolioDocumentoMovimiento="+nFolioDocPoliza;
                Querys[1] = "delete from tDocPolizaCancelDetalle where nFolioDocPolizaCancel=" + nFolioDocPoliza;
                Querys[2] = "delete from tDocPolizaDetalle where nFolioDocPoliza=" + nFolioDocPoliza;
                Querys[3] = "delete from tDocPolizaCancelEncabezado where nFolioDocPolizaCancel=" + nFolioDocPoliza;
                Querys[4] = "delete from tPoliza where cTipoDocumento='DOCPOLIZACANCEL' and nFolioDocumento=" + nFolioDocPoliza;
                Querys[5] = "UPDATE CG_CASO_OPERACION SET ID_OPER=1,CO_RESPONSABLE='CAPTURA_POLIZA' WHERE ID_CASO=" + id_caso;
                Querys[6] = "update tDocPolizaEncabezado set mTotalCargos=0,mTotalAbonos=0,cDocumentoHaplicado='C', nCambio=10 where nFolioDocPoliza=" + nFolioDocPoliza;
                Querys[7] = "update tPoliza  set DocHAplicado='E' ,mTotalCargo=0,	mTotalAbono=0 where cTipoDocumento='DOCPOLIZA' and  nFolioDocumento=" + nFolioDocPoliza;
                Querys[8] = "update CG_CASO_DATO set CD_VALOR='CAPTURA_POLIZA' where ID_CD=4 and ID_CASO=" + id_caso;
                Querys[9] = "update CG_CASO_DATO set CD_VALOR='' where ID_CD=8 and ID_CASO=" + id_caso;
                Querys[10] = "Update CG_CASO SET C_STATUS=1 WHERE ID_CASO=" + id_caso;
                //Querys[11]="update tDocPolizaEncabezado set ncambio=10 where nFolioDocPoliza="+nFolioDocPoliza;
                // Querys[12]="update tDocPolizaEncabezado set cDocumentoHaplicado=null where nFolioDocPoliza="+nFolioDocPoliza;
                Querys[11] = "update CG_CASO set ID_TC=13 where ID_CASO=" + id_caso;
                //log.debug("++++++++++BORRANDO REGISTROS TEMPORALES Y ACTTUALIZANDO DOCUMENTO++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                for (int i = 0; i < Querys.length; i++) {
                    error = TablasManager.ejecutaQuery(conn, Querys[i]);
                    //log.debug(i+" QUERY EJECTUTAD0>> "+Querys[i]+ " << Status: "+!error);
                }
                //log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
            if (!error) {
                conn.commit();
                msj = "Revisión guardada correctamente";
            } else {
                conn.rollback();
                log.debug("*ERROR RECHAZANDO TODAS LAS TRANSACCIONES*");
                msj = "Error al regresar el documento a Captura";
                new Exception("Error al crear Poliza copia");
            }
            c.setIdTC(13);
            c.getTipoCaso().setGavetaAsociada("POLIZA");
            session.setAttribute(GestionInterface.ATT_CASE, c);
            conn.setAutoCommit(true);
            Caso sc = new Caso();
            sc.setIdCaso(c.getIdCaso());
            c = CasoManager.select(conn, sc);
            log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return msj;
    }

    public String getNfoliodocPoliza(String nFolioPoliza, String cCentroContable, String cTipoPoliza) {
        Connection conn = null;
        String nFolioDocPoliza = "";
        try {
            conn = getConnection();
            nFolioDocPoliza = TablasManager.getNfolioDocumento(conn, nFolioPoliza, cCentroContable, cTipoPoliza);
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
        } finally {
            log.debug("Object: {}", "***************Obtenido el folio del documento " + nFolioDocPoliza + " con Folio de Poliza:" + nFolioPoliza + " , centro contable:" + cCentroContable + " y tipo de Poliza:" + cTipoPoliza + " ");
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return nFolioDocPoliza;
    }
}
