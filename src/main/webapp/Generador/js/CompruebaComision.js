let oTablevFact;
let oTableSolicitud;
let oTableBoleto;
let bClicBtn = false;
let modalCorreo;
let modalFactura;
let modalTipoCambio;
let modalObservaciones;
let modalModificaAgenda;
let modalDetalleAgenda;
let modalAdjuntaCB;
let modalReemplazo;
let noComprobable = 0;
let showId = false;
let arrPago = [];
let logErrores = ""; 
let importeRet= 0;
let modalJustif;
let modalBoletos;
let modalGasolina;
let msg ="";

$(document).ready(function() {	
	queryFormPost("leeValoresInicialesViaticos", {async: false});
	queryFormPost("leeTotalesAgenda", {async: false});
	queryFormPost("leeTotalesTransporte", {async: false});
	queryFormPost("consultaTieneReintegroContable", {async: false});
	querySelectPost("fuenteFinanciemientoViaticos", "cFuenteFinanciamiento",{async: false });
	queryFormPost("leeTotalAnticiposViaticos", {async: false});

	queryFormPost("cargaNombreComision","idNombre",{async: false});

	$("#cUnidadResponsable").val($("#urUsuario").val());
	if($("#urUsuario").val() == "A02"){
		$("#btnFinalizar").show();
		showId = true;
	} else {
		$("#btnFinalizar").hide();
	}	
	
	if ($("#cDocHaplicado").val() =="F") {
		$('#comprobacion-list a[href="#editar"]').hide();
	}
	
	$("#btnDesfinalizar").hide();
	$("#nidComision").val($("#idComision").val());
	let totalAgenda = parseFloat( $("#totalAgenda").val() * 100 ) / 100;
	let totalTransporte = parseFloat( $("#totalTransporte").val() * 100 ) / 100;
	$("#totalTransporte").val(totalTransporte.toFixed(2));
	$("#totalAgenda").val(totalAgenda.toFixed(2));
	$("#totalGeneral").val((totalAgenda + totalTransporte).toFixed(2));

	//Se comento para el diseño de pantallas
	$('[href="#detalleRG"]').tab().hide();
	$('[href="#resumenRG"]').tab().hide();
	
	ClassReadOnly("inputDetalle", false);

	querySelectPost("CatalogoOtrasRetencionesRead", "cOtraRetencion",  {async: false});
	querySelectPost("CatalogoRetencionesRead", "cTipoRetencion",  {async: false});
	$("#divSelAnticipo").hide();
	
	$("#cTipoRetencion").change(function(){ 
		$("#nTipoRetencion").val($("#cTipoRetencion").val());
		
		if ($("#nTipoRetencion").val()==0) {
			$("#mImporteIva").val("0.00");
			$("#mImporteISR").val("0.00");
		
		} else {
			queryFormPost("calculaRetencionIVA", {async: false});
			queryFormPost("validaResico",{async: false });
			
			if($("#esResico").val() > 0 ){
				queryFormPost("readPorcRetencionISR", {async: false});
			} else {
				queryFormPost("calculaRetencionISR", {async: false});	
			}	
			
			$("#mImporteIva").val(Number(quitaFmt($("#mImporteSinIVA").val()) * $("#nPorcRetencionIva").val()).toFixed(2) );
			$("#mImporteISR").val(Number(quitaFmt($("#mImporteSinIVA").val()) * $("#nPorcRetencion").val()).toFixed(2) );
		}
	});
	
	$("#cOtraRetencion").change(function(){ 
		$("#nTipoRetencion").val($("#cOtraRetencion").val());
		
		if ($("#nTipoRetencion").val()==0) {
			$("#mOtras").val("0.00");
		} else {
			queryFormPost("calculaOtrasRetenciones", {async: false});				
			$("#mOtras").val(Number(quitaFmt($("#mImporteSinIVA").val()) * $("#nPorcRetencionOtras").val()).toFixed(2) );
		}
	});
		
	$("#guardarBtn").button().click(function() {
		generaSolicitud();
	});
	
	$("#btnBorrar4").button().click(function() {
		borrarDatos();
	});
	
	$("#btnBorrar2").button().click(function() {
		borrarDatos();
	});
	
	$("#btnBorrar3").button().click(function() {
		borrarDatos();
	});
	
	$("#divRetenciones").hide();
	
	if ($("#cTieneReintegroContable").val() == 0 ){
		$("#detalleReemplazo").hide();
	}
	
	//Se oculto el boton por cierre de año
	//$("#nuevoRGBtn").hide();
	
	$("#chk_tieneRete").change(function(){ 
		if ($("#chk_tieneRete").prop("checked")) {
			Swal.fire("Atencion!","Debe cargar únicamente las facturas con retenciones diferentes a RESICO", "info");
			$("#divRetenciones").show();
		} else {
			$("#divRetenciones").hide();
		}
	});
	
	$("#chk_reemplazo").change(function(){ 
		if ($("#chk_reemplazo").prop("checked")) {
			querySelectPost("consultaSolicitudesReemplazar", "cTramiteSelect");
			modalReemplazo.show();
		} else {
			modalReemplazo.hide();
		}
	});
	
	$("#avanzaSolBtn").button().click(function() {
		
			revisaBoletosAvion();	
	});
	
	$("#avanzaFact").button().click(function() {
		if(($("#concepto").val()).trim()=="" || ($("#informeComision").val()).trim() =="") {
			Swal.fire("Capture","El concepto y el informe de Comision no debe estar vacio", "info");
			return false;
		}
	
	 	if (Number(quitaFmt($("#importenoComprobable").val())) > 0 || quitaFmt($("#importenoComprobable").val()) =="") {
			noComprobable = Number(quitaFmt($("#importenoComprobable").val()));
		}
	 
		
		if (Number($("#importeEdicion").val())==0){
			$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val())) + noComprobable + Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));
			$("#importeNeto").val(parseFloat(Number($("#mImporteBruto").val()) -Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));
		} else {
			$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicion").val())) + noComprobable +Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));
			$("#importeNeto").val($("#mImporteBruto").val() );	
		}
		
		if ( $("#cOtraJustificacion").val()!= "" ) {
					$.ajax({
						url : "../viaticos/actualizarOtraJustif",
						type : 'post',
						async : false,
						data :{ cOtraJustificacion : $("#cOtraJustificacion").val(),
								folio : $("#nFolioPago").val()
						},
						dataType : 'json',
						success : function(j) {
							var exito = j.success;
							if (!exito) {
								alert ( "No se pudo guardar la justificacion");
								return false;
							}
						},
						error : function(errorThrown) {
							logErrores =  errorThrown.ERROR;
						}
				});
				
		}
				
		
		if(revisaFacturas() ) {
			 validaRegimenFacturas();			
		} else {
			Swal.fire("Revise","El total de las facturas no coincide con el total de la comision y transporte capturado", "info");
		}
		
		
	});
	
	$("#avanzaMov").button().click(function() {
		validaEventoAnticipo ();
		
		if(avanzarDetalle()) {
			if(validaPartidas()){
				$("#firmantesDiv").show();
				queryFormPost("firmanteElaboraViatRead", {async: false});
				$("#firmaElectronica").prop("checked", true);
				$("#btnGuardar").show();
				$("#guardarBtn").show();
				$("#avanzaMov").hide();
					
			}
		}
	});
	
	$("#btnAceptarDlgEP").button().click(function() {
		guardaMontos();
	});
	
	mostrarDetalle("detalleSol", false);
	
	$("#divCardBoletos").hide();
	
	$("#nuevoBtn").button().click(function() {
		$("#cDocumento").val("CAJA");
		$("#cTipoPago").val("CAJA");
		$("#nidTipo").val("1");
		mostrarDetalle("detalleSol", true);	
		iniciaFirmantes();
		$("#detalleComision").show();
		$("#cardBodyDetalleRG").hide();
		$("#cardBodyDetalleCaja").show();
		$("#lblEvento").show();
		$("#divEvento").show();
		$("#btnAvanzar").hide();
		$("#btnGuardar").show();
		$("#firmantesDiv").show();
		$("#divComisionSV").hide();
		$("#divCardBoletos").hide();
		$("#divExpediente").show();
		
		if (Number(quitaFmt($("#mSaldo").val())) == 0 || $("#permiteAnticipos").val() > 0 ) {
			querySelectPost("consultaEventoViaticosDev", "cEvento",  {async: false});
			
		} else {
			queryFormPost("consultaTotalesAntViaticos", {async: false});
			querySelectPost("consultaEventoViaticos", "cEvento",  {async: false});
			let importe = Number(quitaFmt($("#mAgenda").val())) - Number($("#mImporteAnticipoViat").val() + Number(quitaFmt($("#sumAgendaAnticipo").val())) );
			if (importe < 0 ) {
				importe = 0;
			}
			$("#mTotal2").val( importe );
		}
		
		queryFormPost("firmanteElaboraViatRead", {async: false});
		$("#firmaElectronica").prop("checked", true);
		$("#mTotalGral").val((Number(quitaFmt($("#mTotal2").val())) + Number(quitaFmt($("#mTotalLocal").val()))).toFixed(2));
		
	});
	
	$("#nuevoRGBtn").button().click(function() {
		modalFactura = new bootstrap.Modal(document.getElementById('dialog-validaFact'), 'data-bs-backdrop');
		$("#cDocumento").val("RELACIONGASTOS");
		$("#cTipoPago").val("RELACIONGASTOS");
		$("#cIdRFC_RelacionGasto").val($("#cRFC").val());
		$("#cIdRFC").val($("#cRFC").val());
		$("#nidTipo").val("2");
		mostrarDetalle("detalleSol", true);
		iniciaFirmantes();
		$("#detalleComision").show();
		$("#cardBodyDetalleRG").show();
		$("#cardBodyDetalleCaja").hide();
		$("#lblEvento").show();
		$("#divEvento").show();
		$("#btnAvanzar").show();
		$("#btnGuardar").hide();
		$("#firmantesDiv").hide();
		$("#divComisionSV").hide();
		$("#linkGasolina").hide();
		creaDTFacturas();
		$('.dataTables_scrollHead').hide();
		
		if (Number(quitaFmt($("#mSaldo").val()))  == 0 ) {
			querySelectPost("consultaEventoViaticosDev", "cEvento",  {async: false});
		} else {
			queryFormPost("consultaTotalesAntViaticos", {async: false});
			querySelectPost("consultaEventoViaticos", "cEvento",  {async: false});	
		}
		
		$("#mTotalGral").val((Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val()))).toFixed(2));
		$("#divCardBoletos").show();
		
		queryFormPost("consultaExisteRG", {async: false});
		
		if($("#rgExiste").val() > 0 ){
		 	queryFormPost("consultaFolioRG", {async: false});
		 	$("#nFolioPago").val($("#folioPago").val());
		 	queryFormPost("consultaComprobacionRG", {async: false});
			$("#mTotalGral").val((Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val()))).toFixed(2));
		 	
		 	if (verificaJustGasolina() ) {
		 		avanzarTabFacturas();
		 	}	
		 		 	
		 	queryFormPost("consultaExistenFacturasRG", {async: false});
		 	
		 	validaEdicion();
			$("#importeAuxNeto").val($("#importeNeto").val());
		 	
		 	queryFormPost("tieneClavesPresupuestales", {async:false});
		 
		 	if($("#tieneEpAgregada").val() > 0 ) {
						$('[href="#resumenRG"]').tab().show();
						$('#rg-list a[href="#resumenRG"]').trigger('click');
						$("#avanzaFact").hide();
						$("#btnBorrar4").hide();	
						$("#btnAvanzar").show();
						cambioFF();
			
		 	} else if ($("#tieneFacturas").val() > 0 ) {
				queryFormPost("leeImporteFacturas", {async:false});
				if(revisaFacturas() && revisaRetenciones() && ($("#informeComision").val()).trim()!="") {
					if (validaRegimenFacturas()) {
						$("#mTotalGral").val((Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val()))).toFixed(2));
						queryFormPost("consultaJustificacionRG", {async: false});
						$('[href="#resumenRG"]').tab().show();
						$('#rg-list a[href="#resumenRG"]').trigger('click');
						$("#avanzaFact").hide();
						$("#btnBorrar4").hide();	
						$("#btnAvanzar").show();
						
						if ($("#mImporteRetencion").val() == "") {
						} 
					}
				}
				
				if (Number($("#mImporteRetencion").val()) > 0 ){
						$("#chk_tieneRete").attr("checked", true);
						$("#divRetenciones").show();
						queryFormPost("RetencionesRead", {async:false});
						if (Number($("#mFacturaOtros").val()) > 0 ) {
							$("#divOtrasRete").show();
						} else {
							$("#divOtrasRete").hide();
						}
				} 	
			}
		 }
		 
	});
	
	$("#nuevoCVBtn").button().click(function() {
		$("#cDocumento").val("COMSINVIATICOS");
		$("#cTipoPago").val("COMSINVIATICOS");
		$("#nidTipo").val("3");
		mostrarDetalle("detalleSol", true);
		iniciaFirmantes();
		$("#detalleComision").hide();
		$("#lblEvento").hide();
		$("#divEvento").hide();
		$("#cardBodyDetalleRG").hide();
		$("#cardBodyDetalleCaja").hide();
		$("#btnAvanzar").hide();
		$("#btnGuardar").show();
		$("#firmantesDiv").show();
		$("#divCardBoletos").show();
		$("#divComisionSV").show();
		$("#btnBorrar3").hide();
		queryFormPost("consultaConceptoViaticos", {async: false});	
		queryFormPost("firmanteElaboraViatRead", {async: false});
		$("#firmaElectronica").prop("checked", true);
		
		$('[href="#detalleRG"]').tab().hide();
		$('[href="#resumenRG"]').tab().hide();
		$('#rg-list a[href="#boletosRG"]').trigger('click');
		
	});
	
	$("#btnFinalizar").button().click(function() {
		if ( revisaSaldo()) {
			if( tieneTramites() ){
				finalizaTramite();
			}			
		}
	});
	
	$("#btnDesfinalizar").button().click(function() {
			activarTramite();
	});
	
	$('#rg-list a[href="#resumenRG"]').on('click', function (e) {
		  e.preventDefault()
		  $(this).tab('show')
		  iniciaCapturaMovimientos();
		
	})

	$("#chk_EditaImporte").change(function(){
		if ($("#chk_EditaImporte").prop("checked")){
			Swal.fire('Nota:', 'Al usar esta opcion, el importe a editar debe ser menor al importe en Facturas. Al importe capturado se le sumarán las retenciones para los pasos siguientes', 'info');			
			
			if (Number($("#importeEdicionMaximo").val())==0){
				$("#chk_EditaImporte").prop("checked",false);
				Swal.fire('Importante', 'Primero se deben de cargar las facturas', 'warning');
				return;
			}
			
			document.getElementById("importeEdicion").removeAttribute("disabled",false);
			
		}else{
			$("#importeEdicion").val("0.00");
			$("#importeEdicion").attr("disabled",true);
		}			
	});
	
	
});
	
	modalTipoCambio = new bootstrap.Modal(document.getElementById('dialog-tipoCambio'), 'data-bs-backdrop');
	modalModificaAgenda = new bootstrap.Modal(document.getElementById('dialog-modifica'), 'data-bs-backdrop');
	modalObservaciones  = new bootstrap.Modal(document.getElementById('dialog-notas'), 'data-bs-backdrop');
	modalDetalleAgenda  = new bootstrap.Modal(document.getElementById('dialog-comprobacion'), 'data-bs-backdrop');
	modalCorreo = new bootstrap.Modal(document.getElementById('dialog-actualizaCorreo'), 'data-bs-backdrop');
	modalAdjuntaCB = new bootstrap.Modal(document.getElementById('dialog-adjuntarCB'), 'data-bs-backdrop');
	modalReemplazo = new bootstrap.Modal(document.getElementById('dialog-reemplazo'), 'data-bs-backdrop');
	modalJustif = new bootstrap.Modal(document.getElementById('dialog-justificacion'), 'data-bs-backdrop');
	modalBoletos = new bootstrap.Modal(document.getElementById('dialog-pases'), 'data-bs-backdrop');
	modalGasolina = new bootstrap.Modal(document.getElementById('dialog-gasolina'), 'data-bs-backdrop');
	
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
	
