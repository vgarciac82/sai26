package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReportePlurianualesBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@SuppressWarnings("unused")
@WebServlet(name = "ReportePlurianualesServlet", urlPatterns = { "/reportes/ReportePlurianuales" })
public class ReportePlurianualesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReportePlurianualesServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String ruta = getServletContext().getRealPath("Reportes");
        String tipoReporte = req.getParameter("TIPO_REPORTE");
        ReportePlurianualesBusinessLogic rrs = new ReportePlurianualesBusinessLogic(jniName);
        try {
            if ("1".equals(tipoReporte)) {
                rrs.generaReporte(req, resp, plantillas);
            } else if ("2".equals(tipoReporte)) {
                rrs.generaReporte(req, resp, plantillas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("ContratosPlurianuales", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Contratos_Plurianuales.xls"));
                plantillas.put("CedulaPlurianuales", getServletContext().getRealPath("Cedula" + File.separator + "Plantilla_Cedula_Plurianuales.xls"));
            }
        }
    }
}
