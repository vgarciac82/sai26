var language = {
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

function cargaTransporte() {
	querySelectPost("tTransporteRead", "tipoTransporte", {
		async : false
	});
	$("#tipo").val(document.getElementById("tipoTransporte").value);


}

function cambiaTransporte() {
	if ($("#tipoTransporte").val() == 1) {
		$("#dlgDetalle").css('display', "none");
		$("#dlgKm").css("display", "none");
		document.getElementById("Origen").value = "CASA - AEROPUERTO, AEROPUERTO - CASA";
	} else if ($("#tipoTransporte").val() == 2) {
		$("#dlgDetalle").css('display', "none");
		$("#dlgKm").css('display', "none");
		document.getElementById("Origen").value = "";
	} else if ($("#tipoTransporte").val() == 3) {
		$("#dlgDetalle").css('display', "none");
		$("#dlgKm").css('display', "block");
		document.getElementById("Origen").value = "";
	} else if ($("#tipoTransporte").val() == 4) {
		$("#dlgDetalle").css('display', "block");
		$("#dlgKm").css('display', "block");
		document.getElementById("Origen").value = "";
	}
}

function iniciaTransporte() {
	cargaTransporte();
	creaTransporteDT();

	cargaResumenViaticos();	
}

var oTable;
function creaTransporteDT() {
	var cWhere = "nFolioSolicitudViaticos = " + $("#nFolioSolicitudViaticos").val();
	oTable = $('#dtTransporteDet').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 270,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSolicitudViaticosTransporte&qw=" + " " + encodeURI( cWhere),
			aoColumns : [
				{
					sName : "nFolioSolicitudViaticos",
					bVisible : false,
					sClass : "alignLeft"	
				},{
					sName : "cDescripcion",
					bVisible : true,
					sClass : "alignLeft"
				},{
					sName : "cOrigen",
					bVisible : true,
					sClass : "alignLeft"
				},{
					sName : "mMontoT",
					bVisible : true,
					sClass : "rightCls"
				},{
					sName : "descartar",
					sClass : "centerCls"
				}],
			oLanguage : language
		});

}

function eliminar(tipoTrans) {
	
	$("#tipo").val("0");
	if (confirm("Esta seguro de borrar el renglon?")) {
		$("#tipo").val(tipoTrans);
		queryFormPost("solicitudViaticosTransporteDelete", {async:false});
		creaTransporteDT();
		
	}	
}


function validaDatos() {
	var mMonto = Number(quitaFmt($("input[id='monto']").val()));

	if (isEmpty("km") || $("#km").val() == null)
		$("#km").val(0);

	if (esValorCero("tipoTransporte")) {
		alert("Favor de seleccionar el tipo de transporte");
		return false;

	} else if (isEmpty("Origen")) {
		alert("Favor de capturar el origen y el destino del transporte")
		return false;

	} else if (isEmpty("monto")) {
		alert("Favor de capturar el monto del transporte")
		return false;

	} else if (mMonto == 0) {
		alert('El campo Monto debe ser num&eacute;rico');
		return false;
	} else
		return true;


}

function guardar() {
	if (validaDatos()) {
		var viaticante = transporteToJSON();
		$.ajax({
			url : '../viaticos/CreaViaticoTransporte',
			type : 'post',
			dataType : 'json',
			async : false,
			data : viaticante,
			success : function(data) {
				var exito = (data.success == "true");
				if (exito) {
					alert("Transporte guardado exitosamente");
				} else {
					alert("No fue posible registrar la informacion debido al siguiente error:\n" + data.data_1.result);
				}
			}
		});

	} else {
		alert("No se guardo el transporte, ocurrio un error.");
	}

	creaTransporteDT();
	cargaResumenViaticos();
}

function transporteToJSON() {
	var objViaticante = {
		"folioViatico" : $("#nFolioSolicitudViaticos").val(),
		"tipoTransporte" : $("#tipoTransporte").val(),
		"origen" : $("#Origen").val(),
		"monto" : $("#monto").val(),
		"noKm" : $("#km").val(),
		"placas" : $("#placas").val(),
		"tieneVales" : ($("#gasolina").attr("checked") ? "1" : "0")
	};

	return objViaticante;
}

function cargaResumenViaticos(){
	
	queryFormPost("resumenDiasSolicitudViaticos_Read", {async:false});
	
	var mTotal = Number($("#mImporteAgenda").val()) + Number($("#mTransporte").val());	
	mTotal = mTotal.toFixed(2);
	$("#mTOTAL").val(mTotal);
	$("#mImporteAgenda").formatCurrency();
	$("#mTransporte").formatCurrency();
	$("#mTOTAL").formatCurrency();
		
	var nTotalDias = Number($("#nDNacional").val()) + Number($("#nDInternacional").val()) + Number($("#nDPendientes").val());
	nTotalDias = nTotalDias.toFixed(1);
	$("#nDTotal").val(nTotalDias);
}