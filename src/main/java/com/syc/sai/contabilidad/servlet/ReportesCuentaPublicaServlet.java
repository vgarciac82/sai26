package com.syc.sai.contabilidad.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.CuentaPublicaBusinessLogic;
import com.syc.sai.contabilidad.CuentaPublicaCuerpoReportes;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CuentaPublica", urlPatterns = { "/reports/CuentaPublica" })
public class ReportesCuentaPublicaServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(ReportesCuentaPublicaServlet.class);

    private static final long serialVersionUID = -5034769853645642993L;

    private static final String REPORTS_BODY_ROOT = "ctaPublica";

    private static final String htmlBodyI = "<HTML>\n " + "\t<HEAD>\n " + "\t\t<TITLE>\n</TITLE>\n " + "\t\t<style type=\"text/css\">\n " + "\t\t\tbody {\n " + "\t\t\t\tfont-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;\n " + "\t\t\t\tfont-size: 18;\n " + "\t\t\t}\n " + ".encabezadoTopBottomRight { " + "	border-top-style: solid; " + "	border-top-color: black; " + "	border-top-width: thin; " + "	border-bottom-style: solid; " + "	border-bottom-color: black; " + "	border-bottom-width: thin; " + "	border-right-style: solid; " + "	border-right-color: black; " + "	border-right-width: thin; " + "}" + "\t\t\t.encabezadoTopBottomLeft { " + "\t\t\t\t	border-top-style: solid;\n " + "\t\t\t\t	border-top-color: black;\n " + "\t\t\t\t	border-top-width: thin;\n " + "\t\t\t\t	border-bottom-style: solid;\n " + "\t\t\t\t	border-bottom-color: black;\n " + "\t\t\t\t	border-bottom-width: thin;\n " + "\t\t\t\t	border-left-style: solid;\n " + "\t\t\t\t	border-left-color: black;\n " + "\t\t\t\t	border-left-width: thin;\n " + "\t\t\t\t} " + "\t\t\t.encabezadoTop {\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoTopBottom{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoTopLeft {\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoTopRight {\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoTopLeftRight{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoTopLeftBottom{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoTopRightBottom{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoLeftRight {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoBottomLeft {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoBottomLeftRight {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoBottomRight {\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoLeft {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoRight {\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoBottom {\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + ".encabezadoAll {\n " + "\tborder-top-style: solid;\n " + "\tborder-top-color: black;\n " + "\tborder-top-width: thin;\n " + "\tborder-left-style: solid;\n " + "\tborder-left-color: black;\n " + "\tborder-left-width: thin;\n " + "\tborder-right-style: solid;\n " + "\tborder-right-color: black;\n " + "\tborder-right-width: thin;\n " + "\tborder-bottom-style: solid;\n " + "\tborder-bottom-color: black;\n " + "\tborder-bottom-width: thin;\n " + "}\n" + "\t\t</style>\n " + "\t</HEAD>\n " + "\t<BODY>\n";

    private static final String htmlBodyF = "\n\t</BODY>\n" + "</HTML>";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            sendError(resp, "Session Terminada. Ingrese nuevamente al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            sendError(resp, "No hay usuario en session. Ingrese nuevamente al sistema");
            return;
        }
        try {
            String reportType = req.getParameter("reportType");
            boolean isSP = "true".equals(req.getParameter("isSP"));
            String reportResult = "";
            String reportName = "";
            CuentaPublicaBusinessLogic cpbl = new CuentaPublicaBusinessLogic();
            if ("C32AP390".equals(reportType)) {
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("C32AP390");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("C32AP390", getReportStream("C32AP390"));
                reportResult = cpbl.generateReportC32AP390();
                reportName = "C32AP390";
            } else if ("C32AP400".equals(reportType)) {
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("C32AP400");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("C32AP400", getReportStream("C32AP400"));
                reportResult = cpbl.generateReportC32AP400();
                reportName = "C32AP400";
            } else if ("C32AP405".equals(reportType)) {
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("C32AP405");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("C32AP405", getReportStream("C32AP405"));
                reportResult = cpbl.generateReportC32AP405();
                reportName = "C32AP405";
            } else if ("C11IF085".equals(reportType)) {
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("C11IF085I");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("C11IF085I", getReportStream("C11IF085I"));
                reportBody = CuentaPublicaCuerpoReportes.getReportBody("C11IF085S");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("C11IF085S", getReportStream("C11IF085S"));
                reportResult = cpbl.generateReportC11IF085();
                reportName = "C11IF085";
            } else {
                String[] condiciones = req.getParameterValues("condicion");
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody(reportType);
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody(reportType, getReportStream(reportType));
                if ((reportType.compareTo("PreCierre") == 0) || (reportType.compareTo("ContraCuentas") == 0) || (reportType.compareTo("ConciliacionCostoOperacion") == 0) || (reportType.compareTo("ReporteCapitulo1000concepto") == 0) || (reportType.compareTo("BalanzaDet") == 0) || (reportType.compareTo("BalanzaDet_v2") == 0) || (reportType.compareTo("BalanzaDet_Ac") == 0) || (reportType.compareTo("DIOT") == 0) || (reportType.compareTo("DIOT_v2") == 0) || (reportType.compareTo("DIM") == 0)) {
                    cpbl.generateReport(reportType, isSP, condiciones, resp);
                    System.out.println(reportType);
                } else
                    reportResult = cpbl.generateReport(reportType, isSP, condiciones);
                reportName = reportType;
            }
            if (reportResult != null && !"".equals(reportResult)) {
                sendExcel(resp, reportResult, reportName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendError(resp, e.toString());
        }
    }

    private static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    private static void sendExcel(HttpServletResponse resp, String reportResult, String reportName) throws IOException {
        resp.setContentType("application/vnd.ms-excel");
        resp.addHeader("Content-Disposition", "inline; filename=\"reporte" + reportName + "_" + System.currentTimeMillis() + ".xls\";");
        PrintWriter out = resp.getWriter();
        out.println(htmlBodyI + reportResult + htmlBodyF);
        out.flush();
        out.close();
    }

    private InputStream getReportStream(String reportType) {
        InputStream is = null;
        String reportSrcName = null;
        try {
            reportSrcName = ReportesCuentaPublicaServlet.REPORTS_BODY_ROOT + File.separator + reportType + ".bdy";
            log.debug("Object: {}", "Report Name -> " + reportSrcName);
            String reportPath = getServletContext().getRealPath(reportSrcName);
            log.debug("Object: {}", "Report Path-> " + reportPath);
            if (reportPath != null) {
                is = new FileInputStream(reportPath);
            } else {
                if (!reportSrcName.startsWith("/"))
                    reportSrcName = "/" + reportSrcName;
                reportSrcName.replaceAll("\\\\", "/");
                is = getServletContext().getResource(reportSrcName).openStream();
            }
        } catch (Exception e) {
            log.error("No se pudo cargar o no existe el archivo " + reportSrcName, e);
        }
        return is;
    }
}
