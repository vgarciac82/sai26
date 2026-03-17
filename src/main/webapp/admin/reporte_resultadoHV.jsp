<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
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
	String no_oficio="";
	String fechaini="";
	String fechafin="";
	String area="";
	String rpt_body="";
	
	no_oficio = getValor(request.getParameter("no_oficio"));
	fechaini = getValor(request.getParameter("fechaini"));
	fechafin = getValor(request.getParameter("fechafin"));
	area = getValor(request.getParameter("area"));
	
	//paramPDF = getParamAuditoria(fechaini, fechafin, rptname, login,area);
	
	//tipo_acumulado = getValor(request.getParameter("tipo_acumulado"));
	ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
	if(area!="" || fechaini!="" || fechafin!=""|| no_oficio!="")
		rpt_body = rbl.ReporteHomoViati(no_oficio, fechaini, fechafin,area);

 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

    
    <title>'reporte_resultadoHV.jsp' starting page</title>
    
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
  
  <body>
		<table width="100%" border="1">
			<tr>
				<th>Area</th>
				<th>Fecha</th>
				<th>No de Oficio</th>
			</tr>
			<%=rpt_body %>
		</table>
  </body>
</html>
