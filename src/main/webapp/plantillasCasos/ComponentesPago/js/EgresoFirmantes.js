
/**
 * Se llama automaticamente por JQuery al terminar la carga de la pagina.
 * Homologa estilos y crea los inputs de fecha.
 */
$(document).ready(function() {
	$(".fecha").each(function() {
		$(this).datepicker({
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true,
			changeYear : true,
			changeMonth : true
		});
	});

	$('input[type=text][readonly]').each(function() {
		$(this).css("background", "#DDDDDD");
	});

	$('textarea[readonly]').each(function() {
		$(this).css("background", "#DDDDDD");
	});

});

/**
 * Carga los firmantes de Vo. Bo, Autorizacion, y suplencias que se han definido en el modulo de firmantes.
 * Carga los tipos de suplencia. 
 */
function iniciaFirmantes() {
	$("#cTipoFirmante").val("VOBO");
	querySelectPost("FirmantesPorTipo_Read", "nombreVoBo", {
		async : false
	});

	$("#cTipoFirmante").val("AUT");
	querySelectPost("FirmantesPorTipo_Read", "nombreAut", {
		async : false
	});

	$("#cTipoFirmante").val("SUPAUT");
	querySelectPost("FirmantesPorTipo_Read", "nombreAUTSuplente", {
		async : false
	});

	$("#cTipoFirmante").val("SUPVOBO");
	querySelectPost("FirmantesPorTipo_Read", "nombreVOBOSuplente", {
		async : false
	});


	querySelectPost("catTipoSuplenciaRead", "VoBoSuplenciaMotivo", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "AutSuplenciaMotivo", {
		async : false
	});
	queryFormPost("firmanteElaboraRead", {
		async : false
	});
}

/**
 * Oculta o muestra la captura de suplencias (Vo Bo o Aut) segun elija el usuario.
 */
function showSuplencia(clase, mostrar) {
	$("." + clase).each(function() {
		if (mostrar)
			$(this).show();
		else
			$(this).hide();
	});
}

/**
 * Funcion de control del checkbox de suplencia (VoBoSuplencia y AutSuplencia)
 */
function changeSuplencia(idCheck) {
	var activo = ($("#" + idCheck).prop("checked") == true);
	var clase = idCheck;
	showSuplencia(clase, activo);
	
	if($("#VoBoSuplencia").prop("checked") == true){
		showSuplencia("AutSuplenciaDiv", false);
	} else {
		showSuplencia("AutSuplenciaDiv", true);
	}
	
	return true;
}

/**
 * Carga el puesto al seleccionar un firmante.
 */
function informacionFirmante(tipoFirmante) {
	$("#cTipoFirmante").val(tipoFirmante)

	var numeroEmpleado = -1;
	var postFijo = ""

	if ("VOBO" == tipoFirmante) {
		numeroEmpleado = $("#nombreVoBo").val();
		postFijo = "VOBO";
	} else if ("AUT" == tipoFirmante) {
		numeroEmpleado = $("#nombreAut").val();
		postFijo = "AUT";
	} else if ("SUPAUT" == tipoFirmante) {
		numeroEmpleado = $("#nombreAUTSuplente").val();
		postFijo = "SUPAUT";
	} else if ("SUPVOBO" == tipoFirmante) {
		numeroEmpleado = $("#nombreVOBOSuplente").val();
		postFijo = "SUPVOBO";
	}

	numeroEmpleado = (numeroEmpleado == "" ? "0" : numeroEmpleado);

	if (parseInt(numeroEmpleado, 10) > 0) {
		$("#cPuestoEmpleado").val("");
		$("#nNumEmpleadoBusqueda").val(numeroEmpleado);
		queryFormPost({
			queryName : "infoPuestoFirmanteRead",
			async : false,
			callback : function() {
				$("#puesto" + postFijo).val($("#cPuestoEmpleado").val());
			}
		});
	} else {
		$("#puesto" + postFijo).val("");
	}

}

function validaFirmantes() {
	var msg = "";

	msg += validaElabora();
	msg += validaVoBo();
	msg += validaAut();

	if ($("#VoBoSuplencia").prop("checked"))
		msg += validaSupVobo();

	if ($("#AutSuplencia").prop("checked"))
		msg += validaSupAut();

	if (msg != "") {
		alert("Antes de continuar revise lo siguiente:\n" + msg);
		return false;
	} else {
		return true;
	}
}

