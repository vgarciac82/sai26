package com.axtel.egresos.services.dto;

import java.time.LocalDate;
import java.util.Base64;

public class SourceFromPdf {

    String rfc;

    String folio;

    LocalDate date;

    String senseLetter;

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSenseLetter() {
        return senseLetter;
    }

    public void setSenseLetter(String senseLetter) {
        this.senseLetter = senseLetter;
    }
}
