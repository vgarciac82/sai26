package com.axtel.sisecop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.sisecop.dto.ProyectoServicioActividadDTO;
import com.axtel.sisecop.entities.ProyectoServicioActividad;
import com.axtel.sisecop.services.ProjectActivityService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet("/SISECOP/activity")
public class ProjectActivityController extends HttpServlet {

    private static final long serialVersionUID = 7214789876912436509L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(ProjectActivityController.class);

    private String jniName;

    private ProjectActivityService activityService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        jniName = WebUtils.findJNIName(config);
        activityService = new ProjectActivityService(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProyectoServicioActividadDTO activity = readActivity(request);
        log.trace("Object: {}", "JSON recibido correctamente y mapeado a objeto ProyectoServicioActividadDTO. " + activity);
        try {
            log.debug("Object: " + String.valueOf("Saving activity: " + activity));
            ProyectoServicioActividad activitySaved = activityService.createActivity(activity);
            log.info("Object: {}", "Activity saved: " + activitySaved);
            Util.sendJSONResponse(response, activitySaved);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idActivity = Integer.parseInt(request.getParameter("id"));
            log.info("Object: {}", "Trying to delete " + idActivity + " budget item");
            Map<String, String> result = new HashMap<>();
            activityService.deleteActivity(idActivity);
            log.info("Object: {}", "Activity " + idActivity + " was deleted");
            result.put("deleted", "true");
            result.put("success", "true");
            result.put("rowsAfected", "1");
            Util.sendJSONResponse(response, result);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    private ProyectoServicioActividadDTO readActivity(HttpServletRequest request) throws IOException {
        final StringBuilder jsonRequest = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonRequest.append(line);
            }
        }
        String jsonString = jsonRequest.toString();
        log.trace("Object: {}", "Recibed: " + jsonString);
        ProyectoServicioActividadDTO activity = objectMapper.readValue(jsonString, ProyectoServicioActividadDTO.class);
        return activity;
    }
}
