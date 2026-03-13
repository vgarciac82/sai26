package com.axtel.contratos.procesosAut;

import org.apache.log4j.Logger;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;

public class SendEmailAdquisiciones {
	private static final Logger	log						= Logger.getLogger(SendEmailAdquisiciones.class);
	public static void main( String[] args ) {
		ProcessAgreement process=new ProcessAgreement();
		try {
			/*Proceso automatico que se ejecta cada 20 de cada mes a las 3 de la mañana
			 * Envia correo a las áreas que tienen presupuesto por calendarizar en el PAAAS
			 */
			process.sendEmailPAAASPresupuesto();
		} catch (Exception exc) {
			log.warn("Error en Process:  " + exc.toString());
		}finally {
			process=null;
		}
	}

}
