package com.axtel.sai.sicove.entities;

import java.util.Base64;

public class EmployeeDAO {

    private int employeeNumber;

    private String budgetUnit;

    private String executiveUnit;

    private String employeeResponsibleName;

    private String position;

    @Override
    public String toString() {
        return "EmployeeDAO [employeeNumber=" + employeeNumber + ", budgetUnit=" + budgetUnit + ", executiveUnit=" + executiveUnit + ", employeeResponsibleName=" + employeeResponsibleName + ", position=" + position + "]";
    }

    public String getExecutiveUnit() {
        return executiveUnit;
    }

    public void setExecutiveUnit(String executiveUnit) {
        this.executiveUnit = executiveUnit;
    }

    public String getEmployeeResponsibleName() {
        return employeeResponsibleName;
    }

    public void setEmployeeResponsibleName(String employeeResponsibleName) {
        this.employeeResponsibleName = employeeResponsibleName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBudgetUnit() {
        return budgetUnit;
    }

    public void setBudgetUnit(String budgetUnit) {
        this.budgetUnit = budgetUnit;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
