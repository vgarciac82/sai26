package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.axtel.egresos.viaticos.ViaticosServlet;
import com.syc.egresos.ResponseJSON;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.reportes.repConciliacionBancoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReporteConciliacionBancoServlet", urlPatterns = { "/reportes/RepConciliaBancosServlet" })
public class repConciliacionBancoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = Logger.getLogger(repConciliacionBancoServlet.class);

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
        int tipoReporte = Integer.parseInt(req.getParameter("report"));
        int idRep = (req.getParameter("nIdReporte") == null || req.getParameter("nIdReporte") == "") ? 0 : Integer.parseInt(req.getParameter("nIdReporte"));
        if (idRep == 5)
            tipoReporte = 5;
        repConciliacionBancoBusinessLogic rrs = new repConciliacionBancoBusinessLogic(jniName);
        try {
            switch(tipoReporte) {
                case 1:
                    rrs.generaReporteConciliacion(req, resp, plantillas);
                    break;
                case 2:
                    rrs.generaReporteConciliados(req, resp, plantillas);
                    break;
                case 3:
                    rrs.generaReporteConEdoCta(req, resp, plantillas);
                    break;
                case 4:
                    rrs.generaReporteConAuxiliar(req, resp, plantillas);
                    break;
                case 5:
                    String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
                    String cEsFiel = req.getParameter("esFiel");
                    String nombreReporte = "";
                    if (cEsFiel.equalsIgnoreCase("S")) {
                        nombreReporte = "ConciliaBancos_FIEL.jasper";
                    } else {
                        nombreReporte = "ConciliaBancos.jasper";
                    }
                    rrs.reporteConciliacion(req, resp, reportPath, nombreReporte, cEsFiel);
                    Util.sendHTMLSuccessMsg(resp);
                    //ResponseJSON responseJSON = new ResponseJSON( true, null, Arrays.asList( ( new String [] { String.valueOf( "OK" ) } ) ) );
                    //sendJSONResponse( resp, responseJSON );
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error: " + e, e);
            List<String> errores = new ArrayList<String>();
            errores.add(e.toString());
            ResponseJSON responseJSON = new ResponseJSON(false, errores, null);
            try {
                sendJSONResponse(resp, responseJSON);
            } catch (Exception e2) {
                throw new ServletException(e2);
            }
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("REPCONCILIACION", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteConciliacion.xls"));
                plantillas.put("REPCONMOVIMIENTOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteConciliados.xls"));
                plantillas.put("REPCONAUXILIAR", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteConAuxiliar.xls"));
                plantillas.put("REPCONEDOCTA", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteConEdoCta.xls"));
            }
        }
    }

    public void sendJSONResponse(HttpServletResponse resp, ResponseJSON responseJSON) throws Exception {
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            resp.getWriter();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            out.print(responseJSON.toJSON());
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e2) {
                log.warn("Problemas cerrando flujo: " + e2);
            }
        }
    }
}
