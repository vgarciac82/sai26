package com.syc.egresos.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import com.axtel.contratos.core.QuestionnaireAnswer;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.proveedores.dao.ProveedorDAO;
import com.axtel.proveedores.exception.ProveedorException;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.DetallePago;
import com.syc.egresos.PagoCalendarioBussinessLogic;
import com.syc.egresos.firmante.core.FirmanteManager;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.interfaces.CFDIManager;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public abstract class EgresoEncabezado extends DataSourceManager {

    public static final String COMPROMETIDO = "82103";

    public static final String DETAIL = "D";

    public static final String DISPONIBLE_NETO = "82106";

    public static final String HEADER = "H";

    private static final Logger log = LoggerFactory.getLogger(EgresoEncabezado.class);

    private String alm;

    private boolean amortizacionExt;

    private boolean amortizarAnticipoConEscalacion;

    private boolean aplica15D;

    private boolean aplicaImpuestoCedular;

    private String capitulo;

    private String centroContable;

    private String concepto;

    private String contrarecibo;

    private boolean contrarreciboImpreso;

    private String CTAB;

    private String descripcionPoliza;

    private String documentoAplicado;

    private String ejercicioFiscal;

    private int enviadoSICOP;

    private char esFirmaElectronica = 'N';

    private Date fechaAplicacion;

    private Date fechaCancelacion;

    private Date fechaCaptura;

    private Date fechaCarga;

    private Date fechaProgramadaPago;

    private Date fechaRevision;

    private String firmanteAut;

    private String firmanteEla;

    private String firmanteVoBo;

    private int folioPago;

    private int folioPoliza;

    private int folioPolizaCancelacion;

    private String idConcepto;

    private String idDestinoGasto;

    private int idEstatus;

    private String idTipoConcepto;

    private String idTipoDocumento;

    private String idTipoFondo;

    private String idTipoMovimiento;

    private String idTipoOperacion;

    private String idUsuarioAprobacion;

    private String idUsuarioCaptura;

    private String idUsuarioImpresion;

    private String idUsuarioRechazo;

    private String idUsuarioRevision;

    private BigDecimal importeAcumuladoPagar = new BigDecimal(0.0f);

    private BigDecimal importeAmortizacion = new BigDecimal(0.0f);

    private BigDecimal importeAmortizacionAcumulado = new BigDecimal(0.0f);

    private BigDecimal importeAmortizacionAnticipo = new BigDecimal(0.0f);

    private BigDecimal importeBruto;

    private BigDecimal importeDescuento;

    private BigDecimal importeDevolucion = new BigDecimal(0.0f);

    private BigDecimal importeDevolucionAcumulado = new BigDecimal(0.0f);

    private BigDecimal importeIVA;

    private BigDecimal importeMasIva = new BigDecimal(0.0f);

    private BigDecimal importeNeto;

    private BigDecimal importePenalizacion;

    private BigDecimal importeRetencion;

    private BigDecimal importeSaldoAnticipo = new BigDecimal(0.0f);

    private BigDecimal importeSaldoCedula = new BigDecimal(0.0f);

    private BigDecimal importeSancion = new BigDecimal(0.0f);

    private BigDecimal importeSancionAcumulad = new BigDecimal(0.0f);

    private boolean ingresosPropios;

    private String jniName;

    private String login;

    private String mes;

    private String noEstimacion = "0";

    private String noFactura;

    private String Nombre;

    private int numEmpleadoAut;

    private int numEmpleadoElab;

    private int numEmpleadoVoBo;

    private String numPagoAMF;

    private String oficioDiferenciaCambiaria;

    private BigDecimal otrosImpuestos = new BigDecimal(0.0f);

    private BigDecimal porcAmortizacion = new BigDecimal(0.0f);

    private BigDecimal porcentajeIVA = new BigDecimal(0.00f);

    private BigDecimal porcImpuestoCedular;

    private String puestoAut;

    private String puestoEla;

    private String puestoVoBo;

    private List<QuestionnaireAnswer> questionnaireAnswers;

    private boolean radicado;

    private String ramo = "16";

    private String referenciaPRODDER;

    private String rfc;

    private BigDecimal tipoCambio;

    private String tipoPago;

    private String tipoPoliza = "EG";

    private String unidadResponsable;

    private String unidadResponsableContable = "RHQ";

    private int regimenFiscal;

    /**
     * Actualiza el valor del contrarecibo de un pago
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @return Numero de registros actualizados.
     * @throws Exception
     */
    public int actualizaContrarecibo(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	t").append(getTipoPago()).append("Encabezado ");
        query.append("   SET	canocontrarrecibo = ?");
        query.append(" WHERE nFolio").append(getTipoPago()).append(" = ?");
        log.debug("Object: {}", "Query generado para actualizar contrarecibo:\n" + query.toString());
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getContrarecibo());
            ps.setInt(2, getFolioPago());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public int actualizaDestino(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(getTablaEncabezado());
        query.append("   SET ID_TIPO_MOVIMIENTO = ?,");
        query.append("       ID_TIPO_CONCEPTO = ? ");
        query.append(" WHERE ").append(getCampoLlave()).append(" = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getIdTipoMovimiento());
            ps.setString(2, getIdTipoConcepto());
            ps.setInt(3, getFolioPago());
            log.debug("Object: {}", query.toString());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public int actualizaDestinoFed(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(getTablaEncabezado());
        query.append("   SET ID_TIPO_MOVIMIENTO = ?,");
        query.append("       nIdConcepto = ? ");
        query.append(" WHERE ").append(getCampoLlave()).append(" = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getIdTipoMovimiento());
            ps.setString(2, getIdTipoConcepto());
            ps.setInt(3, getFolioPago());
            log.debug("Object: {}", query.toString());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public abstract int actualizaMontosRetencion(Connection conn) throws Exception;

    public abstract int actualizaRetencion(Connection conn, int idTipoRetencion, BigDecimal valorRetencion) throws Exception;

    public abstract int avanzaEstatus(Connection conn) throws Exception;

    public int bitacoraRetEliminada(Connection conn, int idTipoRetencion) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tRetencionEliminada(cTipoPago, nFolioPago, cIdTipoRetencion)");
        query.append("VALUES(?,?,?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getTipoPago());
            ps.setInt(2, getFolioPago());
            ps.setInt(3, idTipoRetencion);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public abstract EgresoEncabezado cargaEncabezado(HttpServletRequest req) throws Exception;

    public abstract EgresoEncabezado cargaEncabezado(int folioEgreso) throws Exception;

    public Firmante consultaFirmante(String tipoFirmante) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return FirmanteManager.cargaFirmante(conn, this, tipoFirmante);
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Firmante consultaFirmanteSuplente(String tipoFirmante) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return FirmanteManager.cargaFirmanteSuplente(conn, this, tipoFirmante);
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public abstract int delete(Connection conn) throws Exception;

    public abstract int eliminaRetencion(Connection conn, int idTipoRetencion) throws Exception;

    public String generaContrarecibo(Connection conn) throws Exception {
        return generaContrarecibo(conn, null);
    }

    public String generaContrarecibo(Connection conn, CFSequenceManager sm) throws EgresoException {
        String cxpPrefijo = "";
        try {
            cxpPrefijo = getPrefijoCR(conn);
            if (sm != null)
                return ContrareciboManager.generaContrarecibo(conn, String.valueOf(getCentroContable()), cxpPrefijo, sm);
            else
                return ContrareciboManager.generaContrarecibo(conn, String.valueOf(getCentroContable()), cxpPrefijo);
        } catch (Exception e) {
            throw new EgresoException(e);
        }
    }

    public abstract int generaRetenciones(Connection conn) throws Exception;

    public File generaSolicitudPago(String reportPath) throws Exception {
        Connection conn = null;
        boolean requiereAnexo = requiereAnexo();
        File anexo = null;
        File solicitudDePago = null;
        try {
            conn = getConnection();
            solicitudDePago = imprimeSolicitudDePago(conn, reportPath);
            if (requiereAnexo) {
                anexo = imprimeAnexo(conn, reportPath);
                Map<String, File> archivos = new HashMap<String, File>();
                archivos.put("Solicitud de Pago.pdf", solicitudDePago);
                archivos.put("Anexo.pdf", anexo);
                return Util.generaZip(archivos);
            }
            return solicitudDePago;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getAlm() {
        return alm;
    }

    public abstract Amortizacion getAmortizacion(Connection conn) throws Exception;

    public String getCampoLlave() {
        return "nFolio" + getTipoPago();
    }

    public String getCapitulo() {
        return capitulo;
    }

    public String getCentroContable() {
        return centroContable;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getContrarecibo() {
        return contrarecibo;
    }

    public String getCTAB() {
        return CTAB;
    }

    public String getDescripcionPoliza() {
        return descripcionPoliza;
    }

    public String getDocumentoAplicado() {
        return documentoAplicado;
    }

    public String getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public int getEnviadoSICOP() {
        return enviadoSICOP;
    }

    public char getEsFirmaElectronica() {
        return esFirmaElectronica;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public Date getFechaCancelacion() {
        return fechaCancelacion;
    }

    public Date getFechaCaptura() {
        return fechaCaptura;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public Date getFechaProgramadaPago() {
        return fechaProgramadaPago;
    }

    public Date getFechaRevision() {
        return this.fechaRevision;
    }

    public String getFirmanteAut() {
        return firmanteAut;
    }

    public String getFirmanteEla() {
        return firmanteEla;
    }

    public String getFirmanteVoBo() {
        return firmanteVoBo;
    }

    public int getFolioApartado(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT nFolioPagoApartado ");
        query.append("  FROM tPagoApartadoEncabezado ");
        query.append(" WHERE cTipoPago = ? ");
        query.append("   AND nFolioPago = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        int folioApartado = 0;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getTipoPago());
            ps.setInt(2, getFolioPago());
            rs = ps.executeQuery();
            if (rs.next()) {
                folioApartado = rs.getInt(1);
            }
            return folioApartado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public int getFolioPago() {
        return folioPago;
    }

    public int getFolioPoliza() {
        return folioPoliza;
    }

    public int getFolioPolizaCancelacion() {
        return folioPolizaCancelacion;
    }

    public String getIdConcepto() {
        return idConcepto;
    }

    public String getIdDestinoGasto() {
        return idDestinoGasto;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public String getIdTipoConcepto() {
        return idTipoConcepto;
    }

    public String getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public String getIdTipoFondo() {
        return idTipoFondo;
    }

    public String getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public String getIdTipoOperacion() {
        return idTipoOperacion;
    }

    public String getIdUsuarioAprobacion() {
        return idUsuarioAprobacion;
    }

    public String getIdUsuarioCaptura() {
        return idUsuarioCaptura;
    }

    public String getIdUsuarioImpresion() {
        return idUsuarioImpresion;
    }

    public String getIdUsuarioRechazo() {
        return idUsuarioRechazo;
    }

    public String getIdUsuarioRevision() {
        return idUsuarioRevision;
    }

    public BigDecimal getImporteAcumuladoPagar() {
        return importeAcumuladoPagar;
    }

    public BigDecimal getImporteAmortizacion() {
        return importeAmortizacion;
    }

    public BigDecimal getImporteAmortizacionAcumulado() {
        return importeAmortizacionAcumulado;
    }

    public BigDecimal getImporteAmortizacionAnticipo() {
        return importeAmortizacionAnticipo;
    }

    public BigDecimal getImporteBruto() {
        return importeBruto;
    }

    public BigDecimal getImporteDescuento() {
        return importeDescuento;
    }

    public BigDecimal getImporteDevolucion() {
        return importeDevolucion;
    }

    public BigDecimal getImporteDevolucionAcumulado() {
        return importeDevolucionAcumulado;
    }

    public BigDecimal getImporteIVA() {
        return importeIVA;
    }

    public BigDecimal getImporteMasIva() {
        return importeMasIva;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public BigDecimal getImportePenalizacion() {
        return importePenalizacion;
    }

    public BigDecimal getImporteRetencion() {
        return importeRetencion;
    }

    public BigDecimal getImporteSaldoAnticipo() {
        return importeSaldoAnticipo;
    }

    public BigDecimal getImporteSaldoCedula() {
        return importeSaldoCedula;
    }

    public BigDecimal getImporteSancion() {
        return importeSancion;
    }

    public BigDecimal getImporteSancionAcumulad() {
        return importeSancionAcumulad;
    }

    public String getJniName() {
        return this.jniName;
    }

    public String getLogin() {
        return login;
    }

    public String getMes() {
        return mes;
    }

    public BigDecimal getMontoAcumuladoPagar() {
        return importeAcumuladoPagar;
    }

    public BigDecimal getMontoAmortizacion() {
        return importeAmortizacion;
    }

    public BigDecimal getMontoAmortizacionAcumulado() {
        return importeAmortizacionAcumulado;
    }

    public BigDecimal getMontoAmortizacionAnticipo() {
        return importeAmortizacionAnticipo;
    }

    public BigDecimal getMontoImporteDevolucion() {
        return importeDevolucion;
    }

    public BigDecimal getMontoImporteDevolucionAcumulado() {
        return importeDevolucionAcumulado;
    }

    public String getNoEstimacion() {
        return noEstimacion;
    }

    public String getNoFactura() {
        return noFactura;
    }

    public String getNombre() {
        return Nombre;
    }

    public abstract String getNombreAnexo();

    public abstract String getNombreSolicitudPago();

    public int getNumEmpleadoAut() {
        return numEmpleadoAut;
    }

    public int getNumEmpleadoElab() {
        return numEmpleadoElab;
    }

    public int getNumEmpleadoVoBo() {
        return numEmpleadoVoBo;
    }

    public String getNumPagoAMF() {
        return numPagoAMF;
    }

    public String getOficioDiferenciaCambiaria() {
        return oficioDiferenciaCambiaria;
    }

    public BigDecimal getOtrosImpuestos() {
        return otrosImpuestos;
    }

    public BigDecimal getPorcAmortizacion() {
        return porcAmortizacion;
    }

    public BigDecimal getPorcentajeIVA() {
        return porcentajeIVA;
    }

    public BigDecimal getPorcImpuestoCedular() {
        return porcImpuestoCedular;
    }

    public abstract String getPrefijoCR(Connection conn) throws Exception;

    public String getPuestoAut() {
        return puestoAut;
    }

    public String getPuestoEla() {
        return puestoEla;
    }

    public String getPuestoVoBo() {
        return puestoVoBo;
    }

    public List<QuestionnaireAnswer> getQuestionnaireAnswers() {
        return this.questionnaireAnswers;
    }

    public String getRamo() {
        return ramo;
    }

    public String getReferenciaPRODDER() {
        return referenciaPRODDER;
    }

    public String getRfc() {
        return rfc;
    }

    public String getTablaDetalle() {
        return "t" + getTipoPago() + "Detalle";
    }

    public String getTablaEncabezado() {
        return "t" + getTipoPago() + "Encabezado";
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public String getTipoPoliza() {
        return tipoPoliza;
    }

    public String getUnidadResponsable() {
        return unidadResponsable;
    }

    public String getUnidadResponsableContable() {
        return unidadResponsableContable;
    }

    private File imprimeAnexo(Connection conn, String reportPath) throws Exception {
        File tmpDir = Util.getTempDir();
        InputStream is = null;
        OutputStream out = null;
        String nombreReporteAnexo = getNombreAnexo();
        StringBuilder condicionAnexo = new StringBuilder(" and caNoContrarrecibo = '").append(getContrarecibo()).append("'");
        File anexoFile = null;
        try {
            anexoFile = File.createTempFile("Anexo", ".pdf", tmpDir);
            is = new FileInputStream(reportPath + File.separatorChar + nombreReporteAnexo);
            out = new FileOutputStream(anexoFile);
            Map<String, Object> parms = new HashMap<String, Object>();
            parms.put("SUBREPORT_DIR", reportPath);
            parms.put("swhere", condicionAnexo.toString());
            JasperRunManager.runReportToPdfStream(is, out, parms, conn);
            out.flush();
            return anexoFile;
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo de entrada: " + e);
                }
            if (out != null)
                try {
                    is.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo de entrada: " + e);
                }
        }
    }

    private File imprimeSolicitudDePago(Connection conn, String reportPath) throws Exception {
        File tmpDir = Util.getTempDir();
        InputStream is = null;
        OutputStream out = null;
        String nombreReporteSP = getNombreSolicitudPago();
        StringBuilder condicionSolicitud = new StringBuilder(" CR.canocontrarrecibo = '").append(getContrarecibo()).append("'");
        File solicitudDePago = null;
        try {
            solicitudDePago = File.createTempFile("SolicitudDePago", ".pdf", tmpDir);
            is = new FileInputStream(reportPath + File.separatorChar + nombreReporteSP);
            out = new FileOutputStream(solicitudDePago);
            Map<String, Object> parms = new HashMap<String, Object>();
            parms.put("SUBREPORT_DIR", reportPath);
            parms.put("whereFolio", condicionSolicitud.toString());
            JasperRunManager.runReportToPdfStream(is, out, parms, conn);
            out.flush();
            return solicitudDePago;
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo de entrada: " + e);
                }
            if (out != null)
                try {
                    is.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo de entrada: " + e);
                }
        }
    }

    public int insertaApartado(Connection conn) throws Exception {
        return EgresosManager.insertaPagoApartado(conn, this);
    }

    public int insertaCalendario(Connection conn, DetallePago renglon, String cuentaOrigen) throws Exception {
        int insertados = 0;
        PagoCalendarioBussinessLogic pcbl = new PagoCalendarioBussinessLogic(jniName);
        if (COMPROMETIDO.equals(cuentaOrigen))
            insertados = pcbl.insertaCalendarioPagoCompromiso(renglon);
        else if (DISPONIBLE_NETO.equals(cuentaOrigen))
            insertados = pcbl.insertaCalendarioPagoDisponible(renglon);
        return insertados;
    }

    public boolean isAmortizacionExt() {
        return amortizacionExt;
    }

    public boolean isAmortizarAnticipoConEscalacion() {
        return amortizarAnticipoConEscalacion;
    }

    /**
     * @return the aplica15D
     */
    public boolean isAplica15D() {
        return aplica15D;
    }

    public boolean isAplicaImpuestoCedular() {
        return aplicaImpuestoCedular;
    }

    public boolean isContrarreciboImpreso() {
        return contrarreciboImpreso;
    }

    public boolean isIngresosPropios() {
        return ingresosPropios;
    }

    public boolean isRadicado() {
        return radicado;
    }

    public void notificaRechazo(Connection conn, String motivoRechazo) throws Exception {
        UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        String login = getIdUsuarioCaptura();
        String subject = "Rechazo de pago: " + getContrarecibo();
        Usuario usuarioNotificar = new Usuario();
        Empleado empleado = new Empleado();
        usuarioNotificar.setLogin(login);
        empleado.setClaveUsuario(login);
        usuarioNotificar = ubl.getUsuario(usuarioNotificar);
        empleado = ebl.getEmpleado(empleado);
        StringBuilder bodymail = new StringBuilder();
        bodymail.append("<html>");
        bodymail.append("<body>");
        bodymail.append("<p>");
        bodymail.append("<b>").append(empleado.getSalutacion()).append(" ").append(empleado.getNombreCompleto()).append("</b><br/>");
        bodymail.append("<b>").append(empleado.getCargo()).append("</b>").append("</b></p><br>");
        bodymail.append("<br>");
        bodymail.append("<p>");
        bodymail.append("Se hace de su conocimiento que el pago con la cuenta por pagar <b>").append(getContrarecibo()).append("</b> ha sido rechazado debido a la(s) siguiente(s) razon(es):");
        bodymail.append("<br>");
        bodymail.append("<I>&quot;");
        bodymail.append(motivoRechazo);
        bodymail.append("&quot;</I><br/>");
        bodymail.append("Por favor tome las medidas necesarias para solventar las observaciones.<br/>");
        bodymail.append("</p>");
        bodymail.append("Notificaciones Automaticas SAI");
        bodymail.append("</body>");
        bodymail.append("</html>");
        AlarmaManager.procesaAlarmaCNF(conn, null, null, null, subject, usuarioNotificar.getU_email(), bodymail.toString());
    }

    public boolean numeroREPSECapturado() throws ProveedorException, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return ProveedorDAO.numeroREPSECapturado(conn, getRfc());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public EgresoEncabezado readFromRequest(HttpServletRequest request, EgresoEncabezado encabezado) throws Exception {
        encabezado.setFechaCaptura(Util.stringToDate(request.getParameter("fechaCaptura"), "dd/MM/yyyy"));
        encabezado.setFechaAplicacion(Util.stringToDate(request.getParameter("fechaAplicacion"), "dd/MM/yyyy"));
        encabezado.setFechaCarga(Util.stringToDate(request.getParameter("fechaCaptura"), "dd/MM/yyyy"));
        encabezado.setRamo(request.getParameter("ramo"));
        encabezado.setUnidadResponsable(request.getParameter("unidadEjecutora"));
        encabezado.setEjercicioFiscal(request.getParameter("ejercicioFiscal"));
        encabezado.setCentroContable(request.getParameter("centroContable"));
        encabezado.setIdTipoDocumento(request.getParameter("idTipoDocumento"));
        encabezado.setRfc(request.getParameter("rfc"));
        encabezado.setFechaProgramadaPago(Util.stringToDate(request.getParameter("fechaAplicacion"), "dd/MM/yyyy"));
        encabezado.setConcepto(new String(StringUtils.trimToEmpty(request.getParameter("concepto")).getBytes("ISO-8859-1"), "UTF-8"));
        encabezado.setImporteBruto(new BigDecimal(request.getParameter("importeBruto")));
        encabezado.setImporteIVA(new BigDecimal(request.getParameter("importeIVA")));
        encabezado.setImporteRetencion(new BigDecimal(StringUtils.trimToNull(request.getParameter("importeRetencion")) == null ? "0.00" : request.getParameter("importeRetencion")));
        encabezado.setImporteDescuento(new BigDecimal(request.getParameter("importeDescuento")));
        encabezado.setOtrosImpuestos(new BigDecimal(request.getParameter("otrosImpuestos")));
        encabezado.setImporteNeto(new BigDecimal(request.getParameter("importeNeto")));
        encabezado.setIdTipoFondo(request.getParameter("idTipoFondo"));
        encabezado.setContrarecibo(request.getParameter("contrarecibo"));
        encabezado.setIdDestinoGasto(request.getParameter("idDestinoGasto"));
        encabezado.setIdTipoMovimiento(request.getParameter("tmovimiento"));
        encabezado.setIdConcepto(request.getParameter("idConcepto"));
        encabezado.setIdTipoConcepto(request.getParameter("idTipoConcepto"));
        encabezado.setIdTipoMovimiento(request.getParameter("idTipoMovimiento"));
        encabezado.setIdEstatus(Integer.parseInt(request.getParameter("nIDEstatus")));
        encabezado.setIdUsuarioCaptura(request.getParameter("idUsuarioCaptura"));
        encabezado.setIdUsuarioRevision(request.getParameter("idUsuarioCaptura"));
        encabezado.setIdUsuarioAprobacion(request.getParameter("idUsuarioAprobacion"));
        encabezado.setNumEmpleadoElab(Integer.parseInt(StringUtils.isBlank(request.getParameter("numEmpleadoElab")) ? "-1" : request.getParameter("numEmpleadoElab")));
        encabezado.setTipoPoliza("EG");
        encabezado.setImportePenalizacion(new BigDecimal(request.getParameter("importePenalizacion") == null ? "0.00" : request.getParameter("importePenalizacion")));
        return encabezado;
    }

    /**
     * Al rechazar un pago se eliminan las facturas capturadas y se actualiza el
     * estatus a -1
     *
     * @param conn
     * @param motivoRechazo
     * @param encabezado
     */
    public abstract void rechazaPago(Connection conn, String motivoRechazo) throws Exception;

    private boolean requiereAnexo() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int nFacturasPago = CFDIManager.getNumeroDeFacturas(conn, this);
            int nClavesPago = EgresosManager.getNumClavesDetalle(conn, this);
            return nFacturasPago > 7 || nClavesPago > 7;
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<Map<String, String>> resumenCalendario(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	EP, mImporteBruto, tipoConcepto, tipoMovimiento ");
        query.append("  FROM	vImportesCalendario WITH(NOLOCK)");
        query.append(" WHERE	cTipoPago = ?");
        query.append("   AND	nFolioPago = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, String>> detalle = new ArrayList<Map<String, String>>();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getTipoPago());
            ps.setInt(2, getFolioPago());
            rs = ps.executeQuery();
            DateConverter converter = new DateConverter(null);
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            while (resultObj != null) {
                detalle.add(resultObj);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public abstract Map<String, String> resumenConcepto(Connection conn) throws Exception;

    public abstract Map<String, String> resumenPago(Connection conn) throws Exception;

    public abstract List<Map<String, String>> resumenRetenciones(Connection conn) throws Exception;

    public abstract boolean retencionEliminable(Connection conn, int idRetencion) throws Exception;

    public abstract int save(Connection conn) throws Exception;

    public void setAlm(String alm) {
        this.alm = alm;
    }

    public void setAmortizacionExt(boolean amortizacionExt) {
        this.amortizacionExt = amortizacionExt;
    }

    public void setAmortizarAnticipoConEscalacion(boolean amortizarAnticipoConEscalacion) {
        this.amortizarAnticipoConEscalacion = amortizarAnticipoConEscalacion;
    }

    public void setAplica15D(boolean aplica15D) {
        this.aplica15D = aplica15D;
    }

    public void setAplicaImpuestoCedular(boolean aplicaImpuestoCedular) {
        this.aplicaImpuestoCedular = aplicaImpuestoCedular;
    }

    public void setCapitulo(String capitulo) {
        this.capitulo = capitulo;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public void setContrarecibo(String contrarecibo) {
        this.contrarecibo = contrarecibo;
    }

    public void setContrarreciboImpreso(boolean contrarreciboImpreso) {
        this.contrarreciboImpreso = contrarreciboImpreso;
    }

    public void setCTAB(String cTAB) {
        CTAB = cTAB;
    }

    public void setDescripcionPoliza(String descripcionPoliza) {
        this.descripcionPoliza = descripcionPoliza;
    }

    public void setDocumentoAplicado(String documentoAplicado) {
        this.documentoAplicado = documentoAplicado;
    }

    public void setEjercicioFiscal(String ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public void setEnviadoSICOP(int enviadoSICOP) {
        this.enviadoSICOP = enviadoSICOP;
    }

    public void setEsFirmaElectronica(char esFirmaElectronica) {
        this.esFirmaElectronica = esFirmaElectronica;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public void setFechaCancelacion(Date fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public void setFechaCaptura(Date fechaCaptura) {
        this.fechaCaptura = fechaCaptura;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public void setFechaProgramadaPago(Date fechaProgramadaPago) {
        this.fechaProgramadaPago = fechaProgramadaPago;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public void setFirmanteAut(String firmanteAut) {
        this.firmanteAut = firmanteAut;
    }

    public void setFirmanteEla(String firmanteEla) {
        this.firmanteEla = firmanteEla;
    }

    public void setFirmanteVoBo(String firmanteVoBo) {
        this.firmanteVoBo = firmanteVoBo;
    }

    public void setFolioPago(int folioPago) {
        this.folioPago = folioPago;
    }

    public void setFolioPoliza(int folioPoliza) {
        this.folioPoliza = folioPoliza;
    }

    public void setFolioPolizaCancelacion(int folioPolizaCancelacion) {
        this.folioPolizaCancelacion = folioPolizaCancelacion;
    }

    public void setIdConcepto(String idConcepto) {
        this.idConcepto = idConcepto;
    }

    public void setIdDestinoGasto(String idDestinoGasto) {
        this.idDestinoGasto = idDestinoGasto;
    }

    private void setIdEstatus(int idEstus) {
        this.idEstatus = idEstus;
    }

    public void setIdTipoConcepto(String idTipoConcepto) {
        this.idTipoConcepto = idTipoConcepto;
    }

    public void setIdTipoDocumento(String idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public void setIdTipoFondo(String idTipoFondo) {
        this.idTipoFondo = idTipoFondo;
    }

    public void setIdTipoMovimiento(String idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public void setIdTipoOperacion(String idTipoOperacion) {
        this.idTipoOperacion = idTipoOperacion;
    }

    public void setIdUsuarioAprobacion(String idUsuarioAprobacion) {
        this.idUsuarioAprobacion = idUsuarioAprobacion;
    }

    public void setIdUsuarioCaptura(String idUsuarioCaptura) {
        this.idUsuarioCaptura = idUsuarioCaptura;
    }

    public void setIdUsuarioImpresion(String idUsuarioImpresion) {
        this.idUsuarioImpresion = idUsuarioImpresion;
    }

    public void setIdUsuarioRechazo(String idUsuarioRechazo) {
        this.idUsuarioRechazo = idUsuarioRechazo;
    }

    public void setIdUsuarioRevision(String idUsuarioRevision) {
        this.idUsuarioRevision = idUsuarioRevision;
    }

    public void setImporteAcumuladoPagar(BigDecimal importeAcumuladoPagar) {
        this.importeAcumuladoPagar = importeAcumuladoPagar;
    }

    public void setImporteAmortizacion(BigDecimal importeAmortizacion) {
        this.importeAmortizacion = importeAmortizacion;
    }

    public void setImporteAmortizacionAcumulado(BigDecimal importeAmortizacionAcumulado) {
        this.importeAmortizacionAcumulado = importeAmortizacionAcumulado;
    }

    public void setImporteAmortizacionAnticipo(BigDecimal importeAmortizacionAnticipo) {
        this.importeAmortizacionAnticipo = importeAmortizacionAnticipo;
    }

    public void setImporteBruto(BigDecimal importeBruto) {
        this.importeBruto = importeBruto;
    }

    public void setImporteDescuento(BigDecimal importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public void setImporteDevolucion(BigDecimal importeDevolucion) {
        this.importeDevolucion = importeDevolucion;
    }

    public void setImporteDevolucionAcumulado(BigDecimal importeDevolucionAcumulado) {
        this.importeDevolucionAcumulado = importeDevolucionAcumulado;
    }

    public void setImporteIVA(BigDecimal importeIVA) {
        this.importeIVA = importeIVA;
    }

    public void setImporteMasIva(BigDecimal importeMasIva) {
        this.importeMasIva = importeMasIva;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }

    public void setImportePenalizacion(BigDecimal importePenalizacion) {
        this.importePenalizacion = importePenalizacion;
    }

    public void setImporteRetencion(BigDecimal importeRetencion) {
        this.importeRetencion = importeRetencion;
    }

    public void setImporteSaldoAnticipo(BigDecimal importeSaldoAnticipo) {
        this.importeSaldoAnticipo = importeSaldoAnticipo;
    }

    public void setImporteSaldoCedula(BigDecimal importeSaldoCedula) {
        this.importeSaldoCedula = importeSaldoCedula;
    }

    public void setImporteSancion(BigDecimal importeSancion) {
        this.importeSancion = importeSancion;
    }

    public void setImporteSancionAcumulad(BigDecimal importeSancionAcumulad) {
        this.importeSancionAcumulad = importeSancionAcumulad;
    }

    public void setIngresosPropios(boolean ingresosPropios) {
        this.ingresosPropios = ingresosPropios;
    }

    public void setJniName(String jniName) {
        this.jniName = jniName;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public void setMontoAcumuladoPagar(BigDecimal montoAcumuladoPagar) {
        this.importeAcumuladoPagar = montoAcumuladoPagar;
    }

    public void setMontoAmortizacion(BigDecimal montoAmortizacion) {
        this.importeAmortizacion = montoAmortizacion;
    }

    public void setMontoAmortizacionAcumulado(BigDecimal montoAmortizacionAcumulado) {
        this.importeAmortizacionAcumulado = montoAmortizacionAcumulado;
    }

    public void setMontoAmortizacionAnticipo(BigDecimal montoAmortizacionAnticipo) {
        this.importeAmortizacionAnticipo = montoAmortizacionAnticipo;
    }

    public void setMontoImporteDevolucion(BigDecimal montoImporteDevolucion) {
        this.importeDevolucion = montoImporteDevolucion;
    }

    public void setMontoImporteDevolucionAcumulado(BigDecimal montoImporteDevolucionAcumulado) {
        this.importeDevolucionAcumulado = montoImporteDevolucionAcumulado;
    }

    public void setNoEstimacion(String noEstimacion) {
        this.noEstimacion = noEstimacion;
    }

    public void setNoFactura(String noFactura) {
        this.noFactura = noFactura;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setNumEmpleadoAut(int numEmpleadoAut) {
        this.numEmpleadoAut = numEmpleadoAut;
    }

    public void setNumEmpleadoElab(int numEmpleadoElab) {
        this.numEmpleadoElab = numEmpleadoElab;
    }

    public void setNumEmpleadoVoBo(int numEmpleadoVoBo) {
        this.numEmpleadoVoBo = numEmpleadoVoBo;
    }

    public void setNumPagoAMF(String numPagoAMF) {
        this.numPagoAMF = numPagoAMF;
    }

    public void setOficioDiferenciaCambiaria(String oficioDiferenciaCambiaria) {
        this.oficioDiferenciaCambiaria = oficioDiferenciaCambiaria;
    }

    public void setOtrosImpuestos(BigDecimal otrosImpuestos) {
        this.otrosImpuestos = otrosImpuestos;
    }

    public void setPorcAmortizacion(BigDecimal porcAmortizacion) {
        this.porcAmortizacion = porcAmortizacion;
    }

    public void setPorcentajeIVA(BigDecimal porcentajeIVA) {
        this.porcentajeIVA = porcentajeIVA;
    }

    public void setPorcImpuestoCedular(BigDecimal porcImpuestoCedular) {
        this.porcImpuestoCedular = porcImpuestoCedular;
    }

    public void setPuestoAut(String puestoAut) {
        this.puestoAut = puestoAut;
    }

    public void setPuestoEla(String puestoEla) {
        this.puestoEla = puestoEla;
    }

    public void setPuestoVoBo(String puestoVoBo) {
        this.puestoVoBo = puestoVoBo;
    }

    public void setQuestionnaireAnswers(List<QuestionnaireAnswer> answers) {
        this.questionnaireAnswers = answers;
    }

    public void setRadicado(boolean radicado) {
        this.radicado = radicado;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public void setReferenciaPRODDER(String referenciaPRODDER) {
        this.referenciaPRODDER = referenciaPRODDER;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public void setTipoPoliza(String tipoPoliza) {
        this.tipoPoliza = tipoPoliza;
    }

    public void setUnidadResponsable(String unidadResponsable) {
        this.unidadResponsable = unidadResponsable;
    }

    public void setUnidadResponsableContable(String unidadResponsableContable) {
        this.unidadResponsableContable = unidadResponsableContable;
    }

    public boolean isQuestionaireAnswered() throws EgresoException {
        Connection conn = null;
        try {
            conn = getConnection();
            return FirmaElectronicaManager.isQuestionnaireAnswered(conn, this);
        } catch (SQLException | FirmaElectronicaException e) {
            log.error(e.getMessage(), e);
            throw new EgresoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int getRegimenFiscal() {
        return regimenFiscal;
    }

    public void setRegimenFiscal(int regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public abstract List<EgresoExcedeUMA> validaTopeUMASUnidad(Connection conn, String rfc2) throws Exception;
}
