<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%!private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

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

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String rpt_body = "";

	String ep = getValor(request.getParameter("ep"));
	String EjercicioFiscal = getValor(request
			.getParameter("EjercicioFiscal"));

//	String Cuenta = getValor(request.getParameter("Cuenta"));
	String Cuenta= "'"+request.getParameter("nCtasPresups").replace(",","','")+"'";
/*
	StringBuffer cadena = new StringBuffer();
	String[] selections = request.getParameterValues("Cuenta");
	 if (selections != null) {for (int x=0;x<selections.length;x++){if (cadena.append(selections[x])!= null) {cadena =cadena.append(selections[x]);}}}
	String Cuenta = cadena.toString();
*/
	String NomCuenta = getValor(request.getParameter("Cuenta"));
	//String login=  getValor(request.getParameter("login"));
	String UnidadEjecutora = getValor(request
			.getParameter("cUnidadEjecutora"));
	String hcUnidadEjecutora = getValor(request
			.getParameter("hcUnidadEjecutora"));
	String RamoEP = getValor(request.getParameter("RamoEP"));
	String UnidadResponsableEP = getValor(request
			.getParameter("UnidadResponsableEP"));
	String hUnidadResponsableEP = getValor(request
			.getParameter("hUnidadResponsableEP"));
	String GrupoFuncional = getValor(request
			.getParameter("GrupoFuncional"));
	String Funcion = getValor(request.getParameter("Funcion"));
	String SubFuncion = getValor(request.getParameter("SubFuncion"));
	String ProgramaGeneral = getValor(request
			.getParameter("ProgramaGeneral"));
	String ProgramaPresupuestario = getValor(request
			.getParameter("ProgramaPresupuestario"));
	String hProgramaPresupuestario = getValor(request
			.getParameter("hProgramaPresupuestario"));
	String ActividadInstitucional = getValor(request
			.getParameter("ActividadInstitucional"));
	String Partida = getValor(request.getParameter("Partida"));
	String hPartida = getValor(request.getParameter("hPartida"));
	String TipoGasto = getValor(request.getParameter("TipoGasto"));
	String FuenteFinanciamiento = getValor(request
			.getParameter("FuenteFinanciamiento"));
	String EntidadFederativa = getValor(request
			.getParameter("EntidadFederativa"));
	String Cartera = getValor(request.getParameter("Cartera"));
	String hCartera = getValor(request.getParameter("hCartera"));
	String UnidadNormativa = getValor(request
			.getParameter("UnidadNormativa"));
	String hUnidadNormativa = getValor(request
			.getParameter("hUnidadNormativa"));
	String ClaveCNA = getValor(request.getParameter("ClaveCNA"));
	//String Mes=  getValor(request.getParameter("Mes"));
	String cOrddeBy = getValor(request.getParameter("cOrddeBy"));
	String cGroupBy = getValor(request.getParameter("cGroupBy"));
	String InfoRegMes = getValor(request.getParameter("InfoRegMes"));
	String TipoReporte = getValor(request.getParameter("TipoReporte"));
	String Componentes = getValor(request.getParameter("Componentes"));
	String tipoEP = getValor(request.getParameter("tipoEP"));
	String Resumen = getValor(request.getParameter("Resumen"));
	String Usuario = getValor(request.getParameter("Usuario"));
