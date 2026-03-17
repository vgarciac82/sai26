
/**
 * Inicializa catalogos y muestra la informacion no editable.
 */
function setInitialValuesConcepto() {
	/* ========== Valores Fijos ======= */
	$("#nFolioPagoDirecto").val($("#nFolioPago").val());
	$("#fAplicacion").val($("#fechaAplicacion").val());
	muestraBotonCalculo();
	
	var idEstatus = obtenPagoEstatus();
	if (idEstatus == 1) {

		/* =========== Catalogos =========== */
		querySelectPost("CAT_TIPO_OPERACIONRead", "catIdTipoOperacion", {
			async : false
		});

		cargaDestinoGasto();

	}
}

function limpiaSelect(idSelect, defaultVal, defaultText) {
	$("#" + idSelect).empty();
	$("#" + idSelect).append('<option value="' + defaultVal + '" selected="selected">' + defaultText + '</option>');
}

function cat_beneficiario() {
	var formName = "formPagos";
	var inputName = "cTipoRfc";
	var inputRFCTarget = "cIDRFC";
	var inputDRFCTarget = "cnombre";

	$("#cTipoRfc").val("1,2");
	window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=' + formName + '&inputName=' + inputName + '&inputRFCTarget=' + inputRFCTarget + '&inputDRFCTarget=' + inputDRFCTarget, 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
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

function muestraDivFacturas() {
	if (conceptoMinimoCapturado()) {
		creaDiagloFacturas(true);
		return true;
	} else {
		return false;
	}
}

function precapturaCompleta() {
	var msg = "";

	if ($.trim($("#DESTINO_GASTO").val()) == "" || $.trim($("#DESTINO_GASTO").val()) == "-1")
		msg += "\nSeleccione el tipo de destino.";
	if ($.trim($("#concepto").val()) == "")
		msg += "\nCapture el concepto del pago.";
	if ($.trim($("#cIDRFC").val()) == "")
		msg += "\nSeleccione el beneficiario del pago.";
	if ($.trim($("#cIDRFC").val()) != "" && $.trim($("#ctaBancaria").val()) == "")
		msg += "\nEl beneficiario no cuenta con cuenta bancaria autorizada. Reporte al administrador.";
	var facturaCapturdas = getFacturasCapturadas();

	if (facturaCapturdas == 0)
		msg += "\nDebe cargar las facturas del pago.";

	var msgDif = getDiferenciaPagoCFDI();
	if (msgDif != "")
		msg += "\n" + msgDif;

	//Valida que si tiene PPD se haya capturado el correo
	var mensaje = validaCapturaCorreo();
	if (mensaje != "")
		msg += "\n" + mensaje;
	
	return msg;
}

function muestraBotonCalculo(){
	$("#tieneCedular").val("0");
		
		queryFormPost("tieneImpuestoCedular5Mil", {async : false});
		
		if( $("#tieneCedular").val() == "0" ){
			$("#calcularBtn").hide();
		}else{
			//document.getElementById(calcularBtn).style.visibility = "visible"; 
			$("#calcularBtn").show();
		}		
}

function revisaTipoFacturas(){
	queryFormPost("readEsPPD", {async : false});
	
		if( $("#esPPD").val() != "0" ){
			Swal.fire ('IMPORTANTE', 'La factura adjunta tiene el metodo de pago PPD por lo que es indispensable posteriormente solicitar al proveedor el CFDI de complemento de pago.', 'warning');
			creaDialogCorreo(true);
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
function calcularImpuesto(){
	queryFormPost("consultaImpuesto5", {async : false});
	
	queryFormPost("actualizarImporte", {async : false});
	Swal.fire ('Importe Actualizado', 'Se actualizó el importe de las facturas', 'success');
}

function creaDiagloFacturas(abrir) {
	$("#dialog-validaFact").dialog({
		autoOpen : false,
		height : 600,
		width : 950,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				var aData = oTablevFact.fnGetData();
				$(this).dialog("close");
			},
			"Cancelar" : function() {
				bClicBtn = true;
				$(this).dialog("close");
			}
		},
		
		close : function() {
			muestraBotonCalculo();
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

function creaDialogCorreo(abrir) {
	$("#dialog-actualizaCorreo").dialog({
		autoOpen : false,
		height : 270,
		width : 600,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				validarCorreo();
			},
			"Cancelar" : function() {
				$(this).dialog("close");
			}
		},
		open : function() {
			mostrarCorreo();
			
		}
	});

	if (abrir) {
		$("#dialog-actualizaCorreo").dialog("open");
	}
}


function mostrarCorreo(){
	queryFormPost("mostrarCorreoRead",{async: false});
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
			$("#dialog-actualizaCorreo").dialog("close");
		}
	} else {
     	Swal.fire("Correo Invalido", "El correo no es valido, intente de nuevo", "error");
     	return;
    }
}
function guardaEncabezadoParcial() {
	var exito = false;
	$("#accion").val("SAVE");
	retiraFormatoMoneda();
	$.ajax({
		url : '../egresos/SAVE',
		type : "POST",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {

			} else {
				alert("No es posible continuar debido al siguiente error: " + json.data_1.result)
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	$("#accion").val("");
	formatoMoneda();
	return exito;

}



function togleDivFacts(nIdDiv) {
	if (nIdDiv == 0) {
		$('#uploadFacturasFrm').attr('src', "../Generador/UploadFacturas.jsp?tipo_pago=PAGODIRECTO&RFC=" + $("#rfc").val());
		leeDatosFactura();
		creaDTFacturas();
	} else {
		$('#uploadFacturasFrm').attr('src', "../Generador/UploadFacturas.jsp??tipo_pago=PAGODIRECTO&RFC=" + $("#rfc").val());
		$("#uploadFacturasDiv").show();
	}

}

function Sinfrmt(fld) {
	var valcol = quitaFmt($(fld).val());
	$(fld).val(valcol);
}

function retiraFormatoMoneda() {
	$(".money").each(function() {
		Sinfrmt(this);
	});
}
function formatoMoneda() {
	$(".money").each(function() {
		Sinfrmt(this);
	});
}

function cambiafrmt(fld) {
	$(fld).formatCurrency();
}

function conceptoPagoDirectoRead() {
	var error = null;

	queryFormPost({
		queryName : "conceptoPagoDirectoRead",
		async : false,
		callback : function() {
			try {
				//alert("idDestinoGasto: " + $("#DESTINO_GASTO").val() );
				$("#DESTINO_GASTO").val($("#idDestinoGasto").val());
				$("#DESTINO_GASTO").change();
				cargaCtaBancariaRFC();
			} catch (err) {
				alert("Ocurrio el siguiente error y no se podra continuar: " + err.message);
				error = err;
			}
		}
	});

	if (error)
		throw error;
}

/**
 * Comprueba que se ha capturado la informacion minima requerida para continuar con el pago.
 */
function validacionesConcepto() {
	var msg = precapturaCompleta();
	if (msg != "") {
		Swal.fire('Para continuar debe corregir lo siguiente:', msg, 'warning');
		return false;
	} else {
		return true;
	}

}

/**
 * Devuelve el total de facturas capturadas.
 * 
 */
function getFacturasCapturadas() {
	$("#nFacturasCapturadas").val(0);
	queryFormPost("facturasCapturadasRead", {
		async : false
	});
	return parseInt(($("#nFacturasCapturadas").val() == "" ? 0 : $("#nFacturasCapturadas").val()), 10);
}

/**
 * Devuelve si hay diferencia entre el pago y la factura.
 * 
 */
function getDiferenciaPagoCFDI() {
	var consultado = false;
	$("#diferenciaCFDIPago").val("0");
	var diferenciaCFDIPago = 0.00;
	var msgTxt = "";

	queryFormPost({
		queryName : "diferenciaPagoDirectoCFDI",
		async : false,
		callback : function() {
			consultado = true;
			if ($("#diferenciaCFDIPago").val() == "")
				$("#diferenciaCFDIPago").val("0");
			diferenciaCFDIPago = parseFloat($("#diferenciaCFDIPago").val());
			if (diferenciaCFDIPago != 0)
				msgTxt = "Existe diferencia de " + diferenciaCFDIPago + " entre el pago y las facturas.";
		}
	});

	if (!consultado)
		msgTxt = "No fue posible consultar diferencias entre CFDI y Pagos";

	return msgTxt;
}


function cargaDestinoGasto() {
	querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {
		async : false
	});
}

function conceptoMinimoCapturado() {
	var msg = "";
	var completado = true;

	if ($.trim($("#catIdTipoOperacion").val()) == "" || $.trim($("#catIdTipoOperacion").val()) == "-1") {
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
	if ($.trim($("#cIDRFC").val()) == "") {
		msg += "\nSeleccione el beneficiario del pago.";
		completado = false;
	}
	if ($.trim($("#cIDRFC").val()) != "" && $.trim($("#ctaBancaria").val()) == "") {
		msg += "\nEl beneficiario no cuenta con cuenta bancaria autorizada. Reporte al administrador.";
		completado = false;
	}

	if (msg != "")
		Swal.fire("Para continuar debe corregir lo siguiente:",  msg, "info");

	return completado;
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
				$("#capturaConceptoPago").hide();
				creaDTResumenFacturas();
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

function validaMontoPartidaUMAChange(){

	let idRFC = $("#cIDRFC").val();

	if(idRFC != ""){
		getPartidasExcedenUMA(idRFC);
	}

}
