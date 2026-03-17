/**
 * Contiene el valor del IVA en monto.
 */
var res_iva = 0.0;

function init() {
	$(".subtotall").change(function() {
		calculaIVA();
	});
}

/**
 * Funcion que se encarga del calculo de cada campo en la pantalla.
 */
function calculaIVA() {
	$("#mAmortizacionAcumulado").val(mAmortizacionAcumuladoRes);
	$("#mAmortizacionAnticipo").val(mAmortizacionAnticipoRes);
	$("#mImporteDevolucionAcumulado").val(mImporteDevolucionAcumulado);
	$("#mImporteSancionAcumulado").val(mImporteSancionAcumulado);
	$("#acumulado").val(Number(comprometido).toFixed(2));

	suma = parseFloat(quitaFmt($("#mImporteBruto").val() == "" ? "0" : $(
		"#mImporteBruto").val()));
	suma -= parseFloat(quitaFmt($("#mImporteSancion").val() == "" ? "0" : $(
		"#mImporteSancion").val()));
	suma += parseFloat(quitaFmt($("#mImporteDevolucion").val() == "" ? "0" : $(
		"#mImporteDevolucion").val()));

	var importeDevolucion = 0;
	importeDevolucion = Number($("#mImporteDevolucion").val())
	+ Number($("#mImporteDevolucionAcumulado").val());
	importeDevolucion = importeDevolucion.toFixed(2);

	$("#mImporteDevolucionAcumulado").val(importeDevolucion);

	var importeSancion = 0;
	importeSancion = Number($("#mImporteSancion").val())
	+ Number($("#mImporteSancionAcumulado").val());
	importeSancion = importeSancion.toFixed(2);

	$("#mImporteSancionAcumulado").val(importeSancion);

	if ((Number($("#mImporteDevolucionAcumulado").val()) > Number($(
			"#mImporteSancionAcumulado").val()))
		&& Number($("#mImporteDevolucionAcumulado").val()) != 0) {
		alert("Las Devoluciones son mayores a las Sanciones ");
		$("#mImporteDevolucion").val(0);
		return;
	}

	$("#subTotal_1").val(suma);

	if ($("#lHaySaldoAnticipo").val() == 1
		&& $("#mAmortizado").val() == '0.0000') {
		$("#nPorcAmortizacion").val(0);
		$("#mAmortizacionAnticipo").val(0);
	} else {

		mAmortizacionAnticipo = parseFloat(($("#nPorcAmortizacion").val() / 100))
		* parseFloat($("#subTotal_1").val());
		$("#mAmortizacionAnticipo").val(
			Math.round(mAmortizacionAnticipo * 100) / 100);
		$("#mAmortizacionAnticipoMasIva")
			.val(
				Number($("#mAmortizacionAnticipo").val())
				+ (Number($("#mAmortizacionAnticipo").val())
				* $("input[id='nPorcIVAAplicable']")
					.val() / 100));
	}

	if ((parseFloat($("#mAmortizacionAnticipo").val()) + parseFloat($(
			"#mAmortizacionAcumulado").val())) <= $("#saldoAnticipo").val()) {
		$("#mAmortizacionAcumulado").val(
			parseFloat($("#mAmortizacionAnticipo").val())
			+ parseFloat($("#mAmortizacionAcumulado").val()));
	} else {
		$("#mAmortizacionAnticipo").val(
			parseFloat($("#saldoAnticipo").val())
			- parseFloat($("#mAmortizacionAcumulado").val()));
		$("#mAmortizacionAcumulado").val(parseFloat($("#saldoAnticipo").val()));
	}

	suma -= $("#mAmortizacionAnticipo").val();
	$("#subTotal_2").val(suma);

	var otrosImpuestosStr = parseFloat(quitaFmt($("#otrosImpuestos").val() == "" ? "0"
		: $("#otrosImpuestos").val()));

	var pagoConFacturas = esPagoConFacturas();

	if (!pagoConFacturas) {
		var pctgIVA = parseFloat($("#nPorcIVAAplicable").val());
		var montoSinIVA = parseFloat(quitaFmt($("#mImporteBruto").val()));
		var montoIVA = (montoSinIVA * pctgIVA) / 100;

		res_iva = montoIVA;

	} else {

		res_iva = parseFloat(quitaFmt($("#mImporteIVA").val() == "" ? "0" : $(
			"#mImporteIVA").val()));
	}

	$("#mImporteIVA").val(res_iva);

	$("#mImporteMasIva").val(
		Number($("#mImporteIVA").val()) + Number($("#subTotal_2").val())
		+ Number(otrosImpuestosStr));

	suma += Number($("input[id='mImporteIVA']").val());
	suma += otrosImpuestosStr;

	DCD_IVADES = 0;
	if (mImporteFlete23 == 1) {
		DCD_IVADES = (Number($("#mImporteIVA").val()) * 2) / 3;
	}

	if (Number($("#lHaySaldoAnticipo").val()) == 1
		&& $("#mAmortizado").val() == '0.0000') {
		$("#DCD_MIL2").val(0);
		$("#DCD_MIL5").val(0);
		$("#DCD_RETENCION").val(0);
		$("#DCD_IVADES").val(DCD_IVADES.toFixed(2));
	} else {
		$("#DCD_MIL2").val(DCD_MIL2res);
		$("#DCD_MIL5").val(DCD_MIL5res);
		$("#sumareten").val(DCD_RETENCIONres);
		DCD_MIL2 = parseFloat($("#DCD_MIL2").val()
			* parseFloat($("#subTotal_1").val()));
		DCD_MIL5 = parseFloat($("#DCD_MIL5").val() * $("#subTotal_1").val());
		DCD_RETENCION = parseFloat($("#sumareten").val()
			* $("#subTotal_1").val());
		$("#DCD_MIL2").val(Math.round(DCD_MIL2 * 100) / 100);
		$("#DCD_MIL5").val(Math.round(DCD_MIL5 * 100) / 100);
		$("#DCD_RETENCION").val(Math.round(DCD_RETENCION * 100) / 100);
		$("#DCD_IVADES").val(Math.round(DCD_IVADES * 100) / 100);
	}

	$("#mImpEjercer").val(Math.round(Number(suma) * 100) / 100);

	ayuda1 = Number($("#sumareten").val())
	* Number($("input[id='subTotal_1']").val());

	ayuda2 = parseFloat(ayuda1) + parseFloat($("#DCD_MIL2").val())
	+ parseFloat($("#DCD_MIL5").val())
	+ parseFloat($("#DCD_IVADES").val());

	$("#mImporteRetencion").val(Math.round(Number(ayuda2) * 100) / 100);

	suma -= Number($("input[id='mImporteRetencion']").val());
	suma -= Number($("input[id='mImportePenalizacion']").val());

	$("#mImporteNeto").val(Math.round(Number(suma) * 100) / 100);

	acumula = Number($("#acumulado").val()) + Number($("#mImpEjercer").val());

	if (Number($("#saldoCed").val()) >= Number(acumula).toFixed(2)) {

	} else {
		$("#mImporteBruto").val(0);
		alert("Ha superado el monto del Compromiso");
	}

	if ($("#PagoAMF").val() != "" && $("#mImporteNeto").val() != "0") {
		if (Number($("#mImporteNeto").val()) != Number($("#PagoAMF").val())) {
			$("#mImporteBruto").val(0);
			alert("Importe Neto es diferente al Importe AMF");
		}
	}

}

