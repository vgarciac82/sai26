/**
 * DataTable que almacena los resultados de la busqueda.
 */
var dTable;

/**
 * Folio para el convenio modificatorio.
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
		"oLanguage" : lengParams,
		"aoColumnDefs" : [ {
			"bSearchable" : false,
			"aTargets" : [ 4 ]
		}, {
			"bSearchable" : false,
			"bVisible" : false,
			"aTargets" : [ 5 ]
		}, {
			"bSearchable" : false,
			"bVisible" : false,
			"aTargets" : [ 6 ]
		} ]
	});

	$("#searachButton").button().click(function() {
		search();
	});

	$("#yes").button().click(function() {
		$.blockUI({
			message : "<h1>Espere ...</h1>"
		});
		queryFormPost({
			queryName : "updateSuspendePagoOP,updateSuspendePagoCO",
			async : false,
			callback : function() {
				alert("Terminado exitosamente");
				$.unblockUI();
			}
		});

	});

	$("#no").button().click(function() {
		$.unblockUI();
	});

	queryFormPost("cEjercicioFiscalActivoRead", {
		async : false
	});

	$("#cveContrato").focus();

}

/**
 * Busca contratos en base al numero.
 */
function search() {
	folioSAI = "";
	if ($("#cveContrato").val() == "") {
		alert("Debe ingresar el n\u00FAmero de contrato");
		$("#cveContrato").focus();
		return;
	} else {
		$
				.blockUI({
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
				dTable.fnAddData([
						data[i].Col0,
						data[i].Col1,
						data[i].Col2,
						data[i].Col3,
						creaCheckBox(data[i].Col5, "chk"+i, data[i].Col0,
								"suspendePagos('" + i + "')",
								data[i].Col4), data[i].Col4, data[i].Col5 ]);
			}
			$.unblockUI();
			if (i == 0)
				alert("No se encontraron coincidencias para el contrato: "
						+ $("#cveContrato").val());
		});
	}

}

function creaCheckBox(name, id, val, onclickFn, activo) {
	var str = '<input type="checkbox" value="' + val + '" id="' + id
			+ '" name="' + name + '" onclick="' + onclickFn + '"'
			+ (activo == 'S' ? 'checked="checked"' : '') + '/>';
	return str;
}

function suspendePagos(cCveContrato) {

	var estatusPago, folioSAI, cCentroContable;
	var index = parseInt(cCveContrato,10);
	
	var rows = dTable.fnGetData();
	var dataCnt = rows[index];
	
	$("#estatusPago").val("");
	$("#folioSAI").val("");
	$("#cCentroContable").val("");
	
	estatusPago = $("#chk" + index).is(":checked") ? "S" : "N";
	folioSAI = dataCnt[0];
	cCentroContable = dataCnt[6];
		
	$("#estatusPago").val(estatusPago);
	$("#folioSAI").val(folioSAI);
	$("#cCentroContable").val(cCentroContable);
	
	if ("N" == estatusPago) {
		$("#msgAdvertencia").text(
				"Se activaran los pagos para el contrato seleccionado: "
						+ folioSAI);
	} else {
		$("#msgAdvertencia").text(
				"Se suspenderan los pagos para el contrato seleccionado: "
						+ folioSAI);
	}
	$.blockUI({
		message : $('#question'),
		css : {
			width : '375px'
		}
	});
}