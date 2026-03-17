package com.axtel.sisecop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.services.ProyectoServicioService;
import com.axtel.web.utils.WebUtils;
import tools.jackson.databind.ObjectMapper;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet("/SISECOP/proyectosWorkflow")
public class ProyectoWorkflowController extends HttpServlet {

    private static final long serialVersionUID = 338085628802187809L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(ProyectoWorkflowController.class);

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
        ProyectoServicio proyectoServicio = readProjectRequest(request);
        log.trace("Object: {}", "JSON recibido correctamente y mapeado a objeto ProyectoServicio. " + proyectoServicio);
        try {
            ProyectoServicio proyectoOriginal = proyectoService.findById(proyectoServicio.getServicioId());
            proyectoOriginal.setEstatus(proyectoServicio.getEstatus());
            if (10 == proyectoServicio.getEstatus().getEstatusId())
                proyectoOriginal.setObservaciones("");
            else
                proyectoOriginal.setObservaciones(proyectoServicio.getObservaciones());
            HttpSession session = request.getSession(false);
            Usuario user = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            log.debug("Object: " + String.valueOf("Sending project to step : " + proyectoOriginal.getEstatus().getEstatusId()));
            proyectoOriginal = proyectoService.nextStep(proyectoOriginal, user);
            log.info("Object: {}", "Project avanced to next step: " + proyectoOriginal);
            Util.sendJSONResponse(response, proyectoOriginal);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    private ProyectoServicio readProjectRequest(HttpServletRequest request) throws IOException {
        final StringBuilder jsonRequest = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonRequest.append(line);
            }
        }
        String jsonString = jsonRequest.toString();
        log.trace("Object: {}", "Received: " + jsonString);
        ProyectoServicio proyectoServicio = objectMapper.readValue(jsonString, ProyectoServicio.class);
        return proyectoServicio;
    }
}
