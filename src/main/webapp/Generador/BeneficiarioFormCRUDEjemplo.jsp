<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Beneficiario</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>

		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>

		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$('#tblTipoRFC').dataTable({
				"bJQueryUI" : true,
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : false,
				"bInfo" : false,
				"bAutoWidth" : false
			});
			$("#datepicker").datepicker({
				showOn : "button",
				buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
			$(".tabs").tabs();
			$("#frmBeneficiario").ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			

		});
		function formSubmited() {
                alert("Beneficiario enviado!");
            }
		function Hello() {
			// Para la llamada a la funcion querySelectPost
			// el primer campo del query nombrado (crud.xml) es
			// el que se usa para el contenido del tag OPTION y con los
			// demas campos formara una lista separada por pipes
			// (|) en el atributo VALUE del OPTION
			querySelectPost("catalogoTipoPersonaRFCRead", "cIdTipoRFC");
			querySelectPost("EstadosRead", "cIdEntidadFederativaFiscal");
			querySelectPost("EstadosRead", "cIdEntidadFederativaActual");
			// Pobla los campos del la forma con los valores del query nombrado
			// usando el nombre de las columnas como ID's de los campos
			queryFormPost("BeneficiarioRead");
		}
		</script>
	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<form id="frmBeneficiario" method="post">

				<h1>Beneficiario</h1><input type="submit" value="Enviar">
				<table border="0" align="center">
					<tr>
						<td align="right">Tipo de Persona:</td>
						<td>
							<select id="cIdTipoRFC" name="cIdTipoRFC" style="width: 20em;">
								<option value="Z:">A</option>
								<option value="Y:">B</option>
								<option value="X:">C</option>
								<option value="xx" selected>--</option>
							</select>
						</td>
						<td align="right">&nbsp;</td>
						<td colspan="3" align="right">
							<input type="checkbox" id="lRFCValido" name="lRFCValido" value="Milk">
							RFC V&aacute;lido &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C&oacute;digo SICOP:
							<input id="txtContrato2" name="txtContrato2" type="text" size="5" maxlength="5">
						</td>
					</tr>
					<tr>
						<td align="right">RFC:</td>
						<td>
							<input type="hidden" id="cIdRFC" name="cIdRFC" value=" AUP-941104-238">
							<input id="cIdRFC1" name="cIdRFC1" type="text" size="3" maxlength="4">
							-
							<input id="cIdRFC2" name="cIdRFC2" type="text" size="4" maxlength="6">
							-
							<input id="cIdRFC3" name="cIdRFC3" type="text" size="2" maxlength="3">
						</td>
						<td align="right">CURP:</td>
						<td colspan="3">
							<input id="cCURP" name="cCURP" type="text" size="23" maxlength="4">
						</td>
					</tr>
					<tr>
						<td align="right">A.Paterno:</td>
						<td>
							<input id="cApellidoPaternoPF" name="cApellidoPaternoPF" type="text" size="23" maxlength="30">
						</td>
						<td align="right">A.Materno:</td>
						<td>
							<input id="cApellidoMaternoPF" name="cApellidoMaternoPF" type="text" size="23" maxlength="30">
						</td>
						<td align="right">Nombre:</td>
						<td>
							<input id="cNombrePF" name="cNombrePF" type="text" size="23" maxlength="30">
						</td>
					</tr>
					<tr>
						<td align="right">Persona Moral:</td>
						<td colspan="5">
							<input id="cNombrePersonaMoral" name="cNombrePersonaMoral" type="text" size="106" maxlength="50">
						</td>
					</tr>
					<tr>
						<td colspan="6" align="right">
							<input type="button" onclick="Hello()" value="Cuentas Bancarias" />
						</td>
					</tr>
				</table>

				<h1>Tipo de RFC</h1>
				<table border="0" align="center">
					<tr>
						<td>Tipo de RFC:</td>
						<td rowspan="3" valign="bottom">
							<fieldset>
								<legend>Caracter&aacute;sticas especiales</legend>
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td><input type="checkbox" id="lCompensacionAdeudos" name="lCompensacionAdeudos"></td>
										<td>Compensaci&oacute;n de Adeudos (SICOM)</td>
									</tr>
									<tr>
										<td><input type="checkbox" id="lServiciosPersonalesNomina" name="lServiciosPersonalesNomina"></td>
										<td>Servicios Personales (N&oacute;mina)</td>
									</tr>
									<tr>
										<td><input type="checkbox" id="lServiciosPersonalesTerceros" name="lServiciosPersonalesTerceros"></td>
										<td>Servicios Personales (Terceros)</td>
									</tr>
									<tr>
										<td><input type="checkbox" id="lServiciosPersonalesISSSTE" name="lServiciosPersonalesISSSTE"></td>
										<td>Servicios Personales (ISSSTE)</td>
									</tr>
									<tr>
										<td><input type="checkbox" id="lOperacionAjena" name="lOperacionAjena"></td>
										<td>Operaciones Ajenas</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td>
							<select id="cIDTipoPersonaRFC" name="cIDTipoPersonaRFC" style="width: 20em;">
								<option value="Z:">A</option>
								<option value="Y:">B</option>
								<option value="X:">C</option>
								<option value="xx" selected>--</option>
							</select>
							<input type="button" onclick="Hello()" value="A" />
							<input type="button" onclick="Hello()" value="B" />
							<input type="button" onclick="Hello()" value="C" />
						</td>
					</tr>
					<tr>
						<td>
							<table id="tblTipoRFC" class="display">
								<thead>
									<tr>
										<th>Tipo RFC</th>
										<th>Descripci&oacute;n</th>
									</tr>
								</thead>
								<tbody>
									<tr class="odd gradeA">
										<td>A</td>
										<td>AA</td>
									</tr>
									<tr class="even gradeA">
										<td>B</td>
										<td>BB</td>
									</tr>
									<tr class="odd gradeA">
										<td>C</td>
										<td>CC</td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</table>

				<h1>Domicilio</h1>
				<div class="tabs" align="center">
					<ul>
						<li><a href="#tabs-1">Direcci&oacute;n Fiscal</a></li>
						<li><a href="#tabs-2">Direcci&oacute;n Actual</a></li>
					</ul>
					<div id="tabs-1">
						<table border="0">
							<tr>
								<td align="right">Entidad Federativa:</td>
								<td>
									<select id="cIdEntidadFederativaFiscal" name="cIdEntidadFederativaFiscal" style="width: 20em;">
										<option value="A:">A</option>
										<option value="B:">B</option>
										<option value="C:">C</option>
										<option value="xx" selected>--</option>
									</select>
								</td>
								<td align="right">&nbsp;</td>
								<td colspan="3" align="right">
									<input type="button" onclick="Hello()" value="?" />
								</td>
							</tr>
							<tr>
								<td align="right">Calle:</td>
								<td>
									<input id="cCalleFiscal" name="cCalleFiscal" type="text" size="30" maxlength="4">
								</td>
								<td align="right">No.:</td>
								<td>
									<input id="cNoDomicilioFiscal" name="cNoDomicilioFiscal" type="text" size="6" maxlength="10">
								</td>
								<td align="right">No.Interior:</td>
								<td>
									<input id="cNoInteriorDomicilioFiscal" name="cNoInteriorDomicilioFiscal" type="text" size="6" maxlength="10">
								</td>
							</tr>
							<tr>
								<td align="right">Otras se&ntilde;as:</td>
								<td colspan="5">
									<input id="cOtrosDatosFiscal" name="cOtrosDatosFiscal" type="text" size="80" maxlength="50">
								</td>
							</tr>
							<tr>
								<td align="right">Colonia o Manzana:</td>
								<td colspan="5">
									<input id="cColoniaFiscal" name="cColoniaFiscal" type="text" size="80" maxlength="50">
								</td>
							</tr>
							<tr>
								<td align="right">Delegaci&oacute;n:</td>
								<td>
									<select id="cDelegacionFiscal" name="cDelegacionFiscal" style="width: 20em;">
										<option value="A:">A</option>
										<option value="B:">B</option>
										<option value="C:">C</option>
										<option value="xx" selected>--</option>
									</select>
								</td>
								<td colspan="2" align="right">
									&nbsp;
								</td>
								<td align="right">C&oacute;digo Postal:</td>
								<td>
									<input id="cCodigoPostalFiscal" name="cCodigoPostalFiscal" type="text" size="6" maxlength="5">
								</td>
							</tr>
							<tr>
								<td align="right">Tel&eacute;fono:</td>
								<td>
									<input id="cTelefonoFiscal" name="cTelefonoFiscal" type="text" size="15" maxlength="15">
								</td>
								<td align="right">Fax:</td>
								<td colspan="3">
									<input id="cFaxFiscal" name="cFaxFiscal" type="text" size="15" maxlength="15">
								</td>
							</tr>
							<tr>
								<td align="right">E-Mail:</td>
								<td colspan="5">
									<input id="cEMailFiscal" name="cEMailFiscal" type="text" size="80" maxlength="50">
								</td>
							</tr>
						</table>
					</div>
					<div id="tabs-2">
						<table border="0">
							<tr>
								<td align="right">Entidad Federativa:</td>
								<td>
									<select id="cIdEntidadFederativaActual" name="cIdEntidadFederativaActual" style="width: 20em;">
										<option value="A:">A</option>
										<option value="B:">B</option>
										<option value="C:">C</option>
										<option value="xx" selected>--</option>
									</select>
								</td>
								<td align="right">&nbsp;</td>
								<td colspan="3" align="right">
									<input type="button" onclick="Hello()" value="?" />
								</td>
							</tr>
							<tr>
								<td align="right">Calle:</td>
								<td>
									<input id="cCalleActual" name="cCalleActual" type="text" size="30" maxlength="4">
								</td>
								<td align="right">No.:</td>
								<td>
									<input id="cNoDomicilioActual" name="cNoDomicilioActual" type="text" size="6" maxlength="10">
								</td>
								<td align="right">No.Interior:</td>
								<td>
									<input id="cNoInteriorDomicilioActual" name="cNoInteriorDomicilioActual" type="text" size="6" maxlength="10">
								</td>
							</tr>
							<tr>
								<td align="right">Otras se&ntilde;as:</td>
								<td colspan="5">
									<input id="cOtrosDatosActual" name="cOtrosDatosActual" type="text" size="80" maxlength="50">
								</td>
							</tr>
							<tr>
								<td align="right">Colonia o Manzana:</td>
								<td colspan="5">
									<input id="cColoniaActual" name="cColoniaActual" type="text" size="80" maxlength="50">
								</td>
							</tr>
							<tr>
								<td align="right">Delegaci&oacute;n:</td>
								<td>
									<select id="cDelegacionActual" name="cDelegacionActual" style="width: 20em;">
										<option value="A:">A</option>
										<option value="B:">B</option>
										<option value="C:">C</option>
										<option value="xx" selected>--</option>
									</select>
								</td>
								<td colspan="2" align="right">&nbsp;</td>
								<td align="right">C&oacute;digo Postal:</td>
								<td>
									<input id="cCodigoPostalActual" name="cCodigoPostalActual" type="text" size="6" maxlength="5">
								</td>
							</tr>
							<tr>
								<td align="right">Tel&eacute;fono:</td>
								<td>
									<input id="cTelefonoActual" name="cTelefonoActual" type="text" size="15" maxlength="15">
								</td>
								<td align="right">Fax:</td>
								<td colspan="3">
									<input id="cFaxActual" name="cFaxActual" type="text" size="15" maxlength="15">
								</td>
							</tr>
							<tr>
								<td align="right">E-Mail:</td>
								<td colspan="5">
									<input id="cEMailActual" name="txteMailActual" type="text" size="80" maxlength="50">
								</td>
							</tr>
						</table>
					</div>
				</div>

				<h1>Apoderado</h1>
				<table border="0" align="center">
					<tr>
						<td align="right">A.Paterno:</td>
						<td>
							<input id="cApellidoPaternoApoderado" name="cApellidoPaternoApoderado" type="text" size="23" maxlength="30">
						</td>
						<td align="right">A.Materno:</td>
						<td>
							<input id="cApellidoMaternoApoderado" name="cApellidoMaternoApoderado" type="text" size="23" maxlength="30">
						</td>
						<td align="right">Nombre:</td>
						<td>
							<input id="cNombreApoderado" name="cNombreApoderado" type="text" size="23" maxlength="30">
						</td>
					</tr>
					<tr>
						<td align="right">Tel&eacute;fono:</td>
						<td>
							<input id="cTelefonoApoderado" name="cTelefonoApoderado" type="text" size="15" maxlength="15">
						</td>
						<td align="right">Fax:</td>
						<td colspan="3">
							<input id="cFaxApoderado" name="cFaxApoderado" type="text" size="15" maxlength="15">
						</td>
					</tr>
					<tr>
						<td align="right">E-Mail:</td>
						<td colspan="5">
							<input id="cEMailApoderado" name="cEMailApoderado" type="text" size="80" maxlength="50">
						</td>
					</tr>
					<tr>
						<td align="right">No. Oficio Poder Legal:</td>
						<td colspan="5">
							<input id="cNoOficioPoderLegal" name="cNoOficioPoderLegal" type="text" size="23" maxlength="15">
						</td>
					</tr>
				</table>
			</form>
		</div>
	</body>
</html>