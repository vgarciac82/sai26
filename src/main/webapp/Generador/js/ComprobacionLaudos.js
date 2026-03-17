var relaciones;
var numeroEmpleado = "<%=numeroEmpleado%>";
var permitePagoSinFIEL = true;

/**
 * Funcion de inicio.
 */
function init() {
	muestraRadicado();
	/*
	querySelectPost("catTipoSuplenciaRead", "tipoSuplencia", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBoUpdate", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaUpdate", {
		async : false
	});
	*/
	$("#oficioDelegatorioCaptura").hide();
	$("#oficioDelegatorioVoBo").hide();
	

	$("#dialog-Procesando").dialog(
		{
			autoOpen : false,
			height : 400,
			width : 400,
			modal : true,
			async : false,
			open : function() {
				var tipo = "aplicarMotor";
				var caNoContrarrecibo = $("#caNoContrarrecibo").val();
				var campo = $("#campo").val();
				var tablaEnc = $("#tablaEnc").val();
				var campoCondicion = $("#campoCondicion").val();
				var tablaDet = $("#tablaDet").val();
				var tipoAplicar = $("#tipoAplicar").val();
				
				var numEmpleadoCaptura = $("#numeroEmpleado").val();
				var numEmpleadoVoBo = $("#cboVoBo").val();
				var numEmpleadoAut = $("#cboAutoriza").val();
				
				var autorizadoPorFiel = $("#autorizadoPorFiel").val();

				$("#divEsperaProcesando").attr("style", "visibility=visible");
				var fAppActualizada = actualizaMesAplicacion();

				if( !fAppActualizada ) {
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
						tipoAplicar : tipoAplicar,
						empleadoCaptura : numEmpleadoCaptura,
						empleadoVoBo : numEmpleadoVoBo,
						empleadoAutoriza : numEmpleadoAut,
						autorizadoPorFiel : autorizadoPorFiel
					},
					async : false,
					success : function(data) {
						if( data.sinSesion == 'sinSesion' ) {
							location.href = "../index.jsp";
						}
						if( data.estatus == "guardado" ) {

							if( $("#id_oper").val() == 1 ) {
								if( bCOMSOC ) {
									creaCasoComsoc($("#cDocumento").val(), $("#id_caso").val());
								}

								retencionesCambio();		
								var miCheckbox = document.getElementById('Si_ISSSTE');	
								if(!miCheckbox.checked){
									$("#dRFCCuotas").val("");
								}else {
									$("#dRFCCuotas").val($("#dRFC_Cuotas").val());
								}	
								queryFormPost({
									queryName : "tComprobacionLaudosCreate",
									async : false,
									callback : function() {
										Swal.fire({ icon: 'success',
													text: "El documento se avanzo a Autorización: " + $("#caNoContrarrecibo").val() });
										parent.document.getElementById("pb_send").disabled = false;
										parent.execOperacion();
										parent.execResponsable();
										parent.document.getElementById("pb_send").click();
									}
								});							
							} else {
								$("#docAplicado").val("S") ;
								if(autorizadoPorFiel == "false"){									
									cmdImprimir('PolizaPago');
								}
								Swal.fire({ icon: 'success',
											text: "Documento Aplicado Correctamente: " + $("#caNoContrarrecibo").val() });

								queryFormPost("updateCampoFacturaDC", {
									async : false
								}); //URVP.21012015 se actualiza el campo de factura a la tDocumentacionComprobatoriaDet

								parent.document.getElementById("pb_send").disabled = false;
								parent.execOperacion();
								parent.execResponsable();
								parent.document.getElementById("pb_send").click();

							}
							$("#cIdDocumento").val($("#cIdRelacion").val());
							$("#cMotivoRechazoCRUD").val("");
							$("#nIdEstado").val("4");

							queryFormPost("mRelaciongastosUpdate", {
								async : false
							});

							breturnVal = true;
						} else {
							breturnVal = false;
							Swal.fire({ icon: 'success',
										text: "El Documento No Se Aplico: " + $("#caNoContrarrecibo").val() + " - " + data.estatus });
						}
						$("#divEsperaProcesando").attr("style", "visibility=hidden");
						$("#dialog-Procesando").dialog("close");
					}
				});
			},
			close : function() {}
		});
	$("#DialogSolCaja").dialog({
		autoOpen : false,
		height : 550,
		width : 1100,
		modal : true,
		buttons : {
			"Cancelar" : function() {
				if( confirm("Desea descartar la Comprobación Laudos") )
					$("#esperar").dialog("open");
			}
		}
	});
	// DataTable Movimientos (EP's Agregadas)
	$('#grdMovimientos').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : true,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers"
		});
	
	$('#grdCompromisos').dataTable({
		"iDisplayLength" : 20,
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"sScrollY" : 100,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers"
	});

	// DataTable con las Solicitudes
	tablaSolicitudesCaja = $('#tblSolicitudesB').dataTable({
		"bPaginate" : true,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bAutoWidth" : true,
		"sScrollY" : 270,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		oLanguage : {
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
		}
	});

	$("#tblSolicitudesB tbody").dblclick(function(event) {
		doubleClickSolicitudCaja(event);
	});

	// Dialogo Esperar para dar a entender al usuario que se esta
	// cargando el caso.
	$("#esperar").dialog({
		autoOpen : false,
		height : 150,
		width : 200,
		modal : true,
		close : function() {}
	});
	
	$("#dialog-form").dialog({
		autoOpen : false,
		height : 400,
		width : 800,
		modal : true,
		beforeClose : function(event, ui) {
			return bClicBtn;
		}
	});


	// Pestañas
	$("#tabs").tabs(
		{
			"show" : function(event, ui) {
				var oTable = $('div.dataTables_scrollBody>table.display',
					ui.panel).dataTable();
				if( oTable.length > 0 ) {
					oTable.fnAdjustColumnSizing();
				}
			}
		});
	
	$('#grdFacturas').dataTable({
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers"
			
		});
		cat_INGF();
		
		$("#divPagoISSSTE").hide();
		$("#nSiISSSTE").val("N");

}
// -------------Fin del READY

/*
 * -----------Funciones de Caso -------------- 
 * ------------------------ INICIO------------------------------
 */
