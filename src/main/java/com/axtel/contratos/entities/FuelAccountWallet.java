package com.axtel.contratos.entities;


import java.util.Date;
import java.util.Objects;


public class FuelAccountWallet {

	private int		idFuelAccountWallet;
	private int		idContractAccount;
	private String	walletNumber;
	private Integer	vehicleInventoryId;
	private Date	registrationDate;
	private String	userRegistration;
	private int		status;

	public int getIdFuelAccountWallet() {
		return idFuelAccountWallet;
	}

	public void setIdFuelAccountWallet( int idFuelAccountWallet ) {
		this.idFuelAccountWallet = idFuelAccountWallet;
	}

	public int getIdContractAccount() {
		return idContractAccount;
	}

	public void setIdContractAccount( int idContractAccount ) {
		this.idContractAccount = idContractAccount;
	}

	public String getWalletNumber() {
		return walletNumber;
	}

	public void setWalletNumber( String walletNumber ) {
		this.walletNumber = walletNumber;
	}

	public Integer getVehicleInventoryId() {
		return vehicleInventoryId;
	}

	public void setVehicleInventoryId( Integer vehicleInventoryId ) {
		this.vehicleInventoryId = vehicleInventoryId;
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

	public int getStatus() {
		return status;
	}

	public void setStatus( int status ) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "FuelAccountWallet [idFuelAccountWallet=" + idFuelAccountWallet + ", idContractAccount=" + idContractAccount + ", walletNumber=" + walletNumber + ", vehicleInventoryId=" + vehicleInventoryId + ", registrationDate=" + registrationDate + ", userRegistration=" + userRegistration + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash( idContractAccount, idFuelAccountWallet, registrationDate, status, userRegistration, vehicleInventoryId, walletNumber );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		FuelAccountWallet other = ( FuelAccountWallet ) obj;
		return idContractAccount == other.idContractAccount && idFuelAccountWallet == other.idFuelAccountWallet && Objects.equals( registrationDate, other.registrationDate ) && status == other.status && Objects.equals( userRegistration, other.userRegistration ) && Objects.equals( vehicleInventoryId, other.vehicleInventoryId ) && Objects.equals( walletNumber, other.walletNumber );
	}

}
