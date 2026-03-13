package com.syc.egresos.core;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

import com.syc.egresos.core.impl.EgresoPAGODIRECTOEncabezado;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;


public class EgresoRetencionManager {

	public static List<EgresoRetencion> generaRetencionesEgresoDirecto( Connection conn, EgresoEncabezado encabezado ) throws Exception {
		int regimen = encabezado.getRegimenFiscal();
		List<EgresoRetencion> retenciones = new ArrayList<EgresoRetencion>();
		StringBuilder query = new StringBuilder();

		// Agregar la retencion de IVA Resico cuando el regimen fiscal es Resico
		if ( regimen == 626 ) {
			query.append( "SELECT DISTINCT '' AS cIDContrato," );
			query.append( "		'PAGODIRECTO'  AS tipoPago," );
			query.append( "		pagoDirecto.nFolioPagoDirecto AS folioPago," );
			query.append( "		pagoDirecto.caNoContrarrecibo AS caNoContrarrecibo," );
			query.append( "		pagoDirecto.mImporteBruto AS importeBruto," );
			query.append( "		beneficiario.cIdTipoPersonaRFC AS tipoPersona," );
			query.append( "		partidaRetencion.idRetencion AS idTipoRetencion," );
			query.append( "		partidaRetencion.obligatoria AS obligatoria," );
			query.append( "		partidaRetencion.cFisica AS requeridaPersonaFisica," );
			query.append( "		partidaRetencion.cMoral AS requeridaPersonaMoral," );
			query.append( "		catRetencion.cTipoRetencion AS tipoRetencion," );
			query.append( "		catRetencion.nPorcRetencion AS porcRetencion," );
			query.append( "		catRetencion.cComponente AS componente," );
			query.append( "		ROUND( pagoDirecto.mImporteBruto * catRetencion.nPorcRetencion, 2) AS importeRetencion" );
			query.append( "   FROM	tPagoDirectoEncabezado pagoDirecto WITH(NOLOCK) " );
			query.append( "		INNER JOIN tPagoDirectoPAAS paas WITH(NOLOCK) " );
			query.append( "		ON pagoDirecto.nFolioPagoDirecto = paas.nFolioPagoDirecto " );
			query.append( "		INNER JOIN tBeneficiario beneficiario WITH(NOLOCK) " );
			query.append( "		ON pagoDirecto.cIdRFC = beneficiario.dRFC " );
			query.append( "		INNER JOIN tRelacionPartidaRetencion partidaRetencion WITH(NOLOCK) " );
			query.append( "		ON paas.cIdSubPartida = partidaRetencion.partida" );
			query.append( "		INNER JOIN pCatalogoTipoRetencion catRetencion WITH(NOLOCK) " );
			query.append( "		ON partidaRetencion.idRetencion = catRetencion.cIdTipoRetencion " );
			query.append( "  WHERE	(" );
			query.append( "			( CASE " );
			query.append( "				WHEN ( beneficiario.cIdTipoPersonaRFC = 2 OR beneficiario.cIdTipoPersonaRFC = 3 ) THEN 1" );
			query.append( "				ELSE -1" );
			query.append( "			  END" );
			query.append( "			) = partidaRetencion.cFisica" );
			query.append( "			OR" );
			query.append( "			( CASE " );
			query.append( "				WHEN beneficiario.cIdTipoPersonaRFC = 1  THEN 1" );
			query.append( "				ELSE -1" );
			query.append( "			  END" );
			query.append( "			) = partidaRetencion.cMoral" );
			query.append( "		)" );
			query.append( "   AND	pagoDirecto.nFolioPagoDirecto = ?  AND partidaRetencion.idRetencion <> 4" );
			query.append( " UNION ALL " );
			query.append( " SELECT '' AS cIDContrato, " );
			query.append( " 	'PAGODIRECTO'  AS tipoPago, " );
			query.append( " 	pagoDirecto.nFolioPagoDirecto AS folioPago, " );
			query.append( " 	pagoDirecto.caNoContrarrecibo AS caNoContrarrecibo, " );
			query.append( " 	pagoDirecto.mImporteBruto AS importeBruto, " );
			query.append( " 	beneficiario.cIdTipoPersonaRFC AS tipoPersona, " );
			query.append( " 	catRetencion.cIdTipoRetencion AS idTipoRetencion," );
			query.append( " 	0 AS obligatoria, " );
			query.append( " 	CASE WHEN beneficiario.cIdTipoPersonaRFC = 2 THEN 1 ELSE 0 END  AS requeridaPersonaFisica," );
			query.append( " 	0 AS requeridaPersonaMoral, " );
			query.append( " catRetencion.cTipoRetencion AS tipoRetencion," );
			query.append( " catRetencion.nPorcRetencion AS porcRetencion," );
			query.append( " catRetencion.cComponente AS componente," );
			query.append( "	ROUND( pagoDirecto.mImporteBruto * catRetencion.nPorcRetencion, 2) AS importeRetencion " );
			query.append( " FROM tPagoDirectoEncabezado pagoDirecto WITH(NOLOCK) " );
			query.append( " INNER JOIN tBeneficiario beneficiario WITH(NOLOCK) " );
			query.append( " on pagoDirecto.cIdRFC = beneficiario.dRFC" );
			query.append( " INNER JOIN 	pCatalogoTipoRetencion catRetencion WITH(NOLOCK) " );
			query.append( " ON 	catRetencion.cIdTipoRetencion = 18" );
			query.append( " where nFolioPagoDirecto = ?" );

		} else {

			query.append( "SELECT DISTINCT '' AS cIDContrato," );
			query.append( "		'PAGODIRECTO'  AS tipoPago," );
			query.append( "		pagoDirecto.nFolioPagoDirecto AS folioPago," );
			query.append( "		pagoDirecto.caNoContrarrecibo AS caNoContrarrecibo," );
			query.append( "		pagoDirecto.mImporteBruto AS importeBruto," );
			query.append( "		beneficiario.cIdTipoPersonaRFC AS tipoPersona," );
			query.append( "		partidaRetencion.idRetencion AS idTipoRetencion," );
			query.append( "		partidaRetencion.obligatoria AS obligatoria," );
			query.append( "		partidaRetencion.cFisica AS requeridaPersonaFisica," );
			query.append( "		partidaRetencion.cMoral AS requeridaPersonaMoral," );
			query.append( "		catRetencion.cTipoRetencion AS tipoRetencion," );
			query.append( "		catRetencion.nPorcRetencion AS porcRetencion," );
			query.append( "		catRetencion.cComponente AS componente," );
			query.append( "		ROUND( pagoDirecto.mImporteBruto * catRetencion.nPorcRetencion, 2) AS importeRetencion" );
			query.append( "   FROM	tPagoDirectoEncabezado pagoDirecto WITH(NOLOCK) " );
			query.append( "		INNER JOIN" );
			query.append( "		tPagoDirectoPAAS paas WITH(NOLOCK) " );
			query.append( "		ON pagoDirecto.nFolioPagoDirecto = paas.nFolioPagoDirecto" );
			query.append( "		INNER JOIN" );
			query.append( "		tBeneficiario beneficiario WITH(NOLOCK)" );
			query.append( "		ON pagoDirecto.cIdRFC = beneficiario.dRFC" );
			query.append( "		INNER JOIN" );
			query.append( "		tRelacionPartidaRetencion partidaRetencion WITH(NOLOCK)" );
			query.append( "		ON paas.cIdSubPartida = partidaRetencion.partida" );
			query.append( "		INNER JOIN " );
			query.append( "		pCatalogoTipoRetencion catRetencion WITH(NOLOCK)" );
			query.append( "		ON partidaRetencion.idRetencion = catRetencion.cIdTipoRetencion" );
			query.append( "  WHERE	(" );
			query.append( "			( CASE " );
			query.append( "				WHEN ( beneficiario.cIdTipoPersonaRFC = 2 OR beneficiario.cIdTipoPersonaRFC = 3 ) THEN 1" );
			query.append( "				ELSE -1" );
			query.append( "			  END" );
			query.append( "			) = partidaRetencion.cFisica" );
			query.append( "			OR" );
			query.append( "			( CASE " );
			query.append( "				WHEN beneficiario.cIdTipoPersonaRFC = 1  THEN 1" );
			query.append( "				ELSE -1" );
			query.append( "			  END" );
			query.append( "			) = partidaRetencion.cMoral" );
			query.append( "		)" );
			query.append( "   AND	pagoDirecto.nFolioPagoDirecto = ?  " );

		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, ( ( EgresoPAGODIRECTOEncabezado ) encabezado ).getFolioPagoDirecto() );
			if ( regimen == 626 ) {
				ps.setInt( 2, ( ( EgresoPAGODIRECTOEncabezado ) encabezado ).getFolioPagoDirecto() );
			}
			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoRetencion retencion = new EgresoRetencion();
				BeanUtils.populate( retencion, resultObj );
				retenciones.add( retencion );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}
			return retenciones;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static int insertaRetencionEgresoDirecto( Connection conn, EgresoEncabezado encabezado, EgresoRetencion retencion ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( "INSERT INTO tPagoDirectoRetencion (cEjercicio, cIdEntidadContable, nFolioPagoDirecto, cIdTipoRetencion, mImporteRetencion)" );
		sb.append( "VALUES(?, ?, ?, ?, ?)" );
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setString( 1, encabezado.getEjercicioFiscal() );
			ps.setString( 2, encabezado.getCentroContable() );
			ps.setInt( 3, ( ( EgresoPAGODIRECTOEncabezado ) encabezado ).getFolioPagoDirecto() );
			ps.setInt( 4, retencion.getIdTipoRetencion() );
			ps.setBigDecimal( 5, retencion.getImporteRetencion() );

			int afectados = ps.executeUpdate();
			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	public static BigDecimal totalRetenciones( List<EgresoRetencion> retenciones ) {
		BigDecimal totalRetenciones = new BigDecimal( 0.00 );
		if ( retenciones != null )
			for ( EgresoRetencion retencion : retenciones )
				totalRetenciones = totalRetenciones.add( retencion.getImporteRetencion() );

		return totalRetenciones;
	}

}
