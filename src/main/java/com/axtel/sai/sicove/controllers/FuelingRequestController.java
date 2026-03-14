package com.axtel.sai.sicove.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.sai.sicove.SICOVE;
import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.entities.VehicleFuelRequestDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingJustificationRepository;
import com.axtel.sai.sicove.repositories.FuelingNotificatorRepository;
import com.axtel.sai.sicove.repositories.FuelingRequestRepository;
import com.axtel.sai.sicove.repositories.impl.JDBCEmployeeRepository;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelingJustificationRepository;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelingNotificatorRepository;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelingRequestRepository;
import com.axtel.sai.sicove.repositories.impl.JDBCVehicleRepository;
import com.axtel.sai.sicove.services.FuelingNotificatorService;
import com.axtel.sai.sicove.services.FuelingRequestService;
import com.axtel.sai.sicove.services.impl.JBCFuelingRequestService;
import com.axtel.sai.sicove.services.impl.MailFuelingNotificatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "FuelingRequestController", urlPatterns = { "/SICOVE/FuelProvisioningWallet", "/SICOVE/FuelProvisioningWallet/nextStatus", "/SICOVE/FuelProvisioningWallet/authRequest", "/SICOVE/FuelProvisioningWallet/finishRequest", "/SICOVE/FuelProvisioningWallet/discardRequest", "/SICOVE/FuelProvisioningWallet/rejectRequest", "/SICOVE/FuelProvisioningWallet/validatingVerification" })
public class FuelingRequestController extends HttpServlet {

    public static final TimeZone GMT_MINUS_6 = TimeZone.getTimeZone("GMT-06:00");

    private static final long serialVersionUID = 1039657416626590670L;

    private static final Logger log = LoggerFactory.getLogger(FuelingRequestController.class);

    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private String jniName;

    private FuelingRequestService fuelingRequestService;

    private FuelingNotificatorService fuelingNotificatorService;

    private FuelingRequestRepository fuelingRequestRepository;

    private FuelingJustificationRepository fuelingJustificationRepository;

    private FuelingNotificatorRepository fuelingNotificatorRepository;

    private JDBCVehicleRepository vehicleRepository;

    private JDBCEmployeeRepository employeeRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        fuelingNotificatorRepository = new JDBCFuelingNotificatorRepository();
        fuelingJustificationRepository = new JDBCFuelingJustificationRepository();
        fuelingRequestRepository = new JDBCFuelingRequestRepository();
        vehicleRepository = new JDBCVehicleRepository();
        employeeRepository = new JDBCEmployeeRepository();
        fuelingRequestService = new JBCFuelingRequestService(jniName, fuelingRequestRepository, fuelingJustificationRepository, vehicleRepository, employeeRepository);
        fuelingNotificatorService = new MailFuelingNotificatorService(jniName, "REQFUELWALLET", fuelingNotificatorRepository);
        objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(GMT_MINUS_6);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VehicleFuelRequest fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
        try {
            fuelRequest = fuelingRequestService.saveFuelRequest(fuelRequest);
            sendFuelrequest(response, fuelRequest);
        } catch (ContratoException e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean fullResponse = false;
        try {
            int fuelRequestId = Integer.parseInt(request.getParameter(SICOVE.PARAM_REQUEST_FOLIO));
            fullResponse = "true".equals(StringUtils.trimToEmpty(request.getParameter(SICOVE.PARAM_FULL_REQUEST)));
            if (fullResponse) {
                VehicleFuelRequestDAO fullFuelRequest = fuelingRequestService.getFullFuelRequest(fuelRequestId);
                sendFullFuelrequest(response, fullFuelRequest);
            } else {
                VehicleFuelRequest fuelRequest = fuelingRequestService.getFuelRequest(fuelRequestId);
                sendFuelrequest(response, fuelRequest);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
        VehicleFuelRequest fuelRequest;
        log.debug("Object: {}", "Action: " + action);
        try {
            if ("validatingVerification".equals(action)) {
                fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
                fuelingRequestService.updateFuelRequestStatus(fuelRequest.getFuelingRequestId(), fuelRequest.getIdStatus());
                if (fuelRequest.getIdStatus() == SICOVE.CAPTURE_VERIFICATION)
                    fuelingNotificatorService.sendCaptureVerifNotification(fuelRequest);
                else if (fuelRequest.getIdStatus() == SICOVE.VERIFIED_FUELING_REQUEST)
                    fuelingNotificatorService.sendAprovedVerifNotification(fuelRequest);
                else
                    fuelingNotificatorService.sendValidateVerifNotification(fuelRequest);
                fuelRequest = fuelingRequestService.getFuelRequest(fuelRequest.getFuelingRequestId());
            } else if ("finishRequest".equals(action)) {
                fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
                VehicleFuelRequest savedRequest = fuelingRequestService.getFuelRequest(fuelRequest.getFuelingRequestId());
                if (savedRequest.getIdStatus() == SICOVE.FUELING_REQUEST_AUTHORIZED)
                    fuelRequest = savedRequest;
                else {
                    fuelingRequestService.finishRequest(fuelRequest);
                    fuelingNotificatorService.sendAuthNotification(fuelRequest);
                    fuelRequest = fuelingRequestService.getFuelRequest(fuelRequest.getFuelingRequestId());
                }
            } else if ("authRequest".equals(action)) {
                fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
                fuelingRequestService.authRequestStatus(fuelRequest);
                fuelRequest = fuelingRequestService.getFuelRequest(fuelRequest.getFuelingRequestId());
            } else if ("nextStatus".equals(action)) {
                int fuelRequestId = Integer.parseInt(request.getParameter(SICOVE.PARAM_REQUEST_FOLIO));
                int status = Integer.parseInt(request.getParameter(SICOVE.PARAM_REQUEST_STATUS));
                fuelingRequestService.updateFuelRequestStatus(fuelRequestId, status);
                fuelRequest = fuelingRequestService.getFuelRequest(fuelRequestId);
                fuelingNotificatorService.sendPendingAuthNotification(fuelRequest);
            } else {
                fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
                fuelRequest = fuelingRequestService.updateFuelRequest(fuelRequest);
            }
            sendFuelrequest(response, fuelRequest);
        } catch (SicoveException e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(response, e);
        }
    }

    private void sendFullFuelrequest(HttpServletResponse response, VehicleFuelRequestDAO fuelRequest) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(fuelRequest);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void sendFuelrequest(HttpServletResponse response, VehicleFuelRequest fuelRequest) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(fuelRequest);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
        VehicleFuelRequest fuelRequest;
        log.debug("Object: {}", "Action: " + action);
        try {
            Map<String, String> result = new HashMap<>();
            result.put("success", "true");
            if ("rejectRequest".equals(action)) {
                fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
                fuelingRequestService.rejectRequest(fuelRequest);
                fuelingNotificatorService.notifyRejection(fuelRequest);
                result.put("message", "Tramite descartado exitosmente");
            } else if ("discardRequest".equals(action)) {
                fuelRequest = objectMapper.readValue(request.getInputStream(), VehicleFuelRequest.class);
                fuelingRequestService.discardRequest(fuelRequest);
                result.put("message", "Tramite rechazado");
            }
            Util.sendJSON(response, result);
        } catch (SicoveException e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(response, e);
        }
    }
}
