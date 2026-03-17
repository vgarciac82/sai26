var es_MX = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtado de _MAX_ registros)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Buscar:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};

function aplicaEstilos() {
	$('input[type=text][readonly]').each(function() {
		$(this).addClass("notEditable");
	});

	$('input[type=text][readonly],textarea[readonly]').each(function() {
		$(this).addClass("notEditable");
	});

	$(".fecha").each(function() {
		$(this).datepicker({
			showOn : "button",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
	});

	$(".decimal").each(function() {
		formatoMoneda(this);
	});
}

function initUI() {
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	aplicaEstilos();
	creaTablaRetenciones();
	creaTablaPComprometido();
	creaTablaPDevengar();
}

function creaTablaRetenciones() {
	$("#grdRetencion").dataTable({
		"oLanguage" : es_MX,
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		"sScrollY" : "75px"
	});
}


function creaTablaPComprometido() {
	$("#grdPComprometido").dataTable({
		"oLanguage" : es_MX,
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		"sScrollY" : "100px"
	});

}


function creaTablaPDevengar() {
	$("#grdPDevengado").dataTable({
		"oLanguage" : es_MX,
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		"sScrollY" : "100px"
	});

}

function formatoNumerico(val) {
	val = val.replace("$", "");
	val = val.replace(/,/g, "");

	if (val.indexOf("(") >= 0) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
	}
	return val;
}

function formatoMoneda(fld) {
	$("#" + fld.id).formatCurrency();
}

function soloNumerosPositivo(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = '-0123456789.';
	var key = String.fromCharCode(keyPressed);
	if (strCheck.indexOf(key) == -1)
		return false;
	return true;
}