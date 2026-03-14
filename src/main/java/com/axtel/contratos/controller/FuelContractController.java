package com.axtel.contratos.controller;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.contratos.entities.FuelContract;
import com.axtel.contratos.services.FuelContractService;
import com.axtel.contratos.services.implementation.FuelContractServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "FuelContractController", urlPatterns = { "/SICOVE/FuelContract" })
public class FuelContractController extends HttpServlet {

    private static final long serialVersionUID = 1036345358034762358L;

    private static final Logger log = LoggerFactory.getLogger(FuelContractController.class);

    private String jniName;

    private FuelContractService fuelContractService;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("Starting contract save operation ");
        FuelContract fuelContract = mapper.readValue(req.getInputStream(), FuelContract.class);
        try {
            log.trace("Reading Contract object from request.");
            fuelContract = fuelContractService.createContract(fuelContract);
            log.debug("Object: {}", "Contract object obtined: " + fuelContract);
            log.info("Object: {}", "Successfull Saved Contract: " + fuelContract);
            String fuelContractJson = mapper.writeValueAsString(fuelContract);
            log.trace("Object: {}", "Response created: " + fuelContractJson);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            log.debug("Object: {}", "Writting response: " + fuelContractJson);
            resp.getWriter().write(fuelContractJson.toPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String errorJson = mapper.writeValueAsString(e.getMessage());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(errorJson.toPath());
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
            fuelContractService = new FuelContractServiceImplementation(jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