/**
 * Funcion que se llama cuando el usaurio cambia entre la opcion de cargar
 * facturas o ver las facturas capturadas.
 */
function togleDivFacts(nIdDiv) {
	if (nIdDiv == 0) {
		$("#uploadFacturasDiv").hide();
		$("#facturasCapturadasDiv").show();
		creaDTFacturas();
		leeMontosFacturas();
	} else {
		if ($("#esExtranjero").val() == "1")
			$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
		else
			$('#uploadFacturasFrm').attr('src', "UploadFacturasPF.jsp?tipo_pago=PAGOFEDERALIZADO");
		$("#uploadFacturasDiv").show();
		$("#facturasCapturadasDiv").hide();
	}

	queryFormPost({
		queryName : "facturasCapturadasPFRead",
		async : false,
		callback : function() {
			var totalFacturas = parseInt(($("#nFacturasCapturadas").val() == "" ? "0" : $("#nFacturasCapturadas").val()), 10);
			var totalOficios = parseInt(($("#nOficiosCapturadas").val() == "" ? "0" : $("#nOficiosCapturadas").val()), 10);

			if ((totalFacturas == 0 && totalOficios == 0) || totalFacturas > 0) {
				$("#uploadFacturasTR").css("display", "block");
			} else {
				$("#uploadFacturasTR").css("display", "none");
			}
		}
	});
}

