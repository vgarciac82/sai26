/**
 * 
 */
 var esFirmaElectronica;

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
	
function polizaCaja() {
	esFirmaElectronica = $("#autorizadoPorFielChk").attr("checked") ? true : false;

	if( esFirmaElectronica && id_oper == 1 ) {
		procesaFirmaElectronica();
	} else {
		var where = " and ce.nfoliocaja=" + nfoliocaja;
		window.open("../servlet/SeguridadCatalogosMateriales?"
			+ "catalogo=REPORTE"
			+ "&accion=run"
			+ "&rn=SolicitudPolizaCaja.jasper"
			+ "&formato=PDF"
			+ "&whereFolio=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
	}

}

function solicitudPolizaCaja() {
	esFirmaElectronica = $("#autorizadoPorFielChk").attr("checked") ? true : false;
	if( esFirmaElectronica ) {
		procesaFirmaElectronica();
	} else {
		var where = " and ce.nfoliocaja=" + nfoliocaja;
		window.open("../servlet/SeguridadCatalogosMateriales?"
			+ "catalogo=REPORTE"
			+ "&accion=run"
			+ "&rn=SolicitudPolizaCaja.jasper"
			+ "&formato=PDF"
			+ "&whereFolio=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
	}
}



function cargaCaja() {
	
	verificaTipoAutorizacion();

	var sOrder = "";
	var param = "";
	var zTabla = "R_CARGA_CAJA";
	//var groupFilter = "";
	var camposWhere = " where TE.nFoliocaja=" + $("#nfoliocaja").val() + " order by nDocRenglon ";

	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : zTabla,
		Campos : camposWhere,
		Param : param,
		Order : sOrder,
		MaxReg : "5",
		ajax : "true"
	}, function(j) {
		totalCuentas = j.length;

		for( var i = 0; i < j.length; i++ ) {
			loadTableDetail(j[ i ].Col5, j[ i ].Col6, j[ i ].Col7.replace("$", ""), j[ i ].Col8.replace("$", ""), j[ i ].Col9, j[ i ].Col10, j[ i ].Col11, j[ i ].Col12);
			if( $.trim(j[ i ].Col9) != "" )
				loadShowTable(j[ i ].Col5, j[ i ].Col7, j[ i ].Col9);
			if( $.trim(j[ i ].Col10) != "" )
				loadShowTable(j[ i ].Col5, j[ i ].Col7, j[ i ].Col10);
			if( $.trim(j[ i ].Col11) != "" )
				loadShowTable(j[ i ].Col5, j[ i ].Col7, j[ i ].Col11);
			if( $.trim(j[ i ].Col12) != "" )
				loadShowTable(j[ i ].Col5, j[ i ].Col7, j[ i ].Col12);
		}


		if( totalCuentas > 0 ) {
			$("#nctabBeneficiario").val($.trim(j[ 0 ].Col18));
			$("#Nombre_Cheque").val($.trim(j[ 0 ].Col19));
			$("#APaterno_Cheque").val($.trim(j[ 0 ].Col20));
			$("#AMaterno_Cheque").val($.trim(j[ 0 ].Col21));

		
			$("#cConcepto").val(j[ 0 ].Col1);
			if( $.trim(j[ 0 ].Col2) != "" ) {
				$("#btnRechazo").show();
				$("#motivoRechazo").val(j[ 0 ].Col2);
				//cambiar luego porque en captura cambia el motivo y no se puede volver a cargar
				$("#txtmotivoRechazo").val(j[ 0 ].Col2);
			}
			$("#ue").val(j[ 0 ].Col3);
			$("#nidgrupoevento").val(j[ 0 ].Col4);
			regEvto = totalCuentas;
			$("#guardar").attr("disabled", false);
			$("#enviar").attr("disabled", false);
			$("#cTipoPoliza").val(j[ 0 ].Col13);
			$("#mTotalLbl").text("$" + j[ 0 ].Col14);

			if( $.trim(j[ 0 ].Col15 != "") && Number(j[ 0 ].Col15) != 0 ) {
				fill_Devolucion(j[ 0 ].Col15, j[ 0 ].Col16, j[ 0 ].Col17, j[ 0 ].Col12);
				$("#NoCajaDevolucion").val(j[ 0 ].Col15);
				$("#registroDevolucion").show();
				$("#mMontoSolicitud").val(j[ 0 ].Col14);
			}
			
			if( $("#Evento").val() != "" ) {
				$("#tdEvento").hide();
			}	
		}
		//VERIFICA QUE SEA UN EVENTO DE LAUDOS PARA QUE PUEDA CAPTURARSE EL NOMBRE DEL BENEFICIARIO DEL CHEQUE
		if( $("#Evento").val() == "8_2_3" ) {
			if( id_oper == 2 || id_oper == 3 ) {
				$("#Datos_Cheque").hide();
			} else {
				$("#Datos_Cheque").show();
				$("#Nombre_Cheque").prop('readonly', true);
				$("#Nombre_Cheque").addClass('notEditable');
				$("#APaterno_Cheque").prop('readonly', true);
				$("#APaterno_Cheque").addClass('notEditable');
				$("#AMaterno_Cheque").prop('readonly', true);
				$("#AMaterno_Cheque").addClass('notEditable');
				$("#guardar").attr("disabled", false);
				$("#enviar").attr("disabled", false);
				$("#Edita_Datos_Cheque").show();
				$("#Agrega_Datos_Cheque").hide();

			}
		}
	});
}

