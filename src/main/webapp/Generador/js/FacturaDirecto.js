/**
 * Funcion de inicializacion de inputs y comportamiento.
 */
function init() {
	$(".subtotall")
			.change(
					function() {

						quitaFormato();

						calculaIVA(1, $("#Imp_Bruto").val());

						actVariablestmp();

						$("#Imp_Iva").val(Math.round(mImporteIVA * 100) / 100);

						$("#Imp_Retenciones").val(
								Math.round(mImporteRetencion2 * 100) / 100);

						$("#Imp_Ejercer")
								.val(
										Math
												.round((mImporteNeto
														+ mImporteRetencion2 + mImportePenalizacion) * 100) / 100);
						$("#Imp_Neto")
								.val(Math.round(mImporteNeto * 100) / 100);

						actualSaldos(1);

						if ($("#PagoAMF").val() != ""
								&& $("#Imp_Neto").val() != "0") {

							if (Number($("#Imp_Neto").val()) != Number($(
									"#PagoAMF").val())) {
								$("#Imp_Bruto").val(0);
								alert("Importe Neto es diferente al Importe AMF");
								calculaIVA(1, $("#Imp_Bruto").val());
								actVariablestmp();
								actualSaldos(1);
							}
						}

						ponFormato();
					});
}

/**
 * Log de cambios: 20150218 1) El calculo del iva se excluye, se toma de la
 * factura ya que pueden venir combinados productos con IVA y Excentos.
 * 
 * @param calcRet
 * @param elMonto
 * @param esNeto
 */
