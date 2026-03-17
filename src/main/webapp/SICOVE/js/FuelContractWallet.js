const fuelWalletAccount = {
    "idFuelAccountWallet": 0,
    "idContractAccount": 0,
    "walletNumber": "",
    "vehicleInventoryId": null,
    "userRegistration": "",
    "status": 1
}

const saveWalletAccount = function(fuelWalletAccountObj, onSuccess, onError) {

    $.ajax({
        url: 'FuelAccountWallet',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(fuelWalletAccountObj),
        success: function(data) {
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });


}

const updateWalletAccount = function(fuelWalletAccountObj, onSuccess, onError) {

    $.ajax({
        url: 'FuelAccountWallet',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(fuelWalletAccountObj),
        success: function(data) {
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });


}