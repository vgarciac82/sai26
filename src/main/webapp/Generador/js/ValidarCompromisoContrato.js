/*
 //IRD 20131121	RO-0009 Todo lo de pago de pasivo
 * **************************************************************************
 * Funciones Generales.
 * **************************************************************************
 */
/**
 * Tabla que almacena la informacion de las EP's
 */
var dtEP;
var dtEPModif;
var dtEPPagoPasivo;
var dtEPPlurianual;
var esContratoPlurianual = false;
/**
 * Tabla que almacena la informacion de los anticipos.
 */
var dtAnt;

/**
 * Bandera para indicar si se debe terminar el caso. Cambia a true SII el motor
 * contable aplica correctamente
 */
var terminar = false;
/**
 * Lenguaje de las DataTables.
 */
var mx_esp = {
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
	sSearch : "Buscar:"
};

/**
 * Funcion que inicializa la pantalla.
 */
function init() {
	
	$("#fAdjudicacion").val($("#today").val());
	$("#fFirmaContrato").val($("#today").val());

	loadInfo();

	initWidgets();
	disableInputs();
	llenaInformacionAnticipos();
	creaDiagloFacturas(false);
	$("#trCargaFacturas").css("display", "none");
		
}

/**
 * Funcion que crea la tabla de anticipos
 */
function creaTablaAnticipo() {
	dtAnt = $("#dt_anticipos").dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		aoColumns : [ {
			sName : "anticipo",
			bSortable : false
		}, {
			sName : "monto",
			bSortable : false
		} ]
	});
}

/**
 * Funcion que crea la tabla de EP
 */
function creaTablaEP() {
	dtEP = $("#dt_total").dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		aoColumns : [ {
			sName : "nMes",
			bSortable : false
		}, {
			sName : "EP",
			bSortable : false
		}, {
			sName : "Total",
			bSortable : false
		} ]
		
		
	});
	dtEPModif = $("#dt_total_modif").dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		aoColumns : [ {
			sName : "nMes",
			bSortable : false
		}, {
			sName : "EP",
			bSortable : false
		}, {
			sName : "Total",
			bSortable : false
		} ]
	});
	dtEPPagoPasivo = $("#dt_pago_pasivo").dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Buscar:",
			oPaginate: {
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		aoColumns : [ {
			sName : "nMes",
			bSortable : false
		}, {
			sName : "EP",
			bSortable : false
		}, {
			sName : "Total",
			bSortable : false
		} ]
	});
	dtEPPlurianual = $("#dt_plurianual").dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Buscar:",
			oPaginate: {
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		aoColumns : [ {
			sName : "cCveContrato",
			bSortable : false
		}, {
			sName : "nAContrato",
			bSortable : false
		}, {
			sName : "mMontoContrato",
			bSortable : false
		} ]
	});
	var condition = " nFolioOPComHeader = '" + $("#nFolioOPComHeader").val()
			+ "'";
	var tableName = "COMPROMISO_O_PUB_DETAIL";

	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableName,
		Param : condition,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEP.fnAddData( [ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});

	var tableNameReten = 'COMPROMISO_O_PUB_RETENCIONES_DETAIL';
	var conditionReten = " a.cidcontrato = '" + $("#ccvecontrato").val() + "'" + " AND a.nFolioOPCOmHeader = " + $("#nFolioOPComHeader").val();
	
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameReten,
		Param : conditionReten,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			$('#dt_retencion').dataTable().fnAddData( [ data[i].Col0, data[i].Col1]);
		}
	});
		
	var conditionCM = " nFolioOPConvHeader = '" + $("#nFolioOPConvHeader").val() + "'";
	var tableNameCM = "COMPROMISO_O_PUB_CONV_MOFI_DETAIL";

	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameCM,
		Param : conditionCM,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEPModif.fnAddData( [ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});
	
	conditionCM = " nfoliooppagpasheader = '" + $("#nfoliooppagpasheader").val() + "'";
	tableNameCM = "COMPROMISO_O_PUB_PAGO_PASIVO_DETAIL";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameCM,
		Param : conditionCM,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEPPagoPasivo.fnAddData( [ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});
	
	conditionCM = " ccvecontrato = '" + $("#ccvecontrato").val() + "'";
	tableNameCM = "COMPROMISO_O_PUB_PLURIANUAL_DETAIL";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : tableNameCM,
		Param : conditionCM,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		for ( var i = 0; i < data.length; i++) {
			dtEPPlurianual.fnAddData( [ data[i].Col0, data[i].Col1, data[i].Col2 ]);
		}
	});
}

