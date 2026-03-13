package com.axtel.cfdi.descargaMasiva;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;

import common.Logger;


public class EncodeBased64Binary {

	private static final Logger log = Logger.getLogger( EncodeBased64Binary.class );

	public String encodeFileToBase64Binary( String fileName ) throws IOException {

		File file = new File( fileName );
		byte[] bytes = loadFile( file );
		byte[] encoded = Base64.encodeBase64( bytes );
		String encodedString = new String( encoded );

		return encodedString;
	}

	private static byte[] loadFile( File file ) throws IOException {
		InputStream is = null;

		try {
			is = new FileInputStream( file );

			long length = file.length();
			if ( length > Integer.MAX_VALUE ) {
				// File is too large
			}
			byte[] bytes = new byte [( int ) length];

			int offset = 0;
			int numRead = 0;
			while ( offset < bytes.length && ( numRead = is.read( bytes, offset, bytes.length - offset ) ) >= 0 ) {
				offset += numRead;
			}

			if ( offset < bytes.length ) {
				throw new IOException( "Could not completely read file " + file.getName() );
			}
			return bytes;
		} finally {
			if ( is != null )
				try {
					is.close();
				} catch ( Exception e ) {
					log.warn( "Problemas cerrando InputStream: " + is );
				}
		}

	}

	public static void main( String args[] ) throws Exception {
		String certificateFilePath = "C:\\Vicente\\certs\\gacv820307ql8.cer";
		EncodeBased64Binary enc64 = new EncodeBased64Binary();
		String certificateInfo = enc64.encodeFileToBase64Binary( certificateFilePath );
		System.out.println( certificateInfo );
	}
}
