/**
 * Utilidades para el resumen de poliza manual
 */

var es_MX = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtrado de _MAX_ registros)",
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

/**
 * Inicializa los componentes de la pantalla
 */
function init(iniciaFirmantes) {
	/*Crea botones*/
	$("#autorizarBtn").button().click(function() {
		autorizaPoliza();
	});
	$("#rechazarBtn").button().click(function() {
		rechazaPoliza();
	});

	/*Carga Informacion inicial y crea DT*/
	queryFormPost("resumenPolizaManualRead", {
		async : false
	});

	queryFormPost("empleadoElaboraPMRead", {
		async : false
	});

	creaDTMovimientos();

	if (true == iniciaFirmantes) {
		if (!esConsulta) {

			muestraEditaFirmantes();
			$(".firmaElectronica").each(function() {
				$(this).show();
			});

		} else {
			var esFIEL = esFirmaElectronica();
			if (esFIEL) {
				muestraResumenFirmas();
			} else {
				$("#imprimirBtn").button().click(function() {
					imprimirPoliza();
				});
				$("#actualizarFirmantesBtn").button().click(function() {
					if( actualizaFirmantes() )
						imprimirPoliza();
				});
				muestraEditaFirmantes();
				$("#operacionesConsultaDiv").show();

			}

		}
	}
	return;
}

/**
 * Crea la tabla de movimientos y obtiene el contenido
 */
function creaDTMovimientos() {
	oTableCuen = $("#pCuentas").dataTable({
		oLanguage : es_MX,
		bProcessing : true,
		bAutoWidth : true,
		bRetrive : true,
		bDestroy : true,
		bPaginate : true,
		sScrollX : "100%",
		bLengthChange : true,
		bInfo : true,
		bFilter : true,
		bSort : true,
		sAjaxSource : "../export/GeneraJsonTxt",
		"fnServerParams" : function(aoData) {
			aoData.push({
				"name" : "more_data",
				"value" : $("#nFolioDocPoliza").val()
			});
		},
		left : true,
		sPaginationType : "full_numbers",
		bJQueryUI : true,
		"aLengthMenu" : [
			[ 25, 50, 100, 200, -1 ],
			[ 25, 50, 100, 200, "Todo" ]
		],
		"iDisplayLength" : 25,
		"aoColumnDefs" : [
			{
				"bVisible" : false,
				"aTargets" : [ 15 ]
			}
		]
	});
}

/**
 * Callback de llamado al dar click en el boton Autorizar.
 * Se valida que los firmantes esten capturados, actualiza la informacion de viaticos de ser necesario y aplica contablemente. Si todo es exitoso avanza el tramite a consulta
 * 
 */