function onLoadPlantilla(idOperacion) {
	
	
	
}

function validaRegimenFacturas() {
			queryFormPost("actualizaActividadesRG", {async: false});
			queryFormPost("actualizaComisionComp", {async: false});
			
			/* valida RESICO */
			queryFormPost("validaResico",{async: false });
			queryFormPost("tieneRetencionesXML_RG_Read",{async: false });
			queryFormPost("tieneRetencionesCapturadas",{async: false });
			queryFormPost("importeRetencionRG",{async: false });
			
			/* Revisa las solicitudes y los regimenes para que tengan la retencion RESICO si corresponde */
			queryFormPost("consultaSolicitudesSinRetencion",{async: false });
			msg = $("#listaSol").val();
			
			if(msg != ""){
				Swal.fire("No tiene retenciones","La factura no tiene retenciones y el regimen es Resico (PERSONA FISICA):" + msg,"error");
				return false;
			}
			
			/* Revisa que el calculo del impuesto RESICO */
			queryFormPost("validaImporteResico",{async: false });
			msg = $("#msgRF").val();
			
			if(msg != ""){
				Swal.fire("Revise las facturas", msg,"error");
				return false;
			}

			if(validacionesRetencion()) {
					validaFacturasyFechas();
			} else {
				return false;
			}

}

function mostrarDetalle(clase, mostrar) {
	$("." + clase).each(function() {
		if (mostrar)
			$(this).show();
		else
			$(this).hide();
	});
	
}

function ClassReadOnly(clase, mostrar) {
	$("." + clase).each(function() {
		let conFormato = currencyFormatter({currency:'USD', value: quitaFmt($(this).val()) });
		$(this).val(conFormato);
		
		if (mostrar)
			$(this).attr("readonly", true); 
		else
			$(this).attr("readonly", false); 
	});
	
}



