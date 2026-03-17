<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>B&uacute;squeda de Retenciones</title>
		<link href="js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css"
			rel="stylesheet">
		<link href="../Generador/css/demo_page.css" rel="stylesheet">
		<link href="../Generador/css/demo_table_jui.css" rel="stylesheet">
		<script type="text/javascript" src="js/jq9/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/jq9/jquery-ui-1.9.0.custom.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript"
			src="../js/ContabilidadCentroContable.js"></script>


		<script type="text/javascript">
	var dTable;
	$(document)
			.ready(
					function() {
						$("#PolFechCapturaIni").datepicker({
							showOn : "button",
							buttonImage : "images/calendar.png",
							buttonImageOnly : true
						}).datepicker("option", "dateFormat", 'dd/mm/yy');

						$("#PolFechCapturaFin").datepicker({
							showOn : "button",
							buttonImage : "images/calendar.png",
							buttonImageOnly : true
						}).datepicker("option", "dateFormat", 'dd/mm/yy');

						$("#PolFechAplicacionIni").datepicker({
							showOn : "button",
							buttonImage : "images/calendar.png",
							buttonImageOnly : true
						}).datepicker("option", "dateFormat", 'dd/mm/yy');

						$("#PolFechAplicacionFin").datepicker({
							showOn : "button",
							buttonImage : "images/calendar.png",
							buttonImageOnly : true
						}).datepicker("option", "dateFormat", 'dd/mm/yy');

						getCentroContable();

						if (newCC != '') {
							$("#PolCtroContable").val(newCC);
							queryFormPost("CCentroContablePolizaRead", {
								async : true
							});
						}

						$("#titulo").text(
								"Consulta de Retenciones "
										+ $("#hPolCtroContable").val());

						querySelectPost("catalogoEjercicioFiscalRead",
								"PolEjercicioFiscal");

						querySelectPost("catalogoPolOrigen", "PolOrigen", {
							async : false
						});

						querySelectPost("catalogoPolTipo", "PolTipo", {
							async : false
						});

						$("#PolTipo").change(function() {
							querySelectPost("catalogoPolOrigen", "PolOrigen", {
								async : false
							});
						});

						$("#PolOrigen").change(function() {
							querySelectPost("catalogoPolTipo", "PolTipo", {
								async : false
							});
						});

						$("#PolAutomatica").change(function() {
							querySelectPost("catalogoPolOrigen", "PolOrigen", {
								async : false
							});
							querySelectPost("catalogoPolTipo", "PolTipo", {
								async : false
							});
						});

						querySelectPost("catalogoPolStatus", "PolStatus");

						$("#consultar")
								.button()
								.click(
										function() {
											var prm = "PolCtroContable="
													+ $("#PolCtroContable")
															.val()
													+ "&PolEjercicioFiscal="
													+ $("#PolEjercicioFiscal")
															.val()
													+ "&PolStatus="
													+ $("#PolStatus").val()
													+ "&PolOrigen="
													+ $("#PolOrigen").val()
													+ "&PolFechCapturaIni="
													+ $("#PolFechCapturaIni").val()
													+ "&PolFechCapturaFin="
													+ $("#PolFechCapturaFin")
															.val()
													+ "&PolTipo="
													+ $("#PolTipo").val()
													+ "&PolFechAplicacionIni="
													+ $("#PolFechAplicacionIni")
															.val()
													+ "&PolFechAplicacionFin="
													+ $("#PolFechAplicacionFin")
															.val()
													+ "&PolNumeroIni="
													+ $("#PolNumeroIni").val()
													+ "&PolNumeroFin="
													+ $("#PolNumeroFin").val()
													+ "&PolMontoIni="
													+ $("#PolMontoIni").val()
													+ "&PolMontoFin="
													+ $("#PolMontoFin").val()
													+ "&PolAutomatica="
													+ $("#PolAutomatica").val();

											var ajxSource = '../CuentaContable/ConsultaPoliza?'
													+ prm;
											dTable.fnSettings().sAjaxSource = ajxSource;
											dTable.fnReloadAjax();

										});

						$("#limpiar")
								.button()
								.click(
										function() {
											dTable.fnClearTable();
											$(":input")
													.each(
															function() {
																if (this.type != 'hidden'
																		&& this.type != 'button')
																	$(this)
																			.val(
																					'');
															});
										});

						$("#accordion").accordion();
						$.fn.dataTableExt.oApi.fnReloadAjax = function(
								oSettings, sNewSource) {
							$
									.blockUI({
										message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
									});
							if (typeof sNewSource != 'undefined')
								oSettings.sAjaxSource = sNewSource;

							this.fnClearTable(this);
							this.oApi._fnProcessingDisplay(oSettings, true);
							var that = this;

							$
									.getJSON(
											oSettings.sAjaxSource,
											null,
											function(json) {
												/* Got the data - add it to the table */
												for ( var i = 0; i < json.aaData.length; i++) {
													that.oApi._fnAddData(
															oSettings,
															json.aaData[i]);
												}

												oSettings.aiDisplay = oSettings.aiDisplayMaster
														.slice();
												that.fnDraw(that);
												that.oApi._fnProcessingDisplay(
														oSettings, false);
												$.unblockUI();
											});
						};

						dTable = $("#dTbl").dataTable({
							"bJQueryUI" : true,
							"sAjaxSource" : '../CuentaContable/ConsultaPoliza',
							"sScrollY" : 250,
							"sScrollX" : 900,
							"bPaginate" : false,
							"aoColumnDefs" : [ {
								"bSearchable" : false,
								"bVisible" : false,
								"aTargets" : [ 9 ]
							} ]
						});

						$("#selAll").click(function() {
							var checado = $("#selAll").attr("checked");
							$(":checkbox").each(function() {
								if (this.id != 'selAll') {
									if (checado)
										$(this).attr("checked", "checked");
									else
										$(this).removeAttr("checked");
								}
							});
						});

						$("#editar")
								.button()
								.click(
										function() {
											var n = $("[checked]").length;
											if (n == 0 || !n) {
												alert("Debe seleccionar al menos una p\u00F3liza");
												return;
											}

											if (n > 1) {
												alert("No puede editar m\u00E1s de una p\u00F3liza");
												return;
											} else {
												var val = $(
														"input[name='selection']:checked")
														.val();
												val = $(
														"input[name='selection']:checked")
														.val().replace(/\[/g,
																'').replace(
																/\]/g, '')
														.replace(/\s/g, '')
														.replace(/and/g, ',')
														.replace(/'/g, '');

//												var elmnts = val.split(',');
//												var nFolioPoliza = elmnts[0]
//														.split("=")[1];
//												var cCentroContable = elmnts[1]
//														.split("=")[1];
//												var cTipoPoliza = elmnts[2]
//														.split("=")[1];
												var elmnts = val.split(',');
//												var nFolioPoliza = elmnts[0]
//														.split(",")[1];
//												var cCentroContable = elmnts[1]
//														.split(",")[1];
//												var cTipoPoliza = elmnts[2]
//														.split("]")[1];
												var nFolioPoliza = elmnts[0];
												var cCentroContable = elmnts[1];
												var cTipoPoliza = elmnts[2];

												$("#nFolioPoliza").val(
														nFolioPoliza);
												$("#cTipoPoliza").val(
														cTipoPoliza);
												$("#cCentroContable").val(
														cCentroContable);
												$("#aEjercicioFiscal")
														.val(
																$(
																		"#PolEjercicioFiscal")
																		.val());

												queryFormPost(
														"readNFolioDocumento",
														{
															async : false
														});

												if ('DOCPOLIZA' == $(
														"#cTipoDocumento")
														.val()
														&& $("#nFolioDocumento")
																.val() != '') {
													queryFormPost(
															"MesContableAbiertoEdit",
															{
																async : false
															});
													if ($("#nMes").val() == $(
															"#mesAbierto")
															.val())
														$("#mainForm").submit();
													else {
														alert("Para editar una poliza el mes debe estar abierto");
													}
												} else
													alert('Solo es posible editar p\u00F3lizas manuales');
											}
										});
						$("#imprimir")
								.button()
								.click(
										function() {
											var vcheckbox = "";
											var flagI = 0;

											$(":checkbox")
													.each(
															function() {
																if (this.id != 'selAll'
																		&& $(
																				this)
																				.attr(
																						"checked")) {
																	if (flagI == 0) {
																		vcheckbox = $(
																				this)
																				.val();
																		flagI = 1;
																	} else {
//																		vcheckbox += " or "
																		vcheckbox += ""
																				+ $(
																						this)
																						.val();
																	}
																}
															});
											if (vcheckbox != '') {
												var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=PolizaContable.jasper&condiciones='("
														+ vcheckbox + ")'";
												var ventimp = window
														.open(url, "popacuse",
																"scrollbars=1, resizable=yes, width=1024, height=768");
											} else {
												alert("Debe seleccionar al menos una poliza para imprimir.");
											}
										});

						$("#dTbl tbody")
								.dblclick(
										function(evt) {
											var aPos = dTable
													.fnGetPosition(evt.target.parentNode);

											if (aPos instanceof Array)
												currIndex = aPos[0];
											else
												currIndex = aPos;
											var val = $(
													dTable.fnGetData()[currIndex][0])
													.val();

											if (val != '') {
												var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=PolizaContable.jasper&condiciones='("
														+ val + ")'";
												var ventimp = window
														.open(url, "popacuse",
																"scrollbars=1, resizable=yes, width=1024, height=768");
											} else {
												alert("No se tiene informacion para imprimir la poliza.");
											}

										});
					});
</script>
	</head>
	<body id="dt_example">
		<form action="../poliza/EditaPoliza" target="content-iframe"
			method="post" id="mainForm">
			<input type="hidden" id="mesAbierto" name="mesAbierto" value="" />
			<input type="hidden" id="nMes" name="nMes" value="" />
			<input type="hidden" id="nFolioDocumento" name="nFolioDocumento"
				value="" />
			<input type="hidden" id="cTipoDocumento" name="cTipoDocumento"
				value="" />
			<input type="hidden" id="cCentroContable" name="cCentroContable"
				value="" />
			<input type="hidden" id="nFolioPoliza" name="nFolioPoliza" value="" />
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="" />
			<input type="hidden" id="hPolCtroContable" name="hPolCtroContable"
				value="">
			<input type="hidden" id="PolCtroContable" name="PolCtroContable"
				value="">
			<input type="hidden" id="action" name="action" value="EDITAR" />
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal"
				value="" />
			<h1>
				<label id="titulo"></label>
			</h1>
			<div id="container" class="container SyCData">
				<div id="accordion">
					<h3>
						Campos de B&uacute;squeda
					</h3>
					<div>
						<input type="hidden" id="hPolCtroContable" name="hPolCtroContable"
							value="">
						<input type="hidden" id="PolCtroContable" name="PolCtroContable"
							value="">
						<table>
							<tr>
								<td colspan="2" align="left">
									N&uacute;mero de P&oacute;liza
								</td>
								<td colspan="2" align="left">
									Monto de P&oacute;liza
								</td>
							</tr>
							<tr>
								<td align="left">
									Del:&nbsp;
									<input id="PolNumeroIni" name="PolNumeroIni" value=""
										maxlength="20" size="10" type="text">
								</td>
								<td align="left">
									&nbsp;Al:&nbsp;
									<input id="PolNumeroFin" name="PolNumeroFin" value=""
										maxlength="20" size="10" type="text">
								</td>
								<td align="left">
									De:&nbsp;$
									<input id="PolMontoIni" name="PolMontoIni" value=""
										maxlength="20" size="10" type="text">
								</td>
								<td align="right">
									A:&nbsp;$
									<input id="PolMontoFin" name="PolMontoFin" value=""
										maxlength="20" size="10" type="text">
								</td>
							</tr>
							<tr>
								<td align="left">
									Autom&aacute;tica
								</td>
								<td align="left">
									Tipo:
								</td>
								<td align="left">
									Origen:
								</td>
<!--								<td align="left">-->
<!--									Ej. Fiscal:-->
<!--								</td>-->
							</tr>
							<tr>
								<td align="left">
									<select id="PolAutomatica" name="PolAutomatica">
										<option value=""></option>
										<option value="1">
											AUTOMÁTICA
										</option>
										<option value="0">
											CONTABILIDAD
										</option>
									</select>
								</td>
								<td align="left">
									<select id="PolTipo" name="PolTipo">
										<option value="">
										</option>
									</select>
								</td>
								<td align="left">
									<select id="PolOrigen" name="PolOrigen">
										<option value="">
										</option>
									</select>
								</td>
<!--								<td align="left">-->
<!--									<select id="PolEjercicioFiscal" name="PolEjercicioFiscal">-->
<!--										<option value="">-->
<!--										</option>-->
<!--									</select>-->
<!--								</td>-->

							</tr>
<!--						</table>-->
<!--					</div>-->
<!--					<h3>-->
<!--						Opciones avanzadas-->
<!--					</h3>-->
<!--					<div>-->
<!--						<table width="100%">-->
							<tr>
<!--								<td colspan="2" align="left">-->
<!--									Fecha de Captura:-->
<!--								</td>-->
								<td align="left">
									Fecha de Aplicaci&oacute;n:
								</td>
							</tr>
							<tr>
<!--								<td align="left">-->
<!--									Del:&nbsp;-->
<!--									<input id="PolFechCapturaIni" name="PolFechCapturaIni" value=""-->
<!--										readonly="readonly" maxlength="10" size="10" type="text">-->
<!--								</td>-->
<!--								<td align="left">-->
<!--									&nbsp;Al:&nbsp;-->
<!--									<input id="PolFechCapturaFin" name="PolFechCapturaFin" value=""-->
<!--										readonly="readonly" maxlength="20" size="10" type="text">-->
<!--								</td>-->
								<td align="left">
									Del:&nbsp;
									<input id="PolFechAplicacionIni" name="PolFechAplicacionIni"
										value="" readonly="readonly" maxlength="10" size="10"
										type="text">
								</td>
								<td align="left">
									&nbsp;Al:&nbsp;
									<input id="PolFechAplicacionFin" name="PolFechAplicacionFin"
										value="" readonly="readonly" maxlength="20" size="10"
										type="text">
								</td>
							</tr>
<!--							<tr>-->
<!--								<td align="left">-->
<!--									Status:-->
<!--								</td>-->
<!--							</tr>-->
<!--							<tr>-->
<!--								<td align="left">-->
<!--									<select id="PolStatus" name="PolStatus">-->
<!--										<option value="">-->
<!--										</option>-->
<!--									</select>-->
<!--								</td>-->
<!--							</tr>-->
						</table>
					</div>
				</div>
				<div>
					<table width="100%">
						<tr>
							<td align="right">
								<input type="button" id="imprimir" value="Imprimir">
								<input type="button" id="consultar" value="Consultar">
								<input type="button" id="limpiar" value="Limpiar">
								<input type="button" id="editar" value="Editar" />
							</td>
						</tr>
					</table>
				</div>
				<div>
					<table id="dTbl">
						<thead>
							<tr>
								<th>
									<input type="checkbox" id="selAll" name="selAll">
								</th>
								<th>
									Tipo
								</th>
								<th>
									Folio
								</th>
								<th>
									F. de Captura
								</th>
								<th>
									F. de Aplicaci&oacute;n
								</th>
								<th>
									Estatus
								</th>
								<th>
									Origen
								</th>
								<th>
									Concepto
								</th>
								<th>
									Autorizo
								</th>
								<th>
									Centro Contable
								</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</form>
	</body>
</html>