/**
 * Carga la informacion en la pantalla.
 */
function loadInfo() {
		
	queryFormPost("EjercicioFiscalActvRead", {
		async : false
	});
	queryFormPost( {
		queryName : "readPantAutHeader",
		async : false,
		callback : function() {
			var nPorceAnticipo = ( $("#nporceanticipo").val() == '' ? 0: parseFloat($("#nporceanticipo").val()) );
			if(nPorceAnticipo>30){
				$("#lblNoFolioAutAnticpoMayor").css("visibility","visible");
				$("#noFolioAutAnticpoMayor").css("visibility","visible");
			}
		}
	});

	querySelectPost("CatalogoTipoRetencionRead", "cIdTipoRetencion", {
		async : false
	});

	if (esConvenioModificatorio) {
		queryFormPost( {
					queryName : "folioAutorizacionConvenioModificatorioRead",
					async : false,
					callback : function() {
						queryFormPost( {
							queryName : "ConvModifAutRead",
							async : false,
							callback : function() {
								if ($("#fFinConv").val() == ''
										|| $("#fFinConv").val() == '01/01/1900') {
									$("#fFinConv").val("");
								}
							}
						});
					}
				});
	}
	if (esPagoPasivo) {
		queryFormPost( {
					queryName : "folioAutorizacioPagoPasivoRead",
					async : false,
					callback : function() {
					}
				});
	}
	queryFormPost("contratoOPEsPLU", {
	async : false
	});
	
	if($("#iEsPluriAnual").val()=='1')
		esContratoPlurianual=true;
		
	if (esContratoPlurianual) {		
		queryFormPost( {
					queryName : "montoOPPlurianualRead",
					async : false,
					callback : function() {
					}
				});
	}
		
	creaTablaEP();
	if($("#cEsRadicado").val()=='S'){
		document.getElementById("chk_radicado").checked = true;
	}else{
		document.getElementById("chk_radicado").checked = false;
	}
	document.getElementById("chk_radicado").disabled = true;
}

/**
 * Toma todos los inputs y los transforma en solo lectura.
 */
function disableInputs() {
	$('[type=text]').each(function() {
		$(this).attr("readonly", true);
	});
	$('textarea').attr('readonly', true);
}

/**
 * Crea los elementos en la pagina.
 */
function initWidgets() {
	
	//parent.document.getElementById("pb_cancel").style.display="none";
	creaTablaAnticipo();
	var oTable = $('#dt_retencion').dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true
	});

	if (esConvenioModificatorio) {
		$("#aTab4").show();
	}else if (esPagoPasivo) {
		$("#aTab6").show();
	} 
	else if (esPlurianual) {
		$("#aTab7").show();
	} 
	else {
		$("#aTab3").hide();
		$("#aTab4").hide();
	}
}

/**
 * Funcion que valida que solo se ingresen numeros en un campo de texto.
 * 
 * @param evt
 *            Evento.
 * @returns {Boolean} true si y solo si es numero o los caractes [.] y [-]
 */
function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 47) {
		return false;
	}
	return ((keyPressed > 47 && keyPressed < 58) || keyPressed == 46);
}

/**
 * Retira el formato monetario de una cadena;
 * 
 */
function retiraFormatoMoney(fld) {
	var valcol = fld;
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}

/**
 * Cambia a formato monetario.
 * 
 * @param fld
 *            campo.
 */
