<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Administraci&oacute;n de Bloqueo de Cuentas</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">

		<link rel="shortcut icon" type="image/ico"
			href="../Generador/imagenes/favicon.ico" />
		<link rel="stylesheet" type="text/css"
			href="../Ayudas/css/autocompleta.css">
		<link rel="stylesheet" type="text/css"
			href="../css/BloqueoCuentas.css">

		<style type="text/css" title="currentStyle">
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/css/demo_page.css";
		</style>

		<style type="text/css">
			table.display td {
				padding: 0px;
			}
		</style>

		<script type="text/javascript"
			src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../js/BloqueoCuentas.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
				init();
			});
		</script>
	</head>
	<body id="dt_example">
		<form>
			<input type="hidden" name="cBloqueaAbonos" id="cBloqueaAbonos" />
			<input type="hidden" name="cBloqueaCargos" id="cBloqueaCargos" />
			<input type="hidden" name="cNivelBloqueo" id="cNivelBloqueo" />
			<input type="hidden" name="nCuenta" id="nCuenta" />
			<div id="container" class="container">
				<h1>
					Bloqueo de Cuentas Contables
				</h1>
				<div id="tableDiv">
					<div id="topLegend">
						<table align="left">
						<tbody>
							<tr>
								<td>
									<strong style="font-size: 10; color: gray;">Doble click en una cuenta para editar.</strong>
								</td>
							</tr>
						</tbody>
						 </table>
						<table align="right">
							<tbody>
								<tr>
									<td align="right">
										<div class="ctaBloqCargo">
										</div>
									</td>
									<td align="left">
										Bloqueo Cargo
									</td>
									<td align="right">
										<div class="ctaBloqAbono">
										</div>
									</td>
									<td align="left">
										Bloqueo Abono
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div id="tblContainer">
						<table cellpadding="0" cellspacing="0" border="1" class="display"
							id="tblBloqueoCuentas">
							<thead>
								<tr>
									<th>
										Cuenta Contable / Mes
									</th>
									<th>
										1
									</th>
									<th>
										2
									</th>
									<th>
										3
									</th>
									<th>
										4
									</th>
									<th>
										5
									</th>
									<th>
										6
									</th>
									<th>
										7
									</th>
									<th>
										8
									</th>
									<th>
										9
									</th>
									<th>
										10
									</th>
									<th>
										11
									</th>
									<th>
										12
									</th>
									<th>
										13
									</th>
									<th>
										1
									</th>
									<th>
										2
									</th>
									<th>
										3
									</th>
									<th>
										4
									</th>
									<th>
										5
									</th>
									<th>
										6
									</th>
									<th>
										7
									</th>
									<th>
										8
									</th>
									<th>
										9
									</th>
									<th>
										10
									</th>
									<th>
										11
									</th>
									<th>
										12
									</th>
									<th>
										13
									</th>
									<th>
										Nivel Bloqueo
									</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>

				<div id="altaBloqueo">
					<table id="mainTable">
						<tr>
							<td colspan="2">
								<label id="ctaEnc" class="enc">
									Cuenta:
								</label>
								<label id="cta">
									&nbsp;
								</label>

								<label id="descEnc" class="enc">
									Descripci&oacute;n:
								</label>
								<label id="descCta">
									&nbsp;
								</label>
							</td>

						</tr>
						<tr>
							<td>
								<fieldset>
									<legend>
										Meses
									</legend>
									<table id="selMesesTbl" align="center">
										<thead>
											<tr>
												<th>
													&nbsp;
												</th>
												<th>
													Cargos
												</th>
												<th>
													Abonos
												</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td align="left">
													Enero
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cEnero"
														value="0">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aEnero"
														value="0">
												</td>
											</tr>
											<tr>
												<td align="left">
													Febrero
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cFebrero"
														value="1">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aFebrero"
														value="1">
												</td>
											</tr>
											<tr>
												<td align="left">
													Marzo
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cMarzo"
														value="2">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aMarzo"
														value="2">
												</td>
											</tr>
											<tr>
												<td align="left">
													Abril
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cAbril"
														value="3">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aAbril"
														value="3">
												</td>
											</tr>
											<tr>
												<td align="left">
													Mayo
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cMayo"
														value="4">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aMayo"
														value="4">
												</td>
											</tr>
											<tr>
												<td align="left">
													Junio
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cJunio"
														value="5">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aJunio"
														value="5">
												</td>
											</tr>
											<tr>
												<td align="left">
													Julio
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cJulio"
														value="6">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aJulio"
														value="6">
												</td>
											</tr>
											<tr>
												<td align="left">
													Agosto
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cAgosto"
														value="7">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aAgosto"
														value="7">
												</td>
											</tr>

											<tr>
												<td align="left">
													Septiembre
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cSeptiembre"
														value="8">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aSeptiembre"
														value="8">
												</td>
											</tr>
											<tr>
												<td align="left">
													Octubre
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cOctubre"
														value="9">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aOctubre"
														value="9">
												</td>
											</tr>
											<tr>
												<td align="left">
													Noviembre
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cNoviembre"
														value="10">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aNoviembre"
														value="10">
												</td>
											</tr>
											<tr>
												<td align="left">
													Diciembre
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cDiciembre"
														value="11">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aDiciembre"
														value="11">
												</td>
											</tr>
											<tr>
												<td align="left">
													Trece
												</td>
												<td align="center">
													<input type="checkbox" name="cargoChck" id="cDiciembre"
														value="12">
												</td>
												<td align="center">
													<input type="checkbox" name="abonoChck" id="aDiciembre"
														value="12">
												</td>
											</tr>

										</tbody>
									</table>
								</fieldset>
							</td>
							<td valign="top">
								<fieldset>
									<legend>
										Alcance
									</legend>
									<table align="left">
										<tr>
											<td>
												<input type="radio" name="alcance" id="T" value="T">
												<label for="t">
													Todas
												</label>
											</td>
										</tr>
										<tr>
											<td>
												<input type="radio" name="alcance" id="C" value="C">
												<label for="C">
													Oficinas Centrales
												</label>
											</td>
										</tr>
										<tr>
											<td>
												<input type="radio" name="alcance" id="F" value="F">
												<label for="F">
													Oficinas Foraneas
												</label>
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
					</table>
				</div>

			</div>
		</form>
	</body>
</html>
