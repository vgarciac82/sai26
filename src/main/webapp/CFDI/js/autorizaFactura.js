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

$(document).ready(function () {
    tableCFDI = $('#tablaCfdi').DataTable({
        "ajax": {
            "url": SYSTEM_URL +
                "/crud?rt=nt&ql=vw_CFDI_Encabezado",
            type: "POST",
        },
        columns: [
            { data: "nfoliopago" },
            { data: "crfcreceptor" },
            { data: "cnombrereceptor" },
            { data: "mtotal" },
            { data: "cserie" },
            { data: "cfolio" },
            { data: "cversion" },
            { data: "dfechaemision" }
        ],
        order: [[1, "asc"]],
        rowId: "nfoliopago",
        processing: true,
        serverSide: true,
        scrollCollapse: true,
        scrollY: "400px",
        language: es_mx,
    });

    $('#tablaCfdi tbody').on('click', 'tr', function () {
        $(this).toggleClass('selected');
    });

    $('[data-bs-toggle="tooltip"]').tooltip();
});


// Esqueleto de las funciones stamp() y cancelCFDI()
const stamp = function () {

    console.log("Timbrando CFDI(s) seleccionados...");
    if (!validarSeleccion()) {
        Swal.fire({
            icon: "warning",
            title: "No hay selección",
            text: "Seleccione al menos un comprobante para timbrar.",
        });
        return;
    }

    confirmarTimbrado().then((result) => {
        if (result.isConfirmed) {
            mostrarSpinner(true);
            enviarTimbradoCFDIs()
                .done((response) => {
                    Swal.fire({
                        icon: "success",
                        title: "Éxito",
                        text: "CFDIs timbrados correctamente.",
                    });
                    actualizarTabla();
                })
                .fail((jqXHR) => {
                    mostrarError(jqXHR);
                })
                .always(() => {
                    mostrarSpinner(false);
                });
        }
    });
}

const cancelCFDI = function () {
    // Lógica para eliminar (por implementar)
    console.log("Cancelando CFDI(s) seleccionados...");
}


// Función para validar si hay al menos un renglón seleccionado
const validarSeleccion = function () {
    return $(".selected").length > 0;
};

// Función de confirmación de SweetAlert
const confirmarTimbrado = function () {
    return Swal.fire({
        title: "¿Está seguro de timbrar los comprobantes seleccionados?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, timbrar",
        cancelButtonText: "Cancelar",
    });
};

const mostrarSpinner = function (activar) {
    if (activar) {
        $("#spinner").show();
        $("button").prop("disabled", true);
    } else {
        $("#spinner").hide();
        $("button").prop("disabled", false);
    }
};

const enviarTimbradoCFDIs = function () {

    const seleccionados = tableCFDI.rows('.selected').data().toArray().map(row => row.nfoliopago);

    return $.ajax({
        url: "../CFDIManagment/stamp",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(seleccionados),
    });
};


const mostrarError = function (jqXHR) {
    const mensajeError = jqXHR.responseJSON?.error || "Error al timbrar los CFDIs.";
    Swal.fire({
        icon: "error",
        title: "Error",
        text: mensajeError,
    });
};


const actualizarTabla = function () {
    tableCFDI.ajax.url(SYSTEM_URL + "/crud?rt=nt&ql=vw_CFDI_Encabezado").load();
};