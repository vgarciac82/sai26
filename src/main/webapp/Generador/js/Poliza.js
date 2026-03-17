// ------------------------- Funciones de Data Tables ------------------------- //
/**
 * Parametros de lenguaje ES para las Data Tables.
 */
var lengParams = {
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

var toUpdate = -1;

var ultimoTipoMovimiento = '';

var seleccionado = -1;
/**
 * Crea la tabla para almacenar las cuentas
 */
function creaTblPCuentas() {
	return $("#pCuentas").dataTable({
		bPaginate : true,
		bLengthChange : true,
		bInfo : true,
		sScrollX : "100%",
		bJQueryUI : true,
		bFilter : true,
		bSort : true,
		bDestroy : true,
		bRetrieve : true,
		left : true,
		bAutoWidth : true,
		oLanguage : lengParams
	});
}

function clicTblCuentas() {
	$("#pCuentas tbody tr").live('click', function(evt1) {
		if ($(this).hasClass('row_selected')) {
			$(this).removeClass('row_selected');
			seleccionado = -1;
		} else {
			$('tr.row_selected').removeClass('row_selected');
			$(this).addClass('row_selected');
			seleccionado = oTableCuen.fnGetPosition(evt1.target.parentNode);

		}
	});
}
/**
 * Define la funcionalidad doble clic en cuentas
 */
function dobleClickTblCuentas() {
	$("#pCuentas tbody")
			.dblclick(
					function(event) {
						var aPos = oTableCuen
								.fnGetPosition(event.target.parentNode);

						if (aPos == toUpdate && toUpdate >= 0) {
							alert("Ya esta editando el movimiento seleccionado.");
							return;
						} else if (toUpdate >= 0 && aPos != toUpdate) {
							alert("Debe terminar de editar el movimiento antes de editar otro diferente");
							return;
						}

						$(oTableCuen.fnSettings().aoData).each(function() {
							$(this.nTr).removeClass('row_selected');
							var aPos = oTableCuen.fnGetPosition(this.nTr);
							var aData = oTableCuen.fnGetData(aPos[0]);
						});

						$(event.target.parentNode).addClass('row_selected');

						var aData = oTableCuen.fnGetData(aPos);
						toUpdate = aPos;
						cambioTotales(aData[0], aData[1], aData[2], aData[3],
								aData[4], aData[5], aData[6], '', aData[7]);
						seleccionado = -1;
						// oTableCuen.fnDeleteRow(aPos);
					});
}
/**
 * Crea tabla de movimientos.
 */
function creaTblMovientos() {
	return $('#pMovimiento').dataTable({
		sScrollY : "250px",
		sScrollX : "500px",
		"bPaginate" : true,
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
}
/**
 * Define la funcionalidad doble clic en movimientos
 */
function dobleClickTblMovimiento() {
	$("#pMovimiento tbody").dblclick(function(event) {
		$(".detalle").val("");
		$('#ppMovimientoDetalle').dataTable().fnClearTable();
		$(oTableMov.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
			var aPos = oTableMov.fnGetPosition(this.nTr);
			var aData = oTableMov.fnGetData(aPos[0]);
		});
		$(event.target.parentNode).addClass('row_selected');
		var aPos = oTableMov.fnGetPosition(event.target.parentNode);
		var aData = oTableMov.fnGetData(aPos);
		$("#polizaDet").val(aData[0]);
		$("#cConceptoDet").val(aData[1]);
		$("#fCapturaDet").val(aData[2]);
		$("#fAplicacionDet").val(aData[3]);
		$("#PolTipoDet").val(aData[6]);
		$("#PolOrigenDet").val(aData[7]);
		$("#hPolCtroContableDet2").val(aData[8]);
		detallepoliza();
	});
}
/**
 * Crea tabla de cuentas.
 */
function creaTablaCuentasRev() {
	$("#pCuentasRev").dataTable({
		bPaginate : true,
		bLengthChange : true,
		bInfo : true,
		sScrollX : "100%",
		bJQueryUI : true,
		bFilter : true,
		bSort : true,
		bDestroy : true,
		bRetrieve : true,
		left : true,
		bAutoWidth : true,
		oLanguage : lengParams

	});
}
/**
 * Crea tabla de cuentas autorizadas.
 */
function creaTablaCuentasAut() {
	$("#pCuentasAut").dataTable({
		bPaginate : true,
		bLengthChange : true,
		bInfo : true,
		sScrollX : "100%",
		bJQueryUI : true,
		bFilter : true,
		bSort : true,
		bDestroy : true,
		bRetrieve : true,
		left : true,
		bAutoWidth : true,
		oLanguage : lengParams

	});
}
/**
 * Crea tabla de detalle de movimientos.
 */
function creaTablaMovimientoDetalle() {
	$('#ppMovimientoDetalle').dataTable({
		sScrollY : "400px",
		sScrollX : "800px",
		"bPaginate" : true,
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
}

function limpiaCuentas() {
	$("#valSubCuentaCompar").val("");
	$("#dCuenta").val("");
	$("#nCuenta").val("");
}

function ocultaSubCuentas() {
	$("#cIDRFC").val("");
	$("#CTABAN").val("");
	$("#ALM").val("");
	$("#divAML").hide();
	$("#divCTAB").hide();
	$("#divRFC").hide();
	$("#sinAuxiliar").show();
	$("#lblAuxiliar").text("Auxiliar:");
}
/**
 * Suma el movimiento capturado por el usuario a los totales previos.
 */
function creaMovimiento() {
	var formatoabono;

	queryFormPost("ComparaPolizaCuentaRead", {
		async : false
	});

	if ($("#nCuenta").val() == '') {
		alert("Es Necesario que indique una cuenta");
		return;
	}

	if ($("#Cargos").val() == '')
		$("#Cargos").val(0);

	if ($("#Abonos").val() == '')
		$("#Abonos").val(0);

	if (parseFloat(quitaFmt($("#Cargos").val())) == 0
			&& parseFloat(quitaFmt($("#Abonos").val())) == 0) {
		alert("Es Necesario que capture al menos un monto de cargo o abono.");
		return;
	}

	if ($("#nCuenta").val() != $("#valSubCuentaCompar").val()) {
		alert("El numero de cuenta [" + $("#nCuenta").val()
				+ "] no existe o no es una cuenta de aplicacion. ");
		limpiaCuentas();
		ocultaSubCuentas();
		return;
	}
	var op = '';
	if (Number(quitaFmt($("#Abonos").val())) > 0) {
		op = 'A';
	} else {
		op = 'C';
	}
	var nMesTest = $("#fAplicacion").val();
	if (nMesTest.indexOf("/") > 0) {
		var partfech = nMesTest.split("/");
		var mesAplicar = partfech[1];
	} 
	var bloqueo = validaCuenta($("#nCuenta").val(), mesAplicar, $(
			"#cCentroContable").val(), op);
	if (bloqueo)
		return;

	if ($("#nSubCuenta").val() == "RFC") {
		$("#valSubCuenta").val($("#cIDRFC").val());
		$("#valSubCuentaCompar").val("");

		if ($("#valSubCuenta").val() == '') {
			alert("Es obligatorio indicar un RFC");
			$("#cIDRFC").focus();
			return;
		}

		
		//VGC20150702 cuando es un usuario de admin, valida contra los beneficiarios tanto de financieros como de RRHH
		if( esAdminContabilidad )
			queryFormPost("ComparaPolizaRFCAdminRead", {
				async : false
			});
		else
			queryFormPost("ComparaPolizaRFCRead", {
				async : false
			});

		if ($("#cIDRFC").val() != $("#valSubCuentaCompar").val()) {
			alert("El RFC [" + $("#cIDRFC").val() + "] no existe. ");
			$("#valSubCuenta").val(0);
			$("#valSubCuentaCompar").val("");
			$("#cIDRFC").val("");
			return;
		}
	} else if ($("#nSubCuenta").val() == "CTAB") {
		$("#valSubCuenta").val($("#CTABAN").val());
		$("#valSubCuentaCompar").val("");
		if ($("#valSubCuenta").val() == '') {
			alert("Es obligatorio indicar una cuenta bancaria");
			$("#CTABAN").focus();
			return;
		}
		queryFormPost("ComparaPolizaCuentaBanRead", {
			async : false
		});
		if ($("#CTABAN").val() != $("#valSubCuentaCompar").val()) {
			alert("La cuenta Bancaria [" + $("#CTABAN").val() + "] no existe. ");
			$("#valSubCuenta").val(0);
			$("#valSubCuentaCompar").val("");
			$("#CTABAN").val("");
			return;
		}
	} else if ($("#nSubCuenta").val() == "ALM") {
		$("#valSubCuenta").val($("#ALM").val());
	} else {
		$("#valSubCuenta").val("");
	}

	$("#Cargos").val(quitaFmt($("#Cargos").val()));
	$("#Abonos").val(quitaFmt($("#Abonos").val()));
	$("#Tcargos").val(quitaFmt($("#Tcargos").val()));
	$("#Tabonos").val(quitaFmt($("#Tabonos").val()));

	$("#Tcargos").val(Number($("#Tcargos").val()) + Number($("#Cargos").val()));
	$("#TcargosLbl").text($("#Tcargos").val());

	$("#Tabonos").val(Number($("#Tabonos").val()) + Number($("#Abonos").val()));
	$("#TabonosLbl").text($("#Tabonos").val());
	if ($("#Abonos").val() > 0) {
		ultimoTipoMovimiento = 'A';
	} else {
		ultimoTipoMovimiento = 'C';
	}

	if (Number($("#Cargos").val()) > 0 && Number($("#Abonos").val()) > 0) {
		alert("No puede hacer un cargo y abono al mismo tiempo.");
		moneyFrmt("Cargos", "0");
		moneyFrmt("Abonos", "0");
		$("#Cargos").focus();
		return;
	} else {
		tmovimientos();
	}

	moneyFrmt("Cargos", "0");
	moneyFrmt("Abonos", "0");

	$("#Tcargos").val(formatCurrency($("#Tcargos").val()));
	$("#TcargosLbl").text($("#Tcargos").val());

	$("#Tabonos").val(formatCurrency($("#Tabonos").val()));
	$("#TabonosLbl").text($("#Tabonos").val());

	if ($("#movParcialidad").is(":checked") == false) {
		$("#referencia").val("");
		$("#Parciales").val("");
		$("#nSubCuenta").val("");
		$("#nCuenta").val("");
		$("#dCuenta").val("");
		$("#cIDRFC").val("");
		$("#CTABAN").val("");
		$("#ALM").val("");
		ocultaSubCuentas();
	}
}
/**
 * Agrega el movimiento a la tabla
 */
function tmovimientos() {
	var movParcialidadF = $("#movParcialidad").attr("checked") ? "S" : "N";
	if (toUpdate >= 0) {
		$('#pCuentas').dataTable().fnUpdate(
				[ toUpdate + 1, $("#nCuenta").val(), $("#dCuenta").val(),
						$("#valSubCuenta").val(), $("#Parciales").val(),
						formatCurrency($("#Cargos").val()),
						formatCurrency($("#Abonos").val()), movParcialidadF ],
				toUpdate, 0);
		toUpdate = -1;
	} else {
		$('#pCuentas').dataTable().fnAddData(
				[
						(seleccionado >= 0 ? (Number(seleccionado) + 1) - 0.5
								: renglon), $("#nCuenta").val(),
						$("#dCuenta").val(), $("#valSubCuenta").val(),
						$("#Parciales").val(),
						formatCurrency($("#Cargos").val()),
						formatCurrency($("#Abonos").val()),movParcialidadF ]);
		if (seleccionado >= 0) {

			$("#pCuentas tbody tr").each(function(evt1) {
				$(this).removeClass('row_selected');
			});

			oTableCuen.fnSettings().aoData.sort(function(a, b) {
				return a._aData[0] - b._aData[0];
			});

			renumeraTabla();
			oTableCuen.fnDraw();
			seleccionado = -1;
		}
		renglon++;

	}
	seleccionado = -1;
	$('tr.row_selected').removeClass('row_selected');
}

// ------------------------- Funciones Generales ------------------------- //
/**
 * Si se presiona backspace o suprimir, limpia los campos de busqueda para la
 * ayuda.
 */
function limpiaBusqueda(evt) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	var lngth = getSelText().length;

	if (charCode == 8
			|| charCode == 46
			|| ((charCode != 16 && charCode != 17 && charCode != 18 && charCode != 9) && lngth > 0)) {
		$("#dCuenta").val("");
		$("#nSubCuenta").val("");
		return true;
	}
	if (charCode == 9) {
		$("#divACnCuenta").hide();
		blurNCta();
		return false;
	}

}

/**
 * Crea los botones del formulario aplicando el estilo de JQuery
 */
function createButtons() {
	$("#Agregar").button();
	$("#Limpia1").button();
	$("#cancelar").button();
	$("#Guarda2").button();
	$("#Guarda").button();
}

function cambioTotales(contador, cuenta, subcuenta, auxiliar, dmov, cargo,
		abono, referencia, esParcial) {

	$("#nDato").val(contador);

	$("#Tcargos").val(
			formatCurrency(Number(quitaFmt($("#Tcargos").val()))
					- Number(quitaFmt(cargo))));
	$("#TcargosLbl").text($("#Tcargos").val());

	$("#Tabonos").val(
			formatCurrency(Number(quitaFmt($("#Tabonos").val()))
					- Number(quitaFmt(abono))));
	$("#TabonosLbl").text($("#Tabonos").val());

	$("#Cargos").val(cargo);
	$("#Abonos").val(abono);
	$("#nCuenta").val(cuenta);
	$("#dCuenta").val(subcuenta);
	$("#Parciales").val(dmov);

	queryFormPost("CONSULTAPOLIZARead", {
		async : false
	});

	if (esParcial=='S')
		$("#movParcialidad").attr("checked", true);
	else
		$("#movParcialidad").removeAttr("checked");

	if ($("#nSubCuenta").val() == "RFC") {
		$("#divRFC").show();

		$("#lblAuxiliar").text("RFC:");
		$("#sinAuxiliar").hide();

		$("#divCTAB").hide();
		$("#divAML").hide();
		$("#cIDRFC").val(auxiliar);
	} else if ($("#nSubCuenta").val() == "CTAB") {
		$("#divRFC").hide();

		$("#divCTAB").show();
		$("#lblAuxiliar").text("Cta. Ban:");
		$("#sinAuxiliar").hide();

		$("#divAML").hide();
		$("#CTABAN").val(auxiliar);
	} else if ($("#nSubCuenta").val() == "ALM") {
		$("#divAML").show();
		$("#lblAuxiliar").text("Almacen:");
		$("#sinAuxiliar").hide();

		$("#divCTAB").hide();
		$("#divRFC").hide();
		$("#ALM").val(auxiliar);
	}
}
// ------------------------- Funciones de Formateo ------------------------- //
/**
 * Al obtener el foco el input limpia su contenido para permitir editar el
 * contenido.
 */
function onFocusMoney(input) {
	if (Number(input.value) == 0) {
		input.value = '';
	} else if (input.value > 0) {
		input.select();
	}
}

/**
 * Al perder el foco el componente restaura su valor a 0.0 si no se capturaron
 * datos.
 * 
 * @param input
 */
function onBlurMoney(input) {

	if (input.value == 0 || input.value == '') {
		input.value = '0.0';
	}

}

/**
 * Quita el formato monetario.
 */
function Sinfrmt(fld) {
	var valcol = fld.value;
	valcol = valcol.replace("$", "");
	valcol = valcol.replace(/,/g, "");
	$("#" + fld.id).val(valcol);
}

/**
 * Cambia un numero a formato monetario.
 * 
 * @param fld
 *            Campo a transformar
 */
function cambiafrmt(fld) {
	$("#" + fld.id).val(formatCurrency(quitaFmt($("#" + fld.id).val())));
}

/**
 * Transforma un numero a formato monetario.
 * 
 * @param fld
 *            Campo a transformar
 */
function moneyFrmt(id, val) {
	val = quitaFmt(val);
	$("#" + id).val(val);
	$("#" + id).formatCurrency();
}

/**
 * Retira el formato monetario al texto
 * 
 * @param val
 *            Campo a transformar
 * @returns numero sin formato.
 */
function quitaFmt(val) {
	val = String(val);
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
 * 
 * @param lblName
 */
function setLabelText(lblName, txt) {
	$("#" + lblName).text(txt);
}

function formatCurrency(txt) {
	$("#tmpInpt").val(txt);
	$("#tmpInpt").formatCurrency();
	return $("#tmpInpt").val();
}

function creacionDetalle( sCrudFrm ) {

	//queryFormPost("tDocPolizaDetalleDelete", {async : false});

	var rows = oTableCuen.fnGetData();
	var rowCount = rows.length;
	var bGuardar = false;
	$('#dtGuardaDetalle').dataTable().fnClearTable();
	
	for ( var i = 0; i < rowCount; i++) {
		var row = rows[i];
		var cargoGrd = '';
		var abonoGrd = '';
		var subcuentaGrd = '';
		var cuentaGrd = '';
		var parcialesGrd = '';
		var parcialMov = '';
		var mImporte = 0;
		var cEvento = ''; 
		
		try {
			var cuentaGrd = row[1];
			var cargoGrd = row[5];
			var abonoGrd = row[6];
			if (row[4] == 'undefined' || row[4] == '' || row[4] == null) {
				parcialesGrd = '';
			} else {
				parcialesGrd = row[4];
			}
			if (row[3] == 'undefined' || row[3] == '' || row[3] == null) {
				subcuentaGrd = '';
			} else {
				subcuentaGrd = row[3];
			}
			
			parcialMov = row[7];
			
		} catch (e) {
			null;
		}
		if (null != cuentaGrd) {
			if (cuentaGrd.toString() != "") {
				$("#nCuenta").val(cuentaGrd.toString());
				$("#Cargos").val(quitaFmt(cargoGrd.toString()));
				$("#Abonos").val(quitaFmt(abonoGrd.toString()));
				$("#valSubCuenta").val(subcuentaGrd.toString());
				$("#ParcialesCrud").val(parcialesGrd.toString());
				$("#Parcial").val(parcialMov);

				if (Number($("#Cargos").val()) != 0) {
					mImporte = $("#Cargos").val();
					cEvento = "CARGO";
				} else if (Number($("#Abonos").val()) != 0) {
					mImporte = $("#Abonos").val();
					cEvento = "ABONO";
				}
				if ($("#nIdAlmacen").val() == '') {
					$("#ALM").val('');
				}
				bGuardar = true;
				
				$('#dtGuardaDetalle').dataTable().fnAddData([
									'<td><input type="text" id="nCuentaCrud" name="nCuentaCrud" value="' + $("#nCuenta").val() + '"></td>',
									'<td><input type="text" id="valSubCuentaCrud" name="valSubCuentaCrud" value="' + $("#valSubCuenta").val() + '"></td>',
									'<td><input type="text" id="ParcialesCrud" name="ParcialesCrud" value="' + $("#Parciales").val() + '"></td>',
									'<td><input type="text" id="Parcial" name="Parcial" value="' + $("#Parcial").val() + '"></td>',
									'<td><input type="text" id="mImporte" name="mImporte" value="' + mImporte + '"></td>',
									'<td><input type="text" id="cEvento" name="cEvento" value="' + cEvento + '"></td>'
									]);
			}
		}
	}
	if(bGuardar){
		queryFormPost( sCrudFrm + "tPolizaDetalleCreate", 
			{async : false, 
			callback : function() {
					detalleC=true;	
				} 
		});
	}

}

/**
 * 
 */
function ocultaOperacionesFlujo() {
	if (parent.document && parent.document.getElementById("pb_save")) {
		parent.document.getElementById("pb_save").style.visibility = 'hidden';
		parent.document.getElementById("pb_send").style.visibility = 'hidden';
	}
}

/**
 * 
 */
function muestraOperacionesFlujo() {
	if (parent.document && parent.document.getElementById("pb_save")) {
		parent.document.getElementById("pb_save").style.visibility = 'visible';
		parent.document.getElementById("pb_send").style.visibility = 'visible';
	}
}

function getSelText() {
	var txt = '';
	if (window.getSelection) {
		txt = window.getSelection();
	} else if (document.getSelection) // FireFox
	{
		txt = document.getSelection();
	} else if (document.selection) // IE 6/7
	{
		txt = document.selection.createRange().text;
	} else
		return '';
	return txt;
}

/**
 * Avanza la poliza al paso "Formulado" sin cambiar de usuario, siempre que el
 * usuario actual tenga el rol siguiente.
 */
function avanzaRevision() 
{
	

	if ($("#nCambio").val() < 1) {
		queryFormPost("tPolizaEncabezadoCreate", {
			async : false
		});
	}
	
	$("#cadFolio").val("POLI-C" + $("#cCentroContable").val() + "-"	+ $("#id_caso").val());
	
	creacionDetalle( "" );
	restringeTipoPol();
	
	parent.document.getElementById("pb_save").disabled = false;
	parent.document.getElementById("pb_save").click();
	parent.document.getElementById("pb_save").disabled = true;

}

/**
 * Valida que la poliza cuadre. Es decir, que la suma de los abonos y los cargos
 * sea 0.
 * 
 * return true si la poliza esta cuadrada.
 */
function validaPolizaCuadra() {

	return Number(quitaFmt($("#Tcargos").val())) == Number(quitaFmt($(
			"#Tabonos").val()));

}

/**
 * Valida los campos capturados por el usuario. No se valida formato, solo que
 * contenga informacion.
 * 
 * @returns {Array}
 */
function validaCaptura() {
	var incorrectos = new Array();
	if ($('#cConcepto').val() == '')
		incorrectos.push("Concepto");
	return incorrectos;
}

function actualizaDetalle() {
	$.getJSON('../poliza/EditaPoliza', {
		"action" : "ACTUALIZA_DETALLE",
		"nFolioDocPoliza" : $("#id_caso").val(),
		"cCentroContable" : $("#cCentroContable").val(),
		"cTipoPoliza" : $("#cTipoPoliza").val(),
		"aEjercicioFiscal" : $("#EF").val()

	}, function(data, textStatus) {

	});
}

function actualizaEstatusPoliza(estatus) {
	$.getJSON('../poliza/EditaPoliza', {
		"action" : "CAMBIA_ESTATUS",
		"nFolioPoliza" : $("#nFolioPoliza").val(),
		"cCentroContable" : $("#cCentroContable").val(),
		"cTipoPoliza" : $("#cTipoPoliza").val(),
		"aEjercicioFiscal" : $("#EF").val(),
		"polizaEstatus" : estatus

	}, function(data, textStatus) {

	});
}

function comprobacionPoliza() {

	queryFormPost("ObtenerNumeroPolizaManualRead", {
		async : false
	});

	// actualizaEstatusPoliza('S');

	alert("El Documento fue Aplicado con el Numero de Poliza: "
			+ $("#nFolioPoliza").val() + " El Centro Contable: "
			+ $("#cCentroContable").val() + " El tipo de Poliza:  "
			+ $("#cTipoPoliza").val());
	
	$("#nFolioPolizaDef").val( $("#nFolioPoliza").val());
	
	
	//revision();
	//autorizacion();

	/*if ($("#nCambio").val() == 2)
		actualizaEstatusPoliza('E');
	else if (Number($("#nCambio").val()) == 5)
		actualizaEstatusPoliza('S');*/

	/*if (creado) {
		actualizaDetalle();
		creado = false;
	}*/

	/*if (usuarioRevisionPoliza) {
		$("#tba4").click();
		$("#tba4").show();
		$("#tba1").hide();
		$("#tba2").hide();
		$("#tba3").hide();
		$("#nCuenta").val("");
		$("#Cargos").val("");
		$("#Abonos").val("");
		$("#valSubCuenta").val("");
		$("#cComentariosRev").val("");

	} else {
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
		parent.document.getElementById("pb_send").disabled = true;
	}*/
}

/**
 * La fecha de aplicacion se basa en el mes contable abierto. Si el mes en el
 * que se captura la poliza es mayor al ultimo mes abierto la fecha de captura
 * sera el ultimo dia del mes inmediato anterior.
 * 
 * Si es el primer mes del año ...
 * 
 * @returns
 */
function fechaAplicacion() {
	var fApp = new Date();
	var fechaActual = new Date();
	var mesActual = fechaActual.getMonth() + 1;
	var r;

	/* Valida si el mes actual esta abierto */
	$("#nMes").val(mesActual);
	queryFormPost("MesContableAbiertoPoliza", {
		async : false
	});
	if ($("#mesAbierto").val() == '' || $("#mesAbierto").val() == '0') {

		/*
		 * El mes actual aun no esta contablemente abierto. Se ajusta la fecha
		 * de aplicacion al ultimo dia del mes anterior. Para calcularlo, se
		 * crea una fecha con el primer dia del mes y se resta un dia
		 * 
		 * Cambio 11/10/2012 Se obtiene el ultimo mes contable abierto y se
		 * calcula, en base a este, la fecha de aplicacion.
		 */
		queryFormPost("dMes", {
			async : false,
			callback : function() {
				if ($("#dMes").val() == 13) {
					var tmpDate = new Date(fechaActual.getFullYear(), 0, 1);
					$("#nMes").val(13);
				} else {
					var tmpDate = new Date(fechaActual.getFullYear(),
							$("#dMes").val(), 1);
				}
				tmpDate.setDate(tmpDate.getDate() - 1);
				fApp = new Date(tmpDate.getFullYear(), tmpDate.getMonth(),
						tmpDate.getDate());
			}
		});
	}

	r = (fApp.getDate() < 10 ? "0" + fApp.getDate() : fApp.getDate())
			+ "/"
			+ ((fApp.getMonth() + 1) < 10 ? "0" + (fApp.getMonth() + 1) : fApp
					.getMonth() + 1) + "/" + $("#EF").val();
	
	return r;
}

/**
 * Cambia la fecha de aplicacion
 */
function changeFAplicacion() {
	$("#fAplicacionAut").val($("#fAplicacion").val());
	$("#fAplicacionRev").val($("#fAplicacion").val());

	$("#fAplicacionLbl").text($("#fAplicacion").val());
	$("#fAplicacionAutLbl").text($("#fAplicacion").val());
	$("#fAplicacionRevLbl").text($("#fAplicacion").val());
}
/**
 * Cambia la fecha de captura.
 */
function changeFCaptura() {
	$("#fCapturaRev").val($("#fCaptura").val());
	$("#fCapturaAut").val($("#fCaptura").val());

	$("#fCapturaAutLbl").text($("#fCapturaAut").val());
	$("#fCatpuraLbl").text($("#fCaptura").val());
	$("#fCatpuraRevLbl").text($("#fCapturaRev").val());
}

/**
 * Carga informacion de la poliza. Muestra y/o oculta paneles segun el paso en
 * el que se encuentra la poliza.
 * 
 */

//Se remplaza esta funcion por instrucciones de Jorge Soto  20140604
function folio() {
	var drenglon = 1;
	var nFolioDocPolizaParam =$("#id_caso").val();	
	var Concepto;
	var nFolioPoliza;
	var cTipoPoliza = "";
	
	if ($("#hPolCtroContable").val() != "") {
	
		queryFormPost("datosPolizaManual", {async:false});	
		nFolioPoliza = $("#nFolioPolizaDef").val(); //ArrayDatos[0];
	    $("#TcargosLbl").text( formatCurrency( $("#Tcargos").val() ) );
		$("#TabonosLbl").text( formatCurrency( $("#Tabonos").val() ) );
		renglon = $("#ndocrenglon").val();			//ArrayDatos[3])+1;ndocrenglon
        id_caso_oper = parseInt($("#Id_Caso_Oper").val(), 10);		//ArrayDatos[8]; 
		cTipoPoliza = $('#PolTipo').val();
	}

		
	if ( $("#cDocumentoHaplicado").val() == "P" ){
	    	  alert("Documento se Encuentra en Proceso de Aplicación Contable");
	    	  document.redireccionar.submit();
	}

	
	if(id_caso_oper==1)
	   {
		 //$("#cDocumentoHaplicado").val("");
	     // $("#nGrupo").val(ArrayDatos[5]); 
	     // $("#nSubGrupo").val(ArrayDatos[6]); 
	     // $("#nEvento").val(ArrayDatos[7]); 
		captura(nFolioDocPolizaParam, cTipoPoliza);
		//$("#cConcepto").val(Concepto); 
		
	
		if($("#nFolioPolizaDef").val()>1)
        		{        		
        		document.getElementById('cancelaBtn').style.visibility='hidden';         		
        		}
        	 
        	


	  
	    if(renglon>2)
	      {deshabilitarBotones();}
	    
	   }
	else if(id_caso_oper==2)
	   {
		
		 revision(nFolioDocPolizaParam, cTipoPoliza);
		 $("#FolioPolizaRev").val( $("#nFolioPolizaDef").val() ); 
		 $("#cConceptoRev").val( $("#cConcepto").val() );
		
		 
		 
	   }
	else if(id_caso_oper==3)
	   {
		autorizacion(nFolioDocPolizaParam, cTipoPoliza);
	    $("#FolioPolizaAut").val( $("#nFolioPolizaDef").val() ); 
	    $("#cConceptoAut").val( $("#cConcepto").val() );
	   }
	
	

	if ($("#nCambio").val() == 4 || id_caso_oper == 1 || $("#nCambio").val() == 10) {
		$("#tba1").show();
		$("#tba4").hide();
		$("#tba5").hide();

		if (parseInt($("#nFolioPolizaDef").val(), 10) > 0) {
			$("#tba2").hide();
			$("#tba3").hide();
			$("#cComentarios").show();
			$("#lbObservaciones").show();
			restringeTipoPol();
			$("#cancelar").attr("disabled", true);
			if($("#cComentariosRev").val()!='')
			alert("Motivo de Rechazo:  "+$("#cComentariosRev").val());
		}

	//} else if ($("#nCambio").val() == 2) {
		} else if (id_caso_oper == 2) {
		$("#tba1").hide();
		$("#tba2").hide();
		$("#tba3").hide();
		$("#tba4").show();
		$("#tba5").hide();
		$("#tba4").click();
	} else if (id_caso_oper == 3) {
		$("#tba1").hide();
		$("#tba2").hide();
		$("#tba3").hide();
		$("#tba4").hide();
		$("#tba5").show();
		$("#tba5").click();
	} else if ($("#nCambio").val() == 5) {
		queryFormPost("ObtenerNumeroPolizaManualRead", {
			async : false
		});
		$('.encabezado').attr('disabled', true);
		$('#Agregar').attr('disabled', true);
		$('#Limpia1').attr('disabled', true);
		$('#cancelar').attr('disabled', true);
		$('#Guarda').attr('disabled', true);
		$('#Guarda2').attr('disabled', true);

		$("#divConsulta").show();
	}

	Limpia();

}


/**
 * Regresa la fecha actual en el formato dd/mm/yyyy
 * 
 * @returns {String}
 */
function today() {
	var d = new Date();

	return (d.getDate() < 10 ? "0" + d.getDate() : d.getDate())
			+ "/"
			+ ((d.getMonth() + 1) < 10 ? "0" + (d.getMonth() + 1) : d
					.getMonth() + 1) + "/" + d.getFullYear();
}

function clickParcial(checked) {

	if (checked) {
		// $("#Cargos").focus();
	} else {
		$("#Parciales").val('');
		Limpia();
		$("#nCuenta").focus();
	}

}

function toNext(e, id) {
	var tecla = (document.all) ? e.keyCode : e.which;
	/*
	 * Calcula el siguiente elemento a ser capturado en base al ID para darle el
	 * foco.
	 */
	if (tecla == 13) {
		if (id == 'ALM')
			$("#Cargos").focus();
		else if (id == 'Cargos' || id == 'Abonos') {
			creaMovimiento();
			if ($("#movParcialidad").is(":checked")) {
				/*
				 * if (ultimoTipoMovimiento == 'A') $("#Abonos").focus(); else
				 * $("#Cargos").focus();
				 */
				$("#Parciales").focus();
			} else {
				$("#nCuenta").focus();
			}
		} else if (id == 'Parciales') {
			creaMovimiento();
			if ($("#movParcialidad").is(":checked")) {
				if (ultimoTipoMovimiento == 'A')
					$("#Abonos").focus();
				else
					$("#Cargos").focus();
			} else {
				$("#nCuenta").focus();
			}
		} else if (id == 'referencia') {
			creaMovimiento();
			if ($("#movParcialidad").is(":checked")) {
				if (ultimoTipoMovimiento == 'A')
					$("#Abonos").focus();
				else
					$("#Cargos").focus();
			} else {
				$("#nCuenta").focus();
			}
		}
		return false;
	} else {
		if (id == 'Cargos' || id == 'Abonos')
			return validar2(event);
		else
			return true;
	}
}

function catchTab(e, id) {
	var tecla = ((document.all) ? e.keyCode : e.which);
	if (tecla == 9) {
		if (id == "Cargos") {
			if ($("#Cargos").val() == "" || quitaFmt($("#Cargos").val()) == 0) {
				$("#Abonos").focus();
			} else
				$("#Parciales").focus();
			return false;
		} else if (id == 'Abonos') {
			$("#Parciales").focus();
			return false;
		} else if (id == 'nCuenta') {
			blurNCta();
			return false;
		}
	}
	return true;
}

function ctaKeyDwn(e, id) {
	var tecla = ((document.all) ? e.keyCode : e.which);
	if (tecla != 9) {
		if (id == 'idCuenta') {
			$("#CTABAN").val('');
			return true;
		} else if (id == 'CTABAN') {
			$("#idCuenta").val('');
			return true;
		} else {
			return true;
		}
	}
}

function Limpia() {
	if (toUpdate >= 0) {
		if (confirm('Se eliminar\u00E1 el movimiento que est\u00E1 editando \u00BFContinuar?')) {
			oTableCuen.fnDeleteRow(toUpdate);
			toUpdate = -1;
			renumeraTabla();
		} else {
			return;
		}
	}

	$("#Cargos").val(0);
	moneyFrmt("Cargos", $("#Cargos").val());

	$("#Abonos").val(0);
	moneyFrmt("Abonos", $("#Abonos").val());

	$("#nSubCuenta").val("");
	$("#nCuenta").val("");
	$("#dCuenta").val("");
	$("#cIDRFC").val("");
	$("#CTABAN").val("");
	$("#ALM").val("");
	ocultaSubCuentas();
	$("#idCuenta").val("");
}

function validar2(e) {
	tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return true;
	patron = /[-.\d]/g;
	// var patron;
	// patron = /^-?[0-9]+([\.][0-9]*)?$/
	te = String.fromCharCode(tecla);
	return patron.test(te);
}

function disableKeys(e) {
	var tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return false;
	else
		return true;
}

function updateCCont() {
	$("#hPolCtroContableC").val($("#hPolCtroContable").val());
	$("#hPolCtroContableCLbl").val(
			$("#hPolCtroContable option:selected").text());
}


function captura(nFolioDocPolizaParam,ctipoPoliza)
{
	
	
	  //alert("En captura");

$("#id_caso_lbl").val($("#id_caso").val());
nFolioDocPolizaParam=$("#id_caso_lbl").val();

$('#PolTipo').val(ctipoPoliza);

$("#nCambio").val("10");

$('#pCuentas').dataTable({         
		        			oLanguage: lengParams,
		        			async: false,
							sAjaxSource:"../export/GeneraJsonTxt",
							"fnServerParams":  function ( aoData ) {
                             aoData.push( { "name": "more_data", "value": nFolioDocPolizaParam } );
                              },
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true			      		
				      		
		        });	
	
}

function revision(ctipoPoliza) {
	
    
	
    creaTablaCuentasRev();
	$('#pCuentasRev').dataTable().fnClearTable();
	$("#TcargosRev").val('$'+$("#Tcargos").val());
	$("#TabonosRev").val('$'+$("#Tabonos").val());
	$("#EFRev").val($("#EF").val());
	$("#fAplicacionRev").val($("#fAplicacion").val());
	$("#FolioPolizaRev").val($("#nFolioPolizaDef").val());
	$("#PolTipoRev").val(ctipoPoliza);
    $("#id_casoRev").val($("#id_caso").val());
	$("#hPolCtroContableRev").val($("#hPolCtroContable").val());
	//$("#cConceptoRev").val($("#cConcepto").val());

	var docrenglonRev = 1;

	var rows = oTableCuen.fnGetData();
	var rowCount = rows.length;
	
	nFolioDocPolizaParam=$("#id_casoRev").val();
   
	$("#nCambio").val(2);
		
	 $('#pCuentasRev').dataTable({         
		        			oLanguage: lengParams,
		        			async: false,
							sAjaxSource:"../export/GeneraJsonTxt",
							"fnServerParams":  function ( aoData ) {
                             aoData.push( { "name": "more_data", "value": nFolioDocPolizaParam } );
                              },
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true			      		
				      		
		        });	
	
	
	Limpia();

}

function autorizacion(ctipoPoliza) {
	
	
	creaTablaCuentasAut();
	$('#pCuentasAut').dataTable().fnClearTable();
	$("#TcargosAut").val('$'+$("#Tcargos").val());
	$("#TabonosAut").val('$'+$("#Tabonos").val());
	$("#cComentarios").val($("#cComentariosAut").val());
	queryFormPost("tdocPolizaComentarioCaptUpdate", {
		async : false
	});
	$("#EFAut").val($("#EF").val());
	$("#fAplicacionAut").val($("#fAplicacion").val());
	$("#FolioPolizaAut").val($("#nFolioPolizaDef").val());
	$("#PolTipoAut").val(ctipoPoliza);
	$("#id_casoAut").val($("#id_caso").val());
	$("#hPolCtroContableAut").val($("#hPolCtroContable").val());
	$("#cConceptoAut").val($("#cConcepto").val());
	var docrenglonRev = 1;
	
	$("#nCambio").val(3);

	//var rows = oTableCuen.fnGetData();
	//var rowCount = rows.length;

		nFolioDocPolizaParam=$("#id_casoAut").val();
		
	 $('#pCuentasAut').dataTable({         
		        			oLanguage: lengParams,
		        			async: false,
							sAjaxSource:"../export/GeneraJsonTxt",
							"fnServerParams":  function ( aoData ) {
                             aoData.push( { "name": "more_data", "value": nFolioDocPolizaParam } );
                              },
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true			      		
				      		
		        });	
	
	
	Limpia();
	$("#Parciales").val("");
}

function CopiaPoliza() {
	$("#PolTipo").val($("#PolTipoDet").val());
	$("#PolOrigen").val($("#PolOrigenDet").val());

	$("#Tcargos").val(formatCurrency($("#mTotalCargo").val()));
	$("#TcargosLbl").text($("#Tcargos").val());

	$("#Tabonos").val(formatCurrency($("#mTotalAbono").val()));
	$("#TabonosLbl").text($("#Tabonos").val());

	$("#hPolCtroContable").val($("#hPolCtroContableDet2").val());

	$("#cConcepto").val($("#cConceptoDet").val());

	document.getElementById("esperar").style.visibility = "visible";
	document.getElementById("esperardet").style.visibility = "visible";
	$('#pCuentas').dataTable().fnClearTable();

	var elParametro = "nFolioPoliza=" + $("#polizaDet").val()
			+ " AND cTipoDocumento= '" + $("#PolOrigenDet").val()
			+ "' AND cCentroContable='" + $("#hPolCtroContableDet2").val()
			+ "' AND mMovimiento !=0 ";

	var szTabla = "TPOLIZAMOVIMIENTOS";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {
		var abono = 0;
		var cargo = 0;
		var docrenglon = 1;
		var subcuenta1 = '';
		for ( var i = 0; i < j.length; i++) {
			if (j[i].Col2 == 'C') {
				cargo = j[i].Col3;
				abono = 0;
			} else {
				abono = j[i].Col3;
				cargo = 0;
			}

			if (j[i].Col1 != 'undefined' || j[i].Col1 != null) {
				j[i].Col1;
			} else {
				j[i].Col1 = ' ';
			}
			if (j[i].Col5 != 'undefined' || j[i].Col5 != null) {
				j[i].Col5;
			} else {
				j[i].Col5 = ' ';
			}

			$('#pCuentas').dataTable().fnAddData(
					[ docrenglon, j[i].Col0, j[i].Col4, j[i].Col1, j[i].Col5,
							formatCurrency(cargo), formatCurrency(abono),
							j[i].Col6 ]);
			docrenglon++;
		}
		document.getElementById("esperar").style.visibility = "hidden";
		document.getElementById("esperardet").style.visibility = "hidden";
		renglon = docrenglon;
	});
	$("#tba1").click();

}

function editaCuenta(elmnt) {
	if (Number(elmnt.selectionEnd) > 0)
		clearSelection();
	else
		elmnt.select();
	$("#valSubCuentaCompar").val("");
	$("#dCuenta").val("");
	ocultaSubCuentas();

}

function getSelectedText() {
	var txt = "";
	if (typeof window.getSelection != "undefined") {
		txt = window.getSelection();
	} else if (typeof document.selection != "undefined") {
		txt = document.selection.createRange().text;
	} else if (document.getSelection) {
		txt = document.getSelection();
	}
	return txt;
}

function clearSelection() {
	if (document.selection) {
		document.selection.empty();
	} else if (window.getSelection) {
		window.getSelection().removeAllRanges();
	}
}

function BuscarMovimiento() {
	$('#pMovimiento').dataTable().fnClearTable();

	document.getElementById("esperar").style.visibility = "visible";
	document.getElementById("esperardet").style.visibility = "visible";

	var elParametro = "";

	// =================================== TIPO DE POLIZA =====================
	elParametro = elParametro + " nPolizaAutomatica =  "
			+ $("#PolAutomatica").val();

	// =================================== Ejercicio Fical ====================
	elParametro = elParametro + " AND aEjercicioFiscal = '" + $("#EFC").val()
			+ "'";

	// =================================== Centro Contable ====================
	if ($("#hPolCtroContableC").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro + " cCentroContable = '"
				+ $("#hPolCtroContableC").val() + "'";
	}

	// =================================== Fecha de Captura ===================
	if ($("#fCapturaC").val() != "" && $("#fCaptura2C").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103) >= "
				+ "CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCapturaC  ").val()
				+ "', 103)), 103)"
				+ " AND  CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103)  <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCaptura2C  ").val() + "', 103)), 103)";

	} else if ($("#fCapturaC").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103) >= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCapturaC  ").val() + "', 103)), 103)";
	} else if ($("#fCaptura2C").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103) <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCaptura2C  ").val() + "', 103)), 103)";
	}
	// =================================== Fecha de Aplicacion ================
	if ($("#fAplicacionC").val() != "" && $("#fAplicacion2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103) >= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacionC").val()
				+ "', 103)), 103) AND  CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103)  <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacion2C").val() + "', 103)), 103)";

	} else if ($("#fAplicacionC").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103) >= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacionC").val() + "', 103)), 103)";

	} else if ($("#fAplicacion2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103) <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacion2C").val() + "', 103)), 103)";
	}

	// =================================== No. de poliza ======================
	if ($("#polizaC").val() != "" && $("#poliza2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro + " nFolioPoliza >= " + $("#polizaC").val()
				+ " AND  nFolioPoliza <=	" + $("#poliza2C").val();

	} else if ($("#polizaC").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro + " nFolioPoliza >= " + $("#polizaC").val();

	} else if ($("#poliza2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro + " nFolioPoliza <= " + $("#poliza2C").val();

	}

	// ========================== Tipo de Poliza ==============================
	if ($("#PolTipoC").val() != "") {
		elParametro = elParametro + " 	AND ";
		elParametro = elParametro + " cTipoPoliza = '" + $("#PolTipoC").val()
				+ "'";

	}

	// ========================== Estatus de Poliza ===========================
	if ($("#PolStatus").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro + " DocHAplicado= '" + $("#PolStatus").val()
				+ "'";
	}

	// ========================== Origen de Poliza ============================
	if ($("#PolOrigen").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro + " ctipodocumento= '"
				+ $("#PolOrigen").val() + "'";
	}

	var szTabla = "BUSCARMOV_POLIZA";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {
		for ( var i = 0; i < j.length; i++) {
			$('#pMovimiento').dataTable().fnAddData(
					[ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4,
							j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8 ]);
		}
		document.getElementById("esperar").style.visibility = "hidden";
		document.getElementById("esperardet").style.visibility = "hidden";
	});
}