/**
 * Funcion llamada al abrir el dialogo de carga de facturas o al cambiar a la
 * tabla de facturas capturadas para mostrar el total de las facturas.
 */
function leeMontosFacturas() {
	queryFormPost({
		queryName : "readMontoFacturasPF",
		async : false,
		callback : function() {
			res_iva = parseFloat(quitaFmt($("#mImporteIVA").val() == "" ? "0"
				: $("#mImporteIVA").val()));
		}
	});
}

/**
 * Retira el formato monetario a una cadena de texto. Por ejemplo la cadena
 * $589,697.56 se transforma en 589697.56
 * 
 * @param val
 *            Cadena a transformar
 * @returns cadena en formato numerico.
 */
function quitaFmt(val) {
	val = val.replace("$", "");
	val = val.replace(/,/g, "");

	if (val.indexOf("(") >= 0) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
	}
	return val;
}

/**
 * Funcion que se llama al capturar el importe de la clave.<br>
 * Valida que se haya capturado una clave presupuestal (EP) antes de llenar el
 * importe. Si se trata de una EP con cartera valida el OLI, sino efectua la
 * validacion de movimientos.<br>
 * URVP.23062014 se valida que ya se haya seleecionado la EP
 */
function cambioss() {
	if ($("#ep").val() == "") {
		alert("Favor de seleccionar la EP");
		$("#montoDev").val(0);
		return;
	}
    $("#EP").val($("#ep").val());
    $('#grdRetClave').dataTable().fnClearTable();
    
	cambioMovmientos();
	
}

function cambioMovmientos() {
	var vOGT = $("#ep").val();
	vOGT = vOGT.substring(31, 36);
	$("#vOGT").val(vOGT);

	queryFormPost("tMsgPartidaRead", {
		async : false
	});

	if ($("#obs").val() != '') {
		alert($("#obs").val());
	}

	var validM;
	validM = (parseFloat($("#saldoCompM").val())
	+ parseFloat($("#montoDev").val())).toFixed(2);

	if (parseFloat(validM) > parseFloat($("#mImpEjercer").val())) {
		alert("Ha superado el Importe a Ejercer");
		$("#montoDev").val("0");
		$("#ep").val("");
		$("#codSIAFF").val("");
		return;
	}

	validM = parseFloat($("#montoDev").val());

	var campos = "";
	var szWhere = "";
	var elMonto = 12;

	$("#cMes").val($("#fRecepcion").val().split("/")[1]);

	campos = $("#cMes").val() + ", '" + $("#cIdContrato").val() + "', '"
		+ $("#cCentroContable").val() + "', 'FE', '" + $("#ep").val() + "'";
	szWhere = "";
	var szTabla = "SALDOS_DISPONIBLE_PAGOOBRA";

	$.ajax({
		url : "../catalogos/SelectJson.jsp",
		dataType : 'json',
		data : {
			Tabla : szTabla,
			Param : szWhere,
			MaxReg : elMonto,
			Campos : campos,
			Order : " order by 1 desc ",
			ajax : 'false'
		},
		async : false,
		success : function(j) {
			var acumulado = 0.0;
			for (var i = 0; i < j.length; i++) {
				acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
				acumulado = Number(acumulado.toFixed(2));
				$("#TOTALSUBCUENTA").val(acumulado);
			}

			if (acumulado < validM) {
				alert('La cuenta no tiene suficiente saldo comprometido');
				$("#montoDev").val("");
				return;
			}

			var resto = parseFloat(validM);
			var aplicar = 0.0;
			for (var i = 0; i < j.length; i++) {
				if (resto - parseFloat(j[i].Col2) > 0) {
					aplicar = j[i].Col2;
				} else {
					aplicar = resto;
				}

				resto = resto - aplicar;
				if (parseFloat(aplicar) > 0.0) {
					$('#grdRetClave').dataTable().fnAddData(
						[ j[i].Col1, j[i].Col0, aplicar ]);
				}
			}
		}
	});
}


