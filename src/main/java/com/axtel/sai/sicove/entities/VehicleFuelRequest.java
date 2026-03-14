package com.axtel.sai.sicove.entities;

import java.math.BigDecimal;
import java.util.Base64;

public class VehicleFuelRequest {

    private int fuelingRequestId;

    private int vehicleId;

    private String employeeResponsible;

    private String userRequest;

    private FuelingJustification justification;

    private BigDecimal fuelingAmount;

    private BigDecimal estimatedKilometers;

    private BigDecimal authorizedAmount;

    private int idProcess;

    private int justificationId;

    private int idStatus;

    private String walletNumber;

    private String walletSupplierName;

    private String rejectJustification;

    private int idWallet;

    public int getFuelingRequestId() {
        return fuelingRequestId;
    }

    public void setFuelingRequestId(int fuelingRequestId) {
        this.fuelingRequestId = fuelingRequestId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getEmployeeResponsible() {
        return employeeResponsible;
    }

    public void setEmployeeResponsible(String employeeResponsible) {
        this.employeeResponsible = employeeResponsible;
    }

    public String getUserRequest() {
        return userRequest;
    }

    public void setUserRequest(String userRequest) {
        this.userRequest = userRequest;
    }

    public FuelingJustification getJustification() {
        return justification;
    }

    public void setJustification(FuelingJustification justification) {
        this.justification = justification;
    }

    public BigDecimal getFuelingAmount() {
        return fuelingAmount;
    }

    public void setFuelingAmount(BigDecimal fuelingAmount) {
        this.fuelingAmount = fuelingAmount;
    }

    public BigDecimal getEstimatedKilometers() {
        return estimatedKilometers;
    }

    public void setEstimatedKilometers(BigDecimal estimatedKilometers) {
        this.estimatedKilometers = estimatedKilometers;
    }

    public int getIdProcess() {
        return this.idProcess;
    }

    public void setIdProcess(int idProcess) {
        this.idProcess = idProcess;
    }

    public int getJustificationId() {
        return justificationId;
    }

    public void setJustificationId(int justificationId) {
        this.justificationId = justificationId;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public BigDecimal getAuthorizedAmount() {
        return authorizedAmount;
    }

    public void setAuthorizedAmount(BigDecimal authorizedAmount) {
        this.authorizedAmount = authorizedAmount;
    }

    public String getRejectJustification() {
        return this.rejectJustification;
    }

    public void setRejectJustification(String rejectJustification) {
        this.rejectJustification = rejectJustification;
    }

    public int getIdWallet() {
        return idWallet;
    }

    public void setIdWallet(int idWallet) {
        this.idWallet = idWallet;
    }

    public String getWalletSupplierName() {
        return walletSupplierName;
    }

    public void setWalletSupplierName(String walletSupplierName) {
        this.walletSupplierName = walletSupplierName;
    }

    @Override
    public String toString() {
        return "VehicleFuelRequest [fuelingRequestId=" + fuelingRequestId + ", vehicleId=" + vehicleId + ", employeeResponsible=" + employeeResponsible + ", userRequest=" + userRequest + ", justification=" + justification + ", fuelingAmount=" + fuelingAmount + ", estimatedKilometers=" + estimatedKilometers + ", authorizedAmount=" + authorizedAmount + ", idProcess=" + idProcess + ", justificationId=" + justificationId + ", idStatus=" + idStatus + ", walletNumber=" + walletNumber + ", walletSupplierName=" + walletSupplierName + ", rejectJustification=" + rejectJustification + ", idWallet=" + idWallet + "]";
    }
}
