package com.axtel.sai.sicove.services;

import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface FuelAccountNotificatorService {

    String generateNotificationBody(FuelProvisioningRequest request, RequestAuthChain requestAuthChain) throws SicoveException;

    boolean sendPendingAuthNotification(FuelProvisioningRequest request) throws SicoveException;

    boolean sendAuthNotification(FuelProvisioningRequest fuelRequest) throws SicoveException;

    String generateAuthNotificationBody(FuelProvisioningRequest request, RequestAuthChain requestAuthChain) throws SicoveException;

    boolean notifyRejection(FuelProvisioningRequest fuelRequest) throws SicoveException;

    String generateRejectNotificationBody(FuelProvisioningRequest request, RequestAuthChain requestAuthChain) throws SicoveException;
}
