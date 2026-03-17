/**
 * Funcion que inicializa los componentes del JSP
 */
function init() {

	querySelectPost("catalogoTramitesAdjuntarRead", "TITULO_APLICACION", {
		async : false
	});

	$("#enviarBtn").button().click(function() {
		sendFile();
	});

	creaDigalogoRespuesta();
	if (showResult){
		$("#divLblCarga").css("display", "block");
		muestraResultados();
	}
}

function sendFile() {
	if ($("#fileName").val() == "") {
		alert("Debe eligir el archivo de carga.");
		return;
	} else if (confirm("Esta seguro de realizar la carga?")) {
		if (confirm("Se adjuntaran los comprobantes al tramite "
				+ $("#TITULO_APLICACION :selected").text() + " Es correcto?")){
			$.blockUI({message: "Procesando espere ......"});
			$("#formAdjuntar").submit();
		}
	}
}

function creaDigalogoRespuesta() {
	$("#repuestaLog").dialog({
		autoOpen : false,
		width : "800px",
		heigth : "450px",
		modal : true
	});
}

function muestraResultados() {
	$("#repuestaLog").dialog("open");
}