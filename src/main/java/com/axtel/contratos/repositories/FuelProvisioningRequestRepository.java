package com.axtel.contratos.repositories;

import java.sql.Connection;
import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.contratos.exception.ContratoException;
import java.util.Base64;

public interface FuelProvisioningRequestRepository {

    FuelProvisioningRequest insert(Connection conn, FuelProvisioningRequest fuelProvisioningRequest) throws ContratoException;

    FuelProvisioningRequest readFuelProvisioning(Connection conn, int id) throws ContratoException;

    FuelProvisioningRequest update(Connection conn, FuelProvisioningRequest fuelProvisioningRequest) throws ContratoException;

    boolean delete(Connection conn, int id) throws ContratoException;
}
