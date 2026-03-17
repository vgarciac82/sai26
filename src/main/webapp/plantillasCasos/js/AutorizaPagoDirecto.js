
/**
 * 
 */


$("#form").append('<input type="hidden" name="ANIO" valu="' + $("laVariable").val() + '">');
var excepcionUser = "Briseida Noemi Ramirez Garcia";
var excepcionEmail = "briseida.ramirez@conafor.gob.mx";


function onLoadPlantilla(idOperacion) {

	$("#dlgQuestionnaire").css("height", "0px");
	$("#operacion").val("");
	setValoresIniciales();
	iniciaFirmantes();
	
	$.when(getPartidasExcedenUMA()).done(function() {
		console.log("getPartidasExcedenUMA done");
		if( $("#cuestionarioRespondido").val() === "true") {
			if ($("#firmaElectronica").length > 0) {
				$("#firmaElectronica").attr("checked", true);
				$(".firmaElectronica").each(function() {
					$(this).hide();
				})
			}
			
			if( validaREPSE() ){
				$("#repseDiv").show();
			}
		}
	
		creaDialogMotivo();
	
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
			Swal.fire("El pago ya ha sido rechazado.","Se enviara a consulta automáticamente.","success");
			$.blockUI();
			avanzaCaso();
		} else if (estatusPago == 6) {
			$("#operacion").val("ACEPTAR")
			Swal.fire("El pago ya ha sido autorizado.","Se enviara a consulta automáticamente.", "success");
			$.blockUI();
			avanzaCaso();
		} else if (estatusPago == 5) {
			$("#operacionesDiv").show();
		}
	});
	
	

}


function deshabilitaTodo() {
	$.blockUI();
	if (parent.document.getElementById("pb_save")) {
		parent.document.getElementById("pb_save").disabled = true;
		parent.document.getElementById("pb_save").style.display = "none";
	}
}

function creaDialogMotivo() {
	$("#dialogMotivo").dialog({
		autoOpen: false,
		height: 260,
		width: 320,
		modal: true,
		buttons: {
			"Aceptar": function() {
				aceptarDlg();

			},
			"Cancelar": function() {
				$("#motivoRechazo").val("");
				$("#operacion").val("");
				$(this).dialog("close");
			}
		}
	});
}
function aceptarDlg() {
	if ($.trim($("#motivoRechazo").val()) == "") {
		Swal.fire("Capture","Debe ingresar el motivo de rechazo.","info");
	} else {
		Swal.fire("Presione Guardar","Para continuar guarde el trámite","info");
		$("#cMotivoRechazo").val($("#motivoRechazo").val());
		$("#motivoRechazo").val("");
		$("#dialogMotivo").dialog("close");
	}
}

function aceptaPago() {
	$("#operacion").val("ACEPTAR");
	$("#firmantesDiv").show();
	$("#cMotivoRechazo").val("");
	$("#firmaElectronica").prop("checked", true);
}

function rechazaPago() {
	$("#operacion").val("RECHAZO");

	$("#firmantesDiv").hide();
	$("#dialogMotivo").dialog("open");

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
			queryName: "estatusPagoDirectoRead",
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
		Swal.fire("No ha seleccionado alguna operacion.","De click en el boton Aceptar o Rechazar segun corresponda.","info");
		return false;
	} else if ($("#operacion").val() == "RECHAZO") {
		return validaRechazo();
	} else if ($("#operacion").val() == "ACEPTAR") {
		return validaAceptar();
	}

}

function validaAceptar() {
	if( !validaREPSE() ){
		Swal.fire("El pago indica que aplica el art. 15D al proveedor","Sin embargo no se encontro su numero REPSE. Por favor actualice el proveedor con la informacion faltante.","info");
		return false;
	}else{
		if( $("#aplica15D").val() === "true" ){
			Swal.fire("Esta seguro de haber validado el numero de registro en el portal de la STPS?","Se le recuerda que es su responabilidad tener el expediente actualizado y confirmar que el registro del proveedor no haya sido removido/cancelado","info");
		}
	}
	if ( validaFirmantes()) {
		return true;
	} else
		return false;
}

function validaRechazo() {
	if ($.trim($("#cMotivoRechazo").val()) == "") {
		Swal.fire("Debe indicar el motivo de rechazo.","De nuevamente clic en el boton rechazar para ingresar el motivo.","info");
		return false;
	} else
		return true;
}


function ResponsableSiguiente(idOper) {
	return "CONSULTA_PAGODIRECTO";
}

function OperacionSiguiente(idOper) {
	return "consulta_factura";
}


