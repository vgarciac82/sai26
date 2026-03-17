/**
 * 
 */
var currYear = 2000;
var totalMA = 0;
var maSaved = false;
var totalOriginal = 0;

function deleteInformacionMA() {
	$("#totalMontoMultiAnual").val('');
	$("#totalMultiAnual").val('');
	queryFormPost({
		queryName : "deleteContratoMultiAnual",
		async : false,
		callback : function() {
			if(!esPlurianualAniosAnteriores)
				alert("Se elimino la informacion multianual exitosamente");
			myModalMA.hide();
			$("#divModals").hide();
		}
	});
}

function cargaInformacionMA() {
	var tableNameReten = 'OBRA_PUBLICA_CNT_MA';
	var conditionReten = " ccvecontrato = '" + $("#cCveContrato").val() + "'";

	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameReten,
		Param : conditionReten,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		clearTable("multiAnualTbl");
		var suma = parseFloat($("#mObra").val() == '' ? 0 : quitaFrmt($(
				"#mObra").val()));
		var totY = 0;
		for ( var i = 0; i < data.length; i++) {
			suma += parseFloat(data[i].Col1);
			var rw = createRow("multiAnualTbl");
			var yTD = rw.insertCell(0);
			var mTD = rw.insertCell(1);
			createYearCell(yTD, data[i].Col0);
			createMoneyCell(mTD, parseFloat(data[i].Col1).toFixed(2), "monto",
					data[i].Col0, "form-control montoAnual");
			totY++;
		}
		var rw = createRow("multiAnualTbl");
		var yTD = rw.insertCell(0);
		var mTD = rw.insertCell(1);
		createYearCell(yTD, 'Total');
		$("#totalMontoMultiAnual").val(suma);
		createMoneyCell(mTD, suma, 'TOTAL', 'Total',
				"form-control montoMATotal notEditable numerico");

		$("#totalMultiAnual").val(totY);
		totalOriginal = totY;
		iniciaCapturaMontosMA();
		formatAll("montoAnual");

	});
}

function save() {
	var tot = $("#totalMultiAnual").val();
	if (tot == '' || tot == '0')
		deleteInformacionMA();
	else {
		var correcto = validaCapturaMontos() && validaTotalMontos();
		if (correcto)
			queryFormPost({
				queryName : "deleteContratoMultiAnual",
				async : false,
				callback : function() {
					showAlertSaveComp = false;
					var saveFirst = false;
					if ($("#nFolioOPComHeader").val() == -1)
						queryFormPost({
							queryName : "readnFolioOPComHeader",
							async : false,
							callback : function() {
								saveFirst = $("#nFolioOPComHeader").val() == -1;
							}
						});

					if (saveFirst) {
						saveHeaderComByCrud(true);
						saveDetailComByCrud();
					}else{
						saveHeaderComByCrud(false);
					}

					y = currYear;
					$(".montoAnual").each(function() {
						$("#yMA").val(y);
						$("#mMA").val(quitaFrmt($(this).val()));
						queryFormPost("createContratoMultiAnual", {
							async : false
						});
						y++;
					});
					//alert("Proceso terminado exitosamente");
					//$("#dialogMA").dialog("close");
					//myModalMA.hide();
					//$("#divModals").hide();
					maSaved = true;
				}
			});
		else
			alert("Existen errores en la caputra de los montos anuales. Los datos no se guardar\u00E1n.");
	}

}

function createInput(parentContainer, inptType, prefixID, sufixID, clssName,
		val, inptSize) {
	var inpt = document.createElement("input");
	$(inpt).attr("id", prefixID + "MultiAnual" + sufixID);
	$(inpt).attr("name", prefixID + "MultiAnual");
	$(inpt).attr("type", inptType);

	if (inptSize && parseInt(inptSize,10) > 0)
		$(inpt).attr("size", inptSize);
	else
		$(inpt).attr("size", 8);

	$(inpt).attr("class", clssName);
	$(inpt).val(val);
	return inpt;
}

function sumaMontosMultiAnual() {
	$(".montoAnual").each(
			function(index, element) {
				if (index == 0)
					$("#TOTALMultiAnualTotal").val(
							$("#mObra").val() == '' ? 0 : quitaFrmt($("#mObra")
									.val()));

				var t = parseFloat(quitaFrmt($("#TOTALMultiAnualTotal").val()))
						+ parseFloat(quitaFrmt($(this).val()));
				$("#TOTALMultiAnualTotal").val(t);
				cambiafrmt($("#TOTALMultiAnualTotal")[0]);
				formatAll();
			});
}

function formatAll(clssNme) {
	$(".montoAnual").each(function() {
		$(this).formatCurrency();
	});
}

function setCurrYear() {
	var dt = new Date();
	currYear = parseInt(dt.getFullYear(), 10) + 1;
}

function clearTable(idTable) {
	var tbl = $("#" + idTable)[0];
	var rowCount = parseInt(tbl.rows.length, 10);
	if (rowCount > 1)
		for ( var i = rowCount; i > 1; i--) {
			tbl.deleteRow(i - 1);
		}
}

function createRow(tableID) {
	var tbl = $("#" + tableID)[0];
	var rowCount = tbl.rows.length;
	var row = tbl.insertRow(rowCount);
	return row;
}

function createYearCell(yTD, y) {
	var lbl = document.createElement("label");
	$(lbl).text(y);
	yTD.appendChild(lbl);
}

