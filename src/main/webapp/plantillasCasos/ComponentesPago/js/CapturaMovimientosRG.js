var ejecutadoInitMov = false;
let montoRet;
let montoRetFact;
let montoRetXCapturar;
var myModal;
function iniciaCapturaMovimientos() {
		let ejercer = Number(quitaFmt($("#mImporteBruto").val()));
		$("#mMontoEjercer").val(ejercer);	
		revisaRetenciones();
		$("#mMontoRetFact").val($("#mImporteRetencion").val());
		
		creaDTEPs();
		createDTCalendario();
		createConcepto();
		myModal = new bootstrap.Modal(document.getElementById('dlg-CalendarioMontos'), 'data-bs-backdrop');
		ejecutadoInitMov = true;
}

function createDTCalendario() {
	var cWhere = " nFolioPago=" + $("#nFolioPago").val() + " AND cTipoPago='" + $("#cTipoPago").val() + "'";

	oTableSaldos = $("#tblCalendario").dataTable(
		{
			"bDestroy" : true,
			fnDrawCallback : function() {},
			//bAutoWidth : true,
			oLanguage : es_mx,
			bServerSide : true,
			"bInfo" : false,
			"bFilter" : false,
			"bSort" : true,
			"bPaginate" : false,
			"sScrollX" : "100%",
			"sScrollY" : "100px",
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
				sName : "mImporteRetencion",
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

		var ep = $("#tblCalendario").dataTable().fnGetData(event.target.parentNode)[0];
		
		$("#epBorrar").val(ep);

		if (confirm("Esta seguro de eliminar la clave\n" + ep + "\n del calendario?")) {
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

function validaRetenciones(){
	if ($("#montoRetencion").val() == "")
		$("#montoRetencion").val("0.00");
	
	let retCapturadas = parseFloat(quitaFmt($("#montoRetencion").val()));	
	if (montoRetFact < retCapturadas ){
		Swal.fire("Error","Las retenciones capturadas no puede ser mayor al importe de retenciones en facturas.","error");
		return false;
	}
}

function guardaMontos() {
	if ($("#montoEjercer").val() == "")
		$("#montoEjercer").val("0.00");

	var montoCapturado = parseFloat(quitaFmt($("#montoEjercer").val()));
	var montoDisponible = parseFloat(quitaFmt($("#disponibleEPTotal").val()));
	var montoTotalEjercer = parseFloat(quitaFmt($("#mMontoEjercer").val()));
	
	var ep = $("#epShow").val();

	if (montoCapturado <= 0) {
		Swal.fire("Error","Debe capturar el monto a ejercer y este debe ser positivo.","error");
		$("#montoEjercer").focus();
		return false;
	} else if (montoCapturado > montoDisponible) {
		Swal.fire("Revise","El monto a ejercer debe ser menor o igual al monto disponible","error");
		$("#montoEjercer").focus();
		return false;
	} else {

		$("#importeConsulta").val(montoCapturado);
		var correcto = false;
		var totalCalendarizado = 0.00;
		let totalRet = 0.00;
		var msg = "";

		queryFormPost({
			queryName : "calendarioAcumRead",
			async : false,
			callback : function() {
				if ($("#totalCalendarizado").val() == "") {
					msg = "No se pudo consultar el total calendarizado";
				} else {
					totalCalendarizado = parseFloat(quitaFmt($("#totalCalendarizado").val()));
					if (totalCalendarizado > montoTotalEjercer) {
						msg = "Con el monto capturado supera el monto total del pago.";
					} else {
						correcto = true;
					}
				}

			}
		});
		
		queryFormPost({
			queryName : "calendarioRetAcumRead",
			async : false,
			callback : function() {
				if ($("#totalRetencion").val() == "") {
					msg = "No se pudo consultar el total de las retenciones";
				} else {
					totalRet = parseFloat(quitaFmt($("#totalRetencion").val()));
					if (totalRet > montoRetFact) {
						msg = "Con el monto capturado supera el monto de las retenciones.";
					} else {
						correcto = true;
					}
				}

			}
		});

		if (!correcto) {
			Swal.fire("Revise",msg,"info");
			return false;
		}
	}
	guardaImporteCalendario(ep, montoCapturado);
}

function guardaImporteCalendario(ep, importeEP) {
	//if (confirm("Esta seguro de guardar el monto a ejercer?")) {
		$("#epCalendario").val(ep);
		$("#importeCalendario").val(importeEP);

		var msg = "";
		$.ajax({
			url : '../viaticos/guardaCalendario',
			type : "POST",
			dataType : 'json',
			data : {				
				ep :  			$("#epShow").val(),
				mes :  			$("#mesConsulta").val(),
				nFolioPago :  	$("#nFolioPago").val(),
				cTipoPago : 	$("#cTipoPago").val(),
				cIdContrato : 	$("#cidcontrato").val(),
				montoEjercer : 	quitaFmt($("#montoEjercer").val()),
				montoRetencion : 	quitaFmt($("#montoRetencion").val()),
				disponibleEP :  quitaFmt($("#disponibleEPTotal").val()),
				cEvento : 		$("#cEvento").val()
			},
			async : false,
			success : function(json) {
				exito = json.success;
				if (exito) {
					let mensaje = json.messageList[0];
					Swal.fire('OK', mensaje, 'success');
				} else {
					var errores = json.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						msg = msg + errores[cnt] + "\n";
					}
					Swal.fire("Intente nuevamente","No se guardo el registro de calendario debido al error: \n" + msg, 'warning' );
				}

				creaDTEPs();
				createDTCalendario();
				$('#dlg-CalendarioMontos').modal('hide');
				
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				msg = "Error obteniendo retenciones iniciales";
			}
		});
	//}

}

function leeTotalCalendarizado() {
	queryFormPost({
		queryName : "montoCalendarizadoRead",
		async : false,
		callback : function() {
			let montoTotalEjercer = parseFloat(quitaFmt($("#mMontoEjercer").val()));
			let montoCalendario = parseFloat(quitaFmt($("#mMontoCalendarizado").val()));
			let montoRestante = (montoTotalEjercer - montoCalendario).toFixed(2);
			montoRet= parseFloat(quitaFmt($("#mMontoRetenciones").val()));
			montoRetFact= parseFloat(quitaFmt($("#mMontoRetFact").val()));
			montoRetXCapturar= (montoRetFact - montoRet).toFixed(2);;

			montoTotalEjercer = currencyFormatter({currency:'USD', value: montoTotalEjercer });
			$("#mMontoEjercer").val(montoTotalEjercer);	
			
			montoCalendario = currencyFormatter({currency:'USD', value: montoCalendario });
			$("#mMontoCalendarizado").val(montoCalendario);
			
			montoRestante = currencyFormatter({currency:'USD', value: montoRestante });
			$("#mMontoPorEjercer").val(montoRestante);
			
			montoRet = currencyFormatter({currency:'USD', value: montoRet });
			$("#mMontoRetenciones").val(montoRet);	
			
			montoRetFact = currencyFormatter({currency:'USD', value: montoRetFact });
			$("#mMontoRetFact").val(montoRetFact);		
			
			montoRetXCapturar = currencyFormatter({currency:'USD', value: montoRetXCapturar });
			$("#mMontoRetPendientes").val(montoRetXCapturar);		
			}
	});
}

function validacionesCalendario() {
	var correcto = false;
	queryFormPost({
		queryName : "montoPendienteRGRead",
		async : false,
		callback : function() {
			var diferencia = parseFloat(quitaFmt($("#diferencia").val()));
			if (diferencia == 0.00) {
				correcto = actualizaEstatus();
			} else {
				Swal.fire("Capture","Falta por cubrir: " + diferencia + " para completar el pago.","info");
			}
		}
	});
	return correcto;
}


function movimientosTerminado() {
	var exito = false;
	$.ajax({
		url : '../egresos/resumenCalendario',
		type : "GET",
		dataType : 'json',
		data : $("#formPagos").serialize(),
		async : false,
		success : function(json) {
			exito = json.success;
			if (exito) {
				var resumen = json.resumen;
				var classRow = "";
				for (var idx = 0; idx < resumen.length; idx++) {
					if (idx % 2 == 0)
						classRow = "even";
					else
						classRow = "odd";

					$('#resumenCalendario > tbody:last-child').append('<tr id="rwMovto' + idx + '" class="' + classRow + '"></tr>');
					$("#rwMovto" + idx).append('<td class="infoResumen">' + resumen[idx].EP + '</td>');
					$("#rwMovto" + idx).append('<td class="infoResumenMonto">' + resumen[idx].mImporteBruto + '</td>');
					$("#rwMovto" + idx).append('<td class="infoResumen">' + resumen[idx].tipoConcepto + '</td>');
					$("#rwMovto" + idx).append('<td class="infoResumen">' + resumen[idx].tipoMovimiento + '</td>');
				}

				$("#Movimientos").show();
				$("#TabMovimientos").show();
				$("#CalendarioResumenDiv").show();
				$("#capturaMovimientos").hide();
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

function cambioFF() {
	 $('#cFuenteFinanciamiento').prop('disabled', true);
	creaDTEPs();
}

function creaDTEPs() {
	var diaUsarMesSiguiente;
	var dia = $("#fAplicacion").val().split("/")[0];
	var mes = $("#fAplicacion").val().split("/")[1];
	var folio = $("#nFolioPago").val();
	var login = $("#login").val();
	var fuenteFinanciamiento = $("#cFuenteFinanciamiento").val();
	
	queryFormPost({
		queryName: "diaUsarMesSiguienteRead",
		async: false,
		callback: function() {
			if ($("#gp_valor").val() && !isNaN(Number($("#gp_valor").val()))) {
				diaUsarMesSiguiente = Number($("#gp_valor").val());
			}
		}
	});
	
	if (Number(dia) > diaUsarMesSiguiente ) {
		mes = Number(mes) + 1;
	}
	
	//var cidcontrato = $("#cidcontrato").val();
	var funcionSQL = "fn_SaldoDisponibleRG (" + mes + "," + folio + "," + "'" + login + "'" + "," + fuenteFinanciamiento + ")";
	/*
	if (fuenteFinanciamiento == 1 ) {
		funcionSQL = "fn_SaldoCompromisoRG (" + mes + "," + folio + ", '" + login + "' ," + fuenteFinanciamiento + ",'"  +  cidcontrato +  "')";
	} 
	*/
	oTableEPs = $('#tblEP').dataTable(
			{
				"bPaginate" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : true,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"sPaginationType": "full_numbers",
				sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=" + funcionSQL,
				aoColumns : [ {
					sName : "ep"
				}, {
					sName : "mSaldoEnero"
				}, {
					sName : "mSaldoFebrero"
				}, {
					sName : "mSaldoMarzo"
				}, {
					sName : "mSaldoAbril"
				}, {
					sName : "mSaldoMayo"
				},{
					sName : "mSaldoJunio"
				},{
					sName : "mSaldoJulio"
				},{
					sName : "mSaldoAgosto"
				},{
					sName : "mSaldoSeptiembre"
				},{
					sName : "mSaldoOctubre"
				},{
					sName : "mSaldoNoviembre"
				},{
					sName : "mSaldoDiciembre"
				}],
				oLanguage : es_mx
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

		var ep = $("#tblEP").dataTable().fnGetData()[currIndex][0];
		$("#epShow").val(ep);
		$("#epConsulta").val(ep);
		$("#mesConsulta").val(mes);
		queryFormPost("saldoDispobleAcumRead", {async : false});

		if(Number(quitaFmt($("#mMontoPorEjercer").val())) > 0 ) {
			myModal.show();
			$("#montoEjercer").val("0.00");
			$("#montoEjercer").focus();
		} else {
			Swal.fire("Terminado", "El total ya fue calendarizado, no se pueden agregar mas claves. Presione el botón AVANZAR.", "info");
		}
	});
}

function createConcepto() {

		oTableSaldos = $("#tblConceptosRG").dataTable(
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
			"sPaginationType": "full_numbers",
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=v_conceptosComprobacion",
			bProcessing : true,
			//sPaginationType : "full_numbers",
			bJQueryUI : true,
			aoColumns : [ {
				sName : "cTipo"
			}, {
				sName : "Partidas"
			}, {
				sName : "cNombreAp"
			}]
		});
}