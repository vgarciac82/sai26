/**
 * 
 */

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

function creaTablas(){
	readMontosCnt();
	readMontosCFDI();
	creaTablaAdjuntar();
	creaTablaCFDIsCnt();
	creaTablaCFDIsPago();
}
function init(){
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	creaTablas();
	$("#msgDialog").dialog({
		autoOpen : false,
		height : 300,
		width : 450,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				$("#msgDialog").dialog("close");
				$("#cargaRepFrame").attr("src","about:blank");
			}
		}
	});
	
	$("#dlgCargar").dialog({
		autoOpen : false,
		height : 380,
		width : 660,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				$("#dlgCargar").dialog("close");
			}
		}
	});
	
	readMontosCnt();
	readMontosCFDI();
}

var oTablePendientes;
var oTableCFDIs;
var oRecibosPago;

function cargaPagosContrato(){
	creaTablas();
} 

function creaTablaAdjuntar() {
	var condition = "";
	condition = "cIdTipoContrato='" + $("#tipoContrato").val() + "'";
	condition += " AND cIdContrado ='" + $.trim( $("#IDContrato").val() ) + "'";
	
	oTablePendientes = $("#dtAdjuntarREP").dataTable({
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bPaginate" : true,
		"bAutoWidth" : true,
		"bScrollCollapse" : true, 
		"sScrollX": "100%",
		"sPaginationType" : "full_numbers",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide": true,
		"iDisplayLength": 10,
		"fnInitComplete": function() {    
			oTablePendientes.fnAdjustColumnSizing();
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
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_DT_AdjuntaREPContrato&qw=" + encodeURI(condition),
		aoColumns : [
			{sName : "caNoContrarrecibo" ,bSortable:false},
			{sName : "nFolioPago"},
			{sName : "mImporteTotal"},
			{sName : "CMD"} 
		],
		"order" : [ [ 0, "desc" ] ]
	}) ;
}


function creaTablaCFDIsPago() {
	var condition = "cIDContrato ='" + $.trim( $("#IDContrato").val() ) + "'";
	
	oRecibosPago = $("#dt_RecibosPago").dataTable({
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bPaginate" : true,
		"bAutoWidth" : true,
		"bScrollCollapse" : true, 
		"sScrollX": "100%",
		"sPaginationType" : "full_numbers",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide": true,
		"iDisplayLength": 10,
		"fnInitComplete": function() {    
			oRecibosPago.fnAdjustColumnSizing();
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
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_dt_PagoReciboCnt&qw=" + encodeURI(condition),
		aoColumns : [
			{sName : "canocontrarrecibo" ,bSortable:false},
			{sName : "uuid_cfdi"},
			{sName : "uuid_rep"},
			{sName : "cParcialidad"},
			{sName : "mImporteNeto"},
			{sName : "mMontoTotal"},
			{sName : "mimpsaldoant"},
			{sName : "mimppagado"},
			{sName : "mimpsaldoinsoluto"}  
		]
	}) ;
}
function creaTablaCFDIsCnt() {
	var condition = "cIDContrato ='" + $.trim( $("#IDContrato").val() ) + "'";
	
	oTableCFDIs = $("#dtInfoContratoCFDI").dataTable({
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bPaginate" : true,
		"bAutoWidth" : true,
		"bScrollCollapse" : true, 
		"sScrollX": "100%",
		"sPaginationType" : "full_numbers",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide": true,
		"iDisplayLength": 10,
		"fnInitComplete": function() {    
			oTableCFDIs.fnAdjustColumnSizing();
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
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_dt_ContratoFactura&qw=" + encodeURI(condition),
		aoColumns : [
			{sName : "cFactura" ,bSortable:false},
			{sName : "cRFCFactura"},
			{sName : "importeBruto"},
			{sName : "mimporteIVA"},
			{sName : "mOtrosImpuestos"},
			{sName : "mImporteConImpuestos"}  
		],
		"order" : [ [ 1, "desc" ] ]
	}) ;
}



function adjuntaArchivo(tipoPago, folioPago){
	$("#cargaRepFrame").attr("src","../procesos/adjuntaREPPago.jsp?tipoContrato="+$("#tipoContrato").val()+"&tipoPago="+tipoPago+"&folioPago="+folioPago );
	$("#dlgCargar").dialog("open");
} 

function beforeSend(){
	$.blockUI({message:"Procesando por favor espere. No cierre esta pagina."});
}

function cargaTerminada(msg){
	$.unblockUI();
	creaTablas();
	$("#msgTxt").val(msg);
	$("#dlgCargar").dialog("close");
	$("#msgDialog").dialog("open");
}

function readMontosCnt(){
	$("#MontoTotalContrato").val();
	
	queryFormPost({
		queryName:"rep_TotalContrato",
		async:true,
		callback: function(){
			var valTotal = $("#MontoTotalContrato").val();
			if( valTotal == "")
				$("#totalContrato").text("0.00");
			else
				$("#totalContrato").text( $("#MontoTotalContrato").val() );
		}
	});
}

function readMontosCFDI(){
	$("#MontoTotalCFDIs").val();
	
	queryFormPost({
		
		queryName:"rep_TotalCFDI",
		async:true,
		callback: function(){
			
			var valTotal = $("#MontoTotalCFDIs").val();
			if( valTotal == "")
				$("#totalCFDIS").text("0.00");
			else
				$("#totalCFDIS").text( $("#MontoTotalCFDIs").val() );
				
		}
	});
}

