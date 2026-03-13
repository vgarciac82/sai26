package com.syc.egresos.core;


import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import com.syc.contable.core.SaldoMensual;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;


public abstract class EgresoDetalle extends DataSourceManager {

	private char		adefas						= 'N';
	private String		alm							= "";
	private String		altaAlmacen					= "||";
	private String		capitulo;
	private String		centroContable;
	private String		cIdEntidadContable;
	private String		cIdRelacion;
	private String		cTipoPoliza;
	private String		documentoAplicado;
	private String		ejercicioFiscal;
	private String		ep;
	private String		evento;
	private int			folioPoliza;
	private String		idCuentaContable			= "";
	private String		idTipoConcepto;
	private String		idTipoMovimiento;
	private BigDecimal	importe						= new BigDecimal( 0.00d );
	private BigDecimal	importe23IVA				= new BigDecimal( 0.00d );
	private BigDecimal	importe2Millar				= new BigDecimal( 0.00d );
	private BigDecimal	importe5Millar				= new BigDecimal( 0.00d );
	private BigDecimal	importeAmortiza				= new BigDecimal( 0.00d );
	private BigDecimal	importeAmortizacionAnticipo	= new BigDecimal( 0.00d );
	private BigDecimal	importeBruto				= new BigDecimal( 0.00d );
	private BigDecimal	importeCedular				= new BigDecimal( 0.00d );
	private BigDecimal	importeCNIC					= new BigDecimal( 0.00d );
	private BigDecimal	importeComprometido			= new BigDecimal( 0.00d );
	private BigDecimal	importeDevolucion			= new BigDecimal( 0.00d );
	private BigDecimal	importeFlete23				= new BigDecimal( 0.00d );
	private BigDecimal	importeFlete4				= new BigDecimal( 0.00d );
	private BigDecimal	importeFletes				= new BigDecimal( 0.00d );
	private BigDecimal	importeIMDT					= new BigDecimal( 0.00d );
	private BigDecimal	importeImporteNegativo		= new BigDecimal( 0.00d );
	private BigDecimal	importeISRArrenda			= new BigDecimal( 0.00d );
	private BigDecimal	importeISRHonorarios		= new BigDecimal( 0.00d );
	private BigDecimal	importeISRLaudos			= new BigDecimal( 0.00d );
	private BigDecimal	importeISROtros				= new BigDecimal( 0.00d );
	private BigDecimal	importeISRResico			= new BigDecimal( 0.00d );
	private BigDecimal	importeIva					= new BigDecimal( 0.00d );
	private BigDecimal	importeIVA					= new BigDecimal( 0.00d );
	private BigDecimal	importeIva6					= new BigDecimal( 0.0d );
	private BigDecimal	importeIvaArrenda			= new BigDecimal( 0.00d );
	private BigDecimal	importeIvaHonorarios		= new BigDecimal( 0.00d );
	private BigDecimal	importeIvaProv				= new BigDecimal( 0.00d );
	private BigDecimal	importeMasIva				= new BigDecimal( 0.00d );
	private BigDecimal	importeNeto					= new BigDecimal( 0.00d );
	private BigDecimal	importeObra					= new BigDecimal( 0.00d );
	private BigDecimal	importeObra5				= new BigDecimal( 0.00d );
	private BigDecimal	importeOtrosImpuestos		= new BigDecimal( 0.00d );
	private BigDecimal	importePenalizacion			= new BigDecimal( 0.00d );
	private BigDecimal	importeRetencion			= new BigDecimal( 0.00d );
	private BigDecimal	importeRetImpuestoCedular	= new BigDecimal( 0.00d );
	private BigDecimal	importeSancion				= new BigDecimal( 0.00d );
	private BigDecimal	importeTesofe				= new BigDecimal( 0.00d );
	private BigDecimal	iva;
	private String		jniName;
	private String		mesCalendario;
	private int			numeroMes;
	private int			numeroPoliza;
	private int			numeroRenglon;
	private String		obgt;
	private char		periodo13					= 'N';
	private String		rfc;
	private String		unidadResponsable;

	public abstract List<EgresoDetalle> cargaDetalle( int folioEgreso ) throws Exception;

