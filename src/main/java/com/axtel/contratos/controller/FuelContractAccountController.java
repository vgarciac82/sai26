package com.axtel.contratos.controller;

import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import com.axtel.contratos.entities.FuelContractAccount;
import com.axtel.contratos.services.FuelContractService;
import com.axtel.contratos.services.implementation.FuelContractServiceImplementation;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "FuelContractAccountController", urlPatterns = { "/SICOVE/FuelContract/addAccount", "/SICOVE/FuelContract/getAccounts" })
public class FuelContractAccountController extends HttpServlet implements GenericFuelContract {

    private static final long serialVersionUID = 2355019797040910400L;

    private static final Logger log = LoggerFactory.getLogger(FuelContractAccountController.class);

    private String jniName;

    private FuelContractService fuelContractService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        if (GET_ACCOUNTS.equals(action)) {
            List<FuelContractAccount> accounts = null;
            try {
                int unitId = Integer.parseInt(req.getParameter("unitId"));
                accounts = fuelContractService.getAccounts(unitId);
                Util.sendJSON(resp, accounts);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Util.sendJSONError(resp, e);
            }
        }
        log.info("Object: {}", "Accion a ejecutar: " + action);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FuelContractAccount fuelContractAccount = mapper.readValue(req.getInputStream(), FuelContractAccount.class);
        log.info("Object: {}", String.valueOf(fuelContractAccount));
        try {
            fuelContractAccount = fuelContractService.createContractAccount(fuelContractAccount);
            log.info("Object: {}", String.valueOf(fuelContractAccount));
            Util.sendJSON(resp, fuelContractAccount);
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
        fuelContractService = new FuelContractServiceImplementation(jniName);
    }
}
