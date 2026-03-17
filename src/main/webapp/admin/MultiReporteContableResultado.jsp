<%@page language="java" import="java.util.*"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%!
	public String getValor(String data) {
		return (data == null ? "" : data);
	}
%>
<%

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String rpt_body = "";
	
	String ep = getValor(request.getParameter("ihep"));
	String EjercicioFiscal = getValor(request.getParameter("ihEjercicioFiscal"));

//	String Cuenta = getValor(request.getParameter("Cuenta"));
	String Cuenta= "'"+getValor(request.getParameter("ihnCtasPresups")).replace(",","','")+"'";
/*
	StringBuffer cadena = new StringBuffer();
	String[] selections = request.getParameterValues("Cuenta");
	 if (selections != null) {for (int x=0;x<selections.length;x++){if (cadena.append(selections[x])!= null) {cadena =cadena.append(selections[x]);}}}
	String Cuenta = cadena.toString();
*/
	//String NomCuenta = getValor(request.getParameter("Cuenta"));
	//String login=  getValor(request.getParameter("login"));
	String UnidadEjecutora = getValor(request
			.getParameter("ihcUnidadEjecutora"));
	String hcUnidadEjecutora = getValor(request
			.getParameter("ihhcUnidadEjecutora"));
	String RamoEP = getValor(request.getParameter("ihRamoEP"));
	String UnidadResponsableEP = getValor(request
			.getParameter("ihUnidadResponsableEP"));
	String hUnidadResponsableEP = getValor(request
			.getParameter("ihhUnidadResponsableEP"));
	String GrupoFuncional = getValor(request
			.getParameter("ihGrupoFuncional"));
	String Funcion = getValor(request.getParameter("ihFuncion"));
	String SubFuncion = getValor(request.getParameter("ihSubFuncion"));
	String ProgramaGeneral = getValor(request
			.getParameter("ihProgramaGeneral"));
	String ProgramaPresupuestario = getValor(request
			.getParameter("ihProgramaPresupuestario"));
	String hProgramaPresupuestario = getValor(request
			.getParameter("ihhProgramaPresupuestario"));
	String ActividadInstitucional = getValor(request
			.getParameter("ihActividadInstitucional"));
	String Partida = getValor(request.getParameter("ihPartida"));
	String hPartida = getValor(request.getParameter("ihhPartida"));
	String TipoGasto = getValor(request.getParameter("ihTipoGasto"));
	String FuenteFinanciamiento = getValor(request
			.getParameter("ihFuenteFinanciamiento"));
	String EntidadFederativa = getValor(request
			.getParameter("ihEntidadFederativa"));
	String Cartera = getValor(request.getParameter("ihCartera"));
	String hCartera = getValor(request.getParameter("ihhCartera"));
	String UnidadNormativa = getValor(request
			.getParameter("ihUnidadNormativa"));
	String hUnidadNormativa = getValor(request
			.getParameter("ihhUnidadNormativa"));
	String ClaveCNA = getValor(request.getParameter("ihClaveCNA"));
	//String Mes=  getValor(request.getParameter("Mes"));
	String cOrddeBy = getValor(request.getParameter("ihcOrddeBy"));
	String cGroupBy = getValor(request.getParameter("ihcGroupBy"));
	String InfoRegMes = getValor(request.getParameter("ihInfoRegMes"));
	String TipoReporte = getValor(request.getParameter("ihTipoReporte"));
	String Componentes = getValor(request.getParameter("ihComponentes"));
	String Resumen = getValor(request.getParameter("ihResumen"));
	String Usuario = getValor(request.getParameter("ihUsuario"));
//	String Filtro = getValor(request.getParameter("Filtro"));
	String Filtro= request.getParameter("ihdCtasPresups");
	String cCentroContable= request.getParameter("ihcCentroContable");
		System.out.println("MultiReporteContableResultado.jsp"+cCentroContable);		

	if (Filtro == ""){
		Filtro="<tr class=\"alternateRow\"><td colspan=\"4\"><i>Presupuesto: Todo</i></td></tr>";
	} else {
		Filtro=	"<tr class=\"alternateRow\"><td colspan=\"4\"><i>"+"Presupuesto: "+Filtro+"</i></td></tr>";
	};

	//Filtro=Filtro+"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Tipo de Reporte: "+TipoReporte+"</i></td></tr>";

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

	String[] nCtasPresup  = request.getParameter("ihnCtasPresups").split(",\\s*");
	String[] dCtasPresup  = request.getParameter("ihdCtasPresups").split(",\\s*");
	String rpt_headerCtas= "<th>"+request.getParameter("ihdCtasPresups").replace(",","</th><th>")+"</th>";
	
