package com.axtel.sai.sicove.services;

import java.sql.Connection;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.sai.sicove.entities.Vehicle;
import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.entities.VehicleFuelRequestDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.syc.gestion.core.Caso;
import java.util.Base64;

public interface FuelingRequestService {

    VehicleFuelRequest saveFuelRequest(VehicleFuelRequest fuelRequest) throws ContratoException;

    Caso initProcess(Connection conn, VehicleFuelRequest fuelRequest, Vehicle vehicle) throws SicoveException;

    VehicleFuelRequest getFuelRequest(int fuelRequestId) throws SicoveException;

    VehicleFuelRequest updateFuelRequest(VehicleFuelRequest fuelRequest) throws SicoveException;

    void updateFuelRequestStatus(int fuelRequestId, int status) throws SicoveException;

    void authRequestStatus(VehicleFuelRequest fuelRequest) throws SicoveException;

    void finishRequest(VehicleFuelRequest fuelRequest) throws SicoveException;

    void discardRequest(VehicleFuelRequest fuelRequest) throws SicoveException;

    void rejectRequest(VehicleFuelRequest fuelRequest) throws SicoveException;

    VehicleFuelRequestDAO getFullFuelRequest(int fuelRequestId) throws SicoveException;
}
