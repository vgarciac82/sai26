<%@page import="com.syc.obrapublica.ObraPublicaContractBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
		ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(
			"jdbc/gestion");
	String accion = request.getParameter("accion");
	if ("buscar".equals(accion)) {

	}
	
			Empleado e = new Empleado();
			EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
					"jdbc/gestion");
			e.setClaveUsuario(usuario.getLogin());
			e = ebl.getEmpleado(e);
			EmpleadoArea ea = new EmpleadoArea();
			ea.setId(e.getClaveArea());
			ea = ebl.getEmpleadoArea(ea);
			String Meses[] = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO",
					"JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE",
					"NOVIEMBRE", "DICIEMBRE" };
			String Control[] = {"EjercicioFiscal","RamoEP",
			"UnidadResponsableEP",
			"GrupoFuncional","Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
			"ProgramaPresupuestario","Partida",
			"TipoGasto","FuenteFinanciamiento","EntidadFederativa","Cartera","UnidadNormativa",
			"cUnidadEjecutora"
			};
			Calendar calendar = Calendar.getInstance();
			java.util.Calendar fecha = java.util.Calendar.getInstance();
//			int mesDeAplicacion=calendar.get(Calendar.MONTH);
			int mesDeAplicacion=12;
	
			
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>B&uacute;squeda de Contratos</title>
		<script type="text/javascript" src="js/multiReporteObraPublica.js"></script>
		<script type="text/javascript" src="../admin/js/multiReporte.js"></script>
<script type="text/javascript" src="../js/datepickercontrol.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />
		<script type="text/javascript" src="js/validaciones.js"></script>
		<script type="text/javascript" src="js/MultiAnual.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>

	<script type="text/javascript">
		
	$(document).ready(function() {
		init2();
		

			$("#GenerarReporte2").attr("disabled", true);
			$("#Limpiar").attr("disabled", true);

			// Inicializaciones CRUD

			querySelectPost("catalogoEjercicioFiscalRead", "EjercicioFiscal");
			querySelectPost("catalogoRamoRead", "RamoEP");
			querySelectPost("catalogoGrupoFuncionalRead", "GrupoFuncional");
			querySelectPost("catalogoFuncionRead", "Funcion");
//			querySelectPost("catalogoUnidadResponsableRead", "cUnidadEjecutora");
//			querySelectPost("catalogoUnidadResponsableRead", "UnidadNormativa");
//			querySelectPost("catalogoUnidadResponsableRead", "UnidadResponsableEP");
			querySelectPost("catalogoSubFuncionRead", "SubFuncion");
			querySelectPost("catalogoProgramaGeneralRead", "ProgramaGeneral");
			querySelectPost("catalogoEntidadFederativaRead", "EntidadFederativa");
			querySelectPost("catalogoFuenteFinanciamientoRead", "FuenteFinanciamiento");
			querySelectPost("catalogoTipoGastoRead", "TipoGasto");
//			querySelectPost("catalogoCarteraRead", "Cartera");
//			querySelectPost("catalogoPartidaRead", "Partida");
//			querySelectPost("catalogoProgramaPresupuestarioRead", "ProgramaPresupuestario");
			querySelectPost("catalogoActividadInstitucionalRead", "ActividadInstitucional");
//			querySelectPost("catalogoClaveCNARead", "ClaveCNA");
//			querySelectPost("catalogoSubCuentaRead", "ep");


			insertactasPresupuestales();
			
			fnSeleccionaMes();

			$("#GenerarReporte2").attr("disabled", false);
			$("#Limpiar").attr("disabled", false);

			/*querySelectPost("EstadosRead", "cIdEntidadFederativaFiscal");
			querySelectPost("EstadosRead", "cIdEntidadFederativaActual");
			querySelectPost("MunicipiosFiscalRead", "cDelegacionFiscal");
			querySelectPost("MunicipiosActualRead", "cDelegacionActual");*/

//			queryFormPost("BeneficiarioRead");


				$( "#ep" )
				.change(function() {
				rellenaCampos();
				});

				$( "#GrupoFuncional" )
				.change(function() {
					querySelectPost("catalogoFuncionRead", "Funcion");
				});

				$( "#Funcion" )
				.change(function() {
					querySelectPost("catalogoSubFuncionRead", "SubFuncion");
				});

				$( "#RamoEP" )
				.change(function() {
//					querySelectPost("catalogoUnidadResponsableRead", "cUnidadEjecutora");
//					querySelectPost("catalogoUnidadResponsableRead", "UnidadNormativa");
//					querySelectPost("catalogoUnidadResponsableRead", "UnidadResponsableEP");
				});
				
/*							var todosCampos = "";
			var elements, i, curClass;  

    
    		//Get all children of the scope node
    		elements = document.getElementsByTagName('*');
    		for( i=0; i < elements.length; i++ ){
    			if (elements[i].id != "" && elements[i].id != null && elements[i].type != null && elements[i].type != "undefined" && elements[i].id != "undefined")
        			todosCampos = todosCampos +  elements[i].id + ";" + elements[i].type  + "\n";
 
    		}
    		$("#nadadenada").val(todosCampos); */
    		
	});
	
	function rellenaCampos() {
//	querySelectPost("catalogoClaveCNA2Read", "ClaveCNA");
	<%for (int i = 0; i < Control.length; i++) {%>
		$('#nOrden').val('<%=i+1%>');
		<%if ((i+1)!=3 && (i+1)!=9 && (i+1)!=10 && (i+1)!=14 && (i+1)!=15 && (i+1)!=16){%>
			querySelectPost("catalogoEPRead", "<%=Control[i]%>");
			$("#<%=Control[i]%>").attr("disabled", true);
		<%}else{%>
			$("#h<%=Control[i]%>").attr("disabled", true);
		<%}%>
	<%}%>
	//$("#hClaveCNA").attr("disabled", true);
}