function calculaIVA(calcRet, elMonto, esNeto) {

	quitaFormato();

	var ayuda = 0;
	var ayuda2 = 0;
	var diferencia = 0;
	var otrosImpuestos = 0;

	otrosImpuestos = parseFloat(quitaFmt($("#otrosImpuestos").val()));
	elMonto = quitaFmt(elMonto);

	suma = 0;
	subTotal_1 = 0.0;
	subTotal_2 = 0.0;

	mImporteNeto = 0.0;
	mImporteBruto = 0.0;
	mImporteSancion = 0.0;
	mImporteDevolucion = 0.0;
	mAmortizacionAnticipo = 0.0;
	mImporteRetencion2 = 0.0;
	mImportePenalizacion = 0.0;
	m2Millar = 0.0;
	m23IVA = 0.0;
	mISRHonorarios = 0.0;
	m5Millar = 0.0;
	mFletes = 0.0;
	mISRArrenda = 0.0;
	mCedular = 0.0;

	if (esNeto == 1) {
		elProrrateo = Number(elMonto) / Number($("#Imp_Ejercer").val());
		mImporteBruto = elMonto / (1 + Number($("#por_Iva").val()));
	} else {

		if (Number($("#Imp_Bruto").val()) == 0) {
			elProrrateo = Number(elMonto);
		} else {
			elProrrateo = Number(elMonto) / Number($("#Imp_Bruto").val());
		}

		mImporteBruto = Number($("#Imp_Bruto").val());
	}

	mImporteBruto = mImporteBruto.toFixed(2);
	suma = Number(mImporteBruto);

	mImporteSancion = (Number($("#mImporteSancion").val()) * Number(elProrrateo));
	mImporteSancion = mImporteSancion.toFixed(2);

	mImporteDevolucion = Math
			.round(($("#mImporteDevolucion").val() * Number(elProrrateo)) * 100) / 100;
	mImporteDevolucion = mImporteDevolucion.toFixed(2);

	mImportePenalizacion = Math
			.round(($("#mImportePenalizacion").val() * Number(elProrrateo)) * 100) / 100;
	mImportePenalizacion = Number(mImportePenalizacion.toFixed(2));

	suma -= Number($("input[id='mImporteSancion']").val());
	suma += Number($("input[id='mImporteDevolucion']").val());

	subTotal_1 = suma;

	mAmortizacionAnticipo = Number($("#mAmortizacionAnticipo").val())
			* Number(elProrrateo);
	mAmortizacionAnticipo = mAmortizacionAnticipo.toFixed(2);

	suma -= Number(mAmortizacionAnticipo);
	subTotal_2 = suma;

	if (esNeto == 1) {
		mImporteIVA = elMonto - Number(mImporteBruto);
	}

	suma += Number(mImporteIVA);
	if (esNeto != 1) {
		suma += otrosImpuestos;
	}
	

	if (calcRet == 1) {
		try {
			var table = document.getElementById('grdRetencion');
			var rowCount = table.rows.length;
			var elMonto2 = 0.0;
			for ( var i = 1; i < rowCount; i++) {
				var row = table.rows[i];
				var elPorcentaje = '';
				var elTipo = '';
				var descTipo = '';
				var gua = 0;
				var gua2 = 0;
				try {
					var elTipo = row.cells[0].childNodes[0];
					var descTipo = row.cells[1].childNodes[0];
					var elPorcentaje = row.cells[2].childNodes[0];
				} catch (e) {
					null;
				}
				if (null != elPorcentaje) {
					if ('' != elPorcentaje.toString()) {
						$("#tipoRet").val(elTipo.toString());
						gua2 = parseFloat(elPorcentaje.toString())
								* parseFloat(subTotal_1);
						gua2 = gua2.toFixed(2);
						elMonto2 = Number(gua2);

						/*
						 * URVP.10092014 SE CAMBIA LA CONDICION == 0 A >=10 YA
						 * QUE SE DESGLOZARON EN MAS RETENCIONES Y LAS
						 * POSTERIORES AL ID 10 SON LAS EQUIVALENTES A LA 0 DEL
						 * 2/3 DEL IVA
						 */
						if (parseInt($("#tipoRet").val(), 10) > 10) {

							gua2 = (Number(mImporteIVA) * 2) / 3;
							elMonto2 = gua2.toFixed(2);
							mImporteRetencion2 = Number(mImporteRetencion2)
									+ Number(elMonto2);
							m23IVA = Number(elMonto2);
						}
						if (parseInt($("#tipoRet").val(), 10) == 2) {

							mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							m2Millar = elMonto2.toFixed(2);
						}
						if (parseInt($("#tipoRet").val(), 10) == 3) {
							mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							m5Millar = elMonto2.toFixed(2);
						}
						if (parseInt($("#tipoRet").val(), 10) == 4) {
							mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							mISRHonorarios = elMonto2.toFixed(2);
						}
						if (parseInt($("#tipoRet").val(), 10) == 5) {
							mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							mFletes = elMonto2.toFixed(2);
						}
						if (parseInt($("#tipoRet").val(), 10) == 6) {
							mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							mISRArrenda = elMonto2.toFixed(2);
						}
						if (parseInt($("#tipoRet").val(), 10) == 7) {
							elMonto2 = parseFloat(elPorcentaje.toString())
									* parseFloat(mImportePenalizacion);
							elMonto2 = elMonto2.toFixed(2);
							mImporteRetencion2 = mImporteRetencion2
									+ Number(elMonto2);
							mImportePenalizacion = elMonto2;
						}
						if (parseInt($("#tipoRet").val(), 10) == 9) {
							mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							mCedular = elMonto2.toFixed(2);
						}
					}
				}
			}
		} catch (e) {
			alert(e);
		}
	} else {
		mImporteRetencion2 = $("#mImporteRetencion2").val() * elProrrateo;
		mImporteRetencion2 = mImporteRetencion2.toFixed(2);
	}

	suma -= Number(mImporteRetencion2);
	suma -= Number(mImportePenalizacion);
	mImporteNeto = suma;

}

/**
 * Retira el formato monetario para realizar calculos.
 */
function quitaFormato() {
	$('.subtotall').each(function() {
		quitaFmtObj(this);
	});
	$('.tmpTotall').each(function() {
		quitaFmtObj(this);
	});
	$('.paso055').each(function() {
		quitaFmtObj(this);
	});
}

/**
 * Quita el formato moetario $000,000.00 de una cadena,dejando el formato para
 * su conversion a numero
 * 
 * @param val
 *            Valor a quitarel formato.
 * @returns cadena sin formato numerico.
 */
function quitaFmt(val) {
	val = val.replace("$", "");
	val = val.replace(",", "");
	val = val.replace(",", "");
	val = val.replace(",", "");
	val = val.replace(",", "");
	val = val.replace(",", "");
	val = val.replace(",", "");

	if (val.indexOf("(") >= 0) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
	}
	return val;
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
}

