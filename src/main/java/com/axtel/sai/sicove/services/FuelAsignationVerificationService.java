package com.axtel.sai.sicove.services;


import java.io.File;
import java.util.List;

import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.exceptions.SicoveException;


public interface FuelAsignationVerificationService {

	FuelAsignationVerification createFuelingVerification( FuelAsignationVerification fuelAsignationVerification ) throws SicoveException;

	FuelAsignationVerification readFuelingVerification( int id ) throws SicoveException;

	FuelAsignationVerification updateFuelingVerification( FuelAsignationVerification fuelAsignationVerification ) throws SicoveException;

	int readFuelingVerificationIdByAsignation( int idAsignation ) throws SicoveException;

	WalletFuelRequestVerificationDetail addVerificationDetail( int idProcess, String folderName, File file, WalletFuelRequestVerificationDetail verificationDetail, String userName ) throws SicoveException;

	List<WalletFuelRequestVerificationDetail> readFuelingVerificationDetailList( int idVerification ) throws SicoveException;

	WalletFuelRequestVerificationDetail readFuelingVerificationDetail( int idDetail ) throws SicoveException;

	WalletFuelRequestVerificationDetail updateFuelingVerificationDetail( WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException;

	void deleteFuelingVerificationDetail( WalletFuelRequestVerificationDetail detail )throws SicoveException;

}