function onLoadPlantilla(id_oper) { // Carga Plantilla
	var paso = parseInt($("#numPaso").val(), 10);
	$("#id_oper").val(id_oper);	
	//Obtener Ejericio Fiscal
	queryFormPost("tEjercicioRead", {
		async : false
	});
	
	$("#cEjercicio").val($("#aEjercicioFiscal").val());
	$("#cIdDocumento").val($("#cIdRelacion").val());
	$("#cUnidadResponsable").val(cURUsuario);
	$("#divImprimePoliza").hide();

	//alert($("#numPaso").val());
	switch( parseInt(id_oper) ) {
		case 1 :
			if( $("#nfolioCaja").val() == '' ) {
				OpenDialogSolicitudes();
				document.getElementById("chk_retenciones").checked = true;
				retencionesCambio();	
				$("#numPaso").val("1");
			} else {
				sumaImpuestos();
			}
			$(".pasoDos").hide();
			$(".pasoTres").hide();
			$(".pasoULTIMO").hide();
			break;
		case 2 : 
			queryFormPost("tRelacionGastoEncabezadoRead", {
				async : false
			});
			
			queryFormPost("tComprobacionLaudos_IngresoRead", {
				async : false
			});
			
			queryFormPost("tComprobacionLaudosRead", {
				async : false
			});
			
			if($("#nSiISSSTE").val() == "S"){				
				document.getElementById("Si_ISSSTE").checked = true;	
				//$("#divPagoISSSTE").show();
			}
			
			$("#nIdIntegracion").attr('disabled', true);
			$("#dRFC_Cuotas").attr('disabled', true);			
			document.getElementById("Si_ISSSTE").disabled = true;
			parent.document.getElementById("pb_cancel").disabled = true;
			document.getElementById("cIdRelacion").readOnly = true;
			document.getElementById("mImporteISRLaudosTotal").readOnly = true;
			document.getElementById("cConcepto").readOnly = true;
			document.getElementById("chk_retenciones").disabled = true;
			document.getElementById("botonCambia").disabled = true;
			document.getElementById("btnBeneficiario").disabled = true;
			$("#agrega2").attr('disabled', true);
			$("#Limpia").attr('disabled', true);
			$("#nIdClaveEgresos2").attr('disabled', true);
			$("#Agregar").attr('disabled', true);
			queryFormPost("tComprobacionLaudosEsDevengadoRead", {
				async : false
			});
			queryFormPost("tComprobacionLaudosEsLiquidacionRead", {
				async : false
			});
			if( $("#cEsDevengado").val() == "S" || $("#cEsLiquidacion").val() == "S" ) {
				$("#solicitudAnticipadaDiv").css("display", "none");
			}
			Swal.fire({ icon: 'info',
						text: "Dar clic sobre el boton Guardar para Autorizar" });
			break;
		case 3 :
			queryFormPost("tRelacionGastoEncabezadoRead", {
				async : false
			});
			
			queryFormPost("tComprobacionLaudosRead", {
				async : false
			});
			
			if($("#nSiISSSTE").val() == "S"){				
				document.getElementById("Si_ISSSTE").checked = true;
				$("#divPagoISSSTE").show();	
			}
			
			$("#nIdIntegracion").attr('disabled', true);
			$("#dRFC_Cuotas").attr('disabled', true);			
			document.getElementById("Si_ISSSTE").disabled = true;
			document.getElementById("cIdRelacion").readOnly = true;
			document.getElementById("mImporteISRLaudosTotal").readOnly = true;
			document.getElementById("cConcepto").readOnly = true;
			document.getElementById("chk_retenciones").disabled = true;
			document.getElementById("botonCambia").disabled = true;
			document.getElementById("btnBeneficiario").disabled = true;
			$("#agrega2").attr('disabled', true);
			$("#Limpia").attr('disabled', true);
			$("#nIdClaveEgresos2").attr('disabled', true);
			$("#Agregar").attr('disabled', true);
			$("#divImprimePoliza").show();
			$("#EditaFirmas").css('visibility', 'visible');
			queryFormPost("tComprobacionLaudosEsDevengadoRead", {
				async : false
			});
			queryFormPost("tComprobacionLaudosEsLiquidacionRead", {
				async : false
			});
			if( $("#cEsDevengado").val() == "S" || $("#cEsLiquidacion").val() == "S" ) {
				$("#solicitudAnticipadaDiv").css("display", "none");
			}
			break;
	}

} // fin onLoadPlantilla

function onSubmit(id_oper) { // clic boton Guardar
	var paso = parseInt($("#numPaso").val(), 10);
	var valido = false;

	var laudodev = document.getElementById("chkLaudoDevengado").checked;
	var liqdev = document.getElementById("chkLiquidacionDevengado").checked;

	switch( parseInt(id_oper) ) {
		case 1 : //Captura
			switch( paso ) {
				case 1 : 
						
					var p = window.parent;
					var resultado = false;
					if( !laudodev && !liqdev ) {
						$("#cTipoPoliza").val("DI");
					} else {
						$("#cTipoPoliza").val("EG");
					}
					p.gestion.setFolio($("#FOLIO").val());
					p.gestion.setOperador(operador);
					p.gestion.setFechaDocumento($("#fAplicacion").val());
					p.gestion.setEjercicioFiscal($("#aEjercicioFiscal").val());
					p.gestion.setMoneda("MXP");
					//alert($("#numPaso").val());
					if( validaCampos(paso) ) {
						getNextSequenceVal({
							seqName : "APARTADO",
							async : false,
							callback : setSequenceAptd
						});
						queryFormPost("checaComprobacion", {
							async : false
						});
						if( $("#existe").val() == 'EXISTE' ) {
							queryFormPost("tRELACIONGASTOSEncabezadoDelete", {
								async : false
							});
							queryFormPost("tRELACIONGASTOSDetalleDelete", {
								async : false
							});
						}
						queryFormPost(
							{
								queryName : "tRelacionGastosEncabezadoCreate",
								async : false,
								callback : function() {
									$(".pasoDos").show();
									$("#L02").click();
									$("#numPaso").val("2");
									document.getElementById("cIdRelacion").readOnly = true;
									document.getElementById("cIdRelacion").className = "notEditable";
									document.getElementById("mImporteISRLaudosTotal").readOnly = true;
									document.getElementById("mImporteISRLaudosTotal").className = "notEditable";
									document.getElementById("cConcepto").readOnly = true;
									document.getElementById("cConcepto").className = "notEditable";
									document.getElementById("chk_retenciones").disabled = true;
									document.getElementById("botonCambia").disabled = true;
									document.getElementById("btnBeneficiario").disabled = true;
									querySelectPost("CatalogoObraTConceptoRead", "tConcepto", {
										async : false
									});
									parent.document.getElementById("pb_save").disabled = true;
									$("#totalsolicitud").val($("#mImporteBruto").val());
									valido = true;
								}
							}
						);
					
						querySelectPost("CatalogoRGTMovimendoRead", "TIPO_MOVIMIENTO", {
							async : false
						});
						
						if($("#nIdIntegracion").val() != ''){
							queryFormPost("tRELACIONGASTOSIngresoCreate", {
								async : false
							});
						}
					}						
					break;
				case 2 : //Documentacion
					cmdGuardar();
					break;
			}
			break;
		case 2 : //Autoriza
			$("#campo").val("nFolio" + $("#cDocumento").val());
			$("#tablaEnc").val("t" + $("#cDocumento").val() + "Encabezado");
			$("#campoCondicion").val("caNoContrarrecibo");
			$("#tablaDet").val("t" + $("#cDocumento").val() + "Detalle");
			$("#tipoAplicar").val($("#cDocumento").val());
			$("#Fecha_Pago").val($("#fAplicacion").val());
			$("#elcontraTemp").val($("#caNoContrarrecibo").val());
			if( $("#elcontraTemp").val().substring(0, 4) != $("#cCentroContable").val() + $("#cxpPrefijo").val() ) {
				getNextSequenceVal({
					seqName : "CR-" + $("#cCentroContable").val(),
					async : false,
					callback : setSequenceVal
				});
			}
			$("#elcontra").val($("#caNoContrarrecibo").val());
			queryFormPost("tRelacionGtosEncFPagoUpdate,tContraReciboFPagoUpdate,tDocCompFPagoUpdate", {
				async : false
			});
			tipoFirmantes();
			break;


	} // fin switch

	return valido;
} // fin onSubmit