function carga() {

	querySelectPost({
		queryName : "catalogoTipoPoliza",
		targetObjectId : "PolTipo",
		async : false,
		callback : function() {
			// $('#PolTipo option:eq("DI")').attr('selected', 'selected');
			$('#PolTipo').val('PI');
		}
	});

	querySelectPost("catalogoTipoPoliza", "PolTipoAut", {
		async : false
	});
	querySelectPost("catalogoTipoPoliza", "PolTipoRev", {
		async : false
	});
	querySelectPost("catalogoPolTipoPolizaM", "PolTipoC", {
		async : false,
		callback : function() {
			$('#PolTipoC').val('PI');
		}
	});
	querySelectPost("catalogoEjercicioFiscalRead", "EF", {
		async : false
	});
	querySelectPost("catalogoEjercicioFiscalRead", "EFC", {
		async : false
	});
	querySelectPost("catalogoPolStatus", "PolStatus", {
		async : false
	});
	querySelectPost("catalogoPolOrigenPolizaM", "PolOrigen", {
		async : false
	});
	document.getElementById("esperar").style.visibility = "hidden";
	document.getElementById("esperardet").style.visibility = "hidden";

	if (usuarioCCentroContable == '10') {
		querySelectPost("CatalogoCentroContTodoRead", "hPolCtroContable", {
			async : false,
			callback : function() {
				updateCCont();
			}
		});
		querySelectPost("CatalogoCentroContTodoRead", "hPolCtroContableRev", {
			async : false
		});
		querySelectPost("CatalogoCentroContTodoRead", "hPolCtroContableAut", {
			async : false
		});
		querySelectPost("CatalogoAlmacenVacioRead", "ALM", {
			async : false
		});
	} else {
		querySelectPost("CatalogoCentroContRead", "hPolCtroContable", {
			async : false,
			callback : function() {
				updateCCont();
			}
		});
		querySelectPost("CatalogoCentroContRead", "hPolCtroContableRev", {
			async : false
		});
		querySelectPost("CatalogoCentroContTodoRead", "hPolCtroContableAut", {
			async : false
		});
	}

	ocultaOperacionesFlujo();
	$("#divConsulta").hide();
	$("#cComentarios").hide();
	$("#lbObservaciones").hide();

	/*
	 * Si el ncambio es 0, no se ha guardado la poliza, tomamos por default el
	 * centro contable seleccionado por el usuario, en caso contrario,
	 * utilizamos el de la poliza
	 */
	$("#tba4").hide();
	$("#tba5").hide();
	$(".EleAut").attr('disabled', true);
	queryFormPost("tdescripcionPolizaCentroContRead", "CtroContable", {
		async : false
	});
	$("#cCentroContable").val($("#hPolCtroContable").val());
	folio();

}

