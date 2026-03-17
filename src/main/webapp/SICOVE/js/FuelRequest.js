let accountFuelRequest = {
    "autorizedAmount": 0.00,
    "fuelContractAccountId": 0,
    "fuelProvisioningRequestId": 0,
    "rejectJustification": null,
    "requestAmount": 0.00,
    "requestDate": 0.00,
    "requestJustification": null,
    "requestMonth": 0,
    "requestStatus": 0,
    "userRequest": null
}

const saveAccountFuelRequest = function(onSuccess, onError) {

    $.ajax({
        url: 'FuelProvisioningAccount',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(accountFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Guardando Informacion...' });
        },
        success: function(data) {
            $.unblockUI();
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });

}

const updateAccountFuelRequest = function(onSuccess, onError) {


    $.ajax({
        url: 'FuelProvisioningAccount',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(accountFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Actualizando Informacion...' });
        },
        success: function(data) {
            $.unblockUI();
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });

}

const authAccountFuelRequest = function(onSuccess, onError) {


    $.ajax({
        url: 'FuelProvisioningAccount/auth',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(accountFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Autorizando Solicitud ...' });
        },
        success: function(data) {
            $.unblockUI();
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });

}

const getAccountFuelRequest = function(folio, onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningAccount?REQUEST_FOLIO=' + folio,
        type: 'GET',
        contentType: 'application/json',
        success: function(data) {
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const deleteAccountFuelRequest = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningAccount?REQUEST_FOLIO=' + accountFuelRequest.fuelProvisioningRequestId,
        type: 'DELETE',
        contentType: 'application/json',
        success: function(data) {
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const sendFuelRequest = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningAccount/sendRequest',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(accountFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Guardando Informacion...' });
        },
        success: function(data) {
            $.unblockUI();
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}

const rejectFuelRequest = function(onSuccess, onError) {
    $.ajax({
        url: 'FuelProvisioningAccount/rejectRequest',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify(accountFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: 'Rechazando Solicitud ...' });
        },
        success: function(data) {
            $.unblockUI();
            accountFuelRequest = data;
            onSuccess(accountFuelRequest);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });
}