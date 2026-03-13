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
import com.syc.reportes.ReporteViaticosBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteViaticosServlet", urlPatterns = { "/reportes/ReporteViaticos" })
public class ReporteViaticosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteViaticosServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    //@SuppressWarnings("unused")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String general = req.getParameter("TIPO_REPORTE");
        ReporteViaticosBusinessLogic rrs = new ReporteViaticosBusinessLogic(jniName);
        try {
            if ("REPORTEVIAT".equals(general)) {
                rrs.generaReporte(req, resp, plantillas);
            } else if ("REPORTEGASTOS".equals(general)) {
                rrs.generaReporteGastos(req, resp, plantillas);
            } else if ("REPORTEVIATICOSYGASTOS".equals(general)) {
                rrs.generaReporteViatocosyGastos(req, resp, plantillas);
            } else {
                rrs.generaReporteModuloViaticos(req, resp, plantillas);
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
                plantillas.put("REPORTEVIAT", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_viaticos.xls"));
                plantillas.put("REPORTEGASTOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_gastos.xls"));
                plantillas.put("REPORTEVIATICOSYGASTOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_viaticos_gastos.xls"));
                plantillas.put("REPORTEVIATICOSYGASTOSV2", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_viaticos_gastosV2.xls"));
                plantillas.put("REPORTEDEUDORES", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_DeudoresXViatico.xls"));
                plantillas.put("REPORTEDIASACUM", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_DiasAcumulados.xls"));
                plantillas.put("REPORTESALDOVENC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SaldoVencimiento.xls"));
                plantillas.put("REPORTEGASTORUBRO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_GastoXRubro.xls"));
                plantillas.put("REPORTEBOLETOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_BoletosPorUR.xls"));
                plantillas.put("REPORTEAGENDASPENDIENTES", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_AgendasPendientes.xls"));
            }
        }
    }
}
