/*********************************************************
 *  CONFIGURACIÓN
 *********************************************************/

var tiposPago = {
	"DI": "PAGODIRECTO",
	"DV": "PAGODIVERSO",
	"FE": "PAGOFEDERALIZADO",
	"RG": "RELACIONGASTOS",
	"PO": "PAGOOBRA",
	"AX": "ANEXO1",
	"IF": "REINTEGROINGRESO",
	"PC": "PAGOPENASCONV",
	"CA": "CAJA",
	"CV": "COMSINVIATICOS",
	"PM": "POLIZA",
	"OA": "OPERAJENAS",
	"RC": "REINTEGROCAJA",
	"CO": "COMPROMISO"
};

// Instancias Bootstrap Modal
let modalFiel = null;
let modalLog = null;

// DataTable
let oTable = null;

/*********************************************************
 *  DOCUMENT READY
 *********************************************************/
$(document).ready(function () {

	init();
	creaDialogoSeleccion();    
	creaDialogoLog();        

	$("#u_Login").val(cLogin);
	$("#RFC").val(RFC);

	if (($("#cUR").val() != "A02" && $("#cUR").val() != "A03") || esSAIAlterno)
		querySelectPost("cUnidadEjecutoraVistasTesoreria", "uEjecutora", { async: false });
	else
		querySelectPost("cUnidadEjecutoraRead", "uEjecutora", { async: false });

	$("#uEjecutora").val(cUR);

	cargaGrid();

	if (mensaje !== "")
		Swal.fire({ icon: "warning", text: mensaje });

	$("#chkTodos").on("change", function () {
		let checked = this.checked;
		$("#dt_AutorizarLayouts tbody input[type='checkbox']").prop("checked", checked);
	});
});

/*********************************************************
 *  CREACIÓN MODAL LOG (ANTES jQuery UI)
 *********************************************************/
function creaDialogoLog() {

	if (mostrarResultado) {
		$("#logTable").css("display", "block");
		if (fielExpiringSoon)
			$("#fielWarning").css("display", "block");
	}

	modalLog = new bootstrap.Modal(document.getElementById("dlg-Msg"), {
		backdrop: "static",
		keyboard: false
	});

	if (mostrarResultado) {
		modalLog.show();
	}
}

/*********************************************************
 *  CREACIÓN MODAL FIEL (ANTES jQuery UI)
 *********************************************************/
function creaDialogoSeleccion() {

	const modalEl = document.getElementById("dlg-FIEL");

	modalFiel = new bootstrap.Modal(modalEl, {
		backdrop: "static",
		keyboard: false
	});

	modalEl.addEventListener("shown.bs.modal", function () {
		$(".dlgFielInpt").val(""); // limpiar inputs
	});
}

/*********************************************************
 *  TABLA PRINCIPAL
 *********************************************************/
function cargaGrid() {

	$("#cWhere").val(generaCondicion());

	let vista = "";
	if (tipoAutorizacion === "VOBO")
		vista = "vListaPagosVoBo";
	else if (tipoAutorizacion === "AUT")
		vista = "vListaPagosAut";

	if (!vista) return;

	let baseUrl = window.location.protocol + "//" + window.location.host + "/" +
		window.location.pathname.split("/")[1];

	let ajaxUrl = baseUrl + "/crud?rt=nt&ql=" + vista +
		"&qw=" + encodeURIComponent($("#cWhere").val() || "");

	if (!$.fn.dataTable.isDataTable("#dt_AutorizarLayouts")) {

		oTable = $("#dt_AutorizarLayouts").DataTable({
			processing: true,
			serverSide: true,
			ajax: { url: ajaxUrl, type: "POST" },
			pageLength: 20,
			lengthChange: true,
			searching: true,
			ordering: true,
			info: true,
			autoWidth: false,
			pagingType: "full_numbers",

			columns: [
				{
					data: "id",
					orderable: false,
					searchable: false,
					className: "text-center",
					render: function (data, type) {
						return (type === "display")
							? `<input type="checkbox" id="${data}" />`
							: data;
					}
				},
				{ data: "cUnidadResponsable", className: "text-start" },
				{ data: "Folio", className: "text-start" },
				{ data: "caNoContrarrecibo", className: "text-start" },
				{ data: "cnombre", className: "text-start" },
				{ data: "mImporteNeto", className: "text-start" },
				{ data: "CTAB", className: "text-start" },
				{ data: "cConcepto", className: "text-start" },
				{ data: "fAplicacion", className: "text-start" }
			],

			language: {
				processing: "Procesando...",
				lengthMenu: "Mostrar _MENU_ registros",
				zeroRecords: "No hay registros a mostrar",
				emptyTable: "No se encontraron resultados",
				loadingRecords: "Cargando...",
				info: "Registros _START_ al _END_ de _TOTAL_",
				infoEmpty: "Registro 0 al 0 de 0",
				infoFiltered: "(filtrado de _MAX_ totales)",
				search: "Buscar:",
				paginate: {
					first: "Primero",
					previous: "Ant.",
					next: "Sigte.",
					last: "Último"
				}
			}
		});

		/**** EVENTOS ROW CLICK ***/
		$("#dt_AutorizarLayouts tbody")
			.on("click", "tr", function () {
				$("#dt_AutorizarLayouts tr").removeClass("row_selected");
				$(this).addClass("row_selected");
			})
			.on("dblclick", "tr", function () {
				let data = oTable.row(this).data();
				if (!data) return;

				let f = data.Folio;
				let a = (tipoAutorizacion === "VOBO") ? "VoBoPago" : "AutPago";
				let d = tiposPago[$("input[name=cTipoPago]:checked").val()];

				let targetUrl = {
					"CAJA": "../Generador/ResumenCajaNoPresupuestal.jsp",
					"COMSINVIATICOS": "../Generador/ComisionesSinComprobacionFirma.jsp",
					"POLIZA": "../plantillasCasos/ResumenPolizaManualFIEL.jsp",
					"REINTEGROCAJA": "../Generador/ResumenReintegrosCaja.jsp",
					"COMPROMISO": "../Generador/ResumenCompromiso.jsp"
				}[d] || "../Generador/ResumenPagos.jsp";

				location.href = `${targetUrl}?a=${a}&f=${f}&d=${d}`;
			});

	}
	else {
		oTable.ajax.url(ajaxUrl).load();
	}
}

