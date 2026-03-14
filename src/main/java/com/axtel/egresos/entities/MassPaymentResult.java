package com.axtel.egresos.entities;

import java.util.Base64;

public class MassPaymentResult {

    private String folio;

    private String fileName;

    private String log;

    private String recipt;

    private boolean success;

    public String getFileName() {
        return fileName;
    }

    public String getFolio() {
        return folio;
    }

    public String getLog() {
        return log;
    }

    public String getRecipt() {
        return recipt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setRecipt(String recipt) {
        this.recipt = recipt;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
