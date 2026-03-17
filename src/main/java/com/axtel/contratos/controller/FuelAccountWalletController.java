package com.axtel.contratos.controller;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.contratos.entities.FuelAccountWallet;
import com.axtel.contratos.repositories.JDBCFuelAccountWalletRepository;
import com.axtel.contratos.services.FuelAccountWalletService;
import com.axtel.contratos.services.implementation.JDBCFuelAccountWalletService;
import tools.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "FuelAccountWalletController", urlPatterns = { "/SICOVE/FuelAccountWallet" })
public class FuelAccountWalletController extends HttpServlet {

    private static final long serialVersionUID = 5595453004875399225L;

    private FuelAccountWalletService fuelAccountWalletService;

    private String jniName = "";

    private static final Logger log = LoggerFactory.getLogger(FuelAccountWalletController.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FuelAccountWallet fuelAccountWallet = mapper.readValue(req.getInputStream(), FuelAccountWallet.class);
        log.info("Object: {}", String.valueOf(fuelAccountWallet));
        try {
            fuelAccountWallet = fuelAccountWalletService.insert(fuelAccountWallet);
            log.info("Object: {}", String.valueOf(fuelAccountWallet));
            Util.sendJSON(resp, fuelAccountWallet);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FuelAccountWallet fuelAccountWallet = mapper.readValue(req.getInputStream(), FuelAccountWallet.class);
        log.info("Object: {}", "Updating " + fuelAccountWallet);
        try {
            fuelAccountWallet = fuelAccountWalletService.update(fuelAccountWallet);
            log.info("Object: {}", "After update: " + fuelAccountWallet);
            Util.sendJSON(resp, fuelAccountWallet);
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
        fuelAccountWalletService = new JDBCFuelAccountWalletService(new JDBCFuelAccountWalletRepository(), jniName);
    }
}
