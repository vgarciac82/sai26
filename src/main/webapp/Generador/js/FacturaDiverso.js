/**
 * Calculo de iva, retenciones y amortizaciones.
 */
var ivaAplicable = 1.0;

function calculaIVA() {
	
	var esSAIAlterno = $("#esSAIAlterno").val() === "true";
	
	queryFormPost("porcIVARead", { async: false });

	queryFormPost({
		queryName: "leeImpuestosContrato",
		async: false,
		callback: function() {
			ivaAplicable = $("#iva").val();
		}
	});


	let mAmortizacionAnticipo = (Number(quitaFmt($("#mAmortizacionAnticipo ").val())) * 100) / 100;

	suma = Number(quitaFmt($("input[id='mImporteBruto']").val())) + Number(quitaFmt($("input[id='mOtrosImpuestos']").val()));
	suma -= Number(quitaFmt($("input[id='mImporteSancion']").val()));
	suma += Number(quitaFmt($("input[id='mImporteDevolucion']").val()));
	
	/*Aqui guarda el ( Importe Bruto + Otros Impuestos) - Sanciones + Devoluciones*/
	$("#subTotal_1").val(suma.toFixed(2));
	suma -= mAmortizacionAnticipo ;
	validaPagoConPenalizacion();
	
	if (mAmortizacionAnticipo == 0){
		$("#subTotal_2").val($("#subTotal_1").val());
	}

	//JGDS si tiene sanciones,devoluciones, otros impuesto o amortizacion del anticipo, sino deja el importe de iva que ya traia
	if ($("#subTotal_1").val() != $("#subTotal_2").val()) {
		var res_iva = parseFloat($("input[id='nPorcIVAAplicable']").val() / 100) * (parseFloat($("input[id='subTotal_2']").val()));
		res_iva = res_iva.toFixed(2);
		$("#mImporteIVA").val(res_iva);
		$("#mImporteMasIva").val(Number($("#mImporteIVA").val()) + Number($("#subTotal_2").val()));
	}

	suma += Number($("input[id='mImporteIVA']").val());

	if (mImporteFlete23 == 1) {
		DCD_IVADES = (Number($("#subTotal_2").val()) * 0.106667);
		DCD_IVADES = Number(DCD_IVADES).toFixed(2);
		//		DCD_IVADES =  (Number($("#mImporteIVA").val()) * 2) / 3;
	}

	var mImpteCedular = 0.00;

	if (Number($("#lHaySaldoAnticipo").val()) == 1
		&& $("#mAmortizado").val() == '0.0000') {

		$("#DCD_MIL2").val(0);
		$("#DCD_MIL5").val(0);
		$("#DCD_RETENCION").val(0);
		$("#DCD_IVADES").val(0);
	} else {
		CalculaRetencionesG();
		$("#DCD_MIL2").val(DCD_MIL2res);
		$("#DCD_MIL5").val(DCD_MIL5res);
		$("#sumareten").val(DCD_RETENCIONres);
		DCD_MIL2 = parseFloat($("#DCD_MIL2").val() * parseFloat($("#subTotal_1").val()));
		DCD_MIL5 = parseFloat($("#DCD_MIL5").val() * $("#subTotal_1").val());

		if ($("#porcCedular").val() == "1") {
			mImpteCedular = parseFloat($("#mRetImpuestoCedular").val() * $("#subTotal_1").val());
			mImpteCedular = Number(mImpteCedular).toFixed(2);

		} else {
			if (Number($("#mRetImpuestoCedular").val()) > 0) {
				mImpteCedular = parseFloat($("#mRetImpuestoCedular").val() * $("#subTotal_1").val());
				mImpteCedular = Number(mImpteCedular).toFixed(2);
			}
		}
		DCD_RETENCION = parseFloat($("#sumareten").val() * $("#subTotal_1").val());
		DCD_RETENCION = Number(DCD_RETENCION).toFixed(2);
		//		DCD_RETENCION = parseFloat(DCD_RETENCION) + parseFloat(mImpteCedular);
		DCD_RETENCION = Number(DCD_RETENCION).toFixed(2);

		$("#DCD_MIL2").val(Math.round(DCD_MIL2 * 100) / 100);
		$("#DCD_MIL5").val(Math.round(DCD_MIL5 * 100) / 100);
		$("#DCD_RETENCION").val(Math.round(DCD_RETENCION * 100) / 100);
		$("#DCD_IVADES").val(Math.round(DCD_IVADES * 100) / 100);

	}

	$("#mImpEjercer").val(Math.round(Number(suma) * 100) / 100);

	ayuda1 = Number($("#sumareten").val()) * Number($("input[id='subTotal_1']").val());
	ayuda1 = Number(ayuda1).toFixed(2);

	if ($("#cCentroContable").val() == "28" || $("#cCentroContable").val() == "21") {
		ayuda1 = parseFloat(ayuda1) + parseFloat(mImpteCedular);
	}

	ayuda1 = Number(ayuda1).toFixed(2);

	ayuda2 = parseFloat(ayuda1) + parseFloat($("#DCD_MIL2").val())
		+ parseFloat($("#DCD_MIL5").val())
		+ parseFloat($("#DCD_IVADES").val());
	ayuda2 = Number(ayuda2).toFixed(2);

	$("#mImporteRetencion").val(Math.round(Number(ayuda2) * 100) / 100);

	if (Number(ayuda2) > 0 && Number($("#mImporteFacturas").val()) > 0) {
		$("#mImporteRetenPago").val("0");
		queryFormPost("importeRetencionPDIVERSO_Read", { async: false });

		var retencionPago = $("#mImporteRetenPago").val();
		retencionPago = Number(retencionPago).toFixed(2);
		var retenciones = $("#mImporteRetencion").val();
		retenciones = Number(retenciones).toFixed(2);

		var diferenciaRet = 0;
		diferenciaRet = Number(retenciones) - Number(retencionPago);
		diferenciaRet = Number(diferenciaRet).toFixed(2);

		if (diferenciaRet == -0.01 || diferenciaRet == 0.01) {
			$("#mImporteRetencion").val(Math.round(Number(retencionPago) * 100) / 100);
		}
	}

	suma -= Number($("input[id='mImporteRetencion']").val());

	var penalizacion = 0.00;
	penalizacion = (parseFloat($("#mImportePenalizacion").val())) * 100 / 100;
	suma -= Number(penalizacion);

	$("#mImporteNeto").val(Math.round(Number(suma) * 100) / 100);

	acumula = Number($("#acumulado").val()) + Number($("#mImpEjercer").val());
	acumula = acumula.toFixed(2);


	if ($("#PagoAMF").val() != "" && $("#mImporteNeto").val() != "0") {
		if (Number($("#mImporteNeto").val()) != Number($("#PagoAMF").val())) {
			$("#mImporteBruto").val(0);
			Swal.fire("Verifique", "Importe Neto es diferente al Importe AMF", "warning");
			calculaIVA();
		}
	}

	/*
	 * -----------------------------------------------------------------------------------------
	 * URVP.09092014 SE PONE A DOS DECIMALES LOS SIGUIENTES CAMPOS
	 */
	$("#nPorcIVAAplicable").val(Number($("#nPorcIVAAplicable").val()).toFixed(2));
	$("#saldoCed").val(Number($("#saldoCed").val()).toFixed(2));
	$("#mTotal").val(Number($("#mTotal").val()).toFixed(2));
	$("#saldoAnticipo").val(Number($("#saldoAnticipo").val()).toFixed(2));
	// ------------------------------------------------------------------------------------------

	/*
	 * EHR2015 Si es recepcion con factor de amortizacion se permite la captura.
	 */
	if ($("#tipoAmortizacion").val() == "1") {
		$("#mAmortizacionAnticipo")[0].readOnly = false;
		$("#mAmortizacionAnticipo").removeClass("notEditable");

	} else {
		$("#mAmortizacionAnticipo")[0].readOnly = true;
		$("#mAmortizacionAnticipo").addClass("notEditable");
	}



	if (penalizacion > 0.00) {
		var neto = parseFloat($("#mImporteNeto").val()).toFixed(2);
		var retenciones = $("#mImporteRetencion").val();
		retenciones = Number(retenciones).toFixed(2);

		var ejercer = parseFloat(Number(neto) + Number(penalizacion) + Number(retenciones)).toFixed(2);

		$("#mImpEjercer").val(ejercer);
		$("#mImporteMasIva").val(ejercer);
	}

}

