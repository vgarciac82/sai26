/**
 */
package com.syc.egresos.core.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iccvi
 */
public class EgresoRELACIONGASTOSDetalle extends EgresoDetalle {

    private static final Logger log = LoggerFactory.getLogger(EgresoRELACIONGASTOSDetalle.class);

    private String ejercicioFiscal;

    private int folioRelacionGastos;

    private BigDecimal importeAmortizacionAnticipo = new BigDecimal(0.00d);

    private BigDecimal importeBruto = new BigDecimal(0.00d);

    private BigDecimal importeNeto = new BigDecimal(0.00d);

    private BigDecimal ImporteHospedaje = new BigDecimal(0.00d);

    /**
     * (non-Javadoc)
     *
     * @see com.syc.egresos.core.EgresoDetalle#cargaDetalle(int)
     */
    @Override
    public List<EgresoDetalle> cargaDetalle(int folioRelacionGastos) throws Exception {
        super.init(getJniName());
        List<EgresoDetalle> epdd = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //epdd = PagosFederalizadoManager.cargaDetalle( conn, folioRelacionGastos );
            return epdd;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public EgresoDetalle generaDetalle(Connection conn, EgresoCalendario caledarioMes, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos) throws Exception {
        BigDecimal importe = caledarioMes.getImporteBruto();
        double valor = pcIVA.doubleValue() + 1;
        double bruto = importe.doubleValue() / valor;
        double iva = importe.doubleValue() - bruto;
        double porcentaje = pcIVA.doubleValue() * 100;
        setEp(caledarioMes.getEp());
        setImporteComprometido(caledarioMes.getImporteBruto());
        setImporteNeto(caledarioMes.getImporteBruto());
        setImporteBruto(new BigDecimal(bruto).setScale(2, RoundingMode.HALF_UP));
        setImporteMasIva(caledarioMes.getImporteBruto());
        setImporteIVA(new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP));
        setCapitulo(EPManager.getComponente(caledarioMes.getEp(), "CAPITULO"));
        setImporteRetencion(new BigDecimal(0.00d));
        setImporteIva(new BigDecimal(iva).setScale(2, RoundingMode.HALF_UP));
        setObgt(EPManager.getComponente(caledarioMes.getEp(), "CAPITULO"));
        setMesCalendario(String.valueOf(caledarioMes.getMesPresupuesto()));
        setIdTipoConcepto(caledarioMes.getIdTipoConcepto());
        setEvento("APD_" + EgresoDetalleManager.calculaEvento(conn, encabezado, this));
        setUnidadResponsable(encabezado.getUnidadResponsable());
        setEjercicioFiscal(encabezado.getEjercicioFiscal());
        setFolioRelacionGastos(Integer.parseInt(((EgresoRELACIONGASTOSEncabezado) encabezado).getFolioRelacionGastos()));
        setCentroContable(encabezado.getCentroContable());
        setNumeroMes(Integer.parseInt(encabezado.getMes()));
        setcIdRelacion(((EgresoRELACIONGASTOSEncabezado) encabezado).getIdRelacion());
        setRfc(encabezado.getRfc());
        setcIdEntidadContable("00");
        setIdTipoMovimiento(encabezado.getIdTipoMovimiento());
        if ("PN".equals(caledarioMes.getIdTipoConcepto())) {
            setcTipoPoliza("EG");
        } else {
            setcTipoPoliza("DI");
        }
        return this;
    }

