package com.syc.obrapublica.core.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.DatosEstimacionObra;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.exceptions.EstimacionObraException;

public class FirmaAutorizacionObraManager {
	private static final Logger	log	= LogManager.getLogger( FirmaAutorizacionObraManager.class );
	public static void saveAtentaNota(Connection conn, DatosEstimacionObra estimacion)throws SQLException {
		StringBuilder query = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			query = new StringBuilder();
			query.append( "INSERT INTO tNotaAutorizaEstimacion( " );
			query.append( "	cIDContrato, " );
			query.append( "	nIdEstimacion, " );
			query.append( "	cFolioNota, " );
			query.append( "	cNumeroEmpleado, " );
			query.append( "	dMotivoNota, " );
			query.append( "	cFoliobra," );
			query.append( "	cIDUsuarioCaptura," );
			query.append( "	cNumeroEmpleadoJefe," );
			query.append( "	cNumeroEmpleadoSubgerente" );
			query.append( ")values( " );
			query.append( "	?,  " );
			query.append( "	?, " );
			query.append( "	?,  " );
			query.append( "	?,  " );
			query.append( "	?,  " );
			query.append( "	?,  " );
			query.append( "	?,  " );
			query.append( "	?,  " );
			query.append( "	?  )" );
			ps = conn.prepareStatement( query.toString(), Statement.RETURN_GENERATED_KEYS );
			ps.setString( 1, estimacion.getContratoCNET() );
			ps.setInt( 2, estimacion.getnEstimacion() );
			ps.setString( 3, estimacion.getFolioNota() );
			ps.setInt( 4, estimacion.getNumeroEmpleado() );
			ps.setString( 5, estimacion.getMotivoAutorizacion() );
			ps.setString( 6, estimacion.getcFolioObra() );
			ps.setString( 7, estimacion.getcLogin() );
			ps.setInt( 8, estimacion.getNumeroEmpleadoJefe() );
			ps.setInt( 9, estimacion.getNumeroEmpleadoSubgerente() );
			int insertados = ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			estimacion.setIdNota( rs.getInt( 1 ) );

			log.info( "Se insertaron: " + insertados + " solicitudes de autorizacion de estimación de obra pública" );
		} finally {
			CloseObject.closeObject( ps );
			query = null;
			rs = null;
		}
	}
	public static int avanzaEstatus( Connection conn, DatosEstimacionObra estimacion ) throws SQLException {

		log.info( "Cambiando estatus a: " + estimacion.getIdEstatusEstimacion() + " en la estimación de obra pública: " + estimacion );
		StringBuilder sb = new StringBuilder();
		sb.append( "update tobrapublicaavancefisico set nIdEstatus=? where ccvecontrato=? and foliosai=? and noestimacion=?" );
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setInt( 1, estimacion.getIdEstatusEstimacion() );
			ps.setString( 2, estimacion.getContratoCNET() );
			ps.setString( 3, estimacion.getcFolioObra() );
			ps.setInt( 4, estimacion.getnEstimacion() );

			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
		}

	}
	public static String getConceptoContrato( Connection conn,String cContratoCNET ) throws EstimacionObraException {

		String objetoContrato = null;

		StringBuilder query = new StringBuilder();
		query.append( "SELECT 'del contrato '+ cidcontrato + ' a favor de: ' + cIdRFC " );
		query.append( "       + ' por el concepto de <p><i>' " );
		query.append( "       + cObjetoContrato+ '</i></p><br>' " );
		query.append( "FROM   pContratoObra with(Nolock) " );
		query.append( "WHERE  cIdContrato = ?   " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, cContratoCNET);

			rs = ps.executeQuery();
			if ( rs.next() ) {
				objetoContrato = rs.getString( 1 );
			} else
				throw new EstimacionObraException( "No se encontro informacion para el contrato." );

			return objetoContrato;
		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de base de datos mientras se buscaba el objeto del contrato: " + e, e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}
	public static DatosEstimacionObra read( Connection conn, int folioNota ) throws SQLException, ParseException {

		StringBuilder query = new StringBuilder();
		query.append( "select foliosai, noestimacion " );
		query.append( ",obra.cIdRFC+' '+prov.cRazonSocial beneficiario " );
		query.append( ",obra.cIdContrato cnocontratocnet " );
		query.append( ",nota.cFolioNota ,convert(varchar,dfechanota,103) dFechaNota " );
		query.append( ",obra.cObjetoContrato as cConceptoContrato " );
		query.append( ",nota.dMotivoNota,nota.cIDUsuarioCaptura " );
		query.append( ",avance.nIdEstatus idEstatusEst " );
		query.append( ",estatus.cEstatus as estatus" );
		query.append( ",case when avance.nIdEstatus=2 then nota.cNumeroEmpleado  " );
		query.append( " when avance.nIdEstatus=5 then nota.cNumeroEmpleadoJefe" );
		query.append( " when avance.nIdEstatus=6 then nota.cNumeroEmpleadoSubgerente  else nota.cNumeroEmpleado end	cNumeroEmpleado " );
		query.append( ",nota.cNumeroEmpleadoJefe,nota.cNumeroEmpleadoSubgerente " );
		query.append( "from pContratoObra as obra with(Nolock) " );
		query.append( "inner join tobrapublicaavancefisico as avance with(Nolock) " );
		query.append( "on avance.ccvecontrato=obra.cIdContrato " );
		query.append( "inner join tNotaAutorizaEstimacion nota with(Nolock) " );
		query.append( "on nota.cIDContrato=obra.cIdContrato " );
		query.append( " and nota.nIdEstimacion=avance.noestimacion " );
		query.append( "inner join tEstatusAvanceFisico estatus with(Nolock) " );
		query.append( "on estatus.nIdEstatus=avance.nIdEstatus " );
		query.append( "inner join mCatalogoProveedor as prov with(Nolock) " );
		query.append( "on replace(prov.cIdRFC,'-','')=obra.cIdRFC " );
		query.append( "where nota.nIDNota=? " );

		PreparedStatement ps = null;
		ResultSet rs = null;
		String [] arrayFolio=null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folioNota );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				arrayFolio=rs.getString( "foliosai" ).split( "-" );
				DatosEstimacionObra est = new DatosEstimacionObra();
				est.setcFolioObra( rs.getString( "foliosai" ) );
				est.setcLogin( rs.getString( "cIDUsuarioCaptura" ) );
				est.setContratoCNET( rs.getString( "cnocontratocnet" ) );
				est.setFechaNota( Util.stringToDate( rs.getString( "dFechaNota" ), "dd/MM/yyyy" ) );
				est.setFolioNota( rs.getString( "cFolioNota" ) );
				est.setIdEstatusEstimacion( rs.getInt( "idEstatusEst" ) );
				est.setIdNota( folioNota );
				est.setMotivoAutorizacion( rs.getString( "dMotivoNota" ) );
				est.setnEstimacion( rs.getInt( "noestimacion" ));
				est.setnFolioObra( arrayFolio[2] );
				est.setNumeroEmpleado( rs.getInt( "cNumeroEmpleado" ) );
				est.setNumeroEmpleadoJefe( rs.getInt( "cNumeroEmpleadoJefe" ) );
				est.setNumeroEmpleadoSubgerente( rs.getInt( "cNumeroEmpleadoSubgerente" ) );
				
				return est;

			} else {
				throw new SQLException( "No se encontro la Nota de autorizacion con folio : " + folioNota );
			}

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
			query=null;
		}

	}
	public static int getMinimoNoEstimacion( Connection conn, DatosEstimacionObra estimacion ) throws SQLException, EstimacionObraException {
		StringBuilder query = new StringBuilder();
		query.append( "select MAX(noestimacion)minimoNoEstimacion from tobrapublicaavancefisico with(Nolock) where ccvecontrato=?  and noestimacion<=0" );
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, estimacion.getContratoCNET() );
			rs = ps.executeQuery();
			if ( rs.next() )
				return rs.getInt( "minimoNoEstimacion" );
			else
				throw new EstimacionObraException( "No se encontro informaci\u00f3n de estimaciones para el contrato " +estimacion.getContratoCNET()  );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}
	public static void updateIdEstimacionAtentaNota(Connection conn, DatosEstimacionObra estimacion)throws SQLException {
		StringBuilder query = null;
		PreparedStatement ps = null;
		try {
			query = new StringBuilder();
			query.append( "update tNotaAutorizaEstimacion set nIdEstimacion=? where cIDContrato=? and cFolioNota=? and nIdEstimacion=?" );
			
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, estimacion.getnEstimacionCancelado() );
			ps.setString( 2, estimacion.getContratoCNET() );
			ps.setString( 3, estimacion.getFolioNota() );
			ps.setInt( 4, estimacion.getnEstimacion());
			
			ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
			query = null;
		}
	}
	public static void updateFolioAtentaNota(Connection conn, DatosEstimacionObra estimacion)throws SQLException {
		StringBuilder query = null;
		PreparedStatement ps = null;
		try {
			query = new StringBuilder();
			query.append( "update tNotaAutorizaEstimacion set cFolioNota=? where cIDContrato=? and cFolioNota=? " );
			
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, estimacion.getFolioNotaCancelada() );
			ps.setString( 2, estimacion.getContratoCNET() );
			ps.setString( 3, estimacion.getFolioNota() );
			
			ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
			query = null;
		}
	}
	public static void updateIdEstimacionAvanceFisico(Connection conn, DatosEstimacionObra estimacion)throws SQLException {
		StringBuilder query = null;
		PreparedStatement ps = null;
		try {
			query = new StringBuilder();
			query.append( "update tobrapublicaavancefisico set noestimacion=? where ccvecontrato=? and foliosai=? and noestimacion=?" );
			
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, String.valueOf( estimacion.getnEstimacionCancelado()) );
			ps.setString( 2, estimacion.getContratoCNET() );
			ps.setString( 3, estimacion.getcFolioObra());
			ps.setInt( 4, estimacion.getnEstimacion());
			
			ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
			query = null;
		}
	}
}
