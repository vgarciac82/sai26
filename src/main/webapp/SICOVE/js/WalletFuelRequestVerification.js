let walletFuelRequestVerification = {
    idVerification: 0,
    fuelingRequestId: 0,
    currentWalletBalance: 0.0,
    initialVehicleKilometers:0.0,
    currentVehicleKilometers: 0.0,
    validationAmount: 0.0,
    verification_captured: null,
    verification_authorized: null,
    verification_rejected: null,
    user_capture: "",
};

const getWalletFuelRequestVerificationByAsignation = function(
    fuelingRequestId,
    onSuccess,
    onError
) {
    $.ajax({
        url: "FuelVerification/getByAsignationId?fuelingRequestId=" + fuelingRequestId,
        type: "GET",
        contentType: "application/json",
        success: function(data) {
            walletFuelRequestVerification = data;
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("Error:", textStatus, errorThrown);
            onError(errorThrown);
        },
    });
};

const getWalletFuelRequestVerificationList = function(onSuccess, onError) {
    $.ajax({
        url: "FuelVerification/getVerificationList?idVerification=" +
            walletFuelRequestVerification.idVerification,
        type: "GET",
        contentType: "application/json",
        success: function(data) {
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("Error:", textStatus, errorThrown);
            onError(errorThrown);
        },
    });
};

const loadWalletFuelRequestVerification = function(
    idVerification,
    onSuccess,
    onError
) {
    $.ajax({
        url: "FuelVerification?idVerification=" + idVerification,
        type: "GET",
        contentType: "application/json",
        success: function(data) {
            walletFuelRequestVerification = data;
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("Error:", textStatus, errorThrown);
            onError(errorThrown);
        },
    });
};

const saveWalletFuelRequestVerification = function(onSuccess, onError) {
    $.ajax({
        url: "FuelVerification",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(walletFuelRequestVerification),
        success: function(data) {
            walletFuelRequestVerification = data;
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("Error:", textStatus, errorThrown);
            onError(errorThrown);
        },
    });
};

const sendVerification = function(onSuccess, onError) {
    $.ajax({
        url: "FuelProvisioningWallet/validatingVerification",
        type: "PUT",
        data: JSON.stringify(vehicleFuelRequest),
        beforeSend: function() {
            $.blockUI({ message: "Guardando Informacion..." });
        },
        success: function(response) {
            $.unblockUI();
            onSuccess();
        },
        error: function(error) {
            console.log(error);
            onError(error);
            $.unblockUI();
        },
    });
};

const updateVerificationDetail = function(
    idDetail,
    authorized,
    message,
    onSuccess,
    onError
) {
    $.ajax({
        url: "fuelingExpedient/updateVerificationDetail",
        type: "PUT",
        data: JSON.stringify({
            "idDetail": idDetail,
            "acepted": authorized,
            "ticketObservations": message,
        }),
        beforeSend: function() {
            $.blockUI({ message: "Guardando Informacion..." });
        },
        success: function(response) {
            $.unblockUI();
            onSuccess();
        },
        error: function(error) {
            console.log(error);
            onError(error);
            $.unblockUI();
        },
    });
}

const deleteDetail = function(idDetail, onSuccess, onError) {
    $.ajax({
        url: "fuelingExpedient",
        type: "DELETE",
        data: JSON.stringify({
            "idDetail": idDetail
        }),
        beforeSend: function() {
            $.blockUI({ message: "Eliminando Registro ..." });
        },
        success: function(response) {
            $.unblockUI();
            onSuccess();
        },
        error: function(error) {
            console.log(error);
            onError(error);
            $.unblockUI();
        },
    });
}


const updateWalletFuelRequestVerification = function(onSuccess, onError) {
    $.ajax({
        url: "FuelVerification",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(walletFuelRequestVerification),
        beforeSend: function() {
            $.blockUI({ message: "Actualizando Comprobacion ..." });
        },
        success: function(data) {
            $.unblockUI();
            walletFuelRequestVerification = data;
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $.unblockUI();
            console.error("Error:", textStatus, errorThrown);
            onError(errorThrown);
        },
    });
};