function FiltroMovimientos() {
	$("#montoDev").val(quitaFmt($("#montoDev").val()));

	if ($("#ep").val() != "") {

		var monto = $("#montoDev").val();

		if (parseInt(monto.indexOf("."), 10) != parseInt(monto.lastIndexOf("."), 10)
			|| monto == "." || monto == "") {
			alert("Revisa que el importe sea correcto");
			return;
		} else if (parseFloat($("#montoDev").val()).toFixed(2) == 0) {
			$("#montoDev").val(parseFloat($("#montoDev").val()).toFixed(2));
			alert("No se puede agregar un movimiento con importe 0");
			return;
		}

	} else {
		alert("Favor de seleccionar la EP");
		$("#montoDev").val(0);
		return;
	}

	$("#montoDev").val(parseFloat($("#montoDev").val()).toFixed(2));
	var mImporteADev = $("#montoDev").val();
	var table = $("#grdRetClave").dataTable().fnGetData();
	var rowCount = table.length;

	if (rowCount == 0) {
		cambioss();
		table = $("#grdRetClave").dataTable().fnGetData();
		rowCount = table.length;
	}

	if (rowCount > 0) {
		$("#montoDev").formatCurrency();
		$('#grdCompromisos').dataTable().fnAddData(
			[ $("#codSIAFF").val(), $("#ep").val(), $("#montoDev").val() ]);
		$("#montoDev").val(mImporteADev);
	} else {
		alert("Verifica que el saldo este disponible del mes actual hacia atras.");
		return;
	}

	$("#nombre").val($("#cobjetocontrato").val());
	$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
	document.getElementById("TIPO_CONCEPTO").removeAttribute("disabled", false); //SE HABILITA PARA TOMAR EL DATO
	var vOGT = $("#ep").val();
	vOGT = vOGT.substring(31, 36);
	$("#cOBGT").val(vOGT);
	$("#cEvento").val("");
	queryFormPost("eventpEP3Read", {
		async : false
	});

	if ($("#cEvento").val() == "") {
		alert('La cuenta no corresponde al concepto');
		return;
	}

	$("#cEvento").val("D_" + $("#cEvento").val());

	for (var i = 0; i < rowCount; i++) {

		try {
			var elMesGrd = table[i][1];
			var elMontoGrd = table[i][2];
			var mess;
		} catch (e) {}

		if (null != elMesGrd) {
			if (elMesGrd.toString() != "") {

				if (parseInt(elMesGrd.toString(), 10) < 10)
					mess = '0' + elMesGrd.toString();
				else
					mess = elMesGrd.toString();

				$("#cMes").val(mess);
				var mimportebrutofix = Number(elMontoGrd.toString());
				mimportebrutofix = mimportebrutofix.toFixed(2);
				$("#DCD_IMP_BRUTO").val(mimportebrutofix);

				if ($("#lHaySaldoAnticipo").val() == 0
					|| $("#mAmortizado").val() != '0.0000') {
					calculaRetencionesEP($("#DCD_IMP_BRUTO").val());
				} else {
					$("#mImporteNetoEP").val($("#DCD_IMP_BRUTO").val());
					calculaIva($("#DCD_IMP_BRUTO").val());
				}
				queryFormPost("tContratoFEDERALFacturaDetalleCreate", {
					async : false
				});
			}
		}
	}

	validacion = (Number($("#saldoCompM").val()) + Number($("#montoDev").val())).toFixed(2);
	if (parseFloat(validacion) > parseFloat($("#mImpEjercer").val())) {
		$("#montoDev").val("0");
		validacion = 0;
	} else {
		$("#saldoCompM").val(
			parseFloat($("#saldoCompM").val())
			+ parseFloat($("#montoDev").val()));
		$("#saldoCompM").val(Math.round($("#saldoCompM").val() * 100) / 100);

		validacion = 0;
		if (parseFloat($("#saldoCompM").val()) == parseFloat($("#mImpEjercer")
				.val())) {

			if (Number($("#lHaySaldoAnticipo").val()) == 0
				&& Number($("#mAmortizacionAnticipoMasIva").val()) > 0) {
				$("#mAmortizacionAnticipoMasIvaEP").val(
					$("#mAmortizacionAnticipoMasIva").val());
				$("#cEvento").val("ANTICIPO");
				queryFormPost("tContratoFEDERALFacturaDetalleCreate", {
					async : false
				});
			}

			var actualiacionCorrecta = actualizaRetenciones();
			if (actualiacionCorrecta) {
				comprobacionImportes();

				$("#montoDev").val("");
				$("#ep").val("");
				$("#codSIAFF").val("");
				$(".pasoTres").show();
				$("#agrega2").attr('disabled', true);
				$("#TIPO_CONCEPTO").attr('disabled', true); 
				$("#TIPO_MOVIMIENTO").attr('disabled', true); 
				$("#Limpia").attr('disabled', true); 
				$("#nIdClaveEgresos2").attr('disabled', true); 
				$("#montoDev").attr('readonly', true); 

				compDocumentacion();
				$("#L04").click();

				$("#DCD_FACTURA").attr('readonly', true); 
				$("#DCD_FECHA_FACTURA").attr('readonly', true); 
				$("#DESCRIPCION20").attr('readonly', true); 
				$("#DCD_IMP_BRUTO").attr('readonly', true); 
				$("#DCD_IVA").attr('readonly', true); 
				$("#DCD_IVADES").attr('readonly', true); 
				$("#DCD_ISR").attr('readonly', true); 
				$("#DCD_MIL5").attr('readonly', true); 
				$("#DCD_MIL2").attr('readonly', true); 
				$("#DCD_CONTRIBUCION").attr('readonly', true); 
				$("#DCD_OTRAS_RET").attr('readonly', true); 
				$("#DCD_PENALIZACION").attr('readonly', true); 
				$("#DCD_SANCION").attr('readonly', true); 
				$("#DCD_DEVOL").attr('readonly', true); 
				$("#DCD_AMORT").attr('readonly', true); 
				$("#DCD_RETENCION").attr('readonly', true); 
				$("#DCD_NETO").attr('readonly', true); 
				$("#DCD_CONCEPTO").attr('readonly', true); 
				$("#DCD_TIPO_OPE").attr('disabled', true); 

				cssReadOnly(); 
			}
		}
	}
	$("#montoDev").val("");
	$("#ep").val("");
	$("#codSIAFF").val("");
	$("#TIPO_CONCEPTO").attr('disabled', true); 
}

