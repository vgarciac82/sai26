function buscaProveedor() {
	myModal.show();
	showTableProv("tblProveedoresDisponibles");
}
function mostrarTablaProveedores(idTable) {
	var consulta = "qw= 1=1";
	if (($.trim($("#rfcProveedor").val())) != "") {
		consulta += " AND cIdRFC LIKE '%25"
				+ $.trim($("#rfcProveedor").val()) + "%25'";
		consulta = consulta.replace("\&", "%26");

	}
	if (($.trim($("#rSocialProveedor").val())) != "") {
		consulta += " AND cRazonSocial LIKE '%25"
				+ $.trim($("#rSocialProveedor").val()) + "%25'";
		consulta = consulta.replace("\&", "%26");
	}
	oTableConsulta = $('#'+idTable+'').dataTable(
	{
		bScrollCollapse: true,
		bInfo: false,
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "<h1>Doble click para seleccionar el proveedor</h1>",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
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
		bProcessing: true,
		pageLength : 5,
		"sAjaxSource" : window.location.protocol+ "//"+ window.location.host+ "/"+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=fn_GetMostrarProveedoresProcedimiento()&"
				+ consulta,
		"aaSorting" : [ [ 0, "asc" ] ],
		aoColumns : [ 
			{sName : "cIdRFC"}, 
			{sName : "cRazonSocial"} 
		]
		
	});
}
function showTableProv(idTable){
	var consulta = "qw= 1=1";
	consulta += " AND cIdRFC LIKE '%25AAA-%25'";
	consulta = consulta.replace("\&", "%26");
	if (($.trim($("#rSocialProveedor").val())) != "") {
		consulta += " AND cRazonSocial LIKE '%25"
				+ $.trim($("#rSocialProveedor").val()) + "%25'";
		consulta = consulta.replace("\&", "%26");
	}
	oTableConsulta = $('#'+idTable+'').dataTable(
	{
		bScrollCollapse: true,
		bInfo: false,
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "<h1>Doble click para seleccionar el proveedor</h1>",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
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
		pageLength : 5,
		bServerSide: true,
		bProcessing: true,
		"sAjaxSource" : window.location.protocol+ "//"+ window.location.host+ "/"+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=fn_GetMostrarProveedoresProcedimiento()&"
				+ consulta,
		"aaSorting" : [ [ 0, "asc" ] ],
		aoColumns : [ 
			{sName : "cIdRFC"}, 
			{sName : "cRazonSocial"} 
		]
	});
}
function  cargaTabla(){
	oTableDocumentos = $("#tblConsultaDatos").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
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
		bProcessing: true,
		pageLength : 5,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCatalogoProveedoresIncumplidos" ,
		"aaSorting" : [ [ 0, "asc" ] ],
		aoColumns : [ 
			{sName : "cEjercicioFiscal"},
			{sName : "cIdRFC"}, 
			{sName : "cRazonSocial"},
			{sName : "cNumeroContratoCNET"},
			{sName : "cNumeroProcedimiento"},
			{sName : "cCodigoExpedienteCNET"},
			{sName : "cCodigoContratoCNET"},
			{sName : "cOficioSancion"},
			{sName : "fFechaOficioSancion"} ,
			{sName : "fFechaTerminoFirma"},
			{sName : "fFechaCaptura"},
			{sName : "usuarioCaptura"},
			{sName : "cDescripcion"}
		]
		
	});
}
function agregaDatePickerFechasSancion(){
	$("#fOficioSancionado").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
		
	});
	$("#fTerminoFirmado").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
		
	});
}
function clearInputSancion(){
	$("#cProveedor").val('');
	$("#cIdRFC").val('');
	$("#cRazonSocial").val('');
	$("#cNoContratoCNET").val('');
	$("#cCodProcedimientoCNET").val('');
	$("#cCodExpedienteCNET").val('');
	$("#cCodContratoCNET").val('');
	
	$("#cOficioSancion").val('');
	$("#fOficioSancion").val('');
	$("#fTerminoFirma").val('');
	$("#nameArchivo").val('');
	$("#causaProveedorIncum").val('');
}
function guardar(){
	if (! $('#formCatProvIncumplido')[0].checkValidity()) {
	      $('#formCatProvIncumplido')[0].reportValidity();
	}else{
		swal({
			title: "Proceso para el guardado de información a proveedores incumplidos",
			text: "¿Está seguro de guardar la información capturada?'",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				execAjax();
			}
		});
	}
}
function execAjax(){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat();
	$.ajax({
			url : "../../servlet/AltaProveedoresServlet",
			type : 'post',
			async : false,
			data : object,
			dataType : 'json',
			success : function(j) {
				var mensaje = j[0].MENSAJE;
				swal({
					title: "",
					text: mensaje,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						cargaTabla();
						clearInputSancion();
						$.unblockUI();
				});
			}
		});
}
function llenaObjectDat(){
	var data0= {
		operacion:$("#operacion").val(),
		cCodigoContratoCNET: $("#cCodContratoCNET").val(),
		cCodigoExpedienteCNET: $("#cCodExpedienteCNET").val(),
		cDescripcion: $("#causaProveedorIncum").val(),
		cEjercicioFiscal: "2023",
		cIdRFC: $("#cIdRFC").val(),
		cNumeroContratoCNET: $("#cNoContratoCNET").val(),
		cNumeroProcedimiento: $("#cCodProcedimientoCNET").val(),
		cOficioSancion: $("#cOficioSancion").val(),
		cRazonSocial: $("#cRazonSocial").val(),
		fFechaOficioSancion: $("#fOficioSancion").val(),
		fFechaTerminoFirma: $("#fTerminoFirma").val()
	};
	return data0;
}
function clearInputs(){
	$("#cProveedor").val("");
	$("#cIdRFC").val("");
	$("#cRazonSocial").val("");
	$("#cNoContratoCNET").val("");
	$("#cCodProcedimientoCNET").val("");
	$("#cCodExpedienteCNET").val("");
	$("#cCodContratoCNET").val("");
	$("#cOficioSancion").val("");
	$("#fOficioSancion").val("");
	$("#fTerminoFirma").val("");
	$("#nameArchivo").val("");
	$("#causaProveedorIncum").val("");
}
function llevaPagoPendienteAction(){
	$("#lTienePagoPendiente").val("0");
	if ($('#llevaPagoPendiente').is(':checked')) {
		$("#lTienePagoPendiente").val("1");
	}
}
function agregaDatePickerFechas(){
	$("#fTerminacion").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
		
	});
	$("#fNotificacion").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
		
	});
}
function buscaProveedorRecision(){
	myModalProvRecision.show();
	showTableProv("tblProveedoresDisponiblesRecision");
}
function searchContract(){
	myModalcontracts.show();
	queryTableContracts();
}
function queryTableContracts(){
	//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCatalogoProveedoresIncumplidos" ,
	var consulta = "'"+$("#cIdRFCRecision").val()+"'";
	var parametros="";
	var arrayContract=new Array();
	var msgTable="NO HAY CONTRATOS FORMALIZADOS EN SAI PARA EL PROVEEDOR "+$("#cRazonSocialRecision").val();
	if($("#cRazonSocialRecision").val()==""){
		msgTable="PRIMERO HAY QUE SELECCIONAR EL PROVEEDOR";
	}
	$('#tblConsultaContract').dataTable().fnClearTable();
	tableConsultaContracts=$("#tblConsultaContract").dataTable({
		bAutoWidth : true,
		bPaginate:false,
		bFilter : false,
		bDestroy:true,
		bRetrive:true,
		bLengthChange: false,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: msgTable,
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Buscar:"
			
		},
		bProcessing: true,
		bJQueryUI: true,
		aaSorting: [[ 1, "desc" ]] ,
		aoColumns: [
			{ sName: "cEjercicio",bSortable: false },
			{ sName: "cIdContratoSAI",bSortable: false   },
			{ sName: "cIdRFC",bSortable: false , bVisible: false  },					
			{ sName: "proveedor",bSortable: false,bVisible: false 	},
			{ sName: "cNoContratoCNET",bSortable: false },
			{ sName: "cNoProcedimientoCNET",bSortable: false },
			{ sName: "nCodExpedienteCNET",bSortable: false,bVisible: false  },
			{ sName: "nCodContratoCNET", bVisible: false,bSortable: false },
			{ sName: "folioCaso",bSortable: false,bVisible: false },
			{ sName: "cConceptoContrato",bSortable: false,bVisible: false },
			{ sName: "nameDB",bSortable: false,bVisible: false }
			
		]
	});
	$.getJSON("../../catalogos/SelectJson.jsp",
		{
			Tabla : "CONTRACTS_PROVEEDOR",
			Param : parametros,
			Campos : consulta,
			MaxReg : "",
			ajax : 'false'
		},
		function(data) {
			for( var i = 0; i < data.length; i++ ) {
				arrayContract [i]=[data[ i ].Col0, data[ i ].Col1, data[ i ].Col2, data[ i ].Col3,data[ i ].Col4
								,data[ i ].Col5,data[ i ].Col6,data[ i ].Col7,data[ i ].Col9,data[ i ].Col8,data[ i ].Col10 ];
				
			}
			if(data.length>0){
				$("#tblConsultaContract").dataTable().fnAddData(arrayContract);
				$("#tblConsultaContract").dataTable().fnAdjustColumnSizing();
			}
		}
	);
}
function clearInputsContract(){
	$("#cEjercicioContrato").val( '');
	$("#cIdContratoDefinitivo").val('');
	$("#cNoContratoRecision").val('');
	$("#cCodProcedimientoCNETRecision").val('');
	$("#cCodExpedienteCNETRecision").val('');
	$("#cCodContratoCNETRecision").val('');
	$("#folioCaso").val('');
}
function consultaRecisiones(){
	$("#tblConsultaRecisionCont").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay recisiones capturadas en SAI",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
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
		bProcessing: true,
		pageLength : 5,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratosRescindidos" ,
		"aaSorting" : [ [ 0, "asc" ] ],
		aoColumns : [ 
			{sName : "cEjercicioFiscal"},
			{sName : "cIdRFC"}, 
			{sName : "cRazonSocial"},
			{sName : "cIdContratoDefinitivo"},
			{sName : "cNumeroContratoCNET"},
			{sName : "cNumeroProcedimiento"},
			{sName : "cCodigoExpedienteCNET"},
			{sName : "cCodigoContratoCNET"},
			{sName : "fFechaRecision"} ,
			{sName : "fFechaNotificacionUAF"},
			{sName : "tienePagoPendiente"},
			{sName : "fFechaCaptura"},
			{sName : "cLogin"},
			{sName : "cCausaRecision"}
		]
	});	
}
function GuardaRecision(){
	if (! $('#formRecisionContratos')[0].checkValidity()) {
	      $('#formRecisionContratos')[0].reportValidity();
	}else{
		if($("#cProveedorRecision").val()==""){
			swal("Favor de seleccionar el proveedor.",{icon:"warning",button: "Cerrar"});
			return;
		}
		if($("#cNoContratoRecision").val()==""){
			swal("Favor de seleccionar el contrato.",{icon:"warning",button: "Cerrar"});
			return;
		}
		if(!validateAtachment()){
			swal("Favor de seleccionar un archivo.",{icon:"warning",button: "Cerrar"});
			return;
		}
		swal({
			title: "Captura de contratos rescindidos.",
			text: "¿Está seguro de guardar la información capturada?'",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				ejecutaAjax();
			}
		});
		
	}
}

