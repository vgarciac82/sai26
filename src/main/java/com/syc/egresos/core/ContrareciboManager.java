package com.syc.egresos.core;


import java.sql.Connection;
import java.util.Formatter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.EjercicioFiscalManager;


public class ContrareciboManager {

	private static final Logger log = Logger.getLogger( ContrareciboManager.class );

	private static String generaCadenaPostfijo( int consecutivo ) {
		Formatter formatter = null;
		try {
			formatter = new Formatter();
			return String.valueOf( formatter.format( "%05d", consecutivo ) );
		} finally {
			if ( formatter != null )
				formatter.close();
		}
	}

	public static String generaContrarecibo( Connection conn, String centroContable, String cxpPrefijo ) throws Exception {

		log.info( "Generando CxP nueva para el CC " + centroContable + "Prefijo: " + cxpPrefijo );
		CFSequenceManager seqManager = CFSequenceManager.getInstance( GestionInterface.ATT_CONEXION );
		return generaContrarecibo( conn, centroContable, cxpPrefijo, seqManager );

	}

	public static String generaContrarecibo( Connection conn, String centroContable, String cxpPrefijo, CFSequenceManager seqManager ) throws Exception {

		log.info( "Generando CxP nueva para el CC " + centroContable + "Prefijo: " + cxpPrefijo );

		String key = "CR-" + centroContable;
		log.trace( "Generando sequencia nueva para: " + key );

		int consecutivo = seqManager.nextVal( key );
		log.info( "Sequencia generada: " + key );

		String cadPostfijo = generaCadenaPostfijo( consecutivo );
		log.debug( "Se genero el postfijo: " + cadPostfijo );

		String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo( conn ).getaEjercicioFiscal();
		log.debug( "Ejercicio Fiscal: " + cadPostfijo );

		String cxp = StringUtils.trim(  centroContable + cxpPrefijo + ejercicioFiscal + "1" + cadPostfijo );

		log.info( "Se genero contrarecibo: " + cxp );
		return cxp;

	}

}
