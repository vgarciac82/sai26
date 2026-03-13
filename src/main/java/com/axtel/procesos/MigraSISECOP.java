package com.axtel.procesos;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.util.Util;


public class MigraSISECOP {

	private static final String	URL_SOURCE				= "jdbc:mysql://localhost:3306/sisecop_original?useSSL=false";
	private static final String	URL_DEST				= "jdbc:sqlserver://10.0.0.194;database=sai_2024;autoReconnect=false";

	private final String		querySelectServicios	= "SELECT servicioId, usuarioUsuario, servicioTitulo, servicioFolioPre, " + "servicioObjetivos, confidencialidadId, servicioDuracion, servicioVinculacion, " + "estatusId, servicioCompleto, servicioCreacion, servicioModificacion, " + "servicioFolioAnio, servicioFolioNum, servicioGerencia, servicioCoordinacion, tipoId, " + "servicioListo FROM sicosis_servicios";
	private final String		querySelectServicioProd	= "SELECT sp.servicioId, sp.productoId, sp.servicioproductoDescripcion, sp.servicioproductoId FROM sicosis_serviciosproductos sp inner join  sicosis_servicios ss on sp.servicioId = ss.servicioId order by ss.servicioId";
	private final String		querySelectServicioAct	= "SELECT sa.* FROM sicosis_serviciosactividades sa inner join sicosis_servicios s on sa.servicioId = s.servicioId";
	private final String		querySelectServicioClv	= "SELECT sc.* FROM sicosis_serviciosclaves sc inner join sicosis_servicios  s on sc.servicioId = s.servicioId";
	private final String		querySelectServicioTerm	= "SELECT st.servicioId, st.servicioterminoArchivo, st.servicioterminoId, st.servicioterminoRuta FROM sicosis_serviciosterminos st inner join sicosis_servicios s on st.servicioId = s.servicioId";
	private final String		querySelectServicioPago	= "SELECT st.* FROM sicosis_serviciospagos st inner join sicosis_servicios s on st.servicioId = s.servicioId;";
	private final String		querySelectServicioSrv	= "SELECT st.* FROM sicosis_serviciosadicionales st inner join sicosis_servicios s on st.servicioId = s.servicioId;";
	private final String		querySelectServicioMpio	= "SELECT m.* from  municipios m inner join entidades e on m.entidadId = e.entidadId";
	private final String		querySelectServicioTerr	= "SELECT st.* FROM sicosis_serviciosterritorios st inner join sicosis_servicios s on st.servicioId = s.servicioId";

	private final String		queryInsertServicios	= "SET IDENTITY_INSERT sisecop_servicios ON;\nINSERT INTO sisecop_servicios (servicioId, usuarioUsuario, servicioTitulo, servicioFolioPre, " + "servicioObjetivos, confidencialidadId, servicioDuracion, servicioVinculacion, " + "estatusId, servicioCompleto, servicioCreacion, servicioModificacion, " + "servicioFolioAnio, servicioFolioNum, servicioGerencia, servicioCoordinacion, tipoId, " + "servicioListo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);SET IDENTITY_INSERT sisecop_servicios OFF";
	private final String		queryInsertServicioProd	= "SET IDENTITY_INSERT sisecop_serviciosproductos ON;\nINSERT INTO sisecop_serviciosproductos (servicioId, productoId, servicioproductoDescripcion,servicioproductoId)VALUES (?, ?, ?, ?);\nSET IDENTITY_INSERT sisecop_serviciosproductos OFF";
	private final String		queryInsertServicioAct	= "SET IDENTITY_INSERT sisecop_serviciosactividades ON;\nINSERT INTO sisecop_serviciosactividades (servicioactividadId, servicioId, servicioactividadAnio, mesId, servicioactividadDescripcion)VALUES (?, ?, ?, ?, ?);\nSET IDENTITY_INSERT sisecop_serviciosactividades OFF";
	private final String		queryInsertServicioClv	= "SET IDENTITY_INSERT sisecop_serviciosclaves ON;\nINSERT INTO sisecop_serviciosclaves (servicioclaveId, servicioId, servicioclaveAnioIni, servicioclaveAnioFin, servicioclaveUnidad, servicioclaveGerencia, servicioclavePartida)VALUES (?, ?, ?, ?, ?, ?, ?);\nSET IDENTITY_INSERT sisecop_serviciosclaves OFF";
	private final String		queryInsertServicioTerm	= "SET IDENTITY_INSERT sisecop_serviciosterminos ON;\nINSERT INTO sisecop_serviciosterminos (servicioId, servicioterminoArchivo, servicioterminoId, servicioterminoRuta)VALUES (?, ?, ?, ?);\nSET IDENTITY_INSERT sisecop_serviciosterminos OFF";
	private final String		queryInsertServicioPago	= "SET IDENTITY_INSERT sisecop_serviciospagos ON;\nINSERT INTO sisecop_serviciospagos (serviciopagoId, servicioId, serviciopagoAnio, mesId, serviciopagoCantidad)VALUES (?, ?, ?, ?,?);\nSET IDENTITY_INSERT sisecop_serviciospagos OFF";
	private final String		queryInsertServicioSrv	= "SET IDENTITY_INSERT sisecop_serviciosadicionales ON;\nINSERT INTO sisecop_serviciosadicionales (servicioId, servicioadicionalArchivo, servicioadicionalId, servicioadicionalRuta)VALUES (?, ?, ?, ?);\nSET IDENTITY_INSERT sisecop_serviciosadicionales OFF";
	private final String		queryInsertServicioMpio	= "INSERT INTO sisecop_municipios (municipioId, entidadId, municipioNombre)VALUES (?, ?, ?)";
	private final String		queryInsertServicioTerr	= "SET IDENTITY_INSERT sisecop_serviciosterritorios ON;\nINSERT INTO sisecop_serviciosterritorios (servicioId, entidadId, municipioId, servicioterritorioId)VALUES (?, ?, ?, ?);\nSET IDENTITY_INSERT sisecop_serviciosterritorios OFF";

