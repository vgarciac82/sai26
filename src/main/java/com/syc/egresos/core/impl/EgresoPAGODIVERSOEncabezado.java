package com.syc.egresos.core.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.axtel.egresos.exceptions.EgresoException;
import com.syc.adquisiciones.core.RecepcionMaterial;
import com.syc.adquisiciones.manager.RecepcionMaterialManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.PagosDiversosManager;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EgresoPAGODIVERSOEncabezado extends EgresoEncabezado {

    private static final Logger log = LoggerFactory.getLogger(EgresoPAGODIVERSOEncabezado.class);

    private boolean cargaMasiva;

    private boolean esIngresoFiscal;

    private boolean esRelacionGastos;

    private String folioPagoDiverso;

    private String idContrato;

    private String idEstadoEstimacion;

    private String idRecepMat;

    private long idTransactionWS;

    private BigDecimal importePenalizacion = new BigDecimal(0.0f);

    private int numeroAcompanantes;

    private String observaciones;

    private boolean pagoReferenciado;

    private boolean pagoTiendaDigital;

    StringBuilder queryIns = new StringBuilder();

    private String referenciaBancaria;

    {
        queryIns.append("INSERT INTO	tPAGODIVERSOEncabezado(");
        queryIns.append("			cFolioPAGODIVERSO,");
        queryIns.append("			nFolioPAGODIVERSO,");
        queryIns.append("			caNoContrarrecibo,");
        queryIns.append("			rfc,");
        queryIns.append("			nombre,");
        queryIns.append("			cNoEstimacion,");
        queryIns.append("			cIdTipoOperacion,");
        queryIns.append("			fAplicacion,");
        queryIns.append("			cNoFactura,");
        queryIns.append("			mImporteBruto,");
        queryIns.append("			mImporteSancion,");
        queryIns.append("			mImporteDevolucion,");
        queryIns.append("			mAmortizacionAnticipo,");
        queryIns.append("			mImporteIVA,");
        queryIns.append("			mImporteRetencion,");
        queryIns.append("			mImportePenalizacion,");
        queryIns.append("			mImporteNeto,");
        queryIns.append("			nPorcAmortizacion,");
        queryIns.append("			cIdEstadoEstimacion,");
        queryIns.append("			lContrarreciboImpreso,");
        queryIns.append("			cConcepto,");
        queryIns.append("			lAmortizarAnticipoConEscalacion,");
        queryIns.append("			nIdConcepto,");
        queryIns.append("			fProgramadaPago,");
        queryIns.append("			nTipoCambio,");
        queryIns.append("			cIdUsuarioCaptura,");
        queryIns.append("			cIdUsuarioImpresion,");
        queryIns.append("			cIdUsuarioRevision,");
        queryIns.append("			cIdUsuarioAprobacion,");
        queryIns.append("			cIdUsuarioRechazo,");
        queryIns.append("			ID_DESTINO_GASTO,");
        queryIns.append("			ID_TIPO_OPERACION,");
        queryIns.append("			ID_TIPO_MOVIMIENTO,");
        queryIns.append("			ID_TIPO_FONDO,");
        queryIns.append("			cUnidadResponsable,");
        queryIns.append("			cRamo,");
        queryIns.append("			cIdTipoDocumento,");
        queryIns.append("			mImporteMasIva,");
        queryIns.append("			mAmortizacion,");
        queryIns.append("			mSaldoCedula,");
        queryIns.append("			mAcumuladoxpagar,");
        queryIns.append("			mSaldoAnticipo,");
        queryIns.append("			cCentroContable,");
        queryIns.append("			cMes,");
        queryIns.append("			aEjercicioFiscal,");
        queryIns.append("			mAmortizacionAcumulado,");
        queryIns.append("			mImporteSancionAcumulado,");
        queryIns.append("			mImporteDevolucionAcumulado,");
        queryIns.append("			cTipoPoliza,");
        queryIns.append("			NumPagoAMF,");
        queryIns.append("			CTAB,");
        queryIns.append("			cIdRecepMat,");
        queryIns.append("			cEsRelacionGastos,");
        queryIns.append("			mOtrosImpuestos,");
        queryIns.append("			nNumEmpleadoElab,");
        queryIns.append("			nNumEmpleadoVoBo,");
        queryIns.append("			nNumEmpleadoAut,");
        queryIns.append("			cEsFirmaElectronica,");
        queryIns.append("			cEsAmortizacionExt,");
        queryIns.append("			cPagoTiendaDigital,");
        queryIns.append("			cAplica15D,");
        queryIns.append("			cDescripcionPoliza, ");
        queryIns.append("			esCargaMasiva");
        queryIns.append("		)");
        queryIns.append("VALUES	(");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		LTRIM(RTRIM(?)),");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		null,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		0,");
        queryIns.append("		0,");
        queryIns.append("		0,");
        queryIns.append("		?,");
        queryIns.append("		0,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		0,");
        queryIns.append("		?,");
        queryIns.append("		0,");
        queryIns.append("		0,");
        queryIns.append("		0,");
        queryIns.append("		0,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		0,");
        queryIns.append("		0,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?,");
        queryIns.append("		?)");
    }

    public EgresoPAGODIVERSOEncabezado() {
        setTipoPago("PAGODIVERSO");
    }

    @Override
    public int actualizaMontosRetencion(Connection conn) throws Exception {
        throw new Exception("Funcionalidad actualizaMontosRetencion no implementada.");
    }

    @Override
    public int actualizaRetencion(Connection conn, int idTipoRetencion, BigDecimal valorRetencion) throws Exception {
        throw new Exception("Funcionalidad actualizaRetencion no implementada.");
    }

    @Override
    public int avanzaEstatus(Connection conn) throws Exception {
        RecepcionMaterialManager.changeStatus(conn, getIdContrato(), getIdRecepMat(), 3);
        return 1;
    }

    @Override
    public EgresoEncabezado cargaEncabezado(HttpServletRequest req) throws Exception {
        throw new Exception("No se ha implementado el metodo con parametro request");
    }

    @Override
    public EgresoEncabezado cargaEncabezado(int folioEgreso) throws Exception {
        super.init(getJniName());
        EgresoPAGODIVERSOEncabezado epde = null;
        Connection conn = null;
        try {
            conn = getConnection();
            epde = PagosDiversosManager.cargaEncabezado(conn, folioEgreso);
            return epde;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public int delete(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("DELETE ");
        query.append("  FROM	tPagoDiversoEncabezado ");
        query.append(" WHERE	nFolioPagoDiverso = ? ");
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", "Iniciando eliminacion del pago directo: " + getFolioPagoDiverso());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getFolioPagoDiverso());
            afectados = ps.executeUpdate();
            log.trace("Object: {}", "Se eliminaron : " + afectados + " pagos con el folio: " + getFolioPagoDiverso());
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int eliminaRetencion(Connection conn, int idTipoRetencion) throws Exception {
        throw new Exception("Funcionalidad eliminaRetencion no implementada.");
    }

    @Override
    public int generaRetenciones(Connection conn) throws Exception {
        throw new Exception("Funcionalidad generaRetenciones no implementada.");
    }

    @Override
    public Amortizacion getAmortizacion(Connection conn) throws Exception {
        return EgresosManager.getAmortizacion(conn, this);
    }

    public String getFolioPagoDiverso() {
        return folioPagoDiverso;
    }

    public String getIdContrato() {
        return idContrato;
    }

    public String getIdEstadoEstimacion() {
        return idEstadoEstimacion;
    }

    public String getIdRecepMat() {
        return idRecepMat;
    }

    public long getIdTransactionWS() {
        return idTransactionWS;
    }

    public BigDecimal getImportePenalizacion() {
        return importePenalizacion;
    }

    @Override
    public String getNombreAnexo() {
        throw new RuntimeException("getNombreAnexo No implementado");
    }

    @Override
    public String getNombreSolicitudPago() {
        throw new RuntimeException("getNombreSolicitudPago no implementado");
    }

    public int getNumeroAcompanantes() {
        return numeroAcompanantes;
    }

    public String getObservaciones() {
        return observaciones;
    }

    @Override
    public String getPrefijoCR(Connection conn) throws Exception {
        String cxpPrefijo = ConfiguraAplicativoManager.getSystemSetting(conn, "CXP_PREFIJO");
        return cxpPrefijo;
    }

    public String getReferenciaBancaria() {
        return referenciaBancaria;
    }

    public boolean isCargaMasiva() {
        return cargaMasiva;
    }

    public boolean isEsIngresoFiscal() {
        return esIngresoFiscal;
    }

    public boolean isEsRelacionGastos() {
        return esRelacionGastos;
    }

    public boolean isPagoReferenciado() {
        return pagoReferenciado;
    }

    public boolean isPagoTiendaDigital() {
        return pagoTiendaDigital;
    }

    @Override
    public void rechazaPago(Connection conn, String motivoRechazo) throws Exception {
        throw new RuntimeException("rechazaPago No implementado");
    }

    @Override
    public Map<String, String> resumenConcepto(Connection conn) throws Exception {
        throw new Exception("No implementado");
    }

    @Override
    public Map<String, String> resumenPago(Connection conn) throws Exception {
        throw new Exception("Funcionalidad resumenPago no implementada.");
    }

    @Override
    public List<Map<String, String>> resumenRetenciones(Connection conn) throws Exception {
        throw new Exception("Funcionalidad resumenRetenciones no implementada.");
    }

    @Override
    public boolean retencionEliminable(Connection conn, int idRetencion) throws Exception {
        throw new RuntimeException("retencionEliminable No Implementado.");
    }

    @Override
    public int save(Connection conn) throws EgresoException {
        PreparedStatement ps = null;
        try {
            int param = 1;
            ps = conn.prepareStatement(queryIns.toString());
            ps.setString(param++, getFolioPagoDiverso());
            ps.setInt(param++, getFolioPago());
            ps.setString(param++, getContrarecibo());
            ps.setString(param++, getRfc());
            ps.setString(param++, getNombre());
            ps.setString(param++, getNoEstimacion());
            ps.setDate(param++, Util.toSQLDate(getFechaAplicacion()));
            ps.setString(param++, getNoFactura());
            ps.setBigDecimal(param++, getImporteBruto());
            ps.setBigDecimal(param++, getImporteSancion());
            ps.setBigDecimal(param++, getImporteDevolucion());
            ps.setBigDecimal(param++, getImporteAmortizacionAnticipo());
            ps.setBigDecimal(param++, getImporteIVA());
            ps.setBigDecimal(param++, getImporteRetencion());
            ps.setBigDecimal(param++, getImportePenalizacion());
            ps.setBigDecimal(param++, getImporteNeto());
            ps.setString(param++, getConcepto());
            ps.setString(param++, getIdConcepto());
            ps.setDate(param++, Util.toSQLDate(getFechaProgramadaPago()));
            ps.setString(param++, getIdUsuarioCaptura());
            ps.setString(param++, getIdDestinoGasto());
            ps.setString(param++, getIdTipoOperacion());
            ps.setString(param++, getUnidadResponsable());
            ps.setString(param++, getRamo());
            ps.setString(param++, getIdTipoDocumento());
            ps.setBigDecimal(param++, getImporteMasIva());
            ps.setBigDecimal(param++, getImporteAmortizacion());
            ps.setBigDecimal(param++, getImporteSaldoCedula());
            ps.setBigDecimal(param++, getImporteAcumuladoPagar());
            ps.setBigDecimal(param++, getImporteSaldoAnticipo());
            ps.setString(param++, getCentroContable());
            ps.setString(param++, getMes());
            ps.setString(param++, getEjercicioFiscal());
            ps.setBigDecimal(param++, getImporteAmortizacionAcumulado());
            ps.setBigDecimal(param++, getImporteSancionAcumulad());
            ps.setBigDecimal(param++, getImporteDevolucionAcumulado());
            ps.setString(param++, getTipoPoliza());
            ps.setString(param++, getNumPagoAMF());
            ps.setString(param++, getCTAB());
            ps.setString(param++, getIdRecepMat());
            ps.setString(param++, isEsRelacionGastos() ? "S" : "N");
            ps.setBigDecimal(param++, getOtrosImpuestos());
            ps.setInt(param++, getNumEmpleadoElab());
            ps.setInt(param++, getNumEmpleadoVoBo());
            ps.setInt(param++, getNumEmpleadoAut());
            ps.setString(param++, String.valueOf(getEsFirmaElectronica()));
            ps.setString(param++, isAmortizacionExt() ? "S" : "N");
            ps.setString(param++, isPagoTiendaDigital() ? "S" : "N");
            ps.setString(param++, isAplica15D() ? "S" : "N");
            ps.setString(param++, getDescripcionPoliza());
            ps.setString(param++, isCargaMasiva() ? "S" : "N");
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new EgresoException(e);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public void setCargaMasiva(boolean cargaMasiva) {
        this.cargaMasiva = cargaMasiva;
    }

    public void setEsIngresoFiscal(boolean esIngresoFiscal) {
        this.esIngresoFiscal = esIngresoFiscal;
    }

    public void setEsRelacionGastos(boolean esRelacionGastos) {
        this.esRelacionGastos = esRelacionGastos;
    }

    public void setFolioPagoDiverso(String folioPagoDiverso) {
        this.folioPagoDiverso = folioPagoDiverso;
    }

    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    public void setIdEstadoEstimacion(String idEstadoEstimacion) {
        this.idEstadoEstimacion = idEstadoEstimacion;
    }

    public void setIdRecepMat(String idRecepMat) {
        this.idRecepMat = idRecepMat;
    }

    public void setIdTransactionWS(long idTransactionWS) {
        this.idTransactionWS = idTransactionWS;
    }

    public void setImportePenalizacion(BigDecimal importePenalizacion) {
        this.importePenalizacion = importePenalizacion;
    }

    public void setNumeroAcompanantes(int numeroAcompanantes) {
        this.numeroAcompanantes = numeroAcompanantes;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setPagoReferenciado(boolean pagoReferenciado) {
        this.pagoReferenciado = pagoReferenciado;
    }

    public void setPagoTiendaDigital(boolean pagoTiendaDigital) {
        this.pagoTiendaDigital = pagoTiendaDigital;
    }

    public void setReferenciaBancaria(String referenciaBancaria) {
        this.referenciaBancaria = referenciaBancaria;
    }

    @Override
    public List<EgresoExcedeUMA> validaTopeUMASUnidad(Connection conn, String rfc2) {
        return new ArrayList<EgresoExcedeUMA>();
    }
}
