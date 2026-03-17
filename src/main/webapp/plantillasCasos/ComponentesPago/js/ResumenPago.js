
var terminadoEjectuado = false;
function onLoadResumen() {
	createDialogQuestionnaire();
	actualizaGabinete();
	informacionPagoRead();
	requiereDocumentacionRetencion();
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
						existsQuestionnaire();
	
						if(  "S" === $("#questionnaireAnswered").val() ){
							readQuestionnaireResponses();
						}else{
							requiereCuestionario(muestraCuestionario);
						}
						
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
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="center">' + resumen[idx].base + '</td>');
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="right">' + resumen[idx].porcentajeRetencion + '</td>');
						$("#rwReteResumen" + idx).append('<td class="BB BR" align="right">' + resumen[idx].retencion + '</td>');
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
	
	requiereCuestionario();
	existsQuestionnaire();
	
	var questionnaireRequired = ( "S" === $("#applyQuestionnaire").val() );
	var questionnaireAnswered = ( "S" === $("#questionnaireAnswered").val() );
	
	if( questionnaireRequired && !questionnaireAnswered ){
		muestraCuestionario(true);
		return;
	}
	
	var avanzar = !requiereDocumentacionRetencion();
	
	if (avanzar) {
		var cxp = $.trim($("#contrarecibo").val());
		if (cxp == "") {
			Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se generara el contrarecibo.",
				  icon: 'success',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  	cxp = generaPagoApartado();
						$("#contrarecibo").val(cxp);
						avanzar = true;
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  avanzar = false;
				  }
				})
			/*if (confirm("Se generara el contrarecibo. Desea continuar?")) {
				cxp = generaPagoApartado();
				$("#contrarecibo").val(cxp);
				avanzar = true;
			} else {
				avanzar = false;
			}*/
		}
	}
	return avanzar;
}

function generaPagoApartado() {
	var exito = false;
	var msgError = "";
	var cxp = "";
	try {

		$.ajax({
			url: '../egresos/apartadoPago',
			type: "POST",
			dataType: 'json',
			data: $("form").serialize(),
			async: false,
			success: function(json) {
				exito = json.success;
				if (exito) {
					Swal.fire("OK","Recurso Apartado exitosamente.","success");
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

		Swal.fire("No se puede continuar debido al error: ",   msg + "\nIntente nuevamente o notifique al administrador", "error");
		throw e;
	}
}

function capturaTerminada() {
	var cxp = $.trim($("#contrarecibo").val());
	if (cxp != "") {
		alert("Cuenta por pagar generada [" + cxp + "]\n Se enviara el tramite a autorizacion.");
		parent.document.getElementById("pb_save").disabled = true;
		parent.document.getElementById("pb_save").style.display = "none";
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").style.display = "block";
		parent.document.getElementById("pb_send").click();

		return true;
	} else
		return false;
}

function requiereCuestionario(fnCallBack) {
	queryFormPost({
		queryName: "applyQuestionnairePayments",
		async: false,
		callback: function() {
			if( fnCallBack )
				fnCallBack("S" === $("#applyQuestionnaire").val());
		}
	});
}

function muestraCuestionario(mostrar) {
	if (mostrar) {
		Swal.fire("Cuestionario Obligatorio","La partida seleccionada indica que debe contestar el cuestionario de determinacion del articulo 15-D","info");
		$("#dlgQuestionnaire").dialog("open");
	}else{
		$("#dlgQuestionnaire").dialog("close");
	}
}


function createDialogQuestionnaire() {

	$("#dlgQuestionnaire").dialog({
		autoOpen: false,
		height: 472,
		width: 850,
		modal: true,
		open: function() {
			$('#questionnaire').attr('src', "../Generador/SAICYS/Cuestionario.jsp");
		}
	});

}

function saveQuestionnaire(answers, aplica15D) {
	$("#aplicaArt15D").val(aplica15D);
	$.blockUI({ message: '<h1><img src="../Generador/imagenes/wait24trans.gif" /> Guardando ...</h1>' });
	$.ajax({
		url: '../egresos/saveQuestionnaire',
		type: "POST",
		dataType: 'json',
		data: {
			"answers":answers.join(";"),
			"aplicaArt15D":$("#aplicaArt15D").val()
		},
		async: false,
		success: function(json) {
			
			exito = json.success;
			
			if (exito) {
				Swal.fire("OK",json.messageList[0], "success");
				$("#dlgQuestionnaire").dialog("close");
				readQuestionnaireResponses();
				$.unblockUI();
			} else {
				var arr = json.errorList;
				var msgError = "";
				for (var cntError = 0; cntError < arr.length; cntError++) {
					msgError += arr[cntError] + "\n";
				}
				
				Swal.fire("Error:","No fue posible guardar las respuestas debido a: " + msgError, "error");
			}
			$.unblockUI();
		},
		error: function(xhr, textStatus, errorThrown) {
			$.unblockUI();
			msgError = "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown;
		}
	});
}

function existsQuestionnaire(){
	var error = true;
	queryFormPost({
		queryName: "questionnairePaymentAnswered",
		async: false,
		callback: function() {
			 error = false;
		
		}
	});
	
	if( error )
		Swal.fire( "Error","No se logro obtener informacion respecto al cuestionario respondido.","error");
}	

function readQuestionnaireResponses(){
	$.ajax({
		url: '../questionnarire/PaymentAnswers',
		type: "GET",
		dataType: 'json',
		data: {},
		async: true,
		success: function(json) {
			
			exito = json.success;
			
			if (exito) {
				var answersArray = json.answers;
				$("#resumenCuestionario").show();
				for( idx = 0; idx < answersArray.length; idx++ ){
					var question = answersArray[idx].question;
					var answer = ("S" === answersArray[idx].answer ?"Sí":"No");
					$("#answersBdy").append('<tr><td class="BL BR BB" style="text-align: left;font-weight:normal ">'+question+'</td><td class="BB">'+ answer + '</td></tr>');
				}
			}else{
				Swal.fire("No se puede continuar debido al error: ",   json.errorMsg + "\nIntente nuevamente o notifique al administrador", "error");
				deshabilitaTodo();
				$.unblockUI();
			}
			
		},
		error: function(xhr, textStatus, errorThrown) {
			msgError = "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown;
			alert("No se puede continuar debido al error: \n" + json.errorMsg + "\nIntente nuevamente o notifique al administrador");
				deshabilitaTodo();
		}
	});
}	


function rechazaProceso(){
	$.blockUI({ message: '<h1><img src="../Generador/imagenes/wait24trans.gif" /> Guardando ...</h1>' });
	parent.document.getElementById("frmCancel").submit();
}