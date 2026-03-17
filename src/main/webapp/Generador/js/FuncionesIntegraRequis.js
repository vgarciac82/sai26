var oTableConsulta;
var oTablePresel;
var oTableSel;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	switch (tab){
		case 1://Consulta
			if($('#tblConsulta >tbody >tr').length>0){
				oTableConsulta.fnAdjustColumnSizing();
			}
		break;
		case 2://Preselección
			if($('#tblSolicitudDispPreSel >tbody >tr').length>0){
				oTablePresel.fnAdjustColumnSizing();
			}
			if($('#tblSolicitudPreSel >tbody >tr').length>0){
				oTableSel.fnAdjustColumnSizing();
			}
		break;
		
	}
}
function showAndHideTabs(){
	$( "#PreseleccionaRequis" ).show();
	if(parseInt($("#tbs").val())!=2){
		$( "#PreseleccionaRequis" ).hide();	
	}
}
function cambiaCentrocontableUsuario(){
	if($("#cIdUnidadEjecutora").val()!='0'){
		$.ajax({
			url: '../../servlet/CambiaPropiedadesUsuario',
			dataType: 'json',
			data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
			async : false,
			success : function(j) {
				if(j[0].error){
					swal("No se hizo el cambio de centro contable y unidad ejecutora",{icon:"info",button: "Cerrar"});
				}else{
					$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora); 
				}
			}
		});
	}
}
function textCounter( field, maxlimit ) {
	if ( field.value.length > maxlimit )
		field.value = field.value.substring( 0, maxlimit );
}
function guardaIntegraRequis(){
	if($("#cDescripcion").val()==""){
		swal("La descrici\u00f3n es un dato requerido.",{icon:"info",button: "Cerrar"});
	}else{
		queryFormPost("ConsecutivoIntegraRequisRead",{async : false,
			callback : function() 
			{
				queryFormPost("AgrupaRequisCreate",{async : false,
					callback : function() 
					{
						window.location = "IntegraRequis.jsp?tab=2&cEjercicio=" + $("#cEjercicio").val() + "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val() 
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&nTipoIntegracion=" + $("#nTipoIntegracion").val();
					}
				});
			}
		});	
		
	}	
}
function  initTabla(){
	oTableConsulta= $("#tblConsulta").dataTable({
		bPaginate: true,
		bLengthChange: true,
		bFilter: true,
		bSort: false,
		bInfo: false,
		sScrollX: "100%",
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
function buscarIntegraRequis(){
	var qw="1=1 and cUnidadEjecutora='"+$("#cIdUnidadEjecutora").val()+"'";
	if($("#nTipoIntegracion").val()!=0){
		qw=qw+" and nIdTipoIntegracion='"+$("#nTipoIntegracion").val()+"'";
	}
	if($("#cDescripcion").val()!=''){
		qw=qw+" and cDescripcion LIKE '%25"+$("#cDescripcion").val()+"%25'";
	}
	oTableConsulta = $("#tblConsulta").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sScrollX: "100%",
		sPaginationType: "full_numbers",
		oLanguage: {
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
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mIntegraRequis&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 2, "asc" ],[3, "asc"]] ,
		aoColumns: [
			{sName: "cVinculo",bVisible: false},
			{sName: "nIdIntegraRequi",bVisible: false},
			{sName: "nEstatus",bVisible: false},
			{sName: "nEnviadoSICOP",bVisible: false},
			{sName: "nIdTipoIntegracion",bVisible: false},
			{sName: "cTipoIntegracion"},
			{sName: "cUnidadEjecutora"},
			{sName: "nConsecutivo"},
			{sName: "cDescripcion"},
			{sName: "estatusIntegracion"},
			{sName: "estatusSICOP"},
			{sName: "cFolioSICOP"}
		]
	});
}
function  initTablaRequis(){
	oTablePresel=$("#tblSolicitudDispPreSel").dataTable({
		bPaginate: true,
		bLengthChange: true,
		bFilter: true,
		bSort: false,
		bInfo: false,
		sScrollX: "100%",
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
	$("#tblSolicitudPreSel").dataTable({
		bPaginate: true,
		bLengthChange: true,
		bFilter: true,
		bSort: false,
		bInfo: false,
		sScrollX: "100%",
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
function  showTblRequis(){
	var qw=" cIdTipoSolicitud='"+$("#cIdTipoSolicitud").val()+"' ";
	if($("#cCapitulo").val()!=0){
		qw=qw+" and SUBSTRING(cIdSubPartida,1,1)='"+$("#cCapitulo").val()+"'";
	}
	if($("#cUnidadEjecutoraRMC").val()!=''){
		qw=qw+" and cIdUnidadEjecutora ='"+$("#cUnidadEjecutoraRMC").val()+"'";
	}
	
	oTablePresel=$("#tblSolicitudDispPreSel").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sScrollX: "100%",
		sPaginationType: "full_numbers",
		"fnInitComplete": function() {
			oTablePresel.fnAdjustColumnSizing();
		},
		oLanguage: {
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
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRequisSinIntegrar&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ],[1, "asc"]] ,
		aoColumns: [
			
			{sName: "cIdSolicitud"},
			{sName: "cIdSubPartida"},
			{sName: "cDescripcion"},
			{sName: "nFolioApartado",bVisible: false}
			
		]
	});
}
function  showTblRequisIntegradas(){
	var qw=" nIdIntegraRequi="+$("#nIdIntegraRequi").val();
	
	oTableSel=$("#tblSolicitudPreSel").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sScrollX: "100%",
		sPaginationType: "full_numbers",
		oLanguage: {
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
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRequisIntegradas&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ],[1, "asc"]] ,
		aoColumns: [
			
			{sName: "cIdSolicitud"},
			{sName: "cIdSubPartida"},
			{sName: "cDescripcion"},
			{sName: "nFolioApartado",bVisible: false}
			
		]
	});
}
function agregarTodo(){
	if(parseInt($("#nEstatus").val(),10)>1){
		swal("Para agregar el estatus debe de ser \"captura\"",{icon:"info",button: "Cerrar"});
		return;
	}
	var aTrs =  oTablePresel.fnGetNodes();
	if(aTrs.length>0){
		queryFormPost("mAgregaRequisVariasIntegracion", {async : false,
			callback : function() 
			{
				$('#tblSolicitudPreSel').dataTable().fnClearTable();
				$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
				swal("Requisiciones agregadas correctamente.",{icon:"info",button: "Cerrar"});
				showTblRequis();
				showTblRequisIntegradas();
			}
		});
	}else{
		swal("No hay requisiciones para agregar",{icon:"info",button: "Cerrar"});
	}
}
function eliminarTodo(){
	if(parseInt($("#nEstatus").val(),10)>1){
		swal("Para eliminar el estatus debe de ser \"captura\"",{icon:"info",button: "Cerrar"});
		return;
	}
	var aTrs =  oTableSel.fnGetNodes();
	if(aTrs.length>0){
		queryFormPost("mDeleteRequiIntegradaTodo", {async : false,
			callback : function() 
			{
				$('#tblSolicitudPreSel').dataTable().fnClearTable();
				$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
				swal("Requisiciones eliminadas correctamente.",{icon:"info",button: "Cerrar"});
				showTblRequisIntegradas();
				showTblRequis();
			}
		});
	}
}
function showAndHiddenButtons(){
	$("#imgAprobarPreseleccion").show();
	$("#imgDevolverPreseleccion").show();
	$("#imgAnular").show();
	$("#requisDisp").show();
	$("#btnDesagregarTodas").show();
	
	if(parseInt($("#nEstatus").val(),10)==1){
		$("#imgDevolverPreseleccion").hide();
	}else if(parseInt($("#nEstatus").val(),10)==2){
		$("#imgAprobarPreseleccion").hide();
		$("#imgAnular").hide();
		$("#requisDisp").hide();
		$("#btnDesagregarTodas").hide();
	}else{
		$("#imgAprobarPreseleccion").hide();
		$("#imgDevolverPreseleccion").hide();
		$("#imgAnular").hide();
		$("#requisDisp").hide();
		$("#btnDesagregarTodas").hide();
	}
}
function aprobarIntegracion(){
	if(parseInt($("#nEstatus").val(),10)!=1){
		swal("Para aprobar la integraci\u00f3n el estatus debe de ser \"captura\"",{icon:"info",button: "Cerrar"});
		return;
	}else{
		var aTrs =  oTableSel.fnGetNodes();
		if(aTrs.length>0){
			$("#nEstatus").val(2);
			queryFormPost("mUpdateEstatusIntegraRequis", {async : false,
				callback : function() 
				{
					queryFormPost("mIntegraRequisRead", {async : false});
					showAndHiddenButtons();
					swal("Se aprobo correctamente.",{icon:"info",button: "Cerrar"});
				}
			});
		}else{
			swal("Es necesario al menos agregar una requisici\u00f3n para poder aprobar el proceso.",{icon:"info",button: "Cerrar"});
		}
	}
}
function devolverIntegracion(){
	if(parseInt($("#nEstatus").val(),10)!=2){
		swal("Para devolver la integraci\u00f3n el estatus debe de ser \"aprobado\"",{icon:"info",button: "Cerrar"});
		return;
	}else{
		queryFormPost("mExisteRequiEnConsolidadoRead", {async : false,
			callback : function() 
			{
				if( parseInt($("#existeRequiEnConsolidado").val(),10)==1){
					swal("Primero hay que cancelar el consolidado "+$("#cIdConsolidado").val() +", para poder devolver este proceso.",{icon:"info",button: "Cerrar"});
				}else{
					$("#nEstatus").val(1);
					queryFormPost("mUpdateEstatusIntegraRequis", {async : false,
						callback : function() 
						{
							queryFormPost("mIntegraRequisRead", {async : false});
							showAndHiddenButtons();
							swal("Se devolvio a captura correctamente.",{icon:"info",button: "Cerrar"});
						}
					});
				}
			}
		});
	}
}
function anularIntegracion(){
	if(parseInt($("#nEstatus").val(),10)!=1){
		swal("Para anular la integraci\u00f3n el estatus debe de ser \"capturado\"",{icon:"info",button: "Cerrar"});
		return;
	}else{
		queryFormPost("mExisteRequiEnConsolidadoRead", {async : false,
			callback : function() 
			{
				if( parseInt($("#existeRequiEnConsolidado").val(),10)==1){
					swal("Primero hay que cancelar el consolidado "+$("#cIdConsolidado").val() +", para poder devolver este proceso.",{icon:"info",button: "Cerrar"});
				}else{
					swal({
						title: "Desea anular este proceso?",
						text: "",
						icon: "info",
						buttons: {
							confirm : "Aceptar",
							cancel: "Cancelar"
							},
						}).then((continuar) => {
							if (!continuar) {
								return;
						}else{
							$("#nEstatus").val(3);
							queryFormPost("mUpdateEstatusIntegraRequis", {async : false,
								callback : function() 
								{
									queryFormPost("mRequisIntegradasDelete", {async : false});
									queryFormPost("mIntegraRequisRead", {async : false});
									showAndHiddenButtons();
									swal("Proceso anulado.",{icon:"info",button: "Cerrar"});
								}
							});
						}
					});
				}
			}
		});
	}
}
