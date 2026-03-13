package com.axtel.sai.sicove.repositories;


import java.sql.Connection;
import java.util.List;

import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.exceptions.SicoveException;


public interface FuelAsignationVerificationRepository {

	FuelAsignationVerification createFuelingVerification( Connection conn, FuelAsignationVerification fuelAsignationVerification ) throws SicoveException;

	FuelAsignationVerification readFuelingVerification( Connection conn, int id ) throws SicoveException;

	FuelAsignationVerification updateFuelingVerification( Connection conn, FuelAsignationVerification fuelAsignationVerification ) throws SicoveException;

	int readFuelingVerificationIdByAsignation( Connection conn, int idAsignation ) throws SicoveException;

	WalletFuelRequestVerificationDetail saveVerificationDetail( Connection conn, WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException;

	WalletFuelRequestVerificationDetail readVerificationDetail( Connection conn, int intValue ) throws SicoveException;

	List<WalletFuelRequestVerificationDetail> readFuelingVerificationDetailList( Connection conn, int idVerification ) throws SicoveException;

	WalletFuelRequestVerificationDetail updateFuelingVerificationDetail( Connection conn, WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException;

	void deleteFuelingVerificationDetail( Connection conn, WalletFuelRequestVerificationDetail detail ) throws SicoveException;

	void updatePendingAmount( Connection conn, WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException;
}
