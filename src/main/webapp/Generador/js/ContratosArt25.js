function showAndHideTabs(){
	$("#NuevasEpsContratoArt25").hide();
	$("#AmpliacionContratoArt25").hide();
	if(tabb==0 || tabb==1){
		$("#PartidasContratoArt25").hide();
		$("#PresupuestoContratoArt25").hide();
		$("#PrecompromisoContratoArt25").hide();
		$("#NuevasEpsContratoArt25").hide();
		$("#AmpliacionContratoArt25").hide();
	}else{
		$("#PartidasContratoArt25").show();
		$("#PresupuestoContratoArt25").show();
		$("#PrecompromisoContratoArt25").show();
		$("#NuevasEpsContratoArt25").show();
		$("#AmpliacionContratoArt25").show();
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
				swal("No se hizo el cambio de centro contable y unidad ejecutora",{icon:"warning",button: "Cerrar"});
			}else{
				$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
			}
		}
	});
}
function pestanaConsulta(){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoAreaResponsable,"cIdUnidadEjecutora");
				llenaCombo(j[0].catalogoEstatus,"estatusContratoArt25");
				llenaCombo(j[0].catalogoTipoContrato,"catTipoContratoArt25");
				$("#cIdUnidadEjecutora").val(UE_UsuarioActual);
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
		tipoOperacion:$("#tipoOperacion").val(),
		cIdContratoDefinitivo:$("#cIdContratoDefinitivo").val(),
		cEjercicio:$("#cEjercicio").val(),
		cClaveInterna:$("#ClaveInterna").val(),
		cIdClaveEgresos:$("#ClaveEP").val(),
		cIdContrato:$("#cIdContratoDefinitivo").val(),
		
		cIdEntidadContable:$("#cIdEntidadContable").val(),
		cIdTipocontrato:$("#cIdTipoContrato").val(),
		
		cIdUnidadEjecutora:$("#cIdUnidadEjecutoraEP").val()
		
	};
	return data0;
}
function pestanaConsultaDatePickerFechas(){
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
}
function searchContracts(){
	var ue=" '"+$("#cIdUnidadEjecutora").val()+"' ";
	var cadCont="'"+$("#contratoArt25").val()+"'";
	var funcion=" fn_mConsultaContratosArt25 ";
	var nEstatus="'"+$("#estatusContratoArt25").val()+"'";
	if($("#estatusContratoArt25").val()==0){
		nEstatus="'1,2,3,4'";
	}
	
	var tipoCont="'"+$("#catTipoContratoArt25").val()+"'";
	if($("#catTipoContratoArt25").val()==""){
		tipoCont="'CR,CS'";
	}
	var fechaIni="'"+$("#fInicio").val()+"'";
	var fechaFin="'"+$("#fFin").val()+"'";
	$('#tblConsulta').dataTable().fnClearTable();
	oTableConsultaContratos = $("#tblConsulta").dataTable({
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (ue+","+cadCont+","+nEstatus+","+tipoCont +","+fechaIni +","+fechaFin) +")" ) ,
		//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mContratoArt25&qw="+qw,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cURL", bVisible: false },
			{ sName: "cIdUnidadEjecutora" },
			{ sName: "cIdContratoDefinitivo" },
			{ sName: "cIdTipoContrato" },
			{ sName: "cConceptoContrato" },
			{ sName: "cIdUsuarioCreacion" },
			{ sName: "cEstado" },
			{ sName: "cIdRFC" },
			{ sName: "cRazonSocial" }
			
		]
		,fnInitComplete: function() {
			if($('#tblContratosAprobados >tbody >tr').length>0){
				oTableContratosAprovados.fnAdjustColumnSizing();
			}
		}
	});
}
function mostrar(){
	var consulta = "'"+$("#cIdDefinitivo").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdRFC").val()+"','"+$("#cDescripcion").val()+"'";
	var parametros="";
	var arrayContract=new Array();
	var msgTable="NO HAY CONTRATOS FORMALIZADOS EN EL EJERCICIO ANTERIOR ";
	$('#tblNuevoContratoArt25').dataTable().fnClearTable();
	oTable=$("#tblNuevoContratoArt25").dataTable({
		sScrollX: "100%",
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
			{ sName: "cIdUnidadEjecutora",bSortable: false },
			{ sName: "cIdContratoDefinitivo",bSortable: false   },
			{ sName: "cNoContratoCNET",bSortable: false   },					
			{ sName: "cIdRFC",bSortable: false 	},
			{ sName: "cRazonSocial",bSortable: false },
			{ sName: "cConceptoContrato",bSortable: false },
			{ sName: "cDB",bSortable: false,bVisible: false  }
			
		]
	});
	$.getJSON("../../catalogos/SelectJson.jsp",
		{
			Tabla : "CONTRACTS_ART25",
			Param : parametros,
			Campos : consulta,
			MaxReg : "",
			ajax : 'false'
		},
		function(data) {
			for( var i = 0; i < data.length; i++ ) {
				arrayContract [i]=[data[ i ].Col0, data[ i ].Col1, data[ i ].Col2, data[ i ].Col3,data[ i ].Col4,data[ i ].Col5,data[ i ].Col6 ];
				
			}
			if(data.length>0){
				$("#tblNuevoContratoArt25").dataTable().fnAddData(arrayContract);
				$("#tblNuevoContratoArt25").dataTable().fnAdjustColumnSizing();
			}
		}
	);
	
}
function consultaDatosMigrados(){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				vaciarJsonAInputs(j[0].datosContratoArt25);
				$.unblockUI();
		}, error: function() {
			$.unblockUI();
		}
	});
}
//Funciones de la pestaña partidas
function showTables(){
	hideDivs();
	if($("#cIdTipoContrato").val()=='CS' || $("#esCucopGasolina").val()!=0){
		if(parseInt($("#nEsAbierto").val(),10)==0){
			$("#tblLineas-serv").show();
			tipo=1;
			showLinesAdded();
		}else{
			$("#divLineasServ-ContAbierto").show();
			showLinesAddedServContAbierto();
			tipo=2;
		}
	}else{
		if(parseInt($("#nEsAbierto").val(),10)==0){
			$("#divLineasBienes").show();
			tipo=3;
			showLinesAddedBienes();
		}else{
			$("#divLineasBienes-ContAbierto").show();
			tipo=4;
			showLinesAddedBienesContAbierto();
		}
	}
	
}
function hideDivs(){
	$("#divLineasServ-ContAbierto").css("display","none");
	$("#divLineasBienes-ContAbierto").css("display","none");
	$("#divLineasBienes").css("display","none");
	$("#tblLineas-serv").css("display","none");
}
function showLinesAdded(){
	var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";

	oTableLineasServ = $("#tblLineas-serv").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		//sScrollY : "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPartidasContArt25&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 1, "asc" ]] ,
		aoColumns: [
			{sName: "cIdContratoDefinitivo",bVisible: false},
			{sName: "cIdUnidadRequi"},
			{sName: "nIdLineaConsolidado"},
			{sName: "cIdSubPartida"},
			{sName: "cIdCABM"},
			{sName: "cDescripcionAdicional"},
			{sName: "nCantidadMinima"},
			{sName: "nCantidadMAx",bVisible: false},
			{sName: "mMontoMinimo"},
			{sName: "mMontoMaximo",bVisible: false},
			{sName: "nPorcentajeIVA"},
			{sName: "mMontoNetoLineaInput"},
			{sName: "mMontoNetoMinimo",bVisible: false},
			{sName: "mMontoNetoLineaMaximo	",bVisible: false}
		]
	});
}
function showLinesAddedBienes(){
	var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";

	oTableLineasBienes = $("#tblLineas-bienes").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		//sScrollY : "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPartidasContArt25&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 1, "asc" ]] ,
		aoColumns: [
			{sName: "cIdContratoDefinitivo",bVisible: false},
			{sName: "cIdUnidadRequi"},
			{sName: "nIdLineaConsolidado"},
			{sName: "cIdSubPartida"},
			{sName: "cIdCABM"},
			{sName: "cDescripcionAdicional"},
			{sName: "nCantidadMinimaInput"},
			{sName: "nCantidadMAx",bVisible: false},
			{sName: "mMontoMinimo"},
			{sName: "mMontoMaximo",bVisible: false},
			{sName: "nPorcentajeIVA"},
			{sName: "mMontoNetoLineaInput"},
			{sName: "mMontoNetoMinimo",bVisible: false},
			{sName: "mMontoNetoLineaMaximo	",bVisible: false}
		]
	});
}
function showLinesAddedBienesContAbierto(){
	var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";

	oTableLineasBienesContAbierto = $("#tblLineas-bienesContAbierto").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		//sScrollY : "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPartidasContArt25&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 1, "asc" ]] ,
		aoColumns: [
			{sName: "cIdContratoDefinitivo",bVisible: false},
			{sName: "cIdUnidadRequi"},
			{sName: "nIdLineaConsolidado"},
			{sName: "cIdSubPartida"},
			{sName: "cIdCABM"},
			{sName: "cDescripcionAdicional"},
			{sName: "nCantidadMinimaInput"},
			{sName: "nCantidadMAxInput"},
			{sName: "mMontoMinimo"},
			{sName: "mMontoMaximo",bVisible: false},
			{sName: "nPorcentajeIVA"},
			{sName: "mMontoNetoLineaInput"},
			{sName: "mMontoNetoMinimo",bVisible: false},
			{sName: "mMontoNetoLineaMaximoInput	"}
		]
	});
}
function showLinesAddedServContAbierto(){
	var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";

	oTableLineasServContAbierto = $("#tblLineas-servContAbierto").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		//sScrollY : "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPartidasContArt25&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 1, "asc" ]] ,
		aoColumns: [
			{sName: "cIdContratoDefinitivo",bVisible: false},
			{sName: "cIdUnidadRequi"},
			{sName: "nIdLineaConsolidado"},
			{sName: "cIdSubPartida"},
			{sName: "cIdCABM"},
			{sName: "cDescripcionAdicional"},
			{sName: "nCantidadMinima"},
			{sName: "nCantidadMAx",bVisible: false},
			{sName: "mMontoMinimo"},
			{sName: "mMontoMaximo"},
			{sName: "nPorcentajeIVA"},
			{sName: "mMontoNetoLineaInput"},
			{sName: "mMontoNetoMinimo",bVisible: false},
			{sName: "mMontoNetoLineaMaximoInput	"}
		]
	});
}
//funciones de la pestaña presupuesto

