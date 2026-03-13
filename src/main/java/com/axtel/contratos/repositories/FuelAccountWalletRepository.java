package com.axtel.contratos.repositories;


import java.sql.Connection;

import com.axtel.contratos.entities.FuelAccountWallet;
import com.axtel.contratos.exception.ContratoException;


public interface FuelAccountWalletRepository {

	FuelAccountWallet insert( Connection conn, FuelAccountWallet fuelAccountWallet ) throws ContratoException;

	FuelAccountWallet readAccountWallet( Connection conn, int id ) throws ContratoException;

	FuelAccountWallet update( Connection conn, FuelAccountWallet fuelAccountWallet ) throws ContratoException;
}
