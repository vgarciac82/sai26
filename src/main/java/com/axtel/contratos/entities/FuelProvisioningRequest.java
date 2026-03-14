package com.axtel.contratos.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Base64;

public class FuelProvisioningRequest {

    private Date autorizationDate;

    private BigDecimal autorizedAmount;

    private int fuelContractAccountId;

    private int fuelProvisioningRequestId;

    private String rejectJustification;

    private BigDecimal requestAmount;

    private Date requestDate;

    private String requestJustification;

    private int requestMonth;

    private int requestStatus;

    private String userRequest;

    private int processId;

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FuelProvisioningRequest other = (FuelProvisioningRequest) obj;
        return Objects.equals(autorizationDate, other.autorizationDate) && Objects.equals(autorizedAmount, other.autorizedAmount) && fuelContractAccountId == other.fuelContractAccountId && fuelProvisioningRequestId == other.fuelProvisioningRequestId && Objects.equals(rejectJustification, other.rejectJustification) && Objects.equals(requestAmount, other.requestAmount) && Objects.equals(requestDate, other.requestDate) && Objects.equals(requestJustification, other.requestJustification) && requestMonth == other.requestMonth && requestStatus == other.requestStatus && Objects.equals(userRequest, other.userRequest);
    }

    public Date getAutorizationDate() {
        return autorizationDate;
    }

    public BigDecimal getAutorizedAmount() {
        return autorizedAmount;
    }

    public int getFuelContractAccountId() {
        return fuelContractAccountId;
    }

    public int getFuelProvisioningRequestId() {
        return fuelProvisioningRequestId;
    }

    public String getRejectJustification() {
        return rejectJustification;
    }

    public BigDecimal getRequestAmount() {
        return requestAmount;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public String getRequestJustification() {
        return requestJustification;
    }

    public int getRequestMonth() {
        return requestMonth;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public String getUserRequest() {
        return userRequest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(autorizationDate, autorizedAmount, fuelContractAccountId, fuelProvisioningRequestId, rejectJustification, requestAmount, requestDate, requestJustification, requestMonth, requestStatus, userRequest);
    }

    public void setAutorizationDate(Date autorizationDate) {
        this.autorizationDate = autorizationDate;
    }

    public void setAutorizedAmount(BigDecimal autorizedAmount) {
        this.autorizedAmount = autorizedAmount;
    }

    public void setFuelContractAccountId(int fuelContractAccountId) {
        this.fuelContractAccountId = fuelContractAccountId;
    }

    public void setFuelProvisioningRequestId(int fuelProvisioningRequestId) {
        this.fuelProvisioningRequestId = fuelProvisioningRequestId;
    }

    public void setRejectJustification(String rejectJustification) {
        this.rejectJustification = rejectJustification;
    }

    public void setRequestAmount(BigDecimal requestAmount) {
        this.requestAmount = requestAmount;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public void setRequestJustification(String requestJustification) {
        this.requestJustification = requestJustification;
    }

    public void setRequestMonth(int requestMonth) {
        this.requestMonth = requestMonth;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setUserRequest(String userRequest) {
        this.userRequest = userRequest;
    }

    @Override
    public String toString() {
        return "FuelProvisioningRequest [autorizationDate=" + autorizationDate + ", autorizedAmount=" + autorizedAmount + ", fuelContractAccountId=" + fuelContractAccountId + ", fuelProvisioningRequestId=" + fuelProvisioningRequestId + ", rejectJustification=" + rejectJustification + ", requestAmount=" + requestAmount + ", requestDate=" + requestDate + ", requestJustification=" + requestJustification + ", requestMonth=" + requestMonth + ", requestStatus=" + requestStatus + ", userRequest=" + userRequest + ", processId=" + processId + "]";
    }
}
