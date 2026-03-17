<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Carga Factura Obra Publica</title>

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

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">

	<div id="container" class="container">

		<div id="uploadFacturasDiv">
			<iframe id="uploadFacturasFrm"
				src="UploadCFDIContrato.jsp?TipoContrato=OB&IDContrato=&RFC="
				align="top" frameborder="0" height="360" width="510"> </iframe>
		</div>
		<div id="facturasCapturadasDiv">
			<table width="100%">
				<tr id="uploadFacturasTR">
					<td align="right"><a href="#"
						onclick="togleDivFacts(1);return false;">Cargar Facturas</a></td>
				</tr>
				<tr>
					<td>
						<fieldset>
							<legend>Facturas Capturadas.</legend>
							<table id="grdValidaFacturas">
								<thead>
									<tr>
										<th>Factura</th>
										<th>Importe Bruto</th>
										<th>Impuestos</th>
										<th>Total</th>
									</tr>
								</thead>
							</table>
							Total de las facturas:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
								type="text" style="text-align: right;" name="mTotalFacturaV"
								id="mTotalFacturaV" value="0.00" size="12" maxlength="12"
								readonly />
						</fieldset>
					</td>
				</tr>
			</table>
		</div>
	</div>
	</body>
</html>