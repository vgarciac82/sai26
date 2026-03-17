const API_COMPROMISO_PDIRECTO = "../api/compromiso/pagodirecto";
let tblSuficiencia;

const creaDTSuficiencia = function () {

    tblSuficiencia = $("#tblSuficiencias").dataTable({
        bScrollCollapse: true,
        bDestroy: true,
        "iDisplayLength": 10,
        oLanguage: {
            sProcessing: "Procesando...",
            sLengthMenu: "Mostrar _MENU_ registros",
            sZeroRecords: "No hay registros a mostrar",
            sEmptyTable: "No hay datos en la tabla",
            sLoadingRecords: "Cargando...",
            sInfo: "Registros _START_ al _END_ de _TOTAL_",
            sInfoEmpty: "Registro 0 al 0 de 0",
            sInfoFiltered: "(filtado de _MAX_ registros)",
            sInfoPostFix: "",
            sInfoThousands: ",",
            sSearch: "Buscar:",
            oPaginate: {
                sFirst: "Primero",
                sPrevious: "Ant.",
                sNext: "Sigte.",
                sLast: "&Uacute;ltimo"
            }
        },
        bServerSide: true,
        sAjaxSource: window.location.protocol + "//" + window.location.host + "/"
            + window.location.pathname.split("/")[1] +
            "/crud?rt=t&ql=v_suficiencias_autorizadas&qw=cunidadresponsable IN( SELECT ur FROM tvistasUR WHERE usuario = '" + $("#login").val() + "' AND modulo='TESORERIA')",
        bProcessing: true,
        sPaginationType: "full_numbers",
        bJQueryUI: true,
        aaSorting: [[0, "asc"]],
        aoColumns: [
            {
                sName: "cidcontrato"
            },
            {
                sName: "canocompromiso"
            },
            {
                sName: "RFC"
            },
            {
                sName: "nombre"
            },
            {
                sName: "mimporte"
            },
            {
                sName: "mimporteiva"
            },
            {
                sName: "mtotal"
            }

        ]
    });

    $('#tblSuficiencias tbody tr').live('click', function () {
        console.log("Fila seleccionada: ", $(this).text());
        $('#tblSuficiencias tbody tr').removeClass('row_selected');
        $(this).addClass('row_selected');
    });
}

const createPaymentFromSufficiency = function (e) {
    if (e && e.preventDefault) e.preventDefault();

    var sufficiency = getContratoSeleccionado();
    if (sufficiency === null) {
        Swal.fire("Atención", "Debe seleccionar una suficiencia antes de continuar.", "warning");
        return;
    }

    var data = { folio: sufficiency };

    $.ajax({
        type: 'POST',
        url: API_COMPROMISO_PDIRECTO,
        data: data, // si tu API espera JSON, cambia a contentType + JSON.stringify
        success: function (response) {
			leeEncabezado();
			conceptoPagoDirectoRead();
            capturePAAS();
            Swal.fire("Éxito", "Documento creado exitosamente.", "success");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire("Error", "No se pudo crear el documento.", "error");
            console.error('Estado:', textStatus, 'Error:', errorThrown);
        }
    });
};


const getContratoSeleccionado = function () {
    var oTable = $('#tblSuficiencias').dataTable();
    var selectedRow = $('#tblSuficiencias tbody tr.row_selected');

    if (selectedRow.length > 0) {
        var rowData = oTable.fnGetData(selectedRow[0]);
        return rowData[0];
    }
    return null;
};

const capturePAAS = function () {

    $("#suficienciaDiv").hide();
    $("#TabPaas").show();
    $("#PAAS").show();
    $("#TabPaas").click();

}