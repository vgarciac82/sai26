
var importes = new Array();
var arrData = new Array();
var arrDataD = new Array();
var montoAnterior = 0.00;
var retencionesFacturas = 0.00;
var initRetencionesEjecutado = false;

function iniciaCapturaRetenciones() {
	if (!initRetencionesEjecutado) {
		try {
			creaDialogEditRetencion();
			generaRetencionesIniciales();
			$("#btAgregaMov").button().click(function() {
				fnAgregarRet();
			});

			/* LEER EL MONTO DE RETENCIONES tPagoFacturaRetencion Y LA PARTIDA DEL PAAS tPagoDirectoPAAS*/
			queryFormPost({
				queryName : "readRetencion",
				async : false,
				callback : function() {
					if ($("#retencionesFacturas").val() == "")
						$("#retencionesFacturas").val("0.00");
				}
			});

			llenaCatalogoRet();
			/* 
			 * Validacion: Si en la factura no tiene retenciones pero por la partida se encuentran retenciones obligatorias no permite continuar el pago. 
			 */
			/*var montoRetenidoFact = parseFloat(quitaFmt($("#retencionesFacturas").val()));

			if (montoRetenidoFact == 0.00) {
				var retObligatorias = retencionesObligatoriasPago();

				if (retObligatorias > 0) {
					throw "La(s) facturas cargadas no cuentan con retenciones, sin embargo se encontraron retenciones obligatorias.El pago no puede continuar. Favor de notificar al proveedor.";
				}*/ /*else
					alert("No se encontraron retenciones en el factura pero la(s) partidas contienen retenciones opcionales. \n Es su responsabilidad eliminar dichas retenciones.")
				  */	
					//TODO Lanzar una confirmacion, si el usuario quiere continuar o se descarta el tramite.

			/*}*/
		} catch (e) {
			throw e;
		}

	}
}

/**
 * Realiza el calculo de la retencion seleccionada.
 */
function tipoRetencionChange(componente) {
	$("#importeRetencionCalculado").val("");

	queryFormPost({
		queryName : "calculaRetencionPDRead",
		async : false,
		callback : function() {
			if ($("#importeRetencionCalculado").val() == "")
				$("#importeRetencionCalculado").val("0.00");
		}
	});

}

/**
 * Agrega una retencion al pago. Al finalizar actualiza totales.
 */
function fnAgregarRet() {
	var montoRetencion = quitaFmt($("#importeRetencionCalculado").val());

	if (montoRetencion > 0.00) {
		var guardado = false;
		queryFormPost({
			queryName : "tPagoDirectoRetencionesCreate",
			async : false,
			callback : function() {
				Swal.fire("OK","Retencion agregada exitosamente.","success");
			}
		});

		llenaCatalogoRet();
		creaDTRetenciones();
	}

}

/**
 * Valida la diferencia entre las retenciones en factura y las retenciones calculadas
 */
function validaRemanente() {
	var remanenteRetFacturas = 0.00;
	var consultado = false;

	queryFormPost({
		queryName : "diferenciaRetencionPDRead",
		async : false,
		callback : function() {
			if( $("#diferencia").val() == "" )
				$("#diferencia").val("0.00");
			
			remanenteRetFacturas = parseFloat($("#diferencia").val());
			consultado = true;
		}
	});
	if (!consultado)
		throw "No se pudo consultar el remanente";
	else
		return remanenteRetFacturas.toFixed(2);

}

/**
 * Elimina la retencion seleccionada.
 */
function eliminaRetencion(idRetencion) {
	if (confirm("Esta seguro de eliminar la retencion?")) {
		$("#idRetencion").val(idRetencion);
		var msg = "";
		$.ajax({
			url : '../egresos/eliminaRetencion',
			type : "POST",
			dataType : 'json',
			data : $("#formPagos").serialize(),
			async : false,
			success : function(json) {
				exito = json.success;
				if (exito) {
					Swal.fire("OK","Retencion eliminada exitosamente.","success");
				} else {
					var errores = json.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						msg = msg + errores[cnt] + "\n";
					}
					alert("No se elimino la retencion debido al error: \n" + msg + "Intente nuevamente");
				}
				llenaCatalogoRet();
				creaDTRetenciones();
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				msg = "Error obteniendo retenciones iniciales";
			}
		});
	}

	$("#idRetencion").val("");
}