function sumaDetalle() {
	let total = 0;
	let totalGral = 0;
	total = Number(quitaFmt($("#mPasaje").val())) + Number(quitaFmt( $("#mTaxi").val())) + Number(quitaFmt($("#mPeaje").val())) 
		+ Number(quitaFmt($("#mHotel").val())) + Number(quitaFmt($("#mConsumos").val())) + Number(quitaFmt($("#mOtros").val()));
	
	//Relacion gastos
	if ($("#nidTipo").val() == 2) {
		total = currencyFormatter({currency:'USD', value: total });
		$("#mTotal1").val(total);	 
		totalGral = Number(quitaFmt($("#mPasajeLocal").val())) + Number(quitaFmt( $("#mTaxiLocal").val())) + Number(quitaFmt($("#mGasolinaLocal").val())) 
		 + Number(quitaFmt($("#mMaritimoLocal").val()))  + Number(quitaFmt($("#mAereoLocal").val()))  + Number(quitaFmt($("#mPeajeLocal").val())) + Number(quitaFmt( $("#mTotal1").val()));
		totalGral = currencyFormatter({currency:'USD', value: totalGral });
		$("#mTotalGral").val(totalGral); 
	
	//Caja
	} else if ($("#nidTipo").val() == 1) {
		totalGral = Number(quitaFmt($("#mPasajeLocal").val())) + Number(quitaFmt( $("#mTaxiLocal").val())) + Number(quitaFmt($("#mGasolinaLocal").val())) 
		 + Number(quitaFmt($("#mMaritimoLocal").val()))  + Number(quitaFmt($("#mAereoLocal").val()))   + Number(quitaFmt($("#mPeajeLocal").val())) + Number(quitaFmt( $("#mTotal2").val()));
		totalGral = currencyFormatter({currency:'USD', value: totalGral });
		$("#mTotalGral").val(totalGral);
	}
	
	let pasaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPasaje").val()) })
	$("#mPasaje").val(pasaje)
	
	let taxi = currencyFormatter({currency:'USD', value: quitaFmt($("#mTaxi").val()) })
	$("#mTaxi").val(taxi)
	
	let peaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPeaje").val()) })
	$("#mPeaje").val(peaje)
	
	let hotel = currencyFormatter({currency:'USD', value: quitaFmt($("#mHotel").val()) })
	$("#mHotel").val(hotel)
	
	let cons = currencyFormatter({currency:'USD', value: quitaFmt($("#mConsumos").val()) })
	$("#mConsumos").val(cons)
	
	let otros = currencyFormatter({currency:'USD', value: quitaFmt($("#mOtros").val()) })
	$("#mOtros").val(otros)	
	
	
}


function sumaTransporte() {
	let total = 0;
	total = Number(quitaFmt($("#mPasajeLocal").val())) + Number(quitaFmt( $("#mTaxiLocal").val())) + Number(quitaFmt($("#mGasolinaLocal").val()))  + Number(quitaFmt($("#mPeajeLocal").val())) 
			 + Number(quitaFmt($("#mMaritimoLocal").val()))  + Number(quitaFmt($("#mAereoLocal").val()))  ;
	total = currencyFormatter({currency:'USD', value: total });
	$("#mTotalLocal").val(total);
	
	let pasaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPasajeLocal").val()) })
	$("#mPasajeLocal").val(pasaje)
	
	let taxi = currencyFormatter({currency:'USD', value: quitaFmt($("#mTaxiLocal").val()) })
	$("#mTaxiLocal").val(taxi)	
	
	let gas = currencyFormatter({currency:'USD', value: quitaFmt($("#mGasolinaLocal").val()) })
	$("#mGasolinaLocal").val(gas)
	
	let peaje = currencyFormatter({currency:'USD', value: quitaFmt($("#mPeajeLocal").val()) })
	$("#mPeajeLocal").val(peaje)	
	
	let aereo = currencyFormatter({currency:'USD', value: quitaFmt($("#mAereoLocal").val()) })
	$("#mAereoLocal").val(aereo)
	
	let maritimo = currencyFormatter({currency:'USD', value: quitaFmt($("#mMaritimoLocal").val()) })
	$("#mMaritimoLocal").val(maritimo)		  
}

function consultaTipoEvento(){	
	queryFormPost("readTipoEvento", {async: false});
	
	if( $("#tipoEvento").val() =="D") {
		queryFormPost("tieneVariosAnticipos", {async: false});
		
		if( $("#tieneVariosAnt").val() > 1) {
			$("#divSelAnticipo").show();
			consultaFoliosCaja();
			
		} else {
			//Si solo tiene un anticipo se selecciona el que ya tiene
			queryFormPost("consultaAnticipoCaja", {async: false});
			queryFormPost("consultaImporteAnticipo", {async: false});
			queryFormPost("consultaImporteViaticosAnt", {async: false});
		}
		
	}
}

function consultaFoliosCaja() {
		//Implementar el select que llene los anticipos con saldo
		querySelectPost("ConsultaFoliosCajaConSaldo", "folioCaja",  {async: false});
		$("#nFolioCaja").val($("#folioCaja").val());
		queryFormPost("consultaImporteAnticipo", {async: false});
		queryFormPost("consultaImporteViaticosAnt", {async: false});
}

function formatoTotal() {
	let total = currencyFormatter({currency:'USD', value: quitaFmt($("#mTotal2").val()) })
	$("#mTotal2").val(total)	
}

function validaDetalle(importe) {
	//Si es de caja igualar los totales
	if( $("#nidTipo").val() == 1 )  {
		$("#mTotal1").val($("#mTotal2").val())
		$("#mTotalGral").val((Number(quitaFmt($("#mTotalLocal").val())) + Number(quitaFmt($("#mTotal1").val()))).toFixed(2));
	}
	//Validación el importe capturado no puede ser mayor al importe de la Agenda nunca
	let porAgregar = Number(quitaFmt($(importe).val())) ;
	let saldoAgenda = Number(quitaFmt($("#sumTotal").val())) - Number(quitaFmt($("#sumComprueba").val()));
	let saldoViaticos = Number(quitaFmt($("#mAgenda").val())) - Number(quitaFmt($("#sumAgendaAnticipo").val()));
	let saldoComision = Number(quitaFmt($("#mSaldo").val()));
	
	saldoAgenda = Number(saldoAgenda).toFixed(2);
	saldoViaticos = Number(saldoViaticos).toFixed(2);
	
		if (porAgregar < 0) {
			$(importe).val(0);
			Swal.fire("Verifique","Los importes deben capturarse en positivo, no acepta numeros negativos","warning");
			sumaDetalle();
		}
	
	//Validaciones para Comprobaciones y Devoluciones	
	if( ($("#nidTipo").val() == 1 || $("#nidTipo").val() == 2 ) && $("#tipoEvento").val() =='D' ){
		//valida que tenga anticipo
		queryFormPost("tieneAnticipoViaticos", {async: false});

		if($("#tieneAnticipo").val()==0) {
			Swal.fire("No tiene Anticipo", "No se puede realizar una devolución o comprobación antes de un Anticipo, cambie el Evento", "error");
			reiniciaValores("inputDetalle", true);
			return;
		} else {
			//Valida que el importe no sobrepase el saldo del anticipo
			let porAgregar = Number(quitaFmt($("#mTotal1").val())) 
			let traspLocal = Number(quitaFmt($("#mTotalLocal").val()));
			let suma = Number(quitaFmt($("#sumPorComprobar").val()));
			let total = porAgregar + traspLocal;
			
			if (total.toFixed(2) >  suma) {
				$(importe).val(0);
				Swal.fire("Verifique","El importe de la devolución no puede ser mayor al importe del Anticipo que es de " + suma,"warning");
				sumaDetalle();
			}
			
			//Valida que no sobrepase el saldo por concepto de Viaticos
			if(porAgregar > saldoAgenda){	
				$(importe).val(0);
				Swal.fire("Verifique","El detalle de la comisión no puede ser mayor al importe de los Anticipos por concepto de Viáticos, que es por: $" + saldoAgenda,"warning");
				sumaDetalle();
			}
		}
	} else if ($("#nidTipo").val() == 2  && $("#tipoEvento").val() =='X') {
		
		if(porAgregar > saldoComision){	
				$(importe).val(0);
				Swal.fire("Verifique","El detalle de la comisión no puede ser mayor al saldo de la comisión","warning");
				sumaDetalle();
			}
			
		if(porAgregar > saldoViaticos){	
				$(importe).val(0);
				Swal.fire("Verifique","El detalle de la comisión no puede ser mayor al saldo de Viáticos ","warning");
				sumaDetalle();
			}
	}  
	
	if( ($("#nidTipo").val() == 1 || $("#nidTipo").val() == 2 ) && $("#tipoEvento").val() =='A' ){
		//Valida que el antipo no sea mayor al importe de la Agenda
		//let valor = Number($(importe).val());
		let porAgregar = Number(quitaFmt($("#mTotal1").val()));
		let sumaAgenda =  Number(quitaFmt($("#mAgenda").val())) - Number(quitaFmt($("#sumAgendaAnticipo").val())) + Number(quitaFmt($("#mDevolucion").val())) ;
		
		if (porAgregar  >  sumaAgenda) {
			$(importe).val(0);
			Swal.fire("Verifique","El detalle de la comisión es mayor que el Saldo de la Agenda, el saldo es de: $" + sumaAgenda.toFixed(2),"warning");
			sumaDetalle();
		}
		
		//Valida que el total de Anticipos (agenda) no sea superado	
		if (porAgregar > saldoViaticos ) {
			Swal.fire("Verifique","Los viaticos capturados no puede sobrepasar al Importe de Viaticos Autorizado","warning");
			return false;
		}
	}
}

