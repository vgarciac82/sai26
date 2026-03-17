function onLoadPlantilla(id_oper) { //Carga Plantilla
	var resp = true;
	switch (parseInt(id_oper, 10)) {
	/*Captura*/
	case 1:

		try {
			$("#idCaso").val(idCaso);
			$("#numEmpleadoElab").val(numeroEmpleado);
			if (parent.document.getElementById("pb_save")) {
				parent.document.getElementById("pb_save").disabled = true;
			}
			$("#tabs").tabs();
			initUI();
			estatusCambiado(id_oper);
			
			$("#Concepto").click(function() {});
			if (parent.document.getElementById("pb_save")) {
				parent.document.getElementById("pb_save").disabled = false;
			}
			actualizaGabinete();

		} catch (e) {
			var msg = "";
			if (e instanceof TypeError)
				msg = e.message;
			else
				msg = e;

			alert("Error cargando informacion inicial \nCasua:" + msg + "\n Intente nuevamente. Si el problema continua reporte al administrador.");
			deshabilitaTodo();
		}
	case 2: //
		break;
	default:
		resp = false;
		break;
	}
	return resp;
}

/**
 * funcion llamada al dar clic boton Guardar.
 */

function onSubmit(id_oper) {
	var ejecucionCorrecta = ejecutaValidaciones(id_oper, $("#nIDEstatus").val());
	switch (parseInt(id_oper, 10)) {

	case 1: /*Captura*/
		ejecucionCorrecta = ejecucionCorrecta && setVariablesCaso(id_oper);

		if (ejecucionCorrecta) {
			try {
				avanzaEstatus(id_oper);
				avanzaTab = true;
			} catch (e) {
				alert(e);
				ejecucionCorrecta = false;
			}

		}
		break;
	case 2:
		break;
	default:
		ejecucionCorrecta = false;
		break;
	}
	return ejecucionCorrecta;
}

function ResponsableSiguiente(id_oper) {
	switch (parseInt(id_oper, 10)) {
	case 1: // Captura
		return "AUTORIZA_PAGODIRECTO";
		break;
	case 2:
		return "CONSULTA_PAGODIRECTO";
		break;
	}
}
function OperacionSiguiente(id_oper) {
	switch (parseInt(id_oper, 10)) {
	case 1: //Captura
		return "autoriza_factura";
		break;
	case 2:
		return "consulta_factura";
		break;
	} // fin switch
} // fin OperacionSiguiente

function onPostDisplay(id_oper) {
	estatusCambiado(id_oper);
	return true;
} // fin onPostDisplay

function onPostSubmit(id_oper) { // clic boton enviar

	var respSig = parent.document.getElementById("responsable").value;
	if (respSig == "") {
		parent.execResponsable();
		parent.execOperacion();
	}
	$.blockUI();
	return true;
}
// fin  onPostSubmit


function setVariablesCaso(idOperacion) {
	var ejecucionCorrecta = true;
	try {
		var operacion = parseInt(idOperacion, 10);
		var estatus = parseInt($("#nIDEstatus").val(), 10);

		/*Para evitar sobreescribir la informacion. Solo en el primer guardar se establecen los valores de las variables*/
		if (operacion == 1 && estatus == 0) {
			p.gestion.setFolio($("#cFolio").val());
			p.gestion.setOperador($("#operador").val());
			p.gestion.setFechaDocumento($("#fechaCaptura").val());
			p.gestion.setEjercicioFiscal($("#ejercicioFiscal").val());
		}
	} catch (e) {
		window.alert("onSubmit: Error: " + e);
		parent.document.getElementById("pb_send").disabled = true;
		ejecucionCorrecta = false;
		throw e;
	}
	return ejecucionCorrecta;
}

function actualizaGabinete() {
	var ejecutado = false;

	queryFormPost({
		queryName : "idGabineteRead",
		async : false,
		callback : function() {
			ejecutado = true;
		}
	});

	if (!ejecutado)
		throw "No se logo consultar el ID Gabinete. Intente nuevamente";
}