'use strict';

// ====== Rutas (con contexto) ======
const CONTEXTO = window.location.pathname.split('/')[1] || '';
const BASE = `${window.location.protocol}//${window.location.host}/${CONTEXTO}`;

const API_LISTA = `${BASE}/crud?rt=nt&ql=v_SuficienciaPendiente&qw=isnull( nEnviadoSICOP,0) = 0 and isnull(nEnviadoSicopSuficiencia,0) = 0 AND (cTipoContrato IN ('FE', 'RE', 'RI', 'PD', 'AV') )`;
const API_ENVIAR = `${BASE}/compromiso/solicitaFirma`;  

// ====== Estado ======
const selectedIds = new Set();
let dt = null;

// ====== Helpers ======
function sincronizarChecksEnVista() {
	$('#tabla-contratos tbody input.row-select').each(function() {
		const id = String($(this).data('id'));
		const marcado = selectedIds.has(id);
		$(this).prop('checked', marcado);
		$(this).closest('tr').toggleClass('selected-row', marcado);
	});
}

function actualizarSelectAll() {
	const filas = $('#tabla-contratos tbody input.row-select');
	if (filas.length === 0) { $('#selectAll').prop('checked', false); return; }
	const todasMarcadas = filas.length > 0 && filas.filter(':checked').length === filas.length;
	$('#selectAll').prop('checked', todasMarcadas);
}