function createMoneyCell(mTd, val, prefixID, y, clssNme) {
	var inpt = createInput(mTd, "text", prefixID, y, clssNme, val, 12);
	var inptH = createInput(mTd, "hidden", prefixID + "H", y, "", y, 8);

	mTd.appendChild(inpt);
	mTd.appendChild(inptH);
	cambiafrmt($(inpt)[0]);
}

function generaEntradaAnual() {

	var tot = parseInt($("#totalMultiAnual").val(), 10);
	if (totalOriginal != tot) {
		clearTable("multiAnualTbl");
		if (tot > 10) {
			alert("No puede agregar contrato multianual por mas de 10 a\u00F1os");
			return false;
		} else {
			totalMA = 0;
			var y = currYear;
			if (calculaMontosMA() > 0) {
				for ( var i = 0; i < tot; i++) {
					var rw = createRow("multiAnualTbl");
					var yTD = rw.insertCell(0);
					var mTD = rw.insertCell(1);
					createYearCell(yTD, y);
					createMoneyCell(mTD, calculaMontosMA(), "monto", y,
							"form-control montoAnual");
					y++;
				}
			} else {
				$("#totalMultiAnual").val("0");
			}
			var rw = createRow("multiAnualTbl");
			var yTD = rw.insertCell(0);
			var mTD = rw.insertCell(1);
			createYearCell(yTD, 'Total');
			createMoneyCell(mTD, $("#mObra").val() == '' ? 0
					: parseFloat(quitaFrmt($("#mObra").val())), 'TOTAL',
					'Total', "form-control montoMATotal notEditable numerico");
			iniciaCapturaMontosMA();
			formatAll("montoAnual");
		}

		totalOriginal = tot;
		setMontosMultiAnual();
	}

}

/**
 * Funcion llamada al recibir el foco uno de los campos de catura de monto.
 */
function onFocusMontoMoneyMA(idInpt) {
	var val = Sinfrmt($("#" + idInpt)[0]);
	if (val < 0)
		$("#" + idInpt).val('');
	else {
		cacheMovtoObra = $("#" + idInpt).val();
		$("#" + idInpt).val('');
	}

	$("#" + idInpt).removeClass("montoAnual");
	$("#" + idInpt).addClass("montoAnualEdit");
}

/**
 * Funcion llamada cuando un input monetario pierde el foco.
 */
function onBlurMontoMoneyMA(idInpt, classNormal) {
	if ($("#" + idInpt).val() != '') {
		cambiafrmt($("#" + idInpt)[0]);
	} else {
		$("#" + idInpt).val(cacheMovtoObra);
		Sinfrmt($("#" + idInpt)[0]);
		cambiafrmt($("#" + idInpt)[0]);
	}

	$("#" + idInpt).removeClass("montoAnualEdit");
	$("#" + idInpt).addClass(classNormal ? classNormal : "montoAnual");
}

/**
 * Funcion que inicializa los input's de captura de montos para una EP, asigna
 * la funcion controladora para los eventos onBlur y onFocus
 */
function iniciaCapturaMontosMA() {
	$(".montoAnual").each(function() {
		$(this).focus(function() {
			onFocusMontoMoneyMA(this.id);
		});
		$(this).blur(function() {
			onBlurMontoMoneyMA(this.id);
			sumaMontosMultiAnual();
		});
		$(this).keypress(function(e) {
			return onlyNumbers(e);
		});
	});
}

function calculaMontosMA() {
	var totYStr = $("#totalMultiAnual").val();
	var totMStr = quitaFrmt($("#totalMontoMultiAnual").val());
	var r = 0.0;

	if (totYStr != '' && totMStr != '') {
		var totY = parseInt(totYStr,10);
		var totM = parseFloat(totMStr);
		var totO = parseFloat(quitaFrmt(parseFloat($("#mObra").val() == '' ? 0
				: quitaFrmt($("#mObra").val()))));

		if (totO >= totM) {
			alert("El monto capturado para el a\u00F1o de obra debe ser menor al monto total de la obra");
			$("#totalMontoMultiAnual").val("0");
			$("#totalMultiAnual").val("0");
			return 0;
		} else if (totY != 0) {
			totM = totM - totO;
			r = (totM / totY).toFixed(2);
		}
	}

	return r;
}

function setMontosMultiAnual() {
	var monto = calculaMontosMA();
	$(".montoAnual").each(function(index, element) {
		$(this).val(monto);
	});
	formatAll();
	cambiafrmt($("#totalMontoMultiAnual")[0]);
	sumaMontosMultiAnual();
}

function validaCapturaMontos() {
	var arr = new Array();
	var r = true;
	var y = currYear;
	$(".montoAnual").each(function(index, element) {
		var t = parseFloat(quitaFrmt($(this).val()));
		if (t <= 0) {
			arr.push(y);
			r = false;
		}
		y++;
	});
	if (arr.length > 0)
		alert("No se captur\u00F3 monto para los siguientes a\u00F1os: "
				+ arr.join(","));

	return r;
}

function validaTotalMontos() {

	if ($("#totalMontoMultiAnual").val() == ''
			|| parseFloat(quitaFrmt($("#totalMontoMultiAnual").val())) == 0) {
		alert("No captur\u00F3 el monto total del contrato plurianual.");
		return false;
	} else if (parseFloat(quitaFrmt($("#TOTALMultiAnualTotal").val())) != parseFloat(quitaFrmt($(
			"#totalMontoMultiAnual").val()))) {
		alert("No cuadra la suma de los a\u00F1os con el total registrado del contrato");
		return false;
	}
	return true;
}