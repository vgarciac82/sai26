package com.axtel.contratos.procesosAut;

import org.apache.log4j.Logger;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;

public class RequisicionesFirmaElectronica {
	private static final Logger	log						= Logger.getLogger(RequisicionesFirmaElectronica.class);
	public static void main( String[] args ) {
		ProcessAgreement process=new ProcessAgreement();
		try {
			/*Proceso automatico que se ejecuta cada 5 días
			 * Envía correo a los firmantes que tengan pendiente requisiciones con firma FIEL
			 */
			process.sendEmailRequisicionesFirmaFIEL();
		} catch ( Exception e ) {
			log.error( e );
			e.printStackTrace();
		}finally {
			process=null;
		}

	}

}
