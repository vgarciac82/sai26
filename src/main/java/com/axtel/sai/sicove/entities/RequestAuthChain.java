package com.axtel.sai.sicove.entities;

import com.syc.gestion.core.Usuario;
import java.util.Base64;

public class RequestAuthChain {

    private String walletNumber;

    private String accountNumber;

    private String applicantName;

    private String applicantPosition;

    private String applicantMail;

    private String authorizerName;

    private String authorizerPosition;

    private String authorizerMail;

    private int authorizerEmployeeNumber;

    private Usuario initiatingUser;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAuthorizerEmployeeNumber() {
        return authorizerEmployeeNumber;
    }

    public void setAuthorizerEmployeeNumber(int authorizerEmployeeNumber) {
        this.authorizerEmployeeNumber = authorizerEmployeeNumber;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPosition() {
        return applicantPosition;
    }

    public void setApplicantPosition(String applicantPosition) {
        this.applicantPosition = applicantPosition;
    }

    public String getApplicantMail() {
        return applicantMail;
    }

    public void setApplicantMail(String applicantMail) {
        this.applicantMail = applicantMail;
    }

    public String getAuthorizerName() {
        return authorizerName;
    }

    public void setAuthorizerName(String authorizerName) {
        this.authorizerName = authorizerName;
    }

    public String getAuthorizerPosition() {
        return authorizerPosition;
    }

    public void setAuthorizerPosition(String authorizerPosition) {
        this.authorizerPosition = authorizerPosition;
    }

    public String getAuthorizerMail() {
        return authorizerMail;
    }

    public void setAuthorizerMail(String authorizerMail) {
        this.authorizerMail = authorizerMail;
    }

    public Usuario getInitiatingUser() {
        return initiatingUser;
    }

    public void setInitiatingUser(Usuario initiatingUser) {
        this.initiatingUser = initiatingUser;
    }

    @Override
    public String toString() {
        return "RequestAuthChain [walletNumber=" + walletNumber + ", accountNumber=" + accountNumber + ", applicantName=" + applicantName + ", applicantPosition=" + applicantPosition + ", applicantMail=" + applicantMail + ", authorizerName=" + authorizerName + ", authorizerPosition=" + authorizerPosition + ", authorizerMail=" + authorizerMail + ", authorizerEmployeeNumber=" + authorizerEmployeeNumber + ", initiatingUser=" + initiatingUser + "]";
    }
}