function onPostDisplay(id_oper) {

	if ($("#operacion").val() == "RECHAZO") {
		Swal.fire({
				  title: '¿Esta seguro de rechazar la solicitud?',
				  text: "Esta operacion no se puede deshacer",
				  icon: 'question',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  var exitoso = rechazaOperacion();
						if (exitoso) {
							$("#operacionesDiv").hide();
							avanzaCaso();
						}
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  return false;
				  }
				})
	} else if ($("#operacion").val() == "ACEPTAR") {
			Swal.fire({
				  title: '¿Esta seguro de autorizar la solicitud?',
				  text: "Esta operacion no se puede deshacer",
				  icon: 'question',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  /*Validar si la retencion insertada en el detalle corresponde al regimen fiscal del proveedor */			
						queryFormPost("validaRegimenFiscal", {async: false});
						var msgRF = $("#msgRF").val();
						if ( msgRF != "" ){
							alert(msgRF);				
							return; 			
						}
						
						/*Validar la suma de retenciones del detalle vs el encabezado*/			
						queryFormPost("validaRetencionENCvsDET", {async: false});
						var msgRF = $("#msgRF").val();
						if ( msgRF != "" ){
							alert(msgRF);
							$("#dialog-Procesando").dialog("close");
							return; 			
						}
						
						/*Validar si el regimen es 626 y el tipo persona es Moral no debe tener retencion RESICO*/			
						queryFormPost("validaRESICOPersonaMoral", {async: false});
						var msgRF = $("#msgRF").val();
						if ( msgRF != "" ){
							alert(msgRF);
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
						
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
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
				Swal.fire("OK","Pago rechazado exitosamente","success");
			} else {
				var errores = json.errorList;
				var cntIndex = 0;
				for (cntIndex = 0; cntIndex < errores.length; cntIndex++) {
					msg = msg + errores[cntIndex] + "\n";
				}
				alert("No se rechazo el pago debido al error: \n" + msg + "Intente nuevamente");
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
				Swal.fire("OK","Pago aplicado contablemente con exito.", "success");
			} else {
				var errores = json.errorList;
				var cntIndex = 0;
				for (cntIndex = 0; cntIndex < errores.length; cntIndex++) {
					msg = msg + errores[cntIndex] + "\n";
				}
				alert("No se autorizo el pago debido al error: \n" + msg + "Intente nuevamente");
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}

function validaREPSE() {
	if( $("#aplica15D").val() === "true" ) {
		var repseCapturado = false;
		$.ajax({
			url: '../egresos/validaREPSE',
			type: "GET",
			dataType: 'json',
			async: false,
			success: function(json) {
				
				if( json.success === true )
					repseCapturado = "true" === json.messageList[0];
				else{
					alert("Ocurrio el siguiente error al intentar consultar el folio REPSE: \n" + json.errorList[0]);
					repseCapturado = false;
				}
			},
			error: function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				exito = false;
			}
		});
	
		return repseCapturado;
	}else{
		return true;
	}
}



function getPartidasExcedenUMA(idRFC) {
	
	$.ajax({
		url : '../egresos/validaMontoTotalizado',
		type : "GET",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : true,
		success : function(json) {
			
			if (json.success) {
				var partidasExcenden = json.partidasExcenden;
				var msg = "";
				
				for (var k = 0; k < partidasExcenden.length; k++) {
					msg +=	"La partida " +  partidasExcenden[k].partida + 
							" excede con el monto " + partidasExcenden[k].montoNeto + 
							" el total de pagos directos para el proveedor " + partidasExcenden[k].rfc + " - " +  partidasExcenden[k].razonSocial +
							 " de 300 umas.";
				}
				if(msg != ""){
					parent.document.getElementById("pb_save").disabled=true;
					descartaPagoExcedido(msg);
				}else{
					parent.document.getElementById("pb_save").disabled=false;
				}
			} else {
				alert("No es posible continuar debido al siguiente error: " + json.errorList);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});
 
}

function descartaPagoExcedido(msg){
	Swal.fire({
		  title: 'No puede continuar el pago!',
		  text: "Se encontraron los siguientes problemas: \n\n" + msg + "\n\nSi necesita continuar con el pago envíe un correo a "  + excepcionUser + " al correo " + excepcionEmail + " con la justificacion del pago.",
		  icon: 'error',
		  showCancelButton: false,
		  confirmButtonText: 'Aceptar'
		}).then((result) => {
			parent.document.getElementById("pb_cancel").disabled=false;
			$.blockUI();
			parent.document.forms["frmCancel"].submit();
			
		})
}