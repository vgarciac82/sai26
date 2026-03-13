package com.syc.info.cfdi.export.config;


import java.util.Properties;

import com.syc.cfdi.util.CFDIUtils;


public class ReportConfig {

	private static Properties	reportProperties;

	public static void initConfig( String propertiesFilePath ) throws Exception {
		if ( reportProperties == null ) {
			ReportConfig.reportProperties = instance( propertiesFilePath );
		}
	}

	private static Properties instance( String propertiesFilePath ) throws Exception {

		Properties dbProperties = CFDIUtils.loadFileProperties( propertiesFilePath );
		return dbProperties;

	}

	public static String getReportPropertie( String propertieName ) throws Exception {
		if ( ReportConfig.reportProperties == null ) {
			throw new Exception( "Properties has not been initializate. " );
		}

		return ReportConfig.reportProperties.getProperty( propertieName );
	}
}