// ====== Init ======
$(document).ready(function() {
	// DataTable
	dt = new DataTable('#tabla-contratos', {
		processing: true,
		serverSide: true,
		ajax: {
			url: API_LISTA,
			type: 'GET',
			dataSrc: function(json) { return json.data || json; }
		},
		order: [[1, 'asc']],
		columns: [
			{
				data: 'nFolioCompromiso',
				orderable: false,
				searchable: false,
				className: 'text-center',
				render: function(id/*, type, row*/) {
					const checked = selectedIds.has(String(id)) ? 'checked' : '';
					return `<input type="checkbox" class="form-check-input row-select" data-id="${id}" ${checked} />`;
				}
			},
			{ data: 'cIdContrato' },
			{ data: 'cTipoContrato' },
			{ data: 'folioInterno' },
			{ data: 'cUnidadResponsable' },
			{ data: 'dRFC' },
			{ data: 'razon_social' },
			{ data: 'Descripcion' },
			{
				data: 'Importe',
				className: 'text-end',
				render: function(val) {
					if (val == null) return '';
					const num = Number(val);
					return isNaN(num) ? val : num.toLocaleString('es-MX', { style: 'currency', currency: 'MXN' });
				}
			}
		],
		drawCallback: function() {
			sincronizarChecksEnVista();
			actualizarSelectAll();
		}
	});

	// Select/Deselect all (vista actual)
	$('#selectAll').on('change', function() {
		const marcar = $(this).is(':checked');
		$('#tabla-contratos tbody input.row-select').each(function() {
			const id = String($(this).data('id'));
			$(this).prop('checked', marcar);
			if (marcar) {
				selectedIds.add(id);
				$(this).closest('tr').addClass('selected-row');
			} else {
				selectedIds.delete(id);
				$(this).closest('tr').removeClass('selected-row');
			}
		});
	});

	// Toggle por fila
	$('#tabla-contratos tbody').on('change', 'input.row-select', function() {
		const id = String($(this).data('id'));
		if ($(this).is(':checked')) {
			selectedIds.add(id);
			$(this).closest('tr').addClass('selected-row');
		} else {
			selectedIds.delete(id);
			$(this).closest('tr').removeClass('selected-row');
		}
		actualizarSelectAll();
	});

	// Validar y limitar justificacion_adicional a 1500 caracteres
	$('#justificacion_adicional').on('input', function() {
		let valor = $(this).val();
		if (valor.length > 1500) {
			$(this).val(valor.substring(0, 1500));
		}
	});

	$('#btnLimpiar').on('click', function(e) {
		e.preventDefault();
		selectedIds.clear();
		$('#selectAll').prop('checked', false);
		if (dt) dt.draw(false);
		$('#empleado').val('');
		$('#nombre').val('');
		$('#cargo').val('');
		$('#justificacion_adicional').val('');
		$('#tabla-resultado tbody').empty();
		$('#card-resultado').hide();

	});

	// ====== Enviar Solicitud de Firma ======
	$('#btnSolicitarFirma').on('click', async function(e) {
		e.preventDefault();
		if (selectedIds.size === 0) {
			Swal.fire({ icon: 'warning', title: 'Sin selección', text: 'Seleccione al menos un contrato.' });
			return;
		}

		const idsNumericos = Array.from(selectedIds)
			.map(v => parseInt(v, 10))
			.filter(n => !Number.isNaN(n));

		const empleadoAut = parseInt($('#empleado').val() || '0', 10);

		if (empleadoAut === '0') {
			Swal.fire({ icon: 'warning', title: 'Sin firmante', text: 'Seleccione el firmante.' });
			return;
		}

		const confirm = await Swal.fire({
			icon: 'question',
			title: '¿Confirmar solicitud de firma?',
			html: `Se enviará solicitud para <b>${idsNumericos.length}</b> contrato(s).`,
			showCancelButton: true,
			confirmButtonText: 'Sí, continuar',
			cancelButtonText: 'Cancelar'
		});

		if (!confirm.isConfirmed) return;

		const $btn = $('#btnSolicitarFirma');
		const justificacionAdicional = ($('#justificacion_adicional').val() || '').trim();
		const payload = {
			folioList: idsNumericos,
			employeeAut: empleadoAut,
			justificacionAdicional: justificacionAdicional
		};

		Swal.fire({
			title: 'Enviando...',
			text: 'Por favor espere',
			allowOutsideClick: false,
			didOpen: () => { Swal.showLoading(); }
		});

		$btn.prop('disabled', true);
		$('#tabla-resultado tbody').empty();
		$('#card-resultado').hide();

		$.ajax({
			url: API_ENVIAR,
			type: 'POST',
			contentType: 'application/json; charset=UTF-8',
			dataType: 'json',
			data: JSON.stringify(payload)
		})
			.done(function(resp) {
				console.log('Respuesta de solicitar firma:', resp);
				Swal.fire({ icon: 'success', title: 'Listo', text: 'Solicitud enviada. Revisa los resultados para cada tramite.' });
				renderResultadoSolicitud(resp);
			})
			.fail(function(xhr) {
				console.log('Error en solicitar firma:', xhr?.responseText || xhr?.statusText || xhr);
				Swal.fire({ icon: 'error', title: 'Error', text: 'No fue posible enviar la solicitud.' })
					.then(function() {
						const $card = $('#card-resultado');
						const $tabla = $('#tabla-resultado');

						if (!$tabla.attr('tabindex')) {
							$tabla.attr('tabindex', '-1');
						}

						$card.show();
						$tabla.focus();

						const cardElem = $card.get(0);
						if (cardElem && cardElem.scrollIntoView) {
							cardElem.scrollIntoView({ behavior: 'smooth', block: 'start' });
						}
					});
			})
			.always(function() {
				$btn.prop('disabled', false);
				selectedIds.clear();  // Limpiar seleccion anterior
				if (dt) {
					dt.ajax.reload(null, false);
				}
			});
	});

	querySelectPost('FirmantesPorTipo_Read', 'empleado', { async: true });
});

const getEmployeeRole = function() {
	queryFormPost("PuestoEmpleadoRead", { async: true });
}


const renderResultadoSolicitud = function(resp) {
	try {
		const $card = $('#card-resultado');
		const $tbody = $('#tabla-resultado tbody');
		$tbody.empty();


		const lista = (resp && Array.isArray(resp.result)) ? resp.result : [];
		if (lista.length === 0) {
			$tbody.append('<tr><td colspan="3" class="text-center text-muted">Sin resultados</td></tr>');
			$card.show();
			return;
		}


		lista.forEach(function(item) {
			const folio = item.folio != null ? item.folio : '';
			const status = (item.status || '').toLowerCase();
			const causa = item.errorCause != null ? item.errorCause : '';


			const badgeClass = status === 'success' ? 'bg-success' : (status === 'fail' ? 'bg-danger' : 'bg-secondary');
			const estatusHtml = '<span class="badge ' + badgeClass + '">' + (status || 'N/A') + '</span>';


			const tr = '<tr>' +
				'<td>' + folio + '</td>' +
				'<td>' + estatusHtml + '</td>' +
				'<td>' + causa + '</td>' +
				'</tr>';
			$tbody.append(tr);
		});


		$card.show();
	} catch (e) {
		console.log('Error al renderizar resultado:', e);
	}
}