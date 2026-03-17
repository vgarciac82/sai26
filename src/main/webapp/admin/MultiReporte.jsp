<%@page import="java.util.Iterator"%>
<%@page import="com.syc.gestion.core.UnidadEjecutora"%>
<%@page import="java.util.List"%>
<%@page import="com.syc.gestion.core.UsuarioVistaBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.core.UsuarioGrupo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%
			Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
			
			String Meses[] = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE","NOVIEMBRE", "DICIEMBRE" };
			String Control[] = {"EjercicioFiscal","RamoEP", "UnidadResponsableEP", "GrupoFuncional","Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
			"ProgramaPresupuestario","Partida",	"TipoGasto","FuenteFinanciamiento","EntidadFederativa","Cartera","UnidadNormativa","cUnidadEjecutora"};
			Calendar calendar = Calendar.getInstance();
			java.util.Calendar fecha = java.util.Calendar.getInstance();
			int mesDeAplicacion=12;
			
			AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic("jdbc/gestion");
			boolean esUsuarioAdecuaciones = adbl.esUsuarioAdecuacion(usuario);
			List<UnidadEjecutora> vistas = null;
			
			if( usuario.getGrupo("JEFATURA_ADECUACIONES") == null ){
				UsuarioVistaBusinessLogic uvbl = new UsuarioVistaBusinessLogic("jdbc/gestion");
				vistas = uvbl.getVistasUsuario(usuario.getLogin(), "PRESUPUESTO");			
			}
			
			
			
		%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MultiReporte</title>
	<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>	
	
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>	
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>     
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>


