package com.axtel.sisecop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.axtel.sisecop.clients.TopAuthorizationClient;
import com.axtel.sisecop.dto.TopAuthorizationDTO;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.services.ProyectoServicioService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/SISECOP/proyectos")
public class ProyectoServicioController extends HttpServlet {

    private static final long serialVersionUID = 338085628802187809L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(ProyectoServicioController.class);

    private String jniName;

    private ProyectoServicioService proyectoService = null;

    private ConfiguraAplicativoBusinessLogic systemConfig = null;

    private EjercicioFiscalBusinessLogic fiscalYearService = null;

    private String centralUnit;

    private String fiscalYear;

    private String urlEstructuraOrg;

    private TopAuthorizationClient autClient;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        jniName = WebUtils.findJNIName(config);
        proyectoService = new ProyectoServicioService(jniName);
        systemConfig = new ConfiguraAplicativoBusinessLogic(jniName);
        fiscalYearService = new EjercicioFiscalBusinessLogic(jniName);
        centralUnit = systemConfig.getSystemSetting("UNIDAD_CONTABLE");
        urlEstructuraOrg = systemConfig.getSystemSetting("UNIT_ADMIN_SERVICE_URL");
        fiscalYear = fiscalYearService.getEjercicioFiscalActivo().getaEjercicioFiscal();
        autClient = new TopAuthorizationClient(urlEstructuraOrg);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProyectoServicio proyectoServicio = readProjectRequest(request);
        log.trace("JSON recibido correctamente y mapeado a objeto ProyectoServicio. " + proyectoServicio);
        try {
            HttpSession session = request.getSession(false);
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            log.info("Lookin for user unit hierarchy");
            List<TopAuthorizationDTO> units = autClient.getTopAuthorizations(u.getU_UR());
            if (units != null && units.size() >= 1) {
                proyectoServicio.setManagmentUnit(units.get(units.size() - 1).getAdscription());
                proyectoServicio.setResponsibleUnit(units.get(0).getAdscription());
            }
            proyectoServicio.setServicioFolioPre(centralUnit);
            proyectoServicio.setServicioFolioAnio(Integer.valueOf(fiscalYear));
            log.debug("Saving proyecto: " + proyectoServicio);
            proyectoServicio = proyectoService.create(proyectoServicio);
            log.info("Project saved: " + proyectoServicio);
            Util.sendJSONResponse(response, proyectoServicio);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProyectoServicio proyectoServicio = readProjectRequest(request);
        log.trace("JSON recibido correctamente y mapeado a objeto ProyectoServicio. " + proyectoServicio);
        try {
            log.debug("Updating project: " + proyectoServicio);
            proyectoServicio = proyectoService.update(proyectoServicio);
            log.info("Project updated: " + proyectoServicio);
            Util.sendJSONResponse(response, proyectoServicio);
        } catch (Exception e) {
            log.error("Error saving porject: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.info("Looking for project with id: " + req.getParameter("id"));
            int id = Integer.parseInt(req.getParameter("id"));
            ProyectoServicio project = proyectoService.findById(id);
            log.debug("Project founded: " + project);
            Util.sendJSONResponse(response, project);
        } catch (Exception e) {
            log.error("Error getting project: " + e.toString(), e);
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
        log.trace("Received: " + jsonString);
        ProyectoServicio proyectoServicio = objectMapper.readValue(jsonString, ProyectoServicio.class);
        return proyectoServicio;
    }
}