	public abstract EgresoDetalle generaDetalle( Connection conn, EgresoCalendario importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception;

	public abstract List<EgresoDetalle> generaDetalleRetencion( Connection conn, EgresoEncabezado encabezado, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception;

	public abstract EgresoDetalle generaDetalleRG( Connection conn, EgresoCalendarioRG importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception;

	public char getAdefas() {
		return adefas;
	}

	public String getAlm() {
		return alm;
	}

	public String getAltaAlmacen() {
		return altaAlmacen;
	}

	public String getCapitulo() {
		return capitulo;
	}

	public String getCentroContable() {
		return centroContable;
	}

	public String getcIdEntidadContable() {
		return cIdEntidadContable;
	}

	public String getcIdRelacion() {
		return cIdRelacion;
	}

	public String getcTipoPoliza() {
		return cTipoPoliza;
	}

	public String getDocumentoAplicado() {
		return documentoAplicado;
	}

	public String getEjercicioFiscal() {
		return ejercicioFiscal;
	}

	public String getEp() {
		return ep;
	}

	public String getEvento() {
		return evento;
	}

	public int getFolioPoliza() {
		return folioPoliza;
	}

	public String getIdCuentaContable() {
		return idCuentaContable;
	}

	public String getIdTipoConcepto() {
		return idTipoConcepto;
	}

	public String getIdTipoMovimiento() {
		return idTipoMovimiento;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public BigDecimal getImporte23IVA() {
		return importe23IVA;
	}

	public BigDecimal getImporte2Millar() {
		return importe2Millar;
	}

	public BigDecimal getImporte5Millar() {
		return importe5Millar;
	}

	public BigDecimal getImporteAmortiza() {
		return importeAmortiza;
	}

	public BigDecimal getImporteAmortizacionAnticipo() {
		return importeAmortizacionAnticipo;
	}

	public BigDecimal getImporteBruto() {
		return importeBruto;
	}

	public BigDecimal getImporteCedular() {
		return importeCedular;
	}

	public BigDecimal getImporteCNIC() {
		return importeCNIC;
	}

	public BigDecimal getImporteComprometido() {
		return importeComprometido;
	}

	public BigDecimal getImporteDevolucion() {
		return importeDevolucion;
	}

	public BigDecimal getImporteFlete23() {
		return importeFlete23;
	}

	public BigDecimal getImporteFlete4() {
		return importeFlete4;
	}

	public BigDecimal getImporteFletes() {
		return importeFletes;
	}

	public BigDecimal getImporteIMDT() {
		return importeIMDT;
	}

	public BigDecimal getImporteImporteNegativo() {
		return importeImporteNegativo;
	}

	public BigDecimal getImporteISRArrenda() {
		return importeISRArrenda;
	}

	public BigDecimal getImporteISRHonorarios() {
		return importeISRHonorarios;
	}

	public BigDecimal getImporteISRLaudos() {
		return importeISRLaudos;
	}

	public BigDecimal getImporteISROtros() {
		return importeISROtros;
	}

	public BigDecimal getImporteISRResico() {
		return importeISRResico;
	}

	public BigDecimal getImporteIva() {
		return importeIva;
	}

	public BigDecimal getImporteIVA() {
		return importeIVA;
	}

	public BigDecimal getImporteIva6() {
		return importeIva6;
	}

	public BigDecimal getImporteIvaArrenda() {
		return importeIvaArrenda;
	}

	public BigDecimal getImporteIvaHonorarios() {
		return importeIvaHonorarios;
	}

	public BigDecimal getImporteIvaProv() {
		return importeIvaProv;
	}

	public BigDecimal getImporteMasIva() {
		return importeMasIva;
	}

	public BigDecimal getImporteNeto() {
		return importeNeto;
	}

	public BigDecimal getImporteObra() {
		return importeObra;
	}

	public BigDecimal getImporteObra5() {
		return importeObra5;
	}

	public BigDecimal getImporteOtrosImpuestos() {
		return importeOtrosImpuestos;
	}

	public BigDecimal getImportePenalizacion() {
		return importePenalizacion;
	}

	public BigDecimal getImporteRetencion() {
		return importeRetencion;
	}

	public BigDecimal getImporteRetImpuestoCedular() {
		return importeRetImpuestoCedular;
	}

	public BigDecimal getImporteSancion() {
		return importeSancion;
	}

	public BigDecimal getImporteTesofe() {
		return importeTesofe;
	}

	public BigDecimal getIva() {
		return this.iva;
	}

	public String getJniName() {
		return jniName;
	}

	public String getMesCalendario() {
		return mesCalendario;
	}

	public int getNumeroMes() {
		return numeroMes;
	}

	public int getNumeroPoliza() {
		return numeroPoliza;
	}

	public int getNumeroRenglon() {
		return numeroRenglon;
	}

	public String getObgt() {
		return obgt;
	}

	public char getPeriodo13() {
		return periodo13;
	}

	public String getRfc() {
		return rfc;
	}

	public String getUnidadResponsable() {
		return unidadResponsable;
	}

	public abstract int insertaRenglon( Connection conn ) throws Exception;

	public abstract EgresoDetalle renglonNuevo();

	public void setAdefas( char adefas ) {
		this.adefas = adefas;
	}

	public void setAlm( String alm ) {
		this.alm = alm;
	}

	public void setAltaAlmacen( String altaAlmacen ) {
		this.altaAlmacen = altaAlmacen;
	}

	public void setCapitulo( String capitulo ) {
		this.capitulo = capitulo;
	}

	public void setCentroContable( String centroContable ) {
		this.centroContable = centroContable;
	}

	public void setcIdEntidadContable( String cIdEntidadContable ) {
		this.cIdEntidadContable = cIdEntidadContable;
	}

	public void setcIdRelacion( String cIdRelacion ) {
		this.cIdRelacion = cIdRelacion;
	}

	public void setcTipoPoliza( String cTipoPoliza ) {
		this.cTipoPoliza = cTipoPoliza;
	}

	public void setDocumentoAplicado( String documentoAplicado ) {
		this.documentoAplicado = documentoAplicado;
	}

	public void setEjercicioFiscal( String ejercicioFiscal ) {
		this.ejercicioFiscal = ejercicioFiscal;
	}

	public void setEp( String ep ) {
		this.ep = ep;
	}

	public void setEvento( String evento ) {
		this.evento = evento;
	}

	public void setFolioPoliza( int folioPoliza ) {
		this.folioPoliza = folioPoliza;
	}

	public void setIdCuentaContable( String idCuentaContable ) {
		this.idCuentaContable = idCuentaContable;
	}

	public void setIdTipoConcepto( String idTipoConcepto ) {
		this.idTipoConcepto = idTipoConcepto;
	}

	public void setIdTipoMovimiento( String idTipoMovimiento ) {
		this.idTipoMovimiento = idTipoMovimiento;
	}

	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	public void setImporte23IVA( BigDecimal importe23iva ) {
		importe23IVA = importe23iva;
	}

	public void setImporte2Millar( BigDecimal importe2Millar ) {
		this.importe2Millar = importe2Millar;
	}

	public void setImporte5Millar( BigDecimal importe5Millar ) {
		this.importe5Millar = importe5Millar;
	}

	public void setImporteAmortiza( BigDecimal importeAmortiza ) {
		this.importeAmortiza = importeAmortiza;
	}

	public void setImporteAmortizacionAnticipo( BigDecimal importeAmortizacionAnticipo ) {
		this.importeAmortizacionAnticipo = importeAmortizacionAnticipo;
	}

	public void setImporteBruto( BigDecimal importeBruto ) {
		this.importeBruto = importeBruto;
	}

	public void setImporteCedular( BigDecimal importeCedular ) {
		this.importeCedular = importeCedular;
	}

	public void setImporteCNIC( BigDecimal importeCNIC ) {
		this.importeCNIC = importeCNIC;
	}

	public void setImporteComprometido( BigDecimal importeComprometido ) {
		this.importeComprometido = importeComprometido;
	}

	public void setImporteDevolucion( BigDecimal importeDevolucion ) {
		this.importeDevolucion = importeDevolucion;
	}

	public void setImporteFlete23( BigDecimal importeFlete23 ) {
		this.importeFlete23 = importeFlete23;
	}

	public void setImporteFlete4( BigDecimal importeFlete4 ) {
		this.importeFlete4 = importeFlete4;
	}

	public void setImporteFletes( BigDecimal importeFletes ) {
		this.importeFletes = importeFletes;
	}

	public void setImporteIMDT( BigDecimal importeIMDT ) {
		this.importeIMDT = importeIMDT;
	}

	public void setImporteImporteNegativo( BigDecimal importeImporteNegativo ) {
		this.importeImporteNegativo = importeImporteNegativo;
	}

	public void setImporteISRArrenda( BigDecimal importeISRArrenda ) {
		this.importeISRArrenda = importeISRArrenda;
	}

	public void setImporteISRHonorarios( BigDecimal importeISRHonorarios ) {
		this.importeISRHonorarios = importeISRHonorarios;
	}

	public void setImporteISRLaudos( BigDecimal importeISRLaudos ) {
		this.importeISRLaudos = importeISRLaudos;
	}

	public void setImporteISROtros( BigDecimal importeISROtros ) {
		this.importeISROtros = importeISROtros;
	}

	public void setImporteISRResico( BigDecimal importeISRResico ) {
		this.importeISRResico = importeISRResico;
	}

	public void setImporteIva( BigDecimal importeIva ) {
		this.importeIva = importeIva;
	}

	public void setImporteIVA( BigDecimal importeIVA ) {
		this.importeIVA = importeIVA;
	}

	public void setImporteIva6( BigDecimal importeIva6 ) {
		this.importeIva6 = importeIva6;
	}

	public void setImporteIvaArrenda( BigDecimal importeIvaArrenda ) {
		this.importeIvaArrenda = importeIvaArrenda;
	}

	public void setImporteIvaHonorarios( BigDecimal importeIvaHonorarios ) {
		this.importeIvaHonorarios = importeIvaHonorarios;
	}

	public void setImporteIvaProv( BigDecimal importeIvaProv ) {
		this.importeIvaProv = importeIvaProv;
	}

	public void setImporteMasIva( BigDecimal importeMasIva ) {
		this.importeMasIva = importeMasIva;
	}

	public void setImporteNeto( BigDecimal importeNeto ) {
		this.importeNeto = importeNeto;
	}

	public void setImporteObra( BigDecimal importeObra ) {
		this.importeObra = importeObra;
	}

	public void setImporteObra5( BigDecimal importeObra5 ) {
		this.importeObra5 = importeObra5;
	}

	public void setImporteOtrosImpuestos( BigDecimal importeOtrosImpuestos ) {
		this.importeOtrosImpuestos = importeOtrosImpuestos;
	}

	public void setImportePenalizacion( BigDecimal importePenalizacion ) {
		this.importePenalizacion = importePenalizacion;
	}

	public void setImporteRetencion( BigDecimal importeRetencion ) {
		this.importeRetencion = importeRetencion;
	}

	public void setImporteRetImpuestoCedular( BigDecimal importeRetImpuestoCedular ) {
		this.importeRetImpuestoCedular = importeRetImpuestoCedular;
	}

	public void setImporteSancion( BigDecimal importeSancion ) {
		this.importeSancion = importeSancion;
	}

	public void setImporteTesofe( BigDecimal importeTesofe ) {
		this.importeTesofe = importeTesofe;
	}

	public void setIva( BigDecimal iva ) {
		this.iva = iva;
	}

	public void setJniName( String jniName ) {
		this.jniName = jniName;
	}

	public void setMesCalendario( String mesCalendario ) {
		this.mesCalendario = mesCalendario;
	}

	public void setNumeroMes( int numeroMes ) {
		this.numeroMes = numeroMes;
	}

	public void setNumeroPoliza( int numeroPoliza ) {
		this.numeroPoliza = numeroPoliza;
	}

	public void setNumeroRenglon( int numeroRenglon ) {
		this.numeroRenglon = numeroRenglon;
	}

	public void setObgt( String obgt ) {
		this.obgt = obgt;
	}

	public void setPeriodo13( char periodo13 ) {
		this.periodo13 = periodo13;
	}

	public void setRfc( String rfc ) {
		this.rfc = rfc;
	}

	public void setUnidadResponsable( String unidadResponsable ) {
		this.unidadResponsable = unidadResponsable;
	}

}
