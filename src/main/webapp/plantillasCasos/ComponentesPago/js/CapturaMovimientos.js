var ejecutadoInitMov = false;

function iniciaCapturaMovimientos() {
	var tipopago = $("#tipo_pago").val();
	
	if(tipopago == 'PAGOFEDERALIZADO' || tipopago == 'RELACIONGASTOS') {
		queryFormPost( "montoEjercerRead", {async: false});
	} else {
		modalEP = new bootstrap.Modal(document.getElementById('dlg-CalendarioMontos'), 'data-bs-backdrop');
		queryFormPost({
			queryName : "montoEjercerPDRead",
			async : false,
			callback : function() {}
		});
	}
	if (!ejecutadoInitMov) {
		
		if (tipopago != 'RELACIONGASTOS') {
			leeTipoConcepto();	
			$("#divCalendario").show();
			$("#divCalendarioRG").hide();
			$("#divRetenciones").hide();
		} else {
			$("#divConcepto").hide();
			$("#divTitle").hide();
			$("#divCalendario").hide();
			$("#divCalendarioRG").show();
			//$("#divRetenciones").show();
			$("#mMontoRetFact").val($("#mImporteRetencion").val()); 
			$("#mMontoEjercer").val(Number(quitaFmt($("#mImporteNeto").val())) + Number( $("#mImporteRetencion").val()));
            $("#mMontoPorEjercer").val("0");
			//$("#mMontoRetPendientes").val(Number($("#mMontoRetFact").val()) - Number( $("#mMontoRetenciones").val()));
		}
		
		creaDTEPs();
		createDTCalendario();
		ejecutadoInitMov = true;
	}
}

function openModalEditMovimientos() {
	modalEP.show();	
	$("#montoEjercer").val("0.00");
	$("#montoEjercer").focus();

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
		var totalRet = 0.00;
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
		modalEP.hide();	
	
}


function guardaMontosRG() {
	if ($("#montoEjercer").val() == "")
		$("#montoEjercer").val("0.00");

	var montoCapturado = parseFloat(quitaFmt($("#montoEjercerRG").val()));
	var montoDisponible = parseFloat(quitaFmt($("#disponibleEPTotalRG").val()));
	var montoTotalEjercer = parseFloat(quitaFmt($("#mMontoEjercer").val()));
	
	var ep = $("#epShowRG").val();

	if (montoCapturado <= 0) {
		Swal.fire("Error","Debe capturar el monto a ejercer y este debe ser positivo.","error");
		$("#montoEjercer").focus();
		return false;
	} else if (montoCapturado > montoDisponible) {
		Swal.fire("Revise","El monto a ejercer debe ser menor o igual al monto disponible","error");
		$("#montoEjercerRG").focus();
		return false;
	} else {

		$("#importeConsulta").val(montoCapturado);
		var correcto = false;
		var totalCalendarizado = 0.00;
		let totalRet = 0.00;
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
		
		queryFormPost({
			queryName : "calendarioRetAcumRead",
			async : false,
			callback : function() {
				if ($("#totalRetencion").val() == "") {
					msg = "No se pudo consultar el total de las retenciones";
				} else {
					totalRet = parseFloat(quitaFmt($("#totalRetencion").val()));
					if (totalRet > montoRetFact) {
						msg = "Con el monto capturado supera el monto de las retenciones.";
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
	guardaImporteCalendarioRG(ep, montoCapturado);
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
				modalEP.hide();
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				msg = "Error obteniendo retenciones iniciales";
			}
		});
	//}

}

function guardaImporteCalendarioRG(ep, importeEP) {
	//if (confirm("Esta seguro de guardar el monto a ejercer?")) {
		$("#epCalendario").val(ep);
		$("#importeCalendario").val(importeEP);

		var msg = "";
		$.ajax({
			url : '../viaticos/guardaCalendario',
			type : "POST",
			dataType : 'json',
			data : {				
				ep :  			$("#epShowRG").val(),
				mes :  			$("#mesConsulta").val(),
				nFolioPago :  	$("#nFolioPago").val(),
				cTipoPago : 	$("#cTipoPago").val(),
				cIdContrato : 	$("#cIdContrato").val(),
				montoEjercer : 	quitaFmt($("#montoEjercerRG").val()),
				montoRetencion : 	quitaFmt($("#montoRetencionRG").val()),
				disponibleEP :  quitaFmt($("#disponibleEPTotalRG").val()),
				cEvento : 		$("#cEvento").val(),
				tConcepto : 	$("#tConcepto").val(),
				tipoFuente : 	$("#tipo_fuente").val(),
			},
			async : false,
			success : function(json) {
				exito = json.success;
				if (exito) {
					let mensaje = json.messageList[0];
					Swal.fire('OK', mensaje, 'success');
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
				$('#dlg-CalendarioMontosRG').modal('hide');
				
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				msg = "Error obteniendo retenciones iniciales";
			}
		});
	
}
function leeTotalCalendarizado() {
	queryFormPost({
		queryName : "montoCalendarizadoRead",
		async : false,
		callback : function() {
			var montoTotalEjercer =  parseFloat(quitaFmt($("#mMontoEjercer").val()));
			var montoCalendario = parseFloat(quitaFmt($("#mMontoCalendarizado").val()));
			var montoRestante = (montoTotalEjercer - montoCalendario).toFixed(2);
			$("#mMontoPorEjercer").val(montoRestante);

			$("#mMontoEjercer").formatCurrency();
			$("#mMontoCalendarizado").formatCurrency();
			$("#mMontoPorEjercer").formatCurrency();
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
	querySelectPost( {queryName: "CatalogoObraTConceptoRead", targetObjectId: "tConcepto", async: false, 
						 callback: function () {
						 if ($.trim($("#rfc").val()) == "BMN930209927" && $("#idDestinoGasto").val() == "CSPF" ){
							var opts = document.getElementById("tConcepto").options;
							for( cntFF = 0;cntFF < opts.length; cntFF++ ){
								if( "FFM" != opts[cntFF].value && "" != $.trim(opts[cntFF].value) )
									document.getElementById("tConcepto").remove( cntFF );
							}
						}
					}
				});
}