function estableceFormatoMoney(fld) {
	$("#" + fld.id).formatCurrency();
}
/*
 * **************************************************************************
 * Funciones de logica de negocios.
 * **************************************************************************
 */
function showDialog(acept) {
	if (terminar == true)
		return true;
	else {
		var msg = "";
		var continuar = false;
		var operacionExitosa = false;
		
		if (acept == 'S'){
			/*if( !facturaCapturada() && !esSAIAlterno ){
				alert("Para continuar debe cargar la factura por el monto total de compromiso.");
				return false;
			}
			*/
			msg = "El compromiso se autorizar\u00E1. Esta operacion no se puede regresar.\n \u00BFEsta seguro que desea continuar?";
		}else
			msg = "El compromiso no se autorizar\u00E1. El tramite regresara a modificaci\u00F3n. \u00BFEsta seguro que desea continuar?";

		continuar = confirm(msg);

		if (continuar && acept == 'S')
			operacionExitosa = onAcept();
		else if (continuar && acept == 'N')
			operacionExitosa = onCancel();

		return continuar && operacionExitosa;
	}
}

/**
 * Funcion que se ejecuta al momento de aceptar el pre-compromiso. Genera el
 * llamado al motor contable.
 */
function onAcept() {
	divAplica.innerHTML = "Procesando, por favor espere.";
	myModalApcon.show();
	setTimeout('fnAplicaMotor()', 3000);
}

/**
 * Funcion que se ejecuta al momento de rechazar el pre-compromiso. Se regresa
 * el tramite a edicion.
 */
function onCancel() {
	$("#iStatus").val(6);
	var ok = false;
	try {
		queryFormPost( {
			queryName : "actualizaStatusPreCompromiso",
			async : false,
			callback : function() {
/*				if (esPagoPasivo) {
					var strAction = "../AplicaContableObraPublica?FolioSAI=" + $("#FolioSAI").val()
							+ "&nFolioOPConvHeader=" + $("#nFolioOPConvHeader").val() 
							+ "&accion=CANCEL_PRECOM_PASIVO"
							+ "&cEjercicio="+$("#cEjercicio").val()
							+ "&cCentroContable="+$("#cCentroContable").val()
							+ "&ccvecontrato="+$("#ccvecontrato").val()
							+ "&cNoConvenio="+$("#cNoConvenio").val()
							+ "&fInicioConv="+$("#fInicioConv").val()
							+ "&fFinConv="+$("#fFinConv").val()
							+ "&mTotalConv="+ retiraFormatoMoney( $("#mTotalConv").val() );
					divAplica.innerHTML = "<iframe id='ifAplica' src='"
							+ strAction
							+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
				} 
			
				if (esPlurianual) {
					var strAction = "../AplicaContableObraPublica?FolioSAI=" + $("#FolioSAI").val()
							+ "&nFolioOPConvHeader=" + $("#nFolioOPConvHeader").val() 
							+ "&accion=CANCEL_PRECOM_PLURIANUAL"
							+ "&cEjercicio="+$("#cEjercicio").val()
							+ "&cCentroContable="+$("#cCentroContable").val()
							+ "&ccvecontrato="+$("#ccvecontrato").val()
							+ "&cNoConvenio="+$("#cNoConvenio").val()
							+ "&fInicioConv="+$("#fInicioConv").val()
							+ "&fFinConv="+$("#fFinConv").val()
							+ "&mTotalConv="+ retiraFormatoMoney( $("#mTotalConv").val() );
					divAplica.innerHTML = "<iframe id='ifAplica' src='"
							+ strAction
							+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
				} 
*/		
				ok = true;
				
			}
		});
	} catch (err) {
	}
	return ok;
}
//SASV QUITAR FORMATO...
function quitaFmt( val ) {
   	val = val.replace("$", "");
   	val = val.replace(/,/g, "");

   	if ( val.indexOf( "(" ) >= 0 ) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
   	}
   	return val;
}		

/**
 * Envia el llamado al motor contable.
 */
