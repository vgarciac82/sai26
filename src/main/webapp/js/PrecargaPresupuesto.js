/**
 * PrecargaPresupuesto.js Archivo de funciones JavaScript auxiliares en el
 * modulo de Calendarizacion de Proyecto
 * 
 */
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

/**
 * Funcion de incio que crea componentes JQuery y carga informacion inicial.
 */
function init() {
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	var url = generaURLDataTable();
	$('#dt_calendario').dataTable({
		"bPaginate" : true,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bAutoWidth" : true,
		"sScrollX" : 800,
		"sScrollY" : 250,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : url,
		aoColumns : [ {
			sName : "EP"
		}, {
			sName : "mMonto_ANUAL"
		}, {
			sName : "mMonto_Enero"
		}, {
			sName : "mMonto_Febrero"
		}, {
			sName : "mMonto_Marzo"
		}, {
			sName : "mMonto_Abril"
		}, {
			sName : "mMonto_Mayo"
		}, {
			sName : "mMonto_Junio"
		}, {
			sName : "mMonto_Julio"
		}, {
			sName : "mMonto_Agosto"
		}, {
			sName : "mMonto_Septiembre"
		}, {
			sName : "mMonto_Octubre"
		}, {
			sName : "mMonto_Noviembre"
		}, {
			sName : "mMonto_Diciembre"
		} ],
		oLanguage : es_mx
	});

	// Carga el ejercicio fiscal activo.
	queryFormPost("EjercicioFiscalActvRead", {
		async : false
	});

	// Crea botones y asigna sus acciones
	$("#descargaBtn").button().click(function() {
		descargaProyecto();
	});

	$("#cargaBtn").button().click(function() {
		cargaCalendario();
	});

	// Crea el dialogo de mensajes.
	$("#dialog-mensaje").dialog({
		autoOpen : false,
		height : 450,
		width : 730,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				validaInformacionCapturada();
			}
		}

	});

	if (mensaje != "") {
		$("#msgTextArea").val(mensaje);
		$("#dialog-mensaje").dialog("open");
	}

	habilitaDeshabilitaComponentes();
}

/* ========================================================================= */
/* ================= Funciones de logica de negocios. ====================== */
/* ========================================================================= */

function habilitaDeshabilitaComponentes() {
	if (calendarioCargado == true) {
		$("#fPresupuesto")[0].disabled = true;
		$("#cargaBtn")[0].disabled = true;
	}
}
/**
 * Genera la URL para alimentar el DataTable basado en los atributos del
 * usuario. Si es un usuario administrador podra ver el calendario de su UE mas
 * las partidas restringidas a los demas usuarios. En otro caso solo vera el
 * calendario de su UE.
 */
function generaURLDataTable() {
	var urlBase = window.location.protocol + "//" + window.location.host + "/"
			+ window.location.pathname.split("/")[1]
			+ "/crud?rt=t&ql=vCalendario_Proyecto";
	var condicion = "";

	var arrCap = partidasRestringidas.split("|");
	var orCond = "";
	for (i = 0; i < arrCap.length; i++)
		orCond += (esAdmin ? " OR " : " AND ") + " cPartida "
				+ (esAdmin ? " " : " NOT ") + " LIKE '" + arrCap[i] + "%'";

	condicion = "&qw=1=1 "
			+ encodeURIComponent(" AND ( cUnidadEjecutora = '" + UEUsuario
					+ "'" + orCond + ")");
	return urlBase + condicion;
}

/**
 * Valida que se haya guardado informacion de calendario. Si se guardo
 * correctamente avanza el paso a consulta, de otra forma tienen que capturar
 * nuevamente.
 */
function validaInformacionCapturada() {
	habilitaDeshabilitaComponentes();
	$("#dialog-mensaje").dialog("close");
}
/**
 * Envia el formulario para iniciar la descarga del proyecto
 */
function descargaProyecto() {
	$("#formProyecto").submit();
}

/**
 * Envia el formulario para iniciar la carga de calendario
 */
