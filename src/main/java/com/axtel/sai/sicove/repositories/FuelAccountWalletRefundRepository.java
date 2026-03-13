package com.axtel.sai.sicove.repositories;


import java.sql.Connection;

import com.axtel.sai.sicove.entities.FuelAccountWalletRefund;
import com.axtel.sai.sicove.exceptions.SicoveException;


public interface FuelAccountWalletRefundRepository {

	FuelAccountWalletRefund create( Connection conn, FuelAccountWalletRefund fuelAccountWalletRefund ) throws SicoveException;

	FuelAccountWalletRefund read( Connection conn, Integer idRefund ) throws SicoveException;

	FuelAccountWalletRefund update( Connection conn, Integer idRefund, FuelAccountWalletRefund fuelAccountWalletRefund ) throws SicoveException;

	void delete( Connection conn, Integer idRefund ) throws SicoveException;
}
