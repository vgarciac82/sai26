<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.adquisiciones.core.ComparativaBusinessLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%!
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

	public String getValor(String data){
		return (data == null? "": data);
	}
	
 %>
<%
				String rpt_header=("");
			    String rpt_body ="";
				String cEjercicio=  getValor(request.getParameter("cEjercicio"));
				String cIdTipoProcedimiento=  getValor(request.getParameter("cIdTipoProcedimiento"));
				String cIdUnidadEjecutora=  getValor(request.getParameter("cIdUnidadEjecutora"));
				int nIdConsecutivo=Integer.parseInt(request.getParameter("nIdConsecutivo"));
				String cTipoReporte=getValor(request.getParameter("tipoReporte"));
				 
				

				session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);
				
				ComparativaBusinessLogic rbl = new ComparativaBusinessLogic(jniName);
				
				rpt_body = rbl.ReporteComparativa(cEjercicio, cIdTipoProcedimiento, cIdUnidadEjecutora, nIdConsecutivo,cTipoReporte);
				
				session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>


    <title>'TablaComparativa.jsp' starting page</title>

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
	<script type="text/javascript" src="../js/datepickercontrol.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
  	<script language="javascript">
		function openExcel(){
			var param = "&rptExcel="+document.getElementById("hidEXCEL").value
				+ "&FiltroReporte=Prueba";
			
			var url = "../../reportes/reporte_export.jsp?id=100&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
  </script>

  <body>
	<input type="hidden" id="hidEXCEL" name="hidEXCEL">
	<div id="divAcciones" align="right">
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel" onclick="javascript:openExcel();" />
	</div>
		 <table width="100%" border="2">
			<!--<=rpt_header %>
			 -->
			<%=rpt_body %>
		</table>
  </body>
</html>