function validaElabora() {
	var msg = "";

	if ($("#nombreElabora").val() == "") {
		msg += "No se logro definir el usuario que capturo el tramite.\n";
	}
	if ($("#puestoElabora").val() == "") {
		msg += "No se encontro el puesto del usuario que capturo el tramite.\n";
	}

	return msg;
}

function validaVoBo() {
	var msg = "";

	if ($("#nombreVoBo").val() == "" || $("#nombreVoBo").val() == "-1") {
		msg += "No ha seleccionado el funcionario que dara el visto bueno\n";
	} else if ($("#puestoVOBO").val() == "") {
		msg += "No se encontro el puesto del funcionario que dara el visto bueno.\n";
	}

	return msg;
}

function validaAut() {
	var msg = "";

	if ($("#nombreAut").val() == "" || $("#nombreAut").val() == "-1") {
		msg += "No ha seleccionado el funcionario que dara su autorizacion.\n";
	} else if ($("#puestoAUT").val() == "") {
		msg += "No se encontro el puesto del funcionario que dara su autorizacion.\n";
	}

	return msg;
}

function validaSupVobo() {
	var msg = "";

	if ($("#noOficioVoBo").val() == "") {
		msg += "Debe ingresar el Numero de Oficio\n";
	}
	if ($("#fechaOficioVobo").val() == "") {
		msg += "Debe ingresar la fecha de oficio.\n";
	}
	if ($("#VoBoSuplenciaMotivo").val() == "") {
		msg += "Debe ingresar el motivo de suplencia.\n";
	}
	if ($("#nombreVOBOSuplente").val() == "" || $("#nombreVOBOSuplente").val() == "-1") {
		msg += "Debe seleccionar el funcionario que dara el visto bueno en suplencia.\n";
	} else if ($("#puestoSUPVOBO").val() == "") {
		msg += "No se encontro el puesto del funcionario que dara el visto bueno en suplencia.\n";
	}

	return msg;

}

function validaSupAut() {
	var msg = "";

	if ($("#noOficioAut").val() == "") {
		msg += "Debe ingresar el Numero de Oficio\n";

	}
	if ($("#fechaOficioAut").val() == "") {
		msg += "Debe ingresar la fecha de oficio.\n";

	}
	if ($("#AutSuplenciaMotivo").val() == "") {
		msg += "Debe ingresar el motivo de suplencia.\n";

	}
	if ($("#nombreAUTSuplente").val() == "" || $("#nombreAUTSuplente").val() == "-1") {
		msg += "Debe seleccionar el funcionario que dara su autorizacion en suplencia.\n";

	} else if ($("#puestoSUPAUT").val() == "") {
		msg += "No se encontro el puesto del funcionario que dara su autorizacion en suplencia.\n";

	}

	return msg;

}

