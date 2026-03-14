package com.syc.solicitudviaticos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.servlet.RegistroViaticosServlet;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SolicitudViaticosBusinessLogic extends DataSourceManager implements TipoCasoInterface {

    private static Logger log = LoggerFactory.getLogger(SolicitudViaticosBusinessLogic.class);

    private static final int AVANZA_AUTORIZACION = 2;

    public static final String SI_AUTORIZA = "Si";

    public static final String NO_AUTORIZA = "No";

    public static final int CAPTURA = 1;

    public static final int ESPERA_AUTORIZACION = 2;

    public static final int AUTORIZADA = 3;

    public static final int RECHAZADA = 4;

    public static final int DESCARTADA = 5;

    public static final int FIRMANTE_CAPTURA = 0;

    public static final int FIRMANTE_AUTORIZA = 1;

    public static final int FIRMANTE_RECHAZA = 2;

    public SolicitudViaticosBusinessLogic() {
        super.init(GestionInterface.ATT_CONEXION);
    }

    public SolicitudViaticosBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void creaViaticoTransporte(HttpServletRequest req) throws Exception {
        BigDecimal monto = new BigDecimal(req.getParameter("monto"));
        int tipoTransporte = Integer.parseInt(req.getParameter("tipoTransporte"));
        int folioSolicitud = Integer.parseInt(req.getParameter("folioViatico"));
        int noKm = Integer.parseInt(req.getParameter("noKm"));
        String origen = req.getParameter("origen");
        String placas = req.getParameter("placas");
        String tieneVales = req.getParameter("tieneVales");
        Connection conn = null;
        try {
            conn = getConnection();
            SolicitudViaticosManager.creaViaticoTransporte(conn, monto, tipoTransporte, folioSolicitud, noKm, origen, placas, tieneVales);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error dando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void creaViatico(HttpServletRequest req) throws Exception {
        int folioViatico = Integer.parseInt(req.getParameter("folioViatico"));
        String fechaCaptura = req.getParameter("fechaCaptura");
        String loginCaptura = req.getParameter("loginCaptura");
        String numeroEmpleadoBeneficiario = req.getParameter("numeroEmpleadoBeneficiario");
        String esSolicitudPropia = req.getParameter("esSolicitudPropia");
        String cUnidadEjecutora = req.getParameter("cUnidadEjecutora");
        String email = req.getParameter("email");
        String cuentaBanco = req.getParameter("dCuentaEmpleado");
        Connection conn = null;
        try {
            conn = getConnection();
            SolicitudViaticosManager.creaViatico(conn, folioViatico, fechaCaptura, loginCaptura, numeroEmpleadoBeneficiario, esSolicitudPropia, cUnidadEjecutora, email, cuentaBanco);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error dando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void creaFirmantes(HttpServletRequest req) throws Exception {
        int folioViatico = Integer.parseInt(req.getParameter("folioViatico"));
        Connection conn = null;
        try {
            conn = getConnection();
            SolicitudViaticosManager.creaFirmantes(conn, folioViatico);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error dando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void creaViaticoAgenda(HttpServletRequest req) throws Exception {
        int folioSolicitud = Integer.parseInt(req.getParameter("folioViatico"));
        String fInicio = req.getParameter("fInicio");
        String fFin = req.getParameter("fFin");
        String lEsAnticipado = req.getParameter("lEsAnticipado");
        String lEsNacional = req.getParameter("lEsNacional");
        int nIdPais = Integer.parseInt(req.getParameter("nIdPais"));
        int ID_ESTADO = Integer.parseInt(req.getParameter("ID_ESTADO"));
        int ID_MUNICIPIO = Integer.parseInt(req.getParameter("ID_MUNICIPIO"));
        String cLocalidad = req.getParameter("cLocalidad");
        String cMotivo = req.getParameter("cMotivo");
        BigDecimal mCuotaDia = new BigDecimal(req.getParameter("mCuotaDia"));
        int nIDPaquete = Integer.parseInt(req.getParameter("nIDPaquete"));
        int nIDHomologacion = Integer.parseInt(req.getParameter("nIDHomologacion"));
        String cIDTipoMoneda = req.getParameter("cIDTipoMoneda");
        String cActividadesAgenda = req.getParameter("cActividadesAgenda");
        Connection conn = null;
        try {
            conn = getConnection();
            SolicitudViaticosManager.creaViaticoAgenda(conn, folioSolicitud, fInicio, fFin, lEsAnticipado, lEsNacional, nIdPais, ID_ESTADO, ID_MUNICIPIO, cLocalidad, cMotivo, mCuotaDia, nIDPaquete, nIDHomologacion, cIDTipoMoneda, cActividadesAgenda);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error dando rollback: " + e2);
                }
            throw e;
        }
    }

    public boolean buscaPorExpediente(Caso c, String u_login) {
        log.debug("Llamado a buscaPorExpediente sin implementar");
        return false;
    }

    public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException {
        log.debug("Llamado a onIniciaCaso sin implementar");
    }

    public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
        log.debug("Llamado a onCreateExpediente sin implementar");
    }

    public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
        log.debug("Llamado a onRecibeDocumento sin implementar");
    }

    public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        log.debug("Llamado a onEjecutaCaso sin implementar");
    }

    public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        log.info("Object: {}", String.format("Inicia manejo de evento onAvanzaCaso Folio[%s] id:[%d] Usuario:[%s] IDCasoOper:[%d]", c.getFolio(), c.getIdCaso(), u_login, id_caso_oper));
        int nFolioSolicitudViaticos = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        if (id_caso_oper == AVANZA_AUTORIZACION) {
            try {
                notificaSolicitudAutorizacion(conn, c, nFolioSolicitudViaticos, 2);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new SQLException(e);
            }
        }
    }

    private void notificaSolicitudAutorizacion(Connection conn, Caso c, int folioSolicitud, int nDocRenglon) throws Exception {
        notificaSolicitudAutorizacion(conn, c, folioSolicitud, nDocRenglon, true);
    }

    private void notificaSolicitudAutorizacion(Connection conn, Caso c, int folioSolicitud, int nDocRenglon, boolean primerAutorizacion) throws Exception {
        NotificacionSolicitudViaticos notificacion = null;
        if (primerAutorizacion) {
            notificacion = SolicitudViaticosManager.getPrimerAutorizacion(conn, folioSolicitud);
            SolicitudViaticosManager.cambiaEstatusSolicitud(conn, folioSolicitud, ESPERA_AUTORIZACION);
        } else
            notificacion = SolicitudViaticosManager.selectNotificacionSolicitudViaticos(conn, folioSolicitud, nDocRenglon);
        if (notificacion != null) {
            AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, "Solicitud de Autorizacion de Viaticos", notificacion.getAutorizadorCorreo(), notificacion.generaHTMLNotificacion(folioSolicitud));
        } else
            throw new Exception("No se encotnro destinatario de notificacion para el tramite: " + folioSolicitud);
    }

    private void notificaSolicitudRechazo(Connection conn, NotificacionSolicitudViaticos notificacion, Caso c) throws Exception {
        AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, "Solicitud de Viaticos Rechazada", notificacion.getSolicitanteCorreo(), notificacion.generaHTMLRechazo(notificacion.getFolioSolicitudViaticos()));
    }

    public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, @SuppressWarnings("rawtypes") Map data) throws SQLException {
        int folioSolicitud = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        try {
            SolicitudViaticosManager.cambiaEstatusSolicitud(conn, folioSolicitud, DESCARTADA);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        }
    }

    public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException {
        log.debug("Llamado a onVenceCaso sin implementar");
    }

    public void direccionaSolAutorizacion(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> datosToken = NotificacionSolicitudViaticos.consumeToken(request);
            HttpSession session = request.getSession(false);
            if (session == null)
                session = request.getSession(true);
            for (Iterator<String> i = datosToken.keySet().iterator(); i.hasNext(); ) {
                String campo = i.next();
                String valor = datosToken.get(campo);
                session.setAttribute(campo, valor);
            }
            if (!SolicitudViaticosManager.notifiacionAtendida(conn, datosToken)) {
                // HTTP
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                // 1.1.
                // HTTP 1.0.
                response.setHeader("Pragma", "no-cache");
                // Proxies.
                response.setHeader("Expires", "0");
                response.sendRedirect("../Generador/ResumenViaticos.jsp");
            } else {
                session.setAttribute("tipoRespuesta", "2");
                session.setAttribute("mensaje", "Se autorizo anteriormente la solicitud. De click en aceptar para cerrar esta ventana.");
                response.sendRedirect("../Generador/RespuestaViaticos.jsp");
            }
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void registraAutorizacion(HttpServletRequest req, HttpServletResponse response) {
        HttpSession session = req.getSession(false);
        if (session == null)
            session = req.getSession(true);
        try {
            int folioSolicitud = Integer.parseInt(req.getParameter("nFolioSolicitudViaticos"));
            String empleadoAutoriza = req.getParameter("empleadoAutoriza");
            int IdRenglonAutorizador = Integer.parseInt(req.getParameter("IdRenglonAutorizador"));
            String autorizacion = req.getParameter("AutorizoRB");
            String motivoRechazo = req.getParameter("motivoRechazo");
            boolean ultimaAutorizacion = registraAutorizacion(folioSolicitud, empleadoAutoriza, IdRenglonAutorizador, autorizacion, motivoRechazo);
            session.setAttribute("tipoRespuesta", "1");
            session.setAttribute("mensaje", "Se autorizo correctamente la solicitud. De click en aceptar para cerrar esta ventana.");
            response.sendRedirect("../Generador/RespuestaViaticos.jsp");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                session.setAttribute("tipoRespuesta", "3");
                session.setAttribute("mensaje", "No se pudo completar la operacion debido al siguiente error:\n" + e + "\n Notifique al administrador del sistema.");
                response.sendRedirect("../Generador/RespuestaViaticos.jsp");
            } catch (Exception e3) {
                log.warn("Object: {}", "Problemas enviando respuesta " + e3);
            }
        }
    }

    private boolean registraAutorizacion(int folioSolicitud, String empleadoAutoriza, int idRenglonAutorizador, String autorizacion, String motivoRechazo) throws Exception {
        Connection conn = null;
        boolean ultimaAutorizacion = false;
        try {
            AccountingEngine ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            log.info("Object: {}", "Autorizadn Solicitud: " + folioSolicitud + " No. Empleado Autorizador: " + empleadoAutoriza);
            conn = getConnection();
            Caso casoSolViaticos = SolicitudViaticosManager.buscaCaso(conn, folioSolicitud);
            Usuario u = SolicitudViaticosManager.getUsuarioCaptura(conn, folioSolicitud);
            NotificacionSolicitudViaticos autorizacionObj = SolicitudViaticosManager.selectNotificacionSolicitudViaticos(conn, folioSolicitud, idRenglonAutorizador);
            autorizacionObj.setAutoriza(SI_AUTORIZA.equalsIgnoreCase(autorizacion) ? 1 : 0);
            autorizacionObj.setMotivoRechazo(StringUtils.trimToNull(motivoRechazo));
            if (SI_AUTORIZA.equalsIgnoreCase(autorizacion)) {
                autorizacionObj.setAutoriza(FIRMANTE_AUTORIZA);
                SolicitudViaticosManager.actualizaEstausAutorizacion(conn, autorizacionObj);
                NotificacionSolicitudViaticos autorizacionSiguiente = SolicitudViaticosManager.selectNotificacionSolicitudViaticos(conn, folioSolicitud, idRenglonAutorizador + 1);
                if (autorizacionSiguiente.getIdRenglonAutorizador() <= 0) {
                    SolicitudViaticosManager.finalizaSolicitud(conn, u, casoSolViaticos);
                    int idComision = SolicitudViaticosManager.creaComision(conn, folioSolicitud);
                    Caso casoCaja = SolicitudViaticosManager.creaSolicitudCaja(conn, folioSolicitud, idComision, u);
                    int nFolioCaja = Integer.parseInt(casoCaja.getFolio().substring(casoCaja.getFolio().lastIndexOf('-') + 1));
                    ae.makeAccountingApplication(conn, "CAJA", String.valueOf(nFolioCaja), "tCajaEncabezado", "tCajaDetalle", "nFolioCaja");
                    SolicitudViaticosManager.avanzaTramiteCaja(conn, u, casoCaja);
                    SolicitudViaticosManager.autorizaTramiteCaja(conn, u, casoCaja);
                    ultimaAutorizacion = true;
                    autorizacionSiguiente.setFolioSNP(nFolioCaja);
                    notificaAutorizacion(conn, casoSolViaticos, u, autorizacionSiguiente, idComision, casoCaja, nFolioCaja);
                    SolicitudViaticosManager.cambiaEstatusSolicitud(conn, folioSolicitud, AUTORIZADA);
                } else {
                    notificaSolicitudAutorizacion(conn, casoSolViaticos, folioSolicitud, idRenglonAutorizador + 1, false);
                }
            } else {
                autorizacionObj.setAutoriza(FIRMANTE_RECHAZA);
                autorizacionObj.setMotivoRechazo(motivoRechazo);
                SolicitudViaticosManager.actualizaEstausAutorizacion(conn, autorizacionObj);
                SolicitudViaticosManager.finalizaSolicitud(conn, u, casoSolViaticos);
                notificaSolicitudRechazo(conn, autorizacionObj, casoSolViaticos);
                SolicitudViaticosManager.cambiaEstatusSolicitud(conn, folioSolicitud, RECHAZADA);
            }
            conn.commit();
            return ultimaAutorizacion;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problema en rollback:" + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void notificaAutorizacion(Connection conn, Caso c, Usuario u, NotificacionSolicitudViaticos notificacionSolicitud, int idComision, Caso casoCaja, int nFolioCaja) throws Exception {
        /* Notifica correos a los interasados, adjunta sol. de caja */
        File solicitud = generaSolicitudAutorizacion(conn, idComision, casoCaja, nFolioCaja);
        String correoSolicitante = notificacionSolicitud.getSolicitanteCorreo();
        String usuarioCreadorCorreo = u.getU_email();
        AlarmaManager.procesaAlarmaAttachmentCNF(conn, "", c.getCasoOperacion(0), c, "Solicitud de Viaticos Autorizada", correoSolicitante + (correoSolicitante.equalsIgnoreCase(usuarioCreadorCorreo) ? "" : ";" + usuarioCreadorCorreo), notificacionSolicitud.generaHTMLAutorizacion(), solicitud, false);
    }

    private File generaSolicitudAutorizacion(Connection conn, int idComision, Caso casoCaja, int nFolioCaja) throws Exception {
        File dirOutput = new File(System.getProperty("java.io.tmpdir"));
        File reportParentDir = new File(RegistroViaticosServlet.REPORT_DIR);
        String fileName = casoCaja.getFolio().replaceAll("\\s", "") + ".pdf";
        String reportName = "SolicitudPolizaCaja.jasper";
        File solicitud = new File(dirOutput, fileName);
        File reporteIn = new File(reportParentDir, reportName);
        String whereFolio = "and ce.nfoliocaja=" + nFolioCaja;
        OutputStream out = null;
        InputStream in = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("whereFolio", whereFolio);
            params.put("SUBREPORT_DIR", RegistroViaticosServlet.SUBREPORT_DIR);
            in = new FileInputStream(reporteIn);
            out = new FileOutputStream(solicitud);
            JasperRunManager.runReportToPdfStream(in, out, params, conn);
            out.flush();
            return solicitud;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo: " + e);
                }
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo: " + e);
                }
        }
    }

    public void onSolicitaFirmaElectronica(Caso c, Usuario u, String reportPath) throws SQLException {
    }
}
