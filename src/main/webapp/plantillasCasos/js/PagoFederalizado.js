var oTablevFact;
var oTableCucop;
var oTableLineas;
var oTableRetencion;
var oTableEPs;
var oTableSaldos;
var oTableCalendarioPago
var retencionesCargadas = false;
var toleracia = 0.01;
let modalEP;
let modalRetenciones;
let modalCorreo;

var es_MX = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtado de _MAX_ registros)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Buscar:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};

function initUI() {
	try {
					
		aplicaEstilos();
		creaDiagloFacturas();
		obtenPagoEstatus();
		cargaDestinoGasto();
		
		// ========== Asignacion de Funciones ======= 
		$("#cargaFacturasBtn").button().click(function() {
			muestraDivFacturas();
			
		});
		
		modalEP = new bootstrap.Modal(document.getElementById('dlg-CalendarioMontos'), 'data-bs-backdrop');
		modalRetenciones = new bootstrap.Modal(document.getElementById('dlg-EditaRetencion'), 'data-bs-backdrop');
		modalCorreo = new bootstrap.Modal(document.getElementById('dialog-actualizaCorreo'), 'data-bs-backdrop');
		

		$("#capturaCorreo").hide();
		$("#DESTINO_GASTO").change(function() {

			$("#idDestinoGasto").val($("#DESTINO_GASTO").val());

			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				querySelectPost("CatalogoObraTConceptoRead", "tConcepto", {
					async : false
				});
			} else {
				limpiaSelect("tConcepto", "-1", "Seleccione Tipo de Concepto");
				$("#tConcepto").change();
			}
		});

		$("#tConcepto").change(function() {
			$("#idTipoConcepto").val($(this).val());
			$("#idConcepto").val($(this).val());
			$("#TIPO_CONCEPTO").val($(this).val());

			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				querySelectPost({
					queryName : 'CatalogoObraTMovimendoRead',
					targetObjectId : 'tmovimiento',
					async : false,
					callback : function() {
						$("#tmovimiento").change();
					}
				});
			} else {
				limpiaSelect("tmovimiento", "-1", "Seleccione Tipo de Movimiento");
				$("#tmovimiento").change();
			}

		});

		$("#tmovimiento").change(function() {
			$("#idTipoMovimiento").val($(this).val());
		});
		
		$("#ctaBancaria").change(function() {
			$("#CTAB").val($("#ctaBancaria").val());
		});
	} catch (err) {
		errorFatal = true;
		errorFatalMSG = "No se logro obtener el estatus del pago.";

	}
	if (errorFatal) {
		Swal.fire('Ocurrio el siguiente error y no puede continuar el proceso:\n' + errorFatalMSG, msg, 'error');
		deshabilitaTodo();
		return;
	}

	try {
		var idEstatus = obtenPagoEstatus();
		creaDTFacturas();
		creaDTRetenciones();
		leeEncabezado();
		leeDatosFactura();
		if (idEstatus == 1)
			iniciaCapturaRetenciones();
	} catch (eCarga) {
		if (eCarga.message)
			errorFatalMSG = eCarga.message;
		else
			errorFatalMSG = eCarga;

		errorFatal = true;
		Swal.fire('Error',errorFatalMSG, 'error');
		deshabilitaTodo();
		throw eCarga;
	}


}
function conceptoPagoRead() {
	var error = null;

	queryFormPost({
		queryName : "conceptoPagoFedRead",
		async : false,
		callback : function() {
			try {
				$("#DESTINO_GASTO").val($("#idDestinoGasto").val());
				$("#DESTINO_GASTO").change();
				cargaCtaBancariaRFC();
			} catch (err) {
				error = err;
			}
		}
	});

	if (error)
		Swal.fire("Ocurrio el siguiente error"," y no se podra continuar: " + err.message, "error");
}
function cargaDestinoGasto() {
	querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {
		async : false
	});
	querySelectPost("catalogoTipoPagoDirectoRead", "TIPO_OPERACION", {async : false});
}

