/**Bandera que indica si se debe insertar o actualizar el registro en el detalle**/
var insertar = false;

/**EP que genera los "Debe Decir"**/
var epPadreDebeDecir = "";

/**Renglon que se esta editando.**/
var nDocRenglonEditar = "";
var nDocRenglonPadreEditar= "";


/**Memoria para guardar el monto a editar. Si cierran el dialogo (por cancelar o cerrar) suma esto al remanente**/
var montoEditar = 0.0;  

/** Para regresar la solicitud (caNoContrarrecibo del anexo1.) **/
var solicitud = "";
/**Crea el dialogo para editar el debe decir**/
function creaDialogoEditar() {
	$("#EdtaRenglon").dialog({
		autoOpen : false,
		height : 332,
		width : 640,
		modal : true,
		buttons : {
			"Agregar" : function() {
				agregarDebeDecir();
			},
			"Cancelar" : function() {

				var totalDD = parseFloat($("#totalDebeDecir").val());
				montoEditar = parseFloat(montoEditar);

				$("#totalDebeDecir").val( (totalDD + parseFloat(montoEditar)).toFixed(2) );
				$(this).dialog("close");
			}
		},
		close : function(){}
	});
}

/**
 * Funcion que se llama al dar doble clic sobre un renglon en tabla de pago.
 * Muestra la ventana interna para la seleccion de la EP del DEBE DECIR <br>
 * Se asume que la tabla tiene la siguiente estructura: <br>
 * aData[n][0] = Renglon <br>
 * aData[n][1] = Mes <br>
 * aData[n][2] = Solicitud <br>
 * aData[n][3] = EP <br>
 * aData[n][4] = Evento<br>
 * aData[n][5] = Importe Neto <br>
 * aData[n][6] = Remanente <br>
 * aData[n][7] = Descartar secuencia <br>
 * aData[n][8] = EP Padre
 * 
 * @param event
 */
function tblPagoDblClick(event) {

	limpiaDebeDecir();
	montoEditar = 0.0;
	/* VGC Obtengo los valores del renglon para mostrarlos en la ventana */
	var aPos = oTable.fnGetPosition(event.target.parentNode);
	var aData = oTable.fnGetData(aPos);
	
	epPadreDebeDecir = aData[8];
	var EPDice = aData[3];
	var nMesDice = aData[1];
	var mImporteDice = aData[6];
	solicitud = aData[2];
	// Si es una ep del pago ...
	if (epPadreDebeDecir == "") {
		insertar = true;
		nDocRenglonPadreEditar=aPos;
	} else {
		insertar = false;
		mImporteDice = aData[5];
		nDocRenglonEditar = aPos;

		var totalDD = parseFloat($("#totalDebeDecir").val());
		montoEditar = parseFloat(mImporteDice);

		$("#totalDebeDecir").val((totalDD - parseFloat(mImporteDice)).toFixed(2));

		$("#EPDebeDecir").val(EPDice);
		$("#MesDebeDecir").val(nMesDice);

		$("#ImporteDebeDecir").val(mImporteDice);
	}

	$("#EPDice").val(EPDice);
	$("#nMesDice").val(nMesDice);
	$("#mImporteDice").val(mImporteDice);
	$("#solicitud").val(solicitud);

	$("#EdtaRenglon").dialog("open");
}

/**
 * Abre la ventana de seleccion de EPs
 * @returns {Boolean}
 */