function comprobacionImportes() {
	queryFormPost("tFACTFEDERALImpNetoRead", {
		async : false
	});

	if (Number($("#TotalNeto").val()) != Number($("#mImporteNeto").val())) {

		$("#TotalNeto").val(
			Number($("#mImporteNeto").val())
			- Number($("#TotalNeto").val()));
		queryFormPost("tFACTFEDERALimpNetoUpdate", {
			async : false
		});
	}

	queryFormPost("tFACTFEDERALRetencionesRead", {
		async : false
	});

	if (Number($("#TotalRetenciones").val()) != Number($("#mImporteRetencion")
			.val())) {

		if (Number($("#mObra5").val()) > 0) {
			$("#campoRetencion").val("mObra5");
		} else if (Number($("#mISRHonorarios").val()) > 0) {
			$("#campoRetencion").val("mISRHonorarios");
		} else if (Number($("#mImporteFlete4").val()) > 0) {
			$("#campoRetencion").val("mImporteFlete4");
		} else if (Number($("#mISRArrenda").val()) > 0) {
			$("#campoRetencion").val("mISRArrenda");
		} else if (Number($("#mTesofe").val()) > 0) {
			$("#campoRetencion").val("mTesofe");
		} else if (Number($("#mRetImpuestoCedular").val()) > 0) {
			$("#campoRetencion").val("mRetImpuestoCedular");
		} else if (Number($("#mCNIC").val()) > 0) {
			$("#campoRetencion").val("mCNIC");
		} else if (Number($("#mIMDT").val()) > 0) {
			$("#campoRetencion").val("mIMDT");
		} else if (Number($("#mImporteFlete23").val()) > 0) {
			$("#campoRetencion").val("mImporteFlete23");
		}

		TotalRetenciones = Number($("#mImporteRetencion").val())
		- Number($("#TotalRetenciones").val());
		$("#TotalRetenciones").val(TotalRetenciones.toFixed(2));
		actualizaretencion();

	}

}

