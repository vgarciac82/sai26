var es_mx = {
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
};

var oTable;
var oTableDetalle;
var cxpPagado;
var tipoPagado;
var renglonEdit = 0;

/**
 * Crea los componentes e inicializa valores.
 */
function init(idOper) {
	if( idOper == 1 ) {
		createDT();
		createDTDetalle();
		creaDialogoSeleccion();
		creaDialogoMontos();
		$("#SeleccionDialog").dialog("open");
		$("#fAplicacion").val(fAplicacion);
		$("#nFolioPagoPenasConv").val(nFolioPagoPenasConv);
		$("#FOLIO").val(folio);
		$("#aEjercicioFiscal").val(ejercicioFiscal);
		$("#cCentroContable").val(CC);
		$("#cUnidadResponsable").val(UR);
		$("#U_LOGIN").val(nombreUsuario);
		$("#Imprimir").button();
		
				
	} else if( idOper == 2 ) {
		
		$("#nFolioPagoPenasConv").val(nFolioPagoPenasConv);
		queryFormPost("ConsultaPagoPenas", {async:false} );
		creaDialogoMontos();
		$("#generaLayout").button().click(function() {
			generaLayout();
		});
		
		createDTDetalleConsulta("nFolioPagoPenasConv=" + nFolioPagoPenasConv);
		
		$("#btnDiv").css("display", "block");

	}

	$("#SeleccionDialog").dialog({
		autoOpen : false,
		height : 400,
		width : 700,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				aceptarDlg();
			},
			"Cancelar" : function() {}
		},
		close : function() {},
		open : function() {
			cxpPagado = '';
			tipoPagado = '';
		}
	});
	
		
	$("#dialog-firmantes").dialog({
		autoOpen : false,
		height : 400,
		width : 480,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				if( $("#cNombreVoBo").val() == "" ) {
					alert("Falta Ingresar Nombre en Datos Vº Bº"); return;
				} else if( $("#cPaternoVoBo").val() == "" ) {
					alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return;
				} else if( $("#cMaternoVoBo").val() == "" ) {
					alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return;
				} else if( $("#cPuestoVoBo").val() == "" ) {
					alert("Falta Ingresar Puesto en Datos Vº Bº"); return;
				}

				if( $("#cNombreAut").val() == "" ) {
					alert("Falta Ingresar Nombre en Datos Autorizar"); return;
				} else if( $("#cPaternoAut").val() == "" ) {
					alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return;
				} else if( $("#cMaternoAut").val() == "" ) {
					alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return;
				} else if( $("#cPuestoAut").val() == "" ) {
					alert("Falta Ingresar Puesto en Datos Autorizar"); return;
				}

				$("#cNombreVo").val($("#cNombreVoBo").val());
				$("#cPaternoVo").val($("#cPaternoVoBo").val());
				$("#cMaternoVo").val($("#cMaternoVoBo").val());
				$("#cPuestoVo").val($("#cPuestoVoBo").val());

				$("#cNombreA").val($("#cNombreAut").val());
				$("#cPaternoA").val($("#cPaternoAut").val());
				$("#cMaternoA").val($("#cMaternoAut").val());
				$("#cPuestoA").val($("#cPuestoAut").val());

				$("#firmanteVoBo").val($("#cNombreVoBo").val() + " " + $("#cPaternoVoBo").val() + " " + $("#cMaternoVoBo").val());
				$("#firmanteAut").val($("#cNombreAut").val() + " " + $("#cPaternoAut").val() + " " + $("#cMaternoAut").val());

				var msn = "No Se Guardo Correctamente Informacion de Firmantes";

				queryFormPost("tPagoPenasFirmante_Update", {
					async : false
				});
				$("#dialog-firmantes").dialog("close");
				cmdImprimir('PolizaPenas');

				if( procesar() ) {
					//parent.document.getElementById("pb_save").disabled = false;	
					parent.document.getElementById("pb_send").style.visibility = 'visible';
					parent.document.getElementById("pb_send").disabled = false;
				}
			},
			"Cancelar" : function() {
				parent.document.getElementById("pb_save").disabled = false;
				$(this).dialog("close");
			}
		},
		close : function() {
			parent.document.getElementById("pb_save").disabled = false;
		}
	});
}

