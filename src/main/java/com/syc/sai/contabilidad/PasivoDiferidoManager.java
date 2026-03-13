
package com.syc.sai.contabilidad;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.axtel.egresos.exceptions.EgresoException;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.contable.DocumentAppliedException;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class PasivoDiferidoManager {

	private static final String	CONCEPTO_POLIZA	= "Por el registro del pasivo por concepto de los subsidios, transferencias o la aportaciones del Gobierno Federal pendientes de aplicar, mediante Gasto Directo";
	private static final String	TIPO_POLIZA		= "DI";
	private static final String	RFC				= "TESOFE";
	private static final Logger	log				= Logger.getLogger( PasivoDiferidoManager.class );

	public static boolean aplicarPasivoDiferido( Connection conn, String documento, String nFolio, String tablaEncabezado, String tablaDetalle, String campo ) throws EgresoException {
		boolean exito = true;
		PreparedStatement psEncabezado = null;
		PreparedStatement psDetalle = null;

		try {
			if ( PasivoDiferidoManager.documentoAplicaPasivoDiferido( conn, documento ) && !PasivoDiferidoManager.esDocumentoIngresosPropios( conn, tablaDetalle, campo, nFolio ) && !PasivoDiferidoManager.esRGOC( conn, documento, nFolio ) ) {

				CFSequenceManager seq = CFSequenceManager.getInstance( GestionInterface.ATT_CONEXION );
				int nFolioPasivoDiferido = seq.nextVal( "PASIVODIFERIDO" );
				String queryInsertaEncabezado = "INSERT INTO tPasivoDiferidoEncabezado(nFolioPasivoDiferido, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza, cUnidadResponsableContable, cDescripcionPoliza, cIdUsuarioCaptura,cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", '" + documento + "', " + nFolio + ", fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, '" + TIPO_POLIZA + "', cUnidadResponsableContable, '" + CONCEPTO_POLIZA + "', cIdUsuarioCaptura, cUnidadResponsable" + "  FROM " + tablaEncabezado + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;

				String queryInsertaDetalle = "INSERT INTO tPasivoDiferidoDetalle(nFolioPasivoDiferido, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mPasivoDiferido, RFC)" + "SELECT " + nFolioPasivoDiferido + ", nDocRenglon, cMes, REPLACE( REPLACE( cEvento, 'APD_',  'D_' ), 'D_', 'PA_') , cEjercicio, cCentroContable, EP, CASE WHEN (SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH(nolock) WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'SAI_FONDEN') = 'true' THEN mImporteNeto + mPenalizacion ELSE mImporteMasIVA END AS mImporteMasIVA, '" + RFC + "'" + "  FROM " + tablaDetalle + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;

				psEncabezado = conn.prepareStatement( queryInsertaEncabezado );
				psDetalle = conn.prepareStatement( queryInsertaDetalle );

				int insertados = psEncabezado.executeUpdate();
				insertados += psDetalle.executeUpdate();

				log.debug( "Se insertaron " + insertados + " campos para aplicar Pasivo Diferido " );
				log.info( " Aplicando motor para el Pasivo Diferido " + nFolioPasivoDiferido );

				AccountingEngine accEng = new AccountingEngine();
				accEng.setValidaInsuficienciaDeSaldo( true );
				accEng.makeAccountingApplication( conn, "PASIVODIFERIDO", String.valueOf( nFolioPasivoDiferido ), "tPasivoDiferidoEncabezado", "tPasivoDiferidoDetalle", "nFolioPasivoDiferido" );
			}
			return exito;
		} catch ( SQLException | DocumentAppliedException | AccountingEngineException e ) {
			throw new EgresoException( "Problemas aplicando pasivo diferido: " + e, e );
		} finally {
			CloseObject.closeObject( psEncabezado );
			CloseObject.closeObject( psDetalle );
		}
	}

	private static boolean esRGOC( Connection conn, String documento, String nFolio ) throws SQLException {

		boolean esRGOC = false;
		if ( "PAGODIVERSO".equalsIgnoreCase( documento ) ) {
			String query = "SELECT	COUNT(*) AS esRGOC " + "  FROM	tPAGODIVERSOEncabezado WITH(NOLOCK) " + " WHERE	cEsRelacionGastos = 'S' " + "   AND	nFolioPAGODIVERSO = ? ";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = conn.prepareStatement( query );
				ps.setString( 1, nFolio );

				rs = ps.executeQuery();

				if ( rs.next() ) {
					esRGOC = rs.getInt( 1 ) > 0;
				}
			} finally {
				CloseObject.closeObject( rs );
				CloseObject.closeObject( ps );
			}
		}

		return esRGOC;
	}

	private static boolean esDocumentoIngresosPropios( Connection conn, String tablaDetalle, String campo, String nFolio ) throws SQLException {
		boolean esIP = false;
		String query = "SELECT	COUNT(*) AS EPsIngresoPropio " + "  FROM	" + tablaDetalle + "  WITH(nolock) " + " WHERE	" + campo + " = ? " + "   AND	substring( EP,40,1 ) = '4'";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement( query );
			ps.setString( 1, nFolio );

			rs = ps.executeQuery();
			if ( rs.next() )
				esIP = rs.getInt( 1 ) > 0;

			return esIP;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private static boolean documentoAplicaPasivoDiferido( Connection conn, String documento ) throws SQLException {
		String query = "SELECT COUNT(*) AS Existe FROM tPagoAplicaPasivoDiferido WITH(NOLOCK) WHERE cTipoDocumento = ? AND cActivo = 'S'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean existe = false;
		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, documento );

			rs = ps.executeQuery();

			if ( rs.next() )
				existe = rs.getInt( 1 ) > 0;
			return existe;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private static int getFolioPasivoDiferido( Connection conn, String documento, int nFolioDocumento ) throws EgresoException {
		String query = "SELECT nFolioPasivoDiferido  FROM tPasivoDiferidoEncabezado WITH(NOLOCK) WHERE cTipoPago = ? AND nFolioPago = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nFolioPasivoDiferido = -1;
		try {

			ps = conn.prepareStatement( query );
			ps.setString( 1, documento );
			ps.setInt( 2, nFolioDocumento );

			rs = ps.executeQuery();

			if ( rs.next() )
				nFolioPasivoDiferido = rs.getInt( 1 );

			return nFolioPasivoDiferido;
		} catch ( SQLException e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	public static void cancelarPasivoDiferido( Connection conn, String documento, String nFolioDocumento ) throws AccountingEngineException, NumberFormatException, EgresoException {
		try {
			int nFolioPasivoDiferido = PasivoDiferidoManager.getFolioPasivoDiferido( conn, documento, Integer.parseInt( nFolioDocumento ) );
			if ( nFolioPasivoDiferido > 0 ) {
				AccountingEngine accEng = new AccountingEngine();
				accEng.setValidaInsuficienciaDeSaldo( true );

				accEng.cancelAccountingApplication( conn, "PASIVODIFERIDO", String.valueOf( nFolioPasivoDiferido ), "tPasivoDiferidoEncabezado", "tPasivoDiferidoDetalle", "nFolioPasivoDiferido" );

			}
		} catch ( AccountingEngineException e ) {
			if ( e.toString().indexOf( "sido cancelado" ) < 0 )
				throw e;
		}
	}

	public static boolean aplicarReduccionDevengado( Connection conn, String documento, String nFolio, String tablaEncabezado, String tablaDetalle, String campo ) throws Exception {

		/* Valida que pueda aplicarse la reduccion para el pasivo diferido */
		try {
			if ( "NOMINADEV".equals( documento ) )
				validaAplicarReduccionNom( conn, Integer.parseInt( nFolio ) );
			else
				validaAplicarReduccion( conn, Integer.parseInt( nFolio ) );
		} catch ( Exception e ) {
			log.error( e, e );
			return false;
		}

		boolean exito = true;
		PreparedStatement psEncabezado = null;
		PreparedStatement psDetalle = null;

		CFSequenceManager seq = CFSequenceManager.getInstance( GestionInterface.ATT_CONEXION );
		int nFolioPasivoDiferido = seq.nextVal( "PASIVODIFERIDO" );
		String queryInsertaEncabezado = null;
		String queryInsertaDetalle = null;

		if ( "NOMINADEV".equals( documento ) ) {
			queryInsertaEncabezado = "INSERT INTO tPasivoDiferidoEncabezado(nFolioPasivoDiferido, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza, cUnidadResponsableContable, cDescripcionPoliza, cIdUsuarioCaptura, cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", '" + documento + "', " + nFolio + ", fAplicacion, cRamo, caNoContrarreciboCLC, aEjercicioFiscal, '" + TIPO_POLIZA + "', cUnidadResponsableContable, '" + CONCEPTO_POLIZA + "', cIdUsuarioCaptura, cUnidadResponsable" + "  FROM " + tablaEncabezado + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;
			queryInsertaDetalle = "INSERT INTO tPasivoDiferidoDetalle(nFolioPasivoDiferido, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mPasivoDiferido, RFC, cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", nDocRenglon, cMes, REPLACE(DET.cEvento, 'D_', 'PA_') , DET.aEjercicioFiscal, DET.cCentroContable, DET.EP, DET.mImporteNeto, '" + RFC + "'" + ", cUnidadResponsable  FROM " + tablaDetalle + " DET WITH(NOLOCK) JOIN tNOMINADevEncabezado ENC WITH (NOLOCK) ON DET.nFolioNOMINACLC = ENC.nFolioNOMINACLC " + " WHERE DET." + campo + " = " + nFolio;
		} else {
			queryInsertaEncabezado = "INSERT INTO tPasivoDiferidoEncabezado(nFolioPasivoDiferido, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza, cUnidadResponsableContable, cDescripcionPoliza, cIdUsuarioCaptura, cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", '" + documento + "', " + nFolio + ", fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, '" + TIPO_POLIZA + "', cUnidadResponsableContable, '" + CONCEPTO_POLIZA + "', u_login, cUnidadResponsable" + "  FROM " + tablaEncabezado + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;

			queryInsertaDetalle = "INSERT INTO tPasivoDiferidoDetalle(nFolioPasivoDiferido, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mPasivoDiferido, RFC, cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", nDocRenglon, cMes, REPLACE(REPLACE(cEvento,'D_','PA_'),'APPA','PA') , aEjercicioFiscal, cCentroContable, EP, mTotal, '" + RFC + "'" + ", cUnidadResponsable  FROM " + tablaDetalle + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;
		}

		try {

			psEncabezado = conn.prepareStatement( queryInsertaEncabezado );
			psDetalle = conn.prepareStatement( queryInsertaDetalle );

			int insertados = psEncabezado.executeUpdate();
			insertados += psDetalle.executeUpdate();

			log.debug( "Se insertaron " + insertados + " campos para aplicar Pasivo Diferido " );
			log.info( " Aplicando motor para el Pasivo Diferido " + nFolioPasivoDiferido );

			AccountingEngine accEng = new AccountingEngine();
			accEng.setValidaInsuficienciaDeSaldo( true );
			accEng.makeAccountingApplication( conn, "PASIVODIFERIDO", String.valueOf( nFolioPasivoDiferido ), "tPasivoDiferidoEncabezado", "tPasivoDiferidoDetalle", "nFolioPasivoDiferido" );
		} finally {
			CloseObject.closeObject( psEncabezado );
			CloseObject.closeObject( psDetalle );
		}
		return exito;
	}

	private static void validaAplicarReduccion( Connection conn, int  nfoliodisminuciondev ) throws Exception {
		String query =" SELECT Count(*) AS documentosNoAplica "
					+ "  FROM   tdisminuciondevdetalle disminucionDevengado WITH(nolock) "
					+ "       LEFT OUTER JOIN tPasivoDiferidoEncabezado diferido WITH (NOLOCK) "
					+ "                    ON disminucionDevengado.caNoContrarrecibo = "
					+ "                       diferido.caNoContrarrecibo "
					+ " WHERE  nfoliodisminuciondev = ? "
					+ "       AND diferido.cDocumentoHaplicado = 'S' ";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query );
			ps.setInt( 1, nfoliodisminuciondev );

			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) == 0)
					throw new Exception("El documento de disminucion con folio " + nfoliodisminuciondev + " contiene documentos que no aplican para el pasivo diferido");

			} else {
				throw new Exception( "El documento no tiene detalle" );
			}

			if ( PasivoDiferidoManager.esDocumentoDDIngresosPropios( conn, nfoliodisminuciondev ) )
				throw new Exception( "El pago es de ingresos Propios. No aplica reduccion" );
			if ( PasivoDiferidoManager.esRGOC( conn, nfoliodisminuciondev ) )
				throw new Exception( "El pago es RG con OC, no aplica reduccion " );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private static void validaAplicarReduccionNom( Connection conn, int nfoliodisminuciondev ) throws Exception {
		String query = " SELECT Count(*) AS documentosNoAplica " + "  FROM   tNOMINADevDetalle disminucionDevengado WITH(nolock) " + "       LEFT OUTER JOIN tpagoaplicapasivodiferido aplicarDocto WITH(nolock) " + "                    ON 'NOMINADEV' = aplicarDocto.ctipodocumento " + " WHERE  nFolioNOMINACLC = ? " + "       AND aplicarDocto.ctipodocumento IS NULL ";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query );
			ps.setInt( 1, nfoliodisminuciondev );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				if ( rs.getInt( 1 ) > 1 )
					throw new Exception( "El documento de disminucion con folio " + nfoliodisminuciondev + " contiene documentos que no aplican para el pasivo diferido" );
			} else {
				throw new Exception( "El documento no tiene detalle" );
			}

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private static boolean esRGOC( Connection conn, int nfoliodisminuciondev ) throws Exception {
		String query = "SELECT	cTipoDoc,  " + "		nFolioDoc  " + "  FROM	tDisminucionDevDetalle detalle WITH(NOLOCK) " + " WHERE	nFolioDisminucionDev = ? ";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query );
			ps.setInt( 1, nfoliodisminuciondev );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				String documento = rs.getString( "cTipoDoc" );
				String nFolio = rs.getString( "nFolioDoc" );

				return esRGOC( conn, documento, nFolio );
			} else
				throw new Exception( "No se encontro detalle en la disminucion del devengado con folio " + nfoliodisminuciondev );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	private static boolean esDocumentoDDIngresosPropios( Connection conn, int nfoliodisminuciondev ) throws Exception {
		return esDocumentoIngresosPropios( conn, "tdisminuciondevdetalle", "nfoliodisminuciondev", String.valueOf( nfoliodisminuciondev ) );
	}

	public static boolean aplicaPasivoDiferidoLaudos( Connection conn, String documento, String nFolio, String tablaEncabezado, String tablaDetalle, String campo ) throws Exception {
		boolean exito = true;

		if ( !PasivoDiferidoManager.esDocumentoIngresosPropios( conn, tablaDetalle, campo, nFolio ) && PasivoDiferidoManager.retencionLaudoSICOP( conn, nFolio ) ) {
			PreparedStatement psEncabezado = null;
			PreparedStatement psDetalle = null;

			double mImporteISRLaudos = importeRetencionLaudos( conn, nFolio );

			CFSequenceManager seq = CFSequenceManager.getInstance( GestionInterface.ATT_CONEXION );
			int nFolioPasivoDiferido = seq.nextVal( "PASIVODIFERIDO" );
			String queryInsertaEncabezado = "INSERT INTO tPasivoDiferidoEncabezado(nFolioPasivoDiferido, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza, cUnidadResponsableContable, cDescripcionPoliza, cIdUsuarioCaptura,cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", '" + documento + "', " + nFolio + ", fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, '" + TIPO_POLIZA + "', cUnidadResponsableContable, '" + CONCEPTO_POLIZA + "', cIdUsuarioCaptura, cUnidadResponsable" + "  FROM " + tablaEncabezado + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;

			String queryInsertaDetalle = "INSERT INTO tPasivoDiferidoDetalle(nFolioPasivoDiferido, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mPasivoDiferido, RFC) \r\n" + "SELECT DISTINCT " + nFolioPasivoDiferido + ", CASE WHEN RETEENC.nFolioRetencion IS NULL THEN RGDET.nDocRenglon ELSE RETEDET.nDocRenglon END AS nDocRenglon\r\n" + "	, CASE WHEN RETEDET.CMES IS NULL THEN RGDET.cMes ELSE RETEDET.cMes END AS cMes\r\n" + "	, REPLACE(RGDET.cEvento, 'APD_', 'PA_')\r\n" + "	, RGDET.cEjercicio\r\n" + "	, RGDET.cCentroContable\r\n" + "	, CASE WHEN RETEDET.EP IS NULL THEN RGDET.EP ELSE RETEDET.EP END AS EP\r\n" + "	, CASE WHEN RETEDET.mImporteISRLaudos IS NULL THEN RGDET.mImporteISRLaudos ELSE RETEDET.mImporteISRLaudos END AS mImporteISRLaudos\r\n" + " , '" + RFC + "'\r\n" + " FROM " + tablaEncabezado + " AS RGENC WITH(NOLOCK)\r\n" + " JOIN " + tablaDetalle + " AS RGDET WITH(NOLOCK) ON RGENC.nFolioRELACIONGASTOS = RGDET.nFolioRELACIONGASTOS\r\n" + " LEFT JOIN tRetencionEncabezado AS RETEENC WITH(NOLOCK) ON RGENC.caNoContrarrecibo = RETEENC.caNoContrarrecibo\r\n" + " LEFT JOIN tRetencionDetalle AS RETEDET WITH(NOLOCK) ON RETEENC.nFolioRetencion = RETEDET.nFolioRetencion\r\n" + " WHERE RGENC." + campo + " = " + nFolio;

			try {
				psEncabezado = conn.prepareStatement( queryInsertaEncabezado );
				psDetalle = conn.prepareStatement( queryInsertaDetalle );

				int insertados = psEncabezado.executeUpdate();
				insertados += psDetalle.executeUpdate();

				log.debug( "Se insertaron " + insertados + " campos para aplicar Pasivo Diferido " );
				log.info( " Aplicando motor para el Pasivo Diferido " + nFolioPasivoDiferido );

				AccountingEngine accEng = new AccountingEngine();
				accEng.setValidaInsuficienciaDeSaldo( true );
				accEng.makeAccountingApplication( conn, "PASIVODIFERIDO", String.valueOf( nFolioPasivoDiferido ), "tPasivoDiferidoEncabezado", "tPasivoDiferidoDetalle", "nFolioPasivoDiferido" );
			} finally {
				CloseObject.closeObject( psEncabezado );
				CloseObject.closeObject( psDetalle );
			}
		}
		return exito;
	}

	private static boolean retencionLaudoSICOP( Connection conn, String nFolio ) throws Exception {
		boolean cRetSICOP = false;

		String query = " SELECT cRetSICOP FROM dbo.tComprobacionLaudos (NOLOCK) WHERE nFolioRELACIONGASTOS = ? ";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, nFolio );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				cRetSICOP = "S".equals( rs.getString( "cRetSICOP" ) );
			}

		} catch ( Exception e ) {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

		return cRetSICOP;
	}

	private static double importeRetencionLaudos( Connection conn, String nFolio ) throws Exception {
		double retencion = 0.00;

		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = " SELECT mImporteRet FROM dbo.tComprobacionLaudos (NOLOCK) WHERE nFolioRELACIONGASTOS = ? ";

		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, nFolio );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				retencion = rs.getDouble( "mImporteRet" );
			}

		} catch ( Exception e ) {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

		return retencion;
	}
	
	public static boolean aplicaPasivoDiferidoRESICORG( Connection conn, String documento, String nFolio, String tablaEncabezado, String tablaDetalle, String campo ) throws Exception {
		boolean exito = true;

		if ( !PasivoDiferidoManager.esDocumentoIngresosPropios( conn, tablaDetalle, campo, nFolio ) ) {
			PreparedStatement psEncabezado = null;
			PreparedStatement psDetalle = null;

			CFSequenceManager seq = CFSequenceManager.getInstance( GestionInterface.ATT_CONEXION );
			int nFolioPasivoDiferido = seq.nextVal( "PASIVODIFERIDO" );
			String queryInsertaEncabezado = "INSERT INTO tPasivoDiferidoEncabezado(nFolioPasivoDiferido, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza, cUnidadResponsableContable, cDescripcionPoliza, cIdUsuarioCaptura,cUnidadResponsable)" + "SELECT " + nFolioPasivoDiferido + ", '" + documento + "', " + nFolio + ", fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, '" + TIPO_POLIZA + "', cUnidadResponsableContable, '" + CONCEPTO_POLIZA + "', cIdUsuarioCaptura, cUnidadResponsable" + "  FROM " + tablaEncabezado + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;

			String queryInsertaDetalle = "INSERT INTO tPasivoDiferidoDetalle(nFolioPasivoDiferido, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mPasivoDiferido, RFC, cUnidadResponsable) \r\n" 
										+ "SELECT DISTINCT " + nFolioPasivoDiferido + ", CASE WHEN RETEENC.nFolioRetencion IS NULL THEN RGDET.nDocRenglon ELSE RETEDET.nDocRenglon END AS nDocRenglon\r\n" 
										+ "	, CASE WHEN RETEDET.CMES IS NULL THEN RGDET.cMes ELSE RETEDET.cMes END AS cMes\r\n" 
										+ "	, REPLACE(RGDET.cEvento, 'APD_', 'PA_')\r\n" 
										+ "	, RGDET.cEjercicio\r\n" 
										+ "	, RGDET.cCentroContable\r\n" 
										+ "	, CASE WHEN RETEDET.EP IS NULL THEN RGDET.EP ELSE RETEDET.EP END AS EP\r\n" 
										+ "	, CASE WHEN SUM(RETEDET.mimporteISRResico + RETEDET.mImporteFlete4 + RETEDET.mImporteIvaHonorarios + RETEDET.mImporteIvaArrenda + RETEDET.mISRArrenda + RETEDET.mISRHonorarios) IS NULL \r\n"
										+ "			THEN SUM(RGDET.mimporteISRResico + RGDET.mImporteFlete4 + RGDET.mImporteIvaHonorarios + RGDET.mImporteIvaArrenda + RGDET.mISRArrenda + RGDET.mISRHonorarios) \r\n"
										+ "		ELSE SUM(RETEDET.mimporteISRResico + RETEDET.mImporteFlete4 + RETEDET.mImporteIvaHonorarios + RETEDET.mImporteIvaArrenda + RETEDET.mISRArrenda + RETEDET.mISRHonorarios) \r\n"
										+ "	END AS mimporte\r\n" 
										+ " , '" + RFC + "'\r\n" 
										+ "	, RGDET.cUnidadResponsable\r\n"
										+ " FROM " + tablaEncabezado + " AS RGENC WITH(NOLOCK)\r\n" 
										+ " JOIN " + tablaDetalle + " AS RGDET WITH(NOLOCK) ON RGENC.nFolioRELACIONGASTOS = RGDET.nFolioRELACIONGASTOS\r\n" 
										+ " LEFT JOIN tRetencionEncabezado AS RETEENC WITH(NOLOCK) ON RGENC.caNoContrarrecibo = RETEENC.caNoContrarrecibo\r\n" 
										+ " LEFT JOIN tRetencionDetalle AS RETEDET WITH(NOLOCK) ON RETEENC.nFolioRetencion = RETEDET.nFolioRetencion\r\n" 
										+ " WHERE RGENC." + campo + " = " + nFolio + " \r\n"
										+ " GROUP BY RETEENC.nFolioRetencion, RGDET.nDocRenglon, RETEDET.nDocRenglon, RETEDET.CMES, RGDET.cMes, RGDET.cEvento, RGDET.cEjercicio, RGDET.cCentroContable, RETEDET.EP, RGDET.EP, RETEDET.mimporteISRResico, RGDET.cUnidadResponsable \r\n"
										+ " HAVING SUM(RGDET.mimporteISRResico + RGDET.mImporteFlete4 + RGDET.mImporteIvaHonorarios + RGDET.mImporteIvaArrenda + RGDET.mISRArrenda + RGDET.mISRHonorarios) <> 0";

			try {
				psEncabezado = conn.prepareStatement( queryInsertaEncabezado );
				psDetalle = conn.prepareStatement( queryInsertaDetalle );

				int insertados = psEncabezado.executeUpdate();
				insertados += psDetalle.executeUpdate();

				log.debug( "Se insertaron " + insertados + " campos para aplicar Pasivo Diferido " );
				log.info( " Aplicando motor para el Pasivo Diferido " + nFolioPasivoDiferido );

				AccountingEngine accEng = new AccountingEngine();
				accEng.setValidaInsuficienciaDeSaldo( true );
				accEng.makeAccountingApplication( conn, "PASIVODIFERIDO", String.valueOf( nFolioPasivoDiferido ), "tPasivoDiferidoEncabezado", "tPasivoDiferidoDetalle", "nFolioPasivoDiferido" );
			} finally {
				CloseObject.closeObject( psEncabezado );
				CloseObject.closeObject( psDetalle );
			}
		}
		return exito;
	}

}
