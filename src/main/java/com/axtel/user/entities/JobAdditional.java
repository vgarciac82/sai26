package com.axtel.user.entities;

import java.util.Base64;

public class JobAdditional {

    private String assignment;

    private ExecutingUnit executingUnit;

    private int id;

    private int idDepartment;

    private int idStructuredProgram;

    private String normativeUnit;

    private String responsibleUnit;

    public String getAssignment() {
        return assignment;
    }

    public ExecutingUnit getExecutingUnit() {
        return executingUnit;
    }

    public int getId() {
        return id;
    }

    public int getIdDepartment() {
        return idDepartment;
    }

    public int getIdStructuredProgram() {
        return idStructuredProgram;
    }

    public String getNormativeUnit() {
        return normativeUnit;
    }

    public String getResponsibleUnit() {
        return responsibleUnit;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public void setExecutingUnit(ExecutingUnit executingUnit) {
        this.executingUnit = executingUnit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdDepartment(int idDepartment) {
        this.idDepartment = idDepartment;
    }

    public void setIdStructuredProgram(int idStructuredProgram) {
        this.idStructuredProgram = idStructuredProgram;
    }

    public void setNormativeUnit(String normativeUnit) {
        this.normativeUnit = normativeUnit;
    }

    public void setResponsibleUnit(String responsibleUnit) {
        this.responsibleUnit = responsibleUnit;
    }

    @Override
    public String toString() {
        return "JobAdditional [id=" + id + ", assignment=" + assignment + ", executingUnit=" + executingUnit + ", idStructuredProgram=" + idStructuredProgram + ", responsibleUnit=" + responsibleUnit + ", normativeUnit=" + normativeUnit + ", idDepartment=" + idDepartment + "]";
    }
}