function fnClickAddRowC(nTipoOperacion) {
	if($('#ep').val()==''){
		swal("Favor de seleccionar una EP",{icon:"info",button: "Cerrar"});
		return;
	}
	
	$.blockUI({message: "Procesando espere ......"});
	var vep = $('#ep').val();
	$('#ClaveEP').val($('#ep').val());
	$("#tipoOperacion").val(nTipoOperacion);
	var tmp = vep.lastIndexOf( "\." );
	var uEje= vep.substring( tmp - 3, tmp);
	var cint = vep.substring( tmp -3 );
	$("#cIdUnidadEjecutoraEP").val(uEje);
	$("#ClaveInterna").val(cint);
	if($("#nEsDescentralizado").val()==0){
		$("#cIdUnidadEjecutoraEP").val($("#cIdUnidadEjecutoraCont").val());
	}
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				swal(j[0].MENSAJE,{icon:"info",button: "Cerrar"});
				loadClavesPresupuestalesContrato();
				$('#ep').val('');
				$.unblockUI();
			}, error: function() {
				$.unblockUI();
			}
	});
}
function buscaClavePresupuestal(){
	window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
		+ '&cIdDocumento=' + $('#cIdContratoDefinitivo').val()+ '&isPlurianual=0'
		, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px, top=10px');
}
function loadClavesPresupuestalesContrato(){
	 var qw = " cEjercicio = '" + $("#cEjercicio").val() +
	 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
	 "' AND cIdContrato = '" + $("#cIdContratoDefinitivo").val()+"'";
	
	oTableClaves=$('#dt_clavepresup').dataTable( {
		bPaginate: false,
		bLengthChange: false,
		bFilter: false,
		bInfo: false,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos en la tabla",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0"
			
		},
		bAutoWidth: true,
		bScrollCollapse: true,
		bJQueryUI: true,
		bDestroy : true,
		bServerSide: true,   
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
		aoColumns: [
			{ sName: "nIdClaveEgresos"},
			{ sName: "ClaveInterna" }
		]
	}) ;
}
function validaCondicionesIniciales(){
	$("#btnIdClaveEgresos").show();
	$("#btnAgregarEPContArt25").show();
	$("#imgAprobarContArt25").show();
	$("#imgDevolverContArt25").show();
	if (parseInt($("#nIdEstatus").val(),10)== 3 || parseInt($("#nIdEstatus").val(),10)== 4 || parseInt($("#nIdEstatus").val(),10)== 1){
		$("#imgDevolverContArt25").hide();
	}
	if (parseInt($("#nIdEstatus").val(),10)> 1 ){
		$("#btnIdClaveEgresos").hide();
		$("#btnAgregarEPContArt25").hide();
		$("#imgAprobarContArt25").hide();
	}
}
function guardaContrato(){
	$("#tipoOperacion").val(4);
	exceAjaxPresupuesto();
}
function devuelveContrato(){
	$("#tipoOperacion").val(5);
	exceAjaxPresupuesto();
}
function exceAjaxPresupuesto(){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				vaciarJsonAInputs(j[0].datosContratoArt25);
				swal(j[0].MENSAJE,{icon:"info",button: "Cerrar"});
				validaCondicionesIniciales();
				$.unblockUI();
			}, error: function() {
				$.unblockUI();
			}
	});
}
//	Funciones de la pestaña precompromiso

