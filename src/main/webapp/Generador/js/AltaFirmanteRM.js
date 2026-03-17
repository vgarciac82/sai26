/**
 * 
 */

var oTableFirmantes;
function resizeDt(){
	if($('#tblFirmantes >tbody >tr').length>0){
		oTableFirmantes.fnAdjustColumnSizing();
	}
			
}
var es_MX = {
	sProcessing: "Procesando...",
	sLengthMenu: "Mostrar _MENU_ registros",
	sZeroRecords: "No hay registros a mostrar",
	sEmptyTable: "No hay datos en la tabla",
	sLoadingRecords: "Cargando...",
	sInfo: "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty: "Registro 0 al 0 de 0",
	sInfoFiltered: "(filtado de _MAX_ registros)",
	sInfoPostFix: "",
	sInfoThousands: ",",
	sSearch: "Buscar:",
	oPaginate: {
		sFirst: "Primero",
		sPrevious: "Ant.",
		sNext: "Sigte.",
		sLast: "&Uacute;ltimo"
	}
};

function createDTFirmantes() {

	var qw = " cUnidadResponsable='" + $("#cIdUnidadEjecutora").val() + "'";

	oTableFirmantes = $("#tblFirmantes").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive: true,
		bDestroy: true,
		sPaginationType: "full_numbers",
		oLanguage: es_MX
		,
		bServerSide: true,
		"fnServerData": function(sSource, aoData, fnCallback) {
			$.ajax({
				"dataType": 'json',
				"type": "POST",
				"url": sSource,
				"data": aoData,
				"success": fnCallback
			});
		},
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catFirmantesRM&qw=" + qw,
		bJQueryUI: true,
		aaSorting: [[0, "asc"]],
		aoColumns: [
			{ sName: "descUnidadFirmante" },
			{ sName: "nombre" },
			{ sName: "apellidoPaterno" },
			{ sName: "apellidoMaterno" },
			{ sName: "puesto" },
			{ sName: "estatus" }
		]
	});
}

function agregaFirmante() {

	if (validaCompleto()) {
		$.blockUI("Creando Firmante...");
		$("#existe").val("0");

		queryFormPost({
			queryName: "firmanteRMExiste",
			async: true,
			callback: function() {

				var existeVal = $("#existe").val() == "" ? "0" : $("#existe").val();

				if (parseInt(existeVal, 10) > 0) {
					swal("El firmante ya existe para la unidad.",{icon:"info",button: "Cerrar"});
					$.unblockUI();
				} else {
					createFirmante();
				}
			}
		});
		$("#existe").val("0");
	}
}

function createFirmante() {
	queryFormPost({
		queryName: "tFirmanteRMCreate",
		async: true,
		callback: function() {
			createDTFirmantes();
			swal("Firmante creado exitosamente.",{icon:"info",button: "Cerrar"});
			$.unblockUI();
		}
	});
}


function validaCompleto() {
	if ($("#cIdUnidadEjecutora").val() == "") {
		swal("La unidad ejecutora es requerida.",{icon:"info",button: "Cerrar"});
		$("#cIdUnidadEjecutora").focus();
		return false;
	}

	if ($("#NumEmpleado").val() == "") {
		swal("El empleado es requerido.",{icon:"info",button: "Cerrar"});
		$("#NumEmpleado").focus();
		return false;
	}

	return true;
}