function detallepoliza() {
	document.getElementById("esperar").style.visibility = "visible";
	document.getElementById("esperardet").style.visibility = "visible";

	queryFormPost("tPolizaCentroContRead", {
		async : false
	});

	var elParametro = "nFolioPoliza=" + $("#polizaDet").val()
			+ " AND cTipoDocumento= '" + $("#PolOrigenDet").val()
			+ "' AND cCentroContable='" + $("#hPolCtroContableDet2").val()
			+ "' AND mMovimiento !=0 ";
	var szTabla = "TPOLIZAMOVIMIENTOS";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {
		var abono = 0;
		var cargo = 0;
		for ( var i = 0; i < j.length; i++) {
			if (j[i].Col2 == 'C') {
				cargo = j[i].Col3;
				abono = 0;
			} else {
				abono = j[i].Col3;
				cargo = 0;
			}
			$('#ppMovimientoDetalle').dataTable().fnAddData(
					[ j[i].Col0, j[i].Col1, cargo, abono ]);
		}
		document.getElementById("esperar").style.visibility = "hidden";
		document.getElementById("esperardet").style.visibility = "hidden";
	});
	$("#tba3").click();
}

function limpiaPantallaBusqueda() {
	$("#fCapturaC").val("");
	$("#fCaptura2C").val("");
	$("#fAplicacionC").val("");
	$("#fAplicacion2C").val("");
	$("#polizaC").val("");
	$("#poliza2C").val("");
	$("#PolTipoC").val("DI");
	$("#PolOrigen").val("");
	$("#PolAutomatica").val("0");
	$("#PolStatus").val("");
	$("#polizaC").focus();
}

