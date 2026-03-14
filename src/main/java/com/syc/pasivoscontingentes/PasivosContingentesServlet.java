package com.syc.pasivoscontingentes;

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
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "PasivosContingentesServlet", urlPatterns = { "/pasivoscontingentes/PasivosContingentes", "/reportes/PasivosContingentes" })
public class PasivosContingentesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(PasivosContingentesServlet.class);

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
        String tipoReporte = req.getParameter("TIPO_REPORTE");
        String unidad = req.getParameter("cUnidadResponsable");
        String tipoCedula = req.getParameter("TIPO_CEDULA");
        PasivosContingentesBusinessLogic rrs = new PasivosContingentesBusinessLogic(jniName);
        try {
            if ("1".equals(tipoReporte)) {
                //cedula pasatrimoniale
                rrs.generaCedula(req, resp, plantillas);
            } else if ("3".equals(tipoReporte)) {
                //cedula pasatrimoniale
                rrs.generaCedulaLaboral(req, resp, plantillas);
            } else if ("5".equals(tipoReporte)) {
                //cedula antigüedad
                rrs.generaCedualAntiguedad(req, resp, plantillas, unidad);
            } else if ("6".equals(tipoReporte)) {
                //cedula trimestral - 2018 y anteriores  mensual - 2019 y subsecuebntes
                rrs.generaCedualTrimestral(req, resp, plantillas, unidad);
            } else if ("7".equals(tipoReporte)) {
                //cedula antigüedad por expediente
                if ("A".equals(tipoCedula)) {
                    //Analitica
                    rrs.generaCedualAntiguedadExpediente(req, resp, plantillas, unidad);
                } else if ("C".equals(tipoCedula)) {
                    //Consolidada
                    rrs.generaCedualAntiguedadExpedienteConsolidada(req, resp, plantillas, unidad);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msg = "";
        String tipoReporte = req.getParameter("TIPO_REPORTE");
        String RFC = req.getParameter("cRFC");
        String PC = req.getParameter("cSubcuenta");
        String reportPath = req.getParameter("reportPath");
        try {
            if (session == null) {
                session = req.getSession(true);
                throw new ServletException("Su session a caducado");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new ServletException("Su session a caducado");
            PasivosContingentesBusinessLogic rrs = new PasivosContingentesBusinessLogic(jniName);
            if ("2".equals(tipoReporte)) {
                //poliza mensual pasivo patrimonial
                String fAplicacion = req.getParameter("fecha_aplicacion");
                if (StringUtils.isEmpty(fAplicacion))
                    throw new Exception("No se recibio la fecha de aplicacion.");
                int nFolioDocPoliza = rrs.generaPoliza(fAplicacion, u.getU_UR(), u.getPropiedad("CCENTROCONTABLE").getValor(), u.getLogin(), u.getNombre(), reportPath, u);
                msg = "Se genero exitosamente la poliza con folio: " + nFolioDocPoliza + ". Con Firma Electronica.";
                session.setAttribute("RESULT", msg);
            } else if ("4".equals(tipoReporte)) {
                //poliza mensual pasivo laboral
                String fAplicacion = req.getParameter("fecha_aplicacion");
                if (StringUtils.isEmpty(fAplicacion))
                    throw new Exception("No se recibio la fecha de aplicacion.");
                int nFolioDocPoliza = rrs.generaPolizaLaboral(fAplicacion, u.getU_UR(), u.getPropiedad("CCENTROCONTABLE").getValor(), u.getLogin(), u.getNombre(), reportPath, u);
                msg = "Se genero exitosamente la poliza con folio: " + nFolioDocPoliza + ". Con Firma Electronica.";
                session.setAttribute("RESULT", msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Ocurrio el siguiente error: " + e.getMessage();
            session.setAttribute("RESULT", msg);
        }
        if ("2".equals(tipoReporte)) {
            resp.sendRedirect("../Generador/CedulayPolizaPasivoContingentePatrimonial.jsp");
        } else if ("4".equals(tipoReporte)) {
            resp.sendRedirect("../Generador/CedulayPolizaPasivoContingenteLaboral.jsp");
        }
        return;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("CEDULA", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaPasivosContingentesPatrimoniales.xls"));
                plantillas.put("CEDULALABORAL", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaPasivosContingentesLaborales.xls"));
                plantillas.put("CEDULAANTIGUEDAD", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaAntiguedad_PasivosContingentes.xls"));
                plantillas.put("CEDULATRIMESTRAL", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaTrimestral_PasivosContingentes.xls"));
                plantillas.put("CEDULAANTIGUEDADEXPEDIENTE", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaAntiguedad_PasivosContingentes_Expedientes.xls"));
                plantillas.put("CEDULAANTIGUEDADEXPEDIENTECONSOLIDADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaAntiguedad_PasivosContingentes_ExpedientesConsolidado.xls"));
            }
        }
    }
}
