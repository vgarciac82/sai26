/**
 * 
 */
var oTableFirmas;

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
			dateFormat : "dd/mm/yy",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true,
			changeMonth : true,
			changeYear : true
		});
	});

	creaTabla();

	$("#filtrar").button().click(function() {
		filtrarInformacion();
	});
}

function filtrarInformacion() {
	creaTabla();
}

function creaTabla() {
	var token = "";
	var condOperacion = "";
	var condUsuario = "";
	
	$("#fInicio").val($("#fIni").val().split('-').reverse().join('/'));
	$("#fFin").val($("#fF").val().split('-').reverse().join('/'));
	
	$("input[name='tramiteSel']").each(function() {
		
		if ( $(this).attr("checked")) {
			condOperacion = condOperacion + token + "'" + $(this).attr("id") + "'";
			token = ",";
		}
	});

	if (condOperacion == ""){
		alert("Debe seleccionar al menos una operacion para filtrar.");
		return;
	}else{
		condOperacion = " AND cOperacion IN(" + condOperacion + ")";
	}
	
	if($.trim( $("#firmanteLogin").val() ) != "" )
		condUsuario = " AND U_NOMBRE LIKE '%" + $("#firmanteLogin").val() + "%'";
	
	var finicio = $("#fInicio").val();
	var fFIn = $("#fFin").val();
	var condFecha = "CONVERT(DATE, dFechaOperacion, 103) BETWEEN CONVERT(DATE, '" + finicio + "', 103) AND CONVERT(DATE, '" + fFIn + "', 103)";

	var cond = condFecha  + " " + condOperacion + " " + condUsuario;
	oTableFirmas = $("#dt_solicitudes").dataTable({
		bAutoWidth:true,
		bSort: true,
		bPaginate : true,
		bLengthChange: true,
		sPaginationType: "full_numbers",
		bFilter : true,
		bInfo : true,
		sScrollX : "100%",
		sScrollY : "250",
		bJQueryUI : true,
		bDestroy : true,
		bServerSide : true,
		fnInitComplete: function() {   
						oTableFirmas.fnAdjustColumnSizing();
	    			},
		fnServerData : function(sSource, aoData, fnCallback) {
			$.ajax({
				"dataType" : 'json',
				"type" : "POST",
				"url" : sSource,
				"data" : aoData,
				"success" : fnCallback
			});
		},
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vBitacoraFirma&qw=" + encodeURI(cond),
		aoColumns : [
			{
				sName : "U_NOMBRE",
				bVisible : true
			},
			{
				sName : "cTipoDocumento",
				bSearchable : true
			},
			{
				sName : "nFolioDocumento",
				bSearchable : true
			},
			{
				sName : "cOperacion",
				bSearchable : true
			},
			{
				sName : "dFechaOperacion",
				bSearchable : true
			},
			{
				sName : "cMotivo",
				bSearchable : false
			}
		],
		"order" : [ [ 4, "desc" ] ]
	}) ;
}