function fnAplicaMotor() {
	// Actualiza el estatus a 3 para indicar que se ha terminado.
	queryFormPost( {
		queryName : "updateStatusObraPublicaHeaderCompromiso",
		async : false,
		callback : function() {
		}
	});

	// Aplica el motor contable.
	if (esConvenioModificatorio) {
		var strAction = "../AplicaContableObraPublica?FolioSAI=" + $("#FolioSAI").val()
				+ "&nFolioOPConvHeader=" + $("#nFolioOPConvHeader").val() 
				+ "&accion=APPLY_CONV_MOD"
				+ "&cEjercicio="+$("#cEjercicio").val()
				+ "&cCentroContable="+$("#cCentroContable").val()
				+ "&ccvecontrato="+$("#ccvecontrato").val()
				+ "&cNoConvenio="+$("#cNoConvenio").val()
				+ "&fInicioConv="+$("#fInicioConv").val()
				+ "&fFinConv="+$("#fFinConv").val()
				+ "&mTotalConv="+ retiraFormatoMoney( $("#mTotalConv").val() );
		divAplica.innerHTML = "<iframe id='ifAplica' src='"
				+ strAction
				+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
		
		//SASV CM 25/05/2015
		var datos =dtEPModif.dataTable().fnGetData();
		var suma=0;
		//var datosOriginal=$("#dt_clavepresup2").fnGetData();
		

		//$("#nIdClaveEgresos").val(datosOriginal[0][1]);
		$("#cEjercicioTbl").val($("#cEjercicio").val());
		$("#cIdContratoTbl").val($("#ccvecontrato").val());
		$("#cTContratoTbl").val("OB");
		$("#cIdEntidadContableTbl").val($("#cCentroContable").val());

		
		for ( var i = 0; i < datos.length; i++) {
			if($("#nIdClaveEgresos").val()!=datos[i][1]){
				$("#nIdClaveEgresos").val(datos[i][1]);
				$("#cIdEntidadContable").val(0)
				queryFormPost("existeRegistroContratoEP", {async: false });
				if($("#cIdEntidadContable").val()=="0")
				queryFormPost("ContratoEPCreate", {async: false });
			}	
			suma+=parseFloat(quitaFmt(datos[i][2]));
		}
		
		$("#mtotal2").val(suma);
		queryFormPost("PonerConsultaCM", {async: false });//SASV Convenio Modificatorio OBRA para poder hacer mas de 1 convenio despues de autorizado, regresar a consulta
		
	} else if (esPagoPasivo) {
		var strAction = "../AplicaContableObraPublica?FolioSAI=" + $("#FolioSAI").val()
		+ "&nfoliooppagpasheader=" + $("#nfoliooppagpasheader").val() 
		+ "&accion=APPLY_PAGOPASIVO_PRECOMP"
		+ "&cEjercicio="+$("#cEjercicio").val()
		+ "&cCentroContable="+$("#cCentroContable").val()
		+ "&ccvecontrato="+$("#ccvecontrato").val()
		+ "&cNoConvenio="+$("#cNoConvenio").val()
		+ "&fInicioConv="+$("#fInicioConv").val()
		+ "&fFinConv="+$("#fFinConv").val()
		+ "&mTotalConv="+ retiraFormatoMoney( $("#mTotalConv").val() );
divAplica.innerHTML = "<iframe id='ifAplica' src='"
		+ strAction
		+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
	} else if (esPlurianual) {
		//alert($("#nfolioopplurianualheader").val());
		var strAction = "../AplicaContableObraPublica?FolioSAI=" + $("#FolioSAI").val()
		+ "&nfolioopplurianualheader=" + $("#nfolioopplurianualheader").val() 
		+ "&accion=APPLY_PLURIANUAL_PRECOMP"
		+ "&cEjercicio="+$("#cEjercicio").val()
		+ "&cCentroContable="+$("#cCentroContable").val()
		+ "&ccvecontrato="+$("#ccvecontrato").val()
		+ "&cNoConvenio="+$("#cNoConvenio").val()
		+ "&fInicioConv="+$("#fInicioConv").val()
		+ "&fFinConv="+$("#fFinConv").val()
		+ "&mTotalConv="+ retiraFormatoMoney( $("#mTotalConv").val());
	divAplica.innerHTML = "<iframe id='ifAplica' src='"
		+ strAction
		+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
	}else {
		var strAction = "../AplicaContableObraPublica?FolioSAI="
				+ $("#FolioSAI").val() + "&accion=APPLY_CONT&aEjercicioFiscal="+$("#cEjercicio").val();
		divAplica.innerHTML = "<iframe id='ifAplica' src='"
				+ strAction
				+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
	}
}