function actualizaretencion() {
	elParametro = $("#campoRetencion").val() + " = "
		+ $("#campoRetencion").val() + " + " + $("#TotalRetenciones").val()
		+ " where  nFolioPAGOFEDERALIZADO = " + $("#id_caso").val()
		+ " AND cEvento !='ANTICIPO' AND nDocRenglon=1 ";

	$.getJSON("../catalogos/InsertJson.jsp", {
		Tabla : "ACTUALIZARETENCIONFED",
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {});
}

function fnClickAddRowComp() {
	queryFormPost("leeImporteTotalFederalizadoEncabezado", {
		async : false
	});
	queryFormPost("leeImporteTotalFederalizadoDetalle", {
		async : false
	});
	if ($("#importeTotalEncabezado").val() != $("#importeTotalDetalle").val()) {
		alert("No se puede seguir con el pago debido a que el detalle no corresponde al encabezado.");
		return;
	}

	var esPagoFid = parseInt($("#esPagoFID").val(), 10) > 0;

	if (esPagoFid && $.trim($("#cIdRFC").val()) == $("#RFCFIBBanorte").val()) {
		if (!validaCapturaFID()) {
			$("#capturaFID_DIV").dialog("open");
			return;
		}
	}

	$("#DCD_TIPO_OPE").attr('disabled', false);

	$('#grdFacturas').dataTable().fnAddData(
		[ $("#DCD_FACTURA").val(), $("#DCD_FECHA_FACTURA").val(),
			$("#DCD_TBEN").val(), $("#DCD_CBEN").val(),
			$("#DCD_TIPO_OPE").val(), $("#DESCRIPCION20").val(),
			$("#DCD_IMP_BRUTO").val(), $("#DCD_IVADES").val(),
			$("#DCD_IVA").val(), $("#DCD_ISR").val(),
			$("#DCD_MIL5").val(), $("#DCD_MIL2").val(),
			$("#DCD_CONTRIBUCION").val(), $("#DCD_OTRAS_RET").val(),
			$("#DCD_PENALIZACION").val() ]);
	$("#fRecepcion2").val($("#fRecepcion").val());

	getNextSequenceVal({
		seqName : "CR-" + $("#cCentroContable").val(),
		async : false,
		callback : setSequenceVal
	});

	setTimeout("contrareibo()", 1000);
	parent.document.getElementById("pb_send").disabled = false;
}

function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "1" + seqValue.substr(seqValue.length - 5);
	seqValue = $("#cCentroContable").val() + $("#cxpPrefijo").val() + $("#cEjercicio").val()
		+ seqValue;
	$("#caNoContrarrecibo").val(seqValue);
}