function agregaAmortizacion(calcularAmortizacion) {
	let importeIVA = 0;
	/*
	 * EHR2015 Si es recepcion con factor de amortizacion se permite la captura.
	 */
	if ($("#tipoAmortizacion").val() != "1") {

		if (calcularAmortizacion) {
			queryFormPost("buscaAnticipoPagoDiversoRead", { async: false });

			if (Number($("#saldoAnticipo").val()) - Number($("#mAmortizacionAcumulado").val()) != 0) {
				
					if ($("#tipoAmortizacion").val() != "1") {
				
						if ($("#lHaySaldoAnticipo").val() == 1
							&& $("#mAmortizado").val() == '0.0000') {
							$("#nPorcAmortizacion").val(0);
						} else {
							var mAmortizacionAnticipo = $("#mAmortizacionAnticipo").val();
							var importeBrutoNuevo = (Number(importeRecepcionOriginal) + Number(mAmortizacionAnticipo )) * 100 / 100;
							$("#mImporteBruto").val(importeBrutoNuevo.toFixed(2));
							
							var v_amortanticmasiva = Number(quitaFmt($("#mAmortizacionAnticipoMasIva").val()));
							v_amortanticmasiva = v_amortanticmasiva.toFixed(2);
				
							$("#mAmortizacionAnticipoMasIva").val(v_amortanticmasiva);
						}
				
						$("#subTotal_1").val(( importeBrutoNuevo ).toFixed(2));
						$("#subTotal_2").val(( importeBrutoNuevo - mAmortizacionAnticipo ).toFixed(2));
						importeIVA = parseFloat(Number($("#subTotal_2").val()) * 0.16).toFixed(2);
						$("#mImporteIVA").val(importeIVA);
						
					} else {
				
						$("#mImporteBruto").val(importeRecepcionOriginal);
						
						suma = Number(quitaFmt($("input[id='mImporteBruto']").val())) + Number(quitaFmt($("input[id='mOtrosImpuestos']").val()));
						
						if( !esSAIAlterno )
							suma = suma + Number(quitaFmt($("input[id='mAmortizacionAnticipo']").val()));
						 
						suma -= Number(quitaFmt($("input[id='mImporteSancion']").val()));
						suma += Number(quitaFmt($("input[id='mImporteDevolucion']").val()));
						
						$("#subTotal_1").val(suma.toFixed(2));
				
						var importeSubtotal = 0;
						if (mAmortizacionAnticipo > 0) {
				
							if( !esSAIAlterno )			
								$("#mImporteBruto").val( Number(importeRecepcionOriginal) + Number( mAmortizacionAnticipo) );
							else 
								$("#mImporteBruto").val(importeRecepcionOriginal)
								
							importeSubtotal = ( suma - mAmortizacionAnticipo ) * 100 / 100;
							$("#subTotal_2").val(importeSubtotal.toFixed(2));
				
							
							importeIVA = parseFloat(Number($("#subTotal_2").val()) * 0.16).toFixed(2);
							$("#mImporteIVA").val(importeIVA);
				
						} else {
							$("#subTotal_2").val(suma.toFixed(2));
						}
				
					}
			}
		} else {
			$("#importeEjercer").val($("#mImpEjercer").val());
		}
	}
	
	if ($("#mAmortizacionAnticipo").val() =="") {
		$("#mAmortizacionAnticipo").val("0")
	}
}

