var oTable;
var oTable2;
var oTableEP;
var oTableSaldos;
var oTableLineas;
var oTableCAMBs2;
var oTableCAMBs;
var oTableCucop;
var tblPresupuesto;
var oDTPresupuesto;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	switch (tab){
		case 1://Consulta
			if($('#tblSolicitudesRequisicion >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 2://Carátula
			if($('#tblSolicitudesRequisicion >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 3://Líneas
			if($('#tblLineas >tbody >tr').length>0){
				oTableLineas.fnAdjustColumnSizing();
			}
			if($('#tblRequisicionMeses2 >tbody >tr').length>0){
				oTableCAMBs2.fnAdjustColumnSizing();
			}
			if($('#tblRequisicionMeses >tbody >tr').length>0){
				oTableCAMBs.fnAdjustColumnSizing();
			}
			if($('#tblCucop >tbody >tr').length>0){
				oTableCucop.fnAdjustColumnSizing();
			}
		break;
		case 4://Firmantes
			if($('#tblSolicitudesRequisicion >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 5://Presupuesto
			if($('#tblEP >tbody >tr').length>0){
				oTableEP.fnAdjustColumnSizing();
			}
			if($('#tblSaldos >tbody >tr').length>0){
				oTableSaldos.fnAdjustColumnSizing();
			}
			if($('#tblLineas >tbody >tr').length>0){
				oTableLineas.fnAdjustColumnSizing();
			}
		break;
		case 6://Apartado
			if($('#tblPresupuesto >tbody >tr').length>0){
				oDTPresupuesto.fnAdjustColumnSizing();
			}
		break;
		
	}
}
function fnGetSelected( oTableLocal ){
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	for ( var i=0 ; i<aTrs.length ; i++ ){
		if ( $(aTrs[i]).hasClass('row_selected') ){
			aReturn.push( aTrs[i] );
		}
	}
	return aReturn;
}
function windowStatus( texto ){
	window.status=texto
}
function showHideTabs(){
	$( "#CaratulaRequisiciones" ).hide();
	$( "#LineasRequisiciones" ).hide();
	$( "#FirmantesRequisiciones" ).hide();
	$( "#PresupuestoRequisiciones" ).hide();
	$( "#ApartadoRequisiciones" ).hide();
	if (parseInt($("#tbs").val())  >1 ) {
		$( "#CaratulaRequisiciones" ).show();
		$( "#LineasRequisiciones" ).show();
		$( "#FirmantesRequisiciones" ).show();
		$( "#PresupuestoRequisiciones" ).show();
		$( "#ApartadoRequisiciones" ).show();
	}
	var apartables = [ "RC", "RM", "RS","RT" ]; //Agregar está linea para activar el apartado
	if ($.inArray(cIdTipo, apartables) == -1){
		$( "#PresupuestoRequisiciones" ).hide();
		$( "#ApartadoRequisiciones" ).hide();
	}
}
function openLayout(){
	window.open("../../servlet/ReportesGRM?"
		+"cIdSolicitud="+$("#cIdSolicitud").val()
		+"&reporteNombre="+$("#reporteNombre").val()
		+"&nTipoReporte="+$("#nTipoReporte").val()
		+"&operacion="+$("#operacion").val()
		,'Procesando', 'status=1, width=400px, height=200px, left=150px');	
}
function ejecutaAjax(){
	var hayDocumento=false;
	var data = new FormData();
	jQuery.each(jQuery('#uploadLayoutFile')[0].files, function(i, file) {
	    data.append('file-'+i, file);
	    hayDocumento=true;
	});
	if(!hayDocumento){
		swal("Favor de seleccionar un archivo.",{icon:"info",button: "Cerrar"});
		return;
	}
	data.append('operacion', 7);
	data.append('cIdSolicitud', $("#cIdSolicitud").val());
	data.append('namePlantilla', $("#uploadLayoutFile").val());
	$.blockUI({message: "Procesando espere ......"});
	$("#observaciones").val('');
	jQuery.ajax({
	    url: '../../servlet/LeeArchivos',
	    data: data,
	    cache: false,
	    contentType: false,
	    dataType: "json",
	    processData: false,
	    method: 'POST',
	    type: 'POST', // For jQuery < 1.9
	    success: function(j){
			$("#observaciones").val(j[0].MSG);
	    	$.unblockUI();
	    },
	    error: function(j){
	    	$("#observaciones").val(j[0].MSG);
	    	$.unblockUI();
	    }
	});
}