function autorizaPoliza() {
	try {
		if (confirm("Esta operacion autorizara la afectacion contable de la poliza mostrada.\nEsta seguro que desea continuar?")) {
			var guardado = actualizaFirmantes();
			if (guardado) {
				if ($("#fComprobacion").val() != "0") {
					$("#fComprobacion").removeAttr("disabled");
					queryFormPost("actualizaViaticos,insertaDetalleViaticos", {
						async : false
					});
				}

				if (aplicaPoliza()) {
					alert("La poliza se aplico correctamente.");
					imprimirPoliza();
					avanzaConsulta();
				}
			}

		}
	} catch (ex) {
		alert("Error 0006js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
	}
}

/**
 * Aplica contablemente la poliza sea para afectar o cancelar la afectacion.
 */
function aplicaPoliza(polizaCancel) {
	var exito = false;
	try {
		var tipo = "aplicarMotorPoliza";
		var nFolioDocPoliza = $("#folioDocumento").val();
		var campo = "nFolioDocPoliza";
		var tablaEnc = "tDocPolizaEncabezado";
		var tablaDet = "tDocPolizaDetalle";
		var tipoAplicar = "DOCPOLIZA";
		var autorizadoPorFiel = ($("#firmaElectronica").prop("checked") == true);
		var empleadoCaptura = $("#empleadoCaptura").val();
		var empleadoVoBo = $("#nombreVoBo").val();
		var empleadoAutoriza = $("#nombreAut").val();

		if (polizaCancel) {
			campo = "nFolioDocPolizaCancel";
			tablaEnc = "tDocPolizaCancelEncabezado";
			tablaDet = "tDocPolizaCancelDetalle";
			tipoAplicar = "DOCPOLIZACANCEL";
		}

		$.ajax({
			url : '../Generador/cierrePresupuestal.jsp',
			type : 'post',
			dataType : 'json',
			async : false,
			data : {
				"tipo" : tipo,
				"nFolioDocPoliza" : nFolioDocPoliza,
				"campo" : campo,
				"tablaEnc" : tablaEnc,
				"tablaDet" : tablaDet,
				"tipoAplicar" : tipoAplicar,
				"autorizadoPorFiel" : autorizadoPorFiel,
				"firmaElectronica" : autorizadoPorFiel,
				"empleadoCaptura" : empleadoCaptura,
				"empleadoVoBo" : empleadoVoBo,
				"nombreVoBo" : empleadoVoBo,
				"nombreAut" : empleadoAutoriza,
				"empleadoAutoriza" : empleadoAutoriza
			},
			success : function(data) {

				if (data.sinSesion == 'sinSesion') {
					alert("No fue posible autorizar debido a que la sesion ha terminado.");
					location.href = "../index.jsp";
				}

				if (data.estatus == "guardado") {
					exito = true;

				} else {
					alert("El Documento No Se Aplicó: " + data.estatus) ;
				}
			}
		});

	} catch (ex) {
		alert("Error 0026js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
	}

	return exito;
}

/**
 * 
 */
function rechazaPoliza() {
	$("#tOperacion").val("Regresando de autorizacion a revision");
	queryFormPost("tDocPolizaBitacora", {
		async : false
	});

	$("#pb_save", parent.window.document).click();
	regresaRevision();
}

/**
 * Funcion callabck de la firma electronica al rechazar el tramite.
 * 
 * Envia la cancelacion contable el tramite si es rechazado por algun firmante.
 */
function rechazaPago() {
	var tipo = "aplicarMotorPolizaCancelacion";
	var cFolioDocumento = $("#folioSAI").val();
	var Fecha = $("#fAplicacion").val();
	var Usuario = $("#usuario").val();
	var mesAbierto = $("#mesAbierto").val();
	var mes = $("#mesAbierto").val() - 1;
	var mesOrigen = parseInt($("#fAplicacion").val().split("-")[1], 10);
	var meses = [ "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" ];

	var msg = "Est\u00e1 a punto de cancelar el pago con folio: " + $("#nFolioPago").val();

	if (confirm(msg)) {
		try {
			$("#operacionesDiv").hide();
			$.blockUI();
			$.ajax({
				url : '../Generador/cierrePresupuestal.jsp',
				type : 'post',
				dataType : 'json',
				async : true,
				data : {
					tipo : tipo,
					cFolioDocumento : cFolioDocumento,
					Fecha : Fecha,
					Usuario : Usuario,
					mesAbierto : mesAbierto,
					autorizadoPorFiel : true
				},
				success : function(data) {

					if (data.sinSesion == 'sinSesion') {
						alert("Su sesion termino.")
						location.href = "../index.jsp";
					}
					if (data.estatus == "guardado") {
						done = true;
						queryFormPost("PolizaCancelacionContabilidad", {
							async : false
						});

						alert("Documento cancelado correctamente");

						$.unblockUI();
						ejecutaConsulta();
					} else {
						alert("No se pudo Cancelar el documento.\r Reintente en un momento." + data.estatus) ;
					}

					$.unblockUI();
				},
				error : function(xhr, ajaxOptions, thrownError) {
					alert(xhr.status);
					alert(thrownError);
					$.unblockUI();
				}
			});

		} catch (ex) {
			alert("Error 0003js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
		}
	}
}

function aceptarDlg() {
	var msgValidaciones = validaCamposCompletos();
	if ("" == msgValidaciones) {
		var msg = "Esta a punto de aceptar el proceso de los pagos con folio: " + $("#nFolios").val();
		if (confirm(msg)) {
			$("#urlRetorno").val("plantillasCasos/ResumenPolizaManualFIEL.jsp")
			$("#tipoPagoSeleccionado").val($("#cTipoPago").val());
			$("#autorizaLayouts").submit();
			$("#dlg-FIEL").dialog("close");
			$.blockUI({
				message : "<h1>Espere ...</h1>"
			});
		}
	} else {
		alert(msgValidaciones);
	}
}

function regresar() {
	location.href = "../Generador/AutorizaGeneracionLayouts.jsp?TYPE=" + tipoAutorizacion;
}


function imprimirPoliza() {
	queryFormPost("folioPoliza_DocPolRead", {
		async : false
	});
	window.open("../polizamanual/ImprimePoliza?"
	+ "nFolioPoliza=" + $("#nFolioPoliza").val()
	+ "&cCentroContable=" + $("#cCentroContable").val()
	+ "&TIPO_REPORTE=PolizaManual.jasper",
		"Poliza",
		"scrollbars=1, resizable=yes, width=1024, height=768");
}

/* ========================================= CALLBACKS GESTION =============================================*/
function ResponsableSiguiente(id_oper) {
}
function OperacionSiguiente(id_oper) {
}
function onPostDisplay(id_oper) {
}
function onPostSubmit(id_oper) {
}

function onLoadPlantilla(id_oper) {
	init(true);
}

function onSubmit(id_oper) {
	return true;
}