function validaDetalleTrans(importe) {
	if ($("#cEvento").val() ==null || $("#cEvento").val() =='0' ) {
		Swal.fire("Seleccione el evento","Antes de iniciar la captura debe seleccionar el evento", "info");
	}
	
	let porAgregar = Number(quitaFmt($("#mPasajeLocal").val())) + Number(quitaFmt($("#mTaxiLocal").val())) +Number(quitaFmt($("#mGasolinaLocal").val())) 
		+Number(quitaFmt($("#mMaritimoLocal").val())) +Number(quitaFmt($("#mAereoLocal").val())) +Number(quitaFmt($("#mPeajeLocal").val()));
	let importeT = Number(quitaFmt($(importe).val()));
	let saldoPendiente = Number($("#sumTransporteAnticipo").val())- Number($("#sumTransporteC").val())
	let transporte = Number(quitaFmt($("#mTransporte").val()));
	
	$("#mTotalGral").val((Number(quitaFmt($("#mTotalLocal").val())) + Number(quitaFmt($("#mTotal1").val()))).toFixed(2));
	
	if(porAgregar > transporte ){	
				$(importe).val(0);
				Swal.fire("Verifique","El importe del transporte Local no puede ser mayor al importe del Transporte total de la Agenda.","warning");
				sumaTransporte();
	}
	
	if (importeT < 0) {
			$(importe).val(0);
			Swal.fire("Verifique","Los importes deben capturarse en positivo, no acepta numeros negativos","warning");
			sumaTransporte();
		}
	
	if( ($("#nidTipo").val() == 1 || $("#nidTipo").val() == 2 ) && $("#tipoEvento").val() =='D' ){
		//valida que tenga anticipo
		queryFormPost("tieneAnticipoViaticos", {async: false});

		if($("#tieneAnticipo").val()==0) {
			Swal.fire("No tiene Anticipo", "No se puede realizar una devolución o comprobación antes de un Anticipo, cambie el Evento", "error");
			return;
		} else {
			//Valida que el importe no sobrepase el importe del anticipo
			let suma = Number(quitaFmt($("#sumPorComprobar").val())); 
			
			if (importeT  >  suma) {
				$(importe).val(0);
				Swal.fire("Verifique","El importe de la devolución o comprobación no puede ser mayor al importe del Anticipo que es de " + $("#sumPorComprobar").val(),"warning");
				sumaTransporte();
			}
			
			if(porAgregar > saldoPendiente)	{
				$(importe).val(0);
				Swal.fire("Verifique","El importe de la devolución o comprobación no puede ser mayor al saldo del anticipado por concepto de transporte, que es de: $" + saldoPendiente,"warning");
				sumaTransporte();
			}
			
		}
	} 
			
	if( ($("#nidTipo").val() == 1 || $("#nidTipo").val() == 2 ) && $("#tipoEvento").val() =='A' ){
		//Valida que el antipo no sea mayor al importe de la Agenda
			if (porAgregar  >  transporte) {
				$(importe).val(0);
				Swal.fire("Verifique","El detalle del transporte local es mayor que total Transporte","warning");
				sumaTransporte();
			}
	
			let saldo = Number(quitaFmt($("#mSaldo").val()));
			if (porAgregar  >  saldo) {
				$(importe).val(0);
				Swal.fire("Verifique","El detalle del transporte local es mayor que el Saldo de la Agenda","warning");
				sumaTransporte();
			}
			
	}
}

		
		
function creaTableBoletos(){
		sWhere ="RFC ='" + $("#cIdRFC").val() + "'";
		tablaBoletos = $('#tablaBoletos').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_BoletosViaticos&qw=" + sWhere ,
				aoColumns : [ {
					sName : "id"
				},{
					sName : "creferencia"
				}, {
					sName : "mTotal"
				}, {
					sName : "cPartida"
				}, {
					sName : "cRuta"
				}, {
					sName : "ffechaSalida"
				}, {
					sName : "ffechaRegreso"
				}, {
					sName : "RFC"
				}, {
					sName : "cNombre"
				}],
				oLanguage : es_mx
			});			
			
	}
	
		
		
function creaTableTaxis(){
		sWhere ="nidComision =" + $("#idComision").val();
		$('#tablaTaxis').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_TaxisViaticos&qw=" + sWhere ,
				aoColumns : [{
					sName : "cFolioTaxi"
				}, {
					sName : "cDestino"
				}, {
					sName : "fAplicacion"
				}, {
					sName : "mMonto"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	
	
function creaTicketAsignados(){
		sWhere ="nidComision =" + $("#idComision").val();
		oTableBoleto = $('#tablaAsignados').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_BoletosAsignados&qw=" + sWhere ,
				aoColumns : [ {
					sName : "creferencia"
				}, {
					sName : "mImporteBoleto"
				}, {
					sName : "cPartida"
				}, {
					sName : "cRuta"
				}, {
					sName : "RFCVuelo"
				}, {
					sName : "cNombre"
				},{
					sName : "eliminar"
				}],
				oLanguage : es_mx
				//"sScrollY" : "100%",
			});			
			
	}

function creaTablaSolicitud(){
		sWhere ="nidComisionModulo =" + $("#idComision").val();
		oTableSolicitud = $('#tablaSolicitud').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bLengthChange" : false,
				//"bAutoWidth" : false,
				"bSort" : true,
				"bInfo" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_RelacionComision&qw=" + sWhere ,
				aoColumns : [ {
					sName : "id", "bVisible": showId
				}, {
					sName : "nFolioTramite"
				}, {
					sName : "cTipoTramite"
				}, {
					sName : "dEvento"
				}, {
					sName : "viaticos"
				}, {
					sName : "transporte"
				},{
					sName : "total"
				},{
					sName : "Anticipo"
				},{
					sName : "Devolucion"
				},{
					sName : "estatus"
				},{
					sName : "documento"
				},{
					sName : "cObservaciones"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	
function agregaBoleto(referencia){
	var trClone = $('#tablaBoletos tr:eq(' +  referencia + ')').clone();
	$("#boletoAsignado").val(trClone.find('td:eq(1)').html());
	$("#importeBoleto").val(trClone.find('td:eq(2)').html());
	$("#partidaBoleto").val(trClone.find('td:eq(3)').html());        
    $("#rutaBoleto").val(trClone.find('td:eq(4)').html());    

	var guardado = false;
	logErrores = "";	
	
		$.ajax({
			url : "../viaticos/agregaBoletoAvion",
			type : 'post',
			async : false,
			data : $("#formViaticos").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					creaTableBoletos();
					creaTicketAsignados();
					$('.dataTables_scrollHead').hide();
				} else {
					var errores = j.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						logErrores = logErrores + errores[cnt] + "\n";
					}
				}
			},
			error : function(errorThrown) {
				logErrores = errorThrown.ERROR;
			}
		});
	
		if (!guardado)
			Swal.fire("Error al agregar el boleto de avión", logErrores, "error")
		
		return guardado;
		
}

	
function eliminarBoleto(referencia){

		Swal.fire({
				  title: 'Desea eliminar el boleto de avión?',
				  text: "Se quitará la relación entre la comisión y el boleto de avión.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					   	var trClone = $('#tablaAsignados tr:eq(' +  referencia + ')').clone();
						$("#boletoAsignado").val(trClone.find('td:eq(0)').html());
						$("#importeBoleto").val(trClone.find('td:eq(1)').html());
						$("#partidaBoleto").val(trClone.find('td:eq(2)').html());        
					        
						var guardado = false;
						logErrores = "";
						
						$.ajax({
								url : "../viaticos/eliminarBoletoAvion",
								type : 'post',
								async : false,
								data : $("#formViaticos").serialize(),
								dataType : 'json',
								success : function(j) {
									var exito = j.success;
						
									if (exito) {
										guardado = true;
										creaTableBoletos();
										creaTicketAsignados();
										$('.dataTables_scrollHead').hide();
									} else {
										var errores = j.errorList;
										var cnt = 0;
										for (cnt = 0; cnt < errores.length; cnt++) {
											logErrores = logErrores + errores[cnt] + "\n";
										}
									}
								},
								error : function(errorThrown) {
									logErrores = errorThrown.ERROR;
								}
							});
						
							if (!guardado)
								Swal.fire("Error al eliminar el boleto de avión", logErrores, "error")
								
							return guardado;	
				  }
				})
}


function togleDivFacts(nIdDiv){
			
			creaDTFacturas();
			$('.dataTables_scrollHead').hide();
				
			if( nIdDiv == 0){
				$("#uploadFacturasDiv").hide();
				$("#facturasCapturadasDiv").show();
				queryFormPost("readMontoFacturasNetoRG", {async:false});
				
			}else{
				if ($("#esExtranjero").val()=="1")
					$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
				else
					$('#uploadFacturasFrm').attr('src', "UploadFacturasRGV.jsp?tipo_pago=RELACIONGASTOS&DESTINO_GASTO="+$("#cEvento").val()+"&RFC="+ encodeURIComponent($("#cRFC").val())+"&FOLIO="+$("#nFolioPago").val() );
				$("#uploadFacturasDiv").show();
				$("#facturasCapturadasDiv").hide();
			}		
}

function avanzarJust() {
	$('[href="#resumenRG"]').tab().show();
	$('#rg-list a[href="#resumenRG"]').trigger('click');
}

function cerrarJustificacion(){
	modalJustif.hide();
}

function cerrarBoletos(){
	modalBoletos.hide();
}

function cerrarGasolina(){
	modalGasolina.hide();
}

function leerNombreComision() {
		queryFormPost("leeNombreComision", {async:false});	
	}
	
function creaDTFacturas(){
			oTablevFact = $('#grdValidaFacturas').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : true,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"sScrollY": "100%",
				"sScrollXInner": "100%",
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] 
					+ "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='RELACIONGASTOS'" ,
				aoColumns: [
							{ sName: "Serie" },
							{ sName: "Factura" },
							{ sName: "mimporteconiva" } ],
				oLanguage : es_mx	
			});
}
	
