var oTable;
var oTablePartidas;
var oTableFirmantes;
var oTableCatalogo;
var oTableClaves;
var oTablePagos;
var oTableRetencion;
var oTableAnticipo;
var oTableLineas;
var oTableDocumentos;
var oTablevFact;
var oTableLineasServ;
var oTableLineasBienes;
var oTablaAmpliaciones;
var oTablaAmpliacionesDetalle;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	//alert("tab: "+tab)
	switch (tab){
		case 0://consulta
			if($('#tblConsultaPedidos >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 2://partidas
			if($('#tblPartidas >tbody >tr').length>0){
				oTablePartidas.fnAdjustColumnSizing();
			}
			if($('#tblLineas >tbody >tr').length>0){
				oTableLineas.fnAdjustColumnSizing();
			}
		break;
		case 13://firmantes
			if($('#tblFirmantesPedido >tbody >tr').length>0){
				oTableFirmantes.fnAdjustColumnSizing();
			}
			if($('#tblCatalogoFirmantes >tbody >tr').length>0){
				oTableCatalogo.fnAdjustColumnSizing();
			}
		break;
		case 3://presupuesto
			if($('#dt_clavepresup >tbody >tr').length>0){
				oTableClaves.fnAdjustColumnSizing();
			}
			
		break;
		case 4://precompromiso
			
		break;
		case 6://pagos
			if($('#tblPagos >tbody >tr').length>0){
				oTablePagos.fnAdjustColumnSizing();
			}
		break;
		case 11://Ampliaciones
			if($('#tblLineasServ >tbody >tr').length>0){
				oTableLineasServ.fnAdjustColumnSizing();
			}
			if($('#tblLineasBienes >tbody >tr').length>0){
				oTableLineasBienes.fnAdjustColumnSizing();
			}
			if($('#tblAmpliaciones >tbody >tr').length>0){
				oTablaAmpliaciones.fnAdjustColumnSizing();
			}
			if($('#tblDetalleAmpliaciones >tbody >tr').length>0){
				oTablaAmpliacionesDetalle.fnAdjustColumnSizing();
			}
			if($('#tblSuficienciaDetalleAmpliaciones >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 17://Retenciones
			if($('#dt_retencion >tbody >tr').length>0){
				oTableRetencion.fnAdjustColumnSizing();
			}
			if($('#dt_anticipos >tbody >tr').length>0){
				oTableAnticipo.fnAdjustColumnSizing();
			}
		break;
		case 18://Factura global
			if($('#grdValidaFacturas >tbody >tr').length>0){
				oTablevFact.fnAdjustColumnSizing();
			}
		break;
		case 19://documentos físicos
			if($('#tblDocContrato >tbody >tr').length>0){
				oTableDocumentos.fnAdjustColumnSizing();
			}
		break;
	}
}
function showAndHideTabs(){
	$( "#nuevoContratoFONDEN" ).hide();
	$( "#nuevoContratoCap1000" ).hide();
	$( "#caratulaContrato" ).hide();
	$( "#partidasContrato" ).hide();
	$( "#firmantesContrato" ).hide();
	$( "#presupuestoContrato" ).hide();
	$( "#preCompromisoContrato" ).hide();
	$( "#pagosContrato" ).hide();
	$( "#imprimirContrato" ).hide();
	$( "#clausulasPedido" ).hide();
	$( "#plurianualidadContrato" ).hide();
	$( "#pasivoContrato" ).hide();
	$( "#ampliacionesContrato" ).hide();
	$( "#anticiposRetencion" ).hide();
	$( "#nuevasEps" ).hide();
	$( "#reclasificaTipoAdj" ).hide();
	$( "#documentacion" ).hide();
	$( "#actDatCNET" ).hide();
	$( "#facturaGlobal" ).hide();
	$( "#docContrato" ).hide();
	$( "#terminacionAnticipada" ).hide();
	
	if(parseInt($("#tbs").val())>0){
		$( "#caratulaPedido" ).show();
		$( "#caratulaContrato" ).show();
		$( "#partidasContrato" ).show();
		$( "#firmantesContrato" ).show();
		$( "#presupuestoContrato" ).show();
		$( "#preCompromisoContrato" ).show();
		$( "#imprimirContrato" ).show();
		if(cIdTipoContrato=="CS" || cIdTipoContrato=="CR"){
			$( "#presupuestoContrato" ).hide();
		}
		if(nIdEstado >= 2 && nIdEstado <= 4){
			$( "#docContrato" ).show();
			$( "#anticiposRetencion" ).show();
			if(nIdEstado == 4 ){
				$( "#nuevasEps" ).show();
				$( "#reclasificaTipoAdj" ).show();
				$( "#documentacion" ).show();
				$( "#actDatCNET" ).show();
				$( "#facturaGlobal" ).show();
				$( "#terminacionAnticipada" ).show();
				$( "#pagosContrato" ).show();
				if(nContratoAbierto==1){
					$( "#ampliacionesContrato" ).show();
				}
			}
		}
	}
}
function showAndHideTablePSP(){
	if(document.getElementById("checkLEsPSP").checked){
		//Muestra tabla
		showTablePSP();
		$("#nEsContratacionPSP").val(1);
		//document.getElementById("trPrestacionServicio").style.display="none";
		$("#trPrestacionServicio").hide();
		queryFormPost("mDeleteAllPrestacionServicioContrato", {async : false});
	}else{
		//Oculta Tabla
		clearSelectsPSP();
		hideTablePSP();
		$("#nEsContratacionPSP").val(0);
		//document.getElementById("trPrestacionServicio").style.display="block";
		$("#trPrestacionServicio").show();
		showPrestacionServicio();
	}
}
function esContratacionPSP(){
	if($("#nEsContratacionPSP").val()==1 || $("#nEsContratacionPSP").val()=="NULL"){
		document.getElementById("checkLEsPSP").checked=true
		showTablePSP();
		$("#trPrestacionServicio").hide();
	}else{
		//Oculta Tabla
		clearSelectsPSP();
		hideTablePSP();
		document.getElementById("checkLEsPSP").checked=false;
		showPrestacionServicio();
		$("#trPrestacionServicio").show();
	}
}
function hideTablePSP(){
	document.getElementById("trDatosPSP").style.display="none";	
}
function showTablePSP(){
	document.getElementById("trDatosPSP").style.display="block";
}

function clearSelectsPSP(){
	//$("#nEsContratacionPSP").val(0);
	$("#cAreaReq").val(0);
	$("#cAreaResp").val(0);
	$("#cCentroTrabajo").val(0);
	$("#nElPSPEsMaestro").val(0);
	$("#checkLEsMaestro" ).prop( "checked", false );
}
function disabledDatosPSP(){
	$('#cAreaReq').prop('disabled', true);
	$('#cAreaResp').prop('disabled', true);
	$('#cCentroTrabajo').prop('disabled', true);
	$('#checkLEsMaestro').prop('disabled', true);
	$('#checkLEsPSP').prop('disabled', true);
}
function consultaSelects(){
	$("#esperar").dialog("open");
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoCoordinaciones,"cAreaReq");
				llenaCombo(j[0].catalogoAreaResponsable,"cAreaResp");
				llenaCombo(j[0].catalogoCentroTrabajo,"cCentroTrabajo");
				llenaCombo(j[0].catalogoCentroTrabajo,"cCentroTrabajoCont");
				vaciarJsonAInputs(j[0].datosGuardados);
				vaciarJsonAInputs(j[0].datoContratacionPSP);
				checkMaestro();
				esContratacionPSP();
				if($("#nIdEstado").val()==4){
					disabledDatosPSP();
					$("#trAgregaServicio").hide();
				}
				$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function llenaObjectDat(){
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val(),
		cAreaRequirente:$("#cAreaReq").val(),
		cContratoDefinitivo:$("#cContratoDefinitivo").val()
	};
	return data0;
}
function obtieneAreasResponsables(){
	$("#esperar").dialog("open");
	$("#tipoOperacion").val(2);
	var object=llenaObjectDat();
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				$('#cAreaResp').empty();
				llenaCombo(j[0].catalogoAreaResponsable,"cAreaResp");
				$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function esMaestro(){
	if(document.getElementById("checkLEsMaestro").checked){
		$("#nElPSPEsMaestro").val(1);
	}else{
		$("#nElPSPEsMaestro").val(0);
	}
}
function checkMaestro(){
	if($("#nElPSPEsMaestro").val()==1){
		document.getElementById("checkLEsMaestro").checked=true;
	}else{
		document.getElementById("checkLEsMaestro").checked=false;
	}
}
function guardaDatosPSP(){
	$("#tipoOperacion").val(4);
	var object=llenaObjectCaratula();
	var resp=false;
	$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				resp=j[0].RESPUESTA;
				if(!resp || "false"==resp){
					swal({
						title: "",
						text: j[0].MENSAJE,
						icon: "warning",
						buttons: {
							confirm : "Cerrar"
							},
					}).then((continuar) => {
					});
				}
				$("#esperar").dialog("close");
		}, error: function() {
			$("#esperar").dialog("close");
		}
	});
	return resp;
	
}
function validaCapturaGarantia(){
	var resp=false;
	var montoTotalGarantia=(($("#mTotalGarantias").val()).replace(/,/g, '')).replace("$", "");
	if($("#lExcentaGarantia").val()==0 && parseFloat(montoTotalGarantia,10)<=0.00){
		resp=true;
	}
	return resp;
}
function llenaObjectCaratula(){
	var data0= {
		tipoProceso:$("#tipoProceso").val(),
		tipoOperacion:$("#tipoOperacion").val(),
		cAreaRequirente:$("#cAreaReq").val(),
		cAreaResponsable:$("#cAreaResp").val(),
		cCentroTrabajo:$("#cCentroTrabajo").val(),
		nElPSPEsMaestro:$("#nElPSPEsMaestro").val(),
		nEsContratacionPSP:$("#nEsContratacionPSP").val(),
		cContratoDefinitivo:$("#cContratoDefinitivo").val(),
		cIdProcedimiento:$("#cIdProcedimiento").val(),
		nIdConsecutivoAdj:$("#nIdConsecutivoAdj").val(),
		nIdFundamentoLeg:$("#cboFundamentoLeg").val(),
		nIdCategoriaProced:$("#cboCategoriaCaratula").val(),
		cConceptoContrato:$("#cConceptoV").val(),
		cContratoCNET:$("#cnumCompranet").val(),
		nCodExpedienteCNET:$("#nCodExpedienteCNET").val(),
		nCodContratoCNET:$("#nCodContratoCNET").val(),
		fFechaFormalizacion:$("#fechaFormalizacionV").val(),
		fFechaEntrega:$("#fechaEntregaV").val(),
		fFechaIninio:$("#fechaInicioV").val(),
		fFechaFin:$("#fechaFinV").val(),
		fFechaPropuestas:$("#fechaPropuestas").val(),
		fFechaSolicitud:$("#fechaSolicitud").val(),
		lEsDescentralizado:$("#esDescentralizado").val(),
		mMontoTotalGarantias:(($("#mTotalGarantias").val()).replace(/,/g, '')).replace("$", ""),
		mMontoGarantiaAnticipo:(($("#mGarantiaAnticipo").val()).replace(/,/g, '')).replace("$", ""),
		mMontoGarantiaCumplimiento:(($("#mGarantiaCumplimiento").val()).replace(/,/g, '')).replace("$", ""),
		llevaAnticipo:$("#nllevaAnticipo").val(),
		cMecanismosVigilancia:$("#cMecanismosVigilancia").val(),
		cDescripcionOtroImpuesto1:$("#cDescripcionOtroImpuesto1").val(),
		cMontoOtroImpuesto1	:(($("#cMontoOtroImpuesto1").val()).replace(/,/g, '')).replace("$", ""),
		cDescripcionOtroImpuesto2:$("#cDescripcionOtroImpuesto2").val(),
		cMontoOtroImpuesto2:(($("#cMontoOtroImpuesto2").val()).replace(/,/g, '')).replace("$", ""),
		cDescripcionOtroImpuesto3:$("#cDescripcionOtroImpuesto3").val(),
		cMontoOtroImpuesto3:(($("#cMontoOtroImpuesto3").val()).replace(/,/g, '')).replace("$", ""),
		cDescripJustTipoProced:$("#descripJustTipoProced").val(),
		lJustificaTipoProced:$("#lJustificaTipoProced").val(),
		cOficioDG:$("#oficioDG").val(),
		cFolioMASCP: $("#folioMASCP").val(),
		mMontoMensual:(($("#mMontoMensual").val()).replace(/,/g, '')).replace("$", ""),
		cDenominacionProyecto: $("#cDenominacionProyecto").val(),
		nIdEstado:$("#nIdEstado").val(),
		cEjercicio:$("#cEjercicio").val(),
		cUnidadEjecutora:$("#cIdUnidadEjecutora").val(),
		rfc:$("#cIdRFC").val(),
		cIdTipoContrato:$("#cIdTipoContrato").val(),
		cIdTipoProcedimiento:$("#cIdTipoProcedimiento").val(),
		nIdConsecutivoCont:$("#nIdConsecutivo").val(),
		lExcentaGarantia:$("#lExcentaGarantia").val(),
		nIdConsecutivoProced:$("#nIdConsecutivoProcedimiento").val(),
		
	};
	return data0;
}
function OtorgaAnticipo(){
	if(document.getElementById("checkLlevaAnticipo").checked){
		$("#nllevaAnticipo").val(1);
		$("#mGarantiaAnticipo").prop("readonly",false);
	}else{
		$("#nllevaAnticipo").val(0);
		$("#mGarantiaAnticipo").val(0);
		$("#mGarantiaAnticipo").prop("readonly",true);
	}
	sumaTotalGarantias();
}
function agregaCentroTrabajo(){
	var cadenaADividir=$("#cCentroTrabajoCont option:selected").text();
	var arrayDeCadenas = cadenaADividir.split("-");
	$("#cCentroTrabajoContDescrip").val(arrayDeCadenas[1].trim());
	queryFormPost("addPrestacionServicio", {async : false,
		callback : function() {
			showPrestacionServicio();
		}
	});
	//insert into mContratoServicioPrestado (cIdcontratoDefinitivo,nCentroTrabajo,cDescripcion) values(?,?,?)
}
function showPrestacionServicio(){
	var func="'"+$("#cContratoDefinitivo").val()+"'"
	$("#tblervicioEntrega").dataTable({
		sScrollX: "100%",
		sScrollY: "100%",
		bScrollCollapse: true,
		bDestroy: true,
		bAutoWidth: true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Pedidos",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ContratoServicioPrestado("+func+")",
		//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mContratoServicioPrestado&qw=" + qw,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			
			{ sName: "nCentroTrabajo" },
			{ sName: "cDescripcion" },
			{ sName: "borrar" }
			
		]
	});	
}
function borrarCentroTrabajo(nCentroTrabajo){
	$("#nIdCentroTrabajo").val(nCentroTrabajo);
	queryFormPost("mDeletePrestacionServicioContrato", {async : false,
		callback : function() {
			showPrestacionServicio();
		}
	});
}
function excentaGarantia(){
	$("#lExcentaGarantia").val(0);
	document.getElementById("checkLlevaAnticipo").checked=false;
	if($('#checkExcentoGarantia').is(':checked')){
		$("#lExcentaGarantia").val(1);
		$("#mTotalGarantias").val(0);
		$("#mGarantiaCumplimiento").val(0);
		$("#mGarantiaAnticipo").val(0);
		$("#mGarantiaCumplimiento").prop("readonly",true);
		$("#mGarantiaAnticipo").prop("readonly",true);
		$('#checkLlevaAnticipo').prop('disabled', true);
	}else{
		$("#mGarantiaCumplimiento").prop("readonly",false);
		$("#mGarantiaAnticipo").prop("readonly",false);
		$('#checkLlevaAnticipo').prop('disabled', false);
	}
	OtorgaAnticipo();
}
function seExcentoGarantia(){
	document.getElementById("checkExcentoGarantia").checked=false;
	document.getElementById("mGarantiaCumplimiento").style.readonly=false;
	document.getElementById("mGarantiaAnticipo").style.readonly=false;
	if($("#lExcentaGarantia").val()==1){
		document.getElementById("checkExcentoGarantia").checked=true;
		$("#mGarantiaCumplimiento").prop("readonly",true);
		$("#mGarantiaAnticipo").prop("readonly",true);
		$('#checkLlevaAnticipo').prop('disabled', true);
	}
	OtorgaAnticipo();
}