package com.axtel.contratos.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.contratos.exception.ContratoException;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ContratoDiversoManager {

	private static final Logger log = LogManager.getLogger( ContratoDiversoManager.class );

	public static void deleteWithholding( Connection conn, String contractId, Integer withholdingId ) throws SQLException {

		StringBuilder queryDelete = new StringBuilder( "DELETE FROM pContratoDiversoRetencion WHERE cIdContrato = ? AND cIdTipoRetencion= ?" );
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( queryDelete.toString() );
			ps.setString( 1, contractId );
			ps.setInt( 2, withholdingId );

			ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	private static boolean existeContratoEP( Connection conn, ContratoEP detalle ) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT COUNT(*) AS EXISTE FROM tContratoEP WITH(NOLOCK) WHERE cEjercicio = ? AND cIdContrato  = ? AND cTipoContrato  = ? AND EP = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, String.valueOf( detalle.getEjercicio() ) );
			ps.setString( 2, detalle.getIdContrato() );
			ps.setString( 3, detalle.getTipoContrato() );
			ps.setString( 4, detalle.getEp() );

			rs = ps.executeQuery();
			if ( rs.next() )
				return rs.getInt( 1 ) > 0;
			return false;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	private static Date getDate( ResultSet rs, String columnName ) throws SQLException {
		return rs.getDate( columnName ) != null ? new Date( rs.getDate( columnName ).getTime() ) : null;
	}

	/**
	 * Inserta el detalle de el anticipo del contrato
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param anticipo
	 *            anticipo a insertar
	 * @return Numero de renglones insertados.
	 * @throws ContratoException
	 */
	public static int insertaContratoAnticipo( Connection conn, ContratoDiversoAnticipo anticipo ) throws ContratoException {

		PreparedStatement ps = null;

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO pContratoDiversoAnticipo(cEjercicio ,cIdEntidadContable ,cIdContrato ,cIdTipoAnticipoDiverso ,mImporteAnticipo ,mImporteAnticipoIVA ,mTotalAnticipo ,nPorcAmortizacion ,fAnticipo ,nPorcAsignacion ,mAmortizado)" );
		query.append( "VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )" );
		int insertados = 0;

		try {

			ps = conn.prepareStatement( query.toString() );

			ps.setString( 1, anticipo.getEjercicio() );
			ps.setString( 2, anticipo.getCentroContable() );
			ps.setString( 3, anticipo.getIdContrato() );
			ps.setInt( 4, anticipo.getIdTipoAnticipoDiverso() );
			ps.setBigDecimal( 5, anticipo.getImporteAnticipo() );
			ps.setBigDecimal( 6, anticipo.getImporteAnticipoIVA() );
			ps.setBigDecimal( 7, anticipo.getTotalAnticipo() );
			ps.setInt( 8, anticipo.getPorcAmortizacion() );
			ps.setDate( 9, Util.toSQLDate( anticipo.getfAnticipo() ) );
			ps.setDouble( 10, anticipo.getPorcAsignacion() );
			ps.setBigDecimal( 11, anticipo.getImporteAmortizado() );

			insertados += ps.executeUpdate();

			log.debug( "Registros insertados (Anticipo Convenio) " + insertados );
			return insertados;

		} catch ( SQLException e ) {
			throw new ContratoException( "No fue posible insertar renglon debido al error: " + e.toString() );
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static int insertaContratoDiverso( Connection conn, ContratoDiverso contrato ) throws ContratoException {
		StringBuilder query = new StringBuilder();

		query.append( "INSERT INTO PCONTRATODIVERSO (cEjercicio  , cIdEntidadContable  , cIdContrato  , cIdTipoDocumento  , cIdTipoMontoDesembolso  , cIdRFC  " + ", cIdTipoContratoDiverso  , cPlazo  , cIdUnidadAdministrativa  , cIdGRegional  , cIdGEstatal  , cIdDistritoRiego  , cIdTipoFondo  , cConceptoContrato  " + ", fDocumento  , cIdTipoAdjudicacion  , fAdjudicacion  , fContratoIni  , fContratoFin  , cIdTipoMoneda  , fVigenciaIVA  , nPorcIVAAplicable  , mImporteContrato  " + ", mImporteHonorarios  , mImporteViaticos  , mImporteBruto  , mImporteIVA  , mImporteTotal  , mContratoMN  , mContratoME  , lRequiereAnticipo  , lRenunciaAnticipo  " + ", cNoOficioRenunciaAnticipo  , cIdUsuarioResponsable  , lHaySaldoAnticipo  , fFirmaContrato  , nPorcImpuestoCedular  , lAplicaImpuestoCedular  , cIdSistemaOrigen  " + ", cIdTipoLimiteDlls  , caNoCompromiso  , cFolioCompromisoSICOP  , id_caso  , lEsPlurianual  , CamInst  , cOrigenRM  , fContratoSolicitud  , fContratoPropuestas  " + ", IEsPasivo  , IEsAbierto  , id_precio  , mOtrosImpuestos  , nEsDescentralizado  , isRadicado  , isConvEjercicioAnt  , cNoProcedimientoCNET  , nCodContratoCNET  " + ", cAprobacionPLU  , nCodExpedienteCNET  , bTieneAnticipo  , lEliminaRetencion6IVA )" );
		query.append( "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
		log.trace( "Insertando Contrato Diverso. Query: " + query );

		PreparedStatement ps = null;

		try {

			log.debug( "Insertando objeto: " + contrato );
			ps = conn.prepareStatement( query.toString() );
			int param = 1;

			ps.setString( param++, contrato.getEjercicioFiscal() );
			ps.setString( param++, contrato.getCentroContable() );
			ps.setString( param++, contrato.getIdContrato() );
			ps.setString( param++, contrato.getIdTipoDocumento() );
			ps.setString( param++, contrato.getIdTipoMontoDesembolso() );
			ps.setString( param++, contrato.getRfc() );
			ps.setString( param++, contrato.getIdTipoContratoDiverso() );
			ps.setString( param++, contrato.getPlazo() );
			ps.setString( param++, contrato.getIdUnidadAdministrativa() );
			ps.setString( param++, contrato.getIdGRegional() );
			ps.setString( param++, contrato.getIdGEstatal() );
			ps.setString( param++, contrato.getIdDistritoRiego() );
			ps.setString( param++, contrato.getIdTipoFondo() );
			ps.setString( param++, contrato.getConceptoContrato() );
			ps.setDate( param++, Util.toSQLDate( contrato.getfDocumento() ) );
			ps.setInt( param++, contrato.getIdTipoAdjudicacion() );
			ps.setDate( param++, Util.toSQLDate( contrato.getfAdjudicacion() ) );
			ps.setDate( param++, Util.toSQLDate( contrato.getfContratoIni() ) );
			ps.setDate( param++, Util.toSQLDate( contrato.getfContratoFin() ) );
			ps.setString( param++, contrato.getIdTipoMoneda() );
			ps.setDate( param++, Util.toSQLDate( contrato.getfVigenciaIVA() ) );
			ps.setDouble( param++, contrato.getPorcIVAAplicable() );
			ps.setBigDecimal( param++, contrato.getmImporteContrato() );
			ps.setBigDecimal( param++, contrato.getmImporteHonorarios() );
			ps.setBigDecimal( param++, contrato.getmImporteViaticos() );
			ps.setBigDecimal( param++, contrato.getmImporteBruto() );
			ps.setBigDecimal( param++, contrato.getmImporteIVA() );
			ps.setBigDecimal( param++, contrato.getmImporteTotal() );
			ps.setBigDecimal( param++, contrato.getmContratoMN() );
			ps.setBigDecimal( param++, contrato.getmContratoME() );
			ps.setString( param++, contrato.isRequiereAnticipo() ? "1" : "0" );
			ps.setString( param++, contrato.isRenunciaAnticipo() ? "1" : "0" );
			ps.setString( param++, contrato.getNoOficioRenunciaAnticipo() );
			ps.setString( param++, contrato.getIdUsuarioResponsable() );
			ps.setString( param++, contrato.isSaldoAnticipo() ? "1" : "0" );
			ps.setDate( param++, Util.toSQLDate( contrato.getfFirmaContrato() ) );
			ps.setDouble( param++, contrato.getPorcImpuestoCedular() );
			ps.setString( param++, contrato.isAplicaImpuestoCedular() ? "1" : "0" );
			ps.setString( param++, "" + contrato.getIdSistemaOrigen() );
			ps.setString( param++, contrato.getIdTipoLimiteDlls() );
			ps.setString( param++, contrato.getCaNoCompromiso() );
			ps.setString( param++, contrato.getFolioCompromisoSICOP() );
			ps.setInt( param++, contrato.getIdCaso() );
			ps.setString( param++, contrato.isPlurianual() ? "1" : "0" );
			ps.setString( param++, contrato.isCamInst() ? "1" : "0" );
			ps.setString( param++, contrato.getOrigenRM() );
			ps.setDate( param++, Util.toSQLDate( contrato.getfContratoSolicitud() ) );
			ps.setDate( param++, Util.toSQLDate( contrato.getfContratoPropuestas() ) );
			ps.setString( param++, contrato.isPasivo() ? "1" : "0" );
			ps.setString( param++, contrato.isAbierto() ? "1" : "0" );
			ps.setInt( param++, contrato.getIdPrecio() );
			ps.setBigDecimal( param++, contrato.getOtrosImpuestos() );
			ps.setString( param++, contrato.isDescentralizado() ? "1" : "0" );
			ps.setString( param++, contrato.isRadicado() ? "1" : "0" );
			ps.setString( param++, contrato.isConvEjercicioAnt() ? "1" : "0" );
			ps.setString( param++, contrato.getNoProcedimientoCNET() );
			ps.setString( param++, contrato.getCodContratoCNET() );
			ps.setString( param++, contrato.getAprobacionPLU() );
			ps.setString( param++, contrato.getCodExpedienteCNET() );
			ps.setString( param++, contrato.isTieneAnticipo() ? "1" : "0" );
			ps.setString( param++, contrato.isEliminaRetencion6IVA() ? "1" : "0" );

			int insertados = ps.executeUpdate();
			return insertados;

		} catch ( SQLException e ) {
			throw new ContratoException( e );
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	/**
	 * Inserta el detalle de EPs que pagara el contrato
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param detalle
	 *            Detalle a insertar
	 * @return Numero de renglones insertados.
	 * @throws ContratoException
	 */
	public static int insertaContratoEP( Connection conn, List<ContratoEP> detalle ) throws ContratoException {

		PreparedStatement ps = null;

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tContratoEP(cEjercicio,cIdContrato,cTipoContrato,EP,cIdEntidadContable)" );
		query.append( "VALUES( ?,?,?,?,?)" );

		int insertados = 0;
		try {
			ps = conn.prepareStatement( query.toString() );

			for ( ContratoEP renglon : detalle ) {
				if ( existeContratoEP( conn, renglon ) )
					continue;
				log.info( "Se ejecutara: [" + query + "]\n" + renglon );

				ps.setString( 1, renglon.getEjercicio() );
				ps.setString( 2, renglon.getIdContrato() );
				ps.setString( 3, renglon.getTipoContrato() );
				ps.setString( 4, renglon.getEp() );
				ps.setString( 5, renglon.getCentroContable() );

				insertados += ps.executeUpdate();
				log.debug( "Se inserto renglon! " + renglon );

				ps.clearParameters();
			}

			log.debug( "Registros insertados (contratoep) " + insertados );
			return insertados;

		} catch ( SQLException e ) {
			throw new ContratoException( "No fue posible insertar renglon debido al error: " + e.toString() );
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static int insertaContratoRetencion( Connection conn, ContratoDiversoRetencion withholding ) throws SQLException {
		PreparedStatement ps = null;

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO pContratoDiversoRetencion (cEjercicio, cIdEntidadContable, cIdContrato, cIdTipoRetencion)" );
		query.append( "VALUES(?,?,?,?)" );
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, withholding.getEjercicio() );
			ps.setString( 2, withholding.getCentroContable() );
			ps.setString( 3, withholding.getIdContrato() );
			ps.setInt( 4, withholding.getIdTipoRetencion() );

			log.info( "Ejecutando [" + query.toString() + "]\n" + withholding );
			ps.executeUpdate();
			ps.clearParameters();
			return 1;
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static int insertaContratoRetencion( Connection conn, List<ContratoDiversoRetencion> retenciones ) throws ContratoException {

		if ( retenciones != null ) {
			int insertados = 0;

			try {

				for ( ContratoDiversoRetencion retencion : retenciones ) {
					insertaContratoRetencion( conn, retencion );
					insertados++;
				}

				log.debug( "Registros insertados (Retenciones) " + insertados );
				return insertados;

			} catch ( SQLException e ) {
				throw new ContratoException( "No fue posible insertar renglon debido al error: " + e.toString() );
			}
		}
		return 0;
	}

	public static int insertaJustificacionCnet( Connection conn, ContratoDiverso contrato ) throws ContratoException {
		PreparedStatement ps = null;
		int insertados = 0;
		try {
			ps = conn.prepareStatement( "INSERT INTO tContratosJustCNET VALUES ( ?, 'DI', 'CONVENIO COLABORACIÓN')" );
			ps.setString( 1, contrato.getIdContrato() );

			insertados = ps.executeUpdate();

			return insertados;

		} catch ( SQLException e ) {
			throw new ContratoException( e );
		} finally {
			CloseObject.closeObject( ps );

		}
	}

	private static ContratoDiverso mapToContratoDiverso( Connection conn, ResultSet rs ) throws SQLException {
		ContratoDiverso diverseContract = new ContratoDiverso();

		diverseContract.setAbierto( "1".equals( rs.getString( "IEsAbierto" ) ) );
		diverseContract.setAnticipo( ContratoDiversoManager.selectContractAdvance( conn, rs.getString( "cIdContrato" ) ) );
		diverseContract.setAplicaImpuestoCedular( "1".equals( rs.getString( "lAplicaImpuestoCedular" ) ) );
		diverseContract.setAprobacionPLU( rs.getString( "cAprobacionPLU" ) );
		diverseContract.setCamInst( "1".equals( rs.getString( "CamInst" ) ) );
		diverseContract.setCaNoCompromiso( rs.getString( "caNoCompromiso" ) );
		diverseContract.setCentroContable( rs.getString( "cIdEntidadContable" ) );
		diverseContract.setCodContratoCNET( rs.getString( "nCodContratoCNET" ) );
		diverseContract.setCodExpedienteCNET( rs.getString( "nCodExpedienteCNET" ) );
		diverseContract.setConceptoContrato( rs.getString( "cConceptoContrato" ) );
		diverseContract.setConvEjercicioAnt( "1".equals( rs.getString( "isConvEjercicioAnt" ) ) );
		diverseContract.setDescentralizado( "1".equals( rs.getString( "nEsDescentralizado" ) ) );
		diverseContract.setDetalleEP( ContratoDiversoManager.selectDetail( conn, rs.getString( "cIdContrato" ) ) );
		diverseContract.setEjercicioFiscal( rs.getString( "cEjercicio" ) );
		diverseContract.setEliminaRetencion6IVA( "1".equals( rs.getString( "lEliminaRetencion6IVA" ) ) );

		diverseContract.setfAdjudicacion( getDate( rs, "fAdjudicacion" ) );
		diverseContract.setfContratoFin( getDate( rs, "fContratoFin" ) );
		diverseContract.setfContratoIni( getDate( rs, "fContratoIni" ) );
		diverseContract.setfContratoPropuestas( getDate( rs, "fContratoPropuestas" ) );
		diverseContract.setfContratoSolicitud( getDate( rs, "fContratoSolicitud" ) );
		diverseContract.setfDocumento( getDate( rs, "fDocumento" ) );
		diverseContract.setfFirmaContrato( getDate( rs, "fFirmaContrato" ) );
		diverseContract.setFolioCompromisoSICOP( rs.getString( "cFolioCompromisoSICOP" ) );
		diverseContract.setfVigenciaIVA( getDate( rs, "fVigenciaIVA" ) );

		diverseContract.setIdCaso( rs.getInt( "id_caso" ) );
		diverseContract.setIdContrato( rs.getString( "cIdContrato" ) );
		diverseContract.setIdDistritoRiego( rs.getString( "cIdDistritoRiego" ) );
		diverseContract.setIdGEstatal( rs.getString( "cIdGEstatal" ) );
		diverseContract.setIdGRegional( rs.getString( "cIdGRegional" ) );
		diverseContract.setIdPrecio( rs.getInt( "id_precio" ) );

		String idSistemaOrigen = rs.getString( "cIdSistemaOrigen" );
		if ( idSistemaOrigen != null ) {
			diverseContract.setIdSistemaOrigen( idSistemaOrigen.charAt( 0 ) );
		}

		diverseContract.setIdTipoAdjudicacion( rs.getInt( "cIdTipoAdjudicacion" ) );
		diverseContract.setIdTipoContratoDiverso( rs.getString( "cIdTipoContratoDiverso" ) );
		diverseContract.setIdTipoDocumento( rs.getString( "cIdTipoDocumento" ) );
		diverseContract.setIdTipoFondo( rs.getString( "cIdTipoFondo" ) );
		diverseContract.setIdTipoLimiteDlls( rs.getString( "cIdTipoLimiteDlls" ) );
		diverseContract.setIdTipoMoneda( rs.getString( "cIdTipoMoneda" ) );
		diverseContract.setIdTipoMontoDesembolso( rs.getString( "cIdTipoMontoDesembolso" ) );
		diverseContract.setIdUnidadAdministrativa( rs.getString( "cIdUnidadAdministrativa" ) );
		diverseContract.setIdUsuarioResponsable( rs.getString( "cIdUsuarioResponsable" ) );

		diverseContract.setmContratoME( rs.getBigDecimal( "mContratoME" ) );
		diverseContract.setmContratoMN( rs.getBigDecimal( "mContratoMN" ) );
		diverseContract.setmImporteBruto( rs.getBigDecimal( "mImporteBruto" ) );
		diverseContract.setmImporteContrato( rs.getBigDecimal( "mImporteContrato" ) );
		diverseContract.setmImporteHonorarios( rs.getBigDecimal( "mImporteHonorarios" ) );
		diverseContract.setmImporteIVA( rs.getBigDecimal( "mImporteIVA" ) );
		diverseContract.setmImporteTotal( rs.getBigDecimal( "mImporteTotal" ) );
		diverseContract.setmImporteViaticos( rs.getBigDecimal( "mImporteViaticos" ) );

		diverseContract.setNoOficioRenunciaAnticipo( rs.getString( "cNoOficioRenunciaAnticipo" ) );
		diverseContract.setNoProcedimientoCNET( rs.getString( "cNoProcedimientoCNET" ) );
		diverseContract.setOrigenRM( rs.getString( "cOrigenRM" ) );
		diverseContract.setOtrosImpuestos( rs.getBigDecimal( "mOtrosImpuestos" ) );
		diverseContract.setPasivo( "1".equals( rs.getString( "IEsPasivo" ) ) );
		diverseContract.setPlazo( rs.getString( "cPlazo" ) );
		diverseContract.setPlurianual( "1".equals( rs.getString( "lEsPlurianual" ) ) );
		diverseContract.setPorcImpuestoCedular( rs.getDouble( "nPorcImpuestoCedular" ) );
		diverseContract.setPorcIVAAplicable( rs.getDouble( "nPorcIVAAplicable" ) );
		diverseContract.setRadicado( "1".equals( rs.getString( "isRadicado" ) ) );
		diverseContract.setRenunciaAnticipo( "1".equals( rs.getString( "lRenunciaAnticipo" ) ) );
		diverseContract.setRequiereAnticipo( "1".equals( rs.getString( "lRequiereAnticipo" ) ) );
		diverseContract.setRetenciones( ContratoDiversoManager.selectContractWithholdings( conn, rs.getString( "cIdContrato" ) ) );
		diverseContract.setRfc( rs.getString( "cIdRFC" ) );
		diverseContract.setSaldoAnticipo( "1".equals( rs.getString( "lHaySaldoAnticipo" ) ) );
		diverseContract.setTieneAnticipo( "1".equals( rs.getString( "bTieneAnticipo" ) ) );

		return diverseContract;
	}

	public static void saveWithholdingEliminated( Connection conn, String contractId, String login, int idWithholdingEliminated, String justification ) throws SQLException {

		log.info( "Registering deleted withholding: [" + contractId + "," + login + "," + idWithholdingEliminated + "," + justification + "]" );

		StringBuilder queryInsert = new StringBuilder();
		queryInsert.append( " INSERT INTO tlog_withholding_eliminated( " );
		queryInsert.append( " 			contract_id " );
		queryInsert.append( "            ,withholding_id " );
		queryInsert.append( "            ,justification " );
		queryInsert.append( "            ,login) " );
		queryInsert.append( "      VALUES( " );
		queryInsert.append( " 		   ?, " );
		queryInsert.append( "            ?, " );
		queryInsert.append( "            ?, " );
		queryInsert.append( "            ? " );
		queryInsert.append( " 		   ) " );

		log.trace( "Query: [" + queryInsert + "]" );
		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement( queryInsert.toString() );
			ps.setString( 1, contractId );
			ps.setInt( 2, idWithholdingEliminated );
			ps.setString( 3, justification );
			ps.setString( 4, login );

			int inserted = ps.executeUpdate();
			log.debug( "Inserted " + inserted + " withholding deleted" );
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static ContratoDiverso select( Connection conn, String contractId ) throws ContratoException {
		String query = "SELECT * FROM PCONTRATODIVERSO WITH(NOLOCK) WHERE cIdContrato = ?";

		try ( PreparedStatement ps = conn.prepareStatement( query ) ) {
			ps.setString( 1, contractId );
			log.trace( "Looking for diverse contract. \n[" + query + "]\n[" + contractId + "]" );

			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					return mapToContratoDiverso( conn, rs );
				}
			}
		} catch ( SQLException e ) {
			log.error( "Error retrieving contract [" + contractId + "]: " + e, e );
			throw new ContratoException( e );
		}

		return null;
	}

	public static List<ContratoDiverso> selectBySupplier( Connection conn, String rfcEmisor ) throws SQLException {
		String query = "SELECT * FROM PCONTRATODIVERSO WITH(NOLOCK) WHERE replace(cIdRFC,'-','') = replace(?,'-','') AND bContratoVigenteParaPago = 1 ";
		List<ContratoDiverso> contratos = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, rfcEmisor );

			log.trace( "Executing query for supplier RFC: " + rfcEmisor );
			rs = ps.executeQuery();

			while ( rs.next() ) {
				contratos.add( mapToContratoDiverso( conn, rs ) );
			}
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}

		return contratos;
	}

	private static ContratoDiversoAnticipo selectContractAdvance( Connection conn, String contractId ) throws SQLException {
		StringBuilder query = new StringBuilder( "SELECT * FROM pContratoDiversoAnticipo WHERE cIdContrato = ?" );
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, contractId );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				ContratoDiversoAnticipo contractAdvance = new ContratoDiversoAnticipo();
				contractAdvance.setCentroContable( rs.getString( "cIdEntidadContable" ) );
				contractAdvance.setEjercicio( rs.getString( "cEjercicio" ) );
				contractAdvance.setfAnticipo( new Date( rs.getDate( "fAnticipo" ).getTime() ) );
				contractAdvance.setIdContrato( rs.getString( "cIdContrato" ) );
				contractAdvance.setIdTipoAnticipoDiverso( rs.getInt( "cIdTipoAnticipoDiverso" ) );
				contractAdvance.setImporteAmortizado( rs.getBigDecimal( "mAmortizado" ) );
				contractAdvance.setImporteAnticipo( rs.getBigDecimal( "mImporteAnticipo" ) );
				contractAdvance.setImporteAnticipoIVA( rs.getBigDecimal( "mImporteAnticipoIVA" ) );
				contractAdvance.setPorcAmortizacion( rs.getInt( "nPorcAmortizacion" ) );
				contractAdvance.setPorcAsignacion( rs.getDouble( "nPorcAsignacion" ) );
				contractAdvance.setTotalAnticipo( rs.getBigDecimal( "mTotalAnticipo" ) );

				return contractAdvance;
			}
			return null;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	public static List<ContratoDiversoRetencion> selectContractWithholdings( Connection conn, String contractId ) throws SQLException {

		List<ContratoDiversoRetencion> withholdingList = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT * FROM pContratoDiversoRetencion WITH(NOLOCK) WHERE cIdContrato = ?" );

		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setString( 1, contractId );

			rs = ps.executeQuery();
			while ( rs.next() ) {
				ContratoDiversoRetencion withholding = new ContratoDiversoRetencion();
				withholding.setCentroContable( rs.getString( "cIdEntidadContable" ) );
				withholding.setEjercicio( rs.getString( "cEjercicio" ) );
				withholding.setIdContrato( rs.getString( "cIdContrato" ) );
				withholding.setIdTipoRetencion( rs.getInt( "cIdTipoRetencion" ) );
			}
			return withholdingList;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private static List<ContratoEP> selectDetail( Connection conn, String contractId ) throws SQLException {

		StringBuilder query = new StringBuilder( "SELECT * FROM tContratoEP WHERE cTipoContrato = 'DI' AND cIdContrato = ?" );
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, contractId );

			rs = ps.executeQuery();

			List<ContratoEP> epList = new ArrayList<>();

			while ( rs.next() ) {

				ContratoEP contractEP = new ContratoEP();
				contractEP.setCentroContable( rs.getString( "cIdEntidadContable" ) );
				contractEP.setEjercicio( rs.getString( "cEjercicio" ) );
				contractEP.setEp( rs.getString( "EP" ) );
				contractEP.setIdContrato( rs.getString( "cIdContrato" ) );
				contractEP.setTipoContrato( rs.getString( "cTipoContrato" ) );

				epList.add( contractEP );

			}
			return epList;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

}
