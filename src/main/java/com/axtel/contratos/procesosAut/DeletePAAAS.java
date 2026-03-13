package com.axtel.contratos.procesosAut;

import org.apache.log4j.Logger;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;

public class DeletePAAAS {

	private static final Logger	log						= Logger.getLogger(DeletePAAAS.class);
	public static void main( String[] args ) {
		ProcessAgreement process=new ProcessAgreement();
		try {
			/*Borra el paaas disponible cuando 
			 * el presupuesto modificado de una partida es cero 
			 * Se ejecuta cada 20 de cada mes a las 3 de la mañana.
			 */
			process.borraPAAASPresupuestoCero();
		} catch (Exception exc) {
			log.warn("Error en Process:  " + exc.toString());
		}finally {
			process=null;
		}
	}

}
