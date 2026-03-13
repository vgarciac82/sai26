package com.axtel.sai.sicove.entities;


import java.math.BigDecimal;
import java.util.Date;


public class FuelAsignationVerification {

	private int			idVerification;
	private int			fuelingRequestId;
	private BigDecimal	currentWalletBalance;
	private BigDecimal	initialVehicleKilometers;
	private BigDecimal	currentVehicleKilometers;
	private BigDecimal	validationAmount;
	private Date		verificationCaptured;
	private Date		verificationAuthorized;
	private Date		verificationRejected;
	private String		userCapture;
	
	public int getIdVerification() {
		return idVerification;
	}
	
	public void setIdVerification( int idVerification ) {
		this.idVerification = idVerification;
	}
	
	public int getFuelingRequestId() {
		return fuelingRequestId;
	}
	
	public void setFuelingRequestId( int fuelingRequestId ) {
		this.fuelingRequestId = fuelingRequestId;
	}
	
	public BigDecimal getCurrentWalletBalance() {
		return currentWalletBalance;
	}
	
	public void setCurrentWalletBalance( BigDecimal currentWalletBalance ) {
		this.currentWalletBalance = currentWalletBalance;
	}
	
	public BigDecimal getCurrentVehicleKilometers() {
		return currentVehicleKilometers;
	}
	
	public void setCurrentVehicleKilometers( BigDecimal currentVehicleKilometers ) {
		this.currentVehicleKilometers = currentVehicleKilometers;
	}
	
	public BigDecimal getValidationAmount() {
		return validationAmount;
	}
	
	public void setValidationAmount( BigDecimal validationAmount ) {
		this.validationAmount = validationAmount;
	}
	
	public Date getVerificationCaptured() {
		return verificationCaptured;
	}
	
	public void setVerificationCaptured( Date verificationCaptured ) {
		this.verificationCaptured = verificationCaptured;
	}
	
	public Date getVerificationAuthorized() {
		return verificationAuthorized;
	}
	
	public void setVerificationAuthorized( Date verificationAuthorized ) {
		this.verificationAuthorized = verificationAuthorized;
	}
	
	public Date getVerificationRejected() {
		return verificationRejected;
	}
	
	public void setVerificationRejected( Date verificationRejected ) {
		this.verificationRejected = verificationRejected;
	}
	
	public String getUserCapture() {
		return userCapture;
	}
	
	public void setUserCapture( String userCapture ) {
		this.userCapture = userCapture;
	}

	@Override
	public String toString() {
		return "FuelAsignationVerification [idVerification=" + idVerification + ", fuelingRequestId=" + fuelingRequestId + ", currentWalletBalance=" + currentWalletBalance + ", currentVehicleKilometers=" + currentVehicleKilometers + ", validationAmount=" + validationAmount + ", verificationCaptured=" + verificationCaptured + ", verificationAuthorized=" + verificationAuthorized + ", verificationRejected=" + verificationRejected + ", userCapture=" + userCapture + "]";
	}

	public BigDecimal getInitialVehicleKilometers() {
		return initialVehicleKilometers;
	}

	public void setInitialVehicleKilometers( BigDecimal initialVehicleKilometers ) {
		this.initialVehicleKilometers = initialVehicleKilometers;
	}
	
}