function verificaTipoAutorizacion() {
	queryFormPost({
		queryName : "esAutFIEL_CAJA",
		async : false,
		callback : function() {
			if( $("#cEsFirmaElectronica").val() == "S" ) {
				$("#autorizadoPorFielChk").attr("checked", true);

			} else
				$("#autorizadoPorFielChk").attr("checked", false);
		}
	});

}

function init() {
	
	queryFormPost({
		queryName : "tipoAutorizacionRead",
		async:false,
		callback: function(){
			if( $("#TipoAutorizacion").val() == "N" ){
				muestraDivImprimePoliza = true;
			}
		}
	});
	
	$("#nfoliocaja").val(nfoliocaja);
	$("#numeroEmpleadoElabora").val(numeroEmpleadoElabora);
	//VGC Se agrega la creacion del expediente en el primer momento del tramite
	$.ajax({
		url : '../expediente/CreaExpediente',
		dataType : 'json',
		type : "POST",
		data : {
			"folio" : $("#nfoliocaja").val()
		},
		async : true,
		success : function(json) {
			var exito = json.success;

			if( exito != "true" ) {
				alert(json.data_1.result);
				throw json.data_1.result;
			} else {
				if( "" != json.data_1.result )
					$("#idGabinete").val(json.data_1.result);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});

	$("#autorizadoPorFielChk").click(function() {
		onclicAutorizadoPorFielChk(this);
	});

	$("#enviar").click(
		function(e) {
			enviarTramite(e);
		}
	);
}

function onclicAutorizadoPorFielChk(element) {
	if( $(element).attr("checked") )
		$("#cEsFirmaElectronica").val("S");
	else
		$("#cEsFirmaElectronica").val("N");
}

function procesaFirmaElectronica() {
	Swal.fire({
				  title: '¿Desea continuar?',
				  text: "Al dar clic en Aceptar el tramite se enviara para su autorizacion por firma electronica.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					 enviarTramite();
				  } 
				})
}


function enviarTramite(e) {
	
	$("#co_responsable").val(ResponsableSiguiente(id_oper));
	$("#id_oper").val(Id_OperacionSiguiente(id_oper));
	$("#cCentroContable").val(cCentroContable);

	if( id_oper == 1 ) {
		return avanzaDesdeCaptura();
	} else if( id_oper == 2 && !cont && $('input:radio[name=grpAutorizar]:checked').val() == "Si" ) {
		var mesAplicacion = parseInt($("#fechaAplicacion").val().split("-")[ 1 ], 10);
		var fecha = new Date();
		var mesActual = fecha.getMonth() + 1;
		foliosSA = new Array();
		montosSA = new Array();

		if( mesAplicacion == $("#mesAbierto").val() || mesActual == mesAplicacion ) {
			Swal.fire({
				  title: '¿Desea continuar?',
				  text: "Se aplicará el trámite.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
						 id_oper = 3;
						Id_OperacionSiguiente(id_oper);
		
						queryFormPost("esAntiguedadSaldo", { async : false});
		
						if( $("#esAntiguedadSI").val() == 1 ) {
							queryFormPost("tamanoSI", {
								async : false
							});
		
							if( $("#cantidadSA").val() > 1 ) {
								queryFormPost("arregloFoliosSA", {
									async : false
								});
								queryFormPost("arregloMontosSA", {
									async : false
								});
								foliosSA = $("#arrFoliosSA").val().split(",");
								montosSA = $("#arrMontosSA").val().split(",");
		
							} else if( $("#cantidadSA").val() == 1 ) {
								queryFormPost("unFolioSA", {
									async : false
								});
								queryFormPost("unMontoSA", {
									async : false
								});
								foliosSA.push($("#arrFoliosSA").val());
								montosSA.push($("#arrMontosSA").val());
		
							} else if( $("#cantidadSA").val() == 0 ) {
								Swal.fire("Error","No existe detalle para el FolioCaja: " + $("#nfoliocaja").val(),"error");
								return false;
							}
		
						}
						
						queryFormPost("remanenteCajaRead", {async : false});
						queryFormPost("consultaModuloViaticos", {async: false});
						
						//ARLA si es una devolucion de viaticos valida el remantente antes de aplicar contablemente
						if( $("#Evento").val() == "8_2_2" ) {
							if( Number( $("#mMontoRemanente").val() ) < Number( $("#mMontoSolicitud").val()) ){
								Swal.fire("Verifique","El monto de la devolucion supera el remanente del anticipo.","warning");
								return false;
							}
						}
												
						//ARLA validar el remanente si es 0 se elimina el empleado de la tabla tEmpleadosCajaChica
						if( $("#Evento").val() == "8_1_2" ) {								
							queryFormPost("remanenteCajaRead", {
								async : false
							});
	
							if( $("#mMontoRemanente").val() == 0 )
								queryFormPost("tEmpleadosCajaChicaDelete", {
									async : false
								});
						} else if( $("#Evento").val() == "35_2_1_A" || $("#Evento").val() == "35_1_3" ) {
							queryFormPost("readmMontoSolicitud", {
								async : false
							});
							$("#mMontoRemanenteComprobacion").val(( $("#mMontoSolicitud").val() ));
							queryFormPost("montoRemanenteComprobacionUpdate", {
								async : false
							}); 
						}
		
						//ARLA insertar RFC y nombre del empleado cuando se autoriza solicitud con evento 8_1_1 REGISTRO DE LA CONSTITUCIÓN DEL FONDO FIJO DE CAJA
						if( $("#Evento").val() == "8_1_1" ) {
							queryFormPost("tEmpleadosCajaChicaRead", {
								async : false
							});
							if( $("#existeCajaChica").val() == 0 ) {
								queryFormPost("tEmpleadosCajaChicaCreate", {
									async : false
								});
							}
						}
							
						procesar(); //aplica motor contable
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					 	if( !cont )
						return false;
				  }
				})

		} else {
			Swal.fire("No se puede aplicar el trámite","La fecha de aplicacion no corresponde al mes abierto (" + nombreMes($("#mesAbierto").val()) + ") o el mes actual (" + nombreMes(mesActual) + ") .                   Favor de notificar al área correspondiente.","info");
		}
	} else {
		
		if (!cont ){
			Swal.fire({
				  title: '¿Desea continuar?',
				  text: "Se rechazará el trámite.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					 	$("#CausaRegreso").dialog("open");
						if( $("#Evento").val() == "35_2_1_A" || $("#Evento").val() == "35_1_3" ) {
							queryFormPost("tEstadoDeCuentaComprobacionesEncabezadoDelete", {
								async : false
							}); 
						}
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  return false;
				  }
				})
		} else 
			return false;
	}

	$("#pb_save", parent.window.document).click();
}

