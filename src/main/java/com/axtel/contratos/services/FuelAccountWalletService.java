package com.axtel.contratos.services;


import com.axtel.contratos.entities.FuelAccountWallet;
import com.axtel.contratos.exception.ContratoException;


public interface FuelAccountWalletService {

	FuelAccountWallet insert( FuelAccountWallet fuelAccountWallet ) throws ContratoException;

	FuelAccountWallet readAccountWallet( int id ) throws ContratoException;

	FuelAccountWallet update( FuelAccountWallet fuelAccountWallet )throws ContratoException;
}