function llenaInformacionAnticipos(){
	var porcIVA = parseFloat( $("#nporceiva").val() );
	var porcAnticipo = parseFloat( $("#nporceanticipo").val() );
	
	var importeObra = parseFloat( retiraFormatoMoney( $("#nmonto").val() ) );
	if(esContratoPlurianual)
		importeObra = parseFloat( retiraFormatoMoney( $("#mMontoPlurianual").val() ) );

	
	var montoAnticipoNeto = parseFloat( importeObra * porcAnticipo /100 );
	var montoAnticipoIVA = 	parseFloat( montoAnticipoNeto * porcIVA / 100 );
	var totalAnticipo = montoAnticipoNeto + montoAnticipoIVA;
	
	$("#porcAnticipoShow").val(porcAnticipo + '%');
	$("#impBrutoShow").val(montoAnticipoNeto.toFixed(2)); 
	$("#ivaAnticipoShow").val(montoAnticipoIVA.toFixed(2));
	$("#totalAnticipoShow").val(totalAnticipo.toFixed(2));
	
	estableceFormatoMoney($("#impBrutoShow")[0]);
	estableceFormatoMoney($("#ivaAnticipoShow")[0]);
	estableceFormatoMoney($("#totalAnticipoShow")[0]);
	
	
}
/*
 * **************************************************************************
 * Funciones CALLBACK del Flujo de Datos.
 * **************************************************************************
 */

/**
 * Funcion llamada al finalizar la carga del XML del caso. (NO UTILIZADA EN ESTE
 * FLUJO)
 * 
 * @param idOper
 *            Operacion a ejecutar (ID)
 * @returns {Boolean}
 */
function onLoadPlantilla(idOper) {
	init();
	return true;
}

/**
 * Funcion llamada al terminar de guardar el formulario.
 * 
 * @param idOper
 * @returns {Boolean}
 */
function onPostDisplay(idOper) {
	return true;
}

/**
 * Funcion llamada previo el envio a avanzar el caso.
 * 
 * @param idOper
 * @returns {Boolean}
 */
function onPostSubmit(idOper) {
	var autorizado = $("input[name='operacionFinal']:radio:checked").val();
	return showDialog(autorizado);
}

/**
 * Funcion llamada al guardar el formulario.
 * 
 * @param idOper
 *            ID de la operacion a ejecutar.
 * @returns {Boolean}
 */
function onSubmit(idOper) {
	if (terminar == true)
		return true;
	else {
		
		alert("El tramite se guardo correctamente.");
		parent.document.getElementById("pb_send").disabled = false;
		return true;
	}
}

/*
 * **************************************************************************
 * Funciones del Flujo de Datos.
 * **************************************************************************
 */
/**
 * La operacion siguiente se calcula en base a la desicion del usuario. Si
 * acepta el precompromiso se envia a la aplicacion contable, en caso ontrario
 * se regresa a modificacion.
 */
function OperacionSiguiente(id_oper) {
	var autorizado = $("input[name='operacionFinal']:radio:checked").val();
	if (esConvenioModificatorio) {
		if ('S' == autorizado)
			return 'consulta_obra';
		else
			return 'conv_modificatorio';
	} else 	if (esPagoPasivo) {
		if ('S' == autorizado)
			return 'consulta_obra';
		else
			return 'pago_pasivo';
	}  else if (esPlurianual) {
		if ('S' == autorizado)
			return 'consulta_obra';
		else
			return 'OBRAPUBLICA_PLURI';
	} else {
		if ('S' == autorizado)
			return 'consulta_obra';
		else
			return 'captura_obra';
	}
}
/**
 * El responsable siguiente se calcula en base a la desicion del usuario. Si
 * acepta el precompromiso se envia a la aplicacion contable, en caso ontrario
 * se regresa a modificacion.
 */