function avanzaDesdeCaptura() {
	
	if ($.trim($("#cEvento").val())=="8_2_1") {
		agregaComision();
	}
	
	esFirmaElectronica = $("#autorizadoPorFielChk").attr("checked") ? true : false;

	if( !esFirmaElectronica ) {
		$("#paginas").val("0");

		queryFormPost("readPaginasSNP", {
			async : false
		});

		if( parseInt($("#paginas").val(), 10) <= 0 ) {
			Swal.fire("Adjuntar","Para continuar debe digitalizar la solicitud firmada en la pestaña de adjuntos","info");
			return false;
		}
	}else{
		$("#id_oper").val("1")
		$("#co_responsable").val( "CAPTURA_CAJA" );
	}

	
	if( esFirmaElectronica ) {
		
		actualizaDatosAutoriza();

	} else {
		Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se enviará el tramite a estatus de autorización",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  actualizaDatosAutoriza();
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					 return false;
				  }
				})	
	}
	

}

function actualizaDatosAutoriza(){
	$("#momentoGuarda").val(esFirmaElectronica? 1 : 2);
		
		queryFormPost("UpdateCoperCaja",
			{
				async : false,
				callback : function() {
					if( $("#Evento").val() == "35_2_1_A" || $("#Evento").val() == "35_1_3" ) {
						queryFormPost("readmMontoSolicitud", {
							async : false
						});
						queryFormPost("tEstadoDeCuentaComprobacionesEncabezadoCreate", {
							async : false,
							callback : function() {
								queryFormPost("tCajaEncabezadoUpdateComprobacion", { async : false });
							}
						});
					} else
						update = true;
				}
			});


		if( !esFirmaElectronica ) {
			document.liberardocumento.submit();
		} else {
			var exito = false;
			$.ajax({
				beforeSend : function() {
					$.blockUI({
						message : "Enviando. Espere ..."
					});
				},
				complete : function() {
					$.unblockUI();
				},
				type : "POST",
				dataType : 'json',
				url : "..//FIEL/solicitaFirmaElectronica",
				cache : false,
				async : false,
				data : $("#FormContrato").serialize(),
				error : function(xhr, textStatus, errorThrown) {
					$.unblockUI();
					alert("No se pudo completar la operacion debido al error: " + errorThrown);
				},
				success : function(RS) {
					$.unblockUI();
					var success = RS.success;
					if( "true" == success ){
						alert( RS.data_1.result );
						parent.frmLeave.submit();
					}else{
						alert( RS.data_1.result );
					}
				}
			});
		}
		
		return exito;
}