function ResponsableSiguiente(id_oper) {
	switch( parseInt(id_oper) ) {
		case 1 :
			return "AUTORIZA_COMPLAUDOS";
			break;
		case 2 :
			return "CONSULTA_COMPLAUDOS";
			break;
	} // fin switch
} // fin ResponsableSiguiente

function OperacionSiguiente(id_oper) {
	switch( parseInt(id_oper) ) {
		case 1 :
			return "autoriza_complaudos";
			break;
		case 2 :
			return "consulta_complaudos";
			break;
	} // fin switch
} // fin OperacionSiguiente

function onPostDisplay(id_oper) {
} // fin onPostDisplay

function onPostSubmit(id_oper) {
	$("#esperar").dialog("open");
	return true; // clic boton enviar
} // fin onPostSubmit

// -------------Funciones de Caso---FIN
// --------------------------------------------------------------------------------------------------------

/**
 * Funcion para mostrar Dialogo de SNP
 */
function OpenDialogSolicitudes() {
	cargaSolicitudes();
	setTimeout('tablaSolicitudesCaja.fnAdjustColumnSizing()', 2000);
	setTimeout('$("#DialogSolCaja").dialog("open")', 1000);
	document.getElementById("chkLaudoDevengado").checked = false;
	laudoDevengadoClick();
	document.getElementById("chkLiquidacionDevengado").checked = false;
	liquidacionDevengadoClick();
}

/**
 * Funcion para Cargar Datos a la tabla
 */
function cargaSolicitudes() {
	var sOrder = "";
	var param = "";
	var zTabla = "R_CARGA_COMPLAU";
	var groupFilter = "";
	var camposWhere = " order by nFolioCaja";
	tablaSolicitudesCaja.fnClearTable();
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : zTabla,
		Campos : camposWhere,
		Param : param,
		Order : sOrder,
		MaxReg : "5",
		ajax : "true"
	}, function(j) {
		for( var i = 0; i < j.length; i++ ) {
			tablaSolicitudesCaja.fnAddData([ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2,
				j[ i ].Col3, j[ i ].Col4, j[ i ].Col5, j[ i ].Col6 ]);
		}
	});
}

/**
 * Funcion para cargar datos de SNP en la CompLaudos
 */
function doubleClickSolicitudCaja(event) {
	var aPos = tablaSolicitudesCaja.fnGetPosition(event.target.parentNode);
	var aData = tablaSolicitudesCaja.fnGetData(aPos);
	var nfolioCaja = aData[ 0 ];
	var cDescripcionCaja = aData[ 1 ];
	var montoSol = aData[ 2 ];
	var tipoComprobacion = aData[ 3 ];
	$("#nfolioCaja").val(nfolioCaja);
	$("#cDescripcionCaja").val(cDescripcionCaja);
	$("#montoSol").val('$ ' + Number(montoSol).toFixed(2));
	$("#mImporteNeto").val('$ ' + Number(montoSol).toFixed(2));
	$("#tipoComprobacion").val(tipoComprobacion);
	$("#totalsolicitud").val('$ ' + Number(montoSol).toFixed(2));
	$("#DialogSolCaja").dialog("close");	
	if ($("#tipoComprobacion").val() == "Anticipo Liquidacion"){
		$("#cTipoRfc").val("3");
		$("#ID_DESTINO_GASTO").val("CQRE");
		$("#idDestinoGasto").val("CQRE");
		$("#cEsDevengado").val("N");
		$("#cEsLiquidacion").val("S");
		$("#cPasivo").val("");
	}
	else 
		$("#cTipoRfc").val("6");
	
	sumaImpuestos();
	actualizaFolioCaja();
}

/**
 * Funcion que calcula el Importe Neto
 */
function sumaImpuestos() {
	var montoSol,
		importeNeto,
		isr;

	montoSol = Sinfrmt($("#mImporteNeto").val());
	isr = Sinfrmt($("#mImporteISRLaudosTotal").val());
	importeNeto = parseFloat(montoSol) + parseFloat(isr);

	$("#mImporteBruto").val('$ ' + Number(importeNeto).toFixed(2));
	$("#mImporteISRLaudosTotal").val('$ ' + Number(isr).toFixed(2));

	/*moneyFrmt("montoSol");
	moneyFrmt("mImporteNeto");
	moneyFrmt("mImporteISRLaudosTotal");
	moneyFrmt("mImporteBruto");*/
}

/**
 * Funcion para cargar Ventana con el Catalogo Beneficiarios
 */
