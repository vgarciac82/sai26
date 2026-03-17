
var modificacionEstimacion = false;

/**
 * Almacen temporal del valor del monto de obra //IRD 20131121 RO-0009 todo lo
 * de pago de pasivo
 */
var cacheMontoObra = 0;
var cacheMovtoObra = 0;
var showAlertSaveComp = true;
// Coment
var nombreMeses = new Array("enero", "febrero", "marzo", "abril", "mayo",
	"junio", "julio", "agosto", "septiembre", "octubre", "noviembre",
	"diciembre");
var validaFechasLP = new Array();
var validaFechasOtro = new Array();

var modificando = false;
var filaModificada = -1;
var primerAjuste = 0;
/**
 * Funcion llamada al recibir el foco uno de los campos de catura de monto.
 */
function onFocusMontoMoney(idInpt) {
	var val = Sinfrmt($("#" + idInpt)[ 0 ]);
	if( val < 0 )
		$("#" + idInpt).val('');else {
		cacheMovtoObra = $("#" + idInpt).val();
		$("#" + idInpt).val('');
	}

	$("#" + idInpt).removeClass("montoCaptura");
	$("#" + idInpt).addClass("montoCapturaEdit");
}
/**
 * Funcion llamada cuando un input monetario gana el foco.
 */
function onFocusMoney() {
	var val = Sinfrmt($("#mObra")[ 0 ]);
	if( val < 0 ) {
		$("#mObra").val('');
	} else {
		cacheMontoObra = $("#mObra").val();
		$("#mObra").val('');
	}

	$("#mObra").removeClass("normal");
	$("#mObra").addClass("inEdit");

}

function onFocusMoneyContrato() {
	var val = Sinfrmt($("#nMontoContratoOP")[ 0 ]);
	if( val < 0 ) {
		$("#nMontoContratoOP").val('');
	} else {
		cacheMontoObra = $("#nMontoContratoOP").val();
		$("#nMontoContratoOP").val('');
	}

	$("#nMontoContratoOP").removeClass("normal");
	$("#nMontoContratoOP").addClass("inEdit");

}
/**
 * Funcion llamada cuando un input monetario pierde el foco.
 */
function onBlurMontoMoney(idInpt, classNormal) {
	if( $("#" + idInpt).val() != '' ) {
		cambiafrmt($("#" + idInpt)[ 0 ]);
	} else {
		$("#" + idInpt).val(cacheMovtoObra);
		Sinfrmt($("#" + idInpt)[ 0 ]);
		cambiafrmt($("#" + idInpt)[ 0 ]);
	}

	$("#" + idInpt).removeClass("montoCapturaEdit");
	$("#" + idInpt).addClass(classNormal ? classNormal : "montoCaptura");
}
/**
 * Funcion llamada cuando un input monetario pierde el foco.
 */
function onBlurMoney() {
	if( $("#mObra").val() != '' ) {
		$("#mImporteCapEPs").val($("#mTotal").val());
		cambiafrmt($("#mObra")[ 0 ]);
	} else {
		$("#mImporteCapEPs").val(cacheMontoObra);

		$("#mObra").val(cacheMontoObra);
		Sinfrmt($("#mObra")[ 0 ]);
		cambiafrmt($("#mObra")[ 0 ]);
	}

	$("#mObra").removeClass("inEdit");
	$("#mObra").addClass("normal");
}
function onBlurMoneyContrato() {
	if( $("#mObra").val() != '' ) {
		$("#mImporteCapEPs").val($("#mTotal").val());
		cambiafrmt($("#mObra")[ 0 ]);
	} else {
		$("#mImporteCapEPs").val(cacheMontoObra);

		$("#mObra").val(cacheMontoObra);
		Sinfrmt($("#mObra")[ 0 ]);
		cambiafrmt($("#mObra")[ 0 ]);
	}

	$("#mObra").removeClass("inEdit");
	$("#mObra").addClass("normal");
}

/**
 * Retira el formato monetario
 * 
 * @param fld
 *            campo.
 */
function Sinfrmt(fld) {
	var valcol = fld.value;
	valcol = valcol.replace(/$/g, "");
	valcol = valcol.replace(/,/g, "");
	$("#" + fld.id).val(valcol);
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

/**
 * Cambia a formato monetario.
 * 
 * @param fld
 *            campo.
 */
function cambiafrmt(fld) {
	$("#" + fld.id).formatCurrency();
}

/**
 * Inicializa los campos de la pantalla.
 */
function init() {
	myModalQuest = new bootstrap.Modal(document.getElementById('dlgQuestionnaire'), {keyboard: false});
	myModalPluObr = new bootstrap.Modal(document.getElementById('DialogPluObr'), {keyboard: false});
	
	validaFechasLP[ "fAclaracion" ] = "fConvocatoria";
	validaFechasLP[ "fRecepProp" ] = "fAclaracion";
	validaFechasLP[ "fRecepFallo" ] = "fRecepProp";
	validaFechasLP[ "fAdjudicacion" ] = "fAdjudicacion";

	validaFechasOtro[ "fRecepProp" ] = "fConvocatoria";
	validaFechasOtro[ "fRecepFallo" ] = "fRecepProp";
	validaFechasOtro[ "fAdjudicacion" ] = "fAdjudicacion";
	
	// RO-0011 Esquema de precios
	ocultaBotonesGestion();
	querySelectPost("CatalogoEsquemaPrecioRead", "esquemaPrecios", {
		async : false
	});
	querySelectPost("readCatalogoUN_OP", "cU_UE", {
		async : false
	});
	
	$("#cOLI_2").hide();
	$("#cCarteraProyec_2").hide();
	$("#fFinConv").change(function() {
		validaFechaFinModificado();
	});



	$("#mIncremento").focus(
		function() {
			onFocusMontoMoney("mIncremento");
		});

	$("#mIncremento").blur(function() {
		onBlurMontoMoney("mIncremento", "normal");
		validaMontoModificado();
	});

	$("#mPagoPasivo").focus(
		function() {
			onFocusMontoMoney("mIncremento");
		});
	$("#mPagoPasivo").change(
		function() {
			var montoTotalModif = $("#mPagoPasivo").val() == '' ? 0 : parseFloat(quitaFrmt($("#mPagoPasivo").val()));
			$("#mTotalPagoPasivo").val(Math.round(( montoTotalModif + ( montoTotalModif * parseFloat($("#ivaPagPasF").val()) / 100 ) ) * 100) / 100);
		});
	$("#mPagoPasivo").blur(function() {
		onBlurMontoMoney("mIncremento", "normal");
		validaMontoModificado();
	});
	// RO_0010 MLR
	$("#mPlurianual").focus(
		function() {
			onFocusMontoMoney("mIncremento");
		});
	$("#mPlurianual").change(
		function() {
			var montoTotalModif = $("#mPlurianual").val() == '' ? 0 : parseFloat(quitaFrmt($("#mPlurianual").val()));
			$("#mImporteIVAPlurianual").val(Math.round(montoTotalModif * parseFloat($("#ivaPlurianual").val()) * 100) / 100);
			$("#mTotalPlurianual").val(parseFloat(montoTotalModif) + parseFloat($("#mImporteIVAPlurianual").val()));
			$("#mTotalPlurianual").val(Math.round(parseFloat(quitaFrmt($("#mTotalPlurianual").val())) * 100) / 100);

		});
	$("#ivaPlurianual").change(
		function() {
			$("#mPlurianual").change();
		});
	$("#mPlurianual").blur(function() {
		onBlurMontoMoney("mIncremento", "normal");
		validaMontoModificado();
	});

	$("#cDescripcionContrato").attr("title", "Descripción Contrato");
	$("#fRecepcion").val(hoy);
	$("#fHoy").val(hoy);
	$('#Link04').hide();
	$("#btnActualizar").attr("disabled", "disabled");

	oTableDisp = $("#tblTipoRFCDisp").dataTable({
		bPaginate : false,
		bLengthChange : false,
		bInfo : false,
		bAutoWidth : false,
		sScrollY : "114",
		sScrollX : "100%",
		bJQueryUI : true,
		bFilter : false,
		bSort : false,
		bInfo : false,
		bAutoWidth : false,
		// bSearch : false,
		// sScrollXInner: "100%",
		oLanguage : {
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
		},
		aaSorting : [ [ 1, "asc" ] ],
		aoColumns : [ {
			sName : "cIdTipoRFCDisp",
			bSearchable : false,
			bSortable : false,
			bVisible : false
		}, {
			sName : "cTipoRFCDisp",
			bSortable : false
		} ]
	});

	oTable = $("#tblTipoRFC").dataTable({
		bPaginate : false,
		bLengthChange : false,
		bInfo : false,
		bAutoWidth : false,
		sScrollY : "114",
		sScrollX : "100%",
		bJQueryUI : true,
		bFilter : false,
		bSort : false,
		bInfo : false,
		bAutoWidth : false,
		oLanguage : {
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
		},
		aaSorting : [ [ 1, "asc" ] ],
		aoColumns : [ {
			sName : "cIdTipoRFC",
			bSearchable : false,
			bSortable : false,
			bVisible : false
		}, {
			sName : "cTipoRFC",
			bSortable : false
		} ]
	});

	var tablaApartado = $('#dt_clavepresup').dataTable({
		"sScrollX" : "500px",
		"bScrollCollapse" : true,
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true
	});

	$('#dt_clavepresup2').dataTable({
		"sScrollX" : "500px",
		"bScrollCollapse" : true,
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true
	});

	$('#dt_claveConvenioModificatorio').dataTable({
		"sScrollX" : "500px",
		"bScrollCollapse" : true,
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true
	});

	$('#dt_clavePasivo').dataTable({
		"sScrollX" : "500px",
		"bScrollCollapse" : true,
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true
	});

	$('#dt_clavePlurianual').dataTable({
		"sScrollX" : "500px",
		"bScrollCollapse" : true,
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true
	});
	$("#nPorcIVAAplicable")
		.change(
			function() {
				onchangenPorcIVAAplicable();
			});

	$("#mImporteContrato").formatCurrency();


	$("#btnAmortiza").button().click(function() {
		$("#divModals").show();
		myModalDocAmortizacion.show();
		if( $("#nFolioOPComHeader").val() == '-1' || $("#nFolioOPComHeader").val() == "" ) {
			queryFormPost("readnFolioOPComHeader", {
				async : false
			});
		}
		validaAmortiazacionNecesaria();
		if( $("#cCveContrato").val() == '' ) {
			alert("Antes de capturar la informaci\u00F3n debe capturar el No. de Contrato.");
			return false;
		}
		if( esConvenioModificatorio )
			$("#btnAcepDocAmortizacion").hide();
		if( esPagoPasivo )
			$("#btnAcepDocAmortizacion").hide();
		if( esPlurianual )
			$("#btnAcepDocAmortizacion").hide();
		
		
		querySelectPost("CatalogoTipoRetencionRead", "cIdTipoRetencion", {
			async : false
		});
		llenaInformacionAnticipos();
		llenaRetenciones(true);
		setTimeout("borraCancelar()", 100);
	
	});//Fin del boton btnAmortiza

	$("#btnGuarda").button();
	$("#btnAplica").button();
	$("#btnCancela").button();
	$("#btnSuspencion").button();



	// Inicializaciones CRUD
	queryFormPost("readMaxMesesAnterioresOP", {
		async : false
	});
	// Meses a partir de los cuales se puede realizar el apartado. El valor por
	// defecto es 0, es decir puede apartar del mes actual al siguiente
	queryFormPost("readAPartadoMesSuperiorOP", {
		async : false
	});

	/*
	 * queryFormPost("fEntregaVentanillaRead", { async : false });
	 */
	queryFormPost("EjercicioFiscalActvRead", {
		async : false
	});
	querySelectPost("PorcIVARead", "nPorcIVAAplicable", {
		async : false,
		callback : function() {
			$("#nPorcIVAAplicable").val("0.16");
		}
	}); // genera montosIVA
	querySelectPost("PorcIVARead", "ivaPlurianual", {
		async : false
	}); // genera montosIVA Plurianual

	querySelectPost("catalogoTipoPersonaRFCRead", "cIdTipoPersonaRFC", {
		async : false
	});
	querySelectPost("tCatalogoTipoObraRead", "cIdTObra", {
		async : false
	});
	querySelectPost("tCatalogoTipoRecursosRead", "cIdTipRec", {
		async : false
	});

	querySelectPost("tCatalogoTipoArticuloRead", "cIdTipoAdjudica", {
		async : false
	});

	querySelectPost("CatalogoTipoContratoObraRead", "cIdTipoContratoObra", {
		async : false
	});
	querySelectPost("CatalogoClaseContratoObraRead", "cIdClaseContratoObra", {
		async : false
	});
	querySelectPost("UnidadresponsableRead", "cIdUnidadAdministrativa", {
		async : false
	});
	querySelectPost("tCatalogoAdicionalesRead", "cIdAdicionales", {
		async : false
	});

	readCarterasProimpro();
	$("#cCarteraProyec").change(function() {
		$("#cCarteraProyec_2").val($("#cCarteraProyec").val());
		clearSelect([ "cOLI", "epSel" ]);
		if( esPagoPasivo || ( $("#cCarteraProyec").val() == -1 && parseInt($("#PartidasSinOLI").val(), 10) > 0 ) ) // IRD
			// 20140117
			// los
			// pagos
			// de
			// pasivo
			// no
			// se
			// pagan
			// por
			// oli
			// sino
			// directo
			// al
			// disponible
			readClavesPresupuestales();
		else
			readOLIs();
	});

	/*
	 * Muestra las Claves Presupuestales Relacionadas con los OLIS
	 */
	// queryFormPost("fRecepcionRead", {async : false });

	$("#cOLI").change(function() {
		$("#cOLI_2").val($("#cOLI").val());
		clearSelect([ "epSel" ]);
		readClavesPresupuestales();
	// $("#epSel").focus();
	});


	$("#imgPlayStop").css("visibility", "hidden");

	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();

	$("#mObra").change(function() {
		onchangemObra();
	});

	$("#cIdTObra").focus();


	/*
	 * Crea el area de captura de EP.
	 */

	deshabilitaMontos();
	iniciaCapturaMontos();

	/*
	 * Crea el div para la aplicacion contable
	 */
	
	$("#nPorcAnticipo").blur(function() {
		var nPorcAnticipo = ( $("#nPorcAnticipo").val() == '' ? 0 : parseFloat($("#nPorcAnticipo").val()) );
		if( nPorcAnticipo > 100 ) {
			$("#nPorcAnticipo").val("0");
			$("#mAnticipo").val("0");
			alert("El Porcentaje de Anticipo no puede ser mayor al 100%");
		} else if( nPorcAnticipo > 30 ) {
			$("#myModalDocAutorizacion").hide();
			$("#divModals").hide();
		} else {
			ocultaFolAutorizacionPA();
			calculaPorcAnticipo();
		}
	});

	$("#mAnticipo").blur(function() {
		calculaPorcAnticipoMonto();
		var nPorcAnticipo = ( $("#nPorcAnticipo").val() == '' ? 0 : parseFloat($("#nPorcAnticipo").val()) );
		if( nPorcAnticipo > 100 ) {
			$("#nPorcAnticipo").val("0");
			$("#mAnticipo").val("0");
			alert("El Porcentaje de Anticipo no puede ser mayor al 100%");
		} else if( nPorcAnticipo > 30 ) { // poner el 30% del contrato
			$("#divModals").show();			
			$("#myModalDocAutorizacion").show();
		} else {
			ocultaFolAutorizacionPA();
		// calculaPorcAnticipo();
		}
	});

	setBlurMonthCapture();
	ocultaColumnas();
	if( !esConvenioModificatorio ) {
		$('#Link05').hide();
	} else {
		queryFormPost("OPConvModifIVARead", {
			async : false
		});
	}

	if( !esCapturaEstimacion )
		$('#Link06').hide();
	else
		$('#Link06').show();
	if( !esPagoPasivo ) {
		// if(!esCapturaEstimacion)
		$('#Link07').hide();
	}
	else
		$('#Link07').show();
	if( !esPlurianual ) {
		// if(!esCapturaEstimacion)
		$('#Link08').hide();
	}
	else
		$('#Link08').show();
	/*
	 * Crea el área de captura para Contrato Plurianual
	 */

	/*
		 * Crea data_table para agregar Retenciones
		 */
	var oTable = $('#dt_retencion').dataTable({
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bJQueryUI" : true
	});

	/*
	 * Agrega las retenciones al datatable dt_retencion.
	 */
	$("#addBtn").button().click(
		function() {
			var optSel = $("#cIdTipoRetencion").val();
			var optTxt = $("#cIdTipoRetencion option[value='" + optSel + "']").text();
			var existe = validaAgregarRetencion(optSel);
			if( existe )
				alert("El tipo de retencion " + optTxt + " ha sido previamente agregado");else {
				var arr = new Array(optSel, optTxt);
				$("#dt_retencion").dataTable().fnAddData(arr);

				if( optSel == 2 && $("#IMDT").attr("checked") ) {
					$("#opc2PC").val($("#IMDT").val());
					queryFormPost("update2PC", {
						async : false
					});
				} else if( optSel == 2 && $("#CNIC").attr("checked") ) {
					$("#opc2PC").val($("#CNIC").val());
					queryFormPost("update2PC", {
						async : false
					});
				}
			}
		});
	/*
	 * Elimina las retenciones al datatable dt_retencion.
	 */
	$("#dt_retencion tbody").dblclick(function(event) {
		if( confirm("Desea eliminar la retencion") ) {
			var aPos = oTable.fnGetPosition(event.target.parentNode);
			var aData = oTable.fnGetData(aPos);
			oTable.fnDeleteRow(aPos);
			queryFormPost("updateElimina2PC", {
				async : false
			});
		}
	});

	/*
	 * Modifica las Estimaciones en el data_table
	 */
	$("#dt_solicitudPago tbody").dblclick(function(event) {
		if( esCapturaEstimacion ) {
			var aPost = oTableEst.fnGetPosition(event.target.parentNode);
			var aData = oTableEst.fnGetData(aPost);
			$("#noEstimacion").val(aData[ 0 ]);
			$("#noEstimacion2").val(aData[ 0 ]);
			//SASV 13/05/2015 Validacion si la Estimacion pertenece algun Pago.
			queryFormPost("estatusEstimacion", {
				async : false
			});
			if( $("#estatus").val() == "1" ) {
				alert("La Estimacion ya pertenece a un Pago");
				cancelaModifEstim();
				return false;
			} else {
				if( confirm("Desea Modificar la Estimacion") ) {
					modificacionEstimacion = true;
					// $("#contenedorNoEstimacion").html("");
					// $("#contenedorNoEstimacion").html("<input type='text'
					// id='noEstimacion' name='noEstimacion' size='14'
					// readonly='readonly'/>");
					if( $("#noEstimacion").val() == "Anticipo" ) {
						document.getElementById("TipoCaptura").selectedIndex = "1";
					} else
						document.getElementById("TipoCaptura").selectedIndex = "2";
					document.getElementById("TipoCaptura").disabled = true;
					tipoCaptura();
					$("#noEstimacion").hide();
					$("#noEstimacion2").show();
					$("#mMontoEstimacion").val(quitaFmt(aData[ 2 ].toString()).replace(" ", ""));
					$("#nPorceAvanceFisicoEstimado").val(aData[ 3 ]);
					$("#nPorceAvanceFisicoEjecutado").val(aData[ 4 ]);
					$("#nPorceAvanceFisicoProgramado").val(aData[ 5 ]);
					// MLR R0-0007
					$("#mmontoFisicoEjecutado").val(quitaFmt(aData[ 6 ].toString()).replace(" ", ""));
					$("#mmontoFisicoProgramado").val(quitaFmt(aData[ 7 ].toString()).replace(" ", ""));
					$("#fEntregaVentanilla").val(aData[ 8 ]);
					$("#mesEstimado").val(aData[ 9 ]);
					$("#fperiodoEstimacionIni").val(aData[ 10 ]);
					$("#fperiodoEstimacionFin").val(aData[ 11 ]);
					// Guarda el noEstimacion, para actualizar el registro
					$("#noEstimacionDtTable").val($("#noEstimacion2").val());
					$("#btnAddEstimacion").hide();
					$("#btnModEstimacion").show();
					$("#btnCancelModificacion").show();
					if( $("#noEstimacion2").val() != "Anticipo" ) {						
						calculaMontosEstimacion(false);											
						queryFormPost("ExtraeAcumuladoEstimacion", {
							async : false
						});
						var acumulado = parseFloat((quitaFrmt($("#acumuladoEstimado").val()))).toFixed(2);
						var estimacionActual = parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2);
						$("#acumuladoEstimado").val(acumulado - estimacionActual);
					}
				} else {
					cancelaModifEstim();
				}
			}
		}
	});

	setCurrYear();
	$('#totalMultiAnual').bind('keyup', function() {
		generaEntradaAnual();
	});
	$("#cPluriaAnual").click(function() {
		if( document.getElementById("cPluriaAnual").checked) {
			if( $("#cCveContrato").val() == '' ) {
				alert("Antes de capturar la informaci\u00F3n debe capturar el No. de Contrato.");
				return false;
			}
			$("#divModals").show();
			myModalMA.show();
		} else {
			eleminaInfoPlurianual();
		}
	});
	// Asigna el valor al input periodo que se encuentra en el tab Solicitud
	// Pago
	$("#eFiscalPago").val($("#cEjercicio").val());

	// Crea data_Table para gregar las Estimaciones
	var oTableEst = $('#dt_solicitudPago').dataTable({
		"bScrollCollapse" : false,
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : true,
		"bJQueryUI" : true
	});

	$("#btnAddEstimacion").button().click(function() {
		porcEstimacion();
	});

	$("#btnModEstimacion").hide();
	$("#btnModEstimacion").button().click(function() {
		$("#btnCancelModificacion").hide();
		actEstimacion();
	});

	$("#btnCancelModificacion").hide();
	$("#btnCancelModificacion").button().click(function() {
		cancelaModifEstim();
	}
	// $("#contenedorNoEstimacion").html("");
	// $("#contenedorNoEstimacion").html("<input type='text'
	// id='noEstimacion' name='noEstimacion' size='14' maxlength='40'
	// onkeypress='Validaciones(this,17)' class='estimacion'
	// readonly='readonly'/>");
	);
	$('#Link06').hide();

	queryFormPost("readUnidadPermiteNoOLIS", {
		async : false
	});

	llenaFundamentoLegal();

	// DataTable con las Solicitudes
	tablaPluObra = $('#tblPluObra').dataTable({
		
		"bPaginate": true,
        "bJQueryUI": true, 
        "bLengthChange": false,
        "bFilter": false,
        "bSort": false,
        "bInfo": true,
        "bAutoWidth": true,
        "bProcessing": true,
        "iDisplayLength": 10,
		"sScrollX": "100%",
		oLanguage : {
			sProcessing : "Procesando...",
			sLengthMenu : "Mostrar _MENU_ registros",
			sZeroRecords : "No hay registros a mostrar",
			sEmptyTable : "No hay datos en la tabla",
			sLoadingRecords : "Cargando...",
			sInfo : "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty : "Registro 0 al 0 de 0",
			sInfoFiltered : "(filtered from _MAX_ total entries)",
			sInfoPostFix : "",
			sInfoThousands : ",",
			sSearch : "Filtro:",
			oPaginate : {
				sFirst : "Primero",
				sPrevious : "Ant.",
				sNext : "Sigte.",
				sLast : "&Uacute;ltimo"
			}
		}
	});
	$("#tblPluObra tbody").dblclick(function(event) {
		doubleClickPluObra(event);
	});


}//Fin de la funcion init