function fnPrintTituloReporte2() {

/*				$.blockUI({
					message : "<p><img src='imagenes/wait24trans.gif' />&nbsp;&nbsp;Buscando. Por favor espere...</p>"
				});*/
	$("#FormContrato").action = "../ObraPublica/reportes";
	$("#FormContrato").submit();
//		$.unblockUI();
}
function fnPrintTituloReporte() {

	var principal = parent.parent.document;
	var asunto    = document.datawork;

	var ep= $.trim(document.getElementById('ep').value);
	var EjercicioFiscal = (ep==""?document.getElementById('EjercicioFiscal').value:"");

	var UnidadEjecutora 		= <%=ea.getAreaPadre()%>;
	var cUnidadEjecutora 		= $.trim(ep==""?document.getElementById('cUnidadEjecutora').value:"");
	var hcUnidadEjecutora 		= $.trim(ep==""?document.getElementById('hcUnidadEjecutora').value:"");
	var RamoEP					= $.trim(ep==""?document.getElementById('RamoEP').value:"");
	var UnidadResponsableEP		= $.trim(ep==""?document.getElementById('UnidadResponsableEP').value:"");
	var hUnidadResponsableEP		= $.trim(ep==""?document.getElementById('hUnidadResponsableEP').value:"");
	var GrupoFuncional			= $.trim(ep==""?document.getElementById('GrupoFuncional').value:"");
	var Funcion					= $.trim(ep==""?document.getElementById('Funcion').value:"");
	var SubFuncion				= $.trim(ep==""?document.getElementById('SubFuncion').value:"");
    var ProgramaGeneral			= $.trim(ep==""?document.getElementById('ProgramaGeneral').value:"");
	var ProgramaPresupuestario	= $.trim(ep==""?document.getElementById('ProgramaPresupuestario').value:"");
	var hProgramaPresupuestario	= $.trim(ep==""?document.getElementById('hProgramaPresupuestario').value:"");
	var ActividadInstitucional	= $.trim(ep==""?document.getElementById('ActividadInstitucional').value:"");
	var Partida					= $.trim(ep==""?document.getElementById('Partida').value:"");
	var hPartida					= $.trim(ep==""?document.getElementById('hPartida').value:"");
	var TipoGasto				= $.trim(ep==""?document.getElementById('TipoGasto').value:"");
	var FuenteFinanciamiento	= $.trim(ep==""?document.getElementById('FuenteFinanciamiento').value:"");
	var EntidadFederativa 		= $.trim(ep==""?document.getElementById('EntidadFederativa').value:"");
	var Cartera 				= $.trim(ep==""?document.getElementById('Cartera').value:"");
	var hCartera 				= $.trim(ep==""?document.getElementById('hCartera').value:"");
	var UnidadNormativa 		= $.trim(ep==""?document.getElementById('UnidadNormativa').value:"");
	var hUnidadNormativa 		= $.trim(ep==""?document.getElementById('hUnidadNormativa').value:"");
    var cOrddeBy				= document.getElementById('orderBy').value;
    var cGroupBy				= document.getElementById('groupBy').value;
    var InfoRegMes				= "mSaldo"+document.getElementById('InfoRegMes').value;
    var Componentes				= document.getElementById('Componentes').value;
    var Resumen					= document.getElementById('Resumen').value;
    var TipoReporte				= document.getElementById('TipoReporte').value;
    var Usuario					= $.trim("<%=usuario.getLogin()%>");

	var cadena="";
	var filtro="";
	var nCtasPresup = new Array();
	var dCtasPresup = new Array();
	var j=0;

	for(i=1;i<ctasPresupuestales.options.length+1;i++) {
        if ( document.getElementById(ctasPresupuestales.options[i-1].value.replace(" ","").toLowerCase()).checked){
			cadena= cadena == ""?  "'" +  ctasPresupuestales.options[i-1].text + "'" : cadena + ", " + "'" +  ctasPresupuestales.options[i-1].text + "'";
			filtro= filtro == ""?  ctasPresupuestales.options[i-1].value.toLowerCase() : filtro + ", "+ctasPresupuestales.options[i-1].value.toLowerCase();
			nCtasPresup[j]=ctasPresupuestales.options[i-1].text; 
			dCtasPresup[j]=ctasPresupuestales.options[i-1].value;
			j=j+1;
		}
	}
	if (cadena==""){
		for(i=1;i<ctasPresupuestales.options.length+1;i++) {
			filtro= filtro == ""?  ctasPresupuestales.options[i-1].value.toLowerCase() : filtro + ", "+ctasPresupuestales.options[i-1].value.toLowerCase();
			nCtasPresup[i-1]=ctasPresupuestales.options[i-1].text; 
			dCtasPresup[i-1]=ctasPresupuestales.options[i-1].value;
		}
	} 
	Cuenta    		= cadena;

	var param = "id=<%=request.getParameter("id")%>"
		 + "&ep=" + ep
		 + "&EjercicioFiscal=" + EjercicioFiscal
		 + "&cUnidadEjecutora=" + cUnidadEjecutora
		 + "&hcUnidadEjecutora=" + hcUnidadEjecutora
		 + "&RamoEP=" + RamoEP
		 + "&UnidadResponsableEP=" + UnidadResponsableEP
		 + "&hUnidadResponsableEP=" + hUnidadResponsableEP
		 + "&GrupoFuncional=" + GrupoFuncional
		 + "&Funcion=" + Funcion
		 + "&SubFuncion=" + SubFuncion
		 + "&ProgramaGeneral=" + ProgramaGeneral
		 + "&ProgramaPresupuestario=" + ProgramaPresupuestario
		 + "&hProgramaPresupuestario=" + hProgramaPresupuestario
		 + "&ActividadInstitucional=" + ActividadInstitucional
		 + "&Partida=" + Partida
		 + "&hPartida=" + hPartida
		 + "&TipoGasto=" + TipoGasto
		 + "&FuenteFinanciamiento=" + FuenteFinanciamiento
		 + "&EntidadFederativa=" + EntidadFederativa
		 + "&Cartera=" + Cartera
		 + "&UnidadNormativa=" + UnidadNormativa
		 + "&hUnidadNormativa=" + hUnidadNormativa
		 + "&cOrddeBy="+ cOrddeBy
		 + "&cGroupBy="+ cGroupBy
		 + "&InfoRegMes="+ InfoRegMes
		 + "&TipoReporte="+ TipoReporte
		 + "&Componentes="+ Componentes
		 + "&Resumen="+ Resumen
		 + "&Usuario="+ Usuario
		 + "&nCtasPresups="+ nCtasPresup
		 + "&dCtasPresups="+ dCtasPresup
		 + "&rn=ReporteAuditoria.jasper";


	if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" )
	{
		alert("Capturar algun dato");
		return false;
	} else {

		$("#GenerarReporte2").attr("disabled", true);
		$("#Limpiar").attr("disabled", true);
		window.open('../admin/MultiReporteResultado.jsp?'+ param, 'MultiReporteResultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
		$("#GenerarReporte2").attr("disabled", false);
		$("#Limpiar").attr("disabled", false);

/*	 	     document.FormContrato.action="../reports/ReportetoExcel";
    popup=window.open('', 'formpopup', 'width=1000,height=600,resizeable,scrollbars');
    document.formulario.target = 'formpopup';
    document.FormContrato.submit();*/
		return true;
	}
}
</script>
	</head>
	<body id="dt_example" background="../imagenes/steel_BG.gif" scroll="yes" style="width: 97%">
	<!-- <form method="get" action="../ObraPublica/reportes" id="frmOP"> -->
	<form id="FormContrato" name="FormContrato" method="post" action="../ObraPublica/reportes" target="_blank">
	<div class="Contenido" style="height: 10%">
		<table class="TituloRutaCA"  height="10">
			<tr>
				<td>
					<img src="../imagenes/iconos/reportes.png" alt="" width="16"
						height="16">
					<font color="#FFFFFF">
					<strong>
						MultiReporte Obra Publica.
					</strong>
					</font>
				</td>
				<td>
					&nbsp;
			</tr>
		</table>	</div>
		<table align="center"  height="10">

	</table>				<input type="hidden" value="" id="folioSAI" name="folioSAI">
			<input type="hidden" value="PAGO_PASIVO" id="accion" name="accion">
			<input type="hidden" value="" id="OperacionActual" name="OperacionActual">
			<input type="hidden" value="" id="cU_UE" name="cU_UE">
			<input type="hidden" id="cDocumento" value="CONTRATOOBRA" name="cDocumento" size="20" />
			<input type="hidden" id="bTipoReporte" name="bTipoReporte" value="Multi Reporte Obra"/>


					<div id="tabs-8">
<!-- <iframe id="fileDetalle" src="../admin/MultiReporte.jsp" width="110%"  height="700px"></iframe>-->
<table border="0" width="1028" height="10">
			<tr>
				<td width="20%" height="10"><input type="hidden" id="UnidadEjecutora" name="UnidadEjecutora" value=""/></td>
				<td style="display: none;"><select id="ctasPresupuestales" name="ctasPresupuestales" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td width="20%" height="10">
				<input type="hidden" id="nOrden" name="nOrden" value=""/>
				</td>
<!--				<td width="10%" style="display: none;"><select id="nOrden" name="nOrden">-->
<!--					<option value="Z:">A</option>-->
<!--					<option value="Y:">B</option>-->
<!--					<option value="X:">C</option>-->
<!--					<option value="xx" selected>--</option>-->
<!--				</select></td>		-->
				<td align="left" height="10" width="40%"></td>
			</tr>
			<tr>
<!--				<td align="right" >Código:</td>
					<td><input id="hClaveCNA" name="hClaveCNA" type="text"   value="" size="5" maxlength="5" class="AyudaSyC  autoCompletaSyC"/>
					<input id="ClaveCNA" name="ClaveCNA" type="hidden"   value="" size="5" maxlength="5" class=""/></td>-->
				<td align="right">Estructura Programática:</td>
					<td>
<!--
					<input id="hep" name="hep" type="text"   value="" size="64" maxlength="64" class="AyudaSyC  autoCompletaSyC"/>
					<input id="ep" name="ep" type="hidden"   value="" size="4" maxlength="64" class=""/>
-->
<!--					<input id="ep" name="ep" type="text"   value="" size="64" maxlength="64" class=""/>-->

<!-- 					<input id="hep" name="hep" type="text"   value="" size="64" maxlength="64" class=""/> -->
					<input id="ep" name="ep" type="text"   value="" size="64" maxlength="64" class=""/>
					</td>
					<td>
					<input type="button" value="..." onclick="Grid()"  />
					</td>
			</tr>
				<tr>
				<td align="right" >Ejercicio Fiscal:</td>
<!--				<td><input id="EjercicioFiscal"     name="EjercicioFiscal"   value="2012" size="4"  maxlength="5"/></td>-->
				<td><select id="EjercicioFiscal" name="EjercicioFiscal" value="">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Programa Presupuestario:</td>
<!--				<td><input id="ProgramaPresupuestario"  name="ProgramaPresupuestario"  value="" size="4"  maxlength="5"/></td>-->
					<td><input id="hProgramaPresupuestario" name="hProgramaPresupuestario" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
<!--				<td><select id="ProgramaPresupuestario" name="ProgramaPresupuestario">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
			</tr>
			<tr>
				<td align="right">Ramo:</td>
<!--				<td><input id="RamoEP"     name="RamoEP"     value="" size="3"  maxlength="5"/></td>-->
<!--					<td><input id="RamoEP" name="RamoEP" type="text"   value="" size="3" maxlength="5" class="AyudaSyC  autoCompletaSyC"/></td>-->
				<td><select id="RamoEP" name="RamoEP">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right" >Partida:</td>
<!--				<td><input id="Partida"  name="Partida"  value="" size="5"  maxlength="7"/></td>-->
					<td><input id="hPartida" name="hPartida" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="Partida" name="Partida" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
<!--				<td><select id="Partida" name="Partida" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
			</tr>
			<tr>
<!--  				<td align="right">Centro Contable:</td>-->
<!--				<td><input id="UnidadResponsableEP"     name="UnidadResponsableEP"     value="" size="4"  maxlength="5"/></td>-->
<!--  					<td><input id="hUnidadResponsableEP" name="hUnidadResponsableEP" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="UnidadResponsableEP" name="UnidadResponsableEP" type="hidden"   value="" size="5" maxlength="5" class=""/></td>-->
<!-- 				<td align="right">Unidad Responsable:</td> -->
<!--				<td><input id="UnidadResponsableEP"     name="UnidadResponsableEP"     value="" size="4"  maxlength="5"/></td>-->
<!--  					<td><input id="hUnidadResponsableEP" name="hUnidadResponsableEP" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="UnidadResponsableEP" name="UnidadResponsableEP" type="hidden"   value="" size="5" maxlength="5" class=""/></td>-->
<!--				<td><select id="UnidadResponsableEP" name="UnidadResponsableEP" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
				<td align="right">Tipo Gasto:</td>
<!--				<td><input id="TipoGasto"  name="TipoGasto"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="TipoGasto" name="TipoGasto">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
			</tr>
			<tr>
				<td align="right">Grupo Funcional:</td>
<!--				<td><input id="GrupoFuncional"  name="GrupoFuncional"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="GrupoFuncional" name="GrupoFuncional" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Fuente Financiamiento:</td>
<!--				<td><input id="FuenteFinanciamiento"  name="FuenteFinanciamiento"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="FuenteFinanciamiento" name="FuenteFinanciamiento">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
			</tr>
			<tr>
				<td align="right">Funcion:</td>
<!--				<td><input id="Funcion"  name="Funcion"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="Funcion" name="Funcion">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Entidad Federativa</td>
<!--				<td><input id="EntidadFederativa"  name="EntidadFederativa"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="EntidadFederativa" name="EntidadFederativa">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				</tr>
			<tr>
				<td align="right">SubFuncion:</td>
<!--				<td><input id="SubFuncion"  name="SubFuncion"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="SubFuncion" name="SubFuncion">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Cartera:</td>
<!--				<td><input id="Cartera"  name="Cartera"  value="" size="12"  maxlength="15"/></td>-->
					<td><input id="hCartera" name="hCartera" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="Cartera" name="Cartera" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
<!--				<td><select id="Cartera" name="Cartera">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
			</tr>
			<tr>
				<td align="right">Programa General:</td>
<!--				<td><input id="ProgramaGeneral"  name="ProgramaGeneral"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="ProgramaGeneral" name="ProgramaGeneral">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Unidad Normativa:</td>
<td><input id="hUnidadNormativa" name="hUnidadNormativa" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
							<input id="UnidadNormativa" name="UnidadNormativa" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
							<!--				<td><input id="UnidadNormativa"  name="UnidadNormativa"  value="" size="3"  maxlength="5"/></td>-->
<!--					<td><input id="hUnidadNormativa" name="hUnidadNormativa" type="text"   value="" size="40" maxlength="50" class="AyudaSyC "/>
					<input id="UnidadNormativa" name="UnidadNormativa" type="hidden"   value="" size="5" maxlength="5" class=""/></td>-->
<!--				<td><select id="UnidadNormativa" name="UnidadNormativa" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
			</tr>
			<tr>
			<td align="right">Actividad Institucional:</td>
<!--				<td><input id="ActividadInstitucional"  name="ActividadInstitucional"  value="" size="4"  maxlength="5"/></td>-->
				<td><select id="ActividadInstitucional" name="ActividadInstitucional" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Unidad Ejecutora:</td>
<td><input  onchange="cargaAreas();" id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden"   value="" size="5" maxlength="5" class=""  onchange="cargaAreas();"/></td>
<!--				<td><input id="cUnidadEjecutora"  name="cUnidadEjecutora"  value="" size="2"  maxlength="5"/></td>-->
<!-- 					<td><input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden"   value="" size="5" maxlength="5" class=""/></td> -->
<!--				<td><select id="cUnidadEjecutora" name="cUnidadEjecutora" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select>
				</td>-->
				</tr>
	</table>
			<table border="0" width="1028" height="10">
				<tr>
				<td align="right" width="15%">Información registrada al mes:</td>
				<td>
					<select name="InfoRegMes" id="InfoRegMes" onchange=""  value="">
						<option value="Z:">AAAAAAAAA</option>
						<option value="Y:">BBBBBBBBB</option>
						<option value="X:">CCCCCCCCC</option>
						<option value="xx" selected>--</option>
					</select>
				</td>

				<td align="right" width="10%">Ordenado por:</td>
				<td><select name="orderBy" id="orderBy" onchange="">
					<option value="dCuenta">Cuenta</option>
<!--					<option value="nClaveCNA">Código</option>-->
					<option value="cSubCuenta" selected>Estructura Programática</option>
					<option value="aEjercicioFiscal_1">Ejercicio Fiscal</option>
					<option value="cRamo_2">Ramo</option>
					<option value="cUnidadResponsable_3">Unidad Responsable</option>
					<option value="cGrupoFuncional_4">Grupo Funcional</option>
					<option value="cFuncion_5">Funcion</option>
					<option value="cSubFuncion_6">Sub Funcion</option>
					<option value="cProgramaGeneral_7">Programa General</option>
					<option value="cProgramaPresupuestario_9">Programa Presupuestario</option>
					<option value="cActividadInstitucional_8">Actividad Institucional</option>
					<option value="cPartida_10">Partida</option>
					<option value="cTipoGasto_11">Tipo Gasto</option>
					<option value="cFuenteFinanciamiento_12">Fuente Financiamiento</option>
					<option value="cEntidadFederativa_13">Entidad Federativa</option>
					<option value="cCartera_14">Cartera</option>
					<option value="cUnidadResponsable_15">Unidad Ejecutora</option>
					<option value="cUnidadResponsable_16">Unidad Normativa</option>
					</select></td>
				<td align="right" width="10%">Agrupado por:</td>
				<td><select name="groupBy" id="groupBy" onchange="">
<!--					<option value="nClaveCNA">Código</option>-->
					<option value="cSubCuenta" selected>Estructura Programática</option>
					<option value="aEjercicioFiscal_1">Ejercicio Fiscal</option>
					<option value="cRamo_2">Ramo</option>
					<option value="cUnidadResponsable_3">Unidad Responsable</option>
					<option value="cGrupoFuncional_4">Grupo Funcional</option>
					<option value="cFuncion_5">Funcion</option>
					<option value="cSubFuncion_6">Sub Funcion</option>
					<option value="cProgramaGeneral_7">Programa General</option>
					<option value="cProgramaPresupuestario_9">Programa Presupuestario</option>
					<option value="cActividadInstitucional_8">Actividad Institucional</option>
					<option value="cPartida_10">Partida</option>
					<option value="cTipoGasto_11">Tipo Gasto</option>
					<option value="cFuenteFinanciamiento_12">Fuente Financiamiento</option>
					<option value="cEntidadFederativa_13">Entidad Federativa</option>
					<option value="cCartera_14">Cartera</option>
					<option value="cUnidadResponsable_15">Unidad Ejecutora</option>
					<option value="cUnidadResponsable_16">Unidad Normativa</option>
					</select></td>

			</tr>
			<tr>
				<td align="right" width="10%">Componentes EP:</td>
				<td><select name="Componentes" id="Componentes" onchange="">
					<option value="INCLUIR" >INCLUIR</option>
					<option value="DESCARTAR" selected>DESCARTAR</option>
					</select></td>
				<td align="right" width="10%">Resumen Criterios:</td>
				<td><select name="Resumen" id="Resumen" onchange="">
					<option value="INCLUIR" >INCLUIR</option>
					<option value="DESCARTAR" selected>DESCARTAR</option>
					</select>
					</td>
				<td align="right" width="10%">Tipo de Reporte:</td>
				<td><select name="TipoReporte" id="TipoReporte" onchange="">
					<option value="GENERAL" selected>GENERAL</option>
					<option value="CALENDARIZADO">CALENDARIZADO</option>
					</select></td>
					
			</tr>
			<tr>
														<td align="right" nowrap="nowrap" style="padding: 5px">
												Tipo de procedimiento:
											</td>
											<td>
												<input type="text" id="cIdTipoAdjudica" name="cIdTipoAdjudica" size="20" maxlength="20">
											</td>
												<td colspan="2">
								<input type="checkbox" id="chkPasivo" name="chkPasivo" value="chkPasivo">Pago Pasivo
							</td>
							<td nowrap>
								No. Contrato:
							</td>
							<td colspan="2">
								<input title="Número de Contrato el cual deberá capturarse conforme a la Normativa del Cómite de Obra Pública (Normativa-Ejecutora-Estado-Año-Area-Número de Contrato-Tipo de Recursos-Adjudicación)" type="text" id="cCveContrato" name="cCveContrato"
									size="40" maxlength="40" />
							</td>
			</tr>
			<tr>
								<td nowrap="nowrap" colspan="7"  align="left">
									R.F.C: 
								
									<input type="text" id="cIDRFC" name="cIDRFC" size="15"
										maxlength="15" readonly="readonly"
										class="AyudaSyC  obligatorio" />
								
									&nbsp;<input type="text" id="cnombre" name="cnombre" size="61"
										maxlength="100" readonly="readonly" />
								</td>
			</tr>
			
	</table>
			<span id="zonaInsercion"> <a>Llamo Función insertactasPresupuestales</a> </span>
		<table align="center"  height="10">
		<tr>
			<td align="center" colspan="6">
				<input type="button" id="GenerarReporte2" name="GenerarReporte2" value="Generar Reporte" onClick="return fnPrintTituloReporte2();">
		<!-- 		<input type="button"  id="Limpiar" name="Limpiar" value="Limpiar" onClick="llamaReset();"/> -->
				<input type="reset"  id="Limpiar" name="Limpiar" value="Limpiar" />
			</td>
		</tr>
	</table>

					</div>
<input type="hidden" name="rt" id="rt" value="MULTI_REPORTE" />
		</form>
	</body>
</html>