function restringeTipoPol() {
	$("#tipoPolizaSel").val($("#PolTipo option:selected").val());
	$('#PolTipo option').each(function(index, option) {
		$(option).remove();
	});
	querySelectPost({
		queryName : "catalogoTipoPolizaRestringido",
		targetObjectId : "PolTipo",
		async : false
	});
}

function validaCuenta(nCuenta, nMes, cCentroContable, operacion) {
	var r = false;

	$.ajax({
		url : '../CuentaContable/VerificaBloqueo',
		dataType : 'json',
		data : {
			"accion" : "VericaBloqueoCuenta",
			"nCuenta" : nCuenta,
			"nMes" : nMes,
			"cCentroContable" : cCentroContable,
			"operacion" : operacion

		},
		async : false,
		success : function(json) {
			r = json.data_1.result == 'true';
			if (r) {
				alert("La cuenta " + nCuenta
						+ " est\u00E1 bloqueada en el mes " + nMes + " para "
						+ ("C" == operacion ? "cargos" : "abonos")
						+ "\nPor favor seleccione otra cuenta.");
				$("#nCuenta").focus();
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
	return r;
}

function validaCuentasBloqueadas() {
	var rows = oTableCuen.fnGetData();
	var rowCount = rows.length;

	var cargoGrd = '';
	var abonoGrd = '';
	var cuentaGrd = '';
	var cuentasDeCargo = '';
	var cuentasDeAbono = '';
	var tokenCargos = '';
	var tokenAbonos = '';
	var arrRes = Array();

	for ( var i = 0; i < rowCount; i++) {
		var row = rows[i];
		var cuentaGrd = row[1];
		var cargoGrd = row[5];
		var abonoGrd = row[6];

		if (Number(quitaFmt(cargoGrd)) != 0) {
			cuentasDeCargo += tokenCargos + "'" + cuentaGrd + "'";
			tokenCargos = ",";
		} else if (Number(quitaFmt(abonoGrd)) != 0) {
			cuentasDeAbono += tokenAbonos + "'" + cuentaGrd + "'";
			tokenAbonos = ',';
		}
	}
	var nMes = $("#nMes").val();
	var cCentroContable = $("#cCentroContable").val();

	if (rowCount>0)  
	$.ajax({
		url : '../CuentaContable/VerificaBloqueo',
		dataType : 'json',
		data : {
			"accion" : "VerificaCuentas",
			"cuentasCargo" : cuentasDeCargo,
			"cuentasAbono" : cuentasDeAbono,
			"nMes" : nMes,
			"cCentroContable" : cCentroContable
		},
		async : false,
		success : function(RS) {
			var index, data, col;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						for ( var name in col) {
							arrRes.push(eval("col." + name));
						}
					}
				}
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});

	return arrRes;
}

function renumeraTabla() {

	var rows = oTableCuen.fnGetData();
	var rw = 1;

	for ( var i = 0; i < rows.length; i++) {
		oTableCuen.fnUpdate(rw, i, 0);
		rw++;
	}

	renglon = rw;

}

function blurCtaBanc() {
	if ($("#idCuenta").val() != '')
		queryFormPost({
			queryName : "leeCtaBan",
			async : false,
			callback : function() {
				if ($("#CTABAN").val() == '') {
					alert("No se encontro la cuenta con indice "
							+ $("#idCuenta").val());
					return false;
				} else {
					if (ultimoTipoMovimiento == 'A')
						$("#Abonos").focus();
					else
						$("#Cargos").focus();
					return false;
				}
			}
		});
}
function blurNCta() {
	$("#divACnCuenta").hide();
	var nCta = $("#nCuenta").val();
	var arrNCta = [ '00000', '00000', '00000', '00000' ];
	var ceros = '00000';
	if (nCta.indexOf("-") > 0) {
		var quintetos = nCta.split("-");
		for ( var i = 0; i < quintetos.length; i++) {
			var ctaElem = ceros.substring(quintetos[i].length) + quintetos[i];
			arrNCta[i] = ctaElem;
		}
	} else
		arrNCta[0] = ceros.substring(nCta.length) + nCta;

	nCta = arrNCta.join("-");
	$("#nCuenta").val(nCta);

	if ($("#nCuenta").val().length == 23) {
		queryFormPost({
			queryName : "leeAttrCuenta",
			async : false,
			callback : function() {
				if ($("#dCuenta").val() == "") {
					alert("La cuenta " + $("#nCuenta").val()
							+ " no existe o no es una cuenta de aplicacion.");
					$("#nCuenta").val('');
					$("#nCuenta").focus();
					return false;
				}
				tipoSubCuenta();
				$("#divACnCuenta").hide();
				return true;
			}
		});

	}
	return false;
}

function tipoSubCuenta() {
	if ($("#nSubCuenta").val() == "RFC") {
		$("#lblAuxiliar").text("RFC:");
		$("#sinAuxiliar").hide();
		$("#divRFC").show();
		$("#cIDRFC").focus();

		$("#divCTAB").hide();
		$("#divAML").hide();
	} else if ($("#nSubCuenta").val() == "CTAB") {
		$("#divRFC").hide();

		$("#divCTAB").show();
		$("#lblAuxiliar").text("Cta. Ban:");
		$("#sinAuxiliar").hide();
		$("#idCuenta").focus();

		$("#divAML").hide();
	} else if ($("#nSubCuenta").val() == "ALM") {
		$("#hPolCtroContable").attr("disabled", false);
		cambioALM();
		$("#hPolCtroContable").attr("disabled", true);
		$("#divAML").show();
		$("#ALM").focus();
		$("#lblAuxiliar").text("Almacen:");
		$("#sinAuxiliar").hide();

		$("#divCTAB").hide();
		$("#divRFC").hide();
	} else {
		ocultaSubCuentas();
		$("#Cargos").focus();
	}
}

function cierraAyuda() {
	$("#divACnCuenta").hide();
}

function showAjuste() {
	if ($("#nMes").val() != 13) {
		$("#Periodo13Div").hide();
		$("#periodo13RevDiv").hide();
		$("#periodo13AutDiv").hide();

		$("#AjusteCapt").hide();
		$("#AjusteRev").hide();
		$("#AjusteAut").hide();

	} else {
		$("#Periodo13Div").show();
		$("#periodo13RevDiv").show();
		$("#periodo13AutDiv").show();

		$("#periodo13").val("S");
		$("#periodo13Rev").val("S");
		$("#periodo13Aut").val("S");

		$("#AjusteCapt").show();
		$("#AjusteRev").show();
		$("#AjusteAut").show();
	}
}

function updateAjuste() {
	$("#nTipoAjusteRev").val($("#nTipoAjuste").val());
	$("#nTipoAjusteAut").val($("#nTipoAjuste").val());
}