function ResponsableSiguiente(id_oper) {
	var autorizado = $("input[name='operacionFinal']:radio:checked").val();
	var resSig;

	if ('S' == autorizado)
		resSig = 'CONSULTA_OBRA';
	else
		resSig = 'CAPTURA_OBRAPUBLICA';

	return resSig;
}

/**
 * Funcion callback que se llama al terminar la aplicacion contable.
 */
function terminaAppCont(success) {
	myModalApcon.hide();
	terminar = success;
	if( success ){
		if (esConvenioModificatorio)
		queryFormPost("ActualizaMonto", {async: false });//SASV 17/06/2015 Se Actualiza la tabla con el monto total del convenio.
		parent.document.getElementById("pb_send").click();
	}
}

function generaInformacionContrato() {
	var exito = false;

	$.ajax( {
		url : '../GeneraInformacionContrato',
		dataType : 'json',
		data : {
			"accion" : "CREA_CASO",
			"FolioSAI" : $("#FolioSAI").val(),
			"aEjercicioFiscal" : $("#cEjercicio").val()
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
}

function facturaCapturada(){
	var exito = false;

	$.ajax( {
		url : '../GeneraInformacionContrato',
		dataType : 'json',
		data : {
			"accion" : "CONTRATO_CON_FACTURA" 
		},
		async : false,
		success : function(RS) {
			if (RS.success == "true") {
				var capturados = parseInt( RS.data_1.result, 10);
				if( capturados > 0 )
					exito = true;
			}else{
				alert("No se puede continuar debido al siguiente error: " + RS.data_1.result );
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			exito = false;
		}
	});
	
	return exito;
}

function creaDTFacturas(){
	oTablevFact = $('#grdValidaFacturas').dataTable(
	{
		"bProcessing": true,
		"bServerSide": true,
		"bDestroy": true,
		"bSort": true, 
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoFactura&qw=cTipoContrato='" + $("#TipoContrato").val() + "' AND cIDContrato='"+$("#ccvecontrato").val()+"'" ,
		"bJQueryUI": true,
		"sScrollX": "100%",
		"bPaginate": false,
		"bAutoWidth": true,
		"bInfo": true,
		aoColumns: [
					{ sName: "cFactura" },
					{ sName: "importeBruto" },
					{ sName: "importeImpuestos" },
					{ sName: "importeTotal" } ]	
	});
}

function creaDiagloFacturas(abrir){
	myModalValidaFact = new bootstrap.Modal(document.getElementById('dialog-validaFact'), {
	  keyboard: false
	});
	if( abrir ){
		creaDTFacturas();
		togleDivFacts(0);
		myModalValidaFact.show();
	}
}

function togleDivFacts(nIdDiv) {
	
	if (nIdDiv == 0) {
		$("#uploadFacturasDiv").hide();
		$("#facturasCapturadasDiv").show();
		creaDTFacturas();
		leeMontosFacturas();
	} else {
		$('#uploadFacturasFrm').attr('src',"UploadCFDIContrato.jsp?TipoContrato="+$("#TipoContrato").val()+"&IDContrato="+$("#ccvecontrato").val()+"&RFC="+$("#crfc").val());
		$("#uploadFacturasDiv").show();
		$("#facturasCapturadasDiv").hide();
	}
}
function leeMontosFacturas() {
	queryFormPost({
		queryName : "readMontoFacturasContrato",
		async : false,
		callback : function() {
			if ( $("#mTotalFacturaV").val() == "" ){
				$("#mTotalFacturaV").val("0.00");
			}
		}
	});
}
