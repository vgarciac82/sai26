package com.axtel.egresos.services.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class OpinionResolveResponse {

    public static final String POSITIVE = "Positivo";

    boolean ok;

    boolean manualValidationRequired;

    String reasonCode;

    String message;

    SourceFromPdf source;

    String qrUrl;

    Map<String, String> sat;

    List<List<String>> rows;

    public OpinionResolveResponse() {
    }

    public OpinionResolveResponse(String rfc, String folio, LocalDate date, String senseLetter) {
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean isManualValidationRequired() {
        return manualValidationRequired;
    }

    public void setManualValidationRequired(boolean manualValidationRequired) {
        this.manualValidationRequired = manualValidationRequired;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SourceFromPdf getSource() {
        return source;
    }

    public void setSource(SourceFromPdf source) {
        this.source = source;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public Map<String, String> getSat() {
        return sat;
    }

    public void setSat(Map<String, String> sat) {
        this.sat = sat;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}
