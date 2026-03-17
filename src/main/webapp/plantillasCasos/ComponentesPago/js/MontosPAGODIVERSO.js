/**
 * 
 */


$(document).ready(function() {

	$(".montoRecepcion").each(function() {
		$(this).val("0.00");
	});

});

function cargaMontosRecepcion() {
	var idRecepcion = $("#cIdRecepMat").val();
	if ("" == idRecepcion)
		reiniciaMontosPago();
	else
		queryFormPost({
			queryName : "infoRecepcionMatREAD",
			async : false,
			callback : function() {
				$(".montoRecepcion").each(function() {
					formatoMoneda($(this)[0]);

					if ($("#isFactAmort").val() == "1") {
						habilitaAmortiazacion();
					} else {
						inhabilitaAmortizacion();
					}

					if ($("#cIdRecepMat").val().substr(0, 2) == "RA")
						$("#esAnticipo").val("S");
					else
						$("#esAnticipo").val("N");

				});
			}
		});
}

function reiniciaMontosPago() {
	$("#isFactAmort").val("0");
	$("#esAnticipo").val("N");
	$(".montoRecepcion").each(function() {
		$(this).val("0.00");
		formatoMoneda($(this)[0]);
	});
	$("#isFactAmort").val("0");
	inhabilitaAmortizacion();
}

function habilitaAmortiazacion() {
	$("#mAmortizacion").removeClass("notEditable");

	$("#mAmortizacion")[0].readOnly = false;
	$("#mAmortizacion").keypress(function(e) {
		return soloNumerosPositivo(e);
	});

}

function onFocusMoney(trgt) {
	$("#" + trgt).val(formatoNumerico($("#" + trgt).val()));
}


function onBlurMoney(trgt) {
	var exito = true;

	try {
		if ($.trim($("#" + trgt).val()) == "")
			$("#" + trgt).val("0");

		formatoMoneda($("#" + trgt)[0]) ;
		exito && recalculaPago();
	} catch (e) {
		alert(e.message);
		exito = false;
	}

	return exito;
}

function inhabilitaAmortizacion() {
	$("#mAmortizacion").addClass("notEditable");
	$("#mAmortizacion")[0].readOnly = true;
}


function instanciaRecepcion() {
	var recepcion = {
		"cIdPedContDef" : $("#numeroContrato").val(),
		"cIdRecepAnticipo" : $("#esAnticipo").val(),
		"cIdRecepcionMat" : $("#cIdRecepMat").val(),
		"factorAmortizacion" : $("#isFactAmort").val(),
		"recepMat" : "1"
	};

	return recepcion;
}

function recalculaPago() {
	var exitoso = false;

	$.ajax({
		url : '../egresos/CalculaPAGODIVERSO',
		dataType : 'json',
		contentType : 'application/json',
		type : "POST",
		data : JSON.stringify({
			"recepcion" : instanciaRecepcion(),
			"importeAmortizacion" : formatoNumerico($("#mAmortizacion").val()),
			"importeDescuento" : formatoNumerico($("#mDescuentos").val()),
			"importePenalizacion" : formatoNumerico($("#mPenas").val())
		}
		),
		async : false,
		success : function(json) {
			exitoso = json.success == "true";
			if (exitoso)
				alert("Se logro");
			else
				alert("No se puede continuar debido al siguiente error: " + json.data_1.result);

		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Error con la peticion en recalculaPago() \nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	return exitoso;
}