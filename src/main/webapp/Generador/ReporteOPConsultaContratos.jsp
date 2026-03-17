<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Consulta de Contratos.</title>

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
		<script type="text/javascript" src="js/ReporteOPConsultaContratos.js"></script>
		<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>
	</head>

	<body id="dt_example">
		<form action="../ConvenioModificatorio" method="post" id="mainForm">
			<h1>
				<label id="titulo">
					Convenio Modificatorio
				</label>
			</h1>
			<div id="container" class="container SyCData">
				<h5>
					Ingrese la siguiente informaci&oacute;n:
				</h5>
			</div>
			<table style="font-size: 12px;">
				<tr>
					<td align="right">
						Folio SAI:
					</td>
					<td align="left">
						<input type="text" id="FolioSAI" name="FolioSAI" size="12" />
					</td>
					<td align="right">
						OLI:
					</td>
					<td align="left">
						<input type="text" id="OLI" name="OLI" size="6" />
					</td>
					<td align="right">
						Cartera:
					</td>
					<td align="left">
						<input type="text" id="Cartera" name="Cartera" size="12" />
					</td>
					<td colspan="2">

					</td>
				</tr>
				<tr>
					<td align="right">
						Estatus:
					</td>
					<td align="left">
						<select id="estatus_contrato" name="estatus_contrato" style="font-size: 12px;">
							<option value="">
								
							</option>
							<option value="APARTADO EN CAPTURA">
								APARTADO EN CAPTURA
							</option>
							<option value="APARTADO CANCELADO">
								APARTADO CANCELADO
							</option>
							<option value="APARTADO AUTORIZADO">
								APARTADO AUTORIZADO
							</option>
							<option value="PRECOMPROMISO EN CAPTURA">
								PRECOMPROMISO EN CAPTURA
							</option>
							<option value="PRECOMPROMISO AUTORIZADO">
								PRECOMPROMISO AUTORIZADO
							</option>
							<option value="PRECOMPROMISO CANCELADO">
								PRECOMPROMISO CANCELADO
							</option>
							<option value="COMPROMISO EN CAPTURA">
								COMPROMISO EN CAPTURA
							</option>
							<option value="COMPROMISO AUTORIZADO">
								COMPROMISO AUTORIZADO
							</option>
							<option value="COMPROMISO CANCELADO">
								COMPROMISO CANCELADO
							</option>
							<option value="ESPERA AUTORIZACION VENTANILLA">
								ESPERA AUTORIZACION VENTANILLA
							</option>
						</select>
					</td>
					<td colspan="6">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="right">
						Unidad Normativa:
					</td>
					<td align="left" colspan="7">
						<select id="unidadResponsable" name="unidadResponsable">
						</select>
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
						Junta De Aclaraci&oacute;n:
					</td>
					<td align="left">
						<input type="text" id="FechaJuntaAclaracionPrecompromiso"
							name="FechaJuntaAclaracionPrecompromiso" size="11"
							readonly="readonly" class="fecha" />
					</td>
					<td align="right">
						Recepci&oacute;n de Propuesta
					</td>
					<td align="left">
						<input type="text" id="FechaRecepcionPropuestaPrecompromiso"
							name="FechaRecepcionPropuestaPrecompromiso" size="11"
							readonly="readonly" class="fecha" />
					</td>
					<td align="right">
						&nbsp;
					</td>
					<td align="left">
						&nbsp;
					</td>

				</tr>
				<tr>
					<td align="right">
						F. de Fallo:
					</td>
					<td align="left">
						<input type="text" id="FechaDeFalloPrecompromiso"
							name="FechaDeFalloPrecompromiso" size="15" readonly="readonly"
							class="fecha" />
					</td>
					<td align="right">
						%Anticipo:
					</td>
					<td align="left">
						<input type="text" id="PorcentajeAnticipoCompromiso"
							name="PorcentajeAnticipoCompromiso" size="3" class="numerico"
							onKeyPress="return onlyNumbers(event)" />
					</td>
					<td align="right">
						Inicio De Contrato:
					</td>
					<td align="left">
						<input type="text" id="FechaDeInicioContrato"
							name="FechaDeInicioContrato" size="11" readonly="readonly"
							class="fecha" />
					</td>
					<td align="right">
						&nbsp;
					</td>
					<td align="left">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="right">
						Fin De Contrato
					</td>
					<td align="left">
						<input type="text" id="FechaFinDeContrato"
							name="FechaFinDeContrato" size="11" readonly="readonly"
							class="fecha" />
					</td>
					<td align="right">
						No de Concurso:
					</td>
					<td align="left">
						<input type="text" id="NoConcursoCompromiso"
							name="NoConcursoCompromiso" size="15" />
					</td>
					<td align="right">
						Tipo de Contrato:
					</td>
					<td align="left">
						<select id="IdTipoContrato" name="IdTipoContrato"
							style="size: 150px">
						</select>
					</td>
					<td align="right">
						&nbsp;
					</td>
					<td align="left">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="right">
						Adjudicaci&oacute;n:
					</td>
					<td align="left" colspan="2">
						<select id="IdTipoAdjudicacion" name="IdTipoAdjudicacion"
							style="size: 265"></select>
					</td>
					<td align="right">
						No. de Oficio:
					</td>
					<td align="left">
						<input type="text" id="NoOficio" name="NoOficio" size="15" />
					</td>
					<td align="right">
						F. De Oficio:
					</td>
					<td align="left">
						<input type="text" id="FechaDeOficio" name="FechaDeOficio"
							size="11" readonly="readonly" class="fecha" />
					</td>
					<td>
						&nbsp;
					</td>
				</tr>

				<tr>
					<td align="right">
						Oficio:
					</td>
					<td align="left">
						<select id="idTipoAdicional" name="idTipoAdicional">
						</select>
					</td>

					<td align="right">
						<input type="checkbox" id="ContratoPlurianual"
							name="ContratoPlurianual" value="1">
					</td>
					<td align="left">
						Contrato Plurianual
					</td>
					<td align="right">
						Monto Anticipo:
					</td>
					<td align="left">
						$
						<input type="text" id="MontoAnticipo" name="MontoAnticipo"
							size="15" class="numerico" onKeyPress="return onlyNumbers(event)" />
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="right">
						<input type="checkbox" id="pagosSuspendidos"
							name="pagosSuspendidos" value="S">
					</td>
					<td align="left">
						Pagos Suspendidos
					</td>
					<td align="right">
						%Amortizacion:
					</td>
					<td align="left">
						<input type="text" id="porcentajeDeAmortizacion"
							name="porcentajeDeAmortizacion" size="4" class="numerico"
							onKeyPress="return onlyNumbers(event)" />
					</td>
					<td align="right">
						Autorizaci&oacute;n de Anticipo
					</td>
					<td align="left">
						<input type="text" id="FolioDeAutorizacionAnticipo"
							name="FolioDeAutorizacionAnticipo" size="10" />
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="right">
						Fianza:
					</td>
					<td align="left">
						<input type="text" id="NoDeFianza" name="NoDeFianza" size="15" />
					</td>
					<td align="right">
						Entidad Federativa
					</td>
					<td align="left">
						<select id="EntidadDondeSeDesarrollaObra"
							name="EntidadDondeSeDesarrollaObra"></select>
					</td>
					<td align="right">
						Monto de Obra:
					</td>
					<td align="left">
						$
						<input type="text" id="MontoObra" name="MontoObra" size="15"
							class="numerico" onKeyPress="return onlyNumbers(event)" />
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="right">
						IVA
					</td>
					<td align="left">
						<select id="IVAObra" name="IVAObra">
						</select>
					</td>
					<td align="right">
						Recurso:
					</td>
					<td align="left">
						<select id="idTipoRecurso" name="idTipoRecurso"></select>
					</td>
					<td align="right">
						Tipo de Obra
					</td>
					<td align="left">
						<select id="idTipoObra" name="idTipoObra"></select>
					</td>
				</tr>
				<tr>
					<td align="right">
						Monto Total de Obra:
					</td>
					<td align="left">
						<input type="text" id="MontoTotalObra" name="MontoTotalObra"
							size="15" class="numerico" onKeyPress="return onlyNumbers(event)" />
					</td>
					<td align="right">
						No. de Contrato
					</td>
					<td align="left" colspan="2">
						<input type="text" id="NoContrato" name="NoContrato" size="40" />
					</td>
					<td align="left" colspan="3">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="8" align="right">
						<input type="button" value="Buscar" id="searchBtn">
						<input type="button" value="Imprimir" id="prntBtn">
						<input type="button" value="Limpiar" id="clearBtn">
					</td>
				</tr>
			</table>
			<table id="resultTable"
				style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11px;">
				<thead>
					<tr>
						<th>
							Folio SAI
						</th>
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
							Estatus Contrato
						</th>
						<th>
							Tipo de Obra
						</th>
						<th>
							Tipo de Recurso
						</th>
						<th>
							OLI
						</th>
						<th>
							Cartera
						</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</form>
	</body>
</html>
