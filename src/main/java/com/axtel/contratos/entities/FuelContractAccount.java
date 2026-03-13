package com.axtel.contratos.entities;


import java.math.BigDecimal;
import java.util.Date;


public class FuelContractAccount {

	private int			idAccount;
	private int			idContract;
	private int			employeeResponsible;
	private int			idUnit;
	private String		accountNumber;
	private Date		registrationDate	= new Date();
	private String		userRegistration;
	private BigDecimal	monthlyAsignation;
	
	public int getIdAccount() {
		return idAccount;
	}
	
	public void setIdAccount( int idAccount ) {
		this.idAccount = idAccount;
	}
	
	public int getIdContract() {
		return idContract;
	}
	
	public void setIdContract( int idContract ) {
		this.idContract = idContract;
	}
	
	public int getEmployeeResponsible() {
		return employeeResponsible;
	}
	
	public void setEmployeeResponsible( int employeeResponsible ) {
		this.employeeResponsible = employeeResponsible;
	}
	
	public int getIdUnit() {
		return idUnit;
	}
	
	public void setIdUnit( int idUnit ) {
		this.idUnit = idUnit;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber( String accountNumber ) {
		this.accountNumber = accountNumber;
	}
	
	public Date getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate( Date registrationDate ) {
		this.registrationDate = registrationDate;
	}
	
	public String getUserRegistration() {
		return userRegistration;
	}
	
	public void setUserRegistration( String userRegistration ) {
		this.userRegistration = userRegistration;
	}
	
	public BigDecimal getMonthlyAsignation() {
		return monthlyAsignation;
	}
	
	public void setMonthlyAsignation( BigDecimal monthlyAsignation ) {
		this.monthlyAsignation = monthlyAsignation;
	}

	@Override
	public String toString() {
		return "FuelContractAccount [idAccount=" + idAccount + ", idContract=" + idContract + ", employeeResponsible=" + employeeResponsible + ", idUnit=" + idUnit + ", accountNumber=" + accountNumber + ", registrationDate=" + registrationDate + ", userRegistration=" + userRegistration + ", monthlyAsignation=" + monthlyAsignation + "]";
	}

}
