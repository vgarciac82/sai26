var oTablePorGenerar;
var es_mx = {
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
function  initTabla(){
	oTablePorGenerar= $("#dtPorGenerar").dataTable({
		bPaginate: true,
			bLengthChange: true,
			bFilter: true,
			bSort: false,
			bInfo: false,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Buscar:",
			oPaginate: {
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		aaSorting: [[ 0, "asc" ]]
	});
}
function searchDatos(){
	if(parseInt($("#nPestana").val(),10)==1){
		searchApartados();
	}else if(parseInt($("#nPestana").val(),10)==3){
		initTablaEnviados();
	}else if(parseInt($("#nPestana").val(),10)==4){
		initTablaAutorizados();
	}
}
function searchApartados(){
	var cUR = $("#cboUnidadEjecutora").val();
	var where="nEnviadoSICOP=0  ";
	if (cUR != "*" && cUR != "**"){
		where += " AND cUnidadEjecutora = '" + cUR + "'";
	}else{
		if(cUR == "*"){
			where +=" AND cUnidadEjecutora not  LIKE '%25G%25'";
		}else{
			where +=" AND cUnidadEjecutora  LIKE '%25G%25'";
		}
	}
	oTableConsulta= $('#dtPorGenerar').dataTable({
		bRetrive: true,
		bPaginate: true,
		bDestroy: true,
		bLengthChange: true,
		bFilter: true,
		bSort: true,
		bInfo: false,
		bAutoWidth: true,
		oLanguage: es_mx,
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mListaApartadosLayout&qw="+where,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		"aLengthMenu": [
				            [10, 25, 50, 100,  -1],
				            [10, 25, 50, 100,  "Todo"]
				        ], 
		"iDisplayLength" : 10,
		aoColumns: [
			
			{ sName: "nIdIntegraRequi",bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "folioIntegracion",		bSearchable: false, bSortable: false, bVisible: true},
			{ sName: "integracion",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "FoliosApartado",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "Requis",			bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "tipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "estatus",	bSearchable: true, bSortable: false, bVisible: true}
			
		]
	});
}
function initTablaProcesoEnvio(folios){
	var cUR = $("#cboUnidadEjecutora").val();
	var where="nEnviadoSICOP=0 ";
	where +=" AND nIdIntegraRequi in("+folios+")";
	var oTableLocal = $('#dtProcesoEnvio').dataTable({
		bRetrive: true,
		bPaginate: true,
		bDestroy: true,
		bLengthChange: true,
		bFilter: true,
		bSort: true,
		bInfo: false,
		bAutoWidth: true,
		oLanguage: es_mx,
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mListaApartadosLayout&qw="+where,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		"aLengthMenu": [
				            [10, 25, 50, 100,  -1],
				            [10, 25, 50, 100,  "Todo"]
				        ], 
		"iDisplayLength" : 10,
		aoColumns: [
			{ sName: "nIdIntegraRequi",bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "integracion",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "FoliosApartado",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "Requis",			bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "tipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "estatus",	bSearchable: true, bSortable: false, bVisible: true}
			
		]
	});
}
function initTablaEnviados(){
	var cUR = $("#cboUnidadEjecutora").val();
	var where="1=1 ";
	if (cUR != "*" && cUR != "**"){
		where += " AND cUnidadEjecutora = '" + cUR + "'";
	}else{
		if(cUR == "*"){
			where +=" AND cUnidadEjecutora not  LIKE '%25G%25'";
		}else{
			where +=" AND cUnidadEjecutora  LIKE '%25G%25'";
		}
	}
	where +=" AND nEnviadoSICOP=1";
	var oTableLocal = $('#dtEnviados').dataTable({
		bRetrive: true,
		bPaginate: true,
		bDestroy: true,
		bLengthChange: true,
		bFilter: true,
		bSort: true,
		bInfo: false,
		bAutoWidth: true,
		oLanguage: es_mx,
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mListaApartadosLayout&qw="+where,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		"aLengthMenu": [
				            [10, 25, 50, 100,  -1],
				            [10, 25, 50, 100,  "Todo"]
				        ], 
		"iDisplayLength" : 10,
		aoColumns: [
			{ sName: "nIdIntegraRequi",bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "folioIntegracion",		bSearchable: false, bSortable: false, bVisible: true},
			{ sName: "integracion",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "FoliosApartado",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "Requis",			bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "tipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "estatus",	bSearchable: true, bSortable: false, bVisible: true}
			
		]
	});
}
function initTablaAutorizados(){
	var cUR = $("#cboUnidadEjecutora").val();
	var where="1=1 ";
	if (cUR != "*" && cUR != "**"){
		where += " AND cUnidadEjecutora = '" + cUR + "'";
	}else{
		if(cUR == "*"){
			where +=" AND cUnidadEjecutora not  LIKE '%25G%25'";
		}else{
			where +=" AND cUnidadEjecutora  LIKE '%25G%25'";
		}
	}
	where +=" AND nEnviadoSICOP=2";
	var oTableLocal = $('#dtAutorizados').dataTable({
		bRetrive: true,
		bPaginate: true,
		bDestroy: true,
		bLengthChange: true,
		bFilter: true,
		bSort: true,
		bInfo: false,
		bAutoWidth: true,
		oLanguage: es_mx,
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mListaApartadosLayout&qw="+where,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		"aLengthMenu": [
				            [10, 25, 50, 100,  -1],
				            [10, 25, 50, 100,  "Todo"]
				        ], 
		"iDisplayLength" : 10,
		aoColumns: [
			{ sName: "integracion",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "FoliosApartado",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "Requis",			bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "tipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
			{ sName: "estatus",	bSearchable: true, bSortable: false, bVisible: true}
			
		]
	});
}
/*Para obtener los folios seleccionados de la tabla
 * 
 * */
function getFolios(id){
	//dtPorGenerar
	var folios=""
	var token="";
	$('#'+id+' tbody tr input:checked').each(function(idx, elm){
		folios=folios+token+$(this).parent('td').parent('tr').find('td:eq(0)').html();
		token=",";
	});
	return folios;
}

function generaLayout(){
	var folios=$("#cFolios").val();
	if(folios!=""){
		$.blockUI({message: "Procesando espere ......"});
		$("#btnGeneraLayout").hide();
		window.open(
				"../../GeneraLayoutServlet?"
				+ "nTipoLayout=1"
				+ "&cFolios=" + folios
				,
				"Procesando", "status=1, width=500px, height=100px, left=150px");
		
		$.unblockUI();
		$("#cFolios").val('-1');
		setTimeout(function(){ window.location = "LayoutRequisicion.jsp?tab=" + 0; }, 1000);
		
	} 
}
function iniciaEstatus(){
	var folios= getFolios("dtEnviados");
	if(folios!=""){
		$.blockUI({message: "Procesando espere ......"});
		$.ajax({
			url : "../../GeneraLayoutServlet",
			type : 'post',
			async : false,
			data : 'nTipoLayout=2'+"&cFolios="+folios
			,
			dataType : 'json',
			success : function(j) {
				alert(j[0].MENSAJE);
				initTablaEnviados();
				$.unblockUI();
			}, error: function( jqXHR, textStatus, errorThrown ) {
				$.unblockUI();
			}
		});
	} 
}

