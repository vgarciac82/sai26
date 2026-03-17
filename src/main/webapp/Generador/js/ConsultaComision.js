let modalCuentaBancaria;
let showId = false;
let modalObservaciones;

$(document).ready(function() {
	
	modalCuentaBancaria = new bootstrap.Modal(document.getElementById('dialog-modificaCuenta'), 'data-bs-backdrop');
	modalObservaciones  = new bootstrap.Modal(document.getElementById('dialog-notas'), 'data-bs-backdrop');
		
	//$(".modal-dialog").draggable({  handle: ".modal-header", });	
	queryFormPost("leeValoresInicialesViaticos", {async: false});
	queryFormPost("leeTotalesAgenda", {async: false});
	queryFormPost("leeTotalesTransporte", {async: false});
	
	formatoCampos();
	
	creaTableAgendaConsulta();
	creaTableTransporteConsulta();
	creaTableAutoriza();
	creaTableAutorizaBitacora();
	creaTablaSolicitud()
	
	

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


function creaTableAgendaConsulta(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableAgenda = $('#tablaAgendaConsulta').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"sScrollY" : "100%",
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
					sName : "cAutorizado"
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
					sName : "cObservaciones"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	
	function formatoCampos() {
		let totalAgenda = parseFloat( $("#totalAgenda").val() * 100 ) / 100;
		let totalTransporte = parseFloat( $("#totalTransporte").val() * 100 ) / 100;
		$("#totalTransporte").val(totalTransporte.toFixed(2));
		$("#totalAgenda").val(totalAgenda.toFixed(2));
		$("#totalGeneral").val((totalAgenda + totalTransporte).toFixed(2));
		
		let agenda = currencyFormatter({currency:'USD', value: totalAgenda })
		$("#totalAgenda").val(agenda)
		
		let transp = currencyFormatter({currency:'USD', value: totalTransporte })
		$("#totalTransporte").val(transp);
		
		let tot = currencyFormatter({currency:'USD', value: quitaFmt($("#totalGeneral").val()) })
		$("#totalGeneral").val(tot);	
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

function onLoadPlantilla() {
	
}