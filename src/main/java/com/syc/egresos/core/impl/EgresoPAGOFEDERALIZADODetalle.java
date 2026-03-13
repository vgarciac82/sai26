/**
 * 
 */
package com.syc.egresos.core.impl;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.cfdi.db.CloseObject;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.contable.core.PagosFederalizadoManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;


/**
 * @author iccvi
 *
 */
public class EgresoPAGOFEDERALIZADODetalle extends EgresoDetalle {
	
	private static final Logger log = Logger.getLogger( EgresoPAGOFEDERALIZADODetalle.class );

	private String		ejercicioFiscal;
	private int			folioPAGOFEDERALIZADO;
	private BigDecimal	importeAmortizacionAnticipo	= new BigDecimal( 0.00d );
	private BigDecimal	importeBruto				= new BigDecimal( 0.00d );
	private BigDecimal	importeNeto					= new BigDecimal( 0.00d );
	private String 		FFM;
	public final String RFCFFM = "BMN930209927";

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.syc.egresos.core.EgresoDetalle#cargaDetalle(int)
	 */
	@Override
	public List<EgresoDetalle> cargaDetalle( int folioPagoFederalizado ) throws Exception {
		super.init( getJniName() );
		List<EgresoDetalle> epdd = null;
		Connection conn = null;
		try {
			conn = getConnection();
			epdd = PagosFederalizadoManager.cargaDetalle( conn, folioPagoFederalizado );
			return epdd;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public EgresoDetalle generaDetalle( Connection conn, EgresoCalendario caledarioMes, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		
		BigDecimal importe =caledarioMes.getImporteBruto();
		double valor = pcIVA.doubleValue() + 1;
		double bruto = importe.doubleValue() / valor;		
		double iva = importe.doubleValue() - bruto;
		double porcentaje = pcIVA.doubleValue() * 100;
		
		setEp( caledarioMes.getEp() );
		setImporteComprometido( caledarioMes.getImporteBruto() );
		setImporteNeto( caledarioMes.getImporteBruto() );
		setImporteBruto( new BigDecimal (bruto).setScale( 2 , RoundingMode.HALF_UP) );
		setImporteMasIva( caledarioMes.getImporteBruto() );
		setImporteIVA( new BigDecimal (iva).setScale( 2 , RoundingMode.HALF_UP) );
		setCapitulo( EPManager.getComponente( caledarioMes.getEp(), "CAPITULO" ) );
		setImporteRetencion( new BigDecimal( 0.00d ) );
		setImporteIva( new BigDecimal (porcentaje).setScale( 2 , RoundingMode.HALF_UP) );
		setObgt( EPManager.getComponente( caledarioMes.getEp(), "CAPITULO" ) );
		setMesCalendario( String.valueOf( caledarioMes.getMesPresupuesto() ) );
		setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, this ) );
		setUnidadResponsable( encabezado.getUnidadResponsable() );
		setEjercicioFiscal( encabezado.getEjercicioFiscal() );
		setFolioPAGOFEDERALIZADO( encabezado.getFolioPago() );
		setCentroContable(encabezado.getCentroContable());
		setNumeroMes( Integer.parseInt(  encabezado.getMes() ));
		setcIdRelacion( encabezado.getNombre() );
		setIdTipoConcepto( encabezado.getIdConcepto() );
		setRfc( encabezado.getRfc() );
		
		if (RFCFFM.equals( encabezado.getRfc())) {
			setFFM(getObgt() + EgresoDetalleManager.calculaProgramaFFM( conn, encabezado, this ) );
		}
		
		return this;
	}

	@Override
	public List<EgresoDetalle> generaDetalleRetencion( Connection conn, EgresoEncabezado encabezado, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		List<EgresoDetalle> detalleRetenciones = new ArrayList<EgresoDetalle>();

		for ( SaldoMensual saldoMes : calendarioRetencion ) {

			EgresoDetalle detalle = renglonNuevo();

			detalle.setCentroContable( encabezado.getCentroContable() );
			detalle.setEjercicioFiscal( encabezado.getEjercicioFiscal() );
			detalle.setMesCalendario( String.valueOf( saldoMes.getMes() ) );
			detalle.setEp( saldoMes.getEp() );
			detalle.setIdTipoConcepto( saldoMes.getIdTipoConcepto() );
			detalle.setIdTipoMovimiento( saldoMes.getIdTipoMovimiento() );
			detalle.setImporteBruto( saldoMes.getMontoSaldo() );
			detalle.setImporteMasIva( saldoMes.getMontoSaldo() );
			detalle.setCapitulo( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			( ( EgresoPAGOFEDERALIZADODetalle ) detalle ).setFolioPAGOFEDERALIZADO( encabezado.getFolioPago() );
			detalle.setObgt( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setRfc( encabezado.getRfc() );
			detalle.setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, detalle ) );
			detalle.setUnidadResponsable( encabezado.getUnidadResponsable() );
			detalle.setEjercicioFiscal( encabezado.getEjercicioFiscal() );
			BeanUtils.setProperty( detalle, retencion.getComponente(), saldoMes.getMontoSaldo() );
			detalle.setCentroContable(encabezado.getCentroContable());
			
			if (RFCFFM.equals( encabezado.getRfc())) {
				( ( EgresoPAGOFEDERALIZADODetalle ) detalle ).setFFM(getObgt() + EgresoDetalleManager.calculaProgramaFFM( conn, encabezado, this ) );
			}
			
			detalleRetenciones.add( detalle );

		}

		return detalleRetenciones;
	}