function aceptarFacturas() {
	var aData = oTablevFact.fnGetData();
	validaEdicion();
	$("#importeAuxNeto").val($("#importeNeto").val());
	revisaTipoFacturas();
	queryFormPost("RetencionesRead", {async:false});
	
	if (revisaFacturaConRetenciones()){	
		if (Number($("#mFacturaOtros").val()) > 0 ) {
			$("#divOtrasRete").show();
		} else {
			$("#divOtrasRete").hide();
		}
		$("#avanzaFact").show();
	} else {
		$("#avanzaFact").hide();
	}
		
	modalFactura.hide();
}		


function revisaFacturaConRetenciones(){
	queryFormPost("readMesFacturas", {async : false});
	
		if( $("#cantMeses").val() > 1 ){
			Swal.fire ('El pago tiene facturas generadas en diferentes meses con retenciones.', 'Favor de realizar pagos/comprobaciones por separado, una por cada mes de las facturas.', 'error');
			return false;
		}  else
			return true;
}

function creaDiagloFacturas(){

			if   (  "" != $.trim( $( "#cEvento" ).val() )  )  {
				queryFormPost("readMontoFacturasNetoRG", {async:false});
				creaDTFacturas();
				togleDivFacts(0);
				modalFactura.show();
				$('.dataTables_scrollHead').hide();
			} else if( "" == $.trim( $("#cEvento").val() ) )
				Swal.fire("Capturar", "Debe seleccionar el evento para continuar" ,"info");

			
}

function validar2(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		patron = /[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}

function validaimportemaximo(){
		
			var importe = 0.00;
			importe = parseFloat(quitaFmt($("#importeEdicion").val()));
			importe = importe.toFixed(2);

			importeEdicionMaximo = parseFloat(quitaFmt($("#importeEdicionMaximo").val()));			
			importeEdicionMaximo = importeEdicionMaximo.toFixed(2);
			
			$("#importeEdicion").val(importe);
			
			$("#importeEdicionMaximo").val(importeEdicionMaximo);
			
			if (Number(quitaFmt($("#importeEdicion").val())) > Number(quitaFmt($("#importeEdicionMaximo").val()))){
				Swal.fire("Verifique","El importe editado no puede ser mayor a $"+$("#importeEdicionMaximo").val(),"info");			
				$("#importeEdicion").val("0.00");
			}
			//else{
				//$("#mImporteNeto").val(parseFloat(Number(importe)+Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2));	
			//}
			
			queryFormPost("leeImporteFacturas", {async:false});
			queryFormPost("importeRetencionRG", {async:false});

			if (Number($("#importeEdicion").val())==0){
				$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val()))+Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));
			} else {
				$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicion").val()))+Number(quitaFmt($("#mImporteRetencion").val()))).toFixed(2));	
			}
}

function validaEdicion(){	
			queryFormPost("leeImporteFacturas", {async:false});
			queryFormPost("leeImportenoComprobable", {async:false});
			queryFormPost("importeRetencionRG", {async:false});

			let rete = $("#mImporteRetencion").val();
			if (rete == "")
				rete = "0.00";

			var importeNeto = parseFloat(Number(quitaFmt($("#importeEdicionMaximo").val()))+Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2);
			var importeBruto = parseFloat(Number(importeNeto) + Number(rete)).toFixed(2);
			$("#mImporteBruto").val(importeBruto);
			$("#mImporteRetencion").val(rete);
			$("#importeNeto").val(importeNeto);
}
		
function revisaFacturas(){
	let impFacturas = 0;
	let importe = 0;
	
	if (document.getElementById("chk_EditaImporte").checked == true) {
		impFacturas =(Number(quitaFmt($("#importeEdicion").val())) + noComprobable).toFixed(2); 
		 
	} else {
		impFacturas = 	(Number(quitaFmt($("#importeEdicionMaximo").val())) + noComprobable).toFixed(2);
	}
	
	importe = (Number(quitaFmt($("#mTotal1").val())) +Number(quitaFmt($("#mTotalLocal").val()))).toFixed(2); 
	
	if(importe == impFacturas) {
		return true;	
	} else {
		return false;
	}
}

function revisaRetenciones() {
	if($("#mImporteRetencion").val() == "" ){
		$("#mImporteRetencion").val(0)
	}
	
	if ($("#mImporteRetencion").val() > 0 ) {
		queryFormPost("retencionesGuardadasRead", {async: false});
		if( Number($("#sumaRetenciones").val()).toFixed(2) == Number($("#mImporteRetencion").val()).toFixed(2)){
			return true
		} else {
			return false
		}
	} else {
		return true;
	}
}


function avanzarDetalle() {
	var correcto = false;
	queryFormPost({
		queryName : "montoPendienteRGRead",
		async : false,
		callback : function() {
			var diferencia = parseFloat(quitaFmt($("#diferencia").val()));
			if (diferencia == 0.00) {
				correcto = true;
			} else {
				Swal.fire("Capture","Falta por cubrir: " + diferencia + " para completar el pago.","info");
			}
		}
	});
	return correcto;
}

function borrarDatos() {
	var guardado = false;
		logErrores = "";
		$("#folioPago").val($("#nFolioPago").val());
		
		Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se eliminara la información previamente capturada para generar la solicitud",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $.ajax({
								url : "../viaticos/borrarTodo",
								type : 'post',
								async : false,
								data : {id :  		$("#idComision").val(),
										folioPago :	$("#folioPago").val()
										},
								dataType : 'json',
								success : function(j) {
									var exito = j.success;
						
									if (exito) {
										guardado = true;
										$('[href="#detalleRG"]').tab().hide();
										$('[href="#resumenRG"]').tab().hide();
										location.reload(true);
									} else {
										var errores = j.errorList;
										var cnt = 0;
										for (cnt = 0; cnt < errores.length; cnt++) {
											logErrores = logErrores + errores[cnt] + "\n";
										}
									}
								},
								error : function(errorThrown) {
									logErrores =  errorThrown.ERROR;
								}
							});
						
							if (!guardado)
								Swal.fire("Error borrando el pago de la agenda", logErrores, "error")
								
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  $("#chk_Boleto").prop("checked",false);
				  }
				})
		
		
} 

function validaFacturasyFechas() {
	
	queryFormPost("consultaNecesitaJustificacion", {async: false});
	queryFormPost("tieneJustificacionCapturada", {async: false});
	$("#folioPago").val($("#nFolioPago").val());
	
	if ($("#necesitaJustificar").val() != 0 && $("#tieneJustCapturada").val() == 0) {	
		$('#uploadJustificacionFrm').attr('src', "UploadJustificacion.jsp?folioPago=" + $("#folioPago").val());
		modalJustif.show();
		
			
	} else {
		$('[href="#resumenRG"]').tab().show();
		$('#rg-list a[href="#resumenRG"]').trigger('click');
	}
	
	return true;
}


function validacionesRetencion(){
		let diferencia;
		if ($("#chk_tieneRete").prop("checked")) {
			
			if (Number($("#cOtraRetencion").val())== 0 && Number($("#cTipoRetencion").val())  == 0) {
				Swal.fire("Seleccione", "Debe seleccionar el tipo de Otras Retenciones para continuar", "info");
				return false;
			
			} else {
				//Otras retenciones
				if (Number($("#mFacturaOtros").val()) > 0 ) {
							
							diferencia = Number($("#mOtras").val()) - Number($("#mFacturaOtros").val())
							
							if (diferencia > 0.01 || diferencia < -0.01 ){
								Swal.fire("Verifique la Factura", "El calculo de las Otras retenciones no corresponde con la factura", "error");
								return false;	
							} else {
								queryFormPost("insertaOtrasRetenciones", {async: false});
								
							} 
				}
					
				//Retenciones IVA e ISR
				 if ( Number($("#mIvaFactura").val()) > 0  ||  Number($("#mISRFactura").val()) > 0 ) {
						
						diferencia = Number($("#mImporteIva").val())  + Number($("#mImporteISR").val()) - Number($("#mIvaFactura").val()) -Number($("#mISRFactura").val());
						
						if (diferencia > 0.01 || diferencia < -0.01 ){
							//Eliminar otras retenciones
							queryFormPost("eliminaRetenciones", {async: false});
							Swal.fire("Verifique la Factura", "El calculo de las retenciones no corresponde con la factura", "error");
							return false;	
						} else {
							if(!agregaRetenciones()){
								return false;
							}
						}
				
				} else {
					if($("#tieneRetenXML").val() == "1" && $("#retencionesCapturadas").val() > 0 ){
						return true;
						
					} else if($("#tieneRetenXML").val() == "1"){
						
						Swal.fire("No se puede continuar"," Debido a que se encontraron retenciones diferentes al ISR en el XML de la Factura.","error");
							if($("#esResico").val() == "0")
								Swal.fire("No se puede continuar","Debido a que la factura tiene retenciones y el proveedor no es RESICO.","error");
						return false;
					} 
				}
			}		
		} else {	
					if($("#tieneRetenXML").val() == "1" && $("#retencionesCapturadas").val() > 0 ){
						return true;
						
					} else  if ($("#tieneRetenXML").val() == "1"){
						Swal.fire("No se puede continuar"," Debido a que se encontraron retenciones diferentes al ISR en el XML de la Factura.","error");
							if($("#esResico").val() == "0")
								Swal.fire("No se puede continuar","Debido a que la factura tiene retenciones y el proveedor no es RESICO.","error");
						return false;
							
					} else {
						$("#cTipoPago").val($("#cDocumento").val());
						
						$("#msgRF").val();
						queryFormPost("validaResicoRG",{async: false });
						if ($("#msgRF").val()!=""){
							Swal.fire("Factura(s) erronea(s)","La(s) factura(s) " + $("#msgRF").val() + " tienen otro regimen fiscal diferente a RESICO.","error");
							return false;
						}
						
						queryFormPost("validaRESICOPersonaMoral",{async: false });
						if ($("#msgRF").val()!=""){
							Swal.fire("No se puede continuar",$("#msgRF").val(),"error");
							return false;
						}
					} 
		}
		return true;
}