function ejecutaAjax(){
	$.blockUI({message: "Procesando espere ......"});
	dataRecision.append('operacion', $("#operacion").val());
	dataRecision.append('descripcionCausa', encodeURIComponent($("#causaRecision").val()));
	dataRecision.append('fechaTermino', $("#fTermino").val());
	dataRecision.append('tienePagoPendiente', $("#lTienePagoPendiente").val());
	dataRecision.append('cContratoCNET', $("#cNoContratoRecision").val());
	dataRecision.append('cFolio', $("#folioCaso").val());
	dataRecision.append('cContratoDefinitivo', $("#cIdContratoDefinitivo").val());
	dataRecision.append('fechaNotificacionUAF', $("#fNotificacionUAF").val());
	dataRecision.append('cRFC', $("#cIdRFCRecision").val());
	dataRecision.append('nTipoTerminacionCont', $("#nTipoTerminacionCont").val());
	dataRecision.append('ejercicioContrato', $("#cEjercicioContrato").val());
	dataRecision.append('procedimientoCNET', $("#cCodProcedimientoCNETRecision").val());
	dataRecision.append('expedienteCNET', $("#cCodExpedienteCNETRecision").val());
	dataRecision.append('codigoContratoCNET', $("#cCodContratoCNETRecision").val());
	dataRecision.append('nameDB', $("#cNameDB").val());	
	jQuery.ajax({
	    url: '../../servlet/LeeArchivos',
	    data: dataRecision,
	    cache: false,
	    contentType: false,
	    dataType: "json",
	    processData: false,
	    method: 'POST',
	    type: 'POST', // For jQuery < 1.9
	    success: function(j){
	    	consultaRecisiones();
	    	if(j[0].ISCORRECT=="true" || j[0].ISCORRECT){
	    		swal(j[0].MSG,{icon:"success",button: "Cerrar"});
	    	}else{
	    		swal(j[0].MSG,{icon:"error",button: "Cerrar"});
	    	}
	    	$.unblockUI();
			clearInputRecision();
	    },error: function(){
	    	swal("Error",{icon:"error",button: "Cerrar"});
	    	$.unblockUI();
	    }
	});
}
function validateAtachment(){
	var hayDocumento=false;
	jQuery.each(jQuery('#nameArchivoRecision')[0].files, function(i, file) {
	    dataRecision.append('file-'+i, file);
	    hayDocumento=true;
	});
	return hayDocumento;
}
function clearInputRecision(){
	$("#cProveedorRecision").val('');
	$("#nameArchivoRecision").val('');
	$("#causaRecision").val('');
	$("#fTermino").val('');
	$('#llevaPagoPendiente').prop('checked', true);
	$("#lTienePagoPendiente").val('1');
	$("#cNoContratoRecision").val('');
	$("#folioCaso").val('');
	$("#cIdContratoDefinitivo").val('');
	$("#fNotificacionUAF").val('');
	$("#cNameDB").val('');
	$("#cIdRFCRecision").val('');
	$("#cEjercicioContrato").val('');
	$("#cCodProcedimientoCNETRecision").val('');
	$("#cCodExpedienteCNETRecision").val('');
	$("#cCodContratoCNETRecision").val('');
	$("#cConceptoContrato").val('');
	
}