/*
	for (String nCtasPresups: nCtasPresup)
	System.out.println(nCtasPresups);
	
	for (String dCtasPresups: dCtasPresup)
	System.out.println(dCtasPresups);
*/
	//PARA EL LAYOUT DE ADECUACIONES, QUE VIAJE COMPLETO
	String nCtasPresup2  = request.getParameter("ihnCtasPresups");
	String dCtasPresup2  = request.getParameter("ihdCtasPresups");
	//*********************************
	
	String rpt_header = "<thead align=\"center\" id=\"tHeader\">";
	rpt_header+="<th>RENGLON</th>";
	if(cGroupBy.equalsIgnoreCase("cCentroContable"))
		rpt_header+="<th>CENTRO CONTABLE</th>";
	rpt_header+=(TipoReporte.equals("GENERAL")?"":"<th>CUENTA</th>");
	rpt_header+=(TipoReporte.equals("GENERAL")?"":"<th>DESCRIPCION_CUENTA</th>");
	//rpt_header+=(cGroupBy.equals("cSubCuenta")?"<th>CODIGO</th>":"");
	rpt_header+=( cGroupBy.equals("cSubCuenta")?"<th>AUXILIAR</th>":"");
	rpt_header+=( cGroupBy.equals("cSubCuenta")?"<th>DESCRIPCION_AUXILIAR</th>":"");
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
	rpt_header+= (TipoReporte.equals("GENERAL")?"":"<th>SALDO_INICIAL</th><th>ENERO</th><th>FEBRERO</th><th>MARZO</th><th>ABRIL</th><th>MAYO</th><th>JUNIO</th><th>JULIO</th><th>AGOSTO</th><th>SEPTIEMBRE</th><th>OCTUBRE</th><th>NOVIEMBRE</th><th>DICIEMBRE</th><th>SALDO_FINAL</th>");
	rpt_header+="</thead>";
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
	paramPDF+=(cOrddeBy 			!=""&&cOrddeBy				!=null?" Order by " + cOrddeBy +"" :"");

	//tipo_acumulado = getValor(request.getParameter("tipo_acumulado"));
	ReporteBussinesLogic rbl = new ReporteBussinesLogic(GestionInterface.ATT_USER);
	//	if (ep=="" || EjercicioFiscal=="" || Cuenta=="" ||  UnidadEjecutora=="" || RamoEP=="" || UnidadResponsableEP=="" || GrupoFuncional=="" || Funcion=="" || SubFuncion=="" || ProgramaGeneral=="" || ProgramaPresupuestario=="" || ActividadInstitucional=="" || Partida=="" || TipoGasto=="" || FuenteFinanciamiento=="" || EntidadFederativa=="" || Cartera=="" || UnidadNormativa=="" || ClaveCNA=="")
	if (ep == "" || EjercicioFiscal == "" || UnidadEjecutora == ""
			|| RamoEP == "" || UnidadResponsableEP == ""
			|| GrupoFuncional == "" || Funcion == ""
			|| SubFuncion == "" || ProgramaGeneral == ""
			|| ProgramaPresupuestario == ""
			|| ActividadInstitucional == "" || Partida == ""
			|| TipoGasto == "" || FuenteFinanciamiento == ""
			|| EntidadFederativa == "" || Cartera == ""
			|| UnidadNormativa == "" || ClaveCNA == "")
		//		rpt_body = rbl.MultiReporteResultado(ep, EjercicioFiscal, Cuenta, UnidadEjecutora, RamoEP, UnidadResponsableEP, GrupoFuncional, Funcion, SubFuncion, ProgramaGeneral, ProgramaPresupuestario, ActividadInstitucional, Partida, TipoGasto, FuenteFinanciamiento, EntidadFederativa, Cartera, UnidadNormativa, ClaveCNA, cOrddeBy);
		System.out.println("MultiReporteContableResultado.jsp"+cCentroContable);		
		rpt_body = rbl.MultiReporteContableResultado(ep, EjercicioFiscal,
				Cuenta, UnidadEjecutora, RamoEP, UnidadResponsableEP,
				GrupoFuncional, Funcion, SubFuncion, ProgramaGeneral,
				ProgramaPresupuestario, ActividadInstitucional,
				Partida, TipoGasto, FuenteFinanciamiento,
				EntidadFederativa, Cartera, UnidadNormativa, ClaveCNA,
				cOrddeBy, cGroupBy, InfoRegMes, TipoReporte, Usuario, Filtro, 
				nCtasPresup, cCentroContable, Componentes, Resumen
				 );
	session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>


   