function FiltroMovimientos() {
	$("#montoDev").val(quitaFmt($("#montoDev").val()));
	if ($("#ep").val() != "") {

		var monto = $("#montoDev").val();
		var esEPCap5Mil = $("#ep").val();
		esEPCap5Mil = esEPCap5Mil.substring(31, 32);

		if (esEPCap5Mil != "5") {
			//se valida si la ep capturada pertene a CECFOR y el tipo de concepto es "ALMACEN".
			if (!validaEPsCECFOR()) {
				$("#esEPCECFOR").val("");
				return;
			}
		}

		// URVP.16062014-Validacion de mas de un punto decimal
		if (parseInt(monto.indexOf("."), 10) != parseInt(monto.lastIndexOf("."), 10)
			|| parseInt(monto.indexOf("-"), 10) != parseInt(monto
				.lastIndexOf("-"), 10) || parseInt(monto.indexOf("-"), 10) > 0
			|| monto == "-" || monto == "-." || monto == "" || monto == ".") {
			Swal.fire("Revise", "Revisa que el importe sea correcto", "warning");
			return;
		}
		if (parseFloat($("#montoDev").val()).toFixed(2) < 0) {
			Swal.fire("Revise", "No se puede agregar un movimiento con importe Negativo", "warning");
			return;
		} else if (parseFloat($("#montoDev").val()).toFixed(2) == 0) {
			Swal.fire("Revise", "No se puede agregar un movimiento con importe 0", "warning");
			return;
		}
	} else {
		Swal.fire("Capture", "Favor de seleccionar la EP", "warning");
		return;
	}

	var msg = "";
	try {
		msg = registraCalendarioEP($("#ep").val(), monto);
	} catch (err) {
		msg = "generando calendario: " + err.message;
	}

	if (msg != "") {
		Swal.fire("Ocurrio el siguiente error ", msg, "error");
		return false;
	}
	// se valida si la partida que se captura pertenece a gastos de representacion (38501). para desplegar mensaje.
	validaPartidaGtosRepresentacion();

	// URVP.17062017 se deja el campo a dos decimales debido a que en el grid se
	// mostraba dos decimales en BD si registraba todos los decimales que estaban
	$("#montoDev").val(parseFloat($("#montoDev").val()).toFixed(2));

	var vOGT = $("#ep").val();
	vOGT = vOGT.substring(31, 36);
	$("#cOBGT").val(vOGT);

	if ($("#TIPO_CONCEPTO").val() == "AL" || vOGT == $("#partida").val()) {
		if ($("#altaAlmacen").val() == "") {
			Swal.fire("Capture", "Falta Capturar el Alta Almacenaria", "info");
			return;
		}

		if ($("#cAnioFactEP").val() == "") {
			Swal.fire("Capture", "Falta Capturar el Año de la Factura", "info");
			return;
		}

		if ($("#nFacturaEP").val() == "") {
			Swal.fire("Capture", "Falta Capturar el Numero de Factura", "info");
			return;
		}
	}

	$("#nombre").val($("#cobjetocontrato").val());
	$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
	/*
	 * SE HABILITA PARA TOMAR EL DATO
	 */
	document.getElementById("TIPO_CONCEPTO").removeAttribute("disabled", false);
	$("#cEvento").val("");
	queryFormPost("eventpEP3Read", { async: false });

	if ($("#cEvento").val() == "") {
		Swal.fire("Revise el evento", 'La cuenta no corresponde al concepto', "warning");
		return;
	}

	var mImporteADev = $("#montoDev").val();

	if ($("#destGasto").val() == "AL") {
		var cFactura = $("#nFacturaEP").val();
		$("#cValidaFactura").val($("#nFacturaEP").val());
		$("#cExiste").val("0");

		//Valida que la factura no exista en otro pago
		queryFormPost("ValidaFactPagosRead", { async: false });

		if (Number($("#cExiste").val()) > 0) {
			Swal.fire("Revise", "La Factura " + cFactura + " ya existe", "warning");
			return -1;
		} else {
			var aData = oTablevFact.fnGetData();
			var bExiste = false;
			for (var i = 0; i < aData.length; i++) {
				var cFactura = aData[i][1];
				if (cFactura == $("#nFacturaEP").val()) {
					bExiste = true;
				}
			}
			if (!bExiste) {
				$('#grdValidaFacturas').dataTable().fnAddData(
					["", $("#nFacturaEP").val(), $("#montoDev").val()]);
			}
		}
	}

	$("#cEvento").val("D_" + $("#cEvento").val());
	var mTotalEjercer = parseFloat($("#saldoCompM").val());
	var table = document.getElementById('grdRetClave');
	var rowCount = table.rows.length;
	for (var i = 1; i < rowCount; i++) {
		var row = table.rows[i];
		var elMesGrd = '';
		var elMontoGrd = '';
		try {
			var elMesGrd = row.cells[1].innerHTML;
			var elMontoGrd = row.cells[2].innerHTML;
			var mess;
		} catch (e) {
			null;
		}
		if (null != elMesGrd) {
			if (elMesGrd.toString() != "") {

				if (parseInt(elMesGrd.toString(), 10) < 10)
					mess = '0' + elMesGrd.toString();
				else
					mess = elMesGrd.toString();

				$("#cMes").val(mess);

				var vdcd_imp_bruto = Number(elMontoGrd.toString());
				vdcd_imp_bruto = vdcd_imp_bruto.toFixed(2);

				$("#DCD_IMP_BRUTO").val(vdcd_imp_bruto);

				// URVP.17062014 se hace el insert en el grid una ves que si se
				// cumple el if para que se agrege al insertar por BD realmente
				$("#montoDev").formatCurrency();
				$('#grdCompromisos').dataTable().fnAddData(
					[$("#codSIAFF").val(), $("#ep").val(),
					$("#DCD_IMP_BRUTO").val()]);
				$("#montoDev").val(mImporteADev);
				// FIN URVP.17062014 se hace el insert en el grid una ves que si
				// se cumple el if para que se agrege al insertar por BD
				// realmente

				if ($("#lHaySaldoAnticipo").val() == 0
					|| $("#mAmortizado").val() != '0.0000') {
					calculaRetencionesEP($("#DCD_IMP_BRUTO").val());
					actualSaldosDet(1);
				} else {
					$("#mImporteNetoEP").val($("#DCD_IMP_BRUTO").val());
				}

				var altaAlmacenresp = $("#altaAlmacen").val();

				mTotalEjercer = Number(mTotalEjercer) + Number(elMontoGrd.toString());
				mTotalEjercer = mTotalEjercer.toFixed(2);
				if (Number(mTotalEjercer) == Number($("#mImpEjercer").val())) {
					AjustaRetencionesD();
				}

				$("#altaAlmacen").val(
					$("#altaAlmacen").val() + "|" + $("#cAnioFactEP").val()
					+ "|" + $("#nFacturaEP").val());

				if (esIvaArrenda == 1) {
					$("#mImporteIvaArrenda").val($("#mImporteFlete23").val());
					$("#mImporteFlete23").val(0);
				} else {
					$("#mImporteIvaArrenda").val(0);
				}

				var aux = $("#mAmortizacionAnticipoMasIvaEP").val();
				var mAmortizacionEP = 0.00;
				mAmortizacionEP = $("#mAmortizacionAnticipo").val();
				var porcent = (vdcd_imp_bruto * 100 / Number($("#mImpEjercer").val())
					.toFixed(2));
				//mAmortizacionEP = Number(mAmortizacionEP * porcent / 100).toFixed(2);
				$("#mAmortizacionAnticipoMasIvaEP").val(mAmortizacionEP);
				/*
				 * URVP.09012014 SE RESTA LO AMORTIZADO PARA PONER EL IMPORTE
				 * SIN LA AMORTIZACION
				 */

				if ($("#tipoAmortizacion").val() == "1") {
					var dcdImpBruto = (parseFloat(vdcd_imp_bruto) + parseFloat(mAmortizacionEP)) * 100 / 100;
					$("#DCD_IMP_BRUTO").val(parseFloat(vdcd_imp_bruto));
					//mAmortizacionEP = Number (mAmortizacionEP * 1.16).toFixed(2); // SE COMENTA PARA REALIZAR PAGO DE MARTHA CON FACTOR DE AMORTIZACION
					mAmortizacionEP = Number(mAmortizacionEP * porcent / 100).toFixed(2);
					$("#mAmortizacionAnticipoMasIvaEP").val(mAmortizacionEP);
				}
				

				var otrosImpuestosEp = 0.00;
				otrosImpuestosEp = $("#mOtrosImpuestos").val();
				otrosImpuestosEp = Number(otrosImpuestosEp * porcent / 100).toFixed(2);
				$("#mOtrosImpuestosEP").val(otrosImpuestosEp);

				var mPenalizacion = 0.00;
				mPenalizacion = $("#mImportePenalizacion").val();

				if (mPenalizacion > 0) {
					$("#mPenalizacion").val("0");
				}

				queryFormPost("tFACTDIVERSOSDETALLECreate", { async: false });

				//se inserta otro renglo cuando es un pago con penalizacion				
				if (mPenalizacion > 0) {

					mPenalizacion = Number(mPenalizacion * porcent / 100).toFixed(2);
					$("#mPenalizacion").val(mPenalizacion);

					queryFormPost("tPAGODIVERSODetallePenalizacionCreate", {
						async: false
					});

					$("#mPenalizacion").val("0");
				}
				/*
				 * se regresa el valor que traia
				 */
				//$("#mAmortizacionAnticipoMasIvaEP").val(aux);
				$("#altaAlmacen").val(altaAlmacenresp);
				table.deleteRow(i);
				rowCount--;
				i--;
				// Para Conafor
				$("#numPaso").val("4");
				parent.document.getElementById("pb_save").disabled = true;
			} else {
				$("#montoDev").val('0');
				Swal.fire("Verifique!", "Favor de verificar que la EP tenga saldo comprometido para el Pago.", "warning");
				return;
			}
		}
	}

	validacion = parseFloat($("#saldoCompM").val())
		+ parseFloat($("#montoDev").val());
	validacion = validacion.toFixed(2);
	if (validacion > parseFloat($("#mImpEjercer").val())) {
		$("#montoDev").val("0");
		validacion = 0;
	} else {
		var totalacumulado = parseFloat($("#saldoCompM").val())
			+ parseFloat($("#montoDev").val());
		totalacumulado = totalacumulado.toFixed(2);
		$("#saldoCompM").val(totalacumulado);
		validacion = 0;

		if (parseFloat($("#saldoCompM").val()) == parseFloat($("#mImpEjercer")
			.val())) {

			if ($("#destGasto").val() == "AL") {
				var aData = oTablevFact.fnGetData();
				for (var i = 0; i < aData.length; i++) {
					var cFactura = aData[i][1];
					var cImporte = quitaFmt(aData[i][2]);
					$("#cValidaFactura").val(cFactura);
					$("#mImporteBrutoF").val(cImporte);
					queryFormPost("tPagoFacturaCreate", {
						async: false
					});
				}
			}

			var actualiacionCorrecta = actualizaRetenciones();
			if (actualiacionCorrecta) {
				$("#montoDev").val("");
				$("#ep").val("");
				$("#codSIAFF").val("");
				$(".pasoTres").show();

				$("#nIdClaveEgresos2").attr('disabled', true); // URVP

				$("#agrega2").attr('disabled', true); // URVP
				$("#TIPO_CONCEPTO").attr('disabled', true); // URVP
				$("#ALM").attr('disabled', true); // URVP

				compDocumentacion();
				$("#DCD_TIPO_OPE").attr('disabled', true); // URVP
				$("#L04").click();
			}
		}
	}
	$("#montoDev").val("");
	$("#ep").val("");
	$("#codSIAFF").val("");
	if ($("#TIPO_CONCEPTO").val() != "AL") {
		if (vOGT == $("#partida").val()) {
			$("#altaAlmacen").val("");
			$("#altaAlmacen").hide();
			$("#cAnioFactEP").val("");
			$("#nFacturaEP").val("");
			$("#cAnioFactEP").hide();
			$("#nFacturaEP").hide();

			querySelectPost("CatalogoAlmacenVacioRead", "ALM", { async: false });
		}
	}

	$("#partCOMSOC").val("");
	queryFormPost("BuscaPartidaCOMSOCRead", {
		async: false
	});
	if (vOGT == $("#partCOMSOC").val()) {
		bCOMSOC = true;
		alert($("#cMsjCOMSOC").val());
	}
	$("#TIPO_CONCEPTO").attr('disabled', true); // SE DESHABILITA NUEVAMENTE
}