function Grid() {
	window.open('../Generador/AyudEPsPresupuestos.jsp?cEsRadicado=1', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');	
	return false;
}

/**
 * Valida si el remanente capturado es mayor al remanente.
 * @returns Mensaje de validacion
 */
function validaImporteCapturado() {
	var importeEP = parseFloat(quitaFrmt($("#ImporteDebeDecir").val()));
	var importeRemanente = $("#totalDice").val();
	var importeDice = parseFloat(quitaFrmt($("#mImporteDice").val()));

	if (importeEP > importeRemanente || importeDice > importeRemanente)
		return "\nLos importes del Dice o del Debe Decir capturados superan el remanente.";
	else
		return "";
}

/**
 * Valida que se haya capturado una EP.
 * @returns {String}
 */
function validaEPCapturada() {
	if ($("#EPDebeDecir") == "")
		return "\nDebe seleccionar una EP.";
	else
		return "";
}
/**Retira el formato monetario de una cadena;**/
function quitaFrmt(fld) {
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}

function agregarDebeDecir() {
	var msg = "";
	msg += validaImporteCapturado();
	msg += validaEPCapturada();

	if (msg != "") {
		alert("Para continuar verifique lo siguiente:" + msg);
	} else {

		var totalDD = parseFloat(quitaFrmt($("#totalDebeDecir").val()));
		var mes = $("#MesDebeDecir").val();
		var mesPadre = $("#nMesDice").val();
		var montoDice = $("#mImporteDice").val();
		var nRenglon = (oTable.fnGetData().length) + 1;
		var eventoDebeDecir = "DEBE DECIR";
		var eventoDice = "DICE";
		var montoDebeDecir = parseFloat($("#ImporteDebeDecir").val());
		var epDebeDecir = $("#EPDebeDecir").val();
		var epPadre = $("#EPDice").val();
		var cmdBorrar = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar Renglon\" onClick=\"descartar('"
				+ epDebeDecir
				+ "','"
				+ mes
				+ "','"
				+ eventoDebeDecir
				+ "'"
				+ "," + nRenglon + ");\" />";

		if (!existeEPRegistrada(epDebeDecir, mes, eventoDebeDecir, solicitud, epPadre+";"+mesPadre) && insertar) {

			oTable.fnAddData([ nRenglon, mes, solicitud, epDebeDecir, eventoDebeDecir, montoDebeDecir, "-", cmdBorrar, epPadre + ';' + mesPadre ]);
			
			oTable.fnUpdate([
				                 oTable.fnGetData()[nDocRenglonPadreEditar][0], 
				                 oTable.fnGetData()[nDocRenglonPadreEditar][1],
				                 solicitud,
				                 oTable.fnGetData()[nDocRenglonPadreEditar][3], 
				                 oTable.fnGetData()[nDocRenglonPadreEditar][4], 
				                 montoDice, 
				                 oTable.fnGetData()[nDocRenglonPadreEditar][6], 
				                 oTable.fnGetData()[nDocRenglonPadreEditar][7], 
				                 oTable.fnGetData()[nDocRenglonPadreEditar][8]
			                 ],
			                 nDocRenglonPadreEditar
			);

			
			totalDD = totalDD + montoDebeDecir;
			$("#totalDebeDecir").val(totalDD.toFixed(2));
			$("#totalDice").val(montoDice);
			$("#totalDiceValor").val(montoDice);
			$("#EdtaRenglon").dialog("close");
		} else if (!insertar) {
			oTable.fnUpdate([ oTable.fnGetData()[nDocRenglonEditar][0], mes, solicitud, epDebeDecir, eventoDebeDecir, montoDebeDecir, "-", cmdBorrar, epPadreDebeDecir ], nDocRenglonEditar);
			totalDD = totalDD + montoDebeDecir;
			$("#totalDebeDecir").val(totalDD.toFixed(2));
			$("#EdtaRenglon").dialog("close");

		} else {
			alert("Ya existe un registro con la informacion:\nEP[" + epDebeDecir + "]\nMes[" + mes + "]");
		}
		sumaImporteDiceDebeDecirReal();
	}

}
function sumaImporteDiceDebeDecirReal(){ 
	var tListado = $("#tblPagadoFiltrado").dataTable().fnGetData();
	var mImporteDice = 0.00;
	var mImporteDebeDecir = 0.00;
	for (i=0; i<tListado.length;i++){
		if(tListado[i][4] == "DICE")
			mImporteDice += Number(tListado[i][5]);
		if(tListado[i][4] == "DEBE DECIR")
			mImporteDebeDecir += Number(tListado[i][5]);
	}
	$("#totalDice").val(mImporteDice.toFixed(2));
	$("#totalDiceValor").val(mImporteDice.toFixed(2));
	$("#totalDebeDecir").val(mImporteDebeDecir.toFixed(2));
}
/**
 * Busca en la tabla de detalle una renglon que cumpla con los criterios de EP,
 * Mes, Evento.<br>
 * Se asume que la tabla tiene la siguiente estructura: <br>
 * aData[n][0] = Renglon <br>
 * aData[n][1] = Mes <br>
 * aData[n][2] = Solicitud <br>
 * aData[n][3] = EP <br>
 * aData[n][4] = Evento<br>
 * aData[n][5] = Importe Neto <br>
 * aData[n][6] = Remanente <br>
 * aData[n][7] = Descartar secuencia <br>
 * aData[n][8] = EP Padre
 * 
 * @param ep
 *            EP a buscar
 * @param mes
 *            mes en el que se afecta la EP
 * @param evento
 *            Evento de la EP
 * @returns {Boolean} true si y solo si existe una elemento en la tabla que
 *          corresponde uno a uno con los parametros especificados.
 */
function existeEPRegistrada(ep, mes, evento, sol, epPadre) {
	var info = oTable.fnGetData();
	var existe = false;

	//for (i = 0; i < info.length; i++) {
		//var renglonInfo = info[i];
		//if (renglonInfo[3] == ep && renglonInfo[1] == mes && renglonInfo[4] == evento && renglonInfo[2] == sol && renglonInfo[8] == epPadre) {
			//existe = true;
			//break;
		//}
	//}
	return existe;
}

/**
 * Limpia los elementos de la ventana de captura.
 */
function limpiaDebeDecir() {
	$("#EPDebeDecir").val("");
	$("#MesDebeDecir").val("1");
	$("#ImporteDebeDecir").val("");
}

/**
 * Elimina un renglon del detalle. Actuliza montos.
 * 
 * @param ep
 *            Ep del renlon a eliminar
 * @param mes
 *            mes del renlon a eliminar
 * @param evento
 *            evento del renlon a eliminar
 */
function descartar(ep, mes, evento, monto) {
	if (confirm("Esta seguro de borrar el renglon?\n Si se han agregado renglones que dependen de el, se eliminaran tambien.")) {
		var info = oTable.fnGetData();

		for (i = 0; i < info.length; i++) {
			var renglonInfo = info[i];
			monto = renglonInfo[5];
			if (renglonInfo[3] == ep && renglonInfo[1] == mes && renglonInfo[4] == evento && renglonInfo[5] == monto) {
				if (renglonInfo[7] != "") {
					var montoDescontar = parseFloat(quitaFrmt(renglonInfo[5]));
					var montoDD = parseFloat(quitaFrmt($("#totalDebeDecir").val()));
					oTable.fnDeleteRow(i);
					$("#totalDebeDecir").val(
							(montoDD - montoDescontar).toFixed(2));
					break;
				} else {
					descartaPadreHijos(ep, mes, evento, ep + ";" + mes, monto);
					break;
				}
			}
		}
		renumeraRenglones();
		sumaImporteDiceDebeDecirReal();
	}
}
	
function descartaPadreHijos(ep, mes, evento, epMesPadre, monto) {

	for (k = oTable.fnGetData().length - 1; k >= 0; k--) {
		var renglonInfo = oTable.fnGetData()[k];
		if (epMesPadre == renglonInfo[8] && "DEBE DECIR" == renglonInfo[4]) {
			var montoDescontar = parseFloat(quitaFrmt(renglonInfo[5]));
			var montoDD = parseFloat(quitaFrmt($("#totalDebeDecir").val()));
			oTable.fnDeleteRow(k);
			$("#totalDebeDecir").val((montoDD - montoDescontar).toFixed(2));
		}
	}

	for (k = oTable.fnGetData().length - 1; k >= 0; k--) {
		var renglonInfo = oTable.fnGetData()[k];
		if (ep == renglonInfo[3] && "DICE" == renglonInfo[4]
				&& mes == renglonInfo[1] && parseFloat(monto) == parseFloat( renglonInfo[5] ) ) {
			var montoDescontar = parseFloat(quitaFrmt(renglonInfo[5]));
			var montoDD = parseFloat(quitaFrmt($("#totalDebeDecir").val()));
			oTable.fnDeleteRow(k);
		}
	}
}

function renumeraRenglones() {

	for ( var i = 0; i < oTable.fnGetData().length; i++) {
		oTable.fnUpdate(i + 1, i, 0);
	}

}