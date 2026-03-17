package com.axtel.cfdi.stamp.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.cfdi.stamp.core.InvoiceRespond;
import com.axtel.cfdi.stamp.service.StampCFDIService;
import com.axtel.web.utils.ControllerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet("/CFDIManagment/stamp")
public class CFDIStampController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(CFDIStampController.class);

    private final Gson gson = new GsonBuilder().create();

    private StampCFDIService stampService;

    private String jniName;

    private String REPORT_DIR;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String json = ControllerUtils.readJsonFromRequest(request);
            @SuppressWarnings("unchecked")
            List<String> cfdiIds = (List<String>) gson.fromJson(json, List.class);
            log.info("Object: {}", "Timbrando CFDIs con IDs: " + cfdiIds);
            List<InvoiceRespond> result = stampService.stamp(cfdiIds);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            log.error("Error al timbrar los CFDIs: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error al timbrar los CFDIs. Por favor intente nuevamente.\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String json = ControllerUtils.readJsonFromRequest(request);
            List<Integer> cfdiIds = gson.fromJson(json, List.class);
            // Log para los IDs recibidos
            log.info("Object: {}", "Cancelando CFDIs con IDs: " + cfdiIds);
            // Aquí agregar la lógica de cancelación de CFDIs
            // Por ejemplo: cfdiService.cancelCFDIs(cfdiIds);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"CFDIs cancelados correctamente.\"}");
        } catch (Exception e) {
            log.error("Error al cancelar los CFDIs: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error al cancelar los CFDIs. Por favor intente nuevamente.\"}");
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        REPORT_DIR = getServletContext().getRealPath("Reportes" + File.separator);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula, usando default \"" + jniName + "\"");
            } else {
                log.info("Object: {}", "dataSourceRefName=" + jniName);
            }
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida, usando default \"" + jniName + "\"");
        }
        stampService = new StampCFDIService(jniName, new File(REPORT_DIR));
    }
}
