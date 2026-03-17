function showAndHideTabs(){
	if(tabb==0 || tabb==1){
		$("#caratulaConvCap4").hide();
		$("#presupuestoConvCap4").hide();
		$("#precompromisoConvCap4").hide();
	}else{
		$("#caratulaConvCap4").show();
		$("#presupuestoConvCap4").show();
		$("#precompromisoConvCap4").show();
	}
}
function cambiaCentrocontableUsuario(){
	$.ajax({
		url: '../../servlet/CambiaPropiedadesUsuario',
		dataType: 'json',
		data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
		async : false,
		success : function(j) {
			if(j[0].error){
				swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"info",button: "Cerrar"});
			}else{
				$("#cIdUnidadEjecutora").val(j[0].unidadEjecutora);
				$("#cIdUnidadResponsableUsuario").val(j[0].unidadEjecutora);
				if($("#tipoOperacion").val()==1){
					searchContracts();	
				}else if($("#tipoOperacion").val()==3){
					searchConvenios();
				}
			}
		}
	});
}
function consultaNuevoConv(){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoAreaResponsable,"cIdUnidadEjecutora");
				llenaCombo(j[0].catalogoTipoMod,"cIdTipoMod");
				$("#cIdUnidadEjecutora").val($("#cIdUnidadResponsableUsuario").val());
				searchContracts();
				$.unblockUI();
		}, error: function() {
			$.unblockUI();
		}
	});
}
function llenaObjectDat(){
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val()
		
	};
	return data0;
}
function searchContracts(){
	var param = "'"+$("#cIdUnidadEjecutora").val()+"'";
	var funcion="fn_mContratosCap4Autorizados";
	var cadCont="''";
	var cadRFC="''";
	var cadConcepto="''";
	if($("#cIdDefinitivo").val()!=""){
		cadCont="'"+$("#cIdDefinitivo").val()+"'";
	}
	if($("#cIdRFC").val()!=""){
		cadRFC="'"+$("#cIdRFC").val()+"'";
	}
	if($("#cDescripcion").val()!=""){
		cadConcepto="'"+$("#cDescripcion").val()+"'";
	}
	$('#tblContratosAprobados').dataTable().fnClearTable();
	oTableContratosAprovados = $("#tblContratosAprobados").dataTable({
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
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Contratos",
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param+","+cadCont+","+cadConcepto+","+cadRFC ) +")" ) ,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cIdUnidadEjecutora" },
			{ sName: "cIdContratoDefinitivo" },
			{ sName: "cIdRFC" },
			{ sName: "cRazonSocial" },
			{ sName: "mMontoSinIVAMinimo" },
			{ sName: "mMontoNetoMinimo" },
			{ sName: "cConceptoContrato" }
			
		]
		,fnInitComplete: function() {
			if($('#tblContratosAprobados >tbody >tr').length>0){
				oTableContratosAprovados.fnAdjustColumnSizing();
			}
		}
	});
}
function consultaConv(){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoAreaResponsable,"cIdUnidadEjecutora");
				$("#cIdUnidadEjecutora").val($("#cIdUnidadResponsableUsuario").val());
				searchConvenios();
				$.unblockUI();
		}, error: function() {
			$.unblockUI();
		}
	});
}
function searchConvenios(){
	var param = "'"+$("#cIdUnidadEjecutora").val()+"'";
	var funcion="fn_mConveniosCap4";
	var cadCont="''";
	
	if($("#cIdDefinitivo").val()!=""){
		cadCont="'"+$("#cIdDefinitivo").val()+"'";
	}
	
	$('#tblconsultaConveniosContCap4').dataTable().fnClearTable();
	oTableConvenios = $("#tblconsultaConveniosContCap4").dataTable({
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
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Convenios",
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param+","+cadCont ) +")" ) ,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cEjercicio" },
			{ sName: "nConsecutivoModificacion" },
			{ sName: "cIdContratoDefinitivo" },
			{ sName: "cEstado" },
			{ sName: "mTotalAnterior" },
			{ sName: "montoModificado" },
			{ sName: "mTotalNuevo" },
			{ sName: "nIdContModCap4", bVisible: false }
		]
		,fnInitComplete: function() {
			if($('#tblconsultaConveniosContCap4 >tbody >tr').length>0){
				oTableConvenios.fnAdjustColumnSizing();
			}
		}
	});
}
function addNewConvenio(){
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectNewCov();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true" ){
					
					swal({
						title: "",
						text: j[0].MENSAJE,
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							//window.location = 'ConveniosCap4.jsp?tab=2';
							window.location = 'ConveniosCap4.jsp?tab=2&cIdContratoDefinitivo='+ j[0].ContractConvCap4Definitivo+'&nConsecutivoMod='+ j[0].ContractConvCap4Consecutivo+'&nIdContModCap4='+ j[0].nIdContModCap4;
								
					});
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
}
function fillObjectNewCov(){
	if($('#esPorTotalPlurianual').is(':checked') ){
		$("#bEsXTotalPlu").val(1);
	}
	var data0= {
			tipoProceso:$("#tipoProceso").val(),
			tipoOperacion:2,
			cIdContratoDefinitivo:$("#cIdContratoDefinitivo").val(),
			lEsTotalPluri:$("#bEsXTotalPlu").val(),
			nTipoModificacion:$("#cIdTipoMod").val()
		};
	return data0;
}
function infoQuery(){
	$.blockUI({message: "Procesando espere ......"});
	$("#tipoOperacion").val(4);
	var object=fillObjectQuery();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true" ){
					vaciarJsonAInputs(j[0].datosCaratula);
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
}
function fillObjectQuery(){
	var data0= {
			tipoProceso:$("#tipoProceso").val(),
			tipoOperacion:$("#tipoOperacion").val(),
			cIdContratoDefinitivo:$("#cIdContratoDefinitivo").val(),
			nConsecutivoMod:$("#nConsecutivoMod").val(),
			nIdcontratoMod:$("#nIdContModCap4").val(),
			nIdLineaConsolidado:$("#nIdLineaConsolidado").val(),
			mMontoNeto:$("#mMontoNeto").val(),
			nCantidad:$("#nCantidad").val() 
		};
	return data0;
}
function fillObjectAprueba(){
	if(!("SRV"==$("#cIdUnidadMedida").val())){
		$("#fechaInicio").val($("#fechaEntrega").val());
		$("#fechaFin").val($("#fechaEntrega").val());
	}
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val(),
		cIdContratoDefinitivo:$("#cIdContratoDefinitivo").val(),
		nConsecutivoMod:$("#nConsecutivoMod").val(),
		nIdcontratoMod:$("#nIdContModCap4").val(),
		cObjetoConvenio:$("#objConv").val(),
		cNoConvenio:$("#cNoConvenio").val(),
		fFormalizacion:$("#fechaFormalizacion").val(),
		fInicio:$("#fechaInicio").val(),
		fFin:$("#fechaFin").val()
	};
	return data0;
}
function hideAndShowDates(){
	if("SRV"==$("#cIdUnidadMedida").val()){
		$("#trfini").show();
		$("#trffin").show();
		$("#trfentrega").hide();
	}else{
		$("#trfini").hide();
		$("#trffin").hide();
		$("#trfentrega").show();
	}
}
function deleteModificatorio(){
	if($("#nEstatus").val()>1){
		swal("El estatus del convenio debe estar en captura para poder eliminarlo.",{icon:"info",button: "Cerrar"});
		return;
	}
	$.blockUI({message: "Procesando espere ......"});
	$("#tipoOperacion").val(5);
	var object=fillObjectQuery();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						if(j[0].RESPUESTA || j[0].RESPUESTA=="true" )
							window.location = 'ConveniosCap4.jsp?tab=1';
				});
				
		}, error: function() {
			$.unblockUI();
		}
	});
}
function searchItemsContract(){
	var funcion='fn_mPartidasConvenioCap4';
	var cidcontratoDef = "'"+$("#cIdContratoDefinitivo").val()+"'";
	$('#tblPartidasMods').dataTable().fnClearTable();
	oTableItems = $("#tblPartidasMods").dataTable({
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
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Contratos",
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (cidcontratoDef+","+$("#nIdContModCap4").val()+","+$("#nConsecutivoMod").val() ) +")" ) ,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "nIdLineaConsolidado" },
			{ sName: "cIdSubPartida" },
			{ sName: "cIdCABM" },
			{ sName: "cIdUnidadMedida" },
			{ sName: "cDescripcionAdicional" },
			{ sName: "nCantidad" },
			{ sName: "mPrecioUnitario" },
			{ sName: "valorIVA" },
			{ sName: "mMontoNeto" },
			{ sName: "porcentajeMod" },
			{ sName: "mMontoNetoOriginal",bVisible:false }
		]
		,fnInitComplete: function() {
			if($('#tblPartidasMods >tbody >tr').length>0){
				oTableItems.fnAdjustColumnSizing();
			}
		}
	});
}
function calulaprecioUnitario(){
	if($("#mMontoNeto").val()=="" || $("#mMontoNeto").val()==0.00 || $("#nCantidad").val()=="" || $("#nCantidad").val()==0.00){
		$("#mPrecioUnitario").val(0.00);
		return;
	}
	$("#mPrecioUnitario").val(($("#mMontoNeto").val() / ((1+(0.01*$("#nPorcentajeIVA").val() ))*$("#nCantidad").val() )).toFixed(2));
	calculaPorcentajeMod();
	
}
function calculaPorcentajeMod(){
	if($("#mMontoNeto").val()=="" || $("#mMontoNeto").val()==0.00){
		$("#nPorcentajeMod").val(0)
		return;
	}
	$("#nPorcentajeMod").val( ($("#mMontoNeto").val()/$("#mMontoOriginal").val() *100).toFixed(2) );
}
function calculaMontoNeto(){
	if($("#nCantidad").val()=="" || $("#nCantidad").val()==0.00){
		$("#mMontoNeto").val(0.00)
		return;
	}
	$("#mMontoNeto").val( ($("#nCantidad").val()*$("#mPrecioUnitario").val() *(1+(0.01*$("#nPorcentajeIVA").val() ))).toFixed(2) );
	calculaPorcentajeMod();
}
function enabledAndDisabledItems(unidadMed){
	if("SRV"==unidadMed){
		document.getElementById("mMontoNeto").disabled = false;
		document.getElementById("nCantidad").disabled = true;
	}else{
		document.getElementById("nCantidad").disabled = false;
		document.getElementById("mMontoNeto").disabled = true;
	}
}
function saveItem(){
	if(parseFloat($("#nPorcentajeMod").val())>20.00){
		swal("El porcentaje de modificación no puede ser mayor al 20 %.",{icon:"warning",button: "Cerrar"});
		return;
	}
	$.blockUI({message: "Procesando espere ......"});
	$("#tipoOperacion").val(6);
	var object=fillObjectQuery();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				myModal.hide();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
					swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosCaratula);
					searchItemsContract();
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
}
function apruebaContModCap4(){
	if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0) { 
		swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
		return;
	}
	$("#tipoOperacion").val(7);
	if(validateFillItems()){
		return;
	}
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectAprueba();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
					swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosCaratula);
					disabledenabledEdicionConvenio();
					hideAndShowButtonAprobar();
					hideAndShowButtonDevuelvePresup();
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
				
		}, error: function() {
			$.unblockUI();
		}
	});
}
function validateFillItems(){
	var resp=false;
	var msg="";
	var token="";
	if(""==$("#objConv").val()){
		msg=token+"Favor de capturar el objeto del convenio."
		token="\n";
	}
	if(""==$("#cNoConvenio").val()){
		msg+=token+"Favor de capturar el número del convenio."
		token="\n";
	}
	if(""==$("#fechaFormalizacion").val()){
		msg+=token+"Favor de capturar la fecha de formalización."
		token="\n";
	}
	if("SRV"==$("#cIdUnidadMedida").val() && ""==$("#fechaInicio").val()){
		msg+=token+"Favor de capturar la fecha inicial."
		token="\n";
	}
	if("SRV"==$("#cIdUnidadMedida").val() && ""==$("#fechaFin").val()){
		msg+=token+"Favor de capturar la fecha final."
		token="\n";
	}
	if(!("SRV"==$("#cIdUnidadMedida").val()) && ""==$("#fechaEntrega").val()){
		msg+=token+"Favor de capturar la fecha d."
		token="\n";
	}
	if(""!=msg){
		swal(msg,{icon:"warning",button: "Cerrar"});
		resp=true;
	}
	return resp;
}
function disabledenabledEdicionConvenio(){
	document.getElementById("objConv").disabled = true;
	document.getElementById("cNoConvenio").disabled = true;
	document.getElementById("fechaFormalizacion").disabled = true;
	document.getElementById("fechaInicio").disabled = true;
	document.getElementById("fechaFin").disabled = true;
	document.getElementById("fechaEntrega").disabled = true;
	if($("#nEstatus").val()==1){
		document.getElementById("objConv").disabled = false;
		document.getElementById("cNoConvenio").disabled = false;
		document.getElementById("fechaFormalizacion").disabled = false;
		document.getElementById("fechaInicio").disabled = false;
		document.getElementById("fechaFin").disabled = false;
		document.getElementById("fechaEntrega").disabled = false;
	}
}
function devuelveContModCap4(){
	if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0) { 
		swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
		return;
	}
	$("#tipoOperacion").val(8);
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectAprueba();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
					swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosCaratula);
					hideAndShowButtonDevuelvePresup();
					hideAndShowButtonAprobar();
					disabledenabledEdicionConvenio();
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
	
}
function agregaDatePickerFechas(){
	$("#fechaFormaliza").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fInicial").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fFinal").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fEntrega").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true		
	});		
}
function loadClavesPresupuestalesContratoMod(){
	var qw=encodeURIComponent("cIdContratoDefinitivo='"+$('#cIdContratoDefinitivo').val()+"#M"+$('#nConsecutivoMod').val()+"'" );	
	oTableClaves=$('#dt_clavepresup').dataTable( {
			bPaginate: false,
			bLengthChange: false,
			bFilter: false,
			bInfo: false,
			bAutoWidth: true,
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No hay registros a mostrar",
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
			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			sScrollX: "100%",
			bRetrive : true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
			aoColumns: [
				{ sName: "nIdClaveEgresos"},
				{ sName: "ClaveInterna" }
				]
	}) ;
}
function buscaClavePresupuestal(){
	window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
			+ '&cIdDocumento=' + $('#cIdContratoDefinitivo').val() + '&cIdRFC=' + $('#cIdRFC').val()
			 +'&cuentaDisponible=' + $('#cuentaDisponible').val()+'&isContratoCap4=1'
			, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
}
function fnClickAddRowC() {
	rowCount = $('#dt_clavepresup tr').length;
	var vep = $('#ep').val();
	if (vep == '') {
		return ;
	}
	if(searchEP(vep)){
		swal("La estructura presupuestal ya está agregada, favor de seleccionar otra.",{icon:"warning",button: "Cerrar"});
		return;
	}
	var tmp = vep.lastIndexOf( "\." );
	var uEje= vep.substring( tmp - 3, tmp);
	var cint = vep.substring( tmp -3 );
	$("#cIdUnidadEjecutoraEP").val(uEje);
	$("#ClaveInterna").val(cint);
	$("#nIdClaveEgresos").val(vep);
	
	swal({
		title: "",
		text: "Seguro que desea agregar la siguiente EP: "+vep+"\nNo se podr\u00e1 revertir el cambio.\n¿Desea continuar?",
		icon: "info",
		buttons: {
			confirm : "Aceptar",
			cancel: "Cancelar"
		},
	}).then((continuar) => {
		if (!continuar) {
			return;
		}else{
			$("#cIdContratoMat").val($("#cIdContratoDefinitivo").val());
			$("#cContratoDefinitivo").val($("#cIdContratoDefinitivo").val()+'#M'+$("#nConsecutivoMod").val());
			queryFormPost("agregaEPContratoCreate", {async: false,
				callback: function(){
					$('#ep').val("");
					loadClavesPresupuestalesContratoMod();
				} 				
			});
		}
	});
}
function searchEP(vEP){
	var resp=false;
	var aData;
	var aTrs = oTableClaves.fnGetNodes();
	for (i=1 ; i<=aTrs.length ; i++ ){
		aData = oTableClaves.fnGetData(i-1);
		if(vEP==aData[0]){
			resp=true;
			break;
		}
	}
	return resp;	
}
function deleteEP(vep){
	swal({
		title: "",
		text: "Seguro que desea eliminar la siguiente EP: "+vep+"\n¿Desea continuar?",
		icon: "info",
		buttons: {
			confirm : "Aceptar",
			cancel: "Cancelar"
		},
	}).then((continuar) => {
		if (!continuar) {
			return;
		}else{
			$("#tipoOperacion").val(11);
			$.blockUI({message: "Procesando espere ......"});
			$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
				,data:'tipoProceso='+$("#tipoProceso").val()+'&tipoOperacion='+$("#tipoOperacion").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
					+'&nConsecutivoMod='+$("#nConsecutivoMod").val()+'&nIdcontratoMod='+$("#nIdContModCap4").val()+'&cEP='+vep
				,dataType: 'json', success: 
					function(j){
						$.unblockUI();
						if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
							swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
							loadClavesPresupuestalesContratoMod();
						}else{
							swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
						}
				}, error: function() {
					$.unblockUI();
				}
			});
			
		}
	});
}
function hideAndShowButtonAprobar(){
	$("#imgAprobarPresupuestoContMod").hide();
	$("#divAddEP").hide();
	if($("#nEstatus").val()==1){
		$("#imgAprobarPresupuestoContMod").show();
		$("#divAddEP").show();
	}
}
function hideAndShowButtonDelete(){
	$("#imgEliminar").hide();
	if($("#nEstatus").val()==1){
		$("#imgEliminar").show();
	}
}
function hideAndShowButtonDevuelvePresup(){
	$("#imgDevolverPresupuestoContMod").hide();
	if($("#nEstatus").val()==2){
		$("#imgDevolverPresupuestoContMod").show();
	}
}
function clickHandlers(){
	$('#edit').click( function () {
       	editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
   	} );
}
function editPrecompromiso( oTableLocal ){
	//funcion para agregar los inputs a la tabla
	var i,j,descMes, nomMes,nColumna;
	var aData;
	var aTrs = oTableLocal.fnGetNodes();
	for (i=1 ; i<=aTrs.length ; i++ ){
		mes=parseInt($("#mesDisponible").val(),10);
		if(mes==0)
			mes=mes+1;
	    aData = oTableLocal.fnGetData(i-1);
	    nColumna=aData.length-mes-1;//dos columnas al final ocultas
	    for(j=2; j<nColumna; j++){
	    	nomMes='mes'+mes;
	    	descMes='mes'+mes+'-'+i;
	   		$("#dt_preCompromiso").children().children()[i].children[j].innerHTML = '<input class="form-control" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyDoubles(event))">';
	    	mes++;	
	    }
		
    }
}
function Sinfrmt( fld )	{
   	var valcol = fld.value ;
   	var vcompr = $("#mComprometido").val();
   	vcompr = quitaFmt( vcompr );
   	valcol = quitaFmt( valcol );
	$("#" + fld.id).val( valcol );
   	fld.select();
	$("#mComprometido").val( parseFloat( vcompr ) - parseFloat( valcol ) );
	$("#mComprometido").formatCurrency();
}
function cambiafrmt( fld ){
   	var vcompr = $("#mComprometido").val();
   	var vfld = $("#" + fld.id).val()
   	if (vfld == "")
   		vfld = '0';
	vcompr = quitaFmt( vcompr );
	vfld=quitaFmt(vfld);
   	$("#mComprometido").val( parseFloat(vcompr) + parseFloat( vfld ) );
	$("#" + fld.id).formatCurrency();
	$("#mComprometido").formatCurrency();
}
function valSufic( fld ) { 
	var valor = $("#" + fld.id).val();

	valor=quitaFmt(valor);
	//validacion para que solo se permitan números
	if(isNaN(valor)){
		swal("Ingrese solo valores num\u00e9ricos",{icon:"success",button: "Cerrar"});
		$("#" + fld.id).val( "0" );
		return false;
	}
	if(valor<0){
		swal("No se pueden ingresar valores negativos",{icon:"success",button: "Cerrar"});
    	$("#" + fld.id).val( "0" );
    	return false;
	}
	//obtiene la suficiencia de la EP y el valor del precompromiso ingresado para comparar
	var oTableLocal = $('#dt_suficiencia').dataTable();
	var vimptot = $("#mImporteTotal").val();
	var vcompr  = $("#mComprometido").val();
	vimptot = quitaFmt( vimptot );
	vcompr = quitaFmt( vcompr );
	vcompT = parseFloat(vimptot)-parseFloat(vcompr);
	var nren = parseInt(fld.id.substring(fld.id.lastIndexOf("-") + 1),10) -1;
	var ncol = parseInt( fld.id.substring(5, 3),10 ) + 1 ;
	var aData = oTableLocal.fnGetData( nren );
	if (valor == "") {
		valor = '0';
		$("#" + fld.id).val( "0" );
	}
	var valsuf = parseFloat( aData[ ncol ] );
	if (parseFloat(valor) > valsuf) {
    	swal("NO hay Suficicencia Mensual en la Clave Presupuestal",{icon:"success",button: "Cerrar"});
    	$("#" + fld.id).val( "0" );
    	return false;
    }
	vcompr=parseFloat(vcompr);
	valor=parseFloat(valor);
	vimptot=parseFloat(vimptot);
	if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
    	swal("El Compromiso Actual Excede al Saldo Compromiso",{icon:"success",button: "Cerrar"});
    	$("#" + fld.id).val( "0" );
    	return false;
    }
	vcompT = vimptot-valor-vcompr;
	//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
	vcompT=vcompT.toFixed(2);
	$("#difPrecompromiso").val(parseFloat(vcompT));
	$("#difPrecompromiso").formatCurrency();
}
function cargaPreCompromisoVacio(){
	var funcion="fn_mPrecompromisoConvCap4";
	var param=encodeURIComponent("'"+$("#cIdContratoDefinitivo").val()+"#M"+$("#nConsecutivoMod").val()+"'");
	$('#dt_preCompromiso').dataTable( {
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
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Estructuras presupuestales",
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
		
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param ) +")" ) ,
		aoColumns: [
			{ sName: "ClaveSIAFF",bSortable: false },
			{ sName: "ClaveInterna",bSortable: false },
			{ sName: "compromiso01",bSortable: false },
			{ sName: "compromiso02",bSortable: false },
			{ sName: "compromiso03",bSortable: false },
			{ sName: "compromiso04",bSortable: false },
			{ sName: "compromiso05",bSortable: false },
			{ sName: "compromiso06",bSortable: false },
			{ sName: "compromiso07",bSortable: false },
			{ sName: "compromiso08",bSortable: false },
			{ sName: "compromiso09",bSortable: false },
			{ sName: "compromiso10",bSortable: false },
			{ sName: "compromiso11",bSortable: false },
			{ sName: "compromiso12",bSortable: false },
			{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
       		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }]
          } ) ;
}
function cargaSuficiencias(){
	var funcion="fn_mDisponibleEP";
	var param=encodeURIComponent("'"+$("#cIdContratoDefinitivo").val()+"#M"+$("#nConsecutivoMod").val()+"'");
	
	$('#dt_suficiencia').dataTable( {
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
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Estructuras presupuestales",
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param ) +")" ) ,
		aoColumns: [
			{ sName: "ClaveSIAFF" ,bSortable: false},
			{ sName: "ClaveInterna",bSortable: false },
			{ sName: "MontoEnero" ,bSortable: false},
			{ sName: "MontoFebrero" ,bSortable: false},
			{ sName: "MontoMarzo" ,bSortable: false},
			{ sName: "MontoAbril",bSortable: false },
			{ sName: "MontoMayo" ,bSortable: false},
			{ sName: "MontoJunio",bSortable: false },
			{ sName: "MontoJulio" ,bSortable: false},
			{ sName: "MontoAgosto" ,bSortable: false},
			{ sName: "MontoSeptiembre" ,bSortable: false},
			{ sName: "MontoOctubre",bSortable: false },
			{ sName: "MontoNoviembre" ,bSortable: false},
			{ sName: "MontoDiciembre" ,bSortable: false},
			{ sName: "MontoAnual",bSortable: false },
			{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false } ]	
        });
}
function precomprometer(){
	if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0) { 
		swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
		return;
	}
	if(parseInt($("#nEstatus").val(),10)!=2){
		swal("El estatus del convenio no permite realizar está acción.",{icon:"info",button: "Cerrar"});
		return;
	}
	//crea el array de la tabla de montos
	var arregloDatos=ArrayTabla();
	if(arregloDatos==null || arregloDatos==""){
		swal("Falta agregar los montos del precompromiso ",{icon:"info",button: "Cerrar"});
		return;
	}
	if($("#mComprometido").val()!=$("#mImporteTotal").val()){
		swal("No se puede comprometer mas o menos del monto del convenio, monto calendarizado = "+$("#mComprometido").val()+" Monto que se requiere calendarizar ="+$("#mImporteTotal").val(),{icon:"info",button: "Cerrar"});
		return;
	}
	$("#tipoOperacion").val(9);
	$.blockUI({message: "Procesando espere ......"});
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:'tipoProceso='+$("#tipoProceso").val()+'&tablaDatos='+arregloDatos+'&tipoOperacion='+$("#tipoOperacion").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
					+'&nConsecutivoMod='+$("#nConsecutivoMod").val()+'&nIdcontratoMod='+$("#nIdContModCap4").val()+'&cDescripPoliza=PRECOMPROMISO DE CONVENIOS CAPITULO 4000'
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
					swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosCaratula);
					cargaPreCompromisoVacio();
					hideAndShowButtonDevolverPrecom();
					hideAndShowButtonAutPrecom();
					hideAndShowButtonPrecomprometer();
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
}
function ArrayTabla(){
	var arregloTmp=new Array();
	var arrayFila=new Object();
	
	var aTrs = $('#dt_preCompromiso').dataTable().fnGetNodes();
	var vimporteP;
	var nTr;
	var jqInputs;
	for ( var i=0 ; i<aTrs.length; i++ ){
		nTr =  $('#dt_preCompromiso').dataTable().fnGetData(aTrs[i]);
		jqInputs = $('input',aTrs[i] );
		for ( j=0 ; j < jqInputs.length ; j++ ) {
			var k=j;
			vimporteP = jqInputs[j].value ;
			vimporteP = quitaFmt(vimporteP);
			if(vimporteP==0){
				continue;
			}
			arrayFila=[nTr[0]+'.'+nTr[1],k+1,vimporteP,"|"];
			arregloTmp.push(arrayFila);
		}
	}	
	return arregloTmp;
}
function devuelvePrecompromiso(){
	if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0) { 
		swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
		return;
	}
	$("#tipoOperacion").val(10);
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectPrecom();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
					swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosCaratula);
					$("#mComprometido").val(0.0);
					$("#mComprometido").formatCurrency();
					hideAndShowButtonDevolverPrecom();
					hideAndShowButtonAutPrecom();
					hideAndShowButtonPrecomprometer();
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
}
function fillObjectPrecom(){
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val(),
		cIdContratoDefinitivo:$("#cIdContratoDefinitivo").val(),
		nConsecutivoMod:$("#nConsecutivoMod").val(),
		nIdcontratoMod:$("#nIdContModCap4").val()
	};
	return data0;
}
function hideAndShowButtonDevolverPrecom(){
	$("#imgDevolverpreCompromisoPluriContrato").hide();
	if($("#nEstatus").val()==3){
		$("#imgDevolverpreCompromisoPluriContrato").show();
	}
}
function hideAndShowButtonAutPrecom(){
	$("#imgAprobarCompromisoPluriContrato").hide();
	if($("#nEstatus").val()==3 && ( (roles.indexOf("ADMIN_RECMAT") >= 0) ||(roles.indexOf("JEFES") >= 0) ) ){
		$("#imgAprobarCompromisoPluriContrato").show();
	}
}
function hideAndShowButtonPrecomprometer(){
	$("#imgAprobarpreCompromisoPluriContrato").hide();
	$("#edit").hide();
	if($("#nEstatus").val()==2){
		$("#imgAprobarpreCompromisoPluriContrato").show();
		$("#edit").show();
	}
}
function comprometer(){
	if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 ) { 
		swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
		return;
	}
	$("#tipoOperacion").val(12);
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectPrecom();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$.unblockUI();
				if(j[0].RESPUESTA || j[0].RESPUESTA=="true"){
					swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosCaratula);
					hideAndShowButtonDevolverPrecom();
					hideAndShowButtonAutPrecom();
					hideAndShowButtonPrecomprometer();
				}else{
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}
		}, error: function() {
			$.unblockUI();
		}
	});
}