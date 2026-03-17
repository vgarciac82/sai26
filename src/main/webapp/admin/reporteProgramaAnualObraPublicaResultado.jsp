<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!
	private Logger log = LoggerFactory.getLogger(getClass());
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
			String rpt_header=("<tr height=34 style='height:25.5pt'>");
			String TipoReporte=  getValor(request.getParameter("TipoReporte"));
			String FiltroReporte=  getValor(request.getParameter("FiltroReporte"));
			String rpt_body ="";

				String DepuraLineas=  getValor(request.getParameter("DepuraLineas"));
				String DepuraColumnas=  getValor(request.getParameter("DepuraColumnas"));
				String mesIni=  getValor(request.getParameter("mesIni"));
				String mesFin=  getValor(request.getParameter("mesFin"));
				String FiltroSubcuenta=  getValor(request.getParameter("FiltroSubcuenta"));
				String TipoSubCuentaC=  getValor(request.getParameter("TipoSubCuentaC"));
				String buscaCuentaIni=  getValor(request.getParameter("buscaCuentaIni"));
				String buscaCuentaFin	=  getValor(request.getParameter("buscaCuentaFin"));
				String aEjercicioFiscal=  getValor(request.getParameter("aEjercicioFiscal"));
				String cCentroContable=  getValor(request.getParameter("cCentroContable"));

				rpt_header+=("<th height=34 width=52 style='height:25.5pt;width:39pt text-align:justify;' valing=top>CLAVE_CUCOP (Ver cat logo Cucop)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CONCEPTO (TODO EN MAYUSCULAS)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Valor total Multianual Estimado (En miles de pesos y sin decimales)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Valor estimado de compras a Mipymes (0 Porque no hay PYMES en Obra)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Valor estimado de compras no cubiertas por TLC (En miles de pesos y sin decimales)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Cantidad (En n£mero enteros)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Unidad de Medida (Ver Cat logo)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Car cter del procedimiento de contrataci¢n (N= NACIONAL o I= Internacional)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Entidad Federativa (Ver Cat logo)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Trimestre1 (Enteros y 0 si no ee reporta nada)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Trimestre2 (Enteros y 0 si no ee reporta nada)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Trimestre3 (Enteros y 0 si no ee reporta nada)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Trimestre4 (Enteros y 0 si no ee reporta nada)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Fecha Inicial Contrato</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Plurianual (0=Anual y 1= Plurianual)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Ejercicios ficales (1=Mismo A¤o y 2 mas de un a¤o)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Monto Anual a Ejercer en el Presente A¤o (En miles de pesos y decimales)</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>comentario1</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Fecha Final Contrato</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>comentario3</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>Tipo del Procedimiento (Adjudicacion)</td>");
				rpt_header+=("</tr>");

				session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);

				ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
				rpt_body = rbl.ReportePAOP(aEjercicioFiscal,  mesIni);

				session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);

 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>


    <title>'reporteProgramaAnualObraPublicaResultado.jsp' starting page</title>

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
			$("#cmdExcel").attr("disabled", true);
			var param = "&rptExcel="+ document.getElementById("hidEXCEL").value
				+ "&FiltroReporte=<%=FiltroReporte%>";
			var url = "../reportes/reporte_export.jsp?id=15&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			//alert(url);
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
			$("#cmdExcel").attr("disabled", false);
		}
  </script>

  <body>
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel"        	onclick="javascript:openExcel();" />
	</div>
		<table width="100%" border="1">
			<%=rpt_header %>
			<%=rpt_body %>
		</table>
  </body>
</html>