/**
 * Crea DataTable para seleccionar los pagos con penalizaciones pendientes de
 * pagar
 */
function createDT() {
	oTable = $('#dtSelPagos').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 270,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[ 1 ]
				+ "/crud?rt=t&ql=vPenalizacionPendiente",
			aoColumns : [ {
				sName : "caNoContrarrecibo"
			}, {
				sName : "cTipoPago"
			}, {
				sName : "nFolioPago"
			}, {
				sName : "cIdRFC"
			}, {
				sName : "mImportePenalizacion"
			}, {
				sName : "mRemanentePenalizacion"
			} ],
			oLanguage : es_mx
		});

	$("#dtSelPagos tbody").click(function(event) {

		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});

		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTable.fnGetPosition(event.target.parentNode);
		var aData = oTable.fnGetData(aPos);
		cxpPagado = aData[ 0 ];
		tipoPagado = aData[ 1 ];
	});
}

function createDTDetalle(condicion) {
	if( !condicion )
		condicion = "1=2";

	oTableDetalle = $('#dtDetallePenas').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 270,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[ 1 ]
				+ "/crud?rt=t&ql=vPenalizacionRemanente&qw="
				+ condicion,
			aoColumns : [ {
				sName : "cMes"
			}, {
				sName : "EP"
			}, {
				sName : "mImportePenalizacion"
			}, {
				sName : "remanentePenalizacion"
			}, {
				sName : "caNoContrarrecibo"				
			} ],
			oLanguage : es_mx
		});
}

function createDTDetalleConsulta(condicion) {
	
	oTableDetalle = $('#dtDetallePenas').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 270,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[ 1 ]
				+ "/crud?rt=t&ql=vPagoPenasDet&qw="
				+ condicion,
			aoColumns : [ {
				sName : "cMes"
			}, {
				sName : "EP"
			}, {
				sName : "mImportePenalizacion"
			}, {
				sName : "remanentePenalizacion"
			}, {
				sName : "mImporteAComprobar"
			}, {
				sName : "caNoContrarrecibo"	
			}],
			oLanguage : es_mx
		});
}

/**
 * Crea dialogo de seleccion
 */
function creaDialogoSeleccion() {
	$("#SeleccionDialog").dialog({
		autoOpen : false,
		height : 400,
		width : 700,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				aceptarDlg();
			},
			"Cancelar" : function() {}
		},
		close : function() {},
		open : function() {
			cxpPagado = '';
			tipoPagado = '';
		}
	});
}

/**
 * Crea dialog para la captura de montos.
 */
function creaDialogoMontos() {
	$("#CambiaValorDialog").dialog({
		autoOpen : false,
		height : 220,
		width : 320,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				cambiaMontos();
			},
			"Cancelar" : function() {
				$("#CambiaValorDialog").dialog("close");
			}
		},
		open : function() {
			$("#importeMes").val("0.00");
		}
	});
}
/**
 * Funcion que responde al envento "Aceptar" del dialogo de seleccion de pagos.
 */
function aceptarDlg() {
	if( cxpPagado == '' ) {
		alert("Debe seleccionar una solicitud de pago.");
	} else {

		$("#cxpbusqueda").val(cxpPagado);
		$("#tipoPagado").val(tipoPagado);

		var conceptoPago = "Pago de pena convencional de la solicitud de pago "
			+ cxpPagado;
		$("#cConcepto").val(conceptoPago);

		$("#SeleccionDialog").dialog("close");
		$.blockUI({
			message : "Cargando informacion. Por favor espere ......"
		});
		queryFormPost({
			queryName : "penasConvencionalesRead",
			async : false,
			callback : function() {
				$.unblockUI();
				createDTDetalle("cTipoPago='" + tipoPagado
					+ "' AND caNoContrarrecibo='" + cxpPagado + "'");
			}
		});

	}
}

/**
 * Funcion que cambia los montos en la tabla.
 */
function cambiaMontos() {
	// TODO realizar validaciones de montos.
	$('#dtDetallePenas').dataTable().fnUpdate($("#importeMes").val(),
		renglonEdit, 4);
}
/**
 * Funcion del Workflow. Se llama en el inicio al terminar de cargar el XML
 * 
 * @param idOper
 *            Operacion actual en el flujo
 */
