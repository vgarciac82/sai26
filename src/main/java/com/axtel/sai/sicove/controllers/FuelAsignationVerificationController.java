package com.axtel.sai.sicove.controllers;

import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelAsignationVerificationRepository;
import com.axtel.sai.sicove.services.impl.JDBCFuelAsignationVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "FuelVerificationController", urlPatterns = { "/SICOVE/FuelVerification/getByAsignationId", "/SICOVE/FuelVerification", "/SICOVE/FuelVerification/getVerificationList" })
public class FuelAsignationVerificationController extends HttpServlet {

    private static final long serialVersionUID = 1037498415129331546L;

    private String jniName;

    private JDBCFuelAsignationVerificationService fuelAsignationVerificationService;

    private ObjectMapper mapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(FuelAsignationVerificationController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        log.info("Object: {}", "Action: " + action);
        try {
            FuelAsignationVerification fuelAsignationVerification = null;
            if ("getVerificationList".equals(action)) {
                int idVerification = Integer.parseInt(req.getParameter("idVerification"));
                log.info("Object: {}", "Looking for verification list with assignation folio " + idVerification);
                List<WalletFuelRequestVerificationDetail> verificationDetailList = fuelAsignationVerificationService.readFuelingVerificationDetailList(idVerification);
                Util.sendJSON(resp, verificationDetailList);
                return;
            } else if ("getByAsignationId".equals(action)) {
                int id = Integer.parseInt(req.getParameter("fuelingRequestId"));
                log.info("Object: {}", "Looking for verification with assignation folio " + id);
                int idVerification = fuelAsignationVerificationService.readFuelingVerificationIdByAsignation(id);
                if (idVerification > 0)
                    fuelAsignationVerification = fuelAsignationVerificationService.readFuelingVerification(idVerification);
                else
                    fuelAsignationVerification = new FuelAsignationVerification();
            } else {
                int id = Integer.parseInt(req.getParameter("idVerification"));
                log.info("Object: {}", "Looking for verification with id " + id);
                fuelAsignationVerification = fuelAsignationVerificationService.readFuelingVerification(id);
                log.info("Object: {}", "Found: " + fuelAsignationVerification);
            }
            Util.sendJSON(resp, fuelAsignationVerification);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FuelAsignationVerification fuelAsignationVerification = mapper.readValue(req.getInputStream(), FuelAsignationVerification.class);
        log.info("Object: {}", "Saving: " + fuelAsignationVerification);
        try {
            fuelAsignationVerification = fuelAsignationVerificationService.createFuelingVerification(fuelAsignationVerification);
            log.info("Object: {}", String.valueOf(fuelAsignationVerification));
            Util.sendJSON(resp, fuelAsignationVerification);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FuelAsignationVerification fuelAsignationVerification = mapper.readValue(req.getInputStream(), FuelAsignationVerification.class);
        log.info("Object: {}", "Updating: " + fuelAsignationVerification);
        try {
            FuelAsignationVerification originalAsignation = fuelAsignationVerificationService.readFuelingVerification(fuelAsignationVerification.getIdVerification());
            originalAsignation.setCurrentVehicleKilometers(fuelAsignationVerification.getCurrentVehicleKilometers());
            originalAsignation.setCurrentWalletBalance(fuelAsignationVerification.getCurrentWalletBalance());
            originalAsignation.setValidationAmount(fuelAsignationVerification.getValidationAmount());
            originalAsignation.setInitialVehicleKilometers(fuelAsignationVerification.getInitialVehicleKilometers());
            fuelAsignationVerification = fuelAsignationVerificationService.updateFuelingVerification(originalAsignation);
            log.info("Object: {}", String.valueOf(fuelAsignationVerification));
            Util.sendJSON(resp, fuelAsignationVerification);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }

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
        fuelAsignationVerificationService = new JDBCFuelAsignationVerificationService(jniName, new JDBCFuelAsignationVerificationRepository());
    }
}
