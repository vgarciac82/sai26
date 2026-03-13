package com.axtel.sai.sicove.repositories;


import java.math.BigDecimal;
import java.sql.Connection;

import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.entities.VehicleFuelRequestDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;


public interface FuelingRequestRepository {

	VehicleFuelRequest saveFuelRequest( Connection conn, VehicleFuelRequest vehicleFuelRequest ) throws SicoveException;

	VehicleFuelRequest readVehicleFuelRequest( Connection conn, int id ) throws SicoveException;

	VehicleFuelRequest updateFuelRequest( Connection conn, VehicleFuelRequest fuelRequestOrig ) throws SicoveException;

	void updateFuelRequestStatus( Connection conn, int fuelRequestId, int status ) throws SicoveException;

	void authRequestStatus( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException;

	void finishRequest( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException;

	void discardRequest( Connection conn, VehicleFuelRequest fuelRequest )throws SicoveException;

	BigDecimal rejectRequest( Connection conn, VehicleFuelRequest fuelRequest )throws SicoveException;

	VehicleFuelRequestDAO readFullVehicleFuelRequest( Connection conn, int fuelRequestId ) throws SicoveException;
}
