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
import com.axtel.sisecop.dto.ProjectBudgetItemDTO;
import com.axtel.sisecop.entities.ProjectBudgetItem;
import com.axtel.sisecop.services.ProjectBudgetService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet("/SISECOP/budget")
public class ProjectBudgetController extends HttpServlet {

    private static final long serialVersionUID = 7214789876912436509L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(ProjectBudgetController.class);

    private String jniName;

    private ProjectBudgetService budgetService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        jniName = WebUtils.findJNIName(config);
        budgetService = new ProjectBudgetService(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProjectBudgetItemDTO budgetItem = readBudgetItem(request);
        log.trace("Object: {}", "JSON recibido correctamente y mapeado a objeto BudgetItem. " + budgetItem);
        try {
            log.debug("Object: " + String.valueOf("Saving budgetItem: " + budgetItem));
            ProjectBudgetItem projectBudgetItem = budgetService.createProjectBudget(budgetItem);
            log.info("Object: {}", "Territory saved: " + projectBudgetItem);
            Util.sendJSONResponse(response, projectBudgetItem);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idBudgetItem = Integer.parseInt(request.getParameter("id"));
            log.info("Object: {}", "Trying to delete " + idBudgetItem + " budget item");
            Map<String, String> result = new HashMap<>();
            budgetService.deleteProjectBudget(idBudgetItem);
            log.info("Object: {}", "Budget Item " + idBudgetItem + " was deleted");
            result.put("deleted", "true");
            result.put("success", "true");
            result.put("rowsAfected", "1");
            Util.sendJSONResponse(response, result);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    private ProjectBudgetItemDTO readBudgetItem(HttpServletRequest request) throws IOException {
        final StringBuilder jsonRequest = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonRequest.append(line);
            }
        }
        String jsonString = jsonRequest.toString();
        log.trace("Object: {}", "Recibed: " + jsonString);
        ProjectBudgetItemDTO budgetItemDTO = objectMapper.readValue(jsonString, ProjectBudgetItemDTO.class);
        return budgetItemDTO;
    }
}
