
var terminadoEjectuado = false;
function onLoadResumen() {
	
	actualizaGabinete();
	informacionPagoRead();
}

function informacionPagoRead() {
	var msgError = "";
	if (!terminadoEjectuado) {
		try {
			var exito = false;
			$.ajax({
				url: '../egresos/resumenFinalPago',
				type: "GET",
				dataType: 'json',
				data: $("#formPagos").serialize(),
				async: false,
				success: function(json) {
					exito = json.success;
					if (exito) {
						var resumen = json.resumen;
						for (var k in resumen) {
							$("#" + k).text(resumen[k]);
						}
						resumenRetencionesPago();
						resumenMovimientos();
						
					} else {
						var arr = json.errorList;
						for (var cntError = 0; cntError < arr.length; cntError++) {
							msgError += arr[cntError] + "\n";
						}
					}
				},
				error: function(xhr, textStatus, errorThrown) {
					msgError = "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown;
				}
			});

			if (msgError != "")
				throw msgError;
			terminadoEjectuado = true;
			return exito;
		} catch (e) {
			var msg = "";
			if (e instanceof TypeError)
				msg = e.message;
			else
				msg = e;

			alert("No se puede continuar debido al error: \n" + msg + "\nIntente nuevamente o notifique al administrador");
			deshabilitaTodo();
			throw e;

		}
	}
}

function resumenRetencionesPago() {
	var exito;
	$.ajax({
		url: '../egresos/resumenRetenciones',
		type: "GET",
		dataType: 'json',
		data: $("#formPagos").serialize(),
		async: false,
		success: function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				var classRow = "";
				if (resumen.length > 0) {
					for (var idx = 0; idx < resumen.length; idx++) {
						$('#tblResumenPagoR > tbody:last-child').append('<tr id="rwReteResumen' + idx + '" class="' + classRow + '"></tr>');
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="left">' + resumen[idx].cTipoRetencion + '</td>');
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="center">' + resumen[idx].mImporteBruto + '</td>');
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="right">' + resumen[idx].nPorcRetencion + '</td>');
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="right">' + resumen[idx].importeRetencion + '</td>');
					}
				} else {
					$('#tblResumenPagoR > tbody:last-child').append('<tr id="rwReteResumen" class=""></tr>');
					$("#rwReteResumen").append('<td class="BB BR" colspan="4" align="center"><b>PAGO SIN RETENCION</b></td>');
				}

			} else {
				alert("No es posible continuar debido al siguiente error: " + json.errorList);
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}


function resumenMovimientos() {
	var exito = false;
	$.ajax({
		url: '../egresos/resumenCalendario',
		type: "GET",
		dataType: 'json',
		data: $("#formPagos").serialize(),
		async: false,
		success: function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				var classRow = "";
				for (var idx = 0; idx < resumen.length; idx++) {

					$('#tblResumenMovtosR > tbody:last-child').append('<tr id="rwMovtoResumen' + idx + '" class="' + classRow + '"></tr>');
					$("#rwMovtoResumen" + idx).append('<td class="BB BR" algin="center">' + resumen[idx].EP + '</td>');
					$("#rwMovtoResumen" + idx).append('<td class="BB BR" algin="center">' + resumen[idx].tipoConcepto + '</td>');
					$("#rwMovtoResumen" + idx).append('<td class="BB BR" algin="right">' + resumen[idx].mImporteBruto + '</td>');
				}

			} else {
				alert("No es posible continuar debido al siguiente error: " + json.errorList);
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}


function togleJustificacion() {
	var capturado = validaDoctoCapturado();

	if (capturado) {
		$("#adjuntarDocRete").hide();
		$("#dialog-Justificacion").dialog("close");
	}

}


// TODO Se pueden validar montos, validar fechas, validar facturas etc. Para esta version solo valida que no tenga doctos pendientes
function validacionesCapturaCompleta() {
	var avanzar = false;

	var cxp = $.trim($("#contrarecibo").val());
	if (cxp == "") {
			    cxp = generaPago();
				$("#contrarecibo").val(cxp);
				avanzar = true;		  
	}
	
	return avanzar;
}

function generaPago() {
	var exito = false;
	var msgError = "";
	var cxp = "";
	try {

		$.ajax({
			url: '../egresos/guardarPago',
			type: "POST",
			dataType: 'json',
			data: $("form").serialize(),
			async: false,
			success: function(json) {
				exito = json.success;
				if (exito) {
					alert("Solicitud guardada exitosamente.");
					cxp = json.messageList[0];
				} else {
					var arr = json.errorList;
					for (var cntError = 0; cntError < arr.length; cntError++) {
						msgError += arr[cntError] + "\n";
					}
				}
			},
			error: function(xhr, textStatus, errorThrown) {
				throw "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown;
			}
		});
		if (!exito)
			throw msgError;
		else 
			return cxp;
		
	} catch (e) {
		var msg = "";
		if (e instanceof TypeError)
			msg = e.message;
		else
			msg = e;

		alert("No se puede continuar debido al error: \n" + msg + "\nIntente nuevamente o notifique al administrador");
		throw e;
	}
}

function capturaTerminada() {
	var cxp = $.trim($("#contrarecibo").val());
	if (cxp != "") {
		alert("Captura terminada. Cuenta por pagar generada [" + cxp + "]\n Se enviara el tramite a autorizacion.");
		parent.document.getElementById("pb_save").disabled = true;
		parent.document.getElementById("pb_save").style.display = "none";
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").style.display = "block";
		parent.document.getElementById("pb_send").click();

		return true;
	} else
		return false;
}

function muestraCaptura() {
	
		parent.document.getElementById("pb_save").disabled = false;
		parent.document.getElementById("pb_save").style.display = "block";
		return true;
	
}

function rechazaProceso(){
	$.blockUI({ message: '<h1><img src="../Generador/imagenes/wait24trans.gif" /> Guardando ...</h1>' });
	parent.document.getElementById("frmCancel").submit();
}