function agregaRetenciones() {
	var guardado = false;
		logErrores = "";
			
				$.ajax({
					url : "../viaticos/agregaRetenciones",
					type : 'post',
					async : false,
					data : {folioPago : 	$("#nFolioPago").val(),
							mIvaFactura: 	$("#mIvaFactura").val(),
							mISRFactura: 	$("#mISRFactura").val(),
							aejercicioFiscal: $("#aejercicioFiscal").val(),
							cTipoPago: 		$("#cTipoPago").val(),
							tipoRetIVA: 	$("#tipoRetIVA").val(),
							tipoRetISR: 	$("#tipoRetISR").val(),
							concepto:		$("#cTipoRetencion").val()
					},
					dataType : 'json',
					success : function(j) {
						var exito = j.success;
			
						if (exito) {
							guardado = true;
						} else {
							var errores = j.errorList;
							var cnt = 0;
							for (cnt = 0; cnt < errores.length; cnt++) {
								logErrores = logErrores + errores[cnt] + "\n";
							}
						}
					},
					error : function(errorThrown) {
						logErrores = errorThrown.ERROR;
					}
				});
			
				if (!guardado)
					Swal.fire("Error al agregar las retenciones", logErrores, "error")
					
				return guardado;
}

function reiniciaValores(clase, mostrar) {
	$("." + clase).each(function() {
		let conFormato = currencyFormatter({currency:'USD', value: quitaFmt($(this).val()) });
		$(this).val(conFormato);
		
		if (mostrar) {
			$(this).val("$0.00")
		}
	});
	
}

function actualizarTipoCambio() {
	
	if ($("#importeTipoCambio").val() =="" || $("#importeTipoCambio").val() =="0") {
		Swal.fire("Importante","El importe del tipo de cambio no puede estar vacio ni debe ser cero","warning");
		return false;
	} else {
		queryFormPost("actualizarTipoCambio", {async: false});
		queryFormPost("actualizarTotalComision", {async: false});
		queryFormPost("leeValoresInicialesViaticos", {async: false});
		queryFormPost("leeTotalesAgenda", {async: false});
		queryFormPost("leeTotalesTransporte", {async: false});
		formatoDatos();
		formatoTotales();
		
		modalTipoCambio.hide();
	}	
}

function abrirDlgTipoCambio() {
	modalTipoCambio.show();	
}

function abrirDlgModifica() {
	querySelectPost("consultaNombreComision","idNombre",{async: false});
	
	modalModificaAgenda.show();
}

function actualizaNombre () {
	$("#nComision").val($("#nombreComision").val())
	var guardado = false;
	logErrores = "";
	
	$.ajax({
		url : "../viaticos/actualizaNombreComision",
		type : 'post',
		async : false,
		data : {
			nombreComision : $("#nComision").val(),
			idComision : $("#nidComision").val(),
			idNombre : $("#idNombre").val(),
		},
		dataType : 'json',
		success : function(j) {
			var exito = j.success;

			if (exito) {
				guardado = true;
				Swal.fire("OK","¡Nombre de la agenda actualizada con exito!", "success");
			} else {
				var errores = j.errorList;
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
			}
		},
		error : function(errorThrown) {
			logErrores =  errorThrown.ERROR;
		}
	});

	if (!guardado)
		Swal.fire("Error actualizando la Agenda", logErrores, "error")
			
	modalModificaAgenda.hide();
}

function revisaBoletosAvion() {
	queryFormPost("boletosAgregados", {async: false});
	
	if(validaImporteAnticipo()) {
				
			if($("#tieneBoletos").val() == 0 ) {
					Swal.fire({
							  title: 'Desea continuar?',
							  text: "No se han agregado boletos de avión.",
							  icon: 'warning',
							  showCancelButton: true,
							  confirmButtonColor: '#288BA8',
							  cancelButtonColor: '#e6e6e6',
							  confirmButtonText: 'Aceptar',
							  cancelButtonText: 'Cancelar'
							}).then((result) => {
							  if (result.isConfirmed) {
								  if (validaEventoAnticipo()) {
									if(avanzarSolicitud()) {
										if(capturarJustificacionGasolina()){
											avanzarTabFacturas();	
										}
									}
								}
									
							  } else {
									return false;
							}
							})
							
			} else {
					if (validaEventoAnticipo()) {
						if(avanzarSolicitud()) {
							if(capturarJustificacionGasolina()) {
								$('#uploadPasesFrm').attr('src', "UploadPasesAbordar.jsp?folioPago=" + $("#nFolioPago").val());
								modalBoletos.show();
							}
						}
					}
			}
	
	}
}

function capturarJustificacionGasolina() {
	
	if ( Number(quitaFmt($("#mGasolinaLocal").val())) > 0 ) {
		$('#uploadGasolinaFrm').attr('src', "UploadGasolina.jsp?folioPago=" + $("#nFolioPago").val());
		modalGasolina.show();
	}	else {
		return true;
	}
	
}

function avanzarBoletos() {
		if (verificaJustGasolina()) {
			avanzarTabFacturas();	
		} else {
			return false
		}
					
		
}

function verificaJustGasolina() {
	if ( Number(quitaFmt($("#mGasolinaLocal").val())) > 0 ) {
			queryFormPost("consultaJustificacionGasolina", {async: false});
			
			if($("#cJustGasolina").val()=="" || $("#cJustGasolina").val() === null) {
				Swal.fire("Capture Justificación", "Debe capturarse la justificación del gasto de Gasolina local para continuar.","warning");
				$("#linkGasolina").show();
				return false;
			} 
			
			return true;
		} else {
			return true;
		}
}

function avanzarTabFacturas() {
		$("#avanzaSolBtn").hide();
		ClassReadOnly("inputDetalle", true);
	 	$("#btnBorrar3").hide();
		$('[href="#detalleRG"]').tab().show();
		$('#rg-list a[href="#detalleRG"]').trigger('click');
		queryFormPost("consultaConceptoViaticos", {async: false});
}

function abrirBoletosCuandoTieneGasolina() {
	if($("#tieneBoletos").val() == 1 ) {
			$('#uploadPasesFrm').attr('src', "UploadPasesAbordar.jsp?folioPago=" + $("#nFolioPago").val());
			modalBoletos.show();
	} else {
		avanzarTabFacturas();
	}								
}

function validaEventoAnticipo () {
	var validado = false;
	let total =Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val()));
	total = Number(total.toFixed(2));
	 
	//Valida que la cantidad no sea negativa
	if (Number(quitaFmt($("#mTotal1").val())) < 0 ){
			Swal.fire("Verifique", "El importe del Viatico Anticipado no puede ser menor a cero", "error");
			return validado;
	} 
		
	//Valida que si es Anticipado no sobrepase la agenda
	if ($("#nidTipo").val() == 1  && $("#tipoEvento").val() =='A') {
	
		if (Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val())) > Number(quitaFmt($("#mSaldo").val()))){
			Swal.fire("Verifique", "El importe del Viatico Anticipado no puede ser mayor al saldo de la Agenda", "error");
			return validado;
		} 
	
	//Valida que tenga Anticipado 
	} else if ((Number(quitaFmt($("#sumTotal").val())) == 0 && $("#nidTipo").val() == 1) 
			||  (Number(quitaFmt($("#sumTotal").val())) == 0 && $("#nidTipo").val() == 2 &&  $("#cEvento").val() == "GCRE")) 
			{
			Swal.fire("Verifique", "No se puede realizar una devolución un viatico que no tiene una solicitud de Viatico Anticipado, seleccione el evento correcto.", "warning");
			reiniciaValores("inputDetalle", true);
			return validado;
	
	} else if ((total > Number(quitaFmt($("#sumPorComprobar").val())) && $("#nidTipo").val() == 1)
			 || total > Number(quitaFmt($("#sumPorComprobar").val())) && $("#nidTipo").val() == 2 &&  $("#cEvento").val() == "GCRE") 
			{
			Swal.fire("Verifique", "El importe de la solicitud no puede ser mayor al saldo por Comprobar", "error");
			return validado;
	
	} else if (Number(quitaFmt($("#mTotalLocal").val())) > Number(quitaFmt($("#mTransporte").val())))
			{
			Swal.fire("Verifique", "El importe del transporte Local capturado no puede ser mayor al total de Transporte del Viatico", "error");
			return validado;
	} else {
		validado = true;
	}
	
	return validado;
}

function validaImporteAnticipo() {
	
	let importeCapturado = Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val())) ;
	importeCapturado = Number(importeCapturado.toFixed(2));
	if (( importeCapturado > Number(quitaFmt($("#importeAnticipo").val())) &&  $("#tipoEvento").val() == "D")) 
			{
			Swal.fire("Verifique", "El saldo del Anticipo seleccionado es menor que el importe del Detalle de la comision", "warning");
			reiniciaValores("inputDetalle", true);
			$("#folioCaja").focus();
			return false;
		}
		
	if (($("#nidTipo").val() == 1 || $("#nidTipo").val() == 2 ) && importeCapturado == 0 ) {
		Swal.fire("Capture", "El Detalle de la comision no puede ser cero", "warning");
		return false;
	}
	
	return true;
}

