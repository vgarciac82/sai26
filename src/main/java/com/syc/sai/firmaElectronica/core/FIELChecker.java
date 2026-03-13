package com.syc.sai.firmaElectronica.core;


import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class FIELChecker {

	public static long isCertificateExpiringSoon( String certificatePath ) {
		try {

			FileInputStream fis = new FileInputStream( certificatePath );
			CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
			X509Certificate certificate = ( X509Certificate ) cf.generateCertificate( fis );
			return isCertificateExpiringSoon( certificate );

		} catch ( Exception e ) {
			throw new RuntimeException( e.toString(), e );
		}
	}

	public static long isCertificateExpiringSoon( X509Certificate certificate ) {
		Date expiryDate = certificate.getNotAfter();

		LocalDate expiryLocalDate = expiryDate.toInstant().atZone( ZoneId.systemDefault() ).toLocalDate();
		LocalDate currentDate = LocalDate.now();

		long daysUntilExpiry = ChronoUnit.DAYS.between( currentDate, expiryLocalDate );

		return daysUntilExpiry;
	}

}
