<%@page language="java" import="java.util.*"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%!private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;
	
	/**
	* versión MODIFICADA del MultiReporteResultado.jsp
	* modificada por Martha Aurora Sánchez Valdivieso
	* para SYC Constructores de Sistemas SA de CV
	* desarrollo gestion_conagua_sif
	* México D.F. 26/07/2012 - 14/08/2012
	*/

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log
						.info("Environment Entry \"dataSourceRefName\" nula usando default \""
								+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log
					.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
							+ jniName + "\"");
		}
	}

	public String getValor(String data) {
		return (data == null ? "" : data);
	}%>
<%
	String ep = getValor(request.getParameter("ep"));
	//String login=  getValor(request.getParameter("login"));
	String ejercicioFiscal = getValor(request.getParameter("EjercicioFiscal"));
	String ramoEP = getValor(request.getParameter("RamoEP"));
	String unidadResponsableEP = getValor(request.getParameter("UnidadResponsableEP"));
	String grupoFuncional = getValor(request.getParameter("GrupoFuncional"));
	String funcion = getValor(request.getParameter("Funcion"));
	String subFuncion = getValor(request.getParameter("SubFuncion"));
	String programaGeneral = getValor(request.getParameter("ProgramaGeneral"));
	String programaPresupuestario = getValor(request.getParameter("ProgramaPresupuestario"));
	String actividadInstitucional = getValor(request.getParameter("ActividadInstitucional"));
	String partida = getValor(request.getParameter("Partida"));
	String tipoGasto = getValor(request.getParameter("TipoGasto"));
	String fuenteFinanciamiento = getValor(request.getParameter("FuenteFinanciamiento"));
	String entidadFederativa = getValor(request.getParameter("EntidadFederativa"));
	String cartera = getValor(request.getParameter("Cartera"));
	String unidadNormativa = getValor(request.getParameter("UnidadNormativa"));
	String unidadEjecutora = getValor(request.getParameter("UnidadEjecutora"));

	String cOrderBy = getValor(request.getParameter("cOrderBy"));
	String cGroupBy = getValor(request.getParameter("cGroupBy"));

	String cuenta = getValor(request.getParameter("Cuenta"));

	String usuario = getValor(request.getParameter("Usuario"));
	String filtro = getValor(request.getParameter("Filtro"));

	String rpt_header = "<tr align=\"center\">";
	String rpt_body = "";
	
	//RENGLON||EJERCICIO FISCAL||UNIDAD RESPONSABLE||UNIDADEJECUTORA||CLAVESIAFF||CLAVEINTERNA||mCALCULADO||mOPTIMO||mIRREDUCTIBLE||
	rpt_header+="<th>FOLIO ANTEPROYECTO AUTORIZADO</th>";
	rpt_header+="<th>RENGLON</th>";
	rpt_header+=( cGroupBy.equals("aEjercicioFiscal_1")|| cGroupBy.equals("cSubCuenta")?"<th>EJERCICIO FISCAL</th>":"");
	rpt_header+=( cGroupBy.equals("cUnidadResponsable_3")|| cGroupBy.equals("cSubCuenta")?"<th>UNIDAD RESPONSABLE</th>":"");
	rpt_header+=( cGroupBy.equals("cUnidadResponsable_15")|| cGroupBy.equals("cSubCuenta")?"<th>UNIDAD EJECUTORA</th>":"");
	rpt_header+="<th>CLAVE SIAFF</th>";
	rpt_header+="<th>CLAVE INTERNA</th>";

	if ( cuenta == null || cuenta.equals("") ) {
		rpt_header+= ( cuenta==""|| cuenta.contains("calculado")?"<th>MONTO CALCULADO</th>":"");
		rpt_header+= ( cuenta==""|| cuenta.contains("optimo")?"<th>MONTO OPTIMO</th>":"");
		rpt_header+= ( cuenta==""|| cuenta.contains("irreductible")?"<th>MONTO IRREDUCTIBLE</th>":"");
	}

	if ( cuenta != null ) {
		rpt_header+= ( cuenta.contains("calculado")?   "<th>MONTO CALCULADO</th>"   : "");
		rpt_header+= ( cuenta.contains("optimo")?	   "<th>MONTO OPTIMO</th>"		: "");
		rpt_header+= ( cuenta.contains("irreductible")?"<th>MONTO IRREDUCTIBLE</th>": "");
	}

	rpt_header+="</tr>";

	session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);
	ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);

	if (ep == "" || ejercicioFiscal == "" || unidadEjecutora == "" || ramoEP == "" || unidadResponsableEP == "" || grupoFuncional == "" || funcion == "" || subFuncion == "" || programaGeneral == "" || programaPresupuestario == "" || actividadInstitucional == "" || partida == "" || tipoGasto == "" || fuenteFinanciamiento == "" || entidadFederativa == "" || cartera == ""){
		rpt_body = rbl.ResultadoCreacionDelPresupuesto(ep, ejercicioFiscal, ramoEP, unidadResponsableEP, grupoFuncional, funcion, subFuncion, programaGeneral, programaPresupuestario, actividadInstitucional, partida, tipoGasto, fuenteFinanciamiento, entidadFederativa, cartera, unidadEjecutora, unidadNormativa, cOrderBy, cGroupBy, cuenta, usuario, filtro );
	}

	session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);
	
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<title>Resultado</title>
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
			var param = "&rptExcel="+ document.getElementById("hidEXCEL").value;
			/*
			Esteban Badillo. Fecha: 11/Sep/2009. Descripcion: Se agrega el paso del parametro "orden" para realizar la impresion
			condicional de columnas en la exportaciÃ³n del reporte a Excel.
			*/
			param += "&orden=" + <%="\"" + cOrderBy + "\""%>;

			var url = "../reportes/reporte_export.jsp?id=9&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
  </script>
  <body>
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel" onclick="javascript:openExcel();" />
	</div>
		<table width="100%" border="1" >
			<%=rpt_header%>
			<%=rpt_body%>
		</table>
  </body>
</html>