function finalizaTramite() {
	let guardado;
	logErrores = "";
	if( tieneTramitesPendiente()){	
			$.ajax({
					url : "../viaticos/finalizaTramite",
					type : 'post',
					async : false,
					data : $("#formViaticos").serialize(),
					dataType : 'json',
					success : function(j) {
						var exito = j.success;
			
						if (exito) {
							guardado = true;
							queryFormPost("finalizaComision", {async: false});
							$('#comprobacion-list a[href="#editar"]').hide();
							$("#btnFinalizar").hide(); 
							$("#btnDesfinalizar").show();
							$("#nuevoBtn").hide();
							$("#nuevoRGBtn").hide();
							$("#nuevoCVBtn").hide();
							Swal.fire("OK", "Se finalizó la comision con exito", "success");
						} else {
							var errores = j.errorList;
							var cnt = 0;
							for (cnt = 0; cnt < errores.length; cnt++) {
								logErrores = logErrores + errores[cnt] + "\n";
							}
						}
					},
					error : function(errorThrown) {
						logErrores =  errorThrown.ERROR;
					}
				});
			
				if (!guardado)
					Swal.fire("Error finalizando trámite", logErrores, "error")	
		
	}
	return true;	
}

function revisaSaldo() {
		
	if ($("#sumPorComprobar").val() != "$0.00" ) {
		Swal.fire("No se puede finalizar","La comisión aun tiene saldo por Comprobar","info")
		return false;
	} else
		return true;
}


function generaSolicitud() {
	//Valida que seleccione el evento
	if($("#nidTipo").val() != 3  && ($("#cEvento").val() =="" || $("#cEvento").val() === null)) {
		Swal.fire("Seleccione","Debe seleccionar el evento antes de continuar", "warning");
		return false;
	} else if($("#nidTipo").val() != 3 ) {
		//Devuelve el tipo del evento
		consultaTipoEvento();
		if ($("#cEvento").val() =="8_2_2" && $("#tieneVariosAnt").val() > 1 && ($("#folioCaja").val() === null || $("#folioCaja").val() === undefined )) {
			Swal.fire("Seleccione","Debe seleccionar un Folio de Anticipo antes de continuar.", "warning");
			return false;
		}
	}
	
	
	if($("#nidTipo").val() == 1) {
		$("#mTotal1").val(Number(quitaFmt($("#mTotal2").val())));
	}
	//Suma Viaticos mas Transporte
	let total =Number(quitaFmt($("#mTotal1").val())) + Number(quitaFmt($("#mTotalLocal").val()));
	 
	validaEventoAnticipo();
	
	if ((total == 0) && $("#cDocumento").val() != 'COMSINVIATICOS' ){
		Swal.fire("Los importes no pueden ser cero", "Debe especificar los detalles de la comisión o el transporte","warning");
		return false;
		
	} else {
		
		if($("#firmaElectronica").prop("checked") == true){
			$("#cEsFirmaElectronica").val("S");
		} else
			$("#cEsFirmaElectronica").val("N");
			
		if ( validaFirmantes() ) {
			//Validación para que asegurar que no va a seleccionar boletos antes de generar
			if($("#cDocumento").val() ==  'COMSINVIATICOS' ){
				queryFormPost("tieneBoletoAsignado", {async: false});
				 if ($("#cuantosBoletos").val() == 0 ){
					if(confirm("Se generará una comisión sin viaticos sin ningun boleto de avión.\n ¿Desea continuar?")) {
						if(generaTramite())
							location.reload(true);
					} else{
						return false;
					}
					
				} else {
					if(generaTramite())
						location.reload(true);
				}
			} else {
				if(generaTramite())
					location.reload(true);
			}
			
		} else
			return false;
	} 
}

/*
 * BUGFIX No envia la informacion de delegatorios, por lo pronto se agrega al formulario y al registrar la respuesta se eliminan.
 */
function generaTramite(){
		var guardado = false;
		var logErrores = "";
		quitarFormato();
		
		if($("#mImporteRetencion").val()==""){
			$("#mImporteRetencion").val("0.00");
		}
		
		if($("#cEvento").val() == 0){
			Swal.fire("Evento", "Favor de Seleccionar el evento", "info");
			return false;
		}
		
		remueveSuplencias();
		agregaDelegatorioVoBo();
		agregaDelegatorioAut();		
		
		$.blockUI({message: "Procesando espere ......"});
		$.ajax({
			url : "../viaticos/generaSolicitud",
			type : 'post',
			async : false,
			data : $("#formViaticos").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
				$.unblockUI();
				if (exito) {
					guardado = true;
					folioGenerado = j.messageList[0];
					$("#nFolioPago").val(folioGenerado);
					if ($("#nidTipo").val() == 1) {
						alert("Se genero la solicitud de Caja con el No. " + folioGenerado);
					} else if ($("#nidTipo").val() == 2) {
						alert("Se genero la solicitud de Relacion de Gastos No. " + folioGenerado);
					}  else if ($("#nidTipo").val() == 3) {
						alert("Se genero la solicitud de Comisión Sin Viaticos No. " + folioGenerado);
					}	
					
					if ( $("#cEsFirmaElectronica").val() == "N") {
						if ($("#nidTipo").val() == 1 ) {
							var where = " and ce.nfoliocaja=" + folioGenerado;
							window.open("../servlet/SeguridadCatalogosMateriales?"
								+ "catalogo=REPORTE"
								+ "&accion=run"
								+ "&rn=PolizaCaja.jasper"
								+ "&formato=PDF"
								+ "&whereFolio=" + where, "Procesando", "status=1, width=400px, height=200px, left=150px");
						
						} else if($("#nidTipo").val() == 2 ) {
							queryFormPost("obtenerContrarrecibo", {async: false });
							let whereReporte = " and caNoContrarrecibo ='" + $("#contrarecibo").val() + "'";	
							$("#cDocumento").val("RELACIONGASTOS");
							$("#totFacturas").val(0);
							queryFormPost("cantFacturas", {async: false });
							$("#boletosExistentes").val(0);
							queryFormPost("existenBoletosRG", {async: false });	
							
							//Imprimir Justificaciones
							queryFormPost("consultaNecesitaJustificacion", {async: false});
							if ($("#necesitaJustificar").val() != 0 || $("#cOtraJustificacion").val() != "") {   //si se tiene alguna justificacion se imprime	
								  	$("#sWhere").val($("#nFolioPago").val());
									$("#formJustificacion").submit();	
							}
							
							//Imprimir anexo
							if (($("#cllave").val()>6 || ($("#totFacturas").val()>6)) //se cambia a EP's mayor a 7 o mas de 7 facturas
								  	|| ($("#boletosExistentes").val()>3)) {   //Se agrego para cuando se tiene mas de un boleto d avion	
								  	$("#swhere").val(whereReporte);
									$("#formAnexo").submit();	
							}
							
							//Imprimir Solicitud
							window.open("../admin/SeguridadCatalogos?" 
						    + "catalogo=CONTRARECIBO"
							+ "&accion=run" 
							+ "&rn=PolizaPago.jasper" 
							+ "&folio= "   +  $("#contrarecibo").val() + "", 
							"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
							
						} else if($("#nidTipo").val() == 3 ) {
							window.open(
								"../admin/SeguridadCatalogos?"
								+ "catalogo=CONTRARECIBO"
								+ "&accion=run"
								+ "&rn=PolizaInformeComision.jasper"
								+ "&whereFolio= " + whereReporte + "", 			
								"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
						}
					}
				} else {
					
					var errores = j.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						logErrores = logErrores + errores[cnt] + "\n";
					}
				}
				remueveSuplencias();
			},
			error : function(errorThrown) {
				$.unblockUI();
				remueveSuplencias();
				logErrores = errorThrown.ERROR;
			}
		});
	
		if (!guardado)
			Swal.fire("Error en el resumen de Viaticos", logErrores, "error")
			
		return guardado;
}



const agregaDelegatorioVoBo = function(){
	
	if( $("#VoBoSuplencia").prop("checked") ){
		
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='voboSuplencia' id='voboSuplencia' value='true' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='voboNumeroOficio' id='voboNumeroOficio' value='" + $("#noOficioVoBo").val() + "' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='voboFechaOficio' id='voboFechaOficio' value='" + $("#fechaOficioVobo").val() + "' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='voboSuplenciaMotivo' id='voboSuplenciaMotivo' value='" + $("#VoBoSuplenciaMotivo").val() + "' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='voboEmpleadoSuplente' id='voboEmpleadoSuplente' value='" + $("#nombreVOBOSuplente").val() + "' />");
	}
	
}

const agregaDelegatorioAut = function(){
	
	if( $("#AutSuplencia").prop("checked") ){
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='autSuplencia' id='autSuplencia' value='true' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='autNumroOficio' id='autNumroOficio' value='" + $("#noOficioAut").val() + "' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='autFechaOficio' id='autFechaOficio' value='" + $("#fechaOficioAut").val() + "' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='autSuplenciaMotivo' id='autSuplenciaMotivo' value='" + $("#AutSuplenciaMotivo").val() + "' />");
		$("#formViaticos").append("<input type='hidden' class='suplenciaInput' name='autEmpleadoSuplente' id='autEmpleadoSuplente' value='" + $("#nombreAUTSuplente").val() + "' />");
	}
	
}