function cat_beneficiario() {
	window.open('CatalogoBeneficiariosRG.jsp?formName=mainFrm&inputRFCTarget=cIdRFC_RelacionGasto&inputDRFCTarget=cnombre', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
}

/**
 * Funcion para cargar Ventana con los registros de ingresos fiscales
 */
function cat_INGF() {
	querySelectPost("CatalogoR_INGFRead", "nIdIntegracion", {
		async : false
	});	
}

/**
 * Funcion da formato de Moneda al campo recibe id de campo.
 */
function moneyFrmt(id) {
	try {
		$("#" + id).formatCurrency();
	} catch( ex ) {
		Swal.fire({ icon: 'error',
					text: "Error 0001js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });		
	}
}

/**
 * Funcion quita formato de Moneda
 * para operaciones
 * recibe fload.
 */
function Sinfrmt(fld) {
	fld = fld.replace("$", "");
	fld = fld.replace(/,/g, "");
	if( fld == "" )
		fld = 0;
	return fld;
}



/**
 * Funcion donde se encuentran los campos que no deben ir vacios
 * recibe #paso para saber en que estatus se encuentra el caso
 * 
 */
function validaCampos(paso) {
	var esValido = true;
	switch( paso ) {
		case 1 :
			if( $("#cEsDevengado").val == "S" || $("#cEsLiquidacion").val == "S" ) {
				esValido = esValido && esRequerido("nfolioCaja", "Folio de Caja");
				esValido = esValido && esRequerido("montoSol", "Monto de la Solicitud de Caja");
			}
			esValido = esValido && esRequerido("cIdRelacion", "Folio de la Relacion");
			esValido = esValido && esRequerido("cIdRFC_RelacionGasto", "RFC");
			esValido = esValido && esRequerido("cnombre", "Nombre");
			esValido = esValido && esRequerido("mImporteNeto", "Importe Neto");
			esValido = esValido && esRequerido("mImporteISRLaudosTotal", "Monto ISR");
			esValido = esValido && esRequerido("cConcepto", "Concepto");
			return esValido;
			break;
	}
}

/**
 * Funcion para validar si un campo es requerido (si no esta vacio)
 * recibe id de campo y mensaje a mostrar (nombre o descripcion del campo)
 */
function esRequerido(id, n) {
	var sTemp = $("#" + id).val();
	if( sTemp.length == 0 ) {
		$("#" + id).addClass("ui-state-error");
		updateTipsDlg(n + " es un dato requerido.");
		parent.document.getElementById("pb_send").disabled = true;
		$("#" + id).focus();
		return false;
	} else {
		$("#" + id).removeClass("ui-state-error");
		$("#" + id).focus();
		return true;
	}
}

/**
 * Funcion que manda mensaje de error
 * recibe texto a mostrar
 */
function updateTipsDlg(t) {
	tips
		.text(t);	
	Swal.fire({ icon: 'error',
				text: t });
}

/**
 * Funcion que permite solo numeros en campo.
 */
function onlyFloat(evt) {
	var keyPressed = ( evt.which ) ? evt.which : event.keyCode;
	var strCheck = '0123456789.';
	var key = String.fromCharCode(keyPressed);
	if( strCheck.indexOf(key) == -1 )
		return false;
	return true;
}

/**
 * Funcion cambio en check de retenciones.
 */
function retencionesCambio() {
	if( $("#chk_retenciones").prop("checked") ) {
		$("#cRetSICOP").val("S");
	} else {
		$("#cRetSICOP").val("N");
	}
}

/**
 * Funcion cambio Tipo Movimiento
 * al seleccionar Tipo Concepto
 */
function concepto() {
	$("#TIPO_CONCEPTO").val($("#tConcepto").val());
	querySelectPost("CatalogoRGTMovimendoRead", "TIPO_MOVIMIENTO", {
		async : false
	});
}
/**
 * Funcion Limpia EP's
 * Borra Detalle de BD
 */
function LimpiaEP() {
	document.getElementById("tConcepto").removeAttribute("disabled", false);
	$("#acumuladoOperacion").val(0);
	queryFormPost("TRELGASTOSdetallexFolioDelete,tPagoApartadoDetxFolioDelete", {
		async : false
	});
	$('#grdMovimientos').dataTable().fnClearTable();
}
/**
 * Funcion Carga EP´s
 * $('#ID_DESTINO_GASTO').val(); 
		var tconcepto = $('#TIPO_CONCEPTO').val(); 
		var tipodocto = $('#TO_TIPO_DOCTO').val(); 
		var cunidadej = $('#cUnidadEjecutora').val();
 */
function Grid() {
	var fFinanciamiento = $("#cTipoFuente").val();

	/*Presupuesto Radicado Validacion Ayuda*/
	if( $("#cEsRadicado").val() == "S" ) {
		if( relaciones )
			relaciones.length = 0;
		window.open("AyudasEPRadicadoRG.jsp?formName=mainFrm", 'AyudaEPsPagos', 'status=1, width=900px, height=780px, left=100px, scrollbars=1');
	} else {
		window.open('AyudaEPsPagos.jsp?formName=mainFrm&ID_DESTINO_GASTO=ID_DESTINO_GASTO&TIPO_CONCEPTO=TIPO_CONCEPTO&TO_TIPO_DOCTO=TO_TIPO_DOCTO&cUnidadEjecutora=cUnidadEjecutora&cRadicado=0&fuenteFinancimiento=' + fFinanciamiento, 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	}
}

function FiltroMovimientos() {
	$("#mMovimiento").val(Sinfrmt($("#mMovimiento").val()));
	$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
	$("#mImporteNetoEP").val($("#mMovimiento").val());
	$("#cIdRFC_RelacionGasto").val();
	var subCuenta = "";
	if( $("#cTipoRfc").val() == "6" ) {
		queryFormPost("readPCContingente", {
			async : false
		});
		subCuenta = $("#cPasivo").val();
	}
	
	if ($("#tipoComprobacion").val() == "Anticipo Liquidacion"){
		$("#DESTINO_GASTO").val("CQRE");
		$("#cPasivo").val("");
	}
		
	if( $("#EP").val() == "" ) {
		Swal.fire({ icon: 'warning',
					text: "Favor de Seleccionar la EP" });
		return;
	}
	if( $("#mMovimiento").val() == ""
		|| Sinfrmt($("#mMovimiento").val()) == 0.00 ) {		
		Swal.fire({ icon: 'warning',
					text: "Favor de Introducir el importe" });
		return;
	}
	var monto = $("#mMovimiento").val();
	if( parseInt(monto.indexOf("."), 10) != parseInt(monto.lastIndexOf("."), 10) ) { // URVP.16062014-Validacion de mas de un punto decimal
		Swal.fire({ icon: 'warning',
					text: "No se permiten dos puntos decimales, favor de introducir el importe correcto." });		
		$("#mMovimiento").val("0");
		return;
	}

	$("#cCtaBanc").val("N/A");
	$("#dCtaBanc").val("N/A");

	var hayError = "";
	var vOGT = $("#EP").val();
	vOGT = vOGT.substring(31, 36);
	$("#vOGT").val(vOGT);

	var checa = true;
	var validN;
	var validM;
	var valid;
	var redondeo;
	var redondeo2;
	validM = Number(Sinfrmt($("#mMovimiento").val())).toFixed(2);
	validN = Number(Sinfrmt($("#mImporteBruto").val())).toFixed(2);

	var fechaDia = new Date();
	const añoActual = fechaDia.getFullYear();

	var mes = [ "MontoEnero", "MontoFebrero", "MontoMarzo", "MontoAbril",
		"MontoMayo", "MontoJunio", "MontoJulio", "MontoAgosto",
		"MontoSeptiembre", "MontoOctubre", "MontoNoviembre",
		"MontoDiciembre" ];

	var fecha = 0; //parseInt($("#fRecepcion").val().split("/")[ 1 ], 10);

	if( $("#aEjercicioFiscal").val() == añoActual )
		fecha = parseInt($("#fRecepcion").val().split("/")[ 1 ], 10);
	else
		fecha = 12;

	var elMonto = " where " + " cSubCuenta='" + $("#EP").val() + "'";
	var token = "";
	var campos = "";
	for( var i = 0; i < fecha; i++ ) {
		campos += token + mes[ i ];
		token = " + ";
	}

	var acum = 0.0;
	var acum = parseFloat($("#acumuladoOperacion").val());
	var szWhere = "";

	var szTabla = $("#cEsRadicado").val() == "S" ? "VDISPONIBLEEP_RADICADO" : "VDISPONIBLEEP";

	$.getJSON(
			"../catalogos/SelectJson.jsp",
			{
				Tabla : szTabla,
				Param : szWhere,
				MaxReg : elMonto,
				Campos : campos,
				ajax : 'false'
			},
			function(j) {
				var acumulado = 0.0;
				for( var i = 0; i < j.length; i++ ) {
					acumulado = parseFloat(acumulado) + parseFloat(j[ i ].Col3);
				}
				if( acumulado < validM ) {
					Swal.fire({ icon: 'warning',
								text: "La cuenta no tiene suficiente disponible." });
					$("#mMovimiento").val("");
					$("#EP").val("");
					$("#nClaveCNA1").val("");
					return;
				}

				var resto = parseFloat(validM);
				var aplicar = 0.0;

				for( var i = 0; i < j.length; i++ ) {
					$("#cEvento").val("");

					if( $("#tipo_fuente").val() == "IP" ) {
						queryFormPost("eventpEP4Read", {
							async : false
						});
					} else {
						queryFormPost("eventpEP2Read", {
							async : false
						});
					}

					if( $("#cEvento").val() == "" ) {
						Swal.fire({ icon: 'warning',
									text: "La cuenta no corresponde al concepto." });						
						return;
					}

					$("#eventoPoliza").val($("#cEvento").val());
					$("#cEvento").val("APD_" + $("#cEvento").val());
					$("#nMes").val($("#fRecepcion").val().split("/")[ 1 ]);
					$("#cMes").val($("#nMes").val());

					redondeo = Number($("#acumuladoOperacion").val()) + Number(validM);
					redondeo2 = Math.round(Number(redondeo) * 100) / 100;
					var oTableM = $('#grdMovimientos').dataTable();
					var aData = oTableM.fnGetData();
					var nRows = aData.length + 1;
					if( Number(redondeo2) < Number(validN) ) {
						fnClickAddRowZ(nRows, j[ i ].Col1, $("#tConcepto").val(), $("#ALM").val(), $("#altaAlmacen").val() + "|" + $("#cAnioFactEP").val() + "|" + $("#nFacturaEP").val(), validM, $("#dCtaBanc").val(), $("#cCtaBanc").val());
						$("#acumuladoOperacion").val(Math.round(Number(redondeo) * 100) / 100);
					} else if( Number(redondeo2) == Number(validN) ) {

						fnClickAddRowZ(nRows, j[ i ].Col1, $("#tConcepto").val(), $("#ALM").val(), $("#altaAlmacen").val() + "|" + $("#cAnioFactEP").val() + "|" + $("#nFacturaEP").val(), validM, $("#dCtaBanc").val(), $("#cCtaBanc").val());
						$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
						$("#acumuladoOperacion").val(Math.round(Number(redondeo) * 100) / 100);
						$(".pasoTres").show();

						$("#nIdClaveEgresos2").attr('disabled', true); //URVP se deshabilita al cumplir el importe capturado al inicio-------SEGUIR REVISANDO
						$("#Limpia").attr('disabled', true); //URVP se deshabilita al cumplir el importe capturado al inicio
						$("#tConcepto").attr('disabled', true); //URVP se deshabilita al cumplir el importe capturado al inicio
						$("#mMovimiento").attr('readonly', true); //URVP se deshabilita al cumplir el importe capturado al inicio
						$("#agrega2").attr('disabled', true); //URVP.11092014
						$("#L03").click();
						$("#DCD_RETENCION").val($("#mImporteISRLaudosTotal").val());
						$("#DCD_NETO").val($("#mImporteBruto").val());
						$("#DCD_ISR").val($("#mImporteISRLaudosTotal").val());
						$("#DCD_CONCEPTO").val($("#cConcepto").val());
						//$(".extra").attr('disabled', true);
						//cssReadOnly();
						//$("#TIPO_MOVIMIENTO").attr('disabled', true);//URVP se deshabilita al cumplir el importe capturado al inicio (se comentariza y se agrega e una condicion mass abajo) ya que hizo el insert en bd y no se ocupa el control

					} else if( Number(redondeo2) > Number(validN) ) {

						Swal.fire({ icon: 'warning',
									text: "Con este Monto superaria el Importe Neto." });
						//$("#agrega2").attr('disabled', true); //URVP.11092014
						$("#mMovimiento").val("");
						$("#EP").val("");
						$("#nClaveCNA1").val("");
						return;
					}


					// $(".pasoTres").show();
					var table = document.getElementById('grdCompromisos');
					var rowCount = table.rows.length;
					for( var i = 1; i <= rowCount; i++ ) { // URVP.05112014
						// TENER EN
						// CUENTA EL
						// CAMBIO DE
						// = A <=
						var row = table.rows[ i ];
						var elMesGrd = '';
						var elMontoGrd = '';
						var redonGrd = '';
						try {
							var elMesGrd = row.cells[ 0 ].innerHTML;
							var elMontoGrd = row.cells[ 2 ].innerHTML;
						} catch( e ) {
							null;
						}
						if( null != elMesGrd ) {
							if( elMesGrd.toString() != "" ) {

								$("#cMes").val(elMesGrd.toString());
								$("#nMes").val(elMesGrd.toString());

								redonGrd = Math.round(elMontoGrd.toString() * 100) / 100;
								$("#DCD_IMP_BRUTO").val(redonGrd);
								$("#mImporteMasIva").val(redonGrd);
								var altaAlmacenresp = $("#altaAlmacen").val();

								$("#altaAlmacen").val(
									$("#altaAlmacen").val()
									+ "|"
									+ $("#cAnioFactEP")
										.val()
									+ "|"
									+ $("#nFacturaEP")
										.val());

								// VGC20140913 Se actualiza el numero de
								// folio de la relacion de gastos cuando
								// se ha seleccionado.
								try {
									// URVP.21012015 SE OBTIENE QUE
									// PORCENTAJE CORRESPONDE EL ISR AL
									// PAGO PARA INSERTAR LO
									// CORRESPONDIENTE EN EL DETALLE
									if( $("#DESTINO_GASTO").val() == "NORE"  || $("#DESTINO_GASTO").val() == "CQRE") {
										/*var montoISR = $("#mImporteISRLaudosTotal").val();
										montoISR = montoISR.toFixed(2);
										$("#mImporteISRLaudos").val(montoISR);
										$("#mImporteNetoEP").val(Number(redonGrd- montoISR).toFixed(2));*/
										var porcentaje = 0.00;
										//porcentaje = ( redonGrd * 100 ) / validN;
										//porcentaje = porcentaje / 100;
										porcentaje = Number(Sinfrmt($("#mImporteISRLaudosTotal").val())) / validN;
										//porcentaje = porcentaje.toFixed(2);
										var montoISR = 0.00;
										//montoISR = porcentaje * Number(Sinfrmt($("#mImporteISRLaudosTotal").val()));
										montoISR = porcentaje * Number(Sinfrmt($("#mImporteMasIva").val()));
										montoISR = montoISR.toFixed(2);
										$("#mImporteISRLaudos").val(montoISR);
										$("#mImporteNetoEP").val(Number(redonGrd - montoISR).toFixed(2));
									}


									if( $("#cEsRadicado").val() == "S" ) {
										queryFormPost({
											queryName : "tRelacionGastoDetalleCreate,tPagoApartadoDetCreateRadicado",
											async : false,
											callback : function() {}
										});
									} else {
										if ($("#tipoComprobacion").val() == "Anticipo Liquidacion"){
											queryFormPost({
												queryName : "tRelacionGastoDetalleCreateLIQ",
												async : false,
												callback : function() {
													queryFormPost(
														"tPagoApartadoDetCreate",
														{
															async : false
														});
													}
												});
										}else{
											queryFormPost({
												queryName : "tRelacionGastoDetalleCreate",
												async : false,
												callback : function() {
													queryFormPost(
														"tPagoApartadoDetCreate",
														{
															async : false
														});
													}
												});											
										}
									}

								} catch( e ) {
									$("#grdMovimientos").dataTable()
										.fnClearTable();
									claves();
									Swal.fire({ icon: 'warning',
												text: "No se insertó el registro, intenta nuevamente." });
								}

								$("#altaAlmacen").val(altaAlmacenresp);

								table.deleteRow(i);
								rowCount--;
								i--;
							}
						}
					}
					$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
					$("#mMovimiento").val("");
					$("#EP").val("");
					$("#nClaveCNA1").val("");
					$("#importePartidaRelGasto").val("");
					$("#importePartidaViatico").val("");
				
				}

				$("#partCOMSOC").val("");
				queryFormPost("BuscaPartidaCOMSOCRead", {
					async : false
				});
				if( vOGT == $("#partCOMSOC").val() ) {
					bCOMSOC = true;
					Swal.fire({ icon: 'warning',
								text: $("#cMsjCOMSOC").val() });
				}

				if( Number(redondeo2) == Number(validN) ) { //URVP se deshabilita al cumplir el importe capturado al inicio
					$("#TIPO_MOVIMIENTO").attr('disabled', true);
					queryFormPost({
						queryName : "leeImporteRegistradoBD",
						async : false,
						callback : function() {
							if( Number($("#importeCapturadoBD").val()) != Number($(
									"#mImporteNeto").val()) ) {
								regresaPasoCapturasEP();
							} else {
								if( $("#DESTINO_GASTO").val() == "CPRP" )
									$("#DCD_IMP_BRUTO").val(
											parseFloat(
												Number($(
													"#DCD_IMP_BRUTO")
													.val())
												- Number($(
													"#DCD_IVADES")
													.val()))
												.toFixed(2));
							}
						}
					});
				}
			});
	$("#tConcepto").attr('disabled', true); //SE DESHABILITA NUEVAMENTE
}

function fnClickAddRowZ(A, B, C, D, E, F, G, H) {
	$('#grdMovimientos').dataTable().fnAddData([ A, B, C, D, E, F, G, H ]);
}
function fnClickAddRowB(A, B, C) {
	$('#grdRetencion').dataTable().fnAddData([ A, B, C ]);
}
function fnClickAddRowC(A, B, C) {
	$('#grdCompromisos').dataTable().fnAddData([ A, B, C ]);
}

function cambioMovmientos() {
	var validM;
	var fechaDia = new Date();
	const anio = fechaDia.getFullYear();

	validM = Sinfrmt($("#mMovimiento").val());

	var szWhere = "";
	var elMonto = "";
	var campos = "";
	var oTablD = $('#grdCompromisos').dataTable();
	oTablD.fnClearTable();

	if( $("#aEjercicioFiscal").val() == anio )
		campos = $("#aEjercicioFiscal").val() + ", " + $("#fRecepcion").val().split("/")[ 1 ] + ", '" + $("#EP").val() + "'";
	else
		campos = $("#aEjercicioFiscal").val() + ", " + "12" + ", '" + $("#EP").val() + "'";

	szWhere = " clavesiaff =substring('" + $("#EP").val() + "',1,55)  ";
	var szTabla = "SALDOS_DISPONIBLE_PAGO";
	if( $("#cEsRadicado").val() == "S" )
		szTabla = "SALDOS_DISPONIBLE_PAGO_RADICADO";

	$.ajax({
		url : "../catalogos/SelectJson.jsp",
		dataType : 'json',
		type : "POST",
		data : {
			"Tabla" : szTabla,
			"Param" : szWhere,
			"MaxReg" : elMonto,
			"Campos" : campos,
			"Order" : " order by 1 desc "
		},
		async : false,
		success : function(j) {

			var acumulado = 0.0;
			var redon = 0;
			for( var i = 0; i < j.length; i++ ) {
				acumulado = parseFloat(acumulado) + parseFloat(j[ i ].Col2);
				acumulado = acumulado.toFixed(2);
				$("#TOTALSUBCUENTA").val(acumulado);
			}
			queryFormPost("LeerMontoRELG_EP_Read", {
				async : false
			});
			acumulado = parseFloat(acumulado) - parseFloat($("#montoprevio").val());
			acumulado = acumulado.toFixed(2);

			redon = Math.round(parseFloat(acumulado) * 100) / 100;

			if( redon < validM ) {
				Swal.fire({ icon: 'warning',
							text: "La Cuenta no tiene Suficiente Saldo Disponible" });
				$("#mMovimiento").val(0);
				return;
			}

			var resto = parseFloat(validM);
			var aplicar = 0.0;

			generar();
			for( var i = 0; i < j.length; i++ ) {
				$("#nMes").val(j[ i ].Col0);
				queryFormPost("LeerMontoRELG_EPMes_Read", {
					async : false
				});
				var SuficMes = parseFloat(j[ i ].Col2) - parseFloat($("#montoprevio").val());
				SuficMes = SuficMes.toFixed(2);
				if( SuficMes > 0 ) {
					if( resto - SuficMes > 0 ) {
						aplicar = SuficMes;
					} else {
						aplicar = resto;
					}
					aplicar = parseFloat(aplicar);
					resto = resto - aplicar;
					resto = parseFloat(resto.toFixed(2));
					//alert(aplicar);
					if( aplicar > 0.0 ) {
						fnClickAddRowC(j[ i ].Col0, j[ i ].Col1, aplicar);
					}
				}
			}

			//$("#agrega2").attr('disabled', false); //URVP.11092014

			var vOGT = $("#EP").val();
			vOGT = vOGT.substring(31, 36);
			$("#vOGT").val(vOGT);

			if( $("#tConcepto").val() != "AL" ) {
				$("#partida").val("");
				queryFormPost("BuscaPartidaExcepRead", {
					async : false
				});
				if( vOGT == $("#partida").val() ) {
					querySelectPost("CatalogoAlmacenRead", "ALM", {
						async : false
					});
					$("#altaAlmacen").val("");
					//$("#altaAlmacen").show();
					$("#ALM").show();
					$("#cAnioFactEP").val("");
					$("#nFacturaEP").val("");
				//$("#cAnioFactEP").show(); 
				//$("#nFacturaEP").show();			
				} else {
					$("#altaAlmacen").val("0");
					$("#altaAlmacen").hide();
					querySelectPost("CatalogoAlmacenVacioRead", "ALM", {
						async : false
					});
					$("#ALM").hide();
					$("#cAnioFactEP").val("");
					$("#nFacturaEP").val("");
					$("#cAnioFactEP").hide();
					$("#nFacturaEP").hide();
				}
			}

		},
		error : function(xhr, textStatus, errorThrown) {
			Swal.fire({ icon: 'warning',
						text: "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown });
			return false;
		}
	});

/*
$.getJSON("../catalogos/SelectJson.jsp", {Tabla: szTabla, Param: szWhere, MaxReg: elMonto,	Campos: campos,	Order: " order by 1 desc ", ajax: 'false'},
	function(j) {
		var acumulado = 0.0;
		var redon = 0;
		for ( var i = 0; i < j.length; i++) {
			acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
			acumulado = acumulado.toFixed(2);
			$("#TOTALSUBCUENTA").val(acumulado);
		}
		queryFormPost("LeerMontoRELG_EP_Read", {async : false});
	   	acumulado = parseFloat(acumulado) - parseFloat( $("#montoprevio").val() );
	   	acumulado = acumulado.toFixed(2);

		redon = Math.round(parseFloat(acumulado) * 100) / 100;

		if (redon < validM) {

			alert('La Cuenta no tiene Suficiente Saldo Disponible');
			$("#mMovimiento").val(0);
			return;
		}

		var resto = parseFloat(validM);
		var aplicar = 0.0;

		generar();
		for ( var i = 0; i < j.length; i++) {
			$("#nMes").val( j[i].Col0 );
			queryFormPost("LeerMontoRELG_EPMes_Read", {async : false});
			var SuficMes = parseFloat(j[i].Col2) - parseFloat( $("#montoprevio").val() );
			SuficMes = SuficMes.toFixed(2);
			if (SuficMes > 0){	
				if (resto - SuficMes > 0) {
					aplicar = SuficMes;
				} else {
					aplicar = resto;
				}
				aplicar = parseFloat(aplicar);
				resto = resto - aplicar;
				resto = parseFloat(resto.toFixed(2));
				//alert(aplicar);
				if (aplicar > 0.0) {
					fnClickAddRowC(j[i].Col0, j[i].Col1, aplicar);
				}		
			}

		}
		
		//$("#agrega2").attr('disabled', false); //URVP.11092014
		
		var vOGT = $("#EP").val();
		vOGT = vOGT.substring(31, 36);
		$("#vOGT").val( vOGT ); 
		
		if ($("#tConcepto").val() != "AL"){
			$("#partida").val("");
			queryFormPost("BuscaPartidaExcepRead", {async : false});
			if (vOGT == $("#partida").val()){
				querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
				$("#altaAlmacen").val("");
				//$("#altaAlmacen").show();
				$("#ALM").show();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				//$("#cAnioFactEP").show(); 
				//$("#nFacturaEP").show();			
			} else{
				$("#altaAlmacen").val("0");
				$("#altaAlmacen").hide();
				querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
				$("#ALM").hide();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				$("#cAnioFactEP").hide(); 
				$("#nFacturaEP").hide();			
			}
		}		
	}
);
*/
}


function generar() {
	try {
		var table = document.getElementById('grdCompromisos');
		//var table2 = document.getElementById('dt_paraEnvio')       		
		var rowCount = table.rows.length;

		//alert("rowCount "+rowCount);

		for( var i = 0; i < rowCount; i++ ) {
			var row = table.rows[ i ];
			//    var chkbox = row.cells[0].childNodes[0];
			//var algo = row.cells[3].childNodes[0].toString();
			//alert(algo);			
			var chkbox = '';
			try {
				var chkbox = row.cells[ 3 ].childNodes[ 0 ];
			} catch( e ) {
				null;
			}
			if( null != chkbox ) {
				if( chkbox.toString() == $("#EP").val() ) {
					//alert(chkbox.toString);			
					//   if(null != chkbox && true == chkbox.checked) {			
					//quitando de la lista de compromisos los registros enviados a SICOP...
					table.deleteRow(i);
					rowCount--;
					i--;
				}
			}

		// }		
		}
	} catch( e ) {
		Swal.fire({ icon: "error",
					text: e });
	}
}
function setSequenceAptd(seqValue) {
	$("#nFolioApartado").val(seqValue);
}
function obtenTipoClaveBenf() {
	queryFormPost("obtenerTipoCBEN", {
		async : false
	});
}

function fnClickAddRowComp() {
	$("#DCD_TIPO_OPE").attr('disabled', false); //URVP.18062014 se habilita para obtener su info y se habilita nuevamente en contrarecibo()
	$("#Agregar").attr('disabled', true);
	$('#grdFacturas').dataTable().fnAddData(
		[ $("#DCD_FACTURA").val(),
			$("#DCD_FECHA_FACTURA").val(),
			$("#DCD_TBEN").val(),
			$("#DCD_CBEN").val(),
			$("#DCD_TIPO_OPE").val(),
			$("#DESCRIPCION20").val(),
			$("#DCD_IMP_BRUTO").val(),
			$("#DCD_IVADES").val(),
			$("#DCD_IVA").val(),
			$("#DCD_ISR").val(),
			$("#DCD_MIL5").val(),
			$("#DCD_MIL2").val(),
			$("#DCD_CONTRIBUCION").val(),
			$("#DCD_OTRAS_RET").val(),
			$("#DCD_PENALIZACION").val()
		]);
	$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
	$("#RFC").val($("#cIdRFC_RelacionGasto2").val());
	$("#fRecepcion2").val($("#fRecepcion").val());

	/*if( $("#cCentroContable").val() == "10" ) {
		getNextSequenceVal({
			seqName : "CT-" + $("#cCentroContable").val(),
			async : false,
			callback : setSequenceValCT
		});
	} else {*/
		getNextSequenceVal({
			seqName : "CR-" + $("#cCentroContable").val(),
			async : false,
			callback : setSequenceVal
		});
	//}
	//getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
	$("#elcontra").val($("#caNoContrarrecibo").val());

	setTimeout("contrarecibo()", 1000);
	var miCheckbox = document.getElementById('Si_ISSSTE');
	if(miCheckbox.checked){	
		queryFormPost("tRelacionGastoDetalleCuotasUpdate",{
			async : false
		});
	}

}


function setSequenceValCT(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = $("#cCentroContable").val() + "CT" + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val(seqValue);
	$("#elcontra").val(seqValue);
}


function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "1" + seqValue.substr(seqValue.length - 5);
	seqValue = $("#cCentroContable").val() + $("#cxpPrefijo").val() + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val(seqValue);
}

function contrarecibo() {
	$("#cDescripcionPoliza").val("Apartado del pago: " + $("#caNoContrarrecibo").val());
	$("#FechaAplAptd").val($("#fAplicacion").val());

	queryFormPost("SIG_FOLIO_CONTRARRECIBORELGASTOUpdate", {
		async : false
	});
	queryFormPost("tDocumentacionComprobatoriaDetCreate", {
		async : false
	});
	queryFormPost("tPagoApartadoEncCreate", {
		async : false
	});

	//$("#divImprime").show();//***
	//cmdImprimir('Contrarecibo');
	$("#DCD_TIPO_OPE").attr('disabled', true); //URVP.18062014 se deshabilita nuevamente
	setTimeout("elRetardo()", 1000);

}

function cmdGuardar() {
	$("#nombre").val($("#cIdRelacion").val());
	$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());

	$("#nMes").val($("#fAplicacion").val().split("/")[ 1 ]);
	$("#cMes").val($("#nMes").val());


	$("#campo").val("nFolioPagoApartado");
	$("#tablaEnc").val("tPagoApartadoEncabezado");
	$("#campoCondicion").val("caNoContrarrecibo");
	$("#tablaDet").val("tPagoApartadoDetalle");
	$("#tipoAplicar").val("PAGOAPARTADO");
	if( procesar() ) {
		//actualizar Encabezado Folio de Caja
		queryFormPost("UPDATERelacionGastosFolioCaja", {
			async : false
		});

	}
}

function procesar() {
	$("#dialog-Procesando").dialog("open");
	return breturnVal;
}

function elRetardo() {
	$("#mImporteNetoEP").val(Number(Number($("#mImporteNeto").val())).toFixed(2)) ;

	$(".paso01").attr('disabled', false);
	elParametro = "'" + $("#caNoContrarrecibo").val() + "', '"
		+ "1', '0', '0', '"
		+ $("#DCD_IMP_BRUTO").val() + "', '" + $("#DCD_DEVOL").val()
		+ "', '" + $("#DCD_SANCION").val() + "', '" + $("#DCD_AMORT").val()
		+ "', '" + $("#DCD_IVADES").val() + "','" + $("#DCD_RETENCION").val()
		+ "', '" + $("#DCD_PENALIZACION").val() + "', '"
		+ $("#mImporteNetoEP").val() //+$("#DCD_NETO").val()  
		+ "', '" + $("#DCD_FECHA_FACTURA").val()
		+ "','" + $("#cCentroContable").val() + "'," + $("#aEjercicioFiscal").val() + ",'"
		+ $("#nombre").val() + "', '3','0','"
		+ $("#cIdRFC_RelacionGasto2").val() + "', 0.0";
	// alert(elParametro);
	$.getJSON("../catalogos/InsertJson.jsp", {
		Tabla : "TCONTRARECIBODIVERSOSCREATE",
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {});
	$(".paso01").attr('disabled', true);
	parent.document.getElementById("pb_save").disabled = false;

}

function cmdImprimirRCH() {
	window.open(
		"../admin/SeguridadCatalogos?"
		+ "catalogo=CONTRARECIBO"
		+ "&accion=run"
		+ "&rn=VOLANTERECHAZO.jasper"
		+ "&NumeroFolio=" + $("#noFolio").val(),
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");
}

function cmdImprimir(elFormato) {
	var swhere = "&folio=" + $("#caNoContrarrecibo").val();
	if( $("#docAplicado").val() == "C" && $("#caNoContrarrecibo").val().substring(0, 4) == $("#cCentroContable").val() + "CT" ) {
		var vUnidad = $("#cUnidadResponsable").val();
		vUnidad = vUnidad.substring(0, 3);
		$("#noFolio").val("RELG-" + vUnidad + "-" + $("#id_caso").val());

		cmdImprimirRCH();
		return;
	}

	if( elFormato == "ComprobanteRegistro" || elFormato == "NuevoContrarecibo" ) {
		swhere = "&whereFolio= and CR.caNocontrarrecibo = '" + $("#caNoContrarrecibo").val() + "'";
	}
	window.open("../admin/SeguridadCatalogos?"
	+ "catalogo=CONTRARECIBO"
	+ "&accion=run"
	+ "&rn=" + elFormato + ".jasper"
	+ swhere,
		"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");

		//setTimeout('anexo("' + elFormato + '")', 1000);

}
function showDivOficio(esUpdate) {
	var cmpName = "oficioDelegatorio" + ( esUpdate ? "Update" : "" );
	var divName = "oficioDelegatorioCaptura" + ( esUpdate ? "Update" : "" );
	if( $("#" + cmpName).is(":checked") )
		$("#" + divName).show();
	else
		$("#" + divName).hide();
}

function showDivOficioVoBo(esUpdate){
			var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
			var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
			if( $("#" + cmpName ).is(":checked") )
				$("#"+divName).show();
			else
				$("#"+divName).hide();
		}
		
function actualizaFolioCaja() {
	$("#noSolicitudCaja").val($("#nfolioCaja").val());
}

function laudoDevengadoClick() {
	var devengado = document.getElementById("chkLaudoDevengado").checked;
	if( devengado ) {
		$("#solicitudAnticipadaDiv").css("display", "none");
		$("#DialogSolCaja").dialog("close");
		$("#cTipoRfc").val("6");
		$("#mImporteNeto").removeClass("notEditable");
		document.getElementById("mImporteNeto").readOnly = false;
		$("#cEsDevengado").val("S");
		$("#cEsLiquidacion").val("N");
		$("#ID_DESTINO_GASTO").val("CLRE");
		$("#idDestinoGasto").val("CLRE");
		$("#nfolioCaja").val("");
		$("#montoSol").val("");
		$("#cDescripcionCaja").val("");
		$("#mImporteNeto").val("$0.00");
		$("#mImporteISRLaudosTotal").val("$0.00");
		$("#mImporteBruto").val("$0.00");
		actualizaFolioCaja();
	} else {
		$("#solicitudAnticipadaDiv").css("display", "block");
		$("#cTipoRfc").val("6");		
		//document.getElementById("mImporteNeto").className = "notEditable";
		document.getElementById("mImporteNeto").readOnly = false;
		$("#ID_DESTINO_GASTO").val("NORE");
		$("#idDestinoGasto").val("NORE");
		$("#cEsDevengado").val("N");
		$("#cEsLiquidacion").val("N");
	}

}

function liquidacionDevengadoClick() {
	var liquidacion = document.getElementById("chkLiquidacionDevengado").checked;
	if( liquidacion ) {
		$("#solicitudAnticipadaDiv").css("display", "none");
		$("#DialogSolCaja").dialog("close");
		$("#cTipoRfc").val("3");
		$("#mImporteNeto").removeClass("notEditable");
		document.getElementById("mImporteNeto").readOnly = false;
		$("#cEsDevengado").val("S");
		$("#cEsLiquidacion").val("S");
		$("#ID_DESTINO_GASTO").val("GLRE");
		$("#idDestinoGasto").val("GLRE");
		$("#nfolioCaja").val("");
		$("#montoSol").val("");
		$("#cDescripcionCaja").val("");
		$("#mImporteNeto").val("$0.00");
		$("#mImporteISRLaudosTotal").val("$0.00");
		$("#mImporteBruto").val("$0.00");
		actualizaFolioCaja();
	} else {
		$("#solicitudAnticipadaDiv").css("display", "block");
		$("#cTipoRfc").val("6");
		//document.getElementById("mImporteNeto").className = "notEditable";
		document.getElementById("mImporteNeto").readOnly = false;
		$("#ID_DESTINO_GASTO").val("NORE");
		$("#idDestinoGasto").val("NORE");
		$("#cEsDevengado").val("N");
		$("#cEsLiquidacion").val("N");
		
	}

}


function muestraRadicado() {
	if( esRadicado ) {
		$("#chk_radicado").show();
		$("#lbl_radicado").show();
		$("#chk_radicado").attr("checked", true);
		$("#chk_radicado").prop("disabled", true);
	} else {
		$("#chk_radicado").hide();
		$("#lbl_radicado").hide();
	}

	$("#chk_radicado").click();

}

function changeRadicado() {
	var checked = $("#chk_radicado").attr("checked");

	if( checked ) {
		$("#cEsRadicado").val("S");
	} else {
		$("#cEsRadicado").val("N");
	}

}

/*vgc290916 Valida que la EP que se intenta agreagar no exista ya en el pago.*/
function validaEPCapturada(epFind) {
	var existe = false;
	var eps = $("#grdCompromisos").dataTable().fnGetData();
	for( i = 0; i < eps.length; i++ ) {
		if( eps[ i ][ 1 ] == epFind ) {
			existe = true;
			break;
		}
	}

	return existe;
}

function insertaObjeto(folioIngresoP, EPP, montoP, mesP) {
	if( !relaciones )
		relaciones = [];

	var obj = {
		folioIngreso : folioIngresoP,
		EP : EPP,
		monto : montoP,
		mes : mesP
	};

	relaciones[ relaciones.length ] = obj ;
}

function cambioss() {
	cambioMovmientos();
}

function cat_pasivo() {	
	Swal.fire({ icon: "info",
				text: "hola" });
}

function cargaTipoDestino() {
	if( $("#tipo_fuente").val() == "IP" ) {
		$("#cTipoFuente").val("4");
	} else if( $("#tipo_fuente").val() == "CE" ) {
		$("#cTipoFuente").val("1");
	} else {
		$("#cTipoFuente").val("1");
	}
}

function habilita(){								
	var miCheckbox = document.getElementById('Si_ISSSTE');
	
	if(miCheckbox.checked){	
		$("#nSiISSSTE").val("S");
		$("#divPagoISSSTE").show();
		querySelectPost("Catalogo_BeneficiariosCuotas", "dRFC_Cuotas", {
			async : false
		});	
	} else {
		$("#nSiISSSTE").val("N");		
		$("#dRFC_Cuotas").val("");
		$("#divPagoISSSTE").hide();
	}	
			
	//queryFormPost("tReintegroEIngresoUpdate", {async: false });
	document.getElementById("Si_ISSSTE").disabled = true;
}