/*********************************************************
 *  GENERAR WHERE
 *********************************************************/
function generaCondicion() {

	let where = "";

	if ("cUR" != "A02" || esSAIAlterno) {
		where += " tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
	} else {
		where += " cUnidadResponsable IN (SELECT cUnidadRespnNumEmpleadoAutonsable FROM dbo.tCatUnidadResponsable WITH (NOLOCK) WHERE nAlcance=1)";
		where += " AND tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
	}

	where += (tipoAutorizacion === "VOBO")
		? " AND nNumEmpleadoVoBo = " + numeroEmpleado
		: " AND nNumEmpleadoAut = " + numeroEmpleado;

	return where;
}

/*********************************************************
 *  ABRIR MODAL FIEL DESDE BOTÓN AUTORIZAR
 *********************************************************/
function AutorizaLayouts() {

	const seleccionados = $("#dt_AutorizarLayouts tbody input[type='checkbox']:checked");

	if (seleccionados.length === 0) {
		return Swal.fire({
			icon: "warning",
			text: "Seleccione al menos un Pago para autorizar."
		});
	}

	let folios = [];
	seleccionados.each(function () { folios.push(this.id); });

	$("#nFolios").val(folios.join(","));

	modalFiel.show();
}

/*********************************************************
 *  CANCELAR / ACEPTAR (MODAL FIEL)
 *********************************************************/
function cancelarDlg() {
	$("#nFolios").val("");
	modalFiel.hide();
	$(".dlgFielInpt").val("");
}

function aceptarDlg() {

    let msgValidaciones = validaCamposCompletos();
    if (msgValidaciones !== "") {
        return Swal.fire({
            icon: "warning",
            text: msgValidaciones
        });
    }

    let msg = "Está a punto de aceptar el proceso de los pagos con folio: "
        + $("#nFolios").val();

    Swal.fire({
        title: '¿Desea continuar?',
        text: msg,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#288BA8',
        cancelButtonColor: '#e6e6e6',
        confirmButtonText: 'Aceptar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {

        if (!result.isConfirmed) {
            return;
        }

        $("#tipoPagoSeleccionado").val(tiposPago[$("input[name=cTipoPago]:checked").val()]);
        if ($("#tipoPagoSeleccionado").val() === "OPERAJENAS")
            $("#folder").val("Solicitud Firmada");
        else
            $("#folder").val("");

        modalFiel.hide();

        Swal.fire({
            title: "Procesando...",
            html: "<b>Espere por favor</b>",
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        $("#btn-Autoriza, #dlg-FIEL button").prop("disabled", true);

        $("#formFIEL").submit();
    });
}


/*********************************************************
 *  VALIDACIONES FIEL
 *********************************************************/
function validaCamposCompletos() {

	let msg = "";
	let token = "";

	if ($("#cerFile").val() === "") {
		msg = "Es necesario que adjunte su certificado.";
		token = "\n";
	} else if (!fileValidation(".cer", $("#cerFile").val())) {
		msg += token + "El certificado debe tener extensión .cer";
		token = "\n";
	}

	if ($("#keyFile").val() === "") {
		msg += token + "Es necesario que adjunte su llave privada.";
		token = "\n";
	} else if (!fileValidation(".key", $("#keyFile").val())) {
		msg += token + "La llave privada debe tener extensión .key";
		token = "\n";
	}

	if ($("#passwordLlave").val() === "")
		msg += token + "El password de su llave privada es requerido.";

	return msg;
}

function fileValidation(ext, filePath) {
	let reg = new RegExp("(" + ext + ")$", "i");
	return reg.test(filePath);
}

/*********************************************************
 *  MOSTRAR LOG (ANTES dialog)
 *********************************************************/
function muestraLog() {
	modalLog.show();
}

/*********************************************************
 *  INICIALIZACIÓN
 *********************************************************/
function init() {
	if (tipoAutorizacion === "VOBO")
		$("#tituloOperacion").text("Visto Bueno de Egreso");
	else if (tipoAutorizacion === "AUT")
		$("#tituloOperacion").text("Autoriza Egreso");
}