function cancelaModifEstim() {
	modificacionEstimacion = false;
	limpiaCamposEstimacion();
	$("#noEstimacion").show();
	$("#noEstimacion2").hide();
	$("#btnCancelModificacion").hide();
	$("#btnModEstimacion").hide();
	$("#btnAddEstimacion").show();
	muestraTabla();
	document.getElementById("TipoCaptura").selectedIndex = "0";
	tipoCaptura();
	document.getElementById("TipoCaptura").disabled = false;
}
function validaFechasEstimacion() {
	var mesHoy = parseInt($("#fHoy").val().split("/")[ 1 ], 10);
	var mesInicio = parseInt($("#fperiodoEstimacionIni").val().split("/")[ 1 ], 10);
	var mesFin = parseInt($("#fperiodoEstimacionFin").val().split("/")[ 1 ], 10);

	if( mesInicio > $("#mesEstimado").val() ) {
		alert("La fecha inicio debe ser menor o igual al mes estimado ");
		return false;
	}

	if( mesFin != $("#mesEstimado").val() ) {
		alert("La fecha final debe ser igual al mes estimado ");
		return false;
	}
	var claveContrato = $("#cCveContrato").val();

	if( mesInicio > mesHoy && claveContrato.indexOf("-PAS") == -1 ) {
		alert("El mes estimado no puede ser mayor al mes actual ");
		return false;
	}
	return true;
}
function borraCancelar() {
	if( $("#reten").val() == 0 ) {
		$('#dt_retencion').dataTable().fnAddData([ "3", "INSPECCION DE OBRA (0.5%)" ]);
		$("#btnCancelDocAmortizacion").hide();
	}

}

function llenaRetenciones(agregaRegistro) {
	var i = 0;
	if( $("#nFolioOPComHeader").val() != -1 ) {
		$('#dt_retencion').dataTable().fnClearTable();
		var tableNameReten = 'COMPROMISO_O_PUB_RETENCIONES_DETAIL';
		var conditionReten = " a.cidcontrato = '" + $("#cCveContrato").val() + "' AND a.nFolioOPCOmHeader = " + $("#nFolioOPComHeader").val();

		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : tableNameReten,
			Param : conditionReten,
			MaxReg : "",
			ajax : 'false'
		}, function(data) {
			$("#reten").val(data.length);
			for( i = 0; i < data.length; i++ ) {
				$('#dt_retencion').dataTable().fnAddData([ data[ i ].Col0, data[ i ].Col1 ]);
			}
		});
	}
	return i;
}

/**
 * Muestra/Oculta las opciones para el 2PC de retencion
 */
function valida2PC() {
	var optSel = $("#cIdTipoRetencion").val();
	if( optSel == 2 )
		$("#opciones2PC").show();
	else
		$("#opciones2PC").hide();
}

function validaAgregarRetencion(retencion) {
	var rowsTbl = $("#dt_retencion").dataTable().fnGetData();
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var ret = rowsTbl[ i ][ 0 ];
		if( retencion == ret )
			return true;
	}
	return false;
}

function llenaInformacionAnticipos() {
	var porcIVA = parseFloat($("#nPorcIVAAplicable").val().replace(/%/g, ''));
	var porcAnticipo = parseFloat($("#nPorcAnticipo").val());

	var importeObra;

	if( document.getElementById("cPluriaAnual").checked|| esPlurianualAniosAnteriores )
		importeObra = parseFloat(quitaFrmt($("#nMontoContratoOP").val())).toFixed(2);
	else
		importeObra = parseFloat(quitaFrmt($("#mObra").val())).toFixed(2);


	var montoAnticipoNeto = parseFloat(importeObra * porcAnticipo / 100).toFixed(2);
	var montoAnticipoIVA = parseFloat(montoAnticipoNeto * porcIVA).toFixed(2);
	var totalAnticipo = parseFloat(montoAnticipoNeto) + parseFloat(montoAnticipoIVA);

	$("#porcRetencion").val(porcAnticipo);
	$("#porcAnticipoShow").val(porcAnticipo + '%');
	$("#impBrutoShow").val(montoAnticipoNeto);
	$("#ivaAnticipoShow").val(montoAnticipoIVA);
	$("#totalAnticipoShow").val(totalAnticipo);

	cambiafrmt($("#impBrutoShow")[ 0 ]);
	cambiafrmt($("#ivaAnticipoShow")[ 0 ]);
	cambiafrmt($("#totalAnticipoShow")[ 0 ]);

}

function guardaDetalleRetenciones() {
	queryFormPost({
		queryName : "readnFolioOPComHeader",
		async : false,
		callback : function() {
			queryFormPost("deleteDetalleRetenciones", {
				async : false
			});

			var rowsTbl = $("#dt_retencion").dataTable().fnGetData();
			for( var i = 0; i < rowsTbl.length; i++ ) {
				var idRet = rowsTbl[ i ][ 0 ];
				$("#cIdTipoRetencionVal").val(idRet);
				queryFormPost("createDetalleRetenciones", {
					async : false
				});
			}

		}
	});
}

function guardaPorceRetencion() {
	var porceRet = $("#porcRetencion").val();
	if( porceRet == '' )
		$("#porcRetencion").val("0");

	$("#porcRetencionSend").val($("#porcRetencion").val());
	queryFormPost("updatePorcRetencionOP", {
		async : false
	});
}

function validaPorcRetencion() {
	var retencionVal = $("#porcRetencion").val();
	var porcAnticipo = $("#porcAnticipoShow").val().replace("%", "");

	if( retencionVal != '' && parseFloat(retencionVal) != 0 && parseFloat(retencionVal) < parseFloat(porcAnticipo) ) {
		alert("El porcentaje de Amortizacion debe ser igual o mayor al porcentaje de Anticipo");
		$("#porcRetencion").val("");
		$("#porcRetencion").focus();
	}

}

function fnGetSelected(oTableLocal) {
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();

	for( var i = 0; i < aTrs.length; i++ ) {
		if( $(aTrs[ i ]).hasClass('row_selected') ) {
			aReturn.push(aTrs[ i ]);
		}
	}
	return aReturn;
}

/**
 * Agrega EP en Tabla y dataTable
 */
function fnClickAddRowC() {
	myModalEP = new bootstrap.Modal(document.getElementById('dialog-form-ep'), {
	  	keyboard: false
	});
	
	$("#eneImporte").val("0");
	$("#febImporte").val("0");
	$("#marImporte").val("0");
	$("#abrImporte").val("0");
	$("#mayImporte").val("0");
	$("#junImporte").val("0");
	$("#julImporte").val("0");
	$("#agoImporte").val("0");
	$("#sepImporte").val("0");
	$("#octImporte").val("0");
	$("#novImporte").val("0");
	$("#dicImporte").val("0");
	if( $("#cEsRadicado").val() == "N" )
		queryFormPost({
			queryName : "readMontosEPObra",
			async : false,
			callback : function() {

				rowCount = $('#dt_clavepresup tr').length;
				var vep = $('#epSel').val();
				if( vep == '' || vep == '-1' ) {
					return;
				}

				var cint = vep.substring(vep.length - 8);

				if( $('#epSel').val() == $('#nIdClaveEgresos').val() ) {
					alert("Esa clave ya se encuentra Agregada");
					return;
				}

				var nMes = 1;
				var renglon = parseInt($("#nDocRenglon").val(), 10) + 1;

				limpiaMontos();
				
				$("#epDisp").val($("#epSel").val());
				$("#eneroDisp").val($("#eneImporte").val());
				cambiafrmt($("#eneroDisp")[ 0 ]);
				$("#febreroDisp").val($("#febImporte").val());
				cambiafrmt($("#febreroDisp")[ 0 ]);
				$("#marzoDisp").val($("#marImporte").val());
				cambiafrmt($("#marzoDisp")[ 0 ]);
				$("#abrilDisp").val($("#abrImporte").val());
				cambiafrmt($("#abrilDisp")[ 0 ]);
				$("#mayoDisp").val($("#mayImporte").val());
				cambiafrmt($("#mayoDisp")[ 0 ]);
				$("#junioDisp").val($("#junImporte").val());
				cambiafrmt($("#junioDisp")[ 0 ]);
				$("#julioDisp").val($("#julImporte").val());
				cambiafrmt($("#julioDisp")[ 0 ]);
				$("#agostoDisp").val($("#agoImporte").val());
				cambiafrmt($("#agostoDisp")[ 0 ]);
				$("#septiembreDisp").val($("#sepImporte").val());
				cambiafrmt($("#septiembreDisp")[ 0 ]);
				$("#octubreDisp").val($("#octImporte").val());
				cambiafrmt($("#octubreDisp")[ 0 ]);
				$("#noviembreDisp").val($("#novImporte").val());
				cambiafrmt($("#noviembreDisp")[ 0 ]);
				$("#diciembreDisp").val($("#dicImporte").val());
				cambiafrmt($("#diciembreDisp")[ 0 ]);
				sumaTotalCalendarizado();
				inhabilitaCapturaMontosPorMes();
				
				myModalEP.show();
				$("#divModals").show();

			}
		});
	else
		queryFormPost({
			queryName : "readMontosEPObraRadicado",
			async : false,
			callback : function() {

				rowCount = $('#dt_clavepresup tr').length;
				var vep = $('#epSel').val();
				if( vep == '' || vep == '-1' ) {
					return;
				}

				var cint = vep.substring(vep.length - 8);

				if( $('#epSel').val() == $('#nIdClaveEgresos').val() ) {
					alert("Esa clave ya se encuentra Agregada");
					return;
				}

				var nMes = 1;
				var renglon = parseInt($("#nDocRenglon").val(), 10) + 1;

				limpiaMontos();

				$("#epDisp").val($("#epSel").val());
				$("#eneroDisp").val($("#eneImporte").val());
				cambiafrmt($("#eneroDisp")[ 0 ]);
				$("#febreroDisp").val($("#febImporte").val());
				cambiafrmt($("#febreroDisp")[ 0 ]);
				$("#marzoDisp").val($("#marImporte").val());
				cambiafrmt($("#marzoDisp")[ 0 ]);
				$("#abrilDisp").val($("#abrImporte").val());
				cambiafrmt($("#abrilDisp")[ 0 ]);
				$("#mayoDisp").val($("#mayImporte").val());
				cambiafrmt($("#mayoDisp")[ 0 ]);
				$("#junioDisp").val($("#junImporte").val());
				cambiafrmt($("#junioDisp")[ 0 ]);
				$("#julioDisp").val($("#julImporte").val());
				cambiafrmt($("#julioDisp")[ 0 ]);
				$("#agostoDisp").val($("#agoImporte").val());
				cambiafrmt($("#agostoDisp")[ 0 ]);
				$("#septiembreDisp").val($("#sepImporte").val());
				cambiafrmt($("#septiembreDisp")[ 0 ]);
				$("#octubreDisp").val($("#octImporte").val());
				cambiafrmt($("#octubreDisp")[ 0 ]);
				$("#noviembreDisp").val($("#novImporte").val());
				cambiafrmt($("#noviembreDisp")[ 0 ]);
				$("#diciembreDisp").val($("#dicImporte").val());
				cambiafrmt($("#diciembreDisp")[ 0 ]);
				sumaTotalCalendarizado();
				inhabilitaCapturaMontosPorMes();
				myModalEP.show();
				$("#divModals").show();

			}
		});

}

/**
 * Deshabilita los campos en que se muestra los montos de la EP seleccionada.
 */
function deshabilitaMontos() {
	$(".monto").each(function() {
		$(this).attr('readonly', 'readonly');
	});
}
/**
 * Funcion que limpia el contenido del area de captura de montos.
 */
function limpiaMontos() {
	$(".monto").each(function() {
		$(this).val('');
	});
	if( !modificando ) {
		$(".montoCaptura").each(function() {
			$(this).val('0');
			cambiafrmt(this);
		});
	}
}

/**
 * Funcion que inicializa los input's de captura de montos para una EP, asigna
 * la funcion controladora para los eventos onBlur y onFocus
 */
function iniciaCapturaMontos() {
	$(".montoCaptura").each(function() {
		$(this).val(0);
		cambiafrmt(this);
		$(this).focus(function() {
			onFocusMontoMoney(this.id);
		});
		$(this).blur(function() {
			onBlurMontoMoney(this.id);
			sumaTotalCalendarizado();
		});
		$(this).keypress(function(e) {
			return onlyNumbers(e);
		});
	});

}

/**
 * Funcion general que limpia el contenido de un select agregando una opcion por
 * default con valor -1
 */
function clearSelect(idSel) {
	for( var i = 0; i < idSel.length; i++ )
		$('#' + idSel[ i ]).find('option').remove().end().append(
			'<option value="-1"></option>');
}

/**
 * Funcion que valida que solo se ingresen numeros en un campo de texto.
 * 
 * @param evt
 *            Evento.
 * @returns {Boolean} true si y solo si es numero o los caractes [.] y [-]
 */
function onlyNumbers(evt) {
	var keyPressed = ( evt.which ) ? evt.which : event.keyCode;
	if( keyPressed == 47 ) {
		return false;
	}
	return ( ( keyPressed > 47 && keyPressed < 58 ) || keyPressed == 46 );
}

function agregaMovimientos() {
	var renglon = ( modificando ? filaModificada : $("#dt_clavepresup").dataTable().fnGetData().length );
	if( esPagoPasivo ) {
		renglon = ( modificando ? filaModificada : $("#dt_clavePasivo").dataTable().fnGetData().length );
	}
	if( esPlurianual ) {
		renglon = ( modificando ? filaModificada : $("#dt_clavePlurianual").dataTable().fnGetData().length );
	}
	var arrAdd = new Array();
	var sumaCargos = 0;

	arrAdd[ 0 ] = parseInt(renglon, 10) + 1;
	arrAdd[ 1 ] = $("#epSel").val();

	var i = 0;
	j = 2;

	for( i = 0; i < nombreMeses.length; i++ ) {

		sumaCargos += parseFloat(quitaFrmt($("#" + nombreMeses[ i ] + "Apart").val()));
		$("#toConvert").val(quitaFrmt($("#" + nombreMeses[ i ] + "Disp").val()) - quitaFrmt($("#" + nombreMeses[ i ] + "Apart").val()));
		cambiafrmt($("#toConvert")[ 0 ]);

		arrAdd[ j++ ] = $("#" + nombreMeses[ i ] + "Disp").val();
		arrAdd[ j++ ] = $("#" + nombreMeses[ i ] + "Apart").val();

		arrAdd[ j++ ] = $("#toConvert").val();

	}
	var mImporteCapEPs;

	if( esConvenioModificatorio ) {
		// Valida que no sobrepase el monto con iva de la obra.
		mImporteCapEPs = parseFloat(quitaFrmt($("#mIncrementoConIVA").val()));
		if( parseFloat(sumaCargos.toFixed(2)) > mImporteCapEPs ) {
			alert("El importe capturado de la EP sobrepasa al monto con IVA del convenio");
			return false;
		}
		else
			$("#mImporteCapEPs").val(mImporteCapEPs - sumaCargos);
	// Termina valida el monto con iva
	} else if( esPagoPasivo ) {
		// Valida que no sobrepase el monto con iva de la obra.
		mImporteCapEPs = parseFloat(quitaFrmt($("#mTotalPagoPasivo").val()));
		if( parseFloat(sumaCargos.toFixed(2)) > mImporteCapEPs ) {
			alert("El importe capturado de la EP sobrepasa al monto con IVA del convenio");
			return false;
		}
		else
			$("#mImporteCapEPs").val(mImporteCapEPs - sumaCargos);

		var avMontoTotal = 0;
		if( $("#conveniosAnteriores").val() == "" ) // No tiene convenios
			avMontoTotal = parseFloat(quitaFmt($("#mTotal").val()));
		else
			avMontoTotal = parseFloat(quitaFmt($("#conveniosAnteriores").val()));
		avMontoTotal = Math.round(avMontoTotal * 100);

		queryFormPost({
			queryName : "sumaEstimacionRead",
			async : false,
			callback : function() {}
		});

		avMontoTotal -= Math.round(( parseFloat(sumaCargos) + parseFloat($("#sumaEstimacion").val()) ) * 100) ;

		if( avMontoTotal < 0 ) {
			// alert(avMontoTotal);
			alert("La suma de las estimaciones más el pasivo rebasa el monto total del contrato. " + avMontoTotal / 100);
			return;
		}
	// Termina valida el monto con iva
	} else if( esPlurianual ) {
		// Valida que no sobrepase el monto con iva de la obra.
		mImporteCapEPs = parseFloat(quitaFrmt($("#mTotalPlurianual").val()));
		if( parseFloat(sumaCargos.toFixed(2)) > mImporteCapEPs ) {
			alert("El importe capturado de la EP sobrepasa al monto con IVA del convenio");
			return false;
		}
		else
			$("#mImporteCapEPs").val(mImporteCapEPs - sumaCargos);

		var avMontoTotal = 0;
		if( $("#conveniosAnteriores").val() == "" ) // No tiene convenios
			avMontoTotal = parseFloat(quitaFmt($("#mTotal").val()));
		else
			avMontoTotal = parseFloat(quitaFmt($("#conveniosAnteriores").val()));
		avMontoTotal = Math.round(avMontoTotal * 100);

		queryFormPost({
			queryName : "sumaEstimacionRead",
			async : false,
			callback : function() {}
		});

	/*
	 * avMontoTotal -=
	 * Math.round((parseFloat(sumaCargos)+parseFloat($("#sumaEstimacion").val())) *
	 * 100) ;
	 * 
	 * if(avMontoTotal < 0){ // alert(avMontoTotal); alert("La suma de las
	 * estimaciones más el pasivo rebasa el monto total del contrato. " +
	 * avMontoTotal/100); return; }
	 */
	// Termina valida el monto con iva
	} else {
		// Valida que no sobrepase el monto con iva de la obra.
		mImporteCapEPs = parseFloat(quitaFrmt($("#mTotal").val()));
		var tot = parseFloat(obtineTotalEpsCapturadas());
		// alert("monto total :"+mImporteCapEPs +" captura :"+tot)
		if( mImporteCapEPs < tot ) {
			alert("Supero el monto total del Contrato; montoTotal= " + mImporteCapEPs + "  totalCapturado = " + tot);
			return;
		} else if( parseFloat(quitaFrmt($("#totalCalendarizado").val())) <= 0 ) {
			alert("No puede capturar EP´s con montos negativos o en ceros");
			return;
		} else if( parseFloat(sumaCargos.toFixed(2)) > mImporteCapEPs ) {
			alert("El importe capturado de la EP sobrepasa al monto con IVA de la obra");
			return false;
		}
		else
			$("#mImporteCapEPs").val(mImporteCapEPs - sumaCargos);
	// Termina valida el monto con iva
	}
	var totAnual = parseFloat($("#anualImporte").val()) - parseFloat(sumaCargos);
	// $("#toConvert").val(totAnual);
	$("#toConvert").val(sumaCargos);
	cambiafrmt($("#toConvert")[ 0 ]);

	arrAdd[ j++ ] = $("#toConvert").val();

	arrAdd[ j++ ] = createImgContent("imagenes/edit.png", "editaMovimiento(" + renglon + ")", "Editar");
	arrAdd[ j++ ] = createImgContent("imagenes/delete.png", "eliminaMovimiento(" + renglon + ")", "Eliminar");

	if( modificando == true ) {
		if( esPagoPasivo ) {
			$("#dt_clavePasivo").dataTable().fnUpdate(arrAdd, filaModificada);
		} else if( esPlurianual ) {
			$("#dt_clavePlurianual").dataTable().fnUpdate(arrAdd, filaModificada);

		} //MLR 13 Convenio 
		else if( esConvenioModificatorio ) {
			$("#dt_claveConvenioModificatorio").dataTable().fnUpdate(arrAdd, filaModificada);
		} else {
			$('#dt_clavepresup').dataTable().fnClearTable();
			$("#dt_clavepresup").dataTable().fnUpdate(arrAdd, filaModificada);
		}
		modificando = false;
		filaModificada = -1;
	} else if( esPagoPasivo ) {
		$('#dt_clavePasivo').dataTable().fnClearTable();
		$("#dt_clavePasivo").dataTable().fnAddData(arrAdd);
	} else if( esPlurianual ) {
		$("#dt_clavePlurianual").dataTable().fnAddData(arrAdd);
	}

	// MLR 13 
	else if( esConvenioModificatorio ) {
		$("#dt_claveConvenioModificatorio").dataTable().fnAddData(arrAdd);
	} else {
		$('#dt_clavepresup').dataTable().fnClearTable();
		$("#dt_clavepresup").dataTable().fnAddData(arrAdd);
		document.getElementById("chk_radicado").disabled = true;
	}
	return true;
}

function createImgContent(imageSrc, fn, alt) {
	return '<a href="#" onclick="' + fn + '" > <img alt="' + alt + '" src="' + imageSrc + '" align="middle" border="0">  </a>';
}


function editaMovimiento(nRenglon) {
	$("#esEditado").val(1);
	var tablaModificada = $("#dt_clavepresup").dataTable();
	if( esPlurianual )
		tablaModificada = $("#dt_clavePlurianual").dataTable();
	if( esPagoPasivo )
		tablaModificada = $("#dt_clavePasivo").dataTable();

	//MLR 13 
	if( esConvenioModificatorio )
		tablaModificada = $("#dt_claveConvenioModificatorio").dataTable();

	var dt_clavepresup = tablaModificada.fnGetData()[ nRenglon ][ 1 ];

	if( !tablaModificada.fnGetData()[ nRenglon ] ) {
		alert("No se puede editar el renglon " + nRenglon);
	} else {
		modificando = true;
		filaModificada = nRenglon;

		var ep = tablaModificada.fnGetData()[ nRenglon ][ 1 ];
		$("#epSel").append('<option value="' + ep + '">' + ep + '</option>');
		$("#epSel").val(ep);

		var j = 3;
		for( var i = 0; i < nombreMeses.length; i++ ) {
			$("#" + nombreMeses[ i ] + "Apart").val(tablaModificada.fnGetData()[ nRenglon ][ j ]);
			if( $("#" + nombreMeses[ i ] + "Hide").val() == "" )
				$("#" + nombreMeses[ i ] + "Hide").val(tablaModificada.fnGetData()[ nRenglon ][ j ]);
			cambiafrmt($("#" + nombreMeses[ i ] + "Apart")[ 0 ]);
			j = j + 3;
		}
		fnClickAddRowC();
	}
}

function eliminaMovimiento(nRenglon) {
	if( confirm("Esta seguro que desea eliminar este registro?") ) {
		// Devuelve el valor que se tenía de la EP a la variable mImporteCapEPs
		if( esConvenioModificatorio ) { //SASV 25/05/15 CM 
			var currRow = $("#dt_claveConvenioModificatorio").dataTable().fnGetData()[ nRenglon ];
			$("#mImporteCapEPs").val(parseFloat($("#mImporteCapEPs").val()) + parseFloat(currRow[ 38 ]));

			$("#dt_claveConvenioModificatorio").dataTable().fnDeleteRow(nRenglon);
			renumeraTabla();
		} else {
			var currRow = $("#dt_clavepresup").dataTable().fnGetData()[ nRenglon ];

			$("#mImporteCapEPs").val(parseFloat($("#mImporteCapEPs").val()) + parseFloat(currRow[ 38 ]));

			$("#dt_clavepresup").dataTable().fnDeleteRow(nRenglon);
			renumeraTabla();
		}
		if( parseInt($("#dt_clavepresup").dataTable().fnGetData().length, 10) == 0 )
			document.getElementById("chk_radicado").disabled = false;
	}
}


function renumeraTabla() {
	var rows = $("#dt_clavepresup").dataTable().fnGetData();
	var rw = 1;

	for( var i = 0; i < rows.length; i++ ) {
		$("#dt_clavepresup").dataTable().fnUpdate(rw, i, 0);
		rw++;
	}

}


function applyDocument() {
	if( confirm("Enviara el Documento a Aplicar Contablemente.\n¿Desea continuar?") ) {
		divAplica.innerHTML = "Procesando, por favor espere.";
		$("#divModals").show();
		myModalApcon.show();
		saveByCRUD();
		setTimeout('fnAplicaMotor()', 2000);
	}
}

function cancelContract() {
	if( confirm("Enviara el Documento a Cancelar Contablemente.\n¿Desea continuar?") ) {
		divAplica.innerHTML = "Procesando, por favor espere.";
		$("#divModals").show();
		myModalApcon.show();
		setTimeout('fnAplicaMotorForCancel()', 2000);
	}

}

