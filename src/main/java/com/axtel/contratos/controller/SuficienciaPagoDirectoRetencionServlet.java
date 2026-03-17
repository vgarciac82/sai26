package com.axtel.contratos.controller;

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
import com.axtel.contratos.core.SuficienciaPagoDirectoRetencion;
import com.axtel.contratos.services.SuficienciaPagoDirectoRetencionBusinessLogic;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet("/api/suficiencia/retencion")
public class SuficienciaPagoDirectoRetencionServlet extends HttpServlet {

    private static final long serialVersionUID = 1053143187815992583L;

    private static final Logger log = LoggerFactory.getLogger(SuficienciaPagoDirectoRetencionServlet.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private SuficienciaPagoDirectoRetencionBusinessLogic logic;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String jniName = "jdbc/gestion";
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        logic = new SuficienciaPagoDirectoRetencionBusinessLogic(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SuficienciaPagoDirectoRetencion bean = mapper.readValue(request.getReader(), SuficienciaPagoDirectoRetencion.class);
            SuficienciaPagoDirectoRetencion resultado = logic.insert(bean);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), resultado);
        } catch (Exception ex) {
            log.error("Error al insertar Retención", ex);
            enviarError(response, "Error al insertar Retención.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SuficienciaPagoDirectoRetencion bean = mapper.readValue(request.getReader(), SuficienciaPagoDirectoRetencion.class);
            SuficienciaPagoDirectoRetencion resultado = logic.update(bean);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), resultado);
        } catch (Exception ex) {
            log.error("Error al actualizar Retención", ex);
            enviarError(response, "Error al actualizar Retención.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int folio = Integer.parseInt(request.getParameter("folio"));
            int idRetencion = Integer.parseInt(request.getParameter("idTipoRetencion"));
            log.info("Object: {}", "Eliminando retencion " + idRetencion + " del folio " + folio);
            logic.deleteByFolio(folio, idRetencion);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"mensaje\":\"Eliminadas correctamente\"}");
        } catch (Exception ex) {
            log.error("Error al eliminar Retenciones", ex);
            enviarError(response, "Error al eliminar Retenciones.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int folio = Integer.parseInt(request.getParameter("folio"));
            List<SuficienciaPagoDirectoRetencion> lista = logic.findByFolio(folio);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), lista);
        } catch (Exception ex) {
            log.error("Error al buscar Retenciones", ex);
            enviarError(response, "Error al buscar Retenciones.");
        }
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }
}
