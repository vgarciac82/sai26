let modalRechaza;
/**
 * 
 */


$("#form").append('<input type="hidden" name="ANIO" valu="' + $("laVariable").val() + '">');



function onLoadPlantilla(idOperacion) {

	$("#operacion").val("");
	setValoresIniciales();
	iniciaFirmantes();
	
	modalRechaza = new bootstrap.Modal(document.getElementById('dialogMotivo'), 'data-bs-backdrop');
	$("#rechazarBtn").button().click(function() {
		rechazaPago();
	});

	$("#aceptarBtn").button().click(function() {
		aceptaPago();
	});

	informacionPagoRead();

	var estatusPago = parseInt(obtenPagoEstatus(), 10);
	if (estatusPago == -1) {
		$("#operacion").val("RECHAZO")
		alert("El pago ya ha sido rechazado. Se enviara a consulta automaticamente.");
		$.blockUI();
		avanzaCaso();
	} else if (estatusPago == 6) {
		$("#operacion").val("ACEPTAR")
		alert("El pago ya ha sido autorizado. Se enviara a consulta automaticamente.");
		$.blockUI();
		avanzaCaso();
	} else if (estatusPago == 5) {
		$("#operacionesDiv").show();
	}

}


function deshabilitaTodo() {
	$.blockUI();
	if (parent.document.getElementById("pb_save")) {
		parent.document.getElementById("pb_save").disabled = true;
		parent.document.getElementById("pb_save").style.display = "none";
	}
}


function aceptarDlg() {
	if ($.trim($("#motivoRechazo").val()) == "") {
		Swal.fire('Capturar',"Debe ingresar el motivo de rechazo.", 'warning');
	} else {
		Swal.fire('Presione Guardar',"Para continuar guarde el tramite", 'success');
		$("#cMotivoRechazo").val($("#motivoRechazo").val());
		$("#motivoRechazo").val("");
		modalRechaza.hide();
	}
}

function aceptaPago() {
	$("#operacion").val("ACEPTAR");
	$("#firmantesDiv").show();
	$("#cMotivoRechazo").val("");
}

function rechazaPago() {
	$("#operacion").val("RECHAZO");

	$("#firmantesDiv").hide();
	modalRechaza.show();
}

function setValoresIniciales() {
	for (objVar in infoObj) {
		$("#" + objVar).val(infoObj[objVar]);
	}

	$("#idUsuarioAprobacion").val($("#login").val());
	$("#idUsuarioRevision").val($("#login").val());
}


/**
 * Lee el estatus actual del pago. Los estaus se corresponden al catalogo: 
 */
function obtenPagoEstatus() {
	var estatusEncontrado = false;
	$("#nIDEstatus").val("-1");

	queryFormPost(
		{
			queryName: "estatusPagoFedRead",
			async: false,
			callback: function() {
				estatusEncontrado = true;
			}
		});

	if (estatusEncontrado)
		return parseInt($("#nIDEstatus").val(), 10);
	else
		throw "No se pudo consultar el estatus del pago";
}

function onSubmit(idOper) {

	if ($("#operacion").val() == "") {
		Swal.fire('No ha seleccionado alguna operacion.'," De click en el boton Aceptar o Rechazar segun corresponda.",'warning');
		return false;
	} else if ($("#operacion").val() == "RECHAZO") {
		return validaRechazo();
	} else if ($("#operacion").val() == "ACEPTAR") {
		return validaAceptar();
	}

}

function validaAceptar() {
	if ( validaFirmantes()) {
		return true;
	} else
		return false;
}

function validaRechazo() {
	if ($.trim($("#cMotivoRechazo").val()) == "") {
		Swal.fire("Debe indicar el motivo de rechazo."," De nuevamente clic en el boton rechazar para ingresar el motivo.","warning");
		return false;
	} else
		return true;
}


function ResponsableSiguiente(idOper) {
	return "CONSULTA_PAGOFEDERALIZADO";
}

function OperacionSiguiente(idOper) {
	return "consulta_factura";
}


