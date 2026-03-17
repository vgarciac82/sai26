let modalRechazo;
$(document).ready(function() {
		
	queryFormPost("leeValoresInicialesViaticos", {async: false});
	queryFormPost("leeTotalesAgenda", {async: false});
	queryFormPost("leeTotalesTransporte", {async: false});
	queryFormPost("consultaTienePagosViaticos", {async: false});
			
	let totalAgenda = parseFloat( $("#totalAgenda").val() * 100 ) / 100;
	let totalTransporte = parseFloat( $("#totalTransporte").val() * 100 ) / 100;
	$("#totalTransporte").val(totalTransporte.toFixed(2));
	$("#totalAgenda").val(totalAgenda.toFixed(2));
	$("#totalGeneral").val((totalAgenda + totalTransporte).toFixed(2));
	
	let totalDias = parseFloat($("#diasNacional").val()) + parseFloat($("#diasInternacional").val())
				+ parseFloat($("#totalDias").val())
	$("#totalAcDias").val(totalDias);
	
	modalRechazo = new bootstrap.Modal(document.getElementById('dialogMotivo'), 'data-bs-backdrop');		
	$("#rechazarBtn").button().click(function() {
		$("#operacion").val("RECHAZO");
		if ($("#tienePagos").val()==0 ){
			Swal.fire("No se puede rechazar","Esta comisión ya tiene Anticipo o Pagos relacionados","error");
			return;
		} else {
			modalRechazo.show();
		}
	});

	$("#aceptarBtn").button().click(function() {
		$("#operacion").val("ACEPTAR");
		$("#cMotivoRechazo").val("");

		avanzar();

	});
	
	if( $("#cTieneBoleto").val() == 1  ) {
		$('.alert').alert();	
	} else {
		$(".alert").alert('close');
	} 
	
	queryFormPost("FirmaUsuarioExiste", {async: false});
	queryFormPost("DocHAplicadoComision", {async: false});

	if($("#cAutorizado").val()=="S"){
		$("#aceptarBtn").hide();
		$("#rechazarBtn").hide();
		Swal.fire("Firmado","Este tramite ya fue firmado por este Usuario","success");
		parent.window.frames['content-iframe'].location.href="../gstnmngr/gestion?cmd=1";
	}
	
	if($("#cDocHaplicado").val()=="C"){
		$("#aceptarBtn").hide();
		$("#rechazarBtn").hide();
		Swal.fire("Cancelado","Este tramite se encuentra Cancelado","warning");		
	}
	
	creaTableAgenda();
	creaTableTransporte();
	creaTableAutoriza();
	
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
	
function onLoadPlantilla(idOperacion) {
	
	let totalAgenda = parseFloat( $("#totalAgenda").val() * 100 ) / 100;
	let totalTransporte = parseFloat( $("#totalTransporte").val() * 100 ) / 100;
	$("#totalTransporte").val(totalTransporte.toFixed(2));
	$("#totalAgenda").val(totalAgenda.toFixed(2));
	$("#totalGeneral").val((totalAgenda + totalTransporte).toFixed(2));
}


function validaRechazo() {
	queryFormPost("consultaTienePagosViaticos", {async: false});
	
	if ($("#tienePagos").val() == 0 ) {
		
		Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se rechazara la solicitud de Viaticos.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
						if (rechazarAgenda()) {
							Swal.fire(
									  'Cancelación',
									  'Se cancelo la comisión correctamente',
									  'success'
									)
						} else { 
							Swal.fire("Error cancelando los viaticos", logErrores, "error")
				  		}
				  } 
			})
	
	} else {
		queryFormPost("consultaSaldoEnSolicitudes", {async: false});
		
		if ($("#saldoPagos").val() != 0 ) {
			Swal.fire("Pagos","Esta comisión ya tiene pagos elaborados y/o saldo por lo que no se puede cancelar", "error");
			return false;
		}
	}		
	
}

function rechazarAgenda() {
		var firmado = false;
		
		$.ajax({
			url : "../viaticos/rechazarAgenda",
			type : 'post',
			async : false,
			data : $("#formViaticos").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					firmado = true;
					alert(j.messageList[0]);
					
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
	
		if (firmado) 
			location.reload(true);
		
		return firmado;	
}

function avanzar(){
		var guardado = false;
		var logErrores = "";
	
		$.ajax({
			url : "../viaticos/avanzaFirmante",
			type : 'post',
			async : false,
			data : $("#formViaticos").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					Swal.fire("Solicitud autorizada.", "La solicitud se autorizo exitosamente", "success");
					parent.window.frames['content-iframe'].location.href="../gstnmngr/gestion?cmd=1";
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
			Swal.fire("Error en el resumen de Viaticos", logErrores, "error")
			
		return guardado;
}



function aceptarDlg() {
	
	if ($.trim($("#motivoRechazo").val()) == "") {
		Swal.fire('Capturar',"Debe ingresar el motivo de rechazo.", 'warning');
	
	} else {
		$("#cMotivoRechazo").val($("#motivoRechazo").val());
		$("#motivoRechazo").val("");
		modalRechazo.hide();
		validaRechazo();
	}
}
		
function creaTableAgenda(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableAgenda = $('#tablaAgenda').dataTable(
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
						+ "/crud?rt=t&ql=v_AgendaViaticos&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nidAgenda"
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				}, {
					sName : "destino"
				}, {
					sName : "cMotivoComision"
				}, {
					sName : "mCuotaPorDia"
				}, {
					sName : "dias"
				}, {
					sName : "Importe"
				}],
				oLanguage : es_mx
			});			
			
	}
	
function creaTableTransporte(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableTransporte = $('#tablaTransporte').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : true,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bScrollCollapse" : true,
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
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : true,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bScrollCollapse" : true,
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
				}, {
					sName : "cNota"
				}],
				oLanguage : es_mx
			});			
			
	}


function ResponsableSiguiente(idOper) {
	return "VALIDA_VIATICOS";
}

function OperacionSiguiente(idOper) {
	return "valida_viaticos";
}	