<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>Suspensi&oacute;n de Pagos Obra P&uacute;blica</title>
		<link href="../admin/js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css"
			rel="stylesheet">
		<link href="css/demo_page.css" rel="stylesheet">
		<link href="css/demo_table_jui.css" rel="stylesheet">
		<script type="text/javascript" src="../admin/js/jq9/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript"
			src="../admin/js/jq9/jquery-ui-1.9.0.custom.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/SuspendePagosOP.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
				init();
			});
		</script>
	</head>
	<body id="dt_example">
		<form action="../SuspensionPagos" method="post" id="mainForm">
			<input type="hidden" value="" id="cEjercicio" name="cEjercicio">
			<input type="hidden" value="" id="cCentroContable"
				name="cCentroContable">
			<input type="hidden" value="" id="folioSAI" name="folioSAI">
			<input type="hidden" value="" id="estatusPago" name="estatusPago">
			<h1>
				<label id="titulo">
					Suspensi&oacute;n de Pagos
				</label>
			</h1>
			<div id="container" class="container SyCData">
				<h5>
					Ingrese la siguiente informaci&oacute;n:
				</h5>
				<table>
					<tr>
						<td colspan="2" align="right">
							N&uacute;mero de Contrato:
						</td>
						<td colspan="2" align="left">
							<input type="text" id="cveContrato" size="30">
						</td>
					</tr>
					<tr>
						<td colspan="4" align="right">
							<input type="button" id="searachButton" value="Buscar">
						</td>
					</tr>
				</table>
				<br />
				<h6>
					Seleccione el contrato al que desea suspender/reiniciar los pagos.
				</h6>
				<table id="resultTable" width="100%">
					<thead>
						<tr>
							<th>
								No. Contrato
							</th>
							<th>
								Beneficiario
							</th>
							<th>
								Monto
							</th>
							<th>
								Folio SAI
							</th>
							<th>
								&nbsp;
							</th>
							<th>
								Estatus
							</th>
							<th>
								C. Contable
							</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div id="question" style="display: none; cursor: default">
					<h4>
						&#191;Desea continuar?
					</h4>
					<br>
					<h5>
						<label id="msgAdvertencia"></label>
					</h5>
					<input type="button" id="yes" value="Continuar" />
					<input type="button" id="no" value="Cancelar" />
				</div>
			</div>
		</form>
	</body>
</html>