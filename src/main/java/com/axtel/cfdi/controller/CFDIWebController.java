package com.axtel.cfdi.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.axtel.cfdi.CFDI;
import com.axtel.cfdi.CFDIEncabezado;
import com.axtel.cfdi.service.CFDIService;
import com.axtel.web.utils.ControllerUtils;
import com.axtel.web.utils.DateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet("/CFDIManagment")
public class CFDIWebController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CFDIWebController.class);

    private static final long serialVersionUID = 1L;

    private CFDIService cfdiService;

    private Gson gson;

    private String jniName;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("Iniciando procesamiento de doGet para obtener CFDI.");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                log.warn("Sesión no encontrada. Enviando status 401.");
                // 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null) {
                log.warn("Usuario no encontrado en la sesión. Enviando status 401.");
                // 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String idInvoiceParam = request.getParameter("idInvoice");
            if (idInvoiceParam == null) {
                log.warn("El parámetro idInvoice es nulo. Enviando status 400.");
                // 400
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            int idInvoice;
            try {
                idInvoice = Integer.parseInt(idInvoiceParam);
            } catch (NumberFormatException e) {
                log.error("Error al convertir idInvoice a entero. Valor recibido: " + idInvoiceParam, e);
                // 400
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            CFDI cfdi = cfdiService.getCFDI(idInvoice);
            if (cfdi == null) {
                log.warn("Object: {}", "CFDI no encontrado para idInvoice: " + idInvoice + ". Enviando status 404.");
                // 404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(cfdi);
            response.getWriter().write(jsonResponse);
            log.info("Object: {}", "CFDI obtenido y enviado correctamente para idInvoice: " + idInvoice);
        } catch (Exception e) {
            log.error("Error inesperado en doGet", e);
            // 500
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("Inicio de procesamiento de CFDI en método POST.");
        try {
            String json = ControllerUtils.readJsonFromRequest(request);
            log.debug("Object: " + String.valueOf("JSON recibido: " + json));
            CFDI cfdi = gson.fromJson(json, CFDI.class);
            log.info("Object: {}", "Objeto CFDI deserializado: " + cfdi);
            log.info("Insertando encabezado del CFDI.");
            CFDIEncabezado cfdiHeader = cfdiService.insertCFDIHeader(cfdi.getEncabezado());
            cfdi.setEncabezado(cfdiHeader);
            log.info("Object: {}", "Encabezado del CFDI insertado correctamente con ID: " + cfdiHeader.getCfdiId());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(cfdi);
            log.debug("Object: " + String.valueOf("Respuesta JSON generada: " + jsonResponse));
            response.getWriter().write(jsonResponse);
            log.info("CFDI procesado y respuesta enviada correctamente.");
        } catch (Exception e) {
            log.error("Error al procesar el CFDI en método POST: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Error al procesar el CFDI. Por favor verifique los datos.\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("Inicio de actualización de CFDI en método PUT.");
        try {
            String json = ControllerUtils.readJsonFromRequest(request);
            log.debug("Object: " + String.valueOf("JSON recibido: " + json));
            CFDI cfdi = gson.fromJson(json, CFDI.class);
            log.info("Object: {}", "Objeto CFDI deserializado: " + cfdi);
            log.info("Actualizando CFDI en base de datos.");
            cfdi = cfdiService.updateCFDI(cfdi);
            log.info("Object: {}", "CFDI actualizado correctamente con ID: " + cfdi.getEncabezado().getCfdiId());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(cfdi);
            log.debug("Object: " + String.valueOf("Respuesta JSON generada: " + jsonResponse));
            response.getWriter().write(jsonResponse);
            log.info("CFDI actualizado y respuesta enviada correctamente.");
        } catch (Exception e) {
            log.error("Error al procesar el CFDI en método PUT: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Error al procesar el CFDI. Por favor verifique los datos.\"}");
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
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula, usando default \"" + jniName + "\"");
            } else {
                log.info("Object: {}", "dataSourceRefName=" + jniName);
            }
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida, usando default \"" + jniName + "\"");
        }
        GsonBuilder gbuilder = new GsonBuilder();
        gbuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        gson = gbuilder.create();
        cfdiService = new CFDIService(jniName, new File(getServletContext().getRealPath("Reportes")));
    }
}
