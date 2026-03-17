let dtDetalleCompromiso;

const SYSTEM_URL = window.location.protocol +
	"//" +
	window.location.host +
	"/" +
	window.location.pathname
		.split("/")[1];


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

const creaDTDocumentos = function() {
	$("#tblFiles").DataTable({
		paging: false,

		searching: false,

		ordering: false,

		info: false,

		// Habilitar el desplazamiento horizontal con 100% de ancho (sScrollX: "100%")
		scrollX: "100%",


		destroy: true,

		retrieve: true,

		language: es_mx
	});
}

const getDocuments = function() {
	$.ajax({
		url: '../fortimax/documents',
		dataType: 'json',
		type: "GET",
		data: {
			"accion": "send_tree"
		},
		async: true,
		success: function(objResp) {
			var nodos = objResp.nodos;
			for (var i in nodos) {
				nodo = nodos[i];
				$("#bodyDoctos")
					.append("<tr>"
						+ "<td>" + nodo.nombreDocumento + "</td>"
						+ "<td><a style='color:black;' href=\"#\" onclick=\"muestraDocumento('" + nodo.fortimax + "')\">" + nodo.path + "</a></td>"
						+ "</tr>");
			}
		},
		error: function(xhr, textStatus, errorThrown) {
			try {
				var obj = eval(xhr.responseText);
				var msg = obj.errCause;
				alert("No fue posible consultar los documentos debido al error: " + msg);
			} catch (e) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
			}

			$.unblockUI();
		}
	});
}

const creaDT = function(folio) {


	dtDetalleCompromiso = $('#tablaPresupuestoComprometido').DataTable({
		ajax: {
			url: SYSTEM_URL + '/crud?rt=nt&ql=v_Resumen_Detalle_Compromiso&qw=nfoliocompromiso=' + folio,
			type: 'POST'
		},
		columns: [
			{ data: 'ep' },
			{
				data: 'importeEnero',
				render: function(data, type, row) {
					return formatoNumero(row.importeEnero);
				}

			},
			{
				data: 'importeFebrero',
				render: function(data, type, row) {
					return formatoNumero(row.importeFebrero);
				}
			},
			{
				data: 'importeMarzo',
				render: function(data, type, row) {
					return formatoNumero(row.importeMarzo);
				}
			},
			{
				data: 'importeAbril',
				render: function(data, type, row) {
					return formatoNumero(row.importeAbril);
				}
			},
			{
				data: 'importeMayo',
				render: function(data, type, row) {
					return formatoNumero(row.importeMayo);
				}
			},
			{
				data: 'importeJunio',
				render: function(data, type, row) {
					return formatoNumero(row.importeJunio);
				}
			},
			{
				data: 'importeJulio',
				render: function(data, type, row) {
					return formatoNumero(row.importeJulio);
				}
			},
			{
				data: 'importeAgosto',
				render: function(data, type, row) {
					return formatoNumero(row.importeAgosto);
				}
			},
			{
				data: 'importeSeptiembre',
				render: function(data, type, row) {
					return formatoNumero(row.importeSeptiembre);
				}
			},
			{
				data: 'importeOctubre',
				render: function(data, type, row) {
					return formatoNumero(row.importeOctubre);
				}
			},
			{
				data: 'importeNoviembre',
				render: function(data, type, row) {
					return formatoNumero(row.importeNoviembre);
				}
			},
			{
				data: 'importeDiciembre',
				render: function(data, type, row) {
					return formatoNumero(row.importeDiciembre);
				}
			},
			{
				data: null,
				title: 'Total',
				render: function(data, type, row) {
					const total = (
						Number(row.importeEnero || 0) +
						Number(row.importeFebrero || 0) +
						Number(row.importeMarzo || 0) +
						Number(row.importeAbril || 0) +
						Number(row.importeMayo || 0) +
						Number(row.importeJunio || 0) +
						Number(row.importeJulio || 0) +
						Number(row.importeAgosto || 0) +
						Number(row.importeSeptiembre || 0) +
						Number(row.importeOctubre || 0) +
						Number(row.importeNoviembre || 0) +
						Number(row.importeDiciembre || 0)
					);

					return formatoNumero(total);
				}
			}
		],
		order: [[1, 'asc']],
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
}

const consultarContratoPorFolio = function(folioCompromiso) {
	$.ajax({
		url: '/sai/crud',
		method: 'POST',
		data: {
			rt: 's',
			ql: 'ResumenCompromisoRead',
			folioCompromiso: folioCompromiso
		},
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		dataType: 'json',
		success: function(resp) {

			if (resp && String(resp.success) === 'true' && Array.isArray(resp.data_1) && resp.data_1.length > 0) {
				const r = resp.data_1[0];
				$("#claveContrato").val(r.claveContrato);
				$("#tipoContrato").val(r.tipoContrato);
				$("#tipoDocumento").val(r.tipoDocumento);
				$("#unidadAdministrativa").val(r.unidadAdministrativa);
				$("#tipoAdjudicacion").val(r.tipoAdjudicacion);
				$("#ruc").val(r.ruc);
				$("#nombre").val(r.nombre);
				$("#conceptoContrato").val(r.conceptoContrato);
				$("#montoContrato").val(formatoNumero(r.montoContrato));
				$("#compromisoActual").val(formatoNumero(r.compromisoActual));
			} else {
				Swal.fire({
					icon: 'error',
					title: 'No se pudo completar la operación',
					text: resp.message || 'Ocurrió un error inesperado.',
					confirmButtonText: 'Entendido'
				});
				return;
			}
		},
		error: function(xhr, status, err) {
			console.error('Error en la petición:', status, err, xhr?.responseText);
		}
	});
}

const formatoNumero = n => Number(n).toLocaleString('es-MX', {
	minimumFractionDigits: 2,
	maximumFractionDigits: 2
});



const muestraDocumento = function(fortimaxNode) {
	openCenteredWindow("../filestore?select=" + fortimaxNode, "_blank", 800, 1024);
}

const openCenteredWindow = function(url, name, height, width, parms) {
	var left = Math.floor((screen.width - width) / 2);
	var top = Math.floor((screen.height - height) / 2);
	var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes,resizable=1";
	if (parms) {
		winParms += "," + parms;
	}
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) {
		win.window.focus();
	}
	return win;
}

const regresar = function() {
	location.href = "../Generador/AutorizaGeneracionLayouts.jsp?TYPE=" + tipoAutorizacion;
}