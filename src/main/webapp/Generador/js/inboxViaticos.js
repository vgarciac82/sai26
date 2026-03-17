let modalDetalle;
let modalEditar;
let modalPagos;
var oTable = $("#dt_AutorizarLayouts").dataTable();

$(document).ready(function() {
	
	$("#u_Login").val(cLogin);
	$("#RFCUsuario").val(RFCUsuario);
	$("#idEmpleado").val(numeroEmpleado);
	$("#btnBuscar").button();
	$("#btnLimpiar").button();
	
	if($("#cUR").val()=="A02" || $("#cUR").val()=="A03"){
		querySelectPost("cURVistasViaticosTodos", "uEjecutora", { async : false });
	} else
		querySelectPost("cUnidadEjecutoraVistasViaticos", "uEjecutora", { async : false });
		
	queryFormPost("proyectoUsuario", { async : false });
	queryFormPost("rolUsuarioRead", { async : false });

	$("#uEjecutora").val(cUR);
	cargaGrid();
	
	if ($("#tieneRolPagos").val() > 0 ) {
		$("#divEmp").show();	
	} else {
		$("#divEmp").hide();
	} 
		
	modalDetalle = new bootstrap.Modal(document.getElementById('dialog-Detalle'), 'data-bs-backdrop');
	modalEditar = new bootstrap.Modal(document.getElementById('dialog-Editar'), 'data-bs-backdrop');
	modalPagos = new bootstrap.Modal(document.getElementById('dialog-capturaPagos'), 'data-bs-backdrop');
	
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
	
	if ($("#tieneRolPagos").val() > 0 ) {
		$("#cWhere").val("cUnidadResponsable =  '" + $("#uEjecutora").val() + "' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N') ");	
	} else {
		$("#cWhere").val("cUnidadResponsable =  '" + $("#uEjecutora").val() + "' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N') AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());
	}
	

	oTable = $("#dt_listaViaticos").dataTable({
		"bPaginate": false,
		"bLengthChange": true,
		"bFilter": false,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": false,
		"bJQueryUI": true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType": "full_numbers",
		"bServerSide": true,  
		"order": [[ 3, "desc" ]],
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_inboxViaticos&qw=" + " " + encodeURI($("#cWhere").val()),
		aoColumns : [
			{
				sName : "nIdComision",
			},  {
				sName : "nIdEmpleado",
			}, 	{
				sName : "cUnidadResponsable",
			},	{
				sName : "nombreCompleto",
			}, 	{
				sName : "fInicio",
			},	{
				sName : "fFin",
			},	{
				sName : "Estatus",
			},	{
				sName : "nOrigen",
			},	{
				sName : "idConsulta",
			}
		],
		oLanguage : es_mx
	});

	$("#dt_listaViaticos tbody").click(function(event) {
		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});
	
}

function consultaSolicitud(idComision) {
	$("#idComision").val(idComision);

	// Carga dinámica del contenido
	$("#modalBodyDetalle").load("ConsultaComisionViaticos.jsp?idComision=" + idComision, function () {
		queryFormPost("leeValoresInicialesViaticos", { async: false });
		queryFormPost("leeTotalesAgenda", { async: false });
		queryFormPost("leeTotalesTransporte", { async: false });

		createTableAgendaConsulta();
		creaTableTransporteConsulta();
		creaTableAutoriza();

		// Formatea totales
		let totalAgenda = $("#totalAgenda").val();
		let totalTransporte = $("#totalTransporte").val();
		let totalGral = (parseFloat(totalAgenda) + parseFloat(totalTransporte)).toFixed(2);

		$("#totalAgenda").val(addCommas(totalAgenda));
		$("#totalTransporte").val(addCommas(totalTransporte));
		$("#totalGeneral").val(addCommas(totalGral));

		$("#nidComision").val(idComision);

		if ($("#cDocHaplicado").val() == "C" || $("#cDocHaplicado").val() == "N") {
			$("#btnCancelaComision").hide();
		} else {
			$("#btnCancelaComision").show();
		}

		modalDetalle.show();
	});
}

function catalogo_beneficiario(){
		$("#cTipoRfc").val("3");
		window.open('CatalogoBeneficiarios.jsp?formName=inboxViaticos&inputNoEmpTarget=nidEmpleado&inputDRFCTarget=cnombre&inputRFCTarget=cRFCEmpleado' ,'Beneficiarios', 'status=1, width=900px, height=430px, left=100px, resizable=yes');
		
	}
	
function consultarPagos(idComision){
			document.getElementById('uploadPagosFrm').setAttribute("src","CompruebaComisionViaticos.jsp?nFolioTramite=" + idComision);
			modalPagos.show();
}	

function editaSolicitud(idComision) {
	$("#cComision").val(idComision);
	
	queryFormPost("consultaEmpleado", {async: false});
	
	$("#btnEditarAgenda").hide();
	$("#btnEditarTrans").hide();
	
	setFechas();		 
	cargaPaises();
	cargaEstados();
	cargaMunicipios();
	activarBotones();
	cargaNiveles();
	activarPaquete();
	cargaPaquetes();
	cargaTransporte();
	creaTableAgendaEditar();
	creaTableTransporteEditar();	
	cargaCuota();
		
	querySelectPost("cat_tipoMoneda", "cMoneda",{async: false });
	modalEditar.show();
	
}

	function addCommas(nStr){
            nStr += '';
            x = nStr.split('.');
            x1 = x[0];
            x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
               x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2.substring(0,3);
     }
     
    
function creaTableAgenda(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableAgenda = $('#tablaAgenda').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bInfo" : false,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"sScrollX" : "100%",
				"order": [[0, "asc"]],
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
				"bInfo" : false,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"sScrollX" : "100%",
				"order": [[0, "asc"]],
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
				}, {
					sName : "cNota"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	function cancelar() {
		
		$("#idEmpleado").val($("#numEmpleado").val());
		
		Swal.fire({
					  title: '¿Desea continuar?',
					  text: " La comisión se cancelará y esta acción no se podra revertir",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
							queryFormPost("estatusComision", {async: false});
						    queryFormPost("consultaPagosViaticos", {async: false});
							
							if($("#cDocHaplicado").val() =="F" ){
								
								Swal.fire("No se puede cancelar","Esta comision ya fue finalizada", "info");
							
							} else if($("#cDocHaplicado").val() =="C" ){
								
								Swal.fire("Ya esta cancelada","Esta comision ya fue cancelada", "info");
									
							} else if( $("#cDocHaplicado").val() == "S"){
								
								if($("#existenPagos").val() > 0) {
									//Si tiene pagos y el saldo es cero se cancela en el control de asistencia
									queryFormPost("consultaSaldoEnSolicitudes", {async: false});
									queryFormPost("consultaTieneComisionSinViaticos", {async: false});
									
									if ($("#saldoPagos").val() == 0 && $("#tieneComSinViat").val() == 0) {
											queryFormPost("cancelaComision", {async: false});
											cancelarAsistencia();
											cargaGrid();
									} else {
										Swal.fire("No se puede cancelar","Esta comision tiene pagos y/o saldo por lo que no puede cancelarse", "info");
									}
								
								} else {
									//Si no tiene pagos y ya fue aplicada se cancela en el control de asistencia
									queryFormPost("cancelaComision", {async: false});
									cancelarAsistencia();
									cargaGrid();
								}
							} else {
								//Se ejectua cuando no esta aplicado el tramite
								queryFormPost("consultaFolioCaso", {async: false});
								queryFormPost("cancelaComision", {async: false});
								cancelarAsistencia();
								cargaGrid();
								Swal.fire("OK","Se cancelo la comision correctamente", "success");
							}
					  } 
					});
	}
	
	function cancelarAsistencia() {
			var eliminado = false;
			var logErrores;
			$.ajax({
					url : "../viaticos/cancelaComision",
					type : 'post',
					async : false,
					data : $("#inboxViaticos").serialize(),
					dataType : 'json',
					success : function(j) {
						var exito = j.success;
			
						if (exito) {
							eliminado = true;
							queryFormPost("cancelaComision", {async: false});
							Swal.fire("OK", j.messageList, "success")
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
			
				if (!eliminado)
					Swal.fire("Error cancelando la agenda", logErrores, "error")
	}
	
	function filtroStatus(){
		if($("#finalizados").prop("checked") && $("#rechazados").prop("checked")) { //check finalizados y rechazados seleccionados		
			if ($("#tieneRolPagos").val() > 0) {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F','C','N')");					
				} else if ($("#nFolio").val() != "") { 				
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F','C','N') AND nIdComision = " + $("#nFolio").val());				
				} else if ($("#nidEmpleado").val() != "") { 
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F','C','N') AND nIdEmpleado = " + $("#nidEmpleado").val());				
				}
			} else {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F','C','N') AND nIdEmpleado = " + $("#nEmpleadoUsuario").val()) ;					
				} else if ($("#nFolio").val() != "") { 				
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F','C','N') AND nIdEmpleado = " + $("#nEmpleadoUsuario").val() +" AND nIdComision = " + $("#nFolio").val());				
				} 
			}
		} else if($("#finalizados").prop("checked")) { //check finalizados seleccionado
			if ($("#tieneRolPagos").val() > 0) {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F')");					
				} else if ($("#nFolio").val() != "") { 				
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F') AND nIdComision = " + $("#nFolio").val());				
				} else if ($("#nidEmpleado").val() != "") { 
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F') AND nIdEmpleado = " + $("#nidEmpleado").val());				
				}	
			} else {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F') AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());					
				} else if ($("#nFolio").val() != "") { 				
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('F') AND nIdComision = " + $("#nFolio").val() + "AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());				
				}
			}
		} else if($("#rechazados").prop("checked")) { //check rechazados seleccionados
			if ($("#tieneRolPagos").val() > 0) {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('C','N')");					
				} else if ($("#nFolio").val() != "") { 				
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('C','N') AND nIdComision = " + $("#nFolio").val());				
				} else if ($("#nidEmpleado").val() != "") { 
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('C','N') AND nIdEmpleado = " + $("#nidEmpleado").val());				
				}
			} else {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('C','N') AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());					
				} else if ($("#nFolio").val() != "") { 				
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') IN ('C','N') AND nIdComision = " + $("#nFolio").val() + "AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());				
				} 
			}
		} else {
			if ($("#tieneRolPagos").val() > 0) {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N')");				
				} else if ($("#nFolio").val() != "") { 
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N') AND nIdComision = " + $("#nFolio").val());					
				} else if ($("#nidEmpleado").val() != "") {
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N') AND nIdEmpleado = " + $("#nidEmpleado").val());					
				}			
			} else {
				if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N') AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());				
				} else if ($("#nFolio").val() != "") { 
					$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N') AND nIdComision = " + $("#nFolio").val() + "AND nIdEmpleado = " + $("#nEmpleadoUsuario").val());					
				}
			}
		}

		oTable = $("#dt_listaViaticos").dataTable({
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
			//"sScrollY" : "100%",
			"order": [[ 3, "desc" ]],
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_inboxViaticos&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns : [
				{
					sName : "nIdComision",
				},  {
					sName : "nIdEmpleado",
				}, 	{
					sName : "cUnidadResponsable",
				},	{
					sName : "nombreCompleto",
				}, 	{
					sName : "fInicio",
				},	{
					sName : "fFin",
				},	{
					sName : "Estatus",
				},	{
					sName : "nOrigen",
				},	{
					sName : "idConsulta",
				}
			],
			oLanguage : es_mx
		});
	
		$("#dt_listaViaticos tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		});
	}
		
	function bloqueaEmpleado(){
		$("#nidEmpleado").prop('disabled', true);
	}
	
	function bloqueaFolio(){
		$("#nFolio").prop('disabled', true);
	}
	
	function cambiaNumEmp() {
		if ($("#nidEmpleado").val() != "") {
			queryFormPost("consultaNombre", {async: false});
		}
	}
	
	function buscar(){
		if ($("#nFolio").val() == "" && $("#nidEmpleado").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Debe capturar el Folio o el Numero de Empleado para hacer la busqueda."});
			return false;
		}
		
		if ($("#nFolio").val() != ""){
			if($("#finalizados").prop("checked") && $("#rechazados").prop("checked")) { 
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdComision = " + $("#nFolio").val() + " AND ISNULL(cDocHAplicado,'') IN ('F','C','N')");
			} else if($("#finalizados").prop("checked")) {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdComision = " + $("#nFolio").val() + " AND ISNULL(cDocHAplicado,'') IN ('F')");
			} else if($("#rechazados").prop("checked")) {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdComision = " + $("#nFolio").val() + " AND ISNULL(cDocHAplicado,'') IN ('C','N')");				
			} else {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdComision = " + $("#nFolio").val() + " AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N')");				
			}
		} else if ($("#nidEmpleado").val() != ""){
			if($("#finalizados").prop("checked") && $("#rechazados").prop("checked")) {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdEmpleado = " + $("#nidEmpleado").val() + " AND ISNULL(cDocHAplicado,'') IN ('F','C','N')");				
			} else if($("#finalizados").prop("checked")) {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdEmpleado = " + $("#nidEmpleado").val() + " AND ISNULL(cDocHAplicado,'') IN ('F')");				
			} else if($("#rechazados").prop("checked")) {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdEmpleado = " + $("#nidEmpleado").val() + " AND ISNULL(cDocHAplicado,'') IN ('C','N')");				
			} else {
				$("#cWhere").val("cUnidadResponsable LIKE '%" + $("#uEjecutora").val() + "%' AND nIdEmpleado = " + $("#nidEmpleado").val() + " AND ISNULL(cDocHAplicado,'') NOT IN ('F','C','N')");
			}			
		}

		oTable = $("#dt_listaViaticos").dataTable({
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
			"order": [[ 3, "desc" ]],
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_inboxViaticos&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns : [
				{
					sName : "nIdComision",
				},  {
					sName : "nIdEmpleado",
				}, 	{
					sName : "cUnidadResponsable",
				},	{
					sName : "nombreCompleto",
				}, 	{
					sName : "fInicio",
				},	{
					sName : "fFin",
				},	{
					sName : "Estatus",
				},	{
					sName : "nOrigen",
				},	{
					sName : "idConsulta",
				}
			],
			oLanguage : es_mx
		});
	
		$("#dt_listaViaticos tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');		
		});
	}
	
	function limpiaCampos(){
		$("#nFolio").prop('disabled', false);
		$("#nidEmpleado").prop('disabled', false);
		$("#nFolio").val("");
		$("#nidEmpleado").val("");
		$("#finalizados").prop('checked', false);
		$("#rechazados").prop('checked', false);
		cargaGrid();
	}