<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	

	
 <script language="javascript" charset="utf-8">
    	
    	
   
function openPDF(){

	var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=reportePresupuesto.jasper<%=paramPDF%>";
	var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");

}


function openExcel(){
			
	var numColumns = document.getElementById('reporteTabla').rows[0].cells.length;  
	
	
	document.getElementById("tabla0").value = "<%=rpt_body.replace("</td>","|").replace("<tr align=\"center\"><td>","").replace("</td></tr>","").replace("<td>","").replace("<td align=\"center\">","").replace("<tr class=\"AlternateRow\">","").replace("<tr class=\"NormalRow\">","").replace("</tr>","").replace("<td colspan=\"4\">","").replace("<i>","").replace("</i>","")%>";
	
	document.getElementById("colsxTabla").value=numColumns;
	document.getElementById("encabezadosTabla0").value ="<%=rpt_header.replace("<thead align=\"center\" id=\"tHeader\">","").replace("</th></thead>","").replace("</th>","|").replace("<th>","")%>";
	document.getElementById("titulosxTabla").value = "MultiReporte Contable";
	
	temp="String";
	for(var i=1;i<numColumns;i++)
	{
	temp+="|String"
	}
	
	
	document.getElementById("formatosTabla0").value =temp;
	document.getElementById("tTablas").value = "1";
	document.formulario.submit();
				
			
}
		
function generaAdecuacion(){

			document.adecuacion.submit();

}


  </script>
  
  <link href="../css/vistaPrevia.css" rel="stylesheet" type="text/css" />
  
  
  
  
  <title> Multireporte Contable</title>
  
 </head>
  <body >
  <div id="container">
  	<input type="hidden" id="hidPDF" name="hidPDF" />
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
		<input type="hidden" id="cmdPdf"   name="cmdPdf"   value="PDF"       		onclick="javascript:openPDF();"   />
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel"        	onclick="javascript:openExcel();" />
		<%if(usuario!=null){ %>
			<%if(usuario.getGrupos().containsKey("JEFATURA_ADECUACIONES") || usuario.getGrupos().containsKey("REVISORES_ADECUACIONES")){ %>
				<%if("CALENDARIZADO".equals(TipoReporte)){ %>
					<input type="button"  id="GeneraAdecuacion" name="GeneraAdecuacion" value="Generar Adecuacion" onClick="generaAdecuacion();"/>
				<%} %>
			<%} %>
		<%} %>
	</div>
		<table width="100%" border="0" id="reporteTabla" >
		
			<%=rpt_header%>
		<tbody>
			<%=rpt_body%>
		</tbody>
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
			
			<textarea name="Filtro" id="Filtro"  style="display: none;" ><%=Filtro %></textarea>
			<input type="hidden" name="nCtasPresup2" id="nCtasPresup2" value="<%=nCtasPresup2 %>"/>
			<input type="hidden" name="dCtasPresup2" id="dCtasPresup2" value="<%=dCtasPresup2 %>"/>
			<input type="hidden" name="Componentes" id="Componentes" value="<%=Componentes %>"/>
			<input type="hidden" name="Resumen" id="Resumen" value="<%=Resumen %>"/>
		</form>
		
	<form action="../reports/GeneraExcelTable" method="post" id="formulario" name="formulario">
    <input type="hidden" name="tabla0" id="tabla0" />
	<input type="hidden" name="colsxTabla" id="colsxTabla" />
	<input type="hidden" name="titulosxTabla" id="titulosxTabla" />
	<input type="hidden" name="encabezadosTabla0" id="encabezadosTabla0" />
	<input type="hidden" name="formatosTabla0" id="formatosTabla0" />
	<input type="hidden" name="tTablas" id="tTablas" />
	</form>
	</div>
  </body>
</html>
