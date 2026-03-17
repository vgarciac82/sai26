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
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$('#dt_catalogo').dataTable({
			"bScrollCollapse" : true,
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true
		});

	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<div id="container" class="container">
			<h1>Catalogo Entidad Federativa / Unidad Ejecutora.</h1>
			<fieldset>
				<legend>Captura Manual</legend>
				<table align="center">
					<tr>
						<td align="center" colspan="6">Relacion Entidad Federativa
							/ Unidad Ejecutora:<input type="text" id="RelacionEF_UE"
							size="17" class="AyudaSyC">
						</td>
					</tr>
					<tr>
						<td align="right">Entidad Federativa:</td>
						<td align="left"><input type="text" size="3"
							id="ProgramaPresupuestario" name="EntidadFederativa"
							class="AyudaSyC">
						</td>
						<td align="right">Unidad Ejecutora:</td>
						<td align="left"><input type="text" size="4" id="UE"
							name="UE" class="AyudaSyC"></td>
					</tr>

					<tr>
						<td colspan="6" align="right"><input type="button"
							value="Guardar"></td>
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
				<table id="dt_catalogo" class="display" cellspacing="0"
					cellpadding="2" align="center">
					<thead>
						<tr>
							<th>Entidad Federativa</th>
							<th>Unidad Ejecutora</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</form>
</body>
</html>