function onLoadPlantilla(idOper) {
	init(idOper);
}

function onSubmit(idOper) {
	if( idOper == 1 ) {
		var p = window.parent;
		var resultado = false;

		p.gestion.setFolio($("#FOLIO").val());
		p.gestion.setOperador(operador);
		p.gestion.setFechaDocumento($("#fAplicacion").val());
		p.gestion.setEjercicioFiscal($("#aEjercicioFiscal").val());
		p.gestion.setMoneda("MXP");

		if( $("#caNoContrarrecibo").val() == "" )
			generaCxP();

		var correcto = false;
		queryFormPost({
			queryName : "tPagoPenasConvEncabezadoCreate",
			async : false,
			callback : function() {
				generaInputs();
				queryFormPost({
					queryName : "tPagoPenasConvDetalleCreate",
					async : false,
					callback : function() {
						actualizaEnviadoSicop();
						alert("Datos guardados exitosamente");
						tipoFirmantes();
					}
				});
				correcto = true;
			}
		});
		return correcto;
	}
}

function ResponsableSiguiente(idOper) {
	if( idOper == 1 )
		return "CONSULTA_PAGOPENASCONV";
}

function OperacionSiguiente(idOper) {
	if( idOper == 1 )
		return "consulta_pagopenasconv";
}

function onPostDisplay(id_oper) {
	parent.document.getElementById("pb_send").disabled = false;
	parent.document.getElementById("pb_save").disabled = true;
	return true;
}

function generaCxP() {
	getNextSequenceVal({
		seqName : "CRPENAS-" + $("#cCentroContable").val(),
		async : false,
		callback : function setSequenceVal(seqValue) {
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = "1" + seqValue.substr(seqValue.length - 5);
			seqValue = $("#cCentroContable").val() + "PC"
				+ $("#aEjercicioFiscal").val() + seqValue;
			$("#caNoContrarrecibo").val(seqValue);

		}
	});

}

function borraElementos() {
	$('.remove').remove();
}

function createInput(form, name, value) {
	$('<input>').attr({
		type : 'hidden',
		name : name,
		value : value
	}).addClass('remove').appendTo('#' + form);
}

function generaInputs() {
	borraElementos();

	var arrData = $("#dtDetallePenas").dataTable().fnGetData();
	for( var i = 0; i < arrData.length; i++ ) {

		var EP = arrData[ i ][ 1 ];
		var CXP = arrData[ i ][ 4 ];
		var cEvento = ObtenerEventoEPRow(EP, CXP);

		createInput('mainFrm', 'nDocRenglon', i + 1);
		createInput('mainFrm', 'cMes', arrData[ i ][ 0 ]);
		//createInput('mainFrm', 'cEvento', 'PENACONV');
		createInput('mainFrm', 'cEvento', cEvento);
		createInput('mainFrm', 'EP', arrData[ i ][ 1 ]);
		createInput('mainFrm', 'importePena', arrData[ i ][ 2 ].replace(/,/g, ''));
		createInput('mainFrm', 'RFC', "TESOFE");
		createInput('mainFrm', 'mImporteMasIva', arrData[ i ][ 3 ].replace(/,/g, ''));
		createInput('mainFrm', 'OBGT', arrData[ i ][ 1 ].substring(31, 36));
	}

	return true;
}

function onPostSubmit(idOper) {
	$.blockUI();
	return true;
}
function tipoFirmantes() {
	$("#dialog-firmantes").dialog("open");
	queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {
		async : false
	}); // voBo - autoriza
}

function actualizaEnviadoSicop() {
	queryFormPost("nEnviadoSicopPagoPenasUpdate", {
		async : false
	});
}

function ObtenerEventoEPRow(EP, CXP) {
	var cEventoEP = "";

	$("#cEventoEPRow").val("");
	$("#CXPRow").val(CXP);
	$("#EPRow").val(EP);

	queryFormPost("obtenerEventoEPRowPenas_Read", {
		async : false
	});
	cEventoEP = $("#cEventoEPRow").val();

	return cEventoEP;
}