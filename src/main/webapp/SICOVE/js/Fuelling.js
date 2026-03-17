const CAPTURE_FUELING_REQUEST = 1;
const WAITING_AUTHORIZATION_FUELING_REQUEST = 2;
const WAITING_SUFFICIENCY_FUELING_REQUEST = 3;
const AUTHORIZED_FUELING_REQUEST = 4;
const REJECTED_FUELING_REQUEST = 5;
const DISCARTED_FUELING_REQUEST = 6;
const CAPTURE_VERIFICATION = 7;
const VALIDATING_VERIFICATION = 8;
const VERIFIED_FUELING_REQUEST = 9;

let vehicleFuelRequest = {
    "fuelingRequestId": 0,
    "vehicleId": 0,
    "employeeResponsible": "",
    "userRequest": "",
    "idStatus": 0,
    "walletNumber": "",
    "justification": {
        "withJustification": false,
        "idCommision": 0,
        "justification": "",
        "initialDate": "",
        "endDate": "",
        "countryId": 0,
        "stateName": "",
        "municipalityName": ""
    },
    "fuelingAmount": 0.00,
    "authorizedAmount": 0.00,
    "estimatedKilometers": 0.00,
    "idProcess": 0,
    "rejectJustification": "",
    "idWallet": 0
}

const onSuccesLoadEmployee = function(employee) {
    $("#resonsibleVehicle").val(employee.name + " " + employee.lastName1 + "  " + employee.lastName2 + " - " + employee.jobTile.name);
}

const saveFuelRequest = function(onSuccess, onError, isUpdate) {

    let typeMethod = (isUpdate ? "PUT" : "POST");

    $.ajax({
        url: 'FuelProvisioningWallet',
        type: typeMethod,
        contentType: 'application/json',
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Guardando Informacion...' });
        },
        success: function(data) {
            $.unblockUI();
            vehicleFuelRequest = data;
            onSuccess(vehicleFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}


const loadWalletFuelingRequest = function(fuelingRequestId, onSuccess, onError) {

    $.ajax({
        url: 'FuelProvisioningWallet?REQUEST_FOLIO=' + fuelingRequestId,
        type: 'GET',
        contentType: 'application/json',
        beforeSend: function() {
            $.blockUI({ message: 'Cargando Informacion...' });
        },
        success: function(data) {
            $.unblockUI();
            vehicleFuelRequest = data;
            onSuccess(vehicleFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const updateFuelRequest = function(onSuccess, onError) {
    saveFuelRequest(onSuccess, onError, true);
}

const changeRequestStatus = function(newStatus, onSuccess, onError) {

    $.ajax({
        url: 'FuelProvisioningWallet/nextStatus?REQUEST_FOLIO=' + vehicleFuelRequest.fuelingRequestId + '&REQUEST_STATUS=' + newStatus,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Guardando Informacion...' });
        },
        success: function(data) {
            vehicleFuelRequest = data;
            $.unblockUI();
            onSuccess(vehicleFuelRequest.idStatus);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const authRequest = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningWallet/authRequest',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Guardando Informacion...' });
        },
        success: function(data) {
            vehicleFuelRequest = data;
            $.unblockUI();
            onSuccess(vehicleFuelRequest.idStatus);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}


const requestComplete = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningWallet/finishRequest',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Terminando Proceso...' });
        },
        success: function(data) {
            vehicleFuelRequest = data;
            $.unblockUI();
            onSuccess(vehicleFuelRequest.idStatus);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const discardRequest = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningWallet/discardRequest',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Descartando Solicitud...' });
        },
        success: function(data) {
            $.unblockUI();
            onSuccess();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const rejectRequest = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningWallet/rejectRequest',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Descartando Solicitud...' });
        },
        success: function(data) {
            $.unblockUI();
            onSuccess();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}