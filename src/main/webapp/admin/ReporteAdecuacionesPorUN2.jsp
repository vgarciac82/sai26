<%@page language="java" import="java.util.*"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.contable.core.ReporteAdecuacionesPorUN"%>
<%@page import="java.io.File"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="org.apache.poi.ss.usermodel.Sheet"%>
<%@page import="org.apache.poi.ss.usermodel.Workbook"%>
<%@page import="java.io.BufferedOutputStream"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%!private Logger log = LoggerFactory.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
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

	public String getValor(String data) {
		return (data == null ? "" : data);
	}%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	StringBuffer sb = new StringBuffer();
	String unidadResponsable = "";
	int renglon = 0;
	String encabezados = "";
	String folioSICOP = "";
	String folioMAP = "";
	List ReporteAdecuacionesPorUNList = new ArrayList();
	ReporteBussinesLogic obj = new ReporteBussinesLogic(jniName);

	String opcion = getValor(request.getParameter("opcion"));
	if (opcion.equals("reporte")) {
		String Ep = getValor(request.getParameter("EP"));
		String UnidadResponsableEP = getValor(request.getParameter("UnidadResponsableEP"));
		String Folio = getValor(request.getParameter("mFolio"));
		String Estatus = getValor(request.getParameter("mEstatus"));
		String FechaIni = getValor(request.getParameter("fechaIni"));
		String FechaFin = getValor(request.getParameter("fechaFin"));
		String Monto = getValor(request.getParameter("mMonto"));
		String FolioSICOP = getValor(request.getParameter("mFolioSICOP"));
		String FolioMAP = getValor(request.getParameter("mFolioMAP"));
		String Login = getValor(request.getParameter("login"));
	
		encabezados = "<tr class=\"alternateRow\"><td colspan=\"8\" align=\"center\"><font size=\"5\">Reporte Adecuaciones por Unidad Responsable</font></td></tr>";

		ReporteAdecuacionesPorUNList = obj.ReporteAdecuacionesPorUN(Ep, UnidadResponsableEP, Folio, Estatus, FechaIni, FechaFin, Monto, FolioSICOP, FolioMAP, Login);
	
		unidadResponsable = "";
		if (ReporteAdecuacionesPorUNList.size() == 0)
		{
			sb.append("<tr class=\"alternateRow\">" + "<td colspan=\"8\"><font size=\"4\">No hay informaci&oacute;n para mostrar</font></td>" + "</tr>");
		}
		else
		{
			session.setAttribute("listaReporteAdecuaciones", ReporteAdecuacionesPorUNList);

			for (Iterator<?> iter = ReporteAdecuacionesPorUNList.iterator(); iter.hasNext(); renglon++) {
				ReporteAdecuacionesPorUN reporteAdecuacionesPorUN = (ReporteAdecuacionesPorUN) iter.next();

				if (!reporteAdecuacionesPorUN.getcUnidad().equals(unidadResponsable))
				{
					unidadResponsable = reporteAdecuacionesPorUN.getcUnidad();
					sb.append("<tr class=\"alternateRow\"><td colspan=\"8\" align=\"center\"><font size=\"3\">UNIDAD: " + unidadResponsable + "</font></td></tr>");
					sb.append("<tr>");
					sb.append("<th>Folio</th>");
					sb.append("<th>Estructura Programática</th>");
					sb.append("<th>Estatus</th>");
					sb.append("<th>Fecha Operacion</th>");
					sb.append("<th>Monto</th>");
					sb.append("<th>Folio SICOP</th>");
					sb.append("<th>Folio MAP</th>");
					sb.append("<th>Usuario</th>");
					sb.append("</tr>");
					
				}
				String monto = reporteAdecuacionesPorUN.getcMonto();
				folioSICOP = reporteAdecuacionesPorUN.getcFolioSICOP().equals("")?"&nbsp":reporteAdecuacionesPorUN.getcFolioSICOP();
				folioMAP = reporteAdecuacionesPorUN.getcFolioMAP().equals("")?"&nbsp":reporteAdecuacionesPorUN.getcFolioMAP();
				sb.append("<tr class=" + ((renglon % 2) == 0 ? "AlternateRow" : "NormalRow") + ">");
				sb.append("<td align=\"center\">" + reporteAdecuacionesPorUN.getCfolio() + "</td>");
				sb.append("<td align=\"center\">" + reporteAdecuacionesPorUN.getEp() + "</td>");
				sb.append("<td align=\"center\">" + reporteAdecuacionesPorUN.getcEstatus() + "</td>");
				sb.append("<td align=\"center\">" + reporteAdecuacionesPorUN.getfFecha() + "</td>");
				sb.append("<td align=\"right\">"  + reporteAdecuacionesPorUN.getcMonto() + "</td>");
				sb.append("<td align=\"center\">" + folioSICOP + "</td>");
				sb.append("<td align=\"center\">" + folioMAP + "</td>");
				sb.append("<td align=\"center\">" + reporteAdecuacionesPorUN.getcUsuario() + "</td>");
				sb.append("</tr>");
			}
		}
	}
	else
	{
		ReporteAdecuacionesPorUNList = (List)session.getAttribute("listaReporteAdecuaciones");

		String file_name = "PlantillaReporteAdecuacionesPorUN" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "PlantillaReporteAdecuacionesPorUN.xls");
		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(file_name);

		Workbook workbook = new HSSFWorkbook(fsArchivo);
		Sheet sheet = workbook.getSheetAt(0);

		sheet = obj.callExcelReporteAdecuacionesPorUN(sheet, ReporteAdecuacionesPorUNList, 1);

		fsArchivo.close();

		File fsalida = new File(file_name);

		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");

		FileOutputStream fos = new FileOutputStream(fsalida);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
		workbook.write(bos);

		/* Cierra Flujos */
		bos.flush();
		bos.close();
		fos.close();

		ServletOutputStream out1 = response.getOutputStream();

		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(file_name);

		Util.doDownload(out1, file_name, file_name, mimetype);

		out1.flush();
		out1.close();
		if (!fsalida.delete()) {
			fsalida.deleteOnExit();
		}
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>


    <title>'MultiReporteResultado.jsp' starting page</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
    	<script language="javascript">
		function openExcel(){
			var url = "../admin/ReporteAdecuacionesPorUN2.jsp?opcion=excel";
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
  </script>

  <body>
  	<input type="hidden" id="hidPDF" name="hidPDF" />
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel" onclick="javascript:openExcel();" />
	</div>
		<table width="100%" border="1" >
			<%=encabezados%>
			<%=sb.toString()%>
		</table>
		
		<form id="adecuacion" name="adecuacion" method="post" action="../gstnmngr/Adecuacion">
		</form>
  </body>
</html>
