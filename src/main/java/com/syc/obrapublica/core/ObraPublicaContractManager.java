package com.syc.obrapublica.core;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ObraPublicaContractManager {

	private static Logger log = Logger.getLogger( ObraPublicaContractManager.class );

	public static byte SaveApartado( Connection conn, ObraPublicaContract opc, String[] listHeader, String[][] listDetail ) throws SQLException {

		byte retval = -1;

		String query = "{call ManteObraPublicaApartadoHeader (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		CallableStatement cs = null;

		cs = conn.prepareCall( query );
		// Comienza llenado de parametros de SP
		cs.setByte( 1, Byte.parseByte( "1" ) ); // Opción
		cs.registerOutParameter( 2, Types.INTEGER );

		cs.setString( 3, listHeader[0] ); // Folio
		cs.setString( 4, listHeader[1] ); // Cartera
		cs.setString( 5, listHeader[2] ); // OLI
		cs.setFloat( 6, Float.parseFloat( listHeader[3] ) ); // monto
		cs.setFloat( 7, Float.parseFloat( listHeader[4] ) ); // porcentaje IVA
		cs.setFloat( 8, Float.parseFloat( listHeader[5] ) ); // Monto con IVA
		cs.setString( 9, listHeader[6] ); // Tipo obra
		cs.setString( 10, listHeader[7] ); // Tipo recurso
		cs.setString( 11, listHeader[8] ); // Descripcion
		cs.setByte( 12, Byte.parseByte( listHeader[9] ) ); // Status
		cs.setString( 13, listHeader[10] ); // Fecha de aplicacion
		cs.setString( 14, listHeader[11] ); // Ejercicio fiscal
		cs.setString( 15, listHeader[12] ); // Login
		cs.setString( 16, listHeader[13] ); // Ramo
		cs.setString( 17, listHeader[14] ); // Unidad responsable
		cs.setString( 18, listHeader[15] ); // DocumentoAplicado
		cs.setInt( 19, Integer.parseInt( listHeader[16] ) ); // Folio poliza
		cs.setString( 20, listHeader[17] ); // Tipo poliza
		cs.setInt( 21, Integer.parseInt( listHeader[18] ) ); // Folio poliza
																// cancelacion
		cs.setString( 22, listHeader[19] ); // fecha cancelacion

		// Termina llenado

		// Call the inherited PreparedStatement.executeUpdate( ) method
		// Código que estaba
		try {
			// ResultSet rs = cs.executeQuery();
			cs.execute();
			int idApartadoHeader = cs.getInt( 2 );
			opc.setIdApartadoHeader( idApartadoHeader );
			// Si existen registros en la tabla detalle se eliminan

			cs = null;

			String query2 = "EXEC ManteObraPublicaApartadoDetail ?,? output,?,?,?,?,?,?,?,? ";
			cs = conn.prepareCall( query2 );
			cs.clearParameters();
			cs.setByte( 1, Byte.parseByte( "9" ) ); // Opción borrar
			cs.registerOutParameter( 2, Types.INTEGER ); // Id de la llave
															// primaria de la
															// tabla
															// detalle

			// cs.setNull(2, Sql.INTEGER); //.setInt(2,0); // Se manda cero, ya
			// que no importa este valor para opcion dos
			cs.setInt( 3, opc.getIdApartadoHeader() ); // IdHeader

			cs.setString( 4, "" ); // EP, pero no importa su valor
			cs.setByte( 5, Byte.parseByte( "0" ) ); // Mes
			cs.setFloat( 6, 0 ); // importe, pero no importa su valor
			cs.setString( 7, "" ); // Evento, pero no importa su valor
			cs.setFloat( 8, 0 ); // importe negativo, pero no importa su valor
			cs.setString( 9, "" ); // Centro contable, pero no importa su valor
			cs.setInt( 10, 0 ); // DocRenglon , pero no importa su valor

			// Call the inherited PreparedStatement.executeUpdate( ) method
			cs.execute();

			// Termina el borrado de detalle

			// Inserta detalle
			opc.listEvent.clear();

			cs = null;

			// Create an instance of the CallableStatement
			String query3 = "EXEC ManteObraPublicaApartadoDetail ?,? output,?,?,?,?,?,?,?,? ";
			cs = conn.prepareCall( query3 );

			for ( int i = 0; i < listDetail.length; i++ ) {
				cs.clearParameters();
				cs.setByte( 1, Byte.parseByte( "1" ) ); // Opción
				cs.registerOutParameter( 2, Types.INTEGER );

				cs.setInt( 3, opc.getIdApartadoHeader() ); // IdHeader

				cs.setString( 4, listDetail[i][0] ); // EP
				cs.setByte( 5, Byte.parseByte( listDetail[i][1] ) ); // Mes
				cs.setFloat( 6, Float.parseFloat( listDetail[i][2] ) ); // importe
				cs.setString( 7, listDetail[i][3] ); // Evento
				cs.setFloat( 8, Float.parseFloat( listDetail[i][4] ) ); // importe
																		// negativo
				cs.setString( 9, listDetail[i][5] ); // Centro contable
				cs.setInt( 10, Integer.parseInt( listDetail[i][6] ) ); // DocRenglon

				// Call the inherited PreparedStatement.executeUpdate( ) method
				cs.execute();

				// Validar si evento existe en la lista

				if ( !opc.listEvent.contains( listDetail[i][3] ) ) {
					opc.listEvent.add( listDetail[i][3] );
				}

			}
			// verifica aplicar motor contable
			retval = 0;
		} finally {
			if ( cs != null )
				cs.close();

			cs = null;

		}
		return retval;
	}

	public static byte ApplyApartado( Connection conn, ObraPublicaContract opc ) {
		byte retVal = -1;
		try {
			AccountingEngine accEng = new AccountingEngine();
			for ( String evento : opc.listEvent ) {
				accEng.makeAccountingApplication( conn, evento, Integer.toString( opc.getIdApartadoHeader() ), "tObraPublicaApartadoEncabezado", "tObraPublicaApartadoDetalle", "nFolioOPAHeader" );
			}

			retVal = 0;
		} catch ( AccountingEngineException e ) {
			log.error( e, e );
		}
		return retVal;
	}

	public static byte SavePreCompromiso( Connection conn, ObraPublicaContract opc, String[] listHeader, String[][] listDetail ) throws SQLException {

		byte retval = -1;

		String query = "{call dbo.ManteObraPublicaPreComprHeader (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		CallableStatement cs = null;

		cs = conn.prepareCall( query );
		// Comienza llenado de parametros de SP
		cs.setByte( 1, Byte.parseByte( "1" ) ); // Opción salvar
		cs.registerOutParameter( 2, Types.INTEGER );

		cs.setString( 3, listHeader[0] ); // Folio SAI
		cs.setString( 4, listHeader[1] ); // Cartera
		cs.setString( 5, listHeader[2] ); // OLI
		cs.setFloat( 6, Float.parseFloat( listHeader[3] ) ); // monto
		cs.setFloat( 7, Float.parseFloat( listHeader[4] ) ); // porcentaje IVA
		cs.setFloat( 8, Float.parseFloat( listHeader[5] ) ); // Monto con IVA
		cs.setString( 9, listHeader[6] ); // Tipo obra
		cs.setString( 10, listHeader[7] ); // Tipo recurso
		cs.setString( 11, listHeader[8] ); // Descripcion
		cs.setString( 12, listHeader[9] ); // Nombre de convocatoria
		cs.setString( 13, listHeader[10] ); // Fecha de convocatoria

		cs.setString( 14, listHeader[11] ); // Fecha de junta de aclaración
		cs.setString( 15, listHeader[12] ); // Fecha de recepcion de propuesta
		cs.setString( 16, listHeader[13] ); // Fecha de fallo
		cs.setByte( 17, Byte.parseByte( listHeader[14] ) );
		cs.setFloat( 18, Float.parseFloat( listHeader[15] ) ); // porcentaje
																// anticipo

		cs.setByte( 19, Byte.parseByte( listHeader[16] ) ); // Status
		cs.setString( 20, listHeader[17] ); // Fecha de aplicacion
		cs.setString( 21, listHeader[18] ); // Ejercicio fiscal
		cs.setString( 22, listHeader[19] ); // Login
		cs.setString( 23, listHeader[20] ); // Ramo
		cs.setString( 24, listHeader[21] ); // Unidad responsable
		cs.setString( 25, listHeader[22] ); // DocumentoAplicado
		cs.setInt( 26, Integer.parseInt( listHeader[23] ) ); // Folio poliza
		cs.setString( 27, listHeader[24] ); // Tipo poliza
		cs.setInt( 28, Integer.parseInt( listHeader[25] ) ); // Folio poliza
																// cancelacion
		cs.setString( 29, listHeader[26] ); // fecha cancelacion

		// Termina llenado

		// Call the inherited PreparedStatement.executeUpdate( ) method
		// Código que estaba
		try {
			// ResultSet rs = cs.executeQuery();
			cs.execute();
			int idPreCompromisoHeader = cs.getInt( 2 );
			opc.setIdPreCompromisoHeader( idPreCompromisoHeader );

			// Elimina registro de detalle asociados al header

			cs = null;

			// Create an instance of the CallableStatement
			String query2 = "EXEC ManteObraPublicaPreComprDetail ?,? output,?,?,?,?,?,?,?,?,?,?";
			cs = conn.prepareCall( query2 );

			cs.clearParameters();
			cs.setByte( 1, Byte.parseByte( "9" ) ); // Opción borrado
			cs.registerOutParameter( 2, Types.INTEGER ); // Id de llave primaria
			cs.setInt( 3, opc.getIdPreCompromisoHeader() ); // IdHeader
			cs.setString( 4, "" ); // EP, pero no importa valor que se mande
									// para
									// esta opción del Stored Procedure
			cs.setByte( 5, Byte.parseByte( "99" ) ); // Mes, pero no importa
														// valor
														// que se mande para
														// esta
														// opción del Stored
														// Procedure
			cs.setFloat( 6, 0 ); // importe, pero no importa valor que se mande
									// para esta opción del Stored Procedure
			cs.setFloat( 7, 0 ); // importeAnt, pero no importa su valor
			cs.setFloat( 8, 0 ); // importeDif, pero no importa su valor

			cs.setString( 9, "" ); // Evento, pero no importa su valor
			cs.setFloat( 10, 0 ); // importe negativo, pero no importa valor que
									// se mande
			cs.setString( 11, "" ); // Centro contable, pero no importa valor
									// que
									// se mande para esta opción del Stored
									// Procedure
			cs.setInt( 12, 0 ); // DocRenglon

			// Call the inherited PreparedStatement.executeUpdate( ) method
			cs.execute(); // Invoca el borrado a través del stored procedure

			// Inserta detalle
			cs = null;
			opc.listEvent.clear();
			// Create an instance of the CallableStatement
			String query3 = "EXEC ManteObraPublicaPreComprDetail ?,? output,?,?,?,?,?,?,?,?,?,?";
			cs = conn.prepareCall( query3 );

			for ( int i = 0; i < listDetail.length; i++ ) {
				cs.clearParameters();
				cs.setByte( 1, Byte.parseByte( "1" ) ); // Opción
				cs.registerOutParameter( 2, Types.INTEGER ); // Identificador
																// IdPreComprObraPublicaDetail
				cs.setInt( 3, opc.getIdPreCompromisoHeader() ); // IdHeader
				cs.setString( 4, listDetail[i][0] ); // EP
				cs.setByte( 5, Byte.parseByte( listDetail[i][1] ) ); // Mes
				cs.setFloat( 6, Float.parseFloat( listDetail[i][2] ) ); // importe
				cs.setFloat( 7, Float.parseFloat( listDetail[i][3] ) ); // importeAnt
				cs.setFloat( 8, Float.parseFloat( listDetail[i][4] ) ); // importeDif

				cs.setString( 9, listDetail[i][5] ); // Evento
				cs.setFloat( 10, Float.parseFloat( listDetail[i][6] ) ); // importe
																			// negativo
				cs.setString( 11, listDetail[i][7] ); // Centro contable
				cs.setInt( 12, Integer.parseInt( listDetail[i][8] ) ); // DocRenglon

				// Call the inherited PreparedStatement.executeUpdate( ) method
				cs.execute();

				// Validar si evento existe en la lista

				if ( !opc.listEvent.contains( listDetail[i][5] ) ) {
					opc.listEvent.add( listDetail[i][5] );
				}

			}
			// verifica aplicar motor contable
			retval = 0;
		} finally {
			if ( cs != null )
				cs.close();

			cs = null;

		}
		return retval;
	}

	public static byte ApplyPreCompr( Connection conn, ObraPublicaContract opc ) {
		byte retVal = -1;
		try {
			AccountingEngine accEng = new AccountingEngine();
			for ( String evento : opc.listEvent ) {
				accEng.makeAccountingApplication( conn, evento, Integer.toString( opc.getIdPreCompromisoHeader() ), "tObraPublicaPreCompromisoEncabezado", "tObraPublicaPreCompromisoDetalle", "nFolioOPPreComHeader" );
			}

			retVal = 0;
		} catch ( AccountingEngineException e ) {
			log.error( e, e );
		}
		return retVal;
	}

	public static byte SaveCompromiso( Connection conn, ObraPublicaContract opc, String[] listHeader, String[][] listDetail ) throws SQLException {

		byte retval = -1;

		String query = "{call dbo.ManteObraPublicaCompromisoHeader (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		CallableStatement cs = null;

		cs = conn.prepareCall( query );
		// Comienza llenado de parametros de SP
		cs.setByte( 1, Byte.parseByte( "1" ) ); // Opción
		cs.registerOutParameter( 2, Types.INTEGER ); // IdHeader

		cs.setString( 3, listHeader[0] ); // FolioSAI
		cs.setString( 4, listHeader[1] ); // Cartera
		cs.setString( 5, listHeader[2] ); // OLI
		cs.setFloat( 6, Float.parseFloat( listHeader[3] ) ); // monto
		cs.setFloat( 7, Float.parseFloat( listHeader[4] ) ); // porcentaje IVA
		cs.setFloat( 8, Float.parseFloat( listHeader[5] ) ); // Monto con IVA
		cs.setString( 9, listHeader[6] ); // Tipo obra
		cs.setString( 10, listHeader[7] ); // Tipo recurso
		cs.setString( 11, listHeader[8] ); // Descripcion
		cs.setString( 12, listHeader[9] ); // RFC
		cs.setString( 13, listHeader[10] ); // Beneficiario
		cs.setString( 14, listHeader[11] ); // Nombre de convocatoria
		cs.setString( 15, listHeader[12] ); // Fecha de convocatoria

		cs.setString( 16, listHeader[13] ); // Fecha de junta de aclaración
		cs.setString( 17, listHeader[14] ); // Fecha de recepcion de propuesta
		cs.setString( 18, listHeader[15] ); // Fecha de fallo
		cs.setByte( 19, Byte.parseByte( listHeader[16] ) );
		cs.setFloat( 20, Float.parseFloat( listHeader[17] ) ); // porcentaje
																// anticipo

		cs.setString( 21, listHeader[18] ); // Fecha de inicio de cotrato
		cs.setString( 22, listHeader[19] ); // Fecha de fin de contrato
		cs.setString( 23, listHeader[20] ); // Clave de contrato
		cs.setString( 24, listHeader[21] ); // Clave de concurso
		cs.setString( 25, listHeader[22] ); // Tipo de contrato
		cs.setString( 26, listHeader[23] ); // tipo de adjudicación
		cs.setByte( 27, Byte.parseByte( listHeader[24] ) ); // Tipo adicional
		cs.setString( 28, listHeader[25] ); // oficio
		cs.setString( 29, listHeader[26] ); // fecha oficio
		cs.setByte( 30, Byte.parseByte( listHeader[27] ) ); // es plurianual
		cs.setFloat( 31, Float.parseFloat( listHeader[28] ) ); // Monto anticipo
		cs.setString( 32, listHeader[29] ); // fecha autorización ventanilla
		cs.setByte( 33, Byte.parseByte( listHeader[30] ) ); // Tiene convenio
															// modificatorio
		cs.setString( 34, listHeader[31] ); // Clave de convenio modificatorio

		cs.setByte( 34, Byte.parseByte( listHeader[31] ) ); // Status
		cs.setString( 35, listHeader[32] ); // Fecha de aplicacion
		cs.setString( 36, listHeader[33] ); // Ejercicio fiscal
		cs.setString( 37, listHeader[34] ); // Login
		cs.setString( 38, listHeader[35] ); // Ramo
		cs.setString( 39, listHeader[36] ); // Unidad responsable
		cs.setString( 40, listHeader[37] ); // DocumentoAplicado
		cs.setInt( 41, Integer.parseInt( listHeader[38] ) ); // Folio poliza
		cs.setString( 42, listHeader[39] ); // Tipo poliza
		cs.setInt( 43, Integer.parseInt( listHeader[40] ) ); // Folio poliza
																// cancelacion
		cs.setString( 44, listHeader[41] ); // fecha cancelacion

		// Termina llenado

		// Call the inherited PreparedStatement.executeUpdate( ) method
		// Código que estaba
		try {
			// ResultSet rs = cs.executeQuery();
			cs.execute();
			int idCompromisoHeader = cs.getInt( 2 );
			opc.setIdCompromisoHeader( idCompromisoHeader );

			// Elimina los registros en detalle asociado a
			// idCompromisoObraPublicaHeader

			cs = null;

			// Create an instance of the CallableStatement
			String query2 = "EXEC ManteObraPublicaCompromisoDetail ?,? output,?,?,?,?,?,?,?,?,?,?";
			cs = conn.prepareCall( query2 );

			cs.clearParameters();
			cs.setByte( 1, Byte.parseByte( "9" ) ); // Opción
			cs.registerOutParameter( 2, Types.INTEGER ); // Identificador
															// IdComprObraPublicaDetail
			cs.setInt( 3, opc.getIdCompromisoHeader() ); // IdHeader
			cs.setString( 4, "" ); // EP, pero no importa valor que se mande
									// para
									// esta opción del Stored Procedure
			cs.setByte( 5, Byte.parseByte( "99" ) ); // Mes, pero no importa
														// valor
														// que se mande para
														// esta
														// opción del Stored
														// Procedure
			cs.setFloat( 6, 0 ); // importe, pero no importa valor que se mande
									// para esta opción del Stored Procedure
			cs.setFloat( 7, 0 ); // importeAnt, pero no importa valor que se
									// mande
									// para esta opción del Stored Procedure
			cs.setFloat( 8, 0 ); // importeDif, pero no importa valor que se
									// mande
									// para esta opción del Stored Procedure

			cs.setString( 9, "" ); // Evento
			cs.setFloat( 10, 0 ); // importe negativo
			cs.setString( 11, "" ); // Centro contable
			cs.setInt( 12, 0 ); // DocRenglon

			// Call the inherited PreparedStatement.executeUpdate( ) method
			cs.execute();

			// Inserta detalle
			opc.listEvent.clear();

			cs = null;
			// Create an instance of the CallableStatement
			String query3 = "EXEC ManteObraPublicaCompromisoDetail ?,? output,?,?,?,?,?,?,?,?,?,?";
			cs = conn.prepareCall( query3 );

			for ( int i = 0; i < listDetail.length; i++ ) {
				cs.clearParameters();
				cs.setByte( 1, Byte.parseByte( "1" ) ); // Opción
				cs.registerOutParameter( 2, Types.INTEGER ); // Identificador
																// IdPreComprObraPublicaDetail
				cs.setInt( 3, opc.getIdCompromisoHeader() ); // IdHeader
				cs.setString( 4, listDetail[i][0] ); // EP
				cs.setByte( 5, Byte.parseByte( listDetail[i][1] ) ); // Mes
				cs.setFloat( 6, Float.parseFloat( listDetail[i][2] ) ); // importe
				cs.setFloat( 7, Float.parseFloat( listDetail[i][3] ) ); // importeAnt
				cs.setFloat( 8, Float.parseFloat( listDetail[i][4] ) ); // importeDif

				cs.setString( 9, listDetail[i][5] ); // Evento
				cs.setFloat( 10, Float.parseFloat( listDetail[i][6] ) ); // importe
																			// negativo
				cs.setString( 11, listDetail[i][7] ); // Centro contable
				cs.setInt( 12, Integer.parseInt( listDetail[i][8] ) ); // DocRenglon

				// Call the inherited PreparedStatement.executeUpdate( ) method
				cs.execute();

				// Validar si evento existe en la lista
				if ( !opc.listEvent.contains( listDetail[i][5] ) ) {
					opc.listEvent.add( listDetail[i][5] );
				}

			}
			// verifica aplicar motor contable
			retval = 0;
		} finally {
			if ( cs != null )
				cs.close();

			cs = null;

		}
		return retval;
	}

	public static byte ApplyCompromiso( Connection conn, ObraPublicaContract opc ) {
		byte retVal = -1;
		try {

			AccountingEngine accEng = new AccountingEngine();
			for ( String evento : opc.listEvent ) {
				accEng.makeAccountingApplication( conn, evento, Integer.toString( opc.getIdCompromisoHeader() ), "tObraPublicaCompromisoEncabezado", "tObraPublicaCompromisoDetalle", "nFolioOPComHeader" );
			}

			retVal = 0;
		} catch ( AccountingEngineException e ) {
			log.error( e, e );
		}
		return retVal;
	}

	public static byte SelectIdApartadoHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioOPAHeader,d.cEvento " + " FROM tObraPublicaApartadoEncabezado h WITH(NOLOCK),tObraPublicaApartadoDetalle d WITH(NOLOCK) " + " WHERE FolioSAI = ? " + "  AND h.nFolioOPAHeader = d.nFolioOPAHeader " + " GROUP BY h.nFolioOPAHeader,d.cEvento ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI

		try {
			opc.listEvent.clear();
			int idApartadoHeader = 0;
			ResultSet rs = ps.executeQuery();

			while ( rs.next() ) {
				idApartadoHeader = rs.getInt( 1 );

				opc.listEvent.add( rs.getString( 2 ) );
			}
			if ( idApartadoHeader > 0 ) {
				opc.setIdApartadoHeader( idApartadoHeader );
				retVal = 0;

			} else
				retVal = -2;

		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SelectIdPreCompromisoHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioOPPreComHeader,d.cEvento " + " FROM tObraPublicaPreCompromisoEncabezado h WITH(NOLOCK),tObraPublicaPreCompromisoDetalle d  WITH(NOLOCK)" + " WHERE FolioSAI = ? " + "  AND h.nFolioOPPreComHeader = d.nFolioOPPreComHeader " + " GROUP BY h.nFolioOPPreComHeader,d.cEvento ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI

		try {
			opc.listEvent.clear();
			int idPreCompromisoHeader = 0;

			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				idPreCompromisoHeader = rs.getInt( 1 );

				opc.listEvent.add( rs.getString( 2 ) );
			}
			if ( idPreCompromisoHeader > 0 ) {
				opc.setIdPreCompromisoHeader( idPreCompromisoHeader );
				retVal = 0;

			} else
				retVal = -2;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SelectIdCompromisoHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioOPComHeader,d.cEvento,cCveContrato " + " FROM tObraPublicaCompromisoEncabezado h WITH(NOLOCK),tObraPublicaCompromisoDetalle d  WITH(NOLOCK)" + " WHERE FolioSAI = ? " + "  AND h.nFolioOPComHeader = d.nFolioOPComHeader " + " GROUP BY h.nFolioOPComHeader,d.cEvento,cCveContrato ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI
		String cIdContrato=null;
		try {
			opc.listEvent.clear();
			int idCompromisoHeader = 0;

			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				idCompromisoHeader = rs.getInt( 1 );
				opc.listEvent.add( rs.getString( 2 ) );
				cIdContrato=rs.getString( 3 );
			}
			if ( idCompromisoHeader > 0 ) {
				opc.setIdCompromisoHeader( idCompromisoHeader );
				opc.setcIdContrato( cIdContrato );
				retVal = 0;

			} else
				retVal = -2;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	// MLR Cancelación masiva 20140620
	public static byte SelectIdCompromisoSinAceptarVentanilla( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;
		String query = " SELECT h.nFolioOPComHeader FROM tObraPublicaCompromisoEncabezado h WITH(NOLOCK) " + " WHERE FolioSAI = ? ";
		// String query = " SELECT h.nFolioOPComHeader,d.cEvento " + " FROM
		// tObraPublicaCompromisoEncabezado h
		// WITH(NOLOCK),tObraPublicaCompromisoDetalle d WITH(NOLOCK)" + " WHERE
		// FolioSAI = ? " + " AND h.nFolioOPComHeader = d.nFolioOPComHeader " +
		// " GROUP BY h.nFolioOPComHeader,d.cEvento ";
		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI

		try {
			opc.listEvent.clear();
			int idCompHeader = 0;
			ResultSet rs = ps.executeQuery();

			while ( rs.next() ) {
				idCompHeader = rs.getInt( 1 );

				// opc.listEvent.add(rs.getString(2));
			}
			if ( idCompHeader > 0 ) {
				opc.setIdCompHeader( idCompHeader );
				retVal = 0;

			} else
				retVal = -2;

		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SelectIdPagoPasivoHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioOPPagPasHeader,d.cEvento, max(isnull(h.cDocumentoHaplicado,'')) cDocumentoHaplicado " + " FROM tObraPublicaPagoPasivoEncabezado h WITH(NOLOCK),tObraPublicaPagoPasivoDetalle d  WITH(NOLOCK)" + " WHERE FolioSAI = ? " + "  AND h.nFolioOPPagPasHeader = d.nFolioOPPagPasHeader " + " GROUP BY h.nFolioOPPagPasHeader,d.cEvento ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI

		try {
			opc.listEvent.clear();
			int idCompromisoHeader = 0;
			String cDocumentoHaplicado = "";

			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				idCompromisoHeader = rs.getInt( 1 );
				cDocumentoHaplicado = rs.getString( 3 );

				opc.listEvent.add( rs.getString( 2 ) );
			}
			opc.setcDocumentoHaplicado( cDocumentoHaplicado );
			if ( idCompromisoHeader > 0 ) {
				opc.setIdCompromisoHeader( idCompromisoHeader );
				retVal = 0;

			} else
				retVal = -2;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	// IRD 20140901 se incluye cancelación de convenios
	public static byte SelectIdConvModifHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioOPConvHeader,d.cEvento, max(isnull(h.cDocumentoHaplicado,'')) cDocumentoHaplicado, max(isnull(h.cConvenioEnCaptura,'')) cConvenioEnCaptura FROM tObraPublicaConvModifEncabezado h WITH(NOLOCK),tObraPublicaConvModifDetalle d  WITH(NOLOCK)   WHERE h.cCveContrato =( select max(cCveContrato)  from tObraPublicaCompromisoEncabezado where FolioSAI = ?)   AND  h.nFolioOPConvHeader = d.nFolioOPConvHeader GROUP BY h.nFolioOPConvHeader,d.cEvento ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI
		try {
			opc.listEvent.clear();
			int idConvModifHeader = 0;
			String cDocumentoHaplicado = "";
			String cConvenioEnCaptura = "";

			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				idConvModifHeader = rs.getInt( 1 );
				opc.listEvent.add( rs.getString( 2 ) );
				cDocumentoHaplicado = rs.getString( 3 );
				cConvenioEnCaptura = rs.getString( 4 );
			}
			opc.setcDocumentoHaplicado( cDocumentoHaplicado );
			opc.setcConvenioEnCaptura( cConvenioEnCaptura );
			if ( idConvModifHeader > 0 ) {
				opc.setidConvModHeader( idConvModifHeader );
				retVal = 0;

			} else
				retVal = -2;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	// IRD 20140901 se incluye cancelación de precompromisos de proinpro
	public static byte SelectIdProinproHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioDocOli,d.cEvento, max(isnull(h.cDocumentoHaplicado,'')) cDocumentoHaplicado, '' cConvenioEnCaptura   FROM tDocProinproEncabezado h WITH(NOLOCK),tDocProinproDetalle d  WITH(NOLOCK)     WHERE 'PINPR-' + cast(h.nFolioDocOli as varchar(10)) = ? GROUP BY h.nFolioDocOli,d.cEvento ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI
		try {
			opc.listEvent.clear();
			int idConvModifHeader = 0;
			String cDocumentoHaplicado = "";
			String cConvenioEnCaptura = "";

			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				idConvModifHeader = rs.getInt( 1 );
				opc.listEvent.add( rs.getString( 2 ) );
				cDocumentoHaplicado = rs.getString( 3 );
				cConvenioEnCaptura = rs.getString( 4 );
			}
			opc.setcDocumentoHaplicado( cDocumentoHaplicado );
			opc.setcConvenioEnCaptura( cConvenioEnCaptura );
			if ( idConvModifHeader > 0 ) {
				opc.setidProinproHeader( idConvModifHeader );
				retVal = 0;
			} else
				retVal = -2;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SelectIdPlurianualHeaderAndEventos( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = " SELECT h.nFolioOPPlurianualHeader,d.cEvento, max(isnull(h.cDocumentoHaplicado,'')) cDocumentoHaplicado " + " FROM tObraPublicaPlurianualEncabezado h WITH(NOLOCK),tObraPublicaPluriAnualDetalle d  WITH(NOLOCK)" + " WHERE FolioSAI = ? " + "  AND h.nFolioOPPlurianualHeader = d.nFolioOPPlurianualHeader " + " GROUP BY h.nFolioOPPlurianualHeader,d.cEvento ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, opc.getFolioSAI() ); // FolioSAI

		try {
			opc.listEvent.clear();
			int idCompromisoHeader = 0;
			String cDocumentoHaplicado = "";

			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				idCompromisoHeader = rs.getInt( 1 );
				cDocumentoHaplicado = rs.getString( 3 );

				opc.listEvent.add( rs.getString( 2 ) );
			}
			opc.setcDocumentoHaplicado( cDocumentoHaplicado );
			if ( idCompromisoHeader > 0 ) {
				opc.setIdCompromisoHeader( idCompromisoHeader );
				retVal = 0;

			} else
				retVal = -2;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetDocApliApartado( Connection conn, String folioSAI, String status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaApartadoEncabezado SET cDocumentoHAplicado = ? WHERE folioSAI = ? ";
		query += " UPDATE tObraPublicaPreCompromisoEncabezado SET cDocumentoHAplicado = ? WHERE folioSAI = ? ";
		query += " UPDATE tObraPublicaCompromisoEncabezado SET cDocumentoHAplicado = ? WHERE folioSAI = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, status );
		ps.setString( 2, folioSAI );
		ps.setString( 3, status );
		ps.setString( 4, folioSAI );
		ps.setString( 5, status );
		ps.setString( 6, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	// Cancelación masiva 20140620
	public static byte SetDocCancelComp( Connection conn, String folioSAI, String status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaCompromisoEncabezado SET cDocumentoHAplicado = ? WHERE folioSAI = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, status );
		ps.setString( 2, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetDocApliConvModif( Connection conn, int convModifHeader, String status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaConvModifEncabezado " + "   SET cDocumentoHAplicado = ?" + " WHERE nFolioOPConvHeader = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, status );
		ps.setInt( 2, convModifHeader );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetDocApliProinpro( Connection conn, int convModifHeader, String status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tDocProinproEncabezado " + "   SET cDocumentoHAplicado = ?" + " WHERE nFolioDocOli = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, status );
		ps.setInt( 2, convModifHeader );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetDocApliPagoPasivo( Connection conn, String folioSAI, String status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaPagoPasivoEncabezado " + "   SET cDocumentoHAplicado = ?" + " WHERE folioSAI = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, status );
		ps.setString( 2, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	// MLR Cancelación masiva 20140620
	public static byte updateCancelacionMas( Connection conn, String folioSAI ) throws SQLException {
		byte retVal = -1;

		String query = " update CG_CASO_OPERACION set CO_RESPONSABLE = 'CONSULTA_OBRA', id_oper = 3 where ID_CASO = (select id_caso from cg_caso where C_FOLIO =?) ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	// MLR Cancelación convenio 20141111
	public static byte updateCancelacionConv( Connection conn, String folioSAI ) throws SQLException {
		byte retVal = -1;

		String query = " update CG_CASO_OPERACION set CO_RESPONSABLE = 'CONSULTA_OBRA', id_oper = 3 where ID_CASO = (select id_caso from cg_caso where C_FOLIO =?) ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetDocApliPlurianual( Connection conn, String folioSAI, String status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaPlurianualEncabezado " + "   SET cDocumentoHAplicado = ?" + " WHERE folioSAI = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setString( 1, status );
		ps.setString( 2, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetStatusApartado( Connection conn, int idApartadoHeader, byte status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaApartadoEncabezado " + "   SET iStatus = ?" + " WHERE nFolioOPAHeader = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setByte( 1, status );
		ps.setInt( 2, idApartadoHeader );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetStatusPreCompromiso( Connection conn, int idPreCompromisoHeader, byte status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaPreCompromisoEncabezado " + "   SET iStatus = ?" + " WHERE nFolioOPPreComHeader = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setByte( 1, status );

		ps.setInt( 2, idPreCompromisoHeader );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte SetStatusCompromiso( Connection conn, int idCompromisoHeader, byte status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaCompromisoEncabezado " + "   SET iStatus = ?" + " WHERE nFolioOPComHeader = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setByte( 1, status );
		ps.setInt( 2, idCompromisoHeader );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte MoveToAuthorize( Connection conn, String folioSAI, byte status ) throws SQLException {
		byte retVal = -1;

		String query = " UPDATE tObraPublicaCompromisoEncabezado " + "   SET iStatus = 3" + " WHERE FolioSAI = ? ";

		PreparedStatement ps = null;
		ps = conn.prepareStatement( query );
		// Comienza llenado de parametros de setencia
		ps.setByte( 1, status );
		ps.setString( 2, folioSAI );

		try {
			ps.executeUpdate();
			retVal = 0;
		} finally {
			if ( ps != null )
				ps.close();

			ps = null;
		}
		return retVal;

	}

	public static byte GetSituacionContratoObraPublica( Connection conn, ObraPublicaContract opc ) throws SQLException {
		byte retVal = -1;

		String query = "{call GetSituacionObraPublicaContract (?)}";
		CallableStatement cs = null;

		cs = conn.prepareCall( query );
		// Comienza llenado de parametros de SP
		cs.setString( 1, opc.getFolioSAI() ); // Opción
		try {
			// ResultSet rs = cs.executeQuery();
			byte iWhereIsContract = -1;
			ResultSet rs = cs.executeQuery();
			while ( rs.next() ) {
				iWhereIsContract = rs.getByte( 1 );

			}
			if ( iWhereIsContract > -1 ) {
				opc.setWhereIsContract( iWhereIsContract );
				retVal = 0;

			} else
				retVal = -2;

		} finally {
			if ( cs != null )
				cs.close();

			cs = null;
		}
		return retVal;

	}

	public static byte CancelApartado( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;
		// IRD 20140806 En caso de error no regresaba el -1 por eso se agrega
		// try
		try {
			AccountingEngine accEng = new AccountingEngine();
			accEng.cancelAccountingApplication( conn, "APARTADO_OPC", Integer.toString( opc.getIdApartadoHeader() ), "tObraPublicaApartadoEncabezado", "tObraPublicaApartadoDetalle", "nFolioOPAHeader" );
			retVal = 0;
		} catch ( Exception exe ) {

		}

		return retVal;
	}

	public static byte CancelPreCompromiso( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;
		try {
			AccountingEngine accEng = new AccountingEngine();
			accEng.cancelAccountingApplication( conn, "PRECOM_OPC", Integer.toString( opc.getIdPreCompromisoHeader() ), "tObraPublicaPreCompromisoEncabezado", "tObraPublicaPreCompromisoDetalle", "nFolioOPPreComHeader" );
			retVal = 0;
		} catch ( Exception exe ) {

		}
		return retVal;
	}

	public static byte CancelPagoPasivo( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;

		try {
			AccountingEngine accEng = new AccountingEngine();

			accEng.cancelAccountingApplication( conn, "PAGOPASIVO_PRECOM", Integer.toString( opc.getIdCompromisoHeader() ), "tObraPublicaPagoPasivoEncabezado", "tObraPublicaPagoPasivoDetalle", "nFolioOPPagPasHeader" );
			retVal = 0;
		} catch ( Exception exe ) {

		}

		return retVal;
	}

	public static byte CancelConvModif( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;

		try {
			AccountingEngine accEng = new AccountingEngine();
			accEng.cancelAccountingApplication( conn, "CONVMOD_PRECOM", Integer.toString( opc.getidConvModHeader() ), "tObraPublicaConvModifEncabezado", "tObraPublicaConvModifDetalle", "nFolioOPConvHeader" );
			retVal = 0;
		} catch ( Exception exe ) {

		}

		return retVal;
	}

	public static byte CancelProinpro( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;

		try {
			AccountingEngine accEng = new AccountingEngine();
			accEng.cancelAccountingApplication( conn, "PROINPRO", Integer.toString( opc.getidProinproHeader() ), "tDocProinproEncabezado", "tDocProinproDetalle", "nFolioDocOli" );
			retVal = 0;
		} catch ( Exception exe ) {

		}

		return retVal;
	}

	public static byte CancelPlurianual( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;

		try {
			AccountingEngine accEng = new AccountingEngine();

			accEng.cancelAccountingApplication( conn, "PLURIANUAL_PRECOM", Integer.toString( opc.getIdCompromisoHeader() ), "tObraPublicaPlurianualEncabezado", "tObraPublicaPluriAnualDetalle", "nFolioOPPlurianualHeader" );
			retVal = 0;
		} catch ( Exception exe ) {

		}

		return retVal;
	}

	public static byte CancelCompromiso( Connection conn, ObraPublicaContract opc ) throws Exception {
		byte retVal = -1;

		try {
			AccountingEngine accEng = new AccountingEngine();
			for ( String evento : opc.listEvent ) {
				accEng.cancelAccountingApplication( conn, evento, Integer.toString( opc.getIdCompromisoHeader() ), "tObraPublicaCompromisoEncabezado", "tObraPublicaCompromisoDetalle", "nFolioOPComHeader" );
			}
		} catch ( Exception exe ) {

		}

		retVal = 0;

		return retVal;
	}

	public static int generaInformacionConvenioCompromiso( Connection conn, String nFolioConvModif, String folioSAI, int idCasoCompromisoStr, int nMes, String caNoCompromiso ) throws SQLException {
		String call = "{call SP_INSERTA_COMPROMISO_CONVENIO_MODIFICATORIO( ?,?,?,?,? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, folioSAI );
			callStmnt.setString( 2, nFolioConvModif );
			callStmnt.setInt( 3, idCasoCompromisoStr );
			callStmnt.setInt( 4, nMes );
			callStmnt.setString( 5, caNoCompromiso );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}
	}

	// IRD 20131121 RO-0009
	public static int generaInformacionPagoPasivoCompromiso( Connection conn, String nFolioConvModif, String folioSAI, int idCasoCompromisoStr, int nMes, String caNoCompromiso ) throws SQLException {
		String call = "{call SP_INSERTA_COMPROMISO_PAGO_PASIVO( ?,?,?,?,? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, folioSAI );
			callStmnt.setString( 2, nFolioConvModif );
			callStmnt.setInt( 3, idCasoCompromisoStr );
			callStmnt.setInt( 4, nMes );
			callStmnt.setString( 5, caNoCompromiso );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}
	}

	// MLR 20131218 RO-0010
	public static int generaInformacionPlurianualCompromiso( Connection conn, String nFolioConvModif, String folioSAI, int idCasoCompromisoStr, int nMes, String caNoCompromiso ) throws SQLException {
		String call = "{call SP_INSERTA_COMPROMISO_PLURIANUAL( ?,?,?,?,? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, folioSAI );
			callStmnt.setString( 2, nFolioConvModif );
			callStmnt.setInt( 3, idCasoCompromisoStr );
			callStmnt.setInt( 4, nMes );
			callStmnt.setString( 5, caNoCompromiso );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}
	}

	public static int generaInformacionCompromiso( Connection conn, String folioSAI, int idCasoCompromisoStr, int nMes, String caNoCompromiso ) throws SQLException {
		String call = "{call SP_INSERTA_COMPROMISO_DETALLE( ?,?,?,? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, folioSAI );
			callStmnt.setInt( 2, idCasoCompromisoStr );
			callStmnt.setInt( 3, nMes );
			callStmnt.setString( 4, caNoCompromiso );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}
	}
	public static boolean esContratoPluriEjercicioAnt( Connection conn, String cNoContrato) throws SQLException {
		PreparedStatement ps = null;
		StringBuilder query=null;
		ResultSet rs = null;
		boolean resp=false;
		try {
			query=new StringBuilder();
			query.append( " select YEAR(cEjercicio)-YEAR(fInicio)diferencia_ejercicios from pContratoObra with(Nolock) where cIdContrato=? " );
			
			log.info(query.toString());
			ps = conn.prepareStatement(query.toString());
			ps.setString( 1, cNoContrato );
			rs=ps.executeQuery();
			if(rs.next()){
				resp=rs.getInt( 1 )>0;
			}
			return resp;
		} finally {
			CloseObject.closeObject(ps);
		}
	}
	public static void addAnticipoObraNoamortizado( Connection conn, String cNoContrato, String etiquetaSAI) throws SQLException {
		PreparedStatement ps = null;
		StringBuilder query=null;
		try {
			query=new StringBuilder();
			query.append( " insert into pContratoObraAnticipoEFAnterior " );
			query.append( "(cEjercicio, cIdentidadContable, cidcontrato, cIdTipoAnticipoObra, nasignacion, nporcasignacion, mImporteAnticipo, mImporteAnticipoIVA, mTotalAnticipo, fAnticipo, mTotalAnticipado, mTotalAMortizado ) " );
			query.append( " select *from "+etiquetaSAI+"fn_getAnticipoObraNoAmortizado(?) " );
			log.info(query.toString());
			ps = conn.prepareStatement(query.toString());
			ps.setString( 1, cNoContrato );
			
		} finally {
			CloseObject.closeObject(ps);
		}
	}
	// IRD 20131121 RO-0009
	public static int copiaInformacionContratoAnioAnterior( Connection conn, String folioSAIAnterior, String folioSAI ) throws Exception {
		String call = "{call copiaContratosAniosAnteriores ( ?,?, ? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, "2015" );
			callStmnt.setString( 2, folioSAIAnterior );
			callStmnt.setString( 3, folioSAI );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}

	}

	public static int copiaInformacionContratoAnioAnteriorPl( Connection conn, String folioSAIAnterior, String folioSAI ) throws Exception {
		String call = "{call copiaContratosAniosAnterioresPl ( ?,?, ? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, "2015" );
			callStmnt.setString( 2, folioSAIAnterior );
			callStmnt.setString( 3, folioSAI );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}

	}

	public static int generaInformacionContrato( Connection conn, String folioSAI, int idCaso ) throws Exception {
		String call = "{call Sp_inserta_obra_publica_contrato( ?,? ) }";
		CallableStatement callStmnt = null;
		try {
			callStmnt = conn.prepareCall( call );
			callStmnt.setString( 1, folioSAI );
			callStmnt.setInt( 2, idCaso );
			callStmnt.execute();
			return 0;
		} finally {
			if ( callStmnt != null )
				try {
					callStmnt.close();
				} catch ( Exception e ) {
					log.warn( "No fue posible cerrar el statement debido al error: " + e, e );
				}
			callStmnt = null;
		}

	}

	public static String generaFolioContratoOP( Connection conn, String cUR, String tRecurso, String tAdjudicacion ) throws Exception {
		String sqlUN = "SELECT TOP 1 cunidadnorativa " + " FROM   tcatalogoep WITH( nolock ) " + " WHERE  cunidadejecutora = ? ";
		String sqlEF = "SELECT siglas, " + "       cidentidadfederativa " + "FROM   v_catalogoentidadesfederativasrpt WITH( nolock ) " + "WHERE  unidad_ejecutora = ? ";
		String sqlNEF = "select  LTRIM( RTRIM(dEntidadFederativaCorto) ) from tCatalogoEntidadFederativa with(nolock) where cEntidadFederativa = ? ";

		String folio = "";
		String cUN = null;
		String cEF = null;
		PreparedStatement pStmntUN = null;
		PreparedStatement pStmntEF = null;
		PreparedStatement pStmntNEF = null;

		ResultSet rs = null;

		try {
			String UN;
			String UR;
			String EF;
			String AC;
			String SQ;

			CFSequenceManager cfm = CFSequenceManager.getInstance();
			pStmntUN = conn.prepareStatement( sqlUN );
			pStmntEF = conn.prepareStatement( sqlEF );
			pStmntNEF = conn.prepareStatement( sqlNEF );

			pStmntUN.setString( 1, cUR );

			rs = pStmntUN.executeQuery();
			if ( rs.next() )
				cUN = rs.getString( 1 );
			else
				throw new Exception( "No se encontro unidad normativa para la UR[" + cUR + "] Notifique al administrador." );

			pStmntEF.setString( 1, cUN );
			rs = pStmntEF.executeQuery();
			if ( rs.next() )
				UN = rs.getString( 1 );
			else
				throw new Exception( "No se encontraron siglas para la UN [" + cUN + "] Notifique al administrador." );

			pStmntEF.setString( 1, cUR );
			rs = pStmntEF.executeQuery();
			if ( rs.next() ) {
				UR = rs.getString( 1 );
				cEF = rs.getString( 2 );
			} else
				throw new Exception( "No se encontraron siglas para la UR [" + cUR + "] Notifique al administrador." );

			pStmntNEF.setString( 1, cEF );
			rs = pStmntNEF.executeQuery();
			if ( rs.next() )
				EF = rs.getString( 1 );
			else
				throw new Exception( "No se encontro la abreviatura del estado [" + cEF + "] Notifique al administrador." );

			Calendar c = GregorianCalendar.getInstance();
			c.setTimeInMillis( System.currentTimeMillis() );

			AC = String.valueOf( c.get( Calendar.YEAR ) ).substring( 2 );
			SQ = String.valueOf( cfm.nextVal( "NO_CNT_OP" ) );
			SQ = "000".substring( 0, 3 - SQ.length() ) + SQ;

			folio = UN + "-" + UR + "-" + EF + "-" + AC + "-" + SQ;
			folio += tRecurso == null || "".equals( tRecurso ) ? "" : "-" + tRecurso;
			folio += tAdjudicacion == null || "".equals( tAdjudicacion ) ? "" : "-" + tAdjudicacion;

			return folio;
		} finally {
			try {

				CloseObject.closeObject( pStmntUN, false );
				CloseObject.closeObject( pStmntEF, false );
				CloseObject.closeObject( pStmntNEF, false );
				CloseObject.closeObject( rs, false );

			} catch ( Exception e ) {

			}
		}
	}

	public static String generaFolioConvenioOP( Connection conn, String cUR, String tRecurso, String tAdjudicacion ) throws Exception {
		String sqlUN = "SELECT TOP 1 cunidadnorativa " + " FROM   tcatalogoep WITH( nolock ) " + " WHERE  cunidadejecutora = ? ";
		String sqlEF = "SELECT siglas, " + "       cidentidadfederativa " + "FROM   v_catalogoentidadesfederativasrpt WITH( nolock ) " + "WHERE  unidad_ejecutora = ? ";
		String sqlNEF = "select  LTRIM( RTRIM(dEntidadFederativaCorto) ) from tCatalogoEntidadFederativa with(nolock) where cEntidadFederativa = ? ";

		String folio = "";
		String cUN = null;
		String cEF = null;
		PreparedStatement pStmntUN = null;
		PreparedStatement pStmntEF = null;
		PreparedStatement pStmntNEF = null;

		ResultSet rs = null;

		try {
			String UN;
			String UR;
			String EF;
			String AC;
			String SQ;

			CFSequenceManager cfm = CFSequenceManager.getInstance();
			pStmntUN = conn.prepareStatement( sqlUN );
			pStmntEF = conn.prepareStatement( sqlEF );
			pStmntNEF = conn.prepareStatement( sqlNEF );

			pStmntUN.setString( 1, cUR );

			rs = pStmntUN.executeQuery();
			if ( rs.next() )
				cUN = rs.getString( 1 );
			else
				throw new Exception( "No se encontro unidad normativa para la UR[" + cUR + "] Notifique al administrador." );

			pStmntEF.setString( 1, cUN );
			rs = pStmntEF.executeQuery();
			if ( rs.next() )
				UN = rs.getString( 1 );
			else
				throw new Exception( "No se encontraron siglas para la UN [" + cUN + "] Notifique al administrador." );

			pStmntEF.setString( 1, cUR );
			rs = pStmntEF.executeQuery();
			if ( rs.next() ) {
				UR = rs.getString( 1 );
				cEF = rs.getString( 2 );
			} else
				throw new Exception( "No se encontraron siglas para la UR [" + cUR + "] Notifique al administrador." );

			pStmntNEF.setString( 1, cEF );
			rs = pStmntNEF.executeQuery();
			if ( rs.next() )
				EF = rs.getString( 1 );
			else
				throw new Exception( "No se encontro la abreviatura del estado [" + cEF + "] Notifique al administrador." );

			Calendar c = GregorianCalendar.getInstance();
			c.setTimeInMillis( System.currentTimeMillis() );

			AC = String.valueOf( c.get( Calendar.YEAR ) ).substring( 2 );
			SQ = String.valueOf( cfm.nextVal( "NO_CNV_OP" ) );
			SQ = "000".substring( 0, 3 - SQ.length() ) + SQ;

			folio = UN + "-" + UR + "-" + EF + "-" + AC + "-" + SQ;
			folio += tRecurso == null || "".equals( tRecurso ) ? "" : "-" + tRecurso;
			folio += "-CC";

			return folio;
		} finally {
			try {

				CloseObject.closeObject( pStmntUN, false );
				CloseObject.closeObject( pStmntEF, false );
				CloseObject.closeObject( pStmntNEF, false );
				CloseObject.closeObject( rs, false );

			} catch ( Exception e ) {

			}
		}
	}

	public static String obtenTipoDocumentoOP( Connection conn, String folioSAI ) throws Exception {
		String sqlUN = "select tipoDocumento from VOBRAPUBLICAPENDIENTES where folioSAI = ? ";

		String tipoDocumento = "";
		String cEF = null;
		PreparedStatement pStmntUN = null;

		ResultSet rs = null;

		try {
			String UN;

			pStmntUN = conn.prepareStatement( sqlUN );

			pStmntUN.setString( 1, folioSAI );

			rs = pStmntUN.executeQuery();
			if ( rs.next() )
				tipoDocumento = rs.getString( 1 );
			else
				throw new Exception( "No se pudo determinar el tipo de documento del folio " + folioSAI + "." );

			return tipoDocumento;
		} finally {
			try {

				CloseObject.closeObject( pStmntUN, false );
				CloseObject.closeObject( rs, false );

			} catch ( Exception e ) {

			}
		}
	}

	public static int actualizaInformacionInmuebles( Connection conn, String folioSAI, int idRePublicWork ) throws Exception {
		String query = " UPDATE tObraPublicaCompromisoEncabezado SET nIDRePublicWork = ? WHERE folioSAI = ? ";
		PreparedStatement ps = null;
		int afectados = 0;
		try {
			ps = conn.prepareStatement( query );
			ps.setInt( 1, idRePublicWork );
			ps.setString( 2, folioSAI );

			afectados = ps.executeUpdate();
			return afectados;

		} finally {
			CloseObject.closeObject( ps );
		}
	}
	public static int getTotalCFDICapturdos( Connection conn, String tituloAplicacion, int idGabinete ) throws Exception {
		return getTotalCFDICapturdos(  conn,  tituloAplicacion,  idGabinete, 1 );
	}
	public static int getTotalCFDICapturdos( Connection conn, String tituloAplicacion, int idGabinete, int tipoFacturaGlobal ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int total = 0;
		String query = "SELECT	COUNT(*) AS existe " + "  FROM	IMX_CARPETA carpeta WITH(NOLOCK)" + "		INNER JOIN dbo.IMX_DOCUMENTO documento WITH(NOLOCK)" + "		ON carpeta.TITULO_APLICACION = documento.TITULO_APLICACION " + "		AND carpeta.ID_GABINETE = documento.ID_GABINETE AND carpeta.ID_CARPETA = documento.ID_CARPETA_PADRE" + " WHERE	carpeta.TITULO_APLICACION = ?" + "   AND	carpeta.ID_GABINETE = ?  " + "   AND	carpeta.NOMBRE_CARPETA = 'CFDI Contrato'" + "   AND	documento.iEsVersion = 0";
		try {
			if(tipoFacturaGlobal==2) {
				query = "SELECT	COUNT(*) AS existe " + "  FROM	IMX_CARPETA carpeta WITH(NOLOCK)" 
						+ "		INNER JOIN dbo.IMX_DOCUMENTO documento WITH(NOLOCK)" + "		ON carpeta.TITULO_APLICACION = documento.TITULO_APLICACION " 
						+ "		AND carpeta.ID_GABINETE = documento.ID_GABINETE AND carpeta.ID_CARPETA = documento.ID_CARPETA_PADRE" 
						+ "   WHERE	carpeta.TITULO_APLICACION = ?" + "   AND	carpeta.ID_GABINETE = ?  " 
						+ "   AND	carpeta.NOMBRE_CARPETA = 'CFDI Contrato'" 
						+ "   AND	documento.iEsVersion = 0 and documento.ID_DOCUMENTO > 2";
			}
			ps = conn.prepareStatement( query );
			ps.setString( 1, tituloAplicacion );
			ps.setInt( 2, idGabinete );

			rs = ps.executeQuery();
			if ( rs.next() )
				total = rs.getInt( 1 );

			return total;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );

		}

	}
	public static boolean traeRecursoFiscal(Connection conn,String folioSAI) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder query=null;
		boolean resp=false;
		try {
			query=new StringBuilder();
			query.append(" select  ");
			query.append(" SUBSTRING(det.EP,40,1) tipoIngreso ");
			query.append(" from tobrapublicaprecompromisoencabezado enc with(Nolock)  ");
			query.append(" inner join tObraPublicaPreCompromisoDetalle as det with(Nolock)  ");
			query.append(" on enc.nFolioOPPreComHeader=det.nFolioOPPreComHeader ");
			query.append(" where FolioSAI=? and SUBSTRING(det.EP,40,1)='1'  ");
			
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, folioSAI );
			rs = ps.executeQuery();
			if ( rs.next() )
				resp=true;
			return resp;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
			if(query!=null)
				query.delete( 0, query.length() );
			
			query=null;
			
		}
	}
	public static boolean convTraeRecursoFiscal(Connection conn, int nFolioOPConvHeader) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder query=null;
		boolean resp=false;
		try {
			query=new StringBuilder();
			query.append(" select *from tObraPublicaConvModifDetalle with(Nolock) where nFolioOPConvHeader=?  and SUBSTRING(EP,40,1)='1'   ");
			
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, nFolioOPConvHeader );
			rs = ps.executeQuery();
			if (rs.next() )
				resp=true;
			return resp;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
			if(query!=null)
				query.delete( 0, query.length() );
			
			query=null;
			
		}
	}

	/**
	 * Devuelve el caso que dio origen a este expediente.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param folioContrato
	 *            Folio del contrato
	 * @return Caso activo o null si no se encuentra
	 * @throws SQLException
	 */
	public static Caso cargaCasoContrato( Connection conn, String folioContrato ) throws SQLException {

		log.debug( "Intentando encontrar caso para el contrato: " + folioContrato );

		PreparedStatement psFolio = null;
		ResultSet rs = null;

		StringBuilder query = new StringBuilder();
		query.append( "SELECT  foliosai " );
		query.append( "  FROM  tobrapublicacompromisoencabezado with(nolock)" );
		query.append( " WHERE  cdocumentohaplicado = 'S' " );
		query.append( "   AND  ccvecontrato = ?" );

		try {
			psFolio = conn.prepareStatement( query.toString() );
			psFolio.setString( 1, folioContrato );
			rs = psFolio.executeQuery();

			if ( rs.next() ) {
				String folioSAI = rs.getString( 1 );
				log.debug( "Se encontro folio: " + folioSAI + " para el contrato:  " + folioContrato );
				Caso c = new Caso();
				c.setFolio( folioSAI );
				return CasoManager.select( conn, c );
			} else
				return null;
		} finally {
			CloseObject.closeObject( psFolio );
			CloseObject.closeObject( rs );
		}
	}
}
