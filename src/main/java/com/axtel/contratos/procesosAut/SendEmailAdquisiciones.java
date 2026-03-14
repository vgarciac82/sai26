package com.axtel.contratos.procesosAut;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SendEmailAdquisiciones {

    private static final Logger log = LoggerFactory.getLogger(SendEmailAdquisiciones.class);

    public static void main(String[] args) {
        ProcessAgreement process = new ProcessAgreement();
        try {
            /*Proceso automatico que se ejecta cada 20 de cada mes a las 3 de la mañana
			 * Envia correo a las áreas que tienen presupuesto por calendarizar en el PAAAS
			 */
            process.sendEmailPAAASPresupuesto();
        } catch (Exception exc) {
            log.warn("Error occurred", "Error en Process:  " + exc.toString());
        } finally {
            process = null;
        }
    }
}
