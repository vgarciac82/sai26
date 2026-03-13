package com.syc.gestion.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.core.ReporteConf;
import com.syc.gestion.servlet.ActualizaAplicacionServlet;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteExportServlet", urlPatterns = { "/reportes/reporte_export.jsp" })
public class ReporteExportServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ActualizaAplicacionServlet.class);

    private String tempDir = null;

    private String jniName = null;

    private boolean isTmpWidthSessionId = true;

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + "Reportes" + File.separator + "ChartImg" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("../index.jsp");
            return;
        }
        String rpt_export = req.getParameter("exportto");
        if (rpt_export == null) {
            log.warn("Par\u00e1metros incompletos: No se conoce el formato de exportaci\u00f3n ");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        String rpt_header = (String) session.getAttribute(GestionInterface.ATT_EXP_HEADER);
        if (rpt_header == null) {
            log.warn("Par\u00e1metros incompletos: Reporte excel");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        String rpt_bdy = (String) session.getAttribute(GestionInterface.ATT_EXP_BODY);
        StringBuffer kl = (StringBuffer) session.getAttribute("ATT_SB_BODY");
        StringBuffer rpt_bdy_detalle = (StringBuffer) session.getAttribute(GestionInterface.ATT_EXP_BODY_DETALLE);
        if (rpt_bdy == null && kl.toString() == null) {
            log.warn("Par\u00e1metros incompletos: Reporte excel ");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        ReporteConf in_rc = new ReporteConf();
        String idreporte = req.getParameter("id");
        if (idreporte == null) {
            log.warn("Par\u00e1metros incompletos: id ");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        in_rc.setId(Integer.parseInt(idreporte));
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        String file_name = "";
        PrintWriter out = resp.getWriter();
        int tipo_exp = Integer.parseInt(rpt_export);
        try {
            switch(in_rc.getId()) {
                case GestionInterface.RPT_GENERAL:
                    file_name = "ReporteGeneral";
                    break;
                case GestionInterface.RPT_CONSOLIDADO:
                    file_name = "Consolidado";
                    break;
                case GestionInterface.RPT_RESPUESTA:
                    file_name = "Respuesta";
                    break;
                case GestionInterface.RPT_POR_AREA:
                    file_name = "ReportePorArea";
                    break;
                case GestionInterface.RPT_POR_EMPLEADO:
                    file_name = "ReportePorEmpleado";
                    break;
                case GestionInterface.RPT_DETALLADO:
                    file_name = "ReporteDetallado";
                    break;
                case GestionInterface.RPT_POR_EMPLEADO_DETALLE:
                    file_name = ("ReporteDetallePorEmpleado");
                    break;
                case GestionInterface.RPT_POR_AREA_DETALLE:
                    //file_name = ("ReporteDetallePorArea");
                    file_name = ("reportePresupuesto");
                    break;
                case GestionInterface.RPT_BALANZA:
                    //file_name = ("ReporteDetallePorArea");
                    file_name = ("reporteBalanza");
                    break;
                case GestionInterface.RPT_PAOP:
                    file_name = ("reportePAOP");
                    break;
                case GestionInterface.RPT_COMPARATIVA:
                    //file_name = ("ReporteDetallePorArea");
                    file_name = ("tablaComparativa");
                    break;
            }
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy h:m:s");
            Calendar c2 = GregorianCalendar.getInstance();
            switch(tipo_exp) {
                case RPT_EXP_HTML:
                    resp.setContentType("text/html");
                    resp.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".html\";");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Reportes Resultado</title>");
                    out.println("	<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/reportes.css\" />");
                    out.println("</head>");
                    out.println("<body>");
                    out.println(rpt_header);
                    out.println("<div id=\"divAcciones\" align=\"right\"><input type=\"button\" value=\"IMPRIMIR\" onclick=\"window.print();\"></div>");
                    break;
                case RPT_EXP_EXCEL:
                    resp.setContentType("application/vnd.ms-excel");
                    resp.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".xls\";");
                    //out.println("	<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/reportes.css\" />");
                    //out.println(rpt_header);
                    break;
                case RPT_EXP_CHART:
                    resp.setContentType("text/html");
                    resp.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".html\";");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Reportes Resultado</title>");
                    out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/reportes.css\" />");
                    out.println("</head>");
                    out.println("<body>");
                    out.println(rpt_header);
                    out.println("<div id=\"divAcciones\" align=\"right\"><input type=\"button\" value=\"IMPRIMIR\" onclick=\"window.print();\"></div>");
                    break;
            }
            switch(in_rc.getId()) {
                case GestionInterface.RPT_GENERAL:
                    String orden = req.getParameter("orden");
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"98%\">");
                    out.println("<tr>");
                    out.println("		<td height=\"98%\">");
                    out.println("			<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("				<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\" border=\"1\" class=\"scrollTable\">");
                    out.println("					<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println("						<tr>");
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    if ("REMITENTE".equals(orden)) {
                        out.println("							<th id=\"idTh\" colspan=\"2\">RECIBIDOS DE</th>");
                    } else if ("RESPONSABLE".equals(orden)) {
                        out.println("							<th id=\"idTh\" colspan=\"2\">ENVIADOS A</th>");
                    }
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    out.println("							<th id=\"idTh\" colspan=\"3\">&nbsp;</th>");
                    out.println("						</tr>");
                    out.println("						<tr>");
                    out.println("							<th id=\"idTh\">&nbsp;</th>");
                    out.println("							<th id=\"idTh\" nowrap>FOLIO</th>");
                    out.println("							<th id=\"idTh\">REFERENCIA</th>");
                    out.println("							<th id=\"idTh\" nowrap>&Aacute;REA</th>");
                    out.println("							<th id=\"idTh\" nowrap>EMPLEADO</th>");
                    out.println("							<th id=\"idTh\">ESTATUS</th>");
                    out.println("							<th id=\"idTh\" nowrap>FECHA LIMITE DE ATENCION</th>");
                    out.println("							<th id=\"idTh\" nowrap>FECHA DE ENVIO</th>");
                    out.println("							<th id=\"idTh\" nowrap>TIPO DE INSTRUCCI&Oacute;N</th>");
                    out.println("							<th id=\"idTh\" nowrap>FECHA DE REGISTRO</th>");
                    out.println("							<th id=\"idTh\">PRIORIDAD</th>");
                    out.println("							<th id=\"idTh\" width=\"2000\">ASUNTO</th>");
                    out.println("						</tr>");
                    out.println("					</thead>");
                    out.println("					<tbody class=\"scrollContent\">");
                    out.println("					");
                    out.println(kl.length() > 0 ? kl.toString() : rpt_bdy);
                    out.println("			        </tbody>");
                    out.println("		        </table>");
                    out.println("	       </div>");
                    out.println("		</td>");
                    out.println("</tr>");
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_CONSOLIDADO:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\">");
                    out.println("	<tr>");
                    out.println("		<td></td>");
                    out.println("		<td id=\"idTh\" colspan=\"4\">PENDIENTES</td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("	</tr>");
                    out.println("	<tr>");
                    out.println("		<th>&nbsp;</th>");
                    out.println("		<td id=\"idTh\">&Aacute;REA</td>");
                    out.println("		<td id=\"idTh\">VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">NO VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("		<td id=\"idTh\">CONCLUIDOS</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("	</tr>");
                    out.println("   ");
                    out.println(rpt_bdy == null ? kl.toString() : rpt_bdy);
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_RESPUESTA:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\">");
                    out.println("	<tr>");
                    out.println("		<td></td>");
                    out.println("		<td id=\"idTh\" colspan=\"5\">PENDIENTES</td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("	</tr>");
                    out.println("	<tr>");
                    out.println("		<td id=\"idTh\">&Aacute;</td>");
                    out.println("		<td id=\"idTh\">VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">NO VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("		<td id=\"idTh\">CONCLUIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("	</tr>");
                    out.println("   ");
                    out.println(rpt_bdy);
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_POR_AREA:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\">");
                    out.println("	<tr>");
                    out.println("		<td colspan=\"2\"></td>");
                    out.println("<td id=\"idTh\" colspan=\"4\">PENDIENTES</td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("	</tr>");
                    out.println("	<tr>");
                    out.println("		<td id=\"idTh\">&nbsp;</td>");
                    out.println("		<td id=\"idTh\">&Aacute;REA</td>");
                    out.println("		<td id=\"idTh\">VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">NO VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("		<td id=\"idTh\">CONCLUIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("	</tr>");
                    out.println("	");
                    out.println(rpt_bdy);
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_POR_EMPLEADO:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\">");
                    out.println("	<tr>");
                    out.println("		<td></td>");
                    out.println("		<td id=\"idTh\" colspan=\"5\">PENDIENTES</td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("	</tr>");
                    out.println("	<tr>");
                    out.println("		<td id=\"idTh\">&nbsp;</td>");
                    out.println("		<td id=\"idTh\">FUNCIONARIO</td>");
                    out.println("	    <td id=\"idTh\">VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">NO VENCIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("		<td id=\"idTh\">CONCLUIDOS</td>");
                    out.println("		<td id=\"idTh\">%</td>");
                    out.println("		<td id=\"idTh\">TOTAL</td>");
                    out.println("	</tr>");
                    out.println("	");
                    out.println(rpt_bdy);
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_DETALLADO:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\">");
                    out.println("	<tr>");
                    out.println("		<td></td>");
                    out.println("		<td id=\"idTh\" colspan=\"5\">PENDIENTES</td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("		<td></td>");
                    out.println("	</tr>");
                    out.println("	<tr>");
                    out.println("		<th id=\"idTh\">&nbsp;</th>");
                    out.println("		<td id=\"idTh\">FOLIO</td>");
                    out.println("	    <td id=\"idTh\">REFERENCIA</td>");
                    out.println("		<td id=\"idTh\">STATUS</td>");
                    out.println("		<td id=\"idTh\">TIPO DE ASUNTO</td>");
                    out.println("		<td id=\"idTh\">ASUNTO</td>");
                    out.println("		<td id=\"idTh\">RESPUESTA</td>");
                    out.println("	</tr>");
                    out.println("	");
                    out.println(rpt_bdy);
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_POR_EMPLEADO_DETALLE:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
                    out.println(" <tr>");
                    out.println("	<td height=\"98%\" valing=\"top\">");
                    out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
                    out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println("					<tr>");
                    out.println("						<th id=\"idTh\">&nbsp;</th>");
                    out.println("						<!--th id=\"idTh\">&Aacute;REA</th-->");
                    out.println("						<th id=\"idTh\">FOLIO</th>");
                    out.println("						<th id=\"idTh\">REFERENCIA</th>");
                    out.println("						<th id=\"idTh\">NOMBRE DEL REMITENTE</th>");
                    out.println("						<th id=\"idTh\">&Aacute;REA DEL REMITENTE</th>");
                    out.println("						<th id=\"idTh\">FECHA ENVIO</th>");
                    out.println("						<th id=\"idTh\">FECHA LIMITE</th>");
                    out.println("						<th id=\"idTh\">ESTATUS</th>");
                    out.println("						<!--th id=\"idTh\">TURNADO A</th-->");
                    out.println("						<th id=\"idTh\">PRIORIDAD</th>");
                    out.println("						<th id=\"idTh\">ASUNTO</th>");
                    out.println("					</tr>");
                    out.println("				</thead>");
                    out.println("				<tbody class=\"scrollContent\">");
                    out.println("			    ");
                    out.println(in_rc.getId() == GestionInterface.RPT_POR_EMPLEADO_DETALLE ? rpt_bdy_detalle : rpt_bdy);
                    out.println("				</tbody>");
                    out.println("			</table>");
                    out.println("		</div>");
                    out.println("	    </td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    break;
                /*case GestionInterface.RPT_POR_AREA_DETALLE:
					out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
					out.println(" <tr>");
					out.println("	<td height=\"98%\" valing=\"top\">");
					out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
					out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
					out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
					out.println("					<tr>");
					out.println("						<th id=\"idTh\">&nbsp;</th>");
					out.println("						<!--th id=\"idTh\">&Aacute;REA</th-->");
					out.println("						<th id=\"idTh\">FOLIO</th>");
					out.println("						<th id=\"idTh\">REFERENCIA</th>");
					out.println("						<th id=\"idTh\">NOMBRE DEL REMITENTE</th>");
					out.println("						<th id=\"idTh\">&Aacute;REA DEL REMITENTE</th>");
					out.println("						<th id=\"idTh\">NOMBRE DEL RESPONSABLE</th>");
					out.println("						<th id=\"idTh\">&Aacute;REA DEL RESPONSABLE</th>");
					out.println("						<th id=\"idTh\">FECHA ENVIO</th>");
					out.println("						<th id=\"idTh\">FECHA LIMITE</th>");
					out.println("						<th id=\"idTh\">ESTATUS</th>");
					out.println("						<!--th id=\"idTh\">TURNADO A</th-->");
					out.println("						<th id=\"idTh\">PRIORIDAD</th>");
					out.println("						<th id=\"idTh\">ASUNTO</th>");
					out.println("					</tr>");
					out.println("				</thead>");
					out.println("				<tbody class=\"scrollContent\">");
					out.println("			    ");
					out.println((in_rc.getId() == GestionInterface.RPT_POR_AREA_DETALLE ? rpt_bdy_detalle : rpt_bdy));
					out.println("				</tbody>");
					out.println("			</table>");
					out.println("		</div>");
					out.println("	    </td>");
					out.println("  </tr>");
					out.println("</table>");
					break;
*/
                case GestionInterface.RPT_POR_AREA_DETALLE:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
                    out.println(" <tr>");
                    out.println(" <td><img src=\"./imagenes/logotipo-usuario.png\"></td>");
                    out.println(" </tr>");
                    out.println(" <tr><td align=\"center\"><B>COMISION NACIONAL FORESTAL</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>PRESUPUESTO DE EGRESOS</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + sdf2.format(c2.getTime()).toString() + "</B></td></tr>");
                    //out.println(" <tr><td align=\"center\"><B>"+new Date().toGMTString()+"</B></td></tr>");
                    out.println(" <tr><td align=\"center\">&nbsp;</td></tr>");
                    out.println(" <tr>");
                    out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
                    out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
                    out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println(rpt_header);
                    out.println("				</thead>");
                    out.println("				<tbody class=\"scrollContent\">");
                    out.println("			    ");
                    out.println(rpt_bdy);
                    out.println("				</tbody>");
                    out.println("			</table>");
                    out.println("		</div>");
                    out.println("	    </td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_POLIZAS:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
                    out.println(" <tr>");
                    out.println(" <td><img src=\"./imagenes/logotipo-usuario.png\"></td>");
                    out.println(" </tr>");
                    out.println(" <tr><td align=\"center\"><B>COMISION NACIONAL FORESTAL</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>POLIZAS</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + new Date().toGMTString() + "</B></td></tr>");
                    out.println(" <tr><td align=\"center\">&nbsp;</td></tr>");
                    out.println(" <tr>");
                    out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
                    out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
                    out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println(rpt_header);
                    out.println("				</thead>");
                    out.println("				<tbody class=\"scrollContent\">");
                    out.println("			    ");
                    out.println(rpt_bdy);
                    out.println("				</tbody>");
                    out.println("			</table>");
                    out.println("		</div>");
                    out.println("	    </td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_BALANZA:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
                    out.println(" <tr>");
                    out.println(" <td><img src=\"./imagenes/logotipo-usuario.png\"></td>");
                    out.println(" </tr>");
                    out.println(" <tr><td align=\"center\"><B>COMISION NACIONAL FORESTAL</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>SISTEMA DE ADMINISTRACION INTEGRAL</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>CONTABILIDAD</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + req.getParameter("hcCentroContable") + "</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + req.getParameter("FiltroReporte") + "</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + new Date().toString() + "</B></td></tr>");
                    out.println(" <tr><td align=\"center\">&nbsp;</td></tr>");
                    out.println(" <tr>");
                    out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
                    out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
                    out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println(rpt_header);
                    out.println("				</thead>");
                    out.println("				<tbody class=\"scrollContent\">");
                    out.println("			    ");
                    out.println(rpt_bdy);
                    out.println("				</tbody>");
                    out.println("			</table>");
                    out.println("		</div>");
                    out.println("	    </td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_PAOP:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
                    out.println(" <tr>");
                    out.println(" <td><img src=\"./imagenes/logotipo-usuario.png\"></td>");
                    out.println(" </tr>");
                    out.println(" <tr><td align=\"center\"><B>COMISION NACIONAL FORESTAL</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>PROGRAMA ANUAL DE OBRAS PUBLICAS (PAOP)</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>MODULO DE OBRA PUBLICA</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + req.getParameter("FiltroReporte") + "</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>" + new Date().toGMTString() + "</B></td></tr>");
                    out.println(" <tr><td align=\"center\">&nbsp;</td></tr>");
                    out.println(" <tr>");
                    out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
                    out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
                    out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println(rpt_header);
                    out.println("				</thead>");
                    out.println("				<tbody class=\"scrollContent\">");
                    out.println("			    ");
                    out.println(rpt_bdy);
                    out.println("				</tbody>");
                    out.println("			</table>");
                    out.println("		</div>");
                    out.println("	    </td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    break;
                case GestionInterface.RPT_COMPARATIVA:
                    out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
                    out.println(" <tr>");
                    out.println(" <td><img src=\"./imagenes/logotipo-usuario.png\"></td>");
                    out.println(" </tr>");
                    out.println(" <tr><td align=\"center\"><B>                                             SUBDIRECCIÓN GENERAL DE ADMINISTRACIÓN</B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>GERENCIA DE RECURSOS MATERIALES           </B></td></tr>");
                    out.println(" <tr><td align=\"center\"><B>TABLA COMPARATIVA DE COTIZACIONES</B></td></tr>");
                    out.println(" <tr><td align=\"center\">&nbsp;</td></tr>");
                    out.println(" <tr>");
                    out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
                    out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
                    out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
                    out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
                    out.println(rpt_header);
                    out.println("				</thead>");
                    out.println("				<tbody class=\"scrollContent\">");
                    out.println("			    ");
                    out.println(rpt_bdy);
                    out.println("				</tbody>");
                    out.println("			</table>");
                    out.println("		</div>");
                    out.println("	    </td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    break;
            }
            switch(tipo_exp) {
                case RPT_EXP_HTML:
                    out.println("</body>");
                    out.println("</html>");
                    break;
                case RPT_EXP_EXCEL:
                    break;
                case RPT_EXP_CHART:
                    String nameRpt = null;
                    try {
                        nameRpt = GeneraGrafica(req, resp, session.getId());
                    } catch (GestionException e) {
                        log.error(e);
                        throw new ServletException(e);
                    }
                    out.println("	<table border=\"0\">");
                    out.println("		<tr>");
                    out.println("			<td></td>");
                    out.println("		</tr>");
                    out.println("		<tr>");
                    out.println("			<td><img id=\"imgChart\" src=\"" + req.getContextPath() + "/Reportes/ChartImg/" + session.getId() + "/" + nameRpt + "\"></td>");
                    out.println("		</tr>");
                    out.println("	</table>");
                    out.println("</body>");
                    out.println("</html>");
                    break;
            }
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private String GeneraGrafica(HttpServletRequest req, HttpServletResponse resp, String sessionId) throws GestionException {
        String id = req.getParameter("id");
        String titulo_rpt = "";
        String nameChartJpg = "";
        //Esteban Badillo. Fecha: 26/Febrero/2010. Se agregan nuevas variables para el paso de datos de la gr\u00e1fica
        HttpSession session = req.getSession();
        StringBuffer sbGrafColumnKeys = (StringBuffer) session.getAttribute("ATT_SB_GRAF_COLUMNKEYS");
        StringBuffer sbGrafVencidos = (StringBuffer) session.getAttribute("ATT_SB_GRAF_VENCIDOS");
        StringBuffer sbGrafNoVencidos = (StringBuffer) session.getAttribute("ATT_SB_GRAF_NOVENCIDOS");
        StringBuffer sbGrafConcluidos = (StringBuffer) session.getAttribute("ATT_SB_GRAF_CONCLUIDOS");
        //String columskey = req.getParameter("COLUMSKEYS");
        //String vencidos = req.getParameter("VENCIDOS");
        //String novencidos = req.getParameter("NO_VENCIDOS");
        //String concluidos = req.getParameter("CONCLUIDOS");
        String columskey = sbGrafColumnKeys.toString();
        String vencidos = sbGrafVencidos.toString();
        String novencidos = sbGrafNoVencidos.toString();
        String concluidos = sbGrafConcluidos.toString();
        if (columskey == null || vencidos == null || novencidos == null || concluidos == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int cmd = Integer.parseInt(id);
        switch(cmd) {
            case //1
            GestionInterface.RPT_CONSOLIDADO:
                titulo_rpt = "Reporte Consolidado";
                nameChartJpg = "consolidado.jpg";
                break;
            case // 2
            GestionInterface.RPT_GENERAL:
                titulo_rpt = "Reporte General";
                nameChartJpg = "general.jpg";
                break;
            case // 3
            GestionInterface.RPT_POR_EMPLEADO:
                titulo_rpt = "Reporte por Empleado";
                nameChartJpg = "porempleado.jpg";
                break;
            case // 4
            GestionInterface.RPT_POR_AREA:
                titulo_rpt = "Reporte por \u00e1rea";
                nameChartJpg = "porarea.jpg";
                break;
            case // 5
            GestionInterface.RPT_DETALLADOVIEJO:
                titulo_rpt = "Reporte Detallado";
                nameChartJpg = "detallado.jpg";
                break;
            case // 11
            GestionInterface.RPT_RESPUESTA:
                titulo_rpt = "Reporte Respuesta Folio";
                nameChartJpg = "detallado.jpg";
                break;
            case // 12
            GestionInterface.RPT_DETALLADO:
                titulo_rpt = "Reporte Detallado";
                nameChartJpg = "detallado.jpg";
                break;
        }
        String ETQ_1 = "VENCIDOS";
        String ETQ_2 = "NO VENCIDOS";
        String ETQ_3 = "CONCLUIDOS";
        // Creamos y rellenamos el modelo de datos
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] colKey = columskey.split("\\,");
        String[] valKey = vencidos.split("\\,");
        // Visitas del sitio web 1
        int ctaAnchoGrafica = 0;
        int anchoGrafica = 1024;
        for (int i = 0; i < colKey.length; i++) {
            dataset.setValue(Integer.parseInt(valKey[i]), ETQ_1, colKey[i]);
        }
        valKey = novencidos.split("\\,");
        for (int i = 0; i < colKey.length; i++) {
            dataset.setValue(Integer.parseInt(valKey[i]), ETQ_2, colKey[i]);
        }
        valKey = concluidos.split("\\,");
        for (int i = 0; i < colKey.length; i++) {
            dataset.setValue(Integer.parseInt(valKey[i]), ETQ_3, colKey[i]);
            if (ctaAnchoGrafica++ > 9) {
                anchoGrafica += 256;
                ctaAnchoGrafica = 0;
            }
        }
        // Show legend
        JFreeChart // Show legend
        chart = // Show legend
        ChartFactory.// Show legend
        createBarChart3D(// Show legend
        titulo_rpt, // Show legend
        "\u00e1reas", // Show legend
        "N\u00famero de Asuntos", // Show legend
        dataset, // Show legend
        PlotOrientation.VERTICAL, // Show legend
        true, // Show legend
        true, true);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 3.0));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setFillZoomRectangle(true);
        //chartPanel.setMouseWheelEnabled(true);
        //chartPanel.setPreferredSize(new Dimension(500, 270));
        //chartPanel.setContentPane(chartPanel);
        //OutputStream os = response.getOutputStream().;
        //os.write("<table><tr><td>Titulo de la gr&aacute;fica</td></tr></table>".getBytes());
        //ChartUtilities.writeChartAsJPEG(response.getOutputStream(),chart,1280,800);
        try {
            if (isTmpWidthSessionId)
                createDirTempWidthSessionId(sessionId);
            ChartUtilities.saveChartAsJPEG(new File(tempDir + File.separator + sessionId + File.separator + nameChartJpg), chart, anchoGrafica, 900);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nameChartJpg;
    }

    private void createDirTempWidthSessionId(String idSession) throws ServletException {
        File fDir = new File(tempDir + idSession + File.separator);
        if (!fDir.exists())
            if (!fDir.mkdirs())
                throw new ServletException("No se pudo crear el directorio " + tempDir);
    }
}
