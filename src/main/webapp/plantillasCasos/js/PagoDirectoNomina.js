var oTablevFact;
var oTableCucop;
var oTableLineas;
var oTableRetencion;
var oTableEPs;
var oTableSaldos;
var oTableCalendarioPago
var retencionesCargadas = false;
var toleracia = 0.01;

var es_MX = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtado de _MAX_ registros)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Buscar:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};

function onLoadPlantilla(id_oper) { //Carga Plantilla
	var resp = true;
	switch (parseInt(id_oper, 10)) {
	//Captura
	case 1:

		try {
			$("#idCaso").val(idCaso);
			$("#numEmpleadoElab").val(numeroEmpleado);
			//if (parent.document.getElementById("pb_save")) {
				//parent.document.getElementById("pb_save").disabled = true;
			//}
			$("#tabs").tabs();
			initUI();
			estatusCambiado(id_oper);
			
			actualizaGabinete();

		} catch (e) {
			var msg = "";
			if (e instanceof TypeError)
				msg = e.message;
			else
				msg = e;

			Swal.fire(" Si el problema continua reporte al administrador.","Error cargando informacion inicial \nCasua:" + msg + "\n Intente nuevamente.","error");
			deshabilitaTodo();
		}
	case 2: //
		break;
	default:
		resp = false;
		break;
	} 
	return resp;
}

function initUI() {
	oTablevFact =  $('#grdValidaFacturas').dataTable({
				        "bPaginate": true,
	        			"bLengthChange": true,
	        			"bFilter": true,
	        			"bSort": true,
	        			"bInfo": true,
	        			"bAutoWidth": true,
						"sScrollY": "100%",
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",						
						"bScrollCollapse": true,
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtered from _MAX_ total entries)",
							sInfoPostFix: "",
							sInfoThousands: ",",
							sSearch: "Filtro:",
							oPaginate: {
								sFirst:    "Primero",
								sPrevious: "Ant.",
								sNext:     "Sigte.",
								sLast:     "&Uacute;ltimo"
							}
						}
			});
	try {		
		creaDiagloFacturas();
		obtenPagoEstatus();
		cargaDestinoGasto();
		creaDialogCorreo();
		
		/* ========== Asignacion de Funciones ======= */
		$("#cargaFacturasBtn").button().click(function() {
			muestraDivFacturas();
		});
		
		$("#calcularBtn").button();
		$("#capturaCorreo").button();		
		$("#capturaCorreo").hide();
		$("#catIdTipoOperacion").change(function() {
			$("#idTipoDocumento").val($("#catIdTipoOperacion").val());
			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				cargaDestinoGasto();
			} else {
				limpiaSelect("DESTINO_GASTO", "-1", "Seleccione Tipo Destino");
				$("#DESTINO_GASTO").change();
			}

		});

		$("#DESTINO_GASTO").change(function() {

			$("#idDestinoGasto").val($("#DESTINO_GASTO").val());

			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				querySelectPost("CatalogoObraTConceptoRead", "tConcepto", {
					async : false
				});
			} else {
				limpiaSelect("tConcepto", "-1", "Seleccione Tipo de Concepto");
				$("#tConcepto").change();
			}
		});

		$("#tConcepto").change(function() {
			$("#idTipoConcepto").val($(this).val());
			$("#idConcepto").val($(this).val());
			$("#TIPO_CONCEPTO").val($(this).val());

			if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
				querySelectPost({
					queryName : 'CatalogoObraTMovimendoRead',
					targetObjectId : 'tmovimiento',
					async : false,
					callback : function() {
						$("#tmovimiento").change();
					}
				});
			} else {
				limpiaSelect("tmovimiento", "-1", "Seleccione Tipo de Movimiento");
				$("#tmovimiento").change();
			}

		});

		$("#tmovimiento").change(function() {
			$("#idTipoMovimiento").val($(this).val());
		});
		$("#ctaBancaria").change(function() {
			$("#CTAB").val($("#ctaBancaria").val());
		});
	} catch (err) {
		errorFatal = true;
		errorFatalMSG = "No se logro obtener el estatus del pago.";

	}
	if (errorFatal) {
		Swal.fire("Ocurrio el siguiente error y no puede continuar el proceso:", errorFatalMSG, 'error');
		deshabilitaTodo();
		return;
	}

	try {
		var idEstatus = obtenPagoEstatus();		
		creaDTFacturas();
		creaDTRetenciones();
		setInitialValuesConcepto();		
		leeEncabezado();
		conceptoPagoDirectoRead();
		leeDatosFactura();
		if (idEstatus == 2)
			iniciaCapturaRetenciones();
	} catch (eCarga) {
		if (eCarga.message)
			errorFatalMSG = eCarga.message;
		else
			errorFatalMSG = eCarga;

		errorFatal = true;
		Swal.fire("Error",errorFatalMSG, "error");
		deshabilitaTodo();
		throw eCarga;
	}
}

