package com.syc.egresos.core;


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

import com.syc.egresos.core.impl.EgresoPAGOFEDERALIZADOEncabezado;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;


public class EgresoRetencionFederalizadoManager {

	public static List<EgresoRetencion> generaRetencionesEgresoFederalizado( Connection conn, EgresoEncabezado encabezado ) throws Exception {
		List<EgresoRetencion> retenciones = new ArrayList<EgresoRetencion>();
		StringBuilder query = new StringBuilder();
				query.append( " SELECT DISTINCT cFolioContratoObra  cIDContrato, ");
				query.append( "		  'PAGOFEDERALIZADO'  AS tipoPago, ");
				query.append( "		  pagoFederalizado.nFoliopagoFederalizado AS folioPago, ");
				query.append( "		  pagoFederalizado.caNoContrarrecibo AS caNoContrarrecibo, ");
				query.append( "		  pagoFederalizado.mImporteBruto AS importeBruto, ");
				query.append( "		  beneficiario.cIdTipoPersonaRFC AS tipoPersona, ");
				query.append( "		  catRetencion.cIdTipoRetencion AS idTipoRetencion, ");
				query.append( "		  catRetencion.nPorcRetencion AS porcRetencion, ");
				query.append( "		  catRetencion.cComponente AS componente, ");
				query.append( "		  ROUND( pagoFederalizado.mImporteBruto * catRetencion.nPorcRetencion, 2) AS importeRetencion ");
				query.append( "	FROM  tPAGOFEDERALIZADOEncabezado pagoFederalizado (NOLOCK) ");
				query.append( "			  INNER JOIN pContratoFederalizadoRetencion contrato (NOLOCK) ");
				query.append( "			  on pagoFederalizado.cFolioContratoObra = contrato.cIdContrato ");
				query.append( "			  INNER JOIN pCatalogoTipoRetencion catRetencion  (NOLOCK)");
				query.append( "			  on contrato.cIdTipoRetencion = catRetencion.cIdTipoRetencion ");
				query.append( "			  INNER JOIN tBeneficiario beneficiario (NOLOCK)");
				query.append( "			  on beneficiario.drfc = pagoFederalizado.rfc ");
				query.append( "	where nFolioPAGOFEDERALIZADO = ? ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, ( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).getFolioPago() );
			
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

	public static int insertaRetencionEgresoFederalizado( Connection conn, EgresoEncabezado encabezado, EgresoRetencion retencion ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( "INSERT INTO tPagoRetencion (cEjercicio, cTipoDocumento ,cIdEntidadContable, nFolioPago, cIdTipoRetencion, mImporteRetencion)" );
		sb.append( "VALUES(?, 'PAGOFEDERALIZADO' , ?, ?, ?, ?)" );
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setString( 1, encabezado.getEjercicioFiscal() );
			ps.setString( 2, encabezado.getCentroContable() );
			ps.setInt( 3, ( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).getFolioPago() );
			ps.setInt( 4, retencion.getIdTipoRetencion() );
			ps.setBigDecimal( 5, retencion.getImporteRetencion() );

			int afectados = ps.executeUpdate();
			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}
	
	
}
