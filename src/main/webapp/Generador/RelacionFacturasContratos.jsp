<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Contrato Factura.</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<style type="text/css">
</style>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/RelacionFacturasContratos.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form action="CFDI/cargaFacturaPago" method="post" id="formCFDI" enctype="multipart/form-data">
		<input type="hidden" id="cTipoContrato" name="cTipoContrato" value="OB">
		<div id="container" class="container">
			<span> <label style="font-weight:bold; font-size: 10px; text-align: right;">*Seleccione el contrato para visulizar su informacion </label>
			</span>
			<div id="InfoContrato">
				<table id="infoContrato">
					<tr>
						<td align="right">No. Contrato</td>
						<td align="left"><input type="text" name="cIDContratoObra" id="cIDContratoObra" onChange="complementaInformacion();" class="AyudaSyC " size="40" maxlength="40" readonly /></td>
					</tr>
					<tr>
						<td align="right">Beneficiario</td>
						<td align="left"><input type="text" name="cIdRFC" id="cIdRFC" readonly="readonly" size="15" maxlength="15" /> <input type="text" name="cobjetocontrato" id="cobjetocontrato" readonly="readonly" size="50" maxlength="50" /></td>
					</tr>
					<tr>
						<td align="right">Concepto</td>
						<td><textarea rows="8" cols="50" readonly="readonly" id="cObjetoContrato"></textarea></td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<table>
								<tr>
									<td align="right">Importe Bruto:</td>
									<td align="left"><input type="text" id="mImporte" value="" readonly="readonly"></td>
									<td align="right">Importe Impuestos:</td>
									<td align="left"><input type="text" id="mIVA" value="" readonly="readonly"></td>
									<td align="right">Importe Total:</td>
									<td align="left"><input type="text" id="mTotal" value="" readonly="readonly"></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
			<div id="divFacturasContrato">
				<fieldset>
					<legend>Facturas de Contrato</legend>
					<table id="dtFacturasContrato" class="display">
						<thead>
							<tr>
								<th width="35%">UUID</th>
								<th width="5%">RFC</th>
								<th width="15%">Importe Bruto</th>
								<th width="15%">Impuestos</th>
								<th width="15%">Total</th>
								<th width="15%">Remanente</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</fieldset>
			</div>
		</div>
	</form>
</body>

</html>