function actualSaldos(elTipo) {
	var laRetencion = 0;

	laRetencion = Number(m2Millar) + Number(m23IVA) + Number(mISRHonorarios) + Number(m5Millar) + Number(mFletes) + Number(mISRArrenda) + Number(mCedular);

	laRetencion = laRetencion.toFixed(2);

	$("#DCD_ISR").val(parseFloat($("#mISRHonorarios").val()) + parseFloat(mISRArrenda));
	$("#DCD_IMP_BRUTO").val($("#Imp_Bruto").val());
	$("#DCD_IVADES").val($("#Imp_Iva").val());
	$("#DCD_IVA").val($("#m23IVA").val());
	$("#DCD_MIL5").val($("#m5Millar").val());
	$("#DCD_MIL2").val($("#m2Millar").val());
	$("#DCD_CONTRIBUCION").val($("#mFletes").val());
	$("#DCD_OTRAS_RET").val($("#mCedular").val());
	$("#DCD_PENALIZACION").val($("#mPenalizacion").val());
	$("#DCD_SANCION").val($("#mSancion").val());
	$("#DCD_DEVOL").val($("#mDevolucion").val());
	$("#DCD_AMORT").val($("#laamortizacion").val());
	$("#DCD_RETENCION").val(laRetencion);
	$("#DCD_OTROS_IMP").val($("#otrosImpuestos").val());
	$("#DCD_NETO").val(
			Math.round((parseFloat($("#DCD_IMP_BRUTO").val())
					+ parseFloat($("#DCD_IVADES").val())
					+ parseFloat($("#DCD_OTROS_IMP").val())
					- parseFloat($("#DCD_RETENCION").val()) - parseFloat($(
					"#DCD_PENALIZACION").val())) * 100) / 100);

}

/**
 * Agrega la documentacion comprobatoria.
 */
function fnClickAddRowComp() {
	quitaFormato();
	$("#btAgregarFact").attr('disabled', true);
	var porce = (parseInt($('#por_Iva option:selected').text(),10));

	$('#grdFacturas').dataTable().fnAddData(
			[ $("#DCD_FACTURA").val(), $("#DCD_FECHA_FACTURA").val(),
					$("#DCD_TBEN").val(), $("#DCD_CBEN").val(),
					$("#DCD_TIPO_OPE").val(), porce, $("#DCD_IMP_BRUTO").val(),
					$("#DCD_IVADES").val(), $("#DCD_IVA").val(),
					$("#DCD_ISR").val(), $("#DCD_MIL5").val(),
					$("#DCD_MIL2").val(), $("#DCD_CONTRIBUCION").val(),
					$("#DCD_OTRAS_RET").val(), $("#DCD_PENALIZACION").val() ]);

	if ($.trim($("#caNoContrarrecibo").val()) == "") {
		if ($("#cCentroContable").val() == "10") {
			getNextSequenceVal({
				seqName : "CT-" + $("#cCentroContable").val(),
				async : false,
				callback : setSequenceValCT
			});
		} else {
			getNextSequenceVal({
				seqName : "CR-" + $("#cCentroContable").val(),
				async : false,
				callback : setSequenceVal
			});
		}

		$("#Acumulado_Op").val();
		$("#elfolio").val($("#No_Folio").val());
		setTimeout('cano()', 100);

	}

	if ((parseFloat($("#DCD_IMP_BRUTO").val()) - parseFloat($("#Imp_Bruto")
			.val())) > -0.02
			&& (parseFloat($("#DCD_IMP_BRUTO").val()) - parseFloat($(
					"#Imp_Bruto").val())) < 0.02) {
		parent.document.getElementById("pb_save").disabled = false;
	}

	ponFormato();

}

