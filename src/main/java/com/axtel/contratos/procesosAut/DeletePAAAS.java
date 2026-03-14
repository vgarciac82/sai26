package com.axtel.contratos.procesosAut;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletePAAAS {

    private static final Logger log = LoggerFactory.getLogger(DeletePAAAS.class);

    public static void main(String[] args) {
        ProcessAgreement process = new ProcessAgreement();
        try {
            /*Borra el paaas disponible cuando 
			 * el presupuesto modificado de una partida es cero 
			 * Se ejecuta cada 20 de cada mes a las 3 de la mañana.
			 */
            process.borraPAAASPresupuestoCero();
        } catch (Exception exc) {
            log.warn("Error occurred", "Error en Process:  " + exc.toString());
        } finally {
            process = null;
        }
    }
}
