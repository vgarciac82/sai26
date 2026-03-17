function showAndHideTabs(){
	if(tabb==0 ){
		$("#LiberaGarantias").hide();
	}else{
		$("#LiberaGarantias").show();
	}
}
function consultaDatos(){
	$.blockUI({message: "Procesando espere ......"});
	var object={tipoProceso:5,
				tipoOperacion:4,
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
					vaciarJsonAInputs(j[0].datosGarantiaLiberada);
					esChequeGarantia();
					esFianzaGarantia();
					ischequeEntregado();
					showAndHideURL();
				}
				$.unblockUI();
		}, error: function() {
			$.unblockUI();
		}
	});
}
function showAndHideURL(){
	$("#divFile").hide();
	if($("#existeDoctoGarantiaLiberada").val()==1){
		$("#divFile").show();
	}
}
function esChequeGarantia(){
	if($("#lchequeG").val()==1){
		onclickChequeGarantia();
	}
}
function esFianzaGarantia(){
	if($("#lFianzaG").val()==1){
		onclickFianzaGarantia();
	}
}
function onclickFianzaGarantia(){
	document.getElementById("inlineFianzaG").checked=true;
	document.getElementById("inlineChequeG").checked=false;
	$("#lchequeG").val(0);
	$("#lFianzaG").val(1);			
}
function onclickChequeGarantia(){
	document.getElementById("inlineChequeG").checked=true;
	document.getElementById("inlineFianzaG").checked=false;
	$("#lchequeG").val(1);
	$("#lFianzaG").val(0);
}
function onclickChequeEntregado(){
	$("#lChequeEntregado").val(0);
	if($('#inlineChequeEntregado').is(':checked')){
		$("#lChequeEntregado").val(1);
	}
}
function ischequeEntregado(){
	document.getElementById("inlineChequeEntregado").checked=false;
	if($("#lChequeEntregado").val()==1){
		document.getElementById("inlineChequeEntregado").checked=true;
	}
}
function saveItemsLiberaGarantia(){
	$.blockUI({message: "Procesando espere ......"});
	fillObject();
	jQuery.ajax({
	    url: '../../ContratacionFormalizadaServlet?tipoProceso=5&tipoOperacion=5',
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
				vaciarJsonAInputs(j[0].datosContratoGarantia);
				vaciarJsonAInputs(j[0].datosGarantiaLiberada);
				esChequeGarantia();
				esFianzaGarantia();
				ischequeEntregado();
				showAndHideURL();
		    	
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
	dataObject.append('tipoOperacion', 5);
	dataObject.append('cIdContratoDefinitivo', $("#cIdContratoDefinitivo").val());
	dataObject.append('lchequeEntregadoProveedor', $("#lChequeEntregado").val());
	dataObject.append('cOficioSolicitud', $("#nOficioSolicitud").val());
	dataObject.append('fFechaSolicitud', $("#fFechaOficioSol").val());
	dataObject.append('cOficioLiberacion', $("#nOficioLiberacion").val());
	dataObject.append('fFechaLiberacion', $("#fFechaOficioLibera").val());
	dataObject.append('cMotivoLiberacion', $("#cMotivoLiberacion").val());
	
	jQuery.each(jQuery('#nameArchivoLiberaGarantia')[0].files, function(i, file) {
	    dataObject.append('file-'+i, file);
	});
}
function agregaDatePickerFechas(){
	$("#fOficioSolicitud").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
		
	});
	$("#fFechaOficioLiberacion").datetimepicker({
		format: 'DD/MM/YYYY',
		altField: "#actualDate",
	 	currentText: "Now",
		changeYear: true
		
	});
}
function downloadFileLiberaGarantia(nTypeFile){
	window.open("../../servlet/ReportesGRM?"
		+"&operacion=4"
		+"&nTypeFile="+nTypeFile
		+"&cFolio="+$("#cFolio").val() 
		+"&cContratoDefinitivo="+$("#cIdContratoDefinitivo").val()
		+"&cContratoCNET="+$("#contratoCNET").val() 
		, 'Procesando', 'status=1, width=400px, height=200px, left=150px');

}