function reten() {
	if ($.trim($("#destGasto").val()) != "") {

		$("#ID_DESTINO_GASTO").val($("#destGasto").val());
		$("#idDestinoGasto").val($("#destGasto").val());

		querySelectPost({
			queryName : "CatalogoObraTConceptoReadPF",
			targetObjectId : "TIPO_CONCEPTO",
			async : false,
			callback : function() {
				queryFormPost("leeBorrarFFM", {
					async : false
				});
				if ($("#cCentroContable").val() != '10' || $("#borrarFFM").val() == '1' || $.trim($("#cIdRFC").val()) != "BMN930209927") {
					var opts = document.getElementById("TIPO_CONCEPTO").options;
					var indexFF = -1;

					for (cntFF = 0; cntFF < opts.length; cntFF++)
						if ("FFM" == opts[cntFF].value) {
							indexFF = cntFF;
							break;
					}

					if (indexFF >= 0)
						document.getElementById("TIPO_CONCEPTO").remove(indexFF);
				} else if ($.trim($("#cIdRFC").val()) == "BMN930209927" && $("#destGasto").val() == "CSPF") {
					var opts = document.getElementById("TIPO_CONCEPTO").options;
					for (cntFF = 0; cntFF < opts.length; cntFF++) {
						if ("FFM" != opts[cntFF].value && "" != $.trim(opts[cntFF].value))
							document.getElementById("TIPO_CONCEPTO").remove(cntFF);
					}
				}
			}
		});

		concepto();

		$("#fecha_Pago").attr("readonly", true); // URVP
		$("#desde").attr("readonly", true); // URVP
		$("#hasta").attr("readonly", true); // URVP
		$("#cNoEstimacion").attr("readonly", true); // URVP
		$("#cNoFactura").attr("readonly", true); // URVP
		$("#mImporteSancion").attr("readonly", true); // URVP
		$("#mImporteDevolucion").attr("readonly", true); // URVP
		$("#mAmortizacionAnticipo").attr("readonly", true); // URVP
		$("#mImportePenalizacion").attr("readonly", true); // URVP
		$("#nPorcAmortizacion").attr("readonly", true); // URVP
		$("#mAmortizacionAcumulado").attr("readonly", true); // URVP
		$("#Descrip_Concepto").attr("readonly", true); // URVP
		$("#destGasto").attr("disabled", true); // URVP
		$("#TIPO_OPERACION").attr("disabled", true); // URVP

		$("#DCD_FACTURA").val($("#cNoFactura").val());
		$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
		$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
		$("#DCD_IMP_BRUTO").val();
		$("#DCD_IVA").val($("#mImporteIVA").val());
		$("#DCD_NETO").val($("#mImporteNeto").val());
		$("#DCD_SANCION").val($("#mImporteSancion").val());
		$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
		$("#DCD_CONCEPTO").val($("#Descrip_Concepto").val());
		$("#DCD_AMORT").val($("#mAmortizacionAnticipo").val());
		$("#DCD_RETENCION").val($("#mImporteRetencion").val());
		$("#DCD_CONTRIBUCION").val($("#mImporteFlete4").val());
		$("#DCD_PENALIZACION").val($("#mTesofe").val());
	}

	$("#cIDContratoFed").val($("#cFolioContratoObra").val());

	if ($("#cIDContratoFed").val() != "") {
		FiltroRetenciones();

		$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());
		$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
		$("input[id='iva']").val($("input[id='nPorcIVAAplicable']").val());
		$("input[id='nIdConcepto']").val(
			$('#TIPO_CONCEPTO option:selected').val());
		$("input[id='tConcepto2']").val(
			$('#TIPO_CONCEPTO option:selected').text());
		$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
		$("input[id='cIdTipoOperacion2']").val(
			$('#cIdTipoOperacion option:selected').val());
		$("input[id='tipoOper']").val(
			$('#TIPO_OPERACION option:selected').val());
		$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
		$("#cIDContratoObra2").val($("#cIDContratoFed").val());

		$("#impTotal2").val($("#impTotal").val());
		$("#iva").val($("#nPorcIVAAplicable").val());
		$("#poriva").val($("#mImporteIVA").val());

		$(".pasoDos").show();
		if (id_oper != 3) {

			parent.execOperacion();
			parent.execResponsable();

		}
		if (id_oper == 3) {
			$("#cancelar").hide();
		}
		claves();

		$(".subtotall").attr('disabled', true);
		$("#guardar").attr("disabled", true);

		$(".pasoTres").hide();

		setTimeout("retraso3()", 100);
		setTimeout("retraso5()", 100);
	} else {
		$(".pasoDos").hide();
		$(".pasoTres").hide();
	}
}

