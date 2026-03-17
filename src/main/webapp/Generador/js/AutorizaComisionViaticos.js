let modalDetalle;
let mensajeError ="";
$(document).ready(function() {

	var oTable = $("#dt_AutorizarLayouts").dataTable();
	
	$("#u_Login").val(cLogin);
	$("#RFCUsuario").val(RFCUsuario);
	$("#idEmpleado").val(numeroEmpleado);

	if( ( $("#cUR").val() != "A02" && $("#cUR").val() != "A03") || esSAIAlterno )
		querySelectPost("cUnidadEjecutoraVistasTesoreria", "uEjecutora", {
			async : false
		});
	else
		querySelectPost("cUnidadEjecutoraRead", "uEjecutora", {
			async : false
		});

	$("#uEjecutora").val(cUR);
	cargaGrid();
	//creaDialogDetalle();
	creaDialogoLog();
	modalDetalle = new bootstrap.Modal(document.getElementById('dialog-Detalle'), 'data-bs-backdrop');
	
	$("#btn_Autoriza").button();

	if( mensaje != "" )
		alert(mensaje);
	
	queryFormPost ("consultaBoletosAutorizacion", {async:false });
	
	if( $("#cTieneBoleto").val() == 1  ) {
		$('.alert').alert();	
	} else {
		$(".alert").alert('close');
	} 
	
	$("#checkAll").change(
		function(){
			if ($('#checkAll').is(':checked')){				
				$("input:checkbox").attr('checked', 'checked');
			}else{
				$("input:checkbox").removeAttr('checked');
				$("#chkIntegra").attr('checked', 'checked');
			}
				
		});
		
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
	

var f ;
function cargaGrid() {
	$("#cWhere").val("nIdEmpleado = " + numeroEmpleado);

	oTable = $("#dt_AutorizarLayouts").dataTable({
		"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bScrollCollapse" : true,
			"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_AutorizaFirmantesViaticos&qw=" + " " + encodeURI($("#cWhere").val()),
		aoColumns : [
			{
				sName : "id",
				sClass : "alignLeft"
			},	{
				sName : "cUnidadResponsable",
				sClass : "alignLeft"
			},	{
				sName : "nFolio",
				bSearchable : true,
				bSortable : true,
				bVisible : true,
				sClass : "alignLeft"
			},	{
				sName : "RFC",
				bSearchable : true,
				bSortable : true,
				bVisible : true,
				sClass : "alignLeft"
			},	{
				sName : "cNombre",
				sClass : "alignLeft"
			},	{
				sName : "mTotalAgenda",
				sClass : "alignLeft"
			},	{
				sName : "mTotalTransporte",
				sClass : "alignLeft"
			},	{
				sName : "cNombreComision",
				sClass : "alignLeft"
			},{
				sName : "diasComision",
				sClass : "alignLeft"
			}, {
				sName : "cNota",
				sClass : "alignLeft"
			}, {
				sName : "cTieneBoleto",
				sClass : "alignLeft"
			}
		],
		oLanguage : es_mx
	});

	$("#dt_AutorizarLayouts tbody").click(function(event) {
		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});
	
	$("#dt_AutorizarLayouts tbody").dblclick(
			function(event) {
				
				$(oTable.fnSettings().aoData).each(
					function() {
						$(this.nTr).removeClass('row_selected');
					}
				);

				$(event.target.parentNode).addClass('row_selected');
				aPos = oTable.dataTable().fnGetPosition(event.target.parentNode);
				
				var aData = oTable.fnGetData(aPos);
				f = aData[2];
				
				$("#idComision").val(f);
				queryFormPost("leeValoresInicialesViaticos", {async: false});
				queryFormPost("leeTotalesAgenda", {async: false});
				queryFormPost("leeTotalesTransporte", {async: false});
				creaTableAgenda();
				creaTableTransporte();
				creaTableAutoriza();
				
				let totalAgenda = $("#totalAgenda").val();
				let totalTransporte = $("#totalTransporte").val();
				let totalGral = (parseFloat(totalAgenda) + parseFloat(totalTransporte)).toFixed(2);
				
				totalAgenda = addCommas(totalAgenda);
				$("#totalAgenda").val(totalAgenda);
				
				totalTransporte = addCommas(totalTransporte);
				$("#totalTransporte").val(totalTransporte);
				
				totalGral = addCommas(totalGral);
				$("#totalGeneral").val(totalGral);
				
				$("#nidComision").val($("#idComision").val());
				modalDetalle.show();
				
				return;
			});
}

	
function creaDialogDetalle(){
		
		modalDetalle.show();
		
}	

function consolidarFolio(estatus) {
	mensajeError ="";
	$("#nFolios").val("");
	var vacio = true;
	var tListado = $("#dt_AutorizarLayouts").dataTable().fnGetData();
	var tListado2 = document.getElementById('dt_AutorizarLayouts');

	$("#nFolios").val("");
	var folios = "";
	var token = "";
	
	for( i = 0; i < tListado.length; i++ ) {
		var row = tListado2.rows[ i + 1 ];
		var chkbox = row.cells[ 0 ].childNodes[ 0 ];

		if( null != chkbox && true == chkbox.checked ) {
			folios += token + tListado[ i ][ 2 ];
			
			if (estatus == "rechazo") {
				$("#idComision").val(tListado[ i ][ 2 ]);
				queryFormPost("consultaTienePagosViaticos", {async: false});
				
				if ( $("#tienePagos").val() != 0 ) {
					queryFormPost("consultaSaldoEnSolicitudes", {async: false});
					if ($("#saldoPagos").val() != 0 ) {
						mensajeError += token + tListado[ i ][ 2 ];
					}	
				}
			}	 
			$("#nFolios").val(folios);
			token = ",";
			vacio = false;
		}
	}
	
	if (mensajeError != "") {
		alert ("Los folios " + mensajeError + " tienen pagos y/o saldo por lo que no pueden rechazar");
		vacio = true;
	}
	
	return vacio;
}	

function AutorizaComisiones() {
	var vacio = true;
	vacio = consolidarFolio('Autoriza');

	if( !vacio ) {

				Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se autorizarán todas las comisiones seleccionadas,",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  	if (aplicaComisiones()) {
							Swal.fire(
									  'Autorización',
									  'Se autorizaron todas las comisiones correctamente',
									  'success'
									)
						} else { 
							Swal.fire("Error firmando los viaticos", logErrores, "error")
				  		}
				  } 
				})

	} else  {
		Swal.fire("Seleccione..","Debe seleccionar al menos un Pago para autorizar.", "info");
		return;
	}
		
}
var logErrores = "";
function aplicaComisiones() {
		var firmado = false;
		
		
		$.ajax({
			url : "../viaticos/autorizarViaticos",
			type : 'get',
			async : false,
			data : $("#formAutViaticos").serialize(),
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

function rechazoComisiones() {
	
	var vacio = true;
	vacio = consolidarFolio('rechazo');

	if( !vacio ) {

				Swal.fire({
				  title: '¿Desea continuar?',
				  text: "Se rechazarán todas las comisiones seleccionadas,",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  	if (rechazarComisiones()) {
							Swal.fire(
									  'Cancelación',
									  'Se cancelaron todas las comisiones correctamente',
									  'success'
									)
						} else { 
							Swal.fire("Error cancelando los viaticos", logErrores, "error")
				  		}
				  } 
				})

	} else if (mensajeError == "") {
		Swal.fire("Seleccione..","Debe seleccionar al menos un Pago para autorizar.", "info");
		return;
	}
}
function rechazarComisiones() {
		var firmado = false;
		
		$.ajax({
			url : "../viaticos/rechazarViaticos",
			type : 'get',
			async : false,
			data : $("#formAutViaticos").serialize(),
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
	
function creaTableAgenda(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableAgenda = $('#tablaAgendaConsulta').dataTable(
			{
				"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bScrollCollapse" : true,
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
				}, {
					sName : "nPorcentaje"
				}],
				oLanguage : es_mx
			});			
			
	}
	
function creaTableTransporte(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableTransporte = $('#tablaTransporteConsulta').dataTable(
			{
				"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
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
		sWhere ="cAutorizado = 'S' and nFolio =" + $("#idComision").val();
		oTableAutorizaciones = $('#tablaAutoriza').dataTable(
			{
				"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
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

     /**
	 * Crea dialogo que muestra el log
	 */
	function creaDialogoLog() {
		if( mostrarResultado )
			$("#logTable").css("display", "block");
	
		$("#dlg-Msg").dialog({
			autoOpen : mostrarResultado,
			height : 600,
			width : 800,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
	}
	
	function muestraLog() {
		$("#dlg-Msg").dialog("open");
	}