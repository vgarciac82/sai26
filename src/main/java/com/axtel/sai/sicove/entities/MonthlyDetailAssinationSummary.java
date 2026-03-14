package com.axtel.sai.sicove.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Base64;

public class MonthlyDetailAssinationSummary {

    private Date requestDate;

    private int month;

    private BigDecimal authorizedAmount;

    private BigDecimal requestedAmount;

    private Integer asignationIdAccount;

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getAuthorizedAmount() {
        return authorizedAmount;
    }

    public void setAuthorizedAmount(BigDecimal authorizedAmount) {
        this.authorizedAmount = authorizedAmount;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getAsignationIdAccount() {
        return asignationIdAccount;
    }

    public void setAsignationIdAccount(Integer asignationIdAccount) {
        this.asignationIdAccount = asignationIdAccount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(asignationIdAccount, authorizedAmount, month, requestDate, requestedAmount);
    }

    @Override
    public String toString() {
        return "MonthlyDetailAssinationSummary [requestDate=" + requestDate + ", month=" + month + ", authorizedAmount=" + authorizedAmount + ", requestedAmount=" + requestedAmount + ", asignationIdAccount=" + asignationIdAccount + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MonthlyDetailAssinationSummary other = (MonthlyDetailAssinationSummary) obj;
        return Objects.equals(asignationIdAccount, other.asignationIdAccount) && Objects.equals(authorizedAmount, other.authorizedAmount) && month == other.month && Objects.equals(requestDate, other.requestDate) && Objects.equals(requestedAmount, other.requestedAmount);
    }
}
