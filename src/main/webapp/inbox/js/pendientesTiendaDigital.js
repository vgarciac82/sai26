var es_mx = {
	sProcessing: "Procesando...",
	sLengthMenu: "Mostrar _MENU_ registros",
	sZeroRecords: "No hay registros a mostrar",
	sEmptyTable: "No hay datos en la tabla",
	sLoadingRecords: "Cargando...",
	sInfo: "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty: "Registro 0 al 0 de 0",
	sInfoFiltered: "(filtered from _MAX_ total entries)",
	sInfoPostFix: "",
	sInfoThousands: ",",
	sSearch: "Filtro:",
	oPaginate: {
		sFirst: "Primero",
		sPrevious: "Ant.",
		sNext: "Sigte.",
		sLast: "&Uacute;ltimo"
	}
};

var oTable;

var tituloAplicacion = 'PAGODIVERSO';
var folioPago = -1;

function actualizaSICOP() {

	$.blockUI();

	var aceptado = $('input[name=oper]:checked').val();
	var valorSICOP = -4;
	if ("S" == aceptado) {
		valorSICOP = 1;
	}

	$("#nEnviadoSICOP").val(valorSICOP);
	$("#nFolioPagoDiverso").val(folioPago);

	queryFormPost({
		queryName: "actualizaDiversoSICOP",
		async: true,
		callback: function() {
			$.unblockUI();
			creaTabla();
			if ("S" == aceptado) {
				alert("El pago se actualizo correctamente. Ahora puede procesar el pago en SICOP.");
			} else {
				alert("El pago se rechazo correctamente. Ahora puede cancelar el pago en SAI y SICOP.");
			}
		}
	});

}

function init() {

	var dialog = "";
	dialog += "<div id='dlgDocumentos'>";
	dialog += "<fieldset>";
	dialog += "<legend>Operaciones</legend>";
	dialog += "<table>";
	dialog += "<tr>";
	dialog += "<td align=\"rigth\">";
	dialog += "<input type=\"radio\" id=\"acepta\" name=\"oper\" checked value=\"S\"><label for=\"acepta\">Aceptar";
	dialog += "&nbsp";
	dialog += "<input type=\"radio\" id=\"rechaza\" name=\"oper\"  value=\"N\"><label for=\"rechaza\">Rechazar";
	dialog += "</td>";
	dialog += "</tr>";
	dialog += "</table>";
	dialog += "</fieldset>";
	dialog += "<div id='DoctosDiv'>";
	dialog += "<fieldset>";
	dialog += "<legend>Documentos</legend>";
	dialog += "<ul id='doctosList'>";
	dialog += "</ul>";
	dialog += "</fieldset>";
	dialog += "</div>";
	dialog += "</div>";

	$(document.body).append(dialog);

	creaTabla();

	$("#dlgDocumentos").dialog({
		autoOpen: false,
		height: 500,
		width: 750,
		modal: true,
		buttons: {
			"Aceptar": function() {
				var msg = "Esta seguro que desea " + ($('input[name=oper]:checked').val() == "S" ? "Autorizar" : "Rechazar") + " el tramite? Esta operacion no se puede deshacer."
				if (confirm(msg)){
					actualizaSICOP();
					$("#dlgDocumentos").dialog("close");
				}
			}
		},
		open: function() { },
		beforeClose: function() { }
	});

	$('#pagosPendientesTbl > tr').each(function() {
		alert(this);
	});
}

function creaTabla() {

	oTable = $('#pagosPendientesTbl').dataTable({
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bPaginate" : true,
		"bAutoWidth" : true,
		"bScrollCollapse" : true,
		"sScrollXInner": "100%", 
		"sScrollX": "100%",
		"sPaginationType" : "full_numbers",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide": true,
		"iDisplayLength": 25,
		"fnInitComplete": function() {    
			oTable.fnAdjustColumnSizing();
		},
		aaSorting: [[7, "desc"]],
		sAjaxSource: window.location.protocol + "//"
			+ window.location.host + "/"
			+ window.location.pathname.split("/")[1]
			+ "/crud?rt=t&ql=vPagosTiendaDigital",
		aoColumns: [{
			sName: "Folio"
		}, {
			sName: "CuentaPorPagar"
		}, {
			sName: "Beneficiario"
		}, {
			sName: "Concepto"
		}, {
			sName: "Monto"

		}, {
			sName: "Estatus"
		}, {
			sName: "c_folio",
			bVisible: false
		}],
		oLanguage: es_mx
	});

	$("#pagosPendientesTbl tbody").click(function(event) {
		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});

	$("#pagosPendientesTbl tbody").dblclick(function(evt) {

		var aPos = oTable.fnGetPosition(evt.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var arr = oTable.fnGetData()[currIndex];
		var folio = arr[6];
		folioPago = arr[0];
		showDoctos(folio);


	});

}

function showDoctos(folio) {
	$.blockUI();
	$("#doctosList").children().remove();

	$.ajax({
		url: '../fortimax/documents',
		dataType: 'json',
		type: "GET",
		data: {
			"accion": "get_doc_list",
			"folio": folio
		},
		async: true,
		success: function(objResp) {
			var nodos = objResp.nodos;
			for (var i in nodos) {
				nodo = nodos[i];
				$("#doctosList").append("<li><a style='color:black;' href=\"#\" onclick=\"muestraDocumento('" + nodo.fortimax + "')\">" + nodo.path + "</a></li>");
			}
			$.unblockUI();
			$("#dlgDocumentos").dialog("open");
		},
		error: function(xhr, textStatus, errorThrown) {
			try {
				var obj = eval(xhr.responseText);
				var msg = obj.errCause;
				alert("No fue posible consultar los documentos debido al error: " + msg);
			} catch (e) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
			}

			$.unblockUI();
		}
	});
}

function muestraDocumento(fortimaxNode) {
	openCenteredWindow("../filestore?select=" + fortimaxNode, "_blank", 800, 1024);
}

function openCenteredWindow(url, name, height, width, parms) {
	var left = Math.floor((screen.width - width) / 2);
	var top = Math.floor((screen.height - height) / 2);
	var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes,resizable=1";
	if (parms) {
		winParms += "," + parms;
	}
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) {
		win.window.focus();
	}
	return win;
}

