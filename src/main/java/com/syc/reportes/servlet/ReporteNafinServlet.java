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
import com.syc.gestion.util.Util;
import com.syc.reportes.ReporteNafinBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteNafinServlet", urlPatterns = { "/reportes/ReporteNafin", "/reportes/FinalizarProveedor" })
public class ReporteNafinServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteNafinServlet.class);

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
        ReporteNafinBusinessLogic rrs = new ReporteNafinBusinessLogic(jniName);
        try {
            rrs.generaReporte(req, resp, plantillas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        ReporteNafinBusinessLogic rrs = new ReporteNafinBusinessLogic(jniName);
        try {
            String tipo = req.getParameter("reporte");
            if ("Proveedor".equals(tipo)) {
                rrs.finalizaProveedores(req, resp);
            } else {
                rrs.finalizaPagos(req, resp);
            }
            Util.sendJSONResponse(resp, "success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                Util.sendHTMLErrorMsg(resp, e);
            } catch (Exception e2) {
                log.warn("No se pudo notificar la causa de la excepcion: " + e2, e2);
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("PROVEEDORNAFIN", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ProveedorNafin.xlsx"));
                plantillas.put("PAGOSNAFIN", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_PagosNafin.xlsx"));
            }
        }
    }
}
