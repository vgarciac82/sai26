<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
	String folioSAI = request.getParameter("folioSAI");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<title>Contrato de Obra Publica</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">

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
		<script type="text/javascript" src="js/ConsultaObraPublica.js"></script>
		<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>
		<style type="text/css">
.tabs1 td {
	color: #333;
	font-family: "Lucida Grande", Verdana, Arial, Helvetica, sans-serif;
	font-size: 80%;
	font-style: normal;
	font-variant: normal;
	font-weight: normal;
}
</style>
	</head>

	<body id="dt_example">
		<form>
			<div id="container" class="container">
				<input type="hidden" value="<%=folioSAI%>" id="FolioSAISearch"
					name="FolioSAISearch" />
				<input type="hidden" id="nFolioOPAHeader" name="nFolioOPAHeader" />
				<input type="hidden" id="nFolioOPPreComHeader"
					name="nFolioOPPreComHeader" />
				<input type="hidden" id="nFolioOPComHeader" name="nFolioOPComHeader" />

				<h1>
					<label id="titulo">
						Contrato de Obra P&uacute;blica
					</label>
				</h1>
				<div class="tabs">
					<ul>
						<li>
							<a id="Link01" href="#tabs-0">Contrato</a>
						</li>
						<li>
							<a id="Link02" onClick="loadPlurianualInfo()" href="#tabs-1">Informaci&oacute;n
								Plurianual</a>
						</li>
						<li>
							<a id="Link03" onClick="loadRetenciones()" href="#tabs-2">Retenciones</a>
						</li>
						<li>
							<a id="Link04" onClick="loadConvenios()" href="#tabs-3">Convenios
								Modificatorios</a>
						</li>
						<li>
							<a id="Link05" href="#tabs-4" onclick="loadPagos()">Pagos</a>
						</li>
					</ul>
					<div id="tabs-0">
						<table border="0" cellspacing="0" id="header" class="tabs1">
							<tr>
								<td align="right">
									Folio SAI:
								</td>
								<td align="left" colspan="2">
									<input type="text" id="FolioSAI" name="FolioSAI" size="12" />
								</td>
								<td align="right">
									No. de Contrato:
								</td>
								<td align="left" colspan="2">
									<input type="text" id="NoContrato" name="NoContrato" size="33" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Estatus:
								</td>
								<td align="left" colspan="2">
									<input type="text" id="estatus_contrato"
										name="estatus_contrato" size="33" />
								</td>
								<td align="right">
									Captur&oacute;:
								</td>
								<td align="left" colspan="2">
									<input type="text" id="usuarioCaptura" name="usuarioCaptura"
										size="33" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Unidad Normativa:
								</td>
								<td align="left">
									<input type="text" id="unidadResponsable"
										name="unidadResponsable" size="4" />
								</td>
								<td align="right">
									Cartera:
								</td>
								<td align="left">
									<input type="text" id="Cartera" name="Cartera" size="13" />
								</td>
								<td align="right">
									OLI:
								</td>
								<td align="left">
									<input type="text" id="OLI" name="OLI" size="6" />
								</td>
							</tr>
							<tr>
								<td colspan="6" align="center" style="font-weight: bold;">
									Tipo de:
								</td>
							</tr>
							<tr>
								<td align="right">
									Contrato:
								</td>
								<td align="left">
									<input type="text" id="descTipoContrato"
										name="descTipoContrato" size="9" />
								</td>
								<td align="right">
									Recurso:
								</td>
								<td align="left">
									<input type="text" id="descTipoRecurso" name="descTipoRecurso"
										size="16" />
								</td>
								<td align="right">
									Adjudicaci&oacute;n:
								</td>
								<td align="left">
									<input type="text" id="descTipoAdjudicacion"
										name="descTipoAdjudicacion" size="25" />
								</td>
							</tr>
							<tr>
								<td align="right">
									No. Convocatoria:
								</td>
								<td align="left">
									<input type="text" id="NoConvocatoriaPrecompromiso"
										name="NoConvocatoriaPrecompromiso" size="15" />
								</td>
								<td align="right">
									Fecha:
								</td>
								<td align="left">
									<input type="text" id="FechaConvocatoriaPrecompromiso"
										name="FechaConvocatoriaPrecompromiso" size="11" class="fecha">
								</td>
								<td align="right">
									No de Concurso:
								</td>
								<td align="left">
									<input type="text" id="NoConcursoCompromiso"
										name="NoConcursoCompromiso" size="15" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Monto de Obra:
								</td>
								<td align="left">
									<input type="text" id="MontoObra" name="MontoObra" size="15"
										class="numerico" />
								</td>
								<td align="right">
									IVA:
								</td>
								<td align="left">
									<input type="text" id="IVAObra" name="IVAObra" size="5"
										class="porcentaje" />
								</td>
								<td align="right">
									Total:
								</td>
								<td align="left">
									<input type="text" id="MontoTotalObra" name="MontoTotalObra"
										size="15" class="numerico"
										onKeyPress="return onlyNumbers(event)" />
								</td>
							<tr>
								<td colspan="6" align="center" style="font-weight: bold;">
									Fecha de:
								</td>
							</tr>
							<tr>
								<td align="right">
									Junta Aclaraci&oacute;n:
								</td>
								<td align="left">
									<input type="text" id="FechaJuntaAclaracionPrecompromiso"
										name="FechaJuntaAclaracionPrecompromiso" size="11"
										class="fecha" />
								</td>
								<td align="right">
									Recepci&oacute;n de Propuesta
								</td>
								<td align="left">
									<input type="text" id="FechaRecepcionPropuestaPrecompromiso"
										name="FechaRecepcionPropuestaPrecompromiso" size="11"
										class="fecha" />
								</td>
								<td align="right">
									Fallo:
								</td>
								<td align="left">
									<input type="text" id="FechaDeFalloPrecompromiso"
										name="FechaDeFalloPrecompromiso" size="15" class="fecha" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Anticipo:
								</td>
								<td align="left">
									<input type="text" id="PorcentajeAnticipoCompromiso"
										name="PorcentajeAnticipoCompromiso" size="4"
										class="porcentaje" />
								</td>
								<td align="right">
									Monto:
								</td>
								<td align="left">
									<input type="text" id="MontoAnticipo" name="MontoAnticipo"
										size="15" class="numerico" />
								</td>
								<td align="right">
									Amortizacion:
								</td>
								<td align="left">
									<input type="text" id="porcentajeDeAmortizacion"
										name="porcentajeDeAmortizacion" size="4" class="numerico" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Aut. de Anticipo
								</td>
								<td align="left">
									<input type="text" id="FolioDeAutorizacionAnticipo"
										name="FolioDeAutorizacionAnticipo" size="10" />
								</td>
								<td align="right">
									<input type="checkbox" id="chkContratoPlurianual"
										name="chkContratoPlurianual">
									<input type="hidden" id="ContratoPlurianual">
								</td>
								<td align="left">
									Contrato Plurianual
								</td>
								<td align="right">
									<input type="checkbox" id="chkPagosSuspendidos"
										name="chkPagosSuspendidos">
									<input type="hidden" id="pagosSuspendidos" />
								</td>
								<td align="left">
									Pagos Suspendidos
								</td>
							</tr>
							<tr>
								<td align="right">
									Tipo de Obra:
								</td>
								<td align="left">
									<input type="text" id="descTipoObra" name="descTipoObra"
										size="14" />
								</td>
								<td align="right">
									Inicio De Contrato:
								</td>
								<td align="left">
									<input type="text" id="FechaDeInicioContrato"
										name="FechaDeInicioContrato" size="11" class="fecha" />
								</td>
								<td align="right">
									Fin De Contrato
								</td>
								<td align="left">
									<input type="text" id="FechaFinDeContrato"
										name="FechaFinDeContrato" size="11" class="fecha" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Oficio:
								</td>
								<td align="left">
									<input type="text" id="descTipoAdicionales"
										name="descTipoAdicionales" size="22" />
								</td>
								<td align="right">
									No. de Oficio:
								</td>
								<td align="left">
									<input type="text" id="NoOficio" name="NoOficio" size="15" />
								</td>
								<td align="right">
									Fecha:
								</td>
								<td align="left">
									<input type="text" id="FechaDeOficio" name="FechaDeOficio"
										size="11" class="fecha" />
								</td>
							</tr>
							<tr>
								<td align="right">
									Entidad Federativa
								</td>
								<td align="left" colspan="2">
									<input type="text" id="DescEntidadDondeSeDesarrollaObra"
										name="DescEntidadDondeSeDesarrollaObra" size="30">
								</td>
								<td align="right">
									Fianza:
								</td>
								<td align="left" colspan="2">
									<input type="text" id="NoDeFianza" name="NoDeFianza" size="15" />
								</td>
							</tr>
							<tr>
								<td colspan="6" align="center">
									Descripci&oacute;n de la obra
								</td>
							</tr>
							<tr>
								<td colspan="6" align="center">
									<textarea rows="5" cols="60" id="descripcionObra"
										name="descripcionObra">
									</textarea>
								</td>
							</tr>
						</table>
						<table align="left" width="750px">
							<tr>
								<td>
									<table id="dt_total" width="100%" class="display"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 80%">
										<thead>
											<tr>
												<th>
													Mes
												</th>
												<th>
													EP
												</th>
												<th>
													Total
												</th>
											</tr>
										</thead>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div id="tabs-1">
						<table align="center">
							<tr>
								<td align="center">
									<table id="pluriAnualTbl" align="center"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 80%"
										width="100%">
										<thead>
											<tr>
												<th>
													No. Contrato
												</th>
												<th>
													A&ntilde;o
												</th>
												<th>
													Monto X A&ntilde;o
												</th>
											</tr>
										</thead>
									</table>
								</td>
							</tr>
							<tr>
						</table>
					</div>
					<div id="tabs-2">
						<table align="center">
							<tr>
								<td align="center">
									<table id="dt_retencion" class="display" align="center"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 80%"
										width="100%">
										<thead>
											<tr>
												<th>
													Clave de retenci&oacute;n
												</th>
												<th>
													Tipo de retenci&oacute;n
												</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div id="tabs-3">
						<table align="center">
							<tr>
								<td align="center">
									<table id="dt_convenio" class="display" align="center"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 80%"
										width="100%">
										<thead>
											<tr>
												<th>
													Clave Convenio
												</th>
												<th>
													Inicio de Contrato
												</th>
												<th>
													Fin de Contrato
												</th>
												<th>
													Monto Modificado
												</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div id="tabs-4">
						<table align="center"">
							<tr valign="middle">
								<td align="center">
									<table id="dt_pagos" class="display" align="center"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 80%"
										width="100%">
										<thead>
											<tr>
												<th>
													Estimaci&oacute;n
												</th>
												<th>
													Importe + IVA
												</th>
												<th>
													Retenci&oacute;n 5%
												</th>
												<th>
													Retenci&oacute;n 2%
												</th>
												<th>
													Amortizaci&oacute;n
												</th>
												<th>
													Fecha Aut. de Pago
												</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