//Funciones para la pestaña nuevas EP´S
function loadClavesPresupuestalesContrato(){
	var qw = " 1 = 1  AND cIdContratoDefinitivo = '"+ $("#cIdContratoDefinitivo").val()+"'";
	oTableClaves=$('#dt_clavepresup').dataTable( {
			bPaginate: false,
			bLengthChange: false,
			bFilter: false,
			bInfo: false,
			bAutoWidth: false,
			sScrollY: 100,
			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,   
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
			aoColumns: [
				{ sName: "nIdClaveEgresos"},
				{ sName: "ClaveInterna" }
				]
	}) ;
}

//Amplicaiones
function muestraLineasBienes(){
	var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'" ;
	oTableLineasBienes = $("#tblLineasBienes").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		sScrollY : "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPartidasContArt25&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 2, "asc" ]],
		aoColumns: [
			{sName: "cIdContratoDefinitivo",bVisible: false,bSearchable: false},
			{sName: "cIdRFC",bVisible: false,bSearchable: false},
			{sName: "nIdLineaConsolidado"},
			{sName: "descripcion"},
			{sName: "nCantidadMinima"},
			{sName: "nCantidadMAx"},
			{sName: "nCantidadDisponible"},
			{sName: "nCantidadAmpliacion"}
			
			
		]
	});
}
function muestraLineasServ(){
	var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'" ;
	oTableLineasServ = $("#tblLineasServ").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		sScrollY : "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPartidasContArt25&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 2, "asc" ]],
		aoColumns: [
			{sName: "cIdContratoDefinitivo",bVisible: false,bSearchable: false},
			{sName: "cIdRFC",bVisible: false,bSearchable: false},
			{sName: "nIdLineaConsolidado"},
			{sName: "descripcion"},
			{sName: "mMontoMinimo"},
			{sName: "mMontoNetoLineaMaximo"},
			{sName: "mMontoNetoDisponible"},
			{sName: "mMontoNetoAmpliacion"}
			
			
		]
	});
}
function initDataTable(){
	$("#cEstadoAmpliacion").val("");
	$("#nIdAmpliacion").val("");
	oTablaAmpliaciones=$('#tblAmpliaciones').dataTable({         
		bAutoWidth : true,
		bScrollCollapse: true,
		bDestroy: true,		
		iDisplayLength: 25,
		sScrollX: "100%",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mContratoArt25Ampliaciones('"+$("#cIdContratoDefinitivo").val()+"','"+$("#cIdUnidadEjecutoraSolicitud").val()+"')",
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		//aaSorting: [[ 1, "asc" ]] ,
		aoColumns: [
			{ sName: "nIdAmpliacion", bVisible:false },
			{ sName: "cIdOficioPrecompromiso" },
			{ sName: "cEstado" },
			{ sName: "cantidadAmpliacion" },
			{ sName: "montoAmpliacion" },
			{ sName: "cIdUsuarioCreacion" },
			{ sName: "cIdUsuarioCancelacion" },
			{ sName: "ConsecutivoPRECOMP" , bVisible:false},
			{ sName: "C_FOLIO_PRE" , bVisible:false},
			{ sName: "cNumCuentaDisp" , bVisible:false}
			//{ sName: "Guarda" }
		]
	});
	
}
function ocultarBotones(){
	$("#imgAprobarAmpliacion").hide();
	$("#imgDevolverAmpliacionPres").hide();
	$("#imgAprobarpreCompromisoContAmp").hide();
	$("#imgAprobarCompromisoContAmp").hide();
	$("#imgDevolverAmpliacion").hide();
	$("#imgEliminarAmpliacion").hide();
	
	$("#btnEditarAmpliacionDetalle").hide();
	$("#nIdClaveEP").hide();
	$("#btnAgregarAmpliacionDetalle").hide();
}