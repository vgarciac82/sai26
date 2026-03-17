const SYSTEM_URL = window.location.protocol +
	"//" +
	window.location.host +
	"/" +
	window.location.pathname
		.split("/")[1];

let calendarioDisponibleNeto = {};
let CAT_RETENCIONES = [];
let firstEditableCalInputId = null;

let montoTotalTramite;

const RET_FAMILIAS = { "001": [], "002": [], "OTROS": [] };
 

const MESES_CONFIG = [
	{ num: 1, disp: '#disponibleEnero', cal: '#calendarioEnero' },
	{ num: 2, disp: '#disponibleFebrero', cal: '#calendarioFebrero' },
	{ num: 3, disp: '#disponibleMarzo', cal: '#calendarioMarzo' },
	{ num: 4, disp: '#disponibleAbril', cal: '#calendarioAbril' },
	{ num: 5, disp: '#disponibleMayo', cal: '#calendarioMayo' },
	{ num: 6, disp: '#disponibleJunio', cal: '#calendarioJunio' },
	{ num: 7, disp: '#disponibleJulio', cal: '#calendarioJulio' },
	{ num: 8, disp: '#disponibleAgosto', cal: '#calendarioAgosto' },
	{ num: 9, disp: '#disponibleSeptiembre', cal: '#calendarioSeptiembre' },
	{ num: 10, disp: '#disponibleOctubre', cal: '#calendarioOctubre' },
	{ num: 11, disp: '#disponibleNoviembre', cal: '#calendarioNoviembre' },
	{ num: 12, disp: '#disponibleDiciembre', cal: '#calendarioDiciembre' }
];

const EP_MESES_COLUMNS = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

const DISP_INPUTS = {
	1: '#disponibleEnero',
	2: '#disponibleFebrero',
	3: '#disponibleMarzo',
	4: '#disponibleAbril',
	5: '#disponibleMayo',
	6: '#disponibleJunio',
	7: '#disponibleJulio',
	8: '#disponibleAgosto',
	9: '#disponibleSeptiembre',
	10: '#disponibleOctubre',
	11: '#disponibleNoviembre',
	12: '#disponibleDiciembre'
};

// ====== CONFIG ======
const API = {
	ENC: '../../api/suficiencia',
	FIN: '../../api/suficiencia/finalizar',
	EP: '../../api/suficiencia/ep',
	DET: '/api/suficiencia/detalle',
	RET: '../../api/suficiencia/retencion',
	PROV: SYSTEM_URL + '/crud?rt=nt&ql=v_Proveedores_Directo',
	DT_RET: SYSTEM_URL + '/crud?rt=nt&ql=v_suficiencia_contrato_directo_retencion',
	DT_EP: SYSTEM_URL + '/crud?rt=nt&ql=vSuficienciaPagoDirectoEP',
	DT_INVOICE: SYSTEM_URL + '/crud?rt=nt&ql=vFacturasPagos',
	DT_INVOICE_RET: '../../invoice/retenciones',
	CAT_RETENCIONES: '../../invoice/catalog/retenciones',
	API_CALENDARIO_EP: '../../suficiencia/calendarioDisponible'
};

const es_mx = {
	sProcessing: "Procesando...",
	sLengthMenu: "Mostrar _MENU_ registros",
	sZeroRecords: "No hay registros a mostrar",
	sEmptyTable: "No hay datos en la tabla",
	sLoadingRecords: "Cargando...",
	sInfo: "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty: "Registro 0 al 0 de 0",
	sInfoFiltered: "(filtered from _MAX_ total entries)",
	sInfoPostFix: "",
	sInfoThousands: ",",
	sSearch: "Filtro:",
	oPaginate: {
		sFirst: "Primero",
		sPrevious: "Ant.",
		sNext: "Sigte.",
		sLast: "&Uacute;ltimo",
	},
};

// Fecha de hoy
const hoy = new Date();

let dtProv;
let dtRet;
let dtDisponible;
let dtEP;
let dtInvoices;
let diaUsarMesSiguiente = 0;
// Dia del mes 1 - 31 por ejemplo 25/01/2026 debe contener 25
let diaHoy = hoy.getDate();


const debounce = (fn, ms = 300) => { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn.apply(null, a), ms); }; };

// Modal helper
const getProvModal = () => bootstrap.Modal.getOrCreateInstance(document.getElementById('modalProveedores'));

// Limpiar filtros/tabla/selección (DT limpia)
const limpiarDialogoProveedores = (limpiarTabla) => {
	$('#fRFC').val('');
	$('#fNombre').val('');
	if (dtProv && limpiarTabla) dtProv.clear().draw();
	dtProv && dtProv.$('tr.selected').removeClass('selected');
};

$(document).ready(function() {

	parent.document.getElementById("pb_save").disabled = true;
	parent.document.getElementById("pb_send").disabled = true;
	parent.document.getElementById("pb_cancel").disabled = true;
	parent.document.getElementById("pb_save").style.visibility = "hidden";
	parent.document.getElementById("pb_send").style.visibility = "hidden";
	parent.document.getElementById("pb_cancel").style.visibility = "hidden";


	$('#modalCalendarioEP').on('blur', '.cal-input', function() {
		validarCalendarioMes(this);
	});

	$('#folio').val(folio);

	$('#fechaHoy').val(
		String(hoy.getDate()).padStart(2, '0') + '/' + String(hoy.getMonth() + 1).padStart(2, '0') + '/' + hoy.getFullYear()
	);

	querySelectPost("CatalogoTipoRetencionRead", "retencionTipo", {
		async: true
	});

	queryFormPost("montoTotalSalarioMinimo", { async: false });
	queryFormPost( {queryName:"diaUsarMesSiguienteRead",
		 async: false,
		callback:function(){
			if( $("#diaUsarMesSiguiente").val() && !isNaN( Number( $("#diaUsarMesSiguiente").val() ) ) ){
				diaUsarMesSiguiente = Number( $("#diaUsarMesSiguiente").val() );
			}
		}
		});


	$('#btnBuscarRFC').on('click', () => {
		limpiarDialogoProveedores(false);
		getProvModal().show();

		if (!$.fn.DataTable.isDataTable('#tblProveedores')) {
			initModalProveedores();
		} else {

			const sep = API.PROV.includes('?') ? '&' : '?';
			dtProv.ajax.url(API.PROV + sep + 'qw=' + encodeURIComponent('1<>1')).load();
		}
	});

	$('#sendButton').on('click', function() {
		sendInvoices();
	});

	// === Validación y guardado ===
	$('#btnGuardar').on('click', function(e) {
		btnGuardarAction(e);
	});

	$("#btnGuardarCalendario").off('click').on('click', function(e) {
		onAgregarDetalleClick(e);
	});

	$('#btnAgregarDetalle').off('click').on('click', abrirDialogoCalendarioEP);
	$('#btnEliminarCal').off('click').on('click', onEliminarCalClick);
	$('#btnFinalizar').off('click').on('click', onFinalizarClick);
	$('#btnCancelar').off('click').on('click', onCancelarClick);





	cargarEncabezadoOnLoad();
	initRetencionesDT();
	initDisponibleDT();
	initInvoices_DT();

	$("#justificacion").on("paste input", function() {
		limpiarJustificacion(this);
	});


});