/**
 * Actualiza el monto de la retencion seleccionada.
 */
function actualizaRetencion() {
	var id = $("#idRetencionEdit").val();
	var ret = $("#descRetencionEdit").val();
	var retCalculada = parseFloat(quitaFmt($("#importeCalculadoEdit").val()));
	var retEditada = parseFloat(quitaFmt($("#nuevoImporteRetEdit").val()));
	var diferencia = (retCalculada - retEditada).toFixed(2);
	diferencia = Math.abs(diferencia);

	if (diferencia > toleracia) {
		alert("El nuevo monto supera la tolerancia de " + toleracia);
		$("#nuevoImporteRetEdit").focus();
	} else {
		$("#idRetencion").val(id);
		$("#retencionEditada").val(retEditada);

		$.ajax({
			url : '../egresos/actualizaRetencion',
			type : "POST",
			dataType : 'json',
			data : $("#formPagos").serialize(),
			async : false,
			success : function(json) {
				exito = json.success;
				if (exito) {
					creaDTRetenciones();
					Swal.fire("OK","Retencion actualizada correctamente.","success");
					$("#dlg-EditaRetencion").dialog("close");
				} else {
					var errores = json.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						msg = msg + errores[cnt] + "\n";
					}
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				msg = "Error obteniendo retenciones iniciales";
			}
		});

	}

}

/**
 * En base a las partidas seleccionadas en el PAAS el sistema inserta las retenciones que corresponden segun cada partida.
 */
function generaRetencionesIniciales() {
	var retCapturadas = numRetencionesCargadas();
	var estatusActual = obtenPagoEstatus();

	//if (!retencionesCargadas) {

		if (retCapturadas == 0 ){//&& estatusActual == 2) {
			var generado = false;
			var msg = "";
			$.ajax({
				url : '../egresos/generaRetenciones',
				type : "POST",
				dataType : 'json',
				data : $("#formPagos").serialize(),
				async : false,
				success : function(json) {
					exito = json.success;
					if (exito) {
						generado = true;
						creaDTRetenciones();
					} else {
						var errores = json.errorList;
						var cnt = 0;
						for (cnt = 0; cnt < errores.length; cnt++) {
							msg = msg + errores[cnt] + "\n";
						}
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("Advertencia: " + xhr.responseText + "\nEstatus: "
						+ textStatus + "\n" + errorThrown);
					msg = "Error obteniendo retenciones iniciales";
				}
			});

			if (!generado)
				throw msg;
		}
	//}

	retencionesCargadas = true;
}

/**
 * Devuelve el numero de retenciones ya existentes en el pago
 */
function numRetencionesCargadas() {
	$("#totRetencionesCapturas").val("");

	var retencionesCapt = 0;
	var consultado = false;

	try {

		queryFormPost({
			queryName : "retencionesCapturadasPDRead",
			async : false,
			callback : function() {
				consultado = true;
				if ($("#totRetencionesCapturas").val() != "") {
					retencionesCapt = parseInt($("#totRetencionesCapturas").val(), 10);
				}
			}
		});

		if (!consultado)
			throw "No se logro consultar el numero de retenciones capturadas.";

		return retencionesCapt;
	} catch (e) {
		throw e;
	}
}

/**
 * Devuelve el numero de retenciones obligatorias segun la partida seleccionada en el PAAS
 */
function retencionesObligatoriasPago() {
	var consultado = false;
	var obligatorias = 0;

	queryFormPost({
		queryName : "readretencionesObligatorias",
		async : false,
		callback : function() {
			consultado = true;
			obligatorias = parseInt($("#obligatoria").val() == "" ? 0 : $("#obligatoria").val(), 10);
		}
	});

	if (!consultado)
		throw "No se logro consultar el numero de retenciones obligatorias para el pago."

	return obligatorias;


}

/**
 * Llena el catalogo de retenciones disponibles. Excluye aquellas que ya existen. 
 */
function llenaCatalogoRet() {
	querySelectPost("readTipoRetencion", "cIdTipoRetencion", {
		async : false
	});
	$("#cIdTipoRetencion").change();
}
/**
 * Crea el dialogo para la edicion de las retenciones.
 */
function creaDialogEditRetencion() {
	$("#dlg-EditaRetencion").dialog({
		autoOpen : false,
		height : 400,
		width : 500,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				actualizaRetencion()
			},
			"Cancelar" : function() {
				$("#dlg-EditaRetencion").dialog("close");
			}
		},
		open : function() {
			$("#nuevoImporteRetEdit").val("");
		}
	});

}

