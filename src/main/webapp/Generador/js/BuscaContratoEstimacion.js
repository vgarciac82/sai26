/**
 * DataTable que almacena los resultados de la busqueda.
 */
var dTable;

/**
 * Folio para el contrato de estimacion.
 */
var folioSAI = "";

/**
 * Parametros de lenguaje
 */
var lengParams = {
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

/**
 * Inicializa los campos de la pantalla.
 */
function init() {
	dTable = $("#resultTable").dataTable({
		"bJQueryUI" : true,
		"sScrollY" : 250,
		"bPaginate" : false,
		"oLanguage" : lengParams
	});
	setDblClck();

	$("#searachButton").button().click(function() {
		search();
	});

	$("#yes")
			.button()
			.click(
					function() {
						$.blockUI({
							message : "<h1>Espere ...</h1>"
						});
						$("#folioSAI").val(folioSAI);
						queryFormPost({
							queryName : 'readEstatusTramiteObraPublica',
							async : false,
							callback : function() {
								var operAct = $("#OperacionActual").val();
								if (operAct != "3") {
									Swal.fire({ icon: 'info',
												text: "No se puede inicar un convenio modificatorio ya que:\n*El tramite de contrato no ha terminado\n*El contrato se encuentra ya en proceso de Convenio Modificatorio." });									
									$.unblockUI();
								} else {
									$("#mainForm").submit();
								}
							}
						});
					});

	$("#no").button().click(function() {
		$.unblockUI();
		folioSAI = "";
		$("#OperacionActual").val("");
	});

	$("#cveContrato").focus();

}
/**
 * Busca contratos en base al numero.
 */
function search() {
	folioSAI = "";
	$("#OperacionActual").val("");
	if ($("#cveContrato").val() == "") {
		Swal.fire({ icon: 'warning',
					text: "Debe ingresar el n\u00FAmero de contrato" });
		$("#cveContrato").focus();
		return;
	} else {
		$.blockUI({
					message : "<p><img src='imagenes/wait24trans.gif' />&nbsp;&nbsp;Buscando. Por favor espere...</p>"
				});
		var condition = " ccvecontrato LIKE '" + $("#cveContrato").val() + "%'";
		var tableName = "BUSCA_CONTRATO_OBRA_PUBLICA_CONV_MODIF";
		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : tableName,
			Param : condition,
			MaxReg : "",
			ajax : 'false'
		}, function(data) {
			dTable.fnClearTable();
			for ( var i = 0; i < data.length; i++) {
				dTable.fnAddData([ data[i].Col0, data[i].Col1, data[i].Col2, data[i].Col3 ]);
			}
			$.unblockUI();
			if (i == 0)
				Swal.fire({ icon: 'warning',
							text: "No se encontraron coincidencias para el contrato: " + $("#cveContrato").val() });				
		});
	}

}

/**
 * Establece la funcion para el doble click. Lee de la tabla los valores
 * necesarios para iniciar el convenio modificatorio.
 */
function setDblClck() {
	$("#resultTable tbody").dblclick(function(evt) {

		var aPos = dTable.fnGetPosition(evt.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var arr = dTable.fnGetData()[currIndex];
		folioSAI = arr[3];
		$.blockUI({
			message : $('#question'),
			css : {
				width : '475px'
			}
		});
	});
}