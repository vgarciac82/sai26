function guardaNuevaAgenda(){
		if (validaCampos()){
			 if(validaDatosPrevios()){
				if(agregarNuevaAgenda()) 
					limpiarValores();
			}
		}	
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
					creaTableAgenda();
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
					creaTableTransporte();
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
		
		if (Number($("#saldoPagos").val()) > Number($("#saldoComision").val())) {
			Swal.fire("Nota","El total editado " + parseFloat($("#saldoComision").val()).toFixed(2) + " es menor que el importe de la solicitudes generadas por " + parseFloat($("#saldoPagos").val()).toFixed(2), "info");
			//return false;
		}
		queryFormPost("consultaSaldoEnSolicitudes", {async: false});
		queryFormPost("consultaImporteEditado", {async: false});
		

		Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se actualizará el control checador con los cambios y se eliminaran las firmas actuales.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
						queryFormPost("consultaSaldoEnSolicitudes", {async: false});
						queryFormPost("consultaImporteEditado", {async: false});
						
						$.ajax({
							url : "../viaticos/enviarFirmarAgenda",
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
									//location.reload(true);
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
					Swal.fire("Error al guardar y enviar a firmar, notifique al adminsitrador.", logErrores, "error")
				
				}
			});
			
		
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
						creaTableAgenda();
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
					creaTableTransporte();
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
		$("#id").val($("#idComision").val());
		$("#tipoOperacion").val($("#operacion").val())
		$("#numEmpleado").val($("#nEmpleado").val());
	}