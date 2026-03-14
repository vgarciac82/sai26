package com.axtel.contratos.core;

import java.util.List;
import java.util.Base64;

public class ContractQuestionnaire {

    public static final String APPLICATION = "APARTADO";

    public static final String FORMAT = "pdf";

    private List<QuestionnaireAnswer> answers;

    private int cabinetId;

    private String employeeNumber;

    private String captureEmployeeLogin;

    private String idRequest;

    private boolean apply15D = true;

    private int status;

    private boolean applyQuestionnaire = true;

    /**
     * @return the answers
     */
    public List<QuestionnaireAnswer> getAnswers() {
        return answers;
    }

    /**
     * @return the cabinetId
     */
    public int getCabinetId() {
        return cabinetId;
    }

    /**
     * @return the login
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /**
     * @return the idRequest
     */
    public String getIdRequest() {
        return idRequest;
    }

    public int getStatus() {
        return this.status;
    }

    /**
     * @param answers
     *            the answers to set
     */
    public void setAnswers(List<QuestionnaireAnswer> answers) {
        this.answers = answers;
    }

    /**
     * @param cabinetId
     *            the cabinetId to set
     */
    public void setCabinetId(int cabinetId) {
        this.cabinetId = cabinetId;
    }

    /**
     * @param login
     *            the login to set
     */
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    /**
     * @param idRequest
     *            the idRequest to set
     */
    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the apply15D
     */
    public boolean isApply15D() {
        return apply15D;
    }

    /**
     * @param apply15d
     *            the apply15D to set
     */
    public void setApply15D(boolean apply15d) {
        apply15D = apply15d;
    }

    public String getCaptureEmployeeLogin() {
        return captureEmployeeLogin;
    }

    public void setCaptureEmployeeLogin(String captureEmployeeLogin) {
        this.captureEmployeeLogin = captureEmployeeLogin;
    }

    public boolean isApplyQuestionnaire() {
        return applyQuestionnaire;
    }

    public void setApplyQuestionnaire(boolean applyQuestionnaire) {
        this.applyQuestionnaire = applyQuestionnaire;
    }
}
