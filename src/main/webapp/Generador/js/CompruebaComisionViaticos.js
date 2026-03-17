let modalCuentaBancaria;

$(document).ready(function() {
	
	modalCuentaBancaria = new bootstrap.Modal(document.getElementById('dialog-modificaCuenta'), 'data-bs-backdrop');
		
	$(".modal-dialog").draggable({  handle: ".modal-header", });	
	queryFormPost("leeValoresInicialesViaticos", {async: false});
	queryFormPost("leeTotalesAgenda", {async: false});
	queryFormPost("leeTotalesTransporte", {async: false});
	queryFormPost("leeTieneCertificado", {async: false});
	
	formatoDatos();
	
	if ($("#tieneCertificado").val() == 0 ) {
		$("#divCertificadoTransito").hide();
	} else {
		$("#divCertificadoTransito").show();
	}
	
	$('#rg-list a').on('click', function (e) {
			  e.preventDefault()
			  $(this).tab('show')
	})
			
	$('#comprobacion-list a').on('click', function (e) {
			  e.preventDefault()
			  $(this).tab('show')
	})
	
	$("#leyendaTipoCambio").hide();
	$('#comprobacion-list a[href="#pagos"]').on('click', function (e) {
		  e.preventDefault()
		  $(this).tab('show')
		  validaTipoCambio();
		  compruebaPago();
		  validaTieneAnticipos();
		  
			desactivarBotones();
			
			if ($("#cDocHaplicado").val() =="F") {
					$("#nuevoBtn").hide();
					$("#nuevoRGBtn").hide();
					$("#nuevoCVBtn").hide();
					$("#btnFinalizar").hide();
					$('#comprobacion-list a[href="#editar"]').hide();
					if($("#urUsuario").val() == "A02"){
						$("#btnDesfinalizar").show();
					} else {
						$("#btnDesfinalizar").hide();
					}
					
			}
	
	})	
			
	$('#comprobacion-list a[href="#editar"]').on('click', function (e) {
		  e.preventDefault()
		  $(this).tab('show')
		  editaPago();
		  setValores();
			
		  querySelectPost("cat_tipoMoneda", "cMoneda",{async: false });
		  
	})	

	
	
	$("#capturaCorreo").hide();
	
	creaTableAgendaConsulta();
	creaTableTransporteConsulta();
	creaTableAutoriza();
	creaTableAutorizaBitacora();
	
	//Pestaña de edicion
		creaTableAgenda();
		creaTableTransporte();
		setFechas();		 
		cargaPaises();
		cargaEstados();
		cargaMunicipios();
		activarBotones();
		cargaNiveles();
		activarPaquete();
		cargaPaquetes();
		cargaTransporte();
		$("#btnEditarAgenda").hide();
		$("#btnEditarTrans").hide();
		querySelectPost("cargaNombreComision2", "nombreCom",{async: false });
});

	
var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		}	
	
	};

function formatoDatos() {
	
	let totalAgenda = parseFloat( $("#totalAgenda").val() * 100 ) / 100;
	let totalTransporte = parseFloat( $("#totalTransporte").val() * 100 ) / 100;
	$("#totalTransporte").val(totalTransporte.toFixed(2));
	$("#totalAgenda").val(totalAgenda.toFixed(2));
	$("#totalGeneral").val((totalAgenda + totalTransporte).toFixed(2));
	
	let totalDias = parseFloat($("#diasNacional").val()) + parseFloat($("#diasInternacional").val())
				+ parseFloat($("#totalDias").val())
				
	$("#totalAcDias").val(totalDias);
	
}

function validaTieneAnticipos() {
	queryFormPost("tieneAnticiposRead", {async: false});
	
}


