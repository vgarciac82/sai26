var oTablevFact;
var oTableCucop;
var oTableLineas;
var oTableRetencion;
var oTableEPs;
var oTableSaldos;
var oTableCalendarioPago
var retencionesCargadas = false;
var toleracia = 0.05;

var es_MX = {
	sProcessing: "Procesando...",
	sLengthMenu: "Mostrar _MENU_ registros",
	sZeroRecords: "No hay registros a mostrar",
	sEmptyTable: "No hay datos en la tabla",
	sLoadingRecords: "Cargando...",
	sInfo: "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty: "Registro 0 al 0 de 0",
	sInfoFiltered: "(filtado de _MAX_ registros)",
	sInfoPostFix: "",
	sInfoThousands: ",",
	sSearch: "Buscar:",
	oPaginate: {
		sFirst: "Primero",
		sPrevious: "Ant.",
		sNext: "Sigte.",
		sLast: "&Uacute;ltimo"
	}
};

var excepcionUser = "Briseida Noemi Ramirez Garcia";
var excepcionEmail = "briseida.ramirez@conafor.gob.mx";


function initUI() {
	oTablevFact = $('#grdValidaFacturas').dataTable({
		"bPaginate": true,
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true,
		"sScrollY": "100%",
		"bJQueryUI": true,
		"bRetrive": true,
		"bDestroy": true,
		"sPaginationType": "full_numbers",
		"bScrollCollapse": true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos en la tabla",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtered from _MAX_ total entries)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Filtro:",
			oPaginate: {
				sFirst: "Primero",
				sPrevious: "Ant.",
				sNext: "Sigte.",
				sLast: "&Uacute;ltimo"
			}
		}
	});
	try {
		aplicaEstilos();
		creaDiagloFacturas();
		obtenPagoEstatus();
		cargaDestinoGasto();
		creaDialogCorreo();
		/* ========== Asignacion de Funciones ======= */
		$("#cargaFacturasBtn").button().click(function () {
			muestraDivFacturas();
		});

		$("#calcularBtn").button();
		$("#capturaCorreo").button();
		$("#capturaCorreo").hide();
		$("#catIdTipoOperacion").change(function () {
			$("#idTipoDocumento").val($("#catIdTipoOperacion").val());
			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				cargaDestinoGasto();
			} else {
				limpiaSelect("DESTINO_GASTO", "-1", "Seleccione Tipo Destino");
				$("#DESTINO_GASTO").change();
			}

		});

		$("#btnSeleccionarSuficiencia").button().click(function (e) {
			e.preventDefault();
			e.stopPropagation();
			createPaymentFromSufficiency();
		});

		$("#DESTINO_GASTO").change(function () {

			$("#idDestinoGasto").val($("#DESTINO_GASTO").val());

			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				querySelectPost("CatalogoObraTConceptoRead", "tConcepto", {
					async: false
				});
			} else {
				limpiaSelect("tConcepto", "-1", "Seleccione Tipo de Concepto");
				$("#tConcepto").change();
			}
		});

		$("#tConcepto").change(function () {
			$("#idTipoConcepto").val($(this).val());
			$("#idConcepto").val($(this).val());
			$("#TIPO_CONCEPTO").val($(this).val());

			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				querySelectPost({
					queryName: 'CatalogoObraTMovimendoRead',
					targetObjectId: 'tmovimiento',
					async: false,
					callback: function () {
						$("#tmovimiento").change();
					}
				});
			} else {
				limpiaSelect("tmovimiento", "-1", "Seleccione Tipo de Movimiento");
				$("#tmovimiento").change();
			}

		});

		$("#tmovimiento").change(function () {
			$("#idTipoMovimiento").val($(this).val());
		});
		$("#ctaBancaria").change(function () {
			$("#CTAB").val($("#ctaBancaria").val());
		});
	} catch (err) {
		console.error("Error al inicializar la UI: ", err);
		errorFatal = true;
		errorFatalMSG = "No se logro obtener el estatus del pago.";

	}
	if (errorFatal) {
		Swal.fire("Ocurrio el siguiente error y no puede continuar el proceso:", errorFatalMSG, 'error');
		deshabilitaTodo();
		return;
	}

	try {
		var idEstatus = obtenPagoEstatus();
		initPestanaPAAAS();
		creaDTFacturas();
		creaDTRetenciones();
		setInitialValuesConcepto();
		leeEncabezado();
		conceptoPagoDirectoRead();
		leeDatosFactura();
		if (idEstatus == 2)
			iniciaCapturaRetenciones();
	} catch (eCarga) {
		if (eCarga.message)
			errorFatalMSG = eCarga.message;
		else
			errorFatalMSG = eCarga;

		errorFatal = true;
		Swal.fire("Error", errorFatalMSG, "error");
		deshabilitaTodo();
		throw eCarga;
	}

	$("#AgregarLineasAgrup").button();
	$("#btnGuardarLineasPagoDirecetoPAAS").button().click(function () {
		actualizaLineas();
	});

}

