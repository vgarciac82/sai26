/**
 * ReportePresupuesto.js Archivo de funciones JavaScript auxiliares en el modulo
 * de Calendarizacion de Proyecto.
 */

/* ========================================================================= */
/* ================= Funciones de logica de negocios. ====================== */
/* ========================================================================= */

/**
 * Arreglo de meses.
 */
var MESES = [ "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO",
		"AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" ];
/**
 * Funcion de carga inicial de informacion y creacion de objetos
 */
function init() {

	cargaUltimaVersion();
	$("#CONGELAR").button().click(function() {
		congelaInformacion();
	});
	$("#EXPORTAR_DB").button().click(function() {
		exportar();
	});

	$("#GENERAR").button().click(function() {
		generarReporte();
	});

	$("#LIMPIAR").button();

	$("#LIBERAR").button().click(function() {
		liberaCongelamiento();
	});

	$(".MP").each(function() {
		$(this).attr("checked", "checked");
	});

	$(".capitulo").each(function() {
		$(this).attr("checked", "checked");
	});

	calculaMeses();
}

/**
 * Funcion para congelar la informacion.
 */
function congelaInformacion() {
	$.blockUI({
		message : "Realizando congelamiento. Espere ......"
	});

	$
			.ajax({
				url : '../presupuestos/ReportePresupuestos',
				dataType : 'json',
				type : "POST",
				data : {
					"accion" : "CONGELA_INFO"
				},
				async : true,
				success : function(json) {
					var exito = json.success;
					if (exito == "true") {
						r = json.data_1.result;
						alert("Congelamiento realizado con exito. " + r
								+ " filas insertadas.");
					} else {
						alert("ATENCION! No fue posible realizar el congelamiento:\n"
								+ json.data_1.result
								+ "\nIntente nuevamente. Si el problema persiste reportelo al administrador del sistema.x");
					}
					cargaUltimaVersion();
					$.unblockUI();
				},
				error : function(xhr, textStatus, errorThrown) {
					$.unblockUI();
					alert("Advertencia: " + xhr.responseText + "\nEstatus: "
							+ textStatus + "\n" + errorThrown);
					r = true;
				}
			});
}

/**
 * Carga la ultima version de congelamiento de informacion.
 */
function cargaUltimaVersion() {
	$("#ULTIMA_CONGELACION").val("");
	queryFormPost({
		queryName : "readVersionCongelamientoInfo",
		async : false,
		callback : function() {
			if ($("#ULTIMA_CONGELACION").val() == '')
				$("#ULTIMA_CONGELACION").val("N/A");
		}
	});
}

/**
 * Libera el congelamiento de la informacion. Hace el truncate completo a la
 * tabla.
 */
function liberaCongelamiento() {
	if (confirm("Esta seguro de que desea liberar la informacion congelada?")) {
		$.blockUI({
			message : "Liberando informacion. Espere ......"
		});
		queryFormPost({
			queryName : "DeleteCongelamientoInformacion",
			async : true,
			callback : function() {
				queryFormPost({
					queryName : "DeleteCongelamientoVersion",
					async : false,
					callback : function() {
						cargaUltimaVersion();
						$.unblockUI();
						alert("Se libero el congelamiento exitosamente.");
					}
				});
			}
		});
	}
}

/**
 * Funcion que inicia la descarga de la informacion congelada.
 */
function exportar() {
	if (confirm("Esta seguro de que desea descargar el archivo?")) {
		$("#descargaFrm").submit();
	}
}

/**
 * Funcion que inicia la generacion del reporte y su posterior descarga.
 */
function generarReporte() {
	$("#formReporte").submit();
}

/**
 * Selecciona/Deselecciona todos los capitulos.
 */
function seleccionarTodosCapitulos() {

	var checked = $("#seleccionaCapitulos").attr("checked");
	$(".capitulo").each(function() {
		if (checked)
			$(this).attr("checked", "checked");
		else
			$(this).removeAttr("checked");
	});
}

/**
 * Selecciona/Deselecciona todos los momentos presupuestales.
 */
function seleccionarTodosMomentos() {

	var checked = $("#seleccionaMomentos").attr("checked");
	$(".MP").each(function() {
		if (checked)
			$(this).attr("checked", "checked");
		else
			$(this).removeAttr("checked");
	});
}

/**
 * Funcion que obtiene el mes actual y lo toma como referencia para llenar hasta
 * el el select de meses.
 */
function calculaMeses() {

	var hoy = new Date();
	var nMes = hoy.getMonth();

	for ( var i = 0; i <= nMes; i++) {
		$('#MesCorte').append($('<option>', {
			value : MESES[i].toUpperCase(),
			text : MESES[i]
		}));
	}

	$('#MesCorte:last option').attr("selected", "selected");
}