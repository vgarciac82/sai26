/**
 * Bandera que indica si se debe insertar o actualizar el registro en el detalle
 */
var insertar = false;

/**
 * EP que genera los "Debe Decir"
 */
var epPadreDebeDecir = "";

/**
 * Renglon que se esta editando.
 */
var nDocRenglonEditar = "";
var nDocRenglonPadreEditar= "";

/**
 * Memoria para guardar el monto a editar. Si cierran el dialogo (por cancelar o
 * cerrar) suma esto al remanente
 */
var montoEditar = 0.0;

/** Para regresar la solicitud (caNoContrarrecibo.) **/
var solicitud = "";

var sinSaldo = false;
/**
 * Crea el dialogo para editar el debe decir
 */
function creaDialogoEditar() {	
	
	$("#EdtaRenglon")
			.dialog(
					{
						autoOpen : false,
						height : 600,
						width : 800,
						modal : true,
						buttons : {
							"Agregar" : function() {
								//validarMesRectificacion();
								validaSaldo();
								if(sinSaldo){
									$(this).dialog("close");
								} else {
									agregarDebeDecir();								
									//mostrarProgSubProg();
								}
							},
							"Cancelar" : function() {

								var totalDD = parseFloat($("#totalDebeDecir").val());
								montoEditar = parseFloat(montoEditar);

								$("#totalDebeDecir").val((totalDD + parseFloat(montoEditar)).toFixed(2));
								$(this).dialog("close");
							}
						},
						
						open: function(){
							//Al abrir muestra la captura de programa subprograma si el beneficiario es FIB Banorte.
							mostrarProgSubProg();							
						},
						close : function() {
						}
					});
}

/**
 * Funcion que se llama al dar doble clic sobre un renglon en tabla de pago.
 * Muestra la ventana interna para la seleccion de la EP del DEBE DECIR <br>
 * Se asume que la tabla tiene la siguiente estructura: <br>
 * aData[n][0] = Renglon <br>
 * aData[n][1] = Solicitud <br>
 * aData[n][2] = Mes <br>
 * aData[n][3] = EP <br>
 * aData[n][4] = Evento<br>
 * aData[n][5] = Importe Neto <br>
 * aData[n][6] = Remanente <br>
 * aData[n][7] = Descartar secuencia <br>
 * aData[n][8] = EP Padre <br>
 * aData[n][9] = Programa FFM <br>
 * aData[n][10] = Subprograma FFM <br>
 * 
 * @param event
 */
function tblPagoDblClick(event) {
	creaDialogoEditar();
	limpiaDebeDecir();		
	montoEditar = 0.0;
	/* VGC Obtengo los valores del renglon para mostrarlos en la ventana */
	var aPos = oTable.fnGetPosition(event.target.parentNode);
	var aData = oTable.fnGetData(aPos);

	epPadreDebeDecir = aData[8];
	var EPDice = aData[3];
	var nMesDice = aData[2];
	var mImporteDice = aData[6];
	var idPrograma = aData[9];
	var nSubPrograma = aData[10];
	
	solicitud = aData[1];
	
	if($("#caNoContrarrecibo").val().substring(2, 4) == "OA"){	
		$("#CxP").val(solicitud);
		queryFormPost("tipoPagoOrigen", {async : false});
		if($("#dTipoPago").val() == ""){			
			$("#dTipoPago").val($("#dTipoPagoOA").val());
			$("#dTipoPagoOA").val("");
		} else if($("#dTipoPago").val() != $("#dTipoPagoOA").val()){
			Swal.fire({ icon: 'warning',
						text: "El origen del pago anterior fue " + $("#dTipoPago").val() + ".\n El origen del pago actual es " +  $("#dTipoPagoOA").val() + ".\n No es posible mezclar pagos de diferente origen" });			
			exit;	
		}		
	}

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
	$("#idProgramaDice").val( idPrograma );
	$("#idSubProgramaDice").val( nSubPrograma );
	$("#EdtaRenglon").dialog("open");	
}

