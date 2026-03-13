package com.syc.gestion.reportes.servlet;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.servlet.ActualizaAplicacionServlet;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "reportesServlet", urlPatterns = { "/reportes" })
public class reportesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ActualizaAplicacionServlet.class);

    private String jniName = null;

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
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("index.jsp");
            // <script language="javascript">self.top.location.href =
            // "../index.jsp";</script>
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        String strCmd = req.getParameter(PRM_CMD);
        if (strCmd == null) {
            log.error("Llamada inválida sin comando");
            throw new ServletException("Llamada inválida sin comando");
        }
        int command = Integer.parseInt(strCmd);
        switch(command) {
            //Se borra un monton de código original de reportes/isste
            case RPT_MOVIMIENTOS_RECT:
                {
                    String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "ReporteMovimientosRectificacion.jasper");
                    reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
                    objReporte.reporteMovimientosRectificacion(req, resp, reportPath, u.getNombre());
                    break;
                }
            case RPT_MOVIMIENTOS_REINT:
                {
                    String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "ReporteMovimientosReintegro.jasper");
                    reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
                    objReporte.reporteMovimientosReintegro(req, resp, reportPath, u.getNombre());
                    break;
                }
            case RPT_MOVIMIENTOS_RECTMIL:
                {
                    String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "ReporteMovimientosRectificacionMil.jasper");
                    reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
                    objReporte.reporteMovimientosRectificacion(req, resp, reportPath, u.getNombre());
                    break;
                }
            case RPT_MOVIMIENTOS_REINMIL:
                {
                    String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "ReporteMovimientosReintegroMil.jasper");
                    reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
                    objReporte.reporteMovimientosReintegro(req, resp, reportPath, u.getNombre());
                    break;
                }
        }
    }
}
