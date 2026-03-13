package com.syc.egresos.core.impl;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.syc.cfdi.db.CloseObject;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.contable.core.PagoObrasManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import com.syc.gestion.util.Util;


public class EgresoPAGOOBRADetalle extends EgresoDetalle {

	private int folioPAGOOBRA;

	@Override
	public List<EgresoDetalle> cargaDetalle( int folioEgreso ) throws Exception {
		super.init( getJniName() );
		List<EgresoDetalle> epdd = null;
		Connection conn = null;
		try {
			conn = getConnection();
			this.setfolioPAGOOBRA( folioEgreso );
			epdd = PagoObrasManager.cargaDetalle( conn, folioEgreso );
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
			importeBruto = importe.divide( ivaCalculo, 2, RoundingMode.HALF_UP );
		}

		BigDecimal importeIVA = importe.subtract( importeBruto ).setScale( 2, RoundingMode.HALF_UP );

		setEp( caledarioMes.getEp() );
		setImporteComprometido( caledarioMes.getImporteBruto() );
		setImporteNeto( caledarioMes.getImporteBruto() );
		setImporteBruto( importeBruto );
		setImporteMasIva( caledarioMes.getImporteBruto() );
		setImporteIva( pcIVA.multiply( new BigDecimal( 100.00 ) ).setScale( 2, RoundingMode.HALF_UP ) ); setImporteIVA( pcIVA.multiply( new BigDecimal( 100.00 ) ).setScale( 2, RoundingMode.HALF_UP ) );
		setCapitulo( EPManager.getComponente( caledarioMes.getEp(), "CAPITULO" ) );
		setImporteRetencion( new BigDecimal( 0.00d ) );
		setIva( importeIVA );
		setObgt( EPManager.getComponente( caledarioMes.getEp(), "CAPITULO" ) );
		setMesCalendario( String.valueOf( caledarioMes.getMesPresupuesto() ) );
		setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, this ) );
		return this;
	}

	@Override
	public List<EgresoDetalle> generaDetalleRetencion( Connection conn, EgresoEncabezado encabezado, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		throw new Exception( "Metodo no implementado" );
	}

	public int getFolioPAGOOBRA() {
		return folioPAGOOBRA;
	}

	@Override
	public int insertaRenglon( Connection conn ) throws Exception {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append( "INSERT INTO tPagoObraDetalle (" );

		queryBuilder.append( "    nFolioPAGOOBRA, " ); /* 1 */
		queryBuilder.append( "    nDocRenglon, " ); /* 2 */
		queryBuilder.append( "    nMes, " ); /* 3 */
		queryBuilder.append( "    cEjercicio, " ); /* 4 */
		queryBuilder.append( "    cIdEntidadContable, " ); /* 5 */
		queryBuilder.append( "    cIdRelacion, " ); /* 6 */
		queryBuilder.append( "    EP, " ); /* 7 */
		queryBuilder.append( "    cIdCuentaContable, " ); /* 8 */
		queryBuilder.append( "    mComprometido, " ); /* 9 */
		queryBuilder.append( "    nPoliza, " ); /* 10 */
		queryBuilder.append( "    ID_TIPO_MOVIMIENTO, " ); /* 11 */
		queryBuilder.append( "    ID_TIPO_CONCEPTO, " ); /* 12 */
		queryBuilder.append( "    cEvento, " ); /* 13 */
		queryBuilder.append( "    aEjercicioFiscal, " ); /* 14 */
		queryBuilder.append( "    cCentroContable, " ); /* 15 */
		queryBuilder.append( "    cMes, " ); /* 16 */
		queryBuilder.append( "    RFC, " ); /* 17 */
		queryBuilder.append( "    mImporteNeto, " ); /* 18 */
		queryBuilder.append( "    ALM, " ); /* 19 */
		queryBuilder.append( "    mImporteBruto, " ); /* 20 */
		queryBuilder.append( "    mImporteMasIva, " ); /* 21 */
		queryBuilder.append( "    mImporteIva, " ); /* 22 */
		queryBuilder.append( "    nCapitulo, " ); /* 23 */
		queryBuilder.append( "    cDocumentoHaplicado, " ); /* 24 */
		queryBuilder.append( "    nFolioPoliza, " ); /* 25 */
		queryBuilder.append( "    cTipoPoliza, " ); /* 26 */
		queryBuilder.append( "    mSancion, " ); /* 27 */
		queryBuilder.append( "    mDevolucion, " ); /* 28 */
		queryBuilder.append( "    mImporteAmortiza, " ); /* 29 */
		queryBuilder.append( "    mRetencion, " ); /* 30 */
		queryBuilder.append( "    mPenalizacion, " ); /* 31 */
		queryBuilder.append( "    m2Millar, " ); /* 32 */
		queryBuilder.append( "    m23IVA, " ); /* 33 */
		queryBuilder.append( "    mISRHonorarios, " ); /* 34 */
		queryBuilder.append( "    mObra5, " ); /* 35 */
		queryBuilder.append( "    mImporteFlete4, " ); /* 36 */
		queryBuilder.append( "    mISRArrenda, " ); /* 37 */
		queryBuilder.append( "    mRetImpuestoCedular, " ); /* 38 */
		queryBuilder.append( "    mBruto, " ); /* 39 */
		queryBuilder.append( "    mAmortizacionAnticipo, " );/* 40 */
		queryBuilder.append( "    mIVA, " ); /* 41 */
		queryBuilder.append( "    mNeto, " ); /* 42 */
		queryBuilder.append( "    m5Millar, " ); /* 43 */
		queryBuilder.append( "    mFletes, " ); /* 44 */
		queryBuilder.append( "    mCedular, " ); /* 45 */
		queryBuilder.append( "    mImporte, " ); /* 46 */
		queryBuilder.append( "    mImporteIvaArrenda, " ); /* 47 */
		queryBuilder.append( "    mImporteIvaHonorarios, " );/* 48 */
		queryBuilder.append( "    mImporteFlete23, " ); /* 49 */
		queryBuilder.append( "    mImporteIvaProv, " ); /* 50 */
		queryBuilder.append( "    mImporteObra, " ); /* 51 */
		queryBuilder.append( "    mCNIC, " ); /* 52 */
		queryBuilder.append( "    mIMDT, " ); /* 53 */
		queryBuilder.append( "    mTesofe, " ); /* 54 */
		queryBuilder.append( "    altaAlmacen, " ); /* 55 */
		queryBuilder.append( "    Periodo13, " ); /* 56 */
		queryBuilder.append( "    ADEFAS, " ); /* 57 */
		queryBuilder.append( "    OBGT, " ); /* 58 */
		queryBuilder.append( "    mImporteISRLaudos, " ); /* 59 */
		queryBuilder.append( "    mImporteNegativo, " ); /* 60 */
		queryBuilder.append( "    mISROtros, " ); /* 61 */
		queryBuilder.append( "    mImporteIva6" ); /* 62 */
		queryBuilder.append( ") " );

		// Cláusula VALUES y la lista de parámetros (?)
		queryBuilder.append( "VALUES (" );
		queryBuilder.append( "    ?, " ); /* 1 */
		queryBuilder.append( "    ?, " ); /* 2 */
		queryBuilder.append( "    ?, " ); /* 3 */
		queryBuilder.append( "    ?, " ); /* 4 */
		queryBuilder.append( "    ?, " ); /* 5 */
		queryBuilder.append( "    ?, " ); /* 6 */
		queryBuilder.append( "    ?, " ); /* 7 */
		queryBuilder.append( "    ?, " ); /* 8 */
		queryBuilder.append( "    ?, " ); /* 9 */
		queryBuilder.append( "    ?, " ); /* 10 */
		queryBuilder.append( "    ?, " ); /* 11 */
		queryBuilder.append( "    ?, " ); /* 12 */
		queryBuilder.append( "    ?, " ); /* 13 */
		queryBuilder.append( "    ?, " ); /* 14 */
		queryBuilder.append( "    ?, " ); /* 15 */
		queryBuilder.append( "    ?, " ); /* 16 */
		queryBuilder.append( "    ?, " ); /* 17 */
		queryBuilder.append( "    ?, " ); /* 18 */
		queryBuilder.append( "    ?, " ); /* 19 */
		queryBuilder.append( "    ?, " ); /* 20 */
		queryBuilder.append( "    ?, " ); /* 21 */
		queryBuilder.append( "    ?, " ); /* 22 */
		queryBuilder.append( "    ?, " ); /* 23 */
		queryBuilder.append( "    ?, " ); /* 24 */
		queryBuilder.append( "    ?, " ); /* 25 */
		queryBuilder.append( "    ?, " ); /* 26 */
		queryBuilder.append( "    ?, " ); /* 27 */
		queryBuilder.append( "    ?, " ); /* 28 */
		queryBuilder.append( "    ?, " ); /* 29 */
		queryBuilder.append( "    ?, " ); /* 30 */
		queryBuilder.append( "    ?, " ); /* 31 */
		queryBuilder.append( "    ?, " ); /* 32 */
		queryBuilder.append( "    ?, " ); /* 33 */
		queryBuilder.append( "    ?, " ); /* 34 */
		queryBuilder.append( "    ?, " ); /* 35 */
		queryBuilder.append( "    ?, " ); /* 36 */
		queryBuilder.append( "    ?, " ); /* 37 */
		queryBuilder.append( "    ?, " ); /* 38 */
		queryBuilder.append( "    ?, " ); /* 39 */
		queryBuilder.append( "    ?, " ); /* 40 */
		queryBuilder.append( "    ?, " ); /* 41 */
		queryBuilder.append( "    ?, " ); /* 42 */
		queryBuilder.append( "    ?, " ); /* 43 */
		queryBuilder.append( "    ?, " ); /* 44 */
		queryBuilder.append( "    ?, " ); /* 45 */
		queryBuilder.append( "    ?, " ); /* 46 */
		queryBuilder.append( "    ?, " ); /* 47 */
		queryBuilder.append( "    ?, " ); /* 48 */
		queryBuilder.append( "    ?, " ); /* 49 */
		queryBuilder.append( "    ?, " ); /* 50 */
		queryBuilder.append( "    ?, " ); /* 51 */
		queryBuilder.append( "    ?, " ); /* 52 */
		queryBuilder.append( "    ?, " ); /* 53 */
		queryBuilder.append( "    ?, " ); /* 54 */
		queryBuilder.append( "    ?, " ); /* 55 */
		queryBuilder.append( "    ?, " ); /* 56 */
		queryBuilder.append( "    ?, " ); /* 57 */
		queryBuilder.append( "    ?, " ); /* 58 */
		queryBuilder.append( "    ?, " ); /* 59 */
		queryBuilder.append( "    ?, " ); /* 60 */
		queryBuilder.append( "    ?, " ); /* 61 */
		queryBuilder.append( "    ?" ); /* 62 */
		queryBuilder.append( ")" );

		PreparedStatement ps = null;

		/*
		 * Raro pero en mIVA va el importe de IVA y en mImporteIVA el
		 * porcentaje.
		 */
		try {
			int i = 1;
			ps = conn.prepareStatement( queryBuilder.toString() );

			// Parámetros para INSERT INTO tPagoObraDetalle
			ps.setInt( i++, getFolioPAGOOBRA() );                                 /* 1: nFolioPAGOOBRA */
			ps.setInt( i++, getNumeroRenglon() );                                 /* 2: nDocRenglon */
			ps.setInt( i++, getNumeroMes() );                                     /* 3: nMes */
			ps.setString( i++, getEjercicioFiscal() );                            /* 4: cEjercicio */
			ps.setString( i++, getcIdEntidadContable() );                         /* 5: cIdEntidadContable */
			ps.setString( i++, getcIdRelacion() );                                /* 6: cIdRelacion */
			ps.setString( i++, getEp() );                                         /* 7: EP */
			ps.setString( i++, StringUtils.trimToEmpty( getIdCuentaContable() ) ); /* 8: cIdCuentaContable */
			ps.setBigDecimal( i++, getImporteComprometido() );                    /* 9: mComprometido */
			ps.setInt( i++, getNumeroPoliza() );                                  /* 10: nPoliza */
			ps.setString( i++, getIdTipoMovimiento() ); 							/* 11: ID_TIPO_MOVIMIENTO */
			ps.setString( i++, getIdTipoConcepto() );                             /* 12: ID_TIPO_CONCEPTO */
			ps.setString( i++, getEvento() );                                     /* 13: cEvento */
			ps.setString( i++, getEjercicioFiscal() );                            /* 14: aEjercicioFiscal */
			ps.setString( i++, getCentroContable() );                             /* 15: cCentroContable */
			ps.setString( i++, getMesCalendario() );                              /* 16: cMes */
			ps.setString( i++, getRfc() );                                        /* 17: RFC */
			ps.setBigDecimal( i++, getImporteNeto() );                            /* 18: mImporteNeto */
			ps.setString( i++, getAlm() );                                        /* 19: ALM */
			ps.setBigDecimal( i++, getImporteBruto() );                           /* 20: mImporteBruto */
			ps.setBigDecimal( i++, getImporteMasIva() );                          /* 21: mImporteMasIva */
			ps.setBigDecimal( i++, getImporteIva() );                             /* 22: mImporteIva */
			ps.setString( i++, getCapitulo() );                                   /* 23: nCapitulo */
			ps.setString( i++, getDocumentoAplicado() );                          /* 24: cDocumentoHaplicado */
			ps.setInt( i++, getFolioPoliza() );                                   /* 25: nFolioPoliza */
			ps.setString( i++, getcTipoPoliza() );                                /* 26: cTipoPoliza */
			ps.setBigDecimal( i++, getImporteSancion() );                         /* 27: mSancion */
			ps.setBigDecimal( i++, getImporteDevolucion() );                      /* 28: mDevolucion */
			ps.setBigDecimal( i++, getImporteAmortiza() );                        /* 29: mImporteAmortiza */
			ps.setBigDecimal( i++, getImporteRetencion() );                       /* 30: mRetencion */
			ps.setBigDecimal( i++, getImportePenalizacion() );                    /* 31: mPenalizacion */
			ps.setBigDecimal( i++, getImporte2Millar() );                         /* 32: m2Millar */
			ps.setBigDecimal( i++, getImporte23IVA() );                           /* 33: m23IVA */
			ps.setBigDecimal( i++, getImporteISRHonorarios() );                   /* 34: mISRHonorarios */
			ps.setBigDecimal( i++, getImporteObra5() );                           /* 35: mObra5 */
			ps.setBigDecimal( i++, getImporteFlete4() );                          /* 36: mImporteFlete4 */
			ps.setBigDecimal( i++, getImporteISRArrenda() );                      /* 37: mISRArrenda */
			ps.setBigDecimal( i++, getImporteRetImpuestoCedular() );              /* 38: mRetImpuestoCedular */
			ps.setBigDecimal( i++, getImporteBruto() );                           /* 39: mBruto */
			ps.setBigDecimal( i++, getImporteAmortizacionAnticipo() );            /* 40: mAmortizacionAnticipo */
			ps.setBigDecimal( i++, getIva() );                             /* 41: mIVA */
			ps.setBigDecimal( i++, getImporteNeto() );                            /* 42: mNeto */
			ps.setBigDecimal( i++, getImporte5Millar() );                         /* 43: m5Millar */
			ps.setBigDecimal( i++, getImporteFletes() );                          /* 44: mFletes */
			ps.setBigDecimal( i++, getImporteCedular() );                         /* 45: mCedular */
			ps.setBigDecimal( i++, getImporte() );                                /* 46: mImporte */
			ps.setBigDecimal( i++, getImporteIvaArrenda() );                      /* 47: mImporteIvaArrenda */
			ps.setBigDecimal( i++, getImporteIvaHonorarios() );                   /* 48: mImporteIvaHonorarios */
			ps.setBigDecimal( i++, getImporteFlete23() );                         /* 49: mImporteFlete23 */
			ps.setBigDecimal( i++, getImporteIvaProv() );                         /* 50: mImporteIvaProv */
			ps.setBigDecimal( i++, getImporteObra() );                            /* 51: mImporteObra */
			ps.setBigDecimal( i++, getImporteCNIC() );                            /* 52: mCNIC */
			ps.setBigDecimal( i++, getImporteIMDT() );                            /* 53: mIMDT */
			ps.setBigDecimal( i++, getImporteTesofe() );                          /* 54: mTesofe */
			ps.setString( i++, getAltaAlmacen() );                                /* 55: altaAlmacen */
			ps.setString( i++, String.valueOf( getPeriodo13() ) );                 /* 56: Periodo13 */
			ps.setString( i++, String.valueOf( getAdefas() ) );                    /* 57: ADEFAS */
			ps.setString( i++, String.valueOf( getObgt() ) );                      /* 58: OBGT */
			ps.setBigDecimal( i++, getImporteISRLaudos() );                       /* 59: mImporteISRLaudos */
			ps.setBigDecimal( i++, getImporteMasIva().multiply( new BigDecimal( -1.0d ) ) ); /* 60: mImporteNegativo */ 
			ps.setBigDecimal( i++, getImporteISROtros() );                        /* 61: mISROtros */
			ps.setBigDecimal( i++, getImporteIva6() );                            /* 62: mImporteIva6 */

			int afectados = ps.executeUpdate();

			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public EgresoDetalle renglonNuevo() {
		EgresoPAGOOBRADetalle renglonNuevo = new EgresoPAGOOBRADetalle();
		renglonNuevo.setfolioPAGOOBRA( this.getFolioPAGOOBRA() );
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

	public void setfolioPAGOOBRA( int folioPAGOOBRA ) {
		this.folioPAGOOBRA = folioPAGOOBRA;
	}

	@Override
	public EgresoDetalle generaDetalleRG( Connection conn, EgresoCalendarioRG importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
