package com.axtel.cfdi.stamp.service;


import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axtel.cfdi.stamp.core.DigitalSignature;
import com.axtel.cfdi.stamp.core.UtilSecurity;
import com.syc.cfdi.CertificateUtil;
import com.syc.cfdi.db.CloseObject;

import mx.grupocorasa.sat.security.KeyLoaderEnumeration;
import mx.grupocorasa.sat.security.factory.KeyLoaderFactory;


public class DigitalSignatureService {

	private static final Logger log = LoggerFactory.getLogger( DigitalSignatureService.class );

	private static String[] loadKeyInformation( Connection conn ) throws Exception {
		String[] keyComponents = null;
		String query = "SELECT cCertFile , cKeyFile , cKey FROM cfdi_config ";
		Statement stmnt = null;
		ResultSet rs = null;
		try {
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery( query );
			if ( rs.next() ) {
				keyComponents = new String [3];
				keyComponents[0] = rs.getString( "cKeyFile" );
				keyComponents[1] = rs.getString( "cCertFile" );
				keyComponents[2] = rs.getString( "cKey" );
			}
			return keyComponents;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( stmnt );
		}
	}

	public static synchronized DigitalSignature loadDigitalSignature( Connection conn ) throws Exception {

		log.info( "Cargando key" );

		String[] keyInformation = DigitalSignatureService.loadKeyInformation( conn );

		if ( keyInformation == null )
			throw new RuntimeException( "No se especifico informacion respecto al sello digital." );
		else {
			DigitalSignature digitalSignature = new DigitalSignature();

			File keyFile = new File( UtilSecurity.decrypt( keyInformation[0] ) );
			File certFile = new File( UtilSecurity.decrypt( keyInformation[1] ) );

			digitalSignature.setKey( KeyLoaderFactory.createInstance( KeyLoaderEnumeration.PRIVATE_KEY_LOADER, new FileInputStream( keyFile ), UtilSecurity.decrypt( keyInformation[2] ) ).getKey() );
			digitalSignature.setKeyString( CertificateUtil.convertCertificateToString( keyFile.getAbsolutePath() ) );
			digitalSignature.setKeyPassword( UtilSecurity.decrypt( keyInformation[2] ) );

			digitalSignature.setCert( KeyLoaderFactory.createInstance( KeyLoaderEnumeration.PUBLIC_KEY_LOADER, new FileInputStream( certFile ) ).getKey() );
			digitalSignature.setCertString( CertificateUtil.convertCertificateToString( certFile.getAbsolutePath() ) );
			return digitalSignature;
		}

	}
}
