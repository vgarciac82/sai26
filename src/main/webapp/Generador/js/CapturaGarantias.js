function showAndHideTabs(){
	if(tabb==0 ){
		$("#CapturaGarantiasContrato").hide();
	}else{
		$("#CapturaGarantiasContrato").show();
	}
}
function agregaDatePickerFechas(){
	$("#fExpedidoGA").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fExpedidoGC").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fExpedidoGV").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fExpedido_endoso").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fExpedido_endoso2").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
	$("#fExpedido_endoso3").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
	});
}
function consultaDatos(){
	$.blockUI({message: "Procesando espere ......"});
	var object={tipoProceso:5,
				tipoOperacion:2,
				cIdContratoDefinitivo:$("#cIdContratoDefinitivo").val()
				};
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				if(j[0].RESPUESTA=="false" ||j[0].RESPUESTA==false){
					swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
				}else{
					//swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
					vaciarJsonAInputs(j[0].datosContratoGarantia);
					vaciarJsonAInputs(j[0].datosGarantiaGA);
					vaciarJsonAInputs(j[0].datosGarantiaGC);
					vaciarJsonAInputs(j[0].datosGarantiaGV);
					vaciarJsonAInputs(j[0].datosEndoso);
					vaciarJsonAInputs(j[0].datosEndoso2);
					vaciarJsonAInputs(j[0].datosEndoso3);
					esChequeGarantiaA();
					esFianzaGarantiaA();
					esChequeGarantiaC();
					esFianzaGarantiaC();
					esChequeGarantiaV();
					esFianzaGarantiaV();
					esChequeEndoso('');
					esFianzaEndoso('');
					esChequeEndoso('2');
					esFianzaEndoso('2');
					esChequeEndoso('3');
					esFianzaEndoso('3');
					showAndHideURLSGarantiaAndEndoso();
				}
				$.unblockUI();
		}, error: function() {
			$.unblockUI();
		}
	});
}
function showAndHideURLSGarantiaAndEndoso(){
	$("#divDownloadFileGarantia").hide();
	$("#divDownloadFileEndoso").hide();
	if($("#existeDoctoGarantia").val()==1){
		$("#divDownloadFileGarantia").show();
	}
	if($("#existeDoctoEndoso").val()==1){
		$("#divDownloadFileEndoso").show();
	}
}
function onclickFianzaGarantiaA(){
	document.getElementById("inlineFianzaGA").checked=true;
	document.getElementById("inlineChequeGA").checked=false;
	$("#lchequeGA").val(0);
	$("#lFianzaGA").val(1);			
}
function onclickChequeGarantiaA(){
	document.getElementById("inlineChequeGA").checked=true;
	document.getElementById("inlineFianzaGA").checked=false;
	$("#lchequeGA").val(1);
	$("#lFianzaGA").val(0);
}
function onclickFianzaGarantiaC(){
	document.getElementById("inlineFianzaGC").checked=true;
	document.getElementById("inlineChequeGC").checked=false;
	$("#lchequeGC").val(0);
	$("#lFianzaGC").val(1);			
}
function onclickChequeGarantiaC(){
	document.getElementById("inlineChequeGC").checked=true;
	document.getElementById("inlineFianzaGC").checked=false;
	$("#lchequeGC").val(1);
	$("#lFianzaGC").val(0);
}
function onclickFianzaGarantiaV(){
	document.getElementById("inlineFianzaGV").checked=true;
	document.getElementById("inlineChequeGV").checked=false;
	$("#lchequeGV").val(0);
	$("#lFianzaGV").val(1);			
}
function onclickChequeGarantiaV(){
	document.getElementById("inlineChequeGV").checked=true;
	document.getElementById("inlineFianzaGV").checked=false;
	$("#lchequeGV").val(1);
	$("#lFianzaGV").val(0);
}
function onclickFianzaEndoso(nId){
	document.getElementById("inlineFianzaE"+nId).checked=true;
	document.getElementById("inlineChequeE"+nId).checked=false;	
	$("#lchequeE"+nId).val(0);
	$("#lFianzaE"+nId).val(1);
}
function onclickChequeEndoso(nId){
	document.getElementById("inlineChequeE"+nId).checked=true;
	document.getElementById("inlineFianzaE"+nId).checked=false;
	$("#lchequeE"+nId).val(1);
	$("#lFianzaE"+nId).val(0);
}
function esChequeGarantiaA(){
	if($("#lchequeGA").val()==1){
		onclickChequeGarantiaA();
	}
}
function esFianzaGarantiaA(){
	if($("#lFianzaGA").val()==1){
		onclickFianzaGarantiaA();
	}
}
function esChequeGarantiaC(){
	if($("#lchequeGC").val()==1){
		onclickChequeGarantiaC();
	}
}
function esFianzaGarantiaC(){
	if($("#lFianzaGC").val()==1){
		onclickFianzaGarantiaC();
	}
}
function esChequeGarantiaV(){
	if($("#lchequeGV").val()==1){
		onclickChequeGarantiaV();
	}
}
function esFianzaGarantiaV(){
	if($("#lFianzaGV").val()==1){
		onclickFianzaGarantiaV();
	}
}
function esChequeEndoso(id){
	if($("#lchequeE"+id).val()==1){
		onclickChequeEndoso(id);
	}
}
function esFianzaEndoso(id){
	if($("#lFianzaE"+id).val()==1){
		onclickFianzaEndoso(id);
	}
}
function saveItemsGarantia(){	
	$.blockUI({message: "Procesando espere ......"});
	fillObject();
	jQuery.ajax({
	    url: '../../ContratacionFormalizadaServlet?tipoProceso=5&tipoOperacion=3',
	    data: dataObject,
	    cache: false,
	    contentType: false,
	    dataType: "json",
	    processData: false,
	    method: 'POST',
	    type: 'POST', // For jQuery < 1.9
	    success: function(j){
			if(j[0].RESPUESTA=="false" ||j[0].RESPUESTA==false){
				swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
			}else{
				swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
				clearInputs();
		    	vaciarJsonAInputs(j[0].datosContratoGarantia);
				vaciarJsonAInputs(j[0].datosGarantiaGA);
				vaciarJsonAInputs(j[0].datosGarantiaGC);
				vaciarJsonAInputs(j[0].datosGarantiaGV);
				esChequeGarantiaA();
				esFianzaGarantiaA();
				esChequeGarantiaC();
				esFianzaGarantiaC();
				esChequeGarantiaV();
				esFianzaGarantiaV();
				showAndHideURLSGarantiaAndEndoso();
	    	}
	    	$.unblockUI();
	    },error: function(){
	    	swal("Error",{icon:"error",button: "Cerrar"});
	    	$.unblockUI();
	    }
	});
}
function saveItemsEndoso(){
	if($("#lchequeE").val()==0 && $("#lFianzaE").val()==0){
		swal("Favor de seleccionar si es cheque o fianza",{icon:"warning",button: "Cerrar"});
		return;
	}
	if($("#cAseguradora_endoso").val()==""){
		swal("Favor de capturar el nombre de la Aseguradora/Afianzadora.",{icon:"warning",button: "Cerrar"});
		return;
	}
	if($("#nChequeFianza_endoso").val()==""){
		swal("Favor de capturar el número de Cheque/Fianza.",{icon:"warning",button: "Cerrar"});
		return;
	}
	if($("#fFechaExpedicion_endoso").val()==""){
		swal("Favor de capturar la fecha de expedición.",{icon:"warning",button: "Cerrar"});
		return;
	}
	
	$.blockUI({message: "Procesando espere ......"});
	fillObjectEndoso();
	jQuery.ajax({
	    url: '../../ContratacionFormalizadaServlet?tipoProceso=5&tipoOperacion=3',
	    data: dataObjectEndoso,
	    cache: false,
	    contentType: false,
	    dataType: "json",
	    processData: false,
	    method: 'POST',
	    type: 'POST', // For jQuery < 1.9
	    success: function(j){
			if(j[0].RESPUESTA=="false" ||j[0].RESPUESTA==false){
				swal(j[0].MENSAJE,{icon:"warning",button: "Cerrar"});
			}else{
				swal(j[0].MENSAJE,{icon:"success",button: "Cerrar"});
				clearInputsEndoso();
		    	vaciarJsonAInputs(j[0].datosContratoGarantia);
				vaciarJsonAInputs(j[0].datosEndoso);
				vaciarJsonAInputs(j[0].datosEndoso2);
				vaciarJsonAInputs(j[0].datosEndoso3);
				esChequeEndoso('');
				esFianzaEndoso('');
				esChequeEndoso('2');
				esFianzaEndoso('2');
				esChequeEndoso('3');
				esFianzaEndoso('3');
				showAndHideURLSGarantiaAndEndoso();
	    	}
	    	$.unblockUI();
	    },error: function(){
	    	swal("Error",{icon:"error",button: "Cerrar"});
	    	$.unblockUI();
	    }
	});
}
function fillObject(){
	dataObject.append('tipoProceso', 5);
	dataObject.append('tipoOperacion', 3);
	dataObject.append('cIdContratoDefinitivo', $("#cIdContratoDefinitivo").val());
	dataObject.append('cAseguradoraGA', $("#cAseguradoraGA").val());
	dataObject.append('fFechaExpedicionGA', $("#fFechaExpedicionGA").val());
	dataObject.append('lChequeGA', $("#lchequeGA").val());
	dataObject.append('lFianzaGA', $("#lFianzaGA").val());
	dataObject.append('nNumeroChequeFianzaGA', $("#nChequeFianzaGA").val());
	dataObject.append('mGarantiaAnticipo', unFrmt2($("#mMontoGarantiaAnticipo").val()));
	
	dataObject.append('cAseguradoraGC', $("#cAseguradoraGC").val());
	dataObject.append('fFechaExpedicionGC', $("#fFechaExpedicionGC").val());
	dataObject.append('lChequeGC', $("#lchequeGC").val());
	dataObject.append('lFianzaGC', $("#lFianzaGC").val());
	dataObject.append('nNumeroChequeFianzaGC', $("#nChequeFianzaGC").val());
	dataObject.append('mGarantiaCumplimiento', unFrmt2($("#mMontoGarantiaCumplimiento").val()));
	
	dataObject.append('cAseguradoraGV', $("#cAseguradoraGV").val());
	dataObject.append('fFechaExpedicionGV', $("#fFechaExpedicionGV").val());
	dataObject.append('lChequeGV', $("#lchequeGV").val());
	dataObject.append('lFianzaGV', $("#lFianzaGV").val());
	dataObject.append('nNumeroChequeFianzaGV', $("#nChequeFianzaGV").val());
	dataObject.append('mGarantiaViciosOcultos', unFrmt2($("#mMontoGarantiaViciosO").val()));
	
	dataObject.append('nTipoProcesoGarantia', 1);
	jQuery.each(jQuery('#nameArchivoGarantia')[0].files, function(i, file) {
	    dataObject.append('file-'+i, file);
	});
}
function clearInputs(){
	$("#cAseguradoraGA").val('');
	$("#fFechaExpedicionGA").val('');
	$("#lchequeGA").val(0);
	$("#lFianzaGA").val(0);
	$("#nChequeFianzaGA").val('');
	$("#mMontoGarantiaAnticipo").val('');
	document.getElementById("inlineChequeGC").checked=false;
	document.getElementById("inlineFianzaGC").checked=false;
	
	$("#cAseguradoraGC").val('');
	$("#fFechaExpedicionGC").val('');
	$("#lchequeGC").val(0);
	$("#lFianzaGC").val(0);
	$("#nChequeFianzaGC").val('');
	$("#mMontoGarantiaCumplimiento").val('');
	document.getElementById("inlineChequeGC").checked=false;
	document.getElementById("inlineFianzaGC").checked=false;
	
	$("#cAseguradoraGV").val('');
	$("#fFechaExpedicionGV").val('');
	$("#lchequeGV").val(0);
	$("#lFianzaGV").val(0);
	$("#nChequeFianzaGV").val('');
	$("#mMontoGarantiaViciosO").val('');
	document.getElementById("inlineChequeGV").checked=false;
	document.getElementById("inlineFianzaGV").checked=false;
}
function clearInputsEndoso(){
	$("#cAseguradora_endoso").val('');
	$("#fFechaExpedicion_endoso").val('');
	$("#lchequeE").val(0);
	$("#lFianzaE").val(0);
	$("#nChequeFianza_endoso").val('');
	$("#mMontoEndosoCumplimiento").val('');
	document.getElementById("inlineChequeE").checked=false;
	document.getElementById("inlineFianzaE").checked=false;
	
	$("#fFechaExpedicion_endoso2").val('');
	$("#lchequeE2").val(0);
	$("#lFianzaE2").val(0);
	$("#nChequeFianza_endoso2").val('');
	$("#mMontoEndosoCumplimiento2").val('');
	document.getElementById("inlineChequeE2").checked=false;
	document.getElementById("inlineFianzaE2").checked=false;
	
	$("#fFechaExpedicion_endoso3").val('');
	$("#lchequeE3").val(0);
	$("#lFianzaE3").val(0);
	$("#nChequeFianza_endoso3").val('');
	$("#mMontoEndosoCumplimiento3").val('');
	document.getElementById("inlineChequeE3").checked=false;
	document.getElementById("inlineFianzaE3").checked=false;
}
function fillObjectEndoso(){
	//alert($("#nChequeFianza_endoso2").val()+" fFechaExpedicion_endoso2="+$("#fFechaExpedicion_endoso2").val()+" lchequeE2="+$("#lchequeE2").val()+" lFianzaE2="+$("#lFianzaE2").val())
	dataObjectEndoso.append('tipoProceso', 5);
	dataObjectEndoso.append('tipoOperacion', 3);
	dataObjectEndoso.append('cIdContratoDefinitivo', $("#cIdContratoDefinitivo").val());
	dataObjectEndoso.append('cAseguradoraGC', $("#cAseguradora_endoso").val());
	
	dataObjectEndoso.append('fFechaExpedicionGC', $("#fFechaExpedicion_endoso").val());
	dataObjectEndoso.append('lChequeGC', $("#lchequeE").val());
	dataObjectEndoso.append('lFianzaGC', $("#lFianzaE").val());
	dataObjectEndoso.append('mGarantiaCumplimiento', unFrmt2($("#mMontoEndosoCumplimiento").val()));
	dataObjectEndoso.append('nNumeroChequeFianzaGC', $("#nChequeFianza_endoso").val());
	dataObjectEndoso.append('nIdConsecutivoEndosoGC', 1);
	
	dataObjectEndoso.append('fFechaExpedicionGA', $("#fFechaExpedicion_endoso2").val());
	dataObjectEndoso.append('lChequeGA', $("#lchequeE2").val());
	dataObjectEndoso.append('lFianzaGA', $("#lFianzaE2").val());
	dataObjectEndoso.append('mGarantiaAnticipo', unFrmt2($("#mMontoEndosoCumplimiento2").val()));
	dataObjectEndoso.append('nNumeroChequeFianzaGA', $("#nChequeFianza_endoso2").val());
	dataObjectEndoso.append('nIdConsecutivoEndosoGA', 2);
	
	dataObjectEndoso.append('fFechaExpedicionGV', $("#fFechaExpedicion_endoso3").val());
	dataObjectEndoso.append('lChequeGV', $("#lchequeE3").val());
	dataObjectEndoso.append('lFianzaGV', $("#lFianzaE3").val());
	dataObjectEndoso.append('mGarantiaViciosOcultos', unFrmt2($("#mMontoEndosoCumplimiento3").val()));
	dataObjectEndoso.append('nNumeroChequeFianzaGV', $("#nChequeFianza_endoso3").val());
	dataObjectEndoso.append('nIdConsecutivoEndosoGV', 3);
	
	dataObjectEndoso.append('nTipoProcesoGarantia', 2);
	jQuery.each(jQuery('#nameArchivoEndoso')[0].files, function(i, file) {
	    dataObjectEndoso.append('file-'+i, file);
	});
}
function downloadFile(nTypeFile){
	window.open("../../servlet/ReportesGRM?"
		+"&operacion=4"
		+"&nTypeFile="+nTypeFile
		+"&cFolio="+$("#cFolio").val() 
		+"&cContratoDefinitivo="+$("#cIdContratoDefinitivo").val()
		+"&cContratoCNET="+$("#contratoCNET").val() 
		, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
}

