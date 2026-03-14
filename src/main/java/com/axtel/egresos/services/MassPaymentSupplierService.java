package com.axtel.egresos.services;

import java.util.List;
import com.axtel.egresos.entities.InvoiceSubmissionRequest;
import com.axtel.egresos.entities.MassPaymentResult;
import com.axtel.egresos.services.dto.OpinionResolveResponse;
import com.syc.cfdi.MassPaymentInvoiceComponents;
import com.syc.gestion.core.Usuario;
import java.util.Base64;

public interface MassPaymentSupplierService {

    List<MassPaymentResult> processSubmission(InvoiceSubmissionRequest submission, Usuario user);

    MassPaymentResult makePayment(String invoiceName, MassPaymentInvoiceComponents payment, Usuario user, InvoiceSubmissionRequest submission, OpinionResolveResponse sco);
}