function agregaMovimientos() {

	$("#mMovimiento").val(quitaFmt($("#mMovimiento").val()));
	if ($("#EP").val() != "") {
		var monto = $("#mMovimiento").val();
		if (parseInt(monto.indexOf("."),10) != parseInt(monto.lastIndexOf("."),10)
				|| parseInt(monto.indexOf("-"),10) != parseInt(monto
						.lastIndexOf("-"),10) || parseInt(monto.indexOf("-"),10) > 0
				|| monto == "-" || monto == "-." || monto == "") {
			alert("Revisa que el importe sea correcto");
			return;
		}
		if (parseFloat($("#mMovimiento").val()).toFixed(2) < 0) {
			alert("No se puede agregar un movimiento con importe Negativo");
			return;
		} else if (parseFloat($("#mMovimiento").val()).toFixed(2) == 0) {
			alert("No se puede agregar un movimiento con importe 0");
			return;
		}
	} else {
		alert("Favor de seleccionar la EP");
		return;
	}
	quitaFormato();
	
	//mostrar mensaje cuando la partida sea de gastos de representacion o viaticos.
	validaPartidaGtosRepresentacion();

	if ($("#TFONDO").val() == "CE" && creditoExt23 == true) { // ULISES
		var porcentajeCE = 1;
		var mImporteCredito = 0.00;
		var mImporteContraparte = 0.00;
		porcentajeCE = Number($("#nPorcentajeFinanciamiento").val());
		if (porcentajeCE != 100) {
			mImporteCredito = quitaFmt($("#Imp_Ejercer").val()) / 1.16;
			mImporteContraparte = mImporteCredito * 0.16;
			if (Number($("#mMovimiento").val()).toFixed(2) != mImporteCredito
					.toFixed(2)) {
				alert("El importe del digito 2 2 debe corresponder al Subtotal del pago. Favor de verificar el tipo de prestamo y el importe.");
				return;
			}
			var cdisponibleEP = $("#EP").val();
			cdisponibleEP = cdisponibleEP.substring(0, 39);
			cdisponibleEP = cdisponibleEP + "3";
			var ep23 = $("#EP").val();
			ep23 = ep23.substring(40);
			$("#disponibleEP").val(cdisponibleEP + ep23);
			queryFormPost("disponibleEPalMes", {
				async : false
			});
			if (Number($("#mImporteDisponibleEP").val()).toFixed(2) < mImporteContraparte) {
				alert("La EP de la contraparte no tiene suficiente saldo disponible para el Pago.");
				return;
			}
		} else {
			// ULISES VALIDACIONES REQUERIDAS EN PORCENTAJE 100
		}
	}

	var hayError = '';
	var vOGT = $("#EP").val();
	vOGT = vOGT.substring(31, 36);
	$("#cOBGT").val(vOGT);

	$('.paso4').each(function() {
		if ($(this).val() == '') {
			if (this.name != "ALM") {
				hayError = hayError + this.name + ', ';
			}
		}
	});
	if (hayError != "") {
		alert('Debe ingresar los siguientes datos: ' + hayError);
		return -1;
	}

	// validación de facturas para tipo destino almacen
	if ($("#DESTINO_GASTO").val() == "AL") {
		var cFactura = $("#nFacturaEP").val();
		$("#cValidaFactura").val($("#nFacturaEP").val());
		$("#cExiste").val("0");
		queryFormPost("ValidaFactPagosRead", {
			async : false
		});
		if (Number($("#cExiste").val()) > 0) {
			alert("La Factura " + cFactura + " ya existe");
			return -1;
		} else {
			var aData = oTablevFact.fnGetData();
			var bExiste = false;
			for ( var i = 0; i < aData.length; i++) {
				var cFactura = aData[i][1];
				if (cFactura == $("#nFacturaEP").val()) {
					bExiste = true;
				}
			}
			if (!bExiste) {
				$('#grdValidaFacturas').dataTable()
						.fnAddData(
								[ "", $("#nFacturaEP").val(),
										$("#mMovimiento").val() ]);
			}
		}
	}

	$("#cUnidadResponsable2").val("<%=cUR%>");
	var checa = true;
	checa = ChecaSiexisteCodSif();
	if (checa)
		return;

	var mes = [ "MontoEnero", "MontoFebrero", "MontoMarzo", "MontoAbril",
			"MontoMayo", "MontoJunio", "MontoJulio", "MontoAgosto",
			"MontoSeptiembre", "MontoOctubre", "MontoNoviembre",
			"MontoDiciembre" ];

	var fecha = parseInt($("#FechaAplicacion").val().split("/")[1], 10);
	var elMonto = " where " + " cSubCuenta='" + $("#EP").val() + "'";
	var token = "";
	var campos = "";
	for ( var i = 0; i < fecha; i++) {
		campos += token + mes[i];
		token = " + ";
	}
	var acum = 0.0;
	var acum = parseFloat($("#Acumulado_Op").val());
	var mTotalEjercer = parseFloat($("#Acumulado_Op").val());
	var szWhere = "";
	var szTabla = "VDISPONIBLEEP";
	$
			.getJSON(
					"../catalogos/SelectJson.jsp",
					{
						Tabla : szTabla,
						Param : szWhere,
						MaxReg : elMonto,
						Campos : campos,
						ajax : 'false'
					},
					function(j) {
						document.getElementById("tConcepto").removeAttribute("disabled",false);//SE HABILITA PARA TOMAR EL DATO ulisesss
						var acumulado = 0.0;

						for ( var i = 0; i < j.length; i++) {
							acumulado = parseFloat(acumulado)
									+ parseFloat(j[i].Col3);
						}
						if (acumulado < $("#mMovimiento").val()) {
							alert('La cuenta no tiene suficiente disponible');
							return;
						}

						var resto = parseFloat($("#mMovimiento").val());
						var aplicar = 0.0;
						for ( var i = 0; i < j.length; i++) {
							if (parseFloat(j[i].Col3) - parseFloat(resto) > 0) {
								aplicar = resto;
							} else {
								aplicar = parseFloat(j[i].Col3);
							}
							if (parseFloat(j[i].Col3) == 0) {
								aplicar = 0;
							}
							if (parseFloat(aplicar)
									+ parseFloat($("#Acumulado_Op").val()) > parseFloat($(
									"#Imp_Ejercer").val())) {
								alert("Con este movimiento pasaría del monto a ejercer");
								return;
							}
							$("#Acumulado_Op").val(parseFloat(acum + aplicar));
							$("#Acumulado_Op").val($("#Acumulado_Op").val());
							resto = resto - aplicar;

							if (parseFloat(aplicar) > 0.0) {
								$("#cEvento").val("");
								queryFormPost("eventpEPRead", {
									async : false
								});

								if ($("#cEvento").val() == "") {
									alert('La cuenta no corresponde al concepto');
									return;
								}

								$("#cEvento").val("APD_" + $("#cEvento").val());

								var oTableM = $('#grdMovimientos').dataTable();
								var aData = oTableM.fnGetData();
								var nRows = aData.length;
								if (nRows > 0) {
									nRows = parseInt(aData[nRows - 1][0],10)
											+ parseInt(1,10); // nRows + 1;
									fnClickAddRowTable(nRows, $("#EP").val(),
											$("#tConcepto").val(), $(
													"#ALM option:selected")
													.text(), $("#altaAlmacen")
													.val()
													+ "|"
													+ $("#cAnioFactEP").val()
													+ "|"
													+ $("#nFacturaEP").val(),
											aplicar.toFixed(2));
								} else {
									nRows = 1;
									fnClickAddRowTable(nRows, $("#EP").val(),
											$("#tConcepto").val(), $(
													"#ALM option:selected")
													.text(), $("#altaAlmacen")
													.val()
													+ "|"
													+ $("#cAnioFactEP").val()
													+ "|"
													+ $("#nFacturaEP").val(),
											aplicar.toFixed(2));
								}

								var table = document
										.getElementById('grdCompromisos');
								var rowCount = table.rows.length;
								for ( var i = 1; i < rowCount; i++) {
									var row = table.rows[i];
									var elMesGrd = '';
									var elMontoGrd = '';
									try {
										elMesGrd = row.cells[0].childNodes[0].nodeValue;
										elMontoGrd = row.cells[2].childNodes[0].nodeValue;
									} catch (e) {
										null;
									}
									if (null != elMesGrd) {
										if (elMesGrd.toString() != "") {

											$("#nMes").val(elMesGrd.toString());
											$("#elfolio").val(
													$("#No_Folio").val());
											// alert(elMontoGrd.toString());
											calculaIVA(1,
													elMontoGrd.toString(), 1);

											actVariablestmp();
											actualSaldosDet(1);

											$("#ID_TIPO_CONCEPTO")
													.val(
															$(
																	'#tConcepto option:selected')
																	.val());
											$("#ID_TIPO_MOVIMIENTO")
													.val(
															$(
																	'#tmovimiento option:selected')
																	.val());
											$("#cIDRFC")
													.attr('disabled', false);
											quitaFormato();

											if ($("#aux").val() != 2) {

												if ($("#aux").val() == 0) {
													$("#mCNIC").val(
															$("#m2Millar")
																	.val());
													$("#mIMDT").val("");
												} else if ($("#aux").val() == 1) {
													$("#mCNIC").val("");
													$("#mIMDT").val(
															$("#m2Millar")
																	.val());
												}
											}

											mTotalEjercer = Number(mTotalEjercer)
													+ Number(elMontoGrd
															.toString());
											mTotalEjercer = mTotalEjercer
													.toFixed(2);
											if (Number(mTotalEjercer) == Number($(
													"#Imp_Ejercer").val())) {
												AjustaRetencionesD();
											}
											var altaAlmacenresp = $(
													"#altaAlmacen").val();

											$("#altaAlmacen").val(
													$("#altaAlmacen").val()
															+ "|"
															+ $("#cAnioFactEP")
																	.val()
															+ "|"
															+ $("#nFacturaEP")
																	.val());

											try {
												queryFormPost(
														"tPagoDirectodetalleCreate,tPagoApartadoDetCreate",
														{
															async : false
														});
											} catch (e) {
												$("#grdMovimientos")
														.dataTable()
														.fnClearTable();
												consMovimientos();
												alert("No se insertó el regitro, intenta nuevamente.");
											}

											$("#altaAlmacen").val(
													altaAlmacenresp);
											table.deleteRow(i);
											rowCount--;
											i--;
										}
									}
								}
								$("#cIDRFC").attr('disabled', true);
								$("#EP").val("");
								$("#nClaveCNA1").val("");
								$("#mMovimiento").val("");
								if ($("#tConcepto").val() != "AL") {
									if (vOGT == $("#partida").val()) {
										$("#altaAlmacen").val("");
										$("#altaAlmacen").hide();
										$("#cAnioFactEP").val("");
										$("#nFacturaEP").val("");
										$("#cAnioFactEP").hide();
										$("#nFacturaEP").hide();
										querySelectPost(
												"CatalogoAlmacenVacioRead",
												"ALM", {
													async : false
												});
									}
								}

							}
						}

						var mAcumuladoOP = Number($("#Acumulado_Op").val());
						mAcumuladoOP = mAcumuladoOP.toFixed(2);
						$("#Acumulado_Op").val(mAcumuladoOP);

						var mImporteEjercer = Number($("#Imp_Ejercer").val());
						mImporteEjercer = mImporteEjercer.toFixed(2);

						if (Number(mAcumuladoOP) == Number(mImporteEjercer)) {
							$("#btAgregaEP").attr('disabled', true);// URVP.28082014
							$("#nIdClaveEgresos2").attr('disabled', true);// URVP.28082014

							parent.document.getElementById("pb_save").disabled = false;
						}

						$("#partCOMSOC").val("");
						queryFormPost("BuscaPartidaCOMSOCRead", {
							async : false
						});

						if (vOGT == $("#partCOMSOC").val()) {
							bCOMSOC = true;
							nCOMSOC++;
							alert($("#cMsjCOMSOC").val());
						}

						/*
						 * URVP.30082014 SE VALIDA SI YA ES EL IMPORTE TOTAL SE
						 * GUARDAN LOS MOVIMIENTOS AUTOMATICAMENTE
						 */
						if (Number(mAcumuladoOP) == Number(mImporteEjercer))
							cmdGuardar();

						if ($("#TFONDO").val() == "CE" && creditoExt23
								&& porcentajeCE != 100) { // ULISES
							$("#EP").val($("#disponibleEP").val());
							$("#mMovimiento").val(
									mImporteContraparte.toFixed(2));
							creditoExt23 = false;
							$("#mMovimiento").change();

						}
						$("#tConcepto").attr('disabled',true);//SE DESHABILITA NUEVAMENTE ulisessss
					});

	ponFormato();

}


function cat_beneficiario(){
	var formName  = "formPagos";
	var inputName = "cTipoRfc";
	var inputRFCTarget = "cIDRFC";
	var inputDRFCTarget = "cnombre";

	$("#cTipoRfc").val("1,2");
	window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=' + formName + '&inputName=' + inputName + '&inputRFCTarget=' + inputRFCTarget + '&inputDRFCTarget=' + inputDRFCTarget , 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
}

function showDivOficio(esUpdate){
	var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
	var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}

function validaPartidaGtosRepresentacion(){
	var bRegresa = true;
	
	$("#esPartidaViaticos").val("0");
	var cPartida = $("#EP").val();
	cPartida = cPartida.substring(31, 36);		
	$("#cPartida").val(cPartida);
	queryFormPost("esPartidaViaticosRead", {async : false});
	
	if(cPartida == "38501" || $("#esPartidaViaticos").val() == "1"){			
		alert( "Queda bajo su responsabilidad la información capturada en los conceptos e informes, misma que séra pública en " + 
				"seguimiento al articulo 70 de la Ley General de Transparencia y Acceso a la Información Pública." );
	}
	
	return bRegresa;
}