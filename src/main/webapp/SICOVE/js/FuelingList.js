const dataTableBaseURL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1];
let fuelingDT;
let fuelingDTAuth;
let listType = 1;

const initUIFuelingList = function(initError, idExecutiveUnit, requestStatus) {
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
    listType = requestStatus;
    createFuelingDT(requestStatus);
    if (requestStatus == 2)
        createFuelingAuthDT(FUELING_STATUS.AWAITING_VENDOR_PROVISIONING);

}

const createFuelingAuthDT = function(requestStatus) {

    if (fuelingDTAuth)
        fuelingDTAuth.destroy();

    fuelingDTAuth = $('#fuelingAuthDataTable').DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=vFuelingRequest&qw=request_status=" + requestStatus,
            type: 'POST',
        },
        "aoColumns": [
            { "name": "id_fuel_provisioning_request" },
            { "name": "unit_complete_name" },
            { "name": "contract_owner" },
            { "name": "account_number" },
            { "name": "request_amount" },
            { "name": "request_date_str" },
            { "name": "status_name" }
        ]
    });

    $('#fuelingAuthDataTable tbody').on('dblclick', 'tr', function() {

        let data = $('#fuelingAuthDataTable').DataTable().row(this).data();
        console.log(data);
        processRequest(data);

    });

}

const createFuelingDT = function(requestStatus) {

    if (fuelingDT)
        fuelingDT.destroy();



    fuelingDT = $('#fuelingDataTable').DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=vFuelingRequest&qw=request_status=" + requestStatus,
            type: 'POST',
        },
        "aoColumns": [
            { "name": "id_fuel_provisioning_request" },
            { "name": "unit_complete_name" },
            { "name": "contract_owner" },
            { "name": "account_number" },
            { "name": "request_amount" },
            { "name": "request_date_str" },
            { "name": "status_name" }
        ]
    });

    $('#fuelingDataTable tbody').on('dblclick', 'tr', function() {

        let data = $('#fuelingDataTable').DataTable().row(this).data();
        console.log(data);
        processRequest(data);

    });

}


const processRequest = function(requestRow) {
    let requestAccount = requestRow[3];
    let requestFolio = requestRow[0];
    if (listType == 1)
        parent.frames['content-iframe'].location.href = "AccountFuelRequest.jsp?REQUEST_ACCOUNT=" + requestAccount + "&REQUEST_FOLIO=" + requestFolio;
    else if (listType == 2 || listType == 3)
        parent.frames['content-iframe'].location.href = "ReviewFuelRequest.jsp?REQUEST_ACCOUNT=" + requestAccount + "&REQUEST_FOLIO=" + requestFolio;
}