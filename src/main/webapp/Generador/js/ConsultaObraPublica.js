var dtEP;
var dtPA;
var dtRet;
var dtPAG;
/**
 * Lenguaje de las DataTables.
 */
var mx_esp = {
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
	sSearch : "Buscar:"
};

function init() {
	$(".tabs").tabs();
	dtEP = $("#dt_total").dataTable({
		bPaginate : false,
		bLengthChange : false,
		bFilter : false,
		bInfo : false,
		oLanguage : mx_esp,
		oPaginate : {
			"sFirst" : "Primero",
			"sPrevious" : "Ant.",
			"sNext" : "Sigte.",
			"sLast" : "&Uacute;ltimo"
		},
		bScrollCollapse : true,
		bJQueryUI : true,
		bDestroy : true,
		aoColumns : [ {
			sName : "nMes",
			bSortable : false
		}, {
			sName : "EP",
			bSortable : false
		}, {
			sName : "Total",
			bSortable : false
		} ]
	});

	dtPA = $("#pluriAnualTbl").dataTable({
		bPaginate : false,
		bLengthChange : false,
		bFilter : false,
		bInfo : false,
		oLanguage : mx_esp,
		oPaginate : {
			"sFirst" : "Primero",
			"sPrevious" : "Ant.",
			"sNext" : "Sigte.",
			"sLast" : "&Uacute;ltimo"
		},
		bScrollCollapse : true,
		bJQueryUI : true,
		bDestroy : true
	});

	dtRet = $('#dt_retencion').dataTable({
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true
	});

	dtConv = $("#dt_convenio").dataTable({
		bPaginate : false,
		bLengthChange : false,
		bFilter : false,
		bInfo : false,
		oLanguage : mx_esp,
		oPaginate : {
			"sFirst" : "Primero",
			"sPrevious" : "Ant.",
			"sNext" : "Sigte.",
			"sLast" : "&Uacute;ltimo"
		},
		bScrollCollapse : true,
		bJQueryUI : true,
		bDestroy : true
	});
	dtPAG = $("#dt_pagos").dataTable({
		bPaginate : false,
		bLengthChange : false,
		bFilter : false,
		bInfo : false,
		oLanguage : mx_esp,
		oPaginate : {
			"sFirst" : "Primero",
			"sPrevious" : "Ant.",
			"sNext" : "Sigte.",
			"sLast" : "&Uacute;ltimo"
		},
		bScrollCollapse : true,
		bJQueryUI : true,
		bDestroy : true
	});
	queryFormPost({
		queryName : "resultadoConsultaOPRead",
		async : false,
		callback : function() {
			queryFormPost({
				queryName : "readObraPublicaFolios",
				async : false,
				callback : function() {
					if ($("#nFolioOPComHeader").val() != '' )
						cargaDetalleCompromiso();
					else if ($("#nFolioOPPreComHeader").val()!='')
						cargaDetallePreCompromiso();
					else
						cargaDetalleApartadoCompromiso();
				}
			});
		}
	});
}
function cargaDetallePreCompromiso() {
	var condition = " nFolioOPPreComHeader = '" + $("#nFolioOPPreComHeader").val()
			+ "'";
	var tableName = "RESULTADO_CONSULTA_OP_P_DETAIL";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableName,
		Param : condition,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEP.fnAddData([ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});
}
function cargaDetalleCompromiso() {
	var condition = " nFolioOPComHeader = '" + $("#nFolioOPComHeader").val()
			+ "'";
	var tableName = "RESULTADO_CONSULTA_OP_C_DETAIL";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableName,
		Param : condition,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEP.fnAddData([ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});
}

function cargaDetalleApartadoCompromiso() {
	var condition = " nFolioOPAHeader = '" + $("#nFolioOPAHeader").val()
			+ "'";
	var tableName = "RESULTADO_CONSULTA_OP_A_DETAIL";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableName,
		Param : condition,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEP.fnAddData([ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});
}

function loadPlurianualInfo() {
	var condition = " nocontrato = '" + $("#NoContrato").val() + "'";
	var tableName = "BUCAR_CONTRATOS_OP_PLURIANUAL";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableName,
		Param : condition,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		dtPA.fnClearTable();
		var totCnt = 0;
		var totMnt = 0;
		for ( var i = 0; i < data.length; i++) {
			totMnt += parseFloat(data[i].Col2);
			dtPA.fnAddData([ data[i].Col0, data[i].Col1, '$' + data[i].Col2 ]);
			totCnt++;
		}
		dtPA.fnAddData([ 'Total: ', totCnt, "$" + totMnt ]);
	});
}

function loadRetenciones() {
	var tableNameReten = 'COMPROMISO_O_PUB_RETENCIONES_DETAIL';
	var conditionReten = " a.cidcontrato = '" + $("#NoContrato").val() + "'";
	dtRet.fnClearTable();
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameReten,
		Param : conditionReten,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			$('#dt_retencion').dataTable().fnAddData(
					[ data[i].Col0, data[i].Col1 ]);
		}
	});
}

function loadConvenios() {
	var tableNameReten = 'CONV_MOD_CONSULTA';
	var conditionReten = " ccvecontrato = '" + $("#NoContrato").val() + "'";
	dtRet.fnClearTable();
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameReten,
		Param : conditionReten,
		MaxReg : "",
		ajax : 'false'
	},
	function(data) {
		$('#dt_convenio').dataTable().fnClearTable();
		for ( var i = 0; i < data.length; i++) {
			$('#dt_convenio').dataTable().fnAddData(
					[ data[i].Col0, data[i].Col1, data[i].Col2,
							data[i].Col3 ]);
		}
	});
}

function loadPagos() {
	var tableNameReten = 'PAGOS_CONSULTA_OP';
	var conditionReten = " cfoliocontratoobra = '" + $("#NoContrato").val()
			+ "'";
	dtRet.fnClearTable();
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameReten,
		Param : conditionReten,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			$('#dt_pagos').dataTable().fnAddData(
					[ data[i].Col0, data[i].Col1, data[i].Col2, data[i].Col3,
							data[i].Col5, data[i].Col4 ]);
		}
	});
}