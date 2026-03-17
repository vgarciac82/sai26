<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%
	/**
	* versión MODIFICADA del MultiReporte.jsp
	* modificada por Martha Aurora Sánchez Valdivieso
	* para SYC Constructores de Sistemas SA de CV
	* desarrollo gestion_conagua_sif
	* México D.F. 26/07/2012 - 14/08/2012
	*/
			Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
			Empleado e = new Empleado();
			EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic("jdbc/gestion");
			e.setClaveUsuario(usuario.getLogin());
			e = ebl.getEmpleado(e);
			EmpleadoArea ea = new EmpleadoArea();
			ea.setId(e.getClaveArea());
			ea = ebl.getEmpleadoArea(ea);
			String cEjercicio = "2013";
			String Control[] = { "EjercicioFiscal"
								,"RamoEP"
								,"UnidadResponsableEP"
								,"GrupoFuncional"
								,"Funcion"
								,"SubFuncion"
								,"ProgramaGeneral"
								,"ActividadInstitucional"
								,"ProgramaPresupuestario"
								,"Partida"
								,"TipoGasto"
								,"FuenteFinanciamiento"
								,"EntidadFederativa"
								,"Cartera"
								,"cUnidadEjecutora"
								,"UnidadNormativa"};
		%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Reporte Creaci&oacute;n del Presupuesto</title>
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
		<script type="text/javascript">
			function limpiarSesion(){
				window.location.href="ReporteCreacionDelPresupuesto.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
			}

			function Grid(){
				window.open('MultiReporteGridAnteproyecto.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=500px');
			}

			function fnPrintTituloReporte() {
				var principal			   = parent.parent.document;
				var asunto				   = document.datawork;

				var ep 					   = $.trim(document.getElementById('ep').value);

				var EjercicioFiscal		   = $.trim(document.getElementById('EjercicioFiscal').value);
				var UnidadEjecutora 	   = <%=ea.getAreaPadre()%>;
				var RamoEP				   = $.trim(ep==""?document.getElementById('RamoEP').value : "");
				var UnidadResponsableEP	   = $.trim(ep==""?document.getElementById('UnidadResponsableEP').value : "");
				var GrupoFuncional		   = $.trim(ep==""?document.getElementById('GrupoFuncional').value : "");
				var Funcion				   = $.trim(ep==""?document.getElementById('Funcion').value : "");
				var SubFuncion			   = $.trim(ep==""?document.getElementById('SubFuncion').value : "");
				var ProgramaGeneral		   = $.trim(ep==""?document.getElementById('ProgramaGeneral').value : "");
				var ProgramaPresupuestario = $.trim(ep==""?document.getElementById('ProgramaPresupuestario').value : "");
				var ActividadInstitucional = $.trim(ep==""?document.getElementById('ActividadInstitucional').value : "");
				var Partida				   = $.trim(ep==""?document.getElementById('Partida').value : "");
				var TipoGasto			   = $.trim(ep==""?document.getElementById('TipoGasto').value : "");
				var FuenteFinanciamiento   = $.trim(ep==""?document.getElementById('FuenteFinanciamiento').value : "");
				var EntidadFederativa 	   = $.trim(ep==""?document.getElementById('EntidadFederativa').value : "");
				var Cartera 			   = $.trim(ep==""?document.getElementById('Cartera').value : "");
				var UnidadNormativa 	   = $.trim(ep==""?document.getElementById('UnidadNormativa').value : "");
				var cUnidadEjecutora 	   = $.trim(ep==""?document.getElementById('cUnidadEjecutora').value : "");

				var cOrderBy			   = document.getElementById('orderBy').value;
				var cGroupBy			   = document.getElementById('groupBy').value;

				var Usuario				   = $.trim("<%=usuario.getLogin()%>");

				var cadena				   = "";
				var filtro				   = "";

				if ( document.getElementById('calculado').checked){
					cadena= cadena == ""? document.getElementById('calculado').value : cadena + "," + document.getElementById('calculado').value;
					filtro= filtro == ""?  "Calculado" : filtro + ", Calculado";
				};
				if ( document.getElementById('optimo').checked){
					cadena= cadena == ""? document.getElementById('optimo').value : cadena + "," + document.getElementById('optimo').value;
					filtro= filtro == ""?  "Optimo" : filtro + ", Optimo";
				};
				if ( document.getElementById('irreductible').checked){
					cadena= cadena == ""? document.getElementById('irreductible').value : cadena + "," + document.getElementById('irreductible').value ;
					filtro= filtro == ""?  "Irreductible" : filtro + ", Irreductible";
				};
				if (filtro == ""){
					filtro="<tr class=\"alternateRow\"><td colspan=\"4\"><i>Presupuesto: Todo</i></td></tr>";
				} else {
					filtro=	"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Presupuesto: " + filtro + "</i></td></tr>";
				};

				if (ep==""){
					filtro=filtro+(EjercicioFiscal==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Ejercicio Fiscal: " + document.getElementById('EjercicioFiscal').value + "</i></td></tr>":"");
					filtro=filtro+(ProgramaPresupuestario==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Programa Presupuestario: " + document.getElementById('hProgPresupuestarioAnteproyecto').value + "</i></td></tr>":"");
					filtro=filtro+(RamoEP==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Ramo: " + document.getElementById('RamoEP').value + "</i></td></tr>":"");
					filtro=filtro+(Partida==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Partida: " + document.getElementById('hPartidaAnteproyecto').value + "</i></td></tr>":"");
					filtro=filtro+(UnidadResponsableEP==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Unidad Responsable: " + document.getElementById('hUnidadResponsableEP').value + "</i></td></tr>":"");
					filtro=filtro+(TipoGasto==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Tipo de Gasto: " + document.getElementById('TipoGasto').value + "</i></td></tr>":"");
					filtro=filtro+(GrupoFuncional==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Grupo Funcional: " + document.getElementById('GrupoFuncional').value + "</i></td></tr>":"");
					filtro=filtro+(FuenteFinanciamiento==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Fuente Financiamiento: " + document.getElementById('FuenteFinanciamiento').value + "</i></td></tr>":"");
					filtro=filtro+(Funcion==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Funcion: " + document.getElementById('Funcion').value + "</i></td></tr>":"");
					filtro=filtro+(EntidadFederativa==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Entidad Federativa: " + document.getElementById('EntidadFederativa').value + "</i></td></tr>":"");
					filtro=filtro+(SubFuncion==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>SubFuncion: " + document.getElementById('SubFuncion').value + "</i></td></tr>":"");
					filtro=filtro+(Cartera==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Cartera: " + document.getElementById('hCarteraAnteproyecto').value + "</i></td></tr>":"");
					filtro=filtro+(ProgramaGeneral==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Programa General: " + document.getElementById('ProgramaGeneral').value + "</i></td></tr>":"");
					filtro=filtro+(ActividadInstitucional==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Actividad Institucional: " + document.getElementById('ActividadInstitucional').value + "</i></td></tr>":"");
					filtro=filtro+(cUnidadEjecutora==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Unidad Ejecutora: " + document.getElementById('hcUnidadEjecutora').value + "</i></td></tr>":"");
					filtro=filtro+(UnidadNormativa==""?"<tr class=\"alternateRow\"><td colspan=\"4\"><i>Unidad Normativa: " + document.getElementById('hUR_Normativa').value + "</i></td></tr>":"");
				} else {
					filtro=filtro + "<tr class=\"alternateRow\"><td colspan=\"4\"><i>Estructura Programatica: " + document.getElementById('ep').value + "</i></td></tr>";
				};

				Cuenta = cadena;

				var param = "id=<%=request.getParameter("id")%>"
							+ "&ep=" + ep
							+ "&EjercicioFiscal=" + EjercicioFiscal
							+ "&RamoEP=" + RamoEP
							+ "&UnidadResponsableEP=" + UnidadResponsableEP
							+ "&GrupoFuncional=" + GrupoFuncional
							+ "&Funcion=" + Funcion
							+ "&SubFuncion=" + SubFuncion
							+ "&ProgramaGeneral=" + ProgramaGeneral
							+ "&ProgramaPresupuestario=" + ProgramaPresupuestario
							+ "&ActividadInstitucional=" + ActividadInstitucional
							+ "&Partida=" + Partida
							+ "&TipoGasto=" + TipoGasto
							+ "&FuenteFinanciamiento=" + FuenteFinanciamiento
							+ "&EntidadFederativa=" + EntidadFederativa
							+ "&Cartera=" + Cartera
							+ "&UnidadEjecutora=" + cUnidadEjecutora
							+ "&UnidadNormativa=" + UnidadNormativa
							+ "&cOrderBy=" + cOrderBy
							+ "&cGroupBy=" + cGroupBy
							+ "&Cuenta=" + Cuenta
							+ "&Usuario=" + Usuario
							+ "&Filtro=" + filtro;

				if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNormativa=="" ){
					alert("Capturar algun dato");
					return false;
				} else {
					//alert("antes de ejecutar MultiReporteResultado.jsp");
//					parent.frames['resultado'].location.href = "MultiReporteResultado.jsp?" + param;
					$("#GenerarReporte").attr("disabled", true);
					$("#Limpiar").attr("disabled", true);
					window.open('resultadoCreacionDelPresupuesto.jsp?'+ param, 'Resultado','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
					$("#GenerarReporte").attr("disabled", false);
					$("#Limpiar").attr("disabled", false);
					return true;
				}
			}

			$(document).ready(function(){
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();

				$(this).ajaxForm({
					dataType:  "json",
					success: formSubmited
				});

				$("#GenerarReporte").attr("disabled", true);
				$("#Limpiar").attr("disabled", true);

				// Inicializaciones CRUD
				//querySelectPost("catalogoEjercicioFiscalRead", "EjercicioFiscal");
				querySelectPost("catalogoRamoRead", "RamoEP");
				querySelectPost("catalogoEntidadFederativaRead", "EntidadFederativa");
				querySelectPost("catalogoAIAnteproyectoRead", "ActividadInstitucional");
//				querySelectPost("catalogoCarteraAnteproyectoRead", "Cartera");
				querySelectPost("catalogoFuncionAnteproyectoRead", "Funcion");
//				querySelectPost("catalogoProgramaPresupuestarioAnteproyectoRead", "ProgramaPresupuestario");
				querySelectPost("catalogoSubfuncionAnteproyectoRead", "SubFuncion");			
				querySelectPost("catalogoGpoFuncionalAnteproyectoRead", "GrupoFuncional");
				querySelectPost("catalogoProgGralAnteproyectoRead", "ProgramaGeneral");
				querySelectPost("catalogoFuenteFinanciamientoAnteproyectoRead", "FuenteFinanciamiento");
				querySelectPost("catalogoTipoGastoAnteproyectoRead", "TipoGasto");
//				querySelectPost("catalogoPartidaAnteproyectoRead", "Partida");
//				querySelectPost("catalogoUnidadResponsableRead", "cUnidadEjecutora");
//				querySelectPost("catalogoUnidadResponsableRead", "UnidadNormativa");
//				querySelectPost("catalogoUnidadResponsableRead", "UnidadResponsableEP");
//				querySelectPost("catalogoClaveCNARead", "ClaveCNA");
//				querySelectPost("catalogoSubCuentaRead", "ep");
				$("#GenerarReporte").attr("disabled", false);
				$("#Limpiar").attr("disabled", false);

				/*querySelectPost("EstadosRead", "cIdEntidadFederativaFiscal");
				querySelectPost("EstadosRead", "cIdEntidadFederativaActual");
				querySelectPost("MunicipiosFiscalRead", "cDelegacionFiscal");
				querySelectPost("MunicipiosActualRead", "cDelegacionActual");*/
//				queryFormPost("BeneficiarioRead");

				$('#ep').change(function() {
				});

				$( "#GrupoFuncional" ).change(function() {
					querySelectPost("catalogoFuncionAnteproyectoRead", "Funcion");
				});

				$( "#Funcion" ).change(function() {
					querySelectPost("catalogoSubfuncionAnteproyectoRead", "SubFuncion");
				});

				$( "#RamoEP" ).change(function() {
//					querySelectPost("catalogoUnidadResponsableRead", "cUnidadEjecutora");
//					querySelectPost("catalogoUnidadResponsableRead", "UnidadNormativa");
//					querySelectPost("catalogoUnidadResponsableRead", "UnidadResponsableEP");
				});
			});

			function formSubmited() {
				alert("Beneficiario enviado!");
			}

		</script>
	</head>
	<body background="../imagenes/steel_BG.gif" style="width: 97%">
		<form id="frmCreacionPresupuesto" name="frmCreacionPresupuesto" method="post" target="formulario" action="./ReporteCreacionDelPresupuesto.jsp?select=u_login">
			<div class="Contenido" style="height: 10%">
				<table class="TituloRutaCA"  height="10">
					<tr>
						<td>
							<img src="../imagenes/iconos/reportes.png" alt="" width="16" height="16">
							<font color="#FFFFFF">
								<strong>Reporte Creaci&oacute;n del Presupuesto.</strong>
							</font>
						</td>
					</tr>
				</table>
			</div>
			<table border="0" width="1028" height="10">
				<tr>
					<td style="width:20%; height:10"><input type="hidden" id="UnidadEjecutora" name="UnidadEjecutora" value=""/></td>
					<td style="width:40%; height:10"></td>
					<td style="width:20%; height:10"><input type="hidden" id="nOrden" name="nOrden" value=""/></td>
					<td style="width:40%; height:10; text-align:left"></td>
				</tr>
				<tr>
					<td align="right">Estructura Programática:</td>
					<td><input id="ep" name="ep" type="text"   value="" size="64" maxlength="64" class=""/></td>
					<td><input type="button" value="..." onclick="Grid()" /></td>
				</tr>
				<tr>
					<td align="right" >Ejercicio Fiscal:</td>
					<td><input align="right" type="text" id="EjercicioFiscal" name="EjercicioFiscal" value="2013" readOnly = "readOnly" ></td>
					<td align="right">Programa Presupuestario:</td>
					<td>
						<input id="hProgPresupuestarioAnteproyecto" name="hProgPresupuestarioAnteproyecto" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
						<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden"   value="" size="5" maxlength="5" class=""/>
					</td>
				</tr>
				<tr>
					<td align="right">Ramo:</td>
					<td align="left" width="75%">
						<select id="RamoEP" name="RamoEP">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>

					<td align="right" >Partida:</td>
					<td>
						<input id="hPartidaAnteproyecto" name="hPartidaAnteproyecto" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
						<input id="Partida" name="Partida" type="hidden"   value="" size="5" maxlength="5" class=""/>
					</td>
				</tr>
				<tr>
					<td align="right">Unidad Responsable:</td>
					<td>
						<input id="hUnidadResponsableEP" name="hUnidadResponsableEP" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
						<input id="UnidadResponsableEP" name="UnidadResponsableEP" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
					<td align="right">Tipo Gasto:</td>
					<td>
						<select id="TipoGasto" name="TipoGasto">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">Grupo Funcional:</td>
					<td>
						<select id="GrupoFuncional" name="GrupoFuncional" >
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
					<td align="right">Fuente Financiamiento:</td>
					<td>
						<select id="FuenteFinanciamiento" name="FuenteFinanciamiento">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">Funcion:</td>
					<td>
						<select id="Funcion" name="Funcion">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
					<td align="right">Entidad Federativa</td>
					<td>
						<select id="EntidadFederativa" name="EntidadFederativa">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">SubFuncion:</td>
					<td>
						<select id="SubFuncion" name="SubFuncion">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
					<td align="right">Cartera:</td>
					<td>
						<input id="hCarteraAnteproyecto" name="hCarteraAnteproyecto" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
						<input id="Cartera" name="Cartera" type="hidden"   value="" size="5" maxlength="5" class=""/>
					</td>
				</tr>
				<tr>
					<td align="right">Programa General:</td>
					<td>
						<select id="ProgramaGeneral" name="ProgramaGeneral">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
					<td align="right">Unidad Ejecutora:</td>
					<td>
						<input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
						<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden"   value="" size="5" maxlength="5" class=""/>
					</td>
				</tr>
				<tr>
					<td align="right">Actividad Institucional:</td>
					<td>
						<select id="ActividadInstitucional" name="ActividadInstitucional" >
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
					</td>
					<td align="right">Unidad Normativa:</td>
					<td>
						<input id="hUR_Normativa" name="hUR_Normativa" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
						<input id="UnidadNormativa" name="UnidadNormativa" type="hidden"   value="" size="5" maxlength="5" class=""/>
					</td>
					
				</tr>
			</table>
			<table border="0" width="1024" height="10">
				<tr>
					<td align="right" width="10%">Ordenado por:</td>
					<td>
						<select name="orderBy" id="orderBy" onchange="">
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
					</td>
					<td align="right" width="10%">Agrupado por:</td>
					<td>
						<select name="groupBy" id="groupBy" onchange="">
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
					</td>
				</tr>
				<tr>
					<td>
						<br/>
					</td>
				</tr>
			</table>
			<table border="0" width="1028"  height="10">
				<tr>
					<td align="right">Presupuesto:</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="checkbox"  id="calculado"  name="Cuenta" value="calculado" />CALCULADO<br></td>
					<td><input type="checkbox"  id="optimo" name="Cuenta" value="optimo"/>ÓPTIMO<br></td>
					<td><input type="checkbox"  id="irreductible" name="Cuenta" value="irreductible"/>IRREDUCTIBLE<br></td>	
				</tr>
			</table>
 			<table align="center"  height="10">
				<tr>
					<td>
						<input type="button" id="GenerarReporte" name="GenerarReporte" value="Generar Reporte" onClick="return fnPrintTituloReporte();">
						<input type="button"  id="Limpiar" name="Limpiar" value="Limpiar" onClick="limpiarSesion();"/>
					</td>
				</tr>
				<tr>
				</tr>
			</table>
		</form>
	</body>
</html>