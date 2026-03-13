package com.axtel.contratos.services;


import java.sql.SQLException;
import java.util.List;

import com.axtel.contratos.entities.FuelContract;
import com.axtel.contratos.entities.FuelContractAccount;


public interface FuelContractService {

	FuelContract createContract( FuelContract fuelContract ) throws SQLException;

	FuelContract readContract( int idContract ) throws SQLException;

	FuelContract updateContract( int idContract, FuelContract fuelContract ) throws SQLException;

	boolean deleteContract( FuelContract fuelContract ) throws SQLException;

	FuelContractAccount createContractAccount( FuelContractAccount fuelContractAccount ) throws SQLException;

	List<FuelContractAccount> getAccounts( int unitId ) throws Exception;
}
