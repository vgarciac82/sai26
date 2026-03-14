package com.axtel.cfdi.controller;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.axtel.cfdi.CFDIDetalle;
import com.axtel.cfdi.service.CFDIService;
import com.axtel.web.utils.ControllerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/CFDIManagment/detalle")
public class CFDIDetalleController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CFDIDetalleController.class);

    private static final long serialVersionUID = -175572219319401243L;

    private CFDIService cfdiService;

    private Gson gson;

    private String jniName;

    private String REPORT_DIR;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("Inicio de procesamiento del método doPost para guardar detalle CFDI.");
        try {
            String json = ControllerUtils.readJsonFromRequest(request);
            log.debug("Object: {}", "JSON recibido en la solicitud: " + json);
            log.trace("Convirtiendo JSON a objeto CFDIDetalle.");
            CFDIDetalle detalle = gson.fromJson(json, CFDIDetalle.class);
            log.trace("Insertando el detalle CFDI en la base de datos.");
            detalle = cfdiService.insertCFDIDetail(detalle);
            log.trace("Preparando respuesta con el detalle CFDI guardado.");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(detalle);
            log.debug("Object: {}", "JSON de respuesta generado: " + jsonResponse);
            response.getWriter().write(jsonResponse);
            log.info("Object: {}", "Detalle CFDI guardado y respuesta enviada correctamente." + detalle);
        } catch (Exception e) {
            log.error("Error al guardar el detalle del CFDI: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = "{\"error\": \"Error al guardar el detalle del CFDI. Intente nuevamente.\"}";
            log.debug("Error occurred", "Enviando respuesta de error: " + errorResponse);
            response.getWriter().write(errorResponse);
        }
        log.info("Fin del método doPost para guardar detalle CFDI.");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("Inicio de procesamiento del método doDelete para eliminar un detalle CFDI.");
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                log.warn("Sesión nula. Enviando estatus 401.");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null) {
                log.warn("Usuario no encontrado en la sesión. Enviando estatus 401.");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String idDetalleParam = req.getParameter("idDetalle");
            if (idDetalleParam == null || idDetalleParam.isEmpty()) {
                log.warn("Parámetro 'idDetalle' vacío o nulo. Enviando estatus 400.");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            int idDetalle;
            try {
                idDetalle = Integer.parseInt(idDetalleParam);
            } catch (NumberFormatException e) {
                log.warn("Error occurred", "Error al convertir 'idDetalle' a entero. Valor recibido: " + idDetalleParam);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            log.trace("Object: {}", "Eliminando detalle CFDI con ID: " + idDetalle);
            cfdiService.deleteDetailRow(idDetalle);
            log.info("Object: {}", "Detalle CFDI con ID " + idDetalle + " eliminado correctamente.");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("Error al eliminar el detalle CFDI: " + e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        log.trace("Fin del procesamiento del método doDelete.");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            REPORT_DIR = getServletContext().getRealPath("Reportes");
            log.info("Object: {}", "REPORT DIR : " + REPORT_DIR);
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
        gson = new GsonBuilder().create();
        cfdiService = new CFDIService(jniName, new File(REPORT_DIR));
    }
}
