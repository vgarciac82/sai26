package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReportesINAIBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReportesINAIServlet", urlPatterns = { "/reportes/ReportesINAI" })
public class ReportesINAIServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReportesINAIServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String nombreReporte = req.getParameter("reporteNombre");
        int tipoReporte = 0;
        ReportesINAIBusinessLogic rpt = new ReportesINAIBusinessLogic(jniName);
        ServletContext context = getServletConfig().getServletContext();
        try {
            if ("ProveedoresContratistas".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("INAI", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_INAI_Padron_Proveedores_Contratistas.xlsx"));
                plantillas.put("fInicio", req.getParameter("fInicio"));
                plantillas.put("fFin", req.getParameter("fFin"));
                tipoReporte = 1;
            } else if ("AdjudicacionDirecta".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("INAI", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_AdjudicacionDirecta_INAI.xlsx"));
                plantillas.put("fInicio", req.getParameter("fInicio"));
                plantillas.put("fFin", req.getParameter("fFin"));
                tipoReporte = 2;
            } else if ("LicitacionesInvitaciones".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("INAI", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_LicitacionesInvitaciones_INAI.xlsx"));
                plantillas.put("fInicio", req.getParameter("fInicio"));
                plantillas.put("fFin", req.getParameter("fFin"));
                tipoReporte = 3;
            } else if ("reporteV2".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "Formato_V2_MATRIZ_DE_CONTRATOS_2019.xls"));
                plantillas.put("nameDB", req.getParameter("catEjercicios"));
                plantillas.put("cEjercicioAnt", req.getParameter("cEjercicioAnt"));
                plantillas.put("cMes", req.getParameter("catMeses"));
                tipoReporte = 4;
            } else if ("reporteAnexo3".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "ReportePresidenciaAnexo3.xls"));
                tipoReporte = 5;
            } else if ("reporteFormato7y8".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "formato7.xlsx"));
                plantillas.put("FechaInicial", req.getParameter("fInicio"));
                plantillas.put("FechaFinal", req.getParameter("fFin"));
                tipoReporte = 6;
            } else if ("reporteFormato14".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "formato14.xlsx"));
                plantillas.put("FechaInicial", req.getParameter("fInicio"));
                plantillas.put("FechaFinal", req.getParameter("fFin"));
                tipoReporte = 8;
            } else if ("reporteV2Backup".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "Formato_V2_MATRIZ_DE_CONTRATOS_2019.xls"));
                plantillas.put("cMes", req.getParameter("catMesesTodo"));
                tipoReporte = 7;
            } else if ("reporteFormato7y8PLU".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "formato7.xlsx"));
                plantillas.put("FechaInicial", req.getParameter("fInicio"));
                plantillas.put("FechaFinal", req.getParameter("fFin"));
                tipoReporte = 9;
            } else if ("reporteFormato14PLU".equalsIgnoreCase(nombreReporte)) {
                plantillas.put("nameReport", getServletContext().getRealPath("Reportes" + File.separator + "formato14.xlsx"));
                plantillas.put("FechaInicial", req.getParameter("fInicio"));
                plantillas.put("FechaFinal", req.getParameter("fFin"));
                tipoReporte = 10;
            } else {
                //plantillas.put("INAI", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato12.xls"));
                log.warn("Reporte desconocido, favor de seleccionar un reporte.", new Throwable());
                return;
            }
            rpt.generaReportesINAI(req, resp, plantillas, tipoReporte, context);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        } finally {
            context = null;
            context = null;
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
            }
        }
    }
}