const remueveSuplencias = function(){
	$(".suplenciaInput").each(function(){
		$(this).remove();
	});
}
		
function quitarFormato() {
			$("#mTotal1").val(quitaFmt($("#mTotal1").val()));
			$("#mTotalLocal").val(quitaFmt($("#mTotalLocal").val()));
			$("#mPasaje").val(quitaFmt($("#mPasaje").val()));
			$("#mTaxi").val(quitaFmt($("#mTaxi").val()));
			$("#mPeaje").val(quitaFmt($("#mPeaje").val()));
			$("#mHotel").val(quitaFmt($("#mHotel").val()));
			$("#mConsumos").val(quitaFmt($("#mConsumos").val()));
			$("#mOtros").val(quitaFmt($("#mOtros").val()));
			$("#mPasajeLocal").val(quitaFmt($("#mPasajeLocal").val()));
			$("#mTaxiLocal").val(quitaFmt($("#mTaxiLocal").val()));
			$("#mGasolinaLocal").val(quitaFmt($("#mGasolinaLocal").val()));
			$("#mMaritimoLocal").val(quitaFmt($("#mMaritimoLocal").val()));
			$("#mAereoLocal").val(quitaFmt($("#mAereoLocal").val()));
			$("#mPeajeLocal").val(quitaFmt($("#mPeajeLocal").val()));
			$("#mImporteBruto").val(quitaFmt($("#mImporteBruto").val()));
			$("#mImporteSinIVA").val(quitaFmt($("#mImporteSinIVA").val()));
			$("#importeEdicionMaximo").val(quitaFmt($("#importeEdicionMaximo").val()));
			$("#importenoComprobable").val(quitaFmt($("#importenoComprobable").val()));
			$("#importeEdicion").val(quitaFmt($("#importeEdicion").val()));
}

function activarTramite() {
			queryFormPost("activaTramite", {async: false}) ;
			$("#nuevoBtn").show();
			$("#nuevoRGBtn").show();
			$("#nuevoCVBtn").show();
			$("#btnDesfinalizar").hide();
			$('#comprobacion-list a[href="#editar"]').show();
			if($("#urUsuario").val() == "A02"){
					$("#btnFinalizar").show();
			} 
			
			desactivarBotones();
}


function agregarObservacion(idPago) {
		arrPago = idPago.split('-');
		modalObservaciones.show();	
}

function guardaNotas() {
	let folio = arrPago[0];
	let tipoPago = arrPago[1];
	
	$("#cNotas").val($("#notaComision").val());
	$("#folio").val(folio);
	$("#tipoPago").val(tipoPago);
			
	queryFormPost("guardarObservaciones", {async: false}) ;
	
	modalObservaciones.hide();
	creaTablaSolicitud();
}


function adjuntarComprobanteBancario(idPago) {
		arrPago = idPago.split('-');
		$("#nFolioCaja").val(arrPago[0]);
		
		queryFormPost({ queryName: "consultaCasoCaja",
						async: true,
						callback: function(){
							getExpedient($("#idProcess").val());
							$('.uploadFile').show();
							modalAdjuntaCB.show();					 							
					} });
	
}


function adjuntarPaseAbordar(idPago) {
		arrPago = idPago.split('-');
		$("#nFolioCaja").val(arrPago[0]);
		
		queryFormPost({ queryName: "consultaCasoCSV",
						async: true,
						callback: function(){
							getExpedient($("#idProcess").val());
							$('.uploadFile').show();
							modalAdjuntaCB.show();					 							
					} });
	
}

let total = 0;
function detalleSolicitud (idPago) {
	arrPago = idPago.split('-');
	let folio = arrPago[0];
	let tipoPago = arrPago[1];
	$("#folio").val(folio);
	$("#tipoPago").val(tipoPago);

	if (tipoPago =="CAJA") {
		mostrarDetalle("inputRG", false);	
	} else {
		mostrarDetalle("inputRG", true);
	}
		
	queryFormPost("consultaComprobacionDetalle", {async: false}) ;
	queryFormPost("consultaImporteSolicitud", {async: false});
	modalDetalleAgenda.show();
	
}

function sumaDetalleDM(importe) {
	let total = Number(quitaFmt($("#dmPasaje").val())) + Number(quitaFmt( $("#dmTaxi").val())) + Number(quitaFmt($("#dmPeaje").val())) 
		+ Number(quitaFmt($("#dmHotel").val())) + Number(quitaFmt($("#dmConsumos").val())) + Number(quitaFmt($("#dmOtros").val()));
	
	if ($("#tipoPago").val() == "RELACIONGASTOS") {
		total = currencyFormatter({currency:'USD', value: total });
		$("#dmTotal").val(total);	  
	}
	
	let importeConFormato = currencyFormatter({currency:'USD', value: quitaFmt($(importe).val()) })
	$(importe).val(importeConFormato)
	
	
	$("#mTotalGral").val( total + Number($("#mTotalLocal").val()).toFixed(2));
}


function sumaTransporteDM(importe) {
	let total = Number(quitaFmt($("#dmPasajeLocal").val())) + Number(quitaFmt( $("#dmTaxiLocal").val())) + Number(quitaFmt($("#dmGasolinaLocal").val())) 
	+ Number(quitaFmt($("#dmPeajeLocal").val())) + Number(quitaFmt($("#dmMaritimoLocal").val())) + Number(quitaFmt($("#dmAereoLocal").val()));
	total = currencyFormatter({currency:'USD', value: total });
	$("#dmTotalLocal").val(total);
	
	let importeConFormato = currencyFormatter({currency:'USD', value: quitaFmt($(importe).val()) })
	$(importe).val(importeConFormato)  ;
	
}

let importeSolicitud = 0;
function guardaDetalle() {
	importeSolicitud = Number(quitaFmt($("#mMontoComision").val())).toFixed(2);
	let total = (Number(quitaFmt($("#dmTotalLocal").val())) + Number(quitaFmt($("#dmTotal").val()))).toFixed(2);
	
	if ( importeSolicitud == total ){
		queryFormPost("actualizaComprobacionDetalle", {async: false}) ;
		modalDetalleAgenda.hide();
		creaTablaSolicitud();
		
	} else {
		Swal.fire("Importe capturado diferente a la Solicitud","El importe capturado no puede ser diferente al importe de la solicitud, que es de :$" + importeSolicitud, "info");
		return false;
		
	}  
}

function tieneTramitesPendiente() {
	let retorno = true;
	queryFormPost("solicitudesNoAplicadas", {async: false});
	if ($("#solNoAp").val() > 0 ) {
		Swal.fire("Solicitudes pendientes de aplicar","Aun hay solicitudes pendientes de aplicar por lo que no se puede finalizar", "error");
		retorno= false;	
		return false;
	}
	
	queryFormPost("solicitudesNoEnviadasSicop", {async: false});
	if ($("#solNoEnviadas").val() > 0 ) {
		Swal.fire("Solicitudes pendientes de revisión","Aun hay solicitudes pendientes de envío SICOP por lo que no se puede finalizar", "error");
		retorno= false;
		return false;	
	}
	
	return retorno;

}

function guardarReemplazo(){
	
		if ($("#cTramiteSelect").val() == null) {
			Swal.fire("Seleccione","Debe seleccionar una solicitud de reemplazo", "info");
			return false 
		} 
		
		Swal.fire({
				  title: '¿Desea iniciar solicitud de reemplazo?',
				  text: "Se genera una solicitud con los mismos importes que la anterior.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
						queryFormPost("leerValoresSolicitudOrigen", {async: false});
						sumaTransporte();
						sumaDetalle();
						$("#mTotalGral").val((Number(quitaFmt($("#mTotalLocal").val())) + Number(quitaFmt($("#mTotal1").val()))).toFixed(2));
						if(avanzaTramite()){
							creaTicketAsignados();
							creaTableBoletos();
							creaTableTaxis()
							modalReemplazo.hide();
							$('#rg-list a[href="#detalleRG"]').trigger('click');
							queryFormPost("consultaConceptoViaticos", {async: false});	
						}
						
					}
				});

}

function tieneTramites(){
	let retorno = true;
	queryFormPost("agendaSinComprobacion", {async: false});
	if ($("#solNoAp").val() == 0 ) {
		Swal.fire("Agenda sin comprobacion","Aun no hay solicitudes para comprobar la agenda por lo que no se puede finalizar", "error");
		retorno= false;	
		return false;	
	}
	return retorno;	
}

function validaPartidas() {
		let regreso = false;
		$.ajax({
						url : "../viaticos/validaPartidasViatico",
						type : 'post',
						async : false,
						data :{ folio : $("#nFolioPago").val()
						},
						dataType : 'json',
						success : function(j) {
							var exito = j.success;
							if (exito) {
								regreso = true;
							} else {
								var errores = j.errorList;
								var cnt = 0;
								for (cnt = 0; cnt < errores.length; cnt++) {
									logErrores = logErrores + errores[cnt] + "\n";
								}
								logErrores = logErrores.replace("com.axtel.egresos.viaticos.ViaticosException:", "");
							 	
							 	Swal.fire("Diferencia", logErrores, "info")
								return regreso;
							}
						},
						error : function(errorThrown) {
							logErrores =  errorThrown.ERROR;
						}
				});
				
		return regreso;
}