//	String Filtro = getValor(request.getParameter("Filtro"));
	String Filtro= request.getParameter("dCtasPresups");
	String Capitulo = request.getParameter( "Capitulo" );
	
	System.out.print( "Capitulo: " + Capitulo );
	if (Filtro == ""){
		Filtro="<tr class=\"alternateRow\"><td colspan=\"4\"><i>Presupuesto: Todo</i></td></tr>";
	} else {
		Filtro=	"<tr class=\"alternateRow\"><td colspan=\"4\"><i>"+"Presupuesto: "+Filtro+"</i></td></tr>";
	};

	Filtro=Filtro+"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Tipo de Reporte: "+TipoReporte+"</i></td></tr>";

	if (ep==""){
		Filtro=Filtro+(EjercicioFiscal!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Ejercicio Fiscal: "+EjercicioFiscal+"</i></td></tr>":"");
		Filtro=Filtro+(ProgramaPresupuestario!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Programa Presupuestario: "+hProgramaPresupuestario+"</i></td></tr>":"");
		Filtro=Filtro+(RamoEP!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Ramo: "+RamoEP+"</i></td></tr>":"");
		Filtro=Filtro+(Partida!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Partida: "+hPartida+"</i></td></tr>":"");
		Filtro=Filtro+(UnidadResponsableEP!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Unidad Responsable: "+hUnidadResponsableEP+"</i></td></tr>":"");
		Filtro=Filtro+(TipoGasto!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Tipo de Gasto: "+TipoGasto+"</i></td></tr>":"");
		Filtro=Filtro+(GrupoFuncional!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Grupo Funcional: "+GrupoFuncional+"</i></td></tr>":"");
		Filtro=Filtro+(FuenteFinanciamiento!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Fuente Financiamiento: "+FuenteFinanciamiento+"</i></td></tr>":"");
		Filtro=Filtro+(Funcion!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Funcion: "+Funcion+"</i></td></tr>":"");
		Filtro=Filtro+(EntidadFederativa!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Entidad Federativa: "+EntidadFederativa+"</i></td></tr>":"");
		Filtro=Filtro+(SubFuncion!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>SubFuncion: "+SubFuncion+"</i></td></tr>":"");
		Filtro=Filtro+(Cartera!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Cartera: "+hCartera+"</i></td></tr>":"");
		Filtro=Filtro+(ProgramaGeneral!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Programa General: "+ProgramaGeneral+"</i></td></tr>":"");
		Filtro=Filtro+(UnidadNormativa!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Unidad Normativa: "+hUnidadNormativa+"</i></td></tr>":"");
		Filtro=Filtro+(ActividadInstitucional!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Actividad Institucional: "+ActividadInstitucional+"</i></td></tr>":"");
		Filtro=Filtro+(UnidadEjecutora!=""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Unidad Ejecutora: "+hcUnidadEjecutora+"</i></td></tr>":"");
	} else {
		Filtro=Filtro+"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Estructura Programatica: "+ep+"</i></td></tr>";
	};

	String mes="";
	
	if (InfoRegMes=="") {
 		mes="12";
	} else {
 	 	mes=InfoRegMes;
 	 	mes=mes.replace("mSaldo", "");
	}

 	Filtro=Filtro+"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Informacion registrada al mes: "+mes+"</i></td></tr>";
	
	
//	String rpt_headerCtas = getValor(request.getParameter("rpt_headerCtas"));
/*	StringBuffer cadena = new StringBuffer();
	String[] selections = request.getParameterValues("ctasPresup");
	 if (selections != null) {for (int x=0;x<selections.length;x++){if (cadena.append(selections[x])!= null) {cadena =cadena.append(selections[x]);}}}
	String ctasPresup = cadena.toString();
	*/
//	String[] nCtasPresup  = request.getParameterValues("nCtasPresups");
//	String[] dCtasPresup  = request.getParameterValues("dCtasPresups");
	String[] nCtasPresup  = request.getParameter("nCtasPresups").split(",\\s*");
	String[] dCtasPresup  = request.getParameter("dCtasPresups").split(",\\s*");
	String rpt_headerCtas= "<th>"+request.getParameter("dCtasPresups").replace(",","</th><th>")+"</th>";
/*
	for (String nCtasPresups: nCtasPresup)
	System.out.println(nCtasPresups);
	
	for (String dCtasPresups: dCtasPresup)
	System.out.println(dCtasPresups);
*/
	//PARA EL LAYOUT DE ADECUACIONES, QUE VIAJE COMPLETO
	String nCtasPresup2  = request.getParameter("nCtasPresups");
	String dCtasPresup2  = request.getParameter("dCtasPresups");
	//*********************************
	
	String rpt_header = "<tr align=\"center\">";
	rpt_header+="<th>RENGLON</th>";
	rpt_header+=(TipoReporte.equals("GENERAL")?"":"<th>CUENTA</th>");
	rpt_header+=(cGroupBy.equals("cSubCuenta")?"<th>CODIGO</th>":"");
	rpt_header+=( cGroupBy.equals("cSubCuenta")?"<th>CLAVE PRESUPUESTAL</th>":"");
	rpt_header+=( cGroupBy.equals("aEjercicioFiscal_1")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>EJERCICIO FISCAL</th>":"");
	rpt_header+=( cGroupBy.equals("cRamo_2") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>RAMO</th>":"");
	rpt_header+=( cGroupBy.equals("cUnidadResponsable_3")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>UNIDAD RESPONSABLE</th>":"");
	rpt_header+=( cGroupBy.equals("cGrupoFuncional_4")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>GRUPO FUNCIONAL</th>":"");
	rpt_header+=( cGroupBy.equals("cFuncion_5") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>FUNCION</th>":"");
	rpt_header+=( cGroupBy.equals("cSubFuncion_6")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>SUB FUNCION</th>":"");
	rpt_header+=( cGroupBy.equals("cProgramaGeneral_7")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>PROGRAMA GENERAL</th>":"");
	rpt_header+=( cGroupBy.equals("cActividadInstitucional_8")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>ACTIVIDAD INSTITUCIONAL</th>":"");
	rpt_header+=( cGroupBy.equals("cProgramaPresupuestario_9")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>PROGRAMA PRESUPUESTARIO</th>":"");
	rpt_header+=( cGroupBy.equals("cPartida_10") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>PARTIDA</th>":"");
	rpt_header+=( cGroupBy.equals("cTipoGasto_11")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>TIPO GASTO</th>":"");
	rpt_header+=( cGroupBy.equals("cFuenteFinanciamiento_12")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>FUENTE FINANCIAMIENTO</th>":"");
	rpt_header+=( cGroupBy.equals("cEntidadFederativa_13")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>ENTIDAD FEDERATIVA</th>":"");
	rpt_header+=( cGroupBy.equals("cCartera_14") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>CARTERA</th>":"");
	rpt_header+=( cGroupBy.equals("cUnidadResponsable_15")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>UNIDAD EJECUTORA</th>":"");
	rpt_header+=( cGroupBy.equals("cUnidadResponsable_16")|| (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))?"<th>UNIDAD NORMATIVA</th>":"");

	if (TipoReporte.equals("GENERAL")) {
	 	rpt_header+= rpt_headerCtas;
	}
	rpt_header+= (TipoReporte.equals("GENERAL")?"":"<th>ANUAL</th><th>ENERO</th><th>FEBRERO</th><th>MARZO</th><th>ABRIL</th><th>MAYO</th><th>JUNIO</th><th>JULIO</th><th>AGOSTO</th><th>SEPTIEMBRE</th><th>OCTUBRE</th><th>NOVIEMBRE</th><th>DICIEMBRE</th>");
	rpt_header+="</tr>";
	session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);


	//paramPDF = getParamAuditoria(fechaini, fechafin, rptname, login,area);
//	String paramPDF = "&ejercicio_fiscal="+(ep==""&&EjercicioFiscal		!=""&&EjercicioFiscal		!=null?EjercicioFiscal:"");
	String paramPDF = "";
	paramPDF+="&condiciones=";
	paramPDF+=(EjercicioFiscal		!=""&&EjercicioFiscal		!=null?" AND s.aEjercicioFiscal = '" + EjercicioFiscal +"'":"");
	paramPDF+=(Cuenta				!=""&&Cuenta				!=null?" AND s.nCuentaP = '"+Cuenta+"'":"");
	paramPDF+=(ep					!=""&&ep					!=null?" AND s.ClaveSIAFF = '"+ep+"'":"");
	paramPDF+=(RamoEP				!=""&&RamoEP				!=null?" AND s.cRamoEP='"+RamoEP+"'":"");
	paramPDF+=(UnidadResponsableEP	!=""&&UnidadResponsableEP	!=null?" AND s.cUnidadResponsableEP='"+UnidadResponsableEP+"'":"");
	paramPDF+=(GrupoFuncional		!=""&&GrupoFuncional		!=null?" AND s.cGrupoFuncional='"+GrupoFuncional+"'":"");
	paramPDF+=(Funcion				!=""&&Funcion				!=null?" AND s.cFuncion='"+Funcion+"'":"");
	paramPDF+=(SubFuncion			!=""&&SubFuncion			!=null?" AND s.cSubFuncion='"+SubFuncion+"'":"");
	paramPDF+=(ProgramaGeneral		!=""&&ProgramaGeneral		!=null?" AND s.cProgramaGeneral='"+ProgramaGeneral+"'":"");
	paramPDF+=(ProgramaPresupuestario!=""&&ProgramaPresupuestario!=null?" AND s.cProgramaPresupuestario='"+ProgramaPresupuestario+"'":"");
	paramPDF+=(ActividadInstitucional!=""&&ActividadInstitucional!=null?" AND s.cActividadInstitucional='"+ActividadInstitucional+"'":"");
	paramPDF+=(Partida				!=""&&Partida				!=null?" AND s.cPartida='"+Partida+"'":"");
	paramPDF+=(TipoGasto			!=""&&TipoGasto				!=null?" AND s.cTipoGasto='"+TipoGasto+"'":"");
	paramPDF+=(FuenteFinanciamiento	!=""&&FuenteFinanciamiento	!=null?" AND s.cFuenteFinanciamiento='"+FuenteFinanciamiento+"'":"");
	paramPDF+=(EntidadFederativa	!=""&&EntidadFederativa		!=null?" AND s.cEntidadFederativa='"+EntidadFederativa+"'":"");
	paramPDF+=(Cartera				!=""&&Cartera				!=null?" AND s.cCartera='"+Cartera+"'":"");
	paramPDF+=(UnidadNormativa		!=""&&UnidadNormativa		!=null?" AND s.cUnidadNormativa='"+UnidadNormativa+"'":"");
	paramPDF+=(UnidadEjecutora		!=""&&UnidadEjecutora		!=null?" AND s.cUnidadEjecutora='"+UnidadEjecutora+"'":"");
	paramPDF+=(ClaveCNA				!=""&&ClaveCNA				!=null?" AND s.nClaveCNA='"+ClaveCNA+"'":"");
	paramPDF+=(Capitulo				!=""&&Capitulo				!=null?" AND SUBSTRING(s.cPartida,1,1)='"+Capitulo+"'":"");
	paramPDF+=(cOrddeBy 			!=""&&cOrddeBy				!=null?" Order by " + cOrddeBy +"" :"");

	System.out.print( paramPDF );
	//tipo_acumulado = getValor(request.getParameter("tipo_acumulado"));
	ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
	//	if (ep=="" || EjercicioFiscal=="" || Cuenta=="" ||  UnidadEjecutora=="" || RamoEP=="" || UnidadResponsableEP=="" || GrupoFuncional=="" || Funcion=="" || SubFuncion=="" || ProgramaGeneral=="" || ProgramaPresupuestario=="" || ActividadInstitucional=="" || Partida=="" || TipoGasto=="" || FuenteFinanciamiento=="" || EntidadFederativa=="" || Cartera=="" || UnidadNormativa=="" || ClaveCNA=="")
	if (ep == "" || EjercicioFiscal == "" || UnidadEjecutora == ""
			|| RamoEP == "" || UnidadResponsableEP == ""
			|| GrupoFuncional == "" || Funcion == ""
			|| SubFuncion == "" || ProgramaGeneral == ""
			|| ProgramaPresupuestario == ""
			|| ActividadInstitucional == "" || Partida == ""
			|| TipoGasto == "" || FuenteFinanciamiento == ""
			|| EntidadFederativa == "" || Cartera == ""
			|| UnidadNormativa == "" || ClaveCNA == "" || Capitulo == "" )
		
		rpt_body = rbl.MultiReporteResultado(ep, EjercicioFiscal,
				Cuenta, UnidadEjecutora, RamoEP, UnidadResponsableEP,
				GrupoFuncional, Funcion, SubFuncion, ProgramaGeneral,
				ProgramaPresupuestario, ActividadInstitucional,
				Partida, TipoGasto, FuenteFinanciamiento,
				EntidadFederativa, Cartera, UnidadNormativa, ClaveCNA,
				cOrddeBy, cGroupBy, InfoRegMes, TipoReporte, Usuario, Filtro, 
				nCtasPresup, dCtasPresup, Componentes, Resumen, tipoEP, Capitulo
				 );
	session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);
	
	String directToExcel = StringUtils.trimToEmpty(  request.getParameter("EXCEL_DIRECT") );
	
	log.debug( "Validando redireciion a EXCEL directo:?" + request.getParameter("EXCEL_DIRECT") );
	
	if( "S".equalsIgnoreCase( directToExcel ) ){

		String param = "&rptExcel=hidEXCEL";
		param += "&orden=\"" + cOrddeBy + "\"";

		String url = "../reportes/reporte_export.jsp?id=9&exportto=" + GestionInterface.RPT_EXP_EXCEL + param;
		response.sendRedirect( 	 url );
		return;	
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
		function openPDF(){
			var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=reportePresupuesto.jasper<%=paramPDF%>";
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}

		function openExcel(){
			var param = "&rptExcel="+ document.getElementById("hidEXCEL").value;
			/*
			Esteban Badillo. Fecha: 11/Sep/2009. Descripcion: Se agrega el paso del parametro "orden" para realizar la impresion
			condicional de columnas en la exportaciÃ³n del reporte a Excel.
			*/
			param += "&orden=" + <%="\"" + cOrddeBy + "\""%>;

			var url = "../reportes/reporte_export.jsp?id=9&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
		function generaAdecuacion(){
			document.adecuacion.submit();
		}
  </script>

  <body>
  	<input type="hidden" id="hidPDF" name="hidPDF" />
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
		<input type="hidden" id="cmdPdf"   name="cmdPdf"   value="PDF"       		onclick="javascript:openPDF();"   />
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel"        	onclick="javascript:openExcel();" />
		<%if(usuario!=null){
		/*SOLICITANTES_ADECUACIONES*/
		 %>
			<%if(usuario.getGrupos().containsKey("JEFATURA_ADECUACIONES") || usuario.getGrupos().containsKey("REVISORES_ADECUACIONES")|| usuario.getGrupos().containsKey("SOLICITANTES_ADECUACIONES")){ %>
				<%if("CALENDARIZADO".equals(TipoReporte)){ %>
					<!-- <input type="button"  id="GeneraAdecuacion" name="GeneraAdecuacion" value="Generar Adecuacion" onClick="generaAdecuacion();"/> -->
				<%} %>
			<%} %>
		<%} %>
	</div>
		<table width="100%" border="1" >
			<%=rpt_header%>
			<%=rpt_body%>
		</table>
		
		<form id="adecuacion" name="adecuacion" method="post" action="../gstnmngr/Adecuacion">
			<input type="hidden" name="accion" id="accion" value="2"/>
			<input type="hidden" name="ep" id="ep" value="<%=ep %>"/>
			<input type="hidden" name="EjercicioFiscal" id="EjercicioFiscal" value="<%=EjercicioFiscal %>"/>
			<input type="hidden" name="Cuenta" id="Cuenta" value="<%=Cuenta %>"/>
			<input type="hidden" name="UnidadEjecutora" id="UnidadEjecutora" value="<%=UnidadEjecutora %>"/>
			<input type="hidden" name="RamoEP" id="RamoEP" value="<%=RamoEP %>"/>
			<input type="hidden" name="UnidadResponsableEP" id="UnidadResponsableEP" value="<%=hUnidadResponsableEP %>"/>
			<input type="hidden" name="GrupoFuncional" id="GrupoFuncional" value="<%=GrupoFuncional %>"/>
			<input type="hidden" name="Funcion" id="Funcion" value="<%=Funcion %>"/>
			<input type="hidden" name="SubFuncion" id="SubFuncion" value="<%=SubFuncion %>"/>
			<input type="hidden" name="ProgramaGeneral" id="ProgramaGeneral" value="<%=ProgramaGeneral %>"/>
			<input type="hidden" name="ProgramaPresupuestario" id="ProgramaPresupuestario" value="<%=ProgramaPresupuestario %>"/>
			<input type="hidden" name="ActividadInstitucional" id="ActividadInstitucional" value="<%=ActividadInstitucional %>"/>
			<input type="hidden" name="Partida" id="Partida" value="<%=Partida %>"/>
			<input type="hidden" name="TipoGasto" id="TipoGasto" value="<%=TipoGasto %>"/>
			<input type="hidden" name="FuenteFinanciamiento" id="FuenteFinanciamiento" value="<%=FuenteFinanciamiento %>"/>
			<input type="hidden" name="EntidadFederativa" id="EntidadFederativa" value="<%=EntidadFederativa %>"/>
			<input type="hidden" name="Cartera" id="Cartera" value="<%=Cartera %>"/>
			<input type="hidden" name="UnidadNormativa" id="UnidadNormativa" value="<%=UnidadNormativa %>"/>
			
			<input type="hidden" name="ClaveCNA" id="ClaveCNA" value="<%=ClaveCNA %>"/>
			<input type="hidden" name="cOrddeBy" id="cOrddeBy" value="<%=cOrddeBy %>"/>
			<input type="hidden" name="cGroupBy" id="cGroupBy" value="<%=cGroupBy %>" />
			<input type="hidden" name="InfoRegMes" id="InfoRegMes" value="<%=InfoRegMes %>"/>
			<input type="hidden" name="TipoReporte" id="TipoReporte" value="<%=TipoReporte %>"/>
			<input type="hidden" name="Usuario" id="Usuario" value="<%=Usuario %>"/>
			
			<input type="hidden" name="Filtro" id="Filtro" value="<%=Filtro %>"/>
			<input type="hidden" name="nCtasPresup2" id="nCtasPresup2" value="<%=nCtasPresup2 %>"/>
			<input type="hidden" name="dCtasPresup2" id="dCtasPresup2" value="<%=dCtasPresup2 %>"/>
			<input type="hidden" name="Componentes" id="Componentes" value="<%=Componentes %>"/>
			<input type="hidden" name="tipoEP" id="tipoEP" value="<%=tipoEP %>"/>
			<input type="hidden" name="Resumen" id="Resumen" value="<%=Resumen %>"/>
			<input type="hidden" name="Capitulo" id="Capitulo" value="<%=Capitulo %>"/>
		</form>
  </body>
</html>
