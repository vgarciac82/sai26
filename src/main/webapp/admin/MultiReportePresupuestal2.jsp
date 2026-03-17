<%@page language="java" import="java.util.*"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.contable.core.ReportePrespuestal"%>
<%@page import="java.io.File"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="org.apache.poi.ss.usermodel.Sheet"%>
<%@page import="org.apache.poi.ss.usermodel.Workbook"%>
<%@page import="java.io.BufferedOutputStream"%>

<%!


/***********************************V ersion 1.0 de MultiReporte Presupuestal****************************************************/

private Logger log = Logger.getLogger(getClass());
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
	String tipoDoc = "";
	String tipoDocActual = "";
	String foliDoc = "";
	String folioDocActual = "";
	int totalTipoDoc = 0;
	int totalFolioDoc = 0;
	int renglon = 0;
	boolean inicio = true;
	String encabezados = "";
	List ReportePrespuestalList = new ArrayList();
	ReporteBussinesLogic obj = new ReporteBussinesLogic(jniName);

	String opcion = getValor(request.getParameter("opcion"));
	if (opcion.equals("reporte")) {
		String EPs = getValor(request.getParameter("EPs"));
		String ProgramaPresupuestario = getValor(request.getParameter("ProgramaPresupuestario"));
		String Partida = getValor(request.getParameter("Partida"));
		String UnidadResponsableEP = getValor(request.getParameter("UnidadResponsableEP"));
		String UnidadEjecutora = getValor(request.getParameter("cUnidadEjecutora"));
		String Cartera = getValor(request.getParameter("Cartera"));
		String ChkReintegros = getValor(request.getParameter("chkReintegros"));
		String ChkRectificaciones = getValor(request.getParameter("chkRectificaciones"));
		String ChkAdecuaciones = getValor(request.getParameter("chkAdecuaciones"));
		String ChkPagosAnticipados = getValor(request.getParameter("chkPagosAnticipados"));
		String ChkPlurianuales = getValor(request.getParameter("chkPlurianuales"));
		String ChkOriginal = getValor(request.getParameter("chkOriginal"));
		String ChkModificado = getValor(request.getParameter("chkModificado"));
		String ChkAmpAutorizada = getValor(request.getParameter("chkAmpAutorizada"));
		String ChkRedAutorizada = getValor(request.getParameter("chkRedAutorizada"));
		String ChkAmpenTramite = getValor(request.getParameter("chkAmpenTramite"));
		String ChkRedenTramite = getValor(request.getParameter("chkRedenTramite"));
		String ChkReienTramite = getValor(request.getParameter("chkReienTramite"));
		String ChkRectificacion = getValor(request.getParameter("chkRectificacion"));
		String ChkRedSHCPenTramite = getValor(request.getParameter("chkRedSHCPenTramite"));
		String ChkRedSHCPAplicada = getValor(request.getParameter("chkRedSHCPAplicada"));
		String ChkApartado = getValor(request.getParameter("chkApartado"));
		String ChkPrecomprometido = getValor(request.getParameter("chkPrecomprometido"));
		String ChkComprometido = getValor(request.getParameter("chkComprometido"));
		String ChkDevengado = getValor(request.getParameter("chkDevengado"));
		String ChkEjernoPagado = getValor(request.getParameter("chkEjernoPagado"));
		String ChkDisponibleNeto = getValor(request.getParameter("chkDisponibleNeto"));
		String ChkDisponibleBruto = getValor(request.getParameter("chkDisponibleBruto"));
		String ChkEjercidoPagado = getValor(request.getParameter("chkEjercidoPagado"));
		String sULogin = getValor(request.getParameter("sULogin"));
	
		encabezados = "<tr class=\"alternateRow\"><td colspan=\"17\"><i>Reporte de Adeucaciones Presupuestales</i></td></tr>";
		encabezados+= "<tr>";
		encabezados+= "<th>Cuenta</th>";
		encabezados+= "<th>Fecha Movimiento</th>";
		encabezados+= "<th>Tipo Documento</th>";
		encabezados+= "<th>Folio Documento</th>";
		encabezados+= "<th>Estructura Programatica:</th>";
		//encabezados+= "<th>Cancelado</th>";
		encabezados+= "<th>Anual</th>";
		encabezados+= "<th>Enero</th>";
		encabezados+= "<th>Febrero</th>";
		encabezados+= "<th>Marzo</th>";
		encabezados+= "<th>Abril</th>";
		encabezados+= "<th>Mayo</th>";
		encabezados+= "<th>Junio</th>";
		encabezados+= "<th>Julio</th>";
		encabezados+= "<th>Agosto</th>";
		encabezados+= "<th>Septiembre</th>";
		encabezados+= "<th>Octubre</th>";
		encabezados+= "<th>Noviembre</th>";
		encabezados+= "<th>Diciembre</th>";
		encabezados+= "</tr>";

		ReportePrespuestalList = obj.MultiReportePresupuestal(EPs, ProgramaPresupuestario, Partida, UnidadResponsableEP, 
		UnidadEjecutora, Cartera, ChkReintegros, ChkRectificaciones, ChkAdecuaciones, ChkPagosAnticipados, ChkPlurianuales, ChkOriginal, 
		ChkModificado, ChkAmpAutorizada, ChkRedAutorizada, ChkAmpenTramite, ChkRedenTramite, ChkReienTramite, ChkRectificacion, ChkRedSHCPenTramite, ChkRedSHCPAplicada, 
		ChkApartado, ChkPrecomprometido, ChkComprometido, ChkDevengado, ChkEjernoPagado, ChkDisponibleNeto, ChkDisponibleBruto, ChkEjercidoPagado,sULogin);
	
	
		tipoDoc = "TipoDoc";
		foliDoc = "FolioDoc";
		if (ReportePrespuestalList.size() == 0)
		{
			sb.append("<tr class=\"alternateRow\">" + "<td colspan=\"17\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>");
		}
		else
		{
			session.setAttribute("listaReportePresu", ReportePrespuestalList);
		
			for (Iterator<?> iter = ReportePrespuestalList.iterator(); iter.hasNext(); renglon++) {
				ReportePrespuestal reportePrespuestal = (ReportePrespuestal) iter.next();
		
				if (inicio)
				{
					tipoDoc = reportePrespuestal.getcTipoDoc();
					inicio = false;
				}
		
				tipoDocActual = reportePrespuestal.getcTipoDoc();
				if (!tipoDoc.equals(tipoDocActual))
				{
					sb.append("<tr class=\"alternateRow\">" + "<td colspan=\"17\" align=\"left\">Total Movimientos: " + totalTipoDoc + "</td>" + "</tr>");
					sb.append("<tr class=\"alternateRow\">" + "<td colspan=\"17\" align=\"left\">Total Documentos: " + totalFolioDoc + "</td>" + "</tr>");
					totalTipoDoc = 0;
					totalFolioDoc = 0;
					tipoDoc = reportePrespuestal.getcTipoDoc();
				}
				folioDocActual = reportePrespuestal.getCfolio();
				if (!foliDoc.equals(folioDocActual))
				{
					totalFolioDoc++;
					foliDoc = reportePrespuestal.getCfolio();
				}
				totalTipoDoc++;
	
				sb.append("<tr class=" + ((renglon % 2) == 0 ? "AlternateRow" : "NormalRow") + ">");
				sb.append("<td align=\"center\">" + reportePrespuestal.getcCuenta() + "</td>");
				sb.append("<td align=\"center\">" + reportePrespuestal.getfFechaMov() + "</td>");
				sb.append("<td align=\"center\">" + reportePrespuestal.getcTipoDoc() + "</td>");
				sb.append("<td align=\"center\">" + reportePrespuestal.getCfolio()+ "</td>");
				sb.append("<td align=\"center\">" + reportePrespuestal.getcEP() + "</td>");
				//sb.append("<td align=\"center\">" + reportePrespuestal.getcCancelado() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcAnual() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcEnero() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcFebrero() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcMarzo() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcAbril() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcMayo() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcJunio() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcJulio() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcAgosto() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcSeptiembre() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcOctubre() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcNoviembre() + "</td>");
				sb.append("<td align=\"right\" style='mso-number-format:\"Standard\"'>" + reportePrespuestal.getcDiciembre() + "</td>");
				sb.append("</tr>");
			}
			sb.append("<tr class=\"alternateRow\">" + "<td colspan=\"17\" align=\"left\">Total Movimientos: " + totalTipoDoc + "</td>" + "</tr>");
			sb.append("<tr class=\"alternateRow\">" + "<td colspan=\"17\" align=\"left\">Total Documentos: " + totalFolioDoc + "</td>" + "</tr>");
		}
	}
	else
	{
		ReportePrespuestalList = (List)session.getAttribute("listaReportePresu");

		String file_name = "PlantillaReportePresupuestal" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "PlantillaReportePresupuestal.xls");
		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(file_name);

		Workbook workbook = new HSSFWorkbook(fsArchivo);
		Sheet sheet = workbook.getSheetAt(0);
		
		sheet = obj.callExcelMultiReportePresupuestal(sheet, ReportePrespuestalList, 3);
		
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
			var url = "../admin/MultiReportePresupuestal2.jsp?opcion=excel";
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
