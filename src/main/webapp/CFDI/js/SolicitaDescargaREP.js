var oTableRequest;

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


/**
 * 
 */

function init() {
	var fFin = new Date();
	$("#btnSolicitar").button().click(function() {
		enviaSolicitud();
	});
	
	$("#fInicio").val("01/01/" + fFin.getFullYear());

	$("#fFin").val((fFin.getDate() < 10 ? "0" : "") + fFin.getDate()
		+ "/"
		+ ((fFin.getMonth() + 1) < 10 ? "0" : "") + (fFin.getMonth() + 1)
		+ "/"
		+ fFin.getFullYear()
	);

	$(".fecha").each(function() {
		$(this).datepicker({
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true,
			changeMonth : true,
			changeYear : true,
			dateFormat: "dd/mm/yy"
		});
	});

	$("#bitacoraDiv").dialog({
		autoOpen : false,
		height : 280,
		width : 650,
		modal : true
	});

	if (msg != "")
		$("#bitacoraDiv").dialog("open");
	creaTabla();
}

function enviaSolicitud() {
	$.blockUI({
		message : "Registrando solicitud. Por favor espere ......"
	});

	if (capturaCorrecta()) {
		$.blockUI();
		$("#frmReqCFDI").submit();
	/*
	$.ajax({
		url : '../cfdi/RegistraDescarga',
		dataType : 'json',
		type : "POST",
		data : {
			"fechaInicio" : $("#fInicio").val(),
			"fechaFin" : $("#fFin").val()
		},
		async : true,
		success : function(objResp) {
			var peticionCorrecta = "true" == objResp.success;
			if (peticionCorrecta) {
				var resultadoSol = objResp.data_1.result;
			} else {
				alert("Ocurrio el siguiente problema al intentar registrar la solicitud en SAT:\n" + objResp.data_1.result);
			}
			$.unblockUI();
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			$.unblockUI();
		}
	});
	*/
	} else {
		$.unblockUI({
			message : "Registrando solicitud. Por favor espere ......"
		});
	}
}

function capturaCorrecta() {
	if ($("#fInicio").val() == "" || $("#fFin").val() == "") {
		alert("Debe proporcionar las fechas de inicio y fin")
		return false;
	} else if ($("#pwdKey").val() == "") {
		alert("Debe capturar la contraseña de su firma electronica");
		return false;
	} else {
		var arrFechaI = $("#fInicio").val().split("/");
		var arrFechaF = $("#fFin").val().split("/");
		var fInicio = new Date(arrFechaI[2], arrFechaI[1], arrFechaI[0]);
		var fFin = new Date(arrFechaF[2], arrFechaF[1], arrFechaF[0]);

		if (fFin < fInicio) {
			alert("La fecha final debe ser mayor o igual a la fecha inicial");
			return false;
		}
	}
	return true;
}

function descargaArchivo(uuid) {
	var url = "../cfdi/DescargaArchivo?uuid=" + uuid;
	window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=300, height=200");
}

function creaTabla() {
	oTableRequest = $("#dt_solicitudes").dataTable({
		bPaginate : false,
		bFilter : false,
		bInfo : false,
		sScrollX : "100%",
		sScrollY : "250",
		bJQueryUI : true,
		bDestroy : true,
		bServerSide : true,
		fnServerData : function(sSource, aoData, fnCallback) {
			$.ajax({
				"dataType" : 'json',
				"type" : "POST",
				"url" : sSource,
				"data" : aoData,
				"success" : fnCallback
			});
		},
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRequestSAT&qw=" + encodeURI("1=1"),
		aoColumns : [
			{
				sName : "UUID",
				bVisible : true
			},
			{
				sName : "fechaRealizacion",
				bSearchable : true
			},
			{
				sName : "fechaInicioConsulta",
				bSearchable : true
			},
			{
				sName : "fechaFinConsulta",
				bSearchable : true
			},
			{
				sName : "RFCReceptor",
				bSearchable : true
			},
			{
				sName : "cEventoDesc",
				bSearchable : true
			},
			{
				sName : "cStatus",
				bSearchable : true
			},
			{
				sName : "nTotalCFDIs",
				bSearchable : true
			},
			{
				sName : "cmd",
				bSearchable : false,
				bSortable : false
			}
		]
	}) ;
}