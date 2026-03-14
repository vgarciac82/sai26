package com.axtel.user.entities;

import java.util.Base64;

public class Employee {

    private String curp;

    private String email;

    private String firstSurname;

    private int idEmployee;

    private Job job;

    private String name;

    private String nss;

    private String rfc;

    private String secondSurname;

    private int status;

    private int supervisor;

    public String getCurp() {
        return curp;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public Job getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public String getNss() {
        return nss;
    }

    public String getRfc() {
        return rfc;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public int getStatus() {
        return status;
    }

    public int getSupervisor() {
        return supervisor;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSupervisor(int supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public String toString() {
        return "Employee [curp=" + curp + ", email=" + email + ", firstSurname=" + firstSurname + ", idEmployee=" + idEmployee + ", job=" + job + ", name=" + name + ", nss=" + nss + ", rfc=" + rfc + ", secondSurname=" + secondSurname + ", status=" + status + ", supervisor=" + supervisor + "]";
    }
}
