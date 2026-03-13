package com.syc.contable.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.syc.gestion.util.DBConfigurator;


public class SolicitudPagoFirmaElectronicaTest {

	private static final Logger						log						= LogManager.getLogger( SolicitudPagoFirmaElectronicaTest.class );
	public static final String						dbPropertiesFilePath	= "/procesos/config/dbConfig.properties";
	private static Connection						conn					= null;
	private static SolicitudPagoFirmaElectronica	spfe					= null;

	public static Connection getConnection( DBConfigurator dbConfigurator, boolean autoCommit ) throws Exception {

		Class.forName( dbConfigurator.getDriverClassName() );
		Connection conn = DriverManager.getConnection( dbConfigurator.getUrl(), dbConfigurator.getUserName(), dbConfigurator.getPassword() );
		conn.setAutoCommit( autoCommit );
		return conn;

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBConfigurator dbConfigurator = DBConfigurator.instance( dbPropertiesFilePath );
		conn = getConnection( dbConfigurator, false );
		spfe = new SolicitudPagoFirmaElectronica();
		spfe.setCargaMasiva( false );
		spfe.setHeader( "tRELACIONGASTOSEncabezado" );
		spfe.setDetail( "tRELACIONGASTOSDetalle" );
		spfe.setDocument( "RELACIONGASTOS" );
		spfe.setField( "nFolioRelacionGastos" );
		spfe.setIdField( 5 );
		 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if ( conn != null )
			conn.close();
	}

	@Test
	public void testGetCuerpoCorreoVistoBueno() {

		try {
			String cuerpoCorreo = spfe.getCuerpoCorreoVistoBueno( conn );
			log.info( cuerpoCorreo );
			assertEquals( "1", cuerpoCorreo );
		} catch ( Exception e ) {
			log.error( e, e );
			fail( "No se logro ejecutar la prueba por el error: " + e.toString() );
		}

	}

}
