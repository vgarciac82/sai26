var meses = [ "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
		"Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" ];

$(document)
		.ready(
				function() {
					if ($("#rolAdminAnexo").val() == "FALSE" || $("#rolAdminAnexo").val() == "false"){
						$("#btnCancela").hide();
					}
						
					$("input.AyudaSyC").subIniciaDlg();
					$("input.autoCompletaSyC").subIniciaAutoCompleta();
					
					$("#btnBorraEp").button();

					queryFormPost("cEjercicioRead", {
						async : false
					});
					querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
					querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo",{async: false });
					
					$('.currency').blur(function() {
						$('.currency').formatCurrency();
					});

					creaTablaEPs();

					$("#dialog-EPS").dialog({
						autoOpen : false,
						width : 800,
						height : 700
					});

					$("#dt_epAgregadas tbody").click(
							function(event) {
								$(
										$("#dt_epAgregadas").dataTable()
												.fnSettings().aoData).each(
										function() {
											$(this.nTr).removeClass(
													'row_selected');
										});
								$(event.target.parentNode).addClass(
										'row_selected');
							});

					/*
					 * Solo si esta en una operacion de captura, permite
					 * modificar la tabla.
					 */
					if (operacion == 1) {
						$("#dt_epAgregadas tbody").dblclick(
								function(event) {
									$(
											$("#dt_epAgregadas").dataTable()
													.fnSettings().aoData).each(
											function() {
												$(this.nTr).removeClass(
														'row_selected');
											});

									$(event.target.parentNode).addClass(
											'row_selected');

									var aPos = $("#dt_epAgregadas").dataTable()
											.fnGetPosition(
													event.target.parentNode);
									var aData = $("#dt_epAgregadas")
											.dataTable().fnGetData(aPos);

									$("#epEditar").val(aData[1]);
									$("#nPosicion").val(aPos);

									$("#epDisp").val($("#epEditar").val());
									queryFormPost(
											"leeImportesDisponiblesEpAnexo1", {
												async : false
											});
									queryFormPost("leeImportesDetalleAnexo1EP",
											{
												async : false
											});
									$("#totalEditado").formatCurrency();
									$("#dialogEditaImportes").dialog("open");
									return;
								});
					}

					$("#tblEPS tbody").click(
							function(event) {
								$(oTableSeleccionEP.fnSettings().aoData).each(
										function() {
											$(this.nTr).removeClass(
													'row_selected');
										});
								$(event.target.parentNode).addClass(
										'row_selected');
							});

					$("#btnAgregaClaves").button().click(function() {
						agregarClaves();
					});
					$("#btnPresupuesto").button().click(function() {
						muestraEPS();
					});

					$("#checkAll").change(function() {
						if ($('#checkAll').is(':checked'))
							$("input:checkbox").attr('checked', 'checked');
						else
							$("input:checkbox").removeAttr('checked');

					});

					$("#dlgCaptuarMotivo").dialog({
						autoOpen : false,
						height : 330,
						width : 400,
						modal : true,
						buttons : {
							"Aceptar" : function() {
								capturaRechazo(true);
							},
							"Cancelar" : function() {
								$(this).dialog("close");
							}
						},
						close : function() {
							closeDlgMovito();
						}
					});

					$("#dialogEditaImportes")
							.dialog(
									{
										autoOpen : false,
										height : 536,
										width : 577,
										modal : true,
										buttons : {
											"Aceptar" : function() {
												var total = $("#totalEditado")
														.val().replace(/[$]/g,
																"");
												total = total.replace(/,/g, "");

												if (Number(total) == 0) {
													alert("El importe no puede ser $0.00");
													return false;
												}

												for (i = 0; i < meses.length; i++) {
													$("#epUpdate").val($("#epDisp").val());
													$("#cMesUpdate").val( parseInt(i + 1,10));
													var valor = $("#" + meses[i]+ "Editar").val().replace(/[$]/g, "").replace(/,/g, "");
													$("#mImporteUpdate").val(valor);
													
													queryFormPost({
														queryName : "verificaExisteDetalleEpMes",
														async : false, 
														callback : function() {
															if ($("#existeDetalle").val()=="0" && Number(valor)>0)
																queryFormPost("insertAnexo1Detalle",{async : false});
															else
																queryFormPost("updateAnexo1Detalle",{async : false});
														}
													});
													
												}

												obtieneTotal();
												$("#epEditar").val("");
												$("#nPosicion").val("");
												$(this).dialog("close");
												creaTablaEPs();
												reiniciaImportes();
												$("#accion").val("-1");
												$.ajax({
													type : "POST",
													url : "../Anexo1/crear",
													cache : false,
													async : false,
													data : $("#anexo1").serialize(),
													error : function(xhr, textStatus, errorThrown) {},
													success : function(RS) {}
												});
											},
											"Cancelar" : function() {
												$(this).dialog("close");
												reiniciaImportes();
											}
										},
										close : function() {
										}
									});

					cssReadOnly();

					if (operacion == 2)
						$("#autorizacionDiv").show();

					$("#dialog-firmantes")
							.dialog(
									{
										autoOpen : false,
										height : 400,
										width : 480,
										modal : true,
										buttons : {
											"Aceptar" : function() {
												
												if ($("#cNombreVoBo").val() == "") {
													alert("Falta Ingresar Nombre en Datos Vº Bº");
													return;
												} else if ($("#cPaternoVoBo").val() == "") {
													alert("Falta Ingresar Apellido Paterno en Datos Vº Bº");
													return;
												} else if ($("#cMaternoVoBo").val() == "") {
													alert("Falta Ingresar Apellido Materno en Datos Vº Bº");
													return;
												} else if ($("#cPuestoVoBo").val() == "") {
													alert("Falta Ingresar Puesto en Datos Vº Bº");
													return;
												}

												if ($("#cNombreAut").val() == "") {
													alert("Falta Ingresar Nombre en Datos Autorizar");
													return;
												} else if ($("#cPaternoAut").val() == "") {
													alert("Falta Ingresar Apellido Paterno en Datos Autorizar");
													return;
												} else if ($("#cMaternoAut").val() == "") {
													alert("Falta Ingresar Apellido Materno en Datos Autorizar");
													return;
												} else if ($("#cPuestoAut").val() == "") {
													alert("Falta Ingresar Puesto en Datos Autorizar");
													return;
												}
												
												if($("#cNombreEla").val() == ""){ alert("Falta Ingresar Nombre en Datos Elabora"); return; } 
												else if($("#cPaternoEla").val() == ""){ alert("Falta Ingresar Apellido Paterno en Datos Elabora"); return; }
												else if($("#cMaternoEla").val() == ""){ alert("Falta Ingresar Apellido Materno en Datos Elabora"); return; }
												else if($("#cPuestoEla").val() == ""){ alert("Falta Ingresar Puesto en Datos Elabora"); return; }

												$("#cNombreVo").val($("#cNombreVoBo").val());
												$("#cPaternoVo").val($("#cPaternoVoBo").val());
												$("#cMaternoVo").val($("#cMaternoVoBo").val());
												$("#cPuestoVo").val($("#cPuestoVoBo").val());

												$("#cNombreA").val($("#cNombreAut").val());
												$("#cPaternoA").val($("#cPaternoAut").val());
												$("#cMaternoA").val($("#cMaternoAut").val());
												$("#cPuestoA").val($("#cPuestoAut").val());
												
												$("#cNombreE").val($("#cNombreEla").val());
												$("#cPaternoE").val($("#cPaternoEla").val());
												$("#cMaternoE").val($("#cMaternoEla").val());
												$("#cPuestoE").val($("#cPuestoEla").val());												

												$("#firmanteVoBo").val($("#cNombreVoBo").val()+ " "+ $("#cPaternoVoBo").val()+ " "+ $("#cMaternoVoBo").val());
												$("#firmanteAut").val($("#cNombreAut").val()+ " "+ $("#cPaternoAut").val()+ " "+ $("#cMaternoAut").val());
												
												$("#firmanteEla").val($("#cNombreEla").val()+" "+$("#cPaternoEla").val()+" "+$("#cMaternoEla").val());	
												
												if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")){
													
													if($("#firmanteExiste").val() == "Existe"){
														queryFormPost({	
															queryName : "firmanteModuloUpdate", 
															async : false, 
															callback : function(){
																msn = "Actualizado Correctamente Firmantes";
															} 
														});
													}else{
														queryFormPost({	
															queryName : "firmanteModuloCreate", 
															async : false, 
															callback : function(){
																msn = "Guardado Correctamente Firmantes";
															} 
														});
													}
													
													if ($("#oficioDelegatorioUpdate").prop("checked")){
														
														var msn = "";
														
														if($("#cFolioOficioUpdate").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
														else if($("#dFechaOficioUpdate").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
														else if($("#cNombreTitularUpdate").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
														else if($("#cApellidoPaternoTitularUpdate").val() == ""){ alert("Falta Ingresar Apellido Paterno del Titular."); return; }
														else if($("#cApellidoMaternoTitularUpdate").val() == ""){ alert("Falta Ingresar Apellido Materno del Titular."); return; }
														else if($("#cPuestoTitularUpdate").val() == ""){ alert("Falta Ingresar Puesto del Titular."); return; }
														
														$("#cFolioOficioAux").val($("#cFolioOficioUpdate").val());
														$("#dFechaOficioAux").val($("#dFechaOficioUpdate").val());
														$("#cNombreTitularAux").val($("#cNombreTitularUpdate").val());
														$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoTitularUpdate").val());
														$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoTitularUpdate").val());
														$("#cPuestoTitularAux").val($("#cPuestoTitularUpdate").val());
														
														if($("#firmanteOficioExiste").val() == "Existe"){
															queryFormPost({	
																queryName : "tPagoFirmanteDelagatorioUpdate", 
																async : false, 
																callback : function(){
																	msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
																} 
															});									
														}else{
															queryFormPost({	
																queryName : "tPagoFirmanteDelagatorioCreate", 
																async : false, 
																callback : function(){
																	msn = "Firmantes Oficio Delegatorio guardado correctamente.";
																} 
															});
														}
															
														alert(msn);
															  
														$("#cFolioOficioUpdate").val("");
														$("#dFechaOficioUpdate").val("");
														$("#cNombreTitularUpdate").val("");
														$("#cApellidoPaternoTitularUpdate").val("");
														$("#cApellidoMaternoTitularUpdate").val("");
														$("#cPuestoTitularUpdate").val("");
														
													}
												}
												
												if ($("#oficioDeleVoBoUpdate").prop("checked")){
													
													var msn = "";
													
													if($("#cFolioOficioVoBoUpdate").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
													else if($("#dFechaOficioVoBoUpdate").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
													else if($("#cNombreTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
													else if($("#cApellidoPaternoTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Apellido Paterno del Titular."); return; }
													else if($("#cApellidoMaternoTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Apellido Materno del Titular."); return; }
													else if($("#cPuestoTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Puesto del Titular."); return; }
													
													$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBoUpdate").val());
													$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBoUpdate").val());
													$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBoUpdate").val());
													$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoTitularVoBoUpdate").val());
													$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoTitularVoBoUpdate").val());
													$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBoUpdate").val());
													
													if($("#firmanteOficioVoBoExiste").val() == "Existe"){
														queryFormPost({	
															queryName : "tPagoFirmanteDelegatorioVoBoUpdate", 
															async : false, 
															callback : function(){
																msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
															} 
														});									
													}else{
														queryFormPost({	
															queryName : "tPagoFirmanteDelegatorioVoBoCreate", 
															async : false, 
															callback : function(){
																msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
															} 
														});
													}
														
													alert(msn);
														  
													$("#cFolioOficioVoBoUpdate").val("");
													$("#dFechaOficioVoBoUpdate").val("");
													$("#cNombreTitularVoBoUpdate").val("");
													$("#cApellidoPaternoTitularVoBoUpdate").val("");
													$("#cApellidoMaternoTitularVoBoUpdate").val("");
													$("#cPuestoTitularVoBoUpdate").val("");
													
												}
												
												queryFormPost("tAnexo1EncabezadoUpdateFirmante",{async : false});

												$(this).dialog("close");
											},
											"Cancelar" : function() {
												$(this).dialog("close");
											}
										},
										close : function() {
										}
									});

					$("#dialog-Procesando").dialog({
						autoOpen : false,
						height : 400,
						width : 400,
						modal : true,
						async : false,
						open : function() {
							
							$("#divEsperaProcesando").attr("style", "visibility=visible");
							var fAppActualizada = actualizaMesAplicacion();

							/********************************************************************************
							 ***SE CAMBIA TABLA DE ANEXO POR PAGOAPARTADO PARA MOVER A LAS CUENTAS COMODIN***
							 ********************************************************************************/
							var tipo = "aplicarMotor";
							var caNoContrarrecibo = $("#caNoContrarrecibo").val();
							var campo = "nFolioPagoApartado"; //"nFolioAnexo1";
							var tablaEnc = "tPagoApartadoEncabezado"; //"tAnexo1Encabezado";
							var campoCondicion = "caNoContrarrecibo";
							var tablaDet = "tPagoApartadoDetalle"; //"tAnexo1Detalle";
							var tipoAplicar = "PAGOAPARTADO"; //$("#cDocumento").val();
							
							if (!fAppActualizada) {
								$("#divEsperaProcesando").attr("style", "visibility=hidden");
								$("#dialog-Procesando").dialog("close");
								return false;
							}

							$.ajax({
								url : './cierrePresupuestal.jsp',
								type : 'post',
								dataType : 'json',
								data : {
									tipo : tipo,
									caNoContrarrecibo : caNoContrarrecibo,
									campo : campo,
									tablaEnc : tablaEnc,
									campoCondicion : campoCondicion,
									tablaDet : tablaDet,
									tipoAplicar : tipoAplicar
								},
								async : false,
								success : function(data) {
									if (data.sinSesion == 'sinSesion') {
										location.href = "../index.jsp";
									}
									
									if (data.estatus == "guardado") {
										alert("Documento Aplicado Correctamente: " + $("#caNoContrarrecibo").val());
										
										parent.document.getElementById("responsable").value = ResponsableSiguiente(operacion) + ";";
										parent.document.getElementById("operacion").value=OperacionSiguiente(operacion) + ";";
										
										parent.document.getElementById("pb_send").disabled = false;
										parent.document.getElementById("pb_send").click();
										
										breturnVal = true;
										
									}else{
										breturnVal = false;
										alert("El Documento No Se Aplico: " + $("#caNoContrarrecibo").val() + " - " + data.estatus);
									}
									$("#divEsperaProcesando").attr("style", "visibility=hidden");
									$("#dialog-Procesando").dialog("close");
								}
							});
						}
					});

					$("#dialog-Cancela").dialog({
						autoOpen: false,
						height: 250,
						width: 450,
						modal: true,
						buttons: {
								"Aceptar": function() {
									$("#divEsperaCancelando").attr("style","visibility=visible");
									var nFolioPagado = $("#nFolioApartado").val();
									var cTipoDocto = "PAGOAPARTADO";
									$.ajax({
										url: './cierrePresupuestal.jsp',
										type: 'post',
										dataType: 'json',
										async: true,
										data: {tipo:'cancelaEjercidoPagado', nFolioAdefa:nFolioPagado, tipoDocumento:cTipoDocto},
										success: function(data){
											if(data.sinSesion == "sinSesion"){
												location.href = "../index.jsp";
											}else if(data.estatus == "guardado"){
												queryFormPost("updateAnexo1EncabezadoCancelado",{async : false});
												$("#btnCancela").attr('disabled', true);
												alert("Apartado Cancelado Correctamente: " + $("#caNoContrarrecibo").val() );
											}else{
												alert( "Apartado NO Cancelado: " + $("#caNoContrarrecibo").val() + " - " + data.estatus ) ;
											}
											$("#divEsperaCancelando").hide();
											$("#dialog-Cancela").dialog("close");
											
										}
									}); 
								},
								"Cancelar": function() {
									$("#divEsperaCancelando").hide();
									$(this).dialog("close");							
								}
							},
						close: function(){},
						open: function() {}
					});				
			});

function blurConcepto() {
	if ("" == $("#cConcepto").val())
		$("#cConcepto").val("Escriba un concepto");
}

function onLoadPlantilla() {
	$("#procesar").dialog({
		autoOpen : false,
		height : 150,
		width : 200,
		modal : true,
		close : function() {
		}
	});
	queryFormPost("leeAnexo1Aplicado",{async : false});
	if ($("#anexoCancelado").val()=="C" || $("#anexoCancelado").val()=="N")
		$("#btnCancela").attr('disabled', true);
	/*
	//VALIDACION PARA QUE SOLO SEA UNA SOL DE RECUROS X PAGAR POR UR AL MES
	queryFormPost({
		queryName : "verificaSolAnexo1ExistentesUR",
		async : false,
		callback : function() {
			if ($("#solGeneradasUR").val() != "0" && operacion == 1) {
				alert("Ya existe una solicitud de este tipo para tu UR, esta solicitud tendra que ser descartada...")
				parent.document.getElementById("pb_cancel").click();
				return false;
			}
			
		}
	});
	*/
	$("#esperar").hide();
	
	if (operacion > 1) {
		$("#cConcepto").attr("readonly", "readonly");
		cssReadOnly();
		document.getElementById('btnPresupuesto').style.display = 'none';
		document.getElementById('btnBorraEp').style.display = 'none';
	}
	// Carga informacion del encabezado.
	queryFormPost("readAnexo1Encabezado", {
		async : false
	});

	if (operacion == 2) {
		$("#tblSelPresupuesto").hide();
	}
	if (operacion == 1 && $("#nIdEstatus").val() == "-1") {
		$("#tblSelPresupuesto").hide();
		parent.document.getElementById("pb_send").style.visibility = 'hidden';
		parent.document.getElementById("pb_send").disabled = true;

		parent.document.getElementById("pb_save").style.visibility = 'hidden';
		parent.document.getElementById("pb_save").disabled = true;
	}
	muestraOcultaDivMotivo();

	obtieneTotal();
	parent.document.getElementById("pb_send").style.visibility = 'hidden';
}

function guardaDatosGestion() {

	var p = window.parent;

	if (p.gestion.getFolio() == "") {
		p.gestion.setMensaje("");
		p.gestion.setFolio(folioTramite);
		p.gestion.setOperador(usuarioTramite);
		p.gestion.setFechaDocumento($("#fAplicacion").val());
		p.gestion.setEjercicioFiscal($("#cEjercicio").val());
		p.gestion.setConceptoMov("Solictud de recursos");
		p.gestion.setMoneda("MXP");
		p.gestion.setMensaje("");
	}

}

/**
 * Funcion del Workflow. Define las acciones a realizar al momento de guardar el
 * tramite. Se realizan validaciones referentes a la operacion y el tramite para
 * establecer si el tramite continua o si requiere hacer correcciones el
 * usuario.
 * 
 * @param id_oper
 *            Operacion actual
 * @returns {Boolean} true si todas las validaciones son correctas. Permite
 *          continuar con el tramite.
 */
function onSubmit(id_oper) {
	if (operacion == 1) {
		/*
		//VALIDACION PARA QUE SOLO SEA UNA SOL DE RECUROS X PAGAR POR UR AL MES
		if ($("#solGeneradasUR").val() != "0") {
			alert("Ya existe una solicitud de este tipo para tu UR, esta solicitud tendra que ser descartada...")
			parent.document.getElementById("pb_cancel").click();
			return false;
		}
		*/
		if ($("#nIdEstatus").val() == "-1") {
			return true;
		} else {
			guardaDatosGestion();
			var nretval = validarTexto();
			if (nretval == -1)
				return false;
			else {
				try {
					queryFormPost("updateEncabezadoAnexo1", {
						async : false
					});
					if (nretval == -1) {
						parent.document.getElementById("pb_send").style.visibility = 'hidden';
						parent.document.getElementById("pb_send").disabled = true;
						return false;
					} else {
						queryFormPost({
							queryName:"existeFirmanteAnexo1Read", 
							async : false, 
							callback:function(){
								if ($("#existeFirmante").val() == "NOEXISTE" ){
									$("#dialog-firmantes").dialog("open");
								}									
							}
						});
						parent.document.getElementById("pb_send").style.visibility = 'visible';
						parent.document.getElementById("pb_send").disabled = false;
						return true;
					}
				} catch (e) {
					alert(e);
				}
			}
		}
	} else if (operacion == 2) {
		var seleccionado = parseInt($("input[name='autorizarAnexo1']:checked")
				.val(), 10);

		if (seleccionado > 0) {
			$("#cMotivoRechazo").val("");
			capturaRechazo(false);

			if (confirm("¿Esta seguro de Aplicar esta solicitud?")) {
				if (!esDocumentoAplicado()) {
					if (procesar()) {
						parent.document.getElementById("pb_send").style.visibility = 'visible';
						parent.document.getElementById("pb_send").disabled = false;
						return true;
					} else
						return false;
				} else{
					parent.document.getElementById("pb_send").style.visibility = 'visible';
					parent.document.getElementById("pb_send").disabled = false;
					return true;
				}
			} else
				return false;

		} else {
			if ($("#cMotivoRechazo").val() == "") {
				alert("Debe Capturar el motivo del rechazo para continuar.");
				return false;
			} else {
				parent.document.getElementById("pb_send").style.visibility = 'visible';
				parent.document.getElementById("pb_send").disabled = false;
				return true;
			}
		}

	}

}

function onPostDisplay(id_oper) {
	return true;
}

function onPostSubmit(id_oper) {
	
	if(id_oper == 1 && solFirmada == 0){
		alert("Favor de adjuntar la Solicitud Firmada, Para continuar.");
		return false;										
	}else{
		var seleccionado = parseInt($("input[name='autorizarAnexo1']:checked")
				.val(), 10);
	
		if (id_oper == 2 && seleccionado == -1){
			if (confirm("Esta seguro de rechazar la solicitud?")) {
				var continuar = false;
				$("#nIdEstatus").val("-1");
				queryFormPost({
					queryName : "updateStatusAnexo1",
					async : false,
					callback : function() {
						$("#procesar").dialog("open");
						continuar = true;
					}
				});
				return continuar;
			} else {
				parent.document.getElementById("pb_send").style.visibility = 'hidden';
				return false;
			}
		}else{
			$("#procesar").dialog("open");
			parent.document.getElementById("pb_send").style.visibility = 'hidden';
			parent.document.getElementById("pb_save").style.visibility = 'hidden';
			parent.document.getElementById("pb_cancel").style.visibility = 'hidden';
			parent.document.getElementById("pb_leave").style.visibility = 'hidden';
			return true;
		}
	}
}

function ResponsableSiguiente(id_oper) {
	if (id_oper == 1)
		if ($("#nIdEstatus").val() == "-1")
			return "CONSULTA_ANEXO1";
		else
			return "AUTORIZA_ANEXO1";
	if (id_oper == 2) {
		var seleccionado = parseInt($("input[name='autorizarAnexo1']:checked")
				.val(), 10);
		if (seleccionado > 0)
			return "CONSULTA_ANEXO1";
		else
			return "VENTANILLA_ANEXO1";
	}
}

function OperacionSiguiente(id_oper) {
	if (id_oper == 1) {
		if ($("#nIdEstatus").val() == "-1")
			return "consulta_anexo1";
		else
			return "autoriza_anexo1";
	} else if (id_oper == 2) {
		var seleccionado = parseInt($("input[name='autorizarAnexo1']:checked")
				.val(), 10);
		if (seleccionado > 0)
			return "consulta_anexo1";
		else
			return "ventanilla_anexo1";
	}
}

function cssReadOnly() {
	$("[readOnly]").each(function() {
		$(this).addClass("notEditable");
	});
}

function muestraEPS() {
	if ($("#cConcepto").text() == "Escriba un concepto"
			|| $.trim($("#cConcepto").text()) == "") {
		alert("Favor de escribir un concepto antes de modificar el disponible.");
		return -1;
	} else {
		createDataTable();
		$("#dialog-EPS").dialog('option', 'modal', true).dialog('open');
		return 0;
	}
}

function validaCaracter(e) {
	tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return true;
	patron = /^([0-9A-Za-zÑñáéíóúÁÉÍÓÚ ]+)$/g;
	te = String.fromCharCode(tecla);
	return patron.test(te);
}

function validarTexto() {
	if ($("#cConcepto").text() == "Escriba un concepto"
			|| $.trim($("#cConcepto").text()) == "") {
		alert("Favor de escribir un concepto.");
		return -1;
	}
	var monto = $("#importeTotal").val();
	monto = monto.replace(/[$]/g, "");
	monto = monto.replace(/,/g, "");
	if (Number(monto) <= 0) {
		alert("Favor de seleccionar las EP requeridas o verificar el Monto de la Solicitud.");
		return -1;
	}
	return 0;
}

function createDataTable() {
	var uUser = $("#uLogin").val();
	var condicion = "";

	if (uUser.toUpperCase() != "ADMIN")
		condicion = " cUnidadEjecutora IN (SELECT UR FROM tVistasUR WITH (NOLOCK) WHERE modulo = 'TESORERIA' AND usuario = '"
				+ $("#uLogin").val() + "')";
	else
		condicion = " 1=1 ";

	condicion += " AND EP NOT IN( SELECT EP FROM tAnexo1Detalle WHERE nFolioAnexo1 = "
			+ $("#nFolioAnexo").val() + ")";
	oTableSeleccionEP = $("#tblEPS")
			.dataTable(
					{
						"bPaginate" : true,
						"iDisplayLength" : "25",
						"bLengthChange" : true,
						"bFilter" : true,
						"bSort" : true,
						"bInfo" : true,
						"bAutoWidth" : false,
						"sScrollY" : 300,
						"sScrollYInner" : "100%",
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers",
						"sScrollX" : "1024",
						"bScrollCollapse" : true,
						"bServerSide" : true,
						sAjaxSource : window.location.protocol + "//"
								+ window.location.host + "/"
								+ window.location.pathname.split("/")[1]
								+ "/crud?rt=t&ql=vSaldosAnualesAnexo1&qw="
								+ condicion,
						aoColumns : [ {
							sName : "id",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignCenter"
						}, {
							sName : "EP",
							bSearchable : true,
							bSortable : true,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoAnual",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoEnero",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoFebrero",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoMarzo",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoAbril",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoMayo",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoJunio",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoJulio",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoAgosto",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoSeptiembre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoOctubre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoNoviembre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "MontoDiciembre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						} ],
						oLanguage : {
							sProcessing : "Procesando...",
							sLengthMenu : "Mostrar _MENU_ registros",
							sZeroRecords : "No hay registros a mostrar",
							sEmptyTable : "No se encontraron resultados con esos filtros",
							sLoadingRecords : "Cargando...",
							sInfo : "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty : "Registro 0 al 0 de 0",
							sInfoFiltered : "(filtered from _MAX_ total entries)",
							sInfoPostFix : "",
							sInfoThousands : ",",
							sSearch : "Buscar:",
							oPaginate : {
								sFirst : "Primero",
								sPrevious : "Ant.",
								sNext : "Sigte.",
								sLast : "&Uacute;ltimo"
							}
						}
					});

}

function agregarClaves() {

	var vacio = $('#tblEPS input:checked').length == 0;
	var oTable2 = $("#dt_epAgregadas").dataTable();

	if (vacio) {
		alert("Favor de Selecccionar al menos una EP.");
		return false;
	}

	$("#btnAgregaClaves").attr("disabled", true);

	var oTableSeleccionEP = $('#tblEPS').dataTable();
	var epsAgrear = $("#tblEPS").dataTable().fnGetData();
	var epsAgregadas = oTable2.fnGetData();
	var arrayList = new Array();
	var i = 0;

	$('#tblEPS input:checked').each(
			function(idx, elm) {
				var paso = epsAgrear[oTableSeleccionEP.fnGetPosition($(this)
						.closest('tr')[0])];
				var agregaEP = true;
				var j = 0;
				while (j < epsAgregadas.length) {
					if (epsAgregadas[j][0] == paso[1]) {
						agregaEP = false;
					}
					j++;
				}
				if (agregaEP) {
					arrayList[i] = new Array(paso[1], paso[2], paso[3],
							paso[4], paso[5], paso[6], paso[7], paso[8],
							paso[9], paso[10], paso[11], paso[12], paso[13],
							paso[14]);
					i++;
				}
			});

	try {
		guardaSolicitud(arrayList);
		$("#dialog-EPS").dialog('close');
		document.getElementById("btnAgregaClaves").removeAttribute("disabled",
				false);
		$('#tblEPS').dataTable().fnClearTable();
		creaTablaEPs();
		obtieneTotal();
	} catch (e) {
		alert(e);
		document.getElementById("btnAgregaClaves").removeAttribute("disabled",
				false);
	}
}

function obtieneTotal() {
	var tListado = $("#dt_epAgregadas").dataTable().fnGetData();
	var monto = 0.00;
	for (i = 0; i < tListado.length; i++) {
		monto += Number((tListado[i][2]).replace(/,/g, ""));
	}
	$("#importeTotal").val(monto);
	queryFormPost("updateImporteEncabezadoAnexo1", {
		async : false
	});
	$("#importeTotal").formatCurrency();

	if (Number(monto) > 0)
		$("#divImprimePoliza").show();
	else
		$("#divImprimePoliza").hide();
}

function validaKeyPress(e) {
	tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return true;
	patron = /[.\d]/;
	te = String.fromCharCode(tecla);
	return patron.test(te);
}

function validaImporte(input) {
	$("#" + input.id).formatCurrency();

	var importeEditado = input.value;
	importeEditado = importeEditado.replace(/[$]/g, "");
	importeEditado = importeEditado.replace(/,/g, "");

	var importeDisponible = $("#monto" + (input.id).replace("Editar", ""))
			.val();
	importeDisponible = importeDisponible.replace(/[$]/g, "");
	importeDisponible = importeDisponible.replace(/,/g, "");

	if (Number(importeEditado) > Number(importeDisponible)) {
		alert("El importe editado sobrepasa el disponible mensual");
		importeEditado = Number(importeDisponible);
	}

	$("#" + input.id).val(importeEditado);

	var total = 0.00;
	total = Number($("#EneroEditar").val()) + Number($("#FebreroEditar").val())
			+ Number($("#MarzoEditar").val()) + Number($("#AbrilEditar").val())
			+ Number($("#MayoEditar").val()) + Number($("#JunioEditar").val())
			+ Number($("#JulioEditar").val())
			+ Number($("#AgostoEditar").val())
			+ Number($("#SeptiembreEditar").val())
			+ Number($("#OctubreEditar").val())
			+ Number($("#NoviembreEditar").val())
			+ Number($("#DiciembreEditar").val());

	$("#totalEditado").val(total);
	$("#totalEditado").formatCurrency();
}

function reiniciaImportes() {
	$("#EneroEditar").val("0.00");
	$("#FebreroEditar").val("0.00");
	$("#MarzoEditar").val("0.00");
	$("#AbrilEditar").val("0.00");
	$("#MayoEditar").val("0.00");
	$("#JunioEditar").val("0.00");
	$("#JulioEditar").val("0.00");
	$("#AgostoEditar").val("0.00");
	$("#SeptiembreEditar").val("0.00");
	$("#OctubreEditar").val("0.00");
	$("#NoviembreEditar").val("0.00");
	$("#DiciembreEditar").val("0.00");
	$("#totalEditado").val("$0.00");
}

function guardaSolicitud(informacion) {

	var afectados = -1;

	$("#importeTotal").val($("#importeTotal").val().replace(/[$]/g, ""));
	$("#importeTotal").val($("#importeTotal").val().replace(/,/g, ""));

	var informacionEnviar;

	if (!informacion)
		informacionEnviar = $("#dt_epAgregadas").dataTable().fnGetData();
	else
		informacionEnviar = informacion;

	var importe = 0.00;

	for ( var i = 0; i < informacionEnviar.length; i++) {
		for ( var j = 2; j <= 13; j++) {
			importe = Number((informacionEnviar[i][j]).replace(/,/g, ""));
			if (importe > 0) {
				createInput('anexo1', 'epDetalle', informacionEnviar[i][0]);
				createInput('anexo1', 'importeDetalle', importe);
				createInput('anexo1', 'mesDetalle', j - 1);
			}
		}
	}

	$("#accion").val("");
	$.ajax({
		type : "POST",
		url : "../Anexo1/crear",
		cache : false,
		async : false,
		data : $("#anexo1").serialize(),
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
			var exito = RS.success;
			if ("true" == exito) {
				var nInsertados = parseInt(RS.data_1.result, 10) - 1;
				alert("Se Guardaron exitosamente: " + nInsertados
						+ " Registros.");
				afectados = parseInt(nInsertados, 10);
			} else
				alert(RS.data_1.result);

			$("#importeTotal").formatCurrency();
			$("#esperar").hide();
			borraElementos();
		},
		error : function(xhr, textStatus, errorThrown) {
			alert(errorThrown);
			$("#importeTotal").formatCurrency();
			$("#esperar").hide();

			document.getElementById("btn_AplicarPDRG").disabled = false;
			document.getElementById("esperar").style.visibility = 'hidden';
			borraElementos();
		}
	});

	return afectados;
}

function createInput(form, name, value) {
	$('<input>').attr({
		type : 'hidden',
		name : name,
		value : value
	}).addClass('remove').appendTo('#' + form);
}

function borraElementos() {
	$('.remove').remove();
}

function cmdImprimir(elFormato) {
	var whereFolio = "&whereFolio=" + $("#caNoContrarrecibo").val();
	window.open("../admin/SeguridadCatalogos?" + "catalogo=CONTRARECIBO"
			+ "&accion=run" + "&rn=" + elFormato + ".jasper" + whereFolio,
			"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
}

function creaTablaEPs() {
	var condicionTbl1 = "nFolioAnexo1=" + $("#nFolioAnexo").val();

	oTable2 = $("#dt_epAgregadas")
			.dataTable(
					{
						"bPaginate" : false,
						"iDisplayLength" : "25",
						"bLengthChange" : true,
						"bFilter" : true,
						"bSort" : true,
						"bInfo" : true,
						"bAutoWidth" : false,
						"sScrollY" : 300,
						"sScrollYInner" : "100%",
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers",
						"sScrollX" : "1024",
						"bScrollCollapse" : true,
						"bServerSide" : true,
						sAjaxSource : window.location.protocol + "//"
								+ window.location.host + "/"
								+ window.location.pathname.split("/")[1]
								+ "/crud?rt=t&ql=vAnexo1Detalle&qw="
								+ condicionTbl1,
						"fnInitComplete" : function(settings, json) {
							obtieneTotal();
						},
						aoColumns : [ {
							sName : "id",
							bSearchable : true,
							bSortable : true,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "EP",
							bSearchable : true,
							bSortable : true,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mTotal",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mEnero",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mFebrero",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mMarzo",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mAbril",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mMayo",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mJunio",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mJulio",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mAgosto",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mSeptiembre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mOctubre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mNoviembre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						}, {
							sName : "mDiciembre",
							bSearchable : false,
							bSortable : false,
							bVisible : true,
							sClass : "alignLeft"
						} ],
						oLanguage : {
							sProcessing : "Procesando...",
							sLengthMenu : "Mostrar _MENU_ registros",
							sZeroRecords : "No hay registros a mostrar",
							sEmptyTable : "No se encontraron resultados con esos filtros",
							sLoadingRecords : "Cargando...",
							sInfo : "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty : "Registro 0 al 0 de 0",
							sInfoFiltered : "(filtered from _MAX_ total entries)",
							sInfoPostFix : "",
							sInfoThousands : ",",
							sSearch : "Buscar:",
							oPaginate : {
								sFirst : "Primero",
								sPrevious : "Ant.",
								sNext : "Sigte.",
								sLast : "&Uacute;ltimo"
							}
						}
					});

}

function capturaRechazo(enviaAlerta) {
	if (operacion != 1) {
		$("#cMotivoRechazoSnd").val($("#cMotivoRechazo").val());
		queryFormPost({
			queryName : "updateMotivoRechazo",
			async : false,
			callback : function() {
				if (enviaAlerta) {
					alert("Motivo actualizado exitosamente.");
				}
				muestraOcultaDivMotivo();
				$("#dlgCaptuarMotivo").dialog("close");
			}
		});
	} else {
		$("#dlgCaptuarMotivo").dialog("close");
	}
}

function muestraCapturaRechazo() {
	$("#dlgCaptuarMotivo").dialog("open");
}

function muestraOcultaDivMotivo() {
	if ($("#cMotivoRechazo").val() != "") {
		$("#mostrarMotivoDiv").show();
		if (operacion == 1) {
			muestraCapturaRechazo(true);
		}
	} else
		$("#mostrarMotivoDiv").hide();
}

function eliminaEP() {
	var epsBorrar = $("#dt_epAgregadas").dataTable().fnGetData();
	var renglon;
	$('#dt_epAgregadas input:checked').each(function(idx, elm) {
		renglon = epsBorrar[oTable2.fnGetPosition($(this).closest('tr')[0])];
		$("#epEditar").val(renglon[1]);
		queryFormPost("eliminaEPDetalleAnexo1", {
			async : false
		});
	});
	$('#dt_epAgregadas').dataTable().fnClearTable();
	creaTablaEPs();
	$("#accion").val("-1");
	$.ajax({
		type : "POST",
		url : "../Anexo1/crear",
		cache : false,
		async : false,
		data : $("#anexo1").serialize(),
		error : function(xhr, textStatus, errorThrown) {
			// alert("Advertencia: " + xhr.responseText + "\nEstatus: " +
			// textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
		}
	});
}

function procesar() {
	$("#dialog-Procesando").dialog("open");
	return breturnVal;
}

function closeDlgMovito() {
	if (operacion == 1 && $("#nIdEstatus").val() == "-1") {
		parent.document.getElementById("pb_save").disabled = false;
		parent.document.getElementById("pb_save").click();
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
	}
}

function esDocumentoAplicado() {
	var aplicado = false;
	$("#cDocumentoHAplicado").val("");

	queryFormPost({
		queryName : "anexo1AplicadoRead",
		async : false,
		callback : function() {
			aplicado = $("#cDocumentoHAplicado").val() == "S"
					|| $("#cDocumentoHAplicado").val() == "C";
		}
	});
	return aplicado;
}

function cancelaSolicitud(){
	queryFormPost("leeFolioApartadoAnexo1",{async : false});
	queryFormPost({
		queryName : "existeIntegradoAnexo1",
		async : false,
		callback : function() {
			if ($("#existeIntegrado").val() != "0"){
				alert("No se puede cancelar debido a que esta solicitud de Recursos ya fue integrada o ya se autorizo para ello.");
				return false;
			}else{
				$("#dialog-Cancela").dialog("open");
			}
		}
	});	
}

function showDivOficio(){
	var cmpName = "oficioDelegatorioUpdate";
	var divName = "oficioDelegatorioCapturaUpdate";
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}

function showDivOficioVoBo(){
	var cmpName = "oficioDeleVoBoUpdate";
	var divName = "oficioDelegatorioVoBoUpdate";
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}

function updateFirmantes(){
	queryFormPost("tPagoFirmanteDelagatorioActualizaRead", {async:false});
	queryFormPost("tPagoFirmanteDelegatorioActualizaVoBoRead", {async:false});
	queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async: false }); // voBo - autoriza
	queryFormPost("tPagoFirmanteDelagatorioRead", {async: false }); // Para los firmantes de oficio delegatorio en caso de que existan.
	queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {async: false }); // Para los firmantes de oficio delegatorio VoBo en caso de que existan.
	
	$("#dialog-firmantes").dialog("open");
}

