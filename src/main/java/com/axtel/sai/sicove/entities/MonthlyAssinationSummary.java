package com.axtel.sai.sicove.entities;

import java.math.BigDecimal;
import java.util.Base64;

public class MonthlyAssinationSummary {

    private Integer asignationIdAccount;

    private Integer asignationRequestMonth;

    private BigDecimal asignationAutorizedAmount;

    private BigDecimal fuelingAuthorizedAmount;

    private BigDecimal totalFuelingAuthorizedAmount;

    private BigDecimal fuelingRefundAmount;

    public Integer getAsignationIdAccount() {
        return asignationIdAccount;
    }

    public void setAsignationIdAccount(Integer asignationIdAccount) {
        this.asignationIdAccount = asignationIdAccount;
    }

    public Integer getAsignationRequestMonth() {
        return asignationRequestMonth;
    }

    public void setAsignationRequestMonth(Integer asignationRequestMonth) {
        this.asignationRequestMonth = asignationRequestMonth;
    }

    public BigDecimal getAsignationAutorizedAmount() {
        return asignationAutorizedAmount;
    }

    public void setAsignationAutorizedAmount(BigDecimal asignationAutorizedAmount) {
        this.asignationAutorizedAmount = asignationAutorizedAmount;
    }

    public BigDecimal getFuelingAuthorizedAmount() {
        return fuelingAuthorizedAmount;
    }

    public void setFuelingAuthorizedAmount(BigDecimal fuelingAuthorizedAmount) {
        this.fuelingAuthorizedAmount = fuelingAuthorizedAmount;
    }

    public BigDecimal getTotalFuelingAuthorizedAmount() {
        return totalFuelingAuthorizedAmount;
    }

    public void setTotalFuelingAuthorizedAmount(BigDecimal totalFuelingAuthorizedAmount) {
        this.totalFuelingAuthorizedAmount = totalFuelingAuthorizedAmount;
    }

    public BigDecimal getFuelingRefundAmount() {
        return fuelingRefundAmount;
    }

    public void setFuelingRefundAmount(BigDecimal fuelingRefundAmount) {
        this.fuelingRefundAmount = fuelingRefundAmount;
    }

    @Override
    public String toString() {
        return "MonthlyAssinationSummary [asignationIdAccount=" + asignationIdAccount + ", asignationRequestMonth=" + asignationRequestMonth + ", asignationAutorizedAmount=" + asignationAutorizedAmount + ", fuelingAuthorizedAmount=" + fuelingAuthorizedAmount + ", totalFuelingAuthorizedAmount=" + totalFuelingAuthorizedAmount + ", fuelingRefundAmount=" + fuelingRefundAmount + "]";
    }
}