function guardaFirmantes() {
	var exito = false;
	var msg = "";
	$.ajax({
		url : '../firmante/save',
		type : "POST",
		dataType : 'json',
		data : $("form").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				/*Aplica motor contable*/
			} else {
				var errores = json.errorList;
				var cntIndex = 0;
				for (cntIndex = 0; cntIndex < errores.length; cntIndex++) {
					msg = msg + errores[cntIndex] + "\n";
				}
				alert("No se autorizo el tramite debido al error: \n" + msg + "Intente nuevamente");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	return exito;
}

function consultaFirmante(tipoFirmante) {
	$("#tipoFirmanteConsulta").val(tipoFirmante);

	var exito = false;

	$.ajax({
		url : '../egresos/ConsultaFirmante',
		type : "GET",
		dataType : 'json',
		data : $("form").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var firmante = json.firmante;
				for (var elmFirmante in firmante) {

					$('#' + elmFirmante + tipoFirmante).text(firmante[elmFirmante]);

				}

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


function tieneSuplenteVoBo() {
	var ejecutado = false;
	var tieneSuplenteVoBo = false;
	queryFormPost({
		queryName : "tieneSuplenteVoBoRead",
		async : false,
		callback : function() {
			ejecutado = true;
			tieneSuplenteVoBo = (parseInt($("#tieneSuplenteVoBo").val(), 10) > 0);
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";

	return tieneSuplenteVoBo;

}

function tieneSuplenteAut() {
	var ejecutado = false;
	var tieneSuplenteAut = false;

	queryFormPost({
		queryName : "tieneSuplenteAutRead",
		async : false,
		callback : function() {
			ejecutado = true;
			tieneSuplenteAut = (parseInt($("#tieneSuplenteAut").val(), 10) > 0);
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";

	return tieneSuplenteAut;

}
function consultaFirmanteSuplente(tipoFirmante) {
	$("#tipoFirmanteConsulta").val(tipoFirmante);

	var exito = false;

	$.ajax({
		url : '../egresos/ConsultaFirmante',
		type : "GET",
		dataType : 'json',
		data : $("form").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var firmante = json.firmante;
				for (var elmFirmante in firmante) {
					$('#' + elmFirmante + 'Sup' + tipoFirmante).text(firmante[elmFirmante]);
				}

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


function esFirmaElectronica() {
	var ejecutado = false;
	var esFIEL = false;
	$("#cEsFirmaElectronica").val("");

	queryFormPost({
		queryName : "esFirmaElectronica",
		async : false,
		callback : function() {
			ejecutado = true;
			esFIEL = $("#cEsFirmaElectronica").val() == "S";
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";

	return esFIEL;
}

function getEstatusPago() {
	var ejecutado = false;
	var estatusPago = false;
	$("#estatusPago").val("");

	queryFormPost({
		queryName : "estatusPagoRead",
		async : false,
		callback : function() {
			ejecutado = true;
			estatusPago = $("#estatusPago").val();
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";

	return estatusPago;
}

function cargaFirmantesTramite() {
	var ejecutado = false;
	queryFormPost({
		queryName : "firmantesPagoRead",
		async : false,
		callback : function() {
			ejecutado = true;
			$("#nombreVoBo").change();
			$("#nombreAut").change();
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";
}

function cargaSupVoboTramite() {
	var ejecutado = false;
	queryFormPost({
		queryName : "firmanteVoBoPagoRead",
		async : false,
		callback : function() {
			ejecutado = true;
			$("#nombreVOBOSuplente").change();
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";
}


function cargaSupAutTramite() {
	var ejecutado = false;
	queryFormPost({
		queryName : "firmanteAutPagoRead",
		async : false,
		callback : function() {
			ejecutado = true;
			$("#nombreAUTSuplente").change();
		}
	});

	if (!ejecutado)
		throw "No se logro consultar la existencia suplente Vo Bo";
}

function actualizaFirmantes() {
	if (validaFirmantes())
		return guardaFirmantes();
	else
		return false;
}


function muestraFirmantes() {
	var esFIEL = esFirmaElectronica();

	if (!esFIEL) {
		var statusPago = getEstatusPago();
		if ("ESPERA AUTORIZACION" == statusPago)
			muestraResumenFirmas();
		else if ("DEVENGADO" == statusPago)
			muestraEditaFirmantes();
	} else {
		muestraResumenFirmas();
	}

	if (!esFIEL)
		$("#operacionesConsulta").show();

	if ("DEVENGADO" == getEstatusPago())
		$("#actualizarFrmnts").show();

}

function muestraResumenFirmas() {
	$("#esSuplente").val("");
	$("#seleccionFirmantes").hide();
	consultaFirmante("Elabora");
	consultaFirmante("VoBo");
	consultaFirmante("Aut");

	var conSuplenteVoBo = tieneSuplenteVoBo();
	if (conSuplenteVoBo) {
		$("#esSuplente").val("S");
		showSuplencia("VoBoSuplencia", true);
		consultaFirmanteSuplente("VoBo");
	} else {
		showSuplencia("VoBoSuplencia", false);
	}

	if (tieneSuplenteAut()) {
		$("#esSuplente").val("S");
		showSuplencia("AutSuplencia", true);
		consultaFirmanteSuplente("Aut");
		if (!conSuplenteVoBo) {
			showSuplencia("VoBoSuplencia", true);
		}
	} else {
		showSuplencia("AutSuplencia", false);
	}

	$("#resumenFirmantes").show();
}

function muestraEditaFirmantes() {
	$(".firmaElectronica").each(function() {
		$(this).hide();
	});

	$("#seleccionFirmantes").show();
	$("#resumenFirmantes").hide();
	iniciaFirmantes();
	cargaFirmantesTramite();

	if (tieneSuplenteVoBo()) {
		$("#VoBoSuplencia")[0].checked = "checked";
		changeSuplencia('VoBoSuplencia');
		cargaSupVoboTramite();
	} else {
		changeSuplencia('VoBoSuplencia');
	}

	if (tieneSuplenteAut()) {
		$("#AutSuplencia")[0].checked = true;
		changeSuplencia('AutSuplencia')
		cargaSupAutTramite();
	} else {
		changeSuplencia('AutSuplencia');
	}

}