var idOper;
function onLoadPlantilla(id_oper) {//Carga Plantilla
	idOper=id_oper;
	var resp=true;
	if(id_oper>1 && id_oper<3 ){
		parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
	}
	switch (parseInt(id_oper,10)){
		case 1://Captura
			informationQuery(id_oper);
			break;
		case 2://valida
			informationQuery(id_oper);
			break;
		case 3://Consulta
			informationQuery(id_oper);
			$("#divValidation").hide();
			$("#spanEncabezado").text("Consulta penas convencionales y deducciones al pago");
			disabledObservaciones();
			break;
		default:
			resp=false;
			break;
	}
	return resp;
}
function onSubmit(id_oper) {//clic boton Guardar
	idOper=id_oper;
	var resp=true;
	if(id_oper>1 && id_oper<3 ){
		parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
	}
	switch (parseInt(id_oper,10)){
		case 1://Captura
			var p = window.parent;
			p.gestion.setFolio($("#cFolio").val());
			p.gestion.setOperador($("#OPERADOR").val());
			p.gestion.setFechaDocumento($("#FECHA_DOCUMENTO").val()); 
			p.gestion.setEjercicioFiscal($("#cEjercicio").val());
			
			saveInformation(id_oper);
			
			break;
		case 2://valida
			resp=false;
			$("#cDocumentHAplicado").val("V");
			if(!document.getElementById("radioNoAut").checked && !document.getElementById("radioSiAut").checked){
				swal("Favor de contestar la pregunta, ¿Es correcto el proceso de c\u00e1lculo?.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			if(document.getElementById("radioNoAut").checked && $("#cObservations").val()==""){
				$("#cDocumentHAplicado").val("R");
				swal("Favor de capturar las observaciones del por qu\u00e9 no es correcto el proceso de c\u00e1lculo.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			resp=true;
			validatePenality(id_oper);
			break;
		case 3://Consulta
			break;
		default:
			resp=false;
			break;
	}
	return resp;
}
function ResponsableSiguiente(id_oper) {
	var respSig="CONSULTA_PENACONVENCIONAL";
	switch (parseInt(id_oper,10)){
		case 1:// Captura
			respSig="VALIDA_PENA_CONVENCIONAL";
			break;
		case 2:// valida
			if(devolucion){
				respSig= "CAPTURA_PENA_CONVENCIONAL";
			}else{
				respSig="CONSULTA_PENACONVENCIONAL";	
			}
			break;
		case 3:// Consulta
			respSig="CONSULTA_PENACONVENCIONAL";
			break;
		default:
			respSig="CONSULTA_PENACONVENCIONAL";
			break;
	}
	return respSig;
}
function OperacionSiguiente(id_oper) {
	var operSiguiente="consulta_penaconvencional";
	switch (parseInt(id_oper,10)){
		case 1:// Captura
			operSiguiente="valida_pena_convencional";
			break;
		case 2:// valida
			if(devolucion){
				operSiguiente= "captura_pena_convencional";
			}else{
				operSiguiente="consulta_penaconvencional";	
			}
			break;
		case 3:// Consulta
			operSiguiente="consulta_penaconvencional";
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
			$.blockUI({message: "Procesando espere ......"});
			resp=true;
			idOper=2;
			$("#cDocumentHAplicado").val("V");
			sendPenality(idOper);
			break;
		case 2://valida
			$.blockUI({message: "Procesando espere ......"});
			resp=true;
			idOper=3;
			$("#cDocumentHAplicado").val("S");
			if(document.getElementById("radioNoAut").checked ){
				$("#cDocumentHAplicado").val("R");
				idOper=1;
				if($("#cObservations").val()==""){
					swal("Favor de capturar las observaciones del por qu\u00e9 no es correcto el proceso de c\u00e1lculo.",{icon:"warning",button: "Cerrar"});
					return false;	
				}
			}
			resp=true;
			sendPenality(idOper);
			break;
		case 3://Consulta
			resp=true;
			break;
		default:
			resp=false;
			break;
	}
	return resp;
}// fin  onPostSubmit
//-------------Funciones de Caso Fin --------------------------------------------------------------------------------------------------------
function addDatePicker(){
	$(".date").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
}
function queryTablePenaltyServices(){
	var param = "'"+$("#pedidoContratoCompromiso").val()+"','"+stringPenalty+"','"+$("#cFolio").val()+"'";
	var funcion="fn_PartidasContratoPenalizacion";
	if(idOper>1){
		param = "'"+$("#pedidoContratoCompromiso").val()+"','"+$("#cFolio").val()+"'";
		funcion="fn_PartidasContratoPenalizacionRead";	
	}
	$('#tblPenaltyService').dataTable().fnClearTable();
	oTablePenalty = $("#tblPenaltyService").dataTable({
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
		bPaginate: false,
		bServerSide: true,
		bProcessing: true,
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param) +")" ) ,
		aaSorting: [[ 0, "asc",1, "asc" ]] ,
		aoColumns: [
			{ sName: "nIdLineaConsolidado" },
			{ sName: "nConsecutiveItem" },
			{ sName: "cDescripcionAdicional" },
			{ sName: "elementosPiezas" },
			{ sName: "montoDeBienesServicios" },
			{ sName: "porcentajePenalidad" },
			{ sName: "penalidadDiaria" },
			{ sName: "diasConAtrasoDeficiencia" },
			{ sName: "importeTotalPenalidad" },
			{ sName: "porcentajeGarantiaCump" },
			{ sName: "diasPenalizaPrincipioPropor" },
			{ sName: "importePenaConv" }
		]
	});
}		
function queryTableDeductionServices(){
	var param = "'"+$("#pedidoContratoCompromiso").val()+"','"+stringDeduction+"','"+$("#cFolio").val()+"'";
	var funcion="fn_PartidasContratoDeduccion";
	if(idOper>1){
		param = "'"+$("#pedidoContratoCompromiso").val()+"','"+$("#cFolio").val()+"'";
		funcion="fn_PartidasContratoDeduccionRead";	
	}
	$('#tblDeductionService').dataTable().fnClearTable();
	oTableDeduction = $("#tblDeductionService").dataTable({
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param) +")" ) ,
		bPaginate: false,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "nIdLineaConsolidado" },
			{ sName: "nConsecutiveItem" },
			{ sName: "cDescripcionAdicional" },
			{ sName: "elementosPiezas" },
			{ sName: "montoDeBienesServicios" },
			{ sName: "porcentajePenalidad" },
			{ sName: "penalidadDiaria" },
			{ sName: "diasConAtrasoDeficiencia" },
			{ sName: "importeTotalPenalidad" },
			{ sName: "porcentajeGarantiaCump" },
			{ sName: "diasPenalizaPrincipioPropor" },
			{ sName: "importePenaConv" }
		]
	});
}
function queryTablePenaltyProducts(){
	var param = "'"+$("#pedidoContratoCompromiso").val()+"','"+stringPenalty+"','"+$("#cFolio").val()+"'";
	var funcion="fn_PartidasContratoPenalizacion";
	if(idOper>1){
		param = "'"+$("#pedidoContratoCompromiso").val()+"','"+$("#cFolio").val()+"'";
		funcion="fn_PartidasContratoPenalizacionRead";	
	}
	$('#tblPenaltyProducts').dataTable().fnClearTable();
	oTablePenalty = $("#tblPenaltyProducts").dataTable({
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param) +")" ) ,
		bPaginate: false,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "nIdLineaConsolidado" },
			{ sName: "nConsecutiveItem" },
			{ sName: "cDescripcionAdicional" },
			{ sName: "fechaEntrega" },
			{ sName: "elementosPiezas" },
			{ sName: "montoDelBien" },
			{ sName: "montoDeBienesServicios" },
			{ sName: "porcentajePenalidad" },
			{ sName: "penalidadDiaria" },
			{ sName: "diasConAtrasoDeficiencia" },
			{ sName: "importeTotalPenalidad" },
			{ sName: "porcentajeGarantiaCump" },
			{ sName: "diasPenalizaPrincipioPropor" },
			{ sName: "importePenaConv" }
		]
	});
}
function queryTableDeductionProducts(){
	var param = "'"+$("#pedidoContratoCompromiso").val()+"','"+stringDeduction+"','"+$("#cFolio").val()+"'";
	var funcion="fn_PartidasContratoDeduccion";
	if(idOper>1){
		param = "'"+$("#pedidoContratoCompromiso").val()+"','"+$("#cFolio").val()+"'";
		funcion="fn_PartidasContratoDeduccionRead";	
	}
	$('#tblDeductionProducts').dataTable().fnClearTable();
	oTableDeduction = $("#tblDeductionProducts").dataTable({
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
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (param) +")" ) ,
		bPaginate: false,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "nIdLineaConsolidado" },
			{ sName: "nConsecutiveItem" },
			{ sName: "cDescripcionAdicional" },
			{ sName: "fechaEntrega" },
			{ sName: "elementosPiezas" },
			{ sName: "montoDelBien" },
			{ sName: "montoDeBienesServicios" },
			{ sName: "porcentajePenalidad" },
			{ sName: "penalidadDiaria" },
			{ sName: "diasConAtrasoDeficiencia" },
			{ sName: "importeTotalPenalidad" },
			{ sName: "porcentajeGarantiaCump" },
			{ sName: "diasPenalizaPrincipioPropor" },
			{ sName: "importePenaConv" }
		]
	});
}
function isSavePenalty(){
	if($("#checkPenaConv").val()==1){
		document.getElementById("checkPenaConv").checked=true;
		showTablesPenalty();
		$("#mAmountPenaltyTotal").formatCurrency();
	}else{
		document.getElementById("checkPenaConv").checked=false;
		hideTablesPenalty();
	}
}
function isSaveDeduction(){
	if($("#checkDeduccion").val()==1){
		document.getElementById("checkDeduccion").checked=true;
		showTablesDeductions();
		$("#mAmountDeductionTotal").formatCurrency();
	}else{
		document.getElementById("checkDeduccion").checked=false;
		hideTablesDeductions();
	}
}
function catchOnclickPenalty(){
	hideTablesPenalty();
	$("#checkPenaConv").val(0);
	if(document.getElementById("checkPenaConv").checked){
		if($("#pedidoContratoCompromiso").val()==""){
			swal("Favor de seleccionar un contrato.",{icon:"warning",button: "Cerrar"});
			document.getElementById("checkPenaConv").checked=false;
			return;
		}
		showTablesPenalty();
		$("#divAddRowItemPenalty").show();
		$("#checkPenaConv").val(1);
		$("#mAmountPenaltyTotal").formatCurrency();
	}
	showAndHideAddRowPenalty();
}
function showAndHideAddRowPenalty(){
	if(idOper==1 && document.getElementById("checkPenaConv").checked && $("#nIdPenaltyDeduction").val()!=-1){
		$("#divAddRowItemPenalty").show();
	}else{
		$("#divAddRowItemPenalty").hide();
	}
}
function showAndHideAddRowDeduction(){
	if(idOper==1 && document.getElementById("checkDeduccion").checked && $("#nIdPenaltyDeduction").val()!=-1){
		$("#divAddRowItemDeduction").show();
	}else{
		$("#divAddRowItemDeduction").hide();
	}
}
function showTablesPenalty(){
	$("#divPenaltyAmount").show();
	if($("#cIdTipoContrato").val()=='CV'){
		$("#divPenaltyService").show();
		queryTablePenaltyServices();
	}else{
		$("#divPenaltyProducts").show()
		queryTablePenaltyProducts();
	}
}
function hideTablesPenalty(){
	$("#divPenaltyService").hide();
	$("#divPenaltyProducts").hide();
	$("#divPenaltyAmount").hide();
}
function catchOnclickDeduction(){
	hideTablesDeductions();
	$("#checkDeduccion").val(0);
	if(document.getElementById("checkDeduccion").checked){
		if($("#pedidoContratoCompromiso").val()==""){
			swal("Favor de seleccionar un contrato.",{icon:"warning",button: "Cerrar"});
			document.getElementById("checkDeduccion").checked=false;
			return;
		}
		showTablesDeductions();
		$("#checkDeduccion").val(1);
		$("#mAmountDeductionTotal").formatCurrency();
	}
	showAndHideAddRowDeduction();
}
function showTablesDeductions(){
	$("#divDeductionAmount").show();
	if($("#cIdTipoContrato").val()=='CV'){
		$("#divDeductionService").show();
		queryTableDeductionServices();
	}else{
		$("#divDeductionProducts").show()
		queryTableDeductionProducts();
	}
}
function hideTablesDeductions(){
	$("#divDeductionProducts").hide();
	$("#divDeductionService").hide();
	$("#divDeductionAmount").hide();
}
function dailyDeduction(idA,idB,idC){
	if($("#"+idA).val()=='' || $("#"+idB).val()==''){
		return;
	}
	$("#"+idC).val( ( parseFloat($("#"+idA).val(),10) * (parseFloat($("#"+idB).val(),10)*0.01) ).toFixed(2) ).change();
}
function penaltyAmount(idA,idB,idD,idE){
	if($("#"+idA).val()=='' || $("#"+idB).val()=='' || $("#"+idD).val()==''){
		return;
	}
	$("#"+idE).val( ( (parseFloat($("#"+idA).val(),10) * (parseFloat($("#"+idB).val(),10)*0.01))  * parseInt($("#"+idD).val(),10)).toFixed(2) );
}
function daysOfPenalty(idB,idF,idG,idD){
	if($("#"+idB).val()=='' || parseFloat($("#"+idB).val(),10)==0.0 ||$("#"+idF).val()==''){
		return;
	}
	if( ((parseFloat($("#"+idF).val(),10)/(parseFloat($("#"+idB).val(),10))).toFixed(2))>parseFloat($("#"+idD).val(),10) ){
		$("#"+idG).val( $("#"+idD).val()).change();
	}else{
		$("#"+idG).val( (parseFloat($("#"+idF).val(),10)/(parseFloat($("#"+idB).val(),10))).toFixed(2) ).change();
	}
	
}
function finalPenaltyAmount(idA,idB,idF,idH,idD){
	if($("#"+idA).val()=='' || $("#"+idB).val()=='' || parseFloat($("#"+idB).val())==0.0 ||$("#"+idF).val()==''){
		return;
	}
	if( ((parseFloat($("#"+idF).val())/(parseFloat($("#"+idB).val()))).toFixed(2))>parseFloat($("#"+idD).val()) ){
		$("#"+idH).val( (( parseFloat($("#"+idA).val(),10) * (parseFloat($("#"+idB).val(),10)*0.01) )  * (parseFloat($("#"+idD).val(),10) ) ).toFixed(2) ).change();
	}else{
		$("#"+idH).val( (( parseFloat($("#"+idA).val(),10) * (parseFloat($("#"+idB).val(),10)*0.01) )  *(parseFloat($("#"+idF).val(),10)/(parseFloat($("#"+idB).val(),10))) ).toFixed(2) ).change();
	}
	
}
function informationQuery(id_oper){
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectData(1,id_oper);
	$.ajax({url: "../../servlet/PenaltiesServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				if(!j[0].RESPUESTA ||j[0].RESPUESTA=="false"){
					swal(j[0].MENSAJE,{icon:"error",button: "Cerrar"});	
				}else{
					llenaCombo(j[0].catPeriodo,"cPeriodo");
					llenaCombo(j[0].cPartidaPenalty,"cPartidaPenalty");
					llenaCombo(j[0].cPartidaDeduction,"cPartidaDeduction");
					vaciarJsonAInputs(j[0].penaltyDeductionSave);
				}
				isSavePenalty();
				isSaveDeduction();
				showObservations();
				showAndHideAddRowPenalty();
				showAndHideAddRowDeduction();
				$.unblockUI();
		}, error: function(xhr, status, error) {
			console.log(error);
			swal("some error",{icon:"error",button: "Cerrar"});
			$.unblockUI();
		}
	});
}
function fillObjectData(operation,id_oper){
	var data0= {
		nIdPenaltyDeduction: $("#nIdPenaltyDeduction").val(),
		cIdContratoDefinitivo:$("#pedidoContratoCompromiso").val(),
		cFolio: $("#cFolio").val(),
		nIdEstate:id_oper,
		lPenalty:$("#checkPenaConv").val(),
		lDeduction:$("#checkDeduccion").val(),
		cTipoContrato:$("#cIdTipoContrato").val(),
		cDocumentHAplicado:$("#cDocumentHAplicado").val(),
		cObservations:$("#cObservations").val(),
		nPeriodo:$("#cPeriodo").val(),
		cOficio:$("#cOficio").val(),
		cConcepto:$("#cConcepto").val(),
		cNumCNET:$("#cNumCNET").val(),
		cProveedor:$("#cProveedor").val(),
		operation:operation
	};
	return data0;
}
function saveInformation(id_oper){
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectData(2,id_oper);
	var dataStringPenalty;
	var dataStringDeduction;
	if(document.getElementById("checkPenaConv").checked){
		dataStringPenalty=dataStringPenaltyAndDeduction(oTablePenalty,stringPenalty);
		if(dataStringPenalty==""){
			object.lPenalty=0;
		}else{
			object.penaltyItems=dataStringPenalty;
		}
	}
	if(document.getElementById("checkDeduccion").checked){
		dataStringDeduction=dataStringPenaltyAndDeduction(oTableDeduction,stringDeduction);
		if(dataStringDeduction==""){
			object.lDeduction=0;
		}else{
			object.deductionItems=dataStringDeduction;
		}
	}
	$.ajax({url: "../../servlet/PenaltiesServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				if(j[0].RESPUESTA=="false" || !(j[0].RESPUESTA)){
					$.unblockUI();
					parent.document.getElementById("pb_send").disabled = true;
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}else{
					vaciarJsonAInputs(j[0].penaltyDeductionSave);
					removeOptiosnSelect('cPartidaPenalty')
					llenaCombo(j[0].cPartidaPenalty,"cPartidaPenalty");
					removeOptiosnSelect('cPartidaDeduction')
					llenaCombo(j[0].cPartidaDeduction,"cPartidaDeduction");
					isSavePenalty();
					isSaveDeduction();
					showAndHideAddRowPenalty();
					showAndHideAddRowDeduction();
					swal({
						title: "",
						text: j[0].MENSAJE,
						icon: "info",
						closeOnClickOutside: false,
						buttons: {
							confirm : "Cerrar"
						},
					}).then((continuar) => {
						if(j[0].RESPUESTA_DOC){
							parent.document.getElementById("pb_send").disabled = false;
						}
						$.unblockUI();
					});
				}
				
		}, error: function() {
			$.unblockUI();
		}
	});
}
function addRowOfTable(opcion){
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectData(opcion,idOper);
	var dataStringPenalty="";
	var dataStringDeduction="";
	if(6==opcion){
		dataStringPenalty=dataRowPenaltyAndDeduction($("#cPartidaPenalty").val());//
		if(dataStringPenalty==""){
			bject.lPenalty=0;
		}else{
			object.penaltyItems=dataStringPenalty;
		}
	}
	if(7==opcion){
		dataStringDeduction=dataRowPenaltyAndDeduction($("#cPartidaDeduction").val());//
		if(dataStringDeduction==""){
			object.lDeduction=0;
		}else{
			object.deductionItems=dataStringDeduction;
		}
	}
	$.ajax({url: "../../servlet/PenaltiesServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				if(6==opcion){
					isSavePenalty();
				}
				if(7==opcion){
					isSaveDeduction();
				}
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
					},
				}).then((continuar) => {
					if(j[0].RESPUESTA){
						parent.document.getElementById("pb_send").disabled = true;
					}
					$.unblockUI();
				});
		}, error: function() {
			$.unblockUI();
		}
	});
}
function dataStringPenaltyAndDeduction(oTable,tipo){
	var dataString='';
	var aTrs = oTable.dataTable().fnGetNodes();
	var token=",";
	var aData;
	var token2=false;
	for(var i=0;i<aTrs.length;i++){
		aData = oTable.fnGetData(aTrs[i]);
		if($("#importePenaConv_"+aData[0]+"_"+aData[1]+tipo).val()==""|| parseFloat($("#importePenaConv_"+aData[0]+"_"+aData[1]+tipo).val())<=0.0){
			continue;
		}
		if(token2){
			dataString+=token;
		}
		token2=true;
		if("CV"==$("#cIdTipoContrato").val()){
			dataString+=aData[0]+"-"+aData[1]+"-"+$("#elementosPiezas_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#montoDeBienesServicios_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#porcentajePenalidad_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#penalidadDiaria_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#diasConAtrasoDeficiencia_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#importeTotalPenalidad_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#porcentajeGarantiaCump_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#diasPenalizaPrincipioPropor_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#importePenaConv_"+aData[0]+"_"+aData[1]+tipo).val();
		}else{
			dataString+=aData[0]+"-"+aData[1]+"-"+(($("#fechaEntrega_"+aData[0]+"_"+aData[1]+tipo).val()).replace("-","/")).replace("-","/")+"-"+$("#elementosPiezas_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#montoDelBien_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#montoDeBienesServicios_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#porcentajePenalidad_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#penalidadDiaria_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#diasConAtrasoDeficiencia_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#importeTotalPenalidad_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#porcentajeGarantiaCump_"+aData[0]+"_"+aData[1]+tipo).val()+"-"+$("#diasPenalizaPrincipioPropor_"+aData[0]+"_"+aData[1]+tipo).val()
			+"-"+$("#importePenaConv_"+aData[0]+"_"+aData[1]+tipo).val();
		}
	}
	return dataString
}
function dataRowPenaltyAndDeduction(partida){
	var dataString='';
	if("CV"==$("#cIdTipoContrato").val()){
		dataString+=partida+"-1-0-0-0-0-0-0-0-0-0";
	}else{
		dataString+=partida+"-1-0-0-0-0-0-0-0-0-0-0-0";
	}
	return dataString
}
function showObservations(){
	if($("#cObservations").val()!=""){
		$("#divObservaciones").show();
		devolucion=true;
		if(idOper==2){
			document.getElementById("radioNoAut").checked=true;	
		}
	}
}
function disabledObservaciones(){
	if(idOper==3){
		$("#floatingTextarea").empty();
		$("#floatingTextarea").append("Favor de capturar las observaciones para la autorizaci&oacute;n.");	
		$("#cObservations").prop('disabled', true);
	}
}
function showAndHideObservation(){
	$("#floatingTextarea").empty();
	$("#cObservations").val('');
	devolucion=false;
	if(document.getElementById("radioNoAut").checked){
		$("#floatingTextarea").append("Favor de capturar las observaciones del por qu&eacute; no es correcto el proceso de c&aacute;lculo.");
		devolucion=true;
	}else{
		$("#floatingTextarea").append("Favor de capturar las observaciones para la autorizaci&oacute;n.");
	}
}
function sendPenality(id_oper){
	
	var object=fillObjectData(4,id_oper);
	$.ajax({url: "../../servlet/PenaltiesServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json'
		, success:function(j){
			$.unblockUI();
		}, error: function() {
			$.unblockUI();
		}
	});
}
function validatePenality(id_oper){
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectData(5,id_oper);
	$.ajax({url: "../../servlet/PenaltiesServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						if(j[0].RESPUESTA){
							parent.document.getElementById("pb_send").disabled = false;
						}
						$.unblockUI();
				});
		}, error: function() {
			$.unblockUI();
		}
	});
}
function searchContract(){
	myModal = new bootstrap.Modal(document.getElementById('modalContracts'), {
	  keyboard: false
	})
	myModal.show();
	clearParams();
	queryTableContracts();
}
function clearParams(){
	$("#cIdDefinitivo").val('');
	$("#cRazonSocial").val('');
	$("#cNumCNET").val('');
}
function queryTableContracts(){
	var param = "'"+$("#cIdUnidadEjecutora").val()+"'";
	var funcion="searContract";
	var cadCont="''";
	var cadProveedor="''";
	var cadContCNET="''";
	if($("#cIdDefinitivo").val()!=""){
		cadCont="'"+$("#cIdDefinitivo").val()+"'";
	}
	if($("#cRazonSocial").val()!=""){
		cadProveedor="'"+$("#cRazonSocial").val()+"'";
	}
	if($("#cNumCNET").val()!=""){
		cadContCNET="'"+$("#cNumCNET").val()+"'";
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
			{ sName: "cIdTipoContrato" }
		]
		,fnInitComplete: function() {
			if($('#tblConsulta >tbody >tr').length>0){
				oTableConsulta.fnAdjustColumnSizing();
			}
		}
	});
}
function deletePenalityItems(id_oper){
	$.blockUI({message: "Procesando espere ......"});
	var object=fillObjectData(3,id_oper);
	$.ajax({url: "../../servlet/PenaltiesServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				vaciarJsonAInputs(j[0].penaltyDeductionSave);
				removeOptiosnSelect('cPartidaPenalty')
				llenaCombo(j[0].cPartidaPenalty,"cPartidaPenalty");
				removeOptiosnSelect('cPartidaDeduction')
				llenaCombo(j[0].cPartidaDeduction,"cPartidaDeduction");
				isSavePenalty();
				isSaveDeduction();
				showAndHideAddRowPenalty();
				showAndHideAddRowDeduction();
				showObservations();
				$("#pedidoContratoCompromiso").val(aData[0]);
				$("#cIdTipoContrato").val(aData[5]);
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						$.unblockUI();
				});
		}, error: function() {
			$.unblockUI();
		}
	});
}
