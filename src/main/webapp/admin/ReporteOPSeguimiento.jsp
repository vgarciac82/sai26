<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>Reporte de Seguimiento de Contratos</title>
		<link href="js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css"
			rel="stylesheet">
		<link href="../Generador/css/demo_page.css" rel="stylesheet">
		<link href="../Generador/css/demo_table_jui.css" rel="stylesheet">
		<link href="../Ayudas/css/autocompleta.css" rel="stylesheet"></link>

		<script type="text/javascript"
			src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/jq9/jquery-ui-1.9.0.custom.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="js/ReporteOPFormato10.js"></script>
		<script type="text/javascript" src="js/ReporteOPformatoArea.js"></script>											
		

		<script type="text/javascript">
			$(document).ready(function() {
				init();
			});
		</script>
		<style type="text/css">
			.calendar_1 {					
			background-color: #F5F5f5;
			padding-left: 6px;
			}
			.calendar_2 {				
				text-align: right;
				background-color: #E2E4FF;
				padding-left: 6px;
				padding-right: 6px;
			}
		</style>
	</head>
	<body id="dt_example">
		<form method="get" action="../ObraPublica/reportes" id="frmOP">
			<h1>
				<label id="titulo">
					Reporte de Seguimiento de Contratos de Obra Pública
				</label>
			</h1>
			<div id="container" class="container SyCData">
				<div>
					<table>
						<tbody>
							<tr>
								<td align="left">
									<label style="font-weight: bold;">
										Ingrese informaci&oacute;n en los filtros para generar el
										reporte
									</label>
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div>
					<table id="reportTbl">
						<tbody>
							<tr>
								<td align="left">
									Unidad Responsable:
								</td>
								<td align="left">
									Area Contable:
								</td>
							</tr>
							<tr>
								<td align="left">
									<input id="opUnidadNormativa" name="opUnidadNormativa"
										type="text" value="" size="40" maxlength="50"
										class="AyudaSyC autoCompletaSyC" />
								</td>
								<td align="left">
									<select id="cAreaContable" name="cAreaContable"
										style="width: 400px;">
										<option value="">
											Seleccione una opci&oacute;n
										</option>
									</select>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<table border="0" align="center" cellSpacing="0">
										<tr>
											<td colspan="4" align="center" class="calendar_1">Periodo</td>
										</tr>
										<tr>
											<td class="calendar_2" style="text-align: right">
												Fecha Inicio:
											</td>
											<td class="calendar_2">
												<input type="text" name="fechaI" id="fechaI" readonly="readonly" size="10" maxlength="10"/>
											</td>
											<td class="calendar_2">
												Fecha Fin:
											</td>
											<td class="calendar_2">
												<input type="text" name="fechaF" id="fechaF" readonly="readonly" size="10" maxlength="10"/>
											</td>
										</tr>
									</table>
								</td>
							</tr>							
							<tr>
								<td colspan="2" align="right">
									<input type="button" id="limpiarBtn" value="Limpiar">
									<input type="button" id="generarBtn" value="Generar Reporte">									
								</td>
							</tr>							
						</tbody>
					</table>
				</div>
			</div>
			<input type="hidden" name="idunidadresponsable"
				id="idunidadresponsable" value="" />
			<input type="hidden" name="rt" id="rt" value="REPORTE_SEGUIMIENTO" />
		</form>
	</body>
</html>