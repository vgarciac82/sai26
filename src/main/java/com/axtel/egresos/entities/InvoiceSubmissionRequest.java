package com.axtel.egresos.entities;

import java.io.File;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import java.util.Base64;

public class InvoiceSubmissionRequest {

    private Firmante authFirmante;

    private String budgetItem;

    private File invoices;

    private File opinions;

    private FirmanteSuplente[] suplentes;

    private Firmante voBoFirmante;

    public InvoiceSubmissionRequest(File invoices, File opinions, Firmante authFirmante, Firmante voBoFirmante, FirmanteSuplente[] suplentes) {
        this.invoices = invoices;
        this.authFirmante = authFirmante;
        this.suplentes = suplentes;
        this.voBoFirmante = voBoFirmante;
        this.setOpinions(opinions);
    }

    public Firmante getAuthFirmante() {
        return authFirmante;
    }

    public String getBudgetItem() {
        return this.budgetItem;
    }

    public File getInvoices() {
        return invoices;
    }

    public File getOpinions() {
        return opinions;
    }

    public FirmanteSuplente[] getSuplentes() {
        return suplentes;
    }

    public Firmante getVoBoFirmante() {
        return voBoFirmante;
    }

    public void setAuthFirmante(Firmante authFirmante) {
        this.authFirmante = authFirmante;
    }

    public void setBudgetItem(String budgetItem) {
        this.budgetItem = budgetItem;
    }

    public void setInvoices(File invoices) {
        this.invoices = invoices;
    }

    public void setOpinions(File opinions) {
        this.opinions = opinions;
    }

    public void setSuplentes(FirmanteSuplente[] suplentes) {
        this.suplentes = suplentes;
    }

    public void setVoBoFirmante(Firmante voBoFirmante) {
        this.voBoFirmante = voBoFirmante;
    }
}