function creaDTResumenFacturas() {
	oTablevFact = $('#grdResumenFacturas').dataTable(
		{
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ResumenCFDI&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='PAGODIRECTO'",
			"bJQueryUI" : true,
			"sScrollX" : "100%",

			"sScrollY" : "150px",
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bInfo" : true,
			aoColumns : [
				{
					sName : "UUID"
				},
				{
					sName : "mImporteBruto"
				},
				{
					sName : "mimporteiva"
				},
				{
					sName : "mOtrosImpuestos"
				},
				{
					sName : "mImporteDescuento"
				},
				{
					sName : "mImporteRetencion"
				},
				{
					sName : "mimporteconiva"
				} ]
		});
}

function creaDTFacturas() {
	oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ResumenCFDI&qw=folioPago=" + $("#nFolioPago").val() + " AND tipoPago='PAGODIRECTO'",
			"bJQueryUI" : true,
			"sScrollX" : "100%",
			"sScrollY" : "125px",
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bInfo" : true,
			aoColumns : [
				{
					sName : "UUID"
				},
				{
					sName : "mImporteBruto"
				},
				{
					sName : "mimporteiva"
				},
				{
					sName : "mOtrosImpuestos"
				},
				{
					sName : "mImporteDescuento"
				},
				{
					sName : "mImporteRetencion"
				},
				{
					sName : "mimporteconiva"
				} ]
		});
}