function muestraDivFacturas() {
	if (conceptoMinimoCapturado()) {
		creaDiagloFacturas(true);
		return true;
	} else {
		return false;
	}
}

function conceptoMinimoCapturado() {
	var msg = "";
	var completado = true;

	if ($.trim($("#TIPO_OPERACION").val()) == "" || $.trim($("#TIPO_OPERACION").val()) == "-1") {
		msg += "\nSeleccione el tipo de pago directo.";
		completado = false;
	}
	if ($.trim($("#DESTINO_GASTO").val()) == "" || $.trim($("#DESTINO_GASTO").val()) == "-1") {
		msg += "\nSeleccione el tipo de destino.";
		completado = false;
	}
	if ($.trim($("#concepto").val()) == "") {
		msg += "\nCapture el concepto del pago.";
		completado = false;
	}
	if ($.trim($("#cIDContratoFed").val()) == "") {
		msg += "\nSeleccione el contrato pago.";
		completado = false;
	}
	if ($.trim($("#cIDRFC").val()) != "" && $.trim($("#ctaBancaria").val()) == "") {
		msg += "\nEl beneficiario no cuenta con cuenta bancaria autorizada. Reporte al administrador.";
		completado = false;
	}
			
	if ($.trim($("#cIDRFC").val()) == "BMN930209927") {
		if ($("#cProgramaDesc").val() =="0"){
			msg += "\nSeleccione el Programa";
			completado = false;
			}
		if ($("#cSubProgramaDesc").val() =="00"){
			msg += "\nSeleccione el SubPrograma";
			completado = false;
			}
	}
	
	if (msg != "")
		Swal.fire("Revise","Para continuar debe capturar lo siguiente:\n" + msg, "warning");

	return completado;
}


function setInitialValuesConcepto() {
	/* ========== Valores Fijos ======= */
	$("#nFolioPagoFed").val($("#nFolioPago").val());
	$("#fAplicacion").val($("#fechaAplicacion").val());

	var idEstatus = obtenPagoEstatus();
	if (idEstatus == 1) {
		cargaDestinoGasto();
	}
	
	DatosBeneficiario();
}


function creaDiagloFacturas(abrir) {
	$("#dialog-validaFact").dialog({
		autoOpen : false,
		height : 500,
		width : 860,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				creaDTFacturas();
				queryFormPost("readMontoFacturasPF",  {async : false});
				$(this).dialog("close");
			},
			"Cancelar" : function() {
				bClicBtn = true;
				$(this).dialog("close");
			}
		},
		close : function() {
			revisaTipoFacturas();
			parent.document.getElementById("pb_save").disabled = false;
		},
		open : function() {
			$("#uploadFacturasDiv").show();
			leeDatosFactura();
			parent.document.getElementById("pb_save").disabled = true;
		}
	});

	if (abrir) {
		$("#dialog-validaFact").dialog("open");
	}
}

	
		
function creaDTResumenFacturas() {
	oTablevFact = $('#grdResumenFacturas').dataTable(
		{
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ResumenCFDI&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='PAGOFEDERALIZADO'",
			"bJQueryUI" : true,
			"sScrollX" : "100%",
			"sScrollY" : "100%",
			"bPaginate" : false,
			"bAutoWidth" : false,
			"bInfo" : false,
			oLanguage : es_MX,
			aoColumns : [
				{
					sName : "UUID"
				},
				{
					sName : "mImporteBruto"
				},
				{
					sName : "mimporteiva"
				},
				{
					sName : "mImporteRetencion"
				},
				{
					sName : "mimporteconiva"
				} ]
		});
}