function creaDTResumenFacturas() {
	oTablevFact = $('#grdResumenFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ResumenCFDI&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='PAGODIRECTO'",
			"bJQueryUI": true,
			"sScrollX": "100%",

			"sScrollY": "150px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			aoColumns: [
				{
					sName: "UUID"
				},
				{
					sName: "mImporteBruto"
				},
				{
					sName: "mimporteiva"
				},
				{
					sName: "mOtrosImpuestos"
				},
				{
					sName: "mImporteDescuento"
				},
				{
					sName: "mImporteRetencion"
				},
				{
					sName: "mimporteconiva"
				}]
		});
}

function creaDTFacturas() {
	oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ResumenCFDI&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='PAGODIRECTO'",
			"bJQueryUI": true,
			"sScrollX": "100%",
			"sScrollY": "125px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			aoColumns: [
				{
					sName: "UUID"
				},
				{
					sName: "mImporteBruto"
				},
				{
					sName: "mimporteiva"
				},
				{
					sName: "mOtrosImpuestos"
				},
				{
					sName: "mImporteDescuento"
				},
				{
					sName: "mImporteRetencion"
				},
				{
					sName: "mimporteconiva"
				}]
		});
}



function creaDTRetenciones() {
	var idEstatus = obtenPagoEstatus();
	if (idEstatus == 2) {
		oTableRetencion = $('#grdRetencion').dataTable({
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPagoDirectoRetencion&qw=nFolioPagoDirecto=" + $("#nFolioPago").val() + " AND 1=1",
			"bJQueryUI": true,
			"sScrollX": "100%",
			"sScrollXInner ": "100%",
			"sScrollY": "145px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			aoColumns: [
				{
					sName: "cIdTipoRetencion"
				},
				{
					sName: "cTipoRetencion"
				},
				{
					sName: "base"
				},
				{
					sName: "porcentajeRetencion"
				},
				{
					sName: "retencion"
				},
				{
					sName: "CMD"
				}]
		});

		$("#grdRetencion tbody").click(function (event) {

			$(oTableRetencion.fnSettings().aoData).each(function () {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

		});

		$("#grdRetencion tbody").dblclick(function (event) {

			$(oTableRetencion.fnSettings().aoData).each(function () {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTableRetencion.fnGetPosition(event.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			var id = $("#grdRetencion").dataTable().fnGetData()[currIndex][0];
			var ret = $("#grdRetencion").dataTable().fnGetData()[currIndex][1];
			var retCalculada = $("#grdRetencion").dataTable().fnGetData()[currIndex][4];

			$("#idRetencionEdit").val(id);
			$("#descRetencionEdit").val(ret);
			$("#importeCalculadoEdit").val(retCalculada);
			$("#nuevoImporteRetEdit").focus();

			$("#dlg-EditaRetencion").dialog("open");

		});

		queryFormPost("sumRetencionesCaptPDRead", {
			async: false
		});
	}
}

function creaDTEPs() {
	var mes = $("#fAplicacion").val().split("/")[1];
	var folio = $("#nFolioPagoDirecto").val();
	var login = $("#login").val();
	var fuenteFinanciamiento = 1;

	oTableEPs = $("#tblEP").dataTable(
		{
			"bDestroy": true,
			fnDrawCallback: function () { },
			bAutoWidth: true,
			oLanguage: es_MX,
			bServerSide: true,
			"sScrollX": "100%",
			"sScrollXInner ": "110%",
			"sScrollY": "100px",
			"fnServerData": function (sSource, aoData, fnCallback) {
				$.ajax({
					"dataType": 'json',
					"type": "POST",
					"url": sSource,
					"data": aoData,
					"success": fnCallback
				});
			},
			sAjaxSource: window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=v_saldocompromisos&qw=cIdContrato='" + $("#cIDContrato").val() + "'",
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aoColumns: [{
				sName: "ep"
			}, {
				sName: "mCompromisoEnero",
				sClass: "money"
			}, {
				sName: "mCompromisoFebrero",
				sClass: "money"
			}, {
				sName: "mCompromisoMarzo",
				sClass: "money"
			}, {
				sName: "mCompromisoAbril",
				sClass: "money"
			}, {
				sName: "mCompromisoMayo",
				sClass: "money"
			}, {
				sName: "mCompromisoJunio",
				sClass: "money"
			}, {
				sName: "mCompromisoJulio",
				sClass: "money"
			}, {
				sName: "mCompromisoAgosto",
				sClass: "money"
			}, {
				sName: "mCompromisoSeptiembre",
				sClass: "money"
			}, {
				sName: "mCompromisoOctubre",
				sClass: "money"
			}, {
				sName: "mCompromisoNoviembre",
				sClass: "money"
			}, {
				sName: "mCompromisoDiciembre",
				sClass: "money"
			}]
		});

	$("#tblEP tbody").click(function (event) {

		$(oTableEPs.fnSettings().aoData).each(function () {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblEP tbody").dblclick(function (event) {

		$(oTableEPs.fnSettings().aoData).each(function () {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableEPs.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		if ($("#tConcepto").val() == "" || $("#tConcepto").val() == "-1" || $("#tmovimiento").val() == "" || $("#tmovimiento").val() == null) {
			Swal.fire("Verifique", "Debe seleccionar el concepto y el movimiento antes de ingresar los montos", "info");
		} else {
			modalEP.show();
			var ep = $("#tblEP").dataTable().fnGetData()[currIndex][0];
			$("#epShow").val(ep);
			$("#epConsulta").val(ep);
			$("#mesConsulta").val(mes);
			queryFormPost("saldoComprometidoAcumRead", {
				async: false
			});
			cambiafrmt($("#disponibleEPTotal")[0]);
		}

	});
}


function windowStatus(texto) {
	window.status = texto;
}

function createDTCalendario() {
	var cWhere = " nFolioPago=" + $("#nFolioPago").val() + " AND cTipoPago='" + $("#cTipoPago").val() + "'";

	oTableSaldos = $("#tblCalendario").dataTable(
		{
			"bDestroy": true,
			fnDrawCallback: function () { },
			bAutoWidth: true,
			oLanguage: es_MX,
			bServerSide: true,
			"sScrollX": "100%",
			"sScrollY": "100px",
			"fnServerData": function (sSource, aoData, fnCallback) {
				$.ajax({
					"dataType": 'json',
					"type": "POST",
					"url": sSource,
					"data": aoData,
					"success": fnCallback
				});
			},
			sAjaxSource: window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=vImportesCalendario&qw=" + cWhere,
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aoColumns: [{
				sName: "EP"
			}, {
				sName: "mImporteBruto",
				sClass: "money"
			}, {
				sName: "tipoConcepto"
			}, {
				sName: "tipoMovimiento"
			}]
		});

	$("#tblCalendario tbody").click(function (event) {

		$(oTableSaldos.fnSettings().aoData).each(function () {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblCalendario tbody").dblclick(function (event) {

		$(oTableSaldos.fnSettings().aoData).each(function () {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableSaldos.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var ep = $("#tblCalendario").dataTable().fnGetData()[currIndex][0];

		$("#epBorrar").val(ep);

		if (confirm("Esta seguro de eliminar la clave\n" + ep + "]\n del calendario?")) {
			queryFormPost({
				queryName: "eliminaEPCalendario",
				async: false,
				callback: function () {
					creaDTEPs();
					createDTCalendario();
				}
			});
		}

	});

	leeTotalCalendarizado();
}


function aplicaEstilos() {
	$('input[type=text][readonly]').each(function () {
		$(this).addClass("notEditable");
	});

	$(".fecha").each(function () {
		$(this).datepicker({
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
	});
}

/**
 * Lee el estatus actual del pago. Los estaus se corresponden al catalogo: 
 */
function obtenPagoEstatus() {
	var estatusEncontrado = false;
	$("#nIDEstatus").val("-1");

	queryFormPost(
		{
			queryName: "estatusPagoDirectoRead",
			async: false,
			callback: function () {
				estatusEncontrado = true;
			}
		});
	if (estatusEncontrado)
		return parseInt($("#nIDEstatus").val(), 10);
	else
		throw 500;
}
function ejecutaValidaciones(idOper, idEstatus) {
	let success = false;

	if (idOper == 1 && idEstatus == 0) {
		leeImporteBruto();
		return validacionesPAAS() && confirm("¿Esta seguro de continuar con el siguiente paso?");
	} else if (idOper == 1 && idEstatus == 1) {
		return validacionesConcepto();
	} else if (idOper == 1 && idEstatus == 2) {

		success = validacionesRetenciones();
		if (success)
			getPartidasExcedenUMA();

		return success;

	} else if (idOper == 1 && idEstatus == 3) {

		$.when(getPartidasExcedenUMA()).done(function () { success = validacionesCalendario(); });
		return success;

	} else if (idOper == 1 && idEstatus == 4) {

		$.when(getPartidasExcedenUMA()).done(function () { success = validacionesCapturaCompleta(); });
		return success;

	} else if (idOper == 1 && idEstatus == 5) {

		$.when(getPartidasExcedenUMA()).done(function () { success = validacionesPAAS(); });
		return success;

	} else if (idOper == 2 && idEstatus == 6) {

		$.when(getPartidasExcedenUMA()).done(function () { success = validacionesAutorizacion(); });
		return success;

	} else
		throw "No existen validaciones para la operación : " + idOper + " estatus: " + idEstatus;

}


function avanzaEstatus(idOper) {
	var idEstatus = parseInt($("#nIDEstatus").val(), 10);
	var terminado = true;
	var logErrores = "";

	if (idOper == 1) {
		try {
			if (idEstatus < 6) {

				if (idEstatus == 0) {
					guardaPagoDirectoEncabezado();
				} else if (idEstatus == 1) {
					actualizaPagoDirectoEncabezado();
				} else if (idEstatus == 2) {
					actualizaImportesPagoDirecto();
				}

			}
		} catch (e) {
			terminado = false;
			logErrores = e;
		}
	}

	if (!terminado) {
		throw "No fue posible terminar la operacion debido a:\n" + logErrores;
	} else {
		obtenPagoEstatus();
	}
}

function actualizaImportesPagoDirecto() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url: "../egresos/updateMontoRetenciones",
		type: 'post',
		async: false,
		data: $("#formPagos").serialize(),
		dataType: 'json',
		success: function (j) {
			var exito = j.success;

			if (exito) {
				guardado = true;
			} else {
				var errores = j.errorList;
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
			}
		},
		error: function (errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		throw logErrores;
}

function actualizaPagoDirectoEncabezado() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url: "../egresos/updateHeader",
		type: 'post',
		async: false,
		data: $("#formPagos").serialize(),
		dataType: 'json',
		success: function (j) {
			var exito = j.success;

			if (exito) {
				guardado = true;
			} else {
				var errores = j.errorList;
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
			}
		},
		error: function (errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		throw logErrores;
}

function guardaPagoDirectoEncabezado() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url: "../egresos/saveHeader",
		type: 'post',
		async: false,
		data: $("#formPagos").serialize(),
		dataType: 'json',
		success: function (j) {
			var exito = j.success;

			if (exito) {
				guardado = true;
			} else {
				var errores = j.errorList;
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
			}
		},
		error: function (errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		throw logErrores;
}

/**
 * Actualiza los tabs en la pantalla ocultando/mostrando dependiendo el estatus del pago.
 */

var stages = {
	stages: [{
		"ejecutado": false,
		"estaus": 0,
		"tab": 1,
		"id": "PAAS",
		"fnTerminado": function () {
			return paasTerminado();
		}
	}, {
		"ejecutado": false,
		"estaus": 1,
		"tab": 2,
		"id": "Concepto",
		"fnTerminado": function () {
			return conceptoTerminado();
		}
	}, {
		"ejecutado": false,
		"estaus": 2,
		"tab": 3,
		"id": "Retenciones",
		"fnTerminado": function () {
			return retencionesTerminado();
		}
	}, {
		"ejecutado": false,
		"estaus": 3,
		"tab": 4,
		"id": "Movimientos",
		"fnTerminado": function () {
			return movimientosTerminado();
		}
	}, {
		"ejecutado": false,
		"estaus": 4,
		"tab": 5,
		"id": "Resumen",
		"fnTerminado": function () {
			return capturaTerminada();
		}
	}, {
		"ejecutado": false,
		"estaus": 5,
		"tab": 5,
		"id": "Resumen",
		"fnTerminado": function () {
			return cxpGenerado();
		}
	}]
}

/**
 * Funcion llamada cuando cambia el estatus del pago.
 */
function estatusCambiado(idOperacion) {
	try {
		var idEstatus = obtenPagoEstatus();
		procesaPestanas(idOperacion, idEstatus);
	} catch (err) {
		throw err;
	}
}

function procesaPestanas(idOperacion, idEstatus) {

	var stg = null;
	if (idEstatus == 0) {
		queryFormPost(
			{
				queryName: "foliosCompromisoPagoDirectoRead",
				async: true,
				callback: function () {
					if ($("#folioAutSICOP").val() == "" || $("#folioAutSICOP").val() == null) {
						$("#suficienciaDiv").show();
						creaDTSuficiencia();
					} else {
						 capturePAAS();
						 
						
					}
				}
			});
	} else {

		for (cnt = 0; cnt < idEstatus; cnt++) {
			stg = stages.stages[cnt];
			if (stg.ejecutado != true) {
				var funcionEjecutada = stg.fnTerminado();
				if (!funcionEjecutada)
					throw "No se logro ejecutar la funcion callback del paso : " + cnt;
				else
					stg.ejecutado = true;
			}
		}

		stg = stages.stages[idEstatus];
		var optName = stg.id;
		$("#" + optName).show();
		$("#Tab" + optName).show();
		$("#" + optName).click();
	}

}

function deshabilitaTodo() {
	$.blockUI();
	if (parent.document.getElementById("pb_save")) {
		parent.document.getElementById("pb_save").disabled = true;
		parent.document.getElementById("pb_save").style.display = "none";
	}
}

/**
 * Lee de la base de datos los montos guardados de la factura.
 */
function leeDatosFactura() {
	queryFormPost({
		queryName: "readMontoFacturasPD",
		async: false,
		callback: function () {
			mImporteIVA = quitaFmt($("#Imp_Iva").val())
		}
	});
}

function leeEncabezado() {
	queryFormPost({
		queryName: "pagoDirectoEncRead",
		async: false,
		callback: function () {
			//TODO actualizar los selects 
		}
	});
}


/**
 * Funcion para evitar la captura de letras. Permite que se caputuren solo
 * monntos numericos.
 * 
 * @param evt
 *            Evento de keypress
 * @returns {Boolean} si y solo si la tecla presionada esta en 0123456789.
 */
function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 47) {
		return false;
	}
	return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

function actualizaEstatus() {
	var actualizado = false;

	$.ajax({
		url: "../egresos/avanzaEstatus",
		type: 'post',
		async: false,
		data: $("#formPagos").serialize(),
		dataType: 'json',
		success: function (j) {
			var exito = j.success;

			if (exito) {
				actualizado = true;
			} else {
				var errores = j.errorList;
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
			}
		},
		error: function (errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	return actualizado;
}


function getPartidasExcedenUMA(idRFC) {

	$.ajax({
		url: '../egresos/validaMontoTotalizado',
		type: "GET",
		dataType: 'json',
		data: $("#formPagos").serialize(),
		async: false,
		success: function (json) {

			if (json.success) {
				var partidasExcenden = json.partidasExcenden;
				var msg = "";

				for (var k = 0; k < partidasExcenden.length; k++) {
					msg += "La partida " + partidasExcenden[k].partida +
						" excede con el monto " + partidasExcenden[k].montoNeto +
						" el total de pagos directos para el proveedor " + partidasExcenden[k].rfc + " - " + partidasExcenden[k].razonSocial +
						" de 300 umas.";
				}
				if (msg != "") {
					parent.document.getElementById("pb_send").disabled = true;
					parent.document.getElementById("pb_save").disabled = true;
					descartaPagoExcedido(msg);
				} else {
					parent.document.getElementById("pb_save").disabled = false;
				}
			} else {
				alert("No es posible continuar debido al siguiente error: " + json.errorList);
			}
		},
		error: function (xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});

}

function descartaPagoExcedido(msg) {
	Swal.fire({
		title: 'No puede continuar el pago!',
		text: "Se encontraron los siguientes problemas: \n\n" + msg + "\n\nSi necesita continuar con el pago envíe un correo a " + excepcionUser + " al correo " + excepcionEmail + " con la justificacion del pago.",
		icon: 'error',
		showCancelButton: false,
		confirmButtonText: 'Aceptar'
	}).then((result) => {
		parent.document.getElementById("pb_cancel").disabled = false;
		$.blockUI();
		parent.document.forms["frmCancel"].submit();

	})
}