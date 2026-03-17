var ejecutadoInitMov = false;

function iniciaCapturaMovimientos() {	
	queryFormPost( "montoEjercerRead", {async: false});
		
	if (!ejecutadoInitMov) {
		creaDialogEditMovimientos();
		creaDTEPs();
		createDTCalendario();
		leeTipoConcepto();
		ejecutadoInitMov = true;
	}
}

function creaDialogEditMovimientos() {
	$("#dlg-CalendarioMontos").dialog({
		autoOpen : false,
		height : 300,
		width : 650,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				guardaMontos();
			},
			"Cancelar" : function() {
				$("#dlg-CalendarioMontos").dialog("close");
			}
		},
		open : function() {			
			$("#montoEjercer").focus();
		}
	});

}


function guardaMontos() {
	if ($("#montoEjercer").val() == "")
		$("#montoEjercer").val("0.00");

	var montoCapturado = parseFloat(quitaFmt($("#montoEjercer").val()));
	var montoDisponible = parseFloat(quitaFmt($("#disponibleEPTotal").val()));
	var montoTotalEjercer = parseFloat(quitaFmt($("#mMontoEjercer").val()));
	var ep = $("#epShow").val();

	if (montoCapturado <= 0) {
		Swal.fire("Error","Debe capturar el monto a ejercer y este debe ser positivo.","error");
		$("#montoEjercer").focus();
		return false;
	} else if (montoCapturado > montoDisponible) {
		Swal.fire("Revise","El monto a ejercer debe ser menor o igual al monto disponible","error");
		$("#montoEjercer").focus();
		return false;
	} else {

		$("#importeConsulta").val(montoCapturado);
		var correcto = false;
		var totalCalendarizado = 0.00;
		var msg = "";

		queryFormPost({
			queryName : "calendarioAcumRead",
			async : false,
			callback : function() {
				if ($("#totalCalendarizado").val() == "") {
					msg = "No se pudo consultar el total calendarizado";
				} else {
					totalCalendarizado = parseFloat(quitaFmt($("#totalCalendarizado").val()));
					if (totalCalendarizado > montoTotalEjercer) {
						msg = "Con el monto capturado supera el monto total del pago.";
					} else {
						correcto = true;
					}
				}

			}
		});

		if (!correcto) {
			Swal.fire("Revise",msg,"info");
			return false;
		}
	}
	guardaImporteCalendario(ep, montoCapturado);
}

function guardaImporteCalendario(ep, importeEP) {
	//if (confirm("Esta seguro de guardar el monto a ejercer?")) {

		$("#epCalendario").val(ep);
		$("#importeCalendario").val(importeEP);

		var msg = "";
		$.ajax({
			url : '../egresos/guardaCalendario',
			type : "POST",
			dataType : 'json',
			data : $("#formPagos").serialize(),
			async : false,
			success : function(json) {
				exito = json.success;
				if (exito) {
					Swal.fire('OK',"Registro guardado exitosamente", 'success');
				} else {
					var errores = json.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						msg = msg + errores[cnt] + "\n";
					}
					Swal.fire("Intente nuevamente","No se guardo el registro de calendario debido al error: \n" + msg, 'warning' );
				}

				creaDTEPs();
				createDTCalendario();
				$("#dlg-CalendarioMontos").dialog("close");
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				msg = "Error obteniendo retenciones iniciales";
			}
		});
	//}

}

function leeTotalCalendarizado() {
	queryFormPost({
		queryName : "montoCalendarizadoRead",
		async : false,
		callback : function() {
			var montoTotalEjercer = parseFloat(quitaFmt($("#mMontoEjercer").val()));
			var montoCalendario = parseFloat(quitaFmt($("#mMontoCalendarizado").val()));
			var montoRestante = (montoTotalEjercer - montoCalendario).toFixed(2);
			$("#mMontoPorEjercer").val(montoRestante);

			$("#mMontoEjercer").val();
			$("#mMontoCalendarizado").val();
			$("#mMontoPorEjercer").val();
		}
	});

}

function validacionesCalendario() {
	var correcto = false;
	queryFormPost({
		queryName : "montoPendienteRead",
		async : false,
		callback : function() {
			var diferencia = parseFloat(quitaFmt($("#diferencia").val()));
			if (diferencia == 0.00) {
				correcto = actualizaEstatus();
			} else {
				Swal.fire("Capture","Falta por cubrir: " + diferencia + " para completar el pago.","info");
			}
		}
	});
	return correcto;
}


function movimientosTerminado() {
	var exito = false;
	$.ajax({
		url : '../egresos/resumenCalendario',
		type : "GET",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				var classRow = "";
				for (var idx = 0; idx < resumen.length; idx++) {
					if (idx % 2 == 0)
						classRow = "even";
					else
						classRow = "odd";

					$('#resumenCalendario > tbody:last-child').append('<tr id="rwMovto' + idx + '" class="' + classRow + '"></tr>');
					$("#rwMovto" + idx).append('<td class="infoResumen">' + resumen[idx].EP + '</td>');
					$("#rwMovto" + idx).append('<td class="infoResumenMonto">' + resumen[idx].mImporteBruto + '</td>');
					$("#rwMovto" + idx).append('<td class="infoResumen">' + resumen[idx].tipoConcepto + '</td>');
					$("#rwMovto" + idx).append('<td class="infoResumen">' + resumen[idx].tipoMovimiento + '</td>');
				}

				$("#Movimientos").show();
				$("#TabMovimientos").show();
				$("#CalendarioResumenDiv").show();
				$("#capturaMovimientos").hide();
			} else {
				alert("No es posible continuar debido al siguiente error: " + json.errorList);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}

function leeTipoConcepto(){
	querySelectPost("CatalogoObraTConceptoRead", "tConcepto", {
		async : false
	});
}
