package com.syc.adquisiciones.core;

import java.util.Base64;

public class CCorreo {

    private String to;

    private String subject;

    private String body;

    private String folioPrecom;

    private int consecutivoPrecom;

    public String getFolioPrecom() {
        return folioPrecom;
    }

    public void setFolioPrecom(String folioPrecom) {
        this.folioPrecom = folioPrecom;
    }

    public int getConsecutivoPrecom() {
        return consecutivoPrecom;
    }

    public void setConsecutivoPrecom(int consecutivoPrecom) {
        this.consecutivoPrecom = consecutivoPrecom;
    }

    public CCorreo() {
    }

    public CCorreo(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public CCorreo(String to, String subject, String body, String folioPrecom, int consecutivoPrecom) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.folioPrecom = folioPrecom;
        this.consecutivoPrecom = consecutivoPrecom;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }
}
