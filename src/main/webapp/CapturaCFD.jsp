<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ejemplo carga de CB</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="Generador/css/demo_page.css"></link>

<script type="text/javascript" src="Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="Generador/js/crud.js"></script>
<script type="text/javascript" src="Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/jsquery.js"></script>
<script type="text/javascript" src="Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/catalogo/general.js"></script>
<script type="text/javascript" src="Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
	$( document ).ready( function() {
		$( "#FechaEmision" ).datepicker( {
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "Generador/images/calendar.gif",
		buttonImageOnly : true
		} );
	} );
</script>

</head>

<body id="dt_example" bottomMargin="0" leftMargin="0" topMargin="0">
	<form id="FormContrato" name="FormContrato" method="post" action="interface/SolicitudCFDI">
		<div id="container" class="container" style="width: 99%">
			<h1>Complemento de Datos para Facturacion</h1>
			<table width="100%">
				<tr>
					<td align="left" colspan="2" width="50%">
							Tipo de Comprobante 
							<select id="tipoComprobante" name="tipoComprobante">
							<optgroup label="Comprobantes Disponibles">
								<option>Donativos FFM</option>
								<option>Donativos</option>
								<option>Factura</option>
								<option>Nota de Credito</option>
								<option>Nota de Credito FFM</option>
								<option>Recibo de Dinero</option>
								<option>Recibo de Dinero</option>
								<option>Recibo de Dinero FFM</option>
								<option>Recibo de Dinero FFM Varios</option>
							</optgroup>
					</select>
					</td>
					<td align="right" colspan="2" width="50%">Fecha:<input type="text" size="10" id="FechaEmision" name="FechaEmision" readonly="readonly" style="background: gray;" value="01/10/2017"></td>
				</tr>
				<tr>
					<td colspan="4">
						<fieldset>
							<legend>Receptor</legend>
							<table>
								<tr>
									<td align="right">RFC:</td>
									<td colspan="5" align="left"><input type="text" readonly="readonly" size="20" value="XXXX-111111-ZZZ" style="background: gray;" id="RFCCliente" name="RFCCliente"></td>
								</tr>
								<tr>
									<td align="right">Nombre:</td>
									<td align="left"><input type="text" readonly="readonly" size="20" value="Nombre" style="background: gray;" id="nombreCliente" name="nombreCliente"></td>
									<td align="right">Apellido Paterno:</td>
									<td align="left"><input type="text" readonly="readonly" size="20" value="Apellido Paterno" style="background: gray;" id="paternoCliente" name="paternoCliente"></td>
									<td align="right">Apellido Materno:</td>
									<td align="left"><input type="text" readonly="readonly" size="20" value="Apellido Materno" style="background: gray;" id="maternoCliente" name="maternoCliente"></td>
								</tr>
								<tr>
									<td>Domicilio Fiscal:</td>
									<td colspan="5"><input type="text" id="domicilioFiscal" name="domicilioFiscal" readonly="readonly" size="60" value="Domicili Fiscal Registrado" style="background:gray;" >
									</td>
								</tr>
							</table>
						</fieldset></td>
				</tr>
				<tr>
					<td colspan="4">
						<fieldset>
							<legend>Concepto</legend>
							<textarea rows="5" cols="80" id="concepto" name="concepto">Texto libre o se puede leer del proceso de custf</textarea>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<fieldset>
							<legend>Pago</legend>
							<table>
								<tr>
									<td align="right">Metodo de Pago:</td>
									<td align="left"><select id="metodoPago" name="metodoPago">
											<optgroup label="Metodos de Pago">
												<option>Efectivo</option>
												<option>Transferencia Electronica de Fondos</option>
												<option>Cheque Nominativo</option>
												<option>Tarjeta de Credito</option>
												<option>Tarjeta de Debito</option>
											</optgroup>
									</select>
									</td>
									<td align="right">Forma de Pago:</td>
									<td align="left"><input type="text" id="formaPago" name="formaPago" value="Pago en una sola exhibicion" readonly="readonly" style="background:gray;" size="30"></td>
									<td align="right">No. de Cuenta:</td>
									<td align="left"><input type="text" id="nCuenta" name="nCuenta" value="110022334400110022" readonly="readonly" style="background:gray;" size="20"></td>
								</tr>
								<tr>
									<td align="right">Subtotal:</td>
									<td align="left"><input type="text" id="subTotal" name="subTotal" value="$100.00" readonly="readonly" style="background:gray;" size="15"></td>
									<td colspan="4">&nbsp;</td>
								</tr>
								<tr>
									<td align="right">Impuesto:</td>
									<td align="left"><input type="text" id="impuesto" name="impuesto" value="$16.00" readonly="readonly" style="background:gray;" size="15"></td>
									<td colspan="4">&nbsp;</td>
								</tr>
								<tr>
									<td align="right">Descuento</td>
									<td align="left"><input type="text"  id="descuento" name="descuento" value="$0.00" readonly="readonly" style="background:gray;" size="15"></td>
									<td colspan="4">&nbsp;</td>
								</tr>
								<tr>
									<td align="right">Total:</td>
									<td align="left"><input type="text" id="total" name="total" value="$116.00" readonly="readonly" style="background:gray;" size="15"></td>
									<td colspan="4">&nbsp;</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td colspan="4" align="right">
						<input type="submit" value="Solicitar Factura">
					</td>
				</tr>
			</table>
		</div>
	</form>
</body>

</html>