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
import com.syc.reportes.ReporteRetencionesBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteRetencionesServlet", urlPatterns = { "/reportes/ReporteRetenciones" })
public class ReporteRetencionesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteRetencionesServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    private static Map<String, String> plantillasResumen = null;

    private static Map<String, String> plantillasAcumulada = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String tipoReporte = req.getParameter("TIPO_REPORTE");
        String tipo_Ajena = req.getParameter("tipo_ajena");
        String conEP = req.getParameter("EP");
        if (StringUtils.isEmpty(tipoReporte))
            throw new ServletException("No se recibio el parametro TIPO_REPORTE");
        ReporteRetencionesBusinessLogic rrs = new ReporteRetencionesBusinessLogic(jniName);
        try {
            if ("1".equals(tipoReporte) && "10".equals(tipo_Ajena))
                rrs.generaReporteRetencionesAcc(req, resp, plantillasAcumulada);
            else if ("1".equals(tipoReporte) && "S".equals(conEP))
                rrs.generaReporteRetenciones(req, resp, plantillas, conEP);
            else if ("1".equals(tipoReporte)) {
                rrs.generaReporteRetenciones(req, resp, plantillas, conEP);
            } else if ("2".equals(tipoReporte)) {
                if ("10".equals(tipo_Ajena)) {
                    System.out.println("Para esta opcion no hay resumen, favor de seleccionar la opcion TESOFE1 o VARIOS (Gob. Estado)");
                } else
                    rrs.generaResumenRetenciones(req, resp, plantillasResumen);
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
                plantillas.put("TESOFE1", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE1.xls"));
                plantillas.put("TESOFE46", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE46.xls"));
                plantillas.put("TESOFE7", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE7.xls"));
                plantillas.put("TESOFE8", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE8.xls"));
                plantillas.put("LAUDOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_LAUDOS.xls"));
                plantillas.put("TESOFE1CC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE1_Coordinacion.xls"));
                plantillas.put("TESOFE46CC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE46_Coordinacion.xls"));
                plantillas.put("TESOFE7CC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE7_Coordinacion.xls"));
                //plantillas.put("TESOFE8CC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE8_Coordinacion.xls"));
                plantillas.put("TESOFE9CC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE9_Coordinacion.xls"));
            }
            if (plantillasResumen == null) {
                plantillasResumen = new HashMap<String, String>();
                plantillasResumen.put("TESOFE1R", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE1_Resumen.xls"));
                plantillasResumen.put("TESOFE6R", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE6_Resumen.xls"));
                plantillasResumen.put("TESOFE78R", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE78_Resumen.xls"));
                plantillasResumen.put("LAUDOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_LAUDOS_Resumen.xls"));
                plantillasResumen.put("TESOFE4R", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_TESOFE4_Resumen.xls"));
            }
            if (plantillasAcumulada == null) {
                plantillasAcumulada = new HashMap<String, String>();
                plantillasAcumulada.put("Acumulada", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteRetenciones_Acumulada.xls"));
            }
        }
    }
}