function elRetardo() {
	$("#nombre").val($("#cobjetocontrato").val());

	elParametro = "'" + $("#caNoContrarrecibo").val() + "', '"
	+ $("#TIPO_OPERACION").val() + "', '0', '0', '"
	+ $("#DCD_IMP_BRUTO").val() + "', '" + $("#DCD_SANCION").val()
	+ "', '" + $("#DCD_DEVOL").val() + "', '" + $("#DCD_AMORT").val()
	+ "', '" + $("#DCD_IVA").val() + "','" + $("#DCD_RETENCION").val()
	+ "', '" + $("#DCD_PENALIZACION").val() + "', '"
	+ $("#DCD_NETO").val() + "', '" + $("#DCD_FECHA_FACTURA").val()
	+ "','" + $("#cCentroContable").val() + "',"
	+ $("#cEjercicio").val() + ",'" + $("#nombre").val()
	+ "', '7','0','" + $("#cIdRFC_RelacionGasto2").val() + "', "
	+ $("#otrosImpuestos").val();

	$.getJSON("../catalogos/InsertJson.jsp", {
		Tabla : "TCONTRARECIBODIVERSOSCREATE",
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {});

	$("#Agregar1").attr('disabled', true);

	parent.document.getElementById("pb_send").style.visibility = 'visible';
	parent.document.getElementById("pb_send").disabled = false;
}

function esPagoConFacturas() {
	var contieneFacturas = false;
	var msg = "";

	$.ajax({
		url : '../uploadFacturasPF',
		type : "GET",
		dataType : 'json',
		async : false,
		data : {
			accion : "NUM_FACTURAS",
			folioPago : $("#id_caso").val()
		},
		success : function(json) {
			exito = "true" == json.success;
			if (!exito) {
				r = json.data_1.result;
				alert(r);
			} else
				contieneFacturas = "true" == json.data_1.result;
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			exito = false;
		}
	});

	return contieneFacturas;
}

/**
 * Crea dialogo para la captura de informacion FID
 */
function creaDialogoFID() {
	$("#capturaFID_DIV").dialog({
		title : "FID",
		autoOpen : false,
		height : 200,
		width : 430,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				if (validaCapturaFID()) {
					$.blockUI({
						message : "Procesando espere ......"
					});
					$("#nIDPrograma").val($("#cProgramaDesc").val());
					$("#cSubPrograma").val($("#cSubProgramaDesc").val());

					queryFormPost({
						queryName : "actualizaDatosFIDUpdate",
						async : "false",
						callback : function() {
							queryFormPost({
								queryName : "actualizaDatosFIDDetalleUpdate",
								async : "false",
								callback : function() {
									return true;
								}
							});
							fnClickAddRowComp();
							$.unblockUI();
							$("#capturaFID_DIV").dialog("close");
						}
					});


				}
			},
			"Cancelar" : function() {
				$(this).dialog("close");
			}
		}
	});

}


/**
 * Valida si las EPs registradas en el pago son de un programa de FID Banorte.
 * Si es asi, muestra al usuario la lista de subprogramas para que seleccione el
 * correcto. En caso contrario, procede de manera normal.
 */
function validaFID() {
	$("#esPagoFID").val("0");

	queryFormPost({
		queryName : "esPagoFederalizadoFID",
		async : "false",
		callback : function() {
			var esPagoFid = parseInt($("#esPagoFID").val(), 10) > 0;
			if (esPagoFid && $.trim($("#cIdRFC").val()) == "BMN930209927") {
				queryFormPost("programaFederalizadoRead", {
					async : false
				});
				querySelectPost("catalogoFideicomisoRead", "cProgramaDesc", {
					async : false
				});
				$("#capturaFID_DIV").dialog("open");
			} else {
				fnClickAddRowComp();
			}
		}
	});

}

/**
 * Actualiza los subprogramas segun la seleccion del programa
 */
function cambiaPrograma() {
	$("#nIDPrograma").val($("#cProgramaDesc").val());
	clearSelect("cSubProgramaDesc");
	querySelectPost("catalogoSubprgRead", "cSubProgramaDesc", {
		async : false
	});
}

/**
 * Funcion general que limpia el contenido de un select agregando una opcion por
 * default con valor -1
 */
function clearSelect(idSel) {
	for (var i = 0; i < idSel.length; i++)
		$('#' + idSel[i]).find('option').remove().end().append(
			'<option value="-1"></option>');
}

/**
 * Valida que el usuario haya capturado correctamente los datos para FID
 * @returns {Boolean} true si y solo si tanto el programa como el subprograma han sido seleccionados.
 */
function validaCapturaFID() {
	var msg = "";
	var token = "";

	if ($("#cProgramaDesc").val() == "-1") {
		msg = "Debe seleccionar el programa.";
		token = "\n";
	}
	if ($("#cSubProgramaDesc").val() == "-1") {
		msg += token + "Debe seleccionar el subprograma.";
	}

	if (msg != "") {
		alert(msg);
		return false;
	} else
		return true;

}

function showDivOficio(esUpdate) {
	var cmpName = "oficioDelegatorio" + (esUpdate ? "Update" : "");
	var divName = "oficioDelegatorioCaptura" + (esUpdate ? "Update" : "");
	if ($("#" + cmpName).is(":checked"))
		$("#" + divName).show();
	else
		$("#" + divName).hide();
}


function validaEFO() {
	var esEFO = false;
	$("#esEFO").val("");
	$("#rfcValidar").val($("#cIdRFC_RelacionGasto").val());

	queryFormPost({
		queryName : "validaEFO",
		async : false,
		callback : function() {
			esEFO = $("#esEFO").val() == "S";
		}
	});

	return esEFO;
}