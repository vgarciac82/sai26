$(document).ready(function() {
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
	});

function guardaNuevaAgenda(){
		$("#nidAgenda").val($("#idAgenda").val());
		$("#idComision").val($("#cComision").val());
		if (validaCampos()){ //esta en comision viaticos
			 if(validaDatosPrevios()){ //esta en comision viaticos
				if(agregarNuevaAgenda()) 
					limpiarValores();  //esta en comision viaticos
			}
		}	
}

function verificaFechasEdicion() {
	$("#nidAgenda").val($("#idAgenda").val());
	$("#idComision").val($("#cComision").val());
	verificaFechas();
}

function agregarNuevaAgenda(){
		var guardado = false;
		var logErrores = "";
		setValores();
	
		$.ajax({
			url : "../viaticos/guardarAgenda",
			type : 'post',
			async : false,
			data : $("#formEdicion").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					creaTableAgendaEditar();
					limpiar();
					Swal.fire("OK","¡Agenda guardada con exito! \n\r Al finalizar no olvide Guardar y Enviar a Firmar", "success");
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
			Swal.fire("Error guardando agenda", logErrores, "error")
			
		return guardado;
	}
	
	function agregarNuevoTransporte(){
		var guardado = false;
		var logErrores = "";
		
		setValores();
		validarDatosTransporte();
		
		$.ajax({
			url : "../viaticos/guardarTransporte",
			type : 'post',
			async : false,
			data : $("#formEdicion").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					creaTableTransporteEditar();
					limpiarTransporte();
					Swal.fire("OK","¡Transporte guardado con exito! \n\r Al finalizar no olvide Guardar y Enviar a Firmar", "success");
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
			Swal.fire("Error guardando transporte", logErrores, "error")
			
	}
	
	function mostrarBotonesAgenda() {
		$("#btnEditarAgenda").hide(); 
		$("#btnAgregaAgenda").show(); 
	}
	
	function mostratBotonesTransporte () {
		$("#btnEditarTrans").hide();
		$("#btnAgregarTrans").show(); 	
	}
	
	function guardarEnviarAgenda(){
		var guardado = false;
		var logErrores = "";
		setValores();
		$("#noEmpleadoComision").val($("#nEmpleado").val())
		
		queryFormPost("consultaAgendas", {async: false});
		if ($("#tieneAgenda").val() == 0) {
			Swal.fire("Capture la Agenda","Debe tener por lo menos una agenda para continuar.", "info");
			return false;
		}
		
		queryFormPost("consultaSaldoEnSolicitudes", {async: false});
		queryFormPost("consultaImporteEditado", {async: false});
		if (Number($("#saldoPagos").val()) > Number($("#saldoComision").val())) {
			Swal.fire("Revise importe editado","El total editado " + parseFloat($("#saldoComision").val()).toFixed(2) + " es menor que el importe de las solicitudes generadas por " + parseFloat($("#saldoPagos").val()).toFixed(2), "info");
			return false;
		}
		
		$.ajax({
			url : "../viaticos/enviarFirmar",
			type : 'post',
			async : false,
			data : $("#formEdicion").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					limpiar();
					limpiarTransporte();
					Swal.fire("OK","¡Actualización del checador y correo enviado con exito!", "success");
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
				logErrores = errorThrown.ERROR;
			}
		});
	
		if (!guardado)
			Swal.fire("Error enviando a Firmar Modificacion de Agenda", logErrores, "error")
			
		return guardado;
	}
	
	function updateAgenda(){
		if(validaCampos()){
					let actualizado = false;
					let logErrores = "";
					setValores();
				
					$.ajax({
						url : "../viaticos/actualizarAgenda",
						type : 'post',
						async : false,
						data : $("#formEdicion").serialize(),
						dataType : 'json',
						success : function(j) {
							let exito = j.success;
				
							if (exito) {
								actualizado = true;
								creaTableAgendaEditar();
								$("#btnEditarAgenda").hide();
								$("#btnAgregaAgenda").show();
								limpiar();
								Swal.fire("OK","¡Guardado con exito! \n\r Al finalizar no olvide Guardar y Enviar a Firmar", "success");
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
				
					if (!actualizado)
						Swal.fire("Error actualizando la agenda seleccionada", logErrores, "error")
				}
		
	}
	
	function editarTransporte(){
		var guardado = false;
		var logErrores = "";
		setValores();
			
		validarDatosTransporte();
		
		$.ajax({
			url : "../viaticos/actualizarTransporte",
			type : 'post',
			async : false,
			data : $("#formEdicion").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					creaTableTransporteEditar();
					limpiarTransporte();
					$("#editarTransporte").hide();
					$("#agregarNuevoTransporte").show();
					Swal.fire("OK","¡Transporte guardado con exito! \n\r Al finalizar no olvide Guardar y Enviar a Firmar", "success");
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
			Swal.fire("Error actualizando el transporte seleccionado", logErrores, "error")
			
	}
	
	function setValores() {
		$("#id").val($("#cComision").val());
		
	}
	
	function creaTableTransporteEditar(){
		sWhere ="nIdComision =" + $("#cComision").val();
		oTableTransporte = $('#tablaTransporteEditar').dataTable(
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
				}, {
					sName : "eliminar",
					sClass : "alignCenter"
				}],
				oLanguage : es_mx
			});			
		
		$("#tablaTransporteEditar tbody").dblclick(function(event) {

			$(oTableTransporte.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTableTransporte.fnGetPosition(event.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			let idTipo = $("#tablaTransporteEditar").dataTable().fnGetData()[currIndex][1];
			let origen = $("#tablaTransporteEditar").dataTable().fnGetData()[currIndex][2];
			let monto = $("#tablaTransporteEditar").dataTable().fnGetData()[currIndex][3];
			let km = $("#tablaTransporteEditar").dataTable().fnGetData()[currIndex][4];
			let numEco = $("#tablaTransporteEditar").dataTable().fnGetData()[currIndex][5];
			let tieneVales = $("#tablaTransporteEditar").dataTable().fnGetData()[currIndex][6];
		
			monto = quitaFrmt(monto);
			
			$("#transporteCombo").val(idTipo);
			$("#descripcion").val(origen);
			$("#importeT").val(monto);
			$("#km").val(km);
			$("#nEconomico").val(numEco);
			$("#chk_tieneVales").val(tieneVales);
			
			if(document.getElementById("transporteCombo").value== 1){
				$("#transporteDet").hide();
			} else if(document.getElementById("transporteCombo").value==2 ) {
				$("#transporteDet").hide();
			} else {
				$("#transporteDet").show();
			
			if (document.getElementById("transporteCombo").value==  4) {
				$("#lblnEconomico").attr("style", "visibility: visible");
				$("#nEconomico").attr("style", "visibility: visible");
			} else {
				$("#lblnEconomico").attr("style", "visibility: hidden");
				$("#nEconomico").attr("style", "visibility: hidden");
			}
		} 
			$("#btnEditarTrans").show();
			$("#btnAgregarTrans").hide();
			
		});	
	}
	
	
	function creaTableAgendaEditar(){
		sWhere ="nIdComision =" + $("#cComision").val();
		oTableAgenda = $('#tablaAgendaEditar').dataTable(
			{
				"bPaginate" : false,
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
					sName : "eliminar",
					sClass : "alignCenter"
				}],
				oLanguage : es_mx
			});			
			
			
		$("#tablaAgendaEditar tbody").dblclick(function(event) {

			$(oTableAgenda.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTableAgenda.fnGetPosition(event.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			let id = $("#tablaAgendaEditar").dataTable().fnGetData()[currIndex][0];
		
			$("#idAgenda").val(id);
			$("#btnEditarAgenda").show();
			$("#btnAgregaAgenda").hide();
			
			queryFormPost({
				queryName : "llenaValoresEditar",
				async : false,
				callback : function() {
					$("#pais").val(document.getElementById("cPais").value);
					cargaEstados();
					$("#estadocombo").val(document.getElementById("estado").value);
					cargaMunicipios();
					$("#municipiocombo").val($("#nidMunicipio").val());
					if($("#cTienePaquete").val()==1){
						$("#hasPackage").val(1);
						$("#chk_paquete").prop('checked', true);
						$(".inputPaquete").css('display', 'block');
					}
					if($("#cTieneHomologacion").val()==1){
						$("#hasSameRate").val(1);
						$("#chk_homologa").prop('checked', true);
						$(".inputHomologa").css('display','block');
					}
				}
			});

		});
	}