package com.syc.sai.procesos;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.util.Util;


public class ActualizaCtab {

	public static void main( String[] args ) throws Exception {
		updateGeneral();
		//updateRFCSinHomoclabe();

	}

	private static void updateRFCSinHomoclabe() throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "select nEdoCtaDet, nEdoCta, cDescripcion  from tEdoCtaDetalleAmp where mAbonos > 0 AND (cRFC IS NULL OR cRFC = '') " );

		StringBuilder queryUpdate = new StringBuilder( "UPDATE tEdoCtaDetalleAmp SET cRFC=? WHERE nEdoCtaDet = ? " );
		Connection conn = null;

		PreparedStatement psSelect = null;
		PreparedStatement psUpdate = null;

		ResultSet rs = null;

		Pattern patternRFC = Pattern.compile( "\\s([A-ZÑ&]{3,4}) ?(?:- ?)?(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01]))\\s{0,1},{0,1}", Pattern.CASE_INSENSITIVE );

		try {

			conn = Util.getStandAloneConnection();

			psSelect = conn.prepareStatement( query.toString() );
			psUpdate = conn.prepareStatement( queryUpdate.toString() );

			rs = psSelect.executeQuery();

			while ( rs.next() ) {

				int nEdoCtaDet = rs.getInt( "nEdoCtaDet" );
				int nEdoCta = rs.getInt( "nEdoCta" );
				String rfc = "";

				Matcher matcherRFC = patternRFC.matcher( rs.getString( "cDescripcion" ) );

				if ( matcherRFC.find() )
					rfc = matcherRFC.group().replace( ',', ' ' ).trim();

				System.out.println( "nEdoCtaDet[" + nEdoCtaDet + "] nEdoCta[" + nEdoCta + "] RFC[" + rfc + "]" );
				psUpdate.setString( 1, rfc );
				psUpdate.setInt( 2, nEdoCtaDet );
				int actualizados = psUpdate.executeUpdate();
				if ( actualizados > 1 )
					throw new Exception( "El folio: " + "nEdoCtaDet[" + nEdoCtaDet + "] nEdoCta[" + nEdoCta + "] afecto mas de un registro: " + actualizados );

			}

			conn.commit();

		} catch ( Exception e ) {
			e.printStackTrace();

			try {
				conn.rollback();
			} catch ( Exception e2 ) {
				System.out.println( "Problemas en rollback " + e2 );
			}
			throw e;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psUpdate );
			CloseObject.closeObject( psSelect );
			CloseObject.closeObject( conn );
		}
	}

	private static void updateGeneral() throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "select nEdoCtaDet, nEdoCta, cDescripcion  from tEdoCtaDetalleAmp where mAbonos > 0  AND (cRFC IS NULL OR cRFC = '') " );

		StringBuilder queryUpdate = new StringBuilder( "UPDATE tEdoCtaDetalleAmp SET cRFC=?,cCTAB=? WHERE nEdoCtaDet = ? " );
		Connection conn = null;
		PreparedStatement psSelect = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;

		Pattern patternCLABE = Pattern.compile( "CLABE:\\s\\d{18}", Pattern.CASE_INSENSITIVE );
		Pattern patternCUENTA = Pattern.compile( "CUENTA:{0,1}\\s\\d{10}", Pattern.CASE_INSENSITIVE );
		Pattern patternRFC = Pattern.compile( "\\s([A-ZÑ&]{3,4}) ?(?:- ?)?(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])) ?(?:- ?)?([A-Z\\d]{2})([A\\d])\\s{0,1},{0,1}", Pattern.CASE_INSENSITIVE );

		try {

			conn = Util.getStandAloneConnection();

			psSelect = conn.prepareStatement( query.toString() );
			psUpdate = conn.prepareStatement( queryUpdate.toString() );

			rs = psSelect.executeQuery();

			while ( rs.next() ) {

				int nEdoCtaDet = rs.getInt( "nEdoCtaDet" );
				int nEdoCta = rs.getInt( "nEdoCta" );
				String ctab = "";
				String rfc = "";

				Matcher matcher = patternCLABE.matcher( rs.getString( "cDescripcion" ) );
				Matcher matcherCuenta = patternCUENTA.matcher( rs.getString( "cDescripcion" ) );
				Matcher matcherRFC = patternRFC.matcher( rs.getString( "cDescripcion" ) );

				if ( matcher.find() ) {
					ctab = matcher.group().substring( 7 );
				} else if ( matcherCuenta.find() )
					ctab = matcherCuenta.group().substring( 8 );
				if ( matcherRFC.find() )
					rfc = matcherRFC.group().replace( ',', ' ' ).trim();

				System.out.println( "nEdoCtaDet[" + nEdoCtaDet + "] nEdoCta[" + nEdoCta + "] CTAB[" + ctab + "] RFC[" + rfc + "]" );
				psUpdate.setString( 1, rfc );
				psUpdate.setString( 2, ctab );
				psUpdate.setInt( 3, nEdoCtaDet );
				int actualizados = psUpdate.executeUpdate();
				if ( actualizados > 1 )
					throw new Exception( "El folio: " + "nEdoCtaDet[" + nEdoCtaDet + "] nEdoCta[" + nEdoCta + "] afecto mas de un registro: " + actualizados );

			}

			conn.commit();

		} catch ( Exception e ) {
			e.printStackTrace();

			try {
				conn.rollback();
			} catch ( Exception e2 ) {
				System.out.println( "Problemas en rollback " + e2 );
			}
			throw e;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psUpdate );
			CloseObject.closeObject( psSelect );
			CloseObject.closeObject( conn );
		}
	}
}
