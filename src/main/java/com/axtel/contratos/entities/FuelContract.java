package com.axtel.contratos.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Base64;

public class FuelContract {

    private int id;

    private String contractNumber;

    private BigDecimal totalMaxAmount;

    private boolean active;

    private Date registrationDate;

    private String employeeRegistration;

    private boolean byLiters;

    private String employeeAdministrator;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public BigDecimal getTotalMaxAmount() {
        return totalMaxAmount;
    }

    public void setTotalMaxAmount(BigDecimal totalMaxAmount) {
        this.totalMaxAmount = totalMaxAmount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getEmployeeRegistration() {
        return employeeRegistration;
    }

    public void setEmployeeRegistration(String employeeRegistration) {
        this.employeeRegistration = employeeRegistration;
    }

    public boolean isByLiters() {
        return byLiters;
    }

    public void setByLiters(boolean byLiters) {
        this.byLiters = byLiters;
    }

    @Override
    public String toString() {
        return "FuelContract [id=" + id + ", contractNumber=" + contractNumber + ", totalMaxAmount=" + totalMaxAmount + ", active=" + active + ", registrationDate=" + registrationDate + ", employeeRegistration=" + employeeRegistration + ", byLiters=" + byLiters + "]";
    }

    public String getEmployeeAdministrator() {
        return employeeAdministrator;
    }

    public void setEmployeeAdministrator(String employeeAdministrator) {
        this.employeeAdministrator = employeeAdministrator;
    }
}