function saveByCRUD() {
	enabledSelect();
	if( $("#pasaValor").val() == 'apartado' ) {
		if( $('#dt_clavepresup').dataTable().fnGetData().length > 1 ) {
			var result = comparaEpsCapturadas();
			if( parseInt(result, 10) > 0 ) {
				alert("Las EP´s son de diferente subpartida");
				return;
			}


		}
		var mImporteCapEPs = parseFloat(quitaFrmt($("#mTotal").val()));
		var tot2 = parseFloat(obtineTotalEpsCapturadas2());
		tot2 = tot2.toFixed(2);
		// alert("monto total :"+mImporteCapEPs +" captura :"+tot)
		if( mImporteCapEPs != tot2 ) {
			alert("Los montos son diferentes; montoTotal= " + mImporteCapEPs + "  totalCapturado = " + tot2);
			return;
		}
		$("#div_btnCargaPlurianualidad").hide();
		saveHeaderApaByCrud();
	}
	if( $("#pasaValor").val() == 'precompromiso' ) {
		saveHeaderPreByCrud();
	}
	if( $("#pasaValor").val() == 'compromiso' ) {
		if( $("#id_gabinete").val() < 1 )
			queryFormPost({
				queryName : "idGabineteOPread",
				async : false,
				callback : function() {}
			});

		var arrVal = {
			"cNoConvenio" : "No Convenio"
		};
		$("#ExisteComEnc").val(0);

		queryFormPost({
			queryName : "existeComRead",
			async : false,
			callback : function() {
				if( $("#ExisteComEnc").val() > 0 )
					saveHeaderComByCrud(false);
				else
					saveHeaderComByCrud(true);
			
				save();
			}
		});
	}
	if( $("#pasaValor").val() == 'convenio' ) {
		var arrVal = {
			"cNoConvenio" : "No Convenio"
		};
		if( validateNotEmpty(arrVal) ) {
			saveHeaderConvByCrud();
		}
	}
	if( $("#pasaValor").val() == 'pagoPasivo' ) {
		saveHeaderPagoPasivoByCrud();
	}
	if( $("#pasaValor").val() == 'plurianual' ) {
		saveHeaderPlurianualByCrud();
	// saveHeaderPagoPasivoByCrud();
	}
	// alert(" termina saveHeaderPlurianualByCrud");
	//disabledSelects(); revisar SAM
	parent.document.getElementById("pb_save").click();

}

function saveHeaderApaByCrud() {
	if( "" != $("#cCveContrato").val() ) {
		if( existeNoContrato() ) {
			alert("El contrato " + $("#cCveContrato").val() + " ya existe capturado previamente en el folio: " + $("#folioExistente").val()
				+ "\n Por favor asigne otro n\u00FAmero de contrato.");
			alert("\u00A1ATENCI\u00D3N\u0021 No se guardaron los datos.");
			return false;
		}
	}

	$("#nMontoNoFrmt").val(quitaFrmt($("#mObra").val()));
	$("#nMontoConIVANoFrmt").val(quitaFrmt($("#mTotal").val()));
	$("#idAreaSend").val($("#id_area").val());
	queryFormPost({
		queryName : "createObraPubicaApaEncabezado",
		async : false,
		callback : function() {
			queryFormPost({
				queryName : "updatetObraPublicaApartadoEncabezadoRadicado",
				async : false,
				callback : function() {
					saveDetailApaByCrud();
				}
			});
		}
	});

	$("#iWhereIsContract").val(1);
}

function saveHeaderPreByCrud() {
	if( "" != $("#cCveContrato").val() ) {
		if( existeNoContrato() ) {
			alert("El contrato " + $("#cCveContrato").val() + " ya existe capturado previamente en el folio: " + $("#folioExistente").val()
				+ "\n Por favor asigne otro n\u00FAmero de contrato.");
			alert("\u00A1ATENCI\u00D3N\u0021 No se guardaron los datos.");
			return false;
		}
	}
	$("#nMontoNoFrmt").val(quitaFrmt($("#mObra").val()));
	$("#nMontoConIVANoFrmt").val(quitaFrmt($("#mTotal").val()));
	
	$("#iTieneAnticipo").val("0");
	$("#nPorcAnticipo").val("0.00");
	
	if($("#vcIdContrato").val() != ""){
		queryFormPost({queryName : "readAllPlurianualidad", async : false, callback : function() {} });
		$("#iTieneAnticipo").val($("#viTieneAnticipo").val());
		$("#nPorcAnticipo").val($("#vnPorceAnticipo").val());
	}
	$("#idAreaSend").val($("#id_area").val());
	queryFormPost({
		queryName : "createObraPubicaPreEncabezado",
		async : false,
		callback : function() {
			queryFormPost({
				queryName : "updatetObraPublicaPreCompromisoEncabezadoRadicado",
				async : false,
				callback : function() {
					saveDetailPreByCrud();
				}
			});
		}
	});
	$("#iWhereIsContract").val(3); // El estado del contrato esta en 3
	// (almacenado en DataBase en Precompromiso
	// sin aplicar el motor contable)

}

function saveDetailApaByCrud() {
	queryFormPost({
		queryName : "readnFolioOpaHeader",
		async : false,
		callback : function() {}
	});

	var rowsTbl = $("#dt_clavepresup").dataTable().fnGetData();
	var nDocRenglonVal = 1;
	var insertados = 0;
	// Hacer llamada de borrado al detalle
	queryFormPost({
		queryName : "deleteAllForIdHeaderObraPubicaApaDetalle",
		async : false,
		callback : function() {
			insertados++;
		}
	});
	// Termina llamada de borrado al detalle
	var conta = 1;
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var epVal = currRow[ 1 ];
		var k = 3;

		for( var j = 0; j < nombreMeses.length; j++ ) {
			if( quitaFrmt(currRow[ k ]) > 0 ) {
				$("#EPSend").val(epVal);
				$("#cMes").val(j + 1);
				$("#mImporte").val(quitaFrmt(currRow[ k ]));
				if( $("#cEsRadicado").val() == "N" )
					$("#cEvento").val("OP_APT");
				else
					$("#cEvento").val("ROP_APT");
				$("#mImporteNegativo").val($("#mImporte").val() * -1);
				$("#nDocRenglon").val(conta);
				queryFormPost({
					queryName : "createObraPubicaApaDetalle",
					async : false,
					callback : function() {
						insertados++;
					}
				});
				conta = conta + 1;
			}
			k = k + 3;
		}

	}
	alert("Los cambios han sido guardados exitosamente");
}

function saveDetailPreByCrud() {
	queryFormPost({
		queryName : "readnFolioOPPreComHeader",
		async : false,
		callback : function() {}
	});

	queryFormPost({
		queryName : "readnFolioOpaHeader",
		async : false,
		callback : function() {}
	});

	var rowsTbl = $("#dt_clavepresup").dataTable().fnGetData();
	var nDocRenglonVal = 1;
	var insertados = 0;

	if( $("#cEsRadicado").val() == "N" )
		$("#cEvento").val("PRECOM_OPC");
	else
		$("#cEvento").val("R_PRECOM_OPC");
	// Hacer llamada de borrado al detalle
	queryFormPost({
		queryName : "deleteAllForIdHeaderObraPubicaPreDetalle",
		async : false,
		callback : function() {
			insertados++;
		}
	});

	if( $("#cEsRadicado").val() == "N" )
		$("#cEvento").val("OP_LA");
	else
		$("#cEvento").val("ROP_LA");

	queryFormPost({
		queryName : "creaOPPrecompromisoDetalleComplemento",
		async : false,
		callback : function() {}
	});

	$("#docRenglon").val("1");

	queryFormPost({
		queryName : "renglonSiguientePrecompromisoDetalleRead",
		async : false,
		callback : function() {}
	});
	var conta = parseInt($("#docRenglon").val(), 10);

	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var epVal = currRow[ 1 ];
		var k = 3;

		for( var j = 0; j < nombreMeses.length; j++ ) {
			if( quitaFrmt(currRow[ k ]) > 0 ) {
				$("#mImporte").val("0");
				$("#mImporteAnt").val("0");
				$("#mImporteDif").val("0");

				$("#EPSend").val(epVal);
				$("#epFind").val(epVal);
				$("#cMes").val(j + 1);
				$("#cmesFind").val($("#cMes").val());
				$("#mImporte").val(quitaFrmt(currRow[ k ]));

				queryFormPost("readEPMesApartado", {
					async : false
				});
				if( $("#mImporteAnt").val() == '' || parseFloat($("#mImporteAnt").val()) == 0 )
					$("#mImporteAnt").val("0");

				$("#mImporteDif").val($("#mImporte").val() - $("#mImporteAnt").val());

				if( $("#cEsRadicado").val() == "N" )
					$("#cEvento").val("OP_PCM");
				else
					$("#cEvento").val("ROP_PCM");

				$("#mImporteNegativo").val($("#mImporte").val() * -1);
				$("#nDocRenglon").val(conta);
				queryFormPost({
					queryName : "createObraPubicaPreDetalle",
					async : false,
					callback : function() {
						insertados++;
					}
				});
				conta = conta + 1;
			}
			k = k + 3;
		}
	}

	$("#FolioSAI").val($("#FOLIO").val());
	alert("Los cambios han sido guardados exitosamente");
}


function cargaDetallePagoPasivo() {
	var iWhereIsContract = $("#iWhereIsContract").val();
	var campos = "5,'" + $("#FOLIO").val() + "'";
	$("#dt_clavePasivo").dataTable().fnClearTable();

	queryFormPost({
		queryName : "readApartadoAplicado",
		async : false,
		callback : function() {}
	});
	$("#fRecepcion").val(hoy);

	var elParametro2 = '';
	var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro2,
		Campos : campos,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		var rn = 0;
		$('#dt_clavePasivo').dataTable().fnClearTable();

		for( var i = 0; i < data.length; i++ ) {
			var ep = data[ i ].Col0;
			var eneDisp = data[ i ].Col4,
				eneApar = data[ i ].Col3,
				eneDif = eneDisp - eneApar;
			var febDisp = data[ i ].Col6,
				febApar = data[ i ].Col5,
				febDif = febDisp - febApar;
			var marDisp = data[ i ].Col8,
				marApar = data[ i ].Col7,
				marDif = marDisp - marApar;
			var abrDisp = data[ i ].Col10,
				abrApar = data[ i ].Col9,
				abrDif = abrDisp - abrApar;
			var mayDisp = data[ i ].Col12,
				mayApar = data[ i ].Col11,
				mayDif = mayDisp - mayApar;
			var junDisp = data[ i ].Col14,
				junApar = data[ i ].Col13,
				junDif = junDisp - junApar;
			var julDisp = data[ i ].Col16,
				julApar = data[ i ].Col15,
				julDif = julDisp - julApar;
			var agoDisp = data[ i ].Col18,
				agoApar = data[ i ].Col17,
				agoDif = agoDisp - agoApar;
			var sepDisp = data[ i ].Col20,
				sepApar = data[ i ].Col19,
				sepDif = sepDisp - sepApar;
			var octDisp = data[ i ].Col22,
				octApar = data[ i ].Col21,
				octDif = octDisp - octApar;
			var novDisp = data[ i ].Col24,
				novApar = data[ i ].Col23,
				novDif = novDisp - novApar;
			var dicDisp = data[ i ].Col26,
				dicApar = data[ i ].Col25,
				dicDif = dicDisp - dicApar;
			var anuTot = data[ i ].Col1;
			var urlModif = createImgContent("imagenes/edit.png", "editaMovimiento(" + rn + ")", "Editar");
			var urlDel = createImgContent("imagenes/delete.png", "eliminaMovimiento(" + rn + ")", "Eliminar");
			// RO-0002 IRD Solo el administrador puede borrar
			if( $("#tipoUsuario").val() != "ADMIN" ) {
				// urlModif = "";
				urlDel = "";
			}

			var arr = new Array(rn + 1, ep, eneDisp, eneApar, eneDif,
				febDisp, febApar, febDif, marDisp, marApar,
				marDif, abrDisp, abrApar, abrDif, mayDisp,
				mayApar, mayDif, junDisp, junApar, junDif,
				julDisp, julApar, julDif, agoDisp, agoApar,
				agoDif, sepDisp, sepApar, sepDif, octDisp,
				octApar, octDif, novDisp, novApar, novDif,
				dicDisp, dicApar, dicDif, anuTot, urlModif, urlDel);

			$("#dt_clavePasivo").dataTable().fnAddData(arr);
			rn++;

		}
	});
}
function cargaDetallePlurianual() {
	var iWhereIsContract = $("#iWhereIsContract").val();
	var campos = "6,'" + $("#FOLIO").val() + "'";
	$("#dt_clavePlurianual").dataTable().fnClearTable();

	queryFormPost({
		queryName : "readApartadoAplicado",
		async : false,
		callback : function() {}
	});
	$("#fRecepcion").val(hoy);

	var elParametro2 = '';
	var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro2,
		Campos : campos,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		var rn = 0;
		$('#dt_clavePlurianual').dataTable().fnClearTable();

		for( var i = 0; i < data.length; i++ ) {
			var ep = data[ i ].Col0;
			var eneDisp = data[ i ].Col4,
				eneApar = data[ i ].Col3,
				eneDif = eneDisp - eneApar;
			var febDisp = data[ i ].Col6,
				febApar = data[ i ].Col5,
				febDif = febDisp - febApar;
			var marDisp = data[ i ].Col8,
				marApar = data[ i ].Col7,
				marDif = marDisp - marApar;
			var abrDisp = data[ i ].Col10,
				abrApar = data[ i ].Col9,
				abrDif = abrDisp - abrApar;
			var mayDisp = data[ i ].Col12,
				mayApar = data[ i ].Col11,
				mayDif = mayDisp - mayApar;
			var junDisp = data[ i ].Col14,
				junApar = data[ i ].Col13,
				junDif = junDisp - junApar;
			var julDisp = data[ i ].Col16,
				julApar = data[ i ].Col15,
				julDif = julDisp - julApar;
			var agoDisp = data[ i ].Col18,
				agoApar = data[ i ].Col17,
				agoDif = agoDisp - agoApar;
			var sepDisp = data[ i ].Col20,
				sepApar = data[ i ].Col19,
				sepDif = sepDisp - sepApar;
			var octDisp = data[ i ].Col22,
				octApar = data[ i ].Col21,
				octDif = octDisp - octApar;
			var novDisp = data[ i ].Col24,
				novApar = data[ i ].Col23,
				novDif = novDisp - novApar;
			var dicDisp = data[ i ].Col26,
				dicApar = data[ i ].Col25,
				dicDif = dicDisp - dicApar;
			var anuTot = data[ i ].Col1;
			var urlModif = createImgContent("imagenes/edit.png", "editaMovimiento(" + rn + ")", "Editar");
			var urlDel = createImgContent("imagenes/delete.png", "eliminaMovimiento(" + rn + ")", "Eliminar");
			// RO-0002 IRD Solo el administrador puede borrar
			if( $("#tipoUsuario").val() != "ADMIN" ) {
				// urlModif = "";
				urlDel = "";
			}

			var arr = new Array(rn + 1, ep, eneDisp, eneApar, eneDif,
				febDisp, febApar, febDif, marDisp, marApar,
				marDif, abrDisp, abrApar, abrDif, mayDisp,
				mayApar, mayDif, junDisp, junApar, junDif,
				julDisp, julApar, julDif, agoDisp, agoApar,
				agoDif, sepDisp, sepApar, sepDif, octDisp,
				octApar, octDif, novDisp, novApar, novDif,
				dicDisp, dicApar, dicDif, anuTot, urlModif, urlDel);

			$("#dt_clavePlurianual").dataTable().fnAddData(arr);
			rn++;

		}
	});
}
/*
 * function cargaDetalleConvenio() { var iWhereIsContract =
 * $("#iWhereIsContract").val(); var campos = "4,'" +$("#FOLIO").val()+"'";
 * $("#dt_claveConvenioModificatorio").dataTable().fnClearTable();
 * 
 * queryFormPost({queryName:"readApartadoAplicado", async : false,
 * callback:function(){ } }); $("#fRecepcion").val(hoy);
 * 
 * var elParametro2 = ''; var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
 * $.getJSON("../catalogos/SelectJson.jsp", { Tabla : szTabla, Param :
 * elParametro2, Campos : campos, MaxReg : "", ajax : 'false' }, function(data) {
 * var rn = 0; $('#dt_claveConvenioModificatorio').dataTable().fnClearTable();
 * 
 * for( var i = 0; i < data.length; i++){ var ep=data[i].Col0; var eneDisp =
 * data[i].Col4, eneApar = data[i].Col3, eneDif = eneDisp - eneApar; var febDisp =
 * data[i].Col6, febApar = data[i].Col5, febDif = febDisp - febApar; var marDisp =
 * data[i].Col8, marApar = data[i].Col7, marDif = marDisp - marApar; var abrDisp =
 * data[i].Col10, abrApar = data[i].Col9, abrDif = abrDisp - abrApar; var
 * mayDisp = data[i].Col12, mayApar = data[i].Col11, mayDif = mayDisp - mayApar;
 * var junDisp = data[i].Col14, junApar = data[i].Col13, junDif = junDisp -
 * junApar; var julDisp = data[i].Col16, julApar = data[i].Col15, julDif =
 * julDisp - julApar; var agoDisp = data[i].Col18, agoApar = data[i].Col17,
 * agoDif = agoDisp - agoApar; var sepDisp = data[i].Col20, sepApar =
 * data[i].Col19, sepDif = sepDisp - sepApar; var octDisp = data[i].Col22,
 * octApar = data[i].Col21, octDif = octDisp - octApar; var novDisp =
 * data[i].Col24, novApar = data[i].Col23, novDif = novDisp - novApar; var
 * dicDisp = data[i].Col26, dicApar = data[i].Col25, dicDif = dicDisp - dicApar;
 * var anuTot = data[i].Col1; var urlModif =
 * createImgContent("imagenes/edit.png", "editaMovimiento(" + rn + ")",
 * "Editar"); var urlDel = createImgContent("imagenes/delete.png",
 * "eliminaMovimiento(" + rn + ")", "Eliminar"); //RO-0002 IRD Solo el
 * administrador puede borrar if ($("#tipoUsuario").val() != "ADMIN" ){ //
 * urlModif = ""; urlDel = ""; }
 * 
 * var arr= new Array (rn+1, ep, eneDisp, eneApar, eneDif, febDisp, febApar,
 * febDif, marDisp, marApar, marDif, abrDisp, abrApar, abrDif, mayDisp, mayApar,
 * mayDif, junDisp, junApar, junDif, julDisp, julApar, julDif, agoDisp, agoApar,
 * agoDif, sepDisp, sepApar, sepDif, octDisp, octApar, octDif, novDisp, novApar,
 * novDif, dicDisp, dicApar, dicDif, anuTot , urlModif, urlDel);
 * 
 * $("#dt_claveConvenioModificatorio").dataTable().fnAddData(arr); rn++;
 *  } }); }
 */

function cargaDetalleApartado() {
	var iWhereIsContract = $("#iWhereIsContract").val();
	var campos = "1,'" + $("#FOLIO").val() + "'";
	$("#dt_clavepresup").dataTable().fnClearTable();
	$("#dt_clavepresup2").dataTable().fnClearTable();
	$("#dt_claveConvenioModificatorio").dataTable().fnClearTable();

	queryFormPost({
		queryName : "readApartadoAplicado",
		async : false,
		callback : function() {}
	});

	if( $("#apartadoAplicado").val() == 'S' ) {
		$("#ddt_clavepresup").css("display", "none");
		$("#ddt_clavepresup2").css("display", "");
		$("#dt_claveConvenioModificatorio").css("display", ""); //SASV 25/05/15 CM

	} else {
		$("#ddt_clavepresup").css("display", "");
		$("#ddt_clavepresup2").css("display", "none");
	}

	var elParametro2 = '';
	var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro2,
		Campos : campos,
		MaxReg : "",
		ajax : 'false'
	}, function(data) {
		var rn = 0;
		$('#dt_clavepresup').dataTable().fnClearTable();

		for( var i = 0; i < data.length; i++ ) {
			var ep = data[ i ].Col0;
			var eneDisp = data[ i ].Col4,
				eneApar = data[ i ].Col3,
				eneDif = eneDisp - eneApar;
			var febDisp = data[ i ].Col6,
				febApar = data[ i ].Col5,
				febDif = febDisp - febApar;
			var marDisp = data[ i ].Col8,
				marApar = data[ i ].Col7,
				marDif = marDisp - marApar;
			var abrDisp = data[ i ].Col10,
				abrApar = data[ i ].Col9,
				abrDif = abrDisp - abrApar;
			var mayDisp = data[ i ].Col12,
				mayApar = data[ i ].Col11,
				mayDif = mayDisp - mayApar;
			var junDisp = data[ i ].Col14,
				junApar = data[ i ].Col13,
				junDif = junDisp - junApar;
			var julDisp = data[ i ].Col16,
				julApar = data[ i ].Col15,
				julDif = julDisp - julApar;
			var agoDisp = data[ i ].Col18,
				agoApar = data[ i ].Col17,
				agoDif = agoDisp - agoApar;
			var sepDisp = data[ i ].Col20,
				sepApar = data[ i ].Col19,
				sepDif = sepDisp - sepApar;
			var octDisp = data[ i ].Col22,
				octApar = data[ i ].Col21,
				octDif = octDisp - octApar;
			var novDisp = data[ i ].Col24,
				novApar = data[ i ].Col23,
				novDif = novDisp - novApar;
			var dicDisp = data[ i ].Col26,
				dicApar = data[ i ].Col25,
				dicDif = dicDisp - dicApar;
			var anuTot = data[ i ].Col1;
			var urlModif = createImgContent("imagenes/edit.png", "editaMovimiento(" + rn + ")", "Editar");
			var urlDel = createImgContent("imagenes/delete.png", "eliminaMovimiento(" + rn + ")", "Eliminar");
			if( $("#tipoUsuario").val() != "ADMIN" ) {
				// urlModif = "";
				urlDel = "";
			}

			var arr = new Array(rn + 1, ep, eneDisp, eneApar, eneDif,
				febDisp, febApar, febDif, marDisp, marApar,
				marDif, abrDisp, abrApar, abrDif, mayDisp,
				mayApar, mayDif, junDisp, junApar, junDif,
				julDisp, julApar, julDif, agoDisp, agoApar,
				agoDif, sepDisp, sepApar, sepDif, octDisp,
				octApar, octDif, novDisp, novApar, novDif,
				dicDisp, dicApar, dicDif, anuTot, urlModif, urlDel);

			$("#dt_clavepresup").dataTable().fnAddData(arr);
			$("#dt_clavepresup2").dataTable().fnAddData(arr);
			rn++;

		}
	});
}

function terminaAppContCancel(success) {
	if( success ) {
		parent.document.getElementById("pb_save").click();
		parent.document.getElementById("pb_send").click();
		return;
	} else {
		$("#iCancelContract").val("0");
	}



	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}

function aplicaApartado() {
	var strAction = "../AplicaContableObraPublica?accion=APLICA_APARTADO&FolioSAI=" + $("#FOLIO").val();
	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}

function terminaAppCont(success) {
	$("#FolioSAI").val($("#FOLIO").val());
	myModalApcon.hide();
	$("#divModals").hide();
	

	if( success == true ) {
		if( esConvenioModificatorio || esPagoPasivo || esPlurianual ) {
			parent.document.getElementById("pb_send").click();
		} else {
			var paso = calculaSiguientePaso($("#pasaValor").val());

			if( paso == 'precompromiso' )
				$("#Link02").click();
			else if( paso == 'compromiso' )
				$("#Link03").click();
		}
	}
}
/*
 * function fnAplicaMotor() { var typeDocumet; if ($("#pasaValor").val() ==
 * 'apartado') typeDocument = "1"; else if ($("#pasaValor").val() ==
 * 'precompromiso') typeDocument = "2"; else if ($("#pasaValor").val() ==
 * 'compromiso') { typeDocument = "3"; $("#FolioSAI").val($("#FOLIO").val());
 * $("#iStatus").val(3);
 * 
 * queryFormPost({queryName:"updateStatusObraPublicaHeaderCompromiso", async :
 * false, callback:function(){ } }); //
 * parent.document.getElementById("pb_save").click();
 * parent.document.getElementById("pb_send").click(); return; } else if
 * ($("#pasaValor").val() == 'convenio'){
 * parent.document.getElementById("pb_send").click(); return; }
 * 
 * var strAction = "ObraPublica.jsp?applyDocument=Si" + "&typeDocument=" +
 * typeDocument; divAplica.innerHTML = "<iframe id='ifAplica' src='" +
 * strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT:
 * 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>"; }
 */

