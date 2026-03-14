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
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReportePolizasBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ReportePolizasServlet", urlPatterns = { "/reportes/ReportePolizas" })
public class ReportePolizasServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReportePolizasServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    //private static Boolean 				tipo_plantilla 		= false;
    //	@SuppressWarnings("unused")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String tipo = req.getParameter("Detalle");
        String general = req.getParameter("TIPO_REPORTE");
        String a = req.getParameter("aEjercicioFiscal");
        ReportePolizasBusinessLogic rrs = new ReportePolizasBusinessLogic(jniName);
        try {
            if (general == null) {
                rrs.generaReportePolizas(req, resp, plantillas);
            } else if ("0".equals(general)) {
                if ("false".equals(tipo)) {
                    rrs.generaReportePolizas(req, resp, plantillas);
                } else if ("true".equals(tipo)) {
                    rrs.generaReportePolizasDet(req, resp, plantillas);
                }
            } else if ("COMSOC".equals(general)) {
                rrs.generaReporteCOMSOC(req, resp, plantillas);
            } else if ("CONCILIARADPAGADO".equals(general) || "CONCILIARADINGRESO".equals(general)) {
                rrs.generaConciliacionRad(req, resp, plantillas);
            } else if ("CONCILIAINGRESOGASTO".equals(general)) {
                rrs.generaConciliacionIngresoGasto(req, resp, plantillas);
            } else if ("Concilia11225".equals(general)) {
                rrs.generaReporteConciliacion11225(req, resp, plantillas);
            } else if ("GastoDevengado".equals(general)) {
                rrs.generaReporteGastoDevengado(req, resp, plantillas);
            } else if ("Patrimonio".equals(general)) {
                rrs.generaCedulaPatrimonio(req, resp, plantillas);
            } else if ("ReporteIP".equals(general)) {
                rrs.generaReporteIP(req, resp, plantillas);
            } else if ("CAPACITACION".equals(general)) {
                rrs.generaReporteCapacitacion(req, resp, plantillas);
            } else if ("ConciliaCompromiso".equals(general) || "ConciliaDevengado".equals(general) || "CONCILIAMOD".equals(general) || "ConciliaEjercido".equals(general) || "ConciliaMomentos".equals(general) || "ConciliaIngreso".equals(general) || "FFM".equals(general) || "ConciliaDevengadoIng".equals(general) || "ConciliaOrgModDispEjeIng".equals(general) || "ConciliaEgresos".equals(general) || "ConciliaRadicadoPagado".equals(general)) {
                rrs.generaReporteConciliaMomentos(req, resp, plantillas);
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
                plantillas.put("REPPOLIZAS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReportePolizas.xlsx"));
                plantillas.put("REPPOLIZASDET", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReportePolizasDet.xlsx"));
                plantillas.put("COMSOC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_COMSOC.xls"));
                plantillas.put("CONCILIAMOD", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaMod.xls"));
                plantillas.put("CONCILIARADPAGADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaRadPagado.xls"));
                plantillas.put("CONCILIARADINGRESO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaRadIngreso.xls"));
                plantillas.put("CONCILIAINGRESOGASTO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaIngresoGasto.xls"));
                plantillas.put("PCONCILIACION11225", getServletContext().getRealPath("Reportes" + File.separator + "FormatoConciliacion11225.xls"));
                plantillas.put("ConciliaGastoDevengado", getServletContext().getRealPath("Reportes" + File.separator + "FormatoConciliacion_GtovsDev.xls"));
                plantillas.put("CONCILIAEJERCIDO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaEjercido.xls"));
                plantillas.put("CONCILIADEVENGADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaDevengado.xls"));
                plantillas.put("CONCILIACOMPROMISO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaCompromiso.xls"));
                plantillas.put("CONCILIAMOMENTOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaMomentos.xls"));
                plantillas.put("PAGOSFFM", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Pagos_FFM.xls"));
                plantillas.put("PATRIMONIO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaPatrimonio.xls"));
                plantillas.put("ConciliaIngreso", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Ingreso_Conta_pptal.xls"));
                plantillas.put("CONCILIADEVENGADOING", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaDevengadoIng.xls"));
                plantillas.put("CONCILIAORGDISPMODEJEING", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaOrgModDispEjeIng.xls"));
                plantillas.put("CONCILIAEGRESOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Egreso_Conta_pptal.xls"));
                plantillas.put("ReporteIP", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteIP.xls"));
                plantillas.put("ConciliaRadicadoPagado", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliaRadicadoPagado.xls"));
                plantillas.put("CAPACITACION", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Capacitacion33401.xls"));
            }
        }
    }
}
