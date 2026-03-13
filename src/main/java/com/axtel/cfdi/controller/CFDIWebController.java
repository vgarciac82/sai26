package com.axtel.cfdi.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.LogManager;
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

@WebServlet("/CFDIManagment")
public class CFDIWebController extends HttpServlet {

    private static final Logger log = LogManager.getLogger(CFDIWebController.class);

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
                log.warn("CFDI no encontrado para idInvoice: " + idInvoice + ". Enviando status 404.");
                // 404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(cfdi);
            response.getWriter().write(jsonResponse);
            log.info("CFDI obtenido y enviado correctamente para idInvoice: " + idInvoice);
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
            log.debug("JSON recibido: " + json);
            CFDI cfdi = gson.fromJson(json, CFDI.class);
            log.info("Objeto CFDI deserializado: " + cfdi);
            log.info("Insertando encabezado del CFDI.");
            CFDIEncabezado cfdiHeader = cfdiService.insertCFDIHeader(cfdi.getEncabezado());
            cfdi.setEncabezado(cfdiHeader);
            log.info("Encabezado del CFDI insertado correctamente con ID: " + cfdiHeader.getCfdiId());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(cfdi);
            log.debug("Respuesta JSON generada: " + jsonResponse);
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
            log.debug("JSON recibido: " + json);
            CFDI cfdi = gson.fromJson(json, CFDI.class);
            log.info("Objeto CFDI deserializado: " + cfdi);
            log.info("Actualizando CFDI en base de datos.");
            cfdi = cfdiService.updateCFDI(cfdi);
            log.info("CFDI actualizado correctamente con ID: " + cfdi.getEncabezado().getCfdiId());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(cfdi);
            log.debug("Respuesta JSON generada: " + jsonResponse);
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
                log.info("Environment Entry \"dataSourceRefName\" nula, usando default \"" + jniName + "\"");
            } else {
                log.info("dataSourceRefName=" + jniName);
            }
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida, usando default \"" + jniName + "\"");
        }
        GsonBuilder gbuilder = new GsonBuilder();
        gbuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        gson = gbuilder.create();
        cfdiService = new CFDIService(jniName, new File(getServletContext().getRealPath("Reportes")));
    }
}
