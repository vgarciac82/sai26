package com.axtel.sisecop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.axtel.sisecop.dto.TerritoryDTO;
import com.axtel.sisecop.entities.ProyectoServicioTerritorio;
import com.axtel.sisecop.services.ProyectoServicioService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/SISECOP/territory")
public class ProjectTerritoryController extends HttpServlet {

    private static final long serialVersionUID = 5399757736298649510L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(ProjectTerritoryController.class);

    private String jniName;

    private ProyectoServicioService proyectoService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        jniName = WebUtils.findJNIName(config);
        proyectoService = new ProyectoServicioService(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TerritoryDTO territory = readProjectTerritory(request);
        log.trace("JSON recibido correctamente y mapeado a objeto TerritoryDTO. " + territory);
        try {
            log.debug("Saving territory: " + territory);
            ProyectoServicioTerritorio projectTerritory = proyectoService.createTerritory(territory);
            log.info("Territory saved: " + projectTerritory);
            Util.sendJSONResponse(response, projectTerritory);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idProject = Integer.parseInt(request.getParameter("servicioId"));
            log.info("Looking for all territoris  for project: " + idProject);
            List<ProyectoServicioTerritorio> territories = proyectoService.readTerritoriosByServicioId(idProject);
            Util.sendJSONResponse(response, territories);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idTerritory = Integer.parseInt(request.getParameter("territorioId"));
            log.info("Trying to delete " + idTerritory + " territory");
            Map<String, String> result = new HashMap<>();
            proyectoService.deleteTerritory(idTerritory);
            log.info("Territory " + idTerritory + " deleted");
            result.put("deleted", "true");
            result.put("success", "true");
            result.put("rowsAfected", "1");
            Util.sendJSONResponse(response, result);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    private TerritoryDTO readProjectTerritory(HttpServletRequest request) throws IOException {
        final StringBuilder jsonRequest = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonRequest.append(line);
            }
        }
        String jsonString = jsonRequest.toString();
        log.trace("Recibed: " + jsonString);
        TerritoryDTO territoryDTO = objectMapper.readValue(jsonString, TerritoryDTO.class);
        return territoryDTO;
    }
}
