package com.axtel.sai.sicove.entities;

import java.util.Base64;

public class VehicleFuelRequestDAO extends VehicleFuelRequest {

    private Vehicle vehicle;

    private EmployeeDAO responsible;

    private EmployeeDAO applicant;

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public EmployeeDAO getResponsible() {
        return responsible;
    }

    public void setResponsible(EmployeeDAO responsible) {
        this.responsible = responsible;
    }

    public EmployeeDAO getApplicant() {
        return applicant;
    }

    public void setApplicant(EmployeeDAO applicant) {
        this.applicant = applicant;
    }
}