<script type="text/javascript">

	function limpiarSesion()
	{
		window.location.href="MultiReporte.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
	}

	function hcUnidadEjecutoraChange(){
		$("#cUnidadEjecutora").val( $("#hcUnidadEjecutora").val() );
	}
	function Grid()
	{
		window.open('MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=500px');
	    if ($.trim(document.getElementById("ep").value)!=""){
			//window.alert($.trim(document.getElementById("ep").value));
			rellenaCampos();
		}
		//window.location.href="MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>";
	 	return false;
	}

	function insertactasPresupuestales()
	{
 		zona2 = document.getElementById('zonaInsercion') ;
		querySelectPost("catalogoCtasPresupuestalesRead","ctasPresupuestales", {async: false });
		ctasPresupuestales = document.getElementById("ctasPresupuestales");
		zona = "<table border=\"0\" width=\"85%\" style=\"overflow: scroll;\" height=\"10\">";
		zona+= "<tr>";
		zona+="<td align=\"right\">Presupuesto:&nbsp;&nbsp;&nbsp;</td>";
		for(i=0;i<ctasPresupuestales.options.length;i++) {
			<%if(esUsuarioAdecuaciones){%>
				zona+="<td><input type=\"checkbox\" class=\"form-check-input\" id=\""+ctasPresupuestales.options[i].value.replace(" ","").toLowerCase()+"\" name=\"CuentasPresup\" value=\""+ctasPresupuestales.options[i].text+"\"/>"+ (  (  ctasPresupuestales.options[i].text=="81106" || ctasPresupuestales.options[i].text=="81105" || ctasPresupuestales.options[i].text=="82102" || ctasPresupuestales.options[i].text=="81103" || ctasPresupuestales.options[i].text=="82106" || ctasPresupuestales.options[i].text=="81102"|| ctasPresupuestales.options[i].text=="81104"|| ctasPresupuestales.options[i].text=="82103") ?"<b>":"") + toTitleCase(ctasPresupuestales.options[i].value) + (  ( ctasPresupuestales.options[i].text=="81106" || ctasPresupuestales.options[i].text=="81105" || ctasPresupuestales.options[i].text=="82102" || ctasPresupuestales.options[i].text=="81103" || ctasPresupuestales.options[i].text=="82106" || ctasPresupuestales.options[i].text=="81102"|| ctasPresupuestales.options[i].text=="81104"|| ctasPresupuestales.options[i].text=="82103")?"</b>":"" ) + "<br></td>";
			<%}else{%>
				zona+="<td><input type=\"checkbox\" class=\"form-check-input\" id=\""+ctasPresupuestales.options[i].value.replace(" ","").toLowerCase()+"\" name=\"CuentasPresup\" value=\""+ctasPresupuestales.options[i].text+"\"/>" + toTitleCase(ctasPresupuestales.options[i].value) + "<br></td>";
			<%}%>	
			if (((i+1) % 5)==0){
				zona+="</tr>";
				zona+="<tr><td></td>";
			} 
		}		
		zona+="</tr>";
		zona+="</table>";
		zona2.innerHTML=zona;
	}

	function fnSeleccionaMes()
	{
		querySelectPost("catalogoMesesRead","InfoRegMes", {async: false });
		InfoRegMes = document.getElementById("InfoRegMes") ;
		$("#InfoRegMes").val( InfoRegMes.options.length ).attr('selected',true);
	}

	function fnPrintTituloReporte(esExcelDirecto) {

			var principal = parent.parent.document;
			var asunto    = document.datawork;

			var ep= $.trim(document.getElementById('ep').value);
			var EjercicioFiscal = (ep==""?document.getElementById('EjercicioFiscal').value:"");

			var UnidadEjecutora 		= "<%=usuario.getU_UR(  )%>";
			var cUnidadEjecutora 		= $.trim(ep==""?document.getElementById('cUnidadEjecutora').value:"");
			var hcUnidadEjecutora 		= $.trim(ep==""?document.getElementById('hcUnidadEjecutora').value:"");
			var RamoEP					= $.trim(ep==""?document.getElementById('RamoEP').value:"");
			var UnidadResponsableEP		= $.trim(ep==""?document.getElementById('UnidadResponsableEP').value:"");
			var hUnidadResponsableEP	= $.trim(ep==""?document.getElementById('hUnidadResponsableEP').value:"");
			var GrupoFuncional			= $.trim(ep==""?document.getElementById('GrupoFuncional').value:"");
			var Funcion					= $.trim(ep==""?document.getElementById('Funcion').value:"");
			var SubFuncion				= $.trim(ep==""?document.getElementById('SubFuncion').value:"");
            var ProgramaGeneral			= $.trim(ep==""?document.getElementById('ProgramaGeneral').value:"");
			var ProgramaPresupuestario	= $.trim(ep==""?document.getElementById('ProgramaPresupuestario').value:"");
			var hProgramaPresupuestario	= $.trim(ep==""?document.getElementById('hProgramaPresupuestario').value:"");
			var ActividadInstitucional	= $.trim(ep==""?document.getElementById('ActividadInstitucional').value:"");
			var Partida					= $.trim(ep==""?document.getElementById('Partida').value:"");
			var hPartida				= $.trim(ep==""?document.getElementById('hPartida').value:"");
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
			var directExcel             = (esExcelDirecto?esExcelDirecto:"N");
			var tipoEP					= document.getElementById('tipoEP').value;
			var Capitulo				= document.getElementById('Capitulo').value;
			
			var cadena="";
			var filtro="";
//			var rpt_headerCtas="";
			var nCtasPresup = new Array();
			var dCtasPresup = new Array();
//			var CuentasPresup=document.getElementByName('CuentasPresup').value;
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
//				 + "&Cuenta=" + Cuenta
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
//				 + "&ClaveCNA=" + ClaveCNA
				 + "&cOrddeBy="+ cOrddeBy
				 + "&cGroupBy="+ cGroupBy
				 + "&InfoRegMes="+ InfoRegMes
				 + "&TipoReporte="+ TipoReporte
				 + "&Componentes="+ Componentes
				 + "&Resumen="+ Resumen
				 + "&Usuario="+ Usuario
//				 + "&Filtro="+ filtro
//				 + "&rpt_headerCtas="+ rpt_headerCtas
				 + "&nCtasPresups="+ nCtasPresup
				 + "&dCtasPresups="+ dCtasPresup
//				 + "&CuentasPresup="+ CuentasPresup
				 + "&rn=ReporteAuditoria.jasper"
				 + "&EXCEL_DIRECT="  + directExcel
				 + "&tipoEP=" + tipoEP
				 + "&Capitulo=" + Capitulo;


			if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" & Capitulo=="" )
			{
				alert("Capturar algun dato");
				return false;
			} else {

				$("#GenerarReporte").attr("disabled", true);
				$("#Limpiar").attr("disabled", true);
				window.open('MultiReporteResultado.jsp?'+ param, 'MultiReporteResultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
				$("#GenerarReporte").attr("disabled", false);
				$("#Limpiar").attr("disabled", false);
				return true;
			}


		}

		$(document).ready(
			function()
			{
				$("input.AyudaSyC").subIniciaDlg();
				$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

			$("#GenerarReporte").attr("disabled", true);
			$("#Limpiar").attr("disabled", true);

			querySelectPost("catalogoEjercicioFiscalRead", "EjercicioFiscal");
			querySelectPost("catalogoRamoRead", "RamoEP");
			querySelectPost("catalogoGrupoFuncionalRead", "GrupoFuncional");
			querySelectPost("catalogoFuncionRead", "Funcion");
			querySelectPost("catalogoSubFuncionRead", "SubFuncion");
			querySelectPost("catalogoProgramaGeneralRead", "ProgramaGeneral");
			querySelectPost("catalogoEntidadFederativaRead", "EntidadFederativa");
			querySelectPost("catalogoFuenteFinanciamientoRead", "FuenteFinanciamiento");
			querySelectPost("catalogoTipoGastoRead", "TipoGasto");
			querySelectPost("catalogoActividadInstitucionalRead", "ActividadInstitucional");

			insertactasPresupuestales();
			
			fnSeleccionaMes();

			$("#GenerarReporte").attr("disabled", false);
			$("#Limpiar").attr("disabled", false);

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
				
			
				

			});


		function capitaliseFirstLetter(string)
		{
		    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
		}
		
		function toTitleCase(str) {
		    return str.toLowerCase().replace(/(?:^|\s)\w/g, function(match) {
		        return match.toUpperCase();
		    });
        }
		
		function formSubmited() {
                alert("Beneficiario enviado!");
        }
            
		function rellenaCampos() {
//					querySelectPost("catalogoClaveCNA2Read", "ClaveCNA");
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
</script>
</head>
<br/>
<body>
	<form id="form1" name="form1" method="post" target="formulario" action="./MultiReporte.jsp?select=u_login">
		<div class="container" style="height: 80%; " >
			<div class="card-header"> 
				<div class="input-group">
					<img src="../imagenes/iconos/reportes.png" alt="" width="16">&nbsp;&nbsp;&nbsp; 
					<h3> MultiReporte </h3>
				</div> 
			</div>
			<hr class="mt-3"/>				
		
			<table border="0" width="1028" >
				<tr>
					<td width="20%" height="10">
						<input type="hidden" id="UnidadEjecutora" name="UnidadEjecutora" value=""/>
					</td>
					<td style="display: none;">
						<select id="ctasPresupuestales" name="ctasPresupuestales" >
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select></td>
					<td width="20%" height="10">
						<input type="hidden" id="nOrden" name="nOrden" value=""/>
					</td>
				</tr>
			</table>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="ep" class="form-label"> Estructura Programática: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<div class="input-group">
						<input id="ep" name="ep" type="text" value="" class="form-control form-control-sm"/>
						<input type="button" value="..." onclick="Grid()" onblur="rellenaCampos();" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="Capitulo" class="form-label"> Capitulo: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<select id="Capitulo" name="Capitulo" class="form-select form-select-sm">
						<option value="" selected></option>
						<option value="1">1000 SERVICIOS PERSONALES</option>
						<option value="2">2000 MATERIALES Y SUMINISTROS</option>
						<option value="3">3000 SERVICIOS GENERALES</option>
						<option value="4">4000 TRANSFERENCIAS, ASIGNACIONES, SUBSIDIOS Y OTRAS AYUDAS</option>
						<option value="5">5000 BIENES MUEBLES, INMUEBLES E INTANGIBLES</option>
						<option value="6">6000 INVERSION PUBLICA</option>
						<option value="7">7000 INVERSIONES FINANCIERAS Y OTRAS PROVISIONES</option>
						<option value="8">8000 PARTICIPACIONES Y APORTACIONES</option>
						<option value="9">9000 DEUDA PUBLICA</option>
					</select>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="EjercicioFiscal" class="form-label"> Ejercicio Fiscal: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="EjercicioFiscal" name="EjercicioFiscal" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="EjercicioFiscal" class="form-label"> Programa Presupuestario: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<div class="input-group">
						<input id="hProgramaPresupuestario" name="hProgramaPresupuestario" type="text" value="" class="AyudaSyC autoCompletaSyC form-control form-control-sm"/>
						<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden" value="" class="form-control form-control-sm"/>
					</div>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="RamoEP" class="form-label"> Ramo: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">					
					<select id="RamoEP" name="RamoEP" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="EjercicioFiscal" class="form-label"> Partida: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<div class="input-group">
						<input id="hPartida" name="hPartida" type="text" value="" class="AyudaSyC autoCompletaSyC form-control form-control-sm"/>
						<input id="Partida" name="Partida" type="hidden" value="" class="form-control form-control-sm"/>
					</div>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="hUnidadResponsableEP" class="form-label"> Unidad Responsable: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">					
						<input id="hUnidadResponsableEP" name="hUnidadResponsableEP" type="text" value="RHQ CONAFOR" class="form-control form-control-sm" readonly/>
						<input id="UnidadResponsableEP" name="UnidadResponsableEP" type="hidden" value="RHQ" class="form-control form-control-sm"/>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="TipoGasto" class="form-label"> Tipo Gasto: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<select id="TipoGasto" name="TipoGasto" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="GrupoFuncional" class="form-label"> Grupo Funcional: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="GrupoFuncional" name="GrupoFuncional" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="FuenteFinanciamiento" class="form-label"> Fuente Financiamiento: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<select id="FuenteFinanciamiento" name="FuenteFinanciamiento" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="Funcion" class="form-label"> Funcion: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="Funcion" name="Funcion" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="EntidadFederativa" class="form-label"> Entidad Federativa: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<select id="EntidadFederativa" name="EntidadFederativa" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="SubFuncion" class="form-label"> SubFuncion: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="SubFuncion" name="SubFuncion" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="hCartera" class="form-label"> Cartera: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<div class="input-group">
						<input id="hCartera" name="hCartera" type="text" class="AyudaSyC  autoCompletaSyC form-control form-control-sm"/>
						<input id="Cartera" name="Cartera" type="hidden" class="form-control form-control-sm"/>
					</div>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="ProgramaGeneral" class="form-label"> Programa General: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="ProgramaGeneral" name="ProgramaGeneral" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="hUnidadNormativa" class="form-label"> Unidad Normativa: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<div class="input-group">
						<input id="hUnidadNormativa" name="hUnidadNormativa" type="text" value="" class="AyudaSyC autoCompletaSyC form-control form-control-sm"/>
						<input id="UnidadNormativa" name="UnidadNormativa" type="hidden" value="" class="form-control form-control-sm"/>
					</div>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="ActividadInstitucional" class="form-label"> Actividad Institucional: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="ActividadInstitucional" name="ActividadInstitucional" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="hcUnidadEjecutora" class="form-label"> Unidad Ejecutora: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<%String primerUE = "";%>
					<%int cnt = 0;%>
					<%String vacio = "";%>
					<%if(vistas!=null){%>
					
					<%	out.println("\t\t\t\t<select id=\"hcUnidadEjecutora\" name=\"hcUnidadEjecutora\" class=\"form-select form-select-sm\" onChange=\"hcUnidadEjecutoraChange()\">");%>
					<%	out.println("\t\t\t\t\t<option value=\"" + vacio + "\"></option>" );%>
					<%	for( Iterator<UnidadEjecutora> i = vistas.iterator(); i.hasNext(); ){ %>
					<%		UnidadEjecutora ue = i.next(); %>
					<% 		if( cnt == 0 ){primerUE=ue.getUe();} %>					
					<%		out.println("\t\t\t\t\t<option value=\"" + ue.getUe() + "\">" + ue.getDescripcion() + "</option>" );%>
					<%		cnt++;%>
					<%	}%>
					<%	out.println("\t\t\t\t</select>");%>					
					<%	if(cnt == 1){ %>
					<%		out.println("\t\t\t\t<input id=\"cUnidadEjecutora\" name=\"cUnidadEjecutora\" type=\"hidden\" value=\"" + primerUE + "\" size=\"5\" maxlength=\"5\" class=\"\"/>");%>
					<%	}else{%>
					<%		out.println("\t\t\t\t<input id=\"cUnidadEjecutora\" name=\"cUnidadEjecutora\" type=\"hidden\" value=\"" + vacio + "\" size=\"5\" maxlength=\"5\" class=\"\"/>");%>
					<%	} %>										
					<%}else{ %>
						<div class="input-group">
							<input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text" class="AyudaSyC autoCompletaSyC form-control form-control-sm"/>
							<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden" class="form-control form-control-sm"/>
						</div>
					<%} %>
				</div>
			</div>
			
			<br/>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="InfoRegMes" class="form-label"> Inf. registrada al mes: </label>				
					<select name="InfoRegMes" id="InfoRegMes" onchange="" class="form-select form-select-sm">
						<option value="Z:">AAAAAAAAA</option>
						<option value="Y:">BBBBBBBBB</option>
						<option value="X:">CCCCCCCCC</option>
						<option value="xx" selected>--</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="orderBy" class="form-label"> Ordenado por: </label>				
					<select name="orderBy" id="orderBy" onchange="" class="form-select form-select-sm">
						<option value="dCuenta">Cuenta</option>
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
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="groupBy" class="form-label"> Agrupado por: </label>		
					<select name="groupBy" id="groupBy" onchange="" class="form-select form-select-sm">
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
					</select>		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="TipoReporte" class="form-label"> Tipo de Reporte: </label>		
					<select name="TipoReporte" id="TipoReporte" onchange="" class="form-select form-select-sm">
						<option value="GENERAL" selected>GENERAL</option>
						<option value="CALENDARIZADO">CALENDARIZADO</option>
					</select>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="Componentes" class="form-label"> Componentes EP: </label>				
					<select name="Componentes" id="Componentes" onchange="" class="form-select form-select-sm">
						<option value="INCLUIR" >INCLUIR</option>
						<option value="DESCARTAR" selected>DESCARTAR</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="Resumen" class="form-label"> Resumen Criterios: </label>				
					<select name="Resumen" id="Resumen" onchange="" class="form-select form-select-sm">
						<option value="INCLUIR" >INCLUIR</option>
						<option value="DESCARTAR" selected>DESCARTAR</option>
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="tipoEP" class="form-label"> Tipo EP: </label>		
					<select name="tipoEP" id="tipoEP" onchange="" class="form-select form-select-sm">
						<option value="EPTRABAJO" selected>EP TRABAJO</option>
						<option value="EPORIGEN">EP ASF</option>
					</select>	
				</div>
			</div>

			<br/>
			
			<span id="zonaInsercion"> <a>Llamo Función insertactasPresupuestales</a> </span>
			
			<br/>
			
			<div class="row justify-content-center">				
				<div class="columna col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="GenerarReporteExcel" value="Generar Reporte Excel" onClick="return fnPrintTituloReporte('S');" class="btn-secondary btn-secondary-sm"/>								
				</div>
				<div class="columna col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="GenerarReporte" value="Generar Reporte" onClick="return fnPrintTituloReporte();" class="btn-secondary btn-secondary-sm"/>				
				</div>
				<div class="columna col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
					<input type="button" id="Limpiar" value="Limpiar" onClick="limpiarSesion();" class="btn-secondary btn-secondary-sm"/>						
				</div>
			</div>
		
		</div>
	</form>
</body>
</html>