function validacionesRetenciones() {
	var retencionesCorrectas = false;
	try {
		var remanente = parseFloat(validaRemanente());
		if (remanente == 0.00) {
			retencionesCorrectas = true;
		} else {
			Swal.fire("Intente editar retenciones","Existe una diferencia de: " + remanente + " entre las retenciones capturadas y las retenciones en facturas.", "info");
		}

		remanente = validaDiferenciaRetenciones();
		if (Math.abs(remanente) <= toleracia) {
			retencionesCorrectas = retencionesCorrectas && true;
		} else {
			retencionesCorrectas = retencionesCorrectas && false;
			Swal.fire("Revise las retenciones","Existe una diferencia de: " + remanente + " entre las retenciones calculadas y las retenciones capturadas que es mayor a la permitida","info");
		}

	} catch (e) {
		var msg = "";
		if (e instanceof TypeError)
			msg = e.message;
		else
			msg = e;

		Swal.fire("Ocurrio el error mientras se validaban las retenciones: " , msg, "error");
		retencionesCorrectas = false;
	}

	return retencionesCorrectas;
}

/**
 * Valida que la diferencia entre lo capturado (ajustado) y lo calculado por los porcentajes de las retenciones no tenga una variacion mayor a la tolerancia.
 */
function validaDiferenciaRetenciones() {
	var diferenciaCalculos = 0.00;
	var consultado = false;

	queryFormPost({
		queryName : "difRetencionCalculadaPDRead",
		async : false,
		callback : function() {
			if ($("#diferencia").val() == "")
				throw "No se pudo calcular la diferencia entre pagos";
			diferenciaCalculos = parseFloat($("#diferencia").val());
			consultado = true;
		}
	});
	if (!consultado)
		throw "No se pudo consultar el remanente";
	else
		return diferenciaCalculos.toFixed(2);

}


function retencionesTerminado() {
	var exito;
	$.ajax({
		url : '../egresos/resumenRetenciones',
		type : "GET",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				var classRow = "";
				if( resumen.length > 0 ){
					for (var idx = 0; idx < resumen.length; idx++) {
						if (idx % 2 == 0)
							classRow = "even";
						else
							classRow = "odd";
	
						$('#resumenRetencion > tbody:last-child').append('<tr id="rwRete' + idx + '" class="' + classRow + '"></tr>');
						$("#rwRete" + idx).append('<td class="infoResumen">' + resumen[idx].cTipoRetencion + '</td>');
						$("#rwRete" + idx).append('<td class="infoResumenMonto">' + resumen[idx].base + '</td>');
						$("#rwRete" + idx).append('<td class="infoResumenMonto">' + resumen[idx].porcentajeRetencion + '</td>');
						$("#rwRete" + idx).append('<td class="infoResumenMonto">' + resumen[idx].retencion + '</td>');
					}
				}else{
					$('#resumenRetencion > tbody:last-child').append('<tr id="rwRete" class="even"></tr>');
					$("#rwRete").append('<td class="infoResumen" colspan="4" align="center"><b><pre> P A G O     S I N     R E T E N C I O N  </pre></b></td>');
				}
				$("#Retenciones").show();
				$("#TabRetenciones").show();
				$("#resumenRetencionDiv").show();
				$("#retencionesDIV").hide();
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