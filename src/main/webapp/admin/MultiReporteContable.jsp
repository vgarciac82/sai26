<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>



<%
			String cCentroContable = "";
			String mensaje = "";
			if (request.getParameter("msg") != null
					&& !"".equals(request.getParameter("msg"))) {
				mensaje = request.getParameter("msg");
				mensaje = mensaje.replace("[", "");
				mensaje = mensaje.replace("]", "");
				mensaje = mensaje.replace(",", "<br>");
			}
			Usuario usuario = (Usuario) session
					.getAttribute(GestionInterface.ATT_USER);
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
			//Valida Centro de Costos
			if (usuario.getPropiedades() != null
					&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
						.getValor();
			}
		
			if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
				mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
			}
			
		%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MultiReporteContable</title>
<script type="text/javascript" src="../js/datepickercontrol.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/jq9/jquery-ui-1.9.0.custom.js"></script>
<script type="text/javascript" src="../Generador/js/Poliza.js"></script>
<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
<script src="../js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />
<link rel="stylesheet" type="text/css" href="../css/menuContabilidad.css" />
<link href="js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css" rel="stylesheet">
<link href="../Generador/css/demo_page.css" rel="stylesheet">
<link href="../Generador/css/demo_table_jui.css" rel="stylesheet">


<script type="text/javascript">

	function limpiarSesion()
	{
		window.location.href="MultiReporteContable.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
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

	function insertactasContables()
	{
// 		zona2 = document.getElementById('zonaInsercion') ;
		querySelectPost("catalogoCtasContablesRead","ctasContablesOrigen", {async: false });
/*
		zona = "<table border=\"0\" width=\"1028\"  height=\"10\">";
		zona+= "<tr>";
		zona+="<td align=\"right\">Contabilidad:</td>";
		for(i=0;i<ctasContables.options.length;i++) {
			zona+="<td><input type=\"checkbox\" id=\""+ctasContables.options[i].value.replace(" ","").toLowerCase()+"\" name=\"CuentasPresup\" value=\""+ctasContables.options[i].text+"\"/>"+toTitleCase(ctasContables.options[i].value)+"<br></td>";
			if (((i+1) % 5)==0){
				zona+="</tr>";
				zona+="<tr><td></td>";
			} 
		}		
		zona+="</tr>";
		zona+="</table>";
		zona2.innerHTML=zona;
*/
	}

	function fnSeleccionaMes()
	{
		querySelectPost("catalogoMesesRead","InfoRegMes", {async: false });
		InfoRegMes = document.getElementById("InfoRegMes") ;
		$("#InfoRegMes").val( InfoRegMes.options.length ).attr('selected',true);
	}

	function fnPrintTituloReporte() {

			var principal = parent.parent.document;
			var asunto    = document.datawork;

			var ep= $.trim(document.getElementById('ep').value);
			var EjercicioFiscal = (ep==""?document.getElementById('EjercicioFiscal').value:"");

/*			var slctIdioma = document.getElementById("Cuenta");
			var idioma="";
			var flagI = 0;
			for(i=1;i<slctIdioma.options.length;i++) {
				if(slctIdioma.options[i].selected == true){
					if(flagI == 0){
						idioma = "'" + slctIdioma.options[i].value + "'";
						flagI = 1;
					}else{
						idioma += ", '" + slctIdioma.options[i].value + "'";
					};
				};
			};
			Cuenta    		= idioma;
*/
//			StringBuffer cadena = new StringBuffer();
//			String[] selections = request.getParameterValues("Cuenta");
//			for (int x=0;x<selections.length;x++){   cadena =cadena.append(selections[x]);}
//			String Cuenta = cadena.toString();
//			%>

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
//			var rpt_headerCtas="";
			var nCtasPresup = new Array();
			var dCtasPresup = new Array();
//			var cCentroContable = document.getElementById('cCentroContable').value;
			var cCentroContable = '<%=cCentroContable%>';
			
//			var CuentasPresup=document.getElementByName('CuentasPresup').value;
			var j=0;
	 		var pipe="";
			var ctasContablesPipe="";
			var ctasContablesPipe2="";

				ctasContables = document.getElementById("ctasContables");
				ctasContablesOrigen = document.getElementById("ctasContablesOrigen");

			for(i=0;i<ctasContables.options.length;i++) {
//	            if ( document.getElementById(ctasContables.options[i-1].value.replace(" ","").toLowerCase()).checked){
					cadena= cadena == ""?  "'" +  ctasContables.options[i].text + "'" : cadena + ", " + "'" +  ctasContables.options[i].text + "'";
					filtro= filtro == ""?  ctasContables.options[i].value.toLowerCase() : filtro + ", "+ctasContables.options[i].value.toLowerCase();
					nCtasPresup[j]=ctasContables.options[i].value; 
					dCtasPresup[j]=ctasContables.options[i].text;
					if (i>0) pipe=",";
					ctasContablesPipe+=pipe+ctasContables.options[i].text;
					ctasContablesPipe2+=pipe+ctasContables.options[i].value;
					j=j+1;
//				}
			}
			if (cadena==""){
				for(i=0;i<ctasContablesOrigen.options.length;i++) {
					filtro= filtro == ""?  ctasContablesOrigen.options[i].value.toLowerCase() : filtro + ", "+ctasContablesOrigen.options[i].value.toLowerCase();
					nCtasPresup[i]=ctasContablesOrigen.options[i].value; 
					dCtasPresup[i]=ctasContablesOrigen.options[i].text;
					if (i>0) pipe=",";
					ctasContablesPipe+=pipe+ctasContablesOrigen.options[i].text;
					ctasContablesPipe2+=pipe+ctasContablesOrigen.options[i].value;
				}
			} 
//			alert(nCtasPresup);
//			alert(dCtasPresup);
//			alert(cadena);
//			alert(filtro);
			Cuenta    		= cadena;
//			alert("Cuenta: "+Cuenta);

				 document.getElementById("ihep").value=ep;
				 document.getElementById("ihEjercicioFiscal").value=EjercicioFiscal;
//				 + "&Cuenta=" + Cuenta
				 document.getElementById("ihcUnidadEjecutora").value=cUnidadEjecutora;
				 document.getElementById("ihhcUnidadEjecutora").value=hcUnidadEjecutora;
				 document.getElementById("ihRamoEP").value=RamoEP;
				 document.getElementById("ihUnidadResponsableEP").value=UnidadResponsableEP;
				 document.getElementById("ihhUnidadResponsableEP").value=hUnidadResponsableEP;
				 document.getElementById("ihGrupoFuncional").value=GrupoFuncional;
				 document.getElementById("ihFuncion").value=Funcion;
				 document.getElementById("ihSubFuncion").value=SubFuncion;
				 document.getElementById("ihProgramaGeneral").value=ProgramaGeneral;
				 document.getElementById("ihProgramaPresupuestario").value=ProgramaPresupuestario;
				 document.getElementById("ihhProgramaPresupuestario").value=hProgramaPresupuestario;
				 document.getElementById("ihActividadInstitucional").value=ActividadInstitucional;
				 document.getElementById("ihPartida").value=Partida;
				 document.getElementById("ihhPartida").value=hPartida;
				 document.getElementById("ihTipoGasto").value=TipoGasto;
				 document.getElementById("ihFuenteFinanciamiento").value=FuenteFinanciamiento;
				 document.getElementById("ihEntidadFederativa").value=EntidadFederativa;
				 document.getElementById("ihCartera").value=Cartera;
				 document.getElementById("ihUnidadNormativa").value=UnidadNormativa;
				 document.getElementById("ihhUnidadNormativa").value=hUnidadNormativa;
//				 + "&ClaveCNA=" + ClaveCNA
				 document.getElementById("ihcOrddeBy").value=cOrddeBy;
				 document.getElementById("ihcGroupBy").value=cGroupBy;
				 document.getElementById("ihInfoRegMes").value=InfoRegMes;
				 document.getElementById("ihTipoReporte").value=TipoReporte;
				 document.getElementById("ihComponentes").value=Componentes;
				 document.getElementById("ihResumen").value=Resumen;
				 document.getElementById("ihUsuario").value=Usuario;
//				 + "&Filtro="+ filtro
//				 + "&rpt_headerCtas="+ rpt_headerCtas
				 document.getElementById("ihnCtasPresups").value=ctasContablesPipe2;
				 document.getElementById("ihdCtasPresups").value=ctasContablesPipe;
				 document.getElementById("ihcCentroContable").value=cCentroContable;
//				 + "&CuentasPresup="+ CuentasPresup
				 document.getElementById("ihrn").value="ReporteAuditoria.jasper";
//				 alert(param);


//			if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" & ClaveCNA=="" )
			if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" )
			{
				alert("Capturar algun dato");
				return false;
			} else {
				//alert("antes de ejecutar MultiReporteContableResultado.jsp");
//				parent.frames['resultado'].location.href = "MultiReporteContableResultado.jsp?" + param;
				$("#GenerarReporte").attr("disabled", true);
				$("#Limpiar").attr("disabled", true);
				//window.open('MultiReporteContableResultado2.jsp?'+ param, 'MultiReporteContableResultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
				//window.open('MultiReporteContableResultado2.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteContableResultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
		   		var strAction="MultiReporteContableResultado.jsp?id=<%=request.getParameter("id")%>";
		   		document.form1.action=strAction;
				document.form1.submit();
				//window.open('MultiReporteContableResultado.jsp?'+ param, 'MultiReporteContableResultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
				$("#GenerarReporte").attr("disabled", false);
				$("#Limpiar").attr("disabled", false);
				return true;
			}


		}
</script>
<script type="text/javascript" >

			$().ready(function() 
				{
					$('.pasar').click(function() { return !$('#ctasContablesOrigen option:selected').remove().appendTo('#ctasContables'); });  
					$('.quitar').click(function() { return !$('#ctasContables option:selected').remove().appendTo('#ctasContablesOrigen'); });
					$('.pasartodos').click(function() { $('#ctasContablesOrigen option').each(function() { $(this).remove().appendTo('#ctasContables'); }); });
					$('.quitartodos').click(function() { $('#ctasContables option').each(function() { $(this).remove().appendTo('#ctasContablesOrigen'); }); });
					$('.submit').click(function() { $('#ctasContables option').prop('selected', 'selected'); });
				});

		$(document).ready(
			function()
			{
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();

				$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
				
			$("#GenerarReporte").attr("disabled", true);
			$("#Limpiar").attr("disabled", true);
			$("#accordion").accordion();
			
			// Inicializaciones CRUD

			querySelectPost("catalogoEjercicioFiscalRead2", "EjercicioFiscal");
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

			insertactasContables();
			
			fnSeleccionaMes();

			$("#GenerarReporte").attr("disabled", false);
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
				$( "#ALM" )
				.change(function() {
				$( "#ep" ).val($( "#ALM" ).val());
				});
				$( "#cIDRFC" )
				.change(function() {
				$( "#ep" ).val($( "#cIDRFC" ).val());
				});
				$( "#idCuenta" )
				.change(function() {
				$( "#ep" ).val($( "#CTABAN" ).val());
				});
				$( "#CTABAN" )
				.change(function() {
				$( "#ep" ).val($( "#CTABAN" ).val());
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
	<body background="../imagenes/steel_BG.gif" scroll="yes"
		style="width: 97%">
		<form id="form1" name="form1" method="post" target="formulario"
			action="./MultiReporteContableResultado.jsp">
			<input type="hidden" id="ihdcuentas" />
			<!--	<input type="hidden" id="ihid"/>-->
			<input type="hidden" id="ihep" name="ihep" />
			<input type="hidden" id="ihEjercicioFiscal" name="ihEjercicioFiscal" />
			<input type="hidden" id="ihcUnidadEjecutora"
				name="ihcUnidadEjecutora" />
			<input type="hidden" id="ihhcUnidadEjecutora"
				name="ihhcUnidadEjecutora" />
			<input type="hidden" id="ihRamoEP" name="ihRamoEP" />
			<input type="hidden" id="ihUnidadResponsableEP"
				name="ihUnidadResponsableEP" />
			<input type="hidden" id="ihhUnidadResponsableEP"
				name="ihhUnidadResponsableEP" />
			<input type="hidden" id="ihGrupoFuncional" name="ihGrupoFuncional" />
			<input type="hidden" id="ihFuncion" name="ihFuncion" />
			<input type="hidden" id="ihSubFuncion" name="ihSubFuncion" />
			<input type="hidden" id="ihProgramaGeneral" name="ihProgramaGeneral" />
			<input type="hidden" id="ihProgramaPresupuestario"
				name="ihProgramaPresupuestario" />
			<input type="hidden" id="ihhProgramaPresupuestario"
				name="ihhProgramaPresupuestario" />
			<input type="hidden" id="ihActividadInstitucional"
				name="ihActividadInstitucional" />
			<input type="hidden" id="ihPartida" name="ihPartida" />
			<input type="hidden" id="ihhPartida" name="ihhPartida" />
			<input type="hidden" id="ihTipoGasto" name="ihTipoGasto" />
			<input type="hidden" id="ihFuenteFinanciamiento"
				name="ihFuenteFinanciamiento" />
			<input type="hidden" id="ihEntidadFederativa"
				name="ihEntidadFederativa" />
			<input type="hidden" id="ihCartera" name="ihCartera" />
			<input type="hidden" id="ihUnidadNormativa" name="ihUnidadNormativa" />
			<input type="hidden" id="ihhUnidadNormativa"
				name="ihhUnidadNormativa" />
			<input type="hidden" id="ihcOrddeBy" name="ihcOrddeBy" />
			<input type="hidden" id="ihcGroupBy" name="ihcGroupBy" />
			<input type="hidden" id="ihInfoRegMes" name="ihInfoRegMes" />
			<input type="hidden" id="ihTipoReporte" name="ihTipoReporte" />
			<input type="hidden" id="ihComponentes" name="ihComponentes" />
			<input type="hidden" id="ihResumen" name="ihResumen" />
			<input type="hidden" id="ihUsuario" name="ihUsuario" />
			<input type="hidden" id="ihnCtasPresups" name="ihnCtasPresups" />
			<input type="hidden" id="ihdCtasPresups" name="ihdCtasPresups" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" />
			<input type="hidden" id="ihcCentroContable" name="ihcCentroContable" />
			<input type="hidden" id="ihrn" name="ihrn" "/>
			<input type="hidden" id="nIdAlmacen" name="nIdAlmacen" value="" />

			<div class="Contenido" style="height: 10%">
				<table class="TituloRutaCA" height="10">
					<tr>
						<td>
							<img src="../imagenes/iconos/reportes.png" alt="" width="16"
								height="16">
							<font color="#FFFFFF"> <strong>
									MultiReporteContable. </strong> </font>
						</td>
						<td>
							&nbsp;
					</tr>
				</table>
			</div>
			<div id="container" class="container SyCData">
				<div id="accordion">
					<h3>
						Clave Presupuestal
					</h3>
					<div>
						<table border="0" width="1028" height="10">
							<tr>
								<td width="20%" height="10">
									<input type="hidden" id="UnidadEjecutora"
										name="UnidadEjecutora" value="" />
								</td>
								<td width="20%" height="10">
									<input type="hidden" id="nOrden" name="nOrden" value="" />
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
								<td align="right">
									Estructura Programática:
								</td>
								<td>
									<!--
					<input id="hep" name="hep" type="text"   value="" size="64" maxlength="64" class="AyudaSyC  autoCompletaSyC"/>
					<input id="ep" name="ep" type="hidden"   value="" size="4" maxlength="64" class=""/>
-->
									<!--					<input id="ep" name="ep" type="text"   value="" size="64" maxlength="64" class=""/>-->

									<!-- 					<input id="hep" name="hep" type="text"   value="" size="64" maxlength="64" class=""/> -->
									<input id="ep" name="ep" type="text" value="" size="64"
										maxlength="64" class="" />
								</td>
								<td>
									<input type="button" value="..." onclick="Grid()"
										onblur="rellenaCampos();" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Ejercicio Fiscal:
								</td>
								<!--				<td><input id="EjercicioFiscal"     name="EjercicioFiscal"   value="2012" size="4"  maxlength="5"/></td>-->
								<td>
									<select id="EjercicioFiscal" name="EjercicioFiscal" value="">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Programa Presupuestario:
								</td>
								<!--				<td><input id="ProgramaPresupuestario"  name="ProgramaPresupuestario"  value="" size="4"  maxlength="5"/></td>-->
								<td>
									<input id="hProgramaPresupuestario"
										name="hProgramaPresupuestario" type="text" value="" size="40"
										maxlength="50" class="AyudaSyC  autoCompletaSyC" />
									<input id="ProgramaPresupuestario"
										name="ProgramaPresupuestario" type="hidden" value="" size="5"
										maxlength="5" class="" />
								</td>
								<!--				<td><select id="ProgramaPresupuestario" name="ProgramaPresupuestario">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
							</tr>
							<tr>
								<td align="right">
									Ramo:
								</td>
								<!--				<td><input id="RamoEP"     name="RamoEP"     value="" size="3"  maxlength="5"/></td>-->
								<!--					<td><input id="RamoEP" name="RamoEP" type="text"   value="" size="3" maxlength="5" class="AyudaSyC  autoCompletaSyC"/></td>-->
								<td>
									<select id="RamoEP" name="RamoEP">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Partida:
								</td>
								<!--				<td><input id="Partida"  name="Partida"  value="" size="5"  maxlength="7"/></td>-->
								<td>
									<input id="hPartida" name="hPartida" type="text" value=""
										size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC" />
									<input id="Partida" name="Partida" type="hidden" value=""
										size="5" maxlength="5" class="" />
								</td>
								<!--				<td><select id="Partida" name="Partida" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
							</tr>
							<tr>
								<td align="right">
									Unidad Responsable:
								</td>
								<!--				<td><input id="UnidadResponsableEP"     name="UnidadResponsableEP"     value="" size="4"  maxlength="5"/></td>-->
								<td>
									<input id="hUnidadResponsableEP" name="hUnidadResponsableEP"
										type="text" value="" size="40" maxlength="50"
										class="AyudaSyC  autoCompletaSyC" />
									<input id="UnidadResponsableEP" name="UnidadResponsableEP"
										type="hidden" value="" size="5" maxlength="5" class="" />
								</td>
								<!--				<td><select id="UnidadResponsableEP" name="UnidadResponsableEP" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
								<td align="right">
									Tipo Gasto:
								</td>
								<!--				<td><input id="TipoGasto"  name="TipoGasto"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="TipoGasto" name="TipoGasto">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">
									Grupo Funcional:
								</td>
								<!--				<td><input id="GrupoFuncional"  name="GrupoFuncional"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="GrupoFuncional" name="GrupoFuncional">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Fuente Financiamiento:
								</td>
								<!--				<td><input id="FuenteFinanciamiento"  name="FuenteFinanciamiento"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="FuenteFinanciamiento" name="FuenteFinanciamiento">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">
									Funcion:
								</td>
								<!--				<td><input id="Funcion"  name="Funcion"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="Funcion" name="Funcion">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Entidad Federativa
								</td>
								<!--				<td><input id="EntidadFederativa"  name="EntidadFederativa"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="EntidadFederativa" name="EntidadFederativa">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">
									SubFuncion:
								</td>
								<!--				<td><input id="SubFuncion"  name="SubFuncion"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="SubFuncion" name="SubFuncion">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Cartera:
								</td>
								<!--				<td><input id="Cartera"  name="Cartera"  value="" size="12"  maxlength="15"/></td>-->
								<td>
									<input id="hCartera" name="hCartera" type="text" value=""
										size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC" />
									<input id="Cartera" name="Cartera" type="hidden" value=""
										size="5" maxlength="5" class="" />
								</td>
								<!--				<td><select id="Cartera" name="Cartera">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
							</tr>
							<tr>
								<td align="right">
									Programa General:
								</td>
								<!--				<td><input id="ProgramaGeneral"  name="ProgramaGeneral"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<select id="ProgramaGeneral" name="ProgramaGeneral">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Unidad Normativa:
								</td>
								<!--				<td><input id="UnidadNormativa"  name="UnidadNormativa"  value="" size="3"  maxlength="5"/></td>-->
								<td>
									<input id="hUnidadNormativa" name="hUnidadNormativa"
										type="text" value="" size="40" maxlength="50"
										class="AyudaSyC  autoCompletaSyC" />
									<input id="UnidadNormativa" name="UnidadNormativa"
										type="hidden" value="" size="5" maxlength="5" class="" />
								</td>
								<!--				<td><select id="UnidadNormativa" name="UnidadNormativa" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>-->
							</tr>
							<tr>
								<td align="right">
									Actividad Institucional:
								</td>
								<!--				<td><input id="ActividadInstitucional"  name="ActividadInstitucional"  value="" size="4"  maxlength="5"/></td>-->
								<td>
									<select id="ActividadInstitucional"
										name="ActividadInstitucional">
										<option value="Z:">
											A
										</option>
										<option value="Y:">
											B
										</option>
										<option value="X:">
											C
										</option>
										<option value="xx" selected>
											--
										</option>
									</select>
								</td>
								<td align="right">
									Unidad Ejecutora:
								</td>
								<!--				<td><input id="cUnidadEjecutora"  name="cUnidadEjecutora"  value="" size="2"  maxlength="5"/></td>-->
								<td>
									<input id="hcUnidadEjecutora" name="hcUnidadEjecutora"
										type="text" value="" size="40" maxlength="50"
										class="AyudaSyC  autoCompletaSyC" />
									<input id="cUnidadEjecutora" name="cUnidadEjecutora"
										type="hidden" value="" size="5" maxlength="5" class="" />
								</td>
								<!--				<td><select id="cUnidadEjecutora" name="cUnidadEjecutora" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select>
				</td>-->
							</tr>
						</table>
					</div>
					<h3>
						Almacen
					</h3>
					<div id="divAML" style="display: none">
						<input type="text" name="ALM" id="ALM" value="" size="17"
							onkeydown="return ctaKeyDwn(event, this.id)"
							onfocus="cierraAyuda()" class="autoCompletaSyC AyudaSyC" />
						<!--																<select id="ALM" name="ALM" onfocus="cierraAyuda()"-->
						<!--																	onkeypress="return toNext(event,this.id)"></select>-->
					</div>
					<h3>
						RFC
					</h3>

					<div id="divRFC">
						<input type="text" name="cIDRFC" id="cIDRFC"
							onfocus="cierraAyuda()" onblur=""
							class="AyudaSyC autoCompletaSyC" />
					</div>
					<h3>
						Cuenta Bancaria
					</h3>

					<div id="divCTAB">
						&nbsp;&nbsp;&nbsp;ID:&nbsp;
						<input type="text" name="idCuenta" id="idCuenta" value=""
							class="AyudaSyC" size="3" maxlength="3" onblur="blurCtaBanc()"
							onkeydown="return ctaKeyDwn(event, this.id)" />
						<br>
						Clabe:
						<input type="text" name="CTABAN" id="CTABAN" value="" size="17"
							onkeydown="return ctaKeyDwn(event, this.id)"
							onfocus="cierraAyuda()" class="autoCompletaSyC AyudaSyC" />
					</div>

				</div>
			</div>
			<div>
				<table border="0" width="1028" height="10">
					<tr>
						<td align="right" width="15%">
							Información registrada al mes:
						</td>
						<td>
							<select name="InfoRegMes" id="InfoRegMes" onchange="" value="">
								<option value="Z:">
									AAAAAAAAA
								</option>
								<option value="Y:">
									BBBBBBBBB
								</option>
								<option value="X:">
									CCCCCCCCC
								</option>
								<option value="xx" selected>
									--
								</option>
							</select>
						</td>

						<td align="right" width="10%">
							Ordenado por:
						</td>
						<td>
							<select name="orderBy" id="orderBy" onchange="">
								<option value="dCuenta">
									Cuenta
								</option>
								<!--					<option value="nClaveCNA">Código</option>-->
								<option value="cSubCuenta" selected>
									Estructura Programática
								</option>
								<option value="aEjercicioFiscal_1">
									Ejercicio Fiscal
								</option>
								<option value="cRamo_2">
									Ramo
								</option>
								<option value="cUnidadResponsable_3">
									Unidad Responsable
								</option>
								<option value="cGrupoFuncional_4">
									Grupo Funcional
								</option>
								<option value="cFuncion_5">
									Funcion
								</option>
								<option value="cSubFuncion_6">
									Sub Funcion
								</option>
								<option value="cProgramaGeneral_7">
									Programa General
								</option>
								<option value="cProgramaPresupuestario_9">
									Programa Presupuestario
								</option>
								<option value="cActividadInstitucional_8">
									Actividad Institucional
								</option>
								<option value="cPartida_10">
									Partida
								</option>
								<option value="cTipoGasto_11">
									Tipo Gasto
								</option>
								<option value="cFuenteFinanciamiento_12">
									Fuente Financiamiento
								</option>
								<option value="cEntidadFederativa_13">
									Entidad Federativa
								</option>
								<option value="cCartera_14">
									Cartera
								</option>
								<option value="cUnidadResponsable_15">
									Unidad Ejecutora
								</option>
								<option value="cUnidadResponsable_16">
									Unidad Normativa
								</option>
							</select>
						</td>
						<td align="right" width="10%">
							Agrupado por:
						</td>
						<td>
							<select name="groupBy" id="groupBy" onchange="">
								<!--					<option value="nClaveCNA">Código</option>-->
								<option value="cSubCuenta" selected>
									Estructura Programática
								</option>
								<option value="aEjercicioFiscal_1">
									Ejercicio Fiscal
								</option>
								<option value="cRamo_2">
									Ramo
								</option>
								<option value="cUnidadResponsable_3">
									Unidad Responsable
								</option>
								<option value="cGrupoFuncional_4">
									Grupo Funcional
								</option>
								<option value="cFuncion_5">
									Funcion
								</option>
								<option value="cSubFuncion_6">
									Sub Funcion
								</option>
								<option value="cProgramaGeneral_7">
									Programa General
								</option>
								<option value="cProgramaPresupuestario_9">
									Programa Presupuestario
								</option>
								<option value="cActividadInstitucional_8">
									Actividad Institucional
								</option>
								<option value="cPartida_10">
									Partida
								</option>
								<option value="cTipoGasto_11">
									Tipo Gasto
								</option>
								<option value="cFuenteFinanciamiento_12">
									Fuente Financiamiento
								</option>
								<option value="cEntidadFederativa_13">
									Entidad Federativa
								</option>
								<option value="cCartera_14">
									Cartera
								</option>
								<option value="cUnidadResponsable_15">
									Unidad Ejecutora
								</option>
								<option value="cUnidadResponsable_16">
									Unidad Normativa
								</option>
								<option value="cCentroContable">
									Centro Contable
								</option>
							</select>
						</td>
						<td align="right" width="10%">
							Tipo de Reporte:
						</td>
						<td>
							<select name="TipoReporte" id="TipoReporte" onchange="">
								<option value="GENERAL" selected>
									GENERAL
								</option>
								<option value="CALENDARIZADO">
									CALENDARIZADO
								</option>
							</select>
						</td>

					</tr>
					<tr>
						<td align="right" width="10%">
							Componentes EP:
						</td>
						<td>
							<select name="Componentes" id="Componentes" onchange="">
								<option value="INCLUIR">
									INCLUIR
								</option>
								<option value="DESCARTAR" selected>
									DESCARTAR
								</option>
							</select>
						</td>
						<td align="right" width="10%">
							Resumen Criterios:
						</td>
						<td>
							<select name="Resumen" id="Resumen" onchange="">
								<option value="INCLUIR">
									INCLUIR
								</option>
								<option value="DESCARTAR" selected>
									DESCARTAR
								</option>
							</select>
						</td>
					</tr>

				</table>
			</div>

<!--			<span id="zonaInsercion"> <a>Llamo Función-->
<!--					insertactasContables</a> </span>-->

			<table>
			<tr>
						<td align="right" width="10%">
				<div>
					<select name="ctasContablesOrigen[]" id="ctasContablesOrigen"
						 size="8">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
					</select>
				</div>
						</td>
						<td align="center" width="10%">
				<div>
					<input type="button" class="pasar izq" value="Pasar »">
					<input type="button" class="quitar der" value="« Quitar">
					<br />
					<input type="button" class="pasartodos izq" value="Todos »">
					<input type="button" class="quitartodos der" value="« Todos">
				</div>
						</td>
						<td align="left" width="10%">
				<div class="">
					<select name="ctasContables[]" id="ctasContables" size="8"></select>
				</div>
						</td>
					</tr>
<!--				<p class="clear">-->
<!--					<input type="submit" class="submit" value="Procesar formulario">-->
<!--				</p>-->
			</table>


			<table align="center" height="10">
				<tr>
					<td>
						<input type="button" id="GenerarReporte" name="GenerarReporte"
							value="Generar Reporte" onClick="return fnPrintTituloReporte();">
						<input type="button" id="Limpiar" name="Limpiar" value="Limpiar"
							onClick="limpiarSesion();" />
					</td>
				</tr>
				<tr>
					&nbsp;
				</tr>
			</table>
		</form>
	</body>
</html>