function avanzarSolicitud() {
	if($("#cEvento").val()=="" || $("#cEvento").val() === null) {
		Swal.fire("Seleccione evento", "El evento no puede estar vacio, seleccione y vuelva a Avanzar","warning");
		return false;
	}
	
	if ($("#nidTipo").val() == 2  && $("#cEvento").val() =='GCRE') {
		//Verifica si hay anticipo	
		if (quitaFmt($("#sumTotal").val()) == 0 ) {
			Swal.fire("Verifique", "No se puede comprobar un viatico que no tiene una solicitud de Viatico Anticipado, seleccione el evento correcto o genere primero el viatico Anticipado", "warning");
			return false;
		}
		
	} else  {
		if (quitaFmt($("#mTotal").val()) < Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val()))) {
			Swal.fire("Verifique", "El monto de la solicitud es mayor al total del Viatico, verifique los importes.", "warning");
			return false;
		}
		
	}
	
	if (quitaFmt($("#mAgenda").val()) < Number(quitaFmt($("#mTotal1").val())) ) {
			Swal.fire("Verifique", "El monto de la agenda no puede ser mayor a los viaticos capturados, verifique los importes.", "warning");
			return false;
		}
		
	//Valida que los importes no sean cero
	if (($("#mTotalLocal").val() == 0 && $("#mTotal1").val() == 0) && $("#cDocumento").val() != 'COMSINVIATICOS' ){
		Swal.fire("Los importes no pueden ser cero", "Debe especificar los detalles de la comisión o el transporte","warning");
		return false;
		
	} else {
		
		let guardado = avanzaTramite();
		return guardado;
	} 
}

function avanzaTramite() {
	var guardado = false;
	var logErrores = "";
	$.blockUI({message: "Procesando espere ......"});
	
	$.ajax({
			url : "../viaticos/iniciaCasoRG",
			type : 'post',
			async : false,
			data : {
				idComision :  	$("#idComision").val(),
				nidTipo :  		$("#nidTipo").val(),				
				nombreVoBo :  	$("#nombreVoBo").val(),
				nombreAut :  	$("#nombreAut").val(),
				nombreElabora :  $("#nEmpleadoElabora").val(),
				cEsFirmaElectronica :  $("#cEsFirmaElectronica").val(),
				mTotal1 :  		quitaFmt($("#mTotal1").val()),
				mTotalLocal :  	quitaFmt($("#mTotalLocal").val()),
				mPasaje:  		quitaFmt($("#mPasaje").val()),
				mTaxi:  		quitaFmt($("#mTaxi").val()),
				mPeaje:  		quitaFmt($("#mPeaje").val()),
				mHotel:  		quitaFmt($("#mHotel").val()),
				mConsumos:  	quitaFmt($("#mConsumos").val()),
				mOtros:  		quitaFmt($("#mOtros").val()),
				mPasajeLocal:  	quitaFmt($("#mPasajeLocal").val()),
				mTaxiLocal:  	quitaFmt($("#mTaxiLocal").val()),
				mGasolinaLocal: quitaFmt($("#mGasolinaLocal").val()),
				mPeajeLocal: 	quitaFmt($("#mPeajeLocal").val()),
				mMaritimoLocal: quitaFmt($("#mMaritimoLocal").val()),
				mAereoLocal: 	quitaFmt($("#mAereoLocal").val()),
				cEvento : 		$("#cEvento").val(),
				folioReemplazo : $("#cTramiteSelect").val()
			},
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
				$.unblockUI();
				if (exito) {
					guardado = true;
					folioGenerado = j.messageList[0];
					$("#nFolioPago").val(folioGenerado);
					$('[href="#detalleRG"]').tab().show();
				} else {
					var errores = j.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						logErrores = logErrores + errores[cnt] + "\n";
					}
				}
			},
			error : function(errorThrown) {
				$.unblockUI();
				logErrores = errorThrown.ERROR;
			}
		});
	
		if (!guardado)
			Swal.fire("Error al avanzar la solicitud", logErrores, "error")
			
		return guardado;
		

}

function creaTableAgendaConsulta(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableAgenda = $('#tablaAgendaConsulta').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_AgendaViaticos&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nidAgenda"
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				},  {
					sName : "destino"
				}, {
					sName : "cMotivoComision"
				}, {
					sName : "mCuotaPorDia"
				}, {
					sName : "dias"
				}, {
					sName : "Importe"
				}, {
					sName : "nPorcentaje"
				}],
				oLanguage : es_mx
			});			
			
	}
	