    public EgresoDetalle generaDetalleRG(Connection conn, EgresoCalendarioRG calendarioMes, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos) throws Exception {
        BigDecimal importe = calendarioMes.getImporteBruto();
        double valor = pcIVA.doubleValue() + 1;
        double bruto = importe.doubleValue() / valor;
        double iva = importe.doubleValue() - bruto;
        double porcentaje = pcIVA.doubleValue() * 100;
        setEp(calendarioMes.getEp());
        setImporteComprometido(calendarioMes.getImporteBruto());
        setImporteNeto(calendarioMes.getImporteBruto().subtract(calendarioMes.getMontoRetencion()));
        setImporteBruto(new BigDecimal(bruto).setScale(2, RoundingMode.HALF_UP));
        setImporteMasIva(calendarioMes.getImporteBruto());
        setImporteIVA(new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP));
        setCapitulo(EPManager.getComponente(calendarioMes.getEp(), "CAPITULO"));
        setImporteRetencion(calendarioMes.getMontoRetencion());
        setImporteIva(new BigDecimal(iva).setScale(2, RoundingMode.HALF_UP));
        setObgt(EPManager.getComponente(calendarioMes.getEp(), "CAPITULO"));
        setMesCalendario(String.valueOf(calendarioMes.getMesPresupuesto()));
        setIdTipoConcepto(calendarioMes.getIdTipoConcepto());
        setEvento("APD_" + EgresoDetalleManager.calculaEvento(conn, encabezado, this));
        setUnidadResponsable(encabezado.getUnidadResponsable());
        setEjercicioFiscal(encabezado.getEjercicioFiscal());
        setFolioRelacionGastos(Integer.parseInt(((EgresoRELACIONGASTOSEncabezado) encabezado).getFolioRelacionGastos()));
        setCentroContable(encabezado.getCentroContable());
        setNumeroMes(Integer.parseInt(encabezado.getMes()));
        setcIdRelacion(((EgresoRELACIONGASTOSEncabezado) encabezado).getIdRelacion());
        setRfc(encabezado.getRfc());
        setcIdEntidadContable("00");
        setIdTipoMovimiento(encabezado.getIdTipoMovimiento());
        setImporteISRResico(calendarioMes.getMontoRetencion());
        if ("PN".equals(calendarioMes.getIdTipoConcepto())) {
            setcTipoPoliza("EG");
        } else {
            setcTipoPoliza("DI");
        }
        return this;
    }

    @Override
    public List<EgresoDetalle> generaDetalleRetencion(Connection conn, EgresoEncabezado encabezado, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos) throws Exception {
        List<EgresoDetalle> detalleRetenciones = new ArrayList<EgresoDetalle>();
        for (SaldoMensual saldoMes : calendarioRetencion) {
            EgresoDetalle detalle = renglonNuevo();
            BigDecimal importe = saldoMes.getMontoSaldo();
            double valor = pcIVA.doubleValue() + 1;
            double bruto = importe.doubleValue() / valor;
            double iva = importe.doubleValue() - bruto;
            double porcentaje = pcIVA.doubleValue() * 100;
            detalle.setCentroContable(encabezado.getCentroContable());
            detalle.setEjercicioFiscal(encabezado.getEjercicioFiscal());
            detalle.setMesCalendario(String.valueOf(saldoMes.getMes()));
            detalle.setEp(saldoMes.getEp());
            detalle.setIdTipoConcepto(saldoMes.getIdTipoConcepto());
            detalle.setIdTipoMovimiento(saldoMes.getIdTipoMovimiento());
            detalle.setImporteBruto(new BigDecimal(bruto).setScale(2, RoundingMode.HALF_UP));
            detalle.setImporteMasIva(saldoMes.getMontoSaldo());
            detalle.setCapitulo(EPManager.getComponente(saldoMes.getEp(), "CAPITULO"));
            ((EgresoRELACIONGASTOSDetalle) detalle).setFolioRelacionGastos(Integer.parseInt(((EgresoRELACIONGASTOSEncabezado) encabezado).getFolioRelacionGastos()));
            detalle.setObgt(EPManager.getComponente(saldoMes.getEp(), "CAPITULO"));
            detalle.setRfc(encabezado.getRfc());
            detalle.setEvento("APD_" + EgresoDetalleManager.calculaEvento(conn, encabezado, detalle));
            detalle.setUnidadResponsable(encabezado.getUnidadResponsable());
            detalle.setEjercicioFiscal(encabezado.getEjercicioFiscal());
            BeanUtils.setProperty(detalle, retencion.getComponente(), saldoMes.getMontoSaldo());
            detalle.setCentroContable(encabezado.getCentroContable());
            detalle.setcIdRelacion(((EgresoRELACIONGASTOSEncabezado) encabezado).getIdRelacion());
            detalle.setImporteIVA(new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP));
            detalle.setImporteIva(new BigDecimal(iva).setScale(2, RoundingMode.HALF_UP));
            if ("PN".equals(saldoMes.getIdTipoConcepto())) {
                detalle.setcTipoPoliza("EG");
            } else {
                detalle.setcTipoPoliza("DI");
            }
            detalleRetenciones.add(detalle);
        }
        return detalleRetenciones;
    }

    public String getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public int getFolioRelacionGastos() {
        return folioRelacionGastos;
    }

    public BigDecimal getImporteAmortizacionAnticipo() {
        return importeAmortizacionAnticipo;
    }

    public BigDecimal getImporteBruto() {
        return importeBruto;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    @Override
    public int insertaRenglon(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO trelaciongastosdetalle(aEjercicioFiscal, ALM, altaAlmacen, cCentroContable, ");
        query.append("cEjercicio, cEvento, cIdCuentaContable, cIdEntidadContable, cIdRelacion, ");
        query.append("cMes, cTipoPoliza, EP, ID_TIPO_CONCEPTO, ID_TIPO_MOVIMIENTO, m23IVA, ");
        query.append("m2Millar, m5Millar, mAmortizacionAnticipo, mBruto, mCedular, mCNIC, mComprometido, ");
        query.append("mDevolucion, mFletes, mIMDT, mImporte, mImporteAmortiza, mImporteBruto, mImporteFlete23, ");
        query.append("mImporteFlete4, mImporteHospedaje, mImporteISRLaudos, mImporteIva, mImporteIvaArrenda, ");
        query.append("mImporteIvaHonorarios, mImporteIvaProv, mImporteMasIva, mImporteNegativo, mImporteNeto, ");
        query.append("mImporteObra, mISRArrenda, mISRHonorarios, mISROtros, mIVA, mNeto, mObra5, mPenalizacion, ");
        query.append("mRetencion, mRetImpuestoCedular, mSancion, mTesofe, nCapitulo, nDocRenglon, nFolioPoliza, ");
        query.append("nFolioRELACIONGASTOS, nMes, nPoliza, OBGT, RFC, mimporteISRResico)");
        query.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
        query.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
        query.append("?, ?, ?, ?)");
        PreparedStatement ps = null;
        try {
            int cnt = 1;
            log.trace("Object: {}", query.toString());
            log.trace("Object: {}", "[" + getEjercicioFiscal() + "]");
            log.trace("Object: {}", "[" + getAlm() + "]");
            log.trace("Object: {}", "[" + getAltaAlmacen() + "]");
            log.trace("Object: {}", "[" + getCentroContable() + "]");
            log.trace("Object: {}", "[" + getEjercicioFiscal() + "]");
            log.trace("Object: {}", "[" + getEvento() + "]");
            log.trace("Object: {}", "[" + getIdCuentaContable() + "]");
            log.trace("Object: {}", "[" + getcIdEntidadContable() + "]");
            log.trace("Object: {}", "[" + getcIdRelacion() + "]");
            log.trace("Object: {}", "[" + getMesCalendario() + "]");
            log.trace("Object: {}", "[" + getcTipoPoliza() + "]");
            log.trace("Object: {}", "[" + getEp() + "]");
            log.trace("Object: {}", "[" + getIdTipoConcepto() + "]");
            log.trace("Object: {}", "[" + getIdTipoMovimiento() + "]");
            log.trace("Object: {}", "[" + getImporte23IVA() + "]");
            log.trace("Object: {}", "[" + getImporte2Millar() + "]");
            log.trace("Object: {}", "[" + getImporte5Millar() + "]");
            log.trace("Object: {}", "[" + getImporteAmortizacionAnticipo() + "]");
            log.trace("Object: {}", "[" + getImporteBruto() + "]");
            log.trace("Object: {}", "[" + getImporteCedular() + "]");
            log.trace("Object: {}", "[" + getImporteCNIC() + "]");
            log.trace("Object: {}", "[" + getImporteMasIva() + "]");
            log.trace("Object: {}", "[" + getImporteDevolucion() + "]");
            log.trace("Object: {}", "[" + getImporteFletes() + "]");
            log.trace("Object: {}", "[" + getImporteIMDT() + "]");
            log.trace("Object: {}", "[" + getImporte() + "]");
            log.trace("Object: {}", "[" + getImporteAmortiza() + "]");
            log.trace("Object: {}", "[" + getImporteBruto() + "]");
            log.trace("Object: {}", "[" + getImporteFlete23() + "]");
            log.trace("Object: {}", "[" + getImporteFlete4() + "]");
            log.trace("Object: {}", "[" + getImporteHospedaje() + "]");
            log.trace("Object: {}", "[" + getImporteISRLaudos() + "]");
            log.trace("Object: {}", "[" + getImporteIva() + "]");
            log.trace("Object: {}", "[" + getImporteIvaArrenda() + "]");
            log.trace("Object: {}", "[" + getImporteIvaHonorarios() + "]");
            log.trace("Object: {}", "[" + getImporteIvaProv() + "]");
            log.trace("Object: {}", "[" + getImporteMasIva() + "]");
            log.trace("Object: {}", "[" + getImporteImporteNegativo() + "]");
            log.trace("Object: {}", "[" + getImporteNeto() + "]");
            log.trace("Object: {}", "[" + getImporteObra() + "]");
            log.trace("Object: {}", "[" + getImporteISRArrenda() + "]");
            log.trace("Object: {}", "[" + getImporteISRHonorarios() + "]");
            log.trace("Object: {}", "[" + getImporteISROtros() + "]");
            log.trace("Object: {}", "[" + getImporteIVA() + "]");
            log.trace("Object: {}", "[" + getImporteNeto() + "]");
            log.trace("Object: {}", "[" + getImporteObra5() + "]");
            log.trace("Object: {}", "[" + getImportePenalizacion() + "]");
            log.trace("Object: {}", "[" + getImporteRetencion() + "]");
            log.trace("Object: {}", "[" + getImporteRetImpuestoCedular() + "]");
            log.trace("Object: {}", "[" + getImporteSancion() + "]");
            log.trace("Object: {}", "[" + getImporteTesofe() + "]");
            log.trace("Object: {}", "[" + getCapitulo() + "]");
            log.trace("Object: {}", "[" + getNumeroRenglon() + "]");
            log.trace("Object: {}", "[" + getFolioPoliza() + "]");
            log.trace("Object: {}", "[" + getFolioRelacionGastos() + "]");
            log.trace("Object: {}", "[" + getMesCalendario() + "]");
            log.trace("Object: {}", "[" + getNumeroPoliza() + "]");
            log.trace("Object: {}", "[" + getObgt() + "]");
            log.trace("Object: {}", "[" + getRfc() + "]");
            log.trace("Object: {}", "[" + getImporteISRResico() + "]");
            cnt = 1;
            ps = conn.prepareStatement(query.toString());
            ps.setString(cnt++, getEjercicioFiscal());
            ps.setString(cnt++, getAlm());
            ps.setString(cnt++, getAltaAlmacen());
            ps.setString(cnt++, getCentroContable());
            ps.setString(cnt++, getEjercicioFiscal());
            ps.setString(cnt++, getEvento());
            ps.setString(cnt++, getIdCuentaContable());
            ps.setString(cnt++, getcIdEntidadContable());
            ps.setString(cnt++, getcIdRelacion());
            ps.setString(cnt++, getMesCalendario());
            ps.setString(cnt++, getcTipoPoliza());
            ps.setString(cnt++, getEp());
            ps.setString(cnt++, getIdTipoConcepto());
            ps.setString(cnt++, getIdTipoMovimiento());
            ps.setBigDecimal(cnt++, getImporte23IVA());
            ps.setBigDecimal(cnt++, getImporte2Millar());
            ps.setBigDecimal(cnt++, getImporte5Millar());
            ps.setBigDecimal(cnt++, getImporteAmortizacionAnticipo());
            ps.setBigDecimal(cnt++, getImporteBruto());
            ps.setBigDecimal(cnt++, getImporteCedular());
            ps.setBigDecimal(cnt++, getImporteCNIC());
            ps.setBigDecimal(cnt++, getImporteMasIva());
            ps.setBigDecimal(cnt++, getImporteDevolucion());
            ps.setBigDecimal(cnt++, getImporteFletes());
            ps.setBigDecimal(cnt++, getImporteIMDT());
            ps.setBigDecimal(cnt++, getImporte());
            ps.setBigDecimal(cnt++, getImporteAmortiza());
            ps.setBigDecimal(cnt++, getImporteBruto());
            ps.setBigDecimal(cnt++, getImporteFlete23());
            ps.setBigDecimal(cnt++, getImporteFlete4());
            ps.setBigDecimal(cnt++, getImporteHospedaje());
            ps.setBigDecimal(cnt++, getImporteISRLaudos());
            ps.setBigDecimal(cnt++, getImporteIva());
            ps.setBigDecimal(cnt++, getImporteIvaArrenda());
            ps.setBigDecimal(cnt++, getImporteIvaHonorarios());
            ps.setBigDecimal(cnt++, getImporteIvaProv());
            ps.setBigDecimal(cnt++, getImporteMasIva());
            ps.setBigDecimal(cnt++, getImporteImporteNegativo());
            ps.setBigDecimal(cnt++, getImporteNeto());
            ps.setBigDecimal(cnt++, getImporteObra());
            ps.setBigDecimal(cnt++, getImporteISRArrenda());
            ps.setBigDecimal(cnt++, getImporteISRHonorarios());
            ps.setBigDecimal(cnt++, getImporteISROtros());
            ps.setBigDecimal(cnt++, getImporteIVA());
            ps.setBigDecimal(cnt++, getImporteNeto());
            ps.setBigDecimal(cnt++, getImporteObra5());
            ps.setBigDecimal(cnt++, getImportePenalizacion());
            ps.setBigDecimal(cnt++, getImporteRetencion());
            ps.setBigDecimal(cnt++, getImporteRetImpuestoCedular());
            ps.setBigDecimal(cnt++, getImporteSancion());
            ps.setBigDecimal(cnt++, getImporteTesofe());
            ps.setString(cnt++, getCapitulo());
            ps.setInt(cnt++, getNumeroRenglon());
            ps.setInt(cnt++, getFolioPoliza());
            ps.setInt(cnt++, getFolioRelacionGastos());
            ps.setString(cnt++, getMesCalendario());
            ps.setInt(cnt++, getNumeroPoliza());
            ps.setString(cnt++, getObgt());
            ps.setString(cnt++, getRfc());
            ps.setBigDecimal(cnt++, getImporteISRResico());
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public EgresoDetalle renglonNuevo() {
        EgresoRELACIONGASTOSDetalle renglonNuevo = new EgresoRELACIONGASTOSDetalle();
        renglonNuevo.setNumeroMes(this.getNumeroMes());
        renglonNuevo.setEjercicioFiscal(this.getEjercicioFiscal());
        renglonNuevo.setcIdEntidadContable("00");
        renglonNuevo.setNumeroPoliza(0);
        renglonNuevo.setIdTipoMovimiento(this.getIdTipoMovimiento());
        renglonNuevo.setIdTipoConcepto(this.getIdTipoConcepto());
        renglonNuevo.setCentroContable(getCentroContable());
        renglonNuevo.setRfc(getRfc());
        renglonNuevo.setAlm(getAlm());
        renglonNuevo.setDocumentoAplicado(getDocumentoAplicado());
        renglonNuevo.setFolioPoliza(getFolioPoliza());
        renglonNuevo.setPeriodo13(getPeriodo13());
        renglonNuevo.setAdefas(getAdefas());
        renglonNuevo.setAltaAlmacen(getAltaAlmacen());
        renglonNuevo.setUnidadResponsable(getUnidadResponsable());
        return renglonNuevo;
    }

    public void setEjercicioFiscal(String ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public void setFolioRelacionGastos(int folioRelacionGastos) {
        this.folioRelacionGastos = folioRelacionGastos;
    }

    public void setImporteAmortizacionAnticipo(BigDecimal importeAmortizacionAnticipo) {
        this.importeAmortizacionAnticipo = importeAmortizacionAnticipo;
    }

    public void setImporteBruto(BigDecimal importeBruto) {
        this.importeBruto = importeBruto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }

    public BigDecimal getImporteHospedaje() {
        return ImporteHospedaje;
    }

    public void setImporteHospedaje(BigDecimal importeHospedaje) {
        ImporteHospedaje = importeHospedaje;
    }
}