function onPostDisplay(id_oper) {

	if ($("#operacion").val() == "RECHAZO") {
		Swal.fire({
			  title: 'Esta seguro de rechazar la solicitud? Esta operacion no se puede deshacer',
			  showCancelButton: true,
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar',
			}).then((result) => {
			  if (result.isConfirmed) {
				    var exitoso = rechazaOperacion();
					if (exitoso) {
						$("#operacionesDiv").hide();
						avanzaCaso();
					}
			  } else if (result.isDenied) {
			    	return false;
			  }
			})
	} else if ($("#operacion").val() == "ACEPTAR") {
		Swal.fire({
			  title: 'Esta seguro de autorizar la solicitud?',
			  showCancelButton: true,
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar',
			}).then((result) => {
			  if (result.isConfirmed) {
				    /*Validar si la retencion insertada en el detalle corresponde al regimen fiscal del proveedor */	
					queryFormPost("validaRegimenFiscal", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						Swal.fire('Validacion',msgRF, 'warning');				
						return; 			
					}
					
					/*Validar la suma de retenciones del detalle vs el encabezado*/			
					queryFormPost("validaRetencionENCvsDET", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						Swal.fire('Validacion',msgRF, 'warning');
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*Validar si el regimen es 626 y el tipo persona es Moral no debe tener retencion RESICO*/			
					queryFormPost("validaRESICOPersonaMoral", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						Swal.fire('Validacion',msgRF, 'warning');
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*ARLA SI LAS FACTURAS ESTAN BORRADAS SE HACE EL INSERT A tPagoFactura Y SE BORRAN DE tPagoFactura_Borrada*/
					queryFormPost("validaExisteFacturas", {async: false});
					
					var guardado = guardaFirmantes();
					if (guardado) {
						if (aplicaPago()) {
							avanzaCaso();
						}
					}
					return guardado;
			  } else if (result.isDenied) {
			    	return false;
			  }
			})
	}

}

function rechazaOperacion() {
	var exito = false;
	var msg = "";
	$.ajax({
		url: '../egresos/RechazaPago',
		type: "POST",
		dataType: 'json',
		data: $("#autPago").serialize(),
		async: false,
		success: function(json) {
			exito = json.success;
			if (exito) {
				Swal.fire('OK',"Pago rechazado exitosamente", 'success');
			} else {
				var errores = json.errorList;
				var cntIndex = 0;
				for (cntIndex = 0; cntIndex < errores.length; cntIndex++) {
					msg = msg + errores[cntIndex] + "\n";
				}
				Swal.fire("Intente nuevamente","No se rechazo el pago debido al error: \n" + msg , 'error');
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}

function avanzaCaso() {
	$.blockUI();
	parent.document.getElementById("pb_save").disabled = true;
	parent.document.getElementById("pb_save").style.display = "none";
	parent.document.getElementById("pb_send").disabled = false;
	parent.document.getElementById("pb_send").click();
}

function onPostSubmit(idOper) {
	var respSig = parent.document.getElementById("responsable").value;
	if (respSig == "") {
		parent.execResponsable();
		parent.execOperacion();
	}
	/*Descarga contrarecibos*/
	if ($("#operacion").val() == "ACEPTAR") {
	}
	return true;
}

function aplicaPago() {
	var exito = false;
	var msg = "";
	$.ajax({
		url: '../egresos/AutorizaPago',
		type: "POST",
		dataType: 'json',
		data: $("#autPago").serialize(),
		async: false,
		success: function(json) {
			exito = json.success;
			if (exito) {
				Swal.fire('OK',"Pago aplicado contablemente con exito.", 'success');
			} else {
				var errores = json.errorList;
				var cntIndex = 0;
				for (cntIndex = 0; cntIndex < errores.length; cntIndex++) {
					msg = msg + errores[cntIndex] + "\n";
				}
				Swal.fire('No se autorizo el pago', "Debido al error: \n" + msg + "Intente nuevamente", 'error');
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}