function creaDTFacturas() {
	oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ResumenCFDI&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='PAGOFEDERALIZADO'",
			"bJQueryUI" : true,
			"sScrollX" : "100%",
			"sScrollY" : "100%",
			"bPaginate" : false,
			"bAutoWidth" : true,
			//"bInfo" : true,
			oLanguage : es_MX,
			aoColumns : [
				{
					sName : "UUID"
				},
				{
					sName : "mImporteBruto"
				},
				{
					sName : "mimporteiva"
				},
				{
					sName : "mImporteRetencion"
				},
				{
					sName : "mimporteconiva"
				} ]
		});
}



function creaDTRetenciones() {
	var idEstatus = obtenPagoEstatus();
	if (idEstatus == 1) {
		oTableRetencion = $('#grdRetencion').dataTable({
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPagoRetencion&qw=nFolioPago=" + $("#nFolioPago").val() + " AND cTipoPago = 'PAGOFEDERALIZADO'",
			"bJQueryUI" : true,
			"sScrollX" : "100%",
			"sScrollXInner " : "100%",
			"sScrollY" : "145px",
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bInfo" : true,
			oLanguage : es_MX,
			aoColumns : [
				{
					sName : "cIdTipoRetencion"
				},
				{
					sName : "cTipoRetencion"
				},
				{
					sName : "mImporteBruto"
				},
				{
					sName : "nPorcRetencion"
				},
				{
					sName : "importeRetencion"
				} ]
		});

		$("#grdRetencion tbody").click(function(event) {

			$(oTableRetencion.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

		});

		$("#grdRetencion tbody").dblclick(function(event) {

			$(oTableRetencion.fnSettings().aoData).each(function() {
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

			modalRetenciones.show();

		});

		queryFormPost("sumRetencionesCaptRead", {
			async : false
		});
	}
}


function windowStatus(texto) {
	window.status = texto;
}


function aplicaEstilos() {
	
	$(".fecha").each(function() {
		$(this).datepicker({
			showOn : "button",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
	});
}

/**
 * Lee el estatus actual del pago. Los estaus se corresponden al catalogo: 
 */
function obtenPagoEstatus() {
	var estatusEncontrado = false;
	
	queryFormPost(
		{
			queryName : "estatusPagoFedRead",
			async : false,
			callback : function() {
				estatusEncontrado = true;
			}
		});
	if (estatusEncontrado)
		return parseInt($("#nIDEstatus").val(), 10);
	else
		throw 500;
}
function ejecutaValidaciones(idOper, idEstatus) {
	 if (idOper == 1 && idEstatus ==0) {
		return validacionesEncabezado();
	} else if (idOper == 1 && idEstatus == 1) {
		return validacionesRetenciones();
	} else if (idOper == 1 && idEstatus == 2) {
		return validacionesCalendarioFed();
	} else if (idOper == 1 && idEstatus == 3) {
		return true; //validacionesCapturaCompleta();
	} else if (idOper == 1 && idEstatus == 4) {
		return validacionesCapturaCompleta();
	}  else if (idOper == 1 && idEstatus == 5) {
		return true;
	} else
		throw "No existen validaciones para la operacion : " + idOper + " estatus: " + idEstatus;

}


function avanzaEstatus(idOper) {
	var idEstatus = parseInt($("#nIDEstatus").val(), 10);
	var terminado = true;
	var logErrores = "";

	if (idOper == 1) {
		try {
			if (idEstatus < 5) {

				if (idEstatus == 0) {
					guardaPagoFederalizadoEncabezado();
				} else if (idEstatus == 1) {
					actualizaPagoFederalizadoEncabezado();
				} else if (idEstatus == 2) {
					actualizaImportesPagoFederalizado();
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

function actualizaImportesPagoFederalizado() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url : "../egresos/updateMontoRetenciones",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
		dataType : 'json',
		success : function(j) {
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
		error : function(errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		Swal.fire("No se guardo la retención", logErrores, "error");
		
}

function actualizaPagoFederalizadoEncabezado() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url : "../egresos/updateHeader",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
		dataType : 'json',
		success : function(j) {
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
		error : function(errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		Swal.fire("No se guardo", logErrores, "error");
}

function validaCaptura() {
	let valida = true;
	if ($("#cIDRFC").val().trim() =="BMN930209927") {
		if($("#cProgramaDesc").val()== -1) {
			valida = false;
			Swal.fire("Capture","Seleccione el programa del FFM", "warning");
		} else if($("#cSubProgramaDesc").val()== -1) {
			valida = false;
			Swal.fire("Capture","Seleccione el Subprograma del FFM", "warning");
		}
	}
	return valida;
}

function guardaPagoFederalizadoEncabezado() {
	var guardado = false;
	var logErrores = "";
	
	if (validaCaptura()) {
	
		$.ajax({
			url : "../egresos/saveHeader",
			type : 'post',
			async : false,
			data : $("#formPagos").serialize(),
			dataType : 'json',
			success : function(j) {
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
			error : function(errorThrown) {
				alert("error: " + errorThrown.ERROR);
				logErrores = "Error guardando encabezado.";
			}
		});

	if (!guardado)
		Swal.fire("No se guardo", logErrores, "error");
		
	}

}

/**
 * Actualiza los tabs en la pantalla ocultando/mostrando dependiendo el estatus del pago.
 */

var stages = {
	stages : [ {
		"ejecutado" : false,
		"estaus" : 0,
		"tab" : 1,
		"id" : "Concepto",
		"fnTerminado" : function() {
			return conceptoTerminado();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 1,
		"tab" : 2,
		"id" : "Retenciones",
		"fnTerminado" : function() {
			return retencionesTerminado();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 2,
		"tab" : 3,
		"id" : "Movimientos",
		"fnTerminado" : function() {
			return movimientosTerminado();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 3,
		"tab" : 4,
		"id" : "Resumen",
		"fnTerminado" : function() {
			return muestraCaptura();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 4,
		"tab" : 4,
		"id" : "Resumen",
		"fnTerminado" : function() {
			return capturaTerminada();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 5,
		"tab" : 4,
		"id" : "Final",
		"fnTerminado" : function() {
			return capturaTerminada();
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
	if (idOperacion == 1) {
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
		queryName : "readMontoFacturasPF",
		async : false,
		callback : function() {
			mImporteIVA = quitaFmt($("#Imp_Iva").val())
		}
	});
	
	$("#mTotalFacturaV").val(quitaFmt($("#mTotalFacturaV").val()));
	$("#mImporteIVA").val(quitaFmt($("#mImporteIVA").val()));
	$("#totalFactura").val(quitaFmt($("#totalFactura").val()));
}

function leeEncabezado() {
	
	queryFormPost({
		queryName : "pagoFederalizadoEncRead",
		async : false,
		callback : function() {
			//TODO actualizar los selects 
		}
	});
	
	$("#cIDContratoFed").val($("#cIDContrato").val());
}

function conceptoTerminado() {
	var exito = false;
	$.ajax({
		url : '../egresos/resumenConcepto',
		type : "GET",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				for (var k in resumen) {
					$("#" + k).append(resumen[k]);
				}
				$("#Concepto").show();
				$("#TabConcepto").show();
				$("#resumenCaratula").show();
				$("#listaFacturas").hide();
				$("#capturaConceptoPago").hide();
				creaDTResumenFacturas();
			} else {
				Swal.fire("No es posible continuar debido al siguiente error: ", json.errorList,"error");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});
	return exito;
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
	var keyPressed = (evt.which) ? evt.which : evt.keyCode;
	if (keyPressed == 47) {
		return false;
	}
	return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

function Sinfrmt(fld) {
				var valcol = fld.value;
				valcol = valcol.replace(/$/g, "");
				valcol = valcol.replace(/,/g, "");
				$("#" + fld.id).val(valcol);
			}

function actualizaEstatus() {
	var actualizado = false;

	$.ajax({
		url : "../egresos/avanzaEstatus",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
		dataType : 'json',
		success : function(j) {
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
		error : function(errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	return actualizado;
}

function onLoadPlantilla(id_oper) { //Carga Plantilla
	var resp = true;
	switch (parseInt(id_oper, 10)) {
	//Captura
	case 1:

		try {
			$("#idCaso").val(idCaso);
			$("#numEmpleadoElab").val(numeroEmpleado);
			//if (parent.document.getElementById("pb_save")) {
				//parent.document.getElementById("pb_save").disabled = true;
			//}
			$("#tabs").tabs();
			initUI();
			estatusCambiado(id_oper);
			
			actualizaGabinete();

		} catch (e) {
			var msg = "";
			if (e instanceof TypeError)
				msg = e.message;
			else
				msg = e;

			Swal.fire(" Si el problema continua reporte al administrador.","Error cargando informacion inicial \nCasua:" + msg + "\n Intente nuevamente.","error");
			deshabilitaTodo();
		}
	case 2: //
		break;
	default:
		resp = false;
		break;
	} 
	return resp;
}

/**
 * funcion llamada al dar clic boton Guardar.
 */

function onSubmit(id_oper) {
	var ejecucionCorrecta = ejecutaValidaciones(id_oper, $("#nIDEstatus").val());
	switch (parseInt(id_oper, 10)) {

	case 1: /*Captura*/
		ejecucionCorrecta = ejecucionCorrecta && setVariablesCaso(id_oper);

		if (ejecucionCorrecta) {
			try {
				avanzaEstatus(id_oper);
				avanzaTab = true;
			} catch (e) {
				alert(e);
				ejecucionCorrecta = false;
			}

		}
		break;
	
	default:
		ejecucionCorrecta = false;
		break;
	}
	return ejecucionCorrecta;
}

function ResponsableSiguiente(id_oper) {
	switch (parseInt(id_oper, 10)) {
	case 1: // Captura
		return "AUTORIZA_PAGOFEDERALIZADO";
		break;
	case 2:
		return "CONSULTA_PAGOFEDERALIZADO";
		break;
	}
}
function OperacionSiguiente(id_oper) {
	switch (parseInt(id_oper, 10)) {
	case 1: //Captura
		return "autoriza_factura";
		break;
	case 2:
		return "consulta_factura";
		break;
	} // fin switch
} 

function onPostDisplay(id_oper) {
	estatusCambiado(id_oper);
	return true;
} 

function onPostSubmit(id_oper) { // clic boton enviar

	var respSig = parent.document.getElementById("responsable").value;
	if (respSig == "") {
		parent.execResponsable();
		parent.execOperacion();
	}
	$.blockUI();
	return true;
}

function setVariablesCaso(idOperacion) {
	var ejecucionCorrecta = true;
	try {
		var operacion = parseInt(idOperacion, 10);
		var estatus = parseInt($("#nIDEstatus").val(), 10);

		/*Para evitar sobreescribir la informacion. Solo en el primer guardar se establecen los valores de las variables*/
		if (operacion == 1 && estatus == 0) {
			p.gestion.setFolio($("#cFolio").val());
			p.gestion.setOperador($("#operador").val());
			p.gestion.setFechaDocumento($("#fechaCaptura").val());
			p.gestion.setEjercicioFiscal($("#ejercicioFiscal").val());
		}
	} catch (e) {
		window.alert("onSubmit: Error: " + e);
		parent.document.getElementById("pb_send").disabled = true;
		ejecucionCorrecta = false;
		throw e;
	}
	return ejecucionCorrecta;
}

function actualizaGabinete() {
	var ejecutado = false;

	queryFormPost({
		queryName : "idGabineteRead",
		async : false,
		callback : function() {
			ejecutado = true;
		}
	});

	if (!ejecutado)
		Swal.fire("Intente nuevamente", "No se logo consultar el ID Gabinete. ", "warning");
}

function validacionesEncabezado() {
	var msg = capturaEncabezadoCompleta();
	if (msg != "") {
		Swal.fire('Para continuar debe corregir lo siguiente:', msg, 'warning');
		return false;
	} else {
		return true;
	}

}
function capturaEncabezadoCompleta() {
	var msg = "";
	var facturaCapturdas = getFacturasCapturadas();

	if (facturaCapturdas == 0)
		msg += "\nDebe cargar las facturas del pago.";
	
	var mensajeValido = validaFacturas(); 
	if(mensajeValido != "\n")
		msg += "\n" + mensajeValido;
		
	$("#mTotalFacturaV").val(quitaFmt($("#mTotalFacturaV").val()));
	$("#mImporteIVA").val(quitaFmt($("#mImporteIVA").val()));
	$("#totalFactura").val(quitaFmt($("#totalFactura").val()));
	
	//Valida que si tiene PPD se haya capturado el correo
	var mensaje = validaCapturaCorreo();
	if (mensaje != "")
		msg += "\n" + mensaje;
	
	return msg;
}

function getFacturasCapturadas() {
	
	$("#nFacturasCapturadas").val(0);
	queryFormPost("facturasCapturadasRead", { async : false });
	return parseInt(($("#nFacturasCapturadas").val() == "" ? 0 : $("#nFacturasCapturadas").val()), 10);
	
}

function validaFacturas(){
	var msg = ""
	queryFormPost("consultaSolicitudesSinRetencion", { async : false });
	if($("#listaSol").val()!= "")
		msg = "El régimen fiscal del Proveedor es RESICO, es persona Fisica y las siguientes facturas no tienen retención. Descarte el pago!\n" + $("#listaSol").val();
	
	queryFormPost("validaImporteResico", { async : false })
	msg += "\n" + $("#msgRF").val();
	
	return msg;
}
function DatosBeneficiario() {
		//Carga datos del beneficiario
		$("#cIDContrato").val($("#cIDContratoFed").val());
		queryFormPost("datosBeneficiariosRead", {async : false});
	
	if ($.trim($("#cIDRFC").val()) == "BMN930209927") {
				$("#tblPrograma").show();			
				querySelectPost("catFideicomisoRead", "cProgramaDesc", {async : false});
				queryFormPost("catalogoSubprgRead", "cSubProgramaDesc", {async : false});
				querySelectPost("CatalogoDGastoFFMRead", "DESTINO_GASTO", {async : false});			
	} else {
		$("#tblPrograma").hide();
		querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async : false});
	}
}

/**
 * Actualiza los subprogramas segun la seleccion del programa
 */
function cambiaPrograma() {
	$("#nIDPrograma").val($("#cProgramaDesc").val());
	clearSelect("cSubProgramaDesc");
	querySelectPost("catalogoSubprgRead", "cSubProgramaDesc", {
		async : false
	});
}

function cambiaSubPrograma() {
	
	$("#nIdSubPrograma").val($("#cSubProgramaDesc").val());
}
/**
 * Funcion general que limpia el contenido de un select agregando una opcion por
 * default con valor -1
 */
function clearSelect(idSel) {
	for (var i = 0; i < idSel.length; i++)
		$('#' + idSel[i]).find('option').remove().end().append(
			'<option value="-1"></option>');
}

function cargaCtaBancariaRFC() {
	$("#campoRFC").val($("#cIDRFC").val());
	$("#rfc").val($("#cIDRFC").val());
	$("#CTAB").val("");

	queryFormPost({
		queryName : "esProveedorExtranjero",
		async : false,
		callback : function() {
			limpiaSelect("ctaBancaria", "", "Seleccione la cuenta bancaria");

			querySelectPost({
				queryName : 'cargaCtaBancariasRFC',
				targetObjectId : 'ctaBancaria',
				async : false,
				callback : function() {
					$("#ctaBancaria").change();
				}
			});

		}
	});

}

function cargaConcepto(){
	
	queryFormPost("readConceptoContrato", {async: false});
}


function limpiaSelect(idSelect, defaultVal, defaultText) {
	$("#" + idSelect).empty();
	$("#" + idSelect).append('<option value="' + defaultVal + '" selected="selected">' + defaultText + '</option>');
}


function createDTCalendario() {
	var cWhere = " nFolioPago=" + $("#nFolioPago").val() + " AND cTipoPago='" + $("#cTipoPago").val() + "'";

	oTableSaldos = $("#tblCalendario").dataTable(
		{
			"bPaginate" : false,
				"bFilter" : true,
				"bInfo" : false,
				"sScrollY" : "100%",
				"sScrollX" : "100%",
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				oLanguage : es_MX,
			fnDrawCallback : function() {},
			"fnServerData" : function(sSource, aoData, fnCallback) {
				$.ajax({
					"dataType" : 'json',
					"type" : "POST",
					"url" : sSource,
					"data" : aoData,
					"success" : fnCallback
				});
			},
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=vImportesCalendario&qw=" + cWhere,
			bProcessing : true,
			sPaginationType : "full_numbers",
			bJQueryUI : true,
			aoColumns : [ {
				sName : "EP"
			}, {
				sName : "mImporteBruto",
				sClass : "money"
			}, {
				sName : "tipoConcepto"
			}, {
				sName : "tipoMovimiento"
			} ]
		});

	$("#tblCalendario tbody").click(function(event) {

		$(oTableSaldos.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblCalendario tbody").dblclick(function(event) {

		$(oTableSaldos.fnSettings().aoData).each(function() {
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

		Swal.fire({
				  title: '¿Desea continuar?',
				  text: " Se eliminará la clave del calendario,",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					    queryFormPost({
							queryName : "eliminaEPCalendario",
							async : false,
							callback : function() {
								creaDTEPs();
								createDTCalendario();
							}
						});
				  } 
				})
	});

	leeTotalCalendarizado();
}



function creaDTEPs() {
	var mes = $("#fAplicacion").val().split("/")[1];
	
	oTableEPs = $("#tblEP").dataTable(
		{
				"bPaginate" : false,
				"bFilter" : true,
				"bInfo" : false,
				"sScrollY" : "100%",
				"sScrollX" : "100%",
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				oLanguage : es_MX,
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=V_SALDOCOMPROMISOS&qw=cidcontrato='" + $("#cIDContratoFed").val()+"'",
			aoColumns : [ {
				sName : "ep"
			}, {
				sName : "mCompromisoEnero",
				sClass : "money"
			}, {
				sName : "mCompromisoFebrero",
				sClass : "money"
			}, {
				sName : "mCompromisoMarzo",
				sClass : "money"
			}, {
				sName : "mCompromisoAbril",
				sClass : "money"
			}, {
				sName : "mCompromisoMayo",
				sClass : "money"
			}, {
				sName : "mCompromisoJunio",
				sClass : "money"
			}, {
				sName : "mCompromisoJulio",
				sClass : "money"
			}, {
				sName : "mCompromisoAgosto",
				sClass : "money"
			}, {
				sName : "mCompromisoSeptiembre",
				sClass : "money"
			}, {
				sName : "mCompromisoOctubre",
				sClass : "money"
			}, {
				sName : "mCompromisoNoviembre",
				sClass : "money"
			}, {
				sName : "mCompromisoDiciembre",
				sClass : "money"
			} ]
		});

	$("#tblEP tbody").click(function(event) {

		$(oTableEPs.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblEP tbody").dblclick(function(event) {

		$(oTableEPs.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableEPs.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		if ($("#tConcepto").val() == "" || $("#tConcepto").val() == "-1" || $("#tmovimiento").val() == "" || $("#tmovimiento").val() == null) {
			Swal.fire('Seleccione...',"Debe seleccionar el concepto y el movimiento antes de ingresar los montos", 'warning');
		} else {
			var ep = $("#tblEP").dataTable().fnGetData()[currIndex][0];		
			$("#epShow").val(ep);
			$("#epConsulta").val(ep);
			$("#mesConsulta").val(Number(mes));
			queryFormPost("saldoCompromisoAcumRead", {async : false});
			
			openModalEditMovimientos();
		}

	});
}

function validacionesCalendarioFed() {
	var correcto = false;
	queryFormPost({
		queryName : "montoPendienteFederalizadoRead",
		async : false,
		callback : function() {
			var diferencia = parseFloat(quitaFmt($("#diferencia").val()));
			if (diferencia == 0.00) {
				correcto = actualizaEstatus();
			} else {
				Swal.fire('Agregar claves' ,"Falta por cubrir: " + diferencia + " para completar el pago.", 'warning');
			}
		}
	});
	return correcto;
}


function revisaTipoFacturas(){
	queryFormPost("readEsPPD", {async : false});
	
		if( $("#esPPD").val() != "0" ){
			Swal.fire ('IMPORTANTE', 'La factura adjunta tiene el metodo de pago PPD por lo que es indispensable posteriormente solicitar al proveedor el CFDI de complemento de pago.', 'warning');
			modalCorreo.show();
			$("#capturaCorreo").show();
		} else {
			$("#capturaCorreo").hide();
		}
}

function validaCapturaCorreo(){
	let mensaje = "";
	if( $("#esPPD").val() != "0" &&  $("#correoActual").val() ==""){
		mensaje = "No se capturo el correo para seguimiento y la(s) factura(s) son PPD"
	}
	
	return mensaje;
}

function abrirCorreo() {
	queryFormPost("mostrarCorreoRead",{async: false});
	modalCorreo.show();
}

function mostrarCorreo(){
	queryFormPost("mostrarCorreoRead",{async: false});
	validarCorreo();
	modalCorreo.hide();
} 
function validarCorreo(){
        
    emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    
    if (emailRegex.test($("#correoActual").val())) {
		$("#correo").val($("#correoActual").val());
		//Actualizar datos y cerrar
		if($("#nombreCorreo").val()==""){
			Swal.fire("Capturar Nombre", "Debe capturar el nombre a la cual se dirigirá el correo", "warning")
		} else {
			$("#paternoCorreo").val($("#aPCorreo").val());
			$("#maternoCorreo").val($("#aMCorreo").val());
			$("#nCorreo").val($("#nombreCorreo").val());
			$("#cCargo").val($("#cargo").val());
			queryFormPost("updateCorreoActualizado",{async: false});
			modalCorreo.hide();
		}
	} else {
     	Swal.fire("Correo Invalido", "El correo no es valido, intente de nuevo", "error");
     	return;
    }
}


function cerrarDlg(){
	$("#dialog-validaFact").dialog("close");
}

function togleDivFacts(nIdDiv) {
	if (nIdDiv == 0) {
		$("#uploadFacturasDiv").hide();
		$("#facturasCapturadasDiv").show();
		creaDTFacturas();
		
	} else {
		if ($("#esExtranjero").val() == "1")
			$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
		else
			$('#uploadFacturasFrm').attr('src', "UploadFacturasPF.jsp?tipo_pago=PAGOFEDERALIZADO");
		$("#uploadFacturasDiv").show();
		$("#facturasCapturadasDiv").hide();
	}

	queryFormPost({
		queryName : "facturasCapturadasPFRead",
		async : false,
		callback : function() {
			var totalFacturas = parseInt(($("#nFacturasCapturadas").val() == "" ? "0" : $("#nFacturasCapturadas").val()), 10);
			var totalOficios = parseInt(($("#nOficiosCapturadas").val() == "" ? "0" : $("#nOficiosCapturadas").val()), 10);

			if ((totalFacturas == 0 && totalOficios == 0) || totalFacturas > 0) {
				$("#uploadFacturasTR").css("display", "block");
			} else {
				$("#uploadFacturasTR").css("display", "none");
			}
		}
	});
}