function consMovimientos() {
	var szTabla = "CLAVES_DIVERSO_CARGA";
	var elMonto = "";
	var szWhere = " AND nfolioPagoDiverso = " + $("#id_caso").val();
	var capturado = 0.00;
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla: szTabla,
		Param: elMonto,
		MaxReg: szWhere,
		ajax: 'false'
	}, function(j) {
		for (var i = 0; i < j.length; i++) {
			$('#grdCompromisos').dataTable().fnAddData([j[i].Col0, j[i].Col1, j[i].Col2]);
			capturado += Number(j[i].Col2);
		}
		$('#saldoCompM').val(capturado.toFixed(2));
	});
}

function showDivOficio(esUpdate) {
	var cmpName = "oficioDelegatorio" + (esUpdate ? "Update" : "");
	var divName = "oficioDelegatorioCaptura" + (esUpdate ? "Update" : "");
	if ($("#" + cmpName).is(":checked"))
		$("#" + divName)[0].style.display = "block";
	else
		$("#" + divName)[0].style.display = "none";
}

function validaEPsCECFOR() {
	var bReturn = true;
	queryFormPost("esEPsCECFORPagoDIV", {
		async: false
	});

	if ($("#TIPO_CONCEPTO").val() == "AL" && $("#esEPCECFOR").val() == "1") {
		Swal.fire("EP capturada pertene a CECFOR", " NO se puede realizar esta combinacion con el concepto ALMACEN.\nSeleccione otro tipo de concepto para esta EP.", "warning");
		bReturn = false;
	}

	return bReturn;
}

