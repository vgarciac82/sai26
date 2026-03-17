<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>

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

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">
	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$('#dt_catalogo').dataTable({
			"bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"sScrollXInner": "110%",
					"bScrollCollapse": true,	
					"bServerSide": true,   
			        sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vEP_PROGRAMA&qw=cFuncion=3 AND cSubFuncion='04'",
			        aoColumns: [
						{ sName: "relacion" },
						{ sName: "cGrupoFuncional" },
						{ sName: "cFuncion"},
						{ sName: "cSubFuncion"},
						{ sName: "cProgramaGeneral"},
						{ sName: "cActividadInstitucional"},
						{ sName: "cProgramaPresupuestario"}
					],
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
		});

		//Genera Tabs (Pestañas)
		$(".tabs").tabs();
		$("#buscaVal").val("4");
		queryFormPost("readPruebaCRUD", {async:false});
		querySelectPost("readCatalogoFunciones", "subfuncion");
		
		
	});
	
	function buscar(){
		$("#buscaVal").val($("#campo1").val());
		queryFormPost("readPruebaCRUD", {async:false});
	}
	function guardar(){
		
		$("#buscaVal").val($("#campo1").val()); 
		$("#existe").val("");
		
		queryFormPost(
			{
				queryName:"existePruebaCrud", 
				async:false,
				callback:function(){
					if( $("#existe").val() == ""  )
						//Forma Simple de llamar al CRUD
						queryFormPost("createPruebaCrud", {async:false});
					else
						queryFormPost("updatePruebaCrud", {async:false});
				}
			});
	}
	
	function borrar(){
		queryFormPost(
			{
				queryName:"deletePruebaCrud", 
				async:false,
				callback:function(){
					alert("Borrado");				
				}
			});
			
	}
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<input type="hidden" value="" id="buscaVal" name="buscaVal">
		<input type="hidden" value="" id="existe" name="existe">
		<div id="container" class="container">
			<h1>Catalogo Estructura Funcional / Programa</h1>
			<fieldset>
				<legend>Captura Manual</legend>
				<table align="center">
					<tr>
						<td align="center" colspan="6">Relacion Estructuroa Funcional
							/ Programa:
							<input type="text" id="campo1" name="campo1" size="20" class="AyudaSyC">
						</td>
					</tr>
					<tr>
						<td align="right">Grupo Funcional</td>
						<td align="left"><input type="text" size="2"
							id="campo2" name="campo2" class="AyudaSyC">
						</td>
						<td align="right">Función</td>
						<td align="left">
							<input type="text" size="2" id="campo3" name="campo3" class="AyudaSyC">
						</td>
						<td align="right">SubFunción</td>
						<td align="left">&nbsp;</td>
					</tr>
					<tr>
						<td>
							<select  id="subfuncion" name="subfuncion" style="width: 350px"></select>
						</td>
					</tr>
					<tr>
						<td align="right">Programa general</td>
						<td align="left"><input type="text" size="2"
							id="Programageneral" name="Programageneral" class="AyudaSyC">
						</td>
						<td align="right">Actividad Institucional</td>
						<td align="left"><input type="text" size="2"
							id="ActividadInstitucional" name="ActividadInstitucional"
							class="AyudaSyC">
						</td>
						<td align="right">Programa Presupuestario</td>
						<td align="left"><input type="text" size="2"
							id="ProgramaPresupuestario" name="ProgramaPresupuestario"
							class="AyudaSyC">
						</td>
					</tr>
					<tr>
						<td colspan="6" align="right"><input type="button"
							value="Guardar" onclick="guardar()">
							<input type="button"
							value="Buscar" onclick="buscar()">
							<input type="button"
							value="Borrar" onclick="borrar()">
							</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend> Carga Masiva </legend>
				<table align="center">
					<tr>
						<td align="right">Archivo Excel:</td>
						<td><input type="file" size="30" id="archivoCarga"> <input
							type="button" value="Cargar Catalogo"></td>
					</tr>
				</table>
			</fieldset>

			<div id="dv">
				<table id="dt_catalogo" class="display" cellspacing="0" cellpadding="2" align="center">
					<thead>
						<tr>
							<th>Relaci&oacute;n</th>
							<th>Grupo Funcional</th>
							<th>Funci&oacute;n</th>
							<th>Subfunci&oacute;n</th>
							<th>Programa general</th>
							<th>Actividad Institucional</th>
							<th>Programa Presupuestario</th>
						</tr>
					</thead>
				</table>
			</div>
			<div class="tabs">
				<ul>
					<li><a id="Link01" onClick="fnTabsClick('apartado')"
						href="#tabs-0">Justificaci&oacute;on de Compromiso</a>
					</li>
					<li><a id="Link02" onClick="fnTabsClick('precompromiso');"
						href="#tabs-1">Justificaci&oacute;on de Plazo</a>
					</li>
				</ul>
				<div id="tabs-0">
					<textarea rows="30" cols="60"></textarea>
				</div>
				<div id="tabs-1">
					<textarea rows="30" cols="60"></textarea>
				</div>
			</div>
		</div>
	</form>
</body>
</html>