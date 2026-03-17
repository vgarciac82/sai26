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

const SYSTEM_URL = window.location.protocol +
	"//" +
	window.location.host +
	"/" +
	window.location.pathname
		.split("/")[1];

let tableCFDI;

$(document).ready(function() {
	tableCFDI = $('#tablaCfdi').DataTable({
		"ajax": {
			"url": SYSTEM_URL +
				"/crud?rt=nt&ql=vw_CFDI_Encabezado&qw=EstatusID=0",
			type: "POST",
		},
		columns: [
			{ data: "nfoliopago" },
			{ data: "crfcreceptor" },
			{ data: "cnombrereceptor" },
			{
				data: "mtotal",
				orderable: false,
				searchable: false
			},
			{ data: "dfechaemision" },
			{
				data: null,
				orderable: false,
				searchable: false,
				orderable: false,
				render: function(data, type, row) {
					return `
                <div class="btn-group">
                    <button class="btn btn-secondary btn-sm m-1" title="Editar" onclick="editarCFDI('${row.nfoliopago}')">
                        <i class="fas fa-pencil-alt"></i>
                    </button>
                    <button class="btn btn-secondary btn-sm m-1" title="Vista previa" onclick="vistaPreviaCFDI('${row.nfoliopago}')">
                        <i class="fas fa-file-alt"></i>
                    </button>
					<button class="btn btn-danger btn-sm m-1" title="Eliminar" onclick="eliminarCFDI('${row.nfoliopago}')">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </div>
            `;
				}
			}
		]
		,
		order: [[1, "asc"]],
		rowId: "nfoliopago",
		processing: true,
		serverSide: true,
		scrollCollapse: true,
		scrollY: "400px",
		language: es_mx,
	});

	$('#tablaCfdi tbody').on('click', 'tr', function() {
		$(this).toggleClass('selected');
	});

	$('[data-bs-toggle="tooltip"]').tooltip();
});


const newCFDI = function() {
	parent.window.frames["content-iframe"].location.href =
		"capturaFactura.jsp"
}

const editarCFDI = function(idInvoice) {
	parent.window.frames["content-iframe"].location.href =
		"capturaFactura.jsp?id="+idInvoice
}