function validarCalendarioMes(inputEl) {
	const $input = $(inputEl);
	const id = $input.attr('id');

	const cfg = MESES_CONFIG.find(function(m) {
		return m.cal === ('#' + id);
	});
	if (!cfg) {
		return;
	}

	const mesNum = cfg.num;
	const disponibleMes = Number(calendarioDisponibleNeto[mesNum] || 0);
	montoTotalTramite = getMontoTotalTramite();

	let valorStr = $input.val();
	let valorNum = parseMonto(valorStr);

	// 1) Si está vacío, establecer a 0.00
	if (!valorStr || valorStr.trim() === '') {
		valorNum = 0;
	}

	// 2) Validar que no sea negativo (debe ser >= 0)
	if (valorNum < 0) {
		Swal.fire({
			icon: 'warning',
			title: 'Valor inválido',
			text: 'No se permiten valores negativos. El valor se ajustará a 0.00.'
		});
		valorNum = 0;
	}

	// 3) Calcular la suma de los valores ya calendarizados en la tabla tblCalendario
	const sumaTablaCalendario = getSumaTablaCalendario();

	// 4) Calcular la suma de los otros meses del modal (excluyendo el mes actual que se está validando)
	let sumaOtrosMeses = 0;
	$('.cal-input').each(function() {
		if (this === inputEl) {
			return;
		}
		sumaOtrosMeses += parseMonto($(this).val());
	});

	// 5) Calcular el monto pendiente disponible 
	//    = monto total del trámite - suma tabla calendario - suma otros meses del modal
	const montoPendienteDisponible = Math.max(0, montoTotalTramite - sumaTablaCalendario - sumaOtrosMeses);

	// 6) Determinar el límite máximo considerando todas las restricciones:
	//    - No puede exceder el disponible del mes
	//    - No puede exceder el monto pendiente disponible
	//    - La suma total no puede exceder el monto total del trámite
	const limiteMaximo = Math.min(disponibleMes, montoPendienteDisponible);

	// 7) Guardar el valor original para comparar
	const valorOriginal = valorNum;
	let mensajeError = '';
	let valorAjustado = false;

	// 8) Validar todas las restricciones y aplicar el límite más restrictivo
	
	// 8.1) Validar contra el disponible del mes
	if (valorNum > disponibleMes) {
		if (mensajeError) mensajeError += ' ';
		mensajeError += 'No puedes calendarizar más del disponible del mes (' + formatNumber(disponibleMes) + ').';
		valorNum = disponibleMes;
		valorAjustado = true;
	}

	// 8.2) Validar contra el monto pendiente disponible 
	// (montoTotalTramite - sumaTablaCalendario - sumaOtrosMeses)
	// Esto asegura que: montoCapturado <= montoPendiente disponible
	if (montoTotalTramite > 0 && valorNum > montoPendienteDisponible) {
		if (mensajeError) mensajeError += ' ';
		mensajeError += 'No puedes calendarizar más del monto pendiente disponible (' + formatNumber(montoPendienteDisponible) + ').';
		valorNum = montoPendienteDisponible;
		valorAjustado = true;
	}

	// 8.3) Validar que la suma total (tabla + otros meses + este mes) no exceda el monto total del trámite
	// Esto asegura que: sumaTablaCalendario + sumaOtrosMeses + valorNum <= (importe + importeIVA)
	const sumaTotal = sumaTablaCalendario + sumaOtrosMeses + valorNum;
	if (montoTotalTramite > 0 && sumaTotal > montoTotalTramite + 0.000001) {
		if (mensajeError) mensajeError += ' ';
		mensajeError += 'La suma total (tabla + meses) no puede exceder el monto total del trámite (' + formatNumber(montoTotalTramite) + ').';
		// Ajustar al límite máximo permitido que ya considera todas las restricciones
		valorNum = limiteMaximo;
		valorAjustado = true;
	}

	// 9) Asegurar que el valor final cumpla todas las restricciones simultáneamente:
	//    - >= 0
	//    - <= disponible del mes
	//    - <= monto pendiente disponible (considerando tabla + otros meses)
	// El limiteMaximo ya considera ambas restricciones, así que aplicamos el mínimo final
	valorNum = Math.max(0, Math.min(valorNum, limiteMaximo));

	// 10) Mostrar mensaje de error solo si hubo ajuste y el valor cambió
	if (valorAjustado && valorOriginal !== valorNum) {
		Swal.fire({
			icon: 'warning',
			title: 'Valor ajustado',
			text: mensajeError
		});
	}

	// Normalizar a 0.00 y actualizar el campo
	$input.val(formatNumber(valorNum));
}


function parseMonto(str) {
	if (str == null) return 0;
	let s = String(str).trim();
	if (s === '') return 0;
	s = s.replace(/\s+/g, '').replace(',', '');
	const n = parseFloat(s);
	return isNaN(n) ? 0 : n;
}

function getMontoTotalTramite() {
	const v = parseMonto($('#importe').val()) + parseMonto($("#importeIVA").val());
	return Math.round(v * 100) / 100;
}

const limpiarJustificacion = function(textarea) {
	const sanitizeInput = function(text) {
		return text
			.replace(/[“”‘’]/g, '') // comillas especiales
			.replace(/[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s\.\-_:;¡!¿?\(\)]/g, '') // solo permitidos
			.replace(/,/g, ''); // elimina comas
	};

	textarea.value = sanitizeInput(textarea.value);
};


const btnGuardarAction = function() {
	const rfc = ($('#rfc').val() || '').trim();
	const just = ($('#justificacion').val() || '').trim();
	const palabras = just.split(/\s+/).filter(Boolean);

	if (!rfc) {
		Swal.fire({ icon: 'warning', title: 'RFC requerido', text: 'Debes seleccionar un proveedor.', confirmButtonText: 'OK' });
		return;
	}
	//	if (monto > montoMax) {
	//	    Swal.fire({ icon: 'warning', title: 'Monto inválido', text: 'El monto no puede ser mayor a 300 UMAS, equivalentes a ' + montoMax+ ' pesos.', confirmButtonText: 'OK' });
	//	    return;
	//	}
	if (palabras.length < 10) {
		Swal.fire({ icon: 'warning', title: 'Justificación insuficiente', text: 'La justificación debe tener al menos 10 palabras.', confirmButtonText: 'OK' });
		return;
	}

	guardarTodoWrapper();
}

const guardarTodoWrapper = async function() {
	try {
		setSaving(true);
		await guardarTodo();
		parent.parent.window.frames["doctree"].location.reload();
		mostrarSecciones();
		$("#btnFinalizar").removeClass('d-none');
		Swal.fire({ icon: 'success', title: 'Guardado', text: 'La suficiencia se guardó correctamente.' });
	} catch (e) {
		const msg = (e && e.message) ? e.message : (typeof e === 'string' ? e : 'Error al guardar');
		Swal.fire({ icon: 'error', title: 'Error al guardar', text: msg });
	} finally {
		setSaving(false);
	}
}

const setSaving = function(isSaving) {
	const $btn = $('#btnGuardar');
	if (isSaving) {
		$btn.prop('disabled', true);
		if (!$btn.data('orig')) $btn.data('orig', $btn.html());
		$btn.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Guardando...');
	} else {
		if ($btn.data('orig')) $btn.html($btn.data('orig'));
		$btn.prop('disabled', false);
	}
}

