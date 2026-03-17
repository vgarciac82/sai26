/**
 * 
 */
var terminadoEjectuado = false;

function onLoadPlantilla() {
	setValoresIniciales();
	informacionPagoRead();
	muestraFirmantes();

	$("#actualizarFrmnts").button().click(function() {
		actualizaFirmantes();
	});
	$("#imprimirCxP").button().click(function() {
		imprimirCxP();
	});
}

function informacionPagoRead() {
	var msgError = "";
	if (!terminadoEjectuado) {
		try {
			var exito = false;
			$.ajax({
				url : '../egresos/resumenFinalPago',
				type : "GET",
				dataType : 'json',
				data : $("#formPagos").serialize(),
				async : false,
				success : function(json) {
					exito = json.success;
					if (exito) {
						var resumen = json.resumen;
						for (var k in resumen) {
							$("#" + k).text(resumen[k]);
						}
						resumenRetencionesPago();
						resumenMovimientos();
					} else {
						var arr = json.errorList;
						for (var cntError = 0; cntError < arr.length; cntError++) {
							msgError += arr[cntError] + "\n";
						}
					}
				},
				error : function(xhr, textStatus, errorThrown) {
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

function setValoresIniciales() {
	for (objVar in infoObj) {
		$("#" + objVar).val(infoObj[objVar]);
	}
}

function imprimirCxP() {
	window.open("../egresos/imprimeSolPago",
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");
}