	private Connection			source					= null;
	private Connection			dest					= null;

	public static void main( String[] args ) {
		MigraSISECOP migra = new MigraSISECOP();
		migra.startMigration();
	}

	private void startMigration() {
		source = getMySQLConnection();
		dest = getDestConnection();
		try {

			migraServicios();
			migraServiciosProductos();
			migraServiciosActividades();
			migraServiciosClaves();
			migraServiciosTDR();
			migraServiciosPago();
			migraServiciosAdicionales();
			migraMunicipios();
			migraTerritorio();
			source.commit();
			dest.commit();

		} catch ( Exception e ) {
			e.printStackTrace();
			Util.rollback( dest );
			Util.rollback( source );

		} finally {
			CloseObject.closeObject( source );
			CloseObject.closeObject( dest );
		}
	}

	private void migraTerritorio() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosTerr = source.prepareStatement( querySelectServicioTerr );
		final PreparedStatement psInsertServiciosTerr = dest.prepareStatement( queryInsertServicioTerr );

		try {

			rs = psSelectServiciosTerr.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciosterritorios a sisecop_serviciosterritorios" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "servicioterritorioId" ) );
				psInsertServiciosTerr.setInt( 1, rs.getInt( "servicioId" ) );
				psInsertServiciosTerr.setInt( 2, rs.getInt( "entidadId" ) );
				psInsertServiciosTerr.setInt( 3, rs.getInt( "municipioId" ) );
				psInsertServiciosTerr.setInt( 4, rs.getInt( "servicioterritorioId" ) );

				psInsertServiciosTerr.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_serviciosterritorios a sisecop_serviciosterritorios terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosTerr );
			CloseObject.closeObject( psInsertServiciosTerr );
		}

	}

	private void migraMunicipios() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosMpio = source.prepareStatement( querySelectServicioMpio );
		final PreparedStatement psInsertServiciosMpio = dest.prepareStatement( queryInsertServicioMpio );

		try {

			rs = psSelectServiciosMpio.executeQuery();
			System.out.println( "Iniciando migracion de tabla: municipios a sisecop_municipios" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "municipioId" ) );
				psInsertServiciosMpio.setInt( 1, rs.getInt( "municipioId" ) );
				psInsertServiciosMpio.setInt( 2, rs.getInt( "entidadId" ) );
				psInsertServiciosMpio.setString( 3, rs.getString( "municipioNombre" ) );

				psInsertServiciosMpio.executeUpdate();
			}
			System.out.println( "Migracion de tabla municipios a sisecop_municipios terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosMpio );
			CloseObject.closeObject( psInsertServiciosMpio );
		}

	}

	private void migraServiciosAdicionales() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosSrv = source.prepareStatement( querySelectServicioSrv );
		final PreparedStatement psInsertServiciosSrv = dest.prepareStatement( queryInsertServicioSrv );

		try {

			rs = psSelectServiciosSrv.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciosadicionales a sisecop_serviciosadicionales" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "servicioadicionalId" ) );
				psInsertServiciosSrv.setInt( 1, rs.getInt( "servicioId" ) );
				psInsertServiciosSrv.setString( 2, rs.getString( "servicioadicionalArchivo" ) );
				psInsertServiciosSrv.setInt( 3, rs.getInt( "servicioadicionalId" ) );
				psInsertServiciosSrv.setString( 4, rs.getString( "servicioadicionalRuta" ) );

				psInsertServiciosSrv.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_serviciosadicionales a sisecop_serviciosadicionales terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosSrv );
			CloseObject.closeObject( psInsertServiciosSrv );
		}

	}

	private void migraServiciosPago() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosPago = source.prepareStatement( querySelectServicioPago );
		final PreparedStatement psInsertServiciosPago = dest.prepareStatement( queryInsertServicioPago );

		try {

			rs = psSelectServiciosPago.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciospagos a sisecop_serviciospagos" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "serviciopagoId" ) );
				psInsertServiciosPago.setInt( 1, rs.getInt( "serviciopagoId" ) );
				psInsertServiciosPago.setInt( 2, rs.getInt( "servicioId" ) );
				psInsertServiciosPago.setString( 3, rs.getString( "serviciopagoAnio" ) );
				psInsertServiciosPago.setInt( 4, rs.getInt( "mesId" ) );
				psInsertServiciosPago.setBigDecimal( 5, rs.getBigDecimal( "serviciopagoCantidad" ) );

				psInsertServiciosPago.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_serviciosterminos a sisecop_serviciosterminos terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosPago );
			CloseObject.closeObject( psInsertServiciosPago );
		}

	}

	private void migraServiciosTDR() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosTerm = source.prepareStatement( querySelectServicioTerm );
		final PreparedStatement psInsertServiciosTerm = dest.prepareStatement( queryInsertServicioTerm );

		try {

			rs = psSelectServiciosTerm.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciosterminos a sisecop_serviciosterminos" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "servicioterminoId" ) );
				psInsertServiciosTerm.setInt( 1, rs.getInt( "servicioId" ) );
				psInsertServiciosTerm.setString( 2, rs.getString( "servicioterminoArchivo" ) );
				psInsertServiciosTerm.setInt( 3, rs.getInt( "servicioterminoId" ) );
				psInsertServiciosTerm.setString( 4, rs.getString( "servicioterminoRuta" ) );

				psInsertServiciosTerm.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_serviciosterminos a sisecop_serviciosterminos terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosTerm );
			CloseObject.closeObject( psInsertServiciosTerm );
		}

	}

	private void migraServiciosClaves() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosClv = source.prepareStatement( querySelectServicioClv );
		final PreparedStatement psInsertServiciosClv = dest.prepareStatement( queryInsertServicioClv );

		try {

			rs = psSelectServiciosClv.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciosactividades a sisecop_serviciosactividades" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "servicioclaveId" ) );
				psInsertServiciosClv.setInt( 1, rs.getInt( "servicioclaveId" ) );
				psInsertServiciosClv.setInt( 2, rs.getInt( "servicioId" ) );
				psInsertServiciosClv.setInt( 3, rs.getInt( "servicioclaveAnioIni" ) );
				psInsertServiciosClv.setInt( 4, rs.getInt( "servicioclaveAnioFin" ) );
				psInsertServiciosClv.setString( 5, rs.getString( "servicioclaveUnidad" ) );
				psInsertServiciosClv.setString( 6, rs.getString( "servicioclaveGerencia" ) );
				psInsertServiciosClv.setString( 7, rs.getString( "servicioclavePartida" ) );

				psInsertServiciosClv.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_servicios a sisecop_servicios terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosClv );
			CloseObject.closeObject( psInsertServiciosClv );
		}

	}

	private void migraServiciosActividades() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosAct = source.prepareStatement( querySelectServicioAct );
		final PreparedStatement psInsertServiciosAct = dest.prepareStatement( queryInsertServicioAct );

		try {

			rs = psSelectServiciosAct.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciosactividades a sisecop_serviciosactividades" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "servicioactividadId" ) );
				psInsertServiciosAct.setInt( 1, rs.getInt( "servicioactividadId" ) );
				psInsertServiciosAct.setInt( 2, rs.getInt( "servicioId" ) );
				psInsertServiciosAct.setInt( 3, rs.getInt( "servicioactividadAnio" ) );
				psInsertServiciosAct.setInt( 4, rs.getInt( "mesId" ) );
				psInsertServiciosAct.setString( 5, rs.getString( "servicioactividadDescripcion" ) );

				psInsertServiciosAct.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_servicios a sisecop_servicios terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosAct );
			CloseObject.closeObject( psInsertServiciosAct );
		}

	}

	private void migraServiciosProductos() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServiciosProd = source.prepareStatement( querySelectServicioProd );
		final PreparedStatement psInsertServiciosProd = dest.prepareStatement( queryInsertServicioProd );

		try {

			rs = psSelectServiciosProd.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_serviciosproductos a sisecop_serviciosproductos" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro:\n servicioId[" +  rs.getInt( "servicioId" )  + "]productoId[" + rs.getInt( "productoId" ) + "]servicioproductoDescripcion[" 
						+ rs.getString( "servicioproductoDescripcion" ) + "]servicioproductoId["+rs.getInt( "servicioproductoId" )  + "]");
				psInsertServiciosProd.setInt( 1, rs.getInt( "servicioId" ) );
				psInsertServiciosProd.setInt( 2, rs.getInt( "productoId" ) );
				psInsertServiciosProd.setString( 3, rs.getString( "servicioproductoDescripcion" ) );
				psInsertServiciosProd.setInt( 4, rs.getInt( "servicioproductoId" ) );

				psInsertServiciosProd.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_servicios a sisecop_servicios terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServiciosProd );
			CloseObject.closeObject( psInsertServiciosProd );
		}

	}

	private void migraServicios() throws SQLException {
		ResultSet rs = null;
		final PreparedStatement psSelectServicios = source.prepareStatement( querySelectServicios );
		final PreparedStatement psInsertServicios = dest.prepareStatement( queryInsertServicios );

		try {

			rs = psSelectServicios.executeQuery();
			System.out.println( "Iniciando migracion de tabla: sicosis_servicios a sisecop_servicios" );

			while ( rs.next() ) {
				System.out.println( "Migrando registro " + rs.getInt( "servicioId" ) );
				psInsertServicios.setInt( 1, rs.getInt( "servicioId" ) );
				psInsertServicios.setString( 2, rs.getString( "usuarioUsuario" ) );
				psInsertServicios.setString( 3, rs.getString( "servicioTitulo" ) );
				psInsertServicios.setString( 4, rs.getString( "servicioFolioPre" ) );
				psInsertServicios.setString( 5, rs.getString( "servicioObjetivos" ) );
				psInsertServicios.setInt( 6, rs.getInt( "confidencialidadId" ) );
				psInsertServicios.setInt( 7, rs.getInt( "servicioDuracion" ) );
				psInsertServicios.setString( 8, rs.getString( "servicioVinculacion" ) );
				psInsertServicios.setInt( 9, rs.getInt( "estatusId" ) );
				psInsertServicios.setBoolean( 10, rs.getBoolean( "servicioCompleto" ) );
				psInsertServicios.setDate( 11, rs.getDate( "servicioCreacion" ) );
				psInsertServicios.setDate( 12, rs.getDate( "servicioModificacion" ) );
				psInsertServicios.setInt( 13, rs.getInt( "servicioFolioAnio" ) );
				psInsertServicios.setInt( 14, rs.getInt( "servicioFolioNum" ) );
				psInsertServicios.setString( 15, rs.getString( "servicioGerencia" ) );
				psInsertServicios.setString( 16, rs.getString( "servicioCoordinacion" ) );
				psInsertServicios.setInt( 17, rs.getInt( "tipoId" ) );
				psInsertServicios.setBoolean( 18, rs.getBoolean( "servicioListo" ) );

				psInsertServicios.executeUpdate();
			}
			System.out.println( "Migracion de tabla sicosis_servicios a sisecop_servicios terminada" );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( psSelectServicios );
			CloseObject.closeObject( psInsertServicios );
		}

	}

	private final Connection getMySQLConnection() {
		String user = "sicove";
		String password = "sicove";

		try {
			System.out.println( "Conectando a MySQL" );
			Class.forName( "com.mysql.cj.jdbc.Driver" ).newInstance();
			Connection conn = DriverManager.getConnection( URL_SOURCE, user, password );
			conn.setAutoCommit( false );
			System.out.println( "Conectado a MySQL" );
			return conn;
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new RuntimeException( "SQLException " + e.toString(), e );
		} catch ( InstantiationException e ) {
			e.printStackTrace();
			throw new RuntimeException( "SQLException " + e.toString(), e );
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
			throw new RuntimeException( "SQLException " + e.toString(), e );
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace();
			throw new RuntimeException( "SQLException " + e.toString(), e );
		}
	}

	private final Connection getDestConnection() {
		String user = "sai";
		String password = "S412020admin";

		try {
			System.out.println( "Conectando a SQL Server" );
			Connection conn = DriverManager.getConnection( URL_DEST, user, password );
			conn.setAutoCommit( false );
			System.out.println( "Conectado a SQL Server" );
			return conn;
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new RuntimeException( "Error de sql: " + e, e );
		}
	}

}