function validaPartidaGtosRepresentacion() {
	var bRegresa = true;

	$("#esPartidaViaticos").val("0");
	var cPartida = $("#ep").val();
	cPartida = cPartida.substring(31, 36);
	$("#cPartida").val(cPartida);
	queryFormPost("esPartidaViaticosRead", {
		async: false
	});

	if (cPartida == "38501" || $("#esPartidaViaticos").val() == "1") {
		Swal.fire("NOTA:", "Queda bajo su responsabilidad la información capturada en los conceptos e informes, misma que séra pública en " +
			"seguimiento al articulo 70 de la Ley General de Transparencia y Acceso a la Información Pública.", "info");
	}

	return bRegresa;
}

function validaPagoConPenalizacion() {
	var penalizacion = 0.00;

	penalizacion = (parseFloat($("#mImportePenalizacion").val())) * 100 / 100;

	if (penalizacion > 0.00) {

		var iva = 0.00;
		iva = parseFloat($("input[id='nPorcIVAAplicable']").val() / 100) * (parseFloat($("input[id='subTotal_2']").val()));

		iva = Number(quitaFmt($("#mImporteIVA").val()));
		iva = iva.toFixed(2);
		$("#mImporteIVA").val(iva);

	}
}


function cmdImprimir(elFormato) {
	if (elFormato == 'PolizaPago') {
		elFormato = elFormato + 'N';
		tipo = 'CR.';
	} else {
		tipo = '';
	}
	window.open(
		"../admin/SeguridadCatalogos?"
		+ "catalogo=CONTRARECIBO"
		+ "&accion=run"
		+ "&rn=" + elFormato + ".jasper"
		+ "&whereFolio= " + tipo + "caNoContrarrecibo = '" + $("#caNoContrarrecibo").val()
		+ "'",
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");

	setTimeout('anexo("' + elFormato + '")', 5000);

}

function anexo(pfmt) {
	$("#totFacturas").val(0);
	queryFormPost("totFacturasPago", {
		async: false
	});

	if (($("#cllave").val() > 7 || $("#totFacturas").val() > 7) && pfmt == "PolizaPagoN") {
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=ANEXO"
			+ "&accion=run"
			+ "&rn=Anexo1.jasper"
			+ "&swhere=  and caNoContrarrecibo ='" + $("#caNoContrarrecibo").val() + "'",
			"Anexo",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
}


function creaDlgProcesar() {
	$("#dialog-Procesando").dialog({
		autoOpen: false,
		height: 400,
		width: 400,
		modal: true,
		open: function() {

			var tipo = "aplicarMotor";
			var caNoContrarrecibo = $("#caNoContrarrecibo").val();
			var campo = $("#campo").val();
			var tablaEnc = $("#tablaEnc").val();
			var campoCondicion = $("#campoCondicion").val();
			var tablaDet = $("#tablaDet").val();
			var tipoAplicar = $("#tipoAplicar").val();

			var numEmpleadoCaptura = numeroEmpleado;
			var numEmpleadoVoBo = $("#cboVoBo").val();
			var numEmpleadoAut = $("#cboAutoriza").val();

			var autorizadoPorFiel = $("#autorizadoPorFiel").val();

			/* JGDS Se agrega este crud para ver si tiene contrarrecibo y no crearlo*/

			var tieneContrarecibo = undefined;

			queryFormPost({
				queryName: "tieneContrarrecibo",
				async: false,
				callback: function() {
					tieneContrarecibo = ($("#correcto").val() == "1");
				}
			});

			if (tieneContrarecibo == undefined)
				Swal.fire("Error:", "No se pudo determinar si el pago tiene contrarecibo", "error");

			var contrareciboRegenerado = false;
			if (!tieneContrarecibo) {
				queryFormPost({
					queryName: "insertaContrarrecibo",
					async: false,
					callback: function() {
						contrareciboRegenerado = true;
					}
				});
				if (!contrareciboRegenerado)
					Swal.fire("El pago no genero contrarecibo.", "Descarte el tramite y reinicie", "error");
			}

			$("#divEsperaProcesando").attr("style", "visibility=visible");

			var fAppActualizada = actualizaMesAplicacion();

			if (!fAppActualizada) {
				$("#divEsperaProcesando").attr("style", "visibility=hidden");
				$("#dialog-Procesando").dialog("close");
				return false;
			}

			/*Validar si la retencion insertada en el detalle corresponde al regimen fiscal del proveedor */
			queryFormPost("validaRegimenFiscal", { async: false });
			var msgRF = $("#msgRF").val();

			if (msgRF != "") {
				Swal.fire('Error:', msgRF, 'error');
				$("#dialog-Procesando").dialog("close");
				return;
			}

			/*Validar la suma de retenciones del detalle vs el encabezado*/
			queryFormPost("validaRetencionENCvsDET", { async: false });
			var msgRF = $("#msgRF").val();

			if (msgRF != "") {
				Swal.fire('Error:', msgRF, 'error');
				$("#dialog-Procesando").dialog("close");
				return;
			}

			/*Validar si el regimen es 626 y el tipo persona es Moral no debe tener retencion RESICO*/
			queryFormPost("validaRESICOPersonaMoral", { async: false });
			var msgRF = $("#msgRF").val();

			if (msgRF != "") {
				Swal.fire('Error:', msgRF, 'error');
				$("#dialog-Procesando").dialog("close");
				return;
			}

			/*ARLA SI LAS FACTURAS ESTAN BORRADAS SE HACE EL INSERT A tPagoFactura Y SE BORRAN DE tPagoFactura_Borrada*/
			queryFormPost("validaExisteFacturas", { async: false });

			$.ajax({
				url: './cierrePresupuestal.jsp',
				type: 'post',
				dataType: 'json',
				data: {
					tipo: tipo,
					caNoContrarrecibo: caNoContrarrecibo,
					campo: campo,
					tablaEnc: tablaEnc,
					campoCondicion: campoCondicion,
					tablaDet: tablaDet,
					tipoAplicar: tipoAplicar,
					empleadoCaptura: numEmpleadoCaptura,
					empleadoVoBo: numEmpleadoVoBo,
					empleadoAutoriza: numEmpleadoAut,
					autorizadoPorFiel: autorizadoPorFiel
				},
				success: function(data) {
					if (data.sinSesion == 'sinSesion') {
						location.href = "../index.jsp";
					}
					if (data.estatus == "guardado") {
						//$("#docAplicado").val( "S" ) ;
						/* JGDS Se agrega este crud para ver si tiene contrarrecibo y no crearlo*/
						var tieneContrarecibo = undefined;
						var contrareciboRegenerado = false;
						queryFormPost({
							queryName: "tieneContrarrecibo",
							async: false,
							callback: function() {
								tieneContrarecibo = ($("#correcto").val() == "1");
							}
						});

						if (tieneContrarecibo == undefined)
							Swal.fire("Descarte", "No se pudo determinar si el pago tiene contrarecibo", "error");

						if (!tieneContrarecibo) {
							queryFormPost({
								queryName: "insertaContrarrecibo",
								async: false,
								callback: function() {
									contrareciboRegenerado = true;
								}
							});
							if (!contrareciboRegenerado)
								Swal.fire("El pago no genero contrarecibo.", "Descarte el tramite y reinicie", "error");
						}

						if ($("#autorizadoPorFiel").val() != "true") {
							cmdImprimir('PolizaPago');
						}
						alert("Documento Aplicado Correctamente: " + $("#caNoContrarrecibo").val());
						parent.document.getElementById("pb_send").disabled = false;
						parent.document.getElementById("pb_send").click();
						breturnVal = true;
						actualizaFolioVueloPagado();
					} else {
						breturnVal = false;
						alert("El Documento No Se Aplico: " + $("#caNoContrarrecibo").val() + " - " + data.estatus);
					}
					$("#divEsperaProcesando").attr("style", "visibility=hidden");
					$("#dialog-Procesando").dialog("close");
				}
			});
		},
		close: function() { }
	});
}

function autorizadoPorFielAction() {
	if ($("#autorizadoPorFielChk").attr("checked"))
		$("#autorizadoPorFiel").val(true);
	else
		$("#autorizadoPorFiel").val(false);

}

function validaEFO() {
	var esEFO = false;
	$("#esEFO").val("");
	$("#rfcValidar").val($("#cIdRFC_RelacionGasto").val());

	queryFormPost({
		queryName: "validaEFO",
		async: false,
		callback: function() {
			esEFO = $("#esEFO").val() == "S";
		}
	});

	return esEFO;
}


function registraCalendarioEP(ep, monto) {
	var errMsg = "";
	var idTipoMovimiento = $("#TIPO_MOVIMIENTO").val();
	var idTipoConcepto = $("#TIPO_CONCEPTO").val();

	$.ajax({
		url: '../egresos/GeneraCalendario',
		dataType: 'json',
		type: "post",
		data: {
			"importeEP": monto,
			"ep": ep,
			"idTipoConcepto": idTipoConcepto,
			"idTipoMovimiento": idTipoMovimiento
		},
		async: false,
		success: function(json) {
			var exito = json.success;
			if (exito != "true") {

				var msg = json.data_1.result;
				errMsg = "No fue posible guardar el calendario debido al error:  " + msg;
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			errMsg = "Error guardando calendario";
		}
	});

	return errMsg;
}

function validaPenalizacion() {
	var monto = "";
	monto = $("#mImportePenalizacion").val();
	if (parseInt(monto.indexOf("."), 10) != parseInt(monto.lastIndexOf("."), 10)) {
		Swal.fire("No se puede captura mas de punto decimal", "Introduce el importe correcto.", "warning");
		$("#mImportePenalizacion").val("0");
		document.getElementById("mImportePenalizacion").focus();
	}
}

function validaREPSE() {
	var numRepse = false;
	queryFormPost("consultarAplica15DRead", { async: false });

	if ($("#cAplica15D").val() == 'S') {
		queryFormPost("consultaProveedorRead", { async: false });
		numRepse = true;
	}
	return numRepse;
}



function revisaTipoFacturas() {
	queryFormPost("readEsPPD", { async: false });

	if ($("#esPPD").val() != "0") {
		Swal.fire('IMPORTANTE', 'La factura adjunta tiene el metodo de pago PPD por lo que es indispensable posteriormente solicitar al proveedor el CFDI de complemento de pago.', 'warning');
		$("#cIDRFC").val($("#cIdRFC").val());
		$("#EditaCorreoE").css('visibility', 'visible');
		creaDialogCorreo();
		
	} else {
		$("#EditaCorreoE").css('visibility', 'hidden');
		
	}
}

function validaCapturaCorreo() {
	let mensaje = "";
	if ($("#esPPD").val() != "0" && $("#correoActual").val() == "") {
		mensaje = "No se capturo el correo para seguimiento y la(s) factura(s) son PPD"
	}

	return mensaje;
}



function validarCorreo() {

	emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;

	if (emailRegex.test($("#correoActual").val())) {
		$("#correo").val($("#correoActual").val());
		//Actualizar datos y cerrar
		if ($("#nombreCorreo").val() == "") {
			Swal.fire("Capturar Nombre", "Debe capturar el nombre a la cual se dirigirá el correo", "warning")
		} else {
			$("#paternoCorreo").val($("#aPCorreo").val());
			$("#maternoCorreo").val($("#aMCorreo").val());
			$("#nCorreo").val($("#nombreCorreo").val());
			$("#cCargo").val($("#cargo").val());
			queryFormPost("updateCorreoActualizado", { async: false });
			modalCorreo.hide();
		}
	} else {
		Swal.fire("Correo Invalido", "El correo no es valido, intente de nuevo", "error");
		return;
	}
}