package com.axtel.contratos.controller;

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
import com.axtel.contratos.core.SuficienciaPagoDirectoEncabezado;
import com.axtel.contratos.services.SuficienciaPagoDirectoEncabezadoBusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet("/api/suficiencia")
public class SuficienciaPagoDirectoEncabezadoServlet extends HttpServlet {

    private static final long serialVersionUID = -7018169420037839716L;

    private static final Logger log = LoggerFactory.getLogger(SuficienciaPagoDirectoEncabezadoServlet.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private SuficienciaPagoDirectoEncabezadoBusinessLogic logic;

    private CasoBusinessLogic casoBusinessLogic;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String jniName;
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
        logic = new SuficienciaPagoDirectoEncabezadoBusinessLogic(jniName);
        casoBusinessLogic = new CasoBusinessLogic(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Caso c = null;
        Usuario u = null;
        try {
            HttpSession session = request.getSession(false);
            if (session == null)
                throw new RuntimeException("Su sesion termino. Ingrese nuevamente.");
            c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            if (c == null)
                throw new RuntimeException("Su sesion termino. Ingrese nuevamente.");
            u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null)
                throw new RuntimeException("Su sesion termino. Ingrese nuevamente.");
            SuficienciaPagoDirectoEncabezado bean = mapper.readValue(request.getReader(), SuficienciaPagoDirectoEncabezado.class);
            SuficienciaPagoDirectoEncabezado resultado = logic.insert(bean, c, u);
            session.setAttribute(GestionInterface.ATT_CASE, c);
            ITree tree = casoBusinessLogic.getArbolCaso(c);
            session.setAttribute(GestionInterface.ATT_TREE, tree);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), resultado);
        } catch (Exception ex) {
            log.error("Error al insertar encabezado", ex);
            enviarError(response, "Error al insertar encabezado.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SuficienciaPagoDirectoEncabezado bean = mapper.readValue(request.getReader(), SuficienciaPagoDirectoEncabezado.class);
            SuficienciaPagoDirectoEncabezado resultado = logic.update(bean);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), resultado);
        } catch (Exception ex) {
            log.error("Error al actualizar encabezado", ex);
            enviarError(response, "Error al actualizar encabezado.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int folio = Integer.parseInt(request.getParameter("folio"));
            logic.deleteByFolio(folio);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"mensaje\":\"Eliminado correctamente\"}".toPath());
        } catch (Exception ex) {
            log.error("Error al eliminar encabezado", ex);
            enviarError(response, "Error al eliminar encabezado.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int folio = Integer.parseInt(request.getParameter("folio"));
            SuficienciaPagoDirectoEncabezado bean = logic.findById(folio);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (bean == null)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(response.getWriter(), bean);
        } catch (Exception ex) {
            log.error("Error al buscar encabezado", ex);
            enviarError(response, "Error al buscar encabezado.");
        }
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}".toPath());
    }
}
