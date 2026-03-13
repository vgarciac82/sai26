package com.axtel.sai.sicove.entities;


import java.math.BigDecimal;
import java.util.Date;


public class FuelAccountWalletRefund {

	private Integer		idRefund;
	private Integer		idFuelAccountWallets;
	private String		walletNumber;
	private BigDecimal	refundAmount;
	private Date		registrationDate;
	private String		userCapture;
	private boolean		supplierRefund;

	public Integer getIdRefund() {
		return idRefund;
	}

	public void setIdRefund( Integer idRefund ) {
		this.idRefund = idRefund;
	}

	public Integer getIdFuelAccountWallets() {
		return idFuelAccountWallets;
	}

	public void setIdFuelAccountWallets( Integer idFuelAccountWallets ) {
		this.idFuelAccountWallets = idFuelAccountWallets;
	}

	public String getWalletNumber() {
		return walletNumber;
	}

	public void setWalletNumber( String walletNumber ) {
		this.walletNumber = walletNumber;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount( BigDecimal refundAmount ) {
		this.refundAmount = refundAmount;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate( Date registrationDate ) {
		this.registrationDate = registrationDate;
	}

	public String getUserCapture() {
		return userCapture;
	}

	public void setUserCapture( String userCapture ) {
		this.userCapture = userCapture;
	}

	public boolean isSupplierRefund() {
		return supplierRefund;
	}

	public void setSupplierRefund( boolean supplierRefund ) {
		this.supplierRefund = supplierRefund;
	}

	@Override
	public String toString() {
		return "FuelAccountWalletRefund [idRefund=" + idRefund + ", idFuelAccountWallets=" + idFuelAccountWallets + ", walletNumber=" + walletNumber + ", refundAmount=" + refundAmount + ", registrationDate=" + registrationDate + ", userCapture=" + userCapture + ", supplierRefund=" + supplierRefund + "]";
	}

}
