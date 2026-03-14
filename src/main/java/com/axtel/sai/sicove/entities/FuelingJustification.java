package com.axtel.sai.sicove.entities;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Base64;

public class FuelingJustification {

    private int justificationId;

    private boolean withJustification;

    private int idCommision;

    private String justification;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT-6")
    private Date initialDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT-6")
    private Date endDate;

    private String countryId;

    private String stateName;

    private String municipalityName;

    public boolean isWithJustification() {
        return withJustification;
    }

    public void setWithJustification(boolean withJustification) {
        this.withJustification = withJustification;
    }

    public int getIdCommision() {
        return idCommision;
    }

    public void setIdCommision(int idCommision) {
        this.idCommision = idCommision;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public int getJustificationId() {
        return justificationId;
    }

    public void setJustificationId(int justificationId) {
        this.justificationId = justificationId;
    }

    @Override
    public String toString() {
        return "FuelingJustification [justificationId=" + justificationId + ", isWithJustification=" + withJustification + ", idCommision=" + idCommision + ", justification=" + justification + ", initialDate=" + initialDate + ", endDate=" + endDate + ", countryId=" + countryId + ", stateName=" + stateName + ", municipalityName=" + municipalityName + "]";
    }
}
