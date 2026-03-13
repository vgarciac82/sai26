package com.syc.egresos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.core.FacturaManager;
import com.syc.contable.AccountingEngine;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.LogCancelaDevengadoManager;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.PasivoDiferidoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelacionGastosMasivaBussinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(RelacionGastosMasivaBussinessLogic.class);

    public RelacionGastosMasivaBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public List<RelacionGastosEncabezado> getIntegradasResumen(int folioIntegracion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return RelacionGastosManager.getIntegradasResumen(conn, folioIntegracion);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void cancelaCargaMasiva(Usuario u, int folioCargaMasiva) throws Exception {
        Connection conn = null;
        String documento = "RELACIONGASTOS";
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            List<RelacionGastosEncabezado> rgs = RelacionGastosManager.getIntegradasResumen(conn, folioCargaMasiva);
            for (RelacionGastosEncabezado rg : rgs) {
                log.info(String.format("Cancelando RG [%s] aplicada por firma electronica.", rg.getCaNoContrarrecibo()));
                RelacionGastosManager.cancelaRelacionGastos(conn, accEng, rg);
                PasivoDiferidoManager.cancelarPasivoDiferido(conn, documento, String.valueOf(rg.getnFolioRELACIONGASTOS()));
                LogCancelaDevengadoManager.registraLog(conn, documento, rg.getnFolioRELACIONGASTOS(), u.getLogin());
                FacturaManager.eliminaFacturas(conn, documento, String.valueOf(rg.getnFolioRELACIONGASTOS()));
                RelacionGastosManager.updateEliminaInfoVuelosRG(conn, rg.getnFolioRELACIONGASTOS());
                RelacionGastosManager.actualizaEstatus(conn, rg, SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
            }
            actualizaEstatus(conn, folioCargaMasiva, SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error realizando rollback en RelacionGastosMasivaBussinessLogic.cancelaCargaMasiva: " + e2.toString());
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private int actualizaEstatus(Connection conn, int folioCargaMasiva, int status) throws Exception {
        String query = "UPDATE tRelacionGastosEncabezado_temp SET cDocumentoHAplicado = 'C', nEnviadoSICOP = ? WHERE folioTempGral = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, status);
            ps.setInt(2, folioCargaMasiva);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public void notificaCancelacion(int folioMasivo, SolicitudFirmaElectronica sfe) throws Exception {
        Connection conn = null;
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        StringBuilder body = new StringBuilder("");
        body.append("<b>C. %s</b>");
        body.append("<br>");
        body.append("<b>%s</b>");
        body.append("<br>");
        body.append("<br>");
        body.append("<p>Se hace de su conocimiento que su solicitud masiva ");
        body.append("de Relacion de Gastos con folio <b>%d</b>");
        body.append("fue cancelada.");
        body.append("</p>");
        body.append("<br>");
        body.append("Por favor tome las medidas pertinentes.");
        body.append("<br><br>");
        body.append("Notificaciones Automaticas SAI");
        body.append("<br><br>");
        body.append("%s");
        try {
            conn = getConnection();
            FirmaElectronicaManager.registraBitacoraCancelacion(conn, "Rechazo de Carga Masiva", sfe);
            String usuarioGenera = RelacionGastosManager.getLoginInicia(conn, folioMasivo);
            if (!StringUtils.isBlank(usuarioGenera)) {
                Usuario u = new Usuario();
                Empleado e = new Empleado();
                u.setLogin(usuarioGenera);
                e.setClaveUsuario(usuarioGenera);
                u = UsuarioManager.select(conn, u);
                e = ebl.getEmpleado(e);
                String cuerpoCorreo = String.format(body.toString(), u.getNombre(), e.getCargo(), folioMasivo, Util.getToday());
                String correo = u.getU_email();
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud Cancelada", correo, cuerpoCorreo);
            }
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getFoliosIntegradosStr(int folioIntegracion) throws Exception {
        Connection conn = null;
        String folios = "";
        try {
            conn = getConnection();
            String token = "";
            List<RelacionGastosEncabezado> rgs = RelacionGastosManager.getIntegradasResumen(conn, folioIntegracion);
            for (RelacionGastosEncabezado rg : rgs) {
                folios = folios + token + rg.getnFolioRELACIONGASTOS();
                token = ",";
            }
            return folios;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
