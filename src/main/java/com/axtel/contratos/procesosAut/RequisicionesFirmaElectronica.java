package com.axtel.contratos.procesosAut;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequisicionesFirmaElectronica {

    private static final Logger log = LoggerFactory.getLogger(RequisicionesFirmaElectronica.class);

    public static void main(String[] args) {
        ProcessAgreement process = new ProcessAgreement();
        try {
            /*Proceso automatico que se ejecuta cada 5 días
			 * Envía correo a los firmantes que tengan pendiente requisiciones con firma FIEL
			 */
            process.sendEmailRequisicionesFirmaFIEL();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            process = null;
        }
    }
}