function creaDTRetenciones() {
	var idEstatus = obtenPagoEstatus();
	
	if (idEstatus == 1) {
		oTableRetencion = $('#grdRetencion').dataTable({
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPagoDirectoRetencion&qw=nFolioPagoDirecto=" + $("#nFolioPago").val() + " AND 1=1",
			"bJQueryUI" : true,
			"sScrollX" : "100%",
			"sScrollXInner " : "100%",
			"sScrollY" : "145px",
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bInfo" : true,
			aoColumns : [
				{
					sName : "cIdTipoRetencion"
				},
				{
					sName : "cTipoRetencion"
				},
				{
					sName : "base"
				},
				{
					sName : "porcentajeRetencion"
				},
				{
					sName : "retencion"
				},
				{
					sName : "CMD"
				} ]
		});

		$("#grdRetencion tbody").click(function(event) {

			$(oTableRetencion.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

		});

		$("#grdRetencion tbody").dblclick(function(event) {

			$(oTableRetencion.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTableRetencion.fnGetPosition(event.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			var id = $("#grdRetencion").dataTable().fnGetData()[currIndex][0];
			var ret = $("#grdRetencion").dataTable().fnGetData()[currIndex][1];
			var retCalculada = $("#grdRetencion").dataTable().fnGetData()[currIndex][4];

			$("#idRetencionEdit").val(id);
			$("#descRetencionEdit").val(ret);
			$("#importeCalculadoEdit").val(retCalculada);
			$("#nuevoImporteRetEdit").focus();

			$("#dlg-EditaRetencion").dialog("open");

		});

		queryFormPost("sumRetencionesCaptPDRead", {
			async : false
		});
	}
}

function creaDTEPs() {
	var mes = $("#fAplicacion").val().split("/")[1];
	var folio = $("#nFolioPagoDirecto").val();
	var login = $("#login").val();
	var fuenteFinanciamiento = 1;

	oTableEPs = $("#tblEP").dataTable(
		{
			"bDestroy" : true,
			fnDrawCallback : function() {},
			bAutoWidth : true,
			oLanguage : es_MX,
			bServerSide : true,
			"sScrollX" : "100%",
			"sScrollXInner " : "110%",
			"sScrollY" : "100px",
			"fnServerData" : function(sSource, aoData, fnCallback) {
				$.ajax({
					"dataType" : 'json',
					"type" : "POST",
					"url" : sSource,
					"data" : aoData,
					"success" : fnCallback
				});
			},
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=fn_SaldoDisponiblePD_Nomina('" + login + "'" + ',' + fuenteFinanciamiento + ")",
			bProcessing : true,
			sPaginationType : "full_numbers",
			bJQueryUI : true,
			aoColumns : [ {
				sName : "ep"
			}, {
				sName : "mSaldoEnero",
				sClass : "money"
			}, {
				sName : "mSaldoFebrero",
				sClass : "money"
			}, {
				sName : "mSaldoMarzo",
				sClass : "money"
			}, {
				sName : "mSaldoAbril",
				sClass : "money"
			}, {
				sName : "mSaldoMayo",
				sClass : "money"
			}, {
				sName : "mSaldoJunio",
				sClass : "money"
			}, {
				sName : "mSaldoJulio",
				sClass : "money"
			}, {
				sName : "mSaldoAgosto",
				sClass : "money"
			}, {
				sName : "mSaldoSeptiembre",
				sClass : "money"
			}, {
				sName : "mSaldoOctubre",
				sClass : "money"
			}, {
				sName : "mSaldoNoviembre",
				sClass : "money"
			}, {
				sName : "mSaldoDiciembre",
				sClass : "money"
			} ]
		});

	$("#tblEP tbody").click(function(event) {

		$(oTableEPs.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblEP tbody").dblclick(function(event) {

		$(oTableEPs.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableEPs.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		if ($("#tConcepto").val() == "" || $("#tConcepto").val() == "-1" || $("#tmovimiento").val() == "" || $("#tmovimiento").val() == null) {
			Swal.fire("Verifique","Debe seleccionar el concepto y el movimiento antes de ingresar los montos","info");
		} else {
			$("#dlg-CalendarioMontos").dialog("open");
			var ep = $("#tblEP").dataTable().fnGetData()[currIndex][0];
			$("#epShow").val(ep);
			$("#epConsulta").val(ep);
			$("#mesConsulta").val(mes);
			queryFormPost("saldoDispobleAcumRead", {
				async : false
			});
			
		}

	});
}


function windowStatus(texto) {
	window.status = texto;
}

function createDTCalendario() {
	var cWhere = " nFolioPago=" + $("#nFolioPago").val() + " AND cTipoPago='" + $("#cTipoPago").val() + "'";

	oTableSaldos = $("#tblCalendario").dataTable(
		{
			"bDestroy" : true,
			fnDrawCallback : function() {},
			bAutoWidth : true,
			oLanguage : es_MX,
			bServerSide : true,
			"sScrollX" : "100%",
			"sScrollY" : "100px",
			"fnServerData" : function(sSource, aoData, fnCallback) {
				$.ajax({
					"dataType" : 'json',
					"type" : "POST",
					"url" : sSource,
					"data" : aoData,
					"success" : fnCallback
				});
			},
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=vImportesCalendario&qw=" + cWhere,
			bProcessing : true,
			sPaginationType : "full_numbers",
			bJQueryUI : true,
			aoColumns : [ {
				sName : "EP"
			}, {
				sName : "mImporteBruto",
				sClass : "money"
			}, {
				sName : "tipoConcepto"
			}, {
				sName : "tipoMovimiento"
			} ]
		});

	$("#tblCalendario tbody").click(function(event) {

		$(oTableSaldos.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblCalendario tbody").dblclick(function(event) {

		$(oTableSaldos.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableSaldos.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var ep = $("#tblCalendario").dataTable().fnGetData()[currIndex][0];

		$("#epBorrar").val(ep);

		if (confirm("Esta seguro de eliminar la clave\n" + ep + "]\n del calendario?")) {
			queryFormPost({
				queryName : "eliminaEPCalendario",
				async : false,
				callback : function() {
					creaDTEPs();
					createDTCalendario();
				}
			});
		}

	});

	leeTotalCalendarizado();
}


/**
 * Lee el estatus actual del pago. Los estaus se corresponden al catalogo: 
 */
function obtenPagoEstatus() {
	var estatusEncontrado = false;
	
	queryFormPost(
		{
			queryName : "estatusPagoDirectoRead",
			async : false,
			callback : function() {
				estatusEncontrado = true;
			}
		});
	if (estatusEncontrado)
		return parseInt($("#nIDEstatus").val(), 10);
	else
		throw 500;
}

function avanzaEstatus(idOper) {
	var idEstatus = parseInt($("#nIDEstatus").val(), 10);
	var terminado = true;
	var logErrores = "";

	if (idOper == 1) {
		try {
			if (idEstatus < 6) {

				if (idEstatus == 0) {
					guardaPagoDirectoEncabezado();
					//actualizaEstatus();
				} else if (idEstatus == 1) {
					actualizaEstatus();
				} else if (idEstatus == 2) {
					actualizaImportesPagoDirecto();
				}

			}
		} catch (e) {
			terminado = false;
			logErrores = e;
		}
	}

	if (!terminado) {
		throw "No fue posible terminar la operacion debido a:\n" + logErrores;
	} else {
		obtenPagoEstatus();
	}
}

function actualizaImportesPagoDirecto() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url : "../egresos/updateMontoRetenciones",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
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
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		throw logErrores;
}

function actualizaPagoDirectoEncabezado() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url : "../egresos/updateHeader",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
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
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		throw logErrores;
}

function guardaPagoDirectoEncabezado() {
	var guardado = false;
	var logErrores = "";

	$.ajax({
		url : "../egresos/saveHeader",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
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
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	if (!guardado)
		throw logErrores;
}

/**
 * Actualiza los tabs en la pantalla ocultando/mostrando dependiendo el estatus del pago.
 */

var stages = {
	stages : [ {
		"ejecutado" : false,
		"estaus" : 1,
		"tab" : 2,
		"id" : "Concepto",
		"fnTerminado" : function() {
			return conceptoTerminado();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 2,
		"tab" : 3,
		"id" : "Retenciones",
		"fnTerminado" : function() {
			return retencionesTerminado();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 3,
		"tab" : 4,
		"id" : "Movimientos",
		"fnTerminado" : function() {
			return movimientosTerminado();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 4,
		"tab" : 5,
		"id" : "Resumen",
		"fnTerminado" : function() {
			return capturaTerminada();
		}
	}, {
		"ejecutado" : false,
		"estaus" : 5,
		"tab" : 5,
		"id" : "Resumen",
		"fnTerminado" : function() {
			return cxpGenerado();
		}
	} ]
}

/**
 * Funcion llamada cuando cambia el estatus del pago.
 */
function estatusCambiado(idOperacion) {
	try {
		var idEstatus = obtenPagoEstatus();
		procesaPestanas(idOperacion, idEstatus);
	} catch (err) {
		throw err;
	}
}

function procesaPestanas(idOperacion, idEstatus) {
	var stg = null;
	if (idOperacion == 1) {
		for (cnt = 0; cnt < idEstatus; cnt++) {
			stg = stages.stages[cnt];
			if (stg.ejecutado != true) {
				var funcionEjecutada = stg.fnTerminado();
				if (!funcionEjecutada)
					throw "No se logro ejecutar la funcion callback del paso : " + cnt;
				else
					stg.ejecutado = true;
			}
		}

		stg = stages.stages[idEstatus];
		var optName = stg.id;
		$("#" + optName).show();
		$("#Tab" + optName).show();
		$("#" + optName).click();

	}
}

function deshabilitaTodo() {
	$.blockUI();
	if (parent.document.getElementById("pb_save")) {
		parent.document.getElementById("pb_save").disabled = true;
		parent.document.getElementById("pb_save").style.display = "none";
	}
}

/**
 * Lee de la base de datos los montos guardados de la factura.
 */
function leeDatosFactura() {
	queryFormPost({
		queryName : "readMontoFacturasPD",
		async : false,
		callback : function() {
			mImporteIVA = quitaFmt($("#Imp_Iva").val())
		}
	});
	
	$("#mTotalFacturaV").val(quitaFmt($("#mTotalFacturaV").val()));
	$("#mImporteIVA").val(quitaFmt($("#Imp_Iva").val()));
	$("#totalFactura").val(quitaFmt($("#totalFactura").val()));
}

function leeEncabezado() {
	queryFormPost({
		queryName : "pagoDirectoEncRead",
		async : false,
		callback : function() {
			//TODO actualizar los selects 
		}
	});
}


/**
 * Funcion para evitar la captura de letras. Permite que se caputuren solo
 * monntos numericos.
 * 
 * @param evt
 *            Evento de keypress
 * @returns {Boolean} si y solo si la tecla presionada esta en 0123456789.
 */
function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 47) {
		return false;
	}
	return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

function actualizaEstatus() {
	var actualizado = false;

	$.ajax({
		url : "../egresos/avanzaEstatus",
		type : 'post',
		async : false,
		data : $("#formPagos").serialize(),
		dataType : 'json',
		success : function(j) {
			var exito = j.success;

			if (exito) {
				actualizado = true;
			} else {
				var errores = j.errorList;
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
			}
		},
		error : function(errorThrown) {
			alert("error: " + errorThrown.ERROR);
			logErrores = "Error guardando encabezado.";
		}
	});

	return actualizado;
}

/**
 * funcion llamada al dar clic boton Guardar.
 */

function onSubmit(id_oper) {
	var ejecucionCorrecta = ejecutaValidaciones(id_oper, $("#nIDEstatus").val());
	switch (parseInt(id_oper, 10)) {

	case 1: /*Captura*/
		ejecucionCorrecta = ejecucionCorrecta && setVariablesCaso(id_oper);

		if (ejecucionCorrecta) {
			try {
				avanzaEstatus(id_oper);
				avanzaTab = true;
			} catch (e) {
				alert(e);
				ejecucionCorrecta = false;
			}

		}
		break;
	
	default:
		ejecucionCorrecta = false;
		break;
	}
	return ejecucionCorrecta;
}

function ejecutaValidaciones(idOper, idEstatus) {
	 if (idOper == 1 && idEstatus ==0) {
		leeImporteBruto();
		return validacionesEncabezado();
	} else if (idOper == 1 && idEstatus == 1) {
		return validacionesConcepto();
	} else if (idOper == 1 && idEstatus == 2) {
		return validacionesRetenciones();
	} /*else if (idOper == 1 && idEstatus == 3) {
		return true; //validacionesCapturaCompleta();
	} */else if (idOper == 1 && idEstatus == 3) {
		return validacionesCapturaCompleta();
	}  else if (idOper == 1 && idEstatus == 4) {
		return true;
	} else
		throw "No existen validaciones para la operacion : " + idOper + " estatus: " + idEstatus;
}

function validacionesEncabezado() {
	var msg = capturaEncabezadoCompleta();
	if (msg != "") {
		Swal.fire('Para continuar debe corregir lo siguiente:', msg, 'warning');
		return false;
	} else {
		return true;
	}

}

function validaCapturaCorreo(){
	let mensaje = "";
	if( $("#esPPD").val() != "0" &&  $("#correoActual").val() ==""){
		mensaje = "No se capturo el correo para seguimiento y la(s) factura(s) son PPD"
	}
	
	return mensaje;
}

function validacionesEncabezado() {
	var msg = capturaEncabezadoCompleta();
	if (msg != "") {
		Swal.fire('Para continuar debe corregir lo siguiente:', msg, 'warning');
		return false;
	} else {
		return true;
	}

}
function capturaEncabezadoCompleta() {
	var msg = "";
	var facturaCapturdas = getFacturasCapturadas();

	if (facturaCapturdas == 0)
		msg += "\nDebe cargar las facturas del pago.";
	
	var mensajeValido = validaFacturas(); 
	if(mensajeValido != "\n")
		msg += "\n" + mensajeValido;
		
	$("#mTotalFacturaV").val(quitaFmt($("#mTotalFacturaV").val()));
	$("#mImporteIVA").val(quitaFmt($("#mImporteIVA").val()));
	$("#totalFactura").val(quitaFmt($("#totalFactura").val()));
	
	//Valida que si tiene PPD se haya capturado el correo
	var mensaje = validaCapturaCorreo();
	if (mensaje != "")
		msg += "\n" + mensaje;
	
	return msg;
}

function getFacturasCapturadas() {
	
	$("#nFacturasCapturadas").val(0);
	queryFormPost("facturasCapturadasRead", { async : false });
	return parseInt(($("#nFacturasCapturadas").val() == "" ? 0 : $("#nFacturasCapturadas").val()), 10);
	
}

function validaFacturas(){
	var msg = ""
	queryFormPost("consultaSolicitudesSinRetencion", { async : false });
	if($("#listaSol").val()!= "")
		msg = "El régimen fiscal del Proveedor es RESICO, es persona Fisica y las siguientes facturas no tienen retención. Descarte el pago!\n" + $("#listaSol").val();
	
	queryFormPost("validaImporteResico", { async : false })
	msg += "\n" + $("#msgRF").val();
	
	return msg;
}

/**
 * Inicializa catalogos y muestra la informacion no editable.
 */
function setInitialValuesConcepto() {
	/* ========== Valores Fijos ======= */
	$("#nFolioPagoDirecto").val($("#nFolioPago").val());
	$("#fAplicacion").val($("#fechaAplicacion").val());
	muestraBotonCalculo();
	
	var idEstatus = obtenPagoEstatus();
	if (idEstatus == 0) {

		/* =========== Catalogos =========== */
		querySelectPost("CAT_TIPO_OPERACIONRead", "catIdTipoOperacion", {
			async : false
		});

		cargaDestinoGasto();

	}
}

function limpiaSelect(idSelect, defaultVal, defaultText) {
	$("#" + idSelect).empty();
	$("#" + idSelect).append('<option value="' + defaultVal + '" selected="selected">' + defaultText + '</option>');
}

function cat_beneficiario() {
	var formName = "formPagos";
	var inputName = "cTipoRfc";
	var inputRFCTarget = "cIDRFC";
	var inputDRFCTarget = "cnombre";

	$("#cTipoRfc").val("1,2,3");
	window.open('../Generador/CatalogoBeneficiariosPDNomina.jsp?formName=' + formName + '&nombreArchivoPadre=PAGODIRECTO&inputName=' + inputName + '&inputRFCTarget=' + inputRFCTarget + '&inputDRFCTarget=' + inputDRFCTarget, 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');	
}

function cargaCtaBancariaRFC() {
	$("#campoRFC").val($("#cIDRFC").val());
	$("#rfc").val($("#cIDRFC").val());
	$("#CTAB").val("");

	queryFormPost({
		queryName : "esProveedorExtranjero",
		async : false,
		callback : function() {
			limpiaSelect("ctaBancaria", "", "Seleccione la cuenta bancaria");

			querySelectPost({
				queryName : 'cargaCtaBancariasRFC',
				targetObjectId : 'ctaBancaria',
				async : false,
				callback : function() {
					$("#ctaBancaria").change();
				}
			});

		}
	});

}

function muestraDivFacturas() {
	if (conceptoMinimoCapturado()) {
		creaDiagloFacturas(true);
		return true;
	} else {
		return false;
	}
}

function precapturaCompleta() {
	var msg = "";

	if ($.trim($("#DESTINO_GASTO").val()) == "" || $.trim($("#DESTINO_GASTO").val()) == "-1")
		msg += "\nSeleccione el tipo de destino.";
	if ($.trim($("#concepto").val()) == "")
		msg += "\nCapture el concepto del pago.";
	if ($.trim($("#cIDRFC").val()) == "")
		msg += "\nSeleccione el beneficiario del pago.";
	if ($.trim($("#cIDRFC").val()) != "" && $.trim($("#ctaBancaria").val()) == "")
		msg += "\nEl beneficiario no cuenta con cuenta bancaria autorizada. Reporte al administrador.";
	var facturaCapturdas = getFacturasCapturadas();

	if (facturaCapturdas == 0)
		msg += "\nDebe cargar las facturas del pago.";

	var msgDif = getDiferenciaPagoCFDI();
	if (msgDif != "")
		msg += "\n" + msgDif;

	//Valida que si tiene PPD se haya capturado el correo
	var mensaje = validaCapturaCorreo();
	if (mensaje != "")
		msg += "\n" + mensaje;
	
	return msg;
}

function muestraBotonCalculo(){
	$("#tieneCedular").val("0");
		
		queryFormPost("tieneImpuestoCedular5Mil", {async : false});
		
		if( $("#tieneCedular").val() == "0" ){
			$("#calcularBtn").hide();
		}else{
			//document.getElementById(calcularBtn).style.visibility = "visible"; 
			$("#calcularBtn").show();
		}		
}

function revisaTipoFacturas(){
	queryFormPost("readEsPPD", {async : false});
	
		if( $("#esPPD").val() != "0" ){
			Swal.fire ('IMPORTANTE', 'La factura adjunta tiene el metodo de pago PPD por lo que es indispensable posteriormente solicitar al proveedor el CFDI de complemento de pago.', 'warning');
			creaDialogCorreo(true);
			$("#capturaCorreo").show();
		} else {
			$("#capturaCorreo").hide();
		}
}

function validaCapturaCorreo(){
	let mensaje = "";
	if( $("#esPPD").val() != "0" &&  $("#correoActual").val() ==""){
		mensaje = "No se capturo el correo para seguimiento y la(s) factura(s) son PPD"
	}
	
	return mensaje;
}
function calcularImpuesto(){
	queryFormPost("consultaImpuesto5", {async : false});
	
	queryFormPost("actualizarImporte", {async : false});
	Swal.fire ('Importe Actualizado', 'Se actualizó el importe de las facturas', 'success');
}

function creaDiagloFacturas(abrir) {
	$("#dialog-validaFact").dialog({
		autoOpen : false,
		height : 600,
		width : 950,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				var aData = oTablevFact.fnGetData();
				$(this).dialog("close");
			},
			"Cancelar" : function() {
				bClicBtn = true;
				$(this).dialog("close");
			}
		},
		
		close : function() {
			muestraBotonCalculo();
			revisaTipoFacturas();
			parent.document.getElementById("pb_save").disabled = false;
		},
		open : function() {
			$("#uploadFacturasDiv").show();
			leeDatosFactura();
			parent.document.getElementById("pb_save").disabled = true;
		}
	});

	if (abrir) {
		$("#dialog-validaFact").dialog("open");
	}
}

function creaDialogCorreo(abrir) {
	$("#dialog-actualizaCorreo").dialog({
		autoOpen : false,
		height : 270,
		width : 600,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				validarCorreo();
			},
			"Cancelar" : function() {
				$(this).dialog("close");
			}
		},
		open : function() {
			mostrarCorreo();
			
		}
	});

	if (abrir) {
		$("#dialog-actualizaCorreo").dialog("open");
	}
}


function mostrarCorreo(){
	queryFormPost("mostrarCorreoRead",{async: false});
} 
function validarCorreo(){
        
    emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    
    if (emailRegex.test($("#correoActual").val())) {
		$("#correo").val($("#correoActual").val());
		//Actualizar datos y cerrar
		if($("#nombreCorreo").val()==""){
			Swal.fire("Capturar Nombre", "Debe capturar el nombre a la cual se dirigirá el correo", "warning")
		} else {
			$("#paternoCorreo").val($("#aPCorreo").val());
			$("#maternoCorreo").val($("#aMCorreo").val());
			$("#nCorreo").val($("#nombreCorreo").val());
			$("#cCargo").val($("#cargo").val());
			queryFormPost("updateCorreoActualizado",{async: false});
			$("#dialog-actualizaCorreo").dialog("close");
		}
	} else {
     	Swal.fire("Correo Invalido", "El correo no es valido, intente de nuevo", "error");
     	return;
    }
}
function guardaEncabezadoParcial() {
	var exito = false;
	$("#accion").val("SAVE");
	retiraFormatoMoneda();
	$.ajax({
		url : '../egresos/SAVE',
		type : "POST",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {

			} else {
				alert("No es posible continuar debido al siguiente error: " + json.data_1.result)
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		}
	});

	$("#accion").val("");
	formatoMoneda();
	return exito;

}



function togleDivFacts(nIdDiv) {
	if (nIdDiv == 0) {
		$('#uploadFacturasFrm').attr('src', "../Generador/UploadFacturas.jsp?tipo_pago=PAGODIRECTO&RFC=" + $("#rfc").val());
		leeDatosFactura();
		creaDTFacturas();
	} else {
		$('#uploadFacturasFrm').attr('src', "../Generador/UploadFacturas.jsp??tipo_pago=PAGODIRECTO&RFC=" + $("#rfc").val());
		$("#uploadFacturasDiv").show();
	}

}

function Sinfrmt(fld) {
	var valcol = quitaFmt($(fld).val());
	$(fld).val(valcol);
}

function retiraFormatoMoneda() {
	$(".money").each(function() {
		Sinfrmt(this);
	});
}
function formatoMoneda() {
	$(".money").each(function() {
		Sinfrmt(this);
	});
}

function cambiafrmt(fld) {
	$(fld).formatCurrency();
}

function conceptoPagoDirectoRead() {
	var error = null;

	queryFormPost({
		queryName : "conceptoPagoDirectoRead",
		async : false,
		callback : function() {
			try {
				//alert("idDestinoGasto: " + $("#DESTINO_GASTO").val() );
				$("#DESTINO_GASTO").val($("#idDestinoGasto").val());
				$("#DESTINO_GASTO").change();
				cargaCtaBancariaRFC();
			} catch (err) {
				alert("Ocurrio el siguiente error y no se podra continuar: " + err.message);
				error = err;
			}
		}
	});

	if (error)
		throw error;
}

/**
 * Comprueba que se ha capturado la informacion minima requerida para continuar con el pago.
 */
function validacionesConcepto() {
	var msg = precapturaCompleta();
	if (msg != "") {
		Swal.fire('Para continuar debe corregir lo siguiente:', msg, 'warning');
		return false;
	} else {
		return true;
	}

}

/**
 * Devuelve el total de facturas capturadas.
 * 
 */
function getFacturasCapturadas() {
	$("#nFacturasCapturadas").val(0);
	queryFormPost("facturasCapturadasRead", {
		async : false
	});
	return parseInt(($("#nFacturasCapturadas").val() == "" ? 0 : $("#nFacturasCapturadas").val()), 10);
}

/**
 * Devuelve si hay diferencia entre el pago y la factura.
 * 
 */
function getDiferenciaPagoCFDI() {
	var consultado = false;
	$("#diferenciaCFDIPago").val("0");
	var diferenciaCFDIPago = 0.00;
	var msgTxt = "";

	queryFormPost({
		queryName : "diferenciaPagoDirectoCFDI",
		async : false,
		callback : function() {
			consultado = true;
			if ($("#diferenciaCFDIPago").val() == "")
				$("#diferenciaCFDIPago").val("0");
			diferenciaCFDIPago = parseFloat($("#diferenciaCFDIPago").val());
			if (diferenciaCFDIPago != 0)
				msgTxt = "Existe diferencia de " + diferenciaCFDIPago + " entre el pago y las facturas.";
		}
	});

	if (!consultado)
		msgTxt = "No fue posible consultar diferencias entre CFDI y Pagos";

	return msgTxt;
}


function cargaDestinoGasto() {
	querySelectPost("CatalogoDGastoNominaRead", "DESTINO_GASTO", {
		async : false
	});
}

function conceptoMinimoCapturado() {
	var msg = "";
	var completado = true;

	if ($.trim($("#catIdTipoOperacion").val()) == "" || $.trim($("#catIdTipoOperacion").val()) == "-1") {
		msg += "\nSeleccione el tipo de pago directo.";
		completado = false;
	}
	if ($.trim($("#DESTINO_GASTO").val()) == "" || $.trim($("#DESTINO_GASTO").val()) == "-1") {
		msg += "\nSeleccione el tipo de destino.";
		completado = false;
	}
	if ($.trim($("#concepto").val()) == "") {
		msg += "\nCapture el concepto del pago.";
		completado = false;
	}
	if ($.trim($("#cIDRFC").val()) == "") {
		msg += "\nSeleccione el beneficiario del pago.";
		completado = false;
	}
	if ($.trim($("#cIDRFC").val()) != "" && $.trim($("#ctaBancaria").val()) == "") {
		msg += "\nEl beneficiario no cuenta con cuenta bancaria autorizada. Reporte al administrador.";
		completado = false;
	}

	if (msg != "")
		Swal.fire("Para continuar debe corregir lo siguiente:",  msg, "info");

	return completado;
}


function conceptoTerminado() {
	var exito = false;
	$.ajax({
		url : '../egresos/resumenConcepto',
		type : "GET",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				for (var k in resumen) {
					$("#" + k).append(resumen[k]);
				}
				$("#Concepto").show();
				$("#TabConcepto").show();
				$("#resumenCaratula").show();
				$("#capturaConceptoPago").hide();
				creaDTResumenFacturas();
			} else {
				alert("No es posible continuar debido al siguiente error: " + json.errorList);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		}
	});
	return exito;
}

function leeImporteBruto() {
	
	var ejecutado = false;

	queryFormPost({
		queryName : "importeBrutoPDNRead",
		async : false,
		callback : function() {
			ejecutado = true;
		}
	});
	
	if( !ejecutado )
		throw "No se encontro el importe bruto";
	
}