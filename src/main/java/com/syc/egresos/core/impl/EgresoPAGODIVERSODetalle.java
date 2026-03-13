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

import com.google.common.collect.Sets;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.contable.core.PagosDiversosManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import com.syc.gestion.util.Util;


/**
 * @author iccvi
 *
 */
public class EgresoPAGODIVERSODetalle extends EgresoDetalle {

	private String		ejercicioFiscal;
	private int			folioPAGODIVERSO;
	private BigDecimal	importeAmortizacionAnticipo	= new BigDecimal( 0.00d );
	private BigDecimal	importeBruto				= new BigDecimal( 0.00d );
	private BigDecimal	importeNeto					= new BigDecimal( 0.00d );
	private BigDecimal	iva;

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.syc.egresos.core.EgresoDetalle#cargaDetalle(int)
	 */
	@Override
	public List<EgresoDetalle> cargaDetalle( int folioPagoDiverso ) throws Exception {
		super.init( getJniName() );
		List<EgresoDetalle> epdd = null;
		Connection conn = null;
		try {
			conn = getConnection();
			epdd = PagosDiversosManager.cargaDetalle( conn, folioPagoDiverso );
			return epdd;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public EgresoDetalle generaDetalle( Connection conn, EgresoCalendario caledarioMes, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {

		BigDecimal importe = caledarioMes.getImporteBruto();
		BigDecimal importeBruto = new BigDecimal( 0.00 );
		
		if ( pcIVA != null && pcIVA.compareTo( Util.ZERO ) > 0 ) {
			BigDecimal ivaCalculo = new BigDecimal( 1.00 ).add( pcIVA ).setScale( 2, RoundingMode.HALF_UP );
			importeBruto = importe.divide( ivaCalculo,2, RoundingMode.HALF_UP );
		}
		
		BigDecimal importeIVA = importe.subtract( importeBruto ).setScale( 2,RoundingMode.HALF_UP );
		
		setFolioPAGODIVERSO( encabezado.getFolioPago() );
		setEp( caledarioMes.getEp() );
		setImporteComprometido( caledarioMes.getImporteBruto() );
		setImporteNeto( caledarioMes.getImporteBruto() );
		setImporteBruto( importeBruto );
		setImporteMasIva( caledarioMes.getImporteBruto() );
		setImporteIva( pcIVA.multiply( new BigDecimal( 100.00 ) ).setScale(2,RoundingMode.HALF_UP) );
		setCapitulo( EPManager.getComponente( caledarioMes.getEp(), "CAPITULO" ) );
		setImporteRetencion( new BigDecimal( 0.00d ) );
		setIva( importeIVA );
		setObgt( EPManager.getComponente( caledarioMes.getEp(), "CAPITULO" ) );
		setMesCalendario( String.valueOf( caledarioMes.getMesPresupuesto() ) );
		setIdTipoConcepto( caledarioMes.getIdTipoConcepto() );
		setIdTipoMovimiento( caledarioMes.getIdTipoMovimiento() );
		setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, this ) );
		setcIdEntidadContable( encabezado.getCentroContable() );
		setRfc( encabezado.getRfc() );

		return this;
	}

	@Override
	public List<EgresoDetalle> generaDetalleRetencion( Connection conn, EgresoEncabezado encabezado, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		List<EgresoDetalle> detalleRetenciones = new ArrayList<EgresoDetalle>();

		for ( SaldoMensual saldoMes : calendarioRetencion ) {

			EgresoDetalle detalle = renglonNuevo();

			BigDecimal importeBruto = BigDecimal.ZERO;
			if (pcIVA != null && pcIVA.compareTo(Util.ZERO) > 0) {
			    BigDecimal ivaCalculo = BigDecimal.ONE.add(pcIVA).setScale(2, RoundingMode.HALF_UP);
			    importeBruto = saldoMes.getMontoSaldo().divide(ivaCalculo, 2, RoundingMode.HALF_UP); // Se agrega la escala en la división
			}


			BigDecimal importeIVA = saldoMes.getMontoSaldo().subtract( importeBruto );

			detalle.setCentroContable( encabezado.getCentroContable() );
			detalle.setEjercicioFiscal( encabezado.getEjercicioFiscal() );
			detalle.setMesCalendario( String.valueOf( saldoMes.getMes() ) );
			detalle.setEp( saldoMes.getEp() );
			detalle.setIdTipoConcepto( saldoMes.getIdTipoConcepto() );
			detalle.setIdTipoMovimiento( saldoMes.getIdTipoMovimiento() );
			detalle.setImporteBruto( importeBruto );
			detalle.setImporteMasIva( saldoMes.getMontoSaldo() );
			detalle.setImporteIva(  pcIVA.multiply( new BigDecimal( 100.00 ) ).setScale(2,RoundingMode.HALF_UP) );
			detalle.setCapitulo( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			( ( EgresoPAGODIVERSODetalle ) detalle ).setFolioPAGODIVERSO( encabezado.getFolioPago() );
			detalle.setObgt( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setRfc( encabezado.getRfc() );
			detalle.setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, detalle ) );
			detalle.setUnidadResponsable( encabezado.getUnidadResponsable() );
			detalle.setEjercicioFiscal( encabezado.getEjercicioFiscal() );
			BeanUtils.setProperty( detalle, retencion.getComponente(), saldoMes.getMontoSaldo() );
			detalle.setCentroContable( encabezado.getCentroContable() );
			detalle.setcIdRelacion( encabezado.getNombre() );
			detalle.setImporteComprometido( saldoMes.getMontoSaldo() );
			detalle.setImporteRetencion( saldoMes.getMontoSaldo() );
			( ( EgresoPAGODIVERSODetalle ) detalle ).setIva( importeIVA );
			detalleRetenciones.add( detalle );

		}

		return detalleRetenciones;
	}

	@Override
	public EgresoDetalle generaDetalleRG( Connection conn, EgresoCalendarioRG importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEjercicioFiscal() {
		return ejercicioFiscal;
	}

	public int getFolioPAGODIVERSO() {
		return folioPAGODIVERSO;
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

	public BigDecimal getIva() {
		return this.iva;
	}

	@Override
	public int insertaRenglon( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder( "" );
		query.append( "INSERT INTO tPAGODIVERSODetalle( "
/*1*/				+ " nFolioPAGODIVERSO , "
/*2*/				+ " nDocRenglon , "
/*3*/				+ " nMes , "
/*4*/				+ " cEjercicio , "
/*5*/				+ " cIdEntidadContable , "
/*6*/				+ " cIdRelacion ,  "
/*7*/				+ " EP ,  "
/*8*/				+ " cIdCuentaContable ,  "
/*9*/				+ " mComprometido ,  " );
		query.append( " "
/*10*/				+ " nPoliza ,  "
/*11*/				+ " ID_TIPO_MOVIMIENTO ,  "
/*12*/				+ " ID_TIPO_CONCEPTO ,  "
/*13*/				+ " cEvento ,  "
/*14*/				+ " aEjercicioFiscal ,  "
/*15*/				+ " cCentroContable ,  "
/*16*/				+ " cMes ,  "
/*17*/				+ " RFC ,  "
/*18*/				+ " mImporteNeto ,  " );
/*19*/		query.append( " ALM ,  "
/*20*/				+ " mImporteBruto ,  "
/*21*/				+ " mImporteMasIva ,  "
/*22*/				+ " mImporteIva ,  "
/*23*/            + " nCapitulo ,  "
/*24*/		            + " cDocumentoHaplicado ,  "
/*25*/		            + " nFolioPoliza ,  "
/*26*/	            + " cTipoPoliza ,  "
/*27*/	            + " mSancion ,  "
/*28*/	            + " mDevolucion , " );
/*29*/	query.append( " mImporteAmortiza ,  "
/*30*/			+ " mRetencion ,  "
/*31*/				+ " mPenalizacion ,  "
/*32*/				+ " m2Millar ,  "
/*33*/				+ " m23IVA ,  "
/*34*/				+ " mISRHonorarios , "
/*35*/				+ " mObra5 ,"
/*36*/				+ " mImporteFlete4 , "
/*37*/				+ " mISRArrenda , " );
/*38*/		query.append( " mRetImpuestoCedular , "
/*39*/				    + " mBruto ,  "
/*40*/				    + " mAmortizacionAnticipo , "
/*41*/				    + " mIVA ,"
/*42*/			    + " mNeto ,"
/*43*/			    + " m5Millar , "
/*44*/			    + " mFletes ,"
/*45*/			    + " mCedular , "
/*46*/			    + " mImporte ,"
/*47*/			    + " mImporteIvaArrenda ,  " );
		query.append( "								 mImporteIvaHonorarios ,  mImporteFlete23 ,  mImporteIvaProv ,  mImporteObra ,  mCNIC ,  mIMDT ,  mTesofe ,  altaAlmacen ,  Periodo13 ,  ADEFAS , " );
		query.append( "								 OBGT ,  mOtrosImpuestos ,  mImporteISRLaudos ,  mImporteNegativo ,  mISROtros ,  mImporteIva6, mimporteISRResico)" );
		query.append( "                               VALUES  ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
		PreparedStatement ps = null;

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			
/*1*/			ps.setInt( i++, getFolioPAGODIVERSO() );
/*2*/			ps.setInt( i++, getNumeroRenglon() );
/*3*/			ps.setInt( i++, getNumeroMes() );
/*4*/			ps.setString( i++, getEjercicioFiscal() );
/*5*/			ps.setString( i++, getcIdEntidadContable() );
/*6*/			ps.setString( i++, getcIdRelacion() );
/*7*/		ps.setString( i++, getEp() );
/*8*/		ps.setString( i++, StringUtils.trimToEmpty( getIdCuentaContable() ) );
/*9*/		ps.setBigDecimal( i++, getImporteComprometido() );
/*10*/		ps.setInt( i++, getNumeroPoliza() );
/*11*/		ps.setString( i++, getIdTipoMovimiento() );
/*12*/		ps.setString( i++, getIdTipoConcepto() );
/*13*/		ps.setString( i++, getEvento() );
/*14*/		ps.setString( i++, getEjercicioFiscal() );
/*15*/		ps.setString( i++, getCentroContable() );
/*16*/		ps.setString( i++, getMesCalendario() );
/*17*/		ps.setString( i++, getRfc() );
/*18*/		ps.setBigDecimal( i++, getImporteNeto() );
/*19*/		ps.setString( i++, getAlm() );
/*20*/		ps.setBigDecimal( i++, getImporteBruto() );
/*21*/		ps.setBigDecimal( i++, getImporteMasIva() );
/*22*/		ps.setBigDecimal( i++, getImporteIva() );
/*23*/		ps.setString( i++, getCapitulo() );
/*24*/		ps.setString( i++, getDocumentoAplicado() );
/*25*/		ps.setInt( i++, getFolioPoliza() );
/*26*/		ps.setString( i++, getcTipoPoliza() );
/*27*/		ps.setBigDecimal( i++, getImporteSancion() );
/*28*/		ps.setBigDecimal( i++, getImporteDevolucion() );
/*29*/		ps.setBigDecimal( i++, getImporteAmortiza() );
/*30*/		ps.setBigDecimal( i++, getImporteRetencion() );
/*31*/		ps.setBigDecimal( i++, getImportePenalizacion() );
/*32*/		ps.setBigDecimal( i++, getImporte2Millar() );
/*33*/		ps.setBigDecimal( i++, getImporte23IVA() );
/*34*/		ps.setBigDecimal( i++, getImporteISRHonorarios() );
/*35*/		ps.setBigDecimal( i++, getImporteObra5() );
/*36*/		ps.setBigDecimal( i++, getImporteFlete4() );
/*37*/		ps.setBigDecimal( i++, getImporteISRArrenda() );
/*38*/		ps.setBigDecimal( i++, getImporteRetImpuestoCedular() );
/*39*/		ps.setBigDecimal( i++, getImporteBruto() );
/*40*/		ps.setBigDecimal( i++, getImporteAmortizacionAnticipo() );
/*41*/		ps.setBigDecimal( i++, getIva() );
/*42*/		ps.setBigDecimal( i++, getImporteNeto() );
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
			ps.setString( i++, String.valueOf( getPeriodo13() ) );
			ps.setString( i++, String.valueOf( getAdefas() ) );
			ps.setString( i++, String.valueOf( getObgt() ) );
			ps.setBigDecimal( i++, getImporteOtrosImpuestos() );
			ps.setBigDecimal( i++, getImporteISRLaudos() );
			ps.setBigDecimal( i++, getImporteMasIva().multiply( new BigDecimal( -1.0d ) ) );
			ps.setBigDecimal( i++, getImporteISROtros() );
			ps.setBigDecimal( i++, getImporteIva6() );
			ps.setBigDecimal( i++, getImporteISRResico() );

			int afectados = ps.executeUpdate();

			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public EgresoDetalle renglonNuevo() {

		EgresoPAGODIVERSODetalle renglonNuevo = new EgresoPAGODIVERSODetalle();

		renglonNuevo.setFolioPAGODIVERSO( this.getFolioPAGODIVERSO() );
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

		return renglonNuevo;
	}

	public void setEjercicioFiscal( String ejercicioFiscal ) {
		this.ejercicioFiscal = ejercicioFiscal;
	}

	public void setFolioPAGODIVERSO( int folioPAGODIVERSO ) {
		this.folioPAGODIVERSO = folioPAGODIVERSO;
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

	public void setIva( BigDecimal iva ) {
		this.iva = iva;
	}

}