function tblPagoDDDblClick(event) {
	creaDialogoEditar();
	limpiaDebeDecir();		
	montoEditar = 0.0;
	/* VGC Obtengo los valores del renglon para mostrarlos en la ventana */
	var aPos = oTableDD.fnGetPosition(event.target.parentNode);
	var aData = oTableDD.fnGetData(aPos);

	epPadreDebeDecir = aData[8];
	var EPDice = aData[3];
	var nMesDice = aData[2];
	var mImporteDice = aData[6];
	var idPrograma = aData[9];
	var nSubPrograma = aData[10];
	
	solicitud = aData[1];	
	
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
	$("#idProgramaDice").val( idPrograma );
	$("#idSubProgramaDice").val( nSubPrograma );
	$("#EdtaRenglon").dialog("open");	
}

/**
 * Abre la ventana de seleccion de EPs
 * 
 * @returns {Boolean}
 */
function Grid() {
	var tipoRectificacion = $("#dTipoPago").val();
	
	if($("#cEsIP").val() == "S"){
		window.open('../Generador/AyudEPsPresupuestos.jsp?fireChange=true&FuenteFinanciamiento=4&tipoRectificacion='+tipoRectificacion, 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	}else{
		window.open('../Generador/AyudEPsPresupuestos.jsp?fireChange=true&tipoRectificacion='+tipoRectificacion, 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	}
	
	return false;
}

/**
 * Valida si el remanente capturado es mayor al remanente.
 * 
 * @returns Mensaje de validacion
 */
function validaImporteCapturado() {
	var importeEP = parseFloat(quitaFrmt($("#ImporteDebeDecir").val()));
	var importeRemanente = parseFloat(quitaFrmt($("#mImporteDice").val()));
	var importeDice = $("#totalDice").val();

	if (importeEP > importeRemanente || importeDice > importeRemanente)
		return "\nLos importes del Dice o del Debe Decir capturados superan el remanente.";
	else
		return "";
}

/**
 * Valida que se haya capturado una EP.
 * 
 * @returns {String}
 */
function validaEPCapturada() {
	if ($("#EPDebeDecir") == ""){
		return "\nDebe seleccionar una EP.";
	}else{
		queryFormPost("esBeneficiarioFIBBanorteRead", {async : false});
		if($("#esFIBBanorte").val() == "SI"){
			var cPrograma = $("#EPDebeDecir").val();
			cPrograma = cPrograma.substring(56, 59);
			$("#cPrograma").val(cPrograma);
			querySelectPost( "catalogoFideicomisoRead", "cboPrograma", {async : false} );
		}
		return "";
	}
}

function validarMesRectificacion(){
	queryFormPost("convertirEPDice", {async : false});
	queryFormPost("convertirEPDebeDecir", {async : false});
	if($("#EPDice1").val().substring(1, 56) == $("#EPDebeDecir1").val().substring(1, 56) && $("#nMesDice").val() != $("#MesDebeDecir").val()){
		return "Esta es una reclasificacion contable, el calendario del Debe Decir tiene que ser el mismo del Dice. \n Las claves cortas son iguales. \n Contacta con presupuestos. ";				
	}else return "";
}
/**
 * Retira el formato monetario de una cadena;
 * 
 */
function quitaFrmt(fld) {
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}

function agregarDebeDecir() {
	var msg = "";
	var info = oTable.fnGetData();
	//msg += validaImporteCapturado();
	msg += validaEPCapturada();
	msg += validaCapturaProgSubProg();
	msg += validarMesRectificacion();

	if (msg != "") {
		Swal.fire({ icon: 'warning',
					text: "Para continuar verifique lo siguiente:\n" + msg });		
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
		
		var cIdPrograma = "-1";
		var cSubPrograma = "-1";
		
		if($("#esFIBBanorte").val() == "SI"){		
			cIdPrograma = $("#cboPrograma").val();
			cSubPrograma = $("#cboSubPrograma").val();		
		}

		if (!existeEPRegistrada(epDebeDecir, mes, eventoDebeDecir) && insertar) {
			
			//Inserta renglon del DEBE DECIR
			oTableDD.fnAddData([ nRenglon,
								 solicitud, 
								 mes, 
							  	 epDebeDecir, 
								 eventoDebeDecir,
								 montoDebeDecir, 
								 "-", 
								 cmdBorrar,
								 "0",
								 cIdPrograma, 
								 cSubPrograma 
							]);
			
			//Inserta renglon del DICE en DEBE DECIR
			oTableDD.fnAddData([
			                 oTable.fnGetData()[nDocRenglonPadreEditar][0],
			                 solicitud,
			                 oTable.fnGetData()[nDocRenglonPadreEditar][2], 
			                 oTable.fnGetData()[nDocRenglonPadreEditar][3], 
			                 oTable.fnGetData()[nDocRenglonPadreEditar][4], 
			                 montoDice, 
			                 oTable.fnGetData()[nDocRenglonPadreEditar][6], 
			                 oTable.fnGetData()[nDocRenglonPadreEditar][7], 
			                 oTable.fnGetData()[nDocRenglonPadreEditar][8],
			                 oTable.fnGetData()[nDocRenglonPadreEditar][9],
			                 oTable.fnGetData()[nDocRenglonPadreEditar][10]
		                 ]);
			
			//Elimina renglon del DICE
			for (i = 0; i < info.length; i++) {
				var renglonInfo = info[i];
				if (renglonInfo[0] == oTable.fnGetData()[nDocRenglonPadreEditar][0]) {
					oTable.fnDeleteRow( i );
					break;
				}		
			}
			
			totalDD = totalDD + montoDebeDecir;
			$("#totalDebeDecir").val(totalDD.toFixed(2));
			$("#totalDice").val(montoDice);
			$("#totalDiceValor").val(montoDice);
			$("#EdtaRenglon").dialog("close");
		} else if (!insertar) {
			oTableDD.fnUpdate([ oTableDD.fnGetData()[nDocRenglonEditar][0], solicitud, mes,
					epDebeDecir, eventoDebeDecir, montoDebeDecir, "-",
					cmdBorrar,"0", cIdPrograma, cSubPrograma ], nDocRenglonEditar);
			totalDD = totalDD + montoDebeDecir;
			$("#totalDebeDecir").val(totalDD.toFixed(2));
			$("#EdtaRenglon").dialog("close");

		} else {
			Swal.fire({ icon: 'warning',
						text: "Ya existe un registro con la informacion:\nEP[" + epDebeDecir + "]\nMes[" + mes + "]" });			
		}
		sumaImporteDiceDebeDecirReal();
		
	}

}
function sumaImporteDiceDebeDecirReal(){ 
	var tListado = $("#tblPagadoFiltradoDEBE").dataTable().fnGetData();
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
 * aData[n][2] = EP <br>
 * aData[n][3] = Evento<br>
 * aData[n][4] = Importe Neto <br>
 * aData[n][5] = Remanente <br>
 * aData[n][6] = Descartar secuencia <br>
 * aData[n][7] = EP Padre
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
function existeEPRegistrada(ep, mes, evento) {
	var info = oTableDD.fnGetData();
	var existe = false;

	for (i = 0; i < info.length; i++) {
		var renglonInfo = info[i];
		if (renglonInfo[3] == ep && renglonInfo[2] == mes
				&& renglonInfo[4] == evento) {
			existe = true;
			break;
		}

	}
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
	Swal.fire({		
			text: "Esta seguro de borrar el renglon?\n Si se han agregado renglones que dependen de el, se eliminaran tambien.",
			icon: "warning",
			showCancelButton: true,
		  	confirmButtonColor: "#7066E0",
		  	cancelButtonColor: "#e6e6e6",
		  	confirmButtonText: "Aceptar",
		  	cancelButtonText: "Cancelar"
		}).then((result) => {
			if(result.isConfirmed){
				var info = oTableDD.fnGetData();
	
				for (i = 0; i < info.length; i++) {
					var renglonInfo = info[i];
					monto = renglonInfo[5];
					if (renglonInfo[3] == ep && renglonInfo[2] == mes
							&& renglonInfo[4] == evento && renglonInfo[5] == monto) {
						if (renglonInfo[8] != "") {
							var montoDescontar = parseFloat(quitaFrmt(renglonInfo[5]));
							var montoDD = parseFloat(quitaFrmt($("#totalDebeDecir")
									.val()));
							oTableDD.fnDeleteRow(i);
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
			} else 
					return false;	
		})
		
	sumaImporteDiceDebeDecirReal();
}
	
function descartaPadreHijos(ep, mes, evento, epMesPadre, monto) {

	for (k = oTableDD.fnGetData().length - 1; k >= 0; k--) {
		var renglonInfo = oTableDD.fnGetData()[k];
		if (epMesPadre == renglonInfo[8] && "DEBE DECIR" == renglonInfo[4]) {
			var montoDescontar = parseFloat(quitaFrmt(renglonInfo[5]));
			var montoDD = parseFloat(quitaFrmt($("#totalDebeDecir").val()));
			oTableDD.fnDeleteRow(k);
			$("#totalDebeDecir").val((montoDD - montoDescontar).toFixed(2));
		}
	}

	for (k = oTableDD.fnGetData().length - 1; k >= 0; k--) {
		var renglonInfo = oTableDD.fnGetData()[k];
		if (ep == renglonInfo[3] && "DICE" == renglonInfo[4]
				&& mes == renglonInfo[2] && parseFloat(monto) == parseFloat( renglonInfo[5] ) ) {
			var montoDescontar = parseFloat(quitaFrmt(renglonInfo[5]));
			var montoDD = parseFloat(quitaFrmt($("#totalDebeDecir").val()));			
			oTable.fnAddData([
			                 renglonInfo[0],
			                 renglonInfo[1],
			                 renglonInfo[2], 
							 renglonInfo[3], 
			                 renglonInfo[4], 
			                 renglonInfo[6],
			                 renglonInfo[6], 
			                 renglonInfo[7], 
			                 renglonInfo[8],
			                 renglonInfo[9],
			                 renglonInfo[10],
		                 ]);
			oTableDD.fnDeleteRow(k);
		}
	}
}

function renumeraRenglones() {

	for ( var i = 0; i < oTable.fnGetData().length; i++) {
		oTable.fnUpdate(i + 1, i, 0);
	}

}
/**
 * Actualiza los subprogramas segun la seleccion del programa
 */
function cambiaPrograma() {
	$("#nIDPrograma").val($("#cboPrograma").val());
	clearSelect("cboSubPrograma");
	querySelectPost("catalogoSubprgRead", "cboSubPrograma", {
		async : false
	});
}
/**
 * Funcion general que limpia el contenido de un select agregando una opcion por
 * default con valor -1
 */
function clearSelect(idSel) {
	for ( var i = 0; i < idSel.length; i++)
		$('#' + idSel[i]).find('option').remove().end().append(
				'<option value="-1"></option>');
}
/**
 * Valida que se haya capturado el programa -- subprograma
 * Solo cuando el beneficiario sea FIB Banorte 'BMN930209927'
 * @returns {String}
 */
function validaCapturaProgSubProg() {
	var msg = "";
	var token = "";
	var iNReg = document.getElementById("cboPrograma").options.length;

	if($("#esFIBBanorte").val() == "SI"){
		
		if(iNReg <= 1){
			msg = "La EP que selecciono no tiene Programa -- SubPrograma asignado.\nFavor de realizar la asignación.";
			token = "\n";			
		}
		if ($("#cboPrograma").val() == "-1") {
			msg +=  token + "Debe seleccionar el programa.";			
		}
		if ($("#cboSubPrograma").val() == "-1" ) {
			msg +=  token + "Debe seleccionar el subprograma.";
		}
	}
	return msg;
}

function llenaFirmanteVoBo() {
	$("#cTipoFirmante").val("VOBO")
	querySelectPost("FirmantesPorTipo_Read", "cboVoBo", {
		async : false
	});
}

function llenaFirmanteAut() {
	$("#cTipoFirmante").val("AUT")
	querySelectPost("FirmantesPorTipo_Read", "cboAutoriza", {
		async : false
	});
}

function llenaSuplenteVoBo() {
	$("#cTipoFirmante").val("SUPVOBO")
	querySelectPost("FirmantesPorTipo_Read", "cboSuplenteVoBo", {
		async : false
	});

}

function llenaSuplenteAut() {
	$("#cTipoFirmante").val("SUPAUT")
	querySelectPost("FirmantesPorTipo_Read", "cboSuplenteAut", {
		async : false
	});
}



function infoEmpleado(tipoFirmante) {
	$("#cNombreEmpleado").val();
	$("#cPaternoEmpleado").val();
	$("#cMaternoEmpleado").val();
	$("#cPuestoEmpleado").val();
	$("#cTipoFirmante").val(tipoFirmante)

	var numeroEmpleado = -1;
	var postFijo = ""

	if( "VOBO" == tipoFirmante ) {
		numeroEmpleado = $("#cboVoBo").val();
		postFijo = "VoBo";
	} else if( "AUT" == tipoFirmante ) {
		numeroEmpleado = $("#cboAutoriza").val();
		postFijo = "Aut";
	} else if( "SUPAUT" == tipoFirmante ) {
		numeroEmpleado = $("#cboSuplenteAut").val();
		postFijo = "Titular";
	} else if( "SUPVOBO" == tipoFirmante ) {
		numeroEmpleado = $("#cboSuplenteVoBo").val();
		postFijo = "TitularVoBo";
	}

	limpiaFirmante(postFijo);

	if( parseInt(numeroEmpleado, 10) > 0 ) {
		$("#nNumEmpleadoBusqueda").val(numeroEmpleado);
		queryFormPost({
			queryName : "infoComplementariaFirmanteRead",
			async : false,
			callback : function() {
				$("#cNombre" + postFijo).val($("#cNombreEmpleado").val());
				$("#cPaterno" + postFijo).val($("#cPaternoEmpleado").val());
				$("#cMaterno" + postFijo).val($("#cMaternoEmpleado").val());
				$("#cPuesto" + postFijo).val($("#cPuestoEmpleado").val());
			}
		});
	}

}

function limpiaFirmante(postFijo) {
	$("#cNombre" + postFijo).val("");
	$("#cPaterno" + postFijo).val("");
	$("#cMaterno" + postFijo).val("");
	$("#cPuesto" + postFijo).val("");
}


function tipoFirmantes() {
	$("#dialog-firmantes").dialog("open");
	llenaFirmanteVoBo();
	llenaFirmanteAut();
	llenaSuplenteVoBo();
	llenaSuplenteAut();
}

function showDivOficioVoBo(esUpdate){
	var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
	var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}

function showDivOficio(esUpdate){
	var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
	var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}

function validaSaldo(){	
	queryFormPost("validaSaldoEPDebeDecir", {async : false});	
	
	if(Number($("#disponibleEPDD").val()) < Number($("#ImporteDebeDecir").val())){
		Swal.fire({ icon: 'error',
					text: "La EP del Debe Decir no tiene suficiencia presupuestal. Saldo Disponible: " + $("#disponibleEPDD").val() + " Favor de revisar con el area de Presupuestos." });
		sinSaldo = true;
	} else {
		sinSaldo = false;
	}
	
	return sinSaldo;
}
