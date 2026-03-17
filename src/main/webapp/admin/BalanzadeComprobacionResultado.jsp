<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
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
			String rpt_header=("<tr height=34 style='height:25.5pt'>");
			String TipoReporte=  getValor(request.getParameter("TipoReporte"));
			String FiltroReporte=  getValor(request.getParameter("FiltroReporte"));
			String cCentroContable=  getValor(request.getParameter("cCentroContable"));
			String hcCentroContable=  getValor(request.getParameter("hcCentroContable"));
		
			
			String rpt_body ="";

			if (TipoReporte.equals("Auxiliares")){

				String buscaCuentaIni=  getValor(request.getParameter("buscaCuentaIni"));
				String fAuxIni=  getValor(request.getParameter("fAuxIni"));
				String fAuxFin=  getValor(request.getParameter("fAuxFin"));
				String swhere=  getValor(request.getParameter("swhere"));
/*
				rpt_header+=("<th height=34 width=52 style='height:25.5pt;width:39pt text-align:justify;' valing=top>CUENTA</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>FOLIO POLIZA</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CENTRO CONTABLE</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CONCEPTO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>REFERENCIA</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>FOLIO DOCUMENTO MOVIMIENTO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>MOVIMIENTO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>TIPO POLIZA</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>TIPO MOVIMIENTO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>MOVIMIENTO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>ACUMULADO</td>");
				rpt_header+=("</tr>");
*/
				rpt_header+=("<th height=34 width=52 style='height:25.5pt;width:39pt text-align:justify;' valing=top>TIPO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>NÚM</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CxP</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>FECHA</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CONCEPTO</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>REFERENCIA</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CARGOS</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>ABONOS</td>");
				rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO</td>");
				rpt_header+=("</tr>");

				session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);

				ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
				rpt_body = rbl.ReporteAuxiliares( swhere,cCentroContable, buscaCuentaIni,fAuxIni,fAuxFin);

				session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);

			} else {
				String DepuraLineas=  getValor(request.getParameter("DepuraLineas"));
				String DepuraColumnas=  getValor(request.getParameter("DepuraColumnas"));
				String mesIni=  getValor(request.getParameter("mesIni"));
				String mesFin=  getValor(request.getParameter("mesFin"));
				String FiltroSubcuenta=  getValor(request.getParameter("FiltroSubcuenta"));
				String TipoSubCuentaC=  getValor(request.getParameter("TipoSubCuentaC"));
				String buscaCuentaIni=  getValor(request.getParameter("buscaCuentaIni"));
				String buscaCuentaFin	=  getValor(request.getParameter("buscaCuentaFin"));
				String aEjercicioFiscal=  getValor(request.getParameter("aEjercicioFiscal"));

				rpt_header+=("<th height=34 width=52 style='height:25.5pt;width:39pt text-align:justify;' valing=top>"+(TipoReporte.equals("Balanza")?"CUENTA":(TipoReporte.equals("Analitico")?"CLAVE C.C.":(TipoSubCuentaC.equals("RFC")?"RFC":(TipoSubCuentaC.equals("CTAB")?"CUENTA":"NUMERO"))))+"</td>");
				rpt_header+=("<th width=288 style='width:216pt text-align:justify;'>"+(TipoReporte.equals("Balanza")?"DESCRIPCION":(TipoReporte.equals("Analitico")?"CENTRO CONTABLE":(TipoSubCuentaC.equals("RFC")?"NOMBRE":(TipoSubCuentaC.equals("CTAB")?"CTA. BANCARIA":"ALMACEN"))))+"</td>");

				if (DepuraColumnas.equals("N")){
					rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO INICIAL</td>");
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>MOVIMIENTOS ACUMULADOS DEBE</td>"); 
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>MOVIMIENTOS ACUMULADOS HABER</td>");
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO MES ANTERIOR</td>");
					rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>MOVIMIENTOS DEBE DEL MES</td>");
					rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>MOVIMIENTOS HABER DEL MES</td>");
					rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO FINAL</td>");
				} else {
					/*rpt_header+=("<th width");*/
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO INICIAL DEUDOR</td>"); 
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO INICIAL ACREEDOR</td>");
					rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>CARGOS</td>");
					rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>ABONOS<td>");
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO FINAL DEUDOR</td>"); 
				  	rpt_header+=("<th width=145 style='width:109pt text-align:justify;'>SALDO FINAL ACREEDOR</td>");
				}
				

				rpt_header+=("</tr>");

				session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);

				ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
				rpt_body = rbl.ReporteBalanza( buscaCuentaIni, buscaCuentaFin,  aEjercicioFiscal,  DepuraLineas,  DepuraColumnas, cCentroContable,  TipoReporte,  mesIni,
								mesFin,  FiltroSubcuenta);
System.out.println(rpt_body);
				session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);
			}

 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>


  <head>


    <title>'BalanzadeComprobacionResultado.jsp' starting page</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
	<link href="../css/vistaPrevia.css" rel="stylesheet" type="text/css" />
	
	
	
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
  	
  	var vPrevia=<%=request.getParameter("vprevia")%>
	
		function openExcel(){
			
		
			///$("#cmdExcel").attr("disabled", true);
			var param = "&rptExcel="+document.getElementById("hidEXCEL").value
				+ "&FiltroReporte=<%=FiltroReporte%>&hcCentroContable=<%=hcCentroContable%>";
			var url = "../reportes/reporte_export.jsp?id=13&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768,location=no,toolbar=no,titlebar=0,toolbar=0");
			
			
			//$("#cmdExcel").attr("disabled", false);
		}
		
		
		
	$(document).ready(function() {
	if(vPrevia!=1)
		{
		$("#vistaPrevia").hide();
		openExcel();
		}
	

}); 	
	  </script>

  <body >
  
 
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
	
	
		<!--<input type="button" id="cmdExcel" name="cmdExcel" value="Excel"  onclick="javascript:openExcel();" />-->
	</div>
	
	<table width="100%"  id="vistaPrevia">
	       <thead> 
	       <%=rpt_header %> 
	       </thead>
		   <tbody>
		   <%=rpt_body %>
		   </tbody>
	</table>
	
	
	 
	
  </body>
</html>
