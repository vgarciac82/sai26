<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>



<%
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
// 			"GrupoFuncional",
			
			"Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MultiReporte</title>
<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

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

<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">

	

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
			var GrupoFuncional			= $.trim("");
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
//			alert(nCtasPresup);
//			alert(dCtasPresup);
//			alert(cadena);
//			alert(filtro);
			Cuenta    		= cadena;
//			alert("Cuenta: "+Cuenta);

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
				 + "&rn=ReporteAuditoria.jasper";
//				 alert(param);


//			if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" & ClaveCNA=="" )
			if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" 
 			& GrupoFuncional=="" 
			& Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" )
			{
				alert("Capturar algun dato");
				return false;
			} else {
				//alert("antes de ejecutar MultiReporteResultado.jsp");
//				parent.frames['resultado'].location.href = "MultiReporteResultado.jsp?" + param;
				$("#GenerarReporte").attr("disabled", true);
				$("#Limpiar").attr("disabled", true);
				//window.open('MultiReporteResultado.jsp?'+ param, 'MultiReporteResultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
				$("#GenerarReporte").attr("disabled", false);
				$("#Limpiar").attr("disabled", false);
				return true;
			}


		}
</script>
<script type="text/javascript" >

		$(document).ready(
			function()
			{
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();

				

			$("#GenerarReporte").attr("disabled", true);
			$("#Limpiar").attr("disabled", true);

			// Inicializaciones CRUD

			querySelectPost("catalogoEjercicioFiscalRead", "EjercicioFiscal");
			querySelectPost("catalogoFuenteFinanciamientoRead", "FuenteFinanciamiento");
			querySelectPost("catalogoTipoGastoRead", "TipoGasto");


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

// 				$( "#GrupoFuncional" )
// 				.change(function() {
// 					querySelectPost("catalogoFuncionRead", "Funcion");
// 				});

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
            
            
            
            function generarReporteAnterior()
            {
            	
            	
					
            	if(!validacion())
            	{
					alert("Debe ingresar un criterio de busqueda");
					return false;

            	}
            	else
            	{
            		
					$.blockUI( {
						message : "Procesando espere ......"
					});
            		$("#form1").submit();
            		$.unblockUI();
           		}
           		
            }
            
            
            function seleccionarTodosCapitulos()
            {
            	//id="10000"
            	if($("#seleccionaCapitulos").is(':checked')) //document.datosReintegro.autorizaRein[1].checked
				{
					$("#10000").attr("checked", "checked");
	            	$("#20000").attr("checked", "checked");
	            	$("#30000").attr("checked", "checked");
	            	$("#40000").attr("checked", "checked");
	            	$("#50000").attr("checked", "checked");
	            	$("#60000").attr("checked", "checked");
				}
				else
				{
					$("#10000").removeAttr("checked");
	            	$("#20000").removeAttr("checked");
	            	$("#30000").removeAttr("checked");
	            	$("#40000").removeAttr("checked");
	            	$("#50000").removeAttr("checked");
	            	$("#60000").removeAttr("checked");
				}
            }
            
            
            function validacion()
            {
            	var revisa=true;
//             	if($("#Partida").val()=="" && $("cUnidadEjecutora").val()==undefined && $("#FuenteFinanciamiento").val()=="")
//             	{
//             		if((!$("#10000").is(':checked')) && (!$("#20000").is(':checked')) && (!$("#30000").is(':checked')) && (!$("#40000").is(':checked')) && (!$("#50000").is(':checked')) && (!$("#60000").is(':checked')) )
//             		{
//             			revisa=false;
//             		}
//             	}
            	return revisa;
            }
            
            
            function limpiarDatos()
            {
            	$("#hPartida").val("");
            	$("#Partida").val("");
            	$("#hcUnidadEjecutora").val("");
				$("#FuenteFinanciamiento").val("");
				$("#cUnidadEjecutora").val("");
				
// 				$("#EjercicioFiscal2 option[value=\"xx\"]").attr("selected","selected");	
// 				$("#EjercicioFiscal3 option[value=\"xx\"]").attr("selected","selected");	
			
				$("#seleccionaCapitulos").removeAttr("checked");
				
				$("#10000").removeAttr("checked");
	            $("#20000").removeAttr("checked");
	            $("#30000").removeAttr("checked");
	            $("#40000").removeAttr("checked");
	            $("#50000").removeAttr("checked");
	            $("#60000").removeAttr("checked");
            }
            
            function validaChechk()
            {
            	
            	var revisa=true;
            	var valida=0;
            	if($("#10000").is(':checked') )
            	{
            		valida+=1;
            	}
            	if($("#20000").is(':checked'))
            	{
            		valida+=1;
            	}
            	if($("#30000").is(':checked'))
            	{
            		valida+=1;
            	}
            	if($("#40000").is(':checked'))
            	{
            		valida+=1;
            	}
            	if($("#50000").is(':checked'))
            	{
            		valida+=1;
            	}
            	if($("#60000").is(':checked'))
            	{
            		valida+=1;
            	}
            	
            	if(valida>=2)
            	{
            		
            		revisa=false;
            		$("#hPartida").removeClass("AyudaSyC");
            		$("#hPartida").removeClass("autoCompletaSyC");
            		//$("#hPartida").attr("type", "hidden");
            		$("#hPartida").attr("disabled", true);
            	}
            	else
            	{
            		$("#hPartida").attr("disabled", false);
            		revisa=true;
            	}
            	
            	  
            	
            	return revisa;
            }
            
            function arrayCheck()
            {
            	var capitulos="";
            	if($("#10000").is(':checked') )
            	{
            		capitulos+=$("#10000").val();
            	}
            	if($("#20000").is(':checked'))
            	{
            		capitulos+=","+$("#20000").val();
            	}
            	if($("#30000").is(':checked'))
            	{
            		capitulos+=","+$("#30000").val();
            	}
            	if($("#40000").is(':checked'))
            	{
            		capitulos+=","+$("#40000").val();
            	}
            	if($("#50000").is(':checked'))
            	{
            		capitulos+=","+$("#50000").val();
            	}
            	if($("#60000").is(':checked'))
            	{
            		capitulos+=","+$("#60000").val();
            	}
            }
            
</script>
</head>

<body id="dt_example">
	<form id="form1" name="form1" action="../servlet/ReporteEjercicioAnteriorServlet" method="post">

	<div id="container" class="container SyCData">
			<h1>Multireporte</h1>
			<fieldset>
				<legend>Reporte de ejercicios anteriores</legend>

		<table border="0" width="1028" height="10">
			<tr>
				<td align="right" >Partida (Objeto de gasto):</td>
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
				<td align="right">Unidad Ejecutora:</td>
<!--				<td><input id="cUnidadEjecutora"  name="cUnidadEjecutora"  value="" size="2"  maxlength="5"/></td>-->
					<td><input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
<!--				<td><select id="cUnidadEjecutora" name="cUnidadEjecutora" >
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select>
				</td>-->
			</tr>
			<tr>
				<td align="right">Fuente Financiamiento:</td>
<!--				<td><input id="FuenteFinanciamiento"  name="FuenteFinanciamiento"  value="" size="2"  maxlength="5"/></td>-->
				<td><select id="FuenteFinanciamiento"  style="width: 305px" name="FuenteFinanciamiento">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
			</tr>
			
			<tr>
				<td align="right">Periodo:</td>
			<td><select id="EjercicioFiscal3" name="EjercicioFiscal3">
<!-- 					<option value="2010">2010</option> -->
<!-- 					<option value="2011">2011</option> -->
<!-- 					<option value="2012">2012</option> -->
<!-- 				    <option value="2013">2013</option> -->
					<option value="xx" selected>2012</option>
				</select> a: 
				<select id="EjercicioFiscal2" name="EjercicioFiscal2">
<!-- 					<option value="2010">2010</option> -->
<!-- 					<option value="2011">2011</option> -->
<!-- 					<option value="2012">2012</option> -->
<!-- 				    <option value="2013">2013</option> -->
					<option value="xx" selected>2013</option>
				</select>
				
				</td> 
				
			</tr>
			
			<tr>
						<td>
											<tr>
									<td align="right">Capitulo:</td>
									<td><input type="checkbox"
										onclick="seleccionarTodosCapitulos()" id="seleccionaCapitulos">Seleccionar Todo</td>
								</tr>
								<tr>
									<td align="right"><input type="checkbox" id="10000" name="CAPITULO" value="1"
										class="capitulo" onclick="validaChechk();">1000</td>
									<td><input type="checkbox" id="20000" name="CAPITULO" value="2"
										class="capitulo" onclick="validaChechk();">2000</td>
									
								</tr>
								<tr>
									<td align="right"><input type="checkbox" id="30000" name="CAPITULO" value="3"
										class="capitulo" onclick="validaChechk();">3000</td>
									<td><input type="checkbox" id="40000" name="CAPITULO" value="4"
										class="capitulo" onclick="validaChechk();">4000</td>
									
								</tr>
								<tr>
									<td align="right"><input type="checkbox" id="50000" name="CAPITULO" value="5"
										class="capitulo" onclick="validaChechk();">5000</td>
										
									<td><input type="checkbox" id="60000" name="CAPITULO" value="6"
										class="capitulo" onclick="validaChechk();" >6000</td>
								</tr>	
						</td>
			</tr>
				
	</table>
	<br></br>
	
	
	
	<table align="center"  height="10">
		<tr>
			<td>	
				<input TYPE="button" id="cc" name="cc" value="Generar Reporte" onclick="generarReporteAnterior()"/>
				<input type="button"  id="Limpiar3" name="Limpiar3" value="Limpiar" onclick="limpiarDatos()" />	
			</td>
		</tr>
	</table>
		
	<br></br>
	</fieldset>	
	</div>	
			
	</form>
</body>
</html>