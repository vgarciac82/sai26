package com.axtel.contratos.procesosAut;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendEmailProcessAutAgreement {

    private static final Logger log = LoggerFactory.getLogger(SendEmailProcessAutAgreement.class);

    public static void main(String[] args) {
        ProcessAgreement process = new ProcessAgreement();
        try {
            /*Proceso automatico que se ejecuta diario a las 3 am 
			 * Envía correo a las áreas con los contratos que están por finalizar su vigencia
			 */
            process.sendEmailAgreement();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        } finally {
            process = null;
        }
    }
}