function fnAplicaMotor() {
	var typeDocumet;
	if( $("#pasaValor").val() == 'apartado' )
		aplicaApartado();
	else if( $("#pasaValor").val() == 'precompromiso' )
		aplicaPrecompromiso();
	else if( $("#pasaValor").val() == 'compromiso' ) {
		typeDocument = "3";
		$("#FolioSAI").val($("#FOLIO").val());
		$("#iStatus").val(3);

		queryFormPost({
			queryName : "updateStatusObraPublicaHeaderCompromiso",
			async : false,
			callback : function() {}
		});
		parent.document.getElementById("pb_send").click();
		return;
	} else if( $("#pasaValor").val() == 'convenio' ) {
		aplicaConvenio();
	} else if( $("#pasaValor").val() == 'pagoPasivo' ) {
		aplicaPagoPasivo();
	} else if( $("#pasaValor").val() == 'plurianual' ) {
		aplicaPlurianual();
	}

}

function aplicaConvenio() {
	var strAction = "../AplicaContableObraPublica?accion=APLICA_CONVENIO_PRECOMP&FolioSAI=" + $("#FOLIO").val() + "&nFolioOPConvHeader=" + $("#nFolioOPConvHeader").val();
	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}
function aplicaPagoPasivo() {
	var strAction = "../AplicaContableObraPublica?accion=APLICA_PAGO_PASIVO_PRECOMP&FolioSAI=" + $("#FOLIO").val() + "&nFolioOPPagPasHeader=" + $("#nFolioOPPagPasHeader").val();
	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}
function aplicaPlurianual() {
	// alert($("#FOLIO").val()+" "+$("#nFolioOPPlurianualHeader").val()+" ")

	var strAction = "../AplicaContableObraPublica?accion=APLICA_PLURIANUAL_PRECOMP&FolioSAI=" + $("#FOLIO").val()
	+ "&nfolioopplurianualheader=" + $("#nFolioOPPlurianualHeader").val()
	+ "&cEjercicio=" + $("#cEjercicio").val()
	+ "&cCentroContable=" + $("#cCentroContable").val()
	+ "&ccvecontrato=" + $("#cCveContrato").val()
	+ "&cNoConvenio=" + $("#cNoConvenio").val()
	+ "&fInicioConv=" + $("#fInicioConv").val()
	+ "&fFinConv=" + $("#fFinConv").val();


	// "mPlurianual": quitaFrmt( $("#mPlurianual").val() ),
	// "ivaPlurianual":$("#ivaPlurianual").val(),
	// "mTotalPlurianual":quitaFrmt($("#mTotalPlurianual").val()),
	// "cMotivoPlurianual":$("#cMotivoPlurianual").val(),
	// "nFolioOPPlurianualHeader":$("#nFolioOPPlurianualHeader").val(),
	// "fRecepcion":$("#fRecepcion").val(),



	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}

function aplicaApartado() {
	var strAction = "../AplicaContableObraPublica?accion=APLICA_APARTADO&FolioSAI=" + $("#FOLIO").val();
	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}

function aplicaPrecompromiso() {
	var strAction = "../AplicaContableObraPublica?accion=APLICA_PRECOMPROMISO&FolioSAI=" + $("#FOLIO").val();
	divAplica.innerHTML = "<iframe id='ifAplica' src='" + strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
}
/*
 * function fnAplicaMotor() { var typeDocumet; if ($("#pasaValor").val() ==
 * 'apartado') typeDocument = "1"; else if ($("#pasaValor").val() ==
 * 'precompromiso') typeDocument = "2"; else if ($("#pasaValor").val() ==
 * 'compromiso') { typeDocument = "3"; $("#FolioSAI").val($("#FOLIO").val());
 * $("#iStatus").val(3);
 * 
 * queryFormPost({queryName:"updateStatusObraPublicaHeaderCompromiso", async :
 * false, callback:function(){ } }); //
 * parent.document.getElementById("pb_save").click();
 * parent.document.getElementById("pb_send").click(); return; } else if
 * ($("#pasaValor").val() == 'convenio'){
 * parent.document.getElementById("pb_send").click(); return; }
 * 
 * var strAction = "ObraPublica.jsp?applyDocument=Si" + "&typeDocument=" +
 * typeDocument; divAplica.innerHTML = "<iframe id='ifAplica' src='" +
 * strAction + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT:
 * 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>"; }
 */

function fnAplicaMotorForCancel() {
	var typeDocumet;
	if( $("#pasaValor").val() == 'apartado' )
		typeDocument = "1";
	else if( $("#pasaValor").val() == 'precompromiso' )
		typeDocument = "2";
	else if( $("#pasaValor").val() == 'compromiso' )
		typeDocument = "3";
	else if( $("#pasaValor").val() == 'pagoPasivo' )
		typeDocument = "4";
	else if( $("#pasaValor").val() == 'plurianual' )
		typeDocument = "5";
	else if( $("#pasaValor").val() == 'convenio' )
		typeDocument = "6";

	$("#iCancelContract").val(1);
	var strAction = "ObraPublica.jsp?cancelContract=Si" + "&typeDocument="
		+ typeDocument;
	divAplica.innerHTML = "<iframe id='ifAplica' src='"
		+ strAction
		+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";

}


function calculaSiguientePaso(pasoActual) {
	var retVal = "";
	if( pasoActual == 'apartado' )
		retVal = "precompromiso";
	else if( pasoActual == 'precompromiso' )
		retVal = "compromiso";
	return retVal;
}

function saveHeaderComByCrud(insertar) {
	var iEsPlurianual = 0;	
	if( "" != $("#cCveContrato").val() ) {
		if( existeNoContrato() ) {
			alert("El contrato " + $("#cCveContrato").val() + " ya existe capturado previamente en el folio: " + $("#folioExistente").val()
				+ "\n Por favor asigne otro n\u00FAmero de contrato.");
			alert("\u00A1ATENCI\u00D3N\u0021 No se guardaron los datos.");
			return false;
		}
	}
	$("#cIdConvocatoriaSend").val($("#cIdConvocatoria").val());
	$("#cIdTipoAdjudicaSend").val($("#cIdTipoAdjudica").val());
	$("#nMontoNoFrmt").val(quitaFrmt($("#mObra").val()));
	$("#nMontoConIVANoFrmt").val(quitaFrmt($("#mTotal").val()));
	$("#mImporteIVANoFrmt").val(quitaFrmt($("#mImporteIVA").val()));
	$("#mImporteIVAPlurianualNoFrmt").val(quitaFrmt($("#mImporteIVAPlurianual").val()));
	$("#nPorcAnticipo").val($("#nPorcAnticipo").val() != '' ? $("#nPorcAnticipo").val() : 0);

	if( $("#nPorcAnticipo").val() != '' && parseFloat($("#nPorcAnticipo").val()) > 0.0 )
		$("#iTieneAnticipo").val(1);
	else
		$("#iTieneAnticipo").val(0);

	var pluriAnualStr = ( document.getElementById("cPluriaAnual").checked ? "1" : 0 );
	$("#iEsPluriAnual").val(pluriAnualStr);
	$("#nMontoAnticipo").val(quitaFrmt($("#mAnticipo").val()));
	
	if($("#vcIdContrato").val() != ""){
		queryFormPost({ queryName : "readAllPlurianualidad", async : false, callback : function() {} });
		$("#iEsPluriAnual").val($("#viEsPluriAnual").val());
		$("#nMontoAnticipo").val($("#vTotalAnticipo").val());
		if($("#iEsPluriAnual").val() == "1"){
			iEsPlurianual = 1;
		}
	}
	
	$("#iTieneConveModif").val(0);
	$("#cCveConveModif").val(0);
	$("#porcRetencionSend").val($("#porcRetencion").val());
	$("#noOfAutorizacionSend").val($("#noOfAutorizacion").val());
	$("#idAreaSend").val($("#id_area").val());

	$("#nMontoContrato").val(quitaFrmt($("#nMontoContratoOP").val()));
	
	if(!esSAIFonden){
		if($("#idRealEstate").val()==null || $("#idRealEstate").val()==0){
			alert("Favor de seleccionar un bien inmueble.");
			return false;
		}		
	}else{
		//removeSelectBox(document.getElementById("idRealEstate"));	   	
	   	$("#idRealEstate").append("<option value=" + "1" + ">1</option>");
	}
	
	$("#cIdRealEstate").val($("#idRealEstate option:selected").text());

	if( insertar )
		queryFormPost({
			queryName : "createObraPubicaComEncabezado",
			async : false,
			callback : function() {
				if(iEsPlurianual == 1){
					//Guardar la tabla auxiliar con los montos del contrato.
					queryFormPost("tContratoObraPlurianual_Auxiliar_Create", {async : false});
				}
				queryFormPost({
					queryName : "updatetObraPublicaCompromisoEncabezadoRadicado",
					async : false,
					callback : function() {
						saveDetailComByCrud();
						// RO-0002 IRD retenciones
						guardaDetalleRetenciones();
					}
				});
			}
		});else {
		queryFormPost({
			queryName : "updateObraPubicaComEncabezado",
			async : false,
			callback : function() {
				queryFormPost({
					queryName : "updatetObraPublicaCompromisoEncabezadoRadicado",
					async : false,
					callback : function() {
						saveDetailComByCrud();
					}
				});
			}
		});
		if( "" != $("#cCveContrato").val() ) {
			queryFormPost({
				queryName : "actualizaCveContratoRetencion",
				async : false,
				callback : function() {}
			});
			queryFormPost({
				queryName : "actualizaCveContratoMultianual",
				async : false,
				callback : function() {}
			});

		}
	}
}
// RO-0002 IRD retenciones
function saveDetailComByCrud() {
	var reten = llenaRetenciones(false);
	setTimeout("saveDetailComByCrud2()", 100);
}

