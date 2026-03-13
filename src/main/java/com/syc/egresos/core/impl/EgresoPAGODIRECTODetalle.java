package com.syc.egresos.core.impl;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.syc.contable.anteproyecto.EPManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class EgresoPAGODIRECTODetalle extends EgresoDetalle {

	private String	idDocumento;
	private int		folioPagoDirecto;

	@Override
	public List<EgresoDetalle> cargaDetalle( int folioEgreso ) throws Exception {
		throw new Exception( "cargaDetalle no implementado " );
	}

	@Override
	public List<EgresoDetalle> generaDetalleRetencion( Connection conn, EgresoEncabezado encabezado, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {

		List<EgresoDetalle> detalleRetenciones = new ArrayList<EgresoDetalle>();

		for ( SaldoMensual saldoMes : calendarioRetencion ) {

			EgresoDetalle detalle = renglonNuevo();

			detalle.setCentroContable( encabezado.getCentroContable() );
			detalle.setEjercicioFiscal( encabezado.getEjercicioFiscal() );
			( ( EgresoPAGODIRECTODetalle ) detalle ).setIdDocumento( String.valueOf( ( ( EgresoPAGODIRECTOEncabezado ) encabezado ).getIdDocumento() ) );
			detalle.setMesCalendario( String.valueOf( saldoMes.getMes() ) );
			detalle.setEp( saldoMes.getEp() );
			detalle.setIdTipoConcepto( saldoMes.getIdTipoConcepto() );
			detalle.setIdTipoMovimiento( saldoMes.getIdTipoMovimiento() );
			detalle.setImporteBruto( saldoMes.getMontoSaldo() );
			detalle.setImporteMasIva( saldoMes.getMontoSaldo() );
			detalle.setCapitulo( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			( ( EgresoPAGODIRECTODetalle ) detalle ).setFolioPagoDirecto( encabezado.getFolioPago() );
			detalle.setObgt( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setRfc( encabezado.getRfc() );
			detalle.setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, detalle ) );
			BeanUtils.setProperty( detalle, retencion.getComponente(), saldoMes.getMontoSaldo() );
			detalleRetenciones.add( detalle );

		}

		return detalleRetenciones;
	}

	public String getIdDocumento() {
		return idDocumento;
	}

	public int getFolioPagoDirecto() {
		return folioPagoDirecto;
	}

	@Override
	public int insertaRenglon( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( " INSERT INTO tPagoDirectoDetalle( " );
		query.append( " 			ADEFAS, " );
		query.append( " 			ALM, " );
		query.append( " 			altaAlmacen, " );
		query.append( " 			cCentroContable, " );
		query.append( " 			cEjercicio, " );
		query.append( " 			cEvento, " );
		query.append( " 			cIdCuentaContable, " );
		query.append( " 			cIdDocumento, " );
		query.append( " 			cMes, " );
		query.append( " 			EP, " );
		query.append( " 			ID_TIPO_CONCEPTO, " );
		query.append( " 			ID_TIPO_MOVIMIENTO, " );
		query.append( " 			m23IVA, " );
		query.append( " 			m2Millar, " );
		query.append( " 			mCNIC, " );
		query.append( " 			mComprometido, " );
		query.append( " 			mDevolucion, " );
		query.append( " 			mIMDT, " );
		query.append( " 			mImporte, " );
		query.append( " 			mImporteAmortiza, " );
		query.append( " 			mImporteBruto, " );
		query.append( " 			mImporteFlete23, " );
		query.append( " 			mImporteFlete4, " );
		query.append( " 			mImporteISRLaudos, " );
		query.append( " 			mImporteIva, " );
		query.append( " 			mImporteIva6, " );
		query.append( " 			mImporteIvaArrenda, " );
		query.append( " 			mImporteIvaHonorarios, " );
		query.append( " 			mImporteIvaProv, " );
		query.append( " 			mImporteMasIva, " );
		query.append( " 			mImporteNeto, " );
		query.append( " 			mImporteObra, " );
		query.append( " 			mISRArrenda, " );
		query.append( " 			mISRHonorarios, " );
		query.append( " 			mISROtros, " );
		query.append( " 			mObra5, " );
		query.append( " 			mOtrosImpuestos, " );
		query.append( " 			mPenalizacion, " );
		query.append( " 			mRetencion, " );
		query.append( " 			mRetImpuestoCedular, " );
		query.append( " 			mSancion, " );
		query.append( " 			mTesofe, " );
		query.append( " 			nCapitulo, " );
		query.append( " 			nDocRenglon, " );
		query.append( " 			nFolioPagoDirecto, " );
		query.append( " 			nPoliza, " );
		query.append( " 			OBGT, " );
		query.append( " 			Periodo13, " );
		query.append( " 			RFC, " );
		query.append( " 			mimporteISRResico) " );
		query.append( " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) " );

		PreparedStatement ps = null;

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setString( i++, String.valueOf( getAdefas() ) );
			ps.setString( i++, getAlm() );
			ps.setString( i++, getAltaAlmacen() );
			ps.setString( i++, getCentroContable() );
			ps.setString( i++, getEjercicioFiscal() );
			ps.setString( i++, getEvento() );
			ps.setString( i++, getIdCuentaContable() );
			ps.setString( i++, getIdDocumento() );
			ps.setString( i++, getMesCalendario() );
			ps.setString( i++, getEp() );
			ps.setString( i++, getIdTipoConcepto() );
			ps.setString( i++, getIdTipoMovimiento() );
			ps.setBigDecimal( i++, getImporte23IVA() );
			ps.setBigDecimal( i++, getImporte2Millar() );
			ps.setBigDecimal( i++, getImporteCNIC() );
			ps.setBigDecimal( i++, getImporteComprometido() );
			ps.setBigDecimal( i++, getImporteDevolucion() );
			ps.setBigDecimal( i++, getImporteIMDT() );
			ps.setBigDecimal( i++, getImporte() );
			ps.setBigDecimal( i++, getImporteAmortiza() );
			ps.setBigDecimal( i++, getImporteBruto() );
			ps.setBigDecimal( i++, getImporteFlete23() );
			ps.setBigDecimal( i++, getImporteFlete4() );
			ps.setBigDecimal( i++, getImporteISRLaudos() );
			ps.setBigDecimal( i++, getImporteIva() );
			ps.setBigDecimal( i++, getImporteIva6() );
			ps.setBigDecimal( i++, getImporteIvaArrenda() );
			ps.setBigDecimal( i++, getImporteIvaHonorarios() );
			ps.setBigDecimal( i++, getImporteIvaProv() );
			ps.setBigDecimal( i++, getImporteMasIva() );
			ps.setBigDecimal( i++, getImporteNeto() );
			ps.setBigDecimal( i++, getImporteObra() );
			ps.setBigDecimal( i++, getImporteISRArrenda() );
			ps.setBigDecimal( i++, getImporteISRHonorarios() );
			ps.setBigDecimal( i++, getImporteISROtros() );
			ps.setBigDecimal( i++, getImporteObra5() );
			ps.setBigDecimal( i++, getImporteOtrosImpuestos() );
			ps.setBigDecimal( i++, getImportePenalizacion() );
			ps.setBigDecimal( i++, getImporteRetencion() );
			ps.setBigDecimal( i++, getImporteRetImpuestoCedular() );
			ps.setBigDecimal( i++, getImporteSancion() );
			ps.setBigDecimal( i++, getImporteTesofe() );
			ps.setString( i++, getCapitulo() );
			ps.setInt( i++, getNumeroRenglon() );
			ps.setInt( i++, getFolioPagoDirecto() );
			ps.setInt( i++, getNumeroPoliza() );
			ps.setString( i++, getObgt() );
			ps.setString( i++, String.valueOf( getPeriodo13() ) );
			ps.setString( i++, getRfc() );
			ps.setBigDecimal( i++, getImporteISRResico() );

			return ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	@Override
	public EgresoDetalle renglonNuevo() {

		EgresoDetalle detalle = new EgresoPAGODIRECTODetalle();
		detalle.setAdefas( 'N' );
		detalle.setAltaAlmacen( "" );
		detalle.setImporteComprometido( new BigDecimal( 0.00d ) );
		detalle.setImporteIva( new BigDecimal( 0.0 ) );
		detalle.setImporteNeto( new BigDecimal( 0.00d ) );
		detalle.setPeriodo13( 'N' );

		return detalle;
	}

	public void setIdDocumento( String idDocumento ) {
		this.idDocumento = idDocumento;
	}

	public void setFolioPagoDirecto( int folioPagoDirecto ) {
		this.folioPagoDirecto = folioPagoDirecto;
	}

	@Override
	public String toString() {
		return "EgresoPAGODIRECTODetalle [idDocumento=" + idDocumento + ", nFolioPagoDirecto=" + folioPagoDirecto + ", getAdefas()=" + getAdefas() + ", getAlm()=" + getAlm() + ", getAltaAlmacen()=" + getAltaAlmacen() + ", getCapitulo()=" + getCapitulo() + ", getCentroContable()=" + getCentroContable() + ", getEjercicioFiscal()=" + getEjercicioFiscal() + ", getEp()=" + getEp() + ", getEvento()=" + getEvento() + ", getIdCuentaContable()=" + getIdCuentaContable() + ", getIdTipoConcepto()=" + getIdTipoConcepto() + ", getIdTipoMovimiento()=" + getIdTipoMovimiento() + ", getImporte()=" + getImporte() + ", getImporte23IVA()=" + getImporte23IVA() + ", getImporte2Millar()=" + getImporte2Millar() + ", getImporteAmortiza()=" + getImporteAmortiza() + ", getImporteBruto()=" + getImporteBruto() + ", getImporteCNIC()=" + getImporteCNIC() + ", getImporteComprometido()=" + getImporteComprometido() + ", getImporteDevolucion()=" + getImporteDevolucion() + ", getImporteFlete23()=" + getImporteFlete23() + ", getImporteFlete4()=" + getImporteFlete4() + ", getImporteIMDT()=" + getImporteIMDT() + ", getImporteISRArrenda()=" + getImporteISRArrenda() + ", getImporteISRHonorarios()=" + getImporteISRHonorarios() + ", getImporteISRLaudos()=" + getImporteISRLaudos() + ", getImporteISROtros()=" + getImporteISROtros() + ", getImporteIva()=" + getImporteIva() + ", getImporteIva6()=" + getImporteIva6() + ", getImporteIvaArrenda()=" + getImporteIvaArrenda() + ", getImporteIvaHonorarios()=" + getImporteIvaHonorarios() + ", getImporteIvaProv()=" + getImporteIvaProv() + ", getImporteMasIva()=" + getImporteMasIva() + ", getImporteNeto()=" + getImporteNeto() + ", getImporteObra()=" + getImporteObra() + ", getImporteObra5()=" + getImporteObra5() + ", getImporteOtrosImpuestos()=" + getImporteOtrosImpuestos() + ", getImportePenalizacion()=" + getImportePenalizacion() + ", getImporteRetencion()=" + getImporteRetencion() + ", getImporteRetImpuestoCedular()=" + getImporteRetImpuestoCedular() + ", getImporteSancion()=" + getImporteSancion() + ", getImporteTesofe()=" + getImporteTesofe() + ", getJniName()=" + getJniName() + ", getMesCalendario()=" + getMesCalendario() + ", getNumeroPoliza()=" + getNumeroPoliza() + ", getNumeroRenglon()=" + getNumeroRenglon() + ", getObgt()=" + getObgt() + ", getPeriodo13()=" + getPeriodo13() + ", getRfc()=" + getRfc() + "]";
	}

	@Override
	public EgresoDetalle generaDetalle( Connection conn, EgresoCalendario importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		setAdefas( 'N' );
		setAltaAlmacen( "" );
		setImporteComprometido( new BigDecimal( 0.00d ) );
		setImporteIva( new BigDecimal( 0.0 ) );
		setImporteNeto( new BigDecimal( 0.00d ) );
		setPeriodo13( 'N' );
		setCentroContable( encabezado.getCentroContable() );
		setEjercicioFiscal( encabezado.getEjercicioFiscal() );
		setIdDocumento( String.valueOf( ( ( EgresoPAGODIRECTOEncabezado ) encabezado ).getIdDocumento() ) );
		setIdTipoMovimiento( importeDetalle.getIdTipoMovimiento() );
		setRfc( encabezado.getRfc() );
		setEp( importeDetalle.getEp() );
		setImporteComprometido( new BigDecimal( 0.0d ) );
		setImporteNeto( importeDetalle.getImporteBruto() );
		setImporteBruto( importeDetalle.getImporteBruto() );
		setImporteMasIva( importeDetalle.getImporteBruto() );
		setImporteIva( new BigDecimal( 0.00d ) );
		setCapitulo( EPManager.getComponente( importeDetalle.getEp(), "CAPITULO" ) );
		setImporteRetencion( new BigDecimal( 0.00d ) );
		setImporteIva( new BigDecimal( 0.0 ) );
		setObgt( EPManager.getComponente( importeDetalle.getEp(), "CAPITULO" ) );
		setMesCalendario( String.valueOf( importeDetalle.getMesPresupuesto() ) );
		setIdTipoConcepto( importeDetalle.getIdTipoConcepto() );
		setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, this ) );
		setFolioPagoDirecto( encabezado.getFolioPago() );
		return this;

	}

	@Override
	public EgresoDetalle generaDetalleRG( Connection conn, EgresoCalendarioRG importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
