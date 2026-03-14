package com.axtel.sai.sicove.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.sai.sicove.entities.FuelAccountWalletRefund;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelAccountWalletRefundRepository;
import com.axtel.sai.sicove.services.FuelAccountWalletRefundService;
import com.axtel.sai.sicove.services.impl.JDBCFuelAccountWalletRefundService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet("/SICOVE/walletRefund")
public class FuelAccountWalletRefunController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(FuelAccountWalletRefunController.class);

    private FuelAccountWalletRefundService fuelAccountWalletRefundService;

    private String jniName;

    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int idRefund = Integer.parseInt(req.getParameter("ID_REFUND"));
            fuelAccountWalletRefundService.delete(idRefund);
            Map<String, Integer> result = new HashMap<>();
            result.put("DELETED", 1);
            Util.sendJSON(resp, result);
        } catch (SicoveException e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objectMapper.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        log.trace("TIME ZONE SETTING CHANGED TO UTC");
        FuelAccountWalletRefund fuelAccountWalletRefun = objectMapper.readValue(req.getInputStream(), FuelAccountWalletRefund.class);
        try {
            fuelAccountWalletRefun = fuelAccountWalletRefundService.create(fuelAccountWalletRefun);
            Util.sendJSON(resp, fuelAccountWalletRefun);
        } catch (SicoveException e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        jniName = Util.readJNIName(config);
        fuelAccountWalletRefundService = new JDBCFuelAccountWalletRefundService(jniName, new JDBCFuelAccountWalletRefundRepository());
        objectMapper = new ObjectMapper();
    }
}