function saveDetailComByCrud2() {
	queryFormPost({
		queryName : "readnFolioOPComHeader",
		async : false,
		callback : function() {}
	});

	queryFormPost({
		queryName : "readnRetencionesOP",
		async : false,
		callback : function() {}
	});

	if( $("#reten").val() == 0 ) {
		alert("Aún no se han ingresado las retenciones.");
		$("#btnAmortiza").click();
		return false;
	}

	queryFormPost("readnFolioOPPreComHeader", {
		async : false
	});

	var rowsTbl = $("#dt_clavepresup").dataTable().fnGetData();
	var nDocRenglonVal = 1;
	var insertados = 0;

	if( $("#cEsRadicado").val() == "N" )
		$("#cEvento").val("COMP_OPC");
	else
		$("#cEvento").val("R_COMP_OPC");

	queryFormPost({
		queryName : "deleteAllForIdHeaderObraPubicaComDetalle",
		async : false,
		callback : function() {
			insertados++;
		}
	});

	if( $("#cEsRadicado").val() == "N" )
		$("#cEvento").val("OP_LP");
	else
		$("#cEvento").val("ROP_LP");

	queryFormPost({
		queryName : "creaOPCompromisoDetalleComplemento",
		async : false,
		callback : function() {}
	});

	$("#docRenglon").val("1");

	queryFormPost({
		queryName : "renglonSiguienteCompromisoDetalleRead",
		async : false,
		callback : function() {}
	});
	var conta = parseInt($("#docRenglon").val(), 10);
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var epVal = currRow[ 1 ];
		var k = 3;

		for( var j = 0; j < nombreMeses.length; j++ ) {
			if( quitaFrmt(currRow[ k ]) > 0 ) {
				$("#mImporte").val("0");
				$("#mImporteAnt").val("0");
				$("#mImporteDif").val("0");

				$("#EPSend").val(epVal);
				$("#epFind").val(epVal);

				$("#cMes").val(j + 1);
				$("#cmesFind").val($("#cMes").val());

				$("#mImporte").val(quitaFrmt(currRow[ k ]));

				$("#mImporteDif").val(0);

				if( $("#cEsRadicado").val() == "N" )
					$("#cEvento").val("OP_PCM");
				else
					$("#cEvento").val("ROP_PCM");

				$("#mImporteNegativo").val($("#mImporte").val() * -1);
				$("#nDocRenglon").val(conta);
				queryFormPost({
					queryName : "createObraPubicaComDetalle",
					async : false,
					callback : function() {
						insertados++;
					}
				});
				conta = conta + 1;
			}
			k = k + 3;
		}

	}

	if( showAlertSaveComp ) {
		// alert("¿Ya anexo los documentos escaneados?");
		validaDoctos();

		alert("Los cambios han sido guardados exitosamente");
	}
	showAlertSaveComp = true;
}
function saveHeaderPagoPasivoByCrud() {
	// queryFormPost("readnFolioOPComHeader", {async:false});
	// queryFormPost("readConvenioModificatorio", {async:false});

	// var montoTotalModif = $("#mIncremento").val() == '' ? 0 : parseFloat(
	// quitaFrmt( $("#mIncremento").val()) );
	// $("#mMontoIncrementoIVA").val(montoTotalModif*parseFloat($("#ivaConv").val()));


	// if($("#existeConvenio").val()>0){
	$.ajax({
		url : '../ConvenioModificatorio',
		dataType : 'json',
		type : "POST",
		data : {
			"accion" : "ACTUALIZA_PAGO_PASIVO",
			"mPagoPasivo" : quitaFrmt($("#mPagoPasivo").val()),
			"ivaPagPasF" : $("#ivaPagPasF").val(),
			"mTotalPagoPasivo" : quitaFrmt($("#mTotalPagoPasivo").val()),
			"cMotivoPagoPasivo" : $("#cMotivoPagoPasivo").val(),
			"nFolioOPPagPasHeader" : $("#nFolioOPPagPasHeader").val(),
			"fRecepcion" : $("#fRecepcion").val(),
			"nFolioOPPagPasHeader" : $("#nFolioOPPagPasHeader").val(),
			"cEjercicio" : $("#cEjercicio").val(),
			"folioSAI" : $("#FolioSAI").val()
		},
		async : false,
		success : function(json) {
			var exito = json.success;
			if( exito == "true" ) {
				r = json.data_1.result;
				$("#nFolioOPPagPasHeader").val(r);
				saveDetailPagoPasivoCrud();
			} else {
				alert("ATENCION! No fue posible guardar el pago de pasivo debido al siguiente error:\n" + json.data_1.result +
					"\nIntente nuevamente. Si el problema persiste reportelo al administrador del sistema.x");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});

}

function saveHeaderPlurianualByCrud() {
	$.ajax({
		url : '../ConvenioModificatorio',
		dataType : 'json',
		type : "POST",
		data : {
			"accion" : "ACTUALIZA_PLURIANUAL",
			"mPlurianual" : quitaFrmt($("#mPlurianual").val()),
			"ivaPlurianual" : $("#ivaPlurianual").val(),
			"mTotalPlurianual" : quitaFrmt($("#mTotalPlurianual").val()),
			"cMotivoPlurianual" : $("#cMotivoPlurianual").val(),
			"nFolioOPPlurianualHeader" : $("#nFolioOPPlurianualHeader").val(),
			"fRecepcion" : $("#fRecepcion").val(),
			"nFolioOPPlurianualHeader" : $("#nFolioOPPlurianualHeader").val(),
			"cEjercicio" : $("#cEjercicio").val(),
			"folioSAI" : $("#FolioSAI").val()
		},
		async : false,
		success : function(json) {
			var exito = json.success;
			if( exito == "true" ) {
				r = json.data_1.result;
				$("#nFolioOPPlurianualHeader").val(r);
				saveDetailPlurianualCrud();

			} else {
				alert("ATENCION! No fue posible guardar el plurianual debido al siguiente error:\n" + json.data_1.result +
					"\nIntente nuevamente. Si el problema persiste reportelo al administrador del sistema.x");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
	/*
	 * } else{ queryFormPost({ queryName : "convenioModificatorioCreate", async :
	 * false, callback : function() { queryFormPost("existeDetConvModifEnc",
	 * {async:false}); saveDetailConvByCrud(); } }); }
	 */

}
function saveHeaderConvByCrud() {
	//Validar que no se generen varios registros
	queryFormPost("readnFolioOPComHeader", {
		async : false
	});
	queryFormPost("readConvenioModificatorio", {
		async : false
	});

	var montoTotalModif = $("#mIncremento").val() == '' ? 0 : parseFloat(quitaFrmt($("#mIncremento").val()));
	$("#mMontoIncrementoIVA").val(montoTotalModif * parseFloat($("#ivaConv").val()));

	if( $("#existeConvenio").val() > 0 ) {
		queryFormPost("readContratoConvenioModificatorio", {
			async : false
		});
		//SASV 17/06/2015 Validacion para corroborar que el convenio pertenece a otro contrato.
		if( $("#cContratoConvMod").val() != $("#cCveContrato").val() ) {
			alert("El Convenio " + $("#cNoConvenio").val() + " ya existe en otro contrato");
		} else {
			$.ajax({
				url : '../ConvenioModificatorio',
				dataType : 'json',
				type : "POST",
				data : {
					"accion" : "ACTUALIZA_CM",
					"fInicioConv" : $("#fInicioConv").val(),
					"fFinConv" : $("#fFinConv").val(),
					"mConv" : quitaFrmt($("#mConv").val()),
					"ivaConv" : $("#ivaConv").val(),
					"mTotalConv" : quitaFrmt($("#mTotalConv").val()),
					"cMotivoConv" : $("#cMotivoConv").val(),
					"mIncremento" : quitaFrmt($("#mIncremento").val()),
					"mMontoIncrementoIVA" : quitaFrmt($("#mMontoIncrementoIVA").val()),
					"cNoConvenio" : $("#cNoConvenio").val(),
					"nFolioOPConvHeader" : $("#nFolioOPConvHeader").val()
				},
				async : false,
				success : function(json) {
					var exito = json.success;
					if( exito == "true" ) {
						r = json.data_1.result;
						$("#nFolioOPConvHeader").val(r);
						saveDetailConvByCrud();
					} else {
						alert("ATENCION! No fue posible guardar el convenio modificatorio debido al siguiente error:\n" + json.data_1.result +
							"\nIntente nuevamente. Si el problema persiste reportelo al administrador del sistema.x");
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("Advertencia: " + xhr.responseText + "\nEstatus: "
						+ textStatus + "\n" + errorThrown);
					r = true;
				}
			});
		}
	} else {		
		queryFormPost({
			queryName : "convenioModificatorioCreate",
			async : false,
			callback : function() {
				queryFormPost("existeDetConvModifEnc", {
					async : false
				});
				saveDetailConvByCrud();
			}
		});
		
		
	}

}

function saveDetailConvByCrud() {
	var rowsTbl = $("#dt_claveConvenioModificatorio").dataTable().fnGetData(); //SASV 25/05/2015 CM
	var nDocRenglonVal = 1;
	var insertados = 0;
	// Hacer llamada de borrado al detalle
	queryFormPost("deleteAllForIdHeaderObraPubicaConvDetalle", {
		async : false
	});
	var conta = 1;
	// Termina llamada de borrado al detalle
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var epVal = currRow[ 1 ];
		var k = 3;

		for( var j = 0; j < nombreMeses.length; j++ ) {
			if( quitaFrmt(currRow[ k ]) > 0 ) {
				$("#mImporte").val("0");
				$("#mImporteAnt").val("0");
				$("#mImporteDif").val("0");

				$("#EPSend").val(epVal);
				$("#epFind").val(epVal);
				$("#cMes").val(j + 1);
				$("#cmesFind").val($("#cMes").val());

				$("#mImporte").val(quitaFrmt(currRow[ k ]));
				// queryFormPost("readEPMesCom",{async:false});
				// if( $("#mImporteAnt").val() == '' ||
				// parseFloat($("#mImporteAnt").val()) == 0 )
				$("#mImporteAnt").val("0");

				$("#mImporteDif").val(
					$("#mImporte").val() - $("#mImporteAnt").val());
				if( $("#cEsRadicado").val() == "S" )
					$("#cEvento").val("R_CM");
				else
					$("#cEvento").val("CM");
				$("#mImporteNegativo").val($("#mImporte").val() * -1);
				$("#nDocRenglon").val(conta);
				queryFormPost("createObraPubicaConvDetalle", {
					async : false
				});
				conta++;
			}
			k = k + 3;
		}

	}

	alert("Los cambios han sido guardados exitosamente");
}

function saveDetailPagoPasivoCrud() {
	var rowsTbl = $("#dt_clavePasivo").dataTable().fnGetData();
	var nDocRenglonVal = 1;
	var insertados = 0;
	// Hacer llamada de borrado al detalle
	queryFormPost("deleteAllForIdHeaderObraPubicaPagoPasivoDetalle", {
		async : false
	});
	var conta = 1;
	// Termina llamada de borrado al detalle
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var epVal = currRow[ 1 ];
		var k = 3;

		for( var j = 0; j < nombreMeses.length; j++ ) {
			if( quitaFrmt(currRow[ k ]) > 0 ) {
				$("#mImporte").val("0");
				$("#mImporteAnt").val("0");
				$("#mImporteDif").val("0");

				$("#EPSend").val(epVal);
				$("#epFind").val(epVal);
				$("#cMes").val(j + 1);
				$("#cmesFind").val($("#cMes").val());

				$("#mImporte").val(quitaFrmt(currRow[ k ]));
				// queryFormPost("readEPMesCom",{async:false});
				// if( $("#mImporteAnt").val() == '' ||
				// parseFloat($("#mImporteAnt").val()) == 0 )
				$("#mImporteAnt").val("0");

				$("#mImporteDif").val(
					$("#mImporte").val() - $("#mImporteAnt").val());
				$("#cEvento").val("CM");
				$("#mImporteNegativo").val($("#mImporte").val() * -1);
				$("#nDocRenglon").val(conta);
				queryFormPost("createObraPubicaPagoPasivoDetalle", {
					async : false
				});
				conta++;
			}
			k = k + 3;
		}

	}

	alert("Los cambios han sido guardados exitosamente");
}

function saveDetailPlurianualCrud() {
	var rowsTbl = $("#dt_clavePlurianual").dataTable().fnGetData();
	var nDocRenglonVal = 1;
	var insertados = 0;
	// Hacer llamada de borrado al detalle
	queryFormPost("deleteAllForIdHeaderObraPubicaPlurianualDetalle", {
		async : false
	});
	var conta = 1;
	// Termina llamada de borrado al detalle
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var epVal = currRow[ 1 ];
		var k = 3;
		for( var j = 0; j < nombreMeses.length; j++ ) {
			if( quitaFrmt(currRow[ k ]) > 0 ) {
				$("#mImporte").val("0");
				$("#mImporteAnt").val("0");
				$("#mImporteDif").val("0");

				$("#EPSend").val(epVal);
				$("#epFind").val(epVal);
				$("#cMes").val(j + 1);
				$("#cmesFind").val($("#cMes").val());

				$("#mImporte").val(quitaFrmt(currRow[ k ]));
				// queryFormPost("readEPMesCom",{async:false});
				// if( $("#mImporteAnt").val() == '' ||
				// parseFloat($("#mImporteAnt").val()) == 0 )
				$("#mImporteAnt").val("0");

				$("#mImporteDif").val(
					$("#mImporte").val() - $("#mImporteAnt").val());
				$("#cEvento").val("CM");
				$("#mImporteNegativo").val($("#mImporte").val() * -1);
				$("#nDocRenglon").val(conta);
				queryFormPost("createObraPubicaPlurianualDetalle", {
					async : false
				});
				conta++;
			}
			k = k + 3;
		}
	}

	alert("Los cambios han sido guardados exitosamente");
}

function loadDetail(typeDocument) {
	$("#dt_clavepresup").dataTable().fnClearTable();
	if( typeDocument == "apartado" ) {
		cargaDetalleApartado();
		$("#dv").show();
	} else if( typeDocument == "precompromiso" ) {
		loadDetailPreCom();
	} else if( typeDocument == "compromiso" ) {
		loadDetailCom();
	} else if( typeDocument == "convenio" ) {
		$("#existeDetConvModif").val("");
		queryFormPost(
			{
				queryName : "existeDetConvModifRead",
				async : false,
				callback : function() {
					if( $("#existeDetConvModif").val() != "" ) {
						loadDetailCM();
					} else {
						$("#dt_clavepresup").dataTable().fnClearTable();
					}
				}
			});
	// loadDetailCM();
	} else if( typeDocument == "pagoPasivo" ) { // IRD 20140102 Se agrega consulta a pasivo encabezado
		queryFormPost(
			{
				queryName : "pagoPasivoEncabezadoRead",
				async : false,
				callback : function() {}
			});
		cargaDetallePagoPasivo();
	} else if( typeDocument == "plurianual" ) { // MLR 20140102 Se agrega consulta Plurianual encabezado
		queryFormPost(
			{
				queryName : "plurianualEncabezadoRead",
				async : false,
				callback : function() {
					$("#ivaPlurianual").val("0.16");
				}
			});

		cargaDetallePlurianual();
	}
	clearSelect([ "epSel" ]);
	readClavesPresupuestales();
}

function loadDetailPreCom() {
	var tipo = 0;
	$("#dt_clavepresup").dataTable().fnClearTable();
	$("#dt_clavepresup2").dataTable().fnClearTable();
	$("#dt_claveConvenioModificatorio").dataTable().fnClearTable();

	queryFormPost(
		{
			queryName : "existeDetPrecomRead",
			async : false,
			callback : function() {
				if( $("#existeDetPrecom").val() == 1 )
					tipo = 2;
				else
					tipo = 1;
			}
		});

	queryFormPost({
		queryName : "readPrecompromisoAplicado",
		async : false,
		callback : function() {}
	});

	if( $("#precompromisoAplicado").val() == 'S' ) {
		$("#ddt_clavepresup").css("display", "none");
		$("#ddt_clavepresup2").css("display", "");
	} else {
		$("#ddt_clavepresup").css("display", "");
		$("#ddt_clavepresup2").css("display", "none");
	}

	var campos = tipo + ",'" + $("#FOLIO").val() + "'";

	var elParametro2 = '';
	var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
	$
		.getJSON(
			"../catalogos/SelectJson.jsp",
			{
				Tabla : szTabla,
				Param : elParametro2,
				Campos : campos,
				MaxReg : "",
				ajax : 'false'
			},
			function(data) {
				var rn = 0;

				for( var i = 0; i < data.length; i++ ) {
					var ep = data[ i ].Col0;
					var eneDisp = data[ i ].Col4,
						eneApar = data[ i ].Col3,
						eneDif = eneDisp - eneApar;
					var febDisp = data[ i ].Col6,
						febApar = data[ i ].Col5,
						febDif = febDisp - febApar;
					var marDisp = data[ i ].Col8,
						marApar = data[ i ].Col7,
						marDif = marDisp - marApar;
					var abrDisp = data[ i ].Col10,
						abrApar = data[ i ].Col9,
						abrDif = abrDisp - abrApar;
					var mayDisp = data[ i ].Col12,
						mayApar = data[ i ].Col11,
						mayDif = mayDisp - mayApar;
					var junDisp = data[ i ].Col14,
						junApar = data[ i ].Col13,
						junDif = junDisp - junApar;
					var julDisp = data[ i ].Col16,
						julApar = data[ i ].Col15,
						julDif = julDisp - julApar;
					var agoDisp = data[ i ].Col18,
						agoApar = data[ i ].Col17,
						agoDif = agoDisp - agoApar;
					var sepDisp = data[ i ].Col20,
						sepApar = data[ i ].Col19,
						sepDif = sepDisp - sepApar;
					var octDisp = data[ i ].Col22,
						octApar = data[ i ].Col21,
						octDif = octDisp - octApar;
					var novDisp = data[ i ].Col24,
						novApar = data[ i ].Col23,
						novDif = novDisp - novApar;
					var dicDisp = data[ i ].Col26,
						dicApar = data[ i ].Col25,
						dicDif = dicDisp - dicApar;
					var anuTot = data[ i ].Col1;

					var urlModif = createImgContent("imagenes/edit.png", "editaMovimiento(" + rn + ")", "Editar");
					var urlDel = createImgContent("imagenes/delete.png", "eliminaMovimiento(" + rn + ")", "Eliminar");
					// RO-0002 IRD Solo el administrador puede borrar
					if( $("#tipoUsuario").val() != "ADMIN" ) {
						// urlModif = "";
						urlDel = "";
					}

					var arr = new Array(rn + 1, ep, eneDisp, eneApar,
						eneDif, febDisp, febApar, febDif, marDisp,
						marApar, marDif, abrDisp, abrApar, abrDif,
						mayDisp, mayApar, mayDif, junDisp, junApar,
						junDif, julDisp, julApar, julDif, agoDisp,
						agoApar, agoDif, sepDisp, sepApar, sepDif,
						octDisp, octApar, octDif, novDisp, novApar,
						novDif, dicDisp, dicApar, dicDif, anuTot,
						urlModif, urlDel);
					$("#dt_clavepresup").dataTable().fnAddData(arr);
					$("#dt_clavepresup2").dataTable().fnAddData(arr);
					rn++;

				}
			});

}



function loadDetailCom() {
	var tipo = 0;
	$("#dt_clavepresup").dataTable().fnClearTable();
	$("#dt_clavepresup2").dataTable().fnClearTable();
	$("#dt_claveConvenioModificatorio").dataTable().fnClearTable();

	queryFormPost(
		{
			queryName : "existeDetComRead",
			async : false,
			callback : function() {
				if( $("#existeCom").val() == 1 )
					tipo = 3;
				else
					tipo = 2;
			}
		});

	queryFormPost({
		queryName : "readCompromisoAplicado",
		async : false,
		callback : function() {}
	});

	if( $("#compromisoAplicado").val() == 'S' ) {
		$("#ddt_clavepresup").css("display", "none");
		$("#ddt_clavepresup2").css("display", "");
	} else {
		$("#ddt_clavepresup").css("display", "");
		$("#ddt_clavepresup2").css("display", "none");
	}

	var campos = tipo + ",'" + $("#FOLIO").val() + "'";
	var elParametro2 = '';
	var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
	$("#dt_clavepresup").dataTable().fnClearTable();

	$
		.getJSON(
			"../catalogos/SelectJson.jsp",
			{
				Tabla : szTabla,
				Param : elParametro2,
				Campos : campos,
				MaxReg : "",
				ajax : 'false'
			},
			function(data) {
				var rn = 0;

				for( var i = 0; i < data.length; i++ ) {
					var ep = data[ i ].Col0;
					var eneDisp = data[ i ].Col4,
						eneApar = data[ i ].Col3,
						eneDif = eneDisp
							- eneApar;
					var febDisp = data[ i ].Col6,
						febApar = data[ i ].Col5,
						febDif = febDisp
							- febApar;
					var marDisp = data[ i ].Col8,
						marApar = data[ i ].Col7,
						marDif = marDisp
							- marApar;
					var abrDisp = data[ i ].Col10,
						abrApar = data[ i ].Col9,
						abrDif = abrDisp
							- abrApar;
					var mayDisp = data[ i ].Col12,
						mayApar = data[ i ].Col11,
						mayDif = mayDisp
							- mayApar;
					var junDisp = data[ i ].Col14,
						junApar = data[ i ].Col13,
						junDif = junDisp
							- junApar;
					var julDisp = data[ i ].Col16,
						julApar = data[ i ].Col15,
						julDif = julDisp
							- julApar;
					var agoDisp = data[ i ].Col18,
						agoApar = data[ i ].Col17,
						agoDif = agoDisp
							- agoApar;
					var sepDisp = data[ i ].Col20,
						sepApar = data[ i ].Col19,
						sepDif = sepDisp
							- sepApar;
					var octDisp = data[ i ].Col22,
						octApar = data[ i ].Col21,
						octDif = octDisp
							- octApar;
					var novDisp = data[ i ].Col24,
						novApar = data[ i ].Col23,
						novDif = novDisp
							- novApar;
					var dicDisp = data[ i ].Col26,
						dicApar = data[ i ].Col25,
						dicDif = dicDisp
							- dicApar;
					var anuTot = data[ i ].Col1;


					var urlModif = createImgContent(
						"imagenes/edit.png", "editaMovimiento("
						+ rn + ")", "Editar");
					var urlDel = createImgContent(
						"imagenes/delete.png", "eliminaMovimiento("
						+ rn + ")", "Eliminar");
					// RO-0002 IRD Solo el administrador puede borrar
					if( $("#tipoUsuario").val() != "ADMIN" ) {
						// urlModif = "";
						urlDel = "";
					}

					var arr = new Array(rn + 1, ep, eneDisp, eneApar,
						eneDif, febDisp, febApar, febDif, marDisp,
						marApar, marDif, abrDisp, abrApar, abrDif,
						mayDisp, mayApar, mayDif, junDisp, junApar,
						junDif, julDisp, julApar, julDif, agoDisp,
						agoApar, agoDif, sepDisp, sepApar, sepDif,
						octDisp, octApar, octDif, novDisp, novApar,
						novDif, dicDisp, dicApar, dicDif, anuTot,
						urlModif, urlDel);
					$("#dt_clavepresup").dataTable().fnAddData(arr);
					$("#dt_clavepresup2").dataTable().fnAddData(arr);
					rn++;

				}
			});

}

function actualizaConvModif() {
	queryFormPost("convenioModificatorioUpdate", {
		async : false
	});
}

function insertaConvModif() {
	queryFormPost("convenioModificatorioUpdate", {
		async : false
	});
}
function setBlurMonthCapture() {
	for( var i = 0; i < nombreMeses.length; i++ ) {
		$("#" + nombreMeses[ i ] + "Apart")
			.blur(
				function() {
					if( $(this).val() == "" ) {
						$(this).val(0);
						cambiafrmt(this);
						return;
					}
					var error = false;
					var valMesDisp = parseFloat(quitaFrmt($("#" + this.id.substring(0, this.id.indexOf("Apart")) + "Disp").val()));
					var valMesOriginal = parseFloat(quitaFrmt($("#" + this.id.substring(0, this.id.indexOf("Apart")) + "Hide").val()));
					var valMesApart = parseFloat(quitaFrmt($(this).val()));

					if( valMesApart > ( valMesOriginal + valMesDisp ) ) {
						alert("No se puede capturar en el mes de " + this.id.toString().replace("Apart", "") + " ya que no tiene disponible.");
						error = true;
					} else if( valMesDisp > 0 && valMesApart < 0 ) {
						alert("El importe capturado en el mes de " + this.id.toString().replace("Apart", "") + " debe ser mayor a cero.");
						error = true;
					} else if( valMesDisp > 0 && valMesApart > 0
						&& valMesApart > ( valMesDisp + valMesOriginal ) ) {
						alert("El importe capturado no puede superar el importe disponible en el mes de " + this.id.toString().replace("Apart", "") + ".");
						error = true;
					}
					if( error ) {
						$(this).val(0);
						cambiafrmt(this);
						$(this).focus();
					} else {
						cambiafrmt($("#" + this.id.substring(0, this.id.indexOf("Apart")) + "Disp")[ 0 ]);
					}

				});
	}
}

/**
 * Oculta las columnas de disponible y total.
 */
function ocultaColumnas() {
	var j = 2;
	var k = 4;
	for( var i = 0; i < 12; i++ ) {
		$('#dt_clavepresup').dataTable().fnSetColumnVis(j, false);
		$('#dt_clavepresup').dataTable().fnSetColumnVis(k, false);
		$('#dt_clavepresup2').dataTable().fnSetColumnVis(j, false);
		$('#dt_clavepresup2').dataTable().fnSetColumnVis(k, false);
		$('#dt_clavePasivo').dataTable().fnSetColumnVis(j, false);
		$('#dt_clavePasivo').dataTable().fnSetColumnVis(k, false);
		$('#dt_clavePlurianual').dataTable().fnSetColumnVis(j, false);
		$('#dt_clavePlurianual').dataTable().fnSetColumnVis(k, false);
		$('#dt_claveConvenioModificatorio').dataTable().fnSetColumnVis(j, false);
		$('#dt_claveConvenioModificatorio').dataTable().fnSetColumnVis(k, false);

		j += 3;
		k += 3;
	}
}

function pasaVal(val) {
	$("#pasaValor").val(val);
}

var suspendido = false;
function suspende() {
	if( suspendido ) {
		if( confirm("Se reiniciaran los pagos al contrato. ¿Desea Continuar?") ) {
			alert("Los pagos se reiniciaron exitosamente.");
			suspendido = false;
		} else {
			alert("Los pagos se realizaran de manera normal");
			suspendido = true;
		}
	} else {
		if( confirm("Se suspenderan los pagos al contrato. ¿Desea Continuar?") ) {
			alert("Los pagos se suspendieron exitosamente");
			suspendido = true;
		} else {
			alert("Los pagos se realizaran de manera normal");
			suspendido = false;
		}
	}
}

function validaMontoModificado() {
	var montoTotalAnt = parseFloat($("#MontoTotal").val());
	var anticipoAnosAnteriores = parseFloat(quitaFrmt($("#mAnticipoAnosAnt").val())); //SASV 17/06/2015 se lee el anticipo para casos especiales año 2014 (Solo visualizacion)
	var totalMaximoPorIncrem = ( ( parseFloat(quitaFrmt($("#mObra").val())) + anticipoAnosAnteriores ) * 1.25 );
	if(parseFloat(quitaFrmt($("#nMontoContratoOP").val()))>0 ){//Para plurianuales
		montoTotalAnt = parseFloat(quitaFrmt($("#nMontoContratoOP").val()));
		totalMaximoPorIncrem = ( ( (quitaFrmt($("#nMontoContratoOP").val())) + anticipoAnosAnteriores ) * 1.25 );
	}
	var montoTotalModif = $("#mIncremento").val() == '' ? 0 : parseFloat(quitaFrmt($("#mIncremento").val()));
	var totalPorIncrem = ( montoTotalModif + montoTotalAnt + anticipoAnosAnteriores );
	
	if( montoTotalModif > 0 ) {

		if( totalPorIncrem > totalMaximoPorIncrem ) { //SASV solo se autoriza un incremento del 25% al contrato original
			alert("El monto total del convenio supera el 25% del incremento maximo del contrato")
			$("#mIncremento").val(0);
			$('#dt_clavepresup').dataTable().fnClearTable();
			$("#mImporteIVAConv").val(0);
			$("#mTotalConv").val(0);
			$("#mConv").val(0);
			$("#mImporteIVAConvAnt").val(0);
			$("#mTotalConvAnt").val(0);
			$("#mConvAnt").val(0);
			$("#mTotalConvconAnt").val(0);
			$("#mIncrementoConIVA").val(0);

			$("#mImporteIVAConv").formatCurrency();
			$("#mTotalConv").formatCurrency();
			$("#mConv").formatCurrency();
			$("#mIncrementoConIVA").formatCurrency();
			$("#mImporteIVAConvAnt").formatCurrency();
			$("#mTotalConvAnt").formatCurrency();
			$("#mConvAnt").formatCurrency();
			$("#mTotalConvconAnt").formatCurrency();
			return false;
		}

		$("#mMontoIncrementoIVA").val(montoTotalModif * parseFloat($("#ivaConv").val()));

		var mIncrementoConIVA = montoTotalModif + parseFloat($("#mMontoIncrementoIVA").val());
		$("#mIncrementoConIVA").val(mIncrementoConIVA.toFixed(2));
		$("#mIncrementoConIVA").formatCurrency();

		montoTotalModif = montoTotalAnt + montoTotalModif;

		decimal2 = ( montoTotalModif * $("#ivaConv").val() );
		$("#mImporteIVAConv").val(decimal2.toFixed(2));
		$("#mImporteIVAConv").formatCurrency();

		//SASV Si tiene anticipo años Anteriores (solo para visualizacion)
		$("#mImporteIVAConvAnt").val(( montoTotalModif + anticipoAnosAnteriores ) * $("#ivaConv").val());
		$("#mImporteIVAConvAnt").formatCurrency();

		$("#mConv").val(montoTotalModif);
		$("#mConv").formatCurrency();

		//SASV Si tiene anticipo años Anteriores se suma al total(solo para visualizacion)
		$("#mConvAnt").val(montoTotalModif + anticipoAnosAnteriores);
		$("#mConvAnt").formatCurrency();

		decimal3 = ( parseFloat(montoTotalModif) * ( 1 + parseFloat($(
				"#ivaConv").val()) ) );
		$("#mTotalConv").val(decimal3.toFixed(2));
		$("#mTotalConv").formatCurrency();

		//SASV Si tiene anticipo años Anteriores (solo para visualizacion)
		$("#mTotalConvconAnt").val(parseFloat(montoTotalModif + anticipoAnosAnteriores) * ( 1 + parseFloat($("#ivaConv").val()) ));
		$("#mTotalConvconAnt").formatCurrency();

		return true;
	} else {
		$('#dt_clavepresup').dataTable().fnClearTable();
		$("#mImporteIVAConv").val(0);
		$("#mTotalConv").val(0);
		$("#mConv").val(0);
		$("#mImporteIVAConvAnt").val(0);
		$("#mTotalConvAnt").val(0);
		$("#mConvAnt").val(0);
		$("#mTotalConvconAnt").val(0);
		$("#mIncrementoConIVA").val(0);

		$("#mImporteIVAConv").formatCurrency();
		$("#mTotalConv").formatCurrency();
		$("#mConv").formatCurrency();
		$("#mIncrementoConIVA").formatCurrency();
		$("#mImporteIVAConvAnt").formatCurrency();
		$("#mTotalConvAnt").formatCurrency();
		$("#mConvAnt").formatCurrency();
		$("#mTotalConvconAnt").formatCurrency();
		return true;
	}
}

function validaFechaFinModificado() {
	if( $("#fFinConv").val() != '' ) {
		var fIniCntAnt = toDate($("#fInicioConv").val());
		var fFinCntMod = toDate($("#fFinConv").val());
		if( fFinCntMod < fIniCntAnt ) {
			alert("La nueva fecha de termino debe ser mayor a la fecha inicio del contrato establecida anteriormente");
			$("#fFinConv").val("");
			return false;
		}
		return true;
	} else
		return true;
}

function toDate(strDate) {
	var dateSplt = strDate.split("/");
	var d = parseInt(dateSplt[ 0 ], 10);
	var m = parseInt(dateSplt[ 1 ] - 1, 10);
	var y = parseInt(dateSplt[ 2 ], 10);
	return new Date(y, m, d);
}

function validateNotEmpty(arrIds) {
	for( var idElem in arrIds ) {
		if( $("#" + idElem).val() == '' ) {
			var descEl = arrIds[ idElem ];
			alert("La captura del campo: " + descEl + " es obligatoria");
			return false;
		}
	}
	return true;
}

function verifPasivo() {
	var montoTotalAnt = parseFloat(quitaFrmt($("#MontoTotal").val()));
	var montoTotalModif = $("#mIncremento").val() == '' ? 0 : parseFloat(quitaFrmt($("#mIncremento").val()));

	var fContratoAnt = $("#fFinCntAnt").val();
	var fContratoModif = $("#fFinConv").val();

	if( montoTotalModif > 0 )
		if( !validaMontoTotalCM() )
			return false;

	if( $("#cMotivoPagoPasivo").val() == '' ) {
		alert("Debe ingresar el motivo del Pago de Pasivo");
		return false;
	}


	return true;
}
// RO-0010 MLR
function verifPlurianual() {
	var montoTotalAnt = parseFloat(quitaFrmt($("#MontoTotal").val()));
	var montoTotalModif = $("#mIncremento").val() == '' ? 0 : parseFloat(quitaFrmt($("#mIncremento").val()));

	var fContratoAnt = $("#fFinCntAnt").val();
	var fContratoModif = $("#fFinConv").val();

	if( montoTotalModif > 0 )
		if( !validaMontoTotalCM() )
			return false;

	return true;
}
function hayCambios() {
	var montoTotalAnt = parseFloat(quitaFrmt($("#MontoTotal").val()));
	var montoTotalModif = $("#mIncremento").val() == '' ? 0 : parseFloat(quitaFrmt($("#mIncremento").val()));

	var fContratoAnt = $("#fInicioConv").val();
	var fContratoModif = $("#fFinConv").val();

	if( montoTotalModif > 0 )
		if( !validaMontoTotalCM() )
			return false;

	if( $("#cMotivoConv").val() == '' ) {
		alert("Debe ingresar el motivo del convenio modificatorio");
		return false;
	}
	if( fContratoModif == "" && montoTotalModif == 0 ) {
		alert("No se ha realizado algun cambio respecto al contrato autorizado");
		return false;
	}
	if( fContratoModif != '' && toDate(fContratoModif) < toDate(fContratoAnt) ) {
		alert("La nueva fecha de termino debe ser mayor a la fecha inicio del contrato establecida anteriormente");
		return false;
	}
	return true;
}

function validaMontoTotalCM() {
	if( $('#dt_claveConvenioModificatorio').dataTable().fnGetData().length == 0 ) { //SASV 25/05/2015 CM
		alert("Debe ingresar una estructura Program\u00e1tica con montos a comprometer.");
		return false;
	} else {
		var montoTotal;
		montoTotal = $("#mIncrementoConIVA").val() == '' ? 0 : parseFloat(quitaFrmt($("#mIncrementoConIVA").val()));
		var sumaTotalEP = sumaMontosTabla();

		if( sumaTotalEP.toFixed(2) != montoTotal && ( montoTotal > 0 ) ) {
			alert("La suma de los montos calendarizados por EP debe ser igual al monto modificado");
			return false;
		} else {
			if( sumaTotalEP.toFixed(2) != montoTotal.toFixed(2) ) {
				alert("La suma de los montos calendarizados por EP debe ser igual al monto del contrato");
				return false;
			}
		}
	}
	return true;
}

function validaMontoTotal() {
	if( $('#dt_clavepresup').dataTable().fnGetData().length == 0 ) {
		alert("Debe ingresar una estructura Program\u00e1tica con montos a comprometer.");
		return false;
	} else {
		var montoTotal;
		if( esConvenioModificatorio )
			montoTotal = $("#mTotalConv").val() == '' ? 0 : parseFloat(quitaFrmt($("#mTotalConv").val()));
		else
			montoTotal = $("#mTotal").val() == '' ? 0 : parseFloat(quitaFrmt($("#mTotal").val()));

		var sumaTotalEP = sumaMontosTabla();

		if( esConvenioModificatorio ) {
			if( sumaTotalEP.toFixed(2) != montoTotal && ( montoTotal > 0 ) ) {
				alert("La suma de los montos calendarizados por EP debe ser igual al monto modificado");
				return false;
			}
		} else {
			if( sumaTotalEP.toFixed(2) != montoTotal.toFixed(2) ) {
				alert("La suma de los montos calendarizados por EP debe ser igual al monto del contrato");
				return false;
			}
		}
	}
	return true;
}

/**
 * Suma el total de montos calendarizados.
 * 
 * @returns {Number}
 */
function sumaMontosTabla() {
	var sumaTotalEP = 0.0;
	if( esConvenioModificatorio ) { //SASV 25/05/2015 CM
		var n = $('#dt_claveConvenioModificatorio').dataTable().fnGetData().length;
		for( var i = 0; i < n; i++ ) {
			var arrM = $('#dt_claveConvenioModificatorio').dataTable().fnGetData()[ i ];
			for( var j = 1; j <= 12; j++ ) {
				sumaTotalEP += parseFloat(quitaFrmt(arrM[ j * 3 ]));
			}
		}
	} else {

		var n = $('#dt_clavepresup').dataTable().fnGetData().length;
		for( var i = 0; i < n; i++ ) {
			var arrM = $('#dt_clavepresup').dataTable().fnGetData()[ i ];
			for( var j = 1; j <= 12; j++ ) {
				sumaTotalEP += parseFloat(quitaFrmt(arrM[ j * 3 ]));
			}
		}
	}
	return sumaTotalEP;
}

function loadHeaderCM() {
	if( esConsulta ) {
		queryFormPost({
			queryName : "existeDetConvModifEncConsulta",
			async : false,
			callback : function() {
				if( $("#nFolioOPConvHeader").val() != '' ) {
					queryFormPost("ConvModifAutRead", {
						async : false
					});
				}
			}
		});
	} else {
		$("#nFolioOPConvHeader").val('');
		queryFormPost({
			queryName : "existeDetConvModifEnc",
			async : false,
			callback : function() {
				if( $("#nFolioOPConvHeader").val() != '' ) {
					queryFormPost("ConvModifAutRead", {
						async : false
					});

				}
			}
		});
	}
	//SASV 15/06/2015 Suma de Anticipo Años Anteriores muestre total del contrato
	$("#mAnticipoAnosAnt").val(0);
	queryFormPost("montoAnticipoAnosAnteriores", {
		async : false
	});

	var montoTotalModificado = parseFloat(quitaFrmt($("#MontoTotalIVA").val()));
	var montoOriginalContrato = parseFloat(quitaFrmt($("#mTotal").val()));
	if( $("#mAnticipoAnosAnt").val() > 0 ) { //SASV Visualizar montos modificados para casos especiales de Anticipo años anteriores
		//montoOriginalContrato+parseFloat( quitaFrmt( $("#mAnticipoAnosAnt").val() ) );
		$("#mAnticipoAnosAnt").formatCurrency();
		$("#mAnticipoAnosAntLbl").css("visibility", "visible");
		$("#mAnticipoAnosAnt").css("visibility", "visible");
		$("#mTotalConv").css("visibility", "hidden");
		$("#mConv").css("visibility", "hidden");
		$("#mImporteIVAConv").css("visibility", "hidden");
		$("#mTotalConvconAnt").css("visibility", "visible");
		$("#mConvAnt").css("visibility", "visible");
		$("#mImporteIVAConvAnt").css("visibility", "visible");

	}
	if( montoTotalModificado > montoOriginalContrato ) {
		$("#conveniosAnteriores").val(montoTotalModificado + (parseFloat(quitaFrmt($("#mAnticipoAnosAnt").val()))) * 1.16);
		$("#conveniosAnteriores").formatCurrency();
		$("#conveniosAnterioresLbl").css("visibility", "visible");
		$("#conveniosAnteriores").css("visibility", "visible");

	}

}

function loadDetailCM() {
	var iWhereIsContract = $("#iWhereIsContract").val();
	
	var campos = "4,'" + $("#nFolioOPConvHeader").val() + "'";
	if($("#nFolioOPConvHeader").val()==""){
		campos = "4,'-1'";
	}
	// MLR 13
	// $("#dt_claveConvenioModificatorio").dataTable().fnClearTable();

	var elParametro2 = '';
	var szTabla = "SELECT_OBRA_PUBLICA_DETALLE";
	$("#dt_claveConvenioModificatorio").dataTable().fnClearTable();
	$
		.getJSON(
			"../catalogos/SelectJson.jsp",
			{
				Tabla : szTabla,
				Param : elParametro2,
				Campos : campos,
				MaxReg : "",
				ajax : 'false'
			},
			function(data) {
				var rn = 0;


				//$("#dt_claveConvenioModificatorio").dataTable().fnClearTable();

				for( var i = 0; i < data.length; i++ ) {
					var ep = data[ i ].Col0;
					var eneDisp = data[ i ].Col4,
						eneApar = data[ i ].Col3,
						eneDif = eneDisp
							- eneApar;
					var febDisp = data[ i ].Col6,
						febApar = data[ i ].Col5,
						febDif = febDisp
							- febApar;
					var marDisp = data[ i ].Col8,
						marApar = data[ i ].Col7,
						marDif = marDisp
							- marApar;
					var abrDisp = data[ i ].Col10,
						abrApar = data[ i ].Col9,
						abrDif = abrDisp
							- abrApar;
					var mayDisp = data[ i ].Col12,
						mayApar = data[ i ].Col11,
						mayDif = mayDisp
							- mayApar;
					var junDisp = data[ i ].Col14,
						junApar = data[ i ].Col13,
						junDif = junDisp
							- junApar;
					var julDisp = data[ i ].Col16,
						julApar = data[ i ].Col15,
						julDif = julDisp
							- julApar;
					var agoDisp = data[ i ].Col18,
						agoApar = data[ i ].Col17,
						agoDif = agoDisp
							- agoApar;
					var sepDisp = data[ i ].Col20,
						sepApar = data[ i ].Col19,
						sepDif = sepDisp
							- sepApar;
					var octDisp = data[ i ].Col22,
						octApar = data[ i ].Col21,
						octDif = octDisp
							- octApar;
					var novDisp = data[ i ].Col24,
						novApar = data[ i ].Col23,
						novDif = novDisp
							- novApar;
					var dicDisp = data[ i ].Col26,
						dicApar = data[ i ].Col25,
						dicDif = dicDisp
							- dicApar;
					var anuTot = data[ i ].Col1;

					var urlModif = createImgContent(
						"imagenes/edit.png", "editaMovimiento("
						+ rn + ")", "Editar");
					var urlDel = createImgContent(
						"imagenes/delete.png", "eliminaMovimiento("
						+ rn + ")", "Eliminar");
						// RO-0002 IRD Solo el administrador puede borrar
						// if ($("#tipoUsuario").val() != "ADMIN" ){
						/*
												 * MLR asi se queda urlModif = ""; urlDel = "";
												 */
						// }

					var arr = new Array(rn + 1, ep, eneDisp, eneApar,
						eneDif, febDisp, febApar, febDif, marDisp,
						marApar, marDif, abrDisp, abrApar, abrDif,
						mayDisp, mayApar, mayDif, junDisp, junApar,
						junDif, julDisp, julApar, julDif, agoDisp,
						agoApar, agoDif, sepDisp, sepApar, sepDif,
						octDisp, octApar, octDif, novDisp, novApar,
						novDif, dicDisp, dicApar, dicDif, anuTot,
						urlModif, urlDel);
					
					$("#dt_claveConvenioModificatorio").dataTable().fnAddData(arr);
					rn++;

				}
			});

}

function ocultaBotonesGestion() {
	if( parent.document.getElementById("pb_send") )
		parent.document.getElementById("pb_send").style.visibility = 'hidden';
	if( parent.document.getElementById("pb_cancel") )
		parent.document.getElementById("pb_cancel").style.visibility = 'hidden';
}

function disablePant() {
	if( ( $("#pasaValor").val() == 'apartado' && $("#hAplicaApartado").val() == 'S' ) || ( $("#pasaValor").val() == 'precompromiso' ) ) {
		disableApartado();
		if( $("#hAplicaPrecom").val() != 'S' && $("#pasaValor").val() != 'apartado' ) {
			enablePant();
			if( esPlurianualAniosAnteriores )
				llenarCamposPlurianualidad();
		} else if( $("#pasaValor").val() == 'precompromiso' && $("#hAplicaPrecom").val() == 'S' ) {
			disablePreCompromiso();
		}
	} else if( $("#pasaValor").val() == 'precompromiso' && $("#hAplicaPrecom").val() == 'S' ) {
		disablePreCompromiso();
	} else if( $("#pasaValor").val() == 'compromiso' ) {
		if( $("#hAplicaPrecom").val() == 'S' && $("#hAplicaCom").val() != 'S' ) {
			enableComp();
		} else if( $("#pasaValor").val() == 'compromiso' && $("#hAplicaCom").val() == 'S' ) {
			disableCompromiso();
		}
		if( esPlurianualAniosAnteriores )
			llenarCamposPlurianualidad();
	} else if( $("#pasaValor").val() == 'convenio' ) {
		disableConvenio();
	} else {
		enablePant();
		if( esPlurianualAniosAnteriores )
			llenarCamposPlurianualidad();
	}
}

function enableControls() {
	$("#Link01").show();
	if( $("#hGuardadoPrecompromiso").val() == 'S'
		|| $("#hAplicaApartado").val() == 'S' ) {
		$("#Link02").show();
		document.getElementById("chk_radicado").disabled = true;
	} else {
		$("#Link02").hide();
		if( parseInt($("#dt_clavepresup").dataTable().fnGetData().length, 10) > 0 )
			document.getElementById("chk_radicado").disabled = true;
	}

	if( $("#hGuardadoCompromiso").val() == 'S'
		|| $("#hAplicaPrecom").val() == 'S' )
		$("#Link03").show();
	else
		$("#Link03").hide();
}
function enableConvenio() {
	$(".convenio").each(
		function() {
			document.getElementById($(this).attr("id")).readOnly = false;
			$(this).removeClass("notEditable");
			$(this).show();
			$(this).removeAttr("disabled");
		}
	);
	// $("#ivaPagPasF").val($("#nPorcIVAAplicable").val()*100);
	$("#ddt_clavepresup2").css("display", "none");
	$("#ddt_claveConvenioModificatorio").css("display", ""); //SASV
	$("#dt_claveConvenioModificatorio").css("display", "");
	// $("#ddt_clavePlurianual").css("display", "none");
	$("#noEstimacion2").css("display", "none");

	/*
	 * $("#cCarteraProyec").css("visibility","visible");
	 * $("#cCarteraProyec_2").hide(); $("#cOLI").css("visibility","visible");
	 * $("#cOLI_2").hide();
	 */

	$("#AgregarEP").show();
	$("#AgregarEP").css("visibility", "visible");

}

function enablePagoPasivo() {
	$(".PagoPasivo").each(
		function() {
			document.getElementById($(this).attr("id")).readOnly = false;
			$(this).removeClass("notEditable");
			$(this).show();
			$(this).removeAttr("disabled");
		}
	);
	$("#ivaPagPasF").val($("#nPorcIVAAplicable").val() * 100);
	$("#ddt_clavepresup2").css("display", "none");
	$("#ddt_clavePasivo").css("display", "");
	// $("#ddt_clavePlurianual").css("display", "none");
	$("#noEstimacion2").css("display", "none");

	/*
	 * $("#cCarteraProyec").css("visibility","visible");
	 * $("#cCarteraProyec_2").hide(); $("#cOLI").css("visibility","visible");
	 * $("#cOLI_2").hide();
	 */

	$("#AgregarEP").show();
	$("#AgregarEP").css("visibility", "visible");


}

function enablePlurianual() {
	$(".plurianual").each(
		function() {
			document.getElementById($(this).attr("id")).readOnly = false;
			$(this).removeClass("notEditable");
			$(this).show();
			$(this).removeAttr("disabled");
		}
	);
	$("#ivaPlurianual").val($("#ivaPlurianual").val() * 100);
	$("#ddt_clavepresup2").css("display", "none");
	// $("#ddt_clavePlurianual").css("display", "");
	// $("#dt_clavePlurianual").css("display", "");
	$("#noEstimacion2").css("display", "none");
	// $("#cCarteraProyec").css("visibility","visible");
	// $("#cCarteraProyec_2").hide();
	// $("#cOLI").css("visibility","visible");
	$("#AgregarEP").show();
	$("#AgregarEP").css("visibility", "visible");
	// $("#cOLI_2").hide();

}

function enableEstimacion() {
	$(".estimacion,.fechaEstimacion").each(
		function() {
			document.getElementById($(this).attr("id")).readOnly = false;
			$(this).removeClass("notEditable");
			$(this).show();
			$(this).removeAttr("disabled");
		}
	);
	$('#fperiodoEstimacionFin').removeClass("notEditable");
	$('#fperiodoEstimacionFin').prop('readonly', false);
	$('#fperiodoEstimacionIni').removeClass("notEditable");
	$('#fperiodoEstimacionIni').prop('readonly', false);
}

function disableConvenio() {
	document.getElementById("cCveContrato").readOnly = true;
	$("#cCveContrato").css("backgroundColor", "#CCCCCC");
	$("#cIdTObra").attr("disabled", "disabled");
	$("#cIdTipRec").attr("disabled", "disabled");
	$("#mObra").attr("disabled", "disabled");
	$("#nPorcIVAAplicable").attr("disabled", "disabled");
	$("#cOLI").css("visibility", "hidden");
	$("#cCarteraProyec").css("visibility", "hidden");

	$("#cOLI_2").show();
	$("#cCarteraProyec_2").show();

	$("#cDescripcionContrato").attr("disabled", "disabled");

	$("#AgregarEP").css("visibility", "visible");
	$("#AgregarEP").show();
	$("#btnGuarda").css("visibility", "visible");
	$("#btnAplica").val("Enviar a Ventanilla");
	$("#btnAplica").css("visibility", "visible");
	$("#btnSuspencion").css("visibility", "hidden");
	$("#btnCancela").css("visibility", "visible");
}


function validaFechaFinCompromiso() {
	var aceptarValor = true;
	if( $("#fInicioCom").val() == '' ) {
		alert("Debe capturar primero la fecha de inicio de contrato.");
		aceptarValor = false;
	} else {
		var fInicioCom = toDate($("#fInicioCom").val());
		var fFinCom = toDate($("#fFinCom").val());
		if( fFinCom <= fInicioCom ) {
			alert("La fecha de fin del contrato debe ser mayor a la fecha de inicio");
			aceptarValor = false;
		} else {
			var yFin = fFinCom.getFullYear();
			var yActual = ( new Date() ).getFullYear();

		// if( yActual != yFin ){
		// alert("El contrato no puede terminar en un a\u00F1o diferente al actual");
		// aceptarValor = false;
		// }
		}

	}
	if( !aceptarValor )
		$("#fFinCom").val("");
}
function inhabilitaCapturaMontosPorMes() {
	var habilitaValidacion = $("#habilitaCalendarizacionMesSuperior").val();
	var mesAplicacion = parseInt($("#fRecepcion").val().split("/")[ 1 ], 10);
	var nMesesAdelante = parseInt($("#nMesSuperior").val(), 10);
	if( habilitaValidacion == 'S' ) {
		mesAplicacion = ( mesAplicacion + nMesesAdelante > 11 ? 11 : ( mesAplicacion + nMesesAdelante ) )
		for( i = 0; i < nombreMeses.length; i++ ) {

			if( parseInt(i + 1, 10) >= mesAplicacion || parseInt(i + 1, 10) == 12 ) {
				document.getElementById(nombreMeses[ i ] + "Apart").readOnly = false;
			} else {
				document.getElementById(nombreMeses[ i ] + "Apart").readOnly = true;
				document.getElementById(nombreMeses[ i ] + "Apart").style.backgroundColor = "#CCCCCC";
			}

			cambiafrmt($("#" + nombreMeses[ i ] + "Apart")[ 0 ]);
		}
	}
}
/*
 * function inhabilitaCapturaMontosPorMes() { var habilitaValidacion =
 * $("#habilitaCalendarizacionMesSuperior").val(); var mesAplicacion =
 * parseInt($("#fRecepcion").val().split("/")[1], 10);
 * 
 * if ( habilitaValidacion == 'S' ) for (i = 0; i < nombreMeses.length; i++) {
 * 
 * 
 * if (parseInt(i + 1, 10) > mesAplicacion || parseInt(i + 1, 10) == 12 ) {
 * document.getElementById(nombreMeses[i] + "Apart").readOnly = false; } else {
 * document.getElementById(nombreMeses[i] + "Apart").readOnly = true;
 * document.getElementById(nombreMeses[i] + "Apart").style.backgroundColor =
 * "#CCCCCC"; }
 * 
 * cambiafrmt($("#" + nombreMeses[i] + "Apart")[0]); } }
 */

function validaFechaMayor(idFechaOrg, idFechaComp, msgFechaVacia, msgFechaMenor) {
	var tipoContratacion = $("#cIdTipoAdjudica").val();
	var toCompare = "";
	if( "01" == tipoContratacion )
		toCompare = validaFechasLP[ idFechaComp ];
	else
		toCompare = validaFechasOtro[ idFechaComp ] ? validaFechasOtro[ idFechaComp ] : '';

	if( idFechaComp == '' || toCompare == '' ) {
		return true;
	}
	if( $("#" + toCompare).val() == '' ) {
		alert(msgFechaVacia);
		$("#" + idFechaComp).val("");
		return false;
	} else {

		var fFechaOrg = toDate($("#" + toCompare).val());
		var fFechaComp = toDate($("#" + idFechaComp).val());

		if( fFechaComp < fFechaOrg ) {
			alert(msgFechaMenor);
			$("#" + idFechaComp).val("");
			return false;
		}
	}
	return true;
}

function generaNoContrato() {
	$.ajax({
		url : '../NoContratoGenerador',
		dataType : 'json',
		data : {
			"accion" : "GEN_FOLIO",
			"tRecurso" : $("#cIdTipRec").val(),
			"tAdjudicacion" : genCodigoAdjudicacion()
		},
		async : false,
		success : function(json) {
			r = json.data_1.result;
			$("#cCveContrato").val(r);
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});

}

function updateNoContrato() {
	if( '' != $("#cCveContrato").val() ) {
		var elems = $("#cCveContrato").val().split("-");
		elems[ 5 ] = $("#cIdTipRec").val();
		elems[ 6 ] = genCodigoAdjudicacion();

		var nCont = elems.join("-");
		$("#cCveContrato").val(nCont);
	}
}

function genCodigoAdjudicacion() {
	var idAdj = $("#cIdTipoAdjudica").val();
	if( idAdj == '' )
		return '';
	else if( idAdj == '01' )
		return 'LP';
	else if( idAdj == '08' )
		return 'I3';
	else if( idAdj == '17' )
		return 'CO';
	else if( idAdj == '19' )
		return 'LP';
	else if( idAdj == '23' )
		return 'D';
	else if( idAdj == '24' )
		return 'AD';
	else
		return 'XX';
}

function generaNoConvenio() {
	$.ajax({
		url : '../NoConvenioGenerador',
		dataType : 'json',
		data : {
			"accion" : "GEN_FOLIO_CONV",
			"tRecurso" : $("#cIdTipRec").val(),
			"tAdjudicacion" : genCodigoAdjudicacion()
		},
		async : false,
		success : function(json) {
			r = json.data_1.result;
			$("#cNoConvenio").val(r);
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});

}

function verPlurianual() {
	if( $("#cCveContrato").val() == '' ) {
		alert("Antes de capturar la informaci\u00F3n debe capturar el No. de Contrato.");
		return false;
	}
	cargaInformacionMA();
//	if( esConvenioModificatorio ){
//		$("#btnAcepModalMA").hide();
//	}
	$("#divModals").show();
	myModalMA.show();
}

function eleminaInfoPlurianual() {
	if( confirm("Se eliminar\u00E1 la informaci\u00F3n plurianual \u00BFDesea Continuar? ") )
		deleteInformacionMA();
	else
		$("#cPluriaAnual").attr("checked", true);
}

function calculaPorcAnticipo() {
	var porceIva = parseFloat(quitaFrmt($("#nPorcIVAAplicable").val())).toFixed(2);
	var porceAnt = parseFloat(quitaFrmt($("#nPorcAnticipo").val())).toFixed(2);

	var montoNeto;

	if( document.getElementById("cPluriaAnual").checked|| esPlurianualAniosAnteriores )
		montoNeto = parseFloat(quitaFrmt($("#nMontoContratoOP").val())).toFixed(2);
	else
		montoNeto = parseFloat(quitaFrmt($("#mObra").val())).toFixed(2);

	var montoAnticipoSinIva = ( montoNeto * porceAnt / 100 ).toFixed(2);

	var montoAnticipoConIva = ( parseFloat(montoAnticipoSinIva) + ( montoAnticipoSinIva * porceIva ) ).toFixed(2);

	$("#mAnticipo").val(montoAnticipoConIva);
	cambiafrmt($("#mAnticipo")[ 0 ]);
}
function calculaPorcAnticipoMonto() {
	var montoAnticipoConIva = parseFloat(quitaFrmt($("#mAnticipo").val())).toFixed(2);
	var montoNeto;

	if( document.getElementById("cPluriaAnual").checked)
		montoNeto = parseFloat(quitaFrmt($("#nMontoTContratoOPCiva").val())).toFixed(2);
	else
		montoNeto = parseFloat(quitaFrmt($("#mTotal").val())).toFixed(2);

	var porceAnt = ( ( montoAnticipoConIva * 100 ) / montoNeto ).toFixed(2);	
	$("#nPorcAnticipo").val(porceAnt);
	cambiafrmt($("#mAnticipo")[ 0 ]);
}
function showDialogNoAut() {
	$("#divModals").show();
	myModalDocAutorizacion = new bootstrap.Modal(document.getElementById('dialog-form-DocAutorizacion'), {
	  keyboard: false
	});
	myModalDocAutorizacion.show();
}

function ocultaFolAutorizacionPA() {
	$("#noOfAutorizacion").val("");
	$("#noOfAutAnt").val("");
	$("#lblFolAut").css("visibility", "hidden");
	$("#noOfAutAnt").css("visibility", "hidden");
}

function validaAmortiazacionNecesaria() {
	var nPorceAnticipo = ( $("#nPorcAnticipo").val() == '' ? 0 : parseFloat($("#nPorcAnticipo").val()) );
	if( nPorceAnticipo > 0 ) {
		document.getElementById("porcRetencion").readOnly = false;
		$("#porcRetencion").removeClass("notEditable");
		$("#porcRetencion").addClass("normal");
	} else {
		document.getElementById("porcRetencion").readOnly = true;
		$("#porcRetencion").val("0");
		$("#porcRetencion").removeClass("normal");
		$("#porcRetencion").addClass("notEditable");
	}
}

function readCarterasProimpro() {
	var sinOli = "0";
	if( esPagoPasivo )
		sinOli = "1";
	clearSelect([ "ep" ]);
	$.ajax({
		url : '../proimpro/readInfo',
		dataType : 'json',
		data : {
			"accion" : "READ_CARTERA",
			"sinOli" : sinOli
		},
		async : false,
		success : function(RS) {
			var exito = RS.success;
			if( exito == "true" ) {
				var carteras = RS.data_1;
				clearSelect([ "cCarteraProyec" ]);
				for( var i = 0; i < carteras.length; i++ )
					$("#cCarteraProyec").append('<option value="' + carteras[ i ].cartera + '">' + carteras[ i ].cartera + '</option>');
			} else {
				var msg = RS.data_1.result;
				alert("Ocurrio el siguiente problema al cargar las carteras:\n" + msg);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
}

function readOLIs() {
	clearSelect([ "cOLI" ]);
	if( $("#cCarteraProyec").val() != "-1" ) {
		$.ajax({
			url : '../proimpro/readInfo',
			dataType : 'json',
			data : {
				"accion" : "READ_OLIS",
				"cartera" : $("#cCarteraProyec").val()
			},
			async : false,
			success : function(RS) {
				var exito = RS.success;
				if( exito == "true" ) {
					var olis = RS.data_1;
					for( var i = 0; i < olis.length; i++ )
						$("#cOLI").append('<option value="' + olis[ i ].oli + '">' + olis[ i ].oli + '</option>');
				} else {
					var msg = RS.data_1.result;
					alert("Ocurrio el siguiente problema al cargar las carteras:\n" + msg);
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				r = true;
			}
		});
	}
}

function readClavesPresupuestales() {
	var sinOli = "0";
	if( esPagoPasivo )
		sinOli = "1";
	clearSelect([ "ep" ]);
	if( $("#cOLI").val() != "-1" || esPagoPasivo || parseInt($("#PartidasSinOLI").val(), 10) > 0 ) {
		$.ajax({
			url : '../proimpro/readInfo',
			dataType : 'json',
			data : {
				"accion" : "READ_EPS",
				"cartera" : $("#cCarteraProyec").val(),
				"oli" : $("#cOLI").val(),
				"ue" : $("#cU_UR").val(),
				"sinOli" : sinOli,
				"folioSAI" : $("#FOLIO").val()
			},
			async : false,
			success : function(RS) {
				var exito = RS.success;
				if( exito == "true" ) {
					var eps = RS.data_1;
					clearSelect([ "ep" ]);
					for( var i = 0; i < eps.length; i++ )
						if( eps[ i ].oli != "-1" )
							$("#epSel").append('<option value="' + eps[ i ].oli + '">' + eps[ i ].oli + '</option>');
				} else {
					var msg = RS.data_1.result;
					alert("Ocurrio el siguiente problema al cargar las carteras:\n" + msg);
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				r = true;
			}
		});
	}
}

function existeNoContrato() {
	var existe = false;
	$("#folioExistente").val("");
	queryFormPost({
		queryName : "readExistePrevioContratoOP",
		async : false,
		callback : function() {
			existe = $("#folioExistente").val() != "";
		// alert ("existe " + existe);
		}
	});
	return existe;
}

function inhabilitaModifEP() {
	// $('#dt_clavepresup').dataTable().fnSetColumnVis(39, false);
	$('#dt_clavepresup').dataTable().fnSetColumnVis(40, false);
	$('#dt_clavepresup2').dataTable().fnSetColumnVis(39, false);
	$('#AgregarEP').hide();

	// IRD R0-0002 La cartera y el oli deben ser no edtables
	$("#cCarteraProyec").addClass("notEditable");
	$("#cOLI").addClass("notEditable");
}

function avanceFisico() {
	$("#dt_solicitudPago").dataTable().fnAddData();
}

function creaEstimacion() {
	
	return {
		"FolioSAI" : $("#FolioSAI").val(),
		"cCveContrato" : $("#cCveContrato").val(),
		"fEntregaVentanilla" : $("#fEntregaVentanilla").val(),
		"noEstimacion" : ($("#noEstimacion").val()=="Anticipo"?0:$("#noEstimacion").val()),
		"mMontoEstimacion" : quitaFmt($("#mMontoEstimacion").val()),
		"eFiscalPago" : $("#eFiscalPago").val(),
		"nPorceAvanceFisicoEstimado" : $("#nPorceAvanceFisicoEstimado").val(),
		"nPorceAvanceFisicoEjecutado" : $("#nPorceAvanceFisicoEjecutado").val(),
		"nPorceAvanceFisicoProgramado" : $("#nPorceAvanceFisicoProgramado").val(),
		"mmontoFisicoEjecutado" : quitaFmt($("#mmontoFisicoEjecutado").val()),
		"mmontoFisicoProgramado" : quitaFmt($("#mmontoFisicoProgramado").val()),
		"mesEstimado" : $("#mesEstimado").val(),
		"fperiodoEstimacionIni" : $("#fperiodoEstimacionIni").val(),
		"fperiodoEstimacionFin" : $("#fperiodoEstimacionFin").val(),
		"mMontoEstimacionIva" : quitaFmt($("#mMontoEstimacionIva").val()),
		"mMontoEstimacionMasIva" : quitaFmt($("#mMontoEstimacionMasIva").val()),
		"mMontoEstimacionAmortizado" : quitaFmt($("#mMontoEstimacionAmortizado").val()),
		"mMontoEstimacionRetencion" : quitaFmt($("#mMontoEstimacionRetencion").val()),
		"esCapitalizable" : $("#esCapitalizable").val(),
		"ultimaEstimacion" : $("#ultimaEstimacion").val()
	};

}

function saveAvanceObra() {

	/*VGC.20190103 Antes se guardaba la estimacion por un crud. Se cambia a un ajax que llame al servlet para enterar al sistema de inmuebles de la */
	var estimacion = creaEstimacion();
	$.ajax({
		url : '../EstimacionObra',
		type : 'post',
		dataType : 'json',
		async : false,
		data : estimacion,
		success : function(data) {
			var exito = ( data.success == "true" );
			if( exito ) {

				alert("Avance Fisico Guardado exitosamente");

				$('#dt_solicitudPago').dataTable().fnAddData([ $("#noEstimacion").val()
															, $("#eFiscalPago").val()
															, $("#mMontoEstimacion").val()
															, $("#nPorceAvanceFisicoEstimado").val()
															, $("#nPorceAvanceFisicoEjecutado").val()
															, $("#nPorceAvanceFisicoProgramado").val()
															, $("#mmontoFisicoEjecutado").val()
															, $("#mmontoFisicoProgramado").val()
															, $("#fEntregaVentanilla").val()
															, $("#mesEstimado").val()
															, $("#fperiodoEstimacionIni").val()
															, $("#fperiodoEstimacionFin").val()
															, '', '','','','' ]);

				$("#noEstimacion").val('');
				$("#noEstimacion2").val('');
				$("#mMontoEstimacion").val('');
				$("#nPorceAvanceFisicoEstimado").val('');
				$("#nPorceAvanceFisicoEjecutado").val('');
				$("#nPorceAvanceFisicoProgramado").val('');
				$("#mMontoEstimacionIva").val('');
				$("#mMontoEstimacionMasIva").val('');
				$("#mMontoEstimacionAmortizado").val('');
				$("#mMontoEstimacionRetencion").val('');
				$("#mmontoFisicoEjecutado").val('');
				$("#mmontoFisicoProgramado").val('');
				$("#fEntregaVentanilla").val('');
				$("#mesEstimado").val('');
				$("#fperiodoEstimacionIni").val('');
				$("#fperiodoEstimacionFin").val('');

				muestraTabla();
				document.getElementById("TipoCaptura").selectedIndex = "0";
				tipoCaptura();
			} else {
				alert("No fue posible registrar la informacion debido al siguiente error:\n" + data.data_1.result);
			}
		}
	});
}

function loadInfoSolicitudPagos() {
	// Carga Datos en Tab Estimacion
	$("#NoCntSolicitudPago").val($("#cCveContrato").val());
	$("#beneficiarioSolicitudPagos").val($("#cnombre").val());
	$("#rfcSolicitudPagos").val($("#cIDRFC").val());
}

function validaPeriodoContrato() {
	if( $("#fInicioCom").val() != '' ) {
		queryFormPost({
			queryName : "obraPublicaFechaDeFalloRead",
			async : false,
			callback : function() {
				var fInicioCnt = toDate($("#fInicioCom").val());
				var fFallo = toDate($("#fRecepFallo").val());
				if( fInicioCnt < fFallo ) {
					alert("La fecha de inicio del contrato debe ser mayor a la fecha de fallo.");
					$("#fInicioCom").val('');
				}

			}
		});
	}
}
function validaAcumulado() {
	var acumulado = parseFloat((quitaFrmt($("#acumuladoEstimado").val()))).toFixed(2);
	var totalContrato = 0;
	totalContrato = (parseFloat((quitaFrmt($("#brutoContrato").val())))+parseFloat($("#montoConvSinIVA").val(),10)).toFixed(2);
	
	var estimacionActual = parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2);
	
	var restante = ( totalContrato - acumulado ).toFixed(2);
	var correcto = true;
	if( parseFloat(restante) >= parseFloat(estimacionActual) )
		correcto = false;

	return correcto;
}

function validaAmortizacion() {
	queryFormPost("ExtraeTotalAnticipo", {
		async : false
	});
	var acumulado = parseFloat((quitaFrmt($("#acumuladoAmortizacion").val()))).toFixed(2);
	var actualAmortizacion = parseFloat((quitaFrmt($("#mMontoEstimacionAmortizado").val()))).toFixed(2);
	var anticipo = parseFloat((quitaFrmt($("#totalAnticipo").val()))).toFixed(2);
	var restante = ( anticipo - acumulado - actualAmortizacion ).toFixed(2);
	if( parseFloat(restante) > -.01 && parseFloat(restante) < 0 ) {
		if( confirm("¿Desea ajustar la amortizacion?") ) {
			if( primerAjuste == 0 ) {
				$("#mMontoEstimacionAmortizado").val(0);
				primerAjuste++;
				calculaMontosEstimacion(true);
				return true;
			}
			ajustarAmortizacion("-");
		} else
			return true;
	} else if( parseFloat(restante) > 0 && parseFloat(restante) < .01 ) {
		if( confirm("¿Desea ajustar la amortizacion?") ) {
			if( primerAjuste == 0 ) {
				$("#mMontoEstimacionAmortizado").val(( anticipo - acumulado ).toFixed(2));
				primerAjuste++;
				calculaMontosEstimacion(true);
				return true;
			}
			ajustarAmortizacion("+");
		} else
			return true;
	} else
		return false;
}
function ajustarAmortizacion(signo) {
	if( signo == "-" ) {
		$("#mMontoEstimacion").val(( parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2) ) - 0.01);
	} else {
		$("#mMontoEstimacion").val(( parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2) ) + 0.01);
	}
	calculaMontosEstimacion(false);
	validaAmortizacion();
}

function validaSolicitudPagos() {
	/*
	 * MLR Se elimina validación debido a que se oculta el campo en el
	 * formulario if($('#fEntregaVentanilla').val()==''){ alert("Por favor
	 * ingrese la Fecha Estimada de entrega en ventanilla"); return false; }else
	 */
	queryFormPost("ExtraeAcumuladoEstimacion", {
		async : false
	});
	var acumulado = validaAcumulado();
	var amortizado = validaAmortizacion();
	if( $("#noEstimacion").val() == '' ) {
		alert("Por favor ingrese el N\u00famero de estimaci\u00f3n");
		return false;
	} else if( $("#mMontoEstimacion").val() == '' || (parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2))<=0) {
		alert("Por favor capture el Monto total Estimaci\u00f3n");
		return false;
	} else if( $("#mesEstimado").val() == '0' || $("#mesEstimado").val() == '' ) {
		alert("Por favor capture el mes estimado");
	} else if( $("#fperiodoEstimacionIni").val() == '' ) {
		alert("Por favor capture la fecha inicial de estimacion");
	} else if( $("#fperiodoEstimacionFin").val() == '' ) {
		alert("Por favor capture la fecha final de estimacion");
	} else if( $("#TipoCaptura").val() == '0' ) {
		alert("Debe Seleccionar un tipo de Captura");
	} else if( $("#TipoCaptura").val() == '1' && $("#noEstimacion").val() == '0' ) {
		alert("Debe ingresar un N\u00famero de estimaci\u00f3n diferente");
	} else if( acumulado ) {
		alert("Se supero el monto del contrato");
	} else if( amortizado ) {
		alert("Se ajustan los montos de la amortizacion dar clic sobre Agregar Estimacion");
	} else {
		saveAvanceObra();
	}
}



function tipoCaptura() {
	
	$("#checkbox_ultimaEstim").attr('checked', false);
	$("#ultimaEstimacion").val("false");
	$("#checkbox_obraCapitalizable").attr('checked', false);
	$("#esCapitalizable").val("false");
	document.getElementById("trCapitalizable").style.display = "none";
	queryFormPost("extraeDatosDeEstimaciones", {async : false});
	if( $("#TipoCaptura").val() == '2' ) { //Estimacion
		$("#trUltimaEstimacion").show();
		queryFormPost("ExtraePorcAmort", {
			async : false
		});
		$("#noEstimacion").val('');
		$("#noEstimacion").removeClass("notEditable");
		document.getElementById("noEstimacion").readOnly = false;
		document.getElementById("CapturaEstimacion").style.display = "block";
		$("#mMontoEstimacion").removeClass("notEditable");
		
		document.getElementById("mMontoEstimacion").readOnly = false;
		$("#mMontoEstimacion").val('0');
		$("#trAmort").show();
		$("#trRete").show();
	} else if( $("#TipoCaptura").val() == '1' ) { //Anticipo
		$("#trUltimaEstimacion").hide();
		$("#noEstimacion").val('Anticipo');
		document.getElementById("noEstimacion").className = "form-control notEditable";
		document.getElementById("noEstimacion").readOnly = true;
		document.getElementById("CapturaEstimacion").style.display = "block";
		queryFormPost("ExtraeMontosAnticipo", {
			async : false
		});
		calculaMontosAnticipo();
		document.getElementById("mMontoEstimacion").className = "form-control notEditable";
		document.getElementById("mMontoEstimacion").readOnly = true;
		$("#trAmort").hide();
		$("#trRete").hide();
		document.getElementById("trSub").style.display = "none";

	} else {
		document.getElementById("CapturaEstimacion").style.display = "none";
		document.getElementById("noEstimacion").className = "form-control notEditable";
		document.getElementById("noEstimacion").readOnly = true;
	}	
}

function calculaMontosAnticipo() {
	var mMontoEstimacion=0;
	var mMontoEstimacionIva=0;
	var	mMontoEstimacionMasIva=0;
	
	mMontoEstimacion = parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2);
	mMontoEstimacionIva = mMontoEstimacion * $("#nPorcIVAAplicable").val();
	mMontoEstimacionIva = parseFloat((mMontoEstimacionIva)).toFixed(2)
	$("#mMontoEstimacionIva").val(mMontoEstimacionIva);
	mMontoEstimacionMasIva = parseFloat(mMontoEstimacionIva) + parseFloat(mMontoEstimacion);
	$("#mMontoEstimacionMasIva").val(parseFloat((mMontoEstimacionMasIva)).toFixed(2));
	$("#mMontoEstimacionAmortizado").val('0.00');
	$("#mMontoEstimacionRetencion").val('0.00');
	cambiafrmt($("#mMontoEstimacion")[ 0 ]);
	cambiafrmt($("#mMontoEstimacionIva")[ 0 ]);
	cambiafrmt($("#mMontoEstimacionMasIva")[ 0 ]);
}



function existeEstimacion() {
	var oTableEst = $('#dt_solicitudPago').dataTable();
	var sData = oTableEst.fnGetData();
	var estimacion = $("#noEstimacion").val();

	var existeEstimacion = $("#existeEstimacion").val();
	
	if( existeEstimacion > 0 ) {
		alert("El numero de estimacion " + estimacion + " ya fue agregado");
		muestraTabla();
		$("#existeEstimacion").val('');
	} else {
		validaSolicitudPagos();
	}
}
function muestraTabla() {
	queryFormPost("ExtraeAcumuladoAmortizado", {
		async : false
	});
	cambiafrmt($("#acumuladoAmortizacion")[ 0 ]);
	/*
	 * if (entregaVentanilla != ''){ alert(); }
	 */
	$('#dt_solicitudPago').dataTable().fnClearTable();
	var datos = "sinDatos";
	var szTabla = "TSOLICITUDPAGO";
	var elParametro = "";
	var noContrato = $("#NoCntSolicitudPago").val();
	// R0-0007 MLR
	var camposCondicion = " WHERE avfis.nIdEstatus<>4 and cCveContrato ='" + noContrato + "' order by noestimacion";

	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Campos : camposCondicion,
		Param : elParametro,
		MaxReg : "8",
		ajax : 'true'
	}, function(j) {
		for( var i = 0; i < j.length; i++ ) {
			datos = "datos";
			$("#dt_solicitudPago").dataTable().fnAddData([ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2, j[ i ].Col3, j[ i ].Col4, j[ i ].Col5
			, j[ i ].Col6, j[ i ].Col7, j[ i ].Col8, j[ i ].Col9, j[ i ].Col10, j[ i ].Col11, j[ i ].Col12, j[ i ].Col13, j[ i ].Col14, j[ i ].Col15, j[ i ].Col16 ]);
		}
		if( datos == "sinDatos" ) {
			// alert(" No se encontraron Registros.");
			$('#dt_solicitudPago').dataTable().fnClearTable();
		}
	});
}

// Al agregar Valida que el %Fisico de estimacion acumulado no sea > al 100%
function porcEstimacion() {
	//esSAIAlterno=true;//Quitar o comentar está línea
	if( !facturaCapturada() && !esSAIAlterno ){
		alert("Hace falta cargar la factura global. Para continuar debe cargar la factura por el monto total del Contrato.");
		return false;
	}
	var table = document.getElementById('dt_solicitudPago');
	var aTrs = $('#dt_solicitudPago').dataTable().fnGetData();
	var avFisico = 0.0;
	var avFisicoTotal = $("#nPorceAvanceFisicoEstimado").val();
	var avMontoTotal = 0;
	// IRD 20131025 Se cambia validación al monto del contrato mas los convenios
	// modificatorios

	var claveContrato = $("#cCveContrato").val();
	// var sumaCargos = 0;
	if( claveContrato.indexOf("-PAS") > -1 ) {
		var rowsTbl = $("#dt_clavePasivo").dataTable().fnGetData();
		avMontoTotal = parseFloat(quitaFmt($("#mTotal").val()));
		for( var i = 0; i < rowsTbl.length; i++ ) {
			avMontoTotal += parseFloat(rowsTbl[ i ][ 38 ]);
		}
	} else {
		if( $("#conveniosAnteriores").val() == "" ) // No tiene convenios
			avMontoTotal = parseFloat(quitaFmt($("#mTotal").val()));
		else
			avMontoTotal = parseFloat(quitaFmt($("#conveniosAnteriores").val()));
	}
	avMontoTotal = Math.round(avMontoTotal * 100);
	for( var i = 0; i < aTrs.length; i++ ) {
		if( claveContrato.indexOf("-PAS") > -1 ) {
			if( aTrs[ i ][ 1 ] == '2015' ) {
				if( aTrs[ i ][ 0 ] != $("#noEstimacion").val() ) {
					avFisico += parseFloat(aTrs[ i ][ 3 ]);
					avMontoTotal -= Math.round(parseFloat(quitaFmt(aTrs[ i ][ 2 ].toString())) * 100) ;
				}
			}
		} else {
			avFisico += parseFloat(aTrs[ i ][ 3 ]);
			avMontoTotal -= Math.round(parseFloat(quitaFmt(aTrs[ i ][ 2 ].toString())) * 100);

		}
	}

	// suma el %total mas el % que se quiere agregar
	porcFisicoTotal = avFisico + parseFloat(avFisicoTotal);
	avMontoTotal -= Math.round(parseFloat(quitaFmt($("#mMontoEstimacion").val().toString())) * 100) ;

	/*
	 * IRD 20131008 Se quita validación por instrucciones de Eleuterio
	 * if(parseFloat(porcFisicoTotal) == parseFloat(100)){ if(avMontoTotal > 0){
	 * alert("El total de las estimaciones debe cubrir el total del contrato.");
	 * porcFisicoTotal = 0; return; } }
	 */

	if( !validaFechasEstimacion() )
		return;
		/*
		 * if(porcFisicoTotal > 100){ alert("El porcentaje Fisico total no debe ser
		 * mayor a 100"); return; }
		 */ else {
		/*
		 * if(avMontoTotal == 0 && porcFisicoTotal != parseFloat(100)){
		 * alert("El monto total del contrato esta cubierto pero el porcentaje
		 * de Avance Fisico no."); return; } if(avMontoTotal < 0){ //
		 * alert(avMontoTotal); alert("La suma de las estimaciones rebasa el
		 * monto total del contrato. " + avMontoTotal/100); return; }
		 */
		queryFormPost("existeEstimacionRead", {
			async : false
		});
		existeEstimacion();
	}

}

// Al actualizar Valida que el %Fisico de estimacion acumulado no sea > al 100%
function actEstimacion() {
	var acumulado = validaAcumulado();
	validaAmortizacion();
	var table = document.getElementById('dt_solicitudPago');
	var aTrs = $('#dt_solicitudPago').dataTable().fnGetData();
	var avFisico = 0.0;
	var avFisicoTotal = $("#nPorceAvanceFisicoEstimado").val();
	var avMontoTotal = 0.00;
	var claveContrato = $("#cCveContrato").val();
	if( !validaFechasEstimacion() )
		return;

	for( var i = 0; i < aTrs.length; i++ ) {
		if( $("#noEstimacion2").val().toUpperCase() != aTrs[ i ][ 0 ].toUpperCase() ) {
			if( claveContrato.indexOf("-PAS") > -1 ) {
				if( aTrs[ i ][ 1 ] == '2015' ) {
					if( aTrs[ i ][ 0 ] != $("#noEstimacion2").val() ) {
						alert(aTrs[ i ][ 0 ]);
						avFisico += parseFloat(aTrs[ i ][ 3 ]);
						avMontoTotal += parseFloat(quitaFmt(aTrs[ i ][ 2 ].toString()));
					}
				}
			} else {
				avFisico += parseFloat(aTrs[ i ][ 3 ]);
				avMontoTotal += parseFloat(quitaFmt(aTrs[ i ][ 2 ].toString()));

			}

		}
	}

	// suma el %total mas el % que se quiere agregar
	porcFisicoTotal = avFisico + parseFloat(avFisicoTotal);
	avMontoTotal += parseFloat(quitaFmt($("#mMontoEstimacion").val().toString()));
	//SASV el monto Total Cambia si hay covenios Por lo que la validacion se hace sobre el monto incluyendo los convenios (si existieran);
	var montoTotal;
	if( $("#conveniosAnteriores").val() == "" ) {
		montoTotal = parseFloat(quitaFmt($("#mTotal").val()));
	} else {
		montoTotal = parseFloat(quitaFmt($("#conveniosAnteriores").val()));
	}
	//alert(avMontoTotal +"-"+montoTotal+"-"+$("#conveniosAnteriores").val());
	/*if(avMontoTotal > montoTotal){
	 //alert ("avMontoTotal " + avMontoTotal+ " mTotal " +$("#mTotal").val() +
	 //$("#conveniosAnteriores").val());
		alert("La suma de las estimaciones no debe superar el monto total del contrato.");
		porcFisicoTotal = 0;
		return;
	}
	/*
	 * if(porcFisicoTotal > 100){ alert("El porcentaje Fisico total no debe ser
	 * mayor a 100"); porcFisicoTotal = 0; return; }
	 */

	//else{
	queryFormPost("ActualizaEstimacionUpdate", {
		async : false
	});
	modificacionEstimacion = false;
	alert("Los registros se han actualizado con Exito");
	$("#noEstimacionDtTable").val('');
	$("#btnAddEstimacion").show();
	$("#btnModEstimacion").hide();
	$("#noEstimacion").show();
	$("#noEstimacion2").hide();
	// $("#contenedorNoEstimacion").html("");
	// $("#contenedorNoEstimacion").html("<input type='text'
	// id='noEstimacion' name='noEstimacion' size='14' maxlength='40'
	// onkeypress='Validaciones(this,17)' class='estimacion'
	// readonly='readonly'/>");
	limpiaCamposEstimacion();
	document.getElementById("TipoCaptura").selectedIndex = "0";
	tipoCaptura();
	document.getElementById("TipoCaptura").disabled = false;
	muestraTabla();
//}
}

function limpiaCamposEstimacion() {
	$("#noEstimacion").val('');
	$("#noEstimacion2").val('');
	$("#mMontoEstimacion").val('');
	$("#nPorceAvanceFisicoEstimado").val('');
	$("#nPorceAvanceFisicoEjecutado").val('');
	$("#nPorceAvanceFisicoProgramado").val('');
	$("#mesEstimado").val('0');
	$("#fperiodoEstimacionIni").val('');
	$("#fperiodoEstimacionFin").val('');
	$("#mmontoFisicoEjecutado").val('');
	$("#mmontoFisicoProgramado").val('');
	$("#fEntregaVentanilla").val('');
}


function deshabilitaCamposConsulta() {
	$(":button").each(function() {
		if( $(this).attr("id") != "btnAmortiza" )
			$(this).hide();
	});

	$(":text").each(
		function() {
			$(this).removeAttr('disabled');
			$(this).attr('readOnly', 'readOnly');
			$(this).addClass("notEditable");
		}
	);

	$("select").each(
		function() {
			$(this).attr('disabled', 'disabled');
		}
	);

	$("textarea").each(
		function() {
			$(this).removeAttr('disabled');
			$(this).attr('readOnly', 'readOnly');
			$(this).addClass("notEditable");
		}
	);
	$('#dt_clavepresup').dataTable().fnSetColumnVis(39, false);
	$('#Link05').show();
	$('#Link06').show();
	// R0-0007 mlr
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(3, false); //SASV 11/05/2015 Se ocultan los % de avances por solicitud
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(4, false);
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(5, false);
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(6, false);
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(7, false);
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(8, false);
	$('#dt_solicitudPago').dataTable().fnSetColumnVis(15, false);

	if( $("#tipoUsuario").val() == "ADMIN" ) {
	} else {
		$('#dt_solicitudPago').dataTable().fnSetColumnVis(12, false);
		$('#dt_solicitudPago').dataTable().fnSetColumnVis(13, false);
	}

}

function cargaAreas() {
	clearSelect([ "id_area" ]);
	if( $("#cU_UE").val() != '' )
		querySelectPost("obraPublicaCatalogoAreas", "id_area", {
			async : false
		});


}
// Se comento por que Sergio Cruz dijo que debe funcionar igual que todos los
// modulos, dejar atrapado el contrato si no se libera
/*
 * window.onbeforeunload = function(){ //if( confirm("¿Esta seguro que desea
 * abandora esta pagina sin cerrarla?") ) $.ajax({ url: '../gstnmngr/gestion',
 * dataType : 'json', type :"POST", data : { "cmd" : "22", "redirect": "false" },
 * async : false, success : function(json) { }, error : function(xhr,
 * textStatus, errorThrown) { } });
 *  }
 */

function validaDoctos() {
	// if( confirm("¿Ya anexo los documentos escaneados?") )


	queryFormPost({
		queryName : "validaDoctosOP",
		async : false,
		callback : function() {}
	});
	if( $("#numPaginas").val() < 2 )
		alert("Aun no ha agregado los documentos escaneados");

}
// Humberto
function fueCapturadaEP() {
	var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes();
	// alert("aTrs.length : "+aTrs.length);
	for( var i = aTrs.length - 1; i >= 0; i-- ) {
		var aData = $('#dt_clavepresup').dataTable().fnGetData(aTrs[ i ]);
		if( aData[ 1 ] == $("#epDisp").val() ) {
			return true;
		}

	}
	return false;
}
function agregaCarteraOli() {
	var aTrs = $('#dt_carteraOli').dataTable().fnGetNodes();
	// alert("aTrs.length : "+aTrs.length);
	var existe = false;
	for( var i = aTrs.length - 1; i >= 0; i-- ) {
		var aData = $('#dt_carteraOli').dataTable().fnGetData(aTrs[ i ]);
		if( aData[ 0 ] == $("#cCarteraProyec").val() & aData[ 1 ] == $("#cOLI").val() ) {
			existe = true;
		}
	}
	// se agrega nuevo registro
	if( existe == false ) {
		var arr = new Array($("#cCarteraProyec").val(), $("#cOLI").val());
		$("#dt_carteraOli").dataTable().fnAddData(arr);
	}
}
var tablaCarteraOli = $('#dt_carteraOli').dataTable({
	"sScrollX" : "500px",
	"bScrollCollapse" : true,
	"bPaginate" : false,
	"bLengthChange" : false,
	"bFilter" : false,
	"bSort" : false,
	"bInfo" : false,
	"bAutoWidth" : false,
	"bJQueryUI" : true,
	"bRetrive" : true,
	"bDestroy" : true
});

$("#dt_carteraOli").attr('visible', false);

function obtineTotalEpsCapturadas() {
	var rowsTbl = $("#dt_clavepresup").dataTable().fnGetData();
	var total = 0;
	var importe = 0;
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var k = 3;
		if( filaModificada != i ) {
			for( var j = 0; j < nombreMeses.length; j++ ) {
				if( quitaFrmt(currRow[ k ]) > 0 ) {
					importe = quitaFrmt(currRow[ k ]);
					total = parseFloat(total) + parseFloat(importe);
				}
				k = k + 3;
			}
		}
	}
	total = parseFloat(total) + parseFloat(quitaFrmt($("#totalCalendarizado").val()));
	return total;
}
function obtineTotalEpsCapturadas2() {
	var rowsTbl = $("#dt_clavepresup").dataTable().fnGetData();
	var total = 0;
	var importe = 0;
	for( var i = 0; i < rowsTbl.length; i++ ) {
		var currRow = rowsTbl[ i ];
		var k = 3;
		if( filaModificada != i ) {
			for( var j = 0; j < nombreMeses.length; j++ ) {
				if( quitaFrmt(currRow[ k ]) > 0 ) {
					importe = quitaFrmt(currRow[ k ]);
					total = parseFloat(total) + parseFloat(importe);
				}
				k = k + 3;
			}
		}
	}
	return total;
}
function comparaEpsCapturadas() {
	var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes();
	var subPartidaEPAnterior = "";
	var subPartidaEPDespues = "";
	var res = 0;
	for( var i = 0; i < aTrs.length; i++ ) {
		var aData = $('#dt_clavepresup').dataTable().fnGetData(aTrs[ i ]);
		subPartidaEPDespues = aData[ 1 ].substring(31, 36);

		if( subPartidaEPAnterior != subPartidaEPDespues & i > 0 ) {
			res++;
		// alert(subPartidaEPAnterior);
		}
		subPartidaEPAnterior = subPartidaEPDespues;


	}
	return res;
}

function llenaFundamentoLegal() {
	clearSelect("cIdFundamentoLegal");
	$("#cIdTipoAdjudicaSend").val($("#cIdTipoAdjudica").val())
	querySelectPost("readCatalogoOPFundamentoLegal", "cIdFundamentoLegal", {
		async : false
	});
}

function cargaPlurianuales() {
	OpenDialogPlurianuales();
}
function OpenDialogPlurianuales() {
	cargaPluri();
	setTimeout('tablaPluObra.fnAdjustColumnSizing()', 350);
	$("#divModals").show();
	setTimeout('myModalPluObr.show()', 200);
}

/**
 * Funcion para Cargar Datos a la tabla
 */
function cargaPluri() {
	var sOrder = "";
	var param = "";
	var zTabla = "R_CARGA_PLUOBRA";
	var groupFilter = "";
	var camposWhere = " WHERE vcIdContrato NOT IN (SELECT cidcontrato FROM pContratoObra WITH (NOLOCK)) order by vcIdContrato ";
	tablaPluObra.fnClearTable();
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : zTabla,
		Campos : camposWhere,
		Param : param,
		Order : sOrder,
		MaxReg : "5",
		ajax : "true"
	}, function(j) {
		oSettings = tablaPluObra.fnSettings();
		for( var i = 0; i < j.length; i++ ) {
			tablaPluObra.fnAddData([ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2,
				j[ i ].Col3, j[ i ].Col4, j[ i ].Col5, j[ i ].Col6, j[ i ].Col7, j[ i ].Col8, j[ i ].Col9,
				j[ i ].Col10, j[ i ].Col11, j[ i ].Col12, j[ i ].Col13 ]);
		}
		if($('#tblPluObra >tbody >tr').length>0){
			//tablaPluObra.fnAdjustColumnSizing();
			oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
        	tablaPluObra.fnDraw();
		}
	});
}

function doubleClickPluObra(event) {
	var aPos = tablaPluObra.fnGetPosition(event.target.parentNode);
	var aData = tablaPluObra.fnGetData(aPos);
	var vcIdContrato = aData[ 0 ];
	//var cDescripcion = aData[1];
	$("#vcIdContrato").val(vcIdContrato);
	//$("#vcDescripcion").val(cDescripcion);
	myModalPluObr.hide();
	$("#divModals").hide();
	llenarCamposPlurianualidad();
}
function llenarCamposPlurianualidad() {
	enabledSelect();
	esPlurianualAniosAnteriores=true;
	$("#filaMultianual").attr("style", "display:inline");
	queryFormPost("readAllPlurianualidad", {
		async : false
	});

	$("#cDescripcionContrato").val($("#vcDescripcion").val());
	modoSoloLectura("cDescripcionContrato");
	$("#cU_UE").val($("#vcU_UE").val());
	modoSoloLectura("cU_UE");
	cargaAreas();
	$("#id_area").val($("#vID_AREA").val());
	modoSoloLectura("id_area");
	$("#cIdTObra").val($("#vcTipoObra").val());
	modoSoloLectura("cIdTObra");
	$("#cIdTipRec").val($("#vcTipoRecurso").val());
	modoSoloLectura("cIdTipRec");
	$("#nMontoContratoOP").val($("#vmImporte").val());
	var montototalcontrato = $("#nMontoContratoOP").val();
	cambiafrmt($("#nMontoContratoOP")[ 0 ]);
	$("#nMontoContratoOPiva").val($("#vmIVA").val());
	cambiafrmt($("#nMontoContratoOPiva")[ 0 ]);
	$("#nMontoTContratoOPCiva").val($("#vmTotal").val());
	cambiafrmt($("#nMontoTContratoOPCiva")[ 0 ]);
	var pagadoAñoAnterior = $("#vPagadoEjerAnte").val();
	//$("#mObra").val(montototalcontrato - pagadoAñoAnterior);
	$("#mObra").val($("#vnMonto").val());
	onchangemObra();
	onBlurMoney();
	modoSoloLectura("mObra");
	
	$("#cCveContrato").val($("#vcIdContrato").val());
	modoSoloLectura("cCveContrato");
	$("#cIdTipoAdjudica").val($("#vcTipoAdjudica").val());
	modoSoloLectura("cIdTipoAdjudica");
	$("#cIdFundamentoLegal").val($("#vcArticulo").val());
	modoSoloLectura("cIdFundamentoLegal");
	$("#cIdConvocatoria").val($("#vcConvocatoria").val());
	modoSoloLectura("cIdConvocatoria");
	$("#cIdConvocatoria").removeClass("calendar_2");
	$("#cIdConvocatoria").removeClass("calendar_1");
	$("#fConvocatoria").val($("#vfFechaConvoca").val());
	modoSoloLectura("fConvocatoria");
	$("#fAclaracion").val($("#vfFechaJuntAcla").val());
	modoSoloLectura("fAclaracion");
	$("#fRecepProp").val($("#vfFechaReceProp").val());
	modoSoloLectura("fRecepProp");
	$("#fRecepFallo").val($("#vfFechaFallo").val());
	modoSoloLectura("fRecepFallo");
	
	$("#fAdjudicacion").val($("#vfAdjudicacion").val());
	modoSoloLectura("fAdjudicacion");
	
	$("#cIDRFC").val($("#vcIdRFC").val());
	modoSoloLectura("cIDRFC");
	document.getElementById('btnShowProveedores').style.display = 'none';
	$("#cnombre").val($("#vcBeneficiario").val());
	modoSoloLectura("cnombre");
	$("#cIdTipoContratoObra").val($("#vcTipoContrato").val());
	modoSoloLectura("cIdTipoContratoObra");
	$("#nofianza").val($("#vnoFianza").val());
	modoSoloLectura("nofianza");
	$("#nofianzaOcultos").val($("#vnoFianzaVO").val());
	modoSoloLectura("nofianzaOcultos");
	$("#nofianzaAnticipo").val($("#vnoFianzaAnt").val());
	modoSoloLectura("nofianzaAnticipo");
	//document.getElementById('lbl_plu').style.display = 'none';
	document.getElementById('cPluriaAnual').style.display = 'none';
	document.getElementById('verPlu').style.display = 'none';
	document.getElementById('labelPluri').style.display = 'none';
	
	$("#nPorcAnticipo").val($("#vnPorceAnticipo").val());
	modoSoloLectura("nPorcAnticipo");
	calculaPorcAnticipo();
	modoSoloLectura("mAnticipo");
	$("#cIdAdicionales").val($("#vcTipoAdicional").val());
	modoSoloLectura("cIdAdicionales");
	$("#entidadobra").val($("#vcIdEntidadFederativa").val());
	modoSoloLectura("entidadobra");
	$("#noOfAdicionales").val($("#vcOficio").val());
	modoSoloLectura("noOfAdicionales")
	$("#fAdicionales").val($("#vfFechaOficio").val());
	modoSoloLectura("fAdicionales");
	$("#fInicioCom").val($("#vfFechaIniContr").val());
	modoSoloLectura("fInicioCom");
	$("#fFinCom").val($("#vfFechaFinContr").val());
	modoSoloLectura("fFinCom");

	modoSoloLectura("porcRetencion");
	if($("#esFonden").val() == 1){
		$("#chk_esFonden").attr("checked", true);
	}else{
		$("#divCheckFonden").hide();
	}
	modoSoloLectura("cNoProcedimientoCNET");
	modoSoloLectura("nCodContratoCNET");
	modoSoloLectura("nCodExpedienteCNET");
	llamadaWS();
	disabledSelects();

}
function disabledSelects() {
	document.getElementById("cU_UE").disabled = true;
	document.getElementById("id_area").disabled = true;
	document.getElementById("cIdTipoAdjudica").disabled = true;
	document.getElementById("cIdFundamentoLegal").disabled = true;
	document.getElementById("cIdTObra").disabled = true;
	document.getElementById("cIdTipRec").disabled = true;
	document.getElementById("cIdTipoContratoObra").disabled = true;
	document.getElementById("cIdAdicionales").disabled = true;
	document.getElementById("entidadobra").disabled = true;
	document.getElementById("idRealEstate").disabled = true;
	document.getElementById("nPorcIVAAplicable").disabled = true;
	
}
function enabledSelect() {
	document.getElementById("cU_UE").disabled = false;
	document.getElementById("id_area").disabled = false;
	document.getElementById("cIdTipoAdjudica").disabled = false;
	document.getElementById("cIdFundamentoLegal").disabled = false;
	document.getElementById("cIdTObra").disabled = false;
	document.getElementById("cIdTipRec").disabled = false;
	document.getElementById("cIdTipoContratoObra").disabled = false;
	document.getElementById("cIdAdicionales").disabled = false;
	document.getElementById("entidadobra").disabled = false;
	document.getElementById("idRealEstate").disabled = false;
	document.getElementById("nPorcIVAAplicable").disabled = false;
}
function modoSoloLectura(campo) {
	document.getElementById(campo).className = "form-control notEditable";
	document.getElementById(campo).readOnly = true;
	if("mObra"==campo){
		document.getElementById(campo).className = "form-control notEditable numerico";
	}
	
}
function recaulculaMontos() {
	$("#mImporteIVA").val(Math.round(( parseFloat($("#mObra").val()) * parseFloat($("#nPorcIVAAplicable").val()) ) * 100) / 100);
	$("#mTotal").val(Math.round(( parseFloat($("#mObra").val()) * ( 1 + parseFloat($("#nPorcIVAAplicable").val()) ) ) * 100) / 100);

}
function onchangemObra() {
	$("#mImporteBruto").val($("#mObra").val());
	Sinfrmt($("#mImporteBruto")[ 0 ]);
	onchangenPorcIVAAplicable();
	calculaPorcAnticipo();
}

function onchangenPorcIVAAplicable() {
	if( $("#mImporteBruto").val() > 0 ) {

		decimal2 = ( $("#mImporteBruto").val() * $(
			"#nPorcIVAAplicable").val() );
		$("#mImporteIVAPlurianual").val(decimal2.toFixed(2));
		$("#mImporteIVAPlurianual").formatCurrency();

		decimal3 = ( parseFloat($("#mImporteBruto").val()) * ( 1 + parseFloat($(
				"#nPorcIVAAplicable").val()) ) );
		$("#mImporteIVA").val(decimal2.toFixed(2));
		$("#mImporteIVA").formatCurrency();

		$("#mTotal").val(decimal3.toFixed(2));
		$("#mTotal").formatCurrency();
	} else {
		$("#mImporteIVAPlurianual").val(0);
		$("#mTotal").val(0);

		$("#mImporteIVAPlurianual").formatCurrency();
		$("#mTotal").formatCurrency();
	}
	$("mImporteCapEPs").val($("#mTotal").val());

	$("#ivaConv").val($("#nPorcIVAAplicable").val());
	$("#ivaConvF").val($("#nPorcIVAAplicable").val() * 100);
	$("#ivaPlurianual").val($("#ivaPlurianual").val() * 100);
}
function llamadaWS() {
	if ( $("#chk_esFonden").is(':checked')) {		
		llenaComboFONDEN("-2", "idRealEstate", "FONDEN");
	} else {
		var data0 = llenaObject();
		$.blockUI({
			message : "Espere por favor se est\u00e1 consultando un web service..........",
			timeout : 50000
		});
		$.ajax({
			url : "../obrapublica/WSObraPublicaEstadosServlet",
			type : 'post',
			async : false,
			data : data0,
			dataType : 'json',
			success : function(j) {
				$("#idRealEstate").empty();
				if( "TRUE" == j[ 0 ].estatus ) {
					llenaCombo(j[ 0 ].estados, "idRealEstate");
					$("#idRealEstate").val($("#idRealEstateAux").val());
				} else {
					$("#idRealEstate").val(-1);
					alert(j[ 0 ].msg);
				}
				$.unblockUI();
			}
		});
	}
}
function llenaCombo(data, name) {
	var $select = $('#' + name);
	for( var i = 0; i < data.length; i++ ) {
		var obj = data[ i ];
		$select.append($('<option />', {
			value : obj.id,
			text : obj.Descripcion
		}));
	}
}
function llenaComboFONDEN(id, name, Descripcion) {
	$('#' +  name).find('option').remove();
	$('#' +  name).append( '<option value="' + id + '">' + Descripcion + '</option>' );
}

function llenaObject() {
	var data0 = {
		nIdEstado : $("#entidadobra").val()
	};
	return data0;
}
function esUltimaEstimacion(){
	$("#checkbox_obraCapitalizable").attr('checked', false);
	$("#esCapitalizable").val("false");
	if($("#checkbox_ultimaEstim").is(':checked')){
		$("#ultimaEstimacion").val("true");
		$("#trCapitalizable").show();
		estimateRemainder();
	}else{
		$("#ultimaEstimacion").val("false");
		$("#trCapitalizable").hide();
	}
}
function esObraCapitalizable(){
	if($("#checkbox_obraCapitalizable").is(':checked')){
		$("#esCapitalizable").val("true");
	}else{
		$("#esCapitalizable").val("false");
	}
}

function habilitaFONDEN(){
	if ($("#chk_esFonden").is(':checked')) {
		$("#trDescripcionFONDEN").show();
		$("#esFonden").val("1");
		llenaComboFONDEN("-2", "idRealEstate", "FONDEN");	 	
	 }else{
		$('#idRealEstate').find('option').remove();
		$("#trDescripcionFONDEN").hide();
		$("#esFonden").val("0");
		llamadaWS();
	}
}
function showCatProveedores(){
	$("#divModals").show();
	myModalProv = new bootstrap.Modal(document.getElementById('modalProveedores'), {
	  keyboard: false
	});
	myModalProv.show();
	clearParamsCapProv();
	queryCatProveedores();
}
function clearParamsCapProv(){
	$("#cRazonSocialCat").val('');
	$("#cIdRFCCat").val('');
}
function queryCatProveedores(){
	var cadProveedor="''";
	var funcion="fn_mCatalogoProveedor";
	var cadRFC="''";
	if($("#cRazonSocialCat").val()!=""){
		cadProveedor="'"+$("#cRazonSocialCat").val()+"'";
	}
	if($("#cIdRFCCat").val()!=""){
		cadRFC="'"+$("#cIdRFCCat").val()+"'";
	}
	$('#tblConsultaProvedores').dataTable().fnClearTable();
	oTableConsultaProv = $("#tblConsultaProvedores").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay partidas de contrato, favor de seleccionar un contrato SAI",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
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
		bServerSide: true,
		bProcessing: true,
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (cadRFC+","+cadProveedor) +")" ) ,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cIdRFCSinGuiones" },
			{ sName: "cRazonSocial" }
			
		]
		,fnInitComplete: function() {
			if($('#tblConsultaProvedores >tbody >tr').length>0){
				oTableConsultaProv.fnAdjustColumnSizing();
			}
		}
	});
}
function guardaMA(){
	var porceIva = parseFloat(quitaFrmt($("#nPorcIVAAplicable").val())).toFixed(2);
	var montoPlurianual = parseFloat((quitaFrmt($("#totalMontoMultiAnual").val()))).toFixed(2);

	$("#nMontoContratoOP").val(montoPlurianual); //sin iva
	cambiafrmt($("#nMontoContratoOP")[ 0 ]);

	var montoplurianualiva = parseFloat(montoPlurianual * porceIva).toFixed(2);
	$("#nMontoContratoOPiva").val(montoplurianualiva); //iva
	cambiafrmt($("#nMontoContratoOPiva")[ 0 ]);

	var montoplurianualcIva = parseFloat(parseFloat(montoPlurianual) + parseFloat(montoplurianualiva));
	$("#nMontoTContratoOPCiva").val(montoplurianualcIva); //montoconiva
	cambiafrmt($("#nMontoTContratoOPCiva")[ 0 ]);
	//myModalMA.hide();
	//$("#divModals").hide();
}
function closedMA(){
	var tot = $("#totalMultiAnual").val();
	guardaMA();
	if( parseInt(tot, 10) == 0 || tot == '' ) {
		$("#iEsPluriAnual").val(0);
		$("#cPluriaAnual").attr("checked", false);
		$("#filaMultianual").attr("style", "display:none");
	} else {
		$("#iEsPluriAnual").val(1);
		$("#cPluriaAnual").attr("checked", true);
		$("#filaMultianual").attr("style", "display:inline");
	}
	queryFormPost("updateOPPlurianual", {
		async : false
	});
	myModalMA.hide();
	$("#divModals").hide();
}
function sendDocAutorizacion(){
	if( $("#noOfAutorizacion").val() == '' && parseFloat($("#nPorcAnticipo").val()) > 30 )
		alert("No podra avanzar el tramite a ventanilla hasta capturar el No. de Autorizaci\u00F3n");else {
		alert("Por favor adjunte el documento de Autorizaci\u00F3n de Porcentaje");
	}
	$("#noOfAutAnt").val($("#noOfAutorizacion").val());
	$("#lblFolAut").css("visibility", "visible");
	$("#noOfAutAnt").css("visibility", "visible");
	calculaPorcAnticipo();
	$("#myModalDocAutorizacion").hide();
	$("#divModals").hide();
}
function cancelaDocAutorizacion(){
	calculaPorcAnticipo();
	$("#myModalDocAutorizacion").hide();
	$("#divModals").hide();
}
function addEP(){
	var existeEP = fueCapturadaEP();
	if( existeEP == true & $("#esEditado").val() == 0 ) {
		alert("La EP ya está capturada");
	} else {
		var agregado = agregaMovimientos();
		// agregaCarteraOli();
		if( agregado == true ) {
			$("#esEditado").val(0);
			myModalEP.hide();
			$("#divModals").hide();
		} 
	}
}
function cancelEP(){
	myModalEP.hide();
	$("#divModals").hide();
	if( modificando )
		modificando = !modificando;
}
function sumaTotalCalendarizado() {
	$("#totalCalendarizado").val("0");
	$(".montoCaptura").each(function() {
		var val = parseFloat(quitaFrmt($(this).val()) == '' ? 0 : quitaFrmt($(this).val()));
		var sum = parseFloat(quitaFrmt($("#totalCalendarizado").val()));
		$("#totalCalendarizado").val(sum + val);
		cambiafrmt($("#totalCalendarizado")[ 0 ]);
	});
}
function saveDocAmortizacion(){
	if( $("#cCveContrato").val() == '' ) {
		alert("Antes de guardarla la informaci\u00F3n debe capturar el No. de Contrato");
		return false;
	}
	var saveFirst = false;
	if( $("#nFolioOPComHeader").val() == -1 )
		queryFormPost({
			queryName : "readnFolioOPComHeader",
			async : false,
			callback : function() {
				saveFirst = $("#nFolioOPComHeader").val() == -1;
			}
		});

	if( saveFirst ) {
		saveHeaderComByCrud(true);
	// saveDetailComByCrud();
	}
	guardaPorceRetencion();
	guardaDetalleRetenciones();
	alert("Se guardo la información exitosamente");
	myModalDocAmortizacion.hide();
	$("#divModals").hide();
}
function cancelDocAmortizacion(){
	myModalDocAmortizacion.hide();
	$("#divModals").hide();
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