const dataTableBaseURL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1];
let fuelingDT;

const initUIFuelingWalletList = function(initError, idExecutiveUnit, requestStatus) {
    $.fn.dataTable.ext.legacy.ajax = true;
    if (initError) {

        Swal.fire({
            title: 'Error',
            text: 'Ocurrio un error mientras se iniciaba el tramite. Notifique al adminitrador.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        $('#fuelingDataTable').DataTable();
        return;
    }

    createFuelingDT(requestStatus);
}

const initUIFuelingWalletAuthList = function(initError, idExecutiveUnit, tableList) {
    $.fn.dataTable.ext.legacy.ajax = true;
    if (initError) {

        Swal.fire({
            title: 'Error',
            text: 'Ocurrio un error mientras se iniciaba el tramite. Notifique al adminitrador.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        $('#fuelingDataTable').DataTable();
        return;
    }

    tableList.forEach(function(dt) {
        createDT(dt.id, dt.status, dt.fn);
    });
}

const dataTables = new Array();

const createDT = function(idDataTable, requestStatus, onClickFn) {

    let dt = dataTables[idDataTable];

    if (dt)
        dt.destroy();

    let condition = "";

    if (requestStatus == VALIDATING_VERIFICATION) {
        condition = "(user_request = '" + $("#userRequest").val() + "' OR employee_responsible=" + $("#employeeNumber").val() + ") AND id_status =" + requestStatus;
    } else if (requestStatus == AUTHORIZED_FUELING_REQUEST) {
        condition = "(user_request = '" + $("#userRequest").val() + "' OR request_employee_responsible=" + $("#employeeNumber").val() + ")  AND id_status IN( 4,7)";
    } else if (requestStatus == VERIFIED_FUELING_REQUEST) {
        condition = "(user_request = '" + $("#userRequest").val() + "' OR request_employee_responsible=" + $("#employeeNumber").val() + ")  AND id_status = " + requestStatus ;
    }  else {
        condition = "employee_responsible=" + $("#employeeNumber").val() + " AND id_status =" + requestStatus;
    }

    dataTables[idDataTable] = $('#' + idDataTable).DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "scrollX": true,
        "searching": false,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=vFuelingWallet&qw=" + condition,
            type: 'POST',
        },
        "aoColumns": [
            { "name": "fueling_request_id" },
            { "name": "wallet_number" },
            { "name": "brand" },
            { "name": "sub_brand" },
            { "name": "liscence_plate" },
            { "name": "employeeResponsibleName" },
            { "name": "justification_text" },
            { "name": "initial_date" },
            { "name": "end_date" }
        ]
    });

    $('#' + idDataTable + ' tbody').on('dblclick', 'tr', function() {

        let data = $('#' + idDataTable).DataTable().row(this).data();
        console.log(data);
        if (onClickFn)
            onClickFn(data, 3);

    });


}

const createFuelingDT = function(requestStatus) {

    if (fuelingDT)
        fuelingDT.destroy();

    $.fn.dataTable.ext.legacy.ajax = true;

    fuelingDT = $('#fuelingDataTable').DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "scrollX": true,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=vFuelingWallet&qw=(user_request = '" + $("#userRequest").val() + "' OR request_employee_responsible = " + $("#employeeNumber").val() + ") AND id_status=" + requestStatus,
            type: 'POST',
        },
        "aoColumns": [
            { "name": "fueling_request_id" },
            { "name": "wallet_number" },
            { "name": "brand" },
            { "name": "sub_brand" },
            { "name": "liscence_plate" },
            { "name": "employeeResponsibleName" },
            { "name": "justification_text" },
            { "name": "initial_date" },
            { "name": "end_date" }
        ]
    });

    $('#fuelingDataTable tbody').on('dblclick', 'tr', function() {

        let data = $('#fuelingDataTable').DataTable().row(this).data();
        console.log(data);
        processRequest(data);

    });

}

const processRequest = function(requestRow, action) {
    let requestFolio = requestRow[0];
    parent.frames['content-iframe'].location.href = "VehicleFuelRequest.jsp?REQUEST_FOLIO=" + requestFolio + (action ? "&ACTION=" + action : "");
}