	public String getEjercicioFiscal() {
		return ejercicioFiscal;
	}

	public int getFolioPAGOFEDERALIZADO() {
		return folioPAGOFEDERALIZADO;
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
	public int insertaRenglon( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder("");
		query.append("INSERT INTO tPAGOFEDERALIZADODetalle( nFolioPAGOFEDERALIZADO , nDocRenglon , nMes , cEjercicio , cIdEntidadContable , cIdRelacion ,  EP ,  cIdCuentaContable ,  mComprometido ,  ");
		query.append("           nPoliza ,  ID_TIPO_MOVIMIENTO ,  ID_TIPO_CONCEPTO ,  cEvento ,  aEjercicioFiscal ,  cCentroContable ,  cMes ,  RFC ,  mImporteNeto ,  ");
		query.append("			 ALM ,  mImporteBruto ,  mImporteMasIva ,  mImporteIva ,  nCapitulo ,  mSancion ,  mDevolucion ,");
		query.append("			 mImporteAmortiza ,  mRetencion ,  mPenalizacion ,  m2Millar ,  m23IVA ,  mISRHonorarios ,  mObra5 ,  mImporteFlete4 ,  mISRArrenda , ");
		query.append("			 mRetImpuestoCedular ,  mBruto ,  mAmortizacionAnticipo ,  mIVA ,  mNeto ,  m5Millar ,  mFletes ,  mCedular ,  mImporte ,  mImporteIvaArrenda ,  ");
		query.append("			 mImporteIvaHonorarios ,  mImporteFlete23 ,  mImporteIvaProv ,  mImporteObra ,  mCNIC ,  mIMDT ,  mTesofe ,  altaAlmacen ,  ");
		query.append("			 OBGT ,  mOtrosImpuestos ,  mImporteISRLaudos ,  FFM ,  mISROtros ,  mImporteIva6, cUnidadResponsable, mimporteISRResico)");
		query.append("           VALUES  ( ?,?,?,?,'00',?,?,?,?,0,'000',?,?,?,?,?,?,?,1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
		PreparedStatement ps = null;

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( i++, getFolioPAGOFEDERALIZADO() );
			ps.setInt( i++, getNumeroRenglon() );
			ps.setInt( i++, getNumeroMes() );
			ps.setString( i++, getEjercicioFiscal() );
			ps.setString( i++, getcIdRelacion() );
			ps.setString( i++, getEp() );
			ps.setString( i++, StringUtils.trimToEmpty( getIdCuentaContable() ) );
			ps.setBigDecimal( i++, getImporteComprometido() );
			//ps.setString( i++, getIdTipoMovimiento() );
			ps.setString( i++, getIdTipoConcepto() );
			ps.setString( i++, getEvento() );
			ps.setString( i++, getEjercicioFiscal() );
			ps.setString( i++, getCentroContable() );
			ps.setString( i++, getMesCalendario() );
			ps.setString( i++, getRfc() );
			ps.setBigDecimal( i++, getImporteNeto() );
			ps.setBigDecimal( i++, getImporteBruto() );
			ps.setBigDecimal( i++, getImporteMasIva() );
			ps.setBigDecimal( i++, getImporteIva() );
			ps.setString( i++, getCapitulo() );
			ps.setBigDecimal( i++, getImporteSancion() );
			ps.setBigDecimal( i++, getImporteDevolucion() );
			ps.setBigDecimal( i++, getImporteAmortiza() );
			ps.setBigDecimal( i++, getImporteRetencion() );
			ps.setBigDecimal( i++, getImportePenalizacion() );
			ps.setBigDecimal( i++, getImporte2Millar() );
			ps.setBigDecimal( i++, getImporte23IVA() );
			ps.setBigDecimal( i++, getImporteISRHonorarios() );
			ps.setBigDecimal( i++, getImporteObra5() );
			ps.setBigDecimal( i++, getImporteFlete4() );
			ps.setBigDecimal( i++, getImporteISRArrenda() );
			ps.setBigDecimal( i++, getImporteRetImpuestoCedular() );
			ps.setBigDecimal( i++, getImporteBruto() );
			ps.setBigDecimal( i++, getImporteAmortizacionAnticipo() );
			ps.setBigDecimal( i++, getImporteIVA() );
			ps.setBigDecimal( i++, getImporteNeto() );
			ps.setBigDecimal( i++, getImporte5Millar() );
			ps.setBigDecimal( i++, getImporteFletes() );
			ps.setBigDecimal( i++, getImporteCedular() );
			ps.setBigDecimal( i++, getImporte() );
			ps.setBigDecimal( i++, getImporteIvaArrenda() );
			ps.setBigDecimal( i++, getImporteIvaHonorarios() );
			ps.setBigDecimal( i++, getImporteFlete23() );
			ps.setBigDecimal( i++, getImporteIvaProv() );
			ps.setBigDecimal( i++, getImporteObra() );
			ps.setBigDecimal( i++, getImporteCNIC() );
			ps.setBigDecimal( i++, getImporteIMDT() );
			ps.setBigDecimal( i++, getImporteTesofe() );
			ps.setString( i++, getAltaAlmacen() );
			ps.setString( i++, String.valueOf( getObgt() ) );
			ps.setBigDecimal( i++, getImporteOtrosImpuestos() );
			ps.setBigDecimal( i++, getImporteISRLaudos() );
			ps.setString( i++, getFFM() );
			ps.setBigDecimal( i++, getImporteISROtros() );
			ps.setBigDecimal( i++, getImporteIva6() );
			ps.setString( i++, getUnidadResponsable() );
			ps.setBigDecimal( i++, getImporteISRResico() );
			
			log.debug( query.toString() );

			int afectados = ps.executeUpdate();

			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public EgresoDetalle renglonNuevo() {

		EgresoPAGOFEDERALIZADODetalle renglonNuevo = new EgresoPAGOFEDERALIZADODetalle();

		renglonNuevo.setFolioPAGOFEDERALIZADO( this.getFolioPAGOFEDERALIZADO() );
		renglonNuevo.setNumeroMes( this.getNumeroMes() );
		renglonNuevo.setEjercicioFiscal( this.getEjercicioFiscal() );
		renglonNuevo.setcIdEntidadContable( this.getcIdEntidadContable() );
		renglonNuevo.setcIdRelacion( this.getcIdRelacion() );
		renglonNuevo.setNumeroPoliza( 0 );
		renglonNuevo.setIdTipoMovimiento( this.getIdTipoMovimiento() );
		renglonNuevo.setIdTipoConcepto( this.getIdTipoConcepto() );
		renglonNuevo.setCentroContable( getCentroContable() );
		renglonNuevo.setRfc( getRfc() );
		renglonNuevo.setAlm( getAlm() );
		renglonNuevo.setDocumentoAplicado( getDocumentoAplicado() );
		renglonNuevo.setFolioPoliza( getFolioPoliza() );
		renglonNuevo.setPeriodo13( getPeriodo13() );
		renglonNuevo.setAdefas( getAdefas() );
		renglonNuevo.setAltaAlmacen( getAltaAlmacen() );
		renglonNuevo.setFFM( getFFM() );
		renglonNuevo.setUnidadResponsable( getUnidadResponsable() );
		
		return renglonNuevo;
	}

	public void setEjercicioFiscal( String ejercicioFiscal ) {
		this.ejercicioFiscal = ejercicioFiscal;
	}

	public void setFolioPAGOFEDERALIZADO( int folioPAGOFEDERALIZADO ) {
		this.folioPAGOFEDERALIZADO = folioPAGOFEDERALIZADO;
	}

	public void setImporteAmortizacionAnticipo( BigDecimal importeAmortizacionAnticipo ) {
		this.importeAmortizacionAnticipo = importeAmortizacionAnticipo;
	}

	public void setImporteBruto( BigDecimal importeBruto ) {
		this.importeBruto = importeBruto;
	}

	public void setImporteNeto( BigDecimal importeNeto ) {
		this.importeNeto = importeNeto;
	}

	public String getFFM() {
		return FFM;
	}

	public void setFFM( String fFM ) {
		FFM = fFM;
	}

	@Override
	public EgresoDetalle generaDetalleRG( Connection conn, EgresoCalendarioRG importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
