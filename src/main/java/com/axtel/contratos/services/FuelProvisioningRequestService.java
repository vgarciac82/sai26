package com.axtel.contratos.services;

import java.sql.Connection;
import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.syc.gestion.core.Caso;
import java.util.Base64;

public interface FuelProvisioningRequestService {

    FuelProvisioningRequest insert(FuelProvisioningRequest fuelProvisioningRequest) throws ContratoException;

    FuelProvisioningRequest readFuelProvisioning(int id) throws ContratoException;

    FuelProvisioningRequest update(FuelProvisioningRequest fuelProvisioningRequest) throws ContratoException;

    boolean deleteFuelProvisioning(int id) throws ContratoException;

    Caso initProcess(Connection conn, FuelProvisioningRequest fuelProvisioningRequest) throws SicoveException;
}