// ====== PROVEEDORES (Modal) ======
const initModalProveedores = function() {
	if ($.fn.DataTable.isDataTable('#tblProveedores')) {
		dtProv = $('#tblProveedores').DataTable();
	} else {
		dtProv = $('#tblProveedores').DataTable({
			ajax: {
				url: API.PROV + '&qw=1<>1',
				type: 'POST'
			},
			columns: [
				{ data: 'rfc' },
				{ data: 'nombre_proveedor' }
			],
			order: [[1, 'asc']],
			rowId: "rfc",
			paging: true,
			processing: true,
			serverSide: true,
			scrollCollapse: true,
			scrollY: "400px",
			language: es_mx,
			pageLength: 10,
			searching: false,
			info: false,
			lengthChange: false,
		});

		$('#tblProveedores tbody')
			.off('click')
			.on('click', 'tr', function() {
				dtProv.$('tr.selected').removeClass('selected');
				$(this).addClass('selected');
			});

		// 2) Filtros reactivos -> recargar DT
		const recargar = debounce(() => {
			let qw = "";

			const rfc = ($('#fRFC').val() || '').trim().toUpperCase();
			const nombre = ($('#fNombre').val() || '').trim();

			if (rfc) {
				qw += (qw ? " OR " : "") + "rfc LIKE '" + rfc.replace(/'/g, "''") + "%'";
			}
			if (nombre) {
				qw += (qw ? " OR " : "") + "nombre_proveedor LIKE '%" + nombre.replace(/'/g, "''") + "%'";
			}
			if (!qw) qw = "1<>1";

			const sep = API.PROV.includes('?') ? '&' : '?';
			dtProv.ajax.url(API.PROV + sep + 'qw=' + encodeURIComponent(qw)).load();
		}, 250);


		$('#fRFC, #fNombre').off('input').on('input', recargar);

		// 4) Botón Seleccionar
		$('#btnProvSelect').off('click').on('click', function() {
			const row = dtProv.row('.selected');
			if (!row.any()) {
				Swal.fire({
					icon: 'warning',
					title: 'Selecciona un registro',
					text: 'Debes elegir un proveedor de la tabla.',
					confirmButtonText: 'Entendido'
				});
				return;
			}
			const data = row.data();
			// Llenar RFC y Nombre en la pantalla principal
			$('#rfc').val(data.rfc || '');
			$('#nombre').val(data.nombre_proveedor || '');
			getProvModal().hide();
		});

		$('#modalProveedores')
			.off('hidden.bs.modal')
			.on('hidden.bs.modal', function() { limpiarDialogoProveedores(true); $('#justificacion').focus(); });
	}
};


// ====== WS CALLS ======
guardarTodo = async function() {
	let folioSearch = Number($('#folio').val() || 0);

	let encData = null;
	try {
		encData = await $.ajax({
			url: API.ENC + '?folio=' + encodeURIComponent(folioSearch),
			method: 'GET'
		});
	} catch {
		encData = null;
	}

	if (encData) {
		await guardarEncabezado('PUT');
		if (encData.idContrato) {
			$('#idContrato').val(encData.idContrato);
		}
	} else {
		const nuevoEnc = await guardarEncabezado('POST');
		if (nuevoEnc && nuevoEnc.idContrato) {
			$('#idContrato').val(nuevoEnc.idContrato);
		}
	}

};


const guardarEncabezado = function(method) {
	return $.ajax({
		url: API.ENC,
		method,
		contentType: 'application/json; charset=UTF-8',
		data: JSON.stringify(payloadEncabezado())
	}).then(r => {
		if (r && r.error) return Promise.reject(new Error(r.error));
		return r;
	}).fail(xhr => {
		const msg = (typeof errorMsg === 'function') ? errorMsg(xhr) : 'Error en encabezado';
		throw new Error(msg);
	});
}

const payloadEncabezado = function() {

	const folioNum = Number($('#folio').val() || 0);
	const base = Number($('#importe').val() || 0);
	const iva = Number($('#importeIVA').val() || 0);
	const total = Number($('#total').val() || 0);

	const pct = Number($('#porcentajeIVA').val())

	const fechaUI = ($('#aplicacion').val() || $('#fechaHoy').val() || '').trim();
	const fechaAplicacionISO = toISODate(fechaUI);


	const payload = {
		folioSuficienciaPagoDirecto: folio,
		idContrato: $('#idContrato').val(),
		tipoContrato: "PD",
		fechaAplicacion: fechaAplicacionISO,
		fechaCancelacion: null,
		centroContable: cCentroContable,
		ramo: "16",
		unidadResponsable: unidadResponsable,
		porcentajeIVA: pct,
		tipoPoliza: "CO",
		idStatus: 1,
		ejercicioFiscal: ejercicioFiscal,
		unidadResponsableContable: "RHQ",
		rfc: $('#rfc').val().trim(),
		justificacion: $('#justificacion').val().trim(),
		loginCaptura: login,
		importe: base,
		importeIVA: iva,
		total: total,
		porcentajeIVA: pct
	};


	return payload;
}


const toISODate = function(dstr) {
	if (!dstr) return null;
	const s = dstr.trim();
	// si ya viene en ISO, respétalo
	if (/^\d{4}-\d{2}-\d{2}/.test(s)) return s.substring(0, 10);
	const m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(s);
	if (!m) return null;
	const [, dd, mm, yyyy] = m;
	return `${yyyy}-${mm}-${dd}`;
}

// ==== Helpers ====
const toMXDate = function(iso) {
	if (!iso) return '';
	const s = String(iso).substring(0, 10); // yyyy-MM-dd
	const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(s);
	return m ? `${m[3]}/${m[2]}/${m[1]}` : iso;
};

const mostrarSecciones = function() {

	$('#cardRetenciones,#cardRetencionesPendientes, #cardNeto, #cardCalendario, #cardCFDI, #cardTotales, #cardCargaFacturas').removeClass('d-none');
	if (!$.fn.DataTable.isDataTable('#tblRetenciones')) {
		initRetencionesDT();
	}

	if (!$.fn.DataTable.isDataTable('#dtInvoices')) {
		initInvoices_DT();
	}

	setTimeout(() => {
		dtRet.columns.adjust().draw(false);
		dtInvoices.columns.adjust().draw(false);
	}, 0);

};

const getFolioParaCarga = function() {
	return (typeof folio !== 'undefined' && folio) ||
		new URLSearchParams(location.search).get('folio') ||
		$('#folio').val();
};

// ==== Carga en onload ====
const cargarEncabezadoOnLoad = async function() {
	const folioVal = getFolioParaCarga();
	if (!folioVal) return;

	const montoTotal = getMontoTotalTramite();
	const totalEPs = getTotalEPsCalendarizado();
	if (montoTotal > 0 && totalEPs >= (montoTotal - 0.000001)) {
		Swal.fire({
			icon: 'info',
			title: 'Recurso calendarizado',
			text: 'El recurso del trámite ya está completamente calendarizado. No es posible agregar más EPs.'
		});
		return;
	}

	try {
		// 1) Encabezado
		const enc = await $.ajax({
			url: API.ENC + '?folio=' + encodeURIComponent(folioVal),
			method: 'GET'
		});
		if (!enc) return;

		// Rellena visibles
		$('#folio').val(enc.folioSuficienciaPagoDirecto || '');
		$('#idContrato').val(enc.idContrato || '');
		$('#fechaHoy').val(toMXDate(enc.fechaAplicacion)); // muestra dd/MM/yyyy


		$('#importe').val(formatNumber(enc.importe ?? 0));
		$('#importeIVA').val(formatNumber(enc.importeIVA ?? 0));
		$('#importeRetenciones').val(formatNumber(enc.importeRetenciones ?? 0));
		$('#total').val(formatNumber(enc.total ?? 0));

		montoTotalTramite = (enc.importe ?? 0) + (enc.importeIVA ?? 0);

		$('#rfc').val(enc.rfc || enc.RFC || '');
		$('#nombre').val(enc.razonSocial);
		$('#justificacion').val(enc.justificacion || '');
		$("#porcentajeIVA").val(enc.porcentajeIVA || '');

		mostrarSecciones();
		loadRetencionesPendientes(tipoPago, folioVal);
		initEP_DT();
		$("#btnFinalizar").removeClass('d-none');


	} catch (xhr) {
		// Si es 404, lo tomamos como "no existe aún" y no mostramos alerta
		if (xhr && xhr.status === 404) {
			console.info('Suficiencia no encontrada, es probable que sea nuevo trámite.');
			return;
		}

		// Otros estatus => mostrar mensaje de error
		const msg = (function() {
			try {
				const r = JSON.parse(xhr.responseText || '{}');
				return r.error || 'No fue posible cargar la suficiencia.';
			} catch (e) {
				return 'No fue posible cargar la suficiencia.';
			}
		})();

		if (window.Swal) {
			Swal.fire({ icon: 'error', title: 'Error al cargar', text: msg });
		} else {
			alert('Error al cargar: ' + msg);
		}
	}
};


// ==== Helpers para retenciones ====

// Helper para bloquear/desbloquear el botón
const setAddingRetention = function(isAdding) {
	const $btn = $('#btnAgregarRet');
	if (isAdding) {
		$btn.prop('disabled', true);
		if (!$btn.data('orig')) $btn.data('orig', $btn.html());
		$btn.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Agregando...');
	} else {
		if ($btn.data('orig')) $btn.html($btn.data('orig'));
		$btn.prop('disabled', false);
	}
};

const getSelectTipoSaiByRetId = function(idRetencion) {
	return $(`select.tipo-sai[id="${idRetencion}"]`);
};

const onAgregarRetClick = async function(idRetencion, uuid) {

	const folioVal = getFolioParaCarga() || $('#folio').val();
	const tipoId = getSelectTipoSaiByRetId(idRetencion);

	console.log('Agregar retención: folio=', folioVal, ' tipoId=', tipoId);

	if (!tipoId || tipoId.length === 0) {
		Swal.fire({ icon: "error", title: "Oops", text: "No se encontró el selector de retención para la fila." });
		return;
	}

	const val = tipoId.val();
	if (!val) {
		Swal.fire({ icon: "warning", title: "Error", text: "Error, no ha seleccionado el tipo de retención" });
		tipoId.focus();
		return;
	}

	// ===== Validación de tolerancia =====
	const $tr = $('#tblRetencionesPendientes tbody tr').filter(function() {
		return String($(this).data('uuid')) === String(idRetencion);
	});

	if ($tr.length === 0) {
		Swal.fire({ icon: "error", title: "Error", text: "No se encontró la fila de la retención seleccionada." });
		return;
	}

	const round2 = n => Math.round((Number(n) || 0) * 100) / 100;

	const base = Number($tr.data('base')) || 0;       // importeBase (del AJAX)
	const importe = Number($tr.data('importe')) || 0; // mImporteRetencion (del AJAX)

	// % seleccionado en el option (ej. 0.1 para 10%)
	const pct = parseMonto(tipoId.find('option:selected').data('pct')) || 0;

	const importeSai = round2(base * pct);            // calculado por SAI
	const diffAbs = Math.abs(round2(importeSai - importe));

	if (diffAbs > 0.02) {
		Swal.fire({
			icon: 'error',
			title: 'Diferencia fuera de tolerancia',
			html: `
        <div style="text-align:left">
          <div><b>Base:</b> ${fmtMoneyMx(base)}</div>
          <div><b>% Retención:</b> ${(pct * 100).toFixed(2)}%</div>
          <div><b>Importe SAI:</b> ${fmtMoneyMx(importeSai)}</div>
          <div><b>Importe Retención:</b> ${fmtMoneyMx(importe)}</div>
          <div><b>Diferencia:</b> ${fmtMoneyMx(diffAbs)}</div>
          <hr>
          <div>La tolerancia permitida es <b>$0.02</b>. Ajusta la selección.</div>
        </div>
      `
		});
		tipoId.focus();
		return;
	}
	// ===== Fin validación =====

	const payload = {
		folioSuficienciaPagoDirecto: Number(folioVal),
		idTipoRetencion: Number(val),
		importeRetencion: importe,   // importe calculado validado contra tolerancia
		idRetencion: idRetencion
	};

	try {
		setAddingRetention(true);

		await $.ajax({
			url: API.RET,
			method: 'POST',
			contentType: 'application/json; charset=UTF-8',
			data: JSON.stringify(payload)
		});

		recargarRetencionesDT(folioVal);
		loadRetencionesPendientes(tipoPago, folioVal);

		Swal.fire({ icon: 'success', title: 'Retención agregada', timer: 1000, showConfirmButton: false });
	} catch (xhr) {
		let msg = 'No fue posible agregar la retención.';
		try { const r = JSON.parse(xhr.responseText || '{}'); if (r.error) msg = r.error; } catch (e) { }
		Swal.fire({ icon: 'error', title: 'Error', text: msg });
	} finally {
		setAddingRetention(false);
	}
};



const initInvoices_DT = function() {

	if ($.fn.DataTable.isDataTable('#invoicesTable')) {
		dtInvoices = $('#invoicesTable').DataTable();
		return;
	}


	dtInvoices = $('#invoicesTable').DataTable({
		ajax: {
			url: API.DT_INVOICE + "&qw=TipoPago='" + encodeURIComponent(tipoPago) + "' AND folioPago = " + encodeURIComponent(folio),
			type: 'POST'
		},
		columns: [
			{ data: 'factura' },
			{ data: 'Importe' },
			{ data: 'mimporteiva' },
			{ data: 'mimporteconiva' },
			{ data: 'mImporteRetencion' },
			{ data: 'mImporteNeto' },
		],
		columnDefs: [
			{ targets: [1, 2, 3, 4, 5], render: moneyRender, className: 'text-end' }
		],
		initComplete: function() {
			if ($('#invoicesTable').is(':visible')) {
				dtInvoices.columns.adjust().draw(false);
			}
		},
		order: [[0, 'asc']],
		rowId: 'factura',
		paging: true,
		processing: true,
		serverSide: true,
		scrollCollapse: true,
		scrollY: '300px',
		language: es_mx,
		pageLength: 10,
		searching: false,
		info: false,
		lengthChange: false
	});

	$('#invoicesTable tbody').off('click').on('click', 'tr', function() {
		dtInvoices.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
	});

}

const refreshInvoicesDT = function() {
	$('#invoicesTable').DataTable().ajax.reload(null, false);
}

// Función helper para obtener la suma de los valores ya calendarizados en la tabla tblCalendario
const getSumaTablaCalendario = function() {
	if (!dtEP || typeof dtEP.rows !== 'function') {
		return 0;
	}

	let sumaCalendarizado = 0;

	// Iterar sobre todas las filas y sumar los valores de los meses (columnas 1-12)
	dtEP.rows().every(function() {
		const row = this.data();
		if (!row) return;

		// Sumar los valores de los 12 meses (Ene a Dic)
		const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
		meses.forEach(function(mes) {
			const val = Number(row[mes] || 0);
			if (!isNaN(val)) {
				sumaCalendarizado += val;
			}
		});
	});

	return sumaCalendarizado;
};

// Función para actualizar los montos calendarizado y pendiente
const actualizarMontosCalendarizado = function() {
	const sumaCalendarizado = getSumaTablaCalendario();

	// Actualizar montoCalendarizado
	$('#montoCalendarizado').val(formatNumber(sumaCalendarizado));

	// Calcular montoPendiente = (importe + importeIVA) - montoCalendarizado
	const importe = parseMonto($('#importe').val());
	const importeIVA = parseMonto($('#importeIVA').val());
	const montoPendiente = Math.max(0, (importe + importeIVA) - sumaCalendarizado);
	$('#montoPendiente').val(formatNumber(montoPendiente));
};

const initEP_DT = function() {

	if ($.fn.DataTable.isDataTable('#tblCalendario')) {
		dtEP = $('#tblCalendario').DataTable();
		// Actualizar montos cuando la tabla ya existe
		actualizarMontosCalendarizado();
		return;
	}

	const folioVal = getFolioParaCarga() || 0;

	dtEP = $('#tblCalendario').DataTable({
		ajax: {
			url: API.DT_EP + '&qw=nFolioSuficienciaPagoDirecto=' + encodeURIComponent(folioVal),
			type: 'POST'
		},
		columns: [
			{ data: 'EP' },
			{ data: 'Ene' },
			{ data: 'Feb' },
			{ data: 'Mar' },
			{ data: 'Abr' },
			{ data: 'May' },
			{ data: 'Jun' },
			{ data: 'Jul' },
			{ data: 'Ago' },
			{ data: 'Sep' },
			{ data: 'Oct' },
			{ data: 'Nov' },
			{ data: 'Dic' }
		],
		columnDefs: [
			{ targets: [1,2,3,4,5,6,7,8,9,10,11,12], render: moneyRender, className: 'text-end' }
		],
		order: [[0, 'asc']],
		rowId: 'EP',
		paging: true,
		processing: true,
		serverSide: true,
		scrollCollapse: true,
		scrollY: '300px',
		language: es_mx,
		pageLength: 10,
		searching: false,
		info: false,
		lengthChange: false,
		drawCallback: function() {
			actualizarMontosCalendarizado();
		}
	});

	// Selección simple
	$('#tblCalendario tbody').off('click').on('click', 'tr', function() {
		dtEP.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
	});


};

// Datatable para Disponible Neto
const initDisponibleDT = function() {

	if ($.fn.DataTable.isDataTable('#tblNeto')) {
		dtDisponible = $('#tblNeto').DataTable();
		return;
	}

	const mes = 12;//$("#fechaHoy").val().split('/')[1] || '01';
	const folioVal = getFolioParaCarga() || 0;

	dtDisponible = $('#tblNeto').DataTable({
		ajax: {
			url: SYSTEM_URL + "/crud?rt=nt&ql=dbo.fn_SaldoDisponibleContratoDirecto( " + mes + "," + folioVal + "," + "'" + login + "' )",
			type: 'POST'
		},
		columns: [
			{ data: 'EP' },
			{ data: 'mSaldoAnual' },
		],
		order: [[0, 'asc']],
		rowId: 'EP',
		paging: true,
		processing: true,
		serverSide: true,
		scrollCollapse: true,
		scrollY: '300px',
		language: es_mx,
		pageLength: 10,
		searching: true,
		info: true,
		lengthChange: false
	});

	$('#tblNeto tbody').off('click').on('click', 'tr', function() {
		dtDisponible.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
	});


};

const initRetencionesDT = function() {

	if ($.fn.DataTable.isDataTable('#tblRetenciones')) {
		dtRet = $('#tblRetenciones').DataTable();
		return;
	}

	const folioVal = getFolioParaCarga() || 0;

	dtRet = $('#tblRetenciones').DataTable({
		ajax: {
			url: API.DT_RET + '&qw=nFolioSuficienciaPagoDirecto=' + encodeURIComponent(folioVal),
			type: 'POST',
			dataSrc: 'data'
		},
		columns: [
			{ data: 'idRetencion' },
			{ data: 'uuid' },
			{ data: 'nombre_retencion' },
			{ data: 'porcentajeRetencion' },
			{ data: 'base' },
			{ data: 'mImporteRetencion' },
			{
				data: null,
				orderable: false,
				searchable: false,
				defaultContent:
					'<button class="btn btn-sm btn-outline-danger btn-del" title="Eliminar"><i class="fa fa-trash"></i></button>'
			}
		],
		columnDefs: [
			{ targets: 0, visible: false, searchable: false }
		],
		initComplete: function() {
			if ($('#cardRetenciones').is(':visible')) {
				dtRet.columns.adjust().draw(false);
			}
		},
		order: [[0, 'asc']],
		rowId: 'idRetencion',
		paging: true,
		processing: true,
		serverSide: true,
		scrollCollapse: true,
		scrollY: '300px',
		autoWidth: true,
		language: es_mx,
		pageLength: 10,
		searching: false,
		info: false,
		lengthChange: false
	});

	$('#tblRetenciones tbody')
		.off('click')
		.on('click', 'tr', function() {
			dtRet.$('tr.selected').removeClass('selected');
			$(this).addClass('selected');
		});


	$('#tblRetenciones tbody').on('click', '.btn-del', async function(e) {
		e.stopPropagation();
		const row = dtRet.row($(this).closest('tr')).data();
		const folio = getFolioParaCarga() || 0;

		const conf = await Swal.fire({
			icon: 'question',
			title: 'Eliminar retención',
			text: `¿Deseas eliminar la retención seleccionada?`,
			showCancelButton: true,
			confirmButtonText: 'Sí, eliminar',
			cancelButtonText: 'Cancelar'
		});

		if (!conf.isConfirmed) return;

		await $.ajax({
			url: API.RET,
			type: 'DELETE',
			contentType: 'application/json',
			data: JSON.stringify({ "folio": folio, "idRetencion": row.idRetencion })
		});

		Swal.fire({ icon: 'success', title: 'Retención eliminada', timer: 1000, showConfirmButton: false });
		recargarRetencionesDT(folio);
		loadRetencionesPendientes(tipoPago, folio);
	});
};

const recargarRetencionesDT = function(folioVal) {
	const url = API.DT_RET + '&qw=nFolioSuficienciaPagoDirecto=' + encodeURIComponent(folioVal)
	dtRet.ajax.url(url).load();
};

const recargarEPsDT = function(folioVal) {
	const url = API.DT_EP + '&qw=nFolioSuficienciaPagoDirecto=' + encodeURIComponent(folioVal)
	dtEP.ajax.url(url).load();
};

const recargarDispobleDT = function(folioVal) {
	const mes = 12;
	const url = SYSTEM_URL + "/crud?rt=nt&ql=dbo.fn_SaldoDisponibleContratoDirecto( " + mes + "," + folioVal + "," + "'" + login + "' )"
	dtDisponible.ajax.url(url).load();
};

const setAddingDetalle = function(isAdding) {
	const $btn = $('#btnAgregarDetalle');
	if (isAdding) {
		$btn.prop('disabled', true);
		if (!$btn.data('orig')) $btn.data('orig', $btn.html());
		$btn.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Agregando...');
	} else {
		if ($btn.data('orig')) $btn.html($btn.data('orig'));
		$btn.prop('disabled', false);
	}
};

const getTotalEPsCalendarizado = function() {
	if (!dtEP || typeof dtEP.rows !== 'function') {
		return 0;
	}

	let total = 0;

	dtEP.rows().every(function() {
		const row = this.data();
		if (!row) return;

		EP_MESES_COLUMNS.forEach(function(col) {
			const val = Number(row[col] || 0);
			if (!isNaN(val)) {
				total += val;
			}
		});
	});

	return total;
};


const onAgregarDetalleClick = async function(e) {
	e.preventDefault();

	const folioVal = getFolioParaCarga() || $('#folio').val();

	const rowSel = (typeof dtDisponible !== 'undefined' ? dtDisponible.row('.selected')
		: (typeof dtNeto !== 'undefined' ? dtNeto.row('.selected') : null));

	if (!rowSel || !rowSel.any()) {
		Swal.fire({ icon: 'warning', title: 'Selecciona un EP', text: 'Elige un renglón de la tabla para continuar.' });
		return;
	}

	const data = rowSel.data();
	const ep = (data && (data.ep || data.EP)) ? (data.ep || data.EP) : '';
	const neto = Number((data && (data.mSaldoAnual ?? data.mSaldoAnual)) || 0);

	// Validaciones mínimas
	if (!folioVal) {
		Swal.fire({ icon: 'warning', title: 'Folio requerido', text: 'No hay folio para asociar el EP.' });
		return;
	}
	if (!ep) {
		Swal.fire({ icon: 'warning', title: 'EP requerido', text: 'No se pudo leer el EP del renglón seleccionado.' });
		return;
	}
	if (!(neto > 0)) {
		Swal.fire({ icon: 'warning', title: 'Disponible Neto', text: 'El neto del renglón debe ser mayor a 0.' });
		return;
	}

	// Mapeo de meses: nombre del input -> número de mes (1-12)
	const mesesMap = [
		{ id: 'calendarioEnero', mes: 1 },
		{ id: 'calendarioFebrero', mes: 2 },
		{ id: 'calendarioMarzo', mes: 3 },
		{ id: 'calendarioAbril', mes: 4 },
		{ id: 'calendarioMayo', mes: 5 },
		{ id: 'calendarioJunio', mes: 6 },
		{ id: 'calendarioJulio', mes: 7 },
		{ id: 'calendarioAgosto', mes: 8 },
		{ id: 'calendarioSeptiembre', mes: 9 },
		{ id: 'calendarioOctubre', mes: 10 },
		{ id: 'calendarioNoviembre', mes: 11 },
		{ id: 'calendarioDiciembre', mes: 12 }
	];

	// Recolectar valores no-cero del diálogo modalCalendarioEP
	const suficienciaArray = [];
	for (const mesInfo of mesesMap) {
		const valorInput = $('#' + mesInfo.id).val();
		const importe = parseMonto(valorInput) || 0;

		if (importe > 0) {
			suficienciaArray.push({
				folioSuficienciaPagoDirecto: Number(folioVal),
				ep: ep,
				mes: mesInfo.mes,
				importe: importe
			});
		}
	}

	// Validar que haya al menos un mes con importe
	if (suficienciaArray.length === 0) {
		Swal.fire({
			icon: 'warning',
			title: 'Sin importes',
			text: 'Debes ingresar al menos un importe mayor a 0 en el calendario.'
		});
		return;
	}

	// Cerrar el diálogo inmediatamente al guardar, sin esperar la respuesta
	const modalEl = document.getElementById('modalCalendarioEP');
	const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
	modal.hide();

	try {
		setAddingDetalle(true);

		// Enviar el arreglo de objetos SuficienciaPagoDirectoEP
		await $.ajax({
			url: API.EP,
			method: 'POST',
			contentType: 'application/json; charset=UTF-8',
			data: JSON.stringify(suficienciaArray)
		});

		recargarDispobleDT(folioVal); // recarga el DT de Disponible Neto
		recargarEPsDT(folioVal); // recarga el DT de EPs

		Swal.fire({ icon: 'success', title: 'EP agregado', text: `EP ${ep} asociado al folio ${folioVal} con ${suficienciaArray.length} mes(es).`, timer: 1200, showConfirmButton: false });

		const modalEl = document.getElementById('modalCalendarioEP');
		const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
		modal.hide();
	} catch (xhr) {
		let msg = 'No fue posible agregar el EP.';
		try { const r = JSON.parse(xhr.responseText || '{}'); if (r.error) msg = r.error; } catch (e) { }
		Swal.fire({ icon: 'error', title: 'Error', text: msg });
	} finally {
		setAddingDetalle(false);
	}
};



// Spinner / bloqueo del botón Eliminar Calendario
const setDeletingCal = function(isDeleting) {
	const $btn = $('#btnEliminarCal');
	if (isDeleting) {
		$btn.prop('disabled', true);
		if (!$btn.data('orig')) $btn.data('orig', $btn.html());
		$btn.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Eliminando...');
	} else {
		if ($btn.data('orig')) $btn.html($btn.data('orig'));
		$btn.prop('disabled', false);
	}
};

// Click handler: elimina EP seleccionada (tblCalendario) vía API.EP
const onEliminarCalClick = async function(e) {
	e.preventDefault();

	// Obtén la fila seleccionada del DT de calendario
	const dt = (typeof dtEP !== 'undefined') ? dtEP
		: null;

	if (!dt) {
		Swal.fire({ icon: 'error', title: 'Tabla no disponible', text: 'No se encontró la tabla de calendario (dtEP).' });
		return;
	}

	const row = dt.row('.selected');
	if (!row || !row.any()) {
		Swal.fire({ icon: 'warning', title: 'Selecciona un registro', text: 'Elige una EP de la tabla para eliminar.' });
		return;
	}

	const data = row.data();
	const ep = (data && data.EP ? data.EP : '');
	const folioVal = getFolioParaCarga() || $('#folio').val();

	if (!folioVal || !ep) {
		Swal.fire({ icon: 'error', title: 'Datos insuficientes', text: 'No fue posible determinar el folio o la EP seleccionada.' });
		return;
	}

	// Confirmación
	const conf = await Swal.fire({
		icon: 'question',
		title: 'Eliminar EP',
		text: `¿Deseas eliminar la EP "${ep}" del folio ${folioVal}?`,
		showCancelButton: true,
		confirmButtonText: 'Sí, eliminar',
		cancelButtonText: 'Cancelar'
	});
	if (!conf.isConfirmed) return;

	try {
		setDeletingCal(true);

		// DELETE al API EP con llave compuesta (folio + ep)
		const url = API.EP + '?folio=' + encodeURIComponent(folioVal) + '&ep=' + encodeURIComponent(ep);
		await $.ajax({ url, method: 'DELETE' });

		recargarEPsDT(folioVal);
		recargarDispobleDT(folioVal);

		Swal.fire({ icon: 'success', title: 'EP eliminada', timer: 1100, showConfirmButton: false });

	} catch (xhr) {
		let msg = 'No fue posible eliminar la EP.';
		try { const r = JSON.parse(xhr.responseText || '{}'); if (r.error) msg = r.error; } catch (e) { }
		Swal.fire({ icon: 'error', title: 'Error', text: msg });
	} finally {
		setDeletingCal(false);
	}
};


const getPendingRetCount = function() {
	const $tbody = $('#tblRetencionesPendientes tbody');
	if (!$tbody.length) return 0;
	return $tbody.find('tr:visible').length;
};

const onFinalizarClick = async function(e) {
	e.preventDefault();

	// 1) Debe existir al menos una EP en la tabla (dtEP o dtNeto sobre #tblNeto)
	const dtEPRef = (typeof dtEP !== 'undefined') ? dtEP
		: (typeof dtNeto !== 'undefined') ? dtNeto
			: null;

	if (!dtEPRef || dtEPRef.rows().count() === 0) {
		Swal.fire({
			icon: 'warning',
			title: 'Falta EP',
			text: 'Debe capturar al menos una EP antes de finalizar.'
		});
		return;
	}

	// 2) NUEVA REGLA: si hay retenciones pendientes (no mapeadas a SAI), bloquear
	const pendientes = getPendingRetCount();
	if (pendientes > 0) {
		Swal.fire({
			icon: 'error',
			title: 'Retenciones pendientes',
			text: `Hay ${pendientes} retención(es) sin mapear a SAI. Debe completarlas antes de finalizar.`
		});
		return;
	}

	// 3) VALIDACIÓN: Verificar coherencia del trámite antes de finalizar
	try {
		if (window.$ && $.blockUI) {
			$.blockUI({ message: '<h5>Validando trámite...</h5>' });
		}

		const folio = getFolioParaCarga() || $('#folio').val();
		const validacion = await validarTramiteCompleto(folio);

		if ($.unblockUI) $.unblockUI();

		// Si hay errores, mostrar y detener
		if (!validacion.valido && validacion.errores && validacion.errores.length > 0) {
			let mensajeErrores = '<div class="text-start"><strong>Se encontraron inconsistencias:</strong><ul class="mt-2">';
			validacion.errores.forEach((error, idx) => {
				mensajeErrores += `<li><strong>${error.tipo}</strong>: ${error.descripcion}`;
				if (error.valorEsperado && error.valorObtenido) {
					mensajeErrores += `<br/><small class="text-muted">Esperado: ${error.valorEsperado} | Obtenido: ${error.valorObtenido}</small>`;
				}
				mensajeErrores += '</li>';
			});
			mensajeErrores += '</ul></div>';

			Swal.fire({
				icon: 'error',
				title: 'Validación de trámite fallida',
				html: mensajeErrores,
				confirmButtonText: 'Entendido'
			});
			return;
		}

		// Si solo hay advertencias, permitir continuar con confirmación
		if (validacion.advertencias && validacion.advertencias.length > 0) {
			let mensajeAdvertencias = '<div class="text-start"><strong>Se encontraron advertencias:</strong><ul class="mt-2">';
			validacion.advertencias.forEach((adv) => {
				mensajeAdvertencias += `<li><strong>${adv.tipo}</strong>: ${adv.descripcion}`;
				if (adv.valorObtenido && adv.calculado) {
					mensajeAdvertencias += `<br/><small class="text-muted">Calculado: ${adv.calculado} | Registrado: ${adv.valorObtenido}</small>`;
				}
				mensajeAdvertencias += '</li>';
			});
			mensajeAdvertencias += '</ul></div>';

			const confAdvertencias = await Swal.fire({
				icon: 'warning',
				title: 'Advertencias en la validación',
				html: mensajeAdvertencias + '<br/><p class="mt-3">¿Desea continuar de todas formas?</p>',
				showCancelButton: true,
				confirmButtonText: 'Sí, continuar',
				cancelButtonText: 'No'
			});
			if (!confAdvertencias.isConfirmed) return;
		}

	} catch (err) {
		if ($.unblockUI) $.unblockUI();
		Swal.fire({ 
			icon: 'error', 
			title: 'Error al validar trámite', 
			text: (err && err.message) ? err.message : 'Ocurrió un error al validar el trámite.' 
		});
		return;
	}

	// 4) Confirmación final (después de validación exitosa)
	const conf = await Swal.fire({
		icon: 'warning',
		title: 'Finalizar trámite',
		text: 'Al terminar el trámite, no podrá modificar la captura. ¿Está seguro que desea continuar?',
		showCancelButton: true,
		confirmButtonText: 'Sí, finalizar',
		cancelButtonText: 'No'
	});
	if (!conf.isConfirmed) return;

	try {
		if (window.$ && $.blockUI) {
			$.blockUI({ message: '<h5>Finalizando trámite...</h5>' });
		}

		await enviaFinalizarTramite();

		const pdoc = (typeof parent !== 'undefined') ? parent.document : null;
		const btnSend = pdoc ? pdoc.getElementById('pb_send') : null;

		if (!btnSend) {
			if ($.unblockUI) $.unblockUI();
			Swal.fire({ icon: 'error', title: 'No se encontró pb_send', text: 'No fue posible completar el envío.' });
			return;
		}

		btnSend.disabled = false;
		btnSend.click();

	} catch (err) {
		if ($.unblockUI) $.unblockUI();
		Swal.fire({ icon: 'error', title: 'Error al finalizar', text: (err && err.message) ? err.message : 'Ocurrió un error al finalizar el trámite.' });
	}
};


const enviaFinalizarTramite = function() {
	return $.ajax({
		url: API.FIN,
		method: 'POST',
		data: {
			"folio": getFolioParaCarga() || $('#folio').val(),
		}
	}).then(r => {
		if (r && r.error) return Promise.reject(new Error(r.error));
		return r;
	}).fail(xhr => {
		const msg = (typeof errorMsg === 'function') ? errorMsg(xhr) : 'Error en encabezado';
		throw new Error(msg);
	});
}

const onCancelarClick = async function(e) {
	e.preventDefault();

	// Confirmación de cancelación
	const conf = await Swal.fire({
		icon: 'warning',
		title: 'Descartar trámite',
		text: '¿Estás seguro de que deseas descartar este trámite? No podrás recuperarlo.',
		showCancelButton: true,
		confirmButtonText: 'Sí, descartar',
		cancelButtonText: 'Cancelar'
	});

	if (!conf.isConfirmed) return; // si no confirma, sale

	// Llama a la función para descartar el trámite
	descartarTramite('DELETE');
}

const descartarTramite = function(method) {

	$.blockUI({ message: '<h5>Descartando trámite...</h5>' });

	$.ajax({
		url: API.ENC + '?folio=' + encodeURIComponent(getFolioParaCarga() || $('#folio').val()),
		method: 'DELETE',

	}).then(r => {
		const pdoc = (typeof parent !== 'undefined') ? parent.document : null;
		pdoc.forms["frmCancel"].submit();
	}).fail(xhr => {
		$.unblockUI();
		const msg = (typeof errorMsg === 'function') ? errorMsg(xhr) : 'Error en encabezado';
		Swal.fire({
			icon: 'error',
			title: 'Error al descartar',
			text: msg
		});
		throw new Error(msg);

	});
}


// ==============================================================================================================================
// Manejo de envio de facturas
// ==============================================================================================================================

const showProgressBar = function() {
	$('#progressBarContainer').removeClass('d-none');
	$('#progressBar').css('width', '0%').attr('aria-valuenow', 0).text('0%');
}

const hideProgressBar = function() {
	$('#progressBarContainer').addClass('d-none');
}

const resetForm = function() {
	$('#fileUpload').val('');
	$('#sendButton').prop('disabled', false);
	hideProgressBar();
}


// Función para llenar la tabla de facturas inválidas
const populateErrorTable = function(failInvoices) {
	const $tableBody = $('#errorTableBody');
	$tableBody.empty();

	$.each(failInvoices, function(index, invoice) {
		const row = `<tr><td>${invoice.file}</td><td>${invoice.cause}</td></tr>`;
		$tableBody.append(row);
	});

	$('#errorTableContainer').removeClass('d-none');
	$('html, body').animate({
		scrollTop: $('#errorTableContainer').offset().top - 20 // Desplazar al inicio de la tabla
	}, 800);
}


const optionRetHtml = function(item, selectedId) {
	const id = String(item.idRetencion);
	const txt = `${item.descRetencion}`;
	const sel = String(selectedId ?? "") === id ? "selected" : "";
	const pct = item.porcentaje;
	const cod = item.codigoSAT || "";
	return `<option value="${escapeHtml(id)}" ${sel} data-pct="${escapeHtml(pct)}" data-codigosat="${escapeHtml(cod)}">${escapeHtml(txt)}</option>`;
};


const selectTipoSaiHtml = function(uuid, familiaKey, selectedId, idRetencion) {
	if (familiaKey != "001" && familiaKey != "002")
		familiaKey = "OTROS";

	const arr = RET_FAMILIAS[familiaKey] || [];
	const opts = [`<option value="">— Selecciona —</option>`]
		.concat(arr.map(it => optionRetHtml(it, selectedId)))
		.join("");
	return `<select class="form-select form-select-sm tipo-sai"
                 name="tipoSai_${familiaKey}_${idRetencion}"
				 id="${idRetencion}"
                 data-uuid="${escapeHtml(uuid)}"
                 data-familia="${escapeHtml(familiaKey)}">${opts}</select>`;
};

const sendInvoices = function() {
	const fileInput = $('#fileUpload')[0];
	const file = fileInput.files[0];

	if (!file) {
		Swal.fire({
			icon: 'warning',
			title: 'Atención',
			text: 'Por favor, seleccione un archivo ZIP para cargar.'
		});
		return;
	}

	const $sendButton = $(this);
	$sendButton.prop('disabled', true);
	showProgressBar();

	const formData = new FormData();
	formData.append('file', file);
	formData.append('tipoPago', tipoPago);
	formData.append('folioPago', folio);

	const errores = false;
	$.ajax({
		url: '../../invoice/batch-upload',
		type: 'POST',
		data: formData,
		processData: false,
		contentType: false,
		xhr: function() {
			const xhr = new window.XMLHttpRequest();
			xhr.upload.addEventListener('progress', function(evt) {
				if (evt.lengthComputable) {
					const percentComplete = (evt.loaded / evt.total) * 100;
					$('#progressBar').css('width', percentComplete + '%').attr('aria-valuenow', percentComplete).text(Math.round(percentComplete) + '%');
				}
			}, false);
			return xhr;
		}
	})
		.done(function(response) {
			if (response.failInvoices && response.failInvoices.length > 0) {
				Swal.fire({
					icon: 'error',
					title: '¡Errores!',
					html: `Se encontraron ${response.failInvoices.length} errores. <br>`
				}).then((result) => {
					populateErrorTable(response.failInvoices);
				});
			} else {
				Swal.fire({
					icon: 'success',
					title: '¡Éxito!',
					text: 'El archivo se ha subido y procesado correctamente.',
				});
			}
		})
		.fail(function(jqXHR, textStatus, errorThrown) {
			let errorMessage = "Hubo un error al subir el archivo.";
			if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
				errorMessage = jqXHR.responseJSON.message;
			} else if (errorThrown) {
				errorMessage = errorThrown;
			}

			Swal.fire({
				icon: 'error',
				title: 'Error',
				text: errorMessage
			});
		})
		.always(function() {

			refreshInvoicesDT();
			cargarEncabezadoOnLoad();
			resetForm();
		});
}

const normalizeRetencion = function(r) {
	return {
		uuid: r.uuid,
		tipo: r.cNombreRetencion,
		mImporteRetencion: Number(r.mImporteRetencion),
		tipoSai: "",
		importeSai: Number(0.00),
		idRetencion: Number(r.idRetencion),
		importeBase: Number(r.importeBruto)
	};
};

const renderRetencionesPendientes = function(rows) {
	const data = Array.isArray(rows) ? rows : [];
	const $card = $("#cardRetencionesPendientes");
	const $tbody = $("#tblRetencionesPendientes tbody");

	$tbody.empty();

	data.forEach(r => {
		const uuid = r.uuid ?? "";
		const tipo = r.tipo ?? "";
		const importe = Number(r.mImporteRetencion ?? 0); // importe SAT
		const base = Number(r.importeBase ?? 0);
		const tipoSai = r.tipoSai ?? "";
		const importeSai = Number(r.importeSai ?? 0);
		const idRetencion = Number(r.idRetencion);
		const dif = importe - importeSai;

		const tr = `
      <tr data-uuid="${escapeHtml(idRetencion)}" data-base="${base}" data-importe="${importe}">
        <td class="align-middle">${escapeHtml(uuid)}</td>
        <td class="align-middle" style="min-width: 140px;">${tipo}</td>
        <td class="text-end align-middle" style="min-width: 140px;">${fmtMoneyMx(base)}</td>
        <td class="text-end align-middle" style="min-width: 140px;">${fmtMoneyMx(importe)}</td>
        <td class="align-middle" style="min-width: 180px;">
          ${selectTipoSaiHtml(uuid, tipo, tipoSai, idRetencion)}
        </td>
        <td class="text-end align-middle js-importe-sai">${fmtMoneyMx(importeSai)}</td>
        <td class="text-end align-middle fw-semibold js-dif">${fmtMoneyMx(dif)}</td>
        <td class="align-middle">
          <a href="#" onclick="onAgregarRetClick( ${idRetencion}, '${escapeHtml(uuid)}');return false;" title="Guardar">
            <i class="fa-solid fa-floppy-disk"></i>
          </a>
        </td>
      </tr>
    `;
		$tbody.append(tr);
	});

	$card.toggleClass("d-none", data.length === 0);

	// Delegado: cuando cambie el select de tipo SAI, recalcula Importe SAI = base * pct
	// Requiere que cada <option> tenga data-pct (ya viene del backend, ej. 0.1 para 10%)
	$tbody.off('change', '.tipo-sai').on('change', '.tipo-sai', function() {
		const $sel = $(this);
		const $tr = $sel.closest('tr');

		// Lee base/importe del renglón (los mismos valores que llegaron en el AJAX)
		const base = Number($tr.data('base')) || 0;
		const importe = Number($tr.data('importe')) || 0;

		// Porcentaje elegido en el option seleccionado
		const pct = parseMonto($sel.find('option:selected').data('pct')) || 0;

		// Calcula y redondea a 2 decimales
		const nuevoImporteSai = +(base * pct).toFixed(2);
		const nuevaDif = +(importe - nuevoImporteSai).toFixed(2);

		// Actualiza celdas visibles
		$tr.find('td.js-importe-sai').text(fmtMoneyMx(nuevoImporteSai));
		$tr.find('td.js-dif').text(fmtMoneyMx(nuevaDif));
 
		$tr.data('importeSai', nuevoImporteSai);
	});
};


const fmtMoneyMx = (n) =>
	new Intl.NumberFormat("es-MX", { style: "currency", currency: "MXN", minimumFractionDigits: 2 }).format(Number(n || 0));


const escapeHtml = (s) => String(s ?? "")
	.replace(/&/g, "&amp;").replace(/</g, "&lt;")
	.replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");

const loadRetencionesPendientes = function(tipoPago, folioPago) {
	$("#tblRetencionesPendientes tbody").empty().append(
		`<tr><td colspan="7" class="text-center py-3">Cargando…</td></tr>`
	);

	return $.ajax({
		url: API.DT_INVOICE_RET,
		method: "GET",
		dataType: "json",
		data: { tipoPago: String(tipoPago || ""), folioPago: String(folioPago || "") }
	})
		.done(async function(resp) {
			console.log(resp);
			const rows = normalizeList(resp);
			if (rows.length > 0) {
				await loadCatalogoRetenciones();
				renderRetencionesPendientes(rows);
			} else {
				$("#tblRetencionesPendientes tbody").empty();
				$("#cardRetencionesPendientes").addClass("d-none");
			}
		})
		.fail(function(xhr) {
			$("#tblRetencionesPendientes tbody").empty();
			let msg = "Error al consultar retenciones.";
			try {
				const j = JSON.parse(xhr.responseText || "{}");
				msg = j.message || j.error || j.cause || msg;
			} catch (_) { }
			Swal.fire({ icon: "error", title: "Oops", text: msg });
		});
};




// ===== Catálogo: carga y agrupa por familia =====
const loadCatalogoRetenciones = function() {
	return $.ajax({
		url: API.CAT_RETENCIONES,
		method: "GET",
		dataType: "json"
	}).done(function(list) {

		CAT_RETENCIONES = Array.isArray(list) ? list : [];
		RET_FAMILIAS["001"] = [];
		RET_FAMILIAS["002"] = [];
		RET_FAMILIAS["OTROS"] = [];

		CAT_RETENCIONES.forEach(it => {
			const codigo = (it.codigoSAT || "").toString().trim();
			if (codigo === "001") RET_FAMILIAS["001"].push(it);
			else if (codigo === "002") RET_FAMILIAS["002"].push(it);
			else RET_FAMILIAS["OTROS"].push(it);
		});
	}).fail(function(xhr) {
		let msg = "No se pudo cargar el catálogo de retenciones.";
		try { const j = JSON.parse(xhr.responseText || "{}"); msg = j.message || j.error || msg; } catch (_) { }
		Swal.fire({ icon: "error", title: "Catálogo", text: msg });
	});
};


const normalizeList = function(arr) {
	if (!Array.isArray(arr)) return [];
	return arr.map(normalizeRetencion);
};

/**
 * Valida que el trámite esté completo y correcto antes de enviar a finalizar
 * Llama al servlet GET ../../api/suficiencia/finalizar?folio={folio}
 * @param {number} folio - Folio del trámite a validar
 * @returns {Promise<Object>} { valido: boolean, errores: [], advertencias: [] }
 */
const validarTramiteCompleto = async function(folio) {
	return new Promise((resolve, reject) => {
		$.ajax({
			url: `${API.FIN}?folio=${folio}`,
			method: 'GET',
			dataType: 'json',
			success: function(response) {
				// Response structure: { valido: boolean, folio: int, errores: [], advertencias: [], conteoErrores: int, conteoAdvertencias: int }
				resolve(response || { valido: false, errores: [], advertencias: [] });
			},
			error: function(xhr, status, error) {
				console.error('Error en validación:', error, xhr);
				const errorMsg = (xhr && xhr.responseJSON && xhr.responseJSON.mensaje) 
					? xhr.responseJSON.mensaje 
					: `Error al validar (${xhr && xhr.status ? xhr.status : 'desconocido'})`;
				reject(new Error(errorMsg));
			}
		});
	});
};

/**
 * Formatea un número sin el símbolo de moneda, usando separadores de miles y dos decimales.
 * @param {number} amount - El número a formatear.
 * @returns {string} El número formateado como string (ej. "17,241.38").
 */
const formatNumber = function(amount) {
	if (typeof amount !== 'number') {
		amount = Number(amount) || 0;
	}

	return new Intl.NumberFormat('es-MX', {
		style: 'decimal',
		minimumFractionDigits: 2,
		maximumFractionDigits: 2
	}).format(amount);
};


const moneyRender = function(data, type) {
	if (type === 'display' || type === 'filter') return formatNumber(data);
	return data;
};

function resetCamposCalendario() {
	calendarioDisponibleNeto = {};

	MESES_CONFIG.forEach(function(m) {
		$(m.disp).val('0.00');
		$(m.cal).val('0.00').prop('readonly', false);
	});
}

/**
 * Aplica la validación de diaUsarMesSiguiente al abrir el diálogo de calendario
 * Si diaUsarMesSiguiente > 0 y >= día de hoy, inhabilita los meses anteriores al mes actual.
 * Por ejemplo: si diaUsarMesSiguiente=25 y hoy es 25 de enero, inhabilita los meses anteriores a enero.
 * Si es diciembre, solo permite capturar en diciembre (único mes del año disponible).
 */
const aplicarValidacionMesSiguiente = function() {
	if (diaUsarMesSiguiente > 0 && diaUsarMesSiguiente >= diaHoy) {
		const mesActual = hoy.getMonth() + 1;
		MESES_CONFIG.forEach(function(m) {
			if (m.num < mesActual) {
				$(m.cal).prop('readonly', true);
			}
		});
	}
};

const abrirDialogoCalendarioEP = function() {
	const rowSel = (typeof dtDisponible !== 'undefined')
		? dtDisponible.row('.selected')
		: null;

	if (!rowSel || !rowSel.any || !rowSel.any()) {
		Swal.fire({
			icon: 'warning',
			title: 'Selecciona una EP',
			text: 'Debes seleccionar un renglón antes de calendarizar.'
		});
		return;
	}

	// Validar que el recurso global no esté ya calendarizado
	const montoTotal = getMontoTotalTramite();
	const totalEPs = getTotalEPsCalendarizado();
	if (montoTotal > 0 && totalEPs >= (montoTotal - 0.000001)) {
		Swal.fire({
			icon: 'info',
			title: 'Recurso calendarizado',
			text: 'El recurso del trámite ya está completamente calendarizado. No es posible agregar más EPs.'
		});
		return;
	}

	const data = rowSel.data();

	const ep = data.EP || data[0];

	if (!ep) {
		Swal.fire({
			icon: 'error',
			title: 'EP no encontrada',
			text: 'No se pudo obtener la EP de la fila seleccionada.'
		});
		return;
	}

	$('#epSelected').val(ep);

	resetCamposCalendario();


	$.ajax({
		url: API.API_CALENDARIO_EP,
		type: 'GET',
		dataType: 'json',
		data: {
			ep: ep,
			login: login,
			mes: mes,
			folio: folio
		}
	})
		.done(function(resp) {
			resetCamposCalendario();

			const lista = (resp && Array.isArray(resp.calendario)) ? resp.calendario : [];

			let firstEditable = null;

			lista.forEach(function(item) {
				const mesNum = item.mes;
				const base = Number(item.base || 0);

				const cfg = MESES_CONFIG.find(function(m) { return m.num === mesNum; });
				if (!cfg) return;

				calendarioDisponibleNeto[mesNum] = base;

				$(cfg.disp).val(formatNumber(base));

				const $cal = $(cfg.cal);

				if (base === 0) {
					$cal.val('0.00').prop('readonly', true);
				} else {
					$cal.prop('readonly', false);
					if (!firstEditable) {
						firstEditable = $cal;
					}
				}
			});

			if (firstEditable) {
				firstEditableCalInputId = firstEditable.attr('id') || null;
			} else {
				firstEditableCalInputId = null;
			}

			// Aplicar validación de diaUsarMesSiguiente
			aplicarValidacionMesSiguiente();

			const modalEl = document.getElementById('modalCalendarioEP');
			$(modalEl)
				.off('shown.bs.modal.calendario')
				.on('shown.bs.modal.calendario', function() {
					if (firstEditableCalInputId) {
						const $input = $('#' + firstEditableCalInputId);
						if ($input.length) {
							$input.trigger('focus').select();
						}
					}
				});

			const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
			modal.show();
		})
		.fail(function(xhr) {
			console.log('Error al consultar calendario disponible:', xhr);
			Swal.fire({
				icon: 'error',
				title: 'Error',
				text: 'No fue posible obtener el calendario disponible para la EP seleccionada.'
			});
		});
}