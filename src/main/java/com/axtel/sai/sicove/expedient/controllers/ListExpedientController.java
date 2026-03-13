package com.axtel.sai.sicove.expedient.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.axtel.sai.sicove.SICOVE;
import com.axtel.sai.sicove.expedient.entities.DocumentFortimax;
import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.axtel.sai.sicove.expedient.repositories.impl.JDBCExpedientRepostory;
import com.axtel.sai.sicove.expedient.services.ExpedientService;
import com.axtel.sai.sicove.expedient.services.impl.JDBCExpedientService;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@MultipartConfig
@WebServlet(name = "ListExpedientController", urlPatterns = { "/SICOVE/Expedient", "/SICOVE/CapturedExpedient" })
public class ListExpedientController extends HttpServlet {

    private static final long serialVersionUID = -3249489437722787496L;

    private static final Logger log = LogManager.getLogger(ListExpedientController.class);

    private static ExpedientService expedientService;

    private static ExpedientRepository expedientRepository;

    private String jniName;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idProcess = Integer.parseInt(request.getParameter(SICOVE.PARAM_ID_PROCESS));
            String folderExlude = request.getParameter(SICOVE.EXCLUDE);
            String action = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
            List<DocumentFortimax> documents = new ArrayList<>();
            if (action.equals("CapturedExpedient")) {
                documents = expedientService.getExpedientCapturedDocuments(idProcess, folderExlude);
            } else {
                documents = expedientService.getExpedientDocuments(idProcess, folderExlude);
            }
            Util.sendJSON(response, documents);
        } catch (Exception e) {
            log.error(e, e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            boolean cleanOnly = "true".equals(request.getParameter("cleanOnly"));
            Fortimax document = new Fortimax(request.getParameter("nodeId"));
            expedientService.deleteDocument(document, cleanOnly);
            Map<String, String> map = new HashMap<>();
            map.put("success", "true");
            Util.sendJSON(response, map);
        } catch (Exception e) {
            log.error(e, e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Part filePart = request.getPart("file");
            String documentSelect = request.getParameter("documentSelect");
            String fileName = filePart.getSubmittedFileName();
            String idProcess = request.getParameter("idProcess");
            File file = File.createTempFile("upload_", "_" + fileName);
            FileUtils.copyInputStreamToFile(filePart.getInputStream(), file);
            expedientService.saveDocument(file, documentSelect, Integer.parseInt(idProcess));
            Map<String, String> result = new HashMap<>();
            result.put("success", "true");
            result.put("message", "El archivo se adjuntó correctamente.");
            Util.sendJSON(response, result);
        } catch (Exception ex) {
            log.error(ex, ex);
            Util.sendJSONResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.toString());
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
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        expedientRepository = new JDBCExpedientRepostory();
        expedientService = new JDBCExpedientService(jniName, expedientRepository);
    }
}
