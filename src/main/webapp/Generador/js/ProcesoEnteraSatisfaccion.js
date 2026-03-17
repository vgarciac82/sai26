var idOper;
function onLoadPlantilla(id_oper) {//Carga Plantilla
	idOper=id_oper;
	var resp=true;
	if(id_oper>1 && id_oper<6 ){
		parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
	}
	switch (parseInt(id_oper,10)){
		case 1://Captura
			informationQuery(id_oper);
			break;
		case 2://valida
			informationQuery(id_oper);
			break;
		case 3://Espera de firma
			informationQuery(id_oper);
			parent.document.getElementById("pb_save").disabled = false;
			break;
		case 4://Aceptación de testigo 1
			informationQuery(id_oper);
			break;
		case 5://Aceptación de testigo 2
			informationQuery(id_oper);
			break;
		case 6://Consulta
			informationQuery(id_oper);
			hideResp();
			break;
		default:
			resp=false;
			break;
	}
	
	return resp;
	
}
function onSubmit(id_oper) {//clic boton Guardar
	idOper=id_oper;
	$.blockUI({message: "Procesando ......"});
	var resp=false;
	if(id_oper>1 && id_oper<6 ){
		parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
	}
	switch (parseInt(id_oper,10)){
		case 1://Captura
			var p = window.parent;
			p.gestion.setFolio($("#cFolio").val());
			p.gestion.setOperador($("#OPERADOR").val());
			p.gestion.setFechaDocumento($("#FECHA_DOCUMENTO").val()); 
			p.gestion.setEjercicioFiscal($("#cEjercicio").val());
			
			validateInfo(id_oper);
			resp=true;
			break;
		case 2://valida
			//Si se devuelve el proceso guardar las observaciones
			if(!document.getElementById("radioNoAut").checked && !document.getElementById("radioSiAut").checked){
				swal("Favor de contestar la pregunta, ¿Los datos capturados son correctos?.",{icon:"warning",button: "Cerrar"});
				$.unblockUI();
				return false;
			}
			if(document.getElementById("radioNoAut").checked && $("#cObservations").val()==""){
				swal("Favor de capturar las observaciones.",{icon:"warning",button: "Cerrar"});
				$.unblockUI();
				return false;
			}else{
				saveValidate(5,idOper);
				resp=true;
			}
			break;
		case 3://Espera de firma
			resp=true;
			parent.document.getElementById("pb_send").disabled = false;
			$.unblockUI();
			break;
		case 4://Aceptación de testigo 1
			resp=true;
			$.unblockUI();
			break;
		case 5://Aceptación de testigo 2
			resp=true;
			$.unblockUI();
			break;
		case 6://Consulta
			resp=false;
			$.unblockUI();
			break;
		default:
			resp=false;
			$.unblockUI();
			break;
	}
	return resp;
}
function ResponsableSiguiente(id_oper) {
	var respSig="CONSULTA_ENTERASATISFACCION";
	switch (parseInt(id_oper,10)){
		case 1:// Captura
			respSig="VALIDA_ENTERASATISFACCION";
			break;
		case 2:// valida
			if(devolucion){
				respSig= "CAPTURA_ENTERASATISFACCION";
			}else{
				respSig="FIRMA_ENTERASATISFACCION";	
			}
			break;
		case 3:// Espera de firma
			if(devolucion){
				respSig= "CAPTURA_ENTERASATISFACCION";
			}else{
				respSig="CONSULTA_ENTERASATISFACCION";
				if($("#nServicioPrestado").val()==2){
					respSig="TESTIGO1_ENTERASATISFACCION";	
				}
			}
			break;
		case 4://Aceptación de testigo 1
			respSig="TESTIGO2_ENTERASATISFACCION";
			break;
		case 5://Aceptación de testigo 2
			respSig="CONSULTA_ENTERASATISFACCION";
			break;
		case 6://Consulta
			respSig="CONSULTA_ENTERASATISFACCION";
			break;
		default:
			respSig="CONSULTA_ENTERASATISFACCION";
			break;
	}
	return respSig;
}
function OperacionSiguiente(id_oper) {
	var operSiguiente="consulta_enterasatisfaccion";
	switch (parseInt(id_oper,10)){
		case 1:// Captura
			operSiguiente="valida_enterasatisfaccion";
			break;
		case 2:// valida
			operSiguiente="firma_enterasatisfaccion";   //"firma_enterasatisfaccion";
			if(devolucion){
				operSiguiente= "captura_enterasatisfaccion";
			}
			break;
		case 3:// firma
			if(devolucion){
				operSiguiente= "captura_enterasatisfaccion";
			}else{
				operSiguiente="consulta_enterasatisfaccion";
				if($("#nServicioPrestado").val()==2){
					operSiguiente="testigo1_enterasatisfaccion";
				}	
			}
			break;
		case 4: //testigo 1
			operSiguiente= "testigo2_enterasatisfaccion";
			break;
		case 5: //testigo 2                  
			operSiguiente= "consulta_penaconvencional";
			break;
		case 5: //consulta                 
			operSiguiente= "consulta_penaconvencional";
			break;
		default:
			operSiguiente="consulta_penaconvencional";
			break;
	}
	return operSiguiente;
}
function onPostDisplay(id_oper) {
		return true;
}// fin onPostDisplay
function onPostSubmit(id_oper) {// clic boton enviar
	var resp=false;
	parent.document.getElementById("pb_send").disabled = true;
	switch (parseInt(id_oper,10)){
		case 1://Captura
			idOper=2;
			sendProcess(6,idOper);
			resp=true;
			break;
		case 2://valida
			idOper=3;
			if(devolucion){
				idOper=1;
			}
			sendProcess(6,idOper);
			resp=true;
			break;
		case 3://Firma
			idOper=4;
			//sendProcess(6,idOper);
			resp=true;
			break;
		case 4://Aceta testigo 1
			idOper=5;
			//sendProcess(6,idOper);
			resp=true;
			break;
		case 5://Aceta testigo 2
			idOper=6;
			//sendProcess(6,idOper);
			resp=true;
			break;
		case 6://Consulta
			resp=false;
			break;
		default:
			resp=false;
			break;
	}
	return resp;
}
// fin  onPostSubmit
//-------------Funciones de Caso Fin --------------------------------------------------------------------------------------------------------
/** */
function searchContract(){
	myModal = new bootstrap.Modal(document.getElementById('modalContractsServices'), {
	  keyboard: false
	})
	myModal.show();
	clearParams();
	queryTableContracts();
}
function clearParams(){
	$("#cIdDefinitivo").val('');
	$("#cRazonSocial").val('');
	$("#cNumCNET_Search").val('');
}
function queryTableContracts(){
	var param = "'"+$("#cIdUnidadEjecutora").val()+"'";
	var funcion="searContractServices";
	var cadCont="''";
	var cadProveedor="''";
	var cadContCNET="''";
	if($("#cIdDefinitivo").val()!=""){
		cadCont="'"+$("#cIdDefinitivo").val()+"'";
	}
	if($("#cRazonSocial").val()!=""){
		cadProveedor="'"+$("#cRazonSocial").val()+"'";
	}
	if($("#cNumCNET_Search").val()!=""){
		cadContCNET="'"+$("#cNumCNET_Search").val()+"'";
	}
	$('#tblConsulta').dataTable().fnClearTable();
	oTableConsulta = $("#tblConsulta").dataTable({
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
			sEmptyTable: "No hay partidas de contrato, favor de seleccionar un contrato SAI",
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param+","+cadCont+","+cadProveedor+","+cadContCNET) +")" ) ,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cIdContratoDefinitivo" },
			{ sName: "cIdRFC" },
			{ sName: "cNoContratoCNET" },
			{ sName: "cRazonSocial" },
			{ sName: "cConceptoContrato" },
			{ sName: "cIdTipoContrato" },
			{ sName: "diaFormalizacion" , bVisible: false},
			{ sName: "mesFormalizacion" , bVisible: false},
			{ sName: "anioFormalizacion" , bVisible: false}
		]
		,fnInitComplete: function() {
			if($('#tblConsulta >tbody >tr').length>0){
				oTableConsulta.fnAdjustColumnSizing();
			}
		}
	});
}
function addParams(aData){
	$("#pedidoContratoCompromiso").val(aData[0]);
	$("#lblContratoCNET").val(aData[2]);
	$("#cNumCNET").val(aData[2]);
  	$("#lblProveedor").val(aData[3]);
	$("#cIdRFC").val(aData[1]);
  	$("#cIdTipoContrato").val(aData[5]);
  	$("#lblDia").val(aData[6]);
  	$("#lblMesFormalizado").val(aData[7]);
  	$("#lblEjercicio").val(aData[8]);
  	$("#lblMes").val($('select[name="catMesPago"] option:selected').text());
}
function searchFirmante(idFirmante){
	myModalFirmante = new bootstrap.Modal(document.getElementById('modalFirmante'), {
	  keyboard: false
	})
	myModalFirmante.show();
	clearParamsFirmante();
	$("#nIdFirmante").val(idFirmante);
	queryTableEmpleados();
	oTableConsultaFirmante.fnAdjustColumnSizing();
}
function clearParamsFirmante(){
	$("#nNumEmpl_serch").val('');
	$("#cNombreEmpl_serch").val('');
	$("#cPrimerAp_serch").val('');
	$("#cSegundoAp_serch").val('');
	$("#nIdFirmante").val('');
}
function queryTableEmpleados(){
	var qw="1=1";
	if($("#nNumEmpl_serch").val()!=""){
		qw+=" and cNumeroEmpleado="+$("#nNumEmpl_serch").val();
	}
	if($("#cNombreEmpl_serch").val()!=""){
		qw+=" and NOMBREN LIKE'%25"+$("#cNombreEmpl_serch").val()+"%25'";
	}
	if($("#cPrimerAp_serch").val()!=""){
		qw+=" and NOMBREP LIKE'%25"+$("#cPrimerAp_serch").val()+"%25'";
	}
	if($("#cSegundoAp_serch").val()!=""){
		qw+=" and NOMBREM LIKE'%25"+$("#cSegundoAp_serch").val()+"%25'";
	}
	$('#tblConsultaFirmante').dataTable().fnClearTable();
	oTableConsultaFirmante = $("#tblConsultaFirmante").dataTable({
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
			sEmptyTable: "No hay partidas de contrato, favor de seleccionar un contrato SAI",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catEmpleados&qw=" + qw,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cNumeroEmpleado" },
			{ sName: "NOMBREN" },
			{ sName: "NOMBREP" },
			{ sName: "NOMBREM" },
			{ sName: "DESCRIPCION_PUESTO" },
			{ sName: "U_NOMBRE" , bVisible: false}
		]
		,fnInitComplete: function() {
			if($('#tblConsultaFirmante >tbody >tr').length>0){
				oTableConsultaFirmante.fnAdjustColumnSizing();
			}
		}
	});
}
function addParamsFirmantes(aData){
	if($("#nIdFirmante").val()==0){
  		$("#firmanteResponsable").val(aData[5]);
		$("#lblNombreJefeDirecto").val(aData[5]);
		$("#lblPuestoFirmante").val(aData[4]);
	  	$("#numFirmanteResponsable").val(aData[0]);
  	}else if($("#nIdFirmante").val()==1){
  		$("#testigo1").val(aData[5]);
		$("#lblTestigo1").val(aData[5]);
		$("#lblPuestoTestigo1").val(aData[4]);
	  	$("#numEmpTestigo1").val(aData[0]);
  	}else if($("#nIdFirmante").val()==2){
  		$("#testigo2").val(aData[5]);
		$("#lblTestigo2").val(aData[5]);
		$("#lblPuestoTestigo2").val(aData[4]);
	  	$("#numEmpTestigo2").val(aData[0]);
  	}
}
function mostrarDivTestigos(){
	$("#divFirmantesTestigos").hide();
	$("#divAcordionActa").hide();
	if($("#nServicioPrestado").val()==2){
		$("#divFirmantesTestigos").show();
		$("#divAcordionActa").show();
		if($("#processInDB").val()==1){
			muestraDatos();
			muestraDatosServNoPrest();
		}
	}
}
function informationQuery(id_oper){
	var object=fillObjectData(1,id_oper);
	$.ajax({url: "../../ProcesoEnteraSatisfaccion" , type:'post' , async: true
		,data:object
		,dataType: 'json'
		,beforeSend : function() {
           $.blockUI({message: "Procesando ......"});
        }
		, success: function(j){
			if(!j[0].RESPUESTA ||j[0].RESPUESTA=="false"){
				swal(j[0].MENSAJE,{icon:"error",button: "Cerrar"});	
			}else{
				$("#textAreaHechos").val("Declara el C. "+$("#firmanteResponsable").val()+", que siendo aproximadamente las ......., todo lo cual se asienta para que conste.");
				llenaCombo(j[0].catMesPago,"catMesPago");
				llenaCombo(j[0].catLugarPrestServicio,"prestacionServicio");
				llenaCombo(j[0].catPartContrato,"nPartidaCont");
				vaciarJsonAInputs(j[0].infoGuardada);
				mostrarDivTestigos();
				showObservations();
			}
		}
		,error: function(xhr, status, error) {
			console.log(error);
			swal("some error",{icon:"error",button: "Cerrar"});
		}
		,complete: function () {
        	$.unblockUI();
        }
	});
}
function fillObjectData(operation,id_oper){
	var data0= {
		cIdContratoDefinitivo:$("#pedidoContratoCompromiso").val(),
		cFolio: $("#cFolio").val(),
		nIdEstate:id_oper,
		cNumCNET:$("#cNumCNET").val(),
		cIdRFC:$("#cIdRFC").val(),
		nIdLinea:$("#nPartidaCont").val(),
		nIdPeriodoPago:$("#catMesPago").val(),
		nCentroTrabajo:$("#prestacionServicio").val(),
		nNumEmpFirmante:$("#numFirmanteResponsable").val(),
		cFolioFirmante:$("#lblFolioJefe").val(),
		nServPrestEnteraSatisfaccion:$("#nServicioPrestado").val(),
		nServicioEnteraSatisfaccion:$("#nServicioEnteraSatisfaccion").val(),
		idCaso:$("#idCaso").val(),
		//Anexo
		nDiaformalizacion:$("#lblDia").val(),
		cMesFormalizacion:$("#lblMesFormalizado").val(),
		cAnioFormalizacion:$("#lblEjercicio").val(),
		cDeclaraccion:$("#lblDeclaracion").val(),
		cDescripcionServicio:$("#lblSatisfaccion").val(),
		cInmueble:$("#lblInmueble").val(),
		cEjercicioPago:$("#lblEjercioPago").val(),
		operation:operation
	};
	return data0;
}
function fillObjectDataValidate(operation,id_oper){
	var data0= {
		cIdContratoDefinitivo:$("#pedidoContratoCompromiso").val(),
		cFolio: $("#cFolio").val(),
		nIdEstate:id_oper,
		cNumCNET:$("#cNumCNET").val(),
		cIdRFC:$("#cIdRFC").val(),
		cObservacionesTramite:$("#cObservations").val(),
		nServPrestEnteraSatisfaccion:$("#nServicioPrestado").val(),
		nServicioEnteraSatisfaccion:$("#nServicioEnteraSatisfaccion").val(),
		idCaso:$("#idCaso").val(),
		operation:operation
	};
	return data0;
}
function validateInfo(id_oper){
	var msg="";
	var token="";
	if($("#cNumCNET").val()==""){
		msg="Favor de seleccionar el número de contrato.";
		token="\n";
	}
	if($("#catMesPago").val()=="0"){
		msg+=token+"Favor de seleccionar el mes de pago.";
		token="\n";
	}
	if($("#firmanteResponsable").val()==""){
		msg+=token+"Favor de seleccionar el firmante.";
		token="\n";
	}
	if($("#nServicioPrestado").val()=="0"){
		msg+=token+"Favor de seleccionar si el servicio se presto a entera satisfacción.";
		token="\n";
	}
	if(null==$("#nPartidaCont").val() || $("#nPartidaCont").val()=="0"){
		msg+=token+"Favor de seleccionar una partida de contrato.";
		token="\n";
	}
	if(null==$("#prestacionServicio").val() || $("#prestacionServicio").val()=="0"){
		msg+=token+"Favor de seleccionar el lugar de prestación del servicio.";
		token="\n";
	}
	if(msg!=""){
		$.unblockUI();
		swal({
			title: "",
			text: msg,
			icon: "warning",
			closeOnClickOutside: false,
			buttons: {
				confirm : "Cerrar"
			},
		}).then((continuar) => {
			return false;
		});
	}else{
		saveInfo(2,id_oper);
		return true;
	}
}
function saveInfo(operation,id_oper){
	var resp=true;
	var object=fillObjectData(operation,id_oper);
	if($("#nServicioPrestado").val()=="2"){
		object.cDescripcion1=$("#lblLucharHechos").val();
		object.cDescripcion2=$("#lblFechaHechos").val();
		object.nNumEmpTestigo1=$("#numEmpTestigo1").val();
		object.cFolioTestigo1=$("#lblFolioTestigo1").val();
		object.nNumEmpTestigo2=$("#numEmpTestigo2").val();
		object.cFolioTestigo2=$("#lblFolioTestigo2").val();
		object.cLugarAdscripcion=$("#lblPromotoriaHechos").val();
		object.cDescripcionHechos=$("#textAreaHechos").val();
		object.cDescripCierreHechos=$("#lblCierreActa").val();
		object.cPuestoFirmante=$("#lblPuestoFirmante").val();
		object.cPuestoTestigo1=$("#lblPuestoTestigo1").val();
		object.cPuestoTestigo2=$("#lblPuestoTestigo2").val();
		
	}
	$.ajax({url: "../../ProcesoEnteraSatisfaccion" , type:'post' , async: false
		,data:object
		,dataType: 'json'
		,beforeSend : function() {
           $.blockUI({message: "Procesando ......"});
        }
		, success: function(j){
			if(j[0].RESPUESTA=="false" || !(j[0].RESPUESTA)){
				parent.document.getElementById("pb_send").disabled = false;
				swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "warning",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						resp=true;
				});
			}else{
				$("#processInDB").val(1);
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
					},
				}).then((continuar) => {
					parent.document.getElementById("pb_send").disabled = false;
				});
			}
		}
		,error: function() {
			console.log(error);
			swal("some error",{icon:"error",button: "Cerrar"});
		}
		,complete: function () {
        	$.unblockUI();
        }
	});
	return resp;
}
function fillSelects(){
	querySelectPost("catalogoPartidasContrato", "nPartidaCont", {async: false});
	querySelectPost("catalogoLugarPrestacion", "prestacionServicio", {async: false});
}
function addDato(){
	var valor=$('select[name="catMesPago"] option:selected').text();
	$("#lblMes").val(valor);
}
function showFormItem(idTbl){
	if($("#processInDB").val()==0){
		swal("Primero dar clic en el botó Guardar y luego agregar registros",{icon:"warning",button: "Cerrar"});
	}else{
		clearParamItems();
		$("#idTbl").val(idTbl);
		$("#cFolio_search").val($("#cFolio").val());
		myModalItems = new bootstrap.Modal(document.getElementById('modalItems'), {
			keyboard: false
		})
		myModalItems.show();
	}		
}
function clearParamItems(){
	$("#ncantidadElementos_add").val("");
	$("#mMontoTotal_add").val("");
	$("#nCantidadDias_add").val("");
	$("#cDescripcion_add").val("");
}
function addItem(){
	$.blockUI({message: "Procesando ......"});
	if($("#idTbl").val()==1){//Servicio no prestado
		queryFormPost('addItemServicioNoPrestado', {async: false, 
			callback : function() {
				clearParamItems();
				muestraDatosServNoPrest();
				swal("Datos Guardados.",{icon:"success",button: "Cerrar"});
				$.unblockUI();
			}
		});
	}else if($("#idTbl").val()==2){//Servicio prestado con deficiencia
		queryFormPost('addItemServicioConDeficiencia', {async: false,
			callback : function() {
				muestraDatos();
				clearParamItems();
				swal("Datos Guardados.",{icon:"success",button: "Cerrar"});
				$.unblockUI();
			}
		});
	}else{
		swal("Operación desconocida.",{icon:"warning",button: "Cerrar"});
		$.unblockUI();
	}
}
function deleteItems(idTbl){
	$("#cFolio_search").val($("#cFolio").val());
	if(idTbl==1){
		queryFormPost('mDetalleServicioNoPrestadoDelete', {async: false, 
			callback : function() {
				muestraDatosServNoPrest();
				swal("Datos borrados.",{icon:"success",button: "Cerrar"});
			}
		});
	}else if(idTbl==2){
		queryFormPost('mDetalleServicioDeficienteDelete', {async: false,
			callback : function() {
				muestraDatos();
				swal("Datos borrados.",{icon:"success",button: "Cerrar"});
			}
		});
	}else{
		swal("Operación desconocida.",{icon:"warning",button: "Cerrar"});
	}
}
function muestraDatosServNoPrest(){
	var qw="1=1 and cFolio='"+$('#cFolio').val()+"'";
	oTabletblDetalleInasistencia = $("#tblDetalleInasistencia").dataTable({
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mDetalleServicioNoPrestado&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{sName: "nConsecutivo", bVisible: false },
			{sName: "ncantidadElementos"},
			{sName: "mMontoTotal"},
			{sName: "nCantidadDias"},
			{sName: "nPorcentajePenalidad"},
			{sName: "nPorcentajeGarantiaCumplimiento"},
			{sName: "cDescripcion"}
		],fnInitComplete: function() {
			if($('#tblDetalleInasistencia >tbody >tr').length>0){
				oTabletblDetalleInasistencia.fnAdjustColumnSizing();
			}
		}
	});
}
function muestraDatos(){
	var qw="1=1 and cFolio='"+$('#cFolio').val()+"'";
	oTabletblDetalleInasistenciaDeficiente = $("#tblDetalleInasistenciaDeficiente").dataTable({
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mDetalleServicioDeficiente&qw="+qw,
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{sName: "nConsecutivo", bVisible: false },
			{sName: "ncantidadElementos"},
			{sName: "mMontoTotal"},
			{sName: "nCantidadDias"},
			{sName: "nPorcentajePenalidad"},
			{sName: "nPorcentajeGarantiaCumplimiento"},
			{sName: "cDescripcion"}
		],fnInitComplete: function() {
			if($('#tblDetalleInasistenciaDeficiente >tbody >tr').length>0){
				oTabletblDetalleInasistenciaDeficiente.fnAdjustColumnSizing();
			}
		}
	});
}
function generateAnexo(operation){
	if($("#processInDB").val()==0){
		swal("Primero se tiene que dar clic en el botón guardar para poder generar el archivo",{icon:"success",button: "Cerrar"});
		return false;
	}
	//Validar si se capturo la información
	var object=fillObjectData(operation,1);
	$.ajax({url: "../../ProcesoEnteraSatisfaccion" , type:'post' , async: true
		,data:object
		,dataType: 'json'
		,beforeSend : function() {
           $.blockUI({message: "Generando archivo espere por favor ......"});
        }
		,success: function(j){
			if(!j[0].RESPUESTA ||j[0].RESPUESTA=="false"){
				swal(j[0].MENSAJE,{icon:"error",button: "Cerrar"});	
			}else{
				swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
			}
		}, error: function(xhr, status, error) {
			console.log(error);
			swal("some error",{icon:"error",button: "Cerrar"});
		}, 
        complete: function () {
        	$.unblockUI();
        }
	});
}
function showAndHideObservation(){
	$("#floatingTextarea").empty();
	$("#cObservations").val('');
	devolucion=false;
	if(document.getElementById("radioNoAut").checked){
		$("#floatingTextarea").append("Favor de capturar las observaciones encontradas.");
		devolucion=true;
	}
}
function saveValidate(operation,id_oper){
	var resp=false;
	var object=fillObjectDataValidate(operation,id_oper);
	$.ajax({url: "../../ProcesoEnteraSatisfaccion" , type:'post' , async: true
		,data:object
		,dataType: 'json'
		,beforeSend : function() {
           $.blockUI({message: "Procesando ......"});
        }
		, success: function(j){
			resp=true;
			if(j[0].RESPUESTA=="false" || !(j[0].RESPUESTA)){
				$.unblockUI();
				parent.document.getElementById("pb_send").disabled = true;
				swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
			}else{
				$.unblockUI();
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
					},
				}).then((continuar) => {
					parent.document.getElementById("pb_send").disabled = false;
				});
			}
		}
		, error: function(error) {
			console.log(error);
			swal("some error",{icon:"error",button: "Cerrar"});
		}
		,complete: function () {
        	$.unblockUI();
			return resp;
        }
	});
}
function showObservations(){
	if($("#cObservations").val()!=""){
		$("#divObservaciones").show();
		devolucion=true;
		if(idOper==2){
			document.getElementById("radioNoAut").checked=true;	
		}
		if(idOper==1){
			$("#floatingTextarea").append("Observaciones encontradas.");
		}
	}
}
function hideResp(){
	if(idOper==6){
		$("#divRespuesta").hide();
	}
}
function sendProcess(operation,id_oper){
	var object=fillObjectDataValidate(operation,id_oper);
	var resp=false;
	$.ajax({url: "../../ProcesoEnteraSatisfaccion" , type:'post' , async: false
		,data:object
		,dataType: 'json'
		, success: function(j){
			if(j[0].RESPUESTA=="false" || !(j[0].RESPUESTA)){
				swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "warning",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						resp=true;
				});
			}else{
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
					},
				}).then((continuar) => {
					resp=false;
				});
			}
		}
		, error: function() {
			resp=false;
		}
		,complete: function () {
        	$.unblockUI();
			return resp;
        }
	});	
}