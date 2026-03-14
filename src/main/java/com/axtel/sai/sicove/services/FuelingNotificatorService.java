package com.axtel.sai.sicove.services;

import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface FuelingNotificatorService {

    String generateNotificationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException;

    boolean sendPendingAuthNotification(VehicleFuelRequest request) throws SicoveException;

    boolean sendAuthNotification(VehicleFuelRequest fuelRequest) throws SicoveException;

    String generateAuthNotificationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException;

    boolean notifyRejection(VehicleFuelRequest fuelRequest) throws SicoveException;

    String generateRejectNotificationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException;

    boolean sendValidateVerifNotification(VehicleFuelRequest fuelRequest);

    String generateValidationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException;

    boolean sendCaptureVerifNotification(VehicleFuelRequest fuelRequest);

    boolean sendAprovedVerifNotification(VehicleFuelRequest fuelRequest);
}
