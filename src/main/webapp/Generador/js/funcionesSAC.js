var oTableConsulta;

function resizeDt(){
	var tab = parseInt($("#tbs").val());
	switch (tab){
		case 0://Consulta
			if($('#tblConsulta >tbody >tr').length>0){
				oTableConsulta.fnAdjustColumnSizing();
			}
		break;
		
		
	}
}
function showAndHideTabs(){
	$( "#contratoSAC" ).hide();
	$( "#convenioSAC" ).hide();	
	if(parseInt($("#tbs").val())<2){
		$( "#procedimientoSAC" ).hide();
		
	}else{
		$( "#procedimientoSAC" ).show();
	}
}
function consultaSelects(){
	$("#esperar").dialog("open");
	var object=llenaObjectDat();
	$.ajax({url: "../../BaseSAC" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoCoordinaciones,"cAreaReq");
				llenaCombo(j[0].catalogoAreaResponsable,"cAreaResp");
				llenaCombo(j[0].catalogoTipoProced,"nTipoProcedimiento");
				llenaCombo(j[0].catalogoMateriaProced,"nIdMateriaProcedimiento");
				llenaCombo(j[0].catalogoEstatusProveedor,"nProveedorDadoAlta_SAICNET");
				autoWidthSelects();
				$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function GuardarNuevoProced(){
	$("#tipoOperacion").val(3);
	if(validaInfo()){
		var cadTablaParticipantes=ArrayTabla("tblParticipantes");
		if( confirm("Est\u00e1 seguro de agregar este procedimiento?") ) {
			$("#esperar").dialog("open");
			$.ajax({
				url : "../../BaseSAC",
				type : 'post',
				async : false,
				data : 'cadTablaParticipantes=' +cadTablaParticipantes+'&tipoProceso='+$("#tipoProceso").val()+'&tipoOperacion='+$("#tipoOperacion").val()
				+'&cAreaRequirente='+$("#cAreaReq").val()+'&cAreaResponsable='+$("#cAreaResp").val()+'&cAreaTecnica='+$("#cAreaTec").val()
				+'&cDenominacionProced='+$("#cDenominacionProcedimiento").val()+'&cFechaApertProposiciones='+$("#fApertProposiciones").val()
				+'&cFechaAutConvocatoria='+$("#fAutConvocatoria").val()+'&cFechaEvaluacionTecnica='+$("#fEvaluacionTecnica").val()
				+'&cFechaExpediente='+$("#fExpediente").val()+'&cFechaFallo='+$("#fFallo").val()+'&cFechaGeneracionContrato='+$("#fGeneracionContrato").val()
				+'&cFechaJuntaAclara='+$("#fJuntaAclara").val()+'&cFechaPublicacionConvocatoria='+$("#fPublicacionConvocatoria").val()
				+'&cFechaSolicitud='+$("#fSolicitud").val()+'&cNumeroProcedimiento='+$("#cNumProcedimiento").val()+'&cOficioSolicitud='+$("#cOficioSolicitud").val()
				+'&cProcedContTurnado='+$("#ServidorPublico").val()+'&cProyectoConvocatoria='+$("#cProyectoConvocatoria").val()
				+'&nMateriaProcedimiento='+$("#nIdMateriaProcedimiento").val()+'&nProveedorDadoAlta='+$("#nProveedorDadoAlta_SAICNET").val()
				+'&nTipoProcedimiento='+$("#nTipoProcedimiento").val(),
				dataType : 'json',
				success : function(j) {
					var resp=j[0].RESPUESTA;
					swal({
						title: "",
						text: j[0].MENSAJE,
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							if(resp){
								window.location = "SAC.jsp?tab=" + 0;
							}
							$("#esperar").dialog("close");
					});
				}, error: function( jqXHR, textStatus, errorThrown ) {
					$("#esperar").dialog("close");
				}
			});
		}
	}
}
function validaInfo(){
	var msg="";
	var token="";
	var resp=true;
	if($("#fSolicitud").val()==""){
		msg=msg+token+"Favor de capturar el dato del campo :"+$("#fSolicitud").attr("title");
		token="\n";
	}
	if($("#cOficioSolicitud").val()==""){
		msg=msg+token+"Favor de capturar el dato del campo :"+$("#cOficioSolicitud").attr("title");
		token="\n";
	}
	if($("#cAreaReq").val()=="" || $("#cAreaReq").val()=="0"){
		msg=msg+token+"Favor de seleccionar el :"+$("#cAreaReq").attr("title");
		token="\n";
	}
	if($("#cAreaTec").val()==""){
		msg=msg+token+"Favor de capturar el dato del campo :"+$("#cAreaTec").attr("title");
		token="\n";
	}
	if($("#cAreaResp").val()=="" || $("#cAreaResp").val()=="0"){
		msg=msg+token+"Favor de seleccionar el :"+$("#cAreaResp").attr("title");
		token="\n";
	}
	if($("#cProcedimientoTurnado").val()==""){
		msg=msg+token+"Favor de capturar el dato del campo :"+$("#cProcedimientoTurnado").attr("title");
		token="\n";
	}
	if($("#nTipoProcedimiento").val()=="" || $("#nTipoProcedimiento").val()=="0"){
		msg=msg+token+"Favor de seleccionar el :"+$("#nTipoProcedimiento").attr("title");
		token="\n";
	}
	if($("#nIdMateriaProcedimiento").val()=="" || $("#nIdMateriaProcedimiento").val()=="0"){
		msg=msg+token+"Favor de seleccionar el :"+$("#nIdMateriaProcedimiento").attr("title");
		token="\n";
	}
	
	if($("#nProveedorDadoAlta_SAICNET").val()=="" || $("#nProveedorDadoAlta_SAICNET").val()=="0"){
		msg=msg+token+"Favor de seleccionar el :"+$("#nProveedorDadoAlta_SAICNET").attr("title");
		token="\n";
	}
	if(msg!=""){
		resp=false;
		swal(msg,{icon:"info",button: "Cerrar"});
	}
	return resp;
}
function llenaObjectDatGuardar(){
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val(),
		cAreaRequirente:$("#cAreaReq").val(),
		cAreaResponsable:$("#cAreaResp").val(),
		cAreaTecnica:$("#cAreaTec").val(),
		cDenominacionProced:$("#cDenominacionProcedimiento").val(),
		cFechaApertProposiciones:$("#fApertProposiciones").val(),
		cFechaAutConvocatoria:$("#fAutConvocatoria").val(),
		cFechaEvaluacionTecnica:$("#fEvaluacionTecnica").val(),
		cFechaExpediente:$("#fExpediente").val(),
		cFechaFallo:$("#fFallo").val(),
		cFechaGeneracionContrato:$("#fGeneracionContrato").val(),
		cFechaJuntaAclara:$("#fJuntaAclara").val(),
		cFechaPublicacionConvocatoria:$("#fPublicacionConvocatoria").val(),
		cFechaSolicitud:$("#fSolicitud").val(),
		cNumeroProcedimiento:$("#cNumProcedimiento").val(),
		cOficioSolicitud:$("#cOficioSolicitud").val(),
		cProcedContTurnado:$("#cProcedimientoTurnado").val(),
		cProyectoConvocatoria:$("#cProyectoConvocatoria").val(),
		nMateriaProcedimiento:$("#nIdMateriaProcedimiento").val(),
		nProveedorDadoAlta:$("#nProveedorDadoAlta_SAICNET").val(),
		nTipoProcedimiento:$("#nTipoProcedimiento").val()
	};
	return data0;
}
function llenaObjectDat(){
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val(),
		cAreaRequirente:$("#cAreaReq").val(),
		nTipoProcedimiento:$("#nTipoProcedimiento").val(),
		nIdProcedimientoSAC:$("#nIdProcedimientoSAC").val()
	};
	return data0;
}
function obtieneAreasResponsables(){
	$("#esperar").dialog("open");
	$("#tipoOperacion").val(2);
	$("#tipoProceso").val(1);
	var object=llenaObjectDat();
	$.ajax({url: "../../BaseSAC" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$('#cAreaResp').empty();
				llenaCombo(j[0].catalogoAreaResponsable,"cAreaResp");
				autoWidthSelects();
				$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function obtieneProcesosContratacion(){
	$("#esperar").dialog("open");
	$("#tipoOperacion").val(3);
	$("#tipoProceso").val(2);
	var object=llenaObjectDat();
	$.ajax({url: "../../BaseSAC" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$('#cProcesoContratacion').empty();
				llenaCombo(j[0].catalogoProcesoContratacion,"cProcesoContratacion");
				autoWidthSelects();
				$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function fechasProcedSAC(){
	$("#fSolicitud").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
	});
	$("#fAtencion").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
	});
	$("#fAutConvocatoria").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fPublicacionConvocatoria").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fJuntaAclara").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fApertProposiciones").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fEvaluacionTecnica").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fFallo").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fGeneracionContrato").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
	$("#fExpediente").datepicker({
		dateFormat: "dd/mm/yy",
		altField: "#actualDate",
	 	currentText: "Now",
		showOn: 'button',
		buttonImageOnly: true,	
	   	buttonImage: '../images/calendar.gif',			    					 
		changeYear: true
		
	});
}
function AddRow(){
	if($("#ServidoresPublicos").val()!=""){
		$('#tblParticipantes').dataTable().fnAddData([
	        $("#numEmpleado").val(),
	        $("#nombreEmpleado").val(),
	        $("#apellidoPatEmpleado").val(),
	        $("#apellidoMatEmpleado").val() 
		]);
	}else{
		swal("Seleccione un servidor público",{icon:"info",button: "Cerrar"});
	}
	clearHiddens();
}
function clearHiddens(){
	$("#ServidoresPublicos").val("");
	$("#numEmpleado").val("");
    $("#nombreEmpleado").val("");
    $("#apellidoPatEmpleado").val("");
    $("#apellidoMatEmpleado").val("");
}
function showAndHideInputs(){
	if(parseInt($("#nTipoProcedimiento").val(),10)>4){
		$("#cProyectoConvocatoria").val("");
		$("#fAutConvocatoria").val("");
	    $("#fPublicacionConvocatoria").val("");
	    $("#fJuntaAclara").val("");
	    $("#fApertProposiciones").val("");

		$( "#trProyectoConv").hide();
		$( "#trRevAutConvocatoria").hide();
		$( "#trConvocatoriaFechaPub").hide();
		$( "#trJuntaAclaraciones").hide();
		$( "#trAperturaProposiciones").hide();
	}else{
		$( "#trProyectoConv").show();
		$( "#trRevAutConvocatoria").show();
		$( "#trConvocatoriaFechaPub").show();
		$( "#trJuntaAclaraciones").show();
		$( "#trAperturaProposiciones").show();
	}
}
function consultaDatosProced(){
	$("#esperar").dialog("open");
	var object=llenaObjectDat();
	$.ajax({url: "../../BaseSAC" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoCoordinaciones,"cAreaReq");
				llenaCombo(j[0].catalogoAreaResponsable,"cAreaResp");
				llenaCombo(j[0].catalogoTipoProced,"nTipoProcedimiento");
				llenaCombo(j[0].catalogoMateriaProced,"nIdMateriaProcedimiento");
				llenaCombo(j[0].catalogoEstatusProveedor,"nProveedorDadoAlta_SAICNET");
				llenaCombo(j[0].catalogoProcesoContratacion,"cProcesoContratacion");
				vaciarJsonAInputs(j[0].datosGuardados);
				autoWidthSelects();
				showAndHideInputs();
				if($("#nEstatus").val()==4 || $("#nEstatus").val()==5){
					hideDesiertoDevuelto($("#nEstatus").val());
				}else{
					fechasProcedSAC();
				}
				$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function autoWidthSelects(){
	$("#cAreaReq").addClass("widthSelects");
	$("#cAreaResp").addClass("widthSelects");
	$("#nTipoProcedimiento").addClass("widthSelects");
	$("#nIdMateriaProcedimiento").addClass("widthSelects");
	$("#nProveedorDadoAlta_SAICNET").addClass("widthSelects");
	$("#cProcesoContratacion").addClass("widthSelects");
}
function initTableProced(){
	var qw="nIdProcedimientoSAC="+$("#nIdProcedimientoSAC").val();
	oTableParticipantes= $("#tblParticipantes").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mParticipantesProcedSAC&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]],
		aoColumns: [
			{sName: "nNumeroEmpleado"},
			{sName: "cNombre"},
			{sName: "cApellidoPat"},
			{sName: "cApellidoMat"},
			{sName: "eliminar"}
		]
	});
}
function eliminaParticipante(idPArticipante){
	swal({
		title: "Est\u00e1 seguro de eliminar este participante?",
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
			$("#nIdParticipanteProceso").val(idPArticipante);
			queryFormPost("deleteParticipantProcedSAC",{async : false,
				callback : function() {
					initTableProced();
					swal("Dato eliminado correctamente.",{icon:"info",button: "Cerrar"});
				}
			
			});
		}
	});
}
function addParticipante(idPArticipante){
	if($("#ServidoresPublicos").val()!=""){
		queryFormPost("addNewParticipantProcedSAC",{async : false,
			callback : function() 
			{
				clearHiddens();
				initTableProced();
				swal("Datos agregados correctamente.",{icon:"info",button: "Cerrar"});
			}
		});
	}else{
		swal("Seleccione un servidor público",{icon:"info",button: "Cerrar"});
	}
	
	
}
function actualizaProced(){
	$("#tipoOperacion").val(2);
	$("#tipoProceso").val(2);
	if(validaInfo()){
		swal({
			title: "",
			text: "Recuerda actualizar el estatus al proceso de contrataci\u00f3n.\nEst\u00e1 seguro de actualizar este procedimiento?",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
			}else{
				$("#esperar").dialog("open");
				$.ajax({
					url : "../../BaseSAC",
					type : 'post',
					async : false,
					data : 'tipoProceso='+$("#tipoProceso").val()+'&tipoOperacion='+$("#tipoOperacion").val()
					+'&cAreaRequirente='+$("#cAreaReq").val()+'&cAreaResponsable='+$("#cAreaResp").val()+'&cAreaTecnica='+$("#cAreaTec").val()
					+'&cDenominacionProced='+$("#cDenominacionProcedimiento").val()+'&cFechaApertProposiciones='+$("#fApertProposiciones").val()
					+'&cFechaAutConvocatoria='+$("#fAutConvocatoria").val()+'&cFechaEvaluacionTecnica='+$("#fEvaluacionTecnica").val()
					+'&cFechaExpediente='+$("#fExpediente").val()+'&cFechaFallo='+$("#fFallo").val()+'&cFechaGeneracionContrato='+$("#fGeneracionContrato").val()
					+'&cFechaJuntaAclara='+$("#fJuntaAclara").val()+'&cFechaPublicacionConvocatoria='+$("#fPublicacionConvocatoria").val()
					+'&cFechaSolicitud='+$("#fSolicitud").val()+'&cNumeroProcedimiento='+$("#cNumProcedimiento").val()+'&cOficioSolicitud='+$("#cOficioSolicitud").val()
					+'&cProcedContTurnado='+$("#ServidorPublico").val()+'&cProyectoConvocatoria='+$("#cProyectoConvocatoria").val()
					+'&nMateriaProcedimiento='+$("#nIdMateriaProcedimiento").val()+'&nProveedorDadoAlta='+$("#nProveedorDadoAlta_SAICNET").val()
					+'&nTipoProcedimiento='+$("#nTipoProcedimiento").val()+'&nIdProcedimientoSAC='+$("#nIdProcedimientoSAC").val()
					+'&nProcesoContratacion='+$("#cProcesoContratacion").val()+'&cFechaAtencion='+$("#fAtencion").val()
					+'&cObservaciones='+$("#cObservaciones").val(),
					dataType : 'json',
					success : function(j) {
						swal({
							title: "",
							text: j[0].MENSAJE,
							icon: "info",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								$("#esperar").dialog("close");
						});
						
					}, error: function( jqXHR, textStatus, errorThrown ) {
						$("#esperar").dialog("close");
					}
				});
			}
		});
	}
}
function openXLSX(){
	var ext="xlsx";		
	$("#formato").val(ext);
	window.open("../../servlet/ReportesGRM?"
		+"nTipoReporte="+$("#nTipoReporte").val()
		+"&operacion=5"
		+"&fechaInicio="+$("#fInicio").val()
		+"&fechaFin="+$("#fFin").val()
		+"&cEjercicioActual=2022"
		+"&reporteNombre=Plantilla_Semaforo."+ext
		+"&formato="+ext
		, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
}
function procedimientoDesierto(operacion){
	var msg="Est\u00e1 seguro de declarar desierto est\u00e9 procedimiento?";
	if(operacion==5){
		msg="Est\u00e1 seguro de devolver est\u00e9 procedimiento?";
	}
	$("#tipoOperacion").val(operacion);
	$("#tipoProceso").val(2);
	swal({
		title: "",
		text: msg,
		icon: "info",
		buttons: {
			confirm : "Aceptar",
			cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
		}else{
			$("#esperar").dialog("open");
			$.ajax({
				url : "../../BaseSAC",
				type : 'post',
				async : false,
				data : 'tipoProceso='+$("#tipoProceso").val()+'&tipoOperacion='+$("#tipoOperacion").val()
					+'&cAreaRequirente='+$("#cAreaReq").val()+'&cAreaResponsable='+$("#cAreaResp").val()+'&cAreaTecnica='+$("#cAreaTec").val()
					+'&cDenominacionProced='+$("#cDenominacionProcedimiento").val()+'&cFechaApertProposiciones='+$("#fApertProposiciones").val()
					+'&cFechaAutConvocatoria='+$("#fAutConvocatoria").val()+'&cFechaEvaluacionTecnica='+$("#fEvaluacionTecnica").val()
					+'&cFechaExpediente='+$("#fExpediente").val()+'&cFechaFallo='+$("#fFallo").val()+'&cFechaGeneracionContrato='+$("#fGeneracionContrato").val()
					+'&cFechaJuntaAclara='+$("#fJuntaAclara").val()+'&cFechaPublicacionConvocatoria='+$("#fPublicacionConvocatoria").val()
					+'&cFechaSolicitud='+$("#fSolicitud").val()+'&cNumeroProcedimiento='+$("#cNumProcedimiento").val()+'&cOficioSolicitud='+$("#cOficioSolicitud").val()
					+'&cProcedContTurnado='+$("#ServidorPublico").val()+'&cProyectoConvocatoria='+$("#cProyectoConvocatoria").val()
					+'&nMateriaProcedimiento='+$("#nIdMateriaProcedimiento").val()+'&nProveedorDadoAlta='+$("#nProveedorDadoAlta_SAICNET").val()
					+'&nTipoProcedimiento='+$("#nTipoProcedimiento").val()+'&nIdProcedimientoSAC='+$("#nIdProcedimientoSAC").val()
					+'&nProcesoContratacion='+$("#cProcesoContratacion").val()+'&cFechaAtencion='+$("#fAtencion").val()
					+'&cObservaciones='+$("#cObservaciones").val(),
				dataType : 'json',
				success : function(j) {
					swal({
						title: "",
						text: j[0].MENSAJE,
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							if(j[0].RESPUESTA=='true' || j[0].RESPUESTA){
								hideDesiertoDevuelto(operacion);
							}
							$("#esperar").dialog("close");
					});
					
				}, error: function( jqXHR, textStatus, errorThrown ) {
					$("#esperar").dialog("close");
				}
			});
		}
	});
}
function hideDesiertoDevuelto(status){
	if(status==4){
		$("#trDesierto").css("display", "block");	
	}else if(status==5){
		$("#trDevuelto").css("display", "block");
	}
	$("#btnAgregar").css("display", "none");
	$("#btnGuardar").css("display", "none");
	$("#trBotones").css("display", "none");
	cssDisabledInput();
	cssDisabledTextArea();
	disableSelect();
	readOnlyInput();
	readOnlyTextArea();
	initTableProced();
}