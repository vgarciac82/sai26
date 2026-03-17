/**
 * tableroControlCalendario.js Archivo de funciones JavaScript auxiliares en el
 * tablero del control del calendario
 * 
 */
var es_mx = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtered from _MAX_ total entries)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Filtro:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};

/**
 * Funcion de incio que crea componentes JQuery y carga informacion inicial.
 */
function init() {
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();

	$('#dt_calendario').dataTable({
		"bPaginate" : false,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : false,
		"bInfo" : true,
		"bAutoWidth" : true,
		"sScrollX" : "1024px",
		"sScrollY" : "450px",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bScrollCollapse" : true,
		// "bServerSide" : true,
		// sAjaxSource : "",
		aoColumns : [ {
			sName : "UNUE"
		}, {
			sName : "B00"
		}, {
			sName : "B01"
		}, {
			sName : "B02"
		}, {
			sName : "B03"
		}, {
			sName : "B04"
		}, {
			sName : "B05"
		}, {
			sName : "B06"
		}, {
			sName : "B07"
		}, {
			sName : "B08"
		}, {
			sName : "B09"
		}, {
			sName : "B10"
		}, {
			sName : "B11"
		}, {
			sName : "B12"
		}, {
			sName : "B13"
		}, {
			sName : "B14"
		}, {
			sName : "B15"
		} ],
		oLanguage : es_mx
	});

	// Carga el ejercicio fiscal activo.
	queryFormPost("EjercicioFiscalActvRead", {
		async : false
	});

	$("#descargaCalendarioCompleto").button().click(function() {
		descargaCalendario();
	});

}

/**
 * Funcion que llama al servicio de descarga de calendario. Si se omiten los
 * parametros descargara completo el calendario.
 * 
 * @param unidadEjecutora
 *            Unidad Ejecutora de la que se desea hacer la descarga.
 * @param unidadNormativa
 *            Unidad Normativa de la que se desea hacer la descarga.
 */
function descargaCalendario(unidadEjecutora, unidadNormativa) {
	if (unidadEjecutora)
		$("#ue").val(unidadEjecutora);
	else
		$("#ue").val("");
	if (unidadNormativa)
		$("#un").val(unidadNormativa);
	else
		$("#un").val("");
	$("#formDescargaCalendario").submit();
}