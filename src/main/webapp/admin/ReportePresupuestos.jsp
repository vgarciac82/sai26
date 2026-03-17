<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>

<!-- Estilos estandar para los controles JQuery /***************************Version 1.0 *****************************************************/-->

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
<style type="text/css">
.notEditable {
	background-color: #CCCCCC;
}
</style>
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
<script type="text/javascript" src="../js/ReportePresupuestos.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form action="../presupuestos/ReportePresupuestos" method="get"
		target="_blank" id="descargaFrm" name="descargaFrm">
		<input type="hidden" value="DESCARGA_EXCEL" id="accion" name="accion">
	</form>

	<form id="formReporte" name="formReporte"
		action="../presupuestos/ReportePresupuestos?accion=GENERA_REPORTE"
		method="post" target="_blank">
		<div id="container" class="container">
			<h1>Formatos para la toma de decisi&oacute;n</h1>
			<table width="100%" align="center">
				<tr>
					<td align="right" colspan="4">&Uacute;ltima
						Congelaci&oacute;n: <input type="text" size="20"
						id="ULTIMA_CONGELACION" name="ULTIMA_CONGELACION" value=""
						readonly="readonly" class="notEditable">
					</td>
				</tr>
				<tr>
					<td align="right">Mes de Corte:</td>
					<td align="left"><select style="width: 150px;" id="MesCorte"
						name="MesCorte">
					</select></td>
					<td align="right">Tipo de Reporte:</td>
					<td align="left"><select id="TIPO_REPORTE" name="TIPO_REPORTE">
							<option value="CE">Estructura Econ&oacute;mica</option>
							<option value="SFN">Estructura Econ&oacute;mica y
								programa presupuestario</option>
							<option value="UN">Por Unidad Normativa y
								Clasificaci&oacute;n Econ&oacute;mica</option>
							<option value="UE">Por Unidad Ejecutora y
								Clasificaci&oacute;n Econ&oacute;mica</option>
							<option value="EF">Por Entidad Federativa, Grupo
								Funcional y Clasificaci&oacute;n Econ&oacute;mica</option>
					</select></td>
				</tr>
			</table>
			<table width="100%">
				<tr>
					<td>
						<fieldset>
							<legend>Variables Presupuestales</legend>
							<table align="center" width="100%">
								<tr>
									<td colspan="3" align="left"><input type="checkbox"
										onclick="seleccionarTodosMomentos()" id="seleccionaMomentos"
										checked="checked">Seleccionar Todo</td>
								</tr>
								<tr>
									<td><input type="checkbox" value="ORIGINAL"
										name="MOMENTO_PRESUPUESTAL" class="MP">Original</td>
									<td><input type="checkbox" value="MODIFICADO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Modificado</td>
									<td><input type="checkbox" value="EJERCIDO_PAGADO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Ejercido</td>
								</tr>
								<tr>
									<td><input type="checkbox" value="COMPROMETIDO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Comprometido</td>
									<td><input type="checkbox" value="DEVENGADO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Devengado</td>
									<td><input type="checkbox" value="DISPONIBLE_NETO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Disponible</td>
								</tr>
								<tr>
									<td><input type="checkbox" value="APARTADO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Apartado</td>
									<td><input type="checkbox" value="PRECOMPROMETIDO"
										name="MOMENTO_PRESUPUESTAL" class="MP">Precomprometido</td>
									<td>&nbsp;</td>
								</tr>
							</table>
						</fieldset></td>
					<td>
						<fieldset>
							<legend>Capitulos</legend>
							<table align="center" width="100%">
								<tr>
									<td colspan="3" align="left"><input type="checkbox"
										onclick="seleccionarTodosCapitulos()" id="seleccionaCapitulos"
										checked="checked">Seleccionar Todo</td>
								</tr>
								<tr>
									<td><input type="checkbox" name="CAPITULO" value="1000"
										class="capitulo">1000</td>
									<td><input type="checkbox" name="CAPITULO" value="2000"
										class="capitulo">2000</td>
									<td><input type="checkbox" name="CAPITULO" value="3000"
										class="capitulo">3000</td>
								</tr>
								<tr>
									<td><input type="checkbox" name="CAPITULO" value="4000"
										class="capitulo">4000</td>
									<td><input type="checkbox" name="CAPITULO" value="5000"
										class="capitulo">5000</td>
									<td><input type="checkbox" name="CAPITULO" value="6000"
										class="capitulo">6000</td>
								</tr>
							</table>
						</fieldset></td>
				</tr>
				<tr>
					<td align="right" colspan="2">
						<table>
							<tr>
								<td><input type="button" value="Congelar" id="CONGELAR">
									<input type="button" value="Liberar DB" id="LIBERAR"> <input
									type="button" value="Exportar DB" id="EXPORTAR_DB"> <input
									type="button" value="Generar Reporte" id="GENERAR"> <input
									type="button" value="Limpiar" id="LIMPIAR"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</form>
</body>
</html>
