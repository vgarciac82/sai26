package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteAcreedoresDeudoresBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteAcreedoresDeudoresServlet", urlPatterns = { "/reportes/ReporteAcreedoresDeudores" })
public class ReporteAcreedoresDeudoresServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteAcreedoresDeudoresServlet.class);

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
        String tipoRep = req.getParameter("TIPO");
        String tipoReporte = req.getParameter("TIPO_REPORTE");
        if ("Devengado".equals(tipoRep)) {
            tipoReporte = tipoRep;
        }
        String tipoFormato = req.getParameter("analitica");
        String conFechaComision = req.getParameter("fechaComision");
        String conUnidadEjecutora = req.getParameter("conUnidad");
        String ruta = getServletContext().getRealPath("Reportes");
        String fechaIni = req.getParameter("fecha_inicio");
        String fechaFin = req.getParameter("fecha_fin");
        String centroContable = req.getParameter("cCentroContable");
        String tipo = "";
        String limp = req.getParameter("limpiar");
        String msg = "";
        int tipoCedula = 0;
        if ("Acreedores".equals(tipoReporte)) {
            tipoCedula = 1;
        } else if ("Deudores".equals(tipoReporte)) {
            tipoCedula = 2;
        }
        if (StringUtils.isEmpty(tipoReporte))
            throw new ServletException("No se recibio el parametro TIPO_REPORTE");
        ReporteAcreedoresDeudoresBusinessLogic rrs = new ReporteAcreedoresDeudoresBusinessLogic(jniName);
        try {
            if ("limpiar".equals(limp)) {
                msg = rrs.limpiarCedula(req, resp, tipoCedula, fechaFin);
            } else {
                if ("analitica".equals(tipoFormato)) {
                    if ("Acreedores".equals(tipoReporte)) {
                        tipo = "AnaliticaAcreedores.jasper";
                    } else if ("Deudores".equals(tipoReporte)) {
                        tipo = "AnaliticaDeudores.jasper";
                    }
                    String reportPath = getServletContext().getRealPath("Reportes" + File.separator + tipo);
                    rrs.generaAnalitica(req, resp, reportPath, ruta, fechaIni, fechaFin, centroContable);
                } else {
                    if ("Acreedores".equals(tipoReporte))
                        rrs.generaCedulasA(req, resp, plantillas, conUnidadEjecutora);
                    else if ("Deudores".equals(tipoReporte))
                        rrs.generaCedulasD(req, resp, plantillas, conFechaComision);
                    else if ("Devengado".equals(tipoReporte))
                        rrs.generaReporteDev(req, resp, plantillas);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.toString();
        }
        session.setAttribute("msg", msg);
        resp.sendRedirect("../Generador/ReporteAcreedoresDeudores.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("ACREEDORES", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaAcreedores.xls"));
                plantillas.put("DEUDORES", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaDeudores.xls"));
                plantillas.put("DEUDORES_COMISION", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaDeudoresComision.xls"));
                plantillas.put("DEVENGADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaDeudoresDevengado.xls"));
                plantillas.put("ACREEDORES_UNIDAD", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaAcreedoresUnidad.xls"));
            }
        }
    }
}
