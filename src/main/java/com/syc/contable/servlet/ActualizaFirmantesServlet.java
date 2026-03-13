package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.RelacionGastosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ActualizaFirmantes", urlPatterns = { "/ActualizaFirmantesMasivo" })
public class ActualizaFirmantesServlet extends HttpServlet implements GestionInterface {

    private static String jniName = "jdbc/gestion";

    private static final Logger log = LoggerFactory.getLogger(ActualizaFirmantesServlet.class);

    private static Map<String, String> plantillas = null;

    /**
     */
    private static final long serialVersionUID = 3891354314758159744L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = "";
        try {
            String reportPath1 = getServletContext().getRealPath("Reportes" + File.separator);
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new Exception("La sesion ha terminado. Por favor ingrese nuevamente al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new Exception("La sesion ha terminado. Por favor ingrese nuevamente al sistema.");
            String fileName = "SolicitudesPago_" + System.currentTimeMillis() + ".zip";
            int nFolioCargaMasiva = Integer.parseInt(req.getParameter("nFolioCargaMasiva"));
            resp.setContentType("application/zip");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + fileName + "\";");
            ServletOutputStream out = resp.getOutputStream();
            RelacionGastosBussinessLogic rgbl = new RelacionGastosBussinessLogic(jniName, null);
            rgbl.exportaSolicitudesMasiva(reportPath1, fileName, nFolioCargaMasiva, out);
        } catch (Exception e) {
            msg = e.toString();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = null;
        session = req.getSession(false);
        Object msgResult = null;
        boolean fromUpdate = true;
        if (session == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        String envioSICOP = req.getParameter("envioSICOP");
        String rechazoSICOP = req.getParameter("rechazoSICOP");
        try {
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            int nFolioCargaMasiva = Integer.parseInt(req.getParameter("nFolioCargaMasiva"));
            String pendientesSICOP = req.getParameter("pendientesSICOP");
            String tipoDoc = req.getParameter("tipoDocumento");
            String nFolio = req.getParameter("nFolio");
            RelacionGastosBussinessLogic rgbl = new RelacionGastosBussinessLogic(jniName, null);
            if ("1".equals(pendientesSICOP)) {
                rgbl.exportaSolicitudesMasivaPendientesSICOP(resp, nFolioCargaMasiva, plantillas);
            } else if ("1".equals(envioSICOP)) {
                msgResult = rgbl.enviarSICOPSolicitudesMasiva(nFolioCargaMasiva);
            } else if ("1".equals(rechazoSICOP)) {
                msgResult = rgbl.rechazoSICOPSolicitudesMasiva(nFolioCargaMasiva, tipoDoc, nFolio, u);
            } else {
                msgResult = rgbl.actualizaFirmantes(req, resp, session);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        if ("1".equals(envioSICOP) || "1".equals(rechazoSICOP))
            session.setAttribute("MSG_RESP_SICOP", msgResult);
        else
            session.setAttribute("MSG_RESP", msgResult);
        session.setAttribute("ORIGEN_UPDATE", String.valueOf(fromUpdate));
        resp.sendRedirect("Generador/cargaRelacionGastos.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("REPALIMENTACION", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteAlimentacionBrigadistas.xls"));
            }
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