function creaTableTransporteConsulta(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableTransporte = $('#tablaTransporteConsulta').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vTransporteLocal&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nIdTransporte"
				}, {
					sName : "nIdTipo"
				}, {
					sName : "cOrigen"
				}, {
					sName : "mMonto"
				}, {
					sName : "mKm"
				}, {
					sName : "cNumEconomico"
				}, {
					sName : "cTieneVales"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	function creaTableAutoriza(){
		sWhere ="nFolio =" + $("#idComision").val();
		oTableAutorizaciones = $('#tablaAutoriza').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_FirmantesViaticos&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nrenglon"
				}, {
					sName : "cNombre"
				}, {
					sName : "cPuesto"
				}, {
					sName : "dfechaAutoriza"
				}, {
					sName : "cNota"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	function creaTableAutorizaBitacora(){
		sWhere ="nFolio =" + $("#idComision").val();
		oTableAutBitacor= $('#tablaAutorizaBitacora').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_FirmantesViaticosBitacora&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nrenglon"
				}, {
					sName : "cNombre"
				}, {
					sName : "cPuesto"
				}, {
					sName : "dfechaAutoriza"
				}, {
					sName : "dFechaModificacion"
				}],
				oLanguage : es_mx
			});			
			
	}

function editaPago() {
	$("#operacion").val("EDITAR");
	//$("#cCuota").val("1700");
	cargaCuota();
}

function cargaCuota() {
		if (document.querySelector("#chk_homologa").checked) {
			queryFormPost("consultaTiposNivelesRead", {async:false});
		} else {
			queryFormPost("consultaCuotaEmpleado", {async:false});		
		}
}

function formatoTotales(){
	queryFormPost("consultaTotalesAntViaticos", {async: false});
	queryFormPost("consultaTotalCompViaticos", {async: false});
	queryFormPost("consultaDevoluciones", {async: false});
	
	let totalAgenda = quitaFmt($("#totalAgenda").val());
	let totalTransporte = quitaFmt($("#totalTransporte").val());
	
	let agenda = currencyFormatter({currency:'USD', value: totalAgenda })
	$("#mAgenda").val(agenda)
	
	let transp = currencyFormatter({currency:'USD', value: totalTransporte })
	$("#mTransporte").val(transp);
	
	let tot = currencyFormatter({currency:'USD', value: quitaFmt($("#totalGeneral").val()) })
	$("#mTotal").val(tot);
	
	let saldo = Number(quitaFmt($("#mTotal").val())) - Number($("#sumTotal").val()) + Number($("#mDevolucion").val());
	$("#mSaldo").val(saldo);
	
	let comprobacion = Number($("#sumAgendaC").val()) + Number($("#sumTransporteC").val());
	$("#sumComprueba").val(comprobacion.toFixed(2));
	
	let porComprobar = Number(quitaFmt($("#sumTotal").val())) - $("#sumComprueba").val();
	$("#sumPorComprobar").val(porComprobar.toFixed(2));
		
	saldo = currencyFormatter({currency:'USD', value: ($("#mSaldo").val()) })
	$("#mSaldo").val(saldo);
	
	comprobacion = currencyFormatter({currency:'USD', value: ($("#sumComprueba").val()) })
	$("#sumComprueba").val(comprobacion);
	
	porComprobar = currencyFormatter({currency:'USD', value: $("#sumPorComprobar").val() })
	$("#sumPorComprobar").val(porComprobar);
	
	let sumTotal = currencyFormatter({currency:'USD', value: ($("#sumTotal").val()) })
	$("#sumTotal").val(sumTotal);
	
}