function cargaCalendario() {
	if ($("#fPresupuesto").val() == "")
		alert("Debe seleccionar un archivo Excel con el calendario para enviar.");
	else {
		$.blockUI({
			message : "Procesando espere ......"
		});
		$("#FormCargaCalendario").submit();
	}
}
/* ========================================================================= */
/* ======= Funciones de validacion requeridas por el motor de flujos. ====== */
/* ========================================================================= */

/**
 * 
 * @param idOper
 *            ID de operacion que se esta ejecutando. (Se define en
 *            cg_operacion)
 */
function onPostDisplay(idOper) {
	alert("Los datos se guardaron correctamente. De clic en el boton ENVIAR para terminar.");
}

/**
 * Funcion llamada previo al envio del caso. En esta funcion se deben llamar las
 * validaciones necesarias para el correcto envio del tramite.
 * 
 * @param idOper
 *            ID de operacion que se esta ejecutando. (Se define en
 *            cg_operacion)
 * 
 */
function onPostSubmit(idOper) {
	$.blockUI({
		message : "Enviando, Espere ......"
	});
	return true;
}

/**
 * Funcion llamada al terminar la carga de motor de flujos.
 * 
 * @param idOper
 *            ID de operacion que se esta ejecutando. (Se define en
 *            cg_operacion)
 */
function onLoadPlantilla(idOper) {
}
/**
 * Funcion llamada al intentar guardar el tramite. Este guardar es independiente
 * del guardado propio del documento. En este metodo se deben validar los campos
 * requeridos para que el guardado sea exitoso.
 * 
 * En caso que el llamado sea exitos (Devuelve true, paso todas las
 * validaciones) el motor automaticamente llamara a las funciones
 * <b>ResponsableSiguiente</b> y <b>OperacionSiguiente</b>
 * 
 * @param idOper
 *            ID de operacion que se esta ejecutando. (Se define en
 *            cg_operacion)
 * @return true si el documento cumple con las validaciones minimas para su
 *         guardado exitoso.<br>
 *         false en caso contrario
 */
function onSubmit(idOper) {
	var p = window.parent;
	p.gestion.setFolio(folio);
	p.gestion.setOperador(operador);
	p.gestion.setFechaDocumento(fecha);
	p.gestion.setEjercicioFiscal($("#cEjercicio").val());
	p.gestion.setMoneda("MXP");

	if (calendarioCargado == true) {
		parent.document.getElementById("pb_send").disabled = false;
		return true;
	} else
		return false;

}

/* ========================================================================= */
/* ============= Funciones requeridas por el motor de flujos. ============== */
/* ========================================================================= */

/**
 * Funcion que devuelve el responsable siguiente del tramite. El responsable
 * siguiente puede ser un grupo o un usuario en particular <br>
 * Para el caso de este tramite se cuenta con los siguientes responsables segun
 * la operacion:<br>
 * <table border="1">
 * <tr>
 * <td> ID OPER </td>
 * <td> Responsable Siguiente </td>
 * </tr>
 * <tr>
 * <td align="center"> 1 </td>
 * <td align="left">CONSULTA_CALENDARIO</td>
 * </tr>
 * </table>
 * 
 * @param idOper
 *            ID de operacion actual.
 */
function ResponsableSiguiente(idOper) {
	if (idOper == 1)
		return "CONSULTA_CALENDARIO";
}
/**
 * Funcion que devuelve la operacion siguiente del tramite. La operacion debe
 * existir en la tabla CG_OPERACION <br>
 * Para el caso de este tramite se cuenta con las siguientes operaciones. <table
 * border="1">
 * <tr>
 * <td> ID OPER </td>
 * <td> Operacion Siguiente </td>
 * </tr>
 * <tr>
 * <td align="center"> 1 </td>
 * <td align="left">revisa_calendario Operacion en la que el usuario valida la
 * informacion capturada </td>
 * </tr>
 * </table>
 * 
 * @param idOper
 *            ID de operacion actual.
 */
function OperacionSiguiente(idOper) {
	if (idOper == 1)
		return 'consulta_calendario';
}