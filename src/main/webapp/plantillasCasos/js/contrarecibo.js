
/**
 * 
 */
$(document).ready(function() {

	if ($("form:first #contrareciboGenerado").length == 0) {
		$("form:first").append("<input id=\"contrareciboGenerado\" name=\"contrareciboGenerado\" type=\"hidden\" />");
	}

	$("#contrareciboGenerado").val("");

});

function generaContrarecibo() {
	var cxp = "";
	try {
		var cxp = $.trim(consultaContrarecibo());

		if (cxp == "") {

			var exito = false;
			$.ajax({
				url : '../egresos/generaContrarecibo',
				type : "POST",
				dataType : 'json',
				data : $("#formPagos").serialize(),
				async : false,
				success : function(json) {
					exito = json.success;
					if (exito) {
						alert("Contrarecibo generado exitosamente.");
						cxp = consultaContrarecibo();
					} else {
						throw json.errorList;
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					throw "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown;
				}
			});
		}
		return cxp;
	} catch (e) {
		var msg = "";
		if (e instanceof TypeError)
			msg = e.message;
		else
			msg = e;

		alert("No se puede continuar debido al error: \n" + msg + "\nIntente nuevamente o notifique al administrador");
		throw e;
	}
}

function consultaContrarecibo() {
	var ejecutado = false;
	var cxp = "";
	$("#contrareciboGenerado").val("");
	
	queryFormPost({
		queryName : "existeContrareciboRead",
		async : false,
		callback : function() {
			ejecutado = true;
			cxp = $("#contrareciboGenerado").val();
			$("#contrarecibo").val(cxp);
		}
	});

	if (!ejecutado)
		throw "No fue posible consultar el contrarecibo.";
	return cxp;
}