function compruebaPago() {
	$("#operacion").val("COMPROBACION");
	$("#firmantesDiv").show();
	
	$("#cRFC").val($("#cIdRFC").val());
	$("#nombre").val($("#cnombre").val());
	$("#noEmpleado").val($("#nEmpleado").val());
	$("#nivel").val($("#cNivel").val());
	$("#plaza").val($("#cPlaza").val());
	$("#fAplicacion").val($("#fechaAplicacion").val());
	let totalAgenda = quitaFmt($("#totalAgenda").val());
	
	formatoTotales();
	
	queryFormPost("firmanteElaboraViatRead", {async: false});
	queryFormPost("consultaTienePagosViaticos", {async: false});
	
	if ($("#tienePagos").val()==0 ){
		
		queryFormPost("consultaTotalPasajeSinPagos", {async: false});
		let total = parseFloat( $("#sumTransporte").val() * 100 ) / 100	
			
		$("#mTotal2").val(totalAgenda);
		$("#mTotalLocal").val(total.toFixed(2));	
		
	} else {
		
		queryFormPost("consultaTotalPasajeLocal", {async: false});	
		let totalLocal = parseFloat( $("#sumTransporte").val() * 100 ) / 100
		let total = parseFloat( $("#totalViaticos").val() * 100 ) / 100	
		
		$("#mTotalLocal").val(totalLocal.toFixed(2));
		$("#mTotal2").val(total.toFixed(2));
		
	}
	
	let pasajeLocal = parseFloat( $("#sumPasajeLocal").val() * 100 ) / 100
	let taxi = parseFloat( $("#sumTaxiLocal").val() * 100 ) / 100
	let peaje = parseFloat( $("#sumPeajeLocal").val() * 100 ) / 100
	let gasolina = parseFloat( $("#sumGasolinaLocal").val() * 100 ) / 100
	let maritimo = parseFloat( $("#sumMaritimoLocal").val() * 100 ) / 100
	let aereo = parseFloat( $("#sumAereoLocal").val() * 100 ) / 100
	
	
	$("#mPasajeLocal").val(pasajeLocal.toFixed(2));
	$("#mTaxiLocal").val(taxi.toFixed(2));
	$("#mPeajeLocal").val(peaje.toFixed(2));
	$("#mMaritimoLocal").val(maritimo.toFixed(2));
	$("#mAereoLocal").val(aereo.toFixed(2));
	$("#mGasolinaLocal").val(gasolina.toFixed(2));
	
	creaTableBoletos();
	creaTicketAsignados();
	creaTablaSolicitud();
	creaTableTaxis();
	$('.dataTables_scrollHead').hide();
	
	$("#btnAvanzar").hide();
	$("#btnGuardar").hide();
	$("#firmantesDiv").hide();

}


function validaTipoCambio() {
	queryFormPost("tieneOtroTipoCambio", {async: false});
	
	if ($("#otroTipoCambio").val() > 0 ) {
		//validar si ya tenia tipo de cambio sino solo mostrar leyenda para abrir dialog
		queryFormPost("leeImporteTipoCambio", {async: false}); 
		if ($("#mImporteTipoCambio").val() === null || $("#mImporteTipoCambio").val() ==""|| $("#mImporteTipoCambio").val() =="0.0000" ){
			modalTipoCambio.show();
			$("#leyendaTipoCambio").show();
		}	else {
			$("#leyendaTipoCambio").show();
		}	
			
	}
	
}

function imprimir(){
	window.open(
		"../admin/SeguridadCatalogos?"
		+ "catalogo=CONTRARECIBO"
		+ "&accion=run"
		+ "&rn=PolizaViaticos.jasper"
		+ "&whereFolio=" + $("#idComision").val(),			
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");
}

function imprimeCertificado(){
	window.open(
		"../admin/SeguridadCatalogos?"
		+ "catalogo=CONTRARECIBO"
		+ "&accion=run"
		+ "&rn=polizaCertificadoTransito.jasper"
		+ "&whereFolio=" + $("#idComision").val(),			
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");
}

function desactivarBotones() {
	queryFormPost("consultaTieneComSinViaticos", {async: false});
	if ($("#tieneComSinViaticos").val() > 0) {
		$("#nuevoCVBtn").hide();
	}
	
	if ($("#mSaldo").val() == "$0.00" && $("#sumPorComprobar").val() == "$0.00" ){
				$("#nuevoBtn").hide();
				$("#nuevoRGBtn").hide();
				if($("#urUsuario").val() == "A02"){
					$("#btnFinalizar").show();
				} else {
					$("#btnFinalizar").hide();
				}
			}
}

function abrirDlgCuentaBancaria() {
	querySelectPost("cuentasBancariasRFC", "CTAB",{async: false });	
	modalCuentaBancaria.show();	
}

function actualizaCuentaBancaria() {
	$("#cuentaBcoMod").val($("#CTAB").val());
	queryFormPost("actualizaCTAB", {async: false});
	modalCuentaBancaria.hide();
	$("#cuentaBancaria").val($("#cuentaBcoMod").val());
}