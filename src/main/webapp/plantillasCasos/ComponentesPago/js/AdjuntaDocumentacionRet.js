
var cCarpetaNombre = "";
var cDocumentoNombre = "";
var cTipoRetencion = "";

$(document).ready(function() {

	if ($("form:first #requiereDocumentacionRet").length == 0) {
		$("form:first").append("<input id=\"requiereDocumentacionRet\" name=\"requiereDocumentacionRet\" type=\"hidden\" />");
		$("form:first").append("<input id=\"documentosRetCapturados\" name=\"documentosRetCapturados\" type=\"hidden\"/>");
		$("form:first").append("<input id=\"nombreRetencion\" name=\"nombreRetencion\" type=\"hidden\"/>");
		$("form:first").append("<input id=\"cCarpetaNombre\" name=\"cCarpetaNombre\" type=\"hidden\"/>");
		$("form:first").append("<input id=\"cDocumentoNombre\" name=\"cDocumentoNombre\" type=\"hidden\"/>");
	}

	$("#requiereDocumentacionRet").val("0");

});

function requiereDocumentacionRetencion() {
	var ejecutado = false;
	var requiereDocumentacion = false;
	
	queryFormPost({
		queryName : "requiereDoctosRetencionRead",
		async : false,
		callback : function() {
			ejecutado = true;
			var requerido = parseInt($("#requiereDocumentacionRet").val(), 10);

			if (requerido > 0) {
				setValores();
				var documentoCapturado = validaDoctoCapturado();
				if (!documentoCapturado) {
					requiereDocumentacion = true;
					alert("Debido a que elimino la retencion: " + cTipoRetencion + " debe adjuntar el oficio de justificacion");
					iniciaCaptura();
					creaDivAdjuntos();
				}
			}

		}
	})

	if (!ejecutado) {
		throw "No se pudo determinar si se requiere documentacion por retencion eliminada";
	}
	return requiereDocumentacion;
}

function validaDoctoCapturado() {
	var ejecutado = false;
	var adjuntado = false;
	queryFormPost({
		queryName : "doctosRetencionCapturadosRead",
		async : false,
		callback : function() {
			ejecutado = true;

			var documentosCapturados = parseInt($("#documentosRetCapturados").val(), 10);
			if (isNaN(documentosCapturados)) {
				ejecutado = false;
			} else {
				if (documentosCapturados > 0) {
					adjuntado = true;
				}
			}

		}
	})

	if (!ejecutado) {
		throw "No se pudo determinar si se requiere documentacion por retencion eliminada";
	}
	return adjuntado;

}

function setValores() {
	var ejecutado = false;
	queryFormPost({
		queryName : "infoDocumentacionRetReq",
		async : false,
		callback : function() {
			ejecutado = true;
			cCarpetaNombre = $("#cCarpetaNombre").val();
			cDocumentoNombre = $("#cDocumentoNombre").val();
			cTipoRetencion = $("#nombreRetencion").val();
		}
	})

	if (!ejecutado) {
		throw "No se pudo determinar los componentes de la retencion eliminada";
	}
	return ejecutado;
}

function iniciaCaptura() {
	$("#btnAdjuntaDocRete").button().click(function() {
		adjuntaRetencionAction();
	});
	$("#adjuntarDocRete").show();
}

function creaDivAdjuntos() {
	var divJustificacion = "";
	divJustificacion += " <div id=\"dialog-Justificacion\" title=\"Ajuntar Justificacion\"> ";
	divJustificacion += " 	<div id=\"uploadJustificacion\"> ";
	divJustificacion += " 		<iframe id=\"UploadJustificacionIva6\" src=\"../Generador/UploadJustificacionIva6.jsp?tipo_pago=PAGODIVERSO\" align=\"top\" frameborder=\"0\" height=\"360\" width=\"510\"> </iframe> ";
	divJustificacion += " 	</div> ";
	divJustificacion += " </div> ";
	$("form:first").append(divJustificacion);
	creaDialogJustificacion();

}


function creaDialogJustificacion(abrir) {
	$("#dialog-Justificacion").dialog({
		autoOpen : false,
		height : 472,
		width : 558,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				togleJustificacion();
			},
			"Cancelar" : function() {
				bClicBtn = true;
				$(this).dialog("close");
			}
		}
	});
}

function adjuntaRetencionAction() {
	$("#dialog-Justificacion").dialog("open");
}