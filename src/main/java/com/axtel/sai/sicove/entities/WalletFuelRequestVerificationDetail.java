package com.axtel.sai.sicove.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Base64;

public class WalletFuelRequestVerificationDetail {

    private int idDetail;

    private int idVerification;

    private String ticketNumber;

    private BigDecimal ticketAmount;

    private Date ticketDate;

    private String ticketReference;

    private String ticketObservations;

    private boolean acepted;

    public int getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public int getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(int idVerification) {
        this.idVerification = idVerification;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public BigDecimal getTicketAmount() {
        return ticketAmount;
    }

    public void setTicketAmount(BigDecimal ticketAmount) {
        this.ticketAmount = ticketAmount;
    }

    public Date getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(Date ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getTicketReference() {
        return ticketReference;
    }

    public void setTicketReference(String ticketReference) {
        this.ticketReference = ticketReference;
    }

    public String getTicketObservations() {
        return ticketObservations;
    }

    public void setTicketObservations(String ticketObservations) {
        this.ticketObservations = ticketObservations;
    }

    public boolean isAcepted() {
        return acepted;
    }

    public void setAcepted(boolean acepted) {
        this.acepted = acepted;
    }

    @Override
    public String toString() {
        return "WalletFuelRequestVerificationDetail [idDetail=" + idDetail + ", idVerification=" + idVerification + ", ticketNumber=" + ticketNumber + ", ticketAmount=" + ticketAmount + ", ticketDate=" + ticketDate + ", ticketReference=" + ticketReference + ", ticketObservations=" + ticketObservations + ", acepted=" + acepted + "]";
    }
}
